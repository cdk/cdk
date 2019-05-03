/* Copyright (C) 2001-2007  Egon Willighagen <egonw@users.sf.net>
 *                          Stefan Kuhn <shk3@users.sf.net>
 *                          Miguel Rojas-Cherto <miguelrojasch@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.cml.CustomSerializer;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.libio.cml.ICMLCustomizer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializes a {@link IAtomContainerSet} or a {@link IAtomContainer} object to CML 2 code.
 * Chemical Markup Language is an XML-based file format {@cdk.cite PMR99}.
 * Output can be redirected to other Writer objects like {@link StringWriter}
 * and {@link FileWriter}. An example:
 *
 * <pre>
 *   StringWriter output = new StringWriter();
 *   boolean makeFragment = true;
 *   CMLWriter cmlwriter = new CMLWriter(output, makeFragment);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 *   String cmlcode = output.toString();
 * </pre>
 *
 * <p>Output to a file called "molecule.cml" can done with:
 *
 * <pre>
 *   FileWriter output = new FileWriter("molecule.cml");
 *   CMLWriter cmlwriter = new CMLWriter(output);
 *   cmlwriter.write(molecule);
 *   cmlwriter.close();
 * </pre>
 *
 * <p>For atoms it outputs: coordinates, element type and formal charge.
 * For bonds it outputs: order, atoms (2, or more) and wedges.
 *
 * @cdk.module       libiocml
 * @cdk.githash
 * @cdk.require      java1.5+
 * @cdk.bug          1565563
 * @cdk.iooptions
 *
 * @see java.io.FileWriter
 * @see java.io.StringWriter
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, CML
 */
public class CMLWriter extends DefaultChemObjectWriter {

    private OutputStream         output;

    private BooleanIOSetting     cmlIds;
    private BooleanIOSetting     namespacedOutput;
    private StringIOSetting      namespacePrefix;
    private BooleanIOSetting     schemaInstanceOutput;
    private StringIOSetting      instanceLocation;
    private BooleanIOSetting     indent;
    private BooleanIOSetting     xmlDeclaration;

    private static ILoggingTool  logger      = LoggingToolFactory.createLoggingTool(CMLWriter.class);

    private List<ICMLCustomizer> customizers = null;

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. Only one object can be stored.
     *
     * @param writer Writer to redirect the output to.
     */
    public CMLWriter(final Writer writer) {

        // OutputStream doesn't handle encoding - the serializers read/write in the same format we're okay
        logger.warn("possible loss of encoding when using a Writer with CMLWriter");
        this.output = new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                writer.write(b);
            }

            @Override
            public void close() throws IOException {
                writer.close();
            }
        };

        initIOSettings();
    }

    public CMLWriter(OutputStream output) {
        this.output = output;
        initIOSettings();
    }

    public CMLWriter() {
        this(new ByteArrayOutputStream());
    }

    public void registerCustomizer(ICMLCustomizer customizer) {
        if (customizers == null) customizers = new ArrayList<ICMLCustomizer>();

        customizers.add(customizer);
        logger.info("Loaded Customizer: ", customizer.getClass().getName());
    }

    @Override
    public IResourceFormat getFormat() {
        return CMLFormat.getInstance();
    }

    @Override
    public void setWriter(final Writer writer) throws CDKException {

        // OutputStream doesn't handle encoding - the serializers read/write in the same format we're okay
        logger.warn("possible loss of encoding when using a Writer with CMLWriter");
        this.output = new OutputStream() {

            @Override
            public void write(int b) throws IOException {
                writer.write(b);
            }
        };

    }

    @Override
    public void setWriter(OutputStream output) throws CDKException {
        this.output = output;
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        if (output != null) output.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtom.class.equals(interfaces[i])) return true;
            if (IBond.class.equals(interfaces[i])) return true;
            if (ICrystal.class.equals(interfaces[i])) return true;
            if (IChemModel.class.equals(interfaces[i])) return true;
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IChemSequence.class.equals(interfaces[i])) return true;
            if (IAtomContainerSet.class.equals(interfaces[i])) return true;
            if (IReactionSet.class.equals(interfaces[i])) return true;
            if (IReaction.class.equals(interfaces[i])) return true;
        }
        return false;
    }

    /**
     * Serializes the IChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of AtomContaineSet object
     */
    @Override
    public void write(IChemObject object) throws CDKException {

        if (!(object instanceof IAtomContainer) && !(object instanceof IAtomContainerSet)
                && !(object instanceof IReaction) && !(object instanceof IReactionSet)
                && !(object instanceof IChemSequence) && !(object instanceof IChemModel)
                && !(object instanceof IChemFile) && !(object instanceof ICrystal) && !(object instanceof IAtom)
                && !(object instanceof IBond)) {
            throw new CDKException("Cannot write this unsupported IChemObject: " + object.getClass().getName());
        }

        logger.debug("Writing object in CML of type: ", object.getClass().getName());

        customizeJob();

        Convertor convertor = new Convertor(cmlIds.isSet(),
                (namespacePrefix.getSetting().length() > 0) ? namespacePrefix.getSetting() : null);
        // adding the customizer
        if (customizers != null) {
            for (ICMLCustomizer customizer : customizers) {
                convertor.registerCustomizer(customizer);
            }
        }

        // now convert the object
        Element root = null;
        if (object instanceof IPDBPolymer) {
            root = convertor.cdkPDBPolymerToCMLMolecule((IPDBPolymer) object);
        } else if (object instanceof ICrystal) {
            root = convertor.cdkCrystalToCMLMolecule((ICrystal) object);
        } else if (object instanceof IAtom) {
            root = convertor.cdkAtomToCMLAtom(null, (IAtom) object);
        } else if (object instanceof IBond) {
            root = convertor.cdkBondToCMLBond((IBond) object);
        } else if (object instanceof IReaction) {
            root = convertor.cdkReactionToCMLReaction((IReaction) object);
        } else if (object instanceof IReactionScheme) {
            root = convertor.cdkReactionSchemeToCMLReactionSchemeAndMoleculeList((IReactionScheme) object);
        } else if (object instanceof IReactionSet) {
            root = convertor.cdkReactionSetToCMLReactionList((IReactionSet) object);
        } else if (object instanceof IAtomContainerSet) {
            root = convertor.cdkAtomContainerSetToCMLList((IAtomContainerSet) object);
        } else if (object instanceof IChemSequence) {
            root = convertor.cdkChemSequenceToCMLList((IChemSequence) object);
        } else if (object instanceof IChemModel) {
            root = convertor.cdkChemModelToCMLList((IChemModel) object);
        } else if (object instanceof IAtomContainer) {
            root = convertor.cdkAtomContainerToCMLMolecule((IAtomContainer) object);
        } else if (object instanceof IChemFile) {
            root = convertor.cdkChemFileToCMLList((IChemFile) object);
        }

        Document doc = new Document(root);
        try {
            Serializer serializer = null;
            if (xmlDeclaration.isSet()) {
                serializer = new Serializer(output, "ISO-8859-1");
            } else {
                serializer = new CustomSerializer(output, "ISO-8859-1");
            }
            if (indent.isSet()) {
                logger.info("Indenting XML output");
                serializer.setIndent(2);
            }

            if (schemaInstanceOutput.isSet()) {
                root.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                root.addAttribute(new Attribute("xsi:schemaLocation=", "http://www.w3.org/2001/XMLSchema-instance",
                        "http://www.xml-cml.org/schema/cml2/core " + instanceLocation.getSetting()));
            }

            serializer.write(doc);
        } catch (Exception exception) {
            throw new CDKException("Could not write XML output: " + exception.getMessage(), exception);
        }
    }

    private void initIOSettings() {
        cmlIds = addSetting(new BooleanIOSetting("CMLIDs", IOSetting.Importance.LOW,
                "Should the output use CML identifiers?", "true"));

        namespacedOutput = addSetting(new BooleanIOSetting("NamespacedOutput", IOSetting.Importance.LOW,
                "Should the output use namespaced output?", "true"));

        namespacePrefix = addSetting(new StringIOSetting("NamespacePrefix", IOSetting.Importance.LOW,
                "What should the namespace prefix be? [empty is no prefix]", ""));

        schemaInstanceOutput = addSetting(new BooleanIOSetting("SchemaInstance", IOSetting.Importance.LOW,
                "Should the output use the Schema-Instance attribute?", "false"));

        instanceLocation = addSetting(new StringIOSetting("InstanceLocation", IOSetting.Importance.LOW,
                "Where is the schema found?", ""));

        indent = addSetting(new BooleanIOSetting("Indenting", IOSetting.Importance.LOW,
                "Should the output be indented?", "true"));

        xmlDeclaration = addSetting(new BooleanIOSetting("XMLDeclaration", IOSetting.Importance.LOW,
                "Should the output contain an XML declaration?", "true"));
    }

    private void customizeJob() {
        fireIOSettingQuestion(cmlIds);
        fireIOSettingQuestion(namespacedOutput);
        if (namespacedOutput.isSet()) {
            fireIOSettingQuestion(namespacePrefix);
        }
        fireIOSettingQuestion(schemaInstanceOutput);
        if (schemaInstanceOutput.isSet()) {
            fireIOSettingQuestion(instanceLocation);
        }
        fireIOSettingQuestion(indent);
        fireIOSettingQuestion(xmlDeclaration);
    }

}

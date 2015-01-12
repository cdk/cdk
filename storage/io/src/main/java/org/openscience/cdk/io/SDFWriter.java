/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.SDFFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.smiles.InvPair;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Writes MDL SD files ({@cdk.cite DAL92}). A MDL SD file contains one or more molecules,
 * complemented by properties.
 *
 * @cdk.module  io
 * @cdk.githash
 * @cdk.iooptions
 * @cdk.keyword file format, MDL SD file
 */
public class SDFWriter extends DefaultChemObjectWriter {

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SDFWriter.class);

    private BufferedWriter            writer;
    private BooleanIOSetting          writerProperties;
    private Set<String>               propertiesToWrite;

    /**
     * Constructs a new SDFWriter that writes to the given {@link Writer}.
     *
     * @param   out  The {@link Writer} to write to
     */
    public SDFWriter(Writer out) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
        initIOSettings();
    }

    /**
     * Constructs a new MDLWriter that can write to a given
     * {@link OutputStream}.
     *
     * @param   output  The {@link OutputStream} to write to
     */
    public SDFWriter(OutputStream output) {
        this(new OutputStreamWriter(output));
    }

    public SDFWriter() {
        this(new StringWriter());
    }

    /**
     * Constructs a new SDFWriter that writes to the given {@link Writer}.
     *
     * @param out The {@link Writer} to write to
     */
    public SDFWriter(Writer out, Set<String> propertiesToWrite) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
        initIOSettings();
        this.propertiesToWrite = propertiesToWrite;
    }

    /**
     * Constructs a new SdfWriter that can write to a given
     * {@link OutputStream}.
     *
     * @param output The {@link OutputStream} to write to
     */
    public SDFWriter(OutputStream output, Set<String> propertiesToWrite) {
        this(new OutputStreamWriter(output), propertiesToWrite);
    }

    /**
     * Writes SD-File to a String including the given properties
     */
    public SDFWriter(Set<String> propertiesToWrite) {
        this(new StringWriter(), propertiesToWrite);
    }

    @Override
    public IResourceFormat getFormat() {
        return SDFFormat.getInstance();
    }

    @Override
    public void setWriter(Writer out) throws CDKException {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter) out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    @Override
    public void setWriter(OutputStream output) throws CDKException {
        setWriter(new OutputStreamWriter(output));
    }

    /**
     * Flushes the output and closes this object.
     */
    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IAtomContainer.class.equals(interfaces[i])) return true;
            if (IChemFile.class.equals(interfaces[i])) return true;
            if (IChemModel.class.equals(interfaces[i])) return true;
            if (IAtomContainerSet.class.equals(interfaces[i])) return true;
        }
        if (IAtomContainer.class.equals(classObject)) return true;
        if (IChemFile.class.equals(classObject)) return true;
        if (IChemModel.class.equals(classObject)) return true;
        if (IAtomContainerSet.class.equals(classObject)) return true;
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Writes a IChemObject to the MDL SD file formated output. It can only
     * output IChemObjects of type {@link IChemFile}, {@link IAtomContainerSet}
     * and {@link IAtomContainerSet}.
     *
     * @param object an acceptable {@link IChemObject}
     *
     * @see #accepts(Class)
     */
    @Override
    public void write(IChemObject object) throws CDKException {
        try {
            if (object instanceof IAtomContainerSet) {
                writeMoleculeSet((IAtomContainerSet) object);
                return;
            } else if (object instanceof IChemFile) {
                writeChemFile((IChemFile) object);
                return;
            } else if (object instanceof IChemModel) {
                IChemFile file = object.getBuilder().newInstance(IChemFile.class);
                IChemSequence sequence = object.getBuilder().newInstance(IChemSequence.class);
                sequence.addChemModel((IChemModel) object);
                file.addChemSequence(sequence);
                writeChemFile((IChemFile) file);
                return;
            } else if (object instanceof IAtomContainer) {
                writeMolecule((IAtomContainer) object);
                return;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.debug(ex);
            throw new CDKException("Exception while writing MDL file: " + ex.getMessage(), ex);
        }
        throw new CDKException(
                "Only supported is writing of ChemFile, MoleculeSet, AtomContainer and Molecule objects.");
    }

    /**
     * Writes an {@link IAtomContainerSet}.
     *
     * @param   som  the {@link IAtomContainerSet} to serialize
     */
    private void writeMoleculeSet(IAtomContainerSet som) throws Exception {
        for (IAtomContainer mol : som.atomContainers()) {
            writeMolecule(mol);
        }
    }

    private void writeChemFile(IChemFile file) throws Exception {
        for (IAtomContainer container : ChemFileManipulator.getAllAtomContainers(file)) {
            writeMolecule(container);
        }
    }

    private void writeMolecule(IAtomContainer container) throws CDKException {
        try {
            // write the MDL molfile bits
            StringWriter stringWriter = new StringWriter();
            MDLV2000Writer mdlWriter = new MDLV2000Writer(stringWriter);
            mdlWriter.addSettings(getSettings());
            mdlWriter.write(container);
            mdlWriter.close();
            writer.write(stringWriter.toString());

            // write the properties
            Map<Object, Object> sdFields = container.getProperties();
            boolean writeAllProperties = propertiesToWrite == null;
            if (sdFields != null) {
                for (Object propKey : sdFields.keySet()) {
                    if (!isCDKInternalProperty(propKey)) {
                        if (writeAllProperties || propertiesToWrite.contains(propKey)) {
                            writer.write("> <" + propKey + ">");
                            writer.newLine();
                            writer.write("" + sdFields.get(propKey));
                            writer.newLine();
                            writer.newLine();
                        }
                    }
                }
            }
            writer.write("$$$$\n");
        } catch (IOException exception) {
            throw new CDKException("Error while writing a SD file entry: " + exception.getMessage(), exception);
        }
    }

    /**
     * A list of properties used by CDK algorithms which must never be
     * serialized into the SD file format.
     */
    private static List<String> cdkInternalProperties = new ArrayList<String>();

    static {
        cdkInternalProperties.add(InvPair.CANONICAL_LABEL);
        cdkInternalProperties.add(InvPair.INVARIANCE_PAIR);
        // I think there are a few more, but cannot find them right now
    }

    private boolean isCDKInternalProperty(Object propKey) {
        return cdkInternalProperties.contains(propKey);
    }

    private void initIOSettings() {
        writerProperties = addSetting(new BooleanIOSetting("writeProperties", IOSetting.Importance.LOW,
                "Should molecular properties be written?", "true"));
        addSettings(new MDLV2000Writer().getSettings());
    }

    public void customizeJob() {
        for (IOSetting setting : getSettings()) {
            fireIOSettingQuestion(setting);
        }
    }

}

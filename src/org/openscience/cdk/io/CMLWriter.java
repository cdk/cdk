/* $RCSfile$ 
 * $Author$ 
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.SetOfReactions;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Serializes a SetOfMolecules or a Molecule object to CML 2 code.
 * Chemical Markup Language is an XML based file format {@cdk.cite PMR99}.
 * Output can be redirected to other Writer objects like StringWriter
 * and FileWriter. An example:
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
 * @cdk.module       libio-cml
 * @cdk.builddepends xom-1.0.jar
 * @cdk.depends      jumbo50.jar
 * @cdk.require      java1.5
 * @cdk.bug          905062
 *
 * @see java.io.FileWriter
 * @see java.io.StringWriter
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, CML
 */
public class CMLWriter extends DefaultChemObjectWriter {

    private OutputStream output;
    private Writer writer;

    private BooleanIOSetting cmlIds;
    private BooleanIOSetting namespacedOutput;
    private StringIOSetting namespacePrefix;
    private BooleanIOSetting schemaInstanceOutput;
    private StringIOSetting instanceLocation;
    private BooleanIOSetting indent;
    
    private LoggingTool logger;

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. Only one object can be stored.
     *
     * @param out Writer to redirect the output to.
     */
    public CMLWriter(Writer out) {
        this.writer = out;
        output = new OutputStream() {
			public void write(int b) throws IOException {
				writer.write(b);
			}
        };
        logger = new LoggingTool(this);
        initIOSettings();
    }

    public CMLWriter(OutputStream output) {
        this.output = output;
        writer = null;
        logger = new LoggingTool(this);
        initIOSettings();
    }
    
    public CMLWriter() {
        this(new StringWriter());
    }

    public ChemFormat getFormat() {
        return new CMLFormat();
    }
    
    public void setWriter(Writer writer) throws CDKException {
        this.writer = writer;
    }

    public void setWriter(OutputStream output) throws CDKException {
    	setWriter(new OutputStreamWriter(output));
    }
    
    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the IChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(IChemObject object) throws CDKException {
        if (object instanceof Molecule) {
        } else if (object instanceof Reaction) {
        } else if (object instanceof SetOfReactions) {
        } else if (object instanceof SetOfMolecules) {
        } else if (object instanceof ChemSequence) {
        } else if (object instanceof ChemModel) {
        } else if (object instanceof ChemFile) {
        } else if (object instanceof Crystal) {
        } else if (object instanceof IAtom) {
        } else if (object instanceof Bond) {
        } else {
        	throw new CDKException("Cannot write this unsupported IChemObject: " + object.getClass().getName());
        }

        logger.debug("Writing object in CML of type: ", object.getClass().getName());
        
        customizeJob();
        
        Convertor convertor = new Convertor(
        	cmlIds.isSet(), 
        	(namespacePrefix.getSetting().length() >0) ? namespacePrefix.getSetting() : null
        );
        Element root = null;
        if (object instanceof Molecule) {
        	root = convertor.cdkMoleculeToCMLMolecule((Molecule)object);
        } else if (object instanceof Crystal) {
        	root = convertor.cdkCrystalToCMLMolecule((Crystal)object);
        } else if (object instanceof IAtom) {
        	root = convertor.cdkAtomToCMLAtom((IAtom)object);
        } else if (object instanceof Bond) {
        	root = convertor.cdkBondToCMLBond((Bond)object);
        } else if (object instanceof Reaction) {
        	root = convertor.cdkReactionToCMLReaction((Reaction)object);
        } else if (object instanceof SetOfReactions) {
        	root = convertor.cdkSetOfReactionsToCMLReactionList((SetOfReactions)object);
        } else if (object instanceof SetOfMolecules) {
        	root = convertor.cdkSetOfMoleculesToCMLList((SetOfMolecules)object);
        } else if (object instanceof ChemSequence) {
        	root = convertor.cdkChemSequenceToCMLList((ChemSequence)object);
        } else if (object instanceof ChemModel) {
        	root = convertor.cdkChemModelToCMLList((ChemModel)object);
        } else if (object instanceof ChemFile) {
        	root = convertor.cdkChemFileToCMLList((ChemFile)object);
        }
        Document doc = new Document(root);
        
        try {
            Serializer serializer = new Serializer(output, "ISO-8859-1");
            
            if (indent.isSet()) {
                logger.info("Indenting XML output");
                serializer.setIndent(2);
            }
            
            if (schemaInstanceOutput.isSet()) {
            	root.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            	root.addAttribute(new Attribute(
            	  	"xsi:schemaLocation=",
            	  	"http://www.w3.org/2001/XMLSchema-instance",
            	  	"http://www.xml-cml.org/schema/cml2/core " + instanceLocation.getSetting()
            	));
            }
            
        	serializer.write(doc);
        } catch (Exception exception) {
        	throw new CDKException("Could not write XML output: " + exception.getMessage(), exception);
        }
    }

    private void initIOSettings() {
        cmlIds = new BooleanIOSetting("CMLIDs", IOSetting.LOW,
          "Should the output use CML identifiers?", 
          "true");

        namespacedOutput = new BooleanIOSetting("NamespacedOutput", IOSetting.LOW,
          "Should the output use namespaced output?", 
          "true");

        namespacePrefix = new StringIOSetting("NamespacePrefix", IOSetting.LOW,
          "What should the namespace prefix be? [empty is no prefix]",
          "");
          
        schemaInstanceOutput = new BooleanIOSetting("SchemaInstance", IOSetting.LOW,
          "Should the output use the Schema-Instance attribute?", 
          "false");
        
        instanceLocation = new StringIOSetting("InstanceLocation", IOSetting.LOW,
          "Where is the schema found?",
          "");

        indent = new BooleanIOSetting("Indenting", IOSetting.LOW,
          "Should the output be indented?", 
          "true");
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
    }

    public IOSetting[] getIOSettings() {
        IOSetting[] settings = new IOSetting[6];
        settings[0] = cmlIds;
        settings[1] = namespacedOutput;
        settings[2] = namespacePrefix;
        settings[3] = schemaInstanceOutput;
        settings[4] = instanceLocation;
        settings[5] = indent;
        return settings;
    }
    
}

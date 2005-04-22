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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.io.setting.StringIOSetting;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.tools.LoggingTool;

import org.openscience.cdk.libio.cml.Convertor;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.dom.*;
import javax.xml.parsers.*;

import org.w3c.dom.*;

import org.xmlcml.cml.*;
import org.xmlcml.cmlimpl.*;


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
 * @cdk.module  libio-cml
 * @cdk.depends base.jar
 * @cdk.depends pmrlib.jar
 * @cdk.depends cmlAll.jar
 * @cdk.require java1.4
 * @cdk.bug     905062
 *
 * @see java.io.FileWriter
 * @see java.io.StringWriter
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, CML
 */
public class CMLWriter extends DefaultChemObjectWriter {

    private Writer output;

    private BooleanIOSetting xmlDecl;
    private BooleanIOSetting cmlIds;
    private BooleanIOSetting namespacedOutput;
    private StringIOSetting namespacePrefix;
    private BooleanIOSetting schemaInstanceOutput;
    private StringIOSetting instanceLocation;
    private BooleanIOSetting indent;
    
    private boolean done;
    private boolean fragment;
    private boolean isRootElement;

    private String prefix = "";
    
    private LoggingTool logger;
    private IsotopeFactory isotopeFactory = null;

    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. Only one object can be stored.
     *
     * @param out Writer to redirect the output to.
     */
    public CMLWriter(Writer out) {
        this(out, false);
    }

    public CMLWriter(OutputStream input) {
        this(new OutputStreamWriter(input));
    }
    
    public CMLWriter() {
        this(new StringWriter());
    }

    public ChemFormat getFormat() {
        return new CMLFormat();
    }
    
    /**
     * Constructs a new CMLWriter class. Output will be stored in the Writer
     * class given as parameter. The CML code will be valid CML code with a
     * XML header. More than object can be stored.
     *
     * @param w         Writer to redirect the output to.
     * @param fragment  Boolean denoting that the content is not
     */
    public CMLWriter(Writer w, boolean fragment) {
        this(fragment);
        output = w;
    }

    public CMLWriter(boolean fragment) {
        logger = new LoggingTool(this);
        this.fragment = fragment;
        this.done = false;
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            logger.error("Failed to initiate isotope factory: ", exception.getMessage());
            logger.debug(exception);
        }
        initIOSettings();
    }

    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the ChemObject to CML and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(ChemObject object) throws CDKException {
        logger.debug("Writing object in CML of type: ", object.getClass().getName());
        
        customizeJob();
        
        isRootElement = true;
        
        prefix = namespacePrefix.getSetting();
        
        CMLDocument cmldoc = null;
        
        if (!done) {
            
            CMLDocumentFactory docfac = DocumentFactoryImpl.newInstance();
            cmldoc = (CMLDocument) docfac.createDocument();
            try {
                Convertor convertor = new Convertor(cmlIds.isSet(),namespacedOutput.isSet(), 
                                                    schemaInstanceOutput.isSet(), 
                                                    instanceLocation.getSetting(),
                                                    prefix);
                cmldoc.appendChild(convertor.convert(object,cmldoc));
            } catch (CMLException ex){
                throw new CDKException(ex.getMessage());
            }

            if (!fragment) {
                done = true;
            }
        } else {
            logger.warn("I'm done. But what does this mean?!");
        };
        
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            if (fragment || !xmlDecl.isSet()) {
                logger.info("Omiting XML declaration");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            }
            if (indent.isSet()) {
                logger.info("Indenting XML output");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            }
            DOMSource source = new DOMSource(cmldoc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (javax.xml.transform.TransformerException ex) {
            String error = "Error while transforming XML string: " + ex.getMessage();
            logger.error(error);
            logger.debug(ex);
            throw new CDKException(error);
        }
    };

    public ChemObject highestSupportedChemObject() {
        return new ChemFile();
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
          
        xmlDecl = new BooleanIOSetting("XMLDeclaration", IOSetting.LOW,
          "Should the output use have a XMLDeclaration?", 
          "true");
          
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
        fireIOSettingQuestion(xmlDecl);
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
        IOSetting[] settings = new IOSetting[7];
        settings[0] = xmlDecl;
        settings[1] = cmlIds;
        settings[2] = namespacedOutput;
        settings[3] = namespacePrefix;
        settings[4] = schemaInstanceOutput;
        settings[5] = instanceLocation;
        settings[6] = indent;
        return settings;
    }
    
}

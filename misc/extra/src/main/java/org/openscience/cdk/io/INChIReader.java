/* Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads the content of a IUPAC/NIST Chemical Identifier (INChI) document. See
 * {@cdk.cite HEL01}. Recently a new INChI format was introduced an files generated
 * with the latest INChI generator cannot be parsed with this class. This class
 * needs to be updated.
 *
 * <P>The elements that are read are given in the INChIHandler class.
 *
 * @cdk.module extra
 * @cdk.githash
 * @cdk.iooptions
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 * @cdk.created 2004-05-17
 *
 * @cdk.keyword file format, INChI
 * @cdk.keyword chemical identifier
 * @cdk.require java1.4+
 */
public class INChIReader extends DefaultChemObjectReader {

    private XMLReader           parser;
    private InputStream         input;

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(INChIReader.class);

    /**
     * Construct a INChI reader from a InputStream object.
     *
     * @param input the InputStream with the content
     */
    public INChIReader(InputStream input) {
        this.input = input;
        init();
    }

    public INChIReader() {
        this(new ByteArrayInputStream(new byte[0]));
    }

    @Override
    public IResourceFormat getFormat() {
        return INChIFormat.getInstance();
    }

    /**
     * This method must not be used; XML reading requires the use of an InputStream.
     * Use setReader(InputStream) instead.
     */
    @Override
    public void setReader(Reader reader) throws CDKException {
        throw new CDKException("Invalid method call; use SetReader(InputStream) instead.");
    }

    @Override
    public void setReader(InputStream input) throws CDKException {
        this.input = input;
    }

    /**
     * Initializes this reader.
     */
    private void init() {
        boolean success = false;
        // If JAXP is prefered (comes with Sun JVM 1.4.0 and higher)
        if (!success) {
            try {
                javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
                spf.setNamespaceAware(true);
                javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
                parser = saxParser.getXMLReader();
                logger.info("Using JAXP/SAX XML parser.");
                success = true;
            } catch (ParserConfigurationException | SAXException e) {
                logger.warn("Could not instantiate JAXP/SAX XML reader!");
                logger.debug(e);
            }
        }
        // Aelfred is first alternative.
        if (!success) {
            try {
                parser = (XMLReader) this.getClass().getClassLoader().loadClass("gnu.xml.aelfred2.XmlReader")
                        .newInstance();
                logger.info("Using Aelfred2 XML parser.");
                success = true;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.warn("Could not instantiate Aelfred2 XML reader!");
                logger.debug(e);
            }
        }
        // Xerces is second alternative
        if (!success) {
            try {
                parser = (XMLReader) this.getClass().getClassLoader().loadClass("org.apache.xerces.parsers.SAXParser")
                        .newInstance();
                logger.info("Using Xerces XML parser.");
                success = true;
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                logger.warn("Could not instantiate Xerces XML reader!");
                logger.debug(e);
            }
        }
        if (!success) {
            logger.error("Could not instantiate any XML parser!");
        }
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> classObject) {
        if (IChemFile.class.equals(classObject)) return true;
        Class<?>[] interfaces = classObject.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if (IChemFile.class.equals(interfaces[i])) return true;
        }
        Class superClass = classObject.getSuperclass();
        if (superClass != null) return this.accepts(superClass);
        return false;
    }

    /**
     * Reads a IChemObject of type object from input.
     * Supported types are: ChemFile.
     *
     * @param  object type of requested IChemObject
     * @return the content in a ChemFile object
     */
    @Override
    public <T extends IChemObject> T read(T object) throws CDKException {
        if (object instanceof IChemFile) {
            return (T) readChemFile(object.getBuilder());
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private functions

    /**
     * Reads a ChemFile object from input.
     *
     * @return ChemFile with the content read from the input
     */
    private IChemFile readChemFile(IChemObjectBuilder bldr) {
        IChemFile cf = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
        }
        INChIHandler handler = new INChIHandler(bldr);
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            cf = handler.getChemFile();
        } catch (IOException e) {
            logger.error("IOException: ", e.getMessage());
            logger.debug(e);
        } catch (SAXException saxe) {
            logger.error("SAXException: ", saxe.getClass().getName());
            logger.debug(saxe);
        }
        return cf;
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    private static final class INChIHandler extends DefaultHandler {

        private static ILoggingTool       logger = LoggingToolFactory.createLoggingTool(INChIHandler.class);
        private INChIContentProcessorTool inchiTool;

        private IChemFile         chemFile;
        private IChemSequence     chemSequence;
        private IChemModel        chemModel;
        private IAtomContainerSet setOfMolecules;
        private IAtomContainer    tautomer;
        private IChemObjectBuilder builder;

        /** Used to store all chars between two tags */
        private String                    currentChars;

        /**
         * Constructor for the IChIHandler.
         **/
        public INChIHandler(IChemObjectBuilder bldr) {
            this.builder = bldr;
            this.inchiTool = new INChIContentProcessorTool();
        }

        public void doctypeDecl(String name, String publicId, String systemId) throws Exception {
            logger.info("DocType root element: " + name);
            logger.info("DocType root PUBLIC: " + publicId);
            logger.info("DocType root SYSTEM: " + systemId);
        }

        @Override
        public void startDocument() {
            chemFile = builder.newInstance(IChemFile.class);
            chemSequence = builder.newInstance(IChemSequence.class);
            chemModel = builder.newInstance(IChemModel.class);;
            setOfMolecules = builder.newInstance(IAtomContainerSet.class);;
        }

        @Override
        public void endDocument() {
            chemFile.addChemSequence(chemSequence);
        }

        @Override
        public void endElement(String uri, String local, String raw) {
            logger.debug("end element: ", raw);
            if ("identifier".equals(local)) {
                if (tautomer != null) {
                    // ok, add tautomer
                    setOfMolecules.addAtomContainer(tautomer);
                    chemModel.setMoleculeSet(setOfMolecules);
                    chemSequence.addChemModel(chemModel);
                }
            } else if ("formula".equals(local)) {
                if (tautomer != null) {
                    logger.info("Parsing <formula> chars: ", currentChars);
                    tautomer = inchiTool.processFormula(setOfMolecules.getBuilder().newInstance(IAtomContainer.class), currentChars);
                } else {
                    logger.warn("Cannot set atom info for empty tautomer");
                }
            } else if ("connections".equals(local)) {
                if (tautomer != null) {
                    logger.info("Parsing <connections> chars: ", currentChars);
                    inchiTool.processConnections(currentChars, tautomer, -1);
                } else {
                    logger.warn("Cannot set dbond info for empty tautomer");
                }
            } else {
                // skip all other elements
            }
        }

        /**
         * Implementation of the startElement() procedure overwriting the
         * DefaultHandler interface.
         *
         * @param uri       the Universal Resource Identifier
         * @param local     the local name (without namespace part)
         * @param raw       the complete element name (with namespace part)
         * @param atts      the attributes of this element
         */
        @Override
        public void startElement(String uri, String local, String raw, Attributes atts) {
            currentChars = "";
            logger.debug("startElement: ", raw);
            logger.debug("uri: ", uri);
            logger.debug("local: ", local);
            logger.debug("raw: ", raw);
            if ("INChI".equals(local)) {
                // check version
                for (int i = 0; i < atts.getLength(); i++) {
                    if (atts.getQName(i).equals("version")) logger.info("INChI version: ", atts.getValue(i));
                }
            } else if ("structure".equals(local)) {
                tautomer = builder.newAtomContainer();
            } else {
                // skip all other elements
            }
        }

        /**
         * Implementation of the characters() procedure overwriting the
         * DefaultHandler interface.
         *
         * @param ch        characters to handle
         */
        @Override
        public void characters(char ch[], int start, int length) {
            logger.debug("character data");
            currentChars += new String(ch, start, length);
        }

        public IChemFile getChemFile() {
            return chemFile;
        }

    }
}

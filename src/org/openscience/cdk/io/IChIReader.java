/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.ichi.IChIHandler;
import org.openscience.cdk.*;
import org.xml.sax.helpers.*;
import org.xml.sax.*;
import java.io.*;

/**
 * Reads the content of a IUPAC Chemical Identifier (IChI) document. See
 * <a href="http://www.nist.gov/public_affairs/update/upd20020610.htm#International">this
 * press release</a>. Recently a new IChI format was introduced an files generated
 * with the latest IChI generator cannot be parsed with this class. This class
 * needs to be updated.
 *
 * <P>The elements that are read are given in the IChIHandler class.
 *
 * <p>Reference: <a href="http://cdk.sf.net/biblio.html#HEL01">HEL01</a>.
 *
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @created    25 September 2002
 *
 * @keyword file format, IChI
 * @keyword chemical identifier
 *
 * @see     org.openscience.cdk.io.ichi.IChIHandler
 */
public class IChIReader extends DefaultChemObjectReader {

    private XMLReader parser;
    private Reader input;

    private org.openscience.cdk.tools.LoggingTool logger;

    /**
     * Construct a IChI reader from a Reader object.
     *
     * @param input the Reader with the content
     */
    public IChIReader(Reader input) {
        this.init();
        this.input = input;
    }

    /**
     * Initializes this reader.
     */
    private void init() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        try {
            parser = new gnu.xml.aelfred2.XmlReader();
            logger.info("Using Aelfred2 XML parser.");
        } catch (Exception e) {
            logger.error("Could not instantiate Aelfred2 XML reader!");
        }
    }

    /**
     * Reads a ChemObject of type object from input.
     * Supported types are: ChemFile.
     *
     * @param  object type of requested ChemObject
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws CDKException {
      if (object instanceof ChemFile) {
        return (ChemObject)readChemFile();
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
    private ChemFile readChemFile() {
        ChemFile cf = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.info("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
        }
        IChIHandler handler = new IChIHandler();
        parser.setContentHandler(handler);
        try {
            parser.parse(new InputSource(input));
            cf = handler.getChemFile();
        } catch (IOException e) {
            logger.error("IOException: " + e.toString());
        } catch (SAXException saxe) {
            logger.error("SAXException: " + saxe.getClass().getName());
            logger.error(saxe.toString());
        }
        return cf;
    }

}


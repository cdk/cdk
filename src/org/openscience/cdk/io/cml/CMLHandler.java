/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 *
 */
package org.openscience.cdk.io.cml;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.openscience.cdk.io.cml.cdopi.CDOInterface;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * SAX2 implementation for CML XML fragment reading. CML Core is supported
 * as well is the CRML module.
 *
 * <p>Data is stored into the Chemical Document Object which is passed when
 * instantiating this class. This makes it possible that programs that do not
 * use CDK for internal data storage, use this CML library.
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLHandler extends DefaultHandler {
    
    private ModuleInterface conv;
    private org.openscience.cdk.tools.LoggingTool logger;

    private Hashtable userConventions;

  // this is a problem under MSFT ie jvm
  //private Stack xpath;
    private CMLStack xpath;
    
    /**
     * Constructor for the CMLHandler.
     *
     * @param cdo The Chemical Document Object in which data is stored
     **/
    public CMLHandler(CDOInterface cdo) {
        logger = new org.openscience.cdk.tools.LoggingTool(
                       this.getClass().getName());
        conv = new CMLCoreModule(cdo);
        userConventions = new Hashtable();
        xpath = new Stack();
    }

    public void registerConvention(String convention, ModuleInterface conv) {
      userConventions.put(convention, conv);
    }

    /**
     * Implementation of the characters() procedure overwriting the DefaultHandler interface.
     *
     * @param ch        characters to handle
     */
    public void characters(char ch[], int start, int length) {
       logger.debug(new String(ch, start, length));
       conv.characterData(xpath, ch, start, length);
    }

    public void doctypeDecl(String name, String publicId, String systemId) throws Exception {}

    /**
     * Calling this procedure signals the end of the XML document.
     */
    public void endDocument() {
        conv.endDocument();
    }

    public void endElement(String uri, String local, String raw) {
       logger.debug("</" + raw + ">");
       conv.endElement(xpath, uri, local, raw);
       xpath.pop();
    }

    public CDOInterface returnCDO() {
        return conv.returnCDO();
    }

    public void startDocument() {
        conv.startDocument();
    }

    public void startElement(String uri, String local, String raw, Attributes atts) {
        xpath.push(local);
        logger.debug("<" + raw + "> -> " + xpath);
        // Detect CML modules, like CRML and CCML
        if (local.startsWith("reaction")) {
            // e.g. reactionList, reaction -> CRML module
            logger.info("Detected CRML module");
            conv = new CMLReactionModule(conv);
        } else {
            // assume CML Core
                
            // Detect conventions
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("convention")) {
                    logger.info(new StringBuffer("New Convention: ").append(atts.getValue(i)).toString());
                    if (atts.getValue(i).equals("CML")) {
                        logger.debug("Doing nothing");
                    } else if (atts.getValue(i).equals("PDB")) {
                        conv = new PDBConvention(conv);
                    } else if (atts.getValue(i).equals("PMP")) {
                        conv = new PMPConvention(conv);
                    } else if (atts.getValue(i).equals("MDLMol")) {
                        logger.debug("MDLMolConvention instantiated...");
                        conv = new MDLMolConvention(conv);
                    } else if (atts.getValue(i).equals("JMOL-ANIMATION")) {
                        conv = new JMOLANIMATIONConvention(conv);
                    } else {
                        //unknown convention. userConvention?
                        if (userConventions.containsKey(atts.getValue(i))) {
                            ConventionInterface newconv = (ConventionInterface)userConventions.get(atts.getValue(i));
                            newconv.inherit(conv);
                            conv = newconv;
                        }
                    }
                }
            }
        }
        conv.startElement(xpath, uri, local, raw, atts);
    }

}

/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io.cml;

import java.util.Hashtable;
import java.util.Map;

import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX2 implementation for CML XML fragment reading. CML Core is supported
 * as well is the CRML module.
 *
 * <p>Data is stored into the Chemical Document Object which is passed when
 * instantiating this class. This makes it possible that programs that do not
 * use CDK for internal data storage, use this CML library.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLHandler extends DefaultHandler {
    
    private ICMLModule conv;
    private LoggingTool logger;
    private boolean debug = true;
    
    private Map<String,ICMLModule> userConventions;

    private CMLStack xpath;
    private CMLStack conventionStack;

    /**
     * Constructor for the CMLHandler.
     *
     * @param chemFile The document in which data is stored
     **/
    public CMLHandler(IChemFile chemFile) {
        logger = new LoggingTool(this);
        conv = new CMLCoreModule(chemFile);
        userConventions = new Hashtable<String,ICMLModule>();
        xpath = new CMLStack();
        conventionStack = new CMLStack();
    }

    public void registerConvention(String convention, ICMLModule conv) {
      userConventions.put(convention, conv);
    }

    /**
     * Implementation of the characters() procedure overwriting the DefaultHandler interface.
     *
     * @param ch        characters to handle
     */
    public void characters(char ch[], int start, int length) {
       if (debug) logger.debug(new String(ch, start, length));
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
        if (debug) logger.debug("</" + raw + ">");
        conv.endElement(xpath, uri, local, raw);
        xpath.pop();
        conventionStack.pop();
    }

    public void startDocument() {
        conv.startDocument();
        conventionStack.push("CML");

    }

    public void startElement(String uri, String local, String raw, Attributes atts) {
        xpath.push(local);
        if (debug) logger.debug("<", raw, "> -> ", xpath);
        // Detect CML modules, like CRML and CCML
        if (local.startsWith("reaction")) {
            // e.g. reactionList, reaction -> CRML module
            logger.info("Detected CRML module");
            conv = new CMLReactionModule(conv);
            conventionStack.push(conventionStack.current());
        } else {
            // assume CML Core
                
            // Detect conventions
            String convName = "";
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("convention")) {
                    convName = atts.getValue(i);
                }
            }
            if (convName.length() > 0) {
                if (convName.equals(conventionStack.current())) {
                    logger.debug("Same convention as parent");
                } else {
                    logger.info("New Convention: ", convName);
                    if (convName.equals("CML")) {
                        /* Don't reset the convention handler to CMLCore,
                           becuase all handlers should extend this handler,
                           and use it for any content other then specifically
                           put into the specific convention */
                    } else if (convName.equals("PDB")) {
                        conv = new PDBConvention(conv);
                    } else if (convName.equals("PMP")) {
                        conv = new PMPConvention(conv);
                    } else if (convName.equals("MDLMol")) {
                        if (debug) logger.debug("MDLMolConvention instantiated...");
                        conv = new MDLMolConvention(conv);
                    } else if (convName.equals("JMOL-ANIMATION")) {
                        conv = new JMOLANIMATIONConvention(conv);
                    } else if (convName.equals("qsar:DescriptorValue")) {
                        conv = new QSARConvention(conv);
                    } else if (userConventions.containsKey(convName)) {
                            //unknown convention. userConvention?
                            ICMLModule newconv = (ICMLModule)userConventions.get(convName);
                            newconv.inherit(conv);
                            conv = newconv;
                    } else {
                        logger.warn("Detected unknown convention: ", convName);
                    }
                }
                conventionStack.push(convName);
            } else {
                // no convention set/reset: take convention of parent
                conventionStack.push(conventionStack.current());
            }
        }
        if (debug) logger.debug("ConventionStack: ", conventionStack);
        conv.startElement(xpath, uri, local, raw, atts);
    }

}

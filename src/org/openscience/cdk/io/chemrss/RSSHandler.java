/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.chemrss;

import java.io.StringReader;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.ChemicalRSSReader;
import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX2 implementation for a RSS handler. Data is stored into a ChemSequence
 * where each channel item is one ChemModel in this sequence.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-09-07
 */
public class RSSHandler extends DefaultHandler {
    
    private LoggingTool logger;
    
    private ChemSequence channelSequence;
    
    private String cmlString;
    private String cData;
    private boolean readdedNamespace;
    
    private String itemTitle;
    private String itemDesc;
    private String itemDate;
    private String itemLink;
    
    /**
     * Constructor for the RSSHandler.
     */
    public RSSHandler() {
        logger = new LoggingTool(this);
    }

    public ChemSequence getChemSequence() {
        return channelSequence;
    }

    // XML SAX2 methods 

    public void characters(char ch[], int start, int length) {
        if (cData == null) {
            cData = new String();
        }
        cData += new String(ch, start, length);
    }

    public void doctypeDecl(String name, String publicId, String systemId) throws Exception {
    }

    public void startDocument() {
        channelSequence = new ChemSequence();
        cmlString = "";
        readdedNamespace = false;
    }

    public void endDocument() {
    }

    public void endElement(String uri, String local, String raw) {
       logger.debug("</" + raw + ">");
        if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
            cmlString += cData;
            cmlString += toEndTag(raw);
        } else if (local.equals("item")) {
            ChemModel model = null;
            if (cmlString.length() > 0) {
                StringReader reader = new StringReader(cmlString);
                logger.debug("Parsing CML String: " + cmlString);
                CMLReader cmlReader = new CMLReader(reader);
                try {
                    ChemFile file = (ChemFile)cmlReader.read(new ChemFile());
                    if (file.getChemSequenceCount() > 0) {
                        ChemSequence sequence = file.getChemSequence(0);
                        if (sequence.getChemModelCount() > 0) {
                            model = sequence.getChemModel(0);
                        } else {
                            logger.warn("ChemSequence contains no ChemModel");
                        }
                    } else {
                        logger.warn("ChemFile contains no ChemSequene");
                    }
                } catch (Exception exception) {
                    logger.error("Error while parsing CML");
                    logger.debug(exception);
                }
            } else {
                logger.warn("No CML content found");
            }
            if (model == null) {
                logger.warn("Read empty model");
                model = new ChemModel();
            }
            model.setProperty(ChemicalRSSReader.RSS_ITEM_TITLE, itemTitle);
            model.setProperty(ChemicalRSSReader.RSS_ITEM_DATE, itemDate);
            model.setProperty(ChemicalRSSReader.RSS_ITEM_LINK, itemLink);
            model.setProperty(ChemicalRSSReader.RSS_ITEM_DESCRIPTION, itemDesc);                    
            channelSequence.addChemModel(model);
            cmlString = "";
        } else if (local.equals("title")) {
            itemTitle = cData;
        } else if (local.equals("link")) {
            itemLink = cData;
        } else if (local.equals("description")) {
            itemDesc = cData;
        } else if (local.equals("date")) {
            itemDate = cData;
        } else {
            logger.debug("Unparsed element: " + local);
            logger.debug("  uri: " + uri);
        }
        cData = "";
    }

    public void startElement(String uri, String local, String raw, Attributes atts) {
        logger.debug("<" + raw + ">");
        if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
            if (readdedNamespace) {
                cmlString += toStartTag(raw, atts);
            } else {
                cmlString += toStartTag(raw, atts, uri);
            }
        } else if (local.equals("item")) {
            itemTitle = "";
            itemDesc = "";
            itemDate = "";
            itemLink = "";
        }
        cData = "";
    }

    private String toStartTag(String raw, Attributes atts) {
        return toStartTag(raw, atts, null);
    }
    
    private String toStartTag(String raw, Attributes atts, String uri) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<");
        buffer.append(raw);
        for (int i = 0; i < atts.getLength(); i++) {
            buffer.append(" ");
            String qName = atts.getQName(i);
            buffer.append(qName);
            buffer.append("=\"");
            String value = atts.getValue(i);
            buffer.append(value);
            buffer.append("\"");
        }
        if (uri != null) {
            buffer.append(" ");
            if (raw.indexOf(":") != -1) {
                buffer.append("xmlns:");
                String namespace = raw.substring(0, raw.indexOf(":"));
                buffer.append(namespace);
            } else {
                buffer.append("xmlns");
            }
            buffer.append("=\"");
            buffer.append(uri);
            buffer.append("\"");
        }
        buffer.append(">");
        return buffer.toString();
    }

    private String toEndTag(String raw) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</");
        buffer.append(raw);
        buffer.append(">");
        return buffer.toString();
    }

}

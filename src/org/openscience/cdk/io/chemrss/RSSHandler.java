/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.tools.LoggingTool;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * SAX2 implementation for a RSS handler. Data is stored into a ChemSequence
 * where each channel item is one ChemModel in this sequence.
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @created 2003-09-07
 */
public class RSSHandler extends DefaultHandler {
    
    private LoggingTool logger;
    
    private ChemSequence channelSequence;
    
    private String cmlString;
    private String cData;
    
    /**
     * Constructor for the RSSHandler.
     */
    public RSSHandler() {
        logger = new LoggingTool(this.getClass().getName());
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
    }

    public void endDocument() {
    }

    public void endElement(String uri, String local, String raw) {
       logger.debug("</" + raw + ">");
        if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
            cmlString += cData;
            cmlString += toEndTag(local, raw);
        } else if (local.equals("item")) {
            if (cmlString.length() > 0) {
                StringReader reader = new StringReader(cmlString);
                CMLReader cmlReader = new CMLReader(reader);
                ChemModel model = null;
                try {
                    cmlReader.read(new ChemModel());
                } catch (Exception exception) {
                    logger.error("Error while parsing CML");
                    logger.debug(exception);
                }
                if (model != null) {
                    channelSequence.addChemModel(model);
                } else {
                    channelSequence.addChemModel(new ChemModel());
                }
            } else {
                // if not chemical content found, then empty model
                channelSequence.addChemModel(new ChemModel());
            }
        }
    }

    public void startElement(String uri, String local, String raw, Attributes atts) {
        logger.debug("<" + raw + ">");
        if (uri.equals("http://www.xml-cml.org/schema/cml2/core")) {
            cmlString += toStartTag(local, raw, atts);
        } else if (local.equals("item")) {
            //
        }
    }
    
    private String toStartTag(String local, String raw, Attributes atts) {
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
        buffer.append(">");
        return buffer.toString();
    }

    private String toEndTag(String local, String raw) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("</");
        buffer.append(raw);
        buffer.append(">");
        return buffer.toString();
    }

}

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sf.net
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
 */
package org.openscience.cdk.dict;

import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for unmarshalling a dictionary shema file.
 */
public class DictionaryHandler extends DefaultHandler {

    private LoggingTool logger;

    /** Used to store all chars between two tags */
    private String currentChars;

    Dictionary dict;
    
    public DictionaryHandler() {
        logger = new LoggingTool(this);
    }

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
    }

    public void startDocument() {
        dict = new Dictionary();
    }

    public void endDocument() {
    }

    public void endElement(String uri, String local, String raw) {
        if ("entry".equals(local)) {
            // nothing to do
        } else {
            // skip all other elements
        }
    }

    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        if ("entry".equals(local)) {
            Entry entry = new Entry();
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("id")) {
                    entry.setID(atts.getValue(i));
                } else if (atts.getQName(i).equals("term")) {
                    entry.setTerm(atts.getValue(i));
                }
            }
            dict.addEntry(entry);
        } else {
            // skip all other elements
        }
    }

    public void characters(char ch[], int start, int length) {
        currentChars += new String(ch, start, length);
    }

    public Dictionary getDictionary() {
        return dict;
    }

}

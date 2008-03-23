/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.dict;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for unmarshalling a dictionary shema file.
 * 
 * @cdk.module     dict
 */
public class DictionaryHandler extends DefaultHandler {

    private boolean inEntry = false;
    private boolean inMetadataList = false;
    Entry entry;

    /** Used to store all chars between two tags */
    private String currentChars;

    Dictionary dict;
    
    public DictionaryHandler() {}

    public void doctypeDecl(String name, String publicId, String systemId)
        throws Exception {
    }

    public void startDocument() {
        dict = new Dictionary();
    }

    public void endElement(String uri, String local, String raw) {
        if ("entry".equals(local) && !"bibtex:entry".equals(raw) && inEntry) {
            dict.addEntry(entry);
            inEntry = false;
        } else if ("metadataList".equals(local) && inMetadataList) {
            inMetadataList = false;
        }
    }

    public void startElement(String uri, String local, 
                             String raw, Attributes atts) {
        currentChars = "";
        if ("entry".equals(local) && !"bibtex:entry".equals(raw) && !inEntry) {
            inEntry = true;
            entry = new Entry();
            for (int i = 0; i < atts.getLength(); i++) {
                if (atts.getQName(i).equals("id")) {
                    entry.setID(atts.getValue(i));
                } else if (atts.getQName(i).equals("term")) {
                    entry.setLabel(atts.getValue(i));
                }
            }
        } 
        if ("metadataList".equals(local) && !inMetadataList) {
            inMetadataList = true;
        }

        // if we're in a metadataList then look at individual
        // metadata nodes and check for any whose content refers
        // to QSAR metadata and save that. Currently it does'nt 
        // differentiate between descriptorType or descriptorClass.
        // Do we need to differentiate?
        //
        // RG: I think so and so I save a combination of the dictRef attribute
        // and the content attribute
        if ("metadata".equals(local) && inMetadataList) {
            for (int i = 0; i < atts.getLength()-1; i += 2) {

                String dictRefValue = "";
                if (atts.getQName(i).equals("dictRef")) {
                    dictRefValue = atts.getValue(i);
                }
                if (atts.getQName(i+1).equals("content")) {
                    String content = atts.getValue(i+1);
                    if (content.indexOf("qsar-descriptors-metadata:") == 0) {
                        entry.setDescriptorMetadata(dictRefValue+"/"+content);
                    }
                }
            }
        }
    }
    

    public void characters(char character[], int start, int length) {
        currentChars += new String(character, start, length);
    }

    public Dictionary getDictionary() {
        return dict;
    }

}

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.dict;

import org.openscience.cdk.tools.LoggingTool;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Dictionary with entries.
 *
 * <p>FIXME: this should be replace by a uptodate Dictionary Schema
 * DOM type thing.
 *
 * @author     Egon Willighagen
 * @cdk.svnrev  $Revision$
 * @cdk.created    2003-08-23
 * @cdk.keyword    dictionary
 * @cdk.module     dict
 */
public class Dictionary {

    private Hashtable<String, Entry> entries;
    private String ownNS = null;
    
    public Dictionary() {
        entries = new Hashtable<String, Entry>();
    }
    
    public static Dictionary unmarshal(Reader reader) {
        LoggingTool logger = new LoggingTool(Dictionary.class);
        DictionaryHandler handler = new DictionaryHandler();
        XMLReader parser = null;
        try {
	    parser = XMLReaderFactory.createXMLReader();
            logger.debug("Using "+parser);
        } catch (Exception e) {
            logger.error("Could not instantiate any JAXP parser!");
            logger.debug(e);
        }
	
        try {
            if (parser == null) {
                logger.debug("parser object was null!");
                return null;
            }
            parser.setFeature("http://xml.org/sax/features/validation", false);
            logger.debug("Deactivated validation");
        } catch (SAXException e) {
            logger.warn("Cannot deactivate validation.");
            logger.debug(e);
        }
        parser.setContentHandler(handler);
        Dictionary dict = null;
        try {
            parser.parse(new InputSource(reader));
            dict = handler.getDictionary();
        } catch (IOException e) {
            logger.error("IOException: " + e.toString());
	    logger.debug(e);
        } catch (SAXException saxe) {
            logger.error("SAXException: " + saxe.getClass().getName());
            logger.debug(saxe);
        }
        return dict;
    }
    
    public void addEntry(Entry entry) {
        entries.put(entry.getID().toLowerCase(), entry);
    }
    
    public Entry[] getEntries() {
        int size = entries.size();
        Entry[] entryArray = new Entry[size];
        Enumeration<Entry> elements = entries.elements();
        int counter = 0;
        while (elements.hasMoreElements() && counter < size) {
            entryArray[counter] = (Entry)elements.nextElement();
            counter++;
        }
        return entryArray;
    }
    
    public boolean hasEntry(String identifier) {
        return entries.containsKey(identifier);
    }
    
    public Entry getEntry(String identifier) {
        return entries.get(identifier);
    }
    
    public int size() {
    	return entries.size();
    }

    public void setNS(String nameSpace) {
        ownNS = nameSpace;
    }
    public String getNS() {
        return ownNS;
    }
}

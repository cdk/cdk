/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.dict;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Database of dictionaries listing entries with compounds, fragments
 * and entities.
 *
 * @author     Egon Willighagen
 * @created    2003-04-06
 * @keyword    dictionary
 * @depends    stmml.jar
 * @depends    castor.jar
 */
public class DictionaryDatabase {

    public final static String DICTREFPROPERTYNAME = "org.openscience.cdk.dict";
    
    private org.openscience.cdk.tools.LoggingTool logger;
    
    private String[] dictionaryNames = {
        "chemical", "elements"
    };
    
    private Hashtable dictionaries;

    public DictionaryDatabase() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        
        // read dictionaries distributed with CDK
        dictionaries = new Hashtable();
        for (int i=0; i<dictionaryNames.length; i++) {
            String name = dictionaryNames[i];
            dictionaries.put(name.toLowerCase(), readDictionary("org/openscience/cdk/dict/data/" + name + ".xml"));
            logger.info("Read dictionary: " + name);
        }
    }

    private Dictionary readDictionary(String databaseLocator) {
        Dictionary dictionary = null;
        logger.info("Reading dictionary from " + databaseLocator);
        try {
            InputStreamReader reader = new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(databaseLocator));
            dictionary = Dictionary.unmarshal(reader);
        } catch (Exception exception) {
            dictionary = new Dictionary();
            logger.error("Could not read dictionary " + databaseLocator);
            logger.debug(exception);
        }
        return dictionary;
    };

    /**
     * Reads a custom dictionary into the database.
     */
    public void readDictionary(Reader reader, String name) {
        name = name.toLowerCase();
        logger.info("Reading dictionary: " + name);
        if (!dictionaries.containsKey(name)) {
            try {
                Dictionary dictionary = Dictionary.unmarshal(reader);
                dictionaries.put(name, dictionary);
                logger.info("  ... loaded and stored");
            } catch (Exception exception) {
                logger.error("Could not read dictionary: " + name);
                logger.debug(exception);
            }
        } else {
            logger.error("Dictionary already loaded: " + name);
        }
    };

    /**
     * Returns a String[] with the names of the known dictionaries.
     */
    public String[] getDictionaryNames() {
        return dictionaryNames;
    }
    
    /**
     * Returns a String[] with the id's of all entries in the specified database.
     */
    public String[] getDictionaryEntries(String dictionaryName) {
        Dictionary dictionary = (Dictionary)dictionaries.get(dictionaryName);
        if (dictionary == null) {
            logger.error("Cannot find requested dictionary");
            return new String[0];
        } else {
            // FIXME: dummy method that needs an implementation
            Entry[] entries = dictionary.getEntry();
            String[] entryNames = new String[entries.length];
            logger.info("Found " + entryNames.length + " entries in dictionary " + 
              dictionaryName);
            for (int i=0; i<entries.length; i++) {
                entryNames[i] = entries[i].getTerm();
            }
            return entryNames;
        }
    }
    
    /**
     * Returns true if the database contains the dictionary.
     */
    public boolean hasDictionary(String name) {
        return dictionaries.containsKey(name.toLowerCase());
    }
    
    /**
     * Returns true if the database contains the dictionary.
     */
    public Enumeration listDictionaries() {
        return dictionaries.keys();
    }
    
    /**
     * Returns true if the given dictionary contains the given
     * entry.
     */
    public boolean hasEntry(String dictName, String entryID) {
        if (hasDictionary(dictName)) {
            Dictionary dictionary = (Dictionary)dictionaries.get(dictName);
            return dictionary.hasEntry(entryID.toLowerCase());
        } else {
            return false;
        }
    }
    
}

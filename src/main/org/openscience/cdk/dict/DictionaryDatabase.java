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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Database of dictionaries listing entries with compounds, fragments
 * and entities.
 *
 * @author     Egon Willighagen
 * @cdk.svnrev  $Revision$
 * @cdk.created    2003-04-06
 * @cdk.keyword    dictionary
 * @cdk.depends    xom.jar
 * @cdk.module     dict
 */
public class DictionaryDatabase {

    public final static String DICTREFPROPERTYNAME = "org.openscience.cdk.dict";
    
    private LoggingTool logger;
    
    private String[] dictionaryNames = {
        "chemical", "elements", "descriptor-algorithms","reaction-processes"
    };
    private String[] dictionaryTypes = {
        "xml", "xml", "owl", "owl_React"
    };
    
    private Hashtable<String, Dictionary> dictionaries;

    public DictionaryDatabase() {
        logger = new LoggingTool(this);
        
        // read dictionaries distributed with CDK
        dictionaries = new Hashtable<String, Dictionary>();
        for (int i=0; i<dictionaryNames.length; i++) {
            String name = dictionaryNames[i];
            String type = dictionaryTypes[i];
            Dictionary dictionary = readDictionary(
                "org/openscience/cdk/dict/data/" + name, type
            );
            if (dictionary != null) {
                dictionaries.put(name.toLowerCase(), dictionary);
                logger.debug("Read dictionary: ", name);
            }
        }
    }

    private Dictionary readDictionary(String databaseLocator, String type) {
        Dictionary dictionary;
        // to distinguish between OWL: QSAR & REACT
        if(type.contains("_React"))
        	databaseLocator += "." + type.substring(0, type.length()-6);
        else
        	databaseLocator += "." + type;
        logger.info("Reading dictionary from ", databaseLocator);
        try {
            InputStreamReader reader = new InputStreamReader(
                this.getClass().getClassLoader().getResourceAsStream(databaseLocator));
            if (type.equals("owl")) {
                dictionary = OWLFile.unmarshal(reader);
            } else if (type.equals("owl_React")) {
                dictionary = OWLReact.unmarshal(reader);
            } else { // assume XML using Castor
                dictionary = Dictionary.unmarshal(reader);
            }
        } catch (Exception exception) {
            dictionary = null;
            logger.error("Could not read dictionary ", databaseLocator);
            logger.debug(exception);
        }
        return dictionary;
    }

    /**
     * Reads a custom dictionary into the database.
     * @param reader The reader from which the dictionary data will be read
     * @param name The name of the dictionary
     */
    public void readDictionary(Reader reader, String name) {
        name = name.toLowerCase();
        logger.debug("Reading dictionary: ", name);
        if (!dictionaries.containsKey(name)) {
            try {
                Dictionary dictionary = Dictionary.unmarshal(reader);
                dictionaries.put(name, dictionary);
                logger.debug("  ... loaded and stored");
            } catch (Exception exception) {
                logger.error("Could not read dictionary: ", name);
                logger.debug(exception);
            }
        } else {
            logger.error("Dictionary already loaded: ", name);
        }
    }

    /**
     * Returns a String[] with the names of the known dictionaries.
     * @return The names of the dictionaries
     */
    public String[] getDictionaryNames() {
        return dictionaryNames;
    }
    
    public Dictionary getDictionary(String dictionaryName) {
    	return dictionaries.get(dictionaryName);
    }
    
    /**
     * Returns a String[] with the id's of all entries in the specified database.
     * @return The entry names for the specified dictionary
     * @param dictionaryName The name of the dictionary
     */
    public String[] getDictionaryEntries(String dictionaryName) {
        Dictionary dictionary = getDictionary(dictionaryName);
        if (dictionary == null) {
            logger.error("Cannot find requested dictionary");
            return new String[0];
        } else {
            // FIXME: dummy method that needs an implementation
            Entry[] entries = dictionary.getEntries();
            String[] entryNames = new String[entries.length];
            logger.info("Found ", "" + entryNames.length, " entries in dictionary ", 
              dictionaryName);
            for (int i=0; i<entries.length; i++) {
                entryNames[i] = entries[i].getLabel();
            }
            return entryNames;
        }
    }

    public Entry[] getDictionaryEntry(String dictionaryName) {
        Dictionary dictionary = (Dictionary)dictionaries.get(dictionaryName);
        return dictionary.getEntries();
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
    public Enumeration<String> listDictionaries() {
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

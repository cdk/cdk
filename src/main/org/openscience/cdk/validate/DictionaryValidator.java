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
package org.openscience.cdk.validate;

import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.dict.DictionaryDatabase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Validates the existence of references to dictionaries.
 *
 * @author   Egon Willighagen
 * @cdk.svnrev  $Revision$
 * @cdk.created  2003-03-28
 */ 
public class DictionaryValidator extends AbstractValidator {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool(DictionaryValidator.class);
    }
    
    private DictionaryDatabase db;
    
    public DictionaryValidator(DictionaryDatabase db) {
        this.db = db;
    }

    public ValidationReport validateChemObject(IChemObject subject) {
        ValidationReport report = new ValidationReport();
        Map properties = subject.getProperties();
        Iterator iter = properties.keySet().iterator();
        ValidationTest noNamespace = new ValidationTest(subject,
            "Dictionary Reference lacks a namespace indicating the dictionary."
        );
        ValidationTest noDict = new ValidationTest(subject,
            "The referenced dictionary does not exist."
        );
        ValidationTest noEntry = new ValidationTest(subject,
            "The referenced entry does not exist in the dictionary."
        );
        while (iter.hasNext()) {
            Object key = iter.next();
            if (key instanceof String) {
                String keyName = (String)key;
                if (keyName.startsWith(DictionaryDatabase.DICTREFPROPERTYNAME)) {
                    String dictRef = (String)properties.get(keyName);
                    String details = "Dictref being anaylyzed: " + dictRef + ". ";
                    noNamespace.setDetails(details);
                    noDict.setDetails(details);
                    noEntry.setDetails(details);
                    int index = dictRef.indexOf(':');
                    if (index != -1) {
                        report.addOK(noNamespace);
                        String dict = dictRef.substring(0,index);
                        logger.debug("Looking for dictionary:" + dict);
                        if (db.hasDictionary(dict)) {
                            report.addOK(noDict);
                            if (dictRef.length() > index+1) {
                                String entry = dictRef.substring(index+1);
                                logger.debug("Looking for entry:" + entry);
                                if (db.hasEntry(dict, entry)) {
                                    report.addOK(noEntry);
                                } else {
                                    report.addError(noEntry);
                                }
                            } else {
                                report.addError(noEntry);
                            }
                        } else {
                            details += "The dictionary searched: " + dict + ".";
                            noDict.setDetails(details);
                            report.addError(noDict);
                            report.addError(noEntry);
                        }
                    } else {
                        // The dictRef has no namespace
                        details += "There is not a namespace given.";
                        noNamespace.setDetails(details);
                        report.addError(noNamespace);
                        report.addError(noDict);
                        report.addError(noEntry);
                    }
                } else {
                    // not a dictref
                }
            } else {
                // not a dictref
            }
        }
        return report;
    }
    
}

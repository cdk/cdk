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
package org.openscience.cdk.validate;

import org.openscience.cdk.*;
import org.openscience.cdk.dict.*;
import java.util.Map;
import java.util.Iterator;

/**
 * Validates the existence of references to dictionaries.
 *
 * @author   Egon Willighagen
 * @created  2003-03-28
 */ 
public class DictionaryValidator implements ValidatorInterface {

    private DictionaryDatabase db;
    
    public DictionaryValidator(DictionaryDatabase db) {
        this.db = db;
    }

    public ValidationReport validateChemObject(ChemObject subject) {
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
                    String details = "Dictref being anaylyzed: " + dictRef;
                    noNamespace.setDetails(details);
                    noDict.setDetails(details);
                    noEntry.setDetails(details);
                    int index = dictRef.indexOf(':');
                    if (index != -1) {
                        report.addOK(noNamespace);
                        String dict = dictRef.substring(0,index);
                        if (db.hasDictionary(dict)) {
                            report.addOK(noDict);
                            if (dictRef.length() > index+1) {
                                String entry = dictRef.substring(index+1);
                                if (db.hasEntry(dict, entry)) {
                                    report.addOK(noEntry);
                                } else {
                                    report.addError(noEntry);
                                }
                            } else {
                                report.addError(noEntry);
                            }
                        } else {
                            report.addError(noDict);
                            report.addError(noEntry);
                        }
                    } else {
                        // The dictRef has no namespace
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
    };

    
    public ValidationReport validateAtom(Atom subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateAtomType(AtomType subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateBond(Bond subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateChemModel(ChemModel subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateCrystal(Crystal subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateElement(Element subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateIsotope(Isotope subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateReaction(Reaction subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        ValidationReport report = new ValidationReport();
        return report;
    };
    
}

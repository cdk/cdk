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
 */
package org.openscience.cdk.dict;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;

/**
 * This class transforms implicit references to dictionary of CDK
 * objects into explicit references.
 *
 * <p>The syntax of the property names used is as follows:
 * org.openscience.cdk.dict:self or
 * org.openscience.cdk.dict:field:'fieldname', where fieldname
 * indicates a field for this object. The name may be appended
 * by :'number' to allow for more than one reference.
 *
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.svnrev  $Revision$
 * @cdk.created    2003-08-06
 * @cdk.keyword    dictionary, implicit CDK references
 * @cdk.module     dict
 */
public class CDKDictionaryReferences {

    private static String prefix = DictionaryDatabase.DICTREFPROPERTYNAME;
    
    public static void makeReferencesExplicit(IChemObject object) {
        if (object instanceof IAtom) {
            makeReferencesExplicitForAtom((IAtom)object);
        } else if (object instanceof IBond) {
            makeReferencesExplicitForBond((IBond)object);
        } else if (object instanceof IChemModel) {
            makeReferencesExplicitForChemModel((IChemModel)object);
        } else if (object instanceof IIsotope) {
            makeReferencesExplicitForIsotope((IIsotope)object);
        } else if (object instanceof IElement) {
            makeReferencesExplicitForElement((IElement)object);
        }  else if (object instanceof IMolecule) {
            makeReferencesExplicitForMolecule((IMolecule)object);
        } else if (object instanceof IReaction) {
            makeReferencesExplicitForReaction((IReaction)object);
        }
    }
    
    private static void makeReferencesExplicitForAtom(IAtom atom) {
        int selfCounter = 0;
        atom.setProperty(prefix + ":self:" + selfCounter++, "chemical:atom");
        
        makeReferencesExplicitForElement(atom);
    }
    
    private static void makeReferencesExplicitForBond(IBond bond) {
        int selfCounter = 0;
        bond.setProperty(prefix + ":self:" + selfCounter++, "chemical:covalentBond");
        bond.setProperty(prefix + ":field:order", "chemical:bondOrder");
    }

    private static void makeReferencesExplicitForChemModel(IChemModel model) { // NOPMD
        // nothing to do
    }

    private static void makeReferencesExplicitForElement(IElement element) {
        int selfCounter = 0;
        element.setProperty(prefix + ":field:symbol", "chemical:atomSymbol");
        element.setProperty(prefix + ":field:atomicNumber", "chemical:atomicNumber");
        
        if (element.getSymbol().equals("C")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:carbon");
        } else if (element.getSymbol().equals("N")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:nitrogen");
        } else if (element.getSymbol().equals("O")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:oxygen");
        } else if (element.getSymbol().equals("H")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:hydrogen");
        } else if (element.getSymbol().equals("S")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:sulphur");
        } else if (element.getSymbol().equals("P")) {
            element.setProperty(prefix + ":self:" + selfCounter++, "element:phosphorus");
        }
    }

    private static void makeReferencesExplicitForIsotope(IIsotope isotope) {
        int selfCounter = 0;
        isotope.setProperty(prefix + ":self:" + selfCounter++, "chemical:isotope");
    }

    private static void makeReferencesExplicitForMolecule(IMolecule molecule) {
        int selfCounter = 0;
        molecule.setProperty(prefix + ":self:" + selfCounter++, "chemical:molecularEntity");
        /* remark: this is not strictly true... the Compendium includes the
                   ion pair, which normally would not considered a CDK molecule */
    }

    private static void makeReferencesExplicitForReaction(IReaction reaction) {
        int selfCounter = 0;
        reaction.setProperty(prefix + ":self:" + selfCounter++, "reaction:reactionStep");
    }

}


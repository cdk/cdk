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
 */
package org.openscience.cdk.dict;

import org.openscience.cdk.*;

/**
 * This class transforms implicit references to dictionary of CDK
 * objects into explicit references.
 *
 * @author     Egon Willighagen <egonw@sci.kun.nl>
 * @created    2003-08-06
 * @keyword    dictionary, implicit CDK references
 */
public class CDKDictionaryReferences {

    public static void makeReferencesExplicit(ChemObject object) {
        if (object instanceof Atom) {
            makeReferencesExplicitForAtom((Atom)object);
        } else if (object instanceof Element) {
            makeReferencesExplicitForElement((Element)object);
        } else if (object instanceof Bond) {
            makeReferencesExplicitForBond((Bond)object);
        } else if (object instanceof Molecule) {
            makeReferencesExplicitForMolecule((Molecule)object);
        } else if (object instanceof Reaction) {
            makeReferencesExplicitForReaction((Reaction)object);
        } else if (object instanceof ChemModel) {
            makeReferencesExplicitForChemModel((ChemModel)object);
        } else {
            // don't do anything yet
        }
    }
    
    private static void makeReferencesExplicitForAtom(Atom atom) {
        int selfCounter = 0;
        // dictref: Atom:self = chemical:atom
        atom.setProperty(DictionaryDatabase.DICTREFPROPERTYNAME +
            ":self:" + selfCounter, "chemical:atom");
        
        makeReferencesExplicitForElement((Element)atom);
    }
    
    private static void makeReferencesExplicitForElement(Element element) {
        int selfCounter = 0;
        // dictref: Element:field:symbol = chemical:atomSymbol
        element.setProperty(DictionaryDatabase.DICTREFPROPERTYNAME +
            ":field:symbol", "chemical:atomSymbol");
    }

    private static void makeReferencesExplicitForBond(Bond bond) {
        int selfCounter = 0;
        // dictref: Bond:self = chemical:bond
        bond.setProperty(DictionaryDatabase.DICTREFPROPERTYNAME +
            ":field:order", "chemical:bondOrder");
    }

    private static void makeReferencesExplicitForMolecule(Molecule molecule) {
    }

    private static void makeReferencesExplicitForReaction(Reaction reaction) {
        int selfCounter = 0;
        // dictref: Reaction:self = reaction:reactionStep
        reaction.setProperty(DictionaryDatabase.DICTREFPROPERTYNAME +
            ":self:" + selfCounter, "reaction:reactionStep");
    }

    private static void makeReferencesExplicitForChemModel(ChemModel model) {
    }

}


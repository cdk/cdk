/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.interfaces.Element;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.SetOfReactions;

/**
 * Interface that Validators need to implement to be used in validation.
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-03-28
 */ 
public interface ValidatorInterface {

    public ValidationReport validateAtom(IAtom subject);
    public ValidationReport validateAtomContainer(IAtomContainer subject);
    public ValidationReport validateAtomType(IAtomType subject);
    public ValidationReport validateBond(Bond subject);
    public ValidationReport validateChemFile(ChemFile subject);
    public ValidationReport validateChemModel(ChemModel subject);
    public ValidationReport validateChemObject(IChemObject object);
    public ValidationReport validateChemSequence(ChemSequence subject);
    public ValidationReport validateCrystal(Crystal subject);
    public ValidationReport validateElectronContainer(ElectronContainer subject);
    public ValidationReport validateElement(Element subject);
    public ValidationReport validateIsotope(Isotope subject);
    public ValidationReport validateMolecule(Molecule subject);
    public ValidationReport validateReaction(Reaction subject);
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject);
    public ValidationReport validateSetOfReactions(SetOfReactions subject);
    
}

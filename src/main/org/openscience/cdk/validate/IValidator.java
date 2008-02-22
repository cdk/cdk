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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * Interface that Validators need to implement to be used in validation.
 *
 * @author   Egon Willighagen
 * @cdk.svnrev  $Revision$
 * @cdk.created  2003-03-28
 */ 
public interface IValidator {

    public ValidationReport validateAtom(IAtom subject);
    public ValidationReport validateAtomContainer(IAtomContainer subject);
    public ValidationReport validateAtomType(IAtomType subject);
    public ValidationReport validateBond(IBond subject);
    public ValidationReport validateChemFile(IChemFile subject);
    public ValidationReport validateChemModel(IChemModel subject);
    public ValidationReport validateChemObject(IChemObject object);
    public ValidationReport validateChemSequence(IChemSequence subject);
    public ValidationReport validateCrystal(ICrystal subject);
    public ValidationReport validateElectronContainer(IElectronContainer subject);
    public ValidationReport validateElement(IElement subject);
    public ValidationReport validateIsotope(IIsotope subject);
    public ValidationReport validateMolecule(IMolecule subject);
    public ValidationReport validateReaction(IReaction subject);
    public ValidationReport validateMoleculeSet(IMoleculeSet subject);
    public ValidationReport validateReactionSet(IReactionSet subject);
    
}

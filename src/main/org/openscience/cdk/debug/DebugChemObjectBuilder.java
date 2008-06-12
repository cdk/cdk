/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.debug;

import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IBond.Order;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * A helper class to instantiate a IChemObject for the debug implementation.
 *
 * @author        egonw
 * @cdk.module    datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugChemObjectBuilder implements IChemObjectBuilder {

	private static DebugChemObjectBuilder instance = null;
	
	private DebugChemObjectBuilder() {}

	public static DebugChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DebugChemObjectBuilder();
		}
		return instance;
	}
	
	public IAminoAcid newAminoAcid() {
		return new DebugAminoAcid();
	}
	
	public IAtom newAtom() {
		return new DebugAtom();
	}
	
    public IAtom newAtom(String elementSymbol) {
    	return new DebugAtom(elementSymbol);
    }
    
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new DebugAtom(elementSymbol, point2d);
    }

    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new DebugAtom(elementSymbol, point3d);
    }
		
	public IAtomContainer newAtomContainer() {
		return new DebugAtomContainer();
	}
    
	public IAtomContainer newAtomContainer(int atomCount, int electronContainerCount, int lonePairCount, int singleElectronCount) {
		return new DebugAtomContainer(atomCount, electronContainerCount, lonePairCount, singleElectronCount);
	}
    
	public IAtomContainer newAtomContainer(IAtomContainer container) {
		return new DebugAtomContainer(container);
	}
	
    public IAtomParity newAtomParity(
    		IAtom centralAtom, 
    		IAtom first, 
    		IAtom second, 
    		IAtom third, 
    		IAtom fourth,
            int parity) {
    	return new DebugAtomParity(centralAtom, first, second, third, fourth, parity);
    }

	public IAtomType newAtomType(String elementSymbol) {
		return new DebugAtomType(elementSymbol);
	}

	public IAtomType newAtomType(String identifier, String elementSymbol) {
		return new DebugAtomType(identifier, elementSymbol);
	}

	public IBioPolymer newBioPolymer(){
		return new DebugBioPolymer();
	}
	
	public IPDBAtom newPDBAtom(IElement element) {
    	return new DebugPDBAtom(element);
    }
	
	public IPDBAtom newPDBAtom(String elementSymbol) {
    	return new DebugPDBAtom(elementSymbol);
    }
    
    public IPDBAtom newPDBAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new DebugPDBAtom(elementSymbol, point3d);
    }
    
    public IPDBStructure newPDBStructure() {
    	return new DebugPDBStructure();
    }
    
	public IPDBPolymer newPDBPolymer(){
		return new DebugPDBPolymer();
	}
	
	public IPDBMonomer newPDBMonomer(){
		return new DebugPDBMonomer();
	}
	
	public IPDBStructure newStructure(){
		return new DebugPDBStructure();
	}

	public IBond newBond() {
		return new DebugBond();
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2) {
		return new DebugBond(atom1, atom2);
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, Order order) {
		return new DebugBond(atom1, atom2, order);
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, Order order, int stereo) {
		return new DebugBond(atom1, atom2, order, stereo);
	}

    public IBond newBond(IAtom[] atoms) {
        return new DebugBond(atoms);
    }

    public IBond newBond(IAtom[] atoms, Order order) {
        return new DebugBond(atoms, order);
    }

    public IChemFile newChemFile() {
		return new DebugChemFile();
	}

	public IChemModel newChemModel() {
		return new DebugChemModel();
	}
	
	public IChemObject newChemObject() {
		return new DebugChemObject();
	}
	
	public IChemSequence newChemSequence() {
		return new DebugChemSequence();   
	}
	
    public ICrystal newCrystal() {
    	return new DebugCrystal();
    }
    
    public ICrystal newCrystal(IAtomContainer container) {
    	return new DebugCrystal(container);
    }
    
    public IElectronContainer newElectronContainer() {
    	return new DebugElectronContainer();
    }
    
    public IElement newElement() {
    	return new DebugElement();
    }

    public IElement newElement(String symbol) {
    	return new DebugElement(symbol);
    }

    public IElement newElement(String symbol, int atomicNumber) {
    	return new DebugElement(symbol, atomicNumber);
    }

	public IIsotope newIsotope(String elementSymbol) {
		return new DebugIsotope(elementSymbol);
	}
	
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		return new DebugIsotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
	}

	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		return new DebugIsotope(atomicNumber, elementSymbol, exactMass, abundance);
	}

	public IIsotope newIsotope(String elementSymbol, int massNumber) {
		return new DebugIsotope(elementSymbol, massNumber);
	}

    public ILonePair newLonePair() {
    	return new DebugLonePair();
    }

    public ILonePair newLonePair(IAtom atom) {
    	return new DebugLonePair(atom);
    }

    public IMapping newMapping(IChemObject objectOne, IChemObject objectTwo) {
		return new DebugMapping(objectOne, objectTwo);
	}
    
	public IMolecule newMolecule() {
		return new DebugMolecule();
	}

	public IMolecule newMolecule(int atomCount, int electronContainerCount, int lonePairCount, int singleElectronCount) {
		return new DebugMolecule(atomCount, electronContainerCount, lonePairCount, singleElectronCount);
	}

	public IMolecule newMolecule(IAtomContainer container) {
		return new DebugMolecule(container);
	}

	public IMonomer newMonomer () {
		return new DebugMonomer();
	}
	
	public IPolymer newPolymer() {
		return new DebugPolymer();
	}

    public IReaction newReaction() {
    	return new DebugReaction();	
    }
	
	public IRing newRing() {
		return new DebugRing();
	}
	
	public IRing newRing(IAtomContainer container) {
		return new DebugRing(container);
	}
	
	public IRing newRing(int ringSize, String elementSymbol) {
		return new DebugRing(ringSize, elementSymbol);
	}
	
	public IRing newRing(int ringSize) {
		return new DebugRing(ringSize);
	}

	public IRingSet newRingSet() {
		return new DebugRingSet();
	}

	public IAtomContainerSet newAtomContainerSet() {
		return new DebugAtomContainerSet();
	}

	public IMoleculeSet newMoleculeSet() {
		return new DebugMoleculeSet();
	}

	public IReactionSet newReactionSet() {
		return new DebugReactionSet();
	}

	public IReactionScheme newReactionScheme() {
		return new DebugReactionScheme();
	}
	
    public ISingleElectron newSingleElectron() {
    	return new DebugSingleElectron();
    }
    
    public ISingleElectron newSingleElectron(IAtom atom) {
    	return new DebugSingleElectron(atom);   
    }

	public IStrand newStrand() {
		return new DebugStrand();
	}

	public IPseudoAtom newPseudoAtom() {
		return new DebugPseudoAtom();
	}

	public IPseudoAtom newPseudoAtom(String label) {
		return new DebugPseudoAtom(label);
	}

	public IPseudoAtom newPseudoAtom(IAtom atom) {
		return new DebugPseudoAtom(atom);
	}

	public IPseudoAtom newPseudoAtom(String label, Point3d point3d) {
		return new DebugPseudoAtom(label, point3d);
	}

	public IPseudoAtom newPseudoAtom(String label, Point2d point2d) {
		return new DebugPseudoAtom(label, point2d);
	}

	public IAtom newAtom(IElement element) {
		return new DebugAtom(element);
	}

	public IAtomType newAtomType(IElement element) {
		return new DebugAtomType(element);
	}

	public IChemObject newChemObject(IChemObject object) {
		return new DebugChemObject(object);
	}

	public IElement newElement(IElement element) {
		return new DebugElement(element);
	}

	public IIsotope newIsotope(IElement element) {
		return new DebugIsotope(element);
	}

	public IPseudoAtom newPseudoAtom(IElement element) {
		return new DebugPseudoAtom(element);
	}

	public IFragmentAtom newFragmentAtom() {
		return new DebugFragmentAtom();
	}

	public IAdductFormula newAdductFormula() {
		return new DebugAdductFormula();
    }

	public IAdductFormula newAdductFormula(IMolecularFormula formula) {
	    return new DebugAdductFormula(formula);
    }

	public IMolecularFormula newMolecularFormula() {
	    return new DebugMolecularFormula();
    }

	public IMolecularFormulaSet newMolecularFormulaSet() {
	    return new DebugMolecularFormulaSet();
    }

	public IMolecularFormulaSet newMolecularFormulaSet(IMolecularFormula formula) {
	    return new DebugMolecularFormulaSet(formula);
    }
}



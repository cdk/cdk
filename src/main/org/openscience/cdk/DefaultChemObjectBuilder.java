/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk;

import org.openscience.cdk.formula.AdductFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaSet;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStructure;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

/**
 * A helper class to instantiate a IChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    data
 * @cdk.svnrev  $Revision$
 */
public class DefaultChemObjectBuilder implements IChemObjectBuilder {

	private static DefaultChemObjectBuilder instance = null;
	
	private DefaultChemObjectBuilder() {}

	public static DefaultChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DefaultChemObjectBuilder();
		}
		return instance;
	}
	
	public IAminoAcid newAminoAcid() {
		return new AminoAcid();
	}
	
	public IAtom newAtom() {
		return new Atom();
	}
	
    public IAtom newAtom(String elementSymbol) {
    	return new Atom(elementSymbol);
    }
    
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new Atom(elementSymbol, point2d);
    }

    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new Atom(elementSymbol, point3d);
    }
		
	public IAtomContainer newAtomContainer() {
		return new AtomContainer();
	}
    
	public IAtomContainer newAtomContainer(int atomCount, int bondCount, int lonePairCount, int singleElectronCount) {
		return new AtomContainer(atomCount, bondCount, lonePairCount, singleElectronCount);
	}
    
	public IAtomContainer newAtomContainer(IAtomContainer container) {
		return new AtomContainer(container);
	}
	
    public IAtomParity newAtomParity(IAtom centralAtom, IAtom first, IAtom second, 
    		IAtom third, IAtom fourth, int parity) {
    	return new AtomParity(centralAtom, first, second, third, fourth, parity);
    }

	public IAtomType newAtomType(String elementSymbol) {
		return new AtomType(elementSymbol);
	}

	public IAtomType newAtomType(String identifier, String elementSymbol) {
		return new AtomType(identifier, elementSymbol);
	}

	public IBioPolymer newBioPolymer(){
		return new BioPolymer();
	}

	public IBond newBond() {
		return new Bond();
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2) {
		return new Bond(atom1, atom2);
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, IBond.Order order) {
		return new Bond(atom1, atom2, order);
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, IBond.Order order, int stereo) {
		return new Bond(atom1, atom2, order, stereo);
	}

    public IBond newBond(IAtom[] atoms) {
        return new Bond(atoms);
    }

    public IBond newBond(IAtom[] atoms, IBond.Order order) {
        return new Bond(atoms, order);
    }

    public IChemFile newChemFile() {
		return new ChemFile();
	}

	public IChemModel newChemModel() {
		return new ChemModel();
	}
	
	public IChemObject newChemObject() {
		return new ChemObject();
	}
	
	public IChemSequence newChemSequence() {
		return new ChemSequence();   
	}
	
    public ICrystal newCrystal() {
    	return new Crystal();
    }
    
    public ICrystal newCrystal(IAtomContainer container) {
    	return new Crystal(container);
    }
    
    public IElectronContainer newElectronContainer() {
    	return new ElectronContainer();
    }
    
    public IElement newElement() {
    	return new Element();
    }

    public IElement newElement(String symbol) {
    	return new Element(symbol);
    }

    public IElement newElement(String symbol, int atomicNumber) {
    	return new Element(symbol, atomicNumber);
    }

	public IIsotope newIsotope(String elementSymbol) {
		return new Isotope(elementSymbol);
	}
	
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
	}

	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, exactMass, abundance);
	}

	public IIsotope newIsotope(String elementSymbol, int massNumber) {
		return new Isotope(elementSymbol, massNumber);
	}

    public ILonePair newLonePair() {
    	return new LonePair();
    }

    public ILonePair newLonePair(IAtom atom) {
    	return new LonePair(atom);
    }

    public IMapping newMapping(IChemObject objectOne, IChemObject objectTwo) {
		return new Mapping(objectOne, objectTwo);
	}
    
	public IMolecule newMolecule() {
		return new Molecule();
	}

	public IMolecule newMolecule(int atomCount, int electronContainerCount, int lonePairCount, int singleElectronCount) {
		return new Molecule(atomCount, electronContainerCount, lonePairCount, singleElectronCount);
	}

	public IMolecule newMolecule(IAtomContainer container) {
		return new Molecule(container);
	}

	public IMonomer newMonomer () {
		return new Monomer();
	}
	
	public IPolymer newPolymer() {
		return new Polymer();
	}
	
	public IPDBAtom newPDBAtom(IElement element){
		return new PDBAtom(element);
	}
	
	public IPDBAtom newPDBAtom(String symbol){
		return new PDBAtom(symbol);
	}
	
	public IPDBAtom newPDBAtom(String symbol, Point3d coordinate){
		return new PDBAtom(symbol, coordinate);
	}

	public IPDBPolymer newPDBPolymer() {
		return new PDBPolymer();
	}
	
	public IPDBStructure newPDBStructure() {
		return new PDBStructure();
	}
	
	public IPDBMonomer newPDBMonomer() {
		return new PDBMonomer();
	}

    public IReaction newReaction() {
    	return new Reaction();	
    }
	
	public IRing newRing() {
		return new Ring();
	}
	
	public IRing newRing(IAtomContainer container) {
		return new Ring(container);
	}
	
	public IRing newRing(int ringSize, String elementSymbol) {
		return new Ring(ringSize, elementSymbol);
	}
	
	public IRing newRing(int ringSize) {
		return new Ring(ringSize);
	}

	public IRingSet newRingSet() {
		return new RingSet();
	}

	public IAtomContainerSet newAtomContainerSet() {
		return new AtomContainerSet();
	}

	public IMoleculeSet newMoleculeSet() {
		return new MoleculeSet();
	}

	public IReactionSet newReactionSet() {
		return new ReactionSet();
	}

	public IReactionScheme newReactionScheme() {
		return new ReactionScheme();
	}
	
    public ISingleElectron newSingleElectron() {
    	return new SingleElectron();
    }
    
    public ISingleElectron newSingleElectron(IAtom atom) {
    	return new SingleElectron(atom);   
    }

	public IStrand newStrand() {
		return new Strand();
	}

	public IPseudoAtom newPseudoAtom() {
		return new PseudoAtom();
	}

	public IPseudoAtom newPseudoAtom(String label) {
		return new PseudoAtom(label);
	}

	public IPseudoAtom newPseudoAtom(IAtom atom) {
		return new PseudoAtom(atom);
	}

	public IPseudoAtom newPseudoAtom(String label, Point3d point3d) {
		return new PseudoAtom(label, point3d);
	}

	public IPseudoAtom newPseudoAtom(String label, Point2d point2d) {
		return new PseudoAtom(label, point2d);
	}

	public IAtom newAtom(IElement element) {
		return new Atom(element);
	}

	public IAtomType newAtomType(IElement element) {
		return new AtomType(element);
	}

	public IChemObject newChemObject(IChemObject object) {
		return new ChemObject(object);
	}

	public IElement newElement(IElement element) {
		return new Element(element);
	}

	public IIsotope newIsotope(IElement element) {
		return new Isotope(element);
	}

	public IPseudoAtom newPseudoAtom(IElement element) {
		return new PseudoAtom(element);
	}

	public IFragmentAtom newFragmentAtom() {
		return new FragmentAtom();
	}

	public IAdductFormula newAdductFormula() {
	    return new AdductFormula();
    }

	public IMolecularFormula newMolecularFormula() {
	    return new MolecularFormula();
    }

	public IMolecularFormulaSet newMolecularFormulaSet() {
	    return new MolecularFormulaSet();
    }

	public IAdductFormula newAdductFormula(IMolecularFormula formula) {
	    return new AdductFormula(formula);
    }

	public IMolecularFormulaSet newMolecularFormulaSet(IMolecularFormula formula) {
	    return new MolecularFormulaSet(formula);
    }
}



/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;

/**
 * A helper class to instantiate a IChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    data
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
	
	public org.openscience.cdk.interfaces.IAminoAcid newAminoAcid() {
		return new AminoAcid();
	}
	
	public org.openscience.cdk.interfaces.IAtom newAtom() {
		return new Atom();
	}
	
    public org.openscience.cdk.interfaces.IAtom newAtom(String elementSymbol) {
    	return new Atom(elementSymbol);
    }
    
    public org.openscience.cdk.interfaces.IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new Atom(elementSymbol, point2d);
    }

    public org.openscience.cdk.interfaces.IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new Atom(elementSymbol, point3d);
    }
		
	public org.openscience.cdk.interfaces.IAtomContainer newAtomContainer() {
		return new AtomContainer();
	}
    
	public org.openscience.cdk.interfaces.IAtomContainer newAtomContainer(int atomCount, int electronContainerCount) {
		return new AtomContainer(atomCount, electronContainerCount);
	}
    
	public org.openscience.cdk.interfaces.IAtomContainer newAtomContainer(org.openscience.cdk.interfaces.IAtomContainer container) {
		return new AtomContainer(container);
	}
	
    public org.openscience.cdk.interfaces.IAtomParity newAtomParity(
    		org.openscience.cdk.interfaces.IAtom centralAtom, 
    		org.openscience.cdk.interfaces.IAtom first, 
    		org.openscience.cdk.interfaces.IAtom second, 
    		org.openscience.cdk.interfaces.IAtom third, 
    		org.openscience.cdk.interfaces.IAtom fourth,
            int parity) {
    	return new AtomParity(centralAtom, first, second, third, fourth, parity);
    }

	public org.openscience.cdk.interfaces.IAtomType newAtomType(String elementSymbol) {
		return new AtomType(elementSymbol);
	}

	public org.openscience.cdk.interfaces.IAtomType newAtomType(String identifier, String elementSymbol) {
		return new AtomType(identifier, elementSymbol);
	}

	public org.openscience.cdk.interfaces.IBioPolymer newBioPolymer(){
		return new BioPolymer();
	}

	public org.openscience.cdk.interfaces.IBond newBond() {
		return new Bond();
	}
	
	public org.openscience.cdk.interfaces.IBond newBond(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2) {
		return new Bond(atom1, atom2);
	}
	
	public org.openscience.cdk.interfaces.IBond newBond(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2, double order) {
		return new Bond(atom1, atom2, order);
	}
	
	public org.openscience.cdk.interfaces.IBond newBond(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2, double order, int stereo) {
		return new Bond(atom1, atom2, order, stereo);
	}
	
	public org.openscience.cdk.interfaces.IChemFile newChemFile() {
		return new ChemFile();
	}

	public org.openscience.cdk.interfaces.IChemModel newChemModel() {
		return new ChemModel();
	}
	
	public org.openscience.cdk.interfaces.IChemObject newChemObject() {
		return new ChemObject();
	}
	
	public org.openscience.cdk.interfaces.IChemSequence newChemSequence() {
		return new ChemSequence();   
	}
	
    public org.openscience.cdk.interfaces.ICrystal newCrystal() {
    	return new Crystal();
    }
    
    public org.openscience.cdk.interfaces.ICrystal newCrystal(org.openscience.cdk.interfaces.IAtomContainer container) {
    	return new Crystal(container);
    }
    
    public org.openscience.cdk.interfaces.IElectronContainer newElectronContainer() {
    	return new ElectronContainer();
    }
    
    public org.openscience.cdk.interfaces.IElement newElement() {
    	return new Element();
    }

    public org.openscience.cdk.interfaces.IElement newElement(String symbol) {
    	return new Element(symbol);
    }

    public org.openscience.cdk.interfaces.IElement newElement(String symbol, int atomicNumber) {
    	return new Element(symbol, atomicNumber);
    }

	public org.openscience.cdk.interfaces.IIsotope newIsotope(String elementSymbol) {
		return new Isotope(elementSymbol);
	}
	
	public org.openscience.cdk.interfaces.IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.IIsotope newIsotope(String elementSymbol, int massNumber) {
		return new Isotope(elementSymbol, massNumber);
	}

    public org.openscience.cdk.interfaces.ILonePair newLonePair() {
    	return new LonePair();
    }

    public org.openscience.cdk.interfaces.ILonePair newLonePair(org.openscience.cdk.interfaces.IAtom atom) {
    	return new LonePair(atom);
    }

	public org.openscience.cdk.interfaces.Molecule newMolecule() {
		return new Molecule();
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(int atomCount, int electronContainerCount) {
		return new Molecule(atomCount, electronContainerCount);
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(org.openscience.cdk.interfaces.IAtomContainer container) {
		return new Molecule(container);
	}

	public org.openscience.cdk.interfaces.IMonomer newMonomer () {
		return new Monomer();
	}
	
	public org.openscience.cdk.interfaces.Polymer newPolymer() {
		return new Polymer();
	}

    public org.openscience.cdk.interfaces.Reaction newReaction() {
    	return new Reaction();	
    }
	
	public org.openscience.cdk.interfaces.Ring newRing() {
		return new Ring();
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(org.openscience.cdk.interfaces.IAtomContainer container) {
		return new Ring(container);
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(int ringSize, String elementSymbol) {
		return new Ring(ringSize, elementSymbol);
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(int ringSize) {
		return new Ring(ringSize);
	}

	public org.openscience.cdk.interfaces.RingSet newRingSet() {
		return new RingSet();
	}

	public org.openscience.cdk.interfaces.SetOfAtomContainers newSetOfAtomContainers() {
		return new SetOfAtomContainers();
	}

	public org.openscience.cdk.interfaces.SetOfMolecules newSetOfMolecules() {
		return new SetOfMolecules();
	}

	public org.openscience.cdk.interfaces.SetOfReactions newSetOfReactions() {
		return new SetOfReactions();
	}
	
    public org.openscience.cdk.interfaces.ISingleElectron newSingleElectron() {
    	return new SingleElectron();
    }
    
    public org.openscience.cdk.interfaces.ISingleElectron newSingleElectron(org.openscience.cdk.interfaces.IAtom atom) {
    	return new SingleElectron(atom);   
    }

	public org.openscience.cdk.interfaces.Strand newStrand() {
		return new Strand();
	}

	public org.openscience.cdk.interfaces.IPseudoAtom newPseudoAtom() {
		return new PseudoAtom();
	}

	public org.openscience.cdk.interfaces.IPseudoAtom newPseudoAtom(String label) {
		return new PseudoAtom(label);
	}

	public org.openscience.cdk.interfaces.IPseudoAtom newPseudoAtom(org.openscience.cdk.interfaces.IAtom atom) {
		return new PseudoAtom(atom);
	}

	public org.openscience.cdk.interfaces.IPseudoAtom newPseudoAtom(String label, Point3d point3d) {
		return new PseudoAtom(label, point3d);
	}

	public org.openscience.cdk.interfaces.IPseudoAtom newPseudoAtom(String label, Point2d point2d) {
		return new PseudoAtom(label, point2d);
	}
}



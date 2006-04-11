/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 * $Revision: 5855 $
 *
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.nonotify;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomParity;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Element;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Monomer;
import org.openscience.cdk.Polymer;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.Strand;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStrand;

/**
 * A helper class to instantiate a IChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    nonotify
 */
public class NoNotificationChemObjectBuilder implements IChemObjectBuilder {

	private static NoNotificationChemObjectBuilder instance = null;
	
	private NoNotificationChemObjectBuilder() {}

	public static NoNotificationChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new NoNotificationChemObjectBuilder();
		}
		return instance;
	}
	
	public IAminoAcid newAminoAcid() {
		IAminoAcid object = new NNAminoAcid();
		object.setNotification(false);
		return object;
	}
	
	public IAtom newAtom() {
		IAtom object = new NNAtom();
		object.setNotification(false);
		return object;
	}
	
    public IAtom newAtom(String elementSymbol) {
    	IAtom object = new NNAtom(elementSymbol);
		object.setNotification(false);
		return object;
    }
    
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	IAtom object = new NNAtom(elementSymbol, point2d);
		object.setNotification(false);
		return object;
    }

    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	IAtom object = new NNAtom(elementSymbol, point3d);
		object.setNotification(false);
		return object;
    }
		
	public IAtomContainer newAtomContainer() {
		IAtomContainer object = new NNAtomContainer();
		object.setNotification(false);
		return object;
	}
    
	public IAtomContainer newAtomContainer(int atomCount, int electronContainerCount) {
		IAtomContainer object = new NNAtomContainer(atomCount, electronContainerCount);
		object.setNotification(false);
		return object;
	}
    
	public IAtomContainer newAtomContainer(IAtomContainer container) {
		IAtomContainer object = new NNAtomContainer(container);
		object.setNotification(false);
		return object;
	}
	
    public IAtomParity newAtomParity(
    		IAtom centralAtom, 
    		IAtom first, 
    		IAtom second, 
    		IAtom third, 
    		IAtom fourth,
            int parity) {
    	IAtomParity object = new NNAtomParity(centralAtom, first, second, third, fourth, parity);
		return object;
    }

	public IAtomType newAtomType(String elementSymbol) {
		IAtomType object = new NNAtomType(elementSymbol);
		object.setNotification(false);
		return object;
	}

	public IAtomType newAtomType(String identifier, String elementSymbol) {
		IAtomType object = new NNAtomType(identifier, elementSymbol);
		object.setNotification(false);
		return object;
	}

	public IBioPolymer newBioPolymer(){
		IBioPolymer object = new NNBioPolymer();
		object.setNotification(false);
		return object;
	}

	public IBond newBond() {
		IBond object = new NNBond();
		object.setNotification(false);
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2) {
		IBond object = new NNBond(atom1, atom2);
		object.setNotification(false);
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, double order) {
		IBond object = new NNBond(atom1, atom2, order);
		object.setNotification(false);
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, double order, int stereo) {
		IBond object = new NNBond(atom1, atom2, order, stereo);
		object.setNotification(false);
		return object;
	}
	
	public IChemFile newChemFile() {
		IChemFile object = new NNChemFile();
		object.setNotification(false);
		return object;
	}

	public IChemModel newChemModel() {
		IChemModel object = new NNChemModel();
		object.setNotification(false);
		return object;
	}
	
	public IChemObject newChemObject() {
		IChemObject object = new NNChemObject();
		object.setNotification(false);
		return object;
	}
	
	public IChemSequence newChemSequence() {
		IChemSequence object = new ChemSequence();   
		object.setNotification(false);
		return object;
	}
	
    public ICrystal newCrystal() {
    	ICrystal object = new Crystal();
		object.setNotification(false);
		return object;
    }
    
    public ICrystal newCrystal(IAtomContainer container) {
    	ICrystal object = new Crystal(container);
		object.setNotification(false);
		return object;
    }
    
    public IElectronContainer newElectronContainer() {
    	IElectronContainer object = new ElectronContainer();
		object.setNotification(false);
		return object;
    }
    
    public IElement newElement() {
    	IElement object = new Element();
		object.setNotification(false);
		return object;
    }

    public IElement newElement(String symbol) {
    	IElement object = new Element(symbol);
		object.setNotification(false);
		return object;
    }

    public IElement newElement(String symbol, int atomicNumber) {
    	IElement object = new Element(symbol, atomicNumber);
		object.setNotification(false);
		return object;
    }

	public IIsotope newIsotope(String elementSymbol) {
		IIsotope object = new Isotope(elementSymbol);
		object.setNotification(false);
		return object;
	}
	
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		IIsotope object = new Isotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
		object.setNotification(false);
		return object;
	}

	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		IIsotope object = new Isotope(atomicNumber, elementSymbol, exactMass, abundance);
		object.setNotification(false);
		return object;
	}

	public IIsotope newIsotope(String elementSymbol, int massNumber) {
		IIsotope object = new Isotope(elementSymbol, massNumber);
		object.setNotification(false);
		return object;
	}

    public ILonePair newLonePair() {
    	ILonePair object = new LonePair();
		object.setNotification(false);
		return object;
    }

    public ILonePair newLonePair(IAtom atom) {
    	ILonePair object = new LonePair(atom);
		object.setNotification(false);
		return object;
    }

    public IMapping newMapping(IChemObject objectOne, 
                                                              IChemObject objectTwo) {
    	IMapping object = new Mapping(objectOne, objectTwo);
		object.setNotification(false);
		return object;
	}
    
	public IMolecule newMolecule() {
		IMolecule object = new Molecule();
		object.setNotification(false);
		return object;
	}

	public IMolecule newMolecule(int atomCount, int electronContainerCount) {
		IMolecule object = new Molecule(atomCount, electronContainerCount);
		object.setNotification(false);
		return object;
	}

	public IMolecule newMolecule(IAtomContainer container) {
		IMolecule object = new Molecule(container);
		object.setNotification(false);
		return object;
	}

	public IMonomer newMonomer () {
		IMonomer object = new Monomer();
		object.setNotification(false);
		return object;
	}
	
	public IPolymer newPolymer() {
		IPolymer object = new Polymer();
		object.setNotification(false);
		return object;
	}

    public IReaction newReaction() {
    	IReaction object = new Reaction();	
		object.setNotification(false);
		return object;
    }
	
	public IRing newRing() {
		IRing object = new Ring();
		object.setNotification(false);
		return object;
	}
	
	public IRing newRing(IAtomContainer container) {
		IRing object = new Ring(container);
		object.setNotification(false);
		return object;
	}
	
	public IRing newRing(int ringSize, String elementSymbol) {
		IRing object = new Ring(ringSize, elementSymbol);
		object.setNotification(false);
		return object;
	}
	
	public IRing newRing(int ringSize) {
		IRing object = new Ring(ringSize);
		object.setNotification(false);
		return object;
	}

	public IRingSet newRingSet() {
		IRingSet object = new RingSet();
		return object;
	}

	public ISetOfAtomContainers newSetOfAtomContainers() {
		ISetOfAtomContainers object = new SetOfAtomContainers();
		object.setNotification(false);
		return object;
	}

	public ISetOfMolecules newSetOfMolecules() {
		ISetOfMolecules object = new SetOfMolecules();
		object.setNotification(false);
		return object;
	}

	public ISetOfReactions newSetOfReactions() {
		ISetOfReactions object = new SetOfReactions();
		object.setNotification(false);
		return object;
	}
	
    public ISingleElectron newSingleElectron() {
    	ISingleElectron object = new SingleElectron();
		object.setNotification(false);
		return object;
    }
    
    public ISingleElectron newSingleElectron(IAtom atom) {
    	ISingleElectron object = new SingleElectron(atom);   
		object.setNotification(false);
		return object;
    }

	public IStrand newStrand() {
		IStrand object = new Strand();
		object.setNotification(false);
		return object;
	}

	public IPseudoAtom newPseudoAtom() {
		IPseudoAtom object = new PseudoAtom();
		object.setNotification(false);
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label) {
		IPseudoAtom object = new PseudoAtom(label);
		object.setNotification(false);
		return object;
	}

	public IPseudoAtom newPseudoAtom(IAtom atom) {
		IPseudoAtom object = new PseudoAtom(atom);
		object.setNotification(false);
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label, Point3d point3d) {
		IPseudoAtom object = new PseudoAtom(label, point3d);
		object.setNotification(false);
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label, Point2d point2d) {
		IPseudoAtom object = new PseudoAtom(label, point2d);
		object.setNotification(false);
		return object;
	}
}



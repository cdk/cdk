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
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
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
		return object;
	}
	
	public IAtom newAtom() {
		IAtom object = new NNAtom();
		return object;
	}
	
    public IAtom newAtom(String elementSymbol) {
    	IAtom object = new NNAtom(elementSymbol);
		return object;
    }
    
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	IAtom object = new NNAtom(elementSymbol, point2d);

		return object;
    }

    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	IAtom object = new NNAtom(elementSymbol, point3d);
		return object;
    }
		
	public IAtomContainer newAtomContainer() {
		IAtomContainer object = new NNAtomContainer();
		return object;
	}
    
	public IAtomContainer newAtomContainer(int atomCount, int electronContainerCount) {
		IAtomContainer object = new NNAtomContainer(atomCount, electronContainerCount);
		return object;
	}
    
	public IAtomContainer newAtomContainer(IAtomContainer container) {
		IAtomContainer object = new NNAtomContainer(container);
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
		return object;
	}

	public IAtomType newAtomType(String identifier, String elementSymbol) {
		IAtomType object = new NNAtomType(identifier, elementSymbol);
		return object;
	}

	public IBioPolymer newBioPolymer(){
		IBioPolymer object = new NNBioPolymer();
		return object;
	}

	public IBond newBond() {
		IBond object = new NNBond();
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2) {
		IBond object = new NNBond(atom1, atom2);
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, double order) {
		IBond object = new NNBond(atom1, atom2, order);
		return object;
	}
	
	public IBond newBond(IAtom atom1, IAtom atom2, double order, int stereo) {
		IBond object = new NNBond(atom1, atom2, order, stereo);
		return object;
	}
	
	public IChemFile newChemFile() {
		IChemFile object = new NNChemFile();
		return object;
	}

	public IChemModel newChemModel() {
		IChemModel object = new NNChemModel();
		return object;
	}
	
	public IChemObject newChemObject() {
		IChemObject object = new NNChemObject();
		return object;
	}
	
	public IChemSequence newChemSequence() {
		IChemSequence object = new NNChemSequence();   
		return object;
	}
	
    public ICrystal newCrystal() {
    	ICrystal object = new NNCrystal();
		return object;
    }
    
    public ICrystal newCrystal(IAtomContainer container) {
    	ICrystal object = new NNCrystal(container);
		return object;
    }
    
    public IElectronContainer newElectronContainer() {
    	IElectronContainer object = new NNElectronContainer();
		return object;
    }
    
    public IElement newElement() {
    	IElement object = new NNElement();
		return object;
    }

    public IElement newElement(String symbol) {
    	IElement object = new NNElement(symbol);
		return object;
    }

    public IElement newElement(String symbol, int atomicNumber) {
    	IElement object = new NNElement(symbol, atomicNumber);
		return object;
    }

	public IIsotope newIsotope(String elementSymbol) {
		IIsotope object = new NNIsotope(elementSymbol);
		return object;
	}
	
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		IIsotope object = new NNIsotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
		return object;
	}

	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		IIsotope object = new NNIsotope(atomicNumber, elementSymbol, exactMass, abundance);
		return object;
	}

	public IIsotope newIsotope(String elementSymbol, int massNumber) {
		IIsotope object = new NNIsotope(elementSymbol, massNumber);
		return object;
	}

    public ILonePair newLonePair() {
    	ILonePair object = new NNLonePair();
		return object;
    }

    public ILonePair newLonePair(IAtom atom) {
    	ILonePair object = new NNLonePair(atom);
		return object;
    }

    public IMapping newMapping(IChemObject objectOne, 
    		IChemObject objectTwo) {
    	IMapping object = new NNMapping(objectOne, objectTwo);
		return object;
	}
    
	public IMolecule newMolecule() {
		IMolecule object = new NNMolecule();
		return object;
	}

	public IMolecule newMolecule(int atomCount, int electronContainerCount) {
		IMolecule object = new NNMolecule(atomCount, electronContainerCount);
		return object;
	}

	public IMolecule newMolecule(IAtomContainer container) {
		IMolecule object = new NNMolecule(container);
		return object;
	}

	public IMonomer newMonomer () {
		IMonomer object = new NNMonomer();
		return object;
	}
	
	public IPolymer newPolymer() {
		IPolymer object = new NNPolymer();
		return object;
	}

    public IReaction newReaction() {
    	IReaction object = new NNReaction();	
		return object;
    }
	
	public IRing newRing() {
		IRing object = new NNRing();
		return object;
	}
	
	public IRing newRing(IAtomContainer container) {
		IRing object = new NNRing(container);
		return object;
	}
	
	public IRing newRing(int ringSize, String elementSymbol) {
		IRing object = new NNRing(ringSize, elementSymbol);
		return object;
	}

	public IRing newRing(int ringSize) {
		IRing object = new NNRing(ringSize);
		return object;
	}

	public IRingSet newRingSet() {
		IRingSet object = new NNRingSet();
		return object;
	}

	public IAtomContainerSet newSetOfAtomContainers() {
		IAtomContainerSet object = new NNSetOfAtomContainers();
		return object;
	}

	public IMoleculeSet newSetOfMolecules() {
		IMoleculeSet object = new NNSetOfMolecules();
		return object;
	}

	public IReactionSet newSetOfReactions() {
		IReactionSet object = new NNSetOfReactions();
		return object;
	}
	
    public ISingleElectron newSingleElectron() {
    	ISingleElectron object = new NNSingleElectron();
		return object;
    }
    
    public ISingleElectron newSingleElectron(IAtom atom) {
    	ISingleElectron object = new NNSingleElectron(atom);   
		return object;
    }

	public IStrand newStrand() {
		IStrand object = new NNStrand();
		return object;
	}

	public IPseudoAtom newPseudoAtom() {
		IPseudoAtom object = new NNPseudoAtom();
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label) {
		IPseudoAtom object = new NNPseudoAtom(label);
		return object;
	}

	public IPseudoAtom newPseudoAtom(IAtom atom) {
		IPseudoAtom object = new NNPseudoAtom(atom);
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label, Point3d point3d) {
		IPseudoAtom object = new NNPseudoAtom(label, point3d);
		return object;
	}

	public IPseudoAtom newPseudoAtom(String label, Point2d point2d) {
		IPseudoAtom object = new NNPseudoAtom(label, point2d);
		return object;
	}
}



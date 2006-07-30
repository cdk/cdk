/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
package org.openscience.cdk.interfaces;

/**
 * A helper class to instantiate a IChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    interfaces
 */
public interface IChemObjectBuilder {

    /**
     * Constructs an completely unset IAminoAcid.
     * 
     * @return IAminoAcid implementation defined for this IChemObjectBuilder
     */
	public IAminoAcid newAminoAcid();
	
    /**
     * Constructs an completely unset IAtom.
     * 
     * @return IAtom implementation defined for this IChemObjectBuilder
     */
	public IAtom newAtom();
	
    /**
     * Constructs an IAtom from a String containing an element symbol.
     *
     * @param   elementSymbol  The String describing the element for the Atom
     * @return                 IAtom implementation defined for this IChemObjectBuilder
     */
    public IAtom newAtom(String elementSymbol);
    
    /**
     * Constructs an IAtom from an Element and a Point2d.
     *
     * @param   elementSymbol   The Element
     * @param   point2d         The Point
     * @return                  IAtom implementation defined for this IChemObjectBuilder
     */
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d);

    /**
     * Constructs an IAtom from an Element and a Point3d.
     *
     * @param   elementSymbol   The symbol of the atom
     * @param   point3d         The 3D coordinates of the atom
     * @return                  IAtom implementation defined for this IChemObjectBuilder
     */
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d);

	/**
	 * Constructs an empty IAtomContainer.
	 * 
     * @return IAtomContainer implementation defined for this IChemObjectBuilder
	 */
	public IAtomContainer newAtomContainer();
    
	/**
	 * Constructs an empty IAtomContainer that will contain a certain number of
	 * atoms and electronContainers. It will set the starting array lengths to the
	 * defined values, but will not create any IAtom or IElectronContainer's.
	 *
	 * @param  atomCount               Number of atoms to be in this container
	 * @param  electronContainerCount  Number of electronContainers to be in this
	 *                                 container
     * @return                         IAtomContainer implementation defined for
     *                                 this IChemObjectBuilder
	 */
	public IAtomContainer newAtomContainer(int atomCount, int electronContainerCount);
    
	/**
	 * Constructs an IAtomContainer with a copy of the atoms and electronContainers
	 * of another IAtomContainer (A shallow copy, i.e., with the same objects as in
	 * the original IAtomContainer).
	 *
	 * @param  container  An AtomContainer to copy the atoms and electronContainers from
     * @return            IAtomContainer implementation defined for this IChemObjectBuilder
	 */
	public IAtomContainer newAtomContainer(IAtomContainer container);

    /**
     * Constructs an IAtomParity.
     *
     * @param centralAtom Atom for which the parity is defined
     * @param first       First Atom of four that define the stereochemistry
     * @param second      Second Atom of four that define the stereochemistry
     * @param third       Third Atom of four that define the stereochemistry
     * @param fourth      Fourth Atom of four that define the stereochemistry
     * @param parity      +1 or -1, defining the parity
     * @return            IAtomParity implementation defined for this IChemObjectBuilder
     */
    public IAtomParity newAtomParity(IAtom centralAtom, 
        IAtom first, IAtom second, IAtom third, IAtom fourth,
        int parity);

    /**
	 * Constructor for the IAtomType object.
     *
     * @param elementSymbol  Symbol of the atom
     * @return               IAtomType implementation defined for this IChemObjectBuilder
	 */
	public IAtomType newAtomType(String elementSymbol);

	/**
	 * Constructor for the IAtomType object.
	 *
	 * @param  identifier     An id for this atom type, like C3 for sp3 carbon
	 * @param  elementSymbol  The element symbol identifying the element to which this atom type applies
     * @return                IAtomType implementation defined for this IChemObjectBuilder
	 */
	public IAtomType newAtomType(String identifier, String elementSymbol);
	
	/**
	 * Contructs a new IBioPolymer to store the IStrands.
	 * 
     * @return  IBioPolymer implementation defined for this IChemObjectBuilder
	 */	
	public IBioPolymer newBioPolymer();
	
	/**
	 * Constructs an empty IBond.
	 * 
     * @return IBond implementation defined for this IChemObjectBuilder
	 */
	public IBond newBond();
	
	/**
	 * Constructs a IBond with a single bond order..
	 *
	 * @param  atom1  the first IAtom in the bond
	 * @param  atom2  the second IAtom in the bond
     * @return IBond  implementation defined for this IChemObjectBuilder
	 */
	public IBond newBond(IAtom atom1, IAtom atom2);
	
	/**
	 * Constructs a IBond with a given order.
	 *
	 * @param  atom1  the first IAtom in the bond
	 * @param  atom2  the second IAtom in the bond
	 * @param  order  the bond order
     * @return IBond  implementation defined for this IChemObjectBuilder
	 */
	public IBond newBond(IAtom atom1, IAtom atom2, double order);
	
	/**
	 * Constructs a IBond with a given order and stereo orientation from an array
	 * of atoms.
	 *
	 * @param  atom1   the first Atom in the bond
	 * @param  atom2   the second Atom in the bond
	 * @param  order   the bond order
	 * @param  stereo  a descriptor the stereochemical orientation of this bond
     * @return IBond   implementation defined for this IChemObjectBuilder
	 */
	public IBond newBond(IAtom atom1, IAtom atom2, double order, int stereo);

	/**
	 * Constructs an empty IChemFile.
	 * 
     * @return IChemFile implementation defined for this IChemObjectBuilder
	 */
	public IChemFile newChemFile();
	
	/**
	 * Constructs an new IChemModel with a null ISetOfMolecules.
	 * 
     * @return IChemModel implementation defined for this IChemObjectBuilder
	 */
	public IChemModel newChemModel();
	
	/**
	 * Constructs an new IChemObject.
	 * 
     * @return IChemObject implementation defined for this IChemObjectBuilder
	 */
	public IChemObject newChemObject();
	
	/**
	 * Constructs an empty IChemSequence.
	 * 
     * @return IChemSequence implementation defined for this IChemObjectBuilder
	 */
	public IChemSequence newChemSequence();   
	
    /**
     * Constructs a new ICrystal with zero length cell axis.
     * 
     * @return ICrystal implementation defined for this IChemObjectBuilder
     */
    public ICrystal newCrystal();
    
    /**
     * Constructs a new ICrystal with zero length cell axis
     * and adds the atoms in the IAtomContainer as cell content.
     *
     * @param container  the IAtomContainer providing the atoms and bonds
     * @return           ICrystal implementation defined for this IChemObjectBuilder
     */
    public ICrystal newCrystal(IAtomContainer container);

    /**
     * Constructs an empty IElectronContainer.
     * 
     * @return IElectronContainer implementation defined for this IChemObjectBuilder
     */
    public IElectronContainer newElectronContainer();

    /**
     * Constructs an empty IElement.
     * 
     * @return IElement implementation defined for this IChemObjectBuilder
     */
    public IElement newElement();

    /**
     * Constructs an IElement with a given element symbol.
     *
     * @param  symbol The element symbol that this element should have.
     * @return        IElement implementation defined for this IChemObjectBuilder
     */
    public IElement newElement(String symbol);

    /**
     * Constructs an IElement with a given element symbol, 
     * atomic number and atomic mass.
     *
     * @param   symbol       The element symbol of this element.
     * @param   atomicNumber The atomicNumber of this element.
     * @return               IElement implementation defined for this IChemObjectBuilder
     */
    public IElement newElement(String symbol, int atomicNumber);

	/**
	 * Constructor for the IIsotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for oxygen, etc.
     * @return                IIsotope implementation defined for this IChemObjectBuilder
	 */
	public IIsotope newIsotope(String elementSymbol);
	
	/**
	 * Constructor for the IIsotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope
	 * @param  elementSymbol  The element symbol, "O" for oxygen, etc.
	 * @param  massNumber     The atomic mass of the isotope, 16 for oxygen, e.g.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  abundance      The natural abundance of the isotope
     * @return                IIsotope implementation defined for this IChemObjectBuilder
	 */
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance);

	/**
	 * Constructor for the IIsotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope, 8 for oxygen
	 * @param  elementSymbol  The element symbol, "O" for oxygen, etc.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  abundance      The natural abundance of the isotope
     * @return                IIsotope implementation defined for this IChemObjectBuilder
	 */
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance);

	/**
	 * Constructor for the IIsotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for oxygen, etc.
	 * @param  massNumber     The atomic mass of the isotope, 16 for oxygen, e.g.
     * @return                IIsotope implementation defined for this IChemObjectBuilder
	 */
	public IIsotope newIsotope(String elementSymbol, int massNumber);
	
    /**
     * Constructs an unconnected ILonePair.
     * 
     * @return  ILonePair implementation defined for this IChemObjectBuilder
     */
    public ILonePair newLonePair();

    /**
     * Constructs an ILonePair on an IAtom.
     *
     * @param atom  IAtom to which this lone pair is connected
     * @return      ILonePair implementation defined for this IChemObjectBuilder
     */
    public ILonePair newLonePair(IAtom atom);
	
    /**
	 * Creates a directional IMapping between IChemObject's.
	 * 
	 * @param objectOne object which is being mapped 
	 * @param objectTwo object to which is being mapped
     * @return          IMapping implementation defined for this IChemObjectBuilder
	 */
	public IMapping newMapping(IChemObject objectOne, IChemObject objectTwo);
    
	/**
	 * Creates an IMolecule without any IAtoms and IBonds.
	 * 
     * @return IMolecule implementation defined for this IChemObjectBuilder
	 */
	public IMolecule newMolecule();

	/**
	 * Constructor for the IMolecule object. The parameters define the
     * initial capacity of the arrays.
	 *
	 * @param  atomCount               init capacity of IAtom array
	 * @param  electronContainerCount  init capacity of IElectronContainer array
     * @return                         IMolecule implementation defined for this IChemObjectBuilder
	 */
	public IMolecule newMolecule(int atomCount, int electronContainerCount);

	/**
	 * Constructs an IMolecule with
	 * a shallow copy of the atoms and bonds of an IAtomContainer.
	 *
	 * @param   container  An IMolecule to copy the atoms and bonds from
     * @return             IMolecule implementation defined for this IChemObjectBuilder
	 */
	public IMolecule newMolecule(IAtomContainer container);
	
	/**
	 * Contructs a new IMonomer.
	 * 
     * @return IMonomer implementation defined for this IChemObjectBuilder
	 */	
	public IMonomer newMonomer ();
	
	/**
	 * Contructs a new IPolymer to store the IMonomers.
	 * 
     * @return IPolymer implementation defined for this IChemObjectBuilder
	 */	
	public IPolymer newPolymer();

    /**
     * Constructs an empty, forward IReaction.
     * 
     * @return IReaction implementation defined for this IChemObjectBuilder
     */
    public IReaction newReaction();
    
	/**
	 * Constructs an empty IRing.
	 * 
     * @return IRing implementation defined for this IChemObjectBuilder
	 */
	public IRing newRing();
	
	/**
	 * Constructs a IRing from an IAtomContainer.
	 * 
	 * @param  container IAtomContainer to create the IRing from
     * @return           IRing implementation defined for this IChemObjectBuilder
	 */
	public IRing newRing(IAtomContainer container);
	
	/**
	 * Constructs a ring that will have a certain number of atoms of the given elements.
	 *
	 * @param  ringSize      The number of atoms and bonds the ring will have
	 * @param  elementSymbol The element of the atoms the ring will have
     * @return               IRing implementation defined for this IChemObjectBuilder
	 */
	public IRing newRing(int ringSize, String elementSymbol);

	/**
	 * Constructs an empty IRing that will have a certain size.
	 *
	 * @param  ringSize The size (number of atoms) the ring will have
     * @return          IRing implementation defined for this IChemObjectBuilder
	 */
	public IRing newRing(int ringSize);
		
	/**
	 * Constructs an empty IRingSet.
	 * 
     * @return IRingSet implementation defined for this IChemObjectBuilder
	 */
	public IRingSet newRingSet();
	
	/**  
	 * Constructs an empty ISetOfAtomContainers.
	 * 
     * @return ISetOfAtomContainers implementation defined for this IChemObjectBuilder
	 */
	public ISetOfAtomContainers newSetOfAtomContainers();
	
	/**  
	 * Constructs an empty ISetOfMolecules.
	 * 
     * @return ISetOfMolecules implementation defined for this IChemObjectBuilder
	 */
	public IMoleculeSet newSetOfMolecules();
	
	/**
	 * Constructs an empty ISetOfReactions.
	 * 
     * @return ISetOfReactions implementation defined for this IChemObjectBuilder
	 */
	public IReactionSet newSetOfReactions();
	
    /**
     * Constructs an single electron orbital with an associated IAtom.
     * 
     * @return ISingleElectron implementation defined for this IChemObjectBuilder
     */
    public ISingleElectron newSingleElectron();
    
    /**
     * Constructs an single electron orbital on an IAtom.
     *
     * @param atom The atom to which the single electron belongs.
     * @return     ISingleElectron implementation defined for this IChemObjectBuilder
     */
    public ISingleElectron newSingleElectron(IAtom atom);   

	/**
	 * Contructs a new IStrand.
	 * 
     * @return IStrand implementation defined for this IChemObjectBuilder
	 */	
	public IStrand newStrand();

    /**
     * Constructs an empty IPseudoAtom.
     * 
     * @return IPseudoAtom implementation defined for this IChemObjectBuilder
     */
    public IPseudoAtom newPseudoAtom();
    
    /**
     * Constructs an IPseudoAtom from a label.
     *
     * @param   label  The String describing the PseudoAtom
     * @return IPseudoAtom implementation defined for this IChemObjectBuilder
     */
    public IPseudoAtom newPseudoAtom(String label);

    /**
     * Constructs an IPseudoAtom from an existing IAtom object.
     *
     * @param   atom  IAtom from which the IPseudoAtom is constructed
     * @return        IPseudoAtom implementation defined for this IChemObjectBuilder
     */
    public IPseudoAtom newPseudoAtom(IAtom atom);

    /**
     * Constructs an IPseudoAtom from a label and a Point3d.
     *
     * @param   label   The String describing the IPseudoAtom
     * @param   point3d The 3D coordinates of the IPseudoAtom
     * @return          IPseudoAtom implementation defined for this IChemObjectBuilder
     */
    public IPseudoAtom newPseudoAtom(String label, javax.vecmath.Point3d point3d);

    /**
     * Constructs an IPseudoAtom from a label and a Point2d.
     *
     * @param   label   The String describing the IPseudoAtom
     * @param   point2d The 2D coordinates of the IPseudoAtom
     * @return          IPseudoAtom implementation defined for this IChemObjectBuilder
     */
    public IPseudoAtom newPseudoAtom(String label, javax.vecmath.Point2d point2d);
		
}



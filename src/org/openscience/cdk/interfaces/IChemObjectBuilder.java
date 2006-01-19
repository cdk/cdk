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
package org.openscience.cdk.interfaces;

/**
 * A helper class to instantiate a IChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    interfaces
 */
public interface IChemObjectBuilder {

    /**
     * Constructs an completely unset Atom.
     */
	public IAminoAcid newAminoAcid();
	
    /**
     * Constructs an completely unset Atom.
     */
	public IAtom newAtom();
	
    /**
     * Constructs an Atom from a String containing an element symbol.
     *
     * @param   elementSymbol  The String describing the element for the Atom
     */
    public IAtom newAtom(String elementSymbol);
    
    /**
     * Constructs an Atom from an Element and a Point2d.
     *
     * @param   elementSymbol   The Element
     * @param   point2d         The Point
     */
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point2d point2d);

    /**
     * Constructs an Atom from an Element and a Point3d.
     *
     * @param   elementSymbol   The symbol of the atom
     * @param   point3d         The 3D coordinates of the atom
     */
    public IAtom newAtom(String elementSymbol, javax.vecmath.Point3d point3d);

	/**
	 *  Constructs an empty AtomContainer.
	 */
	public IAtomContainer newAtomContainer();
    
	/**
	 * Constructs an empty AtomContainer that will contain a certain number of
	 * atoms and electronContainers. It will set the starting array lengths to the
	 * defined values, but will not create any Atom or ElectronContainer's.
	 *
	 * @param  atomCount               Number of atoms to be in this container
	 * @param  electronContainerCount  Number of electronContainers to be in this
	 *                                 container
	 */
	public IAtomContainer newAtomContainer(int atomCount, int electronContainerCount);
    
	/**
	 * Constructs an AtomContainer with a copy of the atoms and electronContainers
	 * of another AtomContainer (A shallow copy, i.e., with the same objects as in
	 * the original AtomContainer).
	 *
	 * @param  container  An AtomContainer to copy the atoms and electronContainers from
	 */
	public IAtomContainer newAtomContainer(IAtomContainer container);

    /**
     * Constructs an AtomParity.
     *
     * @param centralAtom Atom for which the parity is defined
     * @param first       First Atom of four that define the stereochemistry
     * @param second      Second Atom of four that define the stereochemistry
     * @param third       Third Atom of four that define the stereochemistry
     * @param fourth      Fourth Atom of four that define the stereochemistry
     * @param parity      +1 or -1, defining the parity
     */
    public IAtomParity newAtomParity(IAtom centralAtom, 
        IAtom first, IAtom second, IAtom third, IAtom fourth,
        int parity);

    /**
	 * Constructor for the AtomType object.
     *
     * @param elementSymbol  Symbol of the atom
	 */
	public IAtomType newAtomType(String elementSymbol);

	/**
	 * Constructor for the AtomType object.
	 *
	 * @param  identifier     An id for this atom type, like C3 for sp3 carbon
	 * @param  elementSymbol  The element symbol identifying the element to which this atom type applies
	 */
	public IAtomType newAtomType(String identifier, String elementSymbol);
	
	/**
	 * Contructs a new Polymer to store the Strands.
	 */	
	public IBioPolymer newBioPolymer();
	
	/**
	 * Constructs an empty bond.
	 */
	public IBond newBond();
	
	/**
	 * Constructs a bond with a single bond order..
	 *
	 * @param  atom1  the first Atom in the bond
	 * @param  atom2  the second Atom in the bond
	 */
	public IBond newBond(IAtom atom1, IAtom atom2);
	
	/**
	 * Constructs a bond with a given order.
	 *
	 * @param  atom1  the first Atom in the bond
	 * @param  atom2  the second Atom in the bond
	 * @param  order  the bond order
	 */
	public IBond newBond(IAtom atom1, IAtom atom2, double order);
	
	/**
	 * Constructs a bond with a given order and stereo orientation from an array
	 * of atoms.
	 *
	 * @param  atom1   the first Atom in the bond
	 * @param  atom2   the second Atom in the bond
	 * @param  order   the bond order
	 * @param  stereo  a descriptor the stereochemical orientation of this bond
	 */
	public IBond newBond(IAtom atom1, IAtom atom2, double order, int stereo);

	/**
	 * Constructs an empty ChemFile.
	 */
	public IChemFile newChemFile();
	
	/**
	 * Constructs an new ChemModel with a null setOfMolecules.
	 */
	public IChemModel newChemModel();
	
	/**
	 * Constructs an new IChemObject with a null setOfMolecules.
	 */
	public IChemObject newChemObject();
	
	/**
	 * Constructs an empty ChemSequence.
	 */
	public IChemSequence newChemSequence();   
	
    /**
     * Constructs a new crystal with zero length cell axis.
     */
    public ICrystal newCrystal();
    
    /**
     * Constructs a new crystal with zero length cell axis
     * and adds the atoms in the AtomContainer as cell content.
     *
     * @param container  the AtomContainer providing the atoms and bonds
     */
    public ICrystal newCrystal(IAtomContainer container);

    /**
     * Constructs an empty ElectronContainer.
     */
    public IElectronContainer newElectronContainer();

    /**
     * Constructs an empty Element.
     */
    public IElement newElement();

    /**
     * Constructs an Element with a given 
     * element symbol.
     *
     * @param   symbol The element symbol that this element should have.  
     */
    public IElement newElement(String symbol);

    /**
     * Constructs an Element with a given element symbol, 
     * atomic number and atomic mass.
     *
     * @param   symbol  The element symbol of this element.
     * @param   atomicNumber  The atomicNumber of this element.
     */
    public IElement newElement(String symbol, int atomicNumber);

	/**
	 * Constructor for the Isotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 */
	public IIsotope newIsotope(String elementSymbol);
	
	/**
	 * Constructor for the Isotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  massNumber     The atomic mass of the isotope, 16 for Oxygen, e.g.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  abundance      The natural abundance of the isotope
	 */
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance);

	/**
	 * Constructor for the Isotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope, 8 for Oxygen
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  abundance      The natural abundance of the isotope
	 */
	public IIsotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance);

	/**
	 * Constructor for the Isotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  massNumber     The atomic mass of the isotope, 16 for Oxygen, e.g.
	 */
	public IIsotope newIsotope(String elementSymbol, int massNumber);
	
    /**
     * Constructs an unconnected lone pair.
     */
    public ILonePair newLonePair();

    /**
     * Constructs an lone pair on an Atom.
     *
     * @param atom  Atom to which this lone pair is connected
     */
    public ILonePair newLonePair(IAtom atom);
	
	/**
	 * Creates an Molecule without Atoms and Bonds.
	 */
	public IMolecule newMolecule();

	/**
	 * Constructor for the Molecule object. The parameters define the
     * initial capacity of the arrays.
	 *
	 * @param  atomCount               init capacity of Atom array
	 * @param  electronContainerCount  init capacity of Bond array
	 */
	public IMolecule newMolecule(int atomCount, int electronContainerCount);

	/**
	 * Constructs a Molecule with
	 * a shallow copy of the atoms and bonds of an AtomContainer.
	 *
	 * @param   container  An Molecule to copy the atoms and bonds from
	 */
	public IMolecule newMolecule(IAtomContainer container);
	
	/**
	 * Contructs a new Monomer.
	 */	
	public IMonomer newMonomer ();
	
	/**
	 * Contructs a new Polymer to store the Monomers.
	 */	
	public IPolymer newPolymer();

    /**
     * Constructs an empty, forward reaction.
     */
    public IReaction newReaction();
    
	/**
	 * Constructs an empty ring.
	 */
	public IRing newRing();
	
	/**
	 * Constructs a ring from an AtomContainer.
	 */
	public IRing newRing(IAtomContainer container);
	
	/**
	 * Constructs a ring that will have a certain number of atoms of the given elements.
	 *
	 * @param   ringSize   The number of atoms and bonds the ring will have
	 * @param   elementSymbol   The element of the atoms the ring will have
	 */
	public IRing newRing(int ringSize, String elementSymbol);
	
		
	/**
	 * Constructs an empty ring that will have a certain size.
	 *
	 * @param   ringSize  The size (number of atoms) the ring will have
	 */
	public IRing newRing(int ringSize);
		
	/**
	 * Constructs an empty RingSet.
	 */
	public IRingSet newRingSet();
	
	/**  
	 * Constructs an empty SetOfAtomContainers.
	 */
	public ISetOfAtomContainers newSetOfAtomContainers();
	
	/**  
	 * Constructs an empty SetOfMolecules.
	 */
	public ISetOfMolecules newSetOfMolecules();
	
	/**
	 * Constructs an empty SetOfReactions.
	 */
	public ISetOfReactions newSetOfReactions();
	
    /**
     * Constructs an single electron orbital with an associated Atom.
     */
    public ISingleElectron newSingleElectron();
    
    /**
     * Constructs an single electron orbital on an Atom.
     *
     * @param atom The atom to which the single electron belongs.
     */
    public ISingleElectron newSingleElectron(IAtom atom);   

	/**
	 * Contructs a new Strand.
	 */	
	public IStrand newStrand();

    /**
     * Constructs an empty PseudoAtom.
     */
    public IPseudoAtom newPseudoAtom();
    
    /**
     * Constructs an PseudoAtom from a label.
     *
     * @param   label  The String describing the PseudoAtom
     */
    public IPseudoAtom newPseudoAtom(String label);

    /**
     * Constructs an PseudoAtom from an existing Atom object.
     *
     * @param   atom  Atom from which the PseudoAtom is constructed
     */
    public IPseudoAtom newPseudoAtom(IAtom atom);

    /**
     * Constructs an PseudoAtom from a label and a Point3d.
     *
     * @param   label   The String describing the PseudoAtom
     * @param   point3d The 3D coordinates of the atom
     */
    public IPseudoAtom newPseudoAtom(String label, javax.vecmath.Point3d point3d);

    /**
     * Constructs an PseudoAtom from a label and a Point2d.
     *
     * @param   label   The String describing the PseudoAtom
     * @param   point2d The 2D coordinates of the atom
     */
    public IPseudoAtom newPseudoAtom(String label, javax.vecmath.Point2d point2d);
		
}



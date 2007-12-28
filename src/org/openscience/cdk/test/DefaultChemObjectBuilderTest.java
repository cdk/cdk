/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
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
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test-data
 */
public class DefaultChemObjectBuilderTest extends CDKTestCase {

	protected org.openscience.cdk.ChemObject rootObject;
	
    public DefaultChemObjectBuilderTest(String name) {
        super(name);
    }

    public void setUp() {
        rootObject = new org.openscience.cdk.ChemObject();
    }

    public static Test suite() {
        return new TestSuite(DefaultChemObjectBuilderTest.class);
    }

    public void testGetInstance() {
    	Object builder = DefaultChemObjectBuilder.getInstance();
    	assertNotNull(builder);
    	assertTrue(builder instanceof IChemObjectBuilder);
        assertTrue(builder instanceof DefaultChemObjectBuilder);
    }
    
	public void testNewAminoAcid() {
		Object object = rootObject.getBuilder().newAminoAcid();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAminoAcid);
	}
	 
	public void testNewAtom() {
		Object object = rootObject.getBuilder().newAtom();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtom);
	}
	 
	public void testNewAtom_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newAtom(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtom);
	}
	 
	public void testNewAtom_String() {
		Object object = rootObject.getBuilder().newAtom("C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtom);
	}
	
	public void testNewAtom_String_Point2d() {
		Object object = rootObject.getBuilder().newAtom("C", new Point2d(1.0, 2.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtom);
	}
	
	public void testNewAtom_String_Point3d() {
		Object object = rootObject.getBuilder().newAtom("C", new Point3d(1.0, 2.0, 3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtom);
	}
	
	public void testNewAtomContainer() {
		Object object = rootObject.getBuilder().newAtomContainer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomContainer);
	}
	
	public void testNewAtomContainer_int_int_int_int() {
		Object object = rootObject.getBuilder().newAtomContainer(10,10,0,0);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomContainer);
	}
	
	public void testNewAtomContainer_IAtomContainer() {
		Object object = rootObject.getBuilder().newAtomContainer(rootObject.getBuilder().newAtomContainer());
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomContainer);
	}
	
	public void testNewAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
		Object object = rootObject.getBuilder().newAtomParity(
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				1
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomParity);
	}
	
	public void testNewAtomType_String() {
		Object object = rootObject.getBuilder().newAtomType("Carom");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomType);
	}
	
	public void testNewAtomType_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newAtomType(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomType);
	}
	
	public void testNewAtomType_String_String() {
		Object object = rootObject.getBuilder().newAtomType("Carom");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomType);
	}
	
	public void testNewBioPolymer() {
		Object object = rootObject.getBuilder().newBioPolymer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IBioPolymer);
	}
	
	public void testNewBond() {
		Object object = rootObject.getBuilder().newBond();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IBond);
	}
	
	public void testNewBond_IAtom_IAtom() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IBond);
	}
	
	public void testNewBond_IAtom_IAtom_IBond_Order() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom(),
			IBond.Order.SINGLE
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IBond);
	}
	
	public void testNewBond_IAtom_IAtom_IBond_Order_int() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom(),
			IBond.Order.SINGLE, 1
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IBond);
	}
	
	public void testNewChemFile() {
		Object object = rootObject.getBuilder().newChemFile();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IChemFile);
	}
	
	public void testNewChemModel() {
		Object object = rootObject.getBuilder().newChemModel();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IChemModel);
	}

	public void testNewChemObject() {
		Object object = rootObject.getBuilder().newChemObject();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
	}

	public void testNewChemObject_IChemObject() {
		IChemObject chemObject = rootObject.getBuilder().newChemObject();
		Object object = rootObject.getBuilder().newChemObject(chemObject);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
	}

	public void testNewChemSequence() {
		Object object = rootObject.getBuilder().newChemSequence();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IChemSequence);
	}
	
	public void testNewCrystal() {
		Object object = rootObject.getBuilder().newCrystal();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ICrystal);
	}
	
	public void testNewCrystal_IAtomContainer() {
		Object object = rootObject.getBuilder().newCrystal(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ICrystal);
	}
	
	public void testNewElectronContainer() {
		Object object = rootObject.getBuilder().newElectronContainer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IElectronContainer);
	}

	public void testNewElement() {
		Object object = rootObject.getBuilder().newElement();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IElement);
	}
	
	public void testNewElement_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newElement(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IElement);
	}
	
	public void testNewElement_String() {
		Object object = rootObject.getBuilder().newElement("C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IElement);
	}
	
	public void testNewElement_String_int() {
		Object object = rootObject.getBuilder().newElement("C", 6);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IElement);
	}

	public void testNewIsotope_int_String_double_double() {
		Object object = rootObject.getBuilder().newIsotope(
			12, "C", 12.001, 100.0
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IIsotope);
	}
	
	public void testNewIsotope_int_String_int_double_double() {
		Object object = rootObject.getBuilder().newIsotope(
			12, "C", 6, 12.001, 100.0
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof IIsotope);
	}
	
	public void testNewIsotope_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newIsotope(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof IIsotope);
	}
	
	public void testNewIsotope_String() {
		Object object = rootObject.getBuilder().newIsotope("N");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof IIsotope);
	}
	
	public void testNewIsotope_String_int() {
		Object object = rootObject.getBuilder().newIsotope("N", 5);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof IIsotope);
	}

	public void testNewLonePair() {
		Object object = rootObject.getBuilder().newLonePair();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ILonePair);
	}	

	public void testNewLonePair_IAtom() {
		Object object = rootObject.getBuilder().newLonePair(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ILonePair);
	}	

    public void testNewMapping_IChemObject_IChemObject() {
		Object object = rootObject.getBuilder().newMapping(rootObject.getBuilder().newAtom(), 
                                                           rootObject.getBuilder().newAtom());
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.Mapping);

		assertTrue(object instanceof org.openscience.cdk.interfaces.IMapping);
	}
    
	public void testNewMolecule() {
		Object object = rootObject.getBuilder().newMolecule();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IMolecule);
	}	

	public void testNewMolecule_int_int_int_int() {
		Object object = rootObject.getBuilder().newMolecule(5,5,1,1);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IMolecule);
	}	

	public void testNewMolecule_IAtomContainer() {
		Object object = rootObject.getBuilder().newMolecule(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IMolecule);
	}	

	public void testNewMonomer() {
		Object object = rootObject.getBuilder().newMonomer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IMonomer);
	}	

	public void testNewPolymer() {
		Object object = rootObject.getBuilder().newPolymer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPolymer);
	}	
	
	public void testNewPDBAtom_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newPDBAtom(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBAtom);
	}	
	
	public void testNewPDBAtom_String() {
		Object object = rootObject.getBuilder().newPDBAtom("C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBAtom);
	}	
	
	public void testNewPDBAtom_String_Point3D() {
		Object object = rootObject.getBuilder().newPDBAtom("C", new Point3d(1.0, 2.0, 3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBAtom);
	}
	
	public void testNewPDBPolymer() {
		Object object = rootObject.getBuilder().newPDBPolymer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBPolymer);
	}
	
	public void testNewPDBStructure() {
		Object object = rootObject.getBuilder().newPDBStructure();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBStructure);
	}
	
	public void testNewPDBMonomer() {
		Object object = rootObject.getBuilder().newPDBMonomer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBMonomer);
	}

	
	public void testNewPseudoAtom() {
		Object object = rootObject.getBuilder().newPseudoAtom();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}

	public void testNewPseudoAtom_IElement() {
		IElement element = rootObject.getBuilder().newElement();
		Object object = rootObject.getBuilder().newPseudoAtom(element);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}	

	public void testNewPseudoAtom_IAtom() {
		Object object = rootObject.getBuilder().newPseudoAtom(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}	

	public void testNewPseudoAtom_String() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glu178");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}	

	public void testNewPseudoAtom_String_Point2d() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glue178", new Point2d(1.0,2.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}	

	public void testNewPseudoAtom_String_Point3d() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glue178", new Point3d(1.0,2.0,3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPseudoAtom);
	}	

	public void testNewPDBAtom_String_Point3d() {
		Object object = rootObject.getBuilder().newPDBAtom("CA", new Point3d(1.0,2.0,3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IPDBAtom);
	}	

	public void testNewReaction() {
		Object object = rootObject.getBuilder().newReaction();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IReaction);
	}

	public void testNewRing() {
		Object object = rootObject.getBuilder().newRing();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IRing);
	}	

	public void testNewRing_int() {
		Object object = rootObject.getBuilder().newRing(5);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IRing);
	}	

	public void testNewRing_int_String() {
		Object object = rootObject.getBuilder().newRing(5,"C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IRing);
	}	

	public void testNewRing_IAtomContainer() {
		Object object = rootObject.getBuilder().newRing(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IRing);
	}	

	public void testNewRingSet() {
		Object object = rootObject.getBuilder().newRingSet();
		assertNotNull(object);
        // FIXME: apparently RingSet does not extend IChemObject !
		// assertTrue(object instanceof org.openscience.cdk.interfaces.IChemObject);

		assertTrue(object instanceof IRingSet);
	}

	public void testNewAtomContainerSet() {
		Object object = rootObject.getBuilder().newAtomContainerSet();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IAtomContainerSet);
	}

	public void testNewMoleculeSet() {
		Object object = rootObject.getBuilder().newMoleculeSet();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IMoleculeSet);
	}

	public void testNewReactionSet() {
		Object object = rootObject.getBuilder().newReactionSet();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IReactionSet);
	}

	public void testNewSingleElectron() {
		Object object = rootObject.getBuilder().newSingleElectron();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ISingleElectron);
	}

	public void testNewSingleElectron_IAtom() {
		Object object = rootObject.getBuilder().newSingleElectron(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ISingleElectron);
	}
	
	public void testNewStrand() {
		Object object = rootObject.getBuilder().newStrand();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof IStrand);
	}
	
}

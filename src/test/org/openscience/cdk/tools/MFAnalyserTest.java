/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.tools;

import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Element;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.NewCDKTestCase;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * @cdk.module test-formula
 */
public class MFAnalyserTest extends NewCDKTestCase {
	static Molecule molecule;
	
	@BeforeClass
	public static void setUp()
	{
		molecule = MoleculeFactory.makeAlphaPinene();
	}


	@Test public void testMFAnalyser_String_IAtomContainer()	{
		Assert.assertNotNull(new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer()));
	}

	@Test public void testMFAnalyser_IAtomContainer()	{
		Assert.assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer()));
	}
	
	@Test public void testMFAnalyser_IAtomContainer_boolean()	{
		Assert.assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer(), true));
		Assert.assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer(), false));
	}
	
	@Test public void testGetMolecularFormula()	{
		MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
		IAtomContainer ac = mfa.getAtomContainer();
		MFAnalyser mfa2 = new MFAnalyser(ac);
		String mf = mfa2.getMolecularFormula();
		Assert.assertEquals("C10H16", mf);
	}
	
	@Test public void testGetElements()	{
		MFAnalyser mfa = new MFAnalyser("C10H16", new NNAtomContainer());
		Assert.assertEquals(2, mfa.getElements().size());
		mfa = new MFAnalyser("C10H19N", new NNAtomContainer());
		Assert.assertEquals(3, mfa.getElements().size());
	}
	
	@Test public void testGetDBE() throws Exception{
        MFAnalyser mfa = new MFAnalyser("C10H22", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(0, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H16", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(3, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H16O", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(3, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(2, (int)mfa.getDBE());
	}
	
	@Test public void testGenerateElementFormula_IMolecule_arrayString() {
		MFAnalyser mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals("C10N1H19", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "C", "N", "H" })
		);
		Assert.assertEquals("C10H19N1", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "C", "H", "N" })
		);
		Assert.assertEquals("N1H19C10", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "N", "H", "C" })
		);
	}
	
	@Test public void testAnalyseAtomContainer_IAtomContainer() {
		MFAnalyser mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals("C10H19N", mfa.analyseAtomContainer(
			new Molecule(mfa.getAtomContainer()))
		);
	}
	
	@Test public void testElements() throws Exception{
        MFAnalyser mfa = new MFAnalyser("C10H22", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(2, (int)mfa.getElements().size());

        mfa = new MFAnalyser("C10H16O", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		Assert.assertEquals(3, (int)mfa.getElements().size());
	}
	
    @Test public void testGetAtomContainer() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        IAtomContainer ac = mfa.getAtomContainer();
        Assert.assertEquals(26, ac.getAtomCount());
    }
    
    @Test public void testGetElementCount() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(2, mfa.getElementCount());
    }
        	
    @Test public void testGetElementCount2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(3, mfa.getElementCount());
    }
    
    @Test public void testGetAtomCount_String() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(10, mfa.getAtomCount("C"));
        Assert.assertEquals(16, mfa.getAtomCount("H"));
    }
    
    @Test public void testGetAtomCount_String2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(1, mfa.getAtomCount("C"));
        Assert.assertEquals(1, mfa.getAtomCount("O"));
        Assert.assertEquals(4, mfa.getAtomCount("H"));
    }
    
    @Test public void testGetHeavyAtoms() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(10, mfa.getHeavyAtoms().size());
    }
    
    @Test public void testGetHeavyAtoms2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        Assert.assertEquals(2, mfa.getHeavyAtoms().size());
    }
    
    /**
     * Test removeHydrogensPreserveMultiplyBonded for B2H6, which contains two multiply bonded H.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test public void testRemoveHydrogensPreserveMultiplyBonded() throws IOException, ClassNotFoundException, CDKException
    {
    	IAtomContainer borane = new Molecule();
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("B"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("B"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addBond(0,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(2,3,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(2,4,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(3,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(4,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(5,6,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(5,7,CDKConstants.BONDORDER_SINGLE);
        IAtomContainer ac = new MFAnalyser(borane).removeHydrogensPreserveMultiplyBonded();

        // Should be two connected Bs with H-count == 2 and two explicit Hs.
        Assert.assertEquals("incorrect atom count", 4, ac.getAtomCount());
        Assert.assertEquals("incorrect bond count", 4, ac.getBondCount());

        int b = 0;
        int h = 0;
        for (int i = 0;
                i < ac.getAtomCount();
                i++)
        {
            final org.openscience.cdk.interfaces.IAtom atom = ac.getAtom(i);
            String sym = atom.getSymbol();
            if (sym.equals("B"))
            {
                // Each B has two explicit and two implicit H.
                b++;
                Assert.assertEquals("incorrect hydrogen count", 2, atom.getHydrogenCount());
                java.util.List nbs = ac.getConnectedAtomsList(atom);
                Assert.assertEquals("incorrect connected count", 2, nbs.size());
                Assert.assertEquals("incorrect bond", "H", ((IAtom)nbs.get(0)).getSymbol());
                Assert.assertEquals("incorrect bond", "H", ((IAtom)nbs.get(1)).getSymbol());
            }
            else if (sym.equals("H"))
            {
                h++;
            }
        }
        Assert.assertEquals("incorrect no. Bs", 2, b);
        Assert.assertEquals("incorrect no. Hs", 2, h);
    }


    @Test
    public void testGetFormulaHashtable() {
	MFAnalyser mfa=new MFAnalyser(molecule);
	Map formula = mfa.getFormulaHashtable();
	Assert.assertEquals(10, formula.get("C"));
    }
    
    @Test public void testGetMass() throws Exception{
        MFAnalyser mfa = new MFAnalyser(molecule);
        Assert.assertEquals((float)120, mfa.getMass(), .1);
        Assert.assertEquals((float)120.11038, mfa.getNaturalMass(), .1);
    }
    
    @Test public void testGetNaturalMass_IElement() throws Exception {
        Assert.assertEquals(1.0079760, MFAnalyser.getNaturalMass(new Element("H")), 0.1);
    }
    
    @Test public void testGetNaturalMass() throws Exception {
    	MFAnalyser mfa = new MFAnalyser("C8H10O2Cl2", new Molecule());
    	Assert.assertEquals((float)209.0692 , mfa.getNaturalMass() ,.001);
    }
    
    @Test public void testGetHTMLMolecularFormulaWithCharge() {
    	org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(0);
        MFAnalyser mfa = new MFAnalyser(molecule);
        Assert.assertEquals("C<sub>10</sub>", mfa.getHTMLMolecularFormulaWithCharge());
        atom.setFormalCharge(atom.getFormalCharge() + 1);
        Assert.assertEquals("C<sub>10</sub><sup>1+</sup>", mfa.getHTMLMolecularFormulaWithCharge());
        atom.setFormalCharge(atom.getFormalCharge() - 2);
        Assert.assertEquals("C<sub>10</sub><sup>1-</sup>", mfa.getHTMLMolecularFormulaWithCharge());
    }
    
    @Test public void testGetHTMLMolecularFormula() {
    	MFAnalyser mfa = new MFAnalyser("C8H10O2Cl2", new Molecule());
        Assert.assertEquals("C<sub>8</sub>H<sub>10</sub>O<sub>2</sub>Cl<sub>2</sub>", mfa.getHTMLMolecularFormula());
    }
    
    /**
     * @cdk.bug 1595430
     * @throws Exception if an invalid SMILES is used
     */
    @Test public void test1595430_2() throws Exception {
		String smile="CN(CC2=CC=CO2)C1=CC=CC=C1";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		addExplicitHydrogens(mol);
		MFAnalyser mfa=new MFAnalyser(mol);
		Assert.assertEquals((float)187.2382 , mfa.getNaturalMass() ,.001);
		Assert.assertEquals("C12H13NO",mfa.getMolecularFormula());
    }
}


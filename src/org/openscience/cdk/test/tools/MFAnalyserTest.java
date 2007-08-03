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

import junit.framework.Test;
import junit.framework.TestSuite;

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
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.MFAnalyser;

/**
 * @cdk.module test-standard
 */
public class MFAnalyserTest extends CDKTestCase {
	Molecule molecule;
	
	public MFAnalyserTest(String name)
	{
		super(name);
	}

	public void setUp()
	{
		molecule = MoleculeFactory.makeAlphaPinene();
	}

	public static Test suite() 
	{
		return new TestSuite(MFAnalyserTest.class);
	}

	public void testMFAnalyser_String_IAtomContainer()	{
		assertNotNull(new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer()));
	}

	public void testMFAnalyser_IAtomContainer()	{
		assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer()));
	}
	
	public void testMFAnalyser_IAtomContainer_boolean()	{
		assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer(), true));
		assertNotNull(new MFAnalyser(new org.openscience.cdk.AtomContainer(), false));
	}
	
	public void testGetMolecularFormula()	{
		MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
		IAtomContainer ac = mfa.getAtomContainer();
		MFAnalyser mfa2 = new MFAnalyser(ac);
		String mf = mfa2.getMolecularFormula();
		assertEquals("C10H16", mf);
	}
	
	public void testGetElements()	{
		MFAnalyser mfa = new MFAnalyser("C10H16", new NNAtomContainer());
		assertEquals(2, mfa.getElements().size());
		mfa = new MFAnalyser("C10H19N", new NNAtomContainer());
		assertEquals(3, mfa.getElements().size());
	}
	
	public void testGetDBE() throws Exception{
        MFAnalyser mfa = new MFAnalyser("C10H22", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(0, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H16", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(3, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H16O", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(3, (int)mfa.getDBE());

        mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(2, (int)mfa.getDBE());
	}
	
	public void testGenerateElementFormula_IMolecule_arrayString() {
		MFAnalyser mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals("C10N1H19", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "C", "N", "H" })
		);
		assertEquals("C10H19N1", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "C", "H", "N" })
		);
		assertEquals("N1H19C10", MFAnalyser.generateElementFormula(
			new Molecule(mfa.getAtomContainer()), new String[] { "N", "H", "C" })
		);
	}
	
	public void testAnalyseAtomContainer_IAtomContainer() {
		MFAnalyser mfa = new MFAnalyser("C10H19N", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals("C10H19N", mfa.analyseAtomContainer(
			new Molecule(mfa.getAtomContainer()))
		);
	}
	
	public void testElements() throws Exception{
        MFAnalyser mfa = new MFAnalyser("C10H22", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(2, (int)mfa.getElements().size());

        mfa = new MFAnalyser("C10H16O", DefaultChemObjectBuilder.getInstance().newAtomContainer());
		assertEquals(3, (int)mfa.getElements().size());
	}
	
    public void testGetAtomContainer() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        IAtomContainer ac = mfa.getAtomContainer();
        assertEquals(26, ac.getAtomCount());        
    }
    
    public void testGetElementCount() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        assertEquals(2, mfa.getElementCount());        
    }
        	
    public void testGetElementCount2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        assertEquals(3, mfa.getElementCount());        
    }
    
    public void testGetAtomCount_String() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        assertEquals(10, mfa.getAtomCount("C"));        
        assertEquals(16, mfa.getAtomCount("H"));        
    }
    
    public void testGetAtomCount_String2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        assertEquals(1, mfa.getAtomCount("C"));        
        assertEquals(1, mfa.getAtomCount("O"));        
        assertEquals(4, mfa.getAtomCount("H")); 
    }
    
    public void testGetHeavyAtoms() {
        MFAnalyser mfa = new MFAnalyser("C10H16", new org.openscience.cdk.AtomContainer());
        assertEquals(10, mfa.getHeavyAtoms().size());        
    }
    
    public void testGetHeavyAtoms2() {
        MFAnalyser mfa = new MFAnalyser("CH3OH", new org.openscience.cdk.AtomContainer());
        assertEquals(2, mfa.getHeavyAtoms().size());        
    }
    
    /**
     * Test removeHydrogensPreserveMultiplyBonded for B2H6, which contains two multiply bonded H.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testRemoveHydrogensPreserveMultiplyBonded() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = parser.parseSmiles("B1([H])([H])[H]B([H])([H])[H]1");
        IAtomContainer ac = new MFAnalyser(mol).removeHydrogensPreserveMultiplyBonded();

        // Should be two connected Bs with H-count == 2 and two explicit Hs.
        assertEquals("incorrect atom count", 4, ac.getAtomCount());
        assertEquals("incorrect bond count", 4, ac.getBondCount());

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
                assertEquals("incorrect hydrogen count", 2, atom.getHydrogenCount().intValue());
                java.util.List nbs = ac.getConnectedAtomsList(atom);
                assertEquals("incorrect connected count", 2, nbs.size());
                assertEquals("incorrect bond", "H", ((IAtom)nbs.get(0)).getSymbol());
                assertEquals("incorrect bond", "H", ((IAtom)nbs.get(1)).getSymbol());
            }
            else if (sym.equals("H"))
            {
                h++;
            }
        }
        assertEquals("incorrect no. Bs", 2, b);
        assertEquals("incorrect no. Hs", 2, h);
    }


    public void testGetFormulaHashtable() {
	MFAnalyser mfa=new MFAnalyser(molecule);
	Map formula = mfa.getFormulaHashtable();
	assertEquals(10, ((Integer)formula.get("C")).intValue());
    }
    
    public void testGetMass() throws Exception{
        MFAnalyser mfa = new MFAnalyser(molecule);
        assertEquals((float)120, mfa.getMass(), .1);
        assertEquals((float)120.11038, mfa.getNaturalMass(), .1);
    }
    
    public void testGetNaturalMass_IElement() throws Exception {
        assertEquals(1.0079760, MFAnalyser.getNaturalMass(new Element("H")), 0.1);
    }
    
    public void testGetNaturalMass() throws Exception {
    	MFAnalyser mfa = new MFAnalyser("C8H10O2Cl2", new Molecule());
    	assertEquals((float)209.0692 , mfa.getNaturalMass() ,.001);
    }
    
    public void testGetHTMLMolecularFormulaWithCharge() {
    	org.openscience.cdk.interfaces.IAtom atom = molecule.getAtom(0);
        MFAnalyser mfa = new MFAnalyser(molecule);
        assertEquals("C<sub>10</sub>", mfa.getHTMLMolecularFormulaWithCharge());
        atom.setFormalCharge(atom.getFormalCharge() + 1);
        assertEquals("C<sub>10</sub><sup>1+</sup>", mfa.getHTMLMolecularFormulaWithCharge());
        atom.setFormalCharge(atom.getFormalCharge() - 2);
        assertEquals("C<sub>10</sub><sup>1-</sup>", mfa.getHTMLMolecularFormulaWithCharge());
    }
    
    public void testGetHTMLMolecularFormula() {
    	MFAnalyser mfa = new MFAnalyser("C8H10O2Cl2", new Molecule());
        assertEquals("C<sub>8</sub>H<sub>10</sub>O<sub>2</sub>Cl<sub>2</sub>", mfa.getHTMLMolecularFormula());
    }
    
    /**
     * @cdk.bug 1595430
     */
    public void test1595430_2() throws Exception {
		String smile="CN(CC2=CC=CO2)C1=CC=CC=C1";
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles(smile);
		HydrogenAdder hAdder=new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		MFAnalyser mfa=new MFAnalyser(mol);
		assertEquals((float)187.2382 , mfa.getNaturalMass() ,.001);
		assertEquals("C12H13NO",mfa.getMolecularFormula());
    }
}


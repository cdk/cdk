/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test.atomtype;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class MMFF94AtomTypeMatcherTest extends CDKTestCase {

	private LoggingTool logger;
	private final IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	private HydrogenAdder haad=new HydrogenAdder();
	public MMFF94AtomTypeMatcherTest(String name) {
        super(name);
    }

    public void setUp() {
    	logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MMFF94AtomTypeMatcherTest.class);
    }
    
    public void testMMFF94AtomTypeMatcher() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	MMFF94AtomTypeMatcher matcher = new MMFF94AtomTypeMatcher();
	    assertNotNull(matcher);
	    
    }
    
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	if (!this.runSlowTests()) fail("Slow tests turned of");
    	
    	//logger.debug("**** START ATOMTYPE TEST ******");
    	AtomTypeTools att=new AtomTypeTools();
    	//SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    	Molecule mol=null;
        //HydrogenAdder hAdder = new HydrogenAdder();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
        BufferedReader fin =null;
        InputStream ins=null;
		try{
			ins = this.getClass().getClassLoader().getResourceAsStream("data/mdl/mmff94AtomTypeTest_molecule.mol");
			fin = new BufferedReader(new InputStreamReader(ins));
			//fin=new BufferedReader(new FileReader("data/mmff94AtomTypeTest_molecule.mol"));
			MDLReader mdl=new MDLReader(fin);
			mol=(Molecule)mdl.read(new Molecule());
		}catch (Exception exc1){
			fail("Problems loading file due to "+exc1.toString());
		}
       
        att.assignAtomTypePropertiesToAtom(mol);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        
        logger.debug("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());
        //logger.debug("Atom 0:"+mol.getAtomAt(256).getAtomTypeName());
        
        assertEquals("Sthi",mol.getAtom(0).getAtomTypeName());
        assertEquals("Csp2",mol.getAtom(7).getAtomTypeName());
        assertEquals("Csp",mol.getAtom(51).getAtomTypeName());
        assertEquals("N=O",mol.getAtom(148).getAtomTypeName());
        assertEquals("Oar",mol.getAtom(198).getAtomTypeName());
        assertEquals("N2OX",mol.getAtom(233).getAtomTypeName());
        assertEquals("NAZT",mol.getAtom(256).getAtomTypeName());
        //logger.debug("**** END OF ATOMTYPE TEST ******");
    }
    
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Methanol() throws ClassNotFoundException, CDKException, java.lang.Exception {

//		logger.debug("**** START ATOMTYPE Methanol TEST ******");
		//System.out.println("**** START ATOMTYPE Methanol TEST ******");  
        IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(oxygen);
		mol.addBond(builder.newBond(carbon, oxygen, CDKConstants.BONDORDER_SINGLE));
		
		haad.addExplicitHydrogensToSatisfyValency(mol);
        
		String [] testResult={"C","O","HC","HC","HC","HO"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());       
	}
	/**
	 *  A unit test for JUnit with Methylamine
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Methylamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Methylamine TEST ******");	
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom nitrogen = builder.newAtom(Elements.NITROGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(nitrogen);
		mol.addBond(builder.newBond(carbon, nitrogen, CDKConstants.BONDORDER_SINGLE));
		
		haad.addExplicitHydrogensToSatisfyValency(mol);

		String [] testResult={"C","N","HC","HC","HC","HN","HN"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());    
	}
	/**
	 *  A unit test for JUnit with ethoxyethane
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Ethoxyethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Ethoxyethane TEST ******");	   
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		IAtom carbon2 = builder.newAtom(Elements.CARBON);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(oxygen);
		mol.addAtom(carbon2); 
		mol.addBond(builder.newBond(carbon, oxygen, CDKConstants.BONDORDER_SINGLE));
		mol.addBond(builder.newBond(carbon2, oxygen, CDKConstants.BONDORDER_SINGLE));
		
		haad.addExplicitHydrogensToSatisfyValency(mol);
		
		String [] testResult={"C","O","C","HC","HC","HC","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());
	}
	/**
	 *  A unit test for JUnit with Methanethiol
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Methanethiol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Methanethiol TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom sulfur = builder.newAtom(Elements.SULFUR);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(sulfur);
		mol.addBond(builder.newBond(carbon, sulfur, CDKConstants.BONDORDER_SINGLE));
		
		haad.addExplicitHydrogensToSatisfyValency(mol);

		String [] testResult={"C","S","HC","HC","HC","HP"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());		
	}
	/**
	 *  A unit test for JUnit with Chloromethane
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Chloromethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Chlormethane TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom chlorine = builder.newAtom(Elements.CHLORINE);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(chlorine);
		mol.addBond(builder.newBond(carbon, chlorine, CDKConstants.BONDORDER_SINGLE));
		
		haad.addExplicitHydrogensToSatisfyValency(mol);
		
		String [] testResult={"C","CL","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
       //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());			
	}
	/**
	 *  A unit test for JUnit with Benzene
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Benzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Benzene TEST ******");
		IMolecule mol = builder.newMolecule();
		for (int i=0; i<6; i++) {
			IAtom carbon = builder.newAtom(Elements.CARBON);
			carbon.setFlag(CDKConstants.ISAROMATIC, true);
			// making sure the order matches the test results
			mol.addAtom(carbon);			
		}
		IBond ringBond = builder.newBond(mol.getAtom(0), mol.getAtom(1), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(1), mol.getAtom(2), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(2), mol.getAtom(3), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(3), mol.getAtom(4), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(4), mol.getAtom(5), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(5), mol.getAtom(0), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
			
		haad.addExplicitHydrogensToSatisfyValency(mol);		
		
		String [] testResult={"Car","Car","Car","Car","Car","Car","HC","HC","HC","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
        att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	//System.out.println("MatchedTypeID:"+matched.getID()+" "+matched.getSymbol()+" "+matched.getAtomTypeName());
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());
        //System.out.println(mol.toString());
	}
	/**
	 *  A unit test for JUnit with Water
	 */
	public void testFindMatchingAtomType_IAtomContainer_IAtom_Water() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("**** START ATOMTYPE Water TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(oxygen);
		haad.addExplicitHydrogensToSatisfyValency(mol);

		String [] testResult={"OH2","HO","HO"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertEquals(testResult[i],mol.getAtom(i).getAtomTypeName());
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());		
	}
}

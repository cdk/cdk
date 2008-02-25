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
package org.openscience.cdk.atomtype;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test-experimental
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class MMFF94AtomTypeMatcherTest extends AbstractAtomTypeTest {

	private static LoggingTool logger;
	private final IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	
	private static IMolecule testMolecule = null;
	
    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

	@BeforeClass public static void setUpTestMolecule() throws Exception {
    	logger = new LoggingTool(MMFF94AtomTypeMatcherTest.class);
    
    	if (testMolecule == null) {
        	//logger.debug("**** START ATOMTYPE TEST ******");
        	AtomTypeTools att=new AtomTypeTools();
            MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
            InputStream ins = MMFF94AtomTypeMatcherTest.class.getClassLoader().getResourceAsStream("data/mdl/mmff94AtomTypeTest_molecule.mol");
            MDLV2000Reader mdl=new MDLV2000Reader(new InputStreamReader(ins));
            testMolecule = (IMolecule)mdl.read(new NNMolecule());
           
            att.assignAtomTypePropertiesToAtom(testMolecule);
            for (int i=0;i<testMolecule.getAtomCount();i++){
            	logger.debug("atomNr:" + testMolecule.getAtom(i).toString());
            	IAtomType matched = atm.findMatchingAtomType(testMolecule, testMolecule.getAtom(i));
            	AtomTypeManipulator.configure(testMolecule.getAtom(i), matched);       
            }
            
            logger.debug("MMFF94 Atom 0:"+testMolecule.getAtom(0).getAtomTypeName());
    	}
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(MMFF94AtomTypeMatcherTest.class);
    }
    
    @Test public void testMMFF94AtomTypeMatcher() throws Exception {
    	MMFF94AtomTypeMatcher matcher = new MMFF94AtomTypeMatcher();
	    Assert.assertNotNull(matcher);
	    
    }
    
    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
    	setUpTestMolecule();
    	for (int i=0;i<testMolecule.getAtomCount();i++) {
    		Assert.assertNotNull(testMolecule.getAtom(i).getAtomTypeName());
    		Assert.assertTrue(testMolecule.getAtom(i).getAtomTypeName().length() > 0);
    	}
    }
    
    // FIXME: Below should be tests for *all* atom types in the MM2 atom type specificiation
    
    @Test public void testSthi() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "Sthi",testMolecule.getAtom(0));
    }
    @Test public void testCsp2() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "Csp2",testMolecule.getAtom(7));
    }
    @Test public void testCsp() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "Csp",testMolecule.getAtom(51));
    }
    @Test public void testNdbO() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "N=O",testMolecule.getAtom(148));
    }
    @Test public void testOar() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "Oar",testMolecule.getAtom(198));
    }
    @Test public void testN2OX() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "N2OX",testMolecule.getAtom(233));
    }
    @Test public void testNAZT() throws Exception {
    	setUpTestMolecule();
    	assertAtomType(testedAtomTypes, "NAZT",testMolecule.getAtom(256));
    }
    
    // Other tests
    
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Methanol() throws Exception {

//		logger.debug("**** START ATOMTYPE Methanol TEST ******");
		//System.out.println("**** START ATOMTYPE Methanol TEST ******");  
        IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(oxygen);
		mol.addBond(builder.newBond(carbon, oxygen, CDKConstants.BONDORDER_SINGLE));
		
		addExplicitHydrogens(mol);
        
		String [] testResult={"C","O","HC","HC","HC","HO"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());       
	}
	/**
	 *  A unit test for JUnit with Methylamine
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Methylamine() throws Exception {
		//System.out.println("**** START ATOMTYPE Methylamine TEST ******");	
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom nitrogen = builder.newAtom(Elements.NITROGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(nitrogen);
		mol.addBond(builder.newBond(carbon, nitrogen, CDKConstants.BONDORDER_SINGLE));
		
		addExplicitHydrogens(mol);

		String [] testResult={"C","N","HC","HC","HC","HN","HN"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());    
	}
	/**
	 *  A unit test for JUnit with ethoxyethane
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Ethoxyethane() throws Exception {
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
		
		addExplicitHydrogens(mol);
		
		String [] testResult={"C","O","C","HC","HC","HC","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());
	}
	/**
	 *  A unit test for JUnit with Methanethiol
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Methanethiol() throws Exception {
		//System.out.println("**** START ATOMTYPE Methanethiol TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom sulfur = builder.newAtom(Elements.SULFUR);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(sulfur);
		mol.addBond(builder.newBond(carbon, sulfur, CDKConstants.BONDORDER_SINGLE));
		
		addExplicitHydrogens(mol);

		String [] testResult={"C","S","HC","HC","HC","HP"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());		
	}
	/**
	 *  A unit test for JUnit with Chloromethane
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Chloromethane() throws Exception {
		//System.out.println("**** START ATOMTYPE Chlormethane TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom chlorine = builder.newAtom(Elements.CHLORINE);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(chlorine);
		mol.addBond(builder.newBond(carbon, chlorine, CDKConstants.BONDORDER_SINGLE));
		
		addExplicitHydrogens(mol);
		
		String [] testResult={"C","CL","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
       //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());			
	}
	/**
	 *  A unit test for JUnit with Benzene
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Benzene() throws Exception {
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
			
		addExplicitHydrogens(mol);		
		
		String [] testResult={"Car","Car","Car","Car","Car","Car","HC","HC","HC","HC","HC","HC"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
        att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	//System.out.println("MatchedTypeID:"+matched.getID()+" "+matched.getSymbol()+" "+matched.getAtomTypeName());
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());
        //System.out.println(mol.toString());
	}
	/**
	 *  A unit test for JUnit with Water
	 */
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom_Water() throws Exception {
		//System.out.println("**** START ATOMTYPE Water TEST ******");
		IMolecule mol = builder.newMolecule();
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(oxygen);
		addExplicitHydrogens(mol);

		String [] testResult={"OH2","HO","HO"};
		AtomTypeTools att=new AtomTypeTools();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
		att.assignAtomTypePropertiesToAtom(mol,false);
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:" + mol.getAtom(i).toString());
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
        	Assert.assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtom(i), matched);       
        }
        for (int i=0; i<testResult.length;i++){
        	assertAtomType(testedAtomTypes, testResult[i],mol.getAtom(i));
        }
        //System.out.println("MMFF94 Atom 0:"+mol.getAtom(0).getAtomTypeName());		
	}
	
    /**
     * The test seems to be run by JUnit in order in which they found
     * in the source. Ugly, but @AfterClass does not work because that
     * methods does cannot Assert.assert anything.
     */
    @Test public void countTestedAtomTypes() {
    	AtomTypeFactory factory = AtomTypeFactory.getInstance(
    		"org/openscience/cdk/config/data/mmff94_atomtypes.xml",
            NoNotificationChemObjectBuilder.getInstance()
        );
    	
   	    IAtomType[] expectedTypes = factory.getAllAtomTypes();
    	if (expectedTypes.length != testedAtomTypes.size()) {
       	    String errorMessage = "Atom types not tested:";
       	    for (int i=0; i<expectedTypes.length; i++) {
       	    	if (!testedAtomTypes.containsKey(expectedTypes[i].getAtomTypeName()))
       	    		errorMessage += " " + expectedTypes[i].getAtomTypeName();
       	    }
    		Assert.assertEquals(errorMessage,
    			factory.getAllAtomTypes().length, 
    			testedAtomTypes.size()
    		);
    	}
    }

}

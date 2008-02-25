/* $Revision$ $Author$$Date$
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
import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.atomtype.MM2AtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF2AtomTypeMatcher.
 *
 * @cdk.module test-experimental
 *
 * @see org.openscience.cdk.atomtype.MMFF2AtomTypeMatcher
 */
public class MM2AtomTypeMatcherTest extends AbstractAtomTypeTest {

	private static LoggingTool logger;
	private static IMolecule testMolecule = null;
	
    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(MM2AtomTypeMatcherTest.class);
    }
	
    @BeforeClass public static void setUp() throws Exception {
    	logger = new LoggingTool(MM2AtomTypeMatcherTest.class);
    	
    	if (testMolecule == null) {
    		// read the test file and percieve atom types
    		AtomTypeTools att=new AtomTypeTools();
    		MM2AtomTypeMatcher atm= new MM2AtomTypeMatcher();
    		logger.debug("**** reading MOL file ******");
    		InputStream ins = MM2AtomTypeMatcher.class.getClassLoader().getResourceAsStream("data/mdl/mmff94AtomTypeTest_molecule.mol");
    		IChemObjectReader mdl = new MDLV2000Reader(ins);
    		testMolecule=(IMolecule)mdl.read(new NNMolecule());
    		logger.debug("Molecule load:"+testMolecule.getAtomCount());
    		att.assignAtomTypePropertiesToAtom(testMolecule);
    		for (int i=0;i<testMolecule.getAtomCount();i++){
    			logger.debug("atomNr:"+i);
    			IAtomType matched = null;
    			matched = atm.findMatchingAtomType(testMolecule, testMolecule.getAtom(i));
    			logger.debug("Found AtomType: ", matched);
    			AtomTypeManipulator.configure(testMolecule.getAtom(i), matched);       
    		}
    	}
    }

    @Test public void testMMFF94AtomTypeMatcher() {
    	MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
	    Assert.assertNotNull(matcher);
    }
    
    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
    	for (int i=0;i<testMolecule.getAtomCount();i++) {
    		Assert.assertNotNull(testMolecule.getAtom(i).getAtomTypeName());
    		Assert.assertTrue(testMolecule.getAtom(i).getAtomTypeName().length() > 0);
    	}
    }
    
    // FIXME: Below should be tests for *all* atom types in the MM2 atom type specificiation
    
    @Test public void testSthi() {
        assertAtomType(testedAtomTypes, "Sthi",testMolecule.getAtom(0));
    }
    @Test public void testCsp2() {
        assertAtomType(testedAtomTypes, "Csp2",testMolecule.getAtom(7));
    }
    @Test public void testCsp() {
        assertAtomType(testedAtomTypes, "Csp",testMolecule.getAtom(51));
    }
    @Test public void testNdbC() {
        assertAtomType(testedAtomTypes, "N=C",testMolecule.getAtom(148));
    }
    @Test public void testOar() {
        assertAtomType(testedAtomTypes, "Oar",testMolecule.getAtom(198));
    }
    @Test public void testN2OX() {
        assertAtomType(testedAtomTypes, "N2OX",testMolecule.getAtom(233));
    }
    @Test public void testNsp2() {
        assertAtomType(testedAtomTypes, "Nsp2",testMolecule.getAtom(256));
    }
    
    /**
     * The test seems to be run by JUnit in order in which they found
     * in the source. Ugly, but @AfterClass does not work because that
     * methods does cannot Assert.assert anything.
     */
    @Test public void countTestedAtomTypes() {
    	AtomTypeFactory factory = AtomTypeFactory.getInstance(
    		"org/openscience/cdk/config/data/mm2_atomtypes.xml",
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

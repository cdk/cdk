/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
 * 
 */

package org.openscience.cdk.test.atomtype;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.atomtype.MM2AtomTypeMatcher;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class MM2AtomTypeMatcherTest extends CDKTestCase {

	private LoggingTool logger;
	
	private static IMolecule testMolecule = null;
	
    public MM2AtomTypeMatcherTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
    	logger = new LoggingTool(this);
    	
    	if (testMolecule == null) {
    		// read the test file and percieve atom types
    		AtomTypeTools att=new AtomTypeTools();
    		MM2AtomTypeMatcher atm= new MM2AtomTypeMatcher();
    		logger.debug("**** reading MOL file ******");
    		InputStream ins = this.getClass().getClassLoader().getResourceAsStream("data/mdl/mmff94AtomTypeTest_molecule.mol");
    		IChemObjectReader mdl = new MDLV2000Reader(ins);
    		testMolecule=(IMolecule)mdl.read(new NNMolecule());
    		assertTrue(testMolecule.getAtomCount() > 0);
    		logger.debug("Molecule load:"+testMolecule.getAtomCount());
    		att.assignAtomTypePropertiesToAtom(testMolecule);
    		for (int i=0;i<testMolecule.getAtomCount();i++){
    			logger.debug("atomNr:"+i);
    			IAtomType matched = null;
    			matched = atm.findMatchingAtomType(testMolecule, testMolecule.getAtom(i));
    			logger.debug("Found AtomType: ", matched);
    			assertNotNull(matched);
    			AtomTypeManipulator.configure(testMolecule.getAtom(i), matched);       
    		}
    	}
    }

    public static Test suite() {
        return new TestSuite(MM2AtomTypeMatcherTest.class);
    }
    
    public void testMMFF94AtomTypeMatcher() {
    	MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
	    assertNotNull(matcher);
    }
    
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
    	for (int i=0;i<testMolecule.getAtomCount();i++) {
    		assertNotNull(testMolecule.getAtom(i).getAtomTypeName());
    		assertTrue(testMolecule.getAtom(i).getAtomTypeName().length() > 0);
    	}
    }
    
    // FIXME: Below should be tests for *all* atom types in the MM2 atom type specificiation
    
    public void testSthi() {
        assertEquals("Sthi",testMolecule.getAtom(0).getAtomTypeName());
    }
    public void testCsp2() {
        assertEquals("Csp2",testMolecule.getAtom(7).getAtomTypeName());
    }
    public void testCsp() {
        assertEquals("Csp",testMolecule.getAtom(51).getAtomTypeName());
    }
    public void testNdbC() {
        assertEquals("N=C",testMolecule.getAtom(148).getAtomTypeName());
    }
    public void testOar() {
        assertEquals("Oar",testMolecule.getAtom(198).getAtomTypeName());
    }
    public void testN2OX() {
        assertEquals("N2OX",testMolecule.getAtom(233).getAtomTypeName());
    }
    public void testNsp2() {
        assertEquals("Nsp2",testMolecule.getAtom(256).getAtomTypeName());
    }
}

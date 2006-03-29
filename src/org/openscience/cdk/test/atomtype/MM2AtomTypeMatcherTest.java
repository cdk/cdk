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
//import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.atomtype.MM2AtomTypeMatcher;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.tools.manipulator.*;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test-extra
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class MM2AtomTypeMatcherTest extends CDKTestCase {

	private LoggingTool logger;
	
    public MM2AtomTypeMatcherTest(String name) {
        super(name);
    }

    public void setUp() {
    	logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MM2AtomTypeMatcherTest.class);
    }
    
    public void testMMFF94AtomTypeMatcher() {
    	MM2AtomTypeMatcher matcher = new MM2AtomTypeMatcher();
	    assertNotNull(matcher);
    }
    
    public void testFindMatchingAtomType_IAtomContainer_IAtom() {
    	if (!this.runSlowTests()) fail("Slow tests turned of");
    	
    	logger.debug("**** START MM2 ATOMTYPE TEST ******");
    	AtomTypeTools att=new AtomTypeTools();
    	Molecule mol=null;
    	MM2AtomTypeMatcher atm= new MM2AtomTypeMatcher();
        BufferedReader fin =null;
        InputStream ins=null;
        logger.debug("**** reading MOL file ******");
		try{
			ins = this.getClass().getClassLoader().getResourceAsStream("data/mdl/mmff94AtomTypeTest_molecule.mol");
			fin = new BufferedReader(new InputStreamReader(ins));
			MDLReader mdl=new MDLReader(fin);
			mol=(Molecule)mdl.read(new Molecule());
		} catch (Exception exc1){
			logger.error("Problems loading file due to "+exc1.getMessage());
			logger.debug(exc1);
			fail("Problems loading file due to "+exc1.getMessage());
		}
		assertTrue(mol.getAtomCount() > 0);
		logger.debug("Molecule load:"+mol.getAtomCount());
		try {
			att.assignAtomTypePropertiesToAtom(mol);
		} catch (Exception exception) {
			logger.error("Could not atom type properties: " + exception.getMessage());
			logger.debug(exception);
			fail("Could not atom type properties: " + exception.getMessage());
		}
        for (int i=0;i<mol.getAtomCount();i++){
        	logger.debug("atomNr:"+i);
        	IAtomType matched = null;
        	try {
        		matched = atm.findMatchingAtomType(mol, mol.getAtomAt(i));
        		logger.debug("Found AtomType: ", matched);
        	} catch (Exception exception) {
        		logger.error("Could not percieve atom type: " + exception.getMessage());
        		logger.debug(exception);
        		fail("Could not percieve atom type: " + exception.getMessage());
        	}
        	assertNotNull(matched);
        	AtomTypeManipulator.configure(mol.getAtomAt(i), matched);       
        }
        
        logger.debug("MM2 Atom 0:"+mol.getAtomAt(0).getAtomTypeName());
        
        assertEquals("Sthi",mol.getAtomAt(0).getAtomTypeName());
        assertEquals("Csp2",mol.getAtomAt(7).getAtomTypeName());
        assertEquals("Csp",mol.getAtomAt(51).getAtomTypeName());
        assertEquals("N=C",mol.getAtomAt(148).getAtomTypeName());
        assertEquals("Oar",mol.getAtomAt(198).getAtomTypeName());
        assertEquals("N2OX",mol.getAtomAt(233).getAtomTypeName());
        assertEquals("Nsp2",mol.getAtomAt(256).getAtomTypeName());
        logger.debug("**** END OF ATOMTYPE TEST ******");
    }
}

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
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLReader;
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
public class MMFF94AtomTypeMatcherTest extends CDKTestCase {

	private LoggingTool logger;

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
    	
    	//System.out.println("**** START ATOMTYPE TEST ******");
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
        //System.out.println("Atom 0:"+mol.getAtomAt(256).getAtomTypeName());
        
        assertEquals("Sthi",mol.getAtom(0).getAtomTypeName());
        assertEquals("Csp2",mol.getAtom(7).getAtomTypeName());
        assertEquals("Csp",mol.getAtom(51).getAtomTypeName());
        assertEquals("NdoubleBondedO",mol.getAtom(148).getAtomTypeName());
        assertEquals("Oar",mol.getAtom(198).getAtomTypeName());
        assertEquals("N2OX",mol.getAtom(233).getAtomTypeName());
        assertEquals("NAZT",mol.getAtom(256).getAtomTypeName());
        //System.out.println("**** END OF ATOMTYPE TEST ******");
    }
}

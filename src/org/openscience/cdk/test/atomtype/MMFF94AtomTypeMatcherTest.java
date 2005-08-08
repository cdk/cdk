/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test.atomtype;

import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomType;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.AtomTypeTools;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-MMFF94AtomTypeMatcher.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher
 */
public class MMFF94AtomTypeMatcherTest extends CDKTestCase {

    public MMFF94AtomTypeMatcherTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(MMFF94AtomTypeMatcherTest.class);
    }
    
    public void testMMFF94AtomTypeMatcher() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	MMFF94AtomTypeMatcher matcher = new MMFF94AtomTypeMatcher();
	    assertNotNull(matcher);
	    
    }
    
    public void testFindMatchingAtomType_AtomContainer_Atom() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	//System.out.println("**** START ATOMTYPE TEST ******");
    	AtomTypeTools att=new AtomTypeTools();
    	//SmilesParser sp = new SmilesParser();
    	Molecule mol=null;
        //HydrogenAdder hAdder = new HydrogenAdder();
        MMFF94AtomTypeMatcher atm= new MMFF94AtomTypeMatcher();
        BufferedReader fin =null;
        InputStream ins=null;
		try{
			ins = this.getClass().getClassLoader().getResourceAsStream("data/mmff94AtomTypeTest_molecule.mol");
			fin = new BufferedReader(new InputStreamReader(ins));
			//fin=new BufferedReader(new FileReader("data/mmff94AtomTypeTest_molecule.mol"));
			MDLReader mdl=new MDLReader(fin);
			mol=(Molecule)mdl.read(new Molecule());
		}catch (Exception exc1){
			System.out.println("Problems loading file due to "+exc1.toString());
		}
       
        att.assignAtomTypePropertiesToAtom(mol);
        for (int i=0;i<mol.getAtomCount();i++){
        	//System.out.print("atomNr:"+i);
        	AtomType matched = atm.findMatchingAtomType(mol, mol.getAtomAt(i));
        	AtomTypeManipulator.configure(mol.getAtomAt(i), matched);       
        }
        
        //System.out.println("Atom 0:"+mol.getAtomAt(0).getAtomTypeName());
        //System.out.println("Atom 0:"+mol.getAtomAt(256).getAtomTypeName());
        
        assertEquals("Sthi",mol.getAtomAt(0).getAtomTypeName());
        assertEquals("Csp2",mol.getAtomAt(7).getAtomTypeName());
        assertEquals("Csp",mol.getAtomAt(51).getAtomTypeName());
        assertEquals("N=O",mol.getAtomAt(148).getAtomTypeName());
        assertEquals("Oar",mol.getAtomAt(198).getAtomTypeName());
        assertEquals("N2OX",mol.getAtomAt(233).getAtomTypeName());
        assertEquals("NAZT",mol.getAtomAt(256).getAtomTypeName());
        //System.out.println("**** END OF ATOMTYPE TEST ******");
    }
}

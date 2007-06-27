/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.atomtype;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.StructGenMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * This class tests the matching of atom types defined in the 
 * structgen atom type list.
 * 
 * @cdk.module test-core
 */
public class StructGenMatcherTest extends CDKTestCase {

    private LoggingTool logger = new LoggingTool(StructGenMatcherTest.class);

    public StructGenMatcherTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(StructGenMatcherTest.class);
    }
    
    public void testStructGenMatcher() throws ClassNotFoundException, CDKException {
    	StructGenMatcher matcher = new StructGenMatcher();
        assertNotNull(matcher);
    }
    
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        atom.setHydrogenCount(4);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        assertNotNull(matched);
        
        assertEquals("C", matched.getSymbol());
    }

    public void testN3() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        atom.setHydrogenCount(3);
        mol.addAtom(atom);

        StructGenMatcher atm = new StructGenMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        assertNotNull(matched);
        
        assertEquals("N", matched.getSymbol());
    }
    
    public void testReserpine() throws Exception {
        String filename = "data/mdl/reserpine.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule mol = (IMolecule)reader.read(new NNMolecule());
        assertEquals(44, mol.getAtomCount());
        assertEquals(49, mol.getBondCount());
        assertNotNull(mol);
        
        String[] atomTypes = {
        	"","","","","","","","","","",
        	"","","","","","","","","","",
        	"","","","","","","","","","",
        	"","","","","","","","","","",
        	"","","",""
        };
        StructGenMatcher atm = new StructGenMatcher();
        for (int i=0; i<atomTypes.length; i++) {
        	IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(i));
            assertNotNull(matched);
        	assertEquals(atomTypes[i], matched.getAtomTypeName());
        }
    }    

}

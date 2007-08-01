/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * This class tests the matching of atom types defined in the
 * structgen atom type list.
 *
 * @cdk.module test-core
 */
public class CDKAtomTypeMatcherTest extends AbstractAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CDKAtomTypeMatcherTest.class);
    }

    @Test public void testCDKAtomTypeMatcher() throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        final int thisHybridization = CDKConstants.HYBRIDIZATION_SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        Assert.assertEquals(thisHybridization, matched.getHybridization());
    }

    @Test public void testEthene() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testFormaldehyde() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testMethanol() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testHCN() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "N.sp1", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testMethylAmine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "N.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testMethyleneImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "N.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testEthene_withHybridInfo() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        final int thisHybridization = CDKConstants.HYBRIDIZATION_SP2;
        atom.setHybridization(thisHybridization);
        atom2.setHybridization(thisHybridization);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testStructGenMatcher() throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test public void countTestedAtomTypes() {
        AtomTypeFactory factory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/config/data/cdk_atomtypes.xml",
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

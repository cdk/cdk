/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *               2007  Rajarshi Guha
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

import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list.
 *
 * @cdk.module test-standard
 */
public class CDKAtomTypeMatcherTest extends AbstractAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(CDKAtomTypeMatcherTest.class);
    }

    @Test public void testGetInstance_IChemObjectBuilder() throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        Assert.assertEquals(thisHybridization, matched.getHybridization());
    }

    @Test public void testDummy() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        assertAtomType(testedAtomTypes, "X", matched);
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
    
    @Test public void testPropyne() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom3));
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
    
    @Test public void testHNO2() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("O");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom); atom.setFormalCharge(+1);
        mol.addAtom(atom2);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(0,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(0,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "N.plus.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "O.minus", atm.findMatchingAtomType(mol, atom3));
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
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP2;
        atom.setHybridization(thisHybridization);
        atom2.setHybridization(thisHybridization);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testS3() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, o1, 2.0);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, o2, 2.0);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);

        mol.addBond(b1);
        mol.addBond(b2);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S.3", matched);
    }

    @Test public void testH2S() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, h1, 1.0);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, h2, 1.0);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S.3", matched);
    }

    @Test public void testHS() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        s.setFormalCharge(-1);
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, h1, 1.0);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addBond(b1);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        IAtomType matched = atm.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S.minus", matched);
    }

    @Test public void testDMSO() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "S.inyl", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom4));
    }
    
    @Test public void testAmide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "N.amide", atm.findMatchingAtomType(mol, atom3));
    }
    
    @Test public void testAdenine() throws Exception {
    	IMolecule mol = MoleculeFactory.makeAdenine();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, mol.getAtom(0)));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, mol.getAtom(1)));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, mol.getAtom(2)));
        assertAtomType(testedAtomTypes, "N.sp2", atm.findMatchingAtomType(mol, mol.getAtom(3)));
        assertAtomType(testedAtomTypes, "N.sp2", atm.findMatchingAtomType(mol, mol.getAtom(4)));
        assertAtomType(testedAtomTypes, "N.planar3", atm.findMatchingAtomType(mol, mol.getAtom(5)));
        assertAtomType(testedAtomTypes, "N.sp2", atm.findMatchingAtomType(mol, mol.getAtom(6)));
        assertAtomType(testedAtomTypes, "N.sp3", atm.findMatchingAtomType(mol, mol.getAtom(7)));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, mol.getAtom(8)));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, mol.getAtom(9)));
    }
    
    @Test public void testAmide2() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom1));
        assertAtomType(testedAtomTypes, "N.amide", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom3));
    }
    
    @Test public void testThioAcetone() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "S.2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom4));
    }
    
    @Test public void testSulphuricAcid() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "S.onyl", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom4));
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom5));
    }

    @Test public void testSF6() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        IAtom atom6 = new Atom("F");
        IAtom atom7 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addAtom(atom6);
        mol.addAtom(atom7);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,5,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,6,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "S.octahedral", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testPhosphate() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "P.ate", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom4));
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom5));
    }

    @Test public void testAmmonia() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        IAtom atom5 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        atom2.setFormalCharge(+1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "N.plus", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testTMS() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Si");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "Si.sp3", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testPhosphine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "P.ine", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testDiethylPhosphine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "P.ine", atm.findMatchingAtomType(mol, atom2));
    }
    
    @Test public void testPhosphorCompound() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("P");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "P.ine", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testCarbokation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("H");
        IAtom atom4 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.plus.planar", atm.findMatchingAtomType(mol, atom2));
    }

    @Test public void testHydrogen() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "H", atm.findMatchingAtomType(mol, atom));
    }
    
    @Test public void testHydroxyl() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(oxygen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "H", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "O.minus", atm.findMatchingAtomType(mol, oxygen));
    }
    
    @Test public void testHydroxonium() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom1 = new Atom("H");
        IAtom atom2 = new Atom("H");
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(oxygen);
        mol.addBond(0,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.plus", atm.findMatchingAtomType(mol, oxygen));
    }
    
    @Test public void testPositiveCarbonyl() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom1 = new Atom("H");
        IAtom atom2 = new Atom("H");
        IAtom oxygen = new Atom("O");
        IAtom carbon = new Atom("C");
        oxygen.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(oxygen);
        mol.addAtom(carbon);
        mol.addBond(0,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,4,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(3,4,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.plus.sp2", atm.findMatchingAtomType(mol, oxygen));
    }
    
    @Test public void testProton() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(1);
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "H.plus", atm.findMatchingAtomType(mol, atom));
    }

    @Test public void testHalides() throws Exception {
        IMolecule mol = new Molecule();
        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

        IAtom atom = new Atom("Cl");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Cl.minus", atm.findMatchingAtomType(mol, atom));

    	mol = new Molecule();
        atom = new Atom("F");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "F.minus", atm.findMatchingAtomType(mol, atom));

    	mol = new Molecule();
        atom = new Atom("Br");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Br.minus", atm.findMatchingAtomType(mol, atom));

    	mol = new Molecule();
        atom = new Atom("I");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "I.minus", atm.findMatchingAtomType(mol, atom));
    }
    
    @Test public void testHalogens() throws Exception {
        IMolecule mol = new Molecule();
        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

        IAtom atom = new Atom("Cl");
        IAtom hydrogen = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "Cl", atm.findMatchingAtomType(mol, atom));

        mol = new Molecule();
        atom = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "I", atm.findMatchingAtomType(mol, atom));

        mol = new Molecule();
        atom = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "Br", atm.findMatchingAtomType(mol, atom));

        mol = new Molecule();
        atom = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "F", atm.findMatchingAtomType(mol, atom));
    }

    @Test public void testHydride() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "H.minus", atm.findMatchingAtomType(mol, atom));
    }

    @Test public void testAzide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N"); atom2.setFormalCharge(-1);
        IAtom atom3 = new Atom("N"); atom3.setFormalCharge(+1);
        IAtom atom4 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_TRIPLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "N.minus.sp3", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "N.plus.sp1", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "N.sp1", atm.findMatchingAtomType(mol, atom4));
    }
    
    @Test public void testAzide2() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("N"); atom3.setFormalCharge(+1);
        IAtom atom4 = new Atom("N"); atom4.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "C.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "N.sp2", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "N.plus.sp1", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "N.minus.sp2", atm.findMatchingAtomType(mol, atom4));
    }
    
    @Test public void testMercuryComplex() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        
        IAtom atom = new Atom("Hg");
        atom.setFormalCharge(-1);
        IAtom atom1 = new Atom("O");
        atom1.setFormalCharge(+1);
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2, CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,3, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(3,4, CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(4,0, CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "Hg.minus", atm.findMatchingAtomType(mol, atom));
    }
    	
    @Test public void testPoloniumComplex() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("Po");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,3, CDKConstants.BONDORDER_SINGLE);
        assertAtomType(testedAtomTypes, "Po", atm.findMatchingAtomType(mol, atom1));
    }
    	
    @Test public void testSalts() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        
        IAtom atom = new Atom("Na");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Na.plus", atm.findMatchingAtomType(mol, atom));
        
        mol = new Molecule();
        atom = new Atom("K");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "K.plus", atm.findMatchingAtomType(mol, atom));
        
        mol = new Molecule();
        atom = new Atom("Ca");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Ca.2plus", atm.findMatchingAtomType(mol, atom));
        
        mol = new Molecule();
        atom = new Atom("Mg");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Mg.2plus", atm.findMatchingAtomType(mol, atom));

        mol = new Molecule();
        atom = new Atom("Ni");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        assertAtomType(testedAtomTypes, "Ni.2plus", atm.findMatchingAtomType(mol, atom));
    }

    @Test public void testFerrocene() throws Exception {
    	IAtomContainer ferrocene = new Molecule();
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C")); ferrocene.getAtom(4).setFormalCharge(-1);
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C"));
    	ferrocene.addAtom(new Atom("C")); ferrocene.getAtom(9).setFormalCharge(-1);
    	ferrocene.addAtom(new Atom("Fe")); ferrocene.getAtom(10).setFormalCharge(+2);
    	ferrocene.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
    	ferrocene.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
    	ferrocene.addBond(2,3,CDKConstants.BONDORDER_DOUBLE);
    	ferrocene.addBond(3,4,CDKConstants.BONDORDER_SINGLE);
    	ferrocene.addBond(4,0,CDKConstants.BONDORDER_SINGLE);
    	ferrocene.addBond(5,6,CDKConstants.BONDORDER_DOUBLE);
    	ferrocene.addBond(6,7,CDKConstants.BONDORDER_SINGLE);
    	ferrocene.addBond(7,8,CDKConstants.BONDORDER_DOUBLE);
    	ferrocene.addBond(8,9,CDKConstants.BONDORDER_SINGLE);
    	ferrocene.addBond(0,9,CDKConstants.BONDORDER_SINGLE);
        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(ferrocene.getBuilder());
        assertAtomType(testedAtomTypes, "Fe.2plus", atm.findMatchingAtomType(
        	ferrocene, ferrocene.getAtom(10))
        );
        assertAtomType(testedAtomTypes, "C.minus.planar", atm.findMatchingAtomType(
           	ferrocene, ferrocene.getAtom(4))
        );
    }

    @Test public void testFuran() throws Exception {
    	IAtomContainer furan = new Molecule();
    	furan.addAtom(new Atom("C"));
    	furan.addAtom(new Atom("C"));
    	furan.addAtom(new Atom("C"));
    	furan.addAtom(new Atom("C"));
    	furan.addAtom(new Atom("O"));
    	furan.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
    	furan.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
    	furan.addBond(2,3,CDKConstants.BONDORDER_DOUBLE);
    	furan.addBond(3,4,CDKConstants.BONDORDER_SINGLE);
    	furan.addBond(4,0,CDKConstants.BONDORDER_SINGLE);
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(furan.getBuilder());
    	assertAtomType(testedAtomTypes, "O.planar3", atm.findMatchingAtomType(
    		furan, furan.getAtom(4))
    	);
    }
    
    @Test public void testPerchlorate() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "Cl.perchlorate", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom4));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom5));
    }

    @Test public void testChlorate() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_DOUBLE);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.sp3", atm.findMatchingAtomType(mol, atom));
        assertAtomType(testedAtomTypes, "Cl.chlorate", atm.findMatchingAtomType(mol, atom2));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom3));
        assertAtomType(testedAtomTypes, "O.sp2", atm.findMatchingAtomType(mol, atom4));
    }

    @Test public void testOxide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O"); atom.setFormalCharge(-2);
        mol.addAtom(atom);

        CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());
        assertAtomType(testedAtomTypes, "O.minus2", atm.findMatchingAtomType(mol, atom));
    }

    @Test public void testAzulene() throws Exception {
		Molecule molecule = MoleculeFactory.makeAzulene();
		CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			assertAtomType(testedAtomTypes, "C.sp2", atm.findMatchingAtomType(molecule, molecule.getAtom(f)));
		}
	}
    
    @Test public void testIndole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"N.planar3"
		};
		Molecule molecule = MoleculeFactory.makeIndole();
		CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			assertAtomType(testedAtomTypes, expectedTypes[f], atm.findMatchingAtomType(molecule, molecule.getAtom(f)));
		}
	}
    
    @Test public void testPyrrole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		Molecule molecule = MoleculeFactory.makePyrrole();
		CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			assertAtomType(testedAtomTypes, expectedTypes[f], atm.findMatchingAtomType(molecule, molecule.getAtom(f)));
		}
	}
    
    @Test public void testThiazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"S.planar3",
			"C.sp2"
		};
		Molecule molecule = MoleculeFactory.makeThiazole();
		CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
		for (int f = 0; f < molecule.getAtomCount(); f++) {
			assertAtomType(testedAtomTypes, expectedTypes[f], atm.findMatchingAtomType(molecule, molecule.getAtom(f)));
		}
	}

    @Test public void testHaloniums() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	IAtom carbon1 = new Atom("C");
    	IAtom carbon2 = new Atom("C");

    	IAtom atom = new Atom("I");
    	atom.setFormalCharge(+1);
    	mol.addAtom(atom);
    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	assertAtomType(testedAtomTypes, "I.plus", atm.findMatchingAtomType(mol, atom));

    }
    
    @Test public void testRearrangementCarbokation() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	IAtom carbon1 = new Atom("C");
    	carbon1.setFormalCharge(+1);
    	IAtom carbon2 = new Atom("C");
    	IAtom carbon3 = new Atom("C");

    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addAtom(carbon3);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_DOUBLE);
    	mol.addBond(1, 2, CDKConstants.BONDORDER_SINGLE);

    	assertAtomType(testedAtomTypes, "C.plus.sp2", atm.findMatchingAtomType(mol, carbon1));
    }
    
    @Test public void testChargedSpecies() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	IAtom atom1 = new Atom("C");
    	atom1.setFormalCharge(-1);
    	IAtom atom2 = new Atom("O");
    	atom2.setFormalCharge(+1);
    	

    	mol.addAtom(atom1);
    	mol.addAtom(atom2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_TRIPLE);

    	assertAtomType(testedAtomTypes, "C.minus.sp1", atm.findMatchingAtomType(mol, atom1));
    }
    
//    [O+]=C-[C-]
    @Test public void testChargedSpecies2() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	IAtom atom1 = new Atom("O");
    	atom1.setFormalCharge(1);
    	IAtom atom2 = new Atom("C");
    	IAtom atom3 = new Atom("C");
    	atom3.setFormalCharge(-1);

    	mol.addAtom(atom1);
    	mol.addAtom(atom2);
    	mol.addAtom(atom3);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_DOUBLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	assertAtomType(testedAtomTypes, "C.minus.sp3", atm.findMatchingAtomType(mol, atom3));
    }
    
//    [C-]=C-C
    @Test public void testChargedSpecies3() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	IAtom atom1 = new Atom("C");
    	atom1.setFormalCharge(-1);
    	IAtom atom2 = new Atom("C");
    	IAtom atom3 = new Atom("C");

    	mol.addAtom(atom1);
    	mol.addAtom(atom2);
    	mol.addAtom(atom3);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_DOUBLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	assertAtomType(testedAtomTypes, "C.minus.sp2", atm.findMatchingAtomType(mol, atom1));
    }
    
    @Test public void testNobleGases() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(mol.getBuilder());

    	mol.addAtom(new Atom("He"));
    	assertAtomType(testedAtomTypes, "He", atm.findMatchingAtomType(mol, mol.getAtom(0)));
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

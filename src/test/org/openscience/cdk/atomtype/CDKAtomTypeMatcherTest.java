/* $Revision$ $Author$ $Date$
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
package org.openscience.cdk.atomtype;

import junit.framework.JUnit4TestAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.*;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.Symbols;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list.
 *
 * @cdk.module test-core
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

    @Test public void testGetInstance_IChemObjectBuilder_int() throws CDKException {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(
        	DefaultChemObjectBuilder.getInstance(),
        	CDKAtomTypeMatcher.REQUIRE_EXPLICIT_HYDROGENS
        );
        Assert.assertNotNull(matcher);
    }

    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP3;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        String[] expectedTypes = {"C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testDummy() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testEthene() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testEthyneKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);

        String[] expectedTypes = {"C.sp", "C.plus.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testEthyneRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);

        String[] expectedTypes = {"C.sp", "C.radical.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "N.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testImineRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "N.sp2.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testEtheneRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "C.radical.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testGuanineMethyl() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("N");
        IAtom atom5 = new Atom("C");
        IAtom atom6 = new Atom("C");
        IAtom atom7 = new Atom("N");
        IAtom atom8 = new Atom("C");
        IAtom atom9 = new Atom("C");
        IAtom atom10 = new Atom("N");
        IAtom atom11 = new Atom("O");
        IAtom atom12 = new Atom("N");
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addAtom(atom6);
        mol.addAtom(atom7);
        mol.addAtom(atom8);
        mol.addAtom(atom9);
        mol.addAtom(atom10);
        mol.addAtom(atom11);
        mol.addAtom(atom12);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(0,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(0,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(4,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,5,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,6,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,7,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(3,8,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(5,6,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(7,9,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(7,10,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(8,11,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(8,9,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp2", "N.planar3", "C.sp2", "N.sp2", "C.sp3",
        		                  "C.sp2", "N.sp2", "C.sp2", "C.sp2", "N.amide",
        		                  "O.sp2", "N.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"C.sp", "C.sp", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testFormaldehyde() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"O.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testCarboxylate() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("O"); atom2.setFormalCharge(-1);
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.sp2.co2", "C.sp2", "O.minus.co2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testFormaldehydeRadicalKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O"); atom.setFormalCharge(+1);
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"O.plus.sp2.radical", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethanol() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testLithiumMethanoxide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("Li");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(0,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.sp3", "C.sp3", "Li"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testHCN() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);

        String[] expectedTypes = {"N.sp1", "C.sp"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"N.plus.sp2", "O.sp2", "O.minus", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testNitromethane() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("O");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(0,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(0,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.nitro", "O.sp2", "O.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylAmine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylAmineRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addSingleElectron(0);
        atom.setFormalCharge(+1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.plus.sp3.radical", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethyleneImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"N.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"C.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test
    public void testPiperidine() throws Exception {
        IMolecule molecule = MoleculeFactory.makePiperidine();
        String[] expectedTypes = {"N.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test
    public void testTetrahydropyran() throws Exception {
        IMolecule molecule = MoleculeFactory.makeTetrahydropyran();
        String[] expectedTypes = {"O.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
    }

    @Test public void testS3() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, o2, IBond.Order.DOUBLE);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"S.oxide", "O.sp2", "O.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testH2S() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"S.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/H2Se/h1H2
     */
    @Test public void testH2Se() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom se = DefaultChemObjectBuilder.getInstance().newAtom("Se");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(se, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(se, h2, IBond.Order.SINGLE);

        mol.addAtom(se);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        String[] expectedTypes = {"Se.3", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testH2S_Hybridization() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        s.setHybridization(Hybridization.SP3);
        mol.addAtom(s);
        String[] expectedTypes = {"S.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHS() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        s.setFormalCharge(-1);
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, h1, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addBond(b1);

        String[] expectedTypes = {"S.minus", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"O.sp2", "S.inyl", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testDMSOO() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.sp2", "O.sp2", "S.onyl", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testThioAmide() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("S");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"S.2", "C.sp2", "N.thioamide"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testAdenine() throws Exception {
    	IMolecule mol = MoleculeFactory.makeAdenine();
        String[] expectedTypes = {"C.sp2", "C.sp2", "C.sp2", "N.sp2", "N.sp2", "N.planar3",
        	"N.sp2", "N.sp3", "C.sp2", "C.sp2"
        };        
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testLactam() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        IAtom atom5 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(3,4,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(4,5,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(5,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.sp2", "C.sp2", "N.amide", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"S.2", "C.sp2", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"O.sp2", "S.onyl", "O.sp2", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testSulphuricAcid_Charged() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("S");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom); atom.setFormalCharge(-1);
        mol.addAtom(atom2); atom2.setFormalCharge(+2);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4);
        mol.addAtom(atom5);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.minus", "S.onyl.charged", "O.minus", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"F", "S.octahedral", "F", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMnF4() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Mn");
        IAtom atom3 = new Atom("F");
        IAtom atom4 = new Atom("F");
        IAtom atom5 = new Atom("F");
        mol.addAtom(atom); atom.setFormalCharge(-1);
        mol.addAtom(atom2); atom2.setFormalCharge(+2);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4); atom4.setFormalCharge(-1);
        mol.addAtom(atom5); atom5.setFormalCharge(-1);

        String[] expectedTypes = {"F.minus", "Mn.2plus", "F.minus", "F.minus", "F.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testCrF6() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("Cr");
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

        String[] expectedTypes = {"F", "Cr", "F", "F", "F", "F", "F"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"O.sp2", "P.ate", "O.sp3", "O.sp3", "O.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"H", "N.plus", "H", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testNitrogenRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom atom2 = new Atom("N");
        IAtom atom3 = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(atom2); mol.addSingleElectron(1);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "N.sp3.radical", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"C.sp3", "Si.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testTinCompound() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Sn");
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

        String[] expectedTypes = {"C.sp3", "Sn.sp3", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testArsenicPlus() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("As"); atom2.setFormalCharge(+1);
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

        String[] expectedTypes = {"C.sp3", "As.plus", "C.sp3", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"H", "P.ine", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"C.sp3", "P.ine", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    /**
     * @cdk.inchi InChI=1/C2H5P/c1-3-2/h1H2,2H3
     */
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

        String[] expectedTypes = {"C.sp2", "P.irane", "C.sp3"}; // FIXME: compare with previous test... can't both be P.ine...
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"H", "C.plus.planar", "H", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testCarbokation_implicitHydrogen() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        mol.addAtom(atom2);

        String[] expectedTypes = {"C.plus.sp2"}; // FIXME: compare with previous test... same compound!
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHydrogen() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        mol.addAtom(atom);

        String[] expectedTypes = {"H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testHydroxyl() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        IAtom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(oxygen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"H", "O.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"H", "H", "H", "O.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = {"H", "H", "H", "O.plus.sp2", "C.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testProton() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(1);
        mol.addAtom(atom);

        String[] expectedTypes = {"H.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHalides() throws Exception {
        IMolecule mol = new Molecule();

        IAtom atom = new Atom("Cl");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        String[] expectedTypes = {"Cl.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

    	mol = new Molecule();
        atom = new Atom("F");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"F.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

    	mol = new Molecule();
        atom = new Atom("Br");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Br.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

    	mol = new Molecule();
        atom = new Atom("I");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"I.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testHalogens() throws Exception {
        IMolecule mol = new Molecule();

        IAtom atom = new Atom("Cl");
        IAtom hydrogen = new Atom("H");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        String[] expectedTypes = new String[]{"Cl", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        expectedTypes = new String[]{"I", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        expectedTypes = new String[]{"Br", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(hydrogen);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        expectedTypes = new String[]{"F", "H"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testFluorRadical() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"F.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testChlorRadical() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("Cl");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"Cl.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testBromRadical() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("Br");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"Br.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testIodRadical() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new Atom("I");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = {"I.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHydride() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        atom.setFormalCharge(-1);
        mol.addAtom(atom);

        String[] expectedTypes = new String[]{"H.minus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHydrogenRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("H");
        mol.addAtom(atom);
        mol.addSingleElectron(0);

        String[] expectedTypes = new String[]{"H.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = new String[]{"C.sp3", "N.minus.sp3", "N.plus.sp1", "N.sp1"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

        String[] expectedTypes = new String[]{"C.sp3", "N.sp2", "N.plus.sp1", "N.minus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMercuryComplex() throws Exception {
    	IMolecule mol = new Molecule();
        
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
        String[] expectedTypes = new String[]{"Hg.minus", "O.plus.sp2", "C.sp2", "C.sp2", "N.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    	
    @Test public void testPoloniumComplex() throws Exception {
    	IMolecule mol = new Molecule();
        
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
        String[] expectedTypes = new String[]{"O.sp3", "Po", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    	
    @Test public void testStronglyBoundKations() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	mol.addAtom(new Atom("O")); mol.getAtom(1).setFormalCharge(+1);
    	mol.addBond(0,1, IBond.Order.DOUBLE);    	
        IAtom atom = new Atom("Na");
        mol.addAtom(atom);
        mol.addBond(1,2, IBond.Order.SINGLE);
        
        String[] expectedTypes = new String[]{"C.sp2", "O.plus.sp2", "Na"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
     }

     @Test public void testSalts() throws Exception {
    	IMolecule mol = new Molecule();
        
        IAtom atom = new Atom("Na");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        String[] expectedTypes = new String[]{"Na.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
        
        mol = new Molecule();
        atom = new Atom("K");
        atom.setFormalCharge(+1);
        mol.addAtom(atom);
        expectedTypes = new String[]{"K.plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
        
        mol = new Molecule();
        atom = new Atom("Ca");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Ca.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
        
        mol = new Molecule();
        atom = new Atom("Mg");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Mg.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
        
        mol = new Molecule();
        atom = new Atom("Ni");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Ni.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("Pt");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Pt.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("Co");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Co.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("Cu");
        atom.setFormalCharge(+2);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Cu.2plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);

        mol = new Molecule();
        atom = new Atom("Al");
        atom.setFormalCharge(+3);
        mol.addAtom(atom);
        expectedTypes = new String[]{"Al.3plus"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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
    	ferrocene.addBond(9,5,CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = new String[]{
    		"C.sp2","C.sp2","C.sp2","C.sp2","C.minus.planar",
    		"C.sp2","C.sp2","C.sp2","C.sp2","C.minus.planar",
    		"Fe.2plus"
    	};
        assertAtomTypes(testedAtomTypes, expectedTypes, ferrocene);
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
    	String[] expectedTypes = new String[]{
        	"C.sp2","C.sp2","C.sp2","C.sp2","O.planar3"
        };
        assertAtomTypes(testedAtomTypes, expectedTypes, furan);
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

    	String[] expectedTypes = new String[]{
            "O.sp3", "Cl.perchlorate", "O.sp2", "O.sp2", "O.sp2"
        };
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testPerchlorate_ChargedBonds() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("Cl");
        IAtom atom3 = new Atom("O");
        IAtom atom4 = new Atom("O");
        IAtom atom5 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+3);
        mol.addAtom(atom3); atom3.setFormalCharge(-1);
        mol.addAtom(atom4); atom4.setFormalCharge(-1);
        mol.addAtom(atom5); atom5.setFormalCharge(-1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,4,CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = new String[]{
            "O.sp3", "Cl.perchlorate.charged", "O.minus", "O.minus", "O.minus"
    	};
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
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

    	String[] expectedTypes = new String[]{
            "O.sp3", "Cl.chlorate", "O.sp2", "O.sp2"
        };
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testOxide() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O"); atom.setFormalCharge(-2);
        mol.addAtom(atom);

    	String[] expectedTypes = new String[]{"O.minus2"};
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testAzulene() throws Exception {
		IMolecule molecule = MoleculeFactory.makeAzulene();
    	String[] expectedTypes = new String[]{
    		"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2",
    		"C.sp2", "C.sp2", "C.sp2", "C.sp2", "C.sp2"
    	};
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
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
		IMolecule molecule = MoleculeFactory.makeIndole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyrrole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyrrole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyrroleAnion() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.minus.planar3",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyrroleAnion();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testImidazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"C.sp2",
			"N.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeImidazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyrazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"N.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyrazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void test124Triazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"N.sp2",
			"C.sp2",
			"N.sp2"
		};
		IMolecule molecule = MoleculeFactory.make124Triazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void test123Triazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.planar3",
			"N.sp2",
			"N.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.make123Triazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testTetrazole() throws Exception {
		String[] expectedTypes = {
			"N.sp2",
			"N.planar3",
			"N.sp2",
			"N.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeTetrazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testOxazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"O.planar3",
			"C.sp2",
			"N.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeOxazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testIsoxazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"O.planar3",
			"N.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeIsoxazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    // testThiazole can be found below...
    
    @Test public void testIsothiazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"S.planar3",
			"N.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeIsothiazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testThiadiazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"S.planar3",
			"C.sp2",
			"N.sp2",
			"N.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeThiadiazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testOxadiazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"O.planar3",
			"C.sp2",
			"N.sp2",
			"N.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeOxadiazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
   
    @Test public void testPyridine() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyridine();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyridineDirect() throws Exception {
		String[] expectedTypes = {
			"N.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule mol = new Molecule();
		mol.addAtom(new Atom("N"));
		mol.addAtom(new Atom("C"));
		mol.addBond(0,1,IBond.Order.SINGLE);
		mol.addAtom(new Atom("C"));
		mol.addBond(1,2,IBond.Order.DOUBLE);
		mol.addAtom(new Atom("C"));
		mol.addBond(2,3,IBond.Order.SINGLE);
		mol.addAtom(new Atom("C"));
		mol.addBond(3,4,IBond.Order.DOUBLE);
		mol.addAtom(new Atom("C"));
		mol.addBond(4,5,IBond.Order.SINGLE);
		mol.addBond(0,5,IBond.Order.DOUBLE);
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }


    /**
     * @cdk.bug 1957958
     */
    @Test
    public void testPyridineWithSP2() throws Exception {
        String[] expectedTypes = {
                "N.sp2",
                "C.sp2",
                "C.sp2",
                "C.sp2",
                "C.sp2",
                "C.sp2"
        };
        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("N");
        IAtom a2 = mol.getBuilder().newAtom("C");
        IAtom a3 = mol.getBuilder().newAtom("C");
        IAtom a4 = mol.getBuilder().newAtom("C");
        IAtom a5 = mol.getBuilder().newAtom("C");
        IAtom a6 = mol.getBuilder().newAtom("C");

        a1.setHybridization(Hybridization.SP2);
        a2.setHybridization(Hybridization.SP2);
        a3.setHybridization(Hybridization.SP2);
        a4.setHybridization(Hybridization.SP2);
        a5.setHybridization(Hybridization.SP2);
        a6.setHybridization(Hybridization.SP2);

        mol.addAtom(a1);
        mol.addAtom(a2);
        mol.addAtom(a3);
        mol.addAtom(a4);
        mol.addAtom(a5);
        mol.addAtom(a6);

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(0, 5, IBond.Order.SINGLE);
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
	 * @cdk.bug 1879589
	 */
    @Test public void testChargedSulphurSpecies() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"C.sp2",
			"S.plus",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyridine();
		molecule.getAtom(4).setSymbol("S");
		molecule.getAtom(4).setFormalCharge(+1);
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyridineOxide() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.plus.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"O.minus"
		};
		IMolecule molecule = MoleculeFactory.makePyridineOxide();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
    
    @Test public void testPyridineOxide_SP2() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.plus.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2",
			"O.minus"
		};
		IMolecule molecule = MoleculeFactory.makePyridineOxide();
		Iterator<IBond> bonds = molecule.bonds();
		while (bonds.hasNext()) bonds.next().setOrder(CDKConstants.BONDORDER_SINGLE);
		for (int i=0; i<6; i++) {
			molecule.getAtom(i).setHybridization(IAtomType.Hybridization.SP2);
		}
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
   
    @Test public void testPyrimidine() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyrimidine();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
   
    @Test public void testPyridazine() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"N.sp2",
			"C.sp2",
			"C.sp2",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makePyridazine();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}
   
    @Test public void testTriazine() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"N.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeTriazine();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}

    @Test public void testThiazole() throws Exception {
		String[] expectedTypes = {
			"C.sp2",
			"N.sp2",
			"C.sp2",
			"S.planar3",
			"C.sp2"
		};
		IMolecule molecule = MoleculeFactory.makeThiazole();
    	assertAtomTypes(testedAtomTypes, expectedTypes, molecule);
	}

    /**
     * SDF version of the PubChem entry for the given InChI uses uncharged Ni.
     * 
     * @cdk.inchi InChI=1/C2H6S2.Ni/c3-1-2-4;/h3-4H,1-2H2;/q;+2/p-2/fC2H4S2.Ni/h3-4h;/q-2;m
     */
    @Test public void testNiCovalentlyBound() throws Exception {
		String[] expectedTypes = {
			"C.sp3",
			"C.sp3",
			"S.3",
			"Ni",
			"S.3"
		};
		IMolecule mol = new Molecule();
		mol.addAtom(new Atom("C"));
		mol.addAtom(new Atom("C"));
		mol.addBond(0,1,IBond.Order.SINGLE);
		mol.addAtom(new Atom("S"));
		mol.addBond(1,2,IBond.Order.SINGLE);
		mol.addAtom(new Atom("Ni"));
		mol.addBond(2,3,IBond.Order.SINGLE);
		mol.addAtom(new Atom("S"));
		mol.addBond(3,4,IBond.Order.SINGLE);
		mol.addBond(0,4,IBond.Order.SINGLE);
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
	}

    @Test public void testHaloniumsF() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom carbon1 = new Atom("C");
    	IAtom carbon2 = new Atom("C");

    	IAtom atom = new Atom("F");
    	atom.setFormalCharge(+1);
    	mol.addAtom(atom);
    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"F.plus.sp3", "C.sp3", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }


    @Test public void testHaloniumsCl() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom carbon1 = new Atom("C");
    	IAtom carbon2 = new Atom("C");

    	IAtom atom = new Atom("Cl");
    	atom.setFormalCharge(+1);
    	mol.addAtom(atom);
    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"Cl.plus.sp3", "C.sp3", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }


    @Test public void testHaloniumsBr() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom carbon1 = new Atom("C");
    	IAtom carbon2 = new Atom("C");

    	IAtom atom = new Atom("Br");
    	atom.setFormalCharge(+1);
    	mol.addAtom(atom);
    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"Br.plus.sp3", "C.sp3", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }


    @Test public void testHaloniumsI() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom carbon1 = new Atom("C");
    	IAtom carbon2 = new Atom("C");

    	IAtom atom = new Atom("I");
    	atom.setFormalCharge(+1);
    	mol.addAtom(atom);
    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"I.plus.sp3", "C.sp3", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testRearrangementCarbokation() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom carbon1 = new Atom("C");
    	carbon1.setFormalCharge(+1);
    	IAtom carbon2 = new Atom("C");
    	IAtom carbon3 = new Atom("C");

    	mol.addAtom(carbon1);
    	mol.addAtom(carbon2);
    	mol.addAtom(carbon3);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_DOUBLE);
    	mol.addBond(1, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"C.plus.sp2", "C.sp2", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testChargedSpecies() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom atom1 = new Atom("C");
    	atom1.setFormalCharge(-1);
    	IAtom atom2 = new Atom("O");
    	atom2.setFormalCharge(+1);
    	
    	mol.addAtom(atom1);
    	mol.addAtom(atom2);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_TRIPLE);

    	String[] expectedTypes = {"C.minus.sp1", "O.plus.sp1"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
//    [O+]=C-[C-]
    @Test public void testChargedSpecies2() throws Exception {
    	IMolecule mol = new Molecule();

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

    	String[] expectedTypes = {"O.plus.sp2", "C.sp2", "C.minus.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
//    [C-]=C-C
    @Test public void testChargedSpecies3() throws Exception {
    	IMolecule mol = new Molecule();

    	IAtom atom1 = new Atom("C");
    	atom1.setFormalCharge(-1);
    	IAtom atom2 = new Atom("C");
    	IAtom atom3 = new Atom("C");

    	mol.addAtom(atom1);
    	mol.addAtom(atom2);
    	mol.addAtom(atom3);
    	mol.addBond(0, 1, CDKConstants.BONDORDER_DOUBLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"C.minus.sp2", "C.sp2", "C.sp3"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    // [C-]#[N+]C
    @Test public void testIsonitrile() throws CDKException {
        IMolecule mol = new Molecule();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("N");
        atom2.setFormalCharge(1);
        IAtom atom3 = new Atom("C");
        atom3.setFormalCharge(-1);

        mol.addAtom(atom1);
        mol.addAtom(atom2);
        mol.addAtom(atom3);

        mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1, 2, CDKConstants.BONDORDER_TRIPLE);

    	String[] expectedTypes = {"C.sp3", "N.plus.sp1", "C.minus.sp1"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testNobleGases() throws Exception {
    	IMolecule mol = new Molecule();

    	mol.addAtom(new Atom("He"));
    	mol.addAtom(new Atom("Ne"));
    	mol.addAtom(new Atom("Ar"));
    	mol.addAtom(new Atom("Kr"));
    	mol.addAtom(new Atom("Xe"));
    	mol.addAtom(new Atom("Rn"));

    	String[] expectedTypes = {"He", "Ne", "Ar", "Kr", "Xe", "Rn"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testZincChloride() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Zn"));
    	mol.addAtom(new Atom("Cl"));
    	mol.addAtom(new Atom("Cl"));
    	mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
    	mol.addBond(0, 2, CDKConstants.BONDORDER_SINGLE);

    	String[] expectedTypes = {"Zn", "Cl", "Cl"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testZinc() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Zn")); mol.getAtom(0).setFormalCharge(+2);

    	String[] expectedTypes = {"Zn.2plus"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testSilicon() throws Exception {
    	IMolecule mol = new Molecule();
    	IAtom a1 = mol.getBuilder().newAtom("Si"); mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("O"); mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("O"); mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("O"); mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("C"); mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("C"); mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newAtom("C"); mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("H"); mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newAtom("H"); mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newAtom("H"); mol.addAtom(a10);
    	IAtom a11 = mol.getBuilder().newAtom("H"); mol.addAtom(a11);
    	IAtom a12 = mol.getBuilder().newAtom("H"); mol.addAtom(a12);
    	IAtom a13 = mol.getBuilder().newAtom("H"); mol.addAtom(a13);
    	IAtom a14 = mol.getBuilder().newAtom("H"); mol.addAtom(a14);
    	IAtom a15 = mol.getBuilder().newAtom("H"); mol.addAtom(a15);
    	IAtom a16 = mol.getBuilder().newAtom("H"); mol.addAtom(a16);
    	IAtom a17 = mol.getBuilder().newAtom("H"); mol.addAtom(a17);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.SINGLE); mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a1, a3, IBond.Order.SINGLE); mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE); mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, IBond.Order.SINGLE); mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a3, a6, IBond.Order.SINGLE); mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a4, a7, IBond.Order.SINGLE); mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a5, a8, IBond.Order.SINGLE); mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a5, a9, IBond.Order.SINGLE); mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a5, a10, IBond.Order.SINGLE); mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a6, a11, IBond.Order.SINGLE); mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a6, a12, IBond.Order.SINGLE); mol.addBond(b11);
    	IBond b12 = mol.getBuilder().newBond(a6, a13, IBond.Order.SINGLE); mol.addBond(b12);
    	IBond b13 = mol.getBuilder().newBond(a7, a14, IBond.Order.SINGLE); mol.addBond(b13);
    	IBond b14 = mol.getBuilder().newBond(a7, a15, IBond.Order.SINGLE); mol.addBond(b14);
    	IBond b15 = mol.getBuilder().newBond(a7, a16, IBond.Order.SINGLE); mol.addBond(b15);
    	IBond b16 = mol.getBuilder().newBond(a1, a17, IBond.Order.SINGLE); mol.addBond(b16);

    	String[] expectedTypes = {"Si.sp3", "O.sp3", "O.sp3", "O.sp3", "C.sp3", "C.sp3", "C.sp3",
    			"H", "H", "H", "H", "H", "H", "H", "H", "H", "H"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testScandium() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Sc")); mol.getAtom(0).setFormalCharge(-3);
    	mol.addAtom(new Atom("O")); mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(1,2,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("O")); mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(3,4,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("O")); mol.addBond(0,5,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(5,6,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("O")); mol.addBond(0,7,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(7,8,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("O")); mol.addBond(0,9,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(9,10,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("O")); mol.addBond(0,11,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("H")); mol.addBond(11,12,IBond.Order.SINGLE);
    	
    	String[] expectedTypes = {"Sc.3minus", "O.sp3", "H", "O.sp3", "H",
    		"O.sp3", "H", "O.sp3", "H", "O.sp3", "H", "O.sp3", "H"
    	}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testVanadium() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("V")); mol.getAtom(0).setFormalCharge(-3);
    	mol.addAtom(new Atom("C")); mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(1,2,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(3,4,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,5,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(5,6,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,7,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(7,8,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,9,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(9,10,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,11,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(11,12,IBond.Order.TRIPLE);
    	
    	String[] expectedTypes = {"V.3minus", "C.sp", "N.sp1", "C.sp", "N.sp1",
    		"C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1"
        }; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testTitanium() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Ti")); mol.getAtom(0).setFormalCharge(-3);
    	mol.addAtom(new Atom("C")); mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(1,2,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(3,4,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,5,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(5,6,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,7,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(7,8,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,9,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(9,10,IBond.Order.TRIPLE);
    	mol.addAtom(new Atom("C")); mol.addBond(0,11,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("N")); mol.addBond(11,12,IBond.Order.TRIPLE);
    	
    	String[] expectedTypes = {"Ti.3minus", "C.sp", "N.sp1", "C.sp", "N.sp1",
        	"C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1", "C.sp", "N.sp1"
    	}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testBoronTetraFluoride() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("B")); mol.getAtom(0).setFormalCharge(-1);
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addBond(0,2,IBond.Order.SINGLE);
    	mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addBond(0,4,IBond.Order.SINGLE);
    	
    	String[] expectedTypes = {"B.minus", "F", "F", "F", "F"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testBerylliumTetraFluoride() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Be")); mol.getAtom(0).setFormalCharge(-2);
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addAtom(new Atom("F"));
    	mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addBond(0,2,IBond.Order.SINGLE);
    	mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addBond(0,4,IBond.Order.SINGLE);
    	
    	String[] expectedTypes = {"Be.2minus", "F", "F", "F", "F"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testArsine() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("As"));
    	mol.addAtom(new Atom("H"));
    	mol.addAtom(new Atom("H"));
    	mol.addAtom(new Atom("H"));
    	mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addBond(0,2,IBond.Order.SINGLE);
    	mol.addBond(0,3,IBond.Order.SINGLE);
    	
    	String[] expectedTypes = {"As", "H", "H", "H"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testBoron() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("B"));
    	mol.addAtom(new Atom("H"));
    	mol.addAtom(new Atom("H"));
    	mol.addAtom(new Atom("H"));
    	mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addBond(0,2,IBond.Order.SINGLE);
    	mol.addBond(0,3,IBond.Order.SINGLE);
    	
    	String[] expectedTypes = {"B", "H", "H", "H"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testCarbonMonoxide() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("C")); mol.getAtom(0).setFormalCharge(-1);
    	mol.addAtom(new Atom("O")); mol.getAtom(1).setFormalCharge(1);
    	mol.addBond(0,1,IBond.Order.TRIPLE);
    	
    	String[] expectedTypes = {"C.minus.sp1", "O.plus.sp1"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testTitaniumFourCoordinate() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("Ti"));
    	mol.addAtom(new Atom("Cl")); mol.addBond(0,1,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("Cl")); mol.addBond(0,2,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("Cl")); mol.addBond(0,3,IBond.Order.SINGLE);
    	mol.addAtom(new Atom("Cl")); mol.addBond(0,4,IBond.Order.SINGLE);

    	String[] expectedTypes = {"Ti.sp3", "Cl", "Cl", "Cl", "Cl"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    /**
     * @cdk.bug 1872969
     */
    @Test public void bug1872969() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(new Atom("C"));
    	mol.addAtom(new Atom("S"));
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O"));
    	mol.addAtom(new Atom("O")); mol.getAtom(4).setFormalCharge(-1);
    	mol.addAtom(new Atom("Na")); mol.getAtom(5).setFormalCharge(+1);
    	mol.addBond(0,1, IBond.Order.SINGLE);
    	mol.addBond(1,2, IBond.Order.DOUBLE);
    	mol.addBond(1,3, IBond.Order.DOUBLE);
    	mol.addBond(1,4, IBond.Order.SINGLE);

    	String[] expectedTypes = {"C.sp3", "S.onyl", "O.sp2", "O.sp2", "O.minus", "Na.plus"}; 
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * Test if all elements up to and include Uranium have atom types.
     * 
     * @throws Exception
     */
    @Test public void testAllElementsRepresented() throws Exception {
    	AtomTypeFactory factory = AtomTypeFactory.getInstance(
                "org/openscience/cdk/dict/data/cdk-atom-types.owl",
            NoNotificationChemObjectBuilder.getInstance()
        );
    	Assert.assertTrue("Could not read the atom types", factory.getSize() != 0);
        String errorMessage = "Elements without atom type(s) defined in the XML:";
        final int testUptoAtomicNumber = 18; // TODO: 92 ?
        int elementsMissingTypes = 0;
    	for (int i=1; i<testUptoAtomicNumber; i++) {
    		String symbol = Symbols.byAtomicNumber[i];
    		IAtomType[] expectedTypes = factory.getAtomTypes(symbol);
    		if (expectedTypes.length == 0) {
    			errorMessage += " " + symbol;
    			elementsMissingTypes++;
    		}
    	}
		Assert.assertEquals(
			errorMessage,
			0, elementsMissingTypes
		);
    }
    
    @Test public void testAssumeExplicitHydrogens() throws Exception {
    	IMolecule mol = new Molecule();
    	CDKAtomTypeMatcher atm = CDKAtomTypeMatcher.getInstance(
    		mol.getBuilder(),
    		CDKAtomTypeMatcher.REQUIRE_EXPLICIT_HYDROGENS
    	);

    	mol.addAtom(new Atom("O"));
    	mol.getAtom(0).setFormalCharge(+1);
    	Assert.assertNull(atm.findMatchingAtomType(mol, mol.getAtom(0)));
    	
    	for (int i=0; i<3; i++) {
    		mol.addAtom(new Atom("H"));
    		mol.addBond(new Bond(mol.getAtom(i+1), mol.getAtom(0), IBond.Order.SINGLE));
    	}
    	assertAtomType(testedAtomTypes, "O.plus", 
    		atm.findMatchingAtomType(mol, mol.getAtom(0))
    	);    	
    }
    
    @Test public void testStructGenMatcher() throws Exception {
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
        Assert.assertNotNull(matcher);
    }

    @Test public void testCarbonRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        IAtom atom4 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2); mol.addSingleElectron(1);
        mol.addAtom(atom3);
        mol.addAtom(atom4);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(1,3,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp3", "C.radical.planar", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testEthoxyEthaneRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("O");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom); atom.setFormalCharge(+1);
        mol.addSingleElectron(0);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"O.plus.radical", "C.sp3", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testMethylFluorRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp3", "F.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylChloroRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Cl");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp3", "Cl.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylBromoRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp3", "Br.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylIodoRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addSingleElectron(1);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.sp3", "I.plus.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethyleneFluorKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("F");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "F.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethyleneChlorKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Cl");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "Cl.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethyleneBromKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("Br");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "Br.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethyleneIodKation() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("I");
        mol.addAtom(atom);
        mol.addAtom(atom2); atom2.setFormalCharge(+1);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.sp2", "I.plus.sp2"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethanolRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2); mol.addSingleElectron(1);
        mol.addBond(0,1,IBond.Order.SINGLE);

        String[] expectedTypes = {"C.sp3", "O.sp3.radical"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testMethylMethylimineRadical() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("N");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom); atom.setFormalCharge(+1);
        mol.addSingleElectron(0);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"N.plus.sp2.radical", "C.sp2", "C.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testChargeSeparatedFluoroEthane() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("F");
        IAtom atom2 = new Atom("C"); atom2.setFormalCharge(+1);
        IAtom atom3 = new Atom("C"); atom3.setFormalCharge(-1);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"F", "C.plus.planar", "C.minus.sp3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    /**
     * @cdk.inchi InChI=1/C2H7NS/c1-4(2)3/h3H,1-2H3
     */
    @Test public void testSulphurCompound() throws Exception {
    	  IMolecule mol = new Molecule();
    	  IAtom a1 = mol.getBuilder().newAtom("S");
    	  mol.addAtom(a1);
    	  IAtom a2 = mol.getBuilder().newAtom("N");
    	  mol.addAtom(a2);
    	  IAtom a3 = mol.getBuilder().newAtom("C");
    	  mol.addAtom(a3);
    	  IAtom a4 = mol.getBuilder().newAtom("C");
    	  mol.addAtom(a4);
    	  IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.DOUBLE);
    	  mol.addBond(b1);
    	  IBond b2 = mol.getBuilder().newBond(a1, a3, IBond.Order.SINGLE);
    	  mol.addBond(b2);
    	  IBond b3 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE);
    	  mol.addBond(b3);
    	  
    	  String[] expectedTypes = {"S.inyl", "N.sp2", "C.sp3", "C.sp3"};
          assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testAluminumChloride() throws Exception {
    	IMolecule mol = new Molecule();
    	IAtom a1 = mol.getBuilder().newAtom("Cl");
    	mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("Cl");
    	mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("Cl");
    	mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("Al");
    	mol.addAtom(a4);
    	IBond b1 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE);
    	mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a2, a4, IBond.Order.SINGLE);
    	mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a3, a4, IBond.Order.SINGLE);
    	mol.addBond(b3);

    	String[] expectedTypes = {"Cl", "Cl", "Cl", "Al"};
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    /**
     * @cdk.inchi InChI=1/C3H9NO/c1-4(2,3)5/h1-3H3
     */
    @Test public void cid1145() throws Exception {
    	IMolecule mol = new Molecule();
    	IAtom a1 = mol.getBuilder().newAtom("O");
    	mol.addAtom(a1); a1.setFormalCharge(-1);
    	IAtom a2 = mol.getBuilder().newAtom("N");
    	mol.addAtom(a2); a2.setFormalCharge(+1);
    	IAtom a3 = mol.getBuilder().newAtom("C");
    	mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("C");
    	mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("C");
    	mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a10);
    	IAtom a11 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a11);
    	IAtom a12 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a12);
    	IAtom a13 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a13);
    	IAtom a14 = mol.getBuilder().newAtom("H");
    	mol.addAtom(a14);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.SINGLE);
    	mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a2, a3, IBond.Order.SINGLE);
    	mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a2, a4, IBond.Order.SINGLE);
    	mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, IBond.Order.SINGLE);
    	mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a3, a6, IBond.Order.SINGLE);
    	mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a3, a7, IBond.Order.SINGLE);
    	mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a3, a8, IBond.Order.SINGLE);
    	mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a4, a9, IBond.Order.SINGLE);
    	mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a4, a10, IBond.Order.SINGLE);
    	mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a4, a11, IBond.Order.SINGLE);
    	mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a5, a12, IBond.Order.SINGLE);
    	mol.addBond(b11);
    	IBond b12 = mol.getBuilder().newBond(a5, a13, IBond.Order.SINGLE);
    	mol.addBond(b12);
    	IBond b13 = mol.getBuilder().newBond(a5, a14, IBond.Order.SINGLE);
    	mol.addBond(b13);
    	
    	String[] expectedTypes = {
    		"O.minus", "N.plus", "C.sp3", "C.sp3", "C.sp3", 
    		"H", "H", "H", "H", "H", "H", "H", "H", "H"
    	};
    	assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }
    
    @Test public void testChiPathFail() throws Exception {
        IMolecule mol = new Molecule();
        IAtom a1 = mol.getBuilder().newAtom("C");
        mol.addAtom(a1);
        IAtom a2 = mol.getBuilder().newAtom("C");
        mol.addAtom(a2);
        IAtom a3 = mol.getBuilder().newAtom("C");
        mol.addAtom(a3);
        IAtom a4 = mol.getBuilder().newAtom("O");
        mol.addAtom(a4);
        IBond b1 = mol.getBuilder().newBond(a2, a1, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = mol.getBuilder().newBond(a3, a2, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = mol.getBuilder().newBond(a4, a3, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = mol.getBuilder().newBond(a4, a2, IBond.Order.SINGLE);
        mol.addBond(b4);

        String[] expectedTypes = {
            "C.sp3", "C.sp3", "C.sp3", "O.sp3"
        };
        assertAtomTypes(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void countTestedAtomTypes() {
    	super.countTestedAtomTypes(testedAtomTypes);
    }
    
    @Test public void testForDuplicateDefinitions() {
    	super.testForDuplicateDefinitions();
    }
    
}

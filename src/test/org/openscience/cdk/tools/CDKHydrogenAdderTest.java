/* $Revision$ $Author$ $Date$ 
 *
 * Copyright (C) 2007  Egon Willighagen <egonw@sci.kun.nl>
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
package org.openscience.cdk.tools;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module  test-valencycheck
 *
 * @author      Egon Willighagen <egonw@users.sf.net>
 * @cdk.created 2007-07-28
 */
public class CDKHydrogenAdderTest extends CDKTestCase {

//	private final static LoggingTool logger = new LoggingTool(CDKHydrogenAdderTest.class);
	private final static CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
	private final static CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
	
    public CDKHydrogenAdderTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {}

    public static Test suite() {
        return new TestSuite(CDKHydrogenAdderTest.class);
    }
    
    public void testMethane() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	molecule.addAtom(newAtom);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertEquals(4, newAtom.getHydrogenCount().intValue());	
    }

    public void testFormaldehyde() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	IAtom newAtom2 = new NNAtom(Elements.OXYGEN);
    	molecule.addAtom(newAtom);
    	molecule.addAtom(newAtom2);
    	molecule.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	type = matcher.findMatchingAtomType(molecule, newAtom2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom2, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertNotNull(newAtom2.getHydrogenCount());
    	assertEquals(2, newAtom.getHydrogenCount().intValue());	
    	assertEquals(0, newAtom2.getHydrogenCount().intValue());	
    }

    public void testMethanol() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	IAtom newAtom2 = new NNAtom(Elements.OXYGEN);
    	molecule.addAtom(newAtom);
    	molecule.addAtom(newAtom2);
    	molecule.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	type = matcher.findMatchingAtomType(molecule, newAtom2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom2, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertNotNull(newAtom2.getHydrogenCount());
    	assertEquals(3, newAtom.getHydrogenCount().intValue());	
    	assertEquals(1, newAtom2.getHydrogenCount().intValue());	
    }

    public void testHCN() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	IAtom newAtom2 = new NNAtom(Elements.NITROGEN);
    	molecule.addAtom(newAtom);
    	molecule.addAtom(newAtom2);
    	molecule.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	type = matcher.findMatchingAtomType(molecule, newAtom2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom2, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertNotNull(newAtom2.getHydrogenCount());
    	assertEquals(1, newAtom.getHydrogenCount().intValue());	
    	assertEquals(0, newAtom2.getHydrogenCount().intValue());	
    }

    public void testMethylAmine() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	IAtom newAtom2 = new NNAtom(Elements.NITROGEN);
    	molecule.addAtom(newAtom);
    	molecule.addAtom(newAtom2);
    	molecule.addBond(0,1,CDKConstants.BONDORDER_SINGLE);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	type = matcher.findMatchingAtomType(molecule, newAtom2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom2, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertNotNull(newAtom2.getHydrogenCount());
    	assertEquals(3, newAtom.getHydrogenCount().intValue());	
    	assertEquals(2, newAtom2.getHydrogenCount().intValue());	
    }

    public void testMethyleneImine() throws CDKException {
    	IMolecule molecule = new NNMolecule();
    	IAtom newAtom = new NNAtom(Elements.CARBON);
    	IAtom newAtom2 = new NNAtom(Elements.NITROGEN);
    	molecule.addAtom(newAtom);
    	molecule.addAtom(newAtom2);
    	molecule.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);
    	IAtomType type = matcher.findMatchingAtomType(molecule, newAtom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom, type);
    	type = matcher.findMatchingAtomType(molecule, newAtom2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(newAtom2, type);
    	
    	assertNull(newAtom.getHydrogenCount());
    	adder.addImplicitHydrogens(molecule);
    	assertNotNull(newAtom.getHydrogenCount());
    	assertNotNull(newAtom2.getHydrogenCount());
    	assertEquals(2, newAtom.getHydrogenCount().intValue());	
    	assertEquals(1, newAtom2.getHydrogenCount().intValue());	
    }
    
    public void testSulphur() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("S");
        mol.addAtom(atom);
    	IAtomType type = matcher.findMatchingAtomType(mol, atom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(atom, type);
        
    	assertNull(atom.getHydrogenCount());
        adder.addImplicitHydrogens(mol);
        assertEquals(1, mol.getAtomCount());
    	assertNotNull(atom.getHydrogenCount());
    	assertEquals(2, atom.getHydrogenCount().intValue());	
    }

    public void testProton() throws Exception {
    	Molecule mol = new Molecule();
    	Atom proton = new Atom("H");
    	proton.setFormalCharge(+1);
    	mol.addAtom(proton);
    	IAtomType type = matcher.findMatchingAtomType(mol, proton);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(proton, type);

    	adder.addImplicitHydrogens(mol);

    	assertEquals(1, mol.getAtomCount());
    	IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);
    	assertEquals(1, MolecularFormulaManipulator.getElementCount(formula,mol.getBuilder().newElement("H")));
    	assertEquals(0, mol.getConnectedBondsCount(proton));
    	assertNotNull(proton.getHydrogenCount());
    	assertEquals(0, proton.getHydrogenCount().intValue());
    }

    public void testHydrogen() throws Exception {
    	Molecule mol = new Molecule();
    	Atom proton = new Atom("H");
    	mol.addAtom(proton);
    	IAtomType type = matcher.findMatchingAtomType(mol, proton);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(proton, type);

    	adder.addImplicitHydrogens(mol);

    	assertEquals(1, mol.getAtomCount());
    	IMolecularFormula formula = MolecularFormulaManipulator.getMolecularFormula(mol);
    	assertEquals(1, MolecularFormulaManipulator.getElementCount(formula,mol.getBuilder().newElement("H")));
    	assertEquals(0, mol.getConnectedBondsCount(proton));
    	assertNotNull(proton.getHydrogenCount());
    	assertEquals(1, proton.getHydrogenCount().intValue());
    }

    public void testAmmonia() throws Exception {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        mol.addAtom(nitrogen);
        IAtomType type = matcher.findMatchingAtomType(mol, nitrogen);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(nitrogen, type);
        
        adder.addImplicitHydrogens(mol);
        assertNotNull(nitrogen.getHydrogenCount());
        assertEquals(3, nitrogen.getHydrogenCount().intValue());
    }

    public void testAmmonium() throws Exception {
        Molecule mol = new Molecule();
        Atom nitrogen = new Atom("N");
        nitrogen.setFormalCharge(+1);
        mol.addAtom(nitrogen);
        IAtomType type = matcher.findMatchingAtomType(mol, nitrogen);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(nitrogen, type);
        
        adder.addImplicitHydrogens(mol);
        assertNotNull(nitrogen.getHydrogenCount());
        assertEquals(4, nitrogen.getHydrogenCount().intValue());
    }

    public void testWater() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(oxygen, type);
        
        adder.addImplicitHydrogens(mol);
        assertNotNull(oxygen.getHydrogenCount());
        assertEquals(2, oxygen.getHydrogenCount().intValue());
    }

    public void testHydroxonium() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(+1);
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(oxygen, type);

        adder.addImplicitHydrogens(mol);
        assertNotNull(oxygen.getHydrogenCount());
        assertEquals(3, oxygen.getHydrogenCount().intValue());
    }

    public void testHydroxyl() throws Exception {
        Molecule mol = new Molecule();
        Atom oxygen = new Atom("O");
        oxygen.setFormalCharge(-1);
        mol.addAtom(oxygen);
        IAtomType type = matcher.findMatchingAtomType(mol, oxygen);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(oxygen, type);
        
        adder.addImplicitHydrogens(mol);
        assertNotNull(oxygen.getHydrogenCount());
        assertEquals(1, oxygen.getHydrogenCount().intValue());
    }    

    public void testHalogens() throws Exception {
        halogenTest("I");
        halogenTest("F");
        halogenTest("Cl");
        halogenTest("Br");
    }

    public void testHalogenAnions() throws Exception {
        negativeHalogenTest("I");
        negativeHalogenTest("F");
        negativeHalogenTest("Cl");
        negativeHalogenTest("Br");
    }
    
    private void halogenTest(String halogen) throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom(halogen);
        mol.addAtom(atom);
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(atom, type);
        
        adder.addImplicitHydrogens(mol);
        assertEquals(1, mol.getAtomCount());
        assertNotNull(atom.getHydrogenCount());
        assertEquals(1, atom.getHydrogenCount().intValue());
    }

    private void negativeHalogenTest(String halogen) throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom(halogen);
        atom.setFormalCharge(-1);
        mol.addAtom(atom);
        IAtomType type = matcher.findMatchingAtomType(mol, atom);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(atom, type);
        
        adder.addImplicitHydrogens(mol);
        assertEquals(1, mol.getAtomCount());
        assertNotNull(atom.getHydrogenCount());
        assertEquals(0, atom.getHydrogenCount().intValue());
    }

    public void testSulfite() throws Exception {
        Molecule mol = new Molecule();
        Atom s = new Atom("S");
        Atom o1 = new Atom("O");
        Atom o2 = new Atom("O");
        Atom o3 = new Atom("O");
        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        Bond b1 = new Bond(s, o1, IBond.Order.SINGLE);
        Bond b2 = new Bond(s, o2, IBond.Order.SINGLE);
        Bond b3 = new Bond(s, o3, IBond.Order.DOUBLE);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        IAtomType type = matcher.findMatchingAtomType(mol, s);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(s, type);
        type = matcher.findMatchingAtomType(mol, o1);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(o1, type);
        type = matcher.findMatchingAtomType(mol, o2);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(o2, type);
        type = matcher.findMatchingAtomType(mol, o3);
    	assertNotNull(type);
    	AtomTypeManipulator.configure(o3, type);
        
        adder.addImplicitHydrogens(mol);

        assertEquals(4, mol.getAtomCount());
        assertEquals(3, mol.getBondCount());
        assertNotNull(s.getHydrogenCount());
        assertEquals(0, s.getHydrogenCount().intValue());
        assertNotNull(o1.getHydrogenCount());
        assertEquals(1, o1.getHydrogenCount().intValue());
        assertNotNull(o2.getHydrogenCount());
        assertEquals(1, o2.getHydrogenCount().intValue());
        assertNotNull(o3.getHydrogenCount());
        assertEquals(0, o3.getHydrogenCount().intValue());

    }
    
    public void testAceticAcid() throws Exception {
        Molecule mol = new Molecule();
        Atom carbonylOxygen = new Atom("O");
        Atom hydroxylOxygen = new Atom("O");
        Atom methylCarbon = new Atom("C");
        Atom carbonylCarbon = new Atom("C");
        mol.addAtom(carbonylOxygen);
        mol.addAtom(hydroxylOxygen);
        mol.addAtom(methylCarbon);
        mol.addAtom(carbonylCarbon);
        Bond b1 = new Bond(methylCarbon, carbonylCarbon, IBond.Order.SINGLE);
        Bond b2 = new Bond(carbonylOxygen, carbonylCarbon, IBond.Order.DOUBLE);
        Bond b3 = new Bond(hydroxylOxygen, carbonylCarbon, IBond.Order.SINGLE);
        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertEquals(4, mol.getAtomCount());
        assertEquals(3, mol.getBondCount());
        assertEquals(0, carbonylOxygen.getHydrogenCount().intValue());
        assertEquals(1, hydroxylOxygen.getHydrogenCount().intValue());
        assertEquals(3, methylCarbon.getHydrogenCount().intValue());
        assertEquals(0, carbonylCarbon.getHydrogenCount().intValue());
    }

    public void testEthane() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.SINGLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        assertEquals(3, carbon1.getHydrogenCount().intValue());
        assertEquals(3, carbon2.getHydrogenCount().intValue());
    }

    public void testEthene() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.DOUBLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        assertEquals(2, carbon1.getHydrogenCount().intValue());
        assertEquals(2, carbon2.getHydrogenCount().intValue());
    }

    public void testEthyne() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon1 = new Atom("C");
        Atom carbon2 = new Atom("C");
        Bond b = new Bond(carbon1, carbon2, IBond.Order.TRIPLE);
        mol.addAtom(carbon1);
        mol.addAtom(carbon2);
        mol.addBond(b);
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        assertEquals(1, carbon1.getHydrogenCount().intValue());
        assertEquals(1, carbon2.getHydrogenCount().intValue());
    }

    public void testAromaticSaturation() throws Exception {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5
        mol.addAtom(new Atom("C")); // 6
        mol.addAtom(new Atom("C")); // 7
        
        
        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.SINGLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.SINGLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.SINGLE); // 6
        mol.addBond(0, 6, IBond.Order.SINGLE); // 7
        mol.addBond(6, 7, IBond.Order.TRIPLE); // 8
        
        for (int f = 0; f < 6; f++) {
            mol.getAtom(f).setFlag(CDKConstants.ISAROMATIC, true);
            mol.getAtom(f).setHybridization(IAtomType.Hybridization.SP2);
            mol.getBond(f).setFlag(CDKConstants.ISAROMATIC, true);
        }
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        assertEquals(6, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }

    public void testaddImplicitHydrogensToSatisfyValency_OldValue() throws Exception {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        Atom oxygen = new Atom("O");
        mol.addAtom(oxygen);
        mol.addAtom(new Atom("C"));
        
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        
        oxygen.setHydrogenCount(2); /* e.g. caused by the fact that the element symbol
                                       was changed from C to O (=actual bug) */
        findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertNotNull(oxygen.getHydrogenCount());
        assertEquals(0, oxygen.getHydrogenCount().intValue());
    }

    public void testAdenine() throws Exception 
    {
    	IMolecule mol = new Molecule(); // Adenine
    	IAtom a1 = mol.getBuilder().newAtom("C");
    	a1.setPoint2d(new Point2d(21.0223, -17.2946));  mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("C");
    	a2.setPoint2d(new Point2d(21.0223, -18.8093));  mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("C");
    	a3.setPoint2d(new Point2d(22.1861, -16.6103));  mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("N");
    	a4.setPoint2d(new Point2d(19.8294, -16.8677));  mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("N");
    	a5.setPoint2d(new Point2d(22.2212, -19.5285));  mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("N");
    	a6.setPoint2d(new Point2d(19.8177, -19.2187));  mol.addAtom(a6);
    	IAtom a7 = mol.getBuilder().newAtom("N");
    	a7.setPoint2d(new Point2d(23.4669, -17.3531));  mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("N");
    	a8.setPoint2d(new Point2d(22.1861, -15.2769));  mol.addAtom(a8);
    	IAtom a9 = mol.getBuilder().newAtom("C");
    	a9.setPoint2d(new Point2d(18.9871, -18.0139));  mol.addAtom(a9);
    	IAtom a10 = mol.getBuilder().newAtom("C");
    	a10.setPoint2d(new Point2d(23.4609, -18.8267));  mol.addAtom(a10);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.DOUBLE);
    	mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a1, a3, IBond.Order.SINGLE);
    	mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE);
    	mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, IBond.Order.SINGLE);
    	mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a2, a6, IBond.Order.SINGLE);
    	mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a3, a7, IBond.Order.DOUBLE);
    	mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a3, a8, IBond.Order.SINGLE);
    	mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a4, a9, IBond.Order.DOUBLE);
    	mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a5, a10, IBond.Order.DOUBLE);
    	mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a6, a9, IBond.Order.SINGLE);
    	mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a7, a10, IBond.Order.SINGLE);
    	mol.addBond(b11);

    	findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        assertEquals(5, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }

    /**
     * @cdk.bug 1727373
     *
     */
    public void testBug1727373() throws Exception {
        Molecule molecule = null;
        String filename = "data/mdl/carbocations.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        molecule = (Molecule)reader.read((ChemObject)new Molecule());
    	findAndConfigureAtomTypesForAllAtoms(molecule);
        adder.addImplicitHydrogens(molecule);
        assertEquals(2,molecule.getAtom(0).getHydrogenCount().intValue());
        assertEquals(0,molecule.getAtom(1).getHydrogenCount().intValue());
        assertEquals(1,molecule.getAtom(2).getHydrogenCount().intValue());
        assertEquals(2,molecule.getAtom(3).getHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 1575269
     */
    public void testBug1575269() throws Exception {
        String filename = "data/mdl/furan.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IMolecule molecule = (IMolecule)reader.read((ChemObject)new Molecule());
    	findAndConfigureAtomTypesForAllAtoms(molecule);
        adder.addImplicitHydrogens(molecule);
        assertEquals(1,molecule.getAtom(0).getHydrogenCount().intValue());
        assertEquals(1,molecule.getAtom(1).getHydrogenCount().intValue());
        assertEquals(1,molecule.getAtom(2).getHydrogenCount().intValue());
        assertEquals(1,molecule.getAtom(3).getHydrogenCount().intValue());
    }

    public void testNaCl() throws Exception {
        Molecule mol = new Molecule();
        Atom cl = new Atom("Cl");
        cl.setFormalCharge(-1);
        mol.addAtom(cl);
        Atom na = new Atom("Na");
        na.setFormalCharge(+1);
        mol.addAtom(na);
    	findAndConfigureAtomTypesForAllAtoms(mol);
        adder.addImplicitHydrogens(mol);
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(0, AtomContainerManipulator.getTotalHydrogenCount(mol));
        assertEquals(0, mol.getConnectedBondsCount(cl));
        assertEquals(0, cl.getHydrogenCount().intValue());
        assertEquals(0, mol.getConnectedBondsCount(na));
        assertEquals(0, na.getHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 1244612
     */
    public void testSulfurCompound_ImplicitHydrogens() throws Exception {
        String filename = "data/mdl/sulfurCompound.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new ChemFile());
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        assertEquals(1, containersList.size());

        IAtomContainer atomContainer_0 = (IAtomContainer)containersList.get(0);
        assertEquals(10, atomContainer_0.getAtomCount());
        IAtom sulfur = atomContainer_0.getAtom(1);
    	findAndConfigureAtomTypesForAllAtoms(atomContainer_0);
        adder.addImplicitHydrogens(atomContainer_0);
        assertEquals("S", sulfur.getSymbol());
        assertNotNull(sulfur.getHydrogenCount());
        assertEquals(0, sulfur.getHydrogenCount().intValue());
        assertEquals(3, atomContainer_0.getConnectedAtomsCount(sulfur));

        assertEquals(10, atomContainer_0.getAtomCount());

        assertNotNull(sulfur.getHydrogenCount());
        assertEquals(0, sulfur.getHydrogenCount().intValue());
        assertEquals(3, atomContainer_0.getConnectedAtomsCount(sulfur));
    }
    
    /**
     * @cdk.bug 1627763
     */
    public void testBug1627763() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(mol.getBuilder().newAtom("C"));
    	mol.addAtom(mol.getBuilder().newAtom("O"));
    	mol.addBond(mol.getBuilder().newBond(
    		mol.getAtom(0), 
    		mol.getAtom(1),
    		CDKConstants.BONDORDER_SINGLE)
    	);
        addExplicitHydrogens(mol);
        int hCount = 0;
        Iterator<IAtom> neighbors = mol.getConnectedAtomsList(mol.getAtom(0)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(3, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(1)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(1, hCount);
    }
    
    public void testMercaptan() throws Exception {
    	IMolecule mol = new Molecule();
    	mol.addAtom(mol.getBuilder().newAtom("C"));
    	mol.addAtom(mol.getBuilder().newAtom("C"));
    	mol.addAtom(mol.getBuilder().newAtom("C"));
    	mol.addAtom(mol.getBuilder().newAtom("S"));
    	mol.addBond(mol.getBuilder().newBond(
    		mol.getAtom(0), 
    		mol.getAtom(1),
    		CDKConstants.BONDORDER_DOUBLE)
    	);
    	mol.addBond(mol.getBuilder().newBond(
        	mol.getAtom(1), 
        	mol.getAtom(2),
       		CDKConstants.BONDORDER_SINGLE)
       	);
    	mol.addBond(mol.getBuilder().newBond(
       		mol.getAtom(2), 
       		mol.getAtom(3),
       		CDKConstants.BONDORDER_SINGLE)
       	);
        addExplicitHydrogens(mol);
        int hCount = 0;
        Iterator<IAtom> neighbors = mol.getConnectedAtomsList(mol.getAtom(0)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(2, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(1)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(1, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(2)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(2, hCount);
        hCount = 0;
        neighbors = mol.getConnectedAtomsList(mol.getAtom(3)).iterator(); 
        while (neighbors.hasNext()) {
        	if (neighbors.next().getSymbol().equals("H")) hCount++;
        }
        assertEquals(1, hCount);
    }

    private void findAndConfigureAtomTypesForAllAtoms(IAtomContainer container) throws CDKException {
    	Iterator<IAtom> atoms = container.atoms().iterator();
    	while (atoms.hasNext()) {
    		IAtom atom = atoms.next();
    		IAtomType type = matcher.findMatchingAtomType(container, atom);
        	assertNotNull(type);
        	AtomTypeManipulator.configure(atom, type);
    	}
    }
    
}


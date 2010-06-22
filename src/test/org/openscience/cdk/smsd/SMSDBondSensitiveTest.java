/* Copyright (C) 2009-2010 Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.smsd;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.global.BondType;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.tools.ExtAtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 * 
 * @cdk.module test-smsd
 * @cdk.require java1.6+
 */
public class SMSDBondSensitiveTest {

    private static IMolecule Napthalene;
    private static IMolecule Cyclohexane;
    private static IMolecule Benzene;

    @BeforeClass
    public static void setUp() throws CDKException {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
    }

    @Test
    public void testVFLib() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        SMSD sbf = new SMSD(Algorithm.VFLibMCS, true);
        sbf.init(Benzene, Benzene, true);
        sbf.setChemFilters(true, true, true);
        Assert.assertEquals(true, sbf.isSubgraph());

    }

    @Test
    public void testSubgraph() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        SMSD sbf = new SMSD(Algorithm.SubStructure, true);
        sbf.init(Benzene, Benzene, true);
        sbf.setChemFilters(false, false, false);
        Assert.assertEquals(true, sbf.isSubgraph());
    }

    @Test
    public void testCDKMCS() throws Exception {
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        SMSD ebimcs = new SMSD(Algorithm.CDKMCS, true);
        ebimcs.init(Benzene, Benzene, true);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs.getFirstMapping().size());
        Assert.assertEquals(true, ebimcs.isSubgraph());
    }

    @Test
    public void testMCSPlus() throws Exception {
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);

        SMSD ebimcs = new SMSD(Algorithm.MCSPlus, true);
        ebimcs.init(Cyclohexane, Benzene, true);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(true, ebimcs.isSubgraph());
    }

    @Test
    public void testSMSD() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        BondType.getInstance().setBondSensitiveFlag(true);
        SMSD ebimcs = new SMSD(Algorithm.DEFAULT, true);
        ebimcs.init(Cyclohexane, Benzene, true);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs.getFirstMapping().size());

        SMSD ebimcs1 = new SMSD(Algorithm.DEFAULT, true);
        ebimcs1.init(Benzene, Napthalene, true);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getFirstAtomMapping().size());
    }

    @Test
    public void testSMSDCyclohexaneBenzeneSubgraph() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        SMSD ebimcs = new SMSD(Algorithm.SubStructure, true);
        ebimcs.init(Cyclohexane, Benzene, true);
        ebimcs.setChemFilters(true, true, true);
        Assert.assertEquals(false, ebimcs.isSubgraph());
    }

    @Test
    public void testSMSDBondSensitive() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);
        SMSD ebimcs3 = new SMSD(Algorithm.DEFAULT, true);
        ebimcs3.init(Cyclohexane, Benzene, true);
        ebimcs3.setChemFilters(true, true, true);
        BondType bondType = BondType.getInstance();
        System.out.println(bondType.isBondSensitive());
        Assert.assertEquals(6, ebimcs3.getFirstAtomMapping().size());

        SMSD ebimcs4 = new SMSD(Algorithm.CDKMCS, true);
        ebimcs4.init(Benzene, Napthalene, true);
        ebimcs4.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs4.getFirstAtomMapping().size());
    }

    @Test
    public void testSMSDChemicalFilters() throws Exception {
        Napthalene = createNaphthalene();
        Cyclohexane = createCyclohexane();
        Benzene = createBenzene();
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Napthalene);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Cyclohexane);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(Benzene);

        CDKHueckelAromaticityDetector.detectAromaticity(Napthalene);
        CDKHueckelAromaticityDetector.detectAromaticity(Cyclohexane);
        CDKHueckelAromaticityDetector.detectAromaticity(Benzene);

        SMSD ebimcs1 = new SMSD(Algorithm.DEFAULT, true);
        ebimcs1.init(Napthalene, Benzene, true);
        ebimcs1.setChemFilters(true, true, true);
        Assert.assertEquals(6, ebimcs1.getAllMapping().size());
        Assert.assertEquals(false, ebimcs1.isSubgraph());
    }

    @Test
    public void testSingleMappingTesting() throws Exception {

        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C");
        QueryAtomContainer query = QueryAtomContainerCreator.createBasicQueryContainer(atomContainer);

        IMolecule mol2 = create4Toluene();

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);

        boolean bondSensitive = true;
        boolean removeHydrogen = true;
        boolean stereoMatch = true;
        boolean fragmentMinimization = true;
        boolean energyMinimization = true;

        SMSD comparison1 = new SMSD(Algorithm.DEFAULT, bondSensitive);
        comparison1.init(query, mol2, removeHydrogen);
        comparison1.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);

        Assert.assertEquals(true, comparison1.isSubgraph());
        Assert.assertEquals(1, comparison1.getAllMapping().size());


    }

    /**
     * frag is a subgraph of the het mol
     * @throws Exception
     */
    @Test
    public void testSMSDAdpAtpSubgraph() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String adp = "NC1=NC=NC2=C1N=CN2[C@@H]1O[C@H](COP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        String atp = "NC1=NC=NC2=C1N=CN2[C@@H]1O[C@H](COP(O)(=O)OP(O)(=O)OP(O)(O)=O)[C@@H](O)[C@H]1O";
        IAtomContainer mol1 = sp.parseSmiles(adp);
        IAtomContainer mol2 = sp.parseSmiles(atp);

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

//	Calling the main algorithm to perform MCS cearch

        CDKHueckelAromaticityDetector.detectAromaticity(mol1);
        CDKHueckelAromaticityDetector.detectAromaticity(mol2);

        boolean bondSensitive = true;
        boolean removeHydrogen = true;
        boolean stereoMatch = true;
        boolean fragmentMinimization = true;
        boolean energyMinimization = true;

        SMSD comparison = new SMSD(Algorithm.DEFAULT, bondSensitive);
        comparison.init(mol1, mol2, removeHydrogen);
        comparison.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);

//      Get modified Query and Target Molecules as Mappings will correspond to these molecules
        Assert.assertEquals(true, comparison.isSubgraph());
        Assert.assertEquals(2, comparison.getAllMapping().size());
        Assert.assertEquals(27, comparison.getFirstMapping().size());


    }

    @Test
    public void testSMSDLargeSubgraph() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String c03374 = "CC1=C(C=C)\\C(NC1=O)=C"
                + "\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                + "=C(CC2=C(CCC(O)=O)C(C)=C(N2)"
                + "\\C=C2NC(=O)C(C=C)=C/2C)N1";

        String c05787 = "CC1=C(C=C)\\C(NC1=O)=C"
                + "\\C1=C(C)C(CCC(=O)O[C@@H]2O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]2O)C(O)=O)"
                + "=C(CC2=C(CCC(=O)O[C@@H]3O[C@@H]"
                + "([C@@H](O)[C@H](O)[C@H]3O)C(O)=O)"
                + "C(C)=C(N2)"
                + "\\C=C2NC(=O)C(C=C)=C/2C)N1";

        IAtomContainer mol1 = sp.parseSmiles(c03374);
        IAtomContainer mol2 = sp.parseSmiles(c05787);

        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol1);
        ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol2);

        IAtomContainer source = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(mol1);
        IAtomContainer target = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(mol2);

//	Calling the main algorithm to perform MCS cearch

        CDKHueckelAromaticityDetector.detectAromaticity(source);
        CDKHueckelAromaticityDetector.detectAromaticity(target);

        boolean bondSensitive = true;
        boolean removeHydrogen = true;
        boolean stereoMatch = true;
        boolean fragmentMinimization = true;
        boolean energyMinimization = true;

        SMSD comparison = new SMSD(Algorithm.SubStructure, bondSensitive);
        comparison.init(source, target, removeHydrogen);
        comparison.setChemFilters(stereoMatch, fragmentMinimization, energyMinimization);


        Assert.assertEquals(true, comparison.isSubgraph());
        Assert.assertEquals(55, comparison.getFirstMapping().size());


    }

    private IMolecule create4Toluene() throws CDKException {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c7.setID("7");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);



        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c7, c4, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);

        return result;
    }

    public IMolecule createMethane() {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        result.addAtom(c1);

        return result;
    }

    public IMolecule createPropane() {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");


        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);



        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);



        return result;
    }

    public IMolecule createHexane() throws CDKException {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);


        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);

        return result;
    }

    public static IMolecule createBenzene() throws CDKException {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);


        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);


        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);



        return result;
    }
    //    public static Molecule createPyridine() {
//        Molecule result = new DefaultMolecule();
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//        Atom c5 = result.addAtom("C");
//        Atom c6 = result.addAtom("N");
//
//        result.connect(c1, c2, 1);
//        result.connect(c2, c3, 2);
//        result.connect(c3, c4, 1);
//        result.connect(c4, c5, 2);
//        result.connect(c5, c6, 1);
//        result.connect(c6, c1, 2);
//
//        return result;
//    }
//
//    public static Molecule createToluene() {
//        Molecule result = new DefaultMolecule();
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//        Atom c5 = result.addAtom("C");
//        Atom c6 = result.addAtom("C");
//        Atom c7 = result.addAtom("C");
//
//        result.connect(c1, c2, 1);
//        result.connect(c2, c3, 2);
//        result.connect(c3, c4, 1);
//        result.connect(c4, c5, 2);
//        result.connect(c5, c6, 1);
//        result.connect(c6, c1, 2);
//        result.connect(c7, c1, 1);
//
//        return result;
//    }
//
//    public static Molecule createPhenol() {
//        Molecule result = new DefaultMolecule();
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//        Atom c5 = result.addAtom("C");
//        Atom c6 = result.addAtom("C");
//        Atom c7 = result.addAtom("O");
//
//        result.connect(c1, c2, 1);
//        result.connect(c2, c3, 2);
//        result.connect(c3, c4, 1);
//        result.connect(c4, c5, 2);
//        result.connect(c5, c6, 1);
//        result.connect(c6, c1, 2);
//        result.connect(c7, c1, 1);
//
//        return result;
//    }
//

    public static IMolecule createNaphthalene() throws CDKException {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");
        IAtom c7 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("7");
        IAtom c8 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("8");
        IAtom c9 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("9");
        IAtom c10 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("10");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);
        result.addAtom(c7);
        result.addAtom(c8);
        result.addAtom(c9);
        result.addAtom(c10);



        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.DOUBLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.DOUBLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.DOUBLE);
        IBond bond7 = new Bond(c5, c7, IBond.Order.SINGLE);
        IBond bond8 = new Bond(c7, c8, IBond.Order.DOUBLE);
        IBond bond9 = new Bond(c8, c9, IBond.Order.SINGLE);
        IBond bond10 = new Bond(c9, c10, IBond.Order.DOUBLE);
        IBond bond11 = new Bond(c10, c6, IBond.Order.SINGLE);


        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);
        result.addBond(bond7);
        result.addBond(bond8);
        result.addBond(bond9);
        result.addBond(bond10);
        result.addBond(bond11);


        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);



        return result;
    }
//
//    public static Molecule createAcetone() {
//        Molecule result = new DefaultMolecule();
//        Atom c0 = result.addAtom("C");
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom o3 = result.addAtom("O");
//
//        result.connect(c0, c1, 1);
//        result.connect(c1, c2, 1);
//        result.connect(c1, o3, 2);
//
//        return result;
//    }
//
//    public static Molecule createNeopentane() {
//        Molecule result = new DefaultMolecule();
//        Atom c0 = result.addAtom("C");
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//
//        result.connect(c0, c1, 1);
//        result.connect(c0, c2, 1);
//        result.connect(c0, c3, 1);
//        result.connect(c0, c4, 1);
//
//        return result;
//    }
//
//    public static Molecule createCubane() {
//        Molecule result = new DefaultMolecule();
//        Atom c0 = result.addAtom("C");
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//        Atom c5 = result.addAtom("C");
//        Atom c6 = result.addAtom("C");
//        Atom c7 = result.addAtom("C");
//
//        result.connect(c0, c1, 1);
//        result.connect(c1, c2, 1);
//        result.connect(c2, c3, 1);
//        result.connect(c3, c0, 1);
//
//        result.connect(c4, c5, 1);
//        result.connect(c5, c6, 1);
//        result.connect(c6, c7, 1);
//        result.connect(c7, c4, 1);
//
//        result.connect(c0, c4, 1);
//        result.connect(c1, c5, 1);
//        result.connect(c2, c6, 1);
//        result.connect(c3, c7, 1);
//
//        return result;
//    }
//
//    public static Molecule createBicyclo220hexane() {
//        Molecule result = new DefaultMolecule();
//        Atom c0 = result.addAtom("C");
//        Atom c1 = result.addAtom("C");
//        Atom c2 = result.addAtom("C");
//        Atom c3 = result.addAtom("C");
//        Atom c4 = result.addAtom("C");
//        Atom c5 = result.addAtom("C");
//
//        result.connect(c0, c1, 1);
//        result.connect(c1, c2, 1);
//        result.connect(c2, c3, 1);
//        result.connect(c3, c4, 1);
//        result.connect(c4, c5, 1);
//        result.connect(c5, c0, 1);
//        result.connect(c2, c5, 1);
//
//        return result;
//    }
//
//    public static Molecule createEthylbenzeneWithSuperatom() {
//        Molecule result = Molecules.createBenzene();
//        Atom carbon1 = result.addAtom("C");
//        Atom carbon2 = result.addAtom("C");
//        Bond crossingBond = result.connect(result.getAtom(0), carbon1, 1);
//        result.connect(carbon1, carbon2, 1);
//
//        Superatom substructure = result.addSuperatom();
//        substructure.addAtom(carbon1);
//        substructure.addAtom(carbon2);
//        substructure.addCrossingBond(crossingBond);
//        substructure.setCrossingVector(crossingBond, 0.1, 0.1);
//        substructure.setLabel("Ethyl");
//
//        return result;
//    }
//

    public static IMolecule createCyclohexane() throws CDKException {

        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c1.setID("1");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c2.setID("2");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c3.setID("3");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c4.setID("4");
        IAtom c5 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c5.setID("5");
        IAtom c6 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        c6.setID("6");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);
        result.addAtom(c5);
        result.addAtom(c6);

        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c4, IBond.Order.SINGLE);
        IBond bond4 = new Bond(c4, c5, IBond.Order.SINGLE);
        IBond bond5 = new Bond(c5, c6, IBond.Order.SINGLE);
        IBond bond6 = new Bond(c6, c1, IBond.Order.SINGLE);


        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);
        result.addBond(bond4);
        result.addBond(bond5);
        result.addBond(bond6);


        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(result);



        return result;

    }

    public static IMolecule createCyclopropane() {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);


        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c3, c1, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);

        return result;
    }

    public static IMolecule createIsobutane() {
        IMolecule result = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class);

        IAtom c1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");

        result.addAtom(c1);
        result.addAtom(c2);
        result.addAtom(c3);
        result.addAtom(c4);


        IBond bond1 = new Bond(c1, c2, IBond.Order.SINGLE);
        IBond bond2 = new Bond(c2, c3, IBond.Order.SINGLE);
        IBond bond3 = new Bond(c2, c4, IBond.Order.SINGLE);

        result.addBond(bond1);
        result.addBond(bond2);
        result.addBond(bond3);

        return result;
    }
}

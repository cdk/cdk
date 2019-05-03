/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.reaction.type;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TestSuite that runs a test for the HeterolyticCleavageSBReactionTest.
 * Generalized Reaction: A-B => |[A-] +[B+] // [A+] + |[B-]. Depending on the bond order
 * the bond will be removed or simply the order decreased.
 *
 * @cdk.module test-reaction
 */
public class HeterolyticCleavageSBReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();
    private UniversalIsomorphismTester    uiTester;

    @Before
    public void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    /**
     *  The JUnit setup method
     */
    public HeterolyticCleavageSBReactionTest() throws Exception {
        setReaction(HeterolyticCleavageSBReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testHeterolyticCleavageSBReaction() throws Exception {
        IReactionProcess type = new HeterolyticCleavageSBReaction();
        Assert.assertNotNull(type);
    }

    /**
    * A unit test suite for JUnit. Reaction: propane.
    * CC!-!C => C[C+] + [C-]
    *           C[C-] + [C+]
    *
    * @cdk.inchi InChI=1/C3H8/c1-3-2/h3H2,1-2H3
    *
    * @return    The test suite
    */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        //Smiles("CCC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(0, 5, IBond.Order.SINGLE);
        molecule.addBond(1, 6, IBond.Order.SINGLE);
        molecule.addBond(1, 7, IBond.Order.SINGLE);
        molecule.addBond(2, 8, IBond.Order.SINGLE);
        molecule.addBond(2, 9, IBond.Order.SINGLE);
        molecule.addBond(2, 10, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(1, 5, IBond.Order.SINGLE);
        expected1.addBond(1, 6, IBond.Order.SINGLE);;
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C-]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(-1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        lpcheck.saturate(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

        //Smiles("C[C-]")
        expected1.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        expected2.getAtom(0).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        product2 = setOfReactions.getReaction(1).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: .
     * C[C+]!-!C => CC + [C+]
     *
     * @return    The test suite
     */
    @Test
    public void testCsp2ChargeSingleB() throws Exception {

    }

    /**
     * A unit test suite for JUnit. Reaction: Propene.
     * C=C!-!C => C=[C+] + [C-]
     *            C=[C-] + [C+]
     *
     * @cdk.inchi  InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testCsp2SingleB() throws Exception {
        //Smiles("C=CC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(1, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        molecule.addBond(2, 7, IBond.Order.SINGLE);
        molecule.addBond(2, 8, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C=[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C-]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(-1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        lpcheck.saturate(expected2);

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

        //Smiles("C=[C-]")
        expected1.getAtom(1).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        expected2.getAtom(0).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);

        product2 = setOfReactions.getReaction(1).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: .
     * C=[C+]!-!C => C=C + [C+]
     *
     * @return    The test suite
     */
    @Test
    public void testCspChargeSingleB() throws Exception {

    }

    /**
     * A unit test suite for JUnit. Reaction: Propyne.
     * C#C!-!C => C#[C+] + [C-]
     *            C#[C-] + [C+]
     *
     * @cdk.inchi InChI=1/C3H4/c1-3-2/h1H,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testCspSingleB() throws Exception {
        //Smiles("C#CC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.TRIPLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(2, 4, IBond.Order.SINGLE);
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C#[C-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.TRIPLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(+1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        lpcheck.saturate(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(1).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

        //Smiles("C#[C+]")
        expected1.getAtom(1).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C-]")
        expected2.getAtom(0).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: dimethylamine.
     * CN!-!C => C[N-] + [C+]
     *
     * @cdk.inchi  InChI=1/C2H7N/c1-3-2/h3H,1-2H3
     *
     * @return    The test suite
     */
    @Test
    public void testNsp3SingleB() throws Exception {
        //Smiles("CNC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(0, 5, IBond.Order.SINGLE);
        molecule.addBond(1, 6, IBond.Order.SINGLE);
        molecule.addBond(2, 7, IBond.Order.SINGLE);
        molecule.addBond(2, 8, IBond.Order.SINGLE);
        molecule.addBond(2, 9, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[N-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(1, 5, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(+1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: N-methylmethanimine.
     * C=N!-!C =>[C+] +  C=[N-]
     *
     * @cdk.inchi InChI=1/C2H5N/c1-3-2/h1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testNsp2SingleB() throws Exception {
        //Smiles("C=NC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        molecule.addBond(2, 7, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(0).setFormalCharge(+1);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("C=[N-]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.addAtom(builder.newInstance(IAtom.class, "N"));
        expected2.getAtom(1).setFormalCharge(-1);
        expected2.addBond(0, 1, IBond.Order.DOUBLE);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        lpcheck.saturate(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction:  methoxymethane.
     * CO!-!C =>  C[O-] + [C+]
     *
     * @cdk.inchi InChI=1/C2H6O/c1-3-2/h1-2H3
     *
     * @return    The test suite
     */
    @Test
    public void testOsp2SingleB() throws Exception {
        //Smiles("COC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(0, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        molecule.addBond(2, 7, IBond.Order.SINGLE);
        molecule.addBond(2, 8, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[O-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(+1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: fluoromethane.
     * F!-!C => [F-] + [C+]
     *
     * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     *
     * @return    The test suite
     */
    @Test
    public void testFspSingleB() throws Exception {
        //Smiles("FC")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "F"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavageSBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        //Smiles("[F-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "F"));
        expected1.getAtom(0).setFormalCharge(-1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("[C+]")
        IAtomContainer expected2 = builder.newInstance(IAtomContainer.class);
        expected2.addAtom(builder.newInstance(IAtom.class, "C"));
        expected2.getAtom(0).setFormalCharge(+1);
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addAtom(builder.newInstance(IAtom.class, "H"));
        expected2.addBond(0, 1, IBond.Order.SINGLE);
        expected2.addBond(0, 2, IBond.Order.SINGLE);
        expected2.addBond(0, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected2);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected2);
        Assert.assertTrue(uiTester.isIsomorph(product2, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: C-O => [C+] + [O-]
     * Manually put of the reactive center.
     *
     * @cdk.inchi  InChI=1/CH4O/c1-2/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new HeterolyticCleavageSBReaction();
        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually put the reactive center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit. Reaction: C-O => [C+] + [O-]
     * Test of mapped between the reactant and product. Only is mapped the reactive center.
     *
     * @cdk.inchi  InChI=1/CH4O/c1-2/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new HeterolyticCleavageSBReaction();
        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic search of the center active */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        makeSureAtomTypesAreRecognized(molecule);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);

        Assert.assertEquals(6, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product1.getAtom(0));
        IAtom mappedProductA2 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));

    }

    /**
     * Test to recognize if a IAtomContainer matcher correctly identifies the CDKAtomTypes.
     *
     * @param molecule          The IAtomContainer to analyze
     * @throws CDKException
     */
    private void makeSureAtomTypesAreRecognized(IAtomContainer molecule) throws Exception {

        Iterator<IAtom> atoms = molecule.atoms().iterator();
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
        while (atoms.hasNext()) {
            IAtom nextAtom = atoms.next();
            Assert.assertNotNull("Missing atom type for: " + nextAtom, matcher.findMatchingAtomType(molecule, nextAtom));
        }
    }

    /**
     * Get the example set of molecules.
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);//Smiles("CO")
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        try {
            addExplicitHydrogens(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            lpcheck.saturate(molecule);
            makeSureAtomTypesAreRecognized(molecule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOfReactants.addAtomContainer(molecule);
        return setOfReactants;
    }

    /**
     * Get the expected set of molecules.
     * TODO:reaction. Set the products
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExpectedProducts() {
        IAtomContainerSet setOfProducts = builder.newInstance(IAtomContainerSet.class);

        setOfProducts.addAtomContainer(null);
        return setOfProducts;
    }
}

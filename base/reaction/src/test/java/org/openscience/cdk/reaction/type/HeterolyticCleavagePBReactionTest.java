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
import org.openscience.cdk.Atom;
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
 * TestSuite that runs a test for the HeterolyticCleavagePBReactionTest.
 * Generalized Reaction: A=B => |[A-]-[B+] + [A+]-|[B-]. Depending of the bond order
 * the bond will be removed or simply the order decreased.
 *
 * @cdk.module test-reaction
 */
public class HeterolyticCleavagePBReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();
    private UniversalIsomorphismTester    uiTester;

    @Before
    public void setUpUITester() {
        uiTester = new UniversalIsomorphismTester();
    }

    public HeterolyticCleavagePBReactionTest() throws Exception {
        setReaction(HeterolyticCleavagePBReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testHeterolyticCleavagePBReaction() throws Exception {
        IReactionProcess type = new HeterolyticCleavagePBReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit. Reaction: Propene.
     * CC!=!C => C[C+][C-]
     *           C[C-][C+]
     *
     * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        //Smiles("CC=C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
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
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[C+][C-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(2).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(0, 5, IBond.Order.SINGLE);
        expected1.addBond(1, 6, IBond.Order.SINGLE);
        expected1.addBond(2, 7, IBond.Order.SINGLE);
        expected1.addBond(2, 8, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("C[C-][C+]")
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction:.
     * C[C+]!=!C => C[C+][C-]
     *           C[C-][C+]
     *
     * @return    The test suite
     */
    @Test
    public void testCspChargeDoubleB() throws Exception {

    }

    /**
     * A unit test suite for JUnit. Reaction: Allene.
     * C=C!=!C => C=[C+][C-]
     *            C=[C-][C+]
     *
     * @cdk.inchi InChI=1/C3H4/c1-3-2/h1-2H2
     *
     * @return    The test suite
     */
    @Test
    public void testCspDoubleB() throws Exception {
        //Smiles("C=C=C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C=[C+][C-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(2).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(2, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("C=[C-][C+]")
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, expected1));
    }

    /**
     * A unit test suite for JUnit. Reaction: Propyne.
     * CC#C => C[C+]=[C-]
     *         C[C-]=[C+]
     *
     * @cdk.inchi InChI=1/C3H4/c1-3-2/h1H,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testCspTripleB() throws Exception {
        //Smiles("CC#C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.TRIPLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(0, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[C+]=[C-]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(2).setFormalCharge(-1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(1, 2, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(0, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));

        //Smiles("C[C-]=[C+]")
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.getAtom(2).setFormalCharge(+1);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        product1 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: N-methylmethanimine.
     * CN!=!C => C[N-]-[C+]
     *
     * @cdk.inchi InChI=1/C2H5N/c1-3-2/h1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testNsp2DoubleB() throws Exception {
        //Smiles("CN=C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
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
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[N-]-[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(1).setFormalCharge(-1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(2).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 3, IBond.Order.SINGLE);
        expected1.addBond(0, 4, IBond.Order.SINGLE);
        expected1.addBond(0, 5, IBond.Order.SINGLE);
        expected1.addBond(2, 6, IBond.Order.SINGLE);
        expected1.addBond(2, 7, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: formonitrile.
     * N!#!C => [N-]=[C+]
     *
     * @cdk.inchi InChI=1/CHN/c1-2/h1H
     *
     * @return    The test suite
     */
    @Test
    public void testNspTripleB() throws Exception {
        //Smiles("N#C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.TRIPLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("[N-]=[C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(0).setFormalCharge(-1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: formaldehyde.
     * O!=!C => [O-][C+]
     *
     * @cdk.inchi  InChI=1/CH2O/c1-2/h1H2
     *
     * @return    The test suite
     */
    @Test
    public void testOspDoubleB() throws Exception {
        //Smiles("O=C")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new HeterolyticCleavagePBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("[O-][C+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "O"));
        expected1.getAtom(0).setFormalCharge(-1);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addBond(1, 3, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);
        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(uiTester.isIsomorph(product1, queryAtom));
    }

    /**
     * A unit test suite for JUnit. Reaction: C=O => [C+]-[O-]
     * Manually put of the reactive center.
     *
     * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new HeterolyticCleavagePBReaction();
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);

        /* C=O */
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);//Smiles("C=O")
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        addExplicitHydrogens(molecule);

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);
        setOfReactants.addAtomContainer(molecule);

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
        makeSureAtomTypesAreRecognized(molecule);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit. Reaction: C=O => [C+]-[O-] + [C-]-[O+]
     * Test of mapped between the reactant and product. Only is mapped the reactive center.
     *
     * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new HeterolyticCleavagePBReaction();
        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic search of the center active */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assert.assertEquals(4, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IAtom mappedProductA2 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product.getAtom(1));
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
    * A unit test suite for JUnit. Reaction:
    * C(H)(H)=O => [C+](H)(H)-[O-] + [C+](H)=O +
    * Automatic search of the reactive atoms and bonds.
    *
    * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
    *
    * @return    The test suite
    */
    @Test
    public void testBB_AutomaticSearchCentreActiveFormaldehyde() throws Exception {
        IReactionProcess type = new HeterolyticCleavagePBReaction();
        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic search of the reactive atoms and bonds */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        makeSureAtomTypesAreRecognized(molecule);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        Assert.assertEquals(1, setOfReactions.getReactionCount());

        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assert.assertTrue(uiTester.isIsomorph(getExpectedProducts().getAtomContainer(0), product));

    }

    /**
     * Get the example set of molecules.
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        try {
            addExplicitHydrogens(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            lpcheck.saturate(molecule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOfReactants.addAtomContainer(molecule);
        return setOfReactants;
    }

    /**
     * Get the expected set of molecules.
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExpectedProducts() {
        IAtomContainerSet setOfProducts = builder.newInstance(IAtomContainerSet.class);
        //Smiles("[C+](H)(H)-[O-]");
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        IAtom carbon = builder.newInstance(IAtom.class, "C");
        carbon.setFormalCharge(1);
        molecule.addAtom(carbon);
        IAtom oxyg = builder.newInstance(IAtom.class, "O");
        oxyg.setFormalCharge(-1);
        molecule.addAtom(oxyg);
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(0, 3, IBond.Order.SINGLE);

        setOfProducts.addAtomContainer(molecule);
        return setOfProducts;
    }
}

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
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TestSuite that runs a test for the PiBondingMovementReactionTest.
 * Generalized Reaction: C1=C(C)-C(C)=C-C=C1 -> C1(C)=C(C)-C=C-C=C1.
 *
 * FIXME: REACT: The tests fail if I don't put the smiles, strange
 *
 * @cdk.module test-reaction
 */
public class PiBondingMovementReactionTest extends ReactionProcessTest {

    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public PiBondingMovementReactionTest() throws Exception {
        setReaction(PiBondingMovementReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testPiBondingMovementReaction() {
        IReactionProcess type = new PiBondingMovementReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit with benzene.
     * Reaction:  C1=CC=CC=C1 -> C1(C)=C(C)-C=C-C=C1
     * Automatic search of the center active.
     *
     * InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=C(C)-C(C)=C-C=C1
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.DOUBLE);
        molecule.addBond(5, 0, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        makeSureAtomTypesAreRecognized(molecule);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule, queryAtom));

    }

    /**
     * A unit test suite for JUnit with 1,2-dimethylbenzene.
     * Reaction: C1=C(C)-C(C)=C-C=C1 -> C1(C)=C(C)-C=C-C=C1
     * Automatic search of the center active.
     *
     * InChI=1/C8H10/c1-7-5-3-4-6-8(7)2/h3-6H,1-2H3
     */
    @Test
    public void testAutomaticSearchCentreActiveExample1() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=C(C)-C(C)=C-C=C1
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 5, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(6, 7, IBond.Order.DOUBLE);
        molecule.addBond(7, 0, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        makeSureAtomTypesAreRecognized(molecule);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        //C1(C)=C(C)-C=C-C=C1
        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 2, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 3, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 4, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(4, 5, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(5, 6, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(6, 7, IBond.Order.DOUBLE);
        molecule2.addBond(7, 0, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
        makeSureAtomTypesAreRecognized(molecule2);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

    }

    static boolean matches(IAtomContainer a, IAtomContainer b) throws CDKException {
        IQueryAtomContainer query = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(b);
        return new UniversalIsomorphismTester().isIsomorph(a, query);
    }

    /**
     * A unit test suite for JUnit with 2-methylnaphthalene.
     * Reaction: C1=CC(=CC2=C1C=CC=C2)C
     * -> C1=CC(=CC2=CC=CC=C12)C + C1=C2C(=CC(=C1)C)C=CC=C2
     * Automatic search of the center active.
     *
     * InChI=1/C11H10/c1-9-6-7-10-4-2-3-5-11(10)8-9/h2-8H,1H3
     */
    @Test
    public void testDoubleRingConjugated() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=CC(=CC2=C1C=CC=C2)C
        IAtomContainerSet setOfReactants = getExampleReactants();

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IAtomContainer molecule1 = getExpectedProducts().getAtomContainer(0);

        Assert.assertEquals(1, setOfReactions.getReaction(1).getProductCount());

        IAtomContainer product2 = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        //C1=CC(=CC2=CC=CC=C12)C
        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(1, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(2, 3, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(3, 4, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(4, 5, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(5, 6, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(6, 7, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(7, 8, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(8, 9, IBond.Order.DOUBLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addBond(9, 10, IBond.Order.SINGLE);
        molecule2.addBond(10, 1, IBond.Order.DOUBLE);
        molecule2.addBond(9, 4, IBond.Order.SINGLE);

        addExplicitHydrogens(molecule2);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule2);
        makeSureAtomTypesAreRecognized(molecule2);

        // order depends on ring perception order (may change between versions) we check both
        // combinations just in case
        Assert.assertTrue((matches(molecule2, product1) && matches(molecule1, product2))
                || (matches(molecule1, product1) && matches(molecule2, product2)));

    }

    /**
     * A unit test suite for JUnit with 2-methylnaphthalene.
     * Reaction: C1=CC(=CC2=C1C=CC=C2)C
     * -> C1=CC(=CC2=CC=CC=C12)C + {NO => C1=C2C(=CC(=C1)C)C=CC=C2}
     *
     * restricted the reaction center.
     *
     * InChI=1/C11H10/c1-9-6-7-10-4-2-3-5-11(10)8-9/h2-8H,1H3
     *
     */
    @Test
    public void testDoubleRingConjugated2() throws Exception {
        IReactionProcess type = new PiBondingMovementReaction();
        // C1=CC(=CC2=C1C=CC=C2)C

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually putting the reaction center */
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(9).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(10).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(11).setFlag(CDKConstants.REACTIVE_CENTER, true);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

    }

    /**
     * Create one of the resonance for 2-methylnaphthalene.
     * C1=CC(=CC2=C1C=CC=C2)C
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        // C{0}1=C{1}C{2}(=C{3}C{4}2=C{5}1C{6}=C{7}C{8}=C{9}2)C{10}
        // C1=CC(=CC2=C1C=CC=C2)C
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(7, 8, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(8, 9, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(9, 10, IBond.Order.SINGLE);
        molecule.addBond(10, 1, IBond.Order.DOUBLE);
        molecule.addBond(9, 4, IBond.Order.DOUBLE);

        try {
            addExplicitHydrogens(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            makeSureAtomTypesAreRecognized(molecule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOfReactants.addAtomContainer(molecule);
        return setOfReactants;
    }

    /**
     * Get the expected set of molecules. 2-methylnaphthalene.
     * C=1C=CC2=CC(=CC=C2(C=1))C
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExpectedProducts() {
        IAtomContainerSet setOfProducts = builder.newInstance(IAtomContainerSet.class);

        //C=1C=CC2=CC(=CC=C2(C=1))C
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(3, 4, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(5, 6, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(6, 7, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(7, 8, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(8, 9, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(9, 10, IBond.Order.DOUBLE);
        molecule.addBond(10, 1, IBond.Order.SINGLE);
        molecule.addBond(9, 4, IBond.Order.SINGLE);

        try {
            addExplicitHydrogens(molecule);
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            makeSureAtomTypesAreRecognized(molecule);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOfProducts.addAtomContainer(molecule);
        return setOfProducts;
    }

    /**
     * Test to recognize if a IAtomContainer matcher correctly identifies the CDKAtomTypes.
     *
     * @param molecule          The IAtomContainer to analyze
     */
    private void makeSureAtomTypesAreRecognized(IAtomContainer molecule) throws Exception {

        Iterator<IAtom> atoms = molecule.atoms().iterator();
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
        while (atoms.hasNext()) {
            IAtom nextAtom = atoms.next();
            Assert.assertNotNull("Missing atom type for: " + nextAtom, matcher.findMatchingAtomType(molecule, nextAtom));
        }
    }
}

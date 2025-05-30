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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * TestSuite that runs a test for the RearrangementCationReactionTest.
 * Generalized Reaction: [A+]-B=C => A=B-[c+].
 *
 */
public class RearrangementCationReactionTest extends ReactionProcessTest {

    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    RearrangementCationReactionTest() throws Exception {
        setReaction(RearrangementCationReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    void testRearrangementCationReaction() throws Exception {
        IReactionProcess type = new RearrangementCationReaction();
        Assertions.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
     * Automatic search of the center active.
     *
     *
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        IReactionProcess type = new RearrangementCationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);
        /* initiate */

        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assertions.assertEquals(1, setOfReactions.getReactionCount());
        Assertions.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        Assertions.assertEquals(1, product.getAtom(2).getFormalCharge().intValue());
        Assertions.assertEquals(0, product.getConnectedLonePairsCount(product.getAtom(1)));

        /* C=C-[C+]-C */
        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);
        assertEquals(molecule2, product);
    }

    /**
     * A unit test suite for JUnit. Reaction: [C+]-C=C-C => C=C-[C+]-C
     * Manually put of the center active.
     *
     *
     */
    @Test
    void testManuallyCentreActive() throws Exception {
        IReactionProcess type = new RearrangementCationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually put the center active */
        molecule.getAtom(0).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(IChemObject.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assertions.assertEquals(1, setOfReactions.getReactionCount());
        Assertions.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        /* C=C-[C+]-C */
        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        assertEquals(molecule2, product);
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new RearrangementCationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually put the reactive center */
        molecule.getAtom(0).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(IChemObject.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(IChemObject.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assertions.assertTrue(molecule.getAtom(0).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(reactant.getAtom(0).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(molecule.getAtom(1).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(reactant.getAtom(1).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(molecule.getAtom(2).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(reactant.getAtom(2).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(molecule.getBond(0).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(reactant.getBond(0).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(molecule.getBond(1).getFlag(IChemObject.REACTIVE_CENTER));
        Assertions.assertTrue(reactant.getBond(1).getFlag(IChemObject.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit.
     *
     *
     */
    @Test
    void testMapping() throws Exception {
        IReactionProcess type = new RearrangementCationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic search of the center active */
        List<IParameterReact> paramList = new ArrayList<>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assertions.assertEquals(11, setOfReactions.getReaction(0).getMappingCount());

        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assertions.assertEquals(mappedProductA1, product.getAtom(0));
        mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assertions.assertEquals(mappedProductA1, product.getAtom(1));
        mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(2));
        Assertions.assertEquals(mappedProductA1, product.getAtom(2));
    }

    /**
     * Test to recognize if this IAtomContainer_1 matches correctly into the CDKAtomTypes.
     */
    @Test
    void testAtomTypesAtomContainer1() throws Exception {
        IAtomContainer moleculeTest = getExampleReactants().getAtomContainer(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(moleculeTest);
        makeSureAtomTypesAreRecognized(moleculeTest);

    }

    /**
     * Test to recognize if this IAtomContainer_2 matches correctly into the CDKAtomTypes.
     */
    @Test
    void testAtomTypesAtomContainer2() throws Exception {
        IAtomContainer moleculeTest = getExpectedProducts().getAtomContainer(0);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(moleculeTest);
        makeSureAtomTypesAreRecognized(moleculeTest);

    }

    /**
     * get the molecule 1: [C+]-C=C-C
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.getAtom(0).setFormalCharge(1);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        try {
            addExplicitHydrogens(molecule);
        } catch (Exception e) {
            LoggingToolFactory.createLoggingTool(getClass())
                              .error("Unexpected Error:", e);
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

        // C=C-[C+]-C
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.getAtom(2).setFormalCharge(1);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(2, 3, IBond.Order.SINGLE);
        try {
            addExplicitHydrogens(molecule);
        } catch (Exception e) {
            LoggingToolFactory.createLoggingTool(getClass())
                              .error("Unexpected Error:", e);
        }

        setOfProducts.addAtomContainer(molecule);
        return setOfProducts;
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
            Assertions.assertNotNull(matcher.findMatchingAtomType(molecule, nextAtom), "Missing atom type for: " + nextAtom);
        }
    }
}

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
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * TestSuite that runs a test for the RearrangementRadicalReactionTest.
 * Generalized Reaction: [A*+]-B-C => [A+]=B + [c*].
 *
 * @cdk.module test-reaction
 */
public class RadicalChargeSiteInitiationReactionTest extends ReactionProcessTest {

    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public RadicalChargeSiteInitiationReactionTest() throws Exception {
        setReaction(RadicalChargeSiteInitiationReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testRadicalChargeSiteInitiationReaction() throws Exception {
        IReactionProcess type = new RadicalChargeSiteInitiationReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit. Reaction: [O+*]C([H])([H])C([H])([H])([H]) => [O+]=C([H])([H]) +[C*]([H])([H])([H])
     * Automatic search of the center active.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        IReactionProcess type = new RadicalChargeSiteInitiationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();

        /* initiate */

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule1 = getExpectedProducts().getAtomContainer(0);

        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(molecule1);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(product1, queryAtom));

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(1);

        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new RadicalChargeSiteInitiationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually put the reactive center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getAtom(2).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(2).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new RadicalChargeSiteInitiationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        /* initiate */

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);

        Assert.assertEquals(9, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product1.getAtom(1));
        IAtom mappedProductA2 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(2));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));
        IAtom mappedProductA3 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA3, product1.getAtom(0));

    }

    /**
     * Get the IAtomContainer
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(2, 7, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 8, IBond.Order.SINGLE);

        IAtom atom = molecule.getAtom(0);
        atom.setFormalCharge(1);
        molecule.addSingleElectron(new SingleElectron(atom));

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
        IAtomContainer molecule1 = builder.newInstance(IAtomContainer.class);
        molecule1.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule1.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule1.addBond(0, 1, IBond.Order.DOUBLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(1, 2, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(1, 3, IBond.Order.SINGLE);
        molecule1.getAtom(0).setFormalCharge(1);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(0, 4, IBond.Order.SINGLE);
        setOfProducts.addAtomContainer(molecule1);

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);
        molecule2.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule2.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule2.addBond(0, 1, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule2.addBond(0, 2, IBond.Order.SINGLE);
        molecule2.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule2.addBond(0, 3, IBond.Order.SINGLE);
        molecule2.addSingleElectron(new SingleElectron(molecule2.getAtom(0)));
        setOfProducts.addAtomContainer(molecule2);

        return setOfProducts;
    }
}

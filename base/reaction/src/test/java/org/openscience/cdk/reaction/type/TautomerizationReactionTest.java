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
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.ReactionProcessTest;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * TestSuite that runs a test for the TautomerizationReactionTest.
 * Generalized Reaction: X=Y-Z-H => X(H)-Y=Z.
 *
 * @cdk.module test-reaction
 */
public class TautomerizationReactionTest extends ReactionProcessTest {

    private IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public TautomerizationReactionTest() throws Exception {
        setReaction(TautomerizationReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testTautomerizationReaction() throws Exception {
        IReactionProcess type = new TautomerizationReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit for acetaldehyde.
     * Reaction: O=C-C-H => O(H)-C=C.
     * Automatic looking for active center.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {

        IReactionProcess type = new TautomerizationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

        // reverse process
        IAtomContainerSet setOfReactants2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants2.addAtomContainer(molecule2);

        IReactionSet setOfReactions2 = type.initiate(setOfReactants2, null);

        Assert.assertEquals(1, setOfReactions2.getReactionCount());
        Assert.assertEquals(1, setOfReactions2.getReaction(0).getProductCount());

        IAtomContainer product2 = setOfReactions2.getReaction(0).getProducts().getAtomContainer(0);

        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule, queryAtom));
    }

    /**
     * A unit test suite for JUnit for acetaldehyde.
     * Reaction: O=C-C-H => O(H)-C=C.
     * Manually putting for active center.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testManuallyCentreActive() throws Exception {
        IReactionProcess type = new TautomerizationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually putting the active center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER, true);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

        // reverse process
        /* manually putting the active center */
        molecule2.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getAtom(6).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule2.getBond(5).setFlag(CDKConstants.REACTIVE_CENTER, true);
        IAtomContainerSet setOfReactants2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants2.addAtomContainer(molecule2);

        IReactionSet setOfReactions2 = type.initiate(setOfReactants2, null);

        Assert.assertEquals(1, setOfReactions2.getReactionCount());
        Assert.assertEquals(1, setOfReactions2.getReaction(0).getProductCount());

        IAtomContainer product2 = setOfReactions2.getReaction(0).getProducts().getAtomContainer(0);

        queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule, queryAtom));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new TautomerizationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually putting the active center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(2).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER, true);

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
        Assert.assertTrue(molecule.getAtom(4).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(4).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(3).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(3).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new TautomerizationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic looking for active center */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        /* initiate */

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assert.assertEquals(7, setOfReactions.getReaction(0).getMappingCount());

        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product.getAtom(0));
        mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product.getAtom(1));
        mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(2));
        Assert.assertEquals(mappedProductA1, product.getAtom(2));
        mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(4));
        Assert.assertEquals(mappedProductA1, product.getAtom(4));

    }

    /**
     * Get the Acetaldehyde structure.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2H,1H3
     *
     * @return The IAtomContainer
     * @throws CDKException
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(2, 4, IBond.Order.SINGLE);
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addBond(2, 6, IBond.Order.SINGLE);
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        setOfReactants.addAtomContainer(molecule);
        return setOfReactants;
    }

    /**
     * Get the expected set of molecules.
     *
     * @cdk.inchi InChI=1/C2H4O/c1-2-3/h2-3H,1H2
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExpectedProducts() {
        IAtomContainerSet setOfProducts = builder.newInstance(IAtomContainerSet.class);

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(1, 2, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(2, 4, IBond.Order.SINGLE);
        molecule.addBond(2, 5, IBond.Order.SINGLE);
        molecule.addBond(0, 6, IBond.Order.SINGLE);
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        setOfProducts.addAtomContainer(molecule);
        return setOfProducts;
    }
}

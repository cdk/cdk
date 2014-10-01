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
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.List;

/**
 * TestSuite that runs a test for the AdductionProtonPBReactionTest.
 * Generalized Reaction: A=B + [H+] => [A+]-B-H.
 *
 * @cdk.module test-reaction
 */
public class AdductionProtonPBReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public AdductionProtonPBReactionTest() throws Exception {
        setReaction(AdductionProtonPBReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testAdductionProtonPBReaction() throws Exception {
        IReactionProcess type = new AdductionProtonPBReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit for Ethene.
     * Reaction: O=C-C-H => O(H)-C=C.
     * Automatic looking for active center.
     *
     * @cdk.inchi InChI=1/C2H4/c1-2/h1-2H2
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {

        IReactionProcess type = new AdductionProtonPBReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

    }

    /**
     * A unit test suite for JUnit for Ethene.
     * Reaction: O=C-C-H => O(H)-C=C.
     * Manually putting for active center.
     *
     * @cdk.inchi InChI=1/C2H4/c1-2/h1-2H2
     *
     * @return    The test suite
     */
    @Test
    public void testManuallyCentreActive() throws Exception {
        IReactionProcess type = new AdductionProtonPBReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually putting the active center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(0);

        IQueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(product);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, queryAtom));

    }

    /**
    * A unit test suite for JUnit.
    *
    * @cdk.inchi InChI=1/C2H4/c1-2/h1-2H2
    *
    * @return    The test suite
    */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new AdductionProtonPBReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* manually putting the active center */
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

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assert.assertTrue(molecule.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(molecule.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getBond(0).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit.
     *
     * @cdk.inchi InChI=1/C2H4/c1-2/h1-2H2
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new AdductionProtonPBReaction();

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

    }

    /**
     * Get the Ethene structure.
     *
     * @return The IAtomContainer
     * @throws CDKException
     */
    private IAtomContainerSet getExampleReactants() {
        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addBond(1, 5, IBond.Order.SINGLE);
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

            lpcheck.saturate(molecule);
        } catch (CDKException e) {
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
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.getAtom(0).setFormalCharge(1);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addBond(1, 5, IBond.Order.SINGLE);
        molecule.addBond(1, 6, IBond.Order.SINGLE);
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);

            lpcheck.saturate(molecule);
        } catch (CDKException e) {
            e.printStackTrace();
        }

        setOfProducts.addAtomContainer(molecule);
        return setOfProducts;
    }
}

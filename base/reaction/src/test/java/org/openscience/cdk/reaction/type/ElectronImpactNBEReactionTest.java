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
import org.openscience.cdk.interfaces.ISingleElectron;
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
 * TestSuite that runs a test for the ElectronImpactNBEReactionTest.
 *
 * @cdk.module test-reaction
 */

public class ElectronImpactNBEReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public ElectronImpactNBEReactionTest() throws Exception {
        setReaction(ElectronImpactNBEReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testElectronImpactNBEReaction() throws Exception {
        IReactionProcess type = new ElectronImpactNBEReaction();
        Assert.assertNotNull(type);
    }

    /**
     *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
     *
     * @return    Description of the Return Value
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        /* ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC , set the reactive center */

        IAtomContainer reactant = builder.newInstance(IAtomContainer.class);//Smiles("C=CCC(=O)CC")
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "O"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addBond(0, 1, IBond.Order.DOUBLE);
        reactant.addBond(1, 2, IBond.Order.SINGLE);
        reactant.addBond(2, 3, IBond.Order.SINGLE);
        reactant.addBond(3, 4, IBond.Order.DOUBLE);
        reactant.addBond(3, 5, IBond.Order.SINGLE);
        reactant.addBond(5, 6, IBond.Order.SINGLE);
        addExplicitHydrogens(reactant);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        lpcheck.saturate(reactant);

        Iterator<IAtom> atoms = reactant.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (reactant.getConnectedLonePairsCount(atom) > 0) {
                atom.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(reactant);

        /* initiate */
        makeSureAtomTypesAreRecognized(reactant);

        IReactionProcess type = new ElectronImpactNBEReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer molecule = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(4)));

        Assert.assertTrue(setOfReactions.getReaction(0).mappings().iterator().hasNext());

    }

    /**
     *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
     *
     * @return    Description of the Return Value
     */
    @Test
    public void testAutomatic_Set_Active_Atom() throws Exception {
        /*
         * ionize(>C=O): C=CCC(=O)CC -> C=CCC(=O*)CC, without setting the
         * reactive center
         */
        IAtomContainer reactant = builder.newInstance(IAtomContainer.class);//Smiles("C=CCC(=O)CC")
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "O"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addBond(0, 1, IBond.Order.DOUBLE);
        reactant.addBond(1, 2, IBond.Order.SINGLE);
        reactant.addBond(2, 3, IBond.Order.SINGLE);
        reactant.addBond(3, 4, IBond.Order.DOUBLE);
        reactant.addBond(3, 5, IBond.Order.SINGLE);
        reactant.addBond(5, 6, IBond.Order.SINGLE);
        addExplicitHydrogens(reactant);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        lpcheck.saturate(reactant);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(reactant);

        /* initiate */
        makeSureAtomTypesAreRecognized(reactant);

        IReactionProcess type = new ElectronImpactNBEReaction();
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer molecule = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(4).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(4)));

    }

    /**
     * A unit test suite for JUnit. Reaction: methanamine.
     * C-!N! => C[N*+]
     *
     * @cdk.inchi  InChI=1/CH5N/c1-2/h2H2,1H3
     *
     * @return    The test suite
     */
    @Test
    public void testNsp3SingleB() throws Exception {
        //Smiles("CN")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        molecule.addBond(1, 5, IBond.Order.SINGLE);
        molecule.addBond(1, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new ElectronImpactNBEReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("C[N*+]")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(1).setFormalCharge(+1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(1)));
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
        expected1.addBond(1, 6, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(product1, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: Methanimine.
     * C=!N! => C=[N*+]
     *
     * @cdk.inchi  InChI=1/CH3N/c1-2/h2H,1H2
     *
     * @return    The test suite
     */
    @Test
    public void testNsp2SingleB() throws Exception {
        //Smiles("C=N")
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "N"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        lpcheck.saturate(molecule);

        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new ElectronImpactNBEReaction();

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("[N*+]=C")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "N"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.DOUBLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(0, 2, IBond.Order.SINGLE);
        expected1.addBond(1, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(product1, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: fluoromethane.
     * F!-!C => [F*+]C
     *
     * @cdk.inchi InChI=1/CH3F/c1-2/h1H3
     *
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

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(molecule);

        IReactionProcess type = new ElectronImpactNBEReaction();

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());

        // expected products

        //Smiles("[F*+]C")
        IAtomContainer expected1 = builder.newInstance(IAtomContainer.class);
        expected1.addAtom(builder.newInstance(IAtom.class, "F"));
        expected1.getAtom(0).setFormalCharge(1);
        expected1.addSingleElectron(builder.newInstance(ISingleElectron.class, expected1.getAtom(0)));
        expected1.addAtom(builder.newInstance(IAtom.class, "C"));
        expected1.addBond(0, 1, IBond.Order.SINGLE);
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addAtom(builder.newInstance(IAtom.class, "H"));
        expected1.addBond(1, 2, IBond.Order.SINGLE);
        expected1.addBond(1, 3, IBond.Order.SINGLE);
        expected1.addBond(1, 4, IBond.Order.SINGLE);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(expected1);
        lpcheck.saturate(expected1);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        QueryAtomContainer queryAtom = QueryAtomContainerCreator.createSymbolAndChargeQueryContainer(expected1);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(product1, queryAtom));

    }

    /**
     * A unit test suite for JUnit. Reaction: C=O => C=[O*+]
     * Manually put of the reactive center.
     *
     * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new ElectronImpactNBEReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);
        /* manually put the reactive center */
        molecule.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);

        /* initiate */
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(1, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer reactant = setOfReactions.getReaction(0).getReactants().getAtomContainer(0);
        Assert.assertTrue(molecule.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
        Assert.assertTrue(reactant.getAtom(1).getFlag(CDKConstants.REACTIVE_CENTER));
    }

    /**
     * A unit test suite for JUnit. Reaction: C=O => C=[O*+]
     * Manually put of the reactive center.
     *
     * @cdk.inchi InChI=1/CH2O/c1-2/h1H2
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new ElectronImpactNBEReaction();
        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* automatic search of the center active */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assert.assertEquals(4, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA1, product.getAtom(1));
    }

    /**
     * Test to recognize if a IAtomContainer matcher correctly the CDKAtomTypes.
     *
     * @param molecule          The IAtomContainer to analyze
     * @throws CDKException
     */
    private void makeSureAtomTypesAreRecognized(IAtomContainer molecule) throws CDKException {

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
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);//Smiles("C=O")
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "O"));
        molecule.addBond(0, 1, IBond.Order.DOUBLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addBond(0, 3, IBond.Order.SINGLE);

        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
            lpcheck.saturate(molecule);
            makeSureAtomTypesAreRecognized(molecule);
        } catch (CDKException e) {
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

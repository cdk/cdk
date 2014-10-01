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
import org.openscience.cdk.LonePair;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
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
 * TestSuite that runs a test for the CarbonylEliminationReactionTest.
 * Generalized Reaction: RC-C#[O+] => R[C] + |C#[O+]
 *
 * @cdk.module test-reaction
 */
public class CarbonylEliminationReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public CarbonylEliminationReactionTest() throws Exception {
        setReaction(CarbonylEliminationReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testCarbonylEliminationReaction() throws Exception {
        IReactionProcess type = new CarbonylEliminationReaction();
        Assert.assertNotNull(type);
    }

    /**
     * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
     * Automatically looks for active centre.
     *
     * @return    The test suite
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {

        IReactionProcess type = new CarbonylEliminationReaction();
        /* [C*]-C-C */
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
        IAtomContainer molecule1 = getExpectedProducts().getAtomContainer(0);//Smiles("[C+]");
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule1, product1));

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(1);//Smiles("[C-]#[O+]");
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, product2));

    }

    /**
     * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
     * Automatically looks for active centre.
     *
     * @return    The test suite
     */
    @Test
    public void testManuallyPCentreActiveExample1() throws Exception {

        IReactionProcess type = new CarbonylEliminationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* initiate */
        /* manually put the reactive center */
        molecule.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(4).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getAtom(5).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(3).setFlag(CDKConstants.REACTIVE_CENTER, true);
        molecule.getBond(4).setFlag(CDKConstants.REACTIVE_CENTER, true);

        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(1, setOfReactions.getReactionCount());
        Assert.assertEquals(2, setOfReactions.getReaction(0).getProductCount());

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IAtomContainer molecule1 = getExpectedProducts().getAtomContainer(0);//Smiles("[C+]");
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule1, product1));

        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);
        IAtomContainer molecule2 = getExpectedProducts().getAtomContainer(1);//Smiles("[C-]#[O+]");
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(molecule2, product2));

    }

    /**
     * A unit test suite for JUnit. Reaction: C-C#[O+] => [C+] + [|C-]#[O+]
     * Automatically looks for active centre.
     *
     * @return    The test suite
     */
    @Test
    public void testMappingExample1() throws Exception {

        IReactionProcess type = new CarbonylEliminationReaction();

        IAtomContainerSet setOfReactants = getExampleReactants();
        IAtomContainer molecule = setOfReactants.getAtomContainer(0);

        /* initiate */
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        IAtomContainer product1 = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        IAtomContainer product2 = setOfReactions.getReaction(0).getProducts().getAtomContainer(1);

        Assert.assertEquals(6, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product1.getAtom(0));
        IAtom mappedProductA2 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(4));
        Assert.assertEquals(mappedProductA2, product2.getAtom(0));

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
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);//Smiles("C-C#[O+]")
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 2, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 3, IBond.Order.SINGLE);
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addBond(0, 4, IBond.Order.SINGLE);
        IAtom oxy = builder.newInstance(IAtom.class, "O");
        oxy.setFormalCharge(1);
        molecule.addAtom(oxy);
        molecule.addBond(4, 5, IBond.Order.TRIPLE);

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
     *
     * @return The IAtomContainerSet
     */
    private IAtomContainerSet getExpectedProducts() {
        IAtomContainerSet setOfProducts = builder.newInstance(IAtomContainerSet.class);

        IAtomContainer molecule1 = builder.newInstance(IAtomContainer.class);//Smiles("[C+]");
        IAtom carb = builder.newInstance(IAtom.class, "C");
        carb.setFormalCharge(1);
        molecule1.addAtom(carb);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(0, 1, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(0, 2, IBond.Order.SINGLE);
        molecule1.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule1.addBond(0, 3, IBond.Order.SINGLE);

        IAtomContainer molecule2 = builder.newInstance(IAtomContainer.class);//Smiles("[C-]#[O+]");
        carb = builder.newInstance(IAtom.class, "C");
        carb.setFormalCharge(-1);
        molecule2.addLonePair(new LonePair(carb));
        molecule2.addAtom(carb);
        IAtom oxy = builder.newInstance(IAtom.class, "O");
        oxy.setFormalCharge(1);
        molecule2.addAtom(oxy);
        molecule2.addBond(0, 1, IBond.Order.TRIPLE);

        setOfProducts.addAtomContainer(molecule1);
        setOfProducts.addAtomContainer(molecule2);
        return setOfProducts;
    }
}

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
 * TestSuite that runs a test for the ElectronImpactPDBReactionTest.
 *
 * @cdk.module test-reaction
 */

public class ElectronImpactPDBReactionTest extends ReactionProcessTest {

    private final LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    private IChemObjectBuilder            builder = SilentChemObjectBuilder.getInstance();

    /**
     *  The JUnit setup method
     */
    public ElectronImpactPDBReactionTest() throws Exception {
        setReaction(ElectronImpactPDBReaction.class);
    }

    /**
     *  The JUnit setup method
     */
    @Test
    public void testElectronImpactPDBReaction() throws Exception {
        IReactionProcess type = new ElectronImpactPDBReaction();
        Assert.assertNotNull(type);
    }

    /**
     *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
     *
     * @cdk.inchi InChI=1/C6H10O/c1-3-5-6(7)4-2/h3H,1,4-5H2,2H3
     *
     *@return    Description of the Return Value
     */
    @Test
    @Override
    public void testInitiate_IAtomContainerSet_IAtomContainerSet() throws Exception {
        /* ionize >C=C< , set the reactive center */
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

        Iterator<IBond> bonds = reactant.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();
            IAtom atom1 = bond.getBegin();
            IAtom atom2 = bond.getEnd();
            if (bond.getOrder() == IBond.Order.DOUBLE && atom1.getSymbol().equals("C") && atom2.getSymbol().equals("C")) {
                bond.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom1.setFlag(CDKConstants.REACTIVE_CENTER, true);
                atom2.setFlag(CDKConstants.REACTIVE_CENTER, true);
            }
        }

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(reactant);

        /* initiate */
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        makeSureAtomTypesAreRecognized(reactant);

        IReactionProcess type = new ElectronImpactPDBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        IAtomContainer molecule = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);

        Assert.assertEquals(1, molecule.getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)));
        Assert.assertEquals(1, molecule.getSingleElectronCount());

        molecule = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(0)));
        Assert.assertEquals(1, molecule.getSingleElectronCount());

        Assert.assertEquals(17, setOfReactions.getReaction(0).getMappingCount());

    }

    /**
     *  A unit test for JUnit with the compound propene.
     *
     * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testAutomatic_Set_Active_Bond() throws Exception {
        /* ionize all possible double bonds */
        IAtomContainer reactant = builder.newInstance(IAtomContainer.class);//miles("C=CC")
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addAtom(builder.newInstance(IAtom.class, "C"));
        reactant.addBond(0, 1, IBond.Order.DOUBLE);
        reactant.addBond(1, 2, IBond.Order.SINGLE);
        addExplicitHydrogens(reactant);

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(reactant);

        /* initiate */
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        makeSureAtomTypesAreRecognized(reactant);

        IReactionProcess type = new ElectronImpactPDBReaction();
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());

        IAtomContainer molecule = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)));

        molecule = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(0)));

    }

    /**
     *  A unit test for JUnit with the compound 2_5_Hexen_3_one.
     *
     * @cdk.inchi InChI=1/C6H10O/c1-3-5-6(7)4-2/h3H,1,4-5H2,2H3
     *
     *@return    Description of the Return Value
     */
    @Test
    public void testAutomatic_Set_Active_Bond2() throws Exception {
        /* ionize >C=C< , set the reactive center */
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

        IAtomContainerSet setOfReactants = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);
        setOfReactants.addAtomContainer(reactant);

        /* initiate */
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(reactant);
        makeSureAtomTypesAreRecognized(reactant);

        IReactionProcess type = new ElectronImpactPDBReaction();
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
        IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.FALSE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(3, setOfReactions.getReactionCount());

        IAtomContainer molecule = setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(0).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(1)));

        molecule = setOfReactions.getReaction(1).getProducts().getAtomContainer(0);
        Assert.assertEquals(1, molecule.getAtom(1).getFormalCharge().intValue());
        Assert.assertEquals(1, molecule.getConnectedSingleElectronsCount(molecule.getAtom(0)));

        Assert.assertEquals(17, setOfReactions.getReaction(0).getMappingCount());

    }

    /**
     * A unit test suite for JUnit. Reaction:propene
     * Manually put of the reactive center.
     *
     * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testCDKConstants_REACTIVE_CENTER() throws Exception {
        IReactionProcess type = new ElectronImpactPDBReaction();
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
        makeSureAtomTypesAreRecognized(molecule);

        IReactionSet setOfReactions = type.initiate(setOfReactants, null);

        Assert.assertEquals(2, setOfReactions.getReactionCount());
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
     * A unit test suite for JUnit. Reaction: propene
     * Manually put of the reactive center.
     *
     * @cdk.inchi InChI=1/C3H6/c1-3-2/h3H,1H2,2H3
     *
     * @return    The test suite
     */
    @Test
    public void testMapping() throws Exception {
        IReactionProcess type = new ElectronImpactPDBReaction();
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

        Assert.assertEquals(9, setOfReactions.getReaction(0).getMappingCount());
        IAtom mappedProductA1 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(0));
        Assert.assertEquals(mappedProductA1, product.getAtom(0));
        IAtom mappedProductA2 = (IAtom) ReactionManipulator.getMappedChemObject(setOfReactions.getReaction(0),
                molecule.getAtom(1));
        Assert.assertEquals(mappedProductA2, product.getAtom(1));
    }

    /**
     * Test to recognize if a IAtomContainer matcher correctly the CDKAtomTypes.
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

        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);//miles("C=CC")
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

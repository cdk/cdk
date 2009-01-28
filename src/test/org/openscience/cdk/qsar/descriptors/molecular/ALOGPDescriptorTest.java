package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Test suite for the alogp descriptor
 *
 * @cdk.module test-qsarmolecular
 */
public class ALOGPDescriptorTest extends MolecularDescriptorTest {

    private CDKHydrogenAdder hydrogenAdder;

    @Before
    protected void setUp() throws Exception {
        setDescriptor(ALOGPDescriptor.class);
        hydrogenAdder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testChloroButane() throws Exception {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c4 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom cl = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        mol.addAtom(c1);
        mol.addAtom(c2);
        mol.addAtom(c3);
        mol.addAtom(c4);
        mol.addAtom(cl);

        mol.addBond(new Bond(c1, c2));
        mol.addBond(new Bond(c2, c3));
        mol.addBond(new Bond(c3, c4));
        mol.addBond(new Bond(c4, cl));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        // add explicit hydrogens here
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
        hydrogenAdder.addImplicitHydrogens(mol);
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);

        DescriptorValue v = descriptor.calculate(mol);
        Assert.assertEquals(0.5192, ((DoubleArrayResult) v.getValue()).get(0), 0.0001);
        Assert.assertEquals(19.1381, ((DoubleArrayResult) v.getValue()).get(2), 0.0001);
    }

}

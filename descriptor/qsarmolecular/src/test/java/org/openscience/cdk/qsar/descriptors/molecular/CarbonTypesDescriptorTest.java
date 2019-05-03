package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

public class CarbonTypesDescriptorTest extends MolecularDescriptorTest {

    public CarbonTypesDescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(CarbonTypesDescriptor.class);
    }

    @Test
    public void testButane() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(0, ret.get(0));
        Assert.assertEquals(0, ret.get(1));
        Assert.assertEquals(0, ret.get(2));
        Assert.assertEquals(0, ret.get(3));
        Assert.assertEquals(0, ret.get(4));
        Assert.assertEquals(2, ret.get(5));
        Assert.assertEquals(2, ret.get(6));
        Assert.assertEquals(0, ret.get(7));
        Assert.assertEquals(0, ret.get(8));
    }

    @Test
    public void testComplex1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)C=C(C)C");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(0, ret.get(0));
        Assert.assertEquals(0, ret.get(1));
        Assert.assertEquals(0, ret.get(2));
        Assert.assertEquals(1, ret.get(3));
        Assert.assertEquals(1, ret.get(4));
        Assert.assertEquals(4, ret.get(5));
        Assert.assertEquals(0, ret.get(6));
        Assert.assertEquals(1, ret.get(7));
        Assert.assertEquals(0, ret.get(8));
    }

    @Test
    public void testComplex2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CC(C)=C");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assert.assertEquals(1, ret.get(0));
        Assert.assertEquals(1, ret.get(1));
        Assert.assertEquals(1, ret.get(2));
        Assert.assertEquals(0, ret.get(3));
        Assert.assertEquals(1, ret.get(4));
        Assert.assertEquals(1, ret.get(5));
        Assert.assertEquals(0, ret.get(6));
        Assert.assertEquals(0, ret.get(7));
        Assert.assertEquals(0, ret.get(8));
    }

}

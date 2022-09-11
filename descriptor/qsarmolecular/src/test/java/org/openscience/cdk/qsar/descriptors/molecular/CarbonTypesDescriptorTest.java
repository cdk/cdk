package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsarmolecular
 */

class CarbonTypesDescriptorTest extends MolecularDescriptorTest {

    CarbonTypesDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(CarbonTypesDescriptor.class);
    }

    @Test
    void testButane() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCCC");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assertions.assertEquals(0, ret.get(0));
        Assertions.assertEquals(0, ret.get(1));
        Assertions.assertEquals(0, ret.get(2));
        Assertions.assertEquals(0, ret.get(3));
        Assertions.assertEquals(0, ret.get(4));
        Assertions.assertEquals(2, ret.get(5));
        Assertions.assertEquals(2, ret.get(6));
        Assertions.assertEquals(0, ret.get(7));
        Assertions.assertEquals(0, ret.get(8));
    }

    @Test
    void testComplex1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(C)(C)C=C(C)C");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assertions.assertEquals(0, ret.get(0));
        Assertions.assertEquals(0, ret.get(1));
        Assertions.assertEquals(0, ret.get(2));
        Assertions.assertEquals(1, ret.get(3));
        Assertions.assertEquals(1, ret.get(4));
        Assertions.assertEquals(4, ret.get(5));
        Assertions.assertEquals(0, ret.get(6));
        Assertions.assertEquals(1, ret.get(7));
        Assertions.assertEquals(0, ret.get(8));
    }

    @Test
    void testComplex2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C#CC(C)=C");

        IntegerArrayResult ret = (IntegerArrayResult) descriptor.calculate(mol).getValue();

        Assertions.assertEquals(1, ret.get(0));
        Assertions.assertEquals(1, ret.get(1));
        Assertions.assertEquals(1, ret.get(2));
        Assertions.assertEquals(0, ret.get(3));
        Assertions.assertEquals(1, ret.get(4));
        Assertions.assertEquals(1, ret.get(5));
        Assertions.assertEquals(0, ret.get(6));
        Assertions.assertEquals(0, ret.get(7));
        Assertions.assertEquals(0, ret.get(8));
    }

}

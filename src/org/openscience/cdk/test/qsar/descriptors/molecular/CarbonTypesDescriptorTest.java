package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.descriptors.molecular.CarbonTypesDescriptor;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class CarbonTypesDescriptorTest extends CDKTestCase {

    public CarbonTypesDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(CarbonTypesDescriptorTest.class);
    }

    public void testButane() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = sp.parseSmiles("CCCC");

        CarbonTypesDescriptor ctypes = new CarbonTypesDescriptor();
        IntegerArrayResult ret = (IntegerArrayResult) ctypes.calculate(mol).getValue();

        assertEquals(0, ret.get(0));
        assertEquals(0, ret.get(1));
        assertEquals(0, ret.get(2));
        assertEquals(0, ret.get(3));
        assertEquals(0, ret.get(4));
        assertEquals(2, ret.get(5));
        assertEquals(2, ret.get(6));
        assertEquals(0, ret.get(7));
        assertEquals(0, ret.get(8));
    }


    public void testComplex1() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = sp.parseSmiles("C(C)(C)C=C(C)C");

        CarbonTypesDescriptor ctypes = new CarbonTypesDescriptor();
        IntegerArrayResult ret = (IntegerArrayResult) ctypes.calculate(mol).getValue();

        assertEquals(0, ret.get(0));
        assertEquals(0, ret.get(1));
        assertEquals(0, ret.get(2));
        assertEquals(1, ret.get(3));
        assertEquals(1, ret.get(4));
        assertEquals(4, ret.get(5));
        assertEquals(0, ret.get(6));
        assertEquals(1, ret.get(7));
        assertEquals(0, ret.get(8));
    }

    public void testComplex2() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = sp.parseSmiles("C#CC(C)=C");

        CarbonTypesDescriptor ctypes = new CarbonTypesDescriptor();
        IntegerArrayResult ret = (IntegerArrayResult) ctypes.calculate(mol).getValue();

        assertEquals(1, ret.get(0));
        assertEquals(1, ret.get(1));
        assertEquals(1, ret.get(2));
        assertEquals(0, ret.get(3));
        assertEquals(1, ret.get(4));
        assertEquals(1, ret.get(5));
        assertEquals(0, ret.get(6));
        assertEquals(0, ret.get(7));
        assertEquals(0, ret.get(8));
    }

}

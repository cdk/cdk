/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IPDBStructure} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractPDBStructureTest extends CDKTestCase {

    public static IChemObjectBuilder getBuilder() {
        return object.getBuilder();
    }

    private static IChemObject object;

    public static IChemObject newChemObject() {
        try {
            return (IChemObject) object.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public static void setChemObject(IChemObject object) {
        AbstractPDBStructureTest.object = object;
    }

    @Test
    public void testGetEndChainID() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getEndChainID());
    }

    @Test
    public void testSetEndChainID_Character() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        char endChainID = 'x';
        structure.setEndChainID(endChainID);
        Assert.assertEquals(endChainID, structure.getEndChainID().charValue());
    }

    @Test
    public void testGetEndInsertionCode() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getEndInsertionCode());
    }

    @Test
    public void testSetEndInsertionCode_Character() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        char endInsertionCode = 'x';
        structure.setEndInsertionCode(endInsertionCode);
        Assert.assertEquals(endInsertionCode, structure.getEndInsertionCode().charValue());
    }

    @Test
    public void testGetEndSequenceNumber() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getEndSequenceNumber());
    }

    @Test
    public void testSetEndSequenceNumber_Integer() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        int endSequenceNumber = 5;
        structure.setEndSequenceNumber(endSequenceNumber);
        Assert.assertEquals(endSequenceNumber, structure.getEndSequenceNumber().intValue());
    }

    @Test
    public void testGetStartChainID() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getStartChainID());
    }

    @Test
    public void testSetStartChainID_Character() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        char startChainID = 'x';
        structure.setStartChainID(startChainID);
        Assert.assertEquals(startChainID, structure.getStartChainID().charValue());
    }

    @Test
    public void testGetStartInsertionCode() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getStartInsertionCode());
    }

    @Test
    public void testSetStartInsertionCode_Character() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        char startInsertionCode = 'x';
        structure.setStartInsertionCode(startInsertionCode);
        Assert.assertEquals(startInsertionCode, structure.getStartInsertionCode().charValue());
    }

    @Test
    public void testGetStartSequenceNumber() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        Assert.assertNull(structure.getStartSequenceNumber());
    }

    @Test
    public void testSetStartSequenceNumber_Integer() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        int startSequenceNumber = 5;
        structure.setStartSequenceNumber(startSequenceNumber);
        Assert.assertEquals(startSequenceNumber, structure.getStartSequenceNumber().intValue());
    }

    @Test
    public void testGetStructureType() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        String type = structure.getStructureType();
        Assert.assertNull(type);
    }

    @Test
    public void testSetStructureType_String() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        String type = "alpha-barrel";
        structure.setStructureType(type);
        Assert.assertEquals(type, structure.getStructureType());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    public void testToString() {
        IPDBStructure structure = getBuilder().newInstance(IPDBStructure.class);
        String description = structure.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }
}

/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.tools.diff.AtomTypeDiff;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IAtomType} implementations.
 *
 */
public abstract class AbstractAtomTypeTest extends AbstractIsotopeTest {

    @Test
    public void testSetAtomTypeName_String() {
        IAtomType at = (IAtomType) newChemObject();
        at.setAtomTypeName("C4");
        Assertions.assertEquals("C4", at.getAtomTypeName());
    }

    @Test
    public void testGetAtomTypeName() {
        IAtomType at = (IAtomType) newChemObject();
        at.setAtomTypeName("C4");
        Assertions.assertEquals("C4", at.getAtomTypeName());
    }

    @Test
    public void testSetMaxBondOrder_IBond_Order() {
        IAtomType at = (IAtomType) newChemObject();
        at.setMaxBondOrder(IBond.Order.TRIPLE);
        Assertions.assertEquals(IBond.Order.TRIPLE, at.getMaxBondOrder());
    }

    @Test
    public void testGetMaxBondOrder() {
        testSetMaxBondOrder_IBond_Order();
    }

    @Test
    public void testSetBondOrderSum_Double() {
        IAtomType at = (IAtomType) newChemObject();
        at.setBondOrderSum(4.0);
        Assertions.assertEquals(4.0, at.getBondOrderSum(), 0.001);
    }

    @Test
    public void testGetBondOrderSum() {
        testSetBondOrderSum_Double();
    }

    @Test
    public void testSetCovalentRadius_Double() {
        IAtomType at = (IAtomType) newChemObject();
        at.setCovalentRadius(1.0);
        Assertions.assertEquals(1.0, at.getCovalentRadius(), 0.001);
    }

    @Test
    public void testGetCovalentRadius() {
        testSetCovalentRadius_Double();
    }

    @Test
    public void testSetFormalCharge_Integer() {
        int charge = 1;

        IAtomType a = (IAtomType) newChemObject();
        a.setFormalCharge(charge);
        Assertions.assertEquals(charge, a.getFormalCharge().intValue());
    }

    @Test
    public void testGetFormalCharge() {
        testSetFormalCharge_Integer();
    }

    /**
     * Method to test the get/setValency() methods.
     */
    @Test
    public void testSetValency_Integer() {
        int valency = 4;

        IAtomType a = (IAtomType) newChemObject();
        a.setValency(valency);
        Assertions.assertEquals(valency, (int) a.getValency());
    }

    @Test
    public void testGetValency() {
        testSetValency_Integer();
    }

    @Test
    public void testSetFormalNeighbourCount_Integer() {
        int count = 4;

        IAtomType a = (IAtomType) newChemObject();
        a.setFormalNeighbourCount(count);
        Assertions.assertEquals(count, (int) a.getFormalNeighbourCount());
    }

    @Test
    public void testGetFormalNeighbourCount() {
        testSetFormalNeighbourCount_Integer();
    }

    @Test
    public void testSetHybridization_IAtomType_Hybridization() {
        Hybridization hybridization = Hybridization.SP1;

        IAtomType atom = (IAtomType) newChemObject();
        atom.setHybridization(hybridization);
        Assertions.assertEquals(hybridization, atom.getHybridization());
    }

    @Test
    public void testGetHybridization() {
        testSetHybridization_IAtomType_Hybridization();
    }

    @Test
    public void testSetHybridization_Null() {
        Hybridization hybridization = Hybridization.SP1;

        IAtomType atom = (IAtomType) newChemObject();
        atom.setHybridization(hybridization);
        Assertions.assertEquals(hybridization, atom.getHybridization());
        atom.setHybridization(null);
        Assertions.assertNull(atom.getHybridization());
    }

    @Test
    public void testSetAcceptor_boolean() {
        boolean acceptor = true;
        IAtomType a = (IAtomType) newChemObject();
        a.setFlag(IChemObject.HYDROGEN_BOND_ACCEPTOR, acceptor);
        Assertions.assertTrue(a.getFlag(IChemObject.HYDROGEN_BOND_ACCEPTOR));
    }

    @Test
    public void testGetAcceptor() {
        testSetAcceptor_boolean();
    }

    @Test
    public void testSetDonor_boolean() {
        boolean donor = true;
        IAtomType a = (IAtomType) newChemObject();
        a.setFlag(IChemObject.HYDROGEN_BOND_DONOR, donor);
        Assertions.assertTrue(a.getFlag(IChemObject.HYDROGEN_BOND_DONOR));
    }

    @Test
    public void testGetDonor() {
        testSetDonor_boolean();
    }

    @Test
    public void testSetChemicalGroupConstant_int() {
        int benzol = 6;
        IAtomType a = (IAtomType) newChemObject();
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, benzol);
        Assertions.assertEquals(benzol, ((Integer) a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT)).intValue());
    }

    @Test
    public void testGetChemicalGroupConstant() {
        testSetChemicalGroupConstant_int();
    }

    @Test
    public void testSetRingSize_int() {
        int five = 5;
        IAtomType a = (IAtomType) newChemObject();
        a.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, five);
        Assertions.assertEquals(five, ((Integer) a.getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue());
    }

    @Test
    public void testGetRingSize() {
        testSetRingSize_int();
    }

    @Test
    public void testSetIsAromatic_boolean() {
        IAtomType a = (IAtomType) newChemObject();
        a.setFlag(IChemObject.AROMATIC, true);
        Assertions.assertTrue(a.getFlag(IChemObject.AROMATIC));
    }

    @Test
    public void testGetIsAromatic() {
        testSetIsAromatic_boolean();
    }

    @Test
    public void testSetSphericalMatcher_String() {
        String hoseCode = "C-4;HHHC(;///***)";
        IAtomType a = (IAtomType) newChemObject();
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, hoseCode);
        Assertions.assertEquals(hoseCode, a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));
    }

    @Test
    public void testGetSphericalMatcher() {
        testSetSphericalMatcher_String();
    }

    /**
     * Test for bug #1309731.
     */
    @Test
    public void testAtomTypeNameAndIDBug() {
        IAtomType a = (IAtomType) newChemObject();
        a.setID("carbon1");
        a.setAtomTypeName("C.sp3");
        Assertions.assertEquals("carbon1", a.getID());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        Object clone = at.clone();
        Assertions.assertTrue(clone instanceof IAtomType);

        // test that everything has been cloned properly
        String diff = AtomTypeDiff.diff(at, (IAtomType) clone);
        Assertions.assertNotNull(diff);
        Assertions.assertEquals(0, diff.length());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_MaxBondOrder() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setMaxBondOrder(IBond.Order.SINGLE);
        IAtomType clone = (IAtomType) at.clone();

        at.setMaxBondOrder(IBond.Order.DOUBLE);
        Assertions.assertEquals(IBond.Order.SINGLE, clone.getMaxBondOrder());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_IBondOrderSum() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setBondOrderSum(1.0);
        IAtomType clone = (IAtomType) at.clone();

        at.setBondOrderSum(2.0);
        Assertions.assertEquals(1.0, clone.getBondOrderSum(), 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_CovalentRadius() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setCovalentRadius(1.0);
        IAtomType clone = (IAtomType) at.clone();

        at.setCovalentRadius(2.0);
        Assertions.assertEquals(1.0, clone.getCovalentRadius(), 0.001);
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_FormalCharge() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setFormalCharge(1);
        IAtomType clone = (IAtomType) at.clone();

        at.setFormalCharge(2);
        Assertions.assertEquals(1, clone.getFormalCharge().intValue());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_FormalNeighbourCount() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setFormalNeighbourCount(1);
        IAtomType clone = (IAtomType) at.clone();

        at.setFormalNeighbourCount(2);
        Assertions.assertEquals(1, (int) clone.getFormalNeighbourCount());
    }

    /**
     * Method to test the clone() method
     */
    @Test
    public void testClone_Hybridization() throws Exception {
        IAtomType at = (IAtomType) newChemObject();
        at.setHybridization(Hybridization.PLANAR3);
        IAtomType clone = (IAtomType) at.clone();

        at.setHybridization(Hybridization.SP1);
        Assertions.assertEquals(Hybridization.PLANAR3, clone.getHybridization());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IAtomType at = (IAtomType) newChemObject();
        String description = at.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    public void testToString_AtomTypeName() {
        IAtomType at = (IAtomType) newChemObject();
        at.setAtomTypeName("N.sp2.3");
        String description = at.toString();
        Assertions.assertTrue(description.contains("N.sp2.3"));
    }

    @Test
    public void testDefaultFormalCharge() {
        IAtomType atomType = (IAtomType) newChemObject();
        Assertions.assertEquals(0, atomType.getFormalCharge().intValue());
    }
}

/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.tools.diff.AtomTypeDiff;

/**
 * Checks the functionality of the AtomType class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends IsotopeTest {

	protected static IChemObjectBuilder builder;
	
    @BeforeClass public static void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    @Test public void testAtomType_String() {
        IAtomType at = builder.newAtomType("C");
        Assert.assertEquals("C", at.getSymbol());
    }

    @Test public void testAtomType_IElement() {
    	IElement element = builder.newElement("C");
        IAtomType at = builder.newAtomType(element);
        Assert.assertEquals("C", at.getSymbol());
    }

    @Test public void testAtomType_String_String() {
        IAtomType at = builder.newAtomType("C4", "C");
        Assert.assertEquals("C", at.getSymbol());
        Assert.assertEquals("C4", at.getAtomTypeName());
    }
    
    @Test public void testSetAtomTypeName_String() {
        IAtomType at = builder.newAtomType("C");
        at.setAtomTypeName("C4");
        Assert.assertEquals("C4", at.getAtomTypeName());
    }

    @Test public void testGetAtomTypeName() {
        IAtomType at = builder.newAtomType("C4", "C");
        Assert.assertEquals("C4", at.getAtomTypeName());
    }
    
    @Test public void testSetMaxBondOrder_IBond_Order() {
        IAtomType at = builder.newAtomType("C");
        at.setMaxBondOrder(IBond.Order.TRIPLE);
        Assert.assertEquals(IBond.Order.TRIPLE, at.getMaxBondOrder());
    }

    @Test public void testGetMaxBondOrder() {
    	testSetMaxBondOrder_IBond_Order();
    }

    @Test public void testSetBondOrderSum_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setBondOrderSum(4.0);
        Assert.assertEquals(4.0, at.getBondOrderSum(), 0.001);
    }
    
    @Test public void testGetBondOrderSum() {
        testSetBondOrderSum_Double();
    }
    
    @Test public void testCompare() {
        IAtomType at = builder.newAtomType("C4", "C");
        if (at instanceof org.openscience.cdk.AtomType) {
        	org.openscience.cdk.AtomType at1 = (org.openscience.cdk.AtomType)at;
	        IAtomType at2 = builder.newAtomType("C3", "C");
	        Assert.assertFalse(at1.compare("C4"));
	        Assert.assertFalse(at1.compare(at2));
        }
    }

    @Test public void testSetCovalentRadius_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setCovalentRadius(1.0);
        Assert.assertEquals(1.0, at.getCovalentRadius(), 0.001);
    }
    @Test public void testGetCovalentRadius() {
        testSetCovalentRadius_Double();
    }
    
    /**
     * Method to test the get/setFormalCharge() methods.
     */
    @Test public void testSetFormalCharge_Integer() {
        int charge = 1;

        IAtomType a = builder.newAtomType("C");
        a.setFormalCharge(charge);
        Assert.assertEquals(charge, a.getFormalCharge().intValue());
    }
    @Test public void testGetFormalCharge() {
        testSetFormalCharge_Integer();
    }

    /**
     * Method to test the get/setValency() methods.
     */
    @Test public void testSetValency_Integer() {
        int valency = 4;

        IAtomType a = builder.newAtomType("C");
        a.setValency(valency);
        Assert.assertEquals(valency, (int) a.getValency());
    }
    @Test public void testGetValency() {
        testSetValency_Integer();
    }

    @Test public void testSetFormalNeighbourCount_Integer() {
        int count = 4;

        IAtomType a = builder.newAtomType("C");
        a.setFormalNeighbourCount(count);
        Assert.assertEquals(count, (int) a.getFormalNeighbourCount());
    }
    @Test public void testGetFormalNeighbourCount() {
        testSetFormalNeighbourCount_Integer();
    }

    @Test public void testSetHybridization_IAtomType_Hybridization() {
        Hybridization hybridization = Hybridization.SP1;

        IAtomType atom = builder.newAtomType("C");
        atom.setHybridization(hybridization);
        Assert.assertEquals(hybridization, atom.getHybridization());
    }
    @Test public void testGetHybridization() {
    	testSetHybridization_IAtomType_Hybridization();
    }

    @Test public void testSetHybridization_Null() {
        Hybridization hybridization = Hybridization.SP1;

        IAtomType atom = builder.newAtomType("C");
        atom.setHybridization(hybridization);
        Assert.assertEquals(hybridization, atom.getHybridization());
        atom.setHybridization(null);
        Assert.assertNull(atom.getHybridization());
    }

    @Test public void testSetAcceptor_boolean(){
    	boolean acceptor=true;
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, acceptor);
        Assert.assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));    	
    }
    @Test public void testGetAcceptor(){
    	testSetAcceptor_boolean();
    }
    
    @Test public void testSetDonor_boolean(){
    	boolean donor=true;
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, donor);
        Assert.assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_DONOR));    	
    }
    @Test public void testGetDonor(){
    	testSetDonor_boolean();    	
    }
    
    @Test public void testSetChemicalGroupConstant_int(){
    	int benzol=6;
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, benzol);
        Assert.assertEquals(benzol,((Integer)a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT)).intValue());    	
    }    
    @Test public void testGetChemicalGroupConstant(){
    	testSetChemicalGroupConstant_int();
    }
    
    @Test public void  testSetRingSize_int(){
    	int five=5;
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, five);
        Assert.assertEquals(five,((Integer)a.getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue());    	
    }    
    @Test public void  testGetRingSize(){
    	 testSetRingSize_int();
    }
    
    @Test public void testSetIsAromatic_boolean(){
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.ISAROMATIC, true);
        Assert.assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
    }    
    @Test public void  testGetIsAromatic(){
    	testSetIsAromatic_boolean();
    }
    
    @Test public void testSetSphericalMatcher_String(){
    	String hoseCode="C-4;HHHC(;///***)";
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, hoseCode);
        Assert.assertEquals(hoseCode,a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));    	
    }    
    @Test public void testGetSphericalMatcher(){
    	testSetSphericalMatcher_String();
    }
    
    /**
     * Test for bug #1309731.
     */
    @Test public void testAtomTypeNameAndIDBug() {
    	IAtomType a = builder.newAtomType("C");
    	a.setID("carbon1");
    	a.setAtomTypeName("C.sp3");
    	Assert.assertEquals("carbon1", a.getID());
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone() throws Exception {
        IAtomType at = builder.newAtomType("C");
        Object clone = at.clone();
        Assert.assertTrue(clone instanceof IAtomType);

        // test that everything has been cloned properly
        String diff = AtomTypeDiff.diff(at, (IAtomType)clone);
        Assert.assertNotNull(diff);
        Assert.assertEquals(0, diff.length());
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_MaxBondOrder() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setMaxBondOrder(IBond.Order.SINGLE);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setMaxBondOrder(IBond.Order.DOUBLE);
        Assert.assertEquals(IBond.Order.SINGLE, clone.getMaxBondOrder());
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_IBondOrderSum() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setBondOrderSum(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setBondOrderSum(2.0);
        Assert.assertEquals(1.0, clone.getBondOrderSum(), 0.001);
    }

   
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_CovalentRadius() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setCovalentRadius(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setCovalentRadius(2.0);
        Assert.assertEquals(1.0, clone.getCovalentRadius(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_FormalCharge() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setFormalCharge(1);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setFormalCharge(2);
        Assert.assertEquals(1, clone.getFormalCharge().intValue());
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_FormalNeighbourCount() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setFormalNeighbourCount(1);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setFormalNeighbourCount(2);
        Assert.assertEquals(1, (int) clone.getFormalNeighbourCount());
    }
    
    /**
     * Method to test the clone() method
     */
    @Test public void testClone_Hybridization() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setHybridization(Hybridization.PLANAR3);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setHybridization(Hybridization.SP1);
        Assert.assertEquals(Hybridization.PLANAR3, clone.getHybridization());
    }
    
    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test public void testToString() {
        IAtomType at = builder.newAtomType("C");
        String description = at.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test public void testCompare_Object() {
    	IAtomType someAt = builder.newAtomType("C");
    	if (someAt instanceof org.openscience.cdk.AtomType) {
    		org.openscience.cdk.AtomType at = (org.openscience.cdk.AtomType)someAt;
	        Assert.assertTrue(at.compare(at));
	        IAtomType hydrogen = builder.newAtomType("H");
	        Assert.assertFalse(at.compare(hydrogen));
	        Assert.assertFalse(at.compare("Li"));
    	}
    }

    @Test public void testDefaultFormalCharge() {
        IAtomType atomType = builder.newAtomType("C");
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
    }
}

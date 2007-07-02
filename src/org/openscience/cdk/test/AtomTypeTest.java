/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
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
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;

/**
 * Checks the funcitonality of the AtomType class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public AtomTypeTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(AtomTypeTest.class);
    }
    
    public void testAtomType_String() {
        IAtomType at = builder.newAtomType("C");
        assertEquals("C", at.getSymbol());
    }

    public void testAtomType_IElement() {
    	IElement element = builder.newElement("C");
        IAtomType at = builder.newAtomType(element);
        assertEquals("C", at.getSymbol());
    }

    public void testAtomType_String_String() {
        IAtomType at = builder.newAtomType("C4", "C");
        assertEquals("C", at.getSymbol());
        assertEquals("C4", at.getAtomTypeName());
    }
    
    public void testSetAtomTypeName_String() {
        IAtomType at = builder.newAtomType("C");
        at.setAtomTypeName("C4");
        assertEquals("C4", at.getAtomTypeName());
    }

    public void testGetAtomTypeName() {
        IAtomType at = builder.newAtomType("C4", "C");
        assertEquals("C4", at.getAtomTypeName());
    }
    
    public void testSetMaxBondOrder_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setMaxBondOrder(3.0);
        assertEquals(3.0, at.getMaxBondOrder(), 0.001);
    }

    public void testGetMaxBondOrder() {
        testSetMaxBondOrder_Double();
    }

    public void testSetBondOrderSum_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setBondOrderSum(4.0);
        assertEquals(4.0, at.getBondOrderSum(), 0.001);
    }
    
    public void testGetBondOrderSum() {
        testSetBondOrderSum_Double();
    }
    
    public void testCompare() {
        IAtomType at = builder.newAtomType("C4", "C");
        if (at instanceof org.openscience.cdk.AtomType) {
        	org.openscience.cdk.AtomType at1 = (org.openscience.cdk.AtomType)at;
	        IAtomType at2 = builder.newAtomType("C3", "C");
	        assertFalse(at1.compare("C4"));
	        assertFalse(at1.compare(at2));
        }
    }
    
    public void testSetVanderwaalsRadius_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setVanderwaalsRadius(1.0);
        assertEquals(1.0, at.getVanderwaalsRadius(), 0.001);
    }
    public void testGetVanderwaalsRadius() {
        testSetVanderwaalsRadius_Double();
    }
    
    
    public void testSetCovalentRadius_Double() {
        IAtomType at = builder.newAtomType("C");
        at.setCovalentRadius(1.0);
        assertEquals(1.0, at.getCovalentRadius(), 0.001);
    }
    public void testGetCovalentRadius() {
        testSetCovalentRadius_Double();
    }
    
    /**
     * Method to test the get/setFormalCharge() methods.
     */
    public void testSetFormalCharge_Integer() {
        int charge = 1;

        IAtomType a = builder.newAtomType("C");
        a.setFormalCharge(charge);
        assertEquals(charge, a.getFormalCharge().intValue());
    }
    public void testGetFormalCharge() {
        testSetFormalCharge_Integer();
    }

    /**
     * Method to test the get/setValency() methods.
     */
    public void testSetValency_Integer() {
        int valency = 4;

        IAtomType a = builder.newAtomType("C");
        a.setValency(valency);
        assertEquals(valency, (int) a.getValency());
    }
    public void testGetValency() {
        testSetValency_Integer();
    }

    public void testSetFormalNeighbourCount_Integer() {
        int count = 4;

        IAtomType a = builder.newAtomType("C");
        a.setFormalNeighbourCount(count);
        assertEquals(count, (int) a.getFormalNeighbourCount());
    }
    public void testGetFormalNeighbourCount() {
        testSetFormalNeighbourCount_Integer();
    }

    public void testSetHybridization_Integer() {
        int hybridization = CDKConstants.HYBRIDIZATION_SP3;

        IAtomType atom = builder.newAtomType("C");
        atom.setHybridization(hybridization);
        assertEquals(hybridization, (int) atom.getHybridization());
    }
    public void testGetHybridization() {
        testSetHybridization_Integer();
    }

    public void testSetAcceptor_boolean(){
    	boolean acceptor=true;
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, acceptor);
        assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));    	
    }
    public void testGetAcceptor(){
    	testSetAcceptor_boolean();
    }
    
    public void testSetDonor_boolean(){
    	boolean donor=true;
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, donor);
        assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_DONOR));    	
    }
    public void testGetDonor(){
    	testSetDonor_boolean();    	
    }
    
    public void testSetChemicalGroupConstant_int(){
    	int benzol=6;
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, benzol);
        assertEquals(benzol,((Integer)a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT)).intValue());    	
    }    
    public void testGetChemicalGroupConstant(){
    	testSetChemicalGroupConstant_int();
    }
    
    public void  testSetRingSize_int(){
    	int five=5;
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, five);
        assertEquals(five,((Integer)a.getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue());    	
    }    
    public void  testGetRingSize(){
    	 testSetRingSize_int();
    }
    
    public void testSetIsAromatic_boolean(){
    	IAtomType a = builder.newAtomType("C");
        a.setFlag(CDKConstants.ISAROMATIC, true);
        assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
    }    
    public void  testGetIsAromatic(){
    	testSetIsAromatic_boolean();
    }
    
    public void testSetSphericalMatcher_String(){
    	String hoseCode="C-4;HHHC(;///***)";
    	IAtomType a = builder.newAtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, hoseCode);
        assertEquals(hoseCode,a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));    	
    }    
    public void testGetSphericalMatcher(){
    	testSetSphericalMatcher_String();
    }
    
    /**
     * Test for bug #1309731.
     */
    public void testAtomTypeNameAndIDBug() {
    	IAtomType a = builder.newAtomType("C");
    	a.setID("carbon1");
    	a.setAtomTypeName("C.sp3");
    	assertEquals("carbon1", a.getID());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() throws Exception {
        IAtomType at = builder.newAtomType("C");
        Object clone = at.clone();
        assertTrue(clone instanceof IAtomType);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_MaxBondOrder() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setMaxBondOrder(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setMaxBondOrder(2.0);
        assertEquals(1.0, clone.getMaxBondOrder(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_IBondOrderSum() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setBondOrderSum(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setBondOrderSum(2.0);
        assertEquals(1.0, clone.getBondOrderSum(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_VanderwaalsRadius() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setVanderwaalsRadius(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setVanderwaalsRadius(2.0);
        assertEquals(1.0, clone.getVanderwaalsRadius(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_CovalentRadius() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setCovalentRadius(1.0);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setCovalentRadius(2.0);
        assertEquals(1.0, clone.getCovalentRadius(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_FormalCharge() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setFormalCharge(1);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setFormalCharge(2);
        assertEquals(1, clone.getFormalCharge().intValue());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_FormalNeighbourCount() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setFormalNeighbourCount(1);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setFormalNeighbourCount(2);
        assertEquals(1, (int) clone.getFormalNeighbourCount());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_Hybridization() throws Exception {
        IAtomType at = builder.newAtomType("C");
        at.setHybridization(1);
        IAtomType clone = (IAtomType)at.clone();
        
        at.setHybridization(2);
        assertEquals(1, (int) clone.getHybridization());
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        IAtomType at = builder.newAtomType("C");
        String description = at.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testCompare_Object() {
    	IAtomType someAt = builder.newAtomType("C");
    	if (someAt instanceof org.openscience.cdk.AtomType) {
    		org.openscience.cdk.AtomType at = (org.openscience.cdk.AtomType)someAt;
	        assertTrue(at.compare(at));
	        IAtomType hydrogen = builder.newAtomType("H");
	        assertFalse(at.compare(hydrogen));
	        assertFalse(at.compare("Li"));
    	}
    }

    public void testDefaultFormalCharge() {
        IAtomType atomType = builder.newAtomType("C");
        assertEquals(0, atomType.getFormalCharge().intValue());
    }
}

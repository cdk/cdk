/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomType;
import org.openscience.cdk.CDKConstants;

/**
 * Checks the funcitonality of the AtomType class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.AtomType
 */
public class AtomTypeTest extends CDKTestCase {

    public AtomTypeTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(AtomTypeTest.class);
    }
    
    public void testAtomType_String() {
        AtomType at = new AtomType("C");
        assertEquals("C", at.getSymbol());
    }

    public void testAtomType_String_String() {
        AtomType at = new AtomType("C4", "C");
        assertEquals("C", at.getSymbol());
        assertEquals("C4", at.getAtomTypeName());
    }
    
    public void testSetAtomTypeName_String() {
        AtomType at = new AtomType("C");
        at.setAtomTypeName("C4");
        assertEquals("C4", at.getAtomTypeName());
    }

    public void testGetAtomTypeName() {
        AtomType at = new AtomType("C4", "C");
        assertEquals("C4", at.getAtomTypeName());
    }
    
    public void testSetMaxBondOrder_double() {
        AtomType at = new AtomType("C");
        at.setMaxBondOrder(3.0);
        assertEquals(3.0, at.getMaxBondOrder(), 0.001);
    }

    public void testGetMaxBondOrder() {
        testSetMaxBondOrder_double();
    }

    public void testSetBondOrderSum_double() {
        AtomType at = new AtomType("C");
        at.setBondOrderSum(4.0);
        assertEquals(4.0, at.getBondOrderSum(), 0.001);
    }
    
    public void testGetBondOrderSum() {
        testSetBondOrderSum_double();
    }
    
    public void testCompare() {
        AtomType at = new AtomType("C4", "C");
        AtomType at2 = new AtomType("C3", "C");
        assertFalse(at.compare("C4"));
        assertFalse(at.compare(at2));
    }
    
    public void testSetVanderwaalsRadius_double() {
        AtomType at = new AtomType("C");
        at.setVanderwaalsRadius(1.0);
        assertEquals(1.0, at.getVanderwaalsRadius(), 0.001);
    }
    public void testGetVanderwaalsRadius() {
        testSetVanderwaalsRadius_double();
    }
    
    
    public void testSetCovalentRadius_double() {
        AtomType at = new AtomType("C");
        at.setCovalentRadius(1.0);
        assertEquals(1.0, at.getCovalentRadius(), 0.001);
    }
    public void testGetCovalentRadius() {
        testSetCovalentRadius_double();
    }
    
    /**
     * Method to test the get/setFormalCharge() methods.
     */
    public void testSetFormalCharge_int() {
        int charge = 1;

        AtomType a = new AtomType("C");
        a.setFormalCharge(charge);
        assertEquals(charge, a.getFormalCharge());
    }
    public void testGetFormalCharge() {
        testSetFormalCharge_int();
    }

    /**
     * Method to test the get/setValency() methods.
     */
    public void testSetValency_int() {
        int valency = 4;

        AtomType a = new AtomType("C");
        a.setValency(valency);
        assertEquals(valency, a.getValency());
    }
    public void testGetValency() {
        testSetValency_int();
    }

    public void testSetFormalNeighbourCount_int() {
        int count = 4;

        AtomType a = new AtomType("C");
        a.setFormalNeighbourCount(count);
        assertEquals(count, a.getFormalNeighbourCount());
    }
    public void testGetFormalNeighbourCount() {
        testSetFormalNeighbourCount_int();
    }

    public void testSetHybridization_int() {
        int hybridization = CDKConstants.HYBRIDIZATION_SP3;

        AtomType atom = new AtomType("C");
        atom.setHybridization(hybridization);
        assertEquals(hybridization, atom.getHybridization());
    }
    public void testGetHybridization() {
        testSetHybridization_int();
    }

    public void testSetAcceptor_boolean(){
    	boolean acceptor=true;
    	AtomType a = new AtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, acceptor);
        assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));    	
    }
    public void testGetAcceptor(){
    	testSetAcceptor_boolean();
    }
    
    public void testSetDonor_boolean(){
    	boolean donor=true;
    	AtomType a = new AtomType("C");
        a.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, donor);
        assertTrue(a.getFlag(CDKConstants.IS_HYDROGENBOND_DONOR));    	
    }
    public void testGetDonor(){
    	testSetDonor_boolean();    	
    }
    
    public void testSetChemicalGroupConstant_int(){
    	int benzol=6;
    	AtomType a = new AtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, new Integer(benzol));
        assertEquals(benzol,((Integer)a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT)).intValue());    	
    }    
    public void testGetChemicalGroupConstant(){
    	testSetChemicalGroupConstant_int();
    }
    
    public void  testSetRingSize_int(){
    	int five=5;
    	AtomType a = new AtomType("C");
        a.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, new Integer(five));
        assertEquals(five,((Integer)a.getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue());    	
    }    
    public void  testGetRingSize(){
    	 testSetRingSize_int();
    }
    
    public void testSetIsAromatic_boolean(){
    	AtomType a = new AtomType("C");
        a.setFlag(CDKConstants.ISAROMATIC, true);
        assertTrue(a.getFlag(CDKConstants.ISAROMATIC));
    }    
    public void  testGetIsAromatic(){
    	testSetIsAromatic_boolean();
    }
    
    public void testSetSphericalMatcher_String(){
    	String hoseCode="C-4;HHHC(;///***)";
    	AtomType a = new AtomType("C");
        a.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, hoseCode);
        assertEquals(hoseCode,a.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));    	
    }    
    public void testGetSphericalMatcher(){
    	testSetSphericalMatcher_String();
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() {
        AtomType at = new AtomType("C");
        Object clone = at.clone();
        assertTrue(clone instanceof AtomType);
        AtomType copy = (AtomType)clone;
        assertTrue(at.compare(copy));
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_MaxBondOrder() {
        AtomType at = new AtomType("C");
        at.setMaxBondOrder(1.0);
        AtomType clone = (AtomType)at.clone();
        
        at.setMaxBondOrder(2.0);
        assertEquals(1.0, clone.getMaxBondOrder(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_BondOrderSum() {
        AtomType at = new AtomType("C");
        at.setBondOrderSum(1.0);
        AtomType clone = (AtomType)at.clone();
        
        at.setBondOrderSum(2.0);
        assertEquals(1.0, clone.getBondOrderSum(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_VanderwaalsRadius() {
        AtomType at = new AtomType("C");
        at.setVanderwaalsRadius(1.0);
        AtomType clone = (AtomType)at.clone();
        
        at.setVanderwaalsRadius(2.0);
        assertEquals(1.0, clone.getVanderwaalsRadius(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_CovalentRadius() {
        AtomType at = new AtomType("C");
        at.setCovalentRadius(1.0);
        AtomType clone = (AtomType)at.clone();
        
        at.setCovalentRadius(2.0);
        assertEquals(1.0, clone.getCovalentRadius(), 0.001);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_FormalCharge() {
        AtomType at = new AtomType("C");
        at.setFormalCharge(1);
        AtomType clone = (AtomType)at.clone();
        
        at.setFormalCharge(2);
        assertEquals(1, clone.getFormalCharge());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_FormalNeighbourCount() {
        AtomType at = new AtomType("C");
        at.setFormalNeighbourCount(1);
        AtomType clone = (AtomType)at.clone();
        
        at.setFormalNeighbourCount(2);
        assertEquals(1, clone.getFormalNeighbourCount());
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_Hybridization() {
        AtomType at = new AtomType("C");
        at.setHybridization(1);
        AtomType clone = (AtomType)at.clone();
        
        at.setHybridization(2);
        assertEquals(1, clone.getHybridization());
    }
    
    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
        AtomType at = new AtomType("C");
        String description = at.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testCompare_Object() {
        AtomType at = new AtomType("C");
        assertTrue(at.compare(at));
        AtomType hydrogen = new AtomType("H");
        assertFalse(at.compare(hydrogen));
        assertFalse(at.compare("Li"));
    }
}

/* $RCSfile$
 * $Author: rajarshi $    
 * $Date: 2006-09-20 15:05:25 +0000 (Wed, 20 Sep 2006) $    
 * $Revision: 6982 $
 * 
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.test.protein.data;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the functionality of the AtomTypeFactory
 *
 * @cdk.module test-data
 * @see PDBAtom
 */
public class PDBAtomTest extends CDKTestCase {

	protected IChemObjectBuilder builder;
	
    public PDBAtomTest(String name) {
        super(name);
    }

    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(PDBAtomTest.class);
    }
    
    public void testPDBAtom_IElement() {
    	IElement element = builder.newElement();
        IAtom a = builder.newPDBAtom(element);
        assertNotNull(a);
    }
    
    /**
     * Method to test the Atom(String symbol) method.
     */
    public void testPDBAtom_String() {
    	IPDBAtom a = builder.newPDBAtom("C");
        assertEquals("C", a.getSymbol());
        assertNull(a.getPoint2d());
        assertNull(a.getPoint3d());
        assertNull(a.getFractionalPoint3d());
    }

    /**
     * Method to test the Atom(String symbol, javax.vecmath.Point3d point3D) method.
     */
    public void testPDBAtom_String_Point3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);

        IPDBAtom a = builder.newPDBAtom("C", point3d);
        assertEquals("C", a.getSymbol());
        assertEquals(point3d, a.getPoint3d());
        assertNull(a.getPoint2d());
        assertNull(a.getFractionalPoint3d());
    }


    /**
     * Method to test the setFractional3D() methods.
     */
    public void testSetFractionalPoint3d_Point3d() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setFractionalPoint3d(new Point3d(0.5, 0.5, 0.5));
        Point3d fract = a.getFractionalPoint3d();
        assertNotNull(fract);
        assertEquals(0.5, fract.x, 0.001);
        assertEquals(0.5, fract.y, 0.001);
        assertEquals(0.5, fract.z, 0.001);
    }
    public void testGetFractionalPoint3d() {
        testSetFractionalPoint3d_Point3d();
    }
    
    public void testSetFractX3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setFractX3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.x, 0.001);
    }

    public void testSetFractY3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setFractY3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.y, 0.001);
    }

    public void testSetFractZ3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setFractZ3d(0.5);
        Point3d point3d = a.getFractionalPoint3d();
        assertEquals(0.5, point3d.z, 0.001);
    }

    /**
     * Method to test the set[XYZ]3D() methods.
     */
    public void testSetX3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setX3d(1.0);

        assertNotNull(a.getPoint3d());
        assertEquals(1.0, a.getPoint3d().x, 0.001);
    }
    public void testSetY3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setY3d(2.0);

        assertNotNull(a.getPoint3d());
        assertEquals(2.0, a.getPoint3d().y, 0.001);
    }
    public void testSetZ3d_double() {
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setZ3d(3.0);

        assertNotNull(a.getPoint3d());
        assertEquals(3.0, a.getPoint3d().z, 0.001);
    }
    
    public void testGetPoint3d() {
        Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
        IPDBAtom a = builder.newPDBAtom("C", point3d);
        assertNotNull(a.getPoint3d());
        assertEquals(point3d, a.getPoint3d(), 0.001);
    }
    public void testSetPoint3d_Point3d() {
    	Point3d point3d = new Point3d(1.0, 2.0, 3.0);
        
    	IPDBAtom a = builder.newPDBAtom("C");
        a.setPoint3d(point3d);
        assertEquals(point3d, a.getPoint3d());
    }
    
    /**
     * Method to test the compare() method.
     */
    public void testCompare_Object() {
    	IPDBAtom someAtom = builder.newPDBAtom("C");
        if (someAtom instanceof org.openscience.cdk.Atom) {
        	org.openscience.cdk.Atom atom = (org.openscience.cdk.Atom)someAtom;
        	assertTrue(atom.compare(atom));
        	IAtom hydrogen = builder.newAtom("H");
        	assertFalse(atom.compare(hydrogen));
        	assertFalse(atom.compare("C"));
        }
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone() throws Exception {
    	IPDBAtom atom = builder.newPDBAtom("C");
        Object clone = atom.clone();
        assertTrue(clone instanceof IAtom);
    }
    
    /**
     * Method to test the clone() method
     */
    public void testClone_Point3d() throws Exception {
    	IPDBAtom atom = builder.newPDBAtom("C");
        atom.setPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setX3d(5);
        assertEquals(clone.getPoint3d().x, 2.0, 0.001);
    }

    /**
     * Method to test the clone() method
     */
    public void testClone_FractionalPoint3d() throws Exception {
    	IPDBAtom atom = builder.newPDBAtom("C");
        atom.setFractionalPoint3d(new Point3d(2, 3, 4));
        IAtom clone = (IAtom)atom.clone();

        // test cloning
        atom.setFractX3d(5);
        assertEquals(clone.getFractionalPoint3d().x, 2.0, 0.001);
    }


    /**
     * Method to test wether the class complies with RFC #9.
     */
    public void testToString() {
    	IPDBAtom atom = builder.newPDBAtom("C");
        String description = atom.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue('\n' != description.charAt(i));
            assertTrue('\r' != description.charAt(i));
        }
    }

    /**
     * Checks that the default charge is set to NaN
     */
    public void testDefaultChargeValue() {
    	IPDBAtom atom = builder.newPDBAtom("C");
        assertEquals(CDKConstants.UNSET, atom.getCharge(), 0.00000001);
    }
    
    public void testGetRecord(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setRecord("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N");
        assertTrue(atom.getRecord().equals("ATOM 1635 N PHE 105 -3.504 9.019 -14.276 1.00 0.00 N"));
    }
    
    public void testSetRecord_String(){
    	testGetRecord();
    }
    
    public void testGetTempFactor(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setTempFactor(0.0);
        assertEquals(atom.getTempFactor(),0.0, 001);
    }
    
    public void testSetTempFactor_double(){
    	testGetTempFactor();
    }
    
    public void testSetResName_String(){
    	testGetResName();
    }
    
    public void testGetResName(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setResName("PHE");
        assertTrue(atom.getResName().equals("PHE"));
    }
    
    public void testSetICode_String(){
    	testGetICode();
    }
    
    public void testGetICode(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setICode("123");
        assertTrue(atom.getICode().equals("123"));
    }
    
    public void testSetChainID_String(){
    	testGetChainID();
    }
    
    public void testGetChainID(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setChainID("123");
        assertTrue(atom.getChainID().equals("123"));
    }
    
    public void testSetAltLoc_String(){
    	testGetAltLoc();
    }
    
    public void testGetAltLoc(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setAltLoc("123");
        assertTrue(atom.getAltLoc().equals("123"));
    }
    
    public void testSetSegID_String(){
    	testGetSegID();
    }
    
    public void testGetSegID(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setSegID("123");
        assertTrue(atom.getSegID().equals("123"));
    }
    
    public void testSetSerial_int(){
    	testGetSerial();
    }
    
    public void testGetSerial(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setSerial(123);
        assertEquals(atom.getSerial(),123);
    }
    
    public void testSetResSeq_String(){
    	testGetResSeq();
    }
    
    public void testGetResSeq(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setResSeq("123");
        assertTrue(atom.getResSeq().equals("123"));
    }
    
    public void testSetOxt_boolean(){
    	testGetOxt();
    }
    
    public void testGetOxt(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setOxt(true);
        assertTrue(atom.getOxt());
    }
    
    public void testSetHetAtom_boolean(){
    	testGetHetAtom();
    }
    
    public void testGetHetAtom(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setHetAtom(true);
        assertTrue(atom.getHetAtom());
    }
    
    public void testSetOccupancy_double(){
    	testGetOccupancy();
    }
    
    public void testGetOccupancy(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setOccupancy(1.0);
        assertEquals(atom.getOccupancy(),1.0,0.01);
    }
    
    public void testGetName(){
    	IPDBAtom atom = builder.newPDBAtom("C");
    	atom.setName("123");
        assertTrue(atom.getName().equals("123"));
    }
    
    public void testSetName_String(){
    	testGetName();
    }
}

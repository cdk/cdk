/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.geometry;

import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.geometry.RDFCalculator;
import org.openscience.cdk.geometry.RDFWeightFunction;
import org.openscience.cdk.test.CDKTestCase;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.RDFCalculator is not broken.
 *
 * @cdk.module    test
 *
 * @author        Egon Willighagen
 * @cdk.created   2005-01-12
 *
 * @see org.openscience.cdk.geometry.RDFCalculator
 */
public class RDFCalculatorTest extends CDKTestCase {

    public RDFCalculatorTest(String name) {
        super(name);
    }
    
    public void setUp() {}
    
    /**
     * Defines a set of tests that can be used in automatic regression testing
     * with JUnit.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(RDFCalculatorTest.class);
        return suite;
    }
    
    public void testRDFCalculator_double_double_double_double() {
        RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0);
        
        assertNotNull(calculator);
    }
    
    public void testRDFCalculator_double_double_double_double_RDFWeightFunction() {
        RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0,
            new RDFWeightFunction() {
                public double calculate(org.openscience.cdk.interfaces.IAtom atom, org.openscience.cdk.interfaces.IAtom atom2) {
                    return 1.0;
                }
            }
        );
        
        assertNotNull(calculator);
    }
    
    public void testCalculate() {
        RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0);
        AtomContainer h2mol = new org.openscience.cdk.AtomContainer();
        Atom h1 = new Atom("H"); h1.setPoint3d(new Point3d(-0.5, 0.0, 0.0));
        Atom h2 = new Atom("H"); h2.setPoint3d(new Point3d( 0.5, 0.0, 0.0));
        h2mol.addAtom(h1); h2mol.addAtom(h2);
        
        double[] rdf1 = calculator.calculate(h2mol, h1);
        double[] rdf2 = calculator.calculate(h2mol, h2);

        // test wether the double array length is ok
        assertEquals(51, rdf1.length);
        
        // test wether the RDFs are identical
        assertEquals(rdf1.length, rdf2.length);
        for (int i=0; i<rdf1.length; i++) {
            assertEquals(rdf1[i], rdf2[i], 0.00001);
        }
        
    }
    
    public void testCalculate_RDFWeightFunction() {
        RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0,
            new RDFWeightFunction() {
                public double calculate(org.openscience.cdk.interfaces.IAtom atom, org.openscience.cdk.interfaces.IAtom atom2) {
                    return 1.0;
                }
            }
        );
        AtomContainer h2mol = new org.openscience.cdk.AtomContainer();
        Atom h1 = new Atom("H"); h1.setPoint3d(new Point3d(-0.5, 0.0, 0.0));
        Atom h2 = new Atom("H"); h2.setPoint3d(new Point3d( 0.5, 0.0, 0.0));
        h2mol.addAtom(h1); h2mol.addAtom(h2);
        
        double[] rdf1 = calculator.calculate(h2mol, h1);
        double[] rdf2 = calculator.calculate(h2mol, h2);

        // test wether the double array length is ok
        assertEquals(51, rdf1.length);
        
        // test wether the RDFs are identical
        assertEquals(rdf1.length, rdf2.length);
        for (int i=0; i<rdf1.length; i++) {
            assertEquals(rdf1[i], rdf2[i], 0.00001);
        }
        
    }
    
    public void testCalculate_RDFWeightFunction2() {
        RDFCalculator calculator = new RDFCalculator(0.0, 5.0, 0.1, 0.0,
            new RDFWeightFunction() {
                public double calculate(org.openscience.cdk.interfaces.IAtom atom, org.openscience.cdk.interfaces.IAtom atom2) {
                    return atom.getCharge()*atom2.getCharge();
                }
            }
        );
        AtomContainer h2mol = new org.openscience.cdk.AtomContainer();
        Atom h1 = new Atom("H"); h1.setPoint3d(new Point3d(-0.5, 0.0, 0.0));
          h1.setCharge(+1.0);
        Atom h2 = new Atom("H"); h2.setPoint3d(new Point3d( 0.5, 0.0, 0.0));
          h2.setCharge(-1.0);
        h2mol.addAtom(h1); h2mol.addAtom(h2);
        
        double[] rdf1 = calculator.calculate(h2mol, h1);
        double[] rdf2 = calculator.calculate(h2mol, h2);

        // test wether the double array length is ok
        assertEquals(51, rdf1.length);
        
        // test wether the RDFs are identical
        assertEquals(rdf1.length, rdf2.length);
        for (int i=0; i<rdf1.length; i++) {
            assertEquals(rdf1[i], rdf2[i], 0.00001);
        }
        
    }
    
}


/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.smiles.smarts;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.smarts.ParseException;
import org.openscience.cdk.smiles.smarts.SMARTSParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @cdk.module test
 * @cdk.require ant1.6
 *
 * @author Egon Willighagen
 */
public class ParserTest extends CDKTestCase {
    
    public ParserTest() {}

    public ParserTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(ParserTest.class);
        return suite;
    }
    
    public void testQueryAtomCreation() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("*");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAliphaticAtom() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("A");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAromaticAtom() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("a");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testDegree() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[D2]");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testImplicitHCount() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[h3]");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testTotalHCount() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[H2]");
            assertEquals(1, container.getAtomCount());
            org.openscience.cdk.interfaces.IAtom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testSingleBond() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("C-C");
            assertEquals(2, container.getAtomCount());
            assertEquals(1, container.getBondCount());
            org.openscience.cdk.interfaces.IBond bond = container.getBondAt(0);
            assertTrue(bond instanceof OrderQueryBond);
            OrderQueryBond qBond = (OrderQueryBond)bond;
            assertEquals(1.0, qBond.getOrder(), 0.001);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testDoubleBond() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("C=C");
            assertEquals(2, container.getAtomCount());
            assertEquals(1, container.getBondCount());
            org.openscience.cdk.interfaces.IBond bond = container.getBondAt(0);
            assertTrue(bond instanceof OrderQueryBond);
            OrderQueryBond qBond = (OrderQueryBond)bond;
            assertEquals(2.0, qBond.getOrder(), 0.001);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testTripleBond() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("C#C");
            assertEquals(2, container.getAtomCount());
            assertEquals(1, container.getBondCount());
            org.openscience.cdk.interfaces.IBond bond = container.getBondAt(0);
            assertTrue(bond instanceof OrderQueryBond);
            OrderQueryBond qBond = (OrderQueryBond)bond;
            assertEquals(3.0, qBond.getOrder(), 0.001);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAromaticBond() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("C:C");
            assertEquals(2, container.getAtomCount());
            assertEquals(1, container.getBondCount());
            org.openscience.cdk.interfaces.IBond bond = container.getBondAt(0);
            assertTrue(bond instanceof AromaticQueryBond);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAnyOrderBond() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("C~C");
            assertEquals(2, container.getAtomCount());
            assertEquals(1, container.getBondCount());
            org.openscience.cdk.interfaces.IBond bond = container.getBondAt(0);
            assertTrue(bond instanceof AnyOrderQueryBond);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

}

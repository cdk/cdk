/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.test.smiles.smarts;

import junit.framework.*;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.smarts.*;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @cdk.module test
 * @cdk.require ant1.6
 *
 * @author Egon Willighagen
 */
public class ParserTest extends TestCase {
    
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
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAliphaticAtom() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("A");
            assertEquals(1, container.getAtomCount());
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }

    public void testAromaticAtom() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("a");
            assertEquals(1, container.getAtomCount());
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testDegree() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[D2]");
            assertEquals(1, container.getAtomCount());
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testImplicitHCount() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[h3]");
            assertEquals(1, container.getAtomCount());
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testTotalHCount() throws ParseException {
        try {
            QueryAtomContainer container = SMARTSParser.parse("[H2]");
            assertEquals(1, container.getAtomCount());
            Atom atom = container.getAtomAt(0);
            assertTrue(atom instanceof SMARTSAtom);
        } catch (CDKException exception) {
            fail(exception.getMessage());
        }
    }
}

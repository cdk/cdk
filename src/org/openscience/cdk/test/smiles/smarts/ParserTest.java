/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2004-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.smiles.smarts.SMARTSParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * JUnit test routines for the SMARTS parser.
 *
 * @cdk.module  test-smarts
 * @cdk.require ant1.6
 *
 * @author      Egon Willighagen
 */
public class ParserTest extends CDKTestCase {
    
    public ParserTest() {}

    public ParserTest(String testName) {
        super(testName);
    }
    
    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }
    
    public void testQueryAtomCreation() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("*");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testAliphaticAtom() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("A");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testAromaticAtom() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("a");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testDegree() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("[D2]");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testImplicitHCount() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("[h3]");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testTotalHCount() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("[H2]");
    	assertEquals(1, container.getAtomCount());
    	org.openscience.cdk.interfaces.IAtom atom = container.getAtom(0);
    	assertTrue(atom instanceof SMARTSAtom);
    }

    public void testSingleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C-C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(1.0, qBond.getOrder(), 0.001);
    }

    public void testDoubleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C=C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(2.0, qBond.getOrder(), 0.001);
    }

    public void testTripleBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C#C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof OrderQueryBond);
    	OrderQueryBond qBond = (OrderQueryBond)bond;
    	assertEquals(3.0, qBond.getOrder(), 0.001);
    }

    public void testAromaticBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C:C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof AromaticQueryBond);
    }

    public void testAnyOrderBond() throws Exception {
    	QueryAtomContainer container = SMARTSParser.parse("C~C");
    	assertEquals(2, container.getAtomCount());
    	assertEquals(1, container.getBondCount());
    	org.openscience.cdk.interfaces.IBond bond = container.getBond(0);
    	assertTrue(bond instanceof AnyOrderQueryBond);
    }

}

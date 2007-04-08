/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * JUnit test routines for the SMARTS substructure search.
 *
 * @cdk.module test-experimental
 * @cdk.require ant1.6
 *
 * @author Egon Willighagen
 */
public class SMARTSSearchTest extends CDKTestCase {
    
    private LoggingTool logger;
    
    public SMARTSSearchTest(String testName) {
        super(testName);
        logger = new LoggingTool(this);
    }
    
    public static Test suite() {
        return new TestSuite(SMARTSSearchTest.class);
    }
    
    public void testAromaticBond() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C:C");
    	logger.debug("Query C:C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C1CCCCC1"); // hexane, not aromatic
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testSingleBond() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C-C");
    	logger.debug("Query C-C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C=C");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C#C");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testDoubleBond() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C=C");
    	logger.debug("Query C=C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C=C");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C#C");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testTripleBond() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C#C");
    	logger.debug("Query C#C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C=C");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C#C");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testAnyOrderBond() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C~C");
    	logger.debug("Query C~C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C=C");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("C#C");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testAnyAtom() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("C*C");
    	logger.debug("Query C*C: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("CNC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("CCN");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testAliphaticAtom() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("CAC");
    	logger.debug("Query CAC: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("CNC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testAromaticAtom() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("aaa");
    	logger.debug("Query CaC: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
        assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }

    public void testSymbolQueryAtom() throws Exception {
    	QueryAtomContainer query = SMARTSParser.parse("CCC");
    	logger.debug("Query CAC: " + query.toString());
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());

    	IAtomContainer atomContainer = sp.parseSmiles("CCC");
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("CNC");
    	assertFalse(UniversalIsomorphismTester.isSubgraph(atomContainer, query));

    	atomContainer = sp.parseSmiles("c1ccccc1"); // benzene, aromatic
    	assertTrue(UniversalIsomorphismTester.isSubgraph(atomContainer, query));
    }
}


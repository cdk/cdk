/* $Revision: 7921 $ $Author: egonw $ $Date: 2007-02-09 00:35:55 +0100 (Fri, 09 Feb 2007) $
 * 
 * Copyright (C) 2007  Rajarshi Guha <>
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;
import org.openscience.cdk.test.CDKTestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JUnit test routines for the SMARTS substructure search.
 *
 * @author Rajarshi Guha
 * @cdk.module test-smarts
 * @cdk.require ant1.6
 */
public class SMARTSQueryToolTest extends CDKTestCase {

    public SMARTSQueryToolTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SMARTSQueryToolTest.class);
    }

    public void testQueryTool() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C");
        SMARTSQueryTool querytool = new SMARTSQueryTool("O=CO");

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        assertEquals(2, nmatch);

        List map1 = new ArrayList();
        map1.add(new Integer(1));
        map1.add(new Integer(2));
        map1.add(new Integer(3));

        List map2 = new ArrayList();
        map2.add(new Integer(3));
        map2.add(new Integer(4));
        map2.add(new Integer(5));

        List mappings = querytool.getMatchingAtoms();
        List ret1 = (List) mappings.get(0);
        Collections.sort(ret1);
        for (int i = 0; i < 3; i++) {
            assertEquals(map1.get(i), ret1.get(i));
        }

        List ret2 = (List) mappings.get(1);
        Collections.sort(ret2);
        for (int i = 0; i < 3; i++) {
            assertEquals(map2.get(i), ret2.get(i));
        }
    }

    public void testQueryToolRings() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCC12CCCC2");
        SMARTSQueryTool querytool = new SMARTSQueryTool("CC");

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        assertEquals(9, nmatch);

    }

    public void testQueryToolSingleAtomCase() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCC12CCCC2");
        SMARTSQueryTool querytool = new SMARTSQueryTool("C");

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        assertEquals(8, nmatch);
    }

    public void testQueryToolResetSmarts() throws CDKException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = sp.parseSmiles("C1CCC12CCCC2");
        SMARTSQueryTool querytool = new SMARTSQueryTool("C");

        boolean status = querytool.matches(atomContainer);
        assertTrue(status);

        int nmatch = querytool.countMatches();
        assertEquals(8, nmatch);

        querytool.setSmarts("CC");
        status = querytool.matches(atomContainer);
        assertTrue(status);

        nmatch = querytool.countMatches();
        assertEquals(9, nmatch);

    }
}

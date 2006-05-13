/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.templates;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.templates.AminoAcids;
import org.openscience.cdk.test.CDKTestCase;

import java.util.HashMap;

/**
 * @cdk.module test-pdb
 */
public class AminoAcidsTest extends CDKTestCase {

    public static Test suite() {
        return new TestSuite(AminoAcidsTest.class);
    }

    public void testCreateBondMatrix() {
    	int[][] bonds = AminoAcids.aaBondInfo();
    	assertNotNull(bonds);
    }
    
    public void testCreateAAs() {
        AminoAcid[] aas = AminoAcids.createAAs();
        assertNotNull(aas);
        assertEquals(20, aas.length);
    }

    public void testGetHashMapBySingleCharCode() {
        HashMap map = AminoAcids.getHashMapBySingleCharCode();
        assertNotNull(map);
        assertEquals(20, map.size());

        String[] aas = { "G", "A", "V", "L" };
        for (int i=0; i < aas.length; i++) {
            AminoAcid aa = (AminoAcid)map.get(aas[i]);
            assertNotNull("Did not find AA for: " + aas[i], aa);
        }
    }

    public void testGetHashMapByThreeLetterCode() {
        HashMap map = AminoAcids.getHashMapByThreeLetterCode();
        assertNotNull(map);
        assertEquals(20, map.size());

        String[] aas = { "GLY", "ALA" };
        for (int i=0; i < aas.length; i++) {
            AminoAcid aa = (AminoAcid)map.get(aas[i]);
            assertNotNull("Did not find AA for: " + aas[i], aa);
        }
    }

}


/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.templates;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AminoAcid;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.test.CDKTestCase;

import java.util.Map;

/**
 * @cdk.module test-pdb
 */
public class AminoAcidsTest extends CDKTestCase {

    @Test
    public void testCreateBondMatrix() {
        int[][] bonds = AminoAcids.aaBondInfo();
        Assertions.assertNotNull(bonds);
    }

    @Test
    public void testCreateAAs() {
        IAminoAcid[] aas = AminoAcids.createAAs();
        Assertions.assertNotNull(aas);
        Assertions.assertEquals(20, aas.length);
        for (int i = 0; i < 20; i++) {
            Assertions.assertNotNull(aas[i]);
            Assertions.assertFalse(0 == aas[i].getAtomCount());
            Assertions.assertFalse(0 == aas[i].getBondCount());
            Assertions.assertNotNull(aas[i].getMonomerName());
            Assertions.assertNotNull(aas[i].getProperty(AminoAcids.RESIDUE_NAME_SHORT));
            Assertions.assertNotNull(aas[i].getProperty(AminoAcids.RESIDUE_NAME));
        }
    }

    @Test
    public void testGetHashMapBySingleCharCode() {
        Map<String, IAminoAcid> map = AminoAcids.getHashMapBySingleCharCode();
        Assertions.assertNotNull(map);
        Assertions.assertEquals(20, map.size());

        String[] aas = {"G", "A", "V", "L"};
        for (String aa1 : aas) {
            AminoAcid aa = (AminoAcid) map.get(aa1);
            Assertions.assertNotNull(aa, "Did not find AA for: " + aa1);
        }
    }

    @Test
    public void testGetHashMapByThreeLetterCode() {
        Map<String, IAminoAcid> map = AminoAcids.getHashMapByThreeLetterCode();
        Assertions.assertNotNull(map);
        Assertions.assertEquals(20, map.size());

        String[] aas = {"GLY", "ALA"};
        for (String aa1 : aas) {
            AminoAcid aa = (AminoAcid) map.get(aa1);
            Assertions.assertNotNull(aa, "Did not find AA for: " + aa1);
        }
    }

}

/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.fingerprint.AbstractFingerprinterTest;

import java.util.Map;

/**
 * @cdk.module test-signature
 */
class SignatureFingerprinterTest extends AbstractFingerprinterTest {

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new SignatureFingerprinter();
    }

    @Test
    void testGetSize() throws Exception {
        IFingerprinter fingerprinter = new SignatureFingerprinter();
        Assertions.assertNotNull(fingerprinter);
        Assertions.assertEquals(-1, fingerprinter.getSize());
    }

    @Test
    @Override
    public void testGetRawFingerprint() throws Exception {
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(0);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O(NC)CC");
        Map<String, Integer> map = fingerprinter.getRawFingerprint(mol);
        Assertions.assertEquals(3, map.size());
        String[] expectedPrints = {"[O]", "[C]", "[N]"};
        for (String print : expectedPrints) {
            Assertions.assertTrue(map.containsKey(print));
        }
    }

    @Test
    void testBitFingerprint() throws Exception {
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(0);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O(NC)CC");
        IBitFingerprint bitFP = fingerprinter.getBitFingerprint(mol);
        Assertions.assertNotNull(bitFP);
        Assertions.assertNotSame(0, bitFP.size());
    }

    @Test
    @Override
    public void testGetCountFingerprint() throws Exception {
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter(0);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O(NC)CC");
        ICountFingerprint bitFP = fingerprinter.getCountFingerprint(mol);
        Assertions.assertNotNull(bitFP);
        Assertions.assertNotSame(0, bitFP.size());
    }
}

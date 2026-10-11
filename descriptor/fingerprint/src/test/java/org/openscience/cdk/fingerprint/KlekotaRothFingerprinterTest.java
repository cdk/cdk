/*
 * Copyright (C) 2011 Jonathan Alvarsson <jonalv@users.sf.net>
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

import java.util.BitSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.fingerprint.AbstractFingerprinterTest;

/**
 */
class KlekotaRothFingerprinterTest extends AbstractFingerprinterTest {

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new KlekotaRothFingerprinter();
    }

    @Test
    void testGetSize() throws Exception {
        IFingerprinter printer = getBitFingerprinter();
        Assertions.assertEquals(4860, printer.getSize());
    }

    @Test
    public void testGetCountFingerprint() throws Exception {
        KlekotaRothFingerprinter printer = new KlekotaRothFingerprinter();
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        int carbon = -1;
        for (int i = 0; i < printer.getSize(); i++) {
            if ("C".equals(printer.getSubstructure(i))) {
                carbon = i;
                break;
            }
        }
        Assertions.assertTrue(carbon >= 0);
        ICountFingerprint ethane = printer.getCountFingerprint(parser.parseSmiles("CC"));
        ICountFingerprint methane = printer.getCountFingerprint(parser.parseSmiles("C"));
        Assertions.assertEquals(2, ethane.getCount(carbon));

        ethane.setBehaveAsBitFingerprint(true);
        Assertions.assertEquals(1, ethane.getCount(carbon));
        ethane.merge(methane);
        Assertions.assertEquals(1, ethane.getCount(carbon));
        ethane.setBehaveAsBitFingerprint(false);
        Assertions.assertEquals(3, ethane.getCount(carbon));
        Assertions.assertEquals(3, ethane.getCountForHash(carbon));
        Assertions.assertEquals(4860, ethane.size());
    }

    @Test
    void testFingerprint() throws Exception {
        SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IFingerprinter printer = getBitFingerprinter();

        BitSet bs1 = printer.getBitFingerprint(parser.parseSmiles("C=C-C#N")).asBitSet();
        BitSet bs2 = printer.getBitFingerprint(parser.parseSmiles("C=CCC(O)CC#N")).asBitSet();

        Assertions.assertEquals(4860, printer.getSize());

        Assertions.assertTrue(FingerprinterTool.isSubset(bs2, bs1));
    }
}

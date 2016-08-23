/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.smarts;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SubstructureSmartsTest {

    private String generate(String smi, boolean peripheral, int[] idxs) throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles(smi);
        SubstructureSmarts subsmarts = new SubstructureSmarts(mol);
        subsmarts.setIncludePeripheralBonds(peripheral);
        return subsmarts.generate(idxs);
    }

    private static int[] makeSeq(int beg, int to) {
        int[] a = new int[to-beg];
        for (int i = 0; i < a.length; i++)
            a[i] = beg++;
        return a;
    }

    @Test
    public void indole() throws Exception {
        String smarts = generate("[nH]1ccc2c1cccc2",
                                 false,
                                 makeSeq(0,4));
        assertThat(smarts, is("nccc"));
    }

    @Test
    public void indoleWithPeripheral() throws Exception {
        String smarts = generate("[nH]1ccc2c1cccc2",
                                 true,
                                 makeSeq(0,4));
        assertThat(smarts, is("n(ccc(a)a)a"));
    }

    @Test
    public void biphenylIncludesSingleBond() throws Exception {
        String smarts = generate("c1ccccc1-c1ccccc1",
                                 false,
                                 makeSeq(0,12));
        assertThat(smarts, containsString("-"));
    }

    @Test
    public void fullereneC60() throws Exception {
        String smarts = generate("c12c3c4c5c1c1c6c7c2c2c8c3c3c9c4c4c%10c5c5c1c1c6c6c%11c7c2c2c7c8c3c3c8c9c4c4c9c%10c5c5c1c1c6c6c%11c2c2c7c3c3c8c4c4c9c5c1c1c6c2c3c41",
                                 false,
                                 makeSeq(0,60));
        assertThat(smarts,
                   is("c12c3c4c5c1c1c6c7c2c2c8c3c3c9c4c4c%10c5c5c1c1c6c6c%11c7c2c2c7c8c3c3c8c9c4c4c9c%10c5c5c1c1c6c6c%11c2c2c7c3c3c8c4c4c9c5c1c1c6c2c3c41"));
    }

}
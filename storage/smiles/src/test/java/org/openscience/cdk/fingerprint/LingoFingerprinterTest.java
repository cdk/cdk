/*
 * Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-smiles
 */
public class LingoFingerprinterTest extends AbstractFingerprinterTest {

    boolean standAlone = false;

    @Override
    public IFingerprinter getBitFingerprinter() {
        return new LingoFingerprinter();
    }

    @Test
    public void testGetSize() throws Exception {
        IFingerprinter fingerprinter = new LingoFingerprinter();
        Assert.assertNotNull(fingerprinter);
        Assert.assertEquals(-1, fingerprinter.getSize());
    }

    
    @Test
    @Override
    public void testGetCountFingerprint() throws Exception {
        LingoFingerprinter fpr = new LingoFingerprinter(4);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Oc1ccccc1");
        ICountFingerprint fp = fpr.getCountFingerprint(mol);
        assertThat(fp.getCountForHash("cccc".hashCode()), is(2));
        assertThat(fp.getCountForHash("Oc0c".hashCode()), is(1));
        assertThat(fp.getCountForHash("c0cc".hashCode()), is(1));
        assertThat(fp.getCountForHash("0ccc".hashCode()), is(1));
        assertThat(fp.getCountForHash("ccc0".hashCode()), is(1));
    }

    @Test
    @Override
    public void testGetRawFingerprint() throws Exception {
        LingoFingerprinter lfp = new LingoFingerprinter(3);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("SPONC");
        Map<String, Integer> map = lfp.getRawFingerprint(mol);
        Assert.assertEquals(3, map.size());
        // depend on canonical ordering of the SMILES since lingos uses Unique SMILES
        String[] subs = {"PON", "ONC", "SPO"};
        for (String s : subs)
            Assert.assertTrue(map.containsKey(s));
    }
}

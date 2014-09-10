/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.smiles.smarts;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 */
public class SmartsPatternTest {

    IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();

    @Test
    public void ringSizeOrNumber_membership() throws Exception {
        assertFalse(SmartsPattern.ringSizeOrNumber("[R]"));
    }

    @Test
    public void ringSizeOrNumber_ringConnectivity() throws Exception {
        assertFalse(SmartsPattern.ringSizeOrNumber("[X2]"));
    }

    @Test
    public void ringSizeOrNumber_elements() throws Exception {
        assertFalse(SmartsPattern.ringSizeOrNumber("[Br]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Cr]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Fr]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Sr]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Ra]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Re]"));
        assertFalse(SmartsPattern.ringSizeOrNumber("[Rf]"));
    }

    @Test
    public void ringSizeOrNumber_negatedMembership() throws Exception {
        assertTrue(SmartsPattern.ringSizeOrNumber("[!R]"));
    }

    @Test
    public void ringSizeOrNumber_membershipZero() throws Exception {
        assertTrue(SmartsPattern.ringSizeOrNumber("[R0]"));
    }

    @Test
    public void ringSizeOrNumber_membershipTwo() throws Exception {
        assertTrue(SmartsPattern.ringSizeOrNumber("[R2]"));
    }

    @Test
    public void ringSizeOrNumber_ringSize() throws Exception {
        assertTrue(SmartsPattern.ringSizeOrNumber("[r5]"));
    }

    @Test
    public void components() throws Exception {
        assertTrue(SmartsPattern.create("(O).(O)", bldr).matches(smi("O.O")));
        assertFalse(SmartsPattern.create("(O).(O)", bldr).matches(smi("OO")));
    }

    @Test
    public void stereochemistry() throws Exception {
        assertTrue(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("C[C@H](O)CC")));
        assertFalse(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("C[C@@H](O)CC")));
        assertFalse(SmartsPattern.create("C[C@H](O)CC", bldr).matches(smi("CC(O)CC")));
    }

    IAtomContainer smi(String smi) throws Exception {
        return new SmilesParser(bldr).parseSmiles(smi);
    }
}

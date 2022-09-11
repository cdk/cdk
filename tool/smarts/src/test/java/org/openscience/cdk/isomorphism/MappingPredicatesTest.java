/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
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

package org.openscience.cdk.isomorphism;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author John May
 * @cdk.module test-smarts
 */
class MappingPredicatesTest {

    @Test
    void uniqueAtoms() throws Exception {
        UniqueAtomMatches uam = new UniqueAtomMatches();
        assertTrue(uam.apply(new int[]{1, 2, 3, 4}));
        assertTrue(uam.apply(new int[]{1, 2, 3, 5}));
        Assertions.assertFalse(uam.apply(new int[]{4, 3, 2, 1}));
        Assertions.assertFalse(uam.apply(new int[]{1, 5, 2, 3}));
    }

    @Test
    void uniqueAtomsUniqSet()  {
        UniqueAtomMatches uam = new UniqueAtomMatches();
        assertTrue(uam.apply(new int[]{1, 2, 3}));
        assertTrue(uam.apply(new int[]{4, 2, 5}));
        // seen 3 and 2 and 5 on their own before but not all together
        // this matches other toolkits so is what most would expect but
        // an alternative unique overall which would reject this as a duplicate.
        assertTrue(uam.apply(new int[]{3, 2, 5}));
    }

    @Test
    void uniqueBonds() throws Exception {

        IAtomContainer query = smi("C1CCC1");
        IAtomContainer target = smi("C12C3C1C23");

        Mappings mappings = VentoFoggia.findSubstructure(query).matchAll(target);

        // using unique atoms we may think we only found 1 mapping
        assertThat(mappings.stream().filter(new UniqueAtomMatches()).count(), is(1L));

        // when in fact we found 3 different mappings
        assertThat(mappings.stream().filter(new UniqueBondMatches(GraphUtil.toAdjList(query))).count(), is(3L));
    }

    @Test
    void uniqueAtoms_multipleIterations() throws Exception {
        IAtomContainer ethane = smi("CC");
        IAtomContainer ethanol = smi("CCO");
        Mappings mappings = Pattern.findSubstructure(ethane).matchAll(ethanol);
        assertThat(mappings.countUnique(), is(1));
        assertThat(mappings.countUnique(), is(1)); // re-iteration
    }

    @Test
    void uniqueBonds_multipleIterations() throws Exception {
        IAtomContainer ethane = smi("CC");
        IAtomContainer ethanol = smi("CCO");
        Mappings mappings = Pattern.findSubstructure(ethane).matchAll(ethanol);
        assertThat(mappings.uniqueBonds().count(), is(1));
        assertThat(mappings.uniqueBonds().count(), is(1)); // re-iteration
    }

    private final IChemObjectBuilder bldr   = SilentChemObjectBuilder.getInstance();
    private final SmilesParser       smipar = new SmilesParser(bldr);

    IAtomContainer smi(String smi) throws Exception {
        return smipar.parseSmiles(smi);
    }

}

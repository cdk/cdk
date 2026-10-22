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

package org.openscience.cdk.isomorphism;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
class MappingsTest {

    @Test
    void filter() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), Arrays.asList(p1, p2, p3, p4));

        Predicate<int[]> f = p -> Arrays.equals(p, p2) || Arrays.equals(p, p4);

        assertThat(ms.filter(f).toArray(), is(new int[][]{p2, p4}));
    }

    @Test
    void map() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        List<int[]> mappings = Arrays.asList(p1, p2, p3, p4);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), mappings);

        int[] calls = {0};
        Function<int[], String> f = p -> {
            calls[0]++;
            for (int i = 0; i < mappings.size(); i++) {
                if (Arrays.equals(p, mappings.get(i)))
                    return "p" + (i + 1);
            }
            throw new AssertionError("Unexpected mapping: " + Arrays.toString(p));
        };

        Iterable<String> strings = ms.map(f);
        Iterator<String> stringIt = strings.iterator();

        Assertions.assertEquals(0, calls[0]);

        Assertions.assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p1"));
        Assertions.assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p2"));
        Assertions.assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p3"));
        Assertions.assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p4"));
        Assertions.assertFalse(stringIt.hasNext());

        Assertions.assertEquals(4, calls[0]);
    }

    @Test
    void limit() throws Exception {
        CountingIterator iterator = new CountingIterator(new int[0], new int[0], new int[0], new int[0], new int[0]);
        Iterable<int[]> iterable = () -> iterator;

        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), iterable);
        assertThat(ms.limit(2).count(), is(2));
        Assertions.assertEquals(2, iterator.nextCalls);
    }

    @Test
    void stereochemistry() throws Exception {
        // tested by filter() + StereoMatchTest
    }

    @Test
    void uniqueAtoms() throws Exception {
        // tested by filter() + MappingPredicatesTest
    }

    @Test
    void uniqueBonds() throws Exception {
        // tested by filter() + MappingPredicatesTest
    }

    @Test
    void toArray() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), Arrays.asList(p1, p2, p3, p4));
        assertThat(ms.toArray(), is(new int[][]{p1, p2, p3, p4}));
    }

    @Test
    void toAtomMap() throws Exception {

        IAtomContainer query = smi("CC");
        IAtomContainer target = smi("CC");

        Iterable<Map<IAtom, IAtom>> iterable = Pattern.findIdentical(query).matchAll(target).toAtomMap();
        Iterator<Map<IAtom, IAtom>> iterator = iterable.iterator();

        Assertions.assertTrue(iterator.hasNext());
        Map<IAtom, IAtom> m1 = iterator.next();
        assertThat(m1.get(query.getAtom(0)), is(target.getAtom(0)));
        assertThat(m1.get(query.getAtom(1)), is(target.getAtom(1)));
        Assertions.assertTrue(iterator.hasNext());
        Map<IAtom, IAtom> m2 = iterator.next();
        assertThat(m2.get(query.getAtom(0)), is(target.getAtom(1)));
        assertThat(m2.get(query.getAtom(1)), is(target.getAtom(0)));
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void toBondMap() throws Exception {
        IAtomContainer query = smi("CCC");
        IAtomContainer target = smi("CCC");

        Iterable<Map<IBond, IBond>> iterable = Pattern.findIdentical(query).matchAll(target).toBondMap();
        Iterator<Map<IBond, IBond>> iterator = iterable.iterator();

        Assertions.assertTrue(iterator.hasNext());
        Map<IBond, IBond> m1 = iterator.next();
        assertThat(m1.get(query.getBond(0)), is(target.getBond(0)));
        assertThat(m1.get(query.getBond(1)), is(target.getBond(1)));
        Assertions.assertTrue(iterator.hasNext());
        Map<IBond, IBond> m2 = iterator.next();
        assertThat(m2.get(query.getBond(0)), is(target.getBond(1)));
        assertThat(m2.get(query.getBond(1)), is(target.getBond(0)));
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void toAtomBondMap() throws Exception {
        IAtomContainer query = smi("CCC");
        IAtomContainer target = smi("CCC");

        Iterable<Map<IChemObject, IChemObject>> iterable = Pattern.findIdentical(query).matchAll(target).toAtomBondMap();
        Iterator<Map<IChemObject, IChemObject>> iterator = iterable.iterator();

        Assertions.assertTrue(iterator.hasNext());
        Map<IChemObject, IChemObject> m1 = iterator.next();
        assertThat(m1.get(query.getAtom(0)), is(target.getAtom(0)));
        assertThat(m1.get(query.getAtom(1)), is(target.getAtom(1)));
        assertThat(m1.get(query.getAtom(2)), is(target.getAtom(2)));
        assertThat(m1.get(query.getBond(0)), is(target.getBond(0)));
        assertThat(m1.get(query.getBond(1)), is(target.getBond(1)));
        Assertions.assertTrue(iterator.hasNext());
        Map<IChemObject, IChemObject> m2 = iterator.next();
        assertThat(m2.get(query.getAtom(0)), is(target.getAtom(2)));
        assertThat(m2.get(query.getAtom(1)), is(target.getAtom(1)));
        assertThat(m2.get(query.getAtom(2)), is(target.getAtom(0)));
        assertThat(m2.get(query.getBond(0)), is(target.getBond(1)));
        assertThat(m2.get(query.getBond(1)), is(target.getBond(0)));
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void toSubstructures() throws Exception {
        IAtomContainer query  = smi("O1CC1");
        IAtomContainer target = smi("C1OC1CCC");

        Iterable<IAtomContainer> iterable = Pattern.findSubstructure(query)
                                                   .matchAll(target)
                                                   .uniqueAtoms()
                                                   .toSubstructures();
        Iterator<IAtomContainer> iterator = iterable.iterator();

        Assertions.assertTrue(iterator.hasNext());
        IAtomContainer submol = iterator.next();
        assertThat(submol, is(not(query)));
        // note that indices are mapped from query to target
        assertThat(submol.getAtom(0), is(target.getAtom(1))); // oxygen
        assertThat(submol.getAtom(1), is(target.getAtom(0))); // C
        assertThat(submol.getAtom(2), is(target.getAtom(2))); // C
        assertThat(submol.getBond(0), is(target.getBond(0))); // C-O bond
        assertThat(submol.getBond(1), is(target.getBond(2))); // O-C bond
        assertThat(submol.getBond(2), is(target.getBond(1))); // C-C bond
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void atLeast() throws Exception {
        CountingIterator iterator = new CountingIterator(new int[0], new int[0], new int[0], new int[0], new int[0]);
        Iterable<int[]> iterable = () -> iterator;

        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), iterable);
        Assertions.assertTrue(ms.atLeast(2));
        Assertions.assertEquals(2, iterator.nextCalls);
    }

    @Test
    void first() throws Exception {
        int[] p1 = new int[0];
        int[] p2 = new int[0];

        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), Arrays.asList(p1, p2));
        assertThat(ms.first(), is(sameInstance(p1)));
    }

    @Test
    void count() throws Exception {
        List<int[]> list = Arrays.asList(new int[0],
                                             new int[0],
                                             new int[0],
                                             new int[0],
                                             new int[0]);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), list);
        assertThat(ms.count(), is(5));
    }

    @Test
    void countUnique() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};
        List<int[]> list = Arrays.asList(p1, p2, p3, p4);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), list);
        assertThat(ms.countUnique(), is(2));
    }

    @Test
    void countUnique2() throws Exception {
        int[] p1 = {0, 1};
        int[] p2 = {0, 2};
        int[] p3 = {0, 3};
        int[] p4 = {0, 4};
        List<int[]> list = Arrays.asList(p1, p2, p3, p4);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), list);
        assertThat(ms.countUnique(), is(4));
    }

    @Test
    void countExclusive() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};
        List<int[]> list = Arrays.asList(p1, p2, p3, p4);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), list);
        assertThat(ms.exclusiveAtoms().count(), is(1));
    }

    @Test
    void countExclusive2() throws Exception {
        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {5, 3, 4};
        int[] p4 = {5, 4, 3};
        List<int[]> list = Arrays.asList(p1, p2, p3, p4);
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), list);
        assertThat(ms.exclusiveAtoms().count(), is(2));
    }

    @Test
    void iterator() throws Exception {
        Iterator<int[]> iterator = Arrays.asList(new int[0]).iterator();
        Iterable<int[]> iterable = () -> iterator;
        Mappings ms = new Mappings(new AtomContainer(), new AtomContainer(), iterable);
        assertThat(ms.iterator(), is(sameInstance(iterator)));
    }

    private static final class CountingIterator implements Iterator<int[]> {
        private final Iterator<int[]> delegate;
        private int nextCalls;

        CountingIterator(int[]... mappings) {
            delegate = Arrays.asList(mappings).iterator();
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public int[] next() {
            nextCalls++;
            return delegate.next();
        }
    }

    private final IChemObjectBuilder bldr   = SilentChemObjectBuilder.getInstance();
    private final SmilesParser       smipar = new SmilesParser(bldr);

    IAtomContainer smi(String smi) throws Exception {
        return smipar.parseSmiles(smi);
    }
}

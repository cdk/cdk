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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import org.junit.Test;
import org.mockito.Matchers;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.Iterator;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-smarts
 */
public class MappingsTest {

    @Test
    @SuppressWarnings("unchecked")
    public void filter() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);

        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        when(iterator.hasNext()).thenReturn(true, true, true, true, false);
        when(iterator.next()).thenReturn(p1, p2, p3, p4);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);

        Predicate<int[]> f = mock(Predicate.class);
        when(f.apply(p1)).thenReturn(false);
        when(f.apply(p2)).thenReturn(true);
        when(f.apply(p3)).thenReturn(false);
        when(f.apply(p4)).thenReturn(true);

        assertThat(ms.filter(f).toArray(), is(new int[][]{p2, p4}));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void map() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);

        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        when(iterator.hasNext()).thenReturn(true, true, true, true, false);
        when(iterator.next()).thenReturn(p1, p2, p3, p4);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);

        Function<int[], String> f = mock(Function.class);
        when(f.apply(p1)).thenReturn("p1");
        when(f.apply(p2)).thenReturn("p2");
        when(f.apply(p3)).thenReturn("p3");
        when(f.apply(p4)).thenReturn("p4");

        Iterable<String> strings = ms.map(f);
        Iterator<String> stringIt = strings.iterator();

        verify(f, atMost(0)).apply(Matchers.<int[]> any());

        assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p1"));
        assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p2"));
        assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p3"));
        assertTrue(stringIt.hasNext());
        assertThat(stringIt.next(), is("p4"));
        assertFalse(stringIt.hasNext());

        verify(f, atMost(4)).apply(Matchers.<int[]> any());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void limit() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, true, true, true, false);
        when(iterator.next()).thenReturn(new int[0]);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.limit(2).count(), is(2));
        verify(iterator, atMost(2)).next(); // was only called twice
    }

    @Test
    public void stereochemistry() throws Exception {
        // tested by filter() + StereoMatchTest
    }

    @Test
    public void uniqueAtoms() throws Exception {
        // tested by filter() + MappingPredicatesTest
    }

    @Test
    public void uniqueBonds() throws Exception {
        // tested by filter() + MappingPredicatesTest
    }

    @Test
    @SuppressWarnings("unchecked")
    public void toArray() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, true, true, false);

        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        when(iterator.next()).thenReturn(p1, p2, p3, p4);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.toArray(), is(new int[][]{p1, p2, p3, p4}));
    }

    @Test
    public void toAtomMap() throws Exception {

        IAtomContainer query = smi("CC");
        IAtomContainer target = smi("CC");

        Iterable<Map<IAtom, IAtom>> iterable = Pattern.findIdentical(query).matchAll(target).toAtomMap();
        Iterator<Map<IAtom, IAtom>> iterator = iterable.iterator();

        assertTrue(iterator.hasNext());
        Map<IAtom, IAtom> m1 = iterator.next();
        assertThat(m1.get(query.getAtom(0)), is(target.getAtom(0)));
        assertThat(m1.get(query.getAtom(1)), is(target.getAtom(1)));
        assertTrue(iterator.hasNext());
        Map<IAtom, IAtom> m2 = iterator.next();
        assertThat(m2.get(query.getAtom(0)), is(target.getAtom(1)));
        assertThat(m2.get(query.getAtom(1)), is(target.getAtom(0)));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toBondMap() throws Exception {
        IAtomContainer query = smi("CCC");
        IAtomContainer target = smi("CCC");

        Iterable<Map<IBond, IBond>> iterable = Pattern.findIdentical(query).matchAll(target).toBondMap();
        Iterator<Map<IBond, IBond>> iterator = iterable.iterator();

        assertTrue(iterator.hasNext());
        Map<IBond, IBond> m1 = iterator.next();
        assertThat(m1.get(query.getBond(0)), is(target.getBond(0)));
        assertThat(m1.get(query.getBond(1)), is(target.getBond(1)));
        assertTrue(iterator.hasNext());
        Map<IBond, IBond> m2 = iterator.next();
        assertThat(m2.get(query.getBond(0)), is(target.getBond(1)));
        assertThat(m2.get(query.getBond(1)), is(target.getBond(0)));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toAtomBondMap() throws Exception {
        IAtomContainer query = smi("CCC");
        IAtomContainer target = smi("CCC");

        Iterable<Map<IChemObject, IChemObject>> iterable = Pattern.findIdentical(query).matchAll(target).toAtomBondMap();
        Iterator<Map<IChemObject, IChemObject>> iterator = iterable.iterator();

        assertTrue(iterator.hasNext());
        Map<IChemObject, IChemObject> m1 = iterator.next();
        assertThat(m1.get(query.getAtom(0)), is((IChemObject)target.getAtom(0)));
        assertThat(m1.get(query.getAtom(1)), is((IChemObject)target.getAtom(1)));
        assertThat(m1.get(query.getAtom(2)), is((IChemObject)target.getAtom(2)));
        assertThat(m1.get(query.getBond(0)), is((IChemObject)target.getBond(0)));
        assertThat(m1.get(query.getBond(1)), is((IChemObject)target.getBond(1)));
        assertTrue(iterator.hasNext());
        Map<IChemObject, IChemObject> m2 = iterator.next();
        assertThat(m2.get(query.getAtom(0)), is((IChemObject)target.getAtom(2)));
        assertThat(m2.get(query.getAtom(1)), is((IChemObject)target.getAtom(1)));
        assertThat(m2.get(query.getAtom(2)), is((IChemObject)target.getAtom(0)));
        assertThat(m2.get(query.getBond(0)), is((IChemObject)target.getBond(1)));
        assertThat(m2.get(query.getBond(1)), is((IChemObject)target.getBond(0)));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void toSubstructures() throws Exception {
        IAtomContainer query  = smi("O1CC1");
        IAtomContainer target = smi("C1OC1CCC");

        Iterable<IAtomContainer> iterable = Pattern.findSubstructure(query)
                                                   .matchAll(target)
                                                   .uniqueAtoms()
                                                   .toSubstructures();
        Iterator<IAtomContainer> iterator = iterable.iterator();

        assertTrue(iterator.hasNext());
        IAtomContainer submol = iterator.next();
        assertThat(submol, is(not(query)));
        // note that indices are mapped from query to target
        assertThat(submol.getAtom(0), is(target.getAtom(1))); // oxygen
        assertThat(submol.getAtom(1), is(target.getAtom(0))); // C
        assertThat(submol.getAtom(2), is(target.getAtom(2))); // C
        assertThat(submol.getBond(0), is(target.getBond(0))); // C-O bond
        assertThat(submol.getBond(1), is(target.getBond(2))); // O-C bond
        assertThat(submol.getBond(2), is(target.getBond(1))); // C-C bond
        assertFalse(iterator.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void atLeast() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, true, true, true, false);
        when(iterator.next()).thenReturn(new int[0]);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertTrue(ms.atLeast(2));
        verify(iterator, atMost(2)).next(); // was only called twice
    }

    @Test
    @SuppressWarnings("unchecked")
    public void first() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, false);

        int[] p1 = new int[0];
        int[] p2 = new int[0];

        when(iterator.next()).thenReturn(p1, new int[][]{p2});

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.first(), is(sameInstance(p1)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void count() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, true, true, true, false);
        when(iterator.next()).thenReturn(new int[0]);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.count(), is(5));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void countUnique() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, true, true, true, false);

        int[] p1 = {0, 1, 2};
        int[] p2 = {0, 2, 1};
        int[] p3 = {0, 3, 4};
        int[] p4 = {0, 4, 3};

        when(iterator.next()).thenReturn(p1, p2, p3, p4);

        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.countUnique(), is(2));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void iterator() throws Exception {
        Iterable<int[]> iterable = mock(Iterable.class);
        Iterator<int[]> iterator = mock(Iterator.class);
        when(iterable.iterator()).thenReturn(iterator);
        Mappings ms = new Mappings(mock(IAtomContainer.class), mock(IAtomContainer.class), iterable);
        assertThat(ms.iterator(), is(sameInstance(iterator)));
    }

    IChemObjectBuilder bldr   = SilentChemObjectBuilder.getInstance();
    SmilesParser       smipar = new SmilesParser(bldr);

    IAtomContainer smi(String smi) throws Exception {
        return smipar.parseSmiles(smi);
    }
}

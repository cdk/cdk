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

package org.openscience.cdk.io;

import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 * @cdk.module test-io
 */
public class MDLValenceTest {

    @Test
    public void sodium_metal() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("Na");
        atom.setValency(0);
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(0));
        assertThat(atom.getImplicitHydrogenCount(), is(0));
    }

    @Test
    public void sodium_hydride() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("Na");
        atom.setValency(1);
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(1));
        assertThat(atom.getImplicitHydrogenCount(), is(1));
    }

    @Test
    public void sodium_implicit() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("Na");
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(1));
        assertThat(atom.getImplicitHydrogenCount(), is(1));
    }

    @Test
    public void bismuth() {
        IAtomContainer container = new AtomContainer();
        IAtom bi1 = new Atom("Bi");
        IAtom h2 = new Atom("H");
        bi1.setFormalCharge(+2);
        container.addAtom(bi1);
        container.addAtom(h2);
        container.addBond(0, 1, IBond.Order.SINGLE);
        MDLValence.apply(container);
        assertThat(bi1.getValency(), is(3));
        assertThat(h2.getValency(), is(1));
        assertThat(bi1.getImplicitHydrogenCount(), is(2));
        assertThat(h2.getImplicitHydrogenCount(), is(0));
    }

    @Test
    public void tin_ii() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("Sn");
        atom.setValency(2);
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(2));
        assertThat(atom.getImplicitHydrogenCount(), is(2));
    }

    @Test
    public void tin_iv() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("Sn");
        atom.setValency(4);
        IAtom hydrogen = new Atom("H");
        container.addAtom(atom);
        container.addAtom(hydrogen);
        container.addBond(0, 1, IBond.Order.SINGLE);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(4));
        assertThat(atom.getImplicitHydrogenCount(), is(3)); // 4 - explicit H
    }

    @Test
    public void carbon_neutral() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("C");
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(4));
        assertThat(atom.getImplicitHydrogenCount(), is(4));
    }

    @Test
    public void carbon_cation() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("C");
        atom.setFormalCharge(-1);
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(3));
        assertThat(atom.getImplicitHydrogenCount(), is(3));
    }

    @Test
    public void carbon_cation_doubleBonded() {
        IAtomContainer container = new AtomContainer();
        IAtom c1 = new Atom("C");
        IAtom c2 = new Atom("C");
        c1.setFormalCharge(-1);
        container.addAtom(c1);
        container.addAtom(c2);
        container.addBond(0, 1, IBond.Order.DOUBLE);
        MDLValence.apply(container);
        assertThat(c1.getValency(), is(3));
        assertThat(c1.getImplicitHydrogenCount(), is(1));
        assertThat(c2.getValency(), is(4));
        assertThat(c2.getImplicitHydrogenCount(), is(2));
    }

    @Test
    public void carbon_anion() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("C");
        atom.setFormalCharge(+1);
        container.addAtom(atom);
        MDLValence.apply(container);
        assertThat(atom.getValency(), is(3));
        assertThat(atom.getImplicitHydrogenCount(), is(3));
    }

    @Test
    public void bismuth_isImplicit() {
        IAtomContainer container = new AtomContainer();
        IAtom bi1 = new Atom("Bi");
        IAtom h2 = new Atom("H");
        bi1.setFormalCharge(+2);
        container.addAtom(bi1);
        container.addAtom(h2);
        container.addBond(0, 1, IBond.Order.SINGLE);
        MDLValence.apply(container);
        assertThat(bi1.getValency(), is(3));
        assertThat(h2.getValency(), is(1));
        assertThat(bi1.getImplicitHydrogenCount(), is(2));
        assertThat(h2.getImplicitHydrogenCount(), is(0));
    }

    @Test
    public void nitrogen_neutral() {
        assertThat(MDLValence.implicitValence(7, 0, 0), is(3));
        assertThat(MDLValence.implicitValence(7, 0, 1), is(3));
        assertThat(MDLValence.implicitValence(7, 0, 2), is(3));
        assertThat(MDLValence.implicitValence(7, 0, 3), is(3));
        assertThat(MDLValence.implicitValence(7, 0, 4), is(5));
        assertThat(MDLValence.implicitValence(7, 0, 5), is(5));
        assertThat(MDLValence.implicitValence(7, 0, 6), is(6));
    }

    @Test
    public void nitrogen_cation() {
        assertThat(MDLValence.implicitValence(7, +1, 0), is(4));
        assertThat(MDLValence.implicitValence(7, +1, 1), is(4));
        assertThat(MDLValence.implicitValence(7, +1, 2), is(4));
        assertThat(MDLValence.implicitValence(7, +1, 3), is(4));
        assertThat(MDLValence.implicitValence(7, +1, 4), is(4));
        assertThat(MDLValence.implicitValence(7, +1, 5), is(5));
        assertThat(MDLValence.implicitValence(7, +1, 6), is(6));
    }

    @Test
    public void nitrogen_anion() {
        assertThat(MDLValence.implicitValence(7, -1, 0), is(2));
        assertThat(MDLValence.implicitValence(7, -1, 1), is(2));
        assertThat(MDLValence.implicitValence(7, -1, 2), is(2));
        assertThat(MDLValence.implicitValence(7, -1, 3), is(3));
        assertThat(MDLValence.implicitValence(7, -1, 4), is(4));
        assertThat(MDLValence.implicitValence(7, -1, 5), is(5));
        assertThat(MDLValence.implicitValence(7, -1, 6), is(6));
    }
}

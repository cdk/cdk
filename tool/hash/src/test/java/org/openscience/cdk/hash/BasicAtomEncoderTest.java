/*
 * Copyright (c) 2013. John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.junit.Test;
import org.openscience.cdk.hash.AtomEncoder;
import org.openscience.cdk.hash.BasicAtomEncoder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author John May
 * @cdk.module test-hash
 */
public class BasicAtomEncoderTest {

    @Test
    public void testAtomicNumber() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.ATOMIC_NUMBER;

        when(atom.getAtomicNumber()).thenReturn(6);
        assertThat(encoder.encode(atom, container), is(6));

        verify(atom, times(1)).getAtomicNumber();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testAtomicNumber_Null() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.ATOMIC_NUMBER;

        when(atom.getAtomicNumber()).thenReturn(null);
        assertThat(encoder.encode(atom, container), is(32451169));
        verify(atom, times(1)).getAtomicNumber();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testMassNumber() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.MASS_NUMBER;

        when(atom.getMassNumber()).thenReturn(12);
        assertThat(encoder.encode(atom, container), is(12));

        verify(atom, times(1)).getMassNumber();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testMassNumber_Null() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.MASS_NUMBER;

        when(atom.getMassNumber()).thenReturn(null);
        assertThat(encoder.encode(atom, container), is(32451179));
        verify(atom, times(1)).getMassNumber();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testFormalNumber() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.FORMAL_CHARGE;

        when(atom.getFormalCharge()).thenReturn(-2);
        assertThat(encoder.encode(atom, container), is(-2));

        verify(atom, times(1)).getFormalCharge();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testFormalNumber_Null() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.FORMAL_CHARGE;

        when(atom.getFormalCharge()).thenReturn(null);
        assertThat(encoder.encode(atom, container), is(32451193));
        verify(atom, times(1)).getFormalCharge();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testNConnectedAtoms() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.N_CONNECTED_ATOMS;

        when(container.getConnectedBondsCount(atom)).thenReturn(2);
        assertThat(encoder.encode(atom, container), is(2));
        verify(container, times(1)).getConnectedBondsCount(atom);
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testBondOrderSum() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.BOND_ORDER_SUM;

        when(container.getBondOrderSum(atom)).thenReturn(3D);
        assertThat(encoder.encode(atom, container), is(new Double(3D).hashCode()));
        verify(container, times(1)).getBondOrderSum(atom);
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testOrbitalHybridization() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.ORBITAL_HYBRIDIZATION;

        when(atom.getHybridization()).thenReturn(IAtomType.Hybridization.SP2);
        assertThat(encoder.encode(atom, container), is(IAtomType.Hybridization.SP2.ordinal()));

        verify(atom, times(1)).getHybridization();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testOrbitalHybridization_Null() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.ORBITAL_HYBRIDIZATION;

        when(atom.getHybridization()).thenReturn(null);
        assertThat(encoder.encode(atom, container), is(32451301));
        verify(atom, times(1)).getHybridization();
        verifyNoMoreInteractions(atom, container);
    }

    @Test
    public void testFreeRadicals() {
        IAtom atom = mock(IAtom.class);
        IAtomContainer container = mock(IAtomContainer.class);
        AtomEncoder encoder = BasicAtomEncoder.FREE_RADICALS;

        when(container.getConnectedSingleElectronsCount(atom)).thenReturn(1);
        assertThat(encoder.encode(atom, container), is(1));
        verify(container, times(1)).getConnectedSingleElectronsCount(atom);
        verifyNoMoreInteractions(atom, container);
    }
}

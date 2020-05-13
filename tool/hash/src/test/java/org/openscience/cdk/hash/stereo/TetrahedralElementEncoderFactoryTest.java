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

package org.openscience.cdk.hash.stereo;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * See {@link org.openscience.cdk.hash.HashCodeScenariosTest} for examples.
 * @author John May
 * @cdk.module test-hash
 * @see org.openscience.cdk.hash.HashCodeScenariosTest
 */
public class TetrahedralElementEncoderFactoryTest {

    @Test
    public void createExplicitH() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(5);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);
        IAtom h5 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);
        when(container.getAtom(4)).thenReturn(h5);

        when(container.atoms()).thenReturn(Arrays.asList(c1, o2, n3, c4, h5));

        ITetrahedralChirality tc = mock(ITetrahedralChirality.class);
        when(tc.getChiralAtom()).thenReturn(c1);
        when(tc.getLigands()).thenReturn(new IAtom[]{o2, n3, c4, h5});
        when(tc.getStereo()).thenReturn(ITetrahedralChirality.Stereo.CLOCKWISE);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(tc));

        StereoEncoder encoder = new TetrahedralElementEncoderFactory().create(container, new int[0][0]); // graph not used

        assertThat(getGeometricParity(encoder).parity(), is(-1)); // clockwise
    }

    @Test
    public void createImplicitH_back() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);

        when(container.atoms()).thenReturn(Arrays.asList(c1, o2, n3, c4));

        ITetrahedralChirality tc = mock(ITetrahedralChirality.class);
        when(tc.getChiralAtom()).thenReturn(c1);
        when(tc.getLigands()).thenReturn(new IAtom[]{o2, n3, c4, c1 // <-- represents implicit H
                });
        when(tc.getStereo()).thenReturn(ITetrahedralChirality.Stereo.CLOCKWISE);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(tc));

        StereoEncoder encoder = new TetrahedralElementEncoderFactory().create(container, new int[0][0]); // graph not used

        assertThat(getGeometricParity(encoder).parity(), is(-1)); // clockwise (we didn't have to move the implied H)
    }

    @Test
    public void createImplicitH_front() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);

        when(container.atoms()).thenReturn(Arrays.asList(c1, o2, n3, c4));

        ITetrahedralChirality tc = mock(ITetrahedralChirality.class);
        when(tc.getChiralAtom()).thenReturn(c1);
        when(tc.getLigands()).thenReturn(new IAtom[]{c1, // <-- represents implicit H
                o2, n3, c4,});
        when(tc.getStereo()).thenReturn(ITetrahedralChirality.Stereo.CLOCKWISE);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(tc));

        StereoEncoder encoder = new TetrahedralElementEncoderFactory().create(container, new int[0][0]); // graph not used

        // anti-clockwise (inverted as we had to move the implicit H to the back
        // with an odd number of inversions)
        assertThat(getGeometricParity(encoder).parity(), is(1));
    }

    @Test
    public void createImplicitH_middle() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);

        IAtom c1 = mock(IAtom.class);
        IAtom o2 = mock(IAtom.class);
        IAtom n3 = mock(IAtom.class);
        IAtom c4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(o2);
        when(container.getAtom(2)).thenReturn(n3);
        when(container.getAtom(3)).thenReturn(c4);

        when(container.atoms()).thenReturn(Arrays.asList(c1, o2, n3, c4));

        ITetrahedralChirality tc = mock(ITetrahedralChirality.class);
        when(tc.getChiralAtom()).thenReturn(c1);
        when(tc.getLigands()).thenReturn(new IAtom[]{o2, c1, // <-- represents implicit H
                n3, c4,});
        when(tc.getStereo()).thenReturn(ITetrahedralChirality.Stereo.CLOCKWISE);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(tc));

        StereoEncoder encoder = new TetrahedralElementEncoderFactory().create(container, new int[0][0]); // graph not used

        // clockwise - we had to move the implied H but we moved it an even
        // number of times
        assertThat(getGeometricParity(encoder).parity(), is(-1));
    }

    private static GeometricParity getGeometricParity(StereoEncoder encoder) {
        if (encoder instanceof MultiStereoEncoder) {
            return getGeometricParity(extractEncoders(encoder).get(0));
        } else if (encoder instanceof GeometryEncoder) {
            Field field = null;
            try {
                field = encoder.getClass().getDeclaredField("geometric");
                field.setAccessible(true);
                return (GeometricParity) field.get(encoder);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return null;
    }

    private static List<StereoEncoder> extractEncoders(StereoEncoder encoder) {
        if (encoder instanceof MultiStereoEncoder) {
            Field field = null;
            try {
                field = encoder.getClass().getDeclaredField("encoders");
                field.setAccessible(true);
                return (List<StereoEncoder>) field.get(encoder);
            } catch (NoSuchFieldException e) {
                System.err.println(e.getMessage());
            } catch (IllegalAccessException e) {
                System.err.println(e.getMessage());
            }
        }
        return Collections.emptyList();
    }
}

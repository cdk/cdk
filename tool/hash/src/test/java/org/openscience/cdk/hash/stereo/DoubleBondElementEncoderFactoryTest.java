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
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * See. {@link org.openscience.cdk.hash.HashCodeScenariosTest} for test which show
 * example usage.
 *
 * @author John May
 * @cdk.module test-hash
 */
public class DoubleBondElementEncoderFactoryTest {

    @Test
    public void opposite() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(4);

        IAtom c1 = mock(IAtom.class);
        IAtom c2 = mock(IAtom.class);
        IAtom cl3 = mock(IAtom.class);
        IAtom cl4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(c2);
        when(container.getAtom(2)).thenReturn(cl3);
        when(container.getAtom(3)).thenReturn(cl4);

        when(container.atoms()).thenReturn(Arrays.asList(c1, c2, cl3, cl4));

        IBond stereoBond = mock(IBond.class);
        IBond left = mock(IBond.class);
        IBond right = mock(IBond.class);

        when(stereoBond.getBegin()).thenReturn(c1);
        when(stereoBond.getEnd()).thenReturn(c2);
        when(left.getOther(c1)).thenReturn(cl3);
        when(right.getOther(c2)).thenReturn(cl4);

        IDoubleBondStereochemistry dbs = mock(IDoubleBondStereochemistry.class);
        when(dbs.getStereoBond()).thenReturn(stereoBond);
        when(dbs.getBonds()).thenReturn(new IBond[]{left, right});
        when(dbs.getStereo()).thenReturn(IDoubleBondStereochemistry.Conformation.OPPOSITE);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(dbs));

        StereoEncoder encoder = new DoubleBondElementEncoderFactory().create(container, new int[][]{{1, 2}, {0, 3},
                {0}, {1}});

        assertThat(getGeometricParity(encoder).parity(), is(1));
    }

    @Test
    public void together() throws Exception {

        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getAtomCount()).thenReturn(4);

        IAtom c1 = mock(IAtom.class);
        IAtom c2 = mock(IAtom.class);
        IAtom cl3 = mock(IAtom.class);
        IAtom cl4 = mock(IAtom.class);

        when(container.getAtom(0)).thenReturn(c1);
        when(container.getAtom(1)).thenReturn(c2);
        when(container.getAtom(2)).thenReturn(cl3);
        when(container.getAtom(3)).thenReturn(cl4);

        when(container.atoms()).thenReturn(Arrays.asList(c1, c2, cl3, cl4));

        IBond stereoBond = mock(IBond.class);
        IBond left = mock(IBond.class);
        IBond right = mock(IBond.class);

        when(stereoBond.getBegin()).thenReturn(c1);
        when(stereoBond.getEnd()).thenReturn(c2);
        when(left.getOther(c1)).thenReturn(cl3);
        when(right.getOther(c2)).thenReturn(cl4);

        IDoubleBondStereochemistry dbs = mock(IDoubleBondStereochemistry.class);
        when(dbs.getStereoBond()).thenReturn(stereoBond);
        when(dbs.getBonds()).thenReturn(new IBond[]{left, right});
        when(dbs.getStereo()).thenReturn(IDoubleBondStereochemistry.Conformation.TOGETHER);
        when(container.stereoElements()).thenReturn(Collections.<IStereoElement> singleton(dbs));

        StereoEncoder encoder = new DoubleBondElementEncoderFactory().create(container, new int[][]{{1, 2}, {0, 3},
                {0}, {1}});

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

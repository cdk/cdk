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

package org.openscience.cdk.stereo;

import com.google.common.collect.Iterables;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class StereoElementFactoryTest {

    @Test
    public void e_but2ene() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, -2.19d, 1.64d));
        m.addAtom(atom("C", 1, -1.36d, 1.64d));
        m.addAtom(atom("C", 3, -2.60d, 0.92d));
        m.addAtom(atom("C", 3, -0.95d, 2.35d));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNotNull(element);
        assertThat(element.getStereo(), is(OPPOSITE));
    }

    @Test
    public void z_but2ene() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 3, -2.46d, 1.99d));
        m.addAtom(atom("C", 1, -1.74d, 0.68d));
        m.addAtom(atom("C", 1, -0.24d, 0.65d));
        m.addAtom(atom("C", 3, 0.54d, 1.94d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(1), null);

        assertNotNull(element);
        assertThat(element.getStereo(), is(TOGETHER));
    }

    @Test
    public void unspec_but2ene_byCoordinates() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, -1.37d, 1.64d));
        m.addAtom(atom("C", 1, -2.19d, 1.63d));
        m.addAtom(atom("C", 3, -2.59d, 0.90d));
        m.addAtom(atom("C", 3, -0.52d, 1.73d));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNull(element);
    }

    @Test
    public void unspec_but2ene_wavyBond() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, -1.27d, 1.48d));
        m.addAtom(atom("C", 1, -2.10d, 1.46d));
        m.addAtom(atom("C", 3, -2.50d, 0.74d));
        m.addAtom(atom("C", 3, -0.87d, 2.20d));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE, IBond.Stereo.UP_OR_DOWN);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNull(element);
    }

    @Test
    public void unspec_but2ene_crossBond() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, -1.27d, 1.48d));
        m.addAtom(atom("C", 1, -2.10d, 1.46d));
        m.addAtom(atom("C", 3, -2.50d, 0.74d));
        m.addAtom(atom("C", 3, -0.87d, 2.20d));
        m.addBond(0, 1, IBond.Order.DOUBLE, IBond.Stereo.E_OR_Z);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNull(element);
    }

    @Test
    public void r_butan2ol() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("O", 1, -0.46d, 1.98d));
        m.addAtom(atom("C", 1, -1.28d, 1.96d));
        m.addAtom(atom("C", 2, -1.71d, 2.67d));
        m.addAtom(atom("C", 3, -1.68d, 1.24d));
        m.addAtom(atom("C", 3, -2.53d, 2.66d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(1), null);
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    public void s_butan2ol() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("O", 1, -0.46d, 1.98d));
        m.addAtom(atom("C", 1, -1.28d, 1.96d));
        m.addAtom(atom("C", 2, -1.71d, 2.67d));
        m.addAtom(atom("C", 3, -1.68d, 1.24d));
        m.addAtom(atom("C", 3, -2.53d, 2.66d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(1), null);
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    @Test
    public void r_butan2ol_3d() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 1, 0.56d, 0.05d, 0.71d));
        m.addAtom(atom("C", 2, -0.53d, 0.51d, -0.30d));
        m.addAtom(atom("C", 3, 1.81d, -0.53d, 0.02d));
        m.addAtom(atom("C", 3, -1.80d, 1.06d, 0.37d));
        m.addAtom(atom("O", 1, 0.95d, 1.15d, 1.54d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), Stereocenters.of(m));
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    public void s_butan2ol_3d() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 1, -0.17d, -0.12d, -0.89d));
        m.addAtom(atom("C", 2, 1.12d, -0.91d, -0.51d));
        m.addAtom(atom("C", 3, -0.10d, 0.46d, -2.32d));
        m.addAtom(atom("C", 3, 1.07d, -1.54d, 0.91d));
        m.addAtom(atom("O", 1, -0.38d, 0.96d, 0.02d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), Stereocenters.of(m));
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    @Test
    public void r_butan2ol_3d_expH() {
        IAtomContainer m = new AtomContainer(6, 5, 0, 0);
        m.addAtom(atom("C", 0, -0.07d, -0.14d, 0.50d));
        m.addAtom(atom("C", 2, -0.05d, -1.20d, -0.65d));
        m.addAtom(atom("C", 3, 0.98d, -0.46d, 1.60d));
        m.addAtom(atom("C", 3, -1.11d, -0.94d, -1.75d));
        m.addAtom(atom("O", 1, 0.21d, 1.16d, -0.01d));
        m.addAtom(atom("H", 0, -1.06d, -0.13d, 0.96d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), Stereocenters.of(m));
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    public void s_butan2ol_3d_expH() {
        IAtomContainer m = new AtomContainer(6, 5, 0, 0);
        m.addAtom(atom("C", 0, -0.17d, -0.12d, -0.89d));
        m.addAtom(atom("C", 2, 1.12d, -0.91d, -0.51d));
        m.addAtom(atom("C", 3, -0.10d, 0.46d, -2.32d));
        m.addAtom(atom("C", 3, 1.07d, -1.54d, 0.91d));
        m.addAtom(atom("O", 1, -0.38d, 0.96d, 0.02d));
        m.addAtom(atom("H", 0, -1.03d, -0.79d, -0.83d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), Stereocenters.of(m));
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    @Test
    public void unspec_butan2ol() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("O", 1, -0.46d, 1.98d));
        m.addAtom(atom("C", 1, -1.28d, 1.96d));
        m.addAtom(atom("C", 2, -1.71d, 2.67d));
        m.addAtom(atom("C", 3, -1.68d, 1.24d));
        m.addAtom(atom("C", 3, -2.53d, 2.66d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP_OR_DOWN);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(1), null);
        assertNull(element);
    }

    /**
     * @cdk.inchi InChI=1S/C3H8OS/c1-3-5(2)4/h3H2,1-2H3/t5-/m1/s1
     */
    @Test
    public void r_methanesulfinylethane() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("S", 0, 0.01d, 1.50d));
        m.addAtom(atom("C", 3, 0.03d, 0.00d));
        m.addAtom(atom("C", 2, -1.30d, 2.23d));
        m.addAtom(atom("C", 3, -1.33d, 3.73d));
        m.addAtom(atom("O", 0, 1.29d, 2.28d));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.DOUBLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), null);
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    /**
     * @cdk.inchi InChI=1S/C3H8OS/c1-3-5(2)4/h3H2,1-2H3/t5-/m0/s1
     */
    @Test
    public void s_methanesulfinylethane() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("S", 0, 0.01d, 1.50d));
        m.addAtom(atom("C", 3, 0.03d, 0.00d));
        m.addAtom(atom("C", 2, -1.30d, 2.23d));
        m.addAtom(atom("C", 3, -1.33d, 3.73d));
        m.addAtom(atom("O", 0, 1.29d, 2.28d));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.DOUBLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(0), null);
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    @Test
    public void e_but2ene_3d() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, -0.19d, 0.09d, -0.27d));
        m.addAtom(atom("C", 1, 0.22d, -1.15d, 0.05d));
        m.addAtom(atom("C", 3, 0.21d, 0.75d, -1.49d));
        m.addAtom(atom("C", 3, -0.17d, -1.82d, 1.27d));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNotNull(element);
        assertThat(element.getStereo(), is(OPPOSITE));
    }

    @Test
    public void z_but2ene_3d() {
        IAtomContainer m = new AtomContainer(4, 3, 0, 0);
        m.addAtom(atom("C", 1, 0.05d, -1.28d, 0.13d));
        m.addAtom(atom("C", 1, -0.72d, -0.58d, -0.72d));
        m.addAtom(atom("C", 3, 1.11d, -0.74d, 0.95d));
        m.addAtom(atom("C", 3, -0.65d, 0.85d, -0.94d));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(m);
        IDoubleBondStereochemistry element = factory.createGeometric(m.getBond(0), null);

        assertNotNull(element);
        assertThat(element.getStereo(), is(TOGETHER));
    }

    @Test
    public void inverse_style_downbond() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("O", 1, -0.46d, 1.98d));
        m.addAtom(atom("C", 1, -1.28d, 1.96d));
        m.addAtom(atom("C", 2, -1.71d, 2.67d));
        m.addAtom(atom("C", 3, -1.68d, 1.24d));
        m.addAtom(atom("C", 3, -2.53d, 2.66d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.DOWN_INVERTED);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(1), Stereocenters.of(m));
        assertNotNull(element);
        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
    }

    // this example mocks a case where the down bond is inverse but is shared
    // between two stereo-centres - we can't create an element for atom 1 as
    // this bond is used to specify atom '2'
    @Test
    public void inverse_style_downbond_ambiguous() throws CDKException {
        IAtomContainer m = new AtomContainer(6, 4, 0, 0);
        m.addAtom(atom("O", 1, -0.46d, 1.98d));
        m.addAtom(atom("C", 1, -1.28d, 1.96d));
        m.addAtom(atom("C", 1, -1.71d, 2.67d));
        m.addAtom(atom("C", 3, -1.68d, 1.24d));
        m.addAtom(atom("C", 3, -2.53d, 2.66d));
        m.addAtom(atom("O", 1, -1.31d, 3.39d));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN_INVERTED);
        m.addBond(1, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        m.addBond(2, 5, IBond.Order.SINGLE);

        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = factory.createTetrahedral(m.getAtom(1), Stereocenters.of(m));
        assertNull(element);
    }

    /**
     * MetaCyc CPD-7272 D-dopachrome
     * http://metacyc.org/META/NEW-IMAGE?type=NIL&object=CPD-7272
     * @cdk.inchi InChI=1S/C9H7NO4/c11-7-2-4-1-6(9(13)14)10-5(4)3-8(7)12/h1,3,6,10H,2H2,(H,13,14)/p-1
     */
    @Test
    public void inverse_style_downbond_dopachrome() throws Exception {
        MDLV2000Reader mdl = null;
        try {
            mdl = new MDLV2000Reader(getClass().getResourceAsStream("/data/mdl/CPD-7272.mol"));
            IAtomContainer ac = mdl.read(new AtomContainer());

            // MDL reader currently adds stereo automatically
            IStereoElement[] ses = Iterables.toArray(ac.stereoElements(), IStereoElement.class);

            assertThat(ses.length, is(1));
            assertNotNull(ses[0]);
        } finally {
            if (mdl != null) mdl.close();
        }
    }

    @Test
    public void createExtendedTetrahedralFrom2DCoordinates_cw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 0, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("H", 0, 0.92d, 0.74d));
        m.addAtom(atom("H", 0, -1.53d, 2.21d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(3, 5, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using2DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4), m.getAtom(5)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom2DCoordinates_ccw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 0, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("H", 0, 0.92d, 0.74d));
        m.addAtom(atom("H", 0, -1.53d, 2.21d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(3, 5, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using2DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4), m.getAtom(5)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom2DCoordinatesImplicitHydrogens_cw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 1, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 1, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using2DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(4), m.getAtom(3)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom2DCoordinatesImplicitHydrogens_ccw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 1, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 1, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using2DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(1), m.getAtom(4), m.getAtom(3)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom2DCoordinatesNoNonplanarBonds() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 0, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("H", 0, 0.92d, 0.74d));
        m.addAtom(atom("H", 0, -1.53d, 2.21d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.NONE);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE, IBond.Stereo.NONE);
        m.addBond(3, 5, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using2DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertNull(et);
    }

    @Test
    public void createExtendedTetrahedralFrom3DCoordinates_cw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, 0.1925, -2.7911, 1.8739));
        m.addAtom(atom("C", 0, -0.4383, -2.0366, 0.8166));
        m.addAtom(atom("C", 0, 0.2349, -1.2464, 0.0943));
        m.addAtom(atom("C", 0, 0.9377, -0.4327, -0.5715));
        m.addAtom(atom("C", 3, 1.0851, 0.9388, -0.1444));
        m.addAtom(atom("H", 0, 1.3810, -0.7495, -1.4012));
        m.addAtom(atom("H", 0, -1.4096, -2.1383, 0.6392));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using3DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4), m.getAtom(5)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom3DCoordinates_ccw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, 1.3810, -0.7495, -1.4012));
        m.addAtom(atom("C", 0, -0.4383, -2.0366, 0.8166));
        m.addAtom(atom("C", 0, 0.2349, -1.2464, 0.0943));
        m.addAtom(atom("C", 0, 0.9377, -0.4327, -0.5715));
        m.addAtom(atom("C", 3, 1.0851, 0.9388, -0.1444));
        m.addAtom(atom("H", 0, 0.1925, -2.7911, 1.8739));
        m.addAtom(atom("H", 0, -1.4096, -2.1383, 0.6392));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);

        ExtendedTetrahedral et = StereoElementFactory.using3DCoordinates(m).createExtendedTetrahedral(2,
                Stereocenters.of(m));
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4), m.getAtom(5)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedral() throws CDKException {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 1, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 1, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.setStereoElements(StereoElementFactory.using2DCoordinates(m).createAll());
        assertTrue(m.stereoElements().iterator().hasNext());
        assertThat(m.stereoElements().iterator().next(), is(instanceOf(ExtendedTetrahedral.class)));
    }

    @Test
    public void doNotCreateNonStereogenicExtendedTetrahedral() throws CDKException {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.56d, 0.78d));
        m.addAtom(atom("C", 1, -1.13d, 1.49d));
        m.addAtom(atom("C", 0, -0.31d, 1.47d));
        m.addAtom(atom("C", 0, 0.52d, 1.46d));
        m.addAtom(atom("C", 3, 0.94d, 2.17d));
        m.addAtom(atom("C", 3, 0.92d, 0.74d));
        m.addBond(1, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(2, 3, IBond.Order.DOUBLE, IBond.Stereo.NONE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        m.setStereoElements(StereoElementFactory.using2DCoordinates(m).createAll());
        assertFalse(m.stereoElements().iterator().hasNext());
    }

    /**
     * The embedding of 3D depictions may cause bonds of abnormal length
     * (e.g. CHEBI:7621). The parity computation should consider this, here
     * we check we get the correct (anti-clockwise) configuration.
     */
    @Test
    public void differentBondLengthsDoNotAffectWinding() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("O", 1, 14.50d, -8.72d));
        m.addAtom(atom("N", 2, 14.50d, -11.15d));
        m.addAtom(atom("C", 0, 15.28d, -7.81d));
        m.addAtom(atom("C", 3, 12.91d, -7.81d));
        m.addAtom(atom("H", 0, 16.00d, -7.39d));
        m.addBond(2, 0, IBond.Order.SINGLE);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(3, 2, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE, IBond.Stereo.DOWN);

        StereoElementFactory sef = StereoElementFactory.using2DCoordinates(m);
        ITetrahedralChirality element = sef.createTetrahedral(2, Stereocenters.of(m));

        assertThat(element.getChiralAtom(), is(m.getAtom(2)));

        IAtom[] ligands = element.getLigands();
        assertThat(ligands[0], is(m.getAtom(0)));
        assertThat(ligands[1], is(m.getAtom(1)));
        assertThat(ligands[2], is(m.getAtom(3)));
        assertThat(ligands[3], is(m.getAtom(4)));

        assertThat(element.getStereo(), is(ITetrahedralChirality.Stereo.ANTI_CLOCKWISE));
    }

    @Test
    public void always2DTetrahedralElements() {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 1, 0.34d, 2.28d));
        m.addAtom(atom("O", 1, 1.17d, 2.28d));
        m.addAtom(atom("C", 1, -0.07d, 2.99d));
        m.addAtom(atom("C", 1, -0.07d, 1.56d));
        m.addAtom(atom("O", 1, 0.34d, 3.70d));
        m.addAtom(atom("O", 1, 0.34d, 0.85d));
        m.addAtom(atom("C", 3, -0.90d, 2.99d));
        m.addAtom(atom("C", 3, -0.90d, 1.56d));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(3, 5, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(2, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);

        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m).createAll();
        assertThat(elements.size(), is(3));
    }

    @Test
    public void onlyCreateStereoForConsitionalDifferencesIn3D() {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 1, -1.00d, -0.25d, 1.22d));
        m.addAtom(atom("O", 1, -1.82d, 0.20d, 2.30d));
        m.addAtom(atom("C", 1, -0.04d, -1.38d, 1.71d));
        m.addAtom(atom("C", 1, -0.24d, 0.95d, 0.57d));
        m.addAtom(atom("O", 1, 0.82d, -0.90d, 2.75d));
        m.addAtom(atom("O", 1, 0.63d, 1.58d, 1.51d));
        m.addAtom(atom("C", 3, -0.81d, -2.61d, 2.25d));
        m.addAtom(atom("C", 3, -1.19d, 2.03d, -0.01d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);
        m.addBond(2, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);

        List<IStereoElement> elements = StereoElementFactory.using3DCoordinates(m).createAll();
        // XXX: really 3 but we can't tell the middle centre is one ATM, see
        //      'dontCreateStereoForNonStereogenicIn3D'
        assertThat(elements.size(), is(2));
    }

    @Test
    public void dontCreateStereoForNonStereogenicIn3D() {
        IAtomContainer m = new AtomContainer(5, 4, 0, 0);
        m.addAtom(atom("C", 0, 0.00d, 0.00d, 0.00d));
        m.addAtom(atom("H", 0, -0.36d, -0.51d, 0.89d));
        m.addAtom(atom("H", 0, 1.09d, 0.00d, 0.00d));
        m.addAtom(atom("H", 0, -0.36d, 1.03d, 0.00d));
        m.addAtom(atom("H", 0, -0.36d, -0.51d, -0.89d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);

        List<IStereoElement> elements = StereoElementFactory.using3DCoordinates(m).createAll();

        // methane carbon is of course non-stereogenic
        assertThat(elements.size(), is(0));
    }

    /**
     * glyceraldehyde
     * @cdk.inchi InChI=1/C3H6O3/c4-1-3(6)2-5/h1,3,5-6H,2H2/t3-/s2
     */
    @Test public void onlyInterpretFischerProjectionsWhenAsked() throws Exception {
        IAtomContainer m = new AtomContainer(8, 7, 0, 0);
        m.addAtom(atom("C", 0, 0.80d, 1.24d));
        m.addAtom(atom("C", 0, 0.80d, 0.42d));
        m.addAtom(atom("O", 1, 0.09d, 1.66d));
        m.addAtom(atom("O", 0, 1.52d, 1.66d));
        m.addAtom(atom("O", 1, 1.63d, 0.42d));
        m.addAtom(atom("C", 2, 0.80d, -0.41d));
        m.addAtom(atom("H", 0, -0.02d, 0.42d));
        m.addAtom(atom("O", 1, 1.52d, -0.82d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(0, 3, IBond.Order.DOUBLE, IBond.Stereo.E_Z_BY_COORDINATES);
        m.addBond(1, 4, IBond.Order.SINGLE);
        m.addBond(1, 5, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(5, 7, IBond.Order.SINGLE);

        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Haworth)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Chair)
                                       .createAll()
                                       .isEmpty());
        assertFalse(StereoElementFactory.using2DCoordinates(m)
                                        .interpretProjections(Projection.Fischer)
                                        .createAll()
                                        .isEmpty());
    }

    /**
     * beta-D-glucose
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6-/s2
     */
    @Test public void onlyInterpretHaworthProjectionsWhenAsked() throws Exception {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 1, 4.16d, 1.66d));
        m.addAtom(atom("C", 1, 3.75d, 0.94d));
        m.addAtom(atom("C", 1, 4.16d, 0.23d));
        m.addAtom(atom("C", 1, 5.05d, 0.23d));
        m.addAtom(atom("C", 1, 5.46d, 0.94d));
        m.addAtom(atom("O", 0, 5.05d, 1.66d));
        m.addAtom(atom("O", 1, 5.46d, 1.77d));
        m.addAtom(atom("C", 2, 4.16d, 2.48d));
        m.addAtom(atom("O", 1, 3.45d, 2.89d));
        m.addAtom(atom("O", 1, 3.75d, 0.12d));
        m.addAtom(atom("O", 1, 4.16d, 1.05d));
        m.addAtom(atom("O", 1, 5.05d, -0.60d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(0, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.SINGLE);
        m.addBond(1, 9, IBond.Order.SINGLE);
        m.addBond(2, 10, IBond.Order.SINGLE);
        m.addBond(3, 11, IBond.Order.SINGLE);

        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Fischer)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Chair)
                                       .createAll()
                                       .isEmpty());
        assertFalse(StereoElementFactory.using2DCoordinates(m)
                                        .interpretProjections(Projection.Haworth)
                                        .createAll()
                                        .isEmpty());
    }
    
    /**
     * beta-D-glucose
     * @cdk.inchi InChI=1/C6H12O6/c7-1-2-3(8)4(9)5(10)6(11)12-2/h2-11H,1H2/t2-,3-,4+,5-,6-/s2
     */
    @Test public void onlyInterpretChairProjectionsWhenAsked() throws Exception {
        IAtomContainer m = new AtomContainer(12, 12, 0, 0);
        m.addAtom(atom("C", 1, -0.77d, 10.34d));
        m.addAtom(atom("C", 1, 0.03d, 10.13d));
        m.addAtom(atom("O", 0, 0.83d, 10.34d));
        m.addAtom(atom("C", 1, 1.24d, 9.63d));
        m.addAtom(atom("C", 1, 0.44d, 9.84d));
        m.addAtom(atom("C", 1, -0.35d, 9.63d));
        m.addAtom(atom("O", 1, 0.86d, 9.13d));
        m.addAtom(atom("O", 1, 2.04d, 9.84d));
        m.addAtom(atom("C", 2, -0.68d, 10.54d));
        m.addAtom(atom("O", 1, -0.68d, 11.37d));
        m.addAtom(atom("O", 1, -1.48d, 9.93d));
        m.addAtom(atom("O", 1, -1.15d, 9.84d));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 0, IBond.Order.SINGLE);
        m.addBond(4, 6, IBond.Order.SINGLE);
        m.addBond(3, 7, IBond.Order.SINGLE);
        m.addBond(1, 8, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(0, 10, IBond.Order.SINGLE);
        m.addBond(5, 11, IBond.Order.SINGLE);

        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Fischer)
                                       .createAll()
                                       .isEmpty());
        assertTrue(StereoElementFactory.using2DCoordinates(m)
                                       .interpretProjections(Projection.Haworth)
                                       .createAll()
                                       .isEmpty());
        assertFalse(StereoElementFactory.using2DCoordinates(m)
                                        .interpretProjections(Projection.Chair)
                                        .createAll()
                                        .isEmpty());
    }

    static IAtom atom(String symbol, int h, double x, double y) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(h);
        a.setPoint2d(new Point2d(x, y));
        return a;
    }

    static IAtom atom(String symbol, int h, double x, double y, double z) {
        IAtom a = new Atom(symbol);
        a.setImplicitHydrogenCount(h);
        a.setPoint3d(new Point3d(x, y, z));
        return a;
    }
}

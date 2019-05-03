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
import org.openscience.cdk.CDKConstants;
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
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import java.util.ArrayList;
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

    // don't create double bond configs in benzene
    @Test
    public void benzene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, 1.30, -0.75));
        mol.addAtom(atom("C", 1, -0.00, -1.50));
        mol.addAtom(atom("C", 1, -1.30, -0.75));
        mol.addAtom(atom("C", 1, -1.30, 0.75));
        mol.addAtom(atom("C", 1, 0.00, 1.50));
        mol.addAtom(atom("C", 1, 1.30, 0.75));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.DOUBLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.DOUBLE);
        mol.addBond(0, 5, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        assertThat(factory.createAll().size(), is(0));
    }

    // >=8 is okay for db stereo (ala inchi)
    @Test
    public void cyclooctatetraene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, -10.46, 6.36));
        mol.addAtom(atom("C", 1, -11.34, 5.15));
        mol.addAtom(atom("C", 1, -10.46, 3.93));
        mol.addAtom(atom("C", 1, -9.03, 4.40));
        mol.addAtom(atom("C", 1, -7.60, 3.93));
        mol.addAtom(atom("C", 1, -6.72, 5.15));
        mol.addAtom(atom("C", 1, -7.60, 6.36));
        mol.addAtom(atom("C", 1, -9.03, 5.90));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.DOUBLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.DOUBLE);
        mol.addBond(5, 6, IBond.Order.SINGLE);
        mol.addBond(6, 7, IBond.Order.DOUBLE);
        mol.addBond(0, 7, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        assertThat(factory.createAll().size(), is(4));
    }

    // not okay... but technically the trans form exists
    @Test
    public void doubleBondInSevenMemberedRing() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, -10.46, 6.36));
        mol.addAtom(atom("C", 1, -11.34, 5.15));
        mol.addAtom(atom("C", 1, -10.46, 3.93));
        mol.addAtom(atom("C", 1, -9.03, 4.40));
        mol.addAtom(atom("C", 1, -7.60, 3.93));
        mol.addAtom(atom("C", 1, -6.72, 5.15));
        mol.addAtom(atom("C", 1, -7.60, 6.36));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 6, IBond.Order.SINGLE);
        mol.addBond(6, 0, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        assertThat(factory.createAll().size(), is(0));
    }

    @Test
    public void hydrogenIsotope() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 3, 0.00, 0.00));
        mol.addAtom(atom("C", 1, 1.30, -0.75));
        mol.addAtom(atom("C", 1, 2.60, -0.00));
        mol.addAtom(atom("H", 0, 3.90, -0.75));
        mol.getAtom(3).setMassNumber(2);
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.DOUBLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        assertThat(factory.createAll().size(), is(1));
    }

    @Test
    public void bridgeHeadNitrogen() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 2, 1.23, 0.75));
        mol.addAtom(atom("C", 2, 1.23, -0.75));
        mol.addAtom(atom("N", 0, -0.07, -1.50));
        mol.addAtom(atom("C", 2, -1.36, -0.75));
        mol.addAtom(atom("C", 2, -1.36, 0.75));
        mol.addAtom(atom("N", 0, -0.07, 1.50));
        mol.addAtom(atom("C", 2, 0.39, -0.00));
        mol.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.NONE);
        mol.addBond(2, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        mol.addBond(2, 3, IBond.Order.SINGLE, IBond.Stereo.NONE);
        mol.addBond(3, 4, IBond.Order.SINGLE, IBond.Stereo.NONE);
        mol.addBond(4, 5, IBond.Order.SINGLE, IBond.Stereo.NONE);
        mol.addBond(5, 0, IBond.Order.SINGLE, IBond.Stereo.UP);
        mol.addBond(5, 6, IBond.Order.SINGLE, IBond.Stereo.NONE);
        mol.addBond(2, 6, IBond.Order.SINGLE, IBond.Stereo.NONE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        assertThat(factory.createAll().size(), is(2));
    }

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

    /**
     * (E)-hexa-2,3,4-triene
     * @cdk.smiles C/C=C=C=C/C
     */
    @Test
    public void e_hexa234triene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, 2.48d, 0.00d));
        mol.addAtom(atom("C", 0, 1.65d, 0.00d));
        mol.addAtom(atom("C", 0, 0.83d, 0.00d));
        mol.addAtom(atom("C", 1, 0.00d, 0.00d));
        mol.addAtom(atom("C", 3, -0.41d, -0.71d));
        mol.addAtom(atom("C", 3, 2.89d, 0.71d));
        mol.addBond(0,1,IBond.Order.DOUBLE);
        mol.addBond(1,2,IBond.Order.DOUBLE);
        mol.addBond(2,3,IBond.Order.DOUBLE);
        mol.addBond(3,4,IBond.Order.SINGLE);
        mol.addBond(0,5,IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        List<IBond> dbs = new ArrayList<>();
        dbs.add(mol.getBond(0));
        dbs.add(mol.getBond(1));
        dbs.add(mol.getBond(2));
        ExtendedCisTrans element = factory.createExtendedCisTrans(dbs, Stereocenters.of(mol));
        assertNotNull(element);
        assertThat(element.getConfigOrder(), is(IStereoElement.OPPOSITE));
    }

    /**
     * (Z)-hexa-2,3,4-triene
     * @cdk.smiles C/C=C=C=C\C
     */
    @Test
    public void z_hexa234triene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, 2.48d, 0.00d));
        mol.addAtom(atom("C", 0, 1.65d, 0.00d));
        mol.addAtom(atom("C", 0, 0.83d, 0.00d));
        mol.addAtom(atom("C", 1, 0.00d, 0.00d));
        mol.addAtom(atom("C", 3, -0.41d, -0.71d));
        mol.addAtom(atom("C", 3, 2.92d, -0.69d));
        mol.addBond(0,1,IBond.Order.DOUBLE);
        mol.addBond(1,2,IBond.Order.DOUBLE);
        mol.addBond(2,3,IBond.Order.DOUBLE);
        mol.addBond(3,4,IBond.Order.SINGLE);
        mol.addBond(0,5,IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using2DCoordinates(mol);
        List<IBond> dbs = new ArrayList<>();
        dbs.add(mol.getBond(0));
        dbs.add(mol.getBond(1));
        dbs.add(mol.getBond(2));
        ExtendedCisTrans element = factory.createExtendedCisTrans(dbs, Stereocenters.of(mol));
        assertNotNull(element);
        assertThat(element.getConfigOrder(), is(IStereoElement.TOGETHER));
    }

    /**
     * (E)-hexa-2,3,4-triene
     * @cdk.smiles C/C=C=C=C/C
     */
    @Test
    public void e_hexa234triene_3D() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, 0.29d, 0.01d, 0.02d));
        mol.addAtom(atom("C", 0, -0.56d, -0.90d, 0.25d));
        mol.addAtom(atom("C", 0, -1.37d, -1.75d, 0.46d));
        mol.addAtom(atom("C", 1, -2.24d, -2.65d, 0.67d));
        mol.addAtom(atom("C", 3, -3.66d, -2.36d, 0.68d));
        mol.addAtom(atom("C", 3, 1.69d, -0.32d, -0.11d));
        mol.addBond(0,1,IBond.Order.DOUBLE);
        mol.addBond(1,2,IBond.Order.DOUBLE);
        mol.addBond(2,3,IBond.Order.DOUBLE);
        mol.addBond(3,4,IBond.Order.SINGLE);
        mol.addBond(0,5,IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(mol);
        List<IBond> dbs = new ArrayList<>();
        dbs.add(mol.getBond(0));
        dbs.add(mol.getBond(1));
        dbs.add(mol.getBond(2));
        ExtendedCisTrans element = factory.createExtendedCisTrans(dbs, Stereocenters.of(mol));
        assertNotNull(element);
        assertThat(element.getConfigOrder(), is(IStereoElement.OPPOSITE));
    }

    /**
     * (Z)-hexa-2,3,4-triene
     * @cdk.smiles C/C=C=C=C\C
     */
    @Test
    public void z_hexa234triene_3D() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(atom("C", 1, -0.09d, -0.45d, -1.07d));
        mol.addAtom(atom("C", 0, -0.67d, -1.04d, -0.11d));
        mol.addAtom(atom("C", 0, -1.23d, -1.59d, 0.79d));
        mol.addAtom(atom("C", 1, -1.84d, -2.17d, 1.74d));
        mol.addAtom(atom("C", 3, -3.13d, -1.73d, 2.21d));
        mol.addAtom(atom("C", 3, -0.70d, 0.69d, -1.73d));
        mol.addBond(0,1,IBond.Order.DOUBLE);
        mol.addBond(1,2,IBond.Order.DOUBLE);
        mol.addBond(2,3,IBond.Order.DOUBLE);
        mol.addBond(3,4,IBond.Order.SINGLE);
        mol.addBond(0,5,IBond.Order.SINGLE);
        StereoElementFactory factory = StereoElementFactory.using3DCoordinates(mol);
        List<IBond> dbs = new ArrayList<>();
        dbs.add(mol.getBond(0));
        dbs.add(mol.getBond(1));
        dbs.add(mol.getBond(2));
        ExtendedCisTrans element = factory.createExtendedCisTrans(dbs, Stereocenters.of(mol));
        assertNotNull(element);
        assertThat(element.getConfigOrder(), is(IStereoElement.TOGETHER));
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

        List<IStereoElement> stereos = StereoElementFactory.using3DCoordinates(m).createAll();
        assertThat(stereos.size(), is(1));
        assertThat(stereos.get(0), instanceOf(ExtendedTetrahedral.class));
        ExtendedTetrahedral et = (ExtendedTetrahedral) stereos.get(0);
        assertThat(et.winding(), is(ITetrahedralChirality.Stereo.CLOCKWISE));
        assertThat(et.peripherals(), is(new IAtom[]{m.getAtom(0), m.getAtom(6), m.getAtom(4), m.getAtom(5)}));
        assertThat(et.focus(), is(m.getAtom(2)));
    }

    @Test
    public void createExtendedTetrahedralFrom3DCoordinates_ccw() throws Exception {
        IAtomContainer m = new AtomContainer(7, 6, 0, 0);
        m.addAtom(atom("C", 3, -1.4096, -2.1383, 0.6392));
        m.addAtom(atom("C", 0, -0.4383, -2.0366, 0.8166));
        m.addAtom(atom("C", 0, 0.2349, -1.2464, 0.0943));
        m.addAtom(atom("C", 0, 0.9377, -0.4327, -0.5715));
        m.addAtom(atom("C", 3, 1.0851, 0.9388, -0.1444));
        m.addAtom(atom("H", 0, 1.3810, -0.7495, -1.4012));
        m.addAtom(atom("H", 0, 0.1925, -2.7911, 1.8739));
        m.addBond(1, 0, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        m.addBond(1, 6, IBond.Order.SINGLE);
        m.addBond(3, 5, IBond.Order.SINGLE);

        List<IStereoElement> stereos = StereoElementFactory.using3DCoordinates(m).createAll();
        assertThat(stereos.size(), is(1));
        assertThat(stereos.get(0), instanceOf(ExtendedTetrahedral.class));
        ExtendedTetrahedral et = (ExtendedTetrahedral) stereos.get(0);
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
        m.setStereoElements(StereoElementFactory.using2DCoordinates(m).checkSymmetry(true).createAll());
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


    /**
     * Watch out for cumulated bonds with a kink in 3d. The generation program
     * has not understood the chemistry completely.
     *
     * @cdk.smiles CC=[C@]=CC
     */
    @Test
    public void badlyOptimizedAllene() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 1, -4.02, 3.96, -1.09));
        m.addAtom(atom("C", 0, -4.96, 3.82, 0.13));
        m.addAtom(atom("C", 3, -3.70, 5.35, -1.67));
        m.addAtom(atom("C", 1, -5.27, 2.44, 0.71));
        m.addAtom(atom("C", 3, -6.21, 2.30, 1.92));
        m.addBond(0, 1, IBond.Order.DOUBLE);
        m.addBond(0, 2, IBond.Order.SINGLE);
        m.addBond(1, 3, IBond.Order.DOUBLE);
        m.addBond(3, 4, IBond.Order.SINGLE);
        List<IStereoElement> elements = StereoElementFactory.using3DCoordinates(m).createAll();
        assertThat(elements.size(), is(0));
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

    /**
     * Pass through non-stereo configurations if check symmetry is disabled
     */
    @Test
    public void keepNonStereoConfiguration() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, 0.07, 1.19));
        m.addAtom(atom("H", 0, 0.56, 2.02));
        m.addAtom(atom("C", 3, -0.29, 2.04));
        m.addAtom(atom("C", 3, -0.66, 0.82));
        m.addAtom(atom("C", 2, 0.76, 0.74));
        m.addAtom(atom("C", 3, 1.50, 1.12));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m).createAll();
        assertThat(elements.size(), is(1));
        m.setStereoElements(elements);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo);
        assertThat(smigen.create(m), is("[C@]([H])(C)(C)CC"));
    }

    @Test
    public void keepNonStereoConfigurationPhosphorusTautomer() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("P", 0, 0.07, 1.19));
        m.addAtom(atom("O", 0, 0.56, 2.02));
        m.addAtom(atom("O", 1, -0.29, 2.04));
        m.addAtom(atom("C", 3, -0.66, 0.82));
        m.addAtom(atom("C", 2, 0.76, 0.74));
        m.addAtom(atom("C", 3, 1.50, 1.12));
        m.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 1, IBond.Order.DOUBLE);
        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m).createAll();
        assertThat(elements.size(), is(1));
        m.setStereoElements(elements);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo);
        assertThat(smigen.create(m), is("[P@@](O)(C)(CC)=O"));
    }

    @Test
    public void doNotkeepNonStereoConfigurationPhosphorusTautomer() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("P", 0, 0.07, 1.19));
        m.addAtom(atom("O", 0, 0.56, 2.02));
        m.addAtom(atom("O", 1, -0.29, 2.04));
        m.addAtom(atom("C", 3, -0.66, 0.82));
        m.addAtom(atom("C", 2, 0.76, 0.74));
        m.addAtom(atom("C", 3, 1.50, 1.12));
        m.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 1, IBond.Order.DOUBLE);
        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m)
                                                            .checkSymmetry(true)
                                                            .createAll();
        assertThat(elements.size(), is(0));
        m.setStereoElements(elements);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo);
        assertThat(smigen.create(m), is("P(O)(C)(CC)=O"));
    }

    /**
     * Do not pass through non-stereo configurations if check symmetry is enabled
     */
    @Test
    public void doNotKeepNonStereoConfiguration() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, 0.07, 1.19));
        m.addAtom(atom("H", 0, 0.56, 2.02));
        m.addAtom(atom("C", 3, -0.29, 2.04));
        m.addAtom(atom("C", 3, -0.66, 0.82));
        m.addAtom(atom("C", 2, 0.76, 0.74));
        m.addAtom(atom("C", 3, 1.50, 1.12));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m)
                                                            .checkSymmetry(true)
                                                            .createAll();
        assertThat(elements.size(), is(0));
        m.setStereoElements(elements);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo);
        assertThat(smigen.create(m), is("C([H])(C)(C)CC"));
    }

    /**
     * Pass through non-stereo configurations if check symmetry is disabled
     */
    @Test
    public void keepNonStereoConfigurationH2() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, 0.07, 1.19));
        m.addAtom(atom("H", 0, 0.56, 2.02));
        m.addAtom(atom("H", 0, -0.29, 2.04));
        m.addAtom(atom("C", 3, -0.66, 0.82));
        m.addAtom(atom("C", 2, 0.76, 0.74));
        m.addAtom(atom("C", 3, 1.50, 1.12));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(0, 2, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        m.addBond(0, 3, IBond.Order.SINGLE);
        m.addBond(0, 4, IBond.Order.SINGLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        List<IStereoElement> elements = StereoElementFactory.using2DCoordinates(m).createAll();
        assertThat(elements.size(), is(1));
        m.setStereoElements(elements);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo);
        assertThat(smigen.create(m), is("[C@]([H])([H])(C)CC"));
        AtomContainerManipulator.suppressHydrogens(m);
        assertThat(smigen.create(m), is("[C@H2](C)CC"));
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);
        assertThat(smigen.create(m), is("[C@](C([H])([H])[H])(C(C([H])([H])[H])([H])[H])([H])[H]"));
    }

    /**
     * BiNOL - SMILES/InChI can't represent the atropoisomerism but the single
     *         bond rotation is restricted.
     * @cdk.smiles OC1=CC=C2C=CC=CC2=C1C1=C(O)C=CC2=C1C=CC=C2
     */
    @Test public void binol2D() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, -0.83, -0.01));
        m.addAtom(atom("C", 0, -1.55, -0.42));
        m.addAtom(atom("C", 1, -1.55, -1.25));
        m.addAtom(atom("C", 1, -0.83, -1.66));
        m.addAtom(atom("C", 0, -0.12, -1.25));
        m.addAtom(atom("C", 0, -0.12, -0.42));
        m.addAtom(atom("C", 0, -0.83, 0.82));
        m.addAtom(atom("C", 1, -0.83, 2.47));
        m.addAtom(atom("C", 1, -1.55, 2.05));
        m.addAtom(atom("C", 0, -1.55, 1.23));
        m.addAtom(atom("C", 0, -0.12, 1.23));
        m.addAtom(atom("C", 0, -0.12, 2.05));
        m.addAtom(atom("O", 1, -2.26, 0.82));
        m.addAtom(atom("O", 1, -2.26, -0.01));
        m.addAtom(atom("C", 1, 0.60, 2.47));
        m.addAtom(atom("C", 1, 0.60, 0.82));
        m.addAtom(atom("C", 1, 1.31, 1.23));
        m.addAtom(atom("C", 1, 1.31, 2.05));
        m.addAtom(atom("C", 1, 0.60, -0.01));
        m.addAtom(atom("C", 1, 0.60, -1.66));
        m.addAtom(atom("C", 1, 1.31, -1.25));
        m.addAtom(atom("C", 1, 1.31, -0.42));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.DOUBLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(10, 11, IBond.Order.DOUBLE);
        m.addBond(7, 11, IBond.Order.SINGLE);
        m.addBond(9, 6, IBond.Order.DOUBLE);
        m.addBond(6, 10, IBond.Order.SINGLE);
        m.addBond(9, 12, IBond.Order.SINGLE);
        m.addBond(1, 13, IBond.Order.SINGLE);
        m.addBond(15, 16, IBond.Order.DOUBLE);
        m.addBond(16, 17, IBond.Order.SINGLE);
        m.addBond(14, 17, IBond.Order.DOUBLE);
        m.addBond(10, 15, IBond.Order.SINGLE);
        m.addBond(14, 11, IBond.Order.SINGLE);
        m.addBond(19, 20, IBond.Order.DOUBLE);
        m.addBond(20, 21, IBond.Order.SINGLE);
        m.addBond(18, 21, IBond.Order.DOUBLE);
        m.addBond(4, 19, IBond.Order.SINGLE);
        m.addBond(18, 5, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(0));
        m.getBond(12).setStereo(IBond.Stereo.UP);
        List<IStereoElement> stereoUp =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereoUp.size(), is(1));
        m.getBond(12).setStereo(IBond.Stereo.DOWN);
        List<IStereoElement> stereoDown =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereoDown.size(), is(1));
        IStereoElement s1 = stereoUp.get(0);
        IStereoElement s2 = stereoDown.get(0);
        assertThat(s1.getFocus(), is(s2.getFocus()));
        assertThat(s1.getCarriers(), is(s2.getCarriers()));
        assertThat(s1.getConfigOrder(), is(IStereoElement.RIGHT));
        assertThat(s2.getConfigOrder(), is(IStereoElement.LEFT));

        // now test placement of wedges else where
        m.getBond(12).setStereo(IBond.Stereo.NONE);
        m.getBond(m.getAtom(9), m.getAtom(12)).setStereo(IBond.Stereo.UP);
        List<IStereoElement> stereoUpOther =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereoUpOther.size(), is(1));
        IStereoElement s3 = stereoUpOther.get(0);
        assertThat(s3.getFocus(), is(s2.getFocus()));
        assertThat(s3.getCarriers(), is(s2.getCarriers()));
        assertThat(s3.getConfigOrder(), is(s2.getConfigOrder()));

        m.getBond(m.getAtom(9), m.getAtom(12)).setStereo(IBond.Stereo.DOWN);
        List<IStereoElement> stereoDownOther =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereoDownOther.size(), is(1));
        IStereoElement s4 = stereoDownOther.get(0);
        assertThat(s4.getFocus(), is(s1.getFocus()));
        assertThat(s4.getCarriers(), is(s1.getCarriers()));
        assertThat(s4.getConfigOrder(), is(s1.getConfigOrder()));
    }

    /**
     * @cdk.smiles CC1=C(C=CC=C1)C1=C(C)C=CC=C1O
     */
    @Test
    public void atropisomer1() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, -7.53, -2.12));
        m.addAtom(atom("C", 1, -8.24, -2.53));
        m.addAtom(atom("C", 1, -8.24, -3.36));
        m.addAtom(atom("C", 1, -7.53, -3.77));
        m.addAtom(atom("C", 1, -6.82, -3.36));
        m.addAtom(atom("C", 0, -6.82, -2.53));
        m.addAtom(atom("C", 0, -7.53, -1.30));
        m.addAtom(atom("C", 0, -6.82, -0.88));
        m.addAtom(atom("C", 1, -6.82, -0.06));
        m.addAtom(atom("C", 1, -7.53, 0.35));
        m.addAtom(atom("C", 1, -8.24, -0.06));
        m.addAtom(atom("C", 0, -8.24, -0.88));
        m.addAtom(atom("C", 3, -8.96, -1.30));
        m.addAtom(atom("O", 1, -6.10, -1.30));
        m.addAtom(atom("C", 3, -6.10, -2.12));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE);
        m.addBond(7, 8, IBond.Order.DOUBLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.DOUBLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(6, 11, IBond.Order.DOUBLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.SINGLE);
        m.addBond(7, 13, IBond.Order.SINGLE);
        m.addBond(5, 14, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(1));
    }

    /**
     * @cdk.smiles CC1=C(C(O)=CC=C1)C1=CC=CC=C1
     */
    @Test
    public void nonAtropisomer2() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, -7.53, -2.12));
        m.addAtom(atom("C", 1, -8.24, -2.53));
        m.addAtom(atom("C", 1, -8.24, -3.36));
        m.addAtom(atom("C", 1, -7.53, -3.77));
        m.addAtom(atom("C", 1, -6.82, -3.36));
        m.addAtom(atom("C", 1, -6.82, -2.53));
        m.addAtom(atom("C", 0, -7.53, -1.30));
        m.addAtom(atom("C", 0, -6.82, -0.88));
        m.addAtom(atom("C", 1, -6.82, -0.06));
        m.addAtom(atom("C", 1, -7.53, 0.35));
        m.addAtom(atom("C", 1, -8.24, -0.06));
        m.addAtom(atom("C", 0, -8.24, -0.88));
        m.addAtom(atom("C", 3, -8.96, -1.30));
        m.addAtom(atom("O", 1, -6.10, -1.30));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE);
        m.addBond(7, 8, IBond.Order.DOUBLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.DOUBLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(6, 11, IBond.Order.DOUBLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.SINGLE);
        m.addBond(7, 13, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(0));
    }

    /**
     * @cdk.smiles CC1=C(C=CC=C1)C1=C(C)C=CC=C1
     */
    @Test
    public void nonAtropisomer3() throws CDKException {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 0, -7.53, -2.12));
        m.addAtom(atom("C", 1, -8.24, -2.53));
        m.addAtom(atom("C", 1, -8.24, -3.36));
        m.addAtom(atom("C", 1, -7.53, -3.77));
        m.addAtom(atom("C", 1, -6.82, -3.36));
        m.addAtom(atom("C", 0, -6.82, -2.53));
        m.addAtom(atom("C", 0, -7.53, -1.30));
        m.addAtom(atom("C", 1, -6.82, -0.88));
        m.addAtom(atom("C", 1, -6.82, -0.06));
        m.addAtom(atom("C", 1, -7.53, 0.35));
        m.addAtom(atom("C", 1, -8.24, -0.06));
        m.addAtom(atom("C", 0, -8.24, -0.88));
        m.addAtom(atom("C", 3, -8.96, -1.30));
        m.addAtom(atom("C", 3, -6.10, -2.12));
        m.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE);
        m.addBond(7, 8, IBond.Order.DOUBLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.DOUBLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(6, 11, IBond.Order.DOUBLE);
        m.addBond(0, 6, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.SINGLE);
        m.addBond(5, 13, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(0));
    }


    /**
     * @cdk.smiles [H]C1=CC=C2C=CC=CC2=C1C1=C([H])C=CC2=C1C=CC=C2
     */
    @Test
    public void nonAtropisomerExplHydrogens() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("H", 0, -1.43, 0.83));
        m.addAtom(atom("C", 0, -0.71, 1.24));
        m.addAtom(atom("C", 1, -0.71, 2.06));
        m.addAtom(atom("C", 1, 0.00, 2.48));
        m.addAtom(atom("C", 0, 0.71, 2.06));
        m.addAtom(atom("C", 1, 1.43, 2.48));
        m.addAtom(atom("C", 1, 2.14, 2.06));
        m.addAtom(atom("C", 1, 2.14, 1.24));
        m.addAtom(atom("C", 1, 1.43, 0.83));
        m.addAtom(atom("C", 0, 0.71, 1.24));
        m.addAtom(atom("C", 0, 0.00, 0.83));
        m.addAtom(atom("C", 0, 0.00, 0.00));
        m.addAtom(atom("C", 0, -0.71, -0.41));
        m.addAtom(atom("H", 0, -1.43, 0.00));
        m.addAtom(atom("C", 1, -0.71, -1.24));
        m.addAtom(atom("C", 1, 0.00, -1.65));
        m.addAtom(atom("C", 0, 0.71, -1.24));
        m.addAtom(atom("C", 0, 0.71, -0.41));
        m.addAtom(atom("C", 1, 1.43, 0.00));
        m.addAtom(atom("C", 1, 2.14, -0.41));
        m.addAtom(atom("C", 1, 2.14, -1.24));
        m.addAtom(atom("C", 1, 1.43, -1.65));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(5, 6, IBond.Order.DOUBLE);
        m.addBond(6, 7, IBond.Order.SINGLE);
        m.addBond(7, 8, IBond.Order.DOUBLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(4, 9, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.DOUBLE);
        m.addBond(1, 10, IBond.Order.SINGLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.DOUBLE);
        m.addBond(12, 14, IBond.Order.SINGLE);
        m.addBond(14, 15, IBond.Order.DOUBLE);
        m.addBond(15, 16, IBond.Order.SINGLE);
        m.addBond(16, 17, IBond.Order.DOUBLE);
        m.addBond(11, 17, IBond.Order.SINGLE, IBond.Stereo.UP);
        m.addBond(17, 18, IBond.Order.SINGLE);
        m.addBond(18, 19, IBond.Order.DOUBLE);
        m.addBond(19, 20, IBond.Order.SINGLE);
        m.addBond(20, 21, IBond.Order.DOUBLE);
        m.addBond(16, 21, IBond.Order.SINGLE);
        m.addBond(12, 13, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using2DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(0));
    }

    /**
     * @cdk.smiles CC1=CC=CC(Cl)=C1C1=C(C)C=CC=C1
     */
    @Test
    public void atropisomer3D() {
        IAtomContainer m = new AtomContainer();
        m.addAtom(atom("C", 1, -4.95, 1.27));
        m.addAtom(atom("C", 1, -4.26, 1.73));
        m.addAtom(atom("C", 0, -2.85, 1.74));
        m.addAtom(atom("C", 0, -2.12, 1.25));
        m.addAtom(atom("C", 0, -2.83, 0.80));
        m.addAtom(atom("C", 1, -4.23, 0.82));
        m.addAtom(atom("C", 3, -2.16, 2.26));
        m.addAtom(atom("Cl", 0, -2.04, 0.24));
        m.addAtom(atom("C", 0, -0.70, 1.20));
        m.addAtom(atom("C", 1, 0.00, 2.39));
        m.addAtom(atom("C", 1, 1.41, 2.39));
        m.addAtom(atom("C", 1, 2.12, 1.22));
        m.addAtom(atom("C", 1, 1.44, 0.04));
        m.addAtom(atom("C", 0, 0.02, 0.01));
        m.addAtom(atom("C", 3, -0.62, -1.29));
        m.addBond(0, 1, IBond.Order.SINGLE);
        m.addBond(1, 2, IBond.Order.DOUBLE);
        m.addBond(3, 2, IBond.Order.SINGLE);
        m.addBond(3, 4, IBond.Order.DOUBLE);
        m.addBond(4, 5, IBond.Order.SINGLE);
        m.addBond(0, 5, IBond.Order.DOUBLE);
        m.addBond(2, 6, IBond.Order.SINGLE);
        m.addBond(4, 7, IBond.Order.SINGLE);
        m.addBond(3, 8, IBond.Order.SINGLE);
        m.addBond(9, 10, IBond.Order.DOUBLE);
        m.addBond(10, 11, IBond.Order.SINGLE);
        m.addBond(11, 12, IBond.Order.DOUBLE);
        m.addBond(12, 13, IBond.Order.SINGLE);
        m.addBond(8, 9, IBond.Order.SINGLE);
        m.addBond(8, 13, IBond.Order.DOUBLE);
        m.addBond(13, 14, IBond.Order.SINGLE);
        List<IStereoElement> stereo =
            StereoElementFactory.using3DCoordinates(m)
                                .createAll();
        assertThat(stereo.size(), is(1));
    }
    
    @Test
    public void samePositionWithStereocenter() throws Exception {
    	IAtomContainer m = new AtomContainer();
        m.addAtom(atom("F", 0, -1, -1));
        m.addAtom(atom("Cl", 0, 1, -1));
        m.addAtom(atom("C", 0, 0, 0));
        m.addAtom(atom("Br", 0, 1, 1));
        m.addAtom(atom("H", 0, 0, 0));
        m.addBond(2, 0, IBond.Order.SINGLE);
        m.addBond(2, 1, IBond.Order.SINGLE);
        m.addBond(2, 3, IBond.Order.SINGLE);
        m.addBond(2, 4, IBond.Order.SINGLE);
        m.getBond(2).setStereo(IBond.Stereo.DOWN);
        
        List<IStereoElement> ses = StereoElementFactory.using2DCoordinates(m).createAll();
        boolean flag = false;
        for (IStereoElement se : ses) {
        	if (se != null) {
        		flag = true;
        		break;
        	}
        }
        assertThat(flag, is(true));
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

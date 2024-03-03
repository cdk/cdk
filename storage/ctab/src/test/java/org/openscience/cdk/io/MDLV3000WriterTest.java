/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.stereo.TetrahedralChirality;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

class MDLV3000WriterTest {

    @Test
    void outputValencyWhenNeeded() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("Na"));
        mol.addAtom(new Atom("Na"));
        mol.getAtom(0).setImplicitHydrogenCount(0); // Na metal
        mol.getAtom(1).setImplicitHydrogenCount(1); // Na hydride
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 Na 0 0 0 0 VAL=-1\n"));
        assertThat(res, CoreMatchers.containsString("M  V30 2 Na 0 0 0 0\n"));
    }

    @Test
    void outputFormalCharge() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setFormalCharge(-1);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 O 0 0 0 0 CHG=-1\n"));
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0\n"));
    }

    @Test
    void outputMassNumber() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setMassNumber(2);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        String res = writeToStr(mol);
        // H is pushed to back for compatability
        assertThat(res, CoreMatchers.containsString("M  V30 2 H 0 0 0 0 MASS=2\n"));
        assertThat(res, CoreMatchers.containsString("M  V30 1 C 0 0 0 0\n"));
    }

    @Test
    void outputRadical() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addSingleElectron(0);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 C 0 0 0 0 RAD=2 VAL=3\n"));
    }

    @Test
    void nullBondOrder() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(new Bond(mol.getAtom(0), mol.getAtom(1), null));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setMassNumber(2);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    writeToStr(mol);
                                });
    }

    @Test
    void unsetBondOrder() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.UNSET);
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setMassNumber(2);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    writeToStr(mol);
                                });
    }

    @Test
    void solidWedgeBonds() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 1 1 2 CFG=1\n"));
    }

    @Test
    void hashedWedgeBonds() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.DOWN);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 1 1 2 CFG=3\n"));
    }

    @Test
    void solidWedgeInvBonds() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.UP_INVERTED);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 1 2 1 CFG=1\n"));
    }

    @Test
    void hashedWedgeInvBonds() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE, IBond.Stereo.DOWN_INVERTED);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 1 2 1 CFG=3\n"));
    }

    @Test
    void writeLeadingZero() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        Atom           atom   = new Atom("C");
        atom.setPoint2d(new Point2d(0.5, 1.2));
        mol.addAtom(atom);
        assertThat(writeToStr(mol), CoreMatchers.containsString("0.5 1.2"));
    }

    @Test
    void writeParity() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(1);
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.getAtom(2).setImplicitHydrogenCount(2);
        mol.getAtom(3).setImplicitHydrogenCount(3);
        mol.getAtom(4).setImplicitHydrogenCount(3);
        mol.getAtom(5).setImplicitHydrogenCount(0);
        mol.addStereoElement(new TetrahedralChirality(mol.getAtom(1),
                                                      new IAtom[]{mol.getAtom(0),  // oxygen (look from)
                                                                  mol.getAtom(2),  // Et
                                                                  mol.getAtom(4),  // Me
                                                                  mol.getAtom(5)}, // H
                                                      ITetrahedralChirality.Stereo.CLOCKWISE));
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=2\n"));
    }

    @Test
    void writeParityHNotLast() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(1);
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.getAtom(2).setImplicitHydrogenCount(2);
        mol.getAtom(3).setImplicitHydrogenCount(3);
        mol.getAtom(4).setImplicitHydrogenCount(0);
        mol.getAtom(5).setImplicitHydrogenCount(3);
        mol.addStereoElement(new TetrahedralChirality(mol.getAtom(1),
                                                      new IAtom[]{mol.getAtom(0),  // oxygen (look from)
                                                                  mol.getAtom(2),  // Et
                                                                  mol.getAtom(4),  // H
                                                                  mol.getAtom(5)}, // Me
                                                      ITetrahedralChirality.Stereo.CLOCKWISE));
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=1\n"));
        // H was moved to position 6 from 5
        assertThat(res, CoreMatchers.containsString("M  V30 6 H 0 0 0 0\n"));
    }

    @Test
    void writeParityImplH() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(1);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.getAtom(2).setImplicitHydrogenCount(2);
        mol.getAtom(3).setImplicitHydrogenCount(3);
        mol.getAtom(4).setImplicitHydrogenCount(3);
        mol.addStereoElement(new TetrahedralChirality(mol.getAtom(1),
                                                      new IAtom[]{mol.getAtom(0),  // oxygen (look from)
                                                                  mol.getAtom(2),  // Et
                                                                  mol.getAtom(1),  // H (implicit)
                                                                  mol.getAtom(4)}, // Me
                                                      ITetrahedralChirality.Stereo.CLOCKWISE));
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=1\n"));
    }

    @Test
    void writeParityImplHInverted() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(1);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.getAtom(2).setImplicitHydrogenCount(2);
        mol.getAtom(3).setImplicitHydrogenCount(3);
        mol.getAtom(4).setImplicitHydrogenCount(3);
        mol.addStereoElement(new TetrahedralChirality(mol.getAtom(1),
                                                      new IAtom[]{mol.getAtom(0),  // oxygen (look from)
                                                                  mol.getAtom(1),  // H (implicit)
                                                                  mol.getAtom(2),  // Et
                                                                  mol.getAtom(4)}, // Me
                                                      ITetrahedralChirality.Stereo.CLOCKWISE));
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=2\n"));
    }

    @Test
    void writeSRUs() throws IOException, CDKException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(2);
        mol.getAtom(2).setImplicitHydrogenCount(0);
        mol.getAtom(3).setImplicitHydrogenCount(1);
        List<Sgroup> sgroups = new ArrayList<>();
        Sgroup sgroup = new Sgroup();
        sgroup.addAtom(mol.getAtom(1));
        sgroup.addAtom(mol.getAtom(2));
        sgroup.addBond(mol.getBond(0));
        sgroup.addBond(mol.getBond(2));
        sgroup.setType(SgroupType.CtabStructureRepeatUnit);
        sgroup.setSubscript("n");
        sgroup.putValue(SgroupKey.CtabConnectivity, "HH");
        sgroups.add(sgroup);
        mol.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 SRU 0 ATOMS=(2 2 3) XBONDS=(2 1 3) LABEL=n CONNECT=HH\n"));
    }

    @Test
    void writeMultipleGroup() throws IOException, CDKException {
        final int repeatAtoms = 50;
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        for (int i = 0; i < repeatAtoms; i++)
            mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        for (int i = 0; i < repeatAtoms; i++)
            mol.addBond(i + 1, i + 2, IBond.Order.SINGLE);
        mol.getAtom(0).setImplicitHydrogenCount(3);
        for (int i = 0; i < repeatAtoms; i++)
            mol.getAtom(1 + i).setImplicitHydrogenCount(2);
        mol.getAtom(mol.getAtomCount() - 1).setImplicitHydrogenCount(1);

        List<Sgroup> sgroups = new ArrayList<>();
        Sgroup sgroup = new Sgroup();
        for (int i = 0; i < repeatAtoms; i++)
            sgroup.addAtom(mol.getAtom(i + 1));
        sgroup.addBond(mol.getBond(0));
        sgroup.addBond(mol.getBond(mol.getBondCount() - 1));
        sgroup.putValue(SgroupKey.CtabParentAtomList, Collections.singleton(mol.getAtom(1)));
        sgroup.setType(SgroupType.CtabMultipleGroup);
        sgroup.setSubscript(Integer.toString(repeatAtoms));
        sgroups.add(sgroup);
        mol.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 1 MUL 0 ATOMS=(50 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 2-\n"
                                                    + "M  V30 2 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 -\n"
                                                    + "M  V30 46 47 48 49 50 51) XBONDS=(2 1 51) MULT=50 PATOMS=(1 2)\n"));
    }

    @Test
    void roundTripSRU() throws IOException, CDKException {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-sru-bracketstyles.mol"))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            String res = writeToStr(mol);
            assertThat(res, CoreMatchers.containsString("M  V30 1 SRU 0 ATOMS=(1 2) XBONDS=(2 1 2) LABEL=n CONNECT=HT BRKXYZ=(9 -2.5742-\n"
                                                        + "M  V30  4.207 0 -3.0692 3.3497 0 0 0 0) BRKXYZ=(9 -3.1626 3.3497 0 -3.6576 4.2-\n"
                                                        + "M  V30 07 0 0 0 0) BRKTYP=PAREN\n"
                                                        + "M  V30 2 SRU 0 ATOMS=(1 5) XBONDS=(2 3 4) LABEL=n CONNECT=HT BRKXYZ=(9 0.9542 -\n"
                                                        + "M  V30 4.1874 0 0.4592 3.33 0 0 0 0) BRKXYZ=(9 0.3658 3.33 0 -0.1292 4.1874 0 -\n"
                                                        + "M  V30 0 0 0) BRKTYP=PAREN"));
        }
    }

    @Test
    void roundTripExpandedAbbrv() throws IOException, CDKException {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("triphenyl-phosphate-expanded.mol"))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            String res = writeToStr(mol);
            assertThat(res, CoreMatchers.containsString("M  V30 1 SUP 0 ATOMS=(6 6 19 20 21 22 23) XBONDS=(1 5) ESTATE=E LABEL=Ph\n"
                                                        + "M  V30 2 SUP 0 ATOMS=(6 8 14 15 16 17 18) XBONDS=(1 7) ESTATE=E LABEL=Ph\n"
                                                        + "M  V30 3 SUP 0 ATOMS=(6 7 9 10 11 12 13) XBONDS=(1 6) ESTATE=E LABEL=Ph\n"));
        }
    }

    @Test
    void roundTripOrderMixtures() throws IOException, CDKException {
        try (MDLV2000Reader mdlr = new MDLV2000Reader(getClass().getResourceAsStream("sgroup-ord-mixture.mol"))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            String res = writeToStr(mol);
            assertThat(res, CoreMatchers.containsString("M  V30 1 FOR 0 ATOMS=(24 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21-\n"
                                                        + "M  V30  22 23 24) BRKXYZ=(9 -6.9786 -1.9329 0 -6.9786 4.5847 0 0 0 0) BRKXYZ=(-\n"
                                                        + "M  V30 9 1.9402 4.5847 0 1.9402 -1.9329 0 0 0 0)\n"));
            assertThat(res, CoreMatchers.containsString("M  V30 2 COM 0 ATOMS=(13 1 2 3 4 5 6 7 8 9 10 11 12 13) PARENT=1 BRKXYZ=(9 -6.-\n"
                                                        + "M  V30 5661 -1.1668 0 -6.5661 3.7007 0 0 0 0) BRKXYZ=(9 -2.5532 3.7007 0 -2.55-\n"
                                                        + "M  V30 32 -1.1668 0 0 0 0) COMPNO=1\n"));
            assertThat(res, CoreMatchers.containsString("M  V30 3 COM 0 ATOMS=(11 14 15 16 17 18 19 20 21 22 23 24) PARENT=1 BRKXYZ=(9 -\n"
                                                        + "M  V30 -1.8257 -1.5204 0 -1.8257 4.1722 0 0 0 0) BRKXYZ=(9 1.4727 4.1722 0 1.4-\n"
                                                        + "M  V30 727 -1.5204 0 0 0 0) COMPNO=2\n"));
        }
    }

    @Test
    void positionalVariationRoundTrip() throws Exception {
        try (MDLV3000Reader mdlr = new MDLV3000Reader(getClass().getResourceAsStream("multicenterBond.mol"))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            String res = writeToStr(mol);
            assertThat(res, CoreMatchers.containsString("M  V30 8 1 8 9 ATTACH=ANY ENDPTS=(5 2 3 4 5 6)\n"));
        }
    }

    @Test
    void writeDimensionField() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newAtomContainer();
        IAtom atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setImplicitHydrogenCount(4);
        atom.setPoint2d(new Point2d(0.5, 0.5));
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("2D"));
    }

    @Test
    void writeDimensionField3D() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newAtomContainer();
        IAtom atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setImplicitHydrogenCount(4);
        atom.setPoint3d(new Point3d(0.5, 0.5, 0.1));
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("3D"));
    }

    @Test
    void writeCustomTitle() throws Exception {
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newAtomContainer();
        IAtom atom = builder.newAtom();
        atom.setSymbol("C");
        atom.setImplicitHydrogenCount(4);
        atom.setPoint3d(new Point3d(0.5, 0.5, 0.1));
        mol.addAtom(atom);
        StringWriter sw = new StringWriter();
        try (MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            Properties sdfWriterProps = new Properties();
            sdfWriterProps.put(MDLV2000Writer.OptProgramName, "FakeNews");
            mdlw.addChemObjectIOListener(new PropertiesListener(sdfWriterProps));
            mdlw.customizeJob();
            mdlw.write(mol);
        }
        assertThat(sw.toString(), containsString("FakeNews"));
    }

    private String writeToStr(IAtomContainer mol) throws IOException, CDKException {
        StringWriter sw = new StringWriter();
        try (MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mol);
        }
        return sw.toString();
    }

    @Test
    void testNoChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 COUNTS 7 7 0 0 0"));
        assertThat(sw.toString(), not(containsString("BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1)\n" +
                "END COLLECTION")));
    }

    @Test
    void testChiralFlag() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052112362D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 1\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 COUNTS 7 7 0 0 1"));
        assertThat(sw.toString(), not(containsString("BEGIN COLLECTION\n" +
                "M  V30 M  V30 MDLV30/STEABS ATOMS=(1)\n" +
                "END COLLECTION")));
    }

    @Test
    void testStereoRac1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 COUNTS 7 7 0 0 0"));
        assertThat(sw.toString(), not(containsString("BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1)\n" +
                "END COLLECTION")));
    }

    @Test
    void testStereoRel1() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02052113162D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 7 7 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C -2.1407 12.3148 0 0 CFG=2\n" +
                "M  V30 2 C -3.4743 11.5447 0 0\n" +
                "M  V30 3 C -3.4743 10.0047 0 0\n" +
                "M  V30 4 C -2.1407 9.2347 0 0\n" +
                "M  V30 5 C -0.807 10.0047 0 0\n" +
                "M  V30 6 N -0.807 11.5447 0 0\n" +
                "M  V30 7 O -2.1407 13.8548 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 1 2 3\n" +
                "M  V30 3 1 3 4\n" +
                "M  V30 4 1 4 5\n" +
                "M  V30 5 1 5 6\n" +
                "M  V30 6 1 1 6\n" +
                "M  V30 7 1 1 7 CFG=1\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEREL5 ATOMS=(1 1)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEREL1 ATOMS=(1)\n" +
                "M  V30 END COLLECTION"));
    }

    @Test
    void testStereoRac1And() throws Exception {
        final String input = "\n" +
                "  Mrv1810 02062121432D          \n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 11 11 0 0 1\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 6.16 0 0\n" +
                "M  V30 2 C 0 4.62 0 0 CFG=2\n" +
                "M  V30 3 O -1.3337 3.85 0 0\n" +
                "M  V30 4 C 1.3337 3.85 0 0 CFG=2\n" +
                "M  V30 5 O 2.6674 4.62 0 0\n" +
                "M  V30 6 C 1.3337 2.31 0 0\n" +
                "M  V30 7 C 2.6674 1.54 0 0\n" +
                "M  V30 8 C 2.6674 -0 0 0\n" +
                "M  V30 9 C 1.3337 -0.77 0 0\n" +
                "M  V30 10 C 0 0 0 0\n" +
                "M  V30 11 C 0 1.54 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 2 1\n" +
                "M  V30 2 1 2 3 CFG=1\n" +
                "M  V30 3 1 2 4\n" +
                "M  V30 4 1 4 5 CFG=3\n" +
                "M  V30 5 1 4 6\n" +
                "M  V30 6 1 6 7\n" +
                "M  V30 7 1 7 8\n" +
                "M  V30 8 1 8 9\n" +
                "M  V30 9 1 9 10\n" +
                "M  V30 10 1 10 11\n" +
                "M  V30 11 1 6 11\n" +
                "M  V30 END BOND\n" +
                "M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEABS ATOMS=(1 4)\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(1 2)\n" +
                "M  V30 END COLLECTION\n" +
                "M  V30 END CTAB\n" +
                "M  END\n";
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        StringWriter sw = new StringWriter();
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(input));
             MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
            mdlw.write(mdlr.read(bldr.newAtomContainer()));
        }
        assertThat(sw.toString(), containsString("M  V30 COUNTS 11 11 0 0 0"));
        assertThat(sw.toString(), containsString("M  V30 BEGIN COLLECTION\n" +
                "M  V30 MDLV30/STEABS ATOMS=(4)\n" +
                "M  V30 MDLV30/STERAC1 ATOMS=(2)\n" +
                "M  V30 END COLLECTION"));
    }
}

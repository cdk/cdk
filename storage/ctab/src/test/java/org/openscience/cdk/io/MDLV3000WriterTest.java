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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.Atom;
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

import static org.hamcrest.CoreMatchers.*;
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
    void nullBondOrder() {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(new Bond(mol.getAtom(0), mol.getAtom(1), null));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setMassNumber(2);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        Assertions.assertThrows(CDKException.class, () -> writeToStr(mol));
    }

    @Test
    void unsetBondOrder() throws IOException {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.UNSET);
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(0).setMassNumber(2);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        try {
            writeToStr(mol);
        } catch (CDKException exception) {
            assertThat(exception.getMessage(), is("Bond with bond order UNSET that isn't flagged as aromatic cannot be written to V3000"));
        }
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
        Atom atom = new Atom("C");
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
        mol.addAtom(new Atom("Cl"));
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
        TetrahedralChirality expected = new TetrahedralChirality(mol.getAtom(1),
                                                                new IAtom[]{mol.getAtom(0),  // oxygen (look from)
                                                                            mol.getAtom(2),  // Et
                                                                            mol.getAtom(4),  // Cl
                                                                            mol.getAtom(5)}, // H
                                                                ITetrahedralChirality.Stereo.CLOCKWISE);
        mol.addStereoElement(expected);
        String res = writeToStr(mol);
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=1\n"));

        // ensure round trip of 0D stereo-chemistry
        try (MDLV3000Reader mdlr = new MDLV3000Reader(new StringReader(res))) {
            IAtomContainer roundtrip = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<ITetrahedralChirality> tetrahedrals = new ArrayList<>();
            for (IStereoElement<?, ?> se : roundtrip.stereoElements()) {
                if (se instanceof ITetrahedralChirality)
                    tetrahedrals.add((ITetrahedralChirality) se);
            }
            assertThat(tetrahedrals.size(), is(1));
            assertThat(tetrahedrals.get(0).getStereo(), is(expected.getStereo()));
            List<IAtom> actualCarriers = tetrahedrals.get(0).getCarriers();
            List<IAtom> expectedCarriers = expected.getCarriers();
            for (int i = 0; i < expectedCarriers.size(); i++) {
                assertThat(actualCarriers.get(0).getSymbol(),
                           is(expectedCarriers.get(0).getSymbol()));
            }

        }
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
        assertThat(res, CoreMatchers.containsString("M  V30 2 C 0 0 0 0 CFG=2\n"));
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
        final StringWriter sw = new StringWriter();
        try (final MDLV3000Writer mdlw = new MDLV3000Writer(sw)) {
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

    @Test
    void writeBondTypeFourTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.UNSET);
        mol.addBond(1, 2, IBond.Order.UNSET);
        mol.addBond(2, 3, IBond.Order.UNSET);
        mol.addBond(3, 4, IBond.Order.UNSET);
        mol.addBond(4, 5, IBond.Order.UNSET);
        mol.addBond(5, 0, IBond.Order.UNSET);
        mol.bonds().forEach(bond -> bond.setFlag(IChemObject.AROMATIC, true));
        mol.atoms().forEach(atom -> atom.setImplicitHydrogenCount(1));

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, containsString("M  V30 BEGIN BOND\n" +
                                          "M  V30 1 4 1 2\n" +
                                          "M  V30 2 4 2 3\n" +
                                          "M  V30 3 4 3 4\n" +
                                          "M  V30 4 4 4 5\n" +
                                          "M  V30 5 4 5 6\n" +
                                          "M  V30 6 4 6 1\n" +
                                          "M  V30 END BOND"));
    }

    @Test
    void writeBondTypeFiveTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(mol.getAtom(1), mol.getAtom(2), Expr.Type.SINGLE_OR_DOUBLE);
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        System.out.println(actual);
        assertThat(actual, Matchers.matchesRegex(
                "\n" +
                "  CDK     [0-9]{10}\n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 3 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 0 0 0\n" +
                "M  V30 2 C 0 0 0 0\n" +
                "M  V30 3 C 0 0 0 0\n" +
                "M  V30 4 O 0 0 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 5 2 3\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n"));
    }

    @Test
    void writeBondTypeSixTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(mol.getAtom(1), mol.getAtom(2), Expr.Type.SINGLE_OR_AROMATIC);
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, Matchers.matchesRegex(
                "\n" +
                "  CDK     [0-9]{10}\n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 3 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 0 0 0\n" +
                "M  V30 2 C 0 0 0 0\n" +
                "M  V30 3 C 0 0 0 0\n" +
                "M  V30 4 O 0 0 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 6 2 3\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n"));
    }

    @Test
    void writeBondTypeSevenTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(mol.getAtom(1), mol.getAtom(2), Expr.Type.DOUBLE_OR_AROMATIC);
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, Matchers.matchesRegex(
                "\n" +
                "  CDK     [0-9]{10}\n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 3 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 0 0 0\n" +
                "M  V30 2 C 0 0 0 0\n" +
                "M  V30 3 C 0 0 0 0\n" +
                "M  V30 4 O 0 0 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 7 2 3\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n"));
    }

    @Test
    void writeBondTypeEightTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(mol.getAtom(1), mol.getAtom(2), Expr.Type.TRUE);
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, Matchers.matchesRegex(
                "\n" +
                "  CDK     [0-9]{10}\n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 3 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 0 0 0\n" +
                "M  V30 2 C 0 0 0 0\n" +
                "M  V30 3 C 0 0 0 0\n" +
                "M  V30 4 O 0 0 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 8 2 3\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n"));
    }

    @Test
    void writeBondTypeFiveInRingTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(
                mol.getAtom(1),
                mol.getAtom(2),
                new Expr(Expr.Type.SINGLE_OR_DOUBLE).and(new Expr(Expr.Type.IS_IN_RING))
        );
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, Matchers.matchesRegex(
                "\n" +
                "  CDK     [0-9]{10}\n" +
                "\n" +
                "  0  0  0     0  0            999 V3000\n" +
                "M  V30 BEGIN CTAB\n" +
                "M  V30 COUNTS 4 3 0 0 0\n" +
                "M  V30 BEGIN ATOM\n" +
                "M  V30 1 C 0 0 0 0\n" +
                "M  V30 2 C 0 0 0 0\n" +
                "M  V30 3 C 0 0 0 0\n" +
                "M  V30 4 O 0 0 0 0\n" +
                "M  V30 END ATOM\n" +
                "M  V30 BEGIN BOND\n" +
                "M  V30 1 1 1 2\n" +
                "M  V30 2 5 2 3 TOPO=1\n" +
                "M  V30 3 2 3 4\n" +
                "M  V30 END BOND\n" +
                "M  V30 END CTAB\n" +
                "M  END\n"));
    }

    @Test
    void writeBondTypeSevenInChainTest() throws IOException, CDKException {
        // arrange
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("O"));
        mol.addBond(0, 1, IBond.Order.SINGLE);
        final IQueryBond queryBond = new QueryBond(
                mol.getAtom(1),
                mol.getAtom(2),
                new Expr(Expr.Type.DOUBLE_OR_AROMATIC).and(new Expr(Expr.Type.IS_IN_CHAIN))
        );
        mol.addBond(queryBond);
        mol.addBond(2, 3, IBond.Order.DOUBLE);

        // act
        String actual = writeToStr(mol);

        // assert
        assertThat(actual, Matchers.matchesRegex(
                           "\n" +
                           "  CDK     [0-9]{10}\n" +
                           "\n" +
                           "  0  0  0     0  0            999 V3000\n" +
                           "M  V30 BEGIN CTAB\n" +
                           "M  V30 COUNTS 4 3 0 0 0\n" +
                           "M  V30 BEGIN ATOM\n" +
                           "M  V30 1 C 0 0 0 0\n" +
                           "M  V30 2 C 0 0 0 0\n" +
                           "M  V30 3 C 0 0 0 0\n" +
                           "M  V30 4 O 0 0 0 0\n" +
                           "M  V30 END ATOM\n" +
                           "M  V30 BEGIN BOND\n" +
                           "M  V30 1 1 1 2\n" +
                           "M  V30 2 7 2 3 TOPO=2\n" +
                           "M  V30 3 2 3 4\n" +
                           "M  V30 END BOND\n" +
                           "M  V30 END CTAB\n" +
                           "M  END\n")
                  );
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType4_aromaticBond_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType4_aromaticBond_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 6 6 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C -4.5415 1.04 0 0 VAL=-1\n" +
                               "M  V30 2 C -5.8749 0.2701 0 0 VAL=-1\n" +
                               "M  V30 3 C -5.8749 -1.27 0 0 VAL=-1\n" +
                               "M  V30 4 C -4.5415 -2.04 0 0 VAL=-1\n" +
                               "M  V30 5 C -3.2078 -1.27 0 0 VAL=-1\n" +
                               "M  V30 6 C -3.2078 0.2701 0 0 VAL=-1\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 4 1 2\n" +
                               "M  V30 2 4 2 3\n" +
                               "M  V30 3 4 3 4\n" +
                               "M  V30 4 4 4 5\n" +
                               "M  V30 5 4 5 6\n" +
                               "M  V30 6 4 6 1\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType5_singleOrDouble_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType5_singleOrDouble_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 3 2 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C -17.5389 13.8444 0 0\n" +
                               "M  V30 2 C -16.2052 14.6144 0 0\n" +
                               "M  V30 3 F -14.8715 13.8444 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 1 2\n" +
                               "M  V30 2 5 2 3\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType6_singleOrAromatic_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType6_singleOrAromatic_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 3 2 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C -17.5389 13.8444 0 0\n" +
                               "M  V30 2 C -16.2052 14.6144 0 0\n" +
                               "M  V30 3 F -14.8715 13.8444 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 1 2\n" +
                               "M  V30 2 6 2 3\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType7_doubleOrAromatic_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType7_doubleOrAromatic_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 3 2 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C -17.5389 13.8444 0 0\n" +
                               "M  V30 2 C -16.2052 14.6144 0 0\n" +
                               "M  V30 3 F -14.8715 13.8444 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 1 2\n" +
                               "M  V30 2 7 2 3\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType8_anyBond_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType8_anyBond_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 3 2 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C -17.5389 13.8444 0 0\n" +
                               "M  V30 2 C -16.2052 14.6144 0 0\n" +
                               "M  V30 3 F -14.8715 13.8444 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 1 2\n" +
                               "M  V30 2 8 2 3\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType5_singleOrDouble_bondTopology_inRing_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType5_singleOrDouble_bondTopology_inRing_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 6 6 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C 5.4833 -0.5211 0 0\n" +
                               "M  V30 2 N 4.1497 -1.2911 0 0\n" +
                               "M  V30 3 C 4.1497 -2.8311 0 0\n" +
                               "M  V30 4 C 5.4833 -3.6011 0 0\n" +
                               "M  V30 5 C 6.817 -2.8311 0 0\n" +
                               "M  V30 6 C 6.817 -1.2911 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 2 3\n" +
                               "M  V30 2 1 3 4\n" +
                               "M  V30 3 1 4 5\n" +
                               "M  V30 4 1 5 6\n" +
                               "M  V30 5 1 1 6\n" +
                               "M  V30 6 5 1 2 TOPO=1\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    @Test
    void roundTrip_V3000read_V3000write_bondType5_singleOrDouble_bondTopology_inChain_test() throws Exception {
        // arrange
        try (MDLV3000Reader reader = new MDLV3000Reader(getClass().getResourceAsStream("bondType5_singleOrDouble_bondTopology_inChain_v3000.mol"))) {
            final IAtomContainer mol = reader.read(SilentChemObjectBuilder.getInstance().newAtomContainer());

            // act
            final String actual = writeToStr(mol);

            // assess
            assertThat(actual, Matchers.matchesRegex(
                               "\n" +
                               "  CDK     [0-9]{10}2D\n" +
                               "\n" +
                               "  0  0  0     0  0            999 V3000\n" +
                               "M  V30 BEGIN CTAB\n" +
                               "M  V30 COUNTS 6 6 0 0 0\n" +
                               "M  V30 BEGIN ATOM\n" +
                               "M  V30 1 C 5.4833 -0.5211 0 0\n" +
                               "M  V30 2 N 4.1497 -1.2911 0 0\n" +
                               "M  V30 3 C 4.1497 -2.8311 0 0\n" +
                               "M  V30 4 C 5.4833 -3.6011 0 0\n" +
                               "M  V30 5 C 6.817 -2.8311 0 0\n" +
                               "M  V30 6 C 6.817 -1.2911 0 0\n" +
                               "M  V30 END ATOM\n" +
                               "M  V30 BEGIN BOND\n" +
                               "M  V30 1 1 2 3\n" +
                               "M  V30 2 1 3 4\n" +
                               "M  V30 3 1 4 5\n" +
                               "M  V30 4 1 5 6\n" +
                               "M  V30 5 1 1 6\n" +
                               "M  V30 6 5 1 2 TOPO=2\n" +
                               "M  V30 END BOND\n" +
                               "M  V30 END CTAB\n" +
                               "M  END\n")
                      );
        }
    }

    ///// Below are tests assessing inner class MDLV3000Writer.ExpressionConverter. /////

    @Test
    void toMDLBondType_true_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.TRUE);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.ANY));
    }

    @Test
    void toMDLBondType_orderSingle_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 1);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.SINGLE));
    }

    @Test
    void toMDLBondType_and_orderDouble_isInChain_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 2).and(new Expr(Expr.Type.IS_IN_CHAIN));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.DOUBLE));
    }

    @Test
    void toMDLBondType_and_orderTriple_isAliphatic_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 3).and(new Expr(Expr.Type.IS_ALIPHATIC));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.TRIPLE));
    }

    @Test
    void toMDLBondType_isAromatic_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.IS_AROMATIC);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.AROMATIC));
    }

    @Test
    void toMDLBondType_and_doubleOrAromatic_isInRing_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.DOUBLE_OR_AROMATIC).and(new Expr(Expr.Type.IS_IN_RING));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.DOUBLE_OR_AROMATIC));
    }

    @Test
    void toMDLBondType_and_singleOrAromatic_isInChain_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.SINGLE_OR_AROMATIC).and(new Expr(Expr.Type.IS_IN_CHAIN));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.SINGLE_OR_AROMATIC));
    }

    @Test
    void toMDLBondType_and_singleOrDouble_isInChain_test() throws CDKException {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 1).or(new Expr(Expr.Type.ORDER, 2));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLBondType actual = converter.toMDLBondType();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLBondType.SINGLE_OR_DOUBLE));
    }

    @Test
    void toMDLQueryProperty_orderOne_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 1);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.NOT_SPECIFIED));
    }

    @Test
    void toMDLQueryProperty_orderSingleOrDouble_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.SINGLE_OR_DOUBLE);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.NOT_SPECIFIED));
    }

    @Test
    void toMDLQueryProperty_isAromatic_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.IS_AROMATIC);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.NOT_SPECIFIED));
    }

    @Test
    void toMDLQueryProperty_and_orderDoubleOrAromatic_inChain_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.DOUBLE_OR_AROMATIC).and(new Expr(Expr.Type.IS_IN_CHAIN));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.CHAIN));
    }

    @Test
    void toMDLQueryProperty_and_orderDouble_isInRing_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.ORDER, 1).and(new Expr(Expr.Type.IS_IN_RING));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.RING));
    }

    @Test
    void toMDLQueryProperty_and_isInChain_isInRing_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.IS_IN_CHAIN).and(new Expr(Expr.Type.IS_IN_RING));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.NOT_SPECIFIED));
    }

    @Test
    void toMDLQueryProperty_true_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.TRUE);
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.NOT_SPECIFIED));
    }

    @Test
    void toMDLQueryProperty_and_orderSingleOrDouble_isInRing_test() {
        // arrange
        final Expr expression = new Expr(Expr.Type.SINGLE_OR_DOUBLE).and(new Expr(Expr.Type.IS_IN_RING));
        final MDLV3000Writer.ExpressionConverter converter = new MDLV3000Writer.ExpressionConverter(expression);

        // act
        final MDLV3000Writer.MDLQueryProperty actual = converter.toMDLQueryProperty();

        // assert
        assertThat(actual, is(MDLV3000Writer.MDLQueryProperty.RING));
    }

}

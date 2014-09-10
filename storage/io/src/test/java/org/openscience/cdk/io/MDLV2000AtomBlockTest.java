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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-io
 */
public class MDLV2000AtomBlockTest {

    private final MDLV2000Reader     reader  = new MDLV2000Reader();
    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    public void lineLength_excessSpace() throws Exception {
        IAtom a1 = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0 ",
                builder, 1);
        IAtom a2 = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0    ",
                builder, 1);
    }

    @Test
    public void lineLength_exact() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
    }

    @Test
    public void lineLength_truncated() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  ",
                builder, 1);
    }

    @Test
    public void symbol_C() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getSymbol(), is("C"));
    }

    @Test
    public void symbol_N() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getSymbol(), is("N"));
    }

    @Test
    public void readCoordinates() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getPoint3d().x, is(closeTo(7.8089, 0.5)));
        assertThat(atom.getPoint3d().y, is(closeTo(-1.3194, 0.5)));
        assertThat(atom.getPoint3d().z, is(closeTo(0.0d, 0.5)));
    }

    @Test
    public void massDiff_c13() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   1  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getMassNumber(), is(13));
    }

    @Test
    public void massDiff_c14() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   2  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getMassNumber(), is(14));
    }

    @Test
    public void massDiff_c11() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C  -1  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getMassNumber(), is(11));
    }

    @Test
    public void charge_cation() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  1  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(3));
    }

    @Test
    public void charge_dication() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  2  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(2));
    }

    @Test
    public void charge_trication() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  3  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(1));
    }

    // SingleElectronContainer created by M  RAD
    @Test
    public void charge_doubletradical() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  4  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(0));
    }

    @Test
    public void charge_anion() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  5  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(-1));
    }

    @Test
    public void charge_dianion() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  6  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(-2));
    }

    @Test
    public void charge_trianion() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  7  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(-3));
    }

    @Test
    public void charge_invalid() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  8  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getFormalCharge(), is(0));
    }

    @Test
    public void valence_0() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0 15  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getValency(), is(0));
    }

    @Test
    public void valence_unset() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getValency(), is(nullValue()));
    }

    @Test
    public void valence_1() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  1  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getValency(), is(1));
    }

    @Test
    public void valence_14() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0 14  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getValency(), is(14));
    }

    @Test
    public void valence_invalid() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0 16  0  0  0  0  0  0",
                builder, 1);
        assertThat(atom.getValency(), is(nullValue()));
    }

    @Test
    public void mapping() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0  1  0  0",
                builder, 1);
        assertThat(atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.class), is(1));
    }

    @Test
    public void mapping_42() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0 42  0  0",
                builder, 1);
        assertThat(atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.class), is(42));
    }

    @Test
    public void mapping_999() throws Exception {
        IAtom atom = reader.readAtomFast("    7.8089   -1.3194    0.0000 C   0  0  0  0  0  0  0  0  0999  0  0",
                builder, 1);
        assertThat(atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.class), is(999));
    }

    @Test
    public void lonePairAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("LP"));
    }

    @Test
    public void atomListAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("L"));
    }

    @Test
    public void heavyAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("A"));
    }

    @Test
    public void hetroAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("Q"));
    }

    @Test
    public void unspecifiedAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("*"));
    }

    @Test
    public void rGroupAtomSymbol() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("R"));
    }

    @Test
    public void rGroupAtomSymbol_hash() throws Exception {
        assertTrue(MDLV2000Reader.isPseudoElement("R#"));
    }

    @Test
    public void invalidAtomSymbol() throws Exception {
        assertFalse(MDLV2000Reader.isPseudoElement("RNA"));
        assertFalse(MDLV2000Reader.isPseudoElement("DNA"));
        assertFalse(MDLV2000Reader.isPseudoElement("ACP"));
    }

    @Test
    public void readMDLCoordinate() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("    7.8089", 0), is(closeTo(7.8089, 0.1)));
    }

    @Test
    public void readMDLCoordinate_negative() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("   -2.0012", 0), is(closeTo(-2.0012, 0.1)));
    }

    @Test
    public void readMDLCoordinate_offset() throws Exception {
        assertThat(MDLV2000Reader.readMDLCoordinate("   -2.0012    7.8089", 10), is(closeTo(7.8089, 0.1)));
    }

}

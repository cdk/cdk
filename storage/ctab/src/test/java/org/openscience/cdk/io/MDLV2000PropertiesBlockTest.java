/*
 * Copyright (c) 2013  European Bioinformatics Institute (EMBL-EBI)
 *                     John May <jwmay@users.sf.net>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author John May
 */
class MDLV2000PropertiesBlockTest {

    private final MDLV2000Reader     reader  = new MDLV2000Reader();
    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    void m_end() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  END"), is(MDLV2000Reader.PropertyKey.M_END));
    }

    @Test
    void m_end_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  END  "), is(MDLV2000Reader.PropertyKey.M_END));
    }

    @Test
    void m_chg_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  CHG  "), is(MDLV2000Reader.PropertyKey.M_CHG));
    }

    @Test
    void m_iso_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  ISO  "), is(MDLV2000Reader.PropertyKey.M_ISO));
    }

    @Test
    void atom_alias() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("A    1"), is(MDLV2000Reader.PropertyKey.ATOM_ALIAS));
    }

    @Test
    void atom_value() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("V    1"), is(MDLV2000Reader.PropertyKey.ATOM_VALUE));
    }

    @Test
    void group_abrv() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("G    1"), is(MDLV2000Reader.PropertyKey.GROUP_ABBREVIATION));
    }

    @Test
    void skip() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("S  SKP  5"), is(MDLV2000Reader.PropertyKey.SKIP));
    }

    /** ACDLabs ChemSketch atom labels */
    @Test
    void m_zzc_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  ZZC  "), is(MDLV2000Reader.PropertyKey.M_ZZC));
    }
    
    @Test
    void anion() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  CHG  1   1  -1", mock);
        Assertions.assertEquals(mock.getAtom(0).getFormalCharge(), -1);
    }

    @Test
    void cation() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  CHG  1   1   1", mock);
        Assertions.assertEquals(mock.getAtom(0).getFormalCharge(), +1);
    }

    @Test
    void multipleCharges() throws Exception {
        IAtomContainer mock = mock(6);
        read("M  CHG  2   2   1   5  -2", mock);
        Assertions.assertEquals(mock.getAtom(1).getFormalCharge(), +1);
        Assertions.assertEquals(mock.getAtom(4).getFormalCharge(), -2);
    }

    @Test
    void multipleChargesTruncated() throws Exception {
        IAtomContainer mock = mock(6);
        read("M  CHG  2   2  -3", mock);
        Assertions.assertEquals(mock.getAtom(1).getFormalCharge(), -3);
    }

    @Test
    void c13() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  ISO  1   1  13", mock);
        Assertions.assertEquals(mock.getAtom(0).getMassNumber(), 13);
    }

    @Test
    void c13n14() throws Exception {
        IAtomContainer mock = mock(4);
        read("M  ISO  2   1  13   3  14", mock);
        Assertions.assertEquals(mock.getAtom(0).getMassNumber(), 13);
        Assertions.assertEquals(mock.getAtom(2).getMassNumber(), 14);
    }

    @Test
    void atomValue() throws Exception {
        IAtomContainer mock = mock(3);
        read("V    1 A Comment", mock);
        Assertions.assertEquals(mock.getAtom(0).getProperty(CDKConstants.COMMENT), "A Comment");
    }

    @Test
    void atomAlias() throws Exception {
        IAtomContainer mock = mock(4);
        read("A    4\n" + "Gly", mock);
        assertThat(mock.getAtom(3), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mock.getAtom(3)).getLabel(), is("Gly"));
    }

    @Test
    void acdAtomLabel() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  ZZC   1 6", mock);
        Assertions.assertEquals(mock.getAtom(0).getProperty(CDKConstants.ACDLABS_LABEL), "6");
    }
    
    static IAtomContainer mock(int n) {
        IAtomContainer mock = SilentChemObjectBuilder.getInstance().newAtomContainer();
        for (int i = 0; i < n; i++)
            mock.newAtom();
        return mock;
    }

    void read(String input, IAtomContainer container) throws IOException, CDKException {
        reader.readPropertiesFast(new BufferedReader(new StringReader(input)), container, container.getAtomCount());
    }

}

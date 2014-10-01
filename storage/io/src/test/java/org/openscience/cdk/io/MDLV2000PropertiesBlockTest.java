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

import org.junit.Test;
import org.mockito.Mockito;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author John May
 * @cdk.module test-io
 */
public class MDLV2000PropertiesBlockTest {

    private final MDLV2000Reader     reader  = new MDLV2000Reader();
    private final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    @Test
    public void m_end() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  END"), is(MDLV2000Reader.PropertyKey.M_END));
    }

    @Test
    public void m_end_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  END  "), is(MDLV2000Reader.PropertyKey.M_END));
    }

    @Test
    public void m_chg_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  CHG  "), is(MDLV2000Reader.PropertyKey.M_CHG));
    }

    @Test
    public void m_iso_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  ISO  "), is(MDLV2000Reader.PropertyKey.M_ISO));
    }

    @Test
    public void atom_alias() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("A    1"), is(MDLV2000Reader.PropertyKey.ATOM_ALIAS));
    }

    @Test
    public void atom_value() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("V    1"), is(MDLV2000Reader.PropertyKey.ATOM_VALUE));
    }

    @Test
    public void group_abrv() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("G    1"), is(MDLV2000Reader.PropertyKey.GROUP_ABBREVIATION));
    }

    @Test
    public void skip() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("S  SKP  5"), is(MDLV2000Reader.PropertyKey.SKIP));
    }

    /** ACDLabs ChemSketch atom labels */
    @Test
    public void m_zzc_padding() throws Exception {
        assertThat(MDLV2000Reader.PropertyKey.of("M  ZZC  "), is(MDLV2000Reader.PropertyKey.M_ZZC));
    }
    
    @Test
    public void anion() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  CHG  1   1  -1", mock);
        verify(mock.getAtom(0)).setFormalCharge(-1);
    }

    @Test
    public void cation() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  CHG  1   1   1", mock);
        verify(mock.getAtom(0)).setFormalCharge(+1);
    }

    @Test
    public void multipleCharges() throws Exception {
        IAtomContainer mock = mock(6);
        read("M  CHG  2   2   1   5  -2", mock);
        verify(mock.getAtom(1)).setFormalCharge(+1);
        verify(mock.getAtom(4)).setFormalCharge(-2);
    }

    @Test
    public void multipleChargesTruncated() throws Exception {
        IAtomContainer mock = mock(6);
        read("M  CHG  2   2  -3", mock);
        verify(mock.getAtom(1)).setFormalCharge(-3);
    }

    @Test
    public void c13() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  ISO  1   1  13", mock);
        verify(mock.getAtom(0)).setMassNumber(13);
    }

    @Test
    public void c13n14() throws Exception {
        IAtomContainer mock = mock(4);
        read("M  ISO  2   1  13   3  14", mock);
        verify(mock.getAtom(0)).setMassNumber(13);
        verify(mock.getAtom(2)).setMassNumber(14);
    }

    @Test
    public void atomValue() throws Exception {
        IAtomContainer mock = mock(3);
        read("V    1 A Comment", mock);
        verify(mock.getAtom(0)).setProperty(CDKConstants.COMMENT, "A Comment");
    }

    @Test
    public void atomAlias() throws Exception {
        IAtomContainer mock = mock(4);
        read("A    4\n" + "Gly", mock);
        assertThat(mock.getAtom(3), is(instanceOf(IPseudoAtom.class)));
        assertThat(((IPseudoAtom) mock.getAtom(3)).getLabel(), is("Gly"));
    }

    @Test
    public void acdAtomLabel() throws Exception {
        IAtomContainer mock = mock(3);
        read("M  ZZC   1 6", mock);
        verify(mock.getAtom(0)).setProperty(CDKConstants.ACDLABS_LABEL, "6");
    }
    
    static IAtomContainer mock(int n) {
        IAtomContainer mock = new AtomContainer(n, 0, 0, 0);
        for (int i = 0; i < n; i++)
            mock.addAtom(Mockito.mock(IAtom.class));
        return mock;
    }

    void read(String input, IAtomContainer container) throws IOException, CDKException {
        reader.readPropertiesFast(new BufferedReader(new StringReader(input)), container, container.getAtomCount());
    }

}

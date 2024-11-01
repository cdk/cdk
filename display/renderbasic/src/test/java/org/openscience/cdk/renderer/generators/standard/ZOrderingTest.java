/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.renderer.generators.standard;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.StringReader;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;

class ZOrderingTest {

    public IAtomContainer loadMolfile(String molfile) throws CDKException {
        MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile));
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();
        return mdlr.read(mol);
    }

    private void assertZOrder(IBond lo, IBond hi)
    {
        Integer zlo = lo.getProperty(CDKConstants.Z_ORDER);
        Integer zhi = hi.getProperty(CDKConstants.Z_ORDER);
        Assertions.assertNotNull(zlo);
        Assertions.assertNotNull(zhi);
        MatcherAssert.assertThat("Bond idx=" +  lo.getIndex() + " was not lower than idx=" + hi.getIndex(),
                                 zlo, is(lessThan(zhi)));
    }

    @Test
    public void internalWedgeCues() throws CDKException {
        IAtomContainer mol = loadMolfile(
                        "\n" +
                        "  CDK     11012421112D\n" +
                        "\n" +
                        "  8  9  0  0  1  0  0  0  0  0999 V2000\n" +
                        "   -7.1352    4.4027    0.0000 C   0  0  2  0  0  0\n" +
                        "   -7.6828    3.8646    0.0000 C   0  0\n" +
                        "   -6.3160    4.0811    0.0000 C   0  0\n" +
                        "   -6.8982    4.1195    0.0000 C   0  0  1  0  0  0\n" +
                        "   -6.1445    3.7840    0.0000 C   0  0\n" +
                        "   -5.9088    4.5786    0.0000 C   0  0\n" +
                        "   -7.0697    4.9265    0.0000 C   0  0\n" +
                        "   -7.3247    5.2013    0.0000 C   0  0\n" +
                        "  1  2  1  1\n" +
                        "  1  3  1  0\n" +
                        "  4  2  1  6\n" +
                        "  4  5  1  0\n" +
                        "  3  6  1  0\n" +
                        "  5  6  1  0\n" +
                        "  4  7  1  0\n" +
                        "  1  8  1  0\n" +
                        "  7  8  1  0\n" +
                        "M  END");
        ZOrdering.assign(mol);
        assertZOrder(mol.getBond(1), mol.getBond(6));
    }

    @Test
    public void externalWedgeCues() throws CDKException {
        IAtomContainer mol = loadMolfile(
                "\n" +
                        "  CDK     11012421192D\n" +
                        "\n" +
                        "  9 10  0  0  1  0  0  0  0  0999 V2000\n" +
                        "   -3.7768    4.6198    0.0000 C   0  0\n" +
                        "   -4.3245    4.0818    0.0000 C   0  0\n" +
                        "   -2.9577    4.2983    0.0000 C   0  0\n" +
                        "   -3.5398    4.3367    0.0000 C   0  0  1  0  0  0\n" +
                        "   -2.7861    4.0011    0.0000 C   0  0\n" +
                        "   -2.5504    4.7957    0.0000 C   0  0\n" +
                        "   -3.7114    5.1437    0.0000 C   0  0\n" +
                        "   -3.9663    5.4184    0.0000 C   0  0\n" +
                        "   -3.4536    3.5162    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  1  3  1  0\n" +
                        "  4  2  1  0\n" +
                        "  4  5  1  0\n" +
                        "  3  6  1  0\n" +
                        "  5  6  1  0\n" +
                        "  4  7  1  0\n" +
                        "  1  8  1  0\n" +
                        "  7  8  1  0\n" +
                        "  4  9  1  1\n" +
                        "M  END");
        ZOrdering.assign(mol);
        assertZOrder(mol.getBond(1), mol.getBond(6));
    }

    @Test
    public void testCubane() throws CDKException {
        IAtomContainer mol = loadMolfile(
                "\n" +
                        "  CDK     11012421192D\n" +
                        "\n" +
                        "  8 12  0  0  0  0  0  0  0  0999 V2000\n" +
                        "    2.3862    4.9820    0.0000 C   0  0\n" +
                        "    3.0336    4.8211    0.0000 C   0  0\n" +
                        "    3.0271    3.7980    0.0000 C   0  0\n" +
                        "    2.3818    3.9596    0.0000 C   0  0\n" +
                        "    1.5642    3.8397    0.0000 C   0  0\n" +
                        "    1.5706    4.8621    0.0000 C   0  0\n" +
                        "    2.2181    4.7006    0.0000 C   0  0\n" +
                        "    2.2102    3.6784    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  2  3  1  0\n" +
                        "  3  4  1  0\n" +
                        "  1  4  1  0\n" +
                        "  4  5  1  0\n" +
                        "  5  6  1  0\n" +
                        "  1  6  1  0\n" +
                        "  6  7  1  0\n" +
                        "  2  7  1  0\n" +
                        "  7  8  1  0\n" +
                        "  3  8  1  0\n" +
                        "  5  8  1  0\n" +
                        "M  END");
        ZOrdering.assign(mol);
        // technically the other way around is OK
        assertZOrder(mol.getBond(3), mol.getBond(8));
        assertZOrder(mol.getBond(4), mol.getBond(9));
    }

    @Test
    public void noCues() throws CDKException {
        IAtomContainer mol = loadMolfile(
                "\n" +
                        "  CDK     11012421192D\n" +
                        "\n" +
                        "  8 12  0  0  0  0  0  0  0  0999 V2000\n" +
                        "    2.3862    4.9820    0.0000 C   0  0\n" +
                        "    3.0336    4.8211    0.0000 C   0  0\n" +
                        "    3.0271    3.7980    0.0000 C   0  0\n" +
                        "    2.3818    3.9596    0.0000 C   0  0\n" +
                        "    1.5642    3.8397    0.0000 C   0  0\n" +
                        "    1.5706    4.8621    0.0000 C   0  0\n" +
                        "    2.2181    4.7006    0.0000 C   0  0\n" +
                        "    2.2102    3.6784    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  2  3  1  0\n" +
                        "  3  4  1  0\n" +
                        "  1  4  1  0\n" +
                        "  4  5  1  0\n" +
                        "  5  6  1  0\n" +
                        "  1  6  1  0\n" +
                        "  6  7  1  0\n" +
                        "  2  7  1  0\n" +
                        "  7  8  1  0\n" +
                        "  3  8  1  0\n" +
                        "  5  8  1  0\n" +
                        "M  END");
        ZOrdering.assign(mol);
        // technically the other way around is OK
        assertZOrder(mol.getBond(3), mol.getBond(8));
        assertZOrder(mol.getBond(4), mol.getBond(9));
    }

    @Test
    public void overlapRings() throws CDKException {
        IAtomContainer mol = loadMolfile(
                "\n" +
                        "  CDK     11012421192D\n" +
                        "\n" +
                        " 17 18  0  0  1  0  0  0  0  0999 V2000\n" +
                        "   -8.0805    2.2540    0.0000 C   0  0\n" +
                        "   -8.0805    1.4289    0.0000 C   0  0\n" +
                        "   -7.3660    1.0165    0.0000 C   0  0\n" +
                        "   -6.6515    1.4289    0.0000 C   0  0\n" +
                        "   -6.6515    2.2540    0.0000 C   0  0\n" +
                        "   -7.3660    0.1915    0.0000 C   0  0\n" +
                        "   -6.6515   -0.2209    0.0000 C   0  0\n" +
                        "   -5.9371    0.1915    0.0000 C   0  0  1  0  0  0\n" +
                        "   -5.9371    1.0165    0.0000 C   0  0\n" +
                        "   -5.2226   -0.2209    0.0000 C   0  0\n" +
                        "   -6.4220    1.6839    0.0000 C   0  0\n" +
                        "   -6.0864    2.4376    0.0000 C   0  0\n" +
                        "   -6.5714    3.1051    0.0000 C   0  0\n" +
                        "   -7.7275    2.2652    0.0000 C   0  0\n" +
                        "   -7.2425    1.5977    0.0000 C   0  0\n" +
                        "   -7.3918    3.0188    0.0000 C   0  0\n" +
                        "   -7.3660    2.6665    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  2  3  1  0\n" +
                        "  3  4  1  0\n" +
                        "  4  5  1  0\n" +
                        "  3  6  1  0\n" +
                        "  6  7  1  0\n" +
                        "  7  8  1  0\n" +
                        "  8  9  1  1\n" +
                        "  8 10  1  0\n" +
                        "  9 11  1  0\n" +
                        " 12 13  1  0\n" +
                        " 14 15  1  0\n" +
                        " 11 12  1  0\n" +
                        " 11 15  1  0\n" +
                        " 13 16  1  0\n" +
                        " 16 14  1  0\n" +
                        " 17  1  1  0\n" +
                        " 17  5  1  0\n" +
                        "M  END");
        ZOrdering.assign(mol);
        // technically the other way around is also OK
        assertZOrder(mol.getBond(3), mol.getBond(13));
        assertZOrder(mol.getBond(16), mol.getBond(15));
    }
}
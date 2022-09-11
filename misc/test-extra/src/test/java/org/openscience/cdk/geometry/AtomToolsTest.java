/*
 * =====================================
 *  Copyright (c) 2020 NextMove Software
 * =====================================
 */

package org.openscience.cdk.geometry;

import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import javax.vecmath.Point3d;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;

class AtomToolsTest {

    @Test
    void calculate3DCoordinates3() throws Exception {
        String molfile = "\n" +
                "  Mrv1810 12152010123D          \n" +
                "\n" +
                "  4  3  0  0  0  0            999 V2000\n" +
                "    2.0575    1.4744   -0.0102 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    1.5159    0.0200    0.0346 C   0  0  1  0  0  0  0  0  0  0  0  0\n" +
                "    2.0575   -0.7460    1.2717 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.0359   -0.0059   -0.0102 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  2  4  1  0  0  0  0\n" +
                "M  END\n";
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            Point3d newP = AtomTools.calculate3DCoordinates3(
                    mol.getAtom(1).getPoint3d(),
                    mol.getAtom(0).getPoint3d(),
                    mol.getAtom(2).getPoint3d(),
                    mol.getAtom(3).getPoint3d(), 1.5);
            Assertions.assertNotNull(newP);
            assertThat(newP.x, IsCloseTo.closeTo(2.0160, 0.001));
            assertThat(newP.y, IsCloseTo.closeTo(-0.6871, 0.001));
            assertThat(newP.z, IsCloseTo.closeTo(-1.1901, 0.001));
        }
    }
}
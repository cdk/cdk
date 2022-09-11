/*
 * Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.structgen.maygen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * <p>
 * Unit test class for the MAYGEN class. Randomly selected molecular formulae are tested.
 * The number of generated structures are checked. The number of isomers are also tested with
 * MOLGEN algorithm. MAYGEN generates same number of isomers like MOLGEN.
 * </p>
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * @cdk.module structgen
 */
class MaygenTest {

    @Test
    void test_gettersAndSetters() {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setTsvoutput(true);
        Assertions.assertTrue(maygen.isTsvoutput());
        maygen.isMultiThread();
        maygen.getFormula();
        maygen.getFuzzyFormula();
        maygen.getTotal();
        maygen.getSymbols();
        maygen.getOccurrences();
        maygen.getOxygenSulfur();
        maygen.getTotalHydrogen();
        maygen.isOnSm();
        maygen.setVerbose(true);
        maygen.getVerbose();
        maygen.isSetElement();
    }

    @Test
    void test_C3Cl2H4_writeSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFormula("C3Cl2H4");
        StringWriter sw = new StringWriter();
        maygen.setConsumer(new SmiOutputConsumer(sw));
        maygen.run();
        Assertions.assertEquals(7, maygen.getCount());
        Assertions.assertEquals("C1(Cl)(Cl)CC1\n" +
                        "C(Cl)(=C)CCl\n" +
                        "C(=CCl)(C)Cl\n" +
                        "C(=CC)(Cl)Cl\n" +
                        "C(Cl)C=CCl\n" +
                        "C=CC(Cl)Cl\n" +
                        "C1C(Cl)C1Cl\n", sw.toString());
    }

    @Test
    void test_C3Cl2H4_writeSdf()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFormula("C3Cl2H4");
        StringWriter sw = new StringWriter();
        maygen.setConsumer(new SdfOutputConsumer(sw));
        maygen.run();
        Assertions.assertEquals(7, maygen.getCount());
        String res = sw.toString();
        assertThat(res,
                containsString("  5  5  0  0  0  0  0  0  0  0999 V2000\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "    0.0000    0.0000    0.0000 Cl  0  0\n" +
                        "    0.0000    0.0000    0.0000 Cl  0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  1  3  1  0\n" +
                        "  1  4  1  0\n" +
                        "  1  5  1  0\n" +
                        "  4  5  1  0\n" +
                        "M  END\n"));
        assertThat(res,
                containsString("  5  4  0  0  0  0  0  0  0  0999 V2000\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "    0.0000    0.0000    0.0000 Cl  0  0\n" +
                        "    0.0000    0.0000    0.0000 Cl  0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  1  4  2  0\n" +
                        "  1  5  1  0\n" +
                        "  3  5  1  0\n" +
                        "M  END\n"));
    }

    // important! SDG changes may affect this test!
    @Test
    void test_C3Cl2H4_sdfCoordinates()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFormula("C3Cl2H4");
        StringWriter sw = new StringWriter();
        SdfOutputConsumer consumer = new SdfOutputConsumer(sw);
        consumer.setCoordinates(true);
        maygen.setConsumer(consumer);
        maygen.run();
        String res = sw.toString();
        assertThat(res,
                containsString("  5  5  0  0  0  0  0  0  0  0999 V2000\n" +
                        "   -0.7883   -0.4645    0.0000 C   0  0\n" +
                        "   -2.2654   -0.7252    0.0000 Cl  0  0\n" +
                        "   -0.2751   -1.8739    0.0000 Cl  0  0\n" +
                        "   -0.7887    1.0364    0.0000 C   0  0\n" +
                        "    0.5108    0.2855    0.0000 C   0  0\n" +
                        "  1  2  1  0\n" +
                        "  1  3  1  0\n" +
                        "  1  4  1  0\n" +
                        "  1  5  1  0\n" +
                        "  4  5  1  0\n" +
                        "M  END"));
        assertThat(res,
                containsString("  5  4  0  0  0  0  0  0  0  0999 V2000\n" +
                        "    0.0000    1.5000    0.0000 C   0  0\n" +
                        "   -1.2990    2.2500    0.0000 C   0  0\n" +
                        "    0.0000    0.0000    0.0000 C   0  0\n" +
                        "    1.2990    2.2500    0.0000 Cl  0  0\n" +
                        "   -1.2990    3.7500    0.0000 Cl  0  0\n" +
                        "  1  2  2  0\n" +
                        "  1  3  1  0\n" +
                        "  1  4  1  4\n" +
                        "  2  5  1  0\n" +
                        "M  END"));
    }

    @Test
    void test_C_1_6_Cl2_H_4_8_writeSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFuzzyFormula("C[1-6]Cl2H[4-8]");
        StringWriter sw = new StringWriter();
        maygen.setConsumer(new SmiOutputConsumer(sw));
        maygen.run();
        Assertions.assertEquals(4141, maygen.getFuzzyCount());
        assertThat(sw.toString(),
                allOf(containsString("C12CC(=C1C)C2(Cl)Cl\n"),
                        containsString("C12CC3(Cl)C1(C)C23Cl\n"),
                        containsString("C12CC(Cl)(Cl)C1=C2C\n"),
                        containsString("C12CC(C)=C1C2(Cl)Cl\n"),
                        containsString("C12CC3(C)C1(Cl)C23Cl\n"),
                        containsString("C12CC(C)(Cl)C1=C2Cl\n"),
                        containsString("C1(CC(C)(Cl)Cl)C#C1\n"),
                        containsString("C(C)(Cl)C1(CCl)C#C1\n"),
                        containsString("C(C)(Cl)C=1C(CCl)=C1\n"),
                        containsString("C(C)(Cl)C(=C)C#CCl\n")));
    }

    @Test
    void test_C_1_2_O_H_3_8_falseFormat()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFuzzyFormula("C(1-2}OH[3-8]");
        maygen.run();
        Assertions.assertEquals(0, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        Assertions.assertEquals(0, maygen.getFuzzyCount());
    }
}

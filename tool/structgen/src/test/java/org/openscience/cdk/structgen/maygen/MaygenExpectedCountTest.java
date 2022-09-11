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


import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * This parameterized test-suite runs the checks of how many isomers we exepect
 * for each formula.
 */
class MaygenExpectedCountTest {

    static Stream<Arguments> data() {
        return Stream.of(
                Arguments.arguments("C3Cl2H4", 7),
                Arguments.arguments("CClH10", 0),
                Arguments.arguments("H3", 0),
                Arguments.arguments("N2", 1),
                Arguments.arguments("C4Cl2", 7),
                Arguments.arguments("H2", 1),
                Arguments.arguments("O13S7", 1980),
                Arguments.arguments("S27", 1),
                Arguments.arguments("O10S10", 4752),
                Arguments.arguments("O18", 1),
                Arguments.arguments("C2NO2H5", 84),
                Arguments.arguments("H2O", 1),
                Arguments.arguments("NH3", 1),
                Arguments.arguments("C6H6", 217),
                Arguments.arguments("C3O3H4", 152),
                Arguments.arguments("Cl2C5H4", 217),
                Arguments.arguments("C5H9ClO", 334),
                Arguments.arguments("C6OF2H12", 536),
                Arguments.arguments("C7H10", 575),
                Arguments.arguments("C6O2H12", 1313),
                Arguments.arguments("F2P3BrNO2H", 1958),
                Arguments.arguments("C6OH6", 2237),
                Arguments.arguments("C5H6BrN", 2325),
                Arguments.arguments("C6H7F2I", 3523),
                Arguments.arguments("C5F2O2H2", 7094),
                Arguments.arguments("C7OH10", 7166),
                Arguments.arguments("C4ClHF2O3", 7346),
                Arguments.arguments("C4O5H6", 8070),
                Arguments.arguments("C5ClHF2O2", 12400),
                Arguments.arguments("C5H10BrF2OP", 15009),
                Arguments.arguments("C9H12", 19983),
                Arguments.arguments("C10H16", 24938),
                Arguments.arguments("C6H10O2Br2", 24201),
                Arguments.arguments("C6H6ClOI", 30728),
                Arguments.arguments("C4H5O2Br2N", 41067),
                Arguments.arguments("C4H10NOSP", 52151),
                Arguments.arguments("C7O2H10", 54641),
                Arguments.arguments("P3O3NCl2", 665),
                Arguments.arguments("C5H5SI5", 2619),
                Arguments.arguments("C3O3NH5", 2644),
                Arguments.arguments("C5H9ClOS", 3763),
                Arguments.arguments("C3NO2SH7", 3838),
                Arguments.arguments("C4H8Cl3O2P", 9313),
                Arguments.arguments("C5H2F2SO", 13446),
                Arguments.arguments("C7H11ClS", 15093),
                Arguments.arguments("C4NO3H7", 18469),
                Arguments.arguments("C4H5O2F2P", 41067),
                Arguments.arguments("C3N3O2H7", 45626),
                Arguments.arguments("C5N3H9", 46125),
                Arguments.arguments("C3O6PH5", 51323),
                Arguments.arguments("C5H5POBr2", 62886),
                Arguments.arguments("C[1-2]H[3-8]", 3),
                Arguments.arguments("C[1-6]Cl2H[4-8]", 4141),
                Arguments.arguments("C(val=4)6H(val=1)6", 217) // user defined
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    void testExpectedCountMultithreaded(String formula, int expectedCount) throws CDKException, IOException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        if (formula.contains("val="))
            maygen.setSetElement(true);
        boolean fuzzy = formula.contains("[");
        if (fuzzy)
            maygen.setFuzzyFormula(formula);
        else
            maygen.setFormula(formula);
        maygen.setMultiThread(true);
        maygen.run();
        if (fuzzy)
            Assertions.assertEquals(expectedCount, maygen.getFuzzyCount());
        else
            Assertions.assertEquals(expectedCount, maygen.getCount());
    }

    @ParameterizedTest
    @MethodSource("data")
    @Tag("SlowTest")
    void testExpectedCount(String formula, int expectedCount) throws CDKException, IOException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        if (formula.contains("val="))
            maygen.setSetElement(true);
        boolean fuzzy = formula.contains("[");
        if (fuzzy)
            maygen.setFuzzyFormula(formula);
        else
            maygen.setFormula(formula);
        maygen.setMultiThread(false);
        maygen.run();
        if (fuzzy)
            Assertions.assertEquals(expectedCount, maygen.getFuzzyCount());
        else
            Assertions.assertEquals(expectedCount, maygen.getCount());
    }

}

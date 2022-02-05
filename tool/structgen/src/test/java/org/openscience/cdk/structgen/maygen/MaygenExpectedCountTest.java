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


import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.test.SlowTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

/**
 * This parameterized test-suite runs the checks of how many isomers we exepect
 * for each formula.
 */
@RunWith(Parameterized.class)
public class MaygenExpectedCountTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"C3Cl2H4", 7},
                {"CClH10", 0},
                {"H3", 0},
                {"N2", 1},
                {"C4Cl2", 7},
                {"H2", 1},
                {"O13S7", 1980},
                {"S27", 1},
                {"O10S10", 4752},
                {"O18", 1},
                {"C2NO2H5", 84},
                {"H2O", 1},
                {"NH3", 1},
                {"C6H6", 217},
                {"C3O3H4", 152},
                {"Cl2C5H4", 217},
                {"C5H9ClO", 334},
                {"C6OF2H12", 536},
                {"C7H10", 575},
                {"C6O2H12", 1313},
                {"F2P3BrNO2H", 1958},
                {"C6OH6", 2237},
                {"C5H6BrN", 2325},
                {"C6H7F2I", 3523},
                {"C5F2O2H2", 7094},
                {"C7OH10", 7166},
                {"C4ClHF2O3", 7346},
                {"C4O5H6", 8070},
                {"C5ClHF2O2", 12400},
                {"C5H10BrF2OP", 15009},
                {"C9H12", 19983},
                {"C10H16", 24938},
                {"C6H10O2Br2", 24201},
                {"C6H6ClOI", 30728},
                {"C4H5O2Br2N", 41067},
                {"C4H10NOSP", 52151},
                {"C7O2H10", 54641},
                {"P3O3NCl2", 665},
                {"C5H5SI5", 2619},
                {"C3O3NH5", 2644},
                {"C5H9ClOS", 3763},
                {"C3NO2SH7", 3838},
                {"C4H8Cl3O2P", 9313},
                {"C5H2F2SO", 13446},
                {"C7H11ClS", 15093},
                {"C4NO3H7", 18469},
                {"C4H5O2F2P", 41067},
                {"C3N3O2H7", 45626},
                {"C5N3H9", 46125},
                {"C3O6PH5", 51323},
                {"C5H5POBr2", 62886},
                {"C[1-2]H[3-8]", 3},
                {"C[1-6]Cl2H[4-8]", 4141},
                {"C(val=4)6H(val=1)6", 217} // user defined
        });
    }


    private final String formula;
    private final int expectedCount;

    public MaygenExpectedCountTest(String formula, int count) {
        this.formula = formula;
        this.expectedCount = count;
    }

    @Test
    public void testExpectedCountMultithreaded() throws CDKException, IOException, CloneNotSupportedException {
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
            Assert.assertEquals(expectedCount, maygen.getFuzzyCount());
        else
            Assert.assertEquals(expectedCount, maygen.getCount());
    }

    @Test
    @Category(SlowTest.class)
    public void testExpectedCount() throws CDKException, IOException, CloneNotSupportedException {
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
            Assert.assertEquals(expectedCount, maygen.getFuzzyCount());
        else
            Assert.assertEquals(expectedCount, maygen.getCount());
    }

}

/*
 MIT License

 Copyright (c) 2021 Mehmet Aziz Yirik

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/*
 * This is the junit test class for MAYGEN. Randomly selected 40 molecular formulas are used.
 * The number of generated structures are checked. The number of isomers are also tested with
 * MOLGEN algorithm. MAYGEN generates same number of isomers like MOLGEN.
 *
 * @author Mehmet Aziz Yirik
 */
package org.openscience.cdk.structgen.maygen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;

public class MaygenTest {

    @Test
    public void test_C3Cl2H4() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3Cl2H4");
        maygen.run();
        assertEquals(7, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
    }

    @Test
    public void test_CClH10() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("CClH10");
        maygen.run();
        assertEquals(0, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(0, maygen.getCount());
    }

    @Test
    public void test_H3() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("H3");
        maygen.run();
        assertEquals(0, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(0, maygen.getCount());
    }

    @Test
    public void test_N2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("N2");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_C4Cl2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4Cl2");
        maygen.run();
        assertEquals(7, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
    }

    @Test
    public void test_H2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("H2");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_O13S7() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("O13S7");
        maygen.run();
        assertEquals(1980, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1980, maygen.getCount());
    }

    @Test
    public void test_O10S10() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("O10S10");
        maygen.run();
        assertEquals(4752, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(4752, maygen.getCount());
    }

    @Test
    public void test_S27() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("S27");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_O18() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("O18");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_C2NO2H5() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C2NO2H5");
        maygen.run();
        assertEquals(84, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(84, maygen.getCount());
    }

    @Test
    public void test_H2O() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("H2O");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_NH3() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("NH3");
        maygen.run();
        assertEquals(1, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1, maygen.getCount());
    }

    @Test
    public void test_C6H6() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6H6");
        maygen.run();
        assertEquals(217, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(217, maygen.getCount());
    }

    @Test
    public void test_C3O3H4() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3O3H4");
        maygen.run();
        assertEquals(152, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(152, maygen.getCount());
    }

    @Test
    public void test_Cl2C5H4() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("Cl2C5H4");
        maygen.run();
        assertEquals(217, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(217, maygen.getCount());
    }

    @Test
    public void test_C5H9ClO() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H9ClO");
        maygen.run();
        assertEquals(334, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(334, maygen.getCount());
    }

    @Test
    public void test_C6OF2H12() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6OF2H12");
        maygen.run();
        assertEquals(536, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(536, maygen.getCount());
    }

    @Test
    public void test_C7H10() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C7H10");
        maygen.run();
        assertEquals(575, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(575, maygen.getCount());
    }

    @Test
    public void test_C6O2H12() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6O2H12");
        maygen.run();
        assertEquals(1313, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1313, maygen.getCount());
    }

    @Test
    public void test_F2P3BrNO2H() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("F2P3BrNO2H");
        maygen.run();
        assertEquals(1958, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1958, maygen.getCount());
    }

    @Test
    public void test_C6OH6() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6OH6");
        maygen.run();
        assertEquals(2237, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(2237, maygen.getCount());
    }

    @Test
    public void test_C5H6BrN() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H6BrN");
        maygen.run();
        assertEquals(2325, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(2325, maygen.getCount());
    }

    @Test
    public void test_C6H7F2I() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6H7F2I");
        maygen.run();
        assertEquals(3523, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3523, maygen.getCount());
    }

    @Test
    public void test_C5F2O2H2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5F2O2H2");
        maygen.run();
        assertEquals(7094, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7094, maygen.getCount());
    }

    @Test
    public void test_C7OH10() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C7OH10");
        maygen.run();
        assertEquals(7166, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7166, maygen.getCount());
    }

    @Test
    public void test_C4ClHF2O3() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4ClHF2O3");
        maygen.run();
        assertEquals(7346, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7346, maygen.getCount());
    }

    @Test
    public void test_C4O5H6() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4O5H6");
        maygen.run();
        assertEquals(8070, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(8070, maygen.getCount());
    }

    @Test
    public void test_C5ClHF2O2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5ClHF2O2");
        maygen.run();
        assertEquals(12400, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(12400, maygen.getCount());
    }

    @Test
    public void test_C5H10BrF2OP() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H10BrF2OP");
        maygen.run();
        assertEquals(15009, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(15009, maygen.getCount());
    }

    @Test
    public void test_C9H12() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C9H12");
        maygen.run();
        assertEquals(19983, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(19983, maygen.getCount());
    }

    @Test
    public void test_C6H10O2Br2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6H10O2Br2");
        maygen.run();
        assertEquals(24201, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(24201, maygen.getCount());
    }

    @Test
    public void test_C10H16() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C10H16");
        maygen.run();
        assertEquals(24938, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(24938, maygen.getCount());
    }

    @Test
    public void test_C6H6ClOI() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C6H6ClOI");
        maygen.run();
        assertEquals(30728, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(30728, maygen.getCount());
    }

    @Test
    public void test_C4H5O2Br2N() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4H5O2Br2N");
        maygen.run();
        assertEquals(41067, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(41067, maygen.getCount());
    }

    @Test
    public void test_C4H10NOSP() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4H10NOSP");
        maygen.run();
        assertEquals(52151, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(52151, maygen.getCount());
    }

    @Test
    public void test_C7O2H10() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C7O2H10");
        maygen.run();
        assertEquals(54641, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(54641, maygen.getCount());
    }

    @Test
    public void test_P3O3NCl2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("P3O3NCl2");
        maygen.run();
        assertEquals(665, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(665, maygen.getCount());
    }

    @Test
    public void test_C5H5SI5() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H5SI5");
        maygen.run();
        assertEquals(2619, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(2619, maygen.getCount());
    }

    @Test
    public void test_C3O3NH5() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3O3NH5");
        maygen.run();
        assertEquals(2644, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(2644, maygen.getCount());
    }

    @Test
    public void test_C5H9ClOS() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H9ClOS");
        maygen.run();
        assertEquals(3763, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3763, maygen.getCount());
    }

    @Test
    public void test_C3NO2SH7() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3NO2SH7");
        maygen.run();
        assertEquals(3838, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3838, maygen.getCount());
    }

    @Test
    public void test_C4H8Cl3O2P() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4H8Cl3O2P");
        maygen.run();
        assertEquals(9313, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(9313, maygen.getCount());
    }

    @Test
    public void test_C5H2F2SO() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H2F2SO");
        maygen.run();
        assertEquals(13446, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(13446, maygen.getCount());
    }

    @Test
    public void test_C7H11ClS() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C7H11ClS");
        maygen.run();
        assertEquals(15093, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(15093, maygen.getCount());
    }

    @Test
    public void test_C4NO3H7() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4NO3H7");
        maygen.run();
        assertEquals(18469, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(18469, maygen.getCount());
    }

    @Test
    public void test_C4H5O2F2P() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C4H5O2F2P");
        maygen.run();
        assertEquals(41067, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(41067, maygen.getCount());
    }

    @Test
    public void test_C3N3O2H7() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3N3O2H7");
        maygen.run();
        assertEquals(45626, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(45626, maygen.getCount());
    }

    @Test
    public void test_C5N3H9() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5N3H9");
        maygen.run();
        assertEquals(46125, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(46125, maygen.getCount());
    }

    @Test
    public void test_C3O6PH5() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3O6PH5");
        maygen.run();
        assertEquals(51323, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(51323, maygen.getCount());
    }

    @Test
    public void test_C5H5POBr2() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C5H5POBr2");
        maygen.run();
        assertEquals(62886, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(62886, maygen.getCount());
    }

    @Test
    public void test_C_1_6_Cl2_H_4_8()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C[1-6]Cl2H[4-8]");
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
    }

    @Test
    public void test_C_1_2_H_3_8() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C[1-2]H[3-8]");
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
    }

    @Test
    public void test_gettersAndSetters() {
        Maygen maygen = new Maygen();
        maygen.setWriteSDF(true);
        assertTrue(maygen.isWriteSDF());
        maygen.setWriteSMILES(true);
        assertTrue(maygen.isWriteSMILES());
        maygen.setPrintSDF(true);
        assertTrue(maygen.isPrintSDF());
        maygen.setPrintSMILES(true);
        assertTrue(maygen.isPrintSMILES());
        maygen.setCoordinates(true);
        assertTrue(maygen.isCoordinates());
        maygen.setTsvoutput(true);
        assertTrue(maygen.isTsvoutput());
        maygen.isMultiThread();
        maygen.getFormula();
        maygen.getFuzzyFormula();
        maygen.getTotal();
        maygen.getSymbols();
        maygen.getOccurrences();
        maygen.getOxygenSulfur();
        maygen.getTotalHydrogen();
        maygen.isOnSm();
        maygen.setFiledir(".");
        maygen.getFiledir();
        maygen.setVerbose(true);
        maygen.getVerbose();
        maygen.isSetElement();
    }

    @Test
    public void test_C3Cl2H4_writeSdfAndSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3Cl2H4");
        maygen.setWriteSDF(true);
        maygen.setWriteSMILES(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
    }

    @Test
    public void test_C3Cl2H4_sdfCoordinates()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("C3Cl2H4");
        maygen.setWriteSDF(true);
        maygen.setCoordinates(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(7, maygen.getCount());
    }

    @Test
    public void test_O13S7_writeSdfAndSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFormula("O13S7");
        maygen.setWriteSDF(true);
        maygen.setWriteSMILES(true);
        maygen.run();
        assertEquals(1980, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(1980, maygen.getCount());
    }

    @Test
    public void test_C_1_6_Cl2_H_4_8_writeSdfAndSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C[1-6]Cl2H[4-8]");
        maygen.setWriteSDF(true);
        maygen.setWriteSMILES(true);
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
    }

    @Test
    public void test_C_1_2_H_3_8_writeSdfAndSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C[1-2]H[3-8]");
        maygen.setWriteSDF(true);
        maygen.setWriteSMILES(true);
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
    }

    @Test
    public void test_C_1_2_O_H_3_8_writeSdfAndSmiles()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C[1-2]OH[3-8]");
        maygen.setWriteSDF(true);
        maygen.setWriteSMILES(true);
        maygen.run();
        assertEquals(6, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(6, maygen.getFuzzyCount());
    }

    @Test
    public void test_C_1_2_O_H_3_8_falseFormat()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setFuzzyFormula("C(1-2}OH[3-8]");
        maygen.run();
        assertEquals(0, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(0, maygen.getFuzzyCount());
    }

    @Test
    public void test_userDefined() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen();
        maygen.setSetElement(true);
        maygen.setFormula("C(val=4)6H(val=1)6");
        maygen.run();
        assertEquals(217, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(217, maygen.getCount());
    }
}

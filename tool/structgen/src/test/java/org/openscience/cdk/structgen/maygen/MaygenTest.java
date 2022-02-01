/*
 MIT License

 Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>

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

package org.openscience.cdk.structgen.maygen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * <p>
 * Unit test class for the MAYGEN class. Randomly selected molecular formulae are tested.
 * The number of generated structures are checked. The number of isomers are also tested with
 * MOLGEN algorithm. MAYGEN generates same number of isomers like MOLGEN.
 * </p>
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * 
 * @cdk.module structgen
 *
 */
public class MaygenTest {

    @Test
    public void test_C_1_6_Cl2_H_4_8()
            throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFuzzyFormula("C[1-6]Cl2H[4-8]");
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(4141, maygen.getFuzzyCount());
    }

    @Test
    public void test_C_1_2_H_3_8() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFuzzyFormula("C[1-2]H[3-8]");
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(3, maygen.getFuzzyCount());
    }

    @Test
    public void test_gettersAndSetters() {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
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
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setFuzzyFormula("C(1-2}OH[3-8]");
        maygen.run();
        assertEquals(0, maygen.getFuzzyCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(0, maygen.getFuzzyCount());
    }

    @Test
    public void test_userDefined() throws IOException, CDKException, CloneNotSupportedException {
        Maygen maygen = new Maygen(SilentChemObjectBuilder.getInstance());
        maygen.setSetElement(true);
        maygen.setFormula("C(val=4)6H(val=1)6");
        maygen.run();
        assertEquals(217, maygen.getCount());
        maygen.setMultiThread(true);
        maygen.run();
        assertEquals(217, maygen.getCount());
    }
}

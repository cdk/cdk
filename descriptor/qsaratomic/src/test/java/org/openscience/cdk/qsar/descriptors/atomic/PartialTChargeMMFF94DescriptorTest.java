/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.openscience.cdk.DefaultChemObjectBuilder;

import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 * @cdk.bug 1627763
 */
public class PartialTChargeMMFF94DescriptorTest extends AtomicDescriptorTest {

    private final double METHOD_ERROR = 0.16;

    private final double[] AtomInChIToMMFF94PartialCharges(String InChI) {

        InChIGeneratorFactory factory = null;

        try {
            factory = InChIGeneratorFactory.getInstance();
        } catch (CDKException e2) {

            e2.printStackTrace();
        }

        InChIToStructure parser = null;
        try {
            parser = factory.getInChIToStructure(InChI, DefaultChemObjectBuilder.getInstance());
        } catch (CDKException e1) {

            e1.printStackTrace();
        }

        IAtomContainer ac = parser.getAtomContainer();
        try {
            addExplicitHydrogens(ac);
        } catch (Exception e) {

            e.printStackTrace();
        }

        MMFF94PartialCharges mmff = new MMFF94PartialCharges();
        try {
            mmff.assignMMFF94PartialCharges(ac);
        } catch (Exception e) {

            e.printStackTrace();
        }

        double[] testResult = new double[ac.getAtomCount()];
        int i = 0;
        for (IAtom atom : ac.atoms()) {

            // System.out.println(atom.getAtomTypeName() + " " +
            // atom.getProperty("MMFF94charge").toString());
            testResult[i] = atom.getProperty("MMFF94charge", Double.class);
            i++;

        }

        return testResult;

    }

    /**
     * Constructor for the PartialTChargeMMFF94DescriptorTest object
     *
     * All values taken from table V of Merck Molecular Force Field. II. Thomas
     * A. Halgren DOI:
     * 10.1002/(SICI)1096-987X(199604)17:5/6<520::AID-JCC2>3.0.CO;2-W
     *
     */
    public PartialTChargeMMFF94DescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(PartialTChargeMMFF94Descriptor.class);
    }

    /**
     * A unit test suite for JUnit
     *
     * @return The test suite
     */
    /**
     * A unit test for JUnit with Methanol
     */
    @Test
    public void testPartialTotalChargeDescriptor_Methanol() throws ClassNotFoundException, CDKException,
            java.lang.Exception {

        double[] expectedResult = {0.28, -0.68, 0.0, 0.0, 0.0, 0.4};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH4O/c1-2/h2H,1H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Methylamine
     */
    @Test
    public void testPartialTotalChargeDescriptor_Methylamine() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.27, -0.99, 0.0, 0.0, 0.0, 0.36, 0.36};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH5N/c1-2/h2H2,1H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Acetonitrile
     */
    @Test
    public void testPartialTotalChargeDescriptor_Acetonitrile() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.2, 0.3571, -0.5571, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H3N/c1-2-3/h1H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Dimethyl Ether
     */
    @Test
    public void testPartialTotalChargeDescriptor_DimethylEther() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.28, -0.56, 0.28, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H6O/c1-3-2/h1-2H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Methanethiol
     */
    @Test
    public void testPartialTotalChargeDescriptor_Methanethiol() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.23, -0.41, 0.0, 0.0, 0.0, 0.18};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH4S/c1-2/h2H,1H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);
    }

    /**
     * A unit test for JUnit with Chloromethane
     */
    @Test
    public void testPartialTotalChargeDescriptor_Chloromethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.29, -0.29, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH3Cl/c1-2/h1H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Ethane
     */
    @Test
    public void testPartialTotalChargeDescriptor_Ethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H6/c1-2/h1-2H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Acetamide
     */
    @Test
    public void testPartialTotalChargeDescriptor_Acetamide() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.57, 0.569, -0.8, 0.061, 0.37, 0.37, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H5NO/c1-2(3)4/h1H3,(H2,3,4)");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Acetic Acid
     */
    @Test
    public void testPartialTotalChargeDescriptor_AceticAcid() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.061, 0.659, -0.65, -0.57, 0.0, 0.0, 0.0, 0.5};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Acetone
     */
    @Test
    public void testPartialTotalChargeDescriptor_Acetone() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.061, 0.448, -0.57, 0.061, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C3H6O/c1-3(2)4/h1-2H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Methyl Acetate
     */
    @Test
    public void testPartialTotalChargeDescriptor_MethylAcetate() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.57, 0.659, -0.43, 0.28, 0.061, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C3H6O2/c1-3(4)5-2/h1-2H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Benzene
     */
    @Test
    public void testPartialTotalChargeDescriptor_Benzene() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.15, -0.15, -0.15, -0.15, -0.15, -0.15, 0.15, 0.15, 0.15, 0.15, 0.15, 0.15};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C6H6/c1-2-4-6-5-3-1/h1-6H");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Pyridine
     */
    @Test
    public void testPartialTotalChargeDescriptor_Pyridine() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.15, -0.15, 0.16, -0.62, 0.16, -0.15, 0.15, 0.15, 0.15, 0.15, 0.15};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C5H5N/c1-2-4-6-5-3-1/h1-5H");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Aniline
     */
    @Test
    public void testPartialTotalChargeDescriptor_Aniline() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.15, -0.15, -0.15, 0.1, -0.15, -0.15, -0.9, 0.15, 0.15, 0.15, 0.15, 0.15, 0.4, 0.4};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C6H7N/c7-6-4-2-1-3-5-6/h1-5H,7H2");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Imidazole There may be a typo in the paper.
     * The MMF type of H should be 5, not 15.
     */
    @Test
    public void testPartialTotalChargeDescriptor_Imidazole() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.3016, 0.0772, -0.5653, 0.0365, 0.0332, 0.15, 0.15, 0.15, 0.27};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C3H4N2/c1-2-5-3-4-1/h1-3H,(H,4,5)");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Water
     */
    @Test
    public void testPartialTotalChargeDescriptor_Water() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.86, 0.43, 0.43};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/H2O/h1H2");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Acetate
     */
    @Test
    public void testPartialTotalChargeDescriptor_Acetate() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {-0.106, 0.906, -0.9, -0.9, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C2H4O2/c1-2(3)4/h1H3,(H,3,4)/p-1");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Methanaminium
     */
    @Test
    public void testPartialTotalChargeDescriptor_Methanaminium() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.503, -0.853, 0.0, 0.0, 0.0, 0.45, 0.45, 0.45};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH5N/c1-2/h2H2,1H3/p+1");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Imidazolium
     */
    @Test
    @Ignore
    //@Ignore("Issue with Atom type assignment in MMFF94:  Atom is unkown: Symbol:N does not MATCH AtomType. HoseCode:N-3+;CC(H,H,C,N/H&,H&/)")
    public void testPartialTotalChargeDescriptor_Imidazolium() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.2, 0.2, -0.7, 0.65, -0.7, 0.15, 0.15, 0.45, 0.15, 0.45};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C3H4N2/c1-2-5-3-4-1/h1-3H,(H,4,5)/p+1");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with 7-Aminoheptanoic acid
     */
    // Cannot generate standard InChI for the zwitterionic form of
    // 7-aminoheptanoic acid (as presented in paper)
    // Paper also appears to have left out the values for the (alkyl chain) Cs
    // and Hs

    @Test
    @Ignore
    public void testPartialTotalChargeDescriptor_7AminoheptanoicAcid() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.0, 0.0, 0.0, 0.503, -0.853, 0.0, -0.106, 0.906, -0.9, -0.9, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.45, 0.45, 0.45, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C7H15NO2/c8-6-4-2-1-3-5-7(9)10/h1-6,8H2,(H,9,10)");
        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

    /**
     * A unit test for JUnit with Ethoxyethane
     */
    @Test
    public void testPartialTotalChargeDescriptor_Ethoxyethane() throws ClassNotFoundException, CDKException,
            java.lang.Exception {
        double[] expectedResult = {0.0, 0.0, 0.28, 0.28, -0.56, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C4H10O/c1-3-5-4-2/h3-4H2,1-2H3");

        Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

    }

}

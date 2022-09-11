/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.Matchers.closeTo;

/**
 * TestSuite that runs XlogP tests.
 *
 * @cdk.module test-qsarmolecular
 */
class XLogPDescriptorTest extends MolecularDescriptorTest {

    XLogPDescriptorTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(XLogPDescriptor.class);
    }

    @Disabled
    @Test
    void testno688() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)c1[nH0]cccc1"); // xlogp training set molecule no688
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no688:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-1.69, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno1596() throws java.lang.Exception {
        // the xlogp program value is 0.44 because of paralleled donor pair correction factor
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Nc2ccc(S(=O)(=O)c1ccc(N)cc1)cc2"); // xlogp training set molecule no1596
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1596:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(0.86, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void testno367() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)C(N)CCCN"); // xlogp training set molecule no367
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no367:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-3.30, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno1837() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=P(N1CC1)(N2CC2)N3CC3"); // xlogp training set molecule no1837
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1837:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-1.19, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno87() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("c1cc2ccc3ccc4ccc5cccc6c(c1)c2c3c4c56"); // xlogp training set molecule no87
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no87:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(7.00, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno1782() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("S1C2N(C(=O)C2NC(=O)C(c2ccccc2)C(=O)O)C(C(=O)O)C1(C)C"); // xlogp training set molecule no30
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1782:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(1.84, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno30() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C(#Cc1ccccc1)c1ccccc1"); // xlogp training set molecule no30
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no30:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(4.62, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Disabled
    @Test
    void testno937() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("ClCC(O)C[nH0]1c([nH0]cc1[N+](=O)[O-])C"); // xlogp training set molecule no937
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no937:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(0.66, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno990() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("FC(F)(F)c1ccc(cc1)C(=O)N"); // xlogp training set molecule no990
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no990:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(1.834, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void testno1000() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Clc1cccc(c1)/C=C/[N+](=O)[O-]"); // xlogp training set molecule no1000
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no10000:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(2.809, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void testApirinBug1296383() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CC(=O)OC1=CC=CC=C1C(=O)O"); // aspirin
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("Aspirin:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(1.422, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno1429() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(OC)CNC(=O)c1ccc(N)cc1"); // xlogp training set molecule no1429
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1429:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(0.31, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void testno1274() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=[N+]([O-])c1ccc(cc1)CC(N)C(=O)O"); // xlogp training set molecule no1274
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1274:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-1.487, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void testno454() throws java.lang.Exception {
        //xlogp program gives a result of -0.89, because one N is classified as in ring and not as amid
        //if one takes a 5 or 7 ring than the program assignes amid ... strange
        //sometimes amid is O=C-N-C=O sometimes not...
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C1NC(=O)C=CN1C1OC(CO)C(O)C1O"); // xlogp training set molecule no454
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no454:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-2.11, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testno498() throws java.lang.Exception {
        //even here the amid assignment is very strange
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C1N(C)C=CC(=O)N1C"); // xlogp training set molecule no498
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no498:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(-0.59, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testAprindine() throws java.lang.Exception {
        //even here the amid assignment is very strange
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("CCN(CC)CCCN(C2Cc1ccccc1C2)c3ccccc3"); // xlogp training set molecule Aprindine
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("Aprindine:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(5.03, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void test1844() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        // SMILES is in octet-rule version, PubChem has normalized one
        IAtomContainer mol = sp.parseSmiles("Brc1cc(Cl)c(O[P+]([S-])(OC)OC)cc1Cl"); // xlogp training set molecule 1844
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1844:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(5.22, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    @Test
    void test1810() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("Clc1ccc2Sc3ccccc3N(CCCN3CCN(C)CC3)c2c1"); // xlogp training set molecule 1810
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1810:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(4.56, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 1.0); //at:  16
    }

    /**
     * @cdk.inchi InChI=1/C23H20N2O3S/c26-22-21(16-17-29(28)20-14-8-3-9-15-20)23(27)25(19-12-6-2-7-13-19)24(22)18-10-4-1-5-11-18/h1-15,21H,16-17H2
     */
    @Test
    void test1822() throws java.lang.Exception {
        Object[] params = {Boolean.TRUE, Boolean.FALSE};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[S+]([O-])(CCC1C(=O)N(N(c2ccccc2)C1=O)c1ccccc1)c1ccccc1"); // xlogp training set molecule 1822
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        //logger.debug("no1822:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        Assertions.assertEquals(2.36, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    @Test
    void testAromaticBenzene() throws java.lang.Exception {
        Object[] params = {false, true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=C1"); // benzene
        Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(), Cycles.all());
        aromaticity.apply(mol);
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(2.02, 0.01));
    }

    @Test
    void testNonAromaticBenzene() throws java.lang.Exception {
        Object[] params = {false, true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=C1"); // benzene
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(2.08, 0.01));
    }

    @Test
    void testPerceivedAromaticBenzene() throws java.lang.Exception {
        Object[] params = {true, true};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("C1=CC=CC=C1"); // benzene
        assertAtomTypesPerceived(mol);
        addExplicitHydrogens(mol);
        org.hamcrest.MatcherAssert.assertThat(((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), closeTo(2.02, 0.01));
    }
}

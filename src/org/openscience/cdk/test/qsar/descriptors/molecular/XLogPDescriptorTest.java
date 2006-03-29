/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */

public class XLogPDescriptorTest extends CDKTestCase {

    public XLogPDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(XLogPDescriptorTest.class);
    }

//	public void testXLogPDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
//	
//	// each test is for one or more atom types:
//	// 1. Test for cumarine
//	IDescriptor descriptor = new XLogPDescriptor();
//	Object[] params = {new Boolean(true)};
//	descriptor.setParameters(params);
//	SmilesParser sp = new SmilesParser();
//	Molecule mol = sp.parseSmiles("COc1ccccc1C(C3=C(O)c2ccccc2CC3=O)c5c(O)c4ccccc4oc5=O"); // a cumarine
//	HydrogenAdder hAdder = new HydrogenAdder();
//	hAdder.addExplicitHydrogensToSatisfyValency(mol);
//	System.out.println("Cumarine:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue());
//	//assertEquals(4.54, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
//	
//	
//}

    public void testno688() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=C(O)c1[nH0]cccc1"); // xlogp training set molecule no688
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no688:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-1.69, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1596() throws ClassNotFoundException, CDKException, java.lang.Exception {
        // the xlogp program value is 0.44 bevause of paralleled donor pair correction factor
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("Nc2ccc(S(=O)(=O)c1ccc(N)cc1)cc2"); // xlogp training set molecule no1596
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1596:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(0.86, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno367() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=C(O)C(N)CCCN"); // xlogp training set molecule no367
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no367:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-3.30, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1837() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=P(N1CC1)(N2CC2)N3CC3"); // xlogp training set molecule no1837
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1837:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-1.19, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno87() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("c1cc2ccc3ccc4ccc5cccc6c(c1)c2c3c4c56"); // xlogp training set molecule no87
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no87:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(7.00, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1782() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("S1C2N(C(=O)C2NC(=O)C(c2ccccc2)C(=O)O)C(C(=O)O)C1(C)C"); // xlogp training set molecule no30
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1782:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(1.84, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno30() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("C(#Cc1ccccc1)c1ccccc1"); // xlogp training set molecule no30
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no30:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(4.62, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno937() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("ClCC(O)C[nH0]1c([nH0]cc1[N+](=O)[O-])C"); // xlogp training set molecule no937
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no937:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(0.66, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno990() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("FC(F)(F)c1ccc(cc1)C(=O)N"); // xlogp training set molecule no990
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no990:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(1.834, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1000() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("Clc1cccc(c1)/C=C/[N+](=O)[O-]"); // xlogp training set molecule no1000
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no10000:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(2.809, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testApirinBug1296383() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CC(=O)OC1=CC=CC=C1C(=O)O"); // aspirin
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("Aspirin:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(1.422, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1429() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=C(OC)CNC(=O)c1ccc(N)cc1"); // xlogp training set molecule no1429
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1429:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(0.31, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno1274() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=[N+]([O-])c1ccc(cc1)CC(N)C(=O)O"); // xlogp training set molecule no1274
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1274:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-1.487, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno454() throws ClassNotFoundException, CDKException, java.lang.Exception {
        //xlogp program gives a result of -0.89, because one N is classified as in ring and not as amid
        //if one takes a 5 or 7 ring than the program assignes amid ... strange
        //sometimes amid is O=C-N-C=O sometimes not...
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=C1NC(=O)C=CN1C1OC(CO)C(O)C1O"); // xlogp training set molecule no454
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no454:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-2.11, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testno498() throws ClassNotFoundException, CDKException, java.lang.Exception {
        //even here the amid assignment is very strange
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("O=C1N(C)C=CC(=O)N1C"); // xlogp training set molecule no498
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no498:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(-0.59, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testAprindine() throws ClassNotFoundException, CDKException, java.lang.Exception {
        //even here the amid assignment is very strange
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CCN(CC)CCCN(C2Cc1ccccc1C2)c3ccccc3"); // xlogp training set molecule Aprindine
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("Aprindine:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(5.03, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void test1844() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("Brc1cc(Cl)c(O[P+]([S-])(OC)OC)cc1Cl"); // xlogp training set molecule 1844
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1844:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(5.22, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void test1810() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("Clc1ccc2Sc3ccccc3N(CCCN3CCN(C)CC3)c2c1"); // xlogp training set molecule 1810
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1810:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(4.56, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void test1822() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IDescriptor descriptor = new XLogPDescriptor();
        Object[] params = {new Boolean(true), new Boolean(false)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("[S+]([O-])(CCC1C(=O)N(N(c2ccccc2)C1=O)c1ccccc1)c1ccccc1"); // xlogp training set molecule 1822
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        //System.out.println("no1822:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue()+"\n");
        assertEquals(2.36, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }
}


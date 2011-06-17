/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 * 
 */

package org.openscience.cdk.similarity;

import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.fingerprint.ICountFingerprint;
import org.openscience.cdk.fingerprint.IntArrayCountFingerprint;
import org.openscience.cdk.fingerprint.LingoFingerprinter;
import org.openscience.cdk.fingerprint.SignatureFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @cdk.module test-fingerprint
 */
public class TanimotoTest extends CDKTestCase
{
	
	boolean standAlone = false;

	@Test public void testTanimoto1() throws java.lang.Exception
	{
	    IAtomContainer mol1 = MoleculeFactory.makeIndole();
	    IAtomContainer mol2 = MoleculeFactory.makePyrrole();
		Fingerprinter fingerprinter = new Fingerprinter();
		BitSet bs1 = fingerprinter.getBitFingerprint(mol1).asBitSet();
		BitSet bs2 = fingerprinter.getBitFingerprint(mol2).asBitSet();
		float tanimoto = Tanimoto.calculate(bs1, bs2);
		if (standAlone) System.out.println("Tanimoto: " + tanimoto);
		if (!standAlone) Assert.assertEquals(0.3939, tanimoto, 0.01);
	}
	@Test
    public void testTanimoto2() throws java.lang.Exception
	{
	    IAtomContainer mol1 = MoleculeFactory.makeIndole();
	    IAtomContainer mol2 = MoleculeFactory.makeIndole();
		Fingerprinter fingerprinter = new Fingerprinter();
		BitSet bs1 = fingerprinter.getBitFingerprint(mol1).asBitSet();
		BitSet bs2 = fingerprinter.getBitFingerprint(mol2).asBitSet();
		float tanimoto = Tanimoto.calculate(bs1, bs2);
		if (standAlone) System.out.println("Tanimoto: " + tanimoto);
		if (!standAlone) Assert.assertEquals(1.0, tanimoto, 0.001);
	}

    @Test public void testExactMatch() throws Exception {
        IAtomContainer mol1 = MoleculeFactory.makeIndole();
        IAtomContainer mol2 = MoleculeFactory.makeIndole();
        LingoFingerprinter fingerprinter = new LingoFingerprinter();
        Map<String, Integer> feat1 = fingerprinter.getRawFingerprint(mol1);
        Map<String, Integer> feat2 = fingerprinter.getRawFingerprint(mol2);
        float tanimoto = Tanimoto.calculate(feat1, feat2);
        Assert.assertEquals(1.0, tanimoto, 0.001);

    }

        @Test public void testTanimoto3() throws java.lang.Exception
        {
            double[] f1 = {1,2,3,4,5,6,7};
            double[] f2 = {1,2,3,4,5,6,7};
            float tanimoto = Tanimoto.calculate(f1,f2);
            if (standAlone) System.out.println("Tanimoto: " + tanimoto);
            if (!standAlone) Assert.assertEquals(1.0, tanimoto, 0.001);
        }

    	@Test public void visualTestR00258() throws java.lang.Exception
    	{
    		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    		String smiles1 = "O=C(O)CCC(=O)C(=O)O";
    		String smiles2 = "O=C(O)C(N)CCC(=O)O";
    		String smiles3 = "O=C(O)C(N)C";
    		String smiles4 = "CC(=O)C(=O)O";
    		IAtomContainer molecule1 = sp.parseSmiles(smiles1);
    		IAtomContainer molecule2 = sp.parseSmiles(smiles2);
    		IAtomContainer molecule3 = sp.parseSmiles(smiles3);
    		IAtomContainer molecule4 = sp.parseSmiles(smiles4);
    		Fingerprinter fingerprinter = new Fingerprinter(1024, 6);
    		BitSet bs1 = fingerprinter.getBitFingerprint(molecule1).asBitSet();
    		BitSet bs2 = fingerprinter.getBitFingerprint(molecule2).asBitSet();
    		BitSet bs3 = fingerprinter.getBitFingerprint(molecule3).asBitSet();
    		BitSet bs4 = fingerprinter.getBitFingerprint(molecule4).asBitSet();
    		float tanimoto1 = Tanimoto.calculate(bs1, bs2);
    		float tanimoto2 = Tanimoto.calculate(bs1, bs3);
    		float tanimoto3 = Tanimoto.calculate(bs1, bs4);
    		float tanimoto4 = Tanimoto.calculate(bs2, bs3);
    		float tanimoto5 = Tanimoto.calculate(bs2, bs4);
    		float tanimoto6 = Tanimoto.calculate(bs3, bs4);
    		//logger.debug("Similarity " + smiles1 + " vs. " + smiles2 + ": " + tanimoto1);
    		//logger.debug("Similarity " + smiles1 + " vs. " + smiles3 + ": " + tanimoto2);
    		//logger.debug("Similarity " + smiles1 + " vs. " + smiles4 + ": " + tanimoto3);
    		//logger.debug("Similarity " + smiles2 + " vs. " + smiles3 + ": " + tanimoto4);
    		//logger.debug("Similarity " + smiles2 + " vs. " + smiles4 + ": " + tanimoto5);
    		//logger.debug("Similarity " + smiles3 + " vs. " + smiles4 + ": " + tanimoto6);
    		System.out.println("Similarity 1 vs. 2: " + tanimoto1);
    		System.out.println("Similarity 1 vs. 3: " + tanimoto2);
    		System.out.println("Similarity 1 vs. 4: " + tanimoto3);
    		System.out.println("Similarity 2 vs. 3: " + tanimoto4);
    		System.out.println("Similarity 2 vs. 4: " + tanimoto5);
    		System.out.println("Similarity 3 vs. 4: " + tanimoto6);

    		
    		
    	}
    	
    /**
     * @throws Exception
     * @cdk.bug 3310138
     */
    @Test
    public void testRawTanimotoBetween0and1() throws Exception {
        SmilesParser smilesParser
            = new SmilesParser( SilentChemObjectBuilder.getInstance() );
        IAtomContainer mol1 = smilesParser.parseSmiles(
            "Cc1nc(C(=O)NC23CC4CC(CC(C4)C2)C3)c(C)n1C5CCCCC5");
        IAtomContainer mol2 = smilesParser.parseSmiles(
            "CS(=O)(=O)Nc1ccc(Cc2onc(n2)c3ccc(cc3)S(=O)(=O)Nc4ccc(CCNC[C@H](O)c5cccnc5)cc4)cc1");
	    	SignatureFingerprinter fingerprinter = new SignatureFingerprinter(0);
	    	Map<String, Integer> fp1 = fingerprinter.getRawFingerprint(mol1);
	    	Map<String, Integer> fp2 = fingerprinter.getRawFingerprint(mol2);
	    	float tanimoto = Tanimoto.calculate(fp1, fp2);
	    	Assert.assertTrue( "Tanimoto expected to be between 0 and 1, was:" + tanimoto, 
	    			           tanimoto > 0 && tanimoto < 1 );
    }

    	
    @Test public void testICountFingerprintComparison() throws Exception {
        Molecule mol1 = MoleculeFactory.makeIndole();
        Molecule mol2 = MoleculeFactory.makeIndole();
        SignatureFingerprinter fingerprinter = new SignatureFingerprinter();
        ICountFingerprint fp1 = fingerprinter.getCountFingerprint(mol1);
        ICountFingerprint fp2 = fingerprinter.getCountFingerprint(mol2);
        float tanimoto = Tanimoto.calculate(fp1, fp2);
        Assert.assertEquals(1.0, tanimoto, 0.001);

    }
    
    @Test public void compareCountFingerprintAndRawFingerprintTanimoto() throws CDKException {
    		Molecule mol1 = MoleculeFactory.make123Triazole();
    		Molecule mol2 = MoleculeFactory.makeImidazole();
    		SignatureFingerprinter fingerprinter = new SignatureFingerprinter(1);
    		ICountFingerprint countFp1 = fingerprinter.getCountFingerprint(mol1);
    		ICountFingerprint countFp2 = fingerprinter.getCountFingerprint(mol2);
    		Map<String, Integer> feat1 = fingerprinter.getRawFingerprint(mol1);
    		Map<String, Integer> feat2 = fingerprinter.getRawFingerprint(mol2);
    		float rawTanimoto = Tanimoto.calculate(feat1, feat2);
    		double countTanimoto = Tanimoto.calculate(countFp1, countFp2);
    		Assert.assertEquals(rawTanimoto, countTanimoto, 0.001);
    }
    
    @Test
    public void testCountMethod1and2() {
		IntArrayCountFingerprint fp1 = new IntArrayCountFingerprint(
                                           new HashMap<String, Integer>() {{
             	                               put("A", 3);
                                           }}
            		                       );
		IntArrayCountFingerprint fp2 = new IntArrayCountFingerprint(
                                           new HashMap<String, Integer>() {{
             	                               put("A", 4);
                                           }}
                                       );
		Assert.assertEquals(0.923, Tanimoto.method1(fp1, fp2), 0.001 );
		Assert.assertEquals(0.75, Tanimoto.method2(fp1, fp2), 0.001 );
    }
}
/*
 * $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CKD) project
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

package org.openscience.cdk.test.similarity;

import java.util.BitSet;
import org.openscience.cdk.similarity.Tanimoto;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test
 */
public class TanimotoTest extends CDKTestCase
{
	
	boolean standAlone = false;
	private static LoggingTool logger = new LoggingTool(TanimotoTest.class);
	
	public TanimotoTest(String name)
	{
		super(name);
	}

	public static Test suite() {
		return new TestSuite(TanimotoTest.class);
	}

	public void testTanimoto1() throws java.lang.Exception
	{
		Molecule mol1 = MoleculeFactory.makeIndole();
		Molecule mol2 = MoleculeFactory.makePyrrole();
		Fingerprinter fingerprinter = new Fingerprinter();
		BitSet bs1 = fingerprinter.getFingerprint(mol1);
		BitSet bs2 = fingerprinter.getFingerprint(mol2);
		float tanimoto = Tanimoto.calculate(bs1, bs2);
		if (standAlone) System.out.println("Tanimoto: " + tanimoto);
		if (!standAlone) assertEquals(tanimoto, 0.59, 0.01);
	}
	public void testTanimoto2() throws java.lang.Exception
	{
		Molecule mol1 = MoleculeFactory.makeIndole();
		Molecule mol2 = MoleculeFactory.makeIndole();
		Fingerprinter fingerprinter = new Fingerprinter();
		BitSet bs1 = fingerprinter.getFingerprint(mol1);
		BitSet bs2 = fingerprinter.getFingerprint(mol2);
		float tanimoto = Tanimoto.calculate(bs1, bs2);
		if (standAlone) System.out.println("Tanimoto: " + tanimoto);
		if (!standAlone) assertEquals(tanimoto, 1.0, 0.001);
	}
	
        public void testTanimoto3() throws java.lang.Exception
        {
            double[] f1 = {1,2,3,4,5,6,7};
            double[] f2 = {1,2,3,4,5,6,7};
            float tanimoto = Tanimoto.calculate(f1,f2);
            if (standAlone) System.out.println("Tanimoto: " + tanimoto);
            if (!standAlone) assertEquals(tanimoto, 1.0, 0.001);
        }


	public static void main(String[] args)
	{
		try{
			TanimotoTest tt = new TanimotoTest("TanimotoTest");
			tt.standAlone = true;
			tt.testTanimoto1();
			tt.testTanimoto2();
			tt.testTanimoto3();
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
}


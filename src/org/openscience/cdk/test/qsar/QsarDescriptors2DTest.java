/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test.qsar;

import org.openscience.cdk.qsar.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.config.IsotopeFactory;
import java.lang.Math;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;

/**
 * @cdk.module test
 */
public class QsarDescriptors2DTest extends TestCase {
	
	boolean standAlone = false;
	public  QsarDescriptors2DTest(){}
	public static Test suite()
	{
		return new TestSuite(QsarDescriptors2DTest.class);
	}
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	public void testgetProtonPartialCharges() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult = {0.05783,0.05783,0.05783}; // 0.05783
		//try {
			QsarDescriptors2D qd2d = new QsarDescriptors2D();
			SmilesParser sp = new SmilesParser();
			AtomContainer mol = sp.parseSmiles("CF");
			double [] protoncharge = qd2d.getProtonPartialChargesAt(mol, 0);
			System.err.println(testResult[0]+" "+ protoncharge);
			assertEquals(testResult[0], protoncharge[0], 0.00001);			
		/*} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}*/
	}
	/*
	public void testgetXAtomsOrBondsCount() {
		try {
			QsarDescriptors2D qd2d = new QsarDescriptors2D();
			SmilesParser sp = new SmilesParser();
			AtomContainer mol = sp.parseSmiles("CCC(C)CCC2CCC3CCC(C1CCCCC1)CC23");
			boolean includeTerminals = false; 
			assertEquals(5, qd2d.getRotatableBondsCount(mol, includeTerminals));			
		}
		 catch (Exception exception) {
			 fail(exception.getMessage());
		 }
	}
	*/
}

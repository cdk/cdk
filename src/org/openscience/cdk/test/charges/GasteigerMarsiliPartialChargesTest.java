/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.charges;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 *  Description of the Class
 *
 * @cdk.module test-extra
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class GasteigerMarsiliPartialChargesTest extends CDKTestCase {
	
	boolean standAlone = false;
	
	/**
	 *  Constructor for the GasteigerMarsiliPartialChargesTest
	 *@param  name  Description of the Parameter
	 */
	public  GasteigerMarsiliPartialChargesTest(){}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(GasteigerMarsiliPartialChargesTest.class);
	}
	
	/**
	 *  Sets the standAlone attribute 
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	
	/**
	 *  A unit test for JUnit with methylenfluoride
	 */
	public void testAssignGasteigerMarsiliPartialCharges(){
		double [] testResult={0.07915,-0.25264,0.05783,0.05783,0.05783};
		GasteigerMarsiliPartialCharges peoe=new GasteigerMarsiliPartialCharges();
		HydrogenAdder hAdder = new HydrogenAdder();
		try{
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
			IMolecule mol = sp.parseSmiles("CF");
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
			peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
			for (int i=0;i<mol.getAtomCount();i++){
				//logger.debug("Charge for atom:"+i+" S:"+mol.getAtomAt(i).getSymbol()+" Charge:"+mol.getAtomAt(i).getCharge());
				assertEquals(testResult[i],mol.getAtom(i).getCharge(),0.01);
			}
			
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}
}

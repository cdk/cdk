/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.charges;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.NewCDKTestCase;

/**
 *  Description of the Class
 *
 * @cdk.module test-charges
 *
 *@author     chhoppe
 *@cdk.created    2004-11-04
 */
public class PolarizabilityTest extends NewCDKTestCase {
	
	private boolean standAlone = false;
	

	
	/**
	 *  Sets the standAlone attribute
	 *
	 *@param  arg  The new standAlone value
	 */
    public void setStandAlone(boolean arg)
	{
		standAlone = arg;
	}
	
	
	/**
	 *  A unit test for JUnit with n,n-dimethyl ethylendiamine
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability() throws Exception {
		double [] testResult={4.73,6.92};
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("NCCN(C)(C)");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(0),100, true);
		Assert.assertEquals(testResult[0],result,0.01);
		result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(3),100, true);
		Assert.assertEquals(testResult[1],result,0.01);
	}
	
	
	/**
	 *  A unit test for JUnit with methane
	 */
    @Test
    public void testCalculateKJMeanMolecularPolarizability() throws Exception {
		double testResult=2.61;
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C");
		double result=pol.calculateKJMeanMolecularPolarizability(mol);
		Assert.assertEquals(testResult,result,0.01);
	}

	/**
	 *  A unit test for JUnit with Ethyl chloride
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Ethyl_chloride() throws Exception {
		double testResult = 4.62; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCCl");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(2),100, true);
		Assert.assertEquals(testResult,result,0.01);
	}
	
	/**
	 *  A unit test for JUnit with Allyl bromide
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Allyl_bromide() throws Exception {
		double testResult = 6.17; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCBr");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(3),100, true);
		Assert.assertEquals(testResult,result,0.01);
	}
	
	/**
	 *  A unit test for JUnit with Isopentyl iodide
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Isopentyl_iodide() throws Exception {
		double testResult = 8.69; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C(C)(C)CCI");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(5),100, true);
		Assert.assertEquals(testResult,result,0.01);
	}
	
	/**
	 *  A unit test for JUnit with Ethoxy ethane
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Ethoxy_ethane() throws Exception {
		double testResult = 5.21; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CCOCC");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(2),100, true);
		Assert.assertEquals(testResult,result,0.01);
	}
	
	/**
	 *  A unit test for JUnit with Ethanolamine
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Ethanolamine() throws Exception {
		double [] testResult={4.26,3.60}; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("NCCO");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(3),100, true);
		Assert.assertEquals(testResult[1],result,0.01);
		result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(0),100, true);
		Assert.assertEquals(testResult[0],result,0.01);
	}

	/**
	 *  A unit test for JUnit with Allyl mercaptan
	 */
    @Test
    public void testcalculateGHEffectiveAtomPolarizability_Allyl_mercaptan() throws Exception {
		double testResult = 6.25; /* from thesis Wolfgang Hanebeck, TUM*/
		Polarizability pol=new Polarizability();
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("C=CCS");
		double result=pol.calculateGHEffectiveAtomPolarizability(mol,mol.getAtom(3),100, true);
		Assert.assertEquals(testResult,result,0.01);
	}
}

/* $Revision: 8975 $ $Author: egonw $ $Date: 2007-09-26 21:14:28 +0200 (Wed, 26 Sep 2007) $
 * 
 *  Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.formula;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaManipulator;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the MolecularFormulaManipulator.
 *
 * @cdk.module test-formula
 */
public class MolecularFormulaManipulatorTest extends NewCDKTestCase {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	private IsotopeFactory ifac;

	/**
	 *  Constructor for the MolecularFormulaManipulatorTest object.
	 *
	 */
	public MolecularFormulaManipulatorTest(){
		
		super();
		try {
			ifac = IsotopeFactory.getInstance(builder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetAtomCount_IMolecularFormula() {
    	
        IMolecularFormula formula = new MolecularFormula();
        formula.addIsotope(builder.newIsotope("C") );
        formula.addIsotope( builder.newIsotope("H") ,3);

        Assert.assertEquals(2, formula.getIsotopeCount());
        
        Assert.assertEquals(4, MolecularFormulaManipulator.getAtomCount(formula));
    }
    

    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetElementCount_IMolecularFormula_IElement() {
        IMolecularFormula formula = new MolecularFormula();
    	IIsotope carb = builder.newIsotope("C");
    	IIsotope flu = builder.newIsotope("F");
    	IIsotope h1 = builder.newIsotope("H");
    	IIsotope h2 = builder.newIsotope("H");
    	h2.setExactMass(2.014101778);
    	formula.addIsotope( carb ,2);
    	formula.addIsotope( flu );
    	formula.addIsotope( h1 ,3);
    	formula.addIsotope( h2 ,4);
    	
        Assert.assertEquals(10, MolecularFormulaManipulator.getAtomCount(formula));
        Assert.assertEquals(4, formula.getIsotopeCount());
        Assert.assertEquals(3, formula.getIsotopeCount(h1));
        Assert.assertEquals(4, formula.getIsotopeCount(h2));
        
        Assert.assertEquals(2, MolecularFormulaManipulator.getElementCount(formula,builder.newElement(carb)));
        Assert.assertEquals(1, MolecularFormulaManipulator.getElementCount(formula,builder.newElement(flu)));
        Assert.assertEquals(7, MolecularFormulaManipulator.getElementCount(formula,builder.newElement(h1)));
    }
    /**
	 * A unit test suite for JUnit. Not null.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetIsotopes_IMolecularFormula_IElement() {
		IMolecularFormula formula = new MolecularFormula();
    	IIsotope carb = builder.newIsotope("C");
    	IIsotope flu = builder.newIsotope("F");
    	IIsotope h1 = builder.newIsotope("H");
    	IIsotope h2 = builder.newIsotope("H");
    	h2.setExactMass(2.014101778);
    	formula.addIsotope( carb ,1);
    	formula.addIsotope( flu );
    	formula.addIsotope( h1 ,1);
    	formula.addIsotope( h2 ,2);
    	
    	List<IIsotope> isotopes = MolecularFormulaManipulator.getIsotopes(formula, builder.newElement("H"));
		Assert.assertEquals(2,isotopes.size());
	}
	/**
	 * A unit test suite for JUnit. Not null.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testContainsElement_IMolecularFormula_IElement() {
		IMolecularFormula formula = new MolecularFormula();
    	IIsotope carb = builder.newIsotope("C");
    	IIsotope flu = builder.newIsotope("F");
    	IIsotope h1 = builder.newIsotope("H");
    	IIsotope h2 = builder.newIsotope("H");
    	h2.setExactMass(2.014101778);
    	formula.addIsotope( carb ,1);
    	formula.addIsotope( flu );
    	formula.addIsotope( h1 ,1);
    	formula.addIsotope( h2 ,2);
    	
		Assert.assertTrue(MolecularFormulaManipulator.containsElement(formula, builder.newElement("C")));
		Assert.assertTrue(MolecularFormulaManipulator.containsElement(formula, builder.newElement("H")));
		Assert.assertTrue(MolecularFormulaManipulator.containsElement(formula, builder.newElement("F")));
	}
	
    /**
	 * A unit test suite for JUnit. Not null.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetString_IMolecularFormula_Empty() {
		String stringMF = MolecularFormulaManipulator.getString(new MolecularFormula());
		Assert.assertNotNull(stringMF);
		Assert.assertEquals("",stringMF);
	}
    /**
	 * A unit test suite for JUnit. Not null.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetString__String_IMolecularFormula() 	{
		Assert.assertNotNull(MolecularFormulaManipulator.getMolecularFormula("C10H16", new MolecularFormula()));
		Assert.assertNotNull(MolecularFormulaManipulator.getMolecularFormula("C10H16"));
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetString_IMolecularFormula()	{
		IMolecularFormula mf1 = new MolecularFormula();
		mf1.addIsotope(builder.newIsotope("C"),10);
		mf1.addIsotope(builder.newIsotope("H"),16);
		
		Assert.assertEquals("C10H16", MolecularFormulaManipulator.getString(mf1));
		
		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newAtom("H"),16);
		mf2.addIsotope(builder.newAtom("C"),10);
		
		Assert.assertEquals("C10H16", MolecularFormulaManipulator.getString(mf2));
		
		Assert.assertEquals(MolecularFormulaManipulator.getString(mf2), MolecularFormulaManipulator.getString(mf1));
		
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetMolecularFormula_String()	{
		IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula("C10H16");
		
		Assert.assertEquals(26, MolecularFormulaManipulator.getAtomCount(molecularFormula));
		Assert.assertEquals(2,  molecularFormula.getIsotopeCount());
		
	}
	
	
	/**
	 * A unit test suite for JUnit. 
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetMolecularFormula_String_IMolecularFormula()	{

		IMolecularFormula mf1 = new MolecularFormula();
		mf1.addIsotope(builder.newIsotope("C"),10);
		mf1.addIsotope(builder.newIsotope("H"),16);
		
		Assert.assertEquals(26, MolecularFormulaManipulator.getAtomCount(mf1));
		Assert.assertEquals(2, mf1.getIsotopeCount());
		
		IMolecularFormula mf2 = MolecularFormulaManipulator.getMolecularFormula("C11H17",mf1);
		
		
		Assert.assertEquals(54, MolecularFormulaManipulator.getAtomCount(mf2));
		Assert.assertEquals(2, mf2.getIsotopeCount());
	}
	
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testRemoveElement_IMolecularFormula_IElement() {
		IMolecularFormula formula = new MolecularFormula();
		formula.addIsotope(builder.newIsotope("C"),1);
		IIsotope fl = builder.newIsotope("F");
		IIsotope hy2 = builder.newIsotope("H");
		IIsotope hy1 = builder.newIsotope("H");
		hy2.setExactMass(2.014101778);
		formula.addIsotope(fl,1);
		formula.addIsotope(hy1,2);
		formula.addIsotope(hy2,1);
        
		Assert.assertEquals(4, formula.getIsotopeCount());
        
		formula = MolecularFormulaManipulator.removeElement(formula,builder.newElement("F"));

		Assert.assertEquals(3, formula.getIsotopeCount());
        Assert.assertEquals(4, MolecularFormulaManipulator.getAtomCount(formula));
        

        formula = MolecularFormulaManipulator.removeElement(formula,builder.newElement("H"));
        
        Assert.assertEquals(1, MolecularFormulaManipulator.getAtomCount(formula));
        Assert.assertEquals(1, formula.getIsotopeCount());
        
    }
	/**
     * A unit test suite for JUnit. Test total Exact Mass.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test 
    public void testGetTotalExactMass_IMolecularFormula() throws IOException, ClassNotFoundException, CDKException{

		IMolecularFormula formula = new MolecularFormula();
		IIsotope carb = builder.newIsotope("C");
    	carb.setExactMass(12.00);
    	IIsotope cl = builder.newIsotope("Cl");
        cl.setExactMass(34.96885268);
        
        formula.addIsotope(carb);
        formula.addIsotope(cl);
    	
    	double totalExactMass = MolecularFormulaManipulator.getTotalExactMass(formula);

        Assert.assertEquals(46.96885268,totalExactMass,0.000001);
    }
    
    /**
     * A unit test suite for JUnit. Test total natural abundance.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test 
    public void testGetTotalNaturalAbundance_IMolecularFormula() throws IOException, ClassNotFoundException, CDKException{

		IMolecularFormula formula = new MolecularFormula();
		IIsotope carb = builder.newIsotope("C");
    	carb.setNaturalAbundance(98.93);
    	IIsotope cl = builder.newIsotope("Cl");
        cl.setNaturalAbundance(75.78);
        formula.addIsotope(carb);
        formula.addIsotope(cl);
    	
        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula);

        Assert.assertEquals(0.74969154,totalAbudance,0.000001);
    }
    
    /**
     * A unit test suite for JUnit. Test total natural abundance.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    @Test 
    public void testGetTotalNaturalAbundance_IMolecularFormula2() throws IOException, ClassNotFoundException, CDKException{

		IMolecularFormula formula1 = new MolecularFormula();
		IIsotope br1 = builder.newIsotope("Br");
    	br1.setNaturalAbundance(49.31);
    	IIsotope br2 = builder.newIsotope("Br");
        br2.setNaturalAbundance(50.69);
        formula1.addIsotope(br1);
        formula1.addIsotope(br2);
    	

        Assert.assertEquals(2,formula1.getIsotopeCount(),0.000001);
        double totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula1);

        Assert.assertEquals(0.24995235,totalAbudance,0.000001);
        
        IMolecularFormula formula2 = new MolecularFormula();
		br1 = builder.newIsotope("Br");
    	br1.setNaturalAbundance(50.69);
    	br2 = builder.newIsotope("Br");
        br2.setNaturalAbundance(50.69);
        formula2.addIsotope(br1);
        formula2.addIsotope(br2);

        Assert.assertEquals(1,formula2.getIsotopeCount(),0.000001);
        totalAbudance = MolecularFormulaManipulator.getTotalNaturalAbundance(formula2);

        Assert.assertEquals(0.25694761,totalAbudance,0.000001);
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetDBE_IMolecularFormula()throws IOException, ClassNotFoundException, CDKException{
    	IMolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),10);
    	formula.addIsotope(builder.newIsotope("H"),22);
		
    	Assert.assertEquals(0.0,MolecularFormulaManipulator.getDBE(formula));
    	
    	formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),10);
    	formula.addIsotope(builder.newIsotope("H"),16);
		
    	Assert.assertEquals(3.0,MolecularFormulaManipulator.getDBE(formula));
    	

    	formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),10);
    	formula.addIsotope(builder.newIsotope("H"),16);
    	formula.addIsotope(builder.newIsotope("O"));
		
    	Assert.assertEquals(3.0,MolecularFormulaManipulator.getDBE(formula));
    	

    	formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),10);
    	formula.addIsotope(builder.newIsotope("H"),19);
    	formula.addIsotope(builder.newIsotope("N"));

    	Assert.assertEquals(2.0,MolecularFormulaManipulator.getDBE(formula));

    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetHTML_IMolecularFormula() {
    	MolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),8);
    	formula.addIsotope(builder.newIsotope("H"),10);
    	formula.addIsotope(builder.newIsotope("Cl"),2);
    	formula.addIsotope(builder.newIsotope("O"),2);
    	
        Assert.assertEquals("C<sub>8</sub>H<sub>10</sub>O<sub>2</sub>Cl<sub>2</sub>", MolecularFormulaManipulator.getHTML(formula));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetHTML_IMolecularFormula_boolean_boolean() {
    	MolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),10);
    	
        Assert.assertEquals("C<sub>10</sub>", MolecularFormulaManipulator.getHTML(formula,true,false));
        formula.setCharge(new Double(1));
        Assert.assertEquals("C<sub>10</sub><sup>1.0+</sup>", MolecularFormulaManipulator.getHTML(formula,true,false));
        formula.setCharge(formula.getCharge() - 2);
        Assert.assertEquals("C<sub>10</sub><sup>1.0-</sup>", MolecularFormulaManipulator.getHTML(formula,true,false));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetHTML_IMolecularFormulaWhitIsotope() {
    	MolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(ifac.getMajorIsotope("C"),2);
    	formula.addIsotope(ifac.getMajorIsotope("H"),6);
        Assert.assertEquals("<sup>12</sup>C<sub>2</sub><sup>1</sup>H<sub>6</sub>", MolecularFormulaManipulator.getHTML(formula,false,true));
    }

    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testGetHTML_IMolecularFormulaWhitIsotopeAndCharge() {
    	MolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(ifac.getMajorIsotope("C"),2);
    	formula.addIsotope(ifac.getMajorIsotope("H"),6);
    	formula.setCharge(1.0);
        Assert.assertEquals("<sup>12</sup>C<sub>2</sub><sup>1</sup>H<sub>6</sub><sup>1.0+</sup>", MolecularFormulaManipulator.getHTML(formula,true,true));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetMolecularFormula_IAtomContainer(){
		IAtomContainer ac = builder.newAtomContainer();
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));

		IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac);		

		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),2);
		mf2.addIsotope(builder.newIsotope("H"),4);
		

		Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf2), MolecularFormulaManipulator.getAtomCount(mf1));
		Assert.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
		IElement elemC = builder.newElement("C");
		IElement elemH = builder.newElement("H");
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemC)), mf1.getIsotopeCount(builder.newIsotope(elemC)));
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemH)), mf1.getIsotopeCount(builder.newIsotope(elemH)));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemC), MolecularFormulaManipulator.getElementCount(mf1,elemC));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemH), MolecularFormulaManipulator.getElementCount(mf1,elemH));
				
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetMolecularFormula_IAtomContainer_IMolecularFormula(){
		IAtomContainer ac = builder.newAtomContainer();
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));

		IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac,new MolecularFormula());		


		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),2);
		mf2.addIsotope(builder.newIsotope("H"),4);

		Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf2), MolecularFormulaManipulator.getAtomCount(mf1));
		Assert.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
		IElement elemC = builder.newElement("C");
		IElement elemH = builder.newElement("H");
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemC)), mf1.getIsotopeCount(builder.newIsotope(elemC)));
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemH)), mf1.getIsotopeCount(builder.newIsotope(elemH)));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemC), MolecularFormulaManipulator.getElementCount(mf1,elemC));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemH), MolecularFormulaManipulator.getElementCount(mf1,elemH));
						
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetMolecularFormula_IAtomContainerIMolecularFormula_2(){
		IAtomContainer ac = builder.newAtomContainer();
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));

		IMolecularFormula mf0 = new MolecularFormula();
		mf0.addIsotope(builder.newIsotope("C"),2);
		mf0.addIsotope(builder.newIsotope("H"),5);
		
		IMolecularFormula mf1 = MolecularFormulaManipulator.getMolecularFormula(ac,mf0);		

		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),4);
		mf2.addIsotope(builder.newIsotope("H"),9);
		
		Assert.assertEquals(MolecularFormulaManipulator.getAtomCount(mf2), MolecularFormulaManipulator.getAtomCount(mf1));
		Assert.assertEquals(mf2.getIsotopeCount(), mf1.getIsotopeCount());
		IElement elemC = builder.newElement("C");
		IElement elemH = builder.newElement("H");
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemC)), mf1.getIsotopeCount(builder.newIsotope(elemC)));
		Assert.assertEquals(mf2.getIsotopeCount(builder.newIsotope(elemH)), mf1.getIsotopeCount(builder.newIsotope(elemH)));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemC), MolecularFormulaManipulator.getElementCount(mf1,elemC));
		Assert.assertEquals(MolecularFormulaManipulator.getElementCount(mf2, elemH), MolecularFormulaManipulator.getElementCount(mf1,elemH));
				
	}
	

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetAtomContainer_IMolecularFormula(){
		

		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),2);
		mf2.addIsotope(builder.newIsotope("H"),4);
		
		IAtomContainer ac = MolecularFormulaManipulator.getAtomContainer(mf2);		


		Assert.assertEquals(6, ac.getAtomCount());
				
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testGetAtomContainer_IMolecularFormula_IAtomContainer(){

		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),2);
		mf2.addIsotope(builder.newIsotope("H"),4);
		
		IAtomContainer ac = MolecularFormulaManipulator.getAtomContainer(mf2,builder.newAtomContainer());		


		Assert.assertEquals(6, ac.getAtomCount());
				
	}
	
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testMolecularFormulaIAtomContainer_to_IAtomContainer2(){
		IAtomContainer ac = builder.newAtomContainer();
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("C"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));
		ac.addAtom(builder.newAtom("H"));

		IMolecularFormula mf2 = new MolecularFormula();
		mf2.addIsotope(builder.newIsotope("C"),2);
		mf2.addIsotope(builder.newIsotope("H"),4);
		
		IAtomContainer ac2 = MolecularFormulaManipulator.getAtomContainer(mf2,builder.newAtomContainer());		


		Assert.assertEquals(ac2.getAtomCount(), ac2.getAtomCount());
		Assert.assertEquals(ac2.getAtom(0).getSymbol(),ac2.getAtom(0).getSymbol());
		Assert.assertEquals(ac2.getAtom(5).getSymbol(),ac2.getAtom(5).getSymbol());
				
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testElements_IMolecularFormula(){
		
		IMolecularFormula formula = new MolecularFormula();
    	formula.addIsotope(builder.newIsotope("C"),1);
    	formula.addIsotope(builder.newIsotope("H"),2);
    	
    	IIsotope br1 = builder.newIsotope("Br");
    	br1.setNaturalAbundance(50.69);
        formula.addIsotope(br1);
    	IIsotope br2 = builder.newIsotope("Br");
        br2.setNaturalAbundance(50.69);
        formula.addIsotope(br2);
        
        List<IElement> elements = MolecularFormulaManipulator.elements(formula);
        
        Assert.assertEquals(5, MolecularFormulaManipulator.getAtomCount(formula));
        Assert.assertEquals(3, elements.size());
	}
	
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testCompare_Charge(){
		
		IMolecularFormula formula1 = new MolecularFormula();
    	formula1.addIsotope(builder.newIsotope("C"),1);
    	formula1.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula2 = new MolecularFormula();
    	formula2.addIsotope(builder.newIsotope("C"),1);
    	formula2.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula3 = new MolecularFormula();
    	formula3.addIsotope(builder.newIsotope("C"),1);
    	formula3.addIsotope(builder.newIsotope("H"),2);
    	formula3.setCharge(0.0);
    	
    	Assert.assertTrue(MolecularFormulaManipulator.compare(formula1, formula2));
    	Assert.assertFalse(MolecularFormulaManipulator.compare(formula1, formula3));
        
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testCompare_NumberIsotope(){
		
		IMolecularFormula formula1 = new MolecularFormula();
    	formula1.addIsotope(builder.newIsotope("C"),1);
    	formula1.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula2 = new MolecularFormula();
    	formula2.addIsotope(builder.newIsotope("C"),1);
    	formula2.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula3 = new MolecularFormula();
    	formula3.addIsotope(builder.newIsotope("C"),1);
    	formula3.addIsotope(builder.newIsotope("H"),3);
    	
    	Assert.assertTrue(MolecularFormulaManipulator.compare(formula1, formula2));
    	Assert.assertFalse(MolecularFormulaManipulator.compare(formula1, formula3));
        
	}
	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
	@Test 
    public void testCompare_IMolecularFormula_IMolecularFormula(){
		
		IMolecularFormula formula1 = new MolecularFormula();
    	formula1.addIsotope(builder.newIsotope("C"),1);
    	formula1.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula2 = new MolecularFormula();
    	formula2.addIsotope(builder.newIsotope("C"),1);
    	formula2.addIsotope(builder.newIsotope("H"),2);
    	
    	IMolecularFormula formula3 = new MolecularFormula();
    	formula3.addIsotope(builder.newIsotope("C"),1);
    	IIsotope hyd = builder.newIsotope("H");
    	hyd.setExactMass(2.002334234);
    	formula3.addIsotope(hyd,2);
    	
    	Assert.assertTrue(MolecularFormulaManipulator.compare(formula1, formula2));
    	Assert.assertFalse(MolecularFormulaManipulator.compare(formula1, formula3));
        
	}
}


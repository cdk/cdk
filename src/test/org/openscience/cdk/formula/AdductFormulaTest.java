/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
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
 * 
 */
package org.openscience.cdk.formula;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.formula.AdductFormula;
import org.openscience.cdk.formula.IAdductFormula;
import org.openscience.cdk.formula.IMolecularFormula;
import org.openscience.cdk.formula.IMolecularFormulaSet;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the AdductFormula.
 *
 * @cdk.module test-formula
 * 
 * @see AdductFormula
 */
public class AdductFormulaTest extends NewCDKTestCase {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	
	/**
	 * Constructor of the AdductFormulaTest.
	 */
	public AdductFormulaTest() {
        super();
    }

	/**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testAdductFormula() {
        IAdductFormula mfS = new AdductFormula();
        Assert.assertNotNull(mfS);
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testAdductFormula_IMolecularFormula() {
        IAdductFormula mfS = new AdductFormula(new MolecularFormula());
        Assert.assertEquals(1, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testSize() {
        IAdductFormula mfS = new AdductFormula();
        Assert.assertEquals(0, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testAddIMolecularFormula() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        
        Assert.assertEquals(3, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testAdd_IMolecularFormulaSet() {
        IAdductFormula adduct = new AdductFormula();
        IMolecularFormulaSet mfSet = new MolecularFormulaSet();
        mfSet.addMolecularFormula(new MolecularFormula());
        mfSet.addMolecularFormula(new MolecularFormula());
        mfSet.addMolecularFormula(new MolecularFormula());
        adduct.add(mfSet);
        
        Assert.assertEquals(3, adduct.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testIterator() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(3, mfS.size());
        Iterator<IMolecularFormula> iter = mfS.iterator();
        int count = 0;
        while (iter.hasNext()) {
        	iter.next();
        	++count;
        	iter.remove();
        }
        Assert.assertEquals(0, mfS.size());
        Assert.assertEquals(3, count);
        Assert.assertFalse(iter.hasNext());
    }

    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testMolecularFormulas() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(3, mfS.size());
        int count = 0;
        for(IMolecularFormula formula : mfS.molecularFormulas()) {
        	++count;
//        	mfS.removeMolecularFormula(formula);
        }
//        Assert.assertEquals(0, mfS.size());
        Assert.assertEquals(3, count);
    }

    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testAdd_IAdductFormula() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        
        IAdductFormula tested = new AdductFormula();
        Assert.assertEquals(0, tested.size());
        tested.add(mfS);
        Assert.assertEquals(3, tested.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetMolecularFormula_int() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        
        Assert.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testAddMolecularFormula_IMolecularFormula() {
        IAdductFormula mfS = new AdductFormula();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(5, mfS.size());
        
        // now test it to make sure it properly grows the array
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(7, mfS.size());        
    }
    
    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetMolecularFormulas_int() {
        IAdductFormula mfS = new AdductFormula();
        
        Assert.assertEquals(0, mfS.size());

        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(3, mfS.size());
        Assert.assertNotNull(mfS.getMolecularFormula(0));
        Assert.assertNotNull(mfS.getMolecularFormula(1));
        Assert.assertNotNull(mfS.getMolecularFormula(2));
    }
    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testContains_IIsotope() {
    	IAdductFormula add = new AdductFormula();
        

    	IMolecularFormula mf = new MolecularFormula();
        IIsotope carb = builder.newIsotope("C");
        IIsotope h1 = builder.newIsotope("H");
        IIsotope h2 = builder.newIsotope("H");
        h2.setExactMass(2.00055);
        
        mf.addIsotope( carb );
        mf.addIsotope( h1 );
        
        add.addMolecularFormula(mf);
    	
        Assert.assertTrue(mf.contains(carb));
        Assert.assertTrue(mf.contains(h1));
        Assert.assertFalse(mf.contains(h2));
    }

    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testContains_IMolecularFormula() {
    	IAdductFormula add = new AdductFormula();
        

    	IMolecularFormula mf = new MolecularFormula();
        IIsotope carb = builder.newIsotope("C");
        IIsotope h1 = builder.newIsotope("H");
        IIsotope h2 = builder.newIsotope("H");
        h2.setExactMass(2.00055);
        
        mf.addIsotope( carb );
        mf.addIsotope( h1 );
        
        add.addMolecularFormula(mf);
    	
        Assert.assertTrue(add.contains(mf));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetCharge() {

    	IAdductFormula add = new AdductFormula();
    	IMolecularFormula mf1 = new MolecularFormula();
    	mf1.setCharge(1.0);
        add.addMolecularFormula(mf1);
        
        Assert.assertEquals(1.0,add.getCharge());
        
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testSetCharge_Double() {
    	testGetCharge();
        
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testClone() throws Exception {
        IAdductFormula mfS = new AdductFormula();
        Object clone = mfS.clone();
        Assert.assertTrue(clone instanceof IAdductFormula);
        Assert.assertNotSame(mfS, clone);
    } 
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testRemoveMolecularFormula_IMolecularFormula() {
        IAdductFormula mfS = new AdductFormula();
        IMolecularFormula mf1 = new MolecularFormula();
        IMolecularFormula mf2 = new MolecularFormula();
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        mfS.removeMolecularFormula(mf1);
        Assert.assertEquals(1, mfS.size());
        Assert.assertEquals(mf2, mfS.getMolecularFormula(0));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testRemoveAllMolecularFormulas() {
        IAdductFormula mfS = new AdductFormula();
        IMolecularFormula mf1 = new MolecularFormula();
        IMolecularFormula mf2 = new MolecularFormula();
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        
        Assert.assertEquals(2, mfS.size());
        mfS.removeAllMolecularFormulas();
        Assert.assertEquals(0, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testRemoveMolecularFormula_int() {
        IAdductFormula mfS = new AdductFormula();
        IMolecularFormula mf1 = new MolecularFormula();
        IMolecularFormula mf2 = new MolecularFormula();
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        mfS.removeMolecularFormula(0);
        Assert.assertEquals(1, mfS.size());
        Assert.assertEquals(mf2, mfS.getMolecularFormula(0));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testReplaceMolecularFormula_int_IMolecularFormula() {
        IAdductFormula mfS = new AdductFormula();
        IMolecularFormula mf1 = new MolecularFormula();
        IMolecularFormula mf2 = new MolecularFormula();
        IMolecularFormula mf3 = new MolecularFormula();
        mfS.addMolecularFormula(mf1);
        mfS.addMolecularFormula(mf2);
        Assert.assertEquals(mf2, mfS.getMolecularFormula(1));
        mfS.removeMolecularFormula(1);
        mfS.addMolecularFormula(mf3);
        Assert.assertEquals(mf3, mfS.getMolecularFormula(1));
    }
    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetIsotopeCount() {
    	
    	IAdductFormula add = new AdductFormula();
        Assert.assertEquals(0, add.getIsotopeCount());
    	
    	IMolecularFormula formula = new MolecularFormula();
    	formula.addIsotope( builder.newIsotope("C") );
    	formula.addIsotope( builder.newIsotope("H"),4 );
        
	    add.addMolecularFormula(formula);
	    
        Assert.assertEquals(2, add.getIsotopeCount());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testIsotopes() {
    	IAdductFormula add = new AdductFormula();
    	
    	IMolecularFormula formula1 = new MolecularFormula();
    	formula1.addIsotope( builder.newIsotope("C") );
    	formula1.addIsotope( builder.newIsotope("H"),4 );
    	
    	IMolecularFormula formula2 = new MolecularFormula();
    	formula2.addIsotope( builder.newIsotope("F"));
    	
    	add.addMolecularFormula(formula1);
    	add.addMolecularFormula(formula2);
    	
    	int count = 0;
		Iterator<IIsotope> it = add.isotopes();
		while(it.hasNext()){
         	it.next();
         	++count;
		}
    	Assert.assertEquals(3, count);
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetIsotopeCount_Sum() {
    	
    	IAdductFormula add = new AdductFormula();
        Assert.assertEquals(0, add.getIsotopeCount());
    	
    	IMolecularFormula adduct1 = new MolecularFormula();
    	adduct1.addIsotope( builder.newIsotope("C") );
    	IIsotope h = builder.newIsotope("H");
    	adduct1.addIsotope( h,4 );
	    add.addMolecularFormula(adduct1);
	    
	    IMolecularFormula formula = new MolecularFormula();
	    formula.addIsotope(  h );
	    add.addMolecularFormula(adduct1);
	    
	    
        Assert.assertEquals(2, add.getIsotopeCount());
    }
    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetIsotopeCount_IIsotope() {
    	
    	IAdductFormula add = new AdductFormula();
        Assert.assertEquals(0, add.getIsotopeCount());
    	
    	IMolecularFormula formula = new MolecularFormula();
    	IIsotope C = builder.newIsotope("C");
    	formula.addIsotope( C );
    	IIsotope h = builder.newIsotope("H");
    	formula.addIsotope( h,4 );

	    add.addMolecularFormula(formula);

        Assert.assertEquals(2, formula.getIsotopeCount());
        Assert.assertEquals(2, add.getIsotopeCount());
        Assert.assertEquals(1, add.getIsotopeCount(C));
        Assert.assertEquals(4, add.getIsotopeCount(h));
    }
    
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test
    public void testGetIsotopeCount_Sum_Isotope() {
    	
    	IAdductFormula add = new AdductFormula();
        Assert.assertEquals(0, add.getIsotopeCount());
    	
    	IMolecularFormula adduct1 = new MolecularFormula();
    	IIsotope C = builder.newIsotope("C");
    	adduct1.addIsotope( C );
	    IIsotope h = builder.newIsotope("H");
    	adduct1.addIsotope( h,4 );
	    add.addMolecularFormula(adduct1);
	    
	    IMolecularFormula adduct2 = new MolecularFormula();
	    adduct2.addIsotope(  h );
	    add.addMolecularFormula(adduct2);
	    
	    
        Assert.assertEquals(1, add.getIsotopeCount(C));
        Assert.assertEquals(5, add.getIsotopeCount(h));
    }
}

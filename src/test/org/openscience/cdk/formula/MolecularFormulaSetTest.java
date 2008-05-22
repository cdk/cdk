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
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * Checks the functionality of the MolecularFormulaSet class.
 *
 * @cdk.module test-data
 *
 * @see MolecularFormulaSet
 */
public class MolecularFormulaSetTest extends NewCDKTestCase {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	/**
	 *  Constructor for the MolecularFormulaSetTest object.
	 *
	 */
    public MolecularFormulaSetTest() {
        super();
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testMolecularFormulaSet() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        Assert.assertNotNull(mfS);
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testMolecularFormulaSet_IMolecularFormula() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet(new MolecularFormula());
        Assert.assertEquals(1, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testSize() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        mfS.addMolecularFormula(new MolecularFormula());
        Assert.assertEquals(1, mfS.size());
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testAdd_IMolecularFormula() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
    public void testIterator() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());

        Assert.assertEquals(3, mfS.size());
        int count = 0;
        for(IMolecularFormula formula: mfS.molecularFormulas()) {
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
    public void testAdd_IMolecularFormulaSet() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        
        IMolecularFormulaSet tested = new MolecularFormulaSet();
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
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        mfS.addMolecularFormula(new MolecularFormula());
        
        Assert.assertNotNull(mfS.getMolecularFormula(2)); // third molecule should exist
//        Assert.assertNull(mfS.getMolecularFormula(3)); // fourth molecule must not exist
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testAddMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
    public void testGetMolecularFormulas() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        
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
    public void testContains_IMolecularFormula() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        

    	IMolecularFormula mf = new MolecularFormula();
        IIsotope carb = builder.newIsotope("C");
        IIsotope h1 = builder.newIsotope("H");
        IIsotope h2 = builder.newIsotope("H");
        h2.setExactMass(2.00055);
        
        mf.addIsotope( carb );
        mf.addIsotope( h1 );
        
        mfS.addMolecularFormula(mf);
    	
        Assert.assertTrue(mfS.contains(mf));
    }
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testClone() throws Exception {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
        Object clone = mfS.clone();
        Assert.assertTrue(clone instanceof IMolecularFormulaSet);
        Assert.assertNotSame(mfS, clone);
    } 
    /**
	 * A unit test suite for JUnit.
	 *
	 * @return    The test suite
	 */
    @Test 
    public void testRemoveMolecularFormula_IMolecularFormula() {
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
        IMolecularFormulaSet mfS = new MolecularFormulaSet();
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
}

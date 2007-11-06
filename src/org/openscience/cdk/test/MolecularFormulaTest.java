/* $RCSfile$
 * $Author: egonw $    
 * $Date: 2007-02-09 00:35:55 +0100 (Fri, 09 Feb 2007) $    
 * $Revision: 7921 $
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;


import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.MolecularFormula;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Checks the functionality of the MolecularFormula.
 *
 * @cdk.module test-data
 */
public class MolecularFormulaTest extends CDKTestCase {

	protected IChemObjectBuilder builder;

	/**
	 *  Constructor for the MolecularFormulaTest object
	 *
	 *@param  name  Description of the Parameter
	 */
    public MolecularFormulaTest(String name) {
        super(name);
    }

    /**
     *  The JUnit setup method
     */
    public void setUp() {
    	builder = DefaultChemObjectBuilder.getInstance();
    }

    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public static Test suite() {
        return new TestSuite(MolecularFormulaTest.class);
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet1() {
    	
        MolecularFormula mf = new MolecularFormula();
        mf.addElement( builder.newElement("C") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        
        assertEquals(2, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet2() {
    	MolecularFormula mf = new MolecularFormula();
    	IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
    	IElement h2 = builder.newElement("H");
    	IElement h3 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 );
        mf.addElement( h2 );
        mf.addElement( h3 );
        
        assertEquals(3, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
        assertEquals(1, mf.getAtomCount(carb));
        assertEquals(1, mf.getAtomCount(flu));
        assertEquals(3, mf.getAtomCount(h1));
        assertEquals(1, mf.getAtomCount(mf.getFirstElement()));
        assertEquals(3, mf.getAtomCount(mf.getLastElement()));
    }

    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet2_1() {
    	MolecularFormula mf = new MolecularFormula();
    	IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 ,3);
        
        assertEquals(3, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
        assertEquals(1, mf.getAtomCount(carb));
        assertEquals(1, mf.getAtomCount(flu));
        assertEquals(3, mf.getAtomCount(h1));
        assertEquals(1, mf.getAtomCount(mf.getFirstElement()));
        assertEquals(3, mf.getAtomCount(mf.getLastElement()));
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet2_2() {
    	MolecularFormula mf = new MolecularFormula();
    	IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 ,3);
        
        assertEquals(3, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
        assertEquals(1, mf.getAtomCount(carb));
        assertEquals(1, mf.getAtomCount(flu));
        assertEquals(3, mf.getAtomCount(h1));
        assertEquals(1, mf.getAtomCount(mf.getFirstElement()));
        assertEquals(3, mf.getAtomCount(mf.getLastElement()));
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet3() {
    	
    	 IMolecule acetone = builder.newMolecule();
         
         IAtom c1 = builder.newAtom("C");
         IAtom c2 = builder.newAtom("C");
         IAtom o = builder.newAtom("O");
         IAtom c3 = builder.newAtom("C");
         acetone.addAtom(c1);
         acetone.addAtom(c2);
         acetone.addAtom(c3);
         acetone.addAtom(o);
         IBond b1 = builder.newBond(c1, c2,1);
         IBond b2 = builder.newBond(c1, o, 2);
         IBond b3 = builder.newBond(c1, c3,1);
         acetone.addBond(b1);
         acetone.addBond(b2);
         acetone.addBond(b3);
    	
        MolecularFormula mf = new MolecularFormula(acetone);
        
        assertEquals(2, mf.getElementCount());
        assertEquals(4, mf.getAtomCount());
        assertEquals(3, mf.getAtomCount(c1));
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet_Iterator6() {
    	
        MolecularFormula mf = new MolecularFormula();
        mf.addElement( builder.newElement("C") );
        mf.addElement( builder.newElement("F") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        
        assertEquals(3, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
        assertEquals(0.0, mf.getCharge());
        

        assertEquals("C", mf.getElement(0).getSymbol());
        assertEquals("F", mf.getElement(1).getSymbol());
        assertEquals("H", mf.getElement(2).getSymbol());
    }
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet_Iterator7() {
    	
        MolecularFormula mf = new MolecularFormula();
        mf.addElement( builder.newElement("C") );
        mf.addElement( builder.newElement("F") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        
        Iterator<IElement> elementIter = mf.elements();
        int counter = 0;
        while (elementIter.hasNext()) {
        	elementIter.next();
            counter++;
        }
        assertEquals(3, counter);
    }
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testSet8() {
    	
        MolecularFormula mf = new MolecularFormula();
        IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
    	IElement h2 = builder.newElement("H");
    	IElement h3 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 );
        mf.addElement( h2 );
        mf.addElement( h3 );
        
        Iterator<IElement> elementIter = mf.elements();
        assertNotNull(elementIter);
        assertTrue(elementIter.hasNext());
        IElement next = (IElement)elementIter.next();
        assertTrue(next instanceof IElement);
        assertEquals(carb.getSymbol(), next.getSymbol());
        
        assertTrue(elementIter.hasNext());
        next = (IElement)elementIter.next();
        assertTrue(next instanceof IElement);
        assertEquals(flu.getSymbol(), next.getSymbol());
        
        assertTrue(elementIter.hasNext());
        next = (IElement)elementIter.next();
        assertTrue(next instanceof IElement);
        assertEquals(h1.getSymbol(), next.getSymbol());
        
        assertFalse(elementIter.hasNext());
    }
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testCharge_1() {
    	
        MolecularFormula mf = new MolecularFormula();
        mf.setCharge(1.0);
        mf.addElement( builder.newElement("C") );
        mf.addElement( builder.newElement("F") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        mf.addElement( builder.newElement("H") );
        
        assertEquals(3, mf.getElementCount());
        assertEquals(5, mf.getAtomCount());
        assertEquals(1.0,mf.getCharge());
        
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testCharge_2() {
    	

    	IMolecule acetone = loadAcetone();
    	acetone.getAtom(1).setCharge(1.0);
    	
        MolecularFormula mf = new MolecularFormula(acetone);
        assertEquals(2, mf.getElementCount());
        assertEquals(4, mf.getAtomCount());
        assertEquals(1.0,mf.getCharge());
    }
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testremoveElement() {
    	MolecularFormula mf = new MolecularFormula();
        IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
    	IElement h2 = builder.newElement("H");
    	IElement h3 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 );
        mf.addElement( h2 );
        mf.addElement( h3 );
        
        // remove the Fluorine 
        mf.removeElement(h1);
        
        assertEquals(2, mf.getElementCount());
        assertEquals(2, mf.getAtomCount());
        

        assertEquals("C", mf.getElement(0).getSymbol());
        assertEquals("F", mf.getElement(1).getSymbol());
        assertEquals(1, mf.getAtomCount(flu));
    }
    
    /**
	 * A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
    public void testRemoveAllElements() {
    	MolecularFormula mf = new MolecularFormula();
        IElement carb = builder.newElement("C");
    	IElement flu = builder.newElement("F");
    	IElement h1 = builder.newElement("H");
    	IElement h2 = builder.newElement("H");
    	IElement h3 = builder.newElement("H");
        mf.addElement( carb );
        mf.addElement( flu );
        mf.addElement( h1 );
        mf.addElement( h2 );
        mf.addElement( h3 );
        
        // remove the Fluorine 
        mf.removeAllElements();
        
        assertEquals(0, mf.getElementCount());
        assertEquals(0, mf.getAtomCount());
        
    }
    

    /** Test for RFC #9 */
    public void testToString() {
        MolecularFormula mf = new MolecularFormula();
        String description = mf.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }
    

  /**
   * Only test whether the MolecularFormula are correctly cloned.
   */
	public void testClone() throws Exception {
      MolecularFormula mf = new MolecularFormula();
      Object clone = mf.clone();
      assertTrue(clone instanceof MolecularFormula);
//      assertTrue(clone instanceof IMolecularFormula);
  }    
     
  
  

//    public void testStateChanged_IChemObjectChangeEvent() {
//        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
//        IAtomContainer chemObject = builder.newAtomContainer();
//        chemObject.addListener(listener);
//        
//        chemObject.addAtom(builder.newAtom());
//        assertTrue(listener.changed);
//        
//        listener.reset();
//        assertFalse(listener.changed);
//        chemObject.addBond(builder.newBond(builder.newAtom(), builder.newAtom()));
//        assertTrue(listener.changed);
//    }
//
//    private class ChemObjectListenerImpl implements IChemObjectListener {
//        private boolean changed;
//        
//        private ChemObjectListenerImpl() {
//            changed = false;
//        }
//        
//        public void stateChanged(IChemObjectChangeEvent e) {
//            changed = true;
//        }
//        
//        public void reset() {
//            changed = false;
//        }
//    }
//    
//
	private IMolecule loadAcetone(){
		IMolecule acetone = builder.newMolecule();
         
         IAtom c1 = builder.newAtom("C");
         IAtom c2 = builder.newAtom("C");
         IAtom o = builder.newAtom("O");
         IAtom c3 = builder.newAtom("C");
         acetone.addAtom(c1);
         acetone.addAtom(c2);
         acetone.addAtom(c3);
         acetone.addAtom(o);
         IBond b1 = builder.newBond(c1, c2,1);
         IBond b2 = builder.newBond(c1, o, 2);
         IBond b3 = builder.newBond(c1, c3,1);
         acetone.addBond(b1);
         acetone.addBond(b2);
         acetone.addBond(b3);
         
         return acetone;
	}
}

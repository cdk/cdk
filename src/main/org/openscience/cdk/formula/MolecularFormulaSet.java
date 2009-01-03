/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.formula;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;

/**
 *  Class defining an set object of MolecularFormulas. It maintains
 *   a list of list IMolecularFormula.<p>
 *   
 * @cdk.module  data
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
@TestClass("org.openscience.cdk.formula.MolecularFormulaSetTest")
public class MolecularFormulaSet implements Iterable<IMolecularFormula>, IMolecularFormulaSet, Cloneable {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -2043178712150212550L;
	
	/**  Internal List of IMolecularFormula. */
	private List<IMolecularFormula> components;
	
	/**
	 *  Constructs an empty MolecularFormulaSet.
	 *  
	 *  @see #MolecularFormulaSet(IMolecularFormula)
	 */
	public MolecularFormulaSet() {
		components = new ArrayList<IMolecularFormula>();
	}
	
	/**
	 * Constructs a MolecularFormulaSet with a copy MolecularFormulaSet of another 
	 * MolecularFormulaSet (A shallow copy, i.e., with the same objects as in
	 * the original MolecularFormulaSet).
	 *
	 *  @param  formula  An MolecularFormula to copy from
	 *  @see             #MolecularFormulaSet()
	 */
	public MolecularFormulaSet(IMolecularFormula formula) {
		components = new ArrayList<IMolecularFormula>();
		components.add(0, formula);
	}
	/**
     *  Adds all molecularFormulas in the MolecularFormulaSet to this chemObject.
     *
     * @param  formulaSet  The MolecularFormulaSet 
     */
    @TestMethod("testAdd_IMolecularFormulaSet")
	public void add(IMolecularFormulaSet formulaSet) {
		for (IMolecularFormula mf : formulaSet.molecularFormulas()){
            addMolecularFormula(mf);
        }
        /*
		 *  notifyChanged() is called by addAtomContainer()
		 */
	}
	/**
	 * Adds an molecularFormula to this chemObject.
	 *
	 * @param  formula  The molecularFormula to be added to this chemObject
	 */
    @TestMethod("testAdd_IMolecularFormula")
	public void addMolecularFormula(IMolecularFormula formula) {
		components.add(formula);
	}
	
	/**
	 *  Returns an Iterable for looping over all IMolecularFormula
	 *   in this MolecularFormulaSet.
	 *
	 * @return    An Iterable with the IMolecularFormula in this MolecularFormulaSet
	 */
    @TestMethod("testMolecularFormulas")
	public Iterable<IMolecularFormula> molecularFormulas() {
		return components;
	}
	
	/**
	 *  Returns an Iterator for looping over all IMolecularFormula
	 *   in this MolecularFormulaSet.
	 *
	 * @return    An Iterator with the IMolecularFormula in this MolecularFormulaSet
	 */
    @TestMethod("testIterator")
	public Iterator<IMolecularFormula> iterator() {
		return components.iterator();
	}
	
	/**
     * Returns the number of MolecularFormulas in this MolecularFormulaSet.
     *
     * @return     The number of MolecularFormulas in this MolecularFormulaSet
     */
    @TestMethod("testSize")
	public int size() {
		return components.size();
	}

	/**
	 *  True, if the MolecularFormulaSet contains the given IMolecularFormula object.
	 *
	 * @param  formula  The IMolecularFormula this MolecularFormulaSet is searched for
	 * @return          True, if the MolecularFormulaSet contains the given IMolecularFormula object
	 */
    @TestMethod("testContains_IMolecularFormula")
    public boolean contains(IMolecularFormula formula) {
	    return components.contains(formula);
    }
    
    /**
     *  
     * Returns the MolecularFormula at position <code>number</code> in the
     * chemObject.
     *
     * @param  position The position of the IMolecularFormula to be returned. 
     * @return          The IMolecularFormula at position <code>number</code> . 
     */
    @TestMethod("testGetMolecularFormula_int")
    public  IMolecularFormula getMolecularFormula(int position) {
	    return components.get(position);
    }

    
    /**
	 * Removes all IMolecularFormula from this chemObject.
	 */
    @TestMethod("testRemoveAllMolecularFormulas")
    public void removeAllMolecularFormulas() {
	    components.clear();
    }

    /**
	 * Removes an IMolecularFormula from this chemObject.
	 *
	 * @param  formula  The IMolecularFormula to be removed from this chemObject
	 */
    @TestMethod("testRemoveMolecularFormula_IMolecularFormula")
    public void removeMolecularFormula(IMolecularFormula formula) {
	    components.remove(formula);
    }

    /**
	 * Removes an MolecularFormula from this chemObject.
	 *
	 * @param  position  The position of the MolecularFormula to be removed from this chemObject
	 */
    @TestMethod("testRemoveMolecularFormula_int")
    public void removeMolecularFormula(int position) {
	    components.remove(position);
    }
    
    /**
	 * Clones this MolecularFormulaSet object and its content.
	 *
	 * @return    The cloned object
	 */
    @TestMethod("testClone")
	public Object clone() throws CloneNotSupportedException {
		
//		/* it is not a super class of chemObject */
//		MolecularFormulaSet clone = (MolecularFormulaSet) super.clone();
//        // start from scratch
//		clone.removeAllMolecularFormulas();
//        // clone all molecularFormulas
//		Iterator<IMolecularFormula> iterForm = this.molecularFormulas();
//		while(iterForm.hasNext()){
//			clone.addMolecularFormula((IMolecularFormula) iterForm.next().clone());
//		}
		
		MolecularFormulaSet clone = new MolecularFormulaSet();
		for (IMolecularFormula mf : this.molecularFormulas()){
			clone.addMolecularFormula((IMolecularFormula) mf.clone());
		}
		return clone;
	}

	public IChemObjectBuilder getBuilder() {
	    return DefaultChemObjectBuilder.getInstance();
    }

}

/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
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

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Class defining an adduct object in a MolecularFormula. It maintains
 *   a list of list IMolecularFormula.<p>
 *   
 *  Examples:
 * <ul>
 *   <li><code>[C2H4O2+Na]+</code></li>
 * </ul>
 * 
 * @cdk.module  data
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
@TestClass("org.openscience.cdk.formula.AdductFormulaTest")
public class AdductFormula implements Iterable<IMolecularFormula>, IAdductFormula, Cloneable{

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -811384981700039389L;
	
	/**  Internal List of IMolecularFormula. */
	private List<IMolecularFormula> components;
	
	/**
	 *  Constructs an empty AdductFormula.
	 *  
	 *  @see #AdductFormula(IMolecularFormula)
	 */
	public AdductFormula() {
		components = new ArrayList<IMolecularFormula>();
	}
	
	/**
	 * Constructs an AdductFormula with a copy AdductFormula of another 
	 * AdductFormula (A shallow copy, i.e., with the same objects as in
	 * the original AdductFormula).
	 *
	 *  @param  formula  An MolecularFormula to copy from
	 *  @see             #AdductFormula()
	 */
	public AdductFormula(IMolecularFormula formula) {
		components = new ArrayList<IMolecularFormula>();
		components.add(0, formula);
	}
	
	/**
	 * Adds an molecularFormula to this chemObject.
	 *
	 * @param  formula  The molecularFormula to be added to this chemObject
	 */
    @TestMethod("testAddMolecularFormula_IMolecularFormula")
	public void addMolecularFormula(IMolecularFormula formula) {
		components.add(formula);
	}
	/**
     *  Adds all molecularFormulas in the AdductFormula to this chemObject.
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
	 *  True, if the AdductFormula contains the given IIsotope object and not
	 *  the instance. The method looks for other isotopes which has the same
	 *  symbol, natural abundance and exact mass.
	 *  
	 * @param  isotope  The IIsotope this AdductFormula is searched for
	 * @return          True, if the AdductFormula contains the given isotope object
	 */
    @TestMethod("testContains_IIsotope")
	public boolean contains(IIsotope isotope) {
		for(Iterator<IIsotope> it = isotopes().iterator(); it.hasNext(); ) {
			IIsotope thisIsotope = it.next();
			if(isTheSame(thisIsotope, isotope)){
				return true;
			}
		}
		return false;
	}
	
	/**
     *  Returns the partial charge of this Adduct. If the charge 
     *  has not been set the return value is Double.NaN.
     *
     * @return the charge of this Adduct
     *
     * @see    #setCharge
     */
    @TestMethod("testGetCharge")
	public Integer getCharge() {
		Integer charge = 0;
		Iterator<IMolecularFormula> componentIterator = components.iterator();
		while (componentIterator.hasNext()) {
			charge += componentIterator.next().getCharge();
		}
		return charge;
	}

	/**
	 *  Checks a set of Nodes for the occurrence of the isotope in the 
	 *  adduct formula from a particular isotope. It returns 0 if the does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this adduct
	 * @see                      #getIsotopeCount()
	 */
    @TestMethod("testGetIsotopeCount_IIsotope")
	public int getIsotopeCount(IIsotope isotope) {
		int count = 0;
		Iterator<IMolecularFormula> componentIterator = components.iterator();
		while (componentIterator.hasNext()) {
			count += componentIterator.next().getIsotopeCount(isotope);
		}
		return count;
	}

	/**
	 *  Checks a set of Nodes for the number of different isotopes in the 
	 *  adduct formula.
	 *
	 * @return        The the number of different isotopes in this adduct formula
	 * @see           #getIsotopeCount(IIsotope)
	 */
    @TestMethod("testGetIsotopeCount")
	public int getIsotopeCount() {
		return isotopesList().size();
	}
	
	
	/**
	 *  Returns an Iterator for looping over all isotopes in this adduct formula.
	 *
	 * @return    An Iterator with the isotopes in this adduct formula
	 */
    @TestMethod("testIsotopes")
	public Iterable<IIsotope> isotopes() {
		return new Iterable() {
            public Iterator iterator() {
                return isotopesList().iterator();
            }
        };
	}
	
	/**
	 *  Returns a List for looping over all isotopes in this adduct formula.
	 *
	 * @return    A List with the isotopes in this adduct formula
	 */
	private List<IIsotope> isotopesList() {
		List<IIsotope> isotopes = new ArrayList<IIsotope>();
		Iterator<IMolecularFormula> componentIterator = components.iterator();
		while (componentIterator.hasNext()) {
			Iterator<IIsotope> compIsotopes = componentIterator.next().isotopes().iterator();
			while (compIsotopes.hasNext()) {
				IIsotope isotope = compIsotopes.next();
				if (!isotopes.contains(isotope)) {
					isotopes.add(isotope);
				}
			}
		}
		return isotopes;
	}
	
	/**
     *  No use this method. The charge is defined in each
     *  IMolecularFormula.
     *
     * @param  charge  The partial charge
     * @deprecated
     *
     * @see    #getCharge
     */
     @TestMethod("testSetCharge")
	public void setCharge(Integer charge) {
		throw new java.lang.IllegalAccessError();
	}

	/**
	 *  Returns an Iterable for looping over all IMolecularFormula
	 *   in this adduct formula.
	 *
	 * @return    An Iterable with the IMolecularFormula in this adduct formula
	 */
    @TestMethod("testMolecularFormulas")
	public Iterable<IMolecularFormula> molecularFormulas() {
		return components;
	}

	/**
	 *  Returns an Iterator for looping over all IMolecularFormula
	 *   in this adduct formula.
	 *
	 * @return    An Iterator with the IMolecularFormula in this adduct formula
	 */
    @TestMethod("testIterator")
	public Iterator<IMolecularFormula> iterator() {
		return components.iterator();
	}
	
	/**
     * Returns the number of MolecularFormulas in this AdductFormula.
     *
     * @return     The number of MolecularFormulas in this AdductFormula
     */
    @TestMethod("testSize")
	public int size() {
		return components.size();
	}

	/**
	 *  True, if the AdductFormula contains the given IMolecularFormula object.
	 *
	 * @param  formula  The IMolecularFormula this AdductFormula is searched for
	 * @return          True, if the AdductFormula contains the given IMolecularFormula object
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
    @TestMethod("testGetMolecularFormulas_int")
    public IMolecularFormula getMolecularFormula(int position) {
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
	 * @param  position The position of the MolecularFormula to be removed from this chemObject
	 */
    @TestMethod("testRemoveMolecularFormula_int")
    public void removeMolecularFormula(int position) {
	    components.remove(position);
    }
    /**
	 * Clones this AdductFormula object and its content.
	 *
	 * @return    The cloned object
	 */
    @TestMethod("testClone")
	public Object clone() throws CloneNotSupportedException {
		
//		/* it is not a super class of chemObject */
//		AdductFormula clone = (AdductFormula) super.clone();
//        // start from scratch
//		clone.removeAllMolecularFormulas();
//        // clone all molecularFormulas
//		Iterator<IMolecularFormula> iterForm = this.molecularFormulas();
//		while(iterForm.hasNext()){
//			clone.addMolecularFormula((IMolecularFormula) iterForm.next().clone());
//		}
		
		AdductFormula clone = new AdductFormula();
		for(IMolecularFormula form: this.molecularFormulas()){
			clone.addMolecularFormula((IMolecularFormula) form.clone());
		}
		return clone;
	}
	
	/**
	 * Compare to IIsotope. The method doesn't compare instance but if they
	 * have the same symbol, natural abundance and exact mass.
	 * 
	 * @param isotopeOne   The first Isotope to compare
	 * @param isotopeTwo   The second Isotope to compare
	 * @return             True, if both isotope are the same
	 */
	private boolean isTheSame(IIsotope isotopeOne, IIsotope isotopeTwo) {
		
		if(isotopeOne.getSymbol() != isotopeTwo.getSymbol() )
			return false;
		if(isotopeOne.getNaturalAbundance() != isotopeTwo.getNaturalAbundance() )
			return false;
		if(isotopeOne.getExactMass() != isotopeTwo.getExactMass() )
			return false;
		
		return true;
	}

	public IChemObjectBuilder getBuilder() {
	    return DefaultChemObjectBuilder.getInstance();
    }
}

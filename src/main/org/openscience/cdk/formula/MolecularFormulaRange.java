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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IIsotope;
/**
 *  Class defining a expanded molecular formula object. The Isotopes don't have
 *  a fix occurrence in the MolecularFormula but they have a range.<p>
 *  With this class man can define a MolecularFormula which contains certain IIsotope
 *  with a maximum and minimum occurrence.
 *   
 *  Examples:
 * <ul>
 *   <li><code>[C(1-5)H(4-10)]-</code></li>
 * </ul>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
@TestClass("org.openscience.cdk.formula.MolecularFormulaRangeTest")
public class MolecularFormulaRange implements Cloneable {

	
	private Map<IIsotope,Integer> isotopesMax;
	private Map<IIsotope,Integer> isotopesMin;	
	/**
	 *  Constructs an empty MolecularFormulaExpand.
	 */
	public MolecularFormulaRange() {
		isotopesMax = new HashMap<IIsotope,Integer>();
		isotopesMin = new HashMap<IIsotope,Integer>();
	}
	
	/**
	 *  Adds an Isotope to this MolecularFormulaExpand in a number of
	 *  maximum and minimum occurrences allowed.
	 *
	 * @param  isotope  The isotope to be added to this MolecularFormulaExpand
	 * @param  countMax The maximal number of occurrences to add
	 * @param  countMin The minimal number of occurrences to add
	 * 
	 */
	@TestMethod("testAddIsotope_IIsotope_int_int")
	public void addIsotope(IIsotope isotope, int countMin, int countMax) {
		boolean flag = false;
		for(Iterator<IIsotope> it = isotopes().iterator(); it.hasNext(); ) {
			IIsotope thisIsotope = it.next();
			if(isTheSame(thisIsotope, isotope)){
				isotopesMax.put(thisIsotope, countMax);
				isotopesMin.put(thisIsotope, countMin);
				flag = true;
				break;
			}
		}
		if(!flag){
			isotopesMax.put(isotope, countMax);
			isotopesMin.put(isotope, countMin);
		}		
	}

	/**
	 *  True, if the MolecularFormulaExpand contains the given IIsotope. 
	 *  The method looks for other isotopes which has the same
	 *  symbol, natural abundance and exact mass.
	 *
	 * @param  isotope  The IIsotope this MolecularFormula is searched for
	 * @return          True, if the MolecularFormula contains the given isotope object
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
	 *  Checks a set of Nodes for the maximal occurrence of the isotope in the 
	 *  MolecularFormulaExpand from a particular isotope. It returns -1 if the Isotope
	 *  does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this IMolecularFormula
	 */
	@TestMethod("testGetIsotopeCountMax_IIsotope")
	public int getIsotopeCountMax(IIsotope isotope) {
		return !contains(isotope) ? -1 : isotopesMax.get(getIsotope(isotope));
	}
	/**
	 *  Checks a set of Nodes for the minimal occurrence of the isotope in the 
	 *  MolecularFormulaExpand from a particular isotope. It returns -1 if the Isotope
	 *  does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this IMolecularFormula
	 */
	@TestMethod("testGetIsotopeCountMin_IIsotope")
	public int getIsotopeCountMin(IIsotope isotope) {
		return !contains(isotope) ? -1 : isotopesMin.get(getIsotope(isotope));
	}

	/**
	 *  Checks a set of Nodes for the number of different isotopes in the 
	 *  MolecularFormulaExpand.
	 *
	 * @return        The the number of different isotopes in this MolecularFormulaExpand
	 */
	@TestMethod("testGetIsotopeCount")
	public int getIsotopeCount() {
		return isotopesMax.size();
	}
	
	/**
	 *  Get the isotope instance given an IIsotope. The instance is those
	 *  that has the isotope with the same symbol, natural abundance and
	 *  exact mass.
	 *
	 * @param  isotope The IIsotope for looking for
	 * @return         The IIsotope instance
   * @see            #isotopes
	 */
	private IIsotope getIsotope(IIsotope isotope){
		for(Iterator<IIsotope> it = isotopes().iterator(); it.hasNext(); ) {
			IIsotope thisIsotope = it.next();
			if(isTheSame(isotope,thisIsotope))
				return thisIsotope;
		}
		return null;
	}
	/**
	 *  Returns an Iterator for looping over all isotopes in this MolecularFormulaExpand.
	 *
	 * @return    An Iterator with the isotopes in this MolecularFormulaExpand
	 */
	@TestMethod("testIsotopes")
	public Iterable<IIsotope> isotopes() {
        return new Iterable<IIsotope>(){
            public Iterator<IIsotope> iterator() {
                return isotopesMax.keySet().iterator();
            }
        };
	}

	/**
	 * Removes all isotopes of this molecular formula.
	 */
    @TestMethod("testRemoveAllIsotopes")
	public void removeAllIsotopes() {
	    isotopesMax.clear();
	    isotopesMin.clear();
    }

    /**
	 *  Removes the given isotope from the MolecularFormulaExpand.
	 *
	 * @param isotope  The IIsotope to be removed
	 */
    @TestMethod("testRemoveIsotope_IIsotope")
	public void removeIsotope(IIsotope isotope) {
	    isotopesMax.remove(getIsotope(isotope));
	    isotopesMin.remove(getIsotope(isotope));
    }
    
    

    /**
	 * Clones this MolecularFormulaExpand object and its content. I should
	 * integrate into ChemObject.
	 *
	 * @return    The cloned object
	 */
	@TestMethod("testClone")
	public Object clone() throws CloneNotSupportedException {
		
		MolecularFormulaRange clone = new MolecularFormulaRange();
		Iterator<IIsotope> iterIso = this.isotopes().iterator();
		while(iterIso.hasNext()){
			IIsotope isotope = iterIso.next();
			clone.addIsotope((IIsotope) isotope.clone(),getIsotopeCountMin(isotope),getIsotopeCountMax(isotope));
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

}

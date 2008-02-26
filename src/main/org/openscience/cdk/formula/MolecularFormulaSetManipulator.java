/* $RCSfile$
 * $Author: miguelrojasch $ 
 * $Date: 2007-10-31 11:52:49 +0100 (Wed, 31 Oct 2007) $
 * $Revision: 9273 $
 * 
 *  Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.formula;

import java.util.Iterator;

import org.openscience.cdk.Element;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;


/**
 * Class with convenience methods that provide methods to manipulate
 * MolecularFormulaSet's. For example:
 * <pre>
 *  IMolecularFormula molecularFormula = MolecularManipulatorSet.getMaxOccurrenceElements(molecularFormulaSet);
 * </pre>
 * .
 *
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
@TestClass("org.openscience.cdk.formula.MolecularFormulaSetManipulatorTest")
public class MolecularFormulaSetManipulator {
	
	
	/**
	 * Extract from a set of MolecularFormula the maximum occurrence of each element found and
	 * put the element and occurrence in a new IMolecularFormula.
	 * 
	 * @param mfSet    The set of molecularFormules to inspect
	 * @return         A IMolecularFormula containing the maximum occurrence of the elements
	 * @see            #getMinOccurrenceElements(IMolecularFormulaSet)
	 */
    @TestMethod("testGetMaxOccurrenceElements_IMolecularFormulaSet")
	public static IMolecularFormula getMaxOccurrenceElements(IMolecularFormulaSet mfSet){
		
		IMolecularFormula molecularFormula = new MolecularFormula();
		for(IMolecularFormula mf: mfSet.molecularFormulas()){
			Iterator<IIsotope> iterIsot = mf.isotopes();
			while(iterIsot.hasNext()){
				IElement element = new Element(iterIsot.next());
				int occur_new = MolecularFormulaManipulator.getElementCount(mf,element);
				if(!MolecularFormulaManipulator.containsElement(molecularFormula,element)){
						molecularFormula.addIsotope(new Isotope(element),occur_new);
				}else{
					int occur_old = MolecularFormulaManipulator.getElementCount(molecularFormula,element);
					if(occur_new > occur_old){
						MolecularFormulaManipulator.removeElement(molecularFormula,element);
							molecularFormula.addIsotope(new Isotope(element),occur_new);
					}
				}
			}
		}
		return molecularFormula;
	}
	
	/**
	 * Extract from a set of MolecularFormula the minimal occurrence of each element found and
	 * put the element and occurrence in a new IMolecularFormula.
	 * 
	 * @param mfSet    The set of molecularFormules to inspect
	 * @return         A IMolecularFormula containing the minimal occurrence of the elements
	 * @see            #getMaxOccurrenceElements(IMolecularFormulaSet)
	 */
    @TestMethod("testGetMinOccurrenceElements_IMolecularFormulaSet")
	public static IMolecularFormula getMinOccurrenceElements(IMolecularFormulaSet mfSet){
		
		IMolecularFormula molecularFormula = new MolecularFormula();
		for(IMolecularFormula mf: mfSet.molecularFormulas()){
			Iterator<IIsotope> iterIsot = mf.isotopes();
			while(iterIsot.hasNext()){
				IElement element = new Element(iterIsot.next());
				int occur_new = MolecularFormulaManipulator.getElementCount(mf,element);
				if(!MolecularFormulaManipulator.containsElement(molecularFormula,element)){
						molecularFormula.addIsotope(new Isotope(element),occur_new);
				}else{
					int occur_old = MolecularFormulaManipulator.getElementCount(molecularFormula,element);
					if(occur_new < occur_old){
						MolecularFormulaManipulator.removeElement(molecularFormula,element);
							molecularFormula.addIsotope(new Isotope(element),occur_new);
					}
				}
			}
		}
		return molecularFormula;
	}
	
	/**
	 * Remove all those IMolecularFormula which are not fit theirs IElement 
	 * occurrence into a limits. The limits are given from formulaMax and formulaMin.
	 * In the minimal IMolecularFormula must contain all those IElement found in the 
	 * minimal IMolecularFormula.
	 * 
	 * @param formulaSet  IMolecularFormulaSet to look for
	 * @param formulaMax  A IMolecularFormula which contains the maximal representation of the Elements
	 * @param formulaMin  A IMolecularFormula which contains the minimal representation of the Elements
	 * @return            A IMolecularFormulaSet with only the IMolecularFormula which the IElements
	 * 						are into the correct occurrence 
	 */
    @TestMethod("testRemove_IMolecularFormulaSet_IMolecularFormula_IMolecularFormula")
	public static IMolecularFormulaSet remove(IMolecularFormulaSet formulaSet,
			IMolecularFormula formulaMin, IMolecularFormula formulaMax){
		
		// prove the correlation between maximum and minimum molecularFormula
		if(!validCorrelation(formulaMin, formulaMax))
			return null;
		
		
		IMolecularFormulaSet newFormulaSet = new MolecularFormulaSet();
		
		for(IMolecularFormula formula: formulaSet.molecularFormulas()){
			boolean flagPass = true;
			
			// the formula must contain all element found into the formulaMin
			if(!validCorrelation(formula,formulaMin)) 
					continue;
			
			Iterator<IElement> itEle = MolecularFormulaManipulator.elements(formulaMin).iterator();
			while(itEle.hasNext()){
				IElement element = itEle.next();
				int occur = MolecularFormulaManipulator.getElementCount(formula, element);
				int occurMax = MolecularFormulaManipulator.getElementCount(formulaMax, element);
				int occurMin = MolecularFormulaManipulator.getElementCount(formulaMin, element);
				
				if(!(occurMin <= occur) || !(occur <= occurMax)){
					flagPass = false;
					break;
				}
					
			}
			if(flagPass) // stored if each IElement occurrence is into the limits
				newFormulaSet.addMolecularFormula(formula);
			
		}
		return newFormulaSet;
	}
	
	/**
	 * In the minimal IMolecularFormula must contain all those IElement found in the 
	 * minimal IMolecularFormula.
	 * 
	 * @param formulaMax  A IMolecularFormula which contains the maximal representation of the Elements
	 * @param formulaMin  A IMolecularFormula which contains the minimal representation of the Elements
	 * @return            True, if the correlation is valid
	 */
	private static boolean validCorrelation(IMolecularFormula formulaMin, IMolecularFormula formulamax){
		Iterator<IElement> itMin = MolecularFormulaManipulator.elements(formulaMin).iterator();
		while(itMin.hasNext()){
			if(!MolecularFormulaManipulator.containsElement(formulamax, itMin.next()))
				return false;
			
		}
		return true;
	}

	/**
	 *  True, if the IMolecularFormulaSet contains the given IMolecularFormula but not
	 *  as object. It compare according contains the same number and type of Isotopes.
	 *  It is not based on compare objects.
	 *
	 * @param formulaSet   The IMolecularFormulaSet
	 * @param  formula     The IMolecularFormula this IMolecularFormulaSet is searched for
	 * @return             True, if the IMolecularFormulaSet contains the given formula
	 * @see                IMolecularFormula.contains(IMolecularFormula)
	 */
    @TestMethod("testContains_IMolecularFormulaSet_IMolecularFormula")
	public static boolean contains(IMolecularFormulaSet formulaSet, IMolecularFormula formula){
		for(IMolecularFormula fm: formulaSet.molecularFormulas()){
			if(MolecularFormulaManipulator.compare(fm, formula)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Remove all those IMolecularFormula which are not fit theirs IElement 
	 * occurrence into a limits. The limits are given from formulaMax and formulaMin.
	 * In the minimal IMolecularFormula must contain all those IElement found in the 
	 * minimal IMolecularFormula.
	 * 
	 * @param formulaSet   IMolecularFormulaSet to look for
	 * @param formulaRange A IMolecularFormulaRange which contains the range representation of the IIsotope
	 */
    @TestMethod("testRemove_IMolecularFormulaSet_MolecularFormulaRange")
	public static IMolecularFormulaSet remove(IMolecularFormulaSet formulaSet,
			MolecularFormulaRange formulaRange){
		
		
		IMolecularFormulaSet newFormulaSet = new MolecularFormulaSet();
		
		for(IMolecularFormula formula: formulaSet.molecularFormulas()){
			
			boolean flagCorrect = true;
			Iterator<IIsotope> itEle = formulaRange.isotopes();
			while(itEle.hasNext()){
				IIsotope isotope = itEle.next();
				if(formula.getIsotopeCount(isotope) != 0){
					if((formula.getIsotopeCount(isotope) < formulaRange.getIsotopeCountMin(isotope))
							||(formula.getIsotopeCount(isotope) > formulaRange.getIsotopeCountMax(isotope))){
						flagCorrect = false;
						break;
					}	
				}else if(formulaRange.getIsotopeCountMin(isotope) != 0){
					flagCorrect = false;
					break;
				}
			}
			itEle = formula.isotopes();
			while(itEle.hasNext()){
				IIsotope isotope = itEle.next();
				if(!formulaRange.contains(isotope)){
					flagCorrect = false;
					break;
				}
			}
			if(flagCorrect) // stored if each IElement occurrence is into the limits
				newFormulaSet.addMolecularFormula(formula);
			
		}
		return newFormulaSet;
	}
	
}


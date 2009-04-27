/* $Revision$ $Author$ $Date$
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
 */
package org.openscience.cdk.formula;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.rules.ChargeRule;
import org.openscience.cdk.formula.rules.ElementRule;
import org.openscience.cdk.formula.rules.IRule;
import org.openscience.cdk.formula.rules.ToleranceRangeRule;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaRangeManipulator;

/**
 * <p>Tool to determine molecular formula consistent with a given accurate mass. The 
 * molecular formulas are not validate. It only consist in generate combination according
 * object (see MolecularFormulaChecker).
 * 
 * <pre>
 *   MassToFormulaTool mf = new MassToFormulaTool();
 *   double myMass = 133.004242;
 *   IMolecularFormulaSet mfSet = mf.generate(myMass);
 * </pre>
 * 
 * <p>The elements are listed according on difference with the proposed mass.
 * 
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-03-01
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.formula.MassToFormulaToolTest")
public class MassToFormulaTool {

	private LoggingTool logger = new LoggingTool(MassToFormulaTool.class);
	
	private IChemObjectBuilder builder;
	
	/** */
	AtomTypeFactory factory;
	
	/** matrix to follow for the permutations.*/
	private int[][] matrix_Base;

	/** Array listing the order of the elements to be shown according probability occurrence.*/
	private String[] orderElements;

	/** A List with all rules to be applied. see IRule.*/
	private List<IRule> rules;
	private MolecularFormulaRange mfRange;
	private Double charge;
	private Double tolerance;

	
	/**
	 * Construct an instance of MassToFormulaTool. It is necessary because different
	 * matrix have to build. Furthermore the default restrictions are initiated.
	 * 
	 * @see #setDefaultRestrictions()
	 */
	public MassToFormulaTool(IChemObjectBuilder builder) {
		this.builder = builder;
        logger.info("Initiate MassToForumlaTool");
		factory = AtomTypeFactory.getInstance(builder);
    	this.orderElements = generateOrderE();
		
		setDefaultRestrictions();
		
	}
	
	/**
	 * Set the restrictions that must be presents in the molecular formula.
	 * 
	 * @param rulesNew  The restrictions to impose
	 * 
	 * @see #getRestrictions()
	 * @see #setDefaultRestrictions()
	 * @see IRule
	 */
	@TestMethod("testSetRestrictions_List")
	public void setRestrictions(List<IRule> rulesNew)  throws CDKException {
		
		Iterator<IRule> itRules = rulesNew.iterator();
		while(itRules.hasNext()){
			IRule rule = itRules.next();
			if(rule instanceof ElementRule){
				mfRange = (MolecularFormulaRange) ((Object[])rule.getParameters())[0];
				 
				 //removing the rule
				 Iterator<IRule> oldRuleIt = rules.iterator();
				 while(oldRuleIt.hasNext()){
					 IRule oldRule = oldRuleIt.next();
					 if(oldRule instanceof ElementRule){
						 rules.remove(oldRule);
						 rules.add(rule);
						 break;
					 }
				 }
				 this.matrix_Base = getMatrix(mfRange.getIsotopeCount());
			}else if(rule instanceof ChargeRule){
				this.charge = (Double) ((Object[])rule.getParameters())[0];
				
				 //removing the rule
				 Iterator<IRule> oldRuleIt = rules.iterator();
				 while(oldRuleIt.hasNext()){
					 IRule oldRule = oldRuleIt.next();
					 if(oldRule instanceof ChargeRule){
						 rules.remove(oldRule);
						 rules.add(rule);
						 break;
					 }
				 }
			}else if(rule instanceof ToleranceRangeRule){
				this.tolerance = (Double) ((Object[])rule.getParameters())[1];
				//removing the rule
				 Iterator<IRule> oldRuleIt = rules.iterator();
				 while(oldRuleIt.hasNext()){
					 IRule oldRule = oldRuleIt.next();
					 if(oldRule instanceof ToleranceRangeRule){
						 rules.remove(oldRule);
						 rules.add(rule);
						 break;
					 }
				 }
			}else{
				rules.add(rule);
			}
			
		}
	}
	
	/**
	 * Get the restrictions that must be presents in the molecular formula.
	 * 
	 * @return The restrictions to be imposed
	 * 
	 * @see #setDefaultRestrictions()
	 */
	@TestMethod("testGetRestrictions")
	public List<IRule> getRestrictions(){
		return this.rules;
	}
	
	/**
	 * Set the default restrictions that must be presents in the molecular formula.
	 * 
	 * @see #getRestrictions()
	 */
	@TestMethod("testSetDefaultRestrictions")
	public void setDefaultRestrictions(){
		try {
			callDefaultRestrictions();
		} catch (CDKException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create the default restrictions. They are:<p>
	 * 
	 * The major isotopes = C, H, O and N<p>
	 * Charge = 0.0, indicating neutral compound<p>
	 * Tolerance = 0.05 amu<p>
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws CDKException 
	 * @throws IOException 
	 * 
	 */
	private void callDefaultRestrictions() throws  CDKException, IOException {
		
		List<IRule> rules1 = new ArrayList<IRule>();
		IsotopeFactory ifac = IsotopeFactory.getInstance(builder);
		
		// restriction for occurrence elements
		MolecularFormulaRange mfRange1 = new MolecularFormulaRange();
		mfRange1.addIsotope( ifac.getMajorIsotope("C"), 0, 15);
    	mfRange1.addIsotope( ifac.getMajorIsotope("H"), 0, 15);
    	mfRange1.addIsotope( ifac.getMajorIsotope("N"), 0, 15);
    	mfRange1.addIsotope( ifac.getMajorIsotope("O"), 0, 15);
    	
    	
    	IRule rule  = new ElementRule();
		Object[] params = new Object[1];
		params[0] = mfRange1;
		rule.setParameters(params);
		
		rules1.add(rule);
		
		// occurrence for charge
		rule  = new ChargeRule(); // default 0.0 neutral
		rules1.add(rule);
		charge = (Double) ((Object[])rule.getParameters())[0];
		
		// occurrence for tolerance
		rule  = new ToleranceRangeRule(); // default 0.05
		rules1.add(rule);
		this.tolerance  = (Double) ((Object[])rule.getParameters())[1];
		
		this.matrix_Base = getMatrix(mfRange1.getIsotopeCount());
		
		this.mfRange = mfRange1;
		this.rules = rules1;
		
		
	}
	/**
	 * Method that actually does the work of extracting the molecular formula.
	 *
	 * @param  mass            molecular formula to create from the mass
	 * @return                 the filled molecular formulas as IMolecularFormulaSet
	 */
	@TestMethod("testGenerate_double")
	public IMolecularFormulaSet generate(double mass) {
		
		if(mass <= 0.0){
			logger.error("Proposed mass is not valid: ",mass);
			return null;
		}
		IMolecularFormula minimalMF = MolecularFormulaRangeManipulator.getMinimalFormula(mfRange,builder);
		IMolecularFormula maximalMF = MolecularFormulaRangeManipulator.getMaximalFormula(mfRange,builder);
		double massMim = MolecularFormulaManipulator.getTotalExactMass(minimalMF)-tolerance;
		double massMap = MolecularFormulaManipulator.getTotalExactMass(maximalMF)+tolerance;
		if(massMim > mass ||
				massMap < mass){
			logger.error("Proposed mass is out of the range: ",mass);
			return null;
		}
		
		IMolecularFormulaSet molecularFormulaSet = builder.newMolecularFormulaSet();
		
		int[][] matrix = this.matrix_Base;
		int numberElements = mfRange.getIsotopeCount();
		
		
		// put IIsotope into a list
		List<IIsotope> isotopes_TO = new ArrayList<IIsotope>();
		Iterator<IIsotope> isIt = mfRange.isotopes().iterator();
		while(isIt.hasNext())
			isotopes_TO.add(isIt.next());
		
		isotopes_TO = orderList(isotopes_TO);

		for(int i = 0; i < matrix.length ; i++){
			
			/*constructing initial combinations*/
			int[] value_In = new int[numberElements];
			for(int j= 0; j < numberElements ; j++){
				if(matrix[i][j] == 0)
					value_In[j] = 0;
				else
					value_In[j] = 1;
			}
			
			/*find number of element to combine*/
			int count_E = 0;
			ArrayList<Integer> elem_Pos = new ArrayList<Integer>();
			for(int j= 0 ; j< matrix[1].length; j++)
				if(value_In[j] != 0){
					count_E++;
					elem_Pos.add(j);
				}
			
			
			boolean flag = true;
			/*first position those first starting at the left*/
			int possChan = 0; 
			String lastMFString = "";
			while(flag){
				
//				// print all combinations --------------------------------------------------
//				System.out.print(elem_Pos.get(possChan).intValue()+">");
//				for(int j= 0 ; j< matrix[1].length; j++)
//					System.out.print(isotopes_TO.get(j).getSymbol()+value_In[j]+"-");
//				System.out.println();
//				// print all combinations --------------------------------------------------
				
				// control if some of the element is contained. E.g. C(1-3)H(1-3) 
				// the matrix 01 or 10 can not exist
				boolean flagBreak = false;
				for(int j= 0 ; j< matrix[1].length; j++){
					int min = mfRange.getIsotopeCountMin(isotopes_TO.get(j));
					if(value_In[j] == 0)
						if(min != 0)
							flagBreak = true;
				}
				if(flagBreak)
					break;
				
				
				/*Find max occurence given a mass for a element with minimal elements*/
				int occurence = getMaxOccurence(mass, elem_Pos.get(possChan).intValue(),value_In,isotopes_TO);

				/*at least one*/
				if (occurence == 0)
					break;
				
				int maxx = mfRange.getIsotopeCountMax(isotopes_TO.get(elem_Pos.get(possChan).intValue()));
				int minn = mfRange.getIsotopeCountMin(isotopes_TO.get(elem_Pos.get(possChan).intValue()));
				
				/*restriction of the number of max and min number for a element*/
				if(occurence < minn | maxx < occurence){
					/* when is not in the occurrence that means that we have to
					 * restart one value to the predecessor.*/
					
					if (possChan < elem_Pos.size()-1){
						/*Means that is possible to fit the next*/
						if (maxx < occurence)
							value_In[elem_Pos.get(possChan).intValue()] = maxx;
						possChan++;
						
					}else{
						boolean foundZ = false;
						for(int z= possChan-1; z >= 0 ; z--){
							if (value_In[elem_Pos.get(z).intValue()] != 1){
								possChan = z;
								foundZ = true;
								int newValue = value_In[elem_Pos.get(possChan).intValue()]-1;
								
								value_In[elem_Pos.get(possChan).intValue()] = newValue;
								for(int j= possChan+1; j < elem_Pos.size() ; j++){
									int p = elem_Pos.get(j).intValue();
									value_In[p] = 1;
								}
								possChan++;
								break;
							}
						}
						if(!foundZ)
							break;
						
					}
					
					continue;
				} /*final not occurrence*/
				
				/*set the occurrence into the matrix*/
				value_In[elem_Pos.get(possChan).intValue()] = occurence;
					
				double massT = calculateMassT(isotopes_TO,value_In);
				double diff_new = Math.abs(mass - (massT));
				
				if(diff_new < tolerance){
					IMolecularFormula myMF = getFormula(isotopes_TO,value_In);
					String newMFString = MolecularFormulaManipulator.getString(myMF);

					if(!newMFString.equals(lastMFString)){
						molecularFormulaSet.addMolecularFormula(myMF);
						lastMFString = newMFString;
					}
				}
				
				if(count_E == 1)/*only valid for the first random 1000*/
					break;
				
				if (possChan < elem_Pos.size()-1){
					/*Means that is possible to fit the next*/
//					value_In[elem_Pos.get(possChan).intValue()] = maxx;
					possChan++;
					
				}else{
					boolean foundZ = false;
					for(int z= possChan-1; z >= 0 ; z--){
						if (value_In[elem_Pos.get(z).intValue()] != 1){
							possChan = z;
							foundZ = true;
							int newValue = value_In[elem_Pos.get(possChan).intValue()]-1;
							
							value_In[elem_Pos.get(possChan).intValue()] = newValue;
							for(int j= possChan+1; j < elem_Pos.size() ; j++){
								int p = elem_Pos.get(j).intValue();
								value_In[p] = 1;
							}
							possChan++;
							break;
						}
					}
					if(!foundZ)
						break;
					
				}
			}
			
		}
		
		return returnOrdered(mass, molecularFormulaSet);
	}
	/**
	 * Put the order the List of IIsotope according the probability occurrence.
	 * 
	 * @param isotopes_TO  The List of IIsotope
	 * @return             The list of IIsotope ordered
	 */
	private List<IIsotope> orderList(List<IIsotope> isotopes_TO) {
		List<IIsotope> newOrderList = new ArrayList<IIsotope>();
		for(int i = 0 ; i < orderElements.length; i++){
			String symbol = orderElements[i];
			Iterator<IIsotope> itIso = isotopes_TO.iterator();
			while(itIso.hasNext()){
				IIsotope isotopeToCo = itIso.next();
				if(isotopeToCo.getSymbol().equals(symbol)){
					newOrderList.add(isotopeToCo);
				}
			}
		}
		return newOrderList;
	}

	/**
	 * generate the order of the Elements according probability occurrence.,
	 * beginning the C, H, O, N, Si, P, S, F, Cl, Br, I, Sn, B, Pb, Tl, Ba, In, Pd,
	 * Pt, Os, Ag, Zr, Se, Zn, Cu, Ni, Co, Fe, Cr, Ti, Ca, K, Al, Mg, Na, Ce,
	 * Hg, Au, Ir, Re, W, Ta, Hf, Lu, Yb, Tm, Er, Ho, Dy, Tb, Gd, Eu, Sm, Pm,
	 * Nd, Pr, La, Cs, Xe, Te, Sb, Cd, Rh, Ru, Tc, Mo, Nb, Y, Sr, Rb, Kr, As, 
	 * Ge, Ga, Mn, V, Sc, Ar, Ne, Be, Li, Tl, Pb, Bi, Po, At, Rn, Fr, Ra, Ac, 
	 * Th, Pa, U, Np, Pu. 
	 * 
	 * @return  Array with the elements ordered.
	 * 
	 */
	private String[] generateOrderE(){
		String[] listElements = new String[]{
				    "C", "H", "O", "N", "Si", "P", "S", "F", "Cl",
				    "Br", "I", "Sn", "B", "Pb", "Tl", "Ba", "In", "Pd",
				    "Pt", "Os", "Ag", "Zr", "Se", "Zn", "Cu", "Ni", "Co", 
				    "Fe", "Cr", "Ti", "Ca", "K", "Al", "Mg", "Na", "Ce",
				    "Hg", "Au", "Ir", "Re", "W", "Ta", "Hf", "Lu", "Yb", 
				    "Tm", "Er", "Ho", "Dy", "Tb", "Gd", "Eu", "Sm", "Pm",
				    "Nd", "Pr", "La", "Cs", "Xe", "Te", "Sb", "Cd", "Rh", 
				    "Ru", "Tc", "Mo", "Nb", "Y", "Sr", "Rb", "Kr", "As", 
				    "Ge", "Ga", "Mn", "V", "Sc", "Ar", "Ne", "Be", "Li", 
				    "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", 
				    "Th", "Pa", "U", "Np", "Pu"};
		return listElements;
	}
	/**
	 * Get the maximal occurrence of this List.
	 * 
	 * @param massTo
	 * @param element_pos
	 * @param matrix
	 * @param elemToCond_new
	 * @return                 The occurrence value
	 */
	private int getMaxOccurence(double massTo, int element_pos, int[] matrix,List<IIsotope> isoToCond_new) {
		double massIn = isoToCond_new.get(element_pos).getExactMass();
		double massToM = massTo;
		for(int i = 0; i < matrix.length ; i++)
			if (i != element_pos)
				if(matrix[i] != 0)
					massToM -= isoToCond_new.get(i).getExactMass()*matrix[i];
				
		
		int value = (int)((massToM+1)/massIn);
		return value;
	}

	/**
	 * Set the formula molecular as IMolecularFormula object.
	 *   
	 * @param elemToCond_new   List with IIsotope
	 * @param value_In         Array matrix with occurrences
	 * @return                 The IMolecularFormula
	 */
	private IMolecularFormula getFormula(List<IIsotope> isoToCond_new, int[] value_In) {
		IMolecularFormula mf = builder.newMolecularFormula();;
		for(int i = 0; i < isoToCond_new.size() ; i++){
			if(value_In[i] != 0){
				for(int j = 0 ; j < value_In[i] ; j ++)
					mf.addIsotope(isoToCond_new.get(i));
				
			}
		}
		mf = putInOrder(mf);
		return mf;
	}

	/**
	 * Put in order the elements of the molecular formula. 
	 * 
	 * @param formula The IMolecularFormula to put in order
	 * @return        IMolecularFormula object
	 */
	private IMolecularFormula putInOrder(IMolecularFormula formula) {
		IMolecularFormula new_formula = formula.getBuilder().newMolecularFormula();
		for(int i = 0 ; i < orderElements.length; i++){
			IElement element = builder.newElement(orderElements[i]);
			if(MolecularFormulaManipulator.containsElement(formula,element)){
				Iterator<IIsotope> isotopes = MolecularFormulaManipulator.getIsotopes(formula, element).iterator();
				while(isotopes.hasNext()){
					IIsotope isotope = isotopes.next();
					new_formula.addIsotope(isotope,formula.getIsotopeCount(isotope));
				}
			}
		}
//		new_mf.setCharge(charge);
		return new_formula;
	}
	/**
	 * Calculate the mass total given the elements and their respective occurrences.
	 * 
	 * @param elemToCond_new  The IIsotope to calculate
	 * @param value_In        Array matrix with occurrences
	 * @return                The sum total
	 */
	private double calculateMassT(List<IIsotope> isoToCond_new, int[] value_In) {
		double result = 0;
		for(int i = 0; i < isoToCond_new.size() ; i++){
			if(value_In[i] != 0){
				result += isoToCond_new.get(i).getExactMass()*value_In[i];
			}
		}
		return result;
	}
	
	
	
	/**
	 * Return all molecular formulas but ordered according the tolerance difference between masses.
	 * 
	 * @param  mass        The mass to analyze
	 * @param  formulaSet  The IMolecularFormulaSet to order
	 * @return             The IMolecularFormulaSet ordered
	 */
	private IMolecularFormulaSet returnOrdered(double mass, IMolecularFormulaSet formulaSet){
		IMolecularFormulaSet solutions_new = null;
		
		if(formulaSet.size() != 0){

			double valueMin = 100;
			int i_final = 0;
	        solutions_new = formulaSet.getBuilder().newMolecularFormulaSet();
			List<Integer> listI = new ArrayList<Integer>();
			for (int j = 0; j < formulaSet.size() ; j++){
				for (int i = 0; i < formulaSet.size() ; i++){
					if(listI.contains(i))
						continue;
					
					double value = MolecularFormulaManipulator.getTotalExactMass(formulaSet.getMolecularFormula(i));
					double diff = Math.abs(mass - Math.abs(value));
					if (valueMin > diff){
						valueMin = diff;
						i_final = i;
					}
	
				}
				valueMin = 100;
				solutions_new.addMolecularFormula(formulaSet.getMolecularFormula(i_final));
				listI.add(i_final);
			}
		}
		
		return solutions_new;
	}
	/**
	 * Get the corresponding matrix and create it.
	 *  
	 * @param size Size of the matrix to be created
	 * @return     the matrix with the permutations
	 */
	private int[][] getMatrix(int size){
        logger.info("Creating matrix for isotopes combination");
        int lengthM = (int) Math.pow(2, size); 
        lengthM--;// less 1 because the matrix 00000 we don't need
        
		int[][] matrix = new int[lengthM][size];

		int[] combi = new int[size];
		for(int j = 0 ; j < size ; j ++){
			combi[j] = 0;
		}
		int posChang = size - 1;
		int posRemov = size - 1;
		for(int i = 0 ; i < lengthM; i++){ 
			// cleaning to zeros
			for(int j = posRemov ; j < size ; j ++){
				combi[j] = 0;
			}
			
			combi[posChang] = 1;

			for(int j = 0 ; j < size ; j ++)
				matrix[i][j] = combi[j];

			if(posChang == size - 1){
				//find where is zero position, place to change
				for(int j = posChang ; j >= 0 ; j --){
					if(combi[j] == 0){
						posChang = j;
						posRemov = j+1;
						break;
					}
				}
			}else{
				//look for the last zero
				for(int j = posChang ; j < size ; j ++){
					if(combi[j] == 0){
						posChang = j;
					}
				}
			}
		}
		
		return matrix;
	}
}

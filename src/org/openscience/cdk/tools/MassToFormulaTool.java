/* $Revision: 8397 $ $Author: rajarshi $ $Date: 2007-06-24 05:24:27 +0200 (Sun, 24 Jun 2007) $
 *
 *  Copyright (C) 2005-2007  Miguel Rojasch <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Element;
import org.openscience.cdk.MolecularFormula;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>Tool to determine molecular formula consistent with a given accurate mass. The 
 * molecular formulas are not validate. It only consist in generate combination according
 * object (see MolecularFormulaChecker).
 * 
 * <pre>
 *   MassToFormulaTool mf = new MassToFormulaTool();
 *   double myMass = 133.004242;
 *   ArrayList<IMolecularFormula> resultsMF = mf.generate(myMass);
 * </pre>
 * 
 * <p>The elements are listed in order of probable occurrence, beginning the C, H, O then N and
 * so on.
 * <p>The elements are not listed according on difference with the proposed mass(see MFAnalyser).
 * 
 * @author     Miguel Rojas
 * 
 * @cdk.module experimental
 * @cdk.svnrev  $Revision: 9162 $
 */
public class MassToFormulaTool {

	private LoggingTool logger = new LoggingTool(MassToFormulaTool.class);
	
	/** The mass which is calculate the molecular formula. */
	private double mass;

	protected IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

	/** Elements that must be presents in the molecular formula. As default they
	 *  are O,N,C and H*/
	private IElement_Nr[] elemToCond = generateElemDefault();

	/** Mass tolerance to look for. As default 0.05 amu*/
	private double tolerance = 0.05;
	
	/** */
	AtomTypeFactory factory;

	/** charge of the compound. As default is 0, meaning neutral compound.**/
	private double charge = 0;
	
	/** matrix to follow for the permutations*/
	private int[][] matrix_Base;

	/** Array listing the order of the elements to be shown according probability occurrence*/
	private String[] orderElements;

	
	/**
	 * Construct an instance of MassToFormulaTool.
	 */
	public MassToFormulaTool() {

		factory = AtomTypeFactory.getInstance(new ChemObject().getBuilder());
		this.matrix_Base = callMatrix(elemToCond.length);
		this.orderElements = generateOrderE();
		
	}
	/**
	 * Set the elements that must be presents in the molecular formula.
	 * They will be set as default number of occurrence for each element
	 * as minimum = 0 and maximum = 9. 
	 * 
	 * @param elemToCon  IElements that must be presents
	 * @see #setElements(org.openscience.cdk.tools.MassToFormulaTool.IElement_Nr[])
	 */
	public void setElements(IElement[] elem){
		IElement_Nr[] elemToCondione_re = null;
		/*convert the IElement array to IElement_Nr which contains as default(0,9) the number of maximum
		 * and minimum the repetitions for each IElement.*/
		if((elem != null)&(elem.length > 0)){
			elemToCond = new IElement_Nr[elem.length];
			for(int i = 1; i < elemToCondione_re.length; i++)
				elemToCond[i] = new IElement_Nr(elem[i].getSymbol(),0,9);
			

			/*put in order descendant the Elements according their mass*/
			this.elemToCond = ordningElements(elemToCond);
			
			if(matrix_Base[1].length != elemToCond.length)
				this.matrix_Base = callMatrix(elemToCond.length);
		}else{
			logger.error("The list IElement object is null or not contain elements");
//			return null;
		}
			
	}
	/**
	 * Set the elements that must be presents in the molecular formula.
	 * The IElement_Nr object is subclass of IElement containing the occurrence max and min.
	 * 
	 * @param elemToCon  IElements_Nr
	 * @see #setElements(IElement[])
	 */
	public void setElements(IElement_Nr[] elemToCond){
		if((elemToCond != null)&(elemToCond.length > 0)){
			this.elemToCond = elemToCond;
			this.matrix_Base = callMatrix(elemToCond.length);
		}else
			logger.error("The list IElement_Nr object is null or not contain elements");
		

		/*put in order descendant the Elements according their mass*/
		this.elemToCond = ordningElements(elemToCond);
	}
	/**
	 * Set the charge of the molecule. As default is 0, meaning neutral compound.
	 * 
	 * @param charge The charge value
	 */
	public void setCharge(double charge){
		this.charge = charge;
	}
	/**
	 * Set the mass tolerance. As Default 0.05 uma.
	 * 
	 * @param tolerance The mass tolerance value
	 */
	public void setTolerance(double tolerance){
		this.tolerance = tolerance;
	}
	/**
	 * Method that actually does the work of extracting the molecular formula.
	 *
	 * @param  mass            molecular formula to create from the mass
	 * @return                 the filled molecular formula as ArrayList
	 */
	public List<IMolecularFormula> generate(double mass) {

		ArrayList<IMolecularFormula> solutions_found = new ArrayList<IMolecularFormula>();
		
		if(mass == 0.0){
			logger.error("Proposed mass is not valid: ",mass);
			return null;
		}else
			this.mass = mass;
		
		
		
		int[][] matrix = this.matrix_Base;
		
		for(int i = 0; i < matrix.length ; i++){
		
	
			/*constructing initial combinations*/
			int[] value_In = new int[elemToCond.length];
			for(int j= 0; j < elemToCond.length ; j++){
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
			while(flag){
				
				/*Find max occurence given a mass for a element with minimal elements*/
				int occurence = getMaxOccurence(mass, elem_Pos.get(possChan).intValue(),value_In,elemToCond);
				
				/*at least one*/
				if (occurence == 0)
					break;
				
				int maxx = elemToCond[elem_Pos.get(possChan).intValue()].getOccurrenceMax();
				int minn = elemToCond[elem_Pos.get(possChan).intValue()].getOccurrenceMin();
				/*restriction of the number of max and min number for a element*/
				if(occurence < minn | maxx < occurence){
					/* when is not in the occurrence that means that we have to
					 * restart one value to the predecessor.*/
					
					/*case: 0111 which there is not more possibilities to combine*/
					boolean flagONE = true;
					for (int one = 0 ; one < elem_Pos.size(); one++)
						if ((value_In[elem_Pos.get(one).intValue()] != 1)){
							flagONE = false;
						}
					if(flagONE)
						break;
					
					if (possChan < elem_Pos.size()-1){
						/*Means that is possible to fit the next*/
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
					
				double massT = calculateMassT(elemToCond,value_In);
				double diff_new = Math.abs(mass - (massT));
				
				if(diff_new < tolerance){
					IMolecularFormula myMF = getFormula(elemToCond,value_In);
					solutions_found.add(myMF);
				}
				
				if(count_E == 1)/*only valid for the first random 1000*/
					break;
				
				if (possChan < elem_Pos.size()-1){
					/*Means that is possible to fit the next*/
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
		
		
		return returnOrdered(solutions_found);
	}
	/**
	 * Put in order the element according their mass
	 * 
	 * @param elemToCond2 The elements
	 * @return            List with the elements put in order
	 */
	private IElement_Nr[] ordningElements(IElement_Nr[] elemToCond2) {
		IElement_Nr[] elemToCond_pro = elemToCond;
		IElement_Nr[] elemToCond_new = new IElement_Nr[elemToCond.length];
		int pos = 0;
		for (int i = 0; i < elemToCond.length ; i++){
			
			double valueMax = 0;
			int j_final = 0;
			for (int j = 0 ; j < elemToCond_pro.length; j++){
				if (elemToCond_pro[j] == null)
					continue;
				
				double atomicN = elemToCond_pro[j].getMajorIsotope().getExactMass();
				if (valueMax < atomicN){
					valueMax = atomicN;
					j_final = j;
				}
			}
			elemToCond_new[pos] = (IElement_Nr)elemToCond_pro[j_final];
			elemToCond_pro[j_final] = null;
			pos++;
			
		}
		return elemToCond_new;
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
	 * 
	 * @param massTo
	 * @param element_pos
	 * @param matrix
	 * @param elemToCond_new
	 * @return
	 */
	private int getMaxOccurence(double massTo, int element_pos, int[] matrix,IElement_Nr[] elemToCond_new) {
		double massIn = elemToCond_new[element_pos].getMajorIsotope().getExactMass();
		double massToM = massTo;
		for(int i = 0; i < matrix.length ; i++)
			if (i != element_pos)
				if(matrix[i] != 0)
					massToM -= elemToCond_new[i].getMajorIsotope().getExactMass()*matrix[i];
				
		
		int value = (int)((massToM+1)/massIn);
		return value;
	}

	/**
	 * set the formula molecular as IMolecularFormula object
	 *   
	 * @param elemToCond_new
	 * @param value_In
	 * @return  The IMolecularFormula
	 */
	private IMolecularFormula getFormula(IElement_Nr[] elemToCond_new, int[] value_In) {
		IMolecularFormula mf = new MolecularFormula();;
		for(int i = 0; i < elemToCond_new.length ; i++){
			if(value_In[i] != 0){
				mf.addElement(elemToCond_new[i].getMyElement(),value_In[i]);
			}
		}
		mf = putInOrder(mf);
		return mf;
	}

	/**
	 * Put in order the elements of the molecular formula. 
	 * 
	 * @param mf The IMolecularFormula to put in order
	 * @return   IMolecularFormula object
	 */
	private IMolecularFormula putInOrder(IMolecularFormula mf) {
		IMolecularFormula new_mf = new MolecularFormula();
		for(int i = 0 ; i < orderElements.length; i++){
			IElement element = new Element(orderElements[i]);
			if(mf.contains(element)){
				new_mf.addElement(element, mf.getAtomCount(element));
			}
		}
		new_mf.setCharge(charge);
		return new_mf;
	}
	/**
	 * Calculate the mass total given the elements and their respective occurrences
	 * 
	 * @param elemToCond_new  The IElements to calculate
	 * @param value_In        The occurrences
	 * @return                The sum total
	 */
	private double calculateMassT(IElement_Nr[] elemToCond_new, int[] value_In) {
		double result = 0;
		for(int i = 0; i < elemToCond_new.length ; i++){
			if(value_In[i] != 0){
				result += elemToCond_new[i].getMajorIsotope().getExactMass()*value_In[i];
			}
		}
		return result;
	}
	/**
	 * generate all elements that will be present as default. They 
	 * are C, H, O and N.
	 * 
	 * @return The group of IElement_Nr as default
	 */
	private IElement_Nr[] generateElemDefault(){
		IElement_Nr[] elemDefault = new IElement_Nr[4];
		
		elemDefault[0] = new IElement_Nr("C",0,9);
		elemDefault[1] = new IElement_Nr("H",0,9);
		elemDefault[2] = new IElement_Nr("O",0,9);
		elemDefault[3] = new IElement_Nr("N",0,9);
		return elemDefault;
	}

	/**
	 * returns the exact mass used to calculate the molecular formula.
	 * 
	 * @return    The mass value
	 */
	public double getMass() {
		return mass;
	}
	
	/**
	 * subclass of IElement which informs about the number 
	 * of maximum and minimum of repetitive elements that will be contained in the 
	 * molecular formula.
	 * 
	 * @author Miguel Rojas
	 *
	 */
	public class IElement_Nr{
		
		IElement myElement;
		int maxi = 0;
		int mini = 0;
		private IIsotope maxIsotop;
		/**
		 * Constructor of the ,0,9.
		 * 
		 * @param element The IElement object
		 * @param nim     The number of minimum occurrences of this IElement to look for
		 * @param max     The number of maximum occurrences of this IElement to look for
		 */
		public IElement_Nr(String symbol, int min, int max){

			
			try {
				IsotopeFactory ifac = IsotopeFactory.getInstance(builder);
				myElement = ifac.getElement(symbol);
				maxIsotop = ifac.getMajorIsotope(symbol);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			maxi = max;
			mini = min;
		}
		
		/**
			
		 * return the IElement
		 * 
		 * @return The IElement
		 */
		public IElement getMyElement(){
			return myElement;
		}
		/**
		 * return the number of maximum number of this Element to look for
		 * 
		 * @return The maximum value
		 */
		public int getOccurrenceMax(){
			return maxi;
		}
		/**
		 * return the number of minimum number of this Element to look for
		 * 
		 * @return The minimum value
		 */
		public int getOccurrenceMin(){
			return mini;
		}
		/**IElement_Nr element = new IElement_Nr("C",0,9);
		
		 * Returns the most abundant (major) isotope whose Element
		 * .
		 * @return The IIsotope value
		 */
		public IIsotope getMajorIsotope(){
			return maxIsotop;
		}
	}
	
	/**
	 * return all molecular formula but ordered from difference of the ratio between masses.
	 * 
	 * @return  The molecularFormula in an ArrayList
	 */
	private ArrayList<IMolecularFormula> returnOrdered(ArrayList<IMolecularFormula> molecularFormula){
		ArrayList<IMolecularFormula> solutions_new = null;
		IsotopeFactory ifac;
		try {
			ifac = IsotopeFactory.getInstance(builder);
			if(molecularFormula.size() != 0){
				
				ArrayList<IMolecularFormula> solutions_pro = molecularFormula;
				solutions_new = new ArrayList<IMolecularFormula>();
				for (int i = 0; i < molecularFormula.size() ; i++){
					
					double valueMin = 100;
					int j_final = 0;
					for (int j = 0 ; j < solutions_pro.size() ; j++){
						IAtomContainer newAC = solutions_pro.get(j).getBuilder().newAtomContainer();
						Iterator<IElement> iterator = solutions_pro.get(j).elements();
						while(iterator.hasNext()){
							IElement element = iterator.next();
							double maxIsotop = ifac.getMajorIsotope(element.getSymbol()).getExactMass();
							int rep = solutions_pro.get(j).getAtomCount(element);
							for(int z = 0; z < rep; z++){
								IAtom atom = solutions_pro.get(j).getBuilder().newAtom(element);
								atom.setExactMass(maxIsotop);
								newAC.addAtom(atom);
							}
						}
						
						double value = AtomContainerManipulator.getTotalExactMass(newAC);

							double diff = Math.abs(mass - Math.abs(value));
							if (valueMin > diff){
								valueMin = diff;
								j_final = j;
							}
							
				    	
					}
					solutions_new.add(solutions_pro.get(j_final));
					solutions_pro.remove(j_final);
					
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return solutions_new;
	}
	/**
	 * call the corresponding matrix and create it
	 */
	private int[][] callMatrix(int size){
		 switch (size) {
	         case 1:
	             return getMatrix_1();
	         case 2:
	        	 return getMatrix_2();
	         case 3:
	        	 return getMatrix_3();
	         case 4:
	        	 return getMatrix_4();
	         case 5:
	        	 return getMatrix_5();
	         case 6:
	        	 return getMatrix_6();
	         default:
	        	 logger.error("The size of the matrix is not implemented yet.");
	             return null;
		 }
	}
	/**
	 * get the matrix the permutation for dimension 1
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_1(){
		int[][] matrix = new int[][]{{1},{1}};
		return matrix;
	}
	

	/**
	 * get the matrix the permutation for dimension 2
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_2(){
		int[][] matrix = new int[][]{
				{1,0},{0,1},{1,1}};
		return matrix;
	}
	
	/**
	 * get the matrix the permutation for dimension 3
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_3(){
		int[][] matrix = new int[][]{
				{1,0,0},{0,1,0},{0,0,1},
				{1,1,0},{1,0,1},{0,1,1},
				{1,1,1}};
		return matrix;
	}
	/**
	 * get the matrix the permutation for dimension 4
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_4(){
		int[][] matrix = new int[][]{
				{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}, // 1
				{1,1,0,0},{1,0,1,0},{1,0,0,1},{0,1,1,0}, // 2
				{0,1,0,1},{0,0,1,1},
				{0,1,1,1},{1,0,1,1},{1,1,0,1},{1,1,1,0}, // 3
				{1,1,1,1}}; // 4
		return matrix;
	}

	/**
	 * get the matrix the permutation for dimension 5
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_5(){
		int[][] matrix = new int[][]{
				{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},// 1
				{0,0,0,0,1},
				{1,1,0,0,0},{1,0,1,0,0},{1,0,0,1,0},{1,0,0,0,0},// 2
				{0,1,1,0,0},{0,1,0,1,0},{0,1,0,0,1},{0,0,1,1,0},
				{0,0,1,0,1},{0,0,0,1,1},
				{0,0,1,1,1},{0,1,0,1,1},{0,1,1,0,1},{0,1,1,1,1},// 3
				{1,0,0,1,1},{1,0,1,0,1},{1,0,1,1,0},{1,1,0,0,1},
				{1,1,0,1,0},{1,1,1,0,0},
				{0,1,1,1,1},{1,0,1,1,1},{1,1,0,1,1},{1,1,1,0,1},// 4
				{1,1,1,1,0},
				{1,1,1,1,1}}; // 5
		return matrix;
	}
	/**
	 * get the matrix the permutation for dimension 6
	 *  
	 * @return the matrix with the permutations
	 */
	private int[][] getMatrix_6(){
		int[][] matrix = new int[][]{
				{1,0,0,0,0,0},{0,1,0,0,0,0},{0,0,1,0,0,0},{0,0,0,1,0,0},// 1
				{0,0,0,0,1,0},{0,0,0,0,0,1},
				{1,1,0,0,0,0},{1,0,1,0,0,0},{1,0,0,1,0,0},{1,0,0,0,1,0},// 2
				{1,0,0,0,0,1},{0,1,1,0,0,0},{0,1,0,1,0,0},{0,1,0,0,1,0},
				{0,1,0,0,0,1},{0,0,1,1,0,0},{0,0,1,0,1,0},{0,0,1,0,0,1},
				{0,0,0,1,1,0},{0,0,0,1,0,1},{0,0,0,0,1,1},
				{1,1,1,0,0,0},{1,1,0,1,0,0},{1,1,0,0,1,0},{1,1,0,0,0,1},// 3
				{0,1,1,1,0,0},{0,1,1,0,1,0},{0,1,1,0,0,1},{0,0,1,1,1,0},
				{0,0,1,1,0,1},{0,0,0,1,1,1},
				{1,0,1,1,0,0},{1,0,1,0,1,0},{1,0,1,0,0,1},{1,0,0,1,1,0},
				{1,0,0,1,0,1},{1,0,0,0,1,1},{0,1,0,1,1,0},{0,1,0,1,0,1},
				{0,1,0,0,1,1},{0,0,1,0,1,1},
				{0,1,1,1,1,1},{1,0,1,1,1,1},{1,1,0,1,1,1},{1,1,1,0,1,1},// 4
				{1,1,1,1,0,1},{1,1,1,1,1,0},
				{0,0,1,1,1,1},{0,1,0,1,1,1},{0,1,1,0,1,1},{0,1,1,1,0,1},// 5
				{0,1,1,1,1,0},{1,0,0,1,1,1},{1,0,1,0,1,1},{1,0,1,1,0,1},
				{1,0,1,1,1,0},{1,1,0,0,1,1},{1,1,0,1,0,1},{1,1,0,1,1,0},
				{1,1,1,0,0,1},{1,1,1,0,1,0},{1,1,1,1,0,0},
				{1,1,1,1,1,1}}; // 6
		return matrix;
	}
}

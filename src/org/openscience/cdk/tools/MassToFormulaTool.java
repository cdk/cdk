/* $Revision: 8397 $ $Author: rajarshi $ $Date: 2007-06-24 05:24:27 +0200 (Sun, 24 Jun 2007) $
 *
 * Copyright (C) 1997 Guillaume Cottenceau <gcottenc@ens.insa-rennes.fr> 
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


import org.openscience.cdk.ChemObject;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.NoSuchAtomTypeException;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * Tool to determine molecular formula consistent with a given accurate mass.
 * 
 * @author     Miguel Rojas
 * 
 * @cdk.module experimental
 */
public class MassToFormulaTool {

	private LoggingTool logger = new LoggingTool(MassToFormulaTool.class);
	
	/** The mass which is calculate the molecular formula. */
	private double mass;
	
	/** The max number of solutions to be found. Default number fixed to 50*/
	private static int max_Solutions = 50;
	
	/** The molecular formulas obtained from the accurate mass.*/
	private ArrayList<String> molecularFormula;

	protected IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

	/** Elements that must be presents in the molecular formula.*/
	private IElement_Nr[] elemToCond;

	/** Mass Ratio to look for. As default 0.05*/
	private static double ratio = 0.05;
	
	AtomTypeFactory factory;

	/** charge of the compound. As default is 0 meaning neutral compound.**/
	private static int charge = 0;
	/**
	 * Construct an instance of MassToFormulaTool.
	 */
	public MassToFormulaTool() {
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * 
	 * @param  mass  Mass with which is determined the molecular formula
	 */
	public MassToFormulaTool(double mass) {
		this(mass, max_Solutions, null);
	}
	
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass  Mass with which is determined the molecular formula
	 * @param  max_Solut    Number max of solutions
	 */
	public MassToFormulaTool(double mass, int max_solut) {
		this(mass, max_solut, null);
	}
	
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass,  IElement[] elemToCondione) {
		this(mass, max_Solutions, elemToCondione);
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
//	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 */
//	public MassToFormulaTool(double mass,  int charge, IElement[] elemToCondione) {
//		this(mass, max_Solutions, charge, elemToCondione);
//	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  max_Solut       Number max of solutions to be found
//	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass, int max_Solut, IElement[] elemToCondione) {
		IElement_Nr[] elemToCondione_re = null;
		/*convert the IElement array to IElement_Nr which contains as default(0,9) the number of maximum
		 * and minimum the repetitions for each IElement.*/
		if(elemToCondione != null){
			elemToCondione_re = new IElement_Nr[elemToCondione.length];
			for(int i = 1; i < elemToCondione_re.length; i++)
				elemToCondione_re[i] = new IElement_Nr(elemToCondione[i].getSymbol(),0,9);
				
		}
		
		this.molecularFormula = analyseMF(mass, max_Solut, 0, ratio, elemToCondione_re);
	}
	/**
	 * Construct an instance of MassToFormulaTool, initialized with a mass.
	 * This constructor restricts the elements to be found and the charge.
	 * This constructor restricts the number maxim of elemental elements to be
	 * found.
	 * 
	 * @param  mass            Mass with which is determined the molecular formula
	 * @param  max_Solut       Number max of solutions to be found
	 * @param  charge          Charge of the molecule
	 * @param  ratio           Ratio between the mass of the molecular formula and the mass to apply
	 * @param  elemToCondione  Elements that must be presents
	 */
	public MassToFormulaTool(double mass, int max_Solut, int charge, double ratio, IElement_Nr[] elemToCondione) {
		this.molecularFormula = analyseMF(mass, max_Solut, charge, ratio, elemToCondione);
	}
	
	/**
	 * Method that actually does the work of extracting the molecular formula.
	 *
	 * @param  mass            molecular formula to create an AtomContainer from
	 * @param  max_Solut       Number max of solutions
	 * @param  charge          Charge of the molecule
	 * @param  elemToCondione  Elements that must be presents
	 * @param  ratio           Ratio between the mass of the molecular formula and the mass to apply
	 * @return                 the filled molecular formula as ArrayList
	 */
	private ArrayList<String> analyseMF(double m, int max_Solut, int charg, double rat, IElement_Nr[] elemToCondione) {

		ArrayList<String> solutions_found = new ArrayList<String>();
		
		if(m == 0.0){
			logger.error("Proposed mass is not a valid: ",mass);
			return null;
		}else
			mass = m;
		
		factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/valency_atomtypes.xml", new ChemObject().getBuilder());
		
		max_Solutions = max_Solut;
		ratio = rat;
		charge = charg;
		
		if(elemToCondione == null)
			elemToCond = generateElemDefault();
		else
			elemToCond = elemToCondione;
		
		/*put in order descendent the Elements according their mass*/
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
		
		for(int i = 0; i < elemToCond_new.length ; i++){
			
			/*if H is the last break*/
			if(elemToCond_new[i].getMyElement().getSymbol().equals("H"))
				break;
			
			int occurence1 = getMaxOccurence(mass, elemToCond_new[i].getMajorIsotope().getExactMass());

			int[] value_In = new int[elemToCond_new.length];
			for(int j= 1; j < elemToCond_new.length ; j++)
				value_In[j] = 0;
			
			value_In[i] = occurence1;
			for(int j = occurence1; j > 0; j--){
				
				int maxx = elemToCond_new[i].getOccurrenceMax();
				int minn = elemToCond_new[i].getOccurrenceMin();
				if(j < minn | maxx < j){
					value_In[i]--;
					continue;
				}
				
				for(int k = i+1; k < elemToCond_new.length ; k++)
					value_In[k] = 0;
				
				for(int k = i+1; k < elemToCond_new.length ; k++){
					value_In[k] = 0;
					double massT = calculateMassT(elemToCond_new,value_In);
					double diff = (mass - massT);
					int occurence2 = getMaxOccurence(diff,elemToCond_new[k].getMajorIsotope().getExactMass());
					if(occurence2 == 0){
						continue;
					}
					
					for(int s = occurence2; s > 0; s--){
						
						maxx = elemToCond_new[k].getOccurrenceMax();
						minn = elemToCond_new[k].getOccurrenceMin();
						if(s < minn | maxx < s){
							value_In[k]--;
							continue;
						}
						
						value_In[k] = s;
						double massTT = calculateMassT(elemToCond_new,value_In);
						diff = Math.abs(mass - Math.abs(massTT));
						if(diff < ratio){
							 if(isValiedMF(elemToCond_new,value_In)){ 
								 String myString = getFormulaString(elemToCond_new,value_In);
								 if(charge > 0)
									 myString = "["+myString+"]+"+charge;
								 else if(charge < 0)
									 myString =  "["+myString+"]"+charge;
								 
								 solutions_found.add(myString);
							 }
						
						}else{
							if(k == elemToCond_new.length-1)
								break;
							
							for(int l = k+1; l < elemToCond_new.length ; l++)
								value_In[l] = 0;
							
							for(int l = k+1; l < elemToCond_new.length ; l++){
								value_In[l] = 0;
								double massT3 = calculateMassT(elemToCond_new,value_In);
								double diff3 = (mass - massT3);
								int occurence3 = getMaxOccurence(diff3,elemToCond_new[l].getMajorIsotope().getExactMass());
								if(occurence3 == 0){
									continue;
								}
								
								for(int t = occurence3; t > 0; t--){
									maxx = elemToCond_new[l].getOccurrenceMax();
									minn = elemToCond_new[l].getOccurrenceMin();
									if(t < minn | maxx < t){
										value_In[l]--;
										continue;
									}
									
									value_In[l] = t;
									double massTT3 = calculateMassT(elemToCond_new,value_In);
									double diff4 = Math.abs(mass - Math.abs(massTT3));
									if(diff4 < ratio){
										 if(isValiedMF(elemToCond_new,value_In)){ 
											 String myString = getFormulaString(elemToCond_new,value_In);
											 if(charge > 0)
												 myString = "["+myString+"]+"+charge;
											 else if(charge < 0)
												 myString =  "["+myString+"]"+charge;
											 
											 solutions_found.add(myString);
										 }
									
										value_In[l]--;	
									}else{
										if(l == elemToCond_new.length-1)
											break;
									}
								}
							}
						}
					}
					value_In[k]--;
				}
		
				value_In[i]--;
				
				
			}
		}
		
		return solutions_found;
	}
	/**
	 * Validation of the molecular formula. According to notion 
	 * of connected multigraph. Corollary from 
	 * http://www.sccj.net/publications/JCCJ/v3n3/a01/text.html 
	 * 
	 * @param elemToCond_new
	 * @param value_In
	 * @return
	 */
	private boolean isValiedMF(IElement_Nr[] elemToCond_new, int[] value_In) {
		
		
		/* first validation - an odd number */
		double landaI = charge;
		try {
			for(int i = 0 ; i < elemToCond_new.length ; i++)
				if(value_In[i] != 0){
					IAtomType atomType = factory.getAtomType(elemToCond_new[i].getMyElement().getSymbol());		
					landaI += atomType.getBondOrderSum()*value_In[i];
				}
		} catch (NoSuchAtomTypeException e) {
			e.printStackTrace();
			return false;
		}
		if (landaI % 2 != 0) 
			return false;
		
		/* second validation - calculation: epsilon >= n-1 */
		double epsilon = landaI/2;
		
		int countE = charge;
		for(int i = 0 ; i < elemToCond_new.length ; i++)
			if(value_In[i] != 0)
				countE += value_In[i];
		
		if(epsilon >= countE-1)
			return true;
		
		return false;
	}

	/**
	 * Get the formula molecular as String from the the sum of the Elements
	 *   
	 * @param elemToCond_new
	 * @param value_In
	 * @return
	 */
	private String getFormulaString(IElement_Nr[] elemToCond_new, int[] value_In) {
		String result = "";
		for(int i = 0; i < elemToCond_new.length ; i++){
			if(value_In[i] != 0){
				result += elemToCond_new[i].getMyElement().getSymbol()+value_In[i];
			}
		}
		return result;
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
	 * calculate the occurrence of this Element
	 * @param element    The element to analyze
	 * @return           The occurrence
	 */
	private int getMaxOccurence(double massTo, double massIn) {
		int value = (int)((massTo+1)/massIn);
		return value;
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
	 * Returns the all molecular formulas which can have existence 
	 * looking the notion of connected multigraph. Corla from 
	 * http://www.sccj.net/publications/JCCJ/v3n3/a01/text.html 
	 *
	 * @return    The molecularFormula in an ArrayList
	 */
	public ArrayList<String> getMolecularFormula() {
		return molecularFormula;
	}

	/**
	 * return all molecular formula but ordered from difference of the ratio between masses.
	 * 
	 * @return  The molecularFormula in an ArrayList
	 */
	public ArrayList<String> getMoleculesFormulaOrned(){
		ArrayList<String> solutions_new = null;
		if(molecularFormula.size() != 0){
			
			ArrayList<String> solutions_pro = molecularFormula;
			solutions_new = new ArrayList<String>();
			for (int i = 0; i < molecularFormula.size() ; i++){
				
				double valueMax = 10;
				int j_final = 0;
				for (int j = 0 ; j < solutions_pro.size() ; j++){
					
					MFAnalyser mfa = new MFAnalyser((String)solutions_pro.get(j), new Molecule());
			    	try {
						double value = mfa.getNaturalMass();

						double diff = Math.abs(mass - Math.abs(value));
						if (valueMax > diff){
							valueMax = diff;
							j_final = j;
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
			    	
				}
				solutions_new.add(solutions_pro.get(j_final));
				solutions_pro.remove(j_final);
				
			}
		}
		return solutions_new;
	}

}

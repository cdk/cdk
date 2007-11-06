/* $Revision: 9165 $ $Author: rajarshi $ $Date: 2007-10-22 01:11:04 +0200 (Mon, 22 Oct 2007) $
 *
 *  Copyright (C) 2005-2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p> Validate a molecular formula given in IMolecularformula object. The
 * validation is based according the paper of Tobias Kind on Seven
 * Golden Rules for heuristic filetering of molecular formula (see kind2007).
 * <p> As default are applied the first rule.
 * 
 * @author         Miguel Rojas
 * 
 * @cdk.module     standard
 * @cdk.keyword    molecule, molecular formula
 */
public class MFValidator {

	private LoggingTool logger = new LoggingTool(MFValidator.class);

	/** Molecular formula not containing IElements*/
	public static int NOT_ELEMENTS = -1;
	/** Molecular formula valid*/
	public static int VALID = 0;
	/** Molecular formula no valid according rule 1*/
	public static int NO_VALID_1 = 1;
	/** Molecular formula no valid according rule 2*/
	public static int NO_VALID_2 = 2;
	/** Molecular formula no valid according rule 3*/
	public static int NO_VALID_3 = 3;
	
	/** Applying rule 1. As default true */
	private boolean applyRule_1 = true;

	/** Applying rule 2. As default false */
	private boolean applyRule_2 = true;
	
	/** Applying rule 3. As default false */
	private boolean applyRule_3 = false;

	/**
	 * Internal list of element maximum.
	 */
	protected Hashtable<String, Integer> maxNrElements;

	/**
	 * Internal list of element ratios.
	 */
	protected Hashtable<String, Double> ratiosElementsC;

	/** Accuracy on the abundance measuring isotope pattern*/
	private double accuracyAbundance = 0.1;
	/** Accuracy on the mass measuring isotope pattern*/
	private double accuracyMass = 0.01;

	/** Representation of a spectrum */
	private ArrayList<double[]> spectrum;
	

	IsotopeGenerator isotopeGe = new IsotopeGenerator(0.01);

	/**The minimum score function accuracy to look for comparing isotope patterns.
	 * As default 0.9 */
	private double minScore = 0.9;
	/**
	 * Construct an instance of MFValidar. It is initialized already
	 * with the rules to applied. As default only rules 1 and 2.
	 */
	public MFValidator() {
		generateDefaultMaxNrElements();
		generateDefaultRatiosElementsCH();
		
	}	
	
	/**
	 * Validate if a molecular formula is valid.
	 * <p> -1, if not has elements 
	 * <p> 0,  if is valid
	 * <p> 1,  if is not valid according rule 1
	 *  
	 * 
	 * @param mf The IMolecularFormula value
	 * @return   If it is valid
	 */
	public int isValid(IMolecularFormula mf){logger = new LoggingTool(this);
	
		logger.info("Generating the validity of the molecular formula");
		
		if(mf.getElementCount() == 0.0){
			logger.error("Proposed molecular formula has not elements");
			return NOT_ELEMENTS;
		}
		
		int valid = VALID;
		
		if(applyRule_1){
			if(!isValidRule_1(mf))
				return NO_VALID_1;
		}
		if(applyRule_2){
			if(!isValidRule_2(mf))
				return NO_VALID_2;
		}
		if(applyRule_3){
			if(!isValidRule_3(mf))
				return NO_VALID_3;
		}
		
		return valid;
	}

	/**
	 * Validate the rule 1. It is based on Wiley mass spectral databases.
	 * 
	 * @param mf  The IMolecularFormula object
	 * @return  TRUE, if the molecular formula pass the rule 1
	 */
	public boolean isValidRule_1(IMolecularFormula mf) {
		boolean valid = true;
		this.applyRule_1 = true;
		
		for(int i = 0 ; i < mf.getElementCount(); i++){
			IElement element = mf.getElement(i);
				Integer maxNr = this.maxNrElements.get(element.getSymbol());
				if(maxNr != null){
					if(maxNr.intValue() < mf.getAtomCount(element))
						return false;
				}
		}
		return valid;
	}
	
	/**
	 * add rule 1 to be applied. As default it is set already.
	 */
	public void addRule_1(){
		this.applyRule_1 = true;
		if(maxNrElements == null)
			generateDefaultMaxNrElements();
	}

	/**
	 * remove rule 1 to be applied. As default it is set already.
	 */
	public void removeRule_1(){
		this.applyRule_1 = false;
	}
	
	
	/**
	 * Get if the rule 1 is applied.
	 * 
	 * @return TRUE, if the rule 1 is being applied
	 */
	public boolean isAppliedRule_1(){
		return applyRule_1;
	}
	
	/**
	 * Number of maximum elements proposed according the study. Necessary for rule 1.
	 */
	private void generateDefaultMaxNrElements(){
		this.maxNrElements = new Hashtable<String, Integer>();
		this.maxNrElements.put("C", 39);
		this.maxNrElements.put("H", 72);
		this.maxNrElements.put("N", 20);
		this.maxNrElements.put("O", 20);
		this.maxNrElements.put("P", 9);
		this.maxNrElements.put("S", 10);
		this.maxNrElements.put("F", 16);
		this.maxNrElements.put("Cl", 10);
		this.maxNrElements.put("Br", 4);
		this.maxNrElements.put("Si", 8);
		
	}
	
	/**
	 * Validate the rule 2. It is based on Wiley mass spectral databases.
	 * 
	 * @param mf  The IMolecularFormula object
	 * @return  TRUE, if the molecular formula pass the rule 2
	 */
	public boolean isValidRule_2(IMolecularFormula mf) {
		boolean valid = true;
		this.applyRule_2 = true;
		
		IElement elementC = mf.getBuilder().newElement("C");
		if(!mf.contains(elementC))
			return valid;
		else{
			int nrCarb = mf.getAtomCount(elementC);
			int pos = mf.getElementNumber(elementC);
			for(int i = 0 ; i < mf.getElementCount(); i++)
				if(i != pos){
					IElement element = mf.getElement(i);
					int nrElements = mf.getAtomCount(element);
					Double maxRatio = ratiosElementsC.get(element.getSymbol());
					double result = (nrElements/nrCarb);
					if(maxRatio != null)
						if(maxRatio.doubleValue() < result)
							return false;
					
				}
		}
		
		return valid;
	}
	
	/**
	 * add rule 2 to be applied. As default it is set already.
	 */
	public void addRule_2(){
		this.applyRule_2 = true;
		if(ratiosElementsC == null)
			generateDefaultRatiosElementsCH();
	}

	/**
	 * remove rule 2 to be applied. As default it is set already.
	 */
	public void removeRule_2(){
		this.applyRule_2 = false;
	}
	
	
	/**
	 * Get if the rule 2 is applied.
	 * 
	 * @return TRUE, if the rule 2 is being applied
	 */
	public boolean isAppliedRule_2(){
		return applyRule_2;
	}
	
	/**
	 * Number of maximum elements proposed according the study. Necessary for rule 1.
	 */
	private void generateDefaultRatiosElementsCH(){
		ratiosElementsC = new Hashtable<String, Double>();
		ratiosElementsC.put("H", 6.0);
		ratiosElementsC.put("F", 1.5);
		ratiosElementsC.put("Cl", 0.8);
		ratiosElementsC.put("Br", 0.8);
		ratiosElementsC.put("N", 1.3);
		ratiosElementsC.put("O", 1.2);
		ratiosElementsC.put("P", 0.3);
		ratiosElementsC.put("S", 0.8);
		ratiosElementsC.put("Si", 0.5 );
	}
	/**
	 * Validate the rule 3. It is based on Wiley mass spectral databases.
	 * 
	 * @param mf  The IMolecularFormula object
	 * @return  TRUE, if the molecular formula pass the rule 2
	 */
	public boolean isValidRule_3(IMolecularFormula mf) {
		boolean valid = true;
		this.applyRule_3 = true;
		
		/*create a IAtomContainer*/
		Iterator<IElement> iterator = mf.elements();
		IAtomContainer atomContainer = mf.getBuilder().newAtomContainer();
		
		while(iterator.hasNext()){
			IElement element = iterator.next();
			int rep = mf.getAtomCount(element);
			for(int i = 0 ; i < rep; i++)
				atomContainer.addAtom(mf.getBuilder().newAtom(element));
		}
		IAtomContainerSet containerSet = (IAtomContainerSet)isotopeGe.getIsotopesNormalized(atomContainer);
		
		
		double thisScore = extractScore(containerSet);
		
		if(thisScore < minScore)
			return false;
		
		return valid;
	}
	

	/**
	 * Extract a score function looking for similarities between isotopes pattern
	 * 
	 * @param containerSet The IAtomContainerSet
	 * @return             The score function value
	 */
	private double extractScore(IAtomContainerSet containerSet) {
		double score = 1.0;
		for(int i = 0; i < spectrum.size(); i++){
			boolean foundPeak = false;
			double massS = spectrum.get(i)[0];
			double abundS = spectrum.get(i)[1]/100;
			Iterator<IAtomContainer> iteratorAC = containerSet.atomContainers();
			while(iteratorAC.hasNext()){
				IAtomContainer ac = iteratorAC.next();
				double mass = AtomContainerManipulator.getTotalExactMass(ac);
				
				if((mass-accuracyMass < massS)&(massS < mass+accuracyMass )){
					double maxM = 0.0;
					double minM = 0.0;
					if(mass > massS){
						maxM = mass;
						minM = massS;
					}else{
						maxM = massS;
						minM = mass;
					}
					score = score*( maxM / minM);
					foundPeak = true;
				}
				
			}
			if(!foundPeak)
				score = score*(1-abundS);
			
		}
		return score;
	}

	/**
	 * add rule 3 to be applied. As default it is set already.
	 * 
	 * @param spectrum Distribution of the isotope abundance
	 */
	public void addRule_3(ArrayList<double[]> spectrum){
		this.applyRule_3 = true;
		this.spectrum = spectrum;
	}
	
	/**
	 * remove rule 3 to be applied. As default it is set already.
	 */
	public void removeRule_3(){
		this.applyRule_3 = false;
	}
	
	/**
	 * Get if the rule 3 is applied.
	 * 
	 * @return TRUE, if the rule 2 is being applied
	 */
	public boolean isAppliedRule_3(){
		return applyRule_3;
	}

	/**
	 * Set minimum score function on the accuracy. As default 0.9
	 * 
	 * @param minScore The minimum score function accuracy to look for
	 */
	public void setScoreFunct(double minScore){
		this.minScore = minScore;
	}
	
	/**
	 * Get minimum score function on accuracy.
	 * 
	 * @return The minimum score function accuracy value
	 */
	public double getScoreFunct(){
		return this.minScore;
	}
	/**
	 * Set minimum isotope abundance measure accuracy.
	 * 
	 * @param accuracy The minimum isotope abundance accuracy to look for
	 */
	public void setAccuracyAbundance(double accuracy){
		this.accuracyAbundance = accuracy;
	}
	
	/**
	 * Get minimum isotope abundance measure accuracy.
	 * 
	 * @return The minimum isotope abundance accuracy value
	 */
	public double getAccuracyAbundance(){
		return this.accuracyAbundance;
	}
	
	/**
	 * Set minimum isotope mass measure accuracy.
	 * 
	 * @param accuracy The minimum isotope mass accuracy to look for
	 */
	public void setAccuracyMass(double accuracy){
		this.accuracyMass = accuracy;
	}
	
	/**
	 * Get minimum isotope mass measure accuracy.
	 * 
	 * @return The minimum isotope mass accuracy value
	 */
	public double getAccuracyMass(){
		return this.accuracyMass;
	}
}



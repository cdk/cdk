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
 */
package org.openscience.cdk.formula;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.LoggingTool;


/**
 * Generates all Combinatorial chemical isotopes given a structure.
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword    isotope
 * 
 */
@TestClass("org.openscience.cdk.formula.IsotopePatternGeneratorTest")
public class IsotopePatternGenerator{

	private LoggingTool logger = new LoggingTool(IsotopePatternGenerator.class);
	private IsotopeFactory isotopeFactory;

	/** Minimal abundance of the isotopes to be added in the combinatorial search.*/
	private double minAbundance = 10.0;
	
	/**
	 *  Constructor for the IsotopeGenerator.
	 */
	public IsotopePatternGenerator(){
		this(10.0);
	}
	
	/**
	 * Constructor for the IsotopeGenerator.
	 * 
	 * @param minAb Minimal abundance of the isotopes to be added 
	 * 				in the combinatorial search
	 */
	public IsotopePatternGenerator(double minAb){
		minAbundance = minAb;
        logger.info("Generating all Isotope structures with IsotopeGenerator");

		try {
			isotopeFactory = IsotopeFactory.getInstance(new ChemObject().getBuilder());
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Get all combinatorial chemical isotopes given a structure. 
	 * 
	 * @param molFor  The IMolecularFormula to start
	 * @return        A IMolecularFormulaSet containing the different combinations
	 */
    @TestMethod("testGetIsotopes_IMolecularFormula")
	public IMolecularFormulaSet getIsotopes(IMolecularFormula molFor){
		/** FormulaSet to return*/
		IMolecularFormulaSet molForSet = new MolecularFormulaSet();
        /** all isotopes found*/
		List<IIsotope> isotopicAtoms = new ArrayList<IIsotope>();
		/** Atoms with isotopes*/
		List<IAtom> atomWithIsotopes = new ArrayList<IAtom>();
		/** Atoms with isotopes*/
		List<List> isotopicAtomsV = new ArrayList<List>();
		
		/*Number of permutations*/
        int nC = 1;
        
        /*search atoms which have more than one isotope and they have the minimum abundance*/
		int countt = 0;
		Iterator<IIsotope> itI2 = molFor.isotopes();
		while(itI2.hasNext()){
			IIsotope isotope = itI2.next();
        	for(int z = 0 ; z < molFor.getIsotopeCount(isotope); z++){
	        	IAtom atom = new Atom(isotope.getSymbol());
	    		List<IIsotope> isotopicAtoms2 = new ArrayList<IIsotope>();
	        	
	        	IIsotope[] isotopes = isotopeFactory.getIsotopes(atom.getSymbol());
	        	int count = 0;
	        	for (int i = 0; i < isotopes.length; i++)
					if (isotopes[i].getNaturalAbundance() > minAbundance )
						count++;
	        		
	        		for (int i = 0; i < isotopes.length; i++){
	    				if (isotopes[i].getNaturalAbundance() > minAbundance ){
	    					isotopicAtoms.add(isotopes[i]);
	    					isotopicAtoms2.add(isotopes[i]);
	            		}
	    			}
	        		
	        		atomWithIsotopes.add(countt,atom);
	        		isotopicAtomsV.add(countt,isotopicAtoms2);
	        		countt ++;
	            	nC = nC*count;
        	}
        }
      if(isotopicAtoms.size() != 0)
      	molForSet = mixer(molFor, isotopicAtomsV, isotopicAtoms, nC);
      else
      	molForSet.addMolecularFormula(molFor);
      
      
		return orderAccordingMass(molForSet);
	}
	/**
	 * Put in order the IMolecularFormulaSet according their mass.
	 * 
	 * @param molForSet The IMolecularFormulaSet
	 * @return          The IMolecularFormulaSet ordered
	 */
	private IMolecularFormulaSet orderAccordingMass(IMolecularFormulaSet molForSet) {
		IMolecularFormulaSet newMolForSet = new MolecularFormulaSet();
		int countMFSet = molForSet.size();
		for(int i = 0 ; i < countMFSet; i++){
			double massMin = 10000;
			IMolecularFormula molForToAdd = null;
			for(int j = 0 ; j < molForSet.size(); j++){
				IMolecularFormula molFor = molForSet.getMolecularFormula(j);
				double mass = MolecularFormulaManipulator.getTotalExactMass(molFor);
				if( massMin > mass){
					massMin = mass;
					molForToAdd = molFor;
				}
			}
			newMolForSet.addMolecularFormula(molForToAdd);
			molForSet.removeMolecularFormula(molForToAdd);
		}
		return newMolForSet;
	}
	/**
	 * Combine all possible isotopes.
	 * 
	 * @param molFor           IMolecularFormula to analyze
	 * @param isotopicAtoms    An arrayList containing all isotopes
	 * @param atomWithIsotopes An arrayList containing atoms which have isotopes
	 * @param nc               Number of combinations
	 * 
	 * @return The IMolecularFormulaSet
	 */
	private IMolecularFormulaSet mixer(IMolecularFormula molFor, List<List>isotopicAtomsV, List<IIsotope> isotopicAtoms, int nC){
		IMolecularFormulaSet molForSet = new MolecularFormulaSet();
	    

		int[][] ordreComb = new int[100][isotopicAtomsV.size()];
		List<int[]> ordreCombList = new ArrayList<int[]>();
		List<String[]> atomsCombList = new ArrayList<String[]>();
		
		Hashtable<String, Integer> massV = new Hashtable<String, Integer>();
		int column[] = new int[isotopicAtomsV.size()];

		for (int j = 0; j < isotopicAtomsV.size(); j++){
			
			column[j] = 1;
		}
		// create a matrix with the necessary order
		double abundRef = 0;
		for (int i = 0; i < nC; i++){
			//+++++++++++++++++++++++++++++++++++++++++++++++++++
			/*order of the combinations between isolations*/
			int[] ordreTmp = new int[column.length];
			/*order of the Atoms as string*/
			String[] atomsTmp = new String[column.length];
			
			for (int j = 0; j < isotopicAtomsV.size(); j++)
				ordreTmp[j] = column[j];
			
			
			double massEx = calculateMass(ordreTmp,isotopicAtomsV,isotopicAtoms);
			double abund  = calculateAbund(ordreTmp,isotopicAtomsV,isotopicAtoms);
			
			boolean flag = true;
			if(i==0)
				abundRef = abund;
			else{
				double abundNor = abund/abundRef;
				if(abundNor < 0.0001 )
					flag = false;
					
			}
				
			if(flag){
				String massEx_String = reduceDigits(Double.toString(massEx));
				if(!massV.containsKey(massEx_String)){
	
					massV.put(massEx_String, 1);
					int[] ordreNewTmp = new int[column.length];
					for (int k = 0; k < isotopicAtomsV.size(); k++)
						ordreNewTmp[k] = ordreTmp[k];
					ordreCombList.add(ordreNewTmp);
					for (int j = 0; j < ordreComb[0].length; j++)
						atomsTmp[j] = ((IIsotope)isotopicAtomsV.get(j).get(0)).getSymbol();
					atomsCombList.add(atomsTmp);
				}else{
					int occurr = massV.get(massEx_String);
					massV.put(massEx_String, occurr+1);
				}
			}
			column[isotopicAtomsV.size() - 1]++;
			//+++++++++++++++++++++++++++++++++++++++++++++++++++
			
			
			// control of the end of each column
			for (int k = isotopicAtomsV.size() - 1; k >= 0; k--){
				if (column[k] > isotopicAtomsV.get(k).size()){
					column[k] = 1;
					if(k-1 >= 0)
						column[k - 1]++;
				}
			}
			
		}
		
		/*set the correct isotope for each structure*/
		for (int i = 0; i < ordreCombList.size(); i++){
			
			/*Create the IMolecularFormula*/
			IMolecularFormula molForClon  = new MolecularFormula();
			int[] ordreTmp = ordreCombList.get(i);
			String[] atomsStringTmp = atomsCombList.get(i);
			for (int j = 0; j < ordreTmp.length; j++){
					
				IIsotope isotope = new Isotope(atomsStringTmp[j]);
				
				isotope.setExactMass(((Isotope)isotopicAtomsV.get(j).get(ordreTmp[j]-1)).getExactMass());
				isotope.setNaturalAbundance(((Isotope)isotopicAtomsV.get(j).get(ordreTmp[j]-1)).getNaturalAbundance());
				
				molForClon.addIsotope(isotope);
			}
			
			/*Put the occurrence of this MolecularFormula which was found*/
			double massEx = calculateMass(ordreTmp,isotopicAtomsV,isotopicAtoms);
			String massEx_String = reduceDigits(Double.toString(massEx));
			
			double prob = massV.get(massEx_String);
			Hashtable<Object, Object> hash = new Hashtable<Object, Object>();
			hash.put("occurrence", prob);
			molForClon.setProperties(hash);
			
			molForSet.addMolecularFormula(molForClon);
		

		}
		return molForSet;
	}
	
	/**
	 * Reduce the digits after pint of this Double convert in String to 8
	 *  
	 * @param string The String to reduce
	 * @return       The String reduced
	 */
	private String reduceDigits(String string) {
		int posi = 0;
		for (int ss = 0; ss < string.length(); ss ++) {
		      if (string.charAt(ss) == '.') 
		    	  posi = ss;
		 }
		int maxSize = string.length();
		if(maxSize-posi > 9)
			return string.substring(0,posi+8);
		else
			return string;
	}

	/**
	 * Calculate of the Mass.
	 * 
	 * @param ordreTmp  
	 * @param isotopicAtoms
	 * 
	 * @return  The mass total
	 */
	private double calculateMass(int[] ordreTmp,List<List>isotopicAtomsV,List<IIsotope> isotopicAtoms) {
		double massTotal = 0;
		for (int j = 0; j < ordreTmp.length; j++){
			double mass = ((Isotope)isotopicAtomsV.get(j).get(ordreTmp[j]-1)).getExactMass();
			massTotal += mass;
		}
		return massTotal;
	}
	/**
	 * Calculate of the Abundance.
	 * 
	 * @param ordreTmp  
	 * @param isotopicAtoms
	 * 
	 * @return  The abundance total
	 */
	private double calculateAbund(int[] ordreTmp,List<List>isotopicAtomsV,List<IIsotope> isotopicAtoms) {
		double abundanceTotal = 1.0;
		for (int j = 0; j < ordreTmp.length; j++){
			double abund = ((Isotope)isotopicAtomsV.get(j).get(ordreTmp[j]-1)).getNaturalAbundance();
			abundanceTotal = abundanceTotal* abund;
			
		}
		return abundanceTotal/Math.pow(100,ordreTmp.length);
	}
}

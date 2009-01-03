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
package org.openscience.cdk.formula.rules;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
/**
 * This class validate if the occurrence of the IElements in the IMolecularFormula, for
 * metabolites, are into a maximal limit according paper: . The study is from 2 different mass spectral
 * databases and according different mass of the metabolites. The analysis don't
 * take account if the IElement is not contained in the matrix. It will be jumped. <p>
 * The rules is based from Tobias Kind paper with the title "Seven Golden Rules for heuristic 
 * filtering of molecular formula" {@cdk.cite kind2007}.
 * 
 * <p>This rule uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>database</td>
 *     <td>willey</td>
 *     <td>Mass spectral databases extraction</td>
 *   </tr>
 *   <tr>
 *     <td>massRange</td>
 *     <td>< 500</td>
 *     <td>Mass to take account</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
public class MMElementRule implements IRule{


    /** Database used. As default Willey.*/
	private Database databaseUsed = Database.WILEY;

    /** Mass range used. As default lower than 500.*/
	private RangeMass rangeMassUsed = RangeMass.Minus500;

	private HashMap<String, Integer> hashMap;

	private LoggingTool logger;

	/** A enumeration of the possible mass range
	 * according the rules */
	public static enum RangeMass {
		/** IMolecularFormula from a metabolite with a mass lower than 500 Da. */
		Minus500,
		/** IMolecularFormula from a metabolite with a mass lower than 1000 Da. */
		Minus1000,
		/** IMolecularFormula from a metabolite with a mass lower than 2000 Da. */
		Minus2000,
		/** IMolecularFormula from a metabolite with a mass lower than 3000 Da. */
		Minus3000
	}

    /** A enumeration of the possible databases
	 * according the rules */
	public static enum Database {
		/** Wiley mass spectral database. */
		WILEY,
		/** Dictionary of Natural Products Online mass spectral database. */
		DNP
	}
    /**
     *  Constructor for the MMElementRule object.
     *
     *  @throws IOException            If an error occurs when reading atom type information
     *  @throws ClassNotFoundException If an error occurs during tom typing
     */
    public MMElementRule(){
        logger = new LoggingTool(this);
        
        // initiate Hashmap default
        this.hashMap = getWisley_500();
    }

    /**
     * Sets the parameters attribute of the MMElementRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	 if (params.length > 2) 
             throw new CDKException("MMElementRule only expects maximal two parameters");
            
 	     if(params[0] != null){
         	if (!(params[0] instanceof Database) )
                 throw new CDKException("The parameter must be of type Database enum");
         	databaseUsed = (Database) params[0];
         }
 	    
 	     if(params.length > 1 && params[1] != null){
         	if (!(params[1] instanceof RangeMass) )
                 throw new CDKException("The parameter must be of type RangeMass enum");
         	rangeMassUsed  = (RangeMass) params[1];
         }
 	     
 	     if((databaseUsed == Database.DNP)&&(rangeMassUsed == RangeMass.Minus500))
 	    	this.hashMap = getDNP_500();
 	     else if((databaseUsed == Database.DNP)&&(rangeMassUsed == RangeMass.Minus1000))
  	    	this.hashMap = getDNP_1000();
 	     else if((databaseUsed == Database.DNP)&&(rangeMassUsed == RangeMass.Minus2000))
  	    	this.hashMap = getDNP_2000();
 	     else if((databaseUsed == Database.DNP)&&(rangeMassUsed == RangeMass.Minus3000))
 	    	this.hashMap = getDNP_3000();
 	     else if((databaseUsed == Database.WILEY)&&(rangeMassUsed == RangeMass.Minus500))
 	    	this.hashMap = getWisley_500();
 	     else if((databaseUsed == Database.WILEY)&&(rangeMassUsed == RangeMass.Minus1000))
  	    	this.hashMap = getWisley_1000();
 	     else if((databaseUsed == Database.WILEY)&&(rangeMassUsed == RangeMass.Minus2000))
  	    	this.hashMap = getWisley_2000();
    }

    /**
     * Gets the parameters attribute of the MMElementRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
    	// return the parameters as used for the rule validation
        Object[] params = new Object[2];
        params[0] = databaseUsed;
        params[1] = rangeMassUsed;
        return params;
    }
    
    /**
     * Validate the occurrence of this IMolecularFormula.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          An ArrayList containing 9 elements in the order described above
     */

    public double validate(IMolecularFormula formula) throws CDKException {
    	 logger.info("Start validation of ",formula);
    	 double isValid = 1.0;
    	 Iterator<IElement> itElem = MolecularFormulaManipulator.elements(formula).iterator();
    	 while(itElem.hasNext()){
    		 IElement element = itElem.next();
    	 	 int occur = MolecularFormulaManipulator.getElementCount(formula, element);
    		 if(occur > hashMap.get(element.getSymbol())){
    		    isValid = 0.0;
    			break;
    	 	 }
    	 }
    	
        return isValid;
    }
    
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the DNP database and mass lower than 500 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getDNP_500(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 29);
    	map.put("H", 72);
    	map.put("N", 10);
    	map.put("O", 18);
    	map.put("P", 4);
    	map.put("S", 7);
    	map.put("F", 15);
    	map.put("Cl", 8);
    	map.put("Br", 5);
    	
    	return map;
    }
    
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the DNP database and mass lower than 1000 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getDNP_1000(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 66);
    	map.put("H", 126);
    	map.put("N", 25);
    	map.put("O", 27);
    	map.put("P", 6);
    	map.put("S", 8);
    	map.put("F", 16);
    	map.put("Cl", 11);
    	map.put("Br", 8);
    	
    	return map;
    }
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the DNP database and mass lower than 2000 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getDNP_2000(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 115);
    	map.put("H", 236);
    	map.put("N", 32);
    	map.put("O", 63);
    	map.put("P", 6);
    	map.put("S", 8);
    	map.put("F", 16);
    	map.put("Cl", 11);
    	map.put("Br", 8);
    	
    	return map;
    }
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the DNP database and mass lower than 3000 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getDNP_3000(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 162);
    	map.put("H", 208);
    	map.put("N", 48);
    	map.put("O", 78);
    	map.put("P", 6);
    	map.put("S", 9);
    	map.put("F", 16);
    	map.put("Cl", 11);
    	map.put("Br", 8);
    	
    	return map;
    }
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the Wisley database and mass lower than 500 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getWisley_500(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 39);
    	map.put("H", 72);
    	map.put("N", 20);
    	map.put("O", 20);
    	map.put("P", 9);
    	map.put("S", 10);
    	map.put("F", 16);
    	map.put("Cl", 10);
    	map.put("Br", 4);
    	map.put("Br", 8);
    	
    	return map;
    }
    
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the Wisley database and mass lower than 1000 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getWisley_1000(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 78);
    	map.put("H", 126);
    	map.put("N", 20);
    	map.put("O", 27);
    	map.put("P", 9);
    	map.put("S", 14);
    	map.put("F", 34);
    	map.put("Cl", 12);
    	map.put("Br", 8);
    	map.put("Si", 14);
    	
    	return map;
    }
    /**
     * Get the map linking the symbol of the element and number maximum of occurrence.
     * For the analysis with the Wisley database and mass lower than 2000 Da.
     * 
     * @return The HashMap of the symbol linked with the maximum occurrence
     */
    private HashMap<String, Integer> getWisley_2000(){
    	HashMap<String, Integer> map = new HashMap<String, Integer>();
    	
    	map.put("C", 156);
    	map.put("H", 180);
    	map.put("N", 20);
    	map.put("O", 40);
    	map.put("P", 9);
    	map.put("S", 14);
    	map.put("F", 48);
    	map.put("Cl", 12);
    	map.put("Br", 10);
    	map.put("Si", 15);
    	
    	return map;
    }
}

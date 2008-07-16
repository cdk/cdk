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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.IsotopePatternGenerator;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
/**
 * This class validate if the Isotope Pattern from a given IMolecularFormula
 *  correspond with other to compare.
 * 
 *
 * <p>This rule uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>isotopePattern</td>
 *     <td>List <Double[]></td>
 *     <td>The Isotope Pattern to compare</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
public class IsotopePatternRule implements IRule{


	private LoggingTool logger;

	/** Accuracy on the mass measuring isotope pattern*/
	private double toleranceMass = 0.001;

	/** Representation of a spectrum */
	private List<double[]>  pattern;

	IsotopePatternGenerator isotopeGe;
	
    /**
     *  Constructor for the IsotopePatternRule object.
     *
     *  @throws IOException            If an error occurs when reading atom type information
     *  @throws ClassNotFoundException If an error occurs during tom typing
     */
    public IsotopePatternRule() {
    	isotopeGe = new IsotopePatternGenerator(0.10);

    	logger = new LoggingTool(this);
    }

    /**
     * Sets the parameters attribute of the IsotopePatternRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	 if (params.length != 2) 
             throw new CDKException("IsotopePatternRule expects two parameter");
         
       	 if(!(params[0] instanceof List ))
       		 throw new CDKException("The parameter one must be of type List<Double[]>");
       	 

       	 if(!(params[1] instanceof Double ))
       		 throw new CDKException("The parameter two must be of type Double");
       	
       	 pattern =  (List<double[]>) params[0];
       	 toleranceMass =  (Double) params[1];
    }

    /**
     * Gets the parameters attribute of the IsotopePatternRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
    	// return the parameters as used for the rule validation
        Object[] params = new Object[2];
        params[0] = pattern;
        params[1] = toleranceMass;
        return params;
    }

    
    /**
     * Validate the isotope pattern of this IMolecularFormula. Important, first
     * you have to add with the {@link #setParameters(Object[])} a IMolecularFormulaSet
     * which represents the isotope pattern to compare.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          A double value meaning 1.0 True, 0.0 False
     */

    public double validate(IMolecularFormula formula) throws CDKException {
    	logger.info("Start validation of ",formula);
    	
    	IMolecularFormulaSet formulaSet = isotopeGe.getIsotopes(formula);
    	double score = extractScore(formulaSet);
    	return score;
    }
	/**
	 * Extract a score function looking for similarities between isotopes patterns.
	 * In this algorithm, only the most intensively simulated isotopic peak per nominal
	 * mass have been considered and used for intensity correlation.
	 * 
	 * @param molecularFormulaSet The IMolecularFormulaSet with to compare the isotope pattern
	 * @return                    The score function value
	 */
	private double extractScore(IMolecularFormulaSet formulaSet) {
		double score = 0.0;
		
		String stringMF = MolecularFormulaManipulator.getString(formulaSet.getMolecularFormula(0));
		IMolecularFormula molecularFormulaA = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula(stringMF, formulaSet.getBuilder());
		double massA = MolecularFormulaManipulator.getTotalExactMass(molecularFormulaA);
		double windowsAccuracy = toleranceMass;
		List<Double> inteExperimUnit = new ArrayList<Double>();
		List<Double> intePatternUnit = new ArrayList<Double>(); 
		for(int i = 1 ; i < 5 ; i++){
			// looking highest intensity per nominal mass
			double inteH = 0;
			double massUnit = massA + i;
			
			// around predicted
			for(IMolecularFormula molecularFormula: formulaSet.molecularFormulas()){
				double massS = MolecularFormulaManipulator.getTotalExactMass(molecularFormula);
				if((massUnit-windowsAccuracy < massS)&(massS < massUnit + windowsAccuracy )){
					double occurrence = ((Double)molecularFormula.getProperties().get("occurrence"));
					double intensity = MolecularFormulaManipulator.getTotalNaturalAbundance(molecularFormula)*occurrence;
					if(intensity > inteH){
						inteH = intensity;
					}
				}
			}
			if(inteH != 0){
				intePatternUnit.add(inteH);
				inteH = 0;
				// around experimental
				for(int j = 0; j < pattern.size(); j++){
					double intensity = pattern.get(j)[1];
					double massS = pattern.get(j)[0];
					if((massUnit-windowsAccuracy < massS)&(massS < massUnit + windowsAccuracy )){
						if(intensity > inteH){
							inteH = intensity;
						}
					}
				}
				inteExperimUnit.add(inteH);
				
			}
			
		}
		
		double sumN = 0.0;
		for(int j = 0 ; j < intePatternUnit.size(); j++)
			sumN += intePatternUnit.get(j)*inteExperimUnit.get(j);
		
		double sumD = 0.0;
		for(Iterator<Double> it = intePatternUnit.iterator(); it.hasNext();)
			sumD += Math.pow(it.next(), 2);
		
		double normalization = sumN/sumD;
		
		if(sumN == 0)
			return 0.0;
		
		sumN = 0.0;
		for(int j = 0 ; j < intePatternUnit.size(); j++)
			sumN += Math.pow(intePatternUnit.get(j)*inteExperimUnit.get(j)/normalization,2);
		score = (1-Math.pow(sumN/sumD,0.5));
		
		return score;
	}
    
}

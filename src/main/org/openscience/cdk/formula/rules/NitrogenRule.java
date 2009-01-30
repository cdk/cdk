/*  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2008-05-11 08:06:41 +0200 (Sun, 11 May 2008) $
 *  $Revision: 10958 $
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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;
/**
 * This class validate if the rule of nitrogen is kept.
 * <p>If a compound has an odd number of nitrogen atoms, 
 * then the molecular ion (the [M]+) will have an odd mass and the value for m/e will be odd.</p>
 * <p>If a compound has no nitrogen atom or an even number of nitrogen atoms, then the m/e value of [M]+ will be even.</p>
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
 *     <td>charge</td>
 *     <td>0.0</td>
 *     <td>The Nitrogen rule of MolecularFormula</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2008-06-11
 */
public class NitrogenRule implements IRule{


	private LoggingTool logger;

    /**
     *  Constructor for the NitrogenRule object.
     */
    public NitrogenRule() {
        logger = new LoggingTool(this);
    }

    /**
     * Sets the parameters attribute of the NitrogenRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	 if (params != null) 
             throw new CDKException("NitrogenRule doesn't expect parameters");
        
    }

    /**
     * Gets the parameters attribute of the NitrogenRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
        return null;
    }

    
    /**
     * Validate the nitrogen rule of this IMolecularFormula.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          A double value meaning 1.0 True, 0.0 False
     */

    public double validate(IMolecularFormula formula) throws CDKException {
    	logger.info("Start validation of ",formula);
    	
    	double mass = MolecularFormulaManipulator.getTotalMassNumber(formula);
    	if(mass == 0)
    		return 0.0;
    	
    	int numberN = MolecularFormulaManipulator.getElementCount(formula, formula.getBuilder().newElement("N"));
    	
    	if(formula.getCharge() == null || formula.getCharge() == 0 || !isOdd(Math.abs(formula.getCharge()))){
	    	if(isOdd(mass) && isOdd(numberN)) {
	    		return 1.0;
	    	} else if(!isOdd(mass) && ( numberN == 0 || !isOdd(numberN))){
	    		return 1.0;
	    	} else
	    		return 0.0;
	    }else{
	    	if(!isOdd(mass) && isOdd(numberN)) {
	    		return 1.0;
	    	} else if(isOdd(mass) && ( numberN == 0 || !isOdd(numberN))){
	    		return 1.0;
	    	} else
	    		return 0.0;
	    }
    }
    /**
     * Determine if a integer is odd.
     * 
     * @param value The value to analyze
     * @return      True, if the integer is odd
     */
    private boolean isOdd(double value) {
    	if(value % 2 == 0)
    		return false;
        else
        	return true;
    }
    
}

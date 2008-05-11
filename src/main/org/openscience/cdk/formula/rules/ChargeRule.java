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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
/**
 * This class validate if the charge in the IMolecularFormula correspond with
 * a specific value. As default it is defined as neutral == 0.0.
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
 *     <td>The Charge of MolecularFormula</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
public class ChargeRule implements IRule{


	private LoggingTool logger;
	
	private double charge = 0.0;

    /**
     *  Constructor for the ChargeRule object.
     *
     *  @throws IOException            If an error occurs when reading atom type information
     *  @throws ClassNotFoundException If an error occurs during tom typing
     */
    public ChargeRule() {
        logger = new LoggingTool(this);
    }

    /**
     * Sets the parameters attribute of the ChargeRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	 if (params.length != 1) 
             throw new CDKException("ChargeRule expects only one parameter");
         
       	 if(!(params[0] instanceof Double))
       		 throw new CDKException("The parameter must be of type Double");
       	 
       	 charge = (Double) params[0];
    }

    /**
     * Gets the parameters attribute of the ChargeRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
    	// return the parameters as used for the rule validation
        Object[] params = new Object[1];
        params[0] = charge;
        return params;
    }

    
    /**
     * Validate the charge of this IMolecularFormula.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          A double value meaning 1.0 True, 0.0 False
     */

    public double validate(IMolecularFormula formula) throws CDKException {
    	logger.info("Start validation of ",formula);
    	
    	if (formula.getCharge() == null) {
    		return 0.0;
    	} else if(formula.getCharge() == charge) {
    		return 1.0;
    	} else {
    		return 0.0;
    	}
    }
    
}

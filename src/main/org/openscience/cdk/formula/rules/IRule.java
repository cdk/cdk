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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecularFormula;
/**
 * Interface which groups all method that validate a IMolecularFormula.
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
public interface IRule {
	    
	    /** 
	     * Sets the parameters for this rule. 
	     *
	     * Must be done before calling calculate as 
	     * the parameters influence the validation outcome.
	     *
	     * @param params An array of Object containing the parameters for this rule
	     * @throws       CDKException if invalid number of type of parameters are passed to it
	     * @see          #getParameters
	     */
	    public void setParameters(Object[] params) throws CDKException;
	    
	    /** 
	     * Returns the current parameter values.
	     *
	     * @return An array of Object containing the parameter values
	     * @see    #setParameters
	     * */
	    public Object[] getParameters();
	    
	    /**
	     * Analyze the validity for the given IMolecularFormula.
	     *
	     * @param  formula      An {@link IMolecularFormula} for which this rule
	     *                      should be analyzed
	     * @return              A double value between 0 and 1. 1 meaning 100% valid
	     *                      and 0 not valid
	     * @throws CDKException if an error occurs during the validation. See
	     *                      documentation for individual rules
	     */
	    public double validate(IMolecularFormula formula) throws CDKException;

	    
}
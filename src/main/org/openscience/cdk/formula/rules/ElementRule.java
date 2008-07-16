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
import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.MolecularFormulaRange;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * This class validate if the occurrence of the IElements in the IMolecularFormula
 * are into a limits. As default defines all elements of the periodic table with
 * a occurrence of zero to 100.
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
 *     <td>elements</td>
 *     <td>C,H,N,O</td>
 *     <td>The IELements to be analyzed</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 */
public class ElementRule implements IRule{


	private LoggingTool logger;

	private MolecularFormulaRange mfRange;

    /**
     *  Constructor for the ElementRule object.
     *
     *  @throws IOException            If an error occurs when reading atom type information
     *  @throws ClassNotFoundException If an error occurs during tom typing
     */
    public ElementRule(){
        logger = new LoggingTool(this);
    }

    /**
     * Sets the parameters attribute of the ElementRule object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	 if (params.length != 1) 
             throw new CDKException("ElementRule expects one parameters");
         
       	 if(!(params[0] instanceof MolecularFormulaRange))
       		 throw new CDKException("The parameter must be of type MolecularFormulaExpand");
    	
       	mfRange = (MolecularFormulaRange)params[0];
    }

    /**
     * Gets the parameters attribute of the ElementRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
    	// return the parameters as used for the rule validation
        Object[] params = new Object[1];
        params[0] = mfRange;
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
    	ensureDefaultOccurElements(formula.getBuilder());
    	
    	double isValid = 1.0;
    	Iterator<IElement> itElem = MolecularFormulaManipulator.elements(formula).iterator();
    	while(itElem.hasNext()){
    		IElement element = itElem.next();
    		int occur = MolecularFormulaManipulator.getElementCount(formula, element);
    		IIsotope elemIsotope = formula.getBuilder().newIsotope(element.getSymbol());
    		if((occur < mfRange.getIsotopeCountMin(elemIsotope)) 
    			|| ( occur > mfRange.getIsotopeCountMax(elemIsotope)))
    		{
    			isValid = 0.0;
    			break;
    		}
    	}
    	
        return isValid;
    }
    
    /**
     * Initiate the MolecularFormulaExpand with the maximum and minimum occurrence of the Elements.
     * In this case all elements of the periodic table are loaded.
     */
    private void ensureDefaultOccurElements(IChemObjectBuilder builder){
    	if (mfRange == null) {
    		String[] elements = new String[]{
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

    		mfRange = new MolecularFormulaRange();
    		for(int i = 0; i < elements.length ; i++)
    			mfRange.addIsotope( builder.newIsotope(elements[i]), 0, 50);
    	}
    }
}

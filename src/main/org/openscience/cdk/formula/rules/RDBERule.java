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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
/**
 * <p>Ring Double Bond Equivalents (RDBE) or 
 * Double Bond Equivalents (DBE) are calculated from valence values of 
 * elements contained in a formula and should tell the number of bonds - or rings.
 *  Since this formula will fail for MFs with higher valence states such as 
 *  N(V), P(V), S(IV) or S(VI), this method will focus on the lowest valence state for these elements.</p>
 *  <p>The equation used is: D = 1 + [0.5 SUM_i(N_i(V_I-2))]</p>
 *  <p>where D is the unsaturation, i is the total number of different elements in the composition, N_i the number
 *  of atoms of element i, and Vi is the common valence of the atom i.</p>
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
 *     <td>The RDBE rule of MolecularFormula</td>
 *   </tr>
 * </table>
 * 
 * @cdk.module  formula
 * @author      miguelrojasch
 * @cdk.created 2008-06-11
 */
public class RDBERule implements IRule{

	private static Map<String,Integer> oxidationStateTable = null;

	private LoggingTool logger;
	private double min = -0.5;
	private double max = 30;

    /**
     *  Constructor for the RDBE object.
     */
    public RDBERule() {
        logger = new LoggingTool(this);
        createTable();
    }

    
	/**
     * Sets the parameters attribute of the RDBE object.
     *
     * @param params          The new parameters value
     * @throws CDKException   Description of the Exception
     * 
     * @see                   #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
    	if (params.length != 2) 
            throw new CDKException("RDBERule expects two parameters");
        
      	 if(!(params[0] instanceof Double))
      		 throw new CDKException("The 1 parameter must be of type Double");

      	 if(!(params[1] instanceof Double))
      		 throw new CDKException("The 2 parameter must be of type Double");
      	 
      	min = (Double)params[0];
      	max = (Double)params[1];
        
    }

    /**
     * Gets the parameters attribute of the RDBRule object.
     *
     * @return The parameters value
     * @see    #setParameters
     */
    public Object[] getParameters() {
    	// return the parameters as used for the rule validation
        Object[] params = new Object[2];
        params[0] = min;
        params[1] = max;
        return params;
    }

    
    /**
     * Validate the RDBRule of this IMolecularFormula.
     *
     * @param formula   Parameter is the IMolecularFormula
     * @return          A double value meaning 1.0 True, 0.0 False
     */

    public double validate(IMolecularFormula formula) throws CDKException {
    	logger.info("Start validation of ",formula);
    	
    	double RDBE = getRDBEValue(formula);
    	
		if(min <= RDBE && RDBE <= 30)
    		return 1.0;
    	else
    		return 0.0;
    	
    }

    /**
     * Method to extract the Ring Double Bond Equivalents (RDB) value.
     * 
     * @param formula The IMolecularFormula object
     * @return        The RDBE value
     */
	public double getRDBEValue(IMolecularFormula formula) {
		double RDBE = 0.0;
		for(Iterator<IIsotope> it = formula.isotopes(); it.hasNext();){
    		IIsotope isotope = it.next();
    		int valence = getOxidationState(formula.getBuilder().newAtom(isotope.getSymbol()));
    		double value = (valence-2)*formula.getIsotopeCount(isotope)/2;
    		RDBE += value;
    	}
    	
    	RDBE += 1;
		return RDBE;
	}

	/**
	 * Get the common oxidation state given a atom.
	 * 
	 * @param newAtom The IAtom
	 * @return        The oxidation state value
	 */
	private int getOxidationState(IAtom newAtom) {
		return oxidationStateTable.get(newAtom.getSymbol());
	}
	/**
     * Create the table with the common oxidation states
     */
    private void createTable() {
    	if (oxidationStateTable == null) {
            oxidationStateTable = new HashMap<String,Integer>();
            oxidationStateTable.put("H", 1);
//            oxidationStateTable.put("Li", 1);
//            oxidationStateTable.put("Be", 2);
//            oxidationStateTable.put("B", 3);
            oxidationStateTable.put("C", 4);
            oxidationStateTable.put("N", 3);
            oxidationStateTable.put("O", 2);
            oxidationStateTable.put("F", 1);
//            oxidationStateTable.put("Na", 1);
//            oxidationStateTable.put("Mg", 2);
//            oxidationStateTable.put("Al", 3);
            oxidationStateTable.put("Si", 4);
            oxidationStateTable.put("P", 3);
            oxidationStateTable.put("S", 2);
            oxidationStateTable.put("Cl", 1);
//            oxidationStateTable.put("K", 1);
//            oxidationStateTable.put("Ca", 2);
//            oxidationStateTable.put("Ga", 3);
//            oxidationStateTable.put("Ge", 4);
//            oxidationStateTable.put("As", 5);
//            oxidationStateTable.put("Se", 6);
//            oxidationStateTable.put("Br", 7);
//            oxidationStateTable.put("Rb", 1);
//            oxidationStateTable.put("Sr", 2);
//            oxidationStateTable.put("In", 3);
//            oxidationStateTable.put("Sn", 4);
//            oxidationStateTable.put("Sb", 5);
//            oxidationStateTable.put("Te", 6);
//            oxidationStateTable.put("I", 7);
//            oxidationStateTable.put("Cs", 1);
//            oxidationStateTable.put("Ba", 2);
//            oxidationStateTable.put("Tl", 3);
//            oxidationStateTable.put("Pb", 4);
//            oxidationStateTable.put("Bi", 5);
//            oxidationStateTable.put("Po", 6);
//            oxidationStateTable.put("At", 7);
//            oxidationStateTable.put("Fr", 1);
//            oxidationStateTable.put("Ra", 2);
//            oxidationStateTable.put("Cu", 2);
//            oxidationStateTable.put("Mn", 2);
//            oxidationStateTable.put("Co", 2);
        }
	}

}

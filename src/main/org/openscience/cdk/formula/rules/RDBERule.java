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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.*;

/**
 * <p>Ring Double Bond Equivalents (RDBE) or 
 * Double Bond Equivalents (DBE) are calculated from valence values of 
 * elements contained in a formula and should tell the number of bonds - or rings.
 *  Since this formula will fail for MFs with higher valence states such as 
 *  N(V), P(V), S(IV) or S(VI), this method will focus on the lowest valence state for these elements.</p>
 *  <p>The equation used is: D = 1 + [0.5 SUM_i(N_i(V_I-2))]</p>
 *  <p>where D is the unsaturation, i is the total number of different elements in the composition, N_i the number
 *  of atoms of element i, and Vi is the common valence of the atom i.</p>
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

	private static Map<String,int[]> oxidationStateTable = null;

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
    	
    	List<Double> RDBEList = getRDBEValue(formula);
    	for(Iterator<Double> it = RDBEList.iterator(); it.hasNext();){
    		double RDBE = it.next();
    		if(min <= RDBE && RDBE <= 30)
    			if(validate(formula, RDBE))
    				return 1.0;
    	}
    	
    	return 0.0;
    	
    }
    /**
     * Validate the ion state. It takes into account that neutral, nonradical compounds
     * always have an even-numbered pair-wiser arrangement of binding electrons signilizaded
     * by an integer DBE value. Charged compounds due to soft ionzation techniques
     * will give an odd number of binding electrons and a fractional DBE (X.05).
     * 
     * @param formula   Parameter is the IMolecularFormula
     * @param  value    The RDBE value
     * @return          True, if corresponds with 
     */
    public boolean validate(IMolecularFormula formula, double value) throws CDKException {
    	
    	double charge = 0.0;
    	
    	if(formula.getCharge() != CDKConstants.UNSET)
    		charge = formula.getCharge();
    	
    	long iPart = (long) value;
        double fPart = value - iPart;
        
        if(fPart == 0.0 && charge == 0)
        	return true;
        if(fPart != 0.0 && charge != 0)
        	return true;
        else 
        	return false;
        	
    }

    /**
     * Method to extract the Ring Double Bond Equivalents (RDB) value. It test all possible 
     * oxidation states.
     * 
     * @param formula The IMolecularFormula object
     * @return        The RDBE value
     * @see           #createTable()
     */
	public List<Double> getRDBEValue(IMolecularFormula formula) {
		List<Double> RDBEList = new ArrayList<Double>();
		// The number of combinations with repetition
		// (v+n-1)!/[n!(v-1)!]
		int nE = 0; // number of elements to change
		List<Integer> nV = new ArrayList<Integer>(); // number of valence changing
		for(Iterator<IIsotope> it = formula.isotopes().iterator(); it.hasNext();){
    		IIsotope isotope = it.next();
    		int[] valence = getOxidationState(formula.getBuilder().newAtom(isotope.getSymbol()));
    		if(valence.length != 1){
    			for(int i = 0; i < valence.length; i++){
    				nV.add(valence[i]);
    			}
    			nE += MolecularFormulaManipulator.getElementCount(formula, formula.getBuilder().newElement(isotope.getSymbol()));
    		}
		}
		
		double RDBE = 0;
		if(nE == 0){
			for(Iterator<IIsotope> it = formula.isotopes().iterator(); it.hasNext();){
	    		IIsotope isotope = it.next();
	    		int[] valence = getOxidationState(formula.getBuilder().newAtom(isotope.getSymbol()));
	    		double value = (valence[0]-2)*formula.getIsotopeCount(isotope)/2.0;
	    		RDBE += value;
	    	}	
			RDBE += 1;
	    	RDBEList.add(RDBE);
		}else{
			double RDBE_1 = 0;
			for(Iterator<IIsotope> it = formula.isotopes().iterator(); it.hasNext();){
	    		IIsotope isotope = it.next();
	    		int[] valence = getOxidationState(formula.getBuilder().newAtom(isotope.getSymbol()));
	    		double value = (valence[0]-2)*formula.getIsotopeCount(isotope)*0.5;
	    		RDBE_1 += value;
	    	}
			String[] valences = new String[nV.size()];
			for(int i = 0 ; i < valences.length; i++)
				valences[i] = Integer.toString(nV.get(i)); 
			
			Combinations c = new Combinations(valences, nE);
			while (c.hasMoreElements()) {
				double RDBE_int = 0.0;
				Object[] combo = (Object[])c.nextElement();
				for (int i = 0; i < combo.length; i++) {
		    		int value = (Integer.parseInt((String)combo[i])-2)/2;
		    		RDBE_int += value;
				}
		    	RDBE = 1 + RDBE_1 + RDBE_int;
		    	RDBEList.add(RDBE);
			}
		}
		return RDBEList;
	}

	/**
	 * Get the common oxidation state given a atom.
	 * 
	 * @param newAtom The IAtom
	 * @return        The oxidation state value
	 */
	private int[] getOxidationState(IAtom newAtom) {
		return oxidationStateTable.get(newAtom.getSymbol());
	}
	/**
     * Create the table with the common oxidation states
     */
    private void createTable() {
    	if (oxidationStateTable == null) {
            oxidationStateTable = new HashMap<String,int[]>();
            oxidationStateTable.put("H", new int[]{1});
//            oxidationStateTable.put("Li", 1);
//            oxidationStateTable.put("Be", 2);
            oxidationStateTable.put("B", new int[]{3});
            oxidationStateTable.put("C", new int[]{4});
            oxidationStateTable.put("N", new int[]{3});
            oxidationStateTable.put("O", new int[]{2});
            oxidationStateTable.put("F", new int[]{1});
            oxidationStateTable.put("Na", new int[]{1});
            oxidationStateTable.put("Mg", new int[]{2});
            oxidationStateTable.put("Al", new int[]{3});
            oxidationStateTable.put("Si", new int[]{4});
            oxidationStateTable.put("P", new int[]{3,5});
            oxidationStateTable.put("S", new int[]{2,4,6});
            oxidationStateTable.put("Cl", new int[]{1});
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
            oxidationStateTable.put("I", new int[]{1});
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

    public class Combinations{
        private Object[] inArray;
        private int n, m;
        private int[] index;
        private boolean hasMore = true;
        /**
        * Create a Combination to enumerate through all subsets of the 
        * supplied Object array, selecting m at a time.
        *
        * @param inArray the group to choose from
        * @param m int the number to select in each choice
        */
        public Combinations(Object[] inArray, int m){
            this.inArray = inArray;
            this.n = inArray.length;
            this.m = m;
            
            /**
            * index is an array of ints that keep track of the next combination to return. 

            * For example, an index on 5 things taken 3 at a time might contain {0 3 4}. 
            * This index will be followed by {1 2 3}. Initially, the index is {0 ... m - 1}.
            */
            index = new int[m];
            for (int i = 0; i < m; i++)
                index[0] = 0;
        }
        /**
        * @return true, unless we have already returned the last combination.
        */
        public boolean hasMoreElements()
        { 
            return hasMore;
        }
        /**
        * Move the index forward a notch. The algorithm finds the rightmost
        * index element that can be incremented, increments it, and then 
        * changes the elements to the right to each be 1 plus the element on their left. 
        * <p>
        * For example, if an index of 5 things taken 3 at a time is at {0 3 4}, only the 0 can
        * be incremented without running out of room. The next index is {1, 1+1, 1+2) or
        * {1, 2, 3}. This will be followed by {1, 2, 4}, {1, 3, 4}, and {2, 3, 4}.
        * <p>
        * The algorithm is from Applied Combinatorics, by Alan Tucker.
        *
        */
        private void moveIndex(){
            int i = rightmostIndexBelowMax();
            if (i >= 0){    
                index[i] = index[i] + 1; 
                for (int j = i + 1; j < m; j++)
                    index[j] = index[j-1];
            }else
            	hasMore = false;
        }
        /**
        * @return java.lang.Object, the next combination from the supplied Object array. 
        * <p>
        * Actually, an array of Objects is returned. The declaration must say just Object,
        * because the Combinations class implements Enumeration, which declares that the
        * nextElement() returns a plain Object. Users must cast the returned object to (Object[]).
        */
        public Object nextElement()
        {
            if (!hasMore)
                return null;

            Object[] out = new Object[m];
            for (int i = 0; i < m; i++){
            	out[i] = inArray[index[i]];
            }
            moveIndex();
            return out;
        }
        /**
        * @return int, the index which can be bumped up.
        */
        private int rightmostIndexBelowMax(){
            for (int i = m-1; i >= 0; i--){
            	int s = n -1;
            	if (index[i] != s)
                	return i;
            }
            return -1;
        }
    }
}

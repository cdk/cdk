/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2006-05-11 14:25:07 +0200 (Do, 11 Mai 2006) $
 * $Revision: 6221 $
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.tools;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Iterator;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IIsotope;


/**
* Generates all Combinatorial chemical isotopes given a structure..
* 
* @author         Miguel Rojas
 * @cdk.svnrev  $Revision: 9162 $
* @cdk.created    2007-03-01
*
* @cdk.keyword    isotope
 */
public class IsotopeGenerator implements java.io.Serializable{

	private LoggingTool logger;
	private IsotopeFactory isotopeFactory;
	private static final long serialVersionUID = -5513399059175488001L;

	/** Minimun abundance of the isotopo to be added in the combinatorial search.*/
	private double minAbundance = 10.0;
	/**
	 *  Constructor for the IsotopeGenerator
	 */
	public IsotopeGenerator(){
		this(10.0);
	}
	/**
	 * Constructor for the IsotopeGenerator
	 * 
	 * @param minAbundance Minimun abundance of the isotopo to be added 
	 * 						in the combinatorial search
	 */
	public IsotopeGenerator(double minAb){
		minAbundance = minAb;
        logger = new LoggingTool(this);
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
	 * get all combinatorial chemical isotopes given a structure. 
	 * 
	 * @param container
	 * @return
	 */
	public IAtomContainerSet getIsotopes(IAtomContainer container){
		IAtomContainerSet containerSet = new AtomContainerSet();
        /** all isotopes found*/
		ArrayList isotopicAtoms = new ArrayList();
		/** Atoms with isotopes*/
		ArrayList atomWithIsotopes = new ArrayList();
        /** value of the number of combinations*/
        int nC = 1;
        
        /*search atoms which have more than one isotope and they have the minimum abundance*/
		Iterator itA = container.atoms();
        while(itA.hasNext()){
        	IAtom atom = (IAtom)itA.next();
        	
        	IIsotope[] isotopes = isotopeFactory.getIsotopes(atom.getSymbol());
        	int count = 0;
    		if(isotopes.length > 1)
        	for (int i = 0; i < isotopes.length; i++)
				if (isotopes[i].getNaturalAbundance() > minAbundance )
					count++;
        		
			
        	if(count > 1){
        		for (int i = 0; i < isotopes.length; i++){
    				if (isotopes[i].getNaturalAbundance() > minAbundance ){
    					isotopicAtoms.add(isotopes[i]);
            		}
    			}
        		atomWithIsotopes.add(atom);
            	nC = nC*count;
        	}else{
        		isotopeFactory.configure(atom);
        	}
        }

        if(isotopicAtoms.size() != 0)
        	containerSet = mixer(container, isotopicAtoms, atomWithIsotopes, nC);
        else
        	containerSet.addAtomContainer(container);
        
		return containerSet;
	}
	/**
	 * combine all possible isotopos
	 * 
	 * @param atomContainer    IAtomContainer to analyze
	 * @param isotopicAtoms    An arrayList containing all isotopes
	 * @param atomWithIsotopes An arrayList containing atoms which have isotopes
	 * @param nc               Number of combinations
	 * 
	 * @return The IAtomContainerSet
	 */
	private IAtomContainerSet mixer(IAtomContainer atomContainer, ArrayList isotopicAtoms, ArrayList atomWithIsotopes, int nC){
		 IAtomContainerSet containerSet = new AtomContainerSet();
	    
		int[][] ordreComb = new int[nC][atomWithIsotopes.size()];
		IAtom[][] atomsComb = new Atom[nC][atomWithIsotopes.size()];

		int column[] = new int[atomWithIsotopes.size()];
		for (int j = 0; j < atomWithIsotopes.size(); j++)
		{
			column[j] = 1;
		}

		// create a matrix with the necessary order
		for (int i = 0; i < nC; i++){
			// add the combnation
			for (int j = 0; j < atomWithIsotopes.size(); j++)
				ordreComb[i][j] = column[j];

			for (int j = 0; j < ordreComb[0].length; j++)
				atomsComb[i][j] = (IAtom) atomWithIsotopes.get(j);
			
			column[atomWithIsotopes.size() - 1]++;
			
			// control of the end of each column
			for (int k = atomWithIsotopes.size() - 1; k >= 0; k--){
				if (column[k] > 2){
					column[k] = 1;
					if(k-1 >= 0)
						column[k - 1]++;
				}
			}
			
		}
		
		/*set the correct isotope for each structure*/
		for (int i = 0; i < nC; i++){
			try {
				IAtomContainer containerClon  = (IAtomContainer) atomContainer.clone();
				
					for (int j = 0; j < ordreComb[i].length; j++){
						
						int posAtom = atomContainer.getAtomNumber((IAtom) atomsComb[i][j]);
						int or = ordreComb[i][j]-1;
						double mass = ((Isotope)isotopicAtoms.get(or)).getExactMass();
						containerClon.getAtom(posAtom).setExactMass(mass);
						mass = containerClon.getAtom(posAtom).getExactMass();
						
					}
					containerSet.addAtomContainer(containerClon);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}


		}
		return containerSet;
	}
}

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

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.IOException;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Generates all Combinatorial chemical isotopes given a structure..
 *
 * @author Miguel Rojas
 * @cdk.svnrev $Revision: 9162 $
 * @cdk.created 2007-03-01
 * @cdk.module standard
 * @cdk.keyword isotope
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
	 * @param minAb Minimun abundance of the isotopo to be added
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
	 * Get all combinatorial chemical isotopes given a structure. 
	 * 
	 * @param container The IAtomContainer
	 * @return          The IAtomContainerSet with all isotopes combination
	 * @see #getIsotopes(IAtomContainer)
	 */
	public IAtomContainerSet getAllIsotopes(IAtomContainer container){
		IAtomContainerSet containerSet = new AtomContainerSet();
		/** Atoms with isotopes*/
		ArrayList<ArrayList<IIsotope>> isotopesT = new ArrayList<ArrayList<IIsotope>>();
        /** value of the number of combinations*/
        int nC = 1;
        
        /*search atoms which have more than one isotope and they have the minimum abundance*/
		Iterator<IAtom> itA = container.atoms();
        while(itA.hasNext()){
        	IAtom atom = itA.next();

        	IIsotope[] isotopes = isotopeFactory.getIsotopes(atom.getSymbol());
        	ArrayList<IIsotope> myIsotopes = new ArrayList<IIsotope> ();
        	int count = 0;
    		if(isotopes.length > 1)
	        	for (int i = 0; i < isotopes.length; i++){
//        			System.out.println("i:"+i);
	        
					if (isotopes[i].getNaturalAbundance() > minAbundance ){
						myIsotopes.add(isotopes[i]);
						count++;
					}
        		}
            	nC = nC*count;
//                System.out.println("nC:"+nC+","+count);
            	if(myIsotopes.size() > 0)
            		isotopesT.add(myIsotopes);
        }
//        System.out.println("Iso:"+isotopesT.size());
        if(isotopesT.size() > 0)
        	containerSet = mixer(container, isotopesT,nC);
        else
        	containerSet.addAtomContainer(container);
        
		return containerSet;
	}
	/**
	 * <p>Get all combinatorial chemical isotopes given a structure. Looks for those exact mass in
	 * the combinations are the same and are reduced and summed. Take account that now the natural 
	 * abundance you can not extract from getTotalNaturalAbundance but it is as a property into 
	 * the IAtomContainer.
	 * <p>double value = ((Double)atomContainer.getProperty("abundanceTotal")).doubleValue();
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return          The IAtomContainerSet with all isotopes combination
	 * @see #getAllIsotopes(IAtomContainer)
	 */
	public IAtomContainerSet getIsotopes(IAtomContainer atomContainer){
		
		IAtomContainerSet containerSet = getAllIsotopes(atomContainer);
		
		/* Sum of the molecular formula with the same mass*/
		Iterator<IAtomContainer> iterCS = containerSet.atomContainers();
		IAtomContainerSet atomContainerSpec = atomContainer.getBuilder().newAtomContainerSet();
		double mass = 0.0;
		while(iterCS.hasNext()){
			IAtomContainer atomContainerI = iterCS.next();
			double next_mass = AtomContainerManipulator.getTotalExactMass(atomContainerI);
			double natAbund = AtomContainerManipulator.getTotalNaturalAbundance(atomContainerI);
			if(next_mass != mass){
				Hashtable<String, Double> abundanceTotal = new Hashtable<String, Double>();
				
				abundanceTotal.put("abundanceTotal",natAbund);
				atomContainerI.setProperties(abundanceTotal);
				atomContainerSpec.addAtomContainer(atomContainerI);
				mass = next_mass;
			}else{
				IAtomContainer posteriorAC = atomContainerSpec.getAtomContainer(atomContainerSpec.getAtomContainerCount()-1);
				Double result = (Double) posteriorAC.getProperty("abundanceTotal");
				Hashtable<String, Double> abundanceTotal =  new Hashtable<String, Double>();
				abundanceTotal.put("abundanceTotal",natAbund+ result);
				posteriorAC.setProperties(abundanceTotal);
			}
		}
		return atomContainerSpec;
	}
	
	/**
	 * <p>Get all combinatorial chemical isotopes given a structure. Looks for those exact mass in
	 * the combinations are the same and are reduced and summed. Take account that now the natural 
	 * abundance you can not extract from getTotalNaturalAbundance but it is as a property into 
	 * the IAtomContainer.
	 * <p>double value = ((Double)atomContainer.getProperty("abundanceTotal")).doubleValue();
	 * <p>The abundance is normalized to the maxim abundance.
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return          The IAtomContainerSet with all isotopes combination
	 * @see #getAllIsotopes(IAtomContainer)
	 */
	public IAtomContainerSet getIsotopesNormalized(IAtomContainer atomContainer){
		
		IAtomContainerSet containerSet = getIsotopes(atomContainer);
		return normalization(containerSet);
	}
	/**
	 * Normalize the natural abundance
	 * 
	 * @param containerSet The IAtomContainer to normalize
	 * @return             The normalized IAtomContainer
	 */
	private IAtomContainerSet normalization(IAtomContainerSet containerSet) {
		/* find the maxim*/
		double max = 0.0;
		Iterator<IAtomContainer> iteratorAC = containerSet.atomContainers();
		while(iteratorAC.hasNext()){
			IAtomContainer ac = iteratorAC.next();
			double value = (Double) ac.getProperty("abundanceTotal");
			if(max < value)
				max = value;
		}
		
		iteratorAC = containerSet.atomContainers();
		while(iteratorAC.hasNext()){
			IAtomContainer ac = iteratorAC.next();
			double value = (Double) ac.getProperty("abundanceTotal");
			Hashtable<String, Double> abundanceTotal =  new Hashtable<String, Double>();
			abundanceTotal.put("abundanceTotal",value/max*100);
			ac.setProperties(abundanceTotal);
		}
		return containerSet;
	}
	/**
	 * Combine all possible isotopes. The IAtomcontainerSet are put according mass abundance.
	 * 
	 * @param atomContainer    IAtomContainer to analyze
	 * @param isotopesT        An arrayList containing all isotopes
	 * @param nC               Number of combinations
	 * 
	 * @return The IAtomContainerSet
	 */
	private IAtomContainerSet mixer(IAtomContainer atomContainer,  ArrayList<ArrayList<IIsotope>> isotopesT, int nC){
		 IAtomContainerSet containerSet = new AtomContainerSet();
	    
		 if(nC > 1000)
			 nC = 1000;
		int[][] ordreComb = new int[nC][isotopesT.size()];

			
		int column[] = new int[isotopesT.size()];
		for (int j = 0; j < isotopesT.size(); j++){
			column[j] = 0;
		}

		// create a matrix with the necessary order
		boolean flag = true;
		int columncount = 0;
		int posChanging = isotopesT.size()-1;
		while(flag){
			
			if(columncount == nC)
				break;
			
			for (int j = isotopesT.size()-1; j >= 0; j--)
				ordreComb[columncount][j] = column[j];
			
			int value = isotopesT.get(posChanging).size()-1;
			if(column[posChanging] < value)
				column[posChanging] = column[posChanging] + 1;
			else{
				boolean foundZ = false;
				for(int z= posChanging; z >= 0 ; z--){
					if (column[z] < isotopesT.get(posChanging).size()-1){
						posChanging = z+1;
						foundZ = true;
						break;
					}
				}
				for (int j = posChanging; j < isotopesT.size(); j++)
					column[j] = 0;
				
				column[posChanging-1] = column[posChanging-1] + 1;
				if(foundZ)
					posChanging =  isotopesT.size()-1;
			}
			columncount ++;
			
		}
		
		/*printing results*/
		for (int i = 0; i < nC; i++){
			System.out.print(i+">");
			for (int j = 0; j < ordreComb[i].length; j++){
				System.out.print(ordreComb[i][j]+"-");
				
			}
			System.out.println("");
		}
		
		/*set the correct isotope for each structure*/
		for (int i = 0; i < nC; i++){
			try {
				IAtomContainer containerClon  = (IAtomContainer) atomContainer.clone();
				
					for (int j = 0; j < ordreComb[i].length; j++){
						
						int posAtom = j;
						ArrayList<IIsotope> myIsotopes = isotopesT.get(posAtom);
						double mass = myIsotopes.get(ordreComb[i][j]).getExactMass();
						double abundance = myIsotopes.get(ordreComb[i][j]).getNaturalAbundance();
						containerClon.getAtom(posAtom).setExactMass(mass);
						containerClon.getAtom(posAtom).setNaturalAbundance(abundance);
						
					}
					containerSet.addAtomContainer(containerClon);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}


		}
		return putInOrder(containerSet);
	}
	
	/**
	 * Put in order the ContainerSet according their abundance mass
	 * 
	 * @param containerSet The containerSet to order
	 * @return  The IAtomContainerSet ordered
	 */
	private IAtomContainerSet putInOrder(IAtomContainerSet containerSet) {
		IAtomContainerSet newContainerSet = containerSet.getBuilder().newAtomContainerSet();
		
		int rep = containerSet.getAtomContainerCount();
		for(int i = 0 ; i < rep; i++){
			double abundance = 0.0;
			int posi = 0 ;
			for(int j = 0 ; j < containerSet.getAtomContainerCount(); j++){
				if(containerSet.getAtomContainer(j) != null){
					double new_abundance = AtomContainerManipulator.getTotalNaturalAbundance(containerSet.getAtomContainer(j));
					if(new_abundance > abundance){
						abundance = new_abundance;
						posi = j;
					}
				}
			}
			newContainerSet.addAtomContainer(containerSet.getAtomContainer(posi));
			containerSet.removeAtomContainer(containerSet.getAtomContainer(posi));
		}
		return newContainerSet;
	}
}

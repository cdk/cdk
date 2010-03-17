/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 2000-2009  Christoph Steinbeck, Stefan Kuhn<shk3@users.sf.net>
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
package org.openscience.cdk.structgen.stochastic.operator;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.math.RandomNumbersTool;
import org.openscience.cdk.structgen.stochastic.PartialFilledStructureMerger;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Modified molecular structures by applying crossover operator on a pair of parent structures 
 * and generate a pair of offspring structures. Each of the two offspring structures inherits 
 * a certain fragments from both of its parents.
 * 
 * @cdk.module structgen
 * @cdk.githash
 */
public class CrossoverMachine  
{
	PartialFilledStructureMerger pfsm;
	
	/** selects a partitioning mode*/
	int splitMode = 2;	
	/** selects a partitioning scale*/	
	int numatoms = 5;
	/** Indicates that <code>crossover</code> is using SPLIT_MODE_RADNDOM mode. */
    public static final int SPLIT_MODE_RADNDOM = 0;
    /** Indicates that <code>crossover</code> is using SPLIT_MODE_DEPTH_FIRST mode. */
    public static final int SPLIT_MODE_DEPTH_FIRST = 1;	
    /** Indicates that <code>crossover</code> is using SPLIT_MODE_BREADTH_FIRST mode. */
    public static final int SPLIT_MODE_BREADTH_FIRST = 2;
	
    /**
     * Constructs a new CrossoverMachine operator.
     */
    public CrossoverMachine()
    {
		pfsm = new PartialFilledStructureMerger();
    }
	
	/**
     * Performs the n point crossover of two {@link IAtomContainer}.
     * Precondition: The atoms in the molecules are ordered by properties to 
     * preserve (e. g. atom symbol). Due to its randomized nature, this method 
     * fails in around 3% of all cases. A CDKException with message "Could not 
     * mate these properly" will then be thrown.
     * 
     * @return The children.
     * @exception CDKException if it was not possible to form offsprings.
     */
    public List<IAtomContainer> doCrossover(IAtomContainer dad, IAtomContainer mom) throws CDKException
    {
    	int tries=0;
    	while(true){
			int dim = dad.getAtomCount();
			IAtomContainer[] redChild = new IAtomContainer[2];
			IAtomContainer[] blueChild = new IAtomContainer[2];
			
			List<Integer> redAtoms = new ArrayList<Integer>();
			List<Integer> blueAtoms = new ArrayList<Integer>();		
	
			/***randomly divide atoms into two parts: redAtoms and blueAtoms.***/
			if (splitMode==SPLIT_MODE_RADNDOM)
			{
				/*better way to randomly divide atoms into two parts: redAtoms and blueAtoms.*/
				for (int i = 0; i < dim; i++)
					redAtoms.add(Integer.valueOf(i));
				for (int i = 0; i < (dim - numatoms); i++)
				{   int ranInt = RandomNumbersTool.randomInt(0,redAtoms.size()-1);
					redAtoms.remove(Integer.valueOf(ranInt));
					blueAtoms.add(Integer.valueOf(ranInt));
				}
					 
			}
			else
			{
				/*split graph using depth/breadth first traverse*/
				ChemGraph graph = new ChemGraph(dad);
				graph.setNumAtoms(numatoms);
				if (splitMode==SPLIT_MODE_DEPTH_FIRST)
				{
					redAtoms = graph.pickDFgraph();
				}
				else
				{
					//this is SPLIT_MODE_BREADTH_FIRST
					redAtoms = graph.pickBFgraph();
				}
				
				for (int i = 0; i < dim; i++){
					Integer element = Integer.valueOf(i);
					if (!(redAtoms.contains(element)))
					{
						blueAtoms.add(element);
					}	
				}
			}	
			/*** dividing over ***/
			redChild[0] = dad.getBuilder().newAtomContainer(dad); 
			blueChild[0] = dad.getBuilder().newAtomContainer(dad); 
			redChild[1] = dad.getBuilder().newAtomContainer(mom); 
			blueChild[1] = dad.getBuilder().newAtomContainer(mom); 
			
			List<IAtom> blueAtomsInRedChild0 = new ArrayList<IAtom>();
			for (int j = 0; j < blueAtoms.size(); j++)
			{
				blueAtomsInRedChild0.add(redChild[0].getAtom((Integer)blueAtoms.get(j)));
			}
			for (int j = 0; j < blueAtomsInRedChild0.size(); j++)
			{
				redChild[0].removeAtomAndConnectedElectronContainers(blueAtomsInRedChild0.get(j));
			}
			List<IAtom> blueAtomsInRedChild1 = new ArrayList<IAtom>();
			for (int j = 0; j < blueAtoms.size(); j++)
			{
				blueAtomsInRedChild1.add(redChild[1].getAtom((Integer)blueAtoms.get(j)));
			}
			for (int j = 0; j < blueAtomsInRedChild1.size(); j++)
			{
				redChild[1].removeAtomAndConnectedElectronContainers(blueAtomsInRedChild1.get(j));
			}
			List<IAtom> redAtomsInBlueChild0 = new ArrayList<IAtom>();
			for (int j = 0; j < redAtoms.size(); j++)
			{
				redAtomsInBlueChild0.add(blueChild[0].getAtom((Integer)redAtoms.get(j)));
			}
			for (int j = 0; j < redAtomsInBlueChild0.size(); j++)
			{
				blueChild[0].removeAtomAndConnectedElectronContainers(redAtomsInBlueChild0.get(j));
			}
			List<IAtom> redAtomsInBlueChild1 = new ArrayList<IAtom>();
			for (int j = 0; j < redAtoms.size(); j++)
			{
				redAtomsInBlueChild1.add(blueChild[1].getAtom((Integer)redAtoms.get(j)));
			}
			for (int j = 0; j < redAtomsInBlueChild1.size(); j++)
			{
				blueChild[1].removeAtomAndConnectedElectronContainers(redAtomsInBlueChild1.get(j));
			}
			//if the two fragments of one and only one parent have an uneven number
			//of attachment points, we need to rearrange them
			SaturationChecker satCheck = new SaturationChecker();
			double red1attachpoints=0;
			for(int i=0;i<redChild[0].getAtomCount();i++){
				red1attachpoints += satCheck.getCurrentMaxBondOrder(redChild[0].getAtom(i),redChild[0]);
			}
			double red2attachpoints=0;
			for(int i=0;i<redChild[1].getAtomCount();i++){
				red2attachpoints += satCheck.getCurrentMaxBondOrder(redChild[1].getAtom(i),redChild[1]);
			}
			boolean isok=true;
			if(red1attachpoints % 2 ==1 ^ red2attachpoints % 2==1){
				isok=false;
				IAtomContainer firstToBalance = redChild[1];
				IAtomContainer secondToBalance = blueChild[0];
				if(red1attachpoints % 2 == 1){
					firstToBalance = redChild[0];
					secondToBalance = blueChild[1];
				}
				//we need an atom which has
				//- an uneven number of "attachment points" and
				//- an even number of outgoing bonds
				for(IAtom atom : firstToBalance.atoms()){
					if(satCheck.getCurrentMaxBondOrder(atom,firstToBalance)%2==1 && firstToBalance.getBondOrderSum(atom)%2==0){
						//we remove this from it's current container and add it to the other one
						firstToBalance.removeAtomAndConnectedElectronContainers(atom);
						secondToBalance.addAtom(atom);
						isok=true;
						break;
					}
				}
			}
			//if we have combineable fragments
			if(isok){
				//combine the fragments crosswise
				IAtomContainerSet[] newstrucs = new IAtomContainerSet[2];
				newstrucs[0] = dad.getBuilder().newAtomContainerSet();
				newstrucs[0].add(ConnectivityChecker.partitionIntoMolecules(redChild[0]));
				newstrucs[0].add(ConnectivityChecker.partitionIntoMolecules(blueChild[1]));
				newstrucs[1] = dad.getBuilder().newAtomContainerSet();
				newstrucs[1].add(ConnectivityChecker.partitionIntoMolecules(redChild[1]));
				newstrucs[1].add(ConnectivityChecker.partitionIntoMolecules(blueChild[0]));
		
				//and merge
				List<IAtomContainer> children=new ArrayList<IAtomContainer>(2);	
				for (int f = 0; f < 2; f++)
				{
					try{
						children.add(f, pfsm.generate(newstrucs[f]));
					}catch(Exception ex){
						//if children are not correct, the outer loop will repeat,
						//so we ignore this
					}
				}
				if(children.size()==2 && ConnectivityChecker.isConnected(children.get(0)) && ConnectivityChecker.isConnected(children.get(1)))
					return children;
			}
			tries++;
			if(tries>20)
				throw new CDKException("Could not mate these properly");
    	}
    } 
}

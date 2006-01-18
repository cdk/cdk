/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph.invariant;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;

/**
 * An algorithm for topological symmetry.
 * This algorithm derived from the algorithm {@cdk.cite HU94}.
 *
 *@author      Junfeng Hao
 *@cdk.created 2003-09-24
 *@cdk.dictref blue-obelisk:perceiveGraphSymmetry
 */
public class EquivalentClassPartitioner
{

	private double[][] nodeMatrix;
	private double[][] bondMatrix;
	private double[] weight;
	private double[][] adjaMatrix;
	private int[][] apspMatrix;
	private int layerNumber;
	private int nodeNumber;
	private static double LOST=0.000000000001;
	private org.openscience.cdk.tools.LoggingTool logger;


	/**
	 *  Constructor for the TopologicalEquivalentClass object
	 */
	public EquivalentClassPartitioner(){}


	/**
	 *  Constructor for the TopologicalEquivalentClass object
	 */
	public EquivalentClassPartitioner(AtomContainer atomContainer)
	{
		adjaMatrix = ConnectionMatrix.getMatrix(atomContainer);
		apspMatrix = PathTools.computeFloydAPSP(adjaMatrix);
		layerNumber=1;
		nodeNumber=atomContainer.getAtoms().length;
		for(int i=1;i<atomContainer.getAtoms().length;i++)
			for(int j=0;j<i;j++)
				if(apspMatrix[i][j]>layerNumber)layerNumber=apspMatrix[i][j];
		nodeMatrix=new double[nodeNumber][layerNumber+1];
		bondMatrix=new double[nodeNumber][layerNumber];
		weight=new double[nodeNumber+1];
		logger = new org.openscience.cdk.tools.LoggingTool(this);
	}


	/**
	 *  Get the topological equivalent class of the molecule
	 *
	 * @param atomContainer 	atoms and bonds of the molecule
	 * @return 			an array contains the automorphism partition of the molecule
	 */
	public int[] getTopoEquivClassbyHuXu(AtomContainer atomContainer) throws NoSuchAtomException
	{
		 double nodeSequence[]=prepareNode(atomContainer);
		 nodeMatrix=buildNodeMatrix(nodeSequence);
		 bondMatrix=buildBondMatrix();
		 weight=buildWeightMatrix(nodeMatrix,bondMatrix);
		 int AutomorphismPartition[]=findTopoEquivClass(weight);
		 return AutomorphismPartition;
	}


	/**
	 *  Prepare the node identifier. The purpose of this is to  increase the differentiatation
	 *  of the nodes. Detailed information please see the corresponding literature
	 *
	 * @param atomContainer		atoms and bonds of the molecule
	 * @return			an array of node identifier
	 */
	public double[] prepareNode(AtomContainer atomContainer)
	{
		org.openscience.cdk.interfaces.IAtom[] atoms=atomContainer.getAtoms();
		 double nodeSequence[]=new double[atoms.length];
		 for(int i=0;i<atoms.length;i++)
		 {
			 org.openscience.cdk.interfaces.Bond[] bonds=atomContainer.getConnectedBonds(atoms[i]);
			
			 if(bonds.length==1)
			 {
				 if(atoms[i].getSymbol().equals("C"))
				 {
					if(bonds[0].getOrder()==1.0)nodeSequence[i]=1;//CH3-
					else if(bonds[0].getOrder()==2.0)nodeSequence[i]=3;//CH2=
					else if(bonds[0].getOrder()==3.0)nodeSequence[i]=6;//CH#
				 }
				 else if(atoms[i].getSymbol().equals("O"))
				 {
					if(bonds[0].getOrder()==1.0)nodeSequence[i]=14;//HO-
					else if(bonds[0].getOrder()==2.0)nodeSequence[i]=16;//O=
				 }
				 else if(atoms[i].getSymbol().equals("N"))
				 {
					if(bonds[0].getOrder()==1.0)nodeSequence[i]=18;//NH2-
					else if(bonds[0].getOrder()==2.0)
					{
						if(atoms[i].getCharge()==-1.0)nodeSequence[i]=27;//N= contains -1 charge
						else nodeSequence[i]=20;//NH=
					}
					else if(bonds[0].getOrder()==3.0)nodeSequence[i]=23;//N#
					
				 }
				 else if(atoms[i].getSymbol().equals("S"))
				 {
					if(bonds[0].getOrder()==1.0)nodeSequence[i]=31;//HS-
					else if(bonds[0].getOrder()==2.0)nodeSequence[i]=33;//S=
				 }
				 else if(atoms[i].getSymbol().equals("P"))nodeSequence[i]=38;//PH2-
				 else if(atoms[i].getSymbol().equals("F"))nodeSequence[i]=42;//F-
				 else if(atoms[i].getSymbol().equals("Cl"))nodeSequence[i]=43;//Cl-
				 else if(atoms[i].getSymbol().equals("Br"))nodeSequence[i]=44;//Br-
				 else if(atoms[i].getSymbol().equals("I"))nodeSequence[i]=45;//I-
				 else
				 {
					 logger.debug("in case of a new node, please report this bug to cdk-devel@lists.sf.net.");
				 }
			 }
			 else if(bonds.length==2)
			 {
				 if(atoms[i].getSymbol().equals("C"))
				 {
					 if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0)
						 nodeSequence[i]=2;//-CH2-
					 else if(bonds[0].getOrder()==2.0 && bonds[1].getOrder()==2.0)
						 nodeSequence[i]=10;//=C=
					 else if((bonds[0].getOrder()==1.0 || bonds[1].getOrder()==1.0) &&
						 (bonds[0].getOrder()==2.0 || bonds[1].getOrder()==2.0))
						nodeSequence[i]=5;//-CH=
					 else if((bonds[0].getOrder()==1.0 || bonds[1].getOrder()==3.0) &&
						 (bonds[0].getOrder()==3.0 || bonds[1].getOrder()==3.0))
						nodeSequence[i]=9;//-C#
					else if(bonds[0].getOrder()==1.5 && bonds[1].getOrder()==1.5)
						nodeSequence[i]=11;//ArCH
				}
				else if(atoms[i].getSymbol().equals("N"))
				{
					if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0)
						 nodeSequence[i]=19;//-NH-
					else if(bonds[0].getOrder()==2.0 && bonds[1].getOrder()==2.0)
						nodeSequence[i]=28;//=N= with charge=-1
					else if((bonds[0].getOrder()==1.0 || bonds[1].getOrder()==1.0) &&
						 (bonds[0].getOrder()==2.0 || bonds[1].getOrder()==2.0))
						nodeSequence[i]=22;//-N=
					else if((bonds[0].getOrder()==2.0 || bonds[1].getOrder()==2.0) &&
						 (bonds[0].getOrder()==3.0 || bonds[1].getOrder()==3.0))
						nodeSequence[i]=26;//=N#
					else if((bonds[0].getOrder()==1.0 || bonds[1].getOrder()==1.0) &&
						 (bonds[0].getOrder()==3.0 || bonds[1].getOrder()==3.0))
						nodeSequence[i]=29;//-N# with charge=+1
					else if(bonds[0].getOrder()==1.5 && bonds[1].getOrder()==1.5)
						nodeSequence[i]=30;//ArN
				}
				else if(atoms[i].getSymbol().equals("O"))
				{
					if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0)
						 nodeSequence[i]=15;//-O-
					else if(bonds[0].getOrder()==1.5 && bonds[1].getOrder()==1.5)
						nodeSequence[i]=17;//ArO
				}
				else if(atoms[i].getSymbol().equals("S"))
				{
					if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0)
						 nodeSequence[i]=32;//-S-
					else if(bonds[0].getOrder()==2.0 && bonds[1].getOrder()==2.0)
						 nodeSequence[i]=35;//=S=
					else if(bonds[0].getOrder()==1.5 && bonds[1].getOrder()==1.5)
						nodeSequence[i]=37;//ArS
				}
				else if(atoms[i].getSymbol().equals("P"))
				{
					if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0)
						 nodeSequence[i]=39;//-PH-
				}
				else
				{
					logger.debug("in case of a new node, please report this bug to cdk-devel@lists.sf.net.");
				}
			 }
			 else if(bonds.length==3)
			 {
				 if(atoms[i].getSymbol().equals("C"))
				 {
					 if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0 && bonds[2].getOrder()==1.0)
						 nodeSequence[i]=4;//>C-
					 else if(bonds[0].getOrder()==2.0 || bonds[1].getOrder()==2.0 ||bonds[2].getOrder()==2.0)
						 nodeSequence[i]=8;//>C=
					 else if(bonds[0].getOrder()==1.5 && bonds[1].getOrder()==1.5 && bonds[2].getOrder()==1.5)
						nodeSequence[i]=13;//ArC
					 else if((bonds[0].getOrder()==1.5 || bonds[1].getOrder()==1.5 || bonds[2].getOrder()==1.5) &&
						 (bonds[0].getOrder()==1.0 || bonds[1].getOrder()==1.0 || bonds[2].getOrder()==1.0))
						nodeSequence[i]=12;//ArC-
				 }
				 else if(atoms[i].getSymbol().equals("N"))
				 {
					 if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0 && bonds[2].getOrder()==1.0)
						 nodeSequence[i]=21;//>N-
					 else if(bonds[0].getOrder()==1.0 || bonds[1].getOrder()==1.0 || bonds[2].getOrder()==1.0)
						 nodeSequence[i]=25;//-N(=)=
				 }
				 else if(atoms[i].getSymbol().equals("S"))
				 {
					 if(bonds[0].getOrder()==2.0 || bonds[1].getOrder()==2.0 || bonds[2].getOrder()==2.0)
						 nodeSequence[i]=34;//>S=
				 }
				 else if(atoms[i].getSymbol().equals("P"))
				 {
					 if(bonds[0].getOrder()==1.0 && bonds[1].getOrder()==1.0 && bonds[2].getOrder()==1.0)
						 nodeSequence[i]=40;//>P-
				 }
				 else
				 {
					logger.debug("in case of a new node, please report this bug to cdk-devel@lists.sf.net.");
				 }
			 }
			 else if(bonds.length==4)
			 {
				 if(atoms[i].getSymbol().equals("C"))nodeSequence[i]=7;//>C<
				 else if(atoms[i].getSymbol().equals("N"))nodeSequence[i]=24;//>N(=)-
				 else if(atoms[i].getSymbol().equals("S"))nodeSequence[i]=36;//>S(=)=
				 else if(atoms[i].getSymbol().equals("P"))nodeSequence[i]=41;//=P<-
				 else
				 {
					logger.debug("in case of a new node, please report this bug to cdk-devel@lists.sf.net.");
				 }
			 }
		 }
		 return nodeSequence;
	}


	/**
	 *  Build node Matrix
	 *
	 * @param nodeSequence	an array contains node number for each atom
	 * @return		node Matrix
	 */
	public double[][] buildNodeMatrix(double[] nodeSequence)
	{
		 int i,j,k;
		for(i=0;i<nodeNumber;i++)
		{
			nodeMatrix[i][0]=nodeSequence[i];
			for(j=1;j<=layerNumber;j++)
			{
				nodeMatrix[i][j]=0.0;
				for(k=0;k<nodeNumber;k++)
					if(apspMatrix[i][k]==j)nodeMatrix[i][j]+=nodeSequence[k];
			}
		}
		return nodeMatrix;
	}


	/**
	 *  Build trial node Matrix
	 *
	 * @param weight	an array contains the weight of atom
	 * @return		trial node matrix.
	 */
	public double[][] buildTrialNodeMatrix(double[] weight)
	{
		 int i,j,k;
		for(i=0;i<nodeNumber;i++)
		{
			nodeMatrix[i][0]=weight[i+1];
			for(j=1;j<=layerNumber;j++)
			{
				nodeMatrix[i][j]=0.0;
				for(k=0;k<nodeNumber;k++)
					if(apspMatrix[i][k]==j)nodeMatrix[i][j]+=weight[k+1];
			}
		}
		return nodeMatrix;
	}


	/**
	 *  Build bond matrix
	 *
	 * @return	bond matrix.
	 */
	public double[][] buildBondMatrix()
	{
		 int i,j,k,m;
		 for(i=0;i<nodeNumber;i++)
		 {
			 for(j=1;j<=layerNumber;j++)
			 {
				bondMatrix[i][j-1]=0.0;
				for(k=0;k<nodeNumber;k++)
				{
					if(j==1)
					{
						if(apspMatrix[i][k]==j)
							bondMatrix[i][j-1]+=adjaMatrix[i][k];
					}
					else
					{
						if(apspMatrix[i][k]==j)
						{
							for(m=0;m<nodeNumber;m++)
							{
								if(apspMatrix[i][m]==(j-1))
								{
									bondMatrix[i][j-1]+=adjaMatrix[k][m];
								}
							}
						}
					}
				}
			 }
		 }
		 return bondMatrix;
	}


	/**
	 *  Build weight array for the given node matrix and bond matrix
	 *
	 * @param nodeMatrix	array contains node information
	 * @param bondMatrix	array contains bond information
	 * @return		weight array for the node
	 */
	public double[] buildWeightMatrix(double[][] nodeMatrix,double[][] bondMatrix)
	{
		 for(int i=0;i<nodeNumber;i++)
		 {
			 weight[i+1]=nodeMatrix[i][0];
			 for(int j=0;j<layerNumber;j++)
				 weight[i+1]+=nodeMatrix[i][j+1]*bondMatrix[i][j]*Math.pow(10.0,(double)-(j+1));
		 }
		 weight[0]=0.0;
		 return weight;
	}


	/**
	 *  Get different number of the given number
	 *
	 * @param weight	array contains weight of the nodes
	 * @return		number of different weight
	 */
	public int checkDiffNumber(double[] weight)
	{
		 // Count the number of different weight
		double category[]=new double[weight.length];
		int i,j;
		int count=1;
		double t;
		category[1]=weight[1];
		for(i=2;i<weight.length;i++)
		{
			 
			 for(j=1;j<=count;j++)
			 {
				 t=weight[i]-category[j];
				 if(t<0.0)t=-t;
				 if(t<LOST)break;
			 }
			 if(j>count)
			 {
				 count+=1;
				 category[count]=weight[i];
			 }
		}
		return count;
	}


	/**
	 *  Get the final equivalent class
	 *
	 * @param weight	array contains weight of the nodes
	 * @return		an array contains the automorphism partition
	 */
	public int[] getEquivalentClass(double[] weight)
	{
		double category[]=new double[weight.length];
		int equivalentClass[]=new int[weight.length];
		int i,j;
		int count=1;
		double t;
		category[1]=weight[1];
		for(i=2;i<weight.length;i++)
		{
			 
			 for(j=1;j<=count;j++)
			 {
				 t=weight[i]-category[j];
				 if(t<0.0)t=-t;
				 if(t<LOST)break;
			 }
			 if(j>count)
			 {
				 count+=1;
				 category[count]=weight[i];
			 }
		}
		
		for(i=1;i<weight.length;i++)
			for(j=1;j<=count;j++)
			{
				t=weight[i]-category[j];
				if(t<0.0)t=-t;
				if(t<LOST)equivalentClass[i]=j;
			}
		equivalentClass[0]=count;
		return equivalentClass;
	}


	/**
	 *  Find the topological equivalent class for the given weight
	 *
	 * @param weight	array contains weight of the nodes
	 * @return		an array contains the automorphism partition
	 */
	public int[] findTopoEquivClass(double[] weight)
	{
		int trialCount,i;
		int equivalentClass[]=new int[weight.length];
		int count=checkDiffNumber(weight);
		trialCount=count;
		if(count==nodeNumber)
		{
			for(i=1;i<=nodeNumber;i++)
				equivalentClass[i]=i;
			equivalentClass[0]=count;
			return equivalentClass;
		}
		do
		{
			count=trialCount;
			double[][] trialNodeMatrix=buildTrialNodeMatrix(weight);
			double[] trialWeight=buildWeightMatrix(trialNodeMatrix,bondMatrix);
			trialCount=checkDiffNumber(trialWeight);
			if(trialCount==nodeNumber)
			{
				for(i=1;i<=nodeNumber;i++)
					equivalentClass[i]=i;
				equivalentClass[0]=count;
				return equivalentClass;
			}
			if(trialCount<=count)
			{
				equivalentClass=getEquivalentClass(weight);
				return equivalentClass;
			}
		}while(trialCount>count);
		return equivalentClass;
	 }
 }
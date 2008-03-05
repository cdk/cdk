/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.HashMap;
import java.util.Iterator;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReaction;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  
 *  This class returns the ionization potential of a Bond. It is
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values (NIST data). Up to now is
 *  only possible predict for double- or triple bonds and they are not belong to
 *  conjugated system or not adjacent to an heteroatom.
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author      Miguel Rojas
 * @cdk.created 2006-05-26
 * @cdk.module  qsarmolecular
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:ionizationPotential
 * @cdk.bug     1860497
 * @cdk.bug     1861626
 */
public class IPBondDescriptor implements IBondDescriptor {
	
	private IReactionSet reactionSet;

	/**
	 *  Constructor for the IPBondDescriptor object
	 */
	public IPBondDescriptor() {
	}
	/**
	 *  Gets the specification attribute of the IPBondDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPBondDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}

    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPBondDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }
	/**
	 *  This method calculates the ionization potential of a bond.
	 *
	 *@param  atomContainer         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IBond bond, IAtomContainer atomContainer) throws CDKException{
        IAtomContainer localClone = null;
        try {
            localClone = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Error during clone");
        }
        IBond clonedBond = localClone.getBond(atomContainer.getBondNumber(bond));
        
        String[] descriptorNames = {"DoubleResult"};
    	reactionSet = localClone.getBuilder().newReactionSet();
    	
    	double resultD = -1.0;
		boolean isTarget = false;
		double[] resultsH = null;
		
		try{
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(localClone);
			CDKHueckelAromaticityDetector.detectAromaticity(localClone);
		} catch (Exception exc){
            exc.printStackTrace();
        }
        
        if(clonedBond.getOrder() != IBond.Order.SINGLE && 
        		(localClone.getConnectedLonePairsCount(clonedBond.getAtom(0)) == 0) && /*not containing heteroatoms*/ 
        		(localClone.getConnectedLonePairsCount(clonedBond.getAtom(1)) == 0) && 
        		!clonedBond.getAtom(0).getFlag(CDKConstants.ISAROMATIC) && !clonedBond.getAtom(1).getFlag(CDKConstants.ISAROMATIC)){ /*not belonging to aromatics*/
        		
        		AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(localClone);
                Iterator acI = conjugatedPi.atomContainers();

                boolean isConjugatedPi = false;
        		boolean isConjugatedPi_withHeteroatom = false;
                
        		while(acI.hasNext()){
        			IAtomContainer ac = (IAtomContainer) acI.next();
        			if(ac.contains(clonedBond)){
        				isConjugatedPi = true;
            			
            			Iterator atoms = ac.atoms();
            			while(atoms.hasNext()){
            				IAtom atomsss = (IAtom) atoms.next();
            				
            				if(localClone.getConnectedLonePairsCount(atomsss) > 0){
            					isConjugatedPi_withHeteroatom = true;
//            					resultsH = calculateCojugatedPiSystWithHeteroDescriptor(bond, container, ac);
//                    			resultD = getPySystWithHetero(resultsH);
//            					resultD += 0.05;
//                    			isTarget = true;
                    			break;
            				}
            			}
            			
            			if(!isConjugatedPi_withHeteroatom){
	            			resultsH = calculateCojugatedPiSystDescriptor(clonedBond, localClone, ac);
	            			resultD = getConjugatedPiSys(resultsH);
        					resultD += 0.05;
                			isTarget = true;
                			break;
            			}
        			}
        		}
                if(!isConjugatedPi){

					resultsH = calculatePiSystDescriptor(clonedBond, localClone);
					resultD = getAcetyl_EthylWithoutHetero(resultsH);
					resultD += 0.05;
        			isTarget = true;
                }
		}

		if(isTarget){
    		/* iniziate reaction*/
			IMoleculeSet setOfReactants = localClone.getBuilder().newMoleculeSet();
			setOfReactants.addMolecule((IMolecule) localClone);
			IReactionProcess type  = new ElectronImpactPDBReaction();
			bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("hasActiveCenter",Boolean.TRUE);;
	        type.setParameters(params);
	        IReactionSet pbb = type.initiate(setOfReactants, null);
	        Iterator it = pbb.reactions();
	        while(it.hasNext()){
	        	IReaction reaction = (IReaction)it.next();
	        	reaction.setProperty("IonizationEnergy", resultD);
	        	reactionSet.addReaction(reaction);
	        }
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD),descriptorNames);
	}

	
	/**
	 * tree desicion for the ConjugatedPiSys
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getConjugatedPiSys(double[] resultsH) {
		double result = 0.0;
		double SE_1 = resultsH[0];
		double EE_1 = resultsH[1];
		double RES_c2  = resultsH[2];
		
		if (RES_c2 <= 8.242293)
		{
		  if (EE_1 <= 0.324173)
		  {
		    if (EE_1 <= 0.263763)
		    {
		      if (EE_1 <= 0.196384)
		      {
		        if (EE_1 <= 0.150372) { result = 06.2; /* 2.0/1.0 */}
		        else if (EE_1 > 0.150372) { result = 07.9; /* 3.0/1.0 */}
		      }
		      if (EE_1 > 0.196384) { result = 08.3; /* 5.0/2.0 */}
		    }
		    if (EE_1 > 0.263763)
		    {
		      if (RES_c2 <= 8.152026)
		      {
		        if (EE_1 <= 0.26488) { result = 08.7; /* 2.0 */}
		        else if (EE_1 > 0.26488) { result = 08.5; /* 2.0 */}
		      }
		      if (RES_c2 > 8.152026) { result = 07.7; /* 3.0/2.0 */}
		    }
		  }
		  if (EE_1 > 0.324173)
		  {
		    if (SE_1 <= 0.009142)
		    {
		      if (RES_c2 <= 8.152026) { result = 08.2; /* 29.0/19.0 */}
		      else if (RES_c2 > 8.152026)
		      {
		        if (RES_c2 <= 8.217601)
		        {
		          if (SE_1 <= 0.006519)
		          {
		            if (SE_1 <= 0.005641) { result = 07.8; /* 3.0/1.0 */}
		            else if (SE_1 > 0.005641) { result = 08.1; /* 2.0/1.0 */}
		          }
		          if (SE_1 > 0.006519)
		          {
		            if (EE_1 <= 1.552108) { result = 08.0; /* 5.0/1.0 */}
		            else if (EE_1 > 1.552108) { result = 07.8; /* 2.0/1.0 */}
		          }
		        }
		        if (RES_c2 > 8.217601) { result = 07.9; /* 2.0 */}
		      }
		    }
		    if (SE_1 > 0.009142)
		    {
		      if (RES_c2 <= 8.057972)
		      {
		        if (EE_1 <= 0.529275)
		        {
		          if (EE_1 <= 0.472268) { result = 08.6; /* 3.0/1.0 */}
		          else if (EE_1 > 0.472268) { result = 08.5; /* 2.0 */}
		        }
		        if (EE_1 > 0.529275) { result = 07.9; /* 2.0/1.0 */}
		      }
		      if (RES_c2 > 8.057972)
		      {
		        if (SE_1 <= 0.015444)
		        {
		          if (EE_1 <= 0.421993) { result = 08.1; /* 3.0/1.0 */}
		          else if (EE_1 > 0.421993)
		          {
		            if (SE_1 <= 0.015007)
		            {
		              if (SE_1 <= 0.014856) { result = 08.4; /* 7.0 */}
		              else if (SE_1 > 0.014856)
		              {
		                if (SE_1 <= 0.014941) { result = 08.2; /* 2.0 */}
		                else if (SE_1 > 0.014941) { result = 08.4; /* 2.0 */}
		              }
		            }
		            if (SE_1 > 0.015007)
		            {
		              if (RES_c2 <= 8.1046) { result = 08.6; /* 3.0/1.0 */}
		              else if (RES_c2 > 8.1046)
		              {
		                if (RES_c2 <= 8.11885) { result = 08.5; /* 4.0/2.0 */}
		                else if (RES_c2 > 8.11885) { result = 08.2; /* 3.0/1.0 */}
		              }
		            }
		          }
		        }
		        if (SE_1 > 0.015444) { result = 08.3; /* 6.0/4.0 */}
		      }
		    }
		  }
		}
		if (RES_c2 > 8.242293)
		{
		  if (RES_c2 <= 8.568494)
		  {
		    if (EE_1 <= 0.79014)
		    {
		      if (SE_1 <= 0.016065)
		      {
		        if (EE_1 <= 0.301514) { result = 07.2; /* 3.0/2.0 */}
		        else if (EE_1 > 0.301514)
		        {
		          if (SE_1 <= 0.001726) { result = 07.3; /* 2.0/1.0 */}
		          else if (SE_1 > 0.001726) { result = 07.8; /* 6.0/2.0 */}
		        }
		      }
		      if (SE_1 > 0.016065) { result = 07.3; /* 3.0/2.0 */}
		    }
		    if (EE_1 > 0.79014)
		    {
		      if (SE_1 <= 0.005082) { result = 08.2; /* 2.0 */}
		      else if (SE_1 > 0.005082) { result = 07.5; /* 3.0/2.0 */}
		    }
		  }
		  if (RES_c2 > 8.568494)
		  {
		    if (SE_1 <= 0.004762)
		    {
		      if (EE_1 <= 0.324173)
		      {
		        if (EE_1 <= 0.124581) { result = 09.0; /* 4.0/3.0 */}
		        else if (EE_1 > 0.124581) { result = 07.5; /* 2.0/1.0 */}
		      }
		      if (EE_1 > 0.324173)
		      {
		        if (SE_1 <= 0.003414)
		        {
		          if (RES_c2 <= 8.965872) { result = 08.4; /* 3.0 */}
		          else if (RES_c2 > 8.965872) { result = 08.1; /* 2.0/1.0 */}
		        }
		        if (SE_1 > 0.003414)
		        {
		          if (RES_c2 <= 8.917376) { result = 09.0; /* 4.0/2.0 */}
		          else if (RES_c2 > 8.917376)
		          {
		            if (SE_1 <= 0.004702) { result = 08.8; /* 2.0/1.0 */}
		            else if (SE_1 > 0.004702) { result = 08.4; /* 2.0/1.0 */}
		          }
		        }
		      }
		    }
		    if (SE_1 > 0.004762)
		    {
		      if (EE_1 <= 1.140026)
		      {
		        if (SE_1 <= 0.016032)
		        {
		          if (RES_c2 <= 9.853716) { result = 09.5; /* 3.0/1.0 */}
		          else if (RES_c2 > 9.853716) { result = 08.6; /* 5.0/3.0 */}
		        }
		        if (SE_1 > 0.016032)
		        {
		          if (RES_c2 <= 8.97498) { result = 08.6; /* 2.0 */}
		          else if (RES_c2 > 8.97498) { result = 08.5; /* 2.0 */}
		        }
		      }
		      if (EE_1 > 1.140026)
		      {
		        if (RES_c2 <= 8.987296) { result = 08.7; /* 4.0/2.0 */}
		        else if (RES_c2 > 8.987296) { result = 08.2; /* 4.0/1.0 */}
		      }
		    }
		  }
		}
		return result;
	}
	
	/**
	 * tree desicion for the Acetyl_EthylWithoutHetero
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getAcetyl_EthylWithoutHetero(double[] resultsH) {
		double result = 0.0;
		double SE_1 = resultsH[0];
		double SCH_1 = resultsH[1];
		double SE_2  = resultsH[2];
		double SCH_2 = resultsH[3];
		double RES_c1  = resultsH[4];
		double RES_c2  = resultsH[5];
		
		if (SCH_2 <= -0.099464)
		{
		  if (SCH_2 <= -0.105819)
		  {
		    if (SCH_2 <= -0.119363)
		    {
		      if (SE_1 <= 9.394861) { result = 10.1; /* 3.0/1.0 */}
		      else if (SE_1 > 9.394861)
		      {
		        if (SE_1 <= 9.394917) { result = 09.9; /* 7.0/1.0 */}
		        else if (SE_1 > 9.394917) { result = 10.0; /* 8.0/5.0 */}
		      }
		    }
		    if (SCH_2 > -0.119363)
		    {
		      if (RES_c1 <= 1.484081)
		      {
		        if (RES_c1 <= 0)
		        {
		          if (SE_1 <= 9.269669) { result = 07.9; /* 2.0/1.0 */}
		          else if (SE_1 > 9.269669) { result = 09.8; /* 3.0/1.0 */}
		        }
		        if (RES_c1 > 0) { result = 07.9; /* 2.0/1.0 */}
		      }
		      if (RES_c1 > 1.484081) { result = 09.3; /* 11.0/3.0 */}
		    }
		  }
		  if (SCH_2 > -0.105819)
		  {
		    if (SE_2 <= 7.851763) { result = 09.4; /* 14.0/8.0 */}
		    else if (SE_2 > 7.851763)
		    {
		      if (SCH_2 <= -0.103062)
		      {
		        if (SE_1 <= 9.426247) { result = 09.2; /* 5.0/1.0 */}
		        else if (SE_1 > 9.426247) { result = 09.1; /* 8.0/3.0 */}
		      }
		      if (SCH_2 > -0.103062)
		      {
		        if (RES_c2 <= 0)
		        {
		          if (SE_1 <= 8.054504) { result = 09.1; /* 5.0/3.0 */}
		          else if (SE_1 > 8.054504) { result = 09.0; /* 5.0/1.0 */}
		        }
		        if (RES_c2 > 0) { result = 09.1; /* 20.0/8.0 */}
		      }
		    }
		  }
		}
		if (SCH_2 > -0.099464)
		{
		  if (SCH_2 <= -0.081738)
		  {
		    if (SCH_1 <= -0.084268)
		    {
		      if (SE_1 <= 7.951729)
		      {
		        if (RES_c1 <= 1.113294)
		        {
		          if (SCH_1 <= -0.10278) { result = 08.1; /* 2.0/1.0 */}
		          else if (SCH_1 > -0.10278)
		          {
		            if (SE_2 <= 7.953333) { result = 07.3; /* 2.0/1.0 */}
		            else if (SE_2 > 7.953333) { result = 09.4; /* 5.0/1.0 */}
		          }
		        }
		        if (RES_c1 > 1.113294) { result = 09.1; /* 3.0 */}
		      }
		      if (SE_1 > 7.951729)
		      {
		        if (SE_2 <= 7.97973)
		        {
		          if (SCH_1 <= -0.088386) { result = 09.0; /* 3.0/1.0 */}
		          else if (SCH_1 > -0.088386) { result = 08.9; /* 17.0/5.0 */}
		        }
		        if (SE_2 > 7.97973)
		        {
		          if (SE_1 <= 8.012483)
		          {
		            if (SCH_2 <= -0.084268) { result = 08.8; /* 64.0/33.0 */}
		            else if (SCH_2 > -0.084268)
		            {
		              if (SE_2 <= 8.038205) { result = 08.4; /* 3.0/1.0 */}
		              else if (SE_2 > 8.038205) { result = 08.8; /* 2.0/1.0 */}
		            }
		          }
		          if (SE_1 > 8.012483)
		          {
		            if (SE_2 <= 8.012573)
		            {
		              if (SE_2 <= 7.984696) { result = 08.6; /* 2.0 */}
		              else if (SE_2 > 7.984696)
		              {
		                if (SE_2 <= 7.989665) { result = 08.9; /* 4.0 */}
		                else if (SE_2 > 7.989665) { result = 07.7; /* 4.0/2.0 */}
		              }
		            }
		            if (SE_2 > 8.012573)
		            {
		              if (SCH_1 <= -0.096308) { result = 09.0; /* 2.0/1.0 */}
		              else if (SCH_1 > -0.096308) { result = 08.8; /* 11.0/6.0 */}
		            }
		          }
		        }
		      }
		    }
		    if (SCH_1 > -0.084268)
		    {
		      if (SCH_2 <= -0.090532)
		      {
		        if (SCH_1 <= -0.076605) { result = 08.9; /* 5.0/2.0 */}
		        else if (SCH_1 > -0.076605)
		        {
		          if (RES_c1 <= 0) { result = 08.7; /* 2.0/1.0 */}
		          else if (RES_c1 > 0)
		          {
		            if (SE_2 <= 7.883167) { result = 09.0; /* 18.0/9.0 */}
		            else if (SE_2 > 7.883167) { result = 08.5; /* 2.0/1.0 */}
		          }
		        }
		      }
		      if (SCH_2 > -0.090532)
		      {
		        if (SCH_1 <= -0.082029) { result = 08.6; /* 17.0/13.0 */}
		        else if (SCH_1 > -0.082029)
		        {
		          if (SCH_1 <= -0.073651) { result = 08.5; /* 6.0/3.0 */}
		          else if (SCH_1 > -0.073651)
		          {
		            if (RES_c1 <= 1.484392)
		            {
		              if (SCH_1 <= -0.073401)
		              {
		                if (SE_1 <= 8.113674) { result = 08.3; /* 3.0 */}
		                else if (SE_1 > 8.113674) { result = 08.4; /* 2.0 */}
		              }
		              if (SCH_1 > -0.073401) { result = 08.3; /* 2.0 */}
		            }
		            if (RES_c1 > 1.484392) { result = 08.4; /* 6.0/1.0 */}
		          }
		        }
		      }
		    }
		  }
		  if (SCH_2 > -0.081738)
		  {
		    if (SCH_1 <= -0.077122)
		    {
		      if (RES_c1 <= 0)
		      {
		        if (RES_c2 <= 1.484392)
		        {
		          if (SCH_1 <= -0.079198) { result = 09.0; /* 5.0/2.0 */}
		          else if (SCH_1 > -0.079198) { result = 09.3; /* 2.0/1.0 */}
		        }
		        if (RES_c2 > 1.484392)
		        {
		          if (SE_2 <= 8.084905) { result = 08.7; /* 2.0/1.0 */}
		          else if (SE_2 > 8.084905) { result = 08.0; /* 2.0/1.0 */}
		        }
		      }
		      if (RES_c1 > 0)
		      {
		        if (SCH_1 <= -0.084728)
		        {
		          if (SCH_2 <= -0.075912) { result = 08.6; /* 6.0/1.0 */}
		          else if (SCH_2 > -0.075912) { result = 08.4; /* 6.0 */}
		        }
		        if (SCH_1 > -0.084728) { result = 08.0; /* 2.0/1.0 */}
		      }
		    }
		    if (SCH_1 > -0.077122)
		    {
		      if (SCH_2 <= -0.070275)
		      {
		        if (SCH_2 <= -0.079606)
		        {
		          if (RES_c1 <= 1.484392) { result = 08.3; /* 3.0/1.0 */}
		          else if (RES_c1 > 1.484392) { result = 07.4; /* 2.0/1.0 */}
		        }
		        if (SCH_2 > -0.079606)
		        {
		          if (RES_c1 <= 1.484392)
		          {
		            if (SCH_2 <= -0.072617) { result = 08.1; /* 8.0/2.0 */}
		            else if (SCH_2 > -0.072617) { result = 08.2; /* 4.0/1.0 */}
		          }
		          if (RES_c1 > 1.484392)
		          {
		            if (SE_1 <= 8.083771) { result = 08.2; /* 2.0 */}
		            else if (SE_1 > 8.083771) { result = 08.1; /* 10.0/2.0 */}
		          }
		        }
		      }
		      if (SCH_2 > -0.070275)
		      {
		        if (SE_2 <= 8.178282) { result = 08.5; /* 2.0/1.0 */}
		        else if (SE_2 > 8.178282)
		        {
		          if (SE_1 <= 8.193598) { result = 08.1; /* 3.0/1.0 */}
		          else if (SE_1 > 8.193598) { result = 07.8; /* 2.0/1.0 */}
		        }
		      }
		    }
		  }
		}

		
		
		return result;
		
	}
	/**
	 * Get the reactions obtained with ionization.
	 * The energy is set as property
	 * 
	 * @return The IReactionSet value
	 */
	public IReactionSet getReactionSet(){
		return reactionSet;
	}
	
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private double[] calculatePiSystDescriptor(IBond bond, IAtomContainer atomContainer) throws CDKException {
		double[] results = new double[6];
		IAtom positionC = bond.getAtom(0);
		IAtom positionX = bond.getAtom(1);
    	
		/*0_1*/
		SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
		results[0]= ((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue();
    	/*1_1*/
		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
		results[1]= ((DoubleResult)descriptor2.calculate(positionC, atomContainer).getValue()).doubleValue();
		
		/*0_2*/
		SigmaElectronegativityDescriptor descriptor3 = new SigmaElectronegativityDescriptor();
		results[2]= ((DoubleResult)descriptor3.calculate(positionX, atomContainer).getValue()).doubleValue();
    	/*1_2*/
		PartialSigmaChargeDescriptor descriptor4 = new PartialSigmaChargeDescriptor();
		results[3]= ((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue();
		
		/*  */
		ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
		DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bond, atomContainer).getValue());
		results[4] = dar.get(0);
		results[5] = dar.get(1);
    		
    		
		return results;
	}
	/**
	 * Calculate the necessary descriptors for pi conjugated systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private double[] calculateCojugatedPiSystDescriptor(IBond bond, IAtomContainer atomContainer, IAtomContainer conjugatedSys) throws CDKException {
		double[] results = new double[3];
		
		results[0] = 0.0;
		results[1] = 0.0;
		results[2] = 0.0;
		
		/*calculation of the atomic descriptors*/
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
			double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
			
			
			
			if(result1 > results[0])
				results[0] = result1;
			
			SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
			double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
			results[2] += result2;
			
		}
		
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			IBond bondsss = (IBond) bondIt.next();
			try{
			ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
			
			
			double result1 = dar.get(0);
			double resutt2 = dar.get(1);
			double result12 = (result1+resutt2);
			
			double resultT = 0;
			if(result12 != 0)
				resultT = result12/2;
			
			results[1] += resultT;
			
			
			} catch (Exception exc)
			{
                exc.printStackTrace();
            }
		}
		if(results[1] != 0)
			results[1] = results[1]/conjugatedSys.getAtomCount();
		
		if(results[2] != 0)
			results[2] = results[2]/conjugatedSys.getAtomCount();
		
		return results;
	}
	
	 /**
     * Gets the parameterNames attribute of the IPBondDescriptor object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     * Gets the parameterType attribute of the IPBondDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}


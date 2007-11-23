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
package org.openscience.cdk.qsar.descriptors.atomic;

import java.util.Iterator;
import java.util.List;

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
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.BondPartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.ResonancePositiveChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReaction;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  This class returns the ionization potential of an atom containg lone 
 *  pair electrons. It is
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values. Up to now is only possible predict for 
 *  Cl,Br,I,N,P,O,S Atoms and they are not belong to
 *  conjugated system or not adjacent to an double bond.
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
 * @author           Miguel Rojas
 * @cdk.created      2006-05-26
 * @cdk.module       qsar
 * @cdk.svnrev  $Revision: 9162 $
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 * 
 */
public class IPAtomicDescriptor implements IAtomicDescriptor {

	private IReactionSet reactionSet;
	
	/**
	 *  Constructor for the IPAtomicDescriptor object
	 */
	public IPAtomicDescriptor() {
	}
	/**
	 *  Gets the specification attribute of the IPAtomicDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential",
				this.getClass().getName(),
				"$Id: IPAtomicDescriptor.java 6171 2006-5-22 19:29:58Z egonw $",
				"The Chemistry Development Kit");
	}
    /**
     * This descriptor does have any parameter.
     */
    public void setParameters(Object[] params) throws CDKException {
    }


    /**
     *  Gets the parameters attribute of the IPAtomicDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    public Object[] getParameters() {
        return null;
    }
	/**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  atom          The IAtom to ionize.
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential. Not possible the ionization.
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer atomContainer) throws CDKException{
        IAtomContainer localClone;
        try {
            localClone= (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Error during clone");
        }
        IAtom clonedAtom = localClone.getAtom(atomContainer.getAtomNumber(atom));
        
        reactionSet = localClone.getBuilder().newReactionSet();
    	
		double resultD = -1.0;
		boolean isTarget = false;
		boolean isConjugated = false;
		double[] resultsH;

		/*control if it is into an aromatic or conjugated system*/
		AtomContainerManipulator.percieveAtomTypesAndConfigerAtoms(localClone);
		CDKHueckelAromaticityDetector.detectAromaticity(localClone);
		AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(localClone);
		Iterator acI = conjugatedPi.atomContainers();
 		while(acI.hasNext()){
			IAtomContainer ac = (IAtomContainer) acI.next();
			if(ac.contains(clonedAtom)){
				isConjugated = true;
				if(localClone.getMaximumBondOrder(clonedAtom) == 1 && localClone.getConnectedLonePairsCount(clonedAtom) > 0){
					resultsH = calculateHeteroAtomConjugatedDescriptor(clonedAtom, localClone,ac);
					resultD = getTreeHeteroConjAtom(resultsH);
					resultD += 0.05;
					isTarget = true;
				}
			}
 		}
 		
		if(atom.getFlag(CDKConstants.ISAROMATIC))
			return null;
		
		int count = localClone.getConnectedLonePairsCount(clonedAtom);
		
		if(localClone.getMaximumBondOrder(clonedAtom) > 1 && localClone.getConnectedLonePairsCount(clonedAtom) > 0){
			resultsH = calculateCarbonylDescriptor(clonedAtom, localClone);
			resultD = getTreeDoubleHetero(resultsH);
			resultD += 0.05;
			isTarget = true;
		}else if(localClone.getConnectedLonePairsCount(clonedAtom) > 0 && !isConjugated){
			resultsH = calculateHeteroAtomDescriptor(clonedAtom, localClone);
			resultD = getTreeHeteroAtom(resultsH);
			resultD += 0.05;
			isTarget = true;
		}
			
			
		if(isTarget){
			/* inizate reaction*/
			if(localClone.getConnectedLonePairsCount(clonedAtom) > 0){
				
				IMoleculeSet setOfReactants = localClone.getBuilder().newMoleculeSet();
				setOfReactants.addMolecule((IMolecule) localClone);
				IReactionProcess type  = new ElectronImpactNBEReaction();
				atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
		        Object[] params = {Boolean.TRUE};
		        type.setParameters(params);
		        IReactionSet nbe = type.initiate(setOfReactants, null);
		        Iterator it = nbe.reactions();
		        while(it.hasNext()){
		        	IReaction reaction = (IReaction)it.next();
		        	reaction.setProperty("IonizationEnergy", new Double(resultD));
		        	reactionSet.addReaction(reaction);
		        }
			}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD));
	}
	/**
	 * tree desicion for the carbonyl atoms
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getTreeHeteroConjAtom(double[] resultsH) {
		double result = 0.0;
		double SE_1 = resultsH[0];
		double SE_2 = resultsH[1];
		double EE_1  = resultsH[2];
		double RES_c2 = resultsH[3];
	
		if (SE_1 <= -0.069065)
		{
		  if (SE_1 <= -0.138994)
		  {
		    if (EE_1 <= 0.676304)
		    {
		      if (SE_1 <= -2.022267) { result = 07.7; /* 2.0 */}
		      else if (SE_1 > -2.022267) { result = 08.2; /* 4.0/2.0 */}
		    }
		    if (EE_1 > 0.676304)
		    {
		      if (SE_1 <= -2.514435) { result = 08.0; /* 3.0/1.0 */}
		      else if (SE_1 > -2.514435) { result = 09.5; /* 4.0/2.0 */}
		    }
		  }
		  if (SE_1 > -0.138994)
		  {
		    if (EE_1 <= 0.032157)
		    {
		      if (EE_1 <= 0.026958)
		      {
		        if (EE_1 <= 0.023022) { result = 08.1; /* 2.0/1.0 */}
		        else if (EE_1 > 0.023022)
		        {
		          if (SE_1 <= -0.074747) { result = 08.6; /* 3.0/1.0 */}
		          else if (SE_1 > -0.074747) { result = 08.9; /* 3.0 */}
		        }
		      }
		      if (EE_1 > 0.026958)
		      {
		        if (SE_2 <= 0.081676) { result = 08.3; /* 2.0/1.0 */}
		        else if (SE_2 > 0.081676) { result = 08.0; /* 6.0/1.0 */}
		      }
		    }
		    if (EE_1 > 0.032157)
		    {
		      if (EE_1 <= 0.045671) { result = 08.6; /* 5.0/2.0 */}
		      else if (EE_1 > 0.045671)
		      {
		        if (SE_1 <= -0.137681) { result = 08.1; /* 2.0/1.0 */}
		        else if (SE_1 > -0.137681) { result = 08.4; /* 5.0/2.0 */}
		      }
		    }
		  }
		}
		if (SE_1 > -0.069065)
		{
		  if (SE_1 <= -0.061906)
		  {
		    if (RES_c2 <= 0.006978) { result = 07.4; /* 15.0/10.0 */}
		    else if (RES_c2 > 0.006978)
		    {
		      if (SE_1 <= -0.063064) { result = 07.5; /* 8.0/5.0 */}
		      else if (SE_1 > -0.063064) { result = 07.3; /* 2.0/1.0 */}
		    }
		  }
		  if (SE_1 > -0.061906)
		  {
		    if (RES_c2 <= 0.003398)
		    {
		      if (SE_1 <= 0.051565)
		      {
		        if (EE_1 <= 0.007932)
		        {
		          if (SE_1 <= -0.023796) { result = 08.7; /* 4.0/2.0 */}
		          else if (SE_1 > -0.023796)
		          {
		            if (EE_1 <= -0.003129) { result = 07.3; /* 2.0/1.0 */}
		            else if (EE_1 > -0.003129) { result = 07.4; /* 4.0/2.0 */}
		          }
		        }
		        if (EE_1 > 0.007932)
		        {
		          if (SE_1 <= -0.0252)
		          {
		            if (SE_1 <= -0.02952) { result = 08.0; /* 2.0/1.0 */}
		            else if (SE_1 > -0.02952) { result = 07.8; /* 2.0/1.0 */}
		          }
		          if (SE_1 > -0.0252) { result = 07.7; /* 4.0/1.0 */}
		        }
		      }
		      if (SE_1 > 0.051565)
		      {
		        if (RES_c2 <= -0.003572) { result = 08.5; /* 4.0/2.0 */}
		        else if (RES_c2 > -0.003572)
		        {
		          if (SE_1 <= 0.126992) { result = 07.6; /* 2.0/1.0 */}
		          else if (SE_1 > 0.126992) { result = 08.2; /* 2.0/1.0 */}
		        }
		      }
		    }
		    if (RES_c2 > 0.003398)
		    {
		      if (SE_1 <= -0.031039)
		      {
		        if (RES_c2 <= 0.005076) { result = 08.1; /* 9.0/5.0 */}
		        else if (RES_c2 > 0.005076)
		        {
		          if (SE_1 <= -0.061705) { result = 08.2; /* 2.0 */}
		          else if (SE_1 > -0.061705)
		          {
		            if (SE_1 <= -0.060998) { result = 08.3; /* 2.0 */}
		            else if (SE_1 > -0.060998) { result = 08.0; /* 2.0/1.0 */}
		          }
		        }
		      }
		      if (SE_1 > -0.031039)
		      {
		        if (SE_1 <= -0.023566)
		        {
		          if (SE_1 <= -0.028788) { result = 07.9; /* 2.0 */}
		          else if (SE_1 > -0.028788) { result = 09.3; /* 2.0/1.0 */}
		        }
		        if (SE_1 > -0.023566)
		        {
		          if (RES_c2 <= 0.004518) { result = 08.2; /* 2.0/1.0 */}
		          else if (RES_c2 > 0.004518) { result = 08.7; /* 4.0/2.0 */}
		        }
		      }
		    }
		  }
		}
		
		return result;
	}
	/**
	 * tree desicion for the carbonyl atoms
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getTreeDoubleHetero(double[] resultsH) {
		double result = 0.0;
		double SE_c = resultsH[0];
		double PCH_c = resultsH[1];
		double SB  = resultsH[2];
		double SE_x = resultsH[3];
		double PCH_x = resultsH[4];
		double RES_c = resultsH[5];
		
		if (PCH_c <= 0.045111)
		{
		  if (PCH_x <= -0.041368)
		  {
		    if (SE_c <= 7.471265)
		    {
		      if (RES_c <= 0.182146)
		      {
		        if (SB <= 0.448987)
		        {
		          if (RES_c <= 0.181878) { result = 08.4; /* 3.0/1.0 */}
		          else if (RES_c > 0.181878) { result = 09.2; /* 4.0/1.0 */}
		        }
		        if (SB > 0.448987)
		        {
		          if (SE_c <= 6.758953) { result = 09.0; /* 2.0 */}
		          else if (SE_c > 6.758953) { result = 09.6; /* 2.0/1.0 */}
		        }
		      }
		      if (RES_c > 0.182146)
		      {
		        if (RES_c <= 0.371342)
		        {
		          if (PCH_c <= 0.019491) { result = 09.3; /* 9.0/3.0 */}
		          else if (PCH_c > 0.019491)
		          {
		            if (SE_c <= 6.683097) { result = 09.7; /* 3.0 */}
		            else if (SE_c > 6.683097) { result = 09.6; /* 4.0/2.0 */}
		          }
		        }
		        if (RES_c > 0.371342)
		        {
		          if (PCH_c <= 0.019061) { result = 08.7; /* 3.0/1.0 */}
		          else if (PCH_c > 0.019061)
		          {
		            if (SE_c <= 6.689165) { result = 09.9; /* 2.0/1.0 */}
		            else if (SE_c > 6.689165) { result = 09.5; /* 4.0/1.0 */}
		          }
		        }
		      }
		    }
		    if (SE_c > 7.471265)
		    {
		      if (SE_x <= 13.191725)
		      {
		        if (SB <= 0.479515)
		        {
		          if (PCH_x <= -0.045111) { result = 10.2; /* 4.0/2.0 */}
		          else if (PCH_x > -0.045111) { result = 09.8; /* 2.0/1.0 */}
		        }
		        if (SB > 0.479515)
		        {
		          if (SE_c <= 7.571876) { result = 10.0; /* 4.0/2.0 */}
		          else if (SE_c > 7.571876) { result = 09.6; /* 4.0/1.0 */}
		        }
		      }
		      if (SE_x > 13.191725)
		      {
		        if (PCH_c <= 0.010584) { result = 09.5; /* 3.0/1.0 */}
		        else if (PCH_c > 0.010584) { result = 09.4; /* 2.0/1.0 */}
		      }
		    }
		  }
		  if (PCH_x > -0.041368)
		  {
		    if (PCH_c <= 0.029565)
		    {
		      if (SB <= 0.028396)
		      {
		        if (PCH_c <= 0.02228) { result = 08.6; /* 3.0/1.0 */}
		        else if (PCH_c > 0.02228) { result = 07.4; /* 2.0/1.0 */}
		      }
		      if (SB > 0.028396)
		      {
		        if (SE_x <= 9.633713)
		        {
		          if (SE_c <= 8.750164) { result = 08.4; /* 2.0/1.0 */}
		          else if (SE_c > 8.750164) { result = 09.4; /* 3.0/1.0 */}
		        }
		        if (SE_x > 9.633713)
		        {
		          if (PCH_c <= 0)
		          {
		            if (SE_c <= 7.010887) { result = 09.1; /* 2.0/1.0 */}
		            else if (SE_c > 7.010887) { result = 08.6; /* 3.0/2.0 */}
		          }
		          if (PCH_c > 0)
		          {
		            if (SE_c <= 8.852704)
		            {
		              if (SB <= 0.473847)
		              {
		                if (PCH_c <= 0.000059) { result = 08.5; /* 2.0 */}
		                else if (PCH_c > 0.000059)
		                {
		                  if (SE_c <= 8.779599) { result = 08.6; /* 4.0/1.0 */}
		                  else if (SE_c > 8.779599) { result = 08.5; /* 2.0/1.0 */}
		                }
		              }
		              if (SB > 0.473847) { result = 08.2; /* 2.0/1.0 */}
		            }
		            if (SE_c > 8.852704) { result = 08.2; /* 3.0/2.0 */}
		          }
		        }
		      }
		    }
		    if (PCH_c > 0.029565)
		    {
		      if (SE_x <= 12.990697)
		      {
		        if (PCH_c <= 0.039387)
		        {
		          if (RES_c <= 1.086882)
		          {
		            if (SE_x <= 9.519079) { result = 10.0; /* 2.0 */}
		            else if (SE_x > 9.519079)
		            {
		              if (SE_c <= 8.798893) { result = 09.8; /* 3.0/1.0 */}
		              else if (SE_c > 8.798893) { result = 09.2; /* 3.0/2.0 */}
		            }
		          }
		          if (RES_c > 1.086882) { result = 08.9; /* 3.0/1.0 */}
		        }
		        if (PCH_c > 0.039387)
		        {
		          if (SE_c <= 10.049963) { result = 09.3; /* 13.0/7.0 */}
		          else if (SE_c > 10.049963)
		          {
		            if (PCH_c <= 0.039719) { result = 09.1; /* 25.0/13.0 */}
		            else if (PCH_c > 0.039719)
		            {
		              if (SE_c <= 10.079873) { result = 09.3; /* 2.0/1.0 */}
		              else if (SE_c > 10.079873) { result = 08.9; /* 2.0/1.0 */}
		            }
		          }
		        }
		      }
		      if (SE_x > 12.990697)
		      {
		        if (PCH_c <= 0.039882)
		        {
		          if (RES_c <= 0.913891)
		          {
		            if (SE_x <= 12.995039)
		            {
		              if (SE_c <= 10.112309) { result = 09.0; /* 8.0/4.0 */}
		              else if (SE_c > 10.112309) { result = 08.8; /* 3.0/1.0 */}
		            }
		            if (SE_x > 12.995039)
		            {
		              if (SE_c <= 10.117785) { result = 08.7; /* 2.0/1.0 */}
		              else if (SE_c > 10.117785) { result = 09.0; /* 2.0/1.0 */}
		            }
		          }
		          if (RES_c > 0.913891) { result = 08.9; /* 3.0/1.0 */}
		        }
		        if (PCH_c > 0.039882) { result = 08.8; /* 7.0/4.0 */}
		      }
		    }
		  }
		}
		if (PCH_c > 0.045111)
		{
		  if (SE_c <= 7.915887)
		  {
		    if (RES_c <= 0.699389)
		    {
		      if (PCH_c <= 1.979715) { result = 11.1; /* 4.0/2.0 */}
		      else if (PCH_c > 1.979715) { result = 08.9; /* 3.0/2.0 */}
		    }
		    if (RES_c > 0.699389) { result = 08.5; /* 2.0 */}
		  }
		  if (SE_c > 7.915887)
		  {
		    if (SB <= 0.325594)
		    {
		      if (PCH_c <= 0.582583)
		      {
		        if (PCH_c <= 0.576351) { result = 08.3; /* 3.0/1.0 */}
		        else if (PCH_c > 0.576351) { result = 07.5; /* 2.0/1.0 */}
		      }
		      if (PCH_c > 0.582583)
		      {
		        if (PCH_c <= 1.296286) { result = 08.4; /* 2.0/1.0 */}
		        else if (PCH_c > 1.296286) { result = 09.4; /* 3.0/2.0 */}
		      }
		    }
		    if (SB > 0.325594)
		    {
		      if (SE_x <= 12.940799)
		      {
		        if (SB <= 0.421092) { result = 09.9; /* 2.0 */}
		        else if (SB > 0.421092)
		        {
		          if (PCH_c <= 0.045325) { result = 09.7; /* 5.0/3.0 */}
		          else if (PCH_c > 0.045325) { result = 09.6; /* 4.0/2.0 */}
		        }
		      }
		      if (SE_x > 12.940799) { result = 09.5; /* 6.0/3.0 */}
		    }
		  }
		}
		return result;
	}

	/**
	 * tree desicion for the Heteroatom
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getTreeHeteroAtom(double[] resultsH) {
		double result = 0.0;
		double SE = resultsH[0];
		double SCH = resultsH[1];
		double EE  = resultsH[2];
		double PE  = resultsH[3];
		
		if (SE <= 8.80606)
		{
		  if (EE <= 6.489)
		  {
		    if (SCH <= -0.315373)
		    {
		      if (EE <= 5.422063)
		      {
		        if (EE <= 4.608813)
		        {
		          if (SCH <= -0.330239) { result = 06.2; /* 3.0/2.0 */}
		          else if (SCH > -0.330239)
		          {
		            if (SE <= 8.120134) { result = 08.5; /* 2.0/1.0 */}
		            else if (SE > 8.120134) { result = 09.0; /* 2.0/1.0 */}
		          }
		        }
		        if (EE > 4.608813)
		        {
		          if (SCH <= -0.32534)
		          {
		            if (EE <= 5.00775) { result = 08.6; /* 7.0/3.0 */}
		            else if (EE > 5.00775) { result = 08.5; /* 4.0/1.0 */}
		          }
		          if (SCH > -0.32534) { result = 08.1; /* 3.0/2.0 */}
		        }
		      }
		      if (EE > 5.422063)
		      {
		        if (SCH <= -0.321973)
		        {
		          if (SE <= 8.1475) { result = 08.4; /* 3.0/1.0 */}
		          else if (SE > 8.1475) { result = 08.5; /* 2.0/1.0 */}
		        }
		        if (SCH > -0.321973)
		        {
		          if (EE <= 6.102) { result = 07.9; /* 2.0/1.0 */}
		          else if (EE > 6.102) { result = 08.0; /* 3.0/1.0 */}
		        }
		      }
		    }
		    if (SCH > -0.315373)
		    {
		      if (PE <= 4.873452) { result = 09.2; /* 15.0/10.0 */}
		      else if (PE > 4.873452)
		      {
		        if (SCH <= -0.139567)
		        {
		          if (EE <= 1.438) { result = 09.8; /* 3.0/1.0 */}
		          else if (EE > 1.438)
		          {
		            if (SE <= 8.60319) { result = 09.1; /* 6.0/2.0 */}
		            else if (SE > 8.60319) { result = 08.6; /* 2.0/1.0 */}
		          }
		        }
		        if (SCH > -0.139567)
		        {
		          if (SE <= 7.833611) { result = 09.3; /* 3.0/2.0 */}
		          else if (SE > 7.833611) { result = 08.5; /* 3.0/1.0 */}
		        }
		      }
		    }
		  }
		  if (EE > 6.489)
		  {
		    if (SE <= 8.42798)
		    {
		      if (EE <= 8.234438)
		      {
		        if (EE <= 7.813)
		        {
		          if (SE <= 8.274057)
		          {
		            if (EE <= 6.8275)
		            {
		              if (SCH <= -0.315888) { result = 07.8; /* 2.0/1.0 */}
		              else if (SCH > -0.315888) { result = 08.0; /* 3.0 */}
		            }
		            if (EE > 6.8275)
		            {
		              if (SCH <= -0.315373) { result = 07.6; /* 2.0/1.0 */}
		              else if (SCH > -0.315373) { result = 08.3; /* 3.0/1.0 */}
		            }
		          }
		          if (SE > 8.274057)
		          {
		            if (SCH <= -0.310518)
		            {
		              if (SCH <= -0.310845) { result = 07.9; /* 5.0/3.0 */}
		              else if (SCH > -0.310845) { result = 08.5; /* 2.0/1.0 */}
		            }
		            if (SCH > -0.310518)
		            {
		              if (SCH <= -0.305857)
		              {
		                if (EE <= 7.07075) { result = 07.6; /* 2.0/1.0 */}
		                else if (EE > 7.07075)
		                {
		                  if (SE <= 8.357012) { result = 08.3; /* 6.0/3.0 */}
		                  else if (SE > 8.357012) { result = 07.8; /* 3.0/2.0 */}
		                }
		              }
		              if (SCH > -0.305857)
		              {
		                if (SE <= 8.360428) { result = 07.7; /* 3.0 */}
		                else if (SE > 8.360428) { result = 07.9; /* 2.0/1.0 */}
		              }
		            }
		          }
		        }
		        if (EE > 7.813)
		        {
		          if (EE <= 8.079)
		          {
		            if (EE <= 7.994375)
		            {
		              if (SE <= 8.376288) { result = 08.0; /* 4.0/1.0 */}
		              else if (SE > 8.376288) { result = 07.5; /* 2.0 */}
		            }
		            if (EE > 7.994375) { result = 07.6; /* 3.0 */}
		          }
		          if (EE > 8.079)
		          {
		            if (SCH <= -0.303186) { result = 07.7; /* 2.0/1.0 */}
		            else if (SCH > -0.303186) { result = 08.0; /* 4.0 */}
		          }
		        }
		      }
		      if (EE > 8.234438)
		      {
		        if (EE <= 8.70775)
		        {
		          if (EE <= 8.326844) { result = 07.9; /* 5.0/1.0 */}
		          else if (EE > 8.326844)
		          {
		            if (SCH <= -0.301277) { result = 08.1; /* 5.0/2.0 */}
		            else if (SCH > -0.301277) { result = 07.7; /* 2.0/1.0 */}
		          }
		        }
		        if (EE > 8.70775)
		        {
		          if (SCH <= -0.299978)
		          {
		            if (SE <= 8.391832) { result = 07.0; /* 4.0/2.0 */}
		            else if (SE > 8.391832) { result = 07.8; /* 2.0 */}
		          }
		          if (SCH > -0.299978)
		          {
		            if (SE <= 8.289405) { result = 07.7; /* 2.0 */}
		            else if (SE > 8.289405) { result = 07.9; /* 3.0 */}
		          }
		        }
		      }
		    }
		    if (SE > 8.42798)
		    {
		      if (EE <= 8.732)
		      {
		        if (EE <= 7.673875)
		        {
		          if (EE <= 6.99075) { result = 08.6; /* 5.0/2.0 */}
		          else if (EE > 6.99075)
		          {
		            if (SE <= 8.672826) { result = 08.5; /* 4.0/2.0 */}
		            else if (SE > 8.672826)
		            {
		              if (SE <= 8.724731) { result = 08.6; /* 3.0/1.0 */}
		              else if (SE > 8.724731) { result = 08.4; /* 2.0/1.0 */}
		            }
		          }
		        }
		        if (EE > 7.673875)
		        {
		          if (SCH <= -0.1603)
		          {
		            if (SCH <= -0.16701) { result = 08.7; /* 4.0/2.0 */}
		            else if (SCH > -0.16701) { result = 08.4; /* 9.0/2.0 */}
		          }
		          if (SCH > -0.1603)
		          {
		            if (EE <= 8.4175)
		            {
		              if (SE <= 8.75386) { result = 08.3; /* 2.0 */}
		              else if (SE > 8.75386) { result = 08.1; /* 2.0/1.0 */}
		            }
		            if (EE > 8.4175) { result = 07.9; /* 3.0/2.0 */}
		          }
		        }
		      }
		      if (EE > 8.732)
		      {
		        if (SE <= 8.707362)
		        {
		          if (SE <= 8.460227) { result = 07.7; /* 3.0/1.0 */}
		          else if (SE > 8.460227) { result = 08.3; /* 4.0/2.0 */}
		        }
		        if (SE > 8.707362)
		        {
		          if (EE <= 9.536)
		          {
		            if (EE <= 9.24575) { result = 08.2; /* 2.0/1.0 */}
		            else if (EE > 9.24575) { result = 08.1; /* 3.0/1.0 */}
		          }
		          if (EE > 9.536) { result = 07.8; /* 6.0/3.0 */}
		        }
		      }
		    }
		  }
		}
		if (SE > 8.80606)
		{
		  if (PE <= 4.260194)
		  {
		    if (EE <= 4.862859)
		    {
		      if (EE <= 4.0805)
		      {
		        if (SCH <= -0.395706)
		        {
		          if (SCH <= -0.398947) { result = 12.6; /* 3.0/1.0 */}
		          else if (SCH > -0.398947)
		          {
		            if (SCH <= -0.395985) { result = 09.1; /* 2.0/1.0 */}
		            else if (SCH > -0.395985)
		            {
		              if (EE <= 3.795813) { result = 09.2; /* 4.0/2.0 */}
		              else if (EE > 3.795813) { result = 10.4; /* 3.0/1.0 */}
		            }
		          }
		        }
		        if (SCH > -0.395706)
		        {
		          if (EE <= 3.566) { result = 07.5; /* 3.0/2.0 */}
		          else if (EE > 3.566)
		          {
		            if (EE <= 3.795813) { result = 09.3; /* 2.0/1.0 */}
		            else if (EE > 3.795813) { result = 10.0; /* 5.0/3.0 */}
		          }
		        }
		      }
		      if (EE > 4.0805)
		      {
		        if (SCH <= -0.382949)
		        {
		          if (PE <= 3.172064)
		          {
		            if (EE <= 4.509125) { result = 09.8; /* 5.0/2.0 */}
		            else if (EE > 4.509125) { result = 09.7; /* 3.0 */}
		          }
		          if (PE > 3.172064)
		          {
		            if (SCH <= -0.388693)
		            {
		              if (SE <= 9.323245) { result = 09.6; /* 2.0 */}
		              else if (SE > 9.323245)
		              {
		                if (EE <= 4.509125) { result = 09.5; /* 2.0 */}
		                else if (EE > 4.509125) { result = 09.9; /* 4.0/2.0 */}
		              }
		            }
		            if (SCH > -0.388693) { result = 09.7; /* 2.0 */}
		          }
		        }
		        if (SCH > -0.382949)
		        {
		          if (SCH <= -0.371513) { result = 09.6; /* 4.0/2.0 */}
		          else if (SCH > -0.371513) { result = 09.4; /* 3.0/2.0 */}
		        }
		      }
		    }
		    if (EE > 4.862859)
		    {
		      if (SE <= 9.561631)
		      {
		        if (EE <= 6.120359)
		        {
		          if (SCH <= -0.389218)
		          {
		            if (EE <= 5.2105)
		            {
		              if (SE <= 9.325385) { result = 09.2; /* 2.0/1.0 */}
		              else if (SE > 9.325385) { result = 09.1; /* 5.0/3.0 */}
		            }
		            if (EE > 5.2105)
		            {
		              if (EE <= 5.422063) { result = 09.0; /* 2.0/1.0 */}
		              else if (EE > 5.422063)
		              {
		                if (EE <= 5.559375) { result = 09.6; /* 3.0/1.0 */}
		                else if (EE > 5.559375) { result = 09.0; /* 2.0/1.0 */}
		              }
		            }
		          }
		          if (SCH > -0.389218)
		          {
		            if (EE <= 5.64575)
		            {
		              if (EE <= 5.295125) { result = 09.4; /* 9.0/5.0 */}
		              else if (EE > 5.295125) { result = 09.2; /* 6.0/3.0 */}
		            }
		            if (EE > 5.64575)
		            {
		              if (EE <= 5.87375)
		              {
		                if (SCH <= -0.38064) { result = 09.3; /* 5.0/1.0 */}
		                else if (SCH > -0.38064) { result = 09.4; /* 4.0/2.0 */}
		              }
		              if (EE > 5.87375)
		              {
		                if (EE <= 6.050813) { result = 09.2; /* 2.0 */}
		                else if (EE > 6.050813) { result = 09.3; /* 3.0/1.0 */}
		              }
		            }
		          }
		        }
		        if (EE > 6.120359)
		        {
		          if (SCH <= -0.387072) { result = 09.4; /* 2.0 */}
		          else if (SCH > -0.387072)
		          {
		            if (SE <= 9.208068) { result = 09.3; /* 2.0/1.0 */}
		            else if (SE > 9.208068)
		            {
		              if (EE <= 8.617)
		              {
		                if (SCH <= -0.374181)
		                {
		                  if (EE <= 6.764188) { result = 09.2; /* 11.0/4.0 */}
		                  else if (EE > 6.764188) { result = 09.1; /* 7.0/2.0 */}
		                }
		                if (SCH > -0.374181) { result = 09.2; /* 4.0 */}
		              }
		              if (EE > 8.617) { result = 09.1; /* 5.0 */}
		            }
		          }
		        }
		      }
		      if (SE > 9.561631)
		      {
		        if (EE <= 7.141875)
		        {
		          if (SCH <= -0.143549)
		          {
		            if (EE <= 5.479375) { result = 09.8; /* 3.0/2.0 */}
		            else if (EE > 5.479375)
		            {
		              if (EE <= 5.709125) { result = 09.0; /* 2.0 */}
		              else if (EE > 5.709125) { result = 09.3; /* 2.0/1.0 */}
		            }
		          }
		          if (SCH > -0.143549)
		          {
		            if (SCH <= -0.139872)
		            {
		              if (EE <= 6.331813) { result = 10.1; /* 4.0/1.0 */}
		              else if (EE > 6.331813) { result = 10.0; /* 7.0/3.0 */}
		            }
		            if (SCH > -0.139872) { result = 09.9; /* 4.0/1.0 */}
		          }
		        }
		        if (EE > 7.141875)
		        {
		          if (SE <= 10.155788)
		          {
		            if (SE <= 10.12694) { result = 08.8; /* 2.0/1.0 */}
		            else if (SE > 10.12694) { result = 09.7; /* 3.0/1.0 */}
		          }
		          if (SE > 10.155788) { result = 09.2; /* 4.0/2.0 */}
		        }
		      }
		    }
		  }
		  if (PE > 4.260194)
		  {
		    if (EE <= 6.19275)
		    {
		      if (EE <= 5.2105)
		      {
		        if (SE <= 10.475336) { result = 10.8; /* 3.0/1.0 */}
		        else if (SE > 10.475336) { result = 10.2; /* 4.0/2.0 */}
		      }
		      if (EE > 5.2105)
		      {
		        if (EE <= 5.39475)
		        {
		          if (SE <= 10.47544) { result = 10.1; /* 2.0 */}
		          else if (SE > 10.47544) { result = 10.7; /* 2.0 */}
		        }
		        if (EE > 5.39475)
		        {
		          if (SE <= 10.510052) { result = 09.7; /* 2.0/1.0 */}
		          else if (SE > 10.510052) { result = 10.1; /* 2.0/1.0 */}
		        }
		      }
		    }
		    if (EE > 6.19275)
		    {
		      if (SCH <= -0.080603) { result = 09.0; /* 6.0/1.0 */}
		      else if (SCH > -0.080603)
		      {
		        if (SE <= 9.271976) { result = 08.9; /* 6.0/1.0 */}
		        else if (SE > 9.271976) { result = 08.7; /* 3.0 */}
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
	public IReactionSet getReactionSet()
    {
		return reactionSet;
	}
	/**
	 * Calculate the necessary descriptors for Heteratom atoms
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private double[] calculateHeteroAtomConjugatedDescriptor(IAtom atom, IAtomContainer atomContainer, IAtomContainer conjugatedSys) throws CDKException {
		double[] results = new double[4];
		results[0] = -10.0;
		results[1] = 0.0;
		results[2] = 0.0;
		results[3] = 0.0;
		
		/*calculation of the atomic descriptors*/
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			if(atomContainer.getConnectedLonePairsCount(atomsss) == 0){
				PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
				double result1;
					result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
				
				if(result1 != 0.0)
				if(result1 > results[0])
					results[0] = result1;
			}else{
				
				PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
				double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
				results[1] = result1;
			}
			
			SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
			double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
			results[3] += result2;
			
		}
		/*calculation of the bond descriptors*/
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			IBond bondsss = (IBond) bondIt.next();
			
			ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar;
			
			dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
			double result1 = dar.get(0);
			double resutt2 = dar.get(1);
			double result12 = (result1+resutt2);
			
			double resultT = 0;
			if(result12 != 0)
				resultT = result12/2;
			
			results[2] += resultT;
			
		}
		if(results[2] != 0)
			results[2] = results[1]/conjugatedSys.getAtomCount();
		
		if(results[3] != 0)
			results[3] = results[2]/conjugatedSys.getAtomCount();
		
		
		return results;
    	
	}
	/**
	 * Calculate the necessary descriptors for Heteratom atoms
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private double[] calculateHeteroAtomDescriptor(IAtom atom, IAtomContainer atomContainer) throws CDKException {
		double[] results = new double[4];
		SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
		EffectiveAtomPolarizabilityDescriptor descriptor3 = new EffectiveAtomPolarizabilityDescriptor();
		PiElectronegativityDescriptor descriptor4 = new PiElectronegativityDescriptor();

		results[0]= ((DoubleResult)descriptor1.calculate(atom,atomContainer).getValue()).doubleValue();
		results[1]= ((DoubleResult)descriptor2.calculate(atom,atomContainer).getValue()).doubleValue();
		results[2]= ((DoubleResult)descriptor3.calculate(atom,atomContainer).getValue()).doubleValue();
		results[3]= ((DoubleResult)descriptor4.calculate(atom,atomContainer).getValue()).doubleValue();
    	
		return results;
	}
	/**
	 * Calculate the necessary descriptors for Carbonyl group
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private double[] calculateCarbonylDescriptor(IAtom atom, IAtomContainer atomContainer) {
		
		double[] results = new double[6];
        IAtom positionC = null;
		List<IAtom> listAtoms = atomContainer.getConnectedAtomsList(atom);
        for (IAtom listAtom : listAtoms) {
            if (atomContainer.getBond(atom, listAtom).getOrder() <= 1) {
                continue;
            }
            positionC = listAtom;
        }

        IBond bond = atomContainer.getBond(atom, positionC);
		try {
			AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(atomContainer);
			
			if(conjugatedPi.getAtomContainerCount() == 1){
				IAtomContainer conjugatedSy = conjugatedPi.getAtomContainer(0);
				Iterator atomIt = conjugatedSy.atoms();
				while(atomIt.hasNext()){
					IAtom atomsss = (IAtom) atomIt.next();
					if(atomsss.getSymbol().equals("C")){
						PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
						double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();

						if(result1 > results[1])
							results[1] = result1;
						
						SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
						double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
						results[0] += result2;
					}
				}
				if(results[0] != 0)
					results[0] = results[0]/conjugatedSy.getAtomCount();
				
				if(results[1] != 0)
					results[1] = results[1]/conjugatedSy.getAtomCount();
			}else{
				/* 1 */
				try{
				SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
				results[0] = ((DoubleResult)descriptor1.calculate(positionC,(IAtomContainer) atomContainer).getValue()).doubleValue();

				}catch(Exception e){
					results[0] = 0.0;
				}
//				System.out.println("symbolC: "+atomContainer.getAtom(positionC).getSymbol());
//				System.out.println("symbolX: "+atomContainer.getAtom(positionX).getSymbol());
				
				/* 2 */
				try{
				PartialPiChargeDescriptor descriptor2 = new PartialPiChargeDescriptor();
				results[1] = ((DoubleResult)descriptor2.calculate(positionC,(IAtomContainer) atomContainer).getValue()).doubleValue();
				}catch(Exception e){
					results[1] = 0.0;
				}
			}
			/* 3 */
			try{
			BondPartialSigmaChargeDescriptor descriptor3 = new BondPartialSigmaChargeDescriptor();
			results[2] = ((DoubleResult)descriptor3.calculate(bond,(IAtomContainer) atomContainer).getValue()).doubleValue();
			}catch(Exception e){
				results[2] = 0.0;
			}
			/* 4 */
			try{
			SigmaElectronegativityDescriptor descriptor4 = new SigmaElectronegativityDescriptor();
			results[3] = ((DoubleResult)descriptor4.calculate(atom,(IAtomContainer) atomContainer).getValue()).doubleValue();
			}catch(Exception e){
				results[3] = 0.0;
			}
			/* 5 */
			try{
			PartialPiChargeDescriptor descriptor5 = new PartialPiChargeDescriptor();
			results[4] = ((DoubleResult)descriptor5.calculate(atom,(IAtomContainer) atomContainer).getValue()).doubleValue();
			}catch(Exception e){
				results[4] = 0.0;
			}
			/* 6 */
			try{
			ResonancePositiveChargeDescriptor descriptor6 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor6.calculate(bond,atomContainer).getValue());
			double datT =0.0;
			if((new Double(dar.get(0))).toString().equals("Infinity")||
					(new Double(dar.get(1))).toString().equals("Infinity")){}
			else if(dar.get(0) == 0 && dar.get(1) == 0){}
			else
				datT = (dar.get(0)+dar.get(1))/2;
			results[5] = datT;
			}catch(Exception e){
				results[5] = 0.0;
			}
 
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	 /**
     * Gets the parameterNames attribute of the IPAtomicDescriptor object.
     *
     * @return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[0];
    }


    /**
     * Gets the parameterType attribute of the IPAtomicDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    public Object getParameterType(String name) {
        return null;
    }
}


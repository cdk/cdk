/* $Revision: 6228 $ $Author: egonw $ $Date: 2006-05-11 18:34:42 +0200 (Thu, 11 May 2006) $
 *
 * Copyright (C) 2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
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

/**
 *  This class returns the ionization potential of an atom. It is
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values. Up to now is
 *  only possible predict for Cl,Br,I,N,P,O,S Atoms and they are not belong to
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
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 * 
 */
public class IPAtomicDescriptor implements IAtomicDescriptor {
	
	/** parameter for inizate IReactionSet*/
	private boolean setEnergy = false;

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
        return new Object[0];
    }
	/**
	 *  This method calculates the ionization potential of an atom.
	 *
	 *@param  chemObj           The IAtom to ionize.
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential. Not possible the ionization.
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer container) throws CDKException{
		reactionSet = container.getBuilder().newReactionSet();
    	double resultD = -1.0;
		Double[][] resultsH = null;

		if(atom.getSymbol().equals("F")||
					atom.getSymbol().equals("Cl")||
					atom.getSymbol().equals("Br")||
					atom.getSymbol().equals("I")||
					atom.getSymbol().equals("N")||
					atom.getSymbol().equals("S")||
					atom.getSymbol().equals("O")||
					atom.getSymbol().equals("P")){
				if(container.getMaximumBondOrder(atom) > 1 && atom.getSymbol().equals("O")){
					resultsH = calculateCarbonylDescriptor(atom, container);
					resultD = getTreeCarbonyl(resultsH);
					resultD += 0.05;
				}else{
					resultsH = calculateHeteroAtomDescriptor(atom, container);
					resultD = getTreeHeteroAtom(resultsH);
					resultD += 0.05;
				}
		}
		/* extract reaction*/
		if(setEnergy){
			if(container.getLonePairCount(atom) > 0){
				IMoleculeSet setOfReactants = container.getBuilder().newMoleculeSet();
				setOfReactants.addMolecule((IMolecule) container);
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
	private double getTreeCarbonyl(Double[][] resultsH) {
		double result = 0.0;
		double SE_c = (resultsH[0][0]).doubleValue();
		double PCH_c = (resultsH[0][1]).doubleValue();
		double SB  = (resultsH[0][2]).doubleValue();
		double SE_x = (resultsH[0][3]).doubleValue();
		double PCH_x = (resultsH[0][4]).doubleValue();
		double RES_c = (resultsH[0][5]).doubleValue();
		if (SB <= 0.422256)
		{
		  if (PCH_c <= 0.011778) { result = 09.4; /* 7.0/5.0 */}
		  else if (PCH_c > 0.011778)
		  {
		    if (PCH_c <= 0.016985)
		    {
		      if (SE_c <= 9.930078) { result = 09.8; /* 4.0/2.0 */}
		      else if (SE_c > 9.930078)
		      {
		        if (SB <= 0.420594)
		        {
		          if (SB <= 0.418344) { result = 09.7; /* 4.0/2.0 */}
		          else if (SB > 0.418344) { result = 09.6; /* 4.0/2.0 */}
		        }
		        if (SB > 0.420594) { result = 09.5; /* 4.0/2.0 */}
		      }
		    }
		    if (PCH_c > 0.016985)
		    {
		      if (RES_c <= 0.278324)
		      {
		        if (SE_c <= 8.422177) { result = 09.3; /* 2.0/1.0 */}
		        else if (SE_c > 8.422177) { result = 08.4; /* 2.0/1.0 */}
		      }
		      if (RES_c > 0.278324) { result = 09.7; /* 6.0/4.0 */}
		    }
		  }
		}
		if (SB > 0.422256)
		{
		  if (SB <= 0.431267)
		  {
		    if (SB <= 0.427268)
		    {
		      if (RES_c <= 0.371098) { result = 08.5; /* 3.0/2.0 */}
		      else if (RES_c > 0.371098)
		      {
		        if (PCH_c <= 0.016985) { result = 09.0; /* 2.0 */}
		        else if (PCH_c > 0.016985)
		        {
		          if (PCH_x <= -0.015157) { result = 09.3; /* 12.0/6.0 */}
		          else if (PCH_x > -0.015157) { result = 09.4; /* 2.0/1.0 */}
		        }
		      }
		    }
		    if (SB > 0.427268) { result = 09.1; /* 44.0/28.0 */}
		  }
		  if (SB > 0.431267)
		  {
		    if (RES_c <= 0.371098)
		    {
		      if (PCH_c <= 0.025012)
		      {
		        if (RES_c <= 0)
		        {
		          if (PCH_c <= 0.015108)
		          {
		            if (SB <= 0.502853) { result = 08.8; /* 3.0/1.0 */}
		            else if (SB > 0.502853) { result = 08.7; /* 2.0/1.0 */}
		          }
		          if (PCH_c > 0.015108) { result = 08.6; /* 2.0 */}
		        }
		        if (RES_c > 0)
		        {
		          if (PCH_c <= 0.01453)
		          {
		            if (RES_c <= 0.247399) { result = 08.2; /* 2.0/1.0 */}
		            else if (RES_c > 0.247399) { result = 08.6; /* 3.0/1.0 */}
		          }
		          if (PCH_c > 0.01453) { result = 08.2; /* 2.0 */}
		        }
		      }
		      if (PCH_c > 0.025012)
		      {
		        if (PCH_c <= 0.029206) { result = 09.1; /* 4.0/1.0 */}
		        else if (PCH_c > 0.029206) { result = 08.7; /* 3.0/2.0 */}
		      }
		    }
		    if (RES_c > 0.371098)
		    {
		      if (SE_x <= 13.008931)
		      {
		        if (RES_c <= 0.556647)
		        {
		          if (PCH_c <= 0.029982) { result = 08.8; /* 7.0/3.0 */}
		          else if (PCH_c > 0.029982) { result = 08.9; /* 4.0/2.0 */}
		        }
		        if (RES_c > 0.556647)
		        {
		          if (SE_c <= 10.118553) { result = 08.5; /* 2.0/1.0 */}
		          else if (SE_c > 10.118553) { result = 08.7; /* 2.0/1.0 */}
		        }
		      }
		      if (SE_x > 13.008931)
		      {
		        if (PCH_x <= -0.014547)
		        {
		          if (SE_c <= 10.13501) { result = 08.7; /* 2.0 */}
		          else if (SE_c > 10.13501) { result = 08.4; /* 3.0/2.0 */}
		        }
		        if (PCH_x > -0.014547) { result = 08.9; /* 7.0/1.0 */}
		      }
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
	private double getTreeHeteroAtom(Double[][] resultsH) {
		double result = 0.0;
		double SE = (resultsH[0][0]).doubleValue();
		double SCH = (resultsH[0][1]).doubleValue();
		double EE  = (resultsH[0][2]).doubleValue();
		
		if (SE <= 9.104677)
		{
		  if (EE <= 5.254344)
		  {
		    if (EE <= 2.5625)
		    {
		      if (EE <= 1.4725)
		      {
		        if (SE <= 8.1475)
		        {
		          if (SE <= 0) { result = 13.0; /* 2.0/1.0 */}
		          else if (SE > 0) { result = 09.8; /* 3.0/1.0 */}
		        }
		        if (SE > 8.1475) { result = 12.6; /* 6.0/2.0 */}
		      }
		      if (EE > 1.4725)
		      {
		        if (SCH <= -0.308562) { result = 10.0; /* 5.0/1.0 */}
		        else if (SCH > -0.308562)
		        {
		          if (SE <= 8.466176) { result = 09.1; /* 2.0/1.0 */}
		          else if (SE > 8.466176) { result = 10.7; /* 2.0/1.0 */}
		        }
		      }
		    }
		    if (EE > 2.5625)
		    {
		      if (SE <= 8.142501)
		      {
		        if (EE <= 4.697203)
		        {
		          if (EE <= 3.566) { result = 06.2; /* 2.0/1.0 */}
		          else if (EE > 3.566) { result = 09.3; /* 8.0/5.0 */}
		        }
		        if (EE > 4.697203)
		        {
		          if (EE <= 4.9695)
		          {
		            if (SE <= 8.0378) { result = 08.1; /* 2.0/1.0 */}
		            else if (SE > 8.0378) { result = 08.6; /* 7.0/3.0 */}
		          }
		          if (EE > 4.9695) { result = 08.5; /* 4.0/2.0 */}
		        }
		      }
		      if (SE > 8.142501)
		      {
		        if (EE <= 4.666375)
		        {
		          if (SE <= 8.388638) { result = 09.4; /* 7.0/4.0 */}
		          else if (SE > 8.388638) { result = 05.1; /* 3.0/2.0 */}
		        }
		        if (EE > 4.666375)
		        {
		          if (SE <= 8.224518) { result = 05.9; /* 3.0/2.0 */}
		          else if (SE > 8.224518)
		          {
		            if (SE <= 8.391308) { result = 08.6; /* 2.0/1.0 */}
		            else if (SE > 8.391308) { result = 07.5; /* 2.0/1.0 */}
		          }
		        }
		      }
		    }
		  }
		  if (EE > 5.254344)
		  {
		    if (EE <= 7.7465)
		    {
		      if (SE <= 8.311315)
		      {
		        if (SCH <= -0.318681)
		        {
		          if (SE <= 8.17884)
		          {
		            if (SE <= 8.1475) { result = 08.4; /* 4.0/2.0 */}
		            else if (SE > 8.1475) { result = 08.5; /* 5.0/2.0 */}
		          }
		          if (SE > 8.17884) { result = 08.1; /* 3.0/2.0 */}
		        }
		        if (SCH > -0.318681)
		        {
		          if (EE <= 6.934)
		          {
		            if (SCH <= -0.312287)
		            {
		              if (SCH <= -0.315987) { result = 08.6; /* 4.0/2.0 */}
		              else if (SCH > -0.315987)
		              {
		                if (SCH <= -0.315012) { result = 08.0; /* 7.0/3.0 */}
		                else if (SCH > -0.315012) { result = 08.6; /* 4.0/2.0 */}
		              }
		            }
		            if (SCH > -0.312287)
		            {
		              if (SE <= 8.163774) { result = 06.8; /* 2.0/1.0 */}
		              else if (SE > 8.163774) { result = 05.7; /* 2.0/1.0 */}
		            }
		          }
		          if (EE > 6.934)
		          {
		            if (SE <= 8.267991) { result = 07.8; /* 4.0/2.0 */}
		            else if (SE > 8.267991) { result = 08.5; /* 5.0/3.0 */}
		          }
		        }
		      }
		      if (SE > 8.311315)
		      {
		        if (EE <= 6.8065)
		        {
		          if (EE <= 6.287) { result = 09.2; /* 7.0/3.0 */}
		          else if (EE > 6.287)
		          {
		            if (SE <= 8.435289) { result = 07.7; /* 2.0/1.0 */}
		            else if (SE > 8.435289)
		            {
		              if (SE <= 8.61075) { result = 09.1; /* 4.0 */}
		              else if (SE > 8.61075) { result = 08.6; /* 2.0 */}
		            }
		          }
		        }
		        if (EE > 6.8065)
		        {
		          if (SCH <= -0.305241)
		          {
		            if (SCH <= -0.308632)
		            {
		              if (EE <= 6.97525) { result = 08.8; /* 2.0/1.0 */}
		              else if (EE > 6.97525) { result = 08.3; /* 3.0 */}
		            }
		            if (SCH > -0.308632)
		            {
		              if (EE <= 7.488688)
		              {
		                if (SCH <= -0.306392) { result = 07.5; /* 2.0/1.0 */}
		                else if (SCH > -0.306392) { result = 07.8; /* 3.0/1.0 */}
		              }
		              if (EE > 7.488688)
		              {
		                if (EE <= 7.6195) { result = 07.7; /* 3.0 */}
		                else if (EE > 7.6195) { result = 08.2; /* 2.0/1.0 */}
		              }
		            }
		          }
		          if (SCH > -0.305241)
		          {
		            if (SCH <= -0.23627)
		            {
		              if (SE <= 8.390548) { result = 08.7; /* 2.0 */}
		              else if (SE > 8.390548)
		              {
		                if (SE <= 8.435289) { result = 08.2; /* 3.0/1.0 */}
		                else if (SE > 8.435289) { result = 08.6; /* 2.0 */}
		              }
		            }
		            if (SCH > -0.23627)
		            {
		              if (SCH <= -0.152623)
		              {
		                if (EE <= 7.7)
		                {
		                  if (SE <= 8.705097)
		                  {
		                    if (SE <= 8.681096) { result = 08.8; /* 5.0/3.0 */}
		                    else if (SE > 8.681096) { result = 08.6; /* 2.0 */}
		                  }
		                  if (SE > 8.705097) { result = 08.5; /* 3.0/1.0 */}
		                }
		                if (EE > 7.7) { result = 08.4; /* 2.0/1.0 */}
		              }
		              if (SCH > -0.152623) { result = 08.8; /* 3.0 */}
		            }
		          }
		        }
		      }
		    }
		    if (EE > 7.7465)
		    {
		      if (SE <= 8.495417)
		      {
		        if (EE <= 8.091063)
		        {
		          if (SE <= 8.389591)
		          {
		            if (EE <= 7.813)
		            {
		              if (EE <= 7.764625) { result = 07.7; /* 2.0 */}
		              else if (EE > 7.764625)
		              {
		                if (SE <= 8.364009) { result = 08.2; /* 2.0/1.0 */}
		                else if (SE > 8.364009) { result = 07.6; /* 3.0/1.0 */}
		              }
		            }
		            if (EE > 7.813)
		            {
		              if (SE <= 8.376114)
		              {
		                if (EE <= 7.871438) { result = 07.6; /* 2.0 */}
		                else if (EE > 7.871438)
		                {
		                  if (EE <= 7.997625) { result = 08.0; /* 4.0/1.0 */}
		                  else if (EE > 7.997625) { result = 07.6; /* 2.0 */}
		                }
		              }
		              if (SE > 8.376114)
		              {
		                if (EE <= 7.997625) { result = 07.5; /* 2.0 */}
		                else if (EE > 7.997625) { result = 07.6; /* 2.0/1.0 */}
		              }
		            }
		          }
		          if (SE > 8.389591) { result = 08.4; /* 4.0/1.0 */}
		        }
		        if (EE > 8.091063)
		        {
		          if (EE <= 8.743469)
		          {
		            if (EE <= 8.332625)
		            {
		              if (SE <= 8.335921) { result = 08.2; /* 4.0/2.0 */}
		              else if (SE > 8.335921) { result = 07.9; /* 13.0/8.0 */}
		            }
		            if (EE > 8.332625)
		            {
		              if (SCH <= -0.301785)
		              {
		                if (EE <= 8.381) { result = 08.1; /* 2.0 */}
		                else if (EE > 8.381) { result = 07.5; /* 3.0/1.0 */}
		              }
		              if (SCH > -0.301785)
		              {
		                if (SE <= 8.411249) { result = 07.7; /* 2.0 */}
		                else if (SE > 8.411249) { result = 08.2; /* 3.0/1.0 */}
		              }
		            }
		          }
		          if (EE > 8.743469)
		          {
		            if (SCH <= -0.298112)
		            {
		              if (SCH <= -0.299978)
		              {
		                if (SE <= 8.388052) { result = 07.0; /* 4.0/2.0 */}
		                else if (SE > 8.388052) { result = 07.3; /* 2.0/1.0 */}
		              }
		              if (SCH > -0.299978) { result = 07.9; /* 4.0/1.0 */}
		            }
		            if (SCH > -0.298112)
		            {
		              if (SE <= 8.446749) { result = 07.7; /* 6.0/2.0 */}
		              else if (SE > 8.446749)
		              {
		                if (SE <= 8.466176) { result = 07.2; /* 2.0/1.0 */}
		                else if (SE > 8.466176) { result = 07.3; /* 2.0/1.0 */}
		              }
		            }
		          }
		        }
		      }
		      if (SE > 8.495417)
		      {
		        if (EE <= 8.864938)
		        {
		          if (SE <= 8.716941)
		          {
		            if (SE <= 8.643369) { result = 08.7; /* 2.0 */}
		            else if (SE > 8.643369)
		            {
		              if (SE <= 8.704665) { result = 08.4; /* 6.0/1.0 */}
		              else if (SE > 8.704665)
		              {
		                if (EE <= 8.44775) { result = 08.4; /* 3.0/1.0 */}
		                else if (EE > 8.44775) { result = 08.2; /* 3.0 */}
		              }
		            }
		          }
		          if (SE > 8.716941)
		          {
		            if (EE <= 8.4175) { result = 08.3; /* 4.0/1.0 */}
		            else if (EE > 8.4175)
		            {
		              if (SE <= 8.761634) { result = 08.2; /* 2.0/1.0 */}
		              else if (SE > 8.761634) { result = 08.5; /* 3.0/1.0 */}
		            }
		          }
		        }
		        if (EE > 8.864938)
		        {
		          if (EE <= 9.707625)
		          {
		            if (EE <= 9.255125) { result = 08.3; /* 5.0/1.0 */}
		            else if (EE > 9.255125)
		            {
		              if (SE <= 8.761634)
		              {
		                if (EE <= 9.352313) { result = 08.2; /* 3.0/1.0 */}
		                else if (EE > 9.352313) { result = 08.1; /* 3.0 */}
		              }
		              if (SE > 8.761634) { result = 08.2; /* 4.0/2.0 */}
		            }
		          }
		          if (EE > 9.707625)
		          {
		            if (SE <= 8.761634) { result = 07.8; /* 5.0/1.0 */}
		            else if (SE > 8.761634)
		            {
		              if (SE <= 8.790851) { result = 08.0; /* 2.0/1.0 */}
		              else if (SE > 8.790851) { result = 08.2; /* 2.0/1.0 */}
		            }
		          }
		        }
		      }
		    }
		  }
		}
		if (SE > 9.104677)
		{
		  if (EE <= 3.723)
		  {
		    if (EE <= 2.6955)
		    {
		      if (SCH <= -0.293894)
		      {
		        if (SE <= 11.032853) { result = 07.5; /* 6.0/3.0 */}
		        else if (SE > 11.032853) { result = 08.1; /* 3.0/2.0 */}
		      }
		      if (SCH > -0.293894)
		      {
		        if (SCH <= -0.261302) { result = 13.6; /* 4.0/2.0 */}
		        else if (SCH > -0.261302) { result = 10.7; /* 5.0/3.0 */}
		      }
		    }
		    if (EE > 2.6955)
		    {
		      if (SE <= 12.932768)
		      {
		        if (SE <= 10.484963)
		        {
		          if (EE <= 3.509625)
		          {
		            if (EE <= 3.2585) { result = 06.7; /* 3.0/2.0 */}
		            else if (EE > 3.2585) { result = 10.4; /* 4.0/1.0 */}
		          }
		          if (EE > 3.509625)
		          {
		            if (SE <= 9.381854) { result = 09.6; /* 2.0/1.0 */}
		            else if (SE > 9.381854) { result = 10.5; /* 3.0/1.0 */}
		          }
		        }
		        if (SE > 10.484963)
		        {
		          if (SCH <= -0.236569) { result = 10.2; /* 4.0/2.0 */}
		          else if (SCH > -0.236569)
		          {
		            if (SCH <= -0.233254) { result = 09.2; /* 3.0 */}
		            else if (SCH > -0.233254) { result = 09.4; /* 2.0/1.0 */}
		          }
		        }
		      }
		      if (SE > 12.932768)
		      {
		        if (SCH <= -0.284044)
		        {
		          if (SCH <= -0.299479)
		          {
		            if (EE <= 3.3505) { result = 09.8; /* 3.0/1.0 */}
		            else if (EE > 3.3505) { result = 09.6; /* 15.0/10.0 */}
		          }
		          if (SCH > -0.299479) { result = 09.1; /* 8.0/6.0 */}
		        }
		        if (SCH > -0.284044)
		        {
		          if (EE <= 3.408813) { result = 09.9; /* 5.0/3.0 */}
		          else if (EE > 3.408813) { result = 11.8; /* 4.0/2.0 */}
		        }
		      }
		    }
		  }
		  if (EE > 3.723)
		  {
		    if (SCH <= -0.380455)
		    {
		      if (EE <= 4.985813)
		      {
		        if (SE <= 9.323366)
		        {
		          if (SCH <= -0.395695) { result = 10.4; /* 5.0/3.0 */}
		          else if (SCH > -0.395695)
		          {
		            if (EE <= 4.697203)
		            {
		              if (EE <= 4.0185)
		              {
		                if (SE <= 9.291423) { result = 09.5; /* 2.0/1.0 */}
		                else if (SE > 9.291423) { result = 09.6; /* 2.0/1.0 */}
		              }
		              if (EE > 4.0185)
		              {
		                if (SCH <= -0.392483)
		                {
		                  if (SCH <= -0.395372) { result = 07.1; /* 2.0/1.0 */}
		                  else if (SCH > -0.395372) { result = 09.7; /* 7.0/2.0 */}
		                }
		                if (SCH > -0.392483) { result = 09.5; /* 3.0/1.0 */}
		              }
		            }
		            if (EE > 4.697203) { result = 09.6; /* 6.0/3.0 */}
		          }
		        }
		        if (SE > 9.323366)
		        {
		          if (SCH <= -0.391062)
		          {
		            if (SE <= 9.332876)
		            {
		              if (SE <= 9.327451) { result = 09.3; /* 2.0 */}
		              else if (SE > 9.327451) { result = 09.1; /* 3.0/1.0 */}
		            }
		            if (SE > 9.332876)
		            {
		              if (SE <= 9.337486) { result = 08.1; /* 3.0/2.0 */}
		              else if (SE > 9.337486) { result = 08.2; /* 3.0/2.0 */}
		            }
		          }
		          if (SCH > -0.391062)
		          {
		            if (EE <= 4.5725)
		            {
		              if (EE <= 4.12225) { result = 10.0; /* 3.0/1.0 */}
		              else if (EE > 4.12225)
		              {
		                if (SE <= 9.368092) { result = 09.5; /* 2.0 */}
		                else if (SE > 9.368092) { result = 09.6; /* 2.0 */}
		              }
		            }
		            if (EE > 4.5725)
		            {
		              if (EE <= 4.91325)
		              {
		                if (SCH <= -0.389218) { result = 09.9; /* 2.0/1.0 */}
		                else if (SCH > -0.389218)
		                {
		                  if (EE <= 4.61625) { result = 06.5; /* 3.0/2.0 */}
		                  else if (EE > 4.61625) { result = 09.4; /* 3.0/1.0 */}
		                }
		              }
		              if (EE > 4.91325) { result = 09.1; /* 3.0/2.0 */}
		            }
		          }
		        }
		      }
		      if (EE > 4.985813)
		      {
		        if (SCH <= -0.389218)
		        {
		          if (EE <= 5.407875)
		          {
		            if (SCH <= -0.392127)
		            {
		              if (SE <= 10.217437) { result = 08.6; /* 2.0/1.0 */}
		              else if (SE > 10.217437) { result = 11.7; /* 2.0/1.0 */}
		            }
		            if (SCH > -0.392127) { result = 09.1; /* 12.0/6.0 */}
		          }
		          if (EE > 5.407875)
		          {
		            if (SCH <= -0.391888)
		            {
		              if (SCH <= -0.500959) { result = 09.6; /* 3.0/2.0 */}
		              else if (SCH > -0.500959)
		              {
		                if (EE <= 5.568813) { result = 09.0; /* 4.0/2.0 */}
		                else if (EE > 5.568813) { result = 09.3; /* 2.0/1.0 */}
		              }
		            }
		            if (SCH > -0.391888)
		            {
		              if (EE <= 5.584) { result = 08.6; /* 4.0/2.0 */}
		              else if (EE > 5.584)
		              {
		                if (SE <= 9.340057) { result = 07.8; /* 2.0/1.0 */}
		                else if (SE > 9.340057) { result = 09.0; /* 2.0/1.0 */}
		              }
		            }
		          }
		        }
		        if (SCH > -0.389218)
		        {
		          if (EE <= 5.295125)
		          {
		            if (SCH <= -0.3831) { result = 09.6; /* 4.0/2.0 */}
		            else if (SCH > -0.3831) { result = 09.4; /* 4.0/1.0 */}
		          }
		          if (EE > 5.295125)
		          {
		            if (SCH <= -0.387072)
		            {
		              if (SCH <= -0.388422)
		              {
		                if (SE <= 9.365372) { result = 08.7; /* 2.0/1.0 */}
		                else if (SE > 9.365372) { result = 09.4; /* 2.0 */}
		              }
		              if (SCH > -0.388422)
		              {
		                if (SCH <= -0.388146) { result = 09.1; /* 2.0/1.0 */}
		                else if (SCH > -0.388146) { result = 08.3; /* 3.0/2.0 */}
		              }
		            }
		            if (SCH > -0.387072)
		            {
		              if (SCH <= -0.38373)
		              {
		                if (SE <= 9.416251) { result = 09.2; /* 3.0/1.0 */}
		                else if (SE > 9.416251) { result = 08.0; /* 4.0/1.0 */}
		              }
		              if (SCH > -0.38373)
		              {
		                if (SE <= 9.46366) { result = 09.3; /* 12.0/5.0 */}
		                else if (SE > 9.46366) { result = 09.2; /* 3.0/1.0 */}
		              }
		            }
		          }
		        }
		      }
		    }
		    if (SCH > -0.380455)
		    {
		      if (EE <= 4.175688)
		      {
		        if (SCH <= -0.293894)
		        {
		          if (SCH <= -0.302533)
		          {
		            if (SE <= 11.204989) { result = 10.1; /* 2.0/1.0 */}
		            else if (SE > 11.204989) { result = 09.5; /* 2.0/1.0 */}
		          }
		          if (SCH > -0.302533)
		          {
		            if (SCH <= -0.29949)
		            {
		              if (SE <= 12.941888) { result = 08.5; /* 3.0/2.0 */}
		              else if (SE > 12.941888) { result = 09.5; /* 2.0 */}
		            }
		            if (SCH > -0.29949)
		            {
		              if (EE <= 4.05775)
		              {
		                if (SCH <= -0.299479) { result = 09.3; /* 2.0 */}
		                else if (SCH > -0.299479) { result = 09.5; /* 4.0/2.0 */}
		              }
		              if (EE > 4.05775)
		              {
		                if (SCH <= -0.299479) { result = 09.2; /* 5.0/2.0 */}
		                else if (SCH > -0.299479) { result = 09.3; /* 6.0/2.0 */}
		              }
		            }
		          }
		        }
		        if (SCH > -0.293894)
		        {
		          if (EE <= 3.9875) { result = 11.2; /* 3.0/1.0 */}
		          else if (EE > 3.9875)
		          {
		            if (SE <= 14.330946) { result = 09.1; /* 4.0/2.0 */}
		            else if (SE > 14.330946) { result = 10.5; /* 2.0/1.0 */}
		          }
		        }
		      }
		      if (EE > 4.175688)
		      {
		        if (SCH <= -0.233254)
		        {
		          if (EE <= 4.8235)
		          {
		            if (SCH <= -0.301441)
		            {
		              if (SCH <= -0.371893) { result = 10.1; /* 3.0/1.0 */}
		              else if (SCH > -0.371893)
		              {
		                if (SCH <= -0.341599)
		                {
		                  if (EE <= 4.78025) { result = 09.7; /* 2.0 */}
		                  else if (EE > 4.78025) { result = 09.9; /* 2.0/1.0 */}
		                }
		                if (SCH > -0.341599) { result = 10.0; /* 3.0/1.0 */}
		              }
		            }
		            if (SCH > -0.301441)
		            {
		              if (EE <= 4.635125)
		              {
		                if (SCH <= -0.298528)
		                {
		                  if (SCH <= -0.298823)
		                  {
		                    if (EE <= 4.514125)
		                    {
		                      if (SCH <= -0.299457) { result = 09.0; /* 3.0/1.0 */}
		                      else if (SCH > -0.299457)
		                      {
		                        if (SE <= 12.986371)
		                        {
		                          if (SE <= 12.986356) { result = 09.1; /* 2.0 */}
		                          else if (SE > 12.986356) { result = 09.2; /* 2.0 */}
		                        }
		                        if (SE > 12.986371) { result = 09.1; /* 17.0/5.0 */}
		                      }
		                    }
		                    if (EE > 4.514125)
		                    {
		                      if (SCH <= -0.299111) { result = 09.1; /* 2.0/1.0 */}
		                      else if (SCH > -0.299111) { result = 09.0; /* 3.0/1.0 */}
		                    }
		                  }
		                  if (SCH > -0.298823)
		                  {
		                    if (EE <= 4.4915) { result = 09.0; /* 2.0/1.0 */}
		                    else if (EE > 4.4915) { result = 09.2; /* 3.0/1.0 */}
		                  }
		                }
		                if (SCH > -0.298528)
		                {
		                  if (SE <= 12.999905) { result = 08.2; /* 2.0/1.0 */}
		                  else if (SE > 12.999905) { result = 08.8; /* 2.0/1.0 */}
		                }
		              }
		              if (EE > 4.635125)
		              {
		                if (EE <= 4.756176)
		                {
		                  if (SCH <= -0.298814)
		                  {
		                    if (SCH <= -0.299158) { result = 08.8; /* 2.0/1.0 */}
		                    else if (SCH > -0.299158) { result = 09.0; /* 5.0/2.0 */}
		                  }
		                  if (SCH > -0.298814) { result = 08.9; /* 3.0 */}
		                }
		                if (EE > 4.756176) { result = 08.2; /* 2.0/1.0 */}
		              }
		            }
		          }
		          if (EE > 4.8235)
		          {
		            if (SE <= 9.633713)
		            {
		              if (EE <= 6.102)
		              {
		                if (EE <= 5.568813)
		                {
		                  if (EE <= 5.205703)
		                  {
		                    if (SE <= 9.526853) { result = 09.8; /* 2.0/1.0 */}
		                    else if (SE > 9.526853) { result = 09.1; /* 3.0/1.0 */}
		                  }
		                  if (EE > 5.205703)
		                  {
		                    if (SE <= 9.561631) { result = 10.6; /* 2.0 */}
		                    else if (SE > 9.561631) { result = 08.2; /* 3.0/2.0 */}
		                  }
		                }
		                if (EE > 5.568813)
		                {
		                  if (EE <= 5.799953)
		                  {
		                    if (SE <= 9.480374) { result = 08.8; /* 2.0/1.0 */}
		                    else if (SE > 9.480374) { result = 09.2; /* 4.0/2.0 */}
		                  }
		                  if (EE > 5.799953)
		                  {
		                    if (SE <= 9.523381) { result = 09.4; /* 4.0/2.0 */}
		                    else if (SE > 9.523381) { result = 07.4; /* 4.0/2.0 */}
		                  }
		                }
		              }
		              if (EE > 6.102)
		              {
		                if (SCH <= -0.379437)
		                {
		                  if (SE <= 9.470569) { result = 09.1; /* 7.0/3.0 */}
		                  else if (SE > 9.470569)
		                  {
		                    if (SE <= 9.473074)
		                    {
		                      if (EE <= 6.259) { result = 08.6; /* 2.0 */}
		                      else if (EE > 6.259) { result = 08.3; /* 2.0/1.0 */}
		                    }
		                    if (SE > 9.473074) { result = 08.2; /* 3.0/2.0 */}
		                  }
		                }
		                if (SCH > -0.379437)
		                {
		                  if (EE <= 6.73075)
		                  {
		                    if (SCH <= -0.374181)
		                    {
		                      if (SE <= 9.511648) { result = 09.2; /* 5.0/1.0 */}
		                      else if (SE > 9.511648)
		                      {
		                        if (EE <= 6.40725) { result = 08.4; /* 2.0/1.0 */}
		                        else if (EE > 6.40725) { result = 09.3; /* 4.0/2.0 */}
		                      }
		                    }
		                    if (SCH > -0.374181) { result = 07.2; /* 2.0/1.0 */}
		                  }
		                  if (EE > 6.73075) { result = 09.1; /* 13.0/7.0 */}
		                }
		              }
		            }
		            if (SE > 9.633713)
		            {
		              if (EE <= 6.323406)
		              {
		                if (SE <= 12.995039)
		                {
		                  if (SCH <= -0.321468) { result = 09.4; /* 3.0/2.0 */}
		                  else if (SCH > -0.321468)
		                  {
		                    if (SE <= 9.636834) { result = 08.9; /* 3.0/1.0 */}
		                    else if (SE > 9.636834)
		                    {
		                      if (SE <= 12.990987) { result = 09.8; /* 4.0/2.0 */}
		                      else if (SE > 12.990987) { result = 08.8; /* 5.0/2.0 */}
		                    }
		                  }
		                }
		                if (SE > 12.995039)
		                {
		                  if (EE <= 5.087813)
		                  {
		                    if (EE <= 5.021172)
		                    {
		                      if (SE <= 13.000061) { result = 07.9; /* 3.0/2.0 */}
		                      else if (SE > 13.000061) { result = 08.5; /* 3.0/2.0 */}
		                    }
		                    if (EE > 5.021172)
		                    {
		                      if (EE <= 5.049133) { result = 08.8; /* 5.0/1.0 */}
		                      else if (EE > 5.049133) { result = 08.2; /* 2.0/1.0 */}
		                    }
		                  }
		                  if (EE > 5.087813)
		                  {
		                    if (SE <= 13.000061) { result = 08.9; /* 4.0/1.0 */}
		                    else if (SE > 13.000061) { result = 08.6; /* 4.0/2.0 */}
		                  }
		                }
		              }
		              if (EE > 6.323406)
		              {
		                if (SCH <= -0.29393)
		                {
		                  if (EE <= 7.090875)
		                  {
		                    if (EE <= 6.382352) { result = 08.6; /* 2.0/1.0 */}
		                    else if (EE > 6.382352)
		                    {
		                      if (EE <= 7.0065) { result = 08.8; /* 6.0/3.0 */}
		                      else if (EE > 7.0065) { result = 08.2; /* 2.0/1.0 */}
		                    }
		                  }
		                  if (EE > 7.090875) { result = 08.6; /* 3.0 */}
		                }
		                if (SCH > -0.29393)
		                {
		                  if (SE <= 9.685853) { result = 08.3; /* 2.0/1.0 */}
		                  else if (SE > 9.685853) { result = 08.2; /* 2.0/1.0 */}
		                }
		              }
		            }
		          }
		        }
		        if (SCH > -0.233254)
		        {
		          if (EE <= 7.03075)
		          {
		            if (EE <= 5.77875)
		            {
		              if (SCH <= -0.121832)
		              {
		                if (SCH <= -0.173436)
		                {
		                  if (SE <= 10.475336) { result = 10.8; /* 2.0/1.0 */}
		                  else if (SE > 10.475336)
		                  {
		                    if (EE <= 5.17925) { result = 10.4; /* 2.0/1.0 */}
		                    else if (EE > 5.17925) { result = 10.1; /* 3.0/1.0 */}
		                  }
		                }
		                if (SCH > -0.173436)
		                {
		                  if (EE <= 5.649922)
		                  {
		                    if (EE <= 5.0735) { result = 10.0; /* 2.0/1.0 */}
		                    else if (EE > 5.0735)
		                    {
		                      if (SCH <= -0.170365) { result = 10.7; /* 2.0 */}
		                      else if (SCH > -0.170365) { result = 10.5; /* 2.0 */}
		                    }
		                  }
		                  if (EE > 5.649922) { result = 09.1; /* 2.0/1.0 */}
		                }
		              }
		              if (SCH > -0.121832)
		              {
		                if (SE <= 9.402813) { result = 09.2; /* 3.0/1.0 */}
		                else if (SE > 9.402813) { result = 07.4; /* 3.0/2.0 */}
		              }
		            }
		            if (EE > 5.77875)
		            {
		              if (EE <= 6.153625)
		              {
		                if (SCH <= -0.168201)
		                {
		                  if (EE <= 5.83925) { result = 08.7; /* 2.0/1.0 */}
		                  else if (EE > 5.83925)
		                  {
		                    if (EE <= 5.923875) { result = 08.6; /* 2.0 */}
		                    else if (EE > 5.923875) { result = 08.7; /* 4.0/2.0 */}
		                  }
		                }
		                if (SCH > -0.168201)
		                {
		                  if (SE <= 9.761799) { result = 08.2; /* 2.0/1.0 */}
		                  else if (SE > 9.761799) { result = 10.1; /* 3.0/2.0 */}
		                }
		              }
		              if (EE > 6.153625)
		              {
		                if (SE <= 10.087325)
		                {
		                  if (EE <= 6.331813) { result = 10.1; /* 3.0/1.0 */}
		                  else if (EE > 6.331813) { result = 09.5; /* 3.0/2.0 */}
		                }
		                if (SE > 10.087325)
		                {
		                  if (SCH <= -0.143246) { result = 09.0; /* 5.0/2.0 */}
		                  else if (SCH > -0.143246)
		                  {
		                    if (EE <= 6.8065)
		                    {
		                      if (EE <= 6.3445) { result = 09.9; /* 3.0/1.0 */}
		                      else if (EE > 6.3445) { result = 10.0; /* 5.0/2.0 */}
		                    }
		                    if (EE > 6.8065) { result = 09.9; /* 3.0/1.0 */}
		                  }
		                }
		              }
		            }
		          }
		          if (EE > 7.03075)
		          {
		            if (EE <= 9.058375)
		            {
		              if (EE <= 8.619875)
		              {
		                if (SCH <= -0.138978) { result = 09.3; /* 4.0/2.0 */}
		                else if (SCH > -0.138978)
		                {
		                  if (SCH <= -0.135867)
		                  {
		                    if (SE <= 10.14) { result = 09.2; /* 2.0 */}
		                    else if (SE > 10.14) { result = 09.7; /* 3.0/1.0 */}
		                  }
		                  if (SCH > -0.135867)
		                  {
		                    if (SCH <= -0.084824)
		                    {
		                      if (SCH <= -0.085386)
		                      {
		                        if (SE <= 10.155881) { result = 09.3; /* 2.0 */}
		                        else if (SE > 10.155881) { result = 09.2; /* 3.0/1.0 */}
		                      }
		                      if (SCH > -0.085386) { result = 09.2; /* 3.0 */}
		                    }
		                    if (SCH > -0.084824) { result = 08.4; /* 2.0/1.0 */}
		                  }
		                }
		              }
		              if (EE > 8.619875) { result = 09.1; /* 6.0/1.0 */}
		            }
		            if (EE > 9.058375)
		            {
		              if (SCH <= -0.080179) { result = 09.0; /* 5.0/1.0 */}
		              else if (SCH > -0.080179)
		              {
		                if (SE <= 9.271976) { result = 08.9; /* 6.0/2.0 */}
		                else if (SE > 9.271976) { result = 08.7; /* 3.0 */}
		              }
		            }
		          }
		        }
		      }
		    }
		  }
		}

		return result;
	}
	/**
	 * This method calculates the ionization potential of an atom and set the ionization
	 * energy into each reaction as property
	 * 
	 * @return The IReactionSet value
	 */
	public IReactionSet getReactionSet(IAtom atom, IAtomContainer container) throws CDKException{
		setEnergy = true;
		calculate(atom,container);
		return reactionSet;
	}
	/**
	 * Calculate the necessary descriptors for Heteratom atoms
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 * @throws CDKException 
	 */
	private Double[][] calculateHeteroAtomDescriptor(IAtom atom, IAtomContainer atomContainer) throws CDKException {
		Double[][] results = new Double[1][3];
		SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
		EffectiveAtomPolarizabilityDescriptor descriptor3 = new EffectiveAtomPolarizabilityDescriptor();

		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(atom,atomContainer).getValue()).doubleValue());
    	
		return results;
	}
	/**
	 * Calculate the necessary descriptors for Carbonyl group
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCarbonylDescriptor(IAtom atom, IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		IAtom positionX = atom;
		IAtom positionC = (IAtom) atomContainer.getConnectedAtomsList(atom).get(0);
		IBond bond = atomContainer.getBond(positionX, positionC);
		try {
        	/*0*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1*/
    		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC,atomContainer).getValue()).doubleValue());
    		/*2*/
    		BondPartialSigmaChargeDescriptor descriptor3 = new BondPartialSigmaChargeDescriptor();
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(bond, atomContainer).getValue()).doubleValue());
    		/*3*/
    		SigmaElectronegativityDescriptor descriptor4 = new SigmaElectronegativityDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*4*/
    		PartialSigmaChargeDescriptor descriptor5 = new PartialSigmaChargeDescriptor();
    		results[0][4]= new Double(((DoubleResult)descriptor5.calculate(positionX, atomContainer).getValue()).doubleValue());
    		/*5*/
    		ResonancePositiveChargeDescriptor descriptor6 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor6.calculate(bond, atomContainer).getValue());
			double datT = (dar.get(0)+dar.get(1))/2;
			results[0][5] = new Double(datT);
 
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


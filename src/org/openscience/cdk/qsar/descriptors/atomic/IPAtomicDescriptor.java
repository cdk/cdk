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

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
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
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LonePairElectronChecker;

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
		boolean isTarget = false;
		Double[][] resultsH = null;

//		if(atom.getSymbol().equals("F")||
//					atom.getSymbol().equals("Cl")||
//					atom.getSymbol().equals("Br")||
//					atom.getSymbol().equals("I")||
//					atom.getSymbol().equals("N")||
//					atom.getSymbol().equals("S")||
//					atom.getSymbol().equals("O")||
//					atom.getSymbol().equals("P")){
			
			/*control if it is into an aromatic or conjugated system*/
			HueckelAromaticityDetector.detectAromaticity(container,true);
			AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(container);
			Iterator acI = conjugatedPi.atomContainers();
     		while(acI.hasNext()){
    			IAtomContainer ac = (IAtomContainer) acI.next();
    			if(ac.contains(atom)){
    				return null;
    			}
     		}
     		
			if(container.getMaximumBondOrder(atom) > 1 && container.getLonePairCount(atom) > 0){
				resultsH = calculateCarbonylDescriptor(atom, container);
				resultD = getTreeDoubleHetero(resultsH);
				resultD += 0.05;
    			isTarget = true;
			}else{
				resultsH = calculateHeteroAtomDescriptor(atom, container);
				resultD = getTreeHeteroAtom(resultsH);
				resultD += 0.05;
    			isTarget = true;
			}
//		}
		if(isTarget){
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
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(resultD));
	}
	/**
	 * tree desicion for the carbonyl atoms
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getTreeDoubleHetero(Double[][] resultsH) {
		double result = 0.0;
		double SE_c = (resultsH[0][0]).doubleValue();
		double PCH_c = (resultsH[0][1]).doubleValue();
		double SB  = (resultsH[0][2]).doubleValue();
		double SE_x = (resultsH[0][3]).doubleValue();
		double PCH_x = (resultsH[0][4]).doubleValue();
		double RES_c = (resultsH[0][5]).doubleValue();
		if (PCH_c <= 0.04019)
		{
		  if (PCH_c <= 0.027211)
		  {
		    if (PCH_c <= 0)
		    {
		      if (SE_c <= 10.156837) { result = 07.4; /* 3.0/2.0 */}
		      else if (SE_c > 10.156837) { result = 06.6; /* 3.0/2.0 */}
		    }
		    if (PCH_c > 0)
		    {
		      if (SB <= 0.028396)
		      {
		        if (SE_c <= 8.392565) { result = 08.9; /* 2.0/1.0 */}
		        else if (SE_c > 8.392565) { result = 08.5; /* 2.0/1.0 */}
		      }
		      if (SB > 0.028396)
		      {
		        if (SE_c <= 8.855708)
		        {
		          if (PCH_c <= 0.027114) { result = 08.4; /* 2.0 */}
		          else if (PCH_c > 0.027114) { result = 08.3; /* 2.0/1.0 */}
		        }
		        if (SE_c > 8.855708) { result = 08.2; /* 2.0/1.0 */}
		      }
		    }
		  }
		  if (PCH_c > 0.027211)
		  {
		    if (SB <= 0.434418)
		    {
		      if (SE_c <= 10.049963)
		      {
		        if (RES_c <= 1.086842)
		        {
		          if (PCH_c <= 0.039387)
		          {
		            if (SE_x <= 9.636535)
		            {
		              if (PCH_c <= 0.029894) { result = 09.2; /* 2.0 */}
		              else if (PCH_c > 0.029894) { result = 10.0; /* 3.0/1.0 */}
		            }
		            if (SE_x > 9.636535)
		            {
		              if (RES_c <= 0.699389) { result = 09.3; /* 2.0/1.0 */}
		              else if (RES_c > 0.699389) { result = 09.7; /* 3.0/1.0 */}
		            }
		          }
		          if (PCH_c > 0.039387) { result = 09.3; /* 15.0/9.0 */}
		        }
		        if (RES_c > 1.086842)
		        {
		          if (PCH_c <= 0.029565)
		          {
		            if (SE_c <= 8.779493) { result = 09.4; /* 3.0/1.0 */}
		            else if (SE_c > 8.779493) { result = 08.5; /* 2.0/1.0 */}
		          }
		          if (PCH_c > 0.029565) { result = 08.9; /* 3.0/1.0 */}
		        }
		      }
		      if (SE_c > 10.049963)
		      {
		        if (PCH_c <= 0.03973)
		        {
		          if (SE_x <= 12.990417) { result = 09.1; /* 25.0/13.0 */}
		          else if (SE_x > 12.990417) { result = 09.5; /* 4.0/2.0 */}
		        }
		        if (PCH_c > 0.03973)
		        {
		          if (SE_x <= 12.990724) { result = 08.8; /* 3.0/2.0 */}
		          else if (SE_x > 12.990724)
		          {
		            if (SE_x <= 12.991133) { result = 09.0; /* 6.0/3.0 */}
		            else if (SE_x > 12.991133) { result = 09.1; /* 3.0/1.0 */}
		          }
		        }
		      }
		    }
		    if (SB > 0.434418)
		    {
		      if (SE_x <= 12.995835)
		      {
		        if (PCH_c <= 0.039882)
		        {
		          if (RES_c <= 0.913891)
		          {
		            if (SE_x <= 12.995039)
		            {
		              if (SE_c <= 10.112309) { result = 09.0; /* 2.0/1.0 */}
		              else if (SE_c > 10.112309) { result = 08.8; /* 3.0/1.0 */}
		            }
		            if (SE_x > 12.995039)
		            {
		              if (SE_c <= 10.117785) { result = 08.7; /* 2.0/1.0 */}
		              else if (SE_c > 10.117785) { result = 09.0; /* 2.0/1.0 */}
		            }
		          }
		          if (RES_c > 0.913891) { result = 08.9; /* 2.0 */}
		        }
		        if (PCH_c > 0.039882)
		        {
		          if (RES_c <= 0.456038) { result = 08.9; /* 3.0 */}
		          else if (RES_c > 0.456038) { result = 08.8; /* 3.0/1.0 */}
		        }
		      }
		      if (SE_x > 12.995835)
		      {
		        if (SB <= 0.438563) { result = 08.4; /* 3.0/2.0 */}
		        else if (SB > 0.438563)
		        {
		          if (PCH_c <= 0.038233)
		          {
		            if (SE_c <= 10.920431) { result = 08.9; /* 2.0/1.0 */}
		            else if (SE_c > 10.920431) { result = 08.7; /* 2.0/1.0 */}
		          }
		          if (PCH_c > 0.038233)
		          {
		            if (RES_c <= 0.699389)
		            {
		              if (RES_c <= 0) { result = 08.6; /* 4.0/2.0 */}
		              else if (RES_c > 0) { result = 09.1; /* 2.0 */}
		            }
		            if (RES_c > 0.699389) { result = 08.9; /* 3.0/1.0 */}
		          }
		        }
		      }
		    }
		  }
		}
		if (PCH_c > 0.04019)
		{
		  if (SE_c <= 0)
		  {
		    if (SE_x <= -0.758741)
		    {
		      if (SB <= 15.078905)
		      {
		        if (PCH_c <= 3.99999650662589E120) { result = 06.6; /* 3.0/1.0 */}
		        else if (PCH_c > 3.99999650662589E120) { result = 07.7; /* 2.0/1.0 */}
		      }
		      if (SB > 15.078905)
		      {
		        if (PCH_c <= 5.022942234028359E120) { result = 08.6; /* 2.0/1.0 */}
		        else if (PCH_c > 5.022942234028359E120) { result = 09.0; /* 2.0/1.0 */}
		      }
		    }
		    if (SE_x > -0.758741)
		    {
		      if (RES_c <= 0.699389)
		      {
		        if (PCH_c <= 1.979715) { result = 11.1; /* 4.0/1.0 */}
		        else if (PCH_c > 1.979715) { result = 08.7; /* 2.0/1.0 */}
		      }
		      if (RES_c > 0.699389) { result = 08.5; /* 2.0 */}
		    }
		  }
		  if (SE_c > 0)
		  {
		    if (PCH_c <= 0.046142)
		    {
		      if (RES_c <= 0.93486)
		      {
		        if (PCH_c <= 0.045502)
		        {
		          if (PCH_c <= 0.045325)
		          {
		            if (SE_c <= 9.926993) { result = 09.8; /* 2.0 */}
		            else if (SE_c > 9.926993) { result = 09.7; /* 4.0/2.0 */}
		          }
		          if (PCH_c > 0.045325) { result = 09.6; /* 4.0/2.0 */}
		        }
		        if (PCH_c > 0.045502)
		        {
		          if (SE_c <= 9.956658) { result = 09.5; /* 3.0/1.0 */}
		          else if (SE_c > 9.956658) { result = 09.6; /* 5.0/3.0 */}
		        }
		      }
		      if (RES_c > 0.93486) { result = 09.9; /* 3.0/2.0 */}
		    }
		    if (PCH_c > 0.046142)
		    {
		      if (SB <= 0.049347)
		      {
		        if (SE_c <= 7.941729) { result = 09.4; /* 3.0/2.0 */}
		        else if (SE_c > 7.941729) { result = 08.4; /* 2.0/1.0 */}
		      }
		      if (SB > 0.049347) { result = 08.3; /* 6.0/4.0 */}
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
		double PE  = (resultsH[0][3]).doubleValue();
		
		if (SE <= 9.104677)
		{
		  if (EE <= 6.64375)
		  {
		    if (PE <= 1.575052)
		    {
		      if (EE <= 4.58175)
		      {
		        if (EE <= 3.566)
		        {
		          if (PE <= 0.654288)
		          {
		            if (EE <= 1.438) { result = 09.8; /* 3.0/1.0 */}
		            else if (EE > 1.438) { result = 09.1; /* 2.0/1.0 */}
		          }
		          if (PE > 0.654288) { result = 06.2; /* 2.0/1.0 */}
		        }
		        if (EE > 3.566)
		        {
		          if (SE <= 8.122649)
		          {
		            if (SCH <= -0.330239) { result = 05.7; /* 3.0/2.0 */}
		            else if (SCH > -0.330239) { result = 08.5; /* 3.0/1.0 */}
		          }
		          if (SE > 8.122649) { result = 09.4; /* 3.0/1.0 */}
		        }
		      }
		      if (EE > 4.58175)
		      {
		        if (SCH <= -0.32534)
		        {
		          if (SCH <= -0.327241)
		          {
		            if (EE <= 5.1445)
		            {
		              if (EE <= 4.687406) { result = 08.7; /* 2.0/1.0 */}
		              else if (EE > 4.687406)
		              {
		                if (EE <= 5.00775) { result = 08.6; /* 6.0/2.0 */}
		                else if (EE > 5.00775) { result = 08.5; /* 2.0 */}
		              }
		            }
		            if (EE > 5.1445)
		            {
		              if (SE <= 6.834307) { result = 08.4; /* 2.0/1.0 */}
		              else if (SE > 6.834307) { result = 09.3; /* 3.0/1.0 */}
		            }
		          }
		          if (SCH > -0.327241)
		          {
		            if (SE <= 8.1475) { result = 08.3; /* 2.0/1.0 */}
		            else if (SE > 8.1475)
		            {
		              if (SE <= 8.153334) { result = 08.8; /* 3.0/1.0 */}
		              else if (SE > 8.153334) { result = 08.5; /* 2.0 */}
		            }
		          }
		        }
		        if (SCH > -0.32534)
		        {
		          if (PE <= 1.51965)
		          {
		            if (EE <= 4.8445) { result = 05.9; /* 2.0/1.0 */}
		            else if (EE > 4.8445) { result = 08.1; /* 4.0/2.0 */}
		          }
		          if (PE > 1.51965)
		          {
		            if (SCH <= -0.314518)
		            {
		              if (EE <= 5.96675) { result = 08.6; /* 2.0/1.0 */}
		              else if (EE > 5.96675) { result = 08.0; /* 4.0/1.0 */}
		            }
		            if (SCH > -0.314518) { result = 08.6; /* 6.0/4.0 */}
		          }
		        }
		      }
		    }
		    if (PE > 1.575052)
		    {
		      if (SCH <= -0.589854)
		      {
		        if (SE <= -1.398023)
		        {
		          if (EE <= 4.767125)
		          {
		            if (SE <= -9.044681) { result = 10.3; /* 2.0/1.0 */}
		            else if (SE > -9.044681)
		            {
		              if (SE <= -8.924774) { result = 07.4; /* 2.0/1.0 */}
		              else if (SE > -8.924774) { result = 09.5; /* 2.0/1.0 */}
		            }
		          }
		          if (EE > 4.767125)
		          {
		            if (SCH <= -4.914758)
		            {
		              if (SE <= -7.648857) { result = 08.9; /* 2.0/1.0 */}
		              else if (SE > -7.648857) { result = 09.4; /* 3.0/1.0 */}
		            }
		            if (SCH > -4.914758)
		            {
		              if (SE <= -6.54053) { result = 08.4; /* 2.0/1.0 */}
		              else if (SE > -6.54053) { result = 07.2; /* 3.0/2.0 */}
		            }
		          }
		        }
		        if (SE > -1.398023)
		        {
		          if (PE <= 95.654219) { result = 09.6; /* 11.0/8.0 */}
		          else if (PE > 95.654219)
		          {
		            if (SCH <= -5.702423) { result = 07.7; /* 2.0/1.0 */}
		            else if (SCH > -5.702423) { result = 09.5; /* 3.0/2.0 */}
		          }
		        }
		      }
		      if (SCH > -0.589854)
		      {
		        if (EE <= 5.439)
		        {
		          if (SCH <= -0.332749)
		          {
		            if (SE <= 8.80606) { result = 09.2; /* 2.0/1.0 */}
		            else if (SE > 8.80606) { result = 12.6; /* 2.0 */}
		          }
		          if (SCH > -0.332749)
		          {
		            if (SE <= 8.495417) { result = 09.7; /* 2.0/1.0 */}
		            else if (SE > 8.495417) { result = 05.1; /* 3.0/2.0 */}
		          }
		        }
		        if (EE > 5.439)
		        {
		          if (EE <= 6.259)
		          {
		            if (SCH <= -0.168201) { result = 09.2; /* 5.0/1.0 */}
		            else if (SCH > -0.168201) { result = 05.4; /* 2.0/1.0 */}
		          }
		          if (EE > 6.259)
		          {
		            if (SE <= 8.543609) { result = 08.9; /* 2.0/1.0 */}
		            else if (SE > 8.543609) { result = 09.1; /* 4.0/1.0 */}
		          }
		        }
		      }
		    }
		  }
		  if (EE > 6.64375)
		  {
		    if (PE <= 2.644757)
		    {
		      if (EE <= 7.7465)
		      {
		        if (SE <= 8.311315)
		        {
		          if (PE <= 1.531406)
		          {
		            if (SE <= 8.002657)
		            {
		              if (PE <= 0.004639) { result = 08.3; /* 2.0 */}
		              else if (PE > 0.004639) { result = 07.6; /* 2.0/1.0 */}
		            }
		            if (SE > 8.002657)
		            {
		              if (EE <= 6.90325)
		              {
		                if (SCH <= -0.315888) { result = 07.8; /* 2.0/1.0 */}
		                else if (SCH > -0.315888) { result = 08.0; /* 3.0/1.0 */}
		              }
		              if (EE > 6.90325) { result = 07.8; /* 3.0/1.0 */}
		            }
		          }
		          if (PE > 1.531406) { result = 07.9; /* 5.0/3.0 */}
		        }
		        if (SE > 8.311315)
		        {
		          if (SCH <= -0.305242)
		          {
		            if (SCH <= -0.308425)
		            {
		              if (EE <= 6.84575) { result = 07.7; /* 3.0/2.0 */}
		              else if (EE > 6.84575) { result = 08.3; /* 4.0/1.0 */}
		            }
		            if (SCH > -0.308425)
		            {
		              if (SCH <= -0.306392) { result = 07.5; /* 2.0/1.0 */}
		              else if (SCH > -0.306392)
		              {
		                if (EE <= 7.5165) { result = 07.8; /* 3.0/1.0 */}
		                else if (EE > 7.5165) { result = 07.7; /* 2.0/1.0 */}
		              }
		            }
		          }
		          if (SCH > -0.305242)
		          {
		            if (SE <= 8.390548) { result = 08.7; /* 2.0 */}
		            else if (SE > 8.390548)
		            {
		              if (SE <= 8.435289) { result = 08.2; /* 3.0/1.0 */}
		              else if (SE > 8.435289) { result = 08.6; /* 2.0 */}
		            }
		          }
		        }
		      }
		      if (EE > 7.7465)
		      {
		        if (EE <= 8.716625)
		        {
		          if (EE <= 8.234438)
		          {
		            if (SE <= 8.388638)
		            {
		              if (SE <= 8.357858)
		              {
		                if (SCH <= -0.308632) { result = 07.6; /* 2.0/1.0 */}
		                else if (SCH > -0.308632)
		                {
		                  if (EE <= 7.897375) { result = 08.2; /* 2.0/1.0 */}
		                  else if (EE > 7.897375) { result = 08.0; /* 4.0/1.0 */}
		                }
		              }
		              if (SE > 8.357858)
		              {
		                if (SE <= 8.360428) { result = 07.7; /* 3.0 */}
		                else if (SE > 8.360428)
		                {
		                  if (EE <= 7.994375) { result = 07.5; /* 3.0/1.0 */}
		                  else if (EE > 7.994375)
		                  {
		                    if (EE <= 8.079) { result = 07.6; /* 3.0 */}
		                    else if (EE > 8.079) { result = 08.0; /* 3.0/1.0 */}
		                  }
		                }
		              }
		            }
		            if (SE > 8.388638) { result = 08.1; /* 3.0/2.0 */}
		          }
		          if (EE > 8.234438)
		          {
		            if (SCH <= -0.302776) { result = 07.9; /* 9.0/4.0 */}
		            else if (SCH > -0.302776)
		            {
		              if (SCH <= -0.300515)
		              {
		                if (SCH <= -0.302509) { result = 08.1; /* 3.0/1.0 */}
		                else if (SCH > -0.302509) { result = 07.7; /* 3.0/1.0 */}
		              }
		              if (SCH > -0.300515)
		              {
		                if (EE <= 8.4175) { result = 07.9; /* 2.0/1.0 */}
		                else if (EE > 8.4175) { result = 08.2; /* 2.0 */}
		              }
		            }
		          }
		        }
		        if (EE > 8.716625)
		        {
		          if (SCH <= -0.298843)
		          {
		            if (SCH <= -0.299978) { result = 07.8; /* 7.0/4.0 */}
		            else if (SCH > -0.299978) { result = 07.9; /* 3.0 */}
		          }
		          if (SCH > -0.298843)
		          {
		            if (SE <= 8.446749) { result = 07.7; /* 5.0/1.0 */}
		            else if (SE > 8.446749) { result = 08.3; /* 6.0/4.0 */}
		          }
		        }
		      }
		    }
		    if (PE > 2.644757)
		    {
		      if (EE <= 8.473938)
		      {
		        if (EE <= 7.4345)
		        {
		          if (PE <= 4.95)
		          {
		            if (SE <= 7.90981) { result = 08.2; /* 2.0/1.0 */}
		            else if (SE > 7.90981) { result = 09.0; /* 2.0/1.0 */}
		          }
		          if (PE > 4.95)
		          {
		            if (EE <= 7.432125)
		            {
		              if (EE <= 7.119)
		              {
		                if (SE <= 8.681096) { result = 08.5; /* 3.0/1.0 */}
		                else if (SE > 8.681096) { result = 08.6; /* 2.0 */}
		              }
		              if (EE > 7.119)
		              {
		                if (SE <= -0.876239) { result = 08.1; /* 3.0/2.0 */}
		                else if (SE > -0.876239) { result = 08.8; /* 2.0 */}
		              }
		            }
		            if (EE > 7.432125) { result = 09.4; /* 2.0 */}
		          }
		        }
		        if (EE > 7.4345)
		        {
		          if (SCH <= -0.160825)
		          {
		            if (PE <= 4.969413)
		            {
		              if (SE <= 7.984435) { result = 08.1; /* 2.0/1.0 */}
		              else if (SE > 7.984435) { result = 08.7; /* 2.0 */}
		            }
		            if (PE > 4.969413) { result = 08.4; /* 14.0/6.0 */}
		          }
		          if (SCH > -0.160825)
		          {
		            if (SE <= 8.943162)
		            {
		              if (SE <= 8.711918) { result = 08.7; /* 2.0/1.0 */}
		              else if (SE > 8.711918)
		              {
		                if (EE <= 8.25675) { result = 08.5; /* 4.0/1.0 */}
		                else if (EE > 8.25675) { result = 08.3; /* 3.0/1.0 */}
		              }
		            }
		            if (SE > 8.943162) { result = 08.8; /* 2.0 */}
		          }
		        }
		      }
		      if (EE > 8.473938)
		      {
		        if (SE <= 8.704665)
		        {
		          if (SE <= -0.876239) { result = 08.3; /* 2.0/1.0 */}
		          else if (SE > -0.876239) { result = 08.4; /* 2.0 */}
		        }
		        if (SE > 8.704665)
		        {
		          if (EE <= 9.707625)
		          {
		            if (EE <= 9.24575)
		            {
		              if (EE <= 8.732) { result = 08.2; /* 5.0/1.0 */}
		              else if (EE > 8.732) { result = 08.3; /* 3.0/1.0 */}
		            }
		            if (EE > 9.24575)
		            {
		              if (SE <= 8.735737) { result = 08.1; /* 2.0 */}
		              else if (SE > 8.735737) { result = 08.2; /* 3.0/1.0 */}
		            }
		          }
		          if (EE > 9.707625)
		          {
		            if (SE <= 8.776484) { result = 07.8; /* 4.0/1.0 */}
		            else if (SE > 8.776484) { result = 08.2; /* 2.0/1.0 */}
		          }
		        }
		      }
		    }
		  }
		}
		if (SE > 9.104677)
		{
		  if (SE <= 10.164422)
		  {
		    if (EE <= 4.896125)
		    {
		      if (PE <= 3.141977)
		      {
		        if (PE <= 3.13937) { result = 06.7; /* 4.0/2.0 */}
		        else if (PE > 3.13937)
		        {
		          if (EE <= 3.795813) { result = 09.9; /* 2.0/1.0 */}
		          else if (EE > 3.795813) { result = 10.4; /* 3.0/1.0 */}
		        }
		      }
		      if (PE > 3.141977)
		      {
		        if (EE <= 4.508938)
		        {
		          if (SCH <= -0.39244)
		          {
		            if (SCH <= -0.392753)
		            {
		              if (EE <= 3.9875)
		              {
		                if (SE <= 9.288328) { result = 09.4; /* 2.0/1.0 */}
		                else if (SE > 9.288328) { result = 09.5; /* 3.0/2.0 */}
		              }
		              if (EE > 3.9875) { result = 09.8; /* 5.0/2.0 */}
		            }
		            if (SCH > -0.392753)
		            {
		              if (SE <= 9.323366) { result = 09.5; /* 2.0 */}
		              else if (SE > 9.323366) { result = 09.3; /* 2.0 */}
		            }
		          }
		          if (SCH > -0.39244)
		          {
		            if (EE <= 4.138375)
		            {
		              if (SCH <= -0.389247)
		              {
		                if (SE <= 9.332876) { result = 09.1; /* 3.0/1.0 */}
		                else if (SE > 9.332876) { result = 08.7; /* 2.0/1.0 */}
		              }
		              if (SCH > -0.389247)
		              {
		                if (SCH <= -0.385186) { result = 06.9; /* 2.0/1.0 */}
		                else if (SCH > -0.385186) { result = 10.4; /* 4.0/2.0 */}
		              }
		            }
		            if (EE > 4.138375)
		            {
		              if (SCH <= -0.384627) { result = 09.5; /* 3.0/1.0 */}
		              else if (SCH > -0.384627) { result = 09.6; /* 2.0/1.0 */}
		            }
		          }
		        }
		        if (EE > 4.508938)
		        {
		          if (SE <= 9.323138) { result = 09.7; /* 4.0/1.0 */}
		          else if (SE > 9.323138)
		          {
		            if (SCH <= -0.387072) { result = 09.6; /* 11.0/6.0 */}
		            else if (SCH > -0.387072)
		            {
		              if (EE <= 4.77525)
		              {
		                if (SE <= 9.460441) { result = 06.5; /* 2.0/1.0 */}
		                else if (SE > 9.460441)
		                {
		                  if (SE <= 9.567383) { result = 10.1; /* 2.0 */}
		                  else if (SE > 9.567383) { result = 09.7; /* 2.0 */}
		                }
		              }
		              if (EE > 4.77525) { result = 09.4; /* 3.0/2.0 */}
		            }
		          }
		        }
		      }
		    }
		    if (EE > 4.896125)
		    {
		      if (SE <= 9.561631)
		      {
		        if (PE <= 4.268441)
		        {
		          if (EE <= 6.120359)
		          {
		            if (EE <= 5.407875)
		            {
		              if (SCH <= -0.388146) { result = 09.1; /* 14.0/7.0 */}
		              else if (SCH > -0.388146)
		              {
		                if (SCH <= -0.380305)
		                {
		                  if (SCH <= -0.3831)
		                  {
		                    if (EE <= 5.087813) { result = 09.5; /* 2.0/1.0 */}
		                    else if (EE > 5.087813) { result = 09.6; /* 3.0/1.0 */}
		                  }
		                  if (SCH > -0.3831) { result = 09.4; /* 4.0/2.0 */}
		                }
		                if (SCH > -0.380305) { result = 09.1; /* 3.0/2.0 */}
		              }
		            }
		            if (EE > 5.407875)
		            {
		              if (SCH <= -0.389218) { result = 09.0; /* 8.0/5.0 */}
		              else if (SCH > -0.389218)
		              {
		                if (SCH <= -0.377395)
		                {
		                  if (EE <= 5.79875)
		                  {
		                    if (EE <= 5.709125)
		                    {
		                      if (SCH <= -0.387072) { result = 09.1; /* 2.0/1.0 */}
		                      else if (SCH > -0.387072) { result = 09.2; /* 6.0/3.0 */}
		                    }
		                    if (EE > 5.709125)
		                    {
		                      if (SE <= 9.46366) { result = 09.4; /* 2.0 */}
		                      else if (SE > 9.46366) { result = 09.1; /* 2.0 */}
		                    }
		                  }
		                  if (EE > 5.79875)
		                  {
		                    if (EE <= 5.87375) { result = 09.3; /* 5.0/1.0 */}
		                    else if (EE > 5.87375)
		                    {
		                      if (EE <= 6.081) { result = 09.2; /* 3.0/1.0 */}
		                      else if (EE > 6.081) { result = 09.3; /* 2.0 */}
		                    }
		                  }
		                }
		                if (SCH > -0.377395)
		                {
		                  if (SE <= 9.514797) { result = 09.4; /* 2.0/1.0 */}
		                  else if (SE > 9.514797) { result = 08.8; /* 2.0/1.0 */}
		                }
		              }
		            }
		          }
		          if (EE > 6.120359)
		          {
		            if (EE <= 6.382352)
		            {
		              if (EE <= 6.287)
		              {
		                if (EE <= 6.1415) { result = 09.1; /* 2.0 */}
		                else if (EE > 6.1415)
		                {
		                  if (EE <= 6.1935)
		                  {
		                    if (EE <= 6.153625) { result = 09.2; /* 2.0 */}
		                    else if (EE > 6.153625) { result = 08.6; /* 2.0 */}
		                  }
		                  if (EE > 6.1935) { result = 09.2; /* 3.0/1.0 */}
		                }
		              }
		              if (EE > 6.287)
		              {
		                if (EE <= 6.3445)
		                {
		                  if (SE <= 9.416251) { result = 09.4; /* 3.0/1.0 */}
		                  else if (SE > 9.416251) { result = 09.0; /* 3.0/1.0 */}
		                }
		                if (EE > 6.3445) { result = 09.2; /* 3.0/1.0 */}
		              }
		            }
		            if (EE > 6.382352)
		            {
		              if (EE <= 9.106)
		              {
		                if (EE <= 6.73075) { result = 09.3; /* 6.0/3.0 */}
		                else if (EE > 6.73075)
		                {
		                  if (SCH <= -0.373374) { result = 09.1; /* 11.0/4.0 */}
		                  else if (SCH > -0.373374)
		                  {
		                    if (EE <= 8.619875)
		                    {
		                      if (EE <= 8.496125) { result = 09.3; /* 5.0/2.0 */}
		                      else if (EE > 8.496125) { result = 09.2; /* 2.0 */}
		                    }
		                    if (EE > 8.619875) { result = 09.1; /* 5.0 */}
		                  }
		                }
		              }
		              if (EE > 9.106) { result = 09.0; /* 4.0 */}
		            }
		          }
		        }
		        if (PE > 4.268441)
		        {
		          if (SE <= 9.271976) { result = 08.9; /* 5.0/1.0 */}
		          else if (SE > 9.271976)
		          {
		            if (SE <= 9.28526) { result = 08.7; /* 3.0 */}
		            else if (SE > 9.28526) { result = 08.2; /* 2.0/1.0 */}
		          }
		        }
		      }
		      if (SE > 9.561631)
		      {
		        if (EE <= 7.2)
		        {
		          if (SE <= 10.084617)
		          {
		            if (SE <= 9.636089)
		            {
		              if (SE <= 9.596428) { result = 08.6; /* 2.0/1.0 */}
		              else if (SE > 9.596428) { result = 08.8; /* 3.0/2.0 */}
		            }
		            if (SE > 9.636089)
		            {
		              if (EE <= 5.4805) { result = 09.5; /* 3.0/2.0 */}
		              else if (EE > 5.4805) { result = 07.2; /* 3.0/2.0 */}
		            }
		          }
		          if (SE > 10.084617)
		          {
		            if (SCH <= -0.143246)
		            {
		              if (EE <= 6.331813) { result = 10.1; /* 3.0 */}
		              else if (EE > 6.331813)
		              {
		                if (EE <= 6.3705) { result = 09.6; /* 2.0/1.0 */}
		                else if (EE > 6.3705) { result = 09.5; /* 2.0/1.0 */}
		              }
		            }
		            if (SCH > -0.143246)
		            {
		              if (EE <= 6.8215)
		              {
		                if (EE <= 6.3445) { result = 09.9; /* 3.0/1.0 */}
		                else if (EE > 6.3445) { result = 10.0; /* 4.0/1.0 */}
		              }
		              if (EE > 6.8215) { result = 09.9; /* 3.0/1.0 */}
		            }
		          }
		        }
		        if (EE > 7.2)
		        {
		          if (EE <= 7.813)
		          {
		            if (EE <= 7.652625) { result = 09.2; /* 6.0/2.0 */}
		            else if (EE > 7.652625) { result = 08.8; /* 2.0/1.0 */}
		          }
		          if (EE > 7.813) { result = 09.3; /* 3.0/1.0 */}
		        }
		      }
		    }
		  }
		  if (SE > 10.164422)
		  {
		    if (SCH <= -0.245922)
		    {
		      if (PE <= 4.056221)
		      {
		        if (SE <= 11.333807) { result = 11.9; /* 3.0/2.0 */}
		        else if (SE > 11.333807) { result = 09.7; /* 3.0/2.0 */}
		      }
		      if (PE > 4.056221)
		      {
		        if (EE <= 4.726703)
		        {
		          if (EE <= 3.068625) { result = 09.9; /* 4.0/3.0 */}
		          else if (EE > 3.068625) { result = 09.4; /* 5.0/3.0 */}
		        }
		        if (EE > 4.726703) { result = 09.6; /* 7.0/5.0 */}
		      }
		    }
		    if (SCH > -0.245922)
		    {
		      if (PE <= 4.769337)
		      {
		        if (SCH <= -0.173729)
		        {
		          if (SCH <= -0.239893) { result = 08.3; /* 2.0/1.0 */}
		          else if (SCH > -0.239893) { result = 10.9; /* 3.0/1.0 */}
		        }
		        if (SCH > -0.173729)
		        {
		          if (SE <= 10.47544)
		          {
		            if (SE <= 10.475336) { result = 10.4; /* 2.0/1.0 */}
		            else if (SE > 10.475336)
		            {
		              if (EE <= 5.171203) { result = 10.4; /* 2.0/1.0 */}
		              else if (EE > 5.171203) { result = 10.1; /* 3.0/1.0 */}
		            }
		          }
		          if (SE > 10.47544)
		          {
		            if (SE <= 10.507124) { result = 10.7; /* 2.0 */}
		            else if (SE > 10.507124) { result = 09.0; /* 3.0/2.0 */}
		          }
		        }
		      }
		      if (PE > 4.769337)
		      {
		        if (SCH <= -0.160687)
		        {
		          if (SE <= 11.201319) { result = 09.1; /* 7.0/4.0 */}
		          else if (SE > 11.201319) { result = 09.2; /* 3.0 */}
		        }
		        if (SCH > -0.160687)
		        {
		          if (SE <= 11.517466)
		          {
		            if (EE <= 4.138375) { result = 07.9; /* 2.0 */}
		            else if (EE > 4.138375) { result = 08.3; /* 2.0/1.0 */}
		          }
		          if (SE > 11.517466)
		          {
		            if (EE <= 3.880188) { result = 08.3; /* 2.0/1.0 */}
		            else if (EE > 3.880188) { result = 08.9; /* 2.0/1.0 */}
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
		Double[][] results = new Double[1][4];
		SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
		EffectiveAtomPolarizabilityDescriptor descriptor3 = new EffectiveAtomPolarizabilityDescriptor();
		PiElectronegativityDescriptor descriptor4 = new PiElectronegativityDescriptor();

		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(atom,atomContainer).getValue()).doubleValue());
		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(atom,atomContainer).getValue()).doubleValue());
    	
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
		IAtom positionC = null; 
		List listAtoms = atomContainer.getConnectedAtomsList(atom);
		for(Iterator it = listAtoms.iterator(); it.hasNext();){
			IAtom atom2 = (IAtom)it.next();
			if(((IBond)atomContainer.getBond(atom, atom2)).getOrder() > 1)
				positionC = atom2;
		}

		IBond bond = atomContainer.getBond(positionX, positionC);
		try {
        	/*0*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1*/
    		PartialPiChargeDescriptor descriptor2 = new PartialPiChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC,atomContainer).getValue()).doubleValue());
    		/*2*/
    		BondPartialSigmaChargeDescriptor descriptor3 = new BondPartialSigmaChargeDescriptor();
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(bond, atomContainer).getValue()).doubleValue());
    		/*3*/
    		SigmaElectronegativityDescriptor descriptor4 = new SigmaElectronegativityDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*4*/
    		PartialPiChargeDescriptor descriptor5 = new PartialPiChargeDescriptor();
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


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
package org.openscience.cdk.qsar.descriptors.bond;

import java.util.Iterator;

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
import org.openscience.cdk.qsar.IBondDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialPiChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialSigmaChargeDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.SigmaElectronegativityDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactPDBReaction;

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
 * @author           Miguel Rojas
 * @cdk.created      2006-05-26
 * @cdk.module       qsar
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 */
public class IPBondDescriptor implements IBondDescriptor {
	
	/** parameter for inizate IReactionSet*/
	private boolean setEnergy = false;
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
        return new Object[0];
    }
	/**
	 *  This method calculates the ionization potential of a bond.
	 *
	 *@param  container         Parameter is the IAtomContainer.
	 *@return                   The ionization potential
	 *@exception  CDKException  Description of the Exception
	 */
	public DescriptorValue calculate(IBond bond, IAtomContainer container) throws CDKException{
		reactionSet = container.getBuilder().newReactionSet();
    	double resultD = -1.0;
		boolean isTarget = false;
		Double[][] resultsH = null;
		
		try{
			HueckelAromaticityDetector.detectAromaticity(container,true);
		} catch (Exception exc)
		{
		}
        
        if(bond.getOrder() > 1 && bond.getAtom(0).getSymbol().equals("C") && 
    				bond.getAtom(1).getSymbol().equals("C")){
        		
        		AtomContainerSet conjugatedPi = ConjugatedPiSystemsDetector.detect(container);
                Iterator acI = conjugatedPi.atomContainers();
        		boolean isConjugatedPi = false;
        		boolean isConjugatedPi_withHeteroatom = false;
                while(acI.hasNext()){
        			IAtomContainer ac = (IAtomContainer) acI.next();
        			if(ac.contains(bond)){
        				isConjugatedPi = true;
            			isTarget = true;
            			
            			Iterator atoms = ac.atoms();
            			while(atoms.hasNext()){
            				IAtom atomsss = (IAtom) atoms.next();
            				
            				if(!atomsss.getSymbol().equals("C")){
            					isConjugatedPi_withHeteroatom = true;
            					resultsH = calculateCojugatedPiSystWithHeteroDescriptor(bond, container, ac);
                    			resultD = getPySystWithHetero(resultsH);
            					resultD += 0.05;
                    			break;
            				}
            			}
            			
            			if(!isConjugatedPi_withHeteroatom){
	            			resultsH = calculateCojugatedPiSystWithoutHeteroDescriptor(bond, container, ac);
	            			resultD = getConjugatedPiSys(resultsH);
        					resultD += 0.05;
                			break;
            			}
        			}
        		}
        		
                if(!isConjugatedPi){

					resultsH = calculatePiSystWithoutHeteroDescriptor(bond, container);
					resultD = getAcetyl_EthylWithoutHetero(resultsH);
					resultD += 0.05;
        			isTarget = true;
                }
		}

		if(isTarget){
    		/* extract reaction*/
    		if(setEnergy){
    			IMoleculeSet setOfReactants = container.getBuilder().newMoleculeSet();
    			setOfReactants.addMolecule((IMolecule) container);
    			IReactionProcess type  = new ElectronImpactPDBReaction();
    			bond.setFlag(CDKConstants.REACTIVE_CENTER,true);
    	        Object[] params = {Boolean.TRUE};
    	        type.setParameters(params);
    	        IReactionSet pbb = type.initiate(setOfReactants, null);
    	        Iterator it = pbb.reactions();
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
	 * tree desicion for the PySystWithHetero
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getPySystWithHetero(Double[][] resultsH) {
		double result = 0.0;
		double SE_1 = (resultsH[0][0]).doubleValue();
		double SE_2 = (resultsH[0][1]).doubleValue();
		double EE_1  = (resultsH[0][2]).doubleValue();
		double RES_c2 = (resultsH[0][3]).doubleValue();
	
		if (SE_2 <= 0.040658)
		{
		  if (SE_1 <= 0)
		  {
		    if (RES_c2 <= 0.001483)
		    {
		      if (SE_2 <= 0) { result = 09.2; /* 13.0/11.0 */}
		      else if (SE_2 > 0) { result = 08.0; /* 2.0/1.0 */}
		    }
		    if (RES_c2 > 0.001483)
		    {
		      if (EE_1 <= 0.00984)
		      {
		        if (SE_2 <= 0.024004) { result = 07.7; /* 2.0/1.0 */}
		        else if (SE_2 > 0.024004) { result = 07.8; /* 3.0/2.0 */}
		      }
		      if (EE_1 > 0.00984)
		      {
		        if (SE_2 <= 0.031039) { result = 07.9; /* 3.0/1.0 */}
		        else if (SE_2 > 0.031039)
		        {
		          if (SE_2 <= 0.033705) { result = 08.3; /* 2.0/1.0 */}
		          else if (SE_2 > 0.033705) { result = 09.1; /* 2.0/1.0 */}
		        }
		      }
		    }
		  }
		  if (SE_1 > 0)
		  {
		    if (RES_c2 <= -0.000099)
		    {
		      if (RES_c2 <= -0.004384) { result = 08.5; /* 3.0/1.0 */}
		      else if (RES_c2 > -0.004384) { result = 07.3; /* 3.0/2.0 */}
		    }
		    if (RES_c2 > -0.000099)
		    {
		      if (SE_1 <= 0.00513)
		      {
		        if (SE_1 <= 0.003843)
		        {
		          if (SE_1 <= 0.002366) { result = 08.9; /* 2.0/1.0 */}
		          else if (SE_1 > 0.002366) { result = 07.5; /* 2.0/1.0 */}
		        }
		        if (SE_1 > 0.003843) { result = 09.1; /* 5.0/3.0 */}
		      }
		      if (SE_1 > 0.00513)
		      {
		        if (SE_1 <= 0.007954) { result = 09.6; /* 2.0/1.0 */}
		        else if (SE_1 > 0.007954) { result = 08.6; /* 2.0/1.0 */}
		      }
		    }
		  }
		}
		if (SE_2 > 0.040658)
		{
		  if (SE_2 <= 0.069065)
		  {
		    if (RES_c2 <= 0.006856)
		    {
		      if (RES_c2 <= 0.005076)
		      {
		        if (RES_c2 <= 0.004962)
		        {
		          if (SE_2 <= 0.050662) { result = 07.7; /* 2.0/1.0 */}
		          else if (SE_2 > 0.050662) { result = 08.7; /* 2.0/1.0 */}
		        }
		        if (RES_c2 > 0.004962) { result = 08.1; /* 5.0/2.0 */}
		      }
		      if (RES_c2 > 0.005076)
		      {
		        if (SE_2 <= 0.051366)
		        {
		          if (SE_2 <= 0.049396) { result = 08.0; /* 2.0/1.0 */}
		          else if (SE_2 > 0.049396) { result = 09.3; /* 2.0/1.0 */}
		        }
		        if (SE_2 > 0.051366)
		        {
		          if (SE_2 <= 0.060998) { result = 08.3; /* 2.0 */}
		          else if (SE_2 > 0.060998) { result = 08.2; /* 2.0 */}
		        }
		      }
		    }
		    if (RES_c2 > 0.006856)
		    {
		      if (EE_1 <= 0.020934) { result = 07.4; /* 12.0/8.0 */}
		      else if (EE_1 > 0.020934)
		      {
		        if (SE_2 <= 0.068206)
		        {
		          if (SE_2 <= 0.063051) { result = 07.3; /* 2.0/1.0 */}
		          else if (SE_2 > 0.063051) { result = 07.5; /* 6.0/3.0 */}
		        }
		        if (SE_2 > 0.068206) { result = 07.6; /* 3.0/2.0 */}
		      }
		    }
		  }
		  if (SE_2 > 0.069065)
		  {
		    if (EE_1 <= 0.668163)
		    {
		      if (EE_1 <= 0.032157)
		      {
		        if (SE_1 <= 0.002366)
		        {
		          if (EE_1 <= 0.026958)
		          {
		            if (RES_c2 <= 0.005329) { result = 08.4; /* 2.0/1.0 */}
		            else if (RES_c2 > 0.005329) { result = 08.9; /* 5.0/1.0 */}
		          }
		          if (EE_1 > 0.026958)
		          {
		            if (SE_2 <= 0.081676) { result = 08.3; /* 2.0/1.0 */}
		            else if (SE_2 > 0.081676) { result = 08.0; /* 4.0/1.0 */}
		          }
		        }
		        if (SE_1 > 0.002366) { result = 07.7; /* 3.0/2.0 */}
		      }
		      if (EE_1 > 0.032157)
		      {
		        if (RES_c2 <= 0.015232)
		        {
		          if (SE_2 <= 0.137014) { result = 08.6; /* 5.0/2.0 */}
		          else if (SE_2 > 0.137014) { result = 08.4; /* 6.0/2.0 */}
		        }
		        if (RES_c2 > 0.015232)
		        {
		          if (SE_2 <= 0.138782) { result = 08.1; /* 2.0/1.0 */}
		          else if (SE_2 > 0.138782) { result = 08.2; /* 5.0/3.0 */}
		        }
		      }
		    }
		    if (EE_1 > 0.668163)
		    {
		      if (SE_2 <= 2.067231) { result = 07.7; /* 3.0/1.0 */}
		      else if (SE_2 > 2.067231) { result = 09.5; /* 4.0/2.0 */}
		    }
		  }
		}

		
		return result;
	}
	/**
	 * tree desicion for the ConjugatedPiSys
	 * 
	 * @param resultsH Array which contains the results of each descriptor
	 * @return the result
	 */
	private double getConjugatedPiSys(Double[][] resultsH) {
		double result = 0.0;
		double SE_1 = (resultsH[0][0]).doubleValue();
		double EE_1 = (resultsH[0][1]).doubleValue();
		double RES_c2  = (resultsH[0][2]).doubleValue();
		
		if (RES_c2 <= 8.264821)
		{
		  if (RES_c2 <= 8.115026)
		  {
		    if (EE_1 <= 0.392309)
		    {
		      if (EE_1 <= 0.311778) { result = 08.3; /* 5.0/3.0 */}
		      else if (EE_1 > 0.311778)
		      {
		        if (SE_1 <= 0.003603)
		        {
		          if (SE_1 <= 0.003359) { result = 08.2; /* 2.0 */}
		          else if (SE_1 > 0.003359) { result = 08.3; /* 3.0/2.0 */}
		        }
		        if (SE_1 > 0.003603)
		        {
		          if (RES_c2 <= 8.085076) { result = 08.2; /* 3.0/1.0 */}
		          else if (RES_c2 > 8.085076) { result = 08.1; /* 4.0/1.0 */}
		        }
		      }
		    }
		    if (EE_1 > 0.392309)
		    {
		      if (RES_c2 <= 8.057972)
		      {
		        if (EE_1 <= 0.494797)
		        {
		          if (EE_1 <= 0.429063) { result = 08.6; /* 3.0/1.0 */}
		          else if (EE_1 > 0.429063) { result = 08.5; /* 2.0 */}
		        }
		        if (EE_1 > 0.494797) { result = 07.9; /* 2.0/1.0 */}
		      }
		      if (RES_c2 > 8.057972)
		      {
		        if (SE_1 <= 0.015213)
		        {
		          if (EE_1 <= 1.011935)
		          {
		            if (SE_1 <= 0.014856) { result = 08.4; /* 8.0/1.0 */}
		            else if (SE_1 > 0.014856) { result = 08.2; /* 2.0 */}
		          }
		          if (EE_1 > 1.011935)
		          {
		            if (RES_c2 <= 8.090639) { result = 08.6; /* 2.0/1.0 */}
		            else if (RES_c2 > 8.090639) { result = 08.4; /* 4.0/1.0 */}
		          }
		        }
		        if (SE_1 > 0.015213) { result = 08.5; /* 5.0/3.0 */}
		      }
		    }
		  }
		  if (RES_c2 > 8.115026)
		  {
		    if (EE_1 <= 0.303132)
		    {
		      if (SE_1 <= 0.005082)
		      {
		        if (SE_1 <= 0.002769) { result = 08.4; /* 3.0/2.0 */}
		        else if (SE_1 > 0.002769) { result = 06.2; /* 2.0/1.0 */}
		      }
		      if (SE_1 > 0.005082) { result = 07.7; /* 4.0/2.0 */}
		    }
		    if (EE_1 > 0.303132)
		    {
		      if (RES_c2 <= 8.152026)
		      {
		        if (SE_1 <= 0.001726) { result = 07.9; /* 2.0/1.0 */}
		        else if (SE_1 > 0.001726)
		        {
		          if (SE_1 <= 0.015444) { result = 08.2; /* 14.0/6.0 */}
		          else if (SE_1 > 0.015444) { result = 08.3; /* 2.0/1.0 */}
		        }
		      }
		      if (RES_c2 > 8.152026)
		      {
		        if (RES_c2 <= 8.213491)
		        {
		          if (SE_1 <= 0.00627)
		          {
		            if (SE_1 <= 0.005641) { result = 07.8; /* 3.0/1.0 */}
		            else if (SE_1 > 0.005641) { result = 08.1; /* 2.0/1.0 */}
		          }
		          if (SE_1 > 0.00627)
		          {
		            if (EE_1 <= 1.479227) { result = 08.0; /* 7.0/3.0 */}
		            else if (EE_1 > 1.479227) { result = 07.8; /* 2.0/1.0 */}
		          }
		        }
		        if (RES_c2 > 8.213491)
		        {
		          if (SE_1 <= 0.003487) { result = 07.8; /* 2.0/1.0 */}
		          else if (SE_1 > 0.003487)
		          {
		            if (EE_1 <= 0.41657) { result = 08.3; /* 2.0/1.0 */}
		            else if (EE_1 > 0.41657) { result = 08.5; /* 5.0/2.0 */}
		          }
		        }
		      }
		    }
		  }
		}
		if (RES_c2 > 8.264821)
		{
		  if (RES_c2 <= 8.972797)
		  {
		    if (EE_1 <= 0.562137)
		    {
		      if (RES_c2 <= 8.568494)
		      {
		        if (SE_1 <= 0.004116) { result = 07.4; /* 4.0/2.0 */}
		        else if (SE_1 > 0.004116)
		        {
		          if (SE_1 <= 0.012219) { result = 07.2; /* 2.0/1.0 */}
		          else if (SE_1 > 0.012219) { result = 07.5; /* 2.0/1.0 */}
		        }
		      }
		      if (RES_c2 > 8.568494)
		      {
		        if (RES_c2 <= 8.923106)
		        {
		          if (RES_c2 <= 8.883992)
		          {
		            if (SE_1 <= 0.003412) { result = 08.4; /* 2.0/1.0 */}
		            else if (SE_1 > 0.003412) { result = 09.5; /* 2.0 */}
		          }
		          if (RES_c2 > 8.883992) { result = 09.0; /* 4.0/2.0 */}
		        }
		        if (RES_c2 > 8.923106)
		        {
		          if (RES_c2 <= 8.931694) { result = 08.8; /* 4.0/2.0 */}
		          else if (RES_c2 > 8.931694)
		          {
		            if (RES_c2 <= 8.964206) { result = 08.4; /* 2.0 */}
		            else if (RES_c2 > 8.964206) { result = 08.5; /* 3.0/1.0 */}
		          }
		        }
		      }
		    }
		    if (EE_1 > 0.562137)
		    {
		      if (EE_1 <= 1.529439)
		      {
		        if (RES_c2 <= 8.287498) { result = 07.3; /* 2.0/1.0 */}
		        else if (RES_c2 > 8.287498)
		        {
		          if (RES_c2 <= 8.883992) { result = 09.0; /* 2.0 */}
		          else if (RES_c2 > 8.883992) { result = 08.7; /* 2.0 */}
		        }
		      }
		      if (EE_1 > 1.529439)
		      {
		        if (EE_1 <= 1.920118) { result = 08.1; /* 2.0/1.0 */}
		        else if (EE_1 > 1.920118) { result = 06.3; /* 2.0/1.0 */}
		      }
		    }
		  }
		  if (RES_c2 > 8.972797)
		  {
		    if (EE_1 <= 0.204866)
		    {
		      if (SE_1 <= 0.002325)
		      {
		        if (RES_c2 <= 10.001048)
		        {
		          if (RES_c2 <= 9.853716) { result = 07.6; /* 2.0/1.0 */}
		          else if (RES_c2 > 9.853716) { result = 08.6; /* 2.0/1.0 */}
		        }
		        if (RES_c2 > 10.001048)
		        {
		          if (RES_c2 <= 10.098286) { result = 08.3; /* 2.0/1.0 */}
		          else if (RES_c2 > 10.098286) { result = 08.0; /* 2.0/1.0 */}
		        }
		      }
		      if (SE_1 > 0.002325)
		      {
		        if (SE_1 <= 0.004762) { result = 09.0; /* 3.0/2.0 */}
		        else if (SE_1 > 0.004762) { result = 09.2; /* 2.0/1.0 */}
		      }
		    }
		    if (EE_1 > 0.204866)
		    {
		      if (EE_1 <= 1.158042)
		      {
		        if (EE_1 <= 1.157985) { result = 08.6; /* 8.0/5.0 */}
		        else if (EE_1 > 1.157985) { result = 08.5; /* 3.0/1.0 */}
		      }
		      if (EE_1 > 1.158042) { result = 08.2; /* 5.0/2.0 */}
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
	private double getAcetyl_EthylWithoutHetero(Double[][] resultsH) {
		double result = 0.0;
		double SE_1 = (resultsH[0][0]).doubleValue();
		double SCH_1 = (resultsH[0][1]).doubleValue();
		double SE_2  = (resultsH[0][2]).doubleValue();
		double SCH_2 = (resultsH[0][3]).doubleValue();
		double RES_c1  = (resultsH[0][4]).doubleValue();
		double RES_c2  = (resultsH[0][5]).doubleValue();
		
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
	 * This method calculates the ionization potential of a bond and set the ionization
	 * energy into each reaction as property
	 * 
	 * @return The IReactionSet value
	 */
	public IReactionSet getReactionSet(IBond bond, IAtomContainer container) throws CDKException{
		setEnergy = true;
		calculate(bond,container);
		return reactionSet;
	}
	
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculatePiSystWithoutHeteroDescriptor(IBond bond, IAtomContainer atomContainer) {
		Double[][] results = new Double[1][6];
		Integer[] params = new Integer[1];
		IAtom positionC = bond.getAtom(0);
		IAtom positionX = bond.getAtom(1);
		try {
        	/*0_1*/
			SigmaElectronegativityDescriptor descriptor1 = new SigmaElectronegativityDescriptor();
    		results[0][0]= new Double(((DoubleResult)descriptor1.calculate(positionC, atomContainer).getValue()).doubleValue());
        	/*1_1*/
    		PartialSigmaChargeDescriptor descriptor2 = new PartialSigmaChargeDescriptor();
    		results[0][1]= new Double(((DoubleResult)descriptor2.calculate(positionC, atomContainer).getValue()).doubleValue());
    		
    		/*0_2*/
    		SigmaElectronegativityDescriptor descriptor3 = new SigmaElectronegativityDescriptor();
    		results[0][2]= new Double(((DoubleResult)descriptor3.calculate(positionX, atomContainer).getValue()).doubleValue());
        	/*1_2*/
    		PartialSigmaChargeDescriptor descriptor4 = new PartialSigmaChargeDescriptor();
    		results[0][3]= new Double(((DoubleResult)descriptor4.calculate(positionX, atomContainer).getValue()).doubleValue());
    		
    		/*  */
    		ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
			DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bond, atomContainer).getValue());
			results[0][4] = new Double(dar.get(0));
			results[0][5] = new Double(dar.get(1));
    		
    		
		} catch (CDKException e) {
			e.printStackTrace();
		}
		return results;
	}
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCojugatedPiSystWithoutHeteroDescriptor(IBond bond, IAtomContainer atomContainer, IAtomContainer conjugatedSys) {
		Double[][] results = new Double[1][3];
		
		results[0][0] = new Double(0.0);
		results[0][1] = new Double(0.0);
		results[0][2] = new Double(0.0);
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
			double result1;
			try {
				result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
				if(result1 > (results[0][0]).doubleValue())
					results[0][0] = new Double(result1);
				
				SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
				double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
				results[0][2] = new Double((results[0][2]).doubleValue() + result2);
				
			} catch (CDKException e) {
				e.printStackTrace();
			}
			
			
		}
		
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			
			
			IBond bondsss = (IBond) bondIt.next();
			
			try {
				ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
				DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
				double result1 = dar.get(0);
				double resutt2 = dar.get(1);
				double result12 = (result1+resutt2);
				
				double resultT = 0;
				if(result12 != 0)
					resultT = result12/2;
				
				results[0][1]  = new Double((results[0][1]).doubleValue() + resultT);
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		if((results[0][1]).doubleValue() != 0)
			results[0][1] = new Double((results[0][1]).doubleValue()/conjugatedSys.getAtomCount());
		
		if((results[0][2]).doubleValue() != 0)
			results[0][2] = new Double((results[0][2]).doubleValue()/conjugatedSys.getAtomCount());
		
		return results;
	}
	/**
	 * Calculate the necessary descriptors for pi systems without heteroatom
	 * 
	 * @param atomContainer The IAtomContainer
	 * @return     Array with the values of the descriptors.
	 */
	private Double[][] calculateCojugatedPiSystWithHeteroDescriptor(IBond bond, IAtomContainer atomContainer, IAtomContainer conjugatedSys) {
		Double[][] results = new Double[1][4];
		
		results[0][0] = new Double(0.0);
		results[0][1] = new Double(0.0);
		results[0][2] = new Double(0.0);
		results[0][3] = new Double(0.0);
		Iterator atomIt = conjugatedSys.atoms();
		while(atomIt.hasNext()){
			IAtom atomsss = (IAtom) atomIt.next();
			
			try {
			
				if(atomsss.getSymbol().equals("C")){
					PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
					double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
					if(result1 > (results[0][0]).doubleValue())
						results[0][0] = new Double(result1);
				}else{
					
					PartialPiChargeDescriptor descriptor1 = new PartialPiChargeDescriptor();
					double result1 = ((DoubleResult)descriptor1.calculate(atomsss,atomContainer).getValue()).doubleValue();
					results[0][1] = new Double(result1);
				}
				
				SigmaElectronegativityDescriptor descriptor2 = new SigmaElectronegativityDescriptor();
				double result2 = ((DoubleResult)descriptor2.calculate(atomsss,atomContainer).getValue()).doubleValue();
				results[0][3] = new Double((results[0][3]).doubleValue() + result2);
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
			
		}
		
		Iterator bondIt = conjugatedSys.bonds();
		while(bondIt.hasNext()){
			IBond bondsss = (IBond) bondIt.next();
			try {
				
				ResonancePositiveChargeDescriptor descriptor5 = new ResonancePositiveChargeDescriptor();
				DoubleArrayResult dar = ((DoubleArrayResult)descriptor5.calculate(bondsss,atomContainer).getValue());
				double result1 = dar.get(0);
				double resutt2 = dar.get(1);
				double result12 = (result1+resutt2);
				
				double resultT = 0;
				if(result12 != 0)
					resultT = result12/2;
				
				results[0][2] = new Double((results[0][2]).doubleValue() + resultT);
			
			} catch (CDKException e) {
				e.printStackTrace();
			}
		}
		if((results[0][2]).doubleValue() != 0)
			results[0][2] = new Double((results[0][1]).doubleValue()/conjugatedSys.getAtomCount());
		
		if((results[0][3]).doubleValue() != 0)
			results[0][3] = new Double((results[0][2]).doubleValue()/conjugatedSys.getAtomCount());
		
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


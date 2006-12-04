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
package org.openscience.cdk.qsar.descriptors.molecular;

import java.util.Iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.IPBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;

/**
 *  This class returns the ionization potential of a molecule. It is
 *  based on a decision tree which is extracted from Weka(J48) from 
 *  experimental values. Up to now is
 *  only possible for atomContainers which contain; see IPAtomicDescriptor and
 *  IPBondDescriptor.
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
 * @see IPAtomicDescriptor
 * @see IPBondDescriptor
 */
public class IPMolecularDescriptor implements IMolecularDescriptor {

	/** parameter for inizate IReactionSet*/
	private boolean setEnergy = false;
	private IReactionSet reactionSet;
    /**
     *  Constructor for the IPMolecularDescriptor object
     */
    public IPMolecularDescriptor() { }


    /**
     *  Gets the specification attribute of the IPMolecularDescriptor object
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ip",
            this.getClass().getName(),
            "$Id: IPMolecularDescriptor.java 7032 2006-09-22 15:26:48Z kaihartmann $",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the IPMolecularDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }


    /**
     *  Gets the parameters attribute of the IPMolecularDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        return (null);
        // no parameters to return
    }


    /**
     *  Description of the Method
     *
     *@param  atomContainer                AtomContainer
     *@return                   The 1,2 .. ionization energy
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
    	reactionSet = atomContainer.getBuilder().newReactionSet();
    	DoubleArrayResult dar = new DoubleArrayResult();
    	IPAtomicDescriptor descriptorA = new IPAtomicDescriptor();
    	Iterator itA = atomContainer.atoms();
    	while(itA.hasNext()){
    		IAtom atom = (IAtom) itA.next();
    		double result = -1;
    		if(setEnergy){
    			IReactionSet irs = descriptorA.getReactionSet(atom,atomContainer);
    			if(irs.getReactionCount() > 0){
	    			Iterator iter = irs.reactions();
	    			while(iter.hasNext()){
	    				reactionSet.addReaction((IReaction)iter.next());
	    			}
    			}
    			
    		}else
    			result = ((DoubleResult)descriptorA.calculate(atom,atomContainer).getValue()).doubleValue();
    		
    		if(result != -1)
    		dar.add(result);
    	}
            
    	IPBondDescriptor descriptorB = new IPBondDescriptor();
    	for(int i = 0; i < atomContainer.getBondCount(); i++){
    		double result = -1;
    		if(setEnergy){
    			IReactionSet irs = descriptorB.getReactionSet(atomContainer.getBond(i),atomContainer);
    			if(irs.getReactionCount() > 0){
	    			Iterator iter = irs.reactions();
	    			while(iter.hasNext()){
	    				reactionSet.addReaction((IReaction)iter.next());
	    			}
    			}
    		}else
    			result = ((DoubleResult)descriptorB.calculate(atomContainer.getBond(i),atomContainer).getValue()).doubleValue();
    		
    		if(result != -1)
        		dar.add(result);
    	}
    	return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), dar);
    }
    /**
	 * This method calculates the ionization potential of a molecule and set the ionization
	 * energy into each reaction as property
	 * 
	 * @return The IReactionSet value
	 */
	public IReactionSet getReactionSet(IAtomContainer container) throws CDKException{
		setEnergy = true;
		calculate(container);
		return reactionSet;
	}


    /**
     *  Gets the parameterNames attribute of the IPMolecularDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return (null);
    }



    /**
     *  Gets the parameterType attribute of the IPMolecularDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return (null);
    }
}


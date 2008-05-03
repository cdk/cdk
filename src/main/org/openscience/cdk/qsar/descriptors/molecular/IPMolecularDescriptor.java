/* $Revision$ $Author$ $Date$
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.IPAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.bond.IPBondDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.DoubleResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.LonePairElectronChecker;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  This class returns the ionization potential of a molecule. It is
 *  based on a decision tree which is extracted from Weka(J48) from
 *  experimental values. Up to now is
 *  only possible for atomContainers which contain; see IPAtomicDescriptor and
 *  IPBondDescriptor.
 *
 * The descriptor assumes that explicit hydrogens have been added to the molecule
 *
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>addlp</td>
 *     <td>true</td>
 *     <td>If true lone pairs are added to the molecule</td>
 *   </tr>
 * </table>
 *
 * @author           Miguel Rojas
 * @cdk.created      2006-05-26
 * @cdk.module       qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set          qsar-descriptors
 * @cdk.dictref      qsar-descriptors:ionizationPotential
 * @cdk.bug          1628465
 * @cdk.bug          1651264
 * @cdk.bug          1856148
 * @cdk.keyword      ionization potential
 *
 * @see IPAtomicDescriptor
 * @see IPBondDescriptor
 */
public class IPMolecularDescriptor implements IMolecularDescriptor {

	private IReactionSet reactionSet;
    private boolean addlp = true;
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
            "$Id$",
            "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the IPMolecularDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) throw new CDKException("One parameter expected");
        if (!(params[0] instanceof Boolean)) throw new CDKException("Boolean parameter expected");
        addlp = (Boolean) params[0];
    }


    /**
     * Gets the parameters attribute of the IPMolecularDescriptor object
     *
     * @return The parameters value
     */
    public Object[] getParameters() {
        return new Object[]{new Boolean(addlp)};
    }


    /**
     *  It calculates the first ionization energy of a molecule.
     *
     *@param  atomContainer     AtomContainer
     *@return                   The first ionization energy
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculate(IAtomContainer atomContainer) throws CDKException {
        IAtomContainer local;
        if (addlp) {
            try {
                local = (IAtomContainer) atomContainer.clone();
                LonePairElectronChecker lpcheck = new LonePairElectronChecker();
                lpcheck.saturate(local);
            } catch (CloneNotSupportedException e) {
                throw new CDKException("Error during clone");
            }

        } else local = atomContainer;
        String[] descriptorNames = {"MolIP"};

    	DoubleResult value = new DoubleResult(((DoubleArrayResult)calculatePlus(local).getValue()).get(0));
    	return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), value, descriptorNames );
    }

    /**
     *  It calculates the 1,2,.. ionization energies of a molecule.
     *
     *@param  atomContainer     AtomContainer
     *@return                   The 1, 2, .. ionization energies
     *@exception  CDKException  Possible Exceptions
     */
    public DescriptorValue calculatePlus(IAtomContainer atomContainer) throws CDKException {
    	String[] descriptorNames = {"MolIP"};

    	reactionSet = atomContainer.getBuilder().newReactionSet();
        ArrayList<Double> dar = new ArrayList<Double>();
        IPAtomicDescriptor descriptorA = new IPAtomicDescriptor();
        Iterator itA = atomContainer.atoms();
        while(itA.hasNext()){
            IAtom atom = (IAtom) itA.next();

            if(atomContainer.getConnectedLonePairsCount(atom) == 0)
            	continue;

            double result = ((DoubleResult)descriptorA.calculate(atom,atomContainer).getValue()).doubleValue();
            if(result == -1)
            	continue;
            IReactionSet irs = descriptorA.getReactionSet();
            if(irs.getReactionCount() > 0){
                Iterator iter = irs.reactions();
                while(iter.hasNext()){
                    reactionSet.addReaction((IReaction)iter.next());
                }
            }

            if(result != -1)
            	dar.add(result);
        }

        IPBondDescriptor descriptorB = new IPBondDescriptor();
        Iterator itB = atomContainer.bonds();
        while(itB.hasNext()){
            IBond bond = (IBond) itB.next();

        	if(bond.getOrder() == IBond.Order.SINGLE)
        		continue;

        	double result = ((DoubleResult)descriptorB.calculate(bond,atomContainer).getValue()).doubleValue();

        	if(result == -1)
            	continue;

        	IReactionSet irs = descriptorB.getReactionSet();

            if(irs.getReactionCount() > 0){
            	Iterator iter = irs.reactions();
            	while(iter.hasNext()){
            		reactionSet.addReaction((IReaction)iter.next());
            	}
            }

            dar.add(result);
        }

        if(dar.size() == 0)
        	dar.add(-1.0);

        DoubleArrayResult results = arrangingEnergy(dar);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), results, descriptorNames);
    }

    /**
     * put in increasing order the ArrayList
     *
     * @param array The ArrayList to order
     * @return      The DoubleArrayResult ordered
     */
    private DoubleArrayResult arrangingEnergy(ArrayList array){

    	DoubleArrayResult results = new DoubleArrayResult();
    	int count = array.size();

    	for(int i = 0; i < count; i++){
	    	double min = (Double) array.get(0);
	    	int pos = 0;
			for(int j = 0; j < array.size(); j++){
				double value = (Double) array.get(j);
				if( value < min){
					min = value;
					pos = j;
				}
			}
	    	array.remove(pos);
	    	results.add(min);
    	}

    	return results;
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResultType();
    }

    /**
	 * Get the reactions obtained with the ionization.
	 * The energy is set as property
     *
     * @return The IReactionSet value
     * @throws org.openscience.cdk.exception.CDKException
     */
    public IReactionSet getReactionSet() throws CDKException{
        return reactionSet;
    }


    /**
     *  Gets the parameterNames attribute of the IPMolecularDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        return new String[] {"addlp"};
    }



    /**
     *  Gets the parameterType attribute of the IPMolecularDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
        return new Boolean(addlp);
    }
}



/* $Revision$ $Author$ $Date$
 *
 * Copyright (c) 2014 Collaborative Drug Discovery, Inc. <alex@collaborativedrug.com>
 *
 * Implemented by Alex M. Clark, produced by Collaborative Drug Discovery, Inc.
 * Made available to the CDK community under the terms of the GNU LGPL.
 *
 *    http://collaborativedrug.com
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.volume.VABCVolume;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.qsar.*;
import org.openscience.cdk.qsar.result.*;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

/**
 * Fraction of the surface area that is polar, based on topological determination. This is equivalent
 * to {@link VABCDescriptor} / {@link TPSADescriptor}.
 *
 * @cdk.module qsarmolecular
 * @cdk.githash
 *
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:fractionalPSA
 * @cdk.keyword volume
 * @cdk.keyword descriptor
 */
public class FractionalPSADescriptor implements IMolecularDescriptor 
{
	private static boolean kicked=false; // need to work around an init issue

	public FractionalPSADescriptor()
	{
	}
	
    public void initialise(IChemObjectBuilder builder) {}


    /**
     * {@inheritDoc}
     */
    public DescriptorSpecification getSpecification() 
    {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#fractionalPSA",
            this.getClass().getName(),
            "$Id$",
            "The Chemistry Development Kit"
        );
    }

    /** {@inheritDoc} */
    public void setParameters(Object[] params) throws CDKException 
    {
        if (params.length!=0) 
        {
            throw new CDKException("The FractionalPSADescriptor expects zero parameters");
        }
    }

    /** {@inheritDoc} */
    public Object[] getParameters()
    {
        return new Object[0];
    }

    public String[] getDescriptorNames() 
    {
        return new String[]{"FractionalPSA"};
    }

    private DescriptorValue getDummyDescriptorValue(Exception e)
    {
        return new DescriptorValue
        (
        	getSpecification(),
        	getParameterNames(),
            getParameters(),
            new DoubleResult(Double.NaN),
            getDescriptorNames(),
            e
		);
    }

    /**
     * Calculates the descriptor value: topological polar surface area divided by topological surface area.
     * 
     * @param atomContainer The {@link IAtomContainer} whose volume is to be calculated
     * @return A double containing the fraction
     */
    public DescriptorValue calculate(IAtomContainer mol)
    {        
        // hackaround: need to fire up a dummy SMARTS match otherwise the VABC code will bug out with a null pointer;
        // note: the VABCDescriptor class also suffers from the same problem, if it is invoked before the SMARTS system
        // is bootstrapped
        /*if (!kicked)
        {
        	try
        	{
            	SMARTSQueryTool tool=new SMARTSQueryTool("[CC]");
	            if (tool.matches(atomContainer)) {}
	        	kicked=true;
        	}
        	catch (Exception ex) {}
        }*/
        
        double polar=0,volume=0;
        try 
        {
        	// polar surface area: chain it off the TPSADescriptor
        	TPSADescriptor tpsa=new TPSADescriptor();
        	DescriptorValue value=tpsa.calculate(mol);
        	polar=((DoubleResult)value.getValue()).doubleValue();
        	
        	// regular surface area: request it directly
            volume=VABCVolume.calculate(mol);
        }
        catch (CDKException exception) 
        {
            return getDummyDescriptorValue(exception);
        }
        
        return new DescriptorValue
        (
            getSpecification(),
            getParameterNames(),
            getParameters(),
            new DoubleResult(polar/volume),
            getDescriptorNames()
        );
    }

    /** {@inheritDoc} */
    public IDescriptorResult getDescriptorResultType()
    {
        return new DoubleResultType();
    }

    /** {@inheritDoc} */
    public String[] getParameterNames() 
    {
        return new String[0];
    }

    /** {@inheritDoc} */
    public Object getParameterType(String name) 
    {
        return null;
    }
}

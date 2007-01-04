/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 * $Revision: 5855 $
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Classes that implement this interface are QSAR descriptor calculators.
 *
 * @cdk.module qsar
 */
public interface IBondDescriptor extends IDescriptor {
    
    /** 
     * Calculates the descriptor value for the given IBond.
     *
     * @param  bond         A {@link IBond} for which this descriptor
     *                      should be calculated
     * @return              An object of {@link DescriptorValue} that contain the 
     *                      calculated value as well as specification details
     * @throws CDKException if an error occurs during calculation. See 
     *                      documentation for individual descriptors
     */
    public DescriptorValue calculate(IBond bond, IAtomContainer container) throws CDKException;
    
}


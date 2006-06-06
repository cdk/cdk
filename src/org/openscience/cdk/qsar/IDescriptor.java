/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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

/**
 * Classes that implement this interface are QSAR descriptor calculators.
 *
 * @cdk.module qsar
 */
public interface IDescriptor {

   /**
   * Returns a <code>Map</code> which specifies which descriptor
   * is implemented by this class. 
   *
   * These fields are used in the map:
   * <ul>
   * <li>Specification-Reference: refers to an entry in a unique dictionary
   * <li>Implementation-Title: anything
   * <li>Implementation-Identifier: a unique identifier for this version of
   *  this class
   * <li>Implementation-Vendor: CDK, JOELib, or anything else
   * </ul>
   *
   * @return An object containing the descriptor specification
   */
    public DescriptorSpecification getSpecification();
    
    /** 
     * Returns the names of the parameters for this descriptor. 
     *
     * @return An array of String containing the names of the paraneters 
     * that this descriptor can accept
     */
    public String[] getParameterNames();
    /** 
     * Returns a class matching that of the parameter with the given name.
     *
     * @param name The name of the parameter whose type is requested
     * @return An Object of the class corresponding to the parameter with the supplied name
     */
    public Object getParameterType(String name);
    
    /** 
     * Sets the parameters for this descriptor. 
     *
     * Must be done before calling
     * calculate as the parameters influence the calculation outcome.
     *
     * @param params An array of Object containing the parameters for this descriptor
     * @throws CDKException if invalid number of type of parameters are passed to it
     * @see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException;
    
    /** 
     * Returns the current parameter values.
     *
     * @return An array of Object containing the parameter values
     * @see #setParameters
     * */
    public Object[] getParameters();
    
}


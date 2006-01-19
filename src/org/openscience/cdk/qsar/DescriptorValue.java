/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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


import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * Class that is used to store descriptor values as IChemObject properties.
 *
 * @cdk.module standard
 */
public class DescriptorValue {

    private DescriptorSpecification specification;
    private String[] parameterNames;
    private Object[] parameterSettings;
    private IDescriptorResult value;
    
    public DescriptorValue(DescriptorSpecification specification,
                           String[] parameterNames,
                           Object[] parameterSettings,
                           IDescriptorResult value) {
        this.specification = specification;
        this.parameterNames = parameterNames;
        this.parameterSettings = parameterSettings;
        this.value = value;
    }
    
    public DescriptorSpecification getSpecification() {
        return this.specification;
    };
    
    public Object[] getParameters() {
        return this.parameterSettings;
    };
    
    public String[] getParameterNames() {
        return this.parameterNames;
    };
    
    public IDescriptorResult getValue() {
        return this.value;
    }
    
}


/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.qsar.result;

/**
 * Object that provides access to the calculated descriptor value.
 * The concept was taken from JOELib.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
public interface IDescriptorResult {

    /**
     * String representation of the result.
     *
     * @return A string representation.
     */
    public String toString();
    
    /**
     * Returns the length of this descriptor.
     * 
     * @return the length of the array of return values.
     */
    public int length();

}


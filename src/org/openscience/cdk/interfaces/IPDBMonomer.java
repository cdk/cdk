/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-08-14 21:51:36 +0200 (Mon, 14 Aug 2006) $
 * $Revision: 6785 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.interfaces;

/**
 * Represents the idea of an chemical Monomer.
 *
 * @cdk.module  interfaces
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword pdbpolymer
 */
public interface IPDBMonomer extends IMonomer {
	
	/**
     * set the I code of this Monomer.
     * 
     * @param newICode  the I code of this Monomer
     */
	public void setICode(String newICode);
	
	/**
     * get the I code of this Monomer.
     * 
     * @return the I code of this Monomer
     */
    public String getICode();
    
    /**
     * set the Chain ID of this Monomer.
     * 
     * @param newChainID  the Chain ID of this Monomer
     */
    public void setChainID(String newChainID);
    
    /**
     * get the Chain ID of this Monomer.
     * 
     * @return the Chain ID of this Monomer
     */
    public String getChainID();
	
}






/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 * Represents the idea of an protein monomer as found in PDB files.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword pdbpolymer
 */
public interface IPDBMonomer extends IMonomer {
	
	/**
     * Sets the ICode of this monomer.
     * 
     * @param newICode  the I code of this monomer
     */
	public void setICode(String newICode);
	
	/**
     * Gets the ICode of this monomer.
     * 
     * @return the ICode of this monomer
     */
    public String getICode();
    
    /**
     * Sets the Chain ID of this monomer.
     * 
     * @param newChainID  the Chain ID of this monomer
     */
    public void setChainID(String newChainID);
    
    /**
     * Gets the Chain ID of this monomer.
     * 
     * @return the Chain ID of this monomer
     */
    public String getChainID();
	
    /**
     * Gets the sequence identifier of this monomer.
     * 
     * @return  the sequence identifier of this monomer
     */
    public String getResSeq();

    /**
     * Sets the sequence identifier of this monomer.
     * 
     * @param newResSeq  the new sequence identifier of this monomer
     */
    public void setResSeq(String newResSeq);

}






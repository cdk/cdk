/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 *  Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
 * Represents the idea of an chemical structure.
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword pdbpolymer
 */
public interface IPDBStructure{
	
	/**
     * get the ending Chain identifier of this structure.
     * 
     * @return the ending Chain identifier of this structure
     */
    public Character getEndChainID();
    /**
     * set the ending Chain identifier of this structure.
     * 
     * @param endChainID  the ending Chain identifier of this structure
     */
    public void setEndChainID(Character endChainID);
    /**
     * get the ending Code for insertion of residues of this structure.
     * 
     * @return the ending Code for insertion of residues of this structure
     */
    public Character getEndInsertionCode();
    /**
     * set the ending Code for insertion of residues of this structure.
     * 
     * @param endInsertionCode  the ending Code for insertion of residues of this structure
     */
    public void setEndInsertionCode(Character endInsertionCode);
    /**
     * get the ending sequence number of this structure.
     * 
     * @return the ending sequence number of this structure
     */
    public Integer getEndSequenceNumber();
    /**
     * set the ending sequence number of this structure.
     * 
     * @param endSequenceNumber  the ending sequence number of this structure
     */
    public void setEndSequenceNumber(Integer endSequenceNumber);
    /**
     * get start Chain identifier of this structure.
     * 
     * @return the start Chain identifier of this structure
     */
    public Character getStartChainID();
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startChainID  the start Chain identifier of this structure
     */
    public void setStartChainID(Character startChainID);
    /**
     * get start Code for insertion of residues of this structure.
     * 
     * @return the start Code for insertion of residues of this structure
     */
    public Character getStartInsertionCode();
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startInsertionCode  the start Chain identifier of this structure
     */
    public void setStartInsertionCode(Character startInsertionCode);
    /**
     * get the start sequence number of this structure.
     * 
     * @return the start sequence number of this structure
     */
    public Integer getStartSequenceNumber();
    /**
     * set the start sequence number of this structure.
     * 
     * @param startSequenceNumber  the start sequence number of this structure
     */
    public void setStartSequenceNumber(Integer startSequenceNumber);
    /**
     * get Structure Type of this structure.
     * 
     * @return the Structure Type of this structure
     */
    public String getStructureType();
    /**
     * set the Structure Type of this structure.
     * 
     * @param structureType  the Structure Type of this structure
     */
    public void setStructureType(String structureType);
    
	
}






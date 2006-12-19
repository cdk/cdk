/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-08-14 21:51:36 +0200 (Mon, 14 Aug 2006) $
 * $Revision: 6785 $
 *
 *  Copyright (C) 2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
    public char getEndChainID();
    /**
     * set the ending Chain identifier of this structure.
     * 
     * @param endChainID  the ending Chain identifier of this structure
     */
    public void setEndChainID(char endChainID);
    /**
     * get the ending Code for insertion of residues of this structure.
     * 
     * @return the ending Code for insertion of residues of this structure
     */
    public char getEndInsertionCode();
    /**
     * set the ending Code for insertion of residues of this structure.
     * 
     * @param endInsertionCode  the ending Code for insertion of residues of this structure
     */
    public void setEndInsertionCode(char endInsertionCode);
    /**
     * get the ending sequence number of this structure.
     * 
     * @return the ending sequence number of this structure
     */
    public int getEndSequenceNumber();
    /**
     * set the ending sequence number of this structure.
     * 
     * @param endSequenceNumber  the ending sequence number of this structure
     */
    public void setEndSequenceNumber(int endSequenceNumber);
    /**
     * get start Chain identifier of this structure.
     * 
     * @return the start Chain identifier of this structure
     */
    public char getStartChainID();
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startChainID  the start Chain identifier of this structure
     */
    public void setStartChainID(char startChainID);
    /**
     * get start Code for insertion of residues of this structure.
     * 
     * @return the start Code for insertion of residues of this structure
     */
    public char getStartInsertionCode();
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startInsertionCode  the start Chain identifier of this structure
     */
    public void setStartInsertionCode(char startInsertionCode);
    /**
     * get the start sequence number of this structure.
     * 
     * @return the start sequence number of this structure
     */
    public int getStartSequenceNumber();
    /**
     * set the start sequence number of this structure.
     * 
     * @param endSequenceNumber  the start sequence number of this structure
     */
    public void setStartSequenceNumber(int startSequenceNumber);
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






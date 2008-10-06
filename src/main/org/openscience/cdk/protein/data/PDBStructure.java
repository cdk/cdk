/* $RCSfile: $
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.protein.data;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IPDBStructure;

/**
 * Holder for secundary protein structure elements. Lously modeled after
 * the Jmol Structure.java.
 * 
 * @author     egonw
 * 
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 */
public class PDBStructure extends ChemObject implements IPDBStructure {
	
	private static final long serialVersionUID = -1877529009319324448L;
	
	public final static String HELIX = "helix";
	public final static String SHEET = "sheet";
	public final static String TURN = "turn";
	
    private String structureType;
    private Character startChainID;
    private Integer startSequenceNumber;
    private Character startInsertionCode;
    private Character endChainID;
    private Integer endSequenceNumber;
    private Character endInsertionCode;
    
    /**
     * get the ending Chain identifier of this structure.
     * 
     * @return the ending Chain identifier of this structure
     */
    public Character getEndChainID() {
    	return endChainID;
    }
    /**
     * set the ending Chain identifier of this structure.
     * 
     * @param endChainID  the ending Chain identifier of this structure
     */
    public void setEndChainID(Character endChainID) {
    	this.endChainID = endChainID;
    }
    /**
     * get the ending Code for insertion of residues of this structure.
     * 
     * @return the ending Code for insertion of residues of this structure
     */
    public Character getEndInsertionCode() {
    	return endInsertionCode;
    }
    /**
     * set the ending Code for insertion of residues of this structure.
     * 
     * @param endInsertionCode  the ending Code for insertion of residues of this structure
     */
    public void setEndInsertionCode(Character endInsertionCode) {
    	this.endInsertionCode = endInsertionCode;
    }
    /**
     * get the ending sequence number of this structure.
     * 
     * @return the ending sequence number of this structure
     */
    public Integer getEndSequenceNumber() {
    	return endSequenceNumber;
    }
    /**
     * set the ending sequence number of this structure.
     * 
     * @param endSequenceNumber  the ending sequence number of this structure
     */
    public void setEndSequenceNumber(Integer endSequenceNumber) {
    	this.endSequenceNumber = endSequenceNumber;
    }
    /**
     * get start Chain identifier of this structure.
     * 
     * @return the start Chain identifier of this structure
     */
    public Character getStartChainID() {
    	return startChainID;
    }
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startChainID  the start Chain identifier of this structure
     */
    public void setStartChainID(Character startChainID) {
    	this.startChainID = startChainID;
    }
    /**
     * get start Code for insertion of residues of this structure.
     * 
     * @return the start Code for insertion of residues of this structure
     */
    public Character getStartInsertionCode() {
    	return startInsertionCode;
    }
    /**
     * set the start Chain identifier of this structure.
     * 
     * @param startInsertionCode  the start Chain identifier of this structure
     */
    public void setStartInsertionCode(Character startInsertionCode) {
    	this.startInsertionCode = startInsertionCode;
    }
    /**
     * get the start sequence number of this structure.
     * 
     * @return the start sequence number of this structure
     */
    public Integer getStartSequenceNumber() {
    	return startSequenceNumber;
    }
    /**
     * set the start sequence number of this structure.
     * 
     * @param startSequenceNumber  the start sequence number of this structure
     */
    public void setStartSequenceNumber(Integer startSequenceNumber) {
    	this.startSequenceNumber = startSequenceNumber;
    }
    /**
     * get Structure Type of this structure.
     * 
     * @return the Structure Type of this structure
     */
    public String getStructureType() {
    	return structureType;
    }
    /**
     * set the Structure Type of this structure.
     * 
     * @param structureType  the Structure Type of this structure
     */
    public void setStructureType(String structureType) {
    	this.structureType = structureType;
    }
}

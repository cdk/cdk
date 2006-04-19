/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2005-11-10 16:52:44 +0100 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
 *
 * Copyright (C) 2006  The Chemistry Development Kit (CDK) project
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

/**
 * Holder for secundary protein structure elements. Lously modeled after
 * the Jmol Structure.java.
 * 
 * @author     egonw
 * 
 * @cdk.module pdb
 */
public class PDBStructure {
	
	public final static String HELIX = "helix";
	public final static String SHEET = "sheet";
	public final static String TURN = "turn";
	
    private String structureType;
    private char startChainID;
    private int startSequenceNumber;
    private char startInsertionCode;
    private char endChainID;
    private int endSequenceNumber;
    private char endInsertionCode;
    
    public char getEndChainID() {
    	return endChainID;
    }
    
    public void setEndChainID(char endChainID) {
    	this.endChainID = endChainID;
    }
    
    public char getEndInsertionCode() {
    	return endInsertionCode;
    }
    
    public void setEndInsertionCode(char endInsertionCode) {
    	this.endInsertionCode = endInsertionCode;
    }
    
    public int getEndSequenceNumber() {
    	return endSequenceNumber;
    }
    
    public void setEndSequenceNumber(int endSequenceNumber) {
    	this.endSequenceNumber = endSequenceNumber;
    }
    
    public char getStartChainID() {
    	return startChainID;
    }
    
    public void setStartChainID(char startChainID) {
    	this.startChainID = startChainID;
    }
    
    public char getStartInsertionCode() {
    	return startInsertionCode;
    }
    
    public void setStartInsertionCode(char startInsertionCode) {
    	this.startInsertionCode = startInsertionCode;
    }
    
    public int getStartSequenceNumber() {
    	return startSequenceNumber;
    }
    
    public void setStartSequenceNumber(int startSequenceNumber) {
    	this.startSequenceNumber = startSequenceNumber;
    }
    
    public String getStructureType() {
    	return structureType;
    }
    
    public void setStructureType(String structureType) {
    	this.structureType = structureType;
    }
}

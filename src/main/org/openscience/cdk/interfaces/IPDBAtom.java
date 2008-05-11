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
 * A PDBAtom is a subclass of a Atom which is supposed to store
 * additional informations about the Atom
 *
 * @cdk.module  interfaces
 * @cdk.svnrev  $Revision$
 *
 * @author      Miguel Rojas <miguel.rojas@uni-koeln.de>
 * @cdk.created 2006-11-20 
 *
 * @cdk.keyword pdbpolymer
 */
public interface IPDBAtom extends IAtom {
    /**
     * get one entire line from the PDB entry file which describe the IPDBAtom. 
     * It consists of 80 columns. 
     * 
     * @return a String with all information
     */
	public String getRecord();
    
	/**
     * set one entire line from the PDB entry file which describe the IPDBAtom. 
     * It consists of 80 columns. 
	 * 
	 * @param newRecord A String with all information
	 */
    public void setRecord(String newRecord);
    /**
     * get the Temperature factor of this atom.
     * 
     * @return the Temperature factor of this atom
     */
    public Double getTempFactor();
    /**
     * set the Temperature factor of this atom.
     * 
     * @param newTempFactor  the Temperature factor of this atom
     */
    public void setTempFactor(Double newTempFactor);
    /**
     * get the Residue name of this atom.
     * 
     * @return the Residue name of this atom
     */
    public String getResName();
    /**
     * set the Residue name of this atom.
     * 
     * @param newResName  the Residue name of this atom
     */
    public void setResName(String newResName);
    
    /**
     * get Code for insertion of residues of this atom.
     * 
     * @return the Code for insertion of residues of this atom
     */
    public String getICode();
    
    /**
     * set the Code for insertion of residues of this atom.
     * 
     * @param newICode  the Code for insertion of residues of this atom
     */
    public void setICode(String newICode);
    
    /**
     * get the Atom name of this atom.
     * 
     * @return the Atom name of this atom
     */
    public String getName();
    /**
     * set the Atom name of this atom.
     * 
     * @param newName  the Atom name of this atom
     */
    public void setName(String newName);
    
    /**
     * get the Chain identifier of this atom.
     * 
     * @return the Chain identifier of this atom
     */
    public String getChainID();
    /**
     * set the Chain identifier of this atom.
     * 
     * @param newChainID  the Chain identifier of this atom
     */
    public void setChainID(String newChainID);
    
    /**
     * get the Alternate location indicator of this atom.
     * 
     * @return the Alternate location indicator of this atom
     */
    public String getAltLoc();
    /**
     * set the Alternate location indicator of this atom.
     * 
     * @param newAltLoc  the Alternate location indicator of this atom
     */
    public void setAltLoc(String newAltLoc);
    
    /**
     * get the Segment identifier, left-justified of this atom.
     * 
     * @return the Segment identifier, left-justified of this atom
     */
    public String getSegID();
    /**
     * set the Segment identifier, left-justified of this atom.
     * 
     * @param newSegID  the Segment identifier, left-justified of this atom
     */
    public void setSegID(String newSegID);
    
    /**
     * get the Atom serial number of this atom.
     * 
     * @return the Atom serial number of this atom
     */
    public Integer getSerial();
    /**
     * set the Atom serial number of this atom.
     * 
     * @param newSerial  the Atom serial number of this atom
     */
    public void setSerial(Integer newSerial);
    
    /**
     * get the Residue sequence number of this atom.
     * 
     * @return the Residue sequence number of this atom
     */
    public String getResSeq();
    /**
     * set the Residue sequence number of this atom.
     * 
     * @param newResSeq  the Residue sequence number of this atom
     */
    public void setResSeq(String newResSeq);
    
    
    public Boolean getOxt();
    public void setOxt(Boolean newOxt);
    

    public Boolean getHetAtom();
    public void setHetAtom(Boolean newHetAtom);
    
    /**
     * get the Occupancy of this atom.
     * 
     * @return the Occupancy of this atom
     */
    public Double getOccupancy();
    /**
     * set the Occupancy of this atom.
     * 
     * @param newOccupancy  the Occupancy of this atom
     */
    public void setOccupancy(Double newOccupancy);
    
	
}






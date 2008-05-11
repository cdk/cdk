/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.debug;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     Miguel Rojas
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugPDBAtom extends PDBAtom implements IPDBAtom {


	private static final long serialVersionUID = -2432127382224382452L;

	LoggingTool logger = new LoggingTool(DebugPDBAtom.class);
	
	public DebugPDBAtom(IElement element) {
		super(element);
		logger.debug("Instantiated a DebugPDBAtom: element= ", element);
	}
	
	public DebugPDBAtom(String symbol) {
		super(symbol);
		logger.debug("Instantiated a DebugPDBAtom: symbol= ", symbol);
	}
	
	public DebugPDBAtom(String symbol, Point3d point3d) {
		super(symbol, point3d);
		logger.debug("Instantiated a DebugAtom: symbol= ", symbol + " point3d=" + point3d);
	}

	public String getRecord() {
		logger.debug("Getting Record: ", super.getRecord());
		return super.getRecord();
    }

	public void setRecord(String newRecord) {
		logger.debug("Setting Record: ", newRecord);
		super.setRecord(newRecord);
    }

	public Double getTempFactor() {
		logger.debug("Getting Temp Factor: ", super.getTempFactor());
		return super.getTempFactor();
    }

	public void setTempFactor(Double newTempFactor) {
		logger.debug("Setting Temp Factor: ", newTempFactor);
		super.setTempFactor(newTempFactor);
    }

	public void setResName(String newResName) {
		logger.debug("Setting Res Name: ", newResName);
		super.setResName(newResName);
    }

	public String getResName() {
		logger.debug("Getting Res Name: ", super.getResName());
		return super.getResName();
    }

	public void setICode(String newICode) {
		logger.debug("Setting I Code: ", newICode);
		super.setICode(newICode);
    }

	public String getICode() {
		logger.debug("Getting I Code: ", super.getICode());
		return super.getICode();
    }

	public void setName(String newName) {
		logger.debug("Setting Name: ", newName);
		super.setName(newName);
    }

	public String getName() {
		logger.debug("Getting Name: ", super.getName());
		return super.getName();
    }

	public void setChainID(String newChainID) {
		logger.debug("Setting Chain ID: ", newChainID);
		super.setChainID(newChainID);
    }

	public String getChainID() {
		logger.debug("Getting Chain ID: ", super.getChainID());
		return super.getChainID();
    }

	public void setAltLoc(String newAltLoc) {
		logger.debug("Setting Alt Loc: ", newAltLoc);
		super.setAltLoc(newAltLoc);
    }

	public String getAltLoc() {
		logger.debug("Getting Alt Loc: ", super.getAltLoc());
		return super.getAltLoc();
    }

	public void setSegID(String newSegID) {
		logger.debug("Setting SegID: ", newSegID);
		super.setSegID(newSegID);
    }

	public String getSegID() {
		logger.debug("Getting Seg ID: ", super.getSegID());
		return super.getSegID();
    }

	public void setSerial(Integer newSerial) {
		logger.debug("Setting Serial: ", newSerial);
		super.setSerial(newSerial);
    }

	public Integer getSerial() {
		logger.debug("Getting Serial: ", super.getSerial());
		return super.getSerial();
    }

	public void setResSeq(String newResSeq) {
		logger.debug("Setting Res Seq: ", newResSeq);
		super.setResSeq(newResSeq);
    }

	public String getResSeq() {
		logger.debug("Getting Res Seq: ", super.getResSeq());
		return super.getResSeq();
    }
    
    public void setOxt(Boolean newOxt) {
		logger.debug("Setting Oxt: ", newOxt);
		super.setOxt(newOxt);
    }
    
    public Boolean getOxt() {
		logger.debug("Getting Oxt: ", super.getOxt());
		return super.getOxt();
    }
    
    public void setHetAtom(Boolean newHetAtom) {
		logger.debug("Setting Het Atom: ", newHetAtom);
		super.setHetAtom(newHetAtom);
    }
    
    public Boolean getHetAtom() {
		logger.debug("Getting Het Atom: ", super.getHetAtom());
		return super.getHetAtom();
    }

    public void setOccupancy(Double newOccupancy) {
		logger.debug("Setting Occupancy: ", newOccupancy);
		super.setOccupancy(newOccupancy);
    }

    public Double getOccupancy() {
		logger.debug("Getting Occupancy: ", super.getOccupancy());
		return super.getOccupancy();
    }
}

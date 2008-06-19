/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugPseudoAtom extends org.openscience.cdk.PseudoAtom 
    implements IPseudoAtom {

    private static final long serialVersionUID = -5935090219383862070L;
    
    LoggingTool logger = new LoggingTool(DebugPseudoAtom.class);
	
	public DebugPseudoAtom() {
		super();
		logger.debug("Instantiated a DebugAtom");
	}
	
	public DebugPseudoAtom(IElement element) {
		super(element);
		logger.debug("Instantiated a DebugAtom: element= ", element);
	}

	public DebugPseudoAtom(String symbol) {
		super(symbol);
		logger.debug("Instantiated a DebugAtom: symbol= ", symbol);
	}
	
	public DebugPseudoAtom(String symbol, Point2d point2d) {
		super(symbol, point2d);
		logger.debug("Instantiated a DebugAtom: symbol= ", symbol + " point2d=" + point2d);
	}
	
	public DebugPseudoAtom(String symbol, Point3d point3d) {
		super(symbol, point3d);
		logger.debug("Instantiated a DebugAtom: symbol= ", symbol + " point3d=" + point3d);
	}
	
	public void setCharge(Double charge) {
		logger.debug("Setting charge: ", charge);
		super.setCharge(charge);
	}

	public Double getCharge() {
		logger.debug("Setting charge: ", super.getCharge());
		return super.getCharge();
	}

	public void setHydrogenCount(Integer hydrogenCount) {
		logger.debug("Setting hydrogen count: ", hydrogenCount);
		super.setHydrogenCount(hydrogenCount);
	}

	public Integer getHydrogenCount() {
		logger.debug("Getting hydrogen count: ", super.getHydrogenCount());
		return super.getHydrogenCount();
	}

	public void setPoint2d(Point2d point2d) {
		logger.debug("Setting point2d: x=" + point2d.x + 
				     ", y=" + point2d.y);
		super.setPoint2d(point2d);
	}

	public void setPoint3d(Point3d point3d) {
		logger.debug("Setting point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		super.setPoint3d(point3d);
	}

	public void setFractionalPoint3d(Point3d point3d) {
		logger.debug("Setting fractional point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		super.setFractionalPoint3d(point3d);
	}

	public void setStereoParity(Integer stereoParity) {
		logger.debug("Setting stereoParity: ", stereoParity);
		super.setStereoParity(stereoParity);
	}

	public Point2d getPoint2d() {
		Point2d point2d = super.getPoint2d();
		if (point2d == null) {
			logger.debug("Getting point2d: null");
		} else {
			logger.debug("Getting point2d: x=" + point2d.x + 
			     ", y=" + point2d.y);
		}
		return point2d;
	}

	public Point3d getPoint3d() {
		Point3d point3d = super.getPoint3d();
		if (point3d == null) {
			logger.debug("Getting point3d: null");
		} else {
			logger.debug("Getting point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		}
		return point3d;
	}

	public Point3d getFractionalPoint3d() {
		Point3d point3d = super.getFractionalPoint3d();
		if (point3d == null) {
			logger.debug("Getting fractional point3d: null");
		} else {
			logger.debug("Getting fractional point3d: x=" + point3d.x + 
			     ", y=" + point3d.y, ", z=" + point3d.z);
		}
		return point3d;
	}

	public Integer getStereoParity() {
		logger.debug("Getting stereo parity: ", super.getStereoParity());
		return super.getStereoParity();
	}

	public void setAtomTypeName(String identifier) {
		logger.debug("Setting atom type name: ", identifier);
		super.setAtomTypeName(identifier);
	}

	public void setMaxBondOrder(IBond.Order maxBondOrder) {
		logger.debug("Setting max bond order: ", maxBondOrder);
		super.setMaxBondOrder(maxBondOrder);
	}

	public void setBondOrderSum(Double bondOrderSum) {
		logger.debug("Setting bond order sum: ", bondOrderSum);
		super.setBondOrderSum(bondOrderSum);
	}

	public String getAtomTypeName() {
		logger.debug("Getting atom type name: ", super.getAtomTypeName());
		return super.getAtomTypeName();
	}

	public IBond.Order getMaxBondOrder() {
		logger.debug("Getting max bond order: ", super.getMaxBondOrder());
		return super.getMaxBondOrder();
	}

	public Double getBondOrderSum() {
		logger.debug("Getting bond order sum: ", super.getBondOrderSum());
		return super.getBondOrderSum();
	}

	public void setFormalCharge(Integer charge) {
		logger.debug("Setting formal charge: ", charge);
		super.setFormalCharge(charge);
	}

	public Integer getFormalCharge() {
		logger.debug("Getting formal charge: ", super.getFormalCharge());
		return super.getFormalCharge();
	}

	public void setFormalNeighbourCount(int count) {
		logger.debug("Setting forml neighbour count: ", count);
		super.setFormalNeighbourCount(count);
	}

	public Integer getFormalNeighbourCount() {
		logger.debug("Getting formal neighbour count: ", super.getFormalNeighbourCount());
		return super.getFormalNeighbourCount();
	}

	public void setHybridization(IAtomType.Hybridization hybridization) {
		logger.debug("Setting hybridization: ", hybridization);
		super.setHybridization(hybridization);
	}

	public IAtomType.Hybridization getHybridization() {
		logger.debug("Getting hybridization: ", super.getHybridization());
		return super.getHybridization();
	}

	public void setCovalentRadius(double radius) {
		logger.debug("Setting covalent radius: ", radius);
		super.setCovalentRadius(radius);
	}

	public Double getCovalentRadius() {
		logger.debug("Getting covalent radius: ", super.getCovalentRadius());
		return super.getCovalentRadius();
	}

	public void setValency(int valency) {
		logger.debug("Setting valency: ", valency);
		super.setValency(valency);
	}

	public Integer getValency() {
		logger.debug("Getting valency: ", super.getValency());
		return super.getValency();
	}

	public void setNaturalAbundance(Double naturalAbundance) {
		logger.debug("Setting natural abundance: ", naturalAbundance);
		super.setNaturalAbundance(naturalAbundance);
	}

	public void setExactMass(Double exactMass) {
		logger.debug("Setting exact mass: ", exactMass);
		super.setExactMass(exactMass);
		
	}

	public Double getNaturalAbundance() {
		logger.debug("Getting natural abundance: ", super.getNaturalAbundance());
		return super.getNaturalAbundance();
	}

	public Double getExactMass() {
		logger.debug("Getting exact mass: ", super.getExactMass());
		return super.getExactMass();
	}

	public Integer getMassNumber() {
		logger.debug("Getting mass number: ", super.getMassNumber());
		return super.getMassNumber();
	}

	public void setMassNumber(int massNumber) {
		logger.debug("Setting mass number: ", massNumber);
		super.setMassNumber(massNumber);
	}

	public Integer getAtomicNumber() {
		logger.debug("Getting atomic number: ", super.getAtomicNumber());
		return super.getAtomicNumber();
	}

	public void setAtomicNumber(Integer atomicNumber) {
		logger.debug("Setting atomic number: ", atomicNumber);
		super.setAtomicNumber(atomicNumber);
	}

	public String getSymbol() {
		logger.debug("Getting symbol: ", super.getSymbol());
		return super.getSymbol();
	}

	public void setSymbol(String symbol) {
		logger.debug("Setting symbol: ", symbol);
		super.setSymbol(symbol);
	}

	public void addListener(IChemObjectListener col) {
		logger.debug("Adding listener: ", col);
		super.addListener(col);
	}

	public int getListenerCount() {
		logger.debug("Getting listener count: ", super.getListenerCount());
		return super.getListenerCount();
	}

	public void removeListener(IChemObjectListener col) {
		logger.debug("Removing listener: ", col);
		super.removeListener(col);
	}

	public void notifyChanged() {
		logger.debug("Notifying changed");
		super.notifyChanged();
	}

	public void notifyChanged(IChemObjectChangeEvent evt) {
		logger.debug("Notifying changed event: ", evt);
		super.notifyChanged(evt);
	}

	public void setProperty(Object description, Object property) {
		logger.debug("Setting property: ", description + "=" + property);
		super.setProperty(description, property);
	}

	public void removeProperty(Object description) {
		logger.debug("Removing property: ", description);
		super.removeProperty(description);
	}

	public Object getProperty(Object description) {
		logger.debug("Getting property: ", description + "=" + super.getProperty(description));
		return super.getProperty(description);
	}

	public Map<Object,Object> getProperties() {
		logger.debug("Getting properties");
		return super.getProperties();
	}

	public String getID() {
		logger.debug("Getting ID: ", super.getID());
		return super.getID();
	}

	public void setID(String identifier) {
		logger.debug("Setting ID: ", identifier);
		super.setID(identifier);
	}

	public void setFlag(int flag_type, boolean flag_value) {
		logger.debug("Setting flag: ", flag_type + "=" + flag_value);
		super.setFlag(flag_type, flag_value);
	}

	public boolean getFlag(int flag_type) {
		logger.debug("Setting flag: ", flag_type + "=" + super.getFlag(flag_type));
		return super.getFlag(flag_type);
	}

	public void setProperties(Map<Object,Object> properties) {
		logger.debug("Setting properties: ", properties);
		super.setProperties(properties);
	}

	public void setFlags(boolean[] flagsNew) {
		logger.debug("Setting flags:", flagsNew.length);
		super.setFlags(flagsNew);
	}

	public boolean[] getFlags() {
		logger.debug("Getting flags:", super.getFlags().length);
		return super.getFlags();
	}

	public Object clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
        	clone = super.clone();
        } catch (Exception exception) {
        	logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
        	logger.debug(exception);
        }
        return clone;
	}

	public IChemObjectBuilder getBuilder() {
		return DebugChemObjectBuilder.getInstance();
	}

	public String getLabel() {
		logger.debug("Getting label: ", super.getLabel());
		return super.getLabel();
	}

	public void setLabel(String label) {
		logger.debug("Setting label: ", label);
		super.setLabel(label);
	}

}

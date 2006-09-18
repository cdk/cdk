/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2006  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 */
public class DebugAtomType extends org.openscience.cdk.AtomType
    implements IAtomType {

    private static final long serialVersionUID = 1427549696666679540L;
    
    LoggingTool logger = new LoggingTool(DebugAtomType.class);

	public DebugAtomType(String elementSymbol) {
		super(elementSymbol);
		logger.debug("Instantiated a DebugAtomType: symbol= ", elementSymbol);
	}

	public DebugAtomType(String identifier, String elementSymbol) {
		super(elementSymbol); // cannot use super(identifier, elementSymbol); that gives a NPE
		logger.debug("Instantiated a DebugAtomType: identifier= " + identifier + " symbol= ", elementSymbol);
		this.setSymbol(elementSymbol);
		this.setAtomTypeName(identifier);
	}
	
	public int getAtomicNumber() {
		logger.debug("Getting atomic number: ", super.getAtomicNumber());
		return super.getAtomicNumber();
	}

	public void setAtomicNumber(int atomicNumber) {
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

	public Hashtable getProperties() {
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

	public void setProperties(Hashtable properties) {
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

	public void setNaturalAbundance(double naturalAbundance) {
		logger.debug("Setting natural abundance: ", naturalAbundance);
		super.setNaturalAbundance(naturalAbundance);
	}

	public void setExactMass(double exactMass) {
		logger.debug("Setting exact mass: ", exactMass);
		super.setExactMass(exactMass);
		
	}

	public double getNaturalAbundance() {
		logger.debug("Getting natural abundance: ", super.getNaturalAbundance());
		return super.getNaturalAbundance();
	}

	public double getExactMass() {
		logger.debug("Getting exact mass: ", super.getExactMass());
		return super.getExactMass();
	}

	public int getMassNumber() {
		logger.debug("Getting mass number: ", super.getMassNumber());
		return super.getMassNumber();
	}

	public void setMassNumber(int massNumber) {
		logger.debug("Setting mass number: ", massNumber);
		super.setMassNumber(massNumber);
	}

	public void setAtomTypeName(String identifier) {
		logger.debug("Setting atom type name: ", identifier);
		super.setAtomTypeName(identifier);
	}

	public void setMaxBondOrder(double maxBondOrder) {
		logger.debug("Setting max bond order: ", maxBondOrder);
		super.setMaxBondOrder(maxBondOrder);
	}

	public void setBondOrderSum(double bondOrderSum) {
		logger.debug("Setting bond order sum: ", bondOrderSum);
		super.setBondOrderSum(bondOrderSum);
	}

	public String getAtomTypeName() {
		logger.debug("Getting atom type name: ", super.getAtomTypeName());
		return super.getAtomTypeName();
	}

	public double getMaxBondOrder() {
		logger.debug("Getting max bond order: ", super.getMaxBondOrder());
		return super.getMaxBondOrder();
	}

	public double getBondOrderSum() {
		logger.debug("Getting bond order sum: ", super.getBondOrderSum());
		return super.getBondOrderSum();
	}

	public void setFormalCharge(int charge) {
		logger.debug("Setting formal charge: ", charge);
		super.setFormalCharge(charge);
	}

	public int getFormalCharge() {
		logger.debug("Getting formal charge: ", super.getFormalCharge());
		return super.getFormalCharge();
	}

	public void setFormalNeighbourCount(int count) {
		logger.debug("Setting forml neighbour count: ", count);
		super.setFormalNeighbourCount(count);
	}

	public int getFormalNeighbourCount() {
		logger.debug("Getting formal neighbour count: ", super.getFormalNeighbourCount());
		return super.getFormalNeighbourCount();
	}

	public void setHybridization(int hybridization) {
		logger.debug("Setting hybridization: ", hybridization);
		super.setHybridization(hybridization);
	}

	public int getHybridization() {
		logger.debug("Getting hybridization: ", super.getHybridization());
		return super.getHybridization();
	}

	public void setVanderwaalsRadius(double radius) {
		logger.debug("Setting vanderwaals radius: ", radius);
		super.setVanderwaalsRadius(radius);
	}

	public double getVanderwaalsRadius() {
		logger.debug("Getting vanderwaals radius: ", super.getVanderwaalsRadius());
		return super.getVanderwaalsRadius();
	}

	public void setCovalentRadius(double radius) {
		logger.debug("Setting covalent radius: ", radius);
		super.setCovalentRadius(radius);
	}

	public double getCovalentRadius() {
		logger.debug("Getting covalent radius: ", super.getCovalentRadius());
		return super.getCovalentRadius();
	}

	public void setValency(int valency) {
		logger.debug("Setting valency: ", valency);
		super.setValency(valency);
	}

	public int getValency() {
		logger.debug("Getting valency: ", super.getValency());
		return super.getValency();
	}


}

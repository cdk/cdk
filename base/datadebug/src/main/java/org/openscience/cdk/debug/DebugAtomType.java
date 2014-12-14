/* Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.AtomType;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugAtomType extends AtomType implements IAtomType {

    private static final long serialVersionUID = 1427549696666679540L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugAtomType.class);

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

    public DebugAtomType(IElement element) {
        super(element);
        logger.debug("Instantiated a DebugAtomType: element= ", element);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getAtomicNumber() {
        logger.debug("Getting atomic number: ", super.getAtomicNumber());
        return super.getAtomicNumber();
    }

    /** {@inheritDoc} */
    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        logger.debug("Setting atomic number: ", atomicNumber);
        super.setAtomicNumber(atomicNumber);
    }

    /** {@inheritDoc} */
    @Override
    public String getSymbol() {
        logger.debug("Getting symbol: ", super.getSymbol());
        return super.getSymbol();
    }

    /** {@inheritDoc} */
    @Override
    public void setSymbol(String symbol) {
        logger.debug("Setting symbol: ", symbol);
        super.setSymbol(symbol);
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(IChemObjectListener col) {
        logger.debug("Adding listener: ", col);
        super.addListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public int getListenerCount() {
        logger.debug("Getting listener count: ", super.getListenerCount());
        return super.getListenerCount();
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(IChemObjectListener col) {
        logger.debug("Removing listener: ", col);
        super.removeListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged() {
        logger.debug("Notifying changed");
        super.notifyChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        logger.debug("Notifying changed event: ", evt);
        super.notifyChanged(evt);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        logger.debug("Setting property: ", description + "=" + property);
        super.setProperty(description, property);
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        logger.debug("Removing property: ", description);
        super.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        logger.debug("Getting property: ", description + "=" + super.getProperty(description));
        return super.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        logger.debug("Getting properties");
        return super.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        logger.debug("Getting ID: ", super.getID());
        return super.getID();
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String identifier) {
        logger.debug("Setting ID: ", identifier);
        super.setID(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlag(int flagType, boolean flagValue) {
        logger.debug("Setting flag: ", flagType + "=" + flagValue);
        super.setFlag(flagType, flagValue);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getFlag(int flagType) {
        logger.debug("Setting flag: ", flagType + "=" + super.getFlag(flagType));
        return super.getFlag(flagType);
    }

    /** {@inheritDoc} */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        logger.debug("Setting properties: ", properties);
        super.addProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlags(boolean[] flagsNew) {
        logger.debug("Setting flags:", flagsNew.length);
        super.setFlags(flagsNew);
    }

    /** {@inheritDoc} */
    @Override
    public boolean[] getFlags() {
        logger.debug("Getting flags:", super.getFlags().length);
        return super.getFlags();
    }

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        logger.debug("Setting natural abundance: ", naturalAbundance);
        super.setNaturalAbundance(naturalAbundance);
    }

    /** {@inheritDoc} */
    @Override
    public void setExactMass(Double exactMass) {
        logger.debug("Setting exact mass: ", exactMass);
        super.setExactMass(exactMass);

    }

    /** {@inheritDoc} */
    @Override
    public Double getNaturalAbundance() {
        logger.debug("Getting natural abundance: ", super.getNaturalAbundance());
        return super.getNaturalAbundance();
    }

    /** {@inheritDoc} */
    @Override
    public Double getExactMass() {
        logger.debug("Getting exact mass: ", super.getExactMass());
        return super.getExactMass();
    }

    /** {@inheritDoc} */
    @Override
    public Integer getMassNumber() {
        logger.debug("Getting mass number: ", super.getMassNumber());
        return super.getMassNumber();
    }

    /** {@inheritDoc} */
    @Override
    public void setMassNumber(Integer massNumber) {
        logger.debug("Setting mass number: ", massNumber);
        super.setMassNumber(massNumber);
    }

    /** {@inheritDoc} */
    @Override
    public void setAtomTypeName(String identifier) {
        logger.debug("Setting atom type name: ", identifier);
        super.setAtomTypeName(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxBondOrder(IBond.Order maxBondOrder) {
        logger.debug("Setting max bond order: ", maxBondOrder);
        super.setMaxBondOrder(maxBondOrder);
    }

    /** {@inheritDoc} */
    @Override
    public void setBondOrderSum(Double bondOrderSum) {
        logger.debug("Setting bond order sum: ", bondOrderSum);
        super.setBondOrderSum(bondOrderSum);
    }

    /** {@inheritDoc} */
    @Override
    public String getAtomTypeName() {
        logger.debug("Getting atom type name: ", super.getAtomTypeName());
        return super.getAtomTypeName();
    }

    /** {@inheritDoc} */
    @Override
    public IBond.Order getMaxBondOrder() {
        logger.debug("Getting max bond order: ", super.getMaxBondOrder());
        return super.getMaxBondOrder();
    }

    /** {@inheritDoc} */
    @Override
    public Double getBondOrderSum() {
        logger.debug("Getting bond order sum: ", super.getBondOrderSum());
        return super.getBondOrderSum();
    }

    /** {@inheritDoc} */
    @Override
    public void setFormalCharge(Integer charge) {
        logger.debug("Setting formal charge: ", charge);
        super.setFormalCharge(charge);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getFormalCharge() {
        logger.debug("Getting formal charge: ", super.getFormalCharge());
        return super.getFormalCharge();
    }

    /** {@inheritDoc} */
    @Override
    public void setFormalNeighbourCount(Integer count) {
        logger.debug("Setting forml neighbour count: ", count);
        super.setFormalNeighbourCount(count);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getFormalNeighbourCount() {
        logger.debug("Getting formal neighbour count: ", super.getFormalNeighbourCount());
        return super.getFormalNeighbourCount();
    }

    /** {@inheritDoc} */
    @Override
    public void setHybridization(IAtomType.Hybridization hybridization) {
        logger.debug("Setting hybridization: ", hybridization);
        super.setHybridization(hybridization);
    }

    /** {@inheritDoc} */
    @Override
    public IAtomType.Hybridization getHybridization() {
        logger.debug("Getting hybridization: ", super.getHybridization());
        return super.getHybridization();
    }

    /** {@inheritDoc} */
    @Override
    public void setCovalentRadius(Double radius) {
        logger.debug("Setting covalent radius: ", radius);
        super.setCovalentRadius(radius);
    }

    /** {@inheritDoc} */
    @Override
    public Double getCovalentRadius() {
        logger.debug("Getting covalent radius: ", super.getCovalentRadius());
        return super.getCovalentRadius();
    }

    /** {@inheritDoc} */
    @Override
    public void setValency(Integer valency) {
        logger.debug("Setting valency: ", valency);
        super.setValency(valency);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getValency() {
        logger.debug("Getting valency: ", super.getValency());
        return super.getValency();
    }

}

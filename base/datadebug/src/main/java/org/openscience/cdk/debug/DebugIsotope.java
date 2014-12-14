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

import org.openscience.cdk.Isotope;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
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
public class DebugIsotope extends Isotope implements IIsotope {

    private static final long serialVersionUID = -2659188100080921299L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugIsotope.class);

    public DebugIsotope(String elementSymbol) {
        super(elementSymbol);
        logger.debug("Instantiated a DebugIsotope.");
    }

    public DebugIsotope(int atomicNumber, String elementSymbol, int massNumber, double exactMass, double abundance) {
        super(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
        logger.debug("Instantiated a DebugIsotope.");
    }

    public DebugIsotope(int atomicNumber, String elementSymbol, double exactMass, double abundance) {
        super(atomicNumber, elementSymbol, exactMass, abundance);
        logger.debug("Instantiated a DebugIsotope.");
    }

    public DebugIsotope(String elementSymbol, int massNumber) {
        super(elementSymbol, massNumber);
        logger.debug("Instantiated a DebugIsotope.");
    }

    public DebugIsotope(IElement element) {
        super(element);
        logger.debug("Instantiated a DebugIsotope from element: ", element);
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

}

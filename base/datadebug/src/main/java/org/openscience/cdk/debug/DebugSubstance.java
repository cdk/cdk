/* Copyright (C) 2005-2014  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugSubstance extends AtomContainerSet implements ISubstance {

    private static final long serialVersionUID = 7729610512495602788L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugSubstance.class);

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
    public void addAtomContainer(IAtomContainer atomContainer) {
        logger.debug("Adding atom container: ", atomContainer);
        super.addAtomContainer(atomContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAtomContainer(IAtomContainer atomContainer) {
        logger.debug("Removing atom container: ", atomContainer);
        super.removeAtomContainer(atomContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllAtomContainers() {
        logger.debug("Removing all atom containers");
        super.removeAllAtomContainers();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAtomContainer(int pos) {
        logger.debug("Removing atom container at pos: ", pos);
        super.removeAtomContainer(pos);
    }

    /** {@inheritDoc} */
    @Override
    public void replaceAtomContainer(int position, IAtomContainer container) {
        logger.debug("Replacing atom container at pos: ", position);
        super.replaceAtomContainer(position, container);
    }

    /** {@inheritDoc} */
    @Override
    public boolean setMultiplier(IAtomContainer container, Double multiplier) {
        logger.debug("Setting multiplier for atomcontainer: ", container, "" + multiplier);
        return super.setMultiplier(container, multiplier);
    }

    /** {@inheritDoc} */
    @Override
    public void setMultiplier(int position, Double multiplier) {
        logger.debug("Setting multiplier for atomcontainer at pos: ", "" + position, "" + multiplier);
        super.setMultiplier(position, multiplier);
    }

    /** {@inheritDoc} */
    @Override
    public Double[] getMultipliers() {
        logger.debug("Getting multipliers array: ", super.getMultipliers().length);
        return super.getMultipliers();
    }

    /** {@inheritDoc} */
    @Override
    public boolean setMultipliers(Double[] newMultipliers) {
        logger.debug("Setting multipliers array: ", newMultipliers.length);
        return super.setMultipliers(newMultipliers);
    }

    /** {@inheritDoc} */
    @Override
    public void addAtomContainer(IAtomContainer atomContainer, double multiplier) {
        logger.debug("Adding atom container with multiplier: ", "" + multiplier, atomContainer);
        super.addAtomContainer(atomContainer, multiplier);
    }

    /** {@inheritDoc} */
    @Override
    public void add(IAtomContainerSet atomContainerSet) {
        logger.debug("Adding set of atom containers: ", atomContainerSet);
        super.add(atomContainerSet);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IAtomContainer> atomContainers() {
        logger.debug("Getting AtomContainer iterator");
        return super.atomContainers();
    }

    /** {@inheritDoc} */
    @Override
    public IAtomContainer getAtomContainer(int number) {
        logger.debug("Getting atom container at: ", number);
        return super.getAtomContainer(number);
    }

    /** {@inheritDoc} */
    @Override
    public Double getMultiplier(int number) {
        logger.debug("Getting multiplier at: ", number);
        return super.getMultiplier(number);
    }

    /** {@inheritDoc} */
    @Override
    public Double getMultiplier(IAtomContainer container) {
        logger.debug("Getting multiplier for atom container: ", container);
        return super.getMultiplier(container);
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomContainerCount() {
        logger.debug("Getting atom container count: ", super.getAtomContainerCount());
        return super.getAtomContainerCount();
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        logger.debug("Checking if the atom container set empty: ", atomContainerCount == 0);
        return atomContainerCount == 0;
    }
}

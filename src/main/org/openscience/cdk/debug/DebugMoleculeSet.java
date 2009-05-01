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

import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugMoleculeSet extends MoleculeSet
    implements IMoleculeSet {

    private static final long serialVersionUID = -2925577323304207688L;
    
    LoggingTool logger = new LoggingTool(DebugMoleculeSet.class);

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

	public void addAtomContainer(IAtomContainer atomContainer) {
		logger.debug("Adding atom container: ", atomContainer);
		super.addAtomContainer(atomContainer);
	}

	public void removeAtomContainer(IAtomContainer atomContainer) {
		logger.debug("Removing atom container: ", atomContainer);
		super.removeAtomContainer(atomContainer);
	}

	public void removeAllAtomContainers() {
		logger.debug("Removing all atom containers");
		super.removeAllAtomContainers();
	}

	public void removeAtomContainer(int pos) {
		logger.debug("Removing atom container at pos: ", pos);
		super.removeAtomContainer(pos);
	}

	public void replaceAtomContainer(int position, IAtomContainer container) {
		logger.debug("Replacing atom container at pos: ", position);
		super.replaceAtomContainer(position, container);
	}
	
	public boolean setMultiplier(IAtomContainer container, Double multiplier) {
		logger.debug("Setting multiplier for atomcontainer: ", container, ""+multiplier);
		return super.setMultiplier(container, multiplier);
	}

	public void setMultiplier(int position, Double multiplier) {
		logger.debug("Setting multiplier for atomcontainer at pos: ", ""+position, ""+multiplier);
		super.setMultiplier(position, multiplier);
	}

	public Double[] getMultipliers() {
		logger.debug("Getting multipliers array: ", super.getMultipliers().length);
		return super.getMultipliers();
	}

	public boolean setMultipliers(Double[] newMultipliers) {
		logger.debug("Setting mulipliers array: ", newMultipliers.length);
		return super.setMultipliers(newMultipliers);
	}

	public void addAtomContainer(IAtomContainer atomContainer, double multiplier) {
		logger.debug("Adding atom container with multiplier: ", ""+multiplier, atomContainer);
		super.addAtomContainer(atomContainer, multiplier);
	}

	public void add(IAtomContainerSet atomContainerSet) {
		logger.debug("Adding set of atom containers: ", atomContainerSet);
		super.add(atomContainerSet);
	}

	public Iterable<IAtomContainer> atomContainers() {
		logger.debug("Getting atom container iterator");
		return super.atomContainers();
	}

	public IAtomContainer getAtomContainer(int number) {
		logger.debug("Getting atom container at: ", number);
		return super.getAtomContainer(number);
	}

	public Double getMultiplier(int number) {
		logger.debug("Getting multiplier at: ", number);
		return super.getMultiplier(number);
	}

	public Double getMultiplier(IAtomContainer container) {
		logger.debug("Getting multiplier for atom container: ", container);
		return super.getMultiplier(container);
	}

	public int getAtomContainerCount() {
		logger.debug("Getting atom container count: ", super.getAtomContainerCount());
		return super.getAtomContainerCount();
	}

	public void addMolecule(IMolecule molecule) {
		logger.debug("Adding molecule: ", molecule);
		super.addMolecule(molecule);
	}

	public void add(IMoleculeSet moleculeSet) {
		logger.debug("Adding set of molecules: ", moleculeSet);
		super.add(moleculeSet);
	}

	public void setMolecules(IMolecule[] molecules) {
		logger.debug("Setting molecule array: ", molecules.length);
		super.setMolecules(molecules);
	}

	public Iterable<IAtomContainer> molecules() {
		logger.debug("Getting molecule iterator");
		return super.molecules();
	}

	public IMolecule getMolecule(int number) {
		logger.debug("Getting molecule at: ", number);
		return super.getMolecule(number);
	}

	public int getMoleculeCount() {
		logger.debug("Getting molecule count: ", super.getMoleculeCount());
		return super.getMoleculeCount();
	}

}

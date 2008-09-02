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

import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.Map;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugBond extends org.openscience.cdk.Bond
    implements IBond {

    private static final long serialVersionUID = -2330398179697796261L;
    
    LoggingTool logger = new LoggingTool(DebugBond.class);
	
	public DebugBond() {
		super();
	}
	
	public DebugBond(org.openscience.cdk.interfaces.IAtom atom1, org.openscience.cdk.interfaces.IAtom atom2) {
		super(atom1, atom2);
	}
	
	public DebugBond(IAtom atom1, IAtom atom2, IBond.Order order) {
		super(atom1, atom2, order);
	}
	
	public DebugBond(IAtom atom1, IAtom atom2, IBond.Order order, int stereo) {
		super(atom1, atom2, order, stereo);
	}

    public DebugBond(IAtom[] atoms) {
        super(atoms);
    }

    public DebugBond(IAtom[] atoms, IBond.Order order) {
        super(atoms, order);
    }

    public Integer getElectronCount() {
		logger.debug("Getting electron count: ", super.getElectronCount());
		return super.getElectronCount();
	}

	public void setElectronCount(int electronCount) {
		logger.debug("Setting electron count: ", electronCount);
		super.setElectronCount(electronCount);
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

	public Iterable<IAtom> atoms() {
		logger.debug("Getting atoms iterable");
		return super.atoms();
	}

	public void setAtoms(IAtom[] atoms) {
		logger.debug("Setting atoms: ", atoms.length);
		super.setAtoms(atoms);
	}

	public int getAtomCount() {
		logger.debug("Getting atom count: ", super.getAtomCount());
		return super.getAtomCount();
	}

	public IAtom getAtom(int position) {
		logger.debug("Getting atom at position: ", position);
		return super.getAtom(position);
	}

	public IAtom getConnectedAtom(IAtom atom) {
		logger.debug("Getting connected atom to atom: ", atom);
		return super.getConnectedAtom(atom);
	}

	public boolean contains(IAtom atom) {
		logger.debug("Contains atom: ", atom);
		return super.contains(atom);
	}

	public void setAtom(IAtom atom, int position) {
		logger.debug("Setting atom at position: ", atom);
		super.setAtom(atom, position);
	}

	public Order getOrder() {
		logger.debug("Getting order: ", super.getOrder());
		return super.getOrder();
	}

	public void setOrder(Order order) {
		logger.debug("Setting order: ", order);
		super.setOrder(order);
	}

	public int getStereo() {
		logger.debug("Getting stereo: ", super.getStereo());
		return super.getStereo();
	}

	public void setStereo(int stereo) {
		logger.debug("Setting stereo: ", stereo);
		super.setStereo(stereo);
	}

	public Point2d get2DCenter() {
		logger.debug("Getting 2d center: ", super.get2DCenter());
		return super.get2DCenter();
	}

	public Point3d get3DCenter() {
		logger.debug("Getting 3d center: ", super.get3DCenter());
		return super.get3DCenter();
	}

	public boolean compare(Object object) {
		logger.debug("Comparing to object: ", object);
		return super.compare(object);
	}

	public boolean isConnectedTo(IBond bond) {
		logger.debug("Is connected to bond: ", bond);
		return super.isConnectedTo(bond);
	}

}

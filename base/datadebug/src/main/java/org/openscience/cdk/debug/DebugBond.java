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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
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
public class DebugBond extends Bond implements IBond {

    private static final long serialVersionUID = -2330398179697796261L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugBond.class);

    public DebugBond() {
        super();
    }

    public DebugBond(IAtom atom1, IAtom atom2) {
        super(atom1, atom2);
    }

    public DebugBond(IAtom atom1, IAtom atom2, IBond.Order order) {
        super(atom1, atom2, order);
    }

    public DebugBond(IAtom atom1, IAtom atom2, IBond.Order order, IBond.Stereo stereo) {
        super(atom1, atom2, order, stereo);
    }

    public DebugBond(IAtom[] atoms) {
        super(atoms);
    }

    public DebugBond(IAtom[] atoms, IBond.Order order) {
        super(atoms, order);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getElectronCount() {
        logger.debug("Getting electron count: ", super.getElectronCount());
        return super.getElectronCount();
    }

    /** {@inheritDoc} */
    @Override
    public void setElectronCount(Integer electronCount) {
        logger.debug("Setting electron count: ", electronCount);
        super.setElectronCount(electronCount);
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
    public IBond clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (Exception exception) {
            logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
            logger.debug(exception);
        }
        return (IBond) clone;
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IAtom> atoms() {
        logger.debug("Getting atoms iterable");
        return super.atoms();
    }

    /** {@inheritDoc} */
    @Override
    public void setAtoms(IAtom[] atoms) {
        logger.debug("Setting atoms: ", atoms.length);
        super.setAtoms(atoms);
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomCount() {
        logger.debug("Getting atom count: ", super.getAtomCount());
        return super.getAtomCount();
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getAtom(int position) {
        logger.debug("Getting atom at position: ", position);
        return super.getAtom(position);
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getConnectedAtom(IAtom atom) {
        logger.debug("Getting connected atom to atom: ", atom);
        return super.getConnectedAtom(atom);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IAtom atom) {
        logger.debug("Contains atom: ", atom);
        return super.contains(atom);
    }

    /** {@inheritDoc} */
    @Override
    public void setAtom(IAtom atom, int position) {
        logger.debug("Setting atom at position: ", atom);
        super.setAtom(atom, position);
    }

    /** {@inheritDoc} */
    @Override
    public Order getOrder() {
        logger.debug("Getting order: ", super.getOrder());
        return super.getOrder();
    }

    /** {@inheritDoc} */
    @Override
    public void setOrder(Order order) {
        logger.debug("Setting order: ", order);
        super.setOrder(order);
    }

    /** {@inheritDoc} */
    @Override
    public IBond.Stereo getStereo() {
        logger.debug("Getting stereo: ", super.getStereo());
        return super.getStereo();
    }

    /** {@inheritDoc} */
    @Override
    public void setStereo(IBond.Stereo stereo) {
        logger.debug("Setting stereo: ", stereo);
        super.setStereo(stereo);
    }

    /** {@inheritDoc} */
    @Override
    public Point2d get2DCenter() {
        logger.debug("Getting 2d center: ", super.get2DCenter());
        return super.get2DCenter();
    }

    /** {@inheritDoc} */
    @Override
    public Point3d get3DCenter() {
        logger.debug("Getting 3d center: ", super.get3DCenter());
        return super.get3DCenter();
    }

    /** {@inheritDoc} */
    @Override
    public boolean compare(Object object) {
        logger.debug("Comparing to object: ", object);
        return super.compare(object);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isConnectedTo(IBond bond) {
        logger.debug("Is connected to bond: ", bond);
        return super.isConnectedTo(bond);
    }

}

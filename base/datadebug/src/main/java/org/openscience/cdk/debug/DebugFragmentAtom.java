/* Copyright (C) 2006-2008  Egon Willighagen <ewilligh@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
import org.openscience.cdk.FragmentAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Class to represent an IPseudoAtom which embeds an IAtomContainer. Very much like the MDL molfile
 * <code>Group</code> concept.
 *
 * @cdk.module datadebug
 * @cdk.githash
 * @author egonw
 */
public class DebugFragmentAtom extends FragmentAtom {

    private static final long serialVersionUID = 0L;

    private static final ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(DebugFragmentAtom.class);

    public DebugFragmentAtom() {
        super();
        logger.debug("Instantiated a DebugFragmentAtom");
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExpanded() {
        logger.debug("Getting isExpanded state...");
        return super.isExpanded();
    }

    /** {@inheritDoc} */
    @Override
    public void setExpanded(boolean bool) {
        logger.debug("Setting the isExpanded state: " + bool);
        super.setExpanded(bool);
    }

    /** {@inheritDoc} */
    @Override
    public IAtomContainer getFragment() {
        logger.debug("Getting the fragment...");
        return super.getFragment();
    }

    /** {@inheritDoc} */
    @Override
    public void setFragment(IAtomContainer fragment) {
        logger.debug("Setting the fragment: ", fragment);
        super.setFragment(fragment);
    }

    /** {@inheritDoc} */
    @Override
    public void setCharge(Double charge) {
        logger.debug("Setting charge: ", charge);
        super.setCharge(charge);
    }

    /** {@inheritDoc} */
    @Override
    public Double getCharge() {
        logger.debug("Setting charge: ", super.getCharge());
        return super.getCharge();
    }

    /** {@inheritDoc} */
    @Override
    public void setImplicitHydrogenCount(Integer hydrogenCount) {
        logger.debug("Setting hydrogen count: ", hydrogenCount);
        super.setImplicitHydrogenCount(hydrogenCount);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getImplicitHydrogenCount() {
        logger.debug("Getting hydrogen count: ", super.getImplicitHydrogenCount());
        return super.getImplicitHydrogenCount();
    }

    /** {@inheritDoc} */
    @Override
    public void setPoint2d(Point2d point2d) {
        logger.debug("Setting point2d: x=" + point2d.x + ", y=" + point2d.y);
        super.setPoint2d(point2d);
    }

    /** {@inheritDoc} */
    @Override
    public void setPoint3d(Point3d point3d) {
        logger.debug("Setting point3d: x=" + point3d.x + ", y=" + point3d.y, ", z=" + point3d.z);
        super.setPoint3d(point3d);
    }

    /** {@inheritDoc} */
    @Override
    public void setFractionalPoint3d(Point3d point3d) {
        logger.debug(
                "Setting fractional point3d: x=" + point3d.x + ", y=" + point3d.y,
                ", z=" + point3d.z);
        super.setFractionalPoint3d(point3d);
    }

    /** {@inheritDoc} */
    @Override
    public void setStereoParity(Integer stereoParity) {
        logger.debug("Setting stereoParity: ", stereoParity);
        super.setStereoParity(stereoParity);
    }

    /** {@inheritDoc} */
    @Override
    public Point2d getPoint2d() {
        Point2d point2d = super.getPoint2d();
        if (point2d == null) {
            logger.debug("Getting point2d: null");
        } else {
            logger.debug("Getting point2d: x=" + point2d.x + ", y=" + point2d.y);
        }
        return point2d;
    }

    /** {@inheritDoc} */
    @Override
    public Point3d getPoint3d() {
        Point3d point3d = super.getPoint3d();
        if (point3d == null) {
            logger.debug("Getting point3d: null");
        } else {
            logger.debug(
                    "Getting point3d: x=" + point3d.x + ", y=" + point3d.y, ", z=" + point3d.z);
        }
        return point3d;
    }

    /** {@inheritDoc} */
    @Override
    public Point3d getFractionalPoint3d() {
        Point3d point3d = super.getFractionalPoint3d();
        if (point3d == null) {
            logger.debug("Getting fractional point3d: null");
        } else {
            logger.debug(
                    "Getting fractional point3d: x=" + point3d.x + ", y=" + point3d.y,
                    ", z=" + point3d.z);
        }
        return point3d;
    }

    /** {@inheritDoc} */
    @Override
    public Integer getStereoParity() {
        logger.debug("Getting stereo parity: ", super.getStereoParity());
        return super.getStereoParity();
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
    public IFragmentAtom clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (Exception exception) {
            logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
            logger.debug(exception);
        }
        return (IFragmentAtom) clone;
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public String getLabel() {
        logger.debug("Getting label: ", super.getLabel());
        return super.getLabel();
    }

    /** {@inheritDoc} */
    @Override
    public void setLabel(String label) {
        logger.debug("Setting label: ", label);
        super.setLabel(label);
    }
}

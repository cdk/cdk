/* Copyright (C) 2012-2015  Egon Willighagen <egonw@users.sf.net>
 *               2012-2014  John May <john.wilkinsonmay@gmail.com>
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
package org.openscience.cdk.config;

import java.util.Collections;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 * An immutable {@link IAtomType} implementation to support the {@link AtomTypeFactory}.
 *
 * @author egonw
 */
final class ImmutableAtomType implements IAtomType {

    private final String  element;
    private final Integer atomicNumber;
    private final Double  naturalAbundance;
    private final Double  exactMass;
    private final Integer massNumber;
    private final Integer formalCharge;
    private final IAtomType.Hybridization hybridization;
    private final Integer electronValency;
    private final Integer formalNeighbourCount;
    private final String  identifier;
    private final IBond.Order maxBondOrder;
    private final Double  bondOrderSum;
    private final Double  covalentRadius;
    private final short   flags;
    private final Map<Object,Object> properties;

    ImmutableAtomType(IAtomType type) {
        this.element = type.getSymbol();
        this.atomicNumber = type.getAtomicNumber();
        this.naturalAbundance = type.getNaturalAbundance();
        this.exactMass = type.getExactMass();
        this.massNumber = type.getMassNumber();
        this.formalCharge = type.getFormalCharge();
        this.hybridization = type.getHybridization();
        this.formalNeighbourCount = type.getFormalNeighbourCount();
        this.identifier = type.getAtomTypeName();
        this.maxBondOrder = type.getMaxBondOrder();
        this.bondOrderSum = type.getBondOrderSum();
        this.covalentRadius = type.getCovalentRadius();
        this.flags = (short)type.getFlagValue();
        this.properties = Collections.unmodifiableMap(type.getProperties());
        if (type.getValency() != null) {
            this.electronValency = type.getValency();
        } else {
            Integer piBondCount = type.getProperty(CDKConstants.PI_BOND_COUNT, Integer.class);
            if (piBondCount != null && formalNeighbourCount != null) {
                this.electronValency = piBondCount + formalNeighbourCount;
            } else {
                this.electronValency = null;
            }
        }
    }

    // ignored methods

    @Override
    public void addListener(IChemObjectListener col) {}

    @Override
    public int getListenerCount() {
        return 0;
    }

    @Override
    public void removeListener(IChemObjectListener col) {}

    @Override
    public void setNotification(boolean bool) {}

    @Override
    public boolean getNotification() {
        return false;
    }

    @Override
    public void notifyChanged() {}

    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {}

    // unsupported methods

    @Override
    public Number getFlagValue() {
        return flags;
    }

    @Override
    public void setProperty(Object description, Object property) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void removeProperty(Object description) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public <T> T getProperty(Object description) {
    	if (!properties.containsKey(description)) return null;
        @SuppressWarnings("unchecked")
        T value = (T) properties.get(description);
        return value;
    }

    @Override
    public <T> T getProperty(Object description, Class<T> c) {
    	Object value = properties.get(description);

        if (c.isInstance(value)) {

            @SuppressWarnings("unchecked")
            T typed = (T) value;
            return typed;

        } else if (value != null) {
            throw new IllegalArgumentException("attempted to access a property of incorrect type, expected "
                    + c.getSimpleName() + " got " + value.getClass().getSimpleName());
        }

        return null;
    }

    @Override
    public Map<Object, Object> getProperties() {
    	return properties;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public void setID(String identifier) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setFlag(int flagType, boolean flagValue) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public boolean getFlag(int flagType) {
    	return (flags & flagType) != 0;
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void addProperties(Map<Object, Object> properties) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setFlags(boolean[] flagsNew) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public boolean[] getFlags() {
        // could use a list a invoke .toArray() on the return
        boolean[] flagArray = new boolean[CDKConstants.MAX_FLAG_INDEX + 1];
        for (int i = 0; i < CDKConstants.FLAG_MASKS.length; i++) {
            int mask = CDKConstants.FLAG_MASKS[i];
            flagArray[i] = getFlag(mask);
        }
        return flagArray;

    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return null;
    }

    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setSymbol(String symbol) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setExactMass(Double exactMass) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    @Override
    public void setMassNumber(Integer massNumber) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

    // implemented methods

    @Override
    public String getSymbol() {
        return element;
    }

    @Override
    public Integer getAtomicNumber() {
        return atomicNumber;
    }

    @Override
    public Double getNaturalAbundance() {
        return naturalAbundance;
    }

    @Override
    public Double getExactMass() {
        return exactMass;
    };

    @Override
    public Integer getMassNumber() {
        return massNumber;
    }

    @Override
    public Object clone() {
        return this;
    }

	@Override
	public void setAtomTypeName(String identifier) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public void setMaxBondOrder(Order maxBondOrder) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public void setBondOrderSum(Double bondOrderSum) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public String getAtomTypeName() {
		return this.identifier;
	}

	@Override
	public Order getMaxBondOrder() {
		return this.maxBondOrder;
	}

	@Override
	public Double getBondOrderSum() {
		return this.bondOrderSum;
	}

	@Override
	public void setFormalCharge(Integer charge) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public Integer getFormalCharge() {
		return this.formalCharge;
	}

	@Override
	public void setFormalNeighbourCount(Integer count) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public Integer getFormalNeighbourCount() {
		return this.formalNeighbourCount;
	}

	@Override
	public void setHybridization(Hybridization hybridization) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public Hybridization getHybridization() {
		return this.hybridization;
	}

	@Override
	public void setCovalentRadius(Double radius) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public Double getCovalentRadius() {
		return this.covalentRadius;
	}

	@Override
	public void setValency(Integer valency) {
        throw new UnsupportedOperationException("Immutable atom type cannot be modified");
    }

	@Override
	public Integer getValency() {
		return this.electronValency;
	}

}

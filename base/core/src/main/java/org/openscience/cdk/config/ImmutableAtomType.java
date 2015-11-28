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
public final class ImmutableAtomType implements IAtomType {

    private String  element;
    private Integer atomicNumber;
    private Double  naturalAbundance;
    private Double  exactMass;
    private Integer massNumber;
    private Integer formalCharge;
    private IAtomType.Hybridization hybridization;
    private Integer electronValency;
    private Integer formalNeighbourCount;
    private String  identifier;
    private IBond.Order maxBondOrder;
    private Double  bondOrderSum;
    private Double  covalentRadius;
    private short   flags;
    private Map<Object,Object> properties;

    protected ImmutableAtomType(IAtomType type) {
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
            this.electronValency = type.getProperty(CDKConstants.PI_BOND_COUNT, Integer.class) + type.getFormalNeighbourCount();
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
        return (short) 0;
    }

    @Override
    public void setProperty(Object description, Object property) {}

    @Override
    public void removeProperty(Object description) {}

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
    public void setID(String identifier) {}

    @Override
    public void setFlag(int flagType, boolean flagValue) {}

    @Override
    public boolean getFlag(int flagType) {
    	return (flags & flagType) != 0;
    }

    @Override
    public void setProperties(Map<Object, Object> properties) {}

    @Override
    public void addProperties(Map<Object, Object> properties) {}

    @Override
    public void setFlags(boolean[] flagsNew) {}

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
    public void setAtomicNumber(Integer atomicNumber) {}

    @Override
    public void setSymbol(String symbol) {}

    @Override
    public void setExactMass(Double exactMass) {}

    @Override
    public void setNaturalAbundance(Double naturalAbundance) {}

    @Override
    public void setMassNumber(Integer massNumber) {}

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
	public void setAtomTypeName(String identifier) {}

	@Override
	public void setMaxBondOrder(Order maxBondOrder) {}

	@Override
	public void setBondOrderSum(Double bondOrderSum) {}

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
	public void setFormalCharge(Integer charge) {}

	@Override
	public Integer getFormalCharge() {
		return this.formalCharge;
	}

	@Override
	public void setFormalNeighbourCount(Integer count) {}

	@Override
	public Integer getFormalNeighbourCount() {
		return this.formalNeighbourCount;
	}

	@Override
	public void setHybridization(Hybridization hybridization) {}

	@Override
	public Hybridization getHybridization() {
		return this.hybridization;
	}

	@Override
	public void setCovalentRadius(Double radius) {}

	@Override
	public Double getCovalentRadius() {
		return this.covalentRadius;
	}

	@Override
	public void setValency(Integer valency) {}

	@Override
	public Integer getValency() {
		return this.electronValency;
	}

}

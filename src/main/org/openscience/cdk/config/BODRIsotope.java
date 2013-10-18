/* Copyright (C) 2012-2013  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IIsotope;

/**
 * A read-only class used by {@link Isotopes} for the natural elements. This class is not to
 * be used than by only {@link Isotopes}.
 * 
 * @author      egonw
 * @cdk.module  core 
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.config.BODRIsotopeTest")
final class BODRIsotope implements IIsotope {
	
	private String element;
	private Integer atomicNumber;
	private Double naturalAbundance;
	private Double exactMass;
	private Integer massNumber;

	@TestMethod("testConstructor")
	protected BODRIsotope(String element, Integer atomicNumber, Integer massNumber,
			              Double exactMass, Double naturalAbundance) {
		this.element = element;
		this.atomicNumber = atomicNumber;
		this.massNumber = massNumber;
		this.naturalAbundance = naturalAbundance;
		this.exactMass = exactMass;
	}

	// ignored methods

	@Override @TestMethod("untested") public void addListener(IChemObjectListener col) {}
	@Override @TestMethod("untested") public int getListenerCount() { return 0; }
	@Override @TestMethod("untested") public void removeListener(IChemObjectListener col) {}
	@Override @TestMethod("untested") public void setNotification(boolean bool) {}
	@Override @TestMethod("untested") public boolean getNotification() { return false; }
	@Override @TestMethod("untested") public void notifyChanged() {}
	@Override @TestMethod("untested") public void notifyChanged(IChemObjectChangeEvent evt) {}

	// unsupported methods

    @Override @TestMethod("untested") public Number getFlagValue() { return (short)0; }
	@Override @TestMethod("untested") public void setProperty(Object description, Object property) {}
	@Override @TestMethod("untested") public void removeProperty(Object description) {}
	@Override @TestMethod("untested") public <T> T getProperty(Object description) { return null; }
	@Override @TestMethod("untested") public <T> T getProperty(Object description, Class<T> c) { return null; }
	@Override @TestMethod("untested") public Map<Object, Object> getProperties() { return null; }
	@Override @TestMethod("untested") public String getID() { return null; }
	@Override @TestMethod("untested") public void setID(String identifier) {}
	@Override @TestMethod("untested") public void setFlag(int flag_type, boolean flag_value) {}
	@Override @TestMethod("untested") public boolean getFlag(int flag_type) { return false; }
	@Override @TestMethod("untested") public void setProperties(Map<Object, Object> properties) {}
	@Override @TestMethod("untested") public void setFlags(boolean[] flagsNew) {}
	@Override @TestMethod("untested") public boolean[] getFlags() { return new boolean[CDKConstants.MAX_FLAG_INDEX + 1]; }
	@Override @TestMethod("untested") public IChemObjectBuilder getBuilder() { return null; }
	@Override @TestMethod("testImmutable") public void setAtomicNumber(Integer atomicNumber) {}
	@Override @TestMethod("testImmutable") public void setSymbol(String symbol) {}
	@Override @TestMethod("testImmutable") public void setExactMass(Double exactMass) {}
	@Override @TestMethod("testImmutable") public void setNaturalAbundance(Double naturalAbundance) {}
	@Override @TestMethod("testImmutable") public void setMassNumber(Integer massNumber) {}

	// implemented methods

	@Override @TestMethod("testConstructor") public String getSymbol() { return element; }
	@Override @TestMethod("testConstructor") public Integer getAtomicNumber() { return atomicNumber; }
	@Override @TestMethod("testConstructor") public Double getNaturalAbundance() { return naturalAbundance; }
	@Override @TestMethod("testConstructor") public Double getExactMass() { return exactMass; };
	@Override @TestMethod("testConstructor") public Integer getMassNumber() { return massNumber; }
	@TestMethod("testNonclonable") public Object clone() { return this; }

}

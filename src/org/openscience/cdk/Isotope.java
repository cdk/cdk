/*
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk;

/**
 *  Isotope.java Used to store data of a particular isotope
 *
 * @author     steinbeck
 * @created    August 21, 2001
 *
 * @keyword     isotope
 */

public class Isotope extends Element implements Cloneable {

	public double exactMass = (double) -1;
	public double naturalAbundance = (double) -1;


	/**
	 *  Constructor for the Isotope object
	 *
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 */
	public Isotope(String elementSymbol) {
		super(elementSymbol);
	}
	
	/**
	 *  Constructor for the Isotope object
	 *
	 * @param  atomicNumber   The atomic number of the isotope
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  atomicMass     The atomic mass of the isotope, 16 for Oxygen, e.g.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  nA             The natural abundance of the isotope
	 */
	public Isotope(int atomicMass, String elementSymbol, int atomicNumber, double exactMass, double nA) {
		super(elementSymbol, atomicNumber, atomicMass);
		this.exactMass = exactMass;
		this.naturalAbundance = nA;
	}


	/**
	 *  Constructor for the Isotope object
	 *
	 * @param  atomicNumber   The atomic number of the isotope
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  nA             The natural abundance of the isotope
	 */
	public Isotope(int atomicMass, String elementSymbol, double exactMass, double nA) {
		this(atomicMass, elementSymbol, (int) (atomicMass / 2), exactMass, nA);
	}


	/**
	 *  Sets the NaturalAbundance attribute of the Isotope object
	 *
	 * @param  naturalAbundance  The new NaturalAbundance value
	 */
	public void setNaturalAbundance(double naturalAbundance) {
		this.naturalAbundance = naturalAbundance;
	}


	/**
	 *  Sets the ExactMass attribute of the Isotope object
	 *
	 * @param  exactMass  The new ExactMass value
	 */
	public void setExactMass(double exactMass) {
		this.exactMass = exactMass;
	}


	/**
	 *  Gets the NaturalAbundance attribute of the Isotope object
	 *
	 * @return    The NaturalAbundance value
	 */
	public double getNaturalAbundance() {
		return this.naturalAbundance;
	}


	/**
	 *  Gets the ExactMass attribute of the Isotope object
	 *
	 * @return    The ExactMass value
	 */
	public double getExactMass() {
		return this.exactMass;
	}


	/**
	 *  Clones this atom object.
	 *
	 * @return    The cloned object
	 */
	public Object clone() {
		Object o = null;
		try {
			o = super.clone();
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}
		return o;
	}


	/**
	 *  A string representation of this isotope
	 *
	 * @return    A string representation of this isotope
	 */
	public String toString() {
		String s = "[" + atomicMass + "]";
		s += symbol + ": exact mass = " + exactMass;
		s += "; relative natural abundance = " + naturalAbundance;
		return s;
	}
}


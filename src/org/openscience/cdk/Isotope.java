/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
 * Used to store and retrieve data of a particular isotope.
 * For example, an carbon 13 isotope can be created with:
 * <pre>
 *   Isotope carbon = new Isotope("C", 13);
 * </pre>
 *
 * <p>A full specification can be constructed with:
 * <pre>
 *   // make deuterium
 *   Isotope carbon = new Isotope(1, "H", 2, 2.01410179, 100.0);
 * </pre>
 *
 * @cdk.module core
 *
 * @author     steinbeck
 * @created    2001-08-21
 *
 * @cdk.keyword     isotope
 */
public class Isotope extends Element implements java.io.Serializable, Cloneable 
{

    /** Exact mass of this isotope. */
    public double exactMass = (double) -1;
    /** Natural abundance of this isotope. */
    public double naturalAbundance = (double) -1;
    /** The mass number for this isotope. */
    private int massNumber = 0;


	/**
	 *  Constructor for the Isotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 */
	public Isotope(String elementSymbol) {
		super(elementSymbol);
	}
	
	/**
	 *  Constructor for the Isotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  massNumber     The atomic mass of the isotope, 16 for Oxygen, e.g.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  nA             The natural abundance of the isotope
	 */
	public Isotope(int atomicNumber, String elementSymbol, int massNumber, double exactMass, double nA) {
		this(atomicNumber, elementSymbol, exactMass, nA);
        this.massNumber = massNumber;
	}


	/**
	 *  Constructor for the Isotope object.
	 *
	 * @param  atomicNumber   The atomic number of the isotope, 8 for Oxygen
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 * @param  exactMass      The exact mass of the isotope, be a little more explicit here :-)
	 * @param  nA             The natural abundance of the isotope
	 */
	public Isotope(int atomicNumber, String elementSymbol, double exactMass, double nA) {
		super(elementSymbol, atomicNumber);
		this.exactMass = exactMass;
		this.naturalAbundance = nA;
	}

	/**
	 * Constructor for the Isotope object.
	 *
	 * @param  elementSymbol  The element symbol, "O" for Oxygen, etc.
	 */
	public Isotope(String elementSymbol, int massNumber) {
		super(elementSymbol);
		this.massNumber = massNumber;
	}

	/**
	 *  Sets the NaturalAbundance attribute of the Isotope object.
	 *
	 * @param  naturalAbundance  The new NaturalAbundance value
     *
     * @see       #getNaturalAbundance
	 */
	public void setNaturalAbundance(double naturalAbundance) {
		this.naturalAbundance = naturalAbundance;
	}


	/**
	 *  Sets the ExactMass attribute of the Isotope object.
	 *
	 * @param  exactMass  The new ExactMass value
     *
     * @see       #getExactMass
	 */
	public void setExactMass(double exactMass) {
		this.exactMass = exactMass;
	}


	/**
	 *  Gets the NaturalAbundance attribute of the Isotope object.
	 *
	 * @return    The NaturalAbundance value
     *
     * @see       #setNaturalAbundance
	 */
	public double getNaturalAbundance() {
		return this.naturalAbundance;
	}


	/**
	 *  Gets the ExactMass attribute of the Isotope object.
	 *
	 * @return    The ExactMass value
     *
     * @see       #setExactMass
	 */
	public double getExactMass() {
		return this.exactMass;
	}

    /**
     * Returns the atomic mass of this element.
     *
     * @return The atomic mass of this element
     *
     * @see    #setMassNumber(int)
     */
    public int getMassNumber() {

        return this.massNumber;
    }

    /**
     * Sets the atomic mass of this element.
     *
     * @param   massNumber The atomic mass to be assigned to this element
     *
     * @see    #getMassNumber
     */
    public void setMassNumber(int massNumber) {
        this.massNumber = massNumber;
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
	 *  A string representation of this isotope.
	 *
	 * @return    A string representation of this isotope
	 */
	public String toString() {
        StringBuffer sb = new StringBuffer();
		sb.append("Isotope("); sb.append(massNumber);
		sb.append(", EM:"); sb.append(exactMass);
		sb.append(", AB:"); sb.append(naturalAbundance);
        sb.append(", "); sb.append(super.toString());
        sb.append(")");
		return sb.toString();
	}
    
    /**
     * Compare a atom type with this atom type.
     *
     * @param  object Object of type AtomType
     * @return        Return true, if the atomtypes are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof Isotope)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        Isotope isotope = (Isotope)object;
        if (massNumber == isotope.massNumber && 
            exactMass == isotope.exactMass &&
            naturalAbundance == isotope.naturalAbundance) {
            return true;
        }
        return false;
    }
    
}


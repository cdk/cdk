/* $RCSfile$    
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
 * Implements the idea of an element in the periodic table.
 * Use an element factory to get a ready-to-use element by name or number
 */

public class Element extends ChemObject
{
	/** The element symbol for this element as listed in the periodic table */
	protected String symbol;
	
	/** The atomic number for this element giving their position in the periodic table */
	protected int atomicNumber = 0;
	
	/** The atomic mass of this element */
	protected int atomicMass;	

	/**
	 * Constructs an empty Element
	 *
	 */
	public Element()
	{
	}

	
	/**
	 * Constructs an Element with a given 
	 * element symbol.
	 *
	 * @param   symbol The element symbol that this element should have.  
	 */
	public Element(String symbol)
	{
		this.symbol = symbol;
	}

	/**
	 * Constructs an Element with a given element symbol, 
	 * atomic number and atomic mass.
	 *
	 * @param   symbol  The element symbol of this element.
	 * @param   atomicNumber  The atomicNumber of this element.
	 * @param   atomicMass  The atomicMass of this element.
	 */
	public Element(String symbol, int atomicNumber, int atomicMass)
	{
		this(symbol);
		this.atomicNumber = atomicNumber;
		this.atomicMass = atomicMass;
	}

	/**
	 * Returns the atomic mass of this element
	 *
	 * @return The atomic mass of this element
	 */
		public int getAtomicMass()
	{
		return this.atomicMass;
	}

	/**
	 *	Sets the atomic mass of this element
	 *
	 * @param   atomicMass The atomic mass to be assigned to this element
	 */
	public void setAtomicMass(int atomicMass)
	{
		this.atomicMass = atomicMass;
	}

	/**
	 * Returns the atomic number of this element
	 *
	 * @return The atomic number of this element    
	 */
	public int getAtomicNumber()
	{
/* 		Isotope isotope = null;
		if (this.atomicNumber == 0)
		{
			isotope	= new org.openscience.cdk.tools.StandardIsotopes().getMajorIsotope(getSymbol());
			this.atomicNumber = (int)(isotope.atomicMass / 2);
		}
 */		
 		return this.atomicNumber;
	}

	/**
	 * Set the atomic number of this element
	 *
	 * @param   atomicNumber The atomic mass to be assigned to this element  
	 */
	public void setAtomicNumber(int atomicNumber)
	{
		this.atomicNumber = atomicNumber;
	}

	/**
	 * Returns the element symbol of this element
	 *
	 * @return The element symbol of this element
	 */
	public String getSymbol()
	{
		return this.symbol;
	}

	/**
	 * Sets the element symbol of this element
	 *
	 * @param symbol The element symbol to be assigned to this atom
	 */
	public void setSymbol(String symbol)
	{
		this.symbol = symbol;
	}
	
	        /**
         * Clones this atom object.
         *
         * @return  The cloned object   
         */
        public Object clone()
        {
                Object o = null;
                try
                {
                        o = super.clone();
                }
                catch (Exception e)
                {
                        e.printStackTrace(System.err);
                }
                return o;
        }

}


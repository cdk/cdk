/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 * 
 */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IElement;

import java.io.Serializable;

/**
 * Implements the idea of an element in the periodic table.
 * 
 * <p>Use the IsotopeFactory to get a ready-to-use elements
 * by symbol or atomic number:
 * <pre>
 *   IsotopeFactory if = IsotopeFactory.getInstance(new Element().getBuilder());
 *   Element e1 = if.getElement("C");
 *   Element e2 = if.getElement(12);
 * </pre>
 *
 * @cdk.module data
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword element
 *
 * @see org.openscience.cdk.config.IsotopeFactory
 */
public class Element extends ChemObject implements Serializable, IElement, Cloneable
{

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = 3062529834691231436L;

	/** The element symbol for this element as listed in the periodic table. */
    protected String symbol;

    /** The atomic number for this element giving their position in the periodic table. */
    protected Integer atomicNumber = (Integer) CDKConstants.UNSET;

    /**
     * Constructs an empty Element.
     */
    public Element() {
        super();
        this.symbol = null;
    }

    /**
     * Constructs an empty by copying the symbol, atomic number,
     * flags, and identifier from the given IElement. It does
     * not copy the listeners and properties.
     * 
     * @param element IElement to copy information from
     */
    public Element(IElement element) {
    	super(element);
    	this.symbol = element.getSymbol();
    	this.atomicNumber = element.getAtomicNumber();
    }
    
    /**
     * Constructs an Element with a given 
     * element symbol.
     *
     * @param   symbol The element symbol that this element should have.  
     */
    public Element(String symbol) {
        this();
        this.symbol = symbol;
    }

    /**
     * Constructs an Element with a given element symbol, 
     * atomic number and atomic mass.
     *
     * @param   symbol  The element symbol of this element.
     * @param   atomicNumber  The atomicNumber of this element.
     */
    public Element(String symbol, int atomicNumber) {
        this(symbol);
        this.atomicNumber = atomicNumber;
    }

    /**
     * Returns the atomic number of this element.
     * 
	 *  <p>Once instantiated all field not filled by passing parameters
	 * to the constructor are null. Elements can be configured by using
	 * the IsotopeFactory.configure() method:
	 * <pre>
	 *   Element element = new Element("C");
	 *   IsotopeFactory if = IsotopeFactory.getInstance(element.getBuilder());
	 *   if.configure(element);
	 * </pre>
	 * </p>      
     *
     * @return The atomic number of this element    
     *
     * @see    #setAtomicNumber
     */
    public Integer getAtomicNumber() {
        return this.atomicNumber;
    }

    /**
     * Sets the atomic number of this element.
     *
     * @param   atomicNumber The atomic mass to be assigned to this element
     *
     * @see    #getAtomicNumber
     */
    public void setAtomicNumber(Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
	notifyChanged();
    }

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
	notifyChanged();
    }

    public String toString() {
        StringBuffer resultString = new StringBuffer(32);
        resultString.append("Element(").append(hashCode());
        if (getSymbol() != null) {
        	resultString.append(", S:").append(getSymbol());
        }
        if (getID() != null) {
        	resultString.append(", ID:").append(getID());
        }
        if (getAtomicNumber() != null) {
        	resultString.append(", AN:").append(getAtomicNumber());
        }
        resultString.append(')');
        return resultString.toString();
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Compares an Element with this Element.
     *
     * @param  object Object of type AtomType
     * @return        true if the atom types are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof Element)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        Element elem = (Element)object;
        return atomicNumber == elem.atomicNumber &&
                symbol.equals(elem.symbol);
    }
}

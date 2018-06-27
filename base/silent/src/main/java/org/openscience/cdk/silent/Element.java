/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
package org.openscience.cdk.silent;

import java.io.Serializable;

import com.google.common.base.Objects;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * Implements the idea of an element in the periodic table.
 *
 * <p>Use the IsotopeFactory to get a ready-to-use elements
 * by symbol or atomic number:
 * <pre>
 *   IsotopeFactory if = IsotopeFactory.getInstance(new Element().getNewBuilder());
 *   Element e1 = if.getElement("C");
 *   Element e2 = if.getElement(12);
 * </pre>
 *
 * @cdk.module  silent
 * @cdk.githash
 *
 * @cdk.keyword element
 *
 * @see org.openscience.cdk.config.XMLIsotopeFactory
 */
public class Element extends ChemObject implements Serializable, IElement, Cloneable {

    /**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is incompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
     */
    private static final long serialVersionUID = 3062529834691231436L;

    /** The atomic number for this element giving their position in the periodic table. */
    protected Integer atomicNumber = null;

    /**
     * Constructs an empty Element.
     */
    public Element() {
        super();
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
        setSymbolInternal(symbol);
    }

    /**
     * Constructs an Element with a given element symbol,
     * atomic number and atomic mass.
     *
     * @param   symbol  The element symbol of this element.
     * @param   atomicNumber  The atomicNumber of this element.
     */
    public Element(String symbol, Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
    }

    /**
     * Returns the atomic number of this element.
     *
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Elements can be configured by using
     * the IsotopeFactory.configure() method:</p>
     * <pre>
     *   Element element = new Element("C");
     *   IsotopeFactory if = IsotopeFactory.getInstance(element.getNewBuilder());
     *   if.configure(element);
     * </pre>
     *
     *
     * @return The atomic number of this element
     *
     * @see    #setAtomicNumber
     */
    @Override
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
    @Override
    public void setAtomicNumber(Integer atomicNumber) {
        this.atomicNumber = atomicNumber;
    }

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    @Override
    public String getSymbol() {
        if (atomicNumber == null)
            return null;
        if (atomicNumber == 0)
            return "R";
        return Elements.ofNumber(atomicNumber).symbol();
    }

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    @Override
    public void setSymbol(String symbol) {
        setSymbolInternal(symbol);
    }

    private void setSymbolInternal(String symbol) {
        if (symbol == null)
            this.atomicNumber = null;
        else
            this.atomicNumber = Elements.ofString(symbol).number();
    }

    @Override
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Compares an Element with this Element.
     *
     * @param  object Object of type AtomType
     * @return        true if the atom types are equal
     */
    @Override
    public boolean compare(Object object) {
        if (!(object instanceof Element)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        Element elem = (Element) object;
        return Objects.equal(atomicNumber, elem.atomicNumber);
    }
}

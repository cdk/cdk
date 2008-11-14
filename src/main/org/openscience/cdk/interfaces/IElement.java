/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

/**
 * Implements the idea of an element in the periodic table.
 *
 * @cdk.module interfaces
 * @cdk.svnrev  $Revision$
 * @cdk.keyword element
 * @cdk.keyword atomic number
 * @cdk.keyword number, atomic
 */
public interface IElement extends IChemObject {

    /**
     * Returns the atomic number of this element.
     * 
	 * <p>Once instantiated all field not filled by passing parameters
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
    public Integer getAtomicNumber();

    /**
     * Sets the atomic number of this element.
     *
     * @param   atomicNumber The atomic mass to be assigned to this element
     *
     * @see    #getAtomicNumber
     */
    public void setAtomicNumber(Integer atomicNumber);

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    public String getSymbol();

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    public void setSymbol(String symbol);

}

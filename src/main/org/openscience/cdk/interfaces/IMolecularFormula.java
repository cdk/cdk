/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.interfaces;

import java.util.Iterator;
import java.util.Map;

/**
 *  Class defining a molecular formula object. It maintains
 *   a list of IISotope.<p>
 *   
 *  Examples:
 * <ul>
 *   <li><code>[C5H5]-</code></li>
 *   <li><code>C6H6</code></li>
 *   <li><code><sup>12</sup>C5</sup><sup>13</sup>CH6</code></li>
 * </ul>
 * 
 * @cdk.module  interfaces
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
public interface IMolecularFormula{

	/**
	 * Adds an molecularFormula to this MolecularFormula.
	 *
	 * @param  formula  The molecularFormula to be added to this chemObject
	 */
	public IMolecularFormula add(IMolecularFormula formula);
	
	/**
	 *  Adds an Isotope to this MolecularFormula one time.
	 *
	 * @param  isotope  The isotope to be added to this MolecularFormula
	 * @see             #addIsotope(IIsotope, int)
	 */
	public IMolecularFormula addIsotope(IIsotope isotope); 
	
	/**
	 *  Adds an Isotope to this MolecularFormula in a number of occurrences.
	 *
	 * @param  isotope  The isotope to be added to this MolecularFormula
	 * @param  count    The number of occurrences to add
	 * @see             #addIsotope(IIsotope)
	 */
	public IMolecularFormula addIsotope(IIsotope isotope, int count);
	
	/**
	 *  Checks a set of Nodes for the occurrence of the isotope in the 
	 *  IMolecularFormula from a particular isotope. It returns 0 if the does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this IMolecularFormula
	 * @see                      #getIsotopeCount()
	 */
	public int getIsotopeCount(IIsotope isotope);
	
	/**
	 *  Returns an {@link Iterable} for looping over all isotopes in this IMolecularFormula.
	 *
	 * @return    An {@link Iterable} with the isotopes in this IMolecularFormula
	 */
	public Iterable<IIsotope> isotopes();
	
	/**
	 *  Checks a set of Nodes for the number of different isotopes in the 
	 *  IMolecularFormula.
	 *
	 * @return        The the number of different isotopes in this IMolecularFormula
	 * @see           #getIsotopeCount(IIsotope)
	 */
	public int getIsotopeCount();
    
	/**
	 *  True, if the MolecularFormula contains the given IIsotope object. Not
	 *  the instance. The method looks for other isotopes which has the same
	 *  symbol, natural abundance and exact mass.
	 *
	 * @param  isotope  The IIsotope this IMolecularFormula is searched for
	 * @return          True, if the IMolecularFormula contains the given isotope object
	 */
	public boolean contains(IIsotope isotope);
	
	/**
	 *  Removes the given isotope from the MolecularFormula.
	 *
	 * @param isotope  The IIsotope to be removed
	 */
	public void removeIsotope(IIsotope isotope);

	/**
	 * Removes all isotopes of this molecular formula.
	 */
	public void removeAllIsotopes();
	
	/**
     *  Sets the partial charge of this IMolecularFormula.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Double charge) ;

    /**
     *  Returns the partial charge of this IMolecularFormula. If the charge 
     *  has not been set the return value is Double.NaN.
     *
     * @return the charge of this IMolecularFormula
     *
     * @see    #setCharge
     */
    public Double getCharge();
    /**
	 * Clones this MolecularFormula object and its content.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException ;
	
	
	/**
	 *  Sets a property for a IChemObject. I should
	 * integrate into ChemObject.
	 *
	 *@param  description  An object description of the property (most likely a
	 *      unique string)
	 *@param  property     An object with the property itself
	 *@see                 #getProperty
	 *@see                 #removeProperty
	 */
	public void setProperty(Object description, Object property);


	/**
	 *  Removes a property for a IChemObject. I should
	 * integrate into ChemObject.
	 *
	 *@param  description  The object description of the property (most likely a
	 *      unique string)
	 *@see                 #setProperty
	 *@see                 #getProperty
	 */
	public void removeProperty(Object description);


	/**
	 *  Returns a property for the IChemObject. I should
	 * integrate into ChemObject.
	 *
	 *@param  description  An object description of the property (most likely a
	 *      unique string)
	 *@return              The object containing the property. Returns null if
	 *      property is not set.
	 *@see                 #setProperty
	 *@see                 #removeProperty
	 */
	public Object getProperty(Object description);


	/**
	 *  Returns a Map with the IChemObject's properties.I should
	 * integrate into ChemObject.
	 *
	 *@return    The object's properties as an Hashtable
	 *@see       #setProperties
	 */
	public Map<Object, Object> getProperties();
	/**
	 *  Sets the properties of this object.I should
	 * integrate into ChemObject.
	 *
	 *@param  properties  a Hashtable specifying the property values
	 *@see                #getProperties
	 */
	public void setProperties(Map<Object, Object> properties);
	
    /**
     * Returns a ChemObjectBuilder for the data classes that extend
     * this class.
     * 
     * @return The IChemObjectBuilder matching this IChemObject
     */
    public IChemObjectBuilder getBuilder();
    
}

/* Copyright (C) 2007  Miguel Rojasch <miguelrojasch@users.sf.net>
 *               2012  John May <john.wilkinsonmay@gmail.com>
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
 * @cdk.githash
 */
public interface IMolecularFormula extends ICDKObject {

    /**
     * Adds an molecularFormula to this MolecularFormula.
     *
     * @param  formula  The molecularFormula to be added to this chemObject
     * @return the new molecular formula
     */
    public IMolecularFormula add(IMolecularFormula formula);

    /**
     *  Adds an Isotope to this MolecularFormula one time.
     *
     * @param  isotope  The isotope to be added to this MolecularFormula
     * @see             #addIsotope(IIsotope, int)
     * @return the new molecular formula
     */
    public IMolecularFormula addIsotope(IIsotope isotope);

    /**
     *  Adds an Isotope to this MolecularFormula in a number of occurrences.
     *
     * @param  isotope  The isotope to be added to this MolecularFormula
     * @param  count    The number of occurrences to add
     * @see             #addIsotope(IIsotope)
     * @return the new molecular formula
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
     * Sets the charge of this IMolecularFormula, since there is no atom
     * associated with the charge the number of a given isotope is not modified.
     *
     * <pre>{@code
     *   // Correct usage
     *   IMolecularFormula phenolate = MolecularFormulaManipulator.getMolecularFormula("C6H5O", builder)
     *   mf.setCharge(-1);
     *   // MF=C6H5O-
     *
     *   // Wrong! the H6 is not automatically adjust
     *   IMolecularFormula phenolate = MolecularFormulaManipulator.getMolecularFormula("C6H6O", builder)
     *   mf.setCharge(-1);
     *   // MF=C6H6O- (wrong)
     * }</pre>
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Integer charge);

    /**
     * Access the charge of this IMolecularFormula. If the charge
     * has not been set the return value is null.
     *
     * @return the charge of this IMolecularFormula
     *
     * @see    #setCharge
     */
    public Integer getCharge();

    /**
     * Clones this MolecularFormula object and its content.
     *
     * @return    The cloned object
     */
    public Object clone() throws CloneNotSupportedException;

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
     * Returns a property for the IChemObject - the object is automatically
     * cast to the required type. This does however mean if the wrong type is
     * provided then a runtime ClassCastException will be thrown.
     *
     * <p/>
     * <pre>{@code
     *
     *     IAtom atom = new Atom("C");
     *     atom.setProperty("number", 1); // set an integer property
     *
     *     // access the property and automatically cast to an int
     *     Integer number = atom.getProperty("number");
     *
     *     // if the method is in a chain or needs to be nested the type
     *     // can be provided
     *     methodAcceptingInt(atom.getProperty("number", Integer.class));
     *
     *     // the type cannot be checked and so...
     *     String number = atom.getProperty("number"); // ClassCastException
     *
     *     // if the type is provided a more meaningful error is thrown
     *     atom.getProperty("number", String.class); // IllegalArgumentException
     *
     * }</pre>
     * @param  description  An object description of the property (most likely a
     *                      unique string)
     * @param  <T>          generic return type
     * @return              The object containing the property. Returns null if
     *                      property is not set.
     * @see                 #setProperty
     * @see                 #getProperty(Object, Class)
     * @see                 #removeProperty
     */
    public <T> T getProperty(Object description);

    /**
     * Access a property of the given description and cast the specified class.
     * <p/>
     * <pre>{@code
     *
     *     IAtom atom = new Atom("C");
     *     atom.setProperty("number", 1); // set an integer property
     *
     *     // access the property and automatically cast to an int
     *     Integer number = atom.getProperty("number");
     *
     *     // if the method is in a chain or needs to be nested the type
     *     // can be provided
     *     methodAcceptingInt(atom.getProperty("number", Integer.class));
     *
     *     // the type cannot be checked and so...
     *     String number = atom.getProperty("number"); // ClassCastException
     *
     *     // if the type is provided a more meaningful error is thrown
     *     atom.getProperty("number", String.class); // IllegalArgumentException
     *
     * }</pre>
     * @param description description of a property (normally a string)
     * @param c           type of the value to be returned
     * @param <T>         generic type (of provided class)
     * @return the value stored for the specified description.
     * @see #getProperty(Object)
     * @see #addProperties(java.util.Map)
     */
    public <T> T getProperty(Object description, Class<T> c);

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

}

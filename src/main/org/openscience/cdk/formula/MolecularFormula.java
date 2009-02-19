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
package org.openscience.cdk.formula;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
/**
 * Class defining a molecular formula object. It maintains
 * a list of list {@link IIsotope}.
 *   
 * <p>Examples:
 * <ul>
 *   <li><code>[C<sub>5</sub>H<sub>5</sub>]-</code></li>
 *   <li><code>C<sub>6</sub>H<sub>6</sub></code></li>
 *   <li><code><sup>12</sup>C<sub>5</sub><sup>13</sup>CH<sub>6</sub></code></li>
 * </ul>
 * 
 * @cdk.module  data
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
@TestClass("org.openscience.cdk.formula.MolecularFormulaTest")
public class MolecularFormula implements IMolecularFormula, Cloneable {

	/**
     * Determines if a de-serialized object is compatible with this class.
     *
     * This value must only be changed if and only if the new version
     * of this class is imcompatible with the old version. See Sun docs
     * for <a href=http://java.sun.com/products/jdk/1.1/docs/guide
     * /serialization/spec/version.doc.html>details</a>.
	 */
	private static final long serialVersionUID = -2011407700837295287L;
	
	private Map<IIsotope, Integer> isotopes;
	/**
     *  The partial charge of the molecularFormula. The default value is Double.NaN.
     */
	private Integer charge = (Integer) CDKConstants.UNSET;

	/**
	 *  A hashtable for the storage of any kind of properties of this IChemObject.
	 */
	private Map<Object, Object> properties;
	
	/**
	 *  Constructs an empty MolecularFormula.
	 */
	public MolecularFormula() {
		isotopes = new HashMap<IIsotope,Integer>();
	}
	
	/**
	 * Adds an molecularFormula to this MolecularFormula.
	 *
	 * @param  formula  The molecularFormula to be added to this chemObject
	 * @return          The IMolecularFormula
	 */
    @TestMethod("testAdd_IMolecularFormula")
	public IMolecularFormula add(IMolecularFormula formula) {
		for (IIsotope newIsotope : formula.isotopes()) {
			addIsotope(newIsotope,formula.getIsotopeCount(newIsotope));
		}
		if(formula.getCharge() != null)charge += formula.getCharge();
		return this;
	}

	
	/**
	 *  Adds an Isotope to this MolecularFormula one time.
	 *
	 * @param  isotope  The isotope to be added to this MolecularFormula
	 * @see             #addIsotope(IIsotope, int)
	 */
    @TestMethod("addIsotope_IIsotope")
	public IMolecularFormula addIsotope(IIsotope isotope) {
		return this.addIsotope(isotope, 1);
	}

	/**
	 *  Adds an Isotope to this MolecularFormula in a number of occurrences.
	 *
	 * @param  isotope  The isotope to be added to this MolecularFormula
	 * @param  count    The number of occurrences to add
	 * @see             #addIsotope(IIsotope)
	 */
    @TestMethod("testAddIsotope_IIsotope_int")
	public IMolecularFormula addIsotope(IIsotope isotope, int count) {
		boolean flag = false;
		for (IIsotope thisIsotope : isotopes()) {
			if(isTheSame(thisIsotope, isotope)){
				isotopes.put(thisIsotope, isotopes.get(thisIsotope) + count);
				flag = true;
				break;
			}
		}
		if(!flag){
			isotopes.put(isotope, count);
		}
		
		return this;
	}

	/**
	 *  True, if the MolecularFormula contains the given IIsotope object and not
	 *  the instance. The method looks for other isotopes which has the same
	 *  symbol, natural abundance and exact mass.
	 *
	 * @param  isotope  The IIsotope this MolecularFormula is searched for
	 * @return          True, if the MolecularFormula contains the given isotope object
	 */
    @TestMethod("testContains_IIsotope")
	public boolean contains(IIsotope isotope) {
		for (IIsotope thisIsotope : isotopes()) {
			if(isTheSame(thisIsotope, isotope)){
				return true;
			}
		}
		return false;
	}

	/**
     *  Returns the partial charge of this IMolecularFormula. If the charge 
     *  has not been set the return value is Double.NaN.
     *
     * @return the charge of this IMolecularFormula
     *
     * @see    #setCharge
     */
    @TestMethod("testGetCharge")
	public Integer getCharge() {
		return charge;
	}

	/**
	 *  Checks a set of Nodes for the occurrence of the isotope in the 
	 *  IMolecularFormula from a particular isotope. It returns 0 if the does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this IMolecularFormula
	 * @see                      #getIsotopeCount()
	 */
    @TestMethod("testGetIsotopeCount_IIsotope")
	public int getIsotopeCount(IIsotope isotope) {
		return !contains(isotope) ? 0 : isotopes.get(getIsotope(isotope));
	}

	/**
	 *  Checks a set of Nodes for the number of different isotopes in the 
	 *  IMolecularFormula.
	 *
	 * @return        The the number of different isotopes in this IMolecularFormula
	 * @see           #getIsotopeCount(IIsotope)
	 */
    @TestMethod("testGetIsotopeCount")
	public int getIsotopeCount() {
		return isotopes.size();
	}
	
	/**
	 *  Get the isotope instance given an IIsotope. The instance is those
	 *  that has the isotope with the same symbol, natural abundance and
	 *  exact mass.
	 *
	 * @param  isotope The IIsotope for looking for
	 * @return         The IIsotope instance
   * @see            #isotopes
	 */
	private IIsotope getIsotope(IIsotope isotope){
		for (IIsotope thisIsotope : isotopes()) {
			if(isTheSame(isotope,thisIsotope))
				return thisIsotope;
		}
		return null;
	}
	/**
	 *  Returns an Iterator for looping over all isotopes in this IMolecularFormula.
	 *
	 * @return    An Iterator with the isotopes in this IMolecularFormula
	 */
    @TestMethod("testIsotopes")
	public Iterable<IIsotope> isotopes() {
		return isotopes.keySet();
	}

	/**
     *  Sets the partial charge of this IMolecularFormula.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    @TestMethod("testCharge_Integer")
	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	/**
	 * Removes all isotopes of this molecular formula.
	 */
    @TestMethod("testRemoveAllIsotopes")
    public void removeAllIsotopes() {
	    isotopes.clear();
    }

    /**
	 *  Removes the given isotope from the MolecularFormula.
	 *
	 * @param isotope  The IIsotope to be removed
	 */
    @TestMethod("testRemoveIsotope_IIsotope")
    public void removeIsotope(IIsotope isotope) {
	    isotopes.remove(getIsotope(isotope));
    }

    /**
	 * Clones this MolecularFormula object and its content. I should
	 * integrate into ChemObject.
	 *
	 * @return    The cloned object
	 */
    @TestMethod("testClone")
	public Object clone() throws CloneNotSupportedException {
		
//		/* it is not a super class of chemObject */
//		MolecularFormula clone = (MolecularFormula) super.clone();
//        // start from scratch
//		clone.removeAllIsotopes();
//        // clone all isotopes
//		Iterator<IIsotope> iterIso = this.isotopes();
//		while(iterIso.hasNext()){
//			IIsotope isotope = iterIso.next();
//			clone.addIsotope((IIsotope) isotope.clone(),getIsotopeCount(isotope));
//		}
		
		MolecularFormula clone = new MolecularFormula();
		for (IIsotope isotope : isotopes()) {
			clone.addIsotope((IIsotope) isotope.clone(),getIsotopeCount(isotope));
		}
		clone.setCharge(getCharge());
		return clone;
	}
	/**
	 * Lazy creation of properties hash. I should
	 * integrate into ChemObject.
	 *
	 * @return    Returns in instance of the properties
	 */
	private Map<Object, Object> lazyProperties(){
		if (properties == null)
		{
			properties = new Hashtable<Object, Object>();
		}
		return properties;
	}


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
    @TestMethod("testSetProperty_Object_Object")
	public void setProperty(Object description, Object property){
		lazyProperties().put(description, property);
	}


	/**
	 *  Removes a property for a IChemObject. I should
	 * integrate into ChemObject.
	 *
	 *@param  description  The object description of the property (most likely a
	 *      unique string)
	 *@see                 #setProperty
	 *@see                 #getProperty
	 */
    @TestMethod("testRemoveProperty_Object")
	public void removeProperty(Object description){
		if (properties == null) {
            return;
        }
        lazyProperties().remove(description);
	}


	/**
	 *  Returns a property for the IChemObject. I should
	 * integrate into ChemObject.
	 *
	 *@param  description  An object description of the property (most likely a
	 *      unique string)
	 *@return              The object containing the property. Returns null if
	 *      propert is not set.
	 *@see                 #setProperty
	 *@see                 #removeProperty
	 */
    @TestMethod("testGetProperty_Object")
	public Object getProperty(Object description){
        if (properties != null) {
            return lazyProperties().get(description);
        }
        return null;
	}


	/**
	 *  Returns a Map with the IChemObject's properties.I should
	 * integrate into ChemObject.
	 *
	 *@return    The object's properties as an Hashtable
	 *@see       #setProperties
	 */
    @TestMethod("testGetProperties")
	public Map<Object, Object> getProperties(){
		return lazyProperties();
	}
	/**
	 *  Sets the properties of this object.
	 *
	 *@param  properties  a Hashtable specifying the property values
	 *@see                #getProperties
	 */
    @TestMethod("testSetProperties_Hashtable")
	public void setProperties(Map<Object, Object> properties){
		
		Iterator<Object> keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			Object key = keys.next();
			lazyProperties().put(key, properties.get(key));
		}
	}
	/**
	 * Compare to IIsotope. The method doesn't compare instance but if they
	 * have the same symbol, natural abundance and exact mass.
	 * 
	 * @param isotopeOne   The first Isotope to compare
	 * @param isotopeTwo   The second Isotope to compare
	 * @return             True, if both isotope are the same
	 */
	private boolean isTheSame(IIsotope isotopeOne, IIsotope isotopeTwo) {
		
		if(!isotopeOne.getSymbol().equals(isotopeTwo.getSymbol() ))
			return false;
		if(isotopeOne.getNaturalAbundance() != isotopeTwo.getNaturalAbundance() )
			return false;
		if(isotopeOne.getExactMass() != isotopeTwo.getExactMass() )
			return false;
		
		return true;
	}

	public IChemObjectBuilder getBuilder() {
	    return DefaultChemObjectBuilder.getInstance();
    }

}

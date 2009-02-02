/*  $Revision$ $Author$ $Date$
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


/**
 *  Class defining an adduct object in a MolecularFormula. It maintains
 *   a list of list IMolecularFormula.<p>
 *   
 *  Examples:
 * <ul>
 *   <li><code>[C2H4O2+Na]+</code></li>
 * </ul>
 * 
 * @cdk.module  interfaces
 * @author      miguelrojasch
 * @cdk.created 2007-11-20
 * @cdk.keyword molecular formula
 */
public interface IAdductFormula extends IMolecularFormulaSet{

	/**
	 *  Checks a set of Nodes for the occurrence of the isotope in the 
	 *  adduct formula from a particular isotope. It returns 0 if the does not exist.
	 *
	 * @param   isotope          The IIsotope to look for
	 * @return                   The occurrence of this isotope in this adduct
	 * @see                      #getIsotopeCount()
	 */
	public int getIsotopeCount(IIsotope isotope);

	/**
	 *  Checks a set of Nodes for the number of different isotopes in the 
	 *  adduct formula.
	 *
	 * @return        The the number of different isotopes in this adduct formula
	 * @see           #getIsotopeCount(IIsotope)
	 */
	public int getIsotopeCount();
	
	/**
	 *  Returns an Iterator for looping over all isotopes in this adduct formula.
	 *
	 * @return    An Iterator with the isotopes in this adduct formula
	 */
	public Iterable<IIsotope> isotopes();
	
	/**
     *  Returns the partial charge of this Adduct. If the charge 
     *  has not been set the return value is Double.NaN.
     *
     * @return the charge of this Adduct
     *
     * @see    #setCharge
     */
    public Integer getCharge();
    
    /**
	 *  True, if the AdductFormula contains the given IIsotope object.
	 *
	 * @param  isotope  The IIsotope this AdductFormula is searched for
	 * @return          True, if the AdductFormula contains the given isotope object
	 */
	public boolean contains(IIsotope isotope);
	
	/**
     *  Sets the partial charge of this adduct formula.
     *
     * @param  charge  The partial charge
     *
     * @see    #getCharge
     */
    public void setCharge(Integer charge) ;
    
    /**
	 * Clones this IAdductFormula object and its content.
	 *
	 * @return    The cloned object
	 */
	public Object clone() throws CloneNotSupportedException ;
	
    /**
     * Returns a ChemObjectBuilder for the data classes that extend
     * this class.
     * 
     * @return The IChemObjectBuilder matching this IChemObject
     */
    public IChemObjectBuilder getBuilder();

}

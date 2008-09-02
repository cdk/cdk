/* $Revision: 10962 $ $Author: egonw $ $Date: 2008-05-11 08:26:06 +0200 (Sun, 11 May 2008) $
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.debug;

import org.openscience.cdk.formula.AdductFormula;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.tools.LoggingTool;

import java.util.Iterator;

/**
 * @cdk.module datadebug
 */
public class DebugAdductFormula extends AdductFormula implements IAdductFormula {

    private LoggingTool logger = new LoggingTool(DebugAdductFormula.class);
	
    public DebugAdductFormula() {
    	super();
    }
    
    public DebugAdductFormula(IMolecularFormula formula) {
    	super(formula);
    }
    
	public boolean contains(IIsotope isotope) {
	    logger.debug("Contains Isotope?: ", isotope);
	    return super.contains(isotope);
    }

	public Double getCharge() {
		Double charge = super.getCharge();
		logger.debug("Getting charge: ", charge);
	    return charge;
    }

	public int getIsotopeCount(IIsotope isotope) {
		logger.debug("Getting isotope count for: ", isotope);
	    return super.getIsotopeCount(isotope);
    }

	public int getIsotopeCount() {
		logger.debug("Getting isotope count: ", super.getIsotopeCount());
	    return super.getIsotopeCount();
    }

	public Iterable<IIsotope> isotopes() {
	    logger.debug("Getting isotope iterator..");
	    return super.isotopes();
    }

	public void setCharge(Double charge) {
	    logger.debug("Setting the charge to: ", charge);
	    super.setCharge(charge);
    }

	public void add(IMolecularFormulaSet formulaSet) {
	    logger.debug("Adding a formula set: ", formulaSet);
	    super.add(formulaSet);
    }

	public void addMolecularFormula(IMolecularFormula formula) {
	    logger.debug("Adding formula: ", formula);
	    super.addMolecularFormula(formula);
    }

	public boolean contains(IMolecularFormula formula) {
	    logger.debug("Contains formula?: ", formula);
	    return super.contains(formula);
    }

	public IMolecularFormula getMolecularFormula(int position) {
	    logger.debug("Getting formula at: ", position);
	    return super.getMolecularFormula(position);
    }

	public Iterator<IMolecularFormula> iterator() {
	    logger.debug("Getting molecular formula iterator...");
	    return super.iterator();
    }

	public Iterable<IMolecularFormula> molecularFormulas() {
		logger.debug("Getting molecular formula iterable...");
	    return super.molecularFormulas();
    }

	public void removeAllMolecularFormulas() {
	    logger.debug("Removing all formulas...");
	    super.removeAllMolecularFormulas();
    }

	public void removeMolecularFormula(IMolecularFormula formula) {
	    logger.debug("Removing this formula: ", formula);
	    super.removeMolecularFormula(formula);
    }

	public void removeMolecularFormula(int position) {
	    logger.debug("Removing the formula at position: ", position);
	    super.removeMolecularFormula(position);
    }

	public int size() {
	    logger.debug("Getting the size of this adduct: " + super.size());
	    return super.size();
    }

	public IChemObjectBuilder getBuilder() {
	    return DebugChemObjectBuilder.getInstance();
    }

}

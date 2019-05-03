/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Map;

import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging implementation of {@link IMolecularFormula}.
 *
 * @cdk.module datadebug
 * @cdk.githash tag
 */
public class DebugMolecularFormula extends MolecularFormula implements IMolecularFormula {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(DebugMolecularFormula.class);

    /** {@inheritDoc} */
    @Override
    public IMolecularFormula add(IMolecularFormula formula) {
        logger.debug("Adding formula: ", formula);
        return super.add(formula);
    }

    /** {@inheritDoc} */
    @Override
    public IMolecularFormula addIsotope(IIsotope isotope) {
        logger.debug("Adding isotope: ", isotope);
        return super.addIsotope(isotope);
    }

    /** {@inheritDoc} */
    @Override
    public IMolecularFormula addIsotope(IIsotope isotope, int count) {
        logger.debug("Adding 'count' isotopes: ", isotope);
        return super.addIsotope(isotope, count);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IIsotope isotope) {
        logger.debug("Contains isotope?: ", isotope);
        return super.contains(isotope);
    }

    /** {@inheritDoc} */
    @Override
    public Integer getCharge() {
        Integer charge = super.getCharge();
        logger.debug("Getting charge: ", charge);
        return charge;
    }

    /** {@inheritDoc} */
    @Override
    public int getIsotopeCount(IIsotope isotope) {
        logger.debug("Getting isotope count for: ", isotope);
        return super.getIsotopeCount(isotope);
    }

    /** {@inheritDoc} */
    @Override
    public int getIsotopeCount() {
        logger.debug("Getting isotope count: ", super.getIsotopeCount());
        return super.getIsotopeCount();
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        logger.debug("Getting properties...");
        return super.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        logger.debug("Getting property: " + description);
        return super.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IIsotope> isotopes() {
        logger.debug("Getting isotope iterator..");
        return super.isotopes();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllIsotopes() {
        logger.debug("Removing all isotopes...");
        super.removeAllIsotopes();
    }

    /** {@inheritDoc} */
    @Override
    public void removeIsotope(IIsotope isotope) {
        logger.debug("Removing this isotope: ", isotope);
        super.removeIsotope(isotope);
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        logger.debug("Removing property: " + description);
        super.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public void setCharge(Integer charge) {
        logger.debug("Setting the charge to: ", charge);
        super.setCharge(charge);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperties(Map<Object, Object> properties) {
        logger.debug("Setting new properties...");
        super.setProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        logger.debug("Setting new property: " + description + " -> " + property);
        super.setProperty(description, property);
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

}

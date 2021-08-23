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

import java.util.Iterator;
import org.openscience.cdk.formula.MolecularFormulaSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging implementation of {@link IMolecularFormulaSet}.
 *
 * @cdk.module datadebug
 * @cdk.githash tag
 */
public class DebugMolecularFormulaSet extends MolecularFormulaSet implements IMolecularFormulaSet {

    private ILoggingTool logger =
            LoggingToolFactory.createLoggingTool(DebugMolecularFormulaSet.class);

    public DebugMolecularFormulaSet() {
        super();
    }

    public DebugMolecularFormulaSet(IMolecularFormula formula) {
        super(formula);
    }

    /** {@inheritDoc} */
    @Override
    public void add(IMolecularFormulaSet formulaSet) {
        logger.debug("Adding formula set: ", formulaSet);
        super.add(formulaSet);
    }

    /** {@inheritDoc} */
    @Override
    public void addMolecularFormula(IMolecularFormula formula) {
        logger.debug("Adding formula: ", formula);
        super.addMolecularFormula(formula);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IMolecularFormula formula) {
        logger.debug("Contains formula?: ", formula);
        return super.contains(formula);
    }

    /** {@inheritDoc} */
    @Override
    public IMolecularFormula getMolecularFormula(int position) {
        logger.debug("Getting formula at: ", position);
        return super.getMolecularFormula(position);
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<IMolecularFormula> iterator() {
        logger.debug("Getting molecular formula iterator...");
        return super.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IMolecularFormula> molecularFormulas() {
        logger.debug("Getting molecular formula iterable...");
        return super.molecularFormulas();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllMolecularFormulas() {
        logger.debug("Removing all formulas...");
        super.removeAllMolecularFormulas();
    }

    /** {@inheritDoc} */
    @Override
    public void removeMolecularFormula(IMolecularFormula formula) {
        logger.debug("Removing this formula: ", formula);
        super.removeMolecularFormula(formula);
    }

    /** {@inheritDoc} */
    @Override
    public void removeMolecularFormula(int position) {
        logger.debug("Removing the formula at position: ", position);
        super.removeMolecularFormula(position);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        logger.debug("Getting the size of this adduct: " + super.size());
        return super.size();
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }
}

/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.validate;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * Abstract validator that does nothing but provide all the methods that the
 * ValidatorInterface requires.
 *
 * @cdk.module extra
 * @cdk.githash
 *
 * @author   Egon Willighagen
 * @cdk.created  2004-03-27
 * @cdk.require java1.4+
 */
public class AbstractValidator implements IValidator {

    @Override
    public ValidationReport validateChemObject(IChemObject subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateAtom(IAtom subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateAtomContainer(IAtomContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateAtomType(IAtomType subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateBond(IBond subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateChemFile(IChemFile subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateChemModel(IChemModel subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateChemSequence(IChemSequence subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateCrystal(ICrystal subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateElectronContainer(IElectronContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateElement(IElement subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateIsotope(IIsotope subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateMolecule(IAtomContainer subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateReaction(IReaction subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateMoleculeSet(IAtomContainerSet subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

    @Override
    public ValidationReport validateReactionSet(IReactionSet subject) {
        ValidationReport report = new ValidationReport();
        return report;
    }

}

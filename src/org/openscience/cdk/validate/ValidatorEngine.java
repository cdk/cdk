/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
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
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.ISetOfReactions;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Engine that performs the validation by traversing the IChemObject
 * hierarchy. Basic use of the ValidatorEngine is:
 * <pre>
 * ValidatorEngine engine = new ValidatorEngine();
 * engine.addValidator(new BasicValidator());
 * ValidationReport report = engine.validateMolecule(new Molecule());
 * </pre>
 *
 * @author   Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created  2003-08-22
 */ 
public class ValidatorEngine implements IValidator {
    
    private Hashtable validators;
    private LoggingTool logger;
    
    public ValidatorEngine() {
        validators = new Hashtable();
        logger = new LoggingTool(this);
    }
    
    public void addValidator(IValidator validator) {
        logger.info("Registering validator: " + validator.getClass().getName());
        String validatorName = validator.getClass().getName();
        if (validators.containsKey(validatorName)) {
            logger.warn("  already registered.");
        } else {
            validators.put(validatorName, validator);
        }
    }
    
    public void removeValidator(IValidator validator) {
        logger.info("Removing validator: " + validator.getClass().getName());
        String validatorName = validator.getClass().getName();
        if (!validators.containsKey(validatorName)) {
            logger.warn("  not in list.");
        } else {
            validators.remove(validatorName);
        }
    }
    
    public ValidationReport validateAtom(org.openscience.cdk.interfaces.IAtom subject) {
        logger.info("Validating org.openscience.cdk.Atom");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateAtom(subject));
        }
        // traverse into super class
        report.addReport(validateAtomType(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateAtomContainer(IAtomContainer subject) {
        logger.info("Validating org.openscience.cdk.AtomContainer");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateAtomContainer(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IAtom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            report.addReport(validateAtom(atoms[i]));
        }
        IBond[] bonds = subject.getBonds();
        for (int i=0; i<bonds.length; i++) {
            report.addReport(validateBond(bonds[i]));
        }
        return report;
    }
    public ValidationReport validateAtomType(IAtomType subject) {
        logger.info("Validating org.openscience.cdk.AtomType");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateAtomType(subject));
        }
        // traverse into super class
        report.addReport(validateIsotope(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateBond(IBond subject) {
        logger.info("Validating org.openscience.cdk.Bond");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateBond(subject));
        }
        // traverse into super class
        report.addReport(validateElectronContainer(subject));
        // traverse into hierarchy
        org.openscience.cdk.interfaces.IAtom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            report.addReport(validateAtom(atoms[i]));
        }
        return report;
    }
    public ValidationReport validateChemFile(IChemFile subject) {
        logger.info("Validating org.openscience.cdk.ChemFile");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateChemFile(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IChemSequence[] sequences = subject.getChemSequences();
        for (int i=0; i< sequences.length; i++) {
            report.addReport(validateChemSequence(sequences[i]));
        }
        return report;
    }
    public ValidationReport validateChemModel(IChemModel subject) {
        logger.info("Validating org.openscience.cdk.ChemModel");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateChemModel(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        ICrystal crystal = subject.getCrystal();
        if (crystal != null) {
            report.addReport(validateCrystal(crystal));
        }
        ISetOfReactions reactionSet = subject.getSetOfReactions();
        if (reactionSet != null) {
            report.addReport(validateSetOfReactions(reactionSet));
        }
        ISetOfMolecules moleculeSet = subject.getSetOfMolecules();
        if (moleculeSet != null) {
            report.addReport(validateSetOfMolecules(moleculeSet));
        }
        return report;
    }
    public ValidationReport validateChemObject(IChemObject subject) {
        logger.info("Validating org.openscience.cdk.ChemObject");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateChemObject(subject));
        }
        // traverse into super class
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateChemSequence(IChemSequence subject) {
        logger.info("Validating org.openscience.cdk.ChemSequence");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateChemSequence(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IChemModel[] models = subject.getChemModels();
        for (int i=0; i<models.length; i++) {
            report.addReport(validateChemModel(models[i]));
        }
        return report;
    }
    public ValidationReport validateCrystal(ICrystal subject) {
        logger.info("Validating org.openscience.cdk.Crystal");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateCrystal(subject));
        }
        // traverse into super class
        report.addReport(validateAtomContainer(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateElectronContainer(IElectronContainer subject) {
        logger.info("Validating org.openscience.cdk.ElectronContainer");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateElectronContainer(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateElement(IElement subject) {
        logger.info("Validating org.openscience.cdk.Element");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateElement(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateIsotope(IIsotope subject) {
        logger.info("Validating org.openscience.cdk.Isotope");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateIsotope(subject));
        }
        // traverse into super class
        report.addReport(validateElement(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateMolecule(IMolecule subject) {
        logger.info("Validating org.openscience.cdk.Molecule");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateMolecule(subject));
        }
        // traverse into super class
        report.addReport(validateAtomContainer(subject));
        // traverse into hierarchy
        return report;
    }
    public ValidationReport validateReaction(IReaction subject) {
        logger.info("Validating org.openscience.cdk.Reaction");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateReaction(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IMolecule[] reactants = subject.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            report.addReport(validateMolecule(reactants[i]));
        }
        IMolecule[] products = subject.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            report.addReport(validateMolecule(products[i]));
        }
        return report;
    }
    public ValidationReport validateSetOfMolecules(ISetOfMolecules subject) {
        logger.info("Validating org.openscience.cdk.SetOfMolecules");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateSetOfMolecules(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IMolecule[] molecules = subject.getMolecules();
        for (int i=0; i<molecules.length; i++) {
            report.addReport(validateMolecule(molecules[i]));
        }
        return report;
    }
    public ValidationReport validateSetOfReactions(ISetOfReactions subject) {
        logger.info("Validating org.openscience.cdk.SetOfReactions");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            IValidator test = (IValidator)tests.nextElement();
            report.addReport(test.validateSetOfReactions(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        IReaction[] reactions = subject.getReactions();
        for (int i=0; i<reactions.length; i++) {
            report.addReport(validateReaction(reactions[i]));
        }
        return report;
    }
    
}

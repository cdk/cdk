/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk.validate;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.tools.AtomTypeFactory;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Validator which tests a number of basic chemical semantics.
 *
 * @author   Egon Willighagen
 * @created  2003-08-22
 */ 
public class BasicValidator implements ValidatorInterface {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool("org.openscience.cdk.validate.BasicValidator");
    }
    
    public BasicValidator() {
    }
    
    public ValidationReport validateAtom(Atom subject) {
        ValidationReport report = new ValidationReport();
        report.addReport(validateCharge(subject));
        report.addReport(validateHydrogenCount(subject));
        report.addReport(validatePseudoAtom(subject));
        return report;
    };
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        return new ValidationReport();
    };
    public ValidationReport validateAtomType(AtomType subject) {
        return new ValidationReport();
    };
    public ValidationReport validateBond(Bond subject) {
        ValidationReport report = new ValidationReport();
        report.addReport(validateStereoChemistry(subject));
        report.addReport(validateMaxBondOrder(subject));
        return report;
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemModel(ChemModel subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemObject(ChemObject subject) {
        return new ValidationReport();
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        return new ValidationReport();
    };
    public ValidationReport validateCrystal(Crystal subject) {
        return new ValidationReport();
    };
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        return new ValidationReport();
    };
    public ValidationReport validateElement(Element subject) {
        return new ValidationReport();
    };
    public ValidationReport validateIsotope(Isotope subject) {
        return validateIsotopeExistence(subject);
    };
    public ValidationReport validateMolecule(Molecule subject) {
        ValidationReport report = new ValidationReport();
        ValidationTest emptyMolecule = new ValidationTest(subject,
            "Molecule does not contain any atom"
        );
        Atom[] atoms = subject.getAtoms();
        if (atoms.length == 0) {
            report.addError(emptyMolecule);
        } else {
            report.addOK(emptyMolecule);
            ValidationTest massCalcProblem = new ValidationTest(subject,
                "Molecule contains PseudoAtom's. Won't be able to calculate some properties, like molecular mass."
            );
            boolean foundMassCalcProblem = false;
            for (int i=0; i<atoms.length; i++) {
                if (atoms[i] instanceof PseudoAtom) {
                    foundMassCalcProblem = true;
                } else {
                    report.addReport(validateAtomValency(atoms[i], subject));
                    report.addReport(validateBondOrderSum(atoms[i], subject));
                }
            }
            if (foundMassCalcProblem) {
                report.addWarning(massCalcProblem);
            } else {
                report.addOK(massCalcProblem);
            }
        }
        return report;
    };
    public ValidationReport validateReaction(Reaction subject) {
        ValidationReport report = new ValidationReport();
        AtomContainer container1 = new AtomContainer();
        Molecule[] reactants = subject.getReactants();
        for (int i=0; i<reactants.length; i++) {
            container1.add(reactants[i]);
        }
        AtomContainer container2 = new AtomContainer();
        Molecule[] products = subject.getProducts();
        for (int i=0; i<products.length; i++) {
            container2.add(products[i]);
        }
        report.addReport(validateAtomCountConservation(subject, container1, container2));
        report.addReport(validateChargeConservation(subject, container1, container2));
        return report;
    };
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        return new ValidationReport();
    };
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        return new ValidationReport();
    };
    
    // the Atom tests
    
    private static ValidationReport validateCharge(Atom atom) {
        ValidationReport report = new ValidationReport();
        ValidationTest tooCharged = new ValidationTest(atom, "Atom has an unlikely large positive or negative charge");
        if (atom.getSymbol().equals("O") || atom.getSymbol().equals("N") ||
            atom.getSymbol().equals("C") || atom.getSymbol().equals("H")) {
            if (atom.getFormalCharge() < -3) {
                report.addError(tooCharged);
            } else if (atom.getFormalCharge() < -1) {
                report.addWarning(tooCharged);
            } else if (atom.getFormalCharge() > 3) {
                report.addError(tooCharged);
            } else if (atom.getFormalCharge() > 1) {
                report.addWarning(tooCharged);
            } else {
                report.addOK(tooCharged);
            }
        } else {
            if (atom.getFormalCharge() < -4) {
                report.addError(tooCharged);
            } else if (atom.getFormalCharge() < -3) {
                report.addWarning(tooCharged);
            } else if (atom.getFormalCharge() > 4) {
                report.addError(tooCharged);
            } else if (atom.getFormalCharge() > 2) {
                report.addWarning(tooCharged);
            } else {
                report.addOK(tooCharged);
            }
        }
        return report;
    }

    private static ValidationReport validateHydrogenCount(Atom atom) {
        ValidationReport report = new ValidationReport();
        ValidationTest negativeHydrogenCount = new ValidationTest(atom,
            "An Atom cannot have a negative number of hydrogens attached."
        );
        if (atom.getHydrogenCount() < 0 ) {
            negativeHydrogenCount.setDetails(
                "Atom has " + atom.getHydrogenCount() + " hydrogens."
            );
            report.addError(negativeHydrogenCount);
        } else {
            report.addOK(negativeHydrogenCount);
        }
        return report;
    }

    private static ValidationReport validatePseudoAtom(Atom atom) {
        ValidationReport report = new ValidationReport();
        ValidationTest isElementOrPseudo = new ValidationTest(atom,
            "Non-element atom must be of class PseudoAtom."
        );
        if (atom instanceof PseudoAtom) {
            // that's fine
            report.addOK(isElementOrPseudo);
        } else {
            // check wether atom is really an element
            Atom copy = null;
            try {
                IsotopeFactory isotopeFactory = IsotopeFactory.getInstance();
                Element element = isotopeFactory.getElement(atom.getSymbol());
                if (element == null) {
                    isElementOrPseudo.setDetails(
                        "Element " + atom.getSymbol() + " does not exist."
                    );
                    report.addError(isElementOrPseudo);
                } else {
                    report.addOK(isElementOrPseudo);
                }
            } catch (Exception exception) {
                // well... don't throw an error then.
                isElementOrPseudo.setDetails(exception.toString());
                report.addCDKError(isElementOrPseudo);
            }
        }
        return report;
    }
    
    // the Bond tests
    
    private static ValidationReport validateStereoChemistry(Bond bond) {
        ValidationReport report = new ValidationReport();
        ValidationTest bondStereo = new ValidationTest(bond,
            "Defining stereochemistry on bonds is not safe.",
            "Use atom based stereochemistry."
        );
        if (bond.getStereo() != CDKConstants.STEREO_BOND_NONE) {
            report.addWarning(bondStereo);
        } else {
            report.addOK(bondStereo);
        }
        return report;
    }
    
    private static ValidationReport validateMaxBondOrder(Bond bond) {
        ValidationReport report = new ValidationReport();
        ValidationTest maxBO = new ValidationTest(bond,
            "Bond order exceeds the maximum for one of its atoms."
        );
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance();
            for (int i=0; i<bond.getAtomCount(); i++) {
                Atom atom = bond.getAtomAt(i);
                AtomType[] atomTypes = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
                AtomType failedOn = null;
                boolean foundMatchingAtomType = false;
                for (int j=0; j<atomTypes.length; j++) {
                    if (bond.getOrder() <= atomTypes[j].getMaxBondOrder()) {
                        foundMatchingAtomType = true;
                    } else {
                        failedOn = atomTypes[j];
                    }
                }
                if (foundMatchingAtomType) {
                    report.addOK(maxBO);
                } else {
                    if (failedOn != null) {
                        maxBO.setDetails(
                            "Bond order exceeds the one allowed for atom " +
                            atom.getSymbol() + " for which the maximum bond order is " +
                            failedOn.getMaxBondOrder()
                        );
                    }
                    report.addError(maxBO);
                }
            }
        } catch (Exception exception) {
            logger.error("Error while performing atom bos validation");
            logger.debug(exception);
            maxBO.setDetails("Error while performing atom bos validation: " +
              exception.toString());
            report.addCDKError(maxBO);
        }
        return report;
    }
    
    // the Isotope tests
    
    public ValidationReport validateIsotopeExistence(Isotope isotope) {
        ValidationReport report = new ValidationReport();
        ValidationTest isotopeExists = new ValidationTest(isotope,
            "Isotope with this mass number is not known for this element."
        );
        try {
            IsotopeFactory isotopeFac = IsotopeFactory.getInstance();
            Isotope[] isotopes = isotopeFac.getIsotopes(isotope.getSymbol());
            if (isotope.getMassNumber() != 0) {
                boolean foundKnownIsotope = false;
                for (int i=0; i<isotopes.length; i++) {
                    if (isotopes[i].getMassNumber() == isotope.getMassNumber()) {
                        foundKnownIsotope = true;
                    }
                }
                if (!foundKnownIsotope) {
                    report.addError(isotopeExists);
                } else {
                    report.addOK(isotopeExists);
                }
            } else {
                // isotopic number is not set
                report.addOK(isotopeExists);
            }
        } catch (Exception exception) {
            // too bad...
        }
        return report;
    }
    
    // the Molecule tests

    private static ValidationReport validateAtomValency(Atom atom, Molecule molecule) {
        ValidationReport report = new ValidationReport();
        ValidationTest checkValency = new ValidationTest(atom,
            "The atom has an unfulfilled valency."
        );
        try {
            SaturationChecker saturationChecker = new SaturationChecker();
            if (!saturationChecker.isSaturated(atom, molecule)) {
                report.addError(checkValency);
            } else {
                report.addOK(checkValency);
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom valency validation: " + exception.toString());
        }
        return report;
    }

    private static ValidationReport validateBondOrderSum(Atom atom, Molecule molecule) {
        ValidationReport report = new ValidationReport();
        ValidationTest checkBondSum = new ValidationTest(atom,
            "The atom's total bond order is too high."
        );
        try {
            AtomTypeFactory atf = AtomTypeFactory.getInstance();
            int bos = (int)molecule.getBondOrderSum(atom);
            AtomType[] atomTypes = atf.getAtomTypes(atom.getSymbol(), atf.ATOMTYPE_ID_STRUCTGEN);
            AtomType failedOn = null;
            boolean foundMatchingAtomType = false;
            for (int j=0; j<atomTypes.length; j++) {
                if (bos <= atomTypes[j].getMaxBondOrderSum()) {
                    foundMatchingAtomType = true;
                } else {
                    failedOn = atomTypes[j];
                }
            }
            if (foundMatchingAtomType) {
                report.addOK(checkBondSum);
            } else {
                if (failedOn != null) {
                    checkBondSum.setDetails(
                        "Bond order exceeds the one allowed for atom " +
                        atom.getSymbol() + " for which the maximum bond order is " +
                        failedOn.getMaxBondOrderSum()
                    );
                }
                report.addError(checkBondSum);
            }
        } catch (Exception exception) {
            System.err.println("Error while performing atom bos validation: " + exception.toString());
        }
        return report;
    }

    private static ValidationReport validateAtomCountConservation(Reaction reaction,
                                                        AtomContainer reactants,
                                                        AtomContainer products) {
        ValidationReport report = new ValidationReport();
        ValidationTest atomCount = new ValidationTest(reaction,
            "Atom count mismatch for reaction: the product side has a different atom count than the reactant side."
        );
        if (reactants.getAtomCount() != products.getAtomCount()) {
            report.addError(atomCount);
        } else {
            report.addOK(atomCount);
        }
        return report;
    }

    private static ValidationReport validateChargeConservation(Reaction reaction,
                                                     AtomContainer reactants,
                                                     AtomContainer products) {
        ValidationReport report = new ValidationReport();
        ValidationTest chargeConservation = new ValidationTest(reaction,
            "Total formal charge is not preserved during the reaction"
        );
        Atom[] atoms1 = reactants.getAtoms();
        int totalCharge1 = 0;
        for (int i=0;i<atoms1.length; i++) {
            totalCharge1 =+ atoms1[i].getFormalCharge();
        }
        Atom[] atoms2 = products.getAtoms();
        int totalCharge2 = 0;
        for (int i=0;i<atoms2.length; i++) {
            totalCharge2 =+ atoms2[i].getFormalCharge();
        }
        if (totalCharge1 != totalCharge2) {
            report.addError(chargeConservation);
        } else {
            report.addOK(chargeConservation);
        }
        return report;
    }
    
}

/* 
 * Copyright (C) 2003  University of Manchester
 *          2003-2007  The Chemistry Development Kit (CDK) Project
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *   (or see http://www.gnu.org/copyleft/lesser.html)
 */

package org.openscience.cdk.iupac.parser;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

import java.util.Iterator;
import java.util.List;

/**
 * Takes in parsed Tokens from NomParser and contains rules
 * to convert those tokens to a Molecule.
 *
 * @see Token
 * @author David Robinson
 * @cdk.githash
 * @author Bhupinder Sandhu
 * @author Stephen Tomkinson
 *
 * @cdk.require ant1.6
 */
public class MoleculeBuilder {

    /** The molecule which is worked upon throughout the class and returned at the end */
    private IAtomContainer currentMolecule = null;
    private IAtom          endOfChain;

    public MoleculeBuilder(IChemObjectBuilder builder) {
        currentMolecule = builder.newInstance(IAtomContainer.class);
    }

    /**
     * Instantiates a builder using the {@link DefaultChemObjectBuilder}.
     */
    public MoleculeBuilder() {
        this(DefaultChemObjectBuilder.getInstance());
    }

    /**
     * Builds the main chain which may act as a foundation for futher working groups.
     *
    * @param mainChain The parsed prefix which depicts the chain's length.
     * @param isMainCyclic A flag to show if the molecule is a ring. 0 means not a ring, 1 means is a ring.
     * @return A Molecule containing the requested chain.
     */
    private IAtomContainer buildChain(int length, boolean isMainCyclic) {
        IAtomContainer currentChain;
        if (length > 0) {
            //If is cyclic
            if (isMainCyclic) {
                //Rely on CDK's ring class constructor to generate our cyclic molecules.
                currentChain = currentMolecule.getBuilder().newInstance(IAtomContainer.class);
                currentChain.add(currentMolecule.getBuilder().newInstance(IRing.class, length, "C"));
            } //Else must not be cyclic
            else {
                currentChain = makeAlkane(length);
            }
        } else {
            currentChain = currentMolecule.getBuilder().newInstance(IAtomContainer.class);
        }

        return currentChain;
    }

    /**
     * Initiates the building of the molecules functional group(s).
     * Adds the functional group to atom 0 if only one group exists or runs
     * down the list of positions adding groups as required.
     *
     * @param attachedGroups A vector of AttachedGroup's representing functional groups.
     * @see #addFunGroup
     */
    private void buildFunGroups(List<AttachedGroup> attachedGroups) {
        Iterator<AttachedGroup> groupsIterator = attachedGroups.iterator();
        while (groupsIterator.hasNext()) {
            AttachedGroup attachedGroup = groupsIterator.next();

            Iterator<Token> locationsIterator = attachedGroup.getLocations().iterator();
            while (locationsIterator.hasNext()) {
                Token locationToken = locationsIterator.next();
                addFunGroup(attachedGroup.getName(), Integer.parseInt(locationToken.image) - 1);
            }
        }
    }

    /**
     * Adds a functional group to a given atom in the current molecule.
     *
     * @param funGroupToken The token which denotes this specific functional group.
     * @param addPos The atom to add the group to.
     */
    private void addFunGroup(String funGroupToken, int addPos) {
        //BOND MODIFICATION
        //Alkanes - Single bond
        if ("an".equals(funGroupToken)) {
            //Do nothing since all bonds are single by default.
        }
        //Alkenes - Double bond
        else if ("en".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                //Set the first bond to an order of 2 (i.e. a double bond)
                currentMolecule.getBond(0).setOrder(IBond.Order.DOUBLE);
            } else {
                //Set the addPos'th bond to an order of 2 (i.e. a double bond)
                currentMolecule.getBond(addPos).setOrder(IBond.Order.DOUBLE);
            }
        }
        //Alkynes - Tripple bond
        else if ("yn".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                //Set the first bond to an order of 3 (i.e. a tripple bond)
                currentMolecule.getBond(0).setOrder(IBond.Order.TRIPLE);
            } else {
                //Set the addPos'th bond to an order of 3 (i.e. a tripple bond)
                currentMolecule.getBond(addPos).setOrder(IBond.Order.TRIPLE);
            }
        }
        //FUNCTIONAL GROUP SUFFIXES
        //Ending "e"
        else if ("e".equals(funGroupToken)) {
            //Do nothing, since the "e" is found at the end of chain names
            //with a bond modifer but no functional groups.
        }
        //Alcohols
        else if ("ol".equals(funGroupToken) || "hydroxy".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("O", endOfChain, IBond.Order.SINGLE, 1);
            } else {
                addAtom("O", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 1);
            }
        }
        //Aldehydes
        else if ("al".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 0);
        }
        //Carboxylic acid
        else if ("oic acid".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 0);
            addAtom("O", endOfChain, IBond.Order.SINGLE, 1);
        }
        //Carboxylic Acid Chloride
        else if ("oyl chloride".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 0);
            addAtom("Cl", endOfChain, IBond.Order.SINGLE, 0);
        }
        //PREFIXES
        //Halogens
        //Chlorine
        else if ("chloro".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("Cl", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 0);
            } else {
                addAtom("Cl", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 0);
            }
        }
        //Fluorine
        else if ("fluoro".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("F", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 0);
            } else {
                addAtom("F", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 0);
            }
        }
        //Bromine
        else if ("bromo".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("Br", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 0);
            } else {
                addAtom("Br", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 0);
            }
        }
        //Iodine
        else if ("iodo".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("I", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 0);
            } else {
                addAtom("I", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 0);
            }
        }
        //Nitro
        else if ("nitro".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("N", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 0);
            } else {
                addAtom("N", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 0);
            }

            //Stuff which applied no matter where the N atom is:
            IAtom nitrogenAtom = currentMolecule.getAtom(currentMolecule.getAtomCount());
            nitrogenAtom.setFormalCharge(+1);
            addAtom("O", nitrogenAtom, IBond.Order.SINGLE, 0);
            currentMolecule.getAtom(currentMolecule.getAtomCount()).setFormalCharge(-1);
            addAtom("O", nitrogenAtom, IBond.Order.DOUBLE, 0);
        }
        //Oxo
        else if ("oxo".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("O", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.DOUBLE, 0);
            } else {
                addAtom("O", currentMolecule.getAtom(addPos), IBond.Order.DOUBLE, 0);
            }
        }
        //Nitrile
        else if ("nitrile".equals(funGroupToken)) {
            addAtom("N", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.TRIPLE, 0);
        }
        //Benzene
        else if ("phenyl".equals(funGroupToken)) {
            IAtomContainer benzene = makeBenzene();
            //Detect Aromacity in the benzene ring.
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(benzene);
                Aromaticity.cdkLegacy().apply(benzene);
            } catch (CDKException exc) {
                //                logger.debug("No atom detected");
            }
            currentMolecule.add(benzene);

            IBond joiningBond;
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                joiningBond = currentMolecule.getBuilder().newInstance(IBond.class, currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0),
                        benzene.getAtom(0));
            } else {
                joiningBond = currentMolecule.getBuilder().newInstance(IBond.class, currentMolecule.getAtom(addPos),
                        benzene.getAtom(0));
            }
            currentMolecule.addBond(joiningBond);
        } else if ("amino".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("N", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 2);
            } else {
                addAtom("N", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 2);
            }
        }
        //ORGANO METALLICS ADDED AS PREFIXES
        else if ("alumino".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("Al", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 2);
            } else {
                addAtom("Al", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 2);
            }
        } else if ("litho".equals(funGroupToken)) {
            //If functional group hasn't had a location specified:
            if (addPos < 0) {
                addAtom("Li", currentMolecule.isEmpty() ? null : currentMolecule.getAtom(0), IBond.Order.SINGLE, 2);
            } else {
                addAtom("Li", currentMolecule.getAtom(addPos), IBond.Order.SINGLE, 2);
            }
        }
        //PRIORITY SUBSTITUENTS

        //FUNCTIONAL GROUPS WHICH MAY HAVE THEIR OWN SUBSTITUENTS
        //Esters ("...oate")
        else if ("oate".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 0);
            addAtom("O", endOfChain, IBond.Order.SINGLE, 0);
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getAtom(currentMolecule.getAtomCount());
        }
        //Amines
        else if ("amine".equals(funGroupToken)) {
            addAtom("N", endOfChain, IBond.Order.SINGLE, 1);
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getAtom(currentMolecule.getAtomCount());
        }
        //Amides
        else if ("amide".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 0);
            addAtom("N", endOfChain, IBond.Order.SINGLE, 1);
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getAtom(currentMolecule.getAtomCount());
        }
        //Ketones
        else if ("one".equals(funGroupToken)) {
            addAtom("O", endOfChain, IBond.Order.DOUBLE, 2);
            //End of chain doesn't change in this case
        }
        //Organometals
        else if (getMetalAtomicSymbol(funGroupToken) != null) {
            currentMolecule.addAtom(currentMolecule.getBuilder().newInstance(IAtom.class,
                    getMetalAtomicSymbol(funGroupToken)));
            endOfChain = currentMolecule.getAtom(currentMolecule.getAtomCount());
        } else {
            //            logger.debug("Encountered unknown group: " + funGroupToken + " at " + addPos +
            //            "\nThe parser thinks this is valid but the molecule builder has no logic for it");
        }
    }

    /**
     * Translates a metal's name into it's atomic symbol.
     *
     * @param metalName The name of the metal, e.g. lead
     * @return The given metal's atomic symbol e.g. Pb or null if none exist.
     */
    String getMetalAtomicSymbol(String metalName) {
        if ("aluminium".equals(metalName)) {
            return "Al";
        } else if ("magnesium".equals(metalName)) {
            return "Mg";
        } else if ("gallium".equals(metalName)) {
            return "Ga";
        } else if ("indium".equals(metalName)) {
            return "In";
        } else if ("thallium".equals(metalName)) {
            return "Tl";
        } else if ("germanium".equals(metalName)) {
            return "Ge";
        } else if ("tin".equals(metalName)) {
            return "Sn";
        } else if ("lead".equals(metalName)) {
            return "Pb";
        } else if ("arsenic".equals(metalName)) {
            return "As";
        } else if ("antimony".equals(metalName)) {
            return "Sb";
        } else if ("bismuth".equals(metalName)) {
            return "Bi";
        }

        return null;
    }

    /**
     * Adds an atom to the current molecule.
     *
     * @param newAtomType The atomic symbol for the atom.
     * @param otherConnectingAtom An atom already in the molecule which
     * the new one should connect to.
     * @param bondOrder The order of the bond to use to join the two atoms.
     * @param hydrogenCount The number of hydrogen atoms connected to this atom.
     */
    private void addAtom(String newAtomType, IAtom otherConnectingAtom, Order bondOrder, int hydrogenCount) {
        //Create the new atom and bond.
        IAtom newAtom = currentMolecule.getBuilder().newInstance(IAtom.class, newAtomType);
        newAtom.setImplicitHydrogenCount(hydrogenCount);
        IBond newBond = currentMolecule.getBuilder().newInstance(IBond.class, newAtom, otherConnectingAtom, bondOrder);

        //Add the new atom and bond to the molecule.
        currentMolecule.addAtom(newAtom);
        currentMolecule.addBond(newBond);
    }

    /**
     * Adds other chains to the main chain connected at the specified atom.
     *
     * @param attachedSubstituents A vector of AttachedGroup's representing substituents.
     */
    private void addHeads(List<AttachedGroup> attachedSubstituents) {
        Iterator<AttachedGroup> substituentsIterator = attachedSubstituents.iterator();
        while (substituentsIterator.hasNext()) {
            AttachedGroup attachedSubstituent = substituentsIterator.next();

            Iterator<Token> locationsIterator = attachedSubstituent.getLocations().iterator();
            while (locationsIterator.hasNext()) {
                Token locationToken = locationsIterator.next();

                int joinLocation = Integer.parseInt(locationToken.image) - 1;
                IAtom connectingAtom;

                //If join location wasn't specified we must be dealing with the "hack" which makes
                //mainchains a substituent if a real substituent has already been parsed and interpreted as a main chain
                if (joinLocation < 0) {
                    connectingAtom = endOfChain;
                } else {
                    connectingAtom = currentMolecule.getAtom(joinLocation);
                }

                IAtomContainer subChain = buildChain(attachedSubstituent.getLength(), false);

                IBond linkingBond = currentMolecule.getBuilder().newInstance(IBond.class, subChain.getAtom(0),
                        connectingAtom);
                currentMolecule.addBond(linkingBond);
                currentMolecule.add(subChain);
            }
        }
    }

    /**
     * Start of the process of building a molecule from the parsed data. Passes the parsed
     * tokens to other functions which build up the Molecule.
     *
     * @param mainChain The string representation of the length of the main chain.
     * @param attachedSubstituents A vector of AttachedGroup's representing substituents.
     * @param attachedGroups A vector of AttachedGroup's representing functional groups.
     * @param isMainCyclic An indication of if the main chain is cyclic.
     * @return The molecule as built from the parsed tokens.
     */
    protected IAtomContainer buildMolecule(int mainChain, List<AttachedGroup> attachedSubstituents,
            List<AttachedGroup> attachedGroups, boolean isMainCyclic, String name) throws ParseException, CDKException {
        //Set up the molecle's name
        currentMolecule.setID(name);
        //Build the main chain
        currentMolecule.add(buildChain(mainChain, isMainCyclic));

        //Set the last atom here if a main chain has been built,
        //if not rely on the functional group setting one of it's atoms as last
        if (mainChain != 0) endOfChain = currentMolecule.getAtom(currentMolecule.getAtomCount()-1);

        //Add functional groups
        buildFunGroups(attachedGroups);

        //Add on further sub chains
        addHeads(attachedSubstituents);

        //Add the hydrogens to create a balanced molecule
        CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(currentMolecule.getBuilder());
        Iterator<IAtom> atoms = currentMolecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            IAtomType type = matcher.findMatchingAtomType(currentMolecule, atom);
            AtomTypeManipulator.configure(atom, type);
        }
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(currentMolecule.getBuilder());
        hAdder.addImplicitHydrogens(currentMolecule);

        return currentMolecule;
    }

    private static IAtomContainer makeBenzene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("C")); // 2
        mol.addAtom(new Atom("C")); // 3
        mol.addAtom(new Atom("C")); // 4
        mol.addAtom(new Atom("C")); // 5

        mol.addBond(0, 1, IBond.Order.SINGLE); // 1
        mol.addBond(1, 2, IBond.Order.DOUBLE); // 2
        mol.addBond(2, 3, IBond.Order.SINGLE); // 3
        mol.addBond(3, 4, IBond.Order.DOUBLE); // 4
        mol.addBond(4, 5, IBond.Order.SINGLE); // 5
        mol.addBond(5, 0, IBond.Order.DOUBLE); // 6
        return mol;
    }

    /**
     * Generate an Alkane (chain of carbons with no hydrogens) of a given length.
     *
     * <p>This method was written by Stephen Tomkinson.
     *
     * @param chainLength The number of carbon atoms to have in the chain.
     * @return A molecule containing a bonded chain of carbons.
     *
     * @cdk.created 2003-08-15
     */
    private static IAtomContainer makeAlkane(int chainLength) {
        IAtomContainer currentChain = new AtomContainer();

        //Add the initial atom
        currentChain.addAtom(new Atom("C"));

        //Add further atoms and bonds as needed, a pair at a time.
        for (int atomCount = 1; atomCount < chainLength; atomCount++) {
            currentChain.addAtom(new Atom("C"));
            currentChain.addBond(atomCount, atomCount - 1, IBond.Order.SINGLE);
        }

        return currentChain;
    }
}

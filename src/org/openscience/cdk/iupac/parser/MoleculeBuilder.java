/*  $RCSfile$
 *  $Author$  
 *  $Date$  
 *  $Revision$
 *
 *   Copyright (C) 2003  University of Manchester
 *   Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) Project
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

import java.util.Iterator;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * Takes in parsed Tokens from NomParser and contains rules
 * to convert those tokens to a Molecule.
 *
 * @see Token
 * @author David Robinson
 * @author Bhupinder Sandhu
 * @author Stephen Tomkinson
 *
 * @cdk.require ant1.6
 */
public class MoleculeBuilder
{
    /** The molecule which is worked upon throughout the class and returned at the end */
    private Molecule currentMolecule = new Molecule();
    private org.openscience.cdk.interfaces.IAtom endOfChain;
        
    /**
     * Builds the main chain which may act as a foundation for futher working groups.
     *
     * @param mainChain The parsed prefix which depicts the chain's length.
     * @param isMainCyclic A flag to show if the molecule is a ring. 0 means not a ring, 1 means is a ring.
     * @return A Molecule containing the requested chain.
     */
    private Molecule buildChain(int length, boolean isMainCyclic)
    {
        Molecule currentChain;
        if (length > 0)
        {
            //If is cyclic
            if (isMainCyclic)
            {
                //Rely on CDK's ring class constructor to generate our cyclic molecules.
                currentChain = new Molecule();
                currentChain.add(new Ring(length, "C"));
            } //Else must not be cyclic
            else
            {
                currentChain = MoleculeFactory.makeAlkane(length);
            }
        }
        else
        {
            currentChain = new Molecule();
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
    private void buildFunGroups(Vector attachedGroups)
    {
        Iterator groupsIterator = attachedGroups.iterator();
        while (groupsIterator.hasNext())
        {
            AttachedGroup attachedGroup = (AttachedGroup) groupsIterator.next();
            
            Iterator locationsIterator = attachedGroup.getLocations().iterator();
            while (locationsIterator.hasNext())
            {
                Token locationToken = (Token) locationsIterator.next();
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
    private void addFunGroup(String funGroupToken, int addPos)
    {
        //BOND MODIFICATION
        //Alkanes - Single bond
        if (funGroupToken == "an")
        {
            //Do nothing since all bonds are single by default.
        }
        //Alkenes - Double bond
        else if (funGroupToken == "en")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                //Set the first bond to an order of 2 (i.e. a double bond)
                currentMolecule.getBond(0).setOrder(2.0);
            }
            else
            {
                //Set the addPos'th bond to an order of 2 (i.e. a double bond)
                currentMolecule.getBond(addPos).setOrder(2.0);
            }
        }
        //Alkynes - Tripple bond
        else if (funGroupToken == "yn")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                //Set the first bond to an order of 3 (i.e. a tripple bond)
                currentMolecule.getBond(0).setOrder(3.0);
            }
            else
            {
                //Set the addPos'th bond to an order of 3 (i.e. a tripple bond)
                currentMolecule.getBond(addPos).setOrder(3.0);
            }
        }
        //FUNCTIONAL GROUP SUFFIXES
        //Ending "e"
        else if (funGroupToken == "e")
        {
            //Do nothing, since the "e" is found at the end of chain names
            //with a bond modifer but no functional groups.
        }
        //Alcohols
        else if (funGroupToken == "ol" || funGroupToken == "hydroxy")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("O", endOfChain, 1.0, 1);
            }
            else
            {
                addAtom("O", currentMolecule.getAtom(addPos), 1.0, 1);
            }
        }
        //Aldehydes
        else if (funGroupToken == "al")
        {
            addAtom("O", endOfChain, 2.0, 0);
        }
        //Carboxylic acid
        else if (funGroupToken == "oic acid")
        {
            addAtom("O", endOfChain, 2.0, 0);
            addAtom("O", endOfChain, 1.0, 1);
        }
        //Carboxylic Acid Chloride
        else if (funGroupToken == "oyl chloride")
        {
            addAtom("O", endOfChain, 2.0, 0);
            addAtom("Cl", endOfChain, 1.0, 0);
        }
        //PREFIXES
        //Halogens
        //Chlorine
        else if (funGroupToken == "chloro")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("Cl", currentMolecule.getFirstAtom(), 1.0, 0);
            }
            else
            {
                addAtom("Cl", currentMolecule.getAtom(addPos), 1.0, 0);
            }
        }
        //Fluorine
        else if (funGroupToken == "fluoro")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("F", currentMolecule.getFirstAtom(), 1.0, 0);
            }
            else
            {
                addAtom("F", currentMolecule.getAtom(addPos), 1.0, 0);
            }
        }
        //Bromine
        else if (funGroupToken == "bromo")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("Br", currentMolecule.getFirstAtom(), 1.0, 0);
            }
            else
            {
                addAtom("Br", currentMolecule.getAtom(addPos), 1.0, 0);
            }
        }
        //Iodine
        else if (funGroupToken == "iodo")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("I", currentMolecule.getFirstAtom(), 1.0, 0);
            }
            else
            {
                addAtom("I", currentMolecule.getAtom(addPos), 1.0, 0);
            }
        }
        //Nitro
        else if (funGroupToken == "nitro")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("N", currentMolecule.getFirstAtom(), 1.0, 0);
            }
            else
            {
                addAtom("N", currentMolecule.getAtom(addPos), 1.0, 0);
            }
            
            //Stuff which applied no matter where the N atom is:
            org.openscience.cdk.interfaces.IAtom nitrogenAtom = currentMolecule.getLastAtom();
            nitrogenAtom.setFormalCharge(+1);
            addAtom("O", nitrogenAtom, 1.0, 0);
            currentMolecule.getLastAtom().setFormalCharge(-1);
            addAtom("O", nitrogenAtom, 2.0, 0);
        }
        //Oxo
        else if (funGroupToken == "oxo")
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("O", currentMolecule.getFirstAtom(), 2.0, 0);
            }
            else
            {
                addAtom("O", currentMolecule.getAtom(addPos), 2.0, 0);
            }
        }
        //Nitrile
        else if (funGroupToken == "nitrile" )
        {
            addAtom("N", currentMolecule.getFirstAtom(), 3.0, 0);
        }
        //Benzene
        else if (funGroupToken == "phenyl" )
        {
            Molecule benzene = MoleculeFactory.makeBenzene();
            //Detect Aromacity in the benzene ring.
            try
            {
                HueckelAromaticityDetector.detectAromaticity(benzene);
            }
            catch (Exception exc)
            {
//                logger.debug("No atom detected");
            }
            currentMolecule.add(benzene);
            
            Bond joiningBond;
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                joiningBond = new Bond(currentMolecule.getFirstAtom(), benzene.getFirstAtom());
            }
            else
            {
                joiningBond = new Bond(currentMolecule.getAtom(addPos), benzene.getFirstAtom());
            }
            currentMolecule.addBond(joiningBond);
        }
        else if (funGroupToken == "amino" )
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("N", currentMolecule.getFirstAtom(), 1.0, 2);
            }
            else
            {
                addAtom("N", currentMolecule.getAtom(addPos), 1.0, 2);
            }
        }
        //ORGANO METALLICS ADDED AS PREFIXES
        else if (funGroupToken == "alumino" )
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("Al", currentMolecule.getFirstAtom(), 1.0, 2);
            }
            else
            {
                addAtom("Al", currentMolecule.getAtom(addPos), 1.0, 2);
            }
        }
        else if (funGroupToken == "litho" )
        {
            //If functional group hasn't had a location specified:
            if (addPos < 0)
            {
                addAtom("Li", currentMolecule.getFirstAtom(), 1.0, 2);
            }
            else
            {
                addAtom("Li", currentMolecule.getAtom(addPos), 1.0, 2);
            }
        }
        //PRIORITY SUBSTITUENTS

        //FUNCTIONAL GROUPS WHICH MAY HAVE THEIR OWN SUBSTITUENTS
        //Esters ("...oate")
        else if (funGroupToken == "oate")
        {
            addAtom("O", endOfChain, 2.0, 0);
            addAtom("O", endOfChain, 1.0, 0);
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getLastAtom();
        }
        //Amines
        else if (funGroupToken == "amine")
        {
            addAtom("N", endOfChain, 1.0, 1);            
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getLastAtom();
        }
        //Amides
        else if (funGroupToken =="amide")
        {
            addAtom("O", endOfChain, 2.0, 0);
            addAtom("N", endOfChain, 1.0, 1);
            //Set the end of the chain to be built on for unspecified substituents.
            endOfChain = currentMolecule.getLastAtom();
        }
        //Ketones
        else if (funGroupToken == "one")
        {
            addAtom("O", endOfChain, 2.0, 2);
            //End of chain doesn't change in this case
        }
        //Organometals
        else if (getMetalAtomicSymbol (funGroupToken) != null)
        {
            currentMolecule.addAtom (new Atom (getMetalAtomicSymbol (funGroupToken)));
            endOfChain = currentMolecule.getLastAtom();
        }
        else
        {
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
    String getMetalAtomicSymbol (String metalName)
    {
        if (metalName == "aluminium")
        {
            return "Al";
        }
        else if (metalName == "magnesium" )
        {
            return "Mg";
        }
        else if (metalName == "gallium")
        {
            return "Ga";
        }
        else if (metalName == "indium")
        {
            return "In";
        }
        else if (metalName == "thallium")
        {
            return "Tl";
        }
        else if (metalName == "germanium")
        {
            return "Ge";
        }
        else if (metalName == "tin")
        {
            return "Sn";
        }
        else if (metalName == "lead")
        {
            return "Pb";
        }
        else if (metalName == "arsenic")
        {
            return "As";
        }
        else if (metalName == "antimony")
        {
            return "Sb";
        }
        else if (metalName == "bismuth")
        {
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
    private void addAtom(String newAtomType, org.openscience.cdk.interfaces.IAtom otherConnectingAtom, double bondOrder, int hydrogenCount)
    {
        //Create the new atom and bond.
        Atom newAtom = new Atom(newAtomType);
        newAtom.setHydrogenCount(hydrogenCount);
        Bond newBond = new Bond(newAtom, otherConnectingAtom, bondOrder);
        
        //Add the new atom and bond to the molecule.
        currentMolecule.addAtom(newAtom);
        currentMolecule.addBond(newBond);
    }
    
    /**
     * Adds other chains to the main chain connected at the specified atom.
     *
     * @param attachedSubstituents A vector of AttachedGroup's representing substituents.
     */
    private void addHeads(Vector attachedSubstituents)
    {
        Iterator substituentsIterator = attachedSubstituents.iterator();
        while (substituentsIterator.hasNext())
        {
            AttachedGroup attachedSubstituent = (AttachedGroup) substituentsIterator.next();
            
            Iterator locationsIterator = attachedSubstituent.getLocations().iterator();
            while (locationsIterator.hasNext())
            {
                Token locationToken = (Token) locationsIterator.next();
                
                int joinLocation = Integer.parseInt(locationToken.image) - 1;
                org.openscience.cdk.interfaces.IAtom connectingAtom;
                
                //If join location wasn't specified we must be dealing with the "hack" which makes
                //mainchains a substituent if a real substituent has already been parsed and interpreted as a main chain
                if (joinLocation < 0)
                {
                    connectingAtom = endOfChain;
                }
                else
                {
                    connectingAtom = currentMolecule.getAtom(joinLocation);
                }
                
                Molecule subChain = buildChain(attachedSubstituent.getLength(), false);
                
                Bond linkingBond = new Bond(subChain.getFirstAtom(), connectingAtom);
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
     * @param isMainCyclic An indiacation of if the main chain is cyclic.
     * @return The molecule as built from the parsed tokens.
     */
    protected Molecule buildMolecule(int mainChain, Vector attachedSubstituents
    , Vector attachedGroups, boolean isMainCyclic, String name) throws
    ParseException, CDKException
    {
        //Set up the molecle's name
        currentMolecule.setID(name);
        //Build the main chain
        currentMolecule.add(buildChain(mainChain,isMainCyclic));
        
        //Set the last atom here if a main chain has been built, 
        //if not rely on the functional group setting one of it's atoms as last
        if (mainChain != 0) endOfChain = currentMolecule.getLastAtom();
        
        //Add functional groups
        buildFunGroups(attachedGroups);
        
        //Add on further sub chains
        addHeads(attachedSubstituents);
        
        //Add the hydrogens to create a balanced molecule
        HydrogenAdder adder = new HydrogenAdder();
        adder.addImplicitHydrogensToSatisfyValency(currentMolecule);
                
        return currentMolecule;
    }
}

/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.smsd.tools;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Class that cleans a molecule before MCS search.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class MoleculeSanityCheck {

    /**
     * Modules for cleaning a molecule
     * @param molecule
     * @return cleaned AtomContainer
     */
    public static IAtomContainer checkAndCleanMolecule(IAtomContainer molecule) {
        boolean isMarkush = false;
        for (IAtom atom : molecule.atoms()) {
            if (atom.getSymbol().equals("R")) {
                isMarkush = true;
                break;
            }
        }

        if (isMarkush) {
            System.err.println("Skipping Markush structure for sanity check");
        }

        // Check for salts and such
        if (!ConnectivityChecker.isConnected(molecule)) {
            // lets see if we have just two parts if so, we assume its a salt and just work
            // on the larger part. Ideally we should have a check to ensure that the smaller
            //  part is a metal/halogen etc.
            IAtomContainerSet fragments = ConnectivityChecker.partitionIntoMolecules(molecule);
            if (fragments.getAtomContainerCount() > 2) {
                System.err.println("More than 2 components. Skipped");
            } else {
                IAtomContainer frag1 = fragments.getAtomContainer(0);
                IAtomContainer frag2 = fragments.getAtomContainer(1);
                if (frag1.getAtomCount() > frag2.getAtomCount()) {
                    molecule = frag1;
                } else {
                    molecule = frag2;
                }
            }
        }
        configure(molecule);
        return molecule;
    }

    /**
     * Fixes Aromaticity of the molecule
     * i.e. need to find rings and aromaticity again since added H's
     * @param mol
     */
    public static void configure(IAtomContainer mol) {
        // need to find rings and aromaticity again since added H's

        IRingSet ringSet = null;
        try {
            AllRingsFinder arf = new AllRingsFinder();
            ringSet = arf.findAllRings(mol);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // figure out which atoms are in aromatic rings:
            CDKHydrogenAdder cdk = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
            ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            cdk.addImplicitHydrogens(mol);

            Aromaticity.cdkLegacy().apply(mol);
            // figure out which rings are aromatic:
            RingSetManipulator.markAromaticRings(ringSet);
            // figure out which simple (non cycles) rings are aromatic:

            // only atoms in 6 membered rings are aromatic
            // determine largest ring that each atom is a part of

            for (int i = 0; i < mol.getAtomCount(); i++) {
                mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
                jloop: for (int j = 0; j < ringSet.getAtomContainerCount(); j++) {
                    //logger.debug(i+"\t"+j);
                    IRing ring = (IRing) ringSet.getAtomContainer(j);
                    if (!ring.getFlag(CDKConstants.ISAROMATIC)) {
                        continue jloop;
                    }
                    boolean haveatom = ring.contains(mol.getAtom(i));
                    //logger.debug("haveatom="+haveatom);
                    if (haveatom && ring.getAtomCount() == 6) {
                        mol.getAtom(i).setFlag(CDKConstants.ISAROMATIC, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

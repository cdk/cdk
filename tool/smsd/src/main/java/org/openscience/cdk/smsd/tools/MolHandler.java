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

//~--- JDK imports ------------------------------------------------------------
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.smsd.labelling.CanonicalLabellingAdaptor;
import org.openscience.cdk.smsd.labelling.ICanonicalMoleculeLabeller;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Class that handles molecules for MCS search.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 */
public class MolHandler {

    private IAtomContainer             atomContainer  = null;
    private boolean                    removeHydrogen = false;
    private final ILoggingTool         logger         = LoggingToolFactory.createLoggingTool(MolHandler.class);
    private ICanonicalMoleculeLabeller canonLabeler   = new CanonicalLabellingAdaptor();

    /**
     * Creates a new instance of MolHandler
     * @param molFile atomContainer file name
     * @param cleanMolecule
     * @param removeHydrogen
     *
     */
    public MolHandler(String molFile, boolean removeHydrogen, boolean cleanMolecule) {

        MDLReader molRead = null;
        this.removeHydrogen = removeHydrogen;
        try {
            FileInputStream readMolecule = null;

            readMolecule = new FileInputStream(molFile);
            molRead = new MDLReader(new InputStreamReader(readMolecule));
            this.atomContainer = (IAtomContainer) molRead.read(new AtomContainer());
            molRead.close();
            readMolecule.close();
            /* Remove Hydrogen by Asad */
            if (removeHydrogen) {
                atomContainer = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(atomContainer);
            }
            if (cleanMolecule) {

                if (!isPseudoAtoms()) {
                    atomContainer = canonLabeler.getCanonicalMolecule(atomContainer);
                }
                // percieve atoms, set valency etc
                ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
                //Add implicit Hydrogens
                CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(atomContainer.getBuilder());
                adder.addImplicitHydrogens(atomContainer);
                // figure out which atoms are in aromatic rings:
                Aromaticity.cdkLegacy().apply(atomContainer);
                BondTools.makeUpDownBonds(atomContainer);
            }
        } catch (IOException ex) {
            logger.debug(ex);
        } catch (CDKException e) {
            System.err.println(e);
        } finally {
            if (molRead != null) {
                try {
                    molRead.close();
                } catch (IOException ioe) {
                    logger.warn("Couldn't close molReader: ", ioe.getMessage());
                    logger.debug(ioe);
                }
            }
        }
    }

    /**
     * Creates a new instance of MolHandler
     * @param container Molecule AtomContainer
     * @param cleanMolecule
     * @param removeHydrogen
     */
    public MolHandler(IAtomContainer container, boolean removeHydrogen, boolean cleanMolecule) {
        String molID = container.getID();
        this.removeHydrogen = removeHydrogen;
        this.atomContainer = container;
        if (removeHydrogen) {
            try {
                this.atomContainer = ExtAtomContainerManipulator
                        .removeHydrogensExceptSingleAndPreserveAtomID(atomContainer);
            } catch (Exception ex) {
                logger.error(ex);
            }
        } else {
            this.atomContainer = container.getBuilder().newInstance(IAtomContainer.class, atomContainer);
        }

        if (cleanMolecule) {
            try {
                if (!isPseudoAtoms()) {
                    atomContainer = canonLabeler.getCanonicalMolecule(atomContainer);
                }
                // percieve atoms, set valency etc
                ExtAtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
                //Add implicit Hydrogens
                CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(atomContainer.getBuilder());
                adder.addImplicitHydrogens(atomContainer);
                // figure out which atoms are in aromatic rings:
                Aromaticity.cdkLegacy().apply(atomContainer);
            } catch (CDKException ex) {
                logger.error(ex);
            }
        }
        atomContainer.setID(molID);
    }

    /**
     * Returns the modified container
     * @return get processed / modified container
     */
    public IAtomContainer getMolecule() {
        return atomContainer;
    }

    /**
     * Returns true if hydrogens were made implicit else return false
     * @return true if remove H else false
     */
    public boolean getRemoveHydrogenFlag() {
        return removeHydrogen;
    }

    private boolean isPseudoAtoms() {
        for (IAtom atoms : atomContainer.atoms()) {
            if (atoms instanceof IPseudoAtom) {
                return true;
            }
        }
        return false;
    }
}

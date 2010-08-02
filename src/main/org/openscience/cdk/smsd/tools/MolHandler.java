/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
import java.util.logging.Level;
//~--- non-JDK imports --------------------------------------------------------
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Class that handles molecules for MCS search.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.helper.MolHandlerTest")
public class MolHandler {

    private IAtomContainer atomContainer = null;
    private IAtomContainerSet fragmentMolSet = null;
    private boolean removeHydrogen = false;
    private boolean connectedFlag = false;
    private final static ILoggingTool Logger =
            LoggingToolFactory.createLoggingTool(MolHandler.class);

    private void checkFragmentation() {

        if (atomContainer.getAtomCount() > 0) {
            connectedFlag = ConnectivityChecker.isConnected(atomContainer);
        }
        fragmentMolSet = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainerSet.class);

        if (!connectedFlag) {
            fragmentMolSet.add(ConnectivityChecker.partitionIntoMolecules(atomContainer));
            fragmentMolSet.setID(atomContainer.getID());

        } else {
            fragmentMolSet.addAtomContainer(atomContainer);
            fragmentMolSet.setID(atomContainer.getID());
        }
    }

    /** 
     * Creates a new instance of MolHandler
     * @param MolFile atomContainer file name
     * @param cleanMolecule
     * @param removeHydrogen
     *
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(String MolFile, boolean cleanMolecule, boolean removeHydrogen) {

        MDLReader molRead = null;
        this.removeHydrogen = removeHydrogen;
        try {
            FileInputStream readMolecule;

            readMolecule = new FileInputStream(MolFile);
            molRead = new MDLReader(new InputStreamReader(readMolecule));
            this.atomContainer = (IMolecule) molRead.read(new Molecule());
            if (cleanMolecule) {
                MoleculeSanityCheck.fixAromaticity((IMolecule) atomContainer);
            }
            BondTools.makeUpDownBonds(atomContainer);
            /*Remove Hydrogen by Asad*/
            if (removeHydrogen) {
                atomContainer = ExtAtomContainerManipulator.removeHydrogens(atomContainer);
            }
            checkFragmentation();
        } catch (IOException ex) {
            Logger.error(Level.SEVERE, null, ex);
        } catch (CDKException e) {
            System.err.println(e);
        } finally {
        	if (molRead != null) {
                try {
                    molRead.close();
                } catch (IOException ioe) {
                    Logger.warn("Couldn't close molReader: ", ioe.getMessage());
                    Logger.debug(ioe);
                }
            }
        }
    }

    /**
     * Creates a new instance of MolHandler
     * @param MolFile
     * @param cleanMolecule
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(String MolFile, boolean cleanMolecule) {

        MDLReader molRead = null;
        this.removeHydrogen = false;


        try {
            FileInputStream ReadMolecule;

            ReadMolecule = new FileInputStream(MolFile);
            molRead = new MDLReader(new InputStreamReader(ReadMolecule));
            this.atomContainer = (IMolecule) molRead.read(new Molecule());
            if (cleanMolecule) {
                MoleculeSanityCheck.fixAromaticity(atomContainer);
            }
            BondTools.makeUpDownBonds(atomContainer);
            /*Remove Hydrogen by Asad*/
            if (removeHydrogen) {
                atomContainer = ExtAtomContainerManipulator.removeHydrogens(atomContainer);
            }

            checkFragmentation();

        } catch (IOException ex) {
            Logger.error(Level.SEVERE, null, ex);
        } catch (CDKException e) {
            System.err.println(e);
        } finally {
            if (molRead != null) {
                try {
                    molRead.close();
                } catch (IOException ioe) {
                    Logger.warn("Couldn't close molReader: ", ioe.getMessage());
                    Logger.debug(ioe);
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
    @TestMethod("MolHandlerTest")
    public MolHandler(IAtomContainer container, boolean cleanMolecule, boolean removeHydrogen) {

        String molID = container.getID();
        this.removeHydrogen = removeHydrogen;
        this.atomContainer = container;
        if (cleanMolecule) {
            MoleculeSanityCheck.fixAromaticity(atomContainer);
        }  /*Hydrogen are always removed for this container before mapping*/

        if (removeHydrogen) {
            try {
                this.atomContainer = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(atomContainer);
                atomContainer.setID(molID);
            } catch (Exception ex) {
                Logger.error(Level.SEVERE, null, ex);
            }

        } else {
            this.atomContainer = container.getBuilder().newInstance(IAtomContainer.class, atomContainer);
            atomContainer.setID(molID);
        }
        checkFragmentation();
    }

    /**
     * Creates a new instance of MolHandler
     * @param container
     * @param cleanMolecule
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(IAtomContainer container, boolean cleanMolecule) {

        String molID = container.getID();
        this.removeHydrogen = false;
        this.atomContainer = container;
        if (cleanMolecule) {
            MoleculeSanityCheck.fixAromaticity(atomContainer);
        }  /*Hydrogen are always removed for this container before mapping*/

        if (removeHydrogen) {
            try {
                this.atomContainer = ExtAtomContainerManipulator.removeHydrogensExceptSingleAndPreserveAtomID(atomContainer);
                atomContainer.setID(molID);
            } catch (Exception ex) {
                Logger.error(Level.SEVERE, null, ex);
            }
        } else {
            this.atomContainer = container.getBuilder().newInstance(IAtomContainer.class, atomContainer);
            atomContainer.setID(molID);
        }
        checkFragmentation();
    }

    /**
     * Returns the modified container
     * @return get processed / modified container
     */
    @TestMethod("testGetMolecule")
    public IAtomContainer getMolecule() {
        return atomContainer;
    }

    /**
     * Returns true if hydrogens were made implicit else return false
     * @return true if remove H else false
     */
    @TestMethod("testGetRemoveHydrogenFlag")
    public boolean getRemoveHydrogenFlag() {
        return removeHydrogen;
    }

    /**
     * Returns Fragmented container if getConnectedFlag was false
     * @return AtomContainer Set
     */
    @TestMethod("testGetFragmentedMolecule")
    public IAtomContainerSet getFragmentedMolecule() {
        return this.fragmentMolSet;
    }

    /**
     * Returns true is container is not fragmented else false
     * @return true is atomContainer is connected else false
     */
    @TestMethod("testGetConnectedFlag")
    public boolean getConnectedFlag() {
        return this.connectedFlag;
    }
}

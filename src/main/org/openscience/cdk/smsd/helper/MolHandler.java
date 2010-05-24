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
package org.openscience.cdk.smsd.helper;

//~--- JDK imports ------------------------------------------------------------
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
//~--- non-JDK imports --------------------------------------------------------
import org.openscience.cdk.smsd.tools.ExtAtomContainerManipulator;
import org.openscience.cdk.smsd.tools.MoleculeSanityCheck;
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
import org.openscience.cdk.interfaces.IMoleculeSet;
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

    private IAtomContainer mol = null;
    private IAtomContainerSet fragmentMolSet = null;
    private boolean removeHydrogen = false;
    private boolean connectedFlag = false;
    private final static ILoggingTool Logger =
            LoggingToolFactory.createLoggingTool(MolHandler.class);

    private void checkFragmentation() {

        if (mol.getAtomCount() > 0) {
            connectedFlag = ConnectivityChecker.isConnected(mol);
        }
        fragmentMolSet = DefaultChemObjectBuilder.getInstance().newInstance(IMoleculeSet.class);

        if (!connectedFlag) {
            fragmentMolSet.add(ConnectivityChecker.partitionIntoMolecules(mol));
            fragmentMolSet.setID(mol.getID());

        } else {
            fragmentMolSet.addAtomContainer(mol);
            fragmentMolSet.setID(mol.getID());
        }
    }

    /** 
     * Creates a new instance of MolHandler
     * @param MolFile mol file name
     * @param cleanMolecule
     * @param removeHydrogen
     *
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(String MolFile, boolean cleanMolecule, boolean removeHydrogen) {

        MDLReader molRead;
        this.removeHydrogen = removeHydrogen;
        try {
            FileInputStream readMolecule;

            readMolecule = new FileInputStream(MolFile);
            molRead = new MDLReader(new InputStreamReader(readMolecule));
            this.mol = (IMolecule) molRead.read(new Molecule());
            if (cleanMolecule) {
                MoleculeSanityCheck.fixAromaticity((IMolecule) mol);
            }
            BondTools.makeUpDownBonds(mol);
            /*Remove Hydrogen by Asad*/
            if (removeHydrogen) {
                mol = ExtAtomContainerManipulator.removeHydrogens(mol);
            }
            checkFragmentation();
        } catch (IOException ex) {
            Logger.error(Level.SEVERE, null, ex);
        } catch (CDKException e) {
            System.err.println(e);
        }
    }

    /**
     * Creates a new instance of MolHandler
     * @param MolFile
     * @param cleanMolecule
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(String MolFile, boolean cleanMolecule) {

        MDLReader molRead;
        this.removeHydrogen = false;


        try {
            FileInputStream ReadMolecule;

            ReadMolecule = new FileInputStream(MolFile);
            molRead = new MDLReader(new InputStreamReader(ReadMolecule));
            this.mol = (IMolecule) molRead.read(new Molecule());
            if (cleanMolecule) {
                MoleculeSanityCheck.fixAromaticity((IMolecule) mol);
            }
            BondTools.makeUpDownBonds(mol);
            /*Remove Hydrogen by Asad*/
            if (removeHydrogen) {
                mol = ExtAtomContainerManipulator.removeHydrogens(mol);

            }

            checkFragmentation();

        } catch (IOException ex) {
            Logger.error(Level.SEVERE, null, ex);
        } catch (CDKException e) {
            System.err.println(e);
        }
    }

    /**
     * Creates a new instance of MolHandler
     * @param molecule Molecule AtomContainer
     * @param cleanMolecule
     * @param removeHydrogen
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(IAtomContainer molecule, boolean cleanMolecule, boolean removeHydrogen) {

        String molID = molecule.getID();
        this.removeHydrogen = removeHydrogen;
        this.mol = molecule;
        if (cleanMolecule) {
            MoleculeSanityCheck.fixAromaticity((IMolecule) mol);
        }  /*Hydrogen are always removed for this molecule before mapping*/

        if (removeHydrogen) {
            try {
                this.mol = (IMolecule) ExtAtomContainerManipulator.removeHydrogensAndPreserveAtomID(mol);
                mol.setID(molID);
            } catch (Exception ex) {
                Logger.error(Level.SEVERE, null, ex);
            }

        } else {
            this.mol = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, mol);
            mol.setID(molID);

        }
        checkFragmentation();
    }

    /**
     * Creates a new instance of MolHandler
     * @param molecule
     * @param cleanMolecule
     */
    @TestMethod("MolHandlerTest")
    public MolHandler(IAtomContainer molecule, boolean cleanMolecule) {

        String molID = molecule.getID();
        this.removeHydrogen = false;
        this.mol = molecule;
        if (cleanMolecule) {
            MoleculeSanityCheck.fixAromaticity((IMolecule) mol);
        }  /*Hydrogen are always removed for this molecule before mapping*/

        if (removeHydrogen) {
            try {
                this.mol = (IMolecule) ExtAtomContainerManipulator.removeHydrogensAndPreserveAtomID(mol);
                mol.setID(molID);
            } catch (Exception ex) {
                Logger.error(Level.SEVERE, null, ex);
            }
        } else {
            this.mol = DefaultChemObjectBuilder.getInstance().newInstance(IMolecule.class, mol);
            mol.setID(molID);
        }
        checkFragmentation();
    }

    /**
     * Returns the modified molecule
     * @return get processed / modified molecule
     */
    @TestMethod("testGetMolecule")
    public IAtomContainer getMolecule() {
        return mol;
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
     * Returns Fragmented molecule if getConnectedFlag was false
     * @return AtomContainer Set
     */
    @TestMethod("testGetFragmentedMolecule")
    public IAtomContainerSet getFragmentedMolecule() {
        return this.fragmentMolSet;
    }

    /**
     * Returns true is molecule is not fragmented else false
     * @return true is mol is connected else false
     */
    @TestMethod("testGetConnectedFlag")
    public boolean getConnectedFlag() {
        return this.connectedFlag;
    }
}

/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.layout.HydrogenPlacer;
import org.openscience.cdk.geometry.GeometryTools;
import java.util.Vector;
import java.io.*;

/**
 * Provides methods for adding missing hydrogen atoms.
 *
 * <p>An example:
 * <pre>
 *   Molecule methane = new Molecule();
 *   Atom carbon = new Atom("C");
 *   methane.addAtom(carbon);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addImplicitHydrogensToSatisfyValency(methane);
 *   int atomCount = methane.getAtomCount(); // = 1
 * </pre>
 * As the example shows, this only adjusts the hydrogenCount
 * on the carbon.
 *
 * <p>If you want to add the hydrogens as separate atoms, you
 * need to do:
 * <pre>
 *   Molecule methane = new Molecule();
 *   Atom carbon = new Atom("C");
 *   methane.addAtom(carbon);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addExplicitHydrogensToSatisfyValency(methane);
 *   int atomCount = methane.getAtomCount(); // = 5
 * </pre>
 *
 * <p>If you want to add the hydrogens to a specific atom only,
 * use this example:
 * <pre>
 *   Molecule ethane = new Molecule();
 *   Atom carbon1 = new Atom("C");
 *   Atom carbon2 = new Atom("C");
 *   ethane.addAtom(carbon1);
 *   ethane.addAtom(carbon2);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addExplicitHydrogensToSatisfyValency(ethane, carbon1);
 *   int atomCount = ethane.getAtomCount(); // = 5
 * </pre>
 *
 * @keyword    hydrogen, adding
 */
public class HydrogenAdder {

    private LoggingTool logger;
    private SaturationChecker satChecker;

    /**
     * Creates a tool to add missing hydrogens.
     */
    public HydrogenAdder() {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        try {
            satChecker = new SaturationChecker();
        } catch (Exception exception) {
            logger.error("Could not intantiate a SaturationChecker.");
            logger.debug(exception);
        }
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     *
     * @param  molecule  Molecule to saturate
     * @keyword          hydrogen, adding
     * @keyword          explicit hydrogen
     */
    public void addHydrogensToSatisfyValency(Molecule molecule) throws IOException, ClassNotFoundException
    {
        addExplicitHydrogensToSatisfyValency(molecule);
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     *
     * @param  molecule  Molecule to saturate
     * @keyword          hydrogen, adding
     * @keyword          explicit hydrogen
     */
    public void addExplicitHydrogensToSatisfyValency(Molecule molecule) throws IOException, ClassNotFoundException
    {
        Atom[] atoms = molecule.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            addHydrogensToSatisfyValency(molecule, atoms[i]);
        }
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     *
     * @keyword          hydrogen, adding
     * @keyword          explicit hydrogen
     *
     * @deprecated
     */
    public void addHydrogensToSatisfyValency(AtomContainer container, Atom atom) 
        throws IOException, ClassNotFoundException
    {
        addExplicitHydrogensToSatisfyValency(container, atom);
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     *
     * @keyword          hydrogen, adding
     * @keyword          explicit hydrogen
     */
    public void addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom) 
        throws IOException, ClassNotFoundException
    {
        boolean create2DCoordinates = GeometryTools.has2DCoordinates(container);
        boolean create3DCoordinates = GeometryTools.has3DCoordinates(container);
        
        Isotope isotope = IsotopeFactory.getInstance().getMajorIsotope("H");
        atom.setHydrogenCount(0);
        // set number of implicit hydrogens to zero
        // add explicit hydrogens
        int missingHydrogens = satChecker.calculateMissingHydrogen(atom, container);
        for (int i = 1; i <= missingHydrogens; i++) {
            Atom hydrogen = new Atom("H");
            IsotopeFactory.getInstance().configure(hydrogen, isotope);
            container.addAtom(hydrogen);
            Bond newBond = new Bond(atom, hydrogen, 1.0);
            container.addBond(newBond);
        }
        // now create coordinates for new hydrogens
        if (create2DCoordinates) {
            logger.debug("Creating 2D coordinates for new hydrogens");
            HydrogenPlacer.placeHydrogens2D(container);
        }
    }
    
    /**
     *  Method that saturates a molecule by adding implicit hydrogens.
     *
     *@param  molecule  Molecule to saturate
     *@keyword          hydrogen, adding
     *@keyword          implicit hydrogen
     */
    public void addImplicitHydrogensToSatisfyValency(AtomContainer container) {
        Atom[] atoms = container.getAtoms();
        for (int f = 0; f < atoms.length; f++) {
            addImplicitHydrogensToSatisfyValency(container, atoms[f]);
        }
    }
    
    /**
     * Method that saturates a molecule by adding implicit hydrogens.
     *
     * @param  molecule  Molecule to saturate
     * @keyword          hydrogen, adding
     * @keyword          implicit hydrogen
     */
    public void addImplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)
    {
        int missingHydrogens = satChecker.calculateMissingHydrogen(atom, container);
        atom.setHydrogenCount(missingHydrogens);
    }

}


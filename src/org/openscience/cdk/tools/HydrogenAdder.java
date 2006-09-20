/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.tools;

import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.*;

import java.io.IOException;
import java.util.HashMap;

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
 * <p>This class skips adding hydrogens for radicals, i.e IAtom's that
 * have an associated ISingleAtom.
 *
 * @cdk.keyword    hydrogen, adding
 * @cdk.module     valencycheck
 */
public class HydrogenAdder {

    private LoggingTool logger;
    private IValencyChecker valencyChecker;

    /**
     * Creates a tool to add missing hydrogens using the SaturationChecker class.
     * 
     * @see org.openscience.cdk.tools.SaturationChecker
     */
    public HydrogenAdder() {
        this("org.openscience.cdk.tools.SaturationChecker");
    }
    
    /**
     * Creates a tool to add missing hydrogens using a ValencyCheckerInterface.
     * 
     * @see org.openscience.cdk.tools.IValencyChecker
     */
    public HydrogenAdder(String valencyCheckerInterfaceClassName) {
        logger = new LoggingTool(this);
        try {
            if (valencyCheckerInterfaceClassName.equals("org.openscience.cdk.tools.ValencyChecker")) {
                valencyChecker = new ValencyChecker();
            } else if (valencyCheckerInterfaceClassName.equals("org.openscience.cdk.tools.SaturationChecker")) {
                valencyChecker = new SaturationChecker();
            } else {
                logger.error("Cannot instantiate unknown ValencyCheckerInterface; using SaturationChecker");
                valencyChecker = new SaturationChecker();
            }
        } catch (Exception exception) {
            logger.error("Could not intantiate a SaturationChecker.");
            logger.debug(exception);
        }
    }
    
    /**
     * Creates a tool to add missing hydrogens using a ValencyCheckerInterface.
     * 
     * @see org.openscience.cdk.tools.IValencyChecker
     */
    public HydrogenAdder(IValencyChecker valencyChecker) {
        logger = new LoggingTool(this);
        this.valencyChecker = valencyChecker;
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  molecule  Molecule to saturate
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public IAtomContainer addHydrogensToSatisfyValency(IMolecule molecule) throws IOException, ClassNotFoundException, CDKException
    {
	    logger.debug("Start of addHydrogensToSatisfyValency");
        IAtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(molecule);
	logger.debug("End of addHydrogensToSatisfyValency");
    return changedAtomsAndBonds;
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  molecule  Molecule to saturate
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public IAtomContainer addExplicitHydrogensToSatisfyValency(IAtomContainer molecule) throws IOException, ClassNotFoundException, CDKException
    {
    	logger.debug("Start of addExplicitHydrogensToSatisfyValency");
      IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(molecule);
      IAtomContainer changedAtomsAndBonds = molecule.getBuilder().newAtomContainer();
      IAtomContainer intermediateContainer= null;
      for (int k = 0; k < moleculeSet.getAtomContainerCount(); k++) {
    	  IMolecule molPart = moleculeSet.getMolecule(k);
        IAtom[] atoms = molPart.getAtoms();
         for (int i = 0; i < atoms.length; i++) {
            intermediateContainer = addHydrogensToSatisfyValency(molPart, atoms[i], molecule);
            changedAtomsAndBonds.add(intermediateContainer);
        }
       
      }
      logger.debug("End of addExplicitHydrogensToSatisfyValency");
      return changedAtomsAndBonds;
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     *
     * @deprecated
     */
    public IAtomContainer addHydrogensToSatisfyValency(IAtomContainer container, IAtom atom, IAtomContainer totalContainer) 
        throws IOException, ClassNotFoundException, CDKException
    {
	logger.debug("Start of addHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    IAtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(container, atom, totalContainer);
	logger.debug("End of addHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    return changedAtomsAndBonds;
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to
     * remember the average bondlength of you molecule (coordinates for
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     *
     * @cdk.keyword hydrogen, adding
     * @cdk.keyword explicit hydrogen
     */
    public IAtomContainer addExplicitHydrogensToSatisfyValency(IAtomContainer container, IAtom atom)
            throws IOException, ClassNotFoundException, CDKException {
        // set number of implicit hydrogens to zero
        // add explicit hydrogens
        logger.debug("Start of addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
        int missingHydrogens = calculateNumberOfImplicitHydrogens(container, atom);
        logger.debug("According to valencyChecker, " + missingHydrogens + " are missing");
        IAtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(container, atom, missingHydrogens, container);
        logger.debug("End of addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
        return changedAtomsAndBonds;
    }


    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public IAtomContainer addExplicitHydrogensToSatisfyValency(IAtomContainer container, IAtom atom, IAtomContainer totalContainer) 
        throws IOException, ClassNotFoundException, CDKException
    {
        // set number of implicit hydrogens to zero
        // add explicit hydrogens
	logger.debug("Start of addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
        int missingHydrogens = calculateNumberOfImplicitHydrogens(container, atom);
  logger.debug("According to valencyChecker, " + missingHydrogens + " are missing");
        IAtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(container, atom, missingHydrogens, totalContainer);
	logger.debug("End of addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    return changedAtomsAndBonds;
    }
    
    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  count     Number of hydrogens to add
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public IAtomContainer addExplicitHydrogensToSatisfyValency(IAtomContainer container, IAtom atom, int count, IAtomContainer totalContainer) 
        throws IOException, ClassNotFoundException
    {
        //boolean create2DCoordinates = GeometryTools.has2DCoordinates(container);
        
        IIsotope isotope = IsotopeFactory.getInstance(container.getBuilder()).getMajorIsotope("H");
        atom.setHydrogenCount(0);
        IAtomContainer changedAtomsAndBonds = container.getBuilder().newAtomContainer();
        for (int i = 1; i <= count; i++) {
            IAtom hydrogen = container.getBuilder().newAtom("H");
            IsotopeFactory.getInstance(container.getBuilder()).configure(hydrogen, isotope);
            totalContainer.addAtom(hydrogen);
            IBond newBond = container.getBuilder().newBond((IAtom)atom, hydrogen, 1.0);
            totalContainer.addBond(newBond);
            changedAtomsAndBonds.addAtom(hydrogen);
            changedAtomsAndBonds.addBond(newBond);
        }
        return changedAtomsAndBonds;
    }
    
    /**
     * Method that saturates a molecule by adding implicit hydrogens.
     *
     *@param  container  Molecule to saturate
     *@cdk.keyword          hydrogen, adding
     *@cdk.keyword          implicit hydrogen
     */
    public HashMap addImplicitHydrogensToSatisfyValency(IAtomContainer container) throws CDKException {
      IMoleculeSet moleculeSet = ConnectivityChecker.partitionIntoMolecules(container);
      HashMap hydrogenAtomMap = new HashMap();
      for (int k = 0; k < moleculeSet.getAtomContainerCount(); k++) {
    	IMolecule molPart = moleculeSet.getMolecule(k);
        IAtom[] atoms = molPart.getAtoms();
        for (int f = 0; f < atoms.length; f++) {
            int[] hydrogens = addImplicitHydrogensToSatisfyValency(molPart, atoms[f]);
            hydrogenAtomMap.put(atoms[f], hydrogens);
        }
      }
      return hydrogenAtomMap;
    }
    
    /**
     * Method that saturates an atom in a molecule by adding implicit hydrogens.
     *
     * @param  container  Molecule to saturate
     * @param  atom      Atom to satureate.
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          implicit hydrogen
     */
    public int[] addImplicitHydrogensToSatisfyValency(IAtomContainer container, IAtom atom) throws CDKException
    {
        int formerHydrogens = atom.getHydrogenCount();
        int missingHydrogens = calculateNumberOfImplicitHydrogens(container, atom);
        atom.setHydrogenCount(missingHydrogens);
        int[] hydrogens = new int[2];
        hydrogens[0] = formerHydrogens;
        hydrogens[1] = missingHydrogens;
        return hydrogens;
    }

    private int calculateNumberOfImplicitHydrogens(IAtomContainer container, IAtom atom) throws CDKException {
    	if (container.getSingleElectronSum(atom) > 0) {
    		// This method does not deal with radicals yet, so don't add hydrogens as stupid default
    		return 0;
    	}
    	return valencyChecker.calculateNumberOfImplicitHydrogens(atom, container);
    }
}


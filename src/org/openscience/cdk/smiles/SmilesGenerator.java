/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) Project
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
package org.openscience.cdk.smiles;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.config.IsotopeFactory;

/**
 * Generates SMILES strings {@cdk.cite WEI88, WEI89}. 
 * It takes into account the isotope and formal charge
 * information of the atoms. In addition to this it takes stereochemistry in account
 * for both Bond's and Atom's.
 *
 * @author     Oliver Horlacher,
 * @author     Stefan Kuhn (chiral smiles)
 * @cdk.created    2002-02-26
 * @cdk.keyword    SMILES, generator
 */
public class SmilesGenerator {
  private static boolean debug = false;

  /**
   *The number of rings that have been opened
   */
  private int ringMarker = 0;

  /**
   * Collection of all the bonds that were broken
   */
  private Vector brokenBonds = new Vector();

  /**
   * The isotope factory which is used to write the mass is needed
   */
  private IsotopeFactory isotopeFactory;


  /**
   * The canonical labler
   */
  private CanonicalLabeler canLabler = new CanonicalLabeler();
  private final String RING_CONFIG="stereoconfig";
  private final String UP="up";
  private final String DOWN="down";


  /**
   * Default constructor
   */
  public SmilesGenerator() {
    try {
      isotopeFactory = IsotopeFactory.getInstance();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }


  /**
   *  Tells if a certain bond is center of a valid double bond configuration.
   *
   * @param  container  The atomcontainer.
   * @param  bond       The bond.
   * @return            true=is a potential configuration, false=is not.
   */
  public boolean isValidDoubleBondConfiguration(AtomContainer container, Bond bond) {
    Atom[] atoms = bond.getAtoms();
    Atom[] connectedAtoms = container.getConnectedAtoms(atoms[0]);
    Atom from = null;
    for (int i = 0; i < connectedAtoms.length; i++) {
      if (connectedAtoms[i] != atoms[1]) {
        from = connectedAtoms[i];
      }
    }
    boolean[] array = new boolean[container.getBonds().length];
    for (int i = 0; i < array.length; i++) {
      array[i] = true;
    }
    if (isStartOfDoubleBond(container, atoms[0], from, array) && isEndOfDoubleBond(container, atoms[1], atoms[0], array) && !bond.getFlag(CDKConstants.ISAROMATIC)) {
      return (true);
    } else {
      return (false);
    }
  }


  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but does not perform any checks on the
   * chemical validity of the molecule.
   *
   * @param  molecule  The molecule to evaluate
   * @return           Description of the Returned Value
   * @see              org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(AtomContainer)
   */
  public synchronized String createSMILES(Molecule molecule) {
    try {
      return (createSMILES(molecule, false, new boolean[molecule.getBondCount()]));
    } catch (CDKException exception) {
      // This exception can only happen if a chiral smiles is requested
      return ("");
    }
  }


  /**
   * Generate a SMILES for the given <code>Reaction</code>.
   */
  public synchronized String createSMILES(Reaction reaction) throws CDKException {
      StringBuffer reactionSMILES = new StringBuffer();
      Molecule[] reactants = reaction.getReactants().getMolecules();
      for (int i=0; i<reactants.length; i++) {
          reactionSMILES.append(createSMILES(reactants[i]));
          if (i+1 < reactants.length) {
              reactionSMILES.append('.');
          }
      }
      reactionSMILES.append('>');
      Molecule[] agents = reaction.getAgents().getMolecules();
      for (int i=0; i<agents.length; i++) {
          reactionSMILES.append(createSMILES(agents[i]));
          if (i+1 < agents.length) {
              reactionSMILES.append('.');
          }
      }
      reactionSMILES.append('>');
      Molecule[] products = reaction.getProducts().getMolecules();
      for (int i=0; i<products.length; i++) {
          reactionSMILES.append(createSMILES(products[i]));
          if (i+1 < products.length) {
              reactionSMILES.append('.');
          }
      }
      return reactionSMILES.toString();
  }

  /**
   * Generate canonical and chiral SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule. The chiral smiles is done like in the <a href="http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html">daylight theory manual</a>.
   * I did not find rules for canonical and chiral smiles, therefore there is no guarantee
   * that the smiles complies to any externeal rules, but it is canonical compared to other smiles
   * produced by this method. The method checks if there are 2D coordinates but does not
   * check if coordinates make sense. Invalid stereo configurations are ignored; if there are no
   * valid stereo configuration the smiles will be the same as the non-chiral one. Note that often
   * stereo configurations are only complete and can be converted to a smiles if explicit Hs are given.
   *
   * @param  molecule                 The molecule to evaluate
   * @param  doubleBondConfiguration  Description of Parameter
   * @return                          Description of the Returned Value
   * @exception  CDKException         At least one atom has no Point2D; coordinates are needed for creating the chiral smiles.
   * @see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(AtomContainer)
   */
  public synchronized String createChiralSMILES(Molecule molecule, boolean[] doubleBondConfiguration) throws CDKException {
    return (createSMILES(molecule, true, doubleBondConfiguration));
  }


  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule. This method also takes care of multiple molecules.
   *
   * @param  molecule                 The molecule to evaluate
   * @param  chiral                   true=SMILES will be chiral, false=SMILES will not be chiral.
   * @param  doubleBondConfiguration  Description of Parameter
   * @return                          Description of the Returned Value
   * @exception  CDKException         At least one atom has no Point2D; coordinates are needed for crating the chiral smiles. This excpetion can only be thrown if chiral smiles is created, ignore it if you want a non-chiral smiles (createSMILES(AtomContainer) does not throw an exception).
   * @see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(AtomContainer)
   */
  public synchronized String createSMILES(Molecule molecule, boolean chiral, boolean doubleBondConfiguration[]) throws CDKException {
    SetOfMolecules moleculeSet = ConnectivityChecker.partitionIntoMolecules(molecule);
    if (moleculeSet.getMoleculeCount() > 1) {
      StringBuffer fullSMILES = new StringBuffer();
      Molecule[] molecules = moleculeSet.getMolecules();
      for (int i = 0; i < molecules.length; i++) {
        Molecule molPart = molecules[i];
        fullSMILES.append(createSMILESWithoutCheckForMultipleMolecules(molPart, chiral, doubleBondConfiguration));
        if (i < (molecules.length - 1)) {
          // are there more molecules?
          fullSMILES.append('.');
        }
      }
      return fullSMILES.toString();
    } else {
      return (createSMILESWithoutCheckForMultipleMolecules(molecule, chiral, doubleBondConfiguration));
    }
  }


  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule. Does not care about multiple molecules.
   *
   * @param  molecule                 The molecule to evaluate
   * @param  chiral                   true=SMILES will be chiral, false=SMILES will not be chiral.
   * @param  doubleBondConfiguration  Description of Parameter
   * @return                          Description of the Returned Value
   * @exception  CDKException         At least one atom has no Point2D; coordinates are needed for crating the chiral smiles. This excpetion can only be thrown if chiral smiles is created, ignore it if you want a non-chiral smiles (createSMILES(AtomContainer) does not throw an exception).
   * @see                             org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel(AtomContainer)
   */
  public synchronized String createSMILESWithoutCheckForMultipleMolecules(Molecule molecule, boolean chiral, boolean doubleBondConfiguration[]) throws CDKException {
    if (molecule.getAtomCount() == 0) {
      return "";
    }
    canLabler.canonLabel(molecule);
    brokenBonds.clear();
    ringMarker = 0;
    Atom[] all = molecule.getAtoms();
    Atom start = null;
    for (int i = 0; i < all.length; i++) {
      Atom atom = all[i];
      if (chiral && atom.getPoint2d() == null) {
        throw new CDKException("Atom number " + i + " has no 2D coordinates, but 2D coordinates are needed for creating chiral smiles");
      }
      atom.setFlag(CDKConstants.VISITED, false);
      if (((Long) atom.getProperty("CanonicalLable")).longValue() == 1) {
        start = atom;
      }
    }

    //detect aromaticity
    AllRingsFinder ringFinder = new AllRingsFinder();
    RingSet rings = ringFinder.findAllRings(molecule);
    (new HueckelAromaticityDetector()).detectAromaticity(molecule, rings, false);
    
    
    if(chiral && rings.size()>0){
      Vector v=RingPartitioner.partitionRings(rings);
      for(int i=0;i<v.size();i++){
        int counter=0;
        AtomContainer allrings=((RingSet)v.get(i)).getRingSetInAtomContainer();
        for(int k=0;k<allrings.getAtomCount();k++){
          if(!isStereo(molecule,allrings.getAtomAt(k)) && hasWedges(molecule,allrings.getAtomAt(k))!=null){
            Bond bond=molecule.getBond(allrings.getAtomAt(k),hasWedges(molecule,allrings.getAtomAt(k)));
            if(bond.getStereo()==CDKConstants.STEREO_BOND_UP)
              allrings.getAtomAt(k).setProperty(RING_CONFIG,UP);
            else
              allrings.getAtomAt(k).setProperty(RING_CONFIG,DOWN);
            counter++;
          }
        }
        if(counter==1){
          for(int k=0;k<allrings.getAtomCount();k++){
            allrings.getAtomAt(k).setProperty(RING_CONFIG,UP);
          }
        }
      }
    }
    
    
    StringBuffer l = new StringBuffer();
    createSMILES(start, l, molecule, chiral, doubleBondConfiguration);
    return l.toString();
  }
  
  
  private Atom hasWedges(AtomContainer ac, Atom a){
    Atom[] atoms=ac.getConnectedAtoms(a);
    for(int i=0;i<atoms.length;i++){
      if(ac.getBond(a, atoms[i]).getStereo()!=CDKConstants.STEREO_BOND_NONE && !atoms[i].getSymbol().equals("H")){
        return(atoms[i]);
      }
    }
    for(int i=0;i<atoms.length;i++){
      if(ac.getBond(a, atoms[i]).getStereo()!=CDKConstants.STEREO_BOND_NONE){
        return(atoms[i]);
      }
    }
    return(null);
  }


  /**
   * Says if an atom is the end of a double bond configuration
   *
   * @param  atom                     The atom which is the end of configuration
   * @param  container                The atomContainer the atom is in
   * @param  parent                   The atom we came from
   * @param  doubleBondConfiguration  The array indicating where double bond configurations are specified (this method ensures that there is actually the possibility of a double bond configuration)
   * @return                          false=is not end of configuration, true=is
   */
  private boolean isEndOfDoubleBond(AtomContainer container, Atom atom, Atom parent, boolean[] doubleBondConfiguration) {
    if (container.getBondNumber(atom, parent) == -1 || doubleBondConfiguration.length<=container.getBondNumber(atom, parent) || !doubleBondConfiguration[container.getBondNumber(atom, parent)]) {
      return false;
    }
    int lengthAtom = container.getConnectedAtoms(atom).length + atom.getHydrogenCount();
    int lengthParent = container.getConnectedAtoms(parent).length + parent.getHydrogenCount();
    if (container.getBond(atom, parent) != null) {
      if (container.getBond(atom, parent).getOrder() == CDKConstants.BONDORDER_DOUBLE && (lengthAtom == 3 || (lengthAtom == 2 && atom.getSymbol().equals("N"))) && (lengthParent == 3 || (lengthParent == 2 && parent.getSymbol().equals("N")))) {
        Atom[] atoms = container.getConnectedAtoms(atom);
        Atom one = null;
        Atom two = null;
        for (int i = 0; i < atoms.length; i++) {
          if (atoms[i] != parent && one == null) {
            one = atoms[i];
          } else if (atoms[i] != parent && one != null) {
            two = atoms[i];
          }
        }
        String[] morgannumbers = MorganNumbersTools.getMorganNumbersWithElementSymbol(container);
        if ((one != null && two == null && atom.getSymbol().equals("N") && Math.abs(giveAngleBothMethods(parent, atom, one, true)) > Math.PI / 10) || (!atom.getSymbol().equals("N") && one != null && two != null && !morgannumbers[container.getAtomNumber(one)].equals(morgannumbers[container.getAtomNumber(two)]))) {
          return (true);
        } else {
          return (false);
        }
      }
    }
    return (false);
  }


  /**
   * Says if an atom is the start of a double bond configuration
   *
   * @param  a                        The atom which is the start of configuration
   * @param  container                The atomContainer the atom is in
   * @param  parent                   The atom we came from
   * @param  doubleBondConfiguration  The array indicating where double bond configurations are specified (this method ensures that there is actually the possibility of a double bond configuration)
   * @return                          false=is not start of configuration, true=is
   */
  private boolean isStartOfDoubleBond(AtomContainer container, Atom a, Atom parent, boolean[] doubleBondConfiguration) {
    int lengthAtom = container.getConnectedAtoms(a).length + a.getHydrogenCount();
    if (lengthAtom != 3 && (lengthAtom != 2 && a.getSymbol() != ("N"))) {
      return (false);
    }
    Atom[] atoms = container.getConnectedAtoms(a);
    Atom one = null;
    Atom two = null;
    boolean doubleBond = false;
    Atom nextAtom = null;
    for (int i = 0; i < atoms.length; i++) {
      if (atoms[i] != parent && container.getBond(atoms[i], a).getOrder() == CDKConstants.BONDORDER_DOUBLE && isEndOfDoubleBond(container, atoms[i], a, doubleBondConfiguration)) {
        doubleBond = true;
        nextAtom = atoms[i];
      }
      if (atoms[i] != nextAtom && one == null) {
        one = atoms[i];
      } else if (atoms[i] != nextAtom && one != null) {
        two = atoms[i];
      }
    }
    String[] morgannumbers = MorganNumbersTools.getMorganNumbersWithElementSymbol(container);
    if (one != null && ((!a.getSymbol().equals("N") && two != null && !morgannumbers[container.getAtomNumber(one)].equals(morgannumbers[container.getAtomNumber(two)]) && doubleBond && doubleBondConfiguration[container.getBondNumber(a, nextAtom)]) || (doubleBond && a.getSymbol().equals("N") && Math.abs(giveAngleBothMethods(nextAtom, a, parent, true)) > Math.PI / 10))) {
      return (true);
    } else {
      return (false);
    }
  }


  /**
   * Says if an atom as a center of a tetrahedral chirality
   *
   * @param  a          The atom which is the center
   * @param  container  The atomContainer the atom is in
   * @return            0=is not tetrahedral;>1 is a certain depiction of tetrahedrality (evaluated in parse chain)
   */
  private int isTetrahedral(AtomContainer container, Atom a) {
    Atom[] atoms = container.getConnectedAtoms(a);
    if (atoms.length != 4) {
      return (0);
    }
    Bond[] bonds = container.getConnectedBonds(a);
    int normal = 0;
    int up = 0;
    int down = 0;
    for (int i = 0; i < bonds.length; i++) {
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_NONE || bonds[i].getStereo() == CDKConstants.STEREO_BOND_UNDEFINED) {
        normal++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_UP) {
        up++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_DOWN) {
        down++;
      }
    }
    if (up == 1 && down == 1) {
      return 1;
    }
    if (up == 2 && down == 2) {
      if (stereosAreOpposite(container, a)) {
        return 2;
      }
      return 0;
    }
    if (up == 1 && down == 0) {
      return 3;
    }
    if (down == 1 && up == 0) {
      return 4;
    }
    if (down == 2 && up == 1) {
      return 5;
    }
    if (down == 1 && up == 2) {
      return 6;
    }
    return 0;
  }


  /**
   * Says if an atom as a center of a square planar chirality
   *
   * @param  a          The atom which is the center
   * @param  container  The atomContainer the atom is in
   * @return            true=is square planar, false=is not
   */
  private boolean isSquarePlanar(AtomContainer container, Atom a) {
    Atom[] atoms = container.getConnectedAtoms(a);
    if (atoms.length != 4) {
      return (false);
    }
    Bond[] bonds = container.getConnectedBonds(a);
    int normal = 0;
    int up = 0;
    int down = 0;
    for (int i = 0; i < bonds.length; i++) {
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_UNDEFINED || bonds[i].getStereo() == CDKConstants.STEREO_BOND_NONE) {
        normal++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_UP) {
        up++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_DOWN) {
        down++;
      }
    }
    if (up == 2 && down == 2 && !stereosAreOpposite(container, a)) {
      return true;
    }
    return false;
  }


  /**
   * Says if an atom as a center of a trigonal-bipyramidal or actahedral chirality
   *
   * @param  a          The atom which is the center
   * @param  container  The atomContainer the atom is in
   * @return            true=is square planar, false=is not
   */
  private boolean isTrigonalBipyramidalOrOctahedral(AtomContainer container, Atom a) {
    Atom[] atoms = container.getConnectedAtoms(a);
    if (atoms.length < 5 || atoms.length > 6) {
      return (false);
    }
    Bond[] bonds = container.getConnectedBonds(a);
    int normal = 0;
    int up = 0;
    int down = 0;
    for (int i = 0; i < bonds.length; i++) {
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_UNDEFINED || bonds[i].getStereo() == CDKConstants.STEREO_BOND_NONE) {
        normal++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_UP) {
        up++;
      }
      if (bonds[i].getStereo() == CDKConstants.STEREO_BOND_DOWN) {
        down++;
      }
    }
    if (up == 1 && down == 1) {
      return true;
    }
    return false;
  }


  /**
   * Says if an atom as a center of any valid stereo configuration or not
   *
   * @param  a          The atom which is the center
   * @param  container  The atomContainer the atom is in
   * @return            true=is a stereo atom, false=is not
   */
  private boolean isStereo(AtomContainer container, Atom a) {
    Atom[] atoms = container.getConnectedAtoms(a);
    if (atoms.length < 4 || atoms.length > 6) {
      return (false);
    }
    Bond[] bonds = container.getConnectedBonds(a);
    int stereo = 0;
    for (int i = 0; i < bonds.length; i++) {
      if (bonds[i].getStereo() != 0) {
        stereo++;
      }
    }
    if (stereo == 0) {
      return false;
    }
    int differentAtoms = 0;
    for (int i = 0; i < atoms.length; i++) {
      boolean isDifferent = true;
      for (int k = 0; k < i; k++) {
        if (atoms[i].getSymbol().equals(atoms[k].getSymbol())) {
          isDifferent = false;
          break;
        }
      }
      if (isDifferent) {
        differentAtoms++;
      }
    }
    if (differentAtoms != atoms.length) {
      int[] morgannumbers = MorganNumbersTools.getMorganNumbers(container);
      Vector differentSymbols = new Vector();
      for (int i = 0; i < atoms.length; i++) {
        if (!differentSymbols.contains(atoms[i].getSymbol())) {
          differentSymbols.add(atoms[i].getSymbol());
        }
      }
      int[] onlyRelevantIfTwo = new int[2];
      if (differentSymbols.size() == 2) {
        for (int i = 0; i < atoms.length; i++) {
          if (differentSymbols.indexOf(atoms[i].getSymbol()) == 0) {
            onlyRelevantIfTwo[0]++;
          } else {
            onlyRelevantIfTwo[1]++;
          }
        }
      }
      boolean[] symbolsWithDifferentMorganNumbers = new boolean[differentSymbols.size()];
      Vector[] symbolsMorganNumbers = new Vector[differentSymbols.size()];
      for (int i = 0; i < symbolsWithDifferentMorganNumbers.length; i++) {
        symbolsWithDifferentMorganNumbers[i] = true;
        symbolsMorganNumbers[i] = new Vector();
      }
      for (int k = 0; k < atoms.length; k++) {
        int elementNumber = differentSymbols.indexOf(atoms[k].getSymbol());
        if (symbolsMorganNumbers[elementNumber].contains(new Integer(morgannumbers[container.getAtomNumber(atoms[k])]))) {
          symbolsWithDifferentMorganNumbers[elementNumber] = false;
        } else {
          symbolsMorganNumbers[elementNumber].add(new Integer(morgannumbers[container.getAtomNumber(atoms[k])]));
        }
      }
      int numberOfSymbolsWithDifferentMorganNumbers = 0;
      for (int i = 0; i < symbolsWithDifferentMorganNumbers.length; i++) {
        if (symbolsWithDifferentMorganNumbers[i] == true) {
          numberOfSymbolsWithDifferentMorganNumbers++;
        }
      }
      if (numberOfSymbolsWithDifferentMorganNumbers != differentSymbols.size()) {
        if ((atoms.length == 5 || atoms.length == 6) && (numberOfSymbolsWithDifferentMorganNumbers + differentAtoms > 2 || (differentAtoms == 2 && onlyRelevantIfTwo[0] > 1 && onlyRelevantIfTwo[1] > 1))) {
          return (true);
        }
        if (isSquarePlanar(container, a) && (numberOfSymbolsWithDifferentMorganNumbers + differentAtoms > 2 || (differentAtoms == 2 && onlyRelevantIfTwo[0] > 1 && onlyRelevantIfTwo[1] > 1))) {
          return (true);
        }
        return false;
      }
    }
    return (true);
  }


  /**
   *  Gets the bondBroken attribute of the SmilesGenerator object
   *
   * @param  a1  Description of Parameter
   * @param  a2  Description of Parameter
   * @return     The bondBroken value
   */
  private boolean isBondBroken(Atom a1, Atom a2) {
    Iterator it = brokenBonds.iterator();
    while (it.hasNext()) {
      BrokenBond bond = ((BrokenBond) it.next());
      if ((bond.getA1().equals(a1) || bond.getA1().equals(a2)) && (bond.getA2().equals(a1) || bond.getA2().equals(a2))) {
        return (true);
      }
    }
    return false;
  }


  /**
   * Says if an atom is on the left side of a another atom seen from
   * a certain atom or not
   *
   * @param  whereIs   The atom the position of which is returned
   * @param  viewFrom  The atom from which to look
   * @param  viewTo    The atom to which to look
   * @return           true=is left, false = is not
   */
  private boolean isLeft(Atom whereIs, Atom viewFrom, Atom viewTo) {
    double angle = giveAngleBothMethods(viewFrom, viewTo, whereIs, false);
    if (angle < 0) {
      return (false);
    } else {
      return (true);
    }
  }


  /**
   * Determines if the atom <code>a</code> is a atom with a ring
   * marker.
   *
   * @param  a  the atom to test
   * @return    true if the atom participates in a bond that was broken in the first pass.
   */
  private boolean isRingOpening(Atom a) {
    Iterator it = brokenBonds.iterator();
    while (it.hasNext()) {
      BrokenBond bond = (BrokenBond) it.next();
      if (bond.getA1().equals(a) || bond.getA2().equals(a)) {
        return true;
      }
    }
    return false;
  }


  /**
   * Determines if the atom <code>a</code> is a atom with a ring
   * marker.
   *
   * @param  a1  Description of Parameter
   * @param  a2  Description of Parameter
   * @return     true if the atom participates in a bond that was broken in the first pass.
   */
  private boolean isRingOpening(Atom a1, Vector v) {
    Iterator it = brokenBonds.iterator();
    while (it.hasNext()) {
      BrokenBond bond = (BrokenBond) it.next();
      for(int i=0;i<v.size();i++){
        if ((bond.getA1().equals(a1) && bond.getA2().equals((Atom)v.get(i))) || (bond.getA1().equals((Atom)v.get(i)) && bond.getA2().equals(a1))) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * Return the neighbours of atom <code>a</code> in canonical order with the atoms that have
   * high bond order at the front.
   *
   * @param  a          the atom whose neighbours are to be found.
   * @param  container  the AtomContainer that is being parsed.
   * @return            Vector of atoms in canonical oreder.
   */
  private Vector getCanNeigh(final Atom a, final AtomContainer container) {
    Vector v = container.getConnectedAtomsVector(a);
    if (v.size() > 1) {
      Collections.sort(v,
        new Comparator() {
          public int compare(Object o1, Object o2) {
            return (int) (((Long) ((Atom) o1).getProperty("CanonicalLable")).longValue() - ((Long) ((Atom) o2).getProperty("CanonicalLable")).longValue());
          }
        });
    }
    return v;
  }


  /**
   *  Gets the ringOpenings attribute of the SmilesGenerator object
   *
   * @param  a  Description of Parameter
   * @return    The ringOpenings value
   */
  private Vector getRingOpenings(Atom a) {
    Iterator it = brokenBonds.iterator();
    Vector v = new Vector(10);
    while (it.hasNext()) {
      BrokenBond bond = (BrokenBond) it.next();
      if (bond.getA1().equals(a) || bond.getA2().equals(a)) {
        v.add(new Integer(bond.getMarker()));
      }
    }
    Collections.sort(v);
    return v;
  }


  /**
   * Returns true if the <code>atom</code> in the <code>container</code> has
   * been marked as a chiral center by the user.
   *
   * @param  atom       Description of Parameter
   * @param  container  Description of Parameter
   * @return            The chiralCenter value
   */
  private boolean isChiralCenter(Atom atom, AtomContainer container) {
    Bond[] bonds = container.getConnectedBonds(atom);
    for (int i = 0; i < bonds.length; i++) {
      Bond bond = bonds[i];
      int stereo = bond.getStereo();
      if (stereo == CDKConstants.STEREO_BOND_DOWN ||
          stereo == CDKConstants.STEREO_BOND_UP) {
        return true;
      }
    }
    return false;
  }


  /**
   *  Gets the last atom object (not Vector) in a Vector as created by createDSFTree.
   *
   * @param  v  The Vector
   * @param  i  The number of the last element (size -1)
   * @return    The last atom.
   */
  private void addAtoms(Vector v, Vector result) {
    for(int i=0;i<v.size();i++){
      if(v.get(i) instanceof Atom)
        result.add((Atom)v.get(i));
      else
        addAtoms((Vector)v.get(i),result);
    }
  }


  /**
   * Calls giveAngleBothMethods with bool = true
   *
   * @param  from  the atom to view from
   * @param  to1   first direction to look in
   * @param  to2   second direction to look in
   * @return       The angle in rad from 0 to 2*PI
   */
  private double giveAngle(Atom from, Atom to1, Atom to2) {
    return (giveAngleBothMethods(from, to1, to2, true));
  }


  /**
   * Calls giveAngleBothMethods with bool = false
   *
   * @param  from  the atom to view from
   * @param  to1   first direction to look in
   * @param  to2   second direction to look in
   * @return       The angle in rad from -PI to PI
   */
  private double giveAngleFromMiddle(Atom from, Atom to1, Atom to2) {
    return (giveAngleBothMethods(from, to1, to2, false));
  }


  /**
   * Gives the angle between two lines starting at atom from and going to
   * to1 and to2. If bool=false the angle starts from the middle line and goes from
   * 0 to PI or 0 to -PI if the to2 is on the left or right side of the line. If bool=true
   * the angle goes from 0 to 2PI.
   *
   * @param  from  the atom to view from.
   * @param  to1   first direction to look in.
   * @param  to2   second direction to look in.
   * @param  bool  true=angle is 0 to 2PI, false=angel is -PI to PI.
   * @return       The angle in rad.
   */
  private double giveAngleBothMethods(Atom from, Atom to1, Atom to2, boolean bool) {
    double[] A = new double[2];
    from.getPoint2d().get(A);
    double[] B = new double[2];
    to1.getPoint2d().get(B);
    double[] C = new double[2];
    to2.getPoint2d().get(C);
    double angle1 = Math.atan2((B[1] - A[1]), (B[0] - A[0]));
    double angle2 = Math.atan2((C[1] - A[1]), (C[0] - A[0]));
    double angle = angle2 - angle1;
    if (angle2 < 0 && angle1 > 0 && angle2 < -(Math.PI / 2)) {
      angle = Math.PI + angle2 + Math.PI - angle1;
    }
    if (angle2 > 0 && angle1 < 0 && angle1 < -(Math.PI / 2)) {
      angle = -Math.PI + angle2 - Math.PI - angle1;
    }
    if (bool && angle < 0) {
      return (2 * Math.PI + angle);
    } else {
      return (angle);
    }
  }


  /**
   * Says if of four atoms connected two one atom the up and down bonds are
   * opposite or not, i. e.if it's tetrehedral or square planar. The method doesnot check if
   * there are four atoms and if two or up and two are down
   *
   * @param  a          The atom which is the center
   * @param  container  The atomContainer the atom is in
   * @return            true=are opposite, false=are not
   */
  private boolean stereosAreOpposite(AtomContainer container, Atom a) {
    Vector atoms = container.getConnectedAtomsVector(a);
    TreeMap hm = new TreeMap();
    for (int i = 1; i < atoms.size(); i++) {
      hm.put(new Double(giveAngle(a, (Atom) atoms.get(0), ((Atom) atoms.get(i)))), new Integer(i));
    }
    Object[] ohere = hm.values().toArray();
    int stereoOne = container.getBond(a, (Atom) atoms.get(0)).getStereo();
    int stereoOpposite = container.getBond(a, (Atom) atoms.get((((Integer) ohere[1])).intValue())).getStereo();
    if (stereoOpposite == stereoOne) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * Performes a DFS search on the <code>atomContainer</code>.  Then parses the resulting
   * tree to create the SMILES string.
   *
   * @param  a                        the atom to start the search at.
   * @param  line                     the StringBuffer that the SMILES is to be appended to.
   * @param  chiral                   true=SMILES will be chiral, false=SMILES will not be chiral.
   * @param  atomContainer            the AtomContainer that the SMILES string is generated for.
   * @param  doubleBondConfiguration  Description of Parameter
   */
  private void createSMILES(Atom a, StringBuffer line, AtomContainer atomContainer, boolean chiral, boolean[] doubleBondConfiguration) {
    Vector tree = new Vector();
    createDFSTree(a, tree, null, atomContainer);    
    parseChain(tree, line, atomContainer, null, chiral, doubleBondConfiguration, new Vector());
  }


  /**
   * Recursively perform a DFS search on the <code>container</code> placing atoms and branches in the
   * vector <code>tree</code>.
   *
   * @param  a          the atom being visited.
   * @param  tree       vector holding the tree.
   * @param  parent     the atom we came from.
   * @param  container  the AtomContainer that we are parsing.
   */
  private void createDFSTree(Atom a, Vector tree, Atom parent, AtomContainer container) {
    tree.add(a);
    Vector neighbours = getCanNeigh(a, container);
    neighbours.remove(parent);
    Atom next;
    a.setFlag(CDKConstants.VISITED, true);
    for (int x = 0; x < neighbours.size(); x++) {
      next = (Atom) neighbours.elementAt(x);
      if (!next.getFlag(CDKConstants.VISITED)) {
        if (x == neighbours.size() - 1) {
          //Last neighbour therefore in this chain
          createDFSTree(next, tree, a, container);
        } else {
          Vector branch = new Vector();
          tree.add(branch);
          createDFSTree(next, branch, a, container);
        }
      } else {
        //Found ring closure between next and a
        ringMarker++;
        BrokenBond bond = new BrokenBond(a, next, ringMarker);
        if (!brokenBonds.contains(bond)) {
          brokenBonds.add(bond);
        } else {
          ringMarker--;
        }
      }
    }
  }


  /**
   * Parse a branch
   *
   * @param  v                        Description of Parameter
   * @param  buffer                   Description of Parameter
   * @param  container                Description of Parameter
   * @param  parent                   Description of Parameter
   * @param  chiral                   Description of Parameter
   * @param  doubleBondConfiguration  Description of Parameter
   * @param  atomsInOrderOfSmiles     Description of Parameter
   */
  private void parseChain(Vector v, StringBuffer buffer, AtomContainer container, Atom parent, boolean chiral, boolean[] doubleBondConfiguration, Vector atomsInOrderOfSmiles) {
    int positionInVector = 0;
    Atom atom;
    for (int h = 0; h < v.size(); h++) {
      Object o = v.get(h);
      if (o instanceof Atom) {
        atom = (Atom) o;
        if (parent != null) {
          parseBond(buffer, atom, parent, container);
        } else {
          if (chiral && isStereo(container, atom)) {
            parent = (Atom) ((Vector) v.get(1)).get(0);
          }
        }
        parseAtom(atom, buffer, container, chiral, doubleBondConfiguration, parent, atomsInOrderOfSmiles, v);
        /*
         *  The principle of making chiral smiles is quite simple, although the code is
         *  pretty uggly. The Atoms connected to the chiral center are put in sorted[] in the
         *  order they have to appear in the smiles. Then the Vector v is rearranged according
         *  to sorted[]
         */
        if (chiral && isStereo(container, atom) && container.getBond(parent, atom) != null) {
          Atom[] sorted = null;
          Vector chiralNeighbours = container.getConnectedAtomsVector(atom);
          if (isTetrahedral(container, atom) > 0) {
            sorted = new Atom[3];
          }
          if (isTetrahedral(container, atom) == 1) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UNDEFINED || container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_NONE) {
              boolean normalBindingIsLeft = false;
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                    if (isLeft(((Atom) chiralNeighbours.get(i)), parent, atom)) {
                      normalBindingIsLeft = true;
                      break;
                    }
                  }
                }
              }
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (normalBindingIsLeft) {
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                      sorted[0] = (Atom) chiralNeighbours.get(i);
                    }
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
                      sorted[2] = (Atom) chiralNeighbours.get(i);
                    }
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                      sorted[1] = (Atom) chiralNeighbours.get(i);
                    }
                  } else {
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
                      sorted[1] = (Atom) chiralNeighbours.get(i);
                    }
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                      sorted[0] = (Atom) chiralNeighbours.get(i);
                    }
                    if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                      sorted[2] = (Atom) chiralNeighbours.get(i);
                    }
                  }
                }
              }
            }
          }
          if (isTetrahedral(container, atom) == 2) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              double angle1=0;
              double angle2=0;
              Atom atom1=null;
              Atom atom2=null;
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    if(angle1==0){
                      angle1=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom1=(Atom) chiralNeighbours.get(i);
                    }else{
                      angle2=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom2=(Atom) chiralNeighbours.get(i);
                    }
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
              if(angle1<angle2){
                sorted[0] = atom2;
                sorted[2] = atom1;
              }else{
                sorted[0] = atom1;
                sorted[2] = atom2;
              }
            }
          }
          if (isTetrahedral(container, atom) == 3) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              TreeMap hm = new TreeMap();
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                  hm.put(new Double(giveAngle(atom, parent, ((Atom) chiralNeighbours.get(i)))), new Integer(i));
                }
              }
              Object[] ohere = hm.values().toArray();
              for (int i = ohere.length - 1; i > -1; i--) {
                sorted[i] = ((Atom) chiralNeighbours.get(((Integer) ohere[i]).intValue()));
              }
            }
            if (container.getBond(parent, atom).getStereo() == 0) {
              double angle1=0;
              double angle2=0;
              Atom atom1=null;
              Atom atom2=null;
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    if(angle1==0){
                      angle1=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom1=(Atom) chiralNeighbours.get(i);
                    }else{
                      angle2=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom2=(Atom) chiralNeighbours.get(i);
                    }
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
              if(angle1<angle2){
                sorted[1] = atom2;
                sorted[2] = atom1;
              }else{
                sorted[1] = atom1;
                sorted[2] = atom2;
              }
            }
          }
          if (isTetrahedral(container, atom) == 4) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              TreeMap hm = new TreeMap();
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                  hm.put(new Double(giveAngle(atom, parent, ((Atom) chiralNeighbours.get(i)))), new Integer(i));
                }
              }
              Object[] ohere = hm.values().toArray();
              for (int i = ohere.length - 1; i > -1; i--) {
                sorted[i] = ((Atom) chiralNeighbours.get(((Integer) ohere[i]).intValue()));
              }
            }
            if (container.getBond(parent, atom).getStereo() == 0) {
              double angle1=0;
              double angle2=0;
              Atom atom1=null;
              Atom atom2=null;
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0 && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    if(angle1==0){
                      angle1=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom1=(Atom) chiralNeighbours.get(i);
                    }else{
                      angle2=giveAngle(atom,parent,(Atom) chiralNeighbours.get(i));
                      atom2=(Atom) chiralNeighbours.get(i);
                    }
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
              if(angle1<angle2){
                sorted[1] = atom2;
                sorted[0] = atom1;
              }else{
                sorted[1] = atom1;
                sorted[0] = atom2;
              }
            }
          }
          if (isTetrahedral(container, atom) == 5) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UNDEFINED || container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_NONE) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
          }
          if (isTetrahedral(container, atom) == 6) {
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == 0) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UNDEFINED || container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_NONE) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[2] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_UP && !isLeft(((Atom) chiralNeighbours.get(i)), parent, atom) && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond((Atom) chiralNeighbours.get(i), atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                    sorted[1] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
            }
          }
          if (isSquarePlanar(container, atom)) {
            sorted = new Atom[3];
            //This produces a U=SP1 order in every case
            TreeMap hm = new TreeMap();
            for (int i = 0; i < chiralNeighbours.size(); i++) {
              if (chiralNeighbours.get(i) != parent && !isBondBroken((Atom) chiralNeighbours.get(i), atom)) {
                hm.put(new Double(giveAngle(atom, parent, ((Atom) chiralNeighbours.get(i)))), new Integer(i));
              }
            }
            Object[] ohere = hm.values().toArray();
            for (int i = 0; i < ohere.length; i++) {
              sorted[i] = ((Atom) chiralNeighbours.get(((Integer) ohere[i]).intValue()));
            }
          }
          if (isTrigonalBipyramidalOrOctahedral(container, atom)) {
            sorted = new Atom[container.getConnectedAtoms(atom).length - 1];
            TreeMap hm = new TreeMap();
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_UP) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == 0) {
                  hm.put(new Double(giveAngle(atom, parent, ((Atom) chiralNeighbours.get(i)))), new Integer(i));
                }
                if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                  sorted[sorted.length - 1] = (Atom) chiralNeighbours.get(i);
                }
              }
              Object[] ohere = hm.values().toArray();
              for (int i = 0; i < ohere.length; i++) {
                sorted[i] = ((Atom) chiralNeighbours.get(((Integer) ohere[i]).intValue()));
              }
            }
            if (container.getBond(parent, atom).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == 0) {
                  hm.put(new Double(giveAngle(atom, parent, ((Atom) chiralNeighbours.get(i)))), new Integer(i));
                }
                if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == CDKConstants.STEREO_BOND_UP) {
                  sorted[sorted.length - 1] = (Atom) chiralNeighbours.get(i);
                }
              }
              Object[] ohere = hm.values().toArray();
              for (int i = 0; i < ohere.length; i++) {
                sorted[i] = ((Atom) chiralNeighbours.get(((Integer) ohere[i]).intValue()));
              }
            }
            if (container.getBond(parent, atom).getStereo() == 0) {
              for (int i = 0; i < chiralNeighbours.size(); i++) {
                if (chiralNeighbours.get(i) != parent) {
                  if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == 0) {
                    hm.put(new Double((giveAngleFromMiddle(atom, parent, ((Atom) chiralNeighbours.get(i))))), new Integer(i));
                  }
                  if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == CDKConstants.STEREO_BOND_UP) {
                    sorted[0] = (Atom) chiralNeighbours.get(i);
                  }
                  if (container.getBond(atom, (Atom) chiralNeighbours.get(i)).getStereo() == CDKConstants.STEREO_BOND_DOWN) {
                    sorted[sorted.length - 2] = (Atom) chiralNeighbours.get(i);
                  }
                }
              }
              Object[] ohere = hm.values().toArray();
              sorted[sorted.length - 1] = ((Atom) chiralNeighbours.get(((Integer) ohere[ohere.length - 1]).intValue()));
              if (ohere.length == 2) {
                sorted[sorted.length - 3] = ((Atom) chiralNeighbours.get(((Integer) ohere[0]).intValue()));
                if (giveAngleFromMiddle(atom, parent, ((Atom) chiralNeighbours.get(((Integer) ohere[1]).intValue()))) < 0) {
                  Atom dummy = sorted[sorted.length - 2];
                  sorted[sorted.length - 2] = sorted[0];
                  sorted[0] = dummy;
                }
              }
              if (ohere.length == 3) {
                sorted[sorted.length - 3] = sorted[sorted.length - 2];
                sorted[sorted.length - 2] = ((Atom) chiralNeighbours.get(((Integer) ohere[ohere.length - 2]).intValue()));
                sorted[sorted.length - 4] = ((Atom) chiralNeighbours.get(((Integer) ohere[ohere.length - 3]).intValue()));
              }
            }
          }
          //This builds an onew[] containing the objects after the center of the chirality in the order given by sorted[]
          if (sorted != null) {
            int numberOfAtoms = 3;
            if (isTrigonalBipyramidalOrOctahedral(container, atom)) {
              numberOfAtoms = container.getConnectedAtoms(atom).length - 1;
            }
            Object[] omy = new Object[numberOfAtoms];
            Object[] onew = new Object[numberOfAtoms];
            for (int k = getRingOpenings(atom).size(); k < numberOfAtoms; k++) {
              if(positionInVector + 1 + k - getRingOpenings(atom).size()<v.size())
                omy[k] = v.get(positionInVector + 1 + k - getRingOpenings(atom).size());
            }
            for (int k = 0; k < sorted.length; k++) {
              if (sorted[k] != null) {
                for (int m = 0; m < omy.length; m++) {
                  if (omy[m] instanceof Atom) {
                    if (omy[m] == sorted[k]) {
                      onew[k] = omy[m];
                    }
                  } else {
                    if (omy[m] == null) {
                      onew[k] = null;
                    } else {
                      if (((Vector) omy[m]).get(0) == sorted[k]) {
                        onew[k] = omy[m];
                      }
                    }
                  }
                }
              } else {
                onew[k] = null;
              }
            }
            //This is a workaround for 3624.MOL.2 I don't have a better solution currently
            boolean doubleentry=false;
            for (int m = 0; m < onew.length; m++) {
              for (int k = 0; k < onew.length; k++) {
                if(m!=k && onew[k]==onew[m])
                  doubleentry=true;
              }
            }
            if(!doubleentry){
              //Make sure that the first atom in onew is the first one in the original smiles order. This is important to have a canonical smiles.
              if (positionInVector + 1 < v.size()) {
                Object atomAfterCenterInOriginalSmiles = v.get(positionInVector + 1);
                int l = 0;
                while (onew[0] != atomAfterCenterInOriginalSmiles) {
                  Object placeholder = onew[onew.length - 1];
                  for (int k = onew.length - 2; k > -1; k--) {
                    onew[k + 1] = onew[k];
                  }
                  onew[0] = placeholder;
                  l++;
                  if (l > onew.length) {
                    break;
                  }
                }
              }
              //This cares about ring openings. Here the ring closure (represendted by a figure) must be the first atom. In onew the closure is null.
              if(getRingOpenings(atom).size()>0){
                int l = 0;
                while (onew[0] != null) {
                  Object placeholder = onew[0];
                  for (int k = 1; k < onew.length; k++) {
                    onew[k-1] = onew[k];
                  }
                  onew[onew.length-1] = placeholder;
                  l++;
                  if (l > onew.length) {
                    break;
                  }
                }              
              }
              //The last in onew is a vector: This means we need to exchange the rest of the original smiles with the rest of this vector.
              if (onew[numberOfAtoms - 1] instanceof Vector) {
                for (int i = 0; i < numberOfAtoms; i++) {
                  if (onew[i] instanceof Atom) {
                    Vector vtemp = new Vector();
                    vtemp.add(onew[i]);
                    for (int k = positionInVector + 1 + numberOfAtoms; k < v.size(); k++) {
                      vtemp.add(v.get(k));
                    }
                    onew[i] = vtemp;
                    for (int k = v.size() - 1; k > positionInVector + 1 + numberOfAtoms - 1; k--) {
                      v.remove(k);
                    }
                    for (int k = 1; k < ((Vector) onew[numberOfAtoms - 1]).size(); k++) {
                      v.add(((Vector) onew[numberOfAtoms - 1]).get(k));
                    }
                    onew[numberOfAtoms - 1] = ((Vector) onew[numberOfAtoms - 1]).get(0);
                    break;
                  }
                }
              }
              //Put the onew objects in the original Vector
              int k = 0;
              for (int m = 0; m < onew.length; m++) {
                if (onew[m] != null) {
                  v.set(positionInVector + 1 + k, onew[m]);
                  k++;
                }
              }
            }
          }
        }
        parent = atom;
      } else {
        //Have Vector
        boolean brackets = true;
        Vector result=new Vector();
        addAtoms((Vector) o, result);
        if (isRingOpening(parent, result) && container.getBondCount(parent) < 4) {
          brackets = false;
        }
        if (brackets) {
          buffer.append('(');
        }
        parseChain((Vector) o, buffer, container, parent, chiral, doubleBondConfiguration, atomsInOrderOfSmiles);
        if (brackets) {
          buffer.append(')');
        }
      }
      positionInVector++;
    }
  }


  /**
   * Append the symbol for the bond order between <code>a1</code> and <code>a2</code> to
   * the <code>line</code>.
   *
   * @param  line           the StringBuffer that the bond symbol is appended to.
   * @param  a1             Atom participating in the bond.
   * @param  a2             Atom participating in the bond.
   * @param  atomContainer  the AtomContainer that the SMILES string is generated for.
   */
  private void parseBond(StringBuffer line, Atom a1, Atom a2, AtomContainer atomContainer) {
    if (a1.getFlag(CDKConstants.ISAROMATIC) && a1.getFlag(CDKConstants.ISAROMATIC)) {
      return;
    }
    if (atomContainer.getBond(a1, a2) == null) {
      return;
    }
    int type = 0;
    type = (int) atomContainer.getBond(a1, a2).getOrder();
    if (type == 1) {
    } else if (type == 2) {
      line.append("=");

    } else if (type == 3) {
      line.append("#");
    } else {
      // System.out.println("Unknown bond type");
    }
  }


  /**
   * Generates the SMILES string for the atom
   *
   * @param  a                        the atom to generate the SMILES for.
   * @param  buffer                   the string buffer that the atom is to be apended to.
   * @param  container                the AtomContainer to analyze.
   * @param  chiral                   is a chiral smiles wished?
   * @param  parent                   the atom we came from.
   * @param  atomsInOrderOfSmiles     a vector containing the atoms in the order they are in the smiles.
   * @param  currentChain             The chain we currently deal with.
   * @param  doubleBondConfiguration  Description of Parameter
   */
  private void parseAtom(Atom a, StringBuffer buffer, AtomContainer container, boolean chiral, boolean[] doubleBondConfiguration, Atom parent, Vector atomsInOrderOfSmiles, Vector currentChain) {
    String symbol = a.getSymbol();
    boolean stereo = isStereo(container, a);
    boolean brackets = symbol.equals("B") || symbol.equals("C") || symbol.equals("N") || symbol.equals("O") || symbol.equals("P") || symbol.equals("S") || symbol.equals("F") || symbol.equals("Br") || symbol.equals("I") || symbol.equals("Cl");
    brackets = !brackets;

    //Deal with the start of a double bond configuration
    if (isStartOfDoubleBond(container, a, parent, doubleBondConfiguration)) {
        buffer.append('/');
    }
    
    if (a instanceof PseudoAtom) {
        buffer.append("[*]");
    } else {
        String mass = generateMassString(a);
        brackets = brackets | !mass.equals("");
        
        String charge = generateChargeString(a);
        brackets = brackets | !charge.equals("");
        
        if (chiral && stereo) {
            brackets = true;
        }
        if (brackets) {
            buffer.append('[');
        }
        buffer.append(mass);
        if (a.getFlag(CDKConstants.ISAROMATIC)) {
            // Strictly speaking, this is wrong. Lower case is only used for sp2 atoms!
            buffer.append(a.getSymbol().toLowerCase());
        } else if (a.getHybridization() == CDKConstants.HYBRIDIZATION_SP2) {
            buffer.append(a.getSymbol().toLowerCase());
        } else {
            buffer.append(symbol);
        }
        if(a.getProperty(RING_CONFIG)!=null && a.getProperty(RING_CONFIG).equals(UP))
          buffer.append('/');
        if(a.getProperty(RING_CONFIG)!=null && a.getProperty(RING_CONFIG).equals(DOWN))
          buffer.append('\\');
        if (chiral && stereo && (isTrigonalBipyramidalOrOctahedral(container, a) || isSquarePlanar(container, a) || isTetrahedral(container, a) != 0)) {
            buffer.append('@');
        }
        if (chiral && stereo && isSquarePlanar(container, a)) {
            buffer.append("SP1");
        }
        //chiral
        //hcount
        buffer.append(charge);
        if (brackets) {
            buffer.append(']');
        }
    }
    //Deal with the end of a double bond configuration
    if (isEndOfDoubleBond(container, a, parent, doubleBondConfiguration)) {
      Atom viewFrom = null;
      for (int i = 0; i < currentChain.size(); i++) {
        if (currentChain.get(i) == parent) {
          int k = i - 1;
          while (k > -1) {
            if (currentChain.get(k) instanceof Atom) {
              viewFrom = (Atom) currentChain.get(k);
              break;
            }
            k--;
          }
        }
      }
      if (viewFrom == null) {
        for (int i = 0; i < atomsInOrderOfSmiles.size(); i++) {
          if (atomsInOrderOfSmiles.get(i) == parent) {
            viewFrom = (Atom) atomsInOrderOfSmiles.get(i - 1);
          }
        }
      }
      boolean afterThisAtom = false;
      Atom viewTo = null;
      for (int i = 0; i < currentChain.size(); i++) {
        if (afterThisAtom && currentChain.get(i) instanceof Atom) {
          viewTo = (Atom) currentChain.get(i);
          break;
        }
        if (afterThisAtom && currentChain.get(i) instanceof Vector) {
          viewTo = (Atom) ((Vector)currentChain.get(i)).get(0);
          break;
        }
        if (a == currentChain.get(i)) {
          afterThisAtom = true;
        }
      }
      boolean firstDirection = isLeft(viewFrom, a, parent);
      boolean secondDirection = isLeft(viewTo, parent, a);
      if (firstDirection == secondDirection) {
        buffer.append('\\');
      } else {
        buffer.append('/');
      }
    }
    Iterator it = getRingOpenings(a).iterator();
    while (it.hasNext()) {
      Integer integer = (Integer) it.next();
      buffer.append(integer);
    }
    atomsInOrderOfSmiles.add(a);
  }


  /**
   * Creates a string for the charge of atom <code>a</code>.  If the charge is 1 + is returned
   * if it is -1 - is returned.  The positive values all have + in front of them.
   *
   * @param  a  Description of Parameter
   * @return    string representing the charge on <code>a</code>
   */
  private String generateChargeString(Atom a) {
    int charge = a.getFormalCharge();
    StringBuffer buffer = new StringBuffer(3);
    if (charge > 0) {
      //Positive
      buffer.append('+');
      if (charge > 1) {
        buffer.append(charge);
      }
    } else if (charge < 0) {
      //Negative
      if (charge == -1) {
        buffer.append('-');
      } else {
        buffer.append(charge);
      }
    }
    return buffer.toString();
  }


  /**
   * Creates a string containing the mass of the atom <code>a</code>.  If the
   * mass is the same as the majour isotope an empty string is returned.
   *
   * @param  a  the atom to create the mass
   * @return    Description of the Returned Value
   */
  private String generateMassString(Atom a) {
    Isotope majorIsotope = isotopeFactory.getMajorIsotope(a.getSymbol());
    if (majorIsotope.getExactMass() == a.getExactMass()) {
      return "";
    } else if (a.getMassNumber() == 0) {
      return "";
    } else {
      return Integer.toString(a.getMassNumber());
    }
  }

  /**
  *  Description of the Class
  *
  * @author     shk3
  * @cdk.created    2003-06-17
  */
  class BrokenBond {
      
      
      /**
      *The atoms which close the ring
      */
      private Atom a1, a2;
      
      /**
      * The number of the marker
      */
      private int marker;
      
      
      /**
      * Construct a BrokenBond between <code>a1</code> and <code>a2</code> with
      * the marker <code>marker</code>.
      *
      * @param  marker  the ring closure marker. (Great comment!)
      * @param  a1      Description of Parameter
      * @param  a2      Description of Parameter
      */
      BrokenBond(Atom a1, Atom a2, int marker) {
          this.a1 = a1;
          this.a2 = a2;
          this.marker = marker;
      }
      
      
      /**
      * Getter method for a1 property
      *
      * @return    The a1 value
      */
      public Atom getA1() {
          return a1;
      }
      
      
      /**
      * Getter method for a2 property
      *
      * @return    The a2 value
      */
      public Atom getA2() {
          return a2;
      }
      
      
      /**
      * Getter method for marker property
      *
      * @return    The marker value
      */
      public int getMarker() {
          return marker;
      }
      
      
      /**
      *  Description of the Method
      *
      * @return    Description of the Returned Value
      */
      public String toString() {
          return Integer.toString(marker);
      }
      
      
      /**
      *  Description of the Method
      *
      * @param  o  Description of Parameter
      * @return    Description of the Returned Value
      */
      public boolean equals(Object o) {
          if (!(o instanceof BrokenBond)) {
              return false;
          }
          BrokenBond bond = (BrokenBond) o;
          return (a1.equals(bond.getA1()) && a2.equals(bond.getA2())) || (a1.equals(bond.getA2()) && a2.equals(bond.getA1()));
      }
  }
}



/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.smiles;

import org.openscience.cdk.*;
import org.openscience.cdk.aromaticity.AromaticityCalculator;
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.exception.Coordinates2DMissingException;

import java.util.*;
import java.io.IOException;
import java.text.NumberFormat;
import javax.vecmath.Vector2d;

/**
 * Generates SMILES strings.
 *
 * References:
 *   <a href="http://cdk.sf.net/biblio.html#WEI88">WEI88</a>,
 *   <a href="http://cdk.sf.net/biblio.html#WEI89">WEI89</a>
 *
 * @author  Oliver Horlacher, 
 * @author  Stefan Kuhn (chiral smiles)
 * @created Feb 26, 2002
 *
 * @keyword SMILES, generator
 */
public class SmilesGenerator {
  private static boolean debug = false;

  /**The number of rings that have been opened*/
  private int ringMarker = 0;

  /** Collection of all the bonds that were broken */
  private Vector brokenBonds = new Vector();

  /** The isotope factory which is used to write the mass is needed*/
  private IsotopeFactory isotopeFactory;

  /** The rings that are aromatic*/
  private Set arromaticRings = new HashSet();

  /** The canonical labler */
  private CanonicalLabeler canLabler = new CanonicalLabeler();

  /** This is used for remembering wich vector marks the end of a double bond configuration **/
  private Stack endOfDoubleBondConfiguration=new Stack();
  private Stack beginnOfDoubleBondConfiguration=new Stack();
  
  /**
   * Default constructor
   */
  public SmilesGenerator(){
    try {
      isotopeFactory = IsotopeFactory.getInstance();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Calls giveAngleBothMethods with bool = true
   *
   * @param from  the atom to view from
   * @param to1   first direction to look in
   * @param to2   second direction to look in
   * @return The angle in rad from 0 to 2*PI
   */
  private double giveAngle(Atom from, Atom to1, Atom to2){
    return (giveAngleBothMethods(from, to1, to2, true));
  }
  

  /**
   * Calls giveAngleBothMethods with bool = false
   *
   * @param from  the atom to view from
   * @param to1   first direction to look in
   * @param to2   second direction to look in
   * @return The angle in rad from -PI to PI
   */
  private double giveAngleFromMiddle(Atom from, Atom to1, Atom to2){
    return (giveAngleBothMethods(from, to1, to2, false));
  }

  /**
   * Gives the angle between two lines starting at atom from and going to
   * to1 and to2. If bool=false the angle starts from the middle line and goes from
   * 0 to PI or o to -PI if the to2 is on the left or right side of the line. If bool=true
   * the angle goes from 0 to 2PI.
   *
   * @param from  the atom to view from.
   * @param to1   first direction to look in.
   * @param to2   second direction to look in.
   * @param bool  true=angle is 0 to 2PI, false=angel is -PI to PI.
   * @return The angle in rad.
   */
  private double giveAngleBothMethods(Atom from, Atom to1, Atom to2, boolean bool){
    double[] A=new double[2];
    from.getPoint2D().get(A);
    double[] B=new double[2];
    to1.getPoint2D().get(B);
    double[] C=new double[2];
    to2.getPoint2D().get(C);
    double angle=Math.atan2(A[1]-B[1],A[0]-B[0])-Math.atan2(A[1]-C[1],A[0]-C[0]);
    if(angle<0 && bool)
      angle=(2*Math.PI)+angle;
    return(angle);
  }

  /**
   * Says if an atom is the end of a double bond configuration 
   *
   * @param atom      The atom which is the end of configuration
   * @param container The atomContainer the atom is in
   * @param parent    The atom we came from
   * @return false=is not end of configuration, true=is 
   */
  private boolean isEndOfDoubleBond(AtomContainer container, Atom atom, Atom parent, boolean[] doubleBondConfiguration){
    if(container.getBondNumber(atom,parent)==-1 || !doubleBondConfiguration[container.getBondNumber(atom,parent)])
      return false;
    int lengthAtom=container.getConnectedAtoms(atom).length+atom.getHydrogenCount();
    int lengthParent=container.getConnectedAtoms(parent).length+parent.getHydrogenCount();
    if(container.getBond(atom, parent)!=null){
      if(container.getBond(atom,parent).getOrder()==CDKConstants.BONDORDER_DOUBLE&&(lengthAtom==3 || (lengthAtom==2 && atom.getSymbol().equals("N")))&&(lengthParent==3 || (lengthParent==2 && parent.getSymbol().equals("N")))){
        Atom[] atoms=container.getConnectedAtoms(atom);
        Atom one=null;;
        Atom two=null;
        for(int i=0;i<atoms.length;i++){
          if(atoms[i]!=parent&&one==null){
            one=atoms[i];
            break;
          }
          if(atoms[i]!=parent&&one!=null)
            two=atoms[i];
        }
        if(two!=null&&one.getSymbol().equals(two.getSymbol()))
          return(false);
        else{
          return(true);
        }
      }
    }
    return(false);
  }
  
  /**
   * Says if an atom is the start of a double bond configuration 
   *
   * @param a         The atom which is the start of configuration
   * @param container The atomContainer the atom is in
   * @param parent    The atom we came from
   * @return false=is not start of configuration, true=is 
   */
  private boolean isBeginnOfDoubleBond(AtomContainer container, Atom a, Atom parent,boolean[] doubleBondConfiguration){
    int lengthAtom=container.getConnectedAtoms(a).length+a.getHydrogenCount();
    if(lengthAtom!=3 && (lengthAtom!=2 && a.getSymbol()!=("N")))
      return(false);
    Atom[] atoms=container.getConnectedAtoms(a);
    Atom one=null;
    Atom two=null;
    boolean doubleBond=false;
    Atom nextAtom=null;
    for(int i=0;i< atoms.length;i++){
      if(atoms[i]!=parent&& container.getBond(atoms[i],a).getOrder()==CDKConstants.BONDORDER_DOUBLE&&isEndOfDoubleBond(container,atoms[i],a,doubleBondConfiguration)){
        doubleBond=true;
        nextAtom=atoms[i];
      }
      if(atoms[i]!=parent&&one==null){
        one=atoms[i];
        break;
      }
      if(atoms[i]!=parent&&one!=null)
        two=atoms[i];
    }
    if(one!=two && doubleBond && doubleBondConfiguration[container.getBondNumber(a,nextAtom)])
      return(true);
    else
      return(false);
  }
  
   
  /**
   * Says if an atom as a center of a tetrahedral chirality 
   *
   * @param a         The atom which is the center
   * @param container The atomContainer the atom is in
   * @return 0=is not tetrahedral;>1 is a certain depiction of tetrahedrality (evaluated in parse chain) 
   */
  private int isTetrahedral(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    if(atoms.length!=4)
      return(0);
    Bond[] bonds=container.getConnectedBonds(a);
    int normal=0;
    int up=0;
    int down=0;
    for(int i=0;i<bonds.length;i++){
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UNDEFINED ||bonds[i].getStereo()==0)
        normal++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UP)
        up++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_DOWN)
        down++;
    }
    if(up==1 && down==1)
      return 1;
    if(up==2 && down==2){
      if(stereosAreOpposite(container, a)){
        return 2;
      }
      return 0;
    }
    if(up==1&&down==0)
      return 3;
    if(down==1&&up==0)
      return 4;
    return 0;
  }
  
  /**
   * Says if an atom as a center of a square planar chirality 
   *
   * @param a         The atom which is the center
   * @param container The atomContainer the atom is in
   * @return true=is square planar, false=is not
   */
  private boolean isSquarePlanar(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    if(atoms.length!=4)
      return(false);
    Bond[] bonds=container.getConnectedBonds(a);
    int normal=0;
    int up=0;
    int down=0;
    for(int i=0;i<bonds.length;i++){
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UNDEFINED ||bonds[i].getStereo()==0)
        normal++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UP)
        up++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_DOWN)
        down++;
    }
    if(up==2 && down==2 && !stereosAreOpposite(container, a))
      return true;
    return false;
  }

  /**
   * Says if an atom as a center of a trigonal-bipyramidal or actahedral chirality 
   *
   * @param a         The atom which is the center
   * @param container The atomContainer the atom is in
   * @return true=is square planar, false=is not
   */
  private boolean isTrigonalBipyramidalOrOctahedral(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    if(atoms.length<5 || atoms.length>6)
      return(false);
    Bond[] bonds=container.getConnectedBonds(a);
    int normal=0;
    int up=0;
    int down=0;
    for(int i=0;i<bonds.length;i++){
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UNDEFINED ||bonds[i].getStereo()==0)
        normal++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_UP)
        up++;
      if(bonds[i].getStereo()==CDKConstants.STEREO_BOND_DOWN)
        down++;
    }
    if(up==1 && down==1)
      return true;
    return false;
  }
  
  /**
   * Says if of four atoms connected two one atom the up and down bonds are
   * opposite or not, i. e.if it's tetrehedral or square planar. The method doesnot check if 
   * there are four atoms and if two or up and two are down
   *
   * @param a         The atom which is the center
   * @param container The atomContainer the atom is in
   * @return true=are opposite, false=are not
   */
  private boolean stereosAreOpposite(AtomContainer container, Atom a) {
    Vector atoms=container.getConnectedAtomsVector(a);
    TreeMap hm=new TreeMap();
    for(int i=1;i<atoms.size();i++){
      hm.put(new Double(giveAngle(a, (Atom)atoms.get(0), ((Atom)atoms.get(i)))),new Integer(i));
    }
    Object[] ohere=hm.values().toArray();
    int stereoOne=container.getBond(a,(Atom)atoms.get(0)).getStereo();
    int stereoOpposite=container.getBond(a,(Atom)atoms.get((((Integer)ohere[1])).intValue())).getStereo();
    if(stereoOpposite==stereoOne)
      return true;
    else
      return false;
  }

  /**
   * Says if an atom as a center of any valid stereo configuration or not
   *
   * @param a         The atom which is the center
   * @param container The atomContainer the atom is in
   * @return true=is a stereo atom, false=is not
   */
  private boolean isStereo(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    if(atoms.length<4 || atoms.length>6)
      return(false);
    Bond[] bonds=container.getConnectedBonds(a);
    int stereo=0;
    for(int i=0;i<bonds.length;i++){
      if(bonds[i].getStereo()!=0){
        stereo++;
      }
    }
    if(stereo==0)
      return false;
    int differentAtoms=0;
    for(int i=0;i<atoms.length;i++){
      boolean isDifferent=true;
      for(int k=0;k<i;k++){
        if(atoms[i].getSymbol().equals(atoms[k].getSymbol())){
          isDifferent=false;
          break;
        }
      }
      if(isDifferent)
        differentAtoms++;
    }
    if(differentAtoms!=atoms.length){
        int[] morgannumbers= MorganNumbersTools.getMorganNumbers(container);
        Vector differentSymbols=new Vector();
        for(int i=0;i<atoms.length;i++){
          if(!differentSymbols.contains(atoms[i].getSymbol()))
            differentSymbols.add(atoms[i].getSymbol());
        }
        int[] onlyRelevantIfTwo=new int[2];
        if(differentSymbols.size()==2){
          for(int i=0;i<atoms.length;i++){
            if(differentSymbols.indexOf(atoms[i].getSymbol())==0)
              onlyRelevantIfTwo[0]++;
            else
              onlyRelevantIfTwo[1]++;
          }
        }
        boolean[] symbolsWithDifferentMorganNumbers=new boolean[differentSymbols.size()];
        Vector[] symbolsMorganNumbers=new Vector[differentSymbols.size()];
        for(int i=0;i<symbolsWithDifferentMorganNumbers.length;i++){
          symbolsWithDifferentMorganNumbers[i]=true;
          symbolsMorganNumbers[i]=new Vector();
        }
        for(int k=0;k<atoms.length;k++){
          int elementNumber=differentSymbols.indexOf(atoms[k].getSymbol());
          if(symbolsMorganNumbers[elementNumber].contains(new Integer(morgannumbers[container.getAtomNumber(atoms[k])])))
            symbolsWithDifferentMorganNumbers[elementNumber]=false;
          else
            symbolsMorganNumbers[elementNumber].add(new Integer(morgannumbers[container.getAtomNumber(atoms[k])]));
        }
        int numberOfSymbolsWithDifferentMorganNumbers=0;
        for(int i=0;i<symbolsWithDifferentMorganNumbers.length;i++){
          if(symbolsWithDifferentMorganNumbers[i]==true)
            numberOfSymbolsWithDifferentMorganNumbers++;
        }
        if(numberOfSymbolsWithDifferentMorganNumbers!=differentSymbols.size()){
          if(stereo==1&&atoms.length==4){
            for(int i=0;i<atoms.length;i++){
              RingSet rs=new SSSRFinder().findSSSR((Molecule)container);
              RingSet rs1=rs.getRings(a);
              RingSet rs2=rs1.getRings(atoms[i]);
              if(rs2.size()>1){
                return true;
              }
            }
          }
          if((atoms.length==5 || atoms.length==6) && (numberOfSymbolsWithDifferentMorganNumbers+differentAtoms>2 || (differentAtoms==2 && onlyRelevantIfTwo[0]>1 && onlyRelevantIfTwo[1]>1)))
            return(true);
          if(isSquarePlanar(container,a) && (numberOfSymbolsWithDifferentMorganNumbers+differentAtoms>2 || (differentAtoms==2 && onlyRelevantIfTwo[0]>1 && onlyRelevantIfTwo[1]>1)))
            return(true);
          return false;
        }
    }
    return(true);
  }
  
  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule.
   *
   * @see org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel
   *
   */
  public synchronized String createSMILES(Molecule molecule) {
    try{
      return (createSMILES(molecule, false, new boolean[molecule.getBondCount()]));
    }
    catch(Coordinates2DMissingException ex){return("");}//This exception can only happen if a chiral smiles is requested
  }
  
  /**
   * Generate canonical and chiral SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule. The chiral smiles is done like in the <a href="http://www.daylight.com/dayhtml/doc/theory/theory.smiles.html">daylight theory manual</a>.
   * I did not find rules for canonical and chiral smiles, therefore there is no guarantee 
   * that the smiles complies to any externeal rules, but it is canonical compared to other smiles
   * produced by this method. The method checks if there are 2D coordinates but does not do
   * check if coordinates make sense. Invalid stereo configurations are ignored; if there are no 
   * valid stereo configuration the smiles will be the same as the non-chiral one.
   *
   * @exception  Coordinates2DMissingException  At least one atom has no Point2D; coordinates are needed for crating the chiral smiles.
   * @param molecule The molecule to evaluate
   * @param isDoubleBondSpecified[] Should double bond configurations be evaluated. If bond X (numbering as in the bonds array) should be evaluated isDoubleBondSpecified[x] should be true, if not false.
   * @see org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel.
   *
   */
  public synchronized String createChiralSMILES(Molecule molecule, boolean[] doubleBondConfiguration) throws Coordinates2DMissingException {
    return(createSMILES(molecule, true, doubleBondConfiguration));
  }

  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule.
   *
   * @see org.openscience.cdk.graph.invariant.CanonicalLabeler#canonLabel.
   * @param molecule The molecule to evaluate
   * @param chiral true=SMILES will be chiral, false=SMILES will not be chiral.
   * @param isDoubleBondSpecified[] Should double bond configurations be evaluated. If bond X (numbering as in the bonds array) should be evaluated isDoubleBondSpecified[x] should be true, if not false.
   * @exception  Coordinates2DMissingException  At least one atom has no Point2D; coordinates are needed for crating the chiral smiles. This excpetion can only be thrown if chiral smiles is created, ignore it if you want a non-chiral smiles (createSMILES(AtomContainer) does not throw an exception).
   *
   */
  public synchronized String createSMILES(Molecule molecule, boolean chiral, boolean doubleBondConfiguration[]) throws Coordinates2DMissingException{
    if (molecule.getAtomCount() == 0)
      return "";
    canLabler.canonLabel(molecule);
    brokenBonds.clear();
    ringMarker = 0;
    arromaticRings.clear();
    Atom[] all = molecule.getAtoms();
    Atom start = null;
    for (int i = 0; i < all.length; i++) {
      Atom atom = all[i];
      if(chiral && atom.getPoint2D()==null)
        throw new Coordinates2DMissingException("Atom number "+i+" has no 2D coordinates");
      if (atom.flags == null) atom.flags = new boolean[100];
      atom.flags[CDKConstants.VISITED] = false;
      if (((Long)atom.getProperty("CanonicalLable")).longValue() == 1) {
        start = atom;
      }
    }

    //Sort aromatic rings
    SSSRFinder ringFinder = new SSSRFinder();
    RingSet rings = ringFinder.findSSSR(molecule);
    Iterator it = rings.iterator();
    while (it.hasNext()) {
      Ring ring = (Ring) it.next();
      if(AromaticityCalculator.isAromatic(ring,  molecule)){
        arromaticRings.add(ring);
      }
    }

    StringBuffer l = new StringBuffer();
    createSMILES(start, l, molecule, chiral, doubleBondConfiguration);
    return l.toString();
  }

  /**
   * Performes a DFS search on the <code>atomContainer</code>.  Then parses the resulting
   * tree to create the SMILES string.
   *
   * @param a the atom to start the search at.
   * @param line the StringBuffer that the SMILES is to be appended to.
   * @param chiral true=SMILES will be chiral, false=SMILES will not be chiral.
   * @param isDoubleBondSpecified[] Should double bond configurations be evaluated. If bond X (numbering as in the bonds array) should be evaluated isDoubleBondSpecified[x] should be true, if not false.
   * @param atomContainer the AtomContainer that the SMILES string is generated for.
   */
  private void createSMILES(Atom a, StringBuffer line, AtomContainer atomContainer, boolean chiral, boolean[] doubleBondConfiguration) {
    Vector tree = new Vector();
    createDFSTree(a, tree, null, atomContainer); 
    parseChain(tree, line, atomContainer, null, chiral, doubleBondConfiguration,null);
  }

  /**
   * Recursively perform a DFS search on the <code>container</code> placing atoms and branches in the
   * vector <code>tree</code>.
   *
   * @param a the atom being visited.
   * @param tree vector holding the tree.
   * @param parent the atom we came from.
   * @param container the AtomContainer that we are parsing.
   */
  private void createDFSTree(Atom a, Vector tree, Atom parent, AtomContainer container) {
    tree.add(a);
    Vector neighbours = getCanNeigh(a, container);
    neighbours.remove(parent);
    Atom next;
    a.flags[CDKConstants.VISITED] = true;
    for(int x = 0; x < neighbours.size(); x++) {
      next = (Atom)neighbours.elementAt(x);
      if (!next.flags[CDKConstants.VISITED]) {
        if(x == neighbours.size() - 1) { //Last neighbour therefore in this chain
          createDFSTree(next, tree, a, container);
        }
        else {
          Vector branch = new Vector();
          tree.add(branch);
          createDFSTree(next, branch, a, container);
        }
      }
      else { //Found ring closure between next and a
        ringMarker++;
        BrokenBond bond = new BrokenBond(a, next, ringMarker);
        if(!brokenBonds.contains(bond))
          brokenBonds.add(bond);
        else
          ringMarker--;
      }
    }
  }

  private boolean isBondBroken(Atom a1, Atom a2) {
    Iterator it = brokenBonds.iterator();
    while (it.hasNext()) {
      BrokenBond bond=((BrokenBond) it.next());
      if((bond.getA1().equals(a1)|| bond.getA1().equals(a2))&&(bond.getA2().equals(a1)|| bond.getA2().equals(a2))){
        return(true);
      }
    }
    return false;
  }

  /**
   * Parse a branch
   */
  private void parseChain(Vector v, StringBuffer buffer, AtomContainer container, Atom parent, boolean chiral, boolean[] doubleBondConfiguration, Vector vectorBefore){
    int positionInVector=0;
    Atom atom;
    for(int h=0;h<v.size();h++){
      Object o=v.get(h);
      if(o instanceof Atom) {
        atom = (Atom)o;
        if(parent != null) {
          parseBond(buffer, atom, parent, container);
        }
        else
        {
          if(chiral&&isStereo(container,atom))
            parent=(Atom)((Vector)v.get(1)).get(0);
        }
        parseAtom(atom, buffer, container, chiral,doubleBondConfiguration,parent);
        /*The principle of making chiral smiles is quite simple, although the code is
        pretty uggly. The Atoms connected to the chiral center are put in sorted[] in the
        order they have to appear in the smiles. Then the Vector v is rearranged according 
        to sorted[]*/
      	if(chiral && isStereo(container,atom)){
          Atom[] sorted=null;
          Vector chiralNeighbours=container.getConnectedAtomsVector(atom);
          if(isTetrahedral(container,atom)>0)
            sorted=new Atom[3];
          if(isTetrahedral(container,atom)==1){
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_UP){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_UNDEFINED||container.getBond(parent,atom).getStereo()==0){
              boolean normalBindingIsLeft=false;
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0){
                    if(isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)){
                      normalBindingIsLeft=true;
                      break;
                    }
                  }
                }
              }
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                if(normalBindingIsLeft){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                }
                else
                {
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                }
                }
              }
            }
          }
          if(isTetrahedral(container,atom)==2){
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_UP){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
          }
          if(isTetrahedral(container,atom)==3){
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_UP){
              TreeMap hm=new TreeMap();
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                  hm.put(new Double(giveAngle(atom, parent, ((Atom)chiralNeighbours.get(i)))),new Integer(i));
                }
              }
              Object[] ohere=hm.values().toArray();
              for(int i=ohere.length-1;i>-1;i--){
                sorted[i]=((Atom)chiralNeighbours.get(((Integer)ohere[i]).intValue()));
              }
            }
            if(container.getBond(parent,atom).getStereo()==0){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
          }
          if(isTetrahedral(container,atom)==4){
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
              TreeMap hm=new TreeMap();
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                  hm.put(new Double(giveAngle(atom, parent, ((Atom)chiralNeighbours.get(i)))),new Integer(i));
                }
              }
              Object[] ohere=hm.values().toArray();
              for(int i=ohere.length-1;i>-1;i--){
                sorted[i]=((Atom)chiralNeighbours.get(((Integer)ohere[i]).intValue()));
              }
            }
            if(container.getBond(parent,atom).getStereo()==0){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isLeft(((Atom)chiralNeighbours.get(i)),parent,atom)&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                }
              }
            }
          }
          if(isSquarePlanar(container,atom)){
            sorted=new Atom[3];
            //This produces a U=SP1 order in every case
            TreeMap hm=new TreeMap();
            for(int i=0;i<chiralNeighbours.size();i++){
              if(chiralNeighbours.get(i)!=parent&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                hm.put(new Double(giveAngle(atom, parent, ((Atom)chiralNeighbours.get(i)))),new Integer(i));
              }
            }
            Object[] ohere=hm.values().toArray();
            for(int i=0;i<ohere.length;i++){
              sorted[i]=((Atom)chiralNeighbours.get(((Integer)ohere[i]).intValue()));
            }
          }
          if(isTrigonalBipyramidalOrOctahedral(container, atom)){
            sorted=new Atom[container.getConnectedAtoms(atom).length-1];
            TreeMap hm=new TreeMap();
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_UP){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==0)
                  hm.put(new Double(giveAngle(atom, parent, ((Atom)chiralNeighbours.get(i)))),new Integer(i));
                if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==CDKConstants.STEREO_BOND_DOWN)
                  sorted[sorted.length-1]=(Atom)chiralNeighbours.get(i);
              }
              Object[] ohere=hm.values().toArray();
              for(int i=0;i<ohere.length;i++){
                sorted[i]=((Atom)chiralNeighbours.get(((Integer)ohere[i]).intValue()));
              }
            }
            if(container.getBond(parent,atom).getStereo()==CDKConstants.STEREO_BOND_DOWN){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==0)
                  hm.put(new Double(giveAngle(atom, parent, ((Atom)chiralNeighbours.get(i)))),new Integer(i));
                if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==CDKConstants.STEREO_BOND_UP)
                  sorted[sorted.length-1]=(Atom)chiralNeighbours.get(i);
              }
              Object[] ohere=hm.values().toArray();
              for(int i=0;i<ohere.length;i++){
                sorted[i]=((Atom)chiralNeighbours.get(((Integer)ohere[i]).intValue()));
              }
            }
            if(container.getBond(parent,atom).getStereo()==0){
              for(int i=0;i<chiralNeighbours.size();i++){
                if(chiralNeighbours.get(i)!=parent){
                  if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==0)
                    hm.put(new Double((giveAngleFromMiddle(atom, parent, ((Atom)chiralNeighbours.get(i))))),new Integer(i));
                  if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==CDKConstants.STEREO_BOND_UP)
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  if(container.getBond(atom,(Atom)chiralNeighbours.get(i)).getStereo()==CDKConstants.STEREO_BOND_DOWN)
                    sorted[sorted.length-2]=(Atom)chiralNeighbours.get(i);
                }
              }
              Object[] ohere=hm.values().toArray();
              sorted[sorted.length-1]=((Atom)chiralNeighbours.get(((Integer)ohere[ohere.length-1]).intValue()));
              if(ohere.length==2){
                sorted[sorted.length-3]=((Atom)chiralNeighbours.get(((Integer)ohere[0]).intValue()));
                if(giveAngleFromMiddle(atom, parent, ((Atom)chiralNeighbours.get(((Integer)ohere[1]).intValue())))<0){
                  Atom dummy=sorted[sorted.length-2];
                  sorted[sorted.length-2]=sorted[0];
                  sorted[0]=dummy;
                }
              }
              if(ohere.length==3){
                sorted[sorted.length-3]=sorted[sorted.length-2];
                sorted[sorted.length-2]=((Atom)chiralNeighbours.get(((Integer)ohere[ohere.length-2]).intValue()));
                sorted[sorted.length-4]=((Atom)chiralNeighbours.get(((Integer)ohere[ohere.length-3]).intValue()));
              }
            }
          }
          if(sorted!=null){
            int numberOfAtoms=3;
            if(isTrigonalBipyramidalOrOctahedral(container, atom))
              numberOfAtoms=container.getConnectedAtoms(atom).length-1;
            Object[] omy=new Object[numberOfAtoms];
            Object[] onew=new Object[numberOfAtoms];
            for(int k=getRingOpenings(atom).size();k<numberOfAtoms;k++){
              omy[k]=v.get(positionInVector+1+k-getRingOpenings(atom).size());
            }
            for(int k=0;k<sorted.length;k++){
              if(sorted[k]!=null){
                for(int m=0;m<omy.length;m++){
                  if(omy[m] instanceof Atom){
                    if(omy[m]==sorted[k]){
                      onew[k]=omy[m];
                    }
                  }
                  else
                  {
                    if(omy[m]==null){
                      onew[k]=null;
                    }
                    else
                    {
                      if(((Vector)omy[m]).get(0)==sorted[k]){
                        onew[k]=omy[m];
                      }
                    }
                  }
                }
              }
              else
              {
                onew[k]=null;
              }
            }
            if(onew[numberOfAtoms-1] instanceof Vector){
              for(int i=0;i<numberOfAtoms;i++){
                if(onew[i] instanceof Atom){
                  Vector vtemp=new Vector();
                  vtemp.add(onew[i]);
                  for(int k=positionInVector+1+numberOfAtoms;k<v.size();k++){
                      vtemp.add(v.get(k));
                  }
                  Vector vtemp2=new Vector();
                  for(int k=0;k<positionInVector+1+numberOfAtoms-1;k++){
                    if(k==positionInVector+1+i){
                      vtemp2.add(vtemp);
                    }
                    else
                    {
                      if(k>positionInVector){
                        vtemp2.add(onew[k-positionInVector-1]);
                      }
                      else
                      {
                        vtemp2.add(v.get(k));
                      }
                    }
                  }
                  for(int k=0;k<((Vector)onew[numberOfAtoms-1]).size();k++){
                    vtemp2.add(((Vector)onew[numberOfAtoms-1]).get(k));
                  }
                  break;
                }
              }
            }
            else
            {
              int k=0;
              for(int m=0;m<onew.length;m++){
                if(onew[m]!=null){
                  v.set(positionInVector+1+k,onew[m]);
                  k++;
                }
              }
            }
          }
        }
        //Check if this atom represents the end of a double bond configuration
        if(atom!=null&&parent!=null && isEndOfDoubleBond(container,atom,parent,doubleBondConfiguration)){
          int position=-1;
          //if the next atom is part of a vector, i. e. a branch, we need to remember the end of the configuration
          if(v.get(positionInVector+1) instanceof Vector){
            //We distingiush if we are at the start of the smiles or not
            if(positionInVector>0){
              //We push the atom we came from when "walking" throught the molecule on the stack and also the number of the next atom
              if(v.get(positionInVector-1) instanceof Vector){
                endOfDoubleBondConfiguration.push(((Vector)v.get(positionInVector-1)).get(0));
              }
              else
                endOfDoubleBondConfiguration.push((Atom)v.get(positionInVector-2));
              endOfDoubleBondConfiguration.push(new Integer(positionInVector+1));
            }else{
              for(int i=0;i<vectorBefore.size();i++){
                if(vectorBefore.get(i) instanceof Atom && ((Atom)vectorBefore.get(i)).getPoint2D().equals(parent.getPoint2D()))
                  position=i;
              }
              if(position>0){
                //We push the atom we came from when "walking" throught the molecule on the stack and also the number of the next atom
                if(vectorBefore.get(position-1) instanceof Atom)
                  endOfDoubleBondConfiguration.push(vectorBefore.get(position-1));
                else
                  endOfDoubleBondConfiguration.push((((Vector)vectorBefore.get(position-1)).get(0)));
                endOfDoubleBondConfiguration.push(new Integer(positionInVector+1));
              }
            }
          }
          else
          {
            //if the next atom is only an atom, we can append / or \ directly
            Atom viewFrom=null;
            if(positionInVector>2){
              if(v.get(positionInVector-2) instanceof Atom)
                viewFrom=(Atom)v.get(positionInVector-2);
              else
                viewFrom=(Atom)((Vector)v.get(positionInVector-2)).get(0);
            }else{
              if(vectorBefore==null)
                viewFrom=(Atom)v.get(positionInVector-1);
              else
                viewFrom=(Atom)vectorBefore.get(position-1);
            }
            boolean oldAtom=isLeft(viewFrom,atom, parent);
            boolean newAtom=isLeft((Atom)v.get(positionInVector+1),parent,atom);
            if(oldAtom==newAtom)
              buffer.append('\\');
            else
              buffer.append('/');
          }
        }
        parent = atom;
      }
      else { //Have Vector
        boolean brackets = true;
        if(isRingOpening(parent) && container.getBondCount(parent) < 4)
          brackets = false;
        if(brackets)
          buffer.append('(');
        parseChain((Vector)o, buffer, container, parent, chiral, doubleBondConfiguration,v);
        if(brackets)
          buffer.append(')');
        //Check if we remembered the start of double bond configuration by checking if there is an atom on the respective stack
        if(!beginnOfDoubleBondConfiguration.empty()){
          Atom beginn=(Atom)beginnOfDoubleBondConfiguration.pop();
          //If there is also the end of the configuration remembered, we need to handle it
          if(container.getBondNumber(beginn,parent)!=-1 && doubleBondConfiguration[container.getBondNumber(beginn,parent)]){
            if(!endOfDoubleBondConfiguration.empty()){
              Integer integer=(Integer)endOfDoubleBondConfiguration.pop();
              Atom viewFrom=(Atom)endOfDoubleBondConfiguration.pop();
              //Make sure we are already processing the atom which represents the end of the configuration
              if(v.indexOf(o)==integer.intValue())
              {
                /*Now view From represents the atom the configuration starts with, 
                  parent the atom at the end of the double bond. We need to find the
                  the atom at the start of the double bond, which we will call between
                */
                Atom[] atomsOfParent=container.getConnectedAtoms(parent);
                Atom[] atomsOfViewFrom=container.getConnectedAtoms(viewFrom);
                Atom between=null;
                for(int i=0;i<atomsOfParent.length;i++){
                  for(int k=0;k<atomsOfViewFrom.length;k++){
                    if(atomsOfParent[i]==atomsOfViewFrom[k])
                      between=atomsOfParent[i];
                  }
                }
                if(between!=null){
                  boolean oldAtom=isLeft(viewFrom,parent,between);
                  boolean newAtom=isLeft((Atom)v.get(positionInVector+1),between,parent);
                  if(oldAtom==newAtom)
                    buffer.append('/');
                  else
                    buffer.append('\\');
                }
              }
              else
              {
                //if not still remember the end
                endOfDoubleBondConfiguration.push(viewFrom);
                endOfDoubleBondConfiguration.push(integer);
              }
            }
          }
          else
          {
            //if the end is not yet reached, we keep the beginning by pushing it back on the stack
            beginnOfDoubleBondConfiguration.push(beginn);
          }
        }
      }
      positionInVector++;
    }
  }
  
  /**
   * Says if an atom is on the left side of a another atom seen from 
   * a certain atom or not
   *
   * @param whereIs  The atom the position of which is returned
   * @param viewFrom The atom from which to look
   * @param viewTo   The atom to which to look
   * @return true=is left, false = is not
   *
   */
  private boolean isLeft(Atom whereIs, Atom viewFrom, Atom viewTo){
    double[] A=new double[2];
    viewFrom.getPoint2D().get(A);
    double[] B=new double[2];
    viewTo.getPoint2D().get(B);
    double[] C=new double[2];
    whereIs.getPoint2D().get(C);
    double[] D=new double[2];
    whereIs.getPoint2D().get(D);
    D[0]=D[0]-1;
    double d0 = A[0]*B[1] - A[1]*B[0];
    double d1 = C[0]*D[1] - C[1]*D[0];
    double den = (B[1]-A[1])*(C[0]-D[0]) - (A[0]-B[0])*(D[1]-C[1]);
    double x = (d0*(C[0]-D[0]) - d1*(A[0]-B[0])) / den;
    double y = (d1*(B[1]-A[1]) - d0*(D[1]-C[1])) / den;
    if(y>C[1]){
      if(x>C[0])
        return false;
      else
        return true;
    } else {
      if(x>C[0])
        return true;
      else
        return false;
    }
  }

  /**
   * Determines if the atom <code>a</code> is a atom with a ring
   * marker.
   *
   * @param a the atom to test
   * @return true if the atom participates in a bond that was broken in the first pass.
   */
  private boolean isRingOpening(Atom a) {
    Iterator it = brokenBonds.iterator();
    while (it.hasNext()) {
      BrokenBond bond = (BrokenBond) it.next();
      if(bond.getA1().equals(a) || bond.getA2().equals(a))
        return true;
    }
    return false;
  }

  /**
   * Return the neighbours of atom <code>a</code> in canonical order with the atoms that have
   * high bond order at the front.
   *
   * @param a the atom whose neighbours are to be found.
   * @param container the AtomContainer that is being parsed.
   * @return Vector of atoms in canonical oreder.
   */
  private Vector getCanNeigh(final Atom a, final AtomContainer container) {
    Vector v = container.getConnectedAtomsVector(a);
    if (v.size() > 1) {
      Collections.sort(v, new Comparator() {
        public int compare(Object o1, Object o2) {
          return (int) (((Long)((Atom) o1).getProperty("CanonicalLable")).longValue() - ((Long)((Atom) o2).getProperty("CanonicalLable")).longValue());
        }
      });
    }
    return v;
  }

  /**
   * Append the symbol for the bond order between <code>a1</code> and <code>a2</code> to
   * the <code>line</code>.
   *
   * @param line the StringBuffer that the bond symbol is appended to.
   * @param a1 Atom participating in the bond.
   * @param a2 Atom participating in the bond.
   * @param atomContainer the AtomContainer that the SMILES string is generated for.
   */
  private void parseBond(StringBuffer line, Atom a1, Atom a2, AtomContainer atomContainer) {
    if(isAromatic(a1) && isAromatic(a2)) {
      return;
    }
    if(atomContainer.getBond(a1, a2)==null)
      return;
    int type=0;
      type = (int)atomContainer.getBond(a1, a2).getOrder();
    if (type == 1) {
    } else if (type == 2) {
      line.append("=");

    } else if (type == 3) {
      line.append("#");
    } else
      System.out.println("Unknown bond type");
  }

  /**
   * Generates the SMILES string for the atom
   *
   * @param atom the atom to generate the SMILES for
   * @param buffer the string buffer that the atom is to be apended to.
   * @param isDoubleBondSpecified[] Should double bond configurations be evaluated. If bond X (numbering as in the bonds array) should be evaluated isDoubleBondSpecified[x] should be true, if not false.
   */
  private void parseAtom(Atom a, StringBuffer buffer, AtomContainer container, boolean chiral, boolean[] doubleBondConfiguration, Atom parent) {
    String symbol = a.getSymbol();
    boolean stereo=isStereo(container,a);
    boolean brackets = symbol.equals("B") || symbol.equals("C") || symbol.equals("N") || symbol.equals("O") || symbol.equals("P") || symbol.equals("S") || symbol.equals("F") || symbol.equals("Br") || symbol.equals("I") || symbol.equals("Cl");
    brackets = !brackets;

    String mass = generateMassString(a);
    brackets = brackets | !mass.equals("");

    String charge = generateChargeString(a);
    brackets = brackets | !charge.equals("");

    if(chiral && stereo)
      brackets=true;
    //If this atom is the start of a double bond configuration, we append the / and remember this atom via pushing it on a stack
    if(isBeginnOfDoubleBond(container,a,parent,doubleBondConfiguration)){
      buffer.append('/');
      beginnOfDoubleBondConfiguration.push(a);
    }
    if(brackets)
      buffer.append('[');
    buffer.append(mass);
    if(isAromatic(a))
     buffer.append(a.getSymbol().toLowerCase());
    else
      buffer.append(symbol);
    if(chiral && stereo)
      buffer.append('@');
    if(chiral && stereo && isSquarePlanar(container,a))
      buffer.append("SP1");
    //chiral
    //hcount
    buffer.append(charge);
    if(brackets)
      buffer.append(']');

    Iterator it = getRingOpenings(a).iterator();
    while (it.hasNext()) {
      Integer integer = (Integer) it.next();
      buffer.append(integer);
    }
  }

  /**
   * Creates a string for the charge of atom <code>a</code>.  If the charge is 1 + is returned
   * if it is -1 - is returned.  The positive values all have + in front of them.
   *
   * @return string representing the charge on <code>a</code>
   */
  private String generateChargeString(Atom a) {
    int charge = (int)a.getCharge();
    StringBuffer buffer = new StringBuffer(3);
    if(charge > 0) { //Positive
      buffer.append('+');
      if(charge > 1) {
        buffer.append(charge);
      }
    }
    else if(charge < 0) { //Negative
      if(charge == -1) {
        buffer.append('-');
      }
      else {
        buffer.append(charge);
      }
    }
    return buffer.toString();
  }

  /**
   * Creates a string containing the mass of the atom <code>a</code>.  If the
   * mass is the same as the majour isotope an empty string is returned.
   *
   * @param a the atom to create the mass
   */
  private String generateMassString(Atom a) {
    Isotope majorIsotope = isotopeFactory.getMajorIsotope(a.getSymbol());
    if(majorIsotope.getExactMass() == a.getExactMass()) {
      return "";
    } else if (a.getAtomicMass() == 0) {
			return "";
	  } else {
      return Integer.toString(a.getAtomicMass());
		}
  }

  private Vector getRingOpenings(Atom a) {
    Iterator it = brokenBonds.iterator();
    Vector v = new Vector(10);
    while (it.hasNext()) {
      BrokenBond bond = (BrokenBond) it.next();
      if(bond.getA1().equals(a) || bond.getA2().equals(a)) {
        v.add(new Integer(bond.getMarker()));
      }
    }
    Collections.sort(v);
    return v;
  }

  /**
   * Is the atom in a ring that is arromatic.
   *
   * @param a the atom to test
   * @return true if a is in a aromatic ring false otherwise
   */
  private boolean isAromatic(Atom a) {
    Iterator it = arromaticRings.iterator();
    while (it.hasNext()) {
      Ring ring = (Ring) it.next();
      if(ring.contains(a))
        return true;
    }
    return false;
  }

  /**
   * Returns true if the <code>atom</code> in the <code>container</code> has
   * been marked as a chiral center by the user.
   */
  private boolean isChiralCenter(Atom atom, AtomContainer container) {
    Bond[] bonds = container.getConnectedBonds(atom);
    for (int i = 0; i < bonds.length; i++) {
      Bond bond = bonds[i];
      int stereo = bond.getStereo();
      if(stereo == CDKConstants.STEREO_BOND_DOWN || 
         stereo == CDKConstants.STEREO_BOND_UP) {
        return true;
      }
    }
    return false;
  }
}

class BrokenBond {
  /**The atoms which close the ring */
  private Atom a1, a2;

  /** The number of the marker */
  private  int marker;
  /**
   * Construct a BrokenBond between <code>a1</code> and <code>a2</code> with
   * the marker <code>marker</code>.
   *
   * @param a1, a2 the atoms involved in the ring closure.
   * @param marker the ring closure marker. (Great comment!)
   */
  BrokenBond(Atom a1, Atom a2, int marker) {
    this.a1 = a1;
    this.a2 = a2;
    this.marker = marker;
  }

  /**
   * Getter method for a1 property
   */
  public Atom getA1() {
    return a1;
  }

  /**
   * Getter method for a2 property
   */
  public Atom getA2() {
    return a2;
  }

  /**
   * Getter method for marker property
   */
  public int getMarker() {
    return marker;
  }

  public String toString() {
    return Integer.toString(marker);
  }

  public boolean equals(Object o){
    if(!(o instanceof BrokenBond))
      return false;
    BrokenBond bond = (BrokenBond)o;
    return (a1.equals(bond.getA1()) && a2.equals(bond.getA2())) || (a1.equals(bond.getA2()) && a2.equals(bond.getA1()));
  }
}




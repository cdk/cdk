/* AromaticityCalculator.java
 *
 * $ author: 	Oliver Horlacher		$
 * $ contact: 	oliver.horlacher@therastrat.com 	$
 * $ date: 		Feb 26, 2002			$
 *
 * Copyright (C) 2001-2002
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
import org.openscience.cdk.tools.IsotopeFactory;
import org.openscience.cdk.graphinvariant.MorganNumbersTools;
import org.openscience.cdk.test.MoleculeFactory;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.exception.NoSuchAtomException;

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
 * @author Oliver Horlacher
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
  
  private double giveAngle(Atom from, Atom to1, Atom to2){
    double[] A=new double[2];
    from.getPoint2D().get(A);
    double[] B=new double[2];
    to1.getPoint2D().get(B);
    double[] C=new double[2];
    to2.getPoint2D().get(C);
    return(Math.atan2(A[1]-B[1],A[0]-B[0])-Math.atan2(A[1]-C[1],A[0]-C[0]));
  }

  public int isTetrahedral(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
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
    return 0;
  }
  
  public boolean stereosAreOpposite(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    int stereoOne=container.getBond(a,atoms[0]).getStereo();
    double largestAngle=0;
    int oppositeAtom=0;
    for(int i=1;i<4;i++){
      double angle=giveAngle(a,atoms[0],atoms[i]);
      if(angle>largestAngle){
        largestAngle=angle;
        oppositeAtom=i;
      }
    }
    if(container.getBond(a,atoms[oppositeAtom]).getStereo()==stereoOne)
      return true;
    else
      return false;
  }
      

  public boolean isStereo(AtomContainer container, Atom a) {
    Atom[] atoms=container.getConnectedAtoms(a);
    if(atoms.length!=4)
      return(false);
    Bond[] bonds=container.getConnectedBonds(a);
    boolean stereo=false;
    for(int i=0;i<bonds.length;i++){
      if(bonds[i].getStereo()!=0){
        stereo=true;
      }
    }
    if(!stereo)
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
      try{
        int[] morgannumbers= MorganNumbersTools.getMorganNumbers(container);
        Vector differentSymbols=new Vector();
        for(int i=0;i<atoms.length;i++){
          if(!differentSymbols.contains(atoms[i].getSymbol()))
            differentSymbols.add(atoms[i].getSymbol());
        }
        int symbolsWithDifferentMorganNumbers=differentSymbols.size();
        for(int i=0;i<differentSymbols.size();i++){
          int firstMorganNumber=-1;
          for(int k=0;k<atoms.length;k++){
            if(((String)differentSymbols.get(i)).equals(atoms[k].getSymbol())){
              if(firstMorganNumber==-1)
              {
                firstMorganNumber=morgannumbers[container.getAtomNumber(atoms[k])];
              }
              else
              {
                if(morgannumbers[container.getAtomNumber(atoms[k])]==firstMorganNumber)
                  symbolsWithDifferentMorganNumbers--;
              }
            }
          }
        }
        if(symbolsWithDifferentMorganNumbers!=differentSymbols.size())
          return false;
      }
      catch(NoSuchAtomException ex){
        ex.printStackTrace();
      }
    }
    return(true);
  }
  
  
  public synchronized String createSMILES(Molecule molecule) {
    return (createSMILES(molecule, false));
  }
  
  public synchronized String createChiralSMILES(Molecule molecule) {
    return(createSMILES(molecule, true));
  }

  /**
   * Generate canonical SMILES from the <code>molecule</code>.  This method
   * canonicaly lables the molecule but dose not perform any checks on the
   * chemical validity of the molecule.
   *
   * @see org.openscience.cdk.smiles.CanonicalLabeler#canonLabel
   *
   */
  public synchronized String createSMILES(Molecule molecule, boolean chiral) {
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
      if(AromaticityCalculator.isArromatic(ring,  molecule)){
        arromaticRings.add(ring);
      }
    }

    StringBuffer l = new StringBuffer();
    createSMILES(start, l, molecule, chiral);
    return l.toString();
  }

  /**
   * Performes a DFS search on the <code>atomContainer</code>.  Then parses the resulting
   * tree to create the SMILES string.
   *
   * @param a the atom to start the search at.
   * @param line the StringBuffer that the SMILES is to be appended to.
   * @param atomContainer the AtomContainer that the SMILES string is generated for.
   */
  private void createSMILES(Atom a, StringBuffer line, AtomContainer atomContainer, boolean chiral) {
    Vector tree = new Vector();
    createDFSTree(a, tree, null, atomContainer); //Dummy parent
    parseChain(tree, line, atomContainer, null, chiral);
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
  private void parseChain(Vector v, StringBuffer buffer, AtomContainer container, Atom parent, boolean chiral){
    int positionInVector=0;
    Iterator it = v.iterator();
    Atom atom;
    while (it.hasNext()) {
      Object o = (Object) it.next();
      if(o instanceof Atom) {
        atom = (Atom)o;
        if(parent != null) {
          parseBond(buffer, atom, parent, container);
        }
        parseAtom(atom, buffer, container, chiral);
      	if(chiral && isStereo(container,atom)){
          Atom[] sorted=new Atom[3];
          Vector chiralNeighbours=container.getConnectedAtomsVector(atom);
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
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[2]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                }
                else
                {
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_UP&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[1]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==0&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
                    sorted[0]=(Atom)chiralNeighbours.get(i);
                  }
                  if(container.getBond((Atom)chiralNeighbours.get(i),atom).getStereo()==CDKConstants.STEREO_BOND_DOWN&&!isBondBroken((Atom)chiralNeighbours.get(i),atom)){
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
          int numberOfAtoms=3-getRingOpenings(atom).size();
          Atom[] sortedNew=new Atom[numberOfAtoms];
          int l=0;
          for(int k=0;k<sorted.length;k++){
            if(sorted[k]!=null){
              sortedNew[l]=sorted[k];
              l++;
            }
          }
          Object[] omy=new Object[numberOfAtoms];
          Object[] onew=new Object[numberOfAtoms];
          for(int k=0;k<omy.length;k++){
            omy[k]=v.get(positionInVector+1+k);
          }
          l=0;
          for(int k=0;k<sorted.length;k++){
            for(int m=0;m<omy.length;m++){
              if(omy[m] instanceof Atom){
                if(sorted[k]!=null&&omy[m]==sorted[k]){
                  onew[l]=omy[m];
                }
              }
              else
              {
                if(sorted[k]!=null&&((Vector)omy[m]).get(0)==sorted[k]){
                  onew[l]=omy[m];
                }
              }
            }
            if(sorted[k]!=null)
            l++;
          }
          int k=0;
          while(!(onew[numberOfAtoms-1] instanceof Atom)){
            Object dummy=onew[numberOfAtoms-1];
            for(int m=numberOfAtoms-1;m>0;m--){
              onew[m]=onew[m-1];
            }
            onew[0]=dummy;
            k++;
            if(k>numberOfAtoms)
              break;
          }
          if(!(onew[numberOfAtoms-1] instanceof Atom)){
            Vector vneigh=getCanNeigh(atom, container);
            for(int i=0;i<vneigh.size();i++){
              if(isBondBroken(atom,((Atom)vneigh.get(0))))
                vneigh.remove(0);
              if(vneigh.get(0)==parent)
                vneigh.remove(0);
            }
            //Diese sortierung muss atome, die als zahlen vorliegen, berücksichtigen
            while(((Vector)onew[0]).get(0)!=vneigh.get(0)){
              Object dummy=onew[numberOfAtoms-1];
              for(int m=numberOfAtoms-1;m>0;m--){
                onew[m]=onew[m-1];
              }
              onew[0]=dummy;
            }
          }
          for(int m=0;m<numberOfAtoms-1;m++){
            v.set(positionInVector+1+m,onew[m]);
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
        parseChain((Vector)o, buffer, container, parent, chiral);
        if(brackets)
          buffer.append(')');
      }
      positionInVector++;
    }
  }
  
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
    int type = (int)atomContainer.getBond(a1, a2).getOrder();
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
   */
  private void parseAtom(Atom a, StringBuffer buffer, AtomContainer container, boolean chiral) {
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
    if(brackets)
      buffer.append('[');
    buffer.append(mass);
    if(isAromatic(a))
     buffer.append(a.getSymbol().toLowerCase());
    else
      buffer.append(symbol);
    if(chiral && stereo)
      buffer.append('@');
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
    if(majorIsotope.getExactMass() == a.getExactMass())
      return "";
    else
      return Integer.toString(a.getAtomicMass());
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



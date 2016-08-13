/* 
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
 *                     2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
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
 */
package org.openscience.cdk.smiles;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.RingManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Tool that tries to deduce bond orders based on connectivity and hybridization
 * for a number of common ring systems of up to seven-membered rings. It assumes
 * that atom types have been perceived before that class is used.
 *
 * <p>The calculation can be interrupted with {@link #setInterrupted(boolean)},
 * but assumes that this class is not used in a threaded fashion. When a calculation
 * is interrupted, the boolean is reset to false.
 *
 * @author Todd Martin
 * @cdk.module smiles
 * @cdk.githash
 * @cdk.keyword bond order
 *
 * @cdk.bug 1895805
 * @cdk.bug 1931262
 *
 * @cdk.threadnonsafe
 * @deprecated Use the newer {@link org.openscience.cdk.aromaticity.Kekulization}
 */
@Deprecated
public class DeduceBondSystemTool {

    private AllRingsFinder      allRingsFinder;
    private static ILoggingTool logger      = LoggingToolFactory.createLoggingTool(DeduceBondSystemTool.class);

    private List<Integer[]>     listOfRings = null;

    private boolean             interrupted;

    /**
     * Constructor for the DeduceBondSystemTool object.
     */
    public DeduceBondSystemTool() {
        allRingsFinder = new AllRingsFinder();
    }

    /**
     * Constructor for the DeduceBondSystemTool object accepting a custom {@link AllRingsFinder}.
     *
     * @param ringFinder a custom {@link AllRingsFinder}.
     */
    public DeduceBondSystemTool(AllRingsFinder ringFinder) {
        allRingsFinder = ringFinder;
    }

    /**
     * Determines if, according to the algorithms implemented in this class, the given
     * AtomContainer has properly distributed double bonds.
     *
     * @param  m {@link IAtomContainer} to check the bond orders for.
     * @return true, if bond orders are properly distributed
     * @throws CDKException thrown when something went wrong
     */
    public boolean isOK(IAtomContainer m) throws CDKException {
        // OK, we take advantage here from the fact that this class does not take
        // into account rings larger than 7 atoms. See fixAromaticBondOrders().
        IRingSet rs = allRingsFinder.findAllRings(m, 7);
        storeRingSystem(m, rs);
        boolean StructureOK = this.isStructureOK(m);
        IRingSet irs = this.removeExtraRings(m);

        if (irs == null) throw new CDKException("error in AllRingsFinder.findAllRings");

        int count = this.getBadCount(m, irs);

        return StructureOK && count == 0;
    }

    /**
     * Added missing bond orders based on atom type information.
     *
     * @param atomContainer {@link IAtomContainer} for which to distribute double bond orders
     * @return a {@link IAtomContainer} with assigned double bonds.
     * @throws CDKException if something went wrong.
     */
    public IAtomContainer fixAromaticBondOrders(IAtomContainer atomContainer) throws CDKException {

        // preset all bond orders to single
        for (IBond bond : atomContainer.bonds()) {
            if (bond.isAromatic() && bond.getOrder() == IBond.Order.UNSET)
                bond.setOrder(IBond.Order.SINGLE);
        }

        // OK, we take advantage here from the fact that this class does not take
        // into account rings larger than 7 atoms. See fixAromaticBondOrders().
        IRingSet rs = allRingsFinder.findAllRings(atomContainer, 7);
        storeRingSystem(atomContainer, rs);

        IRingSet ringSet;

        // TODO remove rings with nonsp2 carbons(?) and rings larger than 7 atoms
        ringSet = removeExtraRings(atomContainer);

        if (ringSet == null) throw new CDKException("failure in AllRingsFinder.findAllRings");

        List<List<List<String>>> MasterList = new ArrayList<List<List<String>>>();

        //this.counter=0;// counter which keeps track of all current possibilities for placing double bonds

        this.FixPyridineNOxides(atomContainer, ringSet);

        for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {

            IRing ring = (IRing) ringSet.getAtomContainer(i);

            // only takes into account rings up to 7 atoms... if that changes,
            // make sure to update the findAllRings() calls too!
            if (ring.getAtomCount() == 5) {
                fiveMemberedRingPossibilities(atomContainer, ring, MasterList);
            } else if (ring.getAtomCount() == 6) {
                sixMemberedRingPossibilities(atomContainer, ring, MasterList);
            } else if (ring.getAtomCount() == 7) {
                sevenMemberedRingPossibilities(atomContainer, ring, MasterList);
                //TODO- add code for all 7 membered aromatic ring possibilities not just 3 bonds
            } else {
                //TODO: what about other rings systems?
                logger.debug("Found ring of size: " + ring.getAtomCount());
            }
        }

        IAtomContainerSet som = atomContainer.getBuilder().newInstance(IAtomContainerSet.class);

        //		int number=1; // total number of possibilities
        //
        //		for (int ii=0;ii<=MasterList.size()-1;ii++) {
        //		ArrayList ringlist=(ArrayList)MasterList.get(ii);
        //		number*=ringlist.size();
        //		}
        //		logger.debug("number= "+number);

        int[] choices;

        //if (number> 1000000) return null;

        choices = new int[MasterList.size()];

        if (MasterList.size() > 0) {
            IAtomContainer iAtomContainer = loop(System.currentTimeMillis(), atomContainer, 0, MasterList, choices, som);
            if (iAtomContainer != null) return iAtomContainer;
        }

        int mincount = 99999999;

        int best = -1; // one with minimum number of bad atoms

        // minimize number of potentially bad nitrogens among AtomContainers in the set

        for (int i = 0; i <= som.getAtomContainerCount() - 1; i++) {

            IAtomContainer mol = som.getAtomContainer(i);

            ringSet = removeExtraRings(mol);

            if (ringSet == null) continue;

            int count = getBadCount(mol, ringSet);

            //logger.debug(i + "\t" + count);

            if (count < mincount) {
                mincount = count;
                best = i;
            }

        }

        if (som.getAtomContainerCount() > 0) return som.getAtomContainer(best);
        return atomContainer;
    }

    private void FixPyridineNOxides(IAtomContainer atomContainer, IRingSet ringSet) {

        //convert n(=O) to [n+][O-]

        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom ai = atomContainer.getAtom(i);

            if (ai.getSymbol().equals("N") && (ai.getFormalCharge() == null || ai.getFormalCharge() == 0)) {
                if (inRingSet(ai, ringSet)) {
                    List<IAtom> ca = atomContainer.getConnectedAtomsList(ai);
                    for (IAtom caj : ca) {
                        if (caj.getSymbol().equals("O")
                                && atomContainer.getBond(ai, caj).getOrder() == IBond.Order.DOUBLE) {
                            ai.setFormalCharge(1);
                            caj.setFormalCharge(-1);
                            atomContainer.getBond(ai, caj).setOrder(IBond.Order.SINGLE);
                        }
                    }// end for (int j=0;j<ca.size();j++)

                } // end if (inRingSet(ai,ringSet)) {
            } // end if (ai.getSymbol().equals("N") && ai.getFormalCharge()==0)

        } // end for (int i=0;i<atomContainer.getAtomCount();i++)

    }

    private void applyBonds(IAtomContainer m, List<String> al) {

        //logger.debug("");

        for (int i = 0; i <= al.size() - 1; i++) {

            String s = al.get(i);
            String s1 = s.substring(0, s.indexOf('-'));
            String s2 = s.substring(s.indexOf('-') + 1, s.length());

            int i1 = Integer.parseInt(s1);
            int i2 = Integer.parseInt(s2);

            //logger.debug(s1+"\t"+s2);

            IBond b = m.getBond(m.getAtom(i1), m.getAtom(i2));
            b.setOrder(IBond.Order.DOUBLE);

        }

    }

    private void fiveMemberedRingPossibilities(IAtomContainer m, IRing r, List<List<List<String>>> MasterList) {
        // 5 possibilities for placing 2 double bonds
        // 5 possibilities for placing 1 double bond

        int[] num = new int[5]; // stores atom numbers based on atom numbers in AtomContainer instead of ring

        for (int j = 0; j <= 4; j++) {
            num[j] = m.getAtomNumber(r.getAtom(j));
            //logger.debug(num[j]);
        }

        List<String> al1 = new ArrayList<String>();
        List<String> al2 = new ArrayList<String>();
        List<String> al3 = new ArrayList<String>();
        List<String> al4 = new ArrayList<String>();
        List<String> al5 = new ArrayList<String>();

        List<String> al6 = new ArrayList<String>();
        List<String> al7 = new ArrayList<String>();
        List<String> al8 = new ArrayList<String>();
        List<String> al9 = new ArrayList<String>();
        List<String> al10 = new ArrayList<String>();

        al1.add(num[1] + "-" + num[2]);
        al1.add(num[3] + "-" + num[4]);

        al2.add(num[2] + "-" + num[3]);
        al2.add(num[0] + "-" + num[4]);

        al3.add(num[0] + "-" + num[1]);
        al3.add(num[3] + "-" + num[4]);

        al4.add(num[0] + "-" + num[4]);
        al4.add(num[1] + "-" + num[2]);

        al5.add(num[0] + "-" + num[1]);
        al5.add(num[2] + "-" + num[3]);

        al6.add(num[0] + "-" + num[1]);
        al7.add(num[1] + "-" + num[2]);
        al8.add(num[2] + "-" + num[3]);
        al9.add(num[3] + "-" + num[4]);
        al10.add(num[4] + "-" + num[0]);

        List<List<String>> mal = new ArrayList<List<String>>();

        mal.add(al1);
        mal.add(al2);
        mal.add(al3);
        mal.add(al4);
        mal.add(al5);

        mal.add(al6);
        mal.add(al7);
        mal.add(al8);
        mal.add(al9);
        mal.add(al10);

        //		mal.add(al11);

        MasterList.add(mal);

    }

    private void sixMemberedRingPossibilities(IAtomContainer m, IRing r, List<List<List<String>>> MasterList) {
        // 2 possibilities for placing 3 double bonds
        // 6 possibilities for placing 2 double bonds
        // 6 possibilities for placing 1 double bonds

        IAtom[] ringatoms = new IAtom[6];

        ringatoms[0] = r.getAtom(0);

        int[] num = new int[6];

        for (int j = 0; j <= 5; j++) {
            num[j] = m.getAtomNumber(r.getAtom(j));
        }

        List<String> al1 = new ArrayList<String>();
        List<String> al2 = new ArrayList<String>();

        al1.add(num[0] + "-" + num[1]);
        al1.add(num[2] + "-" + num[3]);
        al1.add(num[4] + "-" + num[5]);

        al2.add(num[1] + "-" + num[2]);
        al2.add(num[3] + "-" + num[4]);
        al2.add(num[5] + "-" + num[0]);

        List<String> al3 = new ArrayList<String>();
        List<String> al4 = new ArrayList<String>();
        List<String> al5 = new ArrayList<String>();
        List<String> al6 = new ArrayList<String>();
        List<String> al7 = new ArrayList<String>();
        List<String> al8 = new ArrayList<String>();
        List<String> al9 = new ArrayList<String>();
        List<String> al10 = new ArrayList<String>();
        List<String> al11 = new ArrayList<String>();

        List<String> al12 = new ArrayList<String>();
        List<String> al13 = new ArrayList<String>();
        List<String> al14 = new ArrayList<String>();
        List<String> al15 = new ArrayList<String>();
        List<String> al16 = new ArrayList<String>();
        List<String> al17 = new ArrayList<String>();

        List<String> al18 = new ArrayList<String>();

        al3.add(num[0] + "-" + num[1]);
        al3.add(num[2] + "-" + num[3]);

        al4.add(num[0] + "-" + num[1]);
        al4.add(num[4] + "-" + num[5]);

        al5.add(num[1] + "-" + num[2]);
        al5.add(num[3] + "-" + num[4]);

        al6.add(num[1] + "-" + num[2]);
        al6.add(num[0] + "-" + num[5]);

        al7.add(num[2] + "-" + num[3]);
        al7.add(num[4] + "-" + num[5]);

        al8.add(num[0] + "-" + num[5]);
        al8.add(num[3] + "-" + num[4]);

        al9.add(num[0] + "-" + num[1]);
        al9.add(num[3] + "-" + num[4]);

        al10.add(num[1] + "-" + num[2]);
        al10.add(num[4] + "-" + num[5]);

        al11.add(num[2] + "-" + num[3]);
        al11.add(num[0] + "-" + num[5]);

        al12.add(num[0] + "-" + num[1]);
        al13.add(num[1] + "-" + num[2]);
        al14.add(num[2] + "-" + num[3]);
        al15.add(num[3] + "-" + num[4]);
        al16.add(num[4] + "-" + num[5]);
        al17.add(num[5] + "-" + num[0]);

        List<List<String>> mal = new ArrayList<List<String>>();

        mal.add(al1);
        mal.add(al2);

        mal.add(al3);
        mal.add(al4);
        mal.add(al5);
        mal.add(al6);
        mal.add(al7);
        mal.add(al8);
        mal.add(al9);
        mal.add(al10);
        mal.add(al11);

        mal.add(al12);
        mal.add(al13);
        mal.add(al14);
        mal.add(al15);
        mal.add(al16);
        mal.add(al17);
        mal.add(al18);

        MasterList.add(mal);

    }

    private void sevenMemberedRingPossibilities(IAtomContainer m, IRing r, List<List<List<String>>> MasterList) {
        // for now only consider case where have 3 double bonds

        IAtom[] ringatoms = new IAtom[7];

        ringatoms[0] = r.getAtom(0);

        int[] num = new int[7];

        for (int j = 0; j <= 6; j++) {
            num[j] = m.getAtomNumber(r.getAtom(j));
        }

        List<String> al1 = new ArrayList<String>();
        List<String> al2 = new ArrayList<String>();
        List<String> al3 = new ArrayList<String>();
        List<String> al4 = new ArrayList<String>();
        List<String> al5 = new ArrayList<String>();

        al1.add(num[0] + "-" + num[1]);
        al1.add(num[2] + "-" + num[3]);
        al1.add(num[4] + "-" + num[5]);

        al2.add(num[0] + "-" + num[1]);
        al2.add(num[2] + "-" + num[3]);
        al2.add(num[5] + "-" + num[6]);

        al3.add(num[1] + "-" + num[2]);
        al3.add(num[3] + "-" + num[4]);
        al3.add(num[5] + "-" + num[6]);

        al4.add(num[1] + "-" + num[2]);
        al4.add(num[3] + "-" + num[4]);
        al4.add(num[6] + "-" + num[0]);

        al5.add(num[2] + "-" + num[3]);
        al5.add(num[4] + "-" + num[5]);
        al5.add(num[6] + "-" + num[0]);

        List<List<String>> mal = new ArrayList<List<String>>();

        mal.add(al1);
        mal.add(al2);
        mal.add(al3);
        mal.add(al4);
        mal.add(al5);

        MasterList.add(mal);

    }

    private int getBadCount(IAtomContainer atomContainer, IRingSet ringSet) {
        // finds count of nitrogens in the rings that have 4 bonds
        // to non hydrogen atoms and one to hydrogen
        // or nitrogens with 2 double bonds to atoms in the ringset
        // or have S atom with more than 2 bonds
        // these arent necessarily bad- just unlikely

        int count = 0;

        for (int j = 0; j <= atomContainer.getAtomCount() - 1; j++) {
            IAtom atom = atomContainer.getAtom(j);

            //logger.debug(mol.getBondOrderSum(a));

            if (inRingSet(atom, ringSet)) {
                //logger.debug("in ring set");

                if (atom.getSymbol().equals("N")) {
                    if (atom.getFormalCharge() == 0) {
                        //						logger.debug(mol.getBondOrderSum(a));
                        if (atomContainer.getBondOrderSum(atom) == 4) {
                            count++; //
                        } else if (atomContainer.getBondOrderSum(atom) == 5) {
                            // check if have 2 double bonds to atom in ring
                            int doublebondcount = 0;
                            List<IAtom> ca = atomContainer.getConnectedAtomsList(atom);

                            for (int k = 0; k <= ca.size() - 1; k++) {
                                if (atomContainer.getBond(atom, ca.get(k)).getOrder() == IBond.Order.DOUBLE) {
                                    if (inRingSet(ca.get(k), ringSet)) {
                                        doublebondcount++;
                                    }
                                }
                            }

                            if (doublebondcount == 2) {
                                count++;
                            }

                        }
                    } else if (atom.getFormalCharge() == 1) {
                        if (atomContainer.getBondOrderSum(atom) == 5) {
                            count++;
                        }
                    }
                } else if (atom.getSymbol().equals("S")) {
                    if (atomContainer.getBondOrderSum(atom) > 2) {
                        count++;
                    }
                }
            }
        }
        //logger.debug("here bad count = " + count);

        return count;
    }

    private boolean inRingSet(IAtom atom, IRingSet ringSet) {
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            IRing ring = (IRing) ringSet.getAtomContainer(i);
            if (ring.contains(atom)) return true;
        }
        return false;
    }

    private IAtomContainer loop(long starttime, IAtomContainer atomContainer, int index,
            List<List<List<String>>> MasterList, int[] choices, IAtomContainerSet som) throws CDKException {

        //logger.debug(System.currentTimeMillis());

        long time = System.currentTimeMillis();

        long diff = time - starttime;

        if (diff > 100000) {
            //time out after 100 seconds
            throw new CDKException("Timed out after 100 seconds.");
        } else if (this.interrupted) {
            // reset the interruption
            this.interrupted = false;
            throw new CDKException("Process was interrupted.");
        }

        List<List<String>> ringlist = MasterList.get(index);

        IAtomContainer mnew2 = null;

        for (int i = 0; i <= ringlist.size() - 1; i++) {

            choices[index] = i;

            if (index == MasterList.size() - 1) {
                //logger.debug(choices[0]+"\t"+choices[1]);

                IAtomContainer mnew = null;
                try {
                    mnew = (IAtomContainer) atomContainer.clone();
                } catch (Exception e) {
                    logger.error("Failed to clone atomContainer: ", e.getMessage());
                    logger.debug(e);
                }

                for (int j = 0; j <= MasterList.size() - 1; j++) {
                    List<List<String>> ringlist2 = MasterList.get(j);
                    List<String> bondlist = ringlist2.get(choices[j]);
                    //					logger.debug(j+"\t"+choices[j]);
                    applyBonds(mnew, bondlist);
                }
                //				logger.debug("");

                if (isStructureOK(mnew)) {

                    IRingSet rs = this.removeExtraRings(mnew); // need to redo this since created new atomContainer (mnew)

                    if (rs != null) {

                        int count = this.getBadCount(mnew, rs);
                        // logger.debug("bad count="+count);

                        if (count == 0) {
                            // logger.debug("found match after "+counter+"
                            // iterations");
                            return mnew; // dont worry about adding to set
                                         // just finish
                        } else {
                            som.addAtomContainer(mnew);
                        }
                    }
                }

            }

            if (index + 1 <= MasterList.size() - 1) {
                // logger.debug("here3="+counter);
                mnew2 = loop(starttime, atomContainer, index + 1, MasterList, choices, som); //recursive def
            }

            if (mnew2 instanceof IAtomContainer) {
                return mnew2;
            }
        }
        return null;

    }

    private boolean isStructureOK(IAtomContainer atomContainer) {
        try {
            CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(atomContainer.getBuilder());
            Iterator<IAtom> atoms = atomContainer.atoms().iterator();
            while (atoms.hasNext()) {
                IAtom atom = atoms.next();
                IAtomType matched = matcher.findMatchingAtomType(atomContainer, atom);
                if (matched == null || matched.getAtomTypeName().equals("X")) return false;
            }

            IRingSet ringSet = recoverRingSystem(atomContainer);
            //logger.debug("Rs size= "+rs.size());

            // clear aromaticity flags
            for (int i = 0; i <= atomContainer.getAtomCount() - 1; i++) {
                atomContainer.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
            }
            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
                IRing r = (IRing) ringSet.getAtomContainer(i);
                r.setFlag(CDKConstants.ISAROMATIC, false);
            }
            // now, detect aromaticity from cratch, and mark rings as aromatic too (if ...)
            Aromaticity.cdkLegacy().apply(atomContainer);
            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
                IRing ring = (IRing) ringSet.getAtomContainer(i);
                RingManipulator.markAromaticRings(ring);
            }

            //			Figure out which rings we want to make sure are aromatic:
            boolean[] Check = this.findRingsToCheck(ringSet);

            //			for (int i=0;i<=Check.length-1;i++) {
            //			logger.debug(i+"\t"+rs.getAtomContainer(i).getAtomCount()+"\t"+Check[i]);
            //			}

            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
                IRing ring = (IRing) ringSet.getAtomContainer(i);

                //logger.debug(k+"\t"+r.getAtomCount()+"\t"+r.getFlag(CDKConstants.ISAROMATIC));
                if (Check[i]) {

                    for (int j = 0; j <= ring.getAtomCount() - 1; j++) {
                        if (ring.getAtom(j).getImplicitHydrogenCount() != CDKConstants.UNSET
                                && ring.getAtom(j).getImplicitHydrogenCount() < 0) {
                            return false;
                        }
                    }

                    if (!ring.getFlag(CDKConstants.ISAROMATIC)) {
                        //						logger.debug(counter+"\t"+"ring not aromatic"+"\t"+r.getAtomCount());
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }

    }

    /**
     * Remove rings.
     * <p/>
     * Removes rings which do not have all sp2/planar3 aromatic atoms and also gets rid of rings that have more than
     * 7 or less than 5 atoms in them.
     *
     * @param m The AtomContainer from which we want to remove rings
     * @return The set of reduced rings
     */
    private IRingSet removeExtraRings(IAtomContainer m) {

        try {
            IRingSet rs = Cycles.sssr(m).toRingSet();

            //remove rings which dont have all aromatic atoms (according to hybridization set by lower case symbols in smiles):

            //logger.debug("numrings="+rs.size());

            iloop: for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {

                IRing r = (IRing) rs.getAtomContainer(i);

                if (r.getAtomCount() > 7 || r.getAtomCount() < 5) {
                    rs.removeAtomContainer(i);
                    i--; // go back to first one
                    continue iloop;
                }

                //int NonSP2Count = 0;

                for (int j = 0; j <= r.getAtomCount() - 1; j++) {

                    //logger.debug(j+"\t"+r.getAtomAt(j).getSymbol()+"\t"+r.getAtomAt(j).getHybridization());

                    if (r.getAtom(j).getHybridization() == CDKConstants.UNSET
                            || !(r.getAtom(j).getHybridization() == Hybridization.SP2 || r.getAtom(j)
                                    .getHybridization() == Hybridization.PLANAR3)) {
                        rs.removeAtomContainer(i);
                        i--; // go back
                        continue iloop;
                        //                        NonSP2Count++;
                        //                        if (r.getAtom(j).getSymbol().equals("C")) {
                        //                            rs.removeAtomContainer(i);
                        //                            i--; // go back
                        //                            continue iloop;
                        //                        }
                    }
                }

                //                if (NonSP2Count > 1) {
                //                    rs.removeAtomContainer(i);
                //                    i--; // go back
                //                    continue iloop;
                //                }

            }
            return rs;

        } catch (Exception e) {
            return m.getBuilder().newInstance(IRingSet.class);
        }
    }

    private boolean[] findRingsToCheck(IRingSet rs) {

        boolean[] Check = new boolean[rs.getAtomContainerCount()];

        for (int i = 0; i <= Check.length - 1; i++) {
            Check[i] = true;
        }

        iloop:

        for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {

            IRing r = (IRing) rs.getAtomContainer(i);

            if (r.getAtomCount() > 7) {
                Check[i] = false;
                continue;
            }

            int NonSP2Count = 0;

            for (int j = 0; j <= r.getAtomCount() - 1; j++) {

                // logger.debug(j+"\t"+r.getAtomAt(j).getSymbol()+"\t"+r.getAtomAt(j).getHybridization());

                if (r.getAtom(j).getHybridization() == CDKConstants.UNSET
                        || r.getAtom(j).getHybridization() != Hybridization.SP2) {
                    NonSP2Count++;
                    if (r.getAtom(j).getSymbol().equals("C")) {
                        Check[i] = false;
                        continue iloop;
                    }
                }
            }

            if (NonSP2Count > 1) {
                Check[i] = false;
                continue;
            }

        }

        return Check;
    }

    /**
     * Stores an IRingSet corresponding to a AtomContainer using the bond numbers.
     *
     * @param mol      The IAtomContainer for which to store the IRingSet.
     * @param ringSet  The IRingSet to store
     */
    private void storeRingSystem(IAtomContainer mol, IRingSet ringSet) {
        listOfRings = new ArrayList<Integer[]>(); // this is a list of int arrays
        for (int r = 0; r < ringSet.getAtomContainerCount(); ++r) {
            IRing ring = (IRing) ringSet.getAtomContainer(r);
            Integer[] bondNumbers = new Integer[ring.getBondCount()];
            for (int i = 0; i < ring.getBondCount(); ++i)
                bondNumbers[i] = mol.getBondNumber(ring.getBond(i));
            listOfRings.add(bondNumbers);
        }
    }

    /**
     * Recovers a RingSet corresponding to a AtomContainer that has been
     * stored by storeRingSystem().
     *
     * @param mol      The IAtomContainer for which to recover the IRingSet.
     */
    private IRingSet recoverRingSystem(IAtomContainer mol) {
        IRingSet ringSet = mol.getBuilder().newInstance(IRingSet.class);
        for (Integer[] bondNumbers : listOfRings) {
            IRing ring = mol.getBuilder().newInstance(IRing.class, bondNumbers.length);
            for (int bondNumber : bondNumbers) {
                IBond bond = mol.getBond(bondNumber);
                ring.addBond(bond);
                if (!ring.contains(bond.getAtom(0))) ring.addAtom(bond.getAtom(0));
                if (!ring.contains(bond.getAtom(1))) ring.addAtom(bond.getAtom(1));
            }
            ringSet.addAtomContainer(ring);
        }
        return ringSet;
    }

    /**
     * Sets if the calculation should be interrupted.
     *
     * @param interrupted true, if the calculation should be canceled
     */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Returns if the next or running calculation should be interrupted.
     *
     * @return true or false
     */
    public boolean isInterrupted() {
        return this.interrupted;
    }

}

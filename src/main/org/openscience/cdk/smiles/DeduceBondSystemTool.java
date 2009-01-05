/*  $RCSfile: $
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.*;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Tool that tries to deduce bond orders based on connectivity and hybridization
 * for a number of common ring systems.
 *
 * @author Todd Martin
 * @cdk.module smiles
 * @cdk.svnrev  $Revision$
 * @cdk.keyword bond order
 * 
 * @cdk.bug 1895805
 * @cdk.bug 1931262
 */
@TestClass("org.openscience.cdk.smiles.DeduceBondSystemToolTest")
public class DeduceBondSystemTool {

	private AllRingsFinder allRingsFinder;
    private LoggingTool logger;
	
    private List<Integer[]> listOfRings = null;
    	
	private boolean interrupted;

    /**
     * Constructor for the DeduceBondSystemTool object.
     */
    public DeduceBondSystemTool() {
    	allRingsFinder = new AllRingsFinder();
        logger = new LoggingTool(this);
    }

    public boolean isOK(IMolecule m) throws CDKException {
    	IRingSet rs = allRingsFinder.findAllRings(m);
    	storeRingSystem(m, rs);
    	boolean StructureOK=this.isStructureOK(m);
    	IRingSet irs=this.removeExtraRings(m);
    	
    	if (irs==null) throw new CDKException("error in AllRingsFinder.findAllRings");
    	
    	int count=this.getBadCount(m,irs);

        return StructureOK && count == 0;
    }

    @TestMethod("xtestQuinone,xtestPyrrole")
    public IMolecule fixAromaticBondOrders(IMolecule molecule) throws CDKException {
        //logger.debug("here");
    	
    	IRingSet rs = allRingsFinder.findAllRings(molecule);
    	storeRingSystem(molecule, rs);
    	
        IRingSet ringSet;

        // TODO remove rings with nonsp2 carbons(?) and rings larger than 7 atoms
        ringSet = removeExtraRings(molecule);
        
        if (ringSet==null) throw new CDKException("failure in AllRingsFinder.findAllRings");
        
        ArrayList MasterList = new ArrayList();

        //this.counter=0;// counter which keeps track of all current possibilities for placing double bonds
        
        this.FixPyridineNOxides(molecule,ringSet);
        

        for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {

            IRing ring = (IRing) ringSet.getAtomContainer(i);

            if (ring.getAtomCount() == 5) {
                fiveMemberedRingPossibilities(molecule, ring, MasterList);
            } else if (ring.getAtomCount() == 6) {
                sixMemberedRingPossibilities(molecule, ring, MasterList);
            } else if (ring.getAtomCount() == 7){
                sevenMemberedRingPossibilities(molecule, ring, MasterList);
                //TODO- add code for all 7 membered aromatic ring possibilities not just 3 bonds
            } else {
            	//TODO: what about other rings systems?
            	logger.debug("Found ring of size: " + ring.getAtomCount());
            }
        }

        IMoleculeSet som = molecule.getBuilder().newMoleculeSet();

//		int number=1; // total number of possibilities
//		
//		for (int ii=0;ii<=MasterList.size()-1;ii++) {
//		ArrayList ringlist=(ArrayList)MasterList.get(ii);
//		number*=ringlist.size();
//		}
//		logger.debug("number= "+number);			


        int [] choices;

        //if (number> 1000000) return null;

        choices = new int [MasterList.size()];

        if (MasterList.size() > 0) {
            IMolecule iMolecule = loop(System.currentTimeMillis(), molecule, 0, MasterList, choices, som);
            if (iMolecule != null) return iMolecule;
        }


        int mincount = 99999999;

        int best = -1; // one with minimum number of bad atoms

        // minimize number of potentially bad nitrogens among molecules in the set

        for (int i = 0; i <= som.getAtomContainerCount() - 1; i++) {

            IMolecule mol = som.getMolecule(i);

            ringSet = removeExtraRings(mol);
            
            if (ringSet==null) continue;
            
            int count = getBadCount(mol, ringSet);

            //logger.debug(i + "\t" + count);

            if (count < mincount) {
                mincount = count;
                best = i;
            }
            
        }

        if (som.getAtomContainerCount() > 0) return som.getMolecule(best);
        return molecule;
    }

    private void FixPyridineNOxides(IMolecule molecule,IRingSet ringSet) {
    	
    	//convert n(=O) to [n+][O-]
    	
    	for (int i=0;i<molecule.getAtomCount();i++) {
    		IAtom ai=molecule.getAtom(i);
    		
    		if (ai.getSymbol().equals("N") && 
    			(ai.getFormalCharge() == null || ai.getFormalCharge() == 0)) {
    			if (inRingSet(ai,ringSet)) {
    				List<IAtom> ca=molecule.getConnectedAtomsList(ai);
                    for (IAtom caj : ca) {
                        if (caj.getSymbol().equals("O") && molecule.getBond(ai, caj).getOrder() == IBond.Order.DOUBLE) {
                            ai.setFormalCharge(1);
                            caj.setFormalCharge(-1);
                            molecule.getBond(ai, caj).setOrder(IBond.Order.SINGLE);
                        }
                    }// end for (int j=0;j<ca.size();j++)
    				
    			} // end if (inRingSet(ai,ringSet)) {
    		} // end if (ai.getSymbol().equals("N") && ai.getFormalCharge()==0)
    		
    	} // end for (int i=0;i<molecule.getAtomCount();i++)
    	
	
	
    }
    private void applyBonds(IMolecule m, ArrayList al) {

        //logger.debug("");

        for (int i = 0; i <= al.size() - 1; i++) {

            String s = (String) al.get(i);
            String s1 = s.substring(0, s.indexOf("-"));
            String s2 = s.substring(s.indexOf("-") + 1, s.length());

            int i1 = Integer.parseInt(s1);
            int i2 = Integer.parseInt(s2);

            //logger.debug(s1+"\t"+s2);

            IBond b = m.getBond(m.getAtom(i1), m.getAtom(i2));
            b.setOrder(IBond.Order.DOUBLE);


        }

    }

    private void fiveMemberedRingPossibilities(IMolecule m, IRing r, ArrayList MasterList) {
        // 5 possibilities for placing 2 double bonds
        // 5 possibilities for placing 1 double bond

        int [] num = new int [5]; // stores atom numbers based on atom numbers in molecule instead of ring

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

    private void sixMemberedRingPossibilities(IMolecule m, IRing r, ArrayList MasterList) {
        // 2 possibilities for placing 3 double bonds
        // 6 possibilities for placing 2 double bonds
        // 6 possibilities for placing 1 double bonds

        IAtom [] ringatoms = new Atom [6];

        ringatoms[0] = r.getAtom(0);

        int [] num = new int [6];

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

    private void sevenMemberedRingPossibilities(IMolecule m, IRing r, ArrayList MasterList) {
        // for now only consider case where have 3 double bonds

        IAtom[] ringatoms = new Atom[7];

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


    private int getBadCount(IMolecule molecule, IRingSet ringSet) {
        // finds count of nitrogens in the rings that have 4 bonds
        // to non hydrogen atoms and one to hydrogen
        // or nitrogens with 2 double bonds to atoms in the ringset
        // or have S atom with more than 2 bonds
        // these arent necessarily bad- just unlikely


        int count = 0;

        for (int j = 0; j <= molecule.getAtomCount() - 1; j++) {
            IAtom atom = molecule.getAtom(j);

            //logger.debug(mol.getBondOrderSum(a));

            if (inRingSet(atom, ringSet)) {
                //logger.debug("in ring set");

                if (atom.getSymbol().equals("N")) {
                    if (atom.getFormalCharge() == 0) {
//						logger.debug(mol.getBondOrderSum(a));
                        if (molecule.getBondOrderSum(atom) == 4) {
                            count++; //
                        } else if (molecule.getBondOrderSum(atom) == 5) {
                            // check if have 2 double bonds to atom in ring
                            int doublebondcount = 0;
                            java.util.List ca = molecule.getConnectedAtomsList(atom);

                            for (int k = 0; k <= ca.size() - 1; k++) {
                                if (molecule.getBond(atom, (IAtom)ca.get(k)).getOrder() == IBond.Order.DOUBLE) {
                                    if (inRingSet((IAtom)ca.get(k), ringSet)) {
                                        doublebondcount++;
                                    }
                                }
                            }

                            if (doublebondcount == 2) {
                                count++;
                            }


                        }
                    } else if (atom.getFormalCharge() == 1) {
                        if (molecule.getBondOrderSum(atom) == 5) {
                            count++;
                        }
                    }
                } else if (atom.getSymbol().equals("S")) {
                    if (molecule.getBondOrderSum(atom) > 2) {
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
            Ring ring = (Ring) ringSet.getAtomContainer(i);
            if (ring.contains(atom)) return true;
        }
        return false;
    }

    private IMolecule loop(long starttime, IMolecule molecule, int index, 
    		               ArrayList MasterList, int [] choices, IMoleculeSet som) throws CDKException {

        //logger.debug(System.currentTimeMillis());

        long time = System.currentTimeMillis();

        long diff = time - starttime;

        if (diff > 100000) { 
        	//time out after 100 seconds
        	throw new CDKException("Timed out after 100 seconds.");
        } else if (this.interrupted) {
        	throw new CDKException("Process was interrupted.");
        }

        ArrayList ringlist = (ArrayList) MasterList.get(index);

        IMolecule mnew2 = null;

        for (int i = 0; i <= ringlist.size() - 1; i++) {

            choices[index] = i;

            if (index == MasterList.size() - 1) {
                //logger.debug(choices[0]+"\t"+choices[1]);

                IMolecule mnew = null;
                try {
                    mnew = (Molecule) molecule.clone();
                } catch (Exception e) {
                    logger.error("Failed to clone molecule: ", e.getMessage());
                    logger.debug(e);
                }

                for (int j = 0; j <= MasterList.size() - 1; j++) {
                    ArrayList ringlist2 = (ArrayList) MasterList.get(j);
                    ArrayList bondlist = (ArrayList) ringlist2.get(choices[j]);
//					logger.debug(j+"\t"+choices[j]);
                    applyBonds(mnew, bondlist);
                }
//				logger.debug("");

                if (isStructureOK(mnew)) {

                    IRingSet rs = this.removeExtraRings(mnew); // need to redo this since created new molecule (mnew)

                    if (rs != null) {

						int count = this.getBadCount(mnew, rs);
						// logger.debug("bad count="+count);

						if (count == 0) {
							// logger.debug("found match after "+counter+"
							// iterations");
							return mnew; // dont worry about adding to set
											// just finish
						} else {
							som.addMolecule(mnew);
						}
					}
                }

            }

            if (index + 1 <= MasterList.size() - 1) {
                // logger.debug("here3="+counter);
                mnew2 = loop(starttime, molecule, index + 1, MasterList, choices, som); //recursive def
            }


            if (mnew2 instanceof IMolecule) {
                return mnew2;
            }
        }
        return null;

    }

    private boolean isStructureOK(IMolecule molecule) {
        try {
        	CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(molecule.getBuilder());
            Iterator<IAtom> atoms = molecule.atoms().iterator();
            while (atoms.hasNext()) {
            	IAtom atom = atoms.next();
            	IAtomType matched = matcher.findMatchingAtomType(molecule, atom);
            	if (matched == null) return false;
            }

        	IRingSet ringSet = recoverRingSystem(molecule);
            //logger.debug("Rs size= "+rs.size());

        	// clear aromaticity flags
            for (int i = 0; i <= molecule.getAtomCount() - 1; i++) {
                molecule.getAtom(i).setFlag(CDKConstants.ISAROMATIC, false);
            }
            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
                Ring r = (Ring) ringSet.getAtomContainer(i);
                r.setFlag(CDKConstants.ISAROMATIC, false);
            }
            // now, detect aromaticity from cratch, and mark rings as aromatic too (if ...)
            CDKHueckelAromaticityDetector.detectAromaticity(molecule);
            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
            	Ring ring = (Ring) ringSet.getAtomContainer(i);
            	RingManipulator.markAromaticRings(ring);
            }

//			Figure out which rings we want to make sure are aromatic:
            boolean [] Check = this.findRingsToCheck(ringSet);

//			for (int i=0;i<=Check.length-1;i++) {
//			logger.debug(i+"\t"+rs.getAtomContainer(i).getAtomCount()+"\t"+Check[i]);
//			}

            for (int i = 0; i <= ringSet.getAtomContainerCount() - 1; i++) {
                Ring ring = (Ring) ringSet.getAtomContainer(i);

                //logger.debug(k+"\t"+r.getAtomCount()+"\t"+r.getFlag(CDKConstants.ISAROMATIC));
                if (Check[i]) {
                	
                    for (int j = 0; j <= ring.getAtomCount() - 1; j++) {
                        if (ring.getAtom(j).getHydrogenCount() != CDKConstants.UNSET && ring.getAtom(j).getHydrogenCount() < 0) {
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
     * @param m The molecule from which we want to remove rings
     * @return The set of reduced rings
     */
    private IRingSet removeExtraRings(IMolecule m) {

        try {
            SSSRFinder arf = new SSSRFinder(m);
            IRingSet rs = arf.findSSSR();

            //remove rings which dont have all aromatic atoms (according to hybridization set by lower case symbols in smiles):

            //logger.debug("numrings="+rs.size());

            iloop:
            for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {

                IRing r = (Ring) rs.getAtomContainer(i);


                if (r.getAtomCount() > 7 || r.getAtomCount() < 5) {
                    rs.removeAtomContainer(i);
                    i--; // go back to first one
                    continue iloop;
                }

                //int NonSP2Count = 0;

                for (int j = 0; j <= r.getAtomCount() - 1; j++) {

                    //logger.debug(j+"\t"+r.getAtomAt(j).getSymbol()+"\t"+r.getAtomAt(j).getHybridization());

                    if (r.getAtom(j).getHybridization() == CDKConstants.UNSET || 
                    	!(r.getAtom(j).getHybridization() == Hybridization.SP2 ||
                    	r.getAtom(j).getHybridization() == Hybridization.PLANAR3)) {
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
            return new RingSet();
        }
    }

    private boolean[] findRingsToCheck(IRingSet rs) {

        boolean[] Check = new boolean[rs.getAtomContainerCount()];

        for (int i = 0; i <= Check.length - 1; i++) {
            Check[i] = true;
        }

        iloop:

        for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {

            IRing r = (Ring) rs.getAtomContainer(i);

            if (r.getAtomCount() > 7) {
                Check[i] = false;
                continue;
            }

            int NonSP2Count = 0;

            for (int j = 0; j <= r.getAtomCount() - 1; j++) {

                // logger.debug(j+"\t"+r.getAtomAt(j).getSymbol()+"\t"+r.getAtomAt(j).getHybridization());

                if (r.getAtom(j).getHybridization() == CDKConstants.UNSET ||
                	r.getAtom(j).getHybridization() != Hybridization.SP2) {
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
     * Stores an IRingSet corresponding to a molecule using the bond numbers.
     * 
     * @param mol      The IMolecule for which to store the IRingSet.
     * @param ringSet  The IRingSet to store
     */
    private void storeRingSystem(IMolecule mol, IRingSet ringSet) {
    	listOfRings = new ArrayList<Integer[]>(); // this is a list of int arrays
    	for (int r = 0; r < ringSet.getAtomContainerCount(); ++r) {
    		IRing ring = (IRing)ringSet.getAtomContainer(r);
    		Integer[] bondNumbers = new Integer[ring.getBondCount()];
    		for (int i = 0; i < ring.getBondCount(); ++i)
    			bondNumbers[i] = mol.getBondNumber(ring.getBond(i));
    		listOfRings.add(bondNumbers);
    	}
    }
    
    /**
     * Recovers a RingSet corresponding to a molecule that has been
     * stored by storeRingSystem().
     * 
     * @param mol      The IMolecule for which to recover the IRingSet.     
     */
    private IRingSet recoverRingSystem(IMolecule mol) {
    	IRingSet ringSet = mol.getBuilder().newRingSet();
        for (Integer[] bondNumbers : listOfRings) {
            IRing ring = mol.getBuilder().newRing(bondNumbers.length);
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

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public boolean isInterrupted() {
		return this.interrupted;
	}

}
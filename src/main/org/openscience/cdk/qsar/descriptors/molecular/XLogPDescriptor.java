/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.BFSShortestPath;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.*;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Prediction of logP based on the atom-type method called XLogP. <b>Requires
 * all hydrogens to be explicit</b>.
 * <p>For description of the methodology see Ref. @cdk.cite{WANG97} and @cdk.cite{WANG00}
 * or <a href="http://www.chem.ac.ru/Chemistry/Soft/XLOGP.en.html">http://www.chem.ac.ru/Chemistry/Soft/XLOGP.en.html</a>.
 * Actually one molecular factor is missing (presence of para Hs donor pair).
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 *   <tr>
 *     <td>salicylFlag</td>
 *     <td>false</td>
 *     <td>True is to use the salicyl acid correction factor</td>
 *   </tr>
 * </table>
 *
 * <p>changed 2005-11-03 by chhoppe<br>
 *  -Internal hydrogen bonds are implemented<br>
 * CDK IDescriptor was validated against xlogp2.1<br>
 * As mentioned in the xlogP tutorial don't use charges, always draw bonds. To some extend we can support charges
 * but not in every case.
 * <p>CDK follows the program in following points (which is not documented in the paper):<br>
 * 	-Atomtyp 7 is -0.137<br>
 *  -Atomtype 81 is -0.447<br>
 *  -pi system does not consider P or S<br>
 *  -ring system >3<br>
 *  -aromatic ring systems >=6<br>
 *  -N atomtypes: (ring) is always (ring)c<br>
 *  -F 83 is not 0.375, the program uses 0.512 [2005-11-21]<br>
 *  -hydrophobic carbon is 1-3 relationship not 1-4 [2005-11-22]<br>
 *  -Atomtyp C 34/35/36 perception corrected [2005-11-22]; before Atomtyp perception ring perception is done -> slows run time<br>
 *
 *
 *
 *  <p>In question:<br>
 *  	-Correction factor for salicylic acid (in paper, but not used by the program)<br>
 *  	-Amid classification is not consequent (in 6 rings (R2)N-C(R)=0 is eg 46 and in !6 membered rings it is amid)<br>
 *  		-sometimes O=C(R)-N(R)-C(R)=O is an amid ... sometimes not<br>
 *		-Value for internal H bonds is in paper 0.429 but for no454 it is 0.643<br>
 *		-pi system defintion, the neighbourhood is unclear<br>
 *
 * <p>changed 2005-11-21 by chhoppe<br>
 * 	-added new parameter for the salicyl acid correction factor<br>
 *  -Corrected P and S perception for charges<br>
 *
 *
 *@author         mfe4, chhoppe
 *@cdk.created    2004-11-03
 *@cdk.module     qsarmolecular
 * @cdk.svnrev  $Revision$
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:xlogP
 *
 * @cdk.keyword XLogP
 * @cdk.keyword descriptor
 */
public class XLogPDescriptor implements IMolecularDescriptor {

    private boolean checkAromaticity = false;
    private boolean salicylFlag=false;
    private SSSRFinder ssrf=null;
    private static final String[] names = {"XLogP"};

    /**
     *  Constructor for the XLogPDescriptor object.
     */
    public XLogPDescriptor() { }


    /**
     *  Gets the specification attribute of the XLogPDescriptor object.
     *
     *@return    The specification value
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#xlogP",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     *  Sets the parameters attribute of the XLogPDescriptor object.
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 2) {
            throw new CDKException("XLogPDescriptor expects two parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The first parameter must be of type Boolean");
        }else if(!(params[1] instanceof Boolean)) {
            throw new CDKException("The second parameter must be of type Boolean");
        }
        // ok, all should be fine
        checkAromaticity = (Boolean) params[0];
        salicylFlag= (Boolean) params[1];
    }


    /**
     *Gets the parameters attribute of the XLogPDescriptor object.
     *
     *@return    The parameters value [boolean checkAromaticity, boolean salicylFlag]
     *@see #setParameters
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[2];
        params[0] = checkAromaticity;
        params[1] = salicylFlag;
        return params;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
         return new DescriptorValue(getSpecification(), getParameterNames(),
                 getParameters(), new DoubleResult(Double.NaN), getDescriptorNames(), e);
     }

    /**
     *  Calculates the xlogP for an atom container.
     *
     *  If checkAromaticity is true, the method check the aromaticity, if false, means that the aromaticity has
     *  already been checked. It is necessary to use before the call of this mehtod the
     *  addExplicitHydrogensToSatisfyValency method (HydrogenAdder classe).
     *
     *@param  atomContainer               AtomContainer
     *@return XLogP is a double
     */

    public DescriptorValue calculate(IAtomContainer atomContainer) {
        IAtomContainer ac;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }

        IRingSet rs = (IRingSet) new SSSRFinder(ac).findSSSR();
        IRingSet atomRingSet=null;
        if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                CDKHueckelAromaticityDetector.detectAromaticity(ac);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }
        double xlogP = 0;
//		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String symbol = "";
        int bondCount = 0;
        int atomCount = ac.getAtomCount();
        int hsCount = 0;
        double xlogPOld=0;
        IBond.Order maxBondOrder = IBond.Order.SINGLE;
        List<Integer> hBondAcceptors=new ArrayList<Integer>();
        List<Integer> hBondDonors=new ArrayList<Integer>();
        int checkAminoAcid=1;//if 0 no check, if >1 check
        IAtom atomi = null;
        for (int i = 0; i < atomCount; i++) {
            atomi = (IAtom)ac.getAtom(i);
            //			Problem fused ring systems
            atomRingSet= rs.getRings(atomi);
            atomi.setProperty("IS_IN_AROMATIC_RING", false);
            atomi.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, 0);
            //logger.debug("atomRingSet.size "+atomRingSet.size());
            if (atomRingSet.getAtomContainerCount()>0){
                if (atomRingSet.getAtomContainerCount()>1){
                	Iterator containers = RingSetManipulator.getAllAtomContainers(atomRingSet).iterator();
                	atomRingSet = rs.getBuilder().newRingSet();
                	while (containers.hasNext()) {
                		ssrf = new SSSRFinder((IAtomContainer)containers.next());
                		atomRingSet.add(ssrf.findEssentialRings());
                	}
                    //logger.debug(" SSSRatomRingSet.size "+atomRingSet.size());
                }
                for (int j=0;j<atomRingSet.getAtomContainerCount();j++){
                    if (j==0){
                        atomi.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, ((IRing) atomRingSet.getAtomContainer(j)).getRingSize());
                    }

                    if (((IRing)atomRingSet.getAtomContainer(j)).contains(atomi)){
                        if (((IRing)atomRingSet.getAtomContainer(j)).getRingSize()>=6 && atomi.getFlag(CDKConstants.ISAROMATIC)){
                            atomi.setProperty("IS_IN_AROMATIC_RING", true);
                        }
                        if (((IRing)atomRingSet.getAtomContainer(j)).getRingSize()< (Integer) atomi.getProperty(CDKConstants.PART_OF_RING_OF_SIZE)){
                            atomi.setProperty(CDKConstants.PART_OF_RING_OF_SIZE, ((IRing) atomRingSet.getAtomContainer(j)).getRingSize());
                        }
                    }
                }
            }//else{
            //logger.debug();
            //}
        }


        for (int i = 0; i < atomCount; i++) {
            atomi = (IAtom)ac.getAtom(i);
            if (xlogPOld==xlogP & i>0 & !symbol.equals("H")){
                //logger.debug("\nXlogPAssignmentError: Could not assign atom number:"+(i-1));
            }

            xlogPOld=xlogP;
            symbol = atomi.getSymbol();
            bondCount = ac.getConnectedBondsCount(atomi);
            hsCount = getHydrogenCount(ac, atomi);
            maxBondOrder = ac.getMaximumBondOrder(atomi);
            if (!symbol.equals("H")){
                //logger.debug("i:"+i+" Symbol:"+symbol+" "+" bondC:"+bondCount+" Charge:"+atoms[i].getFormalCharge()+" hsC:"+hsCount+" maxBO:"+maxBondOrder+" Arom:"+atoms[i].getFlag(CDKConstants.ISAROMATIC)+" AtomTypeX:"+getAtomTypeXCount(ac, atoms[i])+" PiSys:"+getPiSystemsCount(ac, atoms[i])+" C=:"+getDoubleBondedCarbonsCount(ac, atoms[i])+" AromCc:"+getAromaticCarbonsCount(ac,atoms[i])+" RS:"+((Integer)atoms[i].getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue()+"\t");
            }
            if (symbol.equals("C")) {
                if (bondCount == 2) {
                    // C sp
                    if (hsCount >= 1) {
                        xlogP += 0.209;
                        //logger.debug("XLOGP: 38		 0.209");
                    } else {
                        if (maxBondOrder == IBond.Order.DOUBLE) {
                            xlogP += 2.073;
                            //logger.debug("XLOGP: 40		 2.037");
                        } else if (maxBondOrder == IBond.Order.TRIPLE) {
                            xlogP += 0.33;
                            //logger.debug("XLOGP: 39		 0.33");
                        }
                    }
                }
                if (bondCount == 3) {
                    // C sp2
                    if ((Boolean) atomi.getProperty("IS_IN_AROMATIC_RING")) {
                        if (getAromaticCarbonsCount(ac, atomi) >= 2 && getAromaticNitrogensCount(ac,atomi)==0) {
                            if (hsCount == 0) {
                                if (getAtomTypeXCount(ac, atomi) == 0 ) {
                                    xlogP += 0.296;
                                    //logger.debug("XLOGP: 34		 0.296");
                                } else {
                                    xlogP -= 0.151;
                                    //logger.debug("XLOGP: 35	C.ar.x	-0.151");
                                }
                            } else {
                                xlogP += 0.337;
                                //logger.debug("XLOGP: 32		 0.337");
                            }
                            //} else if (getAromaticCarbonsCount(ac, atoms[i]) < 2 && getAromaticNitrogensCount(ac, atoms[i]) > 1) {
                        } else if (getAromaticNitrogensCount(ac, atomi) >= 1) {
                            if (hsCount == 0) {
                                if (getAtomTypeXCount(ac, atomi) == 0) {
                                    xlogP += 0.174;
                                    //logger.debug("XLOGP: 36	C.ar.(X)	 0.174");
                                } else {
                                    xlogP += 0.366;
                                    //logger.debug("XLOGP: 37		 0.366");
                                }
                            } else if (getHydrogenCount(ac, atomi) == 1) {
                                xlogP += 0.126;
                                //logger.debug("XLOGP: 33		 0.126");
                            }
                        }
                        //NOT aromatic, but sp2
                    } else {
                        if (hsCount == 0) {
                            if (getAtomTypeXCount(ac, atomi) == 0) {
                                if (getPiSystemsCount(ac, atomi) <= 1) {
                                    xlogP += 0.05;
                                    //logger.debug("XLOGP: 26		 0.05");
                                } else {
                                    xlogP += 0.013;
                                    //logger.debug("XLOGP: 27		 0.013");
                                }
                            }
                            else if (getAtomTypeXCount(ac, atomi) == 1) {
                                if (getPiSystemsCount(ac, atomi) == 0) {
                                    xlogP -= 0.03;
                                    //logger.debug("XLOGP: 28		-0.03");
                                } else {
                                    xlogP -= 0.027;
                                    //logger.debug("XLOGP: 29		-0.027");
                                }
                            }
                            else if (getAtomTypeXCount(ac, atomi) == 2) {
                                if (getPiSystemsCount(ac, atomi) ==0) {
                                    xlogP += 0.005;
                                    //logger.debug("XLOGP: 30		 0.005");
                                } else {
                                    xlogP -= 0.315;
                                    //logger.debug("XLOGP: 31		-0.315");
                                }
                            }
                        }
                        if (hsCount == 1) {
                            if (getAtomTypeXCount(ac, atomi) == 0) {
                                if (getPiSystemsCount(ac, atomi) == 0) {
                                    xlogP += 0.466;
                                    //logger.debug("XLOGP: 22		 0.466");
                                }
                                if (getPiSystemsCount(ac, atomi) == 1) {
                                    xlogP += 0.136;
                                    //logger.debug("XLOGP: 23		 0.136");
                                }
                            } else {
                                if (getPiSystemsCount(ac, atomi) == 0) {
                                    xlogP += 0.001;
                                    //logger.debug("XLOGP: 24		 0.001");
                                }
                                if (getPiSystemsCount(ac, atomi) == 1) {
                                    xlogP -= 0.31;
                                    //logger.debug("XLOGP: 25		-0.31");
                                }
                            }
                        }
                        if (hsCount == 2) {
                            xlogP += 0.42;
                            //logger.debug("XLOGP: 21		 0.42");
                        }
                        if (getIfCarbonIsHydrophobic(ac, atomi)) {
                            xlogP += 0.211;
                            //logger.debug("XLOGP: Hydrophobic Carbon	0.211");
                        }
                    }//sp2 NOT aromatic
                }

                if (bondCount == 4) {
                    // C sp3
                    if (hsCount == 0) {
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP -= 0.006;
                                //logger.debug("XLOGP: 16		-0.006");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP -= 0.57;
                                //logger.debug("XLOGP: 17		-0.57");
                            }
                            if (getPiSystemsCount(ac, atomi) >= 2) {
                                xlogP -= 0.317;
                                //logger.debug("XLOGP: 18		-0.317");
                            }
                        } else {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP -= 0.316;
                                //logger.debug("XLOGP: 19		-0.316");
                            } else {
                                xlogP -= 0.723;
                                //logger.debug("XLOGP: 20		-0.723");
                            }
                        }
                    }
                    if (hsCount == 1) {
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP += 0.127;
                                //logger.debug("XLOGP: 10		 0.127");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP -= 0.243;
                                //logger.debug("XLOGP: 11		-0.243");
                            }
                            if (getPiSystemsCount(ac, atomi) >= 2) {
                                xlogP -= 0.499;
                                //logger.debug("XLOGP: 12		-0.499");
                            }
                        } else {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP -= 0.205;
                                //logger.debug("XLOGP: 13		-0.205");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP -= 0.305;
                                //logger.debug("XLOGP: 14		-0.305");
                            }
                            if (getPiSystemsCount(ac, atomi) >= 2) {
                                xlogP -= 0.709;
                                //logger.debug("XLOGP: 15		-0.709");
                            }
                        }
                    }
                    if (hsCount == 2) {
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP += 0.358;
                                //logger.debug("XLOGP:  4		 0.358");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP -= 0.008;
                                //logger.debug("XLOGP:  5		-0.008");
                            }
                            if (getPiSystemsCount(ac, atomi) == 2) {
                                xlogP -= 0.185;
                                //logger.debug("XLOGP:  6		-0.185");
                            }
                        } else {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP -= 0.137;
                                //logger.debug("XLOGP:  7		-0.137");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP -= 0.303;
                                //logger.debug("XLOGP:  8		-0.303");
                            }
                            if (getPiSystemsCount(ac, atomi) == 2) {
                                xlogP -= 0.815;
                                //logger.debug("XLOGP:  9		-0.815");
                            }
                        }
                    }
                    if (hsCount > 2) {
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP += 0.528;
                                //logger.debug("XLOGP:  1		 0.528");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP += 0.267;
                                //logger.debug("XLOGP:  2		 0.267");
                            }
                        }else{
                            //if (getNitrogenOrOxygenCount(ac, atomi) == 1) {
                            xlogP -= 0.032;
                            //logger.debug("XLOGP:  3		-0.032");
                        }
                    }
                    if (getIfCarbonIsHydrophobic(ac, atomi)) {
                        xlogP += 0.211;
                        //logger.debug("XLOGP: Hydrophobic Carbon	0.211");
                    }
                }//csp3


            }//C

            if (symbol.equals("N")) {
                //NO2
                if (ac.getBondOrderSum(atomi) >= 3.0 && getOxygenCount(ac, atomi) >= 2 && 
                    maxBondOrder==IBond.Order.DOUBLE) {
                    xlogP += 1.178;
                    //logger.debug("XLOGP: 66		 1.178");
                }
                else {
                    if (getPresenceOfCarbonil(ac, atomi)>=1) {
                        // amidic nitrogen
                        if (hsCount == 0) {
                            if (getAtomTypeXCount(ac, atomi) == 0) {
                                xlogP += 0.078;
                                //logger.debug("XLOGP: 57		 0.078");
                            }
                            if (getAtomTypeXCount(ac, atomi) == 1) {
                                xlogP -= 0.118;
                                //logger.debug("XLOGP: 58		-0.118");
                            }
                        }
                        if (hsCount == 1) {
                            if (getAtomTypeXCount(ac, atomi) == 0) {
                                xlogP -= 0.096;
                                hBondDonors.add(i);
                                //logger.debug("XLOGP: 55		-0.096");
                            } else {
                                xlogP -= 0.044;
                                hBondDonors.add(i);
                                //logger.debug("XLOGP: 56		-0.044");
                            }
                        }
                        if (hsCount == 2) {
                            xlogP -= 0.646;
                            hBondDonors.add(i);
                            //logger.debug("XLOGP: 54		-0.646");
                        }
                    } else {//NO amidic nitrogen
                        if (bondCount == 1) {
                            // -C#N
                            if (getCarbonsCount(ac, atomi) == 1) {
                                xlogP -= 0.566;
                                //logger.debug("XLOGP: 68		-0.566");
                            }
                        }else if (bondCount == 2) {
                            // N sp2
                            if ((Boolean) atomi.getProperty("IS_IN_AROMATIC_RING")) {
                                xlogP -= 0.493;
                                //logger.debug("XLOGP: 67		-0.493");
                                if (checkAminoAcid!=0){ checkAminoAcid+=1;}
                            } else {
                                if (getDoubleBondedCarbonsCount(ac, atomi) == 0) {
                                    if (getDoubleBondedNitrogenCount(ac, atomi) == 0) {
                                        if (getDoubleBondedOxygenCount(ac, atomi) == 1) {
                                            xlogP += 0.427;
                                            //logger.debug("XLOGP: 65		 0.427");
                                        }
                                    }
                                    if (getDoubleBondedNitrogenCount(ac, atomi) == 1) {
                                        if (getAtomTypeXCount(ac, atomi) == 0) {
                                            xlogP += 0.536;
                                            //logger.debug("XLOGP: 63		 0.536");
                                        }
                                        if (getAtomTypeXCount(ac, atomi) == 1) {
                                            xlogP -= 0.597;
                                            //logger.debug("XLOGP: 64		-0.597");
                                        }
                                    }
                                }else if (getDoubleBondedCarbonsCount(ac, atomi) == 1) {
                                    if (getAtomTypeXCount(ac, atomi) == 0) {
                                        if (getPiSystemsCount(ac, atomi) == 0) {
                                            xlogP += 0.007;
                                            //logger.debug("XLOGP: 59		 0.007");
                                        }
                                        if (getPiSystemsCount(ac, atomi) == 1) {
                                            xlogP -= 0.275;
                                            //logger.debug("XLOGP: 60		-0.275");
                                        }
                                    }else if (getAtomTypeXCount(ac, atomi) == 1) {
                                        if (getPiSystemsCount(ac, atomi) == 0) {
                                            xlogP += 0.366;
                                            //logger.debug("XLOGP: 61		 0.366");
                                        }
                                        if (getPiSystemsCount(ac, atomi) == 1) {
                                            xlogP += 0.251;
                                            //logger.debug("XLOGP: 62		 0.251");
                                        }
                                    }
                                }
                            }
                        }else if (bondCount == 3) {
                            // N sp3
                            if (hsCount == 0) {
                                //if (rs.contains(atomi)&&ringSize>3) {
                                if (atomi.getFlag(CDKConstants.ISAROMATIC)|| (rs.contains(atomi)&& (Integer) atomi.getProperty(CDKConstants.PART_OF_RING_OF_SIZE) >3 && getPiSystemsCount(ac,atomi)>=1)){
                                    if (getAtomTypeXCount(ac, atomi) == 0) {
                                        xlogP += 0.881;
                                        //logger.debug("XLOGP: 51		 0.881");
                                    } else {
                                        xlogP -= 0.01;
                                        //logger.debug("XLOGP: 53		-0.01");
                                    }
                                } else {
                                    if (getAtomTypeXCount(ac, atomi) == 0) {
                                        if (getPiSystemsCount(ac, atomi) == 0) {
                                            xlogP += 0.159;
                                            //logger.debug("XLOGP: 49		 0.159");
                                        }
                                        if (getPiSystemsCount(ac, atomi) > 0) {
                                            xlogP += 0.761;
                                            //logger.debug("XLOGP: 50		 0.761");
                                        }
                                    } else {
                                        xlogP -= 0.239;
                                        //logger.debug("XLOGP: 52		-0.239");
                                    }
                                }
                            }else if (hsCount == 1) {
                                if (getAtomTypeXCount(ac, atomi) == 0) {
//									like pyrrole
                                    if (atomi.getFlag(CDKConstants.ISAROMATIC)|| (rs.contains(atomi)&& (Integer) atomi.getProperty(CDKConstants.PART_OF_RING_OF_SIZE) >3 && getPiSystemsCount(ac,atomi)>=2)) {
                                        xlogP += 0.545;
                                        hBondDonors.add(i);
                                        //logger.debug("XLOGP: 46		 0.545");
                                    } else {
                                        if (getPiSystemsCount(ac, atomi) == 0) {
                                            xlogP -= 0.112;
                                            hBondDonors.add(i);
                                            //logger.debug("XLOGP: 44		-0.112");
                                        }
                                        if (getPiSystemsCount(ac, atomi) > 0) {
                                            xlogP += 0.166;
                                            hBondDonors.add(i);
                                            //logger.debug("XLOGP: 45		 0.166");
                                        }
                                    }
                                } else {
                                    if (rs.contains(atomi)) {
                                        xlogP += 0.153;
                                        hBondDonors.add(i);
                                        //logger.debug("XLOGP: 48		 0.153");
                                    } else {
                                        xlogP += 0.324;
                                        hBondDonors.add(i);
                                        //logger.debug("XLOGP: 47		 0.324");
                                    }
                                }
                            }else if (hsCount == 2) {
                                if (getAtomTypeXCount(ac, atomi) == 0) {
                                    if (getPiSystemsCount(ac, atomi) == 0) {
                                        xlogP -= 0.534;
                                        hBondDonors.add(i);
                                        //logger.debug("XLOGP: 41		-0.534");
                                    }
                                    if (getPiSystemsCount(ac, atomi) == 1) {
                                        xlogP -= 0.329;
                                        hBondDonors.add(i);
                                        //logger.debug("XLOGP: 42		-0.329");
                                    }

                                    if (checkAminoAcid!=0){ checkAminoAcid+=1;}
                                } else {
                                    xlogP -= 1.082;
                                    hBondDonors.add(i);
                                    //logger.debug("XLOGP: 43		-1.082");
                                }
                            }
                        }
                    }
                }
            }
            if (symbol.equals("O")) {
                if (bondCount == 1 && maxBondOrder==IBond.Order.DOUBLE) {
                    xlogP -= 0.399;
                    if (!getPresenceOfHydroxy(ac,atomi)){
                        hBondAcceptors.add(i);
                    }
                    //logger.debug("XLOGP: 75	A=O	-0.399");
                }else if(bondCount == 1 && hsCount==0 && (getPresenceOfNitro(ac,atomi) || getPresenceOfCarbonil(ac,atomi)==1) || getPresenceOfSulfat(ac,atomi)){
                    xlogP -= 0.399;
                    if (!getPresenceOfHydroxy(ac,atomi)){
                        hBondAcceptors.add(i);
                    }
                    //logger.debug("XLOGP: 75	A=O	-0.399");
                }else if (bondCount >= 1) {
                    if (hsCount == 0 && bondCount==2) {
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP += 0.084;
                                //logger.debug("XLOGP: 72	R-O-R	 0.084");
                            }
                            if (getPiSystemsCount(ac, atomi) > 0) {
                                xlogP += 0.435;
                                //logger.debug("XLOGP: 73	R-O-R.1	 0.435");
                            }
                        }else if (getAtomTypeXCount(ac, atomi) == 1) {
                            xlogP += 0.105;
                            //logger.debug("XLOGP: 74	R-O-X	 0.105");
                        }
                    }else{
                        if (getAtomTypeXCount(ac, atomi) == 0) {
                            if (getPiSystemsCount(ac, atomi) == 0) {
                                xlogP -= 0.467;
                                hBondDonors.add(i);
                                hBondAcceptors.add(i);
                                //logger.debug("XLOGP: 69	R-OH	-0.467");
                            }
                            if (getPiSystemsCount(ac, atomi) == 1) {
                                xlogP += 0.082;
                                hBondDonors.add(i);
                                hBondAcceptors.add(i);
                                //logger.debug("XLOGP: 70	R-OH.1	 0.082");
                            }
                        }else if (getAtomTypeXCount(ac, atomi) == 1) {
                            xlogP -= 0.522;
                            hBondDonors.add(i);
                            hBondAcceptors.add(i);
                            //logger.debug("XLOGP: 71	X-OH	-0.522");
                        }
                    }
                }
            }
            if (symbol.equals("S")) {
                if ((bondCount == 1 && maxBondOrder==IBond.Order.DOUBLE) || 
                	(bondCount == 1 && atomi.getFormalCharge()==-1)) {
                    xlogP -= 0.148;
                    //logger.debug("XLOGP: 78	A=S	-0.148");
                }else if (bondCount == 2) {
                    if (hsCount == 0) {
                        xlogP += 0.255;
                        //logger.debug("XLOGP: 77	A-S-A	 0.255");
                    } else {
                        xlogP += 0.419;
                        //logger.debug("XLOGP: 76	A-SH	 0.419");
                    }
                }else if (bondCount == 3) {
                    if (getOxygenCount(ac, atomi) >= 1) {
                        xlogP -= 1.375;
                        //logger.debug("XLOGP: 79	A-SO-A	-1.375");
                    }
                }else if (bondCount == 4) {
                    if (getDoubleBondedOxygenCount(ac, atomi) >= 2) {
                        xlogP -= 0.168;
                        //logger.debug("XLOGP: 80	A-SO2-A	-0.168");
                    }
                }
            }
            if (symbol.equals("P")) {
                if (getDoubleBondedSulfurCount(ac, atomi) >= 1 && bondCount>=4) {
                    xlogP += 1.253;
                    //logger.debug("XLOGP: 82	S=PA3	 1.253");
                }else if (getOxygenCount(ac,atomi)>=1 || getDoubleBondedOxygenCount(ac, atomi) == 1 && bondCount>=4) {
                    xlogP -= 0.447;
                    //logger.debug("XLOGP: 81	O=PA3	-0.447");
                }
            }
            if (symbol.equals("F")) {
                if (getPiSystemsCount(ac, atomi) == 0) {
                    xlogP += 0.375;
                    //logger.debug("XLOGP: 83	F.0	 0.512");
                }else if (getPiSystemsCount(ac, atomi) == 1) {
                    xlogP += 0.202;
                    //logger.debug("XLOGP: 84	F.1	 0.202");
                }
            }
            if (symbol.equals("Cl")) {
                if (getPiSystemsCount(ac, atomi) == 0) {
                    xlogP += 0.512;
                    //logger.debug("XLOGP: 85	Cl.0	 0.512");
                }else if (getPiSystemsCount(ac, atomi) >= 1) {
                    xlogP += 0.663;
                    //logger.debug("XLOGP: 86	Cl.1	 0.663");
                }
            }
            if (symbol.equals("Br")) {
                if (getPiSystemsCount(ac, atomi) == 0) {
                    xlogP += 0.85;
                    //logger.debug("XLOGP: 87	Br.0	 0.85");
                }else if (getPiSystemsCount(ac, atomi) == 1) {
                    xlogP += 0.839;
                    //logger.debug("XLOGP: 88	Br.1	 0.839");
                }
            }
            if (symbol.equals("I")) {
                if (getPiSystemsCount(ac, atomi) == 0) {
                    xlogP += 1.05;
                    //logger.debug("XLOGP: 89	I.0	 1.05");
                }else if (getPiSystemsCount(ac, atomi) == 1) {
                    xlogP += 1.109;
                    //logger.debug("XLOGP: 90	I.1	 1.109");
                }
            }

//			Halogen pair 1-3
            int halcount=getHalogenCount(ac, atomi);
            if ( halcount== 2) {
                xlogP += 0.137;
                //logger.debug("XLOGP: Halogen 1-3 pair	 0.137");
            }else if (halcount==3){
                xlogP += (3*0.137);
                //logger.debug("XLOGP: Halogen 1-3 pair	 0.411");
            }else if (halcount==4){
                xlogP += (6*0.137);
                //logger.debug("XLOGP: Halogen 1-3 pair	 1.902");
            }

//			sp2 Oxygen 1-5 pair
            if (getPresenceOfCarbonil(ac, atomi) == 2) {// sp2 oxygen 1-5 pair
                if(!rs.contains(atomi)) {
                    xlogP += 0.580;
                    //logger.debug("XLOGP: sp2 Oxygen 1-5 pair	 0.580");
                }
            }
        }
        //logger.debug("XLOGP: Before Correction:"+xlogP);
        List path=null;
        SimpleGraph moleculeGraph=null;
        int [][] pairCheck=null;
//		//logger.debug("Acceptors:"+hBondAcceptors.size()+" Donors:"+hBondDonors.size());
        if (hBondAcceptors.size()>0 && hBondDonors.size()>0){
            moleculeGraph=MoleculeGraphs.getMoleculeGraph(ac);
            pairCheck=initializeHydrogenPairCheck(new int[atomCount][atomCount]);
        }
        for (int i=0; i<hBondAcceptors.size();i++){
            for (int j=0; j<hBondDonors.size();j++){
                if (checkRingLink(rs,ac,ac.getAtom(hBondAcceptors.get(i))) || checkRingLink(rs,ac,ac.getAtom(hBondDonors.get(j).intValue()))){
                    path=BFSShortestPath.findPathBetween(moleculeGraph,ac.getAtom(hBondAcceptors.get(i)), ac.getAtom((Integer) hBondDonors.get(j)));
//					//logger.debug(" Acc:"+checkRingLink(rs,ac,atoms[((Integer)hBondAcceptors.get(i)).intValue()])
//					+" S:"+atoms[((Integer)hBondAcceptors.get(i)).intValue()].getSymbol()
//					+" Nr:"+((Integer)hBondAcceptors.get(i)).intValue()
//					+" Don:"+checkRingLink(rs,ac,atoms[((Integer)hBondDonors.get(j)).intValue()])
//					+" S:"+atoms[((Integer)hBondDonors.get(j)).intValue()].getSymbol()
//					+" Nr:"+((Integer)hBondDonors.get(j)).intValue()
//					+" i:"+i+" j:"+j+" path:"+path.size());
                    if (checkRingLink(rs,ac,ac.getAtom(hBondAcceptors.get(i))) && checkRingLink(rs,ac,ac.getAtom(hBondDonors.get(j).intValue()))){
                        if (path.size()==3 && pairCheck[hBondAcceptors.get(i)][hBondDonors.get(j)]==0){
                            xlogP += 0.429;
                            pairCheck[hBondAcceptors.get(i)][hBondDonors.get(j)]=1;
                            pairCheck[hBondDonors.get(j)][hBondAcceptors.get(i)]=1;
                            //logger.debug("XLOGP: Internal HBonds 1-4	 0.429");
                        }
                    }else{
                        if (path.size()==4 && pairCheck[hBondAcceptors.get(i)][hBondDonors.get(j)]==0){
                            xlogP += 0.429;
                            pairCheck[hBondAcceptors.get(i)][hBondDonors.get(j)]=1;
                            pairCheck[hBondDonors.get(j)][hBondAcceptors.get(i)]=1;
                            //logger.debug("XLOGP: Internal HBonds 1-5	 0.429");
                        }
                    }
                }
            }
        }

        if (checkAminoAcid>1){
//			alpha amino acid
            QueryAtomContainer aminoAcid =
                QueryAtomContainerCreator.createBasicQueryContainer(
                        createAminoAcid(ac.getBuilder())
                );

            Iterator bonds = aminoAcid.bonds().iterator();
            IAtom bondAtom0=null;
            IAtom bondAtom1=null;
            while (bonds.hasNext()) {
                IBond bond = (IBond) bonds.next();
                bondAtom0=bond.getAtom(0);
                bondAtom1=bond.getAtom(1);
                if ((bondAtom0.getSymbol().equals("C") && bondAtom1.getSymbol().equals("N")) || (bondAtom0.getSymbol().equals("N") && bondAtom1.getSymbol().equals("C"))&& bond.getOrder() == IBond.Order.SINGLE){
                    aminoAcid.removeBond(bondAtom0,bondAtom1);
                    aminoAcid.addBond(new AnyOrderQueryBond((IQueryAtom)bondAtom0,(IQueryAtom)bondAtom1,IBond.Order.SINGLE));
                    break;
                }
            }

            //AtomContainer aminoacid = sp.parseSmiles("NCC(=O)O");
            try {
                if (UniversalIsomorphismTester.isSubgraph(ac, aminoAcid)) {
                    List list = UniversalIsomorphismTester.getSubgraphAtomsMap(ac, aminoAcid);
                    RMap map = null;
                    IAtom atom1=null;
                    for (int j = 0; j < list.size(); j++){
                        map = (RMap) list.get(j);
                        atom1 = ac.getAtom(map.getId1());
                        if (atom1.getSymbol().equals("O")&& ac.getMaximumBondOrder(atom1)==IBond.Order.SINGLE){
                            if (ac.getConnectedBondsCount(atom1)==2 && getHydrogenCount(ac, atom1)==0){
                            }else{
                                xlogP -= 2.166;
                                //logger.debug("XLOGP: alpha amino acid	-2.166");
                                break;
                            }
                        }
                    }
                }
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }

        IAtomContainer paba = createPaba(ac.getBuilder());
        // p-amino sulphonic acid
        try {
            if (UniversalIsomorphismTester.isSubgraph(ac, paba)) {
                xlogP -= 0.501;
                //logger.debug("XLOGP: p-amino sulphonic acid	-0.501");
            }
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        }

        // salicylic acid
        if (salicylFlag){
            IAtomContainer salicilic = createSalicylicAcid(ac.getBuilder());
            try {
                if (UniversalIsomorphismTester.isSubgraph(ac, salicilic)) {
                    xlogP += 0.554;
                    //logger.debug("XLOGP: salicylic acid	 0.554");
                }
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }


//		 ortho oxygen pair
        //AtomContainer orthopair = sp.parseSmiles("OCCO");
        QueryAtomContainer orthopair=new QueryAtomContainer();
        AromaticAtom atom1=new AromaticAtom();
        atom1.setSymbol("C");
        AromaticAtom atom2=new AromaticAtom();
        atom2.setSymbol("C");
        SymbolQueryAtom atom3=new SymbolQueryAtom();
        atom3.setSymbol("O");
        SymbolQueryAtom atom4=new SymbolQueryAtom();
        atom4.setSymbol("O");
        orthopair.addBond(new AromaticQueryBond(atom1,atom2,IBond.Order.SINGLE));
        orthopair.addBond(new OrderQueryBond(atom1,atom3,IBond.Order.SINGLE));
        orthopair.addBond(new OrderQueryBond(atom2,atom4,IBond.Order.SINGLE));

        try {
            if (UniversalIsomorphismTester.isSubgraph(ac, orthopair)) {
                xlogP -= 0.268;
                //logger.debug("XLOGP: Ortho oxygen pair	-0.268");
            }
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        }

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(xlogP), getDescriptorNames());
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     * Method initialise the HydrogenpairCheck with a value
     *
     * @param pairCheck value
     * @return void
     */
    public int[][] initializeHydrogenPairCheck(int [][] pairCheck) {
        for (int i = 0; i < pairCheck.length; i++) {
            for (int j = 0; j < pairCheck[0].length; j++) {
                    pairCheck[i][j] = 0;
            }
        }
        return pairCheck;
    }


    /**
     *  Check if atom or neighbour atom is part of a ring
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The hydrogenCount value
     */
    private boolean checkRingLink(IRingSet ringSet, org.openscience.cdk.interfaces.IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        if (ringSet.contains(atom)){
            return true;
        }
        for (IAtom neighbour : neighbours) {
            if (ringSet.contains(neighbour)) {
                return true;
            }
        }
        return false;
    }



    /**
     *  Gets the hydrogenCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The hydrogenCount value
     */
    private int getHydrogenCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int hcounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("H")) {
                hcounter += 1;
            }
        }
        return hcounter;
    }


    /**
     *  Gets the HalogenCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The alogenCount value
     */
    private int getHalogenCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int acounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("F") || neighbour.getSymbol().equals("I") || neighbour.getSymbol().equals("Cl") || neighbour.getSymbol().equals("Br")) {
                acounter += 1;
            }
        }
        return acounter;
    }

    /**
     *  Gets the atomType X Count attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The nitrogenOrOxygenCount value
     */
    private int getAtomTypeXCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int nocounter = 0;
        IBond bond;
        for (IAtom neighbour : neighbours) {
            if ((neighbour.getSymbol().equals("N") || neighbour.getSymbol().equals("O")) && !(Boolean) neighbour.getProperty("IS_IN_AROMATIC_RING")) {
                //if (ac.getMaximumBondOrder(neighbours[i]) == 1.0) {
                bond = ac.getBond(neighbour, atom);
                if (bond.getOrder() != IBond.Order.DOUBLE) {
                    nocounter += 1;
                }
            }
        }
        return nocounter;
    }


    /**
     *  Gets the aromaticCarbonsCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The aromaticCarbonsCount value
     */
    private int getAromaticCarbonsCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int carocounter = 0;
        for (IAtom neighbour : neighbours) {            
            if (neighbour.getSymbol().equals("C") && neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                carocounter += 1;
            }
        }
        return carocounter;
    }


    /**
     *  Gets the carbonsCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The carbonsCount value
     */
    private int getCarbonsCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int ccounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("C")) {
                if (!neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                    ccounter += 1;
                }
            }
        }
        return ccounter;
    }

    /**
     *  Gets the oxygenCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The carbonsCount value
     */
    private int getOxygenCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int ocounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("O")) {
                if (!neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                    ocounter += 1;
                }
            }
        }
        return ocounter;
    }


    /**
     *  Gets the doubleBondedCarbonsCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The doubleBondedCarbonsCount value
     */
    private int getDoubleBondedCarbonsCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        IBond bond;
        int cdbcounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("C")) {
                bond = ac.getBond(neighbour, atom);
                if (bond.getOrder() == IBond.Order.DOUBLE) {
                    cdbcounter += 1;
                }
            }
        }
        return cdbcounter;
    }


    /**
     *  Gets the doubleBondedOxygenCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The doubleBondedOxygenCount value
     */
    private int getDoubleBondedOxygenCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        IBond bond;
        int odbcounter = 0;
        boolean chargeFlag=false;
        if (atom.getFormalCharge()>=1){
            chargeFlag=true;
        }
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("O")) {
                bond = ac.getBond(neighbour, atom);
                if (chargeFlag && neighbour.getFormalCharge() == -1 && bond.getOrder() == IBond.Order.SINGLE) {
                    odbcounter += 1;
                }
                if (!neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                    if (bond.getOrder() == IBond.Order.DOUBLE) {
                        odbcounter += 1;
                    }
                }
            }
        }
        return odbcounter;
    }


    /**
     *  Gets the doubleBondedSulfurCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The doubleBondedSulfurCount value
     */
    private int getDoubleBondedSulfurCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        IBond bond;
        int sdbcounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("S")) {
                if (atom.getFormalCharge() == 1 && neighbour.getFormalCharge() == -1) {
                    sdbcounter += 1;
                }
                bond = ac.getBond(neighbour, atom);
                if (!neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                    if (bond.getOrder() == IBond.Order.DOUBLE) {
                        sdbcounter += 1;
                    }
                }
            }
        }
        return sdbcounter;
    }


    /**
     *  Gets the doubleBondedNitrogenCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The doubleBondedNitrogenCount value
     */
    private int getDoubleBondedNitrogenCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        IBond bond;
        int ndbcounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("N")) {
                bond = ac.getBond(neighbour, atom);
                if (!neighbour.getFlag(CDKConstants.ISAROMATIC)) {
                    if (bond.getOrder() == IBond.Order.DOUBLE) {
                        ndbcounter += 1;
                    }
                }
            }
        }
        return ndbcounter;
    }


    /**
     *  Gets the aromaticNitrogensCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The aromaticNitrogensCount value
     */
    private int getAromaticNitrogensCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        int narocounter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("N") && (Boolean) neighbour.getProperty("IS_IN_AROMATIC_RING")) {
                narocounter += 1;
            }
        }
        return narocounter;
    }



    // a piSystem is a double or triple or aromatic bond:
    /**
     *  Gets the piSystemsCount attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The piSystemsCount value
     */
    private int getPiSystemsCount(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List neighbours = ac.getConnectedAtomsList(atom);
        int picounter = 0;
        java.util.List bonds=null;
        for (int i = 0; i < neighbours.size(); i++) {
            IAtom neighbour = (IAtom)neighbours.get(i);
            bonds = ac.getConnectedBondsList(neighbour);
            for (int j = 0; j < bonds.size(); j++) {
                IBond bond = (IBond)bonds.get(j);
                if (bond.getOrder()!=IBond.Order.SINGLE && bond.getConnectedAtom(neighbour)!=atom
                        && !neighbour.getSymbol().equals("P")
                        && !neighbour.getSymbol().equals("S")){
                    picounter += 1;
                }/*else if (bonds[j].getConnectedAtom(neighbours[i])!=atom
						&& !neighbours[i].getSymbol().equals("P")
						&& !neighbours[i].getSymbol().equals("S") && bonds[j].getConnectedAtom(neighbours[i]).getFlag(CDKConstants.ISAROMATIC)){
					picounter += 1;
				}*/
            }
        }
        return picounter;
    }

    /**
     *  Gets the presenceOf Hydroxy group attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The presenceOfCarbonil value
     */
    private boolean getPresenceOfHydroxy(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        IAtom neighbour0 = (IAtom)ac.getConnectedAtomsList(atom).get(0);
        java.util.List first = null;
        if (neighbour0.getSymbol().equals("C")) {
            first = ac.getConnectedAtomsList(neighbour0);
            for (int i = 0; i < first.size(); i++) {
                IAtom conAtom = (IAtom)first.get(i);
                if (conAtom.getSymbol().equals("O")) {
                    if(ac.getBond(neighbour0, conAtom).getOrder() == IBond.Order.SINGLE){
                        if (ac.getConnectedBondsCount(conAtom)>1 && getHydrogenCount(ac,conAtom)==0){
                            return false;
                        }else{
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     *  Gets the presenceOfN=O attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The presenceOfNitor [boolean]
     */
    private boolean getPresenceOfNitro(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List neighbours = ac.getConnectedAtomsList(atom);
        java.util.List second = null;
        IBond bond = null;
        //int counter = 0;
        for (int i = 0; i < neighbours.size(); i++) {
            IAtom neighbour = (IAtom)neighbours.get(i);
            if (neighbour.getSymbol().equals("N")) {
                second = ac.getConnectedAtomsList(neighbour);
                for (int b = 0; b < second.size(); b++) {
                    IAtom conAtom = (IAtom)second.get(b);
                    if (conAtom.getSymbol().equals("O")) {
                        bond = ac.getBond(neighbour, conAtom);
                        if (bond.getOrder() == IBond.Order.DOUBLE) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     *  Gets the presenceOfSulfat A-S(O2)-A attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The presenceOfSulfat [boolean]
     */
    private boolean getPresenceOfSulfat(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List<IAtom> neighbours = ac.getConnectedAtomsList(atom);
        //org.openscience.cdk.interfaces.IAtom[] second = null;
        //IBond bond = null;
        //int counter = 0;
        for (IAtom neighbour : neighbours) {
            if (neighbour.getSymbol().equals("S") && getOxygenCount(ac, neighbour) >= 2 && ac.getConnectedBondsCount(neighbour) == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Gets the presenceOfCarbonil attribute of the XLogPDescriptor object.
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The presenceOfCarbonil value
     */
    private int getPresenceOfCarbonil(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List neighbours = ac.getConnectedAtomsList(atom);
        java.util.List second = null;
        IBond bond = null;
        int counter = 0;
        for (int i = 0; i < neighbours.size(); i++) {
            IAtom neighbour = (IAtom)neighbours.get(i);
            if (neighbour.getSymbol().equals("C")) {
                second = ac.getConnectedAtomsList(neighbour);
                for (int b = 0; b < second.size(); b++) {
                    IAtom conAtom = (IAtom)second.get(b);
                    if (conAtom.getSymbol().equals("O")) {
                        bond = ac.getBond(neighbour, conAtom);
                        if (bond.getOrder() == IBond.Order.DOUBLE) {
                            counter +=1;
                        }
                    }
                }
            }
        }
        return counter;
    }


    /**
     *  Gets the ifCarbonIsHydrophobic attribute of the XLogPDescriptor object.
     *  C must be sp2 or sp3 and, for all distances C-1-2-3 only C atoms are permitted
     *
     *@param  ac    Description of the Parameter
     *@param  atom  Description of the Parameter
     *@return       The ifCarbonIsHydrophobic value
     */
    private boolean getIfCarbonIsHydrophobic(IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
        java.util.List first = ac.getConnectedAtomsList(atom);
        java.util.List second = null;
        java.util.List third = null;
        //org.openscience.cdk.interfaces.IAtom[] fourth = null;
        if (first.size() > 0) {
            for (int i = 0; i < first.size(); i++) {
                IAtom firstAtom = (IAtom)first.get(i);
                if (firstAtom.getSymbol().equals("C") || firstAtom.getSymbol().equals("H")) {
                } else {
                    return false;
                }
                second = ac.getConnectedAtomsList(firstAtom);
                if (second.size() > 0) {
                    for (int b = 0; b < second.size(); b++) {
                        IAtom secondAtom = (IAtom)second.get(b);
                        if (secondAtom.getSymbol().equals("C") || secondAtom.getSymbol().equals("H")) {
                        } else {
                            return false;
                        }
                        third = ac.getConnectedAtomsList(secondAtom);
                        if (third.size() > 0) {
                            for (int c = 0; c < third.size(); c++) {
                                IAtom thirdAtom = (IAtom)third.get(c);
                                if (thirdAtom.getSymbol().equals("C") || thirdAtom.getSymbol().equals("H")) {
                                } else {
                                    return false;
                                }
                                //fourth = ac.getConnectedAtoms(third[c]);
                                //if (fourth.length > 0) {
                                //	for (int d = 0; d < fourth.length; d++) {
                                //		if (fourth[d].getSymbol().equals("C") || fourth[d].getSymbol().equals("H")) {
                                //		} else {
                                //			return false;
                                //		}
                                //	}
                                //} else {
                                //	return false;
                                //}
                            }
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }



    /**
     *  Gets the parameterNames attribute of the XLogPDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[2];
        params[0] = "checkAromaticity";
        params[1] = "salicylFlag";
        return params;
    }



    /**
     *  Gets the parameterType attribute of the XLogPDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
            return true;
    }

    private IAtomContainer createPaba(IChemObjectBuilder builder) {
        // SMILES CS(=O)(=O)c1ccc(N)cc1
        IAtomContainer container = builder.newAtomContainer();
        IAtom atom1 = builder.newAtom("C");
        container.addAtom(atom1);
        IAtom atom2 = builder.newAtom("S");
        container.addAtom(atom2);
        IAtom atom3 = builder.newAtom("O");
        container.addAtom(atom3);
        IAtom atom4 = builder.newAtom("O");
        container.addAtom(atom4);
        IAtom atom5 = builder.newAtom("C"); atom5.setHybridization(Hybridization.SP2);
        container.addAtom(atom5);
        IAtom atom6 = builder.newAtom("C"); atom6.setHybridization(Hybridization.SP2);
        container.addAtom(atom6);
        IAtom atom7 = builder.newAtom("C"); atom7.setHybridization(Hybridization.SP2);
        container.addAtom(atom7);
        IAtom atom8 = builder.newAtom("C"); atom8.setHybridization(Hybridization.SP2);
        container.addAtom(atom8);
        IAtom atom9 = builder.newAtom("N");
        container.addAtom(atom9);
        IAtom atom10 = builder.newAtom("C"); atom10.setHybridization(Hybridization.SP2);
        container.addAtom(atom10);
        IAtom atom11 = builder.newAtom("C"); atom11.setHybridization(Hybridization.SP2);
        container.addAtom(atom11);
        IBond bond1 = builder.newBond(atom1, atom2, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond1);
        IBond bond2 = builder.newBond(atom2, atom3, CDKConstants.BONDORDER_DOUBLE);
        container.addBond(bond2);
        IBond bond3 = builder.newBond(atom2, atom4, CDKConstants.BONDORDER_DOUBLE);
        container.addBond(bond3);
        IBond bond4 = builder.newBond(atom2, atom5, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond4);
        IBond bond5 = builder.newBond(atom5, atom6, CDKConstants.BONDORDER_DOUBLE);
        bond5.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond5);
        IBond bond6 = builder.newBond(atom6, atom7, CDKConstants.BONDORDER_SINGLE);
        bond6.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond6);
        IBond bond7 = builder.newBond(atom7, atom8, CDKConstants.BONDORDER_DOUBLE);
        bond7.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond7);
        IBond bond8 = builder.newBond(atom8, atom9, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond8);
        IBond bond9 = builder.newBond(atom8, atom10, CDKConstants.BONDORDER_SINGLE);
        bond9.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond9);
        IBond bond10 = builder.newBond(atom10, atom11, CDKConstants.BONDORDER_DOUBLE);
        bond10.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond10);
        IBond bond11 = builder.newBond(atom5, atom11, CDKConstants.BONDORDER_SINGLE);
        bond11.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond11);

        return container;
    }

    private IAtomContainer createAminoAcid(IChemObjectBuilder builder) {
        // SMILES NCC(=O)O
        IAtomContainer container = builder.newAtomContainer();
        IAtom atom1 = builder.newAtom("N");
        container.addAtom(atom1);
        IAtom atom2 = builder.newAtom("C");
        container.addAtom(atom2);
        IAtom atom3 = builder.newAtom("C"); // carbonyl
        container.addAtom(atom3);
        IAtom atom4 = builder.newAtom("O"); // carbonyl
        container.addAtom(atom4);
        IAtom atom5 = builder.newAtom("O");
        container.addAtom(atom5);
        container.addBond(builder.newBond(atom1, atom2, CDKConstants.BONDORDER_SINGLE));
        container.addBond(builder.newBond(atom2, atom3, CDKConstants.BONDORDER_SINGLE));
        container.addBond(builder.newBond(atom3, atom4, CDKConstants.BONDORDER_DOUBLE));
        container.addBond(builder.newBond(atom3, atom5, CDKConstants.BONDORDER_SINGLE));
        return container;
    }

    private IAtomContainer createSalicylicAcid(IChemObjectBuilder builder) {
        // SMILES O=C(O)c1ccccc1O
        IAtomContainer container = builder.newAtomContainer();
        IAtom atom1 = builder.newAtom("C");
        container.addAtom(atom1);
        IAtom atom2 = builder.newAtom("O");
        container.addAtom(atom2);
        IAtom atom3 = builder.newAtom("O");
        container.addAtom(atom3);
        IAtom atom4 = builder.newAtom("C"); atom4.setHybridization(Hybridization.SP2);
        container.addAtom(atom4);
        IAtom atom5 = builder.newAtom("C"); atom5.setHybridization(Hybridization.SP2);
        container.addAtom(atom5);
        IAtom atom6 = builder.newAtom("C"); atom6.setHybridization(Hybridization.SP2);
        container.addAtom(atom6);
        IAtom atom7 = builder.newAtom("C"); atom7.setHybridization(Hybridization.SP2);
        container.addAtom(atom7);
        IAtom atom8 = builder.newAtom("C"); atom8.setHybridization(Hybridization.SP2);
        container.addAtom(atom8);
        IAtom atom9 = builder.newAtom("C"); atom9.setHybridization(Hybridization.SP2);
        container.addAtom(atom9);
        IAtom atom10 = builder.newAtom("O");
        container.addAtom(atom10);
        IBond bond1 = builder.newBond(atom1, atom2, CDKConstants.BONDORDER_DOUBLE);
        container.addBond(bond1);
        IBond bond2 = builder.newBond(atom1, atom3, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond2);
        IBond bond3 = builder.newBond(atom1, atom4, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond3);
        IBond bond4 = builder.newBond(atom4, atom5, CDKConstants.BONDORDER_DOUBLE);
        bond4.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond4);
        IBond bond5 = builder.newBond(atom5, atom6, CDKConstants.BONDORDER_SINGLE);
        bond5.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond5);
        IBond bond6 = builder.newBond(atom6, atom7, CDKConstants.BONDORDER_DOUBLE);
        bond6.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond6);
        IBond bond7 = builder.newBond(atom7, atom8, CDKConstants.BONDORDER_SINGLE);
        bond7.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond7);
        IBond bond8 = builder.newBond(atom8, atom9, CDKConstants.BONDORDER_DOUBLE);
        bond8.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond8);
        IBond bond9 = builder.newBond(atom9, atom4, CDKConstants.BONDORDER_SINGLE);
        bond9.setFlag(CDKConstants.ISAROMATIC, true);
        container.addBond(bond9);
        IBond bond10 = builder.newBond(atom9, atom10, CDKConstants.BONDORDER_SINGLE);
        container.addBond(bond10);

        return container;
    }
}


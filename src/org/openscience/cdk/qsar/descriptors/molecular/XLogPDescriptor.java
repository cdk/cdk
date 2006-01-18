/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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

import java.util.List;
import java.util.Vector;

import org._3pq.jgrapht.graph.SimpleGraph;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.OrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.isomorphism.matchers.SymbolQueryAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AnyOrderQueryBond;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.AromaticQueryBond;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.graph.BFSShortestPath;
import org.openscience.cdk.graph.MoleculeGraphs;

/**
 * Prediction of logP based on the atom-type method called XLogP. For
 * description of the methodology see Ref. @cdk.cite{WANG97} and @cdk.cite{WANG00}
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
 * changed 2005-11-03 by chhoppe
 *  -Internal hydrogen bonds are implemented
 * CDK IDescriptor was validated against xlogp2.1
 * As mentioned in the xlogP tutorial don't use charges, always draw bonds. To some extend we can support charges
 * but not in every case.
 * CDK follows the program in following points (which is not documented in the paper):
 * 	-Atomtyp 7 is -0.137
 *  -Atomtype 81 is -0.447
 *  -pi system does not consider P or S
 *  -ring system >3
 *  -aromatic ring systems >=6
 *  -N atomtypes: (ring) is always (ring)c
 *  -F 83 is not 0.375, the program uses 0.512 [2005-11-21]
 *  -hydrophobic carbon is 1-3 relationship not 1-4 [2005-11-22]
 *  -Atomtyp C 34/35/36 perception corrected [2005-11-22]; before Atomtyp perception ring perception is done -> slows run time
 *  
 *  
 *  
 *  In question: 
 *  	-Correction factor for salicylic acid (in paper, but not used by the program)
 *  	-Amid classification is not consequent (in 6 rings (R2)N-C(R)=0 is eg 46 and in !6 membered rings it is amid)
 *  		-sometimes O=C(R)-N(R)-C(R)=O is an amid ... sometimes not 
 *		-Value for internal H bonds is in paper 0.429 but for no454 it is 0.643
 *		-pi system defintion, the neighbourhood is unclear
 * 
 * changed 2005-11-21 by chhoppe
 * 	-added new parameter for the salicyl acid correction factor
 *  -Corrected P and S perception for charges
 * 
 * 
 *@author         mfe4, chhoppe
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:xlogP
 */
public class XLogPDescriptor implements IDescriptor {
    
	private boolean checkAromaticity = false;
	private boolean salicylFlag=false;
	private SSSRFinder ssrf=null;
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
		checkAromaticity = ((Boolean) params[0]).booleanValue();
		salicylFlag=((Boolean) params[1]).booleanValue();
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
		params[0] = new Boolean(checkAromaticity);
		params[1] = new Boolean(salicylFlag);
		return params;
	}


	/**
	 *  Calculates the xlogP for an atom container.
     *
     *  If checkAromaticity is true, the
	 *  method check the aromaticity, if false, means that the aromaticity has
	 *  already been checked. It is necessary to use before the
	 *  addExplicitHydrogensToSatisfyValency method (HydrogenAdder classe).
	 *
	 *@param  ac                AtomContainer
	 *@return                   XLogP is a double
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtomContainer ac) throws CDKException {
		RingSet rs = (new AllRingsFinder()).findAllRings(ac);
		RingSet atomRingSet=null;
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		double xlogP = 0;
		SmilesParser sp = new SmilesParser();
		org.openscience.cdk.interfaces.IAtom[] atoms = ac.getAtoms();
		String symbol = "";
		int bondCount = 0;
		int hsCount = 0;
		double xlogPOld=0;
		double maxBondOrder = 0;
		Vector hBondAcceptors=new Vector();
		Vector hBondDonors=new Vector();
		int checkAminoAcid=1;//if 0 no check, if >1 check
		
		for (int i = 0; i < atoms.length; i++) {
			//			Problem fused ring systems
			atomRingSet=rs.getRings(atoms[i]);
			atoms[i].setProperty("IS_IN_AROMATIC_RING", new Boolean(false));
			atoms[i].setProperty(CDKConstants.PART_OF_RING_OF_SIZE, new Integer(0));
			//System.out.print("atomRingSet.size "+atomRingSet.size());
			if (atomRingSet.size()>0){
				if (atomRingSet.size()>1){
					ssrf=new SSSRFinder(RingSetManipulator.getAllInOneContainer(atomRingSet));
					atomRingSet=ssrf.findEssentialRings();
					//System.out.println(" SSSRatomRingSet.size "+atomRingSet.size());
				}
				for (int j=0;j<atomRingSet.size();j++){
					if (j==0){
						atoms[i].setProperty(CDKConstants.PART_OF_RING_OF_SIZE, new Integer(((Ring)atomRingSet.get(j)).getRingSize()));
					}
					
					if (((Ring)atomRingSet.get(j)).contains(atoms[i])){
						if (((Ring)atomRingSet.get(j)).getRingSize()>=6 && atoms[i].getFlag(CDKConstants.ISAROMATIC)){
							atoms[i].setProperty("IS_IN_AROMATIC_RING", new Boolean(true));
						}
						if (((Ring)atomRingSet.get(j)).getRingSize()<((Integer)atoms[i].getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue()){
							atoms[i].setProperty(CDKConstants.PART_OF_RING_OF_SIZE, new Integer(((Ring)atomRingSet.get(j)).getRingSize()));
						}
					}
				}
			}//else{
				//System.out.println();
			//}
		}
		
		
		for (int i = 0; i < atoms.length; i++) {
			if (xlogPOld==xlogP & i>0 & !symbol.equals("H")){
				//System.out.println("\nXlogPAssignmentError: Could not assign atom number:"+(i-1));
			}
			
			xlogPOld=xlogP;
			symbol = atoms[i].getSymbol();
			bondCount = ac.getBondCount(atoms[i]);
			hsCount = getHydrogenCount(ac, atoms[i]);
			maxBondOrder = ac.getMaximumBondOrder(atoms[i]);
			if (!symbol.equals("H")){
				//System.out.print("i:"+i+" Symbol:"+symbol+" "+" bondC:"+bondCount+" Charge:"+atoms[i].getFormalCharge()+" hsC:"+hsCount+" maxBO:"+maxBondOrder+" Arom:"+atoms[i].getFlag(CDKConstants.ISAROMATIC)+" AtomTypeX:"+getAtomTypeXCount(ac, atoms[i])+" PiSys:"+getPiSystemsCount(ac, atoms[i])+" C=:"+getDoubleBondedCarbonsCount(ac, atoms[i])+" AromCc:"+getAromaticCarbonsCount(ac,atoms[i])+" RS:"+((Integer)atoms[i].getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue()+"\t");
			}
			if (symbol.equals("C")) {
				if (bondCount == 2) {
					// C sp
					if (hsCount >= 1) {
						xlogP += 0.209;
						//System.out.println("XLOGP: 38		 0.209");
					} else {
						if (maxBondOrder == 2.0) {
							xlogP += 2.073;
							//System.out.println("XLOGP: 40		 2.037");
						} else if (maxBondOrder == 3.0) {
							xlogP += 0.33;
							//System.out.println("XLOGP: 39		 0.33");
						}
					}
				}
				if (bondCount == 3) {
					// C sp2
					if (((Boolean)atoms[i].getProperty("IS_IN_AROMATIC_RING")).booleanValue()) {
						if (getAromaticCarbonsCount(ac, atoms[i]) >= 2 && getAromaticNitrogensCount(ac,atoms[i])==0) {
							if (hsCount == 0) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0 ) {
									xlogP += 0.296;
									//System.out.println("XLOGP: 34		 0.296");
								} else {
									xlogP -= 0.151;
									//System.out.println("XLOGP: 35	C.ar.x	-0.151");
								}
							} else {
								xlogP += 0.337;
								//System.out.println("XLOGP: 32		 0.337");
							}
						//} else if (getAromaticCarbonsCount(ac, atoms[i]) < 2 && getAromaticNitrogensCount(ac, atoms[i]) > 1) {
						} else if (getAromaticNitrogensCount(ac, atoms[i]) >= 1) {
							if (hsCount == 0) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
									xlogP += 0.174;
									//System.out.println("XLOGP: 36	C.ar.(X)	 0.174");
								} else {
									xlogP += 0.366;
									//System.out.println("XLOGP: 37		 0.366");
								}
							} else if (getHydrogenCount(ac, atoms[i]) == 1) {
								xlogP += 0.126;
								//System.out.println("XLOGP: 33		 0.126");
							}
						}
					//NOT aromatic, but sp2
					} else {
						if (hsCount == 0) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) <= 1) {
									xlogP += 0.05;
									//System.out.println("XLOGP: 26		 0.05");
								} else {
									xlogP += 0.013;
									//System.out.println("XLOGP: 27		 0.013");
								}
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 1) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP -= 0.03;
									//System.out.println("XLOGP: 28		-0.03");
								} else {
									xlogP -= 0.027;
									//System.out.println("XLOGP: 29		-0.027");
								}
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 2) {
								if (getPiSystemsCount(ac, atoms[i]) ==0) {
									xlogP += 0.005;
									//System.out.println("XLOGP: 30		 0.005");
								} else {
									xlogP -= 0.315;
									//System.out.println("XLOGP: 31		-0.315");
								}
							}
						}
						if (hsCount == 1) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.466;
									//System.out.println("XLOGP: 22		 0.466");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP += 0.136;
									//System.out.println("XLOGP: 23		 0.136");
								}
							} else {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.001;
									//System.out.println("XLOGP: 24		 0.001");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP -= 0.31;
									//System.out.println("XLOGP: 25		-0.31");
								}
							}
						}
						if (hsCount == 2) {
							xlogP += 0.42;
							//System.out.println("XLOGP: 21		 0.42");
						}
						if (getIfCarbonIsHydrophobic(ac, atoms[i])) {
							xlogP += 0.211;
							//System.out.println("XLOGP: Hydrophobic Carbon	0.211");
						}
					}//sp2 NOT aromatic
				}
				
				if (bondCount == 4) {
					// C sp3
					if (hsCount == 0) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.006;
								//System.out.println("XLOGP: 16		-0.006");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.57;
								//System.out.println("XLOGP: 17		-0.57");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.317;
								//System.out.println("XLOGP: 18		-0.317");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.316;
								//System.out.println("XLOGP: 19		-0.316");
							} else {
								xlogP -= 0.723;
								//System.out.println("XLOGP: 20		-0.723");
							}
						}
					}
					if (hsCount == 1) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.127;
								//System.out.println("XLOGP: 10		 0.127");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.243;
								//System.out.println("XLOGP: 11		-0.243");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.499;
								//System.out.println("XLOGP: 12		-0.499");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.205;
								//System.out.println("XLOGP: 13		-0.205");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.305;
								//System.out.println("XLOGP: 14		-0.305");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.709;
								//System.out.println("XLOGP: 15		-0.709");
							}
						}
					}
					if (hsCount == 2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.358;
								//System.out.println("XLOGP:  4		 0.358");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.008;
								//System.out.println("XLOGP:  5		-0.008");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.185;
								//System.out.println("XLOGP:  6		-0.185");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.137;
								//System.out.println("XLOGP:  7		-0.137");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.303;
								//System.out.println("XLOGP:  8		-0.303");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.815;
								//System.out.println("XLOGP:  9		-0.815");
							}
						}
					}
					if (hsCount > 2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.528;
								//System.out.println("XLOGP:  1		 0.528");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.267;
								//System.out.println("XLOGP:  2		 0.267");
							}
						}else{
						//if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
							xlogP -= 0.032;
							//System.out.println("XLOGP:  3		-0.032");
						}
					}
					if (getIfCarbonIsHydrophobic(ac, atoms[i])) {
						xlogP += 0.211;
						//System.out.println("XLOGP: Hydrophobic Carbon	0.211");
					}
				}//csp3
				
				
			}//C
			
			if (symbol.equals("N")) {
				//NO2
				if (ac.getBondOrderSum(atoms[i]) >= 3.0 && getOxygenCount(ac, atoms[i]) >= 2 && maxBondOrder==2) {
					xlogP += 1.178;
					//System.out.println("XLOGP: 66		 1.178");
				}
				else {
					if (getPresenceOfCarbonil(ac, atoms[i])>=1) {
						// amidic nitrogen
						if (hsCount == 0) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								xlogP += 0.078;
								//System.out.println("XLOGP: 57		 0.078");
							}
							if (getAtomTypeXCount(ac, atoms[i]) == 1) {
								xlogP -= 0.118;
								//System.out.println("XLOGP: 58		-0.118");
							}
						}
						if (hsCount == 1) {
							if (getAtomTypeXCount(ac, atoms[i]) == 0) {
								xlogP -= 0.096;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 55		-0.096");
							} else {
								xlogP -= 0.044;
								hBondDonors.add(new Integer(i));
								//System.out.println("XLOGP: 56		-0.044");
							}
						}
						if (hsCount == 2) {
							xlogP -= 0.646;
							hBondDonors.add(new Integer(i));
							//System.out.println("XLOGP: 54		-0.646");
						}
					} else {//NO amidic nitrogen
						if (bondCount == 1) {
							// -C#N
							if (getCarbonsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.566;
								//System.out.println("XLOGP: 68		-0.566");
							}
						}else if (bondCount == 2) {
							// N sp2
							if (((Boolean)atoms[i].getProperty("IS_IN_AROMATIC_RING")).booleanValue()) {
								xlogP -= 0.493;
								//System.out.println("XLOGP: 67		-0.493");
								if (checkAminoAcid!=0){ checkAminoAcid+=1;}								
							} else {
								if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 0) {
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 0) {
										if (getDoubleBondedOxygenCount(ac, atoms[i]) == 1) {
											xlogP += 0.427;
											//System.out.println("XLOGP: 65		 0.427");
										}
									}
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 1) {
										if (getAtomTypeXCount(ac, atoms[i]) == 0) {
											xlogP += 0.536;
											//System.out.println("XLOGP: 63		 0.536");
										}
										if (getAtomTypeXCount(ac, atoms[i]) == 1) {
											xlogP -= 0.597;
											//System.out.println("XLOGP: 64		-0.597");
										}
									}
								}else if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 1) {
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.007;
											//System.out.println("XLOGP: 59		 0.007");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP -= 0.275;
											//System.out.println("XLOGP: 60		-0.275");
										}
									}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.366;
											//System.out.println("XLOGP: 61		 0.366");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP += 0.251;
											//System.out.println("XLOGP: 62		 0.251");
										}
									}
								}
							}
						}else if (bondCount == 3) {
							// N sp3
							if (hsCount == 0) {
								//if (rs.contains(atoms[i])&&ringSize>3) {
								if (atoms[i].getFlag(CDKConstants.ISAROMATIC)|| (rs.contains(atoms[i])&& ((Integer)atoms[i].getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue()>3 && getPiSystemsCount(ac,atoms[i])>=1)){
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										xlogP += 0.881;
										//System.out.println("XLOGP: 51		 0.881");
									} else {
										xlogP -= 0.01;
										//System.out.println("XLOGP: 53		-0.01");
									}
								} else {
									if (getAtomTypeXCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.159;
											//System.out.println("XLOGP: 49		 0.159");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.761;
											//System.out.println("XLOGP: 50		 0.761");
										}
									} else {
										xlogP -= 0.239;
										//System.out.println("XLOGP: 52		-0.239");
									}
								}
							}else if (hsCount == 1) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
//									 like pyrrole
									if (atoms[i].getFlag(CDKConstants.ISAROMATIC)|| (rs.contains(atoms[i])&& ((Integer)atoms[i].getProperty(CDKConstants.PART_OF_RING_OF_SIZE)).intValue()>3 && getPiSystemsCount(ac,atoms[i])>=2)) {
										xlogP += 0.545;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 46		 0.545");										
									} else {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP -= 0.112;
											hBondDonors.add(new Integer(i));
											//System.out.println("XLOGP: 44		-0.112");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.166;
											hBondDonors.add(new Integer(i));
											//System.out.println("XLOGP: 45		 0.166");
										}
									}
								} else {
									if (rs.contains(atoms[i])) {
										xlogP += 0.153;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 48		 0.153");
									} else {
										xlogP += 0.324;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 47		 0.324");
									}
								}
							}else if (hsCount == 2) {
								if (getAtomTypeXCount(ac, atoms[i]) == 0) {
									if (getPiSystemsCount(ac, atoms[i]) == 0) {
										xlogP -= 0.534;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 41		-0.534");
									}
									if (getPiSystemsCount(ac, atoms[i]) == 1) {
										xlogP -= 0.329;
										hBondDonors.add(new Integer(i));
										//System.out.println("XLOGP: 42		-0.329");
									}
									
									if (checkAminoAcid!=0){ checkAminoAcid+=1;}
								} else {
									xlogP -= 1.082;
									hBondDonors.add(new Integer(i));
									//System.out.println("XLOGP: 43		-1.082");
								}
							}
						}
					}
				}
			}
			if (symbol.equals("O")) {
				if (bondCount == 1 && maxBondOrder==2.0) {
					xlogP -= 0.399;
					if (!getPresenceOfHydroxy(ac,atoms[i])){
						hBondAcceptors.add(new Integer(i));
					}
					//System.out.println("XLOGP: 75	A=O	-0.399");
				}else if(bondCount == 1 && hsCount==0 && (getPresenceOfNitro(ac,atoms[i]) || getPresenceOfCarbonil(ac,atoms[i])==1) || getPresenceOfSulfat(ac,atoms[i])){
						xlogP -= 0.399;
						if (!getPresenceOfHydroxy(ac,atoms[i])){
							hBondAcceptors.add(new Integer(i));
						}
						//System.out.println("XLOGP: 75	A=O	-0.399");					
				}else if (bondCount >= 1) {
					if (hsCount == 0 && bondCount==2) {
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.084;
								//System.out.println("XLOGP: 72	R-O-R	 0.084");
							}
							if (getPiSystemsCount(ac, atoms[i]) > 0) {
								xlogP += 0.435;
								//System.out.println("XLOGP: 73	R-O-R.1	 0.435");
							}
						}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
							xlogP += 0.105;
							//System.out.println("XLOGP: 74	R-O-X	 0.105");
						}
					}else{
						if (getAtomTypeXCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.467;
								hBondDonors.add(new Integer(i));
								hBondAcceptors.add(new Integer(i));
								//System.out.println("XLOGP: 69	R-OH	-0.467");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.082;
								hBondDonors.add(new Integer(i));
								hBondAcceptors.add(new Integer(i));
								//System.out.println("XLOGP: 70	R-OH.1	 0.082");
							}
						}else if (getAtomTypeXCount(ac, atoms[i]) == 1) {
							xlogP -= 0.522;
							hBondDonors.add(new Integer(i));
							hBondAcceptors.add(new Integer(i));
							//System.out.println("XLOGP: 71	X-OH	-0.522");
						}
					}
				}
			}
			if (symbol.equals("S")) {
				if ((bondCount == 1 && maxBondOrder==2) || (bondCount == 1 && atoms[i].getFormalCharge()==-1)) {
					xlogP -= 0.148;
					//System.out.println("XLOGP: 78	A=S	-0.148");
				}else if (bondCount == 2) {
					if (hsCount == 0) {
						xlogP += 0.255;
						//System.out.println("XLOGP: 77	A-S-A	 0.255");
					} else {
						xlogP += 0.419;
						//System.out.println("XLOGP: 76	A-SH	 0.419");
					}
				}else if (bondCount == 3) {
					if (getOxygenCount(ac, atoms[i]) >= 1) {
						xlogP -= 1.375;
						//System.out.println("XLOGP: 79	A-SO-A	-1.375");
					}
				}else if (bondCount == 4) {
					if (getDoubleBondedOxygenCount(ac, atoms[i]) >= 2) {
						xlogP -= 0.168;
						//System.out.println("XLOGP: 80	A-SO2-A	-0.168");
					}
				}
			}
			if (symbol.equals("P")) {
				if (getDoubleBondedSulfurCount(ac, atoms[i]) >= 1 && bondCount>=4) {
					xlogP += 1.253;
					//System.out.println("XLOGP: 82	S=PA3	 1.253");
				}else if (getOxygenCount(ac,atoms[i])>=1 || getDoubleBondedOxygenCount(ac, atoms[i]) == 1 && bondCount>=4) {
					xlogP -= 0.447;
					//System.out.println("XLOGP: 81	O=PA3	-0.447");
				}
			}
			if (symbol.equals("F")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.375;
					//System.out.println("XLOGP: 83	F.0	 0.512");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.202;
					//System.out.println("XLOGP: 84	F.1	 0.202");
				}
			}
			if (symbol.equals("Cl")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.512;
					//System.out.println("XLOGP: 85	Cl.0	 0.512");
				}else if (getPiSystemsCount(ac, atoms[i]) >= 1) {
					xlogP += 0.663;
					//System.out.println("XLOGP: 86	Cl.1	 0.663");
				}
			}
			if (symbol.equals("Br")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.85;
					//System.out.println("XLOGP: 87	Br.0	 0.85");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.839;
					//System.out.println("XLOGP: 88	Br.1	 0.839");
				}
			}
			if (symbol.equals("I")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 1.05;
					//System.out.println("XLOGP: 89	I.0	 1.05");
				}else if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 1.109;
					//System.out.println("XLOGP: 90	I.1	 1.109");
				}
			}
			
//			Halogen pair 1-3
			int halcount=getHalogenCount(ac, atoms[i]);
			if ( halcount== 2) {
				xlogP += 0.137;
				//System.out.println("XLOGP: Halogen 1-3 pair	 0.137");
			}else if (halcount==3){
				xlogP += (3*0.137);
				//System.out.println("XLOGP: Halogen 1-3 pair	 0.411");
			}else if (halcount==4){
				xlogP += (6*0.137);
				//System.out.println("XLOGP: Halogen 1-3 pair	 1.902");
			}
			
//			sp2 Oxygen 1-5 pair
			if (getPresenceOfCarbonil(ac, atoms[i]) == 2) {// sp2 oxygen 1-5 pair
				if(!rs.contains(atoms[i])) { 
					xlogP += 0.580;
					//System.out.println("XLOGP: sp2 Oxygen 1-5 pair	 0.580");
				}
			}
		}
		//System.out.println("XLOGP: Before Correction:"+xlogP);
		List path=null;
		SimpleGraph moleculeGraph=null;
		int [][] pairCheck=null;
//		//System.out.println("Acceptors:"+hBondAcceptors.size()+" Donors:"+hBondDonors.size());
		if (hBondAcceptors.size()>0 && hBondDonors.size()>0){
			moleculeGraph=MoleculeGraphs.getMoleculeGraph(ac);
			pairCheck=initializeHydrogenPairCheck(new int[atoms.length][atoms.length]);
		}
		for (int i=0; i<hBondAcceptors.size();i++){
			for (int j=0; j<hBondDonors.size();j++){
				if (checkRingLink(rs,ac,atoms[((Integer)hBondAcceptors.get(i)).intValue()]) || checkRingLink(rs,ac,atoms[((Integer)hBondDonors.get(j)).intValue()])){
					path=BFSShortestPath.findPathBetween(moleculeGraph,atoms[((Integer)hBondAcceptors.get(i)).intValue()], atoms[((Integer)hBondDonors.get(j)).intValue()]);
//					//System.out.println(" Acc:"+checkRingLink(rs,ac,atoms[((Integer)hBondAcceptors.get(i)).intValue()])
//										+" S:"+atoms[((Integer)hBondAcceptors.get(i)).intValue()].getSymbol()
//										+" Nr:"+((Integer)hBondAcceptors.get(i)).intValue()
//										+" Don:"+checkRingLink(rs,ac,atoms[((Integer)hBondDonors.get(j)).intValue()])
//										+" S:"+atoms[((Integer)hBondDonors.get(j)).intValue()].getSymbol()
//										+" Nr:"+((Integer)hBondDonors.get(j)).intValue()
//										+" i:"+i+" j:"+j+" path:"+path.size());
					if (checkRingLink(rs,ac,atoms[((Integer)hBondAcceptors.get(i)).intValue()]) && checkRingLink(rs,ac,atoms[((Integer)hBondDonors.get(j)).intValue()])){
						if (path.size()==3 && pairCheck[((Integer)hBondAcceptors.get(i)).intValue()][((Integer)hBondDonors.get(j)).intValue()]==0){
							xlogP += 0.429;
							pairCheck[((Integer)hBondAcceptors.get(i)).intValue()][((Integer)hBondDonors.get(j)).intValue()]=1;
							pairCheck[((Integer)hBondDonors.get(j)).intValue()][((Integer)hBondAcceptors.get(i)).intValue()]=1;
							//System.out.println("XLOGP: Internal HBonds 1-4	 0.429");
						}
					}else{
						if (path.size()==4 && pairCheck[((Integer)hBondAcceptors.get(i)).intValue()][((Integer)hBondDonors.get(j)).intValue()]==0){
							xlogP += 0.429;
							pairCheck[((Integer)hBondAcceptors.get(i)).intValue()][((Integer)hBondDonors.get(j)).intValue()]=1;
							pairCheck[((Integer)hBondDonors.get(j)).intValue()][((Integer)hBondAcceptors.get(i)).intValue()]=1;
							//System.out.println("XLOGP: Internal HBonds 1-5	 0.429");
						}
					}
				}	
			}
		}
		
		if (checkAminoAcid>1){
//			 alpha amino acid
			QueryAtomContainer aminoAcid=QueryAtomContainerCreator.createBasicQueryContainer(sp.parseSmiles("NCC(=O)O"));
			Bond [] bonds=aminoAcid.getBonds();
			IAtom[] bondAtoms=null;
			for (int i=0;i<bonds.length;i++){
				bondAtoms=bonds[i].getAtoms();
				if ((bondAtoms[0].getSymbol().equals("C") && bondAtoms[1].getSymbol().equals("N")) || (bondAtoms[0].getSymbol().equals("N") && bondAtoms[1].getSymbol().equals("C"))&& bonds[i].getOrder()==1){
					aminoAcid.removeBond(bondAtoms[0],bondAtoms[1]);
					aminoAcid.addBond(new AnyOrderQueryBond((QueryAtom)bondAtoms[0],(QueryAtom)bondAtoms[1],1));
					break;
				}
			}
			
			//AtomContainer aminoacid = sp.parseSmiles("NCC(=O)O");
			if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, aminoAcid)) {
				List list = UniversalIsomorphismTester.getSubgraphAtomsMap((org.openscience.cdk.AtomContainer)ac, aminoAcid);
				RMap map = null;
				IAtom atom1=null;
				for (int j = 0; j < list.size(); j++){
					map = (RMap) list.get(j);
					atom1 = ac.getAtomAt(map.getId1());
					if (atom1.getSymbol().equals("O")&& ac.getMaximumBondOrder(atom1)==1){
						if (ac.getBondCount(atom1)==2 && getHydrogenCount(ac, atom1)==0){
						}else{
							xlogP -= 2.166;
							//System.out.println("XLOGP: alpha amino acid	-2.166");
							break;
						}
					}
				}
			}
		}
		
		IAtomContainer paba = sp.parseSmiles("CS(=O)(=O)c1ccc(N)cc1");
		// p-amino sulphonic acid
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, paba)) {
			xlogP -= 0.501;
			//System.out.println("XLOGP: p-amino sulphonic acid	-0.501");
		}

		
		// salicylic acid
		if (salicylFlag){
			IAtomContainer salicilic = sp.parseSmiles("O=C(O)c1ccccc1O");
			if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, salicilic)) {
				xlogP += 0.554;
				//System.out.println("XLOGP: salicylic acid	 0.554");
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
		orthopair.addBond(new AromaticQueryBond(atom1,atom2,1.5));
		orthopair.addBond(new OrderQueryBond(atom1,atom3,1));
		orthopair.addBond(new OrderQueryBond(atom2,atom4,1));
		
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, orthopair)) {
			xlogP -= 0.268;
			//System.out.println("XLOGP: Ortho oxygen pair	-0.268");
		}

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(xlogP));
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
	private boolean checkRingLink(org.openscience.cdk.interfaces.RingSet ringSet, org.openscience.cdk.interfaces.IAtomContainer ac, org.openscience.cdk.interfaces.IAtom atom) {
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		if (ringSet.contains(atom)){
			return true;
		}
		for (int i = 0; i < neighbours.length; i++) {
			if (ringSet.contains(neighbours[i])) {
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
		org.openscience.cdk.interfaces.IAtom[] neighboors = ac.getConnectedAtoms(atom);
		int hcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("H")) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int acounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("F") || neighbours[i].getSymbol().equals("I") || neighbours[i].getSymbol().equals("Cl") || neighbours[i].getSymbol().equals("Br")) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int nocounter = 0;
		Bond bond=null;
		for (int i = 0; i < neighbours.length; i++) {
			if ((neighbours[i].getSymbol().equals("N") || neighbours[i].getSymbol().equals("O")) && !((Boolean)neighbours[i].getProperty("IS_IN_AROMATIC_RING")).booleanValue()) {
				//if (ac.getMaximumBondOrder(neighbours[i]) == 1.0) {
				bond = ac.getBond(neighbours[i], atom);
				if (bond.getOrder() != 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int carocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C") && neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int ccounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int ocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("O")) {
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int cdbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				bond = ac.getBond(neighbours[i], atom);
				if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int odbcounter = 0;
		boolean chargeFlag=false;
		if (atom.getFormalCharge()>=1){
			chargeFlag=true;
		}
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("O")) {
				bond = ac.getBond(neighbours[i], atom);
				if (chargeFlag && neighbours[i].getFormalCharge()==-1 && bond.getOrder() ==1){
					odbcounter += 1;
				}
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int sdbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("S")) {
				if (atom.getFormalCharge()==1 && neighbours[i].getFormalCharge()==-1){
					sdbcounter+=1;
				}
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int ndbcounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N")) {
				bond = ac.getBond(neighbours[i], atom);
				if (!neighbours[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int narocounter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N") && ((Boolean)neighbours[i].getProperty("IS_IN_AROMATIC_RING")).booleanValue()) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		int picounter = 0;
		org.openscience.cdk.interfaces.Bond[] bonds=null;
		for (int i = 0; i < neighbours.length; i++) {
			bonds = ac.getConnectedBonds(neighbours[i]);
			for (int j = 0; j < bonds.length; j++) {
				if (bonds[j].getOrder()>1.0 && bonds[j].getConnectedAtom(neighbours[i])!=atom 
						&& !neighbours[i].getSymbol().equals("P")
						&& !neighbours[i].getSymbol().equals("S")){
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.IAtom[] first = null;
		if (neighbours[0].getSymbol().equals("C")) {
			first = ac.getConnectedAtoms(neighbours[0]);
			for (int i = 0; i < first.length; i++) {
				if (first[i].getSymbol().equals("O")) {
					if(ac.getBond(neighbours[0], first[i]).getOrder()==1){
						if (ac.getBondCount(first[i])>1 && getHydrogenCount(ac,first[i])==0){
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.IAtom[] second = null;
		Bond bond = null;
		//int counter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("N")) {
				second = ac.getConnectedAtoms(neighbours[i]);
				for (int b = 0; b < second.length; b++) {
					if (second[b].getSymbol().equals("O")) {
						bond = ac.getBond(neighbours[i], second[b]);
						if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.IAtom[] second = null;
		Bond bond = null;
		//int counter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("S") && getOxygenCount(ac,neighbours[i])>=2 && ac.getBondCount(neighbours[i])==4){
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
		org.openscience.cdk.interfaces.IAtom[] neighbours = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.IAtom[] second = null;
		Bond bond = null;
		int counter = 0;
		for (int i = 0; i < neighbours.length; i++) {
			if (neighbours[i].getSymbol().equals("C")) {
				second = ac.getConnectedAtoms(neighbours[i]);
				for (int b = 0; b < second.length; b++) {
					if (second[b].getSymbol().equals("O")) {
						bond = ac.getBond(neighbours[i], second[b]);
						if (bond.getOrder() == 2.0) {
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
		org.openscience.cdk.interfaces.IAtom[] first = ac.getConnectedAtoms(atom);
		org.openscience.cdk.interfaces.IAtom[] second = null;
		org.openscience.cdk.interfaces.IAtom[] third = null;
		org.openscience.cdk.interfaces.IAtom[] fourth = null;
		if (first.length > 0) {
			for (int i = 0; i < first.length; i++) {
				if (first[i].getSymbol().equals("C") || first[i].getSymbol().equals("H")) {
				} else {
					return false;
				}
				second = ac.getConnectedAtoms(first[i]);
				if (second.length > 0) {
					for (int b = 0; b < second.length; b++) {
						if (second[b].getSymbol().equals("C") || second[b].getSymbol().equals("H")) {
						} else {
							return false;
						}
						third = ac.getConnectedAtoms(second[b]);
						if (third.length > 0) {
							for (int c = 0; c < third.length; c++) {
								if (third[c].getSymbol().equals("C") || third[c].getSymbol().equals("H")) {
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
            return new Boolean(true);
	}
}


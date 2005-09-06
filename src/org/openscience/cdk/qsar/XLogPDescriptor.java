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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Prediction of logP based on the atom-type method called XLogP. For
 * description of the methodology see Ref. @cdk.cite{WANG97}
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
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:xlogP
 */
public class XLogPDescriptor implements Descriptor {
    
	private boolean checkAromaticity = false;

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
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:xlogP",
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
		if (params.length > 1) {
			throw new CDKException("XLogPDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The first parameter must be of type Boolean");
		}
		// ok, all should be fine
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the XLogPDescriptor object.
	 *
	 *@return    The parameters value
         *@see #setParameters
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Boolean(checkAromaticity);
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
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		RingSet rs = (new AllRingsFinder()).findAllRings(ac);
		HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
		double xlogP = 0;
		org.openscience.cdk.interfaces.Atom[] atoms = ac.getAtoms();
		String symbol = "";
		int bondCount = 0;
		int hsCount = 0;
		double maxBondOrder = 0;
		for (int i = 0; i < atoms.length; i++) {
			symbol = atoms[i].getSymbol();
			bondCount = ac.getBondCount(atoms[i]);
			hsCount = getHydrogenCount(ac, atoms[i]);
			maxBondOrder = ac.getMaximumBondOrder(atoms[i]);
			if (symbol.equals("C")) {
				if (bondCount == 2) {
					// C sp
					if (hsCount >= 1) {
						xlogP += 0.209;
						// // System.out...println("XLOGP: 1");
					} else {
						if (maxBondOrder == 2.0) {
							xlogP += 2.073;
							// // System.out...println("XLOGP: 2");
						} else if (maxBondOrder == 3.0) {
							xlogP += 0.33;
							// // System.out...println("XLOGP: 3");
						}
					}
				}
				if (bondCount == 3) {
					// C sp2
					if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
						if (getAromaticCarbonsCount(ac, atoms[i]) == 2) {
							if (hsCount == 0) {
								if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
									xlogP += 0.296;
									// // System.out...println("XLOGP: 4");
								} else {
									xlogP -= 0.151;
									// // System.out...println("XLOGP: 5");
								}
							} else {
								xlogP += 0.337;
								// // System.out...println("XLOGP: 6");
							}
						} else if (getAromaticCarbonsCount(ac, atoms[i]) < 2 && getAromaticNitrogensCount(ac, atoms[i]) > 1) {
							if (hsCount == 0) {
								if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
									xlogP += 0.174;
									// // System.out...println("XLOGP: 7");
								} else {
									xlogP += 0.366;
									// // System.out...println("XLOGP: 8");
								}
							} else if (getHydrogenCount(ac, atoms[i]) == 1) {
								xlogP += 0.126;
								// // System.out...println("XLOGP: 9");
							}
						}
					} else {
						if (hsCount == 0) {
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.05;
									// // System.out...println("XLOGP: 10");
								} else {
									xlogP += 0.013;
									// // System.out...println("XLOGP: 11");
								}
							}
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP -= 0.03;
									// // System.out...println("XLOGP: 12");
								} else {
									xlogP -= 0.027;
									// // System.out...println("XLOGP: 13");
								}
							}
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 2) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.005;
									// // System.out...println("XLOGP: 14");
								} else {
									xlogP -= 0.315;
									// // System.out...println("XLOGP: 15");
								}
							}
						}
						if (hsCount == 1) {
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.466;
									// // System.out...println("XLOGP: 16");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP += 0.136;
									// // System.out...println("XLOGP: 17");
								}
							} else {
								if (getPiSystemsCount(ac, atoms[i]) == 0) {
									xlogP += 0.001;
									// // System.out...println("XLOGP: 18");
								}
								if (getPiSystemsCount(ac, atoms[i]) == 1) {
									xlogP -= 0.31;
									// // System.out...println("XLOGP: 19");
								}
							}
						}
						if (hsCount == 2) {
							xlogP += 0.42;
							// // System.out...println("XLOGP: 20");
						}
					}
					if (getIfCarbonIsHydrophobic(ac, atoms[i]) >= 0) {
						xlogP += 0.211;
						// // System.out...println("XLOGP: 21");
					}
				}
				if (bondCount == 4) {
					// C sp3
					if (getIfCarbonIsHydrophobic(ac, atoms[i]) >= 0) {
						xlogP += 0.211;
						// // System.out...println("XLOGP: 22");
					}
					if (hsCount == 0) {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.006;
								// // System.out...println("XLOGP: 23");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.57;
								// // System.out...println("XLOGP: 24");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.317;
								// // System.out...println("XLOGP: 25");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.316;
								// // System.out...println("XLOGP: 26");
							} else {
								xlogP -= 0.723;
								// // System.out...println("XLOGP: 27");
							}
						}
					}
					if (hsCount == 1) {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.127;
								// // System.out...println("XLOGP: 28");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.243;
								// // System.out...println("XLOGP: 29");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.499;
								// // System.out...println("XLOGP: 30");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.205;
								// // System.out...println("XLOGP: 31");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.305;
								// // System.out...println("XLOGP: 32");
							}
							if (getPiSystemsCount(ac, atoms[i]) >= 2) {
								xlogP -= 0.709;
								// // System.out...println("XLOGP: 33");
							}
						}
					}
					if (hsCount == 2) {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.358;
								// // System.out...println("XLOGP: 34");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.008;
								// // System.out...println("XLOGP: 35");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.185;
								// // System.out...println("XLOGP: 36");
							}
						} else {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.137;
								// // System.out...println("XLOGP: 37");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.303;
								// // System.out...println("XLOGP: 38");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 2) {
								xlogP -= 0.815;
								// // System.out...println("XLOGP: 39");
							}
						}
					}
					if (hsCount > 2) {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.528;
								// // System.out...println("XLOGP: 40");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.267;
								// // System.out...println("XLOGP: 41");
							}
						}
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
							xlogP -= 0.032;
							// // System.out...println("XLOGP: 42");
						}
					}
				}
			}
			if (symbol.equals("N")) {
				if (ac.getBondOrderSum(atoms[i]) == 5.0) {
					xlogP += 1.178;
					// // System.out...println("XLOGP: 43");
				}
				// NO2
				else {
					if (getPresenceOfCarbonil(ac, atoms[i]) == 1) {
						// amidic nitrogen
						if (hsCount == 0) {
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
								xlogP += 0.078;
								// // System.out...println("XLOGP: 44");
							}
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
								xlogP -= 0.118;
								// // System.out...println("XLOGP: 45");
							}
						}
						if (hsCount == 1) {
							if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
								xlogP -= 0.096;
								// // System.out...println("XLOGP: 46");
							} else {
								xlogP -= 0.044;
								// // System.out...println("XLOGP: 47");
							}
						}
						if (hsCount == 2) {
							xlogP -= 0.646;
							// // System.out...println("XLOGP: 48");
						}
					} else {
						if (bondCount == 1) {
							// -C#N
							if (getCarbonsCount(ac, atoms[i]) == 1) {
								xlogP -= 0.566;
								// // System.out...println("XLOGP: 49");
							}
						}
						if (bondCount == 2) {
							// N sp2
							if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
								xlogP -= 0.493;
								// // System.out...println("XLOGP: 50");
							} else {
								if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 0) {
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 0) {
										if (getDoubleBondedOxygenCount(ac, atoms[i]) == 1) {
											xlogP += 0.427;
											// // System.out...println("XLOGP: 51");
										}
									}
									if (getDoubleBondedNitrogenCount(ac, atoms[i]) == 1) {
										if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
											xlogP += 0.536;
											// // System.out...println("XLOGP: 52");
										}
										if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
											xlogP -= 0.597;
											// // System.out...println("XLOGP: 53");
										}
									}
								}
								if (getDoubleBondedCarbonsCount(ac, atoms[i]) == 1) {
									if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.007;
											// // System.out...println("XLOGP: 54");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP -= 0.275;
											// // System.out...println("XLOGP: 55");
										}
									}
									if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.366;
											// // System.out...println("XLOGP: 56");
										}
										if (getPiSystemsCount(ac, atoms[i]) == 1) {
											xlogP += 0.251;
											// // System.out...println("XLOGP: 57");
										}
									}
								}
							}
						}
						if (bondCount == 3) {
							// N sp3
							if (hsCount == 0) {
								if (rs.contains(atoms[i])) {
									if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
										xlogP += 0.881;
										// // System.out...println("XLOGP: 58");
									} else {
										xlogP -= 0.01;
										// // System.out...println("XLOGP: 59");
									}
								} else {
									if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP += 0.159;
											// // System.out...println("XLOGP: 60");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.761;
											// // System.out...println("XLOGP: 61");
										}
									} else {
										xlogP -= 0.239;
									}
								}
							}
							if (hsCount == 1) {
								if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
									if (atoms[i].getFlag(CDKConstants.ISAROMATIC)) {
										xlogP += 0.545;
										// // System.out...println("XLOGP: 62");
									}
									// like pyrrole
									else {
										if (getPiSystemsCount(ac, atoms[i]) == 0) {
											xlogP -= 0.112;
											// // System.out...println("XLOGP: 63");
										}
										if (getPiSystemsCount(ac, atoms[i]) > 0) {
											xlogP += 0.166;
											// // System.out...println("XLOGP: 64");
										}
									}
								} else {
									if (rs.contains(atoms[i])) {
										xlogP += 0.153;
										// // System.out...println("XLOGP: 65");
									} else {
										xlogP += 0.324;
										// // System.out...println("XLOGP: 66");
									}
								}
							}
							if (hsCount == 2) {
								if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
									if (getPiSystemsCount(ac, atoms[i]) == 0) {
										xlogP -= 0.534;
										// // System.out...println("XLOGP: 66");
									}
									if (getPiSystemsCount(ac, atoms[i]) == 1) {
										xlogP -= 0.329;
										// // System.out...println("XLOGP: 67");
									}
								} else {
									xlogP -= 1.082;
									// // System.out...println("XLOGP: 68");
								}
							}
						}
					}
				}
			}
			if (symbol.equals("O")) {
				if (bondCount == 1) {
					xlogP -= 0.399;
					// // System.out...println("XLOGP: 69");
				}
				if (bondCount == 2) {
					if (hsCount == 0) {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP += 0.084;
								// // System.out...println("XLOGP: 70");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.435;
								// // System.out...println("XLOGP: 71");
							}
						}
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
							xlogP += 0.105;
							// // System.out...println("XLOGP: 72");
						}
					} else {
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 0) {
							if (getPiSystemsCount(ac, atoms[i]) == 0) {
								xlogP -= 0.467;
								// // System.out...println("XLOGP: 73");
							}
							if (getPiSystemsCount(ac, atoms[i]) == 1) {
								xlogP += 0.082;
								// // System.out...println("XLOGP: 74");
							}
						}
						if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
							xlogP -= 0.522;
							// // System.out...println("XLOGP: 75");
						}
					}
				}
			}
			if (symbol.equals("S")) {
				if (bondCount == 1) {
					xlogP -= 0.148;
					// // System.out...println("XLOGP: 76");
				}
				if (bondCount == 2) {
					if (hsCount == 0) {
						xlogP += 0.255;
						// // System.out...println("XLOGP: 77");
					} else {
						xlogP += 0.419;
						// // System.out...println("XLOGP: 78");
					}
				}
				if (bondCount == 3) {
					if (getNitrogenOrOxygenCount(ac, atoms[i]) == 1) {
						xlogP -= 1.375;
						// // System.out...println("XLOGP: 79");
					}
				}
				if (bondCount == 4) {
					if (getNitrogenOrOxygenCount(ac, atoms[i]) == 2) {
						xlogP -= 0.168;
						// // System.out...println("XLOGP: 80");
					}
				}
			}
			if (symbol.equals("P")) {
				if (getDoubleBondedSulfurCount(ac, atoms[i]) == 1) {
					xlogP += 1.253;
					// // System.out...println("XLOGP: 81");
				}
				if (getDoubleBondedOxygenCount(ac, atoms[i]) == 1) {
					xlogP -= 0.477;
					// // System.out...println("XLOGP: 82");
				}
			}
			if (symbol.equals("F")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.375;
					// // System.out...println("XLOGP: 84");
				}
				if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.202;
					// // System.out...println("XLOGP: 85");
				}
			}
			if (symbol.equals("Cl")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.512;
					// // System.out...println("XLOGP: 86");
				}
				if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.663;
					// // System.out...println("XLOGP: 87");
				}
			}
			if (symbol.equals("Br")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 0.85;
					// // System.out...println("XLOGP: 88");
				}
				if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 0.839;
					// // System.out...println("XLOGP: 89");
				}
			}
			if (symbol.equals("I")) {
				if (getPiSystemsCount(ac, atoms[i]) == 0) {
					xlogP += 1.05;
					// // System.out...println("XLOGP: 90");
				}
				if (getPiSystemsCount(ac, atoms[i]) == 1) {
					xlogP += 1.109;
					// // System.out...println("XLOGP: 91");
				}
			}
			if (getAlogenCount(ac, atoms[i]) >= 2) {
				xlogP += 0.137;
				// // System.out...println("XLOGP: 92");
			}
			// more than 2 alogens on the same atom
			
			
			if (getPresenceOfCarbonil(ac, atoms[i]) == 2) {// sp2 oxygen 1-5 pair
				if(!rs.contains(atoms[i])) { xlogP += 0.580; }
			}
		}
		Descriptor acc = new HBondAcceptorCountDescriptor();
		Object[] paramsAcc = {new Boolean(false)};
		acc.setParameters(paramsAcc);
		Descriptor don = new HBondDonorCountDescriptor();
		Object[] paramsDon = {new Boolean(false)};
		don.setParameters(paramsDon);
		int acceptors = ((IntegerResult) acc.calculate(ac).getValue()).intValue();
		int donors = ((IntegerResult) don.calculate(ac).getValue()).intValue();
		if (donors > 0 && acceptors > 0) {
			xlogP += 0.429;
			// internal H-bonds
		}

		SmilesParser sp = new SmilesParser();
		AtomContainer paba = sp.parseSmiles("CS(=O)(=O)c1ccc(N)cc1");
		// p-amino sulphonic acid
		QueryAtomContainer pabaQuery = QueryAtomContainerCreator.createBasicQueryContainer(paba);
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, pabaQuery)) {
			xlogP -= 0.501;
		}

		AtomContainer aminoacid = sp.parseSmiles("NC=O");
		// alpha amino acid
		QueryAtomContainer aminoacidquery = QueryAtomContainerCreator.createBasicQueryContainer(aminoacid);
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, aminoacidquery)) {
			xlogP -= 2.166;
		}

		AtomContainer salicilic = sp.parseSmiles("O=C(O)c1ccccc1O");
		// salicylic acid
		QueryAtomContainer salicilicquery = QueryAtomContainerCreator.createBasicQueryContainer(salicilic);
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, salicilicquery)) {
			xlogP += 0.554;
		}

		AtomContainer orthopair = sp.parseSmiles("OCCO");
		// ortho oxygen pair
		QueryAtomContainer orthopairquery = QueryAtomContainerCreator.createBasicQueryContainer(orthopair);
		if (UniversalIsomorphismTester.isSubgraph((org.openscience.cdk.AtomContainer)ac, orthopairquery)) {
			xlogP -= 0.268;
		}

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(xlogP));
	}


	/**
	 *  Gets the hydrogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The hydrogenCount value
	 */
	private int getHydrogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int hcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("H")) {
				hcounter += 1;
			}
		}
		return hcounter;
	}


	/**
	 *  Gets the alogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The alogenCount value
	 */
	private int getAlogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int acounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("F") || neighboors[i].getSymbol().equals("I") || neighboors[i].getSymbol().equals("Cl") || neighboors[i].getSymbol().equals("Br")) {
				acounter += 1;
			}
		}
		return acounter;
	}


	/**
	 *  Gets the nitrogenOrOxygenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The nitrogenOrOxygenCount value
	 */
	private int getNitrogenOrOxygenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int nocounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("N") || neighboors[i].getSymbol().equals("O")) {
				if (ac.getMaximumBondOrder(neighboors[i]) == 1.0) {
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
	private int getAromaticCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int carocounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("C")) {
				if (neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
					carocounter += 1;
				}
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
	private int getCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int ccounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("C")) {
				if (!neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
					ccounter += 1;
				}
			}
		}
		return ccounter;
	}


	/**
	 *  Gets the doubleBondedCarbonsCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedCarbonsCount value
	 */
	private int getDoubleBondedCarbonsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int cdbcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("C")) {
				bond = ac.getBond(neighboors[i], atom);
				if (!neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						cdbcounter += 1;
					}
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
	private int getDoubleBondedOxygenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int odbcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("O")) {
				bond = ac.getBond(neighboors[i], atom);
				if (!neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
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
	private int getDoubleBondedSulfurCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int odbcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("S")) {
				bond = ac.getBond(neighboors[i], atom);
				if (!neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
					if (bond.getOrder() == 2.0) {
						odbcounter += 1;
					}
				}
			}
		}
		return odbcounter;
	}


	/**
	 *  Gets the doubleBondedNitrogenCount attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The doubleBondedNitrogenCount value
	 */
	private int getDoubleBondedNitrogenCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		Bond bond = null;
		int ndbcounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("N")) {
				bond = ac.getBond(neighboors[i], atom);
				if (!neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
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
	private int getAromaticNitrogensCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int narocounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("N")) {
				if (neighboors[i].getFlag(CDKConstants.ISAROMATIC)) {
					narocounter += 1;
				}
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
	private int getPiSystemsCount(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		int picounter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (ac.getMaximumBondOrder(neighboors[i]) > 1.0) {
				picounter += 1;
			}
		}
		return picounter;
	}


	/**
	 *  Gets the presenceOfCarbonil attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The presenceOfCarbonil value
	 */
	private int getPresenceOfCarbonil(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] neighboors = ac.getConnectedAtoms(atom);
		Atom[] second = null;
		Bond bond = null;
		int counter = 0;
		for (int i = 0; i < neighboors.length; i++) {
			if (neighboors[i].getSymbol().equals("C")) {
				second = ac.getConnectedAtoms(neighboors[i]);
				for (int b = 0; b < second.length; b++) {
					if (second[b].getSymbol().equals("O")) {
						bond = ac.getBond(neighboors[i], second[b]);
						if (bond.getOrder() == 2.0) {
							counter += 1;
						}
					}
				}
			}
		}
		return counter;
	}


	// C must be sp2 or sp3
	// and, for all distances C-1-2-3-4 only C atoms are permitted
	/**
	 *  Gets the ifCarbonIsHydrophobic attribute of the XLogPDescriptor object.
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The ifCarbonIsHydrophobic value
	 */
	private int getIfCarbonIsHydrophobic(AtomContainer ac, org.openscience.cdk.interfaces.Atom atom) {
		Atom[] first = ac.getConnectedAtoms(atom);
		Atom[] second = null;
		Atom[] third = null;
		Atom[] fourth = null;
		int presence = 0;
		if (first.length > 0) {
			for (int i = 0; i < first.length; i++) {
				if (first[i].getSymbol().equals("C") || first[i].getSymbol().equals("H")) {
					presence += 0;
				} else {
					presence -= 1;
					break;
				}
				second = ac.getConnectedAtoms(first[i]);
				if (second.length > 0) {
					for (int b = 0; b < second.length; b++) {
						if (second[b].getSymbol().equals("C") || second[b].getSymbol().equals("H")) {
							presence += 0;
						} else {
							presence -= 1;
							break;
						}
						third = ac.getConnectedAtoms(second[b]);
						if (third.length > 0) {
							for (int c = 0; c < third.length; c++) {
								if (third[c].getSymbol().equals("C") || third[c].getSymbol().equals("H")) {
									presence += 0;
								} else {
									presence -= 1;
									break;
								}
								fourth = ac.getConnectedAtoms(third[c]);
								if (fourth.length > 0) {
									for (int d = 0; d < fourth.length; d++) {
										if (fourth[d].getSymbol().equals("C") || fourth[d].getSymbol().equals("H")) {
											presence += 0;
										} else {
											presence -= 1;
											break;
										}
									}
								} else {
									presence -= 1;
								}
							}
						} else {
							presence -= 1;
						}
					}
				} else {
					presence -= 1;
				}
			}
		} else {
			presence -= 1;
		}
		return presence;
	}



	/**
	 *  Gets the parameterNames attribute of the XLogPDescriptor object.
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "checkAromaticity";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the XLogPDescriptor object.
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Boolean(true);
		return paramTypes;
	}
}


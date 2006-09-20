/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005-2006  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.charges;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * <p>The calculation of the Gasteiger Marsili (PEOE) partial charges is based on 
 * {@cdk.cite GM80}. This class only implements the original method which only
 * applies to &sigma;-bond systems.</p> 
 * 
 *
 * @author      chhoppe
 * @author      rojas
 * 
 * @cdk.module  charges
 * @cdk.created 2004-11-03
 * @cdk.keyword partial atomic charges
 * @cdk.keyword charge distribution
 * @cdk.keyword electronegativities, partial equalization of orbital
 * @cdk.keyword PEOE
 */
public class GasteigerMarsiliPartialCharges {

	private double DEOC_HYDROGEN = 20.02;
	private double MX_DAMP = 0.5;
	private double MX_ITERATIONS = 20;
	private int STEP_SIZE = 5;
	private AtomTypeCharges atomTypeCharges = new AtomTypeCharges();
	/** Flag is set if the formal charge of a chemobject is changed due to resonance.*/

	
	/**
	 *  Constructor for the GasteigerMarsiliPartialCharges object
	 */
	public GasteigerMarsiliPartialCharges() { }
	
	
	/**
	*  Sets chi_cat value for hydrogen, because H poses a special problem due to lack of possible second ionisation
	 *
	 *@param  chiCat  The new DEOC_HYDROGEN value
	 */
	public void setChiCatHydrogen(double chiCat) {
		DEOC_HYDROGEN = chiCat;
	}


	/**
	 *  Sets the maxGasteigerDamp attribute of the GasteigerMarsiliPartialCharges
	 *  object
	 *
	 *@param  damp  The new maxGasteigerDamp value
	 */
	public void setMaxGasteigerDamp(double damp) {
		MX_DAMP = damp;
	}


	/**
	 *  Sets the maxGasteigerIters attribute of the GasteigerMarsiliPartialCharges
	 *  object
	 *
	 *@param  iters  The new maxGasteigerIters value
	 */
	public void setMaxGasteigerIters(double iters) {
		MX_ITERATIONS = iters;
	}
	
	
	/**
	 *  Main method which assigns Gasteiger Marisili partial sigma charges
	 *
	 *@param  ac             AtomContainer
	 *@param  setCharge      boolean flag to set charge on atoms
	 *@return                AtomContainer with partial charges
	 *@exception  Exception  Possible Exceptions
	 */
	public IAtomContainer assignGasteigerMarsiliSigmaPartialCharges(IAtomContainer ac, boolean setCharge) throws Exception {

//		if (setCharge) {
//			atomTypeCharges.setCharges(ac); // not necessary initial charge
//		}
		/*add the initial charge to 0. According results of Gasteiger*/
		for(int i = 0; i < ac.getAtomCount(); i++)
			ac.getAtom(i).setCharge(0.0);
		double[] gasteigerFactors = assignGasteigerSigmaMarsiliFactors(ac);//a,b,c,deoc,chi,q
		double alpha = 1.0;
		double q;
		double deoc;
		IBond[] bonds = null;
		IAtom[] atoms = null;
		int atom1 = 0;
		int atom2 = 0;
		
		double[] q_old = new double[ac.getAtomCount()];
		for(int i = 0 ; i < q_old.length ; i++)
			q_old[0] = 20.0;
		
		out:
		for (int i = 0; i < MX_ITERATIONS; i++) {
			alpha *= MX_DAMP;
			boolean isDifferent = false;
			for (int j = 0; j < ac.getAtomCount(); j++) {
				q = gasteigerFactors[STEP_SIZE * j + j + 5];
				double difference = Math.abs(q_old[j])-Math.abs(q);
				if(Math.abs(difference) > 0.001)
					isDifferent = true;
				q_old[j] = q;
				
				gasteigerFactors[STEP_SIZE * j + j + 4] = gasteigerFactors[STEP_SIZE * j + j + 2] * q * q + gasteigerFactors[STEP_SIZE * j + j + 1] * q + gasteigerFactors[STEP_SIZE * j + j];
//				System.out.println("g4: "+gasteigerFactors[STEP_SIZE * j + j + 4]);
			}
			if(!isDifferent)/* automatically break the maximum iterations*/
				break out;
			
			bonds = ac.getBonds();
			for (int k = 0; k < bonds.length; k++) {
				atoms = bonds[k].getAtoms();
				atom1 = ac.getAtomNumber(atoms[0]);
				atom2 = ac.getAtomNumber(atoms[1]);

				if (gasteigerFactors[STEP_SIZE * atom1 + atom1 + 4] >= gasteigerFactors[STEP_SIZE * atom2 + atom2 + 4]) {
					if (ac.getAtom(atom2).getSymbol().equals("H")) {
						deoc = DEOC_HYDROGEN;
					} else {
						deoc = gasteigerFactors[STEP_SIZE * atom2 + atom2 + 3];
					}
				} else {
					if (ac.getAtom(atom1).getSymbol().equals("H")) {
						deoc = DEOC_HYDROGEN;
					} else {
						deoc = gasteigerFactors[STEP_SIZE * atom1 + atom1 + 3];
					}
				}

				q = (gasteigerFactors[STEP_SIZE * atom1 + atom1 + 4] - gasteigerFactors[STEP_SIZE * atom2 + atom2 + 4]) / deoc;
//				System.out.println("qq: "+q);
				gasteigerFactors[STEP_SIZE * atom1 + atom1 + 5] -= (q*alpha);
				gasteigerFactors[STEP_SIZE * atom2 + atom2 + 5] += (q*alpha);
			}
		}
		
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtom(i).setCharge(gasteigerFactors[STEP_SIZE * i + i + 5]);
		}
		return ac;
	}
	/**
	 *  Get the StepSize attribute of the GasteigerMarsiliPartialCharges
	 *  object
	 *
	 *@return STEP_SIZE
	 */
	public int getStepSize(){
		return STEP_SIZE;
	}
	
	
	/**
	 *  Method which stores and assigns the factors a,b,c and CHI+
	 *
	 *@param  ac  AtomContainer
	 *@return     Array of doubles [a1,b1,c1,denom1,chi1,q1...an,bn,cn...] 1:Atom 1-n in AtomContainer
	 */
	public double[] assignGasteigerSigmaMarsiliFactors(IAtomContainer ac) {
		//a,b,c,denom,chi,q
		double[] gasteigerFactors = new double[(ac.getAtomCount() * (STEP_SIZE+1))];
		String AtomSymbol = "";
		double[] factors = new double[]{0.0, 0.0, 0.0};
		for (int i = 0; i < ac.getAtomCount(); i++) {
			factors[0] = 0.0;
			factors[1] = 0.0;
			factors[2] = 0.0;
			AtomSymbol = ac.getAtom(i).getSymbol();
			if (AtomSymbol.equals("H")) {
				factors[0] = 7.17;
				factors[1] = 6.24;
				factors[2] = -0.56;
			} else if (AtomSymbol.equals("C")) {
				if ((ac.getMaximumBondOrder(ac.getAtom(i)) == 1)&&
				(ac.getAtom(i).getFormalCharge() != -1)){
					factors[0] = 7.98;
					factors[1] = 9.18;
					factors[2] = 1.88;
				} else if ((ac.getMaximumBondOrder(ac.getAtom(i)) > 1 && ac.getMaximumBondOrder(ac.getAtom(i)) < 3)
						||((ac.getMaximumBondOrder(ac.getAtom(i)) == 1)&& ac.getAtom(i).getFormalCharge() == -1)) {
					factors[0] = 8.79;/*8.79*//*8.81*/
					factors[1] = 9.32;/*9.32*//*9.34*/
					factors[2] = 1.51;/*1.51*//*1.52*/
				} else if (ac.getMaximumBondOrder(ac.getAtom(i)) >= 3) {
					factors[0] = 10.39;/*10.39*/
					factors[1] = 9.45;/*9.45*/
					factors[2] = 0.73;
				}
			} else if(AtomSymbol.equals("N")){
				if((ac.getMaximumBondOrder(ac.getAtom(i)) == 1) &&
				(ac.getAtom(i).getFormalCharge() != -1)) {
					factors[0] = 11.54;
					factors[1] = 10.82;
					factors[2] = 1.36;
				} else if ((ac.getMaximumBondOrder(ac.getAtom(i)) > 1 && ac.getMaximumBondOrder(ac.getAtom(i)) < 3) 
						||((ac.getMaximumBondOrder(ac.getAtom(i)) == 1)&& ac.getAtom(i).getFormalCharge() == -1)) {
					factors[0] = 12.87;
					factors[1] = 11.15;
					factors[2] = 0.85;
				} else if (ac.getMaximumBondOrder(ac.getAtom(i)) >= 3) {
					factors[0] = 17.68;/*15.68*/
					factors[1] = 12.70;/*11.70*/
					factors[2] = -0.27;/*-0.27*/
				}
			} else if (AtomSymbol.equals("O")) {
				if ((ac.getMaximumBondOrder(ac.getAtom(i)) == 1) &&
				(ac.getAtom(i).getFormalCharge() != -1)){
					factors[0] = 14.18;
					factors[1] = 12.92;
					factors[2] = 1.39;
				} else if((ac.getMaximumBondOrder(ac.getAtom(i)) > 1 && ac.getMaximumBondOrder(ac.getAtom(i)) < 3)
					||((ac.getMaximumBondOrder(ac.getAtom(i)) == 1)&& ac.getAtom(i).getFormalCharge() == -1)){
					factors[0] = 17.07;/* paramaters aren'T correct parametrized. */
					factors[1] = 13.79;
					factors[2] = 0.47;/*0.47*/
				}
			} else if (AtomSymbol.equals("P")) {
				factors[0] = 8.90;
				factors[1] = 8.32;
				factors[2] = 1.58;
			} else if (AtomSymbol.equals("S") /*&& ac.getMaximumBondOrder(ac.getAtomAt(i)) == 1*/) {
				factors[0] = 10.14;/*10.14*/
				factors[1] = 9.13;/*9.13*/
				factors[2] = 1.38;/*1.38*/
			} else if (AtomSymbol.equals("F")) {
				factors[0] = 14.66;
				factors[1] = 13.85;
				factors[2] = 2.31;
			} else if (AtomSymbol.equals("Cl")) {
				factors[0] = 12.31;/*11.0*//*12.31*/
				factors[1] = 10.84;/*9.69*//*10.84*/
				factors[2] = 1.512;/*1.35*//*1.512*/
			} else if (AtomSymbol.equals("Br")) {
				factors[0] = 11.44;/*10.08*//*11.2*/
				factors[1] = 9.63;/*8.47*//*9.4*/
				factors[2] = 1.31;/*1.16*//*1.29*/
			} else if (AtomSymbol.equals("I")) {
				factors[0] = 9.88;/*9.90*/
				factors[1] = 7.95;/*7.96*/
				factors[2] = 0.945;/*0.96*/
			}

			gasteigerFactors[STEP_SIZE * i + i] = factors[0];
			gasteigerFactors[STEP_SIZE * i + i + 1] = factors[1];
			gasteigerFactors[STEP_SIZE * i + i + 2] = factors[2];
			gasteigerFactors[STEP_SIZE * i + i + 5] = ac.getAtom(i).getCharge();
			if (factors[0] == 0 && factors[1] == 0 && factors[2] == 0) {
				gasteigerFactors[STEP_SIZE * i + i + 3] = 1;
			} else {
				gasteigerFactors[STEP_SIZE * i + i + 3] = factors[0] + factors[1] + factors[2];
			}
		}
		return gasteigerFactors;
	}
}


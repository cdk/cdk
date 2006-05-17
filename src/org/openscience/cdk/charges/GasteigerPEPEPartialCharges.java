/*  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2006-05-11 10:17:36 +0200 (Do, 11 Mai 2006) $
 *  $Revision: 6217 $
 *
 *  Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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

import java.io.IOException;

import org.openscience.cdk.SetOfAtomContainers;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ISetOfAtomContainers;
//import org.openscience.cdk.qsar.IMolecularDescriptor;
//import org.openscience.cdk.qsar.descriptors.atomic.CovalentRadiusDescriptor;
//import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.StructureResonanceGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>The calculation of the Gasteiger (PEPE) partial charges is based on 
 * {@cdk.cite GM81}. This class doesn't implement the original method of the Marsili but the 
 * method based on H. Saller which is described from Petra manual</p> 
 * <p>They are calculated by generating all valence bond(resonance) structures
 * for this system and then weighting them on the basis of pi-orbital electronegativies
 * and formal considerations based on PEPE (Partial Equalization of pi-electronegativity).</p>
 * 
 * @author      Miguel Rojas
 * 
 * @cdk.module  charges
 * @cdk.created 2006-05-14
 * @cdk.keyword partial atomic charges
 * @cdk.keyword charge distribution
 * @cdk.keyword electronegativities, partial equalization of orbital
 * @cdk.keyword PEPE
 * @see GasteigerMarsiliPartialCharges
 */
public class GasteigerPEPEPartialCharges {
	/** max iterations */
	private double MX_ITERATIONS = 12;
	private int STEP_SIZE = 5;
	private AtomTypeFactory factory;
	/** Flag is set if the formal charge of a chemobject is changed due to resonance.*/
	private static int ISCHANGEDFC = 0;

	
	/**
	 *  Constructor for the GasteigerPEPEPartialCharges object
	 */
	public GasteigerPEPEPartialCharges() { }
	/**
	 *  Sets the maxGasteigerIters attribute of the GasteigerPEPEPartialCharges
	 *  object
	 *
	 *@param  iters  The new maxGasteigerIters value
	 */
	public void setMaxGasteigerIters(double iters) {
		MX_ITERATIONS = iters;
	}
	
	/**
	 *  Main method which assigns Gasteiger partial pi charges. 
	 *  
	 *
	 *@param  ac             AtomContainer
	 *@param  setCharge      boolean flag to set charge on atoms
	 *@return                AtomContainer with partial charges
	 *@exception  Exception  Possible Exceptions
	 */
	public IAtomContainer assignGasteigerMarsiliPiPartialCharges(IAtomContainer ac, boolean setCharge) throws Exception {
		
		/* detect conjugated Pi systems*/
		SetOfAtomContainers set = ConjugatedPiSystemsDetector.detect(ac);
		if(set.getAtomContainerCount() == 0 ){
			for(int i = 0; i < ac.getAtomCount() ; i++)
				ac.getAtomAt(i).setCharge(0.0);
		}else{
			/*0: remove charge, flag ac*/
			for(int j = 0 ; j < ac.getAtomCount(); j++){
				ac.getAtomAt(j).setCharge(0.0);
				ac.getAtomAt(j).setFlag(ISCHANGEDFC, false);
			}
			/*1: detect resonance structure*/
			StructureResonanceGenerator gR = new StructureResonanceGenerator();
			ISetOfAtomContainers iSet = gR.getAllStructures(ac);
			
			/*2: search whose atoms which don't keep their formal charge and set flags*/
			double[][] sumCharges = new double[iSet.getAtomContainerCount()][ac.getAtomCount( )];
			for(int i = 1; i < iSet.getAtomContainerCount() ; i++){
				IAtomContainer iac = iSet.getAtomContainer(i);
				for(int j = 0 ; j < ac.getAtomCount(); j++){
					sumCharges[i][j] += iac.getAtomAt(j).getFormalCharge();
				}
			}
			for(int i = 1; i < iSet.getAtomContainerCount() ; i++){
				IAtomContainer iac = iSet.getAtomContainer(i);
				for(int j = 0 ; j < ac.getAtomCount(); j++){
					if(sumCharges[i][j] != 0.0){
						ac.getAtomAt(j).setFlag(ISCHANGEDFC, true);
						iac.getAtomAt(j).setFlag(ISCHANGEDFC, true);
					}
				}
				
			}
			
			/*3: set sigma charge (PEOE). Initial Point*/
			GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();;
			peoe.setMaxGasteigerIters(6);
			IAtomContainer acCloned;
			try {
				acCloned = (IAtomContainer)ac.clone();
				acCloned = peoe.assignGasteigerMarsiliSigmaPartialCharges(acCloned, true);
			} catch (CloneNotSupportedException e) {
				throw new CDKException("Could not clone ac", e);
			}

			/*4: calculate topological weight factors Wt=fQ*fB*fA*/
			double[] Wt = new double[iSet.getAtomContainerCount()-1];
			for(int i = 1; i < iSet.getAtomContainerCount() ; i++)
				Wt[i-1]= getTopologicalFactors(iSet.getAtomContainer(i),ac);
			
			
			double fE = 1.1;
			double fS = 0.37;
			double[][] gasteigerFactors = assignGasteigerPiMarsiliFactors(iSet);//a,b,c,deoc,chi,q
			
			/*calculate electronegativity for changed atoms and make the difference between whose
			 * atoms which change their formal charge*/
			for (int iter = 0; iter < MX_ITERATIONS; iter++) {
				for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
					IAtomContainer iac = iSet.getAtomContainer(k);
					double[] electronegativity1 = new double[2];
					int count = 0;
					int atom1 = 0;
					int atom2 = 0;
					for (int j = 0; j < iac.getAtomCount(); j++) {
						if(iac.getAtomAt(j).getFlag(ISCHANGEDFC)){
							if(count == 0)
								atom1 = j;
							else 
								atom2 = j;
							
							double q1 = acCloned.getAtomAt(j).getCharge();
							electronegativity1[count] = gasteigerFactors[k][STEP_SIZE * j + j + 2] * q1 * q1 + gasteigerFactors[k][STEP_SIZE * j + j + 1] * q1 + gasteigerFactors[k][STEP_SIZE * j + j];
							
							count++;
						}
						
					}
					/*diferency of electronegativity 1 lower*/
					double max1 = Math.max(electronegativity1[0], electronegativity1[1]);
					double min1 = Math.min(electronegativity1[0], electronegativity1[1]);
					double DX = 1.0;
					if(electronegativity1[0] < electronegativity1[1])
						DX = gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 3];
					else
						DX = gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 3];
						
					double Dq = (max1-min1)/DX;
//					System.out.println("Dq : "+Dq+ " = ("+ max1+"-"+min1+")/"+DX);
					double epN1 = getElectrostaticPotentialN(acCloned,atom1);
					double epN2 = getElectrostaticPotentialN(acCloned,atom2);
					double SumQN = Math.abs(epN1 - epN2);
//					System.out.println("sum("+SumQN+") = ("+epN1+") - ("+epN2+")");
					/* electronic weight*/
					double WE = Dq + fE*SumQN;
//					System.out.println("WE : "+WE+" = Dq("+Dq+")+fE("+fE+")*SumQN("+SumQN);
					int iTE = iter+1;
					/* total topological*/
					double W = WE*Wt[k-1]*fS/(iTE);
//					System.out.println("W : "+W+" = WE("+WE+")*Wt("+Wt[k-1]+")*fS("+fS+")/iter("+iTE+"), atoms: "+atom1+", "+atom2);
					if(iac.getAtomAt(atom1).getFormalCharge() == 1){
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = W;
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = -1*W;
					}else{
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = -1*W;
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = W;
					}
				}
				
				for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
					for (int i = 0; i < ac.getAtomCount(); i++) {
					double charge = ac.getAtomAt(i).getCharge();
					double chargeT = 0.0;
					chargeT = charge + gasteigerFactors[k][STEP_SIZE * i + i + 5];
					ac.getAtomAt(i).setCharge(chargeT);
					}
				}
			}
			
		}
//		for (int i = 0; i < ac.getAtomCount(); i++) {
//			System.out.println(ac.getAtomAt(i).getSymbol()+" - charget: "+ac.getAtomAt(i).getCharge());
//		}
		return ac;
		
	}
	/**
	 * get the electrostatic potential of the neighbours of a atom.
	 *  
	 * @param ac   The IAtomContainer to study
	 * @param atom The position of the IAtom to study
	 * @return     The sum of electrostatic potential of the neighbours
	 */
	private double getElectrostaticPotentialN(IAtomContainer ac, int atom1) {
//		IMolecularDescriptor descriptor;
		double CoulombForceConstant = 1/(4*Math.PI*0.885/*Math.pow(10, -12)*/);
		double sum = 0.0;
		try {
			if (factory == null) 
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    ac.getBuilder()
                );
        
            
		
//			descriptor = new CovalentRadiusDescriptor();
			IAtom[] atoms = ac.getConnectedAtoms(ac.getAtomAt(atom1));
			for(int i = 0 ; i < atoms.length ; i++){
				double covalentradius = 0;
	            String symbol = atoms[i].getSymbol();
	            IAtomType type = factory.getAtomType(symbol);
	            covalentradius = type.getCovalentRadius();
//				Object[] params1 = {new Integer(ac.getAtomNumber(atoms[i]))};
//				descriptor.setParameters(params1);
//				double retval = ((DoubleResult)descriptor.calculate(ac).getValue()).doubleValue();
				double charge = atoms[i].getCharge();
				double sumI = CoulombForceConstant*charge/covalentradius;
//				System.out.println("sum("+sumI+") = CFC("+CoulombForceConstant+")*charge("+charge+"/ret("+covalentradius);
				sum += sumI;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (CDKException e) {
			e.printStackTrace();
		}
		
		return sum;
	}


	/**
	 * get the topological weight factor for each atomContainer
	 * 
	 * @param atomContainer  The IAtomContainer to study.
	 * @param ac             The IAtomContainer to study.
	 * @return The value
	 */
	private double getTopologicalFactors(IAtomContainer atomContainer,IAtomContainer ac) {
		/*factor for separation of charge*/
		int totalNCharge = AtomContainerManipulator.getTotalNegativeFormalCharge(atomContainer);
		double fQ = 1.0;
		if(totalNCharge != 0.0){
			fQ = 0.5;
			for(int i = 0; i < atomContainer.getBondCount(); i++){
				IBond bond = atomContainer.getBondAt(i);
				IAtom[] atoms = bond.getAtoms();
				if(atoms[0].getFormalCharge() != 0.0 && atoms[1].getFormalCharge() != 0.0){
					fQ = 0.25;
					break;
				}
			}
		}
		/*factor, if the number of covalents bonds is decreased*/
		double fB = 1.0;
		
		int numBond1 = 0;
		int numBond2 = 0;
        IBond[] bonds = atomContainer.getBonds();
        for (int i = 0; i < bonds.length; i++) {
            if (atomContainer.getBondAt(i).getOrder() == 2.0) 
            	numBond1 += 1;
            if (ac.getBondAt(i).getOrder() == 2.0) 
            	numBond2 += 1;
        }
        if(numBond1 != numBond2)
			fB = 0.8;
		
//		System.out.println("return= sp:"+fQ+", dc:"+fB);
		
		return fQ*fB;
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
	public double[][] assignGasteigerPiMarsiliFactors(ISetOfAtomContainers setAc) {
		//a,b,c,denom,chi,q
		double[][] gasteigerFactors = new double[setAc.getAtomContainerCount()][(setAc.getAtomContainer(0).getAtomCount() * (STEP_SIZE+1))];
		String AtomSymbol = "";
		double[] factors = new double[]{0.0, 0.0, 0.0};
		for( int k = 1 ; k < setAc.getAtomContainerCount(); k ++){
			IAtomContainer ac = setAc.getAtomContainer(k);
			
		for (int i = 0; i < ac.getAtomCount(); i++) {
			factors[0] = 0.0;
			factors[1] = 0.0;
			factors[2] = 0.0;
			AtomSymbol = ac.getAtomAt(i).getSymbol();
			if (AtomSymbol.equals("H")) {
				factors[0] = 0.0;
				factors[1] = 0.0;
				factors[2] = 0.0;
			} else if (AtomSymbol.equals("C")) {
//				if (ac.getAtomAt(i).getFlag(ISCHANGEDFC)) {
					factors[0] = 5.60;
					factors[1] = 8.93;
					factors[2] = 2.94;
//				}
			} else if (AtomSymbol.equals("O")) {
//				if (ac.getAtomAt(i).getFlag(ISCHANGEDFC)) {
					if(ac.getMaximumBondOrder(ac.getAtomAt(i)) == 1){
						
						factors[0] = 10.0;
						factors[1] = 13.86;
						factors[2] = 9.68;
					}else {
						factors[0] = 7.91;
						factors[1] = 14.76;
						factors[2] = 6.85;
					}
//				}
			} else if (AtomSymbol.equals("F")) {
				factors[0] = 7.34;
				factors[1] = 13.86;
				factors[2] = 9.68;
			}
		
			gasteigerFactors[k][STEP_SIZE * i + i] = factors[0];
			gasteigerFactors[k][STEP_SIZE * i + i + 1] = factors[1];
			gasteigerFactors[k][STEP_SIZE * i + i + 2] = factors[2];
			if(ac.getAtomAt(i).getFlag(ISCHANGEDFC)){
//				System.out.println("S: "+AtomSymbol+" "+ac.getAtomAt(i).getCharge());
				gasteigerFactors[k][STEP_SIZE * i + i + 5] = ac.getAtomAt(i).getCharge();
			}

//			else{
//				gasteigerFactors[k][STEP_SIZE * i + i + 5] = 0.0;
//				ac.getAtomAt(i).setCharge(0.0);
//			}
			
			if (factors[0] == 0 && factors[1] == 0 && factors[2] == 0) {
				gasteigerFactors[k][STEP_SIZE * i + i + 3] = 1;
			} else {
				gasteigerFactors[k][STEP_SIZE * i + i + 3] = factors[0] + factors[1] + factors[2];
			}
		}
		}
		

		return gasteigerFactors;
	}
}


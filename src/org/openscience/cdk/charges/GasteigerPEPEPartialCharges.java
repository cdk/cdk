/*  $RCSfile$
 *  $Author: miguelrojasch $
 *  $Date: 2006-05-11 10:17:36 +0200 (Do, 11 Mai 2006) $
 *  $Revision: 6217 $
 *
 *  Copyright (C) 2004-2006  Miguel Rojas <miguel.rojas@uni-koeln.de>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.BreakingBondReaction;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.StructureResonanceGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>The calculation of the Gasteiger (PEPE) partial charges is based on 
 * {@cdk.cite GM81}. This class doesn't implement the original method of the Marsili but the 
 * method based on H. Saller which is described from Petra manual version 2.6</p> 
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
	private double MX_ITERATIONS = 8;
	private int STEP_SIZE = 5;
	private AtomTypeFactory factory;
	/** Flag is set if the formal charge of a chemobject is changed due to resonance.*/
	private static int ISCHANGEDFC = 0;
	
	/** Corresponds an empirical influence between the electrostatic potential and
	 * the neighbours.*/
	private double fE = 1.1;/*1.1*/
	/** Scalle factor which makes same heavay for all structures*/
	private double fS = 0.37;
	
	
	
	private LoggingTool logger = new LoggingTool(GasteigerPEPEPartialCharges.class);

	
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
	public IAtomContainer assignGasteigerPiPartialCharges(IAtomContainer ac, boolean setCharge) throws Exception {
//		System.out.println("smiles1: "+(new SmilesGenerator(ac.getBuilder())).createSMILES((IMolecule) ac));
		IAtomContainerSet setHI = null;
		
		/*0: remove charge, and possible flag ac*/
		for(int j = 0 ; j < ac.getAtomCount(); j++){
			ac.getAtom(j).setCharge(0.0);
			ac.getAtom(j).setFlag(ISCHANGEDFC, false);
		}
		for(int j = 0 ; j < ac.getBondCount(); j++){
			ac.getBond(j).setFlag(ISCHANGEDFC, false);
		}
		
		/*1: detect resonance structure*/
		StructureResonanceGenerator gR = new StructureResonanceGenerator();/*according G. should be integrated the breaking bonding*/
		IAtomContainerSet iSet = gR.getAllStructures(ac);
//		System.out.println("iset: "+iSet.getAtomContainerCount());
		
		/* detect conjugated Pi systems*/
//		SetOfAtomContainers set = ConjugatedPiSystemsDetector.detect(ac);
		if(iSet.getAtomContainerCount()  < 2 ){
			/* detect hyperconjugation interactions */
			setHI = getHyperconjugationInteractions(ac);
//			System.out.println("setHI: "+setHI.getAtomContainerCount());
//			if(setHI.getAtomContainerCount() == 0 )
//				System.out.println("setHI: "+setHI.getAtomContainerCount());
//				for(int i = 0; i < ac.getAtomCount() ; i++)
//					ac.getAtom(i).setCharge(0.0);
		}
		if(setHI != null) 
			if(	setHI.getAtomContainerCount() != 0)
				iSet.add(setHI);
//		System.out.println("isetTT: "+iSet.getAtomContainerCount());

		/*2: search whose atoms which don't keep their formal charge and set flags*/
		double[][] sumCharges = new double[iSet.getAtomContainerCount()][ac.getAtomCount( )];
		for(int i = 1; i < iSet.getAtomContainerCount() ; i++){
			IAtomContainer iac = iSet.getAtomContainer(i);
			for(int j = 0 ; j < iac.getAtomCount(); j++)
				sumCharges[i][j] = iac.getAtom(j).getFormalCharge();
			
		}
		for(int i = 1; i < iSet.getAtomContainerCount() ; i++){
			IAtomContainer iac = iSet.getAtomContainer(i);
			int count = 0;
			for(int j = 0 ; j < ac.getAtomCount(); j++)
				if(count < 2)
				if(sumCharges[i][j] != ac.getAtom(j).getFormalCharge()){
					ac.getAtom(j).setFlag(ISCHANGEDFC, true);
					iac.getAtom(j).setFlag(ISCHANGEDFC, true);
					count++; /* TODO- error*/
				}
		}

		/*3: set sigma charge (PEOE). Initial start point*/
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();;
		peoe.setMaxGasteigerIters(6);
		IAtomContainer acCloned;
		

		double[][] gasteigerFactors = assignPiFactors(iSet);//a,b,c,deoc,chi,q
		
		/*4: calculate topological weight factors Wt=fQ*fB*fA*/
		double[] Wt = new double[iSet.getAtomContainerCount()-1];
		for(int i = 1; i < iSet.getAtomContainerCount() ; i++){
			Wt[i-1]= getTopologicalFactors(iSet.getAtomContainer(i),ac);
//			System.out.println(", W:"+Wt[i-1]);
			try {
				acCloned = (IAtomContainer)iSet.getAtomContainer(i).clone();
//				System.out.println("smilesClo: "+(new SmilesGenerator(ac.getBuilder())).createSMILES((IMolecule) acCloned));
				acCloned = peoe.assignGasteigerMarsiliSigmaPartialCharges(acCloned, true);
				for(int j = 0 ; j<acCloned.getAtomCount(); j++)
					if(iSet.getAtomContainer(i).getAtom(j).getFlag(ISCHANGEDFC)){
						gasteigerFactors[i][STEP_SIZE * j + j + 5] = acCloned.getAtom(j).getCharge(); 
					}
			} catch (CloneNotSupportedException e) {
				throw new CDKException("Could not clone ac", e);
			}
		}
		
		/*calculate electronegativity for changed atoms and make the difference between whose
		 * atoms which change their formal charge*/
		for (int iter = 0; iter < MX_ITERATIONS; iter++) {
			for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
				IAtomContainer iac = iSet.getAtomContainer(k);
//				System.out.println("smilesITERa: "+(new SmilesGenerator(ac.getBuilder())).createSMILES((IMolecule) iac));
				double[] electronegativity = new double[2];
				int count = 0;
				int atom1 = 0;
				int atom2 = 0;
				for (int j = 0; j < iac.getAtomCount(); j++) {
					if(count == 2)/* TODO- not limited*/
						break;
					if(iac.getAtom(j).getFlag(ISCHANGEDFC)){
//						System.out.println("Atom: "+j+", S:"+iac.getAtom(j).getSymbol()+", C:"+iac.getAtom(j).getFormalCharge());
						if(count == 0)
							atom1 = j;
						else 
							atom2 = j;
						
						double q1 = gasteigerFactors[k][STEP_SIZE * j + j + 5];
						electronegativity[count] = gasteigerFactors[k][STEP_SIZE * j + j + 2] * q1 * q1 + gasteigerFactors[k][STEP_SIZE * j + j + 1] * q1 + gasteigerFactors[k][STEP_SIZE * j + j];
//						System.out.println("e:"+electronegativity[count] +",q1: "+q1+", c:"+gasteigerFactors[k][STEP_SIZE * j + j + 2] +", b:"+gasteigerFactors[k][STEP_SIZE * j + j + 1]  + ", a:"+gasteigerFactors[k][STEP_SIZE * j + j]);
						count++;
					}
					
				}
//				System.out.println("Atom1:"+atom1+",Atom2:"+atom2);
				/*diferency of electronegativity 1 lower*/
				double max1 = Math.max(electronegativity[0], electronegativity[1]);
				double min1 = Math.min(electronegativity[0], electronegativity[1]);
				double DX = 1.0;
				if(electronegativity[0] < electronegativity[1])
					DX = gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 3];
				else
					DX = gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 3];
					
				double Dq = (max1-min1)/DX;
//					System.out.println("Dq : "+Dq+ " = ("+ max1+"-"+min1+")/"+DX);
				double epN1 = getElectrostaticPotentialN(iac,atom1,gasteigerFactors[k]);
				double epN2 = getElectrostaticPotentialN(iac,atom2,gasteigerFactors[k]);
				double SumQN = Math.abs(epN1 - epN2);
//					System.out.println("sum("+SumQN+") = ("+epN1+") - ("+epN2+")");
				
				/* electronic weight*/
				double WE = Dq + fE*SumQN;
//					System.out.println("WE : "+WE+" = Dq("+Dq+")+fE("+fE+")*SumQN("+SumQN);
				int iTE = iter+1;
				
				/* total topological*/
				double W = WE*Wt[k-1]*fS/(iTE);
//					System.out.println("W : "+W+" = WE("+WE+")*Wt("+Wt[k-1]+")*fS("+fS+")/iter("+iTE+"), atoms: "+atom1+", "+atom2);
				if(iac.getAtom(atom1).getFormalCharge() == 0){
					if(ac.getAtom(atom1).getFormalCharge() == -1){
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = W;
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = -1*W;
					}else{
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = -1*W;
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = W;
					}
				}else if(iac.getAtom(atom1).getFormalCharge() == 1){
					gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = W;
					gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = -1*W;
				}else{
					gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = -1*W;
					gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = W;
				}
			}
			for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
				for (int i = 0; i < ac.getAtomCount(); i++) 
					if(iSet.getAtomContainer(k).getAtom(i).getFlag(ISCHANGEDFC)){
						double charge = ac.getAtom(i).getCharge();
						double chargeT = 0.0;
						chargeT = charge + gasteigerFactors[k][STEP_SIZE * i + i + 5];
//						System.out.println("i<|"+chargeT+"=c:" +charge + "+g: "+gasteigerFactors[k][STEP_SIZE * i + i + 5]);
						ac.getAtom(i).setCharge(chargeT);
					}
			}
			
		}// iterations
		
		return ac;
		
	}
	/**
	 * get the possibles structures after a hyperconjugation interactions.
	 * 
	 * @param ac IAtomContainer
	 * @return ISetOfAtomContainers
	 * @throws CDKException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	private IAtomContainerSet getHyperconjugationInteractions(IAtomContainer ac) throws IOException, ClassNotFoundException, CDKException {
		IAtomContainerSet set = ac.getBuilder().newAtomContainerSet();
//        try {
            IReactionProcess type = new BreakingBondReaction();
//    		for(int k = 0; k < ac.getAtomCount(); k++){
//    			if(ac.getAtom(k).getSymbol().equals("H")){
//    				ac.removeAtomAndConnectedElectronContainers(ac.getAtom(k));
//    				k--;
//    			}
//    		}
//    		
//    		HydrogenAdder adder = new HydrogenAdder();
//            adder.addImplicitHydrogensToSatisfyValency(ac);
//    		
    		IMoleculeSet setOfReactants = ac.getBuilder().newMoleculeSet();
    		for(int i = 0 ; i < ac.getBondCount() ; i++){
    			if(ac.getBond(i).getOrder() > 1){
    				ac.getBond(i).getAtoms()[0].setFlag(CDKConstants.REACTIVE_CENTER,true);
    				ac.getBond(i).getAtoms()[1].setFlag(CDKConstants.REACTIVE_CENTER,true);
    				ac.getBond(i).setFlag(CDKConstants.REACTIVE_CENTER,true);
    			}
    		}
    			
    		setOfReactants.addMolecule((IMolecule) ac);
    		Object[] params = {Boolean.TRUE};
    		
			type.setParameters(params);
			IReactionSet setOfReactions = type.initiate(setOfReactants, null);
	        for(int i = 0; i < setOfReactions.getReactionCount(); i++){
	        	type = new HyperconjugationReaction();
	    		IMoleculeSet setOfM2 = ac.getBuilder().newMoleculeSet();
	    		IMolecule mol= setOfReactions.getReaction(i).getProducts().getMolecule(0);
//	    		for(int k = 0; k < mol.getAtomCount(); k++){
//	    			if(mol.getAtom(k).getSymbol().equals("H")){
//	    				mol.removeAtomAndConnectedElectronContainers(mol.getAtom(k));
//	    				k--;
//	    			}
//	    		}
	    		for(int k = 0; k < mol.getBondCount(); k++){
	    			mol.getBond(k).setFlag(CDKConstants.REACTIVE_CENTER,false);
	    			mol.getBond(k).getAtoms()[0].setFlag(CDKConstants.REACTIVE_CENTER,false);
	    			mol.getBond(k).getAtoms()[1].setFlag(CDKConstants.REACTIVE_CENTER,false);
	    		}
	    		setOfM2.addMolecule((IMolecule) mol);
	    		Object[] params2 = {Boolean.FALSE};
				type.setParameters(params2);
				IReactionSet setOfReactions2 = type.initiate(setOfM2, null);
				if(setOfReactions2.getReactionCount() > 0){
					
//				IMolecule acc = setOfReactions2.getReaction(0).getProducts().getMolecule(0);
				IMolecule react = setOfReactions2.getReaction(0).getReactants().getMolecule(0);
				
//				hAdder.addExplicitHydrogensToSatisfyValency(acc);
						
				set.addAtomContainer(react);
				}
	        }
	        
//		} catch (CDKException e) {
//			e.printStackTrace();
//		}
		
//		hAdder.addExplicitHydrogensToSatisfyValency((IMolecule) ac);
		
		return set;
	}
	/**
	 * get the electrostatic potential of the neighbours of a atom.
	 *  
	 * @param ac   The IAtomContainer to study
	 * @param ds 
	 * @param atom The position of the IAtom to study
	 * @return     The sum of electrostatic potential of the neighbours
	 */
	private double getElectrostaticPotentialN(IAtomContainer ac, int atom1, double[] ds) {
		
//		double CoulombForceConstant = 1/(4*Math.PI*8.81/*Math.pow(10, -12)*/);
		double CoulombForceConstant = 0.048;
		double sum = 0.0;
		try {
			if (factory == null) 
                factory = AtomTypeFactory.getInstance(
                    "org/openscience/cdk/config/data/jmol_atomtypes.txt", 
                    ac.getBuilder()
                );

			IAtom[] atoms = ac.getConnectedAtoms(ac.getAtom(atom1));
			for(int i = 0 ; i < atoms.length ; i++){
				double covalentradius = 0;
	            String symbol = atoms[i].getSymbol();
	            IAtomType type = factory.getAtomType(symbol);
	            covalentradius = type.getCovalentRadius();

	            double charge = ds[STEP_SIZE * atom1 + atom1 + 5];
				double sumI = CoulombForceConstant*charge/(covalentradius*covalentradius);
//				System.out.println("sum_("+sumI+") = CFC("+CoulombForceConstant+")*charge("+charge+"/ret("+covalentradius);
				sum += sumI;
			}
		} catch (IOException e) {
			logger.debug(e);
		} catch (ClassNotFoundException e) {
			logger.debug(e);
		} catch (CDKException e) {
			logger.debug(e);
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
		int totalNCharge1 = AtomContainerManipulator.getTotalNegativeFormalCharge(atomContainer);
		int totalPCharge1 = AtomContainerManipulator.getTotalPositiveFormalCharge(atomContainer);
		
		double fQ = 1.0;
		if(totalNCharge1 != 0.0){
			fQ = 0.5;
			for(int i = 0; i < atomContainer.getBondCount(); i++){
				IBond bond = atomContainer.getBond(i);
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
            if (atomContainer.getBond(i).getOrder() == 2.0) 
            	numBond1 += 1;
            if (ac.getBond(i).getOrder() == 2.0) 
            	numBond2 += 1;
        }
        
        if(numBond1 </*>*/ numBond2)
			fB = 0.8;
        
		double fPlus = 1.0;
        if(totalNCharge1 == 0.0 && totalPCharge1 == 0.0 )
        	fPlus = 0.1;
        
        
        /*aromatic*/
        double fA = 1.0;
        try {
			if(HueckelAromaticityDetector.detectAromaticity(ac))
				if(!HueckelAromaticityDetector.detectAromaticity(atomContainer))
						fA = 0.3;
		} catch (CDKException e) {
			e.printStackTrace();
		}
//		System.out.println("return "+fQ*fB*fPlus*fA+"= sp:"+fQ+", dc:"+fB+", fPlus:"+fPlus+", fA:"+fA);
		
		return fQ*fB*fPlus*fA;
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
	 * Method which stores and assigns the factors a,b,c and CHI+
	 *
	 * @return     Array of doubles [a1,b1,c1,denom1,chi1,q1...an,bn,cn...] 1:Atom 1-n in AtomContainer
	 */
	private double[][] assignPiFactors(IAtomContainerSet setAc) {
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
				AtomSymbol = ac.getAtom(i).getSymbol();
				if (AtomSymbol.equals("H")) {
					factors[0] = 0.0;
					factors[1] = 0.0;
					factors[2] = 0.0;
				} else if (AtomSymbol.equals("C")) {/*
					if(ac.getAtom(i).getFlag(ISCHANGEDFC))*/{
						factors[0] = 5.60;
						factors[1] = 8.93;
						factors[2] = 2.94;
					}
				} else if (AtomSymbol.equals("O")) {
						if(ac.getMaximumBondOrder(ac.getAtom(i)) == 1){
							factors[0] = 10.0;
							factors[1] = 13.86;
							factors[2] = 9.68;
						}else {
							factors[0] = 7.91;
							factors[1] = 14.76;
							factors[2] = 6.85;
						}
				} else if (AtomSymbol.equals("N")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) != 1){
						factors[0] = 7.95;/*7.95*/
						factors[1] = 9.73;/*9.73*/
						factors[2] = 2.67;/*2.67*/
					}else {
						factors[0] = 4.54;/*4.54*//*5.5*/
						factors[1] = 11.86;/*11.86*//*10.86*/
						factors[2] = 7.32;/*7.32*//*7.99*/
					}
				} else if (AtomSymbol.equals("S")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) == 1){
						factors[0] = 7.73;
						factors[1] = 8.16;
						factors[2] = 1.81;
					}else {
						factors[0] = 6.60;
						factors[1] = 10.32;
						factors[2] = 3.72;
					}
				} else if (AtomSymbol.equals("F")) {
					factors[0] = 7.34;
					factors[1] = 13.86;
					factors[2] = 9.68;
				} else if (AtomSymbol.equals("Cl")) {
					factors[0] = 6.50;
					factors[1] = 11.02;
					factors[2] = 4.52;
				} else if (AtomSymbol.equals("Br")) {
					factors[0] = 5.20;
					factors[1] = 9.68;
					factors[2] = 4.48;
				} else if (AtomSymbol.equals("I")) {
					factors[0] = 4.95;
					factors[1] = 8.81;
					factors[2] = 3.86;
				}
			
				gasteigerFactors[k][STEP_SIZE * i + i] = factors[0];
				gasteigerFactors[k][STEP_SIZE * i + i + 1] = factors[1];
				gasteigerFactors[k][STEP_SIZE * i + i + 2] = factors[2];
				gasteigerFactors[k][STEP_SIZE * i + i + 5] = ac.getAtom(i).getCharge();
	
				if (factors[0] == 0 && factors[1] == 0 && factors[2] == 0) {
					gasteigerFactors[k][STEP_SIZE * i + i + 3] = 1;
				} else {
					gasteigerFactors[k][STEP_SIZE * i + i + 3] = factors[0] + factors[1] + factors[2];
				}
			}
		}
		

		return gasteigerFactors;
	}
	/**
	 *  Method which stores and assigns the factors a,b,c and CHI+
	 *
	 *@return     Array of doubles [a1,b1,c1,denom1,chi1,q1...an,bn,cn...] 1:Atom 1-n in AtomContainer
	 */
	public double[][] assignrPiMarsilliFactors(IAtomContainerSet setAc) {
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
				AtomSymbol = ac.getAtom(i).getSymbol();
				if (AtomSymbol.equals("H")) {
					factors[0] = 0.0;
					factors[1] = 0.0;
					factors[2] = 0.0;
				} else if (AtomSymbol.equals("C")) {
						factors[0] = 5.98;/*5.98-5.60*/
						factors[1] = 7.93;/*7.93-8.93*/
						factors[2] = 1.94;
				} else if (AtomSymbol.equals("O")) {
						if(ac.getMaximumBondOrder(ac.getAtom(i)) > 1){
							factors[0] = 11.2;/*11.2-10.0*/
							factors[1] = 13.24;/*13.24-13.86*/
							factors[2] = 9.68;
						}else {
							factors[0] = 7.91;
							factors[1] = 14.76;
							factors[2] = 6.85;
						}
				} else if (AtomSymbol.equals("N")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i))  > 1){
						
						factors[0] = 8.95;/*7.95*/
						factors[1] = 9.73;/*9.73*/
						factors[2] = 2.67;/*2.67*/
					}else {
						factors[0] = 4.54;
						factors[1] = 11.86;
						factors[2] = 7.32;
					}
				} else if (AtomSymbol.equals("S")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i))  > 1){
						
						factors[0] = 7.73;
						factors[1] = 8.16;
						factors[2] = 1.81;
					}else {
						factors[0] = 6.60;
						factors[1] = 10.32;
						factors[2] = 3.72;
					}
				} else if (AtomSymbol.equals("F")) {
					factors[0] = 7.14/*7.34*/;
					factors[1] = 13.86;
					factors[2] = 5.68;
				} else if (AtomSymbol.equals("Cl")) {
					factors[0] = 6.51;/*6.50*/
					factors[1] = 11.02;
					factors[2] = 4.52;
				} else if (AtomSymbol.equals("Br")) {
					factors[0] = 5.20;
					factors[1] = 9.68;
					factors[2] = 4.48;
				} else if (AtomSymbol.equals("I")) {
					factors[0] = 4.95;
					factors[1] = 8.81;
					factors[2] = 3.86;
				}
			
				gasteigerFactors[k][STEP_SIZE * i + i] = factors[0];
				gasteigerFactors[k][STEP_SIZE * i + i + 1] = factors[1];
				gasteigerFactors[k][STEP_SIZE * i + i + 2] = factors[2];
				gasteigerFactors[k][STEP_SIZE * i + i + 5] = ac.getAtom(i).getCharge();
				
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


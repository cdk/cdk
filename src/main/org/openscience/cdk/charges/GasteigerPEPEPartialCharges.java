/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
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
import org.openscience.cdk.reaction.type.HeterolyticCleavagePBReaction;
import org.openscience.cdk.reaction.type.HeterolyticCleavageSBReaction;
import org.openscience.cdk.reaction.type.HyperconjugationReaction;
import org.openscience.cdk.reaction.type.SharingAnionReaction;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.StructureResonanceGenerator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * <p>The calculation of the Gasteiger (PEPE) partial charges is based on 
 * {@cdk.cite Saller85}. This class doesn't implement the original method of the Marsili but the 
 * method based on H. Saller which is described from Petra manual version 2.6</p> 
 * <p>They are calculated by generating all valence bond (resonance) structures
 * for this system and then weighting them on the basis of pi-orbital electronegativities
 * and formal considerations based on PEPE (Partial Equalization of pi-electronegativity).</p>
 * 
 * @author      Miguel Rojas
 * 
 * @cdk.module  charges
 * @cdk.svnrev  $Revision$
 * @cdk.created 2006-05-14
 * @cdk.keyword partial atomic charges
 * @cdk.keyword charge distribution
 * @cdk.keyword electronegativities, partial equalization of orbital
 * @cdk.keyword PEPE
 * @see GasteigerMarsiliPartialCharges
 */
@TestClass("org.openscience.cdk.charges.GasteigerPEPEPartialChargesTest")
public class GasteigerPEPEPartialCharges implements IChargeCalculator {
	/** max iterations */
	private int MX_ITERATIONS = 8;
	/** max number of resonance structures to be searched*/
	private int MX_RESON = 50;
	private int STEP_SIZE = 5;
	private AtomTypeFactory factory;
	/** Flag is set if the formal charge of a chemobject is changed due to resonance.*/
	private static int ISCHANGEDFC = 0;
	
	/** Corresponds an empirical influence between the electrostatic potential and
	 * the neighbours.*/
	private double fE = 1.1;/*1.1*/
	/** Scale factor which makes same heavy for all structures*/
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
    @TestMethod("testSetMaxGasteigerIters_Double")
	public void setMaxGasteigerIters(int iters) {
		MX_ITERATIONS = iters;
	}
	/**
	 *  Sets the maximum resonance structures to be searched
	 *
	 *@param  numbReson  The number of resonance Structures to be searched
	 */
    @TestMethod("testSetMaxResoStruc_Int")
	public void setMaxResoStruc(int numbReson) {
		MX_RESON = numbReson;
	}
    /**
	 *  Gets the maxGasteigerIters attribute of the GasteigerPEPEPartialCharges
	 *  object
	 *
	 *@return  The new maxGasteigerIters value
	 */
    @TestMethod("testGetMaxGasteigerIters")
	public int getMaxGasteigerIters() {
		return MX_ITERATIONS;
	}
	/**
	 *  Gets the maximum resonance structures to be searched
	 *
     * @return the maximum numebr of resonance structures that will be returned
     */
    @TestMethod("testGetMaxResoStruc")
	public int getMaxResoStruc() {
		return MX_RESON;
	}
	/**
	 *  Main method which assigns Gasteiger partial pi charges. 
	 *  
	 *
	 *@param  ac             AtomContainer
	 *@param  setCharge      currently unused
	 *@return                AtomContainer with partial charges
	 *@exception  Exception  Possible Exceptions
	 */
    @TestMethod("testAssignGasteigerPiPartialCharges_IAtomContainer_Boolean")
	public IAtomContainer assignGasteigerPiPartialCharges(IAtomContainer ac, boolean setCharge) throws Exception {

        // we save the aromaticity flags for the input molecule so that
        // we can add them back before we return
        boolean[] oldBondAromaticity = new boolean[ac.getAtomCount()];
        boolean[] oldAtomAromaticity = new boolean[ac.getAtomCount()];
        for (int i = 0; i < ac.getAtomCount(); i++) oldAtomAromaticity[i] = ac.getAtom(i).getFlag(CDKConstants.ISAROMATIC);
        for (int i = 0; i < ac.getBondCount(); i++) oldBondAromaticity[i] = ac.getBond(i).getFlag(CDKConstants.ISAROMATIC);

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
		StructureResonanceGenerator gR1 = new StructureResonanceGenerator();/*according G. should be integrated the breaking bonding*/
		List<IReactionProcess> reactionList1 = gR1.getReactions();
		List<IParameterReact> paramList1 = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList1.add(param);
        HeterolyticCleavagePBReaction reactionHCPB = new HeterolyticCleavagePBReaction();
		reactionHCPB.setParameterList(paramList1);
		reactionList1.add(new SharingAnionReaction());
		Iterator<IReactionProcess> itReaction = reactionList1.iterator();
		while(itReaction.hasNext()){
	        IReactionProcess reaction = itReaction.next();
	        reaction.setParameterList(paramList1);
		}
		gR1.setReactions(reactionList1);
		
		StructureResonanceGenerator gR2 = new StructureResonanceGenerator();/*according G. should be integrated the breaking bonding*/
		gR2.setMaximalStructures(MX_RESON);
		List<IReactionProcess> reactionList2 = gR2.getReactions();
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact paramA = new SetReactionCenter();
        paramA.setParameter(Boolean.TRUE);
        paramList.add(paramA);
        reactionList2.add(new HeterolyticCleavagePBReaction());
		reactionList2.add(new SharingAnionReaction());
		itReaction = reactionList2.iterator();
		while(itReaction.hasNext()){
	        IReactionProcess reaction = itReaction.next();
	        reaction.setParameterList(paramList);
		}
		gR2.setReactions(reactionList2);
		
		/*find resonance containers, which eliminates the repetitions*/
		StructureResonanceGenerator gRN = new StructureResonanceGenerator();/*according G. should be integrated the breaking bonding*/
		IAtomContainerSet acSet = gRN.getContainers((IMolecule) removingFlagsAromaticity(ac));
//		IAtomContainerSet acSet = ConjugatedPiSystemsDetector.detect(removingFlagsAromaticity(ac));
		
		IMoleculeSet iSet = ac.getBuilder().newMoleculeSet();
		iSet.addAtomContainer(ac);
		
		if(acSet != null)
		for(Iterator<IAtomContainer> it = acSet.atomContainers().iterator(); it.hasNext();){
			IAtomContainer container = it.next();
			ac = setFlags(container, ac, true);
			
			// Aromatic don't brake its double bond homolitically 
			if(CDKHueckelAromaticityDetector.detectAromaticity(ac))
				reactionList1.remove(reactionHCPB);
			else
				reactionList1.add(reactionHCPB);
			
			IMoleculeSet a = gR1.getStructures((IMolecule) removingFlagsAromaticity(ac));
			if(a.getAtomContainerCount() > 1){
				for(int j = 1; j < a.getAtomContainerCount(); j ++){ // the first is already added
					iSet.addAtomContainer(a.getMolecule(j));			
				}
			}
			ac = setFlags(container, ac, false);

			/*processing for which bonds which are not in resonance*/
			for(int number = 0; number < ac.getBondCount() ; number++){
				IAtomContainer aa = setAntiFlags(container,ac, number,true);
				if(aa != null){
					IMoleculeSet ab = gR2.getStructures((IMolecule) aa);
					if(ab.getAtomContainerCount() > 1)
						for(int j = 1; j < ab.getAtomContainerCount(); j ++){ // the first is already added
							iSet.addAtomContainer(ab.getMolecule(j));			
						}
					ac = setAntiFlags(container, aa, number, false);
				}
			}
		}
		
		/* detect hyperconjugation interactions */
		setHI = getHyperconjugationInteractions(ac, iSet);

		if(setHI != null) {
			if(	setHI.getAtomContainerCount() != 0)
				iSet.add(setHI);
			logger.debug("setHI: "+iSet.getAtomContainerCount());
		}
        if (iSet.getAtomContainerCount() < 2) {
            for (int i = 0; i < ac.getAtomCount(); i++)
                ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, oldAtomAromaticity[i]);
            for (int i = 0; i < ac.getBondCount(); i++)
                ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, oldBondAromaticity[i]);
            return ac;
        }

		
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
			logger.debug(", W:"+Wt[i-1]);
			try {
				acCloned = (IAtomContainer)iSet.getAtomContainer(i).clone();
				
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
//		for (int iter = 0; iter < 1; iter++) {
			for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
				IAtomContainer iac = iSet.getAtomContainer(k);
				double[] electronegativity = new double[2];
				int count = 0;
				int atom1 = 0;
				int atom2 = 0;
				for (int j = 0; j < iac.getAtomCount(); j++) {
					if(count == 2)/*The change of sign is product of only two atoms, is not true*/
						break;
					if(iac.getAtom(j).getFlag(ISCHANGEDFC)){
						logger.debug("Atom: "+j+", S:"+iac.getAtom(j).getSymbol()+", C:"+iac.getAtom(j).getFormalCharge());
						if(count == 0)
							atom1 = j;
						else 
							atom2 = j;
						
						double q1 = gasteigerFactors[k][STEP_SIZE * j + j + 5];
						electronegativity[count] = gasteigerFactors[k][STEP_SIZE * j + j + 2] * q1 * q1 + gasteigerFactors[k][STEP_SIZE * j + j + 1] * q1 + gasteigerFactors[k][STEP_SIZE * j + j];
						logger.debug("e:"+electronegativity[count] +",q1: "+q1+", c:"+gasteigerFactors[k][STEP_SIZE * j + j + 2] +", b:"+gasteigerFactors[k][STEP_SIZE * j + j + 1]  + ", a:"+gasteigerFactors[k][STEP_SIZE * j + j]);
						count++;
					}
				}
				logger.debug("Atom1:"+atom1+",Atom2:"+atom2);
				/*diferency of electronegativity 1 lower*/
				double max1 = Math.max(electronegativity[0], electronegativity[1]);
				double min1 = Math.min(electronegativity[0], electronegativity[1]);
				double DX = 1.0;
				if(electronegativity[0] < electronegativity[1])
					DX = gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 3];
				else
					DX = gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 3];
					
				double Dq = (max1-min1)/DX;
				logger.debug("Dq : "+Dq+ " = ("+ max1+"-"+min1+")/"+DX);
				double epN1 = getElectrostaticPotentialN(iac,atom1,gasteigerFactors[k]);
				double epN2 = getElectrostaticPotentialN(iac,atom2,gasteigerFactors[k]);
				double SumQN = Math.abs(epN1 - epN2);
				logger.debug("sum("+SumQN+") = ("+epN1+") - ("+epN2+")");
				
				/* electronic weight*/
				double WE = Dq + fE*SumQN;
				logger.debug("WE : "+WE+" = Dq("+Dq+")+fE("+fE+")*SumQN("+SumQN);
				int iTE = iter+1;
				
				/* total topological*/
				double W = WE*Wt[k-1]*fS/(iTE);
				logger.debug("W : "+W+" = WE("+WE+")*Wt("+Wt[k-1]+")*fS("+fS+")/iter("+iTE+"), atoms: "+atom1+", "+atom2);
				
				/*iac == new structure, ac == old structure*/
				/* atom1 */
				if(iac.getAtom(atom1).getFormalCharge() == 0){
					if(ac.getAtom(atom1).getFormalCharge() < 0){
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = -1*W;
					}else{
						gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = W;
					}
				}else if(iac.getAtom(atom1).getFormalCharge() > 0){
					gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = W;
				}else{
					gasteigerFactors[k][STEP_SIZE * atom1 + atom1 + 5] = -1*W;
				}
				/* atom2*/
				if(iac.getAtom(atom2).getFormalCharge() == 0){
					if(ac.getAtom(atom2).getFormalCharge() < 0){
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = -1*W;
					}else{
						gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = W;
					}
				}else if(iac.getAtom(atom2).getFormalCharge() > 0){
					gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = W;
				}else{
					gasteigerFactors[k][STEP_SIZE * atom2 + atom2 + 5] = -1*W;
				}
				
			}
			for(int k = 1 ; k < iSet.getAtomContainerCount() ; k++){
				
				for (int i = 0; i < ac.getAtomCount(); i++) 
					if(iSet.getAtomContainer(k).getAtom(i).getFlag(ISCHANGEDFC)){
						double charge = ac.getAtom(i).getCharge();
						double chargeT = 0.0;
						chargeT = charge + gasteigerFactors[k][STEP_SIZE * i + i + 5];
						logger.debug("i<|"+ac.getAtom(i).getSymbol()+", "+chargeT+"=c:" +charge + "+g: "+gasteigerFactors[k][STEP_SIZE * i + i + 5]);
						ac.getAtom(i).setCharge(chargeT);
					}
			}
			
		}// iterations
		logger.debug("final");

        // before getting back we should set back the aromatic flags
        for (int i = 0; i < ac.getAtomCount(); i++) ac.getAtom(i).setFlag(CDKConstants.ISAROMATIC, oldAtomAromaticity[i]);
        for (int i = 0; i < ac.getBondCount(); i++) ac.getBond(i).setFlag(CDKConstants.ISAROMATIC, oldBondAromaticity[i]);

        return ac;
		
	}

    @TestMethod("testCalculateCharges_IAtomContainer")
    public void calculateCharges(IAtomContainer container) throws CDKException {
    	try {
	        this.assignGasteigerPiPartialCharges(container, true);
        } catch (Exception exception) {
	        throw new CDKException(
	        	"Could not calculate Gasteiger-Marsili PEPE charges: " +
	        	exception.getMessage(), exception
	        );
        }
    }

	/**
	 * remove the aromaticity flags.
	 * 
	 * @param ac The IAtomContainer to remove flags
	 * @return   The IATomContainer with the flags removed
	 */
	private IAtomContainer removingFlagsAromaticity(IAtomContainer ac) {
		Iterator<IAtom> atoms = ac.atoms().iterator();
		while (atoms.hasNext()) atoms.next().setFlag(CDKConstants.ISAROMATIC, false);
		Iterator<IBond> bonds = ac.bonds().iterator();
		while (bonds.hasNext()) bonds.next().setFlag(CDKConstants.ISAROMATIC, false);
		return ac;
	}
	/**
	 * Set the Flags to atoms and bonds from an atomContainer.
	 * 
	 * @param container Container with the flags
	 * @param ac        Container to put the flags   
	 * @param b         True, if the the flag is true
	 * @return          Container with added flags
	 */
	private IAtomContainer setFlags(IAtomContainer container,
			IAtomContainer ac, boolean b) {
		for(Iterator<IAtom> it = container.atoms().iterator(); it.hasNext();){
			int positionA = ac.getAtomNumber(it.next());
			ac.getAtom(positionA).setFlag(CDKConstants.REACTIVE_CENTER,b);
		}
		for(Iterator<IBond> it = container.bonds().iterator(); it.hasNext();){
			int positionB = ac.getBondNumber(it.next());
			ac.getBond(positionB).setFlag(CDKConstants.REACTIVE_CENTER,b);
		
		}
		return ac;
	}

	/**
	 * Set the Flags to atoms and bonds which are not contained
	 * in an atomContainer.
	 * 
	 * @param container Container with the flags
	 * @param ac        Container to put the flags   
	 * @param b         True, if the the flag is true
	 * @return          Container with added flags
	 */
	private IAtomContainer setAntiFlags(IAtomContainer container,
			IAtomContainer ac, int number, boolean b) {
		IBond bond = ac.getBond(number);
		if(!container.contains(bond)){
			bond.setFlag(CDKConstants.REACTIVE_CENTER,b);
			bond.getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,b);
			bond.getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,b);
		}else
			return null;
		return ac;
	}
	
	/**
	 * get the possibles structures after hyperconjugation interactions for bonds which
	 * do not belong to any resonance structure.
	 * 
	 * @param ac IAtomContainer
	 * @return IAtomContainerSet
	 * @throws CDKException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	private IAtomContainerSet getHyperconjugationInteractions(IAtomContainer ac, IAtomContainerSet iSet) throws IOException, ClassNotFoundException, CDKException {
		IAtomContainerSet set = ac.getBuilder().newAtomContainerSet();
        IReactionProcess type = new HeterolyticCleavageSBReaction();
        cleanFlagReactiveCenter(ac);
        boolean found = false; /* control obtained containers */
		IMoleculeSet setOfReactants = ac.getBuilder().newMoleculeSet();
		/* search of reactive center.*/
		out:
		for(int i = 0 ; i < ac.getBondCount() ; i++){
			if(ac.getBond(i).getOrder() != IBond.Order.SINGLE ){
				for(int j = 0 ; j < iSet.getAtomContainerCount(); j++){
    				IAtomContainer ati = iSet.getAtomContainer(j);
    				if(!ati.equals(ac))
    				for(int k = 0; k < ati.getBondCount(); k++){
    					IAtom a0 = ati.getBond(k).getAtom(0);
    					IAtom a1 = ati.getBond(k).getAtom(1);
    					if(!a0.getSymbol().equals("H") || !a1.getSymbol().equals("H"))
    					if((a0.getID().equals(ac.getBond(i).getAtom(0).getID()) &&
    							a1.getID().equals(ac.getBond(i).getAtom(1).getID())) ||
							(a1.getID().equals(ac.getBond(i).getAtom(0).getID()) &&
	    							a0.getID().equals(ac.getBond(i).getAtom(1).getID()))){
    						if(a0.getFormalCharge() != 0 || a1.getFormalCharge() != 0)
    							continue out;
    					}
    				}
				}
				ac.getBond(i).getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,true);
				ac.getBond(i).getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,true);
				ac.getBond(i).setFlag(CDKConstants.REACTIVE_CENTER,true);
				found = true;
			}
		}
		if(!found)
			return null;
		
		
		setOfReactants.addMolecule((IMolecule) ac);
		
		List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        type.setParameterList(paramList);
        IReactionSet setOfReactions = type.initiate(setOfReactants, null);
        for(int i = 0; i < setOfReactions.getReactionCount(); i++){
        	type = new HyperconjugationReaction();
    		IMoleculeSet setOfM2 = ac.getBuilder().newMoleculeSet();
    		IMolecule mol= setOfReactions.getReaction(i).getProducts().getMolecule(0);
    		for(int k = 0; k < mol.getBondCount(); k++){
    			mol.getBond(k).setFlag(CDKConstants.REACTIVE_CENTER,false);
    			mol.getBond(k).getAtom(0).setFlag(CDKConstants.REACTIVE_CENTER,false);
    			mol.getBond(k).getAtom(1).setFlag(CDKConstants.REACTIVE_CENTER,false);
    		}
    		setOfM2.addMolecule((IMolecule) mol);
    		List<IParameterReact> paramList2 = new ArrayList<IParameterReact>();
    	    IParameterReact param2 = new SetReactionCenter();
            param2.setParameter(Boolean.FALSE);
            paramList2.add(param);
            type.setParameterList(paramList2);
            IReactionSet setOfReactions2 = type.initiate(setOfM2, null);
			if(setOfReactions2.getReactionCount() > 0){
				
			IMolecule react = setOfReactions2.getReaction(0).getReactants().getMolecule(0);

			set.addAtomContainer(react);
			}
        }

		return set;
	}
	/**
	 * get the electrostatic potential of the neighbours of a atom.
	 *  
	 * @param ac   The IAtomContainer to study
	 * @param ds 
	 * @param atom1 The position of the IAtom to study
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

			List<IAtom> atoms = ac.getConnectedAtomsList(ac.getAtom(atom1));
            for (IAtom atom : atoms) {
                double covalentradius = 0;
                String symbol = atom.getSymbol();
                IAtomType type = factory.getAtomType(symbol);
                covalentradius = type.getCovalentRadius();

                double charge = ds[STEP_SIZE * atom1 + atom1 + 5];
                logger.debug("sum_("+sum+") = CFC("+CoulombForceConstant+")*charge("+charge+"/ret("+covalentradius);
                sum += CoulombForceConstant * charge / (covalentradius * covalentradius);
            }
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
				if(bond.getAtom(0).getFormalCharge() != 0.0 && bond.getAtom(1).getFormalCharge() != 0.0){
					fQ = 0.25;
					break;
				}
			}
		}
		/*factor, if the number of covalents bonds is decreased*/
		double fB = 1.0;
		
		int numBond1 = 0;
		int numBond2 = 0;
        for (int i = 0; i < atomContainer.getBondCount(); i++) {
            if (atomContainer.getBond(i).getOrder() == IBond.Order.DOUBLE)
                numBond1 += 1;
            if (ac.getBond(i).getOrder() == IBond.Order.DOUBLE)
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
			if(CDKHueckelAromaticityDetector.detectAromaticity(ac))
				if(!CDKHueckelAromaticityDetector.detectAromaticity(atomContainer))
						fA = 0.3;
		} catch (CDKException e) {
			e.printStackTrace();
		}
		logger.debug("return "+fQ*fB*fPlus*fA+"= sp:"+fQ+", dc:"+fB+", fPlus:"+fPlus+", fA:"+fA);
		
		return fQ*fB*fPlus*fA;
	}
	
	/**
	 *  Get the StepSize attribute of the GasteigerMarsiliPartialCharges
	 *  object
	 *
	 *@return STEP_SIZE
	 */
    @TestMethod("testGetStepSize")
	public int getStepSize(){
		return STEP_SIZE;
	}
    /**
     *  Set the StepSize attribute of the GasteigerMarsiliPartialCharges
     *  object
     *
     *@param step
     */
    @TestMethod("testSetStepSize")
    public void setStepSize(int step){
        STEP_SIZE = step;
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
						if(ac.getMaximumBondOrder(ac.getAtom(i)) == IBond.Order.SINGLE){
							factors[0] = 10.0;
							factors[1] = 13.86;
							factors[2] = 9.68;
						}else {
							factors[0] = 7.91;
							factors[1] = 14.76;
							factors[2] = 6.85;
						}
				} else if (AtomSymbol.equals("N")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) != IBond.Order.SINGLE){
						factors[0] = 7.95;/*7.95*/
						factors[1] = 9.73;/*9.73*/
						factors[2] = 2.67;/*2.67*/
					}else {
						factors[0] = 4.54;/*4.54*//*5.5*/
						factors[1] = 11.86;/*11.86*//*10.86*/
						factors[2] = 7.32;/*7.32*//*7.99*/
					}
				} else if (AtomSymbol.equals("S")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) == IBond.Order.SINGLE){
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
    @TestMethod("testAssignrPiMarsilliFactors_IAtomContainerSet")
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
						if(ac.getMaximumBondOrder(ac.getAtom(i)) != IBond.Order.SINGLE){
							factors[0] = 11.2;/*11.2-10.0*/
							factors[1] = 13.24;/*13.24-13.86*/
							factors[2] = 9.68;
						}else {
							factors[0] = 7.91;
							factors[1] = 14.76;
							factors[2] = 6.85;
						}
				} else if (AtomSymbol.equals("N")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) != IBond.Order.SINGLE){
						
						factors[0] = 8.95;/*7.95*/
						factors[1] = 9.73;/*9.73*/
						factors[2] = 2.67;/*2.67*/
					}else {
						factors[0] = 4.54;
						factors[1] = 11.86;
						factors[2] = 7.32;
					}
				} else if (AtomSymbol.equals("P")) {// <--No correct
					if(ac.getMaximumBondOrder(ac.getAtom(i)) != IBond.Order.SINGLE){
						factors[0] = 10.73;// <--No correct
						factors[1] = 11.16;// <--No correct
						factors[2] = 6.81;// <--No correct
					}else {
						factors[0] = 9.60;// <--No correct
						factors[1] = 13.32;// <--No correct
						factors[2] = 2.72;// <--No correct
					}
				} else if (AtomSymbol.equals("S")) {
					if(ac.getMaximumBondOrder(ac.getAtom(i)) != IBond.Order.SINGLE){
						
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
	/**
     * clean the flags CDKConstants.REACTIVE_CENTER from the molecule
     * 
	 * @param ac
	 */
	private void cleanFlagReactiveCenter(IAtomContainer ac){
		for(int j = 0 ; j < ac.getAtomCount(); j++)
			ac.getAtom(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
		for(int j = 0 ; j < ac.getBondCount(); j++)
			ac.getBond(j).setFlag(CDKConstants.REACTIVE_CENTER, false);
	}
}


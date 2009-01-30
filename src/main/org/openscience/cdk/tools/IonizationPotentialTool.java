/* $Revision: 11004 $ $Author: miguelrojasch $ $Date: 2008-05-15 15:27:25 +0200 (Thu, 15 May 2008) $
 *
 * Copyright (C) 2008  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.tools;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.charges.Electronegativity;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.charges.GasteigerPEPEPartialCharges;
import org.openscience.cdk.charges.PiElectronegativity;
import org.openscience.cdk.charges.Polarizability;
import org.openscience.cdk.charges.StabilizationCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.reaction.IReactionProcess;
import org.openscience.cdk.reaction.type.ElectronImpactNBEReaction;
import org.openscience.cdk.reaction.type.parameters.IParameterReact;
import org.openscience.cdk.reaction.type.parameters.SetReactionCenter;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * <p>This class contains the necessary information to predict ionization
 * potential energy. It contains the models and the families classification.
 * It is used as IPAtomicLearningDescriptor as the IPMolecularLearningDescriptor.
 * <p>
 *  
 * @author       Miguel Rojas
 * @cdk.created  2008-5-15
 * @cdk.module   ionpot
 * 
 * @see org.openscience.cdk.qsar.descriptors.atomic.IPAtomicLearningDescriptor
 * @see org.openscience.cdk.qsar.descriptors.molecular.IPMolecularLearningDescriptor
 */
@TestClass("org.openscience.cdk.test.tools.IonizationPotentialTest")
public class IonizationPotentialTool {
	
	/**
	 * Method which is predict the Ionization Potential from given atom.
	 * 
	 * @param container The IAtomContainer where is contained the IAtom
	 * @param atom      The IAtom to prediction the IP
	 * @return          The value in eV
	 */
	public static double predictIP(IAtomContainer container, IAtom atom) throws CDKException {
		double value = 0;
		
		// at least one lone pair orbital is necessary to ionize
		if(container.getConnectedLonePairsCount(atom) == 0)
        	return value;
		
		// control if the IAtom belongs in some family
		if(familyHalogen(atom))
			value = getDTHalogenF(getQSARs(container,atom));
		else if(familyOxygen(atom))
			value = getDTOxygenF(getQSARs(container,atom));
		else if(familyNitrogen(atom))
			value = getDTNitrogenF(getQSARs(container,atom));
		
		return value;
	}
	/**
	 * Method which is predict the Ionization Potential from given atom.
	 * 
	 * @param container The IAtomContainer where is contained the IAtom
	 * @param bond      The IBond to prediction the IP
	 * @return          The value in eV
	 */
	public static double predictIP(IAtomContainer container, IBond bond) throws CDKException {
		double value = 0;
		
		if(bond.equals(IBond.Order.SINGLE))
			return value;
		
		//if some of the atoms belongs to some of the heteroatom family than
		// it can not ionized
		for(int i = 0; i < 2; i++){
			IAtom atom = bond.getAtom(i);
			if(familyHalogen(atom))
				return value;
			else if(familyOxygen(atom))
				return value;
			else if(familyNitrogen(atom))
				return value;
		}
		
		if(!familyBond(container,bond))
			return value;
		
		return getDTBondF(getQSARs(container,bond));
	}
	/**
	 * Looking if the IAtom belongs to the halogen family.
	 * The IAtoms are F, Cl, Br, I.
	 * 
	 * @param  atom  The IAtom 
	 * @return       True, if it belongs
	 */
	private static boolean familyHalogen(IAtom atom) {
		String symbol = atom.getSymbol();
		if(symbol.equals("F") || 
				symbol.equals("Cl") ||
				symbol.equals("Br") ||
				symbol.equals("I") )
			return true;
		else return false;
	}

	/**
	 * Looking if the Atom belongs to the oxygen family.
	 * The IAtoms are O, S, Se, Te.
	 * 
	 * @param  atom  The IAtom 
	 * @return       True, if it belongs
	 */
	private static boolean familyOxygen(IAtom atom) {
		String symbol = atom.getSymbol();
		if(symbol.equals("O") || 
				symbol.equals("S") ||
				symbol.equals("Se") ||
				symbol.equals("Te") )
			return true;
		else return false;
	}
	/**
	 * Looking if the Atom belongs to the nitrogen family.
	 * The IAtoms are N, P, As, Sb.
	 * 
	 * @param  atom  The IAtom 
	 * @return       True, if it belongs
	 */
	private static boolean familyNitrogen(IAtom atom) {
		String symbol = atom.getSymbol();
		if(symbol.equals("N") || 
				symbol.equals("P") ||
				symbol.equals("As") ||
				symbol.equals("Sb") )
			return true;
		else return false;
	}
	/**
	 * Looking if the Bond belongs to the bond family.
	 * Not in resosance with other heteroatoms.
	 * 
	 * @param  container The IAtomContainer
	 * @param  bond      The IBond
	 * @return           True, if it belongs
	 */
	private static boolean familyBond(IAtomContainer container, IBond bond) {
		List<String> normalAt = new ArrayList<String>();
		normalAt.add("C");
		normalAt.add("H");

		if(getDoubleBondNumber(container) > 30) // taking to long
        	return false;
		
		StructureResonanceGenerator gRN = new StructureResonanceGenerator();
		IAtomContainer ac = gRN.getContainer((IMolecule) container, bond);

		if(ac == null)
			return true;
		
		if(getDoubleBondNumber(ac) > 15) // taking to long
        	return false;
		
        for(IAtom atom : container.atoms()){
			if(!normalAt.contains(atom.getSymbol()))
				if(ac.contains(atom))
					return false;
		}
		
		return true;
	}
	/**
	 * Extract the number of bond with superior ordre.
	 * 
	 * @param container The IAtomContainer
	 * @return          The number
	 */
	private static int getDoubleBondNumber(IAtomContainer container) {
		int doubleNumber = 0;
		for (IBond bond : container.bonds()) {
			if(bond.getOrder().equals(IBond.Order.DOUBLE) || bond.getOrder().equals(IBond.Order.TRIPLE))
				doubleNumber++;
		}
		return doubleNumber;
	}
	/**
	 * Get the results of 7 qsar descriptors been applied. They are:
	 * Electronegativity, 
	 * GasteigerMarsiliPartialCharges,
	 * GasteigerPEPEPartialCharges,
	 * Polarizability,
	 * StabilizationCharge,
	 * Number of Atom in resonance
	 * if the container in resonance is aromatic.
	 * 
	 * @param container  The IAtomContainer which contain the IAtom
	 * @param atom       The IAtom to calculate
	 * @return           An Array containing the results
	 * @throws Exception 
	 */
	public static double[] getQSARs(IAtomContainer container,  IAtom atom) throws CDKException {
		Electronegativity electronegativity = new Electronegativity();
		PiElectronegativity pielectronegativity = new PiElectronegativity();
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
		GasteigerPEPEPartialCharges pepe = new GasteigerPEPEPartialCharges();
		Polarizability pol = new Polarizability();
		StabilizationCharges stabil = new StabilizationCharges();
		StructureResonanceGenerator gRI = new StructureResonanceGenerator();

		IAtomContainer product = initiateIonization(container, atom);
		
		double[] results = new double[8];
		// sigmaElectronegativity
		results[0] = electronegativity.calculateSigmaElectronegativity(container, atom);
		// piElectronegativity
		results[1] = pielectronegativity.calculatePiElectronegativity(container, atom);
		// partialSigmaCharge
		try {
			peoe.assignGasteigerMarsiliSigmaPartialCharges(container, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		results[2] = atom.getCharge();
		// partialPiCharge
		for (int i=0; i < container.getAtomCount(); i++)
			container.getAtom(i).setCharge(0.0);
    	try {
			pepe.assignGasteigerPiPartialCharges(container, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
        results[3] = atom.getCharge();
        // effectiveAtomicPolarizability
		results[4] = pol.calculateGHEffectiveAtomPolarizability(container,atom,100, true);

		int position = container.getAtomNumber(atom);
		if(product != null)
			results[5] = stabil.calculatePositive(product, product.getAtom(position));
		else
			results[5] = 0.0;
		// numberResonance
		IAtomContainer acR = gRI.getContainer((IMolecule) container, atom);
		if(acR != null){
			results[6] = acR.getAtomCount();
			// numberAromaticAtoms
//			boolean isAromatic = CDKHueckelAromaticityDetector.detectAromaticity(container);
			IRingSet ringSet = new SSSRFinder(container).findSSSR();
			RingSetManipulator.markAromaticRings(ringSet);
			int aromRingCount = 0;			
			for (IAtomContainer ring : ringSet.atomContainers()) {
				if (ring.getFlag(CDKConstants.ISAROMATIC)) aromRingCount++;
			}
		    results[7] = aromRingCount;
		}else{
			results[6] = 0;
	        results[7] = 0;
		}
		
		return results;
	}
	/**
	 * Get the results of 7 qsar descriptors been applied. They are:
	 * Electronegativity, 
	 * GasteigerMarsiliPartialCharges,
	 * GasteigerPEPEPartialCharges,
	 * Polarizability,
	 * StabilizationCharge,
	 * Number of Atom in resonance
	 * if the container in resonance is aromatic.
	 * 
	 * @param container  The IAtomContainer which contain the IAtom
	 * @param bond       The IBond to calculate
	 * @return           An Array containing the results
	 * @throws Exception 
	 */
	public static double[] getQSARs(IAtomContainer container,  IBond bond) throws CDKException {
		Electronegativity electronegativity = new Electronegativity();
		PiElectronegativity pielectronegativity = new PiElectronegativity();
		GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
		GasteigerPEPEPartialCharges pepe = new GasteigerPEPEPartialCharges();
		Polarizability pol = new Polarizability();
		StabilizationCharges stabil = new StabilizationCharges();
		StructureResonanceGenerator gRI = new StructureResonanceGenerator();

		double[] results = new double[7];
		
		for(int ia = 0 ; ia < 2 ; ia++){
			IAtom atom = bond.getAtom(ia);
			
			IAtomContainer product = initiateIonization(container, atom);
			
			// sigmaElectronegativity
			results[0] += electronegativity.calculateSigmaElectronegativity(container, atom);
			// piElectronegativity
			results[1] += pielectronegativity.calculatePiElectronegativity(container, atom);
			// partialSigmaCharge
			try {
				peoe.assignGasteigerMarsiliSigmaPartialCharges(container, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			results[2] += atom.getCharge();
			// partialPiCharge
			for (int i=0; i < container.getAtomCount(); i++)
				container.getAtom(i).setCharge(0.0);
	    	try {
				pepe.assignGasteigerPiPartialCharges(container, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        results[3] += atom.getCharge();
	        // effectiveAtomicPolarizability
			results[4] += pol.calculateGHEffectiveAtomPolarizability(container,atom,100, true);
	
			int position = container.getAtomNumber(atom);
			if(product != null)
				results[5] += stabil.calculatePositive(product, product.getAtom(position));
			else
				results[5] += 0.0;
			// numberResonance
			IAtomContainer acR = gRI.getContainer((IMolecule) container, atom);
			if(acR != null){
				results[6] += acR.getAtomCount();
				// numberAromaticAtoms
	//			boolean isAromatic = CDKHueckelAromaticityDetector.detectAromaticity(container);
	//			if(isAromatic)
	//		        results[7] += 0.1;
			}else{
				results[6] += 0;
	//	        results[7] += 0;
			}
		}		
		for(int i = 0; i < results.length; i++)
			results[i] = results[i]/2;
		return results;
	}
	/**
	 * Get the prediction result for the Halogen family given a series of values.
	 * It is based on 167 instances and 9 attributes(descriptors) using the Linear Regression Model
	 * with result of Root mean squared error 0.5817 with a cross validation of 10 folds.
	 * 
	 * @param resultsH      Array which contains the results of each descriptor
	 * @return              The result
	 */
	private static double getDTHalogenF(double[] resultsH) {
		double result = 0.0;
		double SE = resultsH[0];
		double PE = resultsH[1];
		double PSC = resultsH[2];
		double PIC  = resultsH[3];
		double ETP  = resultsH[4];
		double SPC  = resultsH[5];
		double COUNTR  = resultsH[6];
		double COUNTAr  = resultsH[7];
		
//		System.out.println("SE : "+SE+", PE : "+PE+", PSC : "+PSC+", PIC : "+PIC+", ETP : "+ETP+", SPC : "+SPC+", COUNTR : "+COUNTR+", COUNTAr : "+COUNTAr);
		//model leastMedSq
		result = 
		      0.272  * SE +
		      13.5814 * PSC +
		      -4.4765 * PIC +
		      -0.4937 * ETP +
		       0.0095 * SPC +
		      -0.3706 * COUNTR +
		       0.5172 * COUNTAr +
		      12.4183
		      ;
		return result;
	}
	/**
	 * Get the prediction result for the Oxygen family given a series of values.
	 * It is based on 368 instances and 9 attributes(descriptors) using the Linear Regression Model
	 * with result of Root mean squared error 0.64 with a cross validation of 10 folds.
	 * 
	 * @param resultsH      Array which contains the results of each descriptor
	 * @return              The result
	 */
	private static double getDTOxygenF(double[] resultsH) {
		double result = 0.0;
		double SE = resultsH[0];
		double PE = resultsH[1];
		double PSC = resultsH[2];
		double PIC  = resultsH[3];
		double ETP  = resultsH[4];
		double SPC  = resultsH[5];
		double COUNTR  = resultsH[6];
		
//		System.out.println("SE : "+SE+", PE : "+PE+", PSC : "+PSC+", PIC : "+PIC+", ETP : "+ETP+", SPC : "+SPC+", COUNTR : "+COUNTR+", COUNTAr : "+COUNTAr);
		result = -0.0118 * SE -0.1859 * PE -0.0752 * PSC -8.1697 * PIC -0.2278 * ETP -0.0041 * SPC + 0.0175 * COUNTR +  11.4835;
		
		return result;
	}
	/**
	 * Get the prediction result for the Nitrogen family given a series of values.
	 * It is based on 244 instances and 9 attributes(descriptors) using the Linear Regression Model
	 * with result of Root mean squared error 0.54 with a cross validation of 10 folds.
	 * 
	 * @param resultsH      Array which contains the results of each descriptor
	 * @return              The result
	 */
	private static double getDTNitrogenF(double[] resultsH) {
		double result = 0.0;
		double SE = resultsH[0];
		double PE = resultsH[1];
		double PSC = resultsH[2];
		double PIC  = resultsH[3];
		double ETP  = resultsH[4];
		double SPC  = resultsH[5];
		double COUNTR  = resultsH[6];
		
//		System.out.println("SE : "+SE+", PE : "+PE+", PSC : "+PSC+", PIC : "+PIC+", ETP : "+ETP+", SPC : "+SPC+", COUNTR : "+COUNTR+", COUNTAr : "+COUNTAr);
		result = 0.4634 * SE + 0.0201 * PE + 1.1897 * PSC -3.598  * PIC -0.2726 * ETP + 0.0006 * SPC -0.0527 * COUNTR + 6.5419;
		return result;
	}
	/**
	 * Get the desicion-tree result for the Halogen family given a series of values.
	 * It is based in 6 qsar descriptors.
	 * 
	 * @param resultsH      Array which contains the results of each descriptor
	 * @return              The result
	 */
	private static double getDTBondF(double[] resultsH) {
		double result = 0.0;
		double SE = resultsH[0];
		double PE = resultsH[1];
		double PSC = resultsH[2];
		double PIC  = resultsH[3];
		double ETP  = resultsH[4];
		double SPC  = resultsH[5];
		double COUNTR  = resultsH[6];
	
//		System.out.println("SE : "+SE+", PE : "+PE+", PSC : "+PSC+", PIC : "+PIC+", ETP : "+ETP+", SPC : "+SPC+", COUNTR : "+COUNTR);
		result =
			 0.1691 * SE +
		      1.1536 * PE +
		     -6.3049 * PSC +
		    -15.2638 * PIC +
		     -0.2456 * ETP +
		     -0.0139 * COUNTR +
		      2.114 	
			;
		
		return result;
	}
	/**
	 * Initiate the reaction ElectronImpactNBE.
	 * 
	 * @param container The IAtomContainer
	 * @param atom      The IAtom to ionize
	 * @return          The product resultant
	 * @throws CDKException 
	 */
	private static IAtomContainer initiateIonization(IAtomContainer container,
			IAtom atom) throws CDKException {
	    IReactionProcess reactionNBE  = new ElectronImpactNBEReaction();

		IMoleculeSet setOfReactants = container.getBuilder().newMoleculeSet();
        setOfReactants.addMolecule((IMolecule) container);

        atom.setFlag(CDKConstants.REACTIVE_CENTER,true);
        List<IParameterReact> paramList = new ArrayList<IParameterReact>();
	    IParameterReact param = new SetReactionCenter();
        param.setParameter(Boolean.TRUE);
        paramList.add(param);
        reactionNBE.setParameterList(paramList);
       
        /* initiate */
		IReactionSet setOfReactions = reactionNBE.initiate(setOfReactants, null);
		atom.setFlag(CDKConstants.REACTIVE_CENTER, false);
		if(setOfReactions != null && setOfReactions.getReactionCount() == 1 && 
				setOfReactions.getReaction(0).getProducts().getAtomContainerCount() == 1)
			return setOfReactions.getReaction(0).getProducts().getAtomContainer(0);
		else
			return null;
	}


}

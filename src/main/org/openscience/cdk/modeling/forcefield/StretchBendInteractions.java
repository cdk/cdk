/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Violeta Labarta <vlabarta@users.sf.net>
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
package org.openscience.cdk.modeling.forcefield;

import java.util.List;
import java.util.Map;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.modeling.builder3d.MMFF94ParametersCall;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PeriodicTablePositionDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *  Stretch-Bend Interaction calculator for the potential energy function.
 *  Include function and derivatives.
 *
 *@author         vlabarta
 *@cdk.created    2005-02-15
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 */
public class StretchBendInteractions {

	String functionShape = " Stretch-Bend Interactions ";

	double mmff94SumEBA = 0;
	GVector gradientMMFF94SumEBA = null;
	GVector order2ndErrorApproximateGradientMMFF94SumEBA = null;
	GVector order5thErrorApproximateGradientMMFF94SumEBA = null;
	GMatrix hessianMMFF94SumEBA = null;
	GVector currentCoordinates = null;
	GVector gradientCurrentCoordinates = null;

	double[][] dDeltarij = null;
	double[][] dDeltarkj = null;
	double[][] dDeltav = null;

	int[][] bondijAtomPosition = null;
	int[][] bondkjAtomPosition = null;
	double[] r0IJ = null;
	double[] r0KJ = null;
	double[] kbaIJK = null;
	double[] kbaKJI = null;
	double[] rij = null;
	double[] rkj = null;
	double[] deltarij = null;
	double[] deltarkj = null;

	BondStretching bs = new BondStretching();
	AngleBending ab = new AngleBending();
	private LoggingTool logger;

	GVector moleculeCurrentCoordinates = null;
	boolean[] changeAtomCoordinates = null;
	int changedCoordinates;


	/**
	 *  Constructor for the StretchBendInteractions object
	 */
	public StretchBendInteractions() {
		logger = new LoggingTool(this);
	}


	/**
	 *  Set MMFF94 reference bond lengths r0IJ and r0JK and stretch-bend
	 *  interaction constants kbaIJK and kbaKJI for each i-j-k angle in the
	 *  molecule.
	 *
	 *@param  molecule       The molecule like an AtomContainer object.
	 *@param  parameterSet   MMFF94 parameters set
	 *@exception  Exception  Description of the Exception
	 */
	public void setMMFF94StretchBendParameters(IAtomContainer molecule, Map parameterSet, boolean angleBendingFlag) throws Exception {

		//logger.debug("setMMFF94StretchBendParameters");
		
		ab.setMMFF94AngleBendingParameters(molecule, parameterSet, angleBendingFlag);

		IAtom[] atomConnected = null;

		List stretchBendInteractionsData = null;
		List bondData = null;
		MMFF94ParametersCall pc = new MMFF94ParametersCall();
		pc.initialize(parameterSet);

		bondijAtomPosition = new int[ab.angleNumber][];
		bondkjAtomPosition = new int[ab.angleNumber][];
		r0IJ = new double[ab.angleNumber];
		r0KJ = new double[ab.angleNumber];
		kbaIJK = new double[ab.angleNumber];
		kbaKJI = new double[ab.angleNumber];

		String strbndType;
		String angleType;
		String bondIJType;
		String bondKJType;

		IBond bondIJ = null;
		IBond bondKJ = null;
		
		IAtomicDescriptor descriptor  = new PeriodicTablePositionDescriptor();
		int iR = 0;
		int jR = 0;
		int kR = 0;

		
		int l = -1;
		for (int j = 0; j < molecule.getAtomCount(); j++) {
			
			atomConnected = AtomContainerManipulator.getAtomArray(molecule.getConnectedAtomsList(molecule.getAtom(j)));
			
			if (atomConnected.length > 1) {
				
				for (int i = 0; i < atomConnected.length; i++) {
					
					for (int k = i + 1; k < atomConnected.length; k++) {
						
						l += 1;
						
						bondIJ = molecule.getBond(atomConnected[i], molecule.getAtom(j));
						bondIJType = bondIJ.getProperty("MMFF94 bond type").toString();
						
						bondKJ = molecule.getBond(atomConnected[k], molecule.getAtom(j));
						bondKJType = bondKJ.getProperty("MMFF94 bond type").toString();
						
						angleType = "0";
						if ((bondIJType == "1") | (bondKJType == "1")) {
							angleType = "1";
						}  
						if ((bondIJType == "1") & (bondKJType == "1")) {
							angleType = "2";
						}  
						
						//logger.debug("bondIJType = " + bondIJType + ", bondKJType = " + bondKJType + ", angleType = " + angleType);
						
						strbndType = "0";
						if ((angleType == "0") & (bondIJType == "0") & (bondKJType == "0")) {strbndType = "0";}
						else if ((angleType == "1") & (bondIJType == "1") & (bondKJType == "0")) {strbndType = "1";}
						else if ((angleType == "1") & (bondIJType == "0") & (bondKJType == "1")) {strbndType = "2";}
						else if ((angleType == "2") & (bondIJType == "1") & (bondKJType == "1")) {strbndType = "3";}
						else if ((angleType == "4") & (bondIJType == "0") & (bondKJType == "0")) {strbndType = "4";}
						else if ((angleType == "3") & (bondIJType == "0") & (bondKJType == "0")) {strbndType = "5";}
						else if ((angleType == "5") & (bondIJType == "1") & (bondKJType == "0")) {strbndType = "6";}
						else if ((angleType == "5") & (bondIJType == "0") & (bondKJType == "1")) {strbndType = "7";}
						else if ((angleType == "6") & (bondIJType == "1") & (bondKJType == "1")) {strbndType = "8";}
						else if ((angleType == "7") & (bondIJType == "1") & (bondKJType == "0")) {strbndType = "9";}
						else if ((angleType == "7") & (bondIJType == "0") & (bondKJType == "1")) {strbndType = "10";}
						else if ((angleType == "8") & (bondIJType == "1") & (bondKJType == "1")) {strbndType = "11";}
						
						//logger.debug("strbnd: " + strbndType + ", " + atomConnected[i].getAtomTypeName() + "(" + molecule.getAtomNumber(atomConnected[i]) + "), " + molecule.getAtom(j).getAtomTypeName() + "(" + molecule.getAtomNumber(molecule.getAtom(j)) + "), " + ((IAtom)atomConnected.get(k)).getAtomTypeName() + "(" + molecule.getAtomNumber((IAtom)atomConnected.get(k)) + ")");
						stretchBendInteractionsData = pc.getBondAngleInteractionData(strbndType, atomConnected[i].getAtomTypeName(), molecule.getAtom(j).getAtomTypeName(), atomConnected[k].getAtomTypeName());
						
						if (stretchBendInteractionsData == null) {
							if (angleType == "1") {
								if (strbndType == "1") {strbndType = "2";}
								else {strbndType = "1";}
								//logger.debug("strbnd: " + strbndType + ", " + ((IAtom)atomConnected.get(i)).getAtomTypeName() + "(" + molecule.getAtomNumber((IAtom)atomConnected.get(i)) + "), " + molecule.getAtom(j).getAtomTypeName() + "(" + molecule.getAtomNumber(molecule.getAtom(j)) + "), " + ((IAtom)atomConnected.get(k)).getAtomTypeName() + "(" + molecule.getAtomNumber((IAtom)atomConnected.get(k)) + ")");
								stretchBendInteractionsData = pc.getBondAngleInteractionData(strbndType, atomConnected[i].getAtomTypeName(), molecule.getAtom(j).getAtomTypeName(), atomConnected[k].getAtomTypeName());
							}
						}
						
						if (stretchBendInteractionsData == null) {
							iR = ((IntegerResult)descriptor.calculate(atomConnected[i],molecule).getValue()).intValue();
							jR = ((IntegerResult)descriptor.calculate(molecule.getAtom(j),molecule).getValue()).intValue();
							kR = ((IntegerResult)descriptor.calculate(atomConnected[k],molecule).getValue()).intValue();
							stretchBendInteractionsData = pc.getDefaultStretchBendData(iR, jR, kR);
						} 

						//logger.debug("stretchBendInteractionsData : " + stretchBendInteractionsData);
						kbaIJK[l] = ((Double) stretchBendInteractionsData.get(0)).doubleValue();
						kbaKJI[l] = ((Double) stretchBendInteractionsData.get(1)).doubleValue();

						//logger.debug("kbaIJK[" + l + "] = " + kbaIJK[l]);
						//logger.debug("kbaKJI[" + l + "] = " + kbaKJI[l]);

						
						bondData = pc.getBondData(bondIJType, atomConnected[i].getAtomTypeName(), molecule.getAtom(j).getAtomTypeName());
						r0IJ[l] = ((Double) bondData.get(0)).doubleValue();
						bondData = pc.getBondData(bondKJType, atomConnected[k].getAtomTypeName(), molecule.getAtom(j).getAtomTypeName());
						r0KJ[l] = ((Double) bondData.get(0)).doubleValue();
						
						bondijAtomPosition[l] = new int[2];
						bondijAtomPosition[l][0] = molecule.getAtomNumber(atomConnected[i]);
						bondijAtomPosition[l][1] = j;
						
						bondkjAtomPosition[l] = new int[2];
						bondkjAtomPosition[l][0] = molecule.getAtomNumber(atomConnected[k]);
						bondkjAtomPosition[l][1] = j;
					}
				}
			}
		}
		rij = new double[ab.angleNumber];
		rkj = new double[ab.angleNumber];
		deltarij = new double[ab.angleNumber];
		deltarkj = new double[ab.angleNumber];
		currentCoordinates = new GVector(3 * molecule.getAtomCount());
		gradientCurrentCoordinates = new GVector(3 * molecule.getAtomCount());
		gradientMMFF94SumEBA = new GVector(3 * molecule.getAtomCount());
		dDeltarij = new double[3 * molecule.getAtomCount()][];
		dDeltarkj = new double[3 * molecule.getAtomCount()][];
		dDeltav = new double[3 * molecule.getAtomCount()][];
		hessianMMFF94SumEBA = new GMatrix(3 * molecule.getAtomCount(), 3 * molecule.getAtomCount());
		for (int i = 0; i < 3 * molecule.getAtomCount(); i++) {
			dDeltarij[i] = new double[ab.angleNumber];
			dDeltarkj[i] = new double[ab.angleNumber];
			dDeltav[i] = new double[ab.angleNumber];
		}
		
		this.moleculeCurrentCoordinates = new GVector(3 * molecule.getAtomCount());
		for (int i=0; i<moleculeCurrentCoordinates.getSize(); i++) {
			this.moleculeCurrentCoordinates.setElement(i,1E10);
		} 

		this.changeAtomCoordinates = new boolean[molecule.getAtomCount()];

	}


	/**
	 *  Calculate the current bond distances rij and rkj for each angle j, and the
	 *  difference with the reference bonds.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setDeltarijAndDeltarkj(GVector coords3d) {

		changedCoordinates = 0;
		//logger.debug("Setting Deltarij and Deltarkj");
		for (int i=0; i < changeAtomCoordinates.length; i++) {
			this.changeAtomCoordinates[i] = false;
		}
		this.moleculeCurrentCoordinates.sub(coords3d);
		
		for (int i = 0; i < this.moleculeCurrentCoordinates.getSize(); i++) {
			//logger.debug("moleculeCurrentCoordinates " + i + " = " + this.moleculeCurrentCoordinates.getElement(i));
			if (Math.abs(this.moleculeCurrentCoordinates.getElement(i)) > 0) {
				changeAtomCoordinates[i/3] = true;
				changedCoordinates = changedCoordinates + 1;
				//logger.debug("changeAtomCoordinates[" + i/3 + "] = " + changeAtomCoordinates[i/3]);
				i = i + (2 - i % 3);
			}
		}

		for (int i = 0; i < ab.angleNumber; i++) {
			if ((changeAtomCoordinates[ab.angleAtomPosition[i][0]] == true) | 
					(changeAtomCoordinates[ab.angleAtomPosition[i][1]] == true))		{
			
				rij[i] = ForceFieldTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, ab.angleAtomPosition[i][1], ab.angleAtomPosition[i][0]);
				deltarij[i] = rij[i] - r0IJ[i];
				//logger.debug("deltarij[" + i + "] = " + deltarij[i]);
			}
			//else {System.out.println("deltarij[" + i + "] was no recalculated");}
			if ((changeAtomCoordinates[ab.angleAtomPosition[i][1]] == true) | 
					(changeAtomCoordinates[ab.angleAtomPosition[i][2]] == true))		{
			
				rkj[i] = ForceFieldTools.distanceBetweenTwoAtomsFrom3xNCoordinates(coords3d, ab.angleAtomPosition[i][1], ab.angleAtomPosition[i][2]);
				deltarkj[i] = rkj[i] - r0KJ[i];
				//logger.debug("deltarkj[" + i + "] = " + deltarkj[i]);
			}
			//else {System.out.println("deltarkj[" + i + "] was no recalculated");}
		}
		/*if 	(changedCoordinates == changeAtomCoordinates.length) {
			for (int m = 0; m < ab.angleNumber; m++) {
				System.out.println("phi[" + m + "] = " + Math.toDegrees(phi[m]));
			}
		}
		*/
		moleculeCurrentCoordinates.set(coords3d);
	}


	
	/**
	 *  Set the MMFF94 stretch-bend interaction term given the atoms cartesian
	 *  coordinates.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setFunctionMMFF94SumEBA(GVector coords3d) {
		//ab.setAngleBendingFlag(false);
		if (currentCoordinates.equals(coords3d)) {
		} else {
			setDeltarijAndDeltarkj(coords3d);
			ab.setDeltav(coords3d);
			mmff94SumEBA = 0;
			for (int j = 0; j < ab.angleNumber; j++) {
				//logger.debug("kbaIJK[" + j + "] = " + kbaIJK[j]);
				//logger.debug("kbaKJI[" + j + "] = " + kbaKJI[j]);
				//logger.debug("deltarij[" + j + "] = " + deltarij[j]);
				//logger.debug("deltarkj[" + j + "] = " + deltarkj[j]);
				//logger.debug("ab.deltav[" + j + "] = " + ab.deltav[j]);
				mmff94SumEBA = mmff94SumEBA + 2.51210 * (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * ab.deltav[j];
				//logger.debug("mmff94SumEBA = " + mmff94SumEBA);
			}
			//mmff94SumEBA = Math.abs(mmff94SumEBA);
			//logger.debug("mmff94SumEBA = " + mmff94SumEBA);
			currentCoordinates.set(coords3d);
		}
	}


	/**
	 *  Get the MMFF94 stretch-bend interaction term.
	 *
	 *@return    MMFF94 stretch-bend interaction term value.
	 */
	public double getFunctionMMFF94SumEBA() {
		return mmff94SumEBA;
	}


	/**
	 *  Evaluate the gradient of the stretch-bend interaction term.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setGradientMMFF94SumEBA(GVector coords3d) {

		if (currentCoordinates.equals(coords3d)) {
		} else {
			setFunctionMMFF94SumEBA(coords3d);
		}

		bs.setBondLengthsFirstDerivative(coords3d, deltarij, bondijAtomPosition);
		dDeltarij = bs.getBondLengthsFirstDerivative();
		bs.setBondLengthsFirstDerivative(coords3d, deltarkj, bondkjAtomPosition);
		dDeltarkj = bs.getBondLengthsFirstDerivative();
		ab.setAngleBending2ndOrderErrorApproximateGradient(coords3d);
		dDeltav = ab.getAngleBending2ndOrderErrorApproximateGradient();

		if (dDeltav == null) {logger.debug("setGradient: dDeltav null");} 
		double sumGradientEBA;
		for (int i = 0; i < gradientMMFF94SumEBA.getSize(); i++) {
			sumGradientEBA = 0;
			for (int j = 0; j < ab.angleNumber; j++) {
				sumGradientEBA = sumGradientEBA + (kbaIJK[j] * dDeltarij[i][j] + kbaKJI[j] * dDeltarkj[i][j]) * ab.deltav[j]
						 + (kbaIJK[j] * deltarij[j] + kbaKJI[j] * deltarkj[j]) * ab.angleBendingOrder2ndErrorApproximateGradient[i][j];
			}
			sumGradientEBA = sumGradientEBA * 2.51210;

			gradientMMFF94SumEBA.setElement(i, sumGradientEBA);
			gradientCurrentCoordinates.set(coords3d);
		}
		//logger.debug("gradientMMFF94SumEBA = " + gradientMMFF94SumEBA);
	}


	/**
	 *  Get the gradient of the stretch-bend interaction term.
	 *
	 *@return    stretch-bend interaction gradient value.
	 */
	public GVector getGradientMMFF94SumEBA() {
		return gradientMMFF94SumEBA;
	}


	/**
	 *  Evaluate a 2nd order approximation of the gradient for the stretch-bend interaction term, 
	 *  given the atoms coordinates.
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set2ndOrderErrorApproximateGradientMMFF94SumEBA(GVector coord3d) {
		order2ndErrorApproximateGradientMMFF94SumEBA = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.33);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		double fInXplusSigma = 0;
		double fInXminusSigma = 0;

		for (int m = 0; m < order2ndErrorApproximateGradientMMFF94SumEBA.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			this.setFunctionMMFF94SumEBA(xplusSigma);
			fInXplusSigma = this.getFunctionMMFF94SumEBA();
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			this.setFunctionMMFF94SumEBA(xminusSigma);
			fInXminusSigma = this.getFunctionMMFF94SumEBA();
			order2ndErrorApproximateGradientMMFF94SumEBA.setElement(m,(fInXplusSigma - fInXminusSigma) / (2 * sigma));
		}
			
		//logger.debug("order2ndErrorApproximateGradientMMFF94SumEBA : " + order2ndErrorApproximateGradientMMFF94SumEBA);
	}


	/**
	 *  Get the 2nd order error approximate gradient for the stretch-bend term.
	 *
	 *
	 *@return           Stretch-bend interaction 2nd order error approximate gradient value.
	 */
	public GVector get2ndOrderErrorApproximateGradientMMFF94SumEBA() {
		return order2ndErrorApproximateGradientMMFF94SumEBA;
	}


	/**
	 *  Evaluate a 5th order error approximation of the gradient, of the stretch-bend interaction term, for a given atoms
	 *  coordinates
	 *
	 *@param  coord3d  Current molecule coordinates.
	 */
	public void set5thOrderErrorApproximateGradientMMFF94SumEBA(GVector coord3d) {
		order5thErrorApproximateGradientMMFF94SumEBA = new GVector(coord3d.getSize());
		double sigma = Math.pow(0.000000000000001,0.2);
		GVector xplusSigma = new GVector(coord3d.getSize());
		GVector xminusSigma = new GVector(coord3d.getSize());
		GVector xplus2Sigma = new GVector(coord3d.getSize());
		GVector xminus2Sigma = new GVector(coord3d.getSize());
		double fInXplusSigma = 0;
		double fInXminusSigma = 0;
		double fInXplus2Sigma = 0;
		double fInXminus2Sigma = 0;
		
		for (int m=0; m < order5thErrorApproximateGradientMMFF94SumEBA.getSize(); m++) {
			xplusSigma.set(coord3d);
			xplusSigma.setElement(m,coord3d.getElement(m) + sigma);
			this.setFunctionMMFF94SumEBA(xplusSigma);
			fInXplusSigma = this.getFunctionMMFF94SumEBA();
			xminusSigma.set(coord3d);
			xminusSigma.setElement(m,coord3d.getElement(m) - sigma);
			this.setFunctionMMFF94SumEBA(xminusSigma);
			fInXminusSigma = this.getFunctionMMFF94SumEBA();
			xplus2Sigma.set(coord3d);
			xplus2Sigma.setElement(m,coord3d.getElement(m) + 2 * sigma);
			this.setFunctionMMFF94SumEBA(xplus2Sigma);
			fInXplus2Sigma = this.getFunctionMMFF94SumEBA();
			xminus2Sigma.set(coord3d);
			xminus2Sigma.setElement(m,coord3d.getElement(m) - 2 * sigma);
			this.setFunctionMMFF94SumEBA(xminus2Sigma);
			fInXminus2Sigma = this.getFunctionMMFF94SumEBA();
			order5thErrorApproximateGradientMMFF94SumEBA.setElement(m, (8 * (fInXplusSigma - fInXminusSigma) - (fInXplus2Sigma - fInXminus2Sigma)) / (12 * sigma));
		}
			
		//logger.debug("order5thErrorApproximateGradientMMFF94SumEBA : " + order5thErrorApproximateGradientMMFF94SumEBA);
	}


	/**
	 *  Get the 5 order approximate gradient of the stretch-bend interaction term.
	 *
	 *@return        stretch-bend interaction 5 order approximate gradient value.
	 */
	public GVector get5thOrderErrorApproximateGradientMMFF94SumEBA() {
		return order5thErrorApproximateGradientMMFF94SumEBA;
	}


	/**
	 *  Evaluate the hessian of the stretch-bend interaction.
	 *
	 *@param  coords3d  Current molecule coordinates.
	 */
	public void setHessianMMFF94SumEBA(GVector coords3d) {

		double[] forHessian = new double[coords3d.getSize() * coords3d.getSize()];

		if (currentCoordinates.equals(coords3d)) {
		} else {
			setFunctionMMFF94SumEBA(coords3d);
		}

		if (dDeltarij == null) {logger.debug("dDeltarij null");} 
		if (gradientCurrentCoordinates.equals(coords3d) == false) {
			bs.setBondLengthsFirstDerivative(coords3d, deltarij, bondijAtomPosition);
			dDeltarij = bs.getBondLengthsFirstDerivative();
		}
		if (dDeltarkj == null) {logger.debug("dDeltarkj null");} 
		if (gradientCurrentCoordinates.equals(coords3d) == false) {
			bs.setBondLengthsFirstDerivative(coords3d, deltarkj, bondkjAtomPosition);
			dDeltarkj = bs.getBondLengthsFirstDerivative();
		}
		if (dDeltav == null) {logger.debug("setHessian: dDeltav null");} 
		if (gradientCurrentCoordinates.equals(coords3d) == false) {
			//logger.debug("ab.setAngleBending2ndOrderErrorApproximateGradient()");
			ab.setAngleBending2ndOrderErrorApproximateGradient(coords3d);
			dDeltav = ab.getAngleBending2ndOrderErrorApproximateGradient();
		}
		
		ab.setAngleBending2ndOrderErrorApproximateHessian(coords3d);
		double[][][] ddDeltav = ab.getAngleBending2ndOrderErrorApproximateHessian();
		bs.setBondLengthsSecondDerivative(coords3d, deltarij, bondijAtomPosition);
		double[][][] ddDeltarij = bs.getBondLengthsSecondDerivative();
		bs.setBondLengthsSecondDerivative(coords3d, deltarkj, bondkjAtomPosition);
		double[][][] ddDeltarkj = bs.getBondLengthsSecondDerivative();
		
		if (dDeltav == null) {logger.debug("setHessian: dDeltav null");} 
		//logger.debug("ab.angleNumber = " + ab.angleNumber);
		double sumHessianEBA;
		int forHessianIndex;
		for (int i = 0; i < coords3d.getSize(); i++) {
			for (int j = 0; j < coords3d.getSize(); j++) {
				forHessianIndex = i*coords3d.getSize()+j;
				sumHessianEBA = 0;
				for (int k = 0; k < ab.angleNumber; k++) {
					sumHessianEBA = sumHessianEBA + (kbaIJK[k] * ddDeltarij[i][j][k] + kbaKJI[k] * ddDeltarkj[i][j][k]) * ab.deltav[k]
						 + (kbaIJK[k] * dDeltarij[j][k] + kbaKJI[k] * dDeltarkj[j][k]) * dDeltav[j][k]
						 + (kbaIJK[k] * dDeltarij[j][k] + kbaKJI[k] * dDeltarkj[j][k]) * dDeltav[j][k]
						 + (kbaIJK[k] * deltarij[k] + kbaKJI[k] * deltarkj[k]) * ddDeltav[i][j][k];
				}
				forHessian[forHessianIndex] = sumHessianEBA;
			}
		}

		hessianMMFF94SumEBA.setSize(coords3d.getSize(), coords3d.getSize());
		hessianMMFF94SumEBA.set(forHessian);
		//logger.debug("hessianMMFF94SumEBA : " + hessianMMFF94SumEBA);
	}


	/**
	 *  Get the hessian of the stretch-bend interaction.
	 *
	 *@return    Hessian value of the stretch-bend interaction term.
	 */
	public GMatrix getHessianMMFF94SumEBA() {
		return hessianMMFF94SumEBA;
	}

}


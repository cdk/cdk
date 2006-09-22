/*  $Revision$ $Author$ $Date$
 *  
 *  Copyright (C) 2004-2006  Matteo Floris <mfe4@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.charges.GasteigerMarsiliPartialCharges;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.MoleculeGraphs;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

/**
 *  This class calculates 5 RDF proton descriptors used in neural networks for H1 NMR shift.
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
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:rdfProtonCalculatedValues
 * @cdk.bug     1183625
 */
public class RDFProtonDescriptor implements IAtomicDescriptor {

	private boolean checkAromaticity = false;
	private IAtomContainer acold=null;
	private IRingSet rs = null;
	private AtomContainerSet acSet=null;
  
	/**
	 *  Constructor for the RDFProtonDescriptor object
	 */
	public RDFProtonDescriptor() { }


	/**
	 *  Gets the specification attribute of the RDFProtonDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#rdfProtonCalculatedValues",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the RDFProtonDescriptor
	 *  object
	 *
	 *@param  params            Parameters are the proton position and a boolean (true if you need to detect aromaticity)
	 *@exception  CDKException  Possible Exceptions
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RDFProtonDescriptor only expects one parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The second parameter must be of type Boolean");
		}
		checkAromaticity = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RDFProtonDescriptor
	 *  object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = Boolean.valueOf(checkAromaticity);
		return params;
	}


	/**
	 *  The method calculates from 0 to 5 array of doubles. If an array is calculated, the relative number in the 
	 *  arrayList result is set to 1. If not, is 0. Example: if the second position of the result is 1, it means
	 *  that the second descriptor (GHRtopol) has been calculated and it is returned as property.
	 *  Calculated descriptors are stored as properties:
	 *  gesteigerGHR, gesteigerGHRtopol, gesteigerGDR, gesteigerGSR, gesteigerG3R.
	 *  Example:
	 *  with (ArrayList)target.getProperty("gasteigerGHRtopol") it is possible to use values stored by the gasteigerGHRtopol
	 *  property.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
	 *@return                   an arrayList with 5 position (GHR, GHRtopol, GDR, GSR, G3R)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer ac) throws CDKException {
    return(calculate(atom, ac, null));
  }
  
  
	/**
	 *  The method calculates from 0 to 5 array of doubles. If an array is calculated, the relative number in the 
	 *  arrayList result is set to 1. If not, is 0. Example: if the second position of the result is 1, it means
	 *  that the second descriptor (GHRtopol) has been calculated and it is returned as property.
	 *  Calculated descriptors are stored as properties:
	 *  gesteigerGHR, gesteigerGHRtopol, gesteigerGDR, gesteigerGSR, gesteigerG3R.
	 *  Example:
	 *  with (ArrayList)target.getProperty("gasteigerGHRtopol") it is possible to use values stored by the gasteigerGHRtopol
	 *  property.
	 *
	 *@param  atom              The IAtom for which the DescriptorValue is requested
     *@param  ac                AtomContainer
     *@param  precalculatedringset You can give an already generated set of all rings for speeding up things.
	 *@return                   an arrayList with 5 position (GHR, GHRtopol, GDR, GSR, G3R)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(IAtom atom, IAtomContainer ac, IRingSet precalculatedringset) throws CDKException {
        int atomPosition = ac.getAtomNumber(atom);
		IntegerArrayResult rdfProtonCalculatedValues = new IntegerArrayResult(5);
		if(atom.getSymbol().equals("H")) {
			
/////////////////////////FIRST SECTION OF MAIN METHOD: DEFINITION OF MAIN VARIABLES
/////////////////////////AND AROMATICITY AND PI-SYSTEM AND RINGS DETECTION
			
			Molecule mol = new Molecule(ac);
      if(ac!=acold){
        acold=ac;
   			// DETECTION OF pi SYSTEMS
        acSet = ConjugatedPiSystemsDetector.detect(mol);
        if(precalculatedringset==null)
          rs = (new AllRingsFinder()).findAllRings(ac);
        else
          rs=precalculatedringset;
        try {
          GasteigerMarsiliPartialCharges peoe = new GasteigerMarsiliPartialCharges();
          peoe.assignGasteigerMarsiliSigmaPartialCharges(mol, true);
        } catch (Exception ex1) {
          throw new CDKException("Problems with assignGasteigerMarsiliPartialCharges due to " + ex1.toString(), ex1);
        }
      }
			if (checkAromaticity) {
				HueckelAromaticityDetector.detectAromaticity(ac, rs, true);
			}
			List rsAtom;
			Ring ring;
			List ringsWithThisBond;
			// SET ISINRING FLAGS FOR BONDS
			org.openscience.cdk.interfaces.IBond[] bondsInContainer = ac.getBonds();		
			for (int z = 0; z < bondsInContainer.length; z++) {
				ringsWithThisBond = rs.getRings(bondsInContainer[z]);
				if (ringsWithThisBond.size() > 0) {
					bondsInContainer[z].setFlag(CDKConstants.ISINRING, true);
				}
			}
			// SET ISINRING FLAGS FOR ATOMS
			org.openscience.cdk.interfaces.IRingSet ringsWithThisAtom;
			
			for (int w = 0; w < ac.getAtomCount(); w++) {
				ringsWithThisAtom = rs.getRings(ac.getAtom(w));
				if (ringsWithThisAtom.getAtomContainerCount() > 0) {
					ac.getAtom(w).setFlag(CDKConstants.ISINRING, true);
				}
			}
			
   		IAtomContainer detected = acSet.getAtomContainer(0);			
			
			// neighboors[0] is the atom joined to the target proton:
			java.util.List neighboors = mol.getConnectedAtomsList(atom);
			IAtom neighbour0 = (IAtom)neighboors.get(0);
			
			// 2', 3', 4', 5', 6', and 7' atoms up to the target are detected:
			List atomsInSecondSphere = mol.getConnectedAtomsList(neighbour0);
			List atomsInThirdSphere = null;
			List atomsInFourthSphere = null;
			List atomsInFifthSphere = null;
			List atomsInSixthSphere = null;
			List atomsInSeventhSphere = null;
			
			// SOME LISTS ARE CREATED FOR STORING OF INTERESTING ATOMS AND BONDS DURING DETECTION
			ArrayList singles = new ArrayList(); // list of any bond not rotatable
			ArrayList doubles = new ArrayList(); // list with only double bonds
			ArrayList atoms = new ArrayList(); // list with all the atoms in spheres
			//atoms.add( new Integer( mol.getAtomNumber(neighboors[0]) ) );
			ArrayList bondsInCycloex = new ArrayList(); // list for bonds in cycloexane-like rings
			
			// 2', 3', 4', 5', 6', and 7' bonds up to the target are detected:
			org.openscience.cdk.interfaces.IBond secondBond; // (remember that first bond is proton bond)
			org.openscience.cdk.interfaces.IBond thirdBond; //
			org.openscience.cdk.interfaces.IBond fourthBond; //
			org.openscience.cdk.interfaces.IBond fifthBond; //
			org.openscience.cdk.interfaces.IBond sixthBond; //
			org.openscience.cdk.interfaces.IBond seventhBond; //
			
			// definition of some variables used in the main FOR loop for detection of interesting atoms and bonds:
			boolean theBondIsInA6MemberedRing; // this is like a flag for bonds which are in cycloexane-like rings (rings with more than 4 at.)
			double bondOrder;
			int bondNumber;
			int sphere;
			
			// THIS MAIN FOR LOOP DETECT RIGID BONDS IN 7 SPHERES:
			for(int a = 0; a < atomsInSecondSphere.size(); a++) {
				IAtom curAtomSecond = (IAtom)atomsInSecondSphere.get(a);
				secondBond = mol.getBond(neighbour0, curAtomSecond);
				if(mol.getAtomNumber(curAtomSecond)!=atomPosition && getIfBondIsNotRotatable(mol, secondBond, detected)) {
					sphere = 2;
					bondOrder = secondBond.getOrder();
					bondNumber = mol.getBondNumber(secondBond);
					theBondIsInA6MemberedRing = false;
					checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomSecond), atoms, sphere, theBondIsInA6MemberedRing);
					atomsInThirdSphere = mol.getConnectedAtomsList(curAtomSecond);
					if(atomsInThirdSphere.size() > 0) {
					for(int b = 0; b < atomsInThirdSphere.size(); b++) {
						IAtom curAtomThird = (IAtom)atomsInThirdSphere.get(b);
						thirdBond = mol.getBond(curAtomThird, curAtomSecond);
						// IF THE ATOMS IS IN THE THIRD SPHERE AND IN A CYCLOEXANE-LIKE RING, IT IS STORED IN THE PROPER LIST:
						if(mol.getAtomNumber(curAtomThird)!=atomPosition && getIfBondIsNotRotatable(mol, thirdBond, detected)) {
							sphere = 3;
							bondOrder = thirdBond.getOrder();
							bondNumber = mol.getBondNumber(thirdBond);
							theBondIsInA6MemberedRing = false;
							
							// if the bond is in a cyclohexane-like ring (a ring with 5 or more atoms, not aromatic)
							// the boolean "theBondIsInA6MemberedRing" is set to true
							if(!thirdBond.getFlag(CDKConstants.ISAROMATIC)) {
								if(!curAtomThird.equals(neighbour0)) {
									rsAtom = rs.getRings(thirdBond);
									for (int f = 0; f < rsAtom.size(); f++) {
										ring = (Ring)rsAtom.get(f);
										if (ring.getRingSize() > 4 && ring.contains(thirdBond)) {
											theBondIsInA6MemberedRing = true;
										}
									}
								}
							}
							checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomThird), atoms, sphere, theBondIsInA6MemberedRing);
							theBondIsInA6MemberedRing = false;
							atomsInFourthSphere = mol.getConnectedAtomsList(curAtomThird);
							if(atomsInFourthSphere.size() > 0) {
							for(int c = 0; c < atomsInFourthSphere.size(); c++) {
								IAtom curAtomFourth = (IAtom)atomsInFourthSphere.get(c);
								fourthBond = mol.getBond(curAtomThird, curAtomFourth);
								if(mol.getAtomNumber(curAtomFourth)!=atomPosition && getIfBondIsNotRotatable(mol, fourthBond, detected)) {
									sphere = 4;
									bondOrder = fourthBond.getOrder();
									bondNumber = mol.getBondNumber(fourthBond);
									theBondIsInA6MemberedRing = false;
									checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomFourth), atoms, sphere, theBondIsInA6MemberedRing);
									atomsInFifthSphere = mol.getConnectedAtomsList(curAtomFourth);
									if(atomsInFifthSphere.size() > 0) {
									for(int d = 0; d < atomsInFifthSphere.size(); d++) {
										IAtom curAtomFifth = (IAtom)atomsInFifthSphere.get(d);
										fifthBond = mol.getBond(curAtomFifth, curAtomFourth);
										if(mol.getAtomNumber(curAtomFifth)!=atomPosition && getIfBondIsNotRotatable(mol, fifthBond, detected)) {
											sphere = 5;
											bondOrder = fifthBond.getOrder();
											bondNumber = mol.getBondNumber(fifthBond);
											theBondIsInA6MemberedRing = false;
											checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomFifth), atoms, sphere, theBondIsInA6MemberedRing);
											atomsInSixthSphere = mol.getConnectedAtomsList(curAtomFifth);
											if(atomsInSixthSphere.size() > 0) {
											for(int e = 0; e < atomsInSixthSphere.size(); e++) {
												IAtom curAtomSixth = (IAtom)atomsInSixthSphere.get(e);
												sixthBond = mol.getBond(curAtomFifth, curAtomSixth);
												if(mol.getAtomNumber(curAtomSixth)!=atomPosition && getIfBondIsNotRotatable(mol, sixthBond, detected)) {
													sphere = 6;
													bondOrder = sixthBond.getOrder();
													bondNumber = mol.getBondNumber(sixthBond);
													theBondIsInA6MemberedRing = false;
													checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomSixth), atoms, sphere, theBondIsInA6MemberedRing);
													atomsInSeventhSphere = mol.getConnectedAtomsList(curAtomSixth);
													if(atomsInSeventhSphere.size() > 0) {
													for(int f = 0; f < atomsInSeventhSphere.size(); f++) {
														IAtom curAtomSeventh = (IAtom)atomsInSeventhSphere.get(f);
														seventhBond = mol.getBond(curAtomSeventh, curAtomSixth);
														if(mol.getAtomNumber(curAtomSeventh)!=atomPosition && getIfBondIsNotRotatable(mol, seventhBond, detected)) {
															sphere = 7;
															bondOrder = seventhBond.getOrder();
															bondNumber = mol.getBondNumber(seventhBond);
															theBondIsInA6MemberedRing = false;
															checkAndStore(bondNumber, bondOrder, singles, doubles, bondsInCycloex, mol.getAtomNumber(curAtomSeventh), atoms, sphere, theBondIsInA6MemberedRing);
														}
													}}
												}
											}}
										}
									}}
								}
							}}
						}
					}}
				}
			}
			
////////////////////// SECOND SECTION
////////////////////// NOW ATOMS AND BONDS ARE DETECTED, THEN WE HAVE SOME LISTS WITH A DESCRIPTION OF THE PROTON ENVIRONMENT

			// some of these variables are used in all descriptors:
			double[] values; // for storage of results of other methods
			double distance;
			double sum;
			double smooth = -20;
			double partial;
			int position;
			double limitInf = 1.4;
			double limitSup = 4;
			double step = (limitSup - limitInf)/15;
			IAtom atom2;
			
///////////////////////THE FIRST CALCULATED DESCRIPTOR IS g(H)r	 WITH PARTIAL CHARGES:
			
			
			if(atoms.size() > 0) {
				ArrayList gHr_function = new ArrayList(15);
				for(double ghr = limitInf; ghr < limitSup; ghr = ghr + step) {
					sum = 0;
					for( int at = 0; at < atoms.size(); at++ ) {
						distance = 0;
						partial = 0;
						Integer thisAtom = (Integer)atoms.get(at);
						position = thisAtom.intValue();
						atom2 = mol.getAtom(position);
						distance = calculateDistanceBetweenTwoAtoms(atom, atom2 );
						partial = atom2.getCharge() * Math.exp( smooth * (Math.pow( (ghr - distance) , 2)));
						sum += partial;
					}
					gHr_function.add(new Double(sum));
					//System.out.println("RDF gr distance prob.: "+sum+ " at distance "+ghr);
				}
				atom.setProperty("gasteigerGHR", new ArrayList(gHr_function));
				rdfProtonCalculatedValues.add(1);
			}
			else rdfProtonCalculatedValues.add(0);
			//System.out.println("----------------------------");

///////////////////////THE SECOND CALCULATED DESCRIPTOR IS g(H)r TOPOLOGICAL WITH SUM OF BOND LENGTHS

			
			distance = 0;
			sum = 0;
			smooth = -20;
			position = 0;
			atom2 = null;
			org._3pq.jgrapht.Graph mygraph = MoleculeGraphs.getMoleculeGraph(mol);
			Object startVertex = atom;
			Object endVertex;
			org._3pq.jgrapht.Edge edg;
			java.util.List mylist;
			IAtom atomTarget;
			IAtom atomSource;
			Integer thisAtom;
			partial = 0;
			limitInf = 1.4;
			limitSup = 4;
			step = (limitSup - limitInf)/15;
			
			if(atoms.size() > 0) {
				ArrayList gHr_topol_function = new ArrayList(15);
				for(double ghrt = limitInf; ghrt < limitSup; ghrt = ghrt + step) {  
					sum = 0;
					for( int at = 0; at < atoms.size(); at++ ) {
						partial = 0;
						distance = 0;
						thisAtom = (Integer)atoms.get(at);
						position = thisAtom.intValue();
						endVertex = mol.getAtom(position);
						atom2 = mol.getAtom(position);
						mylist = org.openscience.cdk.graph.BFSShortestPath.findPathBetween(mygraph,startVertex,endVertex);
						for (int u = 0; u < mylist.size(); u++) {
							edg = (org._3pq.jgrapht.Edge)mylist.get(u);
							atomTarget = (IAtom)edg.getTarget();
							atomSource = (IAtom)edg.getSource();
							distance += calculateDistanceBetweenTwoAtoms(atomTarget, atomSource);
						}
						partial = atom2.getCharge() * Math.exp( smooth * (Math.pow( (ghrt - distance) , 2)));
						sum += partial;
					}
					gHr_topol_function.add(new Double(sum));
					//System.out.println("RDF gr-topol distance prob.: "+sum+ " at distance "+ghrt);
				}
				atom.setProperty("gasteigerGHRtopol", new ArrayList(gHr_topol_function));
				rdfProtonCalculatedValues.add(1);
			}
			else rdfProtonCalculatedValues.add(0);
                        // System.out.println("TEST PROP: " + ((ArrayList)target.getProperty("gasteigerGHRtopol")).size() );
			
			
//////////////////////// THE THIRD DESCRIPTOR IS gD(r) WITH DISTANCE AND RADIAN ANGLE BTW THE PROTON AND THE MIDDLE POINT OF DOUBLE BOND
			
			Vector3d a_a = new Vector3d();
			Vector3d a_b = new Vector3d();
			Vector3d b_a = new Vector3d();
			Vector3d b_b = new Vector3d();
			Point3d middlePoint = new Point3d();
			double angle = 0;			
			
			if(doubles.size() > 0) {
				IAtom goodAtom0;
				IAtom goodAtom1;
				limitInf = 0;
				limitSup = Math.PI / 2;
				step = (limitSup - limitInf)/7;
				position = 0;
				partial = 0;
				org.openscience.cdk.interfaces.IBond theDoubleBond;
				smooth = -1.15;
				int goodPosition = 0;
				org.openscience.cdk.interfaces.IBond goodBond;
				ArrayList gDr_function = new ArrayList(7);
				for(double ghd = limitInf; ghd < limitSup; ghd = ghd + step) {
					sum = 0;
					for( int dou = 0; dou < doubles.size(); dou++ ) {
						partial = 0;
						Integer thisDoubleBond = (Integer)doubles.get(dou);
						position = thisDoubleBond.intValue();
						theDoubleBond = mol.getBond(position);
						goodPosition = getNearestBondtoAGivenAtom(mol, atom, theDoubleBond);
						goodBond = mol.getBond(goodPosition);
						goodAtom0 = goodBond.getAtom(0);
						goodAtom1 = goodBond.getAtom(1);
						
						//System.out.println("GOOD POS IS "+mol.getAtomNumber(goodAtoms[0])+" "+mol.getAtomNumber(goodAtoms[1]));
						
						middlePoint = theDoubleBond.get3DCenter();
						values = calculateDistanceBetweenAtomAndBond(atom, theDoubleBond );
						
						if(theDoubleBond.contains(goodAtom0)) {						
							a_a.set(goodAtom0.getPoint3d().x, goodAtom0.getPoint3d().y, goodAtom0.getPoint3d().z);
							a_b.set(goodAtom1.getPoint3d().x, goodAtom1.getPoint3d().y, goodAtom1.getPoint3d().z);
						}
						else {
							a_a.set(goodAtom1.getPoint3d().x, goodAtom1.getPoint3d().y, goodAtom1.getPoint3d().z);
							a_b.set(goodAtom0.getPoint3d().x, goodAtom0.getPoint3d().y, goodAtom0.getPoint3d().z);
						}
						b_b.set(middlePoint.x, middlePoint.y, middlePoint.z);
						b_b.set(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z);
						angle = calculateAngleBetweenTwoLines(a_a, a_b, b_a, b_b);
						partial = ( ( 1 / (Math.pow( values[0], 2 ) ) ) * Math.exp( smooth * (Math.pow( (ghd - angle) , 2) ) ) );
						sum += partial;
					}
					gDr_function.add(new Double(sum));
				}
				atom.setProperty("gasteigerGDR", new ArrayList(gDr_function));
				rdfProtonCalculatedValues.add(1);
			}
			else rdfProtonCalculatedValues.add(0);
			
			//System.out.println("----------------------------");
			
//////////////////////// THE FOUTH DESCRIPTOR IS gS(r), WHICH TAKES INTO ACCOUNT SINGLE BONDS IN RIGID SYSTEMS			
			
			
			if(singles.size() > 0) {
				double dist0;
				double dist1;
				IAtom singleBondAtom0;
				IAtom singleBondAtom1;
				distance = 0;
				position = 0;
				org.openscience.cdk.interfaces.IBond theSingleBond = null;
				limitInf = 0;
				limitSup = Math.PI / 2;
				step = (limitSup - limitInf)/7;
				smooth = -1.15;
				ArrayList gSr_function = new ArrayList(7);
				for(double ghs = 0; ghs < limitSup; ghs = ghs + step) {
					sum = 0;
					for( int sing = 0; sing < singles.size(); sing++ ) {
						angle = 0;
						partial = 0;
						Integer thisSingleBond = (Integer)singles.get(sing);
						position = thisSingleBond.intValue();
						theSingleBond = mol.getBond(position);
						middlePoint = theSingleBond.get3DCenter();
						singleBondAtom0 = theSingleBond.getAtom(0);
						singleBondAtom1 = theSingleBond.getAtom(1);
						dist0 = calculateDistanceBetweenTwoAtoms(singleBondAtom0, atom);
						dist1 = calculateDistanceBetweenTwoAtoms(singleBondAtom1, atom);
							
						a_a.set(middlePoint.x, middlePoint.y, middlePoint.z);
						if(dist1 > dist0) a_b.set(singleBondAtom0.getPoint3d().x, singleBondAtom0.getPoint3d().y, singleBondAtom0.getPoint3d().z);
						else a_b.set(singleBondAtom1.getPoint3d().x, singleBondAtom1.getPoint3d().y, singleBondAtom1.getPoint3d().z);
						b_a.set(middlePoint.x, middlePoint.y, middlePoint.z);
						b_b.set(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z);
						
						values = calculateDistanceBetweenAtomAndBond(atom, theSingleBond );
						
						angle = calculateAngleBetweenTwoLines(a_a, a_b, b_a, b_b);
							//System.out.println("ANGLe: "+angle+ " "+ mol.getAtomNumber(atomsInSingleBond[0]) +" " +mol.getAtomNumber(atomsInSingleBond[1]));
							
						partial = (1 / (Math.pow( values[0], 2 ))) * Math.exp( smooth * (Math.pow( (ghs - angle) , 2)));
						sum += partial;
					}
					gSr_function.add(new Double(sum));
					//System.out.println("RDF gSr prob.: " + sum +  " at distance " + ghs);
				}
				atom.setProperty("gasteigerGSR", new ArrayList(gSr_function));
				rdfProtonCalculatedValues.add(1);
			}
			else rdfProtonCalculatedValues.add(0);
			
			//System.out.println("----------------------------");
			
			
////////////////////////// LAST DESCRIPTOR IS g3(r), FOR PROTONS BONDED TO LIKE-CYCLOEXANE RINGS:
			
			
			
			if(bondsInCycloex.size() > 0) {
				IAtom cycloexBondAtom0;
				IAtom cycloexBondAtom1;
				org.openscience.cdk.interfaces.IBond theInCycloexBond;
				distance = 0;
				limitInf = 0;
				limitSup = Math.PI;
				step = (limitSup - limitInf)/13;
				position = 0;
				smooth = -2.86;
				angle = 0;
				int ya_counter = 0;
				List connAtoms;
				ArrayList g3r_function = new ArrayList(13);
				for(double g3r = 0; g3r < limitSup; g3r = g3r + step) {
					sum = 0;
					for( int cyc = 0; cyc < bondsInCycloex.size(); cyc++ ) {
						ya_counter = 0;
						angle = 0;
						partial = 0;
						Integer thisInCycloexBond = (Integer)bondsInCycloex.get(cyc);
						position = thisInCycloexBond.intValue();
						theInCycloexBond = mol.getBond(position);
						cycloexBondAtom0 = theInCycloexBond.getAtom(0);
						cycloexBondAtom1 = theInCycloexBond.getAtom(1);
						
						connAtoms = mol.getConnectedAtomsList(cycloexBondAtom0);
						for(int g = 0; g < connAtoms.size(); g++) {
							if(((IAtom)connAtoms.get(g)).equals(neighbour0)) ya_counter += 1;
						}
						
						if(ya_counter > 0) {
							a_a.set(cycloexBondAtom1.getPoint3d().x, cycloexBondAtom1.getPoint3d().y, cycloexBondAtom1.getPoint3d().z);
							a_b.set(cycloexBondAtom0.getPoint3d().x, cycloexBondAtom0.getPoint3d().y, cycloexBondAtom0.getPoint3d().z);
						}
						else {
							a_a.set(cycloexBondAtom0.getPoint3d().x, cycloexBondAtom0.getPoint3d().y, cycloexBondAtom0.getPoint3d().z);
							a_b.set(cycloexBondAtom1.getPoint3d().x, cycloexBondAtom1.getPoint3d().y, cycloexBondAtom1.getPoint3d().z);
						}
						b_a.set(neighbour0.getPoint3d().x, neighbour0.getPoint3d().y, neighbour0.getPoint3d().z);
						b_b.set(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z);
						
						angle = calculateAngleBetweenTwoLines(a_a, a_b, b_a, b_b);
						
						//System.out.println("gcycr ANGLE: " + angle + " " +mol.getAtomNumber(cycloexBondAtom0) + " "+mol.getAtomNumber(cycloexBondAtom1));
						
						partial = Math.exp( smooth * (Math.pow( (g3r - angle) , 2) ) );
						sum += partial;
					}
					g3r_function.add(new Double(sum));
					//System.out.println("RDF g-cycl prob.: "+sum+ " at distance "+g3r);
				}
				atom.setProperty("gasteigerG3R", new ArrayList(g3r_function));
				rdfProtonCalculatedValues.add(1);
			}
			else rdfProtonCalculatedValues.add(0);
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), rdfProtonCalculatedValues);
	}


/////////////////////////THIRD SECTION OF CODE:
////////////////////////IT CONTAINS SOME PRIVATE FUNCTIONS:


	// this method returns true if a bond is not rotatable.
	// a bond is defined to be not rotatable when:
	// 1) it belongs to a ring
	// 2) it belongs to a pi-system delocalized 
	// 3) it belongs to an amide group
	
	
	private boolean getIfBondIsNotRotatable(Molecule mol, org.openscience.cdk.interfaces.IBond bond, IAtomContainer detected) {
		boolean isBondNotRotatable = false;
		int counter = 0;
		IAtom atom0 = bond.getAtom(0);
		IAtom atom1 = bond.getAtom(1);
		if (detected != null) { 				
			if(detected.contains(bond)) counter += 1;
		}
		if(atom0.getFlag(CDKConstants.ISINRING)) {
			if(atom1.getFlag(CDKConstants.ISINRING)) { counter += 1; }
			else {
				if(atom1.getSymbol().equals("H")) counter += 1;
				else counter += 0;
			}
		}
		if( atom0.getSymbol().equals("N") && atom1.getSymbol().equals("C") ) {
			if(getIfACarbonIsDoubleBondedToAnOxygen(mol, atom1)) counter += 1;
		}
		if( atom0.getSymbol().equals("C") && atom1.getSymbol().equals("N") ) {
			if(getIfACarbonIsDoubleBondedToAnOxygen(mol, atom0)) counter += 1;
		}
		if(counter > 0) isBondNotRotatable = true;
		return isBondNotRotatable;
	}
	
	private boolean getIfACarbonIsDoubleBondedToAnOxygen(Molecule mol, IAtom carbonAtom) {
		boolean isDoubleBondedToOxygen = false;
		java.util.List neighToCarbon = mol.getConnectedAtomsList(carbonAtom);
		org.openscience.cdk.interfaces.IBond tmpBond;
		int counter = 0;
		for(int nei = 0; nei < neighToCarbon.size(); nei++) {
			IAtom neighbour = (IAtom)neighToCarbon.get(nei);
			if(neighbour.getSymbol().equals("O")) {
				tmpBond = mol.getBond(neighbour, carbonAtom);
				if(tmpBond.getOrder() == 2.0) counter += 1;
			}
		}
		if(counter > 0) isDoubleBondedToOxygen = true;
		return isDoubleBondedToOxygen;
	}
	
	// this method calculates the angle between two bonds given coordinates of their atoms
	
	public double calculateAngleBetweenTwoLines(Vector3d a, Vector3d b, Vector3d c, Vector3d d) {
		Vector3d firstLine = new Vector3d();
		firstLine.sub(a, b);
		Vector3d secondLine = new Vector3d();
		secondLine.sub(c, d);
		Vector3d firstVec = new Vector3d(firstLine);
		Vector3d secondVec = new Vector3d(secondLine);
        return firstVec.angle(secondVec);
	}
	
	// this method store atoms and bonds in proper lists:
	private void checkAndStore(int bondToStore, double bondOrder,
                               ArrayList singleVec, ArrayList doubleVec,
                               ArrayList cycloexVec, int a1,
                               ArrayList atomVec, int sphere, boolean isBondInCycloex) {
		if(!atomVec.contains(new Integer(a1))) {
			if(sphere < 6) atomVec.add(new Integer(a1));
		}
		if(!cycloexVec.contains(new Integer(bondToStore))) {
			if(isBondInCycloex) {
				cycloexVec.add(new Integer(bondToStore));
			}
		}
		if(bondOrder == 2.0) {
			if(!doubleVec.contains(new Integer(bondToStore))) doubleVec.add(new Integer(bondToStore));
		}
		if(bondOrder == 1.0) {
			if(!singleVec.contains(new Integer(bondToStore))) singleVec.add(new Integer(bondToStore));
		}
	}
	
	// generic method for calculation of distance btw 2 atoms
	private double calculateDistanceBetweenTwoAtoms(IAtom atom1, IAtom atom2) {
		double distance;
		Point3d firstPoint = atom1.getPoint3d();
		Point3d secondPoint = atom2.getPoint3d();
		distance = firstPoint.distance(secondPoint);
		return distance;
	}
	
	
	// given a double bond 
	// this method returns a bond bonded to this double bond
	private int getNearestBondtoAGivenAtom(Molecule mol, IAtom atom, org.openscience.cdk.interfaces.IBond bond) {
		int nearestBond = 0;
		double[] values;
		double distance = 0;
		IAtom atom0 = bond.getAtom(0);
		IAtom atom1 = bond.getAtom(1);
		List bondsAtLeft = mol.getConnectedBondsList(atom0);
		int partial;
		for(int i=0; i<bondsAtLeft.size();i++) {
			IBond curBond = (IBond)bondsAtLeft.get(i);
			values = calculateDistanceBetweenAtomAndBond(atom, curBond);
			partial = mol.getBondNumber(curBond);
			if(i==0) {
				nearestBond = mol.getBondNumber(curBond);
				distance = values[0];
			}
			else {
				if(values[0] < distance) {
					nearestBond = partial;
				}
				/* XXX commented this out, because is has no effect
				 * 
				 else {
					nearestBond = nearestBond;
				}*/
			}
		}
		return nearestBond;
		
	}
	
	// method which calculated distance btw an atom and the middle point of a bond
	// and returns distance and coordinates of middle point
	private double[] calculateDistanceBetweenAtomAndBond(IAtom proton, org.openscience.cdk.interfaces.IBond theBond) {
		Point3d middlePoint = theBond.get3DCenter();
		Point3d protonPoint = proton.getPoint3d();
		double[] values = new double[4];
		values[0] = middlePoint.distance(protonPoint);
		values[1] = middlePoint.x;
		values[2] = middlePoint.y;
		values[3] = middlePoint.z;
		return values;
	}
	
	
	/**
	 *  Gets the parameterNames attribute of the RDFProtonDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[2];
		params[0] = "atomPosition";
		params[1] = "checkAromaticity";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the RDFProtonDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
                if (name.equals("atomPosition")) return new Integer(0);
                return Boolean.TRUE;
	}
}

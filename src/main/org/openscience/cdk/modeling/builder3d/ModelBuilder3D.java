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
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.modeling.builder3d;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 *  The main class to generate the 3D coordinates of a molecule ModelBuilder3D.
 *  Its use looks like:
 *  <pre>
 *  ModelBuilder3D mb3d = new ModelBuilder3D();
 *  mb3d.setTemplateHandler();
 *  mb3d.setForceField("mm2");
 *  IMolecule molecule = mb3d.generate3DCoordinates(molecule, false);
 *  </pre>
 *
 *  <p>Standing problems:
 *  <ul>
 *    <li>condensed ring systems which are unknown for the template class 
 *    <li>vdWaals clashes
 *    <li>stereochemistry
 *    <li>chains running through ring systems
 *  </ul>
 *
 * @author      cho
 * @author      steinbeck
 * @cdk.created 2004-09-07
 * @cdk.module  builder3d
 * @cdk.svnrev  $Revision$
 * @cdk.keyword 3D coordinates
 * @cdk.keyword coordinate generation, 3D
 */
public class ModelBuilder3D {

	private static Map<String,ModelBuilder3D> memyselfandi = new HashMap<String,ModelBuilder3D>();
	
	private TemplateHandler3D templateHandler = null;
		
	private Map parameterSet = null;

	private final ForceFieldConfigurator ffc = new ForceFieldConfigurator();

	String forceFieldName = "mm2";
	
	private LoggingTool logger = new LoggingTool(ModelBuilder3D.class);
	
	/**
	 * Constructor for the ModelBuilder3D object.
	 *	 
	 * @param  templateHandler  templateHandler Object
	 * @param  ffname           name of force field
	 */
	private ModelBuilder3D(TemplateHandler3D templateHandler, String ffname) throws CDKException {
		setTemplateHandler(templateHandler);
		setForceField(ffname);
	}

	public static ModelBuilder3D getInstance(TemplateHandler3D templateHandler, String ffname) throws CDKException {
		if (ffname == null || ffname.length() == 0) throw new CDKException("The given ffname is null or empty!");
		if (templateHandler == null) throw new CDKException("The given template handler is null!");
		
		String builderCode = templateHandler.getClass().getName()+ "#" + ffname;
		if (!memyselfandi.containsKey(builderCode)) {
			ModelBuilder3D builder = new ModelBuilder3D(
				templateHandler, ffname
			);
			memyselfandi.put(builderCode, builder);
			return builder;
		}
		return memyselfandi.get(builderCode);
	}

	public static ModelBuilder3D getInstance() throws CDKException {
		return getInstance(TemplateHandler3D.getInstance(), "mm2");
	}

	/**
	 * Gives a list of possible force field types.
	 *
	 * @return                the list
	 */
  public String[] getFfTypes(){
    return ffc.getFfTypes();
  }


	/**
	 * Sets the forceField attribute of the ModelBuilder3D object.
	 *
	 * @param  ffname  forceField name
	 */
	private void setForceField(String ffname) throws CDKException {
		if (ffname == null) {
			ffname = "mm2";
		}
		try {
			forceFieldName = ffname;
			ffc.setForceFieldConfigurator(ffname);
			parameterSet = ffc.getParameterSet();
		} catch (Exception ex1) {
			logger.error("Problem with ForceField configuration due to>" + ex1.getMessage());
			logger.debug(ex1);
			throw new CDKException("Problem with ForceField configuration due to>" + ex1.getMessage(), ex1);
		}
	}


	/**
	 * Generate 3D coordinates with force field information.
	 */
	public IMolecule generate3DCoordinates(IMolecule molecule, boolean clone) throws Exception {
	    String[] originalAtomTypeNames = new String[molecule.getAtomCount()];
	    for (int i=0; i<originalAtomTypeNames.length; i++) {
	        originalAtomTypeNames[i] = molecule.getAtom(i).getAtomTypeName();
	    }

		logger.debug("******** GENERATE COORDINATES ********");
		for(int i=0;i<molecule.getAtomCount();i++){
			molecule.getAtom(i).setFlag(CDKConstants.ISPLACED,false);
			molecule.getAtom(i).setFlag(CDKConstants.VISITED,false);
		}
		//CHECK FOR CONNECTIVITY!
		logger.debug("#atoms>"+molecule.getAtomCount());
		if (!ConnectivityChecker.isConnected(molecule)) {
			throw new CDKException("Molecule is NOT connected, could not layout.");
		}
		
		// setup helper classes
		AtomPlacer atomPlacer = new AtomPlacer();
		AtomPlacer3D ap3d = new AtomPlacer3D();
		AtomTetrahedralLigandPlacer3D atlp3d = new AtomTetrahedralLigandPlacer3D();
		ap3d.initilize(parameterSet);
		atlp3d.setParameterSet(parameterSet);
		
		if (clone) molecule = (IMolecule)molecule.clone();
		atomPlacer.setMolecule(molecule);
		
		if (ap3d.numberOfUnplacedHeavyAtoms(molecule) == 1) {
			logger.debug("Only one Heavy Atom");
			molecule.getAtom(0).setPoint3d(new Point3d(0.0, 0.0, 0.0));
			try {
				atlp3d.add3DCoordinatesForSinglyBondedLigands(molecule);
			} catch (Exception ex3) {
				logger.error("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.getMessage());
				logger.debug(ex3);
				throw new CDKException("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.getMessage(), ex3);
			}
			return molecule;
		}
		//Assing Atoms to Rings,Aliphatic and Atomtype
		IRingSet ringSetMolecule = ffc.assignAtomTyps(molecule);
		List ringSystems = null;
		IRingSet largestRingSet = null;
		double NumberOfRingAtoms = 0;

		if (ringSetMolecule.getAtomContainerCount() > 0) {
			if(templateHandler==null){
				throw new CDKException("You are trying to generate coordinates for a molecule with rings, but you have no template handler set. Please do setTemplateHandler() before generation!");
			}
			ringSystems = RingPartitioner.partitionRings(ringSetMolecule);
			largestRingSet = RingSetManipulator.getLargestRingSet(ringSystems);
			IAtomContainer largestRingSetContainer = RingSetManipulator.getAllInOneContainer(largestRingSet);
			NumberOfRingAtoms = (double)largestRingSetContainer.getAtomCount();
			templateHandler.mapTemplates(largestRingSetContainer, NumberOfRingAtoms);
			if (!checkAllRingAtomsHasCoordinates(largestRingSetContainer)) {
				throw new CDKException("RingAtomLayoutError: Not every ring atom is placed! Molecule cannot be layout.");
			}

			setAtomsToPlace(largestRingSetContainer);
			searchAndPlaceBranches(molecule, largestRingSetContainer, ap3d, atlp3d, atomPlacer);
			largestRingSet = null;
		} else {
			//logger.debug("****** Start of handling aliphatic molecule ******");
			IAtomContainer ac = null;

			ac = atomPlacer.getInitialLongestChain(molecule);
			setAtomsToUnVisited(molecule);
			setAtomsToUnPlaced(molecule);
			ap3d.placeAliphaticHeavyChain(molecule, ac);
			//ZMatrixApproach
			ap3d.zmatrixChainToCartesian(molecule, false);
			searchAndPlaceBranches(molecule, ac, ap3d, atlp3d, atomPlacer);
		}
		layoutMolecule(ringSystems, molecule, ap3d, atlp3d, atomPlacer);
		//logger.debug("******* PLACE SUBSTITUENTS ******");
		try {
			atlp3d.add3DCoordinatesForSinglyBondedLigands(molecule);
		} catch (Exception ex3) {
			logger.error("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.getMessage());
			logger.debug(ex3);
			throw new Exception("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.getMessage(), ex3);
		}
		// restore the original atom type names
    for (int i=0; i<originalAtomTypeNames.length; i++) {
        molecule.getAtom(i).setAtomTypeName(originalAtomTypeNames[i]);
    }

		return molecule;
	}



	/**
	 * Gets the ringSetOfAtom attribute of the ModelBuilder3D object.
	 *
	 *@return              The ringSetOfAtom value
	 */
	private IRingSet getRingSetOfAtom(List ringSystems, IAtom atom) {
		IRingSet ringSetOfAtom = null;
		for (int i = 0; i < ringSystems.size(); i++) {
			if (((IRingSet) ringSystems.get(i)).contains(atom)) {
				return (IRingSet) ringSystems.get(i);
			}
		}
		return ringSetOfAtom;
	}


	/**
	 * Layout the molecule, starts with ring systems and than aliphatic chains.
	 *
	 *@param  ringSetMolecule  ringSystems of the molecule
	 *@exception  Exception    Description of the Exception
	 */
	private void layoutMolecule(List ringSetMolecule, IMolecule molecule, AtomPlacer3D ap3d, AtomTetrahedralLigandPlacer3D atlp3d, AtomPlacer atomPlacer) throws Exception {
		//logger.debug("****** LAYOUT MOLECULE MAIN *******");
		IAtomContainer ac = null;
		int safetyCounter = 0;
		IAtom atom = null;
		//Place rest Chains/Atoms
		do {
			safetyCounter++;
			atom = ap3d.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(molecule);
			if (atom != null) {
				//logger.debug("layout RingSystem...");
				IAtom unplacedAtom = ap3d.getUnplacedRingHeavyAtom(molecule, atom);
				IRingSet ringSetA = getRingSetOfAtom(ringSetMolecule, unplacedAtom);
				IAtomContainer ringSetAContainer = RingSetManipulator.getAllInOneContainer(ringSetA);
				templateHandler.mapTemplates(ringSetAContainer, (double)ringSetAContainer.getAtomCount());

				if (checkAllRingAtomsHasCoordinates(ringSetAContainer)) {
				} else {
					throw new IOException("RingAtomLayoutError: Not every ring atom is placed! Molecule cannot be layout.Sorry");
				}

				Point3d firstAtomOriginalCoord = unplacedAtom.getPoint3d();
				Point3d centerPlacedMolecule = ap3d.geometricCenterAllPlacedAtoms(molecule);

				setBranchAtom(molecule, unplacedAtom, atom, ap3d.getPlacedHeavyAtoms(molecule, atom), ap3d, atlp3d);
				layoutRingSystem(firstAtomOriginalCoord, unplacedAtom, ringSetA, centerPlacedMolecule, atom, ap3d);
				searchAndPlaceBranches(molecule, ringSetAContainer, ap3d, atlp3d, atomPlacer);
				//logger.debug("Ready layout Ring System");
				ringSetA = null;
				unplacedAtom = null;
				firstAtomOriginalCoord = null;
				centerPlacedMolecule = null;
			} else {
				//logger.debug("layout chains...");
				setAtomsToUnVisited(molecule);
				atom = ap3d.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(molecule);
				if (atom != null) {
					ac = new org.openscience.cdk.AtomContainer();
					ac.addAtom(atom);
					searchAndPlaceBranches(molecule, ac, ap3d, atlp3d, atomPlacer);
					ac = null;
				}
			}
		} while (!ap3d.allHeavyAtomsPlaced(molecule) || safetyCounter > molecule.getAtomCount());
	}


	/**
	 * Layout the ring system, rotate and translate the template.
	 *
	 *@param  originalCoord         coordinates of the placedRingAtom from the template 
	 *@param  placedRingAtom        placedRingAtom
	 *@param  ringSet               ring system which placedRingAtom is part of
	 *@param  centerPlacedMolecule  the geometric center of the already placed molecule
	 *@param  atomB                 placed neighbour atom of  placedRingAtom
	 */
	private void layoutRingSystem(Point3d originalCoord, IAtom placedRingAtom, IRingSet ringSet, Point3d centerPlacedMolecule, IAtom atomB, AtomPlacer3D ap3d) {
		//logger.debug("****** Layout ring System ******");System.out.println(">around atom:"+molecule.getAtomNumber(placedRingAtom));
		IAtomContainer ac = RingSetManipulator.getAllInOneContainer(ringSet);
		Point3d newCoord = placedRingAtom.getPoint3d();
		Vector3d axis = new Vector3d(atomB.getPoint3d().x - newCoord.x, atomB.getPoint3d().y - newCoord.y, atomB.getPoint3d().z - newCoord.z);
		translateStructure(originalCoord, newCoord, ac);
		//Rotate Ringsystem to farthest possible point
		Vector3d startAtomVector = new Vector3d(newCoord.x - atomB.getPoint3d().x, newCoord.y - atomB.getPoint3d().y, newCoord.z - atomB.getPoint3d().z);
		IAtom farthestAtom = ap3d.getFarthestAtom(placedRingAtom.getPoint3d(), ac);
		Vector3d farthestAtomVector = new Vector3d(farthestAtom.getPoint3d().x - newCoord.x, farthestAtom.getPoint3d().y - newCoord.y, farthestAtom.getPoint3d().z - newCoord.z);
		Vector3d n1 = new Vector3d();
		n1.cross(axis, farthestAtomVector);
		n1.normalize();
		double lengthFarthestAtomVector = farthestAtomVector.length();
		Vector3d farthestVector = new Vector3d(startAtomVector);
		farthestVector.normalize();
		farthestVector.scale((startAtomVector.length() + lengthFarthestAtomVector));
		double dotProduct = farthestAtomVector.dot(farthestVector);
		double angle = Math.acos(dotProduct / (farthestAtomVector.length() * farthestVector.length()));
		Vector3d ringCenter = new Vector3d();

		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtom(i).getFlag(CDKConstants.ISPLACED))) {
				ringCenter.x = (ac.getAtom(i).getPoint3d()).x - newCoord.x;
				ringCenter.y = (ac.getAtom(i).getPoint3d()).y - newCoord.y;
				ringCenter.z = (ac.getAtom(i).getPoint3d()).z - newCoord.z;
				ringCenter = AtomTetrahedralLigandPlacer3D.rotate(ringCenter, n1, angle);
				ac.getAtom(i).setPoint3d(
					new Point3d(
						ringCenter.x + newCoord.x,
						ringCenter.y + newCoord.y,
						ringCenter.z + newCoord.z
					)
				);
				//ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}

		//Rotate Ring so that geometric center is max from placed center
		//logger.debug("Rotate RINGSYSTEM");
		Point3d pointRingCenter = GeometryTools.get3DCenter(ac);
		double distance = 0;
		double rotAngleMax = 0;
		angle = 1 / 180 * Math.PI;
		ringCenter = new Vector3d(pointRingCenter.x, pointRingCenter.y, pointRingCenter.z);
		ringCenter.x = ringCenter.x - newCoord.x;
		ringCenter.y = ringCenter.y - newCoord.y;
		ringCenter.z = ringCenter.z - newCoord.z;
		for (int i = 1; i < 360; i++) {
			ringCenter = AtomTetrahedralLigandPlacer3D.rotate(ringCenter, axis, angle);
			if (centerPlacedMolecule.distance(new Point3d(ringCenter.x, ringCenter.y, ringCenter.z)) > distance) {
				rotAngleMax = i;
				distance = centerPlacedMolecule.distance(new Point3d(ringCenter.x, ringCenter.y, ringCenter.z));
			}
		}

		//rotate ring around axis with best angle
		rotAngleMax = (rotAngleMax / 180) * Math.PI;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtom(i).getFlag(CDKConstants.ISPLACED))) {
				ringCenter.x = (ac.getAtom(i).getPoint3d()).x;
				ringCenter.y = (ac.getAtom(i).getPoint3d()).y;
				ringCenter.z = (ac.getAtom(i).getPoint3d()).z;
				ringCenter = AtomTetrahedralLigandPlacer3D.rotate(ringCenter, axis, rotAngleMax);
				ac.getAtom(i).setPoint3d(
					new Point3d(
						ringCenter.x,
						ringCenter.y,
						ringCenter.z
					)
				);
				ac.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	/**
	 * Sets a branch atom to a ring or aliphatic chain.
	 *
	 *@param  unplacedAtom    The new branchAtom 
	 *@param  atomA           placed atom to which the unplaced satom is connected
	 *@param  atomNeighbours  placed atomNeighbours of atomA
	 *@exception  Exception   Description of the Exception
	 */
	private void setBranchAtom(IMolecule molecule, IAtom unplacedAtom, IAtom atomA, IAtomContainer atomNeighbours, AtomPlacer3D ap3d, AtomTetrahedralLigandPlacer3D atlp3d) throws Exception {
		//logger.debug("****** SET Branch Atom ****** >"+molecule.getAtomNumber(unplacedAtom));
		IAtomContainer noCoords = new org.openscience.cdk.AtomContainer();
		noCoords.addAtom(unplacedAtom);
		Point3d centerPlacedMolecule = ap3d.geometricCenterAllPlacedAtoms(molecule);
		IAtom atomB = atomNeighbours.getAtom(0);

        String atypeNameA = atomA.getAtomTypeName();
        String atypeNameB = atomB.getAtomTypeName();
        String atypeNameUnplaced = unplacedAtom.getAtomTypeName();

        double length = ap3d.getBondLengthValue(atypeNameA, atypeNameUnplaced);
        double angle = (ap3d.getAngleValue(atypeNameB, atypeNameA, atypeNameUnplaced)) * Math.PI / 180;
		/*
		 *  System.out.println("A:"+atomA.getSymbol()+" "+atomA.getAtomTypeName()+" B:"+atomB.getSymbol()+" "+atomB.getAtomTypeName()
		 *  +" unplaced Atom:"+unplacedAtom.getAtomTypeName()+" BL:"+length+" Angle:"+angle
		 *  +" FormalNeighbour:"+atomA.getFormalNeighbourCount()+" HYB:"+atomA.getFlag(CDKConstants.HYBRIDIZATION_SP2)
		 *  +" #Neigbhours:"+atomNeighbours.getAtomCount());
		 */
		IAtom atomC = ap3d.getPlacedHeavyAtom(molecule, atomB, atomA);

		Point3d[] branchPoints = atlp3d.get3DCoordinatesForLigands(atomA, noCoords, atomNeighbours, atomC
				, (atomA.getFormalNeighbourCount() - atomNeighbours.getAtomCount())
				, length, angle);
		double distance = 0;
		int farthestPoint = 0;
		try {
			for (int i = 0; i < branchPoints.length; i++) {
				if (Math.abs(branchPoints[i].distance(centerPlacedMolecule)) > Math.abs(distance)) {
					distance = branchPoints[i].distance(centerPlacedMolecule);
					farthestPoint = i;
				}
			}
		} catch (Exception ex2) {
			throw new IOException("SetBranchAtomERROR: Not enough branch Points");
		}

		int stereo = -1;
		if (atomA.getStereoParity() != CDKConstants.UNSET && atomA.getStereoParity() != 0 ||
				(Math.abs((molecule.getBond(atomA, unplacedAtom)).getStereo()) < 2
				 && Math.abs((molecule.getBond(atomA, unplacedAtom)).getStereo()) != 0)
				 && molecule.getMaximumBondOrder(atomA) == IBond.Order.SINGLE) {
			if (atomNeighbours.getAtomCount() > 1) {
				stereo = atlp3d.makeStereocenter(atomA.getPoint3d(), molecule.getBond(atomA, unplacedAtom), (atomNeighbours.getAtom(0)).getPoint3d(), (atomNeighbours.getAtom(1)).getPoint3d(), branchPoints);
			}
		}
		if (stereo != -1) {
			farthestPoint = stereo;
		}
		unplacedAtom.setPoint3d(branchPoints[farthestPoint]);
		unplacedAtom.setFlag(CDKConstants.ISPLACED, true);
	}


	/**
	 * Search and place branches of a chain or ring.
	 *
	 *@param  chain          AtomContainer if atoms in an aliphatic chain or ring system 
	 *@exception  Exception  Description of the Exception
	 */
	private void searchAndPlaceBranches(IMolecule molecule, IAtomContainer chain, AtomPlacer3D ap3d, AtomTetrahedralLigandPlacer3D atlp3d, AtomPlacer atomPlacer) throws Exception {
		//logger.debug("****** SEARCH AND PLACE ****** Chain length: "+chain.getAtomCount());
		java.util.List atoms = null;
		IAtomContainer branchAtoms = new org.openscience.cdk.AtomContainer();
		IAtomContainer connectedAtoms = new org.openscience.cdk.AtomContainer();
		for (int i = 0; i < chain.getAtomCount(); i++) {
			atoms = molecule.getConnectedAtomsList(chain.getAtom(i));
			for (int j = 0; j < atoms.size(); j++) {
				IAtom atom = (IAtom)atoms.get(j);
				if (!(atom.getSymbol()).equals("H") & !(atom.getFlag(CDKConstants.ISPLACED)) & !(atom.getFlag(CDKConstants.ISINRING))) {
					//logger.debug("SEARCH PLACE AND FOUND Branch Atom "+molecule.getAtomNumber(chain.getAtomAt(i))+
					//			" New Atom:"+molecule.getAtomNumber(atoms[j])+" -> STORE");
					try {
						connectedAtoms.add(ap3d.getPlacedHeavyAtoms(molecule, chain.getAtom(i)));
						//logger.debug("Connected atom1:"+molecule.getAtomNumber(connectedAtoms.getAtomAt(0))+" atom2:"+
						//molecule.getAtomNumber(connectedAtoms.getAtomAt(1))+ " Length:"+connectedAtoms.getAtomCount());
					} catch (Exception ex1) {
						logger.error("SearchAndPlaceBranchERROR: Cannot find connected placed atoms due to" + ex1.toString());
						throw new IOException("SearchAndPlaceBranchERROR: Cannot find connected placed atoms");
					}
					try {
						setBranchAtom(molecule, atom, chain.getAtom(i), connectedAtoms, ap3d, atlp3d);
					} catch (Exception ex2) {
						logger.error("SearchAndPlaceBranchERROR: Cannot find enough neighbour atoms due to" + ex2.toString());
						throw new CDKException("SearchAndPlaceBranchERROR: Cannot find enough neighbour atoms: " + ex2.getMessage(), ex2);
					}
					branchAtoms.addAtom(atom);
					connectedAtoms.removeAllElements();
				}
			}

		}//for ac.getAtomCount
		placeLinearChains3D(molecule, branchAtoms, ap3d, atlp3d, atomPlacer);
	}


	/**
	 * Layout all aliphatic chains with ZMatrix.
	 *
	 *@param  startAtoms     AtomContainer of possible start atoms for a chain
	 *@exception  Exception  Description of the Exception
	 */
	private void placeLinearChains3D(IMolecule molecule, IAtomContainer startAtoms, AtomPlacer3D ap3d, AtomTetrahedralLigandPlacer3D atlp3d, AtomPlacer atomPlacer) throws Exception {
		//logger.debug("****** PLACE LINEAR CHAINS ******");
		IAtom dihPlacedAtom = null;
		IAtom thirdPlacedAtom = null;
		IAtomContainer longestUnplacedChain = new org.openscience.cdk.AtomContainer();
		if (startAtoms.getAtomCount() == 0) {
			//no branch points ->linear chain
			//logger.debug("------ LINEAR CHAIN - FINISH ------");
		} else {
			for (int i = 0; i < startAtoms.getAtomCount(); i++) {
				//logger.debug("FOUND BRANCHED ALKAN");
				//logger.debug("Atom NOT NULL:" + molecule.getAtomNumber(startAtoms.getAtomAt(i)));
				thirdPlacedAtom = ap3d.getPlacedHeavyAtom(molecule, startAtoms.getAtom(i));
				dihPlacedAtom = ap3d.getPlacedHeavyAtom(molecule, thirdPlacedAtom, startAtoms.getAtom(i));
				longestUnplacedChain.addAtom(dihPlacedAtom);
				longestUnplacedChain.addAtom(thirdPlacedAtom);
				longestUnplacedChain.addAtom(startAtoms.getAtom(i));

				longestUnplacedChain.add(atomPlacer.getLongestUnplacedChain(molecule, startAtoms.getAtom(i)));
				setAtomsToUnVisited(molecule);
				
				if (longestUnplacedChain.getAtomCount() < 4) {
					//di,third,sec
					//logger.debug("------ SINGLE BRANCH METHYLTYP ------");
					//break;
				} else {
					//logger.debug("LongestUnchainLength:"+longestUnplacedChain.getAtomCount());
					ap3d.placeAliphaticHeavyChain(molecule, longestUnplacedChain);
					ap3d.zmatrixChainToCartesian(molecule, true);
					searchAndPlaceBranches(molecule, longestUnplacedChain, ap3d, atlp3d, atomPlacer);
				}
				longestUnplacedChain.removeAllElements();
			}//for

		}
		//logger.debug("****** HANDLE ALIPHATICS END ******");
	}


	/**
	 * Translates the template ring system to new coordinates.
	 *
	 *@param  originalCoord  original coordinates of the placed ring atom from template
	 *@param  newCoord       new coordinates from branch placement
	 *@param  ac             AtomContainer contains atoms of ring system 
	 */
	private void translateStructure(Point3d originalCoord, Point3d newCoord, IAtomContainer ac) {
		Point3d transVector = new Point3d(originalCoord);
		transVector.sub(newCoord);
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtom(i).getFlag(CDKConstants.ISPLACED))) {
				ac.getAtom(i).getPoint3d().sub(transVector);
				//ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	/**
	 * Returns the largest (number of atoms) ring set in a molecule.
	 *
	 *@param  ac  AtomContainer
	 *@return     boolean
	 */
	private boolean checkAllRingAtomsHasCoordinates(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (ac.getAtom(i).getPoint3d() != null && ac.getAtom(i).getFlag(CDKConstants.ISINRING)) {
			} else if (!ac.getAtom(i).getFlag(CDKConstants.ISINRING)) {
			} else {
				return false;
			}
		}
		return true;
	}


	/**
	 * Sets the atomsToPlace attribute of the ModelBuilder3D object.
	 *
	 *@param  ac  The new atomsToPlace value
	 */
	private void setAtomsToPlace(IAtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
		}
	}


	/**
	 * Sets the atomsToUnPlaced attribute of the ModelBuilder3D object.
	 */
	private void setAtomsToUnPlaced(IMolecule molecule) {
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtom(i).setFlag(CDKConstants.ISPLACED, false);
		}
	}


	/**
	 * Sets the atomsToUnVisited attribute of the ModelBuilder3D object.
	 */
	private void setAtomsToUnVisited(IMolecule molecule) {
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtom(i).setFlag(CDKConstants.VISITED, false);
		}
	}

	/**
	 * Sets the templateHandler attribute of the ModelBuilder3D object.
	 *
	 * @param  templateHandler  The new templateHandler value
	 */
	private void setTemplateHandler(TemplateHandler3D templateHandler) throws CDKException {
		if (templateHandler == null) throw new NullPointerException("The given template handler is null!");
		
		this.templateHandler = templateHandler;
	}

	/**
	 * Returns the number of loaded templates. Note that it may return 0 because
	 * templates are lazy loaded, that is upon the first ring being layed out.
	 * 
	 * @return 0, if not templates are loaded
	 */
	public int getTemplateCount() {
		return this.templateHandler.getTemplateCount();
	}
	
}


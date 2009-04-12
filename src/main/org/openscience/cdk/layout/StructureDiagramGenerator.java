/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 *
 */
package org.openscience.cdk.layout;

import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Generates 2D coordinates for a molecule for which only connectivity is known
 * or the coordinates have been discarded for some reason. Usage: Create an
 * instance of this class, thereby assigning a molecule, call
 * generateCoordinates() and get your molecule back:
 * <pre>
 * StructureDiagramGenerator sdg = new StructureDiagramGenerator();
 * sdg.setMolecule(someMolecule);
 * sdg.generateCoordinates();
 * Molecule layedOutMol = sdg.getMolecule();
 * </pre>
 *
 * <p>The method will fail if the molecule is disconnected. The
 * partitionIntoMolecules(AtomContainer) can help here.
 *
 * @author      steinbeck
 * @cdk.created 2004-02-02
 * @see         org.openscience.cdk.graph.ConnectivityChecker#partitionIntoMolecules(IAtomContainer)
 * @cdk.keyword Layout
 * @cdk.keyword Structure Diagram Generation (SDG)
 * @cdk.keyword 2D-coordinates
 * @cdk.keyword Coordinate generation, 2D
 * @cdk.dictref blue-obelisk:layoutMolecule
 * @cdk.module  sdg
 * @cdk.svnrev  $Revision$
 * @cdk.bug     1536561
 * @cdk.bug     1788686
 */
public class StructureDiagramGenerator
{

	private LoggingTool logger = new LoggingTool(StructureDiagramGenerator.class);

	private static TemplateHandler DEFAULT_TEMPLATE_HANDLER = null;

	private IMolecule molecule;
	private IRingSet sssr;
	private double bondLength = 1.5;
	private Vector2d firstBondVector;
	private RingPlacer ringPlacer = new RingPlacer();
	private AtomPlacer atomPlacer = new AtomPlacer();
	private List ringSystems = null;
	private final String disconnectedMessage = "Molecule not connected. Use ConnectivityChecker.partitionIntoMolecules() and do the layout for every single component.";
	private TemplateHandler templateHandler = null;
	private boolean useTemplates = true;

	/** Atoms of the molecule that mapped a template */
	private IAtomContainerSet mappedSubstructures;


	/**
	 *  The empty constructor.
	 */
	public StructureDiagramGenerator()
	{
	}


	/**
	 *  Creates an instance of this class while assigning a molecule to be layed
	 *  out.
	 *
	 *  @param  molecule  The molecule to be layed out.
	 */
	public StructureDiagramGenerator(IMolecule molecule) {
		this();
		setMolecule(molecule, false);
		templateHandler = new TemplateHandler(molecule.getBuilder());
	}



	/**
	 *  Assings a molecule to be layed out. Call generateCoordinates() to do the
	 *  actual layout.
	 *
	 *  @param  mol    the molecule for which coordinates are to be generated.
	 *  @param  clone  Should the whole process be performed with a cloned copy?
	 */
	public void setMolecule(IMolecule mol, boolean clone) {
		templateHandler = new TemplateHandler(mol.getBuilder());
		IAtom atom = null;
		if (clone)
		{
			try {
				this.molecule = (IMolecule) mol.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Should clone, but exception occured: ", e.getMessage());
				logger.debug(e);
			}
		} else
		{
			this.molecule = mol;
		}
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			atom = molecule.getAtom(f);
			atom.setPoint2d(null);
			atom.setFlag(CDKConstants.ISPLACED, false);
			atom.setFlag(CDKConstants.VISITED, false);
			atom.setFlag(CDKConstants.ISINRING, false);
			atom.setFlag(CDKConstants.ISALIPHATIC, false);
		}
		atomPlacer.setMolecule(this.molecule);
		ringPlacer.setMolecule(this.molecule);
		ringPlacer.setAtomPlacer(this.atomPlacer);
	}


	/**
	 *  Sets whether to use templates or not. Some complicated ring systems
	 *  like adamantane are only nicely layouted when using templates. This
	 *  option is by default set true.
	 *
	 *@param  useTemplates  set true to use templates, false otherwise
	 */
	public void setUseTemplates(boolean useTemplates)
	{
		this.useTemplates = useTemplates;
	}


	/**
	 *  Returns whether the use of templates is enabled or disabled.
	 *
	 *  @return    true, when the use of templates is enables, false otherwise
	 */
	public boolean getUseTemplates()
	{
		return useTemplates;
	}


	/**
	 *  Sets the templateHandler attribute of the StructureDiagramGenerator object
	 *
	 *  @param  templateHandler  The new templateHandler value
	 */
	public void setTemplateHandler(TemplateHandler templateHandler)
	{
		this.templateHandler = templateHandler;
	}


	/**
	 *  Gets the templateHandler attribute of the StructureDiagramGenerator object
	 *
	 *  @return    The templateHandler value
	 */
	public TemplateHandler getTemplateHandler()
	{
		if (templateHandler == null)
		{
			return DEFAULT_TEMPLATE_HANDLER;
		}
		else
		{
			return templateHandler;
		}
	}


	/**
	 *  Assings a molecule to be layed out. Call generateCoordinates() to do the
	 *  actual layout.
	 *
	 *  @param  molecule  the molecule for which coordinates are to be generated.
	 */
	public void setMolecule(IMolecule molecule)
	{
		setMolecule(molecule, true);
	}


	/**
	 *  Returns the molecule, usually used after a call of generateCoordinates()
	 *
	 *  @return    The molecule with new coordinates (if generateCoordinates() had
	 *             been called)
	 */
	public IMolecule getMolecule()
	{
		return molecule;
	}


	/**
	 *  This method uses generateCoordinates, but it removes the hydrogens first,
	 *  lays out the structuren and then adds them again.
	 *
	 *  @throws  java.lang.Exception  if an error occurs
	 *  @see     #generateCoordinates
	 */
	public void generateExperimentalCoordinates() throws java.lang.Exception
	{
		generateExperimentalCoordinates(new Vector2d(0, 1));
	}


	/**
	 * Generates 2D coordinates on the non-hydrogen skeleton, after which
	 * coordinates for the hydrogens are calculated.
	 *
	 * @param firstBondVector the vector of the first bond to lay out
	 * @throws java.lang.Exception if an error occurs
	 */
	public void generateExperimentalCoordinates(Vector2d firstBondVector) throws java.lang.Exception {
		// first make a shallow copy: Atom/Bond references are kept
		IMolecule original = molecule;
		IMolecule shallowCopy = molecule.getBuilder().newMolecule(molecule);
		// ok, delete H's from
		//IAtom[] atoms = shallowCopy.getAtoms();
		for (int i = 0; i < shallowCopy.getAtomCount(); i++) {
			IAtom curAtom = shallowCopy.getAtom(i);
				if (curAtom.getSymbol().equals("H")) {
						shallowCopy.removeAtomAndConnectedElectronContainers(curAtom);
						curAtom.setPoint2d(null);
				}
		}
		// do layout on the shallow copy
		molecule = shallowCopy;
		generateCoordinates(firstBondVector);
		double bondLength = GeometryTools.getBondLengthAverage(molecule);
		// ok, now create the coordinates for the hydrogens
		HydrogenPlacer hPlacer = new HydrogenPlacer();
		molecule = original;
		hPlacer.placeHydrogens2D(molecule, bondLength);
	}


	/**
	 *  The main method of this StructurDiagramGenerator. Assign a molecule to the
	 *  StructurDiagramGenerator, call the generateCoordinates() method and get
	 *  your molecule back.
	 *
	 *  @param  firstBondVector          The vector of the first bond to lay out
	 *  @throws  java.lang.Exception     if an error occurs
	 */
	public void generateCoordinates(Vector2d firstBondVector) throws java.lang.Exception
	{
		int safetyCounter = 0;
		/*
		 *  if molecule contains only one Atom, don't fail, simply
		 *  set coordinates to simplest: 0,0. See bug #780545
		 */
		logger.debug("Entry point of generateCoordinates()");
		logger.debug("We have a molecules with " + molecule.getAtomCount() + " atoms.");
		if (molecule.getAtomCount() == 1)
		{
			molecule.getAtom(0).setPoint2d(new Point2d(0, 0));
			return;
		}
		if (!ConnectivityChecker.isConnected(molecule))
		{
			logger.debug("Molecule is not connected. Throwing exception.");
			throw new CDKException(disconnectedMessage);
		} else
		{
			logger.debug("Molecule is connected.");
		}

		/*
		 *  compute the minimum number of rings as
		 *  given by Frerejacque, Bull. Soc. Chim. Fr., 5, 1008 (1939)
		 */
		int nrOfEdges = molecule.getBondCount();
		//Vector2d ringSystemVector = null;
		//Vector2d newRingSystemVector = null;
		this.firstBondVector = firstBondVector;
		boolean templateMapped = false;
		double angle;

		/*
		 *  First we check if we can map any templates with predefined coordinates
		 *  Those are stored as CML in <i>org/openscience/cdk/layout/templates</i>.
		 */
		if (useTemplates && (System.getProperty("java.version").indexOf("1.3.") == -1))
		{
			logger.debug("Initializing TemplateHandler");
			logger.debug("TemplateHander initialized");
			logger.debug("Now starting Template Detection in Molecule...");
			mappedSubstructures = getTemplateHandler().getMappedSubstructures(molecule);
			templateMapped = mappedSubstructures.getAtomContainerCount() > 0;
			logger.debug("Template Detection finished");
			logger.debug("Number of found templates: " + mappedSubstructures.getAtomContainerCount());
		}

		int expectedRingCount = nrOfEdges - molecule.getAtomCount() + 1;
		if (expectedRingCount > 0)
		{
			logger.debug("*** Start of handling rings. ***");
			/*
			 *  Get the smallest set of smallest rings on this molecule
			 */
												SSSRFinder sssrf = new SSSRFinder(molecule);

			sssr = sssrf.findSSSR();
			if (sssr.getAtomContainerCount() < 1)
			{
				return;
			}

			/*
			 * Order the rings because SSSRFinder.findSSSR() returns rings in an
			 * undeterministic order.
			 */
			AtomContainerSetManipulator.sort(sssr);

			/*
			 *  Mark all the atoms from the ring system as "ISINRING"
			 */
			markRingAtoms(sssr);
			/*
			 *  Give a handle of our molecule to the ringPlacer
			 */
			ringPlacer.setMolecule(molecule);
			ringPlacer.checkAndMarkPlaced(sssr);
			/*
			 *  Partition the smallest set of smallest rings into disconnected ring system.
			 *  The RingPartioner returns a Vector containing RingSets. Each of the RingSets contains
			 *  rings that are connected to each other either as bridged ringsystems, fused rings or
			 *  via spiro connections.
			 */
			ringSystems = RingPartitioner.partitionRings(sssr);

			/*
			 *  We got our ring systems now
			 */

			/*
			 *  Do the layout for the first connected ring system ...
			 */
			int largest = 0;
			int largestSize = ((IRingSet) ringSystems.get(0)).getAtomContainerCount();
			logger.debug("We have " + ringSystems.size() + " ring system(s).");
			for (int f = 0; f < ringSystems.size(); f++)
			{
				logger.debug("RingSet " + f + " has size " + ((IRingSet) ringSystems.get(f)).getAtomContainerCount());
				if (((IRingSet) ringSystems.get(f)).getAtomContainerCount() > largestSize)
				{
					largestSize = ((IRingSet) ringSystems.get(f)).getAtomContainerCount();
					largest = f;
				}
			}
			logger.debug("Largest RingSystem is at RingSet collection's position " + largest);
			logger.debug("Size of Largest RingSystem: " + largestSize);

			layoutRingSet(firstBondVector, (IRingSet) ringSystems.get(largest));
			logger.debug("First RingSet placed");
			/*
			 *  and do the placement of all the directly connected atoms of this ringsystem
			 */
			ringPlacer.placeRingSubstituents((IRingSet) ringSystems.get(largest), bondLength);

		} else
		{

			logger.debug("*** Start of handling purely aliphatic molecules. ***");
			/*
			 *  We are here because there are no rings in the molecule
			 *  so we get the longest chain in the molecule and placed in
			 *  on a horizontal axis
			 */
			logger.debug("Searching initialLongestChain for this purely aliphatic molecule");
			IAtomContainer longestChain = atomPlacer.getInitialLongestChain(molecule);
			logger.debug("Found linear chain of length " + longestChain.getAtomCount());
			logger.debug("Setting coordinated of first atom to 0,0");
			longestChain.getAtom(0).setPoint2d(new Point2d(0, 0));
			longestChain.getAtom(0).setFlag(CDKConstants.ISPLACED, true);

			/*
			 *  place the first bond such that the whole chain will be horizontally
			 *  alligned on the x axis
			 */
			angle = Math.toRadians(-30);
			logger.debug("Attempting to place the first bond such that the whole chain will be horizontally alligned on the x axis");
			if (firstBondVector != null)
				atomPlacer.placeLinearChain(longestChain, firstBondVector, bondLength);
			else
				atomPlacer.placeLinearChain(longestChain, new Vector2d(Math.cos(angle), Math.sin(angle)), bondLength);
			logger.debug("Placed longest aliphatic chain");
		}

		/*
		 *  Now, do the layout of the rest of the molecule
		 */
		do
		{
			safetyCounter++;
			logger.debug("*** Start of handling the rest of the molecule. ***");
			/*
			 *  do layout for all aliphatic parts of the molecule which are
			 *  connected to the parts which have already been laid out.
			 */
			handleAliphatics();
			/*
			 *  do layout for the next ring aliphatic parts of the molecule which are
			 *  connected to the parts which have already been laid out.
			 */
			layoutNextRingSystem();
		} while (!atomPlacer.allPlaced(molecule) && safetyCounter <= molecule.getAtomCount());

		fixRest();
		new OverlapResolver().resolveOverlap(molecule, sssr);
	}


	/**
	 *  The main method of this StructurDiagramGenerator. Assign a molecule to the
	 *  StructurDiagramGenerator, call the generateCoordinates() method and get
	 *  your molecule back.
	 *
	 *  @throws java.lang.Exception if an error occurs
	 */
	public void generateCoordinates() throws java.lang.Exception
	{
		generateCoordinates(new Vector2d(0, 1));
	}


	/**
	 *  Does a layout of all the rings in a given connected RingSet. Uses a TemplateHandler
	 *  to treat templated mapped substructures differently if <code>useTemplates</code> is
	 *  set true.
	 *
	 * @param firstBondVector A vector giving the placement for the first bond
	 * @param rs The connected RingSet for which the layout is to be
	 *           done
	 * @throws java.lang.Exception if an error occurs
	 */
	private void layoutRingSet(Vector2d firstBondVector, IRingSet rs) throws Exception
	{
		IAtomContainer sharedAtoms;
		Vector2d ringCenterVector;
		int thisRing;
		logger.debug("Start of layoutRingSet");

		/*
		 * First we check if we can map any templates with predifined coordinates.
		 * All mapped substructures are saved in: this.mappedSubstructures
		 */
		if (useTemplates && mappedSubstructures.getAtomContainerCount() > 0 && System.getProperty("java.version").indexOf("1.3.") == -1) {
			/*
			 * Find mapped substructures
			 */
			for (Iterator substructureIterator = mappedSubstructures.atomContainers().iterator(); substructureIterator.hasNext(); ) {
				IAtomContainer substructure = (IAtomContainer) substructureIterator.next();
				boolean substructureMapped = false;
				for (Iterator ringSetIterator = rs.atomContainers().iterator(); ringSetIterator.hasNext() && !substructureMapped; ) {
					IRing ring = (IRing) ringSetIterator.next();
					for (Iterator atomIterator = ring.atoms().iterator(); atomIterator.hasNext() && !substructureMapped; ) {
						IAtom atom = (IAtom) atomIterator.next();
							if (substructure.contains(atom))
								substructureMapped = true;
					}
				}
				/*
				 * Layout a found mapped substructure
				 */
				if (substructureMapped) {
					boolean mapped = getTemplateHandler().mapTemplateExact(substructure);
					if (!mapped)
						logger.warn("A supposedly matched substructure failed to match.");
					else {
						// Mark substructure atoms as CDKConstants.ISPLACED
						for (Iterator iterator = substructure.atoms().iterator(); iterator.hasNext(); ) {
							IAtom atom = (IAtom) iterator.next();
							atom.setFlag(CDKConstants.ISPLACED, true);
						}
						// Mark rings of substrucure as CDKConstants.ISPLACED
						ringPlacer.checkAndMarkPlaced(rs);
					}
				}
			}
		}

		/*
		 * Now layout the rest of this ring system
		 */

		/*
		 *  Get the most complex ring in this RingSet
		 */
		IRing ring = RingSetManipulator.getMostComplexRing(rs);
		int i = 0;

		/*
		 *  Place the most complex ring at the origin of the coordinate system
		 */
		if (!ring.getFlag(CDKConstants.ISPLACED))
		{
			sharedAtoms = placeFirstBond((IBond) ring.getBond(i), firstBondVector);
			/*
			 *  Call the method which lays out the new ring.
			 */
			ringCenterVector = ringPlacer.getRingCenterOfFirstRing(ring, firstBondVector, bondLength);
			ringPlacer.placeRing(ring, sharedAtoms, GeometryTools.get2DCenter(sharedAtoms), ringCenterVector, bondLength);
			/*
			 *  Mark the ring as placed
			 */
			ring.setFlag(CDKConstants.ISPLACED, true);
		}
		/*
		 *  Place all other rings in this ringsystem.
		 */
		thisRing = 0;
		do
		{
			if (ring.getFlag(CDKConstants.ISPLACED))
			{
				ringPlacer.placeConnectedRings(rs, ring, RingPlacer.FUSED, bondLength);
				ringPlacer.placeConnectedRings(rs, ring, RingPlacer.BRIDGED, bondLength);
				ringPlacer.placeConnectedRings(rs, ring, RingPlacer.SPIRO, bondLength);
			}
			thisRing++;
			if (thisRing == rs.getAtomContainerCount())
			{
				thisRing = 0;
			}
			ring = (IRing) rs.getAtomContainer(thisRing);
		} while (!allPlaced(rs));
		logger.debug("End of layoutRingSet");
	}



	/**
	 * Does a layout of all aliphatic parts connected to the parts of the molecule
	 * that have already been laid out. Starts at the first bond with unplaced
	 * neighbours and stops when a ring is encountered.
	 *
	 * @throws org.openscience.cdk.exception.CDKException if an error occurs
	 */
	private void handleAliphatics() throws org.openscience.cdk.exception.CDKException
	{
		logger.debug("Start of handleAliphatics");

		int safetyCounter = 0;
		IAtomContainer unplacedAtoms = null;
		IAtomContainer placedAtoms = null;
		IAtomContainer longestUnplacedChain = null;
		IAtom atom = null;

		Vector2d direction = null;
		Vector2d startVector = null;
		boolean done;
		do
		{
			safetyCounter++;
			done = false;
			atom = getNextAtomWithAliphaticUnplacedNeigbors();
			if (atom != null)
			{
				unplacedAtoms = getUnplacedAtoms(atom);
				placedAtoms = getPlacedAtoms(atom);

				longestUnplacedChain = atomPlacer.getLongestUnplacedChain(molecule, atom);

				logger.debug("---start of longest unplaced chain---");
				try
				{
					logger.debug("Start at atom no. " + (molecule.getAtomNumber(atom) + 1));
					logger.debug(atomPlacer.listNumbers(molecule, longestUnplacedChain));
				} catch (Exception exc) {
					logger.debug(exc);
				}
				logger.debug("---end of longest unplaced chain---");

				if (longestUnplacedChain.getAtomCount() > 1)
				{

					if (placedAtoms.getAtomCount() > 1)
					{
						logger.debug("More than one atoms placed already");
						logger.debug("trying to place neighbors of atom " + (molecule.getAtomNumber(atom) + 1));
						atomPlacer.distributePartners(atom, placedAtoms, GeometryTools.get2DCenter(placedAtoms), unplacedAtoms, bondLength);
						direction = new Vector2d(longestUnplacedChain.getAtom(1).getPoint2d());
						startVector = new Vector2d(atom.getPoint2d());
						direction.sub(startVector);
						logger.debug("Done placing neighbors of atom " + (molecule.getAtomNumber(atom) + 1));
					} else
					{
						logger.debug("Less than or equal one atoms placed already");
						logger.debug("Trying to get next bond vector.");
						direction = atomPlacer.getNextBondVector(atom, placedAtoms.getAtom(0), GeometryTools.get2DCenter(molecule),true);

					}

					for (int f = 1; f < longestUnplacedChain.getAtomCount(); f++)
					{
						longestUnplacedChain.getAtom(f).setFlag(CDKConstants.ISPLACED, false);
					}
					atomPlacer.placeLinearChain(longestUnplacedChain, direction, bondLength);

				} else
				{
					done = true;
				}
			} else
			{
				done = true;
			}
		} while (!done && safetyCounter <= molecule.getAtomCount());

		logger.debug("End of handleAliphatics");
	}


	/**
	 *  Does the layout for the next RingSystem that is connected to those parts of
	 *  the molecule that have already been laid out. Finds the next ring with an
	 *  unplaced ring atom and lays out this ring. Then lays out the ring substituents
	 *  of this ring. Then moves and rotates the laid out ring to match the position
	 *  of its attachment bond to the rest of the molecule.
	 *
	 *  @throws java.lang.Exception if an error occurs
	 */
	private void layoutNextRingSystem() throws Exception
	{
		logger.debug("Start of layoutNextRingSystem()");

		resetUnplacedRings();
		IAtomContainer tempAc = atomPlacer.getPlacedAtoms(molecule);
		logger.debug("Finding attachment bond to already placed part...");
		IBond nextRingAttachmentBond = getNextBondWithUnplacedRingAtom();
		if (nextRingAttachmentBond != null)
		{
			logger.debug("...bond found.");

			/*
			 * Get the chain and the ring atom that are connected to where we are comming from.
			 * Both are connected by nextRingAttachmentBond.
			 */
			IAtom ringAttachmentAtom = getRingAtom(nextRingAttachmentBond);
			IAtom chainAttachmentAtom = getOtherBondAtom(ringAttachmentAtom, nextRingAttachmentBond);

			/*
			 * Get ring system which ringAttachmentAtom is part of
			 */
			IRingSet nextRingSystem = getRingSystemOfAtom(ringSystems, ringAttachmentAtom);

			/*
			 * Get all rings of nextRingSytem as one IAtomContainer
			 */
			IAtomContainer ringSystem = tempAc.getBuilder().newAtomContainer();
			for (Iterator containers = RingSetManipulator.getAllAtomContainers(nextRingSystem).iterator(); containers.hasNext(); )
				ringSystem.add((IAtomContainer) containers.next());

			/*
			 * Save coordinates of ringAttachmentAtom and chainAttachmentAtom
			 */
			Point2d oldRingAttachmentAtomPoint = ringAttachmentAtom.getPoint2d();
			Point2d oldChainAttachmentAtomPoint = chainAttachmentAtom.getPoint2d();

			/*
			 *  Do the layout of the next ring system
			 */
			layoutRingSet(firstBondVector, nextRingSystem);

			/*
			 *  Place all the substituents of next ring system
			 */
			atomPlacer.markNotPlaced(tempAc);
			IAtomContainer placedRingSubstituents = ringPlacer.placeRingSubstituents(nextRingSystem, bondLength);
			ringSystem.add(placedRingSubstituents);
			atomPlacer.markPlaced(tempAc);

			/*
			 * Move and rotate the laid out ring system to match the geometry of the
			 * attachment bond
			 */
			logger.debug("Computing translation/rotation of new ringset to fit old attachment bond orientation...");

			// old placed ring atom coordinate
			Point2d oldPoint2 = oldRingAttachmentAtomPoint;
			// old placed substituent atom coordinate
			Point2d oldPoint1 = oldChainAttachmentAtomPoint;

			// new placed ring atom coordinate
			Point2d newPoint2 = ringAttachmentAtom.getPoint2d();
			// new placed substituent atom coordinate
			Point2d newPoint1 = chainAttachmentAtom.getPoint2d();

			logger.debug("oldPoint1: " + oldPoint1);
			logger.debug("oldPoint2: " + oldPoint2);
			logger.debug("newPoint1: " + newPoint1);
			logger.debug("newPoint2: " + newPoint2);

			double oldAngle = GeometryTools.getAngle(oldPoint2.x - oldPoint1.x, oldPoint2.y - oldPoint1.y);
			double newAngle = GeometryTools.getAngle(newPoint2.x - newPoint1.x, newPoint2.y - newPoint1.y);
			double angleDiff = oldAngle - newAngle;

			logger.debug("oldAngle: " + oldAngle + ", newAngle: " + newAngle + "; diff = " + angleDiff);

			Vector2d translationVector = new Vector2d(oldPoint1);
			translationVector.sub(new Vector2d(newPoint1));

			/*
			 * Move to fit old attachment bond orientation
			 */
			GeometryTools.translate2D(ringSystem, translationVector);

			/*
			 * Rotate to fit old attachment bond orientation
			 */
			GeometryTools.rotate(ringSystem, oldPoint1, angleDiff);

			logger.debug("...done translating/rotating new ringset to fit old attachment bond orientation.");
		}
		else
			logger.debug("...no bond found");

		logger.debug("End of layoutNextRingSystem()");
	}


	/**
	 *  Returns an AtomContainer with all unplaced atoms connected to a given
	 *  atom
	 *
	 *  @param  atom  The Atom whose unplaced bonding partners are to be returned
	 *  @return       an AtomContainer with all unplaced atoms connected to a
	 *                given atom
	 */
	private IAtomContainer getUnplacedAtoms(IAtom atom)
	{
		IAtomContainer unplacedAtoms = atom.getBuilder().newAtomContainer();
		java.util.List bonds = molecule.getConnectedBondsList(atom);
		IAtom connectedAtom;
		for (int f = 0; f < bonds.size(); f++)
		{
			connectedAtom = ((IBond)bonds.get(f)).getConnectedAtom(atom);
			if (!connectedAtom.getFlag(CDKConstants.ISPLACED))
			{
				unplacedAtoms.addAtom(connectedAtom);
			}
		}
		return unplacedAtoms;
	}


	/**
	 *  Returns an AtomContainer with all placed atoms connected to a given
	 *  atom
	 *
	 *  @param  atom  The Atom whose placed bonding partners are to be returned
	 *  @return       an AtomContainer with all placed atoms connected to a given
	 *                atom
	 */
	private IAtomContainer getPlacedAtoms(IAtom atom)
	{
		IAtomContainer placedAtoms = atom.getBuilder().newAtomContainer();
		java.util.List bonds = molecule.getConnectedBondsList(atom);
		IAtom connectedAtom;
		for (int f = 0; f < bonds.size(); f++)
		{
			connectedAtom = ((IBond)bonds.get(f)).getConnectedAtom(atom);
			if (connectedAtom.getFlag(CDKConstants.ISPLACED))
			{
				placedAtoms.addAtom(connectedAtom);
			}
		}
		return placedAtoms;
	}


	/**
	 *  Returns the next atom with unplaced aliphatic neighbors
	 *
	 *  @return    the next atom with unplaced aliphatic neighbors
	 */
	private IAtom getNextAtomWithAliphaticUnplacedNeigbors()
	{
		IBond bond;
		for (int f = 0; f < molecule.getBondCount(); f++)
		{
			bond = molecule.getBond(f);

			if (bond.getAtom(1).getFlag(CDKConstants.ISPLACED) &&
				!bond.getAtom(0).getFlag(CDKConstants.ISPLACED))
			{
				return bond.getAtom(1);
			}

			if (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) &&
				!bond.getAtom(1).getFlag(CDKConstants.ISPLACED))
			{
				return bond.getAtom(0);
			}
		}
		return null;
	}


	/**
	 *  Returns the next bond with an unplaced ring atom
	 *
	 *  @return    the next bond with an unplaced ring atom
	 */
	private IBond getNextBondWithUnplacedRingAtom() {
		Iterator bonds = molecule.bonds().iterator();
		while (bonds.hasNext()) {
			IBond bond = (IBond) bonds.next();

			if (bond.getAtom(0).getPoint2d() != null &&
					bond.getAtom(1).getPoint2d() != null) {
				if (bond.getAtom(1).getFlag(CDKConstants.ISPLACED) &&
						!bond.getAtom(0).getFlag(CDKConstants.ISPLACED) &&
						bond.getAtom(0).getFlag(CDKConstants.ISINRING)) {
					return bond;
				}

				if (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) &&
						!bond.getAtom(1).getFlag(CDKConstants.ISPLACED) &&
						bond.getAtom(1).getFlag(CDKConstants.ISINRING)) {
					return bond;
				}
			}
		}
		return null;
	}


	/**
	 *  Places the first bond of the first ring such that one atom is at (0,0) and
	 *  the other one at the position given by bondVector
	 *
	 *  @param  bondVector  A 2D vector to point to the position of the second bond
	 *                      atom
	 *  @param  bond        the bond to lay out
	 *  @return             an IAtomContainer with the atoms of the bond and the bond itself
	 */
	private IAtomContainer placeFirstBond(IBond bond, Vector2d bondVector)
	{
		IAtomContainer sharedAtoms = null;
		try
		{
			bondVector.normalize();
			logger.debug("placeFirstBondOfFirstRing->bondVector.length():" + bondVector.length());
			bondVector.scale(bondLength);
			logger.debug("placeFirstBondOfFirstRing->bondVector.length() after scaling:" + bondVector.length());
			IAtom atom;
			Point2d point = new Point2d(0, 0);
			atom = bond.getAtom(0);
			logger.debug("Atom 1 of first Bond: " + (molecule.getAtomNumber(atom) + 1));
			atom.setPoint2d(point);
			atom.setFlag(CDKConstants.ISPLACED, true);
			point = new Point2d(0, 0);
			atom = bond.getAtom(1);
			logger.debug("Atom 2 of first Bond: " + (molecule.getAtomNumber(atom) + 1));
			point.add(bondVector);
			atom.setPoint2d(point);
			atom.setFlag(CDKConstants.ISPLACED, true);
			/*
			 *  The new ring is layed out relativ to some shared atoms that have already been
			 *  placed. Usually this is another ring, that has already been draw and to which the new
			 *  ring is somehow connected, or some other system of atoms in an aliphatic chain.
			 *  In this case, it's the first bond that we layout by hand.
			 */
			sharedAtoms = atom.getBuilder().newAtomContainer();
			sharedAtoms.addBond(bond);
			sharedAtoms.addAtom(bond.getAtom(0));
			sharedAtoms.addAtom(bond.getAtom(1));
		} catch (Exception exc) {
			logger.debug(exc);
		}
		return sharedAtoms;
	}


	/**
	 *  This method will go as soon as the rest works. It just assignes Point2d's
	 *  of position (0,0) so that the molecule can be drawn.
	 */
	private void fixRest()
	{
		IAtom atom = null;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			atom = molecule.getAtom(f);
			if (atom.getPoint2d() == null)
			{
				atom.setPoint2d(new Point2d(0, 0));
			}
		}
	}

	/**
	 *  This method will go as soon as the rest works. It just assignes Point2d's
	 *  of position (0,0) so that the molecule can be drawn.
	 *  @param molecule the molecule to fix
	 *  @return the fixed molecule
	 */
	private IMolecule fixMol(IMolecule molecule)
	{
		IAtom atom = null;
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			atom = molecule.getAtom(f);
			if (atom.getPoint2d() == null)
			{
				atom.setPoint2d(new Point2d(0, 0));
			}
		}
								return molecule;
	}

	/**
	 *  Initializes all rings in RingSet rs as not placed
	 *
	 *  @param  rs  The RingSet to be initialized
	 */
//	private void markNotPlaced(IRingSet rs)
//	{
//		for (int f = 0; f < rs.size(); f++)
//		{
//			((IRing) rs.get(f)).setFlag(CDKConstants.ISPLACED, false);
//		}
//	}


	/**
	 *  Are all rings in the Vector placed?
	 *
	 *  @param  rings  The Vector to be checked
	 *  @return        true if all rings are placed, false otherwise
	 */
	private boolean allPlaced(IRingSet rings)
	{
		for (int f = 0; f < rings.getAtomContainerCount(); f++)
		{
			if (!((IRing) rings.getAtomContainer(f)).getFlag(CDKConstants.ISPLACED))
			{
				logger.debug("allPlaced->Ring " + f + " not placed");
				return false;
			}
		}
		return true;
	}


	/**
	 *  Mark all atoms in the molecule as being part of a ring
	 *
	 *  @param  rings  an IRingSet with the rings to process
	 */
	private void markRingAtoms(IRingSet rings)
	{
		IRing ring = null;
		for (int i = 0; i < rings.getAtomContainerCount(); i++)
		{
			ring = (IRing) rings.getAtomContainer(i);
			for (int j = 0; j < ring.getAtomCount(); j++)
			{
				ring.getAtom(j).setFlag(CDKConstants.ISINRING, true);
			}
		}
	}


	/**
	 *  Get the unplaced ring atom in this bond
	 *
	 *  @param  bond  the bond to be search for the unplaced ring atom
	 *  @return       the unplaced ring atom in this bond
	 */
	private IAtom getRingAtom(IBond bond)
	{
		if (bond.getAtom(0).getFlag(CDKConstants.ISINRING) &&
				!bond.getAtom(0).getFlag(CDKConstants.ISPLACED))
		{
			return bond.getAtom(0);
		}
		if (bond.getAtom(1).getFlag(CDKConstants.ISINRING) &&
				!bond.getAtom(1).getFlag(CDKConstants.ISPLACED))
		{
			return bond.getAtom(1);
		}
		return null;
	}


	/**
	 *  Get the ring system of which the given atom is part of
	 *
	 *  @param  ringSystems  a List of ring systems to be searched
	 *  @param  ringAtom     the ring atom to be search in the ring system.
	 *  @return              the ring system the given atom is part of
	 */
	private IRingSet getRingSystemOfAtom(List ringSystems, IAtom ringAtom)
	{
		IRingSet ringSet = null;
		for (int f = 0; f < ringSystems.size(); f++)
		{
			ringSet = (IRingSet) ringSystems.get(f);
			if (ringSet.contains(ringAtom))
			{
				return ringSet;
			}
		}
		return null;
	}


	/**
	 *  Set all the atoms in unplaced rings to be unplaced
	 */
	private void resetUnplacedRings()
	{
		IRing ring = null;
		if (sssr == null)
		{
			return;
		}
		int unplacedCounter = 0;
		for (int f = 0; f < sssr.getAtomContainerCount(); f++)
		{
			ring = (IRing) sssr.getAtomContainer(f);
			if (!ring.getFlag(CDKConstants.ISPLACED))
			{
				logger.debug("Ring with " + ring.getAtomCount() + " atoms is not placed.");
				unplacedCounter++;
				for (int g = 0; g < ring.getAtomCount(); g++)
				{
					ring.getAtom(g).setFlag(CDKConstants.ISPLACED, false);
				}
			}
		}
		logger.debug("There are " + unplacedCounter + " unplaced Rings.");
	}


	/**
	 *  Set the bond length used for laying out the molecule.
	 *  The defaut value is 1.5.
	 *
	 *  @param  bondLength  The new bondLength value
	 */
	public void setBondLength(double bondLength)
	{
		this.bondLength = bondLength;
	}

	/**
	 * Returns the other atom of the bond.
	 * Expects bond to have only two atoms.
	 * Returns null if the given atom is not part of the given bond.
	 *
	 * @param atom the atom we already have
	 * @param bond the bond
	 * @return the other atom of the bond
	 */
	public IAtom getOtherBondAtom(IAtom atom, IBond bond) {
		if (!bond.contains(atom))
			return null;
		if (bond.getAtom(0).equals(atom))
			return bond.getAtom(1);
		else
			return bond.getAtom(0);
	}

}

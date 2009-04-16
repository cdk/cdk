/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 *  Methods for generating coordinates for atoms in various situations. They can
 *  be used for Automated Structure Diagram Generation or in the interactive
 *  buildup of molecules by the user.
 *
 *@author      steinbeck
 *@cdk.created 2003-08-29
 *@cdk.module  sdg
 * @cdk.svnrev  $Revision$
 */
public class AtomPlacer
{

    public final static boolean debug = true;
    private static LoggingTool logger = new LoggingTool(AtomPlacer.class);

    /**
     *  The molecule to be laid out. To be assigned from outside
     */
     IAtomContainer molecule = null;

    final  Comparator ATOM_ORDER =
        new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                IAtom a1 = (IAtom) o1;
                IAtom a2 = (IAtom) o2;
                int i1 = ((Integer) a1.getProperty("Weight")).intValue();
                int i2 = ((Integer) a2.getProperty("Weight")).intValue();
                if (i1 < i2)
                {
                    return -1;
                }
                if (i1 == i2)
                {
                    return 0;
                }
                return 1;
            }
        };


    /**
     *  Constructor for the AtomPlacer object
     */
    public AtomPlacer()
    {
    }


    /**
     *  Return the molecule the AtomPlacer currently works with
     *
     *@return    the molecule the AtomPlacer currently works with
     */
    public IAtomContainer getMolecule()
    {
        return this.molecule;
    }


    /**
     *  Sets the molecule the AtomPlacer currently works with
     *
     *@param  molecule  the molecule the AtomPlacer currently works with
     */
    public void setMolecule(IAtomContainer molecule)
    {
        this.molecule = molecule;
    }



    /**
     *  Distribute the bonded atoms (neighbours) of an atom such that they fill the
     *  remaining space around an atom in a geometrically nice way.
     *  IMPORTANT: This method is not supposed to handle the
     *  case of one or no place neighbor. In the case of
     *  one placed neigbor, the chain placement methods
     *  should be used.
     *
     *@param  atom                The atom whose partners are to be placed
     *@param  placedNeighbours    The atoms which are already placed
     *@param  unplacedNeighbours  The partners to be placed
     *@param  bondLength          The standared bond length for the newly placed
     *      Atoms
     *@param  sharedAtomsCenter   The 2D centre of the placed Atoms
     */
    public void distributePartners(IAtom atom, IAtomContainer placedNeighbours, Point2d sharedAtomsCenter,
            IAtomContainer unplacedNeighbours, double bondLength)
    {
        double occupiedAngle = 0;
        //double smallestDistance = Double.MAX_VALUE;
        //IAtom[] nearestAtoms = new IAtom[2];
        IAtom[] sortedAtoms = null;
        double startAngle = 0.0;
        double addAngle = 0.0;
        double radius = 0.0;
        double remainingAngle = 0.0;
        /*
         *  calculate the direction away from the already placed partners of atom
         */
        //Point2d sharedAtomsCenter = sharedAtoms.get2DCenter();
        Vector2d sharedAtomsCenterVector = new Vector2d(sharedAtomsCenter);

        Vector2d newDirection = new Vector2d(atom.getPoint2d());
        Vector2d occupiedDirection = new Vector2d(sharedAtomsCenter);
        occupiedDirection.sub(newDirection);
        logger.debug("distributePartners->occupiedDirection.lenght(): " + occupiedDirection.length());
        Vector atomsToDraw = new Vector();

        logger.debug("Number of shared atoms: ", placedNeighbours.getAtomCount());

        /*
         *    IMPORTANT: This method is not supposed to handle the
         *    case of one or no place neighbor. In the case of
         *    one placed neigbor, the chain placement methods
         *    should be used.
         */
        if (placedNeighbours.getAtomCount() == 1)
        {
            logger.debug("Only one neighbour...");
            for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++)
            {
                atomsToDraw.addElement(unplacedNeighbours.getAtom(f));
            }

            addAngle = Math.PI * 2 / (unplacedNeighbours.getAtomCount() + placedNeighbours.getAtomCount());
            /*
             *  IMPORTANT: At this point we need a calculation of the
             *  start angle.
             *  Not done yet.
             */
            IAtom placedAtom = placedNeighbours.getAtom(0);
//			double xDiff = atom.getX2d() - placedAtom.getX2d();
//			double yDiff = atom.getY2d() - placedAtom.getY2d();
            double xDiff = placedAtom.getPoint2d().x - atom.getPoint2d().x;
            double yDiff = placedAtom.getPoint2d().y - atom.getPoint2d().y;

            logger.debug("distributePartners->xdiff: " + Math.toDegrees(xDiff));
            logger.debug("distributePartners->ydiff: " + Math.toDegrees(yDiff));
            startAngle = GeometryTools.getAngle(xDiff, yDiff);
            //- (Math.PI / 2.0);
            logger.debug("distributePartners->angle: " + Math.toDegrees(startAngle));

            populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, bondLength);
            return;
        } else if (placedNeighbours.getAtomCount() == 0)
        {
            logger.debug("First atom...");
            for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++)
            {
                atomsToDraw.addElement(unplacedNeighbours.getAtom(f));
            }

            addAngle = Math.PI * 2.0 / unplacedNeighbours.getAtomCount();
            /*
             * IMPORTANT: At this point we need a calculation of the
             * start angle. Not done yet.
             */
            startAngle = 0.0;
            populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, bondLength);
            return;
        }

        /*
         *  if the least hindered side of the atom is clearly defined (bondLength / 10 is an arbitrary value that seemed reasonable)
         */
        //newDirection.sub(sharedAtomsCenterVector);
        sharedAtomsCenterVector.sub(newDirection);
        newDirection = sharedAtomsCenterVector;
        newDirection.normalize();
        newDirection.scale(bondLength);
        newDirection.negate();
        logger.debug("distributePartners->newDirection.lenght(): " + newDirection.length());
        Point2d distanceMeasure = new Point2d(atom.getPoint2d());
        distanceMeasure.add(newDirection);

        /*
         *  get the two sharedAtom partners with the smallest distance to the new center
         */
        sortedAtoms = AtomContainerManipulator.getAtomArray(placedNeighbours);
        GeometryTools.sortBy2DDistance(sortedAtoms, distanceMeasure);
        Vector2d closestPoint1 = new Vector2d(sortedAtoms[0].getPoint2d());
        Vector2d closestPoint2 = new Vector2d(sortedAtoms[1].getPoint2d());
        closestPoint1.sub(new Vector2d(atom.getPoint2d()));
        closestPoint2.sub(new Vector2d(atom.getPoint2d()));
        occupiedAngle = closestPoint1.angle(occupiedDirection);
        occupiedAngle += closestPoint2.angle(occupiedDirection);

        double angle1 = GeometryTools.getAngle(sortedAtoms[0].getPoint2d().x - atom.getPoint2d().x, sortedAtoms[0].getPoint2d().y - atom.getPoint2d().y);
        double angle2 = GeometryTools.getAngle(sortedAtoms[1].getPoint2d().x - atom.getPoint2d().x, sortedAtoms[1].getPoint2d().y - atom.getPoint2d().y);
        double angle3 = GeometryTools.getAngle(distanceMeasure.x - atom.getPoint2d().x, distanceMeasure.y - atom.getPoint2d().y);
        if (debug)
        {
            try
            {
                logger.debug("distributePartners->sortedAtoms[0]: ", (molecule.getAtomNumber(sortedAtoms[0]) + 1));
                logger.debug("distributePartners->sortedAtoms[1]: ", (molecule.getAtomNumber(sortedAtoms[1]) + 1));
                logger.debug("distributePartners->angle1: ", Math.toDegrees(angle1));
                logger.debug("distributePartners->angle2: ", Math.toDegrees(angle2));
            } catch (Exception exc) {
                logger.debug(exc);
            }
        }
        IAtom startAtom = null;

        if (angle1 > angle3)
        {
            if (angle1 - angle3 < Math.PI)
            {
                startAtom = sortedAtoms[1];
            } else
            {
                // 12 o'clock is between the two vectors
                startAtom = sortedAtoms[0];
            }

        } else
        {
            if (angle3 - angle1 < Math.PI)
            {
                startAtom = sortedAtoms[0];
            } else
            {
                // 12 o'clock is between the two vectors
                startAtom = sortedAtoms[1];
            }
        }
        remainingAngle = (2 * Math.PI) - occupiedAngle;
        addAngle = remainingAngle / (unplacedNeighbours.getAtomCount() + 1);
        if (debug)
        {
            try
            {
                logger.debug("distributePartners->startAtom: " + (molecule.getAtomNumber(startAtom) + 1));
                logger.debug("distributePartners->remainingAngle: " + Math.toDegrees(remainingAngle));
                logger.debug("distributePartners->addAngle: " + Math.toDegrees(addAngle));
                logger.debug("distributePartners-> partners.getAtomCount(): " + unplacedNeighbours.getAtomCount());
            } catch (Exception exc)
            {
                logger.debug(exc);
            }

        }
        for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++)
        {
            atomsToDraw.addElement(unplacedNeighbours.getAtom(f));
        }
        radius = bondLength;
        startAngle = GeometryTools.getAngle(startAtom.getPoint2d().x - atom.getPoint2d().x, startAtom.getPoint2d().y - atom.getPoint2d().y);
        logger.debug("Before check: distributePartners->startAngle: " + startAngle);
//        if (startAngle < (Math.PI + 0.001) && startAngle > (Math.PI
//            -0.001))
//        {
//            startAngle = Math.PI/placedNeighbours.getAtomCount();
//        }
        logger.debug("After check: distributePartners->startAngle: " + startAngle);
        populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, radius);
    }


    /**
     * Places the atoms in a linear chain.
     *
     * <p>Expects the first atom to be placed and
     * places the next atom according to initialBondVector. The rest of the chain
     * is placed such that it is as linear as possible (in the overall result, the
     * angles in the chain are set to 120 Deg.)
     *
     * @param  atomContainer  The IAtomContainer containing the chain atom to be placed
     * @param  initialBondVector  The Vector indicating the direction of the first bond
     * @param  bondLength         The factor used to scale the initialBondVector
     */
    public void placeLinearChain(IAtomContainer atomContainer, Vector2d initialBondVector, double bondLength)
    {
        IMolecule withh = atomContainer.getBuilder().newMolecule(atomContainer);

        // BUGFIX - withh does not have cloned cloned atoms, so changes are
        // reflected in our atom container. If we're using implicit hydrogens
        // the correct counts need saving and restoring
        int[] numh = new int[atomContainer.getAtomCount()];
        for (int i = 0, n = atomContainer.getAtomCount(); i < n; i ++) {
            Integer tmp = atomContainer.getAtom(i).getHydrogenCount();
            if (tmp == CDKConstants.UNSET) numh[i]= 0;
            else numh[i] = tmp;            
        }

//		SDG should lay out what it gets and not fiddle with molecules
//      during layout so this was 
//      removed during debugging. Before you put this in again, contact
//      er@doktor-steinbeck.de
        
//        if(GeometryTools.has2DCoordinatesNew(atomContainer)==2){
//            try{
//                new HydrogenAdder().addExplicitHydrogensToSatisfyValency(withh);
//            }catch(Exception ex){
//                logger.warn("Exception in hydrogen adding. This could mean that cleanup does not respect E/Z: ", ex.getMessage());
//                logger.debug(ex);
//            }
//            new HydrogenPlacer().placeHydrogens2D(withh, bondLength);
//        }
        logger.debug("Placing linear chain of length " + atomContainer.getAtomCount());
        Vector2d bondVector = initialBondVector;
        IAtom atom = null;
        Point2d atomPoint = null;
        IAtom nextAtom = null;
        for (int f = 0; f < atomContainer.getAtomCount() - 1; f++)
        {
            atom = atomContainer.getAtom(f);
            nextAtom = atomContainer.getAtom(f + 1);
            atomPoint = new Point2d(atom.getPoint2d());
            bondVector.normalize();
            bondVector.scale(bondLength);
            atomPoint.add(bondVector);
            nextAtom.setPoint2d(atomPoint);
            nextAtom.setFlag(CDKConstants.ISPLACED, true);
            boolean trans=false;
            if(GeometryTools.has2DCoordinatesNew(atomContainer)==2){
                try{
                    if(f>2 && BondTools.isValidDoubleBondConfiguration(withh,withh.getBond(withh.getAtom(f-2),withh.getAtom(f-1)))){
                        trans=BondTools.isCisTrans(withh.getAtom(f-3),withh.getAtom(f-2),withh.getAtom(f-1),withh.getAtom(f-0),withh);
                    }
                }catch(Exception ex){
                    logger.debug("Excpetion in detecting E/Z. This could mean that cleanup does not respect E/Z");
                }
                bondVector = getNextBondVector(nextAtom, atom, GeometryTools.get2DCenter(molecule),trans);
            }else{
                bondVector = getNextBondVector(nextAtom, atom, GeometryTools.get2DCenter(molecule),true);
            }
        }

        // BUGFIX part 2 - restore hydrogen counts
        for (int i = 0, n = atomContainer.getAtomCount(); i < n; i ++) {
            atomContainer.getAtom(i).setHydrogenCount(numh[i]);
        }
    }


    /**
     *  Returns the next bond vector needed for drawing an extended linear chain of
     *  atoms. It assumes an angle of 120 deg for a nice chain layout and
     *  calculates the two possible placments for the next atom. It returns the
     *  vector pointing farmost away from a given start atom.
     *
     *@param  atom             An atom for which the vector to the next atom to
     *      draw is calculated
     *@param  previousAtom     The preceding atom for angle calculation
     *@param  distanceMeasure  A point from which the next atom is to be farmost
     *      away
     *@param   trans           if true E (trans) configurations are built, false makes Z (cis) configurations
     *@return                  A vector pointing to the location of the next atom
     *      to draw
     */
    public Vector2d getNextBondVector(IAtom atom, IAtom previousAtom, Point2d distanceMeasure, boolean trans)
    {
    if (logger.isDebugEnabled())
    {
          logger.debug("Entering AtomPlacer.getNextBondVector()");
          logger.debug("Arguments are atom: " + atom + ", previousAtom: " + previousAtom + ", distanceMeasure: " + distanceMeasure);
    }
        double angle = GeometryTools.getAngle(previousAtom.getPoint2d().x - atom.getPoint2d().x, previousAtom.getPoint2d().y - atom.getPoint2d().y);
        double addAngle = Math.toRadians(120);
        if(!trans)
            addAngle=Math.toRadians(60);
        if (shouldBeLinear(atom, molecule)) addAngle = Math.toRadians(180);

        angle += addAngle;
        Vector2d vec1 = new Vector2d(Math.cos(angle), Math.sin(angle));
        Point2d point1 = new Point2d(atom.getPoint2d());
        point1.add(vec1);
        double distance1 = point1.distance(distanceMeasure);
        angle += addAngle;
        Vector2d vec2 = new Vector2d(Math.cos(angle), Math.sin(angle));
        Point2d point2 = new Point2d(atom.getPoint2d());
        point2.add(vec2);
        double distance2 = point2.distance(distanceMeasure);
        if (distance2 > distance1)
        {
            logger.debug("Exiting AtomPlacer.getNextBondVector()");
            return vec2;
        }
        logger.debug("Exiting AtomPlacer.getNextBondVector()");
        return vec1;
    }


    /**
     *  Populates the corners of a polygon with atoms. Used to place atoms in a
     *  geometrically regular way around a ring center or another atom. If this is
     *  used to place the bonding partner of an atom (and not to draw a ring) we
     *  want to place the atoms such that those with highest "weight" are placed
     *  farmost away from the rest of the molecules. The "weight" mentioned here is
     *  calculated by a modified morgan number algorithm.
     *
     *@param  atomsToDraw     All the atoms to draw
     *@param  startAngle      A start angle, giving the angle of the most clockwise
     *      atom which has already been placed
     *@param  addAngle        An angle to be added to startAngle for each atom from
     *      atomsToDraw
     *@param  rotationCenter  The center of a ring, or an atom for which the
     *      partners are to be placed
     *@param  radius          The radius of the polygon to be populated: bond
     *      length or ring radius
     */
    public void populatePolygonCorners(List<IAtom> atomsToDraw, Point2d rotationCenter, double startAngle, double addAngle, double radius)
    {
        IAtom connectAtom = null;
        double angle = startAngle;
        double newX;
        double newY;
        double x;
        double y;
        logger.debug("populatePolygonCorners->startAngle: ", Math.toDegrees(angle));
        Vector points = new Vector();
        //IAtom atom = null;

        logger.debug("  centerX:", rotationCenter.x);
        logger.debug("  centerY:", rotationCenter.y);
        logger.debug("  radius :", radius);

        for (int i = 0; i < atomsToDraw.size(); i++)
        {
            angle = angle + addAngle;
            if (angle >= 2.0 * Math.PI)
            {
                angle -= 2.0 * Math.PI;
            }
            logger.debug("populatePolygonCorners->angle: ", Math.toDegrees(angle));
            x = Math.cos(angle) * radius;
            y = Math.sin(angle) * radius;
            newX = x + rotationCenter.x;
            newY = y + rotationCenter.y;
            logger.debug("  newX:", newX);
            logger.debug("  newY:", newY);
            points.addElement(new Point2d(newX, newY));

      if (logger.isDebugEnabled())
      try
            {
                logger.debug("populatePolygonCorners->connectAtom: " + (molecule.getAtomNumber(connectAtom) + 1) + " placed at " + connectAtom.getPoint2d());
            } catch (Exception exc)
            {
                //nothing to catch here. This is just for logging
            }
        }

        for (int i = 0; i < atomsToDraw.size(); i++)
        {
            connectAtom = (IAtom) atomsToDraw.get(i);
            connectAtom.setPoint2d((Point2d) points.elementAt(i));
            connectAtom.setFlag(CDKConstants.ISPLACED, true);
        }

    }

    /**
     *  Partition the bonding partners of a given atom into placed (coordinates
     *  assinged) and not placed.
     *
     *@param  atom              The atom whose bonding partners are to be
     *      partitioned
     *@param  unplacedPartners  A vector for the unplaced bonding partners to go in
     *@param  placedPartners    A vector for the placed bonding partners to go in
     */
    public void partitionPartners(IAtom atom, IAtomContainer unplacedPartners, IAtomContainer placedPartners)
    {
        java.util.List atoms = molecule.getConnectedAtomsList(atom);
        for (int i = 0; i < atoms.size(); i++)
        {
            IAtom curatom = (IAtom)atoms.get(i);
            if (curatom.getFlag(CDKConstants.ISPLACED))
            {
                placedPartners.addAtom(curatom);
            } else
            {
                unplacedPartners.addAtom(curatom);
            }
        }
    }


    /**
     *  Search an aliphatic molecule for the longest chain. This is the method to
     *  be used if there are no rings in the molecule and you want to layout the
     *  longest chain in the molecule as a starting point of the structure diagram
     *  generation.
     *
     *@param  molecule                                               The molecule
     *      to be search for the longest unplaced chain
     *@return                                                        An
     *      AtomContainer holding the longest chain.
     *@exception  org.openscience.cdk.exception.NoSuchAtomException  Description of
     *      the Exception
     */
    public IAtomContainer getInitialLongestChain(IMolecule molecule) throws org.openscience.cdk.exception.CDKException
    {
        logger.debug("Start of getInitialLongestChain()");
        double[][] conMat = ConnectionMatrix.getMatrix(molecule);
        logger.debug("Computing all-pairs-shortest-pathes");
        int[][] apsp = PathTools.computeFloydAPSP(conMat);
        int maxPathLength = 0;
        int bestStartAtom = -1;
        int bestEndAtom = -1;
        IAtom atom = null;
        IAtom startAtom = null;
        //IAtom endAtom = null;
        for (int f = 0; f < apsp.length; f++)
        {
            atom = molecule.getAtom(f);
            if (molecule.getConnectedBondsCount(atom) == 1)
            {
                for (int g = 0; g < apsp.length; g++)
                {
                    if (apsp[f][g] > maxPathLength)
                    {
                        maxPathLength = apsp[f][g];
                        bestStartAtom = f;
                        bestEndAtom = g;
                    }
                }
            }
        }
        logger.debug("Longest chaing in molecule is of length " + maxPathLength + " between atoms " + (bestStartAtom+1) +  " and " + (bestEndAtom+1) );

        startAtom = molecule.getAtom(bestStartAtom);
        //endAtom = molecule.getAtomAt(bestEndAtom);
        IAtomContainer path = molecule.getBuilder().newAtomContainer();
        path.addAtom(startAtom);
        path = getLongestUnplacedChain(molecule, startAtom);
        //PathTools.depthFirstTargetSearch(molecule, startAtom, endAtom, path);
        logger.debug("End of getInitialLongestChain()");
        return path;
    }



    /**
     *  Search a molecule for the longest unplaced, aliphatic chain in it. If an
     *  aliphatic chain encounters an unplaced ring atom, the ring atom is also
     *  appended to allow for it to be laid out. This gives us a vector for
     *  attaching the unplaced ring later.
     *
     *@param  molecule                                        The molecule to be
     *      search for the longest unplaced chain
     *@param  startAtom                                       A start atom from
     *      which the chain search starts
     *@return                                                 An AtomContainer
     *      holding the longest unplaced chain.
     *@exception  org.openscience.cdk.exception.CDKException  Description of the
     *      Exception
     */
    public IAtomContainer getLongestUnplacedChain(IMolecule molecule, IAtom startAtom) throws org.openscience.cdk.exception.CDKException
    {
        logger.debug("Start of getLongestUnplacedChain.");
        //ConnectivityChecker cc = new ConnectivityChecker();
        int longest = 0;
        int longestPathLength = 0;
        int maxDegreeSum = 0;
        int degreeSum = 0;
        IAtomContainer[] pathes = new IAtomContainer[molecule.getAtomCount()];
        for (int f = 0; f < molecule.getAtomCount(); f++)
        {
            molecule.getAtom(f).setFlag(CDKConstants.VISITED, false);
            pathes[f] = molecule.getBuilder().newAtomContainer();
            pathes[f].addAtom(startAtom);

        }
        Vector startSphere = new Vector();
        startSphere.addElement(startAtom);
        breadthFirstSearch(molecule, startSphere, pathes);
        for (int f = 0; f < molecule.getAtomCount(); f++)
        {
            if (pathes[f].getAtomCount() >= longestPathLength)
            {
            	degreeSum = getDegreeSum(pathes[f], molecule);
            	
            	if (degreeSum > maxDegreeSum) 
            	{
            		maxDegreeSum = degreeSum;
            		longest = f;
            		longestPathLength = pathes[f].getAtomCount();
            	}
            }
        }
        logger.debug("End of getLongestUnplacedChain.");
        return pathes[longest];
    }


    /**
     *  Performs a breadthFirstSearch in an AtomContainer starting with a
     *  particular sphere, which usually consists of one start atom, and searches
     *  for the longest aliphatic chain which is yet unplaced. If the search
     *  encounters an unplaced ring atom, it is also appended to the chain so that
     *  this last bond of the chain can also be laid out. This gives us the
     *  orientation for the attachment of the ring system.
     *
     *@param  ac                                              The AtomContainer to
     *      be searched
     *@param  sphere                                          A sphere of atoms to
     *      start the search with
     *@param  pathes                                          A vector of N pathes
     *      (N = no of heavy atoms).
     *@exception  org.openscience.cdk.exception.CDKException  Description of the
     *      Exception
     */
    public  void breadthFirstSearch(IAtomContainer ac, Vector sphere, IAtomContainer[] pathes) throws org.openscience.cdk.exception.CDKException
    {
        IAtom atom = null;
        IAtom nextAtom = null;
        int atomNr;
        int nextAtomNr;
        //IAtomContainer path = null;
        Vector newSphere = new Vector();
        logger.debug("Start of breadthFirstSearch");

        for (int f = 0; f < sphere.size(); f++)
        {
            atom = (IAtom) sphere.elementAt(f);
            if (!atom.getFlag(CDKConstants.ISINRING))
            {
                atomNr = ac.getAtomNumber(atom);
                logger.debug("BreadthFirstSearch around atom " + (atomNr + 1));

                java.util.List bonds = ac.getConnectedBondsList(atom);
                for (int g = 0; g < bonds.size(); g++)
                {
                    IBond curBond = (IBond)bonds.get(g);
                    nextAtom = curBond.getConnectedAtom(atom);
                    if (!nextAtom.getFlag(CDKConstants.VISITED) &&
                            !nextAtom.getFlag(CDKConstants.ISPLACED))
                    {
                        nextAtomNr = ac.getAtomNumber(nextAtom);
                        logger.debug("BreadthFirstSearch is meeting new atom " + (nextAtomNr + 1));
                        pathes[nextAtomNr] = ac.getBuilder().newAtomContainer(pathes[atomNr]);
                        logger.debug("Making copy of path " + (atomNr + 1) + " to form new path " + (nextAtomNr + 1));
                        logger.debug("Old path " + (atomNr + 1) + " looks like: " + listNumbers(molecule, pathes[atomNr]));
                        logger.debug("Copied path " + (nextAtomNr + 1) + " looks like: " + listNumbers(molecule, pathes[nextAtomNr]));
                        pathes[nextAtomNr].addAtom(nextAtom);
                        logger.debug("Adding atom " + (nextAtomNr + 1) + " to path " + (nextAtomNr + 1));
                        pathes[nextAtomNr].addBond(curBond);
                        logger.debug("New path " + (nextAtomNr + 1) + " looks like: " + listNumbers(molecule, pathes[nextAtomNr]));
                        if (ac.getConnectedBondsCount(nextAtom) > 1)
                        {
                            newSphere.addElement(nextAtom);
                        }
                    }
                }
            }
        }
        if (newSphere.size() > 0)
        {
            for (int f = 0; f < newSphere.size(); f++)
            {
                ((IAtom) newSphere.elementAt(f)).setFlag(CDKConstants.VISITED, true);
            }
            breadthFirstSearch(ac, newSphere, pathes);
        }
        logger.debug("End of breadthFirstSearch");
    }


    /**
     *  Returns a string with the numbers of all placed atoms in an AtomContainer
     *
     *@param  ac  The AtomContainer for which the placed atoms are to be listed
     *@return     A string with the numbers of all placed atoms in an AtomContainer
     */
    public String listPlaced(IAtomContainer ac)
    {
        String s = "Placed: ";
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            if (ac.getAtom(f).getFlag(CDKConstants.ISPLACED))
            {
                s += (f + 1) + "+ ";
            } else
            {
                s += (f + 1) + "- ";
            }
        }
        return s;
    }


    /**
     *  Returns a string with the numbers of all atoms in an AtomContainer relative
     *  to a given molecule. I.e. the number the is listesd is the position of each
     *  atom in the molecule.
     *
     *@param  ac                                              The AtomContainer for
     *      which the placed atoms are to be listed
     *@param  mol                                             Description of the
     *      Parameter
     *@return                                                 A string with the
     *      numbers of all placed atoms in an AtomContainer
     *@exception  org.openscience.cdk.exception.CDKException  Description of the
     *      Exception
     */
    public  String listNumbers(IAtomContainer mol, IAtomContainer ac) throws org.openscience.cdk.exception.CDKException
    {
        String s = "Numbers: ";
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            s += (mol.getAtomNumber(ac.getAtom(f)) + 1) + " ";
        }
        return s;
    }


    /**
     *  Returns a string with the numbers of all atoms in a Vector relative to a
     *  given molecule. I.e. the number the is listesd is the position of each atom
     *  in the molecule.
     *
     *@param  ac                       The Vector for which the placed atoms are to
     *      be listed
     *@param  mol                      Description of the Parameter
     *@return                          A string with the numbers of all placed
     *      atoms in an AtomContainer
     *@exception  java.lang.Exception  Description of the Exception
     */
    public String listNumbers(IAtomContainer mol, Vector ac) throws java.lang.Exception
    {
        String s = "Numbers: ";
        for (int f = 0; f < ac.size(); f++)
        {
            s += (mol.getAtomNumber((IAtom) ac.elementAt(f)) + 1) + " ";
        }
        return s;
    }


    /**
     *  True is all the atoms in the given AtomContainer have been placed
     *
     *@param  ac  The AtomContainer to be searched
     *@return     True is all the atoms in the given AtomContainer have been placed
     */
    public  boolean allPlaced(IAtomContainer ac)
    {
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            if (!ac.getAtom(f).getFlag(CDKConstants.ISPLACED))
            {
                return false;
            }
        }
        return true;
    }


    /**
     *  Marks all the atoms in the given AtomContainer as not placed
     *
     *@param  ac  The AtomContainer whose atoms are to be marked
     */
    public  void markNotPlaced(IAtomContainer ac)
    {
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            ac.getAtom(f).setFlag(CDKConstants.ISPLACED, false);
        }

    }


    /**
     *  Marks all the atoms in the given AtomContainer as placed
     *
     *@param  ac  The AtomContainer whose atoms are to be marked
     */

    public  void markPlaced(IAtomContainer ac)
    {
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            ac.getAtom(f).setFlag(CDKConstants.ISPLACED, true);
        }
    }


    /**
     *  Get all the placed atoms in an AtomContainer
     *
     *@param  ac  The AtomContainer to be searched for placed atoms
     *@return     An AtomContainer containing all the placed atoms
     */
    public IAtomContainer getPlacedAtoms(IAtomContainer ac)
    {
        IAtomContainer ret = ac.getBuilder().newAtomContainer();
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            if (ac.getAtom(f).getFlag(CDKConstants.ISPLACED))
            {
                ret.addAtom(ac.getAtom(f));
            }
        }
        return ret;
    }


    /**
     *  Sums up the degrees of atoms in an atomcontainer
     *
     *@param  ac  The atomcontainer to be processed
     *@param  superAC  The superAtomContainer from which the former has been derived
     *
     *@return 
     */
    int getDegreeSum(IAtomContainer ac, IAtomContainer superAC)
    {
    	int degreeSum = 0;
        //String path = "DegreeSum for Path: ";
    	for (int f = 0; f < ac.getAtomCount(); f++)
        {
    		//path += ac.getAtom(f).getSymbol();
            degreeSum += superAC.getConnectedBondsCount(ac.getAtom(f));
        }
        //System.out.println(path + ": " + degreeSum);
        return degreeSum;
    }


    
    /**
     *  Calculates weights for unplaced atoms.
     *
     *@param  ac  The atomcontainer for which weights are to be calculated
     */
    void calculateWeights(IAtomContainer ac)
    {
        int[] weights = getWeightNumbers(ac);
        for (int f = 0; f < ac.getAtomCount(); f++)
        {
            ac.getAtom(f).setProperty("Weight", new Integer(weights[f]));
        }
    }


    /**
     *  Makes an array containing morgan-number-like number for an atomContainer.
     *
     *@param  atomContainer  The atomContainer to analyse.
     *@return                The morgan numbers value.
     */
    int[] getWeightNumbers(IAtomContainer atomContainer)
    {
        int[] morganMatrix;
        int[] tempMorganMatrix;
        int N = atomContainer.getAtomCount();
        morganMatrix = new int[N];
        tempMorganMatrix = new int[N];
        java.util.List atoms = null;
        for (int f = 0; f < N; f++)
        {
            morganMatrix[f] = atomContainer.getConnectedBondsCount(f);
            tempMorganMatrix[f] = atomContainer.getConnectedBondsCount(f);
        }
        for (int e = 0; e < N; e++)
        {
            for (int f = 0; f < N; f++)
            {
                morganMatrix[f] = 0;
                atoms = atomContainer.getConnectedAtomsList(atomContainer.getAtom(f));
                for (int g = 0; g < atoms.size(); g++)
                {
                    IAtom atom = (IAtom)atoms.get(g);
                    if (!atom.getFlag(CDKConstants.ISPLACED))
                    {
                        morganMatrix[f] += tempMorganMatrix[atomContainer.getAtomNumber(atom)];
                    }
                }
            }
            System.arraycopy(morganMatrix, 0, tempMorganMatrix, 0, N);
        }
        return tempMorganMatrix;
    }

    public boolean shouldBeLinear(IAtom atom, IAtomContainer molecule)
    {
        int sum = 0;
        java.util.List bonds = molecule.getConnectedBondsList(atom);
        for (int g = 0; g < bonds.size(); g++)
        {
            IBond bond = (IBond)bonds.get(g);
            if (bond.getOrder() == IBond.Order.TRIPLE) sum += 10;
            else if (bond.getOrder() == IBond.Order.SINGLE) sum += 1;
//            else if (bond.getOrder() == IBond.Order.DOUBLE) sum += 5;
        }
        if (sum >= 10) return true;
        return false;
    }
}


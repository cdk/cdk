/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import com.google.common.collect.FluentIterable;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.BondTools;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.graph.matrix.ConnectionMatrix;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *  Methods for generating coordinates for atoms in various situations. They can
 *  be used for Automated Structure Diagram Generation or in the interactive
 *  buildup of molecules by the user.
 *
 *@author      steinbeck
 *@cdk.created 2003-08-29
 *@cdk.module  sdg
 * @cdk.githash
 */
public class AtomPlacer {

    private static final double ANGLE_120 = Math.toRadians(120);
    public final static boolean debug = true;
    public static final String PRIORITY = "Weight";
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(AtomPlacer.class);

    /**
     *  The molecule to be laid out. To be assigned from outside
     */
    IAtomContainer molecule = null;

    /**
     *  Constructor for the AtomPlacer object
     */
    public AtomPlacer() {}

    /**
     *  Return the molecule the AtomPlacer currently works with
     *
     *@return    the molecule the AtomPlacer currently works with
     */
    public IAtomContainer getMolecule() {
        return this.molecule;
    }

    /**
     *  Sets the molecule the AtomPlacer currently works with
     *
     *@param  molecule  the molecule the AtomPlacer currently works with
     */
    public void setMolecule(IAtomContainer molecule) {
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
            IAtomContainer unplacedNeighbours, double bondLength) {
        double occupiedAngle = 0;
        //double smallestDistance = Double.MAX_VALUE;
        //IAtom[] nearestAtoms = new IAtom[2];
        IAtom[] sortedAtoms = null;
        double startAngle = 0.0;
        double addAngle = 0.0;
        double radius = 0.0;
        double remainingAngle = 0.0;
        /*
         * calculate the direction away from the already placed partners of atom
         */
        //Point2d sharedAtomsCenter = sharedAtoms.get2DCenter();
        Vector2d sharedAtomsCenterVector = new Vector2d(sharedAtomsCenter);

        Vector2d newDirection = new Vector2d(atom.getPoint2d());
        Vector2d occupiedDirection = new Vector2d(sharedAtomsCenter);
        occupiedDirection.sub(newDirection);
        // if the placing on the centre atom we get NaNs just give a arbitary direciton the
        // rest works it's self out
        if (Math.abs(occupiedDirection.length()) < 0.001)
            occupiedDirection = new Vector2d(0, 1);
        logger.debug("distributePartners->occupiedDirection.lenght(): " + occupiedDirection.length());
        List<IAtom> atomsToDraw = new ArrayList<IAtom>();

        logger.debug("Number of shared atoms: ", placedNeighbours.getAtomCount());

        /*
         * IMPORTANT: This method is not supposed to handle the case of one or
         * no place neighbor. In the case of one placed neigbor, the chain
         * placement methods should be used.
         */
        if (placedNeighbours.getAtomCount() == 1) {
            logger.debug("Only one neighbour...");
            for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++) {
                atomsToDraw.add(unplacedNeighbours.getAtom(f));
            }

            addAngle = Math.PI * 2 / (unplacedNeighbours.getAtomCount() + placedNeighbours.getAtomCount());
            /*
             * IMPORTANT: At this point we need a calculation of the start
             * angle. Not done yet.
             */
            IAtom placedAtom = placedNeighbours.getAtom(0);
            //			double xDiff = atom.getX2d() - placedAtom.getX2d();
            //			double yDiff = atom.getY2d() - placedAtom.getY2d();
            double xDiff = placedAtom.getPoint2d().x - atom.getPoint2d().x;
            double yDiff = placedAtom.getPoint2d().y - atom.getPoint2d().y;

            logger.debug("distributePartners->xdiff: " + Math.toDegrees(xDiff));
            logger.debug("distributePartners->ydiff: " + Math.toDegrees(yDiff));
            startAngle = GeometryUtil.getAngle(xDiff, yDiff);
            //- (Math.PI / 2.0);
            logger.debug("distributePartners->angle: " + Math.toDegrees(startAngle));

            populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, bondLength);
            return;
        } else if (placedNeighbours.getAtomCount() == 0) {
            logger.debug("First atom...");
            for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++) {
                atomsToDraw.add(unplacedNeighbours.getAtom(f));
            }

            addAngle = Math.PI * 2.0 / unplacedNeighbours.getAtomCount();
            /*
             * IMPORTANT: At this point we need a calculation of the start
             * angle. Not done yet.
             */
            startAngle = 0.0;
            populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, bondLength);
            return;
        }

        if (doAngleSnap(atom, placedNeighbours)) {

            int numTerminal = 0;
            for (IAtom unplaced : unplacedNeighbours.atoms())
                if (molecule.getConnectedBondsCount(unplaced) == 1)
                    numTerminal++;

            if (numTerminal == unplacedNeighbours.getAtomCount()) {
                final Vector2d a = newVector(placedNeighbours.getAtom(0).getPoint2d(), atom.getPoint2d());
                final Vector2d b = newVector(placedNeighbours.getAtom(1).getPoint2d(), atom.getPoint2d());
                final double d1 = GeometryUtil.getAngle(a.x, a.y);
                final double d2 = GeometryUtil.getAngle(b.x, b.y);
                double sweep = a.angle(b);
                if (sweep < Math.PI) {
                    sweep = 2 * Math.PI - sweep;
                }
                startAngle = d2;
                if (d1 > d2 && d1 - d2 < Math.PI || d2 - d1 >= Math.PI) {
                    startAngle = d1;
                }
                sweep /= (1 + unplacedNeighbours.getAtomCount());
                populatePolygonCorners(FluentIterable.from(unplacedNeighbours.atoms()).toList(),
                                       atom.getPoint2d(), startAngle, sweep, bondLength);

                markPlaced(unplacedNeighbours);
                return;
            } else {
                atom.removeProperty(MacroCycleLayout.MACROCYCLE_ATOM_HINT);
            }
        }

        /*
         * if the least hindered side of the atom is clearly defined (bondLength
         * / 10 is an arbitrary value that seemed reasonable)
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
         * get the two sharedAtom partners with the smallest distance to the new
         * center
         */
        sortedAtoms = AtomContainerManipulator.getAtomArray(placedNeighbours);
        GeometryUtil.sortBy2DDistance(sortedAtoms, distanceMeasure);
        Vector2d closestPoint1 = new Vector2d(sortedAtoms[0].getPoint2d());
        Vector2d closestPoint2 = new Vector2d(sortedAtoms[1].getPoint2d());
        closestPoint1.sub(new Vector2d(atom.getPoint2d()));
        closestPoint2.sub(new Vector2d(atom.getPoint2d()));
        occupiedAngle = closestPoint1.angle(occupiedDirection);
        occupiedAngle += closestPoint2.angle(occupiedDirection);

        double angle1 = GeometryUtil.getAngle(sortedAtoms[0].getPoint2d().x - atom.getPoint2d().x,
                sortedAtoms[0].getPoint2d().y - atom.getPoint2d().y);
        double angle2 = GeometryUtil.getAngle(sortedAtoms[1].getPoint2d().x - atom.getPoint2d().x,
                sortedAtoms[1].getPoint2d().y - atom.getPoint2d().y);
        double angle3 = GeometryUtil.getAngle(distanceMeasure.x - atom.getPoint2d().x,
                distanceMeasure.y - atom.getPoint2d().y);
        if (debug) {
            try {
                logger.debug("distributePartners->sortedAtoms[0]: ", (molecule.indexOf(sortedAtoms[0]) + 1));
                logger.debug("distributePartners->sortedAtoms[1]: ", (molecule.indexOf(sortedAtoms[1]) + 1));
                logger.debug("distributePartners->angle1: ", Math.toDegrees(angle1));
                logger.debug("distributePartners->angle2: ", Math.toDegrees(angle2));
            } catch (Exception exc) {
                logger.debug(exc);
            }
        }
        IAtom startAtom = null;

        if (angle1 > angle3) {
            if (angle1 - angle3 < Math.PI) {
                startAtom = sortedAtoms[1];
            } else {
                // 12 o'clock is between the two vectors
                startAtom = sortedAtoms[0];
            }

        } else {
            if (angle3 - angle1 < Math.PI) {
                startAtom = sortedAtoms[0];
            } else {
                // 12 o'clock is between the two vectors
                startAtom = sortedAtoms[1];
            }
        }
        remainingAngle = (2 * Math.PI) - occupiedAngle;
        addAngle = remainingAngle / (unplacedNeighbours.getAtomCount() + 1);
        if (debug) {
            try {
                logger.debug("distributePartners->startAtom: " + (molecule.indexOf(startAtom) + 1));
                logger.debug("distributePartners->remainingAngle: " + Math.toDegrees(remainingAngle));
                logger.debug("distributePartners->addAngle: " + Math.toDegrees(addAngle));
                logger.debug("distributePartners-> partners.getAtomCount(): " + unplacedNeighbours.getAtomCount());
            } catch (Exception exc) {
                logger.debug(exc);
            }

        }
        for (int f = 0; f < unplacedNeighbours.getAtomCount(); f++) {
            atomsToDraw.add(unplacedNeighbours.getAtom(f));
        }
        radius = bondLength;
        startAngle = GeometryUtil.getAngle(startAtom.getPoint2d().x - atom.getPoint2d().x, startAtom.getPoint2d().y
                - atom.getPoint2d().y);
        logger.debug("Before check: distributePartners->startAngle: " + startAngle);
        //        if (startAngle < (Math.PI + 0.001) && startAngle > (Math.PI
        //            -0.001))
        //        {
        //            startAngle = Math.PI/placedNeighbours.getAtomCount();
        //        }
        logger.debug("After check: distributePartners->startAngle: " + startAngle);
        populatePolygonCorners(atomsToDraw, new Point2d(atom.getPoint2d()), startAngle, addAngle, radius);
    }

    private boolean doAngleSnap(IAtom atom, IAtomContainer placedNeighbours) {
        if (placedNeighbours.getAtomCount() != 2)
            return false;
        IBond b1 = molecule.getBond(atom, placedNeighbours.getAtom(0));
        if (!b1.isInRing())
            return false;
        IBond b2 = molecule.getBond(atom, placedNeighbours.getAtom(1));
        if (!b2.isInRing())
            return false;

        Point2d p1 = atom.getPoint2d();
        Point2d p2 = placedNeighbours.getAtom(0).getPoint2d();
        Point2d p3 = placedNeighbours.getAtom(1).getPoint2d();

        Vector2d v1 = newVector(p2, p1);
        Vector2d v2 = newVector(p3, p1);

        return Math.abs(v2.angle(v1) - ANGLE_120) < 0.01;
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
    public void placeLinearChain(IAtomContainer atomContainer, Vector2d initialBondVector, double bondLength) {
        IAtomContainer withh = atomContainer.getBuilder().newInstance(IAtomContainer.class, atomContainer);

        // BUGFIX - withh does not have cloned cloned atoms, so changes are
        // reflected in our atom container. If we're using implicit hydrogens
        // the correct counts need saving and restoring
        int[] numh = new int[atomContainer.getAtomCount()];
        for (int i = 0, n = atomContainer.getAtomCount(); i < n; i++) {
            Integer tmp = atomContainer.getAtom(i).getImplicitHydrogenCount();
            if (tmp == CDKConstants.UNSET)
                numh[i] = 0;
            else
                numh[i] = tmp;
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
        IBond prevBond = null, currBond = null;
        for (int f = 0; f < atomContainer.getAtomCount() - 1; f++) {
            atom = atomContainer.getAtom(f);
            nextAtom = atomContainer.getAtom(f + 1);
            currBond = atomContainer.getBond(atom, nextAtom);
            atomPoint = new Point2d(atom.getPoint2d());
            bondVector.normalize();
            bondVector.scale(bondLength);
            atomPoint.add(bondVector);
            nextAtom.setPoint2d(atomPoint);
            nextAtom.setFlag(CDKConstants.ISPLACED, true);
            boolean trans = false;

            if (prevBond != null &&
                isColinear(atom, molecule.getConnectedBondsList(atom))) {

                int atomicNumber = atom.getAtomicNumber();
                int charge = atom.getFormalCharge();

                // double length of the last bond to determing next placement
                Point2d p = new Point2d(prevBond.getOther(atom).getPoint2d());
                p.interpolate(atom.getPoint2d(), 2);
                nextAtom.setPoint2d(p);
            }

            if (GeometryUtil.has2DCoordinates(atomContainer)) {
                try {
                    if (f > 2
                            && BondTools.isValidDoubleBondConfiguration(withh,
                                    withh.getBond(withh.getAtom(f - 2), withh.getAtom(f - 1)))) {
                        trans = BondTools.isCisTrans(withh.getAtom(f - 3), withh.getAtom(f - 2), withh.getAtom(f - 1),
                                withh.getAtom(f - 0), withh);
                    }
                } catch (Exception ex) {
                    logger.debug("Excpetion in detecting E/Z. This could mean that cleanup does not respect E/Z");
                }
                bondVector = getNextBondVector(nextAtom, atom, GeometryUtil.get2DCenter(molecule), trans);
            } else {
                bondVector = getNextBondVector(nextAtom, atom, GeometryUtil.get2DCenter(molecule), true);
            }

            prevBond = currBond;
        }

        // BUGFIX part 2 - restore hydrogen counts
        for (int i = 0, n = atomContainer.getAtomCount(); i < n; i++) {
            atomContainer.getAtom(i).setImplicitHydrogenCount(numh[i]);
        }
    }

    private boolean isTerminalD4(IAtom atom) {
        List<IBond> bonds = molecule.getConnectedBondsList(atom);
        if (bonds.size() != 4)
            return false;
        int nonD1 = 0;
        for (IBond bond : bonds) {
            if (molecule.getConnectedBondsCount(bond.getOther(atom)) != 1) {
                if (++nonD1 > 1)
                    return false;
            }
        }
        return true;
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
    public Vector2d getNextBondVector(IAtom atom, IAtom previousAtom, Point2d distanceMeasure, boolean trans) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering AtomPlacer.getNextBondVector()");
            logger.debug("Arguments are atom: " + atom + ", previousAtom: " + previousAtom + ", distanceMeasure: "
                    + distanceMeasure);
        }

        final Point2d a = previousAtom.getPoint2d();
        final Point2d b = atom.getPoint2d();

        if (isColinear(atom, molecule.getConnectedBondsList(atom))) {
            return new Vector2d(b.x-a.x, b.y-a.y);
        }

        double angle = GeometryUtil.getAngle(previousAtom.getPoint2d().x - atom.getPoint2d().x,
                previousAtom.getPoint2d().y - atom.getPoint2d().y);
        double addAngle;

        if (isTerminalD4(atom))
            addAngle = Math.toRadians(45);
        else
            addAngle = Math.toRadians(120);

        if (!trans) addAngle = Math.toRadians(60);
        if (isColinear(atom, molecule.getConnectedBondsList(atom))) addAngle = Math.toRadians(180);

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
        if (distance2 > distance1) {
            logger.debug("Exiting AtomPlacer.getNextBondVector()");
            return vec2;
        }
        logger.debug("Exiting AtomPlacer.getNextBondVector()");
        return vec1;
    }

    /**
     * Populates the corners of a polygon with atoms. Used to place atoms in a
     * geometrically regular way around a ring center or another atom. If this is
     * used to place the bonding partner of an atom (and not to draw a ring) we
     * want to place the atoms such that those with highest "weight" are placed
     * furthermost away from the rest of the molecules. The "weight" mentioned here is
     * calculated by a modified morgan number algorithm.
     *
     * @param atoms     All the atoms to draw
     * @param thetaBeg  A start angle (in radians), giving the angle of the most clockwise
     *                  atom which has already been placed
     * @param thetaStep An angle (in radians) to be added for each atom from
     *                  atomsToDraw
     * @param center    The center of a ring, or an atom for which the
     *                  partners are to be placed
     * @param radius    The radius of the polygon to be populated: bond
     *                  length or ring radius
     */
    public void populatePolygonCorners(final List<IAtom> atoms,
                                       final Point2d center,
                                       final double thetaBeg,
                                       final double thetaStep,
                                       final double radius) {
        final int numAtoms = atoms.size();
        double theta = thetaBeg;

        logger.debug("populatePolygonCorners(numAtoms=", numAtoms, ", center=", center, ", thetaBeg=", Math.toDegrees(thetaBeg), ", r=", radius);

        for (IAtom atom : atoms) {
            theta += thetaStep;
            double x = Math.cos(theta) * radius;
            double y = Math.sin(theta) * radius;
            double newX = x + center.x;
            double newY = y + center.y;
            atom.setPoint2d(new Point2d(newX, newY));
            atom.setFlag(CDKConstants.ISPLACED, true);
            logger.debug("populatePolygonCorners - angle=", Math.toDegrees(theta), ", newX=", newX, ", newY=", newY);
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
    public void partitionPartners(IAtom atom, IAtomContainer unplacedPartners, IAtomContainer placedPartners) {
        List atoms = molecule.getConnectedAtomsList(atom);
        for (int i = 0; i < atoms.size(); i++) {
            IAtom curatom = (IAtom) atoms.get(i);
            if (curatom.getFlag(CDKConstants.ISPLACED)) {
                placedPartners.addAtom(curatom);
            } else {
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
    static public IAtomContainer getInitialLongestChain(IAtomContainer molecule) throws CDKException {
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
        for (int f = 0; f < apsp.length; f++) {
            atom = molecule.getAtom(f);
            if (molecule.getConnectedBondsCount(atom) == 1) {
                for (int g = 0; g < apsp.length; g++) {
                    if (apsp[f][g] > maxPathLength) {
                        maxPathLength = apsp[f][g];
                        bestStartAtom = f;
                        bestEndAtom = g;
                    }
                }
            }
        }
        logger.debug("Longest chaing in molecule is of length " + maxPathLength + " between atoms "
                + (bestStartAtom + 1) + " and " + (bestEndAtom + 1));

        startAtom = molecule.getAtom(bestStartAtom);
        //endAtom = molecule.getAtomAt(bestEndAtom);
        IAtomContainer path = molecule.getBuilder().newInstance(IAtomContainer.class);
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
    static public IAtomContainer getLongestUnplacedChain(IAtomContainer molecule, IAtom startAtom) throws CDKException {
        logger.debug("Start of getLongestUnplacedChain.");
        //ConnectivityChecker cc = new ConnectivityChecker();
        int longest = 0;
        int longestPathLength = 0;
        int maxDegreeSum = 0;
        int degreeSum = 0;
        IAtomContainer[] pathes = new IAtomContainer[molecule.getAtomCount()];
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            molecule.getAtom(f).setFlag(CDKConstants.VISITED, false);
            pathes[f] = molecule.getBuilder().newInstance(IAtomContainer.class);
            pathes[f].addAtom(startAtom);

        }
        List<IAtom> startSphere = new ArrayList<IAtom>();
        startSphere.add(startAtom);
        breadthFirstSearch(molecule, startSphere, pathes);
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            if (pathes[f].getAtomCount() >= longestPathLength) {
                degreeSum = getDegreeSum(pathes[f], molecule);

                if (degreeSum > maxDegreeSum) {
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
    static public void breadthFirstSearch(IAtomContainer ac, List<IAtom> sphere, IAtomContainer[] pathes)
            throws CDKException {
        IAtom atom = null;
        IAtom nextAtom = null;
        int atomNr;
        int nextAtomNr;
        //IAtomContainer path = null;
        List<IAtom> newSphere = new ArrayList<IAtom>();
        logger.debug("Start of breadthFirstSearch");

        for (int f = 0; f < sphere.size(); f++) {
            atom = sphere.get(f);
            if (!atom.getFlag(CDKConstants.ISINRING)) {
                atomNr = ac.indexOf(atom);
                logger.debug("BreadthFirstSearch around atom " + (atomNr + 1));

                List bonds = ac.getConnectedBondsList(atom);
                for (int g = 0; g < bonds.size(); g++) {
                    IBond curBond = (IBond) bonds.get(g);
                    nextAtom = curBond.getOther(atom);
                    if (!nextAtom.getFlag(CDKConstants.VISITED) && !nextAtom.getFlag(CDKConstants.ISPLACED)) {
                        nextAtomNr = ac.indexOf(nextAtom);
                        logger.debug("BreadthFirstSearch is meeting new atom " + (nextAtomNr + 1));
                        pathes[nextAtomNr] = ac.getBuilder().newInstance(IAtomContainer.class, pathes[atomNr]);
                        logger.debug("Making copy of path " + (atomNr + 1) + " to form new path " + (nextAtomNr + 1));
                        pathes[nextAtomNr].addAtom(nextAtom);
                        logger.debug("Adding atom " + (nextAtomNr + 1) + " to path " + (nextAtomNr + 1));
                        pathes[nextAtomNr].addBond(curBond);
                        if (ac.getConnectedBondsCount(nextAtom) > 1) {
                            newSphere.add(nextAtom);
                        }
                    }
                }
            }
        }
        if (newSphere.size() > 0) {
            for (int f = 0; f < newSphere.size(); f++) {
                newSphere.get(f).setFlag(CDKConstants.VISITED, true);
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
    public String listPlaced(IAtomContainer ac) {
        String s = "Placed: ";
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (ac.getAtom(f).getFlag(CDKConstants.ISPLACED)) {
                s += (f + 1) + "+ ";
            } else {
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
    static public String listNumbers(IAtomContainer mol, IAtomContainer ac) throws CDKException {
        String s = "Numbers: ";
        for (int f = 0; f < ac.getAtomCount(); f++) {
            s += (mol.indexOf(ac.getAtom(f)) + 1) + " ";
        }
        return s;
    }

    /**
     *  Returns a string with the numbers of all atoms in a Vector relative to a
     *  given molecule. I.e. the number the is listesd is the position of each atom
     *  in the molecule.
     *
     * @param  ac                       The Vector for which the placed atoms are to
     *      be listed
     * @param  mol                      Description of the Parameter
     * @return                          A string with the numbers of all placed
     *      atoms in an AtomContainer
     */
    static public String listNumbers(IAtomContainer mol, List<IAtom> ac) {
        String s = "Numbers: ";
        for (int f = 0; f < ac.size(); f++) {
            s += (mol.indexOf((IAtom) ac.get(f)) + 1) + " ";
        }
        return s;
    }

    /**
     *  True is all the atoms in the given AtomContainer have been placed
     *
     *@param  ac  The AtomContainer to be searched
     *@return     True is all the atoms in the given AtomContainer have been placed
     */
    static public boolean allPlaced(IAtomContainer ac) {
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (!ac.getAtom(f).getFlag(CDKConstants.ISPLACED)) {
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
    static public void markNotPlaced(IAtomContainer ac) {
        for (int f = 0; f < ac.getAtomCount(); f++) {
            ac.getAtom(f).setFlag(CDKConstants.ISPLACED, false);
        }

    }

    /**
     *  Marks all the atoms in the given AtomContainer as placed
     *
     *@param  ac  The AtomContainer whose atoms are to be marked
     */

    static public void markPlaced(IAtomContainer ac) {
        for (int f = 0; f < ac.getAtomCount(); f++) {
            ac.getAtom(f).setFlag(CDKConstants.ISPLACED, true);
        }
    }

    /**
     *  Get all the placed atoms in an AtomContainer
     *
     *@param  ac  The AtomContainer to be searched for placed atoms
     *@return     An AtomContainer containing all the placed atoms
     */
    static public IAtomContainer getPlacedAtoms(IAtomContainer ac) {
        IAtomContainer ret = ac.getBuilder().newInstance(IAtomContainer.class);
        for (int f = 0; f < ac.getAtomCount(); f++) {
            if (ac.getAtom(f).getFlag(CDKConstants.ISPLACED)) {
                ret.addAtom(ac.getAtom(f));
            }
        }
        return ret;
    }

    /**
     * Copy placed atoms/bonds from one container to another.
     *
     * @param dest destination container
     * @param src source container
     */
    static void copyPlaced(IRing dest, IAtomContainer src) {
        for (IBond bond : src.bonds()) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            if (beg.getFlag(CDKConstants.ISPLACED)) {
                dest.addAtom(beg);
                if (end.getFlag(CDKConstants.ISPLACED)) {
                    dest.addAtom(end);
                    dest.addBond(bond);
                }
            } else if (end.getFlag(CDKConstants.ISPLACED)) {
                dest.addAtom(end);
            }
        }
    }

    /**
     *  Sums up the degrees of atoms in an atomcontainer
     *
     *@param  ac  The atomcontainer to be processed
     *@param  superAC  The superAtomContainer from which the former has been derived
     *
     *@return sum of degrees
     */
    static int getDegreeSum(IAtomContainer ac, IAtomContainer superAC) {
        int degreeSum = 0;
        //String path = "DegreeSum for Path: ";
        for (int f = 0; f < ac.getAtomCount(); f++) {
            //path += ac.getAtom(f).getSymbol();
            degreeSum += superAC.getConnectedBondsCount(ac.getAtom(f));

            Integer implH = ac.getAtom(f).getImplicitHydrogenCount();
            if (implH != null) degreeSum += implH;
        }
        //System.out.println(path + ": " + degreeSum);
        return degreeSum;
    }

    /**
     * Calculates priority for atoms in a molecule.
     *
     * @param mol connected molecule
     * @see #PRIORITY
     */
    static void prioritise(IAtomContainer mol) {
        prioritise(mol, GraphUtil.toAdjList(mol));
    }

    /**
     * Calculates priority for atoms in a molecule.
     *
     * @param mol connected molecule
     * @param  adjList fast adjacency lookup
     * @see #PRIORITY
     */
    static void prioritise(IAtomContainer mol, int[][] adjList) {
        int[] weights = getPriority(mol, adjList);
        for (int i = 0; i < mol.getAtomCount(); i++) {
            mol.getAtom(i).setProperty(PRIORITY, weights[i]);
        }
    }

    /**
     * Prioritise atoms of a molecule base on how 'buried' they are. The priority
     * is cacheted with a morgan-like relaxation O(n^2 lg n). Priorities are assign
     * from 1..|V| (usually less than |V| due to symmetry) where the lowest numbers
     * have priority.
     *
     * @param  mol     molecule
     * @param  adjList fast adjacency lookup
     * @return the priority
     */
    static int[] getPriority(IAtomContainer mol, int[][] adjList) {

        final int n = mol.getAtomCount();
        final Integer[] order = new Integer[n];
        final int[] rank  = new int[n];
        final int[] prev  = new int[n];

        // init priorities, Helson 99 favours cyclic (init=2)
        for (int f = 0; f < n; f++) {
            rank[f]  = 1;
            prev[f]  = 1;
            order[f] = f;
        }

        final Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                // highest (most buried) first
                return Integer.compare(rank[a], rank[b]);
            }
        };

        int nDistinct = 1;

        for (int rep = 0; rep < n; rep++) {
            for (int i = 0; i < n; i++) {
                rank[i] = 3 * prev[i];
                for (int w : adjList[i])
                    rank[i] += prev[w];
            }

            // assign new ranks
            Arrays.sort(order, comparator);
            int clsNum = 1;
            prev[order[0]] = clsNum;
            for (int i = 1; i < n; i++) {
                if (rank[order[i]] != rank[order[i-1]])
                    clsNum++;
                prev[order[i]] = clsNum;
            }

            // no refinement over previous
            if (clsNum == nDistinct)
                break;
            nDistinct = clsNum;
        }

        // we want values 1 ≤ x < |V|
        for (int i = 0; i < n; i++)
            prev[i] = 1 + nDistinct - prev[i];

        return prev;
    }

    /**
     * -C#N
     * -[N+]#[C-]
     * -C=[N+]=N
     * -N=[N+]=N
     */
    static boolean isColinear(IAtom atom, Iterable<IBond> bonds) {

        if (Elements.isMetal(atom)) {
            return FluentIterable.from(bonds).size() == 2;
        }

        int numSgl = atom.getImplicitHydrogenCount() == null ? 0 : atom.getImplicitHydrogenCount();
        int numDbl = 0;
        int numTpl = 0;
        int count  = 0;

        for (IBond bond : bonds) {
            ++count;
            switch (bond.getOrder()) {
                case SINGLE:
                    numSgl++;
                    break;
                case DOUBLE:
                    numDbl++;
                    break;
                case TRIPLE:
                    numTpl++;
                    break;
                case QUADRUPLE:
                    return true;
                default:
                    return false;
            }
        }

        if (count != 2)
            return false;

        switch (atom.getAtomicNumber()) {
            case 6:
            case 7:
            case 14:
            case 32:
                if (numTpl == 1 && numSgl == 1)
                    return true;
                if (numDbl == 2 && numSgl == 0)
                    return true;
                break;
        }

        return false;
    }

    @Deprecated
    static public boolean shouldBeLinear(IAtom atom, IAtomContainer molecule) {
        int sum = 0;
        List bonds = molecule.getConnectedBondsList(atom);
        for (int g = 0; g < bonds.size(); g++) {
            IBond bond = (IBond) bonds.get(g);
            if (bond.getOrder() == IBond.Order.TRIPLE)
                sum += 10;
            else if (bond.getOrder() == IBond.Order.SINGLE) sum += 1;
            //            else if (bond.getOrder() == IBond.Order.DOUBLE) sum += 5;
        }
        if (sum >= 10) return true;
        return false;
    }

    static Vector2d newVector(Tuple2d to, Tuple2d from) {
        return new Vector2d(to.x-from.x, to.y-from.y);
    }

}

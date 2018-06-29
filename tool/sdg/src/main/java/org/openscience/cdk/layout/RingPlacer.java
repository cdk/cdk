/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *                    2008  Gilleain Torrance <gilleain@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class providing methods for generating coordinates for ring atoms.
 * Various situations are supported, like condensation, spiro-attachment, etc.
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of ringsystems by the user.
 *
 * @cdk.module sdg
 * @cdk.githash
 **/
public class RingPlacer {

    // indicate we want to snap to regular polygons for bridges, not generally applicable
    // but useful for macro cycles
    static final   String       SNAP_HINT = "sdg.snap.bridged";
    public static final double RAD_30 = Math.toRadians(-30);
    final static   boolean      debug     = false;
    private static ILoggingTool logger    = LoggingToolFactory.createLoggingTool(RingPlacer.class);

    private IAtomContainer molecule;

    private AtomPlacer atomPlacer = new AtomPlacer();

    static int FUSED   = 0;
    static int BRIDGED = 1;
    static int SPIRO   = 2;

    /**
     * Default ring start angles. Map contains pairs: ring size with start angle.
     */
    public static final Map<Integer, Double> defaultAngles = new HashMap<Integer, Double>();

    static {
        defaultAngles.put(3, Math.PI * (0.1666667));
        defaultAngles.put(4, Math.PI * (0.25));
        defaultAngles.put(5, Math.PI * (0.3));
        defaultAngles.put(7, Math.PI * (0.07));
        defaultAngles.put(8, Math.PI * (0.125));
    }

    /**
     * Suggested ring start angles for JChempaint, different due to Y inversion of canvas.
     */
    public static final Map<Integer, Double> jcpAngles = new HashMap<Integer, Double>();

    static {
        jcpAngles.put(3, Math.PI * (0.5));
        jcpAngles.put(4, Math.PI * (0.25));
        jcpAngles.put(5, Math.PI * (0.5));
        jcpAngles.put(7, Math.PI * (0.07));
        jcpAngles.put(8, Math.PI * (0.125));
    }

    /**
     * The empty constructor.
     */
    public RingPlacer() {
    }

    /**
     * Generated coordinates for a given ring. Multiplexes to special handlers
     * for the different possible situations (spiro-, fusion-, bridged attachement)
     *
     * @param   ring  The ring to be placed
     * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
     * @param   sharedAtomsCenter  The geometric center of these atoms
     * @param   ringCenterVector  A vector pointing the the center of the new ring
     * @param   bondLength  The standard bondlength
     */
    public void placeRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector,
                          double bondLength) {
        int sharedAtomCount = sharedAtoms.getAtomCount();
        logger.debug("placeRing -> sharedAtomCount: " + sharedAtomCount);
        if (sharedAtomCount > 2) {
            placeBridgedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
        } else if (sharedAtomCount == 2) {
            placeFusedRing(ring, sharedAtoms, ringCenterVector, bondLength);
        } else if (sharedAtomCount == 1) {
            placeSpiroRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
        }
    }

    /**
     * Place ring with default start angles, using {@link #defaultAngles}.
     * @param ring the ring to place.
     * @param ringCenter center coordinates of the ring.
     * @param bondLength given bond length.
     */
    public void placeRing(IRing ring, Point2d ringCenter, double bondLength) {
        placeRing(ring, ringCenter, bondLength, defaultAngles);
    }

    /**
     * Place ring with user provided angles.
     *
     * @param ring the ring to place.
     * @param ringCenter center coordinates of the ring.
     * @param bondLength given bond length.
     * @param startAngles a map with start angles when drawing the ring.
     */
    public void placeRing(IRing ring, Point2d ringCenter, double bondLength, Map<Integer, Double> startAngles) {
        double radius = this.getNativeRingRadius(ring, bondLength);
        double addAngle = 2 * Math.PI / ring.getRingSize();

        IAtom startAtom = ring.getAtom(0);
        Point2d p = new Point2d(ringCenter.x + radius, ringCenter.y);
        startAtom.setPoint2d(p);
        double startAngle = Math.PI * 0.5;

        /*
         * Different ring sizes get different start angles to have visually
         * correct placement
         */
        int ringSize = ring.getRingSize();
        if (startAngles.get(ringSize) != null) startAngle = startAngles.get(ringSize);

        List<IBond> bonds = ring.getConnectedBondsList(startAtom);
        /*
         * Store all atoms to draw in consecutive order relative to the chosen
         * bond.
         */
        Vector<IAtom> atomsToDraw = new Vector<IAtom>();
        IAtom currentAtom = startAtom;
        IBond currentBond = (IBond) bonds.get(0);
        for (int i = 0; i < ring.getBondCount(); i++) {
            currentBond = ring.getNextBond(currentBond, currentAtom);
            currentAtom = currentBond.getOther(currentAtom);
            atomsToDraw.addElement(currentAtom);
        }
        atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
    }

    /**
     * Positions the aliphatic substituents of a ring system
     *
     * @param   rs The RingSystem for which the substituents are to be laid out
     * @return  A list of atoms that where laid out
     */
    public IAtomContainer placeRingSubstituents(IRingSet rs, double bondLength) {
        logger.debug("RingPlacer.placeRingSubstituents() start");
        IRing ring = null;
        IAtom atom = null;
        IRingSet rings = null;
        IAtomContainer unplacedPartners = rs.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer sharedAtoms = rs.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer primaryAtoms = rs.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer treatedAtoms = rs.getBuilder().newInstance(IAtomContainer.class);
        Point2d centerOfRingGravity = null;
        for (int j = 0; j < rs.getAtomContainerCount(); j++) {
            ring = (IRing) rs.getAtomContainer(j); /*
                                                    * Get the j-th Ring in
                                                    * RingSet rs
                                                    */
            for (int k = 0; k < ring.getAtomCount(); k++) {
                unplacedPartners.removeAllElements();
                sharedAtoms.removeAllElements();
                primaryAtoms.removeAllElements();
                atom = ring.getAtom(k);
                rings = rs.getRings(atom);
                centerOfRingGravity = GeometryUtil.get2DCenter(rings);
                atomPlacer.partitionPartners(atom, unplacedPartners, sharedAtoms);
                atomPlacer.markNotPlaced(unplacedPartners);
                try {
                    for (int f = 0; f < unplacedPartners.getAtomCount(); f++) {
                        logger.debug("placeRingSubstituents->unplacedPartners: "
                                + (molecule.indexOf(unplacedPartners.getAtom(f)) + 1));
                    }
                } catch (Exception exc) {
                }

                treatedAtoms.add(unplacedPartners);
                if (unplacedPartners.getAtomCount() > 0) {
                    atomPlacer.distributePartners(atom, sharedAtoms, centerOfRingGravity, unplacedPartners, bondLength);
                }
            }
        }
        logger.debug("RingPlacer.placeRingSubstituents() end");
        return treatedAtoms;
    }

    private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
        return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
    }

    private static double det(Point2d a, Point2d b, Point2d c) {
        return det(a.x, a.y, b.x, b.y, c.x, c.y);
    }

    /**
     * Generated coordinates for a given ring, which is connected to another ring a bridged ring,
     * i.e. it shares more than two atoms with another ring.
     *
     * @param   ring  The ring to be placed
     * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
     * @param   sharedAtomsCenter  The geometric center of these atoms
     * @param   ringCenterVector  A vector pointing the the center of the new ring
     * @param   bondLength  The standard bondlength
     */
    private void placeBridgedRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength) {

        IAtom[] bridgeAtoms = getBridgeAtoms(sharedAtoms);
        IAtom bondAtom1 = bridgeAtoms[0];
        IAtom bondAtom2 = bridgeAtoms[1];
        List<IAtom> otherAtoms = new ArrayList<>();
        for (IAtom atom : sharedAtoms.atoms())
            if (!atom.equals(bondAtom1) && !atom.equals(bondAtom2))
                otherAtoms.add(atom);

        final boolean snap = ring.getProperty(SNAP_HINT) != null && ring.getProperty(SNAP_HINT, Boolean.class);

        boolean swap = snap ? det(bondAtom1.getPoint2d(), GeometryUtil.get2DCenter(otherAtoms), bondAtom2.getPoint2d()) < 0
                            : det(bondAtom1.getPoint2d(), GeometryUtil.get2DCenter(otherAtoms), bondAtom2.getPoint2d()) > 0;

        if (swap) {
            IAtom tmp = bondAtom1;
            bondAtom1 = bondAtom2;
            bondAtom2 = tmp;
        }

        Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2d());
        Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2d());

        Point2d midPoint   = getMidPoint(bondAtom1Vector, bondAtom2Vector);
        Point2d ringCenter = null;
        double  radius     = getNativeRingRadius(ring, bondLength);
        double  offset     = 0;

        if (snap) {
            ringCenter = new Point2d(midPoint);
            ringCenterVector = getPerpendicular(bondAtom1Vector, bondAtom2Vector,
                                                new Vector2d(midPoint.x - sharedAtomsCenter.x, midPoint.y - sharedAtomsCenter.y));

            offset = 0;
            for (IAtom atom : otherAtoms) {
                double dist = atom.getPoint2d().distance(midPoint);
                if (dist > offset)
                    offset = dist;
            }
        } else {
            ringCenter = new Point2d(sharedAtomsCenter);
        }

        ringCenterVector.normalize();
        ringCenterVector.scale(radius-offset);
        ringCenter.add(ringCenterVector);

        bondAtom1Vector.sub(ringCenter);
        bondAtom2Vector.sub(ringCenter);

        final int numUnplaced = ring.getRingSize() - sharedAtoms.getAtomCount();

        double dot = bondAtom2Vector.x * bondAtom1Vector.x + bondAtom2Vector.y * bondAtom1Vector.y;
        double det = bondAtom2Vector.x * bondAtom1Vector.y - bondAtom2Vector.y * bondAtom1Vector.x;

        // theta remain/step
        double tRemain = Math.atan2(det, dot);
        if (tRemain < 0) tRemain = Math.PI + (Math.PI + tRemain);
        double tStep   = tRemain / (numUnplaced + 1);

        logger.debug("placeBridgedRing->tRemain: " + Math.toDegrees(tRemain));
        logger.debug("placeBridgedRing->tStep: " + Math.toDegrees(tStep));

        double startAngle;
        int direction = -1;

        startAngle = GeometryUtil.getAngle(bondAtom1.getPoint2d().x - ringCenter.x, bondAtom1.getPoint2d().y - ringCenter.y);

        IAtom currentAtom = bondAtom1;
        IBond currentBond = sharedAtoms.getConnectedBondsList(currentAtom).get(0);

        List<IAtom> atoms = new ArrayList<>();
        for (int i = 0; i < ring.getBondCount(); i++) {
            currentBond = ring.getNextBond(currentBond, currentAtom);
            currentAtom = currentBond.getOther(currentAtom);
            if (!sharedAtoms.contains(currentAtom)) {
                atoms.add(currentAtom);
            }
        }

        logger.debug("placeBridgedRing->atomsToPlace: " + AtomPlacer.listNumbers(molecule, atoms));
        logger.debug("placeBridgedRing->startAngle: " + Math.toDegrees(startAngle));
        logger.debug("placeBridgedRing->tStep: " + Math.toDegrees(tStep));

        atomPlacer.populatePolygonCorners(atoms, ringCenter, startAngle, -tStep, radius);
    }

    /**
     * Generated coordinates for a given ring, which is connected to a spiro ring.
     * The rings share exactly one atom.
     *
     * @param   ring  The ring to be placed
     * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
     * @param   sharedAtomsCenter  The geometric center of these atoms
     * @param   ringCenterVector  A vector pointing the the center of the new ring
     * @param   bondLength  The standard bondlength
     */
    public void placeSpiroRing(IRing ring, IAtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength) {

        IAtom startAtom = sharedAtoms.getAtom(0);
        List<IBond> mBonds = molecule.getConnectedBondsList(sharedAtoms.getAtom(0));
        final int degree = mBonds.size();
        logger.debug("placeSpiroRing: D=", degree);

        // recalculate the ringCentreVector
        if (degree != 4) {

            int numPlaced = 0;
            for (IBond bond : mBonds) {
                IAtom nbr = bond.getOther(sharedAtoms.getAtom(0));
                if (!nbr.getFlag(CDKConstants.ISPLACED))
                    continue;
                numPlaced++;
            }

            if (numPlaced == 2) {
                // nudge the shared atom such that bond lengths will be
                // equal
                startAtom.getPoint2d().add(ringCenterVector);
                sharedAtomsCenter.add(ringCenterVector);
            }

            double theta = Math.PI-(2 * Math.PI / (degree / 2));
            rotate(ringCenterVector, theta);
        }

        double radius = getNativeRingRadius(ring, bondLength);
        Point2d ringCenter = new Point2d(sharedAtomsCenter);
        if (degree == 4) {
            ringCenterVector.normalize();
            ringCenterVector.scale(radius);
        } else {
            // spread things out a little for multiple spiro centres
            ringCenterVector.normalize();
            ringCenterVector.scale(2*radius);
        }
        ringCenter.add(ringCenterVector);
        double addAngle = 2 * Math.PI / ring.getRingSize();

        IAtom currentAtom = startAtom;
        double startAngle = GeometryUtil.getAngle(startAtom.getPoint2d().x - ringCenter.x,
                                                  startAtom.getPoint2d().y - ringCenter.y);

        /*
         * Get one bond connected to the spiro bridge atom. It doesn't matter in
         * which direction we draw.
         */
        List rBonds = ring.getConnectedBondsList(startAtom);

        IBond currentBond = (IBond) rBonds.get(0);

        Vector atomsToDraw = new Vector();
        /*
         * Store all atoms to draw in consequtive order relative to the chosen
         * bond.
         */
        for (int i = 0; i < ring.getBondCount(); i++) {
            currentBond = ring.getNextBond(currentBond, currentAtom);
            currentAtom = currentBond.getOther(currentAtom);
            if (!currentAtom.equals(startAtom))
                atomsToDraw.addElement(currentAtom);
        }
        logger.debug("currentAtom  " + currentAtom);
        logger.debug("startAtom  " + startAtom);

        atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);

    }

    /**
     * Generated coordinates for a given ring, which is fused to another ring.
     * The rings share exactly one bond.
     *
     * @param   ring  The ring to be placed
     * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
     * @param   ringCenterVector  A vector pointing the the center of the new ring
     * @param   bondLength  The standard bondlength
     */
    public void placeFusedRing(IRing ring,
                               IAtomContainer sharedAtoms,
                               Vector2d ringCenterVector,
                               double bondLength) {
        logger.debug("RingPlacer.placeFusedRing() start");

        final IAtom beg = sharedAtoms.getAtom(0);
        final IAtom end = sharedAtoms.getAtom(1);

        final Vector2d pBeg = new Vector2d(beg.getPoint2d());
        final Vector2d pEnd = new Vector2d(end.getPoint2d());

        // fuse the ring perpendicular to the bond, ring center is not
        // sub-optimal if non-regular/convex polygon (e.g. macro cycle)
        ringCenterVector = getPerpendicular(pBeg, pEnd, ringCenterVector);


        double radius = getNativeRingRadius(ring, bondLength);
        double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength / 2, 2));
        ringCenterVector.normalize();
        logger.debug("placeFusedRing->: ringCenterVector.length()" + ringCenterVector.length());
        ringCenterVector.scale(newRingPerpendicular);
        final Point2d ringCenter = getMidPoint(pBeg, pEnd);
        ringCenter.add(ringCenterVector);

        final Vector2d originRingCenterVector = new Vector2d(ringCenter);

        pBeg.sub(originRingCenterVector);
        pEnd.sub(originRingCenterVector);

        final double occupiedAngle = angle(pBeg, pEnd);

        final double remainingAngle = (2 * Math.PI) - occupiedAngle;
        double addAngle = remainingAngle / (ring.getRingSize() - 1);

        logger.debug("placeFusedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
        logger.debug("placeFusedRing->remainingAngle: " + Math.toDegrees(remainingAngle));
        logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));

        IAtom startAtom;

        final double centerX = ringCenter.x;
        final double centerY = ringCenter.y;

        final double xDiff = beg.getPoint2d().x - end.getPoint2d().x;
        final double yDiff = beg.getPoint2d().y - end.getPoint2d().y;

        double startAngle;;

        int direction = 1;
        // if bond is vertical
        if (xDiff == 0) {
            logger.debug("placeFusedRing->Bond is vertical");
            //starts with the lower Atom
            if (beg.getPoint2d().y > end.getPoint2d().y) {
                startAtom = beg;
            } else {
                startAtom = end;
            }

            //changes the drawing direction
            if (centerX < beg.getPoint2d().x) {
                direction = 1;
            } else {
                direction = -1;
            }
        }

        // if bond is not vertical
        else {
            //starts with the left Atom
            if (beg.getPoint2d().x > end.getPoint2d().x) {
                startAtom = beg;
            } else {
                startAtom = end;
            }

            //changes the drawing direction
            if (centerY - beg.getPoint2d().y > (centerX - beg.getPoint2d().x) * yDiff / xDiff) {
                direction = 1;
            } else {
                direction = -1;
            }
        }
        startAngle = GeometryUtil.getAngle(startAtom.getPoint2d().x - ringCenter.x, startAtom.getPoint2d().y
                - ringCenter.y);

        IAtom currentAtom = startAtom;
        // determine first bond in Ring
        //        int k = 0;
        //        for (k = 0; k < ring.getElectronContainerCount(); k++) {
        //            if (ring.getElectronContainer(k) instanceof IBond) break;
        //        }
        IBond currentBond = sharedAtoms.getBond(0);
        Vector atomsToDraw = new Vector();
        for (int i = 0; i < ring.getBondCount() - 2; i++) {
            currentBond = ring.getNextBond(currentBond, currentAtom);
            currentAtom = currentBond.getOther(currentAtom);
            atomsToDraw.addElement(currentAtom);
        }
        addAngle = addAngle * direction;

        logger.debug("placeFusedRing->startAngle: " + Math.toDegrees(startAngle));
        logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));
        logger.debug("placeFusedRing->startAtom is: " + (molecule.indexOf(startAtom) + 1));
        logger.debug("AtomsToDraw: " + AtomPlacer.listNumbers(molecule, atomsToDraw));

        atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius);
    }

    /**
     * Completes the layout of a partiallyed laid out ring.
     *
     * @param rset ring set
     * @param ring the ring to complete
     * @param bondLength the bond length
     */
    boolean completePartiallyPlacedRing(IRingSet rset, IRing ring, double bondLength) {
        if (ring.getFlag(CDKConstants.ISPLACED))
            return true;
        IRing partiallyPlacedRing = molecule.getBuilder().newInstance(IRing.class);
        for (IAtom atom : ring.atoms())
            if (atom.getPoint2d() != null)
                atom.setFlag(CDKConstants.ISPLACED, true);
        AtomPlacer.copyPlaced(partiallyPlacedRing, ring);

        if (partiallyPlacedRing.getAtomCount() > 1 &&
            partiallyPlacedRing.getAtomCount() < ring.getAtomCount()) {
            placeConnectedRings(rset, partiallyPlacedRing, RingPlacer.FUSED, bondLength);
            placeConnectedRings(rset, partiallyPlacedRing, RingPlacer.BRIDGED, bondLength);
            placeConnectedRings(rset, partiallyPlacedRing, RingPlacer.SPIRO, bondLength);
            ring.setFlag(CDKConstants.ISPLACED, true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the middle of two provide points.
     *
     * @param a first point
     * @param b second point
     * @return mid
     */
    private static Point2d getMidPoint(Tuple2d a, Tuple2d b) {
        return new Point2d((a.x + b.x) / 2, (a.y + b.y) / 2);
    }

    private static double angle(Vector2d pBeg, Vector2d pEnd) {
        // TODO inline to allow generic Tuple2ds
        return pBeg.angle(pEnd);
    }

    /**
     * Gat a vector perpendicular to the line, a-b, that is pointing
     * the same direction as 'ref'.
     *
     * @param a first coordinate
     * @param b second coordinate
     * @param ref reference vector
     * @return perpendicular vector
     */
    private static Vector2d getPerpendicular(Tuple2d a, Tuple2d b, Vector2d ref) {
        final Vector2d pVec = new Vector2d(-(a.y-b.y), a.x-b.x);
        if (pVec.dot(ref) < 0)
            pVec.negate();
        return pVec;
    }

    /**
     * True if coordinates have been assigned to all atoms in all rings.
     *
     * @param   rs  The ringset to be checked
     * @return  True if coordinates have been assigned to all atoms in all rings.
     */

    public boolean allPlaced(IRingSet rs) {
        for (int i = 0; i < rs.getAtomContainerCount(); i++) {
            if (!((IRing) rs.getAtomContainer(i)).getFlag(CDKConstants.ISPLACED)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Walks throught the atoms of each ring in a ring set and marks
     * a ring as PLACED if all of its atoms have been placed.
     *
     * @param   rs  The ringset to be checked
     */
    public void checkAndMarkPlaced(IRingSet rs) {
        IRing ring = null;
        boolean allPlaced = true;
        boolean ringsetPlaced = true;
        for (int i = 0; i < rs.getAtomContainerCount(); i++) {
            ring = (IRing) rs.getAtomContainer(i);
            allPlaced = true;
            for (int j = 0; j < ring.getAtomCount(); j++) {
                if (!((IAtom) ring.getAtom(j)).getFlag(CDKConstants.ISPLACED)) {
                    allPlaced = false;
                    ringsetPlaced = false;
                    break;
                }
            }
            ring.setFlag(CDKConstants.ISPLACED, allPlaced);
        }
        rs.setFlag(CDKConstants.ISPLACED, ringsetPlaced);
    }

    /**
     * Returns the bridge atoms, that is the outermost atoms in
     * the chain of more than two atoms which are shared by two rings
     *
     * @param   sharedAtoms  The atoms (n > 2) which are shared by two rings
     * @return  The bridge atoms, i.e. the outermost atoms in the chain of more than two atoms which are shared by two rings
     */
    private IAtom[] getBridgeAtoms(IAtomContainer sharedAtoms) {
        IAtom[] bridgeAtoms = new IAtom[2];
        IAtom atom;
        int counter = 0;
        for (int f = 0; f < sharedAtoms.getAtomCount(); f++) {
            atom = sharedAtoms.getAtom(f);
            if (sharedAtoms.getConnectedAtomsList(atom).size() == 1) {
                bridgeAtoms[counter] = atom;
                counter++;
            }
        }
        return bridgeAtoms;
    }

    /**
     * Partition the bonding partners of a given atom into ring atoms and non-ring atoms
     *
     * @param   atom  The atom whose bonding partners are to be partitioned
     * @param   ring  The ring against which the bonding partners are checked
     * @param   ringAtoms  An AtomContainer to store the ring bonding partners
     * @param   nonRingAtoms  An AtomContainer to store the non-ring bonding partners
     */
    public void partitionNonRingPartners(IAtom atom, IRing ring, IAtomContainer ringAtoms, IAtomContainer nonRingAtoms) {
        List atoms = molecule.getConnectedAtomsList(atom);
        for (int i = 0; i < atoms.size(); i++) {
            IAtom curAtom = (IAtom) atoms.get(i);
            if (!ring.contains(curAtom)) {
                nonRingAtoms.addAtom(curAtom);
            } else {
                ringAtoms.addAtom(curAtom);
            }
        }
    }

    /**
     * Returns the ring radius of a perfect polygons of size ring.getAtomCount()
     * The ring radius is the distance of each atom to the ringcenter.
     *
     * @param   ring  The ring for which the radius is to calculated
     * @param   bondLength  The bond length for each bond in the ring
     * @return  The radius of the ring.
     */
    public double getNativeRingRadius(IRing ring, double bondLength) {
        int size = ring.getAtomCount();
        double radius = bondLength / (2 * Math.sin((Math.PI) / size));
        return radius;
    }

    /**
     * Calculated the center for the first ring so that it can
     * layed out. Only then, all other rings can be assigned
     * coordinates relative to it.
     *
     * @param   ring  The ring for which the center is to be calculated
     * @return  A Vector2d pointing to the new ringcenter
     */
    Vector2d getRingCenterOfFirstRing(IRing ring, Vector2d bondVector, double bondLength) {
        int size = ring.getAtomCount();
        double radius = bondLength / (2 * Math.sin((Math.PI) / size));
        double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength / 2, 2));
        /* get the angle between the x axis and the bond vector */
        double rotangle = GeometryUtil.getAngle(bondVector.x, bondVector.y);
        /*
         * Add 90 Degrees to this angle, this is supposed to be the new
         * ringcenter vector
         */
        rotangle += Math.PI / 2;
        return new Vector2d(Math.cos(rotangle) * newRingPerpendicular, Math.sin(rotangle) * newRingPerpendicular);
    }

    private void rotate(Vector2d vec, double rad) {
        double rx = (vec.x * Math.cos(rad)) - (vec.y * Math.sin(rad));
        double ry = (vec.x * Math.sin(rad)) + (vec.y * Math.cos(rad));
        vec.x = rx;
        vec.y = ry;
    }

    /**
     * Layout all rings in the given RingSet that are connected to a given Ring
     *
     * @param   rs  The RingSet to be searched for rings connected to Ring
     * @param   ring  The Ring for which all connected rings in RingSet are to be layed out.
     */
    void placeConnectedRings(IRingSet rs, IRing ring, int handleType, double bondLength) {
        final IRingSet connectedRings = rs.getConnectedRings(ring);

        //		logger.debug(rs.reportRingList(molecule));
        for (IAtomContainer container : connectedRings.atomContainers()) {
            final IRing connectedRing = (IRing) container;
            if (!connectedRing.getFlag(CDKConstants.ISPLACED)) {
                //				logger.debug(ring.toString(molecule));
                //				logger.debug(connectedRing.toString(molecule));
                final IAtomContainer sharedAtoms = AtomContainerManipulator.getIntersection(ring, connectedRing);
                final int numSharedAtoms = sharedAtoms.getAtomCount();
                logger.debug("placeConnectedRings-> connectedRing: " + (ring.toString()));
                if ((numSharedAtoms == 2 && handleType == FUSED) ||
                    (numSharedAtoms == 1 && handleType == SPIRO) ||
                    (numSharedAtoms > 2  && handleType == BRIDGED)) {

                    final Point2d sharedAtomsCenter = GeometryUtil.get2DCenter(sharedAtoms);
                    final Point2d oldRingCenter = GeometryUtil.get2DCenter(ring);
                    final Vector2d tempVector = (new Vector2d(sharedAtomsCenter));
                    final Vector2d newRingCenterVector = new Vector2d(tempVector);
                    newRingCenterVector.sub(new Vector2d(oldRingCenter));

                    // zero (or v. small ring center)
                    if (Math.abs(newRingCenterVector.x) < 0.001 && Math.abs(newRingCenterVector.y) < 0.001) {

                        // first see if we can use terminal bonds
                        IAtomContainer terminalOnly = molecule.getBuilder().newInstance(IAtomContainer.class);

                        for (IAtom atom : ring.atoms()) {
                            if (ring.getConnectedBondsCount(atom) == 1)
                                terminalOnly.addAtom(atom);
                        }

                        if (terminalOnly.getAtomCount() == 2) {
                            newRingCenterVector.set(GeometryUtil.get2DCenter(terminalOnly));
                            newRingCenterVector.sub(oldRingCenter);
                            connectedRing.setProperty(RingPlacer.SNAP_HINT, true);
                        }
                        else {
                            // project coordinates on 12 axis (30 degree snaps) and choose one with most spread
                            Vector2d vec = new Vector2d(0, 1);
                            double   bestLen = -Double.MAX_VALUE;

                            for (int i = 0; i < 12; i++) {
                                Vector2d orth = new Vector2d(-vec.y, vec.x);
                                orth.normalize();
                                double min = Double.MAX_VALUE, max = -Double.MAX_VALUE;
                                for (IAtom atom : sharedAtoms.atoms()) {
                                    // s: scalar projection
                                    double s = orth.dot(new Vector2d(atom.getPoint2d()));
                                    if (s < min)
                                        min = s;
                                    if (s > max)
                                        max = s;
                                }
                                double len = max - min;
                                if (len > bestLen) {
                                    bestLen = len;
                                    newRingCenterVector.set(vec);
                                }
                                rotate(vec, RAD_30);
                            }
                        }

                    }

                    final Vector2d oldRingCenterVector = new Vector2d(newRingCenterVector);
                    logger.debug("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: "
                            + tempVector.length());
                    logger.debug("placeConnectedRing -> bondCenter: " + sharedAtomsCenter);
                    logger.debug("placeConnectedRing -> oldRingCenterVector.length(): " + oldRingCenterVector.length());
                    logger.debug("placeConnectedRing -> newRingCenterVector.length(): " + newRingCenterVector.length());
                    final Point2d tempPoint = new Point2d(sharedAtomsCenter);
                    tempPoint.add(newRingCenterVector);
                    placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);
                    connectedRing.setFlag(CDKConstants.ISPLACED, true);
                    placeConnectedRings(rs, connectedRing, handleType, bondLength);
                }
            }
        }
    }

    public IAtomContainer getMolecule() {
        return this.molecule;
    }

    public void setMolecule(IAtomContainer molecule) {
        this.molecule = molecule;
    }

    public AtomPlacer getAtomPlacer() {
        return this.atomPlacer;
    }

    public void setAtomPlacer(AtomPlacer atomPlacer) {
        this.atomPlacer = atomPlacer;
    }


    /**
     * Sorts ring systems prioritising the most complex. To sort correctly,
     * {@link #countHetero(List)} must first be invoked.
     */
    static final Comparator<IRingSet> RING_COMPARATOR = new Comparator<IRingSet>() {
        @Override
        public int compare(IRingSet a, IRingSet b) {

            // polycyclic better
            int cmp = Boolean.compare(a.getAtomContainerCount() == 1, b.getAtomContainerCount() == 1);
            if (cmp != 0) return cmp;

            // more hetero atoms better
            Integer numHeteroRingA = a.getProperty(NUM_HETERO_RINGS);
            Integer numHeteroRingB = b.getProperty(NUM_HETERO_RINGS);
            if (numHeteroRingA == null) numHeteroRingA = 0;
            if (numHeteroRingB == null) numHeteroRingB = 0;
            cmp = -Integer.compare(numHeteroRingA, numHeteroRingB);
            if (cmp != 0) return cmp;

            // more hetero rings better
            Integer numHeteroAtomA = a.getProperty(NUM_HETERO_ATOMS);
            Integer numHeteroAtomB = b.getProperty(NUM_HETERO_ATOMS);
            if (numHeteroAtomA == null) numHeteroAtomA = 0;
            if (numHeteroAtomB == null) numHeteroAtomB = 0;
            cmp = -Integer.compare(numHeteroAtomA, numHeteroAtomB);
            if (cmp != 0) return cmp;

            // more rings better
            return -Integer.compare(a.getAtomContainerCount(), b.getAtomContainerCount());
        }
    };

    private static final String NUM_HETERO_RINGS = "sdg:numHeteroRings";
    private static final String NUM_HETERO_ATOMS = "sdg:numHeteroAtoms";

    /**
     * Counds the number of hetero atoms and hetero rings in a ringset. The
     * properties {@code sdg:numHeteroRings} and {@code sdg:numHeteroAtoms}
     * are set.
     *
     * @param rsets ring systems
     */
    static void countHetero(List<IRingSet> rsets) {
        for (IRingSet rset : rsets) {
            int numHeteroAtoms = 0;
            int numHeteroRings = 0;
            for (IAtomContainer ring : rset.atomContainers()) {
                int prevNumHeteroAtoms = numHeteroAtoms;
                for (IAtom atom : ring.atoms()) {
                    Integer elem = atom.getAtomicNumber();
                    if (elem != null && elem != 6 && elem != 1)
                        numHeteroAtoms++;
                }
                if (numHeteroAtoms > prevNumHeteroAtoms)
                    numHeteroRings++;
            }
            rset.setProperty(NUM_HETERO_ATOMS, numHeteroAtoms);
            rset.setProperty(NUM_HETERO_RINGS, numHeteroRings);
        }
    }
}

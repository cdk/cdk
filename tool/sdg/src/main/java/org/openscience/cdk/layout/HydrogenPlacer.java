/* Copyright (C) 2003-2013 The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import javax.vecmath.Point2d;
import java.util.List;

/**
 * This is a wrapper class for some existing methods in AtomPlacer. It helps you
 * to layout 2D and 3D coordinates for hydrogen atoms added to a molecule which
 * already has coordinates for the rest of the atoms.
 *
 * <blockquote><pre>
 * IAtomContainer container      = ...;
 * HydrogenPlacer hydrogenPlacer = new HydrogenPlacer();
 * hydrogenPlacer.placeHydrogens2D(container, 1.5);
 * </pre></blockquote>
 *
 * @author Christoph Steinbeck
 * @cdk.created 2003-08-06
 * @cdk.module sdg
 * @cdk.githash
 * @see AtomPlacer
 */
public final class HydrogenPlacer {

    /** Class logger. */
    private static final ILoggingTool logger = LoggingToolFactory.createLoggingTool(HydrogenPlacer.class);

    /**
     * Place all hydrogens connected to atoms which have already been laid out.
     *
     * @param container  atom container
     * @param bondLength bond length to user
     */
    public void placeHydrogens2D(final IAtomContainer container, final double bondLength) {
        logger.debug("placing hydrogens on all atoms");
        for (IAtom atom : container.atoms()) {
            // only place hydrogens for atoms which have coordinates
            if (atom.getPoint2d() != null) {
                placeHydrogens2D(container, atom, bondLength);
            }
        }
        logger.debug("hydrogen placement complete");
    }

    /**
     * Place hydrogens connected to the given atom using the average bond length
     * in the container.
     *
     * @param container atom container of which <i>atom</i> is a member
     * @param atom      the atom of which to place connected hydrogens
     * @throws IllegalArgumentException if the <i>atom</i> does not have 2d
     *                                  coordinates
     * @see #placeHydrogens2D(org.openscience.cdk.interfaces.IAtomContainer,
     *      double)
     */
    public void placeHydrogens2D(IAtomContainer container, IAtom atom) {
        double bondLength = GeometryUtil.getBondLengthAverage(container);
        placeHydrogens2D(container, atom, bondLength);
    }

    /**
     * Place hydrogens connected to the provided atom <i>atom</i> using the
     * specified <i>bondLength</i>.
     *
     * @param container  atom container
     * @param bondLength bond length to user
     * @throws IllegalArgumentException thrown if the <i>atom</i> or
     *                                  <i>container</i> was null or the atom
     *                                  has connected atoms which have not been
     *                                  placed.
     */
    public void placeHydrogens2D(IAtomContainer container, IAtom atom, double bondLength) {

        if (container == null) throw new IllegalArgumentException("cannot place hydrogens, no container provided");
        if (atom.getPoint2d() == null)
            throw new IllegalArgumentException("cannot place hydrogens on atom without coordinates");

        logger.debug("placing hydrogens connected to atom ", atom.getSymbol(), ": ", atom.getPoint2d());
        logger.debug("bond length", bondLength);

        AtomPlacer atomPlacer = new AtomPlacer();
        atomPlacer.setMolecule(container);

        List<IAtom> connected = container.getConnectedAtomsList(atom);
        IAtomContainer placed = container.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer unplaced = container.getBuilder().newInstance(IAtomContainer.class);

        // divide connected atoms into those which are have and haven't been placed
        for (final IAtom conAtom : connected) {
            if (conAtom.getPoint2d() == null) {
                if (conAtom.getSymbol().equals("H")) {
                    unplaced.addAtom(conAtom);
                } else {
                    throw new IllegalArgumentException("cannot place hydrogens, atom has connected"
                            + " non-hydrogens without coordinates");
                }
            } else {
                placed.addAtom(conAtom);
            }
        }

        logger.debug("Atom placement before procedure:");
        logger.debug("Centre atom ", atom.getSymbol(), ": ", atom.getPoint2d());
        for (int i = 0; i < unplaced.getAtomCount(); i++) {
            logger.debug("H-" + i, ": ", unplaced.getAtom(i).getPoint2d());
        }

        Point2d centerPlacedAtoms = GeometryUtil.get2DCenter(placed);
        atomPlacer.distributePartners(atom, placed, centerPlacedAtoms, unplaced, bondLength);

        logger.debug("Atom placement after procedure:");
        logger.debug("Centre atom ", atom.getSymbol(), ": ", atom.getPoint2d());
        for (int i = 0; i < unplaced.getAtomCount(); i++) {
            logger.debug("H-" + i, ": ", unplaced.getAtom(i).getPoint2d());
        }
    }
}

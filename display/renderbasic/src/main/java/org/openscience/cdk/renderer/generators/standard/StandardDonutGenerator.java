/*
 * Copyright (C) 2019  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.DelocalisedDonutsBondDisplay;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.ForceDelocalisedBondDisplay;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import static org.openscience.cdk.interfaces.IBond.Order.UNSET;

final class StandardDonutGenerator {

    // bonds involved in donuts!
    private final Set<IBond> bonds = new HashSet<>();
    private final boolean    forceDelocalised;
    private final boolean    delocalisedDonuts;
    private final double     dbSpacing;
    private final Color      fgColor;
    private final IAtomContainer mol;

    public StandardDonutGenerator(IAtomContainer mol, RendererModel model) {
        this.forceDelocalised = model.get(ForceDelocalisedBondDisplay.class);
        this.delocalisedDonuts = model.get(DelocalisedDonutsBondDisplay.class);
        this.dbSpacing = model.get(StandardGenerator.BondSeparation.class);
        this.fgColor = model.get(StandardGenerator.AtomColor.class).getAtomColor(
                             mol.getBuilder().newInstance(IAtom.class, "C"));
        this.mol = mol;
    }

    private boolean canDelocalise(final IAtomContainer ring) {
        boolean okay = ring.getBondCount() <= 8;
        if (!okay)
            return false;
        for (IBond bond : ring.bonds()) {
            if (!bond.isAromatic())
                okay = false;
            if ((bond.getOrder() != null &&
                 bond.getOrder() != UNSET) &&
                !forceDelocalised)
                okay = false;
        }
        return okay;
    }

    IRenderingElement generate() {
        if (!delocalisedDonuts)
            return null;
        ElementGroup group = new ElementGroup();
        IRingSet     rset  = Cycles.edgeShort(mol).toRingSet();
        for (IAtomContainer ring : rset.atomContainers()) {
            if (!canDelocalise(ring))
                continue;
            for (IBond bond : ring.bonds()) {
                bonds.add(bond);
            }
            Point2d p2 = GeometryUtil.get2DCenter(ring);
            double  s  = GeometryUtil.getBondLengthMedian(ring);
            double  n  = ring.getBondCount();
            double  r  = s / (2 * Math.tan(Math.PI / n));
            group.add(new OvalElement(p2.x, p2.y, r - dbSpacing,
                                      false, fgColor));
        }
        return group;
    }

    boolean isDelocalised(IBond bond) {
        return bonds.contains(bond);
    }
}

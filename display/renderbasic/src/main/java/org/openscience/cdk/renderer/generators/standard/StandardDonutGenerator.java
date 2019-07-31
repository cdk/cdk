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
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.DelocalisedDonutsBondDisplay;
import org.openscience.cdk.renderer.generators.standard.StandardGenerator.ForceDelocalisedBondDisplay;

import javax.vecmath.Point2d;
import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import static org.openscience.cdk.interfaces.IBond.Order.UNSET;

/**
 * Generates aromatic donuts (or life buoys) as ovals in small (<8) aromatic
 * rings. If the ring is charged (and the charged is not shared with another
 * ring, e.g. rbonds > 2) it will be depicted in the middle of the ring.
 *
 * @see ForceDelocalisedBondDisplay
 * @see DelocalisedDonutsBondDisplay
 */
final class StandardDonutGenerator {

    // bonds involved in donuts!
    private final Set<IBond> bonds = new HashSet<>();
    // atoms with delocalised charge
    private final Set<IAtom> atoms = new HashSet<>();
    // smallest rings through each edge
    IRingSet smallest;

    private final boolean    forceDelocalised;
    private final boolean    delocalisedDonuts;
    private final double     dbSpacing;
    private final double     scale;
    private final double     stroke;
    private final Color      fgColor;
    private final Font       font;
    private final IAtomContainer mol;

    /**
     * Create a new generator for a molecule.
     * @param mol molecule
     * @param font the font
     * @param model the rendering parameters
     */
    StandardDonutGenerator(IAtomContainer mol, Font font, RendererModel model,
                           double stroke) {
        this.mol = mol;
        this.font = font;
        this.forceDelocalised = model.get(ForceDelocalisedBondDisplay.class);
        this.delocalisedDonuts = model.get(DelocalisedDonutsBondDisplay.class);
        this.dbSpacing = model.get(StandardGenerator.BondSeparation.class);
        this.scale = model.get(BasicSceneGenerator.Scale.class);
        this.stroke = stroke;
        this.fgColor = model.get(StandardGenerator.AtomColor.class).getAtomColor(
                             mol.getBuilder().newInstance(IAtom.class, "C"));
    }

    private boolean canDelocalise(final IAtomContainer ring) {
        boolean okay = ring.getBondCount() < 8;
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
        smallest = Cycles.edgeShort(mol).toRingSet();
        for (IAtomContainer ring : smallest.atomContainers()) {
            if (!canDelocalise(ring))
                continue;
            for (IBond bond : ring.bonds()) {
                bonds.add(bond);
            }
            int charge   = 0;
            int unpaired = 0;
            for (IAtom atom : ring.atoms()) {
                Integer q = atom.getFormalCharge();
                if (q == null || q == 0) {
                    continue;
                }
                int nCyclic = 0;
                for (IBond bond : mol.getConnectedBondsList(atom))
                    if (bond.isInRing())
                        nCyclic++;
                if (nCyclic > 2)
                    continue;
                atoms.add(atom);
                charge += atom.getFormalCharge();
            }
            Point2d p2 = GeometryUtil.get2DCenter(ring);

            if (charge != 0) {
                String qText = charge < 0 ? "–" : "+";
                if (charge < -1)
                    qText = Math.abs(charge) + qText;
                else if (charge > +1)
                    qText = Math.abs(charge) + qText;

                TextOutline qSym = new TextOutline(qText, font);
                qSym = qSym.resize(1 / scale, -1 / scale);
                qSym = qSym.translate(p2.x - qSym.getCenter().getX(),
                                      p2.y - qSym.getCenter().getY());
                group.add(GeneralPath.shapeOf(qSym.getOutline(), fgColor));
            }

            double  s  = GeometryUtil.getBondLengthMedian(ring);
            double  n  = ring.getBondCount();
            double  r  = s / (2 * Math.tan(Math.PI / n));
            group.add(new OvalElement(p2.x, p2.y, r - 1.5*dbSpacing,
                                      stroke, false, fgColor));
        }
        return group;
    }

    boolean isDelocalised(IBond bond) {
        return bonds.contains(bond);
    }

    boolean isChargeDelocalised(IAtom atom) {
        return atoms.contains(atom);
    }
}

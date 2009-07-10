/* Copyright (C) 2008-2009  Gilleain Torrance <gilleain@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.renderer;

import java.awt.geom.Rectangle2D;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;

/**
 * @cdk.module renderbasic
 */
public class BoundsCalculator {

	public static Rectangle2D calculateBounds(IChemModel chemModel) {
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        IReactionSet reactionSet = chemModel.getReactionSet();
        Rectangle2D totalBounds = null;
        if (moleculeSet != null) {
            totalBounds = calculateBounds(moleculeSet);
        }

        if (reactionSet != null) {
            if (totalBounds == null) {
                totalBounds = calculateBounds(reactionSet);
            } else {
                totalBounds = totalBounds.createUnion(
                        calculateBounds(reactionSet));
            }
        }
        return totalBounds;
    }

    public static Rectangle2D calculateBounds(IReactionSet reactionSet) {
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (IReaction reaction : reactionSet.reactions()) {
            Rectangle2D reactionBounds = calculateBounds(reaction);
            if (totalBounds.isEmpty()) {
                totalBounds = reactionBounds;
            } else {
                Rectangle2D.union(totalBounds, reactionBounds, totalBounds);
            }
        }
        return totalBounds;
    }

    public static Rectangle2D calculateBounds(IReaction reaction) {
        // get the participants in the reaction
        IMoleculeSet reactants = reaction.getReactants();
        IMoleculeSet products = reaction.getProducts();
        if (reactants == null || products == null) return null;

        // determine the bounds of everything in the reaction
        Rectangle2D reactantsBounds = calculateBounds(reactants);
        return reactantsBounds.createUnion(calculateBounds(products));
    }

    public static Rectangle2D calculateBounds(IMoleculeSet moleculeSet) {
        Rectangle2D totalBounds = new Rectangle2D.Double();
        for (int i = 0; i < moleculeSet.getAtomContainerCount(); i++) {
            IAtomContainer ac = moleculeSet.getAtomContainer(i);
            Rectangle2D acBounds = calculateBounds(ac);
            if (totalBounds.isEmpty()) {
                totalBounds = acBounds;
            } else {
                Rectangle2D.union(totalBounds, acBounds, totalBounds);
            }
        }
        return totalBounds;
    }

    public static Rectangle2D calculateBounds(IAtomContainer ac) {
        // this is essential, otherwise a rectangle
        // of (+INF, -INF, +INF, -INF) is returned!
        if (ac.getAtomCount() == 0) {
            return new Rectangle2D.Double();
        } else if (ac.getAtomCount() == 1) {
            Point2d p = ac.getAtom(0).getPoint2d();
            return new Rectangle2D.Double(p.x, p.y, 0, 0);
        }

    	double xmin = Double.POSITIVE_INFINITY;
    	double xmax = Double.NEGATIVE_INFINITY;
    	double ymin = Double.POSITIVE_INFINITY;
    	double ymax = Double.NEGATIVE_INFINITY;

    	for (IAtom atom : ac.atoms()) {
    		Point2d p = atom.getPoint2d();
    		xmin = Math.min(xmin, p.x);
    		xmax = Math.max(xmax, p.x);
    		ymin = Math.min(ymin, p.y);
    		ymax = Math.max(ymax, p.y);
    	}
    	double w = xmax - xmin;
    	double h = ymax - ymin;
    	return new Rectangle2D.Double(xmin, ymin, w, h);
    }

}

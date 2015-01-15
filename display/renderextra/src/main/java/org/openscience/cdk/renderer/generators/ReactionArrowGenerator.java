/*  Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.renderer.generators;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BondLength;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;

/**
 * Generate the arrow for a reaction.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class ReactionArrowGenerator implements IGenerator<IReaction> {

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IReaction reaction, RendererModel model) {
        Rectangle2D totalBoundsReactants = BoundsCalculator.calculateBounds(reaction.getReactants());
        Rectangle2D totalBoundsProducts = BoundsCalculator.calculateBounds(reaction.getProducts());

        if (totalBoundsReactants == null || totalBoundsProducts == null) return null;

        double separation = model.getParameter(BondLength.class).getValue()
                / model.getParameter(Scale.class).getValue();
        Color foregroundColor = model.getParameter(BasicSceneGenerator.ForegroundColor.class).getValue();
        return new ArrowElement(totalBoundsReactants.getMaxX() + separation, totalBoundsReactants.getCenterY(),
                totalBoundsProducts.getMinX() - separation, totalBoundsReactants.getCenterY(), 1 / model.getParameter(
                        Scale.class).getValue(), true, foregroundColor);
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[]{});
    }
}

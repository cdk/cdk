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

import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;

/**
 * Generate the arrow for a reaction.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class ReactionArrowGenerator implements IReactionGenerator {

	public IRenderingElement generate(IReaction reaction, RendererModel model) {
        Rectangle2D totalBoundsReactants = 
            Renderer.calculateBounds(reaction.getReactants());
        Rectangle2D totalBoundsProducts = 
            Renderer.calculateBounds(reaction.getProducts());
        
        if (totalBoundsReactants == null || totalBoundsProducts == null)
        	return null;
        
        double d = model.getBondLength() / model.getScale();
        Color foregroundColor = model.getRenderingParameter(
            BasicSceneGenerator.ForegroundColor.class).getValue();
        return new ArrowElement(
        	totalBoundsReactants.getMaxX() + d,
            totalBoundsReactants.getCenterY(), 
            totalBoundsProducts.getMinX() - d, 
            totalBoundsReactants.getCenterY(),
            1 / model.getScale(), true,
            foregroundColor
        );
	}
}

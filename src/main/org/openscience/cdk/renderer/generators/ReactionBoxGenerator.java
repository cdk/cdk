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
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.BondLength;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.ReactionSceneGenerator.ShowReactionBoxes;

/**
 * Generate the symbols for radicals.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class ReactionBoxGenerator implements IGenerator<IReaction> {

	public IRenderingElement generate(IReaction reaction, RendererModel model) {
		if (!model.getParameter(ShowReactionBoxes.class).getValue())
			return null;
		double d = model.getParameter(BondLength.class)
    		.getValue() / model.getParameter(Scale.class).getValue();
		Rectangle2D totalBounds = BoundsCalculator.calculateBounds(reaction);
        if (totalBounds == null) return null;
        
        ElementGroup diagram = new ElementGroup();
        Color foregroundColor = model.getParameter(
            BasicSceneGenerator.ForegroundColor.class).getValue();
        diagram.add(new RectangleElement(
        	totalBounds.getMinX()-d,
            totalBounds.getMinY()-d,
            totalBounds.getMaxX()+d,
            totalBounds.getMaxY()+d,
            foregroundColor
        ));
        if (reaction.getID() != null) {
        	diagram.add(new TextElement(
        		(totalBounds.getMinX()+totalBounds.getMaxX())/2, 
        		totalBounds.getMinY()-d, 
        		reaction.getID(), 
        		foregroundColor
        	));
        }
        return diagram;
	}

	public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
            new IGeneratorParameter<?>[] {
            }
        );
    }
}

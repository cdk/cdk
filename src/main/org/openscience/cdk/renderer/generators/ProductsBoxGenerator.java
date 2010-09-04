/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009  Stefan Kuhn
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

import org.openscience.cdk.interfaces.IAtomContainer;
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
public class ProductsBoxGenerator implements IGenerator<IReaction> {

	private static double DISTANCE;

	public IRenderingElement generate(IReaction reaction, RendererModel model) {
		if(!model.getParameter(ShowReactionBoxes.class).getValue())
			return null;
	    if (reaction.getProductCount() == 0) 
	    	return new ElementGroup();
		DISTANCE = model.getParameter(BondLength.class)
    		.getValue() / model.getParameter(Scale.class).getValue() / 2;
        Rectangle2D totalBounds = null;
        for (IAtomContainer molecule : reaction.getProducts().molecules()) {
            Rectangle2D bounds = BoundsCalculator.calculateBounds(molecule);
            if (totalBounds == null) {
                totalBounds = bounds;
            } else {
                totalBounds = totalBounds.createUnion(bounds);
            }
        }
        if (totalBounds == null) return null;
        
        ElementGroup diagram = new ElementGroup();
        Color foregroundColor = model.getParameter(
        	BasicSceneGenerator.ForegroundColor.class).getValue();
        diagram.add(new RectangleElement(
        	totalBounds.getMinX()-DISTANCE,
            totalBounds.getMinY()-DISTANCE,
            totalBounds.getMaxX()+DISTANCE,
            totalBounds.getMaxY()+DISTANCE,
            foregroundColor
        ));
        diagram.add(new TextElement(
        	(totalBounds.getMinX()+totalBounds.getMaxX())/2,
        	totalBounds.getMinY()-DISTANCE,
        	"Products",
        	foregroundColor
        ));
        return diagram;
	}

	public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
            new IGeneratorParameter<?>[] {
            }
        );
    }
}

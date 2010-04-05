/* Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
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
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;

/**
 * Generate the symbols for radicals.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class ReactantsBoxGenerator implements IReactionGenerator {

	public IRenderingElement generate(IReaction reaction, RendererModel model) {
		if (!model.getShowReactionBoxes())
			return null;
	    if (reaction.getReactantCount() == 0) 
	    	return new ElementGroup();
	    
		double d = model.getBondLength() / model.getScale()/2;
        Rectangle2D totalBounds = Renderer.calculateBounds(reaction.getReactants());
        
        ElementGroup diagram = new ElementGroup();
        double minX = totalBounds.getMinX();
        double minY = totalBounds.getMinY();
        double maxX = totalBounds.getMaxX();
        double maxY = totalBounds.getMaxY();
        Color foregroundColor = model.getRenderingParameter(
            BasicSceneGenerator.ForegroundColor.class).getValue();
        diagram.add(new RectangleElement(
            minX - d, minY - d, maxX + d, maxY + d, foregroundColor
        ));
        diagram.add(new TextElement(
            (minX+maxX)/2, minY-d, "Reactants", foregroundColor
        ));
        return diagram;
	}

}

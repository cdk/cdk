/* Copyright (C) 2009  Stefan Kuhn <shk3@users.sf.net>
 *               2009  Gilleain Torrance <gilleain@users.sf.net>
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

import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;

/**
 * Generate the arrow for a reaction.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class ReactionPlusGenerator implements IGenerator<IReaction> {

	public IRenderingElement generate(IReaction reaction, RendererModel model) {
		ElementGroup diagram = new ElementGroup();
		
		IMoleculeSet reactants = reaction.getReactants();
        Rectangle2D totalBoundsReactants = Renderer.calculateBounds(reactants);
        Rectangle2D bounds1 = 
            Renderer.calculateBounds(reactants.getAtomContainer(0));
        double axis = totalBoundsReactants.getCenterY();
        Color color = model.getRenderingParameter(
            BasicSceneGenerator.ForegroundColor.class).getValue();
        for (int i = 1; i < reaction.getReactantCount(); i++) {
        	Rectangle2D bounds2 = 
        	    Renderer.calculateBounds(reactants.getAtomContainer(i));
        	diagram.add(makePlus(bounds1, bounds2, axis, color));
        	bounds1 = bounds2;
        }
        
        IMoleculeSet products = reaction.getProducts();
        Rectangle2D totalBoundsProducts = Renderer.calculateBounds(products);
        axis = totalBoundsProducts.getCenterY();
        bounds1 = Renderer.calculateBounds(reactants.getAtomContainer(0));
        for (int i = 1; i < reaction.getProductCount(); i++) {
        	Rectangle2D bounds2 = 
        	    Renderer.calculateBounds(products.getAtomContainer(i));
        	
        	diagram.add(makePlus(bounds1, bounds2, axis, color));
        	bounds1 = bounds2;
        }
        return diagram;
	}
	
	public TextElement makePlus(
	        Rectangle2D a, Rectangle2D b, double axis, Color color) {
	    double x = (a.getCenterX() + b.getCenterX()) / 2;
	    return new TextElement(x, axis, "+", color);
	}

	public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
            new IGeneratorParameter<?>[] {
            }
        );
    }
}

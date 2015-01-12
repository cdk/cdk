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

import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.BoundsCalculator;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;

/**
 * Generate the arrow for a reaction.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class ReactionPlusGenerator implements IGenerator<IReaction> {

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IReaction reaction, RendererModel model) {
        ElementGroup diagram = new ElementGroup();

        Color color = model.getParameter(BasicSceneGenerator.ForegroundColor.class).getValue();
        IAtomContainerSet reactants = reaction.getReactants();

        // only draw + signs when there are more than one reactant
        if (reactants.getAtomContainerCount() > 1) {
            Rectangle2D totalBoundsReactants = BoundsCalculator.calculateBounds(reactants);
            Rectangle2D bounds1 = BoundsCalculator.calculateBounds(reactants.getAtomContainer(0));
            double axis = totalBoundsReactants.getCenterY();
            for (int i = 1; i < reaction.getReactantCount(); i++) {
                Rectangle2D bounds2 = BoundsCalculator.calculateBounds(reactants.getAtomContainer(i));
                diagram.add(makePlus(bounds1, bounds2, axis, color));
                bounds1 = bounds2;
            }
        }

        // only draw + signs when there are more than one products
        IAtomContainerSet products = reaction.getProducts();
        if (products.getAtomContainerCount() > 1) {
            Rectangle2D totalBoundsProducts = BoundsCalculator.calculateBounds(products);
            double axis = totalBoundsProducts.getCenterY();
            Rectangle2D bounds1 = BoundsCalculator.calculateBounds(reactants.getAtomContainer(0));
            for (int i = 1; i < reaction.getProductCount(); i++) {
                Rectangle2D bounds2 = BoundsCalculator.calculateBounds(products.getAtomContainer(i));

                diagram.add(makePlus(bounds1, bounds2, axis, color));
                bounds1 = bounds2;
            }
        }
        return diagram;
    }

    /** Place a '+' sign between two molecules. */
    private TextElement makePlus(Rectangle2D moleculeBox1, Rectangle2D moleculeBox2, double axis, Color color) {
        double arrowCenter = (moleculeBox1.getCenterX() + moleculeBox2.getCenterX()) / 2;
        return new TextElement(arrowCenter, axis, "+", color);
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[]{});
    }
}

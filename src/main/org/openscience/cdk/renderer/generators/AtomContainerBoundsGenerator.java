/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.RectangleElement;

/**
 * {@link IGenerator} that draws a rectangular around the {@link IAtomContainer}.
 *
 * @cdk.module renderextra
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.generators.AtomContainerBoundsGeneratorTest")
public class AtomContainerBoundsGenerator implements IGenerator<IAtomContainer> {

	/** {@inheritDoc}} */
	@Override
	@TestMethod("testEmptyContainer")
    public IRenderingElement generate( IAtomContainer container, RendererModel model) {
        double[] minMax = GeometryTools.getMinMax(container);
        return new RectangleElement(minMax[0], minMax[1], minMax[2], minMax[3],
                new Color(.7f, .7f, 1.0f));
        
    }

	/** {@inheritDoc}} */
	@Override
	@TestMethod("testGetParameters")
    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }

}

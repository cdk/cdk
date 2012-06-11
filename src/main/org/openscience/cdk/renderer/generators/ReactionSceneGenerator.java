/* Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *               2010  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * Generator for general reaction scene components.
 *
 * @cdk.module renderextra
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.generators.ReactionSceneGeneratorTest")
public class ReactionSceneGenerator implements IGenerator<IReaction> {

	/** Boolean that indicates if boxes are drawn around the reaction. */
    public static class ShowReactionBoxes extends
    AbstractGeneratorParameter<Boolean> {
    	/** {@inheritDoc}} */
    	public Boolean getDefault() {
    		return Boolean.TRUE;
    	}
    }
    private IGeneratorParameter<Boolean> showReactionBoxes =
    	new ShowReactionBoxes();

    /** Double which indicates how wide the arrow head is in screen pixels. */
    public static class ArrowHeadWidth extends
    AbstractGeneratorParameter<Double> {
    	/** {@inheritDoc}} */
    	public Double getDefault() {
    		return 10.0;
    	}
    }
    private IGeneratorParameter<Double> arrowHeadWidth =
    	new ArrowHeadWidth();

	/** {@inheritDoc}} */
	@Override
    @TestMethod("testEmptyReaction")
	public IRenderingElement generate(IReaction reaction, RendererModel model) {
		return new ElementGroup();
	}
	
	/** {@inheritDoc}} */
	@Override
	@TestMethod("testGetParameters")
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
            new IGeneratorParameter<?>[] {
            	showReactionBoxes,
            	arrowHeadWidth
            }
        );
    }
}

/*  Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * {@link IGenerator} for {@link IAtomContainer}s that will draw atom numbers
 * for the atoms.
 *
 * @author      maclean
 * @cdk.module  renderextra
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.generators.AtomNumberGeneratorTest")
public class AtomNumberGenerator implements IGenerator<IAtomContainer> {

    /** Color to draw the atom numbers with. */
    public static class AtomNumberTextColor extends AbstractGeneratorParameter<Color> {

        /** {@inheritDoc} */
        @Override
        public Color getDefault() {
            return Color.BLACK;
        }
    }

    private IGeneratorParameter<Color> textColor = new AtomNumberTextColor();

    /** Boolean parameter indicating if atom numbers should be drawn, allowing
     * this feature to be disabled temporarily. */
    public static class WillDrawAtomNumbers extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }

    private WillDrawAtomNumbers willDrawAtomNumbers = new WillDrawAtomNumbers();

    /** The color scheme by which to color the atom numbers, if
     * the {@link ColorByType} boolean is true. */
    public static class AtomColorer extends AbstractGeneratorParameter<IAtomColorer> {

        /** {@inheritDoc} */
        @Override
        public IAtomColorer getDefault() {
            return new CDK2DAtomColors();
        }
    }

    private IGeneratorParameter<IAtomColorer> atomColorer = new AtomColorer();

    /** Boolean to indicate of the {@link AtomColorer} scheme will be used. */
    public static class ColorByType extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }

    private IGeneratorParameter<Boolean> colorByType = new ColorByType();

    /**
     * Offset vector in screen space coordinates where the atom number label
     * will be placed.
     */
    public static class Offset extends AbstractGeneratorParameter<Vector2d> {

        /** {@inheritDoc} */
        @Override
        public Vector2d getDefault() {
            return new Vector2d();
        }
    }

    private Offset offset = new Offset();

    /** {@inheritDoc} */
    @Override
    @TestMethod("testEmptyContainer")
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {
        ElementGroup numbers = new ElementGroup();
        if (!model.getParameter(WillDrawAtomNumbers.class).getValue()) return numbers;

        Vector2d offset = new Vector2d(this.offset.getValue().x, -this.offset.getValue().y);
        offset.scale(1 / model.getParameter(Scale.class).getValue());

        int number = 1;
        for (IAtom atom : container.atoms()) {
            Point2d point = new Point2d(atom.getPoint2d());
            point.add(offset);
            numbers.add(new TextElement(point.x, point.y, String.valueOf(number), colorByType.getValue() ? atomColorer
                    .getValue().getAtomColor(atom) : textColor.getValue()));
            number++;
        }
        return numbers;
    }

    /** {@inheritDoc} */
    @Override
    @TestMethod("testGetParameters")
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[]{textColor, willDrawAtomNumbers, offset, atomColorer,
                colorByType});
    }

}

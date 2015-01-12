/* Copyright (C) 2009  Gilleain Torrance <gilleain.torrance@gmail.com>
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

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomRadius;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;

/**
 * Generate the symbols for lone pairs.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class LonePairGenerator implements IGenerator<IAtomContainer> {

    public LonePairGenerator() {}

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {
        ElementGroup group = new ElementGroup();

        // TODO : put into RendererModel
        final double SCREEN_RADIUS = 1.0;
        // separation between centers
        final double SCREEN_SEPARATION = 2.5;
        final Color RADICAL_COLOR = Color.BLACK;

        // XXX : is this the best option?
        final double ATOM_RADIUS = ((AtomRadius) model.getParameter(AtomRadius.class)).getValue();

        double scale = model.getParameter(Scale.class).getValue();
        double modelAtomRadius = ATOM_RADIUS / scale;
        double modelPointRadius = SCREEN_RADIUS / scale;
        double modelSeparation = SCREEN_SEPARATION / scale;
        for (ILonePair lonePair : container.lonePairs()) {
            IAtom atom = lonePair.getAtom();
            Point2d point = atom.getPoint2d();
            int align = GeometryUtil.getBestAlignmentForLabelXY(container, atom);
            double xRadius = point.x;
            double yRadius = point.y;
            double diffx = 0;
            double diffy = 0;
            if (align == 1) {
                xRadius += modelAtomRadius;
                diffy += modelSeparation;
            } else if (align == -1) {
                xRadius -= modelAtomRadius;
                diffy += modelSeparation;
            } else if (align == 2) {
                yRadius -= modelAtomRadius;
                diffx += modelSeparation;
            } else if (align == -2) {
                yRadius += modelAtomRadius;
                diffx += modelSeparation;
            }
            group.add(new OvalElement(xRadius + diffx, yRadius + diffy, modelPointRadius, true, RADICAL_COLOR));
            group.add(new OvalElement(xRadius - diffx, yRadius - diffy, modelPointRadius, true, RADICAL_COLOR));
        }
        return group;
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}

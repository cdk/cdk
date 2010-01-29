/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2009  Gilleain Torrance <gilleain.torrance@gmail.com>
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

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomRadius;

/**
 * Generate the symbols for lone pairs.
 * 
 * @author maclean
 * @cdk.module renderextra
 *
 */
public class LonePairGenerator implements IGenerator {
    
    public LonePairGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        ElementGroup group = new ElementGroup();
        
        // TODO : put into RendererModel
        final double SCREEN_RADIUS = 1.0;
        // separation between centers
        final double SCREEN_SEPARATION = 2.5;   
        final Color RADICAL_COLOR = Color.BLACK;

        // XXX : is this the best option?
        final double ATOM_RADIUS =
            ((AtomRadius)model.getRenderingParameter(AtomRadius.class)).
            getValue();
        
        double scale = model.getScale();
        double modelAtomRadius = ATOM_RADIUS / scale;
        double modelPointRadius = SCREEN_RADIUS / scale;
        double modelSeparation = SCREEN_SEPARATION / scale;
        for (ILonePair lp : ac.lonePairs()) {
            IAtom atom = lp.getAtom();
            Point2d p = atom.getPoint2d();
            int align = GeometryTools.getBestAlignmentForLabelXY(ac, atom);
            double rx = p.x;
            double ry = p.y;
            double dx = 0;
            double dy = 0;
            if (align == 1) {
                rx += modelAtomRadius;
                dy += modelSeparation;
            } else if (align == -1) {
                rx -= modelAtomRadius;
                dy += modelSeparation;
            } else if (align == 2) {
                ry -= modelAtomRadius;
                dx += modelSeparation;
            } else if (align == -2) {
                ry += modelAtomRadius;
                dx += modelSeparation;
            }
            group.add(
                    new OvalElement(rx + dx, ry + dy,
                            modelPointRadius, true, RADICAL_COLOR));
            group.add(
                    new OvalElement(rx - dx, ry - dy,
                            modelPointRadius, true, RADICAL_COLOR));
        }
        return group;
    }

    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}

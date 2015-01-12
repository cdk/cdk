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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomRadius;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;

/**
 * Generate the symbols for radicals.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class RadicalGenerator implements IGenerator<IAtomContainer> {

    public RadicalGenerator() {}

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {
        ElementGroup group = new ElementGroup();

        // TODO : put into RendererModel
        final double SCREEN_RADIUS = 2.0;
        final Color RADICAL_COLOR = Color.BLACK;

        // XXX : is this the best option?
        final double ATOM_RADIUS = ((AtomRadius) model.getParameter(AtomRadius.class)).getValue()
                / model.getParameter(Scale.class).getValue();

        double modelRadius = SCREEN_RADIUS / model.getParameter(Scale.class).getValue();
        double modelSpacing = modelRadius * 2.5;
        Map<IAtom, Integer> singleElectronsPerAtom = new HashMap<IAtom, Integer>();
        for (ISingleElectron electron : container.singleElectrons()) {
            IAtom atom = electron.getAtom();
            if (singleElectronsPerAtom.get(atom) == null) singleElectronsPerAtom.put(atom, 0);
            Point2d point = atom.getPoint2d();
            int align = GeometryUtil.getBestAlignmentForLabelXY(container, atom);
            double xRadius = point.x;
            double yRadius = point.y;
            if (align == 1) {
                xRadius += ATOM_RADIUS * 4 + singleElectronsPerAtom.get(atom) * modelSpacing;
            } else if (align == -1) {
                xRadius -= ATOM_RADIUS * 4 + singleElectronsPerAtom.get(atom) * modelSpacing;
            } else if (align == 2) {
                yRadius += ATOM_RADIUS * 4 + singleElectronsPerAtom.get(atom) * modelSpacing;
            } else if (align == -2) {
                yRadius -= ATOM_RADIUS * 4 + singleElectronsPerAtom.get(atom) * modelSpacing;
            }
            singleElectronsPerAtom.put(atom, singleElectronsPerAtom.get(atom) + 1);
            group.add(new OvalElement(xRadius, yRadius, modelRadius, true, RADICAL_COLOR));
        }
        return group;
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}

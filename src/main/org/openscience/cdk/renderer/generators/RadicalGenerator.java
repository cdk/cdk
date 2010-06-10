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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
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
 *
 */
public class RadicalGenerator implements IGenerator<IAtomContainer> {
    
    public RadicalGenerator() {}

    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        ElementGroup group = new ElementGroup();
        
        // TODO : put into RendererModel
        final double SCREEN_RADIUS = 2.0;
        final Color RADICAL_COLOR = Color.BLACK;
        
        // XXX : is this the best option?
        final double ATOM_RADIUS =
            ((AtomRadius)model.getParameter(AtomRadius.class)).
            getValue();
        
        double modelRadius = SCREEN_RADIUS /
            model.getParameter(Scale.class).getValue();
        Map<IAtom,Integer> singleElectronsPerAtom = new HashMap<IAtom, Integer>();
        for (ISingleElectron e : ac.singleElectrons()) {
            IAtom atom = e.getAtom();
            if(singleElectronsPerAtom.get(atom)==null)
                singleElectronsPerAtom.put(atom,0);
            Point2d p = atom.getPoint2d();
            int align = GeometryTools.getBestAlignmentForLabelXY(ac, atom);
            double rx = p.x;
            double ry = p.y;
            if (align == 1) {
                rx += ATOM_RADIUS+singleElectronsPerAtom.get(atom)*ATOM_RADIUS;
            } else if (align == -1) {
                rx -= ATOM_RADIUS+singleElectronsPerAtom.get(atom)*ATOM_RADIUS;
            } else if (align == 2) {
                ry += ATOM_RADIUS+singleElectronsPerAtom.get(atom)*ATOM_RADIUS;
            } else if (align == -2) {
                ry -= ATOM_RADIUS+singleElectronsPerAtom.get(atom)*ATOM_RADIUS;
            }
            singleElectronsPerAtom.put(atom, singleElectronsPerAtom.get(atom)+1);
            group.add(
                    new OvalElement(rx, ry, modelRadius, true, RADICAL_COLOR));
        }
        return group;
    }

    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}

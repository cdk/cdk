/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
 *               2009  Stefan Kuhn <sh3@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.generators;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * {@link IGenerator} that will show how atoms map between the reactant and
 * product side.
 *
 * @cdk.module renderextra
 * @cdk.githash
 */
public class MappingGenerator implements IGenerator<IReaction> {

    /**
     * The width on screen of an atom-atom mapping line.
     */
    public static class AtomAtomMappingLineColor extends AbstractGeneratorParameter<Color> {

        /** {@inheritDoc} */
        @Override
        public Color getDefault() {
            return Color.gray;
        }
    }

    private IGeneratorParameter<Color> atomAtomMappingLineColor = new AtomAtomMappingLineColor();

    /**
     * The width on screen of an atom-atom mapping line.
     */
    public static class MappingLineWidth extends AbstractGeneratorParameter<Double> {

        /** {@inheritDoc} */
        @Override
        public Double getDefault() {
            return 1.0;
        }
    }

    private IGeneratorParameter<Double> mappingLineWidth = new MappingLineWidth();

    /** Boolean by which atom-atom mapping depiction can be temporarily disabled. */
    public static class ShowAtomAtomMapping extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }

    private IGeneratorParameter<Boolean> showAtomAtomMapping = new ShowAtomAtomMapping();

    public MappingGenerator() {}

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IReaction reaction, RendererModel model) {
        if (!showAtomAtomMapping.getValue()) return null;
        ElementGroup elementGroup = new ElementGroup();
        Color mappingColor = atomAtomMappingLineColor.getValue();
        for (IMapping mapping : reaction.mappings()) {
            // XXX assume that there are only 2 endpoints!
            // XXX assume that the ChemObjects are actually IAtoms...
            IAtom endPointA = (IAtom) mapping.getChemObject(0);
            IAtom endPointB = (IAtom) mapping.getChemObject(1);
            Point2d pointA = endPointA.getPoint2d();
            Point2d pointB = endPointB.getPoint2d();
            elementGroup.add(new LineElement(pointA.x, pointA.y, pointB.x, pointB.y, getWidthForMappingLine(model),
                    mappingColor));
        }
        return elementGroup;
    }

    /**
     * Determine the width of an atom atom mapping, returning the width defined
     * in the model. Note that this will be scaled
     * to the space of the model.
     *
     * @param model the renderer model
     * @return a double in chem-model space
     */
    private double getWidthForMappingLine(RendererModel model) {
        double scale = model.getParameter(Scale.class).getValue();
        return mappingLineWidth.getValue() / scale;
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(new IGeneratorParameter<?>[]{showAtomAtomMapping, mappingLineWidth,
                atomAtomMappingLineColor});
    }
}

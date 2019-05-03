/* Copyright (C) 2009  Gilleain Torrance <gilleain@users.sf.net>
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2d;

import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.TextGroupElement;
import org.openscience.cdk.renderer.elements.TextGroupElement.Position;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator.WillDrawAtomNumbers;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * A generator for atoms with mass, charge, etc.
 *
 * @author maclean
 * @cdk.module renderextra
 * @cdk.githash
 */
public class ExtendedAtomGenerator extends BasicAtomGenerator {

    /** Boolean that indicates if implicit hydrogens should be depicted. */
    public static class ShowImplicitHydrogens extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }

    private IGeneratorParameter<Boolean> showImplicitHydrogens = new ShowImplicitHydrogens();

    /** Boolean that indicates if atom type names should be given instead
     * of element symbols. */
    public static class ShowAtomTypeNames extends AbstractGeneratorParameter<Boolean> {

        /** {@inheritDoc} */
        @Override
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }

    private ShowAtomTypeNames showAtomTypeNames = new ShowAtomTypeNames();

    /** {@inheritDoc} */
    @Override
    public IRenderingElement generate(IAtomContainer container, IAtom atom, RendererModel model) {
        boolean drawNumbers = false;
        if (model.hasParameter(WillDrawAtomNumbers.class)) {
            drawNumbers = model.getParameter(WillDrawAtomNumbers.class).getValue();
        }
        if (!hasCoordinates(atom) || invisibleHydrogen(atom, model)
                || (invisibleCarbon(atom, container, model) && !drawNumbers)) {
            return null;
        } else if (model.getParameter(CompactAtom.class).getValue()) {
            return this.generateCompactElement(atom, model);
        } else {
            String text;
            if (atom instanceof IPseudoAtom) {
                text = ((IPseudoAtom) atom).getLabel();
            } else if (invisibleCarbon(atom, container, model) && drawNumbers) {
                text = String.valueOf(container.indexOf(atom) + 1);
            } else {
                text = atom.getSymbol();
            }
            Point2d point = atom.getPoint2d();
            Color ccolor = getAtomColor(atom, model);
            TextGroupElement textGroup = new TextGroupElement(point.x, point.y, text, ccolor);
            decorate(textGroup, container, atom, model);
            return textGroup;
        }
    }

    private void decorate(TextGroupElement textGroup, IAtomContainer container, IAtom atom, RendererModel model) {
        Stack<Position> unused = getUnusedPositions(container, atom);

        if (model.hasParameter(WillDrawAtomNumbers.class)) {
            boolean drawNumbers = model.getParameter(WillDrawAtomNumbers.class).getValue();
            if (!invisibleCarbon(atom, container, model) && drawNumbers) {
                Position position = getNextPosition(unused);
                String number = String.valueOf(container.indexOf(atom) + 1);
                textGroup.addChild(number, position);
            }
        }

        if (showImplicitHydrogens.getValue()) {
            if (atom.getImplicitHydrogenCount() != null) {
                int hCount = atom.getImplicitHydrogenCount();
                if (hCount > 0) {
                    Position position = getNextPosition(unused);
                    if (hCount == 1) {
                        textGroup.addChild("H", position);
                    } else {
                        textGroup.addChild("H", String.valueOf(hCount), position);
                    }
                }
            }
        }

        Integer massNumber = atom.getMassNumber();
        if (massNumber != null) {
            try {
                IsotopeFactory factory = Isotopes.getInstance();
                int majorMass = factory.getMajorIsotope(atom.getSymbol()).getMassNumber();
                if (massNumber != majorMass) {
                    Position position = getNextPosition(unused);
                    textGroup.addChild(String.valueOf(massNumber), position);
                }
            } catch (IOException io) {

            }
        }
    }

    private Position getNextPosition(Stack<Position> unused) {
        if (unused.size() > 0) {
            return unused.pop();
        } else {
            return Position.N;
        }
    }

    private Stack<Position> getUnusedPositions(IAtomContainer container, IAtom atom) {
        Stack<Position> unused = new Stack<Position>();
        for (Position p : Position.values()) {
            unused.add(p);
        }

        for (IAtom connectedAtom : container.getConnectedAtomsList(atom)) {
            Position used = getPosition(atom, connectedAtom);
            if (unused.contains(used)) {
                unused.remove(used);
            }
        }
        return unused;
    }

    private Position getPosition(IAtom atom, IAtom connectedAtom) {
        Point2d pointA = atom.getPoint2d();
        Point2d pointB = connectedAtom.getPoint2d();
        double diffx = pointB.x - pointA.x;
        double diffy = pointB.y - pointA.y;

        final double DELTA = 0.2;

        if (diffx < -DELTA) { // generally west
            if (diffy < -DELTA) {
                return Position.NW;
            } else if (diffy > -DELTA && diffy < DELTA) {
                return Position.W;
            } else {
                return Position.SW;
            }
        } else if (diffx > -DELTA && diffx < DELTA) { //  north or south
            if (diffy < -DELTA) {
                return Position.N;
            } else if (diffy > -DELTA && diffy < DELTA) { // right on top of the atom!
                return Position.N; // XXX
            } else {
                return Position.S;
            }
        } else { // generally east
            if (diffy < -DELTA) {
                return Position.NE;
            } else if (diffy > -DELTA && diffy < DELTA) {
                return Position.E;
            } else {
                return Position.SE;
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<IGeneratorParameter<?>> getParameters() {
        List<IGeneratorParameter<?>> parameters = new ArrayList<IGeneratorParameter<?>>();
        parameters.add(showImplicitHydrogens);
        parameters.add(showAtomTypeNames);
        parameters.addAll(super.getParameters());
        return parameters;
    }
}

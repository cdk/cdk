/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
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
import java.util.Arrays;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;
import org.openscience.cdk.validate.ProblemMarker;

/**
 * Generates basic {@link IRenderingElement}s for atoms in an atom container.
 *  
 * @cdk.module renderbasic
 */
public class BasicAtomGenerator implements IGenerator<IAtomContainer> {

    public static class AtomColor extends
    AbstractGeneratorParameter<Color> {
        public Color getDefault() {
            return Color.BLACK;
        }
    }

    /** The default atom color */
    private IGeneratorParameter<Color> atomColor = new AtomColor();

    public static class AtomColorer extends
    AbstractGeneratorParameter<IAtomColorer> {
        public IAtomColorer getDefault() {
            return new CDK2DAtomColors();
        }
    }

    /** Converter between atoms and colors */
    private IGeneratorParameter<IAtomColorer> atomColorer = new AtomColorer();

    public static class ColorByType extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }

    /** If true, colors atoms by their type */
    private IGeneratorParameter<Boolean> colorByType = new ColorByType();

    public static class ShowExplicitHydrogens extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }

    /** If true, explicit hydrogens are displayed*/
    private IGeneratorParameter<Boolean> showExplicitHydrogens =
        new ShowExplicitHydrogens();

    public static class AtomRadius extends
    AbstractGeneratorParameter<Double> {
        public Double getDefault() {
            return 8.0;
        }
    }

    /** The atom radius on screen */
    private IGeneratorParameter<Double> atomRadius = new AtomRadius();

    public static class CompactAtom extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }

    /** If true, atoms are displayed as 'compact' symbols, not text */
    private IGeneratorParameter<Boolean> isCompact = new CompactAtom();

    public static class KekuleStructure extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }

    /**
     * Determines whether structures should be drawn as Kekule structures, thus
     * giving each carbon element explicitly, instead of not displaying the
     * element symbol. Example C-C-C instead of /\.
     */
    private IGeneratorParameter<Boolean> isKekule = new KekuleStructure();

    /**
     * When atoms are selected or in compact mode, they will
     * be covered by a shape determined by this enumeration
     */
    public enum Shape { OVAL, SQUARE };

    public static class CompactShape extends
    AbstractGeneratorParameter<Shape> {
        public Shape getDefault() {
            return Shape.SQUARE;
        }
    }

    /** The compact shape used to display atoms when isCompact is true */
    private IGeneratorParameter<Shape> compactShape = new CompactShape();

    public static class ShowEndCarbons extends
    AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    /**
     * Determines whether methyl carbons' symbols should be drawn explicit for
     * methyl carbons. Example C/\C instead of /\.
     */
    private IGeneratorParameter<Boolean> showEndCarbons = new ShowEndCarbons();

    /**
     * An empty constructor necessary for reflection. 
     */
    public BasicAtomGenerator() {}

    /** {@inheritDoc} */
    public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
        ElementGroup elementGroup = new ElementGroup();
        for (IAtom atom : ac.atoms()) {
            elementGroup.add(this.generate(ac, atom, model));
        }
        return elementGroup;
    }

    /**
     * Checks an atom to see if it has 2D coordinates.
     * 
     * @param atom the atom to check
     * @return true if the atom is not null, and it has non-null coordinates
     */
    public boolean hasCoordinates(IAtom atom) {
        return atom != null && atom.getPoint2d() != null;   
    }

    /**
     * Checks an atom to see if it is an 'invisible hydrogen' - that is, it
     * is a) an (explicit) hydrogen, and b) explicit hydrogens are set to off.
     * 
     * @param atom the atom to check
     * @param model the renderer model
     * @return true if this atom should not be shown
     */
    public boolean invisibleHydrogen(IAtom atom, RendererModel model) {
        return atom.getSymbol().equals("H") 
            && !showExplicitHydrogens.getValue();
    }

    /**
     * Checks an atom to see if it is an 'invisible carbon' - that is, it is:
     * a) a carbon atom and b) this carbon should not be shown.
     * 
     * @param atom the atom to check
     * @param atomContainer the atom container the atom is part of 
     * @param model the renderer model
     * @return true if this atom should not be shown
     */
    public boolean invisibleCarbon(
            IAtom atom, IAtomContainer atomContainer, RendererModel model) {
        return atom.getSymbol().equals("C") 
            && !showCarbon(atom, atomContainer, model);
    }

    /**
     * Checks an atom to see if it should be drawn. There are three reasons
     * not to draw an atom - a) no coordinates, b) an invisible hydrogen or
     * c) an invisible carbon.
     *
     * @param atom the atom to check
     * @param ac the atom container the atom is part of
     * @param model the renderer model
     * @return true if the atom should be drawn
     */
    public boolean canDraw(IAtom atom, IAtomContainer ac, RendererModel model) {
        // don't draw atoms without coordinates
        if (!hasCoordinates(atom)) {
            return false;
        }

        // don't draw invisible hydrogens
        if (invisibleHydrogen(atom, model)) {
            return false;
        }

        // don't draw invisible carbons 
        if (invisibleCarbon(atom, ac, model)) {
            return false;
        }

        return true;
    }

    /**
     * Generate the rendering element(s) for a particular atom.
     * 
     * @param atomContainer the atom container that the atom is from
     * @param atom the atom to generate the rendering element for
     * @param model the renderer model
     * @return a rendering element, or group of elements
     */
    public IRenderingElement generate(
            IAtomContainer atomContainer, IAtom atom, RendererModel model) {
        if (!canDraw(atom, atomContainer, model)) {
            return null;
        } else if (isCompact.getValue()) {
            return this.generateCompactElement(atom, model);
        } else {
            int alignment = 0;
            if (atom.getSymbol().equals("C")) {
                alignment = 
                  GeometryTools.getBestAlignmentForLabel(atomContainer, atom);
            } else {
                alignment = 
                  GeometryTools.getBestAlignmentForLabelXY(atomContainer, atom);
            }

            return generateElement(atom, alignment, model);
        }
    }

    /**
     * Generate a compact element for an atom, such as a circle or a square,
     * rather than text element.
     *  
     * @param atom the atom to generate the compact element for
     * @param model the renderer model
     * @return a compact rendering element
     */
    public IRenderingElement generateCompactElement(
            IAtom atom, RendererModel model) {
        Point2d p = atom.getPoint2d();
        double r = atomRadius.getValue() /
        model.getParameter(Scale.class).getValue();
        double d = 2 * r;
        if (compactShape.getValue() == Shape.SQUARE) {
            return new RectangleElement(
                    p.x - r, p.y - r, d, d, true, getAtomColor(atom));
        } else {
            return new OvalElement(p.x, p.y, r, true, getAtomColor(atom));
        }
    }

    /**
     * Generate an atom symbol element.
     * 
     * @param atom the atom to use
     * @param alignment the alignment of the atom's label
     * @param model the renderer model
     * @return an atom symbol element
     */
    public IRenderingElement generateElement(
            IAtom atom, int alignment, RendererModel model) {
        String text;
        if (atom instanceof IPseudoAtom) {
            text = ((IPseudoAtom) atom).getLabel();
        } else {
            text = atom.getSymbol();
        }
        return new AtomSymbolElement(
                atom.getPoint2d().x,
                atom.getPoint2d().y,
                text,
                atom.getFormalCharge(),
                atom.getImplicitHydrogenCount(),
                alignment, getAtomColor(atom));
    }

    /**
     * Checks a carbon atom to see if it should be shown.
     * 
     * @param carbonAtom the carbon atom to check
     * @param ac the atom container
     * @param model the renderer model
     * @return true if the carbon should be shown 
     */
    public boolean showCarbon(
            IAtom carbonAtom, IAtomContainer ac, RendererModel model) {

        if (isKekule.getValue())
            return true;

        if (carbonAtom.getFormalCharge() != 0)
            return true;

        if (ac.getConnectedBondsList(carbonAtom).size() < 1)
            return true;

        if (showEndCarbons.getValue()
                && ac.getConnectedBondsList(carbonAtom).size() == 1)
            return true;

        if (carbonAtom.getProperty(ProblemMarker.ERROR_MARKER) != null)
            return true;

        if (ac.getConnectedSingleElectronsCount(carbonAtom) > 0)
            return true;

        return false;
    }

    /**
     * Returns the drawing color of the given atom. An atom is colored as
     * highlighted if highlighted. The atom is color marked if in a
     * substructure. If not, the color from the CDK2DAtomColor is used (if
     * selected). Otherwise, the atom is colored black.
     */
    protected Color getAtomColor(IAtom atom) {
        Color atomColor = this.atomColor.getValue();
        if (colorByType.getValue()) {
            atomColor = this.atomColorer.getValue().getAtomColor(atom);
        }
        return atomColor;
    }

    /** {@inheritDoc} */
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
                new IGeneratorParameter<?>[] {
                        atomColor,
                        atomColorer,
                        atomRadius,
                        colorByType,
                        compactShape,
                        isCompact,
                        isKekule,
                        showEndCarbons,
                        showExplicitHydrogens
                }
        );
    }
}

/* Copyright (C) 2008-2009  Arvid Berg <goglepox@users.sf.net>
 *               2008-2009  Gilleain Torrance <gilleain@users.sf.net> 
 *                    2009  Mark Rijnbeek <markr@ebi.ac.uk>
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
import javax.vecmath.Vector2d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.RendererModel.ColorHash;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement.Direction;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerComparatorBy2DCenter;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Generator for elements from bonds. Only two-atom bonds are supported
 * by this generator.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.generators.BasicBondGeneratorTest")
public class BasicBondGenerator implements IGenerator<IAtomContainer> {

    // FIXME: bond width should be defined in world, not screen coordinates
    /**
     * The width on screen of a bond.
     */
    public static class BondWidth extends
    AbstractGeneratorParameter<Double> {
    	/** Returns the default value.
    	 * @return 1.0 */
        public Double getDefault() {
            return 1.0;
        }
    }
    private IGeneratorParameter<Double> bondWidth = new BondWidth();

    /**
     * The gap between double and triple bond lines on the screen.
     */
    public static class BondDistance extends
    AbstractGeneratorParameter<Double> {
    	/** Returns the default value.
    	 * @return 2.0. */
        public Double getDefault() {
            return 2.0;
        }
    }
    private IGeneratorParameter<Double> bondDistance = new BondDistance();

    /**
     * The color to draw bonds if not other color is given.
     */
    public static class DefaultBondColor extends
    AbstractGeneratorParameter<Color> {
    	/** Returns the default value.
    	 * @return Color.BLACK */
        public Color getDefault() {
            return Color.BLACK;
        }
    }
    private IGeneratorParameter<Color> defaultBondColor = new DefaultBondColor();

    /**
     * The width on screen of the fat end of a wedge bond.
     */
    public static class WedgeWidth extends AbstractGeneratorParameter<Double> {
    	/** Returns the default value.
    	 * @return 2.0 */
        public Double getDefault() {
            return 2.0;
        }
    }
    private IGeneratorParameter<Double> wedgeWidth = new WedgeWidth();

    /**
     * The proportion to move in towards the ring center.
     */
    public static class TowardsRingCenterProportion extends
    AbstractGeneratorParameter<Double> {
    	/** Returns the default value.
    	 * @return 0.15 */
        public Double getDefault() {
            return 0.15;
        }
    }
    private IGeneratorParameter<Double> ringCenterProportion =
        new TowardsRingCenterProportion();

    private ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(BasicBondGenerator.class);

    /**
     * Necessary for calculating inner-ring bond elements.
     */
    protected IRingSet ringSet;

    /**
     * A hack to allow the HighlightGenerator to override the standard colors.
     * Set it to non-null to have all bond-lines in this color.
     */
    private Color overrideColor = null;

    /**
     * A similar story to the override color
     */
    private double overrideBondWidth = -1;

    /**
     * The ideal ring size for the given center proportion.
     */
    private int IDEAL_RINGSIZE = 6;

    /**
     * The minimum ring size factor to ensure a minimum gap.
     */
    private double MIN_RINGSIZE_FACTOR = 2.5;

    /**
     * An empty constructor necessary for reflection.
     */
    public BasicBondGenerator() {}

    /**
     * Set the color to use for all bonds, overriding the standard bond colors.
     * 
     * @param color the override color
     */
    public void setOverrideColor(Color color) {
        this.overrideColor = color;
    }

    /**
     * Set the width to use for all bonds, overriding any standard bond widths.
     * 
     * @param bondWidth
     */
    public void setOverrideBondWidth(double bondWidth) {
        this.overrideBondWidth = bondWidth;
    }

    /**
     * Determine the ring set for this atom container. 
     * 
     * @param atomContainer the atom container to find rings in.
     * @return the rings of the molecule
     */
    protected IRingSet getRingSet(IAtomContainer atomContainer) {

        IRingSet ringSet = atomContainer.getBuilder().newInstance(IRingSet.class);
        try {
            IAtomContainerSet molecules =
                ConnectivityChecker.partitionIntoMolecules(atomContainer);
            for (IAtomContainer mol : molecules.atomContainers()) {
                ringSet.add(Cycles.sssr(mol).toRingSet());
            }

            return ringSet;
        } catch (Exception exception) {
            logger.warn("Could not partition molecule: "
                    + exception.getMessage());
            logger.debug(exception);
            return ringSet;
        }
    }

    /**
     * Determine the color of a bond, returning either the default color,
     * the override color or whatever is in the color hash for that bond.
     *
     * @param bond the bond we are generating an element for
     * @param model the rendering model
     * @return the color to paint the bond
     */
    public Color getColorForBond(IBond bond, RendererModel model) {
        if (this.overrideColor != null) {
            return overrideColor;
        }

        Color color = model.getParameter(ColorHash.class).getValue().get(bond);
        if (color == null) {
            return model.getParameter(DefaultBondColor.class).getValue();
        } else {
            return color;
        }
    }

    /**
     * Determine the width of a bond, returning either the width defined
     * in the model, or the override width. Note that this will be scaled
     * to the space of the model.
     *
     * @param bond the bond to determine the width for
     * @param model the renderer model
     * @return a double in chem-model space
     */
    public double getWidthForBond(IBond bond, RendererModel model) {
        double scale = model.getParameter(Scale.class).getValue();
        if (this.overrideBondWidth != -1) {
            return this.overrideBondWidth / scale;
        } else {
            return model.getParameter(BondWidth.class).getValue() / scale;
        }
    }

    /** {@inheritDoc} */
    public IRenderingElement generate(IAtomContainer container, RendererModel model) {
        ElementGroup group = new ElementGroup();
        this.ringSet = this.getRingSet(container);

        //Sort the ringSet consistently to ensure consistent rendering.
        //If this is omitted, the bonds may 'tremble'.
        ringSet.sortAtomContainers(new AtomContainerComparatorBy2DCenter());

        for (IBond bond : container.bonds()) {
            group.add(this.generate(bond, model));
        }
        return group;
    }

    /**
     * Generate rendering element(s) for the current bond, including ring
     * elements if this bond is part of a ring.
     *  
     * @param currentBond the bond to use when generating elements
     * @param model the renderer model
     * @return one or more rendering elements
     */
    public IRenderingElement generate(IBond currentBond, RendererModel model) {
        IRing ring = RingSetManipulator.getHeaviestRing(ringSet, currentBond);
        if (ring != null) {
            return generateRingElements(currentBond, ring, model);
        } else {
            return generateBond(currentBond, model);
        }
    }

    /**
     * Generate rendering elements for a bond, without ring elements but
     * considering the type of the bond (single, double, triple).
     * 
     * @param bond the bond to use when generating elements
     * @param model the renderer model
     * @return one or more rendering elements
     */
    public IRenderingElement generateBondElement(
            IBond bond, RendererModel model) {
        return generateBondElement(bond, bond.getOrder(), model);
    }

    /**
     * Generate a LineElement or an ElementGroup of LineElements for this bond.
     * This version should be used if you want to override the type - for
     * example, for ring double bonds.
     *
     * @param bond the bond to generate for
     * @param type the type of the bond - single, double, etc
     * @param model the renderer model
     * @return one or more rendering elements
     */
    public IRenderingElement generateBondElement(
            IBond bond, IBond.Order type, RendererModel model) {
        // More than 2 atoms per bond not supported by this module
        if (bond.getAtomCount() > 2)
            return null;

        // is object right? if not replace with a good one
        Point2d point1 = bond.getAtom(0).getPoint2d();
        Point2d point2 = bond.getAtom(1).getPoint2d();
        Color color = this.getColorForBond(bond, model);
        double bondWidth = this.getWidthForBond(bond, model);
		double bondDistance = (Double)model.get(BondDistance.class) /
        model.getParameter(Scale.class).getValue();
        if (type == IBond.Order.SINGLE) {
            return new LineElement(point1.x, point1.y, point2.x, point2.y, bondWidth, color);
        } else {
            ElementGroup group = new ElementGroup();
            switch (type) {
            case DOUBLE:
                createLines(point1, point2, bondWidth, bondDistance, color, group);
                break;
            case TRIPLE:
                createLines(point1, point2, bondWidth, bondDistance * 2, color, group);
                group.add(new LineElement(
                        point1.x, point1.y, point2.x, point2.y, bondWidth, color));
                break;
            case QUADRUPLE:
                createLines(point1, point2, bondWidth, bondDistance, color, group);
                createLines(point1, point2, bondWidth, bondDistance * 4, color, group);
            default:
                break;
            }
            return group;
        }
    }

    private void createLines(Point2d point1, Point2d point2, double width, double dist,
            Color color, ElementGroup group) {
        double[] out = generateDistanceData(point1, point2, dist);
        LineElement l1 =
            new LineElement(out[0], out[1], out[4], out[5], width, color);
        LineElement l2 =
            new LineElement(out[2], out[3], out[6], out[7], width, color);
        group.add(l1);
        group.add(l2);
    }

    private double[] generateDistanceData(Point2d point1, Point2d point2, double dist) {
        Vector2d normal = new Vector2d();
        normal.sub(point2, point1);
        normal = new Vector2d(-normal.y, normal.x);
        normal.normalize();
        normal.scale(dist);

        Point2d line1p1 = new Point2d();
        Point2d line1p2 = new Point2d();
        line1p1.add(point1, normal);
        line1p2.add(point2, normal);

        normal.negate();
        Point2d line2p1 = new Point2d();
        Point2d line2p2 = new Point2d();
        line2p1.add(point1, normal);
        line2p2.add(point2, normal);

        return new double[] { 
                line1p1.x, line1p1.y, line2p1.x, line2p1.y,
                line1p2.x, line1p2.y, line2p2.x, line2p2.y };
    }

    /**
     * Generate ring elements, such as inner-ring bonds or ring stereo elements.
     * 
     * @param bond the ring bond to use when generating elements
     * @param ring the ring that the bond is in
     * @param model the renderer model
     * @return one or more rendering elements
     */
    public IRenderingElement generateRingElements(
            IBond bond, IRing ring, RendererModel model) {
        if (isSingle(bond) && isStereoBond(bond)) {
            return generateStereoElement(bond, model);
        } else if (isDouble(bond)) {
            ElementGroup pair = new ElementGroup();
            pair.add(generateBondElement(bond, IBond.Order.SINGLE, model));
            pair.add(generateInnerElement(bond, ring, model));
            return pair;
        } else {
            return generateBondElement(bond, model);
        }
    }

    /**
     * Make the inner ring bond, which is slightly shorter than the outer bond. 
     * 
     * @param bond the ring bond
     * @param ring the ring that the bond is in
     * @param model the renderer model
     * @return the line element
     */
    public LineElement generateInnerElement(
            IBond bond, IRing ring, RendererModel model) {
        Point2d center = GeometryUtil.get2DCenter(ring);
        Point2d a = bond.getAtom(0).getPoint2d();
        Point2d b = bond.getAtom(1).getPoint2d();

        // the proportion to move in towards the ring center
        double distanceFactor = model.getParameter(TowardsRingCenterProportion.class).getValue();
        double ringDistance = distanceFactor * IDEAL_RINGSIZE / ring.getAtomCount();
        if (ringDistance < distanceFactor / MIN_RINGSIZE_FACTOR) ringDistance = distanceFactor / MIN_RINGSIZE_FACTOR;

        Point2d w = new Point2d();
        w.interpolate(a, center, ringDistance);
        Point2d u = new Point2d();
        u.interpolate(b, center, ringDistance);

        double alpha = 0.2;
        Point2d ww = new Point2d();
        ww.interpolate(w, u, alpha);
        Point2d uu = new Point2d();
        uu.interpolate(u, w, alpha);
        
        double width = getWidthForBond(bond, model);
        Color color = getColorForBond(bond, model); 

        return new LineElement(u.x, u.y, w.x, w.y, width, color);
    }

	private IRenderingElement generateStereoElement(
			IBond bond, RendererModel model) {

		IBond.Stereo stereo = bond.getStereo();
		WedgeLineElement.TYPE type = WedgeLineElement.TYPE.WEDGED;
		Direction dir = Direction.toSecond;
		if (stereo == IBond.Stereo.DOWN ||
				stereo == IBond.Stereo.DOWN_INVERTED)
			type = WedgeLineElement.TYPE.DASHED;
		if (stereo == IBond.Stereo.UP_OR_DOWN ||
				stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
			type = WedgeLineElement.TYPE.INDIFF;
		if (stereo == IBond.Stereo.DOWN_INVERTED ||
				stereo == IBond.Stereo.UP_INVERTED ||
				stereo == IBond.Stereo.UP_OR_DOWN_INVERTED)
			dir = Direction.toFirst;

		IRenderingElement base = generateBondElement(
				bond, IBond.Order.SINGLE, model);
		return new WedgeLineElement(
				(LineElement) base, type, dir, getColorForBond(bond, model));
	}

    /**
     * Check to see if a bond is a double bond.
     * 
     * @param bond the bond to check
     * @return true if its order is double
     */
    private boolean isDouble(IBond bond) {
        return bond.getOrder() == IBond.Order.DOUBLE;
    }

    /**
     * Check to see if a bond is a single bond.
     * 
     * @param bond the bond to check
     * @return true if its order is single
     */
    private boolean isSingle(IBond bond) {
        return bond.getOrder() == IBond.Order.SINGLE;
    }

    /**
     * Check to see if a bond is a stereo bond.
     * 
     * @param bond the bond to check
     * @return true if the bond has stero information
     */
    private boolean isStereoBond(IBond bond) {
        return bond.getStereo() != IBond.Stereo.NONE
        && bond.getStereo() != (IBond.Stereo)CDKConstants.UNSET
        && bond.getStereo() != IBond.Stereo.E_Z_BY_COORDINATES;
    }

    /**
     * Check to see if any of the atoms in this bond are hydrogen atoms.
     * 
     * @param bond the bond to check
     * @return true if any atom has an element symbol of "H"
     */
    protected boolean bindsHydrogen(IBond bond) {
        for (int i = 0; i < bond.getAtomCount(); i++) {
            IAtom atom = bond.getAtom(i);
            if ("H".equals(atom.getSymbol()))
                return true;
        }
        return false;
    }

    /**
     * Generate stereo or bond elements for this bond.
     * 
     * @param bond the bond to use when generating elements
     * @param model the renderer model
     * @return one or more rendering elements
     */
    public IRenderingElement generateBond(IBond bond, RendererModel model) {
    	boolean showExplicitHydrogens = true;
    	if (model.hasParameter(
    		 BasicAtomGenerator.ShowExplicitHydrogens.class
    		)) {
    		showExplicitHydrogens = model.getParameter(
                    BasicAtomGenerator.ShowExplicitHydrogens.class
            ).getValue();
    	}

        if (!showExplicitHydrogens && bindsHydrogen(bond)) {
            return null;
        }

        if (isStereoBond(bond)) {
            return generateStereoElement(bond, model);
        } else {
            return generateBondElement(bond, model);
        }
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameters")
    public List<IGeneratorParameter<?>> getParameters() {
        return Arrays.asList(
                new IGeneratorParameter<?>[] {
                        bondWidth,
                        defaultBondColor,
                        wedgeWidth,
                        bondDistance,
                        ringCenterProportion
                }
        );
    }

}

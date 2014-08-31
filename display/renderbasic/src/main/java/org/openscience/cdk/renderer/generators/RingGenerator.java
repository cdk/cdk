/*  Copyright (C) 2008  Arvid Berg <goglepox@users.sf.net>
 *                2011  Jonty Lawson <jontyl@users.sourceforge.net>
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

import static org.openscience.cdk.CDKConstants.ISAROMATIC;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Point2d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * Generates just the aromatic indicators for rings : circles, or light-gray
 * inner bonds, depending on the value of CDKStyleAromaticity.
 * 
 * @cdk.module renderbasic
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.renderer.generators.RingGeneratorTest")
public class RingGenerator extends BasicBondGenerator {

    /**
     * Determines whether rings should be drawn with a circle if they are
     * aromatic.
     */
    public static class ShowAromaticity extends
    AbstractGeneratorParameter<Boolean> {
    	/** Returns the default value.
    	 * @return {@link Boolean}.TRUE */
    	public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }
    private IGeneratorParameter<Boolean> showAromaticity = new ShowAromaticity();

    /**
     * Depicts aromaticity of rings in the original CDK style.
     */
    public static class CDKStyleAromaticity extends
    AbstractGeneratorParameter<Boolean> {
    	/** Returns the default value.
    	 * @return {@link Boolean}.FALSE */
    	public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    /** If true, the aromatic ring is indicated by light gray inner bonds */
    private IGeneratorParameter<Boolean> cdkStyleAromaticity = new CDKStyleAromaticity();

    /**
     * The maximum ring size for which an aromatic ring should be drawn.
     */
    public static class MaxDrawableAromaticRing extends AbstractGeneratorParameter<Integer> {

    	/**
    	 * The maximum default ring size for which an aromatic ring should be drawn.
    	 *
    	 * @return the maximum ring size
    	 */
    	public Integer getDefault() {

    		return 8;
    	}
    }
    private IGeneratorParameter<Integer> maxDrawableAromaticRing = new MaxDrawableAromaticRing();

    /**
     * The proportion of a ring bounds to use to draw the ring.
     */
    public static class RingProportion extends
    AbstractGeneratorParameter<Double> {
    	/** Returns the default value.
    	 * @return 0.35 */
    	public Double getDefault() {
            return 0.35;
        }
    }
    private IGeneratorParameter<Double> ringProportion = new RingProportion();

    /**
     * The rings that have already been painted - that is, a ring element
     * has been generated for it.
     */
    private Set<IRing> painted_rings;

    /**
     * Make a generator for ring elements.
     */
    public RingGenerator() {
        this.painted_rings = new HashSet<IRing>();
    }

    /** {@inheritDoc} */
    public IRenderingElement generateRingElements(
            IBond bond, IRing ring, RendererModel model) {
    	if (ringIsAromatic(ring) && showAromaticity.getValue()
    			&& ring.getAtomCount() < maxDrawableAromaticRing.getValue()) {
            ElementGroup pair = new ElementGroup();
            if (cdkStyleAromaticity.getValue()) {
                pair.add(generateBondElement(bond, IBond.Order.SINGLE, model));
                super.setOverrideColor(Color.LIGHT_GRAY);
                pair.add(generateInnerElement(bond, ring, model));
                super.setOverrideColor(null);
            } else {
                pair.add(generateBondElement(bond, IBond.Order.SINGLE, model));
                if (!painted_rings.contains(ring)) {
                    painted_rings.add(ring);
                    pair.add(generateRingRingElement(bond, ring, model));
                }
            }
            return pair;
        } else {
            return super.generateRingElements(bond, ring, model);
        }
    }

    private IRenderingElement generateRingRingElement(
            IBond bond, IRing ring, RendererModel model) {
        Point2d c = GeometryUtil.get2DCenter(ring);

        double[] minmax = GeometryUtil.getMinMax(ring);
        double width  = minmax[2] - minmax[0];
        double height = minmax[3] - minmax[1];
        double radius = Math.min(width, height) * ringProportion.getValue();
        Color color = getColorForBond(bond, model);
        
        return new OvalElement(c.x, c.y, radius, false, color);
    }

    private boolean ringIsAromatic(IRing ring) {
        boolean isAromatic = true;
        for (IAtom atom : ring.atoms()) {
            if (!atom.getFlag(ISAROMATIC)) {
                isAromatic = false;
                break;
            }
        }
        if (!isAromatic) {
            isAromatic = true;
            for (IBond b : ring.bonds()) {
                if (!b.getFlag(ISAROMATIC)) {
                    return false;
                }
            }
        }
        return isAromatic;
    }

    /** {@inheritDoc} */
    @TestMethod("testGetParameters")
    public List<IGeneratorParameter<?>> getParameters() {
        // Get our super class's version of things
        List<IGeneratorParameter<?>> superPars = super.getParameters();

        // Allocate ArrayList with sufficient space for everything.
        // Note that the number should ideally be the same as the number of entries
        // that we add here, though this is *only* an efficiency consideration.
        List<IGeneratorParameter<?>> pars =
            new ArrayList<IGeneratorParameter<?>>(superPars.size() + 3);

        pars.addAll(superPars);
        pars.add(cdkStyleAromaticity);
        pars.add(showAromaticity);
        pars.add(maxDrawableAromaticRing);
        pars.add(ringProportion);
        return pars;
    }
}

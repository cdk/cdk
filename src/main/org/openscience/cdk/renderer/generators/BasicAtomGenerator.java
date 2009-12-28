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
import java.util.Collections;
import java.util.List;

import javax.vecmath.Point2d;

import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.RenderingParameters.AtomShape;
import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;
import org.openscience.cdk.validate.ProblemMarker;

/**
 * @cdk.module renderbasic
 */
public class BasicAtomGenerator implements IGenerator {

    public static class AtomColor extends
        AbstractGeneratorParameter<Color> {
        public Color getDefault() {
            return Color.BLACK;
        }
    }
    private IGeneratorParameter<Color> atomColor = new AtomColor();

    public static class AtomColorer extends
        AbstractGeneratorParameter<IAtomColorer> {
        public IAtomColorer getDefault() {
            return new CDK2DAtomColors();
        }
    }
    private IGeneratorParameter<IAtomColorer> atomColorer = new AtomColorer();

    public static class ColorByType extends
        AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.TRUE;
        }
    }
    private IGeneratorParameter<Boolean> colorByType = new ColorByType();

	public BasicAtomGenerator() {}

	public IRenderingElement generate(IAtomContainer ac, RendererModel model) {
		ElementGroup elementGroup = new ElementGroup();
		for (IAtom atom : ac.atoms()) {
			elementGroup.add(this.generate(ac, atom, model));
		}
		return elementGroup;
	}
	
	public boolean hasCoordinates(IAtom atom) {
	    return atom != null && atom.getPoint2d() != null;   
	}
	
	public boolean invisibleHydrogen(IAtom atom, RendererModel model) {
	    return isHydrogen(atom) && !model.getShowExplicitHydrogens();
	}
	
	public boolean invisibleCarbon(
	        IAtom atom, IAtomContainer ac, RendererModel model) {
	    return isCarbon(atom) && !showCarbon(atom, ac, model);
	}
	
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

	public IRenderingElement generate(
	        IAtomContainer ac, IAtom atom, RendererModel model) {
	    if (!canDraw(atom, ac, model)) {
	        return null;
	    } else if (model.getIsCompact()) {
		    return this.generateCompactElement(atom, model);
		} else {
    		int alignment = 0;
    		if (isCarbon(atom)) {
    		    alignment = GeometryTools.getBestAlignmentForLabel(ac, atom);
    		} else {
    		    alignment = GeometryTools.getBestAlignmentForLabelXY(ac, atom);
    		}
    
    		return generateElements(atom, alignment, model);
		}
	}

	public IRenderingElement generateCompactElement(
	        IAtom atom, RendererModel model) {
	    Point2d p = atom.getPoint2d();
	    double r = model.getAtomRadius() / model.getScale();
	    double d = 2 * r;
	    if (model.getCompactShape() == AtomShape.SQUARE) {
    	    return new RectangleElement(
    	            p.x - r, p.y - r, d, d, true, getAtomColor(atom));
	    } else {
	        return new OvalElement(
	                p.x, p.y, r, true, getAtomColor(atom));
	    }
	}

	public IRenderingElement generateElements(
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
				atom.getHydrogenCount(),
				alignment, getAtomColor(atom));
	}

	public boolean isHydrogen(IAtom atom) {
		return "H".equals(atom.getSymbol());
	}

	public boolean isCarbon(IAtom atom) {
		return "C".equals(atom.getSymbol());
	}

	public boolean showCarbon(
	        IAtom atom, IAtomContainer ac, RendererModel model) {

		if (model.getKekuleStructure())
			return true;

		if (atom.getFormalCharge() != 0)
			return true;

		if (ac.getConnectedBondsList(atom).size() < 1)
			return true;

		if (model.getShowEndCarbons()
		        && ac.getConnectedBondsList(atom).size() == 1)
			return true;

		if (atom.getProperty(ProblemMarker.ERROR_MARKER) != null)
			return true;

		if (ac.getConnectedSingleElectronsCount(atom) > 0)
			return true;

		return false;
	}

	/* Returns the drawing color of the given atom. An atom is colored as
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

    public List<IGeneratorParameter<?>> getParameters() {
        return Collections.emptyList();
    }
}

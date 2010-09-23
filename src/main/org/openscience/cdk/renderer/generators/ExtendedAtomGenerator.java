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
 *
 */
public class ExtendedAtomGenerator extends BasicAtomGenerator {

    public static class ShowImplicitHydrogens extends
    AbstractGeneratorParameter<Boolean> {
    	public Boolean getDefault() {
    		return Boolean.TRUE;
    	}
    }
    private IGeneratorParameter<Boolean> showImplicitHydrogens =
    	new ShowImplicitHydrogens();

    public static class ShowAtomTypeNames extends
                        AbstractGeneratorParameter<Boolean> {
        public Boolean getDefault() {
            return Boolean.FALSE;
        }
    }
    private ShowAtomTypeNames showAtomTypeNames =
    	new ShowAtomTypeNames();
    
    public IRenderingElement generate(
            IAtomContainer ac, IAtom atom, RendererModel model) {
        boolean drawNumbers = 
            model.getParameter(WillDrawAtomNumbers.class).getValue(); 
        if (!hasCoordinates(atom) 
             || invisibleHydrogen(atom, model) 
             || (invisibleCarbon(atom, ac, model) 
             && !drawNumbers)) {
            return null;
        } else if (model.getParameter(CompactAtom.class).getValue()) {
            return this.generateCompactElement(atom, model);
        } else {
            String text;
            if (atom instanceof IPseudoAtom) {
                text = ((IPseudoAtom) atom).getLabel();
            } else if (invisibleCarbon(atom, ac, model) && drawNumbers) {
                text = String.valueOf(ac.getAtomNumber(atom) + 1);
            } else {
                text = atom.getSymbol();
            }
            Point2d p = atom.getPoint2d();
            Color c = getAtomColor(atom);
            TextGroupElement textGroup = new TextGroupElement(p.x, p.y, text, c);
            decorate(textGroup, ac, atom, model);
            return textGroup;
        }
    }
    
    public boolean hideAtomSymbol(IAtom atom, RendererModel model) {
        return atom.getSymbol().equals("C") &&
               !model.getParameter(KekuleStructure.class).getValue();
    }
    
    public void decorate(TextGroupElement textGroup, 
                         IAtomContainer ac, 
                         IAtom atom, 
                         RendererModel model) {
        Stack<Position> unused = getUnusedPositions(ac, atom);
        
        boolean drawNumbers = 
            model.getParameter(WillDrawAtomNumbers.class).getValue();
        if (!invisibleCarbon(atom, ac, model) && drawNumbers) {
            Position position = getNextPosition(unused);
            String number = String.valueOf(ac.getAtomNumber(atom) + 1);
            textGroup.addChild(number, position);
        }
        
        if (showImplicitHydrogens.getValue()) {
        	if(atom.getImplicitHydrogenCount()!=null){
	            int nH = atom.getImplicitHydrogenCount();
	            if (nH > 0) {
	                Position position = getNextPosition(unused);
	                if (nH == 1) {
	                    textGroup.addChild("H", position);
	                } else {
	                    textGroup.addChild("H", String.valueOf(nH), position);
	                }
	            }
        	}
        }
        
        Integer massNumber = atom.getMassNumber();
        if (massNumber != null) {
            try {
                IsotopeFactory factory = 
                    IsotopeFactory.getInstance(ac.getBuilder());
                int majorMass = 
                    factory.getMajorIsotope(atom.getSymbol()).getMassNumber();
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
    
    public Stack<Position> getUnusedPositions(IAtomContainer ac, IAtom atom) {
        Stack<Position> unused = new Stack<Position>();
        for (Position p : Position.values()) {
            unused.add(p);
        }
        
        for (IAtom connectedAtom : ac.getConnectedAtomsList(atom)) {
            Position used = getPosition(atom, connectedAtom);
            if (unused.contains(used)) {
                unused.remove(used);
            }
        }
        return unused;
    }
    
    public Position getPosition(IAtom atom, IAtom connectedAtom) {
        Point2d pA = atom.getPoint2d();
        Point2d pB = connectedAtom.getPoint2d();
        double dx = pB.x - pA.x;
        double dy = pB.y - pA.y;
        
        final double DELTA = 0.2;
        
        if (dx < -DELTA) {                          // generally west
            if (dy < -DELTA) {
                return Position.NW;
            } else if (dy > -DELTA && dy < DELTA) {
                return Position.W;
            } else {
                return Position.SW;
            }
        } else if (dx > -DELTA && dx < DELTA) {     //  north or south
            if (dy < -DELTA) {
                return Position.N;
            } else if (dy > -DELTA && dy < DELTA) { // right on top of the atom!
                return Position.N;                  // XXX
            } else {
                return Position.S;
            }
        } else {                                    // generally east 
            if (dy < -DELTA) {
                return Position.NE;
            } else if (dy > -DELTA && dy < DELTA) {
                return Position.E;
            } else {
                return Position.SE;
            }
        }
    }
    
    public List<IGeneratorParameter<?>> getParameters() {
    	List<IGeneratorParameter<?>> parameters =
    		new ArrayList<IGeneratorParameter<?>>();
    	parameters.add(showImplicitHydrogens);
    	parameters.add(showAtomTypeNames);
    	parameters.addAll(super.getParameters());
    	return parameters;
    }
}

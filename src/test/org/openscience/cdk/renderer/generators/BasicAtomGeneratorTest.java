/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer.generators;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomColor;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomColorer;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ColorByType;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;

/**
 * @cdk.module test-renderbasic
 */
public class BasicAtomGeneratorTest extends AbstractGeneratorTest {
	
	private BasicAtomGenerator generator;
	
	@Override
	public Rectangle getCustomCanvas() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		super.setup();
		this.generator = new BasicAtomGenerator();
		model.registerParameters(generator);
	}
	
	@Test
	public void ovalShapeTest() {
	    IAtomContainer singleAtom = makeSingleAtom();
	    model.set(CompactShape.class, Shape.OVAL);
	    model.set(CompactAtom.class, true);
        List<IRenderingElement> elements = 
            getAllSimpleElements(generator, singleAtom);
        Assert.assertEquals(1, elements.size());
        Assert.assertEquals(OvalElement.class, elements.get(0).getClass());
	}
	
	@Test
    public void squareShapeTest() {
        IAtomContainer singleAtom = makeSingleAtom();
        model.set(CompactShape.class, Shape.SQUARE);
        model.set(CompactAtom.class, true);
        List<IRenderingElement> elements = 
            getAllSimpleElements(generator, singleAtom);
        Assert.assertEquals(1, elements.size());
        Assert.assertEquals(RectangleElement.class, elements.get(0).getClass());
    }
	
	@Test
	public void atomColorTest() {
	    Color testColor = Color.RED;
	    IAtomContainer singleAtom = makeSingleAtom("O");
	    model.set(AtomColor.class, testColor);
	    model.set(ColorByType.class, false);
	    List<IRenderingElement> elements = 
            getAllSimpleElements(generator, singleAtom);
	    Assert.assertEquals(1, elements.size());
	    AtomSymbolElement element = ((AtomSymbolElement)elements.get(0));
	    Assert.assertEquals(testColor, element.color);
	}
	
	@Test
	public void atomColorerTest() {
	    IAtomContainer cnop = makeSNOPSquare();
	    final Map<String, Color> colorMap = new HashMap<String, Color>();
	    colorMap.put("S", Color.YELLOW);
	    colorMap.put("N", Color.BLUE);
	    colorMap.put("O", Color.RED);
	    colorMap.put("P", Color.MAGENTA);
	    IAtomColorer atomColorer = new IAtomColorer() {

            public Color getAtomColor(IAtom atom) {
                String symbol = atom.getSymbol();
                if (colorMap.containsKey(symbol)) {
                    return colorMap.get(symbol);
                } else {
                    return null;
                }
            }

            public Color getAtomColor(IAtom atom, Color defaultColor) {
                Color color = getAtomColor(atom);
                if (color == null) {
                    return defaultColor;
                } else {
                    return color;
                }
            }
	    };
	    model.set(AtomColorer.class, atomColorer);
	    List<IRenderingElement> elements = getAllSimpleElements(generator, cnop);
        Assert.assertEquals(4, elements.size());
        for (IRenderingElement element : elements) {
            AtomSymbolElement symbolElement = (AtomSymbolElement) element;
            String symbol = symbolElement.text;
            Assert.assertTrue(colorMap.containsKey(symbol));
            Assert.assertEquals(colorMap.get(symbol), symbolElement.color);
        }
	}
	
	@Test
	public void colorByTypeTest() {
	    IAtomContainer snop = makeSNOPSquare();
	    model.set(ColorByType.class, false);
	    List<IRenderingElement> elements = getAllSimpleElements(generator, snop);
	    Color defaultColor = model.getDefault(AtomColor.class);
	    for (IRenderingElement element : elements) {
            AtomSymbolElement symbolElement = (AtomSymbolElement) element;
            Assert.assertEquals(defaultColor, symbolElement.color);
	    }
	}
	
	@Test
	public void testSingleAtom() {
		IAtomContainer singleAtom = makeSingleAtom();
		
		// nothing should be made
		IRenderingElement root = generator.generate(singleAtom, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(1, elements.size());
	}
	
	@Test
	public void testSingleBond() {
		IAtomContainer container = makeSingleBond();
		
		// generate the single line element
		IRenderingElement root = generator.generate(container, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(2, elements.size());
		
		// test that the endpoints are distinct
		OvalElement ovalA = (OvalElement) elements.get(0);
		OvalElement ovalB = (OvalElement) elements.get(1);
		Assert.assertNotSame(0, distance(ovalA.x, ovalA.y, ovalB.x, ovalB.y));
	}
	
	@Test
	public void testSquare() {
		IAtomContainer square = makeSquare();
		
		// generate all four atoms
		IRenderingElement root = generator.generate(square, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(4, elements.size());
		
		// test that the center is at the origin
		Assert.assertEquals(new Point2d(0,0), center(elements));
	}
}

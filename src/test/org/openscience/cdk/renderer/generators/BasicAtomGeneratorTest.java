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
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.AtomRadius;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ColorByType;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.KekuleStructure;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowEndCarbons;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowExplicitHydrogens;
import org.openscience.cdk.validate.ProblemMarker;

/**
 * @cdk.module test-renderbasic
 */
public class BasicAtomGeneratorTest extends AbstractGeneratorTest {

    private BasicAtomGenerator generator;

    @Override
    public Rectangle getCustomCanvas() {
        return null;
    }

    @Before
    public void setup() {
        super.setup();
        this.generator = new BasicAtomGenerator();
        model.registerParameters(generator);
    }
    
    @Test
    public void generateElementTest() {
        IAtom atom = super.builder.newInstance(IAtom.class, "C");
        atom.setPoint2d(new Point2d(2, 3));
        atom.setImplicitHydrogenCount(0);
        int alignment = 1;
        AtomSymbolElement element = 
            generator.generateElement(atom, alignment, model);
        Assert.assertEquals(atom.getPoint2d().x, element.xCoord);
        Assert.assertEquals(atom.getPoint2d().y, element.yCoord);
        Assert.assertEquals(atom.getSymbol(), element.text);
        Assert.assertEquals((int)atom.getFormalCharge(), element.formalCharge);
        Assert.assertEquals((int)atom.getImplicitHydrogenCount(), element.hydrogenCount);
        Assert.assertEquals(alignment, element.alignment);
        Assert.assertEquals(generator.getAtomColor(atom, model), element.color);
    }

    @Test
    public void hasCoordinatesTest() {
        IAtom atomWithCoordinates = super.builder.newInstance(IAtom.class);
        atomWithCoordinates.setPoint2d(new Point2d(0, 0));
        Assert.assertTrue(generator.hasCoordinates(atomWithCoordinates));

        IAtom atomWithoutCoordinates = super.builder.newInstance(IAtom.class);
        atomWithoutCoordinates.setPoint2d(null);
        Assert.assertFalse(generator.hasCoordinates(atomWithoutCoordinates));

        IAtom nullAtom = null;
        Assert.assertFalse(generator.hasCoordinates(nullAtom));
    }

    @Test
    public void canDrawTest() {
        IAtom drawableCAtom = super.builder.newInstance(IAtom.class, "C");
        drawableCAtom.setPoint2d(new Point2d(0, 0));

        IAtom drawableHAtom = super.builder.newInstance(IAtom.class, "H");
        drawableHAtom.setPoint2d(new Point2d(0, 0));

        IAtomContainer dummyContainer = 
            super.builder.newInstance(IAtomContainer.class);

        model.set(KekuleStructure.class, true);
        model.set(ShowExplicitHydrogens.class, true);

        Assert.assertTrue(
                generator.canDraw(drawableCAtom, dummyContainer, model));
        Assert.assertTrue(
                generator.canDraw(drawableHAtom, dummyContainer, model));
    }

    @Test
    public void invisibleHydrogenTest() {
        IAtom hydrogen = super.builder.newInstance(IAtom.class, "H");
        model.set(ShowExplicitHydrogens.class, false);
        Assert.assertTrue(generator.invisibleHydrogen(hydrogen, model));

        model.set(ShowExplicitHydrogens.class, true);
        Assert.assertFalse(generator.invisibleHydrogen(hydrogen, model));

        IAtom nonHydrogen = super.builder.newInstance(IAtom.class, "C");
        model.set(ShowExplicitHydrogens.class, false);
        Assert.assertFalse(generator.invisibleHydrogen(nonHydrogen, model));

        model.set(ShowExplicitHydrogens.class, true);
        Assert.assertFalse(generator.invisibleHydrogen(nonHydrogen, model));
    }

    @Test
    public void invisibleCarbonTest() {
        // NOTE : just testing the element symbol here, see showCarbonTest
        // for the full range of possibilities...
        IAtom carbon = super.builder.newInstance(IAtom.class, "C");
        IAtomContainer dummyContainer = 
            super.builder.newInstance(IAtomContainer.class);

        // we force the issue by making isKekule=true
        model.set(KekuleStructure.class, true);

        Assert.assertFalse(
                generator.invisibleCarbon(carbon, dummyContainer, model));
    }

    @Test
    public void showCarbon_KekuleTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);

        model.set(KekuleStructure.class, true);
        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    public void showCarbon_FormalChargeTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);

        carbon.setFormalCharge(1);
        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    public void showCarbon_SingleCarbonTest() {
        IAtomContainer atomContainer = super.makeSingleAtom("C");
        IAtom carbon = atomContainer.getAtom(0);

        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    public void showCarbon_ShowEndCarbonsTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(0);
        model.set(ShowEndCarbons.class, true);
        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }
	
    @Test
    public void showCarbon_ErrorMarker() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);
        ProblemMarker.markWithError(carbon);
        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));        
    }
    
    @Test
    public void showCarbon_ConnectedSingleElectrons() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);
        atomContainer.addSingleElectron(1);
        Assert.assertTrue(generator.showCarbon(carbon, atomContainer, model));        
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
    public void getAtomColorTest() {
        Color testColor = Color.RED;
        IAtomContainer singleAtom = makeSingleAtom("O");
        model.set(AtomColor.class, testColor);
        model.set(ColorByType.class, false);
        generator.getAtomColor(singleAtom.getAtom(0), model);
        
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
    public void showExplicitHydrogensTest() {
        IAtomContainer methane = makeMethane();
        // don't generate elements for hydrogens
        model.set(ShowExplicitHydrogens.class, false);
        List<IRenderingElement> carbonOnly =
            getAllSimpleElements(generator, methane);
        Assert.assertEquals(1, carbonOnly.size());

        // do generate elements for hydrogens
        model.set(ShowExplicitHydrogens.class, true);
        List<IRenderingElement> carbonPlusHydrogen =
            getAllSimpleElements(generator, methane);
        Assert.assertEquals(5, carbonPlusHydrogen.size());
    }

    @Test
    public void kekuleTest() {
        IAtomContainer singleBond = makeSingleBond();
        model.set(KekuleStructure.class, true);
        Assert.assertEquals(2, getAllSimpleElements(generator, singleBond).size());
        model.set(KekuleStructure.class, false);
        Assert.assertEquals(0, getAllSimpleElements(generator, singleBond).size());
    }

    @Test
    public void showEndCarbonsTest() {
        IAtomContainer singleBond = makeCCC();
        model.set(ShowEndCarbons.class, true);
        Assert.assertEquals(2, getAllSimpleElements(generator, singleBond).size());
        model.set(ShowEndCarbons.class, false);
        Assert.assertEquals(0, getAllSimpleElements(generator, singleBond).size());
    }

    @Test
    public void testSingleAtom() {
        IAtomContainer singleAtom = makeSingleAtom();

        // nothing should be made
        IRenderingElement root = 
            generator.generate(singleAtom, singleAtom.getAtom(0), model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assert.assertEquals(1, elements.size());
    }

    @Test
    public void testSingleBond() {
        IAtomContainer container = makeSingleBond();
        model.set(CompactAtom.class, true);
        model.set(CompactShape.class, Shape.OVAL);
        model.set(ShowEndCarbons.class, true);

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
        model.set(KekuleStructure.class, true);

        // generate all four atoms
        IRenderingElement root = generator.generate(square, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assert.assertEquals(4, elements.size());

        // test that the center is at the origin
        Assert.assertEquals(new Point2d(0,0), center(elements));
    }
    
    @Test
    public void getParametersTest() {
        List<IGeneratorParameter<?>> parameters = generator.getParameters();
        containsParameterType(parameters, AtomColor.class);
        containsParameterType(parameters, AtomColorer.class);
        containsParameterType(parameters, AtomRadius.class);
        containsParameterType(parameters, ColorByType.class);
        containsParameterType(parameters, CompactShape.class);
        containsParameterType(parameters, CompactAtom.class);
        containsParameterType(parameters, KekuleStructure.class);
        containsParameterType(parameters, ShowEndCarbons.class);
        containsParameterType(parameters, ShowExplicitHydrogens.class);
    }
}

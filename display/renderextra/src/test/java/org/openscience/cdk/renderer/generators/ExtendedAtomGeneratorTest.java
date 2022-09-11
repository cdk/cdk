/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 *               2012  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.color.IAtomColorer;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextGroupElement;
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
 * @cdk.module test-renderextra
 */
public class ExtendedAtomGeneratorTest extends BasicAtomGeneratorTest {

    private ExtendedAtomGenerator generator;

    @Override
    protected Rectangle getCustomCanvas() {
        return null;
    }

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
        this.generator = new ExtendedAtomGenerator();
        model.registerParameters(generator);
        super.setTestedGenerator(generator);
    }

    @Test
    @Override
    void generateElementTest() {
        IAtom atom = super.builder.newInstance(IAtom.class, "C");
        atom.setPoint2d(new Point2d(2, 3));
        atom.setImplicitHydrogenCount(0);
        int alignment = 1;
        AtomSymbolElement element = generator.generateElement(atom, alignment, model);
        Assertions.assertEquals(atom.getPoint2d().x, element.xCoord, 0.1);
        Assertions.assertEquals(atom.getPoint2d().y, element.yCoord, 0.1);
        Assertions.assertEquals(atom.getSymbol(), element.text);
        Assertions.assertEquals((int) atom.getFormalCharge(), element.formalCharge);
        Assertions.assertEquals((int) atom.getImplicitHydrogenCount(), element.hydrogenCount);
        Assertions.assertEquals(alignment, element.alignment);
        Assertions.assertEquals(generator.getAtomColor(atom, model), element.color);
    }

    @Test
    @Override
    void hasCoordinatesTest() {
        IAtom atomWithCoordinates = super.builder.newInstance(IAtom.class);
        atomWithCoordinates.setPoint2d(new Point2d(0, 0));
        Assertions.assertTrue(generator.hasCoordinates(atomWithCoordinates));

        IAtom atomWithoutCoordinates = super.builder.newInstance(IAtom.class);
        atomWithoutCoordinates.setPoint2d(null);
        Assertions.assertFalse(generator.hasCoordinates(atomWithoutCoordinates));

        IAtom nullAtom = null;
        Assertions.assertFalse(generator.hasCoordinates(nullAtom));
    }

    @Test
    @Override
    void canDrawTest() {
        IAtom drawableCAtom = super.builder.newInstance(IAtom.class, "C");
        drawableCAtom.setPoint2d(new Point2d(0, 0));

        IAtom drawableHAtom = super.builder.newInstance(IAtom.class, "H");
        drawableHAtom.setPoint2d(new Point2d(0, 0));

        IAtomContainer dummyContainer = super.builder.newInstance(IAtomContainer.class);

        model.set(KekuleStructure.class, true);
        model.set(ShowExplicitHydrogens.class, true);

        Assertions.assertTrue(generator.canDraw(drawableCAtom, dummyContainer, model));
        Assertions.assertTrue(generator.canDraw(drawableHAtom, dummyContainer, model));
    }

    @Test
    @Override
    void invisibleHydrogenTest() {
        IAtom hydrogen = super.builder.newInstance(IAtom.class, "H");
        model.set(ShowExplicitHydrogens.class, false);
        Assertions.assertTrue(generator.invisibleHydrogen(hydrogen, model));

        model.set(ShowExplicitHydrogens.class, true);
        Assertions.assertFalse(generator.invisibleHydrogen(hydrogen, model));

        IAtom nonHydrogen = super.builder.newInstance(IAtom.class, "C");
        model.set(ShowExplicitHydrogens.class, false);
        Assertions.assertFalse(generator.invisibleHydrogen(nonHydrogen, model));

        model.set(ShowExplicitHydrogens.class, true);
        Assertions.assertFalse(generator.invisibleHydrogen(nonHydrogen, model));
    }

    @Test
    @Override
    void invisibleCarbonTest() {
        // NOTE : just testing the element symbol here, see showCarbonTest
        // for the full range of possibilities...
        IAtom carbon = super.builder.newInstance(IAtom.class, "C");
        IAtomContainer dummyContainer = super.builder.newInstance(IAtomContainer.class);

        // we force the issue by making isKekule=true
        model.set(KekuleStructure.class, true);

        Assertions.assertFalse(generator.invisibleCarbon(carbon, dummyContainer, model));
    }

    @Test
    @Override
    void showCarbon_KekuleTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);

        model.set(KekuleStructure.class, true);
        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void showCarbon_FormalChargeTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);

        carbon.setFormalCharge(1);
        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void showCarbon_SingleCarbonTest() {
        IAtomContainer atomContainer = super.makeSingleAtom("C");
        IAtom carbon = atomContainer.getAtom(0);

        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void showCarbon_ShowEndCarbonsTest() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(0);
        model.set(ShowEndCarbons.class, true);
        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void showCarbon_ErrorMarker() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);
        ProblemMarker.markWithError(carbon);
        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void showCarbon_ConnectedSingleElectrons() {
        IAtomContainer atomContainer = super.makeCCC();
        IAtom carbon = atomContainer.getAtom(1);
        atomContainer.addSingleElectron(1);
        Assertions.assertTrue(generator.showCarbon(carbon, atomContainer, model));
    }

    @Test
    @Override
    void ovalShapeTest() {
        IAtomContainer singleAtom = makeSingleAtom();
        model.set(CompactShape.class, Shape.OVAL);
        model.set(CompactAtom.class, true);
        List<IRenderingElement> elements = getAllSimpleElements(generator, singleAtom);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(OvalElement.class, elements.get(0).getClass());
    }

    @Test
    @Override
    void squareShapeTest() {
        IAtomContainer singleAtom = makeSingleAtom();
        model.set(CompactShape.class, Shape.SQUARE);
        model.set(CompactAtom.class, true);
        List<IRenderingElement> elements = getAllSimpleElements(generator, singleAtom);
        Assertions.assertEquals(1, elements.size());
        Assertions.assertEquals(RectangleElement.class, elements.get(0).getClass());
    }

    @Test
    @Override
    void getAtomColorTest() {
        Color testColor = Color.RED;
        IAtomContainer singleAtom = makeSingleAtom("O");
        model.set(AtomColor.class, testColor);
        model.set(ColorByType.class, false);
        generator.getAtomColor(singleAtom.getAtom(0), model);

        List<IRenderingElement> elements = getAllSimpleElements(generator, singleAtom);
        Assertions.assertEquals(1, elements.size());
        TextGroupElement element = ((TextGroupElement) elements.get(0));
        Assertions.assertEquals(testColor, element.color);
    }

    @Test
    @Override
    void atomColorerTest() {
        IAtomContainer cnop = makeSNOPSquare();
        final Map<String, Color> colorMap = new HashMap<>();
        colorMap.put("S", Color.YELLOW);
        colorMap.put("N", Color.BLUE);
        colorMap.put("O", Color.RED);
        colorMap.put("P", Color.MAGENTA);
        IAtomColorer atomColorer = new IAtomColorer() {

            @Override
            public Color getAtomColor(IAtom atom) {
                String symbol = atom.getSymbol();
                if (colorMap.containsKey(symbol)) {
                    return colorMap.get(symbol);
                } else {
                    return null;
                }
            }

            @Override
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
        Assertions.assertEquals(4, elements.size());
        for (IRenderingElement element : elements) {
            TextGroupElement symbolElement = (TextGroupElement) element;
            String symbol = symbolElement.text;
            Assertions.assertTrue(colorMap.containsKey(symbol));
            Assertions.assertEquals(colorMap.get(symbol), symbolElement.color);
        }
    }

    @Test
    @Override
    void colorByTypeTest() {
        IAtomContainer snop = makeSNOPSquare();
        model.set(ColorByType.class, false);
        List<IRenderingElement> elements = getAllSimpleElements(generator, snop);
        Color defaultColor = model.getDefault(AtomColor.class);
        for (IRenderingElement element : elements) {
            TextGroupElement symbolElement = (TextGroupElement) element;
            Assertions.assertEquals(defaultColor, symbolElement.color);
        }
    }

    @Test
    @Override
    void showExplicitHydrogensTest() {
        IAtomContainer methane = makeMethane();
        // don't generate elements for hydrogens
        model.set(ShowExplicitHydrogens.class, false);
        List<IRenderingElement> carbonOnly = getAllSimpleElements(generator, methane);
        Assertions.assertEquals(1, carbonOnly.size());

        // do generate elements for hydrogens
        model.set(ShowExplicitHydrogens.class, true);
        List<IRenderingElement> carbonPlusHydrogen = getAllSimpleElements(generator, methane);
        Assertions.assertEquals(5, carbonPlusHydrogen.size());
    }

    @Test
    @Override
    void kekuleTest() {
        IAtomContainer singleBond = makeSingleBond();
        model.set(KekuleStructure.class, true);
        Assertions.assertEquals(2, getAllSimpleElements(generator, singleBond).size());
        model.set(KekuleStructure.class, false);
        Assertions.assertEquals(0, getAllSimpleElements(generator, singleBond).size());
    }

    @Test
    @Override
    void showEndCarbonsTest() {
        IAtomContainer singleBond = makeCCC();
        model.set(ShowEndCarbons.class, true);
        Assertions.assertEquals(2, getAllSimpleElements(generator, singleBond).size());
        model.set(ShowEndCarbons.class, false);
        Assertions.assertEquals(0, getAllSimpleElements(generator, singleBond).size());
    }

    @Test
    @Override
    void testSingleAtom() {
        IAtomContainer singleAtom = makeSingleAtom();

        // nothing should be made
        IRenderingElement root = generator.generate(singleAtom, singleAtom.getAtom(0), model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(1, elements.size());
    }

    @Test
    @Override
    void testSingleBond() {
        IAtomContainer container = makeSingleBond();
        model.set(CompactAtom.class, true);
        model.set(CompactShape.class, Shape.OVAL);
        model.set(ShowEndCarbons.class, true);

        // generate the single line element
        IRenderingElement root = generator.generate(container, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(2, elements.size());

        // test that the endpoints are distinct
        OvalElement ovalA = (OvalElement) elements.get(0);
        OvalElement ovalB = (OvalElement) elements.get(1);
        Assertions.assertNotSame(0, distance(ovalA.xCoord, ovalA.yCoord, ovalB.xCoord, ovalB.yCoord));
    }

    @Test
    @Override
    void testSquare() {
        IAtomContainer square = makeSquare();
        model.set(KekuleStructure.class, true);

        // generate all four atoms
        IRenderingElement root = generator.generate(square, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(4, elements.size());

        // test that the center is at the origin
        Assertions.assertEquals(new Point2d(0, 0), center(elements));
    }

    @Test
    @Override
    void getParametersTest() {
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

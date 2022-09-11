package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.MarkedElement;

/**
 * Test the {@link BasicBondGenerator}.
 *
 * @author     maclean
 * @cdk.module test-renderbasic
 */
public class BasicBondGeneratorTest extends AbstractGeneratorTest {

    private BasicBondGenerator generator;

    static IRenderingElement unbox(IRenderingElement element) {
        if (element instanceof MarkedElement)
            return ((MarkedElement) element).element();
        return element;
    }

    @Override
    protected Rectangle getCustomCanvas() {
        return null;
    }

    @BeforeEach
    @Override
    void setup() {
        super.setup();
        this.generator = new BasicBondGenerator();
        model.registerParameters(generator);
        super.setTestedGenerator(generator);
    }

    @Test
    void testSingleAtom() {
        IAtomContainer singleAtom = makeSingleAtom();

        // nothing should be made
        IRenderingElement root = generator.generate(singleAtom, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(0, elements.size());
    }

    @Test
    void testSingleBond() {
        IAtomContainer container = makeSingleBond();

        // generate the single line element
        IRenderingElement root = generator.generate(container, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(1, elements.size());

        // test that the endpoints are distinct
        LineElement line = (LineElement) elements.get(0);
        Assertions.assertNotSame(0, AbstractGeneratorTest.length(line));
    }

    @Test
    void testSquare() {
        IAtomContainer square = makeSquare();

        // generate all four bonds
        IRenderingElement root = generator.generate(square, model);
        List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
        Assertions.assertEquals(4, elements.size());

        // test that the center is at the origin
        Assertions.assertEquals(new Point2d(0, 0), center(elements));
    }

}

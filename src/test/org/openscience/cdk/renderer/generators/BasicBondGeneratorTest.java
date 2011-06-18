package org.openscience.cdk.renderer.generators;

import java.awt.Rectangle;
import java.util.List;

import javax.vecmath.Point2d;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;

/**
 * Test the {@link BasicBondGenerator}.
 * 
 * @author     maclean
 * @cdk.module test-renderbasic
 */
public class BasicBondGeneratorTest extends AbstractGeneratorTest {
	
	private BasicBondGenerator generator;
	
	@Override
	public Rectangle getCustomCanvas() {
		return null;
	}

	@Before
	public void setup() {
		super.setup();
		this.generator = new BasicBondGenerator();
		model.registerParameters(generator);
	}
	
	@Test
	public void testSingleAtom() {
		IAtomContainer singleAtom = makeSingleAtom();
		
		// nothing should be made
		IRenderingElement root = generator.generate(singleAtom, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(0, elements.size());
	}
	
	@Test
	public void testSingleBond() {
		IAtomContainer container = makeSingleBond();
		
		// generate the single line element
		IRenderingElement root = generator.generate(container, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(1, elements.size());
		
		// test that the endpoints are distinct
		LineElement line = (LineElement) elements.get(0);
		Assert.assertNotSame(0, AbstractGeneratorTest.length(line));
	}
	
	@Test
	public void testSquare() {
		IAtomContainer square = makeSquare();
		
		// generate all four bonds
		IRenderingElement root = generator.generate(square, model);
		List<IRenderingElement> elements = elementUtil.getAllSimpleElements(root);
		Assert.assertEquals(4, elements.size());
		
		// test that the center is at the origin
		Assert.assertEquals(new Point2d(0,0), center(elements));
	}

}

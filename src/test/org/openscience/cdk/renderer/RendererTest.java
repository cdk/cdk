package org.openscience.cdk.renderer;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.Renderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.KekuleStructure;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowEndCarbons;

/**
 * @author maclean
 *
 */
public class RendererTest {
	
	private IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
	
	private StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	
	public IMolecule layout(IMolecule molecule) {
		sdg.setMolecule(molecule);
		try {
			sdg.generateCoordinates();
		} catch (Exception e) {
			System.err.println(e);
		}
		return sdg.getMolecule();
	}
	
	public IMolecule makeSquare() {
		IMolecule square = builder.newMolecule();
		square.addAtom(builder.newAtom("C"));
		square.addAtom(builder.newAtom("C"));
		square.addAtom(builder.newAtom("C"));
		square.addAtom(builder.newAtom("C"));
		square.addBond(0, 1, IBond.Order.SINGLE);
		square.addBond(0, 3, IBond.Order.SINGLE);
		square.addBond(1, 2, IBond.Order.SINGLE);
		square.addBond(2, 3, IBond.Order.SINGLE);
		
		return layout(square);
	}
	
	@Test
	public void testSquareMolecule() {
		IMolecule square = makeSquare();
		
		List<IGenerator> generators = new ArrayList<IGenerator>();
		generators.add(new BasicBondGenerator());
		BasicAtomGenerator atomGenerator = new BasicAtomGenerator();
		generators.add(atomGenerator);
		
		Renderer renderer = new Renderer(generators, new AWTFontManager());
		RendererModel model = renderer.getRenderer2DModel();
		model.getRenderingParameter(CompactShape.class).setValue(Shape.OVAL);
		model.getRenderingParameter(CompactAtom.class).setValue(true);
		model.getRenderingParameter(KekuleStructure.class).setValue(true);
		model.getRenderingParameter(ShowEndCarbons.class).setValue(true);
		
		// nasty hacks
//		((IGeneratorParameter<Shape>)atomGenerator.getParameters().get(4)).setValue(Shape.OVAL);
//		((IGeneratorParameter<Boolean>)atomGenerator.getParameters().get(5)).setValue(true);
//		((IGeneratorParameter<Boolean>)atomGenerator.getParameters().get(6)).setValue(true);
//		((IGeneratorParameter<Boolean>)atomGenerator.getParameters().get(7)).setValue(true);
		
		ElementUtility visitor = new ElementUtility();
		Rectangle screen = new Rectangle(0, 0, 100, 100);
		renderer.setup(square, screen);
		renderer.paint(square, visitor);
		
		for (IRenderingElement element : visitor.getElements()) {
			System.out.println(visitor.toString(element));
		}
	}

}

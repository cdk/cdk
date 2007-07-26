package org.openscience.cdk.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

public class TestRenderer {

	JFrame frame;
	SwingPainter painter = new SwingPainter();
	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	
	private TestRenderer() {
		frame = new TestFrame();
		painter = new SwingPainter();
		//IMolecule mol = MoleculeFactory.makeAlphaPinene();
		IMolecule mol = MoleculeFactory.makeThiazole();
		
		sdg.setMolecule(mol);
		try {
			sdg.generateCoordinates();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		painter.setMolecule(sdg.getMolecule());
		frame.add(painter);
		
		painter.setBackground(Color.WHITE);
	//	frame.setBackground(Color.GREEN);
		
	}

	private void run() {
		frame.setSize(400, 400);
		frame.show();
	}

	private class TestFrame extends JFrame {
	    private static final long serialVersionUID = 1;

	    private SwingPainter painter;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestRenderer prog = new TestRenderer();
		prog.run();
	}

	public class SwingPainter extends JComponent {

		Renderer2DModel model = new Renderer2DModel();
		IRenderer2D renderer = new Java2DRenderer(model);
		private IMolecule molecule;
		
		public void setMolecule(IMolecule molecule) {
			this.molecule = molecule;
		}
		
		public void paint(Graphics g) {
			//if (isOpaque()) { //paint background
	        //    g.setColor(getBackground());
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
	       // }
			super.paint(g);
			System.out.println("Painting molecule..!");
			
			model.setZoomFactor(0.5);
			renderer.paintMolecule(molecule, (Graphics2D)g, (Rectangle2D)getBounds());
		}

	}

}

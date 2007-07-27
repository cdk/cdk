
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

public class TestRenderer extends JPanel {

	JFrame frame;
	SwingPainter painter = new SwingPainter();
	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	//Graphics2D graphic;
	
	public class RendererListner implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			//System.out.println(e); 
			Point2D ptSrc = e.getPoint();
			
			//painter.getGraphics2D(),
			Point2D ptDst = Java2DRenderer.getCoorFromScreen( ptSrc);
			System.out.println("Mouse click at " + ptSrc + " real world coordinates: " + ptDst);
			Java2DRenderer.showClosestAtomOrBond(painter.getMolecule(), ptDst);
			
		}
		public void mouseEntered(MouseEvent e) { 	}
		public void mouseExited(MouseEvent e) { 	}
		public void mousePressed(MouseEvent e) { 	}
		public void mouseReleased(MouseEvent e) { 	}
	}
	
	private TestRenderer() {
		frame = new TestFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		painter = new SwingPainter();
		//painter.addMouseMotionListener(new TestRendererMouseE());
		//only react on mouse clicks for now
		painter.addMouseListener(new RendererListner());

		//IMolecule mol = MoleculeFactory.makeAlphaPinene();
		IMolecule mol = MoleculeFactory.makeThiazole();
		//IMolecule mol = MoleculeFactory.makeAlkane(18);
		System.out.println("molecule: " + mol);
		
		
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
		private static final long serialVersionUID = 2;

		Renderer2DModel model = new Renderer2DModel();
		
		IRenderer2D renderer = new Java2DRenderer(model);
		
		private IMolecule molecule;
		
		Graphics2D graphic;
		
		public void setMolecule(IMolecule molecule) {
			this.molecule = molecule;
		}
		public IMolecule getMolecule() {
			return this.molecule;
		}
		public Graphics2D getGraphics2D() {			
			return this.graphic;
		}
		AffineTransform affinelast = new AffineTransform();
		public void paint(Graphics g) {
			//if (isOpaque()) { //paint background
	        //    g.setColor(getBackground());
				g.setColor(getBackground());
				g.fillRect(0, 0, getWidth(), getHeight());
	       // }
			super.paint(g);
			//System.out.println("Painting molecule..!");
			graphic = (Graphics2D)g;
			model.setZoomFactor(1);
			
			if (!affinelast.equals(graphic.getTransform())) {
				System.out.println("swing changed matrix to:" + graphic.getTransform());
				affinelast = graphic.getTransform();
			}

			renderer.paintMolecule(molecule, (Graphics2D)g, (Rectangle2D)getBounds());
		}
	}
}


package org.openscience.cdk.renderer.progz;

import org.openscience.cdk.renderer.ISimpleRenderer2D;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.renderer.IRenderer2D;

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

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
//import org.openscience.cdk.interfaces.ISetOfMolecules;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TestRenderer extends JPanel {

	JFrame frame;
	SwingPainter painter = new SwingPainter();
	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	protected IChemObjectBuilder builder;
	public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }
	public class RendererListner implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			System.out.println(e); 
			Point2D ptSrc = e.getPoint();
			IMolecule molecule = painter.getMolecule();
			
			//painter.getGraphics2D(),
			//Point2D ptDst = Java2DRenderer.getCoorFromScreen( ptSrc);
			Point2D ptDst = painter.renderer.getCoorFromScreen( ptSrc);
			System.out.println("Mouse click at " + ptSrc + " real world coordinates: " + ptDst);
			Java2DRenderer.showClosestAtomOrBond(molecule, ptDst);
			try {
				int width = 400, height = 400;

		      // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
		      // into integer pixels
			  System.out.println("\tstarting..\n");
			      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		      System.out.println("bi created\n");

		      Graphics2D ig2 = bi.createGraphics();
		      System.out.println("ig2 created\n");

		      
		      ig2.setColor(getBackground());
		      ig2.fillRect(0, 0, width, height);
     
		      Renderer2DModel model = painter.getModel();
		      IJava2DRenderer renderer = painter.getRenderer();
		      
		      model.setZoomFactor(1);
		      System.out.println("setZoomeFActor done\n");
		      
		      Rectangle2D rectangle = new Rectangle2D.Double();
		      rectangle.setFrame(0, 0, width, height);
		      renderer.paintMolecule(molecule, ig2, rectangle);
		      System.out.println("renderer.paintMolecule done\n");

		      ImageIO.write(bi, "PNG", new File("c:\\tmp\\yourImageName.PNG"));
		      ImageIO.write(bi, "JPEG", new File("c:\\tmp\\yourImageName.JPG"));
		      ImageIO.write(bi, "gif", new File("c:\\tmp\\yourImageName.GIF"));
		      ImageIO.write(bi, "BMP", new File("c:\\tmp\\yourImageName.BMP"));
		      
		    } catch (IOException ie) {
		      ie.printStackTrace();
		    }
		}
		public void mouseEntered(MouseEvent e) { 	}
		public void mouseExited(MouseEvent e) { 	}
		public void mousePressed(MouseEvent e) { 	}
		public void mouseReleased(MouseEvent e) { 	}
	}
	
	private TestRenderer() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		painter = new SwingPainter();
		//painter.addMouseMotionListener(new TestRendererMouseE());
		//only react on mouse clicks for now
		painter.addMouseListener(new RendererListner());
		setUp();
		
		IMolecule mol;
		//mol = MoleculeFactory.makeAlphaPinene();
		//mol = MoleculeFactory.makeThiazole();
		//mol = MoleculeFactory.makeAlkane(5);
		mol = makeAlkanetest(5);
		//mol = makeMasstest();
		
		//mol = MoleculeFactory.makeBenzene();
		//mol = makeBenzene();
		//mol = makeWedgeTest();
		
		//mol = makeSWedgeTest();
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

//		frame.show();
		frame.setVisible(true);
		
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
		
		//IRenderer2D renderer = new Java2DRenderer(model);
		IJava2DRenderer renderer = new Java2DRenderer(model);

		private IMolecule molecule;
		
		Graphics2D graphic;
		
		public void setMolecule(IMolecule molecule) {
			this.molecule = molecule;
		}
		public IJava2DRenderer getRenderer() {
			return this.renderer;
		}
		public Renderer2DModel getModel() {
			return this.model;
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
			
	       // }
			super.paint(g);
			//System.out.println("Painting molecule..!");
			graphic = (Graphics2D)g;
			model.setZoomFactor(1);
			Color bg = model.getBackColor();
			g.setColor(bg);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			if (!affinelast.equals(graphic.getTransform())) {
				System.out.println("swing changed matrix to:" + graphic.getTransform());
				affinelast = graphic.getTransform();
			}

			renderer.paintMolecule(molecule, (Graphics2D)g, (Rectangle2D)getBounds());
		}
	}
	public static Molecule makeAlkanetest(int chainLength)
	  {
	    Molecule currentChain = new Molecule();

	    //Add the initial atom
	    IAtom atom = new Atom("C");
	    atom.setHydrogenCount(3);
	    currentChain.addAtom(atom);
	    
	    //Add further atoms and bonds as needed, a pair at a time.
	    int atomCount = 1;
	    for (; atomCount < chainLength - 1; atomCount++) {
	    	atom = new Atom("C");
	    	atom.setHydrogenCount(2);
	        currentChain.addAtom(atom);
	        currentChain.addBond(atomCount, atomCount - 1, 1);
	    }  
	    atom = new Atom("C");
    	atom.setHydrogenCount(3);
        currentChain.addAtom(atom);
        currentChain.addBond(atomCount, atomCount - 1, 1);
        
	    return currentChain;
	}
	public IMolecule makeSWedgeTest() {
		IMolecule mol = builder.newMolecule();
		IAtom atomC0 = new Atom("C");
	    atomC0.setID("C0"); atomC0.setHydrogenCount(0);
		

		IAtom atomO1 = new Atom("O");
		atomO1.setID("O1"); atomO1.setHydrogenCount(0);
		
		IAtom atomH0 = new Atom("H");
		atomH0.setID("H0"); atomH0.setHydrogenCount(0);
		IAtom atomH1 = new Atom("H");
		atomH1.setID("H1"); atomH1.setHydrogenCount(0);

	    IBond bondB1 = builder.newBond(atomC0, atomO1);
	    bondB1.setElectronCount(2);
	    IBond bondB2 = builder.newBond(atomC0, atomH0);
	    bondB2.setElectronCount(1);
    bondB2.setStereo(CDKConstants.STEREO_BOND_DOWN);

    IBond bondB3 = builder.newBond(atomC0, atomH1);
    bondB3.setElectronCount(1);
    bondB3.setStereo(CDKConstants.STEREO_BOND_UP);

		mol.addAtom(atomC0); 
		mol.addAtom(atomO1);
		mol.addAtom(atomH0);
		mol.addAtom(atomH1);
mol.addBond(bondB1);
 mol.addBond(bondB2);
 mol.addBond(bondB3);

	  return mol;	
	}
	public IMolecule makeMasstest() {
		IMolecule mol = builder.newMolecule();
		IAtom atomC0 = new Atom("C");
	    atomC0.setID("C0"); atomC0.setHydrogenCount(3);
		IAtom atomC1 = new Atom("C");
		atomC1.setID("C1"); atomC1.setHydrogenCount(2);
		
		IAtom atomS = new Atom("S");
		atomS.setID("S"); 
		//atomC1.setHydrogenCount(1);
		atomC0.setMassNumber(10);
		atomS.setMassNumber(4);
		
		atomC1.setMassNumber(26);
		
		atomC0.setFormalCharge(-2);
		atomS.setFormalCharge(2);
		atomC1.setFormalCharge(-1);

		IBond bondB0 = builder.newBond(atomC0, atomC1);
	    bondB0.setElectronCount(1);
	    IBond bondB1 = builder.newBond(atomC1, atomS);
	    bondB1.setElectronCount(1);

		mol.addAtom(atomC0); mol.addAtom(atomC1);
		mol.addAtom(atomS);
		mol.addBond(bondB0); 
		mol.addBond(bondB1); 

	  return mol;	
	}
	public IMolecule makeBenzene() {
		  IMolecule benzene = builder.newMolecule();

		  System.out.println("testing..");
		  IAtom atomC0 = new Atom("C");
		    atomC0.setID("C0"); atomC0.setHydrogenCount(1);
		  IAtom atomC1 = new Atom("C");
		    atomC1.setID("C1"); atomC1.setHydrogenCount(1);
		  IAtom atomC2 = new Atom("C");
		    atomC2.setID("C2"); atomC2.setHydrogenCount(1);
		  IAtom atomC3 = new Atom("C");
		    atomC3.setID("C3"); atomC3.setHydrogenCount(1);
		  IAtom atomC4 = new Atom("C"); 
		    atomC4.setID("C4"); atomC4.setHydrogenCount(1);
		  IAtom atomC5 = new Atom("C"); 
		    atomC5.setID("C5"); atomC5.setHydrogenCount(1);

		    atomC0.setFlag(CDKConstants.ISAROMATIC, true);
		    atomC1.setFlag(CDKConstants.ISAROMATIC, true);
		    atomC2.setFlag(CDKConstants.ISAROMATIC, true);
		    atomC3.setFlag(CDKConstants.ISAROMATIC, true);
		    atomC4.setFlag(CDKConstants.ISAROMATIC, true);
		    atomC5.setFlag(CDKConstants.ISAROMATIC, true);

		  IBond bondB0 = builder.newBond(atomC0, atomC1);
		    bondB0.setElectronCount(2);
		  IBond bondB1 = builder.newBond(atomC1, atomC2);
		    bondB1.setElectronCount(2);
		  IBond bondB2 = builder.newBond(atomC2, atomC3);
		    bondB2.setElectronCount(2);
		  IBond bondB3 = builder.newBond(atomC3, atomC4);
		    bondB3.setElectronCount(2);
		  IBond bondB4 = builder.newBond(atomC4, atomC5);
		    bondB4.setElectronCount(2);
		  IBond bondB5 = builder.newBond(atomC0, atomC5);
		    bondB5.setElectronCount(2);

		  IBond bondingSystem = builder.newBond();
		    bondingSystem.setElectronCount(6);
		    bondingSystem.setAtoms(
		      new IAtom[] { atomC0, atomC1, atomC2, 
		                    atomC3, atomC4, atomC5}
		    );

		  benzene.addAtom(atomC0); benzene.addAtom(atomC1);
		  benzene.addAtom(atomC2); benzene.addAtom(atomC3);
		  benzene.addAtom(atomC4); benzene.addAtom(atomC5);

		  benzene.addBond(bondB0); benzene.addBond(bondB1);
		  benzene.addBond(bondB2); benzene.addBond(bondB3);
		  benzene.addBond(bondB4); benzene.addBond(bondB5);
		  benzene.addBond(bondingSystem);

		  return benzene;
		}
}

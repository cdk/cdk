/* $Revision: 7636 $ $Author: nielsout $ $Date: 2007-09-02 11:46:10 +0100 (su, 02 sep 2007) $
 * 
 * Copyright (C) 2007  Niels Out <nielsout@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net or nout@science.uva.nl
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.renderer.progz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.controller.ExampleController2DModule;
import org.openscience.cdk.controller.SwingEventRelay;
import org.openscience.cdk.controller.SwingMouseEventRelay;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.Molecule;

/**
 * Test class for testing the new Java2DRenderer and Controller2DHub.
 * 
 * @author nielsout
 * @author egonw
 * @cdk.module jchempaint
 * @cdk.svnrev  $Revision: 9162 $
 */
public class TestEditor extends JPanel {

	private static final long serialVersionUID = -4728755515648290149L;

	private JFrame frame;
	private SwingPainter painter = new SwingPainter();
	private StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	protected IChemObjectBuilder builder;
	private Controller2DHub hub;

	public Controller2DHub get2DHub() {
		return hub;
	}
	private JComponent lastActionButton;
	public JComponent getActionButton() {
		return lastActionButton;
	}
	public void setActionButton(JComponent actionButton) {
		lastActionButton = actionButton;
	}
	private TestEditor() {
		builder = DefaultChemObjectBuilder.getInstance();
		
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setName("'frame'");
		
		
	//	sdg.setMolecule(makeMasstest());
		sdg.setMolecule(makeSWedgeTest());

		try {
			sdg.generateCoordinates();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IMolecule mol = sdg.getMolecule();
		System.out.println("molecule: " + mol);

		painter = new SwingPainter();
		painter.setMolecule(mol);
		painter.setName("'painter'");
				
		SwingEventRelay eventRelay = new SwingEventRelay(painter);
		
		hub = new Controller2DHub(
			new Controller2DModel(), painter.getRenderer(),
			ChemModelManipulator.newChemModel(mol),
			eventRelay
		);
		hub.registerGeneralControllerModule(
			new ExampleController2DModule()
		);
		SwingMouseEventRelay relay = new SwingMouseEventRelay(hub); 
		painter.addMouseListener(relay);
		painter.addMouseMotionListener(relay);

		JToolBar toolbar = SomeToolBar.getToolbar(this, 1);
	//	frame.add(toolbar);
		
		frame.add(toolbar, BorderLayout.NORTH);
       // frame.revalidate();
        
		frame.add(painter);

		painter.setBackground(Color.WHITE);


	}

	private void run() {
		frame.setSize(800, 400);
		frame.setVisible(true);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestEditor prog = new TestEditor();
		prog.setName("'the TestEditor'");
		prog.run();
	}

	public class SwingPainter extends JComponent {
		private static final long serialVersionUID = 2;
		
		Renderer2DModel model = new Renderer2DModel();
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
		public void update(Graphics g) {
			System.out.println("!!! Repainting molecule!!!");
			
		}
		public void update() {
			System.out.println("!!! Repainting molecule!!!");
			
		}
		private double prevZoom;
		private int windowH;
		private int windowW;

		public void paint(Graphics g) {
			//System.out.println("Painting molecule..!");
			super.paint(g);
			model.setZoomFactor(1);
			model.setShowAromaticity(true);
			
			//IChemModel imol = hub.getIChemModel();
			//FIXME: make sure to draw all molecules if imol has more then one..
			//molecule = imol.getMoleculeSet().getMolecule(0);
			
			graphic = (Graphics2D)g;
			Color bg = model.getBackColor();
			g.setColor(bg);
			g.fillRect(0, 0, getWidth(), getHeight());

			if (!affinelast.equals(graphic.getTransform())) {
				System.out.println("swing changed matrix to:" + graphic.getTransform());
				affinelast = graphic.getTransform();
			}
						
			if (prevZoom != model.getZoomFactor() || windowH != getSize().height || windowW != getSize().width )
			{
				//new window / size changed
				renderer.paintMolecule(molecule, (Graphics2D)g, (Rectangle2D)getBounds());
				windowH = getSize().height;
				windowW = getSize().width;
				prevZoom = model.getZoomFactor();
			}
			else {	//same window (repaint triggered)
				renderer.paintMolecule(molecule, graphic);
			}
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
			currentChain.addBond(atomCount, atomCount - 1, IBond.Order.SINGLE);
		}  
		atom = new Atom("C");
		atom.setHydrogenCount(3);
		currentChain.addAtom(atom);
		currentChain.addBond(atomCount, atomCount - 1, IBond.Order.SINGLE);

		return currentChain;
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
	    bondB1.setOrder(IBond.Order.DOUBLE);
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
}

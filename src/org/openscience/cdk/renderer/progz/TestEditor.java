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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.controller.Controller2DHub;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * Test class for testing the new Java2DRenderer and Controller2DHub.
 * 
 * @author nielsout
 * @author egonw
 */
public class TestEditor extends JPanel {

	private static final long serialVersionUID = -4728755515648290149L;

	JFrame frame;
	SwingPainter painter = new SwingPainter();
	StructureDiagramGenerator sdg = new StructureDiagramGenerator();
	protected IChemObjectBuilder builder;
	Controller2DHub hub;

	public void setUp() {
		builder = DefaultChemObjectBuilder.getInstance();
	}

	private TestEditor() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		hub = new Controller2DHub();
		
		painter = new SwingPainter();
		SwingMouseEventRelay relay = new SwingMouseEventRelay(hub); 
		painter.addMouseListener(relay);
		painter.addMouseMotionListener(relay);

		setUp();

		IMolecule mol;
		mol = makeMasstest();

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
		frame.setVisible(true);		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestEditor prog = new TestEditor();
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
		public void paint(Graphics g) {
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
}

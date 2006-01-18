/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
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
 *  */
package org.openscience.cdk.renderer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.vecmath.Matrix4d;

import org.openscience.cdk.AtomContainer;

/**
 * Three dimensional renderer which depicts atoms as cartoonlike
 * atoms. No shading, no other fancy stuff.
 *
 * <p>This code is based on code from the Jmol Project.
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword viewer, 3D-viewer
 * @cdk.require swing
 *
 */
public class Renderer3D extends JPanel implements Runnable {

    private org.openscience.cdk.tools.LoggingTool logger;

    private static Matrix4d rotation_matrix = new Matrix4d();

    public Renderer3DModel drawing_properties;

    private boolean painting = false;

    // variables to do with rotation
    private static int x_where_mouse_was_pressed, y_where_mouse_was_pressed;

    public Renderer3D() {
        this(new Renderer3DModel());
    }

    /**
     * Constructs a new Renderer3D using the model properties given by
     * the Renderer3DModel.
     *
     * @param drawing_properties     drawing properties to be used
     */
    public Renderer3D(Renderer3DModel drawing_properties) {
        logger = new org.openscience.cdk.tools.LoggingTool(this);
        this.drawing_properties = drawing_properties;

        // initialize stuff
        rotation_matrix.setIdentity();
    }

    public void paintMolecule(AtomContainer atomCon, Graphics g) {
        if (!painting) {
            logger.debug("Painting...");
            painting = true;

            logger.debug("Painting molecule");
            paintBonds(atomCon, g);
            paintAtoms(atomCon, g);

            painting = false;
        } else {
            logger.debug("Skipping painting...");
        }
        logger.debug("done");
    }

    public void paintAtoms(AtomContainer atomCon, Graphics g) {
        // loop over all atoms in the model
    	org.openscience.cdk.interfaces.IAtom[] atoms = atomCon.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            logger.debug("Paint atom " + i);
            org.openscience.cdk.interfaces.IAtom atom = atoms[i];
            logger.debug(atom.toString());
            // the size of the circle
            // FIXME: this should be determined by Renderer3DModel !
            int atomRadius = 20;
            // get color for this atom
            g.setColor(drawing_properties.getAtomColor(atom));
            // draw atom as filled circle
            g.fillOval((int)(atom.getX3d() - (atomRadius / 2.0))*3,
                       (int)(atom.getY3d() - (atomRadius / 2.0))*3,
                       atomRadius, atomRadius);
            // draw black line around atom
            g.setColor(Color.black);
            g.drawOval((int)(atom.getX3d() - (atomRadius / 2.0))*3,
                       (int)(atom.getY3d() - (atomRadius / 2.0))*3,
                       atomRadius, atomRadius);
        }
    }

    private void paintBonds(AtomContainer atomCon, Graphics g) {
        // FIXME: have bonds drawn
    }

    public Renderer3DModel getRenderer3DModel() {
        return this.drawing_properties;
    }

    /**
     * Sets the new model properties
     *
     * @param drawing_properties      new properties for model drawing
     */
    public void setRenderer3DModel(Renderer3DModel drawing_properties) {
        this.drawing_properties = drawing_properties;
    }

    /** Functions to implement for Runnable interface **/
    public void start() {
        this.addMouseListener(new Renderer3DMouseAdapter());
        this.addMouseMotionListener(new Renderer3DMouseMotionAdapter());
    };

    public void run() {
        try {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // repaint();
    };

    public void stop() {};

    /** Classes that react on mouse operations **/

    class Renderer3DMouseAdapter extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            x_where_mouse_was_pressed = e.getX();
            y_where_mouse_was_pressed = e.getY();
        };
        public void mouseClicked(MouseEvent e) {};
        public void mouseReleased(MouseEvent e) {};
    }

    class Renderer3DMouseMotionAdapter extends MouseMotionAdapter {

        // mouse dragging is rotation
        public void mouseDragged(MouseEvent event) {

            // determine how much molecule must be rotated
            int x = event.getX(); int y = event.getY();
            double xtheta = (y - y_where_mouse_was_pressed) *
                            (2.0 * Math.PI / getSize().width);
            double ytheta = (x - x_where_mouse_was_pressed) *
                            (2.0 * Math.PI / getSize().height);

            // make rotation matrix
            Matrix4d matrix = new Matrix4d();
            // do rotation in two steps, first xtheta part, then ytheta part
            matrix.rotX(xtheta);
            rotation_matrix.mul(matrix, rotation_matrix);
            matrix.rotY(ytheta);
            rotation_matrix.mul(matrix, rotation_matrix);
        }
    }

}

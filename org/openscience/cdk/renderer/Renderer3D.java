/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.renderer;

import org.openscience.cdk.renderer.color.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.*;
import javax.vecmath.*;
import java.util.*;
import java.awt.*;

public class Renderer3D {

    private org.openscience.cdk.tools.LoggingTool logger;

    public Renderer3DModel r3dm;

    public Renderer3D() {
        this(new Renderer3DModel());
    }

    public Renderer3D(Renderer3DModel r3dm) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        this.r3dm = r3dm;
    }

    public void paintMolecule(AtomContainer atomCon, Graphics g) {
        logger.debug("Painting molecule");
        paintBonds(atomCon, g);
        paintAtoms(atomCon, g);
    }

    public void paintAtoms(AtomContainer atomCon, Graphics g) {
        Atom[] atoms = atomCon.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            logger.debug("Paint atom " + i);
            Atom atom = atoms[i];
            int atomRadius = 20;
            g.setColor(r3dm.getAtomColor(atom));
            g.fillOval((int)atom.getX2D() - (atomRadius / 2),
                       (int)atom.getY2D() - (atomRadius / 2),
                       atomRadius, atomRadius);
            g.setColor(Color.black);
            g.drawOval((int)atom.getX2D() - (atomRadius / 2),
                       (int)atom.getY2D() - (atomRadius / 2),
                       atomRadius, atomRadius);
        }
    }

    private void paintBonds(AtomContainer atomCon, Graphics g) {
    }

    public Renderer3DModel getRenderer3DModel() {
        return this.r3dm;
    }

    public void setRenderer3DModel(Renderer3DModel r3dm) {
        this.r3dm = r3dm;
    }
}

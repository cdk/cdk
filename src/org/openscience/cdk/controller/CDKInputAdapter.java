/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.controller;


import java.awt.event.*;
import org.openscience.cdk.controller.JCPController2D;
import org.openscience.cdk.renderer.*;
import org.openscience.cdk.*;
import java.awt.*;

/**
 * Class that acts on MouseEvents.
 */
public class CDKInputAdapter extends JCPController2D 
    implements MouseMotionListener, MouseListener, KeyListener {

    /**
     * Constructs a <code>CDKInputAdapter</code> that acts on the
     * specified AtomContainer.
     *
     * @param atomCon   AtomContainer to act on
     */
	public CDKInputAdapter(AtomContainer atomCon, Renderer2DModel r2dm,
                           JCPController2DModel c2dm) {
		super(atomCon, r2dm, c2dm);
	}

}

/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Jmol Development Team
 *
 *  Contact: jmol-developers@lists.sf.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.applet;

import java.awt.Dimension;

import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;

/**
 * The
 *
 * @cdk.module jchempaint.applet
 * @author     steinbeck
 */
public class JChemPaintEditorApplet extends JChemPaintAbstractApplet
{

    private static final long serialVersionUID = 6015686576993175260L;
    
    JChemPaintEditorPanel jcpep = null;
  
	/* (non-Javadoc)
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		JChemPaintEditorPanel jcpep = new JChemPaintEditorPanel(2, 
            new Dimension((int)this.getSize().getWidth()-100,
                          (int)this.getSize().getHeight()-100),
            "applet"
        );
		jcpep.setShowStatusBar(false);
		setTheJcpp(jcpep);
		super.init();
	}
	
	/* (non-Javadoc)
	 * @see java.applet.Applet#start()
	 */
	public void start() {
		super.start();
	}
	/* (non-Javadoc)
	 * @see java.applet.Applet#stop()
	 */
	public void stop() {
		super.stop();
	}
}

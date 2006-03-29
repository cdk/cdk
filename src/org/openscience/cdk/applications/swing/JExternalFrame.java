/*
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
package org.openscience.cdk.applications.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author dirk49
 *
 * This class allows to transfer an embedded or applet viewer or editor panel  
 * to an external frame. This frame can be resized.
 */

public class JExternalFrame extends JFrame {

	private Component theComponent = null;
	private Container theParent = null;
	private JPanel dummyPanel = null;
	private boolean initialized = false;
	private Dimension embeddedSize = null;

	/**
	 * @return Returns the dummyPanel.
	 */
	private JPanel getDummyPanel() {
		if (dummyPanel == null)
			dummyPanel = new JPanel();
		return dummyPanel;
	}
	
	/**
	 * @param comp Component that is transfered to the external frame
	 */
	public void show(Component comp) {
		int deltaW = 0;
		int deltaH = 0;
		int deltaX = 0;
		int deltaY = 0;
		Point embeddedScreenLocation = null;
	
		theComponent = comp;
		if (comp == null)
			return;
		theParent = comp.getParent();
		if (theParent == null)
			return;
		
		if (!initialized) {
			embeddedScreenLocation = new Point(theComponent.getLocationOnScreen());
			embeddedSize = theComponent.getSize(embeddedSize);
			getContentPane().setLayout(new BorderLayout());
			this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			this.setSize(200,150);
		}

		super.show();

		if (!initialized) {
			this.validate();
			this.repaint();
			deltaW = this.getWidth() - getContentPane().getWidth();
			deltaH = this.getHeight() - getContentPane().getHeight();
			deltaX = embeddedScreenLocation.x - getContentPane().getLocationOnScreen().x;
			deltaY = embeddedScreenLocation.y - getContentPane().getLocationOnScreen().y;
		}
		
		theParent.remove(theComponent);
		theParent.add(getDummyPanel());
		theParent.validate();
		theParent.repaint();
				
		if (!initialized) {
			this.setBounds(this.getLocationOnScreen().x + deltaX, + this.getLocationOnScreen().y + deltaY, 
				embeddedSize.width + deltaW, embeddedSize.height + deltaH);
		}
		
		getContentPane().add(theComponent, BorderLayout.CENTER);
		initialized = true;
		this.validate();
		this.toFront();
		this.repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.Window#dispose()
	 */
	public void dispose() {
    try{
		theParent.remove(getDummyPanel());
		this.getContentPane().remove(theComponent);
		theComponent.setSize(embeddedSize);
		theParent.add(theComponent, BorderLayout.CENTER);
		super.dispose();
		theParent.validate();
		theParent.repaint();
    }catch(Exception ex){
      //FIXME npe in applet here - needs a true fix
    }
	}
}

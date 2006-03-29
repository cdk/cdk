/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sf.net
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
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.openscience.cdk.applications.jchempaint.JCPPropertyHandler;
import org.openscience.cdk.applications.jchempaint.action.JCPAction;
import org.openscience.cdk.tools.LoggingTool;


/**
 * Simple Dialog that shows the JCP logo and a textfield that allows
 * the user to copy&amp;paste the URL of JChemPaints main site.
 *
 * @author        hel
 * @cdk.created       27. April 2005
 * @cdk.module    jchempaint
 */
public class AboutDialog extends JFrame {

	/**  Description of the Field */
	protected static LoggingTool logger = null;

	/** Displays the About Dialog for JChemPaint.  */
	public AboutDialog() {
		super("About JChemPaint");
		logger = new LoggingTool(this);
		doInit();
	}

	/**  Description of the Method */
	public void doInit() {
		Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
		String version = self.getImplementationVersion();
		String s1 = "JChemPaint " + version + "\n";
		s1 += "An open-source editor for 2D chemical structures.";
		String s2 = "An OpenScience project by Christoph Steinbeck, Egon Willighagen and others.\n";
		s2 += "See 'http://jchempaint.sourceforge.net' for more information.";

		getContentPane().setLayout(new BorderLayout());
		getContentPane().setBackground(Color.white);

		JLabel label1 = new JLabel();

		try {
			JCPPropertyHandler jcpph = JCPPropertyHandler.getInstance();
			URL url = jcpph.getResource("jcplogo" + JCPAction.imageSuffix);
			ImageIcon icon = new ImageIcon(url);
			//ImageIcon icon = new ImageIcon(../resources/);
			label1 = new JLabel(icon);
		} catch (Exception exception) {
			logger.error("Cannot add JCP logo: " + exception.getMessage());
			logger.debug(exception);
		}
		label1.setBackground(Color.white);

		Border lb = BorderFactory.createLineBorder(Color.white, 5);
		JTextArea jtf1 = new JTextArea(s1);
		jtf1.setBorder(lb);
		jtf1.setEditable(false);
		JTextArea jtf2 = new JTextArea(s2);
		jtf2.setEditable(false);
		jtf2.setBorder(lb);
		setTitle("About JChemPaint");
		getContentPane().add("Center", label1);
		getContentPane().add("North", jtf1);
		getContentPane().add("South", jtf2);
		pack();
		setVisible(true);
	}
}


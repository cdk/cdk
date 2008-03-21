/* $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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

import javax.swing.JFrame;

import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.config.AtomTypeFactory;

/**
 *  Checks the functionality of the PeriodicTablePanelTest
 *
 *@author        Steinbeck
 *@cdk.created       February 10, 2004
 *@cdk.module    test-extra
 */
public class PeriodicTablePanelTest extends NewCDKTestCase {

	AtomTypeFactory atf = null;

	public void runVisual()
	{
		PeriodicTablePanel ptp = new PeriodicTablePanel();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(ptp);
		frame.pack();
		frame.setVisible(true);
	}

}


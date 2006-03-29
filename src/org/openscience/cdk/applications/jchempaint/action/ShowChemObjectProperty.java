/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;


/**
 *  Shows the contect of a specified property (i.e. as defined in the type of
 *  this JCPAction).
 *
 * @cdk.module  jchempaint
 * @author      steinbeck
 * @cdk.require java1.5
 */
public class ShowChemObjectProperty extends JCPAction
{

	TextViewDialog dialog = null;


	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		IChemObject object = getSource(event);
		// XXX needs fixing JFrame frame = (JFrame) jcpPanel.getFrame();
		JFrame frame = new JFrame();
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame,
					"IChemObject Property " + type,
					new Dimension(400, 100)
					);
		}

		Object prop = object.getProperty(type);
		if (prop == null)
		{
			dialog.setText("There is no " + type + " property");
		} else if (prop instanceof String)
		{
			dialog.setText((String) prop);
		} else
		{
			dialog.setText("The property " + type + " is not a String object");
		}

		dialog.show();
	}

}


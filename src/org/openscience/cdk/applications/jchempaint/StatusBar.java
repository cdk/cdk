/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * JChemPaints status bar
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class StatusBar extends JPanel
{
	JLabel[] status = new JLabel[3];


	/**
	 *  Constructor for the StatusBar object
	 */
	public StatusBar()
	{
		super();

		setLayout(new GridLayout(1, 3));
		setPreferredSize(new Dimension(660, 30));
		for (int i = 0; i <= 2; i++)
		{
			status[i] = new JLabel();
			status[i].setPreferredSize(new Dimension(220, 30));
			status[i].setBorder(BorderFactory.createEtchedBorder());
			status[i].setHorizontalAlignment(JLabel.CENTER);
			add(status[i]);
		}
	}


	/**
	 *  Sets the status attribute of the StatusBar object
	 *
	 *@param  label  The new status value
	 *@param  text   The new status value
	 */
	public void setStatus(int label, String text)
	{
		status[label - 1].setText(text);
	}


	/**
	 *  Gets the status attribute of the StatusBar object
	 *
	 *@param  label  Description of the Parameter
	 *@return        The status value
	 */
	public String getStatus(int label)
	{
		return status[label - 1].getText();
	}

}


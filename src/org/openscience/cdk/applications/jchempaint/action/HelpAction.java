/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
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

import org.openscience.cdk.applications.jchempaint.dialogs.HelpDialog;

import java.awt.event.ActionEvent;

/**
 * Pops up the help.
 *
 * @cdk.module  jchempaint
 * @author      steinbeck
 * @cdk.require javahelp
 */
public class HelpAction extends JCPAction
{

	private static final long serialVersionUID = -9213900779679488824L;

	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
		if (type.equals("tutorial"))
		{
			new HelpDialog(null, "org/openscience/cdk/applications/jchempaint/resources/userhelp_jcp/contain/tutorial.html").setVisible(true);
		} else
		{
			new HelpDialog(null, "org/openscience/cdk/applications/jchempaint/resources/userhelp_jcp/jcp.html").setVisible(true);
		}
	}
}


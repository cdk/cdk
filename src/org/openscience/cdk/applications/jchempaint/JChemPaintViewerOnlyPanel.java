/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2005  The JChemPaint project
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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint;

import javax.swing.event.*;
import org.openscience.cdk.tools.*;

/**
 *  JPanel that contains a a viewer only JChemPaint panel.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class JChemPaintViewerOnlyPanel extends JChemPaintPanel
{

	private LoggingTool logger;


	/**
	 *  Constructor for the JChemPaintViewerOnlyPanel object
	 */
	public JChemPaintViewerOnlyPanel()
	{
		super();
		logger = new LoggingTool(this);
	}


	/**
	 *  Mandatory because JChemPaint is a ChangeListener. Used by other classes to
	 *  update the information in one of the three statusbar fields.
	 *
	 *@param  event  ChangeEvent
	 */
	public void stateChanged(ChangeEvent event)
	{
	}
}



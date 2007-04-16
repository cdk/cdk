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

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.ModifyRenderOptionsDialog;
import org.openscience.cdk.renderer.Renderer2DModel;

import java.awt.event.ActionEvent;


/**
 * Shows a dialog for editing the Display settings
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class ModifyDisplaySettingsAction extends JCPAction
{

    private static final long serialVersionUID = 374787381482528088L;

	public void actionPerformed(ActionEvent e)
	{
		logger.debug("Modify display settings in mode");
		JChemPaintModel jcpm = jcpPanel.getJChemPaintModel();
		Renderer2DModel renderModel = jcpm.getRendererModel();
		ModifyRenderOptionsDialog frame =
				new ModifyRenderOptionsDialog(jcpm, renderModel);
		frame.setVisible(true);
		jcpm.fireChange();
	}

}


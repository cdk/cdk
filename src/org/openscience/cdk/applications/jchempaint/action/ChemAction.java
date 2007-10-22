/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2007  The JChemPaint project
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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.PTDialog;
import org.openscience.cdk.applications.swing.PeriodicTablePanel;
import org.openscience.cdk.controller.Controller2DModel;
import org.openscience.cdk.event.ICDKChangeListener;


/**
 * Changes the editing mode for the Controller2D in CDK.
 * @cdk.module jchempaint
 * @cdk.svnrev  $Revision$
 * @author     steinbeck
 */
public class ChemAction extends JCPAction
{

	private static final long serialVersionUID = -399343341314165265L;
	private PTDialog dialog = null;

	public void actionPerformed(ActionEvent e)
	{
		((JButton)jcpPanel.lastAction.get(0)).setBackground(Color.LIGHT_GRAY);
		((JComponent) e.getSource()).setBackground(Color.GRAY);
		jcpPanel.lastAction.set(0,(JComponent) e.getSource());
		logger.debug("ChemAction performed!");
		JChemPaintModel jcpm;
		Controller2DModel c2dm;
		String s = e.getActionCommand();
		String type = s.substring(s.indexOf("@") + 1);
		logger.info("  type  ", type);
		logger.debug("  source ", e.getSource());
		jcpm = jcpPanel.getJChemPaintModel();
		if (jcpm != null)
		{
			c2dm = jcpm.getControllerModel();
			if (type.equals("bond"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.DRAWBOND);
				c2dm.setDrawElement("C");
			} else if (type.equals("select"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.SELECT);
			} else if (type.equals("move"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.MOVE);
			} else if (type.equals("select"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.SELECT);
			} else if (type.equals("eraser"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.ERASER);
			} else if (type.equals("element"))
			{
				if (dialog == null)
				{
					// open PeriodicTable panel
					dialog = new PTDialog(
							new PTDialogChangeListener(c2dm)
							);
				}
				dialog.pack();
				dialog.setVisible(true);
				c2dm.setDrawMode(Controller2DModel.DrawMode.ELEMENT);
			} else if (type.equals("symbol"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.SYMBOL);
			} else if (type.equals("triangle"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(3);
				c2dm.setDrawElement("C");
			} else if (type.equals("square"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(4);
				c2dm.setDrawElement("C");
			} else if (type.equals("pentagon"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(5);
				c2dm.setDrawElement("C");
			} else if (type.equals("hexagon"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(6);
				c2dm.setDrawElement("C");
			} else if (type.equals("heptagon"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(7);
				c2dm.setDrawElement("C");
			} else if (type.equals("octagon"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.RING);
				c2dm.setRingSize(8);
				c2dm.setDrawElement("C");
			} else if (type.equals("benzene"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.BENZENERING);
				c2dm.setRingSize(6);
				c2dm.setDrawElement("C");
			} else if (type.equals("cleanup"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.CLEANUP);
			} else if (type.equals("flip_H"))
			{
				// not implemented
				c2dm.setDrawMode(Controller2DModel.DrawMode.FLIP_H);
			} else if (type.equals("flip_V"))
			{
				// not implemented
				c2dm.setDrawMode(Controller2DModel.DrawMode.FLIP_V);
			} else if (type.equals("rotation"))
			{
				// not implemented
				c2dm.setDrawMode(Controller2DModel.DrawMode.ROTATION);
			} else if (type.equals("up_bond"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.UP_BOND);
				c2dm.setDrawElement("C");
			} else if (type.equals("down_bond"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.DOWN_BOND);
				c2dm.setDrawElement("C");
			} else if (type.equals("normalize"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.NORMALIZE);
			} else if (type.equals("plus"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.INCCHARGE);
			} else if (type.equals("minus"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.DECCHARGE);
			} else if (type.equals("lasso"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.LASSO);
			} else if (type.equals("map"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.MAPATOMATOM);
			} else if (type.equals("enterelement"))
			{
				c2dm.setDrawMode(Controller2DModel.DrawMode.ENTERELEMENT);
			}
		}
		jcpPanel.stateChanged(new ChangeEvent(this));
		if (jcpm != null)
		{
			jcpm.fireChange();
		}
	}


	class PTDialogChangeListener implements ICDKChangeListener
	{

		Controller2DModel model;


		/**
		 *  Constructor for the PTDialogChangeListener object
		 *
		 *@param  model  Description of the Parameter
		 */
		public PTDialogChangeListener(Controller2DModel model)
		{
			this.model = model;
		}

		public void stateChanged(EventObject event)
		{
			logger.debug("Element change signaled...");
			if (event.getSource() instanceof PeriodicTablePanel)
			{
				PeriodicTablePanel source = (PeriodicTablePanel) event.getSource();
				String symbol = source.getSelectedElement().getSymbol();
				logger.debug("Setting drawing element to: ", symbol);
				model.setDrawElement(symbol);
				dialog.setVisible(false);
				dialog = null;
			} else
			{
				logger.warn("Unkown source for event: ", event.getSource().getClass().getName());
			}
		}
	}

}


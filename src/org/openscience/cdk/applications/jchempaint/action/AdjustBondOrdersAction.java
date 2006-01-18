/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The JChemPaint project
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
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.applications.undoredo.AdjustBondOrdersEdit;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Triggers the adjustment of BondOrders
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 */
public class AdjustBondOrdersAction extends JCPAction
{

	/**
	 *  Description of the Method
	 *
	 *@param  e  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent e)
	{
        HashMap changedBonds = null;
        ChemModel model = (ChemModel) jcpPanel.getJChemPaintModel().getChemModel();
		logger.debug("Adjusting bondorders: ", type);
		if (type.equals("clear"))
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
                changedBonds = new HashMap();
				IAtomContainer[] containers = ChemModelManipulator.getAllAtomContainers(model);
               for (int i = 0; i < containers.length; i++)
				{
                    IAtomContainer containerCopy = (IAtomContainer) containers[i].clone();
					satChecker.unsaturate(containers[i].getBonds());
                     for (int j=0; j<containerCopy.getBondCount(); j++) {
                    	 org.openscience.cdk.interfaces.Bond bondCopy = containerCopy.getBondAt(j);
                    	 org.openscience.cdk.interfaces.Bond bond = containers[i].getBondAt(j);
                            if (bond.getOrder() != bondCopy.getOrder()) {
                                double[] bondOrders = new double[2];
                                bondOrders[0] = bond.getOrder();
                                bondOrders[1] = bondCopy.getOrder();
                                changedBonds.put(bond, bondOrders);
                            }
                        }
				}
				jcpPanel.getJChemPaintModel().fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		} else
		{
			try
			{
				SaturationChecker satChecker = new SaturationChecker();
                changedBonds = new HashMap();
				IAtomContainer[] containers = ChemModelManipulator.getAllAtomContainers(model);
               for (int i = 0; i < containers.length; i++)
				{
                   IAtomContainer containerCopy = (IAtomContainer) containers[i].clone();
					satChecker.saturate(containers[i]);
                    for (int j=0; j<containerCopy.getBondCount(); j++) {
                    	org.openscience.cdk.interfaces.Bond bondCopy = containerCopy.getBondAt(j);
                    	org.openscience.cdk.interfaces.Bond bond = containers[i].getBondAt(j);
                        if (bond.getOrder() != bondCopy.getOrder()) {
                            double[] bondOrders = new double[2];
                            bondOrders[0] = bond.getOrder();
                            bondOrders[1] = bondCopy.getOrder();
                            changedBonds.put(bond, bondOrders);
                        }
                    }
				}
				jcpPanel.getJChemPaintModel().fireChange();
			} catch (Exception exc)
			{
				String error = "Could not adjust bondorders.";
				logger.error(error);
				logger.debug(exc);
				JOptionPane.showMessageDialog(jcpPanel, error);
			}
		}
        UndoableEdit  edit = new AdjustBondOrdersEdit(changedBonds);
        jcpPanel.getUndoSupport().postEdit(edit);
	}
}


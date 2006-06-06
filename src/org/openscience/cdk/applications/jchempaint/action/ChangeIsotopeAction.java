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

import java.awt.event.ActionEvent;

import javax.swing.undo.UndoableEdit;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.ChangeIsotopeEdit;
import org.openscience.cdk.config.IsotopeFactory;


/**
 * Changes the isotope for a selected atom
 *
 * @cdk.module jchempaint
 * @author     Egon Willighagen
 */
public class ChangeIsotopeAction extends JCPAction
{

	private static final long serialVersionUID = -4692219842740123315L;

	/**
	 *  Description of the Method
	 *
	 *@param  event  Description of the Parameter
	 */
	public void actionPerformed(ActionEvent event)
	{
		logger.debug("About to change atom type of relevant atom!");
		JChemPaintModel jcpm = jcpPanel.getJChemPaintModel();
		if (jcpm != null)
		{
			IChemObject object = getSource(event);
			logger.debug("Source of call: ", object);
			if (object instanceof Atom)
			{
				Atom atom = (Atom) object;
				int isotopeNumber = 0;
                 int formerIsotopeNumber = 0;
				try
				{
					IIsotope isotope = IsotopeFactory.getInstance(atom.getBuilder()).
							getMajorIsotope(atom.getSymbol());
					isotopeNumber = isotope.getMassNumber();
                    formerIsotopeNumber = isotopeNumber;
				} catch (Exception exception)
				{
					logger.error("Error while configuring atom");
					logger.debug(exception);
				}
				// adapt for menu chosen
				if (type.equals("major"))
				{
					// that's the default
				} else if (type.equals("majorPlusOne"))
				{
					isotopeNumber++;
				} else if (type.equals("majorPlusTwo"))
				{
					isotopeNumber++;
					isotopeNumber++;
				} else if (type.equals("majorPlusThree"))
				{
					isotopeNumber++;
					isotopeNumber++;
					isotopeNumber++;
				} else if (type.equals("majorMinusOne"))
				{
					isotopeNumber--;
				} else if (type.equals("majorMinusTwo"))
				{
					isotopeNumber--;
					isotopeNumber--;
				} else if (type.equals("majorMinusThree"))
				{
					isotopeNumber--;
					isotopeNumber--;
					isotopeNumber--;
				}
				atom.setMassNumber(isotopeNumber);
                UndoableEdit  edit = new ChangeIsotopeEdit(atom, formerIsotopeNumber, isotopeNumber);
                jcpPanel.getUndoSupport().postEdit(edit);
				jcpm.fireChange();
			}
		}
	}

}


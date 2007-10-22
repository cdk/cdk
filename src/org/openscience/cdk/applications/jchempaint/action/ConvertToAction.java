/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2007  The JChemPaint project
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

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;


/**
 * Triggers the conversion of an Atom to a residueLocator 
 * or other objects
 *
 * @cdk.module jchempaint
 * @cdk.svnrev  $Revision$
 * @author     steinbeck
 */
public class ConvertToAction extends JCPAction
{

	private static final long serialVersionUID = 4239327873015560552L;

	public void actionPerformed(ActionEvent event)
	{
		logger.debug("Converting to: ", type);
		IChemObject object = getSource(event);
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		org.openscience.cdk.interfaces.IChemModel model = jcpmodel.getChemModel();
		if (object != null)
		{
			if (object instanceof Atom)
			{
				if (type.equals("atomToPseudoAtom"))
				{
					Atom atom = (Atom) object;
					IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
					AtomContainerManipulator.replaceAtomByAtom(relevantContainer,
							atom, new PseudoAtom(atom));
				} else if (type.equals("atomToEnzymeResidueLocator"))
				{
					Atom atom = (Atom) object;
					IAtomContainer relevantContainer = ChemModelManipulator.getRelevantAtomContainer(model, atom);
					AtomContainerManipulator.replaceAtomByAtom(relevantContainer,
							atom, new EnzymeResidueLocator(atom));
				} else
				{
					logger.error("Unknown convertTo type!");
				}
			} else
			{
				logger.error("Object not an Atom! Cannot convert into a PseudoAtom!");
			}
		} else
		{
			logger.warn("Cannot convert a null object!");
		}
		jcpmodel.fireChange();
	}
}


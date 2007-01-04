/*  $Revision: 7032 $ $Author: kaihartmann $ $Date: 2006-09-22 17:26:48 +0200 (Fri, 22 Sep 2006) $
 *  
 *  Copyright (C) 2006-2007  Egon Willighagen <ewilligh@uni-koeln.de>
 *
 *  Contact: cdk-devel@lists.sf.net
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
import java.util.Iterator;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.jchempaint.dialogs.ValidateFrame;
import org.openscience.cdk.atomtype.IAtomTypeMatcher;
import org.openscience.cdk.atomtype.MM2AtomTypeMatcher;
import org.openscience.cdk.atomtype.MMFF94AtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * An action that triggers atom type perception by the CDK.
 * 
 * @cdk.module jchempaint
 * @author     E.L. Willighagen <ewilligh@uni-koeln.de>
 */
public class PerceiveAtomTypesAction extends JCPAction
{

    private static final long serialVersionUID = -3776589605934024224L;
    
    ValidateFrame frame = null;

	public void actionPerformed(ActionEvent event) {
		logger.debug("detected force field type list: ", type);
		IAtomTypeMatcher matcher = null;
		if ("mm2".equals(type)) {
			matcher = new MM2AtomTypeMatcher();
		} else if ("mmff94".equals(type)) {
			matcher = new MMFF94AtomTypeMatcher();
		}
		if (matcher == null) {
			logger.warn("Not a known atom type list: " + type);
			return;
		}
		
		JChemPaintModel jcpmodel = jcpPanel.getJChemPaintModel();
		IChemModel model = jcpmodel.getChemModel();
		
		Iterator containers = ChemModelManipulator.getAllAtomContainers(model).iterator();
		while (containers.hasNext()) {
			IAtomContainer container = (IAtomContainer)containers.next();
			Iterator atoms = container.atoms();
			while (atoms.hasNext()) {
				IAtom atom = (IAtom)atoms.next();
				try {
					atom.setAtomTypeName(matcher.findMatchingAtomType(container, atom).getAtomTypeName());
				} catch (CDKException e) {
					logger.error("Could not find atom type name for atom!", e);
					logger.debug(e);
				}
			}
		}
	}

}


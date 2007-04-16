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

import nu.xom.Element;
import org.openscience.cdk.applications.jchempaint.dialogs.TextViewDialog;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.libio.cml.Convertor;
import org.xmlcml.cml.element.CMLCml;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Allow to dump a IChemObject to a newly opened screen
 *
 * @cdk.module       jchempaint
 * @author           steinbeck
 * @cdk.require      java1.5+
 * @cdk.builddepends xom-1.0.jar
 */
public class ShowChemObjectDumpAction extends JCPAction
{

    private static final long serialVersionUID = 3112715648384115740L;
    
    TextViewDialog dialog = null;

	public void actionPerformed(ActionEvent event)
	{
		IChemObject object = getSource(event);
		// XXX needs fixing
		// JFrame frame = (JFrame) jcpPanel.getFrame();
		JFrame frame = new JFrame();
		if (dialog == null)
		{
			dialog = new TextViewDialog(frame, "IChemObject Dump", new Dimension(500, 300));
		}

		Convertor convertor = new Convertor(false, null);
		try {
			Element cmlDOM = new CMLCml();
			if (object instanceof IMolecule) {
				cmlDOM = convertor.cdkMoleculeToCMLMolecule((IMolecule)object);
			} else if (object instanceof IAtom) {
				cmlDOM = convertor.cdkAtomToCMLAtom(null, (IAtom)object);
			} else if (object instanceof IBond) {
				cmlDOM = convertor.cdkBondToCMLBond((IBond)object);
			}
			dialog.setText(cmlDOM.toXML());
		} catch (Exception exception)
		{
			String message = "CML Writer cannot write ChemOject: " + object.getClass().getName();
			logger.error(message);
			logger.debug(exception);
			dialog.setText(message);
		}

		dialog.setVisible(true);
	}

}


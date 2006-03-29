/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sf.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.jchempaint.action;

import java.awt.event.ActionEvent;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.applications.jchempaint.dialogs.EditDictRefs;


/**
 * Allows for editing dictionary references
 *
 * @cdk.module jchempaint
 * @author E.L. Willighagen <elw38@cam.ac.uk>
 */
public class EditDictRefsAction extends JCPAction {

    EditDictRefs frame = null;

    public void actionPerformed(ActionEvent event) {
        if (jcpPanel.getJChemPaintModel() != null) {
            if (frame == null) {
                frame = new EditDictRefs();
            }
            IChemObject object = getSource(event);
            logger.debug("Showing dictionary references for: ", object);
            frame.setChemObject((org.openscience.cdk.ChemObject)object);
            frame.pack();
            frame.show();
        }
    }
    
}

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005-2007  The JChemPaint project
 * 
 * Contact: jchempaint-devel@lists.sourceforge.net
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
import java.util.HashMap;

import javax.swing.undo.UndoableEdit;
import javax.vecmath.Point2d;

import org.openscience.cdk.applications.jchempaint.JChemPaintModel;
import org.openscience.cdk.applications.undoredo.FlipEdit;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.renderer.Renderer2DModel;

/**
 * Action to copy/paste structures.
 *
 * @cdk.module jchempaint
 * @cdk.svnrev  $Revision$
 * @author     Egon Willighagen <e.willighagen@science.ru.nl>
 */
public class FlipAction extends JCPAction {

	private static final long serialVersionUID = 2360209016030592684L;

	public void actionPerformed(ActionEvent e) {
        logger.info("  type  ", type);
        logger.debug("  source ", e.getSource());
        HashMap atomCoordsMap = new HashMap();
        JChemPaintModel jcpModel = jcpPanel.getJChemPaintModel();
        Renderer2DModel renderModel = jcpModel.getRendererModel();
        boolean horiz = "horizontal".equals(type);
        if (renderModel.getSelectedPart()!=null && (horiz || "vertical".equals(type))) {
            IAtomContainer toflip = renderModel.getSelectedPart();
            Point2d center = GeometryTools.get2DCenter(toflip, renderModel.getRenderingCoordinates());
            for (int i=0; i<toflip.getAtomCount(); i++) {
            	IAtom atom = toflip.getAtom(i);
                Point2d p2d = renderModel.getRenderingCoordinate(atom);
                Point2d oldCoord = new Point2d(p2d.x, p2d.y);
                if (horiz) {
                	p2d.y = 2.0*center.y - p2d.y;
                } else {
                	p2d.x = 2.0*center.x - p2d.x;
                }
                Point2d newCoord = p2d;
                if (!oldCoord.equals(newCoord)) {
                    Point2d[] coords = new Point2d[2];
                    coords[0] = newCoord;
                    coords[1] = oldCoord;
                    atomCoordsMap.put(atom, coords);
                }
            }
            UndoableEdit  edit = new FlipEdit(atomCoordsMap);
            jcpPanel.getUndoSupport().postEdit(edit);
            // fire a change so that the view gets updated
            jcpModel.fireChange();
        }
    }

}


/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.renderer.color;

import org.openscience.cdk.interfaces.IAtom;

import java.awt.*;

/**
 * Gives a short table of atom colors for 2D display.
 *
 * @cdk.module render
 * @cdk.svnrev  $Revision$
 */
public class CDK2DAtomColors implements IAtomColorer, java.io.Serializable {

    private static final long serialVersionUID = 6712994043820219426L;
    
    private final static Color HYDROGEN       = Color.black;
    private final static Color CARBON         = Color.black;
    private final static Color NITROGEN       = Color.blue;
    private final static Color OXYGEN         = Color.red;
    private final static Color PHOSPHORUS     = Color.green.darker();
    private final static Color SULPHUR        = Color.yellow.darker();

    private final static Color DEFAULT        = Color.black;
    
    public Color getAtomColor(IAtom a) {
        return getAtomColor(a, DEFAULT);
    }
    
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        int atomnumber = 0;
        if(atom.getAtomicNumber()!=null)
            atomnumber = atom.getAtomicNumber();
        if (atomnumber != 0) {
            switch (atomnumber) {
                case 1:    color = CDK2DAtomColors.HYDROGEN; break;
                case 6:    color = CDK2DAtomColors.CARBON; break;
                case 7:    color = CDK2DAtomColors.NITROGEN; break;
                case 8:    color = CDK2DAtomColors.OXYGEN; break;
                case 15:   color = CDK2DAtomColors.PHOSPHORUS; break;
                case 16:   color = CDK2DAtomColors.SULPHUR; break;
            }
        } else {
            String symbol = atom.getSymbol();
            if (symbol.equals("N")) {
                color = CDK2DAtomColors.NITROGEN;
            }
            if (symbol.equals("O")) {
                color = CDK2DAtomColors.OXYGEN;
            }
            if (symbol.equals("P")) {
                color = CDK2DAtomColors.PHOSPHORUS;
            }
            if (symbol.equals("S")) {
                color = CDK2DAtomColors.SULPHUR;
            }
        }
        return color;
    }
}

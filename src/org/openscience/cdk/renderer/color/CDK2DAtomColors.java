/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import java.awt.Color;

import org.openscience.cdk.interfaces.IAtom;

/**
 * Gives a short table of atom colors for 2D display.
 *
 * @cdk.module render
 */
public class CDK2DAtomColors implements IAtomColorer, java.io.Serializable {

    private final Color HYDROGEN       = Color.black;
    private final Color CARBON         = Color.black;
    private final Color NITROGEN       = Color.blue;
    private final Color OXYGEN         = Color.red;
    private final Color PHOSPHORUS     = Color.green.darker();
    private final Color SULPHUR        = Color.yellow.darker();

    private final Color DEFAULT        = Color.black;
    
    public Color getAtomColor(IAtom a) {
        return getAtomColor(a, DEFAULT);
    }
    
    public Color getAtomColor(IAtom a, Color defaultColor) {
        Color c = defaultColor;
        int atomnumber = a.getAtomicNumber();
        if (atomnumber != 0) {
            switch (atomnumber) {
                case 1:    c = this.HYDROGEN; break;
                case 6:    c = this.CARBON; break;
                case 7:    c = this.NITROGEN; break;
                case 8:    c = this.OXYGEN; break;
                case 15:   c = this.PHOSPHORUS; break;
                case 16:   c = this.SULPHUR; break;
            }
        } else {
            String symbol = a.getSymbol();
            if (symbol.equals("N")) {
                c = this.NITROGEN;
            }
            if (symbol.equals("O")) {
                c = this.OXYGEN;
            }
            if (symbol.equals("P")) {
                c = this.PHOSPHORUS;
            }
            if (symbol.equals("S")) {
                c = this.SULPHUR;
            }
        }
        return c;
    }
}

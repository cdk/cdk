/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.renderer.color;

import org.openscience.cdk.*;
import java.awt.Color;

/**
 * Gives a short table of atom colors for 2D display.
 */
public class CDK2DAtomColors implements AtomColorer, java.io.Serializable {

    private final Color HYDROGEN       = Color.black;
    private final Color CARBON         = Color.black;
    private final Color NITROGEN       = Color.blue;
    private final Color OXYGEN         = Color.red;
    private final Color PHOSPHORUS     = Color.green.darker();
    private final Color SULPHUR        = Color.yellow.darker();

    private final Color DEFAULT        = Color.black;
    
    /**
     * Returns the color for a certain atom type
     */
    public Color getAtomColor(Atom a) {
        Color c = DEFAULT;
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

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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

import java.awt.Color;

import org.openscience.cdk.Atom;

/**
 * Gives a short table of atom colors for 3D display.
 *
 * @cdk.module standard
 */
public class CDKAtomColors implements AtomColorer {

    private final Color HYDROGEN       = Color.white;
    private final Color CARBON         = Color.black;
    private final Color NITROGEN       = Color.blue;
    private final Color OXYGEN         = Color.red;
    private final Color PHOSPHORUS     = Color.green;
    private final Color SULPHUR        = Color.yellow;
    private final Color CHLORINE       = Color.magenta;

    /**
     * Returns the color for a certain atom type
     */
    public Color getAtomColor(Atom a) {
        Color c = Color.darkGray;
        int atomnumber = a.getAtomicNumber();
        switch (atomnumber) {
            case 1:    c = this.HYDROGEN; break;
            case 6:    c = this.CARBON; break;
            case 7:    c = this.NITROGEN; break;
            case 8:    c = this.OXYGEN; break;
            case 15:   c = this.PHOSPHORUS; break;
            case 16:   c = this.SULPHUR; break;
            case 17:   c = this.CHLORINE; break;
        }
        return c;
    }
}

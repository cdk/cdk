/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2001  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Implements the idea of an element in the periodic table.
 * Use an element factory to get a ready-to-use element by name or number
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
            case AtomicNumbers.HYDROGEN:     c = this.HYDROGEN; break;
            case AtomicNumbers.CARBON:       c = this.CARBON; break;
            case AtomicNumbers.NITROGEN:     c = this.NITROGEN; break;
            case AtomicNumbers.OXYGEN:       c = this.OXYGEN; break;
            case AtomicNumbers.PHOSPHORUS:   c = this.PHOSPHORUS; break;
            case AtomicNumbers.SULPHUR:      c = this.SULPHUR; break;
            case AtomicNumbers.CHLORINE:     c = this.CHLORINE; break;
        }
        return c;
    }
}
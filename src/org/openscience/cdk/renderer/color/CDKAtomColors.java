/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * Gives a short table of atom colors for 3D display.
 *
 * @cdk.module render
 * @cdk.svnrev  $Revision$
 */
public class CDKAtomColors implements IAtomColorer {

    private final static Color HYDROGEN       = Color.white;
    private final static Color CARBON         = Color.black;
    private final static Color NITROGEN       = Color.blue;
    private final static Color OXYGEN         = Color.red;
    private final static Color PHOSPHORUS     = Color.green;
    private final static Color SULPHUR        = Color.yellow;
    private final static Color CHLORINE       = Color.magenta;

    private final static Color DEFAULT        = Color.darkGray;

    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, DEFAULT);
    }
    
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Color color = defaultColor;
        int atomnumber = atom.getAtomicNumber();
        switch (atomnumber) {
            case 1:  color = HYDROGEN; break;
            case 6:  color = CARBON; break;
            case 7:  color = NITROGEN; break;
            case 8:  color = OXYGEN; break;
            case 15: color = PHOSPHORUS; break;
            case 16: color = SULPHUR; break;
            case 17: color = CHLORINE; break;
        }
        return color;
    }
}

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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.renderer.color;

import java.awt.Color;

import org.openscience.cdk.Atom;

/**
 * Interface to a class for coloring atoms.
 *
 * @cdk.module standard
 */
public interface AtomColorer {

    /**
     * Returns the color for a certain atom type.
     */
    public Color getAtomColor(Atom atom);

    /**
     * Returns the color for a certain atom type, and uses the
     * given default color if it fails to identify the atom type.
     */
    public Color getAtomColor(Atom atom, Color defaultColor);
}
/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 * Interface to a class for coloring atoms.
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
public interface IAtomColorer {

    /**
     * Returns the color for a certain atom type.
     */
    public Color getAtomColor(IAtom atom);

    /**
     * Returns the color for a certain atom type, and uses the
     * given default color if it fails to identify the atom type.
     */
    public Color getAtomColor(IAtom atom, Color defaultColor);
}
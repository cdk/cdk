/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.color;

import org.openscience.cdk.interfaces.IAtom;

import java.awt.Color;

/**
 * Defines an atom color that draws as a single uniform color.
 *
 * @author John May
 */
public class UniColor implements IAtomColorer {

    private final Color color;

    /**
     * Create a uniform atom colorer.
     *
     * @param color the color
     */
    public UniColor(Color color) {
        this.color = color;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Color getAtomColor(IAtom atom) {
        return color;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        return color;
    }
}

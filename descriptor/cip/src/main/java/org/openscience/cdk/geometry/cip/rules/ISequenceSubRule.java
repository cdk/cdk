/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.geometry.cip.rules;

import java.util.Comparator;

/**
 * Sequence sub rule used in the CIP method to decide which of the two ligands takes
 * precedence {@cdk.cite Cahn1966}. A list ordered based on these rules will be
 * sorted from low to high precedence.
 *
 * @cdk.module cip
 * @cdk.githash
 */
public interface ISequenceSubRule<ILigand> extends Comparator<ILigand> {

    /**
     * Compares two ligands according to the particular sequence sub rule. It returns
     * 1 if ligand1 takes precedence over ligand2, -1 if ligand2 takes precedence over
     * ligand1, and 0 if they are equal.
     *
     * @param  ligand1 the first of the two ligands to compare
     * @param  ligand2 the second of the two ligands to compare
     * @return 1 if ligand1 is of higher precedence than ligand2, -1 if ligand2 is
     *         of higher precedence than ligan1, and 0 if they have equal precedence
     */
    @Override
    public int compare(ILigand ligand1, ILigand ligand2);

}

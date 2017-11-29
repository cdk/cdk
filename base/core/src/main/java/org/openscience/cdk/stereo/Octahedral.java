/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.List;

/**
 * Represents an octahedral configuration of an atom six neighbors. The
 * configuration order is defined as between 1 and 30 using the same permutation
 * tables as SMILES (e.g. @OH1 .. @OH30). This allows the 720 permutations of
 * atom ordering to be described.
 * <br>
 * Normalizing the representation (with {@link #normalize()} returns a
 * configuration reordered such that the configuration order is 1. For example
 * <pre>C[Co@OH8](F)(Br)(Cl)(I)S</pre>
 * is the same as
 * <pre>C[Co@OH1](F)(Cl)(Br)(I)S</pre>. The normalised form is easy to work
 * with as the first and last carriers form an axis, the middle four equatorial
 * carriers are arranged anti-clockwise looking from the first carrier.
 *
 * <pre>
 *      c
 *      | a
 *      |/
 *  d---x---b = OH1
 *     /|       where a: first carrier, b: second carried, etc
 *    f |             x: focus
 *      e             'a' is in front of the focus 'x', 'f' is behind
 * </pre>
 *
 * @see <a href="http://opensmiles.org/opensmiles.html#_octahedral_centers">
 *     Octahedral Centers, OpenSMILES</a>
 * @see TrigonalBipyramidal
 * @see SquarePlanar
 */
public final class Octahedral extends AbstractStereo<IAtom,IAtom> {

    private static final int[][] PERMUTATIONS = new int[][]{
// @OH1
{A, B, C, D, E, F,  A, C, D, E, B, F,  A, D, E, B, C, F,  A, E, B, C, D, F,
 B, A, E, F, C, D,  B, C, A, E, F, D,  B, E, F, C, A, D,  B, F, C, A, E, D,
 C, A, B, F, D, E,  C, B, F, D, A, E,  C, D, A, B, F, E,  C, F, D, A, B, E,
 D, A, C, F, E, B,  D, C, F, E, A, B,  D, E, A, C, F, B,  D, F, E, A, C, B,
 E, A, D, F, B, C,  E, B, A, D, F, C,  E, D, F, B, A, C,  E, F, B, A, D, C,
 F, B, E, D, C, A,  F, C, B, E, D, A,  F, D, C, B, E, A,  F, E, D, C, B, A},
// @OH2
{A, B, E, D, C, F,  A, C, B, E, D, F,  A, D, C, B, E, F,  A, E, D, C, B, F,
 B, A, C, F, E, D,  B, C, F, E, A, D,  B, E, A, C, F, D,  B, F, E, A, C, D,
 C, A, D, F, B, E,  C, B, A, D, F, E,  C, D, F, B, A, E,  C, F, B, A, D, E,
 D, A, E, F, C, B,  D, C, A, E, F, B,  D, E, F, C, A, B,  D, F, C, A, E, B,
 E, A, B, F, D, C,  E, B, F, D, A, C,  E, D, A, B, F, C,  E, F, D, A, B, C,
 F, B, C, D, E, A,  F, C, D, E, B, A,  F, D, E, B, C, A,  F, E, B, C, D, A},
// @OH3
{A, B, C, D, F, E,  A, C, D, F, B, E,  A, D, F, B, C, E,  A, F, B, C, D, E,
 B, A, F, E, C, D,  B, C, A, F, E, D,  B, E, C, A, F, D,  B, F, E, C, A, D,
 C, A, B, E, D, F,  C, B, E, D, A, F,  C, D, A, B, E, F,  C, E, D, A, B, F,
 D, A, C, E, F, B,  D, C, E, F, A, B,  D, E, F, A, C, B,  D, F, A, C, E, B,
 E, B, F, D, C, A,  E, C, B, F, D, A,  E, D, C, B, F, A,  E, F, D, C, B, A,
 F, A, D, E, B, C,  F, B, A, D, E, C,  F, D, E, B, A, C,  F, E, B, A, D, C},
// @OH4
{A, B, C, E, D, F,  A, C, E, D, B, F,  A, D, B, C, E, F,  A, E, D, B, C, F,
 B, A, D, F, C, E,  B, C, A, D, F, E,  B, D, F, C, A, E,  B, F, C, A, D, E,
 C, A, B, F, E, D,  C, B, F, E, A, D,  C, E, A, B, F, D,  C, F, E, A, B, D,
 D, A, E, F, B, C,  D, B, A, E, F, C,  D, E, F, B, A, C,  D, F, B, A, E, C,
 E, A, C, F, D, B,  E, C, F, D, A, B,  E, D, A, C, F, B,  E, F, D, A, C, B,
 F, B, D, E, C, A,  F, C, B, D, E, A,  F, D, E, C, B, A,  F, E, C, B, D, A},
// @OH5
{A, B, C, F, D, E,  A, C, F, D, B, E,  A, D, B, C, F, E,  A, F, D, B, C, E,
 B, A, D, E, C, F,  B, C, A, D, E, F,  B, D, E, C, A, F,  B, E, C, A, D, F,
 C, A, B, E, F, D,  C, B, E, F, A, D,  C, E, F, A, B, D,  C, F, A, B, E, D,
 D, A, F, E, B, C,  D, B, A, F, E, C,  D, E, B, A, F, C,  D, F, E, B, A, C,
 E, B, D, F, C, A,  E, C, B, D, F, A,  E, D, F, C, B, A,  E, F, C, B, D, A,
 F, A, C, E, D, B,  F, C, E, D, A, B,  F, D, A, C, E, B,  F, E, D, A, C, B},
// @OH6
{A, B, C, E, F, D,  A, C, E, F, B, D,  A, E, F, B, C, D,  A, F, B, C, E, D,
 B, A, F, D, C, E,  B, C, A, F, D, E,  B, D, C, A, F, E,  B, F, D, C, A, E,
 C, A, B, D, E, F,  C, B, D, E, A, F,  C, D, E, A, B, F,  C, E, A, B, D, F,
 D, B, F, E, C, A,  D, C, B, F, E, A,  D, E, C, B, F, A,  D, F, E, C, B, A,
 E, A, C, D, F, B,  E, C, D, F, A, B,  E, D, F, A, C, B,  E, F, A, C, D, B,
 F, A, E, D, B, C,  F, B, A, E, D, C,  F, D, B, A, E, C,  F, E, D, B, A, C},
// @OH7
{A, B, C, F, E, D,  A, C, F, E, B, D,  A, E, B, C, F, D,  A, F, E, B, C, D,
 B, A, E, D, C, F,  B, C, A, E, D, F,  B, D, C, A, E, F,  B, E, D, C, A, F,
 C, A, B, D, F, E,  C, B, D, F, A, E,  C, D, F, A, B, E,  C, F, A, B, D, E,
 D, B, E, F, C, A,  D, C, B, E, F, A,  D, E, F, C, B, A,  D, F, C, B, E, A,
 E, A, F, D, B, C,  E, B, A, F, D, C,  E, D, B, A, F, C,  E, F, D, B, A, C,
 F, A, C, D, E, B,  F, C, D, E, A, B,  F, D, E, A, C, B,  F, E, A, C, D, B},
// @OH8
{A, B, D, C, E, F,  A, C, E, B, D, F,  A, D, C, E, B, F,  A, E, B, D, C, F,
 B, A, E, F, D, C,  B, D, A, E, F, C,  B, E, F, D, A, C,  B, F, D, A, E, C,
 C, A, D, F, E, B,  C, D, F, E, A, B,  C, E, A, D, F, B,  C, F, E, A, D, B,
 D, A, B, F, C, E,  D, B, F, C, A, E,  D, C, A, B, F, E,  D, F, C, A, B, E,
 E, A, C, F, B, D,  E, B, A, C, F, D,  E, C, F, B, A, D,  E, F, B, A, C, D,
 F, B, E, C, D, A,  F, C, D, B, E, A,  F, D, B, E, C, A,  F, E, C, D, B, A},
// @OH9
{A, B, D, C, F, E,  A, C, F, B, D, E,  A, D, C, F, B, E,  A, F, B, D, C, E,
 B, A, F, E, D, C,  B, D, A, F, E, C,  B, E, D, A, F, C,  B, F, E, D, A, C,
 C, A, D, E, F, B,  C, D, E, F, A, B,  C, E, F, A, D, B,  C, F, A, D, E, B,
 D, A, B, E, C, F,  D, B, E, C, A, F,  D, C, A, B, E, F,  D, E, C, A, B, F,
 E, B, F, C, D, A,  E, C, D, B, F, A,  E, D, B, F, C, A,  E, F, C, D, B, A,
 F, A, C, E, B, D,  F, B, A, C, E, D,  F, C, E, B, A, D,  F, E, B, A, C, D},
// @OH10
{A, B, E, C, D, F,  A, C, D, B, E, F,  A, D, B, E, C, F,  A, E, C, D, B, F,
 B, A, D, F, E, C,  B, D, F, E, A, C,  B, E, A, D, F, C,  B, F, E, A, D, C,
 C, A, E, F, D, B,  C, D, A, E, F, B,  C, E, F, D, A, B,  C, F, D, A, E, B,
 D, A, C, F, B, E,  D, B, A, C, F, E,  D, C, F, B, A, E,  D, F, B, A, C, E,
 E, A, B, F, C, D,  E, B, F, C, A, D,  E, C, A, B, F, D,  E, F, C, A, B, D,
 F, B, D, C, E, A,  F, C, E, B, D, A,  F, D, C, E, B, A,  F, E, B, D, C, A},
// @OH11
{A, B, F, C, D, E,  A, C, D, B, F, E,  A, D, B, F, C, E,  A, F, C, D, B, E,
 B, A, D, E, F, C,  B, D, E, F, A, C,  B, E, F, A, D, C,  B, F, A, D, E, C,
 C, A, F, E, D, B,  C, D, A, F, E, B,  C, E, D, A, F, B,  C, F, E, D, A, B,
 D, A, C, E, B, F,  D, B, A, C, E, F,  D, C, E, B, A, F,  D, E, B, A, C, F,
 E, B, D, C, F, A,  E, C, F, B, D, A,  E, D, C, F, B, A,  E, F, B, D, C, A,
 F, A, B, E, C, D,  F, B, E, C, A, D,  F, C, A, B, E, D,  F, E, C, A, B, D},
// @OH12
{A, B, E, C, F, D,  A, C, F, B, E, D,  A, E, C, F, B, D,  A, F, B, E, C, D,
 B, A, F, D, E, C,  B, D, E, A, F, C,  B, E, A, F, D, C,  B, F, D, E, A, C,
 C, A, E, D, F, B,  C, D, F, A, E, B,  C, E, D, F, A, B,  C, F, A, E, D, B,
 D, B, F, C, E, A,  D, C, E, B, F, A,  D, E, B, F, C, A,  D, F, C, E, B, A,
 E, A, B, D, C, F,  E, B, D, C, A, F,  E, C, A, B, D, F,  E, D, C, A, B, F,
 F, A, C, D, B, E,  F, B, A, C, D, E,  F, C, D, B, A, E,  F, D, B, A, C, E},
// @OH13
{A, B, F, C, E, D,  A, C, E, B, F, D,  A, E, B, F, C, D,  A, F, C, E, B, D,
 B, A, E, D, F, C,  B, D, F, A, E, C,  B, E, D, F, A, C,  B, F, A, E, D, C,
 C, A, F, D, E, B,  C, D, E, A, F, B,  C, E, A, F, D, B,  C, F, D, E, A, B,
 D, B, E, C, F, A,  D, C, F, B, E, A,  D, E, C, F, B, A,  D, F, B, E, C, A,
 E, A, C, D, B, F,  E, B, A, C, D, F,  E, C, D, B, A, F,  E, D, B, A, C, F,
 F, A, B, D, C, E,  F, B, D, C, A, E,  F, C, A, B, D, E,  F, D, C, A, B, E},
// @OH14
{A, B, D, E, C, F,  A, C, B, D, E, F,  A, D, E, C, B, F,  A, E, C, B, D, F,
 B, A, C, F, D, E,  B, C, F, D, A, E,  B, D, A, C, F, E,  B, F, D, A, C, E,
 C, A, E, F, B, D,  C, B, A, E, F, D,  C, E, F, B, A, D,  C, F, B, A, E, D,
 D, A, B, F, E, C,  D, B, F, E, A, C,  D, E, A, B, F, C,  D, F, E, A, B, C,
 E, A, D, F, C, B,  E, C, A, D, F, B,  E, D, F, C, A, B,  E, F, C, A, D, B,
 F, B, C, E, D, A,  F, C, E, D, B, A,  F, D, B, C, E, A,  F, E, D, B, C, A},
// @OH15
{A, B, D, F, C, E,  A, C, B, D, F, E,  A, D, F, C, B, E,  A, F, C, B, D, E,
 B, A, C, E, D, F,  B, C, E, D, A, F,  B, D, A, C, E, F,  B, E, D, A, C, F,
 C, A, F, E, B, D,  C, B, A, F, E, D,  C, E, B, A, F, D,  C, F, E, B, A, D,
 D, A, B, E, F, C,  D, B, E, F, A, C,  D, E, F, A, B, C,  D, F, A, B, E, C,
 E, B, C, F, D, A,  E, C, F, D, B, A,  E, D, B, C, F, A,  E, F, D, B, C, A,
 F, A, D, E, C, B,  F, C, A, D, E, B,  F, D, E, C, A, B,  F, E, C, A, D, B},
// @OH16
{A, B, F, D, C, E,  A, C, B, F, D, E,  A, D, C, B, F, E,  A, F, D, C, B, E,
 B, A, C, E, F, D,  B, C, E, F, A, D,  B, E, F, A, C, D,  B, F, A, C, E, D,
 C, A, D, E, B, F,  C, B, A, D, E, F,  C, D, E, B, A, F,  C, E, B, A, D, F,
 D, A, F, E, C, B,  D, C, A, F, E, B,  D, E, C, A, F, B,  D, F, E, C, A, B,
 E, B, C, D, F, A,  E, C, D, F, B, A,  E, D, F, B, C, A,  E, F, B, C, D, A,
 F, A, B, E, D, C,  F, B, E, D, A, C,  F, D, A, B, E, C,  F, E, D, A, B, C},
// @OH17
{A, B, E, F, C, D,  A, C, B, E, F, D,  A, E, F, C, B, D,  A, F, C, B, E, D,
 B, A, C, D, E, F,  B, C, D, E, A, F,  B, D, E, A, C, F,  B, E, A, C, D, F,
 C, A, F, D, B, E,  C, B, A, F, D, E,  C, D, B, A, F, E,  C, F, D, B, A, E,
 D, B, C, F, E, A,  D, C, F, E, B, A,  D, E, B, C, F, A,  D, F, E, B, C, A,
 E, A, B, D, F, C,  E, B, D, F, A, C,  E, D, F, A, B, C,  E, F, A, B, D, C,
 F, A, E, D, C, B,  F, C, A, E, D, B,  F, D, C, A, E, B,  F, E, D, C, A, B},
// @OH18
{A, B, F, E, C, D,  A, C, B, F, E, D,  A, E, C, B, F, D,  A, F, E, C, B, D,
 B, A, C, D, F, E,  B, C, D, F, A, E,  B, D, F, A, C, E,  B, F, A, C, D, E,
 C, A, E, D, B, F,  C, B, A, E, D, F,  C, D, B, A, E, F,  C, E, D, B, A, F,
 D, B, C, E, F, A,  D, C, E, F, B, A,  D, E, F, B, C, A,  D, F, B, C, E, A,
 E, A, F, D, C, B,  E, C, A, F, D, B,  E, D, C, A, F, B,  E, F, D, C, A, B,
 F, A, B, D, E, C,  F, B, D, E, A, C,  F, D, E, A, B, C,  F, E, A, B, D, C},
// @OH19
{A, B, D, E, F, C,  A, D, E, F, B, C,  A, E, F, B, D, C,  A, F, B, D, E, C,
 B, A, F, C, D, E,  B, C, D, A, F, E,  B, D, A, F, C, E,  B, F, C, D, A, E,
 C, B, F, E, D, A,  C, D, B, F, E, A,  C, E, D, B, F, A,  C, F, E, D, B, A,
 D, A, B, C, E, F,  D, B, C, E, A, F,  D, C, E, A, B, F,  D, E, A, B, C, F,
 E, A, D, C, F, B,  E, C, F, A, D, B,  E, D, C, F, A, B,  E, F, A, D, C, B,
 F, A, E, C, B, D,  F, B, A, E, C, D,  F, C, B, A, E, D,  F, E, C, B, A, D},
// @OH20
{A, B, D, F, E, C,  A, D, F, E, B, C,  A, E, B, D, F, C,  A, F, E, B, D, C,
 B, A, E, C, D, F,  B, C, D, A, E, F,  B, D, A, E, C, F,  B, E, C, D, A, F,
 C, B, E, F, D, A,  C, D, B, E, F, A,  C, E, F, D, B, A,  C, F, D, B, E, A,
 D, A, B, C, F, E,  D, B, C, F, A, E,  D, C, F, A, B, E,  D, F, A, B, C, E,
 E, A, F, C, B, D,  E, B, A, F, C, D,  E, C, B, A, F, D,  E, F, C, B, A, D,
 F, A, D, C, E, B,  F, C, E, A, D, B,  F, D, C, E, A, B,  F, E, A, D, C, B},
// @OH21
{A, B, E, D, F, C,  A, D, F, B, E, C,  A, E, D, F, B, C,  A, F, B, E, D, C,
 B, A, F, C, E, D,  B, C, E, A, F, D,  B, E, A, F, C, D,  B, F, C, E, A, D,
 C, B, F, D, E, A,  C, D, E, B, F, A,  C, E, B, F, D, A,  C, F, D, E, B, A,
 D, A, E, C, F, B,  D, C, F, A, E, B,  D, E, C, F, A, B,  D, F, A, E, C, B,
 E, A, B, C, D, F,  E, B, C, D, A, F,  E, C, D, A, B, F,  E, D, A, B, C, F,
 F, A, D, C, B, E,  F, B, A, D, C, E,  F, C, B, A, D, E,  F, D, C, B, A, E},
// @OH22
{A, B, F, D, E, C,  A, D, E, B, F, C,  A, E, B, F, D, C,  A, F, D, E, B, C,
 B, A, E, C, F, D,  B, C, F, A, E, D,  B, E, C, F, A, D,  B, F, A, E, C, D,
 C, B, E, D, F, A,  C, D, F, B, E, A,  C, E, D, F, B, A,  C, F, B, E, D, A,
 D, A, F, C, E, B,  D, C, E, A, F, B,  D, E, A, F, C, B,  D, F, C, E, A, B,
 E, A, D, C, B, F,  E, B, A, D, C, F,  E, C, B, A, D, F,  E, D, C, B, A, F,
 F, A, B, C, D, E,  F, B, C, D, A, E,  F, C, D, A, B, E,  F, D, A, B, C, E},
// @OH23
{A, B, E, F, D, C,  A, D, B, E, F, C,  A, E, F, D, B, C,  A, F, D, B, E, C,
 B, A, D, C, E, F,  B, C, E, A, D, F,  B, D, C, E, A, F,  B, E, A, D, C, F,
 C, B, D, F, E, A,  C, D, F, E, B, A,  C, E, B, D, F, A,  C, F, E, B, D, A,
 D, A, F, C, B, E,  D, B, A, F, C, E,  D, C, B, A, F, E,  D, F, C, B, A, E,
 E, A, B, C, F, D,  E, B, C, F, A, D,  E, C, F, A, B, D,  E, F, A, B, C, D,
 F, A, E, C, D, B,  F, C, D, A, E, B,  F, D, A, E, C, B,  F, E, C, D, A, B},
// @OH24
{A, B, F, E, D, C,  A, D, B, F, E, C,  A, E, D, B, F, C,  A, F, E, D, B, C,
 B, A, D, C, F, E,  B, C, F, A, D, E,  B, D, C, F, A, E,  B, F, A, D, C, E,
 C, B, D, E, F, A,  C, D, E, F, B, A,  C, E, F, B, D, A,  C, F, B, D, E, A,
 D, A, E, C, B, F,  D, B, A, E, C, F,  D, C, B, A, E, F,  D, E, C, B, A, F,
 E, A, F, C, D, B,  E, C, D, A, F, B,  E, D, A, F, C, B,  E, F, C, D, A, B,
 F, A, B, C, E, D,  F, B, C, E, A, D,  F, C, E, A, B, D,  F, E, A, B, C, D},
// @OH25
{A, C, D, E, F, B,  A, D, E, F, C, B,  A, E, F, C, D, B,  A, F, C, D, E, B,
 B, C, F, E, D, A,  B, D, C, F, E, A,  B, E, D, C, F, A,  B, F, E, D, C, A,
 C, A, F, B, D, E,  C, B, D, A, F, E,  C, D, A, F, B, E,  C, F, B, D, A, E,
 D, A, C, B, E, F,  D, B, E, A, C, F,  D, C, B, E, A, F,  D, E, A, C, B, F,
 E, A, D, B, F, C,  E, B, F, A, D, C,  E, D, B, F, A, C,  E, F, A, D, B, C,
 F, A, E, B, C, D,  F, B, C, A, E, D,  F, C, A, E, B, D,  F, E, B, C, A, D},
// @OH26
{A, C, D, F, E, B,  A, D, F, E, C, B,  A, E, C, D, F, B,  A, F, E, C, D, B,
 B, C, E, F, D, A,  B, D, C, E, F, A,  B, E, F, D, C, A,  B, F, D, C, E, A,
 C, A, E, B, D, F,  C, B, D, A, E, F,  C, D, A, E, B, F,  C, E, B, D, A, F,
 D, A, C, B, F, E,  D, B, F, A, C, E,  D, C, B, F, A, E,  D, F, A, C, B, E,
 E, A, F, B, C, D,  E, B, C, A, F, D,  E, C, A, F, B, D,  E, F, B, C, A, D,
 F, A, D, B, E, C,  F, B, E, A, D, C,  F, D, B, E, A, C,  F, E, A, D, B, C},
// @OH27
{A, C, E, D, F, B,  A, D, F, C, E, B,  A, E, D, F, C, B,  A, F, C, E, D, B,
 B, C, F, D, E, A,  B, D, E, C, F, A,  B, E, C, F, D, A,  B, F, D, E, C, A,
 C, A, F, B, E, D,  C, B, E, A, F, D,  C, E, A, F, B, D,  C, F, B, E, A, D,
 D, A, E, B, F, C,  D, B, F, A, E, C,  D, E, B, F, A, C,  D, F, A, E, B, C,
 E, A, C, B, D, F,  E, B, D, A, C, F,  E, C, B, D, A, F,  E, D, A, C, B, F,
 F, A, D, B, C, E,  F, B, C, A, D, E,  F, C, A, D, B, E,  F, D, B, C, A, E},
// @OH28
{A, C, F, D, E, B,  A, D, E, C, F, B,  A, E, C, F, D, B,  A, F, D, E, C, B,
 B, C, E, D, F, A,  B, D, F, C, E, A,  B, E, D, F, C, A,  B, F, C, E, D, A,
 C, A, E, B, F, D,  C, B, F, A, E, D,  C, E, B, F, A, D,  C, F, A, E, B, D,
 D, A, F, B, E, C,  D, B, E, A, F, C,  D, E, A, F, B, C,  D, F, B, E, A, C,
 E, A, D, B, C, F,  E, B, C, A, D, F,  E, C, A, D, B, F,  E, D, B, C, A, F,
 F, A, C, B, D, E,  F, B, D, A, C, E,  F, C, B, D, A, E,  F, D, A, C, B, E},
// @OH29
{A, C, E, F, D, B,  A, D, C, E, F, B,  A, E, F, D, C, B,  A, F, D, C, E, B,
 B, C, D, F, E, A,  B, D, F, E, C, A,  B, E, C, D, F, A,  B, F, E, C, D, A,
 C, A, D, B, E, F,  C, B, E, A, D, F,  C, D, B, E, A, F,  C, E, A, D, B, F,
 D, A, F, B, C, E,  D, B, C, A, F, E,  D, C, A, F, B, E,  D, F, B, C, A, E,
 E, A, C, B, F, D,  E, B, F, A, C, D,  E, C, B, F, A, D,  E, F, A, C, B, D,
 F, A, E, B, D, C,  F, B, D, A, E, C,  F, D, A, E, B, C,  F, E, B, D, A, C},
// @OH30
{A, C, F, E, D, B,  A, D, C, F, E, B,  A, E, D, C, F, B,  A, F, E, D, C, B,
 B, C, D, E, F, A,  B, D, E, F, C, A,  B, E, F, C, D, A,  B, F, C, D, E, A,
 C, A, D, B, F, E,  C, B, F, A, D, E,  C, D, B, F, A, E,  C, F, A, D, B, E,
 D, A, E, B, C, F,  D, B, C, A, E, F,  D, C, A, E, B, F,  D, E, B, C, A, F,
 E, A, F, B, D, C,  E, B, D, A, F, C,  E, D, A, F, B, C,  E, F, B, D, A, C,
 F, A, C, B, E, D,  F, B, E, A, C, D,  F, C, B, E, A, D,  F, E, A, C, B, D}
    };

    /**
     * Create a new octahedral configuration.
     * @param focus    the focus
     * @param carriers the carriers
     * @param order    the order of the configuration 0-30.
     */
    public Octahedral(IAtom focus, IAtom[] carriers, int order) {
        super(focus, carriers, IStereoElement.OC | (order & 0xff));
        if (getConfigOrder() < 0 || getConfigOrder() > 30)
            throw new IllegalArgumentException("Invalid configuration order!"
                                               + "Should be in range 1-30");
    }

    /**
     * Normalize the configuration to the lowest order (1). For example
     * <pre>C[Co@OH8](F)(Br)(Cl)(I)S</pre>
     * is the same as
     * <pre>C[Co@OH1](F)(Cl)(Br)(I)S</pre>. The normalised form is easy to
     * work with as the first and last carriers form an axis, the middle four
     * equatorial carriers are arranged anti-clockwise looking from the first carrier.
     * @return the normalized form
     */
    public Octahedral normalize() {
        int cfg = getConfigOrder();
        if (cfg == 1)
          return this;
        if (cfg < 1 || cfg > 30)
            throw new IllegalArgumentException(
                "Invalid config order: " + cfg + ", octahedral should be"
                + "1 <= order <= 30!");
        IAtom[] carriers = invapply(getCarriers().toArray(new IAtom[6]),
                                    PERMUTATIONS[cfg-1]);
        return new Octahedral(getFocus(), carriers, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Octahedral create(IAtom focus, List<IAtom> carriers, int cfg) {
        return new Octahedral(focus,
                              carriers.toArray(new IAtom[6]),
                              cfg);
    }
}

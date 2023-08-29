/*
 * Copyright (C) 2023 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.smirks;

import java.util.EnumSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public enum SmirksOption {
    /**
     * Additional check and warnings when parsing SMIRKS.
     */
    PEDANTIC,
    /**
     * Run the reaction backwards (retro synthesis). The right-hand-side is now
     * matched as a query. Note that not all SMIRKS are reversible and good
     * form is usually write separate forward/backwards transformations.
     */
    REVERSE,
    /**
     * Do not allow '[*:1].[*:2]' to match the same fragment.
     * The preferred way to do this component grouping '([*:1]).([*:2])' but if
     * you are only performing reactions it is reasonable that you expect the
     * parts to be different and some toolkits only have this as an option.
     */
    DIFF_PART,
    /**
     * Do not allow the atomic number to be changed. {@code [Pb:1]>>[Au:1]}
     * will do nothing.
     */
    IGNORE_SET_ELEM,
    /**
     * Do not allow the implicit hydrogen count to be set.
     * {@code [Ch4:1]>>[Ch3:1]} does nothing.
     */
    IGNORE_IMPL_H,
    /**
     * Do not allow the total hydrogen count to be set.
     * {@code [CH4:1]>>[CH3:1]} does nothing.
     */
    IGNORE_TOTAL_H,
    /**
     * Do not allow the total hydrogen count to be set if it is 0.
     * {@code [CH4:1]>>[CH3:1]} works but {@code [CH4:1]>>[CH0:1]} does
     * not.
     */
    IGNORE_TOTAL_H0,
    /**
     * If a bond already exists where a new one is to be created update the bond
     * order as required.
     */
    OVERWRITE_BOND;

    /**
     * Align closer with RDKit's "Reaction SMARTS" semantics.
     * WIP - need to handle the floating valence
     */
    public static final Set<SmirksOption> RDKIT = unmodifiableSet(EnumSet.of(DIFF_PART,
                                                                             IGNORE_IMPL_H,
                                                                             IGNORE_TOTAL_H0,
                                                                             OVERWRITE_BOND));
}

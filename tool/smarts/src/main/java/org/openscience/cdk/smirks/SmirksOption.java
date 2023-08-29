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
     * Run the reaction backwards (retro synthesis), the right-hand-side is now
     * matched as a query. Note that not all SMIRKS are reversible and good
     * form is usually write separate forward/backwards transformations.
     */
    REVERSE
}

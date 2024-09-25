/* Copyright (C) 2024 Uli Fechner
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
 */
package org.openscience.cdk.rinchi;

/**
 * Enumeration representing options for RInChI processing.
 * <br>
 * The single option is:
 * <ul>
 * <li>{@code FORCE_EQUILIBRIUM}: Enforces the reaction to be in equilibrium</li>
 * </ul>
 *
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public enum RInChIOption {
    /**
     * Enforces the reaction to be in equilibrium.
     */
    FORCE_EQUILIBRIUM;

    /**
     * Returns the name of the enum constant in lowercase,
     * with underscores replaced by spaces.
     *
     * @return readable name of the enum constant
     */
    @Override
    public String toString() {
        return name().toLowerCase().replace('_', ' ');
    }
}
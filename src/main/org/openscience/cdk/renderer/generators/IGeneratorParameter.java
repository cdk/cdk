/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@list.sourceforge.net
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
package org.openscience.cdk.renderer.generators;

/**
 * Parameter from some rendering of the 2D diagram. Such parameters
 * may be bond width, (relative) font sizes, coloring scheme, display or
 * not of atomic properties, rendering a circles for aromatic rings,
 * etc.
 *
 * @cdk.module  render
 * @cdk.githash
 */
public interface IGeneratorParameter<T> {

    /**
     * Sets the value for this parameter.
     *
     * @param value the new parameter value
     */
    public void setValue(T value);

    /**
     * Gets the value for this parameter. It must provide a reasonable
     * default when no other value has been set.
     *
     * @return the current parameter value
     */
    public T getValue();

    /**
     * Gets the default value for this parameter. This value is set by the
     * parameter class and cannot be changed.
     *
     * @return the default value for this parameter
     */
    public T getDefault();

}

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
package org.openscience.cdk.renderer.generators.parameter;

import org.openscience.cdk.renderer.generators.IGeneratorParameter;

/**
 * Abstract class to provide the base functionality for
 * {@link IGeneratorParameter} implementations.
 *
 * @cdk.module  render
 */
public abstract class AbstractGeneratorParameter<T>
    implements IGeneratorParameter<T>{

    private T parameterSetting;

    /**
     * Sets the value for this parameter.
     *
     * @param value the new parameter value
     */
    public void setValue(T value) {
        this.parameterSetting = value;
    }

    /**
     * Gets the value for this parameter. It must provide a reasonable
     * default when no other value has been set.
     *
     * @param value the new parameter value
     */
    public T getValue() {
        if (this.parameterSetting == null)
            return getDefault();
        else
            return this.parameterSetting;
    }

}

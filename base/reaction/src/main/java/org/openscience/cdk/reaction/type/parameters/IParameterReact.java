/* Copyright (C) 2008  Miquel Rojas Cherto <miguelrojasch@users.sf.net>
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
package org.openscience.cdk.reaction.type.parameters;


/**
 * Interface for classes that generate parameters used in reactions.
 *
 * @author      miguelrojasch
 */
public interface IParameterReact {

    /**
     * Set the parameter to take account.
     *
     * @param set True, if the parameter needs to take account
     */
    void setParameter(boolean set);

    /**
     * Get if this parameter needs to take account.
     *
     * @return True, if the parameter needs to take account
     */
    boolean isSetParameter();

    /**
     * Set the value of the parameter.
     *
     * @param value The value of the parameter
     */
    void setValue(Object value);

    /**
     * Get the value of the parameter.
     *
     * @return The value of the parameter
     */
    Object getValue();
}

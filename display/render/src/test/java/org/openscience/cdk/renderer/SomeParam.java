/* Copyright (C) 2010  Egon Willighagen <egon.willighagen@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import org.openscience.cdk.renderer.generators.parameter.AbstractGeneratorParameter;

/**
 * Helper class for the unit tests in {@link RendererModelTest}.
 *
 * @cdk.module test-render
 */
public class SomeParam extends AbstractGeneratorParameter<Boolean> {

    Boolean value = Boolean.FALSE;

    @Override
    public Boolean getDefault() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean getValue() {
        return value;
    }

    @Override
    public void setValue(Boolean value) {
        this.value = value;
    }
};

/*
 * Copyright (C) 2000-2012  John May <jwmay@users.sf.net>
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
package org.openscience.cdk;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * A simple mock ICDKObject so we can test private constructors. We can't test
 * with an inner class as synthetic public constructors are made to bridge the
 * inner class creation.
 *
 * @author John May
 * @cdk.module test-core
 * @see DynamicFactoryTest
 */
public class DynamicFactoryTestMock implements ICDKObject {

    public DynamicFactoryTestMock(String ignored) {

    }

    public DynamicFactoryTestMock(IAtom[] atoms) {
        System.out.println("IAtom[] constructor invoked");
    }

    private DynamicFactoryTestMock() {

    }

    @Override
    public IChemObjectBuilder getBuilder() {
        return null;
    }
}

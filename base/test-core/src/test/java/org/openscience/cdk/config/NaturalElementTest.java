/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IElement;

/**
 * @cdk.module test-core
 */
public class NaturalElementTest {

    @Test
    public void testGetFlagValueZeroDefault() {
        IChemObject chemObject = new NaturalElement("C", 12);
        Assert.assertEquals((short) 0, chemObject.getFlagValue());
    }

    @Test
    public void testSymbol() {
        IElement chemObject = new NaturalElement("C", 12);
        Assert.assertEquals("C", chemObject.getSymbol());
    }

    @Test
    public void testMassNumber() {
        IElement chemObject = new NaturalElement("C", 12);
        Assert.assertEquals(12, chemObject.getAtomicNumber().intValue());
    }

}

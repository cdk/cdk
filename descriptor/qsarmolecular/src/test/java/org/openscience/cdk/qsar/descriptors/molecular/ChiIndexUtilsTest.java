/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module test-qsarmolecular
 */
public class ChiIndexUtilsTest extends CDKTestCase {

    DefaultChemObjectBuilder builder;

    public ChiIndexUtilsTest() {}

    @Before
    public void setup() {
        builder = (DefaultChemObjectBuilder) DefaultChemObjectBuilder.getInstance();
    }

    @Test
    public void testDeltaVSulphurSO() {
        IAtom s = builder.newInstance(IAtom.class, "S");
        IAtom o = builder.newInstance(IAtom.class, "O");
        IBond b = builder.newInstance(IBond.class, s, o);
        b.setOrder(IBond.Order.DOUBLE);

        IAtomContainer m = builder.newInstance(IAtomContainer.class);
        m.addAtom(s);
        m.addAtom(o);
        m.addBond(b);

        double deltav = ChiIndexUtils.deltavSulphur(s, m);
        Assert.assertEquals(1.33, deltav, 0.01);
    }

    @Test
    public void testDeltaVSulphurSO2() {
        IAtom s = builder.newInstance(IAtom.class, "S");
        IAtom o1 = builder.newInstance(IAtom.class, "O");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IBond b1 = builder.newInstance(IBond.class, s, o1);
        IBond b2 = builder.newInstance(IBond.class, s, o2);
        b1.setOrder(IBond.Order.DOUBLE);
        b2.setOrder(IBond.Order.DOUBLE);

        IAtomContainer m = builder.newInstance(IAtomContainer.class);
        m.addAtom(s);
        m.addAtom(o1);
        m.addBond(b1);
        m.addAtom(o2);
        m.addBond(b2);

        double deltav = ChiIndexUtils.deltavSulphur(s, m);
        Assert.assertEquals(2.67, deltav, 0.01);
    }

}

/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractAtomContainerTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the AtomContainer.
 *
 * @cdk.module test-data
 */
public class AtomContainerTest extends AbstractAtomContainerTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new AtomContainer();
            }
        });
    }

    @Test
    public void testAtomContainer_int_int_int_int() {
        // create an empty container with predefined
        // array lengths
        IAtomContainer ac = new AtomContainer(5, 6, 1, 2);

        Assert.assertEquals(0, ac.getAtomCount());
        Assert.assertEquals(0, ac.getElectronContainerCount());

        // test whether the ElectronContainer is correctly initialized
        ac.addBond(ac.getBuilder().newInstance(IBond.class, ac.getBuilder().newInstance(IAtom.class, "C"),
                ac.getBuilder().newInstance(IAtom.class, "C"), IBond.Order.DOUBLE));
        ac.addLonePair(ac.getBuilder().newInstance(ILonePair.class, ac.getBuilder().newInstance(IAtom.class, "N")));
    }

    @Test
    public void testAtomContainer() {
        // create an empty container with in the constructor defined array lengths
        IAtomContainer container = new AtomContainer();

        Assert.assertEquals(0, container.getAtomCount());
        Assert.assertEquals(0, container.getBondCount());

        // test whether the ElectronContainer is correctly initialized
        container.addBond(container.getBuilder().newInstance(IBond.class,
                container.getBuilder().newInstance(IAtom.class, "C"),
                container.getBuilder().newInstance(IAtom.class, "C"), IBond.Order.DOUBLE));
        container.addLonePair(container.getBuilder().newInstance(ILonePair.class,
                container.getBuilder().newInstance(IAtom.class, "N")));
    }

    @Test
    public void testAtomContainer_IAtomContainer() {
        IAtomContainer acetone = newChemObject().getBuilder().newInstance(IAtomContainer.class);
        IAtom c1 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom c2 = acetone.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = acetone.getBuilder().newInstance(IAtom.class, "O");
        IAtom c3 = acetone.getBuilder().newInstance(IAtom.class, "C");
        acetone.addAtom(c1);
        acetone.addAtom(c2);
        acetone.addAtom(c3);
        acetone.addAtom(o);
        IBond b1 = acetone.getBuilder().newInstance(IBond.class, c1, c2, IBond.Order.SINGLE);
        IBond b2 = acetone.getBuilder().newInstance(IBond.class, c1, o, IBond.Order.DOUBLE);
        IBond b3 = acetone.getBuilder().newInstance(IBond.class, c1, c3, IBond.Order.SINGLE);
        acetone.addBond(b1);
        acetone.addBond(b2);
        acetone.addBond(b3);

        IAtomContainer container = new AtomContainer(acetone);
        Assert.assertEquals(4, container.getAtomCount());
        Assert.assertEquals(3, container.getBondCount());
    }
}

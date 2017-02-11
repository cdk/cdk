/* Copyright (C) 2001-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk;

import org.openscience.cdk.interfaces.AbstractPolymerTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestCase for the Polymer class.
 *
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @cdk.created 2001-08-09
 * @cdk.module  test-data
 */
public class PolymerTest extends AbstractPolymerTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Polymer();
            }
        });
    }

    @Test
    public void testPolymer() {
        IPolymer oPolymer = new Polymer();
        Assert.assertNotNull(oPolymer);
        Assert.assertEquals(oPolymer.getMonomerCount(), 0);
    }

    /**
     * A clone must deep clone everything, so that after the clone, operations
     * on the original do not modify the clone.
     *
     * @cdk.bug 2454890
     */
    @Test
    public void testPolymerClone() throws Exception {
        IPolymer oPolymer = new Polymer();
        Assert.assertNotNull(oPolymer);
        Assert.assertEquals(0, oPolymer.getMonomerCount());
        Polymer clone = (Polymer) oPolymer.clone();
        Monomer monomer = new Monomer();
        monomer.setMonomerName("TYR55");
        oPolymer.addAtom(new Atom("C"), monomer);

        // changes should not occur in the clone
        Assert.assertEquals(0, clone.getMonomerCount());
        Assert.assertEquals(0, clone.getMonomerNames().size());

        // new clone should see the changes
        clone = (Polymer) oPolymer.clone();
        Assert.assertEquals(1, clone.getMonomerCount());
        Assert.assertEquals(1, clone.getMonomerNames().size());
        Assert.assertEquals(1, clone.getAtomCount());

        oPolymer.addAtom(new Atom("N"));
        clone = (Polymer) oPolymer.clone();
        Assert.assertEquals(1, clone.getMonomerCount());
        Assert.assertEquals(2, clone.getAtomCount());
    }

    /**
     * @cdk.bug  2454890
     */
    @Test
    public void testPolymerClone2() throws CloneNotSupportedException {
        IPolymer oPolymer = new Polymer();
        Assert.assertNotNull(oPolymer);
        Assert.assertEquals(0, oPolymer.getMonomerCount());

        Monomer monomer = new Monomer();
        monomer.setMonomerName("TYR55");
        IAtom atom = monomer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(atom, monomer);

        Polymer clone = (Polymer) oPolymer.clone();
        IMonomer clonedMonomer = clone.getMonomer("TYR55");
        Assert.assertNotSame(monomer, clonedMonomer);
        IAtom clonedAtom = clone.getAtom(0);
        Assert.assertNotSame(atom, clonedAtom);

        IAtom atomFromMonomer = clone.getMonomer("TYR55").getAtom(0);
        Assert.assertEquals("C", atomFromMonomer.getSymbol());
        Assert.assertNotSame(atom, atomFromMonomer);
        Assert.assertSame(atomFromMonomer, clonedAtom);
    }
}

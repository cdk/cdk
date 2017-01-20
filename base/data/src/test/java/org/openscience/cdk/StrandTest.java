/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.interfaces.AbstractStrandTest;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @cdk.module test-data
 */
public class StrandTest extends AbstractStrandTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new Strand();
            }
        });
    }

    @Test
    public void testStrand() {
        IStrand oStrand = new Strand();
        Assert.assertNotNull(oStrand);
        Assert.assertEquals(oStrand.getMonomerCount(), 0);

        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("HOH"));
        IMonomer oMono3 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono3.setMonomerName(new String("GLYA16"));
        IAtom oAtom1 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom4 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom5 = oStrand.getBuilder().newInstance(IAtom.class, "C");

        oStrand.addAtom(oAtom1);
        oStrand.addAtom(oAtom2);
        oStrand.addAtom(oAtom3, oMono1);
        oStrand.addAtom(oAtom4, oMono2);
        oStrand.addAtom(oAtom5, oMono3);
        Assert.assertNotNull(oStrand.getAtom(0));
        Assert.assertNotNull(oStrand.getAtom(1));
        Assert.assertNotNull(oStrand.getAtom(2));
        Assert.assertNotNull(oStrand.getAtom(3));
        Assert.assertNotNull(oStrand.getAtom(4));
        Assert.assertEquals(oAtom1, oStrand.getAtom(0));
        Assert.assertEquals(oAtom2, oStrand.getAtom(1));
        Assert.assertEquals(oAtom3, oStrand.getAtom(2));
        Assert.assertEquals(oAtom4, oStrand.getAtom(3));
        Assert.assertEquals(oAtom5, oStrand.getAtom(4));

        Assert.assertNull(oStrand.getMonomer("0815"));
        Assert.assertNotNull(oStrand.getMonomer(""));
        Assert.assertNotNull(oStrand.getMonomer("TRP279"));
        Assert.assertEquals(oMono1, oStrand.getMonomer("TRP279"));
        Assert.assertEquals(oStrand.getMonomer("TRP279").getAtomCount(), 1);
        Assert.assertNotNull(oStrand.getMonomer("HOH"));
        Assert.assertEquals(oMono2, oStrand.getMonomer("HOH"));
        Assert.assertEquals(oStrand.getMonomer("HOH").getAtomCount(), 1);
        Assert.assertEquals(oStrand.getMonomer("").getAtomCount(), 2);
        Assert.assertEquals(oStrand.getAtomCount(), 5);
        Assert.assertEquals(oStrand.getMonomerCount(), 3);
    }

}

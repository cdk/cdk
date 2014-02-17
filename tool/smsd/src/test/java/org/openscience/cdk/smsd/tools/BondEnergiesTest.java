/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.smsd.tools;


import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * @author Asad
 * @cdk.module test-smsd
 */
public class BondEnergiesTest extends CDKTestCase {

    @Test
    public void testGetInstance() throws Exception {
    	BondEnergies energies = BondEnergies.getInstance();
    	Assert.assertNotNull(energies);
    }

    /**
     * Test of getEnergies method, of class BondEnergies.
     */
    @Test
    public void testGetEnergies() {
        System.out.println("getEnergies");
        IAtom sourceAtom = new Atom("C");
        IAtom targetAtom = new Atom("C");
        Order bondOrder = Order.SINGLE;
        BondEnergies instance = new BondEnergies();
        Integer expResult = 346;
        Integer result = instance.getEnergies(sourceAtom, targetAtom, bondOrder);
        Assert.assertEquals(expResult, result);
    }

}

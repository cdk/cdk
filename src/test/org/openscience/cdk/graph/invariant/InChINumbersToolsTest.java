/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-inchi
 */
public class InChINumbersToolsTest extends CDKTestCase {

    @Test
    public void testSimpleNumbering() throws CDKException{
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("O"));
        container.addAtom(new Atom("C"));
        container.addBond(0, 1, IBond.Order.SINGLE);
        long[] numbers = InChINumbersTools.getNumbers(container);
        Assert.assertEquals(2, numbers.length);
        Assert.assertEquals(2, numbers[0]);
        Assert.assertEquals(1, numbers[1]);
    }
	
    @Test
    public void testHydrogens() throws CDKException{
        IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("H"));
        container.addAtom(new Atom("C"));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 2, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 3, IBond.Order.SINGLE);
        container.addAtom(new Atom("H"));
        container.addBond(1, 4, IBond.Order.SINGLE);
        long[] numbers = InChINumbersTools.getNumbers(container);
        Assert.assertEquals(5, numbers.length);
        Assert.assertEquals(0, numbers[0]);
        Assert.assertEquals(1, numbers[1]);
        Assert.assertEquals(0, numbers[2]);
        Assert.assertEquals(0, numbers[3]);
        Assert.assertEquals(0, numbers[4]);
    }

    @Test
    public void testGlycine() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = parser.parseSmiles("C(C(=O)O)N");
        long[] numbers = InChINumbersTools.getNumbers(atomContainer);
        Assert.assertEquals(5, numbers.length);
        Assert.assertEquals(1, numbers[0]);
        Assert.assertEquals(2, numbers[1]);
        Assert.assertEquals(4, numbers[2]);
        Assert.assertEquals(5, numbers[3]);
        Assert.assertEquals(3, numbers[4]);
    }
}

/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.silent.Atom;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.silent.Bond;

/**
 * Test suite for testing deduce-bond-order implementations.
 * This suite tests deduction from hybridization rich starting
 * points, excluding, but optional, implicit or explicit
 * hydrogen counts.
 *
 * @author      egonw
 * @cdk.module  test-valencycheck
 * @cdk.created 2006-08-16
 */
public class DeduceBondOrderTestFromExplicitHydrogens extends CDKTestCase {

    private IDeduceBondOrderTool dboTool;

    @Before
    public void setUp() throws Exception {
        dboTool = new SaturationChecker();
    }

    /**
     * Test <div class="inchi">InChI=1/C2H2/c1-2/h1-2H</div>.
     */
    @Test
    public void testAcetylene() throws Exception {
        IAtomContainer keto = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(keto, atom1, 1);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(keto, atom2, 1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);

        keto.addAtom(atom1);
        keto.addAtom(atom2);
        keto.addBond(bond1);

        // now have the algorithm have a go at it
        dboTool.saturate(keto);

        // now check whether it did the right thing
        Assert.assertEquals(Order.TRIPLE, bond1.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C2H4O/c1-2-3/h2H,1H3</div>.
     */
    @Test
    public void testKeto() throws Exception {
        IAtomContainer keto = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(keto, atom1, 3);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(keto, atom2, 1);
        IAtom atom3 = new Atom(Elements.OXYGEN);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);

        keto.addAtom(atom1);
        keto.addAtom(atom2);
        keto.addAtom(atom3);
        keto.addBond(bond1);
        keto.addBond(bond2);

        // now have the algorithm have a go at it
        dboTool.saturate(keto);

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE, bond1.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond2.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C2H6O/c1-2-3/h3H,2H2,1H3</div>.
     */
    @Test
    public void testEnol() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom1, 2);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.OXYGEN);
        addHydrogens(enol, atom3, 1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addBond(bond1);
        enol.addBond(bond2);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.DOUBLE, bond1.getOrder());
        Assert.assertEquals(Order.SINGLE, bond2.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C4H6/c1-3-4-2/h3-4H,1-2H2</div>.
     */
    @Test
    public void xtestButadiene() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom1, 2);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom3, 1);
        IAtom atom4 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom4, 2);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.DOUBLE, bond1.getOrder());
        Assert.assertEquals(Order.SINGLE, bond2.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond3.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C6H4O2/c7-5-1-2-6(8)4-3-5/h1-4H</div>.
     */
    @Test
    public void testQuinone() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom3, 1);
        IAtom atom4 = new Atom(Elements.CARBON);
        IAtom atom5 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom5, 1);
        IAtom atom6 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom6, 1);
        IAtom atom7 = new Atom(Elements.OXYGEN);
        IAtom atom8 = new Atom(Elements.OXYGEN);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom6);
        IBond bond6 = new Bond(atom6, atom1);
        IBond bond7 = new Bond(atom7, atom1);
        IBond bond8 = new Bond(atom8, atom4);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addAtom(atom6);
        enol.addAtom(atom7);
        enol.addAtom(atom8);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);
        enol.addBond(bond6);
        enol.addBond(bond7);
        enol.addBond(bond8);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE, bond1.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond2.getOrder());
        Assert.assertEquals(Order.SINGLE, bond3.getOrder());
        Assert.assertEquals(Order.SINGLE, bond4.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond5.getOrder());
        Assert.assertEquals(Order.SINGLE, bond6.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond7.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond8.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C6H6/c1-2-4-6-5-3-1/h1-6H</div>.
     */
    @Test
    public void testBenzene() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom1, 1);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom3, 1);
        IAtom atom4 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom4, 1);
        IAtom atom5 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom5, 1);
        IAtom atom6 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom6, 1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom6);
        IBond bond6 = new Bond(atom6, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addAtom(atom6);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);
        enol.addBond(bond6);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond1
                .getOrder().numeric() + bond6.getOrder().numeric()); // around atom1
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond1
                .getOrder().numeric() + bond2.getOrder().numeric()); // around atom2
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond2
                .getOrder().numeric() + bond3.getOrder().numeric()); // around atom3
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond3
                .getOrder().numeric() + bond4.getOrder().numeric()); // around atom4
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond4
                .getOrder().numeric() + bond5.getOrder().numeric()); // around atom5
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond5
                .getOrder().numeric() + bond6.getOrder().numeric()); // around atom6
    }

    /**
     * Test <div class="inchi">InChI=1/C4H5N/c1-2-4-5-3-1/h1-5H</div>.
     */
    @Test
    public void testPyrrole() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom1, 1);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom3, 1);
        IAtom atom4 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom4, 1);
        IAtom atom5 = new Atom(Elements.NITROGEN);
        addHydrogens(enol, atom5, 1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.DOUBLE, bond1.getOrder());
        Assert.assertEquals(Order.SINGLE, bond2.getOrder());
        Assert.assertEquals(Order.DOUBLE, bond3.getOrder());
        Assert.assertEquals(Order.SINGLE, bond4.getOrder());
        Assert.assertEquals(Order.SINGLE, bond5.getOrder());
    }

    /**
     * Test <div class="inchi">InChI=1/C5H5N/c1-2-4-6-5-3-1/h1-5H</div>.
     */
    @Ignore("previously disabled 'xtest'")
    public void xtestPyridine() throws Exception {
        IAtomContainer enol = new AtomContainer();

        // atom block
        IAtom atom1 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom1, 1);
        IAtom atom2 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom2, 1);
        IAtom atom3 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom3, 1);
        IAtom atom4 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom4, 1);
        IAtom atom5 = new Atom(Elements.NITROGEN);
        IAtom atom6 = new Atom(Elements.CARBON);
        addHydrogens(enol, atom6, 1);

        // bond block
        IBond bond1 = new Bond(atom1, atom2);
        IBond bond2 = new Bond(atom2, atom3);
        IBond bond3 = new Bond(atom3, atom4);
        IBond bond4 = new Bond(atom4, atom5);
        IBond bond5 = new Bond(atom5, atom6);
        IBond bond6 = new Bond(atom6, atom1);

        enol.addAtom(atom1);
        enol.addAtom(atom2);
        enol.addAtom(atom3);
        enol.addAtom(atom4);
        enol.addAtom(atom5);
        enol.addAtom(atom6);
        enol.addBond(bond1);
        enol.addBond(bond2);
        enol.addBond(bond3);
        enol.addBond(bond4);
        enol.addBond(bond5);
        enol.addBond(bond6);

        // now have the algorithm have a go at it
        dboTool.saturate(enol);

        // now check whether it did the right thing
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond1
                .getOrder().numeric() + bond6.getOrder().numeric()); // around atom1
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond1
                .getOrder().numeric() + bond2.getOrder().numeric()); // around atom2
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond2
                .getOrder().numeric() + bond3.getOrder().numeric()); // around atom3
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond3
                .getOrder().numeric() + bond4.getOrder().numeric()); // around atom4
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond4
                .getOrder().numeric() + bond5.getOrder().numeric()); // around atom5
        Assert.assertEquals(Order.SINGLE.numeric() + Order.DOUBLE.numeric(), bond5
                .getOrder().numeric() + bond6.getOrder().numeric()); // around atom6
    }

    private void addHydrogens(IAtomContainer container, IAtom atom, int numberOfHydrogens) {
        for (int i = 0; i < numberOfHydrogens; i++)
            container.addBond(atom.getBuilder().newInstance(IBond.class, atom,
                    atom.getBuilder().newInstance(IAtom.class, "H")));
    }

}

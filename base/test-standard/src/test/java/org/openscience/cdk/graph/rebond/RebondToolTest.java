/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph.rebond;

import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Checks the functionality of the RebondTool.
 *
 * @cdk.module test-standard
 */
public class RebondToolTest extends CDKTestCase {

    public RebondToolTest() {
        super();
    }

    @Test
    public void testRebondTool_double_double_double() {
        RebondTool rebonder = new RebondTool(2.0, 0.5, 0.5);
        Assert.assertNotNull(rebonder);
    }

    @Test
    public void testRebond_IAtomContainer() throws Exception {
        RebondTool rebonder = new RebondTool(2.0, 0.5, 0.5);
        IAtomContainer methane = new AtomContainer();
        methane.addAtom(new Atom("C", new Point3d(0.0, 0.0, 0.0)));
        methane.addAtom(new Atom("H", new Point3d(0.6, 0.6, 0.6)));
        methane.addAtom(new Atom("H", new Point3d(-0.6, -0.6, 0.6)));
        methane.addAtom(new Atom("H", new Point3d(0.6, -0.6, -0.6)));
        methane.addAtom(new Atom("H", new Point3d(-0.6, 0.6, -0.6)));

        // configure atoms
        AtomTypeFactory factory = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/jmol_atomtypes.txt",
                methane.getBuilder());
        //org.openscience.cdk.interfaces.IAtom[] atoms = methane.getAtoms();
        for (int i = 0; i < methane.getAtomCount(); i++) {
            factory.configure(methane.getAtom(i));
        }
        // rebond
        rebonder.rebond(methane);

        Assert.assertEquals(5, methane.getAtomCount());
        Assert.assertEquals(4, methane.getBondCount());
    }
}

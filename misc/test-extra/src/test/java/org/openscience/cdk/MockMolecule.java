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

import javax.vecmath.Point3d;

/**
 * A molecule which looks like a cube. The center
 * of the cube is located at (0,0,0) and the corners have a length of 2.
 *
 * @cdk.module test-extra
 *
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
public class MockMolecule extends AtomContainer {

    private static final long serialVersionUID = -3475592461591784619L;

    public MockMolecule() {
        super();

        addAtom(new Atom("C", new Point3d(1, 1, -1)));
        addAtom(new Atom("C", new Point3d(1, -1, -1)));
        addAtom(new Atom("C", new Point3d(-1, 1, -1)));
        addAtom(new Atom("C", new Point3d(-1, -1, -1)));
        addAtom(new Atom("C", new Point3d(1, 1, 1)));
        addAtom(new Atom("C", new Point3d(1, -1, 1)));
        addAtom(new Atom("C", new Point3d(-1, 1, 1)));
        addAtom(new Atom("C", new Point3d(-1, -1, 1)));
    }
}

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002  The Chemistry Development Kit (CDK) project
 *
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.openscience.cdk.test.libio.jmol;

import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.libio.jmol.*;
import org.openscience.cdk.smiles.*;
import junit.framework.*;
import org.openscience.cdk.isomorphism.IsomorphismTester;

public class JmolLibIOTest extends TestCase {

    public JmolLibIOTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(JmolLibIOTest.class);
    }


    public void testAtom() {
        Atom a = new Atom("C");
        a.setX3D(1.0);
        a.setY3D(2.0);
        a.setZ3D(3.0);

        org.openscience.jmol.Atom converted = Convertor.convert(a);
        Atom reverted = Convertor.convert(converted);

        assertTrue(a.getX3D() == reverted.getX3D());
        assertTrue(a.getY3D() == reverted.getY3D());
        assertTrue(a.getZ3D() == reverted.getZ3D());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(JmolLibIOTest.class));
    }
}

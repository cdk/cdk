/* Copyright (C) 2011 Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.layout;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;

/**
 * @author maclean
 * @cdk.module test-sdg
 */
public class AtomPlacerTest extends CDKTestCase {
    
    @Test
    public void emptyAtomsListTest() {
        List<IAtom> atoms = new ArrayList<IAtom>();
        // switch on debugging, to see if NPE is thrown
        System.setProperty("cdk.debugging", "true");
        System.setProperty("cdk.debug.stdout", "true");
        AtomPlacer placer = new AtomPlacer();
        boolean npeThrown = false;
        try {
            placer.populatePolygonCorners(atoms, new Point2d(0,0), 0, 10, 10);
        } catch (NullPointerException npe) {
            npeThrown = true;
        }
        Assert.assertFalse("Null pointer for empty atoms list", npeThrown);
    }
    
    @Test
    public void triangleTest() {
        List<IAtom> atoms = new ArrayList<IAtom>();
        atoms.add(new Atom("C"));
        atoms.add(new Atom("C"));
        atoms.add(new Atom("C"));
        AtomPlacer placer = new AtomPlacer();
        placer.populatePolygonCorners(atoms, new Point2d(0,0), 0, 10, 10);
        for (IAtom atom : atoms) {
            Assert.assertNotNull(atom.getPoint2d());
        }
    }

}

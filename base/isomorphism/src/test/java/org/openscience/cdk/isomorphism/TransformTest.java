/*
 * Copyright (C) 2022 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.isomorphism;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.TransformOp.Type;
import org.openscience.cdk.templates.TestMoleculeFactory;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransformTest {

    @Test
    void testSimpleRingForming() {
        IAtomContainer pentane1 = TestMoleculeFactory.makeAlkane(5);
        IAtomContainer pentane2 = TestMoleculeFactory.makeAlkane(5);
        Transform transform = new Transform(Pattern.findSubstructure(pentane1),
                Arrays.asList(new TransformOp(Type.NewAtom, 6, 17, 0),
                              new TransformOp(Type.NewBond, 1, 5, 1),
                              new TransformOp(Type.BondOrder, 1, 2, 2)));
        assertTrue(transform.apply(pentane2));
        assertEquals(pentane2.getBondCount(), 5);
        assertEquals(pentane2.getBond(0).getOrder(), IBond.Order.DOUBLE);
    }

}
/*
 * Copyright (C) 2023 John Mayfield
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TransformOpTest {

    @Test
    void testToString() {
        Assertions.assertEquals("Mass{13@1}", new TransformOp(TransformOp.Type.Mass, 1, 13).toString());
        Assertions.assertEquals("MoveH{1=>2}", new TransformOp(TransformOp.Type.MoveH, 1, 2).toString());
        Assertions.assertEquals("NewAtom{[CH3@1]}", new TransformOp(TransformOp.Type.NewAtom, 1, 6, 3).toString());
        Assertions.assertEquals("NewAtom{[cH1@1]}", new TransformOp(TransformOp.Type.NewAtom, 1, 6, 1, 1).toString());
        Assertions.assertEquals("NewBond{1-2}", new TransformOp(TransformOp.Type.NewBond, 1, 2, 1).toString());
        Assertions.assertEquals("NewBond{1=2}", new TransformOp(TransformOp.Type.NewBond, 1, 2, 2).toString());
        Assertions.assertEquals("NewBond{1#2}", new TransformOp(TransformOp.Type.NewBond, 1, 2, 3).toString());
        Assertions.assertEquals("NewBond{1,2,order=4}", new TransformOp(TransformOp.Type.NewBond, 1, 2, 4).toString());
    }
}
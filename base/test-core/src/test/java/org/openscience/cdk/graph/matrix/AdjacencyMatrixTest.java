/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.graph.matrix;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @cdk.module test-core
 */
public class AdjacencyMatrixTest extends CDKTestCase {

    private static SmilesParser sp;

    @BeforeClass
    public static void getSmilesParser() {
        sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
    }

    @Test
    public void testGetMatrix_IAtomContainer() throws Exception {
        IAtomContainer container = sp.parseSmiles("C1CC1");
        int[][] matrix = AdjacencyMatrix.getMatrix(container);
        Assert.assertEquals(3, matrix.length);
        Assert.assertEquals(3, matrix[0].length);
        Assert.assertEquals(0, matrix[0][0]);
        Assert.assertEquals(1, matrix[0][2]);
        Assert.assertEquals(1, matrix[0][1]);
    }

}

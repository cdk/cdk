/* Copyright (C) 2012  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import org.junit.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class AtomGroupTests extends CDKTestCase {

    public IAtomContainer getMol(String smiles) throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        return parser.parseSmiles(smiles);
    }

    public void test(IAtomContainer mol, int expected) {
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        PermutationGroup group = refiner.getAutomorphismGroup(mol);
        Assert.assertEquals(expected, group.order());
    }

    @Test
    public void carbonSingleTree() throws InvalidSmilesException {
        test(getMol("CC(C)C(C)C"), 8);
    }

    @Test
    public void hetatmSingleTree() throws InvalidSmilesException {
        test(getMol("CC(O)C(C)C"), 2);
    }

    @Test
    public void carbonMultipleTree() throws InvalidSmilesException {
        test(getMol("CC(=C)C(C)C"), 2);
    }

    @Test
    public void carbonSingleCycle() throws InvalidSmilesException {
        test(getMol("C1CCC1"), 8);
    }

    @Test
    public void hetatmMultipleTree() throws InvalidSmilesException {
        test(getMol("CC(=O)C(C)C"), 2);
    }

    @Test
    public void hetatmSingleCycle() throws InvalidSmilesException {
        test(getMol("C1COC1"), 2);
    }

    @Test
    public void carbonMultipleCycle() throws InvalidSmilesException {
        test(getMol("C1=CC=C1"), 4);
    }

    @Test
    public void hetatmMultipleCycle() throws InvalidSmilesException {
        test(getMol("C1=OC=C1"), 1);
    }

}

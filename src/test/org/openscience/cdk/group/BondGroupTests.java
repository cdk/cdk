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

import junit.framework.Assert;

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
public class BondGroupTests extends CDKTestCase {
    
    public IAtomContainer getMol(String smiles) throws InvalidSmilesException {
        SmilesParser parser = 
            new SmilesParser(DefaultChemObjectBuilder.getInstance());
        return parser.parseSmiles(smiles);
    }
    
    public void test(IAtomContainer mol, int expected) {
        BondDiscretePartitionRefiner refiner = new BondDiscretePartitionRefiner();
        PermutationGroup group = refiner.getAutomorphismGroup(mol);
        Assert.assertEquals(expected, group.order());
    }

    @Test
    public void cycloButane() throws InvalidSmilesException {
        test(getMol("C1CCC1"), 8);
    }
    
    @Test
    public void cycloButadiene() throws InvalidSmilesException {
        test(getMol("C1=CC=C1"), 4);
    }
    
    @Test
    public void cycloPentadiene() throws InvalidSmilesException {
        test(getMol("C1=CCC=C1"), 2);
    }
    
    @Test
    public void napthaleneA() throws InvalidSmilesException {
        test(getMol("C1=CC=C2C=CC=CC2=C1"), 2);
    }
    
    @Test
    public void napthaleneB() throws InvalidSmilesException {
        test(getMol("C1=CC=CC2=C1C=CC=C2"), 4);
    }
    
}

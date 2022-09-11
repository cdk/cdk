/* Copyright (c) 2018 Kazuya Ujihara <ujihara.kazuya@gmail.com>
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

package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

class FractionalCSP3DescriptorTest extends MolecularDescriptorTest {
    FractionalCSP3DescriptorTest() {}
    
    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(FractionalCSP3Descriptor.class);
    }
    
    static class SmilesValue {
        SmilesValue(String smiles, double value) {
            this.smiles = smiles;
            this.value = value;
        }
        final String smiles;
        final double value;
    }
    
    private static final SmilesValue[] table = new SmilesValue[] {
            new SmilesValue("[H][H]", 0),
            new SmilesValue("O", 0),
            new SmilesValue("C1=CC=CC=C1", 0),
            new SmilesValue("C1=CN=CC=C1", 0),
            new SmilesValue("CC1=CC=CC(C)=N1", 0.29),
            new SmilesValue("CC1CCCC(C)N1", 1),
            new SmilesValue("CC1=NC(NC(NC2CN(C3=CC=CC(F)=C3)C(C2)=O)=O)=CC=C1", 0.24),
    };
    
    @Test
    void testFractionalCSP3Descriptor() throws CDKException {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        for (SmilesValue e: table) {
            IAtomContainer mol = sp.parseSmiles(e.smiles);
            DoubleResult result = (DoubleResult)descriptor.calculate(mol).getValue();
            Assertions.assertEquals(e.value, result.doubleValue(), 0.01);
        }
    }
}

/* 
 * Copyright (C) 2010 Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fragment;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test fragment utils
 *
 * @cdk.module test-fragment
 */
public class FragmentUtilsTest extends CDKTestCase {

    static SmilesParser smilesParser;

    @BeforeClass
    public static void setup() {
        smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testSplit() throws CDKException {
        IAtomContainer mol = smilesParser.parseSmiles("C1CC1C2CCC2");
        SpanningTree st = new SpanningTree(mol);
        IRingSet rings = st.getAllRings();
        IBond splitBond = null;
        for (int i = 0 ; i < mol.getBondCount(); i++) {
            if (rings.getRings(mol.getBond(i)).getAtomContainerCount() == 0) {
                splitBond = mol.getBond(i);
                break;
            }
        }
        List<IAtomContainer> frags = FragmentUtils.splitMolecule(mol, splitBond);
        SmilesGenerator sg = new SmilesGenerator(true);
        Set<String> uniqueFrags = new HashSet<String>();
        for (IAtomContainer frag : frags) {
            uniqueFrags.add(sg.createSMILES(frag));
        }
        Assert.assertEquals(2, uniqueFrags.size());
        Assert.assertTrue(uniqueFrags.contains("C1CC1"));
        Assert.assertTrue(uniqueFrags.contains("C1CCC1"));
    }
}
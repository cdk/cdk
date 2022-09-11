/* Copyright (C) 2009-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.interfaces;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibSubStructureHandler;
import org.openscience.cdk.smsd.tools.MolHandler;

import org.junit.jupiter.api.Test;

/**
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 * @cdk.module test-smsd
 */
public abstract class AbstractSubGraphTest {

    private static AbstractSubGraphTest algorithm = null;

    /**
     *
     * @param algorithm
     */
    static void setMCSAlgorithm(AbstractSubGraphTest algorithm) {
        AbstractSubGraphTest.algorithm = algorithm;
    }

    public AbstractSubGraphTest() {}

    @BeforeAll
    static void setUpClass() throws Exception {}

    @AfterAll
    static void tearDownClass() throws Exception {}

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    /**
     * Test of isSubgraph method, of class AbstractSubGraph.
     * @throws InvalidSmilesException
     */
    @Test
    public void testIsSubgraph() throws InvalidSmilesException {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target = sp.parseSmiles("C\\C=C/Nc1cccc(c1)N(O)\\C=C\\C\\C=C\\C=C/C");
        IAtomContainer queryac = sp.parseSmiles("Nc1ccccc1");

        VFlibSubStructureHandler smsd1 = new VFlibSubStructureHandler();
        MolHandler mol1 = new MolHandler(queryac, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        smsd1.set(mol1, mol2);
        Assertions.assertEquals(true, smsd1.isSubgraph(true));
    }

    private class ISubGraphImpl extends AbstractSubGraph {

        @Override
        public boolean isSubgraph(boolean bondMatch) {
            return false;
        }
    }
}

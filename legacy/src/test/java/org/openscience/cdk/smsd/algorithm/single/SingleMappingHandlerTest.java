/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.single;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smsd.Isomorphism;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithmTest;
import org.openscience.cdk.smsd.interfaces.Algorithm;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * Unit testing for the {@link SingleMappingHandler} class.
 *
 * @author     egonw
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 *
 */
public class SingleMappingHandlerTest extends AbstractMCSAlgorithmTest {

    @BeforeAll
    static void setMCSAlgorithm() {
        AbstractMCSAlgorithmTest.setMCSAlgorithm(new SingleMappingHandler(true));
    }

    /**
     * Test of set method, of class SingleMappingHandler.
     * @throws Exception
     */
    @Test
    void testSet_IAtomContainer_IAtomContainer() throws Exception {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        Assertions.assertNotNull(instance.getFirstAtomMapping());
    }

    /**
     * Test of set method, of class SingleMappingHandler.
     * @throws CDKException
     * @throws IOException
     */
    @Test
    void testSet_String_String() throws CDKException, IOException {
        String molfile = "org/openscience/cdk/smsd/algorithm/decalin.mol";
        String queryfile = "org/openscience/cdk/smsd/algorithm/decalin.mol";
        IAtomContainer query = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(molfile);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(query);
        ins = this.getClass().getClassLoader().getResourceAsStream(queryfile);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        reader.read(target);

        Isomorphism smsd1 = new Isomorphism(Algorithm.DEFAULT, true);
        smsd1.init(query, target, true, true);
        smsd1.setChemFilters(true, true, true);
        double score = 1.0;
        Assertions.assertEquals(score, smsd1.getTanimotoSimilarity(), 0.0001);
    }

    /**
     * Test of set method, of class SingleMappingHandler.
     */
    @Test
    void testSet_MolHandler_MolHandler() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        MolHandler source1 = new MolHandler(source, true, true);
        MolHandler target1 = new MolHandler(target, true, true);

        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        instance.set(source1, target1);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getFirstAtomMapping());
    }

    /**
     * Test of searchMCS method, of class SingleMappingHandler.
     */
    @Test
    @Override
    public void testSearchMCS() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getAllMapping());
        Assertions.assertEquals(1, instance.getAllMapping().size());
    }

    /**
     * Test of getAllMapping method, of class SingleMappingHandler.
     */
    @Test
    void testGetAllMapping() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getAllMapping());
    }

    /**
     * Test of getFirstMapping method, of class SingleMappingHandler.
     */
    @Test
    void testGetFirstMapping() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getFirstMapping());
    }

    /**
     * Test of getAllAtomMapping method, of class SingleMappingHandler.
     */
    @Test
    void testGetAllAtomMapping() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getAllAtomMapping());
    }

    /**
     * Test of getFirstAtomMapping method, of class SingleMappingHandler.
     */
    @Test
    void testGetFirstAtomMapping() {
        IAtom atomSource = new Atom("R");
        IAtom atomTarget = new Atom("R");
        IAtomContainer source = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        source.addAtom(atomSource);
        IAtomContainer target = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        target.addAtom(atomTarget);
        boolean removeHydrogen = false;
        SingleMappingHandler instance = new SingleMappingHandler(removeHydrogen);
        MolHandler mol1 = new MolHandler(source, true, true);
        MolHandler mol2 = new MolHandler(target, true, true);
        instance.set(mol1, mol2);
        instance.searchMCS(true);
        Assertions.assertNotNull(instance.getFirstAtomMapping());
    }
}

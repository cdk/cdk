/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.layout;

import java.io.InputStream;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 *  Description of the Class
 *
 * @cdk.module test-sdg
 *
 *@author     steinbeck
 *@cdk.created    September 4, 2003
 * @cdk.require java1.4+
 */
public class OverlapResolverTest extends CDKTestCase {

    /**
     *  Description of the Field
     */
    public boolean              standAlone = false;
    private static ILoggingTool logger     = LoggingToolFactory.createLoggingTool(OverlapResolverTest.class);
    StructureDiagramGenerator   sdg        = null;

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testResolveOverlap1() throws Exception {
        logger.debug("Test case with atom clash");
        String filename = "data/cml/overlaptest.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer atomContainer = (IAtomContainer) ChemFileManipulator.getAllAtomContainers(chemFile).get(0);

        OverlapResolver or = new OverlapResolver();
        double score = new OverlapResolver().getAtomOverlapScore(atomContainer, new Vector());
        logger.debug("Overlap Score before treatment: " + score);
        Assert.assertTrue(score > 0);
        or.resolveOverlap(atomContainer, null);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), false);
        score = new OverlapResolver().getAtomOverlapScore(atomContainer, new Vector());
        logger.debug("Overlap Score after treatment: " + score);
        Assert.assertEquals(0.0, score, 0.00001);
        logger.debug("End of test case with atom clash");

    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testResolveOverlap2() throws Exception {
        logger.debug("Test case with neither bond nor atom overlap");
        String filename = "data/cml/overlaptest2.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer atomContainer = (IAtomContainer) ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), false);
        double score = new OverlapResolver().getOverlapScore(atomContainer, new Vector(), new Vector());
        Assert.assertEquals(0.0, score, 0.0001);
        logger.debug("End of test case with neither bond nor atom overlap");

    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testResolveOverlap3() throws Exception {
        logger.debug("Test case with bond overlap");
        String filename = "data/cml/overlaptest3.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer atomContainer = (IAtomContainer) ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), false);
        double score = new OverlapResolver().getBondOverlapScore(atomContainer, new Vector());
        Assert.assertTrue(score > 0);
        logger.debug("End of test case with bond overlap");

    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testResolveOverlap4() throws Exception {
        double overlapScore = 0;
        logger.debug("Test case with atom clash");
        String filename = "data/cml/overlaptest.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile chemFile = (IChemFile) reader.read(new ChemFile());
        IAtomContainer atomContainer = (IAtomContainer) ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), false);
        OverlapResolver or = new OverlapResolver();
        overlapScore = or.resolveOverlap(atomContainer, null);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), false);
        Assert.assertEquals(0.0, overlapScore, 0.0001);
        logger.debug("End of test case with atom clash");

    }

    /**
     *  A unit test for JUnit
     *
     *@exception  Exception  Description of the Exception
     */
    @Test
    public void testResolveOverlap5() throws Exception {
        double overlapScore = 0;
        logger.debug("Test case with atom clash");
        IAtomContainer atomContainer = new SmilesParser(DefaultChemObjectBuilder.getInstance())
                .parseSmiles("OC4C(N2C1=C(C(=NC(=N1)SC)SC)C3=C2N=CN=C3N)OC(C4O)CO");
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        sdg.setMolecule(new AtomContainer(atomContainer));
        sdg.generateCoordinates();
        atomContainer = sdg.getMolecule();
        OverlapResolver or = new OverlapResolver();
        overlapScore = or.resolveOverlap(atomContainer, null);
        //MoleculeViewer2D.display(new AtomContainer(atomContainer), true);
        Assert.assertEquals(0.0, overlapScore, 0.0001);
        logger.debug("End of test case with atom clash");

    }

}

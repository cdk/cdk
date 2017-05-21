/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may
 * distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.RGroupQueryFormat;
import org.openscience.cdk.isomorphism.matchers.RGroup;
import org.openscience.cdk.isomorphism.matchers.RGroupList;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * JUnit tests for {@link org.openscience.cdk.io.RGroupQueryReader}.
 * @cdk.module test-io
 * @author Mark Rijnbeek
 */
public class RGroupQueryReaderTest extends SimpleChemObjectReaderTest {

    public RGroupQueryReaderTest() {}

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(RGroupQueryReaderTest.class);

    @BeforeClass
    public static void setup() {
        setSimpleChemObjectReader(new RGroupQueryReader(), "data/mdl/rgfile.1.mol");
    }

    @Test
    public void testAccepts() {
        RGroupQueryReader reader = new RGroupQueryReader();
        Assert.assertFalse(reader.accepts(AtomContainer.class));
        Assert.assertTrue(reader.accepts(RGroupQuery.class));
    }

    @Test
    @Override
    public void testAcceptsAtLeastOneChemObjectClass() {
        RGroupQueryReader reader = new RGroupQueryReader();
        Assert.assertTrue(reader.accepts(RGroupQuery.class));
    }

    @Override
    public void testAcceptsAtLeastOneDebugObject() {}

    @Override
    public void testAcceptsAtLeastOneNonotifyObject() {}

    /**
     * Test that the format factory guesses the correct IChemFormat
     * based on the file content.
     *
     * @throws Exception
     */
    @Test
    public void testRGFileFormat() throws Exception {
        String filename = "data/mdl/rgfile.1.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        IChemFormat format = new FormatFactory().guessFormat(new BufferedInputStream(ins));
        Assert.assertEquals(format.getClass(), RGroupQueryFormat.class);
    }

    /**
     * Test parsing of RGFile rgfile.1.mol.
     * Simple R-group query file.
     */
    @Test
    public void testRgroupQueryFile1() throws Exception {
        String filename = "data/mdl/rgfile.1.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 1);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 7);

        for (IAtom at : rGroupQuery.getAllRgroupQueryAtoms()) {
            if (at instanceof PseudoAtom) {
                Assert.assertEquals(((PseudoAtom) at).getLabel(), "R1");
                Map<IAtom, Map<Integer, IBond>> rootApo = rGroupQuery.getRootAttachmentPoints();
                Map<Integer, IBond> apoBonds = rootApo.get(at);
                Assert.assertEquals(apoBonds.size(), 1);
                // Assert that the root attachment is the bond between R1 and P
                for (IBond bond : rGroupQuery.getRootStructure().bonds()) {
                    if (bond.contains(at)) {
                        Assert.assertEquals(bond, apoBonds.get(1));
                        for (IAtom atInApo : bond.atoms()) {
                            Assert.assertTrue(atInApo.getSymbol().equals("R") || atInApo.getSymbol().equals("P"));
                        }
                    }
                }
            }
        }

        Iterator<Integer> itr = rGroupQuery.getRGroupDefinitions().keySet().iterator();
        int val_1 = itr.next();
        Assert.assertEquals(val_1, 1);
        RGroupList rList = rGroupQuery.getRGroupDefinitions().get(val_1);
        Assert.assertEquals(rList.getOccurrence(), "0,1-3");

        List<RGroup> rGroups = rList.getRGroups();
        Assert.assertEquals(rGroups.get(0).getFirstAttachmentPoint().getSymbol(), "N");
        Assert.assertEquals(rGroups.get(1).getFirstAttachmentPoint().getSymbol(), "O");
        Assert.assertEquals(rGroups.get(2).getFirstAttachmentPoint().getSymbol(), "S");

        Assert.assertNull(rGroups.get(0).getSecondAttachmentPoint());
        Assert.assertNull(rGroups.get(1).getSecondAttachmentPoint());
        Assert.assertNull(rGroups.get(2).getSecondAttachmentPoint());

        List<IAtomContainer> configurations = rGroupQuery.getAllConfigurations();
        Assert.assertEquals(configurations.size(), 4);

        //RestH is set to true for R1, so with zero substitutes, the phosphor should get the restH flag set to true.
        boolean restH_Identified = false;
        for (IAtomContainer atc : configurations) {
            if (atc.getAtomCount() == 6) {
                for (IAtom atom : atc.atoms()) {
                    if (atom.getSymbol().equals("P")) {
                        Assert.assertNotNull(atom.getProperty(CDKConstants.REST_H));
                        Assert.assertEquals(atom.getProperty(CDKConstants.REST_H), true);
                        restH_Identified = true;
                    }
                }
            }
        }
        Assert.assertTrue(restH_Identified);
    }

    /**
     * Test parsing of RGFile rgfile.2.mol.
     * More elaborate R-group query file.
     */
    @Test
    public void testRgroupQueryFile2() throws Exception {
        String filename = "data/mdl/rgfile.2.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 3);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 14);
        Assert.assertEquals(rGroupQuery.getRootAttachmentPoints().size(), 4);

        List<IAtom> rGroupQueryAtoms = rGroupQuery.getAllRgroupQueryAtoms();
        Assert.assertEquals(rGroupQueryAtoms.size(), 4);

        rGroupQueryAtoms = rGroupQuery.getRgroupQueryAtoms(1);
        Assert.assertEquals(rGroupQueryAtoms.size(), 1);

        for (IAtom at : rGroupQuery.getAllRgroupQueryAtoms()) {
            if (at instanceof PseudoAtom) {
                Assert.assertTrue(RGroupQuery.isValidRgroupQueryLabel(((PseudoAtom) at).getLabel()));
                int rgroupNum = new Integer((((PseudoAtom) at).getLabel()).substring(1));
                Assert.assertTrue(rgroupNum == 1 || rgroupNum == 2 || rgroupNum == 11);
                switch (rgroupNum) {
                    case 1: {
                        //Test: R1 has two attachment points, defined by AAL
                        Map<IAtom, Map<Integer, IBond>> rootApo = rGroupQuery.getRootAttachmentPoints();
                        Map<Integer, IBond> apoBonds = rootApo.get(at);
                        Assert.assertEquals(apoBonds.size(), 2);
                        Assert.assertEquals(apoBonds.get(1).getOther(at).getSymbol(), "N");
                        Assert.assertTrue(apoBonds.get(2).getOther(at).getSymbol().equals("C"));
                        //Test: Oxygens are the 2nd APO's for R1
                        RGroupList rList = rGroupQuery.getRGroupDefinitions().get(1);
                        Assert.assertEquals(rList.getRGroups().size(), 2);
                        List<RGroup> rGroups = rList.getRGroups();
                        Assert.assertEquals(rGroups.get(0).getSecondAttachmentPoint().getSymbol(), "O");
                        Assert.assertEquals(rGroups.get(1).getSecondAttachmentPoint().getSymbol(), "O");
                        Assert.assertFalse(rList.isRestH());
                    }
                        break;
                    case 2: {
                        RGroupList rList = rGroupQuery.getRGroupDefinitions().get(2);
                        Assert.assertEquals(rList.getRGroups().size(), 2);
                        Assert.assertEquals(rList.getOccurrence(), "0,2");
                        Assert.assertEquals(rList.getRequiredRGroupNumber(), 11);
                        Assert.assertFalse(rList.isRestH());
                    }
                        break;
                    case 11: {
                        RGroupList rList = rGroupQuery.getRGroupDefinitions().get(11);
                        Assert.assertEquals(rList.getRGroups().size(), 1);
                        Assert.assertEquals(rList.getRequiredRGroupNumber(), 0);
                        Assert.assertTrue(rList.isRestH());

                        List<RGroup> rGroups = rList.getRGroups();
                        Assert.assertEquals(rGroups.get(0).getFirstAttachmentPoint().getSymbol(), "Pt");
                        Assert.assertEquals(rGroups.get(0).getSecondAttachmentPoint(), null);
                    }
                        break;
                }
            }
        }

        List<IAtomContainer> configurations = rGroupQuery.getAllConfigurations();
        Assert.assertEquals(configurations.size(), 12);

        //Test restH values
        int countRestHForSmallestConfigurations = 0;
        for (IAtomContainer atc : configurations) {
            if (atc.getAtomCount() == 13) { // smallest configuration
                for (IAtom atom : atc.atoms()) {
                    if (atom.getProperty(CDKConstants.REST_H) != null) {
                        countRestHForSmallestConfigurations++;
                        if (atom.getSymbol().equals("P"))
                            Assert.assertEquals(atom.getProperty(CDKConstants.REST_H), true);
                    }
                }
            }
        }
        Assert.assertEquals(countRestHForSmallestConfigurations, 6);

    }

    /**
     * Test parsing of RGFile rgfile.3.mol.
     * This R-group query has R1 bound double twice, and has AAL lines to parse.
     */
    @Test
    public void testRgroupQueryFile3() throws Exception {
        String filename = "data/mdl/rgfile.3.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 1);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 10);
        Assert.assertEquals(rGroupQuery.getRootAttachmentPoints().size(), 2);

        Assert.assertEquals(rGroupQuery.getAllConfigurations().size(), 8);

        //Test correctness AAL lines
        for (IAtom at : rGroupQuery.getRgroupQueryAtoms(1)) {
            if (at instanceof PseudoAtom) {
                Assert.assertEquals(((PseudoAtom) at).getLabel(), "R1");

                Map<Integer, IBond> apoBonds = rGroupQuery.getRootAttachmentPoints().get(at);
                Assert.assertEquals(apoBonds.size(), 2);

                IAtom boundAtom1 = apoBonds.get(1).getOther(at);
                Assert.assertTrue(boundAtom1.getSymbol().equals("Te") || boundAtom1.getSymbol().equals("S"));

                IAtom boundAtom2 = apoBonds.get(2).getOther(at);
                Assert.assertTrue(boundAtom2.getSymbol().equals("Po") || boundAtom2.getSymbol().equals("O"));
            }
        }

        // Test that there only two Rgroup query atoms (R#). The third R is a
        // pseudo atom, but because it is not numbered it is not part of any
        // query condition.
        List<IAtom> allrGroupQueryAtoms = rGroupQuery.getAllRgroupQueryAtoms();
        Assert.assertEquals(allrGroupQueryAtoms.size(), 2);
    }

    /**
     * Test parsing of RGFile rgfile.4.mol.
     * This R-group query has its R# atom detached, no bounds.
     */
    @Test
    public void testRgroupQueryFile4() throws Exception {
        String filename = "data/mdl/rgfile.4.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 1);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 6);

        List<IAtom> allrGroupQueryAtoms = rGroupQuery.getAllRgroupQueryAtoms();
        Assert.assertEquals(allrGroupQueryAtoms.size(), 1);
        RGroupList rList = rGroupQuery.getRGroupDefinitions().get(1);
        Assert.assertEquals(rList.getRGroups().size(), 2);
        Assert.assertEquals(rList.getRequiredRGroupNumber(), 0);
        Assert.assertFalse(rList.isRestH());
        Assert.assertEquals(rGroupQuery.getRootAttachmentPoints().size(), 0);
        Assert.assertTrue(rGroupQuery.areSubstituentsDefined());

        Assert.assertEquals(rGroupQuery.getAllConfigurations().size(), 2);

        // This query has a detached R-group, test for empty attachment points
        List<RGroup> rGroups = rList.getRGroups();
        Assert.assertEquals(rGroups.get(0).getFirstAttachmentPoint(), null);
        Assert.assertEquals(rGroups.get(0).getSecondAttachmentPoint(), null);
        Assert.assertEquals(rGroups.get(1).getFirstAttachmentPoint(), null);
        Assert.assertEquals(rGroups.get(1).getSecondAttachmentPoint(), null);
    }

    /**
     * Test parsing of RGFile rgfile.5.mol.
     * This exotic R-group query files has many R# groups and subsitutes,
     * to test mainly for getting all valid configurations.
     */
    @Test
    @Category(SlowTest.class)
    public void testRgroupQueryFile5() throws Exception {
        String filename = "data/mdl/rgfile.5.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 4);

        //Test combinatorial explosion: R5 has many different configurations
        Assert.assertEquals(rGroupQuery.getAllConfigurations().size(), 17820);
    }

    /**
     * Test parsing of RGFile rgfile.6.mol.
     * This RGFile is incomplete, RGP lines are missing. We still want to
     * accept it (Symyx/ChemAxon software accepts it too).
     */
    @Test(expected = CDKException.class)
    public void testRgroupQueryFile6() throws Exception {
        String filename = "data/mdl/rgfile.6.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 3);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 14);

        // This file has missing $RGP blocks. You could argue that this is
        // thus not a legal query (ie missing query specifications)
        Assert.assertFalse(rGroupQuery.areSubstituentsDefined());

        //Getting for all configurations won't happen, because not all groups were set
        rGroupQuery.getAllConfigurations(); // Will raise exception

    }

    /**
     * Test parsing of RGFile rgfile.7.mol.
     * This RGFile has APO lines with value 3: both attachment points.<P>
     *
     * Also, R32 appears twice, but with different numbers of attachment.
     * The parser should not trip over this, and make nice configurations.
     */
    @Test
    public void testRgroupQueryFile7() throws Exception {
        String filename = "data/mdl/rgfile.7.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        RGroupQueryReader reader = new RGroupQueryReader(ins);
        RGroupQuery rGroupQuery = (RGroupQuery) reader.read(new RGroupQuery(DefaultChemObjectBuilder.getInstance()));
        reader.close();
        Assert.assertNotNull(rGroupQuery);
        Assert.assertEquals(rGroupQuery.getRGroupDefinitions().size(), 1);
        Assert.assertEquals(rGroupQuery.getRootStructure().getAtomCount(), 9);
        Assert.assertEquals(rGroupQuery.getAllConfigurations().size(), 20);

    }

}

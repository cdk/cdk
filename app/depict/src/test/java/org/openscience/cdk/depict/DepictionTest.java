/* Copyright (c) 2018 Saulius Gražulis <grazulis@ibt.lt>
 *               2021 Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.depict;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class DepictionTest {

    @Test
    void depictAsPs() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        String eps = dg.depict(ac).toPsStr();
        String nl = System.lineSeparator();
        String[] lines = eps.split(nl,3);
        Assertions.assertEquals("%!PS-Adobe-3.0", lines[0]);
        Assertions.assertEquals("%%Creator: FreeHEP Graphics2D Driver", lines[1]);
    }

    @Test
    void depictAsEps() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        String eps = dg.depict(ac).toEpsStr();
        String nl = System.lineSeparator();
        String[] lines = eps.split(nl,3);
        Assertions.assertEquals("%!PS-Adobe-3.0 EPSF-3.0", lines[0]);
        Assertions.assertTrue(lines[1].startsWith("%%BoundingBox: 0 0"));
    }

    @Test
    void depictAsEps2() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("C1CCCCC1CCCCC");
        String eps = dg.depict(ac).toEpsStr();
        String nl = System.lineSeparator();
        String[] lines = eps.split(nl,3);
        Assertions.assertEquals("%!PS-Adobe-3.0 EPSF-3.0", lines[0]);
        Assertions.assertEquals("%%BoundingBox: 0 0 92 33", lines[1]);
    }

    @Test
    void depictAsSvg() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        String svg = dg.depict(ac).toSvgStr();
        String nl = "\n";
        String[] lines = svg.split(nl,3);
        Assertions.assertEquals("<?xml version='1.0' encoding='UTF-8'?>", lines[0]);
        Assertions.assertEquals("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">", lines[1]);
    }

    @Test
    void connectMolWithTitlesInSvg() throws CDKException
    {
        final SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IReaction rxn = smilesParser.parseReactionSmiles("O.CCCCC(N)=O>>[NH4+].CCCCC([O-])=O");
        int count = 0;
        for(IAtomContainer reactant : rxn.getReactants().atomContainers()) {
            reactant.setProperty(CDKConstants.TITLE, "Reactant-" + ++count);
        }
        count = 0;
        for(IAtomContainer agent : rxn.getAgents().atomContainers()) {
            agent.setProperty(CDKConstants.TITLE, "Agent-" + ++count);
        }
        count = 0;
        for(IAtomContainer product : rxn.getProducts().atomContainers()) {
            product.setProperty(CDKConstants.TITLE, "Product-" + ++count);
        }
        DepictionGenerator dg = new DepictionGenerator().withMolTitle();
        String[] targetLines = dg.depict(rxn).toSvgStr("px").split("\n");

        List<String> stringsToFind = Arrays.asList(
            "<g class='title mol1'>",
            "<g class='title mol2'>",
            "<g class='title mol3'>",
            "<g class='title mol4'>"
        );
        List<String> foundmatches =
            Stream.of(targetLines)
                .map(String::trim)
                .filter(stringsToFind::contains).collect(Collectors.toList());
        Assertions.assertIterableEquals(stringsToFind, foundmatches);
    }

    @Test
    void testAtomPropertyAnnotationLabelCleanup() throws CDKException {
        DepictionGenerator depictionGenerator = new DepictionGenerator().withAtomValues();
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer atomContainer = smilesParser.parseSmiles("C1CCCCC1CCCCC");
        atomContainer.atoms().forEach(a -> a.setProperty(CDKConstants.COMMENT, "21"));
        depictionGenerator.depict(atomContainer);
        depictionGenerator.depict(atomContainer);
    }

    @Test
    void testAtomPropertyEmptyStringAnnotationLabel() throws CDKException {
        DepictionGenerator depictionGenerator = new DepictionGenerator().withAtomValues();
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer molecule = smilesParser.parseSmiles("C1CCCCC1CCCCC");
        molecule.atoms().forEach(a -> a.setProperty(CDKConstants.COMMENT, ""));
        depictionGenerator.depict(molecule);
    }

    @Disabled("Not stable between systems")
    @Test
    void depictUndirectedReactionAsSVG()
            throws CDKException, IOException
    {
        List<String> source = readResourceFile("rhea-net-reaction.svg");
        final SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IReaction rxn = smilesParser.parseReactionSmiles("O.CCCCC(N)=O>>[NH4+].CCCCC([O-])=O");
        rxn.setDirection(IReaction.Direction.UNDIRECTED);
        DepictionGenerator dg = new DepictionGenerator();
        String svg = dg.depict(rxn).toSvgStr();
        String[] targetLines = svg.split("\n");
        Assertions.assertIterableEquals(source, Arrays.asList(targetLines));
    }

    private List<String> readResourceFile(String resourceName)
            throws IOException
    {
        try(InputStream in = getClass().getResourceAsStream(resourceName);
            Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
            BufferedReader buff = new BufferedReader(rdr)) {
            return buff.lines().collect(Collectors.toList());
        }
    }
}

/*  Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                200?-2007  Egon Willighagen <egonw@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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
package org.openscience.cdk.smiles;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.StringTokenizer;

/**
 * Parses a SMILES {@cdk.cite SMILESTUT} string and an AtomContainer. The full
 * SSMILES subset {@cdk.cite SSMILESTUT} and the '%' tag for more than 10 rings
 * at a time are supported. An example:
 * <pre>
 * try {
 *   SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 *   IMolecule m = sp.parseSmiles("c1ccccc1");
 * } catch (InvalidSmilesException ise) {
 * }
 * </pre>
 *
 * <p>This parser does not parse stereochemical information, but the following
 * features are supported: reaction smiles, partitioned structures, charged
 * atoms, implicit hydrogen count, '*' and isotope information.
 *
 * <p>See {@cdk.cite WEI88} for further information.
 *
 * @author Christoph Steinbeck
 * @author Egon Willighagen
 * @cdk.module smiles
 * @cdk.githash
 * @cdk.created 2002-04-29
 * @cdk.keyword SMILES, parser
 * @cdk.bug 1579230
 * @cdk.bug 1579235
 * @cdk.bug 1579244
 */
@TestClass("org.openscience.cdk.smiles.SmilesParserTest")
public final class SmilesParser {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmilesParser.class);

    /**
     * The builder determines which CDK domain objects to create.
     */
    private final IChemObjectBuilder builder;

    /**
     * Direct converter from Beam to CDK.
     */
    private final BeamToCDK beamToCDK;

    /**
     * Kekulise the molecule on load. Generally this is a good idea as a 
     * lower-case symbols in a SMILES do not really mean 'aromatic' but rather
     * 'conjugated'. Loading with kekulise 'on' will automatically assign
     * bond orders (if possible) using an efficient algorithm from the 
     * underlying Beam library (soon to be added to CDK).
     */
    private boolean kekulise = true;

    /**
     * Create a new SMILES parser which will create {@link IAtomContainer}s with
     * the specified builder.
     *
     * @param builder used to create the CDK domain objects
     */
    public SmilesParser(final IChemObjectBuilder builder) {
        this.builder   = builder;
        this.beamToCDK = new BeamToCDK(builder);
    }

    /**
     * Parse a reaction SMILES.
     *
     * @param smiles The SMILES string to parse
     * @return An instance of {@link org.openscience.cdk.interfaces.IReaction}
     * @throws InvalidSmilesException if the string cannot be parsed
     * @see #parseSmiles(String)
     */
    @TestMethod("testReaction,testReactionWithAgents")
    public IReaction parseReactionSmiles(String smiles) throws InvalidSmilesException {
        StringTokenizer tokenizer = new StringTokenizer(smiles, ">");
        String reactantSmiles = tokenizer.nextToken();
        String agentSmiles = "";
        String productSmiles = tokenizer.nextToken();
        if (tokenizer.hasMoreTokens()) {
            agentSmiles = productSmiles;
            productSmiles = tokenizer.nextToken();
        }

        IReaction reaction = builder.newInstance(IReaction.class);

        // add reactants
        IAtomContainer reactantContainer = parseSmiles(reactantSmiles);
        IAtomContainerSet reactantSet = ConnectivityChecker.partitionIntoMolecules(reactantContainer);
        for (int i = 0; i < reactantSet.getAtomContainerCount(); i++) {
            reaction.addReactant(reactantSet.getAtomContainer(i));
        }

        // add reactants
        if (agentSmiles.length() > 0) {
            IAtomContainer agentContainer = parseSmiles(agentSmiles);
            IAtomContainerSet agentSet = ConnectivityChecker.partitionIntoMolecules(agentContainer);
            for (int i = 0; i < agentSet.getAtomContainerCount(); i++) {
                reaction.addAgent(agentSet.getAtomContainer(i));
            }
        }

        // add products
        IAtomContainer productContainer = parseSmiles(productSmiles);
        IAtomContainerSet productSet = ConnectivityChecker.partitionIntoMolecules(productContainer);
        for (int i = 0; i < productSet.getAtomContainerCount(); i++) {
            reaction.addProduct(productSet.getAtomContainer(i));
        }

        return reaction;
    }


    /**
     * Parses a SMILES string and returns a Molecule object.
     *
     * @param smiles A SMILES string
     * @return A Molecule representing the constitution given in the SMILES
     *         string
     * @throws InvalidSmilesException thrown when the SMILES string is invalid
     */
    @TestMethod("testAromaticSmiles,testSFBug1296113")
    public IAtomContainer parseSmiles(String smiles) throws InvalidSmilesException {
        return builder.newInstance(IAtomContainer.class);
    }

    /**
     * Makes the Smiles parser set aromaticity as provided in the Smiles itself,
     * without detecting it. Default false. Atoms will not be typed when set to
     * true.
     *
     * @param preservingAromaticity boolean to indicate if aromaticity is to be
     *                              preserved.
     */
    public void setPreservingAromaticity(boolean preservingAromaticity) {
        this.kekulise = !preservingAromaticity;
    }

    /**
     * Gets the (default false) setting to preserve aromaticity as provided in
     * the Smiles itself.
     *
     * @return true or false indicating if aromaticity is preserved.
     */
    public boolean isPreservingAromaticity() {
        return !kekulise;
    }
}


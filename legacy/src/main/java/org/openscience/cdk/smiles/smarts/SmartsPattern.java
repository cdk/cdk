/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
package org.openscience.cdk.smiles.smarts;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smarts.Smarts;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.IOException;


/**
 * A {@link Pattern} for matching a single SMARTS query against multiple target
 * compounds. The class should <b>not</b> be used for matching many queries
 * against a single target as in substructure keyed fingerprints. The {@link
 * SMARTSQueryTool} is currently a better option as less target initialistion is
 * performed.
 *
 * Simple usage:
 *
 * <blockquote><pre>
 * Pattern ptrn = SmartsPattern.create("O[C@?H](C)CC");
 *
 * for (IAtomContainer ac : acs) {
 *   if (ptrn.matches(ac)) {
 *       // 'ac' contains the pattern
 *   }
 * }
 * </pre></blockquote>
 *
 * Obtaining a {@link Mappings} instance and determine the number of unique
 * matches.
 *
 * <blockquote><pre>
 * Pattern ptrn = SmartsPattern.create("O[C@?H](C)CC");
 *
 * for (IAtomContainer ac : acs) {
 *   nUniqueHits += ptrn.matchAll(ac)
 *                      .countUnique();
 * }
 * </pre></blockquote>
 *
 * @author John May
 */
@Deprecated
public final class SmartsPattern extends Pattern {

    /** Parsed query. */
    private final IAtomContainer query;

    /** Subgraph mapping. */
    private final Pattern        pattern;

    /**
     * Prepare the target molecule (i.e. detect rings, aromaticity) before
     * matching the SMARTS.
     */
    private boolean doPrep = true;

    /** Aromaticity model. */
    private static final Aromaticity    arom = new Aromaticity(ElectronDonation.daylight(),
                                                               Cycles.or(Cycles.all(), Cycles.relevant()));



    /**
     * Internal constructor.
     *
     * @param smarts  pattern
     * @param builder the builder
     * @throws IOException the pattern could not be parsed
     */
    private SmartsPattern(final String smarts, IChemObjectBuilder builder) throws IOException {
        this.query = new QueryAtomContainer(null);
        if (!Smarts.parse(query, smarts))
            throw new IOException("Could not parse SMARTS: " + smarts);
        this.pattern = Pattern.findSubstructure(query);
    }

    static void prepare(IAtomContainer target) {
        // apply the daylight aromaticity model
        try {
            Cycles.markRingAtomsAndBonds(target);
            arom.apply(target);
        } catch (CDKException e) {
            LoggingToolFactory.createLoggingTool(SmartsPattern.class).error(e);
        }
    }

    /**
     * Sets whether the molecule should be "prepared" for a SMARTS match,
     * including set ring flags and perceiving aromaticity. The main reason
     * to skip preparation (via {@link #prepare(IAtomContainer)}) is if it has
     * already been done, for example when matching multiple SMARTS patterns.
     *
     * @param doPrep whether preparation should be done
     */
    public void setPrepare(boolean doPrep) {
        this.doPrep = doPrep;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int[] match(IAtomContainer container) {
        return matchAll(container).first();
    }

    /**
     * Obtain the mappings of the query pattern against the target compound. Any
     * initialisations required for the SMARTS match are automatically
     * performed. The Daylight aromaticity model is applied clearing existing
     * aromaticity. <b>Do not use this for matching multiple SMARTS againsts the
     * same container</b>.
     *
     * <blockquote><pre>
     * Pattern ptrn = SmartsPattern.create("O[C@?H](C)CC");
     * int nUniqueHits = 0;
     *
     * for (IAtomContainer ac : acs) {
     *   nUniqueHits += ptrn.matchAll(ac)
     *                      .countUnique();
     * }
     * </pre></blockquote>
     *
     * See {@link Mappings} for available methods.
     *
     * @param target the target compound in which we want to match the pattern
     * @return mappings of the query to the target compound
     */
    @Override
    public Mappings matchAll(final IAtomContainer target) {

        if (doPrep)
            prepare(target);

        return pattern.matchAll(target);

        // Note: Mappings is lazy, we can't reset aromaticity etc as the
        // substructure match may not have finished
    }

    /**
     * Create a {@link Pattern} that will match the given {@code smarts} query.
     *
     * @param smarts  SMARTS pattern string
     * @param builder chem object builder used to create objects
     * @return a new pattern
     * @throws java.io.IOException the smarts could not be parsed
     */
    public static SmartsPattern create(String smarts, IChemObjectBuilder builder) throws IOException {
        return new SmartsPattern(smarts, builder);
    }

    /**
     * Default SMARTS pattern constructor, passes in a null chem object builder.
     *
     * @param smarts SMARTS pattern string
     * @return a SMARTS pattern
     * @throws IOException problem with SMARTS string syntax/semantics
     */
    public static SmartsPattern create(String smarts) throws IOException {
        return new SmartsPattern(smarts, null);
    }
}

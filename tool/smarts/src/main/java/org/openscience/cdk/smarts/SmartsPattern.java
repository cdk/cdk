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
package org.openscience.cdk.smarts;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * A {@link Pattern} for matching a single SMARTS query against multiple target
 * compounds. The class can be used for efficiently matching many queries
 * against a single target if {@link #setPrepare(boolean)} is disabled ({@link
 * #prepare(IAtomContainer)}) should be called manually once for each molecule.
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
public final class SmartsPattern extends Pattern {

    /** Parsed query. */
    private final IAtomContainer query;

    /** Subgraph mapping. */
    private final Pattern        pattern;

    /**
     * Prepare the target molecule (i.e. detect rings, aromaticity) before
     * matching the SMARTS.
     */
    private int requires = 0;

    static final int RING_REQUIRED = 0x1;
    static final int AROM_REQUIRED = 0x3;

    /**
     * Internal constructor.
     *
     * @param smarts  pattern
     * @param builder the builder
     */
    private SmartsPattern(final String smarts, IChemObjectBuilder builder) {
        this.query = new QueryAtomContainer(builder);
        SmartsResult result = Smarts.parseToResult(query, smarts);
        if (!result.ok())
            throw new IllegalArgumentException("Could not parse SMARTS: " +
                                               smarts + "\n" +
                                               result.getMessage() + "\n" +
                                               result.displayErrorLocation());
        this.pattern = Pattern.findSubstructure(query);
        this.requires = getRequiredPrep(query);
    }

    public static void prepare(IAtomContainer target, int flags) {
        // mark rings if needed
        if ((flags& RING_REQUIRED) != 0)
            Cycles.markRingAtomsAndBonds(target);
        // apply the aromaticity model
        if ((flags& AROM_REQUIRED) != 0) {
            if (!Aromaticity.apply(Aromaticity.Model.Daylight, target))
                LoggingToolFactory.createLoggingTool(SmartsPattern.class)
                                  .error("Molecule had complex rings and aromaticity may not be completely defined");
        }
    }

    public static void prepare(IAtomContainer target) {
        prepare(target, AROM_REQUIRED);
    }

    /**
     * Sets whether the molecule should be "prepared" for a SMARTS match,
     * including set ring flags and perceiving aromaticity. The main reason
     * to skip preparation (via {@link #prepare(IAtomContainer)}) is if it has
     * already been done, for example when matching multiple SMARTS patterns.
     *
     * @param doPrep whether preparation should be done
     * @return self for inline calling
     */
    public SmartsPattern setPrepare(boolean doPrep) {
        this.requires = doPrep ? getRequiredPrep(query) : 0;
        return this;
    }

    private static int getRequiredPrep(Expr expr) {
        switch (expr.type()) {
            case RING_COUNT:
            case RING_BOND_COUNT:
            case RING_SMALLEST:
            case RING_SIZE:
            case IS_IN_RING:
            case IS_IN_CHAIN:
                return RING_REQUIRED;
            case ALIPHATIC_ELEMENT:
                // Chlorine is always aliphatic... and the parser knows this
                // but an expression may have been manually constructed
                switch (expr.value()) {
                    case IElement.Wildcard: // ??
                    case IElement.B: // not Daylight but prep still
                    case IElement.C:
                    case IElement.N:
                    case IElement.O:
                    case IElement.P:
                    case IElement.S:
                    case IElement.Si:
                    case IElement.Se:
                    case IElement.As:
                    case IElement.Te: // not Daylight but prep still
                        return AROM_REQUIRED;
                    default:
                        return 0;
                }
            case IS_ALIPHATIC:
            case IS_AROMATIC:
            case AROMATIC_ELEMENT:
            case ALIPHATIC_ORDER:
            case SINGLE_OR_AROMATIC:
            case DOUBLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case HAS_ALIPHATIC_HETERO_SUBSTITUENT:
            case HAS_HETERO_SUBSTITUENT:
            case IS_ALIPHATIC_HETERO:
            case ALIPHATIC_HETERO_SUBSTITUENT_COUNT:
                return AROM_REQUIRED;
            case RECURSIVE:
                return getRequiredPrep(expr.subquery());
            case AND:
            case OR:
                return getRequiredPrep(expr.left()) |
                        getRequiredPrep(expr.right());
            case NOT:
                return getRequiredPrep(expr.left());
            case TRUE:
            case FALSE:
            case IS_HETERO:
            case HAS_IMPLICIT_HYDROGEN:
            case HAS_ISOTOPE:
            case HAS_UNSPEC_ISOTOPE:
            case UNSATURATED:
            case ELEMENT:
            case IMPL_H_COUNT:
            case TOTAL_H_COUNT:
            case DEGREE:
            case TOTAL_DEGREE:
            case HEAVY_DEGREE:
            case VALENCE:
            case ISOTOPE:
            case FORMAL_CHARGE:
            case HYBRIDISATION_NUMBER:
            case HETERO_SUBSTITUENT_COUNT:
            case PERIODIC_GROUP:
            case INSATURATION:
            case REACTION_ROLE:
            case STEREOCHEMISTRY:
            case ORDER:
                return 0;
            default:
                throw new IllegalStateException("SmartPattern needs updating to know if: " + expr + " needs ring/arom flags?");
        }
    }

    static int getRequiredPrep(IAtomContainer query) {
        int flags = 0;
        for (IAtom atom : query.atoms())
            if (atom instanceof IQueryAtom)
                flags |= getRequiredPrep(((QueryAtom) AtomRef.deref(atom)).getExpression());
        for (IBond bond : query.bonds()) {
            if (bond instanceof IQueryBond)
                flags |= getRequiredPrep(((QueryBond) BondRef.deref(bond)).getExpression());
        }
        return flags;
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

        prepare(target, requires);

        // Note: Mappings is lazy, we can't reset aromaticity etc as the
        // substructure match may not have finished
        return pattern.matchAll(target);
    }

    /**
     * Create a {@link Pattern} that will match the given {@code smarts} query.
     *
     * @param smarts  SMARTS pattern string
     * @param builder chem object builder used to create objects
     * @return a new pattern
     */
    public static SmartsPattern create(String smarts, IChemObjectBuilder builder) {
        return new SmartsPattern(smarts, builder);
    }

    /**
     * Default SMARTS pattern constructor, passes in a null chem object builder.
     *
     * @param smarts SMARTS pattern string
     * @return a SMARTS pattern
     */
    public static SmartsPattern create(String smarts) {
        return new SmartsPattern(smarts, null);
    }
}

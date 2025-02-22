/*
 * Copyright (c) 2013, European Bioinformatics Institute (EMBL-EBI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 */

package uk.ac.ebi.beam;

import static uk.ac.ebi.beam.Configuration.Type.DoubleBond;
import static uk.ac.ebi.beam.Configuration.Type.ExtendedTetrahedral;
import static uk.ac.ebi.beam.Configuration.Type.Implicit;
import static uk.ac.ebi.beam.Configuration.Type.None;
import static uk.ac.ebi.beam.Configuration.Type.Octahedral;
import static uk.ac.ebi.beam.Configuration.Type.SquarePlanar;
import static uk.ac.ebi.beam.Configuration.Type.Tetrahedral;
import static uk.ac.ebi.beam.Configuration.Type.TrigonalBipyramidal;

/**
 * Enumeration of atom-based relative configurations. Each value defines a
 * configuration of a given topology.
 *
 * @author John May
 * @see <a href="http://www.opensmiles.org/opensmiles.html#chirality">Chirality,
 *      OpenSMILES</a>
 */
public enum Configuration {

    /** An atoms has unknown/no configuration. */
    UNKNOWN(None, ""),

    /** Shorthand for TH1, AL1, DB1, TB1 or OH1 configurations. */
    ANTI_CLOCKWISE(Implicit, "@"),

    /** Shorthand for TH2, AL2, DB2, TB2 or OH2 configurations. */
    CLOCKWISE(Implicit, "@@"),

    /**
     * Tetrahedral, neighbors proceed anti-clockwise looking from the first
     * atom.
     */
    TH1(Tetrahedral, "@TH1", ANTI_CLOCKWISE),

    /** Tetrahedral, neighbors proceed clockwise looking from the first atom. */
    TH2(Tetrahedral, "@TH2", CLOCKWISE),

    /**
     * Atom-based double bond configuration, neighbors proceed anti-clockwise in
     * a plane. <i>Note - this configuration is currently specific to
     * grins.</i>
     */
    DB1(DoubleBond, "@DB1", ANTI_CLOCKWISE),

    /**
     * Atom-based double bond configuration, neighbors proceed clockwise in a
     * plane.<i>Note - this configuration is currently specific to grins.</i>
     */
    DB2(DoubleBond, "@DB2", CLOCKWISE),

    // extended tetrahedral, allene-like (Sp)
    AL1(ExtendedTetrahedral, "@AL1", ANTI_CLOCKWISE),
    AL2(ExtendedTetrahedral, "@AL2", CLOCKWISE),

    // square planar
    SP1(SquarePlanar, "@SP1"),
    SP2(SquarePlanar, "@SP2"),
    SP3(SquarePlanar, "@SP3"),

    // trigonal bipyramidal
    TB1(TrigonalBipyramidal, "@TB1", ANTI_CLOCKWISE),
    TB2(TrigonalBipyramidal, "@TB2", CLOCKWISE),
    TB3(TrigonalBipyramidal, "@TB3"),
    TB4(TrigonalBipyramidal, "@TB4"),
    TB5(TrigonalBipyramidal, "@TB5"),
    TB6(TrigonalBipyramidal, "@TB6"),
    TB7(TrigonalBipyramidal, "@TB7"),
    TB8(TrigonalBipyramidal, "@TB8"),
    TB9(TrigonalBipyramidal, "@TB9"),
    TB10(TrigonalBipyramidal, "@TB10"),
    TB11(TrigonalBipyramidal, "@TB11"),
    TB12(TrigonalBipyramidal, "@TB12"),
    TB13(TrigonalBipyramidal, "@TB13"),
    TB14(TrigonalBipyramidal, "@TB14"),
    TB15(TrigonalBipyramidal, "@TB15"),
    TB16(TrigonalBipyramidal, "@TB16"),
    TB17(TrigonalBipyramidal, "@TB17"),
    TB18(TrigonalBipyramidal, "@TB18"),
    TB19(TrigonalBipyramidal, "@TB19"),
    TB20(TrigonalBipyramidal, "@TB20"),

    // octahedral
    OH1(Octahedral, "@OH1", ANTI_CLOCKWISE),
    OH2(Octahedral, "@OH2", CLOCKWISE),
    OH3(Octahedral, "@OH3"),
    OH4(Octahedral, "@OH4"),
    OH5(Octahedral, "@OH5"),
    OH6(Octahedral, "@OH6"),
    OH7(Octahedral, "@OH7"),
    OH8(Octahedral, "@OH8"),
    OH9(Octahedral, "@OH9"),
    OH10(Octahedral, "@OH10"),
    OH11(Octahedral, "@OH11"),
    OH12(Octahedral, "@OH12"),
    OH13(Octahedral, "@OH13"),
    OH14(Octahedral, "@OH14"),
    OH15(Octahedral, "@OH15"),
    OH16(Octahedral, "@OH16"),
    OH17(Octahedral, "@OH17"),
    OH18(Octahedral, "@OH18"),
    OH19(Octahedral, "@OH19"),
    OH20(Octahedral, "@OH20"),
    OH21(Octahedral, "@OH21"),
    OH22(Octahedral, "@OH22"),
    OH23(Octahedral, "@OH23"),
    OH24(Octahedral, "@OH24"),
    OH25(Octahedral, "@OH25"),
    OH26(Octahedral, "@OH26"),
    OH27(Octahedral, "@OH27"),
    OH28(Octahedral, "@OH28"),
    OH29(Octahedral, "@OH29"),
    OH30(Octahedral, "@OH30");

    /** Type of configuration. */
    private final Type type;

    /** Symbol used to represent configuration */
    private final String symbol;

    /** Shorthand - often converted to this in output */
    private final Configuration shorthand;

    /** Lookup tables for trigonal bipyramidal and octahedral */
    private static final Configuration[] tbs = new Configuration[21];
    private static final Configuration[] ohs = new Configuration[31];

    // initialise trigonal lookup
    static {
        int i = 1;
        for (Configuration config : values()) {
            if (config.type().equals(TrigonalBipyramidal))
                tbs[i++] = config;
        }
    }

    // initialise octahedral lookup
    static {
        int i = 1;
        for (Configuration config : values()) {
            if (config.type().equals(Octahedral))
                ohs[i++] = config;
        }
    }

    private Configuration(Type type, String symbol, Configuration shorthand) {
        this.type = type;
        this.symbol = symbol;
        this.shorthand = shorthand;
    }

    private Configuration(Type type, String symbol) {
        this.type = type;
        this.symbol = symbol;
        this.shorthand = this;
    }

    /**
     * Access the shorthand for the configuration, if no shorthand is defined
     * {@link #UNKNOWN} is returned.
     *
     * @return the shorthand '@' or '@@'
     */
    public Configuration shorthand() {
        return shorthand;
    }

    /**
     * Symbol of the chiral configuration.
     *
     * @return the symbol
     */
    public String symbol() {
        return symbol;
    }

    /**
     * The general type of relative configuration this represents.
     *
     * @return type of the configuration
     * @see Type
     */
    public Type type() {
        return type;
    }

    /**
     * Read a chiral configuration from a character buffer and progress the
     * buffer. If there is no configuration then {@link Configuration#UNKNOWN}
     * is returned. Encountering an invalid permutation designator (e.g.
     * &#64;TB21) or incomplete class (e.g. &#64;T) will throw an invalid smiles
     * exception.
     *
     * @param buffer a character buffer
     * @return the configuration
     * @throws InvalidSmilesException
     */
    static Configuration read(final CharBuffer buffer) throws
                                                       InvalidSmilesException {
        if (buffer.getIf('@')) {
            if (buffer.getIf('@')) {
                return Configuration.CLOCKWISE;
            } else if (buffer.getIf('1')) {
                return Configuration.ANTI_CLOCKWISE;
            } else if (buffer.getIf('2')) {
                return Configuration.CLOCKWISE;
            } else if (buffer.getIf('T')) {
                // TH (tetrahedral) or TB (trigonal bipyramidal)
                if (buffer.getIf('H')) {
                    if (buffer.getIf('1'))
                        return Configuration.TH1;
                    else if (buffer.getIf('2'))
                        return Configuration.TH2;
                    else
                        throw new InvalidSmilesException("invalid permutation designator for @TH, valid values are @TH1 or @TH2:",
                                                         buffer);
                } else if (buffer.getIf('B')) {
                    int num = buffer.getNumber();
                    if (num < 1 || num > 20)
                        throw new InvalidSmilesException("invalid permutation designator for @TB, valid values are '@TB1, @TB2, ... @TB20:'",
                                                         buffer);
                    return tbs[num];
                }
                throw new InvalidSmilesException("'@T' is not a valid chiral specification:", buffer);
            } else if (buffer.getIf('D')) {
                // DB (double bond)
                if (buffer.getIf('B')) {
                    if (buffer.getIf('1'))
                        return Configuration.DB1;
                    else if (buffer.getIf('2'))
                        return Configuration.DB2;
                    else
                        throw new InvalidSmilesException("invalid permutation designator for @DB, valid values are @DB1 or @DB2:",
                                                         buffer);
                }
                throw new InvalidSmilesException("'@D' is not a valid chiral specification:", buffer);
            } else if (buffer.getIf('A')) {
                // allene (extended tetrahedral)
                if (buffer.getIf('L')) {
                    if (buffer.getIf('1'))
                        return Configuration.AL1;
                    else if (buffer.getIf('2'))
                        return Configuration.AL2;
                    else
                        throw new InvalidSmilesException("invalid permutation designator for @AL, valid values are '@AL1 or @AL2':", buffer);
                } else {
                    throw new InvalidSmilesException("'@A' is not a valid chiral specification:", buffer);
                }
            } else if (buffer.getIf('S')) {
                // square planar
                if (buffer.getIf('P')) {
                    if (buffer.getIf('1'))
                        return Configuration.SP1;
                    else if (buffer.getIf('2'))
                        return Configuration.SP2;
                    else if (buffer.getIf('3'))
                        return Configuration.SP3;
                    else
                        throw new InvalidSmilesException("invalid permutation designator for @SP, valid values are '@SP1, @SP2 or @SP3':",
                                                         buffer);
                } else {
                    throw new InvalidSmilesException("'@S' is not a valid chiral specification:", buffer);
                }
            } else if (buffer.getIf('O')) {
                if (buffer.getIf('H')) {
                    // octahedral
                    int num = buffer.getNumber();
                    if (num < 1 || num > 30)
                        throw new InvalidSmilesException("invalid permutation designator for @OH, valud values are '@OH1, @OH2, ... @OH30':", buffer);
                    return ohs[num];
                } else {
                    throw new InvalidSmilesException("'@O' is not a valid chiral specification:", buffer);
                }
            } else {
                return Configuration.ANTI_CLOCKWISE;
            }
        }
        return UNKNOWN;
    }

    /** Types of configuration. */
    public enum Type {
        None,
        Implicit,
        Tetrahedral,
        DoubleBond,
        ExtendedTetrahedral,
        SquarePlanar,
        TrigonalBipyramidal,
        Octahedral
    }

    /** Configurations for double-bond bond-based specification. */
    public enum DoubleBond {
        UNSPECIFIED,
        TOGETHER,
        OPPOSITE
    }
}


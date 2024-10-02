/* Copyright (C) 2024 Beilstein-Institute
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
package org.openscience.cdk.rinchi;

import org.openscience.cdk.exception.CDKException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Class to represent layers of InChI (International Chemical Identifier)
 * components. It allows for the aggregation of major and minor layers
 * within an InChI string, supporting operations such as appending components and generating
 * hash representations of the layers.
 * Based on RInChI code:
 * <a href="https://github.com/IUPAC-InChI/RInChI/blob/main/src/rinchi/rinchi_reaction.cpp">RInCHI code</a>
 *
 * <p>This class provides functionality to parse InChI strings, maintain counts of protons,
 * and generate various hash formats based on the layers contained.</p>
 *
 * @author Felix Bänsch
 */
class InChILayers {

    private final StringBuilder majors;
    private final StringBuilder minors;
    private int protonCount;

    /**
     * Constructs an {@code InChILayers} instance by appending components from the given list.
     *
     * @param rcList a list of {@link RInChIComponent} objects to append as layers
     * @throws CDKException if there is an error processing the components
     */
    protected InChILayers(List<RInChIComponent> rcList) throws CDKException {
        this();
        appendComponents(rcList);
    }

    /**
     * Constructs an empty {@code InChILayers} instance.
     * Initializes major and minor StringBuilders and sets the proton count to zero.
     */
    protected InChILayers() {
        this.majors = new StringBuilder();
        this.minors = new StringBuilder();
        this.protonCount = 0;
    }

    /**
     * Appends an InChI string to the layers.
     *
     * <p>This method validates the InChI string, extracts major and minor layers,
     * and updates the proton count accordingly. It supports only standard InChI version 1.</p>
     *
     * @param inchiString the InChI string to append
     * @throws CDKException if the InChI string is invalid or if there is an error processing it
     */
    protected void append(String inchiString) throws CDKException {
        if (inchiString.isEmpty()) {
            return;
        }

        int delimPos = inchiString.indexOf(RInChIConsts.DELIM_LAYER);
        int tokenStart = delimPos + RInChIConsts.DELIM_LAYER.length();

        if (delimPos != 8) throw new CDKException("Invalid InChI string - no layers.");
        if (inchiString.charAt(delimPos - 1) != 'S') throw new CDKException("Only standard InChIs are supported.");
        if (inchiString.charAt(delimPos - 2) != '1') throw new CDKException("Only InChI version 1 supported.");

        boolean isFirstLayer = true;
        boolean isMajorLayer = true;
        StringBuilder majorLayers = new StringBuilder();
        StringBuilder minorLayers = new StringBuilder();

        do {
            delimPos = inchiString.indexOf(RInChIConsts.DELIM_LAYER, tokenStart);
            String layer;
            if (delimPos != -1) {
                layer = inchiString.substring(tokenStart - 1, delimPos);
                tokenStart = delimPos + RInChIConsts.DELIM_LAYER.length();
            } else {
                layer = inchiString.substring(tokenStart - 1);
            }

            if (isFirstLayer) {
                majorLayers.append(layer);
            } else if (layer.length() >= 2) {
                if (isMajorLayer) {
                    switch (layer.charAt(1)) {
                        case 'c':
                        case 'h':
                        case 'q':
                            majorLayers.append(layer);
                            break;
                        case 'p':
                            protonCount += Integer.parseInt(layer.substring(2));
                            break;
                        default:
                            minorLayers.append(layer);
                            isMajorLayer = false;
                    }
                } else {
                    minorLayers.append(layer);
                }
            }

            isFirstLayer = false;
        } while (delimPos != -1);

        if (majorLayers.length() > 0) {
            majorLayers.deleteCharAt(0);
        }
        if (majorLayers.length() == 0) {
            majorLayers.append(RInChIConsts.DELIM_LAYER);
        }

        if (minorLayers.length() > 0) {
            minorLayers.deleteCharAt(0);
        }

        if (majors.length() > 0) {
            majors.append(RInChIConsts.DELIM_COMP);
        }
        majors.append(majorLayers);

        if (minors.length() > 0) {
            minors.append(RInChIConsts.DELIM_COMP);
        }
        minors.append(minorLayers);
    }

    /**
     * Appends components from the provided list of {@link RInChIComponent} objects.
     *
     * @param rcList a list of {@link RInChIComponent} objects to append
     * @throws CDKException if there is an error processing any of the components
     */
    protected void appendComponents(List<RInChIComponent> rcList) throws CDKException {
        for (RInChIComponent rc : rcList) {
            if (!rc.isNoStructure()) {
                append(rc.getInchi());
            }
        }
    }

    /**
     * Generates a 10-character hash representation of the major layers.
     *
     * @return a string representing the 10-character hash of the major layers
     * @throws NoSuchAlgorithmException if there is an error generating the hash
     */
    protected String majorHash() throws NoSuchAlgorithmException {
        return RInChIHash.hash10char(majors.toString());
    }

    /**
     * Generates a 17-character hash representation of the major layers.
     *
     * @return a string representing the 17-character hash of the major layers
     * @throws NoSuchAlgorithmException if there is an error generating the hash
     */
    protected String majorHashExt() throws NoSuchAlgorithmException {
        return RInChIHash.hash17char(majors.toString());
    }

    /**
     * Generates a hash representation of the minor layers, prefixed with the proton count.
     *
     * @return a string representing the hash of the minor layers, including the proton count
     * @throws NoSuchAlgorithmException if there is an error generating the hash
     */
    protected String minorHash() throws NoSuchAlgorithmException {
        return protonCount2char(protonCount) + RInChIHash.hash04char(minors.toString());
    }

    /**
     * Generates an extended hash representation of the minor layers, prefixed with the proton count.
     *
     * @return a string representing the extended hash of the minor layers, including the proton count
     * @throws NoSuchAlgorithmException if there is an error generating the hash
     */
    protected String minorHashExt() throws NoSuchAlgorithmException {
        return protonCount2char(protonCount) + RInChIHash.hash12char(minors.toString());
    }

    /**
     * Returns a predefined string representing an empty major hash.
     *
     * @return a string representing the empty major hash
     */
    protected static String emptyMajorHash() {
        return RInChIConsts.HASH_10_EMPTY_STRING;
    }

    /**
     * Returns a predefined string representing an empty minor hash, prefixed with zero proton count.
     *
     * @return a string representing the empty minor hash
     */
    protected static String emptyMinorHash() {
        return protonCount2char(0) + RInChIConsts.HASH_04_EMPTY_STRING;
    }

    /**
     * Converts the proton count to a corresponding character.
     *
     * <p>Proton counts greater than 12 or less than -12 are represented as 'A'. Counts
     * from -12 to 12 are converted to characters 'N' through 'Z' based on the count.</p>
     *
     * @param protonCount the proton count to convert
     * @return a character representing the proton count
     */
    private static char protonCount2char(int protonCount) {
        if (protonCount > 12 || protonCount < -12) {
            return 'A';
        } else {
            return (char) ('N' + protonCount);
        }
    }
}

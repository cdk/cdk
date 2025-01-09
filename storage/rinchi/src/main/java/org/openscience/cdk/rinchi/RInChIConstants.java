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

/**
 * The RInChIConsts class holds constants used for constructing and parsing 
 * Reaction InChI (RInChI) strings and keys.
 * 
 * @author Felix Bänsch
 * @cdk.module rinchi
 * @cdk.githash
 */
final class RInChIConstants {
    static final String RINCHI_EMPTY_HEADER = "RInChI=";
    static final String RINCHI_VERSION_1_0_0 = "1.00";
    static final String RINCHI_IDENTIFIER = RINCHI_EMPTY_HEADER + RINCHI_VERSION_1_0_0;

    static final String INCHI_VERSION_1 = "1";
    static final String INCHI_IDENTIFIER = "InChI";
    static final String INCHI_STD_HEADER = INCHI_IDENTIFIER + "=" + INCHI_VERSION_1 + "S/";
    static final String RINCHI_STD_HEADER = RINCHI_IDENTIFIER + "." + INCHI_VERSION_1 + "S/";

    static final String INCHI_AUXINFO_HEADER = "AuxInfo=" + INCHI_VERSION_1 + "/";
    static final String RINCHI_AUXINFO_HEADER = "RAuxInfo=" + RINCHI_VERSION_1_0_0 + "." + INCHI_VERSION_1 + "/";

    // Delimiters in RInChI strings.
    static final String DELIMITER_LAYER = "/";
    static final String DELIMITER_COMPONENT = "!";
    static final String DELIMITER_GROUP = "<>";

    static final String DIRECTION_TAG = "/d";
    static final String DIRECTION_FORWARD = "+";
    static final String DIRECTION_REVERSE = "-";
    static final String DIRECTION_EQUILIBRIUM = "=";

    static final String NOSTRUCT_INCHI = INCHI_STD_HEADER + DELIMITER_LAYER;
    static final String NOSTRUCT_AUXINFO = INCHI_AUXINFO_HEADER + DELIMITER_LAYER;
    static final String NOSTRUCT_RINCHI_LONGKEY = "MOSFIJXAXDLOML-UHFFFAOYSA-N";

    static final String NOSTRUCT_TAG = "/u";
    static final char NOSTRUCT_DELIMITER = '-';

    static final String RINCHI_LONG_KEY_HEADER = "Long-RInChIKey=";
    static final String RINCHI_SHORT_KEY_HEADER = "Short-RInChIKey=";
    static final String RINCHI_WEB_KEY_HEADER = "Web-RInChIKey=";

    static final String RINCHI_KEY_VERSION_ID_HEADER = "SA";

    // Delimiters in RInChI keys.
    static final String KEY_DELIMITER_BLOCK = "-";
    static final String KEY_DELIMITER_COMPONENT = "-";
    static final String KEY_DELIMITER_GROUP = "--";

    //Empty hashes
    static final String HASH_04_EMPTY_STRING = "UHFF";
    static final String HASH_10_EMPTY_STRING = "UHFFFADPSC";
    static final String HASH_12_EMPTY_STRING = "UHFFFADPSCTJ";
    static final String HASH_14_EMPTY_STRING = "UHFFFADPSCTJAU";
    static final String HASH_17_EMPTY_STRING = "UHFFFADPSCTJAUYIS";
}

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
 * Class to provides methods for encoding and hashing data using base-26 and hexadecimal representations.
 * Based on InChI C code:
 * (<a href="https://github.com/IUPAC-InChI/InChI/blob/main/INCHI-1-SRC/INCHI_BASE/src/ikey_base26.c">ikey_base26.c</a>)
 *
 * <p>This class contains static methods that manipulate integer arrays to
 * generate base-26 triplets and doublets, compute checksums, and generate
 * hexadecimal strings based on specific portions of the input arrays.
 * It also includes predefined character and weight arrays used in the
 * calculations.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *     <li>Encoding integers into base-26 triplets and doublets.</li>
 *     <li>Calculating a base-26 checksum for strings.</li>
 *     <li>Generating hexadecimal strings from integer arrays.</li>
 * </ul>
 *
 * <p>Note: The methods in this class assume that the input integer arrays
 * simulate C++ unsigned char types, which may affect how byte values are
 * interpreted.</p>
 *
 * @author Felix Bänsch
 */
final class KeyBase26 {

    /**
     * Char array containing all 26 letters of the English alphabet.
     */
    private static final char[] c26 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * Weight scheme for check character.
     */
    private static final int N_UNIQUE_WEIGHTS = 12;
    private static final int[] weights_for_checksum = { 1,3,5,7,9,11,15,17,19,21,23,25 };

    /**
     * Returns a base-26 triplet based on the input.
     * Triplets starting with 'E' are omitted as well as the 516 triplets 'TAA' to 'TTV'.
     * This method is based on the char array "t26" of the InChI C code.
     * Original comment:
     *     "As the 2^14 (16384) is very close to 26^3 (17576), a triplet of uppercase
     *     letters A..Z encodes 14 bits with good efficiency.
     *     For speed, we just tabulate triplets below.     *
     *     We should throw away 17576-16384= 1192 triplets.
     *     These are 676 triplets starting from 'E', the most frequent letter in English
     *     texts (the other 516 are those started at 'T' , "TAA" to "TTV")."
     *
     * @param input int
     * @return base-26 triplet String
     */
    static String getBase26Triplet(int input) {
        int i = 0;
        for (char c1 : c26) {
            if (c1 == 'E')
                continue;

            for (char c2 : c26) {
                for (char c3 : c26) {
                    if (c1 == 'T') {
                        if (c2 < 'T') {
                            continue;
                        } else if (c2 == 'T' && c3 < 'W')
                            continue;
                    }

                    if (i == input) {
                        return String.valueOf(new char[] { c1, c2, c3 });
                    }

                    i++;
                }
            }
        }

        return null;
    }

    /**
     * Returns a base-26 doublet based on the input.
     * This method is based on the char array "d26" of the InChI C code.
     *
     * @param input int
     * @return base-26 doublet String
     */
    static String getBase26Doublet(int input) {
        input = Math.abs(input);
        char firstLetter = (char) ('A' + (input / 26) % 26);
        char secondLetter = (char) ('A' + input % 26);
        return "" + firstLetter + secondLetter;
    }

    /**
     * Converts the first two elements of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method takes the first element of the input integer array and the first 6 bits
     * of the second element, combines them, and converts the result into a base-26 encoded
     * string using {@link #getBase26Triplet(int)}.</p>
     * <p>This corresponds to the function <b>base26_triplet_1</b> in ikey_base_26.c of the
     * InChI C code.</p>
     *
     * @param bytes an integer array where the first two elements are used for encoding
     * @return a base-26 encoded string representing the first two elements of the array
     */
    static String base26Triplet1(int[] bytes) {
        final int b0 = bytes[0];        /* 1111 1111 */
        final int b1 = bytes[1] & 0x3f; /* 0011 1111 */
        final int h = b0 | (b1 << 8);
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method takes bits from the second, third, and fourth elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     * <p>This corresponds to the function <b>base26_triplet_2</b> in ikey_base_26.c of the
     * InChI C code.</p>
     *
     * @param bytes an integer array where the second, third, and fourth elements are used
     *              for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    static String base26Triplet2(int[] bytes) {
        final int b0 = bytes[1] & 0xc0;     /* 1100 0000 */
        final int b1 = bytes[2];            /* 1111 1111 */
        final int b2 = bytes[3] & 0x0f;     /* 0000 1111 */
        final int h = (b0 | b1 << 8 | b2 << 16) >> 6;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the fourth, fifth, and sixth elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     * <p>This corresponds to the function <b>base26_triplet_3</b> in ikey_base_26.c of the
     * InChI C code.</p>
     *
     * @param bytes an integer array where the fourth, fifth, and sixth elements are used
     *              for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    static String base26Triplet3(int[] bytes) {
        final int b0 = bytes[3] & 0xf0;   /* 1111 0000 */
        final int b1 = bytes[4];          /* 1111 1111 */
        final int b2 = bytes[5] & 0x03;   /* 0000 0011 */
        final int h = (b0 | b1 << 8 | b2 << 16) >> 4;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the sixth and seventh elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     * <p>This corresponds to the function <b>base26_triplet_4</b> in ikey_base_26.c of the
     * InChI C code.</p>
     *
     * @param bytes an integer array where the sixth and seventh elements are used for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    static String base26Triplet4(int[] bytes) {
        final int b0 = bytes[5] & 0xfc; /* 1111 1100 */
        final int b1 = bytes[6];        /* 1111 1111 */
        final int h = (b0 | b1 << 8) >> 2;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 doublet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the fourth and fifth elements of the input integer
     * array (covering bits 28 to 36), combines them, and converts the result into a base-26
     * encoded doublet string using {@link #getBase26Doublet(int)}.</p>
     * <p>This corresponds to the function <b>base26_dublet_for_bits_28_to_36</b> in
     * ikey_base_26.c of the InChI C code.</p>
     *
     * @param bytes an integer array where the fourth and fifth elements are used for encoding
     * @return a base-26 encoded doublet string representing the selected elements of the array
     */
    static String base26DoubletForBits28To36(int[] bytes) {
        final int b0 = bytes[3] & 0xf0;  /* 1111 0000 */
        final int b1 = bytes[4] & 0x1f;  /* 0001 1111 */
        final int h = (b0 | b1 << 8) >> 4;
        return getBase26Doublet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 doublet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts the eighth and the least significant bit of the ninth
     * element of the input integer array (covering bits 56 to 64), combines them, and
     * converts the result into a base-26 encoded doublet string using
     * {@link #getBase26Doublet(int)}.</p>
     * <p>This corresponds to the function <b>base26_dublet_for_bits_56_to_64</b> in
     * ikey_base_26.c of the InChI C code.</p>
     *
     * @param bytes an integer array where the eighth and ninth elements are used for encoding
     * @return a base-26 encoded doublet string representing the selected elements of the array
     */
    static String base26DoubletForBits56To64(int[] bytes) {
        final int b0 = bytes[7];         /* 1111 1111 */
        final int b1 = bytes[8] & 0x01;  /* 0000 0001 */
        final int h = (b0 | b1 << 8);
        return getBase26Doublet(h);
    }

    /**
     * Calculates the base-26 checksum for a given input string.
     *
     * <p>This method computes a checksum by iterating over each character in the input string,
     * skipping any hyphens ('-'). It multiplies each character's ASCII value by a corresponding
     * weight and accumulates the result. The final checksum is returned as a character
     * from the predefined base-26 character array.</p>
     *
     * @param input the input string for which the checksum is calculated; hyphens are ignored
     * @return a character representing the base-26 checksum of the input string
     */
    static char base26Checksum(String input) {
        int jj = 0;
        int checksum = 0;
        final int length = input.length();

        for (int j = 0; j < length; j++) {
            char c = input.charAt(j);
            if (c == '-')
                continue;
            checksum += weights_for_checksum[jj] * c;
            jj++;
            if (jj > N_UNIQUE_WEIGHTS - 1)
                jj = 0;
        }
        return c26[checksum%26];
    }

    /**
     * Generates a hexadecimal representation of a specific portion of an integer array.
     *
     * <p>This method constructs a hexadecimal string by first appending the value of the
     * ninth element (masked with 0xfe) and then appending the hexadecimal values of the
     * first 32 elements of the integer array. The resulting string is returned in a
     * concatenated format.</p>
     *
     * @param bytes an integer array from which the hexadecimal values are extracted
     * @return a hexadecimal string representing the specified elements of the array
     */
    static String getExtraHashMajorHex(int[] bytes) {
        final StringBuilder sb = new StringBuilder();
        final int startByte = 8;
        final int c = bytes[startByte] & 0xfe;
        sb.append(String.format("%02x", c));
        for (int i = 0; i < 32; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }

    /**
     * Generates a hexadecimal representation of a specific portion of an integer array.
     *
     * <p>This method constructs a hexadecimal string by first appending the value of the
     * fifth element (masked with 0xe0) and then appending the hexadecimal values of the
     * first 32 elements of the integer array. The resulting string is returned in a
     * concatenated format.</p>
     *
     * @param bytes an integer array from which the hexadecimal values are extracted
     * @return a hexadecimal string representing the specified elements of the array
     */
    static String getExtraHashMinorHex(int[] bytes) {
        final StringBuilder sb = new StringBuilder();
        final int startByte = 4;
        final int c = bytes[startByte] & 0xe0;
        sb.append(String.format("%o2x", c));
        for (int i = 0; i < 32; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }
}

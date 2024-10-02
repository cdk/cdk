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
class KeyBase26 {

    /**
     * Char array containing all 26 letters of the English alphabet
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
    protected static String getBase26Triplet(int input) {
        char[] result = new char[3];

        int i = 0;
        for(char c1 : c26){
            if (c1 == 'E')
                continue;
            for(char c2 : c26) {
                for(char c3 : c26) {
                    if (c1 == 'T')
                        if(c2 < 'T')
                            continue;
                        else
                        if(c2 == 'T' && c3 < 'W')
                            continue;
                    result[0] =c1;
                    result[1] =c2;
                    result[2] =c3;
                    if (i == input)
                        return new String(result);
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
    protected static String getBase26Doublet(int input) {
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
     *
     * @param bytes an integer array where the first two elements are used for encoding
     * @return a base-26 encoded string representing the first two elements of the array
     */
    protected static String base26_triplet_1(int[] bytes) {
        int b0 = bytes[0];
        int b1 = bytes[1] & 0x3f;
        int h = b0 | (b1 << 8);
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method takes bits from the second, third, and fourth elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     *
     * @param bytes an integer array where the second, third, and fourth elements are used
     *              for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    protected static String base26_triplet_2(int[] bytes) {
        int b0 = bytes[1] & 0xc0;
        int b1 = bytes[2];
        int b2 = bytes[3] & 0x0f;
        int h = (b0 | b1 << 8 | b2 << 16) >> 6;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the fourth, fifth, and sixth elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     *
     * @param bytes an integer array where the fourth, fifth, and sixth elements are used
     *              for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    protected static String base26_triplet_3(int[] bytes) {
        int b0 = bytes[3] & 0xf0;
        int b1 = bytes[4];
        int b2 = bytes[5] & 0x03;
        int h = (b0 | b1 << 8 | b2 << 16) >> 4;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 triplet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the sixth and seventh elements of the input
     * integer array, combines them, and converts the result into a base-26 encoded string
     * using {@link #getBase26Triplet(int)}.</p>
     *
     * @param bytes an integer array where the sixth and seventh elements are used for encoding
     * @return a base-26 encoded string representing the selected elements of the array
     */
    protected static String base26_triplet_4(int[] bytes) {
        int b0 = bytes[5] & 0xfc;
        int b1 = bytes[6];
        int h = (b0 | b1 << 8) >> 2;
        return getBase26Triplet(h);
    }

    /**
     * Converts a portion of an integer array into a base-26 doublet string.
     * Integer array is used to simulate C++ unsigned char.
     *
     * <p>This method extracts bits from the fourth and fifth elements of the input integer
     * array (covering bits 28 to 36), combines them, and converts the result into a base-26
     * encoded doublet string using {@link #getBase26Doublet(int)}.</p>
     *
     * @param bytes an integer array where the fourth and fifth elements are used for encoding
     * @return a base-26 encoded doublet string representing the selected elements of the array
     */
    protected static String  base26_doublet_for_bits_28_to_36(int[] bytes) {
        int b0 = bytes[3] & 0xf0;
        int b1 = bytes[4] & 0x1f;
        int h = (b0 | b1 << 8) >> 4;
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
     *
     * @param bytes an integer array where the eighth and ninth elements are used for encoding
     * @return a base-26 encoded doublet string representing the selected elements of the array
     */
    protected static String  base26_doublet_for_bits_56_to_64(int[] bytes) {
        int b0 = bytes[7];
        int b1 = bytes[8] & 0x01;
        int h = (b0 | b1 << 8);
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
    protected static char base26_checksum(String input) {
        int jj = 0;
        int checksum = 0;

        int len = input.length();

        for (int j = 0; j < len; j++) {
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
    protected static String get_xtra_hash_major_hex(int[] bytes) {
        StringBuilder sb = new StringBuilder();
        int startByte = 8;
        int c = bytes[startByte] & 0xfe;
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
    protected static String get_xtra_hash_minor_hex(int[] bytes) {
        int startByte = 4;
        StringBuilder sb = new StringBuilder();
        int c = bytes[startByte] & 0xe0;
        sb.append(String.format("%o2x", c));
        for (int i = 0; i < 32; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }
}

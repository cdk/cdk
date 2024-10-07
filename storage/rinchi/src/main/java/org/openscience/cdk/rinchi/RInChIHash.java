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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Class to provide static methods for generating hash values
 * based on SHA-256 for InChI (International Chemical Identifier) strings.
 * It includes methods to generate hashes of different lengths, which are often used
 * for identifying and comparing chemical structures.
 *
 * <p>Hashing is performed using the SHA-256 algorithm, and the resulting byte array
 * is converted to a string representation suitable for various applications.</p>
 *
 * @author Felix Bänsch
 */
class RInChIHash {

    /**
     * Generates an SHA-256 hash of the provided input string and returns it as a hexadecimal string.
     *
     * @param input the input string to hash
     * @return the hexadecimal representation of the SHA-256 hash
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String generateSha2String(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Generates an SHA-256 hash of the provided input string and returns it as an array of integers.
     *
     * @param input the input string to hash
     * @return an array of integers representing the SHA-256 hash
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static int[] generateSha2(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(input.getBytes());
        int[] unsigned = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            unsigned[i] = 0xff & bytes[i];
        }
        return unsigned;
    }

    /**
     * Generates a 4-character hash from the provided input string.
     *
     * @param input the input string to hash
     * @return a 4-character hash representation
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String hash04char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConstants.HASH_04_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26Triplet1(chksum));
        sb.append(KeyBase26.base26Triplet2(chksum));
        return sb.substring(0, 4);
    }

    /**
     * Generates a 10-character hash from the provided input string.
     *
     * @param input the input string to hash
     * @return a 10-character hash representation
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String hash10char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConstants.HASH_10_EMPTY_STRING;
        return hash12char(input).substring(0,10);
    }

    /**
     * Generates a 12-character hash from the provided input string.
     *
     * @param input the input string to hash
     * @return a 12-character hash representation
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String hash12char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConstants.HASH_12_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26Triplet1(chksum));
        sb.append(KeyBase26.base26Triplet2(chksum));
        sb.append(KeyBase26.base26Triplet3(chksum));
        sb.append(KeyBase26.base26Triplet4(chksum));
        return sb.toString();
    }

    /**
     * Generates a 14-character hash from the provided input string.
     *
     * @param input the input string to hash
     * @return a 14-character hash representation
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String hash14char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConstants.HASH_14_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26Triplet1(chksum));
        sb.append(KeyBase26.base26Triplet2(chksum));
        sb.append(KeyBase26.base26Triplet3(chksum));
        sb.append(KeyBase26.base26Triplet4(chksum));
        sb.append(KeyBase26.base26DoubletForBits56To64(chksum));
        return sb.toString();
    }

    /**
     * Generates a 17-character hash from the provided input string.
     *
     * @param input the input string to hash
     * @return a 17-character hash representation
     * @throws NoSuchAlgorithmException if the SHA-256 algorithm is not available
     */
    protected static String hash17char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConstants.HASH_17_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26Triplet1(chksum));
        sb.append(KeyBase26.base26Triplet2(chksum));
        sb.append(KeyBase26.base26Triplet3(chksum));
        sb.append(KeyBase26.base26Triplet4(chksum));
        sb.append(KeyBase26.base26DoubletForBits56To64(chksum));
        sb.append(KeyBase26.base26Triplet1(Arrays.copyOfRange(chksum, 8, chksum.length)));
        return sb.toString();
    }
}

package org.openscience.cdk.rinchi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

class RInChIHash {

    protected static String generateSha2String(String input) {
        try {
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
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected static int[] generateSha2(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes());
            int[] unsigned = new int[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                unsigned[i] = 0xff & bytes[i];
            }
            return unsigned;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    protected static String hash04char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConsts.HASH_04_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26_triplet_1(chksum));
        sb.append(KeyBase26.base26_triplet_2(chksum));
        return sb.substring(0, 4);
    }

    protected static String hash10char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConsts.HASH_10_EMPTY_STRING;
        return hash12char(input).substring(0,10);
    }

    protected static String hash12char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConsts.HASH_12_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26_triplet_1(chksum));
        sb.append(KeyBase26.base26_triplet_2(chksum));
        sb.append(KeyBase26.base26_triplet_3(chksum));
        sb.append(KeyBase26.base26_triplet_4(chksum));
        return sb.toString();
    }

    protected static String hash14char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConsts.HASH_14_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26_triplet_1(chksum));
        sb.append(KeyBase26.base26_triplet_2(chksum));
        sb.append(KeyBase26.base26_triplet_3(chksum));
        sb.append(KeyBase26.base26_triplet_4(chksum));
        sb.append(KeyBase26.base26_doublet_for_bits_56_to_64(chksum));
        return sb.toString();
    }

    protected static String hash17char(String input) throws NoSuchAlgorithmException {
        if (input.isEmpty())
            return RInChIConsts.HASH_17_EMPTY_STRING;
        int[] chksum = generateSha2(input);
        StringBuilder sb = new StringBuilder();
        sb.append(KeyBase26.base26_triplet_1(chksum));
        sb.append(KeyBase26.base26_triplet_2(chksum));
        sb.append(KeyBase26.base26_triplet_3(chksum));
        sb.append(KeyBase26.base26_triplet_4(chksum));
        sb.append(KeyBase26.base26_doublet_for_bits_56_to_64(chksum));
        sb.append(KeyBase26.base26_triplet_1(Arrays.copyOfRange(chksum, 8, chksum.length)));
        return sb.toString();
    }
}

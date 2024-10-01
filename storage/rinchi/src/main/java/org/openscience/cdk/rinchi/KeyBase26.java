package org.openscience.cdk.rinchi;

class KeyBase26 {

    protected static String getBase26Triplet(int input) {
        char[] alpha = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        char[] result = new char[3];

        int i = 0;
        for(char c1 : alpha){
            if (c1 == 'E')
                continue;
            for(char c2 : alpha) {
                for(char c3 : alpha) {
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

    protected static String getBase26Doublet(int input) {
        input = Math.abs(input);
        char firstLetter = (char) ('A' + (input / 26) % 26);
        char secondLetter = (char) ('A' + input % 26);
        return "" + firstLetter + secondLetter;
    }

    private static final String c26 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int N_UNIQUE_WEIGHTS = 12;
    static int[] weights_for_checksum = { 1,3,5,7,9,11,15,17,19,21,23,25 };

    protected static String base26_triplet_1(int[] bytes) {
        int b0 = bytes[0];
        int b1 = bytes[1] & 0x3f;
        int h = b0 | (b1 << 8);
        return getBase26Triplet(h);
    }

    protected static String base26_triplet_2(int[] bytes) {
        int b0 = bytes[1] & 0xc0;
        int b1 = bytes[2];
        int b2 = bytes[3] & 0x0f;
        int h = (b0 | b1 << 8 | b2 << 16) >> 6;
        return getBase26Triplet(h);
    }

    protected static String base26_triplet_3(int[] bytes) {
        int b0 = bytes[3] & 0xf0;
        int b1 = bytes[4];
        int b2 = bytes[5] & 0x03;
        int h = (b0 | b1 << 8 | b2 << 16) >> 4;
        return getBase26Triplet(h);
    }

    protected static String base26_triplet_4(int[] bytes) {
        int b0 = bytes[5] & 0xfc;
        int b1 = bytes[6];
        int h = (b0 | b1 << 8) >> 2;
        return getBase26Triplet(h);
    }

    protected static String  base26_doublet_for_bits_28_to_36(int[] bytes) {
        int b0 = bytes[3] & 0xf0;
        int b1 = bytes[4] & 0x1f;
        int h = (b0 | b1 << 8) >> 4;
        return getBase26Doublet(h);
    }

    protected static String  base26_doublet_for_bits_56_to_64(int[] bytes) {
        int b0 = bytes[7];
        int b1 = bytes[8] & 0x01;
        int h = (b0 | b1 << 8);
        return getBase26Doublet(h);
    }

    /**
     * Calculate check character A..Z for the string.
     * NB: ignore delimiter dashes.
     * @param input
     * @return
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
        return c26.charAt(checksum%26);
    }

    protected static String get_xtra_hash_major_hex(int[] bytes) {
        StringBuilder sb = new StringBuilder();
        int startByte = 8;
        int c = bytes[startByte] & 0xfe;
        sb.append(String.format("%02x", c));
        for (int i = 0; i < 32; i++)
            sb.append(String.format("%02x", bytes[i]));
        return sb.toString();
    }

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

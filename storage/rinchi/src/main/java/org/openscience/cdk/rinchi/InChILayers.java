package org.openscience.cdk.rinchi;

import org.openscience.cdk.exception.CDKException;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class InChILayers {

    private final StringBuilder majors;
    private final StringBuilder minors;
    private int protonCount;

    protected InChILayers(List<RInChIComponent> rcList) throws CDKException {
        this();
        appendComponents(rcList);
    }

    protected InChILayers() {
        this.majors = new StringBuilder();
        this.minors = new StringBuilder();
        this.protonCount = 0;
    }

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

    protected void appendComponents(List<RInChIComponent> rcList) throws CDKException {
        for (RInChIComponent rc : rcList) {
            if (!rc.isNoStructure()) {
                append(rc.getInchi());
            }
        }
    }

    protected String majorHash() throws NoSuchAlgorithmException {
        return RInChIHash.hash10char(majors.toString());
    }

    protected String majorHashExt() throws NoSuchAlgorithmException {
        return RInChIHash.hash17char(majors.toString());
    }

    protected String minorHash() throws NoSuchAlgorithmException {
        return protonCount2char(protonCount) + RInChIHash.hash04char(minors.toString());
    }

    protected String minorHashExt() throws NoSuchAlgorithmException {
        return protonCount2char(protonCount) + RInChIHash.hash12char(minors.toString());
    }

    protected static String emptyMajorHash() {
        return RInChIConsts.HASH_10_EMPTY_STRING;
    }

    protected static String emptyMinorHash() {
        return protonCount2char(0) + RInChIConsts.HASH_04_EMPTY_STRING;
    }

    private static char protonCount2char(int protonCount) {
        if (protonCount > 12 || protonCount < -12) {
            return 'A';
        } else {
            return (char) ('N' + protonCount);
        }
    }
}

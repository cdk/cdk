package org.openscience.cdk.fingerprint;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Default sets of atom containers aimed for use with the substructure.
 *
 * @author egonw
 *
 * @cdk.module fingerprint
 * @cdk.githash
 */
public class StandardSubstructureSets {
    /**
     * The functional groups.
     *
     * @return A set of the functional groups.
     * @throws Exception if there is an error parsing SMILES for the functional groups
     */
    public static String[] getFunctionalGroupSMARTS() throws Exception {
        return readSMARTSPattern("org/openscience/cdk/fingerprint/data/SMARTS_InteLigand.txt");
    }

    /**
     * Subset of the MACCS fingerprint definitions. The subset encompasses the pattern
     * that are countable:
     * <ul>
     *     <li>Patterns have obvious counting nature, <i>e.g., 6-Ring, C=O, etc.</i></li>
     *     <li>Patterns like <i>"Is there at least 1 of this and that?", "Are there at least 2 ..."</i> etc. are merged</li>
     *     <li>Patterns clearly corresponding to binary properties, <i>e.g., actinide group ([Ac,Th,Pa,...]), isotope, etc.,</i> have been removed.</li>
     * </ul>
     *
     *
     * @return Countable subset of the MACCS fingerprint definition
     * @throws Exception if there is an error parsing SMILES patterns
     */
    public static String[] getCountableMACCSSMARTS() throws Exception {
        return readSMARTSPattern("org/openscience/cdk/fingerprint/data/SMARTS_countable_MACCS_keys.txt");
    }

    /**
     * Load a list of SMARTS patterns from the specified file.
     *
     * Each line in the file corresponds to a pattern with the following structure:
     * PATTERN_DESCRIPTION: SMARTS_PATTERN, <i>e.g., Thioketone: [#6][CX3](=[SX1])[#6]</i>
     *
     * Empty lines and lines starting with a "#" are skipped.
     *
     * @param filename list of the SMARTS pattern to be loaded
     * @return list of strings containing the loaded SMARTS pattern
     * @throws Exception if there is an error parsing SMILES patterns
     */
    private static String[] readSMARTSPattern(String filename) throws Exception {
        InputStream ins = StandardSubstructureSets.class.getClassLoader().getResourceAsStream(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));

        List<String> tmp = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#") || line.trim().length() == 0) continue;
            String[] toks = line.split(":");
            StringBuffer s = new StringBuffer();
            for (int i = 1; i < toks.length - 1; i++)
                s.append(toks[i] + ":");
            s.append(toks[toks.length - 1]);
            tmp.add(s.toString().trim());
        }
        return tmp.toArray(new String[]{});
    }
}

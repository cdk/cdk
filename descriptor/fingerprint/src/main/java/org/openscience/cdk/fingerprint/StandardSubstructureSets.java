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

    private static String[] smarts = null;

    /**
     * The functional groups.
     *
     * @return A set of the functional groups.
     * @throws Exception if there is an error parsing SMILES for the functional groups
     */
    public static String[] getFunctionalGroupSMARTS() throws Exception {
        if (smarts != null) return smarts;

        String filename = "org/openscience/cdk/fingerprint/data/SMARTS_InteLigand.txt";
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
        smarts = tmp.toArray(new String[]{});
        return smarts;

    }

}

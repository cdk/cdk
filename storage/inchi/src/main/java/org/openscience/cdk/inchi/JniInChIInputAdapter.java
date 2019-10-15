/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.inchi;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiInput;

import java.text.DecimalFormat;
import java.util.List;
import java.util.StringTokenizer;

public class JniInChIInputAdapter extends JniInchiInput {

    /**
     * Flag indicating windows or linux.
     */
    private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase().startsWith("windows");

    /**
     * Switch character for passing options. / in windows, - on other systems.
     */
    private static final String FLAG_CHAR = IS_WINDOWS ? "/" : "-";

    public static final String FIVE_SECOND_TIMEOUT = FLAG_CHAR + "W5";

    public JniInChIInputAdapter(String options) throws JniInchiException {
        this.options = options == null ? "" : checkOptions(options);
    }

    public JniInChIInputAdapter(List<INCHI_OPTION> options) throws JniInchiException {
        this.options = options == null ? "" : checkOptions(options);
    }

    private static boolean isTimeoutOptions(String op) {
        if (op == null || op.length() < 2) return false;
        int pos = 0;
        int len = op.length();
        if (op.charAt(pos) == 'W')
            pos++;
        while (pos < len && Character.isDigit(op.charAt(pos)))
            pos++;
        if (pos < len && (op.charAt(pos) == '.' || op.charAt(pos) == ','))
            pos++;
        while (pos < len && Character.isDigit(op.charAt(pos)))
            pos++;
        return pos == len;
    }

    private static boolean isSubSecondTimeout(String op) {
        return op.indexOf('.') >= 0 || op.indexOf(',') >= 0;
    }

    private static String checkOptions(final String ops) throws JniInchiException {
        if (ops == null) {
            throw new IllegalArgumentException("Null options");
        }
        StringBuilder sbOptions = new StringBuilder();


        boolean hasUserSpecifiedTimeout = false;

        StringTokenizer tok = new StringTokenizer(ops);
        while (tok.hasMoreTokens()) {
            String op = tok.nextToken();

            if (op.startsWith("-") || op.startsWith("/")) {
                op = op.substring(1);
            }

            INCHI_OPTION option = INCHI_OPTION.valueOfIgnoreCase(op);
            if (option != null) {
                sbOptions.append(FLAG_CHAR).append(option.name());
                if (tok.hasMoreTokens()) {
                    sbOptions.append(" ");
                }
            } else if (isTimeoutOptions(op)) {
                // only reformat if we actually have a decimal
                if (isSubSecondTimeout(op)) {
                    // because the JNI-InChI library is expecting an platform number, format it as such
	                Double time = Double.parseDouble(op.substring(1));
	                DecimalFormat format = new DecimalFormat("#.##");
	                sbOptions.append(FLAG_CHAR).append('W').append(format.format(time));
                } else {
                    sbOptions.append(FLAG_CHAR).append(op);
                }
                hasUserSpecifiedTimeout = true;
                if (tok.hasMoreTokens()) {
                    sbOptions.append(" ");
                }
            }
            // 1,5 tautomer option
            else if ("15T".equals(op)) {
                sbOptions.append(FLAG_CHAR).append("15T");
                if (tok.hasMoreTokens()) {
                    sbOptions.append(" ");
                }
            }
            // keto-enol tautomer option
            else if ("KET".equals(op)) {
                sbOptions.append(FLAG_CHAR).append("KET");
                if (tok.hasMoreTokens()) {
                    sbOptions.append(" ");
                }
            } else {
                throw new JniInchiException("Unrecognised InChI option");
            }
        }

        if (!hasUserSpecifiedTimeout) {
            if (sbOptions.length() > 0)
                sbOptions.append(' ');
            sbOptions.append(FIVE_SECOND_TIMEOUT);
        }

        return sbOptions.toString();
    }

    private static String checkOptions(List<INCHI_OPTION> ops) throws JniInchiException {
        if (ops == null) {
            throw new IllegalArgumentException("Null options");
        }
        StringBuilder sbOptions = new StringBuilder();

        for (INCHI_OPTION op : ops) {
            sbOptions.append(FLAG_CHAR).append(op.name()).append(" ");
        }

        if (sbOptions.length() > 0)
            sbOptions.append(' ');
        sbOptions.append(FIVE_SECOND_TIMEOUT);

        return sbOptions.toString();
    }
}

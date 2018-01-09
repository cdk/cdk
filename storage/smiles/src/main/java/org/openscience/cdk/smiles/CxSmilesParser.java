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

package org.openscience.cdk.smiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Parse CXSMILES (ChemAxon Extended SMILES) layers. The layers are suffixed after the SMILES but before the title
 * and encode a large number of the features. CXSMILES was not intended for outside consumption so has some quirks
 * but does provide some useful features. This parser handles a subset of the grammar:
 * <br>
 * <pre>
 * - Atom Labels
 * - Atom Values
 * - Atom Coordinates
 * - Positional Variations
 * - Polymer Sgroups
 * - Atom Radicals
 * - Fragment grouping
 * </pre>
 * The following properties are ignored
 * <pre>
 * - cis/trans specification
 * - relative stereochemistry
 * </pre>
 */
final class CxSmilesParser {

    private static final char COMMA_SEPARATOR = ',';
    private static final char DOT_SEPARATOR   = '.';

    private CxSmilesParser() {
    }

    /**
     * Process atom labels from extended SMILES in a char iter.
     *
     * @param iter char iteration
     * @param dest destination of labels (atomidx->label)
     * @return parse success/failure
     */
    private static boolean processAtomLabels(final CharIter iter, final Map<Integer, String> dest) {
        int atomIdx = 0;
        while (iter.hasNext()) {

            // fast forward through empty labels
            while (iter.nextIf(';'))
                atomIdx++;

            char c = iter.next();
            if (c == '$') {
                iter.nextIf(','); // optional
                // end of atom label
                return true;
            } else {
                iter.pos--; // push back
                final int beg = iter.pos;
                int rollback = beg;
                while (iter.hasNext()) {
                    // correct step over of escaped label
                    if (iter.curr() == '&') {
                        rollback = iter.pos;
                        if (iter.nextIf('&') && iter.nextIf('#') && iter.nextIfDigit()) {
                            while (iter.nextIfDigit()){} // more digits
                            if (!iter.nextIf(';')) {
                                iter.pos = rollback;
                            } else {
                            }
                        } else {
                            iter.pos = rollback;
                        }
                    }
                    else if (iter.curr() == ';')
                        break;
                    else if (iter.curr() == '$')
                        break;
                    else
                        iter.next();
                }
                dest.put(atomIdx, unescape(iter.substr(beg, iter.pos)));
                atomIdx++;
                if (iter.nextIf('$')) {
                    iter.nextIf(','); // optional
                    return true;
                }
                if (!iter.nextIf(';'))
                    return false;
            }
        }
        return false;
    }

    private static double readDouble(CharIter iter) {
        int sign = +1;
        if (iter.nextIf('-'))
            sign = -1;
        else if (iter.nextIf('+'))
            sign = +1;
        double intPart;
        double fracPart = 0;
        int divisor = 1;

        intPart = (double) processUnsignedInt(iter);
        if (intPart < 0) intPart = 0;
        iter.nextIf('.');

        char c;
        while (iter.hasNext() && isDigit(c = iter.curr())) {
            fracPart *= 10;
            fracPart += c - '0';
            divisor *= 10;
            iter.next();
        }

        return sign * (intPart + (fracPart / divisor));
    }

    /**
     * Coordinates are written between parenthesis. The z-coord may be omitted '(0,1,),(2,3,)'.
     * @param iter input characters, iterator is progressed by this method
     * @param state output CXSMILES state
     * @return parse was a success (or not)
     */
    private static boolean processCoords(CharIter iter, CxSmilesState state) {
        if (state.atomCoords == null)
            state.atomCoords = new ArrayList<>();
        while (iter.hasNext()) {

            // end of coordinate list
            if (iter.curr() == ')') {
                iter.next();
                iter.nextIf(','); // optional
                return true;
            }

            double x = readDouble(iter);
            if (!iter.nextIf(','))
                return false;
            double y = readDouble(iter);
            if (!iter.nextIf(','))
                return false;
            double z = readDouble(iter);
            iter.nextIf(';');

            state.coordFlag = state.coordFlag || z != 0;
            state.atomCoords.add(new double[]{x, y, z});
        }
        return false;
    }

    /**
     * Fragment grouping defines disconnected components that should be considered part of a single molecule (i.e.
     * Salts). Examples include NaH, AlCl3, Cs2CO3, HATU, etc.
     *
     * @param iter input characters, iterator is progressed by this method
     * @param state output CXSMILES state
     * @return parse was a success (or not)
     */
    private static boolean processFragmentGrouping(final CharIter iter, final CxSmilesState state) {
        if (state.fragGroups == null)
            state.fragGroups = new ArrayList<>();
        List<Integer> dest = new ArrayList<>();
        while (iter.hasNext()) {
            dest.clear();
            if (!processIntList(iter, DOT_SEPARATOR, dest))
                return false;
            iter.nextIf(COMMA_SEPARATOR);
            if (dest.isEmpty())
                return true;
            state.fragGroups.add(new ArrayList<>(dest));
        }
        return false;
    }

    /**
     * Sgroup polymers in CXSMILES can be variable length so may be terminated either with the next group
     * or the end of the CXSMILES.
     *
     * @param c character
     * @return character an delimit an Sgroup
     */
    private static boolean isSgroupDelim(char c) {
        return c == ':' || c == ',' || c == '|';
    }

    private static boolean processDataSgroups(CharIter iter, CxSmilesState state) {

        if (state.dataSgroups == null)
            state.dataSgroups = new ArrayList<>(4);

        final List<Integer> atomset = new ArrayList<>();
        if (!processIntList(iter, COMMA_SEPARATOR, atomset))
            return false;

        if (!iter.nextIf(':'))
            return false;
        int beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String field = unescape(iter.substr(beg, iter.pos));

        if (!iter.nextIf(':'))
            return false;
        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String value = unescape(iter.substr(beg, iter.pos));

        if (!iter.nextIf(':')) {
            state.dataSgroups.add(new CxSmilesState.DataSgroup(atomset, field, value, "", "", ""));
            return true;
        }

        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String operator = unescape(iter.substr(beg, iter.pos));

        if (!iter.nextIf(':')) {
            state.dataSgroups.add(new CxSmilesState.DataSgroup(atomset, field, value, operator, "", ""));
            return true;
        }

        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String unit = unescape(iter.substr(beg, iter.pos));

        if (!iter.nextIf(':')) {
            state.dataSgroups.add(new CxSmilesState.DataSgroup(atomset, field, value, operator, unit, ""));
            return true;
        }

        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String tag = unescape(iter.substr(beg, iter.pos));

        state.dataSgroups.add(new CxSmilesState.DataSgroup(atomset, field, value, operator, unit, tag));

        return true;
    }

    /**
     * Polymer Sgroups describe variations of repeating units. Only the atoms and not crossing bonds are written.
     *
     * @param iter input characters, iterator is progressed by this method
     * @param state output CXSMILES state
     * @return parse was a success (or not)
     */
    private static boolean processPolymerSgroups(CharIter iter, CxSmilesState state) {
        if (state.sgroups == null)
            state.sgroups = new ArrayList<>();
        int beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        final String keyword = iter.substr(beg, iter.pos);
        if (!iter.nextIf(':'))
            return false;
        final List<Integer> atomset = new ArrayList<>();
        if (!processIntList(iter, COMMA_SEPARATOR, atomset))
            return false;


        String subscript;
        String supscript;

        if (!iter.nextIf(':'))
            return false;

        // "If the subscript equals the keyword of the Sgroup this field can be empty", ergo
        // if omitted it equals the keyword
        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        subscript = unescape(iter.substr(beg, iter.pos));
        if (subscript.isEmpty())
            subscript = keyword;

        // "In the superscript only connectivity and flip information is allowed.", default
        // appears to be "eu" either/unspecified
        if (!iter.nextIf(':'))
            return false;
        beg = iter.pos;
        while (iter.hasNext() && !isSgroupDelim(iter.curr()))
            iter.next();
        supscript = unescape(iter.substr(beg, iter.pos));
        if (supscript.isEmpty())
            supscript = "eu";

        if (iter.nextIf(',') || iter.curr() == '|') {
            state.sgroups.add(new CxSmilesState.PolymerSgroup(keyword, atomset, subscript, supscript));
            return true;
        }
        // not supported: crossing bond info (difficult to work out from doc) and bracket orientation

        return false;
    }

    /**
     * Positional variation/multi centre bonding. Describe as a begin atom and one or more end points.
     *
     * @param iter input characters, iterator is progressed by this method
     * @param state output CXSMILES state
     * @return parse was a success (or not)
     */
    private static boolean processPositionalVariation(CharIter iter, CxSmilesState state) {
        if (state.positionVar == null)
            state.positionVar = new TreeMap<>();
        while (iter.hasNext()) {
            if (isDigit(iter.curr())) {
                final int beg = processUnsignedInt(iter);
                if (!iter.nextIf(':'))
                    return false;
                List<Integer> endpoints = new ArrayList<>(6);
                if (!processIntList(iter, DOT_SEPARATOR, endpoints))
                    return false;
                iter.nextIf(',');
                state.positionVar.put(beg, endpoints);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * CXSMILES radicals.
     *
     * @param iter input characters, iterator is progressed by this method
     * @param state output CXSMILES state
     * @return parse was a success (or not)
     */
    private static boolean processRadicals(CharIter iter, CxSmilesState state) {
        if (state.atomRads == null)
            state.atomRads = new TreeMap<>();
        CxSmilesState.Radical rad;
        switch (iter.next()) {
            case '1':
                rad = CxSmilesState.Radical.Monovalent;
                break;
            case '2':
                rad = CxSmilesState.Radical.Divalent;
                break;
            case '3':
                rad = CxSmilesState.Radical.DivalentSinglet;
                break;
            case '4':
                rad = CxSmilesState.Radical.DivalentTriplet;
                break;
            case '5':
                rad = CxSmilesState.Radical.Trivalent;
                break;
            case '6':
                rad = CxSmilesState.Radical.TrivalentDoublet;
                break;
            case '7':
                rad = CxSmilesState.Radical.TrivalentQuartet;
                break;
            default:
                return false;
        }
        if (!iter.nextIf(':'))
            return false;
        List<Integer> dest = new ArrayList<>(4);
        if (!processIntList(iter, COMMA_SEPARATOR, dest))
            return false;
        for (Integer atomidx : dest)
            state.atomRads.put(atomidx, rad);
        return true;
    }

    /**
     * Parse an string possibly containing CXSMILES into an intermediate state
     * ({@link CxSmilesState}) representation.
     *
     * @param str input character string (SMILES title field)
     * @param state output CXSMILES state
     * @return position where CXSMILES ends (below 0 means no CXSMILES)
     */
    static int processCx(final String str, final CxSmilesState state) {

        final CharIter iter = new CharIter(str);

        if (!iter.nextIf('|'))
            return -1;

        while (iter.hasNext()) {
            switch (iter.next()) {
                case '$': // atom labels and values
                    // dest is atom labels by default
                    Map<Integer, String> dest;

                    // check for atom values
                    if (iter.nextIf("_AV:"))
                        dest = state.atomValues = new TreeMap<>();
                    else
                        dest = state.atomLabels = new TreeMap<>();

                    if (!processAtomLabels(iter, dest))
                        return -1;
                    break;
                case '(': // coordinates
                    if (!processCoords(iter, state))
                        return -1;
                    break;
                case 'c': // cis/trans/unspec ignored
                case 't':
                    // c/t:
                    if (iter.nextIf(':')) {
                        if (!skipIntList(iter, COMMA_SEPARATOR))
                            return -1;
                    }
                    // ctu:
                    else if (iter.nextIf("tu:")) {
                        if (!skipIntList(iter, COMMA_SEPARATOR))
                            return -1;
                    }
                    break;
                case 'r': // relative stereochemistry ignored
                    if (!iter.nextIf(':'))
                        return -1;
                    if (!skipIntList(iter, COMMA_SEPARATOR))
                        return -1;
                    break;
                case 'l': // lone pairs ignored
                    if (!iter.nextIf("p:"))
                        return -1;
                    if (!skipIntMap(iter))
                        return -1;
                    break;
                case 'f': // fragment grouping
                    if (!iter.nextIf(':'))
                        return -1;
                    if (!processFragmentGrouping(iter, state))
                        return -1;
                    break;
                case 'S': // Sgroup polymers
                    if (iter.nextIf("g:")) {
                        if (!processPolymerSgroups(iter, state))
                            return -1;
                    }
                    else if (iter.nextIf("gD:")) {
                        if (!processDataSgroups(iter, state))
                            return -1;
                    }
                    else {
                        return -1;
                    }
                    break;
                case 'm': // positional variation
                    if (!iter.nextIf(':'))
                        return -1;
                    if (!processPositionalVariation(iter, state))
                        return -1;
                    break;
                case '^': // Radicals
                    if (!processRadicals(iter, state))
                        return -1;
                    break;
                case 'C':
                case 'H': // coordination and hydrogen bonding ignored
                    if (!iter.nextIf(':'))
                        return -1;
                    while (iter.hasNext() && isDigit(iter.curr())) {
                        if (!skipIntList(iter, DOT_SEPARATOR))
                            return -1;
                        iter.nextIf(',');
                    }
                    break;
                case '|': // end of CX
                    // consume optional separators
                    if (!iter.nextIf(' ')) iter.nextIf('\t');
                    return iter.pos;
                default:
                    return -1;
            }
        }

        return -1;
    }

    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean skipIntList(CharIter iter, char sep) {
        while (iter.hasNext()) {
            char c = iter.curr();
            if (isDigit(c) || c == sep)
                iter.next();
            else
                return true;
        }
        // ran of end
        return false;
    }

    private static boolean skipIntMap(CharIter iter) {
        while (iter.hasNext()) {
            char c = iter.curr();
            if (isDigit(c) || c == ',' || c == ':')
                iter.next();
            else
                return true;
        }
        // ran of end
        return false;
    }

    private static int processUnsignedInt(CharIter iter) {
        if (!iter.hasNext())
            return -1;
        char c = iter.curr();
        if (!isDigit(c))
            return -1;
        int res = c - '0';
        iter.next();
        while (iter.hasNext() && isDigit(c = iter.curr())) {
            res = res * 10 + c - '0';
            iter.next();
        }
        return res;
    }

    /**
     * Process a list of unsigned integers.
     *
     * @param iter char iter
     * @param sep the separator
     * @param dest output
     * @return int-list was successfully processed
     */
    private static boolean processIntList(CharIter iter, char sep, List<Integer> dest) {
        while (iter.hasNext()) {
            char c = iter.curr();
            if (isDigit(c)) {
                int r = processUnsignedInt(iter);
                if (r < 0) return false;
                iter.nextIf(sep);
                dest.add(r);
            } else {
                return true;
            }
        }
        // ran off end
        return false;
    }

    static String unescape(String str) {
        int dst = 0;
        int src = 0;
        char[] chars = str.toCharArray();
        int len = chars.length;
        while (src < chars.length) {
            // match the pattern &#[0-9][0-9]*;
            if (src + 3 < len && chars[src] == '&' && chars[src+1] == '#' && isDigit(chars[src+2])) {
                int tmp  = src+2;
                int code = 0;
                while (tmp < len && isDigit(chars[tmp])) {
                    code *= 10;
                    code += chars[tmp] - '0';
                    tmp++;
                }
                if (tmp < len && chars[tmp] == ';') {
                    src = tmp+1;
                    chars[dst++] = (char) code;
                    continue;
                }
            }
            chars[dst++] = chars[src++];
        }
        return new String(chars, 0, dst);
    }

    /**
     * Utility for parsing a sequence of characters. The char iter allows us to pull
     * of one or more characters at a time and track where we are in the string.
     */
    private static final class CharIter {

        private final String str;
        private final int    len;
        private int pos = 0;

        CharIter(String str) {
            this.str = str;
            this.len = str.length();
        }

        /**
         * If the next character matches the provided query the iterator is progressed.
         *
         * @param c query character
         * @return iterator was moved forwards
         */
        boolean nextIf(char c) {
            if (!hasNext() || str.charAt(pos) != c)
                return false;
            pos++;
            return true;
        }

        boolean nextIfDigit() {
            if (!hasNext() || !isDigit(str.charAt(pos)))
                return false;
            pos++;
            return true;
        }

        /**
         * If the next sequence of characters matches the prefix the iterator
         * is progressed to character following the prefix.
         *
         * @param prefix prefix string
         * @return iterator was moved forwards
         */
        boolean nextIf(final String prefix) {
            boolean res;
            if (res = this.str.startsWith(prefix, pos))
                pos += prefix.length();
            return res;
        }

        /**
         * Is there more chracters to read?
         *
         * @return whether more characters are available
         */
        boolean hasNext() {
            return pos < len;
        }

        /**
         * Access the current character of the iterator.
         *
         * @return charactor
         */
        char curr() {
            return str.charAt(pos);
        }

        /**
         * Access the current character of the iterator and move
         * to the next position.
         *
         * @return charactor
         */
        char next() {
            return str.charAt(pos++);
        }


        /**
         * Access a substring from the iterator.
         *
         * @param beg begin position (inclusive)
         * @param end end position (exclusive)
         * @return substring
         */
        String substr(int beg, int end) {
            return str.substring(beg, end);
        }
    }
}

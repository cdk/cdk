/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.renderer.generators.standard;

import org.openscience.cdk.config.Elements;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Utility class for handling/formatting abbreviation (superatom) labels.
 * 
 * Depending on orientation a label may need to be reversed. For example
 * consider '-OAc', if the bond exits from the right it is preferable to
 * write it 'AcO-'. Other labels don't need reversing at all (e.g. tBu).
 * We reverse labels by spiting them up into 'tokens', reversing token order, * and then joining them back together.
 * 
 * Abbreviation labels that are formulas benefit from sub and subscripting
 * certain parts. For example OPO3H2 looks better with the digits 3 and 2
 * rendered in subscript.
 */
final class AbbreviationLabel {

    /**
     * Better rendering of negative charge by using minus and not
     * an ascii hyphen.
     */
    private static final String MINUS_STRING = "\u2212";

    // chemical symbol prefixes
    private final static String[] PREFIX_LIST = new String[]{
            "n", "norm", "n-", "c", "cy", "cyc", "cyclo", "c-", "cy-", "cyc-", "i", "iso", "i-", "t", "tert", "t-", "s",
            "sec", "s-", "o", "ortho", "o-", "m", "meta", "m-", "p", "para", "p-", "1-", "2-", "3-", "4-", "5-", "6-",
            "7-", "8-", "9-"
    };

    // see https://en.wikipedia.org/wiki/Wikipedia:Naming_conventions_(chemistry)#Prefixes_in_titles
    private final static String[] ITAL_PREFIX = new String[]{
            "n", "norm", "sec", "s", "tert", "t",
            "ortho", "o", "meta", "m", "para", "p"
    };

    // chemical symbols excluding periodic symbols which are loaded separately
    // Some of these are derived from https://github.com/openbabel/superatoms that
    // has the following license:
    //    This is free and unencumbered software released into the public domain.
    //
    //    Anyone is free to copy, modify, publish, use, compile, sell, or
    //    distribute this software, either in source code form or as a compiled
    //    binary, for any purpose, commercial or non-commercial, and by any
    //    means.
    //
    //    In jurisdictions that recognize copyright laws, the author or authors
    //    of this software dedicate any and all copyright interest in the
    //    software to the public domain. We make this dedication for the benefit
    //    of the public at large and to the detriment of our heirs and
    //    successors. We intend this dedication to be an overt act of
    //    relinquishment in perpetuity of all present and future rights to this
    //    software under copyright law.
    //
    //    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    //    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    //    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    //    IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
    //    OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
    //    ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    //    OTHER DEALINGS IN THE SOFTWARE.
    //
    //    For more information, please refer to <http://unlicense.org/>
    private final static String[] SYMBOL_LIST = new String[]{"acac", "Ace", "Acetyl", "Acyl", "Ad", "All", "Alloc", "Allyl", "Amyl", "AOC",
                                                             "BDMS", "Benzoyl", "Benzyl", "Bn", "BOC", "Boc", "BOM", "bpy", "Bromo", "Bs", "Bu", "But", "Butyl", "Bz", "Bzl",
                                                             "Car", "Cbz", "Chloro", "CoA", "Cy",
                                                             "dppf", "dppp", "dba", "D", "Dan", "Dansyl", "DEIPS", "DEM", "Dip", "Dmb", "DPA", "DTBMS",
                                                             "EE", "EOM", "Et", "Ethyl",
                                                             "Fluoro", "FMOC", "Fmoc", "Formyl",
                                                             "Heptyl", "Hexyl",
                                                             "Iodo", "IPDMS",
                                                             "Me", "MEM", "Mesityl", "Mesyl", "Methoxy", "Methyl", "MOM", "Ms",
                                                             "Nitro",
                                                             "Oct", "Octyl",
                                                             "PAB", "Pentyl", "Ph", "Phenyl", "Pivaloyl", "PMB", "Pro", "Propargyl", "Propyl", "Pv",
                                                             "R", "SEM",
                                                             "T", "TBS", "TBDMS", "Trt", "TBDPS", "TES", "Tf", "THP", "THPO", "TIPS", "TMS", "Tos", "Tol", "Tosyl", "Tr", "Troc",
                                                             "Vinyl", "Voc", "Z"};

    private static Trie PREFIX_TRIE = new Trie();
    private static Trie ITAL_PREFIX_TRIE = new Trie();
    private static Trie SYMBOL_TRIE = new Trie();

    // build the tries on class init
    static {
        for (String str : PREFIX_LIST)
            insert(PREFIX_TRIE, str, 0);
        for (String str : ITAL_PREFIX)
            insert(ITAL_PREFIX_TRIE, str, 0);
        for (Elements elem : Elements.values())
            if (!elem.symbol().isEmpty())
                insert(SYMBOL_TRIE, elem.symbol(), 0);
        for (String str : SYMBOL_LIST)
            insert(SYMBOL_TRIE, str, 0);
    }

    static int STYLE_NORMAL    = 0;
    static int STYLE_SUBSCRIPT = -1;
    static int STYLE_SUPSCRIPT = +1;
    static int STYLE_ITALIC    = 2;

    /**
     * A small class to help describe which parts of a string
     * are super and subscript (style field).
     */
    static final class FormattedText {
        String text;
        final int style;

        public FormattedText(String text, int style) {
            this.text = text;
            this.style = style;
        }
    }

    /**
     * Split a label it to recognised tokens for reversing, the
     * validity of the label is not checked! The method is intended
     * for zero/single attachments only and linkers are not supported.
     * 
     * 
     * Example: 
     * {@code NHCH2Ph -> N,H,C,H2,Ph -> reverse/join -> PhH2CHN}
     * 
     * 
     * 
     * The method return value signals whether formula
     * formatting (sub- and super- script) can be applied.
     *
     * @param label  abbreviation label
     * @param tokens the list of tokens from the input (n>0)
     * @return whether the label parsed okay (i.e. apply formatting)
     */
    static boolean parse(String label, List<String> tokens) {

        int i = 0;
        int len = label.length();

        while (i < len) {

            int st = i;
            int last;

            char c = label.charAt(i);

            // BRACKETS we treat as separate
            if (c == '(' || c == ')') {
                tokens.add(Character.toString(c));
                i++;

                // digits following closing brackets
                if (c == ')') {
                    st = i;
                    while (i < len && isDigit(c = label.charAt(i))) {
                        i++;
                    }
                    if (i > st)
                        tokens.add(label.substring(st, i));
                }

                continue;
            }

            // separators
            if (c == '/' || c == '·' || c == '.' || c == '•' || c == '=') {
                tokens.add(Character.toString(c));
                i++;

                int beg = i;
                while (i < label.length() && isDigit(label.charAt(i))) {
                    i++;
                }
                if (i > beg)
                    tokens.add(label.substring(beg, i));
                continue;
            }

            // SYMBOL Tokens
            // optional prefix o- m- p- etc.
            if ((last = findPrefix(PREFIX_TRIE, label, i, -1)) > 0) {
                i += (last - i);
            }
            final int symSt = i;

            // a valid symbol token
            if ((last = findPrefix(SYMBOL_TRIE, label, i, -1)) > 0) {
                i += (last - i);
                // an optional number suffix e.g. O2 F3 Ph3 etc.
                while (i < len && isDigit(label.charAt(i))) {
                    i++;
                }
            }
            // a charge token, only if it's after some other parts
            else if (i == st && st > 0) {
                c = norm(label.charAt(i));
                if (c == '-' || c == '+') {
                    i++;
                    while (i < len && isDigit(label.charAt(i))) {
                        i++;
                    }
                    // we expect charge at the end of the string.. if there is
                    // still more it's not good input
                    if (i < len) {
                        return failParse(label, tokens);
                    }
                }
            }

            if (i == st || i == symSt) {
                return failParse(label, tokens);
            }

            tokens.add(label.substring(st, i));
        }

        return true;
    }

    /**
     * Abort call when a label could not be parsed. The tokens are cleared
     * and replaced with the original label.
     *
     * @param label  the original label
     * @param tokens the current tokens
     * @return always returns false
     */
    private static boolean failParse(String label, List<String> tokens) {
        tokens.clear();
        tokens.add(label);
        return false;
    }

    private static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); i++)
            if (!isDigit(str.charAt(i)))
                return false;
        return true;
    }

    /**
     * Reverse a list of tokens for display, flipping
     * brackets as needed.
     *
     * @param tokens list of tokens
     */
    static void reverse(List<String> tokens) {
        Collections.reverse(tokens);
        // now flip brackets and move numbers
        Deque<String> numbers = new ArrayDeque<>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equals("(")) {
                tokens.set(i, ")");
                String num = numbers.pop();
                if (!num.isEmpty()) {
                    tokens.add(i + 1, num);
                    i++;
                }
            }
            else if (token.equals(")")) {
                tokens.set(i, "(");
                if (i>0 && isNumber(tokens.get(i - 1))) {
                    numbers.push(tokens.remove(i - 1));
                    i--;
                } else {
                    numbers.push("");
                }
            }
        }
    }

    /**
     * Format and optimise the tokens for rendering (e.g. "OAc" or "AcO"
     * can be done in one go) and mark tokens that are subscript (-1)
     * or superscript (+1).
     *
     * @param tokens tokenized label
     */
    static List<FormattedText> format(List<String> tokens) {
        List<FormattedText> texts = new ArrayList<>(2 + tokens.size());
        for (String token : tokens) {
            // charges
            if (isChargeToken(token)) {
                // charges are superscript
                String sign = Character.toString(norm(token.charAt(0)));
                String coef = token.length() > 1 ? token.substring(1) : "";
                if (sign.equals("-")) sign = MINUS_STRING;
                texts.add(new FormattedText(coef + sign, STYLE_SUPSCRIPT));
            }
            // subscript number after brackets
            else if (token.length() == 1 && isDigit(token.charAt(0)) && !texts.isEmpty() && texts.get(texts.size()-1).text.equals(")")) {
                texts.add(new FormattedText(token, STYLE_SUBSCRIPT));
            }
            else {
                // optional prefix
                int i = findPrefix(ITAL_PREFIX_TRIE, token, 0, 0);
                // find a numeric suffix to subscript
                int j = token.length();
                while (j > 0 && isDigit(token.charAt(j - 1)))
                    j--;
                // check if we have numeric suffix
                if (j > 0 && j < token.length()) {
                    if (i > j) i = 0; // prefix overlaps with suffix so don't use it
                    if (i > 0)
                        texts.add(new FormattedText(token.substring(0, i), STYLE_ITALIC));
                    texts.add(new FormattedText(token.substring(i, j), STYLE_NORMAL));
                    texts.add(new FormattedText(token.substring(j), STYLE_SUBSCRIPT));
                } else {
                    if (i > 0)
                        texts.add(new FormattedText(token.substring(0, i), STYLE_ITALIC));
                    texts.add(new FormattedText(token.substring(i), STYLE_NORMAL));
                }
            }
        }

        return texts;
    }

    static void reduce(List<FormattedText> texts, int from, int to) {
        List<FormattedText> tmp = new ArrayList<>(texts.size());
        FormattedText prev = null;
        tmp.addAll(texts.subList(0, from));
        for (FormattedText curr : texts.subList(from, to)) {
            if (prev == null || prev.style != curr.style) {
                tmp.add(prev = curr);
            } else {
                prev.text += curr.text;
            }
        }
        tmp.addAll(texts.subList(to, texts.size()));
        texts.clear();
        texts.addAll(tmp);
    }

    /**
     * Determines if the token is representing a charge.
     *
     * @param token string token
     * @return the token is a charge label (+2, -, +, -2)
     */
    private static boolean isChargeToken(String token) {
        return token.length() > 0 && (norm(token.charAt(0)) == '-' || token.charAt(0) == '+');
    }

    /**
     * Basic method to check if a character is a digit.
     *
     * @param c character
     * @return the character is a digit
     */
    private static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Normalise dashes in-case a user has entered one by accident.
     *
     * @param c character
     * @return normalised character
     */
    private static char norm(char c) {
        switch (c) {
            case '\u002d': // hyphen
            case '\u2012': // figure dash
            case '\u2013': // en-dash
            case '\u2014': // em-dash
            case '\u2212': // minus
                return '-'; // 002d
            default:
                return c;
        }
    }

    /**
     * Find the longest prefix from position (i) in this string that
     * is present in the trie symbol table.
     *
     * @param trie   trie node (start with root)
     * @param string string to find a prefix of
     * @param i      the position in the string
     * @param best   best score so far (-1 to start)
     * @return the length of the prefix
     */
    private static int findPrefix(Trie trie, String string, int i, int best) {
        if (trie == null)
            return best;
        if (trie.token != null)
            best = i;
        if (i == string.length())
            return best;
        final char c = norm(string.charAt(i));
        if (c > 128)
            return best;
        return findPrefix(trie.children[c], string, i + 1, best);
    }

    /**
     * Insert a string (str) into the trie.
     *
     * @param trie trie node
     * @param str  the string to insert
     * @param i    index in the string
     * @return a created child node or null
     */
    private static Trie insert(Trie trie, String str, int i) {
        if (trie == null)
            trie = new Trie();
        if (i == str.length()) {
            trie.token = str;
        } else {
            final char c = str.charAt(i);
            trie.children[c] = insert(trie.children[c], str, i + 1);
        }
        return trie;
    }

    /**
     * A trie symbol table node.
     */
    private static final class Trie {
        String token;
        Trie[] children = new Trie[128];
    }
}

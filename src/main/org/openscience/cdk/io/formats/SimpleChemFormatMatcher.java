/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

package org.openscience.cdk.io.formats;

import java.util.List;

/**
 * A simple line matcher which delegates format matching to the previous
 * per-line implementation.
 *
 * @author John May
 * @cdk.module ioformats
 */
abstract class SimpleChemFormatMatcher extends AbstractResourceFormat
                                       implements IChemFormatMatcher {

    /**
     * Simple implementation, runs the lines one-by-one through {@link
     * #matches(int, String)} and returns true if any line matches.
     *
     * @param lines lines of the input to be checked
     * @return runs the lines
     */
    @Override public final MatchResult matches(final List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            if (matches(i + 1, lines.get(i)))
                return new MatchResult(true, this, i);
        }
        return NO_MATCH;
    }
}

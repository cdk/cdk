/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io.formats;

import com.google.common.primitives.Ints;
import java.util.List;

/**
 * This interface is used for classes that are able to match a certain
 * chemical file format. For example: Chemical Markup Language, PDB etc.
 *
 * @cdk.module ioformats
 * @cdk.githash
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2004-10-25
 **/
public interface IChemFormatMatcher extends IChemFormat {

    /**
     * Method that checks whether the given lines are part of the format read by
     * this reader.
     *
     * @param lines lines of the input to be checked
     * @return whether the format matched and when it matched
     */
    MatchResult matches(List<String> lines);

    /** Convenience method for indicating a format did not match. */
    static MatchResult NO_MATCH = new MatchResult(false, null, Integer.MAX_VALUE);

    /**
     * Simple class holds whether a format matcher matched, when it matched and
     * what the format was. The result is comparable to be prioritised (lower
     * match position being favoured).
     */
    static final class MatchResult implements Comparable<MatchResult> {

        /** Did the format match. */
        private final boolean     matched;

        /** When did the format match. */
        private final int         position;

        /** Which format matched. */
        private final IChemFormat format;

        public MatchResult(boolean matched, IChemFormat format, int position) {
            this.matched = matched;
            this.format = format;
            this.position = position;
        }

        /**
         * Did the chem format match.
         *
         * @return whether the format matched
         */
        public boolean matched() {
            return matched;
        }

        /**
         * What was the format which matched if there was a match ({@link
         * #matched()}).
         *
         * @return the format which matched
         * @throws IllegalArgumentException there was no match
         */
        public IChemFormat format() {
            if (!matched) throw new IllegalArgumentException("result did not match");
            return format;
        }

        /**
         * Compares the match result with another, results with lower position
         * are ordered before those with higher position.
         */
        @Override
        public int compareTo(MatchResult that) {
            return Ints.compare(this.position, that.position);
        }
    }
}

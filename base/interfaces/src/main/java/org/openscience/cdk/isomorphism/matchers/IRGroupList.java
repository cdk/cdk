/* Copyright (C) 2021 John Mayfield
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.isomorphism.matchers;

import java.util.List;

/**
 * Represents a list of Rgroup substitutes to be associated with some key in an
 * {@link IRGroupQuery}.
 *
 * @author John Mayfield
 */
public interface IRGroupList {

    /**
     * Get the Rgroup number, for example R1 => 1.
     * @return the Rgroup number
     */
    int getRGroupNumber();

    /**
     * Access the dependant Rgroup number if R1 then R2.
     * @return the required Rgroup number.
     */
    int getRequiredRGroupNumber();

    /**
     * Access the list of possible RGroups.
     * @return the rgroups.
     */
    List<IRGroup> getRGroups();

    /**
     * Indicates that sites labeled with this Rgroup may only be
     * substituted with a member of the Rgroup or with hydrogen.
     */
    boolean isRestH();

    /**
     * Matches the 'occurrence' condition with a provided maximum number of
     * RGroup attachments. Returns the valid occurrences (numeric) for these
     * two combined. If none found, returns empty list.
     * <br>
     * Example: if R1 occurs 3 times attached to some root structure, then
     * stating "&gt;5" as an occurrence for that RGoupList does not make
     * sense: the example R1 can occur 0..3 times. Empty would be returned.<br>
     * If the occurence would be &gt;2, then 3 would be returned. Etcetera.
     *
     * @param maxAttachments number of attachments
     * @return valid values by combining a max for R# with the occurrence cond.
     */
    List<Integer> matchOccurence(int maxAttachments);

    /**
     * Occurrence required:
     * <UL>
     * <LI>n : exactly n ;</LI>
     * <LI>n - m : n through m ;</LI>
     * <LI>&#62; n : greater than n ;</LI>
     * <LI>&#60; n : fewer than n ;</LI>
     * <LI>default (blank) is > 0 ;</LI>
     * </UL>
     * Any non-contradictory combination of the preceding values is also
     * allowed; for example "1, 3-7, 9, >11".
     */
    String getOccurrence();
}

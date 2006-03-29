/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.applications;

import java.util.StringTokenizer;

/**
 * Helper class for comparing API version numbers using the CVS numbering
 * scheme. For example, 1.12 is bigger than 1.4. Does not take into account
 * version numbers with subsub and subsubsub versions yet, e.g. 1.2.1.1 as
 * used in CVS branches. (Patch welcome).
 *
 * @cdk.module standard
 */
public class APIVersionTester {

    /**
     * Tests wether the second string is bigger than or equals to the first.
     */
    public static boolean isBiggerOrEqual(String one, String two) {
        StringTokenizer oneTokens = new StringTokenizer(one, ".");
        StringTokenizer twoTokens = new StringTokenizer(two, ".");
        int majorOne = Integer.parseInt(oneTokens.nextToken());
        int majorTwo = Integer.parseInt(twoTokens.nextToken());
        if (majorOne == majorTwo) {
            int minorOne = Integer.parseInt(oneTokens.nextToken());
            int minorTwo = Integer.parseInt(twoTokens.nextToken());
            return minorTwo >= minorOne;
        } else {
            return majorTwo > majorOne;
        }
    }
    
    /**
     * Tests wether the second string is smaller than the first.
     */
    public static boolean isSmaller(String one, String two) {
        StringTokenizer oneTokens = new StringTokenizer(one, ".");
        StringTokenizer twoTokens = new StringTokenizer(two, ".");
        int majorOne = Integer.parseInt(oneTokens.nextToken());
        int majorTwo = Integer.parseInt(twoTokens.nextToken());
        if (majorOne == majorTwo) {
            int minorOne = Integer.parseInt(oneTokens.nextToken());
            int minorTwo = Integer.parseInt(twoTokens.nextToken());
            return minorTwo < minorOne;
        } else {
            return majorTwo < majorOne;
        }
    }
    
}


/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.tools;

import org.openscience.cdk.*;
import java.util.*;

/** 
  * Compares elements based on the order commonly used in 
  * molecular formula. Order:
  * C, H, other elements in alphabetic order.
  *
  * @cdkPackage standard
  *
  * @keyword element, sorting
  */
public class ElementComparator implements Comparator {

    /**
     * Returns a positive if o1 comes before o2 in a molecular formula.
     */
    public int compare(Object o1, Object o2) {
        if (o1.equals("C")) {
            if (o2.equals("C")) {
                return 0;
            } else {
                return -1;
            }
        } else if (o1.equals("H")) {
            if (o2.equals("C")) {
                return 1;
            } else if (o2.equals("H")) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (o2.equals("C") || o2.equals("H")) {
                return 1;
            } else {
                return ((String)o1).compareTo((String)o2);
            }
        }
    }

}



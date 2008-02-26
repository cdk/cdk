/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1998-2007  Dan Gezelter
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
 */
package org.openscience.cdk.math;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;


/**
 * Converts a String representation of a Fortran double to a double.
 *
 * <p>A modified version of the atof method provided in the Core Java
 * books by Cay S. Horstmann & Gary Cornell.  The main difference
 * here is that we scan for fortran double precision characters
 * ('D' and 'd') which often cause the C versions of atof to
 * barf.
 *
 * @author Dan Gezelter
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.math.FortranFormatTest")
public class FortranFormat {
    /**
     * Converts a string of digits to an double
     *
     * @param s a string denoting a double
     */
    @TestMethod("testAtof_String")
    public static double atof(String s) {  
        int i = 0;
        int sign = 1;
        double r = 0; // integer part
        double p = 1; // exponent of fractional part
        int state = 0; // 0 = int part, 1 = frac part
        
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        if (i < s.length() && s.charAt(i) == '-') { sign = -1; i++; }
        else if (i < s.length() && s.charAt(i) == '+') { i++; }
        while (i < s.length())
            {  char ch = s.charAt(i);
            if ('0' <= ch && ch <= '9')
                {  if (state == 0)
                    r = r * 10 + ch - '0';
                else if (state == 1)
                    {  p = p / 10;
                    r = r + p * (ch - '0');
                    }
                }
            else if (ch == '.') 
                {  if (state == 0) state = 1; 
                else return sign * r;
                }
            else if (ch == 'e' || ch == 'E' || ch == 'd' || ch == 'D')
                {  long e = (int)parseLong(s.substring(i + 1), 10);
                return sign * r * Math.pow(10, e);
                }
            else return sign * r;
            i++;
            }
        return sign * r;
    }

    private static long parseLong(String s, int base) {  
        int i = 0;
        int sign = 1;
        long r = 0;
        
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        if (i < s.length() && s.charAt(i) == '-') { sign = -1; i++; }
        else if (i < s.length() && s.charAt(i) == '+') { i++; }
        while (i < s.length())
            {  char ch = s.charAt(i);
            if ('0' <= ch && ch < '0' + base)
                r = r * base + ch - '0';
            else if ('A' <= ch && ch < 'A' + base - 10)
                r = r * base + ch - 'A' + 10 ;
            else if ('a' <= ch && ch < 'a' + base - 10)
                r = r * base + ch - 'a' + 10 ;
            else 
                return r * sign;
            i++;
            }
        return r * sign;      
    }
    
}

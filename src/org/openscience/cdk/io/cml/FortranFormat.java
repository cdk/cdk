/*
 * @(#)FortranFormat.java    1.0 98/08/27
 *
 * Copyright (c) 1998 J. Daniel Gezelter All Rights Reserved.
 *
 * J. Daniel Gezelter grants you ("Licensee") a non-exclusive, royalty
 * free, license to use, modify and redistribute this software in
 * source and binary code form, provided that the following conditions 
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED.  J. DANIEL GEZELTER AND HIS LICENSORS SHALL NOT BE LIABLE
 * FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO
 * EVENT WILL J. DANIEL GEZELTER OR HIS LICENSORS BE LIABLE FOR ANY
 * LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF J. DANIEL GEZELTER HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line
 * control of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.  
 */

package org.openscience.cdk.io.cml;

import java.io.*;

public class FortranFormat {
    /* A modified version of the atof method provided in the Core Java
     * books by Cay S. Horstmann & Gary Cornell.  The main difference
     * here is that we scan for fortran double precision characters 
     * ('D' and 'd') which often cause the C versions of atof to
     * barf.
     *   --Dan Gezelter
     */
    
    /** 
     * Converts a string of digits to an double
     * @param s a string
     */
   
    public static double atof(String s) {  
        int i = 0;
        int sign = 1;
        double r = 0; // integer part
        double f = 0; // fractional part
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

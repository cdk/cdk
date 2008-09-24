/* 
 * $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.index;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tools to work with CAS registry numbers.
 * 
 * <p><b>References:</b> 
 * <ul>
 * 	<li><a href="http://www.cas.org/EO/regsys.html">A CAS Registry Number</a></li>
 * 	<li><a href="http://www.cas.org/EO/checkdig.html">Check Digit Verification of CAS Registry Numbers</a></li>
 * </ul>
 * </p>
 * 
 * @author Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.svnrev  $Revision$
 * @author Nathana&euml;l "M.Le_maudit" Mazuir
 *
 * @see <a href="http://www.cas.org">CAS website</a>
 * 
 * @cdk.created 2003-06-30
 * @cdk.keyword CAS number
 * @cdk.require java1.4+
 */
@TestClass("org.openscience.cdk.index.CASNumberTest")
public class CASNumber {

    /**
     * Checks whether the registry number is valid.
     *
     * @param casNumber  the CAS number to validate
     * @cdk.keyword CAS number
     * @cdk.keyword validation
     * @return true if a valid CAS number, false otherwise
     */
    @TestMethod("testInvalidCheckDigits,testValidNumbers")
    public static boolean isValid(String casNumber) {
        boolean overall = true;
        /*
         * check format
         */
        String format = "^(\\d+)-(\\d\\d)-(\\d)$";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(casNumber);
        overall = overall && matcher.matches();
        
        if (matcher.matches()) {
			/*
			 * check number
			 */
			String part1 = matcher.group(1);
			String part2 = matcher.group(2);
			String part3 = matcher.group(3);
	                int part1value = Integer.parseInt(part1);
			if (part1value < 50) {
	                    overall = false; 
			    // CAS numbers start at 50-00-0
			} else {
                int digit = CASNumber.calculateCheckDigit(part1, part2);
                overall = overall && (digit == Integer.parseInt(part3));
            }
        }
        
        return overall;
    }

    private static int calculateCheckDigit(String part1, String part2) {
        int total = 0;
        total = total + 1*Integer.parseInt(part2.substring(1,2));
        total = total + 2*Integer.parseInt(part2.substring(0,1));
        int length = part1.length();
        for (int i=0; i<length; i++) {
            total = total + (3+i)*Integer.parseInt(part1.substring(length-1-i,length-i));
        }
        return total % 10;
    }
}

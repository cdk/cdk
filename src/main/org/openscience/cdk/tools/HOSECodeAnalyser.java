/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyses a molecular formula given in String format and builds
 * an AtomContainer with the Atoms in the molecular formula.
 * 
 * About implict H handling: By default the methods to calculate formula, natural and canonical mass
 * use the explicit Hs and only the explicit Hs if there is at least one in the molecule, implicit Hs are 
 * ignored. If there is no explicit H and only then the implicit Hs are used. If you use the constructor 
 * MFAnalyser(IAtomContainer ac, boolean useboth) and set useboth to true, all explicit Hs and all implicit Hs are used, 
 * the implicit ones also on atoms with explicit Hs.
 *
 * @author         egonw
 * @cdk.created    2007-03-08
 * @cdk.module     extra
 * @cdk.svnrev  $Revision$
 */
public class HOSECodeAnalyser {

	public static List getElements(String code) {
		List elementList = new ArrayList();
		
		if (code.length() == 0) {
			return elementList;
		}

		String currentSymbol = null;
		for (int f = 0; f < code.length(); f++) {
			char currentChar = code.charAt(f);
			if (currentChar >= 'A' && currentChar <= 'Z') {
				currentSymbol = "" + currentChar;
				if (f < code.length()) {
					currentChar = code.charAt(f+1);
					if (currentChar >= 'a' && currentChar <= 'z') {
						currentSymbol += currentChar;
					}
				}
			} else {
				currentSymbol = null;
			}
			if (currentSymbol != null) {
				if (!elementList.contains(currentSymbol)) {
					// reverse HOSECodeGenerator.getElementSymbol translations
					if (currentSymbol.equals("Y")) {
						currentSymbol = "Br";
					} else if (currentSymbol.equals("X")) {
						currentSymbol = "Cl";
					} else if (currentSymbol.equals("Q")) {
						currentSymbol = "Si";
					}
					elementList.add(currentSymbol);
				}
			}
		}
		return elementList;
	}

}



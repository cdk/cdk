/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.libio.biojava;

import org.biojava.bio.symbol.SymbolList;

import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.ProteinBuilderTool;

/**
 * Abstract class that provides convertor procedures to
 * convert CDK classes to <a href="http://www.biojava.org/">BioJava</a>
 * classes and visa versa.
 *
 * @cdk.module   libio-biojava
 * @cdk.requires biojava-1.4.jar
 *
 * @cdk.keyword  BioJava
 * @cdk.keyword  class convertor
 */
public class Convertor {

	/**
	 * Convert a BioJava SymbolList into a BioPolymer object containing
	 * a full AtomContainer based connection table.
	 * 
	 * @param source        BioJava SymbolList object
	 * @return              CDK BioPolymer
	 * @throws CDKException
	 * 
	 * @see org.openscience.cdk.interfaces.IBioPolymer
	 */
	public static IBioPolymer convert(SymbolList source) throws CDKException {
		return ProteinBuilderTool.createProtein(source.seqString());
	}

	// TODO: implement convertor methods below
	
	public static SymbolList convert(IBioPolymer sequence) {
	    return null;	
	}	
	
}

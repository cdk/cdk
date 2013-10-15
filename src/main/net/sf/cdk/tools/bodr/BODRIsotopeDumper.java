/* Copyright (C) 2007,2011  Egon Willighagen <egonw@users.sf.net>
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
package net.sf.cdk.tools.bodr;

import java.io.IOException;

import org.openscience.cdk.config.BODRIsotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Support class that converts a BODR CML file with isotope data into a CDK-specific
 * test file. This class uses the {@link XMLIsotopeFactory} to creates input for the
 * {@link BODRIsotopes} class.
 *
 * @author egonw
 */
public class BODRIsotopeDumper {

	/**
	 * Creates output to STDOUT which can be copied into the isotopes.txt file.
	 *
	 * @param args         Ignored command line parameters
	 * @throws IOException thrown when the input text file cannot be read
	 */
	public static void main(String[] args) throws IOException {
		IsotopeFactory fac = IsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
		for (IIsotope isotope : fac.getIsotopes()) {
			String line = "";
			line += isotope.getSymbol();
			line += ", ";
			line += isotope.getAtomicNumber();
			line += ", ";
			line += isotope.getMassNumber();
			line += ", ";
			line += isotope.getExactMass();
			line += ", ";
			line += isotope.getNaturalAbundance();
			System.out.println(line);
		}
	}

}

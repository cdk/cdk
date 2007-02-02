/* $Revision: 7636 $ $Author: egonw $ $Date: 2007-01-04 18:46:10 +0100 (Thu, 04 Jan 2007) $
 *
 * Copyright (C) 2007  Nina Jeliazkova <nina@acad.bg>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.smiles;

import java.util.Timer;
import java.util.TimerTask;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * An interruptable SMILES parser.
 * 
 * @author     Nina Jeliazkova
 * @cdk.module smiles
 */
public class InterruptableSmilesParser extends SmilesParser {

	public InterruptableSmilesParser(IChemObjectBuilder builder) {
		super(builder);
	}

	/**
	 * Runs the SMILES parser in a Thread with a time out.
	 * 
	 * @param smiles  The SMILES to parse.
	 * @param timeout Time in ms after which to break of SMILES parsing.
	 * 
	 * @return The IMolecule parsed from the SMILES.
	 * @throws InvalidSmilesException
	 */
	public IMolecule parseSmiles(String smiles, long timeout) throws InvalidSmilesException {
		IMolecule mol = null;
		Timer timer = new Timer(true);
		timer.schedule(
			new TimerTask() {
				// this is the actual code that interrupts ValencyChecker        
				public void run() {
					setInterrupted(true);
				}
			},
			timeout
		);
		try {
			synchronized (smiles) {
				mol = parseSmiles(smiles);
			}
		} catch (InvalidSmilesException x) {
			throw new InvalidSmilesException(x.getMessage());
		} catch (Exception x) {
			x.printStackTrace();
			smiles = x.getMessage();
		} finally {
			timer.cancel();
			timer = null;
		}

		if (mol == null) {
			mol = super.builder.newMolecule();
			mol.setProperty(CDKConstants.SMILES,smiles);
		}
		return mol;
	}

}

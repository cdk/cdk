/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import java.util.BitSet;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Interface for fingerprint calculators.
 *
 * @author         egonw
 * @cdk.keyword    fingerprint
 * @cdk.module     core
 * @cdk.svnrev  $Revision$
 */
public interface IFingerprinter {

	/**
	 * Returns the fingerprint for the given IAtomContainer.
	 * 
	 * @param  container IAtomContainer for which the fingerprint should be calculated.
	 * @return           the fingerprint
	 * @throws CDKException TODO
	 */
	public BitSet getFingerprint(IAtomContainer container) throws CDKException;
	
	/**
	 * Returns the size of the fingerprints calculated.
	 * 
	 * @return the size of the fingerprint
	 */
	public int getSize();

}


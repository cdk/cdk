/* $Revision: 11674 $ $Author: rajarshi $ $Date: 2008-07-20 22:05:08 -0400 (Sun, 20 Jul 2008) $
 *
 * Copyright (C) 2008 Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.fragments.EStateFragments;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.smarts.SMARTSQueryTool;

import java.util.BitSet;
import java.util.Map;

/**
 * This fingerprinter generates 79 bit fingerprints using the E-State 
 * fragments.
 *
 * <p>The E-State fragments are those described in {@cdk.cite HALL1995} and 
 * the SMARTS patterns were taken from 
 * <a href="http://www.rdkit.org">RDKit</a>. Note that this fingerprint simply
 * indicates the presence or occurrence of the fragments. If you need counts 
 * of the fragments take a look at {@link 
 * org.openscience.cdk.qsar.descriptors.molecular.KierHallSmartsDescriptor},
 * which also lists the substructures corresponding to each bit position.
 *
 * <p>This class assumes that aromaticity perception and atom typing have 
 * been performed prior to generating the fingerprint
 * 
 * @author Rajarhi Guha
 * @cdk.created 2008-07-23
 *
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.keyword estate
 *
 * @cdk.module fingerprint
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.fingerprint.EStateFingerprinterTest")
public class EStateFingerprinter implements IFingerprinter {

    private static final String[] PATTERNS = EStateFragments.getSmarts();

    @TestMethod("testFingerprint,testGetSize")
    public EStateFingerprinter() {
    }

    /** {@inheritDoc} */
    @TestMethod("testFingerprint")
    public IBitFingerprint getBitFingerprint(IAtomContainer atomContainer) 
                  throws CDKException {

        int bitsetLength = PATTERNS.length;
        BitSet fingerPrint = new BitSet(bitsetLength);

        SMARTSQueryTool sqt = new SMARTSQueryTool("C");
        for (int i = 0; i < PATTERNS.length; i++) {
            sqt.setSmarts(PATTERNS[i]);
            boolean status = sqt.matches(atomContainer);
            if (status) fingerPrint.set(i, true);
        }
        return new BitSetFingerprint(fingerPrint);
    }

    /** {@inheritDoc} */
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc} */
    @TestMethod("testGetSize")
    public int getSize() {
        return PATTERNS.length;
    }

    /** {@inheritDoc} */
	@Override
	public ICountFingerprint getCountFingerprint(IAtomContainer container)
			throws CDKException {
		throw new UnsupportedOperationException();
	}

}
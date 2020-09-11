/* Copyright (C) 2005-2019  Egon Willighagen <egonw@users.sf.net>
 * Copyright (C) 2011       Jonathan Alvarsson <jonalv@users.sf.net>
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
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Interface for fingerprint calculators.
 *
 * @author         egonw
 * @cdk.keyword    fingerprint
 * @cdk.module     core
 * @cdk.githash
 */
public interface IFingerprinter {

    /**
     * Generate a fingerprint type version description in chemfp's FPS format. We
     * report the library version rather than an individual version per fingerprint,
     * although this is awkward as many fingerprint's don't/won't change between releases
     * and we can not keep compatibility we guarantee we document how the fingerprint was
     * encoded.
     *
     * <br>
     * Examples:
     * <pre>
     * #type=CDK-Fingerprinter/2.0 searchDepth=7 pathLimit=2000 hashPseudoAtoms=true
     * #type=CDK-CircularFingerprint/2.0 classType=ECFP4
     * </pre>
     *
     * @return version description.
     */
    String getVersionDescription();

    /**
     * Generate a binary fingerprint as a bit. This method will usually delegate to
     * {@link #getBitFingerprint(IAtomContainer)} and invoke
     * {@link IBitFingerprint#asBitSet()}, it is included for backwards compatibility.
     *
     * @param mol molecule
     * @return BitSet
     * @throws CDKException problem generating fingerprint
     */
    BitSet getFingerprint(IAtomContainer mol) throws CDKException;

    /**
     * Returns the bit fingerprint for the given {@link IAtomContainer}.
     *
     * @param  container {@link IAtomContainer} for which the fingerprint should be calculated.
     * @return           the bit fingerprint
     * @throws CDKException may be thrown if there is an error during aromaticity detection
     * or (for key based fingerprints) if there is a SMARTS parsing error
     * @throws UnsupportedOperationException if the Fingerprinter can not produce bit fingerprints
     */
    IBitFingerprint getBitFingerprint(IAtomContainer container) throws CDKException;

    /**
     * Returns the count fingerprint for the given {@link IAtomContainer}.
     *
     * @param container {@link IAtomContainer} for which the fingerprint should be calculated.
     * @return the count fingerprint
     * @throws CDKException if there is an error during aromaticity detection
     * or (for key based fingerprints) if there is a SMARTS parsing error.
     * @throws UnsupportedOperationException if the Fingerprinter can not produce count fingerprints
     */
    ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException;

    /**
     * Returns the raw representation of the fingerprint for the given IAtomContainer. The raw representation contains
     * counts as well as the key strings.
     *
     * @param container IAtomContainer for which the fingerprint should be calculated.
     * @return the raw fingerprint
     * @throws CDKException
     */
    Map<String, Integer> getRawFingerprint(IAtomContainer container) throws CDKException;

    /**
     * Returns the size (or length) of the fingerprint.
     *
     * @return the size of the fingerprint
     */
    int getSize();
}

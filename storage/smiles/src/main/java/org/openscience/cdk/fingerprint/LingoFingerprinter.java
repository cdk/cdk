/*
 * Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.smiles.SmilesGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.openscience.cdk.graph.Cycles.all;
import static org.openscience.cdk.graph.Cycles.or;
import static org.openscience.cdk.graph.Cycles.relevant;

/**
 * An implementation of the LINGO fingerprint {@cdk.cite Vidal2005}. <p> While the current
 * implementation converts ring closure symbols to 0's it does not convert 2-letter element symbols
 * to single letters (ala OpenEye).
 *
 * @author Rajarshi Guha
 * @cdk.module smiles
 * @cdk.keyword fingerprint
 * @cdk.keyword hologram
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.fingerprint.LingoFingerprinterTest")
public class LingoFingerprinter implements IFingerprinter {

    private final int n;
    private final SmilesGenerator gen    = SmilesGenerator.unique().aromatic();
    private final Pattern         DIGITS = Pattern.compile("[0-9]+");

    private final Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(),
                                                            or(all(), relevant()));

    /**
     * Initialize the fingerprinter with a defult substring length of 4.
     */
    @TestMethod("testFingerprint")
    public LingoFingerprinter() {
        this(4);
    }

    /**
     * Initialize the fingerprinter.
     *
     * @param n The length of substrings to consider
     */
    @TestMethod("testFingerprint")
    public LingoFingerprinter(int n) {
        this.n = n;
    }

    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    @TestMethod("testFingerprint")
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer atomContainer) throws CDKException {
        aromaticity.apply(atomContainer);
        final String smiles = replaceDigits(gen.create(atomContainer));
        final Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0, l = smiles.length() - n + 1; i < l; i++) {
            String subsmi = smiles.substring(i, i + n);
            Integer count = map.get(subsmi);
            if (count == null)
                map.put(subsmi, 1);
            else
                map.put(subsmi, count + 1);
        }
        return map;
    }

    @TestMethod("testGetSize")
    @Override
    public int getSize() {
        return -1; // 1L << 32
    }

    private String replaceDigits(String smiles) {
        return DIGITS.matcher(smiles).replaceAll("0");
    }

    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        throw new UnsupportedOperationException();
    }

}

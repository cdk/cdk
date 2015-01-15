/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.AtomSignature;

/**
 * An implementation of a {@link AtomSignature}-based fingerprint.
 *
 * @cdk.module  signature
 * @cdk.keyword fingerprint
 * @cdk.githash
 */
public class SignatureFingerprinter implements IFingerprinter {

    private int signatureDepth;

    /**
     * Initialize the fingerprinter with a defult signature depth of 1.
     */
    public SignatureFingerprinter() {
        this(1);
    }

    /**
     * Initialize the fingerprinter with a certain signature depth.
     *
     * @param depth The depth of the signatures to calculate.
     */
    public SignatureFingerprinter(int depth) {
        this.signatureDepth = depth;
    }

    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer atomContainer) throws CDKException {
        return new IntArrayFingerprint(getRawFingerprint(atomContainer));
    }

    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer atomContainer) throws CDKException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (IAtom atom : atomContainer.atoms()) {
            String signature = new AtomSignature(atom, signatureDepth, atomContainer).toCanonicalString();
            if (map.containsKey(signature)) {
                map.put(signature, map.get(signature) + 1);
            } else {
                map.put(signature, 1);
            }
        }
        return map;
    }

    @Override
    public int getSize() {
        return -1;
    }

    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        return new IntArrayCountFingerprint(getRawFingerprint(container));
    }

}

/*
 * Copyright (C) 2022 NextMove Software
 *               2022 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.smirks;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.Transform;
import org.openscience.cdk.smarts.SmartsPattern;

/**
 * A SMIRKS transform, is a {@link Transform} which can optionally (on by
 * default) perform ring and aromaticity (Daylight) perception. Before applying
 * the transform the molecule is matched it is passed to
 * {@link SmartsPattern#prepare}. The prepare function ensures ring and aromatic
 * flags are consistent. You can turn off by setting {@link #setPrepare(boolean)} to
 * false.
 * <br/>
 * Basic usage:
 * <pre>
 * {@code
 * SmirksTransform smirks = Smirks.compile("[C:1][H]>>[C:1]Cl");
 * smirks.apply(mol);
 * }
 * </pre>
 *
 * @see org.openscience.cdk.isomorphism.Transform
 * @see org.openscience.cdk.isomorphism.Pattern
 * @see org.openscience.cdk.smarts.SmartsPattern
 */
public final class SmirksTransform extends Transform {

    /**
     * Prepare the target molecule (i.e. detect rings, aromaticity) before
     * matching the SMARTS.
     */
    private boolean doPrep = true;

    // package private!
    SmirksTransform() {
        super();
    }

    /**
     * Sets whether the molecule should be "prepared" for a SMIRKS transform,
     * including set ring flags and perceiving aromaticity. The main reason
     * to skip preparation (via {@link SmartsPattern#prepare(IAtomContainer)})
     * is if it has already been done, for example when matching multiple
     * SMIRKS transforms.
     *
     * @param doPrep whether preparation should be done
     * @return self for inline calling
     */
    public SmirksTransform setPrepare(boolean doPrep) {
        this.doPrep = doPrep;
        return this;
    }

    @Override
    public Iterable<IAtomContainer> apply(IAtomContainer mol, Mode mode) {
        if (doPrep)
            SmartsPattern.prepare(mol);
        return super.apply(mol, mode);
    }

    @Override
    public boolean apply(IAtomContainer mol) {
        if (doPrep)
            SmartsPattern.prepare(mol);
        return super.apply(mol);
    }
}

/*
 * Copyright (c) 2017 John Mayfield <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.List;

/**
 * Restricted axial rotation around Aryl-Aryl bonds. The atropisomer is
 * stored in a similar manner to {@link ExtendedTetrahedral} (and
 * {@link TetrahedralChirality}) except instead of storing the central atom
 * we store the sigma bond around which the rotation is restricted and the
 * four carriers are connect to either end atom of the 'focus' bond.
 * <br>
 * <pre>
 *      a     b'
 *     /       \
 *    Ar --f-- Ar
 *     \      /
 *      a'   b
 * f: focus
 * Ar: Aryl (carriers connected to either end of 'f')
 * a,a',b,b': ortho substituted on the Aryl
 * </pre>
 * <br>
 * Typical examples include <a href="https://en.wikipedia.org/wiki/BINOL">
 * BiNOL</a>, and <a href="https://en.wikipedia.org/wiki/BINAP">BiNAP</a>.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Atropisomer">Atropisomer (Wikipedia)</a>
 */
public class Atropisomeric extends AbstractStereo<IBond,IAtom> {

    /**
     * Define a new atropisomer using the focus bond and the carrier atoms.
     *
     * @param focus the focus bond
     * @param carriers the carriers
     * @param value the configuration {@link #LEFT} or {@link #RIGHT}
     */
    public Atropisomeric(IBond focus, IAtom[] carriers, int value) {
        super(focus, carriers, IStereoElement.AT | value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStereoElement<IBond, IAtom> create(IBond focus,
                                                  List<IAtom> carriers,
                                                  int cfg) {
        return new Atropisomeric(focus, carriers.toArray(new IAtom[4]), cfg);
    }
}

/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *               2013       European Bioinformatics Institute
 *                          John May
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
 * Foundation, 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * (or see http://www.gnu.org/copyleft/lesser.html)
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Match an atom with the defined degree. The degree is also referred to as the
 * explicit connectivity and is encoded in smarts using {@code D<NUMBER>}.
 *
 * @cdk.module smarts
 * @cdk.keyword SMARTS
 * @cdk.githash
 */
public final class ExplicitConnectionAtom extends SMARTSAtom {

    /** Number of explicit connections. */
    private int degree;

    /**
     * Create a query atom for matching the degree of an atom. The degree is the
     * number connected atoms.
     */
    public ExplicitConnectionAtom(int degree, IChemObjectBuilder builder) {
        super(builder);
        this.degree = degree;
    }

    /** @inheritDoc */
    @Override
    public boolean matches(IAtom atom) {
        // XXX: this is incorrect but bug 824 expects this behaviour. The reason
        //      Daylight matches is because the explicit hydrogens are
        //      suppressed by default turning on explicit hydrogens in depict
        //      match shows correct functionality. Discussion needed to revert
        //      but because other invariants aren't adjusted (implicit h) we
        //      can't really do this and the correct option is to enable/disable
        //      hydrogen suppression (removal of explicit H atoms) as a prepossessing
        //      step
        return invariants(atom).connectivity() - invariants(atom).totalHydrogenCount() == degree;
    }
}

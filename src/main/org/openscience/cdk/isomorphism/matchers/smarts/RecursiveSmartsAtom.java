/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.isomorphism.matchers.smarts;

import com.google.common.collect.Iterables;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.ComponentGrouping;
import org.openscience.cdk.isomorphism.Ullmann;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * This matches recursive smarts atoms.
 *
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
public final class RecursiveSmartsAtom extends SMARTSAtom {

    private final static ILoggingTool logger = LoggingToolFactory.createLoggingTool(RecursiveSmartsAtom.class);

    /** The IQueryAtomContainer created by parsing the recursive smarts */
    private final IQueryAtomContainer query;

    /**
     * Creates a new instance
     *
     * @param query
     */
    public RecursiveSmartsAtom(IQueryAtomContainer query) {
        super(query.getBuilder());
        this.query = query;
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
     */
    public boolean matches(IAtom atom) {
        
        if (!((IQueryAtom) query.getAtom(0)).matches(atom))
            return false;
        
        if (query.getAtomCount() == 1)
            return true;

        IAtomContainer target = invariants(atom).target();

        // recursive queries are not currently cached - they also be faster
        // by specifying the initial mapping of (0->0) and
        for (int[] mapping : Iterables.filter(Ullmann.findSubstructure(query)
                                                     .matchAll(target),
                                              new ComponentGrouping(query, target))) {
            if (target.getAtom(mapping[0]) == atom) 
                return true;
        }
        
        return false;
    }
}

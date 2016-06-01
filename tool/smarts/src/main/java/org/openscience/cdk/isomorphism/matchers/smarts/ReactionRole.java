/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Matches atoms with a particular role in a reaction.
 */
public class ReactionRole extends SMARTSAtom {

    public static final int ROLE_REACTANT = 0x1;
    public static final int ROLE_AGENT    = 0x2;
    public static final int ROLE_PRODUCT  = 0x4;
    public static final int ROLE_ANY      = ROLE_REACTANT | ROLE_PRODUCT | ROLE_AGENT;

    private final int role;

    public final static ReactionRole RoleReactant = new ReactionRole(null, ROLE_REACTANT);
    public final static ReactionRole RoleAgent = new ReactionRole(null, ROLE_AGENT);
    public final static ReactionRole RoleProduct = new ReactionRole(null, ROLE_PRODUCT);

    public ReactionRole(IChemObjectBuilder builder, int role) {
        super(builder);
        this.role = role;
    }

    @Override
    public boolean matches(IAtom atom) {
        return true; // TODO
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if ((role & ROLE_REACTANT) != 0)
            sb.append("Reactant");
        if ((role & ROLE_AGENT) != 0)
            sb.append("Agent");
        if ((role & ROLE_PRODUCT) != 0)
            sb.append("Product");
        return "ReactionRole(" + sb.toString() + ")";
    }
}

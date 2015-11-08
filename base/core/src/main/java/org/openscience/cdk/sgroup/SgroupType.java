/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
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

package org.openscience.cdk.sgroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of Ctab Sgroup types.
 * <p/>
 * <br/>
 * <b>Display shortcuts</b>
 * <ul>
 * <li>SUP, abbreviation Sgroup (formerly called superatom)</li>
 * <li>MUL, multiple group</li>
 * <li>GEN, generic</li>
 * </ul>
 * <b>Polymers</b>
 * <ul>
 * <li>SRU, SRU type</li>
 * <li>MON, monomer</li>
 * <li>MER, Mer type</li>
 * <li>COP, copolymer</li>
 * <li>CRO, crosslink</li>
 * <li>MOD, modification</li>
 * <li>GRA, graft</li>
 * <li>ANY, any polymer</li>
 * </ul>
 * <b>Components, Mixtures, and formulations</b>
 * <ul>
 * <li>COM, component</li>
 * <li>MIX, mixture</li>
 * <li>FOR, formulation</li>
 * </ul>
 * <b>Non-chemical</b>
 * <ul>
 * <li>DAT, data Sgroup</li>
 * </ul>
 */
public enum SgroupType {
    CtabAbbreviation("SUP"),
    CtabMultipleGroup("MUL"),
    CtabStructureRepeatUnit("SRU"),
    CtabMonomer("MON"),
    CtabModified("MOD"),
    CtabCopolymer("COP"),
    CtabMer("MER"),
    CtabCrossLink("CRO"),
    CtabGraft("GRA"),
    CtabAnyPolymer("ANY"),
    CtabComponent("COM"),
    CtabMixture("MIX"),
    CtabFormulation("FOR"),
    CtabData("DAT"),
    CtabGeneric("GEN"),

    // extension for handling positional variation and distributed coordination bonds
    ExtMulticenter("N/A");


    static final Map<String, SgroupType> map = new HashMap<>();

    static {
        for (SgroupType t : values())
            map.put(t.ctabKey, t);
    }

    private final String ctabKey;

    SgroupType(String ctabKey) {
        this.ctabKey = ctabKey;
    }

    public String getKey() {
        return ctabKey;
    }

    public static SgroupType parseCtabKey(String str) {
        SgroupType type = map.get(str);
        if (type == null)
            return SgroupType.CtabGeneric;
        return type;
    }
}

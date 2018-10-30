/*
 * Copyright (c) 2018 John Mayfield
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
package org.openscience.cdk.tools.manipulator;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities related to Ctab Sgroups.
 */
public final class SgroupManipulator {

    private SgroupManipulator() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends IChemObject> T get(Map<? extends IChemObject,
                                                     ? extends IChemObject> map,
                                                 T obj) {
        if (map == null)
            return obj;
        T val = (T) map.get(obj);
        if (val == null)
            return obj;
        return val;
    }

    /**
     * Copy a collection of Sgroups, replacing any {@link IAtom}/{@link IBond}
     * references with those present in the provided 'replace' map. If an empty
     * replace map is provided (null or empty) the sgroups are simply
     * duplicated. If an item is not present in the replacement map the original
     * item is preserved.
     * <br>
     * <pre>{@code
     * Map<IChemObject,IChemObject> replace = new HashMap<>();
     * replace.put(orgAtom, newAtom);
     * replace.put(orgBond, newBond);
     * newSgroups = copy(orgSgroups, replace);
     * }</pre>
     *
     * @param sgroups collection of sgroups, can be null
     * @param replace the replacement map, can be null
     * @return list of copied sgroups, null if sgroups input was null
     */
    public static List<Sgroup> copy(Collection<Sgroup> sgroups,
                                    Map<? extends IChemObject,
                                        ? extends IChemObject> replace) {
        if (sgroups == null) return null;
        Map<Sgroup, Sgroup> sgroupMap = new HashMap<>();
        for (Sgroup sgroup : sgroups)
            sgroupMap.put(sgroup, new Sgroup());
        for (Map.Entry<Sgroup, Sgroup> e : sgroupMap.entrySet()) {
            Sgroup orgSgroup = e.getKey();
            Sgroup cpySgroup = e.getValue();
            cpySgroup.setType(orgSgroup.getType());
            for (IAtom atom : orgSgroup.getAtoms())
                cpySgroup.addAtom(get(replace, atom));
            for (IBond bond : orgSgroup.getBonds())
                cpySgroup.addBond(get(replace, bond));
            for (Sgroup parent : orgSgroup.getParents())
                cpySgroup.addParent(sgroupMap.get(parent));
            for (SgroupKey key : SgroupKey.values()) {
                switch (key) {
                    case CtabParentAtomList: {
                        Collection<IAtom> orgVal = orgSgroup.getValue(key);
                        if (orgVal != null) {
                            List<IAtom> cpyVal = new ArrayList<>();
                            for (IAtom atom : orgVal)
                                cpyVal.add(get(replace, atom));
                            cpySgroup.putValue(key, cpyVal);
                        }
                    }
                    break;
                    case CtabBracket: {
                        Collection<SgroupBracket> orgVals = orgSgroup.getValue(key);
                        if (orgVals != null) {
                            for (SgroupBracket bracket : orgVals)
                                cpySgroup.addBracket(new SgroupBracket(bracket));
                        }
                    }
                    break;
                    default:
                        // primitive values, String, Integer are immutable
                        Object val = orgSgroup.getValue(key);
                        if (val != null)
                            cpySgroup.putValue(key, val);
                        break;
                }
            }
        }
        return new ArrayList<>(sgroupMap.values());
    }
}

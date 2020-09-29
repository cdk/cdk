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

package org.openscience.cdk.smiles;

import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.smiles.CxSmilesState.CxDataSgroup;
import org.openscience.cdk.smiles.CxSmilesState.CxPolymerSgroup;
import org.openscience.cdk.smiles.CxSmilesState.CxSgroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CxSmilesGenerator {

    // calculate the inverse of a permutation
    private static int[] inverse(int[] perm) {
        int[] inv = new int[perm.length];
        for (int i = 0, len = perm.length; i < len; i++)
            inv[perm[i]] = i;
        return inv;
    }

    private static String encode_alias(String label) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < label.length(); i++) {
            char c = label.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_') {
                sb.append(c);
            } else {
                sb.append("&#").append(Integer.toString(c)).append(";");
            }
        }
        return sb.toString();
    }

    private static int compare(Comparator<Integer> comp, List<Integer> a, List<Integer> b) {
        final int alen = a.size();
        final int blen = b.size();
        final int len = Math.min(alen, blen);
        for (int i = 0; i < len; i++) {
            int cmp = comp.compare(a.get(i), b.get(i));
            if (cmp != 0) return cmp;
        }
        return Integer.compare(alen, blen);
    }

    static String generate(CxSmilesState state, int opts, int[] components, final int[] ordering) {

        if (!SmiFlavor.isSet(opts, SmiFlavor.CxSmilesWithCoords))
            return "";

        final int[] invorder = inverse(ordering);

        StringBuilder sb = new StringBuilder();
        sb.append(' ');
        sb.append('|');

        final Comparator<Integer> invComp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(invorder[a], invorder[b]);
            }
        };
        final Comparator<Integer> comp = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                return Integer.compare(ordering[a], ordering[b]);
            }
        };

        // Fragment Grouping
        if (SmiFlavor.isSet(opts, SmiFlavor.CxFragmentGroup) &&
            state.fragGroups != null && !state.fragGroups.isEmpty()) {

            int maxCompId = 0;
            for (int compId : components) {
                if (compId > maxCompId)
                    maxCompId = compId;
            }

            // get the output component order
            final int[] compMap = new int[maxCompId + 1];
            int compId = 1;
            for (int idx : invorder) {
                int component = components[idx];
                if (compMap[component] == 0)
                    compMap[component] = compId++;
            }
            // index vs number, we need to output index
            for (int i = 0; i < compMap.length; i++)
                compMap[i]--;

            final Comparator<Integer> compComp = new Comparator<Integer>() {
                @Override
                public int compare(Integer a, Integer b) {
                    return Integer.compare(compMap[a], compMap[b]);
                }
            };

            List<List<Integer>> fragGroupCpy = new ArrayList<>(state.fragGroups);
            for (List<Integer> idxs : fragGroupCpy)
                Collections.sort(idxs, compComp);
            Collections.sort(fragGroupCpy, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> a, List<Integer> b) {
                    return CxSmilesGenerator.compare(compComp, a, b);
                }
            });

            // C1=CC=CC=C1.C1=CC=CC=C1.[OH-].[Na+]>> |f:0.1,2.3,c:0,2,4,6,8,10|
            sb.append('f');
            sb.append(':');
            for (int i = 0; i < fragGroupCpy.size(); i++) {
                if (i > 0) sb.append(',');
                appendIntegers(compMap, '.', sb, fragGroupCpy.get(i));
            }
        }

        // Atom Labels
        if (SmiFlavor.isSet(opts, SmiFlavor.CxAtomLabel) &&
            state.atomLabels != null && !state.atomLabels.isEmpty()) {

            if (sb.length() > 2)
                sb.append(',');
            sb.append('$');
            int nonempty_cnt = 0;
            for (int idx : invorder) {
                String label = state.atomLabels.get(idx);
                if (label == null || label.isEmpty()) label = "";
                else nonempty_cnt++;
                sb.append(encode_alias(label));
                // don't need to write anymore more ';'
                if (nonempty_cnt == state.atomLabels.size())
                    break;
                sb.append(";");
            }
            sb.append('$');
        }

        // Atom Values
        if (SmiFlavor.isSet(opts, SmiFlavor.CxAtomValue) &&
            state.atomValues != null && !state.atomValues.isEmpty()) {

            if (sb.length() > 2)
                sb.append(',');
            sb.append("$_AV:");
            int nonempty_cnt = 0;
            for (int idx : invorder) {
                String label = state.atomValues.get(idx);
                if (label == null || label.isEmpty()) label = "";
                else nonempty_cnt++;
                sb.append(encode_alias(label));
                // don't need to write anymore more ';'
                if (nonempty_cnt == state.atomValues.size())
                    break;
                sb.append(";");
            }
            sb.append('$');
        }

        // 2D/3D Coordinates
        if (SmiFlavor.isSet(opts, SmiFlavor.CxCoordinates) &&
            state.atomCoords != null && !state.atomCoords.isEmpty()) {
            DecimalFormat fmt = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ROOT));
            if (sb.length() > 2) sb.append(',');
            sb.append('(');
            for (int i = 0; i < ordering.length; i++) {
                double[] xyz = state.atomCoords.get(invorder[i]);
                if (i != 0) sb.append(';');
                if (xyz[0] != 0)
                    sb.append(fmt.format(xyz[0]));
                sb.append(',');
                if (xyz[1] != 0)
                    sb.append(fmt.format(xyz[1]));
                sb.append(',');
                if (xyz[2] != 0)
                    sb.append(fmt.format(xyz[2]));
            }
            sb.append(')');
        }

        // Multicenter/Positional variation bonds
        if (SmiFlavor.isSet(opts, SmiFlavor.CxMulticenter) &&
            state.positionVar != null && !state.positionVar.isEmpty()) {

            if (sb.length() > 2) sb.append(',');
            sb.append('m');
            sb.append(':');

            List<Map.Entry<Integer, List<Integer>>> multicenters = new ArrayList<>(state.positionVar.entrySet());

            // consistent output order
            multicenters.sort((a, b) -> comp.compare(a.getKey(), b.getKey()));

            for (int i = 0; i < multicenters.size(); i++) {
                if (i != 0) sb.append(',');
                Map.Entry<Integer, List<Integer>> e = multicenters.get(i);
                sb.append(ordering[e.getKey()]);
                sb.append(':');
                List<Integer> vals = new ArrayList<>(e.getValue());
                vals.sort(comp);
                appendIntegers(ordering, '.', sb, vals);
            }

        }

        if (SmiFlavor.isSet(opts, SmiFlavor.CxLigandOrder) &&
            state.ligandOrdering != null && !state.ligandOrdering.isEmpty()) {

            if (sb.length() > 2) sb.append(',');
            sb.append("LO");
            sb.append(':');

            List<Map.Entry<Integer, List<Integer>>> ligandorderings = new ArrayList<>(state.ligandOrdering.entrySet());

            // consistent output order
            ligandorderings.sort((a, b) -> comp.compare(a.getKey(), b.getKey()));

            for (int i = 0; i < ligandorderings.size(); i++) {
                if (i != 0) sb.append(',');
                Map.Entry<Integer, List<Integer>> e = ligandorderings.get(i);
                sb.append(ordering[e.getKey()]);
                sb.append(':');
                appendIntegers(ordering, '.', sb, e.getValue());
            }

        }


        int numSgroups = 0;

        // *CCO* |$_AP1;;;;_AP2$,Sg:n:1,2,3::ht|
        if (SmiFlavor.isSet(opts, SmiFlavor.CxPolymer) &&
            state.mysgroups != null && !state.mysgroups.isEmpty()) {
            List<CxPolymerSgroup> polysgroups = new ArrayList<>();
            for (CxSgroup polysgroup : state.mysgroups) {
                if (polysgroup instanceof CxPolymerSgroup) {
                    polysgroups.add((CxPolymerSgroup) polysgroup);
                    Collections.sort(polysgroup.atoms, comp);
                }
            }

            Collections.sort(polysgroups, new Comparator<CxPolymerSgroup>() {
                @Override
                public int compare(CxPolymerSgroup a, CxPolymerSgroup b) {
                    int cmp = 0;
                    cmp = a.type.compareTo(b.type);
                    if (cmp != 0) return cmp;
                    cmp = CxSmilesGenerator.compare(comp, a.atoms, b.atoms);
                    return cmp;
                }
            });

            for (CxPolymerSgroup cxPolymerSgroup : polysgroups) {
                cxPolymerSgroup.id = numSgroups++;
                if (sb.length() > 2) sb.append(',');
                sb.append("Sg:");
                sb.append(cxPolymerSgroup.type);
                sb.append(':');
                appendIntegers(ordering, ',', sb, cxPolymerSgroup.atoms);
                sb.append(':');
                if (cxPolymerSgroup.subscript != null)
                    sb.append(cxPolymerSgroup.subscript);
                sb.append(':');
                if (cxPolymerSgroup.supscript != null)
                    sb.append(cxPolymerSgroup.supscript.toLowerCase(Locale.ROOT));
            }
        }

        if (SmiFlavor.isSet(opts, SmiFlavor.CxDataSgroups) &&
            state.mysgroups != null && !state.mysgroups.isEmpty()) {
            List<CxDataSgroup> datasgroups = new ArrayList<>();
            for (CxSgroup datasgroup : state.mysgroups) {
                if (datasgroup instanceof CxDataSgroup) {
                    datasgroups.add((CxDataSgroup)datasgroup);
                    Collections.sort(datasgroup.atoms, comp);
                }
            }

            Collections.sort(datasgroups, new Comparator<CxDataSgroup>() {
                @Override
                public int compare(CxDataSgroup a, CxDataSgroup b) {
                    int cmp = 0;
                    cmp = a.field.compareTo(b.field);
                    if (cmp != 0) return cmp;
                    cmp = a.value.compareTo(b.value);
                    if (cmp != 0) return cmp;
                    cmp = CxSmilesGenerator.compare(comp, a.atoms, b.atoms);
                    return cmp;
                }
            });

            for (CxDataSgroup cxDataSgroup : datasgroups) {
                cxDataSgroup.id = numSgroups++;
                if (sb.length() > 2) sb.append(',');
                sb.append("SgD:");
                appendIntegers(ordering, ',', sb, cxDataSgroup.atoms);
                sb.append(':');
                if (cxDataSgroup.field != null)
                    sb.append(cxDataSgroup.field);
                sb.append(':');
                if (cxDataSgroup.value != null)
                    sb.append(cxDataSgroup.value);
                sb.append(':');
                if (cxDataSgroup.operator != null)
                    sb.append(cxDataSgroup.operator);
                sb.append(':');
                if (cxDataSgroup.unit != null)
                    sb.append(cxDataSgroup.unit);
                // fmt (t/f/n) + coords?
            }
        }

        // hierarchy information
        if (numSgroups > 0) {
            boolean firstSgH = true;
            if (state.mysgroups != null) {
                state.mysgroups.sort(Comparator.comparingInt(o -> o.id));
                for (CxSgroup sgroup : state.mysgroups) {
                    if (sgroup.children.isEmpty())
                        continue;
                    if (sb.length() > 2) sb.append(',');
                    if (firstSgH) {
                        sb.append("SgH:");
                        firstSgH = false;
                    }
                    sb.append(sgroup.id).append(':');
                    boolean first = true;
                    List<CxSgroup> children = new ArrayList<>(sgroup.children);
                    children.sort(Comparator.comparingInt(o -> o.id));
                    for (CxSgroup child : children) {
                        if (child.id < 0)
                            continue;
                        if (!first)
                            sb.append('.');
                        first = false;
                        sb.append(child.id);
                    }
                }
            }
        }

        // [C]1[CH][CH]CCC1 |^1:1,2,^3:0|
        if (SmiFlavor.isSet(opts, SmiFlavor.CxRadical) &&
            state.atomRads != null && !state.atomRads.isEmpty()) {
            Map<CxSmilesState.Radical, List<Integer>> radinv = new TreeMap<>();
            for (Map.Entry<Integer, CxSmilesState.Radical> e : state.atomRads.entrySet()) {
                List<Integer> idxs = radinv.get(e.getValue());
                if (idxs == null)
                    radinv.put(e.getValue(), idxs = new ArrayList<Integer>());
                idxs.add(e.getKey());
            }
            for (Map.Entry<CxSmilesState.Radical, List<Integer>> e : radinv.entrySet()) {
                if (sb.length() > 2) sb.append(',');
                sb.append('^');
                sb.append(e.getKey().ordinal() + 1);
                sb.append(':');
                Collections.sort(e.getValue(), comp);
                appendIntegers(ordering, ',', sb, e.getValue());
            }
        }

        sb.append('|');
        if (sb.length() <= 3) {
            return "";
        } else {
            return sb.toString();
        }
    }

    private static void appendIntegers(int[] invorder, char sep, StringBuilder sb, List<Integer> vals) {
        Iterator<Integer> iter = vals.iterator();
        if (iter.hasNext()) {
            sb.append(invorder[iter.next()]);
            while (iter.hasNext()) {
                sb.append(sep);
                sb.append(invorder[iter.next()]);
            }
        }
    }

}

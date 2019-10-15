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

import org.openscience.cdk.smiles.CxSmilesState.PolymerSgroup;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
            Collections.sort(multicenters,
                             new Comparator<Map.Entry<Integer, List<Integer>>>() {
                                 @Override
                                 public int compare(Map.Entry<Integer, List<Integer>> a,
                                                    Map.Entry<Integer, List<Integer>> b) {
                                     return comp.compare(a.getKey(), b.getKey());
                                 }
                             });

            for (int i = 0; i < multicenters.size(); i++) {
                if (i != 0) sb.append(',');
                Map.Entry<Integer, List<Integer>> e = multicenters.get(i);
                sb.append(ordering[e.getKey()]);
                sb.append(':');
                List<Integer> vals = new ArrayList<>(e.getValue());
                Collections.sort(vals, comp);
                appendIntegers(ordering, '.', sb, vals);
            }

        }


        // *CCO* |$_AP1;;;;_AP2$,Sg:n:1,2,3::ht|
        if (SmiFlavor.isSet(opts, SmiFlavor.CxPolymer) &&
            state.sgroups != null && !state.sgroups.isEmpty()) {
            List<PolymerSgroup> sgroups = new ArrayList<>(state.sgroups);

            for (PolymerSgroup psgroup : sgroups)
                Collections.sort(psgroup.atomset, comp);

            Collections.sort(sgroups, new Comparator<PolymerSgroup>() {
                @Override
                public int compare(PolymerSgroup a, PolymerSgroup b) {
                    int cmp = 0;
                    cmp = a.type.compareTo(b.type);
                    if (cmp != 0) return cmp;
                    cmp = CxSmilesGenerator.compare(comp, a.atomset, b.atomset);
                    return cmp;
                }
            });

            for (int i = 0; i < sgroups.size(); i++) {
                if (sb.length() > 2) sb.append(',');
                sb.append("Sg:");
                PolymerSgroup sgroup = sgroups.get(i);
                sb.append(sgroup.type);
                sb.append(':');
                appendIntegers(ordering, ',', sb, sgroup.atomset);
                sb.append(':');
                if (sgroup.subscript != null)
                    sb.append(sgroup.subscript);
                sb.append(':');
                if (sgroup.supscript != null)
                    sb.append(sgroup.supscript.toLowerCase(Locale.ROOT));
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

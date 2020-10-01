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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Light-weight intermediate data-structure for transferring information CDK to/from
 * CXSMILES.
 */
final class CxSmilesState {

    Map<Integer, String>        atomLabels  = null;
    Map<Integer, String>        atomValues  = null;
    List<double[]>              atomCoords  = null;
    List<List<Integer>>         fragGroups  = null;
    Map<Integer, Radical>       atomRads    = null;
    Map<Integer, List<Integer>> ligandOrdering = null;
    Map<Integer, List<Integer>> positionVar = null;
    List<CxSgroup>              mysgroups   = null;
    boolean                     coordFlag   = false;

    enum Radical {
        Monovalent,
        Divalent,
        DivalentSinglet,
        DivalentTriplet,
        Trivalent,
        TrivalentDoublet,
        TrivalentQuartet
    }

    static class CxSgroup {
        Set<CxSgroup> children = new HashSet<>();
        List<Integer> atoms = new ArrayList<>();
        int id = -1;
    }

    static final class CxDataSgroup extends CxSgroup {
        final String       field;
        final String       value;
        final String       operator;
        final String       unit;
        final String       tag;

        public CxDataSgroup(List<Integer> atoms, String field, String value, String operator, String unit, String tag) {
            this.atoms = atoms;
            this.field = field;
            this.value = value;
            this.operator = operator;
            this.unit = unit;
            this.tag = tag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CxDataSgroup that = (CxDataSgroup) o;

            if (atoms != null ? !atoms.equals(that.atoms) : that.atoms != null) return false;
            if (field != null ? !field.equals(that.field) : that.field != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;
            if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;
            if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;
            return tag != null ? tag.equals(that.tag) : that.tag == null;

        }

        @Override
        public int hashCode() {
            int result = atoms != null ? atoms.hashCode() : 0;
            result = 31 * result + (field != null ? field.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (operator != null ? operator.hashCode() : 0);
            result = 31 * result + (unit != null ? unit.hashCode() : 0);
            result = 31 * result + (tag != null ? tag.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "DataSgroup{" +
                   "atoms=" + atoms +
                   ", field='" + field + '\'' +
                   ", value='" + value + '\'' +
                   ", operator='" + operator + '\'' +
                   ", unit='" + unit + '\'' +
                   ", tag='" + tag + '\'' +
                   '}';
        }
    }

    static final class CxPolymerSgroup extends CxSgroup {
        final String        type;
        final String        subscript;
        final String        supscript;

        CxPolymerSgroup(String type, List<Integer> atomset, String subscript, String supscript) {
            assert type != null && atomset != null;
            this.type = type;
            this.atoms = new ArrayList<>(atomset);
            this.subscript = subscript;
            this.supscript = supscript;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CxPolymerSgroup that = (CxPolymerSgroup) o;

            return type.equals(that.type) &&
                   atoms.equals(that.atoms) &&
                   subscript.equals(that.subscript) &&
                   supscript.equals(that.supscript);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, atoms, subscript, supscript, children);
        }

        @Override
        public String toString() {
            return "PolymerSgroup{" +
                   "type='" + type + '\'' +
                   ", atomset=" + atoms +
                   ", subscript='" + subscript + '\'' +
                   ", supscript='" + supscript + '\'' +
                   '}';
        }
    }


    static String escape(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (isEscapeChar(c))
                sb.append("&#").append((int) c).append(';');
            else
                sb.append(c);
        }
        return sb.toString();

    }

    private static boolean isEscapeChar(char c) {
        return c < 32 || c > 126 || c == '|' || c == '{' || c == '}' || c == ',' || c == ';' || c == ':' || c == '$';
    }
}

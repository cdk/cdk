/* Copyright (C) 2004-2007  Matteo Floris <mfe4@users.sf.net>
 *                    2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.interfaces.IAtom;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class returns the valence of an atom.
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.dictref valence, atom
 */
public class AtomValenceTool {

    private static final Map<String, Integer> valencesTable = createValencesTable();

    private static Map<String, Integer> createValencesTable() {
        Map<String, Integer> valences = new HashMap<>();
        valences.put("H", 1);
        valences.put("He", 8);
        valences.put("Ne", 8);
        valences.put("Ar", 8);
        valences.put("Kr", 8);
        valences.put("Xe", 8);
        valences.put("Hg", 2);
        valences.put("Rn", 8);
        valences.put("Li", 1);
        valences.put("Be", 2);
        valences.put("B", 3);
        valences.put("C", 4);
        valences.put("N", 5);
        valences.put("O", 6);
        valences.put("F", 7);
        valences.put("Na", 1);
        valences.put("Mg", 2);
        valences.put("Al", 3);
        valences.put("Si", 4);
        valences.put("P", 5);
        valences.put("S", 6);
        valences.put("Cl", 7);
        valences.put("K", 1);
        valences.put("Ca", 2);
        valences.put("Ga", 3);
        valences.put("Ge", 4);
        valences.put("As", 5);
        valences.put("Se", 6);
        valences.put("Br", 7);
        valences.put("Rb", 1);
        valences.put("Sr", 2);
        valences.put("In", 3);
        valences.put("Sn", 4);
        valences.put("Sb", 5);
        valences.put("Te", 6);
        valences.put("I", 7);
        valences.put("Cs", 1);
        valences.put("Ba", 2);
        valences.put("Tl", 3);
        valences.put("Pb", 4);
        valences.put("Bi", 5);
        valences.put("Po", 6);
        valences.put("At", 7);
        valences.put("Fr", 1);
        valences.put("Ra", 2);
        valences.put("Cu", 2);
        valences.put("Mn", 2);
        valences.put("Co", 2);
        return Collections.unmodifiableMap(valences);
    }

    public static int getValence(IAtom atom) {
        Integer valence = valencesTable.get(atom.getSymbol());
        if (valence == null) {
            throw new IllegalArgumentException("No valence is defined for element " + atom.getSymbol());
        }
        return valence;
    }

}

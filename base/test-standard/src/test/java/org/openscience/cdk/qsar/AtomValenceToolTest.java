/* Copyright (C) 2026  Kamel Mansouri
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.silent.Atom;

class AtomValenceToolTest {

    @Test
    void supportedElements() {
        String[] symbols = {"H", "He", "Ne", "Ar", "Kr", "Xe", "Hg", "Rn", "Li", "Be", "B", "C", "N",
                "O", "F", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "K", "Ca", "Ga", "Ge", "As", "Se",
                "Br", "Rb", "Sr", "In", "Sn", "Sb", "Te", "I", "Cs", "Ba", "Tl", "Pb", "Bi", "Po",
                "At", "Fr", "Ra", "Cu", "Mn", "Co"};
        int[] valences = {1, 8, 8, 8, 8, 8, 2, 8, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4,
                5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 1, 2, 3, 4, 5, 6, 7, 1, 2, 2, 2, 2};

        for (int i = 0; i < symbols.length; i++) {
            Assertions.assertEquals(valences[i], AtomValenceTool.getValence(new Atom(symbols[i])), symbols[i]);
        }
    }

    @Test
    void unsupportedElement() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> AtomValenceTool.getValence(new Atom("Fe")));
    }
}

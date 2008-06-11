/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2004-2007  Matteo Floris <mfe4@users.sf.net>
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

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;

/**
 * This class returns the valence of an atom.
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  standard
 * @cdk.svnrev  $Revision$
 * @cdk.dictref valence, atom
 */
public class AtomValenceTool {

    private static Map<String,Integer> valencesTable = null;

    public static int getValence(IAtom atom) {
        if (valencesTable == null) {
            valencesTable = new HashMap<String,Integer>();
            valencesTable.put("H", 1);
            valencesTable.put("Li", 1);
            valencesTable.put("Be", 2);
            valencesTable.put("B", 3);
            valencesTable.put("C", 4);
            valencesTable.put("N", 5);
            valencesTable.put("O", 6);
            valencesTable.put("F", 7);
            valencesTable.put("Na", 1);
            valencesTable.put("Mg", 2);
            valencesTable.put("Al", 3);
            valencesTable.put("Si", 4);
            valencesTable.put("P", 5);
            valencesTable.put("S", 6);
            valencesTable.put("Cl", 7);
            valencesTable.put("K", 1);
            valencesTable.put("Ca", 2);
            valencesTable.put("Ga", 3);
            valencesTable.put("Ge", 4);
            valencesTable.put("As", 5);
            valencesTable.put("Se", 6);
            valencesTable.put("Br", 7);
            valencesTable.put("Rb", 1);
            valencesTable.put("Sr", 2);
            valencesTable.put("In", 3);
            valencesTable.put("Sn", 4);
            valencesTable.put("Sb", 5);
            valencesTable.put("Te", 6);
            valencesTable.put("I", 7);
            valencesTable.put("Cs", 1);
            valencesTable.put("Ba", 2);
            valencesTable.put("Tl", 3);
            valencesTable.put("Pb", 4);
            valencesTable.put("Bi", 5);
            valencesTable.put("Po", 6);
            valencesTable.put("At", 7);
            valencesTable.put("Fr", 1);
            valencesTable.put("Ra", 2);
            valencesTable.put("Cu", 2);
            valencesTable.put("Mn", 2);
            valencesTable.put("Co", 2);
        }
        
        return valencesTable.get(atom.getSymbol());
    }

}


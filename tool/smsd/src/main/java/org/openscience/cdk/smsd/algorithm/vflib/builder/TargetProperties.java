/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.smsd.algorithm.vflib.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * Class for building/storing nodes (atoms) in the graph with atom
 * query capabilities.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class TargetProperties implements java.io.Serializable {

    private Map<IAtom, Integer>     connectedTargetAtomCountMap = null;
    private Map<IAtom, List<IAtom>> connectedTargetAtomListMap  = null;
    private IBond[][]               map                         = null;
    private Map<IAtom, Integer>     atoms                       = null;
    private Map<Integer, IAtom>     atomsIndex                  = null;

    /**
     * @param atom
     * @return the connectedTargetAtomCountMap
     */
    public Integer countNeighbors(IAtom atom) {
        if (connectedTargetAtomCountMap == null || !connectedTargetAtomCountMap.containsKey(atom)) {
            System.out.println("Object not found in " + atoms.size() + " atoms");
            return 0;
        }
        return connectedTargetAtomCountMap.get(atom);
    }

    /**
     * @param atom
     * @return the connected Target Atom List
     */
    public List<IAtom> getNeighbors(IAtom atom) {
        return connectedTargetAtomListMap.get(atom);
    }

    /**
     * @param atom1
     * @param atom2
     * @return the map
     */
    public IBond getBond(IAtom atom1, IAtom atom2) {
        return map[atoms.get(atom2)][atoms.get(atom1)];
    }

    /**
     * @return atom count
     */
    public int getAtomCount() {
        return atoms.size();
    }

    /**
     *
     * @param container
     */
    public TargetProperties(IAtomContainer container) {
        int i = 0;
        atoms = new HashMap<IAtom, Integer>();
        atomsIndex = new HashMap<Integer, IAtom>();
        connectedTargetAtomCountMap = new HashMap<IAtom, Integer>();
        connectedTargetAtomListMap = new HashMap<IAtom, List<IAtom>>();
        map = new IBond[container.getAtomCount()][container.getAtomCount()];
        for (IAtom atom : container.atoms()) {
            int count = container.getConnectedAtomsCount(atom);
            connectedTargetAtomCountMap.put(atom, count);
            List<IAtom> list = container.getConnectedAtomsList(atom);
            if (list != null) {
                connectedTargetAtomListMap.put(atom, list);
            } else {
                connectedTargetAtomListMap.put(atom, new ArrayList<IAtom>());
            }
            atoms.put(atom, i);
            atomsIndex.put(i, atom);
            i++;
        }

        for (IBond bond : container.bonds()) {
            map[atoms.get(bond.getAtom(0))][atoms.get(bond.getAtom(1))] = bond;
            map[atoms.get(bond.getAtom(1))][atoms.get(bond.getAtom(0))] = bond;
        }
    }

    public IAtom getAtom(int j) {
        return atomsIndex.get(j);
    }
}

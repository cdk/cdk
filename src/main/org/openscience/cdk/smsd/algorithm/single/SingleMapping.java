/* Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
package org.openscience.cdk.smsd.algorithm.single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.smsd.helper.FinalMappings;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * This class handles single atom mapping.
 * Either query and/or target molecule with single atom is mapped by this class.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.single.SingleMappingTest")
public class SingleMapping {

    private IAtomContainer source = null;
    private IAtomContainer target = null;
    private List<TreeMap<Integer, Integer>> mappings = null;
    private Map<Integer, Integer> connectedBondOrder = null;

    /**
     * Returns single mapping solutions.
     * @param source
     * @param target
     * @param removeHydrogen
     * @return Mappings
     */
    @TestMethod("testGetOverLaps")
    protected List<TreeMap<Integer, Integer>> getOverLaps(IAtomContainer source, IAtomContainer target, boolean removeHydrogen) {

        mappings = new ArrayList<TreeMap<Integer, Integer>>();
        connectedBondOrder = new TreeMap<Integer, Integer>();
        this.source = source;
        this.target = target;

        if (source.getAtomCount() == 1) {
            setSourceSingleAtomMap(removeHydrogen);
        }
        if (target.getAtomCount() == 1) {
            setTargetSingleAtomMap(removeHydrogen);
        }
        postFilter();
        return mappings;
    }

    private void setSourceSingleAtomMap(boolean removeHydrogen) {
        int counter = 0;
        if ((removeHydrogen && !source.getAtom(0).getSymbol().equals("H")) || (!removeHydrogen)) {
            for (int i = 0; i < target.getAtomCount(); i++) {
                TreeMap<Integer, Integer> mapAtoms = new TreeMap<Integer, Integer>();

                if (source.getAtom(0).getSymbol().equalsIgnoreCase(target.getAtom(i).getSymbol())) {
                    mapAtoms.put(0, i);
                    IAtom atom = target.getAtom(i);
                    List<IBond> Bonds = target.getConnectedBondsList(atom);

                    int totalOrder = 0;
                    for (IBond bond : Bonds) {

                        Order bondOrder = bond.getOrder();
                        totalOrder += bondOrder.ordinal() + 1;
                    }

                    connectedBondOrder.put(counter, totalOrder);
                    mappings.add(counter++, mapAtoms);
                }
            }
        } else {
            System.err.println("Skippping Hydrogen mapping or This is not a single mapping case!");
        }
    }

    private void setTargetSingleAtomMap(boolean removeHydrogen) {
        int counter = 0;
        if ((removeHydrogen && !target.getAtom(0).getSymbol().equals("H")) || (!removeHydrogen)) {
            for (int i = 0; i < source.getAtomCount(); i++) {
                TreeMap<Integer, Integer> mapAtoms = new TreeMap<Integer, Integer>();

                if (target.getAtom(0).getSymbol().equalsIgnoreCase(source.getAtom(i).getSymbol())) {
                    mapAtoms.put(i, 0);

                    IAtom atom = source.getAtom(i);
                    List<IBond> Bonds = source.getConnectedBondsList(atom);

                    int totalOrder = 0;
                    for (IBond bond : Bonds) {

                        Order bondOrder = bond.getOrder();
                        totalOrder += bondOrder.ordinal() + 1;
                    }
                    connectedBondOrder.put(counter, totalOrder);
                    mappings.add(counter++, mapAtoms);
                }
            }

        } else {
            System.err.println("Skippping Hydrogen mapping or This is not a single mapping case!");
        }
    }

    private void postFilter() {
        List<TreeMap<Integer, Integer>> SortedMap = new ArrayList<TreeMap<Integer, Integer>>();
        connectedBondOrder = sortByValue(connectedBondOrder);
        for (Map.Entry<Integer, Integer> map : connectedBondOrder.entrySet()) {

            TreeMap<Integer, Integer> mapToBeMoved = mappings.get(map.getKey());
            SortedMap.add(mapToBeMoved);

        }
        mappings = SortedMap;
        FinalMappings final_MAPPINGS = FinalMappings.getInstance();
        final_MAPPINGS.set(new ArrayList<Map<Integer, Integer>>(mappings));
    }

    private <K, V> Map<K, V> sortByValue(Map<K, V> map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            public int compare(Object object1, Object object2) {
                return ((Comparable) ((Map.Entry<K, V>) (object1)).getValue()).compareTo(((Map.Entry<K, V>) (object2)).getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

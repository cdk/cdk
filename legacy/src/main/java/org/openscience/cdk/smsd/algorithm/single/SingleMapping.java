/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.tools.BondEnergies;

/**
 * This class handles single atom mapping.
 * Either query and/or target molecule with single atom is mapped by this class.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class SingleMapping {

    private IAtomContainer          source             = null;
    private IAtomContainer          target             = null;
    private List<Map<IAtom, IAtom>> mappings           = null;
    private Map<Integer, Double>    connectedBondOrder = null;

    /**
     * Default
     */
    public SingleMapping() {}

    /**
     * Returns single mapping solutions.
     * @param source
     * @param target
     * @param removeHydrogen
     * @return Mappings
     * @throws CDKException
     */
    protected List<Map<IAtom, IAtom>> getOverLaps(IAtomContainer source, IAtomContainer target, boolean removeHydrogen)
            throws CDKException {

        mappings = new ArrayList<Map<IAtom, IAtom>>();
        connectedBondOrder = new TreeMap<Integer, Double>();
        this.source = source;
        this.target = target;

        if (source.getAtomCount() == 1 || (source.getAtomCount() > 0 && source.getBondCount() == 0)) {
            setSourceSingleAtomMap(removeHydrogen);
        }
        if (target.getAtomCount() == 1 || (target.getAtomCount() > 0 && target.getBondCount() == 0)) {
            setTargetSingleAtomMap(removeHydrogen);
        }

        postFilter();
        return mappings;
    }

    /**
     * Returns single mapping solutions.
     * @param source
     * @param target
     * @param removeHydrogen
     * @return Mappings
     * @throws CDKException
     */
    protected List<Map<IAtom, IAtom>> getOverLaps(IQueryAtomContainer source, IAtomContainer target,
            boolean removeHydrogen) throws CDKException {
        mappings = new ArrayList<Map<IAtom, IAtom>>();
        connectedBondOrder = new TreeMap<Integer, Double>();
        this.source = source;
        this.target = target;

        if (source.getAtomCount() == 1 || (source.getAtomCount() > 0 && source.getBondCount() == 0)) {
            setSourceSingleAtomMap(source, removeHydrogen);
        }
        if (target.getAtomCount() == 1 || (target.getAtomCount() > 0 && target.getBondCount() == 0)) {
            setTargetSingleAtomMap(removeHydrogen);
        }

        postFilter();
        return mappings;
    }

    private void setSourceSingleAtomMap(IQueryAtomContainer source, boolean removeHydrogen) throws CDKException {
        int counter = 0;
        BondEnergies be = BondEnergies.getInstance();
        for (IAtom sourceAtom : source.atoms()) {
            IQueryAtom smartAtom = (IQueryAtom) sourceAtom;
            if ((removeHydrogen && !smartAtom.getSymbol().equals("H")) || (!removeHydrogen)) {
                for (IAtom targetAtom : target.atoms()) {
                    Map<IAtom, IAtom> mapAtoms = new HashMap<IAtom, IAtom>();
                    if (smartAtom.matches(targetAtom)) {
                        mapAtoms.put(sourceAtom, targetAtom);
                        List<IBond> bonds = target.getConnectedBondsList(targetAtom);

                        double totalOrder = 0;
                        for (IBond bond : bonds) {
                            Order order = bond.getOrder();
                            totalOrder += order.numeric() + be.getEnergies(bond);
                        }
                        if (targetAtom.getFormalCharge() != sourceAtom.getFormalCharge()) {
                            totalOrder += 0.5;
                        }
                        connectedBondOrder.put(counter, totalOrder);
                        mappings.add(counter++, mapAtoms);
                    }
                }
            } else {
                System.err.println("Skippping Hydrogen mapping or This is not a single mapping case!");
            }
        }
    }

    private void setSourceSingleAtomMap(boolean removeHydrogen) throws CDKException {
        int counter = 0;
        BondEnergies be = BondEnergies.getInstance();
        for (IAtom sourceAtom : source.atoms()) {
            if ((removeHydrogen && !sourceAtom.getSymbol().equals("H")) || (!removeHydrogen)) {
                for (IAtom targetAtom : target.atoms()) {
                    Map<IAtom, IAtom> mapAtoms = new HashMap<IAtom, IAtom>();
                    if (sourceAtom.getSymbol().equalsIgnoreCase(targetAtom.getSymbol())) {
                        mapAtoms.put(sourceAtom, targetAtom);
                        List<IBond> bonds = target.getConnectedBondsList(targetAtom);

                        double totalOrder = 0;
                        for (IBond bond : bonds) {
                            Order order = bond.getOrder();
                            totalOrder += order.numeric() + be.getEnergies(bond);
                        }
                        if (targetAtom.getFormalCharge() != sourceAtom.getFormalCharge()) {
                            totalOrder += 0.5;
                        }
                        connectedBondOrder.put(counter, totalOrder);
                        mappings.add(counter++, mapAtoms);
                    }
                }
            } else {
                System.err.println("Skippping Hydrogen mapping or This is not a single mapping case!");
            }
        }
    }

    private void setTargetSingleAtomMap(boolean removeHydrogen) throws CDKException {
        int counter = 0;
        BondEnergies be = BondEnergies.getInstance();
        for (IAtom targetAtom : target.atoms()) {
            if ((removeHydrogen && !targetAtom.getSymbol().equals("H")) || (!removeHydrogen)) {
                for (IAtom sourceAtoms : source.atoms()) {
                    Map<IAtom, IAtom> mapAtoms = new HashMap<IAtom, IAtom>();

                    if (targetAtom.getSymbol().equalsIgnoreCase(sourceAtoms.getSymbol())) {
                        mapAtoms.put(sourceAtoms, targetAtom);
                        List<IBond> bonds = source.getConnectedBondsList(sourceAtoms);

                        double totalOrder = 0;
                        for (IBond bond : bonds) {
                            Order order = bond.getOrder();
                            totalOrder += order.numeric() + be.getEnergies(bond);
                        }
                        if (sourceAtoms.getFormalCharge() != targetAtom.getFormalCharge()) {
                            totalOrder += 0.5;
                        }
                        connectedBondOrder.put(counter, totalOrder);
                        mappings.add(counter++, mapAtoms);
                    }
                }
            } else {
                System.err.println("Skippping Hydrogen mapping or This is not a single mapping case!");
            }
        }
    }

    private void postFilter() {
        List<Map<IAtom, IAtom>> sortedMap = new ArrayList<Map<IAtom, IAtom>>();
        connectedBondOrder = sortByValue(connectedBondOrder);
        for (Integer key : connectedBondOrder.keySet()) {
            Map<IAtom, IAtom> mapToBeMoved = mappings.get(key);
            sortedMap.add(mapToBeMoved);
        }
        mappings = sortedMap;
    }

    private <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {

            @Override
            public int compare(Map.Entry<K, V> object1, Map.Entry<K, V> object2) {
                return object1.getValue().compareTo(object2.getValue());
            }
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

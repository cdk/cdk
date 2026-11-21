/* Copyright (C) 2026  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Captures and restores atom, bond, container, and lone-pair state mutated by
 * atom typing, lone-pair saturation, and partial charge calculation without
 * cloning the entire {@link IAtomContainer}.
 */
final class AtomContainerStateSnapshot {

    private final IAtomContainer            container;
    private final Double[]                  charges;
    private final String[]                  atomTypeNames;
    private final String[]                  symbols;
    private final Integer[]                 atomicNumbers;
    private final Double[]                  exactMasses;
    private final IBond.Order[]             maxBondOrders;
    private final Double[]                  bondOrderSums;
    private final Double[]                  covalentRadii;
    private final Integer[]                 formalCharges;
    private final Integer[]                 implicitHydrogenCounts;
    private final Integer[]                 valencies;
    private final IAtomType.Hybridization[] hybridizations;
    private final Integer[]                 formalNeighbourCounts;
    private final String[]                  atomIds;
    private final int[]                     atomFlags;
    private final Map<Object, Object>[]     atomProperties;
    private final int[]                     bondFlags;
    private final int                       containerFlags;
    private final Map<Object, Object>       containerProperties;
    private final ILonePair[]               lonePairs;

    @SuppressWarnings("unchecked")
    private AtomContainerStateSnapshot(IAtomContainer container) {
        this.container = container;
        int atomCount = container.getAtomCount();
        this.charges = new Double[atomCount];
        this.atomTypeNames = new String[atomCount];
        this.symbols = new String[atomCount];
        this.atomicNumbers = new Integer[atomCount];
        this.exactMasses = new Double[atomCount];
        this.maxBondOrders = new IBond.Order[atomCount];
        this.bondOrderSums = new Double[atomCount];
        this.covalentRadii = new Double[atomCount];
        this.formalCharges = new Integer[atomCount];
        this.implicitHydrogenCounts = new Integer[atomCount];
        this.valencies = new Integer[atomCount];
        this.hybridizations = new IAtomType.Hybridization[atomCount];
        this.formalNeighbourCounts = new Integer[atomCount];
        this.atomIds = new String[atomCount];
        this.atomFlags = new int[atomCount];
        this.atomProperties = new Map[atomCount];

        for (int i = 0; i < atomCount; i++) {
            IAtom atom = container.getAtom(i);
            this.charges[i] = atom.getCharge();
            this.atomTypeNames[i] = atom.getAtomTypeName();
            this.symbols[i] = atom.getSymbol();
            this.atomicNumbers[i] = atom.getAtomicNumber();
            this.exactMasses[i] = atom.getExactMass();
            this.maxBondOrders[i] = atom.getMaxBondOrder();
            this.bondOrderSums[i] = atom.getBondOrderSum();
            this.covalentRadii[i] = atom.getCovalentRadius();
            this.formalCharges[i] = atom.getFormalCharge();
            this.implicitHydrogenCounts[i] = atom.getImplicitHydrogenCount();
            this.valencies[i] = atom.getValency();
            this.hybridizations[i] = atom.getHybridization();
            this.formalNeighbourCounts[i] = atom.getFormalNeighbourCount();
            this.atomIds[i] = atom.getID();
            this.atomFlags[i] = atom.flags();
            this.atomProperties[i] = copyProperties(atom);
        }

        int bondCount = container.getBondCount();
        this.bondFlags = new int[bondCount];
        for (int i = 0; i < bondCount; i++) {
            this.bondFlags[i] = container.getBond(i).flags();
        }

        this.containerFlags = container.flags();
        this.containerProperties = copyProperties(container);

        int lpCount = container.getLonePairCount();
        this.lonePairs = new ILonePair[lpCount];
        for (int i = 0; i < lpCount; i++) {
            this.lonePairs[i] = container.getLonePair(i);
        }
    }

    static AtomContainerStateSnapshot capture(IAtomContainer container) {
        return new AtomContainerStateSnapshot(container);
    }

    void restore() {
        int atomCount = container.getAtomCount();
        for (int i = 0; i < atomCount; i++) {
            IAtom atom = container.getAtom(i);
            atom.setCharge(charges[i]);
            atom.setAtomTypeName(atomTypeNames[i]);
            if (!(atom instanceof IPseudoAtom)) {
                if (!Objects.equals(atom.getSymbol(), symbols[i])) {
                    atom.setSymbol(symbols[i]);
                }
                if (!Objects.equals(atom.getAtomicNumber(), atomicNumbers[i])) {
                    atom.setAtomicNumber(atomicNumbers[i]);
                }
            }
            atom.setExactMass(exactMasses[i]);
            atom.setMaxBondOrder(maxBondOrders[i]);
            atom.setBondOrderSum(bondOrderSums[i]);
            atom.setCovalentRadius(covalentRadii[i]);
            atom.setFormalCharge(formalCharges[i]);
            atom.setImplicitHydrogenCount(implicitHydrogenCounts[i]);
            atom.setValency(valencies[i]);
            atom.setHybridization(hybridizations[i]);
            atom.setFormalNeighbourCount(formalNeighbourCounts[i]);
            atom.setID(atomIds[i]);
            restoreFlags(atom, atomFlags[i]);
            restoreProperties(atom, atomProperties[i]);
        }

        int bondCount = container.getBondCount();
        for (int i = 0; i < bondCount; i++) {
            restoreFlags(container.getBond(i), bondFlags[i]);
        }

        restoreFlags(container, containerFlags);
        restoreProperties(container, containerProperties);
        restoreLonePairs();
    }

    private void restoreLonePairs() {
        int currentCount = container.getLonePairCount();
        if (currentCount == lonePairs.length && hasOriginalLonePairPrefix()) {
            return;
        }
        if (currentCount > lonePairs.length && hasOriginalLonePairPrefix()) {
            for (int i = currentCount - 1; i >= lonePairs.length; i--) {
                container.removeLonePair(i);
            }
            return;
        }
        for (int i = currentCount - 1; i >= 0; i--) {
            container.removeLonePair(i);
        }
        for (ILonePair lp : lonePairs) {
            container.addLonePair(lp);
        }
    }

    private boolean hasOriginalLonePairPrefix() {
        for (int i = 0; i < lonePairs.length; i++) {
            if (container.getLonePair(i) != lonePairs[i]) {
                return false;
            }
        }
        return true;
    }

    private static Map<Object, Object> copyProperties(IChemObject chemObject) {
        Map<Object, Object> props = chemObject.getProperties();
        return (props == null || props.isEmpty()) ? null : new HashMap<>(props);
    }

    private static void restoreProperties(IChemObject chemObject, Map<Object, Object> savedProperties) {
        if (savedProperties == null) {
            Map<Object, Object> current = chemObject.getProperties();
            if (current != null && !current.isEmpty()) {
                chemObject.setProperties(null);
            }
        } else {
            chemObject.setProperties(savedProperties);
        }
    }

    private static void restoreFlags(IChemObject chemObject, int savedFlags) {
        int currentFlags = chemObject.flags();
        if (currentFlags != savedFlags) {
            chemObject.clear(currentFlags & ~savedFlags);
            chemObject.set(savedFlags & ~currentFlags);
        }
    }
}

/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.graph;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

/**
 * Tool class for checking whether the (sub)structure in an
 * AtomContainer is connected.
 * To check whether an AtomContainer is connected this code
 * can be used:
 * <pre>
 *  boolean isConnected = ConnectivityChecker.isConnected(atomContainer);
 * </pre>
 *
 * <p>A disconnected AtomContainer can be fragmented into connected
 * fragments by using code like:
 * <pre>
 *   MoleculeSet fragments = ConnectivityChecker.partitionIntoMolecules(disconnectedContainer);
 *   int fragmentCount = fragments.getAtomContainerCount();
 * </pre>
 *
 * @cdk.module standard
 * @cdk.githash
 *
 * @cdk.keyword connectivity
 */
public class ConnectivityChecker {

    /**
     * Check whether a set of atoms in an {@link IAtomContainer} is connected.
     *
     * @param   atomContainer  The {@link IAtomContainer} to be check for connectedness
     * @return                 true if the {@link IAtomContainer} is connected
     */
    public static boolean isConnected(IAtomContainer atomContainer) {
        // with one atom or less, we define it to be connected, as there is no
        // partitioning needed
        if (atomContainer.getAtomCount() < 2) return true;

        ConnectedComponents cc = new ConnectedComponents(GraphUtil.toAdjList(atomContainer));
        return cc.nComponents() == 1;
    }

    /**
     * Partitions the atoms in an AtomContainer into covalently connected components.
     *
     * @param   container  The AtomContainer to be partitioned into connected components, i.e. molecules
     * @return                 A MoleculeSet.
     *
     * @cdk.dictref   blue-obelisk:graphPartitioning
     */
    public static IAtomContainerSet partitionIntoMolecules(IAtomContainer container) {
        ConnectedComponents cc = new ConnectedComponents(GraphUtil.toAdjList(container));
        return partitionIntoMolecules(container, cc.components());
    }

    public static IAtomContainerSet partitionIntoMolecules(IAtomContainer container, int[] components) {

        int maxComponentIndex = 0;
        for (int component : components)
            if (component > maxComponentIndex)
                maxComponentIndex = component;

        IAtomContainer[] containers = new IAtomContainer[maxComponentIndex + 1];
        Map<IAtom, IAtomContainer> componentsMap = new HashMap<IAtom, IAtomContainer>(2 * container.getAtomCount());

        for (int i = 1; i < containers.length; i++)
            containers[i] = container.getBuilder().newInstance(IAtomContainer.class);

        IAtomContainerSet containerSet = container.getBuilder().newInstance(IAtomContainerSet.class);

        for (int i = 0; i < container.getAtomCount(); i++) {
            componentsMap.put(container.getAtom(i), containers[components[i]]);
            containers[components[i]].addAtom(container.getAtom(i));
        }

        for (IBond bond : container.bonds()) {
            IAtomContainer begComp = componentsMap.get(bond.getBegin());
            IAtomContainer endComp = componentsMap.get(bond.getEnd());
            if (begComp == endComp)
                begComp.addBond(bond);
        }

        for (ISingleElectron electron : container.singleElectrons())
            componentsMap.get(electron.getAtom()).addSingleElectron(electron);

        for (ILonePair lonePair : container.lonePairs())
            componentsMap.get(lonePair.getAtom()).addLonePair(lonePair);

        for (IStereoElement stereo : container.stereoElements()) {
            if (stereo instanceof ITetrahedralChirality) {
                IAtom a = ((ITetrahedralChirality) stereo).getChiralAtom();
                if (componentsMap.containsKey(a)) componentsMap.get(a).addStereoElement(stereo);
            } else if (stereo instanceof IDoubleBondStereochemistry) {
                IBond bond = ((IDoubleBondStereochemistry) stereo).getStereoBond();
                if (componentsMap.containsKey(bond.getBegin()) && componentsMap.containsKey(bond.getEnd()))
                    componentsMap.get(bond.getBegin()).addStereoElement(stereo);
            } else if (stereo instanceof ExtendedTetrahedral) {
                IAtom atom = ((ExtendedTetrahedral) stereo).focus();
                if (componentsMap.containsKey(atom)) componentsMap.get(atom).addStereoElement(stereo);
            } else {
                System.err.println("New stereochemistry element is not currently partitioned with ConnectivityChecker:"
                        + stereo.getClass());
            }
        }

        for (int i = 1; i < containers.length; i++)
            containerSet.addAtomContainer(containers[i]);

        return containerSet;
    }
}

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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.tools.manipulator.SgroupManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        if (cc.nComponents() == 1) {
            return singleton(container);
        }
        return partitionIntoMolecules(container, cc.components());
    }

    private static IAtomContainerSet singleton(IAtomContainer container) {
        IChemObjectBuilder bldr = container.getBuilder();
        IAtomContainerSet acSet = bldr.newInstance(IAtomContainerSet.class);
        acSet.addAtomContainer(container);
        return acSet;
    }

    private static IAtomContainerSet empty(IAtomContainer container) {
        IChemObjectBuilder bldr = container.getBuilder();
        return bldr.newInstance(IAtomContainerSet.class);
    }

    private static IAtomContainer getComponent(Map<IAtom, IAtomContainer> cmap,
                                               IChemObject cobj) {
        if (cobj instanceof IAtom)
            return cmap.get(cobj);
        else if (cobj instanceof IBond) {
            IAtomContainer begMol = cmap.get(((IBond) cobj).getBegin());
            IAtomContainer endMol = cmap.get(((IBond) cobj).getEnd());
            return begMol == endMol ? begMol : null;
        }
        return null;
    }

    /**
     * Given a component mapping atom -> molecule, provide the atom container
     * to add the stereochemistry to. If the stereo is split across two
     * molecules (components) null is returned.
     *
     * @param cmap component map
     * @param se stereo element
     * @return the molecule (or null if inconsistent)
     */
    private static IAtomContainer getComponent(Map<IAtom, IAtomContainer> cmap,
                                               IStereoElement<?, ?> se) {
        IAtomContainer mol = getComponent(cmap, se.getFocus());
        for (IChemObject cobj : se.getCarriers()) {
            IAtomContainer tmp = getComponent(cmap, cobj);
            if (tmp != mol)
                return null; // inconsistent
        }
        return mol;
    }

    private static IAtomContainer getComponent(Map<IAtom, IAtomContainer> cmap,
                                               Sgroup sgroup) {
        IAtomContainer mol = null;
        for (IAtom atom : sgroup.getAtoms()) {
            IAtomContainer tmp = cmap.get(atom);
            if (mol == null)
                mol = tmp;
            else if (mol != tmp)
                return null;
        }
        return mol;
    }

    private static void addSgroup(IAtomContainer component, Sgroup sgroup) {
        List<Sgroup> sgroups = component.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null) {
            sgroups = new ArrayList<>();
            component.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);
        }
        sgroups.add(sgroup);
    }

    /**
     * Split a molecule based on the provided component array. Note this function
     * can also be used to split a single molecule, breaking bonds and distributing
     * stereochemistry as needed.
     *
     * @param container the container
     * @param components the components
     * @return the partitioned set
     */
    public static IAtomContainerSet partitionIntoMolecules(IAtomContainer container, int[] components) {

        int minComponentIndex = Integer.MAX_VALUE;
        int maxComponentIndex = 0;
        for (int component : components) {
            minComponentIndex = Math.min(component, minComponentIndex);
            maxComponentIndex = Math.max(component, maxComponentIndex);
        }

        if (minComponentIndex == maxComponentIndex) {
            if (maxComponentIndex == 0)
                return empty(container);
            else
                return singleton(container);
        }

        IAtomContainer[] containers = new IAtomContainer[maxComponentIndex + 1];
        Map<IAtom, IAtomContainer> componentsMap = new HashMap<>(2 * container.getAtomCount());

        for (int i = 1; i < containers.length; i++)
            containers[i] = container.getBuilder().newInstance(IAtomContainer.class);

        IAtomContainerSet containerSet = container.getBuilder().newInstance(IAtomContainerSet.class);

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom origAtom = container.getAtom(i);
            IAtomContainer newContainer = containers[components[i]];
            componentsMap.put(origAtom, newContainer);
            newContainer.addAtom(origAtom);
        }

        for (IBond bond : container.bonds()) {
            IAtomContainer begComp = componentsMap.get(bond.getBegin());
            IAtomContainer endComp = componentsMap.get(bond.getEnd());
            if (begComp == endComp) {
                begComp.addBond(bond);
            }
        }

        for (ISingleElectron electron : container.singleElectrons())
            componentsMap.get(electron.getAtom()).addSingleElectron(electron);

        for (ILonePair lonePair : container.lonePairs())
            componentsMap.get(lonePair.getAtom()).addLonePair(lonePair);

        // split stereo chemistry, only keep if all atoms/bond in the stereo
        // element are consistent and in the same container
        for (IStereoElement<?,?>stereo : container.stereoElements()) {
            IAtomContainer component = getComponent(componentsMap, stereo);
            if (component != null)
                component.addStereoElement(stereo);
        }

        // split Sgroups, only keep if all atoms/bond in the sgroup
        // are consistent and in the same container
        List<Sgroup> sgroups = SgroupManipulator.copy(container.getProperty(CDKConstants.CTAB_SGROUPS),
                                                       new HashMap<>());
        if (sgroups != null) {
            Map<Sgroup,IAtomContainer> sgroupMap = new HashMap<>();
            for (Sgroup sgroup : sgroups) {
                IAtomContainer component = getComponent(componentsMap, sgroup);
                if (component != null) {
                    addSgroup(component, sgroup);
                }
            }
            // remove any parents that were split
            for (Sgroup sgroup : sgroups) {
                Set<Sgroup> toremove = new HashSet<>();
                for (Sgroup parent : sgroup.getParents()) {
                    if (sgroupMap.get(parent) == null)
                        toremove.add(parent);
                }
                sgroup.removeParents(toremove);
            }
        }

        // create our AtomContainerSet
        for (int i = 1; i < containers.length; i++)
            containerSet.addAtomContainer(containers[i]);
        return containerSet;
    }
}

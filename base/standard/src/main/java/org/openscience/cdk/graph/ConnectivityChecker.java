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

import java.util.*;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
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
        Map<IAtom, IAtomContainer> componentsMap = new HashMap<>(2 * container.getAtomCount());
        Map<IAtom, IAtom> componentAtomMap = new HashMap<>(2 * container.getAtomCount());
        Map<IBond, IBond> componentBondMap = new HashMap<>(2 * container.getBondCount());


        for (int i = 1; i < containers.length; i++)
            containers[i] = container.getBuilder().newInstance(IAtomContainer.class);

        IAtomContainerSet containerSet = container.getBuilder().newInstance(IAtomContainerSet.class);

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtom origAtom = container.getAtom(i);
            IAtomContainer newContainer = containers[components[i]];
            componentsMap.put(origAtom, newContainer);
            newContainer.addAtom(origAtom);
            //the atom should always be added so this should be safe
            componentAtomMap.put(origAtom, newContainer.getAtom(newContainer.getAtomCount()-1));
        }

        for (IBond bond : container.bonds()) {
            IAtomContainer begComp = componentsMap.get(bond.getBegin());
            IAtomContainer endComp = componentsMap.get(bond.getEnd());
            if (begComp == endComp) {
                begComp.addBond(bond);
                //bond should always be added so this should be safe
                componentBondMap.put(bond, begComp.getBond(begComp.getBondCount()-1));
            }
        }

        for (ISingleElectron electron : container.singleElectrons())
            componentsMap.get(electron.getAtom()).addSingleElectron(electron);

        for (ILonePair lonePair : container.lonePairs())
            componentsMap.get(lonePair.getAtom()).addLonePair(lonePair);

        for (IStereoElement stereo : container.stereoElements()) {
            IChemObject focus = stereo.getFocus();
            if (focus instanceof IAtom) {
                if (componentsMap.containsKey(focus))
                    componentsMap.get(focus).addStereoElement(stereo);
            } else if (focus instanceof IBond) {
                if (componentsMap.containsKey(((IBond) focus).getBegin()))
                    componentsMap.get(((IBond) focus).getBegin()).addStereoElement(stereo);
            } else {
                throw new IllegalStateException("New stereo element not using an atom/bond for focus?");
            }
        }
        //add SGroups
        List<Sgroup> sgroups = container.getProperty(CDKConstants.CTAB_SGROUPS);

        if(sgroups !=null){
            Map<Sgroup, Sgroup> old2NewSgroupMap = new HashMap<>();
            List<Sgroup>[] newSgroups = new List[containers.length];
            for(Sgroup sgroup : sgroups){
                Iterator<IAtom> iter =sgroup.getAtoms().iterator();
                if(!iter.hasNext()){
                   continue;
                }
                int componentIndex = getComponentIndexFor(components, containers,iter.next());
                boolean allMatch=componentIndex>=0;
                while(allMatch && iter.hasNext()){
                    //if component index for some atoms
                    //don't match then the sgroup is split across components
                    //so ignore it for now?
                    allMatch = (componentIndex == getComponentIndexFor(components,containers, iter.next()));
                }
                if(allMatch && componentIndex >=0){
                    Sgroup cpy = new Sgroup();
                    List<Sgroup> newComponentSgroups = newSgroups[componentIndex];
                    if(newComponentSgroups ==null){
                        newComponentSgroups = newSgroups[componentIndex] = new ArrayList<>();
                    }
                    newComponentSgroups.add(cpy);
                    old2NewSgroupMap.put(sgroup, cpy);
                    for (IAtom atom : sgroup.getAtoms()) {
                       cpy.addAtom(componentAtomMap.get(atom));

                    }
                    for (IBond bond : sgroup.getBonds()) {
                        IBond newBond = componentBondMap.get(bond);
                        if(newBond!=null) {
                            cpy.addBond(newBond);
                        }
                    }

                    for (SgroupKey key : sgroup.getAttributeKeys())
                        cpy.putValue(key, sgroup.getValue(key));

                }
            }
            //finally update parents
            for(Sgroup sgroup : sgroups){
                Sgroup newSgroup = old2NewSgroupMap.get(sgroup);
                if(newSgroup !=null){
                    for (Sgroup parent : sgroup.getParents()){
                        Sgroup newParent = old2NewSgroupMap.get(parent);
                        if(newParent !=null){
                            newSgroup.addParent(newParent);
                        }
                    }
                }
            }
            for(int i=1; i< containers.length; i++){
                List<Sgroup> sg = newSgroups[i];
                if(sg !=null){
                    containers[i].setProperty(CDKConstants.CTAB_SGROUPS, sg);
                }
            }
        }
        for (int i = 1; i < containers.length; i++)
            containerSet.addAtomContainer(containers[i]);

        return containerSet;
    }

    private static int getComponentIndexFor(int[] components, IAtomContainer[] containers, IAtom atom) {
        int aIndex = atom.getIndex();
        if(aIndex >= 0) {
            return components[aIndex];
        }
        //if index isn't known check each container
        for (int i = 1; i < containers.length; i++){
            if(containers[i].contains(atom)){
                return i;
            }
        }
        return -1;
    }
}

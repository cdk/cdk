/*
 * Copyright (c) 2022 Julian Zander <zanderjulian@gmx.de>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools.scaffold;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The NetworkNodes are nodes from which a {@link ScaffoldNetwork} can be built.
 * It is used to organise the IAtomContainers and enables a relationship between the different objects.
 * A NetworkNode can have multiple children and parents.
 * The parents are the smaller parent scaffolds.
 * @param <MoleculeType> As MoleculeType, any data type can be defined.
 *                     In our scenario, the node contains a CDK IAtomContainer.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.0.0.0
 */
public class NetworkNode <MoleculeType> extends ScaffoldNodeBase<MoleculeType> {
    /**
     * parents of the node
     */
    private List<NetworkNode<MoleculeType>> parents;

    /**
     * Creates a NetworkNode
     * @param aMolecule molecule of the NetworkNode
     */
    public NetworkNode(MoleculeType aMolecule) {
        super(aMolecule);
        this.parents =  new ArrayList<NetworkNode<MoleculeType>>();
    }

    @Override
    public boolean isOrphan() {
        return parents.isEmpty();
    }

    /**
     * @throws NullPointerException if parameter is null
     */
    @Override
    public NetworkNode<MoleculeType> addChild(MoleculeType aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        NetworkNode<MoleculeType> tmpChildNode = new NetworkNode<MoleculeType>(aMolecule);
        this.children.add(tmpChildNode);
        return tmpChildNode;
    }

    /**
     * Add the parents node and add this node as child to the parent node if not already done.
     * @param aParent parent that are added
     * @throws NullPointerException if parameter is null
     */
    public void addParent(NetworkNode<MoleculeType> aParent) throws NullPointerException {
        Objects.requireNonNull(aParent, "Given NetworkNode is 'null'");
        boolean tmpIsAlreadyChild = false;
        /*Check if the child is not already set*/
        for(ScaffoldNodeBase<MoleculeType> tmpBaseNode : aParent.getChildren()) {
            NetworkNode<MoleculeType> tmpNode = (NetworkNode<MoleculeType>) tmpBaseNode;
            if (tmpNode.getMolecule().equals(this.getMolecule())) {
                tmpIsAlreadyChild = true;
                break;
            }
        }
        /*Add child if not already added*/
        if(!tmpIsAlreadyChild) {
            aParent.addChild(this.getMolecule());
        }
        //Add parent
        this.parents.add(aParent);
    }

    @Override
    public int getLevel() {
        if (this.isOrphan())
            return 0;
        else
            return parents.get(0).getLevel() + 1;
    }

    /**
     * Get the parents of the node.
     * @return parents node
     */
    public List<NetworkNode<MoleculeType>> getParents() {
        return this.parents;
    }

    /**
     * Set the parents of the node.
     * @param aParents parents that are set
     * @throws NullPointerException if parameter is null
     */
    public void setParents(List<NetworkNode<MoleculeType>> aParents) throws NullPointerException {
        Objects.requireNonNull(aParents, "Given NetworkNode is 'null'");
        this.parents = aParents;
    }
}

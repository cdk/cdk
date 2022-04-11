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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Inspired by: <a href="https://github.com/gt4dev/yet-another-tree-structure">Another Tree Structure</a>
 * <br>
 * Base class of node objects.
 * Contains the basic functionality of nodes needed for a {@link ScaffoldTree} or {@link ScaffoldNetwork}.
 *
 * @param <MoleculeType> As MoleculeType, any data type can be defined.
 *                      In our scenario, the node contains a CDK IAtomContainer.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.0.1.0
 */
public abstract class ScaffoldNodeBase<MoleculeType> {

    //<editor-fold desc="Protected variables">
    /**
     * Molecule that can be stored in each node
     */
    protected MoleculeType molecule;

    /**
     * List of SMILES of the molecules from which this fragment originates.
     *
     * If additional information of the origin is needed,
     * it can be stored in a matrix with the IAtomContainer. The SMILES stored here can then be used as a key.
     */
    protected ArrayList<String> originSmilesList;

    /**
     * List of SMILES of the molecules from which this fragment directly originates.
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     *
     * If additional information of the origin is needed,
     * it can be stored in a matrix with the IAtomContainer. The SMILES stored here can then be used as a key.
     */
    protected ArrayList<String> nonVirtualOriginSmilesList;

    /**
     * Children of the Node
     */
    protected List<ScaffoldNodeBase<MoleculeType>> children;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Constructor
     * @param aMolecule molecule of the ScaffoldNodeBase
     * @throws NullPointerException if parameter is null
     */
    public ScaffoldNodeBase(MoleculeType aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        this.molecule = aMolecule;
        this.children = new LinkedList<ScaffoldNodeBase<MoleculeType>>();
        this.originSmilesList = new ArrayList<String>();
        this.nonVirtualOriginSmilesList = new ArrayList<String>();
    }
    //</editor-fold>

    //<editor-fold desc="Public methods">
    /**
     * Indicates whether it has children
     * @return true if it has no children
     */
    public boolean isLeaf() {
        return children.size() == 0;
    }

    /**
     * Shows if the node has parents
     * @return Whether the node has parents
     */
    public abstract boolean isOrphan();

    /**
     * Adds a child to the ScaffoldNodeBase, i.e. links it to a ScaffoldNodeBase on the level below
     * @param aMolecule Molecule of the child leave
     * @return Node of the child leave
     * @throws NullPointerException if parameter is null
     */
    public abstract ScaffoldNodeBase<MoleculeType> addChild(MoleculeType aMolecule) throws NullPointerException;

    /**
     * Adds another string to the OriginSmilesList if it is not already present.
     * @param aString String to be added
     * @throws NullPointerException if parameter is null
     */
    public void addOriginSmiles(String aString) throws NullPointerException {
        Objects.requireNonNull(aString, "Given SMILES of the molecule is 'null'");
        if(!this.originSmilesList.contains(aString)) {
            this.originSmilesList.add(aString);
        }
    }

    /**
     * Adds another string to the NonVirtualOriginSmilesList if it is not already present.
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     * @param aString String to be added
     * @throws NullPointerException if parameter is null
     */
    public void addNonVirtualOriginSmiles(String aString) throws NullPointerException {
        Objects.requireNonNull(aString, "Given SMILES of the molecule is 'null'");
        if(!this.nonVirtualOriginSmilesList.contains(aString)) {
            this.nonVirtualOriginSmilesList.add(aString);
        }
    }

    /**
     * Indicates whether the molecule has at least one nonVirtualOrigin.
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     * @return true if the molecule has at least one nonVirtualOrigin
     */
    public boolean hasNonVirtualOriginSmiles() {
        return !this.nonVirtualOriginSmilesList.isEmpty();
    }
    //<editor-fold desc="get/set">

    /**
     * Outputs the level on which the node is located in the entire tree
     * The level indicates the distance to the root (node without parents) and
     * is determined by setting the level of the parent node + 1. The root itself has the level 0.
     * The level is therefore dependent on the data structure and does not have to be set.
     * @return level of the node in the entire NodeCollection
     */
    public abstract int getLevel();

    /**
     * Get the node molecule.
     * @return node molecule
     */
    public MoleculeType getMolecule() {
        return this.molecule;
    }

    /**
     * Set the node molecule.
     * @param aMolecule molecule that are set
     * @throws NullPointerException if parameter is null
     */
    public void setMolecule(MoleculeType aMolecule) throws  NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        this.molecule = aMolecule;
    }

    /**
     * Get the children nodes.
     * @return children nodes
     */
    public List<ScaffoldNodeBase<MoleculeType>> getChildren() {
        return this.children;
    }

    /**
     * Set the children node.
     * @param aChildren children that are set
     * @throws NullPointerException if parameter is null
     */
    public void setChildren(List<ScaffoldNodeBase<MoleculeType>> aChildren) throws NullPointerException{
        Objects.requireNonNull(aChildren, "Given ScaffoldNodeBase List is 'null'");
        this.children = aChildren;
    }

    /**
     * Get the originSmilesList
     * @return List of SMILES of the molecules from which this fragment originates
     */
    public ArrayList<String> getOriginSmilesList() {
        return this.originSmilesList;
    }

    /**
     * Get the nonVirtualOriginSmilesList
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     * @return List of SMILES of the molecules from which this fragment originates
     */
    public ArrayList<String> getNonVirtualOriginSmilesList() {
        return this.nonVirtualOriginSmilesList;
    }

    /**
     * Get the size of the originSmilesList
     * @return size of the originSmilesList
     */
    public Integer getOriginCount() {
        return this.originSmilesList.size();
    }

    /**
     * Get the size of the nonVirtualSmilesList
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     * @return size of the nonVirtualSmilesList
     */
    public Integer getNonVirtualOriginCount() {
        return this.nonVirtualOriginSmilesList.size();
    }

    /**
     * Set the entire originSmilesList
     * @param aOriginSmilesList SMILES of molecules that are set
     * @throws NullPointerException if parameter is null
     */
    public void setOriginSmilesList(ArrayList<String> aOriginSmilesList) throws NullPointerException {
        Objects.requireNonNull(aOriginSmilesList, "Given SMILES of the molecule List is 'null'");
        this.originSmilesList = aOriginSmilesList;
    }

    /**
     * Set the entire nonVirtualOriginSmilesList.
     * NonVirtualOrigin: This node is the direct(without further fragmentation) scaffold of this origin molecule.
     * @param aNonVirtualOriginSmilesList SMILES of molecules that are set
     * @throws NullPointerException if parameter is null
     */
    public void setNonVirtualOriginSmilesList(ArrayList<String> aNonVirtualOriginSmilesList) throws NullPointerException {
        Objects.requireNonNull(aNonVirtualOriginSmilesList, "Given SMILES of the molecule List is 'null'");
        this.nonVirtualOriginSmilesList = aNonVirtualOriginSmilesList;
    }
    //</editor-fold>
    //</editor-fold>
}

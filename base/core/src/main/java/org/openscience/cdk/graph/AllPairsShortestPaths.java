/*
 * Copyright (C) 2012 John May <jwmay@users.sf.net>
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
package org.openscience.cdk.graph;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.List;
import java.util.Map;

/**
 * Utility to determine the shortest paths between all pairs of atoms in a
 * molecule.
 *
 * <blockquote><pre>
 * IAtomContainer        benzene = MoleculeFactory.makeBenzene();
 * AllPairsShortestPaths apsp    = new AllPairsShortestPaths(benzene);
 *
 * for (int i = 0; i &lt; benzene.getAtomCount(); i++) {
 *
 *     // only to half the comparisons, we can reverse the
 *     // path[] to get all j to i
 *     for (int j = i + 1; j &lt; benzene.getAtomCount(); j++) {
 *
 *         // reconstruct shortest path from i to j
 *         int[] path = apsp.from(i).pathTo(j);
 *
 *         // reconstruct all shortest paths from i to j
 *         int[][] paths = apsp.from(i).pathsTo(j);
 *
 *         // reconstruct the atoms in the path from i to j
 *         IAtom[] atoms = apsp.from(i).atomsTo(j);
 *
 *         // access the number of paths from i to j
 *         int nPaths = apsp.from(i).nPathsTo(j);
 *
 *         // access the distance from i to j
 *         int distance = apsp.from(i).nPathsTo(j);
 *
 *     }
 * }
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 * @see ShortestPaths
 */
@TestClass("org.openscience.cdk.graph.AllPairsShortestPathsTest")
public final class AllPairsShortestPaths {

    private final IAtomContainer  container;
    private final ShortestPaths[] shortestPaths;

    /**
     * Create a new all shortest paths utility for an {@link IAtomContainer}.
     *
     * @param container the molecule of which to find the shortest paths
     */
    @TestMethod("testConstruction_Null,testConstruction_Empty")
    public AllPairsShortestPaths(IAtomContainer container) {

        // toAdjList performs null check
        int[][] adjacent = GraphUtil.toAdjList(container);

        int n = container.getAtomCount();

        this.container     = container;
        this.shortestPaths = new ShortestPaths[n];

        // for each atom construct the ShortestPaths object
        for (int i = 0; i < n; i++) {
            shortestPaths[i] = new ShortestPaths(adjacent, container, i);
        }

    }

    /**
     * Access the shortest paths object for provided start vertex.
     *
     * <blockquote><pre>
     * AllPairsShortestPaths apsp = ...;
     *
     * // access explicitly
     * ShortestPaths sp = asp.from(0);
     *
     * // or chain method calls
     * int[] path = asp.from(0).pathTo(5);
     * </pre></blockquote>
     *
     * @param start the start vertex of the path
     * @return The shortest paths from the given state vertex
     * @see ShortestPaths
     */
    @TestMethod("testFrom_Int_Benzene")
    public ShortestPaths from(int start) {
        return (start < 0 || start >= shortestPaths.length)
                ? EMPTY_SHORTEST_PATHS
                : shortestPaths[start];
    }

    /**
     * Access the shortest paths object for provided start atom.
     *
     * <blockquote><pre>
     * AllPairsShortestPaths apsp = ...;
     * IAtom start, end = ...;
     *
     * // access explicitly
     * ShortestPaths sp = asp.from(start);
     *
     * // or chain the method calls together
     *
     * // first path from start to end atom
     * int[] path = asp.from(start).pathTo(end);
     *
     * // first atom path from start to end atom
     * IAtom[] atoms = asp.from(start).atomTo(end);
     * </pre></blockquote>
     *
     * @param start the start atom of the path
     * @return The shortest paths from the given state vertex
     * @see ShortestPaths
     */
    @TestMethod("testFrom_Atom_Benzene")
    public ShortestPaths from(IAtom start) {
        // currently container.getAtomNumber() return -1 when null
        return from(container.getAtomNumber(start));
    }


    /**
     * an empty atom container so we can handle invalid vertices/atoms better.
     * Not very pretty but we can't access the domain model from cdk-core.
     */
    private static final IAtomContainer EMPTY_CONTAINER = new IAtomContainer() {

        public void addStereoElement(IStereoElement element) {
        }

        public void setStereoElements(List<IStereoElement> elements) {
        }

        public Iterable<IStereoElement> stereoElements() {
            throw new UnsupportedOperationException("not supported");
        }

        public void setAtoms(IAtom[] atoms) {
        }

        public void setBonds(IBond[] bonds) {
        }

        public void setAtom(int number, IAtom atom) {
        }

        public IAtom getAtom(int number) {
            throw new UnsupportedOperationException("not supported");
        }

        public IBond getBond(int number) {
            throw new UnsupportedOperationException("not supported");
        }

        public ILonePair getLonePair(int number) {
            throw new UnsupportedOperationException("not supported");
        }

        public ISingleElectron getSingleElectron(int number) {
            throw new UnsupportedOperationException("not supported");
        }

        public Iterable<IAtom> atoms() {
            throw new UnsupportedOperationException("not supported");
        }

        public Iterable<IBond> bonds() {
            throw new UnsupportedOperationException("not supported");
        }


        public Iterable<ILonePair> lonePairs() {
            throw new UnsupportedOperationException("not supported");
        }

        public Iterable<ISingleElectron> singleElectrons() {
            throw new UnsupportedOperationException("not supported");
        }

        public Iterable<IElectronContainer> electronContainers() {
            throw new UnsupportedOperationException("not supported");
        }

        public IAtom getFirstAtom() {
            throw new UnsupportedOperationException("not supported");
        }

        public IAtom getLastAtom() {
            throw new UnsupportedOperationException("not supported");
        }

        public int getAtomNumber(IAtom atom) {
            return -1;
        }

        public int getBondNumber(IAtom atom1, IAtom atom2) {
            return -1;
        }

        public int getBondNumber(IBond bond) {
            return -1;
        }

        public int getLonePairNumber(ILonePair lonePair) {
            return -1;
        }

        public int getSingleElectronNumber(ISingleElectron singleElectron) {
            return -1;
        }

        public IElectronContainer getElectronContainer(int number) {
            throw new UnsupportedOperationException("not supported");
        }

        public IBond getBond(IAtom atom1, IAtom atom2) {
            throw new UnsupportedOperationException("not supported");
        }

        public int getAtomCount() {
            return 0;
        }

        public int getBondCount() {
            return 0;
        }

        public int getLonePairCount() {
            return 0;
        }

        public int getSingleElectronCount() {
            return 0;
        }

        public int getElectronContainerCount() {
            return 0;
        }

        public List<IAtom> getConnectedAtomsList(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public List<IBond> getConnectedBondsList(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public List<IElectronContainer> getConnectedElectronContainersList(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public int getConnectedAtomsCount(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public int getConnectedBondsCount(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public int getConnectedBondsCount(int atomnumber) {
            return 0;
        }

        public int getConnectedLonePairsCount(IAtom atom) {
            return 0;
        }

        public int getConnectedSingleElectronsCount(IAtom atom) {
            return 0;
        }

        public double getBondOrderSum(IAtom atom) {
            return 0;
        }

        public IBond.Order getMaximumBondOrder(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public IBond.Order getMinimumBondOrder(IAtom atom) {
            throw new UnsupportedOperationException("not supported");
        }

        public void add(IAtomContainer atomContainer) {

        }

        public void addAtom(IAtom atom) {

        }

        public void addBond(IBond bond) {

        }

        public void addLonePair(ILonePair lonePair) {

        }

        public void addSingleElectron(ISingleElectron singleElectron) {

        }

        public void addElectronContainer(IElectronContainer electronContainer) {

        }

        public void remove(IAtomContainer atomContainer) {

        }

        public void removeAtom(int position) {

        }

        public void removeAtom(IAtom atom) {

        }

        public IBond removeBond(int position) {
            throw new UnsupportedOperationException("not supported");
        }

        public IBond removeBond(IAtom atom1, IAtom atom2) {
            throw new UnsupportedOperationException("not supported");
        }

        public void removeBond(IBond bond) {

        }

        public ILonePair removeLonePair(int position) {
            throw new UnsupportedOperationException("not supported");
        }

        public void removeLonePair(ILonePair lonePair) {

        }


        public ISingleElectron removeSingleElectron(int position) {
            throw new UnsupportedOperationException("not supported");
        }

        public void removeSingleElectron(ISingleElectron singleElectron) {

        }

        public IElectronContainer removeElectronContainer(int position) {
            throw new UnsupportedOperationException("not supported");
        }

        public void removeElectronContainer(IElectronContainer electronContainer) {

        }

        public void removeAtomAndConnectedElectronContainers(IAtom atom) {

        }

        public void removeAllElements() {

        }

        public void removeAllElectronContainers() {

        }

        public void removeAllBonds() {

        }

        public void addBond(int atom1, int atom2, IBond.Order order, IBond.Stereo stereo) {

        }

        public void addBond(int atom1, int atom2, IBond.Order order) {

        }

        public void addLonePair(int atomID) {

        }

        public void addSingleElectron(int atomID) {

        }

        public boolean contains(IAtom atom) {
            return false;
        }

        public boolean contains(IBond bond) {
            return false;
        }

        public boolean contains(ILonePair lonePair) {
            return false;
        }

        public boolean contains(ISingleElectron singleElectron) {
            return false;
        }

        public boolean contains(IElectronContainer electronContainer) {
            return false;
        }

        public boolean isEmpty() {
            return true;
        }

        public IAtomContainer clone() throws CloneNotSupportedException {
            throw new UnsupportedOperationException("not supported");
        }

        public void addListener(IChemObjectListener col) {

        }

        public int getListenerCount() {
            return 0;
        }

        public void removeListener(IChemObjectListener col) {

        }

        public void setNotification(boolean bool) {

        }

        public boolean getNotification() {
            return false;
        }

        public void notifyChanged() {

        }

        public void notifyChanged(IChemObjectChangeEvent evt) {

        }

        public void setProperty(Object description, Object property) {

        }

        public void removeProperty(Object description) {

        }

        public <T> T getProperty(Object description) {
            throw new UnsupportedOperationException("not supported");
        }

        public <T> T getProperty(Object description, Class<T> c) {
            throw new UnsupportedOperationException("not supported");
        }

        public Map<Object, Object> getProperties() {
            return null;
        }

        public String getID() {
            throw new UnsupportedOperationException("not supported");
        }

        public void setID(String identifier) {

        }

        public void setFlag(int mask, boolean value) {

        }

        public boolean getFlag(int mask) {
            throw new UnsupportedOperationException("not supported");
        }

        public void setProperties(Map<Object, Object> properties) {
        }

        public void setFlags(boolean[] newFlags) {
        }

        public boolean[] getFlags() {
            return new boolean[0];
        }

        public Number getFlagValue() {
            return 0;
        }

        public IChemObjectBuilder getBuilder() {
            throw new UnsupportedOperationException("not supported");
        }

        public void stateChanged(IChemObjectChangeEvent event) {

        }
    };

    /**
     * pseudo shortest-paths - when an invalid atom is given. this will always
     * return 0 length paths and distances.
     */
    private static final ShortestPaths EMPTY_SHORTEST_PATHS = new ShortestPaths(new int[0][0], EMPTY_CONTAINER, 0);

}

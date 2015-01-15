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
public final class AllPairsShortestPaths {

    private final IAtomContainer  container;
    private final ShortestPaths[] shortestPaths;

    /**
     * Create a new all shortest paths utility for an {@link IAtomContainer}.
     *
     * @param container the molecule of which to find the shortest paths
     */
    public AllPairsShortestPaths(IAtomContainer container) {

        // toAdjList performs null check
        int[][] adjacent = GraphUtil.toAdjList(container);

        int n = container.getAtomCount();

        this.container = container;
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
    public ShortestPaths from(int start) {
        return (start < 0 || start >= shortestPaths.length) ? EMPTY_SHORTEST_PATHS : shortestPaths[start];
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
    public ShortestPaths from(IAtom start) {
        // currently container.getAtomNumber() return -1 when null
        return from(container.getAtomNumber(start));
    }

    /**
     * an empty atom container so we can handle invalid vertices/atoms better.
     * Not very pretty but we can't access the domain model from cdk-core.
     */
    private static final IAtomContainer EMPTY_CONTAINER      = new IAtomContainer() {

                                                                 @Override
                                                                 public void addStereoElement(IStereoElement element) {}

                                                                 @Override
                                                                 public void setStereoElements(
                                                                         List<IStereoElement> elements) {}

                                                                 @Override
                                                                 public Iterable<IStereoElement> stereoElements() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void setAtoms(IAtom[] atoms) {}

                                                                 @Override
                                                                 public void setBonds(IBond[] bonds) {}

                                                                 @Override
                                                                 public void setAtom(int number, IAtom atom) {}

                                                                 @Override
                                                                 public IAtom getAtom(int number) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IBond getBond(int number) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public ILonePair getLonePair(int number) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public ISingleElectron getSingleElectron(int number) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Iterable<IAtom> atoms() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Iterable<IBond> bonds() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Iterable<ILonePair> lonePairs() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Iterable<ISingleElectron> singleElectrons() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Iterable<IElectronContainer> electronContainers() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IAtom getFirstAtom() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IAtom getLastAtom() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public int getAtomNumber(IAtom atom) {
                                                                     return -1;
                                                                 }

                                                                 @Override
                                                                 public int getBondNumber(IAtom atom1, IAtom atom2) {
                                                                     return -1;
                                                                 }

                                                                 @Override
                                                                 public int getBondNumber(IBond bond) {
                                                                     return -1;
                                                                 }

                                                                 @Override
                                                                 public int getLonePairNumber(ILonePair lonePair) {
                                                                     return -1;
                                                                 }

                                                                 @Override
                                                                 public int getSingleElectronNumber(
                                                                         ISingleElectron singleElectron) {
                                                                     return -1;
                                                                 }

                                                                 @Override
                                                                 public IElectronContainer getElectronContainer(
                                                                         int number) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IBond getBond(IAtom atom1, IAtom atom2) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public int getAtomCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getBondCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getLonePairCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getSingleElectronCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getElectronContainerCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public List<IAtom> getConnectedAtomsList(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public List<IBond> getConnectedBondsList(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public List<ILonePair> getConnectedLonePairsList(
                                                                         IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public List<ISingleElectron> getConnectedSingleElectronsList(
                                                                         IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public List<IElectronContainer> getConnectedElectronContainersList(
                                                                         IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public int getConnectedAtomsCount(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public int getConnectedBondsCount(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public int getConnectedBondsCount(int atomnumber) {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getConnectedLonePairsCount(IAtom atom) {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public int getConnectedSingleElectronsCount(IAtom atom) {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public double getBondOrderSum(IAtom atom) {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public IBond.Order getMaximumBondOrder(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IBond.Order getMinimumBondOrder(IAtom atom) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void add(IAtomContainer atomContainer) {

                                                                 }

                                                                 @Override
                                                                 public void addAtom(IAtom atom) {

                                                                 }

                                                                 @Override
                                                                 public void addBond(IBond bond) {

                                                                 }

                                                                 @Override
                                                                 public void addLonePair(ILonePair lonePair) {

                                                                 }

                                                                 @Override
                                                                 public void addSingleElectron(
                                                                         ISingleElectron singleElectron) {

                                                                 }

                                                                 @Override
                                                                 public void addElectronContainer(
                                                                         IElectronContainer electronContainer) {

                                                                 }

                                                                 @Override
                                                                 public void remove(IAtomContainer atomContainer) {

                                                                 }

                                                                 @Override
                                                                 public void removeAtom(int position) {

                                                                 }

                                                                 @Override
                                                                 public void removeAtom(IAtom atom) {

                                                                 }

                                                                 @Override
                                                                 public IBond removeBond(int position) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public IBond removeBond(IAtom atom1, IAtom atom2) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void removeBond(IBond bond) {

                                                                 }

                                                                 @Override
                                                                 public ILonePair removeLonePair(int position) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void removeLonePair(ILonePair lonePair) {

                                                                 }

                                                                 @Override
                                                                 public ISingleElectron removeSingleElectron(
                                                                         int position) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void removeSingleElectron(
                                                                         ISingleElectron singleElectron) {

                                                                 }

                                                                 @Override
                                                                 public IElectronContainer removeElectronContainer(
                                                                         int position) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void removeElectronContainer(
                                                                         IElectronContainer electronContainer) {

                                                                 }

                                                                 @Override
                                                                 public void removeAtomAndConnectedElectronContainers(
                                                                         IAtom atom) {

                                                                 }

                                                                 @Override
                                                                 public void removeAllElements() {

                                                                 }

                                                                 @Override
                                                                 public void removeAllElectronContainers() {

                                                                 }

                                                                 @Override
                                                                 public void removeAllBonds() {

                                                                 }

                                                                 @Override
                                                                 public void addBond(int atom1, int atom2,
                                                                         IBond.Order order, IBond.Stereo stereo) {

                                                                 }

                                                                 @Override
                                                                 public void addBond(int atom1, int atom2,
                                                                         IBond.Order order) {

                                                                 }

                                                                 @Override
                                                                 public void addLonePair(int atomID) {

                                                                 }

                                                                 @Override
                                                                 public void addSingleElectron(int atomID) {

                                                                 }

                                                                 @Override
                                                                 public boolean contains(IAtom atom) {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public boolean contains(IBond bond) {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public boolean contains(ILonePair lonePair) {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public boolean contains(ISingleElectron singleElectron) {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public boolean contains(
                                                                         IElectronContainer electronContainer) {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public boolean isEmpty() {
                                                                     return true;
                                                                 }

                                                                 @Override
                                                                 public IAtomContainer clone()
                                                                         throws CloneNotSupportedException {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void addListener(IChemObjectListener col) {

                                                                 }

                                                                 @Override
                                                                 public int getListenerCount() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public void removeListener(IChemObjectListener col) {

                                                                 }

                                                                 @Override
                                                                 public void setNotification(boolean bool) {

                                                                 }

                                                                 @Override
                                                                 public boolean getNotification() {
                                                                     return false;
                                                                 }

                                                                 @Override
                                                                 public void notifyChanged() {

                                                                 }

                                                                 @Override
                                                                 public void notifyChanged(IChemObjectChangeEvent evt) {

                                                                 }

                                                                 @Override
                                                                 public void setProperty(Object description,
                                                                         Object property) {

                                                                 }

                                                                 @Override
                                                                 public void removeProperty(Object description) {

                                                                 }

                                                                 @Override
                                                                 public <T> T getProperty(Object description) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public <T> T getProperty(Object description, Class<T> c) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public Map<Object, Object> getProperties() {
                                                                     return null;
                                                                 }

                                                                 @Override
                                                                 public String getID() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void setID(String identifier) {

                                                                 }

                                                                 @Override
                                                                 public void setFlag(int mask, boolean value) {

                                                                 }

                                                                 @Override
                                                                 public boolean getFlag(int mask) {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void setProperties(
                                                                         Map<Object, Object> properties) {}

                                                                 @Override
                                                                 public void addProperties(
                                                                         Map<Object, Object> properties) {}

                                                                 @Override
                                                                 public void setFlags(boolean[] newFlags) {}

                                                                 @Override
                                                                 public boolean[] getFlags() {
                                                                     return new boolean[0];
                                                                 }

                                                                 @Override
                                                                 public Number getFlagValue() {
                                                                     return 0;
                                                                 }

                                                                 @Override
                                                                 public IChemObjectBuilder getBuilder() {
                                                                     throw new UnsupportedOperationException(
                                                                             "not supported");
                                                                 }

                                                                 @Override
                                                                 public void stateChanged(IChemObjectChangeEvent event) {

                                                                 }
                                                             };

    /**
     * pseudo shortest-paths - when an invalid atom is given. this will always
     * return 0 length paths and distances.
     */
    private static final ShortestPaths  EMPTY_SHORTEST_PATHS = new ShortestPaths(new int[0][0], EMPTY_CONTAINER, 0);

}

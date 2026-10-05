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
 * MERCHANTABILITY or FITNESS FOR sourceAtom PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * This class is the handler for
 * {@link org.openscience.cdk.smsd.interfaces.Algorithm#MCSPlus}.
 * <p>
 * It runs the same search as {@link VFlibMCSHandler}, which finds the
 * maximum common substructure mappings (at most
 * {@link org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper#MAX_MAPPINGS}).
 * The clique-based search in {@link MCSPlus} is no longer used, as it can
 * miss some of these mappings.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MCSPlusHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private final VFlibMCSHandler mcs = new VFlibMCSHandler();

    /**
     * Creates a handler that runs the search of {@link VFlibMCSHandler}.
     */
    public MCSPlusHandler() {
    }

    /** {@inheritDoc}
     *
     * @param source source molecule
     * @param target target molecule
     */
    @Override
    public synchronized void set(MolHandler source, MolHandler target) {
        mcs.set(source, target);
    }

    /** {@inheritDoc}
     *
     * @param source source molecule
     * @param target target molecule
     */
    @Override
    public synchronized void set(IQueryAtomContainer source, IAtomContainer target) {
        mcs.set(source, target);
    }

    /** {@inheritDoc}
     * Function is called by the main program and serves as a starting point for the comparison procedure.
     *
     * @param shouldMatchBonds true to match bonds by order and aromaticity
     */
    @Override
    public synchronized void searchMCS(boolean shouldMatchBonds) {
        mcs.searchMCS(shouldMatchBonds);
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return new ArrayList<>(mcs.getAllMapping());
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<Integer, Integer> getFirstMapping() {
        return new TreeMap<>(mcs.getFirstMapping());
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return new ArrayList<>(mcs.getAllAtomMapping());
    }

    /** {@inheritDoc}
     */
    @Override
    public synchronized Map<IAtom, IAtom> getFirstAtomMapping() {
        return new HashMap<>(mcs.getFirstAtomMapping());
    }
}

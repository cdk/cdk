/* Copyright (C) 2010  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.fragment;

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.SpanningTree;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate fragments exhaustively.
 * 
 * This fragmentation scheme simply breaks single non-ring bonds. By default
 * fragments smaller than 6 atoms in size are not considered, but this can be
 * changed by the user. Side chains are retained.
 *
 * @author Rajarshi Guha
 * @cdk.module  fragment
 * @cdk.githash
 * @cdk.keyword fragment
 */
public class ExhaustiveFragmenter implements IFragmenter {

    private static final int    DEFAULT_MIN_FRAG_SIZE = 6;

    Map<String, IAtomContainer> fragMap;
    SmilesGenerator             smilesGenerator;
    String[]                    fragments             = null;
    int                         minFragSize           = 6;
    private static ILoggingTool logger                = LoggingToolFactory
                                                              .createLoggingTool(ExhaustiveFragmenter.class);

    /**
     * Instantiate fragmenter with default minimum fragment size.
     */
    public ExhaustiveFragmenter() {
        this(DEFAULT_MIN_FRAG_SIZE);
    }

    /**
     * Instantiate fragmenter with user specified minimum fragment size.
     *
     * @param minFragSize the minimum fragment size desired
     */
    public ExhaustiveFragmenter(int minFragSize) {
        this.minFragSize = minFragSize;
        fragMap = new HashMap<String, IAtomContainer>();
        smilesGenerator = SmilesGenerator.unique().aromatic();
    }

    /**
     * Set the minimum fragment size.
     *
     * @param minFragSize the smallest size fragment that will be returned
     */
    public void setMinimumFragmentSize(int minFragSize) {
        this.minFragSize = minFragSize;
    }

    /**
     * Generate fragments for the input molecule.
     *
     * @param atomContainer The input molecule.
     */
    @Override
    public void generateFragments(IAtomContainer atomContainer) throws CDKException {
        fragMap.clear();
        run(atomContainer);
    }

    private List<IAtomContainer> run(IAtomContainer atomContainer) throws CDKException {

        ArrayList<IAtomContainer> fragments = new ArrayList<IAtomContainer>();

        if (atomContainer.getBondCount() < 3) return fragments;
        List<IBond> splitableBonds = getSplitableBonds(atomContainer);
        if (splitableBonds.size() == 0) return fragments;
        logger.debug("Got " + splitableBonds.size() + " splittable bonds");

        String tmpSmiles;
        for (IBond bond : splitableBonds) {
            List<IAtomContainer> parts = FragmentUtils.splitMolecule(atomContainer, bond);
            // make sure we don't add the same fragment twice
            for (IAtomContainer partContainer : parts) {
                AtomContainerManipulator.clearAtomConfigurations(partContainer);
                for (IAtom atom : partContainer.atoms())
                    atom.setImplicitHydrogenCount(null);
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(partContainer);
                CDKHydrogenAdder.getInstance(partContainer.getBuilder()).addImplicitHydrogens(partContainer);
                Aromaticity.cdkLegacy().apply(partContainer);
                tmpSmiles = smilesGenerator.create(partContainer);
                if (partContainer.getAtomCount() >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    fragments.add(partContainer);
                    fragMap.put(tmpSmiles, partContainer);
                }
            }
        }

        // try and partition the fragments
        List<IAtomContainer> tmp = new ArrayList<IAtomContainer>(fragments);
        for (IAtomContainer fragment : fragments) {
            if (fragment.getBondCount() < 3 || fragment.getAtomCount() < minFragSize) continue;
            if (getSplitableBonds(fragment).size() == 0) continue;

            List<IAtomContainer> frags = run(fragment);
            if (frags.size() == 0) continue;

            for (IAtomContainer frag : frags) {
                if (frag.getBondCount() < 3) continue;
                AtomContainerManipulator.clearAtomConfigurations(frag);
                for (IAtom atom : frag.atoms())
                    atom.setImplicitHydrogenCount(null);
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(frag);
                CDKHydrogenAdder.getInstance(frag.getBuilder()).addImplicitHydrogens(frag);
                Aromaticity.cdkLegacy().apply(frag);
                tmpSmiles = smilesGenerator.create(frag);
                if (frag.getAtomCount() >= minFragSize && !fragMap.containsKey(tmpSmiles)) {
                    tmp.add(frag);
                    fragMap.put(tmpSmiles, frag);
                }
            }
        }
        fragments = new ArrayList<IAtomContainer>(tmp);
        return fragments;
    }

    private List<IBond> getSplitableBonds(IAtomContainer atomContainer) throws CDKException {
        // do ring detection
        SpanningTree spanningTree = new SpanningTree(atomContainer);
        IRingSet allRings = spanningTree.getAllRings();

        // find the splitable bonds
        ArrayList<IBond> splitableBonds = new ArrayList<IBond>();

        for (IBond bond : atomContainer.bonds()) {
            boolean isInRing = false;
            boolean isTerminal = false;

            // lets see if it's in a ring
            IRingSet rings = allRings.getRings(bond);
            if (rings.getAtomContainerCount() != 0) isInRing = true;

            // lets see if it is a terminal bond
            for (IAtom atom : bond.atoms()) {
                if (atomContainer.getConnectedBondsCount(atom) == 1) {
                    isTerminal = true;
                    break;
                }
            }

            if (!(isInRing || isTerminal)) splitableBonds.add(bond);
        }
        return splitableBonds;
    }

    /**
     * Get the fragments generated as SMILES strings.
     *
     * @return a String[] of the fragments.
     */
    @Override
    public String[] getFragments() {
        return (new ArrayList<String>(fragMap.keySet())).toArray(new String[0]);
    }

    /**
     * Get the fragments generated as {@link IAtomContainer} objects..
     *
     * @return a IAtomContainer[] of the fragments.
     */
    @Override
    public IAtomContainer[] getFragmentsAsContainers() {
        return (new ArrayList<IAtomContainer>(fragMap.values())).toArray(new IAtomContainer[0]);
    }

}

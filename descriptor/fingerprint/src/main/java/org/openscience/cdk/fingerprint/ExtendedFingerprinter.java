/* Copyright (C) 2002-2007,2020  Stefan Kuhn <shk3@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import org.openscience.cdk.CDK;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import java.util.BitSet;
import java.util.List;
import java.util.Map;

/**
 * Generates an extended fingerprint for a given {@link IAtomContainer}, that
 * extends the {@link Fingerprinter} with additional (25) bits describing ring
 * features and isotopic masses.
 *
 * <i>JWM Comment: It's better to actually just hash the rings over the entire
 * length simply using a different seed.
 * The original version of the class used non-unique SSSR which of course
 * doesn't work for substructure screening so this fingerprint can only
 * be used for similarity.</i>
 *
 * @author shk3
 * @cdk.created 2006-01-13
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 * @see org.openscience.cdk.fingerprint.Fingerprinter
 */
public class ExtendedFingerprinter implements IFingerprinter {

    // number of bits to hash rings into
    private final int RESERVED_BITS = 25;

    private final Fingerprinter fingerprinter;

    /**
     * Creates a fingerprint generator of length <code>DEFAULT_SIZE</code>
     * and with a search depth of <code>DEFAULT_SEARCH_DEPTH</code>.
     */
    public ExtendedFingerprinter() {
        this(Fingerprinter.DEFAULT_SIZE, Fingerprinter.DEFAULT_SEARCH_DEPTH);
    }

    public ExtendedFingerprinter(int size) {
        this(size, Fingerprinter.DEFAULT_SEARCH_DEPTH);
    }

    /**
     * Constructs a fingerprint generator that creates fingerprints of
     * the given size, using a generation algorithm with the given search
     * depth.
     *
     * @param size        The desired size of the fingerprint
     * @param searchDepth The desired depth of search
     */
    public ExtendedFingerprinter(int size, int searchDepth) {
        this.fingerprinter = new Fingerprinter(size - RESERVED_BITS, searchDepth);
    }

    /**
     * Generates a fingerprint of the default size for the given
     * AtomContainer, using path and ring metrics. It contains the
     * informations from getBitFingerprint() and bits which tell if the structure
     * has 0 rings, 1 or less rings, 2 or less rings ... 10 or less rings
     * (referring to smallest set of smallest rings) and bits which tell if
     * there is a fused ring system with 1,2...8 or more rings in it
     *
     * @param container The AtomContainer for which a Fingerprint is generated
     * @return a bit fingerprint for the given <code>IAtomContainer</code>.
     */
    @Override
    public IBitFingerprint getBitFingerprint(IAtomContainer container) throws CDKException {
        return this.getBitFingerprint(container, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getRawFingerprint(IAtomContainer iAtomContainer) throws CDKException {
        throw new UnsupportedOperationException();
    }

    /**
     * Generates a fingerprint of the default size for the given
     * AtomContainer, using path and ring metrics. It contains the
     * informations from getBitFingerprint() and bits which tell if the structure
     * has 0 rings, 1 or less rings, 2 or less rings ... 10 or less rings and
     * bits which tell if there is a fused ring system with 1,2...8 or more
     * rings in it. The RingSet used is passed via rs parameter. This must be
     * a smallesSetOfSmallestRings. The List must be a list of all ring
     * systems in the molecule.
     *
     * @param atomContainer The AtomContainer for which a Fingerprint is
     *                      generated
     * @param ringSet       A SSSR RingSet of ac (if not available, use
     *                      getExtendedFingerprint(AtomContainer ac),
     *                      which does the calculation)
     * @param rslist        A list of all ring systems in ac
     * @return a BitSet representing the fingerprint
     * @throws CDKException for example if input can not be cloned.
     */
    public IBitFingerprint getBitFingerprint(IAtomContainer atomContainer, IRingSet ringSet, List<IRingSet> rslist)
            throws CDKException {
        IAtomContainer container;
        try {
            container = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            throw new CDKException("Could not clone input");
        }

        IBitFingerprint fingerprint = fingerprinter.getBitFingerprint(container);
        int size = this.getSize();
        double weight = MolecularFormulaManipulator.getTotalNaturalAbundance(MolecularFormulaManipulator
                .getMolecularFormula(container));
        for (int i = 1; i < 11; i++) {
            if (weight > (100 * i)) fingerprint.set(size - 26 + i); // 26 := RESERVED_BITS+1
        }
        if (ringSet == null) {
            ringSet = Cycles.sssr(container).toRingSet();
            rslist = RingPartitioner.partitionRings(ringSet);
        }
        for (int i = 0; i < 7; i++) {
            if (ringSet.getAtomContainerCount() > i) fingerprint.set(size - 15 + i); // 15 := RESERVED_BITS+1+10 mass bits
        }
        int maximumringsystemsize = 0;
        for (int i = 0; i < rslist.size(); i++) {
            if (((IRingSet) rslist.get(i)).getAtomContainerCount() > maximumringsystemsize)

                maximumringsystemsize = ((IRingSet) rslist.get(i)).getAtomContainerCount();
        }
        for (int i = 0; i < maximumringsystemsize && i < 9; i++) {
            fingerprint.set(size - 8 + i - 3);
        }
        return fingerprint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return fingerprinter.getSize() + RESERVED_BITS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICountFingerprint getCountFingerprint(IAtomContainer container) throws CDKException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersionDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("CDK-")
                .append(getClass().getSimpleName())
                .append("/")
                .append(CDK.getVersion()); // could version fingerprints separately
        for (Map.Entry<String, String> param : this.fingerprinter.getParameters()) {
            sb.append(' ').append(param.getKey()).append('=').append(param.getValue());
        }
        return sb.toString();
    }

    @Override
    public BitSet getFingerprint(IAtomContainer mol) throws CDKException {
        return getBitFingerprint(mol).asBitSet();
    }

    /**
     * Set the pathLimit for the base daylight/path fingerprint. If too many paths are generated from a single atom
     * an exception is thrown.
     * @param pathLimit the number of paths to generate from a node
     * @see Fingerprinter
     */
    public void setPathLimit(int pathLimit) {
        this.fingerprinter.setPathLimit(pathLimit);
    }

    /**
     * Set the hashPseudoAtoms for the base daylight/path fingerprint. This indicates whether pseudo-atoms should be
     * hashed, for substructure screening this is not desirable - but this fingerprint uses SSSR so can't be used for
     * substructure screening regardless.
     * @param hashPseudoAtoms the number of paths to generate from a node
     * @see Fingerprinter
     */
    public void setHashPseudoAtoms(boolean hashPseudoAtoms) {
        this.fingerprinter.setHashPseudoAtoms(hashPseudoAtoms);
    }
}

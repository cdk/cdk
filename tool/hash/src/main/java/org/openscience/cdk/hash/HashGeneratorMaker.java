/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash;

import org.openscience.cdk.hash.stereo.DoubleBondElementEncoderFactory;
import org.openscience.cdk.hash.stereo.StereoEncoder;
import org.openscience.cdk.hash.stereo.GeometricCumulativeDoubleBondFactory;
import org.openscience.cdk.hash.stereo.GeometricDoubleBondEncoderFactory;
import org.openscience.cdk.hash.stereo.GeometricTetrahedralEncoderFactory;
import org.openscience.cdk.hash.stereo.StereoEncoderFactory;
import org.openscience.cdk.hash.stereo.TetrahedralElementEncoderFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Fluent API for creating hash generators. The maker is first configured with
 * one or more attributes. Once fully configured the generator is made by
 * invoking {@link #atomic()}, {@link #molecular()} or {@link #ensemble()}. The
 * order of the built-in configuration methods does not matter however when
 * specifying custom encoders with {@link #encode(AtomEncoder)} the order they
 * are added is the order they will be used. Therefore one can expect different
 * hash codes if there is a change in the order they are specified.
 *
 * <br>
 * <b>Examples</b>
 * <blockquote><pre>
 * // simple
 * MoleculeHashGenerator generator = new HashGeneratorMaker().depth(16)
 *                                                           .elemental()
 *                                                           .molecular();
 *
 * // fast
 * MoleculeHashGenerator generator = new HashGeneratorMaker().depth(8)
 *                                                           .elemental()
 *                                                           .isotopic()
 *                                                           .charged()
 *                                                           .orbital()
 *                                                           .molecular();
 * // comprehensive
 * MoleculeHashGenerator generator = new HashGeneratorMaker().depth(32)
 *                                                           .elemental()
 *                                                           .isotopic()
 *                                                           .charged()
 *                                                           .chiral()
 *                                                           .perturbed()
 *                                                           .molecular();
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module hash
 * @cdk.githash
 */
public final class HashGeneratorMaker {

    /* no default depth */
    private int                        depth          = -1;

    /* ordered list of custom encoders */
    private List<AtomEncoder>          customEncoders = new ArrayList<AtomEncoder>();

    /* ordered set of basic encoders */
    private EnumSet<BasicAtomEncoder>  encoderSet     = EnumSet.noneOf(BasicAtomEncoder.class);

    /* list of stereo encoders */
    private List<StereoEncoderFactory> stereoEncoders = new ArrayList<StereoEncoderFactory>();

    /* whether we want to use perturbed hash generators */
    private EquivalentSetFinder        equivSetFinder = null;

    /* function determines whether any atoms are suppressed */
    private AtomSuppression            suppression    = AtomSuppression.unsuppressed();

    /**
     * Specify the depth of the hash generator. Larger values discriminate more
     * molecules.
     *
     * @param depth how deep should the generator hash
     * @return reference for fluent API
     * @throws IllegalArgumentException if the depth was less then zero
     */
    public HashGeneratorMaker depth(int depth) {
        if (depth < 0) throw new IllegalArgumentException("depth must not be less than 0");
        this.depth = depth;
        return this;
    }

    /**
     * Discriminate elements.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#ATOMIC_NUMBER
     */
    public HashGeneratorMaker elemental() {
        encoderSet.add(BasicAtomEncoder.ATOMIC_NUMBER);
        return this;
    }

    /**
     * Discriminate isotopes.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#MASS_NUMBER
     */
    public HashGeneratorMaker isotopic() {
        encoderSet.add(BasicAtomEncoder.MASS_NUMBER);
        return this;
    }

    /**
     * Discriminate protonation states.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#FORMAL_CHARGE
     */
    public HashGeneratorMaker charged() {
        encoderSet.add(BasicAtomEncoder.FORMAL_CHARGE);
        return this;
    }

    /**
     * Discriminate atomic orbitals.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#ORBITAL_HYBRIDIZATION
     */
    public HashGeneratorMaker orbital() {
        encoderSet.add(BasicAtomEncoder.ORBITAL_HYBRIDIZATION);
        return this;
    }

    /**
     * Discriminate free radicals.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#FREE_RADICALS
     */
    public HashGeneratorMaker radical() {
        encoderSet.add(BasicAtomEncoder.FREE_RADICALS);
        return this;
    }

    /**
     * Generate different hash codes for stereoisomers. The currently supported
     * geometries are:
     *
     * <ul>
     *     <li>Tetrahedral</li>
     *     <li>Double Bond</li>
     *     <li>Cumulative Double Bonds</li>
     * </ul>
     *
     * @return fluent API reference (self)
     */
    public HashGeneratorMaker chiral() {
        this.stereoEncoders.add(new GeometricTetrahedralEncoderFactory());
        this.stereoEncoders.add(new GeometricDoubleBondEncoderFactory());
        this.stereoEncoders.add(new GeometricCumulativeDoubleBondFactory());
        this.stereoEncoders.add(new TetrahedralElementEncoderFactory());
        this.stereoEncoders.add(new DoubleBondElementEncoderFactory());
        return this;
    }

    /**
     * Suppress any explicit hydrogens in the encoding of hash values. The
     * generation of hashes acts as though the hydrogens are not present and as
     * such preserves stereo-encoding.
     *
     * @return fluent API reference (self)
     */
    public HashGeneratorMaker suppressHydrogens() {
        this.suppression = AtomSuppression.anyHydrogens();
        return this;
    }

    /**
     * Discriminate atoms experiencing uniform environments. This method uses
     * {@link MinimumEquivalentCyclicSet}  to break symmetry but depending on
     * application one may need a more comprehensive method. Please refer to
     * {@link #perturbWith(EquivalentSetFinder)} for further configuration
     * details.
     *
     * @return fluent API reference (self)
     * @see MinimumEquivalentCyclicSet
     * @see #perturbWith(EquivalentSetFinder)
     */
    public HashGeneratorMaker perturbed() {
        return perturbWith(new MinimumEquivalentCyclicSet());
    }

    /**
     * Discriminate atoms experiencing uniform environments using the provided
     * method. Depending on the level of identity required one can choose how
     * the atoms a perturbed in an attempt to break symmetry.  As with all
     * hashing there is always a probability of collision but some of these
     * collisions may be due to an insufficiency in the algorithm opposed to a
     * random chance of collision. Currently there are three strategies but one
     * should choose either to use the fast, but good, heuristic {@link
     * MinimumEquivalentCyclicSet} or the exact {@link AllEquivalentCyclicSet}.
     * In practice {@link MinimumEquivalentCyclicSet} is good enough for most
     * applications but it is important to understand the potential trade off.
     * The {@link MinimumEquivalentCyclicSetUnion} is provided for demonstration
     * only, and as such, is deprecated.
     *
     * <ul> <li>MinimumEquivalentCyclicSet - fastest, attempt to break symmetry
     * by changing a single smallest set of the equivalent atoms which occur in
     * a ring</li> <li><strike>MinimumEquivalentCyclicSetUnion</strike>
     * (deprecated) - distinguishes more molecules by changing all smallest sets
     * of the equivalent atoms which occur in a ring. This method is provided
     * from example only</li> <li>AllEquivalentCyclicSet - slowest,
     * systematically perturb all equivalent atoms that occur in a ring</li>
     * </ul>
     *
     * At the time of writing (Feb, 2013) the number of known false possibles
     * found in PubChem-Compound (aprx. 46,000,000 structures) are as follows:
     *
     * <ul> <li>MinimumEquivalentCyclicSet - 128 molecules, 64 false positives
     * (128/2)</li> <li>MinimumEquivalentCyclicSetUnion - 8 molecules, 4 false
     * positives (8/2)</li> <li>AllEquivalentCyclicSet - 0 molecules</li> </ul>
     *
     * @param equivSetFinder equivalent set finder, used to determine which
     *                       atoms will be perturbed to try and break symmetry.
     * @return fluent API reference (self)
     * @see AllEquivalentCyclicSet
     * @see MinimumEquivalentCyclicSet
     * @see MinimumEquivalentCyclicSetUnion
     */
    HashGeneratorMaker perturbWith(EquivalentSetFinder equivSetFinder) {
        this.equivSetFinder = equivSetFinder;
        return this;
    }

    /**
     * Add a custom encoder to the hash generator which will be built. Although
     * not enforced, the encoder should be stateless and should not modify any
     * passed inputs.
     *
     * @param encoder an atom encoder
     * @return fluent API reference (self)
     * @throws NullPointerException no encoder provided
     */
    public HashGeneratorMaker encode(AtomEncoder encoder) {
        if (encoder == null) throw new NullPointerException("no encoder provided");
        customEncoders.add(encoder);
        return this;
    }

    /**
     * Combines the separate stereo encoder factories into a single factory.
     *
     * @return a single stereo encoder factory
     */
    private StereoEncoderFactory makeStereoEncoderFactory() {
        if (stereoEncoders.isEmpty()) {
            return StereoEncoderFactory.EMPTY;
        } else if (stereoEncoders.size() == 1) {
            return stereoEncoders.get(0);
        } else {
            StereoEncoderFactory factory = new ConjugatedEncoderFactory(stereoEncoders.get(0), stereoEncoders.get(1));
            for (int i = 2; i < stereoEncoders.size(); i++) {
                factory = new ConjugatedEncoderFactory(factory, stereoEncoders.get(i));
            }
            return factory;
        }
    }

    /**
     * Given the current configuration create an {@link EnsembleHashGenerator}.
     *
     * @return instance of the generator
     * @throws IllegalArgumentException no depth or encoders were configured
     */
    public EnsembleHashGenerator ensemble() {
        throw new UnsupportedOperationException("not yet supported");
    }

    /**
     * Given the current configuration create an {@link MoleculeHashGenerator}.
     *
     * @return instance of the generator
     * @throws IllegalArgumentException no depth or encoders were configured
     */
    public MoleculeHashGenerator molecular() {
        return new BasicMoleculeHashGenerator(atomic());
    }

    /**
     * Given the current configuration create an {@link AtomHashGenerator}.
     *
     * @return instance of the generator
     * @throws IllegalArgumentException no depth or encoders were configured
     */
    public AtomHashGenerator atomic() {

        if (depth < 0) throw new IllegalArgumentException("no depth specified, use .depth(int)");

        List<AtomEncoder> encoders = new ArrayList<AtomEncoder>();

        // set is ordered
        for (AtomEncoder encoder : encoderSet) {
            encoders.add(encoder);
        }
        encoders.addAll(this.customEncoders);

        // check if suppression of atoms is wanted - if not use a default value
        // we also use the 'Basic' generator (see below)
        boolean suppress = suppression != AtomSuppression.unsuppressed();

        AtomEncoder encoder = new ConjugatedAtomEncoder(encoders);
        SeedGenerator seeds = new SeedGenerator(encoder, suppression);

        AbstractAtomHashGenerator simple = suppress ? new SuppressedAtomHashGenerator(seeds, new Xorshift(),
                makeStereoEncoderFactory(), suppression, depth) : new BasicAtomHashGenerator(seeds, new Xorshift(),
                makeStereoEncoderFactory(), depth);

        // if there is a finder for checking equivalent vertices then the user
        // wants to 'perturb' the hashed
        if (equivSetFinder != null) {
            return new PerturbedAtomHashGenerator(seeds, simple, new Xorshift(), makeStereoEncoderFactory(),
                    equivSetFinder, suppression);
        } else {
            // no equivalence set finder - just use the simple hash
            return simple;
        }
    }

    /**
     * Help class to combined two stereo encoder factories
     */
    private final class ConjugatedEncoderFactory implements StereoEncoderFactory {

        private final StereoEncoderFactory left, right;

        /**
         * Create a new conjugated encoder factory from the left and right
         * factories.
         *
         * @param left  encoder factory
         * @param right encoder factory
         */
        private ConjugatedEncoderFactory(StereoEncoderFactory left, StereoEncoderFactory right) {
            this.left = left;
            this.right = right;
        }

        /**
         *{@inheritDoc}
         */
        @Override
        public StereoEncoder create(IAtomContainer container, int[][] graph) {
            return new ConjugatedEncoder(left.create(container, graph), right.create(container, graph));
        }
    }

    /**
     * Help class to combined two stereo encoders
     */
    private final class ConjugatedEncoder implements StereoEncoder {

        private final StereoEncoder left, right;

        /**
         * Create a new conjugated encoder from a left and right encoder.
         *
         * @param left  encoder
         * @param right encoder
         */
        private ConjugatedEncoder(StereoEncoder left, StereoEncoder right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Encodes using the left and then the right encoder.
         *
         * @param current current invariants
         * @param next    next invariants
         * @return whether either encoder modified any values
         */
        @Override
        public boolean encode(long[] current, long[] next) {
            boolean modified = left.encode(current, next);
            return right.encode(current, next) || modified;
        }

        /**
         * reset the left and right encoders
         */
        @Override
        public void reset() {
            left.reset();
            right.reset();
        }
    }

}

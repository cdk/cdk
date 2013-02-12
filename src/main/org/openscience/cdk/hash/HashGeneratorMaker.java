package org.openscience.cdk.hash;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.hash.seed.AtomEncoder;
import org.openscience.cdk.hash.seed.BasicAtomEncoder;
import org.openscience.cdk.hash.seed.ConjugatedAtomEncoder;
import org.openscience.cdk.hash.stereo.StereoEncoder;
import org.openscience.cdk.hash.stereo.factory.GeometricDoubleBondEncoderFactory;
import org.openscience.cdk.hash.stereo.factory.GeometricTetrahedralEncoderFactory;
import org.openscience.cdk.hash.stereo.factory.StereoEncoderFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.openscience.cdk.hash.seed.BasicAtomEncoder.ATOMIC_NUMBER;
import static org.openscience.cdk.hash.seed.BasicAtomEncoder.FORMAL_CHARGE;
import static org.openscience.cdk.hash.seed.BasicAtomEncoder.FREE_RADICALS;
import static org.openscience.cdk.hash.seed.BasicAtomEncoder.MASS_NUMBER;
import static org.openscience.cdk.hash.seed.BasicAtomEncoder.ORBITAL_HYBRIDIZATION;

/**
 * Fluent API for creating hash generators. The maker is first configured with
 * one or more attributes. Once fully configured the generator is made by
 * invoking {@link #atomic()}, {@link #molecular()} or {@link #ensemble()}. The
 * order of the built-in configuration methods does not matter however when
 * specifying custom encoders with {@link #encode(AtomEncoder)} the order they
 * are added is the order they will be used. Therefore one can expect different
 * hash codes if there is a change in the order they are specified.
 *
 * <h4>Examples</h4>
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
 */
@TestClass("org.openscience.cdk.hash.HashGeneratorMakerTest")
public class HashGeneratorMaker {

    /* no default depth */
    private int depth = -1;

    /* ordered list of custom encoders */
    private List<AtomEncoder> customEncoders = new ArrayList<AtomEncoder>();

    /* ordered set of basic encoders */
    private EnumSet<BasicAtomEncoder> encoderSet = EnumSet
            .noneOf(BasicAtomEncoder.class);

    /* list of stereo encoders */
    private List<StereoEncoderFactory> stereoEncoders = new ArrayList<StereoEncoderFactory>();

    /**
     * Specify the depth of the hash generator. Larger values discriminate more
     * molecules.
     *
     * @param depth how deep should the generator hash
     * @return reference for fluent API
     * @throws IllegalArgumentException if the depth was less then zero
     */
    @TestMethod("testInvalidDepth,testDepth")
    public HashGeneratorMaker depth(int depth) {
        if (depth < 0)
            throw new IllegalArgumentException("depth must not be less than 0");
        this.depth = depth;
        return this;
    }

    /**
     * Discriminate elements.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#ATOMIC_NUMBER
     */
    @TestMethod("testElemental")
    public HashGeneratorMaker elemental() {
        encoderSet.add(ATOMIC_NUMBER);
        return this;
    }

    /**
     * Discriminate isotopes.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#MASS_NUMBER
     */
    @TestMethod("testIsotopic")
    public HashGeneratorMaker isotopic() {
        encoderSet.add(MASS_NUMBER);
        return this;
    }

    /**
     * Discriminate protonation states.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#FORMAL_CHARGE
     */
    @TestMethod("testCharged")
    public HashGeneratorMaker charged() {
        encoderSet.add(FORMAL_CHARGE);
        return this;
    }

    /**
     * Discriminate atomic orbitals.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#ORBITAL_HYBRIDIZATION
     */
    @TestMethod("testOrbital")
    public HashGeneratorMaker orbital() {
        encoderSet.add(ORBITAL_HYBRIDIZATION);
        return this;
    }

    /**
     * Discriminate free radicals.
     *
     * @return fluent API reference (self)
     * @see BasicAtomEncoder#FREE_RADICALS
     */
    @TestMethod("testRadical")
    public HashGeneratorMaker radical() {
        encoderSet.add(FREE_RADICALS);
        return this;
    }

    /**
     * Discriminate chiral centers.
     *
     * @return fluent API reference (self)
     * @throws UnsupportedOperationException not yet implemented
     */
    @TestMethod("testChiral")
    public HashGeneratorMaker chiral() {
        this.stereoEncoders.add(new GeometricTetrahedralEncoderFactory());
        this.stereoEncoders.add(new GeometricDoubleBondEncoderFactory());
        return this;
    }

    /**
     * Discriminate symmetrical atoms experiencing uniform environments.
     *
     * @return fluent API reference (self)
     * @throws UnsupportedOperationException not yet implemented
     */
    @TestMethod("testPerturbed")
    public HashGeneratorMaker perturbed() {
        throw new UnsupportedOperationException("not yet supported");
    }

    /**
     * Add a custom encoder to the hash generator which will be built. Although
     * not enforced, the encoder should be stateless. A message to standard
     * error is printed if the encoder has any fields.
     *
     * @param encoder an atom encoder
     * @return fluent API reference (self)
     * @throws NullPointerException no encoder provided
     */
    @TestMethod("testEncode_Null,testEncode")
    public HashGeneratorMaker encode(AtomEncoder encoder) {
        if (encoder == null)
            throw new NullPointerException("no encoder provided");
        if (encoder.getClass().getDeclaredFields().length > 0)
            System.err
                  .println("AtomEncoder had fields but should be stateless");
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
            StereoEncoderFactory factory = new ConjugatedEncoderFactory(stereoEncoders
                                                                                .get(0),
                                                                        stereoEncoders
                                                                                .get(1));
            for (int i = 2; i < stereoEncoders.size(); i++) {
                factory = new ConjugatedEncoderFactory(factory, stereoEncoders
                        .get(i));
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
    @TestMethod("testEnsemble")
    public EnsembleHashGenerator ensemble() {
        throw new UnsupportedOperationException("not yet supported");
    }


    /**
     * Given the current configuration create an {@link MoleculeHashGenerator}.
     *
     * @return instance of the generator
     * @throws IllegalArgumentException no depth or encoders were configured
     */
    @TestMethod("testMolecular")
    public MoleculeHashGenerator molecular() {
        return new BasicMoleculeHashGenerator(atomic());
    }

    /**
     * Given the current configuration create an {@link AtomHashGenerator}.
     *
     * @return instance of the generator
     * @throws IllegalArgumentException no depth or encoders were configured
     */
    @TestMethod("testAtomic,testNoDepth")
    public AtomHashGenerator atomic() {

        if (depth < 0)
            throw new IllegalArgumentException("no depth specified, use .depth(int)");

        List<AtomEncoder> encoders = new ArrayList<AtomEncoder>();

        // set is ordered
        for (AtomEncoder encoder : encoderSet) {
            encoders.add(encoder);
        }
        encoders.addAll(this.customEncoders);

        AtomEncoder encoder = new ConjugatedAtomEncoder(encoders);

        return new BasicAtomHashGenerator(new SeedGenerator(encoder),
                                          new Xorshift(),
                                          makeStereoEncoderFactory(),
                                          depth);
    }

    /**
     * Help class to combined two stereo encoder factories
     */
    private final class ConjugatedEncoderFactory
            implements StereoEncoderFactory {
        private final StereoEncoderFactory left, right;

        /**
         * Create a new conjugated encoder factory from the left and right
         * factories.
         * @param left encoder factory
         * @param right encoder factory
         */
        private ConjugatedEncoderFactory(StereoEncoderFactory left, StereoEncoderFactory right) {
            this.left = left;
            this.right = right;
        }

        /**
         * @inheritDoc
         */
        @Override
        public StereoEncoder create(IAtomContainer container, int[][] graph) {
            return new ConjugatedEncoder(left.create(container, graph), right
                    .create(container, graph));
        }
    }

    /**
     * Help class to combined two stereo encoders
     */
    private final class ConjugatedEncoder implements StereoEncoder {

        private final StereoEncoder left, right;

        /**
         * Create a new conjugated encoder from a left and right encoder.
         * @param left encoder
         * @param right encoder
         */
        private ConjugatedEncoder(StereoEncoder left, StereoEncoder right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Encodes using the left and then the right encoder.
         * @param current current invariants
         * @param next    next invariants
         * @return whether either encoder modified any values
         */
        @Override public boolean encode(long[] current, long[] next) {
            boolean modified = left.encode(current, next);
            return right.encode(current, next) || modified;
        }

        /**
         * reset the left and right encoders
         */
        @Override public void reset() {
            left.reset();
            right.reset();
        }
    }

}

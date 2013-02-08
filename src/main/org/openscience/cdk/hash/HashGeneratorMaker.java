package org.openscience.cdk.hash;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.hash.seed.AtomEncoder;
import org.openscience.cdk.hash.seed.BasicAtomEncoder;
import org.openscience.cdk.hash.seed.ConjugatedAtomEncoder;

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
        throw new UnsupportedOperationException("not yet supported");
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
                                          depth);
    }

}

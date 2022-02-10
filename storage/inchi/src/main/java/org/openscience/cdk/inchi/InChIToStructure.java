/* Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.inchi;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondStereo;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiInput;
import io.github.dan2097.jnainchi.InchiInputFromInchiOutput;
import io.github.dan2097.jnainchi.InchiOptions;
import io.github.dan2097.jnainchi.InchiStatus;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnainchi.InchiStereoType;
import io.github.dan2097.jnainchi.JnaInchi;
import net.sf.jniinchi.INCHI_RET;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedCisTrans;
import org.openscience.cdk.stereo.ExtendedTetrahedral;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This class generates a CDK IAtomContainer from an InChI string.  It places
 * calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>The generated IAtomContainer will have all 2D and 3D coordinates set to 0.0,
 * but may have atom parities set.  Double bond and allene stereochemistry are
 * not currently recorded.
 *
 * <br>
 * <b>Example usage</b>
 *
 * <code>// Generate factory - throws CDKException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIToStructure</code><br>
 * <code>InChIToStructure intostruct = factory.getInChIToStructure(</code><br>
 * <code>  inchi, DefaultChemObjectBuilder.getInstance()</code><br>
 * <code>);</code><br>
 * <code></code><br>
 * <code>INCHI_RET ret = intostruct.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // Structure generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + intostruct.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // Structure generation failed</code><br>
 * <code>  throw new CDKException("Structure generation failed failed: " + ret.toString()</code><br>
 * <code>    + " [" + intostruct.getMessage() + "]");</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>IAtomContainer container = intostruct.getAtomContainer();</code><br>
 * <p><br>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIToStructure {

    protected InchiInputFromInchiOutput output;

    protected InchiOptions options;

    protected IAtomContainer          molecule;

    // magic number - indicates isotope mass is relative
    private static final int          ISOTOPIC_SHIFT_FLAG = 10000;
    /**
     * JNI-Inchi uses the magic number {#link ISOTOPIC_SHIFT_FLAG} plus the
     * (possibly negative) relative mass. So any isotope value
     * coming back from jni-inchi greater than this threshold value
     * should be treated as a relative mass.
     */
    private static final int ISOTOPIC_SHIFT_THRESHOLD = ISOTOPIC_SHIFT_FLAG - 100;

    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, InchiOptions options) throws CDKException {
        if (inchi == null)
            throw new IllegalArgumentException("Null InChI string provided");
        if (options == null)
            throw new IllegalArgumentException("Null options provided");
        this.output = JnaInchi.getInchiInputFromInchi(inchi);
        this.options = options;
        generateAtomContainerFromInchi(builder);
    }

    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder) throws CDKException {
        this(inchi, builder, new InchiOptions.InchiOptionsBuilder().build());
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, String options) throws CDKException {
        this(inchi, builder, InChIOptionParser.parseString(options));
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, List<String> options) throws CDKException {
        this(inchi, builder, InChIOptionParser.parseStrings(options));
    }

    /**
     * Flip the storage order of atoms in a bond.
     * @param bond the bond
     */
    private void flip(IBond bond) {
        bond.setAtoms(new IAtom[]{bond.getEnd(), bond.getBegin()});
    }

    /**
     * Gets structure from InChI, and converts InChI library data structure
     * into an IAtomContainer.
     *
     * @throws CDKException
     */
    protected void generateAtomContainerFromInchi(IChemObjectBuilder builder) throws CDKException {

        InchiInput input = output.getInchiInput();

        //molecule = new AtomContainer();
        molecule = builder.newInstance(IAtomContainer.class);

        Map<InchiAtom, IAtom> inchiCdkAtomMap = new HashMap<>();

        List<InchiAtom> atoms = input.getAtoms();
        for (int i = 0; i < atoms.size(); i++) {
            InchiAtom iAt = atoms.get(i);
            IAtom cAt = builder.newInstance(IAtom.class);

            inchiCdkAtomMap.put(iAt, cAt);

            cAt.setID("a" + i);
            cAt.setAtomicNumber(Elements.ofString(iAt.getElName()).number());

            // Ignore coordinates - all zero - unless aux info was given... but
            // the CDK doesn't have an API to provide that

            // InChI does not have unset properties so we set charge,
            // hydrogen count (implicit) and isotopic mass
            cAt.setFormalCharge(iAt.getCharge());
            cAt.setImplicitHydrogenCount(iAt.getImplicitHydrogen());
            int isotopicMass = iAt.getIsotopicMass();

            if (isotopicMass != 0) {
                if (isotopicMass > ISOTOPIC_SHIFT_THRESHOLD) {
                    try {
                        int massNumber = Isotopes.getInstance().getMajorIsotope(cAt.getAtomicNumber()).getMassNumber();
                        cAt.setMassNumber(massNumber + (isotopicMass - ISOTOPIC_SHIFT_FLAG));
                    } catch (IOException e) {
                        throw new CDKException("Could not load Isotopes data", e);
                    }
                } else {
                    cAt.setMassNumber(isotopicMass);
                }
            }

            molecule.addAtom(cAt);
            cAt = molecule.getAtom(molecule.getAtomCount()-1);
            addHydrogenIsotopes(builder, cAt, 2, iAt.getImplicitDeuterium());
            addHydrogenIsotopes(builder, cAt, 3, iAt.getImplicitTritium());
        }

        List<InchiBond> bonds = input.getBonds();
        for (int i = 0; i < bonds.size(); i++) {
            InchiBond iBo = bonds.get(i);
            IBond cBo = builder.newInstance(IBond.class);

            IAtom atO = inchiCdkAtomMap.get(iBo.getStart());
            IAtom atT = inchiCdkAtomMap.get(iBo.getEnd());
            cBo.setAtoms(new IAtom[]{atO, atT});

            InchiBondType type = iBo.getType();
            switch (type) {
                case SINGLE:
                    cBo.setOrder(IBond.Order.SINGLE);
                    break;
                case DOUBLE:
                    cBo.setOrder(IBond.Order.DOUBLE);
                    break;
                case TRIPLE:
                    cBo.setOrder(IBond.Order.TRIPLE);
                    break;
                case ALTERN:
                    cBo.setIsInRing(true);
                    break;
                default:
                    throw new CDKException("Unknown bond type: " + type);
            }

            InchiBondStereo stereo = iBo.getStereo();

            switch (stereo) {
                case NONE:
                    cBo.setStereo(IBond.Stereo.NONE);
                    break;
                case SINGLE_1DOWN:
                    cBo.setStereo(IBond.Stereo.DOWN);
                    break;
                case SINGLE_1UP:
                    cBo.setStereo(IBond.Stereo.UP);
                    break;
                case SINGLE_2DOWN:
                    cBo.setStereo(IBond.Stereo.DOWN_INVERTED);
                    break;
                case SINGLE_2UP:
                    cBo.setStereo(IBond.Stereo.UP_INVERTED);
                    break;
                case SINGLE_1EITHER:
                    cBo.setStereo(IBond.Stereo.UP_OR_DOWN);
                    break;
                case SINGLE_2EITHER:
                    cBo.setStereo(IBond.Stereo.UP_OR_DOWN_INVERTED);
                    break;
            }

            molecule.addBond(cBo);
        }

        List<InchiStereo> stereos = input.getStereos();
        for (int i = 0; i < stereos.size(); i++) {
            InchiStereo stereo0d = stereos.get(i);
            if (stereo0d.getType() == InchiStereoType.Tetrahedral
                    || stereo0d.getType() == InchiStereoType.Allene) {
                InchiAtom central = stereo0d.getCentralAtom();
                InchiAtom[] neighbours = stereo0d.getAtoms();

                IAtom focus = inchiCdkAtomMap.get(central);
                IAtom[] neighbors = new IAtom[]{inchiCdkAtomMap.get(neighbours[0]), inchiCdkAtomMap.get(neighbours[1]),
                        inchiCdkAtomMap.get(neighbours[2]), inchiCdkAtomMap.get(neighbours[3])};
                ITetrahedralChirality.Stereo stereo;

                // as per JNI InChI doc even is clockwise and odd is
                // anti-clockwise
                if (stereo0d.getParity() == InchiStereoParity.ODD) {
                    stereo = ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
                } else if (stereo0d.getParity() == InchiStereoParity.EVEN) {
                    stereo = ITetrahedralChirality.Stereo.CLOCKWISE;
                } else {
                    // CDK Only supports parities of + or -
                    continue;
                }

                IStereoElement stereoElement = null;

                if (stereo0d.getType() == InchiStereoType.Tetrahedral) {
                    stereoElement = builder.newInstance(ITetrahedralChirality.class, focus, neighbors, stereo);
                } else if (stereo0d.getType() == InchiStereoType.Allene) {

                    // The periphals (p<i>) and terminals (t<i>) are refering to
                    // the following atoms. The focus (f) is also shown.
                    //
                    //   p0          p2
                    //    \          /
                    //     t0 = f = t1
                    //    /         \
                    //   p1         p3
                    IAtom[] peripherals = neighbors;
                    IAtom[] terminals = ExtendedTetrahedral.findTerminalAtoms(molecule, focus);

                    // InChI always provides the terminal atoms t0 and t1 as
                    // periphals, here we find where they are and then add in
                    // the other explicit atom. As the InChI create hydrogens
                    // for stereo elements, there will always we an explicit
                    // atom that can be found - it may be optionally suppressed
                    // later.

                    // not much documentation on this (at all) but they appear
                    // to always be the middle two atoms (index 1, 2) we therefore
                    // test these first - but handle the other indices just in
                    // case
                    for (IAtom terminal : terminals) {
                        if (peripherals[1].equals(terminal)) {
                            peripherals[1] = findOtherSinglyBonded(molecule, terminal, peripherals[0]);
                        } else if (peripherals[2].equals(terminal)) {
                            peripherals[2] = findOtherSinglyBonded(molecule, terminal, peripherals[3]);
                        } else if (peripherals[0].equals(terminal)) {
                            peripherals[0] = findOtherSinglyBonded(molecule, terminal, peripherals[1]);
                        } else if (peripherals[3].equals(terminal)) {
                            peripherals[3] = findOtherSinglyBonded(molecule, terminal, peripherals[2]);
                        }
                    }

                    stereoElement = new ExtendedTetrahedral(focus, peripherals, stereo);
                }

                assert stereoElement != null;
                molecule.addStereoElement(stereoElement);
            } else if (stereo0d.getType() == InchiStereoType.DoubleBond) {
                boolean extended = false;
                InchiAtom[] neighbors = stereo0d.getAtoms();

                // from JNI InChI doc
                //                            neighbor[4]  : {#X,#A,#B,#Y} in this order
                // X                          central_atom : NO_ATOM
                //  \            X        Y   type         : INCHI_StereoType_DoubleBond
                //   A == B       \      /
                //         \       A == B
                //          Y
                IAtom x = inchiCdkAtomMap.get(neighbors[0]);
                IAtom a = inchiCdkAtomMap.get(neighbors[1]);
                IAtom b = inchiCdkAtomMap.get(neighbors[2]);
                IAtom y = inchiCdkAtomMap.get(neighbors[3]);

                IBond stereoBond = molecule.getBond(a, b);
                if (stereoBond == null) {
                    extended = true;
                    IBond tmp = null;
                    // A = C = C = B
                    stereoBond = ExtendedCisTrans.findCentralBond(molecule, a);
                    if (stereoBond == null)
                        continue; // warn on invalid input?
                    IAtom[] ends = ExtendedCisTrans.findTerminalAtoms(molecule, stereoBond);
                    assert ends != null;
                    if (ends[0] != a)
                        flip(stereoBond);
                } else {
                    if (!stereoBond.getBegin().equals(a))
                        flip(stereoBond);
                }

                int config = IStereoElement.TOGETHER;
                if (stereo0d.getParity() == InchiStereoParity.EVEN)
                    config = IStereoElement.OPPOSITE;

                if (extended) {
                    molecule.addStereoElement(new ExtendedCisTrans(stereoBond,
                            new IBond[]{molecule.getBond(x, a),
                                        molecule.getBond(b, y)}, config));
                } else {
                    molecule.addStereoElement(new DoubleBondStereochemistry(stereoBond,
                            new IBond[]{molecule.getBond(x, a),
                                        molecule.getBond(b, y)}, config));
                }
            }
        }
    }

    private void addHydrogenIsotopes(IChemObjectBuilder builder, IAtom cAt, int mass, int count) {
        for (int j = 0; j < count; j++) {
            IAtom deut = builder.newInstance(IAtom.class);
            deut.setAtomicNumber(1);
            deut.setSymbol("H");
            deut.setMassNumber(mass);
            deut.setImplicitHydrogenCount(0);
            molecule.addAtom(deut);
            deut = molecule.getAtom(molecule.getAtomCount()-1);
            IBond bond = builder.newInstance(IBond.class, cAt, deut, IBond.Order.SINGLE);
            molecule.addBond(bond);
        }
    }

    /**
     * Finds a neighbor attached to 'atom' that is singley bonded and isn't
     * 'exclude'. If no such atom exists, the 'atom' is returned.
     *
     * @param container a molecule container
     * @param atom      the atom to find the neighbor or
     * @param exclude   don't find this atom
     * @return the other atom (or 'atom')
     */
    private static IAtom findOtherSinglyBonded(IAtomContainer container, IAtom atom, IAtom exclude) {
        for (final IBond bond : container.getConnectedBondsList(atom)) {
            if (!IBond.Order.SINGLE.equals(bond.getOrder()) || bond.contains(exclude)) continue;
            return bond.getOther(atom);
        }
        return atom;
    }

    /**
     * Returns generated molecule.
     * @return An AtomContainer object
     */
    public IAtomContainer getAtomContainer() {
        return (molecule);
    }

    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed. This returns the JNI INCHI enum and requires the optional
     * "cdk-jniinchi-support" module to be loaded (or the full JNI InChI lib
     * to be on the class path).
     * @deprecated use getStatus
     */
    @Deprecated
    public INCHI_RET getReturnStatus() {
        return JniInchiSupport.toJniStatus(output.getStatus());
    }

    /**
     * Access the status of the InChI output.
     * @return the status
     */
    public InchiStatus getStatus() {
        return output.getStatus();
    }

    /**
     * Gets generated (error/warning) messages.
     */
    public String getMessage() {
        return output.getMessage();
    }

    /**
     * Gets generated log.
     */
    public String getLog() {
        return output.getLog();
    }

    /**
     * <p>Returns warning flags, see INCHIDIFF in inchicmp.h.
     *
     * <p>[x][y]:
     * <br>x=0 =&gt; Reconnected if present in InChI otherwise Disconnected/Normal
     * <br>x=1 =&gt; Disconnected layer if Reconnected layer is present
     * <br>y=1 =&gt; Main layer or Mobile-H
     * <br>y=0 =&gt; Fixed-H layer
     */
    public long[][] getWarningFlags() {
        return output.getWarningFlags();
    }

}

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
import io.github.dan2097.jnainchi.InchiRadical;
import io.github.dan2097.jnainchi.InchiStatus;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnainchi.InchiStereoType;
import io.github.dan2097.jnainchi.JnaInchi;
import net.sf.jniinchi.INCHI_RET;
import org.openscience.cdk.config.Elements;
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
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

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

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(InChIToStructure.class);

    /** InChI mass values by atomic numbers - INCHI_BASE/src/util.c */
    private static final int[] defaultElemMass = new int[]{
            0,   1,   4,   7,   9,   11,  12,  14,  //     H He Li Be  B  C  N
            16,  19,  20,  23,  24,  27,  28,  31,  //  O  F Ne Na Mg Al Si  P
            32,  35,  40,  39,  40,  45,  48,  51,  //  S Cl Ar  K Ca Sc Ti  V
            52,  55,  56,  59,  59,  64,  65,  70,  // Cr Mn Fe Co Ni Cu Zn Ga
            73,  75,  79,  80,  84,  85,  88,  89,  // Ge As Se Br Kr Rb Sr  Y
            91,  93,  96,  98,  101, 103, 106, 108, // Zr Nb Mo Tc Ru Rh Pd Ag
            112, 115, 119, 122, 128, 127, 131, 133, // Cd In Sn Sb Te  I Xe Cs
            137, 139, 140, 141, 144, 145, 150, 152, // Ba La Ce Pr Nd Pm Sm Eu
            157, 159, 163, 165, 167, 169, 173, 175, // Gd Tb Dy Ho Er Tm Yb Lu
            178, 181, 184, 186, 190, 192, 195, 197, // Hf Ta  W Re Os Ir Pt Au
            201, 204, 207, 209, 209, 210, 222, 223, // Hg Tl Pb Bi Po At Rn Fr
            226, 227, 232, 231, 238, 237, 244, 243, // Ra Ac Th Pa  U Np Pu Am
            247, 247, 251, 252, 257, 258, 259, 260, // Cm Bk Cf Es Fm Md No Lr
            261, 270, 269, 270, 270, 278, 281, 281, // Rf Db Sg Bh Hs Mt Ds Rg
            285, 278, 289, 289, 293, 297, 294
    };

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
            int elem = Elements.ofString(iAt.getElName()).number();
            cAt.setAtomicNumber(elem);

            // Ignore coordinates - all zero - unless aux info was given... but
            // the CDK doesn't have an API to provide that

            // InChI does not have unset properties so we set charge,
            // hydrogen count (implicit) and isotopic mass
            cAt.setFormalCharge(iAt.getCharge());
            cAt.setImplicitHydrogenCount(iAt.getImplicitHydrogen());
            int isotopicMass = iAt.getIsotopicMass();

            if (isotopicMass != 0) {
                if (isotopicMass > ISOTOPIC_SHIFT_THRESHOLD) {
                    int delta = isotopicMass - ISOTOPIC_SHIFT_FLAG;
                    if (elem > 0 && elem < defaultElemMass.length)
                        cAt.setMassNumber(defaultElemMass[elem] + delta);
                    else
                        logger.error("Cannot set mass delta for element {}, no base mass?", elem);
                } else {
                    cAt.setMassNumber(isotopicMass);
                }
            }

            molecule.addAtom(cAt);
            cAt = molecule.getAtom(molecule.getAtomCount()-1);
            addHydrogenIsotopes(builder, cAt, 2, iAt.getImplicitDeuterium());
            addHydrogenIsotopes(builder, cAt, 3, iAt.getImplicitTritium());

            InchiRadical radical = iAt.getRadical();
            if (radical == InchiRadical.DOUBLET) {
                molecule.addSingleElectron(molecule.indexOf(cAt));
            } else if (radical == InchiRadical.SINGLET ||
                       radical == InchiRadical.TRIPLET) {
                // Information loss - we should make MDL SPIN_MULTIPLICITY avaliable to this API
                molecule.addSingleElectron(molecule.indexOf(cAt));
                molecule.addSingleElectron(molecule.indexOf(cAt));
            }
        }

        List<InchiBond> bonds = input.getBonds();
        for (InchiBond iBo : bonds) {
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
        for (InchiStereo stereo0d : stereos) {
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

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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jniinchi.INCHI_BOND_STEREO;
import net.sf.jniinchi.INCHI_BOND_TYPE;
import net.sf.jniinchi.INCHI_PARITY;
import net.sf.jniinchi.INCHI_RET;
import net.sf.jniinchi.INCHI_STEREOTYPE;
import net.sf.jniinchi.JniInchiAtom;
import net.sf.jniinchi.JniInchiBond;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiInputInchi;
import net.sf.jniinchi.JniInchiOutputStructure;
import net.sf.jniinchi.JniInchiStereo0D;
import net.sf.jniinchi.JniInchiWrapper;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;

/**
 * <p>This class generates a CDK IAtomContainer from an InChI string.  It places
 * calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>The generated IAtomContainer will have all 2D and 3D coordinates set to 0.0,
 * but may have atom parities set.  Double bond and allene stereochemistry are
 * not currently recorded.
 *
 * <h3>Example usage</h3>
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
 * <p><tt><b>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIToStructure {

    protected JniInchiInputInchi      input;

    protected JniInchiOutputStructure output;

    protected IAtomContainer          molecule;

    // magic number - indicates isotope mass is relative
    private static final int          ISOTOPIC_SHIFT_FLAG = 10000;

    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder) throws CDKException {
        try {
            input = new JniInchiInputInchi(inchi, "");
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to convert InChI to molecule: " + jie.getMessage(), jie);
        }
        generateAtomContainerFromInchi(builder);
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, String options) throws CDKException {
        try {
            input = new JniInchiInputInchi(inchi, options);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to convert InChI to molecule: " + jie.getMessage(), jie);
        }
        generateAtomContainerFromInchi(builder);
    }

    /**
     * Constructor. Generates CMLMolecule from InChI.
     * @param inchi
     * @param options
     * @throws CDKException
     */
    protected InChIToStructure(String inchi, IChemObjectBuilder builder, List<String> options) throws CDKException {
        try {
            input = new JniInchiInputInchi(inchi, options);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to convert InChI to molecule: " + jie.getMessage());
        }
        generateAtomContainerFromInchi(builder);
    }

    /**
     * Gets structure from InChI, and converts InChI library data structure
     * into an IAtomContainer.
     *
     * @throws CDKException
     */
    protected void generateAtomContainerFromInchi(IChemObjectBuilder builder) throws CDKException {
        try {
            output = JniInchiWrapper.getStructureFromInchi(input);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to convert InChI to molecule: " + jie.getMessage(), jie);
        }

        //molecule = new AtomContainer();
        molecule = builder.newInstance(IAtomContainer.class);

        Map<JniInchiAtom, IAtom> inchiCdkAtomMap = new HashMap<JniInchiAtom, IAtom>();

        for (int i = 0; i < output.getNumAtoms(); i++) {
            JniInchiAtom iAt = output.getAtom(i);
            IAtom cAt = builder.newInstance(IAtom.class);

            inchiCdkAtomMap.put(iAt, cAt);

            cAt.setID("a" + i);
            cAt.setSymbol(iAt.getElementType());
            cAt.setAtomicNumber(PeriodicTable.getAtomicNumber(cAt.getSymbol()));

            // Ignore coordinates - all zero - unless aux info was given... but
            // the CDK doesn't have an API to provide that

            // InChI does not have unset properties so we set charge,
            // hydrogen count (implicit) and isotopic mass
            cAt.setFormalCharge(iAt.getCharge());
            cAt.setImplicitHydrogenCount(iAt.getImplicitH());

            int isotopicMass = iAt.getIsotopicMass();

            if (isotopicMass != 0) {
                if (ISOTOPIC_SHIFT_FLAG == (isotopicMass & ISOTOPIC_SHIFT_FLAG)) {
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
        }

        for (int i = 0; i < output.getNumBonds(); i++) {
            JniInchiBond iBo = output.getBond(i);
            IBond cBo = builder.newInstance(IBond.class);

            IAtom atO = inchiCdkAtomMap.get(iBo.getOriginAtom());
            IAtom atT = inchiCdkAtomMap.get(iBo.getTargetAtom());
            IAtom[] atoms = new IAtom[2];
            atoms[0] = atO;
            atoms[1] = atT;
            cBo.setAtoms(atoms);

            INCHI_BOND_TYPE type = iBo.getBondType();
            if (type == INCHI_BOND_TYPE.SINGLE) {
                cBo.setOrder(Order.SINGLE);
            } else if (type == INCHI_BOND_TYPE.DOUBLE) {
                cBo.setOrder(Order.DOUBLE);
            } else if (type == INCHI_BOND_TYPE.TRIPLE) {
                cBo.setOrder(Order.TRIPLE);
            } else if (type == INCHI_BOND_TYPE.ALTERN) {
                cBo.setFlag(CDKConstants.ISAROMATIC, true);
            } else {
                throw new CDKException("Unknown bond type: " + type);
            }

            INCHI_BOND_STEREO stereo = iBo.getBondStereo();

            // No stereo definition
            if (stereo == INCHI_BOND_STEREO.NONE) {
                cBo.setStereo(IBond.Stereo.NONE);
            }
            // Bond ending (fat end of wedge) below the plane
            else if (stereo == INCHI_BOND_STEREO.SINGLE_1DOWN) {
                cBo.setStereo(IBond.Stereo.DOWN);
            }
            // Bond ending (fat end of wedge) above the plane
            else if (stereo == INCHI_BOND_STEREO.SINGLE_1UP) {
                cBo.setStereo(IBond.Stereo.UP);
            }
            // Bond starting (pointy end of wedge) below the plane
            else if (stereo == INCHI_BOND_STEREO.SINGLE_2DOWN) {
                cBo.setStereo(IBond.Stereo.DOWN_INVERTED);
            }
            // Bond starting (pointy end of wedge) above the plane
            else if (stereo == INCHI_BOND_STEREO.SINGLE_2UP) {
                cBo.setStereo(IBond.Stereo.UP_INVERTED);
            }
            // Bond with undefined stereochemistry
            else if (stereo == INCHI_BOND_STEREO.SINGLE_1EITHER || stereo == INCHI_BOND_STEREO.DOUBLE_EITHER) {
                cBo.setStereo((IBond.Stereo) CDKConstants.UNSET);
            }

            molecule.addBond(cBo);
        }

        for (int i = 0; i < output.getNumStereo0D(); i++) {
            JniInchiStereo0D stereo0d = output.getStereo0D(i);
            if (stereo0d.getStereoType() == INCHI_STEREOTYPE.TETRAHEDRAL
                    || stereo0d.getStereoType() == INCHI_STEREOTYPE.ALLENE) {
                JniInchiAtom central = stereo0d.getCentralAtom();
                JniInchiAtom[] neighbours = stereo0d.getNeighbors();

                IAtom focus = inchiCdkAtomMap.get(central);
                IAtom[] neighbors = new IAtom[]{inchiCdkAtomMap.get(neighbours[0]), inchiCdkAtomMap.get(neighbours[1]),
                        inchiCdkAtomMap.get(neighbours[2]), inchiCdkAtomMap.get(neighbours[3])};
                ITetrahedralChirality.Stereo stereo;

                // as per JNI InChI doc even is clockwise and odd is
                // anti-clockwise
                if (stereo0d.getParity() == INCHI_PARITY.ODD) {
                    stereo = ITetrahedralChirality.Stereo.ANTI_CLOCKWISE;
                } else if (stereo0d.getParity() == INCHI_PARITY.EVEN) {
                    stereo = ITetrahedralChirality.Stereo.CLOCKWISE;
                } else {
                    // CDK Only supports parities of + or -
                    continue;
                }

                IStereoElement stereoElement = null;

                if (stereo0d.getStereoType() == INCHI_STEREOTYPE.TETRAHEDRAL) {
                    stereoElement = builder.newInstance(ITetrahedralChirality.class, focus, neighbors, stereo);
                } else if (stereo0d.getStereoType() == INCHI_STEREOTYPE.ALLENE) {

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
                        if (peripherals[1] == terminal) {
                            peripherals[1] = findOtherSinglyBonded(molecule, terminal, peripherals[0]);
                        } else if (peripherals[2] == terminal) {
                            peripherals[2] = findOtherSinglyBonded(molecule, terminal, peripherals[3]);
                        } else if (peripherals[0] == terminal) {
                            peripherals[0] = findOtherSinglyBonded(molecule, terminal, peripherals[1]);
                        } else if (peripherals[3] == terminal) {
                            peripherals[3] = findOtherSinglyBonded(molecule, terminal, peripherals[2]);
                        }
                    }

                    stereoElement = new ExtendedTetrahedral(focus, peripherals, stereo);
                }

                assert stereoElement != null;
                molecule.addStereoElement(stereoElement);
            } else if (stereo0d.getStereoType() == INCHI_STEREOTYPE.DOUBLEBOND) {
                JniInchiAtom[] neighbors = stereo0d.getNeighbors();

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

                // XXX: AtomContainer is doing slow lookup
                IBond stereoBond = molecule.getBond(a, b);
                stereoBond.setAtoms(new IAtom[]{a, b}); // ensure a is first atom

                Conformation conformation = null;

                switch (stereo0d.getParity()) {
                    case ODD:
                        conformation = Conformation.TOGETHER;
                        break;
                    case EVEN:
                        conformation = Conformation.OPPOSITE;
                        break;
                }

                // unspecified not stored
                if (conformation == null) continue;

                molecule.addStereoElement(new DoubleBondStereochemistry(stereoBond, new IBond[]{molecule.getBond(x, a),
                        molecule.getBond(b, y)}, conformation));
            } else {
                // TODO - other types of atom parity - double bond, etc
            }
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
            return bond.getConnectedAtom(atom);
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
     * has failed.
     */
    public INCHI_RET getReturnStatus() {
        return (output.getReturnStatus());
    }

    /**
     * Gets generated (error/warning) messages.
     */
    public String getMessage() {
        return (output.getMessage());
    }

    /**
     * Gets generated log.
     */
    public String getLog() {
        return (output.getLog());
    }

    /**
     * <p>Returns warning flags, see INCHIDIFF in inchicmp.h.
     *
     * <p>[x][y]:
     * <br>x=0 => Reconnected if present in InChI otherwise Disconnected/Normal
     * <br>x=1 => Disconnected layer if Reconnected layer is present
     * <br>y=1 => Main layer or Mobile-H
     * <br>y=0 => Fixed-H layer
     */
    public long[][] getWarningFlags() {
        return (output.getWarningFlags());
    }

}

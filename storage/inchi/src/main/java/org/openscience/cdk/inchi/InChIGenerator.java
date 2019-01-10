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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import net.sf.jniinchi.INCHI_BOND_STEREO;
import net.sf.jniinchi.INCHI_BOND_TYPE;
import net.sf.jniinchi.INCHI_KEY;
import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_PARITY;
import net.sf.jniinchi.INCHI_RADICAL;
import net.sf.jniinchi.INCHI_RET;
import net.sf.jniinchi.INCHI_STEREOTYPE;
import net.sf.jniinchi.JniInchiAtom;
import net.sf.jniinchi.JniInchiBond;
import net.sf.jniinchi.JniInchiException;
import net.sf.jniinchi.JniInchiInput;
import net.sf.jniinchi.JniInchiOutput;
import net.sf.jniinchi.JniInchiOutputKey;
import net.sf.jniinchi.JniInchiStereo0D;
import net.sf.jniinchi.JniInchiWrapper;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.ExtendedTetrahedral;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * <p>This class generates the IUPAC International Chemical Identifier (InChI) for
 * a CDK IAtomContainer. It places calls to a JNI wrapper for the InChI C++ library.
 *
 * <p>If the atom container has 3D coordinates for all of its atoms then they
 * will be used, otherwise 2D coordinates will be used if available.
 *
 * <p><i>Spin multiplicities and some aspects of stereochemistry are not
 * currently handled completely.</i>
 *
 * <h3>Example usage</h3>
 *
 * <code>// Generate factory - throws CDKException if native code does not load</code><br>
 * <code>InChIGeneratorFactory factory = new InChIGeneratorFactory();</code><br>
 * <code>// Get InChIGenerator</code><br>
 * <code>InChIGenerator gen = factory.getInChIGenerator(container);</code><br>
 * <code></code><br>
 * <code>INCHI_RET ret = gen.getReturnStatus();</code><br>
 * <code>if (ret == INCHI_RET.WARNING) {</code><br>
 * <code>  // InChI generated, but with warning message</code><br>
 * <code>  System.out.println("InChI warning: " + gen.getMessage());</code><br>
 * <code>} else if (ret != INCHI_RET.OKAY) {</code><br>
 * <code>  // InChI generation failed</code><br>
 * <code>  throw new CDKException("InChI failed: " + ret.toString()</code><br>
 * <code>    + " [" + gen.getMessage() + "]");</code><br>
 * <code>}</code><br>
 * <code></code><br>
 * <code>String inchi = gen.getInchi();</code><br>
 * <code>String auxinfo = gen.getAuxInfo();</code><br>
 * <p>
 * <b>
 * TODO: distinguish between singlet and undefined spin multiplicity<br>
 * TODO: double bond and allene parities<br>
 * TODO: problem recognising bond stereochemistry<br>
 * </b>
 *
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.githash
 */
public class InChIGenerator {

    protected JniInchiInput input;

    protected JniInchiOutput output;

    private final boolean auxNone;

    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(InChIGenerator.class);

    /**
     * AtomContainer instance refers to.
     */
    protected IAtomContainer atomContainer;

    /**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer      AtomContainer to generate InChI for.
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws org.openscience.cdk.exception.CDKException if there is an
     * error during InChI generation
     */
    protected InChIGenerator(IAtomContainer atomContainer, boolean ignoreAromaticBonds) throws CDKException {
        this(atomContainer, Collections.singletonList(INCHI_OPTION.AuxNone), ignoreAromaticBonds);
    }

    /**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer      AtomContainer to generate InChI for.
     * @param options   Space delimited string of options to pass to InChI library.
     *                  Each option may optionally be preceded by a command line
     *                  switch (/ or -).
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws CDKException
     */
    protected InChIGenerator(IAtomContainer atomContainer, String options, boolean ignoreAromaticBonds)
            throws CDKException {
        try {
            input = new JniInChIInputAdapter(options);
            generateInchiFromCDKAtomContainer(atomContainer, ignoreAromaticBonds);
            auxNone = input.getOptions() != null && input.getOptions().contains("AuxNone");
        } catch (JniInchiException jie) {
            throw new CDKException("InChI generation failed: " + jie.getMessage(), jie);
        }
    }

    /**
     * <p>Constructor. Generates InChI from CDK AtomContainer.
     *
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then calls the library.
     *
     * @param atomContainer     AtomContainer to generate InChI for.
     * @param options           List of INCHI_OPTION.
     * @param ignoreAromaticBonds if aromatic bonds should be treated as bonds of type single and double
     * @throws CDKException
     */
    protected InChIGenerator(IAtomContainer atomContainer, List<INCHI_OPTION> options, boolean ignoreAromaticBonds)
            throws CDKException {
        try {
            input = new JniInChIInputAdapter(options);
            generateInchiFromCDKAtomContainer(atomContainer, ignoreAromaticBonds);
            auxNone = input.getOptions() != null && input.getOptions().contains("AuxNone");
        } catch (JniInchiException jie) {
            throw new CDKException("InChI generation failed: " + jie.getMessage(), jie);
        }
    }

    /**
     * <p>Reads atoms, bonds etc from atom container and converts to format
     * InChI library requires, then places call for the library to generate
     * the InChI.
     *
     * @param atomContainer      AtomContainer to generate InChI for.
     * @throws CDKException
     */
    private void generateInchiFromCDKAtomContainer(IAtomContainer atomContainer, boolean ignore) throws CDKException {
        this.atomContainer = atomContainer;

        Iterator<IAtom> atoms = atomContainer.atoms().iterator();

        // Check for 3d coordinates
        boolean all3d = true;
        boolean all2d = true;
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getPoint3d() == null) {
                all3d = false;
            }
            if (atom.getPoint2d() == null) {
                all2d = false;
            }
        }

        Map<IAtom, JniInchiAtom> atomMap = new HashMap<IAtom, JniInchiAtom>();
        atoms = atomContainer.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();

            // Get coordinates
            // Use 3d if possible, otherwise 2d or none
            double x, y, z;
            if (all3d) {
                Point3d p = atom.getPoint3d();
                x = p.x;
                y = p.y;
                z = p.z;
            } else if (all2d) {
                Point2d p = atom.getPoint2d();
                x = p.x;
                y = p.y;
                z = 0.0;
            } else {
                x = 0.0;
                y = 0.0;
                z = 0.0;
            }

            // Chemical element symbol
            String el = atom.getSymbol();

            // Generate InChI atom
            JniInchiAtom iatom = input.addAtom(new JniInchiAtom(x, y, z, el));
            atomMap.put(atom, iatom);

            // Check if charged
            int charge = atom.getFormalCharge();
            if (charge != 0) {
                iatom.setCharge(charge);
            }

            // Check whether isotopic
            Integer isotopeNumber = atom.getMassNumber();
            if (isotopeNumber != null) {
                iatom.setIsotopicMass(isotopeNumber);
            }

            // Check for implicit hydrogens
            // atom.getHydrogenCount() returns number of implict hydrogens, not
            // total number
            // Ref: Posting to cdk-devel list by Egon Willighagen 2005-09-17
            Integer implicitH = atom.getImplicitHydrogenCount();

            // set implicit hydrogen count, -1 tells the inchi to determine it
            iatom.setImplicitH(implicitH != null ? implicitH : -1);

            // Check if radical
            int count = atomContainer.getConnectedSingleElectronsCount(atom);
            if (count == 0) {
                // TODO - how to check whether singlet or undefined multiplicity
            } else if (count == 1) {
                iatom.setRadical(INCHI_RADICAL.DOUBLET);
            } else if (count == 2) {
                iatom.setRadical(INCHI_RADICAL.TRIPLET);
            } else {
                throw new CDKException("Unrecognised radical type");
            }
        }

        // Process bonds
        for (IBond bond : atomContainer.bonds()) {
            // Assumes 2 centre bond
            JniInchiAtom at0 = (JniInchiAtom) atomMap.get(bond.getBegin());
            JniInchiAtom at1 = (JniInchiAtom) atomMap.get(bond.getEnd());

            // Get bond order
            INCHI_BOND_TYPE order;
            Order           bo = bond.getOrder();
            if (!ignore && bond.getFlag(CDKConstants.ISAROMATIC)) {
                order = INCHI_BOND_TYPE.ALTERN;
            } else if (bo == Order.SINGLE) {
                order = INCHI_BOND_TYPE.SINGLE;
            } else if (bo == Order.DOUBLE) {
                order = INCHI_BOND_TYPE.DOUBLE;
            } else if (bo == Order.TRIPLE) {
                order = INCHI_BOND_TYPE.TRIPLE;
            } else {
                throw new CDKException("Failed to generate InChI: Unsupported bond type");
            }

            // Create InChI bond
            JniInchiBond ibond = new JniInchiBond(at0, at1, order);
            input.addBond(ibond);

            // Check for bond stereo definitions
            IBond.Stereo stereo = bond.getStereo();
            // No stereo definition
            if (stereo == IBond.Stereo.NONE) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.NONE);
            }
            // Bond ending (fat end of wedge) below the plane
            else if (stereo == IBond.Stereo.DOWN) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1DOWN);
            }
            // Bond ending (fat end of wedge) above the plane
            else if (stereo == IBond.Stereo.UP) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1UP);
            }
            // Bond starting (pointy end of wedge) below the plane
            else if (stereo == IBond.Stereo.DOWN_INVERTED) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_2DOWN);
            }
            // Bond starting (pointy end of wedge) above the plane
            else if (stereo == IBond.Stereo.UP_INVERTED) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_2UP);
            } else if (stereo == IBond.Stereo.E_OR_Z) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.DOUBLE_EITHER);
            } else if (stereo == IBond.Stereo.UP_OR_DOWN) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1EITHER);
            } else if (stereo == IBond.Stereo.UP_OR_DOWN_INVERTED) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_2EITHER);
            }
            // Bond with undefined stereochemistry
            else if (stereo == CDKConstants.UNSET) {
                if (order == INCHI_BOND_TYPE.SINGLE) {
                    ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1EITHER);
                } else if (order == INCHI_BOND_TYPE.DOUBLE) {
                    ibond.setStereoDefinition(INCHI_BOND_STEREO.DOUBLE_EITHER);
                }
            }
        }

        // Process tetrahedral stereo elements
        for (IStereoElement stereoElem : atomContainer.stereoElements()) {
            if (stereoElem instanceof ITetrahedralChirality) {
                ITetrahedralChirality chirality = (ITetrahedralChirality) stereoElem;
                IAtom[] surroundingAtoms = chirality.getLigands();
                Stereo stereoType = chirality.getStereo();

                JniInchiAtom atC = (JniInchiAtom) atomMap.get(chirality.getChiralAtom());
                JniInchiAtom at0 = (JniInchiAtom) atomMap.get(surroundingAtoms[0]);
                JniInchiAtom at1 = (JniInchiAtom) atomMap.get(surroundingAtoms[1]);
                JniInchiAtom at2 = (JniInchiAtom) atomMap.get(surroundingAtoms[2]);
                JniInchiAtom at3 = (JniInchiAtom) atomMap.get(surroundingAtoms[3]);
                INCHI_PARITY p = INCHI_PARITY.UNKNOWN;
                if (stereoType == Stereo.ANTI_CLOCKWISE) {
                    p = INCHI_PARITY.ODD;
                } else if (stereoType == Stereo.CLOCKWISE) {
                    p = INCHI_PARITY.EVEN;
                } else {
                    throw new CDKException("Unknown tetrahedral chirality");
                }

                JniInchiStereo0D jniStereo = new JniInchiStereo0D(atC, at0, at1, at2, at3,
                        INCHI_STEREOTYPE.TETRAHEDRAL, p);
                input.addStereo0D(jniStereo);
            } else if (stereoElem instanceof IDoubleBondStereochemistry) {
                IDoubleBondStereochemistry dbStereo = (IDoubleBondStereochemistry) stereoElem;
                IBond[] surroundingBonds = dbStereo.getBonds();
                if (surroundingBonds[0] == null || surroundingBonds[1] == null)
                    throw new CDKException("Cannot generate an InChI with incomplete double bond info");
                org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation stereoType = dbStereo
                        .getStereo();

                IBond stereoBond = dbStereo.getStereoBond();
                JniInchiAtom at0 = null;
                JniInchiAtom at1 = null;
                JniInchiAtom at2 = null;
                JniInchiAtom at3 = null;
                // TODO: I should check for two atom bonds... or maybe that should happen when you
                //    create a double bond stereochemistry
                if (stereoBond.contains(surroundingBonds[0].getBegin())) {
                    // first atom is A
                    at1 = (JniInchiAtom) atomMap.get(surroundingBonds[0].getBegin());
                    at0 = (JniInchiAtom) atomMap.get(surroundingBonds[0].getEnd());
                } else {
                    // first atom is X
                    at0 = (JniInchiAtom) atomMap.get(surroundingBonds[0].getBegin());
                    at1 = (JniInchiAtom) atomMap.get(surroundingBonds[0].getEnd());
                }
                if (stereoBond.contains(surroundingBonds[1].getBegin())) {
                    // first atom is B
                    at2 = (JniInchiAtom) atomMap.get(surroundingBonds[1].getBegin());
                    at3 = (JniInchiAtom) atomMap.get(surroundingBonds[1].getEnd());
                } else {
                    // first atom is Y
                    at2 = (JniInchiAtom) atomMap.get(surroundingBonds[1].getEnd());
                    at3 = (JniInchiAtom) atomMap.get(surroundingBonds[1].getBegin());
                }
                INCHI_PARITY p = INCHI_PARITY.UNKNOWN;
                if (stereoType == org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER) {
                    p = INCHI_PARITY.ODD;
                } else if (stereoType == org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE) {
                    p = INCHI_PARITY.EVEN;
                } else {
                    throw new CDKException("Unknown double bond stereochemistry");
                }

                JniInchiStereo0D jniStereo = new JniInchiStereo0D(null, at0, at1, at2, at3,
                        INCHI_STEREOTYPE.DOUBLEBOND, p);
                input.addStereo0D(jniStereo);
            } else if (stereoElem instanceof ExtendedTetrahedral) {

                ExtendedTetrahedral extendedTetrahedral = (ExtendedTetrahedral) stereoElem;
                Stereo winding = extendedTetrahedral.winding();

                // The periphals (p<i>) and terminals (t<i>) are refering to
                // the following atoms. The focus (f) is also shown.
                //
                //   p0          p2
                //    \          /
                //     t0 = f = t1
                //    /         \
                //   p1         p3
                IAtom[] terminals = extendedTetrahedral.findTerminalAtoms(atomContainer);
                IAtom[] peripherals = extendedTetrahedral.peripherals();

                // InChI API is particualar about the input, each terminal atom
                // needs to be present in the list of neighbors and they must
                // be at index 1 and 2 (i.e. in the middle). This is true even
                // of explict atoms. For the implicit atoms, the terminals may
                // be in the peripherals allready and so we correct the winding
                // and reposition as needed.

                List<IBond> t0Bonds = onlySingleBonded(atomContainer.getConnectedBondsList(terminals[0]));
                List<IBond> t1Bonds = onlySingleBonded(atomContainer.getConnectedBondsList(terminals[1]));

                // first if there are two explicit atoms we need to replace one
                // with the terminal atom - the configuration does not change
                if (t0Bonds.size() == 2) {
                    IAtom replace = t0Bonds.remove(0).getOther(terminals[0]);
                    for (int i = 0; i < peripherals.length; i++)
                        if (replace == peripherals[i]) peripherals[i] = terminals[0];
                }

                if (t1Bonds.size() == 2) {
                    IAtom replace = t1Bonds.remove(0).getOther(terminals[1]);
                    for (int i = 0; i < peripherals.length; i++)
                        if (replace == peripherals[i]) peripherals[i] = terminals[1];
                }

                // the neighbor attached to each terminal atom that we will
                // define the configuration of
                IAtom t0Neighbor = t0Bonds.get(0).getOther(terminals[0]);
                IAtom t1Neighbor = t1Bonds.get(0).getOther(terminals[1]);

                // we now need to move all the atoms into the correct positions
                // everytime we exchange atoms the configuration inverts
                for (int i = 0; i < peripherals.length; i++) {
                    if (i != 0 && t0Neighbor.equals(peripherals[i])) {
                        swap(peripherals, i, 0);
                        winding = winding.invert();
                    } else if (i != 1 && terminals[0].equals(peripherals[i])) {
                        swap(peripherals, i, 1);
                        winding = winding.invert();
                    } else if (i != 2 && terminals[1].equals(peripherals[i])) {
                        swap(peripherals, i, 2);
                        winding = winding.invert();
                    } else if (i != 3 && t1Neighbor.equals(peripherals[i])) {
                        swap(peripherals, i, 3);
                        winding = winding.invert();
                    }
                }

                INCHI_PARITY parity = INCHI_PARITY.UNKNOWN;
                if (winding == Stereo.ANTI_CLOCKWISE)
                    parity = INCHI_PARITY.ODD;
                else if (winding == Stereo.CLOCKWISE)
                    parity = INCHI_PARITY.EVEN;
                else
                    throw new CDKException("Unknown extended tetrahedral chirality");

                JniInchiStereo0D jniStereo = new JniInchiStereo0D(atomMap.get(extendedTetrahedral.focus()),
                        atomMap.get(peripherals[0]), atomMap.get(peripherals[1]), atomMap.get(peripherals[2]),
                        atomMap.get(peripherals[3]), INCHI_STEREOTYPE.ALLENE, parity);
                input.addStereo0D(jniStereo);
            }
        }

        try {
            output = JniInchiWrapper.getInchi(input);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to generate InChI: " + jie.getMessage(), jie);
        }
    }

    private static List<IBond> onlySingleBonded(List<IBond> bonds) {
        List<IBond> filtered = new ArrayList<IBond>();
        for (IBond bond : bonds) {
            if (bond.getOrder() == IBond.Order.SINGLE) filtered.add(bond);
        }
        return filtered;
    }

    private static void swap(Object[] objs, int i, int j) {
        final Object tmp = objs[i];
        objs[i] = objs[j];
        objs[j] = tmp;
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
     * Gets generated InChI string.
     */
    public String getInchi() {
        return (output.getInchi());
    }

    /**
     * Gets generated InChIKey string.
     */
    public String getInchiKey() throws CDKException {
        JniInchiOutputKey key;
        try {
            key = JniInchiWrapper.getInchiKey(output.getInchi());
            if (key.getReturnStatus() == INCHI_KEY.OK) {
                return key.getKey();
            } else {
                throw new CDKException("Error while creating InChIKey: " + key.getReturnStatus());
            }
        } catch (JniInchiException exception) {
            throw new CDKException("Error while creating InChIKey: " + exception.getMessage(), exception);
        }
    }

    /**
     * Gets auxillary information.
     */
    public String getAuxInfo() {
        if (auxNone) {
            LOGGER.warn("AuxInfo requested but AuxNone option is set (default).");
        }
        return (output.getAuxInfo());
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
}

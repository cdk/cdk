/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2006-2007  Sam Adams <sea36@users.sf.net>
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

import net.sf.jniinchi.*;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * <p><tt><b>
 * TODO: distinguish between singlet and undefined spin multiplicity<br/>
 * TODO: double bond and allene parities<br/>
 * TODO: problem recognising bond stereochemistry<br/>
 * </b></tt>
 * 
 * @author Sam Adams
 *
 * @cdk.module inchi
 * @cdk.svnrev  $Revision$
 */
@TestClass("org.openscience.cdk.inchi.InChIGeneratorTest")
public class InChIGenerator {
    
    protected JniInchiInput input;
    
    protected JniInchiOutput output;
    
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
     * @throws org.openscience.cdk.exception.CDKException if there is an
     * error during InChI generation
     */
    @TestClass("testGetInchiFromChlorineAtom,testGetInchiFromLithiumIontestGetInchiFromChlorine37Atom")
    protected InChIGenerator(IAtomContainer atomContainer) throws CDKException {
        try {
            input = new JniInchiInput("");
            generateInchiFromCDKAtomContainer(atomContainer);
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
     * @param atomContainer      AtomContainer to generate InChI for.
     * @param options   Space delimited string of options to pass to InChI library.
     *                  Each option may optionally be preceded by a command line
     *                  switch (/ or -).
     * @throws CDKException
     */
    protected InChIGenerator(IAtomContainer atomContainer, String options) throws CDKException {
        try {
            input = new JniInchiInput(options);
            generateInchiFromCDKAtomContainer(atomContainer);
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
     * @throws CDKException
     */
    protected InChIGenerator(IAtomContainer atomContainer, List options) throws CDKException {
        try {
            input = new JniInchiInput(options);
            generateInchiFromCDKAtomContainer(atomContainer);
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
    private void generateInchiFromCDKAtomContainer(IAtomContainer atomContainer) throws CDKException {
        this.atomContainer = atomContainer;
        
        Iterator<IAtom> atoms = atomContainer.atoms().iterator();
        
        // Check for 3d coordinates
        boolean all3d = true;
        boolean all2d = true;
        while (atoms.hasNext()) {
            IAtom atom = (IAtom)atoms.next();
            if (atom.getPoint3d() == null) {
                all3d = false;
            }
            if (atom.getPoint2d() == null) {
                all2d = false;
            }
        }
        
        // Process atoms
        IsotopeFactory ifact = null;
        try {
            ifact = IsotopeFactory.getInstance(atomContainer.getBuilder());
        } catch (Exception e) {
            // Do nothing
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
            if (isotopeNumber != CDKConstants.UNSET && ifact != null) {
                IAtom isotope = atomContainer.getBuilder().newAtom(el);
                ifact.configure(isotope);
                if (isotope.getMassNumber().intValue() == isotopeNumber.intValue()) {
                    isotopeNumber = 0;
                }
            }
            if (isotopeNumber != CDKConstants.UNSET) {
                iatom.setIsotopicMass(isotopeNumber);
            }
            
            // Check for implicit hydrogens
            // atom.getHydrogenCount() returns number of implict hydrogens, not
            // total number
            // Ref: Posting to cdk-devel list by Egon Willighagen 2005-09-17
            Integer implicitH = atom.getHydrogenCount();
            if (implicitH == CDKConstants.UNSET) implicitH = 0;
            
            if (implicitH != 0) {
                iatom.setImplicitH(implicitH);
            }
            
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
        Iterator<IBond> bonds =  atomContainer.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();

            // Assumes 2 centre bond
            JniInchiAtom at0 = (JniInchiAtom) atomMap.get(bond.getAtom(0));
            JniInchiAtom at1 = (JniInchiAtom) atomMap.get(bond.getAtom(1));
            
            // Get bond order
            INCHI_BOND_TYPE order;
            IBond.Order bo = bond.getOrder();
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
            	order = INCHI_BOND_TYPE.ALTERN;
            } else if (bo == CDKConstants.BONDORDER_SINGLE) {
                order = INCHI_BOND_TYPE.SINGLE;
            } else if (bo == CDKConstants.BONDORDER_DOUBLE) {
                order = INCHI_BOND_TYPE.DOUBLE;
            } else if (bo == CDKConstants.BONDORDER_TRIPLE) {
                order = INCHI_BOND_TYPE.TRIPLE;
            } else {
                throw new CDKException("Failed to generate InChI: Unsupported bond type");
            }
            
            // Create InChI bond
            JniInchiBond ibond = new JniInchiBond(at0, at1, order);
            input.addBond(ibond);
            
            // Check for bond stereo definitions
            int stereo = bond.getStereo();
            // No stereo definition
            if (stereo == CDKConstants.STEREO_BOND_NONE) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.NONE);
            }
            // Bond ending (fat end of wedge) below the plane
            else if (stereo == CDKConstants.STEREO_BOND_DOWN) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1DOWN);
            }
            // Bond ending (fat end of wedge) above the plane
            else if (stereo == CDKConstants.STEREO_BOND_UP) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1UP);
            } 
            // Bond starting (pointy end of wedge) below the plane
            else if (stereo == CDKConstants.STEREO_BOND_DOWN_INV) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_2DOWN);
            }
            // Bond starting (pointy end of wedge) above the plane
            else if (stereo == CDKConstants.STEREO_BOND_UP_INV) {
                ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_2UP);
            } 
            // Bond with undefined stereochemistry
            else if (stereo == CDKConstants.STEREO_BOND_UNDEFINED) {
                if (order == INCHI_BOND_TYPE.SINGLE) {
                    ibond.setStereoDefinition(INCHI_BOND_STEREO.SINGLE_1EITHER);
                } else if (order == INCHI_BOND_TYPE.DOUBLE) {
                    ibond.setStereoDefinition(INCHI_BOND_STEREO.DOUBLE_EITHER);
                }
            }
        }
        
        // Process atom parities (tetrahedral InChI Stereo0D Parities)
        atoms = atomContainer.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom atom = atoms.next();
            IAtomParity parity = atomContainer.getAtomParity(atom);
            if (parity != null) {
                IAtom[] surroundingAtoms = parity.getSurroundingAtoms();
                int sign = parity.getParity();
                
                JniInchiAtom atC = (JniInchiAtom) atomMap.get(atom);
                JniInchiAtom at0 = (JniInchiAtom) atomMap.get(surroundingAtoms[0]);
                JniInchiAtom at1 = (JniInchiAtom) atomMap.get(surroundingAtoms[1]);
                JniInchiAtom at2 = (JniInchiAtom) atomMap.get(surroundingAtoms[2]);
                JniInchiAtom at3 = (JniInchiAtom) atomMap.get(surroundingAtoms[3]);
                INCHI_PARITY p = INCHI_PARITY.UNKNOWN;
                if (sign > 0) {
                    p = INCHI_PARITY.EVEN;
                } else if (sign < 0) {
                    p = INCHI_PARITY.ODD;
                } else {
                    throw new CDKException("Atom parity of zero");
                }
                
                input.addStereo0D(new JniInchiStereo0D(atC, at0, at1, at2, at3,
                        INCHI_STEREOTYPE.TETRAHEDRAL, p));
            }
        }
        
        try {
            output = JniInchiWrapper.getInchi(input);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to generate InChI: " + jie.getMessage(), jie);
        }
    }
    
    
    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed.
     */
    @TestMethod("testGetInchiFromLandDAlanine3D,testGetInchiEandZ12Dichloroethene2D")
    public INCHI_RET getReturnStatus() {
        return(output.getReturnStatus());
    }
    
    /**
     * Gets generated InChI string.
     */
    @TestMethod("testGetInchiEandZ12Dichloroethene2D,testGetInchiFromEthyne,testGetInchiFromEthene")
    public String getInchi() {
        return(output.getInchi());
    }
    
    /**
     * Gets generated InChIKey string.
     */
    @TestMethod("testGetInchiFromEthane")
    public String getInchiKey() throws CDKException {
        JniInchiOutputKey key;
        try {
            key = JniInchiWrapper.getInChIKey(output.getInchi());
            if (key.getReturnStatus() == INCHI_KEY.OK) {
                return key.getKey();
            } else {
                throw new CDKException("Error while creating InChIKey: " +
                                       key.getReturnStatus());
            }
        } catch (JniInchiException exception) {
            throw new CDKException("Error while creating InChIKey: " +
                                   exception.getMessage(), exception);
        }
    }
    
    /**
     * Gets auxillary information.
     */
    public String getAuxInfo() {
        return(output.getAuxInfo());
    }
    
    /**
     * Gets generated (error/warning) messages.
     */
    public String getMessage() {
        return(output.getMessage());
    }
    
    /**
     * Gets generated log.
     */
    public String getLog() {
        return(output.getLog());
    }
}

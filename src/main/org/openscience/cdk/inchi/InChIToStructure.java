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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

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
@TestClass("org.openscience.cdk.inchi.InChIToStructureTest")
public class InChIToStructure {

protected JniInchiInputInchi input;
    
    protected JniInchiOutputStructure output;
    
    protected IAtomContainer molecule;
    
    /**
     * Constructor. Generates CDK AtomContainer from InChI.
     * @param inchi
     * @throws CDKException
     */
    @TestMethod("testConstructor_String_IChemObjectBuilder")
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
    @TestMethod("testGetAtomContainer_IChemObjectBuilder")
    protected void generateAtomContainerFromInchi(IChemObjectBuilder builder) throws CDKException {
        try {
            output = JniInchiWrapper.getStructureFromInchi(input);
        } catch (JniInchiException jie) {
            throw new CDKException("Failed to convert InChI to molecule: " + jie.getMessage(), jie);
        }
        
        //molecule = new AtomContainer();
        molecule = builder.newInstance(IAtomContainer.class);
        
        Map<JniInchiAtom, IAtom> inchiCdkAtomMap = new HashMap<JniInchiAtom, IAtom>();
        
        for (int i = 0; i < output.getNumAtoms(); i ++) {
            JniInchiAtom iAt = output.getAtom(i);
            IAtom cAt = builder.newInstance(IAtom.class);
            
            inchiCdkAtomMap.put(iAt, cAt);
            
            cAt.setID("a" + i);
            cAt.setSymbol(iAt.getElementType());
            
            // Ignore coordinates - all zero
            
            int charge = iAt.getCharge();
            if (charge != 0) {
                cAt.setFormalCharge(charge);
            }
            
            // hydrogenCount contains number of implict hydrogens, not
            // total number
            // Ref: Posting to cdk-devel list by Egon Willighagen 2005-09-17
            int numH = iAt.getImplicitH();
            if (numH != 0) {
                cAt.setImplicitHydrogenCount(numH);
            }
            
            molecule.addAtom(cAt);
        }
        
        for (int i = 0; i < output.getNumBonds(); i ++) {
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
                cBo.setOrder(CDKConstants.BONDORDER_SINGLE);
            } else if (type == INCHI_BOND_TYPE.DOUBLE) {
                cBo.setOrder(CDKConstants.BONDORDER_DOUBLE);
            } else if (type == INCHI_BOND_TYPE.TRIPLE) {
                cBo.setOrder(CDKConstants.BONDORDER_TRIPLE);
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
            else if (stereo == INCHI_BOND_STEREO.SINGLE_1EITHER
                  || stereo == INCHI_BOND_STEREO.DOUBLE_EITHER) {
                cBo.setStereo((IBond.Stereo)CDKConstants.UNSET);
            }
            
            molecule.addBond(cBo);
        }
        
        for (int i = 0; i < output.getNumStereo0D(); i ++) {
            JniInchiStereo0D stereo0d = output.getStereo0D(i);
            if (stereo0d.getStereoType() == INCHI_STEREOTYPE.TETRAHEDRAL) {
                JniInchiAtom central = stereo0d.getCentralAtom();
                JniInchiAtom[] neighbours = stereo0d.getNeighbors();
                
                IAtom atC = (IAtom) inchiCdkAtomMap.get(central);
                IAtom at0 = (IAtom) inchiCdkAtomMap.get(neighbours[0]);
                IAtom at1 = (IAtom) inchiCdkAtomMap.get(neighbours[1]);
                IAtom at2 = (IAtom) inchiCdkAtomMap.get(neighbours[2]);
                IAtom at3 = (IAtom) inchiCdkAtomMap.get(neighbours[3]);
                
                int sign;
                if (stereo0d.getParity() == INCHI_PARITY.ODD) {
                    sign = -1;
                } else if (stereo0d.getParity() == INCHI_PARITY.EVEN) {
                    sign = +1;
                } else {
                    // CDK Only supports parities of + or -
                    continue;
                }
                
                IAtomParity parity = builder.newInstance(IAtomParity.class,atC, at0, at1, at2, at3, sign);
                molecule.addStereoElement(parity);
            } else {
                // TODO - other types of atom parity - double bond, etc
            }
        }
    }
    
    /**
     * Returns generated molecule.
     * @return An AtomContainer object
     */
    @TestMethod("testGetAtomContainer")
    public IAtomContainer getAtomContainer() {
        return(molecule);
    }
    
    
    /**
     * Gets return status from InChI process.  OKAY and WARNING indicate
     * InChI has been generated, in all other cases InChI generation
     * has failed.
     */
    @TestMethod("testGetReturnStatus_EOF")
    public INCHI_RET getReturnStatus() {
        return(output.getReturnStatus());
    }
    
    /**
     * Gets generated (error/warning) messages.
     */
    @TestMethod("testGetMessage")
    public String getMessage() {
        return(output.getMessage());
    }
    
    /**
     * Gets generated log.
     */
    @TestMethod("testGetLog")
    public String getLog() {
        return(output.getLog());
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
    @TestMethod("testGetWarningFlags")
    public long[][] getWarningFlags() {
        return(output.getWarningFlags());
    }

}

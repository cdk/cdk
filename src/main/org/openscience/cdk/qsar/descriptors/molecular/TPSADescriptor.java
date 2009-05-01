/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Ring;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Calculation of topological polar surface area based on fragment
 * contributions (TPSA) {@cdk.cite ERTL2000}.
 * <p/>
 * <p>This descriptor uses these parameters:
 * <table border="1">
 * <tr>
 * <td>Name</td>
 * <td>Default</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>checkAromaticity</td>
 * <td>false</td>
 * <td>If true, it will check aromaticity</td>
 * </tr>
 * </table>
 * <p/>
 * This descriptor works properly with AtomContainers whose atoms contain either <b>explicit hydrogens</b> or
 * <b>implicit hydrogens</b>.
 * <p/>
 * Returns a single value named <i>TopoPSA</i>
 *
 * @author mfe4
 * @author ulif
 * @cdk.created 2004-11-03
 * @cdk.module qsarmolecular
 * @cdk.svnrev $Revision$
 * @cdk.set qsar-descriptors
 * @cdk.dictref qsar-descriptors:tpsa
 * @cdk.keyword TPSA
 * @cdk.keyword total polar surface area
 * @cdk.keyword descriptor
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptorTest")
public class TPSADescriptor implements IMolecularDescriptor {
    private boolean checkAromaticity = false;
    private static HashMap map;
    private static final String[] names = {"TopoPSA"};

    /**
     * Constructor for the TPSADescriptor object.
     */
    public TPSADescriptor() {
        if (map == null) {
            map = new HashMap();
            // contributions:
            // every contribution is given by an atom profile;
            // positions in atom profile strings are: symbol, max-bond-order, bond-order-sum,
            // number-of-neighbours, Hcount, formal charge, aromatic-bonds, is-in-3-membered-ring,
            // single-bonds, double-bonds, triple-bonds.

            map.put("N+1.0+3.0+3+0+0+0+0+3+0+0", new Double(3.24)); // 1
            map.put("N+2.0+3.0+2+0+0+0+0+1+1+0", new Double(12.36)); // 2
            map.put("N+3.0+3.0+1+0+0+0+0+0+0+1", new Double(23.79)); // 3
            map.put("N+2.0+5.0+3+0+0+0+0+1+2+0", new Double(11.68));  // 4
            map.put("N+3.0+5.0+2+0+0+0+0+0+1+1", new Double(13.6)); // 5
            map.put("N+1.0+3.0+3+0+0+0+1+3+0+0", new Double(3.01)); // 6
            map.put("N+1.0+3.0+3+1+0+0+0+3+0+0", new Double(12.03));  // 7
            map.put("N+1.0+3.0+3+1+0+0+1+3+0+0", new Double(21.94)); // 8
            map.put("N+2.0+3.0+2+1+0+0+0+1+1+0", new Double(23.85));  //9
            map.put("N+1.0+3.0+3+2+0+0+0+3+0+0", new Double(26.02));  // 10
            map.put("N+1.0+4.0+4+0+1+0+0+4+0+0", new Double(0.0));  //11
            map.put("N+2.0+4.0+3+0+1+0+0+2+1+0", new Double(3.01));  //12
            map.put("N+3.0+4.0+2+0+1+0+0+1+0+1", new Double(4.36));  //13
            map.put("N+1.0+4.0+4+1+1+0+0+4+0+0", new Double(4.44));  //14
            map.put("N+2.0+4.0+3+1+1+0+0+2+1+0", new Double(13.97));  //15
            map.put("N+1.0+4.0+4+2+1+0+0+4+0+0", new Double(16.61));  //16
            map.put("N+2.0+4.0+3+2+1+0+0+2+1+0", new Double(25.59));  //17
            map.put("N+1.0+4.0+4+3+1+0+0+4+0+0", new Double(27.64));  //18
            map.put("N+1.5+3.0+2+0+0+2+0+0+0+0", new Double(12.89));  //19
            map.put("N+1.5+4.5+3+0+0+3+0+0+0+0", new Double(4.41));  //20
            map.put("N+1.5+4.0+3+0+0+2+0+1+0+0", new Double(4.93));  //21
            map.put("N+2.0+5.0+3+0+0+2+0+0+1+0", new Double(8.39));  //22
            map.put("N+1.5+4.0+3+1+0+2+0+1+0+0", new Double(15.79));  //23
            map.put("N+1.5+4.5+3+0+1+3+0+0+0+0", new Double(4.1));  //24
            map.put("N+1.5+4.0+3+0+1+2+0+1+0+0", new Double(3.88));  //25
            map.put("N+1.5+4.0+3+1+1+2+0+1+0+0", new Double(14.14));  //26

            map.put("O+1.0+2.0+2+0+0+0+0+2+0+0", new Double(9.23));  //27
            map.put("O+1.0+2.0+2+0+0+0+1+2+0+0", new Double(12.53));  //28
            map.put("O+2.0+2.0+1+0+0+0+0+0+1+0", new Double(17.07));  //29
            map.put("O+1.0+1.0+1+0+-1+0+0+1+0+0", new Double(23.06));  //30
            map.put("O+1.0+2.0+2+1+0+0+0+2+0+0", new Double(20.23));  //31
            map.put("O+1.5+3.0+2+0+0+2+0+0+0+0", new Double(13.14));  //32

            map.put("S+1.0+2.0+2+0+0+0+0+2+0+0", new Double(25.3));  //33
            map.put("S+2.0+2.0+1+0+0+0+0+0+1+0", new Double(32.09));  //34
            map.put("S+2.0+4.0+3+0+0+0+0+2+1+0", new Double(19.21));  //35
            map.put("S+2.0+6.0+4+0+0+0+0+2+2+0", new Double(8.38));  //36
            map.put("S+1.0+2.0+2+1+0+0+0+2+0+0", new Double(38.8));  //37
            map.put("S+1.5+3.0+2+0+0+2+0+0+0+0", new Double(28.24));  //38
            map.put("S+2.0+5.0+3+0+0+2+0+0+1+0", new Double(21.7));  //39

            map.put("P+1.0+3.0+3+0+0+0+0+3+0+0", new Double(13.59));  //40
            map.put("P+2.0+3.0+3+0+0+0+0+1+1+0", new Double(34.14));  //41
            map.put("P+2.0+5.0+4+0+0+0+0+3+1+0", new Double(9.81));  //42
            map.put("P+2.0+5.0+4+1+0+0+0+3+1+0", new Double(23.47));  //43
        }
    }


    /**
     * Gets the specification attribute of the TPSADescriptor object.
     *
     * @return The specification value
     */
    @TestMethod("testGetSpecification")
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#tpsa",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    /**
     * Sets the parameters attribute of the  TPSADescriptor object.
     * <p/>
     * The descriptor takes a Boolean parameter to indicate whether
     * the descriptor routine should check for aromaticity (TRUE) or
     * not (FALSE).
     *
     * @param params The parameter value (TRUE or FALSE)
     * @throws CDKException if the supplied parameter is not of type Boolean
     * @see #getParameters
     */
    @TestMethod("testSetParameters_arrayObject")
    public void setParameters(Object[] params) throws CDKException {
        if (params.length != 1) {
            throw new CDKException("TPSADescriptor expects one parameter");
        }
        if (!(params[0] instanceof Boolean)) {
            throw new CDKException("The first parameter must be of type Boolean");
        }
        // ok, all should be fine
        checkAromaticity = (Boolean) params[0];
    }


    /**
     * Gets the parameters attribute of the TPSADescriptor object.
     *
     * @return The parameter value. For this descriptor it returns a Boolean
     *         indicating whether aromaticity was to be checked or not
     * @see #setParameters
     */
    @TestMethod("testGetParameters")
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = checkAromaticity;
        return params;

    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return names;
    }


    private DescriptorValue getDummyDescriptorValue(Exception e) {
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), new DoubleResult(Double.NaN), getDescriptorNames(), e);
    }

    /**
     * Calculates the TPSA for an atom container.
     * <p/>
     * Before calling this method, you may want to set the parameter
     * indicating that aromaticity should be checked. If no parameter is specified
     * (or if it is set to FALSE) then it is assumed that aromaticaity has already been
     * checked.
     * <p/>
     * Prior to calling this method it is necessary to either add implicit or explicit hydrogens
     * using {@link CDKHydrogenAdder#addImplicitHydrogens(IAtomContainer)} or
     * {@link AtomContainerManipulator#convertImplicitToExplicitHydrogens(IAtomContainer)}.
     *
     * @param atomContainer The AtomContainer whose TPSA is to be calculated
     * @return A double containing the topological surface area
     */
    @TestMethod("testCalculate_IAtomContainer")
    public DescriptorValue calculate(IAtomContainer atomContainer) {
        IAtomContainer ac;
        try {
            ac = (IAtomContainer) atomContainer.clone();
        } catch (CloneNotSupportedException e) {
            return getDummyDescriptorValue(e);
        }
        List<String> profiles = new ArrayList<String>();

        // calculate the set of all rings
        IRingSet rs;
        try {
            rs = (new AllRingsFinder()).findAllRings(ac);
        } catch (CDKException e) {
            return getDummyDescriptorValue(e);
        }
        // check aromaticity if the descriptor parameter is set to true
        if (checkAromaticity) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);
                CDKHueckelAromaticityDetector.detectAromaticity(ac);
            } catch (CDKException e) {
                return getDummyDescriptorValue(e);
            }
        }

        // iterate over all atoms of ac
        java.util.Iterator atoms = ac.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getSymbol().equals("N") || atom.getSymbol().equals("O") ||
                    atom.getSymbol().equals("S") || atom.getSymbol().equals("P")) {
                int singleBondCount = 0;
                int doubleBondCount = 0;
                int tripleBondCount = 0;
                int aromaticBondCount = 0;
                double maxBondOrder = 0;
                double bondOrderSum = 0;
                int hCount = 0;
                int isIn3MemberRing = 0;

                // counting the number of single/double/triple/aromatic bonds
                List<IBond> connectedBonds = ac.getConnectedBondsList(atom);
                for (IBond connectedBond : connectedBonds) {
                    if (connectedBond.getFlag(CDKConstants.ISAROMATIC))
                        aromaticBondCount++;
                    else if (connectedBond.getOrder() == CDKConstants.BONDORDER_SINGLE)
                        singleBondCount++;
                    else if (connectedBond.getOrder() == CDKConstants.BONDORDER_DOUBLE)
                        doubleBondCount++;
                    else if (connectedBond.getOrder() == CDKConstants.BONDORDER_TRIPLE)
                        tripleBondCount++;
                }
                int formalCharge = atom.getFormalCharge();
                java.util.List connectedAtoms = ac.getConnectedAtomsList(atom);
                int numberOfNeighbours = connectedAtoms.size();

                // EXPLICIT hydrogens: count the number of hydrogen atoms
                for (int neighbourIndex = 0; neighbourIndex < numberOfNeighbours; neighbourIndex++)
                    if (((IAtom) connectedAtoms.get(neighbourIndex)).getSymbol().equals("H"))
                        hCount++;
                // IMPLICIT hydrogens: count the number of hydrogen atoms and adjust other atom profile properties
                Integer implicitHAtoms = atom.getHydrogenCount();
                if (implicitHAtoms == CDKConstants.UNSET) {
                    implicitHAtoms = 0;
                }

                for (int hydrogenIndex = 0; hydrogenIndex < implicitHAtoms; hydrogenIndex++) {
                    hCount++;
                    numberOfNeighbours++;
                    singleBondCount++;
                }
                // Calculate bond order sum using the counters of single/double/triple/aromatic bonds
                bondOrderSum += singleBondCount * 1.0;
                bondOrderSum += doubleBondCount * 2.0;
                bondOrderSum += tripleBondCount * 3.0;
                bondOrderSum += aromaticBondCount * 1.5;
                // setting maxBondOrder
                if (singleBondCount > 0)
                    maxBondOrder = 1.0;
                if (aromaticBondCount > 0)
                    maxBondOrder = 1.5;
                if (doubleBondCount > 0)
                    maxBondOrder = 2.0;
                if (tripleBondCount > 0)
                    maxBondOrder = 3.0;

                // isIn3MemberRing checker
                if (rs.contains(atom)) {
                    IRingSet rsAtom = rs.getRings(atom);
                    for (int ringSetIndex = 0; ringSetIndex < rsAtom.getAtomContainerCount(); ringSetIndex++) {
                        Ring ring = (Ring) rsAtom.getAtomContainer(ringSetIndex);
                        if (ring.getRingSize() == 3)
                            isIn3MemberRing = 1;
                    }
                }
                // create a profile of the current atom (atoms[atomIndex]) according to the profile definition in the constructor
                String profile = atom.getSymbol() + "+" + maxBondOrder + "+" + bondOrderSum + "+" +
                        numberOfNeighbours + "+" + hCount + "+" + formalCharge + "+" + aromaticBondCount + "+" +
                        isIn3MemberRing + "+" + singleBondCount + "+" + doubleBondCount + "+" + tripleBondCount;
                //logger.debug("tpsa profile: "+ profile);
                profiles.add(profile);
            }
        }
        // END OF ATOM LOOP
        // calculate the tpsa for the AtomContainer ac
        double tpsa = 0;
        for (int profileIndex = 0; profileIndex < profiles.size(); profileIndex++) {
            if (map.containsKey(profiles.get(profileIndex))) {
                tpsa += (Double) map.get(profiles.get(profileIndex));
                //logger.debug("tpsa contribs: " + profiles.elementAt(profileIndex) + "\t" + ((Double)map.get(profiles.elementAt(profileIndex))).doubleValue());
            }
        }
        profiles.clear(); // remove all profiles from the profiles-Vector
        //logger.debug("tpsa: " + tpsa);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(),
                new DoubleResult(tpsa), getDescriptorNames());

    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleResult(0.0);
    }

    /**
     * Gets the parameterNames attribute of the  TPSADescriptor object.
     *
     * @return The parameterNames value
     */
    @TestMethod("testGetParameterNames")
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "checkAromaticity";
        return params;
    }


    /**
     * Gets the parameterType attribute of the TPSADescriptor object.
     *
     * @param name Description of the Parameter
     * @return The parameterType value
     */
    @TestMethod("testGetParameterType_String")
    public Object getParameterType(String name) {
        return true;
    }
}

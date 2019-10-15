/* Copyright (C) 2006-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.AbstractAtomicDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.HOSECodeGenerator;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.LonePairElectronChecker;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 *  This class returns the proton affinity of an atom containing.
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * @author       Miguel Rojas
 * @cdk.created  2006-05-26
 * @cdk.module   qsaratomic
 * @cdk.githash
 * @cdk.dictref  qsar-descriptors:protonaffinity
 */
public class ProtonAffinityHOSEDescriptor extends AbstractAtomicDescriptor {

    private static final String[] NAMES = {"protonAffiHOSE"};

    /** Maximum spheres to use by the HoseCode model.*/
    int                           maxSpheresToUse = 10;

    private Affinitydb            db              = new Affinitydb();

    /**
     *  Constructor for the ProtonAffinityDescriptor object.
     */
    public ProtonAffinityHOSEDescriptor() {}

    /**
     *  Gets the specification attribute of the ProtonAffinityDescriptor object
     *
     *@return    The specification value
     */
    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ionizationPotential", this
                        .getClass().getName(), "The Chemistry Development Kit");
    }

    /**
     * This descriptor does have any parameter.
     */
    @Override
    public void setParameters(Object[] params) throws CDKException {}

    /**
     *  Gets the parameters attribute of the ProtonAffinityDescriptor object.
     *
     *@return    The parameters value
     * @see #setParameters
     */
    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return NAMES;
    }

    /**
     *  This method calculates the protonation affinity of an atom.
     *
     *@param  atom              The IAtom to protonate
     *@param  container         Parameter is the IAtomContainer.
     *@return                   The protonation affinity. Not possible the ionization.
     */
    @Override
    public DescriptorValue calculate(IAtom atom, IAtomContainer container) {
        double value;

        try {
            int i = container.indexOf(atom);
            if (i < 0) throw new CDKException("atom was not a memeber of the provided container");

            // don't modify the original
            container = container.clone();
            atom = container.getAtom(i);

            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
            LonePairElectronChecker lpcheck = new LonePairElectronChecker();
            lpcheck.saturate(container);
        } catch (CDKException | CloneNotSupportedException e) {
            return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(
                    Double.NaN), NAMES, null);
        }

        value = db.extractAffinity(container, atom);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(value),
                                   NAMES);

    }

    /**
     * Looking if the Atom belongs to the halogen family.
     *
     * @param  atom  The IAtom
     * @return       True, if it belongs
     */
    private boolean familyHalogen(IAtom atom) {
        String symbol = atom.getSymbol();
        return symbol.equals("F") || symbol.equals("Cl") || symbol.equals("Br") || symbol.equals("I");
    }

    /**
    * Gets the parameterNames attribute of the ProtonAffinityDescriptor object.
    *
    * @return    The parameterNames value
    */
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    /**
     * Gets the parameterType attribute of the ProtonAffinityDescriptor object.
     *
     * @param  name  Description of the Parameter
     * @return       An Object of class equal to that of the parameter being requested
     */
    @Override
    public Object getParameterType(String name) {
        return null;
    }

    /**
     * Class defining the database containing the relation between the energy for ionizing and the HOSEcode
     * fingerprints
     *
     * @author Miguel Rojas
     *
     */
    private class Affinitydb {

        public static final String X_AFFI_PROTON_HOSE_DB = "/org/openscience/cdk/qsar/descriptors/atomic/data/X_AffiProton_HOSE.db";
        public static final String X_AFFI_PROTON_HOSE_S_DB = "/org/openscience/cdk/qsar/descriptors/atomic/data/X_AffiProton_HOSE_S.db";

        /**
         * The constructor of the IPdb.
         *
         */
        public Affinitydb() {

        }

        /**
         * extract from the db the proton affinity.
         *
         * @param container  The IAtomContainer
         * @param atom       The IAtom
         * @return           The energy value
         */
        public double extractAffinity(IAtomContainer container, IAtom atom) {
            // loading the files if they are not done
            HashMap<String, Double> hoseVSenergy;
            HashMap<String, Double> hoseVSenergyS;

            if (familyHalogen(atom)) {
                try (InputStream ins = getClass().getResourceAsStream(X_AFFI_PROTON_HOSE_DB);
                     BufferedReader insr = new BufferedReader(new InputStreamReader(ins))) {
                    hoseVSenergy = extractAttributes(insr);
                } catch (IOException e) {
                    LoggingToolFactory.createLoggingTool(getClass()).error(e);
                    return 0;
                }
                try (InputStream ins = getClass().getResourceAsStream(X_AFFI_PROTON_HOSE_S_DB);
                     BufferedReader insr = new BufferedReader(new InputStreamReader(ins))) {
                    hoseVSenergyS = extractAttributes(insr);
                } catch (IOException e) {
                    LoggingToolFactory.createLoggingTool(getClass()).error(e);
                    return 0;
                }
            } else
                return 0;

            try {
                HOSECodeGenerator hcg = new HOSECodeGenerator();
                //Check starting from the exact sphere hose code and maximal a value of 10
                int exactSphere = 0;
                String hoseCode = "";
                for (int spheres = maxSpheresToUse; spheres > 0; spheres--) {
                    hcg.getSpheres(container, atom, spheres, true);
                    List<IAtom> atoms = hcg.getNodesInSphere(spheres);
                    if (atoms.size() != 0) {
                        exactSphere = spheres;
                        hoseCode = hcg.getHOSECode(container, atom, spheres, true);
                        if (hoseVSenergy.containsKey(hoseCode)) {
                            return hoseVSenergy.get(hoseCode);
                        }
                        if (hoseVSenergyS.containsKey(hoseCode)) {
                            return hoseVSenergyS.get(hoseCode);
                        }
                        break;
                    }
                }
                //Check starting from the rings bigger and smaller
                //TODO:IP: Better application
                for (int i = 0; i < 3; i++) { // two rings
                    for (int plusMinus = 0; plusMinus < 2; plusMinus++) { // plus==bigger, minus==smaller
                        int sign = -1;
                        if (plusMinus == 1) sign = 1;

                        StringTokenizer st = new StringTokenizer(hoseCode, "()/");
                        StringBuilder hoseCodeBuffer = new StringBuilder();
                        int sum = exactSphere + sign * (i + 1);
                        for (int k = 0; k < sum; k++) {
                            if (st.hasMoreTokens()) {
                                String partcode = st.nextToken();
                                hoseCodeBuffer.append(partcode);
                            }
                            if (k == 0) {
                                hoseCodeBuffer.append('(');
                            } else if (k == 3) {
                                hoseCodeBuffer.append(')');
                            } else {
                                hoseCodeBuffer.append('/');
                            }
                        }
                        String hoseCodeBU = hoseCodeBuffer.toString();

                        if (hoseVSenergyS.containsKey(hoseCodeBU)) {
                            return hoseVSenergyS.get(hoseCodeBU);
                        }
                    }
                }
            } catch (CDKException e) {
                e.printStackTrace();
            }
            return 0;
        }

        /**
         * Extract the Hose code and energy
         *
         * @param input  The BufferedReader
         * @return       HashMap with the Hose vs energy attributes
         */
        private HashMap<String, Double> extractAttributes(BufferedReader input) {
            HashMap<String, Double> hoseVSenergy = new HashMap<>();
            String line;

            try {
                while ((line = input.readLine()) != null) {
                    if (line.startsWith("#")) continue;
                    List<String> values = extractInfo(line);
                    if (values.get(1).isEmpty()) continue;
                    hoseVSenergy.put(values.get(0), Double.valueOf(values.get(1)));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return hoseVSenergy;
        }
    }

    /**
     * Extract the information from a line which contains HOSE_ID & energy.
     *
     * @param str  String with the information
     * @return     List with String = HOSECode and String = energy
     */
    private static List<String> extractInfo(String str) {
        int beg = 0;
        int end = 0;
        int len = str.length();
        List<String> parts = new ArrayList<>();
        while (end < len && !Character.isSpaceChar(str.charAt(end)))
            end++;
        parts.add(str.substring(beg,end));
        while (end < len && Character.isSpaceChar(str.charAt(end)))
            end++;
        beg = end;
        while (end < len && !Character.isSpaceChar(str.charAt(end)))
            end++;
        parts.add(str.substring(beg,end));
        return parts;
    }
}

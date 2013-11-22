/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.graph.invariant;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.Arrays;

/**
 * Tool for calculating atom numbers using the InChI algorithm.
 *
 * @cdk.module  inchi
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.graph.invariant.InChINumbersToolsTest")
public class InChINumbersTools {

    /**
     * Makes an array containing the InChI atom numbers of the non-hydrogen
     * atoms in the atomContainer. It returns zero for all hydrogens.
     *
     * @param  atomContainer  The {@link IAtomContainer} to analyze.
     * @return                The number from 1 to the number of heavy atoms.
     * @throws CDKException   When the InChI could not be generated
     */
    @TestMethod("testSimpleNumbering,testHydrogens,testGlycine")
    public static long[] getNumbers(IAtomContainer atomContainer) throws CDKException {
        String aux =  auxInfo(atomContainer);
        aux = aux.substring(aux.indexOf("/N:") + 3);
        String numberStringAux = aux.substring(0, aux.indexOf("/"));
        int i = 1; 
        long[] numbers = new long[atomContainer.getAtomCount()];
        for (String numberString : numberStringAux.split("\\,"))
            numbers[Integer.valueOf(numberString)-1] = i++;
        return numbers;        
    }

    /**
     * Obtain the InChI numbers for the input container to be used to order
     * atoms in Universal SMILES. The numbers are obtained using the
     * fixedH and RecMet options of the InChI. All non-bridged hydrogens
     * are labelled as 0.
     * 
     * @param container the structure to obtain the numbers of
     * @return the atom numbers
     * @throws CDKException
     */
    @TestMethod("testGlycine_uSmiles")
    public static long[] getUSmilesNumbers(IAtomContainer container) throws CDKException {
        String aux     = auxInfo(container, INCHI_OPTION.RecMet, INCHI_OPTION.FixedH);        
        long[] numbers = new long[container.getAtomCount()];
        return parseUSmilesNumbers(aux, numbers);
    }

    /**
     * Parse the InChI canonical atom numbers (from the AuxInfo) to use in
     * Universal SMILES. 
     * 
     * The parsing follows: "Rule A: The correspondence between the input atom
     * order and the InChI canonical labels should be obtained from the
     * reconnected metal layer (/R:) in preference to the initial layer, and
     * then from the fixed hydrogen labels (/F:) in preference to the standard
     * labels (/N:)." 
     * 
     * @param aux     inchi AuxInfo
     * @param numbers array to fill with the numbers (indexed by atom)           
     * @return the numbers string to use
     */
    @TestMethod("parseStandard,parseRecMet,parseFixedH")
    static long[] parseUSmilesNumbers(String aux, long[] numbers) {
        int index;
        
        int label = 1;
        
        if ((index = aux.indexOf("/R:")) >= 0) { // reconnected metal numbers
            String[] baseNumbers = aux.substring(index + 8, aux.indexOf('/', index + 8)).split(";");
            for (String component : baseNumbers) {
                for (String number : component.split(",")) {
                    numbers[Integer.parseInt(number)- 1 ] = label++;
                }
            }
        } else if ((index = aux.indexOf("/N:")) >= 0) { // standard numbers
            
            // read the standard numbers first (need to reference back for some structures)
            String[] baseNumbers = aux.substring(index + 3, aux.indexOf('/', index + 3)).split(";");
            
            if ((index = aux.indexOf("/F:")) >= 0) {
                String[] fixedHNumbers = aux.substring(index + 3, aux.indexOf('/', index + 3)).split(";");
                for (int i = 0; i < fixedHNumbers.length; i++) {

                    String component = fixedHNumbers[i];

                    // m, 2m, 3m ... need to lookup number in the base numbering
                    if (component.charAt(component.length() - 1) == 'm') {
                        int n = component.length() > 1 ? Integer.parseInt(component.substring(0, component.length()-1))
                                                       : 1;
                        for (int j = 0; j < n; j++)
                            for (String number : baseNumbers[i + j].split(","))
                                numbers[Integer.parseInt(number) - 1] = label++;
                    }
                    else {
                        for (String number : component.split(",")) 
                            numbers[Integer.parseInt(number) - 1] = label++;
                    }
                }
            } else {
                for (String component : baseNumbers)
                    for (String number : component.split(","))
                        numbers[Integer.parseInt(number)- 1 ] = label++;
            }
        } else {
            throw new IllegalArgumentException("AuxInfo did not contain extractable base numbers (/N: or /R:).");
        }

        return numbers;
    }
    
    /**
     * Obtain the InChI auxiliary info for the provided structure using 
     * using the specified InChI options.
     *
     * @param  container the structure to obtain the numbers of
     * @return auxiliary info
     * @throws CDKException the inchi could not be generated
     */
    @TestMethod("fixedH")
    static String auxInfo(IAtomContainer container, INCHI_OPTION... options) throws CDKException {
        long[] atomNumbers = new long[container.getAtomCount()];
        InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
        InChIGenerator gen = factory.getInChIGenerator(container, Arrays.asList(options));
        if (gen.getReturnStatus() != INCHI_RET.OKAY && gen.getReturnStatus() != INCHI_RET.WARNING)
            throw new CDKException("Could not generate InChI Numbers: " + gen.getMessage());
        return gen.getAuxInfo();
    }

}


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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import java.util.Arrays;
import java.util.List;

/**
 * Tool for calculating atom numbers using the InChI algorithm.
 *
 * @cdk.module  inchi
 * @cdk.githash
 */
public class InChINumbersTools {

    /**
     * Makes an array containing the InChI atom numbers of the non-hydrogen
     * atoms in the atomContainer. It returns zero for all hydrogens.
     *
     * @param  atomContainer  The {@link IAtomContainer} to analyze.
     * @return                The number from 1 to the number of heavy atoms.
     * @throws CDKException   When the InChI could not be generated
     */
    public static long[] getNumbers(IAtomContainer atomContainer) throws CDKException {
        String aux = auxInfo(atomContainer);
        long[] numbers = new long[atomContainer.getAtomCount()];
        parseAuxInfo(aux, numbers);
        return numbers;
    }

    /**
     * Parse the atom numbering from the auxinfo.
     *
     * @param aux InChI AuxInfo
     * @param numbers the atom numbers
     */
    public static void parseAuxInfo(String aux, long[] numbers) {
        aux = aux.substring(aux.indexOf("/N:") + 3);
        String numberStringAux = aux.substring(0, aux.indexOf('/'));
        int i = 1;
        for (String numberString : numberStringAux.split("[,;]"))
            numbers[Integer.valueOf(numberString) - 1] = i++;
    }

    /**
     * Obtain the InChI numbers for the input container to be used to order
     * atoms in Universal SMILES {@cdk.cite OBoyle12}. The numbers are obtained
     * using the fixedH and RecMet options of the InChI. All non-bridged
     * hydrogens are labelled as 0.
     *
     * @param container the structure to obtain the numbers of
     * @return the atom numbers
     * @throws CDKException
     */
    public static long[] getUSmilesNumbers(IAtomContainer container) throws CDKException {
        String aux = auxInfo(container, INCHI_OPTION.RecMet, INCHI_OPTION.FixedH);
        return parseUSmilesNumbers(aux, container);
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
     * The labels are also adjust for "Rule E: If the start atom is a negatively
     * charged oxygen atom, start instead at any carbonyl oxygen attached to the
     * same neighbour." 
     *
     * All unlabelled atoms (e.g. hydrogens) are assigned the same label which
     * is different but larger then all other labels. The hydrogen
     * labelling then needs to be adjusted externally as universal SMILES
     * suggests hydrogens should be visited first.
     *
     * @param aux       inchi AuxInfo
     * @param container the structure to obtain the numbering of
     * @return the numbers string to use
     */
    static long[] parseUSmilesNumbers(String aux, IAtomContainer container) {

        int index;
        long[] numbers = new long[container.getAtomCount()];
        int[] first = null;
        int label = 1;

        if ((index = aux.indexOf("/R:")) >= 0) { // reconnected metal numbers
            int endIndex = aux.indexOf('/', index + 8);
            if (endIndex<0)
                endIndex = aux.length();
            String[] baseNumbers = aux.substring(index + 8, endIndex).split(";");
            first = new int[baseNumbers.length];
            Arrays.fill(first, -1);
            for (int i = 0; i < baseNumbers.length; i++) {
                String[] numbering = baseNumbers[i].split(",");
                first[i] = Integer.parseInt(numbering[0]) - 1;
                for (String number : numbering) {
                    numbers[Integer.parseInt(number) - 1] = label++;
                }
            }
        } else if ((index = aux.indexOf("/N:")) >= 0) { // standard numbers

            // read the standard numbers first (need to reference back for some structures)
            String[] baseNumbers = aux.substring(index + 3, aux.indexOf('/', index + 3)).split(";");
            first = new int[baseNumbers.length];
            Arrays.fill(first, -1);

            if ((index = aux.indexOf("/F:")) >= 0) {
                String[] fixedHNumbers = aux.substring(index + 3, aux.indexOf('/', index + 3)).split(";");
                for (int i = 0; i < fixedHNumbers.length; i++) {

                    String component = fixedHNumbers[i];

                    // m, 2m, 3m ... need to lookup number in the base numbering
                    if (component.charAt(component.length() - 1) == 'm') {
                        int n = component.length() > 1 ? Integer
                                .parseInt(component.substring(0, component.length() - 1)) : 1;
                        for (int j = 0; j < n; j++) {
                            String[] numbering = baseNumbers[i + j].split(",");
                            first[i + j] = Integer.parseInt(numbering[0]) - 1;
                            for (String number : numbering)
                                numbers[Integer.parseInt(number) - 1] = label++;
                        }
                    } else {
                        String[] numbering = component.split(",");
                        first[i] = Integer.parseInt(numbering[0]) - 1;
                        for (String number : numbering)
                            numbers[Integer.parseInt(number) - 1] = label++;
                    }
                }
            } else {
                for (int i = 0; i < baseNumbers.length; i++) {
                    String[] numbering = baseNumbers[i].split(",");
                    first[i] = Integer.parseInt(numbering[0]) - 1;
                    for (String number : numbering)
                        numbers[Integer.parseInt(number) - 1] = label++;
                }
            }
        } else {
            throw new IllegalArgumentException("AuxInfo did not contain extractable base numbers (/N: or /R:).");
        }

        // Rule E: swap any oxygen anion for a double bonded oxygen (InChI sees
        // them as equivalent)
        for (int v : first) {
            if (v >= 0) {
                IAtom atom = container.getAtom(v);
                if (atom.getFormalCharge() == null) continue;
                if (atom.getAtomicNumber() == 8 && atom.getFormalCharge() == -1) {
                    List<IAtom> neighbors = container.getConnectedAtomsList(atom);
                    if (neighbors.size() == 1) {
                        IAtom correctedStart = findPiBondedOxygen(container, neighbors.get(0));
                        if (correctedStart != null) exch(numbers, v, container.getAtomNumber(correctedStart));
                    }
                }
            }
        }

        // assign unlabelled atoms
        for (int i = 0; i < numbers.length; i++)
            if (numbers[i] == 0) numbers[i] = label++;

        return numbers;
    }

    /**
     * Exchange the elements at index i with that at index j.
     *
     * @param values an array of values
     * @param i an index
     * @param j another index
     */
    private static void exch(long[] values, int i, int j) {
        long k = values[i];
        values[i] = values[j];
        values[j] = k;
    }

    /**
     * Find a neutral oxygen bonded to the {@code atom} with a pi bond.
     *
     * @param container the container
     * @param atom      an atom from the container
     * @return a pi bonded oxygen (or null if not found)
     */
    private static IAtom findPiBondedOxygen(IAtomContainer container, IAtom atom) {
        for (IBond bond : container.getConnectedBondsList(atom)) {
            if (bond.getOrder() == IBond.Order.DOUBLE) {
                IAtom neighbor = bond.getConnectedAtom(atom);
                int charge = neighbor.getFormalCharge() == null ? 0 : neighbor.getFormalCharge();
                if (neighbor.getAtomicNumber() == 8 && charge == 0) return neighbor;
            }
        }
        return null;
    }

    /**
     * Obtain the InChI auxiliary info for the provided structure using
     * using the specified InChI options.
     *
     * @param  container the structure to obtain the numbers of
     * @return auxiliary info
     * @throws CDKException the inchi could not be generated
     */
    static String auxInfo(IAtomContainer container, INCHI_OPTION... options) throws CDKException {
        InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
        boolean org = factory.getIgnoreAromaticBonds();
        factory.setIgnoreAromaticBonds(true);
        InChIGenerator gen = factory.getInChIGenerator(container, Arrays.asList(options));
        factory.setIgnoreAromaticBonds(org); // an option on the singleton so we should reset for others
        if (gen.getReturnStatus() != INCHI_RET.OKAY && gen.getReturnStatus() != INCHI_RET.WARNING)
            throw new CDKException("Could not generate InChI Numbers: " + gen.getMessage());
        return gen.getAuxInfo();
    }

}

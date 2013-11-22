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


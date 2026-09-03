/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.tautomer;

import org.openscience.cdk.Element;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainSubsearch {
    public static void main(String[] args) throws FileNotFoundException {
        TautSubSearch pattern = new TautSubSearch(SilentChemObjectBuilder.getInstance(), "n2ccc1c2cccc1");
//        SmartsPattern pattern = SmartsPattern.create("n2ccc1c2cccc1");
//        pattern.setPrepare(false);

        FileReader reader = new FileReader("/data/chembl_33.smi");
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        int molecules = 0;
        int hits = 0;
        int skipped   = 0;
        long tBeg = System.nanoTime();
        long tMatch = 0;
        Aromaticity aromaticity = new Aromaticity(ElectronDonation.daylight(), Cycles.or(Cycles.all(), Cycles.all(6)));
        try (BufferedReader rdr = new BufferedReader(reader)) {
            String line;
            while ((line = rdr.readLine()) != null) {
                try {
                    IAtomContainer mol = smilesParser.parseSmiles(line);
                    ++molecules;
                    // prep
                    Cycles.markRingAtomsAndBonds(mol);
//                    try {
//                        Cycles.markRingAtomsAndBonds(mol);
//                        aromaticity.apply(mol);
//                    } catch (CDKException e) {
//                        throw new RuntimeException(e);
//                    }
                    long t0 = System.nanoTime();
                    if (pattern.matches(mol))
                        hits++;
                    long t1 = System.nanoTime();
                    tMatch += (t1-t0);
                } catch (InvalidSmilesException e) {
                    throw new RuntimeException(e);
                }
                if (molecules % 500 == 0) {
                    System.err.printf("\r%d %d %.3fs total (%.3f s) %d skipped",
                                      molecules,
                                      hits,
                                      (System.nanoTime()-tBeg)/1e9,
                                      (tMatch/1e9),
                                      skipped);
                }
            }
            System.err.printf("\rDONE: %d %d %.3fs total (%.3f s) %d skipped",
                              molecules,
                              hits,
                              (System.nanoTime()-tBeg)/1e9,
                              (tMatch/1e9),
                              skipped);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

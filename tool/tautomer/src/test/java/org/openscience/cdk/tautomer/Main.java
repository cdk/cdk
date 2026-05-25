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

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        FileReader reader = new FileReader("/data/chembl_33.smi");
        SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
        int molecules = 0;
        int tautomers = 0;
        int skipped   = 0;
        long tBeg = System.nanoTime();
        long tTautomers = 0;
        try (BufferedReader rdr = new BufferedReader(reader)) {
            String line;
            while ((line = rdr.readLine()) != null) {
                try {
                    IAtomContainer mol = smilesParser.parseSmiles(line);
                    ++molecules;
                    if (mol.getAtomCount() > 64) {
                        skipped++;
                        continue;
                    }
                    long t0 = System.nanoTime();
                    for (IAtomContainer tautomer : Tautomers.hetero(mol, Tautomers.Order.SEQUENTIAL)) {
                        tautomers++;
                    }
                    long t1 = System.nanoTime();
                    tTautomers += (t1-t0);
                } catch (InvalidSmilesException e) {
                    throw new RuntimeException(e);
                }
                if (molecules % 500 == 0) {
                    System.err.printf("\r%d %d %.3fs total (%.3fs-1) %d skipped",
                                      molecules,
                                      tautomers,
                                      (System.nanoTime()-tBeg)/1e9,
                                      tautomers/(tTautomers/1e9),
                                      skipped);
                }
            }
            System.err.printf("\r%d %d %.3fs total (%.3fs-1) %d skipped",
                              molecules,
                              tautomers,
                              (System.nanoTime()-tBeg)/1e9,
                              tautomers/(tTautomers/1e9),
                              skipped);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

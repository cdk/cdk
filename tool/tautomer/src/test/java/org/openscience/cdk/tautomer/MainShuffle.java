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

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;

public class MainShuffle {
    public static void main(String[] args) throws IOException {
        for (int i=0; i<60; i++) {
            FileReader reader = new FileReader("/data/briem-lessel.smi");
            SmilesParser smilesParser = new SmilesParser(SilentChemObjectBuilder.getInstance());
            SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric);
            int molecules = 0;
            int tautomers = 0;
            int skipped = 0;
            long tBeg = System.nanoTime();
            long tTautomers = 0;
            try (BufferedReader rdr = new BufferedReader(reader);
                 BufferedWriter bwtr = new BufferedWriter(new FileWriter("/data/briem-lessel-kt" + i + ".smi"));) {
                String line;
                while ((line = rdr.readLine()) != null) {
                    try {
                        IAtomContainer mol = smilesParser.parseSmiles(line);
                        if (mol.getAtomCount() >= 64) {
                            skipped++;
                            continue;
                        }
                        boolean okay = false;
                        long t0 = System.nanoTime();
                        for (IAtomContainer tautomer : Tautomers.hetero(mol, EnumSet.of(Tautomers.Type.CARBON_SHIFTS), Tautomers.Order.RANDOM)) {
                            mol = tautomer;
                            tautomers++;
                            break;
                        }
                        long t1 = System.nanoTime();
                        bwtr.write(smigen.create(mol) + "\t" + mol.getTitle() + "\n");
                        tTautomers += (t1 - t0);
                    } catch (Exception e) {
                        System.err.println(line);
                        throw new RuntimeException(e);
                    }
                    if (++molecules % 500 == 0) {
                        System.err.printf("\r%d %d %.3fs (%.3fs-1) ",
                                          molecules,
                                          tautomers,
                                          (System.nanoTime() - tBeg) / 1e9,
                                          tautomers / (tTautomers / 1e9));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

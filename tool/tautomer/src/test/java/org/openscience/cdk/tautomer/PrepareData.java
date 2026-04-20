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

import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PrepareData {

    private static final int ATOM_LIMIT = 64;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("PrepareData {input.smi}");
            System.exit(1);
        }
        IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
        Aromaticity arom = new Aromaticity(ElectronDonation.daylight(),
                                           Cycles.or(Cycles.all(), Cycles.all(6)));
        SmilesParser smipar = new SmilesParser(builder);
        SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric+SmiFlavor.UseAromaticSymbols);
        String inpname = args[0];
        try (InputStream in = Files.newInputStream(Paths.get(inpname));
             Reader rdr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader brdr = new BufferedReader(rdr);
             BufferedWriter bwtr = new BufferedWriter(new FileWriter("/Users/john/Workspace/NextMove/Scratch/TautomerSubSearch/chembl_34_subset_tautomers.smi"))) {
            String line;
            while ((line = brdr.readLine()) != null) {
                IAtomContainer mol = smipar.parseSmiles(line);
                if (mol.getAtomCount() > ATOM_LIMIT)
                    continue;
                boolean done = false;
                for (IAtomContainer tmp : Tautomers.hetero(mol)) {
                    Cycles.markRingAtomsAndBonds(tmp);
                    arom.apply(tmp);
                    bwtr.write(smigen.create(tmp) + " " + mol.getTitle() + "\n");
                    done = true;
                }
                if (!done)
                    bwtr.write(line + "\n");
            }
        } catch (IOException | CDKException e) {
            throw new RuntimeException(e);
        }

    }
}

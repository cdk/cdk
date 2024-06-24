/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.tautomer;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

final class SDTautomerStreamTest {

    @Test
    void cytosine() throws Exception {
        IAtomContainer cytosine = fromSmi("NC1=NC(=O)NC=C1");
        assertTautomers(SDTautomerStream.create(cytosine),
                        "N=C1NC(O)=NC=C1",
                        "N=C1N=C(O)NC=C1",
                        "NC1=NC(O)=NC=C1",
                        "N=C1NC(=O)NC=C1",
                        "NC=1NC(=O)N=CC1",
                        "NC1=NC(=O)NC=C1");
    }

    @Test
    void adenine() throws Exception {
        IAtomContainer cytosine = fromSmi("NC1=C2N=CNC2=NC=N1");
        assertTautomers(SDTautomerStream.create(cytosine),
                        "N=C1C=2NC=NC2NC=N1",
                        "N=C1C=2NC=NC2N=CN1",
                        "NC1=C2NC=NC2=NC=N1",
                        "N=C1C=2N=CNC2NC=N1",
                        "N=C1C=2N=CNC2N=CN1",
                        "NC1=C2N=CNC2=NC=N1",
                        "NC1=C2N=CN=C2NC=N1",
                        "NC1=C2N=CN=C2N=CN1");
    }

    @Test
    void guanine() throws Exception {
        IAtomContainer cytosine = fromSmi("OC1=C2N=CNC2=NC(=N)N1");
        assertTautomers(SDTautomerStream.create(cytosine),
                        "OC1=C2N=CN=C2NC(=N)N1",
                                "OC1=C2NC=NC2=NC(=N)N1",
                                "OC1=C2N=CNC2=NC(=N)N1",
                                "OC1=C2N=CN=C2N=C(N)N1",
                                "OC=1C=2NC=NC2NC(=N)N1",
                                "OC=1C=2N=CNC2NC(=N)N1",
                                "OC1=C2N=CN=C2NC(N)=N1",
                                "OC1=C2NC=NC2=NC(N)=N1",
                                "OC1=C2N=CNC2=NC(N)=N1",
                                "O=C1C=2NC=NC2NC(=N)N1",
                                "O=C1C=2N=CNC2NC(=N)N1",
                                "O=C1C=2NC=NC2N=C(N)N1",
                                "O=C1C=2N=CNC2N=C(N)N1",
                                "O=C1C=2NC=NC2NC(N)=N1",
                                "O=C1C=2N=CNC2NC(N)=N1");
    }

    void assertTautomers(SDTautomerStream stream, String... smis) throws CDKException {
        int i = 0;
        IAtomContainer container = null;
        List<String> expected = new ArrayList<>(Arrays.asList(smis));
        List<String> actual = new ArrayList<>();
        while ((container = stream.next()) != null) {
            actual.add(toSmi(container));
        }
        System.err.println(actual.stream()
                                 .map(String::valueOf)
                                 .collect(Collectors.joining("\n")));
        Collections.sort(expected);
        Collections.sort(actual);
        Assertions.assertEquals(expected, actual,
                                "size= " + expected.size() + " " + actual.size());
    }


    IAtomContainer fromSmi(String smi) throws InvalidSmilesException {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }

    String toSmi(IAtomContainer container) throws CDKException {
        return SmilesGenerator.generic().create(container);
    }

}
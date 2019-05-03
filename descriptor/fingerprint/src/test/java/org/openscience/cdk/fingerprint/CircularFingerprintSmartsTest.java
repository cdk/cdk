/*
 * Copyright (c) 2016 John May <jwmay@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.fingerprint.CircularFingerprinter.FP;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smarts.SmartsFragmentExtractor;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.isIn;

/**
 * @cdk.module test-standard
 */
public class CircularFingerprintSmartsTest extends CDKTestCase {

	private static ILoggingTool logger = LoggingToolFactory
			.createLoggingTool(CircularFingerprintSmartsTest.class);

	public static SmilesParser parser = new SmilesParser(
			SilentChemObjectBuilder.getInstance());

	@Test
	public void testMol1() throws Exception {
		String molSmiles = "CC";
		String expectedFPSmarts[][] = { { "C*" }, { "CC" } };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol2() throws Exception {
		String molSmiles = "CCC";
		String expectedFPSmarts[][] = { { "C*" }, { "C(*)*" },
				{ "CC*", "C(*)C" }, { "CCC" }, };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol3() throws Exception {
		String molSmiles = "CCN";
		String expectedFPSmarts[][] = { { "C*" }, { "C(*)*" }, { "N*" },
				{ "CC*", "C(*)C" }, { "C(*)N", "NC*" },
				{ "CCN", "NCC", "C(C)N", "C(N)C" }, };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol4() throws Exception {
		String molSmiles = "C1CC1";
		String expectedFPSmarts[][] = {

		{ "C(*)*" }, { "C1CC1", "C(C1)C1" } };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol5() throws Exception {
		String molSmiles = "C1CCC1";
		String expectedFPSmarts[][] = {

		{ "C(*)*" }, { "C(C*)C*", "C(CC*)*", "C(*)CC*" },
				{ "C1CCC1", "C(CC1)C1", "C(C1)CC1" } };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol6() throws Exception {
		String molSmiles = "CC[C-]";
		String expectedFPSmarts[][] = {

		{ "C*" }, { "C(*)*" }, { "[C-]*" }, { "CC*", "C(*)C" },
				{ "[C-]C*", "C(*)[C-]" },
				{ "CC[C-]", "C(C)[C-]", "[C-]CC", "C([C-])C" } };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	@Test
	public void testMol7() throws Exception {
		String molSmiles = "c1ccccc1";
		String expectedFPSmarts[][] = {

				{ "c(a)a" },
				{ "c(a)cca", "c(ca)ca", "c(cca)a" },
				{ "c(a)cccca", "c(ca)ccca", "c(cca)cca", "c(ccca)ca",
						"c(cccca)a" },
				{ "c1ccccc1", "c(c1)cccc1", "c(cc1)ccc1", "c(ccc1)cc1",
						"c(cccc1)c1" } };
		checkFPSmartsForMolecule(molSmiles, expectedFPSmarts);
	}

	private void checkFPSmartsForMolecule(String moleculeSmiles,
			String expectedFPSmarts[][]) throws Exception {

        Set<String> expected = new HashSet<>();
        for (String[] strs : expectedFPSmarts)
            Collections.addAll(expected, strs);

		// expectedFPSmarts[][] is a double array because for each smarts
		// several equivalent variants
		// of the smarts are given e.g. CCC C(C)C
		IAtomContainer mol = parser.parseSmiles(moleculeSmiles);

		CircularFingerprinter circ = new CircularFingerprinter();
		circ.calculate(mol);
        SmartsFragmentExtractor subsmarts = new SmartsFragmentExtractor(mol);
        subsmarts.setMode(SmartsFragmentExtractor.MODE_JCOMPOUNDMAPPER);
		int numFP = circ.getFPCount();

        Set<String> actual = new HashSet<>();
		for (int i = 0; i < numFP; i++) {
			FP fp = circ.getFP(i);
            actual.add(subsmarts.generate(fp.atoms));
		}

		assertThat(actual, everyItem(isIn(expected)));
	}
}

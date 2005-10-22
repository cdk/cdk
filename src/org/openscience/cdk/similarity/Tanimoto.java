package org.openscience.cdk.similarity;

/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  Calculates the Tanimoto coefficient for a given pair of two 
 *  fingerprint bitsets. The Tanimoto coefficient is one way to 
 *  quantitatively measure the "distance" or similarity of 
 *  two chemical structures. 
 *
 *  You can use the FingerPrinter class to retrieve two fingerprint bitsets.
 *  We assume that you have two structures stored in cdk.Molecule objects.
 *  A tanimoto coefficient can then be calculated like: <pre>
 *
 *   BitSet fingerprint1 = Fingerprinter.getFingerprint(molecule1);
 *   BitSet fingerprint2 = Fingerprinter.getFingerprint(molecule2);
 *   float tanimoto_coefficient = Tanimoto.calculate(fingerprint1, fingerprint2);
 *   
 * </pre> <p>
 *
 *  The FingerPrinter assumes that hydrogens are explicitely given, if this 
 *  is desired! <p>
 *  Once the two BitSets have been generated, 
 *
 *@author         steinbeck
 *@cdk.created    2005-10-19
 *@cdk.keyword    tanimoto
 *@cdk.keyword    jaccard
 *@cdk.keyword    similarity
 */

import java.util.BitSet;

public class Tanimoto 
{

	public static float calculate(BitSet bitset1, BitSet bitset2)
	{
		float _bitset1_cardinality = bitset1.cardinality();
		float _bitset2_cardinality = bitset2.cardinality();
		BitSet one_and_two = (BitSet)bitset1.clone();
		one_and_two.and(bitset2);
		float _common_bit_count = one_and_two.cardinality(); 
		float _tanimoto_coefficient = _common_bit_count/(_bitset1_cardinality + _bitset2_cardinality - _common_bit_count);
		return _tanimoto_coefficient;
	}
	
}

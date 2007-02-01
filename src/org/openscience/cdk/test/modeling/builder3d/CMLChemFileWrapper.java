/*
 *  $RCSfile$
 *  $Author: egonw $
 *  $Date: 2007-01-31 18:46:38 +0100 (Wed, 31 Jan 2007) $
 *
 *  Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.modeling.builder3d;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * @cdk.module test-builder3d
 *
 * @author     chhoppe
 * @cdk.created    2004-11-04
 */
public class CMLChemFileWrapper {
	/**
	 * Method which converts an atomContainer to a CMLChemFile 
	 * @param atomContainer
	 * @return CMLChemFile which contains the information of the atomContainer
	 */
	public static CMLChemFile wrapAtomContainerInChemModel(IAtomContainer atomContainer) {
		CMLChemFile file = new CMLChemFile();
		IChemModel model = atomContainer.getBuilder().newChemModel();
		IChemSequence sequence = atomContainer.getBuilder().newChemSequence();
		IMoleculeSet moleculeSet = atomContainer.getBuilder().newMoleculeSet();
		moleculeSet.addAtomContainer(atomContainer);
		model.setMoleculeSet(moleculeSet);
		sequence.addChemModel(model);
		file.addChemSequence(sequence);
		
		return file;
	}
	
	public static CMLChemFile[] wrapAtomContainerArrayInChemModel(IAtomContainer[] atomContainer) {
		CMLChemFile[] cmlChemfile = new CMLChemFile[atomContainer.length];
		for (int i = 0; i < atomContainer.length; i++) {
			cmlChemfile[i] = wrapAtomContainerInChemModel(atomContainer[i]);
		}
		return cmlChemfile;
	}
}

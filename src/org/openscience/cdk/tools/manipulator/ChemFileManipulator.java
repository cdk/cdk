/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.tools.manipulator;

import java.util.Enumeration;
import java.util.Vector;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.ChemModel;

/**
 * Class with convenience methods that provide methods from
 * methods from ChemObjects within the ChemFile.
 *
 * @see org.openscience.cdk.AtomContainer#removeAtomAndConnectedElectronContainers(Atom)
 *
 * @cdk.module standard
 */
public class ChemFileManipulator {

    /**
     * Puts all the Molecules of this container together in one 
     * AtomCcntainer.
     *
     * @return  The AtomContainer with all the Molecules of this container
     */
    public static AtomContainer getAllInOneContainer(ChemFile file) {
        AtomContainer container = new AtomContainer();
        for (int i=0; i<file.getChemSequenceCount(); i++) {
            ChemSequence sequence = file.getChemSequence(i);
            container.add(ChemSequenceManipulator.getAllInOneContainer(sequence));
        }
        return container;
    }

    /**
     * Returns all the AtomContainer's of a ChemFile.
     */
    public static AtomContainer[] getAllAtomContainers(ChemFile file) {
        ChemSequence[] sequences = file.getChemSequences();
        int acCount = 0;
        Vector acArrays = new Vector();
        for (int i=0; i<sequences.length; i++) {
            AtomContainer[] sequenceContainers = ChemSequenceManipulator.
                getAllAtomContainers(sequences[i]);
            acArrays.addElement(sequenceContainers);
            acCount += sequenceContainers.length;
        }
        AtomContainer[] containers = new AtomContainer[acCount];
        int arrayOffset = 0;
        for (Enumeration acArraysElements = acArrays.elements(); 
             acArraysElements.hasMoreElements(); ) {
            AtomContainer[] modelContainers = (AtomContainer[])acArraysElements.nextElement();
            System.arraycopy(modelContainers, 0,
                             containers, arrayOffset,
                             modelContainers.length);
            arrayOffset += modelContainers.length;
        }
        return containers;
    }
    
    public static ChemModel[] getAllChemModels(ChemFile file)
    {
	    ChemSequence[] sequences = file.getChemSequences();
	    int modelCounter = 0;
	    int counter = 0;
	    ChemModel[] tempModels = null;
	    for (int f = 0; f < sequences.length; f++)
	    {
		    modelCounter += sequences[f].getChemModelCount();
	    }
	    ChemModel[] models = new ChemModel[modelCounter];
	    for (int f = 0; f < sequences.length; f++)
	    {
		    tempModels = sequences[f].getChemModels();
		    for (int g = 0; g < tempModels.length; g++)
		    {
			    models[counter] = tempModels[g];
			    counter ++;
		    }
	    }
	    return models;
    }
}


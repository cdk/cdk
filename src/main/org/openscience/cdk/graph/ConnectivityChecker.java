/* $Revision$ $Author$ $Date$    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;

/**
 * Tool class for checking whether the (sub)structure in an
 * AtomContainer is connected.
 * To check whether an AtomContainer is connected this code
 * can be used:
 * <pre>
 *  boolean isConnected = ConnectivityChecker.isConnected(atomContainer);
 * </pre>
 *
 * <p>A disconnected AtomContainer can be fragmented into connected
 * fragments by using code like:
 * <pre>
 *   MoleculeSet fragments = ConnectivityChecker.partitionIntoMolecules(disconnectedContainer);
 *   int fragmentCount = fragments.getMoleculeCount();
 * </pre> 
 *
 * @cdk.module standard
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword connectivity
 */
@TestClass("org.openscience.cdk.graph.ConnectivityCheckerTest")
public class ConnectivityChecker 
{
	/**
	 * Check whether a set of atoms in an {@link IAtomContainer} is connected.
	 *
	 * @param   atomContainer  The {@link IAtomContainer} to be check for connectedness
	 * @return                 true if the {@link IAtomContainer} is connected   
	 */
    @TestMethod("testIsConnected_IAtomContainer,testPartitionIntoMolecules_IsConnected_Consistency")
    public static boolean isConnected(IAtomContainer atomContainer)
	{
		IAtomContainer newContainer = atomContainer.getBuilder().newAtomContainer();
		IAtom atom = null;
		IMolecule molecule = atomContainer.getBuilder().newMolecule();
		List<IAtom> sphere = new ArrayList<IAtom>();
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtom(f);
			atomContainer.getAtom(f).setFlag(CDKConstants.VISITED, false);
			newContainer.addAtom(atomContainer.getAtom(f));
		}

        Iterator<IBond> bonds = atomContainer.bonds();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
			bond.setFlag(CDKConstants.VISITED, false);
			newContainer.addBond(bond);
		}
		atom = newContainer.getAtom(0);
		sphere.add(atom);
		atom.setFlag(CDKConstants.VISITED, true);
		PathTools.breadthFirstSearch(newContainer, sphere, molecule);
        return molecule.getAtomCount() == atomContainer.getAtomCount();
    }
	


	/**
	 * Partitions the atoms in an AtomContainer into covalently connected components.
	 *
	 * @param   atomContainer  The AtomContainer to be partitioned into connected components, i.e. molecules
	 * @return                 A MoleculeSet.
     *
     * @cdk.dictref   blue-obelisk:graphPartitioning
	 */
    @TestMethod("testPartitionIntoMolecules_IAtomContainer,testPartitionIntoMoleculesKeepsAtomIDs,testPartitionIntoMolecules_IsConnected_Consistency")
    public static IMoleculeSet partitionIntoMolecules(IAtomContainer atomContainer) {
		IAtomContainer newContainer = atomContainer.getBuilder().newAtomContainer();
		IAtom atom = null;
		IElectronContainer eContainer = null;
		IMolecule molecule = null;
		IMoleculeSet molecules = atomContainer.getBuilder().newMoleculeSet();
		List<IAtom> sphere = new ArrayList<IAtom>();
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtom(f);
			atom.setFlag(CDKConstants.VISITED, false);
			newContainer.addAtom(atom);
		}
		Iterator<IElectronContainer> eContainers = atomContainer.electronContainers();
		while (eContainers.hasNext()){
			eContainer = (IElectronContainer)eContainers.next();
			eContainer.setFlag(CDKConstants.VISITED, false);
			newContainer.addElectronContainer(eContainer);
		}
		while(newContainer.getAtomCount() > 0) {
			atom = newContainer.getAtom(0);
			molecule = atomContainer.getBuilder().newMolecule();
			sphere.clear();
			sphere.add(atom);
			atom.setFlag(CDKConstants.VISITED, true);
			PathTools.breadthFirstSearch(newContainer, sphere, molecule);
			molecules.addMolecule(molecule);
			newContainer.remove(molecule);
		}
		return molecules;
	}
}

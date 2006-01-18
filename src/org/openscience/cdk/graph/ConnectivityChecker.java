/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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

import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;

/**
 * Tool class for checking whether the (sub)structure in an
 * AtomContainer is connected.
 * To check wether an AtomContainer is connected this code
 * can be used:
 * <pre>
 *  boolean isConnected = ConnectivityChecker.isConnected(atomContainer);
 * </pre>
 *
 * <p>A disconnected AtomContainer can be fragmented into connected
 * fragments by using code like:
 * <pre>
 *   SetOfMolecules fragments = ConnectivityChecker.partitionIntoMolecules(disconnectedContainer);
 *   int fragmentCount = fragments.getMoleculeCount();
 * </pre> 
 *
 * @cdk.module standard
 *
 * @cdk.keyword connectivity
 */ 
public class ConnectivityChecker 
{
	/**
	 * Check whether a set of atoms in an atomcontainer is connected
	 *
	 * @param   atomContainer  The AtomContainer to be check for connectedness
	 * @return                 true if the AtomContainer is connected   
	 */
	public static boolean isConnected(IAtomContainer atomContainer)
	{
		IAtomContainer ac = atomContainer.getBuilder().newAtomContainer();
		IAtom atom = null;
		Bond bond = null;
		Molecule molecule = atomContainer.getBuilder().newMolecule();
		Vector sphere = new Vector();
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtomAt(f);
			atomContainer.getAtomAt(f).setFlag(CDKConstants.VISITED, false);
			ac.addAtom(atomContainer.getAtomAt(f));
		}
		Bond[] bonds = atomContainer.getBonds();
		for (int f = 0; f < bonds.length; f++)
		{
			bond = bonds[f];
			bonds[f].setFlag(CDKConstants.VISITED, false);
			ac.addBond(bonds[f]);
		}
		atom = ac.getAtomAt(0);
		sphere.addElement(atom);
		atom.setFlag(CDKConstants.VISITED, true);
		PathTools.breadthFirstSearch(ac, sphere, molecule);
		if (molecule.getAtomCount() == atomContainer.getAtomCount())
		{
			return true;
		}
		return false;
	}
	


	/**
	 * Partitions the atoms in an AtomContainer into covalently connected components.
	 *
	 * @param   atomContainer  The AtomContainer to be partitioned into connected components, i.e. molecules
	 * @return                 A SetOfMolecules.
     *
     * @cdk.dictref   blue-obelisk:graphPartitioning
	 */
	public static SetOfMolecules partitionIntoMolecules(IAtomContainer atomContainer) {
		IAtomContainer ac = atomContainer.getBuilder().newAtomContainer();
		IAtom atom = null;
		ElectronContainer eContainer = null;
		Molecule molecule = null;
		SetOfMolecules molecules = atomContainer.getBuilder().newSetOfMolecules();
		Vector sphere = new Vector();
		for (int f = 0; f < atomContainer.getAtomCount(); f++)
		{
			atom = atomContainer.getAtomAt(f);
			atom.setFlag(CDKConstants.VISITED, false);
			ac.addAtom(atom);
		}
		ElectronContainer[] eContainers = atomContainer.getElectronContainers();
		for (int f = 0; f < eContainers.length; f++){
			eContainer = eContainers[f];
			eContainer.setFlag(CDKConstants.VISITED, false);
			ac.addElectronContainer(eContainer);
		}
		while(ac.getAtomCount() > 0) {
			atom = ac.getAtomAt(0);
			molecule = atomContainer.getBuilder().newMolecule();
			sphere.removeAllElements();
			sphere.addElement(atom);
			atom.setFlag(CDKConstants.VISITED, true);
			PathTools.breadthFirstSearch(ac, sphere, molecule);
			molecules.addMolecule(molecule);
			ac.remove(molecule);
            // System.out.println("Found molecule: " + molecule);
		}
		return molecules;
	}
}

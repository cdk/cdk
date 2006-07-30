/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.tools.manipulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IAtomContainerSet;

/**
 * @cdk.module standard
 *
 * @see ChemModelManipulator
 */
public class SetOfAtomContainersManipulator {
    
	public static int getAtomCount(IAtomContainerSet set) {
		int count = 0;
		IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
        	count += acs[i].getAtomCount();
        }
        return count;
	}
	
	public static int getBondCount(IAtomContainerSet set) {
		int count = 0;
		IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
        	count += acs[i].getBondCount();
        }
        return count;
	}
	
    public static void removeAtomAndConnectedElectronContainers(IAtomContainerSet set, IAtom atom) {
        IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
            IAtomContainer container = acs[i];
            if (container.contains(atom)) {
                container.removeAtomAndConnectedElectronContainers(atom);
                IMolecule[] molecules = ConnectivityChecker.partitionIntoMolecules(container).getMolecules();
                if(molecules.length>1){
                	set.removeAtomContainer(container);
                	for(int k=0;k<molecules.length;k++){
                		set.addAtomContainer(molecules[k]);
                	}
                }
                return;
            }
        }
    }
    
    public static void removeElectronContainer(IAtomContainerSet set, IElectronContainer electrons) {
        IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
            IAtomContainer container = acs[i];
            if (container.contains(electrons)) {
                container.removeElectronContainer(electrons);
                IMolecule[] molecules = ConnectivityChecker.partitionIntoMolecules(container).getMolecules();
                if(molecules.length>1){
                	set.removeAtomContainer(container);
                	for(int k=0;k<molecules.length;k++){
                		set.addAtomContainer(molecules[k]);
                	}
                }
                return;
            }
        }
    }
    
    /**
     * Puts all the AtomContainers of this set together in one 
     * AtomCcntainer.
     *
     * @return  The AtomContainer with all the AtomContainers of this set
     * 
     * @deprecated This method has a serious performace impact. Try to use
     *   other methods.
     */
    public static IAtomContainer getAllInOneContainer(IAtomContainerSet set) {
        IAtomContainer container = set.getBuilder().newAtomContainer();
        IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
            container.add(acs[i]);
        }
        return container;
    }
    
	/**
     * Returns all the AtomContainer's of a SetOfMolecules.
     */
    public static IAtomContainer[] getAllAtomContainers(IAtomContainerSet set) {
		return set.getAtomContainers();
    }
	
	/**
	 * @return The summed charges of all atoms in this set.
	 */
	public static double getTotalCharge(IAtomContainerSet set) {
		double charge = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			int thisCharge = AtomContainerManipulator.getTotalFormalCharge(set.getAtomContainer(i));
			double stoich = set.getMultiplier(i);
			charge += stoich * thisCharge;
		}
		return charge;
	}
	
	/**
	 * @return The summed formal charges of all atoms in this set.
	 */
	public static double getTotalFormalCharge(IAtomContainerSet set) {
		int charge = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			int thisCharge = AtomContainerManipulator.getTotalFormalCharge(set.getAtomContainer(i));
			double stoich = set.getMultiplier(i);
			charge += stoich * thisCharge;
		}
		return charge;
	}
	
	/**
	 * @return The summed implicit hydrogens of all atoms in this set.
	 */
	public static int getTotalHydrogenCount(IAtomContainerSet set) {
		int hCount = 0;
		for (int i = 0; i < set.getAtomContainerCount(); i++) {
			hCount += AtomContainerManipulator.getTotalHydrogenCount(set.getAtomContainer(i));
		}
		return hCount;
	}
	
    public static Vector getAllIDs(IAtomContainerSet set) {
        Vector idList = new Vector();
        if (set != null) {
            if (set.getID() != null) idList.addElement(set.getID());
            for (int i = 0; i < set.getAtomContainerCount(); i++) {
                idList.add(AtomContainerManipulator.getAllIDs(set.getAtomContainer(i)));
            }
        }
        return idList;
    }
    
    public static void setAtomProperties(IAtomContainerSet set, Object propKey, Object propVal) {
        if (set != null) {
            for (int i = 0; i < set.getAtomContainerCount(); i++) {
                AtomContainerManipulator.setAtomProperties(set.getAtomContainer(i), propKey, propVal);
            }
        }
    }

    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet containerSet, IAtom atom) {
        IAtomContainer[] containers = containerSet.getAtomContainers();
        for (int i=0; i<containers.length; i++) {
            if (containers[i].contains(atom)) {
                return containers[i];
            }
        }
        return null;
    }

    public static IAtomContainer getRelevantAtomContainer(IAtomContainerSet containerSet, IBond bond) {
        IAtomContainer[] containers = containerSet.getAtomContainers();
        for (int i=0; i<containers.length; i++) {
            if (containers[i].contains(bond)) {
                return containers[i];
            }
        }
        return null;
    }
    
    public static List getAllChemObjects(IAtomContainerSet set) {
        ArrayList list = new ArrayList();
        list.add(set);
        IAtomContainer[] acs = set.getAtomContainers();
        for (int i=0; i < acs.length; i++) {
            list.add(acs[i]); // don't recurse into AC's for now
        }
        return list;
    }
    
}


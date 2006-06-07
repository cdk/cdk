/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.debug;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 */
public class DebugRing extends org.openscience.cdk.Ring
    implements IRing {

    private static final long serialVersionUID = -6420813246421544803L;
    
    LoggingTool logger = new LoggingTool(DebugRing.class);

	public DebugRing() {
		super();
	}
    
	public DebugRing(int ringSize, String elementSymbol) {
		this(ringSize);
		super.atomCount = ringSize;
		super.electronContainerCount = ringSize;
		atoms[0] = new DebugAtom(elementSymbol);
		for (int i = 1; i < ringSize; i++) {
			atoms[i] = new DebugAtom(elementSymbol);
			super.electronContainers[i-1] = new Bond(atoms[i - 1], atoms[i], 1);
		}
		super.electronContainers[ringSize-1] = new Bond(atoms[ringSize - 1], atoms[0], 1);
	}
	
	public DebugRing(int ringSize) {
		super(ringSize);
	}

	public DebugRing(IAtomContainer container) {
		super(container);
	}

	public void addAtomParity(IAtomParity parity) {
		logger.debug("Adding atom parity: ", parity);
		super.addAtomParity(parity);
	}

	public IAtomParity getAtomParity(IAtom atom) {
		logger.debug("Getting atom parity: ", atom);
		return super.getAtomParity(atom);
	}

	public void setAtoms(IAtom[] atoms) {
		logger.debug("Setting atoms: ", atoms.length);
		super.setAtoms(atoms);
	}

	public void setElectronContainers(IElectronContainer[] electronContainers) {
		logger.debug("Setting electron containers: ", electronContainers.length);
		super.setElectronContainers(electronContainers);
	}

	public void setAtomAt(int number, IAtom atom) {
		logger.debug("Setting atom at: pos=" + number, " atom=" + atom);
		super.setAtomAt(number, atom);
	}

	public IAtom getAtomAt(int number) {
		logger.debug("Getting atom at: ", number);
		return super.getAtomAt(number);
	}

	public IBond getBondAt(int number) {
		logger.debug("Getting bond at: ", number);
		return super.getBondAt(number);
	}

	public void setElectronContainerAt(int number, IElectronContainer electronContainer) {
		logger.debug("Setting electron container at: pos=" + number, " electron container=" +electronContainer);
		super.setElectronContainerAt(number, electronContainer);
	}

	public void setElectronContainerCount(int electronContainerCount) {
		logger.debug("Setting electron container count: ", electronContainerCount);
		super.setElectronContainerCount(electronContainerCount);
	}

	public void setAtomCount(int atomCount) {
		logger.debug("Settting atom count: ", atomCount);
		super.setAtomCount(atomCount);
	}

	public IAtom[] getAtoms() {
		logger.debug("Getting atoms: ", super.getAtoms().length);
		return super.getAtoms();
	}

	public Enumeration atoms() {
		logger.debug("Getting atoms enumaration");
		return super.atoms();
	}

	public IElectronContainer[] getElectronContainers() {
		logger.debug("Getting electron containers: ", super.getElectronContainers().length);
		return super.getElectronContainers();
	}

	public IBond[] getBonds() {
		logger.debug("Getting bonds: ", super.getBonds().length);
		return super.getBonds();
	}

	public ILonePair[] getLonePairs() {
		logger.debug("Getting lone pairs: ", super.getLonePairs().length);
		return super.getLonePairs();
	}

	public ILonePair[] getLonePairs(IAtom atom) {
		logger.debug("Getting lone pairs at atom: atom=" + atom, " lone pairs=" + super.getLonePairs(atom).length);
		return super.getLonePairs(atom);
	}

	public IAtom getFirstAtom() {
		logger.debug("Getting first atom: ", super.getFirstAtom());
		return super.getFirstAtom();
	}

	public IAtom getLastAtom() {
		logger.debug("Getting last atom: ", super.getLastAtom());
		return super.getLastAtom();
	}

	public int getAtomNumber(IAtom atom) {
		logger.debug("Getting atom number: ", atom);
		return super.getAtomNumber(atom);
	}

	public int getBondNumber(IAtom atom1, IAtom atom2) {
		logger.debug("Getting bond number: atom1=" + atom1, " atom2=" + atom2);
		return super.getBondNumber(atom1, atom2);
	}

	public int getBondNumber(IBond bond) {
		logger.debug("Getting bond numger: ", bond);
		return super.getBondNumber(bond);
	}

	public IElectronContainer getElectronContainerAt(int number) {
		logger.debug("Getting electron container at: ", number);
		return super.getElectronContainerAt(number);
	}

	public IBond getBond(IAtom atom1, IAtom atom2) {
		logger.debug("Getting bond for atoms: atom1=" + atom1, " atom2=" + atom2);
		return super.getBond(atom1, atom2);
	}

	public IAtom[] getConnectedAtoms(IAtom atom) {
		logger.debug("Getting connected atoms for atom: ", atom);
		return super.getConnectedAtoms(atom);
	}

	public List getConnectedAtomsVector(IAtom atom) {
		logger.debug("Getting connecting atoms vector for atom: ", atom);
		return super.getConnectedAtomsVector(atom);
	}

	public IBond[] getConnectedBonds(IAtom atom) {
		logger.debug("Getting connected bonds for atom: ", atom);
		return super.getConnectedBonds(atom);
	}

	public List getConnectedBondsVector(IAtom atom) {
		logger.debug("Getting connected bonds vector for atom: ", atom);
		return super.getConnectedBondsVector(atom);
	}

	public IElectronContainer[] getConnectedElectronContainers(IAtom atom) {
		logger.debug("Getting connected electron containers for atom: ", atom);
		return super.getConnectedElectronContainers(atom);
	}

	public int getBondCount(int atomnumber) {
		logger.debug("Getting bond count for atom: ", atomnumber);
		return super.getBondCount(atomnumber);
	}

	public int getAtomCount() {
		logger.debug("Getting atom count");
		return super.getAtomCount();
	}

	public int getElectronContainerCount() {
		logger.debug("Getting electron container count");
		return super.getElectronContainerCount();
	}

	public int getLonePairCount() {
		logger.debug("Getting lone pair count");
		return super.getLonePairCount();
	}

	public int getBondCount() {
		logger.debug("Getting bond count");
		return super.getBondCount();
	}

	public int getBondCount(IAtom atom) {
		logger.debug("Getting bond count for atom: ", atom);
		return super.getBondCount(atom);
	}

	public int getLonePairCount(IAtom atom) {
		logger.debug("Getting lone pair count for atom: ", atom);
		return super.getLonePairCount(atom);
	}

	public ISingleElectron[] getSingleElectron(IAtom atom) {
		logger.debug("Getting single electrons for atom: ", atom);
		return super.getSingleElectron(atom);
	}

	public int getSingleElectronSum(IAtom atom) {
		logger.debug("Getting single electron sum for atom: ", atom);
		return super.getSingleElectronSum(atom);
	}

	public double getBondOrderSum(IAtom atom) {
		logger.debug("Getting bond order sum for atom: ", atom);
		return super.getBondOrderSum(atom);
	}

	public double getMaximumBondOrder(IAtom atom) {
		logger.debug("Getting maximum bond order for atom: ", atom);
		return super.getMaximumBondOrder(atom);
	}

	public double getMinimumBondOrder(IAtom atom) {
		logger.debug("Getting minimum bond order for atom: ", atom);
		return super.getMinimumBondOrder(atom);
	}

	public IAtomContainer getIntersection(IAtomContainer container) {
		logger.debug("Getting intersection with: ", container);
		return super.getIntersection(container);
	}

	public void addElectronContainers(IAtomContainer atomContainer) {
		logger.debug("Adding electron containers from atom container: ", atomContainer);
		super.addElectronContainers(atomContainer);
	}

	public void add(IAtomContainer atomContainer) {
		logger.debug("Adding atom container: ", atomContainer);
		super.add(atomContainer);
	}

	public void addAtom(IAtom atom) {
		logger.debug("Adding atom: ", atom);
		super.addAtom(atom);
	}

	public void addBond(IBond bond) {
		logger.debug("Adding bond: ", bond);
		super.addBond(bond);
	}

	public void addElectronContainer(IElectronContainer electronContainer) {
		logger.debug("Adding electron container: ", electronContainer);
		super.addElectronContainer(electronContainer);
		
	}

	public void remove(IAtomContainer atomContainer) {
		logger.debug("Removing atom container: ", atomContainer);
		super.remove(atomContainer);
	}

	public IElectronContainer removeElectronContainer(int position) {
		logger.debug("Removing electronContainer: ", position);
		return super.removeElectronContainer(position);
	}

	public IElectronContainer removeElectronContainer(IElectronContainer electronContainer) {
		logger.debug("Removing electron container: ", electronContainer);
		return super.removeElectronContainer(electronContainer);
	}

	public IBond removeBond(IAtom atom1, IAtom atom2) {
		logger.debug("Removing bond: atom1=" + atom1 + " atom2=" + atom2);
		return super.removeBond(atom1, atom2);
	}

	public void removeAtom(int position) {
		logger.debug("Removing atom: ", position);
		super.removeAtom(position);
	}

	public void removeAtomAndConnectedElectronContainers(IAtom atom) {
		logger.debug("Removing atom and connected electron containers: ", atom);
		super.removeAtomAndConnectedElectronContainers(atom);		
	}

	public void removeAtom(IAtom atom) {
		logger.debug("Removing atom: ", atom);
		super.removeAtom(atom);
	}

	public void removeAllElements() {
		logger.debug("Removing all elements");
		super.removeAllElements();
	}

	public void removeAllElectronContainers() {
		logger.debug("Removing all electron containers");
		super.removeAllElectronContainers();
	}

	public void removeAllBonds() {
		logger.debug("Removing all bonds");
		super.removeAllBonds();
	}

	public void addBond(int atom1, int atom2, double order, int stereo) {
		logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order + " stereo=" + stereo);
		super.addBond(atom1, atom2, order, stereo);
	}

	public void addBond(int atom1, int atom2, double order) {
		logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order);
		super.addBond(atom1, atom2, order);
	}

	public void addLonePair(int atomID) {
		logger.debug("Adding long pair: ", atomID);
		super.addLonePair(atomID);
	}

	public boolean contains(IElectronContainer electronContainer) {
		logger.debug("Contains electron container: ", electronContainer);
		return super.contains(electronContainer);
	}

	public boolean contains(IAtom atom) {
		logger.debug("Contains atom: ", atom);
		return super.contains(atom);
	}

	public void addListener(IChemObjectListener col) {
		logger.debug("Adding listener: ", col);
		super.addListener(col);
	}

	public int getListenerCount() {
		logger.debug("Getting listener count: ", super.getListenerCount());
		return super.getListenerCount();
	}

	public void removeListener(IChemObjectListener col) {
		logger.debug("Removing listener: ", col);
		super.removeListener(col);
	}

	public void notifyChanged() {
		logger.debug("Notifying changed");
		super.notifyChanged();
	}

	public void notifyChanged(IChemObjectChangeEvent evt) {
		logger.debug("Notifying changed event: ", evt);
		super.notifyChanged(evt);
	}

	public void setProperty(Object description, Object property) {
		logger.debug("Setting property: ", description + "=" + property);
		super.setProperty(description, property);
	}

	public void removeProperty(Object description) {
		logger.debug("Removing property: ", description);
		super.removeProperty(description);
	}

	public Object getProperty(Object description) {
		logger.debug("Getting property: ", description + "=" + super.getProperty(description));
		return super.getProperty(description);
	}

	public Hashtable getProperties() {
		logger.debug("Getting properties");
		return super.getProperties();
	}

	public String getID() {
		logger.debug("Getting ID: ", super.getID());
		return super.getID();
	}

	public void setID(String identifier) {
		logger.debug("Setting ID: ", identifier);
		super.setID(identifier);
	}

	public void setFlag(int flag_type, boolean flag_value) {
		logger.debug("Setting flag: ", flag_type + "=" + flag_value);
		super.setFlag(flag_type, flag_value);
	}

	public boolean getFlag(int flag_type) {
		logger.debug("Setting flag: ", flag_type + "=" + super.getFlag(flag_type));
		return super.getFlag(flag_type);
	}

	public void setProperties(Hashtable properties) {
		logger.debug("Setting properties: ", properties);
		super.setProperties(properties);
	}

	public void setFlags(boolean[] flagsNew) {
		logger.debug("Setting flags:", flagsNew.length);
		super.setFlags(flagsNew);
	}

	public boolean[] getFlags() {
		logger.debug("Getting flags:", super.getFlags().length);
		return super.getFlags();
	}

	public Object clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
        	clone = super.clone();
        } catch (Exception exception) {
        	logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
        	logger.debug(exception);
        }
        return clone;
	}

	public IChemObjectBuilder getBuilder() {
		return DebugChemObjectBuilder.getInstance();
	}

	public int getRingSize() {
		logger.debug("Getting Ring size:", super.getRingSize());
		return super.getRingSize();
	}

	public IBond getNextBond(IBond bond, IAtom atom) {
		logger.debug("Getting next bond: ", super.getNextBond(bond, atom));
		return super.getNextBond(bond, atom);
	}

	public int getOrderSum() {
		logger.debug("Getting order sum: ", super.getOrderSum());
		return super.getOrderSum();
	}

	public void stateChanged(IChemObjectChangeEvent event) {
		logger.debug("State changed: ", event);
		super.stateChanged(event);
	}

}

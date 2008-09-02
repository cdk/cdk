/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.tools.LoggingTool;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.svnrev  $Revision$
 */
public class DebugStrand extends org.openscience.cdk.Strand
    implements IStrand {

    private static final long serialVersionUID = 1794588804926192427L;
    
    LoggingTool logger = new LoggingTool(DebugAtomContainer.class);

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

//	public void setElectronContainers(IElectronContainer[] electronContainers) {
//		logger.debug("Setting electron containers: ", electronContainers.length);
//		super.setElectronContainers(electronContainers);
//	}

	public void setAtom(int number, IAtom atom) {
		logger.debug("Setting atom at: pos=" + number, " atom=" + atom);
		super.setAtom(number, atom);
	}

	public IAtom getAtom(int number) {
		logger.debug("Getting atom at: ", number);
		return super.getAtom(number);
	}

	public IBond getBond(int number) {
		logger.debug("Getting bond at: ", number);
		return super.getBond(number);
	}

	public ILonePair getLonePair(int number) {
		logger.debug("Getting lone pair at: ", number);
		return super.getLonePair(number);
	}
	
	public ISingleElectron getSingleElectron(int number) {
		logger.debug("Getting single electron at: ", number);
		return super.getSingleElectron(number);
	}
	
//	public void setElectronContainer(int number, IElectronContainer electronContainer) {
//		logger.debug("Setting electron container at: pos=" + number, " electron container=" +electronContainer);
//		super.setElectronContainer(number, electronContainer);
//	}

//	public void setElectronContainerCount(int electronContainerCount) {
//		logger.debug("Setting electron container count: ", electronContainerCount);
//		super.setElectronContainerCount(electronContainerCount);
//	}

//	public void setAtomCount(int atomCount) {
//		logger.debug("Settting atom count: ", atomCount);
//		super.setAtomCount(atomCount);
//	}

	public Iterable<IAtom> atoms() {
		logger.debug("Getting atoms iterator");
		return super.atoms();
	}

	public Iterable<IBond> bonds() {
		logger.debug("Getting bonds iterator");
		return super.bonds();
	}

	public Iterable<ILonePair> lonePairs() {
		logger.debug("Getting lone pairs iterator");
		return super.lonePairs();
	}

	public Iterable<ISingleElectron> singleElectrons() {
		logger.debug("Getting single electrons iterator");
		return super.singleElectrons();
	}

	public Iterable<IElectronContainer> electronContainers() {
		logger.debug("Getting electron containers iterator");
		return super.electronContainers();
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
		logger.debug("Getting bond number: ", bond);
		return super.getBondNumber(bond);
	}

	public int getLonePairNumber(ILonePair bond) {
		logger.debug("Getting lone pair number: ", bond);
		return super.getLonePairNumber(bond);
	}
	
	public int getSingleElectronNumber(ISingleElectron bond) {
		logger.debug("Getting single electron number: ", bond);
		return super.getSingleElectronNumber(bond);
	}
	
	public IElectronContainer getElectronContainer(int number) {
		logger.debug("Getting electron container at: ", number);
		return super.getElectronContainer(number);
	}

	public IBond getBond(IAtom atom1, IAtom atom2) {
		logger.debug("Getting bond for atoms: atom1=" + atom1, " atom2=" + atom2);
		return super.getBond(atom1, atom2);
	}
	
	public int getAtomCount() {
		logger.debug("Getting atom count");
		return super.getAtomCount();
	}

	public int getBondCount() {
		logger.debug("Getting bond count");
		return super.getBondCount();
	}
	
	public int getLonePairCount() {
		logger.debug("Getting lone pair count");
		return super.getLonePairCount();
	}

	public int getSingleElectronCount() {
		logger.debug("Getting single electron count");
		return super.getSingleElectronCount();
	}
	
	public int getElectronContainerCount() {
		logger.debug("Getting electron container count");
		return super.getElectronContainerCount();
	}

	
//	public IAtom[] getConnectedAtoms(IAtom atom) {
//		logger.debug("Getting connected atoms for atom: ", atom);
//		return super.getConnectedAtoms(atom);
//	}

	public List getConnectedAtomsList(IAtom atom) {
		logger.debug("Getting connecting atoms vector for atom: ", atom);
		return super.getConnectedAtomsList(atom);
	}

//	public IBond[] getConnectedBonds(IAtom atom) {
//		logger.debug("Getting connected bonds for atom: ", atom);
//		return super.getConnectedBonds(atom);
//	}

	public List getConnectedBondsList(IAtom atom) {
		logger.debug("Getting connected bonds vector for atom: ", atom);
		return super.getConnectedBondsList(atom);
	}

	public List getConnectedLonePairsList(IAtom atom) {
		logger.debug("Getting lone pairs at atom: atom=" + atom, " lone pairs=" + super.getConnectedLonePairsCount(atom));
		return super.getConnectedLonePairsList(atom);
	}
	
	public List getConnectedSingleElectronsList(IAtom atom) {
		logger.debug("Getting single electrons at atom: atom=" + atom, " single electrons=" + super.getConnectedSingleElectronsCount(atom));
		return super.getConnectedSingleElectronsList(atom);
	}
	
	public java.util.List getConnectedElectronContainersList(IAtom atom) {
		logger.debug("Getting connected electron containers for atom: ", atom);
		return super.getConnectedElectronContainersList(atom);
	}

	public int getConnectedAtomsCount(IAtom atom) {
		logger.debug("Getting connected atoms count for atom: ", atom);
		return super.getConnectedAtomsCount(atom);
	}
	
	public int getConnectedBondsCount(IAtom atom) {
		logger.debug("Getting connected bonds count for atom: ", atom);
		return super.getConnectedBondsCount(atom);
	}
	
	public int getConnectedLonePairsCount(IAtom atom) {
		logger.debug("Getting connected lone pairs count for atom: ", atom);
		return super.getConnectedLonePairsCount(atom);
	}
	
	public int getConnectedSingleElectronsCount(IAtom atom) {
		logger.debug("Getting connected single electrons count for atom: ", atom);
		return super.getConnectedSingleElectronsCount(atom);
	}
	
	public double getBondOrderSum(IAtom atom) {
		logger.debug("Getting bond order sum for atom: ", atom);
		return super.getBondOrderSum(atom);
	}

	public Order getMaximumBondOrder(IAtom atom) {
		logger.debug("Getting maximum bond order for atom: ", atom);
		return super.getMaximumBondOrder(atom);
	}

	public Order getMinimumBondOrder(IAtom atom) {
		logger.debug("Getting minimum bond order for atom: ", atom);
		return super.getMinimumBondOrder(atom);
	}

//	public void addElectronContainers(IAtomContainer atomContainer) {
//		logger.debug("Adding electron containers from atom container: ", atomContainer);
//		super.addElectronContainers(atomContainer);
//	}

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

	public void addLonePair(ILonePair ec) {
		logger.debug("Adding lone pair: ", ec);
		super.addLonePair(ec);
	}
	
	public void addSingleElectron(ISingleElectron ec) {
		logger.debug("Adding single electron: ", ec);
		super.addSingleElectron(ec);
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

	public void removeElectronContainer(IElectronContainer electronContainer) {
		logger.debug("Removing electron container: ", electronContainer);
		super.removeElectronContainer(electronContainer);
	}

	public void removeAtom(int position) {
		logger.debug("Removing atom: ", position);
		super.removeAtom(position);
	}

	public void removeAtom(IAtom atom) {
		logger.debug("Removing atom: ", atom);
		super.removeAtom(atom);
	}

	public IBond removeBond(int pos) {
		logger.debug("Removing bond at " + pos);
		return super.removeBond(pos);
	}
	
	public IBond removeBond(IAtom atom1, IAtom atom2) {
		logger.debug("Removing bond: atom1=" + atom1 + " atom2=" + atom2);
		return super.removeBond(atom1, atom2);
	}
	
	public void removeBond(IBond bond) {
		logger.debug("Removing bond=" + bond);
		super.removeBond(bond);
	}
	
	public ILonePair removeLonePair(int pos) {
		logger.debug("Removing bond at " + pos);
		return super.removeLonePair(pos);
	}
	
	public void removeLonePair(ILonePair ec) {
		logger.debug("Removing bond=" + ec);
		super.removeLonePair(ec);
	}
	
	public ISingleElectron removeSingleElectron(int pos) {
		logger.debug("Removing bond at " + pos);
		return super.removeSingleElectron(pos);
	}
	
	public void removeSingleElectron(ISingleElectron ec) {
		logger.debug("Removing bond=" + ec);
		super.removeSingleElectron(ec);
	}
	
	public void removeAtomAndConnectedElectronContainers(IAtom atom) {
		logger.debug("Removing atom and connected electron containers: ", atom);
		super.removeAtomAndConnectedElectronContainers(atom);		
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

	public void addBond(int atom1, int atom2, IBond.Order order, int stereo) {
		logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order + " stereo=" + stereo);
		super.addBond(atom1, atom2, order, stereo);
	}

	public void addBond(int atom1, int atom2, IBond.Order order) {
		logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order);
		super.addBond(atom1, atom2, order);
	}

	public void addLonePair(int atomID) {
		logger.debug("Adding lone pair: ", atomID);
		super.addLonePair(atomID);
	}

	public void addSingleElectron(int atomID) {
		logger.debug("Adding single electron: ", atomID);
		super.addSingleElectron(atomID);
	}
	
	public boolean contains(IAtom atom) {
		logger.debug("Contains atom: ", atom);
		return super.contains(atom);
	}

	public boolean contains(IBond bond) {
		logger.debug("Contains bond: ", bond);
		return super.contains(bond);
	}
	
	public boolean contains(ILonePair ec) {
		logger.debug("Contains lone pair: ", ec);
		return super.contains(ec);
	}
	
	public boolean contains(ISingleElectron ec) {
		logger.debug("Contains single electron: ", ec);
		return super.contains(ec);
	}
	
	public boolean contains(IElectronContainer electronContainer) {
		logger.debug("Contains electron container: ", electronContainer);
		return super.contains(electronContainer);
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

	public Map<Object,Object> getProperties() {
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

	public void setProperties(Map<Object,Object> properties) {
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

	public String getStrandName() {
		logger.debug("Getting strand name: ", super.getStrandName());
		return super.getStrandName();
	}

	public String getStrandType() {
		logger.debug("Getting strand type: ", super.getStrandType());
		return super.getStrandType();
	}

	public void setStrandName(String cStrandName) {
		logger.debug("Setting strand name to: ", cStrandName);
		super.setStrandName(cStrandName);
	}

	public void setStrandType(String cStrandType) {
		logger.debug("Setting strand type to: ", cStrandType);
		super.setStrandType(cStrandType);
	}

	public void addAtom(IAtom oAtom, IMonomer oMonomer) {
		logger.debug("Adding atom to monomer: ", oAtom, oMonomer);
		super.addAtom(oAtom, oMonomer);
	}

	public int getMonomerCount() {
		logger.debug("Getting monomer count: ", super.getMonomerCount());
		return super.getMonomerCount();
	}

	public IMonomer getMonomer(String cName) {
		logger.debug("Getting monomer for String: ", cName);
		return super.getMonomer(cName);
	}

	public Collection getMonomerNames() {
		logger.debug("Getting monomer names");
		return super.getMonomerNames();
	}

	public void removeMonomer(String name) {
		logger.debug("Removing monomer by string: ", name);
		super.removeMonomer(name);
	}

	public Map<String, IMonomer> getMonomers() {
		logger.debug("Getting monomers as hashtable");
		return super.getMonomers();
	}

	public void stateChanged(IChemObjectChangeEvent event) {
		logger.debug("Receiving state changed event: ", event);
		super.stateChanged(event);
	}

}

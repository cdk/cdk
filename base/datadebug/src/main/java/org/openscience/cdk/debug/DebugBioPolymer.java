/* Copyright (C) 2005-2007  Egon Willighagen <egonw@users.sf.net>
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.BioPolymer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Debugging data class.
 *
 * @author     egonw
 * @cdk.module datadebug
 * @cdk.githash
 */
public class DebugBioPolymer extends BioPolymer implements IBioPolymer {

    private static final long serialVersionUID = 5349870327516864575L;

    ILoggingTool              logger           = LoggingToolFactory.createLoggingTool(DebugAtomContainer.class);

    /** {@inheritDoc} */
    @Override
    public void addStereoElement(IStereoElement parity) {
        logger.debug("Adding stereo element: ", parity);
        super.addStereoElement(parity);
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IStereoElement> stereoElements() {
        logger.debug("Getting stereo elements.");
        return super.stereoElements();
    }

    /** {@inheritDoc} */
    @Override
    public void setAtoms(IAtom[] atoms) {
        logger.debug("Setting atoms: ", atoms.length);
        super.setAtoms(atoms);
    }

    //	public void setElectronContainers(IElectronContainer[] electronContainers) {
    //		logger.debug("Setting electron containers: ", electronContainers.length);
    //		super.setElectronContainers(electronContainers);
    //	}

    /** {@inheritDoc} */
    @Override
    public void setAtom(int number, IAtom atom) {
        logger.debug("Setting atom at: pos=" + number, " atom=" + atom);
        super.setAtom(number, atom);
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getAtom(int number) {
        logger.debug("Getting atom at: ", number);
        return super.getAtom(number);
    }

    /** {@inheritDoc} */
    @Override
    public IBond getBond(int number) {
        logger.debug("Getting bond at: ", number);
        return super.getBond(number);
    }

    /** {@inheritDoc} */
    @Override
    public ILonePair getLonePair(int number) {
        logger.debug("Getting lone pair at: ", number);
        return super.getLonePair(number);
    }

    /** {@inheritDoc} */
    @Override
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

    /** {@inheritDoc} */
    @Override
    public Iterable<IAtom> atoms() {
        logger.debug("Getting atoms iterator");
        return super.atoms();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IBond> bonds() {
        logger.debug("Getting bonds iterator");
        return super.bonds();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<ILonePair> lonePairs() {
        logger.debug("Getting lone pairs iterator");
        return super.lonePairs();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<ISingleElectron> singleElectrons() {
        logger.debug("Getting single electrons iterator");
        return super.singleElectrons();
    }

    /** {@inheritDoc} */
    @Override
    public Iterable<IElectronContainer> electronContainers() {
        logger.debug("Getting electron containers iterator");
        return super.electronContainers();
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getFirstAtom() {
        logger.debug("Getting first atom: ", super.getFirstAtom());
        return super.getFirstAtom();
    }

    /** {@inheritDoc} */
    @Override
    public IAtom getLastAtom() {
        logger.debug("Getting last atom: ", super.getLastAtom());
        return super.getLastAtom();
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomNumber(IAtom atom) {
        logger.debug("Getting atom number: ", atom);
        return super.indexOf(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getBondNumber(IAtom atom1, IAtom atom2) {
        logger.debug("Getting bond number: atom1=" + atom1, " atom2=" + atom2);
        return super.indexOf(super.getBond(atom1, atom2));
    }

    /** {@inheritDoc} */
    @Override
    public int getBondNumber(IBond bond) {
        logger.debug("Getting bond number: ", bond);
        return super.indexOf(bond);
    }

    /** {@inheritDoc} */
    @Override
    public int getLonePairNumber(ILonePair bond) {
        logger.debug("Getting lone pair number: ", bond);
        return super.indexOf(bond);
    }

    /** {@inheritDoc} */
    @Override
    public int getSingleElectronNumber(ISingleElectron bond) {
        logger.debug("Getting single electron number: ", bond);
        return super.indexOf(bond);
    }

    /** {@inheritDoc} */
    @Override
    public IElectronContainer getElectronContainer(int number) {
        logger.debug("Getting electron container at: ", number);
        return super.getElectronContainer(number);
    }

    /** {@inheritDoc} */
    @Override
    public IBond getBond(IAtom atom1, IAtom atom2) {
        logger.debug("Getting bond for atoms: atom1=" + atom1, " atom2=" + atom2);
        return super.getBond(atom1, atom2);
    }

    /** {@inheritDoc} */
    @Override
    public int getAtomCount() {
        logger.debug("Getting atom count");
        return super.getAtomCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getBondCount() {
        logger.debug("Getting bond count");
        return super.getBondCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getLonePairCount() {
        logger.debug("Getting lone pair count");
        return super.getLonePairCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getSingleElectronCount() {
        logger.debug("Getting single electron count");
        return super.getSingleElectronCount();
    }

    /** {@inheritDoc} */
    @Override
    public int getElectronContainerCount() {
        logger.debug("Getting electron container count");
        return super.getElectronContainerCount();
    }

    //	public IAtom[] getConnectedAtoms(IAtom atom) {
    //		logger.debug("Getting connected atoms for atom: ", atom);
    //		return super.getConnectedAtoms(atom);
    //	}

    /** {@inheritDoc} */
    @Override
    public List<IAtom> getConnectedAtomsList(IAtom atom) {
        logger.debug("Getting connecting atoms vector for atom: ", atom);
        return super.getConnectedAtomsList(atom);
    }

    //	public IBond[] getConnectedBonds(IAtom atom) {
    //		logger.debug("Getting connected bonds for atom: ", atom);
    //		return super.getConnectedBonds(atom);
    //	}

    /** {@inheritDoc} */
    @Override
    public List<IBond> getConnectedBondsList(IAtom atom) {
        logger.debug("Getting connected bonds vector for atom: ", atom);
        return super.getConnectedBondsList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public List<ILonePair> getConnectedLonePairsList(IAtom atom) {
        logger.debug("Getting lone pairs at atom: atom=" + atom,
                " lone pairs=" + super.getConnectedLonePairsCount(atom));
        return super.getConnectedLonePairsList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public List<ISingleElectron> getConnectedSingleElectronsList(IAtom atom) {
        logger.debug("Getting single electrons at atom: atom=" + atom,
                " single electrons=" + super.getConnectedSingleElectronsCount(atom));
        return super.getConnectedSingleElectronsList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public List<IElectronContainer> getConnectedElectronContainersList(IAtom atom) {
        logger.debug("Getting connected electron containers for atom: ", atom);
        return super.getConnectedElectronContainersList(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedAtomsCount(IAtom atom) {
        logger.debug("Getting connected atoms count for atom: ", atom);
        return super.getConnectedAtomsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedBondsCount(IAtom atom) {
        logger.debug("Getting connected bonds count for atom: ", atom);
        return super.getConnectedBondsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedLonePairsCount(IAtom atom) {
        logger.debug("Getting connected lone pairs count for atom: ", atom);
        return super.getConnectedLonePairsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public int getConnectedSingleElectronsCount(IAtom atom) {
        logger.debug("Getting connected single electrons count for atom: ", atom);
        return super.getConnectedSingleElectronsCount(atom);
    }

    /** {@inheritDoc} */
    @Override
    public double getBondOrderSum(IAtom atom) {
        logger.debug("Getting bond order sum for atom: ", atom);
        return super.getBondOrderSum(atom);
    }

    /** {@inheritDoc} */
    @Override
    public Order getMaximumBondOrder(IAtom atom) {
        logger.debug("Getting maximum bond order for atom: ", atom);
        return super.getMaximumBondOrder(atom);
    }

    /** {@inheritDoc} */
    @Override
    public Order getMinimumBondOrder(IAtom atom) {
        logger.debug("Getting minimum bond order for atom: ", atom);
        return super.getMinimumBondOrder(atom);
    }

    //	public void addElectronContainers(IAtomContainer atomContainer) {
    //		logger.debug("Adding electron containers from atom container: ", atomContainer);
    //		super.addElectronContainers(atomContainer);
    //	}

    /** {@inheritDoc} */
    @Override
    public void add(IAtomContainer atomContainer) {
        logger.debug("Adding atom container: ", atomContainer);
        super.add(atomContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void addAtom(IAtom atom) {
        logger.debug("Adding atom: ", atom);
        super.addAtom(atom);
    }

    /** {@inheritDoc} */
    @Override
    public void addBond(IBond bond) {
        logger.debug("Adding bond: ", bond);
        super.addBond(bond);
    }

    /** {@inheritDoc} */
    @Override
    public void addLonePair(ILonePair ec) {
        logger.debug("Adding lone pair: ", ec);
        super.addLonePair(ec);
    }

    /** {@inheritDoc} */
    @Override
    public void addSingleElectron(ISingleElectron ec) {
        logger.debug("Adding single electron: ", ec);
        super.addSingleElectron(ec);
    }

    /** {@inheritDoc} */
    @Override
    public void addElectronContainer(IElectronContainer electronContainer) {
        logger.debug("Adding electron container: ", electronContainer);
        super.addElectronContainer(electronContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void remove(IAtomContainer atomContainer) {
        logger.debug("Removing atom container: ", atomContainer);
        super.remove(atomContainer);
    }

    /** {@inheritDoc} */
    @Override
    public IElectronContainer removeElectronContainer(int position) {
        logger.debug("Removing electronContainer: ", position);
        return super.removeElectronContainer(position);
    }

    /** {@inheritDoc} */
    @Override
    public void removeElectronContainer(IElectronContainer electronContainer) {
        logger.debug("Removing electron container: ", electronContainer);
        super.removeElectronContainer(electronContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAtom(int position) {
        logger.debug("Removing atom: ", position);
        super.removeAtom(position);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAtom(IAtom atom) {
        logger.debug("Removing atom: ", atom);
        super.removeAtom(atom);
    }

    /** {@inheritDoc} */
    @Override
    public IBond removeBond(int pos) {
        logger.debug("Removing bond at " + pos);
        return super.removeBond(pos);
    }

    /** {@inheritDoc} */
    @Override
    public IBond removeBond(IAtom atom1, IAtom atom2) {
        logger.debug("Removing bond: atom1=" + atom1 + " atom2=" + atom2);
        return super.removeBond(atom1, atom2);
    }

    /** {@inheritDoc} */
    @Override
    public void removeBond(IBond bond) {
        logger.debug("Removing bond=" + bond);
        super.removeBond(bond);
    }

    /** {@inheritDoc} */
    @Override
    public ILonePair removeLonePair(int pos) {
        logger.debug("Removing bond at " + pos);
        return super.removeLonePair(pos);
    }

    /** {@inheritDoc} */
    @Override
    public void removeLonePair(ILonePair ec) {
        logger.debug("Removing bond=" + ec);
        super.removeLonePair(ec);
    }

    /** {@inheritDoc} */
    @Override
    public ISingleElectron removeSingleElectron(int pos) {
        logger.debug("Removing bond at " + pos);
        return super.removeSingleElectron(pos);
    }

    /** {@inheritDoc} */
    @Override
    public void removeSingleElectron(ISingleElectron ec) {
        logger.debug("Removing bond=" + ec);
        super.removeSingleElectron(ec);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAtomAndConnectedElectronContainers(IAtom atom) {
        logger.debug("Removing atom and connected electron containers: ", atom);
        super.removeAtomAndConnectedElectronContainers(atom);
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllElements() {
        logger.debug("Removing all elements");
        super.removeAllElements();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllElectronContainers() {
        logger.debug("Removing all electron containers");
        super.removeAllElectronContainers();
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllBonds() {
        logger.debug("Removing all bonds");
        super.removeAllBonds();
    }

    /** {@inheritDoc} */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order, IBond.Stereo stereo) {
        logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order + " stereo=" + stereo);
        super.addBond(atom1, atom2, order, stereo);
    }

    /** {@inheritDoc} */
    @Override
    public void addBond(int atom1, int atom2, IBond.Order order) {
        logger.debug("Adding bond: atom1=" + atom1 + " atom2=" + atom2, " order=" + order);
        super.addBond(atom1, atom2, order);
    }

    /** {@inheritDoc} */
    @Override
    public void addLonePair(int atomID) {
        logger.debug("Adding lone pair: ", atomID);
        super.addLonePair(atomID);
    }

    /** {@inheritDoc} */
    @Override
    public void addSingleElectron(int atomID) {
        logger.debug("Adding single electron: ", atomID);
        super.addSingleElectron(atomID);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IAtom atom) {
        logger.debug("Contains atom: ", atom);
        return super.contains(atom);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IBond bond) {
        logger.debug("Contains bond: ", bond);
        return super.contains(bond);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(ILonePair ec) {
        logger.debug("Contains lone pair: ", ec);
        return super.contains(ec);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(ISingleElectron ec) {
        logger.debug("Contains single electron: ", ec);
        return super.contains(ec);
    }

    /** {@inheritDoc} */
    @Override
    public boolean contains(IElectronContainer electronContainer) {
        logger.debug("Contains electron container: ", electronContainer);
        return super.contains(electronContainer);
    }

    /** {@inheritDoc} */
    @Override
    public void addListener(IChemObjectListener col) {
        logger.debug("Adding listener: ", col);
        super.addListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public int getListenerCount() {
        logger.debug("Getting listener count: ", super.getListenerCount());
        return super.getListenerCount();
    }

    /** {@inheritDoc} */
    @Override
    public void removeListener(IChemObjectListener col) {
        logger.debug("Removing listener: ", col);
        super.removeListener(col);
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged() {
        logger.debug("Notifying changed");
        super.notifyChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void notifyChanged(IChemObjectChangeEvent evt) {
        logger.debug("Notifying changed event: ", evt);
        super.notifyChanged(evt);
    }

    /** {@inheritDoc} */
    @Override
    public void setProperty(Object description, Object property) {
        logger.debug("Setting property: ", description + "=" + property);
        super.setProperty(description, property);
    }

    /** {@inheritDoc} */
    @Override
    public void removeProperty(Object description) {
        logger.debug("Removing property: ", description);
        super.removeProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public <T> T getProperty(Object description) {
        logger.debug("Getting property: ", description + "=" + super.getProperty(description));
        return super.getProperty(description);
    }

    /** {@inheritDoc} */
    @Override
    public Map<Object, Object> getProperties() {
        logger.debug("Getting properties");
        return super.getProperties();
    }

    /** {@inheritDoc} */
    @Override
    public String getID() {
        logger.debug("Getting ID: ", super.getID());
        return super.getID();
    }

    /** {@inheritDoc} */
    @Override
    public void setID(String identifier) {
        logger.debug("Setting ID: ", identifier);
        super.setID(identifier);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlag(int flagType, boolean flagValue) {
        logger.debug("Setting flag: ", flagType + "=" + flagValue);
        super.setFlag(flagType, flagValue);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getFlag(int flagType) {
        logger.debug("Setting flag: ", flagType + "=" + super.getFlag(flagType));
        return super.getFlag(flagType);
    }

    /** {@inheritDoc} */
    @Override
    public void addProperties(Map<Object, Object> properties) {
        logger.debug("Setting properties: ", properties);
        super.addProperties(properties);
    }

    /** {@inheritDoc} */
    @Override
    public void setFlags(boolean[] flagsNew) {
        logger.debug("Setting flags:", flagsNew.length);
        super.setFlags(flagsNew);
    }

    /** {@inheritDoc} */
    @Override
    public boolean[] getFlags() {
        logger.debug("Getting flags:", super.getFlags().length);
        return super.getFlags();
    }

    /** {@inheritDoc} */
    @Override
    public IBioPolymer clone() throws CloneNotSupportedException {
        IBioPolymer clone = null;
        try {
            clone = super.clone();
        } catch (Exception exception) {
            logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
            logger.debug(exception);
        }
        return clone;
    }

    /** {@inheritDoc} */
    @Override
    public IChemObjectBuilder getBuilder() {
        return DebugChemObjectBuilder.getInstance();
    }

    /** {@inheritDoc} */
    @Override
    public void stateChanged(IChemObjectChangeEvent event) {
        logger.debug("Receiving state changed event: ", event);
        super.stateChanged(event);
    }

    /** {@inheritDoc} */
    @Override
    public void addAtom(IAtom oAtom, IMonomer oMonomer) {
        logger.debug("Adding atom to monomer: ", oAtom, oMonomer);
        super.addAtom(oAtom, oMonomer);
    }

    /** {@inheritDoc} */
    @Override
    public int getMonomerCount() {
        logger.debug("Getting monomer count: ", super.getMonomerCount());
        return super.getMonomerCount();
    }

    /** {@inheritDoc} */
    @Override
    public IMonomer getMonomer(String cName) {
        logger.debug("Getting monomer for String: ", cName);
        return super.getMonomer(cName);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> getMonomerNames() {
        logger.debug("Getting monomer names");
        return super.getMonomerNames();
    }

    /** {@inheritDoc} */
    @Override
    public void removeMonomer(String name) {
        logger.debug("Removing monomer by string: ", name);
        super.removeMonomer(name);
    }

    /** {@inheritDoc} */
    @Override
    public void addAtom(IAtom oAtom, IStrand oStrand) {
        logger.debug("Adding stoms to strand: ", oAtom, oStrand);
        super.addAtom(oAtom, oStrand);
    }

    /** {@inheritDoc} */
    @Override
    public void addAtom(IAtom oAtom, IMonomer oMonomer, IStrand oStrand) {
        logger.debug("Adding stoms to strand/monomer: ", oAtom, oMonomer, oStrand);
        super.addAtom(oAtom, oMonomer, oStrand);
    }

    /** {@inheritDoc} */
    @Override
    public IMonomer getMonomer(String monName, String strandName) {
        logger.debug("Getting monomer from strand: ", monName, strandName);
        return super.getMonomer(monName, strandName);
    }

    /** {@inheritDoc} */
    @Override
    public int getStrandCount() {
        logger.debug("Getting strand count: ", super.getStrandCount());
        return super.getStrandCount();
    }

    /** {@inheritDoc} */
    @Override
    public IStrand getStrand(String cName) {
        logger.debug("Getting strand by name: ", cName);
        return super.getStrand(cName);
    }

    /** {@inheritDoc} */
    @Override
    public Collection<String> getStrandNames() {
        logger.debug("Getting strand names: ", super.getStrandNames());
        return super.getStrandNames();
    }

    /** {@inheritDoc} */
    @Override
    public void removeStrand(String name) {
        logger.debug("Removing strand by name: ", name);
        super.removeStrand(name);
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, IStrand> getStrands() {
        logger.debug("Getting strands: ", super.getStrands());
        return super.getStrands();
    }

}

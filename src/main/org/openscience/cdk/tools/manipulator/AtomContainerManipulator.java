/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.CDKAtomTypeMatcher;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.XMLIsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import static org.openscience.cdk.CDKConstants.SINGLE_OR_DOUBLE;

/**
 * Class with convenience methods that provide methods to manipulate
 * AtomContainer's. For example:
 * <pre>
 * AtomContainerManipulator.replaceAtomByAtom(container, atom1, atom2);
 * </pre>
 * will replace the Atom in the AtomContainer, but in all the ElectronContainer's
 * it participates too.
 *
 * @cdk.module standard
 * @cdk.githash
 *
 * @author  Egon Willighagen
 * @cdk.created 2003-08-07
 */
@TestClass("org.openscience.cdk.tools.manipulator.AtomContainerManipulatorTest")
public class AtomContainerManipulator {
    
    /**
     * Extract a substructure from an atom container, in the form of a new 
     * cloned atom container with only the atoms with indices in atomIndices and
     * bonds that connect these atoms.
     * <p/>
     * Note that this may result in a disconnected atom container. 
     * 
     * @param atomContainer the source container to extract from
     * @param atomIndices the indices of the substructure
     * @return a cloned atom container with a substructure of the source
     * @throws CloneNotSupportedException if the source container cannot be cloned
     */
    @TestMethod("testExtractSubstructure")
    public static IAtomContainer extractSubstructure(
            IAtomContainer atomContainer, int... atomIndices) throws CloneNotSupportedException {
        IAtomContainer substructure = (IAtomContainer) atomContainer.clone();
        int numberOfAtoms = substructure.getAtomCount();
        IAtom[] atoms = new IAtom[numberOfAtoms];
        for (int atomIndex = 0; atomIndex < numberOfAtoms; atomIndex++) {
            atoms[atomIndex] = substructure.getAtom(atomIndex);
        }
        
        Arrays.sort(atomIndices);
        for (int index = 0; index < numberOfAtoms; index++) {
            if (Arrays.binarySearch(atomIndices, index) < 0) {
                IAtom atom = atoms[index];
                substructure.removeAtomAndConnectedElectronContainers(atom);
            }
        }

        return substructure;
    }

	/**
	 * Returna an atom in an atomcontainer identified by id
	 *
	 * @param ac The AtomContainer to search in
	 * @param id The id to search for
	 * @return An atom having id id
	 * @throws CDKException There is no such atom
	 */
    @TestMethod("testGetAtomById_IAtomContainer_String")
    public static IAtom getAtomById(IAtomContainer ac, String id) throws CDKException{
		for(int i=0;i<ac.getAtomCount();i++){
			if(ac.getAtom(i).getID()!=null && ac.getAtom(i).getID().equals(id))
				return ac.getAtom(i);
		}
		throw new CDKException("no suc atom");
	}

    @TestMethod("testReplaceAtom")
    public static boolean replaceAtomByAtom(IAtomContainer container, IAtom atom, IAtom newAtom) {
        if (!container.contains(atom)) {
            // it should complain
	    return false;
        } else {
            container.setAtom(container.getAtomNumber(atom), newAtom);
            for (IElectronContainer eContainer : container.electronContainers()) {
                if (eContainer instanceof IBond) {
                    IBond bond = (IBond) eContainer;
                    if (bond.contains(atom)) {
                        for (int j = 0; j < bond.getAtomCount(); j++) {
                            if (atom.equals(bond.getAtom(j))) {
                                bond.setAtom(newAtom, j);
                            }
                        }
                    }
                } else if (eContainer instanceof ILonePair) {
                    ILonePair lonePair = (ILonePair) eContainer;
                    if (atom.equals(lonePair.getAtom())) {
                        lonePair.setAtom(newAtom);
                    }
                }
            }
            return true;
        }
    }


    /**
     * Get the summed charge of all atoms in an AtomContainer
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalCharge")
    public static double getTotalCharge(IAtomContainer atomContainer) {
        double charge = 0.0;
        for (IAtom atom : atomContainer.atoms()) {
            // we assume CDKConstant.UNSET is equal to 0
            Double thisCharge = atom.getCharge();
            if (thisCharge != CDKConstants.UNSET)
                charge += thisCharge;
        }
        return charge;
    }

    /**
     * Get the summed exact mass of all atoms in an AtomContainer. It
     * requires isotope information for all atoms to be set. Either set
     * this information using the {@link IsotopeFactory}, or use the
     * {@link MolecularFormulaManipulator#getMajorIsotopeMass(org.openscience.cdk.interfaces.IMolecularFormula)}
     * method, after converting the {@link IAtomContainer} to a
     * {@link IMolecularFormula} with the {@link MolecularFormulaManipulator}.
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed exact mass of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalExactMass_IAtomContainer")
    public static double getTotalExactMass(IAtomContainer atomContainer) {
        double mass = 0.0;
        for (IAtom atom : atomContainer.atoms()) {
            mass += atom.getExactMass();
        }
        return mass;
    }

    /**
     * Returns the molecular mass of the IAtomContainer. For the calculation it uses the
     * masses of the isotope mixture using natural abundances.
     *
     * @param       atomContainer
     * @cdk.keyword mass, molecular
     */
    @TestMethod("testGetNaturalExactMass_IAtomContainer")
    public static double getNaturalExactMass(IAtomContainer atomContainer) {
		 double mass = 0.0;
		 IsotopeFactory factory;
		 try {
			 factory = Isotopes.getInstance();
		 } catch (IOException e) {
			 throw new RuntimeException("Could not instantiate the IsotopeFactory.");
		 }
        for (IAtom atom : atomContainer.atoms()) {
            IElement isotopesElement = atom.getBuilder().newInstance(IElement.class,atom.getSymbol());
            mass += factory.getNaturalMass(isotopesElement);
        }
		 return mass;
    }
    /**
     * Get the summed natural abundance of all atoms in an AtomContainer
     *
     * @param  atomContainer The IAtomContainer to manipulate
     * @return The summed natural abundance of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalNaturalAbundance_IAtomContainer")
    public static double getTotalNaturalAbundance(IAtomContainer atomContainer) {
        double abundance =  1.0;
        for (IAtom iAtom : atomContainer.atoms()) abundance = abundance * iAtom.getNaturalAbundance();


        return abundance/Math.pow(100,atomContainer.getAtomCount());
    }

    /**
     * Get the total formal charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed formal charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalFormalCharge_IAtomContainer")
    public static int getTotalFormalCharge(IAtomContainer atomContainer) {
        int chargeP = getTotalNegativeFormalCharge(atomContainer);
        int chargeN = getTotalPositiveFormalCharge(atomContainer);

        return chargeP + chargeN;
    }
    /**
     * Get the total formal negative charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed negative formal charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalNegativeFormalCharge_IAtomContainer")
    public static int getTotalNegativeFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
        	double chargeI = atomContainer.getAtom(i).getFormalCharge();
        	if(chargeI < 0)
        		charge += chargeI;
        }
        return charge;
    }
    /**
     * Get the total positive formal charge on a molecule.
     *
     * @param atomContainer the atom container to consider
     * @return The summed positive formal charges of all atoms in this AtomContainer.
     */
    @TestMethod("testGetTotalPositiveFormalCharge_IAtomContainer")
    public static int getTotalPositiveFormalCharge(IAtomContainer atomContainer) {
        int charge = 0;
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
        	double chargeI = atomContainer.getAtom(i).getFormalCharge();
        	if(chargeI > 0)
        		charge += chargeI;
        }
        return charge;
    }

    /**
     * Counts the number of hydrogens on the provided IAtomContainer. As this
     * method will sum all implicit hydrogens on each atom it is important to
     * ensure the atoms have already been perceived (and thus have an implicit
     * hydrogen count) (see. {@link #percieveAtomTypesAndConfigureAtoms}).
     *
     * @param container the container to count the hydrogens on
     * @return the total number of hydrogens
     * @see org.openscience.cdk.interfaces.IAtom#getImplicitHydrogenCount()
     * @see #percieveAtomTypesAndConfigureAtoms
     * @throws IllegalArgumentException if the provided container was null
     */
    @TestMethod("testGetTotalHydrogenCount_IAtomContainer,testGetTotalHydrogenCount_IAtomContainer_zeroImplicit,testGetTotalHydrogenCount_IAtomContainer_nullImplicit,testGetTotalHydrogenCount_ImplicitHydrogens")
    public static int getTotalHydrogenCount(IAtomContainer container) {
        if(container == null)
            throw new IllegalArgumentException("null container provided");
        int hydrogens = 0;
        for (IAtom atom : container.atoms()) {

            if(Elements.HYDROGEN.getSymbol().equals(atom.getSymbol())) {
                hydrogens++;
            }

            // rare but a hydrogen may have an implicit hydrogen so we don't use 'else'
            Integer implicit = atom.getImplicitHydrogenCount();
            if (implicit != null) {
                hydrogens += implicit;
            }

        }
        return hydrogens;
    }


    /**
     * Counts the number of implicit hydrogens on the provided IAtomContainer.
     * As this method will sum all implicit hydrogens on each atom it is
     * important to ensure the atoms have already been perceived (and thus have
     * an implicit hydrogen count) (see.
     * {@link #percieveAtomTypesAndConfigureAtoms}).
     *
     * @param container the container to count the implicit hydrogens on
     * @return the total number of implicit hydrogens
     * @see org.openscience.cdk.interfaces.IAtom#getImplicitHydrogenCount()
     * @see #percieveAtomTypesAndConfigureAtoms
     * @throws IllegalArgumentException if the provided container was null
     */
    @TestMethod("testGetImplicitHydrogenCount_unperceived,testGetImplicitHydrogenCount_null,testGetImplicitHydrogenCount_adenine")
    public static int getImplicitHydrogenCount(IAtomContainer container){
        if(container == null)
            throw new IllegalArgumentException("null container provided");
        int count = 0;
        for(IAtom atom : container.atoms()){
            Integer implicit = atom.getImplicitHydrogenCount();
            if(implicit != null) {
                count += implicit;
            }
        }
        return count;
    }

    /**
     * Count explicit hydrogens.
     *
     * @param atomContainer the atom container to consider
     * @return The number of explicit hydrogens on the given IAtom.
     * @throws IllegalArgumentException if either the container or atom were null
     */
    @TestMethod("testCountExplicitH,testCountExplicitH_IAtomContainer_Null,testCountExplicitH_Null_IAtom")
    public static int countExplicitHydrogens(IAtomContainer atomContainer, IAtom atom) {
        if(atomContainer == null || atom == null )
            throw new IllegalArgumentException("null container or atom provided");
        int hCount = 0;
        for (IAtom connected : atomContainer.getConnectedAtomsList(atom)) {
            if (Elements.HYDROGEN.getSymbol().equals(connected.getSymbol())) {
                hCount++;
            }
        }
        return hCount;
    }

    /**
     * Adds explicit hydrogens (without coordinates) to the IAtomContainer,
     * equaling the number of set implicit hydrogens.
     *
     * @param atomContainer the atom container to consider
     * @cdk.keyword hydrogens, adding
     */
    @TestMethod("testConvertImplicitToExplicitHydrogens_IAtomContainer")
    public static void convertImplicitToExplicitHydrogens(IAtomContainer atomContainer) {
        List<IAtom> hydrogens = new ArrayList<IAtom>();
        List<IBond> newBonds = new ArrayList<IBond>();
        List<Integer> atomIndex = new ArrayList<Integer>();

        for (IAtom atom : atomContainer.atoms()) {
            if (!atom.getSymbol().equals("H")) {
                Integer hCount = atom.getImplicitHydrogenCount();
                if (hCount != null) {
                    for (int i = 0; i < hCount; i++) {

                        IAtom hydrogen = atom.getBuilder().newInstance(IAtom.class, "H");
                        hydrogen.setAtomTypeName("H");
                        hydrogens.add(hydrogen);
                        newBonds.add(atom.getBuilder().newInstance(IBond.class,
                                atom, hydrogen, CDKConstants.BONDORDER_SINGLE
                        ));
                    }
                    atomIndex.add(atomContainer.getAtomNumber(atom));
                }
            }
        }
        for (Integer index : atomIndex) atomContainer.getAtom(index).setImplicitHydrogenCount(0);
        for (IAtom atom : hydrogens) atomContainer.addAtom(atom);
        for (IBond bond : newBonds) atomContainer.addBond(bond);
    }

    /**
     * @return The summed implicit + explicit hydrogens of the given IAtom.
     */
    @TestMethod("testCountH")
    public static int countHydrogens(IAtomContainer atomContainer, IAtom atom) {
        int hCount = atom.getImplicitHydrogenCount() == CDKConstants.UNSET ? 0 : atom.getImplicitHydrogenCount();
        hCount += countExplicitHydrogens(atomContainer, atom);
        return hCount;
	}

    @TestMethod("testGetAllIDs_IAtomContainer")
    public static List<String> getAllIDs(IAtomContainer mol) {
    	List<String> idList = new ArrayList<String>();
        if (mol != null) {
            if (mol.getID() != null) idList.add(mol.getID());
            for (IAtom atom : mol.atoms()) {
                if (atom.getID() != null) idList.add(atom.getID());
            }

            for (IBond bond : mol.bonds()) {
                if (bond.getID() != null) idList.add(bond.getID());
            }
        }
        return idList;
    }

    /**
     * Produces an AtomContainer without explicit non stereo-relevant Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param atomContainer The AtomContainer from which to remove the hydrogens
     * @return              The molecule without non stereo-relevant Hs.
     * @cdk.keyword         hydrogens, removal
     */
    @TestMethod("testRemoveNonChiralHydrogens_IAtomContainer")
    public static IAtomContainer removeNonChiralHydrogens(IAtomContainer atomContainer) {

        Map<IAtom, IAtom> map = new HashMap<IAtom, IAtom>(); // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>(); // lists removed Hs.

        // Clone atoms except those to be removed.
        IAtomContainer mol = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        int count = atomContainer.getAtomCount();

        for (int i = 0; i < count; i++) {

            // Clone/remove this atom?
            IAtom atom = atomContainer.getAtom(i);
            boolean addToRemove = false;
            if (atom.getSymbol().equals("H")) {
                // test whether connected to a single hetero atom only, otherwise keep
                if (atomContainer.getConnectedAtomsList(atom).size() == 1) {
                    IAtom neighbour = atomContainer.getConnectedAtomsList(atom).get(0);
                    // keep if the neighbouring hetero atom has stereo information, otherwise continue checking
                    Integer stereoParity = neighbour.getStereoParity();
                    if (stereoParity == null || stereoParity == 0) {
                        addToRemove = true;
                        // keep if any of the bonds of the hetero atom have stereo information
                        for (IBond bond : atomContainer.getConnectedBondsList(neighbour)) {
                            IBond.Stereo bondStereo = bond.getStereo();
                            if (bondStereo != null && bondStereo != IBond.Stereo.NONE) addToRemove = false;
                            IAtom neighboursNeighbour = bond.getConnectedAtom(neighbour);
                            // remove in any case if the hetero atom is connected to more than one hydrogen
                            if (neighboursNeighbour.getSymbol().equals("H") && neighboursNeighbour != atom) {
                                addToRemove = true;
                                break;
                            }
                        }
                    }
                }
            }

            if (addToRemove) remove.add(atom);
            else addClone(atom, mol, map);
        }

        // rescue any false positives, i.e., hydrogens that are stereo-relevant
        // the use of IStereoElement is not fully integrated yet to describe stereo information
        for (IStereoElement stereoElement : atomContainer.stereoElements()) {
            if (stereoElement instanceof ITetrahedralChirality) {
                ITetrahedralChirality tetChirality = (ITetrahedralChirality) stereoElement;
                for (IAtom atom : tetChirality.getLigands()) {
                    if (atom.getSymbol().equals("H") && remove.contains(atom)) {
                        remove.remove(atom);
                        addClone(atom, mol, map);
                    }
                }
            }
        }

        // Clone bonds except those involving removed atoms.
        count = atomContainer.getBondCount();
        for (int i = 0; i < count; i++) {
            // Check bond.
            final IBond bond = atomContainer.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0; k < length; k++) {
                if (remove.contains(bond.getAtom(k))) {
                    removedBond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!removedBond) {
                IBond clone = null;
                try {
                    clone = (IBond) atomContainer.getBond(i).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                assert clone != null;
                clone.setAtoms(new IAtom[]{map.get(bond.getAtom(0)), map.get(bond.getAtom(1))});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom aRemove : remove) {
            // Process neighbours.
            for (IAtom iAtom : atomContainer.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                if (neighb == null) continue; // since for the case of H2, neight H has a heavy atom neighbor
                neighb.setImplicitHydrogenCount(
                        (neighb.getImplicitHydrogenCount() == null ? 0 : neighb.getImplicitHydrogenCount()) + 1);
            }
        }
        for(IAtom atom : mol.atoms()){
            if(atom.getImplicitHydrogenCount()==null)
                atom.setImplicitHydrogenCount(0);
        }
        mol.setProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());

        return (mol);
    }

    private static void addClone(IAtom atom, IAtomContainer mol, Map<IAtom, IAtom> map) {

        IAtom clonedAtom = null;
        try {
            clonedAtom = (IAtom) atom.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        mol.addAtom(clonedAtom);
        map.put(atom, clonedAtom);
    }

    /**
     * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param atomContainer The AtomContainer from which to remove the hydrogens
     * @return              The molecule without Hs.
     * @cdk.keyword         hydrogens, removal
     */
    @TestMethod("testRemoveHydrogens_IAtomContainer")
    public static IAtomContainer removeHydrogens(IAtomContainer atomContainer)
    {
        Map<IAtom, IAtom> map = new HashMap<IAtom,IAtom>();        // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>();  // lists removed Hs.

        // Clone atoms except those to be removed.
        IAtomContainer mol = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        int count = atomContainer.getAtomCount();
        for (int i = 0;
                i < count;
                i++)
        {
            // Clone/remove this atom?
            IAtom atom = atomContainer.getAtom(i);
            if (!atom.getSymbol().equals("H"))
            {
                IAtom clonedAtom = null;
				try {
					clonedAtom = (IAtom) atom.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                mol.addAtom(clonedAtom);
                map.put(atom, clonedAtom);
            }
            else
            {
                remove.add(atom);   // maintain list of removed H.
            }
        }

        // Clone bonds except those involving removed atoms.
        count = atomContainer.getBondCount();
        for (int i = 0;
                i < count;
                i++)
        {
            // Check bond.
            final IBond bond = atomContainer.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0;
                    k < length;
                    k++)
            {
                if (remove.contains(bond.getAtom(k)))
                {
                    removedBond = true;
                    break;
                }
            }

            // Clone/remove this bond?
            if (!removedBond)
                // if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))
            {
                IBond clone = null;
				try {
					clone = (IBond) atomContainer.getBond(i).clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                assert clone != null;
                clone.setAtoms(new IAtom[]{(IAtom) map.get(bond.getAtom(0)), (IAtom) map.get(bond.getAtom(1))});
                mol.addBond(clone);
            }
        }

        // Recompute hydrogen counts of neighbours of removed Hydrogens.
        for (IAtom aRemove : remove) {
            // Process neighbours.
            for (IAtom iAtom : atomContainer.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                if (neighb == null) continue; // since for the case of H2, neight H has a heavy atom neighbor
                neighb.setImplicitHydrogenCount(
                        (neighb.getImplicitHydrogenCount() == null ? 0 : neighb.getImplicitHydrogenCount())
                                + 1
                );
            }
        }
        for(IAtom atom : mol.atoms()){
            if(atom.getImplicitHydrogenCount()==null)
                atom.setImplicitHydrogenCount(0);
        }
        mol.setProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());

        return (mol);
    }

	/**
	 * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
	 * Hs bonded to more than one heavy atom are preserved.  The new molecule is a deep copy.
	 *
	 * @return         The mol without Hs.
	 * @cdk.keyword    hydrogens, removal
	 */
    @TestMethod("testRemoveHydrogensPreserveMultiplyBonded")
	public static IAtomContainer removeHydrogensPreserveMultiplyBonded(IAtomContainer ac) {
		List<IAtom> h = new ArrayList<IAtom>();
		// H list.
		List<IAtom> multi_h = new ArrayList<IAtom>();
		// multiply bonded H

		// Find multiply bonded H.
		int count = ac.getBondCount();
		for (int i = 0; i < count; i++) {
            for (IAtom atom : ac.getBond(i).atoms()) {
                if (atom.getSymbol().equals("H")) {
                    (h.contains(atom) ? multi_h : h).add(atom);
                }
            }
		}

		return removeHydrogens(ac, multi_h);
	}

	/**
	 * Produces an AtomContainer without explicit Hs (except those listed) but with H count from one with Hs.
	 * The new molecule is a deep copy.
	 *
	 * @param  preserve  a list of H atoms to preserve.
	 * @return           The mol without Hs.
	 * @cdk.keyword      hydrogens, removal
	 */
	private static IAtomContainer removeHydrogens(IAtomContainer ac, List<IAtom> preserve) {
		Map<IAtom,IAtom> map = new HashMap<IAtom,IAtom>();
		// maps original atoms to clones.
		List<IAtom> remove = new ArrayList<IAtom>();
		// lists removed Hs.

		// Clone atoms except those to be removed.
		IAtomContainer mol = ac.getBuilder().newInstance(IAtomContainer.class);
		int count = ac.getAtomCount();
		for (int i = 0;
				i < count;
				i++) {
			// Clone/remove this atom?
			IAtom atom = ac.getAtom(i);
			if (!atom.getSymbol().equals("H") || preserve.contains(atom)) {
				IAtom a = null;
				try {
					a = (IAtom) atom.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				a.setImplicitHydrogenCount(0);
				mol.addAtom(a);
				map.put(atom, a);
			} else {
				remove.add(atom);
				// maintain list of removed H.
			}
		}

		// Clone bonds except those involving removed atoms.
		count = ac.getBondCount();
		for (int i = 0;
				i < count;
				i++) {
			// Check bond.
			final IBond bond = ac.getBond(i);
			IAtom atom0 = bond.getAtom(0);
			IAtom atom1 = bond.getAtom(1);
			boolean remove_bond = false;
			for (IAtom atom : bond.atoms()){
				if (remove.contains(atom)) {
					remove_bond = true;
					break;
				}
			}

			// Clone/remove this bond?
			if (!remove_bond) {
				// if (!remove.contains(atoms[0]) && !remove.contains(atoms[1]))

				IBond clone = null;
				try {
					clone = (IBond) ac.getBond(i).clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				clone.setAtoms(new IAtom[]{map.get(atom0), map.get(atom1)});
				mol.addBond(clone);
			}
		}

		// Recompute hydrogen counts of neighbours of removed Hydrogens.
		for (IAtom removeAtom : remove) {
			// Process neighbours.
            for (IAtom  neighbor : ac.getConnectedAtomsList(removeAtom)) {
                final IAtom neighb = map.get(neighbor);
				neighb.setImplicitHydrogenCount(neighb.getImplicitHydrogenCount() + 1);
            }
		}

		return (mol);
	}

	/**
     * Sets a property on all <code>Atom</code>s in the given container.
     */
    public static void setAtomProperties(IAtomContainer container, Object propKey, Object propVal) {
        if (container != null) {
            for (IAtom atom : container.atoms()) {
                atom.setProperty(propKey, propVal);
            }
        }
    }

	/**
	 *  A method to remove ElectronContainerListeners.
	 *  ElectronContainerListeners are used to detect changes
	 *  in ElectronContainers (like bonds) and to notifiy
	 *  registered Listeners in the event of a change.
	 *  If an object looses interest in such changes, it should
	 *  unregister with this AtomContainer in order to improve
	 *  performance of this class.
	 */
	public static void unregisterElectronContainerListeners(IAtomContainer container)
	{
        for (IElectronContainer electronContainer : container.electronContainers())
            electronContainer.removeListener(container);
	}

	/**
	 *  A method to remove AtomListeners.
	 *  AtomListeners are used to detect changes
	 *  in Atom objects within this AtomContainer and to notifiy
	 *  registered Listeners in the event of a change.
	 *  If an object looses interest in such changes, it should
	 *  unregister with this AtomContainer in order to improve
	 *  performance of this class.
	 */
	public static void unregisterAtomListeners(IAtomContainer container)
	{
        for (IAtom atom : container.atoms()) atom.removeListener(container);
	}

	/**
	 * Compares this AtomContainer with another given AtomContainer and returns
	 * the Intersection between them. <p>
	 *
	 * <b>Important Note</b> : This is not the maximum common substructure.
	 *
	 * @param  container1 an AtomContainer object
	 * @param  container2 an AtomContainer object
	 * @return            An AtomContainer containing the intersection between
	 *                    container1 and container2
	 */
    @TestMethod("testGetIntersection_IAtomContainer_IAtomContainer")
    public static IAtomContainer getIntersection(
		IAtomContainer container1, IAtomContainer container2)
	{
		IAtomContainer intersection = container1.getBuilder().newInstance(IAtomContainer.class);

		for (int i = 0; i < container1.getAtomCount(); i++)
		{
			if (container2.contains(container1.getAtom(i)))
			{
				intersection.addAtom(container1.getAtom(i));
			}
		}
		for (int i = 0; i < container1.getElectronContainerCount(); i++)
		{
			if (container2.contains(container1.getElectronContainer(i)))
			{
				intersection.addElectronContainer(container1.getElectronContainer(i));
			}
		}
		return intersection;
	}

	/**
	 * Constructs an array of Atom objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetAtomArray_IAtomContainer")
    public static IAtom[] getAtomArray(IAtomContainer container) {
		IAtom[] ret = new IAtom[container.getAtomCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getAtom(i);
		return ret;
	}

	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetAtomArray_List")
    public static IAtom[] getAtomArray(java.util.List<IAtom> list) {
		IAtom[] ret = new IAtom[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}

	/**
	 * Constructs an array of Bond objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Bond objects.
	 */
    @TestMethod("testGetBondArray_IAtomContainer")
    public static IBond[] getBondArray(IAtomContainer container) {
		IBond[] ret = new IBond[container.getBondCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getBond(i);
		return ret;
	}

	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
    @TestMethod("testGetBondArray_List")
    public static IBond[] getBondArray(java.util.List<IBond> list) {
		IBond[] ret = new IBond[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}

	/**
	 * Constructs an array of Bond objects from an AtomContainer.
	 * @param  container The original AtomContainer.
	 * @return The array of Bond objects.
	 */
	public static IElectronContainer[] getElectronContainerArray(IAtomContainer container) {
		IElectronContainer[] ret = new IElectronContainer[container.getElectronContainerCount()];
		for (int i = 0; i < ret.length; ++i) ret[i] = container.getElectronContainer(i);
		return ret;
	}

	/**
	 * Constructs an array of Atom objects from a List of Atom objects.
	 * @param  list The original List.
	 * @return The array of Atom objects.
	 */
	public static IElectronContainer[] getElectronContainerArray(java.util.List<IElectronContainer> list) {
		IElectronContainer[] ret = new IElectronContainer[list.size()];
		for (int i = 0; i < ret.length; ++i) ret[i] = list.get(i);
		return ret;
	}

	/**
	 * Convenience method to perceive atom types for all <code>IAtom</code>s in the
	 * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
	 * matcher finds a matching atom type, the <code>IAtom</code> will be configured
	 * to have the same properties as the <code>IAtomType</code>. If no matching atom
	 * type is found, no configuration is performed.
	 * <b>This method overwrites existing values.</b>
	 *
	 * @param container
	 * @throws CDKException
	 */
    @TestMethod("testPerceiveAtomTypesAndConfigureAtoms")
    public static void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
		CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
        for (IAtom atom : container.atoms()) {
            if (!(atom instanceof IPseudoAtom)) {
                IAtomType matched = matcher.findMatchingAtomType(container, atom);
                if (matched != null) AtomTypeManipulator.configure(atom, matched);
            }
        }
	}

    /**
     * Convenience method to perceive atom types for all <code>IAtom</code>s in the
     * <code>IAtomContainer</code>, using the <code>CDKAtomTypeMatcher</code>. If the
     * matcher finds a matching atom type, the <code>IAtom</code> will be configured
     * to have the same properties as the <code>IAtomType</code>. If no matching atom
     * type is found, no configuration is performed.
     * <b>This method overwrites existing values.</b>
     *
     * @param container
     * @throws CDKException
     */
      @TestMethod("testPerceiveAtomTypesAndConfigureAtoms")
      public static void percieveAtomTypesAndConfigureUnsetProperties(IAtomContainer container) throws CDKException {
      CDKAtomTypeMatcher matcher = CDKAtomTypeMatcher.getInstance(container.getBuilder());
          for (IAtom atom : container.atoms()) {
              IAtomType matched = matcher.findMatchingAtomType(container, atom);
              if (matched != null) AtomTypeManipulator.configureUnsetProperties(atom, matched);
          }
    }


    /**
     * This method will reset all atom configuration to UNSET.
     *
     * This method is the reverse of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * and after a call to this method all atoms will be "unconfigured".
     *
     * Note that it is not a complete reversal of {@link #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)}
     * since the atomic symbol of the atoms remains unchanged. Also, all the flags that were set
     * by the configuration method (such as IS_HYDROGENBOND_ACCEPTOR or ISAROMATIC) will be set to False.
     *
     * @param container The molecule, whose atoms are to be unconfigured
     * @see #percieveAtomTypesAndConfigureAtoms(org.openscience.cdk.interfaces.IAtomContainer)
     */
    @TestMethod("testClearConfig")
    public static void clearAtomConfigurations(IAtomContainer container) {
        for (IAtom atom : container.atoms()) {
            atom.setAtomTypeName((String) CDKConstants.UNSET);
            atom.setMaxBondOrder((IBond.Order) CDKConstants.UNSET);
            atom.setBondOrderSum((Double) CDKConstants.UNSET);
            atom.setCovalentRadius((Double) CDKConstants.UNSET);
            atom.setValency((Integer) CDKConstants.UNSET);
            atom.setFormalCharge((Integer) CDKConstants.UNSET);
            atom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
            atom.setFormalNeighbourCount((Integer) CDKConstants.UNSET);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR, false);
            atom.setFlag(CDKConstants.IS_HYDROGENBOND_DONOR, false);
            atom.setProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT, CDKConstants.UNSET);
            atom.setFlag(CDKConstants.ISAROMATIC, false);
            atom.setProperty("org.openscience.cdk.renderer.color", CDKConstants.UNSET);
            atom.setAtomicNumber((Integer) CDKConstants.UNSET);
            atom.setExactMass((Double) CDKConstants.UNSET);
        }
    }


    /**
	 * Returns the sum of bond orders, where a single bond counts as one
	 * <i>single bond equivalent</i>, a double as two, etc.
	 */
    @TestMethod("testGetSBE")
    public static int getSingleBondEquivalentSum(IAtomContainer container) {
		int sum = 0;
        for (IBond bond : container.bonds()) {
            IBond.Order order = bond.getOrder();
            if(order != null) {
                sum += order.numeric();
            }
        }
		return sum;
	}

    @TestMethod("testGetMaxBondOrder_IAtomContainer")
    public static IBond.Order getMaximumBondOrder(IAtomContainer container) {
		return BondManipulator.getMaximumBondOrder(container.bonds().iterator());
	}
    /**
	 * Returns a set of nodes excluding all the hydrogens.
	 *
	 * @return         The heavyAtoms value
	 * @cdk.keyword    hydrogens, removal
	 */
    @TestMethod("testGetHeavyAtoms_IAtomContainer")
	public static  List<IAtom> getHeavyAtoms(IAtomContainer container) {
		List<IAtom> newAc = new ArrayList<IAtom>();
		for (int f = 0; f < container.getAtomCount(); f++) {
			if (!container.getAtom(f).getSymbol().equals("H")) {
				newAc.add(container.getAtom(f));
			}
		}
		return newAc;
	}


    /**
     * Generates a cloned atomcontainer with all atoms being carbon, all bonds
     * being single non-aromatic
     *
     * @param atomContainer The input atomcontainer
     * @return The new atomcontainer
     * @throws CloneNotSupportedException The atomcontainer cannot be cloned
     * @deprecated not all attributes are removed producing unexpected results, use
     *             {@link #anonymise}
     */
    @TestMethod("testCreateAnyAtomAnyBondAtomContainer_IAtomContainer")
    public static IAtomContainer createAllCarbonAllSingleNonAromaticBondAtomContainer(
			IAtomContainer atomContainer) throws CloneNotSupportedException{
			IAtomContainer query = (IAtomContainer) atomContainer.clone();
			for (int i = 0; i < query.getBondCount(); i++) {
				query.getBond(i).setOrder(IBond.Order.SINGLE);
				query.getBond(i).setFlag(CDKConstants.ISAROMATIC, false);
				query.getBond(i).setFlag(CDKConstants.SINGLE_OR_DOUBLE, false);
				query.getBond(i).getAtom(0).setSymbol("C");
				query.getBond(i).getAtom(0).setHybridization(null);
				query.getBond(i).getAtom(1).setSymbol("C");
				query.getBond(i).getAtom(1).setHybridization(null);
				query.getBond(i).getAtom(0).setFlag(CDKConstants.ISAROMATIC, false);
				query.getBond(i).getAtom(1).setFlag(CDKConstants.ISAROMATIC, false);
			}
			return query;
	}

    /**
     * Anonymise the provided container to single-bonded carbon atoms. No
     * information other then the connectivity from the original container is
     * retrained.
     *
     * @param src an atom container
     * @return anonymised container
     */
    @TestMethod("testAnonymise")
    public static IAtomContainer anonymise(IAtomContainer src) {

        IChemObjectBuilder builder = src.getBuilder();

        IAtom[] atoms = new IAtom[src.getAtomCount()];
        IBond[] bonds = new IBond[src.getBondCount()];

        for (int i = 0; i < atoms.length; i++) {
            atoms[i] = builder.newInstance(IAtom.class, "C");
        }
        for (int i = 0; i < bonds.length; i++) {
            IBond bond = src.getBond(i);
            int u = src.getAtomNumber(bond.getAtom(0));
            int v = src.getAtomNumber(bond.getAtom(1));
            bonds[i] = builder.newInstance(IBond.class, atoms[u], atoms[v]);
        }

        IAtomContainer dest = builder
                .newInstance(IAtomContainer.class, 0, 0, 0, 0);
        dest.setAtoms(atoms);
        dest.setBonds(bonds);
        return dest;
    }

	/**
	 * Returns the sum of the bond order equivalents for a given IAtom. It
	 * considers single bonds as 1.0, double bonds as 2.0, triple bonds as 3.0,
	 * and quadruple bonds as 4.0.
	 *
	 * @param  atom  The atom for which to calculate the bond order sum
	 * @return       The number of bond order equivalents for this atom
	 */
    @TestMethod("testBondOrderSum")
	public static double getBondOrderSum(IAtomContainer container, IAtom atom) {
		double count = 0;
		for (IBond bond : container.getConnectedBondsList(atom)) {
            IBond.Order order = bond.getOrder();
            if(order != null) {
                count += order.numeric();
            }
        }
		return count;
	}    

    /**
     * Assigns {@link CDKConstants#SINGLE_OR_DOUBLE} flags to the bonds of
     * a container. The single or double flag indicates uncertainty of bond
     * order and in this case is assigned to all aromatic bonds (and atoms)
     * which occur in rings. If any such bonds are found the flag is also set
     * on the container.
     * 
     * <blockquote><pre>
     *     SmilesParser parser = new SmilesParser(...);
     *     parser.setPreservingAromaticity(true);
     *                                                                    
     *     IAtomContainer biphenyl = parser.parseSmiles("c1cccc(c1)c1ccccc1");
     *     
     *     AtomContainerManipulator.setSingleOrDoubleFlags(biphenyl);
     * </pre></blockquote>
     * 
     * @param ac container to which the flags are assigned
     * @return the input for convenience            
     */
    @TestMethod("setSingleOrDoubleFlags")
    public static IAtomContainer setSingleOrDoubleFlags(IAtomContainer ac) {
        // note - we could check for any aromatic bonds to avoid RingSearch but
        // RingSearch is fast enough it probably wouldn't do much to check
        // before hand
        RingSearch rs = new RingSearch(ac);
        boolean singleOrDouble = false;
        for(IBond bond : rs.ringFragments().bonds()) {
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                bond.setFlag(SINGLE_OR_DOUBLE, true);
                bond.getAtom(0).setFlag(SINGLE_OR_DOUBLE, true);
                bond.getAtom(1).setFlag(SINGLE_OR_DOUBLE, true);
                singleOrDouble = singleOrDouble | true;
            }
        } 
        if (singleOrDouble) {
            ac.setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        }
        return ac;
    }
}


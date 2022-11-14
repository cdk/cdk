/*
 * Copyright (c) 2022 Julian Zander <zanderjulian@gmx.de>
 *                    Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.tools.scaffold;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fragment.MurckoFragmenter;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.CycleFinder;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeMap;

/**
 * This class is designed to generate different molecule scaffolds and frameworks.
 * It contains several other methods for the decomposition of molecules.
 * All details of the functionality and implementation are described in <a href="https://doi.org/10.1186/s13321-022-00656-x">
 * Schaub et al. "Scaffold Generator: a Java library implementing molecular scaffold functionalities in the Chemistry
 * Development Kit (CDK)" (J Cheminform 14, 79, 2022)</a><p>
 *
 * Furthermore, the molecules can be decomposed according to the <a href="https://doi.org/10.1021/ci600338x">
 * Schuffenhauer rules</a> and a generation of all possible fragments,
 * which can be created by the iterative removal of the rings, is also possible. <br>
 * The resulting molecular fragments can be organised in the form of a tree or a network.
 * The network approach is based on the <a href="https://doi.org/10.1021/ci2000924">
 * Mining for Bioactive Scaffolds with Scaffold Networks</a> paper.
 * Different trees or networks can also be merged together.
 *
 * @author Julian Zander, Jonas Schaub (zanderjulian@gmx.de, jonas.schaub@uni-jena.de)
 * @version 1.0.4.0
 */
public class ScaffoldGenerator {
    /**
     * Enum with which the type of scaffolds to be generated can be set. These scaffolds are then used for the rest of the processing.
     */
    public enum ScaffoldModeOption {

        /**
         * Terminal side chains of the molecule are removed except for any atoms non-single bonded
         * directly to linkers or rings, as it is e.g. defined in <a href="https://doi.org/10.1021/ci600338x">
         * "The Scaffold Tree − Visualization of the Scaffold Universe by Hierarchical Scaffold Classification"</a>.
         */
        SCAFFOLD(),

        /**
         * Murcko frameworks are generated. Based on <a href="https://doi.org/10.1021/jm9602928">
         * "The Properties of Known Drugs. 1. Molecular Frameworks"</a> by Bemis and Murcko 1996.
         * All terminal side chains are removed and only linkers and rings are retained.
         */
        MURCKO_FRAMEWORK(),

        /**
         * All side chains are removed, all bonds are converted into single bonds and all atoms are converted into carbons.
         * Naming is based on <a href="https://doi.org/10.1186/s13321-021-00526-y">
         * "Molecular Anatomy: a new multi‑dimensional hierarchical scaffold analysis tool"</a>
         * by Manelfi et al. 2021.
         */
        BASIC_WIRE_FRAME(),

        /**
         * All side chains are removed and multiple bonds are converted to single bonds, but the atomic elements remain.
         */
        ELEMENTAL_WIRE_FRAME(),

        /**
         * All side chains are removed and all atoms are converted into carbons. The order of the remaining bonds is not changed.
         * Naming is based on <a href="https://doi.org/10.1186/s13321-021-00526-y">
         * "Molecular Anatomy: a new multi‑dimensional hierarchical scaffold analysis tool"</a>
         * by Manelfi et al. 2021.
         */
        BASIC_FRAMEWORK()
    }

    /**
     * Property of the atoms according to which they are counted and identified.
     */
    public static final String SCAFFOLD_ATOM_COUNTER_PROPERTY = "SCAFFOLD_ATOM_COUNTER_PROPERTY";

    /**
     * Cycle finder used to detect rings.
     */
    public static final CycleFinder CYCLE_FINDER = Cycles.relevant();

    /**
     * Property is true if the backup cycle finder is to be used instead of the normal cycle finder.
     */
    public static final String CYCLE_FINDER_BACKUP_PROPERTY = "CYCLE_FINDER_BACKUP_PROPERTY";

    /**
     * Backup cycle finder used to detect rings.
     * The relevant cycle finder has problems with a few molecules and also finds too many rings in some molecules.
     * Therefore, the mcb is used in these cases.
     */
    public static final CycleFinder CYCLE_FINDER_BACKUP = Cycles.mcb();

    /**
     * Default setting for whether the aromaticity should be determined.
     * By default, the aromaticity is determined.
     */
    public static final boolean DETERMINE_AROMATICITY_SETTING_DEFAULT = true;

    /**
     * Default setting for which aromaticity model should be used.
     * By default, Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet()) is used.
     */
    public static final Aromaticity AROMATICITY_MODEL_SETTING_DEFAULT = new Aromaticity(ElectronDonation.cdk(), Cycles.cdkAromaticSet());

    /**
     * Default setting for which SmilesGenerator should be used.
     * By default, unique SMILES are used.
     */
    public static final SmilesGenerator SMILES_GENERATOR_SETTING_DEFAULT = new SmilesGenerator(SmiFlavor.Unique | SmiFlavor.UseAromaticSymbols);

    /**
     * Default setting for whether rule prioritisation rule 7 of the Schuffenhauer scaffold tree fragmentation should be
     * applied. By default, rule 7 is applied.
     */
    public static final boolean RULE_SEVEN_APPLIED_SETTING_DEFAULT = true;

    /**
     * Default setting for whether hybridisation should only be retained for aromatic bonds.
     * By default, the hybridisation of all bonds is retained.
     */
    public static final boolean RETAIN_ONLY_HYBRIDISATIONS_AT_AROMATIC_BONDS_SETTING_DEFAULT = false;

    /**
     * Default setting for which scaffold mode should be used.
     * By default, ScaffoldModeOption.SCAFFOLD is used.
     */
    public static final ScaffoldModeOption SCAFFOLD_MODE_OPTION_DEFAULT = ScaffoldModeOption.SCAFFOLD;

    /**
     * Specifies whether the aromaticity is to be taken into account.
     */
    private boolean determineAromaticitySetting;

    /**
     * Aromaticity model used to determine the aromaticity of the molecules.
     */
    private Aromaticity aromaticityModelSetting;

    /**
     * SmilesGenerator used to generate SMILES from molecules
     */
    private SmilesGenerator smilesGeneratorSetting;

    /**
     * Indicates whether rule 7 of the Schuffenhauer scaffold tree fragmentation is executed.
     * It can be useful to turn off rule 7 explicitly,
     * as it is only relevant for a relatively small number of molecules, but it increases the computing time.
     */
    private boolean ruleSevenAppliedSetting;

    /**
     * Currently used ScaffoldMode.
     */
    private ScaffoldModeOption scaffoldModeSetting;

    /**
     * With this setting, only the hybridisation of aromatic atoms can be obtained.
     */
    private boolean retainOnlyHybridisationsAtAromaticBondsSetting;

    /**
     * Counts logged exceptions.
     */
    private int tmpLogExceptionCounter;

    /**
     * Default logger.
     */
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(ScaffoldGenerator.class);

    /**
     * The only constructor of this class. Sets all settings to their default values.
     */
    public ScaffoldGenerator() {
        this.tmpLogExceptionCounter = 0;
        this.restoreDefaultSettings();
    }

    /**
     * Specifies whether the aromaticity is to be taken into account.
     * @return true if the aromaticity is determined
     */
    public boolean isAromaticityDetermined() {
        return this.determineAromaticitySetting;
    }

    /**
     * Returns the currently applied Aromaticity model.
     * This consists of the CycleFinder and the ElectronDonation Model.
     * @return the Aromaticity model
     */
    public Aromaticity getAromaticityModel() {
        return this.aromaticityModelSetting;
    }

    /**
     * Returns the currently applied SmilesGenerator.
     * @return the SmilesGenerator
     */
    public SmilesGenerator getSmilesGenerator() {
        return this.smilesGeneratorSetting;
    }

    /**
     * Indicates whether rule 7 of the Schuffenhauer scaffold tree fragmentation is executed.
     * It can be useful to turn off rule 7 explicitly,
     * as it is only relevant for a relatively small number of molecules, but it increases the computing time.
     * @return true if rule 7 is applied
     */
    public boolean isRuleSevenApplied() {
        return this.ruleSevenAppliedSetting;
    }

    /**
     * Returns the currently applied ScaffoldMode.
     * @return the now used scaffold ScaffoldMode
     */
    public ScaffoldModeOption getScaffoldModeSetting() {
        return this.scaffoldModeSetting;
    }

    /**
     * With this setting, only the hybridisation of aromatic atoms can be obtained.
     * @return true if only the hybridisation of aromatic atoms is obtained
     */
    public boolean areOnlyHybridisationsAtAromaticBondsRetained() {
        return this.retainOnlyHybridisationsAtAromaticBondsSetting;
    }

    /**
     * Sets the option to not determine the aromaticity.
     * If false, then no structures labelled as aromatic are created and previously existing ones are not changed.
     * @param anIsAromaticitySet if true the aromaticity is determined
     */
    public void setDetermineAromaticitySetting(boolean anIsAromaticitySet) {
        this.determineAromaticitySetting = anIsAromaticitySet;
    }

    /**
     * Sets the applied aromaticity model. This consists of the CycleFinder and the ElectronDonation Model.
     * Must not be null. However, the aromaticity model is also not used if {@link ScaffoldGenerator#determineAromaticitySetting} == false.
     * @param anAromaticity the new Aromaticity model
     * @throws NullPointerException if parameter is null
     */
    public void setAromaticityModelSetting(Aromaticity anAromaticity) throws NullPointerException {
        Objects.requireNonNull(anAromaticity, "Given aromaticity model must not be null. " +
                "The aromaticity detection can instead be deactivated via setDetermineAromaticitySetting(false).");
        this.aromaticityModelSetting = anAromaticity;
    }

    /**
     * Sets the applied SmilesGenerator.
     * @param aSmilesGenerator the new SmilesGenerator
     * @throws NullPointerException if parameter is null
     */
    public void setSmilesGeneratorSetting(SmilesGenerator aSmilesGenerator) throws NullPointerException {
        Objects.requireNonNull(aSmilesGenerator, "Given SmilesGenerator must not be null");
        this.smilesGeneratorSetting = aSmilesGenerator;
    }

    /**
     * Sets the option to skip rule 7 of the Schuffenhauer scaffold tree fragmentation.
     * It can be useful to turn off rule 7 explicitly,
     * as it is only relevant for a relatively small number of molecules, but it increases the computing time.
     * @param anIsRuleSevenApplied if true rule 7 is applied
     */
    public void setRuleSevenAppliedSetting(boolean anIsRuleSevenApplied) {
        this.ruleSevenAppliedSetting = anIsRuleSevenApplied;
    }

    /**
     * Sets the now used scaffold mode.
     * @param anScaffoldMode the scaffold mode to use
     * @throws NullPointerException if parameter is null
     */
    public void setScaffoldModeSetting(ScaffoldModeOption anScaffoldMode) throws NullPointerException {
        Objects.requireNonNull(anScaffoldMode, "Given scaffold mode is null");
        this.scaffoldModeSetting = anScaffoldMode;

    }

    /**
     * Sets the setting that, only the hybridisation of aromatic atoms is obtained.
     * @param anIsOnlyHybridisationsAtAromaticBondsRetained true, if only the hybridisation of aromatic atoms is obtained.
     */
    public void setRetainOnlyHybridisationsAtAromaticBondsSetting(boolean anIsOnlyHybridisationsAtAromaticBondsRetained) {
        this.retainOnlyHybridisationsAtAromaticBondsSetting = anIsOnlyHybridisationsAtAromaticBondsRetained;
    }
    /**
     * All settings are set to their default values. Automatically executed by the constructor.
     */
    public void restoreDefaultSettings() {
        this.setDetermineAromaticitySetting(ScaffoldGenerator.DETERMINE_AROMATICITY_SETTING_DEFAULT);
        this.setAromaticityModelSetting(ScaffoldGenerator.AROMATICITY_MODEL_SETTING_DEFAULT);
        this.setSmilesGeneratorSetting(ScaffoldGenerator.SMILES_GENERATOR_SETTING_DEFAULT);
        this.setRuleSevenAppliedSetting(ScaffoldGenerator.RULE_SEVEN_APPLIED_SETTING_DEFAULT);
        this.setRetainOnlyHybridisationsAtAromaticBondsSetting(ScaffoldGenerator.RETAIN_ONLY_HYBRIDISATIONS_AT_AROMATIC_BONDS_SETTING_DEFAULT);
        this.setScaffoldModeSetting(ScaffoldGenerator.SCAFFOLD_MODE_OPTION_DEFAULT);
    }

    /**
     * Generates the selected fragment type for the entered molecule and returns it. You can choose from the types available in ScaffoldModeOption.
     * Depending on the internal settings via {@link ScaffoldGenerator#aromaticityModelSetting},
     * a specific aromaticity model is applied to determine the aromaticity of the individual atoms of the fragment.
     * {@link ScaffoldGenerator#determineAromaticitySetting} allows you to determine whether the aromaticity is to be determined.
     * @param aMolecule molecule whose scaffold is produced.
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @return scaffold of the inserted molecule. It can be an empty molecule if the original molecule does not contain a scaffold of the used type.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public IAtomContainer getScaffold(IAtomContainer aMolecule, boolean anAddImplicitHydrogens) throws CDKException, CloneNotSupportedException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpMolecule =  this.getScaffoldInternal(aMolecule,
                anAddImplicitHydrogens,
                this.determineAromaticitySetting,
                this.aromaticityModelSetting,
                this.scaffoldModeSetting);
        return tmpMolecule;
    }

    /**
     * Generates a set of rings depending on the used CycleFinder.
     * The removal of atoms can create open valences. These open valences can be compensated with implicit hydrogens.
     * Can optional add non-single bounded atoms to the rings.
     * @param aMolecule molecule whose rings are produced.
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @param anIsKeepingNonSingleBonds if true, non-single bonded atoms are retained on the ring.
     * @return rings of the inserted molecule.
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     * @throws NullPointerException if parameter is null
     */
    public List<IAtomContainer> getRings(IAtomContainer aMolecule, boolean anAddImplicitHydrogens, boolean anIsKeepingNonSingleBonds) throws CloneNotSupportedException, CDKException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Mark each atom with ascending number*/
        Integer tmpCounter = 0;
        for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
            tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounter);
            tmpCounter++;
        }
        List<IAtomContainer> tmpMoleculeList = this.getRingsInternal(tmpClonedMolecule, anIsKeepingNonSingleBonds);
        /*Add back hydrogens*/
        for(IAtomContainer tmpRing : tmpMoleculeList) {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpRing);
            if(anAddImplicitHydrogens) {
                CDKHydrogenAdder.getInstance(tmpRing.getBuilder()).addImplicitHydrogens(tmpRing);
            }
        }
        return  tmpMoleculeList;
    }

    /**
     * Outputs all fragments that are not contained in the generated scaffold in contrast to the unchanged molecule.
     * Those fragments are called SideChains.
     * The scaffold is therefore subtracted from the original molecule and
     * all remaining fragments are saturated with hydrogens if anAddImplicitHydrogens is true. <p>
     *
     * SideChains cannot be generated for ELEMENTAL_WIRE_FRAME, BASIC_FRAMEWORK and BASIC_WIRE_FRAME themselves.
     * Their SideChains are identical to those of MURCKO_FRAMEWORK. Therefore, they are used.
     * @param aMolecule Molecule whose side chains are to be returned
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @return List of SideChains of the input molecule
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     * @throws NullPointerException if parameter is null
     */
    public List<IAtomContainer> getSideChains(IAtomContainer aMolecule, boolean anAddImplicitHydrogens) throws CloneNotSupportedException, CDKException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpSideChainList = new ArrayList<>(tmpClonedMolecule.getAtomCount());
        /*Mark each atom with ascending number*/
        Integer tmpCounter = 0;
        HashMap<Integer, IAtom> tmpMoleculePropertyMap = new HashMap<>(tmpClonedMolecule.getAtomCount(), 1);
        for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
            tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounter);
            tmpMoleculePropertyMap.put(tmpCounter, tmpAtom);
            tmpCounter++;
        }
        /*Generate scaffold*/
        IAtomContainer tmpScaffold;
        /*SideChains cannot be generated for ELEMENTAL_WIRE_FRAME, BASIC_FRAMEWORK and BASIC_WIRE_FRAME themselves.
        Their SideChains are identical to those of MURCKO_FRAMEWORK. Therefore, they can be used.*/
        if(this.scaffoldModeSetting.equals(ScaffoldModeOption.ELEMENTAL_WIRE_FRAME) || this.scaffoldModeSetting.equals(ScaffoldModeOption.BASIC_FRAMEWORK)
                || this.scaffoldModeSetting.equals(ScaffoldModeOption.BASIC_WIRE_FRAME)) {
            /*Use MURCKO_FRAMEWORK as ScaffoldModeOption for those scaffolds*/
            //Get scaffold
            tmpScaffold = this.getScaffoldInternal(tmpClonedMolecule, anAddImplicitHydrogens,
                    this.determineAromaticitySetting, this.aromaticityModelSetting, ScaffoldModeOption.MURCKO_FRAMEWORK);
        }
        else{
            //Get scaffold
            tmpScaffold = this.getScaffoldInternal(tmpClonedMolecule, anAddImplicitHydrogens,
                    this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
        }
        /*Store the numbers of scaffold atoms in list*/
        List<Integer> tmpRemovedNumberList = new ArrayList<>(tmpClonedMolecule.getAtomCount());
        for(IAtom tmpAtom : tmpScaffold.atoms()) {
            tmpRemovedNumberList.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Remove all numbers of the scaffold from the original molecule*/
        for(Integer tmpNumber : tmpRemovedNumberList) {
            if(tmpMoleculePropertyMap.containsKey(tmpNumber)) {
                if(tmpClonedMolecule.contains(tmpMoleculePropertyMap.get(tmpNumber))) {
                    tmpClonedMolecule.removeAtom(tmpMoleculePropertyMap.get(tmpNumber));
                }
            }
        }
        //Save each unconnected fragment that remains as a separate AtomContainer
        IAtomContainerSet tmpFragments = ConnectivityChecker.partitionIntoMolecules(tmpClonedMolecule);
        /*Add fragments to the SideChain list*/
        for(IAtomContainer tmpFragment : tmpFragments.atomContainers()) {
            if(tmpFragment.getAtomCount() == 1 && tmpFragment.getAtom(0).getSymbol().equals("H")) {
                //Skip single hydrogen
                continue;
            }
            /*Add back hydrogens*/
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpFragment);
            if(anAddImplicitHydrogens) {
                CDKHydrogenAdder.getInstance(tmpFragment.getBuilder()).addImplicitHydrogens(tmpFragment);
            }
            tmpSideChainList.add(tmpFragment);
        }
        return tmpSideChainList;
    }

    /**
     * Returns the linkers of the molecule as a fragment list. Linkers are all fragments that are neither ring nor side chain.
     * Depending on the ScaffoldMode selected, the linkers are different.
     * @param aMolecule Molecule whose linkers are to be returned
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @return linkers of the input molecule
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     * @throws NullPointerException if parameter is null
     */
    public List<IAtomContainer> getLinkers(IAtomContainer aMolecule, boolean anAddImplicitHydrogens) throws CloneNotSupportedException, CDKException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        //Generate Scaffold
        IAtomContainer tmpScaffold = this.getScaffoldInternal(tmpClonedMolecule, anAddImplicitHydrogens, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
        List<IAtomContainer> tmpLinkerList = new ArrayList<>(tmpClonedMolecule.getAtomCount());
        List<IAtomContainer> tmpRingList = this.getRingsInternal(tmpScaffold, true);
        List<Integer> tmpRingAtomNumberList = new ArrayList<>(tmpClonedMolecule.getAtomCount());
        HashMap<Integer, IAtom> tmpScaffoldPropertyMap = new HashMap<>(tmpClonedMolecule.getAtomCount(), 1);
        /*Go through each ring of the scaffold and add there atom number to the list*/
        for(IAtomContainer tmpRing : tmpRingList) {
            for(IAtom tmpAtom : tmpRing.atoms()) {
                tmpRingAtomNumberList.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
        }
        /*Identify each ring atom with the tmpRingAtomNumberList and remove it from the scaffold*/
        for(IAtom tmpScaffoldAtom : tmpScaffold.atoms()) {
            tmpScaffoldPropertyMap.put(tmpScaffoldAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY), tmpScaffoldAtom);
        }
        /*Remove non ring atoms*/
        for(Integer tmpAtomNumber : tmpRingAtomNumberList) {
            if(tmpScaffoldPropertyMap.containsKey(tmpAtomNumber)) {
                if(tmpScaffold.contains(tmpScaffoldPropertyMap.get(tmpAtomNumber))) {
                    tmpScaffold.removeAtom(tmpScaffoldPropertyMap.get(tmpAtomNumber));
                }
            }
        }
        //Save each unconnected fragment that remains as a separate AtomContainer
        IAtomContainerSet tmpFragments = ConnectivityChecker.partitionIntoMolecules(tmpScaffold);
        /*Add fragments to the tmpLinkerList*/
        for(IAtomContainer tmpFragment : tmpFragments.atomContainers()) {
            if(tmpFragment.getAtomCount() == 1 && tmpFragment.getAtom(0).getSymbol().equals("H")) {
                //Skip single hydrogen
                continue;
            }
            /*Add back hydrogens*/
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpFragment);
            if(anAddImplicitHydrogens) {
                CDKHydrogenAdder.getInstance(tmpFragment.getBuilder()).addImplicitHydrogens(tmpFragment);
            }
            tmpLinkerList.add(tmpFragment);
        }
        return tmpLinkerList;
    }

    /**
     * Iteratively removes the terminal rings. All resulting scaffolds are returned. Duplicates are not permitted.
     * The Scaffold of the entire entered molecule is stored first in the list.
     * The scaffolds that follow then become smaller and smaller. <p>
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMolecule Molecule to be disassembled.
     * @return List with all resulting Scaffold.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public List<IAtomContainer> applyEnumerativeRemoval(IAtomContainer aMolecule) throws CDKException, CloneNotSupportedException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpScaffoldOriginal = this.getScaffoldInternal(aMolecule, true, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
        int tmpRingCount = this.getRingsInternal(tmpScaffoldOriginal, true).size();
        List<String> tmpAddedSMILESList = new ArrayList<>(tmpRingCount * 45);
        //List of all fragments already created and size estimated on the basis of an empirical value
        List<IAtomContainer> tmpIterativeRemovalList = new ArrayList<>(tmpRingCount * 45);
        tmpIterativeRemovalList.add(tmpScaffoldOriginal); //Add origin Scaffold
        for(int tmpCounter = 0 ; tmpCounter < tmpIterativeRemovalList.size(); tmpCounter++) {//Go through all the molecules created
            IAtomContainer tmpIterMol = tmpIterativeRemovalList.get(tmpCounter); //Take the next molecule from the list
            List<IAtomContainer> tmpAllRingsList = this.getRingsInternal(tmpIterMol,true);
            int tmpRingSize = tmpAllRingsList.size();
            for(IAtomContainer tmpRing : tmpAllRingsList) { //Go through all rings
                //Skip molecule if it has less than 2 rings or the ring is not removable
                if(tmpRingSize < 2) {
                    continue;
                }
                if(this.isRingTerminal(tmpIterMol, tmpRing) && this.isRingRemovable(tmpRing, tmpAllRingsList, tmpIterMol)) { //Consider all terminal rings
                    boolean tmpIsInList = false;
                    IAtomContainer tmpRingRemoved = this.getScaffoldInternal(this.removeRing(tmpIterMol, true, tmpRing), true, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting); //Remove next ring
                    String tmpRingRemovedSMILES = this.getSmilesGenerator().create(tmpRingRemoved); //Generate SMILES
                    if(tmpAddedSMILESList.contains(tmpRingRemovedSMILES)) { //Check if the molecule has already been added to the list
                        tmpIsInList = true;
                    }
                    if(!tmpIsInList) { //Add the molecule only if it is not already in the list
                        tmpIterativeRemovalList.add(tmpRingRemoved);
                        tmpAddedSMILESList.add(tmpRingRemovedSMILES);
                    }
                }
            }
        }
        return tmpIterativeRemovalList;
    }

    /**
     * Iteratively removes the terminal rings. All resulting Scaffolds are saved in a ScaffoldNetwork.
     * A new level is created when the total number of rings decreases by 1.
     * Level 0 is the one with the fewest (mostly 1) rings and therefore the root. <p>
     *
     * Duplicates are permitted. <br>
     * Duplicates are not given their own node, but a link created to the existing related node.
     * In this way, a child can have several parents. <p>
     *
     * If a molecule does not generate a Scaffold, it is stored as a node with empty SMILES and is treated normally.<p>
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMolecule Molecule to be disassembled.
     * @return ScaffoldNetwork with all resulting Scaffold.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public ScaffoldNetwork generateScaffoldNetwork(IAtomContainer aMolecule) throws CDKException, CloneNotSupportedException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        ScaffoldNetwork tmpScaffoldNetwork = new ScaffoldNetwork(this.getSmilesGenerator());
        IAtomContainer tmpScaffoldOriginal = this.getScaffoldInternal(aMolecule, true, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
        int tmpRingCount = this.getRingsInternal(tmpScaffoldOriginal, true).size();
        //List of all fragments already created and size estimated on the basis of an empirical value
        List<IAtomContainer> tmpIterativeRemovalList = new ArrayList<>(tmpRingCount * 45);
        tmpIterativeRemovalList.add(tmpScaffoldOriginal); //Add origin Scaffold
        /*Add the first node to the tree*/
        NetworkNode<IAtomContainer> tmpFirstNode = new NetworkNode<>(tmpScaffoldOriginal);
        tmpScaffoldNetwork.addNode(tmpFirstNode);
        /*Get the origin and link it to the first node*/
        String tmpFirstNodeSmiles = this.getSmilesGenerator().create(aMolecule);
        tmpFirstNode.addOriginSmiles(tmpFirstNodeSmiles);
        tmpFirstNode.addNonVirtualOriginSmiles(tmpFirstNodeSmiles);
        /*Go through all fragments created by iterative removal*/
        for (int tmpCounter = 0; tmpCounter < tmpIterativeRemovalList.size(); tmpCounter++) {
            IAtomContainer tmpIterMol = tmpIterativeRemovalList.get(tmpCounter); //Take the next molecule from the list
            List<IAtomContainer> tmpAllRingsList = this.getRingsInternal(tmpIterMol, true);
            int tmpRingSize = tmpAllRingsList.size();
            /*Go through all rings of the fragment*/
            for (IAtomContainer tmpRing : tmpAllRingsList) {
                /*Skip molecule if it has less than 2 rings*/
                if (tmpRingSize < 2) {
                    continue;
                }
                /*Consider all removable terminal rings*/
                if (this.isRingTerminal(tmpIterMol, tmpRing) && this.isRingRemovable(tmpRing, tmpAllRingsList, tmpIterMol)) {
                    //Remove next ring
                    IAtomContainer tmpRingRemoved = this.getScaffoldInternal(this.removeRing(tmpIterMol, true, tmpRing), true, this.determineAromaticitySetting, aromaticityModelSetting, this.scaffoldModeSetting);
                    /*The node is not yet in the network and must therefore still be added.*/
                    if(!tmpScaffoldNetwork.containsMolecule(tmpRingRemoved)) {
                        tmpIterativeRemovalList.add(tmpRingRemoved);
                        //Create new node
                        NetworkNode<IAtomContainer> tmpNewNode = new NetworkNode<>(tmpRingRemoved);
                        //Add the new node as parent for the old one
                        NetworkNode tmpNode1 = (NetworkNode) tmpScaffoldNetwork.getNode(tmpIterMol);
                        tmpNode1.addParent(tmpNewNode);
                        //Add Origin
                        tmpNewNode.addOriginSmiles(tmpFirstNodeSmiles);
                        //Add new node to the tree
                        tmpScaffoldNetwork.addNode(tmpNewNode);
                        /*The node is already in the network*/
                    }   else {
                        /*Node with the same molecule already in the tree*/
                        NetworkNode tmpOldNode = (NetworkNode) tmpScaffoldNetwork.getNode(tmpRingRemoved);
                        /*Add parent*/
                        NetworkNode tmpNewNode = (NetworkNode) tmpScaffoldNetwork.getNode(tmpIterMol);
                        tmpNewNode.addParent(tmpOldNode);
                    }
                }
            }
        }
        return tmpScaffoldNetwork;
    }

    /**
     * Generates a network for each molecule in the list and merges the networks together. <p>
     *
     * Iteratively removes the terminal rings of each molecule. All resulting scaffolds of a molecule are saved in a ScaffoldNetwork.
     * A new level is created when the total number of rings decreases by 1.
     * Level 0 is the one with the fewest (mostly 1) rings and therefore the root. <br>
     *
     * All networks are merged together. Duplicates are permitted.
     * Duplicates are not given their own node, but a link created to the existing related node.
     * In this way, a child can have several parents. <p>
     *
     * If one networks do not have a common node with the others, it is still added without a connection. <p>
     *
     * If a molecule does not generate a Scaffold, it is stored as a node with empty SMILES and is treated normally. <p>
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMoleculeList List of Molecules to be disassembled and merged.
     * @return ScaffoldNetwork with all resulting Scaffold.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public ScaffoldNetwork generateScaffoldNetwork(List<IAtomContainer> aMoleculeList) throws CDKException, CloneNotSupportedException, NullPointerException {
        Objects.requireNonNull(aMoleculeList, "Input molecule list must be non null");
        ScaffoldNetwork tmpScaffoldNetwork = new ScaffoldNetwork(this.smilesGeneratorSetting);
        for(IAtomContainer tmpMolecule : aMoleculeList) {
            Objects.requireNonNull(tmpMolecule, "Input molecule must be non null");
            IAtomContainer tmpClonedMolecule = tmpMolecule.clone();
            try {
                tmpScaffoldNetwork.mergeNetwork(this.generateScaffoldNetwork(tmpClonedMolecule));
            } catch (Exception anException) {
                /*Log the skipped molecule*/
                this.tmpLogExceptionCounter++;
                try {
                    ScaffoldGenerator.LOGGER.warn(anException.toString()
                            + "\n generateScaffoldNetwork() Exception. SMILES of the skipped molecule number " + this.tmpLogExceptionCounter + ": "
                            + this.smilesGeneratorSetting.create(tmpClonedMolecule), anException);
                } catch (Exception anExceptionException) {
                    ScaffoldGenerator.LOGGER.warn(anException.toString()
                            + "\nException inside the generateScaffoldNetwork() Exception. Probably a problem with the SMILES generator.",
                            anException);
                }
            }
        }
        return  tmpScaffoldNetwork;
    }

    /**
     * Iteratively removes the rings of the molecule according to specific rules that are queried hierarchically
     * and returns the scaffolds as list. <br>
     * Based on the rules from the  <a href="https://doi.org/10.1021/ci600338x"> "The Scaffold Tree"</a> paper by Schuffenhauer et al.
     * Rule 7 {@link ScaffoldGenerator#applySchuffenhauerRuleSeven(IAtomContainer, List)} is only applied
     * if {@link ScaffoldGenerator#ruleSevenAppliedSetting} is true.
     * The aromaticity is also redetermined by {@link ScaffoldGenerator#determineAromaticitySetting}. <p>
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMolecule Molecule that is to be broken down into its fragments
     * @return Fragments of the molecule according to the Schuffenhauer rules
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public List<IAtomContainer> applySchuffenhauerRules(IAtomContainer aMolecule) throws CloneNotSupportedException, CDKException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpScaffold = this.getScaffoldInternal(tmpClonedMolecule, true, this.determineAromaticitySetting ,this.aromaticityModelSetting, this.scaffoldModeSetting);
        /*All molecules with an atom-to-ring ratio of less than 1.0 are assigned the CYCLE_FINDER_BACKUP_PROPERTY = true property,
         since too many rings were probably detected. The fact that a molecule has more rings than atoms seems concerning. That is why this value was chosen.*/
        int tmpRingNumber = this.getRingsInternal(tmpScaffold, false).size();
        float tmpRingAtomRatio = (float) tmpScaffold.getAtomCount() / tmpRingNumber;
        if(tmpRingAtomRatio < 1.0 ) {
            /*Change the property of all atoms of the molecule*/
            for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
                tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
            }
            /*Apply the new CycleFinder to the molecules*/
            tmpRingNumber = this.getRingsInternal(tmpScaffold, false).size();
            tmpScaffold = this.getScaffoldInternal(tmpClonedMolecule, true, this.determineAromaticitySetting ,this.aromaticityModelSetting, this.scaffoldModeSetting);
        }
        //List of all generated fragments
        List<IAtomContainer> tmpScaffoldFragments = new ArrayList<>(tmpRingNumber);
        tmpScaffoldFragments.add(tmpScaffold);
        /*Go through all the fragments generated and try to break them down further*/
        for(int tmpCounter = 0 ; tmpCounter < tmpScaffoldFragments.size(); tmpCounter++) {
            List<IAtomContainer> tmpRings = this.getRingsInternal(tmpScaffoldFragments.get(tmpCounter), true);
            /*If the fragment has only one ring or no ring, it does not need to be disassembled further*/
            if(tmpRings.size() == 1 || tmpRings.size() == 0) {
                break;
            }
            /*Only the removable terminal rings are further investigated*/
            List<IAtomContainer> tmpRemovableRings = new ArrayList<>(tmpRings.size());
            for (IAtomContainer tmpRing : tmpRings) {
                if (this.isRingTerminal(tmpScaffoldFragments.get(tmpCounter), tmpRing)
                        && this.isRingRemovable(tmpRing, tmpRings, tmpScaffoldFragments.get(tmpCounter))) {
                    tmpRemovableRings.add(tmpRing); //Add the candidate rings
                }
            }
            /*If the fragment has no candidate ring, it does not need to be disassembled further*/
            if(tmpRemovableRings.size() == 0) {
                break;
            }
            /*Apply rule number one*/
            tmpRemovableRings = this.applySchuffenhauerRuleOne(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number two*/
            tmpRemovableRings = this.applySchuffenhauerRuleTwo(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number three*/
            tmpRemovableRings = this.applySchuffenhauerRuleThree(tmpScaffoldFragments.get(tmpScaffoldFragments.size() - 1), tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number four and five*/
            tmpRemovableRings = this.applySchuffenhauerRuleFourAndFive(tmpScaffoldFragments.get(tmpScaffoldFragments.size() - 1), tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number six*/
            tmpRemovableRings = this.applySchuffenhauerRuleSix(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            //Rule seven is only useful when aromaticity is redetermined
            if(this.ruleSevenAppliedSetting && this.determineAromaticitySetting) {
                /*Apply rule number seven*/
                tmpRemovableRings = this.applySchuffenhauerRuleSeven(tmpScaffoldFragments.get(tmpScaffoldFragments.size() - 1), tmpRemovableRings);
                if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                    this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                    //After a new fragment has been added, the next one is investigated
                    continue;
                }
            }
            /*Apply rule number eight*/
            tmpRemovableRings = this.applySchuffenhauerRuleEight(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number nine*/
            tmpRemovableRings = this.applySchuffenhauerRuleNine(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number ten*/
            tmpRemovableRings = this.applySchuffenhauerRuleTen(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number eleven*/
            tmpRemovableRings = this.applySchuffenhauerRuleEleven(tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number twelve*/
            tmpRemovableRings = this.applySchuffenhauerRuleTwelve(tmpScaffoldFragments.get(tmpScaffoldFragments.size() - 1), tmpRemovableRings);
            if (tmpRemovableRings.size() == 1) { //If only one eligible ring remains, it can be removed
                this.removeRingForSchuffenhauerRule(tmpRemovableRings.get(0), tmpScaffoldFragments);
                //After a new fragment has been added, the next one is investigated
                continue;
            }
            /*Apply rule number thirteen, the tiebreaking rule */
            IAtomContainer tmpFragment = this.getScaffoldInternal(this.applySchuffenhauerRuleThirteen(tmpScaffoldFragments.get(tmpScaffoldFragments.size() - 1),
                    tmpRemovableRings), true, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
            tmpScaffoldFragments.add(tmpFragment);
        }
        return tmpScaffoldFragments;
    }


    /**
     * Iteratively removes the rings of the molecule according to specific rules that are queried hierarchically. <p>
     * A tree is built from the resulting fragments.
     * A tree has one single root, the smallest fragment. Each node can have several children but only one parent. <p>
     * Based on the rules from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al.
     * Rule 7 {@link ScaffoldGenerator#applySchuffenhauerRuleSeven(IAtomContainer, List)} is only applied
     * if {@link ScaffoldGenerator#ruleSevenAppliedSetting} is true
     * and the aromaticity is also redetermined by {@link ScaffoldGenerator#determineAromaticitySetting}. <p>
     *
     * If a molecule does not generate a Scaffold,
     * it is stored as node with empty SMILES in a ScaffoldTree and is treated normally. <p>
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMolecule Molecule that is to be broken down into its fragments
     * @return A tree consisting of fragments of the molecule according to the Schuffenhauer rules
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws NullPointerException if parameter is null
     */
    public ScaffoldTree generateSchuffenhauerTree(IAtomContainer aMolecule) throws CloneNotSupportedException, CDKException, NullPointerException {
        Objects.requireNonNull(aMolecule, "Input molecule must be non null");
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpFragmentList = this.applySchuffenhauerRules(tmpClonedMolecule);
        /*Set the root for the ScaffoldTree and add the origin of the root*/
        TreeNode<IAtomContainer> tmpReverseParentNode =  new TreeNode<IAtomContainer>(tmpFragmentList.get(tmpFragmentList.size()-1));
        String tmpSmiles = this.getSmilesGenerator().create(tmpClonedMolecule);
        tmpReverseParentNode.addOriginSmiles(tmpSmiles);
        //Add non-virtual if tmpFragmentList.size loop do not run
        if(tmpFragmentList.size() == 1) {
            tmpReverseParentNode.addNonVirtualOriginSmiles(tmpSmiles);
        }
        ScaffoldTree tmpScaffoldTree = new ScaffoldTree(this.smilesGeneratorSetting);
        tmpScaffoldTree.addNode(tmpReverseParentNode);
        /*Build the ScaffoldTree with the smallest fragment as root and add the origin to each fragment*/
        for(int i = 1; i < tmpFragmentList.size(); i++) {
            TreeNode<IAtomContainer> tmpNewNode = new TreeNode<IAtomContainer>(tmpFragmentList.get((tmpFragmentList.size() - 1) - i));
            IAtomContainer tmpTestMol = (IAtomContainer) tmpNewNode.getMolecule();
            TreeNode tmpNode = (TreeNode) tmpScaffoldTree.getAllNodesOnLevel(i - 1).get(0);
            tmpNode.addChild(tmpTestMol);
            TreeNode tmpChildNode = (TreeNode) tmpScaffoldTree.getAllNodesOnLevel(i - 1).get(0).getChildren().get(0);
            tmpChildNode.addOriginSmiles(tmpSmiles);
            /*The last and thus largest fragment is directly related to the original molecule*/
            if(i == (tmpFragmentList.size() - 1)){
                tmpChildNode.addNonVirtualOriginSmiles(tmpSmiles);
            }
            tmpScaffoldTree.addNode(tmpChildNode);
        }
        return tmpScaffoldTree;
    }

    /**
     * Decomposes the entered molecules into Scaffolds, creates ScaffoldTrees from them and then assembles these trees if possible.
     * If trees have the same root (the smallest fragment), they are joined together so that the same fragments are no longer duplicated.
     * In this way, no fragment created is lost when it is joined together. <p>
     *
     * The trees are generated with {@link ScaffoldGenerator#generateSchuffenhauerTree(IAtomContainer)}. <p>
     *
     * If a molecule does not generate a Scaffold, it is stored as a node with empty SMILES in a new ScaffoldTree and is treated normally. <p>
     * All other empty nodes are then added to this tree accordingly.
     *
     * The removal of atoms can create open valences. These are compensated with implicit hydrogens.
     * @param aMoleculeList Molecules to be transferred into list of trees
     * @return List of ScaffoldTrees consisting of the fragments of the entered molecules.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible
     * @throws NullPointerException if parameter is null
     */
    public List<ScaffoldTree> generateSchuffenhauerForest(List<IAtomContainer> aMoleculeList) throws CDKException, CloneNotSupportedException, NullPointerException {
        Objects.requireNonNull(aMoleculeList, "Input molecule list must be non null");
        /*Prepare the output list*/
        List<ScaffoldTree> tmpOutputForest = new ArrayList<>();
        ScaffoldTree tmpFirstTree = new ScaffoldTree(this.getSmilesGenerator());
        tmpOutputForest.add(tmpFirstTree);
        /*Go through all molecules*/
        for(IAtomContainer tmpMolecule : aMoleculeList) {
            try {
                boolean isMoleculeMerged = false;
                ScaffoldTree tmpOldTree = this.generateSchuffenhauerTree(tmpMolecule);
                /*Go through each newly created tree*/
                for(ScaffoldTree tmpNewTree : tmpOutputForest) {
                    /*When one of the new trees has been joined with one of the old trees, move on to the next molecule*/
                    if(tmpNewTree.mergeTree(tmpOldTree)) {
                        isMoleculeMerged = true;
                        break;
                    }
                }
                /*If the molecule could not be included in a tree add the tree of the molecule*/
                if(!isMoleculeMerged) {
                    tmpOutputForest.add(tmpOldTree);
                }
            } catch (Exception anException) {
                /*Log the skipped molecule*/
                this.tmpLogExceptionCounter++;
                try {
                    ScaffoldGenerator.LOGGER.warn(anException.toString()
                            + "\n generateSchuffenhauerForest() Exception. SMILES of the skipped molecule number "
                            + this.tmpLogExceptionCounter + ": " + this.smilesGeneratorSetting.create(tmpMolecule), anException);
                } catch (Exception anExceptionException) {
                    ScaffoldGenerator.LOGGER.warn(anException.toString()
                            + "\nException inside the generateSchuffenhauerForest() Exception. Probably a problem with the SMILES generator.", anException);
                }
            }
        }
        return tmpOutputForest;
    }

    /**
     * Generates the selected fragment type for the entered molecule and returns it. You can choose from the types available in ScaffoldModeOption.
     * Depending on the internal settings via {@link ScaffoldGenerator#aromaticityModelSetting},
     * a specific aromaticity model is applied to determine the aromaticity of the individual atoms of the fragment.
     * {@link ScaffoldGenerator#determineAromaticitySetting} allows you to determine whether the aromaticity is to be determined.
     * @param aMolecule molecule whose scaffold is produced.
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @param anIsAromaticitySet Indicates whether the aromaticity is to be set.
     * @param anAromaticity anAromaticity Model to be used to determine aromaticity. Can be null if anIsAromaticitySet == false.
     * @param aScaffoldModeOption Indicates which scaffold is to be used.
     * @return scaffold of the inserted molecule. It can be an empty molecule if the original molecule does not contain a Scaffold.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected IAtomContainer getScaffoldInternal(IAtomContainer aMolecule, boolean anAddImplicitHydrogens, boolean anIsAromaticitySet, Aromaticity anAromaticity, ScaffoldModeOption aScaffoldModeOption) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Basic wire frames and element wire frames will be numbered later, as their number will be deleted immediately by anonymization and skeleton*/
        if(!ScaffoldModeOption.BASIC_WIRE_FRAME.equals(aScaffoldModeOption) && !ScaffoldModeOption.ELEMENTAL_WIRE_FRAME.equals(aScaffoldModeOption)) {
            /*Mark each atom with ascending number*/
            Integer tmpCounter = 0;
            for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
                tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounter);
                tmpCounter++;
            }
        }
        /*Generate the murckoFragment*/
        IAtomContainer tmpMurckoFragment = this.getMurckoFragment(tmpClonedMolecule);
        switch (aScaffoldModeOption) {
            /*Generate the Murcko scaffold*/
            case MURCKO_FRAMEWORK:
                break;
            /*Generate the basic wire frame*/
            case BASIC_WIRE_FRAME:
                tmpMurckoFragment = AtomContainerManipulator.anonymise(tmpMurckoFragment);
                /*Mark each atom with ascending number after anonymization because all properties are removed*/
                Integer tmpCounterBWF = 0;
                for(IAtom tmpAtom : tmpMurckoFragment.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounterBWF);
                    tmpCounterBWF++;
                }
                break;
            /*Generate the common scaffold retaining atoms multi-bonded to rings and linkers directly*/
            case SCAFFOLD:
                /*Store the number of each Atom of the murckoFragment*/
                HashSet<Integer> tmpMurckoAtomNumbers = new HashSet<>(tmpClonedMolecule.getAtomCount(), 1);
                for (IAtom tmpMurckoAtom : tmpMurckoFragment.atoms()) {
                    tmpMurckoAtomNumbers.add(tmpMurckoAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
                }
                /*Store the number of each Atom that is not single bonded and the respective bond*/
                HashSet<IBond> tmpAddBondSet = new HashSet<>((tmpClonedMolecule.getAtomCount() / 2), 1);
                for (IBond tmpBond : tmpClonedMolecule.bonds()) {
                    if (!tmpBond.getOrder().equals(IBond.Order.SINGLE) && !tmpBond.getOrder().equals(IBond.Order.UNSET)) {//Consider non-single bonds
                        //If both atoms of the bond are in the Murcko fragment, they are taken over anyway
                        Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        if (tmpMurckoAtomNumbers.contains(tmpBondProperty0)
                                && tmpMurckoAtomNumbers.contains(tmpBondProperty1)) {
                            continue;
                        }
                        //The binding has not yet been added to the list
                        /*Add the bond*/
                        tmpAddBondSet.add(tmpBond);
                    }
                }
                /*Add the missing atom and the respective bond*/
                HashMap<Integer, IAtom> tmpMurckoAtomMap = new HashMap<>(tmpMurckoFragment.getAtomCount(), 1);
                for (IAtom tmpAtom : tmpMurckoFragment.atoms()) {
                    /*Save the properties of the murcko fragment*/
                    int tmpAtomProperty = tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    tmpMurckoAtomMap.put(tmpAtomProperty, tmpAtom);
                }
                for (IBond tmpBond : tmpAddBondSet) { //Go thought all saved bonds
                    /*If both atoms of the bond are contained in the murcko fragment, this bond does not need to be added anymore*/
                    Integer tmpAtomProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    Integer tmpAtomProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if (tmpMurckoAtomMap.containsKey(tmpAtomProperty0) &&
                            tmpMurckoAtomMap.containsKey(tmpAtomProperty1)) {
                        continue; //Skip this bond
                    }
                    /*Atom 1 of the bond is in the Murcko fragment*/
                    if (tmpMurckoAtomMap.containsKey(tmpAtomProperty1)) {
                        IAtom tmpClonedAtom = tmpBond.getAtom(0).clone();
                        tmpMurckoFragment.addAtom(tmpClonedAtom); //Add the atom that is not yet in the murcko fragment
                        IBond tmpNewBond = tmpBond.clone();
                        //Set the first atom
                        tmpNewBond.setAtom(tmpMurckoAtomMap.get(tmpAtomProperty1), 1);
                        tmpNewBond.setAtom(tmpClonedAtom, 0); //Set the second atom
                        tmpMurckoFragment.addBond(tmpNewBond); //Add the whole bond
                        continue; //Next bond
                    }
                    /*Atom 0 of the bond is in the Murcko fragment*/
                    if (tmpMurckoAtomMap.containsKey(tmpAtomProperty0)) {
                        IAtom tmpClonedAtom = tmpBond.getAtom(1).clone();
                        tmpMurckoFragment.addAtom(tmpClonedAtom); //Add the atom that is not yet in the murcko fragment
                        IBond tmpNewBond = tmpBond.clone();
                        //Set the first atom
                        tmpNewBond.setAtom(tmpMurckoAtomMap.get(tmpAtomProperty0), 0);
                        tmpNewBond.setAtom(tmpClonedAtom, 1); //Set the second atom
                        tmpMurckoFragment.addBond(tmpNewBond); //Add the whole bond
                    }
                }
                break;
            /*Generate the element wire frame*/
            case ELEMENTAL_WIRE_FRAME:
                tmpMurckoFragment = AtomContainerManipulator.skeleton(tmpMurckoFragment);
                /*Mark each atom with ascending number after anonymization because all properties are removed*/
                Integer tmpCounterEWF = 0;
                for(IAtom tmpAtom : tmpMurckoFragment.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY, tmpCounterEWF);
                    tmpCounterEWF++;
                }
                break;
            /*Generate the basic framework*/
            case BASIC_FRAMEWORK:
                for(IAtom tmpAtom : tmpMurckoFragment.atoms()) {
                    if(!tmpAtom.getSymbol().equals("C")) {
                        tmpAtom.setSymbol("C");
                    }
                }
                break;
        }
        /*The Murcko fragmenter class does not adjust the hybridisation when the atoms are removed.
        Therefore, this is deleted and determined again.*/
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMurckoFragment);
        /*Add back hydrogens removed by the MurckoFragmenter class*/
        if(anAddImplicitHydrogens) {
            CDKHydrogenAdder.getInstance(tmpMurckoFragment.getBuilder()).addImplicitHydrogens(tmpMurckoFragment);
        }
        /*Set aromaticity if necessary*/
        if (anIsAromaticitySet) {
            Objects.requireNonNull(anAromaticity, "If anIsAromaticitySet == true, anAromaticity must be non null");
            //Set aromaticity
            anAromaticity.apply(tmpMurckoFragment);
        }
        return tmpMurckoFragment;
    }

    /**
     * Generates a set of rings depending on the CycleFinder selected by {@link ScaffoldGenerator#getCycleFinder(IAtomContainer)}.
     * Can optional add non-single bounded atoms to the rings and returns them.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for aMolecule.
     * @param aMolecule molecule whose rings are produced.
     * @param anIsKeepingNonSingleBonds if true, non-single bonded atoms are retained on the ring.
     * @return rings of the inserted molecule.
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present or problem with aromaticity.apply()
     */
    protected List<IAtomContainer> getRingsInternal(IAtomContainer aMolecule, boolean anIsKeepingNonSingleBonds) throws CloneNotSupportedException, CDKException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Generate cycles*/
        Cycles tmpNewCycles = this.getCycleFinder(tmpClonedMolecule).find(tmpClonedMolecule);
        IRingSet tmpRingSet = tmpNewCycles.toRingSet();
        List<IAtomContainer> tmpCycles = new ArrayList<>(tmpNewCycles.numberOfCycles());
        int tmpCycleNumber = tmpNewCycles.numberOfCycles();
        //HashMap cannot be larger than the total number of atoms. Key = C and Val = Bond
        HashSet<IBond> tmpAddBondSet = new HashSet<>((tmpClonedMolecule.getAtomCount() / 2), 1);
        /*Store non single bonded atoms*/
        if(anIsKeepingNonSingleBonds) { //Only needed if non-single bonded atoms are retained
            /*Generate the murckoFragment*/
            IAtomContainer tmpMurckoFragment = this.getMurckoFragment(tmpClonedMolecule);
            /*Store the number of each Atom of the murckoFragment*/
            HashSet<Integer> tmpMurckoAtomNumbers = new HashSet<>(tmpClonedMolecule.getAtomCount(), 1);
            for (IAtom tmpMurckoAtom : tmpMurckoFragment.atoms()) {
                tmpMurckoAtomNumbers.add(tmpMurckoAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
            /*Store the number of each Atom that is not single bonded and the respective bond*/
            for (IBond tmpBond : tmpClonedMolecule.bonds()) {
                if (!tmpBond.getOrder().equals(IBond.Order.SINGLE)) {//Consider non-single bonds
                    //If both atoms of the bond are in the Murcko fragment, they are taken over anyway
                    Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if (tmpMurckoAtomNumbers.contains(tmpBondProperty0)
                            && tmpMurckoAtomNumbers.contains(tmpBondProperty1)) {
                        continue;
                    }
                    /*The binding has not yet been added to the list*/
                    //Add the bond
                    tmpAddBondSet.add(tmpBond);
                }
            }
        }
        /*Add Cycles*/
        for(int tmpCount = 0; tmpCount < tmpCycleNumber; tmpCount++) { //Go through all generated rings
            IAtomContainer tmpCycle = tmpRingSet.getAtomContainer(tmpCount); //Store rings as AtomContainer
            if(anIsKeepingNonSingleBonds) {
                /*Add the missing atom and the respective bond*/
                HashMap<Integer, IAtom> tmpMurckoAtomMap = new HashMap<>(tmpCycle.getAtomCount(), 1);
                for(IAtom tmpAtom : tmpCycle.atoms()) {
                    /*Save the properties of the murcko fragment*/
                    int tmpAtomPropertyNumber = tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    tmpMurckoAtomMap.put(tmpAtomPropertyNumber, tmpAtom);
                }
                for(IBond tmpBond : tmpAddBondSet) { //Go thought all saved bonds
                    /*If both atoms of the bond are contained in the murcko fragment, this bond does not need to be added anymore*/
                    Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if(tmpMurckoAtomMap.containsKey(tmpBondProperty0) &&
                            tmpMurckoAtomMap.containsKey(tmpBondProperty1)) {
                        continue; //Skip this bond
                    }
                    /*Atom 1 of the bond is in the Murcko fragment*/
                    if(tmpMurckoAtomMap.containsKey(tmpBondProperty1)) {
                        IAtom tmpClonedAtom = tmpBond.getAtom(0).clone();
                        tmpCycle.addAtom(tmpClonedAtom); //Add the atom that is not yet in the murcko fragment
                        IBond tmpNewBond = tmpBond.clone();
                        //Set the first atom
                        tmpNewBond.setAtom(tmpMurckoAtomMap.get(tmpBondProperty1), 1);
                        tmpNewBond.setAtom(tmpClonedAtom, 0); //Set the second atom
                        tmpCycle.addBond(tmpNewBond); //Add the whole bond
                        continue; //Next bond
                    }
                    /*Atom 0 of the bond is in the Murcko fragment*/
                    if(tmpMurckoAtomMap.containsKey(tmpBondProperty0)) {
                        IAtom tmpClonedAtom = tmpBond.getAtom(1).clone();
                        tmpCycle.addAtom(tmpClonedAtom); //Add the atom that is not yet in the murcko fragment
                        IBond tmpNewBond = tmpBond.clone();
                        //Set the first atom
                        tmpNewBond.setAtom(tmpMurckoAtomMap.get(tmpBondProperty0), 0);
                        tmpNewBond.setAtom(tmpClonedAtom, 1); //Set the second atom
                        tmpCycle.addBond(tmpNewBond); //Add the whole bond
                    }
                }
            }
            tmpCycles.add(tmpCycle); //Add rings to list
        }
        return tmpCycles;
    }

    /**
     * Returns the Murcko fragment of each molecule entered.
     * In addition, the stereo elements are transferred from the original molecule to the Murcko fragment if possible,
     * as these are lost when the Murcko fragment is generated.
     * @param aMolecule Molecule whose Murcko fragment is to be created
     * @return Murcko fragment of the input molecule
     */
    protected IAtomContainer getMurckoFragment(IAtomContainer aMolecule) {
        IAtomContainer tmpMurckoFragment = MurckoFragmenter.scaffold(aMolecule);
        /*Go through all StereoElements of the original molecule*/
        for (IStereoElement tmpElement : aMolecule.stereoElements()) {
            /*Collect all Carriers and the Focus of the StereoElement of the original molecule*/
            List<IChemObject> tmpList = new ArrayList<>(tmpElement.getCarriers().size() + 1);
            tmpList.addAll(tmpElement.getCarriers());
            tmpList.add(tmpElement.getFocus());
            boolean tmpAllElementsStillPresent = true;
            /*Go through all objects of the original molecule and check if they are still included in the murcko fragment*/
            for (IChemObject tmpObj : tmpList) {
                /*Object is an atom*/
                if (tmpObj instanceof IAtom) {
                    /*Change the boolean if an object is missing*/
                    if (!tmpMurckoFragment.contains((IAtom) tmpObj)) {
                        tmpAllElementsStillPresent = false;
                        break;
                    }
                /*Object is a bond*/
                } else if (tmpObj instanceof IBond) {
                    /*Change the boolean if an object is missing*/
                    if (!tmpMurckoFragment.contains((IBond) tmpObj)) {
                        tmpAllElementsStillPresent = false;
                        break;
                    }
                /*Object is something else (UFO)*/
                } else {
                    /*Change the boolean if it is an unknown object*/
                    tmpAllElementsStillPresent = false;
                    break;
                }
            }
            /*Add the stereo element only if all objects are still there*/
            if (tmpAllElementsStillPresent) {
                tmpMurckoFragment.addStereoElement(tmpElement);
            }
        }
        return tmpMurckoFragment;
    }

    /**
     * Removes the given ring from the total molecule and returns it.
     * Preserves the sp2 hybridisation of a border atom when an aromatic ring is removed.
     * Preserves the hybridisation of all molecules if {@link ScaffoldGenerator#retainOnlyHybridisationsAtAromaticBondsSetting} == true
     * With the removal of a heteroatom of heterocycles of size 3 a double bond is inserted if it is directly adjacent to another ring.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for aMolecule/aRing and match.
     * @param aMolecule Molecule whose ring is to be removed.
     * @param anAddImplicitHydrogens Specifies whether implicit hydrogens are to be added at the end.
     * The removal of atoms can create open valences. These are not compensated with hydrogens at the end if this parameter is false.
     * @param aRing Ring to be removed.
     * @return Molecule whose ring has been removed.
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected IAtomContainer removeRing(IAtomContainer aMolecule, boolean anAddImplicitHydrogens, IAtomContainer aRing) throws CloneNotSupportedException, CDKException {
        /*Clone original molecules*/
        IAtomContainer tmpMoleculeClone = aMolecule.clone();
        IAtomContainer tmpRingClone = aRing.clone();
        boolean tmpIsRingAromatic = true;
        HashSet<Integer> tmpIsNotRing = new HashSet<>(aMolecule.getAtomCount(), 1);
        HashSet<Integer> tmpDoNotRemove = new HashSet<>(aMolecule.getAtomCount(), 1);
        int tmpBoundNumber = 0;
        /*Preparation for insertion of double bonds with removal of aromatic rings*/
        HashSet<Integer> tmpEdgeAtomNumbers = new HashSet<>(tmpMoleculeClone.getAtomCount(), 1);
        /*Store the number of each atom in the molecule*/
        for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
            tmpIsNotRing.add(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Remove all numbers of the ring that is to be removed*/
        for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
            Integer tmpRemoveProperty = tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            tmpIsNotRing.remove(tmpRemoveProperty);
        }
        /*Get the number of bonds of the ring to other atoms*/
        for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
            for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                //All atoms of the ring in the original molecule
                if(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY).equals(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                    for(IBond tmpBond : tmpMolAtom.bonds()){
                        //Bond between ring and non ring atom
                        Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        if(tmpIsNotRing.contains(tmpBondProperty0) ||
                                tmpIsNotRing.contains(tmpBondProperty1)) {
                            tmpBoundNumber++;
                        }
                    }
                }
            }
        }
        /*Add all atoms of rings that are not to be removed to tmpDoNotRemove*/
        //Get all cycles of the molecule
        Cycles tmpCycles = this.getCycleFinder(tmpMoleculeClone).find(tmpMoleculeClone);
        HashSet<Integer> tmpRingPropertySet = new HashSet<>(tmpRingClone.getAtomCount(), 1);
        //Save the properties of the ring
        for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
            tmpRingPropertySet.add(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        for(IAtomContainer tmpCycle : tmpCycles.toRingSet().atomContainers()) {
            boolean tmpIsRingToRemove = true;
            /*Check if it is the ring to be removed*/
            for(IAtom tmpCycleAtom : tmpCycle.atoms()) {
                //If one of the atoms of the ring to be removed is not included, it is not this ring
                Integer tmpCycleAtomProperty = tmpCycleAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                if(!tmpRingPropertySet.contains(tmpCycleAtomProperty)) {
                    tmpIsRingToRemove = false;
                }
            }
            /*If it is not the ring you want to remove, add its atoms to the tmpDoNotRemove list*/
            if(!tmpIsRingToRemove) {
                for(IAtom tmpCycleAtom : tmpCycle.atoms()) {
                    Integer tmpPropertyNumber = tmpCycleAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    tmpDoNotRemove.add(tmpPropertyNumber);
                }
            }
        }
        if(tmpBoundNumber < 2) { //Remove all ring atoms, as there are less than two bonds to other atoms
            for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
                for (IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                    //All atoms of the ring in the original molecule
                    if (tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY).equals(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                        tmpMoleculeClone.removeAtom(tmpMolAtom); //Remove atoms. tmpMoleculeCone.remove() not possible
                        /*Saturate the molecule with hydrogens after removal. Important for Scheme 16*/
                        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMoleculeClone);
                        if(anAddImplicitHydrogens) {
                            CDKHydrogenAdder.getInstance(tmpMoleculeClone.getBuilder()).addImplicitHydrogens(tmpMoleculeClone);
                        }
                    }
                }
            }
        } else { //Remove only the ring atoms that are not bound to the rest of the molecule
            /* Rings consisting of 3 atoms are specially treated*/
            if(tmpRingClone.getAtomCount() == 3) {
                int tmpNonCCounter = 0;
                IAtom tmpNonCAtom = null;
                /*Count the DoNotRemove heteroatoms*/
                for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
                    if(!tmpRingAtom.getSymbol().equals("C") && !tmpDoNotRemove.contains(tmpRingAtom.getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                        tmpNonCCounter++;
                        tmpNonCAtom = tmpRingAtom;
                    }
                }
                /*If the ring contains one heteroatom, it is treated specially.*/
                if (tmpNonCCounter == 1) {
                    tmpRingClone.removeAtom(tmpNonCAtom); //remove the heteroatom from the ring
                    IAtom tmpBondAtom1 = null;
                    IAtom tmpRemoveAtom = null;
                    for(IAtom tmpMolAtom : tmpMoleculeClone.atoms()) { //go through the whole molecule
                        Integer tmpMolAtomProperty = tmpMolAtom.getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        Integer tmpRingCloneProperty0 = tmpRingClone.getAtom(0).getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        Integer tmpRingCloneProperty1 = tmpRingClone.getAtom(1).getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        /* Find the second atom to which the heteroatom was bonded if it was sp3 hybridised*/
                        if(tmpMolAtomProperty.equals(tmpRingCloneProperty0) || tmpMolAtomProperty.equals(tmpRingCloneProperty1)) {
                            if (tmpBondAtom1 != null) {
                                //insert a double bond between the two atoms
                                tmpMoleculeClone.getBond(tmpBondAtom1 , tmpMolAtom).setOrder(IBond.Order.DOUBLE);
                            } else {
                                /*Find the first atom to which the heteroatom was bonded if it was sp3 hybridised*/
                                //Save this atom
                                tmpBondAtom1 = tmpMolAtom;
                            }
                        }
                        /*The heteroatom is to be removed*/
                        if(tmpNonCAtom.getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY).equals(tmpMolAtom.getProperty(SCAFFOLD_ATOM_COUNTER_PROPERTY))) {
                            tmpRemoveAtom = tmpMolAtom;
                        }
                    }
                    //remove the heteroatom
                    tmpMoleculeClone.removeAtom(tmpRemoveAtom);
                }
            }
            /*To test whether the ring is aromatic, exocyclic atoms should not be included*/
            IAtomContainer tmpExocyclicRemovedRing = this.getRingsInternal(aRing.clone(), false).get(0);
            tmpIsRingAromatic = this.isAtomContainerAromatic(tmpExocyclicRemovedRing);
            for(IAtom tmpRingAtom : tmpRingClone.atoms()) {
                for (IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                    /*All atoms of the ring in the original molecule that are not bound to the rest of the molecule*/
                    Integer tmpMolAtomProperty = tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if ((tmpMolAtomProperty.equals(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY)))
                            && !tmpDoNotRemove.contains(tmpMolAtomProperty)) {
                        tmpMoleculeClone.removeAtom(tmpMolAtom); //Remove atoms
                        /*Saturate the molecule with hydrogens after removal*/
                        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMoleculeClone);
                        if (anAddImplicitHydrogens) {
                            CDKHydrogenAdder.getInstance(tmpMoleculeClone.getBuilder()).addImplicitHydrogens(tmpMoleculeClone);
                        }
                    }
                }
            }
            /*Store the number of all atoms from which an aromatic ring has been removed.
             * In these atoms, a double bond was removed without changing the hybridisation from sp2 to sp3.*/
            //Perform calculation only if the ring to be removed is aromatic or if non-aromatic atom hybridisation should also be preserved
            if(tmpIsRingAromatic || !this.retainOnlyHybridisationsAtAromaticBondsSetting) {
                for (IAtom tmpMolAtom : tmpMoleculeClone.atoms()) {
                    //All Atoms that are sp2 hybridised and in the ring to be removed
                    Integer tmpMolAtomProperty = tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if (tmpMolAtom.getHybridization() == IAtomType.Hybridization.SP2
                            && tmpRingPropertySet.contains(tmpMolAtomProperty)) {
                        boolean tmpIsSp3 = true;
                        for (IBond tmpBond : tmpMolAtom.bonds()) { //All bonds of the Atom
                            if (!tmpBond.getOrder().equals(IBond.Order.SINGLE)) { //If it contains a non-single bond it cannot be sp3
                                tmpIsSp3 = false;
                            }
                        }
                        if (tmpIsSp3) { //If the Atom contains only single bonds, it must be a wanted atom
                            tmpEdgeAtomNumbers.add(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
                        }
                    }
                }
            }
            for(IBond tmpBond : tmpMoleculeClone.bonds()) {
                /*If both atoms of a bond were previously part of an aromatic ring, insert a double bond*/
                Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                if(tmpEdgeAtomNumbers.contains(tmpBondProperty0)
                        && tmpEdgeAtomNumbers.contains(tmpBondProperty1)) {
                    tmpBond.setOrder(IBond.Order.DOUBLE);
                    //Remove the atoms that have already been treated from the set
                    tmpEdgeAtomNumbers.remove(tmpBondProperty0);
                    tmpEdgeAtomNumbers.remove(tmpBondProperty1);
                }
            }
            /*Increase the number of hydrogens by 1 for all previously untreated edge C atoms to compensate for the removed atom.*/
            for(IAtom tmpAtom : tmpMoleculeClone.atoms()) {
                Integer tmpAtomProperty = tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                if(tmpEdgeAtomNumbers.contains(tmpAtomProperty) && tmpAtom.getSymbol().equals("C")) {
                    tmpAtom.setImplicitHydrogenCount(tmpAtom.getImplicitHydrogenCount() + 1);
                }
            }
        }
        /*Clear hybridisation. The hybridisation must be reset later by percieveAtomTypesAndConfigureAtoms, as the hybridisation is not changed on its own when the atoms are removed.
        sp2 atoms whose double bonds have been removed must be declared as sp3.*/
        for(IAtom tmpAtom : tmpMoleculeClone.atoms()) {
            tmpAtom.setHybridization((IAtomType.Hybridization) CDKConstants.UNSET);
        }
        /*Add back hydrogens removed by the MurckoFragmenter class*/
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(tmpMoleculeClone);
        if(anAddImplicitHydrogens) {
            CDKHydrogenAdder.getInstance(tmpMoleculeClone.getBuilder()).addImplicitHydrogens(tmpMoleculeClone);
        }
        return tmpMoleculeClone;
    }

    /**
     * Checks whether the tmpRing in the tmpMolecule is terminal. This means whether it can be removed without creating several unconnected parts.
     * Rings that lead to spiro ring systems when removed are also considered non-terminal.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for aMolecule/aRing and match.
     * @param aMolecule Molecule whose ring is to be checked
     * @param aRing Ring to check
     * @return true if the tmpRing is terminal
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected boolean isRingTerminal(IAtomContainer aMolecule, IAtomContainer aRing) throws CloneNotSupportedException {
        /*Clone molecule and ring*/
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpClonedRing = aRing.clone();
        /*Remove ring atoms from original molecule*/
        HashMap<Integer, IAtom> tmpMoleculeCounterMap = new HashMap<>((aMolecule.getAtomCount()), 1);
        for(IAtom tmpMolAtom : tmpClonedMolecule.atoms()) { //Save all atoms of the molecule
            tmpMoleculeCounterMap.put(tmpMolAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY), tmpMolAtom);
        }
        for(IAtom tmpRingAtom : tmpClonedRing.atoms()) { // Go through the ring
            Integer tmpRingAtomProperty = tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(tmpMoleculeCounterMap.containsKey(tmpRingAtomProperty)) { //Is ring atom in molecule
                tmpClonedMolecule.removeAtom(tmpMoleculeCounterMap.get(tmpRingAtomProperty)); //Remove them
            }
        }
        /*Check if there is more than one molecule in the IAtomContainer*/
        boolean tmpRingIsTerminal = ConnectivityChecker.isConnected(tmpClonedMolecule);
        return tmpRingIsTerminal;
    }

    /**
     * Checks whether rings may be removed.
     * If the ring does not contain atoms that are not present in any other rings, it is not removable.
     * Furthermore, removal is impossible when it is an aromatic ring, that borders two consecutive rings.
     * Important: Property (ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY) must be set for aMolecule/aRings/aRing and match.
     * @param aRing Ring being tested for its removability
     * @param aRings All rings of the molecule
     * @param aMolecule Whole molecule
     * @return Whether the ring is removable
     * @throws CloneNotSupportedException if cloning is not possible.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected boolean isRingRemovable(IAtomContainer aRing, List<IAtomContainer> aRings, IAtomContainer aMolecule) throws CloneNotSupportedException, CDKException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpClonedRing = aRing.clone();
        /*---Recognition of rings in which no atom belongs to another ring---*/
        List<IAtomContainer> tmpClonedRings = new ArrayList<>(aRings.size());
        HashSet<Integer> tmpRingsNumbers = new HashSet<>(aRings.size() * tmpClonedRing.getAtomCount(), 1);
        boolean isAnIndependentRing = false;
        /*Store all ring atoms of the whole molecule without the tested ring*/
        for(IAtomContainer tmpRing : aRings) {
            if(tmpRing.equals(aRing)) { //Skip the tested ring
                continue;
            }
            tmpClonedRings.add(tmpRing.clone()); //Store the rings
            for(IAtom tmpAtom : tmpRing.atoms()) { //Store the atoms of the rings
                tmpRingsNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
        }
        /*Investigate whether the ring contains atoms that do not occur in any other ring*/
        for (IAtom tmpSingleRingAtom : aRing.atoms()) {
            Integer tmpRingAtomProperty = tmpSingleRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(!tmpRingsNumbers.contains(tmpRingAtomProperty)){
                isAnIndependentRing = true;
            }
        }
        /*If the ring does not contain atoms that are not present in any other rings, it is not removable*/
        if(!isAnIndependentRing) {
            return false;
        }
        /*---If it is an aromatic ring that borders two consecutive rings, its removal is not possible.---*/
        /*Is it an aromatic ring at all*/
        //Remove exocyclic atoms
        IAtomContainer tmpRemovedRing = this.getRingsInternal(tmpClonedRing, false).get(0);
        /*Do not check aromaticity if both options are false.
        Then pyrene and similar molecules can also be fragmented because no further DB are inserted.*/
        if(this.determineAromaticitySetting || !this.areOnlyHybridisationsAtAromaticBondsRetained()) {
            this.aromaticityModelSetting.apply(tmpRemovedRing);
        }
        if (!this.isAtomContainerAromatic(tmpRemovedRing)) {
            return true;
        }
        /*Store the number of all ring atoms*/
        HashSet<Integer> tmpMoleculeNumbers = new HashSet<>(tmpClonedMolecule.getAtomCount(), 1);
        for(IAtomContainer tmpRing : tmpClonedRings) {
            for(IAtom tmpRingAtom : tmpRing.atoms()) {
                int tmpAtomNumber = tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                tmpMoleculeNumbers.add(tmpAtomNumber);
            }
        }
        /*Store all the atoms of the other rings bordering the aromatic ring*/
        HashSet<Integer> tmpEdgeAtomNumbers = new HashSet<>(tmpClonedMolecule.getAtomCount(), 1);
        for(IAtom tmpRingAtom : tmpClonedRing.atoms()) {
            //Skip the atom if it is already in the HashSet
            Integer tmpRingAtomProperty = tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(tmpMoleculeNumbers.contains(tmpRingAtomProperty)) {
                tmpEdgeAtomNumbers.add(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
        }
        /*At least 3 edge atoms are needed to cause a problem*/
        if(tmpEdgeAtomNumbers.size() < 3) {
            return true;
        }
        /*If one of the edge atoms occurs in more than one other ring, it is not possible to remove the ring*/
        for(Integer tmpEdgeAtomNumber : tmpEdgeAtomNumbers) {
            int tmpRingCounter = 0;
            for(IAtomContainer tmpRing : aRings) {
                for(IAtom tmpRingAtom : tmpRing.atoms()) {
                    //If one of the atoms of the ring to be tested matches one of the edge atoms
                    if(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY).equals(tmpEdgeAtomNumber)) {
                        tmpRingCounter++;
                        if(tmpRingCounter > 1) { //More than one bordering ring
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Removes the selected ring from the last fragment in the list and adds the resulting fragment to this list.
     * Specially designed for {@link ScaffoldGenerator#applySchuffenhauerRules(IAtomContainer)}
     * @param aRing Ring to be removed
     * @param aFragmentList List of all fragments created so far
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected void removeRingForSchuffenhauerRule(IAtomContainer aRing, List<IAtomContainer> aFragmentList) throws CDKException, CloneNotSupportedException {
        //Remove the ring from the fragment currently being treated
        IAtomContainer tmpRingRemoved = this.removeRing(aFragmentList.get(aFragmentList.size() - 1), true, aRing);
        //Remove the linkers
        IAtomContainer tmpScaffoldRingRemoved = this.getScaffoldInternal(tmpRingRemoved, true, this.determineAromaticitySetting, this.aromaticityModelSetting, this.scaffoldModeSetting);
        //Add the fragment to the list of fragments
        aFragmentList.add(tmpScaffoldRingRemoved);
    }
    /**
     * Selects the correct CycleFinder based on ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY.
     * @param aMolecule Molecule for which a CycleFinder is to be generated
     * @return CycleFinder that matches the properties of the molecule
     */
    protected CycleFinder getCycleFinder(IAtomContainer aMolecule) {
        boolean tmpIsBackupFinderUsed = false;
        /*Check whether the backup Cycle finder should be used*/
        for(IAtom tmpAtom : aMolecule.atoms()) {
            /*If no CycleFinder has been assigned to the Atom yet*/
            if(tmpAtom.getProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY) == null) {
                //The ScaffoldGenerator.CYCLE_FINDER is used by default
                tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, false);
                continue;
            }
            /*If one of the atoms has been assigned to the ScaffoldGenerator.CYCLE_FINDER_BACKUP cycle finder, it is used.*/
            if(tmpAtom.getProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY).equals(true)) {
                tmpIsBackupFinderUsed = true;
                break;
            }
        }
        /*Return the right CycleFinder*/
        if(tmpIsBackupFinderUsed) {
            return ScaffoldGenerator.CYCLE_FINDER_BACKUP;
        } else {
            return ScaffoldGenerator.CYCLE_FINDER;
        }
    }

    /**
     * Checks whether the ring of a molecule is in an aromatic fused ring system.
     * These systems cannot easily be further disassembled.
     * @param aRing Ring tested to see if it is in an aromatic fused ring system
     * @param aRings All Rings of the molecule
     * @param aMolecule Whole molecule
     * @return Whether the ring is part of an aromatic fused ring system
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected boolean hasFusedAromaticRings(IAtomContainer aRing, List<IAtomContainer> aRings, IAtomContainer aMolecule) throws CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        IAtomContainer tmpClonedRing = aRing.clone();
        List<IAtomContainer> tmpClonedRings = new ArrayList<>(aRings.size());
        List<Integer> tmpRingNumbers = new ArrayList<>(aMolecule.getAtomCount());
        List<Integer> tmpRingsNumbers = new ArrayList<>(aMolecule.getAtomCount());
        /*If the examined ring itself is not aromatic, it is not such a case*/
        for(IAtom tmpAtom : tmpClonedRing.atoms()){
            if(!tmpAtom.isAromatic()) {
                return false;
            }
            //Save the property numbers of the ring
            tmpRingNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Store all ring atoms of the whole molecule without the tested ring*/
        for(IAtomContainer tmpRing : aRings) {
            if(tmpRing.equals(aRing)) { //Skip the tested ring
                continue;
            }
            if(this.isAtomContainerAromatic(tmpRing)) { //Skip non aromatic rings
                continue;
            }
            tmpClonedRings.add(tmpRing.clone()); //Store the aromatic rings
            for(IAtom tmpAtom : tmpRing.atoms()) { //Store the atoms of the aromatic rings
                tmpRingsNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
        }
        /*Store all the atoms of the other rings bordering the aromatic ring*/
        HashSet<Integer> tmpEdgeAtomNumbers = new HashSet<>(tmpClonedMolecule.getAtomCount(), 1);
        for(IAtom tmpRingAtom : tmpClonedRing.atoms()) {
            //Skip the atom if it is already in the HashSet
            Integer tmpRingAtomProperty = tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(tmpRingsNumbers.contains(tmpRingAtomProperty)) {
                tmpEdgeAtomNumbers.add(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
            }
        }
        /*At least 3 edge atoms are needed to cause a problem*/
        if(tmpEdgeAtomNumbers.size() < 3) {
            return false;
        }
        /*If one of the edge atoms occurs in more than one other aromatic ring, it is not possible to remove the ring*/
        for(Integer tmpEdgeAtomNumber : tmpEdgeAtomNumbers) {
            int tmpRingCounter = 0;
            for(IAtomContainer tmpRing : tmpClonedRings) {
                for(IAtom tmpRingAtom : tmpRing.atoms()) {
                    //If one of the atoms of the ring to be tested matches one of the edge atoms
                    if(tmpRingAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY).equals(tmpEdgeAtomNumber)) {
                        tmpRingCounter++;
                        if(tmpRingCounter > 1) { //More than one bordering ring
                            return true; //Ring is in an aromatic fused ring system
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks the aromaticity of each atom of the input molecule.
     * If one of the atoms is not aromatic, the whole molecule is not aromatic.
     * @param aMolecule Molecule whose aromaticity is to be determined
     * @return true if the molecule is completely aromatic
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected boolean isAtomContainerAromatic(IAtomContainer aMolecule) throws CloneNotSupportedException {
        /*Check the aromaticity of each atom*/
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        for(IAtom tmpAtom : tmpClonedMolecule.atoms()) {
            //The cycle is not aromatic if one atom is not aromatic
            if(!tmpAtom.isAromatic()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sort out the rings according to the first Schuffenhauer rule.
     * Based on the first rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Remove Heterocycles of Size 3 First.
     * Therefore, size 3 hetero rings are preferred when available.
     * Only these rings will be returned if present. If none are present, all rings entered will be returned.
     * @param aRings Rings to which the first rule is to be applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleOne(List<IAtomContainer> aRings) {
        int tmpHeteroCyclesCounter = 0; //Number of size 3 heterocycles
        List<IAtomContainer> tmpHeteroRingList = new ArrayList<>(aRings.size()); //Saved size 3 heterocycles
        /*Investigate how many size 3 heterocycles there are*/
        for(IAtomContainer tmpRing : aRings) {
            //All rings of size 3
            if(tmpRing.getAtomCount() == 3 ) {
                int tmpHeteroAtomCounter = 0;
                for(IAtom tmpAtom : tmpRing.atoms()) { //Atoms of the ring
                    if(!tmpAtom.getSymbol().equals("C")){
                        tmpHeteroAtomCounter++; //Increase if the ring contains a heteroatom
                    }
                }
                if(tmpHeteroAtomCounter == 1) { //If it is a heterocycle
                    tmpHeteroCyclesCounter++;
                    tmpHeteroRingList.add(tmpRing); //Save this ring
                }
            }
        }
        if(tmpHeteroCyclesCounter == 0) { //If there is no heterocycles of size 3
            return aRings; //Unchanged ring list
        } else { //If there are heterocycles of size 3
            return (tmpHeteroRingList); //Only the heterocycles of size 3
        }
    }

    /**
     * Sort out the rings according to the second Schuffenhauer rule.
     * Based on the second rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Do not remove rings with {@literal >}= 12 Atoms if there are still smaller rings to remove.
     * Therefore, this method prefers smaller rings when macro rings are present.
     * If no macro rings are present, all rings entered will be returned.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected List<IAtomContainer> applySchuffenhauerRuleTwo(List<IAtomContainer> aRings) throws CDKException {
        List<IAtomContainer> tmpSmallRings = new ArrayList<>(aRings.size()); //Rings smaller 12
        /*Identify macrocycles and smaller rings*/
        boolean tmpHasRemovableMacroCycle = false;
        for(IAtomContainer tmpRing : aRings) {
            /*To determine the ring size, the exocyclic atoms must be removed*/
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            /*Check whether there are any removable macrocycles at all*/
            if(tmpRemovedExocyclic.toRingSet().getAtomContainer(0).getAtomCount() > 11 ) {
                tmpHasRemovableMacroCycle = true;
            } else { //All removable non macro rings
                tmpSmallRings.add(tmpRing);
            }
        }
        /*Return the unchanged ring list if there are no macrocycles or small rings*/
        if(!tmpHasRemovableMacroCycle || tmpSmallRings.size() == 0) {
            return aRings;
        }
        /*Return the small rings if there are any*/
        return tmpSmallRings;
    }

    /**
     * Sort out the rings according to the third Schuffenhauer rule.
     * Based on the third rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Choose the Parent Scaffold Having the Smallest Number of Acyclic Linker Bonds.
     * Therefore, linked rings are given priority over fused rings.
     * The rings that are connected to the rest of the molecule via the longest linkers have priority in the removal process.
     * The number of atoms of the linkers is calculated here. The number of linkers is directly dependent on the number of atoms:
     * LinkerBonds = LinkerAtoms - 1
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @param aMolecule Molecule from which a ring is to be removed
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleThree(IAtomContainer aMolecule, List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpRemoveRings = new ArrayList<>(aRings.size()); //Rings with the longest linker
        List<Integer> tmpLinkerSize = new ArrayList<>(aRings.size()); //Linker length of each ring
        /*Generate the murcko fragment, as this removes the multiple bonded atoms at the linkers*/
        int tmpMoleculeAtomCount = this.getMurckoFragment(tmpClonedMolecule).getAtomCount();
        /*Calculate the linker length of each ring. Negative integers are fused rings*/
        for(IAtomContainer tmpRing : aRings) {
            IAtomContainer tmpRemovedRing = this.removeRing(tmpClonedMolecule, true, tmpRing);
            //Generate the murcko fragment, as this removes the multiple bonded atoms at the linkers
            IAtomContainer tmpRemovedRingMurckoFragment = this.getMurckoFragment(tmpRemovedRing);
            //The number of atoms of the removed ring and the molecule from which the ring and the linker were removed are subtracted from the atomic number of the whole molecule
            //This leaves only the atomic number of the linker
            tmpLinkerSize.add(tmpMoleculeAtomCount - (tmpRing.getAtomCount() + tmpRemovedRingMurckoFragment.getAtomCount()));
        }
        //Get the maximum linker size
        Integer tmpMaxList = tmpLinkerSize.stream().mapToInt(v->v).max().orElseThrow(NoSuchElementException::new);
        /*Save the linked rings if available*/
        if(tmpMaxList > -1) { //Is there a linked ring
            for(int tmpCounter = 0 ; tmpCounter < tmpLinkerSize.size(); tmpCounter++) {
                if(tmpLinkerSize.get(tmpCounter).equals(tmpMaxList)) { //Get the rings with the longest linkers
                    tmpRemoveRings.add(aRings.get(tmpCounter));
                }
            }
            return tmpRemoveRings;
        }
        /*Return the unchanged ring list if there are only fused rings*/
        return aRings;
    }

    /**
     * Sort out the rings according to the fourth and fifth Schuffenhauer rule.
     * Based on the fourth and fifth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The fourth rule says: Retain Bridged Rings, Spiro Rings, and Nonlinear Ring Fusion Patterns with Preference.
     * Therefore, delta is calculated as follows: |nrrb - (nR - 1)|
     * nrrb: number of bonds being a member in more than one ring
     * nR: number of rings
     * The rings with the highest absolute delta are returned
     * The artificial creation of spiro ring systems (see scheme 10 and rule 5) is not possible in our implementation,
     * because such a ring would not be detected as terminal (and only terminal rings are considered for removal)
     *
     * The fifth rule says: Bridged Ring Systems Are Retained with Preference over Spiro Ring Systems.
     * Therefore, the rings with the positive maximum delta are preferred over the rings with the negative one.
     * Through the isRingTerminal() method, a removal that leads to spiro ring systems is not available for selection anyway.
     * For performance reasons, rules four and five are combined. This way, delta only has to be calculated once.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @param aMolecule Molecule from which a ring is to be removed
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleFourAndFive(IAtomContainer aMolecule, List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpRingsReturn = new ArrayList<>(aRings.size()); //Rings that are returned
        List<Integer> tmpDeltaList = new ArrayList<>(aRings.size()); //Delta values of all rings
        List<Integer> tmpDeltaListAbs = new ArrayList<>(aRings.size()); //Absolute Delta values of all rings
        /*Calculate the delta values for all rings*/
        for(IAtomContainer tmpRing : aRings) {
            IAtomContainer tmpRingRemoved = this.removeRing(tmpClonedMolecule, true, tmpRing); //Remove the ring
            //-----Eliminate Cycle Error-----
            Cycles tmpCycles = null;
            Iterable<IAtomContainer> tmpCycleIterable = null;
            /*With a few molecules, an error occurs with the relevant CycleFinder. Then the mcb CycleFinder is automatically used.*/
            try {
                tmpCycles = this.getCycleFinder(tmpRingRemoved).find(tmpRingRemoved); //get cycle number(nR)
                tmpCycleIterable = tmpCycles.toRingSet().atomContainers();
            } catch (NegativeArraySizeException e) {
                /*Save as a property of the atoms that from now on the CYCLE_FINDER_BACKUP is to be used*/
                for(IAtomContainer tmpOriginalRing : aRings) {
                    for(IAtom tmpAtom : tmpOriginalRing.atoms()) {
                        tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                    }
                }
                for(IAtom tmpAtom : aMolecule.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                }
                for(IAtom tmpAtom : tmpRingRemoved.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                }
                tmpCycles = this.getCycleFinder(tmpRingRemoved).find(tmpRingRemoved); //get cycle number(nR)
                tmpCycleIterable = tmpCycles.toRingSet().atomContainers();
            }
            HashSet<IBond> tmpCycleBonds = new HashSet<>(aRings.size());
            int tmpFusedRingBondCounter = 0; // Number of bonds being a member in more than one ring(nrrb)
            /*Count nrrb*/
            for(IAtomContainer tmpCycle : tmpCycleIterable) { //Go through all cycle
                for(IBond tmpBond : tmpCycle.bonds()) { //Go through all bonds of each cycle
                    //If the bond is already included in the list, it occurs in several rings
                    if(tmpCycleBonds.contains(tmpBond)) {
                        //It is assumed that a bond can be in a maximum of two rings
                        tmpFusedRingBondCounter++;
                    }
                    tmpCycleBonds.add(tmpBond);
                }
            }
            //Calculate the delta
            int tmpDelta = (tmpFusedRingBondCounter - (tmpCycles.numberOfCycles() - 1));
            tmpDeltaListAbs.add(Math.abs(tmpDelta));
            tmpDeltaList.add(tmpDelta);
        }
        //Get the maximum delta
        Integer tmpDeltaAbsMax = tmpDeltaListAbs.stream().mapToInt(v->v).max().getAsInt();
        Integer tmpDeltaMax = tmpDeltaList.stream().mapToInt(v->v).max().getAsInt();
        if(tmpDeltaAbsMax > 0) {
            /* In case the delta and the absolute delta are equal we jump to rule five.
            Rule five: if there is a positive maximum delta, only get the maximum positive deltas*/
            if(tmpDeltaAbsMax.equals(tmpDeltaMax)) {
                /* Add all rings that have the highest delta to the list*/
                for(int tmpCounter = 0 ; tmpCounter < tmpDeltaList.size(); tmpCounter++) {
                    if(tmpDeltaList.get(tmpCounter).equals(tmpDeltaAbsMax)) {
                        tmpRingsReturn.add(aRings.get(tmpCounter));
                    }
                }
                return tmpRingsReturn; //All rings that have the highest delta
            }
            /*Rule four: Add all rings that have the highest absolute delta to the list*/
            for(int tmpCounter = 0 ; tmpCounter < tmpDeltaListAbs.size(); tmpCounter++) {
                if(tmpDeltaListAbs.get(tmpCounter).equals(tmpDeltaAbsMax)) {
                    tmpRingsReturn.add(aRings.get(tmpCounter));
                }
            }
            return tmpRingsReturn; //All rings that have the highest absolute delta
        }
        /*Return the unchanged ring list*/
        return aRings;
    }

    /**
     * Sort out the rings according to the sixth Schuffenhauer rule.
     * Based on the sixth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Remove Rings of Sizes 3, 5, and 6 First.
     * Therefore, the exocyclic atoms are removed and the size of the ring is determined.
     * Rings of size 3, 5 and 6 are preferred.
     * If no ring of these sizes is present, the original list is returned.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected List<IAtomContainer> applySchuffenhauerRuleSix(List<IAtomContainer> aRings) throws CDKException {
        List<IAtomContainer> tmpReturnRingList = new ArrayList<>(aRings.size());
        /*Size 3, 5 and 6 rings will be added to the list if present*/
        for(IAtomContainer tmpRing : aRings) {
            //To determine the ring size, the exocyclic atoms must be removed
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            IAtomContainer tmpRemovedExoRing = tmpRemovedExocyclic.toRingSet().getAtomContainer(0);
            if(tmpRemovedExoRing.getAtomCount() == 3 || tmpRemovedExoRing.getAtomCount() == 5 || tmpRemovedExoRing.getAtomCount() == 6) {
                tmpReturnRingList.add(tmpRing);
            }
        }
        /*If there are rings of the sizes searched for, they are returned*/
        if(tmpReturnRingList.size() != 0) {
            return tmpReturnRingList;
        }
        /*If there are no rings of the searched sizes, the original rings are returned*/
        return aRings;
    }

    /**
     * Sort out the rings according to the seventh Schuffenhauer rule.
     * Based on the seventh rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: A Fully Aromatic Ring System Must Not Be Dissected in a Way That the Resulting System Is Not Aromatic anymore.
     * It was changed to: The number of aromatic rings should be reduced by a maximum of one, when a ring is removed.
     * Therefore, no additional aromatic rings should be deleted by removing a ring.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @param aMolecule Molecule from which a ring is to be removed
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleSeven(IAtomContainer aMolecule, List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        List<IAtomContainer> tmpReturnRings = new ArrayList<>(aRings.size());
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        /*Check the number of aromatic rings in the original molecule*/
        int tmpOriginalAromaticRingCounter = 0;
        //Get all cycles without exocyclic atoms
        Cycles tmpOriginalCycles = this.getCycleFinder(tmpClonedMolecule).find(tmpClonedMolecule);
        for(IAtomContainer tmpCycle : tmpOriginalCycles.toRingSet().atomContainers()) {
            /*Count the aromatic rings*/
            if(this.isAtomContainerAromatic(tmpCycle)) {
                tmpOriginalAromaticRingCounter++;
            }
        }
        /*Remove each ring and count the number of remaining aromatic rings*/
        for(IAtomContainer tmpRing : aRings) {
            IAtomContainer tmpRemovedRing = this.removeRing(aMolecule, true, tmpRing);
            tmpRemovedRing = this.getScaffoldInternal(tmpRemovedRing, true, false, null, this.scaffoldModeSetting);
            this.aromaticityModelSetting.apply(tmpRemovedRing);
            /*Check the number of aromatic rings*/
            int tmpRemovedAromaticRingCounter = 0;
            //-----Eliminate Cycle Error-----
            Cycles tmpRemovedCycles = null;
            Iterable<IAtomContainer> tmpCycleIterable = null;
            /*With a few molecules, an error occurs with the relevant CycleFinder. Then the mcb CycleFinder is automatically used.*/
            try {
                tmpRemovedCycles = this.getCycleFinder(tmpRemovedRing).find(tmpRemovedRing);
                tmpCycleIterable = tmpRemovedCycles.toRingSet().atomContainers();
            } catch (Exception e) {
                /*Save as a property of the atoms that from now on the CYCLE_FINDER_BACKUP is to be used*/
                for(IAtomContainer tmpOriginalRing : aRings) {
                    for(IAtom tmpAtom : tmpOriginalRing.atoms()) {
                        tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                    }
                }
                for(IAtom tmpAtom : aMolecule.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                }
                for(IAtom tmpAtom : tmpRemovedRing.atoms()) {
                    tmpAtom.setProperty(ScaffoldGenerator.CYCLE_FINDER_BACKUP_PROPERTY, true);
                }
                tmpRemovedCycles = this.getCycleFinder(tmpRemovedRing).find(tmpRemovedRing);
                tmpCycleIterable = tmpRemovedCycles.toRingSet().atomContainers();
            }
            /*Check the aromaticity of each Cycle*/
            for(IAtomContainer tmpCycle : tmpCycleIterable) {
                /*Count the aromatic rings*/
                if(this.isAtomContainerAromatic(tmpCycle)) {
                    tmpRemovedAromaticRingCounter++;
                }
            }
            /*Only fragments whose aromatic rings have decreased by a maximum of 1 are of interest.*/
            if((tmpOriginalAromaticRingCounter - tmpRemovedAromaticRingCounter) < 2 ) {
                tmpReturnRings.add(tmpRing);
            }
        }
        /*If the number of rings has changed due to this rule, return the changed number*/
        if(tmpReturnRings.size() < aRings.size() && tmpReturnRings.size() != 0) {
            return tmpReturnRings;
        }
        return aRings;
    }

    /**
     * Sort out the rings according to the eighth Schuffenhauer rule.
     * Based on the eighth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Remove Rings with the Least Number of Heteroatoms First
     * Therefore, the exocyclic atoms are removed and the number of cyclic heteroatoms is counted
     * Rings with the smallest number of heteroatoms are preferred
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if all rings have the same size of heteroatoms.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected List<IAtomContainer> applySchuffenhauerRuleEight(List<IAtomContainer> aRings) throws CDKException {
        List<IAtomContainer> tmpReturnRingList = new ArrayList<>(aRings.size());
        Integer tmpMinNumberOfHeteroAtoms = null;
        /*Store the rings with the lowest number of cyclic heteroatoms*/
        for(IAtomContainer tmpRing : aRings) {
            //get the cyclic atoms
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            //Number of heteroatoms in the ring
            int tmpNumberOfHeteroAtoms = 0;
            /*Count the heteroatoms*/
            for(IAtom tmpAtom : tmpRemovedExocyclic.toRingSet().getAtomContainer(0).atoms()) {
                if(!tmpAtom.getSymbol().equals("C")) {
                    tmpNumberOfHeteroAtoms++;
                }
            }
            //Set the value of the first ring as starting value
            if(tmpMinNumberOfHeteroAtoms == null) {
                tmpMinNumberOfHeteroAtoms = tmpNumberOfHeteroAtoms;
            }
            /*If the number of heteroatoms matches the number of the least heteroatoms so far, add the ring to the list*/
            if(tmpNumberOfHeteroAtoms == tmpMinNumberOfHeteroAtoms) {
                tmpReturnRingList.add(tmpRing);
                continue;
            }
            /*If the ring has fewer heteroatoms, clear the list and add this ring to it*/
            if(tmpNumberOfHeteroAtoms < tmpMinNumberOfHeteroAtoms) {
                tmpMinNumberOfHeteroAtoms = tmpNumberOfHeteroAtoms;
                tmpReturnRingList.clear();
                tmpReturnRingList.add(tmpRing);
            }
        }
        return tmpReturnRingList;
    }

    /**
     * Sort out the rings according to the ninth Schuffenhauer rule.
     * Based on the ninth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: If the Number of Heteroatoms Is Equal, the Priority of Heteroatoms to Retain is N {@literal >} O {@literal >} S.
     * Therefore, the number of cyclic N, O and S of each ring is counted
     * The rings that have the lowest value of heteroatoms according to this rule are selected.
     * If two rings have the same number of N, their amount of O is considered.
     * Heteroatoms that are not N, O or S are ignored.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule.
     * Returns the unchanged list if all rings have the same size of heteroatoms.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected List<IAtomContainer> applySchuffenhauerRuleNine(List<IAtomContainer> aRings) throws CDKException {
        List<IAtomContainer> tmpReturnRingList = new ArrayList<>(aRings.size());
        /*Calculate the maximum number of heteroatoms that can occur*/
        Integer tmpMinNCount = null;
        Integer tmpMinOCount = null;
        Integer tmpMinSCount = null;
        /*Get the rings with the smallest value of heteroatoms*/
        for(IAtomContainer tmpRing : aRings) {
            //Only cyclic heteroatoms count
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            int tmpNCounter = 0;
            int tmpOCounter = 0;
            int tmpSCounter = 0;
            /*Record the composition of the heteroatoms for each ring*/
            for(IAtom tmpAtom : tmpRemovedExocyclic.toRingSet().getAtomContainer(0).atoms()) {
                if(tmpAtom.getSymbol().equals("N")) {
                    tmpNCounter++;
                }
                if(tmpAtom.getSymbol().equals("O")) {
                    tmpOCounter++;
                }
                if(tmpAtom.getSymbol().equals("S")) {
                    tmpSCounter++;
                }
            }
            /*Search for the ring with the lowest value of heteroatoms*/
            //Set the values of the first ring as starting values
            if(tmpMinNCount == null) {
                tmpMinNCount = tmpNCounter;
                tmpMinOCount = tmpOCounter;
                tmpMinSCount = tmpSCounter;
            }
            //If the ring contains more N than the previous minimum, it is not eligible for removal
            if(tmpNCounter > tmpMinNCount) {
                continue;
            }
            //If this ring contains less N than the previous minimum, it is considered for removal
            if(tmpNCounter < tmpMinNCount) {
                tmpReturnRingList.clear();
                tmpReturnRingList.add(tmpRing);
                tmpMinNCount = tmpNCounter;
                tmpMinOCount = tmpOCounter;
                tmpMinSCount = tmpSCounter;
                continue;
            }
            /*If this ring has exactly as many N as the previous minimum*/
            //If the ring contains more O than the previous minimum, it is not eligible for removal
            if(tmpOCounter > tmpMinOCount) {
                continue;
            }
            //If this ring contains less O than the previous minimum, it is considered for removal
            if(tmpOCounter < tmpMinOCount) {
                tmpReturnRingList.clear();
                tmpReturnRingList.add(tmpRing);
                tmpMinNCount = tmpNCounter;
                tmpMinOCount = tmpOCounter;
                tmpMinSCount = tmpSCounter;
                continue;
            }
            /*If this ring has exactly as many N and O as the previous minimum*/
            //If the ring contains more S than the previous minimum, it is not eligible for removal
            if(tmpSCounter > tmpMinSCount) {
                continue;
            }
            //If this ring contains less S than the previous minimum, it is considered for removal
            if(tmpSCounter < tmpMinSCount) {
                tmpReturnRingList.clear();
                tmpReturnRingList.add(tmpRing);
                tmpMinNCount = tmpNCounter;
                tmpMinOCount = tmpOCounter;
                tmpMinSCount = tmpSCounter;
                continue;
            }
            /*If this ring has exactly as many N, O and S as the previous minimum*/
            tmpReturnRingList.add(tmpRing);
        }
        return tmpReturnRingList;
    }

    /**
     * Sort out the rings according to the tenth Schuffenhauer rule.
     * Based on the tenth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Smaller Rings are Removed First
     * Exocyclic atoms are not observed
     * Therefore, the exocyclic atoms are removed and the number of cyclic atoms is counted
     * Rings with the smallest number of atoms are preferred
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if all rings have the same size of heteroatoms.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     */
    protected List<IAtomContainer> applySchuffenhauerRuleTen(List<IAtomContainer> aRings) throws CDKException {
        List<IAtomContainer> tmpReturnRingList = new ArrayList<>(aRings.size());
        Integer tmpMinimumAtomNumber = null;
        /*Store the rings with the lowest number of atoms*/
        for(IAtomContainer tmpRing : aRings) {
            //Remove the exocyclic atoms
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            IAtomContainer tmpCycle = tmpRemovedExocyclic.toRingSet().getAtomContainer(0);
            int tmpAtomNumber = tmpCycle.getAtomCount();
            /*Set the values of the first ring as starting values*/
            if(tmpMinimumAtomNumber == null) {
                tmpMinimumAtomNumber = tmpAtomNumber;
            }
            /*If the number of atoms matches the number of the least atoms so far, add the ring to the list*/
            if(tmpAtomNumber == tmpMinimumAtomNumber) {
                tmpReturnRingList.add(tmpRing);
                continue;
            }
            /*If the ring has fewer atoms, clear the list and add this ring to it*/
            if(tmpAtomNumber < tmpMinimumAtomNumber) {
                tmpMinimumAtomNumber = tmpAtomNumber;
                tmpReturnRingList.clear();
                tmpReturnRingList.add(tmpRing);
            }
        }
        return tmpReturnRingList;
    }

    /**
     * Sort out the rings according to the eleventh Schuffenhauer rule.
     * Based on the eleventh rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: For Mixed Aromatic/Nonaromatic Ring Systems, Retain Nonaromatic Rings with Priority.
     * Therefore, all rings are tested for aromaticity and the nonaromatic ones are preferably removed.
     * If it is not a mixed system, all rings will be returned.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @return List of rings to be removed first according to the rule. Returns the unchanged list,
     * if the molecule is not a mixed ring system.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleEleven(List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        List<IAtomContainer> tmpReturnRingList = new ArrayList<>(aRings.size());
        /*Add all fully aromatic rings to the list*/
        for(IAtomContainer tmpRing  : aRings) {
            //Remove the exocyclic atoms
            Cycles tmpRemovedExocyclic = this.getCycleFinder(tmpRing).find(tmpRing);
            IAtomContainer tmpCycle = tmpRemovedExocyclic.toRingSet().getAtomContainer(0);
            /*The ring is only fully aromatic, if all cyclic atoms are aromatic*/
            /*Add aromatic rings to the list*/
            if(this.isAtomContainerAromatic(tmpCycle)) {
                tmpReturnRingList.add(tmpRing);
            }
        }
        //Return aromatic rings if any are present
        if(tmpReturnRingList.size() > 0){
            return tmpReturnRingList;
        }
        //Return all rings if non-aromatic rings are present
        return aRings;
    }

    /**
     * Sort out the rings according to the twelfth Schuffenhauer rule.
     * Based on the twelfth rule from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * The rule says: Remove Rings First Where the Linker Is Attached
     * to a Ring Heteroatom at Either End of the Linker.
     * Therefore, rings that attached to a linker that have a heteroatom at al. least one end are prioritised. <p>
     *
     * Two cases are treated differently.
     * In the first case, linkers consisting of only one bond are selected.
     * In this case, the ring to be examined is directly linked to the Murcko fragment from which this ring was removed.
     * This bond is found, and it is checked whether it contains at least one heteroatom.
     * In the second case, all other linkers are treated. These consist of at least one atom.
     * Here, the linker atoms are filtered out by subtracting the atoms of the ring to be examined and
     * the atoms of the murcko fragment in which this ring was removed from the total molecule.
     * The remaining atoms are the linker atoms. Now it is checked whether their atoms are bound to heteroatoms of the rest of the molecule.
     *
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @param aMolecule Molecule from which a ring is to be removed
     * @return List of rings to be removed first according to the rule. Returns the unchanged list if the rule cannot be applied to the rings.
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected List<IAtomContainer> applySchuffenhauerRuleTwelve(IAtomContainer aMolecule, List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        List<IAtomContainer> tmpRemoveRings = new ArrayList<>(aRings.size()); //Rings with the longest linker
        /*Check for each ring whether it is attached to a linker with a heteroatom at the end*/
        for(IAtomContainer tmpRing : aRings) {
            if(this.isRingAttachedToHeteroatomLinker(tmpClonedMolecule, tmpRing)) {
                //If the ring is bound to such a linker add it to the list
                tmpRemoveRings.add(tmpRing);
            }
        }
        /*Return the rings attached to a linker with a heteroatom at the end if available*/
        if(tmpRemoveRings.size() > 0) {
            return tmpRemoveRings;
        }
        /*Return the unchanged ring list if the rule cannot be applied to the rings*/
        return aRings;
    }

    /**
     *
     * The ring to be examined is checked to determine whether it is attached to a linker that has a heteroatom at least one end.
     *
     * Two cases are treated differently.
     * In the first case, linkers consisting of only one bond are selected.
     * In this case, the ring to be examined is directly linked to the Murcko fragment from which this ring was removed.
     * This bond is found, and it is checked whether it contains at least one heteroatom.
     * In the second case, all other linkers are treated. These consist of at least one atom.
     * Here, the linker atoms are filtered out by subtracting the atoms of the ring to be examined and
     * the atoms of the Murcko fragment in which this ring was removed from the total molecule.
     * The remaining atoms are the linker atoms. Now it is checked whether their atoms are bound to heteroatoms of the rest of the molecule.
     *
     * Designed for the {@link ScaffoldGenerator#applySchuffenhauerRuleTwelve(IAtomContainer, List)} method.
     * @param aMolecule Molecule from which a ring is to be removed
     * @param aRing rings of the molecule to which the rule is applied
     * @return Whether it is one of the rings sought for
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected boolean isRingAttachedToHeteroatomLinker(IAtomContainer aMolecule, IAtomContainer aRing) throws CDKException, CloneNotSupportedException {
        HashSet<Integer> tmpRingPropertyNumbers = new HashSet<>(aRing.getAtomCount(), 1);
        HashSet<Integer> tmpRemovedMurckoAtomNumbers = new HashSet<>(aMolecule.getAtomCount(), 1);
        /*Save all numbers of the ring atoms*/
        for(IAtom tmpAtom : aRing.atoms()) {
            tmpRingPropertyNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        //Remove the examined ring
        IAtomContainer tmpRemovedRing = this.removeRing(aMolecule, true, aRing);
        //Generate the murcko fragment, as this removes the multiple bonded atoms at the linkers and exocyclic bonds
        IAtomContainer tmpRemovedRingMurckoFragment = this.getMurckoFragment(tmpRemovedRing);
        /*Save all numbers of the murcko fragment atoms*/
        for(IAtom tmpAtom : tmpRemovedRingMurckoFragment.atoms()) {
            tmpRemovedMurckoAtomNumbers.add(tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY));
        }
        /*Treatment of linkers consisting of only one bond*/
        for(IAtom tmpAtom : aMolecule.atoms()) {
            /*Get all ring atoms*/
            Integer tmpAtomProperty = tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(tmpRingPropertyNumbers.contains(tmpAtomProperty)) {
                /*Go thought all bonds of the ring atoms*/
                for(IBond tmpBond : tmpAtom.bonds()) {
                    /*Bond that connects ring atom and murcko fragment, so a linker*/
                    Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if(tmpRemovedMurckoAtomNumbers.contains(tmpBondProperty0)) {
                        /*If the atom of the murcko fragment is a heteroatom, it is one of the rings we are looking for*/
                        if(!tmpBond.getAtom(0).getSymbol().equals("C")) {
                            return true;
                        }
                        /*If the atom of the ring is a heteroatom, it is one of the rings we are looking for*/
                        if(!tmpAtom.getSymbol().equals("C")) {
                            return true;
                        }
                    }
                    /*Bond that connects ring atom and murcko fragment, so a linker.*/
                    Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                    if(tmpRemovedMurckoAtomNumbers.contains(tmpBondProperty1)) {
                        /*If the atom of the murcko fragment is a heteroatom, it is one of the rings we are looking for*/
                        if(!tmpBond.getAtom(1).getSymbol().equals("C")) {
                            return true;
                        }
                        /*If the atom of the ring is a heteroatom, it is one of the rings we are looking for*/
                        if(!tmpAtom.getSymbol().equals("C")) {
                            return true;
                        }
                    }
                }
            }
        }
        /*Treatment for linkers that consist of more than one bond, i.e. at least one atom*/
        //Generate the murcko fragment, as this removes the multiple bonded atoms at the linkers and exocyclic bonds
        IAtomContainer tmpMurcko = this.getMurckoFragment(aMolecule);
        for(IAtom tmpAtom : tmpMurcko.atoms()) {
            /*Atom is not part of the murcko fragment from which the ring was removed, nor is it part of the ring under investigation.
            It is therefore a linker atom.*/
            Integer tmpAtomProperty = tmpAtom.getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
            if(!tmpRemovedMurckoAtomNumbers.contains(tmpAtomProperty) &&
                    !tmpRingPropertyNumbers.contains(tmpAtomProperty)) {
                /*Investigate all bonds of the atom*/
                for(IBond tmpBond : tmpAtom.bonds()) {
                    /*Check if atom 0 of the bond is a heteroatom*/
                    if(!tmpBond.getAtom(0).getSymbol().equals("C")) {
                        /*If the heteroatom is in the ring or in the Murcko fragment with the ring removed, it must be a terminal linker atom*/
                        Integer tmpBondProperty0 = tmpBond.getAtom(0).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        if(tmpRemovedMurckoAtomNumbers.contains(tmpBondProperty0) ||
                                tmpRingPropertyNumbers.contains(tmpBondProperty0)) {
                            return true;
                        }
                    }
                    /*Check if atom 1 of the bond is a heteroatom*/
                    if(!tmpBond.getAtom(1).getSymbol().equals("C")) {
                        /*If the heteroatom is in the ring or in the Murcko fragment with the ring removed, it must be a terminal linker atom*/
                        Integer tmpBondProperty1 = tmpBond.getAtom(1).getProperty(ScaffoldGenerator.SCAFFOLD_ATOM_COUNTER_PROPERTY);
                        if(tmpRemovedMurckoAtomNumbers.contains(tmpBondProperty1) ||
                                tmpRingPropertyNumbers.contains(tmpBondProperty1)) {
                            return true;
                        }
                    }
                }
            }
        }
        //If none of the cases apply, it is not one of the rings we are looking for
        return false;
    }

    /**
     * Remove a ring according to the thirteenth Schuffenhauer rule.
     * Based on rule number 13 from the  <a href="https://doi.org/10.1021/ci600338x">
     * "The Scaffold Tree"</a> paper by Schuffenhauer et al. <p>
     * In contrast to the paper, different types of SMILES can be used here instead of canonical SMILES.
     * The entered rings are sorted alphabetically by their SMILES. The last ring of this sort is returned.
     * If two structures are the same, one is selected arbitrary.
     * @param aRings Removable rings of the molecule to which the rule is applied
     * @param aMolecule Molecule from which a ring is to be removed
     * @return Molecule from which the ring selected by the rule has been removed
     * @throws CDKException problem with CDKHydrogenAdder: Throws if insufficient information is present
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    protected IAtomContainer applySchuffenhauerRuleThirteen(IAtomContainer aMolecule, List<IAtomContainer> aRings) throws CDKException, CloneNotSupportedException {
        IAtomContainer tmpClonedMolecule = aMolecule.clone();
        //Strings are stored in a sorted map. The natural order is alphabetical.
        TreeMap<String, IAtomContainer> tmpRingRemovedMap = new TreeMap();//Sorted map
        for (IAtomContainer tmpRing : aRings) {
            IAtomContainer tmpRingRemoved = this.removeRing(tmpClonedMolecule, true, tmpRing);
            //Remove linker
            IAtomContainer tmpScaffold = this.getScaffoldInternal(tmpRingRemoved, true, false, null, this.scaffoldModeSetting);
            //A few structures do not produce a truly unique SMILES. These are overwritten and are therefore not considered for further selection.
            tmpRingRemovedMap.put(ScaffoldGenerator.SMILES_GENERATOR_SETTING_DEFAULT.create(tmpScaffold), tmpScaffold);
        }
        //The first key in the map is automatically the SMILES key, which has the lower rank in alphabetical order
        IAtomContainer tmpReturnedStructure = tmpRingRemovedMap.get(tmpRingRemovedMap.firstKey());
        return tmpReturnedStructure;
    }
}
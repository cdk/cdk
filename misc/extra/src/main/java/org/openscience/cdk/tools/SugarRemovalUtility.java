/*
 * Copyright (c) 2024 Jonas Schaub <jonas.schaub@uni-jena.de>
 *                    Achim Zielesny <achim.zielesny@w-hs.de>
 *                    Christoph Steinbeck <christoph.steinbeck@uni-jena.de>
 *                    Maria Sorokina <>
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

package org.openscience.cdk.tools;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.DfPattern;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.smarts.SmartsPattern;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerComparator;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Sugar Removal Utility (SRU) implements a generalized algorithm for automated detection of circular and linear
 * sugars in molecular structures and their removal, as described in
 * <a href="https://doi.org/10.1186/s13321-020-00467-y">"Schaub, J., Zielesny, A., Steinbeck, C., Sorokina, M. Too sweet: cheminformatics for deglycosylation in natural products. J Cheminform 12, 67 (2020). https://doi.org/10.1186/s13321-020-00467-y"</a>.
 * It offers various functions to detect and remove sugar moieties with different options.
 *
 * @author Jonas Schaub, Maria Sorokina
 * @version 1.3.2.1
 */
public class SugarRemovalUtility {
    //<editor-fold desc="Enum PreservationModeOption">
    /**
     * Enum with options for how to determine whether a substructure that gets disconnected from the molecule during the
     * removal of a sugar moiety should be preserved or can get removed along with the sugar.
     * <br>The set option plays a major role in discriminating terminal and non-terminal sugar moieties. If only terminal
     * sugar moieties are removed from the molecule, any disconnected structure resulting
     * from a removal step must be too small to keep according to the set preservation mode option and the set threshold
     * and is cleared away. If all the sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>Also, the set preservation mode threshold interrelates with this option. It specifies at least how many heavy
     * atoms or what minimum  molecular weight a disconnected structure needs to have to be preserved (depending on the
     * set option).
     * <p>Important Note for further development: If an option is added here, it needs to have a treatment in the method
     * isTooSmallToPreserve(IAtomContainer). Otherwise, an UnsupportedOperationException will be thrown.
     */
    public static enum PreservationModeOption {
        /**
         * Specifies that all structures should be preserved. Note that if this option is combined with the removal of
         * only terminal moieties, even the smallest attached structure will prevent the removal of a sugar. The most
         * important consequence is that circular sugars with any hydroxy groups will not be removed because these are
         * not considered as part of the sugar moiety.
         */
        ALL (0),

        /**
         * Specifies that whether a structure is worth preserving will be judged by its heavy atom count. The default
         * threshold to preserve a structure is set to 5 heavy atoms (inclusive).
         */
        HEAVY_ATOM_COUNT (5),

        /**
         * Specifies that whether a structure is worth preserving will be judged by its molecular weight. The default
         * threshold to preserve a structure is set to 60 Da (= 5 carbon atoms, inclusive).
         */
        MOLECULAR_WEIGHT (60);

        /**
         * Default preservation mode threshold for the respective option.
         */
        private final int defaultThreshold;

        /**
         * Constructor.
         *
         * @param aDefaultValue the default threshold to preserve a structure for the respective option; must be positive
         * @throws IllegalArgumentException if the default value is negative
         */
        PreservationModeOption(int aDefaultValue) throws IllegalArgumentException {
            if (aDefaultValue < 0) {
                throw new IllegalArgumentException("Default threshold must be positive or zero.");
            }
            this.defaultThreshold = aDefaultValue;
        }

        /**
         * Returns the default threshold to preserve a structure (inclusive) for this option.
         *
         * @return the default threshold
         */
        public int getDefaultThreshold() {
            return this.defaultThreshold;
        }
    }
    //</editor-fold>
    //
    //<editor-fold desc="Public static final constants">
    //<editor-fold desc="Property keys">
    /**
     * Property key to indicate that the structure contains (or contained before removal) circular sugar moieties.
     */
    public static final String CONTAINS_CIRCULAR_SUGAR_PROPERTY_KEY = "CONTAINS_CIRCULAR_SUGAR";

    /**
     * Property key to indicate that the structure contains (or contained before removal) linear sugar moieties.
     */
    public static final String CONTAINS_LINEAR_SUGAR_PROPERTY_KEY = "CONTAINS_LINEAR_SUGAR";

    /**
     * Property key to indicate that the structure contains (or contained before removal) sugar moieties (of any kind).
     */
    public static final String CONTAINS_SUGAR_PROPERTY_KEY = "CONTAINS_SUGAR";

    /**
     * Property key for index that is added to any IAtom object in a given IAtomContainer object for internal unique
     * identification of the respective IAtom object. For internal use only.
     */
    public static final String INDEX_PROPERTY_KEY = "SUGAR_REMOVAL_UTILITY_UNIQUE_ATOM_INDEX";

    /**
     * Key for property that is added to IAtom objects that connect a spiro ring system for identification and preservation
     * of these atoms in the removal process. For internal use only.
     */
    public static final String IS_SPIRO_ATOM_PROPERTY_KEY = "SUGAR_REMOVAL_UTILITY_IS_SPIRO_ATOM";
    //</editor-fold>

    //<editor-fold desc="Substructure patterns">
    /**
     * Linear sugar structures represented as SMILES codes. An input molecule is scanned for these substructures for
     * the detection of linear sugars. This set consists of multiple aldoses, ketoses, and sugar alcohols with sizes
     * between 3 and 7 carbons. Additional structures can be added or specific ones removed from the set at run-time using the
     * respective methods.
     */
    public static final String[] LINEAR_SUGARS_SMILES = {
            /*note: even though it would save time in the constructor to already sort for length decreasing here, the authors
             decided against it to keep this more readable and easier to inspect or extend.*/
            //*aldoses*
            "C(C(C(C(C(C(C=O)O)O)O)O)O)O", //aldoheptose
            "C(C(C(C(C(C=O)O)O)O)O)O", //aldohexose
            "C(C(C(C(C=O)O)O)O)O", //aldopentose
            "C(C(C(C=O)O)O)O", //aldotetrose
            "C(C(C=O)O)O", //aldotriose
            //*ketoses*
            "C(C(C(C(C(C(CO)O)O)O)O)=O)O", //2-ketoheptose
            "C(C(C(C(C(CO)O)O)O)=O)O", //2-ketohexose
            "C(C(C(C(CO)O)O)=O)O", //2-ketopentose
            "C(C(C(CO)O)=O)O", //2-ketotetrose
            "C(C(CO)=O)O", //2-ketotriose
            //*sugar alcohols*
            "C(C(C(C(C(C(CO)O)O)O)O)O)O", //heptitol
            "C(C(C(C(C(CO)O)O)O)O)O", //hexitol
            "C(C(C(C(CO)O)O)O)O", //pentitol
            "C(C(C(CO)O)O)O", //tetraitol
            "C(C(CO)O)O", //triol
            //*deoxy sugars*
            "C(C(C(C(CC=O)O)O)O)O" //2-deoxyhexose
    };

    /**
     * Linear acidic sugar structures represented as SMILES codes. These can be optionally added to the linear sugar structures
     * used for initial detection of linear sugars in an input molecule.
     */
    public static final String[] LINEAR_ACIDIC_SUGARS_SMILES = {
            "C(C(CC(C(CO)O)O)O)(O)=O", //3-deoxyhexonic acid
            "CC(CC(CC(=O)O)O)O", //3,5-Dihydroxyhexanoic acid
            "O=C(O)CC(O)CC(=O)O", //3-hydroxypentanedioic acid
            "O=C(O)CCC(O)C(=O)O", //2-hydroxypentanedioic acid
            "C(C(C(CC(=O)O)O)O)O" //2-deoxypentonic acid
    };

    /**
     * Circular sugar structures represented as SMILES codes. The isolated rings of an input molecule are matched with
     * these structures for the detection of circular sugars. The structures listed here only represent the circular
     * part of sugar rings (i.e. one oxygen atom and multiple carbon atoms). Common exocyclic structures like
     * hydroxy groups are not part of the patterns and therefore not part of the detected circular sugar moieties.
     * The set includes tetrahydrofuran, tetrahydropyran, and oxepane to match furanoses, pyranoses, and heptoses per
     * default. It can be configured at run-time using the respective methods.
     */
    public static final String [] CIRCULAR_SUGARS_SMILES = {
            "C1CCOC1", //tetrahydrofuran to match all 5-membered sugar rings (furanoses)
            "C1CCOCC1", //tetrahydropyran to match all 6-membered sugar rings (pyranoses)
            "C1CCCOCC1" //oxepane to match all 7-membered sugar rings (heptoses)
    };
    //</editor-fold>

    //<editor-fold desc="Default settings">
    /**
     * Default setting for whether only circular sugar moieties that are attached to the parent structure or other sugar
     * moieties via an O-glycosidic bond should be detected and subsequently removed (default: false).
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_ONLY_WITH_O_GLYCOSIDIC_BOND_DEFAULT = false;

    /**
     * Default setting for whether only terminal sugar moieties should be removed, i.e. those that when removed do not
     * cause a split of the remaining molecular structure into two or more disconnected substructures (default: true).
     */
    public static final boolean REMOVE_ONLY_TERMINAL_SUGARS_DEFAULT = true;

    /**
     * Default setting for how to determine whether a substructure that gets disconnected from the molecule during the
     * removal of a sugar moiety should be preserved or can get removed along with the sugar. (default: preserve all
     * structures that consist of 5 or more heavy atoms). The set option plays a major role in discriminating terminal
     * and non-terminal sugar moieties. The minimum value to reach for the respective characteristic to judge by is set
     * in an additional option and all enum constants have their own default values. See the PreservationModeOption enum.
     */
    public static final PreservationModeOption PRESERVATION_MODE_DEFAULT = PreservationModeOption.HEAVY_ATOM_COUNT;

    /**
     * Default setting for whether detected circular sugar candidates must have a sufficient number of attached, single-bonded
     * exocyclic oxygen atoms in order to be detected as a sugar moiety (default: true). The 'sufficient number' is
     * defined in another option / default setting.
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_ONLY_WITH_ENOUGH_EXOCYCLIC_OXYGEN_ATOMS_DEFAULT = true;

    /**
     * Default setting for the minimum ratio of attached exocyclic, single-bonded oxygen atoms to the number of atoms
     * in the candidate circular sugar structure to reach in order to be classified as a sugar moiety
     * if the number of exocyclic oxygen atoms should be evaluated (default: 0.5 so at a minimum 3 connected, exocyclic
     * oxygen atoms for a six-membered ring, for example).
     */
    public static final double EXOCYCLIC_OXYGEN_ATOMS_TO_ATOMS_IN_RING_RATIO_THRESHOLD_DEFAULT = 0.5;

    /**
     * Default setting for whether linear sugar structures that are part of a ring should be detected (default:
     * false). This setting is important for e.g. macrocycles that contain sugars or pseudosugars.
     */
    public static final boolean DETECT_LINEAR_SUGARS_IN_RINGS_DEFAULT = false;

    /**
     * Default setting for whether to add a property to given atom containers to indicate that the structure contains
     * (or contained before removal) sugar moieties (default: true). See property keys in the public constants of this class.
     */
    public static final boolean ADD_PROPERTY_TO_SUGAR_CONTAINING_MOLECULES_DEFAULT = true;

    /**
     * Default setting for the minimum number of carbon atoms a linear sugar candidate must have in order to be detected
     * as a sugar moiety (and subsequently be removed, default: 4, inclusive).
     */
    public static final int LINEAR_SUGAR_CANDIDATE_MIN_SIZE_DEFAULT = 4;

    /**
     * Default setting for the maximum number of carbon atoms a linear sugar candidate can have in order to be detected
     * as a sugar moiety (and subsequently be removed, default: 7, inclusive).
     */
    public static final int LINEAR_SUGAR_CANDIDATE_MAX_SIZE_DEFAULT = 7;

    /**
     * Default setting for whether to include the linear acidic sugar patterns in the linear sugar structures used for
     * initial detection of linear sugars in a given molecule (default: false).
     */
    public static final boolean DETECT_LINEAR_ACIDIC_SUGARS_DEFAULT = false;

    /**
     * Default setting for whether to include spiro rings in the initial set of detected rings considered for circular
     * sugar detection (default: false). If the option is turned on and a spiro sugar ring is removed, its atom connecting
     * it to another ring is preserved.
     */
    public static final boolean DETECT_SPIRO_RINGS_AS_CIRCULAR_SUGARS_DEFAULT = false;

    /**
     * Default setting for whether sugar-like rings that have keto groups should also be detected as circular sugars
     * (default: false). The general rule specified in the original algorithm description is that every potential sugar
     * cycle with an exocyclic double or triple bond is excluded from circular sugar detection. If this option is turned
     * on, an exemption to this rule is made for potential sugar cycles having keto groups. Also, the double-bound oxygen
     * atoms will then count for the number of connected oxygen atoms and the algorithm will not regard how many keto
     * groups are attached to the cycle (might be only one, might be that all connected oxygen atoms are double-bound).
     * If this option is turned off (default), every sugar-like cycle with an exocyclic double or triple bond will be
     * excluded from the detected circular sugars, as it is specified in the original algorithm description.
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_WITH_KETO_GROUPS_DEFAULT = false;
    //</editor-fold>

    //<editor-fold desc="SMARTS patterns for ester, ether, peroxide bonds">
    /**
     * Daylight SMARTS pattern for matching ester bonds between linear sugars.
     * Defines an aliphatic carbon atom connected to a double-bonded oxygen atom and a single-bonded oxygen atom that must not be
     * in a ring and is connected to another aliphatic carbon atom via a single bond. The oxygen atom must not be in a ring to
     * avoid breaking circular sugars.
     */
    public static final SmartsPattern ESTER_SMARTS_PATTERN = SmartsPattern.create("[C](=O)-[O!R]-[C]");

    /**
     * Daylight SMARTS pattern for matching ether bonds between linear sugars.
     * Defines an aliphatic carbon atom connected via single bond to an oxygen atom that must not be in a ring and is in turn connected
     * to another aliphatic carbon atom. The oxygen atom must not be in a ring to avoid breaking circular sugars.
     * This pattern also matches ester bonds which is why esters must be detected and processed before ethers.
     */
    public static final SmartsPattern ETHER_SMARTS_PATTERN = SmartsPattern.create("[C]-[O!R]-[C]");

    /**
     * Daylight SMARTS pattern for matching peroxide bonds between linear sugars.
     * Defines an aliphatic carbon atom connected via single bond to an oxygen atom that must not be in a ring and is connected to
     * another oxygen atom of the same kind, followed by another aliphatic carbon atom.
     * Even tough it is highly unlikely for a peroxide bond to be in a ring, every ring should be preserved.
     */
    public static final SmartsPattern PEROXIDE_SMARTS_PATTERN = SmartsPattern.create("[C]-[O!R]-[O!R]-[C]");
    //</editor-fold>
    //</editor-fold>
    //<editor-fold desc="Private static final constants">
    /**
     * Logger of this class.
     */
    private static final Logger LOGGER = Logger.getLogger(SugarRemovalUtility.class.getName());
    //</editor-fold>
    //
    //<editor-fold desc="Private variables">
    /**
     * Chem object builder for parsing SMILES strings etc.
     */
    private final IChemObjectBuilder builder;

    /**
     * Linear sugar structures parsed into atom containers. Not used for detection but parsed into patterns after sorting.
     */
    private List<IAtomContainer> linearSugarStructuresList;

    /**
     * Circular sugar structures parsed into atom containers. Used for detection via universal isomorphism tester.
     */
    private List<IAtomContainer> circularSugarStructuresList;

    /**
     * Patterns of linear sugar structures to detect linear sugar moieties in the given molecules.
     */
    private List<DfPattern> linearSugarPatternsList;

    /**
     * Linear acidic sugar structures parsed into atom containers. This list serves as reference to be able to add and
     * remove these structures from the linear sugar structures when the respective setting changes.
     */
    private List<IAtomContainer> linearAcidicSugarStructuresList;

    /**
     * Detect glycosidic bond setting.
     */
    private boolean detectCircularSugarsOnlyWithOGlycosidicBondSetting;

    /**
     * Remove only terminal sugar moieties setting.
     */
    private boolean removeOnlyTerminalSugarsSetting;

    /**
     * Preservation mode setting.
     */
    private PreservationModeOption preservationModeSetting;

    /**
     * Threshold for the characteristic of an unconnected fragment set in the preservation mode to judge whether to
     * preserve it or discard it.
     */
    private int preservationModeThresholdSetting;

    /**
     * Include number/ratio of connected, exocyclic oxygen atoms setting.
     */
    private boolean detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting;

    /**
     * Minimum ratio of connected, exocyclic oxygen atoms to the number of atoms in the candidate sugar ring.
     */
    private double exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting;

    /**
     * Remove linear sugars in circular structures setting.
     */
    private boolean detectLinearSugarsInRingsSetting;

    /**
     * Add a property to given sugar-containing atom containers setting.
     */
    private boolean addPropertyToSugarContainingMoleculesSetting;

    /**
     * Linear sugar candidates minimum carbon atom count setting.
     */
    private int linearSugarCandidateMinSizeSetting;

    /**
     * Linear sugar candidates maximum carbon atom count setting.
     */
    private int linearSugarCandidateMaxSizeSetting;

    /**
     * Include linear acidic sugars in linear sugar detection setting.
     */
    private boolean detectLinearAcidicSugarsSetting;

    /**
     * Detect spiro rings as possible sugar rings setting.
     */
    private boolean detectSpiroRingsAsCircularSugarsSetting;

    /**
     * Detect circular sugars with keto groups setting.
     */
    private boolean detectCircularSugarsWithKetoGroupsSetting;
    //</editor-fold>
    //
    //<editor-fold desc="Constructors">
    /**
     * Sole constructor of this class. All settings are set to their default values (see public static constants or
     * enquire via get/is methods). To change these settings, use the respective 'setXY()' methods.
     *
     * @param aBuilder IChemObjectBuilder for i.a. parsing SMILES strings into atom containers
     */
    public SugarRemovalUtility(IChemObjectBuilder aBuilder) throws NullPointerException {
        Objects.requireNonNull(aBuilder, "Given chem object builder is null.");
        this.builder = aBuilder;
        /*method setDetectLinearAcidicSugarsSetting() called in restoreDefaultSettings() checks whether the setting has
        changed, so an initial value must be provided; If the default is true, the initial value must be false in order
        to add the linear acidic sugar patterns to the linear sugar patterns. If the default is false, nothing must be done.*/
        this.detectLinearAcidicSugarsSetting = false;
        this.restoreDefaultSettings();
    }
    //</editor-fold>
    //
    //<editor-fold desc="Public properties get/is/has">
    /**
     * Returns a list of (unique) SMILES strings representing the linear sugar structures an input molecule is scanned for
     * in linear sugar detection. The returned list represents the current state of this list, i.e. externally added
     * structures are included, externally removed structures not, and the linear acidic sugar structures are only
     * included if the respective option is activated. The default structures can also be retrieved from the respective
     * public constant of this class.
     * <br>Note: If a structure cannot be parsed into a SMILES string, it is excluded from the list.
     *
     * @return a list of SMILES codes
     */
    public List<String> getLinearSugarPatternsList() {
        int tmpListSize = this.linearSugarStructuresList.size();
        List<String> tmpSmilesList = new ArrayList<>(tmpListSize);
        SmilesGenerator tmpSmilesGen = new SmilesGenerator(SmiFlavor.Unique);
        for (IAtomContainer tmpLinearSugar : this.linearSugarStructuresList) {
            String tmpSmiles = null;
            try {
                tmpSmiles = tmpSmilesGen.create(tmpLinearSugar);
            } catch (CDKException | NullPointerException anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
            if (!Objects.isNull(tmpSmiles)) {
                try {
                    tmpSmilesList.add(tmpSmiles);
                } catch (Exception anException) {
                    SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
                }
            }
        }
        return tmpSmilesList;
    }

    /**
     * Checks whether the given linear sugar is already present in the list of linear sugar structures an input molecule
     * is scanned for in linear sugar detection. It is checked whether it is isomorph to any linear sugar pattern already
     * present in the list. Note that the return value 'false' does not guarantee its safe addition to the pattern list
     * because is may not comply with other requirements detailed in the 'add'-method. Also note that the linear acidic
     * sugar patterns are only included here if the respective option is turned on.
     *
     * @param aLinearSugar the linear sugar pattern to check for
     * @return true if the linear sugar is already present in the linear sugar pattern list
     * @throws NullPointerException if the given molecule is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or its isomorphism with the already present
     * structures could not be determined
     */
    public boolean hasLinearSugarInPatternsList(IAtomContainer aLinearSugar) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aLinearSugar, "Given atom container is 'null'");
        if (aLinearSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        boolean tmpIsIsomorph = false;
        for (IAtomContainer tmpSugar : this.linearSugarStructuresList) {
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aLinearSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with already present sugar structures.");
            }
            if (tmpIsIsomorph) {
                break;
            }
        }
        return tmpIsIsomorph;
    }

    /**
     * Returns a list of (unique) SMILES strings representing the circular sugar structures an input molecule is scanned for
     * in circular sugar detection. The returned list represents the current state of this list, i.e. externally added
     * structures are included, externally removed structures are not. The default structures can also be retrieved from
     * the respective public constant of this class.
     * <br>Note: If a structure cannot be parsed into a SMILES string, it is excluded from the list.
     *
     * @return a list of SMILES codes
     */
    public List<String> getCircularSugarPatternsList() {
        List<String> tmpSmilesList = new ArrayList<>(this.circularSugarStructuresList.size());
        SmilesGenerator tmpSmilesGen = new SmilesGenerator(SmiFlavor.Unique);
        for (IAtomContainer tmpRingSugar : this.circularSugarStructuresList) {
            String tmpSmiles = null;
            try {
                tmpSmiles = tmpSmilesGen.create(tmpRingSugar);
            } catch (CDKException | NullPointerException anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
            if (!Objects.isNull(tmpSmiles)) {
                try {
                    tmpSmilesList.add(tmpSmiles);
                } catch (Exception anException) {
                    SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
                }
            }
        }
        return tmpSmilesList;
    }

    /**
     * Checks whether the given circular sugar is already present in the list of circular sugar structures an input molecule
     * is scanned for in circular sugar detection. It is checked whether it is isomorph to any circular sugar pattern already
     * present in the list. Note that the return value 'false' does not guarantee its safe addition to the pattern list
     * because is may not comply with other requirements detailed in the 'add'-method.
     *
     * @param aCircularSugar the circular sugar pattern to check for
     * @return true if the circular sugar is already present in the circular sugar pattern list
     * @throws NullPointerException if the given molecule is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or its isomorphism with the already present
     * structures could not be determined
     */
    public boolean hasCircularSugarInPatternsList(IAtomContainer aCircularSugar) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aCircularSugar, "Given atom container is 'null'");
        if (aCircularSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        boolean tmpIsIsomorph = false;
        for (IAtomContainer tmpSugar : this.circularSugarStructuresList) {
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aCircularSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with already present sugar structures.");
            }
            if (tmpIsIsomorph) {
                break;
            }
        }
        return tmpIsIsomorph;
    }

    /**
     * Specifies whether only circular sugar moieties that are attached to the parent structure or other sugar
     * moieties via an O-glycosidic bond should be detected and subsequently removed.
     *
     * @return true if only circular sugar moieties connected via a glycosidic bond are removed according to the current
     * settings
     */
    public boolean areOnlyCircularSugarsWithOGlycosidicBondDetected() {
        return this.detectCircularSugarsOnlyWithOGlycosidicBondSetting;
    }

    /**
     * Specifies whether only terminal sugar moieties should be removed, i.e. those that when removed do not
     * cause a split of the remaining molecular structure into two or more disconnected substructures.
     *
     * @return true if only terminal sugar moieties are removed according to the current settings
     */
    public boolean areOnlyTerminalSugarsRemoved() {
        return this.removeOnlyTerminalSugarsSetting;
    }

    /**
     * Returns the current setting for how to determine whether a substructure that gets disconnected from the molecule during the
     * removal of a sugar moiety should be preserved or can get removed along with the sugar. This can e.g. be judged by its
     * heavy atom count or its molecular weight or it can be specified that all structures are to be preserved. If too
     * small / too light structures are discarded, an additional threshold is specified in the preservation mode threshold
     * setting that the structures have to reach in order to be preserved (i.e. to be judged 'big/heavy enough').
     *
     * @return a PreservationModeOption enum object representing the current setting
     */
    public PreservationModeOption getPreservationModeSetting() {
        return this.preservationModeSetting;
    }

    /**
     * Returns the current threshold of e.g. molecular weight or heavy atom count (depending on the currently set
     * preservation mode) a substructure that gets disconnected from the molecule by the
     * removal of a sugar moiety has to reach in order to be preserved and not discarded.
     *
     * @return an integer specifying the currently set threshold (either specified in Da or number of heavy atoms)
     */
    public int getPreservationModeThresholdSetting() {
        return this.preservationModeThresholdSetting;
    }

    /**
     * Specifies whether detected circular sugar candidates must have a sufficient number of attached
     * exocyclic oxygen atoms in order to be detected as a sugar moiety. If this option is set, the circular sugar candidates
     * have to reach an additionally specified minimum ratio of said oxygen atoms to the number of atoms in the respective
     * ring in order to be seen as a sugar ring and being subsequently removed. See exocyclic oxygen atoms
     * to atoms in ring ratio threshold setting.
     *
     * @return true, if the ratio of attached, exocyclic, single-bonded oxygen atoms to the number of atoms
     * in the candidate sugar ring is evaluated at circular sugar detection according to the current settings
     */
    public boolean areOnlyCircularSugarsWithEnoughExocyclicOxygenAtomsDetected() {
        return this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting;
    }

    /**
     * Returns the currently set minimum ratio of attached, exocyclic, single-bonded oxygen atoms to the number of atoms
     * in the candidate circular sugar structure to reach in order to be classified as a sugar moiety
     * if the number of exocyclic oxygen atoms should be evaluated.
     *
     * @return the minimum ratio of attached oxygen atoms to the number of atoms in the sugar ring; A value of e.g. 0.5
     * means that a six-membered sugar ring needs at least 3 attached oxygen atoms to be classified as a circular sugar
     * moiety
     */
    public double getExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting() {
        return this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting;
    }

    /**
     * Specifies whether linear sugar structures that are part of a ring should be detected according to the current
     * settings. This setting is important for e.g. macrocycles that contain sugars or pseudosugars.
     * <br>Note that potential circular sugar candidates (here always including spiro sugar rings also) are filtered from
     * linear sugar candidates, even with this setting turned on.
     *
     * @return true if linear sugars in rings are detected and removed with the current settings
     */
    public boolean areLinearSugarsInRingsDetected() {
        return this.detectLinearSugarsInRingsSetting;
    }

    /**
     * Specifies whether a property is added to given atom containers that contain (or contained before
     * removal) sugar moieties. See property keys in the public constants of this class.
     *
     * @return true if properties are added to the given atom containers
     */
    public boolean arePropertiesAddedToSugarContainingMolecules() {
        return this.addPropertyToSugarContainingMoleculesSetting;
    }

    /**
     * Returns the currently set minimum number of carbon atoms a linear sugar candidate must have in order to be detected
     * as a sugar moiety (and subsequently be removed).
     *
     * @return the set minimum carbon atom count of detected linear sugars (inclusive)
     */
    public int getLinearSugarCandidateMinSizeSetting() {
        return this.linearSugarCandidateMinSizeSetting;
    }

    /**
     * Returns the currently set maximum number of carbon atoms a linear sugar candidate can have in order to be detected
     * as a sugar moiety (and subsequently be removed).
     *
     * @return the set maximum carbon atom count of detected linear sugars (inclusive)
     */
    public int getLinearSugarCandidateMaxSizeSetting() {
        return this.linearSugarCandidateMaxSizeSetting;
    }

    /**
     * Specifies whether linear acidic sugar patterns are currently included in the linear sugar structures used for
     * initial detection of linear sugars in a given molecule.
     *
     * @return true if acidic sugars are detected
     */
    public boolean areLinearAcidicSugarsDetected() {
        return this.detectLinearAcidicSugarsSetting;
    }

    /**
     * Specifies whether spiro rings are included in the initial set of detected rings considered for circular
     * sugar detection.
     * <br>Note for linear sugar detection: Here, the spiro rings will always be filtered along with the potential
     * circular sugar candidates.
     *
     * @return true if spiro rings can be detected as circular sugars with the current settings
     */
    public boolean areSpiroRingsDetectedAsCircularSugars() {
        return this.detectSpiroRingsAsCircularSugarsSetting;
    }

    /**
     * Specifies whether potential sugar cycles with keto groups are detected in circular sugar detection. The general
     * rule specified in the original algorithm description is that every potential sugar cycle with an exocyclic double
     * or triple bond is excluded from circular sugar detection. If this option is turned
     * on, an exemption to this rule is made for potential sugar cycles having keto groups. Also, the double-bound oxygen
     * atoms will then count for the number of connected oxygen atoms and the algorithm will not regard how many keto
     * groups are attached to the cycle (might be only one, might be that all connected oxygen atoms are double-bound).
     * If this option is turned off, every sugar-like cycle with an exocyclic double or triple bond will be
     * excluded from the detected circular sugars, as it is specified in the original algorithm description.
     *
     * @return true if potential sugar cycles having keto groups are detected in circular sugar detection
     */
    public boolean areCircularSugarsWithKetoGroupsDetected() {
        return this.detectCircularSugarsWithKetoGroupsSetting;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Public properties set/add/remove/clear">
    /**
     * Allows to add an additional sugar ring to the list of circular sugar structures an input molecule is scanned for
     * in circular sugar detection. The given structure must not be isomorph to the already present
     * ones and it must contain exactly one isolated ring without any exocyclic moieties because only the isolated
     * rings of an input structure are matched with the circular sugar patterns.
     *
     * @param aCircularSugar an atom container representing only one isolated sugar ring
     * @throws NullPointerException if the given atom container is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or does represent a molecule that contains
     * no isolated ring, more than one isolated ring, consists of more structures than one isolated ring or is isomorph
     * to a circular sugar structure already present
     * @return true if the addition was successful
     */
    public boolean addCircularSugarToPatternsList(IAtomContainer aCircularSugar)
            throws NullPointerException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aCircularSugar, "Given atom container is 'null'");
        if (aCircularSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        int[][] tmpAdjList = GraphUtil.toAdjList(aCircularSugar);
        RingSearch tmpRingSearch = new RingSearch(aCircularSugar, tmpAdjList);
        List<IAtomContainer> tmpIsolatedRingFragments = tmpRingSearch.isolatedRingFragments();
        int tmpSize = tmpIsolatedRingFragments.size();
        if (tmpSize != 1) {
            throw new IllegalArgumentException("Given molecule does not contain an isolated ring or too many rings.");
        }
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        boolean tmpIsolatedRingMatchesEntireInputStructure = false;
        IAtomContainer tmpIsolatedRing = tmpIsolatedRingFragments.get(0);
        try {
            tmpIsolatedRingMatchesEntireInputStructure = tmpUnivIsomorphTester.isIsomorph(aCircularSugar, tmpIsolatedRing);
        } catch (CDKException aCDKException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
        }
        if (!tmpIsolatedRingMatchesEntireInputStructure) {
            throw new IllegalArgumentException("The given structure does not only consist of one isolated ring.");
        }
        for (IAtomContainer tmpSugar : this.circularSugarStructuresList) {
            boolean tmpIsIsomorph = false;
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aCircularSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with already present sugar structures.");
            }
            if (tmpIsIsomorph) {
                throw new IllegalArgumentException("Given sugar pattern is already present.");
            }
        }
        //</editor-fold>
        boolean tmpAdditionWasSuccessful = false;
        try {
            tmpAdditionWasSuccessful = this.circularSugarStructuresList.add(aCircularSugar);
        } catch (Exception anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not add sugar to the list of circular sugars.");
        }
        if (tmpAdditionWasSuccessful) {
            Comparator<IAtomContainer> tmpComparator = new AtomContainerComparator().reversed();
            //note: this can throw various exceptions but they should not appear here
            this.circularSugarStructuresList.sort(tmpComparator);
        }
        return tmpAdditionWasSuccessful;
    }

    /**
     * Allows to add an additional sugar ring (represented as a SMILES string) to the list of circular sugar structures
     * an input molecule is scanned for
     * in circular sugar detection. The given structure must not be isomorph to the already present
     * ones and it must contain exactly one isolated ring without any exocyclic moieties because only the isolated
     * rings of an input structure are matched with the circular sugar patterns.
     *
     * @param aSmilesCode a SMILES code representation of a molecule consisting of only one isolated sugar ring
     * @throws NullPointerException if the given string is 'null'
     * @throws IllegalArgumentException if the given SMILES string is empty or does represent a molecule that contains
     * no isolated ring, more than one isolated ring, consists of more structures than one isolated ring, is isomorph
     * to a circular sugar structure already present or if the given SMILES string cannot be parsed into a molecular
     * structure
     * @return true if the addition was successful
     */
    public boolean addCircularSugarToPatternsList(String aSmilesCode) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSmilesCode, "Given SMILES code is 'null'");
        if (aSmilesCode.isEmpty()) {
            throw new IllegalArgumentException("Given SMILES code is empty");
        }
        SmilesParser tmpSmiPar = new SmilesParser(this.builder);
        IAtomContainer tmpRingSugar = null;
        try {
            tmpRingSugar = tmpSmiPar.parseSmiles(aSmilesCode);
        } catch (InvalidSmilesException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not parse given string as a SMILES code.");
        }
        //throws NullPointerException and IllegalArgumentException that are relayed
        return this.addCircularSugarToPatternsList(tmpRingSugar);
    }

    /**
     * Allows to add an additional linear sugar to the list of linear sugar structures an input molecule is scanned for
     * in linear sugar detection. The given structure must not be isomorph to the already present
     * ones or the patterns for circular sugars.
     * <br>Note: If the given structure contains cycles, the
     * option to detect linear sugars in rings needs to be enabled to detect its matches entirely. Otherwise, all
     * circular substructures of the 'linear sugars' will not be detected.
     * <br>Additional note: If the given structure is isomorph to a default linear acidic sugar pattern, it may be added
     * here when the option to detect these structures is turned off but will be removed from the pattern list if the option
     * is turned on and off again after this addition.
     *
     * @param aLinearSugar an atom container representing a molecular structure to search for at linear sugar detection
     * @throws NullPointerException if given atom container is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or is isomorph to a linear sugar structure
     * already present or a circular sugar pattern
     * @return true if the addition was successful
     */
    public boolean addLinearSugarToPatternsList(IAtomContainer aLinearSugar) throws NullPointerException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aLinearSugar, "Given atom container is 'null'");
        if (aLinearSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        //note: no check for linearity here to allow adding of structures that contain rings, e.g. amino acids
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        for (IAtomContainer tmpSugar : this.linearSugarStructuresList) {
            boolean tmpIsIsomorph = false;
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aLinearSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with already present sugar structures.");
            }
            if (tmpIsIsomorph) {
                throw new IllegalArgumentException("Given sugar pattern is already present.");
            }
        }
        for (IAtomContainer tmpSugar : this.circularSugarStructuresList) {
            boolean tmpIsIsomorph = false;
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aLinearSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with already present sugar structures.");
            }
            if (tmpIsIsomorph) {
                throw new IllegalArgumentException("Given sugar pattern isomorph to a circular sugar pattern.");
            }
        }
        //</editor-fold>
        boolean tmpAdditionWasSuccessful = false;
        try {
            tmpAdditionWasSuccessful = this.linearSugarStructuresList.add(aLinearSugar);
        } catch (Exception anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not add sugar to the list of linear sugars");
        }
        if (tmpAdditionWasSuccessful) {
            this.updateLinearSugarPatterns();
        }
        return tmpAdditionWasSuccessful;
    }

    /**
     * Allows to add an additional linear sugar (represented as SMILES string) to the list of linear sugar structures
     * an input molecule is scanned for
     * in linear sugar detection. The given structure must not be isomorph to the already present
     * ones or the patterns for circular sugars.
     * <br>Note: If the given structure contains cycles, the
     * option to detect linear sugars in rings needs to be enabled to detect its matches entirely. Otherwise, all
     * circular substructures of the 'linear sugars' will not be detected.
     * <br>Additional note: If the given structure is isomorph to a default linear acidic sugar pattern, it may be added
     * here when the option to detect these structures is turned off but will be removed from the pattern list if the option
     * is turned on and off again after this addition.
     *
     * @param aSmilesCode a SMILES code representation of a molecular structure to search for
     * @throws NullPointerException if given string is 'null'
     * @throws IllegalArgumentException if the given SMILES string is empty or does represent a molecule that is isomorph
     * to a linear sugar structure already present or a circular sugar pattern or if it cannot be parsed into a molecular structure
     * @return true if the addition was successful
     */
    public boolean addLinearSugarToPatternsList(String aSmilesCode) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSmilesCode, "Given SMILES code is 'null'");
        if (aSmilesCode.isEmpty()) {
            throw new IllegalArgumentException("Given SMILES code is empty");
        }
        SmilesParser tmpSmiPar = new SmilesParser(this.builder);
        IAtomContainer tmpLinearSugar = null;
        try {
            tmpLinearSugar = tmpSmiPar.parseSmiles(aSmilesCode);
        } catch (InvalidSmilesException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not parse given string as a SMILES code.");
        }
        //throws NullPointerException and IllegalArgumentException that are relayed
        return this.addLinearSugarToPatternsList(tmpLinearSugar);
    }

    /**
     * Allows to remove a sugar ring pattern (represented as SMILES string) from the list of circular sugar structures
     * an input molecule is scanned for in circular sugar detection. The given character string must be a valid SMILES
     * notation and be isomorph to one of the currently used structure patterns. Example usage: Pass the argument
     * "C1CCOC1" (tetrahydrofuran) to stop detecting furanoses in the circular sugar detection algorithm.
     *
     * @param aSmilesCode a SMILES code representation of a structure present in the circular sugar pattern list
     * @throws NullPointerException if the given string is 'null'
     * @throws IllegalArgumentException if the given SMILES string is empty or cannot be parsed into a molecule or the
     * given structure cannot be found in the circular sugar pattern list
     * @return true if the removal was successful
     */
    public boolean removeCircularSugarFromPatternsList(String aSmilesCode) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSmilesCode, "Given SMILES code is 'null'");
        if (aSmilesCode.isEmpty()) {
            throw new IllegalArgumentException("Given SMILES code is empty");
        }
        SmilesParser tmpSmiPar = new SmilesParser(this.builder);
        IAtomContainer tmpCircularSugar = null;
        try {
            tmpCircularSugar = tmpSmiPar.parseSmiles(aSmilesCode);
        } catch (InvalidSmilesException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not parse given string as a SMILES code.");
        }
        //throws NullPointerException and IllegalArgumentException that are relayed
        return this.removeCircularSugarFromPatternsList(tmpCircularSugar);
    }

    /**
     * Allows to remove a sugar ring from the list of circular sugar structures
     * an input molecule is scanned for in circular sugar detection. The given molecule must be isomorph to one of the
     * currently used structure patterns. Example usage: Pass an atom container object representing the structure of
     * tetrahydrofuran to stop detecting furanoses in the circular sugar detection algorithm.
     *
     * @param aCircularSugar a molecule isomorph to a structure present in the circular sugar pattern list
     * @throws NullPointerException if the given atom container is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or its structure is not isomorph to a circular
     * sugar pattern structure in use
     * @return true if the removal was successful
     */
    public boolean removeCircularSugarFromPatternsList(IAtomContainer aCircularSugar) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aCircularSugar, "Given atom container is 'null'");
        if (aCircularSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        boolean tmpIsIsomorph = false;
        boolean tmpRemovalSuccessful = false;
        for (IAtomContainer tmpSugar : this.circularSugarStructuresList) {
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aCircularSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with present sugar structures.");
            }
            if (tmpIsIsomorph) {
                try {
                    tmpRemovalSuccessful = this.circularSugarStructuresList.remove(tmpSugar);
                } catch (Exception anException) {
                    SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
                    throw new IllegalArgumentException("Could not remove sugar from the list of circular sugars");
                }
                break;
            }
        }
        if (!tmpIsIsomorph) {
            throw new IllegalArgumentException("No circular sugar matching the given molecule was found.");
        } else {
            if (tmpRemovalSuccessful) {
                Comparator<IAtomContainer> tmpComparator = new AtomContainerComparator().reversed();
                //note: this can throw various exceptions but they should not appear here
                this.circularSugarStructuresList.sort(tmpComparator);
            }
            return tmpRemovalSuccessful;
        }
    }

    /**
     * Allows to remove a linear sugar pattern (represented as SMILES string) from the list of linear sugar structures
     * an input molecule is scanned for in linear sugar detection. The given character string must be a valid SMILES
     * notation and be isomorph to one of the currently used structure patterns. Example usage: Pass the argument
     * "C(C(C=O)O)O" (aldotriose) to stop detecting such small aldoses in the linear sugar detection algorithm. Please
     * note that adjusting the linear sugar candidate minimum and maximum sizes can be more straightforward than removing
     * patterns here.
     * <br>Note: If the linear acidic sugars are currently included in the linear sugar pattern structures, individual
     * structures of this group can be removed here.
     *
     * @param aSmilesCode a SMILES code representation of a structure present in the linear sugar pattern list
     * @throws NullPointerException if the given string is 'null'
     * @throws IllegalArgumentException if the given SMILES string is empty or cannot be parsed into a molecule or the
     * given structure cannot be found in the linear sugar pattern list
     * @return true if the removal was successful
     */
    public boolean removeLinearSugarFromPatternsList(String aSmilesCode) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSmilesCode, "Given SMILES code is 'null'");
        if (aSmilesCode.isEmpty()) {
            throw new IllegalArgumentException("Given SMILES code is empty");
        }
        SmilesParser tmpSmiPar = new SmilesParser(this.builder);
        IAtomContainer tmpLinearSugar = null;
        try {
            tmpLinearSugar = tmpSmiPar.parseSmiles(aSmilesCode);
        } catch (InvalidSmilesException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            throw new IllegalArgumentException("Could not parse given string as a SMILES code.");
        }
        //throws NullPointerException and IllegalArgumentException that are relayed
        return this.removeLinearSugarFromPatternsList(tmpLinearSugar);
    }

    /**
     * Allows to remove a linear sugar pattern from the list of linear sugar structures
     * an input molecule is scanned for in linear sugar detection. The given molecule must
     * be isomorph to one of the currently used structure patterns. Example usage: Pass an atom container object representing
     * the structure of aldotriose to stop detecting such small aldoses in the linear sugar detection algorithm. Please
     * note that adjusting the linear sugar candidate minimum and maximum sizes can be more straightforward than removing
     * patterns here.
     * <br>Note: If the linear acidic sugars are currently included in the linear sugar pattern structures, individual
     * structures of this group can be removed here.
     *
     * @param aLinearSugar a molecule isomorph to a structure present in the linear sugar pattern list
     * @throws NullPointerException if the given atom container is 'null'
     * @throws IllegalArgumentException if the given atom container is empty or its structure is not isomorph to a
     * linear sugar pattern structure in use
     * @return true if the removal was successful
     */
    public boolean removeLinearSugarFromPatternsList(IAtomContainer aLinearSugar) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aLinearSugar, "Given atom container is 'null'");
        if (aLinearSugar.isEmpty()) {
            throw new IllegalArgumentException("Given atom container is empty.");
        }
        UniversalIsomorphismTester tmpUnivIsomorphTester = new UniversalIsomorphismTester();
        boolean tmpIsIsomorph = false;
        boolean tmpRemovalSuccessful = false;
        for (IAtomContainer tmpSugar : this.linearSugarStructuresList) {
            try {
                tmpIsIsomorph = tmpUnivIsomorphTester.isIsomorph(tmpSugar, aLinearSugar);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                throw new IllegalArgumentException("Could not determine isomorphism with present sugar structures.");
            }
            if (tmpIsIsomorph) {
                try {
                    tmpRemovalSuccessful = this.linearSugarStructuresList.remove(tmpSugar);
                } catch (Exception anException) {
                    SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
                    throw new IllegalArgumentException("Could not remove sugar from the list of linear sugars");
                }
                break;
            }
        }
        if (!tmpIsIsomorph) {
            throw new IllegalArgumentException("No linear sugar matching the given molecule was found.");
        } else {
            if (tmpRemovalSuccessful) {
                this.updateLinearSugarPatterns();
            }
            return tmpRemovalSuccessful;
        }
    }

    /**
     * Clears all the circular sugar structures an input molecule is scanned for in circular sugar detection.
     */
    public void clearCircularSugarPatternsList() {
        try {
            this.circularSugarStructuresList.clear();
        } catch (UnsupportedOperationException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            this.circularSugarStructuresList = new ArrayList<>(SugarRemovalUtility.CIRCULAR_SUGARS_SMILES.length);
        }
    }

    /**
     * Clears all the linear sugar structures an input molecule is scanned for in linear sugar detection. If the detection
     * of linear acidic sugars is turned on, it is turned off in this method and these structures are also cleared from
     * the linear sugar patterns.
     */
    public void clearLinearSugarPatternsList() {
        try {
            if (this.detectLinearAcidicSugarsSetting) {
                this.setDetectLinearAcidicSugarsSetting(false);
            }
            this.linearSugarStructuresList.clear();
            this.linearSugarPatternsList.clear();
        } catch (UnsupportedOperationException anException) {
            SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            this.detectLinearAcidicSugarsSetting = false;
            this.linearSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
            this.linearSugarPatternsList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        }
    }

    /**
     * Sets the option to only detect (and subsequently remove) circular sugar moieties that are attached to the parent
     * structure or other sugar moieties via an O-glycosidic bond.
     *
     * @param aBoolean true, if only circular sugar moieties connected via a glycosidic bond should be detected (and removed)
     */
    public void setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(boolean aBoolean) {
        this.detectCircularSugarsOnlyWithOGlycosidicBondSetting = aBoolean;
    }

    /**
     * Sets the option to remove only terminal sugar moieties, i.e. those that when removed do not
     * cause a split of the remaining molecular structure into two or more disconnected substructures.
     *
     * @param aBoolean true, if only terminal sugar moieties should be removed
     */
    public void setRemoveOnlyTerminalSugarsSetting(boolean aBoolean) {
        this.removeOnlyTerminalSugarsSetting = aBoolean;
    }

    /**
     * Sets the preservation mode for structures that get disconnected by sugar removal and the preservation mode threshold
     * is set to the default value of the given enum constant.
     * The preservation mode option specifies how to determine whether a substructure that gets disconnected from the molecule during the
     * removal of a sugar moiety should be preserved or can get removed along with the sugar. This can e.g. be judged by its
     * heavy atom count or its molecular weight or it can be specified that all structures are to be preserved.
     * The available options can be selected from the PreservationModeOption enum. If too
     * small / too light structures are discarded, an additional threshold is specified in the preservation mode threshold
     * setting that the structures have to reach in order to be preserved (i.e. to be judged 'big/heavy enough'). This
     * threshold is set to the default value of the given enum constant in this method.
     * <br>Note that if the option "ALL" is combined with the removal of only terminal moieties, even the smallest
     * attached structure will prevent the removal of a sugar. The most important consequence is that circular sugars
     * with any hydroxy groups will not be removed because these are not considered as part of the sugar moiety.
     *
     * @param anOption the selected preservation mode option
     * @throws NullPointerException if the given option is 'null'
     */
    public void setPreservationModeSetting(PreservationModeOption anOption) throws NullPointerException {
        Objects.requireNonNull(anOption, "Given mode is 'null'.");
        this.preservationModeSetting = anOption;
        this.preservationModeThresholdSetting = this.preservationModeSetting.getDefaultThreshold();
    }

    /**
     * Sets the preservation mode threshold, i.e. the molecular weight or heavy atom count (depending on the currently
     * set preservation mode) a substructure that gets disconnected from the molecule during the
     * removal of a sugar moiety has to reach in order to be
     * kept and not removed along with the sugar. If the preservation mode is set to "HEAVY_ATOM_COUNT", the threshold
     * is interpreted as the needed minimum number of heavy atoms and if it is set to "MOLECUAL_WEIGHT", the threshold
     * is interpreted as minimum molecular weight in Da.
     * <br>Notes: A threshold of zero can be set here but it is recommended to choose the preservation mode "ALL" instead.
     * On the other hand, if the preservation mode is set to "ALL", this threshold is automatically set to zero and this
     * method will throw an exception if a non-zero value is given.
     *
     * @param aThreshold the new threshold
     * @throws IllegalArgumentException if the preservation mode is currently set to preserve all structures or the
     * threshold is negative
     */
    public void setPreservationModeThresholdSetting(int aThreshold) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        if ((this.preservationModeSetting == PreservationModeOption.ALL) && aThreshold != 0) {
            throw new IllegalArgumentException("The mode is currently set to preserve all structures, so a nonzero threshold " +
                    "makes no sense.");
        }
        if (aThreshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        //</editor-fold>
        this.preservationModeThresholdSetting = aThreshold;
    }

    /**
     * Sets the option to only detect (and subsequently remove) circular sugars that have a sufficient number of attached,
     * exocyclic, single-bonded oxygen atoms. If this option is set, the circular sugar candidates
     * have to reach an additionally specified minimum ratio of said oxygen atoms to the number of atoms in the respective
     * ring in order to be seen as a sugar ring and being subsequently removed. See exocyclic oxygen atoms
     * to atoms in ring ratio threshold setting. If this option is re-activated, the previously set threshold is used again
     * or the default value if no custom threshold has been set before.
     *
     * @param aBoolean true, if the ratio of attached, exocyclic, single-bonded oxygen atoms to the number of atoms
     * in the candidate sugar ring should be evaluated at circular sugar detection
     */
    public void setDetectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting(boolean aBoolean) {
        this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting = aBoolean;
    }

    /**
     * Sets the minimum ratio of attached, exocyclic, single-bonded oxygen atoms to the number of atoms
     * in the candidate circular sugar structure to reach in order to be classified as a sugar moiety
     * if the number of exocyclic oxygen atoms should be evaluated.
     * <br>A ratio of e.g. 0.5 means that a six-membered candidate sugar ring needs to have at least 3 attached, exocyclic
     * single-bonded oxygen atoms in order to be classified as a circular sugar sugar.
     * <br>A zero value can be given if the option to remove only sugar rings with a sufficient number of exocyclic
     * oxygen atoms is activated, but it is recommended to turn this option of instead. In the other case, when the option is
     * turned off, this method will throw an exception if a non-zero value is passed.
     * <br>Note: The normally present oxygen atom within a sugar ring is included in the number of ring atoms. So setting
     * the threshold to 1.0 implies that at least one of the carbon atoms in the ring has two attached oxygen atoms.
     * In general, the threshold can be set to values higher than 1.0 but it does not make a lot of sense.
     *
     * @param aDouble the new ratio threshold
     * @throws IllegalArgumentException if the given number is infinite, 'NaN' or smaller than 0 or if the ratio is not
     * evaluated under the current settings and a non-zero value is passed
     */
    public void setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(double aDouble) throws IllegalArgumentException {
        //<editor-fold desc="Checks">
        //false for NaN and infinity arguments
        boolean tmpIsFinite = Double.isFinite(aDouble);
        boolean tmpIsNegative = (aDouble < 0);
        if (!tmpIsFinite || tmpIsNegative) {
            throw new IllegalArgumentException("Given double is NaN, infinite or negative.");
        }
        if (!this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting && aDouble != 0) {
            throw new IllegalArgumentException("The number of attached oxygen atoms is currently not included in the " +
                    "decision making process, so a nonzero ratio threshold makes no sense.");
        }
        //</editor-fold>
        this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting = aDouble;
    }

    /**
     * Sets the option to detect linear sugar structures that are part of a ring. This setting is important for e.g.
     * macrocycles that contain sugars or pseudosugars.
     * <br>Note that potential circular sugar candidates (here always including spiro sugar rings also) are filtered from
     * linear sugar candidates, even with this setting turned on.
     *
     * @param aBoolean true, if linear sugar structures that are part of a ring should be detected (and removed)
     */
    public void setDetectLinearSugarsInRingsSetting(boolean aBoolean) {
        this.detectLinearSugarsInRingsSetting = aBoolean;
    }

    /**
     * Sets the option to add a respective property to given atom containers that contain (or contained before
     * removal) sugar moieties. See property keys in the public constants of this class.
     *
     * @param aBoolean true, if properties should be added to the given atom containers
     */
    public void setAddPropertyToSugarContainingMoleculesSetting(boolean aBoolean) {
        this.addPropertyToSugarContainingMoleculesSetting = aBoolean;
    }

    /**
     * Sets the minimum number of carbon atoms a linear sugar candidate must have in order to be detected
     * as a sugar moiety (and subsequently be removed). This minimum is inclusive and does not affect the initial detection
     * of linear sugars. Only at the end of the algorithm, linear sugar candidates that are too small are discarded.
     * <br>Note: It is not tested whether the given minimum size is actually smaller than the set maximum size to allow
     * a user-friendly adjustment of these parameters without having to adhere to a certain order of operations.
     *
     * @param aMinSize the new minimum size (inclusive) of linear sugars detected, interpreted as carbon atom count
     * @throws IllegalArgumentException if the given size is smaller than one
     */
    public void setLinearSugarCandidateMinSizeSetting(int aMinSize) throws IllegalArgumentException {
        if (aMinSize < 1) {
            throw new IllegalArgumentException("Given minimum size is smaller than 1.");
        }
        this.linearSugarCandidateMinSizeSetting = aMinSize;
    }

    /**
     * Sets the maximum number of carbon atoms a linear sugar candidate can have in order to be detected
     * as a sugar moiety (and subsequently be removed). This maximum is inclusive and does not affect the initial detection
     * of linear sugars. Only at the end of the algorithm, linear sugar candidates that are too big are discarded.
     * <br>Note: It is not tested whether the given maximum size is actually greater than the set minimum size to allow
     * a user-friendly adjustment of these parameters without having to adhere to a certain order of operations.
     *
     * @param aMaxSize the new maximum size (inclusive) of linear sugars detected, interpreted as carbon atom count
     * @throws IllegalArgumentException if the given size is smaller than one
     */
    public void setLinearSugarCandidateMaxSizeSetting(int aMaxSize) throws IllegalArgumentException {
        if (aMaxSize < 1) {
            throw new IllegalArgumentException("Given maximum size is smaller than 1.");
        }
        this.linearSugarCandidateMaxSizeSetting = aMaxSize;
    }

    /**
     * Sets the option to include linear acidic sugar patterns in the linear sugar structures used for
     * initial detection of linear sugars in a given molecule. If the option is turned on, the linear acidic sugar
     * patterns are added to the linear sugar patterns list and can be retrieved and configured in the same way as the
     * 'normal' linear sugar patterns. If the option is turned off, they are all removed again from the linear sugar
     * patterns list.
     *
     * @param aBoolean true, if linear acidic sugar patterns should be included in the linear sugar structures used for
     * initial detection of linear sugars
     */
    public void setDetectLinearAcidicSugarsSetting(boolean aBoolean) {
        boolean tmpSettingHasChanged = !(this.detectLinearAcidicSugarsSetting == aBoolean);
        this.detectLinearAcidicSugarsSetting = aBoolean;
        if (tmpSettingHasChanged) {
            if (this.detectLinearAcidicSugarsSetting) {
                for (IAtomContainer tmpLinearAcidicSugar : this.linearAcidicSugarStructuresList) {
                    try {
                        this.addLinearSugarToPatternsList(tmpLinearAcidicSugar);
                    } catch (NullPointerException | IllegalArgumentException anException) {
                        SugarRemovalUtility.LOGGER.log(Level.FINE, anException.toString(), anException);
                    }
                }
            } else {
                for (IAtomContainer tmpLinearAcidicSugar : this.linearAcidicSugarStructuresList) {
                    try {
                        this.removeLinearSugarFromPatternsList(tmpLinearAcidicSugar);
                    } catch (NullPointerException | IllegalArgumentException anException) {
                        SugarRemovalUtility.LOGGER.log(Level.FINE, anException.toString(), anException);
                    }
                }
            }
        }
    }

    /**
     * Sets the option to include spiro rings in the initial set of detected rings considered for circular
     * sugar detection. If the option is turned on, spiro atoms connected two spiro rings will be protected if a spiro
     * sugar ring is removed. In the opposite case, spiro rings will be filtered from the set of isolated cycles detected
     * in the given molecule.
     * <br>Note for linear sugar detection: Here, the spiro rings will always be filtered along with the potential
     * circular sugar candidates.
     *
     * @param aBoolean true, if spiro rings should be detectable as circular sugars
     */
    public void setDetectSpiroRingsAsCircularSugarsSetting(boolean aBoolean) {
        this.detectSpiroRingsAsCircularSugarsSetting = aBoolean;
    }

    /**
     * Sets the option to detect potential sugar cycles with keto groups as circular sugars in circular sugar detection. The general
     * rule specified in the original algorithm description is that every potential sugar cycle with an exocyclic double
     * or triple bond is excluded from circular sugar detection. If this option is turned
     * on, an exemption to this rule is made for potential sugar cycles having keto groups. Also, the double-bound oxygen
     * atoms will then count for the number of connected oxygen atoms and the algorithm will not regard how many keto
     * groups are attached to the cycle (might be only one, might be that all connected oxygen atoms are double-bound).
     * If this option is turned off, every sugar-like cycle with an exocyclic double or triple bond will be
     * excluded from the detected circular sugars, as it is specified in the original algorithm description.
     *
     * @param aBoolean true, if circular sugars with keto groups should be detected
     */
    public void setDetectCircularSugarsWithKetoGroupsSetting(boolean aBoolean) {
        this.detectCircularSugarsWithKetoGroupsSetting = aBoolean;
    }

    /**
     * Sets all settings to their default values (see public static constants or enquire via get/is methods). This
     * includes the pattern lists for linear and circular sugars. To call this method is equivalent to using the
     * constructor of this class.
     */
    public void restoreDefaultSettings() {
        this.linearSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        this.circularSugarStructuresList = new ArrayList<>(SugarRemovalUtility.CIRCULAR_SUGARS_SMILES.length);
        this.linearAcidicSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_ACIDIC_SUGARS_SMILES.length);
        this.linearSugarPatternsList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        SmilesParser tmpSmilesParser = new SmilesParser(this.builder);
        //adding linear sugars to list
        for (String tmpSmiles : SugarRemovalUtility.LINEAR_SUGARS_SMILES) {
            try {
                this.linearSugarStructuresList.add(tmpSmilesParser.parseSmiles(tmpSmiles));
            } catch (Exception anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
        }
        //adding linear acidic sugars to list
        for (String tmpSmiles : SugarRemovalUtility.LINEAR_ACIDIC_SUGARS_SMILES) {
            try {
                this.linearAcidicSugarStructuresList.add(tmpSmilesParser.parseSmiles(tmpSmiles));
            } catch (Exception anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
        }
        Comparator<IAtomContainer> tmpComparator = new AtomContainerComparator().reversed();
        this.linearAcidicSugarStructuresList.sort(tmpComparator);
        //adding ring sugars to list
        for (String tmpSmiles : SugarRemovalUtility.CIRCULAR_SUGARS_SMILES) {
            try {
                this.circularSugarStructuresList.add(tmpSmilesParser.parseSmiles(tmpSmiles));
            } catch (Exception anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
        }
        this.circularSugarStructuresList.sort(tmpComparator);
        //parsing linear sugars into patterns and sorting (in advance)
        this.updateLinearSugarPatterns();
        this.setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(
                SugarRemovalUtility.DETECT_CIRCULAR_SUGARS_ONLY_WITH_O_GLYCOSIDIC_BOND_DEFAULT);
        this.setRemoveOnlyTerminalSugarsSetting(SugarRemovalUtility.REMOVE_ONLY_TERMINAL_SUGARS_DEFAULT);
        this.setPreservationModeSetting(SugarRemovalUtility.PRESERVATION_MODE_DEFAULT);
        this.setPreservationModeThresholdSetting(this.preservationModeSetting.getDefaultThreshold());
        this.setDetectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting(
                SugarRemovalUtility.DETECT_CIRCULAR_SUGARS_ONLY_WITH_ENOUGH_EXOCYCLIC_OXYGEN_ATOMS_DEFAULT);
        this.setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(
                SugarRemovalUtility.EXOCYCLIC_OXYGEN_ATOMS_TO_ATOMS_IN_RING_RATIO_THRESHOLD_DEFAULT);
        this.setDetectLinearSugarsInRingsSetting(SugarRemovalUtility.DETECT_LINEAR_SUGARS_IN_RINGS_DEFAULT);
        this.setAddPropertyToSugarContainingMoleculesSetting(
                SugarRemovalUtility.ADD_PROPERTY_TO_SUGAR_CONTAINING_MOLECULES_DEFAULT);
        this.setLinearSugarCandidateMinSizeSetting(SugarRemovalUtility.LINEAR_SUGAR_CANDIDATE_MIN_SIZE_DEFAULT);
        this.setLinearSugarCandidateMaxSizeSetting(SugarRemovalUtility.LINEAR_SUGAR_CANDIDATE_MAX_SIZE_DEFAULT);
        this.setDetectLinearAcidicSugarsSetting(SugarRemovalUtility.DETECT_LINEAR_ACIDIC_SUGARS_DEFAULT);
        this.setDetectSpiroRingsAsCircularSugarsSetting(SugarRemovalUtility.DETECT_SPIRO_RINGS_AS_CIRCULAR_SUGARS_DEFAULT);
        this.setDetectCircularSugarsWithKetoGroupsSetting(SugarRemovalUtility.DETECT_CIRCULAR_SUGARS_WITH_KETO_GROUPS_DEFAULT);
    }
    //</editor-fold>
    //
    //<editor-fold desc="Public methods for sugar detection and removal">
    /**
     * Detects linear sugar moieties in the given molecule, according to the current settings for linear sugar detection.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, this method will return true even if only non-terminal linear sugar
     * moieties are detected.
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (linear) sugar moieties or not (in addition to the return value of this method).
     *
     * @param aMolecule the atom container to scan for the presence of linear sugar moieties
     * @return true, if the given molecule contains linear sugar moieties
     * @throws NullPointerException if the given atom container is 'null'
     */
    public boolean hasLinearSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(aMolecule);
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpSugarCandidates = this.getLinearSugarCandidates(aMolecule);
        boolean tmpContainsSugar = !tmpSugarCandidates.isEmpty();
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_LINEAR_SUGAR_PROPERTY_KEY, tmpContainsSugar);
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsSugar);
        }
        return tmpContainsSugar;
    }

    /**
     * Detects circular sugar moieties in the given molecule, according to the current settings for circular sugar detection.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, this method will return true even if only non-terminal circular sugar
     * moieties are detected.
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (circular) sugar moieties or not (in addition to the return value of this method).
     *
     * @param aMolecule the atom container to scan for the presence of circular sugar moieties
     * @return true, if the given molecule contains circular sugar moieties
     * @throws NullPointerException if the given atom container is 'null'
     */
    public boolean hasCircularSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(aMolecule);
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpSugarCandidates = this.getCircularSugarCandidates(aMolecule);
        boolean tmpContainsSugar = !tmpSugarCandidates.isEmpty();
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_CIRCULAR_SUGAR_PROPERTY_KEY, tmpContainsSugar);
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsSugar);
        }
        return tmpContainsSugar;
    }

    /**
     * Detects circular and linear sugar moieties in the given molecule, according to the current settings for sugar detection.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, this method will return true even if only non-terminal sugar
     * moieties are detected.
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (circular/linear/any kind of) sugar moieties or not (in addition to the return value of this method).
     *
     * @param aMolecule the atom container to scan for the presence of sugar moieties
     * @return true, if the given molecule contains sugar moieties of any kind (circular or linear)
     * @throws NullPointerException if the given atom container is 'null'
     */
    public boolean hasCircularOrLinearSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(aMolecule);
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpCircularSugarCandidates = this.getCircularSugarCandidates(aMolecule);
        boolean tmpContainsCircularSugar = !tmpCircularSugarCandidates.isEmpty();
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpLinearSugarCandidates = this.getLinearSugarCandidates(aMolecule);
        boolean tmpContainsLinearSugar = !tmpLinearSugarCandidates.isEmpty();
        boolean tmpContainsSugar = (tmpContainsCircularSugar || tmpContainsLinearSugar);
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsSugar);
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_CIRCULAR_SUGAR_PROPERTY_KEY, tmpContainsCircularSugar);
            aMolecule.setProperty(SugarRemovalUtility.CONTAINS_LINEAR_SUGAR_PROPERTY_KEY, tmpContainsLinearSugar);
        }
        return tmpContainsSugar;
    }

    /**
     * Tests whether the given molecule qualifies for the glycosidic bond exemption. This is true for molecules that
     * practically are single-cycle circular sugars, meaning that the molecule is empty if the sugar ring is detected and
     * removed according to the current settings. These molecules or sugar rings do not need to have a glycosidic bond in
     * order to be detected as a sugar ring if the option to only detect those circular sugars that have one is activated.
     * This exemption was introduced because these molecules do not contain any other structure to bind to via a glycosidic
     * bond.
     * <br>Note: It is checked whether the sugar ring really does not have a glycosidic bond.
     *
     * @param aMolecule the molecule to check
     * @return true, if the given molecule qualifies for the exemption (it only has one sugar cycle, is empty after its
     * removal, and does not have a glycosidic bond)
     * @throws NullPointerException if the given atom container is 'null'
     */
    public boolean isQualifiedForGlycosidicBondExemption(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        this.addUniqueIndicesToAtoms(aMolecule);
        List<IAtomContainer> tmpPotentialSugarRings = this.detectPotentialSugarCycles(aMolecule,
                this.detectSpiroRingsAsCircularSugarsSetting, this.detectCircularSugarsWithKetoGroupsSetting);
        if (tmpPotentialSugarRings.size() != 1) {
            return false;
        }
        IAtomContainer tmpPotentialSugarRing = tmpPotentialSugarRings.get(0);
        boolean tmpHasGlycosidicBond = this.hasGlycosidicBond(tmpPotentialSugarRing, aMolecule);
        boolean tmpMoleculeIsOnlyOneSugarRing = false;
        if (!tmpHasGlycosidicBond) {
            //special exemption for molecules that only consist of a sugar ring and nothing else:
            // they should also be seen as candidate even though they do not have a glycosidic bond
            // (because there is nothing to bind to)
            try {
                // this method basically checks whether the molecule is empty after removal of the one sugar ring
                tmpMoleculeIsOnlyOneSugarRing = this.isMoleculeEmptyAfterRemovalOfThisRing(tmpPotentialSugarRing, aMolecule);
            } catch (CloneNotSupportedException | IllegalArgumentException | NullPointerException anException) {
                SugarRemovalUtility.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                return false;
            }
        } else {
            return false;
        }
        return tmpMoleculeIsOnlyOneSugarRing;
    }

    /**
     * Detects circular sugar moieties in the given molecule according to the current settings for circular sugar detection
     * and returns the number of detected moieties.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, the return value of this method will include non-terminal moieties at
     * all times (and terminal ones also).
     *
     * @param aMolecule the atom container to scan for the presence of circular sugar moieties
     * @return an integer representing the number of detected circular sugar moieties in the given molecule
     * @throws NullPointerException if the given atom container is 'null'
     */
    public int getNumberOfCircularSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> tmpCircularSugarCandidates = this.getCircularSugarCandidates(aMolecule);
        int tmpSize = tmpCircularSugarCandidates.size();
        return tmpSize;
    }

    /**
     * Detects linear sugar moieties in the given molecule according to the current settings for linear sugar detection
     * and returns the number of detected moieties.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, the return value of this method will include non-terminal moieties at
     * all times (and terminal ones also).
     *
     * @param aMolecule the atom container to scan for the presence of linear sugar moieties
     * @return an integer representing the number of detected linear sugar moieties in the given molecule
     * @throws NullPointerException if the given atom container is 'null'
     */
    public int getNumberOfLinearSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> tmpLinearSugarCandidates = this.getLinearSugarCandidates(aMolecule);
        int tmpSize = tmpLinearSugarCandidates.size();
        return tmpSize;
    }

    /**
     * Detects circular and linear sugar moieties in the given molecule according to the current settings for circular
     * and linear sugar detection and returns the number of detected moieties.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, the return value of this method will include non-terminal moieties at
     * all times (and terminal ones also).
     *
     * @param aMolecule the atom container to scan for the presence of circular and linear sugar moieties
     * @return an integer representing the number of detected circular and linear sugar moieties in the given molecule
     * @throws NullPointerException if the given atom container is 'null'
     */
    public int getNumberOfCircularAndLinearSugars(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> tmpCircularSugarCandidates = this.getCircularSugarCandidates(aMolecule);
        List<IAtomContainer> tmpLinearSugarCandidates = this.getLinearSugarCandidates(aMolecule);
        int tmpSize = (tmpCircularSugarCandidates.size() + tmpLinearSugarCandidates.size());
        return tmpSize;
    }

    /**
     * Removes circular sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for circular sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected circular sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of circular sugars, an empty atom container is returned.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) circular sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that
     * @return if the given atom container should NOT be cloned, this method returns the same given atom container after the
     * sugar removal; the returned molecule may be unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar)
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public IAtomContainer removeCircularSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return aMolecule;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule = this.removeAndReturnCircularSugars(aMolecule, aShouldBeCloned).get(0);
        //May be empty and may be unconnected, based on the settings
        return tmpNewMolecule;
    }

    /**
     * Removes circular sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for circular sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected circular sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures after
     * deglycosylation, whereas in the former case, the processed structure always consists of one connected structure.
     * <br>If the given molecule consists only of circular sugars, an empty atom container is left after processing.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) circular sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular sugar moieties from
     * @return true if sugar moieties were detected and removed
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public boolean removeCircularSugars(IAtomContainer aMolecule)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        List<IAtomContainer> tmpDeglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnCircularSugars(aMolecule, false);
        boolean tmpSomethingWasRemoved = (tmpDeglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
        return tmpSomethingWasRemoved;
    }

    /**
     * Removes circular sugar moieties from the given atom container and returns the resulting aglycon (at list index 0)
     * and the removed circular sugar moieties. Which substructures are removed depends on the
     * settings for circular sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected circular sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycon at list index 0) may consist of two or more
     * disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of circular sugars, an empty atom container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties while all valences on the aglycon at position 0 are saturated.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container at list index 0
     * specifying whether it contains (or contained before removal) circular sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that; if true, the deglycosylated clone is returned at list index 0
     * @return a list of atom container objects representing the deglycosylated molecule at list index 0 and the removed
     * circular sugar moieties at the remaining list positions. If the given atom container should NOT be cloned, the same
     * given atom container object after sugar removal is returned at list index 0; the returned aglycon may be
     * unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar); the returned sugar
     * moieties that were removed from the molecule have invalid valences at atoms formerly bonded to the molecule
     * core or to other sugar moieties while all valences on the aglycon at position 0 are saturated
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public List<IAtomContainer> removeAndReturnCircularSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            List<IAtomContainer> tmpReturnList = new ArrayList<>(1);
            tmpReturnList.add(0, aMolecule);
            return tmpReturnList;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule;
        if (aShouldBeCloned) {
            tmpNewMolecule = aMolecule.clone();
        } else {
            tmpNewMolecule = aMolecule;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(tmpNewMolecule);
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpSugarCandidates = this.getCircularSugarCandidates(tmpNewMolecule);
        /*note: this means that there are matches of the circular sugar patterns and that they adhere to most of
        the given settings. The exception is that they might not be terminal*/
        boolean tmpContainsSugar = !tmpSugarCandidates.isEmpty();
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_CIRCULAR_SUGAR_PROPERTY_KEY, tmpContainsSugar);
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsSugar);
        }
        List<IAtomContainer> tmpResultList = new ArrayList<>(tmpSugarCandidates.size() + 1);
        tmpResultList.add(0, tmpNewMolecule);
        if (tmpContainsSugar) {
            //throws NullPointerException and IllegalArgumentException
            tmpResultList.addAll(1, this.removeSugarCandidates(tmpNewMolecule, tmpSugarCandidates));
        }
        //the molecule at index 0 may be empty and may be unconnected, based on the settings
        return tmpResultList;
    }

    /**
     * Removes linear sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected linear sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the linear sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of linear sugars, an empty atom container is returned.
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) linear sugar moieties or not.
     *
     * @param aMolecule the molecule to remove linear sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that
     * @return if the given atom container should NOT be cloned, this method returns the same given atom container after the
     * sugar removal; the returned molecule may be unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar)
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public IAtomContainer removeLinearSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return aMolecule;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule = this.removeAndReturnLinearSugars(aMolecule, aShouldBeCloned).get(0);
        //the molecule at index 0 may be empty and may be unconnected, based on the settings
        return tmpNewMolecule;
    }

    /**
     * Removes linear sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected linear sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the linear sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures after
     * deglycosylation, whereas in the former case, the processed structure always consists of one connected structure.
     * <br>If the given molecule consists only of linear sugars, an empty atom container is left after processing.
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) linear sugar moieties or not.
     *
     * @param aMolecule the molecule to remove linear sugar moieties from
     * @return true if sugar moieties were detected and removed
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public boolean removeLinearSugars(IAtomContainer aMolecule)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        List<IAtomContainer> tmpDeglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnLinearSugars(aMolecule, false);
        boolean tmpSomethingWasRemoved = (tmpDeglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
        return tmpSomethingWasRemoved;
    }

    /**
     * Removes linear sugar moieties from the given atom container and returns the resulting aglycon (at list index 0)
     * and the removed linear sugar moieties. Which substructures are removed depends on the
     * settings for linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected linear sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the set preservation mode option and the set threshold and is cleared away.
     * <br>If all the linear sugar moieties are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycon at list index 0) may consist of two or more
     * disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of linear sugars, an empty atom container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties while all valences on the aglycon at position 0 are saturated.
     * <br>If the respective option is set, a property will be added to the given atom container at list index 0
     * specifying whether it contains (or contained before removal) linear sugar moieties or not.
     *
     * @param aMolecule the molecule to remove linear sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that; if true, the deglycosylated clone is returned at list index 0
     * @return a list of atom container objects representing the deglycosylated molecule at list index 0 and the removed
     * linear sugar moieties at the remaining list positions. If the given atom container should NOT be cloned, the same
     * given atom container object after sugar removal is returned at list index 0; the returned aglycon may be
     * unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar); the returned sugar
     * moieties that were removed from the molecule have invalid valences at atoms formerly bonded to the molecule
     * core or to other sugar moieties while all valences on the aglycon at position 0 are saturated
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public List<IAtomContainer> removeAndReturnLinearSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            List<IAtomContainer> tmpReturnList = new ArrayList<>(1);
            tmpReturnList.add(0, aMolecule);
            return tmpReturnList;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule;
        if (aShouldBeCloned) {
            tmpNewMolecule = aMolecule.clone();
        } else {
            tmpNewMolecule = aMolecule;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(tmpNewMolecule);
        //throws NullPointerException if molecule is null
        List<IAtomContainer> tmpSugarCandidates = this.getLinearSugarCandidates(tmpNewMolecule);
        /*note: this means that there are matches of the linear sugar patterns and that they adhere to most of
        the given settings. The exception is that they might not be terminal*/
        boolean tmpContainsSugar = !tmpSugarCandidates.isEmpty();
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_LINEAR_SUGAR_PROPERTY_KEY, tmpContainsSugar);
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsSugar);
        }
        List<IAtomContainer> tmpResultList = new ArrayList<>(tmpSugarCandidates.size() + 1);
        tmpResultList.add(0, tmpNewMolecule);
        if (tmpContainsSugar) {
            //throws NullPointerException and IllegalArgumentException
            tmpResultList.addAll(1, this.removeSugarCandidates(tmpNewMolecule, tmpSugarCandidates));
        }
        //the molecule at index 0 may be empty and may be unconnected, based on the settings
        return tmpResultList;
    }

    /**
     * Removes circular and linear sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for circular and linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. Important note: To ensure the removal
     * also of linear sugars that only become terminal after removing one or more terminal circular sugar and
     * vice-versa, multiple iterations of circular and linear sugar detection and removal are done here. Therefore, this
     * method might in special cases return another aglycon (the 'true' aglycon) than e.g. a subsequent call to the methods
     * for separate circular and linear sugar removal.
     * <br>If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular and linear sugars are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of sugars, an empty atom container is returned.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) circular/linear/any kind of sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular and linear sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that
     * @return if the given atom container should NOT be cloned, this method returns the same given atom container after the
     * sugar removal; the returned molecule may be unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar)
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public IAtomContainer removeCircularAndLinearSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return aMolecule;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule = this.removeAndReturnCircularAndLinearSugars(aMolecule, aShouldBeCloned).get(0);
        //May be empty and may be unconnected, based on the settings
        return tmpNewMolecule;
    }

    /**
     * Removes circular and linear sugar moieties from the given atom container. Which substructures are removed depends on the
     * settings for circular and linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. Important note: To ensure the removal
     * also of linear sugars that only become terminal after removing one or more terminal circular sugar and
     * vice-versa, multiple iterations of circular and linear sugar detection and removal are done here. Therefore, this
     * method might in special cases return another aglycon (the 'true' aglycon) than e.g. a subsequent call to the methods
     * for separate circular and linear sugar removal.
     * <br>If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular and linear sugars are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of sugars, an empty atom container is returned.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container specifying whether
     * it contains (or contained before removal) circular/linear/any kind of sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular and linear sugar moieties from
     * @return the same given atom container after the
     * sugar removal; the returned molecule may be unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar)
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public boolean removeCircularAndLinearSugars(IAtomContainer aMolecule)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return false;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        List<IAtomContainer> tmpDeglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnCircularAndLinearSugars(aMolecule, false);
        boolean tmpSomethingWasRemoved = (tmpDeglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
        return tmpSomethingWasRemoved;
    }

    /**
     * Removes circular and linear sugar moieties from the given atom container and returns the resulting aglycon (at list index 0)
     * and the removed sugar moieties. Which substructures are removed depends on the
     * settings for circular and linear sugar detection, the setting specifying whether only terminal sugar moieties should be
     * removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. Important note: To ensure the removal
     * also of linear sugars that only become terminal after removing one or more terminal circular sugar and
     * vice-versa, multiple iterations of circular and linear sugar detection and removal are done here. Therefore, this
     * method might in special cases return another aglycon (the 'true' aglycon) than e.g. a subsequent call to the methods
     * for separate circular and linear sugar removal.
     * <br>If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular and linear sugars are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycon at list index 0) may consist of two or more
     * disconnected structures when
     * returned, whereas in the former case, the returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of sugars, an empty atom container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties while all valences on the aglycon at position 0 are saturated.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>If the respective option is set, a property will be added to the given atom container at list index 0
     * specifying whether it contains (or contained before removal) circular/linear/any kind of sugar moieties or not.
     *
     * @param aMolecule the molecule to remove circular and linear sugar moieties from
     * @param aShouldBeCloned true, if the sugar moieties should not be removed from the given atom container but a clone
     * of it should be generated and the sugars be removed from that; if true, the deglycosylated clone is returned at list index 0
     * @return a list of atom container objects representing the deglycosylated molecule at list index 0 and the removed
     * sugar moieties at the remaining list positions. If the given atom container should NOT be cloned, the same
     * given atom container object after sugar removal is returned at list index 0; the returned aglycon may be
     * unconnected if also non-terminal sugars are removed according to
     * the settings and it may be empty if the resulting structure after sugar removal was too small to preserve due to the
     * set preservation mode and the associated threshold (i.e. the molecule basically was a sugar); the returned sugar
     * moieties that were removed from the molecule have invalid valences at atoms formerly bonded to the molecule
     * core or to other sugar moieties while all valences on the aglycon at position 0 are saturated
     * @throws NullPointerException if the given atom container is 'null'
     * @throws CloneNotSupportedException if the given atom container does not allow cloning (this function is needed in
     * some steps of the algorithm)
     * @throws IllegalArgumentException if only terminal sugars should be removed but the given atom container already
     * contains multiple, unconnected structures which makes the determination of terminal and non-terminal structures
     * impossible
     */
    public List<IAtomContainer> removeAndReturnCircularAndLinearSugars(IAtomContainer aMolecule, boolean aShouldBeCloned)
            throws NullPointerException, CloneNotSupportedException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            List<IAtomContainer> tmpReturnList = new ArrayList<>(1);
            tmpReturnList.add(0, aMolecule);
            return tmpReturnList;
        }
        if (this.removeOnlyTerminalSugarsSetting) {
            boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
            if (!tmpIsConnected) {
                throw new IllegalArgumentException("Only terminal sugar moieties should be removed but the given atom" +
                        "container already contains multiple unconnected structures.");
            }
        }
        //</editor-fold>
        IAtomContainer tmpNewMolecule;
        if (aShouldBeCloned) {
            tmpNewMolecule = aMolecule.clone();
        } else {
            tmpNewMolecule = aMolecule;
        }
        //throws NullPointerException if molecule is null
        this.addUniqueIndicesToAtoms(tmpNewMolecule);
        boolean tmpContainsCircularSugars = false;
        boolean tmpContainsLinearSugars = false;
        boolean tmpContainsAnyTypeOfSugars = false;
        //note: initial capacity arbitrarily chosen
        List<IAtomContainer> tmpResultList = new ArrayList<>(tmpNewMolecule.getAtomCount() / 6);
        tmpResultList.add(0, tmpNewMolecule);
        while (true) {
            //note: this has to be done stepwise because linear and circular sugar candidates can overlap
            // (TODO: This is not true anymore, right?)
            //throws NullPointerException if molecule is null
            List<IAtomContainer> tmpCircularSugarCandidates = this.getCircularSugarCandidates(tmpNewMolecule);
            if (this.addPropertyToSugarContainingMoleculesSetting) {
                //note: this test for presence of linear sugars has to be done here because some might be detected as part
                // circular sugars, but has to be redone later to not remove anything that is not there anymore. This is
                // important to set the 'contains linear sugar' property correctly.
                // (TODO: This is not true anymore, right?)
                //throws NullPointerException if molecule is null
                List<IAtomContainer> tmpLinearSugarCandidates = this.getLinearSugarCandidates(tmpNewMolecule);
                if (!tmpLinearSugarCandidates.isEmpty() && !tmpContainsLinearSugars) {
                    tmpContainsLinearSugars = true;
                }
            }
            boolean tmpCandidateListIsNotEmpty = !tmpCircularSugarCandidates.isEmpty();
            List<IAtomContainer> tmpRemovedCircularSugarMoieties = new ArrayList<>(0);
            if (tmpCandidateListIsNotEmpty) {
                //throws NullPointerException and IllegalArgumentException
                tmpRemovedCircularSugarMoieties = this.removeSugarCandidates(tmpNewMolecule, tmpCircularSugarCandidates);
                if (!tmpContainsCircularSugars) {
                    tmpContainsCircularSugars = true;
                }
                tmpResultList.addAll(tmpRemovedCircularSugarMoieties);
            }
            //exit here if molecule is empty after removal
            if (tmpNewMolecule.isEmpty()) {
                break;
            }
            //note: if only terminal sugars are removed, the atom container should not be disconnected at this point
            // and that is a requirement for further checks for terminal linear sugar moieties
            //throws NullPointerException if molecule is null
            List<IAtomContainer> tmpLinearSugarCandidates = this.getLinearSugarCandidates(tmpNewMolecule);
            tmpCandidateListIsNotEmpty = !tmpLinearSugarCandidates.isEmpty();
            List<IAtomContainer> tmpRemovedLinearSugarMoieties = new ArrayList<>(0);
            if (tmpCandidateListIsNotEmpty) {
                //throws NullPointerException and IllegalArgumentException
                tmpRemovedLinearSugarMoieties = this.removeSugarCandidates(tmpNewMolecule, tmpLinearSugarCandidates);
                if (!tmpContainsLinearSugars) {
                    tmpContainsLinearSugars = true;
                }
                tmpResultList.addAll(tmpRemovedLinearSugarMoieties);
            }
            //exit here if molecule is empty after removal
            if (tmpNewMolecule.isEmpty()) {
                break;
            }
            if (this.removeOnlyTerminalSugarsSetting) {
                boolean tmpSomethingWasRemoved = ((!tmpRemovedCircularSugarMoieties.isEmpty())
                        || (!tmpRemovedLinearSugarMoieties.isEmpty()));
                if (!tmpSomethingWasRemoved) {
                    //if nothing was removed, the loop is broken; otherwise, there might be new terminal moieties in the
                    // next iteration
                    break;
                }
            } else {
                //if all moieties are to be removed, not only the terminal ones, one iteration is enough
                break;
            }
        }
        if (this.addPropertyToSugarContainingMoleculesSetting) {
            tmpContainsAnyTypeOfSugars = (tmpContainsCircularSugars || tmpContainsLinearSugars);
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_SUGAR_PROPERTY_KEY, tmpContainsAnyTypeOfSugars);
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_CIRCULAR_SUGAR_PROPERTY_KEY, tmpContainsCircularSugars);
            tmpNewMolecule.setProperty(SugarRemovalUtility.CONTAINS_LINEAR_SUGAR_PROPERTY_KEY, tmpContainsLinearSugars);
        }
        //The molecule at index 0 may be empty and may be unconnected, based on the settings
        return tmpResultList;
    }

    /**
     * Extracts circular sugar moieties from the given molecule, according to the current settings for circular sugar detection.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, this method will always return terminal and non-terminal moieties.
     *
     * @param aMolecule the molecule to extract circular sugar moieties from
     * @return a list of substructures in the given molecule that are regarded as circular sugar moieties
     * @throws NullPointerException if the given molecule is 'null'
     */
    public List<IAtomContainer> getCircularSugarCandidates(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        if (aMolecule.isEmpty()) {
            return new ArrayList<>(0);
        }
        boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(aMolecule);
        if (!tmpIndicesAreSet) {
            this.addUniqueIndicesToAtoms(aMolecule);
        }
        List<IAtomContainer> tmpPotentialSugarRings = this.detectPotentialSugarCycles(aMolecule,
                this.detectSpiroRingsAsCircularSugarsSetting, this.detectCircularSugarsWithKetoGroupsSetting);
        if (tmpPotentialSugarRings.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> tmpSugarCandidates = new ArrayList<>(tmpPotentialSugarRings.size());
        for(IAtomContainer tmpPotentialSugarRing : tmpPotentialSugarRings) {
            if (Objects.isNull(tmpPotentialSugarRing) || tmpPotentialSugarRing.isEmpty()) {
                continue;
            }
            /*
             * note: another requirement of a suspected sugar ring is that it contains only single bonds.
             * This is not tested here because all the structures in the reference rings do meet this criterion.
             * But a structure that does not meet this criterion could be added to the references by the user.
             */
            //do not remove rings without an attached glycosidic bond if this option is set
            if (this.detectCircularSugarsOnlyWithOGlycosidicBondSetting) {
                boolean tmpHasGlycosidicBond = this.hasGlycosidicBond(tmpPotentialSugarRing, aMolecule);
                if (!tmpHasGlycosidicBond) {
                    //special exemption for molecules that only consist of a sugar ring and nothing else:
                    // they should also be seen as candidate even though they do not have a glycosidic bond
                    // (because there is nothing to bind to)
                    //Note: There is also a public method testing this! Keep this in mind! It is not used here to not
                    // do the ring search etc. again.
                    if (tmpPotentialSugarRings.size() == 1) {
                        boolean tmpMoleculeIsOnlyOneSugarRing = false;
                        try {
                            tmpMoleculeIsOnlyOneSugarRing = this.isMoleculeEmptyAfterRemovalOfThisRing(tmpPotentialSugarRing, aMolecule);
                        } catch (CloneNotSupportedException | IllegalArgumentException | NullPointerException anException) {
                            SugarRemovalUtility.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                            //there is sth wrong here, do not add this ring to the candidates
                            continue;
                        }
                        if (!tmpMoleculeIsOnlyOneSugarRing) {
                            //isolated ring is not a candidate because it has no glycosidic bond and does not
                            // qualify for the exemption
                            continue;
                        } //else, go on investigating this candidate, even though it does not have a glycosidic bond
                    } else {
                        //not a candidate
                        continue;
                    }
                }
            }
            //do not remove rings with 'too few' attached oxygens if this option is set
            if (this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting) {
                int tmpExocyclicOxygenCount = this.getExocyclicOxygenAtomCount(tmpPotentialSugarRing, aMolecule);
                int tmpAtomsInRing = tmpPotentialSugarRing.getAtomCount();
                boolean tmpAreEnoughOxygensAttached = this.doesRingHaveEnoughExocyclicOxygenAtoms(tmpAtomsInRing,
                        tmpExocyclicOxygenCount);
                if (!tmpAreEnoughOxygensAttached) {
                    continue;
                }
            }
            //if sugar ring has not been excluded yet, the molecule contains sugars, although they might not
            // be terminal
            tmpSugarCandidates.add(tmpPotentialSugarRing);
        }
        return tmpSugarCandidates;
    }

    /**
     * Extracts linear sugar moieties from the given molecule, according to the current settings for linear sugar detection.
     * It is not influenced by the setting specifying whether only terminal sugar moieties should be removed and
     * not by the set preservation mode. Therefore, this method will always return terminal and non-terminal moieties.
     *
     * @param aMolecule the molecule to extract linear sugar moieties from
     * @return a list of substructures in the given molecule that are regarded as linear sugar moieties
     * @throws NullPointerException if the given molecule is 'null'
     */
    public List<IAtomContainer> getLinearSugarCandidates(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        if (aMolecule.isEmpty()) {
            return new ArrayList<IAtomContainer>(0);
        }
        boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(aMolecule);
        if (!tmpIndicesAreSet) {
            this.addUniqueIndicesToAtoms(aMolecule);
        }
        List<IAtomContainer> tmpSugarCandidates = this.detectLinearSugarCandidatesByPatternMatching(aMolecule);
        //alternative ideas: SMARTS or Ertl or matching the biggest patterns first and exclude the matched atoms
        if (!tmpSugarCandidates.isEmpty()) {

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);

            tmpSugarCandidates = this.combineOverlappingCandidates(tmpSugarCandidates);
            //alternative: tmpSugarCandidates = this.splitOverlappingCandidatesPseudoRandomly(tmpSugarCandidates);

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);

            tmpSugarCandidates = this.splitEtherEsterAndPeroxideBonds(tmpSugarCandidates);

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);

            this.removeAtomsOfCircularSugarsFromCandidates(tmpSugarCandidates, aMolecule);
            //alternative: this.removeCandidatesContainingCircularSugars(tmpSugarCandidates, aMolecule);
            //alternative: this.removeCircularSugarsFromCandidates(tmpSugarCandidates, aMolecule);

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);

        }
        if (!this.detectLinearSugarsInRingsSetting && !tmpSugarCandidates.isEmpty()) {
            this.removeCyclicAtomsFromSugarCandidates(tmpSugarCandidates, aMolecule);
            //alternative: this.removeSugarCandidatesWithCyclicAtoms(tmpSugarCandidates, aMolecule);

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);
        }
        if (!tmpSugarCandidates.isEmpty()) {
            tmpSugarCandidates = this.removeTooSmallAndTooLargeCandidates(tmpSugarCandidates);

            //*Debugging*
            //this.printAllMolsAsSmiles(tmpSugarCandidates);
        }
        return tmpSugarCandidates;
    }

    /*
    NOTE: The getCircularAndLinearSugarCandidates() method one would expect at this point, is not here because
    the atom containers in the list (= combination of circular and linear candidates) can overlap (= multiple
    candidates share the same atoms or bonds). This can lead to unsafe operations if one e.g. tries to remove all
    these sugar moieties in one go. Therefore, this method is not offered.
    (TODO: This is not true anymore, right?)
    */

    /**
     * Removes all unconnected fragments that are too small to keep according to the current preservation mode and
     * threshold setting. If all structures are too small, an empty atom container is returned.
     * <br>This does not guarantee that the resulting atom container consists of only one connected structure. There might
     * be multiple unconnected structures that are big enough to be preserved.
     *
     * @param aMolecule the molecule to clean up; it might be empty after this method call but not null
     * @throws NullPointerException if the given molecule is 'null'
     */
    public void removeTooSmallDisconnectedStructures(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return;
        }
        if (this.preservationModeSetting == PreservationModeOption.ALL) {
            return;
        }
        IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(aMolecule);
        for (int i = 0; i < tmpComponents.getAtomContainerCount(); i++) {
            IAtomContainer tmpComponent = tmpComponents.getAtomContainer(i);
            //May throw UnsupportedOperationException if a new PreservationModeOption constant has been added but not implemented
            // in this method yet. Since this is a serious issue, the code is supposed to crash.
            boolean tmpIsTooSmall = this.isTooSmallToPreserve(tmpComponent);
            if (tmpIsTooSmall) {
                //note: careful with removing things from sets/lists while iterating over it! But here it is ok because elements
                // are not removed from the same set that is iterated
                for (IAtom tmpAtom : tmpComponent.atoms()) {
                    //check to avoid exceptions
                    if (aMolecule.contains(tmpAtom)) {
                        aMolecule.removeAtom(tmpAtom);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the given molecule or structure is too small to be kept according to the current preservation mode
     * and threshold setting.
     *
     * @param aMolecule the molecule to check
     * @return true, if the given structure is too small to be preserved
     * @throws NullPointerException if the given molecule is 'null'
     * @throws UnsupportedOperationException if an unknown PreservationModeOption enum constant is set
     */
    public boolean isTooSmallToPreserve(IAtomContainer aMolecule) throws NullPointerException, UnsupportedOperationException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return true;
        }
        boolean tmpIsTooSmall;
        if (this.preservationModeSetting == PreservationModeOption.ALL) {
            tmpIsTooSmall = false;
        } else if (this.preservationModeSetting == PreservationModeOption.HEAVY_ATOM_COUNT) {
            int tmpHeavyAtomCount = AtomContainerManipulator.getHeavyAtoms(aMolecule).size();
            tmpIsTooSmall = tmpHeavyAtomCount < this.preservationModeThresholdSetting;
        } else if (this.preservationModeSetting == PreservationModeOption.MOLECULAR_WEIGHT) {
            double tmpMolWeight = AtomContainerManipulator.getMass(aMolecule, AtomContainerManipulator.MolWeight);
            tmpIsTooSmall = tmpMolWeight < this.preservationModeThresholdSetting;
        } else {
            throw new UnsupportedOperationException("Undefined PreservationModeOption setting!");
        }
        return tmpIsTooSmall;
    }

    /**
     * Checks whether the given substructure is terminal (i.e. it can be removed without producing multiple unconnected
     * structures in the remaining molecule) in the given parent molecule. To do this, the substructure and
     * the parent molecule are cloned, the substructure is removed in the parent molecule clone and finally it is
     * checked whether the parent molecule clone still consists of only one connected structure. If that is the case,
     * the substructure is terminal. If the preservation mode is not set to 'preserve all structures', too small
     * resulting fragments are cleared from the parent clone in between. These structures that are too small must also
     * not be part of any other substructure in the given candidate list to avoid removing parts of other sugar candidates.
     * <br>Note: This method only detects moieties that are immediately terminal. It will not deem terminal a sugar
     * moiety that only becomes terminal after the removal of another sugar moiety, for example.
     *
     * @param aSubstructure the substructure to check for whether it is terminal
     * @param aParentMolecule the molecule the substructure is a part of
     * @param aCandidateList a list containing the detected sugar candidates to check whether atoms of other candidates
     *                       would be cleared away if the given substructure was removed (which has to be avoided)
     * @return true, if the substructure is terminal
     * @throws NullPointerException if any parameter is 'null'
     * @throws IllegalArgumentException if the substructure is not part of the parent molecule or if the parent molecule
     * is already unconnected (i.e. consists of multiple, unconnected substructures)
     * @throws CloneNotSupportedException if one of the atom containers cannot be cloned
     */
    public boolean isTerminal(IAtomContainer aSubstructure,
                               IAtomContainer aParentMolecule,
                               List<IAtomContainer> aCandidateList)
            throws NullPointerException, IllegalArgumentException, CloneNotSupportedException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aSubstructure, "Given substructure is 'null'.");
        Objects.requireNonNull(aParentMolecule, "Given parent molecule is 'null'.");
        Objects.requireNonNull(aCandidateList, "Given list of candidates is 'null'.");
        boolean tmpIsParent = true;
        for (IAtom tmpAtom : aSubstructure.atoms()) {
            if (!aParentMolecule.contains(tmpAtom)) {
                tmpIsParent = false;
                break;
            }
        }
        if (!tmpIsParent) {
            throw new IllegalArgumentException("Given substructure is not part of the given parent molecule.");
        }
        boolean tmpIsUnconnected = !ConnectivityChecker.isConnected(aParentMolecule);
        if (tmpIsUnconnected) {
            throw new IllegalArgumentException("Parent molecule is already unconnected.");
        }
        boolean tmpIndicesAreSetInParent = this.checkUniqueIndicesOfAtoms(aParentMolecule);
        if (!tmpIndicesAreSetInParent) {
            this.addUniqueIndicesToAtoms(aParentMolecule);
        }
        boolean tmpIndicesAreSetInChild = this.checkUniqueIndicesOfAtoms(aSubstructure);
        if (!tmpIndicesAreSetInChild) {
            this.addUniqueIndicesToAtoms(aSubstructure);
        }
        for (IAtomContainer tmpCandidate : aCandidateList) {
            boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(tmpCandidate);
            if (!tmpIndicesAreSet) {
                this.addUniqueIndicesToAtoms(tmpCandidate);
            }
        }
        //</editor-fold>
        boolean tmpIsTerminal;
        IAtomContainer tmpMoleculeClone = aParentMolecule.clone();
        IAtomContainer tmpSubstructureClone = aSubstructure.clone();
        float tmpLoadFactor = 0.75f;
        int tmpIndexToAtomMapInitCapacity = (int)((float) tmpMoleculeClone.getAtomCount() * (1.0f / tmpLoadFactor) + 2.0f);
        HashMap<Integer, IAtom> tmpIndexToAtomMap = new HashMap<>(tmpIndexToAtomMapInitCapacity, tmpLoadFactor);
        for (IAtom tmpAtom : tmpMoleculeClone.atoms()) {
            tmpIndexToAtomMap.put((Integer)tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), tmpAtom);
        }
        for (IAtom tmpAtom : tmpSubstructureClone.atoms()) {
            tmpMoleculeClone.removeAtom(tmpIndexToAtomMap.get(tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY)));
        }
        boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpMoleculeClone);
        if (this.preservationModeSetting == PreservationModeOption.ALL) {
            tmpIsTerminal = tmpIsConnected;
        } else {
            if (tmpIsConnected) {
                tmpIsTerminal = true;
            } else {
                IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpMoleculeClone);
                int tmpAtomIndicesThatArePartOfSugarCandidatesSetInitCapacity = (int)((float) aParentMolecule.getAtomCount() * (1.0f / tmpLoadFactor) + 2.0f);
                HashSet<Integer> tmpAtomIndicesThatArePartOfSugarCandidatesSet = new HashSet<>(
                        tmpAtomIndicesThatArePartOfSugarCandidatesSetInitCapacity,
                        tmpLoadFactor);
                for (IAtomContainer tmpCandidate : aCandidateList) {
                    for (IAtom tmpAtom : tmpCandidate.atoms()) {
                        int tmpIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        tmpAtomIndicesThatArePartOfSugarCandidatesSet.add(tmpIndex);
                    }
                }
                for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                    if (Objects.isNull(tmpComponent) || tmpComponent.isEmpty()) {
                        continue;
                    }
                    //May throw UnsupportedOperationException if a new PreservationModeOption constant has been added but not implemented
                    // in this method yet. Since this is a serious issue, the code is supposed to crash.
                    //throws NullPointerException if molecule is null
                    boolean tmpIsTooSmall = this.isTooSmallToPreserve(tmpComponent);
                    boolean tmpIsPartOfSugarCandidate = false;
                    for (IAtom tmpAtom : tmpComponent.atoms()) {
                        int tmpIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        if (tmpAtomIndicesThatArePartOfSugarCandidatesSet.contains(tmpIndex)) {
                            tmpIsPartOfSugarCandidate = true;
                            break;
                        }
                    }
                    if (tmpIsTooSmall && !tmpIsPartOfSugarCandidate) {
                        //note: no check whether the clone actually contains the component
                        tmpMoleculeClone.remove(tmpComponent);
                    }
                }
                tmpIsTerminal = ConnectivityChecker.isConnected(tmpMoleculeClone);
            }
        }
        return tmpIsTerminal;
    }

    /**
     * Removes the given sugar moieties (or substructures in general) from the given molecule and returns the removed
     * moieties (not the aglycon!). The removal algorithm is the same for linear and circular sugars. The only settings
     * influencing the
     * removal are the option specifying whether to remove only terminal sugar moieties and the set preservation mode
     * (because it influences the determination of terminal vs. non-terminal).
     * <br>If only terminal sugar moieties are to be removed, the sugar candidates are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration starts anew after iterating over all
     * candidates and stops if no terminal sugar was removed in one whole iteration. If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a removal step must be too small to
     * keep according to the preservation mode option and the set threshold and is cleared away.
     * <br>If all the sugars are to be removed from the query molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or more disconnected structures after this
     * method call, whereas in the former case, the remaining structure always consists of one connected structure.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another cycle are preserved (if labelled by the
     * respective property).
     * <br>Note that the deglycosylated core is not returned as part of the given list in this method.
     *
     * @param aMolecule the molecule to remove the sugar candidates from
     * @param aCandidateList the list of sugar moieties in the given molecule
     * @return a list of atom container objects representing the removed sugar moieties; the returned sugar
     * moieties that were removed from the molecule have invalid valences at atoms formerly bonded to the molecule
     * core or to other sugar moieties while all valences on the aglycon (not in the list!) are saturated
     * @throws NullPointerException if any parameter is 'null'
     * @throws IllegalArgumentException if at least one atom in the candidate list is not actually part of the molecule
     * or if it cannot be cloned to determine whether it is terminal (if only terminal moieties are removed according
     * to the current settings)
     */
    public List<IAtomContainer> removeSugarCandidates(IAtomContainer aMolecule, List<IAtomContainer> aCandidateList)
            throws NullPointerException, IllegalArgumentException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty() || aMolecule.isEmpty()) {
            return new ArrayList<IAtomContainer>(0);
        }
        for (IAtomContainer tmpSubstructure : aCandidateList) {
            boolean tmpIsParent = true;
            if (Objects.isNull(tmpSubstructure) || tmpSubstructure.isEmpty()) {
                continue;
            }
            for (IAtom tmpAtom : tmpSubstructure.atoms()) {
                if (!aMolecule.contains(tmpAtom)) {
                    tmpIsParent = false;
                    break;
                }
            }
            if (!tmpIsParent) {
                throw new IllegalArgumentException("At least one of the possible sugar-like substructures is not " +
                        "actually part of the given molecule.");
            }
        }
        //</editor-fold>
        // a copy of the list is needed to avoid iterating over the same elements again if only terminal moieties are removed
        List<IAtomContainer> tmpSugarCandidates = new ArrayList(aCandidateList);
        // the to be returned list of removed moieties
        List<IAtomContainer> tmpRemovedSugarMoieties = new ArrayList<>(aCandidateList.size());
        if (this.removeOnlyTerminalSugarsSetting) {
            //Only terminal sugars should be removed
            //but the definition of terminal depends on the set preservation mode!
            //decisions based on this setting are made in the respective private method
            //No unconnected structures result at the end or at an intermediate step
            boolean tmpContainsNoTerminalSugar = false;
            while (!tmpContainsNoTerminalSugar) {
                boolean tmpSomethingWasRemoved = false;
                for (int i = 0; i < tmpSugarCandidates.size(); i++) {
                    IAtomContainer tmpCandidate = tmpSugarCandidates.get(i);
                    if (Objects.isNull(tmpCandidate) || tmpCandidate.isEmpty()) {
                        continue;
                    }
                    boolean tmpIsTerminal = false;
                    try {
                        //also throws NullPointerExceptions or IllegalArgumentExceptions but they are simply passed on
                        // by this calling method
                        tmpIsTerminal = this.isTerminal(tmpCandidate, aMolecule, tmpSugarCandidates);
                    } catch (CloneNotSupportedException aCloneNotSupportedException) {
                        SugarRemovalUtility.LOGGER.log(Level.WARNING, aCloneNotSupportedException.toString(),
                                aCloneNotSupportedException);
                        throw new IllegalArgumentException("Could not clone one candidate sugar structure and therefore " +
                                "not determine whether it is terminal or not.");
                    }
                    if (tmpIsTerminal) {
                        for (IAtom tmpAtom : tmpCandidate.atoms()) {
                            if (aMolecule.contains(tmpAtom)) {
                                Boolean tmpAtomIsSpiroAtom = tmpAtom.getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY);
                                if (!Objects.isNull(tmpAtomIsSpiroAtom)) {
                                    if (tmpAtomIsSpiroAtom) {
                                        //here, one of the spiro cycles is removed; therefore, the atom is not spiro anymore
                                        tmpAtom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, false);
                                        continue;
                                    }
                                }
                                aMolecule.removeAtom(tmpAtom);
                            }
                        }
                        tmpRemovedSugarMoieties.add(tmpCandidate);
                        tmpSugarCandidates.remove(i);
                        //The removal shifts the remaining indices!
                        i = i - 1;
                        if (!aMolecule.isEmpty()) {
                            //to clear away leftover unconnected fragments that are not to be kept due to the settings and
                            // to generate valid valences by adding implicit hydrogen atoms
                            //throws NullPointerException if molecule is null
                            this.postProcessAfterRemoval(aMolecule);
                        }
                        //atom container may be empty after that
                        if (aMolecule.isEmpty()) {
                            tmpContainsNoTerminalSugar = true;
                            break;
                        }
                        tmpSomethingWasRemoved = true;
                    }
                }
                if (!tmpSomethingWasRemoved) {
                    tmpContainsNoTerminalSugar = true;
                }
            }
        } else {
            //all sugar moieties are removed, may result in an unconnected atom container
            for (IAtomContainer tmpSugarCandidate : tmpSugarCandidates) {
                for (IAtom tmpAtom : tmpSugarCandidate.atoms()) {
                    if (aMolecule.contains(tmpAtom)) {
                        Boolean tmpAtomIsSpiroAtom = tmpAtom.getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY);
                        if (!Objects.isNull(tmpAtomIsSpiroAtom)) {
                            if (tmpAtomIsSpiroAtom) {
                                //here, one of the spiro cycles is removed; therefore, the atom is not spiro anymore
                                tmpAtom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, false);
                                continue;
                            }
                        }
                        aMolecule.removeAtom(tmpAtom);
                    }
                }
                tmpRemovedSugarMoieties.add(tmpSugarCandidate);
            }
        }
        if (!aMolecule.isEmpty()) {
            //to clear away leftover unconnected fragments that are not to be kept due to the settings and
            // to generate valid valences by adding implicit hydrogen atoms
            //throws NullPointerException if molecule is null
            this.postProcessAfterRemoval(aMolecule);
        }
        return tmpRemovedSugarMoieties;
    }

    /**
     * Clears away too small structures (according to the set preservation mode) from the given molecule. It may result
     * in an empty atom container. Also, valid valences on the remaining molecule are generated by the addition of
     * implicit hydrogen atoms to open valences.
     * <br>Note: This method does not check whether a removed disconnected structure is part of a sugar candidate
     * because in the case where only terminal structures are removed, this is checked elsewhere and in the case where
     * all sugar candidates are removed, this method is not called in-between the removal steps.
     *
     * @param aMolecule the molecule to post-process; might be empty after this method call
     * @throws NullPointerException if the given molecule is 'null'
     */
    public void postProcessAfterRemoval(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        if (aMolecule.isEmpty()) {
            return;
        }
        //if too small, unconnected structures should be discarded, this is done now
        //otherwise, the possibly unconnected atom container is returned
        //Even if only terminal sugars are removed, the resulting, connected structure may still be too small to preserve!
        if (this.preservationModeSetting != PreservationModeOption.ALL) {
            //throws NullPointerException if molecule is null
            this.removeTooSmallDisconnectedStructures(aMolecule);
        }
        if (!aMolecule.isEmpty()) {
            try {
                AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(aMolecule);
                CDKHydrogenAdder.getInstance(this.builder).addImplicitHydrogens(aMolecule);
            } catch (CDKException aCDKException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
            }
        }
    }
    //</editor-fold>
    //
    //<editor-fold desc="Public static methods">
    /**
     * Utility method that can be used to select the 'biggest' (i.e. the one with the highest heavy atom count) structure
     * from an atom container containing multiple unconnected structures, e.g. after the removal of both terminal and
     * non-terminal sugar moieties.
     * <br>The properties of the given atom container (IAtomContainer.getProperties()) are transferred to the returned
     * atom container.
     * <br>Note: This method does not clear away structures that are too small. It is independent of all settings.
     *
     * @param aMolecule the molecule to select the biggest structure from out of multiple unconnected structures
     * @return the biggest structure
     * @throws NullPointerException if the given atom container is 'null' or the CDK ConnectivityChecker is unable
     * to determine the unconnected structures
     */
    public static IAtomContainer selectBiggestUnconnectedFragment(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return aMolecule;
        }
        boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
        if (tmpIsConnected) {
            return aMolecule;
        }
        Map<Object, Object> tmpProperties = aMolecule.getProperties();
        IAtomContainerSet tmpUnconnectedFragments = ConnectivityChecker.partitionIntoMolecules(aMolecule);
        IAtomContainer tmpBiggestFragment;
        if(tmpUnconnectedFragments != null && tmpUnconnectedFragments.getAtomContainerCount() >= 1) {
            tmpBiggestFragment = tmpUnconnectedFragments.getAtomContainer(0);
            int tmpBiggestFragmentHeavyAtomCount = AtomContainerManipulator.getHeavyAtoms(tmpBiggestFragment).size();
            for(IAtomContainer tmpFragment : tmpUnconnectedFragments.atomContainers()){
                int tmpFragmentHeavyAtomCount = AtomContainerManipulator.getHeavyAtoms(tmpFragment).size();
                if(tmpFragmentHeavyAtomCount > tmpBiggestFragmentHeavyAtomCount){
                    tmpBiggestFragment = tmpFragment;
                    tmpBiggestFragmentHeavyAtomCount = tmpFragmentHeavyAtomCount;
                }
            }
        } else {
            throw new NullPointerException("Could not detect the unconnected structures of the given atom container.");
        }
        tmpBiggestFragment.setProperties(tmpProperties);
        return tmpBiggestFragment;
    }

    /**
     * Utility method that can be used to select the 'heaviest' (i.e. the one with the highest molecular weight) structure
     * from an atom container containing multiple unconnected structures, e.g. after the removal of both terminal and
     * non-terminal sugar moieties.
     * <br>The properties of the given atom container (IAtomContainer.getProperties()) are transferred to the returned
     * atom container.
     * <br>Note: This method does not clear away structures that are too small. It is independent of all settings.
     *
     * @param aMolecule the molecule to select the heaviest structure from out of multiple unconnected structures
     * @return the heaviest structure
     * @throws NullPointerException if the given atom container is 'null' or the CDK ConnectivityChecker is unable
     * to determine the unconnected structures
     */
    public static IAtomContainer selectHeaviestUnconnectedFragment(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return aMolecule;
        }
        boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
        if (tmpIsConnected) {
            return aMolecule;
        }
        Map<Object, Object> tmpProperties = aMolecule.getProperties();
        IAtomContainerSet tmpUnconnectedFragments = ConnectivityChecker.partitionIntoMolecules(aMolecule);
        IAtomContainer tmpHeaviestFragment;
        if(tmpUnconnectedFragments != null && tmpUnconnectedFragments.getAtomContainerCount() >= 1) {
            tmpHeaviestFragment = tmpUnconnectedFragments.getAtomContainer(0);
            double tmpHeaviestFragmentWeight = AtomContainerManipulator.getMass(tmpHeaviestFragment);
            for(IAtomContainer tmpFragment : tmpUnconnectedFragments.atomContainers()){
                double tmpFragmentWeight = AtomContainerManipulator.getMass(tmpFragment);
                if(tmpFragmentWeight > tmpHeaviestFragmentWeight){
                    tmpHeaviestFragment = tmpFragment;
                    tmpHeaviestFragmentWeight = tmpFragmentWeight;
                }
            }
        } else {
            //if something went wrong
            return null;
        }
        tmpHeaviestFragment.setProperties(tmpProperties);
        return tmpHeaviestFragment;
    }

    /**
     * Utility method that can be used to partition the unconnected structures in an atom container, e.g. after the removal
     * of both terminal and non-terminal sugar moieties, into a list of separate atom container objects and sort this
     * list in decreasing order with the following criteria with decreasing priority: atom count, molecular weight, bond
     * count and sum of bond orders.
     * <br>Note: This method does not clear away structures that are too small. It is independent of all settings.
     *
     * @param aMolecule the molecule whose unconnected structures to separate and sort
     * @return list of sorted atom containers representing the unconnected structures of the given molecule
     * @throws NullPointerException if the given atom container is 'null'
     */
    public static List<IAtomContainer> partitionAndSortUnconnectedFragments(IAtomContainer aMolecule)
            throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        boolean tmpIsEmpty = aMolecule.isEmpty();
        boolean tmpIsConnected = ConnectivityChecker.isConnected(aMolecule);
        if (tmpIsConnected || tmpIsEmpty) {
            ArrayList<IAtomContainer> tmpFragmentList = new ArrayList<>(1);
            tmpFragmentList.add(aMolecule);
            return tmpFragmentList;
        }
        IAtomContainerSet tmpUnconnectedFragments = ConnectivityChecker.partitionIntoMolecules(aMolecule);
        int tmpSize = tmpUnconnectedFragments.getAtomContainerCount();
        ArrayList<IAtomContainer> tmpSortedList = new ArrayList<>(tmpSize);
        for (IAtomContainer tmpFragment : tmpUnconnectedFragments.atomContainers()) {
            tmpSortedList.add(tmpFragment);
        }
        /*Compares two IAtomContainers for order with the following criteria with decreasing priority:
            Compare atom count
            Compare molecular weight (heavy atoms only)
            Compare bond count
            Compare sum of bond orders (heavy atoms only)
        If no difference can be found with the above criteria, the IAtomContainers are considered equal.*/
        Comparator<IAtomContainer> tmpComparator = new AtomContainerComparator().reversed();
        //note: this can throw various exceptions but they should not appear here
        tmpSortedList.sort(tmpComparator);
        return tmpSortedList;
    }
    //</editor-fold>
    //
    //<editor-fold desc="Protected methods">
    //<editor-fold desc="General processing">
    /**
     * Adds an index as property to all atom objects of the given atom container to identify them uniquely within the
     * atom container and its clones. This is required e.g. for the determination of terminal vs. non-terminal sugar
     * moieties.
     *
     * @param aMolecule the molecule that will be processed by the class
     * @throws NullPointerException if molecule is 'null'
     */
    protected void addUniqueIndicesToAtoms(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return;
        }
        for (int i = 0; i < aMolecule.getAtomCount(); i++) {
            IAtom tmpAtom = aMolecule.getAtom(i);
            tmpAtom.setProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY, i);
        }
    }

    /**
     * Creates an identifier string for every substructures in the given list, based on the unique indices of the
     * included atoms, respectively, and returns a set of the generated ids.
     * It is only encoded which atoms are part of the respective substructure, no bond information etc. Used for a quick
     * matching of substructures in the same molecule. The unique indices in every atom have to be set.
     * Note: The returned set includes every id only once but duplicates are allowed in the input list.
     *
     * @param aSubstructureList a list of substructures to create identifiers for
     * @return a set of the generated identifier strings
     * @throws NullPointerException if the given list is 'null' (list elements may be null or empty, they will be skipped)
     * @throws IllegalArgumentException if the unique indices are not set in any non-null atom container of the list
     */
    protected Set<String> generateSubstructureIdentifiers(List<IAtomContainer> aSubstructureList) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSubstructureList, "Given list is 'null'");
        for (IAtomContainer tmpSubstructure : aSubstructureList) {
            if (Objects.isNull(tmpSubstructure)) {
                continue;
            }
            if (tmpSubstructure.isEmpty()) {
                continue;
            }
            //method checks that no index appears multiple times but does not check whether there are numbers missing
            boolean tmpAreIndicesSet = this.checkUniqueIndicesOfAtoms(tmpSubstructure);
            if (!tmpAreIndicesSet) {
                throw new IllegalArgumentException("This method requires that every atom has a unique index.");
            }
        }
        float tmpLoadFactor = 0.75f;
        int tmpIdentifierSetInitCapacity = (int)((float) aSubstructureList.size() * (1.0f / tmpLoadFactor) + 2.0f);
        HashSet<String> tmpIdentifierSet = new HashSet<>(tmpIdentifierSetInitCapacity, tmpLoadFactor);
        for (IAtomContainer tmpSubstructure: aSubstructureList) {
            if (Objects.isNull(tmpSubstructure)) {
                continue;
            }
            if (tmpSubstructure.isEmpty()) {
                continue;
            }
            String tmpSubstructureIdentifier = this.generateSubstructureIdentifier(tmpSubstructure);
            tmpIdentifierSet.add(tmpSubstructureIdentifier);
        }
        return tmpIdentifierSet;
    }

    /**
     * Creates an identifier string for substructures of a molecule, based on the unique indices of the included atoms.
     * It is only encoded which atoms are part of the substructure, no bond information etc. Used for a quick matching
     * of substructures in the same molecule. The unique indices in every atom have to be set.
     *
     * @param aSubstructure the substructure to create an identifier for
     * @return the identifier string
     * @throws NullPointerException if the given substructure is 'null'
     * @throws IllegalArgumentException if the unique indices are not set
     */
    protected String generateSubstructureIdentifier(IAtomContainer aSubstructure) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aSubstructure, "Given substructure is 'null'.");
        boolean tmpAreIndicesSet = this.checkUniqueIndicesOfAtoms(aSubstructure);
        if (!tmpAreIndicesSet) {
            throw new IllegalArgumentException("This method requires that every atom has a unique index.");
        }
        List<Integer> tmpIndicesList = new ArrayList<>(aSubstructure.getAtomCount());
        for (IAtom tmpAtom : aSubstructure.atoms()) {
            int tmpAtomIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
            tmpIndicesList.add(tmpAtomIndex);
        }
        Collections.sort(tmpIndicesList);
        String tmpSubstructureIdentifier = "";
        for (int tmpAtomIndex : tmpIndicesList) {
            tmpSubstructureIdentifier = tmpSubstructureIdentifier.concat(Integer.toString(tmpAtomIndex)).concat(":");
        }
        return tmpSubstructureIdentifier;
    }

    /**
     * Checks whether all atoms in the given molecule have a unique (in the given molecule) index as property. It checks
     * the uniqueness of the detected indices but not whether there are numbers missing (the ids of this class are
     * created as numbers starting from zero and growing in integer steps).
     *
     * @param aMolecule the molecule to check
     * @return true if every atom has an index property that is unique in the given molecule
     * @throws NullPointerException if the given molecule is 'null'
     * @throws IllegalArgumentException if the given molecule is empty
     */
    protected boolean checkUniqueIndicesOfAtoms(IAtomContainer aMolecule) throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            throw new IllegalArgumentException("Given molecule is empty.");
        }
        float tmpLoadFactor = 0.75f;
        int tmpAtomIndicesSetInitCapacity = (int)((float) aMolecule.getAtomCount() * (1.0f / tmpLoadFactor) + 2.0f);
        HashSet<Integer> tmpAtomIndicesSet = new HashSet<>(tmpAtomIndicesSetInitCapacity, tmpLoadFactor);
        for (IAtom tmpAtom : aMolecule.atoms()) {
            if (Objects.isNull(tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY))) {
                return false;
            } else {
                int tmpIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                if (tmpAtomIndicesSet.contains(tmpIndex)) {
                    return false;
                } else {
                    tmpAtomIndicesSet.add(tmpIndex);
                }
            }
        }
        //only reached if method is not exited before because of a missing or non-unique index
        return true;
    }

    /**
     * Prints all molecules in the given list as unique SMILES representations to System.out.
     * Used for debugging and in test class.
     *
     * @param aMoleculeList the list to print to console
     */
    protected void printAllMoleculesAsSmiles(List<IAtomContainer> aMoleculeList) {
        if (Objects.isNull(aMoleculeList) || aMoleculeList.isEmpty()) {
            System.out.println("[List is null or empty]");
            return;
        }
        SmilesGenerator tmpSmiGen = new SmilesGenerator(SmiFlavor.Unique);
        for (IAtomContainer tmpCandidate : aMoleculeList) {
            if (Objects.isNull(tmpCandidate)) {
                System.out.println("[null molecule]");
                continue;
            }
            if (tmpCandidate.isEmpty()) {
                System.out.println("[empty molecule]");
                continue;
            }
            try {
                System.out.println(tmpSmiGen.create(tmpCandidate));
            } catch (CDKException | NullPointerException anException) {
                SugarRemovalUtility.LOGGER.log(Level.SEVERE, anException.toString(), anException);
                System.out.println("[molecule could not be parsed to SMILES code]");
            }
        }
    }
    //</editor-fold>
    //<editor-fold desc="Methods for circular sugars">
    /**
     * Detects and returns cycles of the given molecule that are isolated (spiro rings included or not according to the
     * boolean parameter), isomorph to the circular sugar patterns, and only have exocyclic single bonds (keto groups
     * ignored or not according to the boolean parameter). These cycles are
     * the general candidates for circular sugars that are filtered according to the other settings in the following steps.
     * Spiro atoms are marked by a property.
     *
     * @param aMolecule the molecule to extract potential circular sugars from
     * @param anIncludeSpiroRings specification whether spiro rings should be included in the detected potential sugar
     *                           cycles or filtered out; for circular sugar detection this should be set according to the
     *                           current 'detect spiro rings as circular sugars' setting; for filtering circular sugar
     *                           candidates or their atoms during linear sugar detection, this should be set to 'true'
     * @param anIgnoreKetoGroups specification whether potential sugar cycles with keto groups should be included in the
     *                           returned list; for circular sugar detection this should be set according to the
     *                           current 'detect circular sugars with keto groups' setting; for filtering circular sugar
     *                           candidates or their atoms during linear sugar detection, this should be set to 'true'
     * @return a list of the potential sugar cycles
     * @throws NullPointerException if the given molecule is 'null'
     */
    protected List<IAtomContainer> detectPotentialSugarCycles(IAtomContainer aMolecule,
                                                              boolean anIncludeSpiroRings,
                                                              boolean anIgnoreKetoGroups) throws NullPointerException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        if (aMolecule.isEmpty()) {
            return new ArrayList<>(0);
        }
        boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(aMolecule);
        if (!tmpIndicesAreSet) {
            this.addUniqueIndicesToAtoms(aMolecule);
        }
        int[][] tmpAdjList = GraphUtil.toAdjList(aMolecule);
        //efficient computation/partitioning of the ring systems
        RingSearch tmpRingSearch = new RingSearch(aMolecule, tmpAdjList);
        List<IAtomContainer> tmpIsolatedRings = tmpRingSearch.isolatedRingFragments();
        if (tmpIsolatedRings.isEmpty()) {
            return new ArrayList<>(0);
        }
        //</editor-fold>
        //<editor-fold desc="Identification of spiro rings / atoms">
        //iterating through all atoms in rings to identify spiro rings
        List<IAtomContainer> tmpRingFragments = tmpRingSearch.isolatedRingFragments();
        tmpRingFragments.addAll(tmpRingSearch.fusedRingFragments());
        //Mapping identifiers of all the rings in the molecule to whether they are fused OR spiro (true) or isolated
        // AND non-spiro (false)
        float tmpLoadFactor = 0.75f;
        int tmpRingIdentifierToIsFusedOrSpiroMapInitCapacity = (int)((float) tmpRingFragments.size() * (1.0f / tmpLoadFactor) + 2.0f);
        HashMap<String, Boolean> tmpRingIdentifierToIsFusedOrSpiroMap = new HashMap<>(tmpRingIdentifierToIsFusedOrSpiroMapInitCapacity, tmpLoadFactor);
        //Mapping atom identifiers to identifiers of the rings they are part of
        int tmpAtomIDToRingIDMapInitCapacity = 6 * (int)((float) tmpRingFragments.size() * (1.0f / tmpLoadFactor) + 2.0f);
        HashMap<Integer, Set<String>> tmpAtomIDToRingIDMap = new HashMap<>(tmpAtomIDToRingIDMapInitCapacity, tmpLoadFactor);
        /* Every atom of every ring is visited; If one atom is visited multiple times, it is in a fused ring or a spiro
         * atom connecting two spiro rings */
        for (IAtomContainer tmpRing : tmpRingFragments) {
            String tmpRingID = this.generateSubstructureIdentifier(tmpRing);
            //initial value false until one atom of the ring is visited more than once
            tmpRingIdentifierToIsFusedOrSpiroMap.put(tmpRingID, false);
            for (IAtom tmpAtom : tmpRing.atoms()) {
                int tmpAtomID = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                //case 1: atom is not present in the map yet, so it is not shared by another ring that was already visited
                if (!tmpAtomIDToRingIDMap.containsKey(tmpAtomID)) {
                    //the atom (id) is added to the map with a set that for now only contains the id of the current ring
                    int tmpRingIDSetInitCapacity = (int)((float) 5.0f * (1.0f / tmpLoadFactor) + 2.0f);
                    HashSet<String> tmpRingIDSet = new HashSet<>(tmpRingIDSetInitCapacity, tmpLoadFactor);
                    tmpRingIDSet.add(tmpRingID);
                    tmpAtomIDToRingIDMap.put(tmpAtomID, tmpRingIDSet);
                //case 2: atom was already visited, so it is part of at least one other ring
                } else {
                    //current ring is marked as fused or spiro
                    tmpRingIdentifierToIsFusedOrSpiroMap.put(tmpRingID, true);
                    //set of all identifiers of all rings already visited that this atom is part of
                    Set<String> tmpRingIDSet = tmpAtomIDToRingIDMap.get(tmpAtomID);
                    //they are marked as fused or spiro since they share at least the current atom with another ring
                    for (String tmpAlreadyVisitedRingID : tmpRingIDSet) {
                        tmpRingIdentifierToIsFusedOrSpiroMap.put(tmpAlreadyVisitedRingID, true);
                    }
                    //id of the current ring is added to the list
                    if (!tmpRingIDSet.contains(tmpRingID)) {
                        tmpRingIDSet.add(tmpRingID);
                    }
                }
            }
        }
        //</editor-fold>
        List<IAtomContainer> tmpSugarCandidates = new ArrayList<>(tmpIsolatedRings.size());
        for (IAtomContainer tmpIsolatedRing : tmpIsolatedRings) {
            if (Objects.isNull(tmpIsolatedRing) || tmpIsolatedRing.isEmpty()) {
                continue;
            }
            if (!anIncludeSpiroRings) {
                //Filtering spiro rings if they should not be detected as sugars
                String tmpRingID = this.generateSubstructureIdentifier(tmpIsolatedRing);
                //if true, the ring is fused or spiro according to the map; but since only isolated cycles are queried,
                // they are definitely spiro if the map returns true
                if (tmpRingIdentifierToIsFusedOrSpiroMap.get(tmpRingID)) {
                    continue;
                }
            }
            for (IAtomContainer tmpReferenceRing : this.circularSugarStructuresList) {
                boolean tmpIsIsomorph = false;
                UniversalIsomorphismTester tmpUnivIsoTester = new UniversalIsomorphismTester();
                try {
                    tmpIsIsomorph = tmpUnivIsoTester.isIsomorph(tmpReferenceRing, tmpIsolatedRing);
                } catch (CDKException aCDKException) {
                    SugarRemovalUtility.LOGGER.log(Level.WARNING, aCDKException.toString(), aCDKException);
                    continue;
                }
                if (tmpIsIsomorph) {
                    /* note: another requirement of a suspected sugar ring should be that it contains only single bonds.
                     * This is not tested here because all the structures in the reference rings do meet this criterion.
                     * But a structure that does not meet this criterion could be added to the references by the user.*/
                    boolean tmpAreAllExocyclicBondsSingle = this.areAllExocyclicBondsSingle(tmpIsolatedRing, aMolecule, anIgnoreKetoGroups);
                    if (!tmpAreAllExocyclicBondsSingle) {
                        //do not remove rings with non-single exocyclic bonds, they are not sugars (not an option!)
                        break;
                    }
                    /*identification of spiro atoms (the cycle is isolated, so it can share at max one atom with another cycle
                     * and this atom is therefore a spiro bridge); this is done only now to not disturb the removal of linear sugars
                     * that are part of cycles; the info is only needed if spiro ring are detected as sugars and not filtered
                     * according to the settings (but always noted here anyway, should the setting change between detection and removal)*/
                    for (IAtom tmpAtom : tmpIsolatedRing.atoms()) {
                        int tmpAtomID = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        //note: the id HAS TO be in the map
                        Set<String> tmpRingIDSet = tmpAtomIDToRingIDMap.get(tmpAtomID);
                        int tmpSize = tmpRingIDSet.size();
                        /* if size > 1 atom is part of multiple rings and therefore the spiro bridge of two cycles, so
                         * keep it at removal of the sugar to protect the adjacent ring!
                         * note: the removal method has to test for the presence of the property anyway, so adding it with
                         * value 'false' to the other atoms in the ring is redundant */
                        if (tmpSize > 1) {
                            tmpAtom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, true);
                        }
                    }
                    tmpSugarCandidates.add(tmpIsolatedRing);
                    break;
                } //else {continue;}
            }
        }
        return tmpSugarCandidates;
    }

    /**
     * Checks whether all exocyclic bonds connected to a given ring fragment of a parent atom container are of single
     * order. If the option to allow potential sugar cycles having keto groups is activated, this method also returns
     * true if a cycle having a keto group is processed.
     * <br>The method iterates over all cyclic atoms and all of their bonds. So the runtime scales linear with the number
     * of cyclic atoms and their connected bonds. In principle, this method can be used also for non-cyclic substructures.
     * <br>Note: It is not tested whether the original molecule is actually the parent of the ring to test.
     *
     * @param aRingToTest the ring fragment to test; exocyclic bonds do not have to be included in the fragment but if it
     *                    is a fused system of multiple rings, the internal interconnecting bonds of the different rings
     *                    need to be included; all its atoms need to be exactly the same objects as in the second atom
     *                    container parameter
     * @param anOriginalMolecule the molecule that contains the ring under investigation; The exocyclic bonds will be
     *                           queried from it
     * @param anIgnoreKetoGroups true if this method should ignore keto groups, i.e. also return true if there are some
     *                           attached to the cycle
     * @return true, if all exocyclic bonds connected to the ring are of single order
     * @throws NullPointerException if one parameter is 'null'
     * @throws IllegalArgumentException if one parameter is empty
     */
    protected boolean areAllExocyclicBondsSingle(IAtomContainer aRingToTest, IAtomContainer anOriginalMolecule, boolean anIgnoreKetoGroups)
            throws NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(aRingToTest, "Given ring atom container is 'null'");
        Objects.requireNonNull(anOriginalMolecule, "Given atom container representing the original molecule " +
                "is 'null'");
        if (aRingToTest.isEmpty() || anOriginalMolecule.isEmpty()) {
            throw new IllegalArgumentException("One of the given parameters is empty.");
        }
        int tmpAtomCountInRing = aRingToTest.getAtomCount();
        int tmpArrayListInitCapacity = tmpAtomCountInRing * 2;
        List<IBond> tmpExocyclicBondsList = new ArrayList<>(tmpArrayListInitCapacity);
        Iterable<IAtom> tmpRingAtoms = aRingToTest.atoms();
        for (IAtom tmpRingAtom : tmpRingAtoms) {
            if (!anOriginalMolecule.contains(tmpRingAtom)) {
                continue;
            }
            List<IBond> tmpConnectedBondsList = anOriginalMolecule.getConnectedBondsList(tmpRingAtom);
            for (IBond tmpBond : tmpConnectedBondsList) {
                boolean tmpIsInRing = aRingToTest.contains(tmpBond);
                if (!tmpIsInRing) {
                    tmpExocyclicBondsList.add(tmpBond);
                }
            }
        }
        if (anIgnoreKetoGroups) {
            for (IBond tmpBond : tmpExocyclicBondsList) {
                IBond.Order tmpOrder = tmpBond.getOrder();
                //if the loop is not exited via return, true is returned after its completion
                if (tmpOrder != IBond.Order.SINGLE) {
                    //if the bond order is double, check for keto group; otherwise, return false
                    if (tmpOrder == IBond.Order.DOUBLE) {
                        boolean tmpContainsOxygen = false;
                        for (IAtom tmpAtom : tmpBond.atoms()) {
                            if (tmpAtom.getSymbol().equals("O")) {
                                tmpContainsOxygen = true;
                            }
                        }
                        //if the bond contains oxygen, it is a keto group, because it double-bound
                        // note: it is not checked whether the oxygen is outside the ring, not inside, which is
                        // hardly possible because the bond is exocyclic, the oxygen would be four-bound in total.
                        // note 2: it is also not checked whether the oxygen has more bonds in addition to this double
                        // bond, but this would also be not chemically intuitive.
                        if (!tmpContainsOxygen) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return (BondManipulator.getMaximumBondOrder(tmpExocyclicBondsList) == IBond.Order.SINGLE);
        }
    }

    /**
     * Checks all exocyclic connections of the given ring to detect an  O-glycosidic bond. Checklist for glycosidic bond:
     * Connected oxygen atom that is not in the ring, has two bonds that are both of single order and no bond partner
     * is a hydrogen atom. This algorithm also classifies ester bonds as glycosidic bonds and any other bond type
     * that meets the above criteria. Therefore, many 'non-classical, glycoside-like' connections are classified
     * as O-glycosidic bonds.
     * <br>Note: The 'ring' is not tested for whether it is circular or not. So theoretically, this method
     * can also be used to detect glycosidic bonds of linear structures. BUT: The oxygen atom must not be part of the
     * structure itself. Due to the processing of candidate linear sugar moieties this can make it difficult to use
     * this method also for linear sugars.
     * <br>Note: It is not tested whether the original molecule is actually the parent of the ring to test.
     *
     * @param aRingToTest the candidate sugar ring
     * @param anOriginalMolecule the molecule in which the ring is contained as a substructure to query the connected
     *                           atoms from
     * @return true, if a glycosidic bond is detected
     * @throws NullPointerException if one parameter is 'null'
     * @throws IllegalArgumentException if one parameter is empty
     */
    protected boolean hasGlycosidicBond(IAtomContainer aRingToTest, IAtomContainer anOriginalMolecule)
            throws NullPointerException {
        Objects.requireNonNull(aRingToTest, "Given ring atom container is 'null'");
        Objects.requireNonNull(anOriginalMolecule, "Given atom container representing the original molecule " +
                "is 'null'");
        if (aRingToTest.isEmpty() || anOriginalMolecule.isEmpty()) {
            throw new IllegalArgumentException("One of the given parameters is empty.");
        }
        Iterable<IAtom> tmpRingAtoms = aRingToTest.atoms();
        boolean tmpContainsGlycosidicBond = false;
        for (IAtom tmpRingAtom : tmpRingAtoms) {
            boolean tmpBreakOuterLoop = false;
            //check to avoid exceptions
            if (!anOriginalMolecule.contains(tmpRingAtom)) {
                continue;
            }
            List<IAtom> connectedAtomsList = anOriginalMolecule.getConnectedAtomsList(tmpRingAtom);
            for (IAtom tmpAtom : connectedAtomsList) {
                boolean tmpIsInRing = aRingToTest.contains(tmpAtom);
                if (!tmpIsInRing) {
                    String tmpSymbol = tmpAtom.getSymbol();
                    boolean tmpIsOxygen = (tmpSymbol.equals("O"));
                    if (tmpIsOxygen) {
                        List<IBond> tmpConnectedBondsList = anOriginalMolecule.getConnectedBondsList(tmpAtom);
                        boolean tmpHasOnlyTwoBonds = (tmpConnectedBondsList.size() == 2);
                        boolean tmpAllBondsAreSingle =
                                (BondManipulator.getMaximumBondOrder(tmpConnectedBondsList) == IBond.Order.SINGLE);
                        boolean tmpOneBondAtomIsHydrogen = false;
                        for (IBond tmpBond : tmpConnectedBondsList) {
                            for (IAtom tmpBondAtom : tmpBond.atoms()) {
                                if (tmpBondAtom.getSymbol().equals("H")) {
                                    tmpOneBondAtomIsHydrogen = true;
                                }
                            }
                        }
                        if ((tmpHasOnlyTwoBonds && tmpAllBondsAreSingle) && !tmpOneBondAtomIsHydrogen) {
                            tmpContainsGlycosidicBond = true;
                            tmpBreakOuterLoop = true;
                            break;
                        }
                    }
                }
            }
            if (tmpBreakOuterLoop) {
                break;
            }
        }
        return tmpContainsGlycosidicBond;
    }

    /**
     * Checks whether the given molecule would be empty after removal of the given ring. Any remaining
     * fragment will be cleared away if it is too small according to the set preservation mode option. The given
     * parameters are not altered, clones of them are generated and processed. This method is intended to test for whether
     * a molecule qualifies for the gylcosidic bond exemption.
     *
     * @param aRing the ring to test whether its removal would result in an empty molecule
     * @param aMolecule the parent molecule
     * @return true if the parent molecule is empty after removal of the given ring and subsequent removal of too small
     * remaining fragments
     * @throws NullPointerException if any parameter is 'null'
     * @throws IllegalArgumentException if the given ring is not actually part of the given parent molecule
     * @throws CloneNotSupportedException if the ring or the molecule cannot be cloned
     */
    protected boolean isMoleculeEmptyAfterRemovalOfThisRing(IAtomContainer aRing, IAtomContainer aMolecule)
            throws NullPointerException, IllegalArgumentException, CloneNotSupportedException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aRing, "Given ring is 'null'.");
        Objects.requireNonNull(aMolecule, "Given parent molecule is 'null'.");
        boolean tmpIsParent = true;
        for (IAtom tmpAtom : aRing.atoms()) {
            if (!aMolecule.contains(tmpAtom)) {
                tmpIsParent = false;
                break;
            }
        }
        if (!tmpIsParent) {
            throw new IllegalArgumentException("Given substructure is not part of the given parent molecule.");
        }
        boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(aMolecule);
        if (!tmpIndicesAreSet) {
            this.addUniqueIndicesToAtoms(aMolecule);
        }
        //</editor-fold>
        boolean tmpMoleculeIsEmptyAfterRemoval = false;
        IAtomContainer tmpMoleculeClone = aMolecule.clone();
        IAtomContainer tmpSubstructureClone = aRing.clone();
        float tmpLoadFactor = 0.75f;
        int tmpIndexToAtomMapInitCapacity = (int)((float) tmpMoleculeClone.getAtomCount() * (1.0f / tmpLoadFactor) + 2.0f);
        HashMap<Integer, IAtom> tmpIndexToAtomMap = new HashMap<>(tmpIndexToAtomMapInitCapacity, tmpLoadFactor);
        for (IAtom tmpAtom : tmpMoleculeClone.atoms()) {
            tmpIndexToAtomMap.put(tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), tmpAtom);
        }
        for (IAtom tmpAtom : tmpSubstructureClone.atoms()) {
            tmpMoleculeClone.removeAtom(tmpIndexToAtomMap.get((int)tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY)));
        }
        if (tmpMoleculeClone.isEmpty()) {
            tmpMoleculeIsEmptyAfterRemoval = true;
        } else {
            this.removeTooSmallDisconnectedStructures(tmpMoleculeClone);
            tmpMoleculeIsEmptyAfterRemoval = tmpMoleculeClone.isEmpty();
        }
        return tmpMoleculeIsEmptyAfterRemoval;
    }

    /**
     * Returns the number of attached exocyclic oxygen atoms of a given ring in the original atom container.
     * The method iterates over all cyclic atoms and all of their connected atoms. So the runtime scales linear with
     * the number of cyclic atoms and their connected atoms.
     * The oxygen atoms are not tested for being attached by a single bond since in the algorithm, the
     * determination whether a candidate sugar ring has only exocyclic single bonds precedes the calling of this method.
     * <br>Note: The circularity of the given 'ring' is not tested, so this method could in theory also be used for linear
     * structures. But his does not make much sense.
     * <br>Note: This method does NOT check for hydroxy groups but for oxygen atoms. So e.g. the oxygen atom in a
     * glycosidic bond is counted.
     * <br>Note: It is not tested whether the original molecule is actually the parent of the ring to test.
     *
     * @param aRingToTest the ring fragment to test; exocyclic bonds do not have to be included in the fragment but if it
     *                    is a fused system of multiple rings, the internal interconnecting bonds of the different rings
     *                    need to be included; all its atoms need to be exactly the same objects as in the second atom
     *                    container parameter (they will be skipped otherwise)
     * @param anOriginalMolecule the molecule that contains the ring under investigation; The exocyclic bonds will be
     *                           queried from it
     * @return number of attached exocyclic oxygen atoms of the given ring
     * @throws NullPointerException if a parameter is 'null'
     */
    protected int getExocyclicOxygenAtomCount(IAtomContainer aRingToTest, IAtomContainer anOriginalMolecule)
           throws NullPointerException {
        Objects.requireNonNull(aRingToTest, "Given ring atom container is 'null'");
        Objects.requireNonNull(anOriginalMolecule, "Given atom container representing the original molecule " +
                "is 'null'");
        int tmpExocyclicOxygenCounter = 0;
        Iterable<IAtom> tmpRingAtoms = aRingToTest.atoms();
        for (IAtom tmpRingAtom : tmpRingAtoms) {
            //check to avoid exceptions
            if (!anOriginalMolecule.contains(tmpRingAtom)) {
                continue;
            }
            List<IAtom> tmpConnectedAtomsList = anOriginalMolecule.getConnectedAtomsList(tmpRingAtom);
            for (IAtom tmpConnectedAtom : tmpConnectedAtomsList) {
                String tmpSymbol = tmpConnectedAtom.getSymbol();
                boolean tmpIsOxygen = tmpSymbol.equals("O");
                boolean tmpIsInRing = aRingToTest.contains(tmpConnectedAtom);
                if (tmpIsOxygen && !tmpIsInRing) {
                    tmpExocyclicOxygenCounter++;
                }
            }
        }
        return tmpExocyclicOxygenCounter;
    }

    /**
     * Simple decision-making function for deciding whether a candidate sugar ring has enough attached, single-bonded
     * exocyclic oxygen atoms according to the set threshold. The given number of oxygen atoms
     * is divided by the given number of atoms in the ring (should also contain the usually present oxygen atom in
     * a sugar ring) and the resulting ratio is checked for being equal or higher than the currently set
     * threshold.
     * <br>Note: Only the number of atoms in the ring is checked for not being 0. No further parameter tests are
     * implemented. If the number is 0, false is returned. No exceptions are thrown.
     *
     * @param aNumberOfAtomsInRing number of atoms in the possible sugar ring, including the cyclic oxygen atom
     * @param aNumberOfAttachedExocyclicOxygenAtoms number of attached exocyclic oxygen atoms of the ring under
     *                                              investigation (if zero, false is returned)
     * @return true, if the calculated ratio is equal to or higher than the currently set threshold
     */
    protected boolean doesRingHaveEnoughExocyclicOxygenAtoms(int aNumberOfAtomsInRing,
                                                             int aNumberOfAttachedExocyclicOxygenAtoms) {
        if (aNumberOfAtomsInRing == 0) {
            //better than throwing an exception here?
            return false;
        }
        double tmpAttachedOxygensToAtomsInRingRatio =
                ((double) aNumberOfAttachedExocyclicOxygenAtoms / (double) aNumberOfAtomsInRing);
        boolean tmpMeetsThreshold =
                (tmpAttachedOxygensToAtomsInRingRatio >= this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting);
        return tmpMeetsThreshold;
    }
    //</editor-fold>
    //<editor-fold desc="Methods for linear sugars">
    /**
     * All linear sugar patterns represented by atom containers in the respective list are sorted, parsed into actual
     * pattern objects and stored in the internal list for initial linear sugar detection. To be called when a linear
     * sugar patterns has been deleted or added to the list. It cannot directly be operated on the pattern objects
     * because they cannot be sorted or represented in a human-readable format.
     */
    protected void updateLinearSugarPatterns() {
        Comparator<IAtomContainer> tmpComparator = new AtomContainerComparator().reversed();
        //note: this can throw various exceptions, but they should not appear here
        this.linearSugarStructuresList.sort(tmpComparator);
        //parsing linear sugars into patterns; this has to be re-done completely because the patterns cannot be sorted
        for (IAtomContainer tmpSugarAC : this.linearSugarStructuresList){
            try {
                this.linearSugarPatternsList.add(DfPattern.findSubstructure(tmpSugarAC));
            } catch (Exception anException) {
                SugarRemovalUtility.LOGGER.log(Level.WARNING, anException.toString(), anException);
            }
        }
    }

    /**
     * Initial detection of linear sugar candidates by substructure search for the linear sugar patterns in the given
     * molecule. All 'unique' matches are returned as atom container objects. this means that the same substructure will
     * not be included multiple times but the substructures may overlap.
     *
     * @param aMolecule the molecule to search for linear sugar candidates
     * @return a list of possibly overlapping substructures from the given molecule matching the internal linear sugar
     * patterns
     * @throws NullPointerException if the given molecule is 'null'
     */
    protected List<IAtomContainer> detectLinearSugarCandidatesByPatternMatching(IAtomContainer aMolecule) throws NullPointerException {
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'");
        IAtomContainer tmpNewMolecule = aMolecule;
        if (tmpNewMolecule.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> tmpSugarCandidates = new ArrayList<>(tmpNewMolecule.getAtomCount() / 2);
        int tmpListSize = this.linearSugarPatternsList.size();
        List<DfPattern> tmpListToIterate = new ArrayList<>(tmpListSize);
        tmpListToIterate.addAll(0, this.linearSugarPatternsList);
        for (DfPattern tmpLinearSugarPattern : tmpListToIterate) {
            if (Objects.isNull(tmpLinearSugarPattern)) {
                continue;
            }
            /*unique in this case means that the same match cannot be in this collection multiple times, but they can
            still overlap!*/
            Mappings tmpMappings = tmpLinearSugarPattern.matchAll(tmpNewMolecule);
            Mappings tmpUniqueMappings = tmpMappings.uniqueAtoms();
            Iterable<IAtomContainer> tmpUniqueSubstructureMappings = tmpUniqueMappings.toSubstructures();
            for (IAtomContainer tmpMatchedStructure : tmpUniqueSubstructureMappings) {
                if (Objects.isNull(tmpMatchedStructure)) {
                    continue;
                }
                tmpSugarCandidates.add(tmpMatchedStructure);
            }
        }
        return tmpSugarCandidates;
    }

    /**
     * Combines all overlapping (i.e. sharing the same atoms or bonds) structures in the given list into one atom container,
     * respectively, to return distinct, non-overlapping substructures. Second step of linear sugar detection. Note: The
     * returned substructures can grow very big. This addressed in the third step.
     * The parameter list is not altered and a completely new list returned.
     *
     * @param aCandidateList a list of possibly overlapping substructures from the same atom container object
     * @return a list of distinct, non-overlapping substructures after combining every formerly overlapping structure
     * @throws NullPointerException if the given list or one of its elements is 'null'
     */
    protected List<IAtomContainer> combineOverlappingCandidates(List<IAtomContainer> aCandidateList) throws NullPointerException  {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return aCandidateList;
        }
        int tmpListSize = aCandidateList.size();
        List<IAtomContainer> tmpNonOverlappingSugarCandidates = new ArrayList<>(tmpListSize);
        IAtomContainer tmpMatchesContainer = aCandidateList.get(0).getBuilder().newInstance(IAtomContainer.class);
        for (IAtomContainer tmpCandidate : aCandidateList) {
            Objects.requireNonNull(tmpCandidate, "A substructure in the list is 'null'.");
            tmpMatchesContainer.add(tmpCandidate);
        }
        boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpMatchesContainer);
        if (tmpIsConnected) {
            tmpNonOverlappingSugarCandidates.add(tmpMatchesContainer);
        } else {
            IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpMatchesContainer);
            Iterable<IAtomContainer> tmpMolecules = tmpComponents.atomContainers();
            for (IAtomContainer tmpComponent : tmpMolecules) {
                tmpNonOverlappingSugarCandidates.add(tmpComponent);
            }
        }
        return tmpNonOverlappingSugarCandidates;
    }

    /**
     * Alternative method to combining overlapping substructures after the initial detection: Splitting them pseudo-randomly.
     * The method iterates the given substructures and notes the indices of atoms already visited. If an already visited
     * atom appears again in another substructure (- {@literal >} overlap), it is removed from the respective candidate. In the end,
     * all candidates that got disconnected by this, are separated into distinct atom container objects. The result
     * are distinct, non-overlapping, connected substructures. Note: The returned substructures can be very small, even
     * single-atom candidates can result. Another problem is that this method is practically an unpredictable black-box
     * because the order of the substructures is not predictable.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     *
     * @param aCandidateList a list of possibly overlapping substructures from the same atom container object
     * @throws NullPointerException if the given list is 'null'
     */
    @Deprecated
    protected void splitOverlappingCandidatesPseudoRandomly(List<IAtomContainer> aCandidateList) throws NullPointerException {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        float tmpLoadFactor = 0.75f;
        int tmpSugarCandidateAtomsSetInitCapacity = (int)(8.0f * (float) aCandidateList.size() * (1.0f / tmpLoadFactor) + 2.0f);
        HashSet<Integer> tmpSugarCandidateAtomsSet = new HashSet<>(tmpSugarCandidateAtomsSetInitCapacity, tmpLoadFactor);
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            if (Objects.isNull(tmpCandidate)) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            boolean tmpIndicesAreSet = this.checkUniqueIndicesOfAtoms(tmpCandidate);
            if (!tmpIndicesAreSet) {
                this.addUniqueIndicesToAtoms(tmpCandidate);
            }
            for (int j = 0; j < tmpCandidate.getAtomCount(); j++) {
                IAtom tmpAtom = tmpCandidate.getAtom(j);
                int tmpAtomIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                boolean tmpIsAtomAlreadyInCandidates = tmpSugarCandidateAtomsSet.contains(tmpAtomIndex);
                if (tmpIsAtomAlreadyInCandidates) {
                    tmpCandidate.removeAtom(tmpAtom);
                    //The removal shifts the remaining indices!
                    j = j - 1;
                } else {
                    tmpSugarCandidateAtomsSet.add(tmpAtomIndex);
                }
            }
            if (tmpCandidate.isEmpty()) {
                aCandidateList.remove(tmpCandidate);
                //The removal shifts the remaining indices!
                i = i - 1;
            }
        }
        //sugar candidates may be disconnected in themselves, this is corrected here
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpCandidate);
            if (!tmpIsConnected) {
                IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpCandidate);
                for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                    aCandidateList.add(tmpComponent);
                }
                aCandidateList.remove(i);
                i = i - 1;
            }
        }
    }

    /**
     * Splits all ether, ester, and peroxide bonds in the given linear sugar candidates and separates those that get
     * disconnected in the process. Third step of linear sugar detection. This step was introduced because the linear sugar
     * candidates returned by the combination method can be very big and contain connected sugar chains that should be
     * detected as separate candidates. The detection is done using SMARTS patterns that are public constants of this
     * class.
     * The parameter list is not altered and a completely new list returned.
     *
     * @param aCandidateList a list of potential sugar substructures from the same atom container object
     * @return a new list of candidates where all ether, ester, and peroxide bonds have been split and disconnected
     * candidates separated
     * @throws NullPointerException if the given list is 'null'
     */
    protected List<IAtomContainer> splitEtherEsterAndPeroxideBonds(List<IAtomContainer> aCandidateList) throws NullPointerException {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> tmpProcessedCandidates = new ArrayList<>(aCandidateList.size() * 2);
        for (IAtomContainer tmpCandidate : aCandidateList) {
            if (Objects.isNull(tmpCandidate)) {
                continue;
            }
            SmartsPattern.prepare(tmpCandidate);

            //note: ester matching has to precede the ether matching because the ether pattern also matches esters
            //note 2: here, which bond is removed is specifically defined. This is not the case for the ether
            Mappings tmpEsterMappings = SugarRemovalUtility.ESTER_SMARTS_PATTERN.matchAll(tmpCandidate).uniqueAtoms();
            if (tmpEsterMappings.atLeast(1)) {
                for (IAtomContainer tmpEsterGroup : tmpEsterMappings.toSubstructures()) {
                    IAtom tmpDoubleBondedOxygen = null;
                    IAtom tmpConnectingOxygen = null;
                    for (IAtom tmpAtom : tmpEsterGroup.atoms()) {
                        String tmpSymbol = tmpAtom.getSymbol();
                        if (tmpSymbol.equals("O")) {
                            int tmpBondCount = tmpAtom.getBondCount();
                            if (tmpBondCount == 1) {
                                tmpDoubleBondedOxygen = tmpAtom;
                            } else {
                                tmpConnectingOxygen = tmpAtom;
                            }
                        }
                    }
                    IAtom tmpCarbonBoundToDoubleBondedOxygen = tmpEsterGroup.getConnectedAtomsList(tmpDoubleBondedOxygen).get(0);
                    tmpCandidate.removeBond(tmpCarbonBoundToDoubleBondedOxygen, tmpConnectingOxygen);
                }
            }

            //note: which bond is actually removed is 'pseudo-random', i.e. not predictable by a human
            Mappings tmpEtherMappings = SugarRemovalUtility.ETHER_SMARTS_PATTERN.matchAll(tmpCandidate).uniqueAtoms();
            if (tmpEtherMappings.atLeast(1)) {
                for (IAtomContainer tmpEtherGroup : tmpEtherMappings.toSubstructures()) {
                    IAtom tmpCarbon1 = null;
                    IAtom tmpCarbon2 = null;
                    IAtom tmpOxygen = null;
                    for (IAtom tmpAtom : tmpEtherGroup.atoms()) {
                        String tmpSymbol = tmpAtom.getSymbol();
                        if (tmpSymbol.equals("O")) {
                            tmpOxygen = tmpAtom;
                        } else if (tmpSymbol.equals("C") && Objects.isNull(tmpCarbon1)) {
                            tmpCarbon1 = tmpAtom;
                        } else {
                            tmpCarbon2 = tmpAtom;
                        }
                    }
                    tmpCandidate.removeBond(tmpOxygen, tmpCarbon2);
                }
            }

            Mappings tmpPeroxideMappings = SugarRemovalUtility.PEROXIDE_SMARTS_PATTERN.matchAll(tmpCandidate).uniqueAtoms();
            if (tmpPeroxideMappings.atLeast(1)) {
                for (IAtomContainer tmpPeroxideGroup : tmpPeroxideMappings.toSubstructures()) {
                    IAtom tmpOxygen1 = null;
                    IAtom tmpOxygen2 =  null;
                    for (IAtom tmpAtom : tmpPeroxideGroup.atoms()) {
                        String tmpSymbol = tmpAtom.getSymbol();
                        if (tmpSymbol.equals("O")) {
                            if (Objects.isNull(tmpOxygen1)) {
                                tmpOxygen1 = tmpAtom;
                            } else {
                                tmpOxygen2 = tmpAtom;
                            }
                        }
                    }
                    tmpCandidate.removeBond(tmpOxygen1, tmpOxygen2);
                }
            }

            boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpCandidate);
            if (tmpIsConnected) {
                tmpProcessedCandidates.add(tmpCandidate);
            } else {
                IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpCandidate);
                for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                    tmpProcessedCandidates.add(tmpComponent);
                }
            }
        }
        return tmpProcessedCandidates;
    }

    /**
     * Removes all atoms belonging to possible circular sugars, as returned by the method for initial circular sugar
     * detection, from the given linear sugar candidates. Fourth step of linear sugar detection. The linear sugar patterns
     * also match parts of circular sugar, so this step has to be done to ensure the separate treatment of circular and
     * linear sugars. After the removal, disconnected candidates are separated into new candidates.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     * Note also that it is not checked whether the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param aCandidateList a list of potential sugar substructures from the same atom container object
     * @param aParentMolecule the molecule that is currently scanned for linear sugars to detect its circular sugars
     * @throws NullPointerException if any parameter is 'null'
     */
    protected void removeAtomsOfCircularSugarsFromCandidates(List<IAtomContainer> aCandidateList,
                                                             IAtomContainer aParentMolecule)
            throws NullPointerException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        Objects.requireNonNull(aParentMolecule, "Given parent molecule is 'null'.");
        boolean tmpAreIndicesSet = this.checkUniqueIndicesOfAtoms(aParentMolecule);
        if (!tmpAreIndicesSet) {
            this.addUniqueIndicesToAtoms(aParentMolecule);
        }
        //</editor-fold>
        //generating set of atom ids of atoms that are part of the circular sugars in the molecule
        List<IAtomContainer> tmpPotentialSugarRingsParent = this.detectPotentialSugarCycles(aParentMolecule, true, true);
        //nothing to process
        if (tmpPotentialSugarRingsParent.isEmpty()) {
            return;
        }
        float tmpLoadFactor = 0.75f;
        int tmpCircularSugarAtomIDSetInitCapacity = (int)( 7.0f * (float) tmpPotentialSugarRingsParent.size() * (1.0f / tmpLoadFactor) + 2.0f);
        HashSet<Integer> tmpCircularSugarAtomIDSet = new HashSet<>(tmpCircularSugarAtomIDSetInitCapacity, tmpLoadFactor);
        for (IAtomContainer tmpCircularSugarCandidate : tmpPotentialSugarRingsParent) {
            for (IAtom tmpAtom : tmpCircularSugarCandidate.atoms()) {
                int tmpAtomIndex = tmpAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                if (!tmpCircularSugarAtomIDSet.contains(tmpAtomIndex)) {
                    tmpCircularSugarAtomIDSet.add(tmpAtomIndex);
                }
            }
        }
        //iterating over candidates
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            if (Objects.isNull(tmpCandidate)) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            boolean tmpAreIndicesSetInCandidate = this.checkUniqueIndicesOfAtoms(tmpCandidate);
            if (!tmpAreIndicesSetInCandidate) {
                this.addUniqueIndicesToAtoms(tmpCandidate);
            }
            for (IAtom tmpCandidateAtom : tmpCandidate.atoms()) {
                int tmpAtomIndex = tmpCandidateAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                if (tmpCircularSugarAtomIDSet.contains(tmpAtomIndex)) {
                    if (tmpCandidate.contains(tmpCandidateAtom)) {
                        tmpCandidate.removeAtom(tmpCandidateAtom);
                    }
                }
            }
            //remove the candidate if it is empty after removal of cycles
            if (tmpCandidate.isEmpty()) {
                aCandidateList.remove(i);
                i = i - 1;
                continue;
            }
            //if the candidate got unconnected by the removal of cycles, split the parts in separate candidates
            boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpCandidate);
            if (!tmpIsConnected) {
                IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpCandidate);
                for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                    aCandidateList.add(tmpComponent);
                }
                aCandidateList.remove(i);
                i = i - 1;
                continue;
            }
        }
    }

    /**
     * Alternative method to removing all atoms that belong to circular sugars from the linear sugar candidates: Removing
     * only complete, intact circular sugar rings from the candidates. The method detects potential circular sugars in the
     * candidates and compares them to the potential sugar cycles in the parent molecule. If there is a match, the respective
     * sugar ring is removed from the candidate. In the end, all candidates that got disconnected by this, are separated
     * into distinct atom container objects. This method was deprecated because it relies on the circular sugars
     * being intact in the linear sugar candidates which is not always the case and can lead to removal of parts of circular
     * sugars.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     * Note also that it is not checked whether the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param aCandidateList a list of linear sugar candidates from the same atom container object
     * @param aParentMolecule the molecule that is currently scanned for linear sugars to detect its circular sugars
     * @throws NullPointerException if any parameter is 'null'
     */
    @Deprecated
    protected void removeCircularSugarsFromCandidates(List<IAtomContainer> aCandidateList,
                                                      IAtomContainer aParentMolecule)
            throws NullPointerException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        Objects.requireNonNull(aParentMolecule, "Given parent molecule is 'null'.");
        boolean tmpAreIndicesSet = this.checkUniqueIndicesOfAtoms(aParentMolecule);
        if (!tmpAreIndicesSet) {
            this.addUniqueIndicesToAtoms(aParentMolecule);
        }
        //</editor-fold>
        //generating ids for the isolated potential sugar circles in the parent molecule
        List<IAtomContainer> tmpPotentialSugarRingsParent = this.detectPotentialSugarCycles(aParentMolecule, true, true);
        //nothing to process
        if (tmpPotentialSugarRingsParent.isEmpty()) {
            return;
        }
        Set<String> tmpPotentialSugarRingsParentIdentifierSet = this.generateSubstructureIdentifiers(tmpPotentialSugarRingsParent);
        // iterating over candidates
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            if (Objects.isNull(tmpCandidate)) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            boolean tmpAreIndicesSetInCandidate = this.checkUniqueIndicesOfAtoms(tmpCandidate);
            if (!tmpAreIndicesSetInCandidate) {
                this.addUniqueIndicesToAtoms(tmpCandidate);
            }
            List<IAtomContainer> tmpPotentialSugarRingsCandidate = this.detectPotentialSugarCycles(tmpCandidate, true, true);
            if (!tmpPotentialSugarRingsCandidate.isEmpty()) {
                //iterating over potential sugar rings in candidate
                for(IAtomContainer tmpRing : tmpPotentialSugarRingsCandidate) {
                    if (Objects.isNull(tmpRing) || tmpRing.isEmpty()) {
                        continue;
                    }
                    String tmpRingIdentifier = this.generateSubstructureIdentifier(tmpRing);
                    boolean tmpIsAlsoIsolatedInParent = tmpPotentialSugarRingsParentIdentifierSet.contains(tmpRingIdentifier);
                    if (tmpIsAlsoIsolatedInParent) {
                        for (IAtom tmpAtom : tmpRing.atoms()) {
                            if (tmpCandidate.contains(tmpAtom)) {
                                tmpCandidate.removeAtom(tmpAtom);
                            }
                        }
                    }
                }
                //remove the candidate if it is empty after removal of cycles
                if (tmpCandidate.isEmpty()) {
                    aCandidateList.remove(i);
                    i = i - 1;
                    continue;
                }
                //if the candidate got unconnected by the removal of cycles, split the parts in separate candidates
                boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpCandidate);
                if (!tmpIsConnected) {
                    IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpCandidate);
                    for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                        aCandidateList.add(tmpComponent);
                    }
                    aCandidateList.remove(i);
                    i = i - 1;
                    continue;
                }
            }
        }
    }

    /**
     * Alternative method to removing all atoms that belong to circular sugars from the linear sugar candidates: Rejecting
     * every candidate completely that contains a circular sugar. The method detects potential circular sugars in the
     * candidates and compares them to the potential sugar cycles in the parent molecule. If there is a match, the respective
     * candidate is filtered out. This method was deprecated because it relies on the circular sugars
     * being intact in the linear sugar candidates which is not always the case and because connected linear sugar
     * moieties would also be filtered out using this approach.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     * Note also that it is not checked whether the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param aCandidateList a list of linear sugar candidates from the same atom container object
     * @param aParentMolecule the molecule that is currently scanned for linear sugars to detect its circular sugars
     * @throws NullPointerException if any parameter is 'null'
     */
    @Deprecated
    protected void removeCandidatesContainingCircularSugars(List<IAtomContainer> aCandidateList,
                                                            IAtomContainer aParentMolecule)
            throws NullPointerException {
        //<editor-fold desc="Checks">
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        Objects.requireNonNull(aParentMolecule, "Given parent molecule is 'null'.");
        boolean tmpAreIndicesSet = this.checkUniqueIndicesOfAtoms(aParentMolecule);
        if (!tmpAreIndicesSet) {
            this.addUniqueIndicesToAtoms(aParentMolecule);
        }
        //</editor-fold>
        //generating ids for the isolated potential sugar circles in the parent molecule
        List<IAtomContainer> tmpPotentialSugarRingsParent = this.detectPotentialSugarCycles(aParentMolecule, true, true);
        //nothing to process
        if (tmpPotentialSugarRingsParent.isEmpty()) {
            return;
        }
        Set<String> tmpPotentialSugarRingsParentIdentifierSet = this.generateSubstructureIdentifiers(tmpPotentialSugarRingsParent);
        //iterating over candidates
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            if (Objects.isNull(tmpCandidate)) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            boolean tmpAreIndicesSetInCandidate = this.checkUniqueIndicesOfAtoms(tmpCandidate);
            if (!tmpAreIndicesSetInCandidate) {
                this.addUniqueIndicesToAtoms(tmpCandidate);
            }
            List<IAtomContainer> tmpPotentialSugarRingsCandidate = this.detectPotentialSugarCycles(tmpCandidate, true, true);
            boolean tmpIsAlsoIsolatedInParent = false;
            if (!tmpPotentialSugarRingsCandidate.isEmpty()) {
                //iterating over potential sugar rings in candidate
                for(IAtomContainer tmpRing : tmpPotentialSugarRingsCandidate) {
                    if (Objects.isNull(tmpRing) || tmpRing.isEmpty()) {
                        continue;
                    }
                    String tmpRingIdentifier = this.generateSubstructureIdentifier(tmpRing);
                    tmpIsAlsoIsolatedInParent = tmpPotentialSugarRingsParentIdentifierSet.contains(tmpRingIdentifier);
                    if (tmpIsAlsoIsolatedInParent) {
                        aCandidateList.remove(i);
                        i = i - 1;
                        //break the iteration of rings and go to the next candidate
                        break;
                    }
                }
            }
        }
    }

    /**
     * Removes all atoms that are part of a cycle from the given linear sugar candidates. Optional fifth step of linear
     * sugar detection. The linear sugar patterns can also match in cycles that do not represent circular sugars but e.g.
     * pseudo-sugars or macrocycles. It is optional to detect linear sugars in such structures or not.
     * After the removal, disconnected candidates are separated into new candidates.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     * Note also that it is not checked whether the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param aCandidateList a list of potential sugar substructures from the same atom container object
     * @param aMolecule the molecule that is currently scanned for linear sugars to detect its cycles
     * @throws NullPointerException if any parameter is 'null'
     */
    protected void removeCyclicAtomsFromSugarCandidates(List<IAtomContainer> aCandidateList,
                                                        IAtomContainer aMolecule)
            throws NullPointerException {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        int[][] tmpAdjList = GraphUtil.toAdjList(aMolecule);
        RingSearch tmpRingSearch = new RingSearch(aMolecule, tmpAdjList);
        boolean tmpMoleculeHasRings = tmpRingSearch.numRings() > 0;
        if (!tmpMoleculeHasRings) {
            //nothing to process
            return;
        }
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            if (Objects.isNull(tmpCandidate)) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            for (int j = 0; j < tmpCandidate.getAtomCount(); j++) {
                IAtom tmpAtom = tmpCandidate.getAtom(j);
                if (tmpRingSearch.cyclic(tmpAtom)) {
                    if (tmpCandidate.contains(tmpAtom)) {
                        tmpCandidate.removeAtom(tmpAtom);
                        //The removal shifts the remaining indices!
                        j = j - 1;
                    }
                }
            }
            if (tmpCandidate.isEmpty()) {
                aCandidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
            }
            //if the candidate got unconnected by the removal of cycles, split the parts in separate candidates
            boolean tmpIsConnected = ConnectivityChecker.isConnected(tmpCandidate);
            if (!tmpIsConnected) {
                IAtomContainerSet tmpComponents = ConnectivityChecker.partitionIntoMolecules(tmpCandidate);
                for (IAtomContainer tmpComponent : tmpComponents.atomContainers()) {
                    aCandidateList.add(tmpComponent);
                }
                aCandidateList.remove(i);
                i = i - 1;
                continue;
            }
        }
    }

    /**
     * Alternative method to removing all cyclic atoms from the linear sugar candidates: Rejecting
     * every candidate completely that contains a cyclic atom. This method was deprecated because this way also connected
     * linear moieties get discarded.
     * Note: here, the given list is altered, unlike in some other methods! Therefore, the list is not returned again.
     * Note also that it is not checked whether the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param aCandidateList a list of linear sugar candidates from the same atom container object
     * @param aMolecule the molecule that is currently scanned for linear sugars to detect its circular sugars
     * @throws NullPointerException if any parameter is 'null'
     */
    @Deprecated
    protected void removeSugarCandidatesWithCyclicAtoms(List<IAtomContainer> aCandidateList,
                                                        IAtomContainer aMolecule)
            throws NullPointerException {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return;
        }
        Objects.requireNonNull(aMolecule, "Given molecule is 'null'.");
        int[][] tmpAdjList = GraphUtil.toAdjList(aMolecule);
        RingSearch tmpRingSearch = new RingSearch(aMolecule, tmpAdjList);
        boolean tmpMoleculeHasRings = tmpRingSearch.numRings() > 0;
        if (!tmpMoleculeHasRings) {
            //nothing to process
            return;
        }
        for (int i = 0; i < aCandidateList.size(); i++) {
            IAtomContainer tmpCandidate = aCandidateList.get(i);
            for (int j = 0; j < tmpCandidate.getAtomCount(); j++) {
                IAtom tmpAtom = tmpCandidate.getAtom(j);
                if (tmpRingSearch.cyclic(tmpAtom)) {
                    aCandidateList.remove(i);
                    //removal shifts the remaining indices
                    i = i - 1;
                    break;
                }
            }
        }
    }

    /**
     * Discards all linear sugar candidates that are too small or too big according to the current settings. Final step
     * of linear sugar detection. This step was introduced because the preceding steps may produce small 'fragments',
     * e.g. the hydroxy group of a circular sugar that was removed from a linear sugar candidate. These should be
     * filtered out. ALso, a very large linear sugar that does not consist of multiple subunits linked by ether, ester,
     * or peroxide bonds is considered too interesting to remove and should therefore also be filtered from the linear sugars
     * detected for removal. The 'size' of the linear sugar candidates is determined as their carbon atom count. The
     * set minimum and maximum sizes are inclusive.
     * The parameter list is not altered and a completely new list returned.
     *
     * @param aCandidateList a list of potential sugar substructures from the same atom container object
     * @return a new list of candidates where all too small and too big candidates have been filtered out
     * @throws NullPointerException if the given list is 'null'
     */
    protected List<IAtomContainer> removeTooSmallAndTooLargeCandidates(List<IAtomContainer> aCandidateList)
            throws NullPointerException {
        Objects.requireNonNull(aCandidateList, "Given list is 'null'.");
        if (aCandidateList.isEmpty()) {
            return aCandidateList;
        }
        List<IAtomContainer> tmpProcessedCandidates = new ArrayList<>(aCandidateList.size());
        for (IAtomContainer tmpCandidate : aCandidateList) {
            int tmpCarbonCount = 0;
            for (IAtom tmpAtom : tmpCandidate.atoms()) {
                String tmpSymbol = tmpAtom.getSymbol();
                if (tmpSymbol.equals("C")) {
                    tmpCarbonCount++;
                }
            }
            if (tmpCarbonCount >= this.linearSugarCandidateMinSizeSetting && tmpCarbonCount <= this.linearSugarCandidateMaxSizeSetting) {
                tmpProcessedCandidates.add(tmpCandidate);
            }
        }
        return tmpProcessedCandidates;
    }
    //</editor-fold>
    //</editor-fold>
}

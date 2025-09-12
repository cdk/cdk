/*
 * Copyright (c) 2025 Jonas Schaub <jonas.schaub@uni-jena.de>
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
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.isomorphism.DfPattern;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.ringsearch.RingSearch;
import org.openscience.cdk.smarts.SmartsPattern;
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
import java.util.Set;

/**
 * The Sugar Removal Utility (SRU) implements a generalized algorithm for
 * automated detection of circular and linear sugars in molecular structures
 * and their removal, as described in
 * <a href="https://doi.org/10.1186/s13321-020-00467-y">"Schaub, J., Zielesny, A., Steinbeck, C., Sorokina, M. Too sweet: cheminformatics for deglycosylation in natural products. J Cheminform 12, 67 (2020). https://doi.org/10.1186/s13321-020-00467-y"</a>.
 * It offers various functions to detect and remove sugar moieties with different
 * options. Example usage:
 * <pre>{@code
 * //prepare test molecule
 * SmilesParser smiPar = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * //COCONUT DB CNP0220816
 * IAtomContainer molecule = smiPar.parseSmiles("CC1=CC(OC2OC(CO)C(O)C(O)C2O)=C2C3=C(CCC3)C(=O)OC2=C1");
 * //instantiate sugar removal utility
 * SugarRemovalUtility sugarRemovalUtil = new SugarRemovalUtility(SilentChemObjectBuilder.getInstance());
 * //remove sugar moieties, note that this changes the molecule instance!
 * boolean sugarsWereRemoved = sugarRemovalUtil.removeCircularAndLinearSugars(molecule);
 * if (sugarsWereRemoved) {
 *     //saturate open valences where sugars were situated if needed
 *     AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
 *     CDKHydrogenAdder.getInstance(SilentChemObjectBuilder.getInstance()).addImplicitHydrogens(molecule);
 * }
 * }</pre>
 *
 * @author Jonas Schaub
 * @author Maria Sorokina
 */
public class SugarRemovalUtility {

    /**
     * Enum with options for how to determine whether a substructure that gets
     * disconnected from the molecule during the removal of a sugar moiety
     * should be preserved or can get removed along with the sugar.
     * <br>The set option plays a major role in discriminating terminal and
     * non-terminal sugar moieties. If only terminal
     * sugar moieties are removed from the molecule, any disconnected structure
     * resulting from a removal step must be too small to keep according to the
     * set preservation mode option and the set threshold and is cleared away.
     * If all the sugar moieties are to be removed from the given molecule
     * (including non-terminal ones), those disconnected structures that are too
     * small are only cleared once at the end of the routine.
     * <br>Also, the set preservation mode threshold interrelates with this
     * option. It specifies at least how many heavy
     * atoms or what minimum molecular weight a disconnected structure needs to
     * have to be preserved (depending on the set option).
     * <p>Important Note for further development: If an option is added here, it
     * needs to have a treatment in the method
     * {@link #isTooSmallToPreserve(IAtomContainer)}.
     */
    public enum PreservationMode {
        /**
         * Specifies that all structures should be preserved. Note that if this
         * option is combined with the removal of only terminal moieties, even
         * the smallest attached structure will prevent the removal of a sugar.
         * The most important consequence is that circular sugars with any
         * hydroxy groups will not be removed because these are not considered
         * as part of the sugar moiety.
         */
        ALL (0),

        /**
         * Specifies that whether a structure is worth preserving will be judged
         * by its heavy atom count. The default threshold to preserve a
         * structure is set to 5 heavy atoms (inclusive).
         */
        HEAVY_ATOM_COUNT (5),

        /**
         * Specifies that whether a structure is worth preserving will be judged
         * by its molecular weight. The default threshold to preserve a
         * structure is set to 60 Da (= 5 carbon atoms, inclusive).
         */
        MOLECULAR_WEIGHT (60);

        /**
         * Default preservation mode threshold for the respective option.
         */
        private final int defaultThreshold;

        /**
         * Constructor.
         *
         * @param aDefaultValue the default threshold to preserve a structure
         *                      for the respective option
         */
        PreservationMode(int aDefaultValue) {
            this.defaultThreshold = aDefaultValue;
        }

        /**
         * Returns the default threshold to preserve a structure (inclusive) for
         * this option.
         *
         * @return the default threshold
         */
        public int getDefaultThreshold() {
            return this.defaultThreshold;
        }
    }

    /**
     * Property key for index that is added to any IAtom object in a given
     * IAtomContainer object for internal unique identification of the
     * respective IAtom object. For internal use only.
     */
    public static final String INDEX_PROPERTY_KEY = "SUGAR_REMOVAL_UTILITY_UNIQUE_ATOM_INDEX";

    /**
     * Key for property that is added to IAtom objects that connect a spiro ring
     * system for identification and preservation of these atoms in the removal
     * process. For internal use only.
     */
    public static final String IS_SPIRO_ATOM_PROPERTY_KEY = "SUGAR_REMOVAL_UTILITY_IS_SPIRO_ATOM";

    /**
     * Key for property that is added to IAtom objects that belong to unconnected
     * parts of an input molecule that would be cleared away because it is too
     * small according to the set preservation mode for preservation of these
     * atoms in the removal process. For internal use only.
     */
    public static final String TOO_SMALL_DISCONNECTED_PART_TO_PRESERVE_PROPERTY_KEY = "SUGAR_REMOVAL_UTILITY_TOO_SMALL_DISCONNECTED_PART_TO_PRESERVE";

    /**
     * Linear sugar structures represented as SMILES codes. An input molecule is
     * scanned for these substructures for the detection of linear sugars. This
     * set consists of multiple aldoses, ketoses, and sugar alcohols with sizes
     * between 3 and 7 carbons. Additional structures can be added or specific
     * ones removed from the set at run-time using the respective methods.
     */
    protected static final String[] LINEAR_SUGARS_SMILES = {
            /*note: even though it would save time in the constructor to already
            sort for length decreasing here, the authors decided against it to
            keep this more readable and easier to inspect or extend.*/
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
     * Linear acidic sugar structures represented as SMILES codes. These can be
     * optionally added to the linear sugar structures used for initial
     * detection of linear sugars in an input molecule.
     */
    protected static final String[] LINEAR_ACIDIC_SUGARS_SMILES = {
            "C(C(CC(C(CO)O)O)O)(O)=O", //3-deoxyhexonic acid
            "CC(CC(CC(=O)O)O)O", //3,5-Dihydroxyhexanoic acid
            "O=C(O)CC(O)CC(=O)O", //3-hydroxypentanedioic acid
            "O=C(O)CCC(O)C(=O)O", //2-hydroxypentanedioic acid
            "C(C(C(CC(=O)O)O)O)O" //2-deoxypentonic acid
    };

    /**
     * Circular sugar structures represented as SMILES codes. The isolated rings
     * of an input molecule are matched with these structures for the detection
     * of circular sugars. The structures listed here only represent the
     * circular part of sugar rings (i.e. one oxygen atom and multiple carbon
     * atoms). Common exocyclic structures like hydroxy groups are not part of
     * the patterns and therefore not part of the detected circular sugar
     * moieties. The set includes tetrahydrofuran, tetrahydropyran, and oxepane
     * to match furanoses, pyranoses, and heptoses per default. It can be
     * configured at run-time using the respective methods.
     */
    protected static final String [] CIRCULAR_SUGARS_SMILES = {
            "C1CCOC1", //tetrahydrofuran to match all 5-membered sugar rings (furanoses)
            "C1CCOCC1", //tetrahydropyran to match all 6-membered sugar rings (pyranoses)
            "C1CCCOCC1" //oxepane to match all 7-membered sugar rings (heptoses)
    };

    /**
     * Default setting for whether only circular sugar moieties that are
     * attached to the parent structure or other sugar moieties via an
     * O-glycosidic bond should be detected and subsequently removed (default:
     * false).
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_ONLY_WITH_O_GLYCOSIDIC_BOND_DEFAULT = false;

    /**
     * Default setting for whether only terminal sugar moieties should be
     * removed, i.e. those that when removed do not cause a split of the
     * remaining molecular structure into two or more disconnected substructures
     * (default: true).
     */
    public static final boolean REMOVE_ONLY_TERMINAL_SUGARS_DEFAULT = true;

    /**
     * Default setting for how to determine whether a substructure that gets
     * disconnected from the molecule during the removal of a sugar moiety
     * should be preserved or can get removed along with the sugar. (default:
     * preserve all structures that consist of 5 or more heavy atoms). The set
     * option plays a major role in discriminating terminal and non-terminal
     * sugar moieties. The minimum value to reach for the respective
     * characteristic to judge by is set in an additional option and all enum
     * constants have their own default values. See the PreservationMode enum.
     */
    public static final PreservationMode PRESERVATION_MODE_DEFAULT = PreservationMode.HEAVY_ATOM_COUNT;

    /**
     * Default setting for whether detected circular sugar candidates must have
     * a sufficient number of attached, single-bonded exocyclic oxygen atoms in
     * order to be detected as a sugar moiety (default: true). The 'sufficient
     * number' is defined in another option / default setting.
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_ONLY_WITH_ENOUGH_EXOCYCLIC_OXYGEN_ATOMS_DEFAULT = true;

    /**
     * Default setting for the minimum ratio of attached exocyclic,
     * single-bonded oxygen atoms to the number of atoms in the candidate
     * circular sugar structure to reach in order to be classified as a sugar
     * moiety if the number of exocyclic oxygen atoms should be evaluated
     * (default: 0.5 so at a minimum 3 connected, exocyclic oxygen atoms for a
     * six-membered ring, for example).
     */
    public static final double EXOCYCLIC_OXYGEN_ATOMS_TO_ATOMS_IN_RING_RATIO_THRESHOLD_DEFAULT = 0.5;

    /**
     * Default setting for whether linear sugar structures that are part of a
     * ring should be detected (default: false). This setting is important for
     * e.g. macrocycles that contain sugars or pseudosugars.
     */
    public static final boolean DETECT_LINEAR_SUGARS_IN_RINGS_DEFAULT = false;

    /**
     * Default setting for the minimum number of carbon atoms a linear sugar
     * candidate must have in order to be detected as a sugar moiety (and
     * subsequently be removed, default: 4, inclusive).
     */
    public static final int LINEAR_SUGAR_CANDIDATE_MIN_SIZE_DEFAULT = 4;

    /**
     * Default setting for the maximum number of carbon atoms a linear sugar
     * candidate can have in order to be detected as a sugar moiety (and
     * subsequently be removed, default: 7, inclusive).
     */
    public static final int LINEAR_SUGAR_CANDIDATE_MAX_SIZE_DEFAULT = 7;

    /**
     * Default setting for whether to include the linear acidic sugar patterns
     * in the linear sugar structures used for initial detection of linear
     * sugars in a given molecule (default: false).
     */
    public static final boolean DETECT_LINEAR_ACIDIC_SUGARS_DEFAULT = false;

    /**
     * Default setting for whether to include spiro rings in the initial set of
     * detected rings considered for circular sugar detection (default: false).
     * If the option is turned on and a spiro sugar ring is removed, its atom
     * connecting it to another ring is preserved.
     */
    public static final boolean DETECT_SPIRO_RINGS_AS_CIRCULAR_SUGARS_DEFAULT = false;

    /**
     * Default setting for whether sugar-like rings that have keto groups should
     * also be detected as circular sugars (default: false). The general rule
     * specified in the original algorithm description is that every potential
     * sugar cycle with an exocyclic double or triple bond is excluded from
     * circular sugar detection. If this option is turned on, an exemption to
     * this rule is made for potential sugar cycles having keto groups. Also,
     * the double-bound oxygen atoms will then count for the number of connected
     * oxygen atoms and the algorithm will not regard how many keto groups are
     * attached to the cycle (might be only one, might be that all connected
     * oxygen atoms are double-bound). If this option is turned off (default),
     * every sugar-like cycle with an exocyclic double or triple bond will be
     * excluded from the detected circular sugars, as it is specified in the
     * original algorithm description.
     */
    public static final boolean DETECT_CIRCULAR_SUGARS_WITH_KETO_GROUPS_DEFAULT = false;

    /**
     * Daylight SMARTS pattern for matching ester bonds between linear sugars.
     * Defines an aliphatic carbon atom connected to a double-bonded oxygen atom
     * and a single-bonded oxygen atom that must not be in a ring and is
     * connected to another aliphatic carbon atom via a single bond. The oxygen
     * atom must not be in a ring to avoid breaking circular sugars.
     */
    public static final String ESTER_SMARTS_PATTERN = "[C](=O)-[O!R]-[C]";

    /**
     * Daylight SMARTS pattern for matching ether bonds between linear sugars.
     * Defines an aliphatic carbon atom connected via single bond to an oxygen
     * atom that must not be in a ring and is in turn connected to another
     * aliphatic carbon atom. The oxygen atom must not be in a ring to avoid
     * breaking circular sugars. This pattern also matches ester bonds which is
     * why esters must be detected and processed before ethers.
     */
    public static final String ETHER_SMARTS_PATTERN = "[C]-[O!R]-[C]";

    /**
     * Daylight SMARTS pattern for matching peroxide bonds between linear
     * sugars. Defines an aliphatic carbon atom connected via single bond to an
     * oxygen atom that must not be in a ring and is connected to another oxygen
     * atom of the same kind, followed by another aliphatic carbon atom. Even
     * tough it is highly unlikely for a peroxide bond to be in a ring, every
     * ring should be preserved.
     */
    public static final String PEROXIDE_SMARTS_PATTERN = "[C]-[O!R]-[O!R]-[C]";

    /**
     * Logger of this class.
     */
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(SugarRemovalUtility.class);

    /**
     * Chem object builder for parsing SMILES strings etc.
     */
    private final IChemObjectBuilder builder;

    /**
     * Linear sugar structures parsed into atom containers. Not used for
     * detection but parsed into patterns after sorting.
     */
    private List<IAtomContainer> linearSugarStructuresList;

    /**
     * Circular sugar structures parsed into atom containers. Used for detection
     * via universal isomorphism tester.
     */
    private List<IAtomContainer> circularSugarStructuresList;

    /**
     * Patterns of linear sugar structures to detect linear sugar moieties in
     * the given molecules.
     */
    private List<DfPattern> linearSugarPatternsList;

    /**
     * Linear acidic sugar structures parsed into atom containers. This list
     * serves as reference to be able to add and remove these structures from
     * the linear sugar structures when the respective setting changes.
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
    private PreservationMode preservationModeSetting;

    /**
     * Threshold for the characteristic of an unconnected fragment set in the
     * preservation mode to judge whether to preserve it or discard it.
     */
    private int preservationModeThresholdSetting;

    /**
     * Include number/ratio of connected, exocyclic oxygen atoms setting.
     */
    private boolean detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting;

    /**
     * Minimum ratio of connected, exocyclic oxygen atoms to the number of atoms
     * in the candidate sugar ring.
     */
    private double exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting;

    /**
     * Remove linear sugars in circular structures setting.
     */
    private boolean detectLinearSugarsInRingsSetting;

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

    /**
     * Sole constructor of this class. All settings are set to their default
     * values (see public static constants or enquire via get/is methods). To
     * change these settings, use the respective 'setXY()' methods.
     *
     * @param builder IChemObjectBuilder for i.a. parsing SMILES strings  of
     *                sugar patterns into atom containers
     */
    public SugarRemovalUtility(IChemObjectBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("Given chem object builder is null.");
        }
        this.builder = builder;
        /*method setDetectLinearAcidicSugarsSetting() called in
        restoreDefaultSettings() checks whether the setting has changed, so an
        initial value must be provided; If the default is true, the initial
        value must be false in order to add the linear acidic sugar patterns to
        the linear sugar patterns. If the default is false, the initial value
        should also be false.*/
        this.detectLinearAcidicSugarsSetting = false;
        this.restoreDefaultSettings();
    }

    /**
     * Returns a list of atom containers representing the linear sugar
     * structures an input molecule is scanned for in linear sugar detection.
     * The returned list represents the current state of this list, i.e.
     * externally added structures are included, externally removed structures
     * not, and the linear acidic sugar structures are only included if the
     * respective option is activated.
     * <br>Note: do not change the returned list but use
     * {@link #addLinearSugarToPatternsList(IAtomContainer)} and
     * {@link #removeLinearSugarFromPatternsList(IAtomContainer)} to modify it
     * (or the corresponding SMILES string-based methods) because these sync
     * updates with the actually used list of DfPattterns.
     *
     * @return a list of atom containers representing a current snapshot of
     *         the linear sugar patterns used for detection
     */
    public List<IAtomContainer> getLinearSugarPatternsList() {
        return this.linearSugarStructuresList;
    }

    /**
     * Returns a list of atom containers representing the circular sugar
     * structures an input molecule is scanned for in circular sugar detection.
     * The returned list represents the current state of this list, i.e.
     * externally added structures are included, externally removed structures
     * are not.
     * <br>Note: do not change the returned list but use
     * {@link #addCircularSugarToPatternsList(IAtomContainer)} and
     * {@link #removeCircularSugarFromPatternsList(IAtomContainer)} to modify it
     * (or the corresponding SMILES string-based methods) because these sync
     * updates with the actually used list of DfPattterns.
     *
     * @return a list of atom containers representing a current snapshot of
     *         the circular sugar patterns used for detection
     */
    public List<IAtomContainer> getCircularSugarPatternsList() {
        return this.circularSugarStructuresList;
    }

    /**
     * Specifies whether only circular sugar moieties that are attached to the
     * parent structure or other sugar moieties via an O-glycosidic bond should
     * be detected and subsequently removed.
     *
     * @return true if only circular sugar moieties connected via a glycosidic
     *         bond are removed according to the current settings
     */
    public boolean areOnlyCircularSugarsWithOGlycosidicBondDetected() {
        return this.detectCircularSugarsOnlyWithOGlycosidicBondSetting;
    }

    /**
     * Specifies whether only terminal sugar moieties should be removed, i.e.
     * those that when removed do not cause a split of the remaining molecular
     * structure into two or more disconnected substructures.
     *
     * @return true if only terminal sugar moieties are removed according to the
     *         current settings
     */
    public boolean areOnlyTerminalSugarsRemoved() {
        return this.removeOnlyTerminalSugarsSetting;
    }

    /**
     * Returns the current setting for how to determine whether a substructure
     * that gets disconnected from the molecule during the removal of a sugar
     * moiety should be preserved or can get removed along with the sugar. This
     * can e.g. be judged by its heavy atom count or its molecular weight, or it
     * can be specified that all structures are to be preserved. If too small /
     * too light structures are discarded, an additional threshold is specified
     * in the preservation mode threshold setting that the structures have to
     * reach in order to be preserved (i.e. to be judged 'big/heavy enough').
     *
     * @return a PreservationMode enum object representing the current setting
     */
    public PreservationMode getPreservationModeSetting() {
        return this.preservationModeSetting;
    }

    /**
     * Returns the current threshold of e.g. molecular weight or heavy atom
     * count (depending on the currently set preservation mode) a substructure
     * that gets disconnected from the molecule by the removal of a sugar moiety
     * has to reach in order to be preserved and not discarded.
     *
     * @return an integer specifying the currently set threshold (either
     *         specified in Da or number of heavy atoms)
     */
    public int getPreservationModeThresholdSetting() {
        return this.preservationModeThresholdSetting;
    }

    /**
     * Specifies whether detected circular sugar candidates must have a
     * sufficient number of attached exocyclic oxygen atoms in order to be
     * detected as a sugar moiety. If this option is set, the circular sugar
     * candidates have to reach an additionally specified minimum ratio of said
     * oxygen atoms to the number of atoms in the respective ring in order to be
     * seen as a sugar ring and being subsequently removed. See exocyclic oxygen
     * atoms to atoms in ring ratio threshold setting.
     *
     * @return true, if the ratio of attached, exocyclic, single-bonded oxygen
     *         atoms to the number of atoms in the candidate sugar ring is evaluated at
     *         circular sugar detection according to the current settings
     */
    public boolean areOnlyCircularSugarsWithEnoughExocyclicOxygenAtomsDetected() {
        return this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting;
    }

    /**
     * Returns the currently set minimum ratio of attached, exocyclic,
     * single-bonded oxygen atoms to the number of atoms in the candidate
     * circular sugar structure to reach in order to be classified as a sugar
     * moiety if the number of exocyclic oxygen atoms should be evaluated.
     *
     * @return the minimum ratio of attached oxygen atoms to the number of atoms
     *         in the sugar ring; A value of e.g. 0.5 means that a six-membered sugar
     *         ring needs at least 3 attached oxygen atoms to be classified as a
     *         circular sugar moiety
     */
    public double getExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting() {
        return this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting;
    }

    /**
     * Specifies whether linear sugar structures that are part of a ring should
     * be detected according to the current settings. This setting is important
     * for e.g. macrocycles that contain sugars or pseudosugars.
     * <br>Note that potential circular sugar candidates (here always including
     * spiro sugar rings also) are filtered from
     * linear sugar candidates, even with this setting turned on.
     *
     * @return true if linear sugars in rings are detected and removed with the
     *         current settings
     */
    public boolean areLinearSugarsInRingsDetected() {
        return this.detectLinearSugarsInRingsSetting;
    }

    /**
     * Returns the currently set minimum number of carbon atoms a linear sugar
     * candidate must have in order to be detected as a sugar moiety (and
     * subsequently be removed).
     *
     * @return the set minimum carbon atom count of detected linear sugars
     * (inclusive)
     */
    public int getLinearSugarCandidateMinSizeSetting() {
        return this.linearSugarCandidateMinSizeSetting;
    }

    /**
     * Returns the currently set maximum number of carbon atoms a linear sugar
     * candidate can have in order to be detected as a sugar moiety (and
     * subsequently be removed).
     *
     * @return the set maximum carbon atom count of detected linear sugars
     *         (inclusive)
     */
    public int getLinearSugarCandidateMaxSizeSetting() {
        return this.linearSugarCandidateMaxSizeSetting;
    }

    /**
     * Specifies whether linear acidic sugar patterns are currently included in
     * the linear sugar structures used for initial detection of linear sugars
     * in a given molecule.
     *
     * @return true if acidic sugars are detected
     */
    public boolean areLinearAcidicSugarsDetected() {
        return this.detectLinearAcidicSugarsSetting;
    }

    /**
     * Specifies whether spiro rings are included in the initial set of detected
     * rings considered for circular sugar detection.
     * <br>Note for linear sugar detection: Here, the spiro rings will always be
     * filtered along with the potential
     * circular sugar candidates.
     *
     * @return true if spiro rings can be detected as circular sugars with the
     *         current settings
     */
    public boolean areSpiroRingsDetectedAsCircularSugars() {
        return this.detectSpiroRingsAsCircularSugarsSetting;
    }

    /**
     * Specifies whether potential sugar cycles with keto groups are detected in
     * circular sugar detection. The general rule specified in the original
     * algorithm description is that every potential sugar cycle with an
     * exocyclic double or triple bond is excluded from circular sugar
     * detection. If this option is turned on, an exemption to this rule is made
     * for potential sugar cycles having keto groups. Also, the double-bound
     * oxygen atoms will then count for the number of connected oxygen atoms and
     * the algorithm will not regard how many keto groups are attached to the
     * cycle (might be only one, might be that all connected oxygen atoms are
     * double-bound). If this option is turned off, every sugar-like cycle with
     * an exocyclic double or triple bond will be excluded from the detected
     * circular sugars, as it is specified in the original algorithm
     * description.
     *
     * @return true if potential sugar cycles having keto groups are detected in
     *         circular sugar detection
     */
    public boolean areCircularSugarsWithKetoGroupsDetected() {
        return this.detectCircularSugarsWithKetoGroupsSetting;
    }

    /**
     * Allows to add an additional sugar ring to the list of circular sugar
     * structures an input molecule is scanned for in circular sugar detection.
     * The given structure must not be isomorphic to the already present ones,
     * and it must contain exactly one isolated ring without any exocyclic
     * moieties because only the isolated rings of an input structure are
     * matched with the circular sugar patterns.
     *
     * @param circularSugar an atom container representing only one isolated
     *                      sugar ring
     * @return true if the addition was successful; false if the given atom
     * container is empty or does represent a molecule that contains no isolated
     * ring, more than one isolated ring, consists of more structures than one
     * isolated ring or is isomorphic to a circular sugar structure already
     * present
     */
    public boolean addCircularSugarToPatternsList(IAtomContainer circularSugar) {
        if (circularSugar == null) {
            throw new NullPointerException("Given atom container is 'null'");
        }
        if (circularSugar.isEmpty()) {
            return false;
        }
        int[][] adjList = GraphUtil.toAdjList(circularSugar);
        RingSearch ringSearch = new RingSearch(circularSugar, adjList);
        List<IAtomContainer> isolatedRingFragments = ringSearch.isolatedRingFragments();
        int size = isolatedRingFragments.size();
        if (size != 1) {
            return false;
        }
        UniversalIsomorphismTester univIsomorphTester = new UniversalIsomorphismTester();
        boolean isolatedRingMatchesEntireInputStructure = false;
        IAtomContainer isolatedRing = isolatedRingFragments.get(0);
        try {
            isolatedRingMatchesEntireInputStructure = univIsomorphTester.isIsomorph(circularSugar, isolatedRing);
        } catch (CDKException cdkException) {
            SugarRemovalUtility.LOGGER.warn(cdkException);
        }
        if (!isolatedRingMatchesEntireInputStructure) {
            return false;
        }
        for (IAtomContainer sugar : this.circularSugarStructuresList) {
            boolean isIsomorphic;
            try {
                isIsomorphic = univIsomorphTester.isIsomorph(sugar, circularSugar);
            } catch (CDKException cdkException) {
                SugarRemovalUtility.LOGGER.warn(cdkException);
                return false;
            }
            if (isIsomorphic) {
                return false;
            }
        }

        boolean additionWasSuccessful;
        try {
            additionWasSuccessful = this.circularSugarStructuresList.add(circularSugar);
        } catch (Exception exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            additionWasSuccessful = false;
        }
        if (additionWasSuccessful) {
            Comparator<IAtomContainer> comparator = new AtomContainerComparator().reversed();
            //note: this can throw various exceptions, but they should not appear here
            this.circularSugarStructuresList.sort(comparator);
        }
        return additionWasSuccessful;
    }

    /**
     * Allows to add an additional sugar ring (represented as a SMILES string)
     * to the list of circular sugar structures an input molecule is scanned for
     * in circular sugar detection. The given structure must not be isomorphic
     * to the already present ones, and it must contain exactly one isolated
     * ring without any exocyclic moieties because only the isolated rings of an
     * input structure are matched with the circular sugar patterns.
     *
     * @param smilesCode a SMILES code representation of a molecule consisting
     *                   of only one isolated sugar ring
     * @return true if the addition was successful; false if the given SMILES
     * string is empty or does represent a molecule that contains no isolated
     * ring, more than one isolated ring, consists of more structures than one
     * isolated ring, is isomorphic to a circular sugar structure already
     * present or if the given SMILES string cannot be parsed into a molecular
     * structure
     */
    public boolean addCircularSugarToPatternsList(String smilesCode) {
        if (smilesCode == null) {
            throw new NullPointerException("Given SMILES code is 'null'");
        }
        if (smilesCode.isEmpty()) {
            return false;
        }
        SmilesParser smiPar = new SmilesParser(this.builder);
        IAtomContainer ringSugar;
        try {
            ringSugar = smiPar.parseSmiles(smilesCode);
        } catch (InvalidSmilesException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            return false;
        }
        return this.addCircularSugarToPatternsList(ringSugar);
    }

    /**
     * Allows to add an additional linear sugar to the list of linear sugar
     * structures an input molecule is scanned for in linear sugar detection.
     * The given structure must not be isomorphic to the already present ones or
     * the patterns for circular sugars.
     * <br>Note: If the given structure contains cycles, the
     * option to detect linear sugars in rings needs to be enabled to detect its
     * matches entirely. Otherwise, all circular substructures of the 'linear
     * sugars' will not be detected.
     * <br>Additional note: If the given structure is isomorphic to a default
     * linear acidic sugar pattern, it may be added here when the option to
     * detect these structures is turned off but will be removed from the
     * pattern list if the option is turned on and off again after this
     * addition.
     *
     * @param linearSugar an atom container representing a molecular structure
     *                    to search for at linear sugar detection
     * @return true if the addition was successful; false if the given atom
     * container is empty or is isomorphic to a linear sugar structure already
     * present or a circular sugar pattern
     */
    public boolean addLinearSugarToPatternsList(IAtomContainer linearSugar) {
        if (linearSugar == null) {
            throw new NullPointerException("Given atom container is 'null'");
        }
        if (linearSugar.isEmpty()) {
            return false;
        }
        //note: no check for linearity here to allow adding of structures that contain rings, e.g. amino acids
        UniversalIsomorphismTester univIsomorphTester = new UniversalIsomorphismTester();
        for (IAtomContainer sugar : this.linearSugarStructuresList) {
            boolean isIsomorphic;
            try {
                isIsomorphic = univIsomorphTester.isIsomorph(sugar, linearSugar);
            } catch (CDKException cdkException) {
                SugarRemovalUtility.LOGGER.warn(cdkException);
                return false;
            }
            if (isIsomorphic) {
                return false;
            }
        }
        for (IAtomContainer sugar : this.circularSugarStructuresList) {
            boolean isIsomorphic;
            try {
                isIsomorphic = univIsomorphTester.isIsomorph(sugar, linearSugar);
            } catch (CDKException cdkException) {
                SugarRemovalUtility.LOGGER.warn(cdkException);
                return false;
            }
            if (isIsomorphic) {
                return false;
            }
        }

        boolean additionWasSuccessful;
        try {
            additionWasSuccessful = this.linearSugarStructuresList.add(linearSugar);
        } catch (Exception exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            additionWasSuccessful = false;
        }
        if (additionWasSuccessful) {
            this.updateLinearSugarPatterns();
        }
        return additionWasSuccessful;
    }

    /**
     * Allows to add an additional linear sugar (represented as SMILES string)
     * to the list of linear sugar structures an input molecule is scanned for
     * in linear sugar detection. The given structure must not be isomorphic to
     * the already present ones or the patterns for circular sugars.
     * <br>Note: If the given structure contains cycles, the
     * option to detect linear sugars in rings needs to be enabled to detect its
     * matches entirely. Otherwise, all circular substructures of the 'linear
     * sugars' will not be detected.
     * <br>Additional note: If the given structure is isomorphic to a default
     * linear acidic sugar pattern, it may be added here when the option to
     * detect these structures is turned off but will be removed from the
     * pattern list if the option is turned on and off again after this
     * addition.
     *
     * @param smilesCode a SMILES code representation of a molecular structure
     *                   to search for
     * @return true if the addition was successful; false if the given SMILES
     * string is empty or does represent a molecule that is isomorphic to a
     * linear sugar structure already present or a circular sugar pattern or if
     * it cannot be parsed into a molecular structure
     */
    public boolean addLinearSugarToPatternsList(String smilesCode) {
        if (smilesCode == null) {
            throw new NullPointerException("Given SMILES code is 'null'");
        }
        if (smilesCode.isEmpty()) {
            return false;
        }
        SmilesParser smiPar = new SmilesParser(this.builder);
        IAtomContainer linearSugar;
        try {
            linearSugar = smiPar.parseSmiles(smilesCode);
        } catch (InvalidSmilesException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            return false;
        }
        return this.addLinearSugarToPatternsList(linearSugar);
    }

    /**
     * Allows to remove a sugar ring pattern (represented as SMILES string) from
     * the list of circular sugar structures an input molecule is scanned for in
     * circular sugar detection. The given character string must be a valid
     * SMILES notation and be isomorphic to one of the currently used structure
     * patterns. Example usage: Pass the argument "C1CCOC1" (tetrahydrofuran) to
     * stop detecting furanoses in the circular sugar detection algorithm.
     *
     * @param smilesCode a SMILES code representation of a structure present in
     *                   the circular sugar pattern list
     * @return true if the removal was successful; false if the given SMILES
     * string is empty or cannot be parsed into a molecule or the given
     * structure cannot be found in the circular sugar pattern list
     */
    public boolean removeCircularSugarFromPatternsList(String smilesCode) {
        if (smilesCode == null) {
            throw new NullPointerException("Given SMILES code is 'null'");
        }
        SmilesParser smiPar = new SmilesParser(this.builder);
        IAtomContainer circularSugar;
        try {
            circularSugar = smiPar.parseSmiles(smilesCode);
        } catch (InvalidSmilesException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            return false;
        }
        return this.removeCircularSugarFromPatternsList(circularSugar);
    }

    /**
     * Allows to remove a sugar ring from the list of circular sugar structures
     * an input molecule is scanned for in circular sugar detection. The given
     * molecule must be isomorphic to one of the currently used structure
     * patterns. Example usage: Pass an atom container object representing the
     * structure of tetrahydrofuran to stop detecting furanoses in the circular
     * sugar detection algorithm.
     *
     * @param circularSugar a molecule isomorphic to a structure present in the
     *                      circular sugar pattern list
     * @return true if the removal was successful; false if the given atom
     * container is empty or its structure is not isomorphic to a circular sugar
     * pattern structure in use
     */
    public boolean removeCircularSugarFromPatternsList(IAtomContainer circularSugar) {
        if (circularSugar == null) {
            throw new NullPointerException("Given atom container is 'null'");
        }
        if (circularSugar.isEmpty()) {
            return false;
        }
        UniversalIsomorphismTester univIsomorphTester = new UniversalIsomorphismTester();
        boolean isIsomorphic = false;
        boolean wasRemovalSuccessful = false;
        for (IAtomContainer sugar : this.circularSugarStructuresList) {
            try {
                isIsomorphic = univIsomorphTester.isIsomorph(sugar, circularSugar);
            } catch (CDKException cdkException) {
                SugarRemovalUtility.LOGGER.warn(cdkException);
                return false;
            }
            if (isIsomorphic) {
                try {
                    wasRemovalSuccessful = this.circularSugarStructuresList.remove(sugar);
                } catch (Exception exception) {
                    SugarRemovalUtility.LOGGER.warn(exception);
                    return false;
                }
                break;
            }
        }
        if (!isIsomorphic) {
            return false;
        } else {
            if (wasRemovalSuccessful) {
                Comparator<IAtomContainer> comparator = new AtomContainerComparator().reversed();
                //note: this can throw various exceptions, but they should not appear here
                this.circularSugarStructuresList.sort(comparator);
            }
            return wasRemovalSuccessful;
        }
    }

    /**
     * Allows to remove a linear sugar pattern (represented as SMILES string)
     * from the list of linear sugar structures an input molecule is scanned for
     * in linear sugar detection. The given character string must be a valid
     * SMILES notation and be isomorphic to one of the currently used structure
     * patterns. Example usage: Pass the argument "C(C(C=O)O)O" (aldotriose) to
     * stop detecting such small aldoses in the linear sugar detection
     * algorithm. Please note that adjusting the linear sugar candidate minimum
     * and maximum sizes can be more straightforward than removing patterns
     * here.
     * <br>Note: If the linear acidic sugars are currently included in the
     * linear sugar pattern structures, individual structures of this group can
     * be removed here.
     *
     * @param smilesCode a SMILES code representation of a structure present in
     *                   the linear sugar pattern list
     * @return true if the removal was successful; false if the given SMILES
     * string is empty or cannot be parsed into a molecule or the given
     * structure cannot be found in the linear sugar pattern list
     */
    public boolean removeLinearSugarFromPatternsList(String smilesCode) {
        if (smilesCode == null) {
            throw new NullPointerException("Given SMILES code is 'null'");
        }
        if (smilesCode.isEmpty()) {
            return false;
        }
        SmilesParser smiPar = new SmilesParser(this.builder);
        IAtomContainer linearSugar;
        try {
            linearSugar = smiPar.parseSmiles(smilesCode);
        } catch (InvalidSmilesException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            return false;
        }
        return this.removeLinearSugarFromPatternsList(linearSugar);
    }

    /**
     * Allows to remove a linear sugar pattern from the list of linear sugar
     * structures an input molecule is scanned for in linear sugar detection.
     * The given molecule must be isomorphic to one of the currently used
     * structure patterns. Example usage: Pass an atom container object
     * representing the structure of aldotriose to stop detecting such small
     * aldoses in the linear sugar detection algorithm. Please note that
     * adjusting the linear sugar candidate minimum and maximum sizes can be
     * more straightforward than removing patterns here.
     * <br>Note: If the linear acidic sugars are currently included in the
     * linear sugar pattern structures, individual structures of this group can
     * be removed here.
     *
     * @param linearSugar a molecule isomorphic to a structure present in the
     *                    linear sugar pattern list
     * @return true if the removal was successful; false if the given atom
     * container is empty or its structure is not isomorphic to a linear sugar
     * pattern structure in use
     */
    public boolean removeLinearSugarFromPatternsList(IAtomContainer linearSugar) {
        if (linearSugar == null) {
            throw new NullPointerException("Given atom container is 'null'");
        }
        if (linearSugar.isEmpty()) {
            return false;
        }
        UniversalIsomorphismTester univIsomorphTester = new UniversalIsomorphismTester();
        boolean isIsomorphic = false;
        boolean wasRemovalSuccessful = false;
        for (IAtomContainer sugar : this.linearSugarStructuresList) {
            try {
                isIsomorphic = univIsomorphTester.isIsomorph(sugar, linearSugar);
            } catch (CDKException cdkException) {
                SugarRemovalUtility.LOGGER.warn(cdkException);
                return false;
            }
            if (isIsomorphic) {
                try {
                    wasRemovalSuccessful = this.linearSugarStructuresList.remove(sugar);
                } catch (Exception exception) {
                    SugarRemovalUtility.LOGGER.warn(exception);
                    return false;
                }
                break;
            }
        }
        if (!isIsomorphic) {
            return false;
        } else {
            if (wasRemovalSuccessful) {
                this.updateLinearSugarPatterns();
            }
            return wasRemovalSuccessful;
        }
    }

    /**
     * Clears all the circular sugar structures an input molecule is scanned for
     * in circular sugar detection.
     */
    public void clearCircularSugarPatternsList() {
        try {
            this.circularSugarStructuresList.clear();
        } catch (UnsupportedOperationException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            this.circularSugarStructuresList = new ArrayList<>(SugarRemovalUtility.CIRCULAR_SUGARS_SMILES.length);
        }
    }

    /**
     * Clears all the linear sugar structures an input molecule is scanned for
     * in linear sugar detection. If the detection of linear acidic sugars is
     * turned on, it is turned off in this method and these structures are also
     * cleared from the linear sugar patterns.
     */
    public void clearLinearSugarPatternsList() {
        try {
            if (this.detectLinearAcidicSugarsSetting) {
                this.setDetectLinearAcidicSugarsSetting(false);
            }
            this.linearSugarStructuresList.clear();
            this.linearSugarPatternsList.clear();
        } catch (UnsupportedOperationException exception) {
            SugarRemovalUtility.LOGGER.warn(exception);
            this.detectLinearAcidicSugarsSetting = false;
            this.linearSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
            this.linearSugarPatternsList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        }
    }

    /**
     * Sets the option to only detect (and subsequently remove) circular sugar
     * moieties that are attached to the parent structure or other sugar
     * moieties via an O-glycosidic bond.
     *
     * @param bool true, if only circular sugar moieties connected via a
     *             glycosidic bond should be detected (and removed)
     */
    public void setDetectCircularSugarsOnlyWithOGlycosidicBondSetting(boolean bool) {
        this.detectCircularSugarsOnlyWithOGlycosidicBondSetting = bool;
    }

    /**
     * Sets the option to remove only terminal sugar moieties, i.e. those that
     * when removed do not cause a split of the remaining molecular structure
     * into two or more disconnected substructures.
     *
     * @param bool true, if only terminal sugar moieties should be removed
     */
    public void setRemoveOnlyTerminalSugarsSetting(boolean bool) {
        this.removeOnlyTerminalSugarsSetting = bool;
    }

    /**
     * Sets the preservation mode for structures that get disconnected by sugar
     * removal and the preservation mode threshold is set to the default value
     * of the given enum constant. The preservation mode option specifies how to
     * determine whether a substructure that gets disconnected from the molecule
     * during the removal of a sugar moiety should be preserved or can get
     * removed along with the sugar. This can e.g. be judged by its heavy atom
     * count or its molecular weight, or it can be specified that all structures
     * are to be preserved. The available options can be selected from the
     * PreservationMode enum. If too small / too light structures are discarded,
     * an additional threshold is specified in the preservation mode threshold
     * setting that the structures have to reach in order to be preserved (i.e.
     * to be judged 'big/heavy enough'). This threshold is set to the default
     * value of the given enum constant in this method.
     * <br>Note that if the option "ALL" is combined with the removal of only
     * terminal moieties, even the smallest
     * attached structure will prevent the removal of a sugar. The most
     * important consequence is that circular sugars with any hydroxy groups
     * will not be removed because these are not considered as part of the sugar
     * moiety.
     *
     * @param option the selected preservation mode option
     */
    public void setPreservationModeSetting(PreservationMode option) {
        if (option == null) {
            throw new NullPointerException("Given mode is 'null'.");
        }
        this.preservationModeSetting = option;
        this.preservationModeThresholdSetting = this.preservationModeSetting.getDefaultThreshold();
    }

    /**
     * Sets the preservation mode threshold, i.e. the molecular weight or heavy
     * atom count (depending on the currently set preservation mode) a
     * substructure that gets disconnected from the molecule during the removal
     * of a sugar moiety has to reach in order to be kept and not removed along
     * with the sugar. If the preservation mode is set to "HEAVY_ATOM_COUNT",
     * the threshold is interpreted as the needed minimum number of heavy atoms
     * and if it is set to "MOLECULAR_WEIGHT", the threshold is interpreted as
     * minimum molecular weight in Da.
     * <br>Notes: A threshold of zero can be set here, but it is recommended to
     * choose the preservation mode "ALL" instead.
     * On the other hand, if the preservation mode is set to "ALL", this
     * threshold is automatically set to zero.
     *
     * @param threshold the new threshold
     */
    public void setPreservationModeThresholdSetting(int threshold) {
        this.preservationModeThresholdSetting = threshold;
    }

    /**
     * Sets the option to only detect (and subsequently remove) circular sugars
     * that have a sufficient number of attached, exocyclic, single-bonded
     * oxygen atoms. If this option is set, the circular sugar candidates have
     * to reach an additionally specified minimum ratio of said oxygen atoms to
     * the number of atoms in the respective ring in order to be seen as a sugar
     * ring and being subsequently removed. See exocyclic oxygen atoms to atoms
     * in ring ratio threshold setting. If this option is re-activated, the
     * previously set threshold is used again or the default value if no custom
     * threshold has been set before.
     *
     * @param bool true, if the ratio of attached, exocyclic, single-bonded
     *             oxygen atoms to the number of atoms in the candidate sugar
     *             ring should be evaluated at circular sugar detection
     */
    public void setDetectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting(boolean bool) {
        this.detectCircularSugarsOnlyWithEnoughExocyclicOxygenAtomsSetting = bool;
    }

    /**
     * Sets the minimum ratio of attached, exocyclic, single-bonded oxygen atoms
     * to the number of atoms in the candidate circular sugar structure to reach
     * in order to be classified as a sugar moiety if the number of exocyclic
     * oxygen atoms should be evaluated.
     * <br>A ratio of e.g. 0.5 means that a six-membered candidate sugar ring
     * needs to have at least 3 attached, exocyclic
     * single-bonded oxygen atoms in order to be classified as a circular sugar.
     * <br>A zero value can be given if the option to remove only sugar rings
     * with a sufficient number of exocyclic
     * oxygen atoms is activated, but it is recommended to turn this option of
     * instead.
     * <br>Note: The normally present oxygen atom within a sugar ring is
     * included in the number of ring atoms. So setting
     * the threshold to 1.0 implies that at least one of the carbon atoms in the
     * ring has two attached oxygen atoms. In general, the threshold can be set
     * to values higher than 1.0 or even to negative values, but it does not
     * make a lot of sense.
     *
     * @param threshold the new ratio threshold
     * @return true if updating the value was successful; false if the given number
     * is infinite, 'NaN', or smaller than 0
     */
    public boolean setExocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting(double threshold) {
        //false for NaN and infinity arguments
        boolean isFinite = Double.isFinite(threshold);
        boolean isNegative = (threshold < 0);
        if (!isFinite || isNegative) {
            return false;
        }
        this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting = threshold;
        return true;
    }

    /**
     * Sets the option to detect linear sugar structures that are part of a
     * ring. This setting is important for e.g. macrocycles that contain sugars
     * or pseudosugars.
     * <br>Note that potential circular sugar candidates (here always including
     * spiro sugar rings also) are filtered from
     * linear sugar candidates, even with this setting turned on.
     *
     * @param bool true, if linear sugar structures that are part of a ring
     *             should be detected (and removed)
     */
    public void setDetectLinearSugarsInRingsSetting(boolean bool) {
        this.detectLinearSugarsInRingsSetting = bool;
    }

    /**
     * Sets the minimum number of carbon atoms a linear sugar candidate must
     * have in order to be detected as a sugar moiety (and subsequently be
     * removed). This minimum is inclusive and does not affect the initial
     * detection of linear sugars. Only at the end of the algorithm, linear
     * sugar candidates that are too small are discarded.
     * <br>Note: It is not tested whether the given minimum size is actually
     * smaller than the set maximum size to allow
     * a user-friendly adjustment of these parameters without having to adhere
     * to a certain order of operations.
     *
     * @param minSize the new minimum size (inclusive) of linear sugars
     *                detected, interpreted as carbon atom count
     */
    public void setLinearSugarCandidateMinSizeSetting(int minSize) {
        this.linearSugarCandidateMinSizeSetting = minSize;
    }

    /**
     * Sets the maximum number of carbon atoms a linear sugar candidate can have
     * in order to be detected as a sugar moiety (and subsequently be removed).
     * This maximum is inclusive and does not affect the initial detection of
     * linear sugars. Only at the end of the algorithm, linear sugar candidates
     * that are too big are discarded.
     * <br>Note: It is not tested whether the given maximum size is actually
     * greater than the set minimum size to allow
     * a user-friendly adjustment of these parameters without having to adhere
     * to a certain order of operations.
     *
     * @param maxSize the new maximum size (inclusive) of linear sugars
     *                detected, interpreted as carbon atom count
     */
    public void setLinearSugarCandidateMaxSizeSetting(int maxSize) {
        this.linearSugarCandidateMaxSizeSetting = maxSize;
    }

    /**
     * Sets the option to include linear acidic sugar patterns in the linear
     * sugar structures used for initial detection of linear sugars in a given
     * molecule. If the option is turned on, the linear acidic sugar patterns
     * are added to the linear sugar patterns list and can be retrieved and
     * configured in the same way as the 'normal' linear sugar patterns. If the
     * option is turned off, they are all removed again from the linear sugar
     * patterns list.
     *
     * @param bool true, if linear acidic sugar patterns should be included in
     *             the linear sugar structures used for initial detection of
     *             linear sugars
     */
    public void setDetectLinearAcidicSugarsSetting(boolean bool) {
        boolean hasSettingChanged = this.detectLinearAcidicSugarsSetting != bool;
        this.detectLinearAcidicSugarsSetting = bool;
        if (hasSettingChanged) {
            if (this.detectLinearAcidicSugarsSetting) {
                for (IAtomContainer linearAcidicSugar : this.linearAcidicSugarStructuresList) {
                    this.addLinearSugarToPatternsList(linearAcidicSugar);
                }
            } else {
                for (IAtomContainer linearAcidicSugar : this.linearAcidicSugarStructuresList) {
                    this.removeLinearSugarFromPatternsList(linearAcidicSugar);
                }
            }
        }
    }

    /**
     * Sets the option to include spiro rings in the initial set of detected
     * rings considered for circular sugar detection. If the option is turned
     * on, spiro atoms connected two spiro rings will be protected if a spiro
     * sugar ring is removed. In the opposite case, spiro rings will be filtered
     * from the set of isolated cycles detected in the given molecule.
     * <br>Note for linear sugar detection: Here, the spiro rings will always be
     * filtered along with the potential
     * circular sugar candidates.
     *
     * @param bool true, if spiro rings should be detectable as circular sugars
     */
    public void setDetectSpiroRingsAsCircularSugarsSetting(boolean bool) {
        this.detectSpiroRingsAsCircularSugarsSetting = bool;
    }

    /**
     * Sets the option to detect potential sugar cycles with keto groups as
     * circular sugars in circular sugar detection. The general rule specified
     * in the original algorithm description is that every potential sugar cycle
     * with an exocyclic double or triple bond is excluded from circular sugar
     * detection. If this option is turned on, an exemption to this rule is made
     * for potential sugar cycles having keto groups. Also, the double-bound
     * oxygen atoms will then count for the number of connected oxygen atoms and
     * the algorithm will not regard how many keto groups are attached to the
     * cycle (might be only one, might be that all connected oxygen atoms are
     * double-bound). If this option is turned off, every sugar-like cycle with
     * an exocyclic double or triple bond will be excluded from the detected
     * circular sugars, as it is specified in the original algorithm
     * description.
     *
     * @param bool true, if circular sugars with keto groups should be detected
     */
    public void setDetectCircularSugarsWithKetoGroupsSetting(boolean bool) {
        this.detectCircularSugarsWithKetoGroupsSetting = bool;
    }

    /**
     * Sets all settings to their default values (see public static constants or
     * enquire via get/is methods). This includes the pattern lists for linear
     * and circular sugars. To call this method is equivalent to using the
     * constructor of this class.
     */
    public void restoreDefaultSettings() {
        this.linearSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        this.circularSugarStructuresList = new ArrayList<>(SugarRemovalUtility.CIRCULAR_SUGARS_SMILES.length);
        this.linearAcidicSugarStructuresList = new ArrayList<>(SugarRemovalUtility.LINEAR_ACIDIC_SUGARS_SMILES.length);
        this.linearSugarPatternsList = new ArrayList<>(SugarRemovalUtility.LINEAR_SUGARS_SMILES.length);
        SmilesParser smilesParser = new SmilesParser(this.builder);
        //adding linear sugars to list
        for (String smiles : SugarRemovalUtility.LINEAR_SUGARS_SMILES) {
            try {
                this.linearSugarStructuresList.add(smilesParser.parseSmiles(smiles));
            } catch (InvalidSmilesException exception) {
                SugarRemovalUtility.LOGGER.warn("Unable to parse linear sugar pattern SMILES code: " + smiles);
            }
        }
        //parsing linear sugars into patterns and sorting (in advance)
        this.updateLinearSugarPatterns();
        //adding linear acidic sugars to list
        for (String smiles : SugarRemovalUtility.LINEAR_ACIDIC_SUGARS_SMILES) {
            try {
                this.linearAcidicSugarStructuresList.add(smilesParser.parseSmiles(smiles));
            } catch (InvalidSmilesException exception) {
                SugarRemovalUtility.LOGGER.warn("Unable to parse linear acidic sugar pattern SMILES code: " + smiles);
            }
        }
        Comparator<IAtomContainer> comparator = new AtomContainerComparator().reversed();
        this.linearAcidicSugarStructuresList.sort(comparator);
        //adding ring sugars to list
        for (String smiles : SugarRemovalUtility.CIRCULAR_SUGARS_SMILES) {
            try {
                this.circularSugarStructuresList.add(smilesParser.parseSmiles(smiles));
            } catch (InvalidSmilesException exception) {
                SugarRemovalUtility.LOGGER.warn("Unable to parse circular sugar pattern SMILES code: " + smiles);
            }
        }
        this.circularSugarStructuresList.sort(comparator);
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
        this.setLinearSugarCandidateMinSizeSetting(SugarRemovalUtility.LINEAR_SUGAR_CANDIDATE_MIN_SIZE_DEFAULT);
        this.setLinearSugarCandidateMaxSizeSetting(SugarRemovalUtility.LINEAR_SUGAR_CANDIDATE_MAX_SIZE_DEFAULT);
        this.setDetectLinearAcidicSugarsSetting(SugarRemovalUtility.DETECT_LINEAR_ACIDIC_SUGARS_DEFAULT);
        this.setDetectSpiroRingsAsCircularSugarsSetting(SugarRemovalUtility.DETECT_SPIRO_RINGS_AS_CIRCULAR_SUGARS_DEFAULT);
        this.setDetectCircularSugarsWithKetoGroupsSetting(SugarRemovalUtility.DETECT_CIRCULAR_SUGARS_WITH_KETO_GROUPS_DEFAULT);
    }

    /**
     * Detects linear sugar moieties in the given molecule, according to the
     * current settings for linear sugar detection. It is not influenced by the
     * setting specifying whether only terminal sugar moieties should be removed
     * and not by the set preservation mode. Therefore, this method will return
     * true even if only non-terminal linear sugar moieties are detected.
     *
     * @param moleculeParam the atom container to scan for the presence of
     *                      linear sugar moieties
     * @return true, if the given molecule contains linear sugar moieties
     */
    public boolean hasLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        List<IAtomContainer> sugarCandidates = this.getLinearSugarCandidates(moleculeParam);
        return !sugarCandidates.isEmpty();
    }

    /**
     * Detects circular sugar moieties in the given molecule, according to the
     * current settings for circular sugar detection. It is not influenced by
     * the setting specifying whether only terminal sugar moieties should be
     * removed and not by the set preservation mode. Therefore, this method will
     * return true even if only non-terminal circular sugar moieties are
     * detected.
     *
     * @param moleculeParam the atom container to scan for the presence of
     *                      circular sugar moieties
     * @return true, if the given molecule contains circular sugar moieties
     */
    public boolean hasCircularSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        List<IAtomContainer> sugarCandidates = this.getCircularSugarCandidates(moleculeParam);
        return !sugarCandidates.isEmpty();
    }

    /**
     * Detects circular and linear sugar moieties in the given molecule,
     * according to the current settings for sugar detection. It is not
     * influenced by the setting specifying whether only terminal sugar moieties
     * should be removed and not by the set preservation mode. Therefore, this
     * method will return true even if only non-terminal sugar moieties are
     * detected.
     *
     * @param moleculeParam the atom container to scan for the presence of sugar
     *                      moieties
     * @return true, if the given molecule contains sugar moieties of any kind
     *         (circular or linear)
     */
    public boolean hasCircularOrLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        List<IAtomContainer> circularSugarCandidates = this.getCircularSugarCandidates(moleculeParam);
        boolean containsCircularSugar = !circularSugarCandidates.isEmpty();
        List<IAtomContainer> linearSugarCandidates = this.getLinearSugarCandidates(moleculeParam);
        boolean containsLinearSugar = !linearSugarCandidates.isEmpty();
        return (containsCircularSugar || containsLinearSugar);
    }

    /**
     * Tests whether the given molecule qualifies for the glycosidic bond
     * exemption. This is true for molecules that practically are single-cycle
     * circular sugars, meaning that the molecule is empty if the sugar ring is
     * detected and removed according to the current settings. These molecules
     * or sugar rings do not need to have a glycosidic bond in order to be
     * detected as a sugar ring if the option to only detect those circular
     * sugars that have one is activated. This exemption was introduced because
     * these molecules do not contain any other structure to bind to via a
     * glycosidic bond.
     * <br>Note: It is checked whether the sugar ring really does not have a
     * glycosidic bond and false is returned if it does.
     *
     * @param moleculeParam the molecule to check
     * @return true, if the given molecule qualifies for the exemption (it only
     *         has one sugar cycle, is empty after its removal, and does not
     *         have a glycosidic bond); false otherwise
     */
    public boolean isQualifiedForGlycosidicBondExemption(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        List<IAtomContainer> potentialSugarRings = this.detectPotentialSugarCycles(
                moleculeParam,
                this.detectSpiroRingsAsCircularSugarsSetting,
                this.detectCircularSugarsWithKetoGroupsSetting);
        if (potentialSugarRings.size() != 1) {
            return false;
        }
        IAtomContainer potentialSugarRing = potentialSugarRings.get(0);
        boolean hasGlycosidicBond = this.hasGlycosidicBond(potentialSugarRing, moleculeParam);
        boolean moleculeIsOnlyOneSugarRing;
        if (!hasGlycosidicBond) {
            //special exemption for molecules that only consist of a sugar ring and nothing else:
            // they should also be seen as candidate even though they do not have a glycosidic bond
            // (because there is nothing to bind to)
            // this method basically checks whether the molecule is empty after removal of the one sugar ring
            moleculeIsOnlyOneSugarRing = this.isMoleculeEmptyAfterRemovalOfThisRing(potentialSugarRing, moleculeParam);
        } else {
            return false;
        }
        return moleculeIsOnlyOneSugarRing;
    }

    /**
     * Detects circular sugar moieties in the given molecule according to the
     * current settings for circular sugar detection and returns the number of
     * detected moieties. It is not influenced by the setting specifying whether
     * only terminal sugar moieties should be removed and not by the set
     * preservation mode. Therefore, the return value of this method will
     * include non-terminal moieties at all times (and terminal ones also).
     *
     * @param moleculeParam the atom container to scan for the presence of
     *                      circular sugar moieties
     * @return an integer representing the number of detected circular sugar
     *         moieties in the given molecule
     */
    public int getNumberOfCircularSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> circularSugarCandidates = this.getCircularSugarCandidates(moleculeParam);
        return circularSugarCandidates.size();
    }

    /**
     * Detects linear sugar moieties in the given molecule according to the
     * current settings for linear sugar detection and returns the number of
     * detected moieties. It is not influenced by the setting specifying whether
     * only terminal sugar moieties should be removed and not by the set
     * preservation mode. Therefore, the return value of this method will
     * include non-terminal moieties at all times (and terminal ones also).
     *
     * @param moleculeParam the atom container to scan for the presence of
     *                      linear sugar moieties
     * @return an integer representing the number of detected linear sugar
     *         moieties in the given molecule
     */
    public int getNumberOfLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> linearSugarCandidates = this.getLinearSugarCandidates(moleculeParam);
        return linearSugarCandidates.size();
    }

    /**
     * Detects circular and linear sugar moieties in the given molecule
     * according to the current settings for circular and linear sugar detection
     * and returns the number of detected moieties. It is not influenced by the
     * setting specifying whether only terminal sugar moieties should be removed
     * and not by the set preservation mode. Therefore, the return value of this
     * method will include non-terminal moieties at all times (and terminal ones
     * also).
     *
     * @param moleculeParam the atom container to scan for the presence of
     *                      circular and linear sugar moieties
     * @return an integer representing the number of detected circular and
     *         linear sugar moieties in the given molecule
     */
    public int getNumberOfCircularAndLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return 0;
        }
        List<IAtomContainer> circularSugarCandidates = this.getCircularSugarCandidates(moleculeParam);
        List<IAtomContainer> linearSugarCandidates = this.getLinearSugarCandidates(moleculeParam);
        return (circularSugarCandidates.size() + linearSugarCandidates.size());
    }

    /**
     * Removes circular sugar moieties from the given atom container (this
     * operation modifies the given atom container, so clone the object
     * beforehand if you need to preserve its original structure!). Which
     * substructures are removed depends on the settings for circular sugar
     * detection, the setting specifying whether only terminal sugar moieties
     * should be removed and on the set preservation mode.
     * <br>The aglycone will have open valences in places where removed sugar
     * moieties were formerly situated. If you do not care for where the removed
     * moieties were sitting, saturate with implicit hydrogen atoms.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * circular sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. If only terminal sugar moieties
     * are removed from the molecule, any disconnected structure resulting from
     * a removal step must be too small to keep according to the set
     * preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular sugar moieties are to be removed from the given
     * molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or
     * more disconnected structures after
     * deglycosylation, whereas in the former case, the processed structure
     * always consists of one connected structure.
     * <br>If the given molecule consists only of circular sugars, an empty atom
     * container is left after processing.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another
     * cycle are preserved.
     *
     * @param moleculeParam the molecule to remove circular sugar moieties from
     * @return true if sugar moieties were detected and removed
     */
    public boolean removeCircularSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        List<IAtomContainer> deglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnCircularSugars(moleculeParam);
        return (deglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
    }

    /**
     * Removes circular sugar moieties from the given atom container and returns
     * the resulting aglycone (at list index 0) and the removed circular sugar
     * moieties (this operation modifies the given atom container, so clone the
     * object beforehand if you need to preserve its original structure!).
     * Which substructures are removed depends on the settings for
     * circular sugar detection, the setting specifying whether only terminal
     * sugar moieties should be removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * circular sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. If only terminal sugar moieties
     * are removed from the molecule, any disconnected structure resulting from
     * a removal step must be too small to keep according to the set
     * preservation mode option and the set threshold and is cleared away.
     * <br>If all the circular sugar moieties are to be removed from the given
     * molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycone at list
     * index 0) may consist of two or more
     * disconnected structures when returned, whereas in the former case, the
     * returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of circular sugars, an empty atom
     * container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have
     * invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties and so does the
     * aglycone in places where removed sugar moieties were formerly situated.
     * If you do not care for where the removed moieties were sitting, saturate
     * with implicit hydrogen atoms.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another
     * cycle are preserved.
     * <br>If no sugar moieties were removed, the returned list is of size 1
     * and its only element is the molecule given as parameter, unchanged.
     *
     * @param moleculeParam  the molecule to remove circular sugar moieties
     *                       from
     * @return a list of atom container objects representing the deglycosylated
     *         molecule at list index 0 and the removed circular sugar moieties
     *         at the remaining list positions. The returned aglycone
     *         may be unconnected if also non-terminal sugars are removed
     *         according to the settings, and it may be empty if the resulting
     *         structure after sugar removal was too small to preserve due to
     *         the set preservation mode and the associated threshold
     *         (i.e. the molecule basically was a sugar); the returned sugar
     *         moieties that were removed from the molecule have invalid valences
     *         at atoms formerly bonded to the molecule core or to other sugar
     *         moieties and so does the
     *         aglycone in places where removed sugar moieties were formerly situated.
     *         If you do not care for where the removed moieties were sitting, saturate
     *         with implicit hydrogen atoms.
     */
    public List<IAtomContainer> removeAndReturnCircularSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            List<IAtomContainer> returnList = new ArrayList<>(1);
            returnList.add(0, moleculeParam);
            return returnList;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        if (this.preservationModeSetting != PreservationMode.ALL && !ConnectivityChecker.isConnected(moleculeParam)) {
            this.flagTooSmallDisconnectedPartsToPreserve(moleculeParam);
        }
        List<IAtomContainer> sugarCandidates = this.getCircularSugarCandidates(moleculeParam);
        /*note: this means that there are matches of the circular sugar patterns
        and that they adhere to most of
        the given settings. The exception is that they might not be terminal*/
        boolean containsSugar = !sugarCandidates.isEmpty();
        List<IAtomContainer> resultList = new ArrayList<>(sugarCandidates.size() + 1);
        resultList.add(0, moleculeParam);
        if (containsSugar) {
            resultList.addAll(1, this.removeSugarCandidates(moleculeParam, sugarCandidates));
        }
        //the molecule at index 0 may be empty and may be unconnected, based on the settings
        return resultList;
    }

    /**
     * Removes linear sugar moieties from the given atom container (this
     * operation modifies the given atom container, so clone the object
     * beforehand if you need to preserve its original structure!). Which
     * substructures are removed depends on the settings for linear sugar
     * detection, the setting specifying whether only terminal sugar moieties
     * should be removed and on the set preservation mode.
     * <br>The aglycone will have open valences in places where removed sugar
     * moieties were formerly situated. If you do not care for where the removed
     * moieties were sitting, saturate with implicit hydrogen atoms.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * linear sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. If only terminal sugar moieties
     * are removed from the molecule, any disconnected structure resulting from
     * a removal step must be too small to keep according to the set
     * preservation mode option and the set threshold and is cleared away.
     * <br>If all the linear sugar moieties are to be removed from the given
     * molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or
     * more disconnected structures after
     * deglycosylation, whereas in the former case, the processed structure
     * always consists of one connected structure.
     * <br>If the given molecule consists only of linear sugars, an empty atom
     * container is left after processing.
     *
     * @param moleculeParam the molecule to remove linear sugar moieties from
     * @return true if sugar moieties were detected and removed
     */
    public boolean removeLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }
        List<IAtomContainer> deglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnLinearSugars(moleculeParam);
        return (deglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
    }

    /**
     * Removes linear sugar moieties from the given atom container and returns
     * the resulting aglycone (at list index 0) and the removed linear sugar
     * moieties (this operation modifies the given atom container, so clone the
     * object beforehand if you need to preserve its original structure!).
     * Which substructures are removed depends on the settings for
     * linear sugar detection, the setting specifying whether only terminal
     * sugar moieties should be removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * linear sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. If only terminal sugar moieties
     * are removed from the molecule, any disconnected structure resulting from
     * a removal step must be too small to keep according to the set
     * preservation mode option and the set threshold and is cleared away.
     * <br>If all the linear sugar moieties are to be removed from the given
     * molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycone at list
     * index 0) may consist of two or more
     * disconnected structures when returned, whereas in the former case, the
     * returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of linear sugars, an empty atom
     * container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have
     * invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties and so does the
     * aglycone in places where removed sugar moieties were formerly situated.
     * If you do not care for where the removed moieties were sitting, saturate
     * with implicit hydrogen atoms.
     * <br>If no sugar moieties were removed, the returned list is of size 1
     * and its only element is the molecule given as parameter, unchanged.
     *
     * @param moleculeParam  the molecule to remove linear sugar moieties from
     * @return a list of atom container objects representing the deglycosylated
     *         molecule at list index 0 and the removed linear sugar moieties
     *         at the remaining list positions. The returned aglycone
     *         may be unconnected if also non-terminal sugars are removed
     *         according to the settings, and it may be empty if the resulting
     *         structure after sugar removal was too small to preserve due to
     *         the set preservation mode and the associated threshold (i.e. the
     *         molecule basically was a sugar); the returned sugar moieties
     *         that were removed from the molecule have invalid valences at
     *         atoms formerly bonded to the molecule core or to other sugar
     *         moieties and so does the
     *         aglycone in places where removed sugar moieties were formerly situated.
     *         If you do not care for where the removed moieties were sitting, saturate
     *         with implicit hydrogen atoms.
     */
    public List<IAtomContainer> removeAndReturnLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            List<IAtomContainer> returnList = new ArrayList<>(1);
            returnList.add(0, moleculeParam);
            return returnList;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        if (this.preservationModeSetting != PreservationMode.ALL && !ConnectivityChecker.isConnected(moleculeParam)) {
            this.flagTooSmallDisconnectedPartsToPreserve(moleculeParam);
        }
        List<IAtomContainer> sugarCandidates = this.getLinearSugarCandidates(moleculeParam);
        /*note: this means that there are matches of the linear sugar patterns
        and that they adhere to most of
        the given settings. The exception is that they might not be terminal*/
        boolean containsSugar = !sugarCandidates.isEmpty();
        List<IAtomContainer> resultList = new ArrayList<>(sugarCandidates.size() + 1);
        resultList.add(0, moleculeParam);
        if (containsSugar) {
            resultList.addAll(1, this.removeSugarCandidates(moleculeParam, sugarCandidates));
        }
        //the molecule at index 0 may be empty and may be unconnected, based on the settings
        return resultList;
    }

    /**
     * Removes circular and linear sugar moieties from the given atom container
     * (this operation modifies the given atom container, so clone the object
     * beforehand if you need to preserve its original structure!).
     * Which substructures are removed depends on the settings for circular and
     * linear sugar detection, the setting specifying whether only terminal
     * sugar moieties should be removed and on the set preservation mode.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. Important note: To ensure the
     * removal also of linear sugars that only become terminal after removing
     * one or more terminal circular sugar and vice-versa, multiple iterations
     * of circular and linear sugar detection and removal are done here.
     * Therefore, this method might in special cases return another aglycone (the
     * 'true' aglycone) than e.g. a subsequent call to the methods for separate
     * circular and linear sugar removal.
     * <br>If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a
     * removal step must be too small to keep according to the preservation mode
     * option and the set threshold and is cleared away.
     * <br>If all the circular and linear sugars are to be removed from the
     * given molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or
     * more disconnected structures when
     * returned, whereas in the former case, the returned structure always
     * consists of one connected structure.
     * <br>If the given molecule consists only of sugars, an empty atom
     * container is returned.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another
     * cycle are preserved.
     *
     * @param moleculeParam the molecule to remove circular and linear sugar
     *                      moieties from
     * @return true if sugar moieties were detected and removed
     */
    public boolean removeCircularAndLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return false;
        }

        List<IAtomContainer> deglycosylatedMoleculeAndSugarMoietiesList =
                this.removeAndReturnCircularAndLinearSugars(moleculeParam);
        return (deglycosylatedMoleculeAndSugarMoietiesList.size() > 1);
    }

    /**
     * Removes circular and linear sugar moieties from the given atom container
     * and returns the resulting aglycone (at list index 0) and the removed sugar
     * moieties (this operation modifies the given atom container, so clone the
     * object beforehand if you need to preserve its original structure!). Which
     * substructures are removed depends on the settings for
     * circular and linear sugar detection, the setting specifying whether only
     * terminal sugar moieties should be removed and on the set preservation
     * mode.
     * <br>If only terminal sugar moieties are to be removed, the detected
     * sugars are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. Important note: To ensure the
     * removal also of linear sugars that only become terminal after removing
     * one or more terminal circular sugar and vice-versa, multiple iterations
     * of circular and linear sugar detection and removal are done here.
     * Therefore, this method might in special cases return another aglycone (the
     * 'true' aglycone) than e.g. a subsequent call to the methods for separate
     * circular and linear sugar removal.
     * <br>If only terminal sugar moieties are
     * removed from the molecule, any disconnected structure resulting from a
     * removal step must be too small to keep according to the preservation mode
     * option and the set threshold and is cleared away.
     * <br>If all the circular and linear sugars are to be removed from the
     * given molecule (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule (aglycone at list
     * index 0) may consist of two or more
     * disconnected structures when returned, whereas in the former case, the
     * returned structure always consists of one connected structure.
     * <br>If the given molecule consists only of sugars, an empty atom
     * container is returned at list index 0.
     * <br>The returned sugar moieties that were removed from the molecule have
     * invalid valences at atoms formerly
     * bonded to the molecule core or to other sugar moieties and so does the
     * aglycone in places where removed sugar moieties were formerly situated.
     * If you do not care for where the removed moieties were sitting, saturate
     * with implicit hydrogen atoms.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another
     * cycle are preserved.
     * <br>If no sugar moieties were removed, the returned list is of size 1
     * and its only element is the molecule given as parameter, unchanged.
     *
     * @param moleculeParam  the molecule to remove circular and linear sugar
     *                       moieties from
     * @return a list of atom container objects representing the deglycosylated
     *         molecule at list index 0 and the removed sugar moieties at the
     *         remaining list positions. The returned aglycone may be
     *         unconnected if also non-terminal sugars are removed according to
     *         the settings, and it may be empty if the resulting structure after
     *         sugar removal was too small to preserve due to the set
     *         preservation mode and the associated threshold (i.e. the molecule
     *         basically was a sugar); the returned sugar moieties that were
     *         removed from the molecule have invalid valences at atoms formerly
     *         bonded to the molecule core or to other sugar moieties and so does the
     *         aglycone in places where removed sugar moieties were formerly situated.
     *         If you do not care for where the removed moieties were sitting, saturate
     *         with implicit hydrogen atoms.
     */
    public List<IAtomContainer> removeAndReturnCircularAndLinearSugars(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            List<IAtomContainer> returnList = new ArrayList<>(1);
            returnList.add(0, moleculeParam);
            return returnList;
        }
        this.addUniqueIndicesToAtoms(moleculeParam);
        if (this.preservationModeSetting != PreservationMode.ALL && !ConnectivityChecker.isConnected(moleculeParam)) {
            this.flagTooSmallDisconnectedPartsToPreserve(moleculeParam);
        }
        //note: initial capacity arbitrarily chosen
        List<IAtomContainer> resultList = new ArrayList<>(moleculeParam.getAtomCount() / 6);
        resultList.add(0, moleculeParam);
        while (true) {
            List<IAtomContainer> circularSugarCandidates = this.getCircularSugarCandidates(moleculeParam);
            boolean isCandidateListNotEmpty = !circularSugarCandidates.isEmpty();
            List<IAtomContainer> removedCircularSugarMoieties = new ArrayList<>(0);
            if (isCandidateListNotEmpty) {
                removedCircularSugarMoieties = this.removeSugarCandidates(moleculeParam, circularSugarCandidates);
                resultList.addAll(removedCircularSugarMoieties);
            }
            //exit here if molecule is empty after removal
            if (moleculeParam.isEmpty()) {
                break;
            }
            List<IAtomContainer> linearSugarCandidates = this.getLinearSugarCandidates(moleculeParam);
            isCandidateListNotEmpty = !linearSugarCandidates.isEmpty();
            List<IAtomContainer> removedLinearSugarMoieties = new ArrayList<>(0);
            if (isCandidateListNotEmpty) {
                removedLinearSugarMoieties = this.removeSugarCandidates(moleculeParam, linearSugarCandidates);
                resultList.addAll(removedLinearSugarMoieties);
            }
            //exit here if molecule is empty after removal
            if (moleculeParam.isEmpty()) {
                break;
            }
            if (this.removeOnlyTerminalSugarsSetting) {
                boolean wasSthRemoved = ((!removedCircularSugarMoieties.isEmpty())
                        || (!removedLinearSugarMoieties.isEmpty()));
                if (!wasSthRemoved) {
                    //if nothing was removed, the loop is broken; otherwise,
                    // there might be new terminal moieties in the
                    // next iteration
                    break;
                }
            } else {
                //if all moieties are to be removed, not only the terminal ones,
                // one iteration is enough
                break;
            }
        }
        //The molecule at index 0 may be empty and may be unconnected, based on the settings
        return resultList;
    }

    /**
     * Extracts circular sugar moieties from the given molecule, according to
     * the current settings for circular sugar detection. It is not influenced
     * by the setting specifying whether only terminal sugar moieties should be
     * removed and not by the set preservation mode. Therefore, this method will
     * always return terminal and non-terminal moieties.
     *
     * @param moleculeParam the molecule to extract circular sugar moieties
     *                      from
     * @return a list of substructures in the given molecule that are regarded
     *         as circular sugar moieties
     */
    public List<IAtomContainer> getCircularSugarCandidates(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return new ArrayList<>(0);
        }
        boolean areIndicesSet = this.checkUniqueIndicesOfAtoms(moleculeParam);
        if (!areIndicesSet) {
            this.addUniqueIndicesToAtoms(moleculeParam);
        }
        List<IAtomContainer> potentialSugarRings = this.detectPotentialSugarCycles(moleculeParam,
                this.detectSpiroRingsAsCircularSugarsSetting, this.detectCircularSugarsWithKetoGroupsSetting);
        if (potentialSugarRings.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> sugarCandidates = new ArrayList<>(potentialSugarRings.size());
        for(IAtomContainer potentialSugarRing : potentialSugarRings) {
            if (potentialSugarRing == null || potentialSugarRing.isEmpty()) {
                continue;
            }
            /*
             * note: another requirement of a suspected sugar ring is that it contains only single bonds.
             * This is not tested here because all the structures in the reference rings do meet this criterion.
             * But a structure that does not meet this criterion could be added to the references by the user.
             */
            //do not remove rings without an attached glycosidic bond if this option is set
            if (this.detectCircularSugarsOnlyWithOGlycosidicBondSetting) {
                boolean hasGlycosidicBond = this.hasGlycosidicBond(potentialSugarRing, moleculeParam);
                if (!hasGlycosidicBond) {
                    //special exemption for molecules that only consist of a sugar ring and nothing else:
                    // they should also be seen as candidate even though they do not have a glycosidic bond
                    // (because there is nothing to bind to)
                    //Note: There is also a public method testing this! Keep this in mind! It is not used here to not
                    // do the ring search etc. again.
                    if (potentialSugarRings.size() == 1) {
                        boolean moleculeIsOnlyOneSugarRing = this.isMoleculeEmptyAfterRemovalOfThisRing(potentialSugarRing, moleculeParam);
                        if (!moleculeIsOnlyOneSugarRing) {
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
                int exocyclicOxygenCount = this.getExocyclicOxygenAtomCount(potentialSugarRing, moleculeParam);
                int atomsInRingCount = potentialSugarRing.getAtomCount();
                boolean areEnoughOxygensAttached = this.doesRingHaveEnoughExocyclicOxygenAtoms(atomsInRingCount,
                        exocyclicOxygenCount);
                if (!areEnoughOxygensAttached) {
                    continue;
                }
            }
            //if sugar ring has not been excluded yet, the molecule contains sugars, although they might not
            // be terminal
            sugarCandidates.add(potentialSugarRing);
        }
        return sugarCandidates;
    }

    /**
     * Extracts linear sugar moieties from the given molecule, according to the
     * current settings for linear sugar detection. It is not influenced by the
     * setting specifying whether only terminal sugar moieties should be removed
     * and not by the set preservation mode. Therefore, this method will always
     * return terminal and non-terminal moieties.
     *
     * @param moleculeParam the molecule to extract linear sugar moieties from
     * @return a list of substructures in the given molecule that are regarded
     *         as linear sugar moieties
     */
    public List<IAtomContainer> getLinearSugarCandidates(IAtomContainer moleculeParam) {
        if (moleculeParam == null) {
            throw new NullPointerException("Given molecule is 'null'.");
        }
        if (moleculeParam.isEmpty()) {
            return new ArrayList<>(0);
        }
        boolean areIndicesSet = this.checkUniqueIndicesOfAtoms(moleculeParam);
        if (!areIndicesSet) {
            this.addUniqueIndicesToAtoms(moleculeParam);
        }
        List<IAtomContainer> sugarCandidates = this.detectLinearSugarCandidatesByPatternMatching(moleculeParam);
        //alternative ideas: SMARTS or matching the biggest patterns first and exclude the matched atoms
        if (!sugarCandidates.isEmpty()) {
            sugarCandidates = this.combineOverlappingCandidates(sugarCandidates);
            sugarCandidates = this.splitEtherEsterAndPeroxideBondsExtraction(sugarCandidates);
            this.removeAtomsOfCircularSugarsFromCandidates(sugarCandidates, moleculeParam);
        }
        if (!this.detectLinearSugarsInRingsSetting && !sugarCandidates.isEmpty()) {
            this.removeCyclicAtomsFromSugarCandidates(sugarCandidates, moleculeParam);
        }
        if (!sugarCandidates.isEmpty()) {
            sugarCandidates = this.removeTooSmallAndTooLargeCandidates(sugarCandidates);
        }
        return sugarCandidates;
    }

    /**
     * Checks the given input molecule for unconnected parts that would be cleared
     * away after sugar removal, in order to add a flag to the atoms of these
     * parts to be able to preserve them in the later removal. Note that
     * this method does not check whether the molecule is actually disconnected
     * and whether the preservation mode is not set to 'preserve all'.
     *
     * @param moleculeParam the disconnected input molecule to check
     */
    protected void flagTooSmallDisconnectedPartsToPreserve(IAtomContainer moleculeParam) {
        boolean areIndicesSet = this.checkUniqueIndicesOfAtoms(moleculeParam);
        if (!areIndicesSet) {
            this.addUniqueIndicesToAtoms(moleculeParam);
        }
        float loadFactor = 0.75f;
        int indexToAtomMapInitCapacity = (int)(moleculeParam.getAtomCount() * (1.0f / loadFactor) + 2.0f);
        HashMap<Integer, IAtom> indexToAtomMap = new HashMap<>(indexToAtomMapInitCapacity, loadFactor);
        for (IAtom atom : moleculeParam.atoms()) {
            indexToAtomMap.put(atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), atom);
        }
        IAtomContainerSet unconnectedParts = ConnectivityChecker.partitionIntoMolecules(moleculeParam);
        for (IAtomContainer part : unconnectedParts) {
            if (!this.isTooSmallToPreserve(part)) {
                continue;
            }
            for (IAtom atom : part.atoms()) {
                indexToAtomMap.get((int)atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY))
                        .setProperty(SugarRemovalUtility.TOO_SMALL_DISCONNECTED_PART_TO_PRESERVE_PROPERTY_KEY, true);
            }
        }
    }

    /**
     * Removes all unconnected fragments that are too small to keep according to
     * the current preservation mode and threshold setting. If all structures
     * are too small, an empty atom container is returned.
     * <br>This does not guarantee that the resulting atom container consists of
     * only one connected structure. There might
     * be multiple unconnected structures that are big enough to be preserved.
     *
     * @param moleculeParam the molecule to clean up; it might be empty after
     *                      this method call but not null
     */
    protected void removeTooSmallDisconnectedStructures(IAtomContainer moleculeParam) {
        if (moleculeParam.isEmpty()) {
            return;
        }
        if (this.preservationModeSetting == PreservationMode.ALL) {
            return;
        }
        IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(moleculeParam);
        for (int i = 0; i < components.getAtomContainerCount(); i++) {
            IAtomContainer component = components.getAtomContainer(i);
            //May throw UnsupportedOperationException if a new PreservationMode
            // constant has been added but not implemented
            // in this method yet. Since this is a serious issue, the code is
            // supposed to crash.
            boolean isTooSmall = this.isTooSmallToPreserve(component);
            if (isTooSmall) {
                //note: careful with removing things from sets/lists while
                // iterating over it! Here, using the iterator caused issues,
                // even though elements are not removed from the same set that is iterated
                for (int j = 0; j < component.getAtomCount(); j++) {
                    IAtom atom = component.getAtom(j);
                    //check to avoid exceptions
                    if (moleculeParam.contains(atom)) {
                        moleculeParam.removeAtom(atom);
                    } else {
                        continue;
                    }
                    j = j - 1;
                }
            }
        }
    }

    /**
     * Checks whether the given molecule or structure is too small to be kept
     * according to the current preservation mode and threshold setting.
     *
     * @param moleculeParam the molecule to check
     * @return true, if the given structure is too small to be preserved
     */
    protected boolean isTooSmallToPreserve(IAtomContainer moleculeParam) {
        if (moleculeParam.isEmpty()) {
            return true;
        }
        boolean isTooSmall;
        for (IAtom atom : moleculeParam.atoms()) {
            if (atom.getProperty(SugarRemovalUtility.TOO_SMALL_DISCONNECTED_PART_TO_PRESERVE_PROPERTY_KEY) != null
                    && (boolean)atom.getProperty(SugarRemovalUtility.TOO_SMALL_DISCONNECTED_PART_TO_PRESERVE_PROPERTY_KEY)) {
                return false;
            }
        }
        if (this.preservationModeSetting == PreservationMode.ALL) {
            isTooSmall = false;
        } else if (this.preservationModeSetting == PreservationMode.HEAVY_ATOM_COUNT) {
            int heavyAtomCount = AtomContainerManipulator.getHeavyAtoms(moleculeParam).size();
            isTooSmall = heavyAtomCount < this.preservationModeThresholdSetting;
        } else if (this.preservationModeSetting == PreservationMode.MOLECULAR_WEIGHT) {
            double molWeight = AtomContainerManipulator.getMass(moleculeParam, AtomContainerManipulator.MolWeight);
            isTooSmall = molWeight < this.preservationModeThresholdSetting;
        } else {
            throw new UnsupportedOperationException("Undefined PreservationMode setting!");
        }
        return isTooSmall;
    }

    /**
     * Checks whether the given substructure is terminal (i.e. it can be removed
     * without producing multiple unconnected structures in the remaining
     * molecule) in the given parent molecule. To do this, the parent molecule
     * is copied, the substructure is removed from this copy, and finally it is
     * checked whether the parent molecule copy still consists of only one
     * connected structure. If that is the
     * case, the substructure is terminal. If the preservation mode is not set
     * to 'preserve all structures', too small resulting fragments are cleared
     * from the parent copy in between. These structures that are too small
     * must also not be part of any other substructure in the given candidate
     * list to avoid removing parts of other sugar candidates.
     * <br>Note: This method only detects moieties that are immediately
     * terminal. It will not deem terminal a sugar
     * moiety that only becomes terminal after the removal of another sugar
     * moiety, for example.
     *
     * @param substructure   the substructure to check for whether it is
     *                       terminal
     * @param parentMolecule the molecule the substructure is a part of
     * @param candidateList  a list containing the detected sugar candidates to
     *                       check whether atoms of other candidates would be
     *                       cleared away if the given substructure was removed
     *                       (which has to be avoided)
     * @return true, if the substructure is terminal
     */
    protected boolean isTerminal(IAtomContainer substructure,
                               IAtomContainer parentMolecule,
                               List<IAtomContainer> candidateList) {
        if (!this.checkUniqueIndicesOfAtoms(parentMolecule)) {
            this.addUniqueIndicesToAtoms(parentMolecule);
        }
        if (!this.checkUniqueIndicesOfAtoms(substructure)) {
            this.addUniqueIndicesToAtoms(substructure);
        }
        for (IAtomContainer candidate : candidateList) {
            boolean areIndicesSet = this.checkUniqueIndicesOfAtoms(candidate);
            if (!areIndicesSet) {
                this.addUniqueIndicesToAtoms(candidate);
            }
        }

        boolean isTerminal;
        IAtomContainer moleculeCopy = this.basicCopy(parentMolecule);
        if (!ConnectivityChecker.isConnected(moleculeCopy)) {
            //since we are using isConnected() to determine whether the substructure
            // is terminal, the structure to start with needs to be connected; if
            // it is not, we determine the part that contains the substructure in
            // question here
            IAtomContainerSet unconnectedParts = ConnectivityChecker.partitionIntoMolecules(moleculeCopy);
            boolean breakOuterLoop = false;
            for (IAtomContainer part : unconnectedParts) {
                HashSet<Integer> partAtomIndices = new HashSet<>((int)(part.getAtomCount() * (1.0f / 0.75f) + 2.0f), 0.75f);
                for (IAtom partAtom : part.atoms()) {
                    partAtomIndices.add(partAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY));
                }
                for (IAtom atom : substructure.atoms()) {
                    if (partAtomIndices.contains((int)atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY))) {
                        moleculeCopy = part;
                        breakOuterLoop = true;
                        break;
                    }
                }
                if (breakOuterLoop) {
                    break;
                }
            }
        }
        float loadFactor = 0.75f;
        int indexToAtomMapInitCapacity = (int)(moleculeCopy.getAtomCount() * (1.0f / loadFactor) + 2.0f);
        HashMap<Integer, IAtom> indexToAtomMap = new HashMap<>(indexToAtomMapInitCapacity, loadFactor);
        for (IAtom atom : moleculeCopy.atoms()) {
            indexToAtomMap.put(atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), atom);
        }
        for (IAtom atom : substructure.atoms()) {
            moleculeCopy.removeAtom(indexToAtomMap.get((int)atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY)));
        }
        boolean isConnected = ConnectivityChecker.isConnected(moleculeCopy);
        if (this.preservationModeSetting == PreservationMode.ALL) {
            isTerminal = isConnected;
        } else {
            if (isConnected) {
                isTerminal = true;
            } else {
                IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(moleculeCopy);
                int atomIndicesThatArePartOfSugarCandidatesSetInitCapacity = (int)(parentMolecule.getAtomCount() * (1.0f / loadFactor) + 2.0f);
                HashSet<Integer> atomIndicesThatArePartOfSugarCandidatesSet = new HashSet<>(
                        atomIndicesThatArePartOfSugarCandidatesSetInitCapacity,
                        loadFactor);
                for (IAtomContainer candidate : candidateList) {
                    for (IAtom atom : candidate.atoms()) {
                        int index = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        atomIndicesThatArePartOfSugarCandidatesSet.add(index);
                    }
                }
                for (IAtomContainer component : components.atomContainers()) {
                    if (component == null || component.isEmpty()) {
                        continue;
                    }
                    //May throw UnsupportedOperationException if a new
                    // PreservationMode constant has been added but not
                    // implemented in this method yet. Since this is a serious
                    // issue, the code is supposed to crash.
                    boolean isTooSmall = this.isTooSmallToPreserve(component);
                    boolean isPartOfSugarCandidate = false;
                    for (IAtom atom : component.atoms()) {
                        int index = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        if (atomIndicesThatArePartOfSugarCandidatesSet.contains(index)) {
                            isPartOfSugarCandidate = true;
                            break;
                        }
                    }
                    if (isTooSmall && !isPartOfSugarCandidate) {
                        //note: no check whether the copy actually contains the component
                        moleculeCopy.remove(component);
                    }
                }
                isTerminal = ConnectivityChecker.isConnected(moleculeCopy);
            }
        }
        return isTerminal;
    }

    /**
     * Removes the given sugar moieties (or substructures in general) from the
     * given molecule and returns the removed moieties (not the aglycone!). The
     * removal algorithm is the same for linear and circular sugars. The only
     * settings influencing the removal are the option specifying whether to
     * remove only terminal sugar moieties and the set preservation mode
     * (because it influences the determination of terminal vs. non-terminal).
     * <br>If only terminal sugar moieties are to be removed, the sugar
     * candidates are one-by-one tested for
     * whether they are terminal or not and removed if they are. The iteration
     * starts anew after iterating over all candidates and stops if no terminal
     * sugar was removed in one whole iteration. If only terminal sugar moieties
     * are removed from the molecule, any disconnected structure resulting from
     * a removal step must be too small to keep according to the preservation
     * mode option and the set threshold and is cleared away.
     * <br>If all the sugars are to be removed from the given molecule
     * (including non-terminal
     * ones), those disconnected structures that are too small are only cleared
     * once at the end of the routine.
     * <br>In the latter case, the deglycosylated molecule may consist of two or
     * more disconnected structures after this
     * method call, whereas in the former case, the remaining structure always
     * consists of one connected structure.
     * <br>Spiro atoms connecting a removed circular sugar moiety to another
     * cycle are preserved.
     * <br>Note that the deglycosylated core is not returned as part of the
     * given list in this method.
     *
     * @param moleculeParam the molecule to remove the sugar candidates from
     * @param candidateList the list of sugar moieties in the given molecule
     * @return a list of atom container objects representing the removed sugar
     *         moieties; the returned sugar moieties that were removed from the
     *         molecule have invalid valences at atoms formerly bonded to the
     *         molecule core or to other sugar moieties
     */
    protected List<IAtomContainer> removeSugarCandidates(IAtomContainer moleculeParam, List<IAtomContainer> candidateList) {
        if (candidateList.isEmpty() || moleculeParam.isEmpty()) {
            return new ArrayList<>(0);
        }
        // a copy of the list is needed to avoid iterating over the same elements
        // again if only terminal moieties are removed
        List<IAtomContainer> sugarCandidates = new ArrayList<>(candidateList);
        // the to be returned list of removed moieties
        List<IAtomContainer> removedSugarMoieties = new ArrayList<>(candidateList.size());
        if (this.removeOnlyTerminalSugarsSetting) {
            //Only terminal sugars should be removed
            //but the definition of terminal depends on the set preservation mode!
            //decisions based on this setting are made in the respective private method
            //No unconnected structures result at the end or at an intermediate step
            boolean containsNoTerminalSugar = false;
            while (!containsNoTerminalSugar) {
                boolean wasSthRemoved = false;
                for (int i = 0; i < sugarCandidates.size(); i++) {
                    IAtomContainer candidate = sugarCandidates.get(i);
                    if (candidate == null || candidate.isEmpty()) {
                        continue;
                    }
                    if (this.isTerminal(candidate, moleculeParam, sugarCandidates)) {
                        for (IAtom atom : candidate.atoms()) {
                            if (moleculeParam.contains(atom)) {
                                Boolean atomIsSpiroAtom = atom.getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY);
                                if (atomIsSpiroAtom != null && atomIsSpiroAtom) {
                                        //here, one of the spiro cycles is removed;
                                        // therefore, the atom is not spiro anymore
                                        atom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, false);
                                        continue;
                                }
                                moleculeParam.removeAtom(atom);
                            }
                        }
                        removedSugarMoieties.add(candidate);
                        sugarCandidates.remove(i);
                        //The removal shifts the remaining indices!
                        i = i - 1;
                        if (!moleculeParam.isEmpty() && (this.preservationModeSetting != PreservationMode.ALL)) {
                                this.removeTooSmallDisconnectedStructures(moleculeParam);
                        }
                        //atom container may be empty after that
                        if (moleculeParam.isEmpty()) {
                            containsNoTerminalSugar = true;
                            break;
                        }
                        wasSthRemoved = true;
                    }
                }
                if (!wasSthRemoved) {
                    containsNoTerminalSugar = true;
                }
            }
        } else {
            //all sugar moieties are removed, may result in an unconnected atom container
            for (IAtomContainer sugarCandidate : sugarCandidates) {
                for (IAtom atom : sugarCandidate.atoms()) {
                    if (moleculeParam.contains(atom)) {
                        Boolean atomIsSpiroAtom = atom.getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY);
                        if (atomIsSpiroAtom != null && atomIsSpiroAtom) {
                                //here, one of the spiro cycles is removed;
                                // therefore, the atom is not spiro anymore
                                atom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, false);
                                continue;
                        }
                        moleculeParam.removeAtom(atom);
                    }
                }
                removedSugarMoieties.add(sugarCandidate);
            }
        }
        if (!moleculeParam.isEmpty() && (this.preservationModeSetting != PreservationMode.ALL)) {
                this.removeTooSmallDisconnectedStructures(moleculeParam);
        }
        return removedSugarMoieties;
    }

    /**
     * Generates a very basic copy of the given molecule, intended for testing
     * whether one of its substructures is terminal in
     * {@link #isTerminal(IAtomContainer, IAtomContainer, List)}.
     * Copies atoms (new instances in copy), bonds (new instances in copy),
     * atomic number, implicit hydrogen count, SRU index
     * property, and bond order.
     *
     * @param molecule atom container to copy
     * @return basic copy of the molecule
     */
    protected IAtomContainer basicCopy(IAtomContainer molecule) {
        IAtomContainer copy = molecule.getBuilder().newInstance(IAtomContainer.class);
        if (molecule.isEmpty()) {
            return copy;
        }
        float loadFactor = 0.75f;
        int mapInitCapacity = (int)(molecule.getAtomCount() * (1.0f / loadFactor) + 2.0f);
        HashMap<Integer, IAtom> indexToCopyAtomMap = new HashMap<>(mapInitCapacity, loadFactor);
        // inspired by John Mayfield
        for (IAtom atom : molecule.atoms()) {
            IAtom cpyAtom = copy.newAtom(atom.getAtomicNumber(), atom.getImplicitHydrogenCount());
            cpyAtom.setProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY,
                    atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY));
            indexToCopyAtomMap.put(atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), cpyAtom);
        }
        for (IBond bond : molecule.bonds()) {
            IAtom beg = indexToCopyAtomMap.get((int) bond.getBegin().getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY));
            IAtom end = indexToCopyAtomMap.get((int) bond.getEnd().getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY));
            if (beg == null || end == null)
                continue;
            beg.getContainer().newBond(beg, end, bond.getOrder());
        }
        return copy;
    }

    /**
     * Adds an index as property to all atom objects of the given atom container
     * to identify them uniquely within the atom container and its copies. This
     * is required e.g. for the determination of terminal vs. non-terminal sugar
     * moieties.
     *
     * @param moleculeParam the molecule that will be processed by the class
     */
    protected void addUniqueIndicesToAtoms(IAtomContainer moleculeParam) {
        if (moleculeParam.isEmpty()) {
            return;
        }
        for (int i = 0; i < moleculeParam.getAtomCount(); i++) {
            IAtom atom = moleculeParam.getAtom(i);
            atom.setProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY, i);
        }
    }

    /**
     * Creates an identifier string for substructures of a molecule, based on
     * the unique indices of the included atoms. It is only encoded which atoms
     * are part of the substructure, no bond information etc. Used for a quick
     * matching of substructures in the same molecule. The unique indices in
     * every atom have to be set.
     *
     * @param substructure the substructure to create an identifier for
     * @return the identifier string
     */
    protected String generateSubstructureIdentifier(IAtomContainer substructure) {
        if (substructure.isEmpty()) {
            return "";
        }
        List<Integer> indicesList = new ArrayList<>(substructure.getAtomCount());
        for (IAtom atom : substructure.atoms()) {
            int atomIndex = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
            indicesList.add(atomIndex);
        }
        Collections.sort(indicesList);
        String substructureIdentifier = "";
        for (int atomIndex : indicesList) {
            substructureIdentifier = substructureIdentifier.concat(Integer.toString(atomIndex)).concat(":");
        }
        return substructureIdentifier;
    }

    /**
     * Checks whether all atoms in the given molecule have a unique (in the
     * given molecule) index as property. It checks the uniqueness of the
     * detected indices but not whether there are numbers missing (the ids of
     * this class are created as numbers starting from zero and growing in
     * integer steps).
     *
     * @param moleculeParam the molecule to check
     * @return true if every atom has an index property that is unique in the
     *         given molecule
     */
    protected boolean checkUniqueIndicesOfAtoms(IAtomContainer moleculeParam) {
        if (moleculeParam.isEmpty()) {
            return true;
        }
        float loadFactor = 0.75f;
        int atomIndicesSetInitCapacity = (int)( moleculeParam.getAtomCount() * (1.0f / loadFactor) + 2.0f);
        HashSet<Integer> atomIndicesSet = new HashSet<>(atomIndicesSetInitCapacity, loadFactor);
        for (IAtom atom : moleculeParam.atoms()) {
            if (atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY) == null) {
                return false;
            } else {
                int index = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                if (atomIndicesSet.contains(index)) {
                    return false;
                } else {
                    atomIndicesSet.add(index);
                }
            }
        }
        //only reached if method is not exited before because of a missing or non-unique index
        return true;
    }

    /**
     * Detects and returns cycles of the given molecule that are isolated (spiro
     * rings included or not according to the boolean parameter), isomorphic to
     * the circular sugar patterns, and only have exocyclic single bonds (keto
     * groups ignored or not according to the boolean parameter). These cycles
     * are the general candidates for circular sugars that are filtered
     * according to the other settings in the following steps. Spiro atoms are
     * marked by a property.
     *
     * @param moleculeParam     the molecule to extract potential circular
     *                          sugars from
     * @param includeSpiroRings specification whether spiro rings should be
     *                          included in the detected potential sugar cycles
     *                          or filtered out; for circular sugar detection
     *                          this should be set according to the current
     *                          'detect spiro rings as circular sugars' setting;
     *                          for filtering circular sugar candidates or their
     *                          atoms during linear sugar detection, this should
     *                          be set to 'true'
     * @param ignoreKetoGroups  specification whether potential sugar cycles
     *                          with keto groups should be included in the
     *                          returned list; for circular sugar detection this
     *                          should be set according to the current 'detect
     *                          circular sugars with keto groups' setting; for
     *                          filtering circular sugar candidates or their
     *                          atoms during linear sugar detection, this should
     *                          be set to 'true'
     * @return a list of the potential sugar cycles
     */
    protected List<IAtomContainer> detectPotentialSugarCycles(IAtomContainer moleculeParam,
                                                              boolean includeSpiroRings,
                                                              boolean ignoreKetoGroups) {
        if (moleculeParam.isEmpty()) {
            return new ArrayList<>(0);
        }
        boolean areIndicesSet = this.checkUniqueIndicesOfAtoms(moleculeParam);
        if (!areIndicesSet) {
            this.addUniqueIndicesToAtoms(moleculeParam);
        }
        int[][] adjList = GraphUtil.toAdjList(moleculeParam);
        //efficient computation/partitioning of the ring systems
        RingSearch ringSearch = new RingSearch(moleculeParam, adjList);
        List<IAtomContainer> isolatedRings = ringSearch.isolatedRingFragments();
        if (isolatedRings.isEmpty()) {
            return new ArrayList<>(0);
        }
        //iterating through all atoms in rings to identify spiro rings
        List<IAtomContainer> ringFragments = ringSearch.isolatedRingFragments();
        ringFragments.addAll(ringSearch.fusedRingFragments());
        //Mapping identifiers of all the rings in the molecule to whether they are fused OR spiro (true) or isolated
        // AND non-spiro (false)
        float loadFactor = 0.75f;
        int ringIdentifierToIsFusedOrSpiroMapInitCapacity = (int)(ringFragments.size() * (1.0f / loadFactor) + 2.0f);
        HashMap<String, Boolean> ringIdentifierToIsFusedOrSpiroMap = new HashMap<>(ringIdentifierToIsFusedOrSpiroMapInitCapacity, loadFactor);
        //Mapping atom identifiers to identifiers of the rings they are part of
        int atomIDToRingIDMapInitCapacity = 6 * (int)(ringFragments.size() * (1.0f / loadFactor) + 2.0f);
        HashMap<Integer, Set<String>> atomIDToRingIDMap = new HashMap<>(atomIDToRingIDMapInitCapacity, loadFactor);
        /* Every atom of every ring is visited; If one atom is visited multiple times, it is in a fused ring or a spiro
         * atom connecting two spiro rings */
        for (IAtomContainer ring : ringFragments) {
            String ringID = this.generateSubstructureIdentifier(ring);
            //initial value false until one atom of the ring is visited more than once
            ringIdentifierToIsFusedOrSpiroMap.put(ringID, false);
            for (IAtom atom : ring.atoms()) {
                int atomID = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                //case 1: atom is not present in the map yet, so it is not shared by another ring that was already visited
                if (!atomIDToRingIDMap.containsKey(atomID)) {
                    //the atom (id) is added to the map with a set that for now only contains the id of the current ring
                    int ringIDSetInitCapacity = (int)(5.0f * (1.0f / loadFactor) + 2.0f);
                    HashSet<String> ringIDSet = new HashSet<>(ringIDSetInitCapacity, loadFactor);
                    ringIDSet.add(ringID);
                    atomIDToRingIDMap.put(atomID, ringIDSet);
                //case 2: atom was already visited, so it is part of at least one other ring
                } else {
                    //current ring is marked as fused or spiro
                    ringIdentifierToIsFusedOrSpiroMap.put(ringID, true);
                    //set of all identifiers of all rings already visited that this atom is part of
                    Set<String> ringIDSet = atomIDToRingIDMap.get(atomID);
                    //they are marked as fused or spiro since they share at least the current atom with another ring
                    for (String alreadyVisitedRingID : ringIDSet) {
                        ringIdentifierToIsFusedOrSpiroMap.put(alreadyVisitedRingID, true);
                    }
                    //id of the current ring is added to the list
                    ringIDSet.add(ringID);
                }
            }
        }

        List<IAtomContainer> sugarCandidates = new ArrayList<>(isolatedRings.size());
        for (IAtomContainer isolatedRing : isolatedRings) {
            if (isolatedRing == null || isolatedRing.isEmpty()) {
                continue;
            }
            if (!includeSpiroRings) {
                //Filtering spiro rings if they should not be detected as sugars
                String ringID = this.generateSubstructureIdentifier(isolatedRing);
                //if true, the ring is fused or spiro according to the map; but since only isolated cycles are queried,
                // they are definitely spiro if the map returns true
                if (Boolean.TRUE.equals(ringIdentifierToIsFusedOrSpiroMap.get(ringID))) {
                    continue;
                }
            }
            for (IAtomContainer referenceRing : this.circularSugarStructuresList) {
                boolean isIsomorphic;
                UniversalIsomorphismTester univIsoTester = new UniversalIsomorphismTester();
                try {
                    isIsomorphic = univIsoTester.isIsomorph(referenceRing, isolatedRing);
                } catch (CDKException cdkException) {
                    SugarRemovalUtility.LOGGER.warn(cdkException);
                    continue;
                }
                if (isIsomorphic) {
                    /* note: another requirement of a suspected sugar ring should be that it contains only single bonds.
                     * This is not tested here because all the structures in the reference rings do meet this criterion.
                     * But a structure that does not meet this criterion could be added to the references by the user.*/
                    boolean areAllExocyclicBondsSingle = this.areAllExocyclicBondsSingle(isolatedRing, moleculeParam, ignoreKetoGroups);
                    if (!areAllExocyclicBondsSingle) {
                        //do not remove rings with non-single exocyclic bonds, they are not sugars (not an option!)
                        break;
                    }
                    /*identification of spiro atoms (the cycle is isolated, so it can share at max one atom with another cycle
                     * and this atom is therefore a spiro bridge); this is done only now to not disturb the removal of linear sugars
                     * that are part of cycles; the info is only needed if spiro ring are detected as sugars and not filtered
                     * according to the settings (but always noted here anyway, should the setting change between detection and removal)*/
                    for (IAtom atom : isolatedRing.atoms()) {
                        int atomID = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                        //note: the id HAS TO be in the map
                        Set<String> ringIDSet = atomIDToRingIDMap.get(atomID);
                        int size = ringIDSet.size();
                        /* if size > 1 atom is part of multiple rings and therefore the spiro bridge of two cycles, so
                         * keep it at removal of the sugar to protect the adjacent ring!
                         * note: the removal method has to test for the presence of the property anyway, so adding it with
                         * value 'false' to the other atoms in the ring is redundant */
                        if (size > 1) {
                            atom.setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, true);
                        }
                    }
                    sugarCandidates.add(isolatedRing);
                    break;
                } //else {continue;}
            }
        }
        return sugarCandidates;
    }

    /**
     * Checks whether all exocyclic bonds connected to a given ring fragment of
     * a parent atom container are of single order. If the option to allow
     * potential sugar cycles having keto groups is activated, this method also
     * returns true if a cycle having a keto group is processed.
     * <br>The method iterates over all cyclic atoms and all of their bonds. So
     * the runtime scales linear with the number
     * of cyclic atoms and their connected bonds. In principle, this method can
     * be used also for non-cyclic substructures.
     * <br>Note: It is not tested whether the original molecule is actually the
     * parent of the ring to test.
     *
     * @param ringToTest       the ring fragment to test; exocyclic bonds do not
     *                         have to be included in the fragment but if it is
     *                         a fused system of multiple rings, the internal
     *                         interconnecting bonds of the different rings need
     *                         to be included; all its atoms need to be exactly
     *                         the same objects as in the second atom container
     *                         parameter
     * @param originalMolecule the molecule that contains the ring under
     *                         investigation; The exocyclic bonds will be
     *                         queried from it
     * @param ignoreKetoGroups true if this method should ignore keto groups,
     *                         i.e. also return true if there are some attached
     *                         to the cycle
     * @return true, if all exocyclic bonds connected to the ring are of single
     *         order
     */
    protected boolean areAllExocyclicBondsSingle(IAtomContainer ringToTest, IAtomContainer originalMolecule, boolean ignoreKetoGroups) {
        if (ringToTest.isEmpty() || originalMolecule.isEmpty()) {
            return true;
        }
        int atomCountInRing = ringToTest.getAtomCount();
        int arrayListInitCapacity = atomCountInRing * 2;
        List<IBond> exocyclicBondsList = new ArrayList<>(arrayListInitCapacity);
        Iterable<IAtom> ringAtoms = ringToTest.atoms();
        for (IAtom ringAtom : ringAtoms) {
            if (!originalMolecule.contains(ringAtom)) {
                continue;
            }
            List<IBond> connectedBondsList = originalMolecule.getConnectedBondsList(ringAtom);
            for (IBond bond : connectedBondsList) {
                boolean isInRing = ringToTest.contains(bond);
                if (!isInRing) {
                    exocyclicBondsList.add(bond);
                }
            }
        }
        if (ignoreKetoGroups) {
            for (IBond bond : exocyclicBondsList) {
                IBond.Order order = bond.getOrder();
                //if the loop is not exited via return, true is returned after its completion
                if (order != IBond.Order.SINGLE) {
                    //if the bond order is double, check for keto group; otherwise, return false
                    if (order == IBond.Order.DOUBLE) {
                        boolean containsOxygen = false;
                        for (IAtom atom : bond.atoms()) {
                            if (atom.getAtomicNumber() == IElement.O) {
                                containsOxygen = true;
                            }
                        }
                        //if the bond contains oxygen, it is a keto group, because it double-bound
                        // note: it is not checked whether the oxygen is outside the ring, not inside, which is
                        // hardly possible because the bond is exocyclic, the oxygen would be four-bound in total.
                        // note 2: it is also not checked whether the oxygen has more bonds in addition to this double
                        // bond, but this would also be not chemically intuitive.
                        if (!containsOxygen) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return (BondManipulator.getMaximumBondOrder(exocyclicBondsList) == IBond.Order.SINGLE);
        }
    }

    /**
     * Checks all exocyclic connections of the given ring to detect an
     * O-glycosidic bond. Checklist for glycosidic bond: Connected oxygen atom
     * that is not in the ring, has two bonds that are both of single order and
     * no bond partner is a hydrogen atom. This algorithm also classifies ester
     * bonds as glycosidic bonds and any other bond type that meets the above
     * criteria. Therefore, many 'non-classical, glycoside-like' connections are
     * classified as O-glycosidic bonds.
     * <br>Note: The 'ring' is not tested for whether it is circular or not. So
     * theoretically, this method
     * can also be used to detect glycosidic bonds of linear structures. BUT:
     * The oxygen atom must not be part of the structure itself. Due to the
     * processing of candidate linear sugar moieties this can make it difficult
     * to use this method also for linear sugars.
     * <br>Note: It is not tested whether the original molecule is actually the
     * parent of the ring to test.
     *
     * @param ringToTest       the candidate sugar ring
     * @param originalMolecule the molecule in which the ring is contained as a
     *                         substructure to query the connected atoms from
     * @return true, if a glycosidic bond is detected
     */
    protected boolean hasGlycosidicBond(IAtomContainer ringToTest, IAtomContainer originalMolecule) {
        if (ringToTest.isEmpty() || originalMolecule.isEmpty()) {
            return false;
        }
        Iterable<IAtom> ringAtoms = ringToTest.atoms();
        boolean containsGlycosidicBond = false;
        for (IAtom ringAtom : ringAtoms) {
            boolean breakOuterLoop = false;
            //check to avoid exceptions
            if (!originalMolecule.contains(ringAtom)) {
                continue;
            }
            List<IAtom> connectedAtomsList = originalMolecule.getConnectedAtomsList(ringAtom);
            for (IAtom atom : connectedAtomsList) {
                boolean isInRing = ringToTest.contains(atom);
                if (!isInRing) {
                    boolean isOxygen = atom.getAtomicNumber() == IElement.O;
                    if (isOxygen) {
                        List<IBond> connectedBondsList = originalMolecule.getConnectedBondsList(atom);
                        boolean hasOnlyTwoBonds = (connectedBondsList.size() == 2);
                        boolean areAllBondsSingle =
                                (BondManipulator.getMaximumBondOrder(connectedBondsList) == IBond.Order.SINGLE);
                        boolean isOneBondAtomHydrogen = false;
                        for (IBond bond : connectedBondsList) {
                            for (IAtom bondAtom : bond.atoms()) {
                                if (bondAtom.getAtomicNumber() == IElement.H) {
                                    isOneBondAtomHydrogen = true;
                                }
                            }
                        }
                        if ((hasOnlyTwoBonds && areAllBondsSingle) && !isOneBondAtomHydrogen) {
                            containsGlycosidicBond = true;
                            breakOuterLoop = true;
                            break;
                        }
                    }
                }
            }
            if (breakOuterLoop) {
                break;
            }
        }
        return containsGlycosidicBond;
    }

    /**
     * Checks whether the given molecule would be empty after removal of the
     * given ring. Any remaining fragment will be cleared away if it is too
     * small according to the set preservation mode option. The given parameters
     * are not altered, copies of them are generated and processed. This method
     * is intended to test for whether a molecule qualifies for the gylcosidic
     * bond exemption.
     *
     * @param ringParam     the ring to test whether its removal would result in
     *                      an empty molecule
     * @param moleculeParam the parent molecule
     * @return true if the parent molecule is empty after removal of the given
     *         ring and subsequent removal of too small remaining fragments
     */
    protected boolean isMoleculeEmptyAfterRemovalOfThisRing(IAtomContainer ringParam, IAtomContainer moleculeParam) {
        if (!this.checkUniqueIndicesOfAtoms(moleculeParam)) {
            this.addUniqueIndicesToAtoms(moleculeParam);
        }

        boolean isMoleculeEmptyAfterRemoval;
        IAtomContainer moleculeCopy = this.basicCopy(moleculeParam);
        float loadFactor = 0.75f;
        int indexToAtomMapInitCapacity = (int)(moleculeCopy.getAtomCount() * (1.0f / loadFactor) + 2.0f);
        HashMap<Integer, IAtom> indexToAtomMap = new HashMap<>(indexToAtomMapInitCapacity, loadFactor);
        for (IAtom atom : moleculeCopy.atoms()) {
            indexToAtomMap.put(atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY), atom);
        }
        for (IAtom atom : ringParam.atoms()) {
            moleculeCopy.removeAtom(indexToAtomMap.get((int)atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY)));
        }
        if (moleculeCopy.isEmpty()) {
            isMoleculeEmptyAfterRemoval = true;
        } else {
            this.removeTooSmallDisconnectedStructures(moleculeCopy);
            isMoleculeEmptyAfterRemoval = moleculeCopy.isEmpty();
        }
        return isMoleculeEmptyAfterRemoval;
    }

    /**
     * Returns the number of attached exocyclic oxygen atoms of a given ring in
     * the original atom container. The method iterates over all cyclic atoms
     * and all of their connected atoms. So the runtime scales linear with the
     * number of cyclic atoms and their connected atoms. The oxygen atoms are
     * not tested for being attached by a single bond since in the algorithm,
     * the determination whether a candidate sugar ring has only exocyclic
     * single bonds precedes the calling of this method.
     * <br>Note: The circularity of the given 'ring' is not tested, so this
     * method could in theory also be used for linear
     * structures. But his does not make much sense.
     * <br>Note: This method does NOT check for hydroxy groups but for oxygen
     * atoms. So e.g. the oxygen atom in a
     * glycosidic bond is counted.
     * <br>Note: It is not tested whether the original molecule is actually the
     * parent of the ring to test.
     *
     * @param ringToTest       the ring fragment to test; exocyclic bonds do not
     *                         have to be included in the fragment but if it is
     *                         a fused system of multiple rings, the internal
     *                         interconnecting bonds of the different rings need
     *                         to be included; all its atoms need to be exactly
     *                         the same objects as in the second atom container
     *                         parameter (they will be skipped otherwise)
     * @param originalMolecule the molecule that contains the ring under
     *                         investigation; The exocyclic bonds will be
     *                         queried from it
     * @return number of attached exocyclic oxygen atoms of the given ring
     */
    protected int getExocyclicOxygenAtomCount(IAtomContainer ringToTest, IAtomContainer originalMolecule) {
        int exocyclicOxygenCounter = 0;
        Iterable<IAtom> ringAtoms = ringToTest.atoms();
        for (IAtom ringAtom : ringAtoms) {
            //check to avoid exceptions
            if (!originalMolecule.contains(ringAtom)) {
                continue;
            }
            List<IAtom> connectedAtomsList = originalMolecule.getConnectedAtomsList(ringAtom);
            for (IAtom connectedAtom : connectedAtomsList) {
                boolean isOxygen = connectedAtom.getAtomicNumber() == IElement.O;
                boolean isInRing = ringToTest.contains(connectedAtom);
                if (isOxygen && !isInRing) {
                    exocyclicOxygenCounter++;
                }
            }
        }
        return exocyclicOxygenCounter;
    }

    /**
     * Simple decision-making function for deciding whether a candidate sugar
     * ring has enough attached, single-bonded exocyclic oxygen atoms according
     * to the set threshold. The given number of oxygen atoms is divided by the
     * given number of atoms in the ring (should also contain the usually
     * present oxygen atom in a sugar ring) and the resulting ratio is checked
     * for being equal or higher than the currently set threshold.
     * <br>Note: Only the number of atoms in the ring is checked for not being
     * 0. No further parameter tests are
     * implemented. If the number is 0, false is returned. No exceptions are
     * thrown.
     *
     * @param numberOfAtomsInRing                  number of atoms in the
     *                                             possible sugar ring,
     *                                             including the cyclic oxygen
     *                                             atom
     * @param numberOfAttachedExocyclicOxygenAtoms number of attached exocyclic
     *                                             oxygen atoms of the ring
     *                                             under investigation (if zero,
     *                                             false is returned)
     * @return true, if the calculated ratio is equal to or higher than the
     *         currently set threshold
     */
    protected boolean doesRingHaveEnoughExocyclicOxygenAtoms(int numberOfAtomsInRing,
                                                             int numberOfAttachedExocyclicOxygenAtoms) {
        if (numberOfAtomsInRing == 0) {
            return false;
        }
        double attachedOxygensToAtomsInRingRatio =
                ((double) numberOfAttachedExocyclicOxygenAtoms / (double) numberOfAtomsInRing);
        return attachedOxygensToAtomsInRingRatio >= this.exocyclicOxygenAtomsToAtomsInRingRatioThresholdSetting;
    }

    /**
     * All linear sugar patterns represented by atom containers in the
     * respective list are sorted, parsed into actual pattern objects, and stored
     * in the internal list for initial linear sugar detection. To be called
     * when a linear sugar pattern has been deleted or added to the list. It
     * cannot directly be operated on the pattern objects because they cannot be
     * sorted or represented in a human-readable format.
     */
    protected void updateLinearSugarPatterns() {
        Comparator<IAtomContainer> comparator = new AtomContainerComparator().reversed();
        //note: this can throw various exceptions, but they should not appear here
        this.linearSugarStructuresList.sort(comparator);
        //parsing linear sugars into patterns; this has to be re-done completely because the patterns cannot be sorted
        for (IAtomContainer sugarAC : this.linearSugarStructuresList){
            try {
                this.linearSugarPatternsList.add(DfPattern.findSubstructure(sugarAC));
            } catch (Exception exception) {
                SugarRemovalUtility.LOGGER.warn(exception);
            }
        }
    }

    /**
     * Initial detection of linear sugar candidates by substructure search for
     * the linear sugar patterns in the given molecule. All 'unique' matches are
     * returned as atom container objects. this means that the same substructure
     * will not be included multiple times but the substructures may overlap.
     *
     * @param moleculeParam the molecule to search for linear sugar candidates
     * @return a list of possibly overlapping substructures from the given
     *         molecule matching the internal linear sugar patterns
     */
    protected List<IAtomContainer> detectLinearSugarCandidatesByPatternMatching(IAtomContainer moleculeParam) {
        if (moleculeParam.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> sugarCandidates = new ArrayList<>(moleculeParam.getAtomCount() / 2);
        int listSize = this.linearSugarPatternsList.size();
        List<DfPattern> listToIterate = new ArrayList<>(listSize);
        listToIterate.addAll(0, this.linearSugarPatternsList);
        for (DfPattern linearSugarPattern : listToIterate) {
            if (linearSugarPattern == null) {
                continue;
            }
            /*unique in this case means that the same match cannot be in this
            collection multiple times, but they can still overlap!*/
            Mappings mappings = linearSugarPattern.matchAll(moleculeParam);
            Mappings uniqueMappings = mappings.uniqueAtoms();
            Iterable<IAtomContainer> uniqueSubstructureMappings = uniqueMappings.toSubstructures();
            for (IAtomContainer matchedStructure : uniqueSubstructureMappings) {
                if (matchedStructure == null) {
                    continue;
                }
                sugarCandidates.add(matchedStructure);
            }
        }
        return sugarCandidates;
    }

    /**
     * Combines all overlapping (i.e. sharing the same atoms or bonds)
     * structures in the given list into one atom container, respectively, to
     * return distinct, non-overlapping substructures. Second step of linear
     * sugar detection. Note: The returned substructures can grow very big. This
     * is addressed in the third step. The parameter list is not altered and a
     * completely new list returned.
     *
     * @param candidateList a list of possibly overlapping substructures from
     *                      the same atom container object
     * @return a list of distinct, non-overlapping substructures after combining
     *         every formerly overlapping structure
     */
    protected List<IAtomContainer> combineOverlappingCandidates(List<IAtomContainer> candidateList) {
        if (candidateList == null || candidateList.isEmpty()) {
            return new ArrayList<>(0);
        }
        int listSize = candidateList.size();
        List<IAtomContainer> nonOverlappingSugarCandidates = new ArrayList<>(listSize);
        IAtomContainer matchesContainer = candidateList.get(0).getBuilder().newInstance(IAtomContainer.class);
        for (IAtomContainer candidate : candidateList) {
            if (candidate == null) {
                continue;
            }
            matchesContainer.add(candidate);
        }
        if (ConnectivityChecker.isConnected(matchesContainer)) {
            nonOverlappingSugarCandidates.add(matchesContainer);
        } else {
            IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(matchesContainer);
            Iterable<IAtomContainer> molecules = components.atomContainers();
            for (IAtomContainer component : molecules) {
                nonOverlappingSugarCandidates.add(component);
            }
        }
        return nonOverlappingSugarCandidates;
    }

    /**
     * Splits all ether, ester, and peroxide bonds in the given linear sugar
     * candidates and separates those that get disconnected in the process.
     * Third step of linear sugar detection. This step was introduced because
     * the linear sugar candidates returned by the combination method can be
     * very big and contain connected sugar chains that should be detected as
     * separate candidates. The detection is done using SMARTS patterns that are
     * constants of this class. The parameter list is not altered and a
     * completely new list returned.
     *
     * @param candidateList a list of potential sugar substructures from the
     *                      same atom container object
     * @return a new list of candidates where all ether, ester, and peroxide
     *         bonds have been split and disconnected candidates separated
     */
    protected List<IAtomContainer> splitEtherEsterAndPeroxideBondsExtraction(List<IAtomContainer> candidateList) {
        if (candidateList == null ||candidateList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> processedCandidates = new ArrayList<>(candidateList.size() * 2);
        for (IAtomContainer candidate : candidateList) {
            if (candidate == null) {
                continue;
            }
            SmartsPattern.prepare(candidate);

            //note: ester matching has to precede the ether matching because
            // the ether pattern also matches esters
            //note 2: here, which bond is removed is specifically defined. This
            // is not the case for the ether
            Mappings esterMappings = SmartsPattern.create(SugarRemovalUtility.ESTER_SMARTS_PATTERN).matchAll(candidate).uniqueAtoms();
            if (esterMappings.atLeast(1)) {
                for (IAtomContainer esterGroup : esterMappings.toSubstructures()) {
                    IAtom doubleBondedOxygen = null;
                    IAtom connectingOxygen = null;
                    for (IAtom atom : esterGroup.atoms()) {
                        if (atom.getAtomicNumber() == IElement.O) {
                            int bondCount = atom.getBondCount();
                            if (bondCount == 1) {
                                doubleBondedOxygen = atom;
                            } else {
                                connectingOxygen = atom;
                            }
                        }
                    }
                    IAtom carbonBoundToDoubleBondedOxygen = esterGroup.getConnectedAtomsList(doubleBondedOxygen).get(0);
                    candidate.removeBond(carbonBoundToDoubleBondedOxygen, connectingOxygen);
                }
            }

            //note: which bond is actually removed is 'pseudo-random', i.e. not
            // predictable by a human
            Mappings etherMappings = SmartsPattern.create(SugarRemovalUtility.ETHER_SMARTS_PATTERN).matchAll(candidate).uniqueAtoms();
            if (etherMappings.atLeast(1)) {
                for (IAtomContainer etherGroup : etherMappings.toSubstructures()) {
                    IAtom carbon1 = null;
                    IAtom carbon2 = null;
                    IAtom oxygen = null;
                    for (IAtom atom : etherGroup.atoms()) {
                        if (atom.getAtomicNumber() == IElement.O) {
                            oxygen = atom;
                        } else if (atom.getAtomicNumber() == IElement.C && carbon1 == null) {
                            carbon1 = atom;
                        } else {
                            carbon2 = atom;
                        }
                    }
                    candidate.removeBond(oxygen, carbon2);
                }
            }

            Mappings peroxideMappings = SmartsPattern.create(SugarRemovalUtility.PEROXIDE_SMARTS_PATTERN).matchAll(candidate).uniqueAtoms();
            if (peroxideMappings.atLeast(1)) {
                for (IAtomContainer peroxideGroup : peroxideMappings.toSubstructures()) {
                    IAtom oxygen1 = null;
                    IAtom oxygen2 =  null;
                    for (IAtom atom : peroxideGroup.atoms()) {
                        if (atom.getAtomicNumber() == IElement.O) {
                            if (oxygen1 == null) {
                                oxygen1 = atom;
                            } else {
                                oxygen2 = atom;
                            }
                        }
                    }
                    candidate.removeBond(oxygen1, oxygen2);
                }
            }

            boolean isConnected = ConnectivityChecker.isConnected(candidate);
            if (isConnected) {
                processedCandidates.add(candidate);
            } else {
                IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(candidate);
                for (IAtomContainer component : components.atomContainers()) {
                    processedCandidates.add(component);
                }
            }
        }
        return processedCandidates;
    }

    /**
     * Removes all atoms belonging to possible circular sugars, as returned by
     * the method for initial circular sugar detection, from the given linear
     * sugar candidates. Fourth step of linear sugar detection. The linear sugar
     * patterns also match parts of circular sugar, so this step has to be done
     * to ensure the separate treatment of circular and linear sugars. After the
     * removal, disconnected candidates are separated into new candidates. Note:
     * here, the given list is altered, unlike in some other methods! Therefore,
     * the list is not returned again. Note also that it is not checked whether
     * the given parent molecule is actually the parent of the given
     * substructures.
     *
     * @param candidateList  a list of potential sugar substructures from the
     *                       same atom container object
     * @param parentMolecule the molecule that is currently scanned for linear
     *                       sugars to detect its circular sugars
     */
    protected void removeAtomsOfCircularSugarsFromCandidates(List<IAtomContainer> candidateList,
                                                             IAtomContainer parentMolecule) {
        if (candidateList == null || candidateList.isEmpty()) {
            return;
        }
        if (!this.checkUniqueIndicesOfAtoms(parentMolecule)) {
            this.addUniqueIndicesToAtoms(parentMolecule);
        }

        //generating set of atom ids of atoms that are part of the circular sugars in the molecule
        List<IAtomContainer> potentialSugarRingsParent = this.detectPotentialSugarCycles(parentMolecule, true, true);
        //nothing to process
        if (potentialSugarRingsParent.isEmpty()) {
            return;
        }
        float loadFactor = 0.75f;
        int circularSugarAtomIDSetInitCapacity = (int)(7.0f * potentialSugarRingsParent.size() * (1.0f / loadFactor) + 2.0f);
        HashSet<Integer> circularSugarAtomIDSet = new HashSet<>(circularSugarAtomIDSetInitCapacity, loadFactor);
        for (IAtomContainer circularSugarCandidate : potentialSugarRingsParent) {
            for (IAtom atom : circularSugarCandidate.atoms()) {
                int atomIndex = atom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                circularSugarAtomIDSet.add(atomIndex);
            }
        }
        //iterating over candidates
        for (int i = 0; i < candidateList.size(); i++) {
            IAtomContainer candidate = candidateList.get(i);
            if (candidate == null) {
                candidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            if (!this.checkUniqueIndicesOfAtoms(candidate)) {
                this.addUniqueIndicesToAtoms(candidate);
            }
            for (IAtom candidateAtom : candidate.atoms()) {
                int atomIndex = candidateAtom.getProperty(SugarRemovalUtility.INDEX_PROPERTY_KEY);
                if (circularSugarAtomIDSet.contains(atomIndex) && (candidate.contains(candidateAtom))) {
                        candidate.removeAtom(candidateAtom);
                }
            }
            //remove the candidate if it is empty after removal of cycles
            if (candidate.isEmpty()) {
                candidateList.remove(i);
                i = i - 1;
                continue;
            }
            //if the candidate got unconnected by the removal of cycles, split the parts in separate candidates
            boolean isConnected = ConnectivityChecker.isConnected(candidate);
            if (!isConnected) {
                IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(candidate);
                for (IAtomContainer component : components.atomContainers()) {
                    candidateList.add(component);
                }
                candidateList.remove(i);
                i = i - 1;
            }
        }
    }

    /**
     * Removes all atoms that are part of a cycle from the given linear sugar
     * candidates. Optional fifth step of linear sugar detection. The linear
     * sugar patterns can also match in cycles that do not represent circular
     * sugars but e.g. pseudo-sugars or macrocycles. It is optional to detect
     * linear sugars in such structures or not. After the removal, disconnected
     * candidates are separated into new candidates. Note: here, the given list
     * is altered, unlike in some other methods! Therefore, the list is not
     * returned again. Note also that it is not checked whether the given parent
     * molecule is actually the parent of the given substructures.
     *
     * @param candidateList a list of potential sugar substructures from the
     *                      same atom container object
     * @param moleculeParam the molecule that is currently scanned for linear
     *                      sugars to detect its cycles
     */
    protected void removeCyclicAtomsFromSugarCandidates(List<IAtomContainer> candidateList,
                                                        IAtomContainer moleculeParam) {
        if (candidateList == null || candidateList.isEmpty()) {
            return;
        }
        int[][] adjList = GraphUtil.toAdjList(moleculeParam);
        RingSearch ringSearch = new RingSearch(moleculeParam, adjList);
        boolean moleculeHasRings = ringSearch.numRings() > 0;
        if (!moleculeHasRings) {
            //nothing to process
            return;
        }
        for (int i = 0; i < candidateList.size(); i++) {
            IAtomContainer candidate = candidateList.get(i);
            if (candidate == null) {
                candidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
                continue;
            }
            for (int j = 0; j < candidate.getAtomCount(); j++) {
                IAtom atom = candidate.getAtom(j);
                if (ringSearch.cyclic(atom) && candidate.contains(atom)) {
                    candidate.removeAtom(atom);
                    //The removal shifts the remaining indices!
                    j = j - 1;
                }
            }
            if (candidate.isEmpty()) {
                candidateList.remove(i);
                //The removal shifts the remaining indices!
                i = i - 1;
            }
            //if the candidate got unconnected by the removal of cycles, split the parts in separate candidates
            boolean isConnected = ConnectivityChecker.isConnected(candidate);
            if (!isConnected) {
                IAtomContainerSet components = ConnectivityChecker.partitionIntoMolecules(candidate);
                for (IAtomContainer component : components.atomContainers()) {
                    candidateList.add(component);
                }
                candidateList.remove(i);
                i = i - 1;
            }
        }
    }

    /**
     * Discards all linear sugar candidates that are too small or too big
     * according to the current settings. Final step of linear sugar detection.
     * This step was introduced because the preceding steps may produce small
     * 'fragments', e.g. the hydroxy group of a circular sugar that was removed
     * from a linear sugar candidate. These should be filtered out. ALso, a very
     * large linear sugar that does not consist of multiple subunits linked by
     * ether, ester, or peroxide bonds is considered too interesting to remove
     * and should therefore also be filtered from the linear sugars detected for
     * removal. The 'size' of the linear sugar candidates is determined as their
     * carbon atom count. The set minimum and maximum sizes are inclusive. The
     * parameter list is not altered and a completely new list returned.
     *
     * @param candidateList a list of potential sugar substructures from the
     *                      same atom container object
     * @return a new list of candidates where all too small and too big
     *         candidates have been filtered out
     */
    protected List<IAtomContainer> removeTooSmallAndTooLargeCandidates(List<IAtomContainer> candidateList) {
        if (candidateList == null || candidateList.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<IAtomContainer> processedCandidates = new ArrayList<>(candidateList.size());
        for (IAtomContainer candidate : candidateList) {
            int carbonCount = 0;
            for (IAtom atom : candidate.atoms()) {
                if (atom.getAtomicNumber() == IElement.C) {
                    carbonCount++;
                }
            }
            if (carbonCount >= this.linearSugarCandidateMinSizeSetting
                    && carbonCount <= this.linearSugarCandidateMaxSizeSetting) {
                processedCandidates.add(candidate);
            }
        }
        return processedCandidates;
    }
}

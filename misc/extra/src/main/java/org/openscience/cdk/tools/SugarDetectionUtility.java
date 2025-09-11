/*
 * Copyright (c) 2025 Jonas Schaub <jonas.schaub@uni-jena.de>
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

import org.openscience.cdk.Bond;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.smarts.SmartsPattern;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class for detecting and extracting sugar moieties from molecular structures.
 *
 * <p>This class extends {@link SugarRemovalUtility} to provide functionality for separating
 * glycosides into their aglycone and glycosidic components.
 * The main feature is the ability to create copies of both the aglycone
 * and individual sugar fragments from a given molecule, with proper handling of attachment
 * points and stereochemistry, and some optional postprocessing possibilities.
 *
 * <p>The extraction process supports:
 * <ul>
 *   <li>Detection and extraction of both circular and linear sugar moieties</li>
 *   <li>Preservation of stereochemistry at connection points</li>
 *   <li>Proper saturation of broken bonds with either R-groups or implicit hydrogen atoms</li>
 *   <li>Post-processing of sugar fragments including bond splitting (O-glycosidic, ether, ester, peroxide)</li>
 *   <li>Duplication of connecting heteroatoms (oxygen, nitrogen, sulfur) in glycosidic bonds, to produce more sensible educts</li>
 *   <li>Optional mapping of atoms and bonds from the original molecule to their copies in the aglycone and sugar fragments</li>
 * </ul>
 *
 * <p>All sugar detection and removal operations respect the settings inherited from the
 * parent {@link SugarRemovalUtility} class, including terminal vs. non-terminal sugar
 * removal, preservation mode settings, various detection thresholds, etc. In two cases, the initial
 * {@link SugarRemovalUtility} results are corrected for extraction:
 * <ul>
 *     <li>When a sugar would lose its C6</li>
 *     <li>When a sugar is on the "carboxy end" of an ester bond to the aglycone</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * SugarDetectionUtility utility = new SugarDetectionUtility(SilentChemObjectBuilder.getInstance());
 * //check the overloaded variants of this method for more options
 * List<IAtomContainer> fragments = utility.copyAndExtractAglyconeAndSugars(molecule);
 * IAtomContainer aglycone = fragments.get(0);  // First element is always the aglycone
 * // Subsequent elements are individual sugar fragments
 * }</pre>
 *
 * @author Jonas Schaub (jonas.schaub@uni-jena.de | jonas-schaub@gmx.de | <a href="https://github.com/JonasSchaub">JonasSchaub on GitHub</a>)
 */
public class SugarDetectionUtility extends SugarRemovalUtility {

    /**
     * SMARTS pattern for detecting glycosidic bonds (ether bonds) between circular sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C in a ring with degree 3 or 4 and no charge, connected to an aliphatic O not in a ring
     * with degree 2 and no charge, connected to an aliphatic C with no charge (this side is left more promiscuous for
     * matching cases like linear sugars connected to circular sugars or some corner cases).
     */
    public static final String O_GLYCOSIDIC_BOND_CIRCULAR_SUGARS_SMARTS = "[C;R;D3,D4;+0]-!@[O;!R;D2;+0]-!@[C;+0]";

    /**
     * SMARTS pattern for detecting ester bonds between circular sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C in a ring with degree 3 or 4 and no charge, connected to a carbonyl oxygen (environment)
     * and to another oxygen atom via a non-ring bond, which is connected in turn to another aliphatic carbon atom
     * (this side is left more promiscuous for matching cases like linear sugars connected to circular sugars or some corner cases).
     */
    public static final String ESTER_BOND_CIRCULAR_SUGARS_SMARTS = "[C;R;D3,D4;+0;$(C=!@[O;!R;+0])]-!@[O;!R;D2;+0]-!@[C;+0]";

    /**
     * SMARTS pattern for detecting peroxide bonds between circular sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C in a ring with degree 3 or 4 and no charge, connected to an oxygen atom via a non-ring bond,
     * which is connected in turn to another oxygen atom and that to another aliphatic carbon atom
     * (this side is left more promiscuous for matching cases like linear sugars connected to circular sugars or some corner cases).
     */
    public static final String PEROXIDE_BOND_CIRCULAR_SUGARS_SMARTS = "[C;R;D3,D4;+0]-!@[O;!R;D2;+0]-!@[O;!R;D2;+0]-!@[C;+0]";

    /**
     * SMARTS pattern for detecting ester bonds between linear sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C not in a ring, with no charge, connected to a carbonyl oxygen (environment) and to another
     * oxygen atom via a non-ring bond, which is connected in turn to another aliphatic carbon atom.
     */
    public static final String ESTER_BOND_LINEAR_SUGARS_SMARTS = "[C;!R;+0;$(C=!@[O;!R;+0])]-!@[O;!R;D2;+0]-!@[C;!R;+0]";

    /**
     * SMARTS pattern for detecting cross-linking ether bonds between linear sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C not in a ring, with no charge, connected to the ether oxygen atom
     * via a non-ring bond, which is connected in turn to another aliphatic carbon atom that also has a hydroxy group
     * connected to it (to define the cross-linking nature).
     */
    public static final String CROSS_LINKING_ETHER_BOND_LINEAR_SUGARS_SMARTS = "[C;!R;+0]-!@[O;!R;D2;+0]-!@[C;!R;+0;$(C-!@[OH1;!R;+0])]";

    /**
     * SMARTS pattern for detecting ether bonds between linear sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C not in a ring, with no charge, connected to an oxygen atom
     * via a non-ring bond, which is connected in turn to another aliphatic carbon atom.
     */
    public static final String ETHER_BOND_LINEAR_SUGARS_SMARTS = "[C;!R;+0]-!@[O;!R;D2;+0]-!@[C;!R;+0]";

    /**
     * SMARTS pattern for detecting peroxide bonds between linear sugar moieties for postprocessing after extraction.
     * Defines an aliphatic C not in a ring, with no charge, connected to an oxygen atom
     * via a non-ring bond, which is connected in turn to another oxygen atom and that to another aliphatic carbon atom.
     */
    public static final String PEROXIDE_BOND_LINEAR_SUGARS_SMARTS = "[C;!R;+0]-!@[O;!R;D2;+0]-!@[O;!R;D2;+0]-!@[C;!R;+0]";

    /**
     * Default for extractCircularSugars parameter in copyAndExtractAglyconeAndSugars methods.
     */
    public static final boolean EXTRACT_CIRCULAR_SUGARS_DEFAULT = true;

    /**
     * Default for extractLinearSugars parameter in copyAndExtractAglyconeAndSugars methods.
     */
    public static final boolean EXTRACT_LINEAR_SUGARS_DEFAULT = false;

    /**
     * Default for markAttachPointsByR parameter in copyAndExtractAglyconeAndSugars methods.
     */
    public static final boolean MARK_ATTACH_POINTS_BY_R_DEFAULT = false;

    /**
     * Default for postProcessSugars parameter in copyAndExtractAglyconeAndSugars methods.
     */
    public static final boolean POST_PROCESS_SUGARS_DEFAULT = false;

    /**
     * Default for limitPostProcessingBySize parameter in copyAndExtractAglyconeAndSugars methods.
     */
    public static final boolean LIMIT_POST_PROCESSING_BY_SIZE_DEFAULT = false;

    /**
     * Logger of this class.
     */
    private static final ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(SugarDetectionUtility.class);

    /**
     * Sole constructor of this class. All settings are set to their default
     * values as declared in the {@link SugarRemovalUtility} class.
     *
     * @param builder IChemObjectBuilder for i.a. parsing SMILES strings of
     *                sugar patterns into atom containers
     */
    public SugarDetectionUtility(IChemObjectBuilder builder) {
        super(builder);
    }

    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are saturated with implicit hydrogen atoms.
     * No postprocessing of the sugar fragments is performed, i.e. they are not separated
     * from each other if they are connected in the original structure.
     * Check the overloaded versions of this method for more options.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol
    ) {
        return this.copyAndExtractAglyconeAndSugars(
                mol,
                SugarDetectionUtility.EXTRACT_CIRCULAR_SUGARS_DEFAULT,
                SugarDetectionUtility.EXTRACT_LINEAR_SUGARS_DEFAULT,
                SugarDetectionUtility.MARK_ATTACH_POINTS_BY_R_DEFAULT,
                SugarDetectionUtility.POST_PROCESS_SUGARS_DEFAULT,
                SugarDetectionUtility.LIMIT_POST_PROCESSING_BY_SIZE_DEFAULT,
                null,
                null,
                null,
                null);
    }

    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular and/or linear
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are saturated with implicit hydrogen atoms.
     * No postprocessing of the sugar fragments is performed, i.e. they are not separated
     * from each other if they are connected in the original structure.
     * Check the overloaded versions of this method for more options.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @param extractCircularSugars If true, circular sugar moieties will be detected
     *                             and extracted according to current settings.
     * @param extractLinearSugars If true, linear sugar moieties will be detected
     *                           and extracted according to current settings.
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol,
            boolean extractCircularSugars,
            boolean extractLinearSugars
    ) {
        return this.copyAndExtractAglyconeAndSugars(
                mol,
                extractCircularSugars,
                extractLinearSugars,
                SugarDetectionUtility.MARK_ATTACH_POINTS_BY_R_DEFAULT,
                SugarDetectionUtility.POST_PROCESS_SUGARS_DEFAULT,
                SugarDetectionUtility.LIMIT_POST_PROCESSING_BY_SIZE_DEFAULT,
                null,
                null,
                null,
                null);
    }

    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular and/or linear
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are handled by either adding R-groups
     * (pseudo atoms) or implicit hydrogen atoms to saturate the broken bonds.
     * No postprocessing of the sugar fragments is performed, i.e. they are not separated
     * from each other if they are connected in the original structure.
     * Check the overloaded versions of this method for more options.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @param extractCircularSugars If true, circular sugar moieties will be detected
     *                             and extracted according to current settings.
     * @param extractLinearSugars If true, linear sugar moieties will be detected
     *                           and extracted according to current settings.
     * @param markAttachPointsByR If true, attachment points where sugars and the aglycone were connected
     *                           are marked with R-groups (pseudo atoms). If false,
     *                           implicit hydrogen atoms are added to saturate the connections.
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol,
            boolean extractCircularSugars,
            boolean extractLinearSugars,
            boolean markAttachPointsByR
    ) {
        return this.copyAndExtractAglyconeAndSugars(
                mol,
                extractCircularSugars,
                extractLinearSugars,
                markAttachPointsByR,
                SugarDetectionUtility.POST_PROCESS_SUGARS_DEFAULT,
                SugarDetectionUtility.LIMIT_POST_PROCESSING_BY_SIZE_DEFAULT,
                null,
                null,
                null,
                null);
    }

    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular and/or linear
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are handled by either adding R-groups
     * (pseudo atoms) or implicit hydrogen atoms to saturate the broken bonds.
     * Optionally, postprocessing of the sugar fragments is performed, i.e. they are separated
     * from each other if they are connected in the original structure via O-glycosidic (ether),
     * ester, or peroxide bonds.
     * Check the overloaded versions of this method for more options.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @param extractCircularSugars If true, circular sugar moieties will be detected
     *                             and extracted according to current settings.
     * @param extractLinearSugars If true, linear sugar moieties will be detected
     *                           and extracted according to current settings.
     * @param markAttachPointsByR If true, attachment points where sugars and the aglycone were connected
     *                           are marked with R-groups (pseudo atoms). If false,
     *                           implicit hydrogen atoms are added to saturate the connections.
     * @param postProcessSugars If true, postprocessing of sugar fragments is performed, i.e. splitting O-glycosidic
     *                          (ether), ester, and peroxide bonds between sugar moieties
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure or were disconnected in postprocessing.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol,
            boolean extractCircularSugars,
            boolean extractLinearSugars,
            boolean markAttachPointsByR,
            boolean postProcessSugars
    ) {
        return this.copyAndExtractAglyconeAndSugars(
                mol,
                extractCircularSugars,
                extractLinearSugars,
                markAttachPointsByR,
                postProcessSugars,
                SugarDetectionUtility.LIMIT_POST_PROCESSING_BY_SIZE_DEFAULT,
                null,
                null,
                null,
                null);
    }

    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular and/or linear
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are handled by either adding R-groups
     * (pseudo atoms) or implicit hydrogen atoms to saturate the broken bonds.
     * Optionally, postprocessing of the sugar fragments is performed, i.e. they are separated
     * from each other if they are connected in the original structure via O-glycosidic (ether),
     * ester, or peroxide bonds. Optionally, this postprocessing
     * is limited by size, i.e. sugars and their substituents are only split if both resulting
     * parts are larger than the set preservation mode threshold for circular sugars and
     * the minimum size for linear sugar candidates, respectively, to prevent smaller substituents
     * being split off of the sugars.
     * Check the overloaded versions of this method for more options.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @param extractCircularSugars If true, circular sugar moieties will be detected
     *                             and extracted according to current settings.
     * @param extractLinearSugars If true, linear sugar moieties will be detected
     *                           and extracted according to current settings.
     * @param markAttachPointsByR If true, attachment points where sugars and the aglycone were connected
     *                           are marked with R-groups (pseudo atoms). If false,
     *                           implicit hydrogen atoms are added to saturate the connections.
     * @param postProcessSugars If true, postprocessing of sugar fragments is performed, i.e. splitting O-glycosidic
     *                          (ether), ester, and peroxide bonds between sugar moieties
     * @param limitPostProcessingBySize If true, sugar moieties will only be separated/split in postprocessing if they are larger
     *                                  than the set preservation mode threshold (see {@link SugarRemovalUtility}). This is
     *                                  to prevent smaller substituents like, e.g. methyl ethers, being separated from the
     *                                  sugars. For linear sugars, the minimum size for linear sugar candidates is applied
     *                                  as a criterion.
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure or were disconnected in postprocessing.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol,
            boolean extractCircularSugars,
            boolean extractLinearSugars,
            boolean markAttachPointsByR,
            boolean postProcessSugars,
            boolean limitPostProcessingBySize
    ) {
        return this.copyAndExtractAglyconeAndSugars(
                mol,
                extractCircularSugars,
                extractLinearSugars,
                markAttachPointsByR,
                postProcessSugars,
                limitPostProcessingBySize,
                null,
                null,
                null,
                null);
    }

    //do not copy the aglycone? -> too much of a hassle because for postprocessing, we repeatedly need the original structure
    //implement alternative method that directly returns group indices? -> blows up the code too much and the atom container fragments are the main point of reference
    /**
     * Extracts copies of the aglycone and sugar parts of the given molecule (if there are any).
     * <p>
     * This method creates a deep copy of the input molecule and removes circular and/or linear
     * sugar moieties to produce an aglycone. It then creates
     * a second copy to extract the sugar fragments that were removed. The attachment
     * points between the aglycone and sugars are handled by either adding R-groups
     * (pseudo atoms) or implicit hydrogen atoms to saturate the broken bonds.
     * Optionally, postprocessing of the sugar fragments is performed, i.e. they are separated
     * from each other if they are connected in the original structure via O-glycosidic (ether),
     * ester, or peroxide bonds. Optionally, this postprocessing
     * is limited by size, i.e. sugars and their substituents are only split if both resulting
     * parts are larger than the set preservation mode threshold for circular sugars and
     * the minimum size for linear sugar candidates, respectively, to prevent smaller substituents
     * being split off of the sugars.
     *
     * <p>The method preserves stereochemistry information at connection points and
     * handles glycosidic bonds appropriately. When bonds are broken between sugar
     * moieties and the aglycone, connecting heteroatoms (such as glycosidic oxygen,
     * nitrogen, or sulfur atoms) are copied to both the aglycone and sugar fragments
     * to maintain chemical validity.
     *
     * <p>The extraction process respects all current sugar detection settings as described in
     * {@link SugarRemovalUtility}, including terminal vs. non-terminal sugar removal,
     * preservation mode settings, various detection thresholds, etc.
     *
     * <p>Note that atom types are not copied, they have to be re-perceived if needed.</p>
     *
     * <p>This method additionally gives you the option to supply four maps as parameters that will be filled with a
     * mapping of atoms and bonds in the original molecule to the atoms and bonds in the aglycone and sugar copies. They
     * should be of sufficient size and empty when given.</p>
     *
     * @param mol The input molecule to separate into aglycone and sugar components.
     *            Must not be null but can be empty; a list containing only the empty given
     *            atom container is returned in the latter case.
     * @param extractCircularSugars If true, circular sugar moieties will be detected
     *                             and extracted according to current settings.
     * @param extractLinearSugars If true, linear sugar moieties will be detected
     *                           and extracted according to current settings.
     * @param markAttachPointsByR If true, attachment points where sugars and the aglycone were connected
     *                           are marked with R-groups (pseudo atoms). If false,
     *                           implicit hydrogen atoms are added to saturate the connections.
     * @param postProcessSugars If true, postprocessing of sugar fragments is performed, i.e. splitting O-glycosidic
     *                          (ether), ester, and peroxide bonds between sugar moieties
     * @param limitPostProcessingBySize If true, sugar moieties will only be separated/split in postprocessing if they are larger
     *                                  than the set preservation mode threshold (see {@link SugarRemovalUtility}). This is
     *                                  to prevent smaller substituents like, e.g. methyl ethers, being separated from the
     *                                  sugars. For linear sugars, the minimum size for linear sugar candidates is applied
     *                                  as a criterion.
     * @param inputAtomToAtomCopyInAglyconeMap Map to be filled with mappings from original atoms to their copies in the aglycone.
     *                                         Can be null (a new map will be created) but should be an empty map with sufficient capacity.
     * @param inputBondToBondCopyInAglyconeMap Map to be filled with mappings from original bonds to their copies in the aglycone.
     *                                         Can be null (a new map will be created) but should be an empty map with sufficient capacity.
     * @param inputAtomToAtomCopyInSugarsMap Map to be filled with mappings from original atoms to their copies in the sugar fragments.
     *                                       Can be null (a new map will be created) but should be an empty map with sufficient capacity.
     * @param inputBondToBondCopyInSugarsMap Map to be filled with mappings from original bonds to their copies in the sugar fragments.
     *                                       Can be null (a new map will be created) but should be an empty map with sufficient capacity.
     * @return A list of atom containers where the first element is the aglycone
     *         (copy molecule with sugars removed) and subsequent elements are the
     *         individual sugar fragments that were extracted (also copies). If no sugars were
     *         detected or removed, returns a list containing only a copy of the
     *         original molecule. Sugar fragments may be disconnected from each
     *         other if they were not directly linked in the original structure or were disconnected in postprocessing.
     * @throws NullPointerException if the input molecule is null
     * @see SugarRemovalUtility#removeCircularSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeLinearSugars(IAtomContainer)
     * @see SugarRemovalUtility#removeCircularAndLinearSugars(IAtomContainer)
     */
    public List<IAtomContainer> copyAndExtractAglyconeAndSugars(
            IAtomContainer mol,
            boolean extractCircularSugars,
            boolean extractLinearSugars,
            boolean markAttachPointsByR,
            boolean postProcessSugars,
            boolean limitPostProcessingBySize,
            Map<IAtom, IAtom> inputAtomToAtomCopyInAglyconeMap,
            Map<IBond, IBond> inputBondToBondCopyInAglyconeMap,
            Map<IAtom, IAtom> inputAtomToAtomCopyInSugarsMap,
            Map<IBond, IBond> inputBondToBondCopyInSugarsMap
    ) {
        //checks:
        if (mol == null) {
            throw new NullPointerException("Given molecule is null.");
        }
        if (mol.isEmpty() || (!extractCircularSugars && !extractLinearSugars)) {
            List<IAtomContainer> results = new ArrayList<>(1);
            results.add(mol);
            return results;
        }
        //setup and copying for aglycone:
        float loadFactor = 0.75f; //default load factor of HashMaps
        //ensuring sufficient initial capacity
        int atomMapInitCapacity = (int)((mol.getAtomCount() / loadFactor) + 3.0f);
        int bondMapInitCapacity = (int)((mol.getBondCount() / loadFactor) + 3.0f);
        if (inputAtomToAtomCopyInAglyconeMap == null) {
            inputAtomToAtomCopyInAglyconeMap = new HashMap<>(atomMapInitCapacity);
        }
        if (inputBondToBondCopyInAglyconeMap == null) {
            inputBondToBondCopyInAglyconeMap = new HashMap<>(bondMapInitCapacity);
        }
        IAtomContainer copyForAglycone = this.deeperCopy(mol, inputAtomToAtomCopyInAglyconeMap, inputBondToBondCopyInAglyconeMap);
        boolean wasSugarRemoved = false;
        if (extractCircularSugars && extractLinearSugars) {
            wasSugarRemoved = this.removeCircularAndLinearSugars(copyForAglycone);
        } else if (extractCircularSugars) {
            wasSugarRemoved = this.removeCircularSugars(copyForAglycone);
        } else if (extractLinearSugars) {
            //note: actually, extractLinearSugars must be true here if this is reached but the code was not simplified to have more clarity
            wasSugarRemoved = this.removeLinearSugars(copyForAglycone);
        } //else: wasSugarRemoved remains false, and input structure is returned, same as when no sugars were detected, see below
        if (!wasSugarRemoved) {
            List<IAtomContainer> results = new ArrayList<>(1);
            results.add(copyForAglycone);
            return results;
        }
        //sugars were found and removed from the aglycone, so carry on extracting the sugars:
        //copying for sugars
        if (inputAtomToAtomCopyInSugarsMap == null) {
            inputAtomToAtomCopyInSugarsMap = new HashMap<>(atomMapInitCapacity);
        }
        if (inputBondToBondCopyInSugarsMap == null) {
            inputBondToBondCopyInSugarsMap = new HashMap<>(bondMapInitCapacity);
        }
        IAtomContainer copyForSugars = this.deeperCopy(mol, inputAtomToAtomCopyInSugarsMap, inputBondToBondCopyInSugarsMap);
        //remove aglycone atoms from sugar container:
        //note: instead of copying the whole structure and removing the aglycone atoms, one could only copy those atoms
        // and bonds that are not part of the aglycone to form the sugars to save some memory but the code would be much
        // more complicated, so we don't do it that way for now
        boolean containsSpiroSugars = false;
        for (IAtom atom : mol.atoms()) {
            if (this.areSpiroRingsDetectedAsCircularSugars()
                    && inputAtomToAtomCopyInAglyconeMap.get(atom).getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY) != null) {
                //spiro atom that was marked as part of a circular sugar, so duplicate it (= do not remove it from the sugar) for correct extraction
                inputAtomToAtomCopyInSugarsMap.get(atom).setProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY, true);
                //boolean is needed below for postprocessing
                containsSpiroSugars = true;
                continue;
            }
            if (copyForAglycone.contains(inputAtomToAtomCopyInAglyconeMap.get(atom))) {
                copyForSugars.removeAtom(inputAtomToAtomCopyInSugarsMap.get(atom));
            }
        }
        //note that the four atom and bond maps still hold references to the atoms and bonds that were removed from the
        // two copies to get the aglycone and sugars; important for later queries; only cleared at the very end of this method
        //Preprocessing, correcting the SRU results in special cases:
        this.preprocessSugarRemovalResults(
                mol,
                copyForAglycone,
                copyForSugars,
                inputAtomToAtomCopyInAglyconeMap,
                inputBondToBondCopyInAglyconeMap,
                inputAtomToAtomCopyInSugarsMap,
                inputBondToBondCopyInSugarsMap);
        //general processing that does not need to correct the SRU results:
        //identify bonds that were broken between sugar moieties and aglycone
        // -> copy connecting hetero atoms (glycosidic O/N/S etc.) from one part (sugar or aglycone) to the other,
        // along with its stereo element
        // -> saturate with R or H, depending on the markAttachPointsByR parameter
        boolean hasIdentifiedBrokenBond = false;
        for (IBond bond : mol.bonds()) {
            //bond not in aglycone or sugars, so it was broken during sugar removal
            // (there is no else, we just want to find these broken bonds)
            if (!copyForAglycone.contains(inputBondToBondCopyInAglyconeMap.get(bond))
                    && !copyForSugars.contains(inputBondToBondCopyInSugarsMap.get(bond))) {
                hasIdentifiedBrokenBond = true;
                //if a hetero atom connected sugar and aglycone, copy it to the other side;
                // do nothing for C-C bonds or hetero-hetero connections like peroxides
                // (-> else is down below and just takes care of saturation)
                if ((this.isCarbonAtom(bond.getBegin()) && this.isHeteroAtom(bond.getEnd()))
                        || (this.isHeteroAtom(bond.getBegin()) && this.isCarbonAtom(bond.getEnd()))) {
                    //-> copy hetero atom to the other side and saturate it with H or R
                    //-> saturate "original" hetero atom with H or R
                    IAtom origHeteroAtom;
                    IAtom origCarbonAtom;
                    if (this.isCarbonAtom(bond.getBegin()) && this.isHeteroAtom(bond.getEnd())) {
                        origHeteroAtom = bond.getEnd();
                        origCarbonAtom = bond.getBegin();
                    } else if (this.isCarbonAtom(bond.getEnd()) && this.isHeteroAtom(bond.getBegin())) {
                        origHeteroAtom = bond.getBegin();
                        origCarbonAtom = bond.getEnd();
                    } else {
                        SugarDetectionUtility.LOGGER.error("Broken bond between sugar and aglycone with one carbon " +
                                "and one hetero atom found but they cannot be assigned, this should not happen!");
                        continue;
                    }
                    boolean isHeteroAtomInAglycone = copyForAglycone.contains(inputAtomToAtomCopyInAglyconeMap.get(origHeteroAtom));
                    boolean isHeteroAtomInSugars = copyForSugars.contains(inputAtomToAtomCopyInSugarsMap.get(origHeteroAtom));
                    if (!(isHeteroAtomInAglycone || isHeteroAtomInSugars)) {
                        SugarDetectionUtility.LOGGER.error("Hetero atom not found in aglycone or sugars, this should not happen!");
                        continue;
                    }
                    //copy hetero atom to the other part:
                    IAtom cpyHeteroAtom = this.deeperCopy(origHeteroAtom,
                            isHeteroAtomInSugars? copyForAglycone : copyForSugars);
                    IAtom carbonAtomInCopyToBindTo = isHeteroAtomInSugars?
                            inputAtomToAtomCopyInAglyconeMap.get(origCarbonAtom)
                            : inputAtomToAtomCopyInSugarsMap.get(origCarbonAtom);
                    IBond copyBondToHeteroAtom;
                    if (bond.getBegin().equals(origCarbonAtom)) {
                        copyBondToHeteroAtom = carbonAtomInCopyToBindTo.getBuilder().newInstance(
                                IBond.class, carbonAtomInCopyToBindTo, cpyHeteroAtom, bond.getOrder());
                    } else {
                        copyBondToHeteroAtom = carbonAtomInCopyToBindTo.getBuilder().newInstance(
                                IBond.class, cpyHeteroAtom, carbonAtomInCopyToBindTo, bond.getOrder());
                    }
                    //add the new bond to the right container and update the maps:
                    if (isHeteroAtomInSugars) {
                        copyForAglycone.addBond(copyBondToHeteroAtom);
                        inputAtomToAtomCopyInAglyconeMap.put(origHeteroAtom, cpyHeteroAtom);
                        inputBondToBondCopyInAglyconeMap.put(bond, copyBondToHeteroAtom);
                    } else {
                        copyForSugars.addBond(copyBondToHeteroAtom);
                        inputAtomToAtomCopyInSugarsMap.put(origHeteroAtom, cpyHeteroAtom);
                        inputBondToBondCopyInSugarsMap.put(bond, copyBondToHeteroAtom);
                    }
                    //saturate copied hetero atom with H or R:
                    this.saturate(
                            cpyHeteroAtom,
                            isHeteroAtomInSugars? copyForAglycone : copyForSugars,
                            markAttachPointsByR,
                            origHeteroAtom,
                            bond);
                    //copy stereo elements for the broken bond to preserve the configuration
                    this.mapBondStereoElement(
                            mol,
                            bond,
                            isHeteroAtomInSugars? copyForAglycone : copyForSugars,
                            isHeteroAtomInSugars? inputAtomToAtomCopyInAglyconeMap : inputAtomToAtomCopyInSugarsMap,
                            isHeteroAtomInSugars? inputBondToBondCopyInAglyconeMap : inputBondToBondCopyInSugarsMap);
                    //saturate the hetero atom in the part it was originally assigned to with H or R
                    IAtom heteroAtomToBeSaturated = isHeteroAtomInAglycone?
                            inputAtomToAtomCopyInAglyconeMap.get(origHeteroAtom)
                            : inputAtomToAtomCopyInSugarsMap.get(origHeteroAtom);
                    this.saturate(
                            heteroAtomToBeSaturated,
                            isHeteroAtomInAglycone? copyForAglycone : copyForSugars,
                            markAttachPointsByR,
                            origHeteroAtom,
                            bond);
                } else {
                    //broken bond was a C-C or hetero-hetero bond, just saturate both former bond atoms with R if required
                    for (IAtom atom : bond.atoms()) {
                        boolean isAtomInAglycone = copyForAglycone.contains(inputAtomToAtomCopyInAglyconeMap.get(atom));
                        IAtom copyAtomToSaturate = isAtomInAglycone?
                                inputAtomToAtomCopyInAglyconeMap.get(atom)
                                : inputAtomToAtomCopyInSugarsMap.get(atom);
                        this.saturate(
                                copyAtomToSaturate,
                                isAtomInAglycone? copyForAglycone : copyForSugars,
                                markAttachPointsByR,
                                atom,
                                bond
                        );
                    }
                }
            } //end of if condition looking for bonds broken during sugar extraction
        } // end of for loop over all bonds in the input molecule
        //just a check to be safe, there was an issue in the past:
        if (!hasIdentifiedBrokenBond && !copyForAglycone.isEmpty() && ConnectivityChecker.isConnected(mol) && !containsSpiroSugars) {
            //note for disconnected glycosides, one could process each component separately, but this seems like
            // unnecessary overhead just for the sake of this check
            SugarDetectionUtility.LOGGER.error("No broken bonds found between aglycone and sugars, no saturation performed, this should not happen!");
        }
        //postprocessing for spiro sugars, if there were any detected:
        if (this.areSpiroRingsDetectedAsCircularSugars() && containsSpiroSugars) {
            for (IAtomContainer part : new IAtomContainer[] {copyForAglycone, copyForSugars}) {
                for (IAtom atom : part.atoms()) {
                    if (atom.getProperty(SugarRemovalUtility.IS_SPIRO_ATOM_PROPERTY_KEY) != null
                            && part.getConnectedBondsCount(atom) != 4) {
                        //spiro carbon was part of sugar AND aglycone, saturation needed
                        if (markAttachPointsByR) {
                            for (int i = 0; i < 2; i++) {
                                IPseudoAtom tmpRAtom = atom.getBuilder().newInstance(IPseudoAtom.class, "R");
                                tmpRAtom.setAttachPointNum(1);
                                tmpRAtom.setImplicitHydrogenCount(0);
                                part.addAtom(tmpRAtom);
                                IBond bondToR = atom.getBuilder().newInstance(
                                        IBond.class, atom, tmpRAtom, IBond.Order.SINGLE);
                                part.addBond(bondToR);
                            }
                        } else {
                            int implHCount = atom.getImplicitHydrogenCount();
                            atom.setImplicitHydrogenCount(implHCount + 2);
                        }
                    }
                }
            }
        }
        //postprocessing of extracted sugars, if required:
        if (postProcessSugars) {
            if (extractCircularSugars) {
                this.splitOGlycosidicEsterPeroxideBondsCircularSugarsPostProcessing(copyForSugars, markAttachPointsByR, limitPostProcessingBySize);
            }
            if (extractLinearSugars) {
                this.splitEtherEsterPeroxideBondsLinearSugarsPostProcessing(copyForSugars, markAttachPointsByR, limitPostProcessingBySize);
            }
        }
        //clean up the maps:
        for (IAtom atom : mol.atoms()) {
            if (!copyForAglycone.contains(inputAtomToAtomCopyInAglyconeMap.get(atom))) {
                inputAtomToAtomCopyInAglyconeMap.remove(atom);
            }
        }
        for (IBond bond : mol.bonds()) {
            if (!copyForAglycone.contains(inputBondToBondCopyInAglyconeMap.get(bond))) {
                inputBondToBondCopyInAglyconeMap.remove(bond);
            }
        }
        for (IAtom atom : mol.atoms()) {
            if (!copyForSugars.contains(inputAtomToAtomCopyInSugarsMap.get(atom))) {
                inputAtomToAtomCopyInSugarsMap.remove(atom);
            }
        }
        for (IBond bond : mol.bonds()) {
            if (!copyForSugars.contains(inputBondToBondCopyInSugarsMap.get(bond))) {
                inputBondToBondCopyInSugarsMap.remove(bond);
            }
        }
        //return value preparations, partition disconnected sugars:
        List<IAtomContainer> resultsList = new ArrayList<>(5); //magic number, totally arbitrary
        resultsList.add(0, copyForAglycone); //aglycone is always first, even if disconnected
        if (ConnectivityChecker.isConnected(copyForSugars)) {
            resultsList.add(copyForSugars);
        } else {
            for (IAtomContainer part : ConnectivityChecker.partitionIntoMolecules(copyForSugars)) {
                if (!part.isEmpty()) {
                    resultsList.add(part);
                }
            }
        }
        return resultsList;
    }

    /**
     * Returns the indices of atoms in the input molecule that correspond to atoms in the given group.
     * <p>
     * This method iterates through all atoms in the input molecule and checks if the corresponding
     * atom (via the provided mapping) exists in the group container. The indices of matching atoms
     * are collected and returned as an array.
     * <p>
     * Note that the group may contain atoms that are not present in the input molecule mapping
     * (e.g., R-groups added during processing), which will be ignored.
     *
     * @param mol The input molecule containing the original atoms
     * @param group The group container to check for atom membership, e.g. extracted sugar or aglycone
     * @param inputAtomToAtomCopyMap Map of original atoms to their copies in the group
     * @return Array of atom indices in the input molecule that have corresponding atoms in the group.
     *         Returns empty array if no matching atoms are found or if the group is empty.
     * @throws NullPointerException if any of the parameters is null
     */
    public int[] getAtomIndicesOfGroup(
            IAtomContainer mol,
            IAtomContainer group,
            Map<IAtom, IAtom> inputAtomToAtomCopyMap
    ) {
        if (mol == null || group == null || inputAtomToAtomCopyMap == null) {
            throw new NullPointerException("Given molecule, group, or input atom to atom copy map is null.");
        }
        if (group.isEmpty()) {
            return new int[0];
        }
        //cannot immediately use array because the group may contain atoms that are not in the input molecule, e.g. R atoms
        ArrayList<Integer> groupAtomIndices = new ArrayList<>(group.getAtomCount());
        for (IAtom atom : mol.atoms()) {
            if (group.contains(inputAtomToAtomCopyMap.get(atom))) {
                groupAtomIndices.add(atom.getIndex());
            }
        }
        int[] indices = new int[groupAtomIndices.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = groupAtomIndices.get(i);
        }
        return indices;
    }

    /**
     * Returns the indices of bonds in the input molecule that correspond to bonds in the given group.
     * <p>
     * This method iterates through all bonds in the input molecule and checks if the corresponding
     * bond (via the provided mapping) exists in the group container. The indices of matching bonds
     * are collected and returned as an array.
     * <p>
     * Note that the group may contain bonds that are not present in the input molecule mapping
     * (e.g., bonds to R-groups added during processing), which will be ignored.
     *
     * @param mol The input molecule containing the original bonds
     * @param group The group container to check for bond membership, e.g. extracted sugar or aglycone
     * @param inputBondToBondCopyMap Map from original bonds to their copies in the group
     * @return Array of bond indices in the input molecule that have corresponding bonds in the group.
     *         Returns empty array if no matching bonds are found or if the group is empty.
     * @throws NullPointerException if any of the parameters is null
     */
    public int[] getBondIndicesOfGroup(
            IAtomContainer mol,
            IAtomContainer group,
            Map<IBond, IBond> inputBondToBondCopyMap
    ) {
        if (mol == null || group == null || inputBondToBondCopyMap == null) {
            throw new NullPointerException("Given molecule, group, or input bond to bond copy map is null.");
        }
        if (group.isEmpty()) {
            return new int[0];
        }
        //cannot immediately use array because the group may contain bonds that are not in the input molecule, e.g. bonds to R atoms
        ArrayList<Integer> groupBondIndices = new ArrayList<>(group.getBondCount());
        for (IBond bond : mol.bonds()) {
            if (group.contains(inputBondToBondCopyMap.get(bond))) {
                groupBondIndices.add(bond.getIndex());
            }
        }
        int[] indices = new int[groupBondIndices.size()];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = groupBondIndices.get(i);
        }
        return indices;
    }

    /**
     * Assigns group indices to all atoms in the input molecule based on their membership in the aglycone or sugar fragments.
     * <p>
     * This method iterates through the atoms of the input molecule and determines which group (aglycone or sugar fragment)
     * each atom belongs to. The group indices are assigned as follows:
     * <ul>
     *   <li>Index 0 corresponds to the aglycone.</li>
     *   <li>Indices 1 and above correspond to individual sugar fragments.</li>
     *   <li>Atoms not belonging to any group are assigned an index of -1 (should actually not happen).</li>
     * </ul>
     * <p>
     * This allows you to, for example, generate SMILES strings with glycosidic moiety annotations and
     * depictions with glycosidic moiety highlights, e.g.:
     * <pre>{@code
     * Map<IAtom, IAtom> inputAtomToAglyconeAtomMap = new HashMap<IAtom, IAtom>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f);
     * Map<IAtom, IAtom> inputAtomToSugarAtomMap = new HashMap<IAtom, IAtom>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f);
     * List<IAtomContainer> aglyconeAndSugarsList = sdu.copyAndExtractAglyconeAndSugars(
     *         molecule,
     *         true,
     *         false,
     *         false,
     *         true,
     *         true,
     *         inputAtomToAglyconeAtomMap,
     *         new HashMap<IBond, IBond>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f),
     *         inputAtomToSugarAtomMap,
     *         new HashMap<IBond, IBond>((int) ((mol.getAtomCount() / 0.75f) + 2), 0.75f));
     * int[] groupIndices = sdu.getGroupIndicesForAllAtoms(mol, aglyconeAndSugarsList, inputAtomToAglyconeAtomMap, inputAtomToSugarAtomMap);
     * for (IAtom atom : mol.atoms()) {
     *    atom.setMapIdx(groupIndices[atom.getIndex()] + 1);
     * }
     * String smi = new SmilesGenerator(SmiFlavor.Isomeric | SmiFlavor.AtomAtomMap).create(mol);
     * //example output (for Fusacandin B (CNP0295326.4)):
     * // [CH3:1][CH2:1][CH2:1][CH2:1][CH2:1]/[CH:1]=[CH:1]/[CH:1]=[CH:1]/[C@@H:1]([OH:1])[CH2:1]/[CH:1]=[CH:1]/[CH:1]=[CH:1]/[C:1](=[O:1])[O:1][CH:1]1[CH:1]([OH:1])[C@H:1]([C:1]2=[C:1]([OH:1])[CH:1]=[C:1]([OH:1])[CH:1]=[C:1]2[CH2:1][OH:1])[O:1][C@H:1]([CH2:1][OH:1])[C@H:1]1[O:2][C@@H:2]3[O:2][CH:2]([CH2:2][OH:2])[C@H:2]([OH:2])[C@H:2]([OH:2])[CH:2]3[O:3][C@@H:3]4[O:3][CH:3]([CH2:3][OH:3])[C@H:3]([OH:3])[C@H:3]([OH:3])[CH:3]4[OH:3]
     * }</pre>
     * (Check out the "Color Map" option on the CDK depict web app).
     * <p>The method uses the provided atom-to-atom copy maps to identify the correspondence between atoms in the input molecule
     * and the aglycone/sugar fragments.</p>
     * <p>Note that connecting hetero atoms between the aglycone and a sugar moiety are duplicated in the extraction process
     * but will be assigned to only one of the two structures here.</p>
     *
     * @param mol The input molecule containing all atoms to be indexed. Must not be null or empty.
     * @param aglyconeAndSugars A list of atom containers representing the aglycone (index 0) and sugar fragments (indices 1+).
     *                          Must not be null or empty.
     * @param inputAtomToAtomCopyInAglyconeMap A map linking atoms in the input molecule to their corresponding atoms in the aglycone.
     *                                         Must not be null.
     * @param inputAtomToAtomCopyInSugarsMap A map linking atoms in the input molecule to their corresponding atoms in the sugar fragments.
     *                                       Must not be null.
     * @return An array of integers where each index corresponds to an atom in the input molecule, and the value at that index
     *         represents the group index (0 for aglycone, 1+ for sugar fragments, -1 for unassigned atoms).
     *         Returns an empty array if the input molecule or aglyconeAndSugars list is empty, or if no groups are identified.
     * @throws NullPointerException If any of the input parameters is null.
     */
    public int[] getGroupIndicesForAllAtoms(
            IAtomContainer mol,
            List<IAtomContainer> aglyconeAndSugars,
            Map<IAtom, IAtom> inputAtomToAtomCopyInAglyconeMap,
            Map<IAtom, IAtom> inputAtomToAtomCopyInSugarsMap
    ) {
        if (mol == null || aglyconeAndSugars == null || inputAtomToAtomCopyInAglyconeMap == null || inputAtomToAtomCopyInSugarsMap == null) {
            throw new NullPointerException("Given molecule, extracted structures, or maps are null.");
        }
        if (mol.isEmpty() || aglyconeAndSugars.isEmpty() || (aglyconeAndSugars.size() == 1)) {
            return new int[0];
        }
        int[] groupIndices = new int[mol.getAtomCount()];
        Arrays.fill(groupIndices, -1);
        for (int i = 0; i < aglyconeAndSugars.size(); i++) {
            IAtomContainer group = aglyconeAndSugars.get(i);
            if (group.isEmpty()) {
                continue; //skip empty groups, e.g. empty aglycone
            }
            int[] atomIndices = this.getAtomIndicesOfGroup(mol, group, i == 0 ? inputAtomToAtomCopyInAglyconeMap : inputAtomToAtomCopyInSugarsMap);
            for (int atomIndex : atomIndices) {
                groupIndices[atomIndex] = i;
            }
        }
        return groupIndices;
    }

    /**
     * Creates a relatively deep ("deeper" than cloning but not as extensive) copy of the given
     * atom container mol and fills the given maps
     * with a mapping of the original atoms and bonds to the atoms and bonds in the copy.
     * Copies:
     * <br>- Atoms (atomic number, implicit hydrogen count, aromaticity flag, valency, atom type name, formal charge, some primitive-based properties)
     * <br>- Bonds (begin and end atom, order, aromaticity flag, stereo, display, in ring flag, some primitive-based properties)
     * <br>- Single electrons
     * <br>- Lone pairs
     * <br>- Stereo elements (mapped to the copied atoms and bonds)
     * <br>- Some primitive-based properties (String, Integer, Boolean)
     * <br>Note: atom types of the original atoms are not copied and hence, some properties will be unset in the copies.
     * If you need atom types and their defining properties, you need to re-perceive them after copying.
     *
     * @param mol the molecule to copy
     * @param origToCopyAtomMap empty map to fill with a mapping of the original atoms to the copied atoms
     * @param origToCopyBondMap empty map to fill with a mapping of the original bonds to the copied bonds
     * @return a relatively deep copy of the given atom container
     */
    protected IAtomContainer deeperCopy(
            IAtomContainer mol,
            Map<IAtom, IAtom> origToCopyAtomMap,
            Map<IBond, IBond> origToCopyBondMap
    ) {
        IAtomContainer copy = mol.getBuilder().newAtomContainer();
        // atoms
        for (IAtom atom : mol.atoms()) {
            IAtom cpyAtom = this.deeperCopy(atom, copy);
            origToCopyAtomMap.put(atom, cpyAtom);
        }
        // bonds
        for (IBond bond : mol.bonds()) {
            IAtom beg = origToCopyAtomMap.get(bond.getBegin());
            IAtom end = origToCopyAtomMap.get(bond.getEnd());
            if (beg == null || end == null || beg.getContainer() != end.getContainer()) {
                continue;
            }
            IBond newBond = this.deeperCopy(bond, beg, end);
            copy.addBond(newBond);
            origToCopyBondMap.put(bond, newBond);
        }
        // single electrons
        for (ISingleElectron se : mol.singleElectrons()) {
            IAtom atom = origToCopyAtomMap.get(se.getAtom());
            if (!Objects.isNull(atom)) {
                atom.getContainer().addSingleElectron(atom.getIndex());
            }
        }
        // lone pairs
        for (ILonePair lp : mol.lonePairs()) {
            IAtom atom = origToCopyAtomMap.get(lp.getAtom());
            if (!Objects.isNull(atom)) {
                atom.getContainer().addLonePair(atom.getIndex());
            }
        }
        // stereo elements
        for (IStereoElement elem : mol.stereoElements()) {
            copy.addStereoElement(elem.map(origToCopyAtomMap, origToCopyBondMap));
        }
        // properties
        for (Map.Entry<Object, Object> entry : mol.getProperties().entrySet()) {
            if ((entry.getKey() instanceof String || entry.getKey() instanceof Integer || entry.getKey() instanceof Boolean)
                    && (entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Boolean || entry.getValue() == null)) {
                copy.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return copy;
    }

    /**
     *  Creates a relatively deep ("deeper" than cloning but not as extensive) copy of the given atom and adds it to the given container.
     *  Copies:
     *  <br>- atomic number
     *  <br>- implicit hydrogen count
     *  <br>- aromaticity flag
     *  <br>- valency
     *  <br>- atom type name
     *  <br>- formal charge
     *  <br>- point 2D and 3D coordinates
     *  <br>- flags
     *  <br>- some primitive-based properties (String, Integer, Boolean)
     * <br>Note: atom types and isotopes of the original atoms are not copied and hence, some properties will be unset in the copies.
     * If you need atom types and their defining properties, you need to re-perceive them after copying.
     *
     * @param atom the atom to copy
     * @param container the container to add the copied atom to
     * @return the copied atom
     */
    protected IAtom deeperCopy(IAtom atom, IAtomContainer container) {
        IAtom cpyAtom = container.newAtom(atom.getAtomicNumber(),
                atom.getImplicitHydrogenCount());
        cpyAtom.setIsAromatic(atom.isAromatic());
        cpyAtom.setValency(atom.getValency());
        cpyAtom.setAtomTypeName(atom.getAtomTypeName());
        //setting the formal charge also sets the (partial) charge, see https://github.com/cdk/cdk/pull/1151
        cpyAtom.setFormalCharge(atom.getFormalCharge());
        if (atom.getPoint2d() != null) {
            cpyAtom.setPoint2d(new Point2d(atom.getPoint2d().x, atom.getPoint2d().y));
        }
        if (atom.getPoint3d() != null) {
            cpyAtom.setPoint3d(new Point3d(atom.getPoint3d().x, atom.getPoint3d().y, atom.getPoint3d().z));
        }
        cpyAtom.setFlags(atom.getFlags());
        //fractional point 3D (location in a crystal unit cell) is deliberately not copied; add if needed
        //fields related to atom type (max bond order, bond order sum, covalent radius, hybridization, formal neighbor count) are deliberately not copied; add if needed
        //fields related to isotope (exact mass, natural abundance, mass number) are deliberately not copied; add if needed
        //properties:
        for (Map.Entry<Object, Object> entry : atom.getProperties().entrySet()) {
            if ((entry.getKey() instanceof String || entry.getKey() instanceof Integer || entry.getKey() instanceof Boolean)
                    && (entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Boolean || entry.getValue() == null)) {
                cpyAtom.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return cpyAtom;
    }

    /**
     * Creates a relatively deep ("deeper" than cloning but not as extensive) copy of the given bond between the given begin and end atoms.
     * Copies:
     * <br>- order
     * <br>- aromaticity flag
     * <br>- stereo
     * <br>- display
     * <br>- in ring flag
     * <br>- flags
     * <br>- electron count
     * <br>- some primitive-based properties (String, Integer, Boolean)
     * <br>Note: The begin and end atoms are not copied, but the given ones are used in the copy.
     * <br>Note also: the created bond must be added to the copy atom container by the calling code!
     *
     * @param bond the bond to copy
     * @param begin the begin atom of the bond in the copy(!)
     * @param end the end atom of the bond in the copy(!)
     * @return the copied bond
     */
    protected IBond deeperCopy(IBond bond, IAtom begin, IAtom end) {
        //using begin.getContainer().newBond() here caused weird issues sometimes
        IBond newBond = new Bond(begin, end, bond.getOrder());
        newBond.setIsAromatic(bond.isAromatic());
        newBond.setStereo(bond.getStereo());
        newBond.setDisplay(bond.getDisplay());
        newBond.setIsInRing(bond.isInRing());
        newBond.setFlags(bond.getFlags());
        newBond.setElectronCount(bond.getElectronCount());
        //properties:
        for (Map.Entry<Object, Object> entry : bond.getProperties().entrySet()) {
            if ((entry.getKey() instanceof String || entry.getKey() instanceof Integer || entry.getKey() instanceof Boolean)
                    && (entry.getValue() instanceof String || entry.getValue() instanceof Integer || entry.getValue() instanceof Boolean || entry.getValue() == null)) {
                newBond.setProperty(entry.getKey(), entry.getValue());
            }
        }
        return newBond;
    }

    /**
     * Checks whether the given atom is a hetero-atom (i.e. non-carbon and
     * non-hydrogen). Pseudo (R) atoms will also return false.
     *
     * @param atom the atom to test
     * @return true if the given atom is neither a carbon nor a hydrogen or
     *         pseudo atom
     */
    protected boolean isHeteroAtom(IAtom atom) {
        Integer tmpAtomicNr = atom.getAtomicNumber();
        if (Objects.isNull(tmpAtomicNr)) {
            return false;
        }
        int tmpAtomicNumberInt = tmpAtomicNr;
        return tmpAtomicNumberInt != IElement.H && tmpAtomicNumberInt != IElement.C
                && !this.isPseudoAtom(atom);
    }

    /**
     * Checks whether the given atom is a pseudo atom. Very strict, any atom
     * whose atomic number is null or 0, whose symbol equals "R" or "*", or that
     * is an instance of an IPseudoAtom implementing class will be classified as
     * a pseudo atom.
     *
     * @param atom the atom to test
     * @return true if the given atom is identified as a pseudo (R) atom
     */
    protected boolean isPseudoAtom(IAtom atom) {
        Integer tmpAtomicNr = atom.getAtomicNumber();
        if (Objects.isNull(tmpAtomicNr)) {
            return true;
        }
        String tmpSymbol = atom.getSymbol();
        return tmpAtomicNr == IElement.Wildcard ||
                tmpSymbol.equals("R") ||
                tmpSymbol.equals("*") ||
                atom instanceof IPseudoAtom;
    }

    /**
     * Checks whether the given atom is a carbon atom.
     *
     * @param atom the atom to test
     * @return true if the given atom is a carbon atom
     */
    protected boolean isCarbonAtom(IAtom atom) {
        Integer tmpAtomicNr = atom.getAtomicNumber();
        if (Objects.isNull(tmpAtomicNr)) {
            return false;
        }
        int tmpAtomicNumberInt = tmpAtomicNr;
        return tmpAtomicNumberInt == IElement.C;
    }

    /**
     * Preprocesses the results of sugar removal to correct special cases where the separation was not chemically optimal.
     * <p>
     * The preprocessing handles the following cases:
     * <ul>
     *   <li><strong>C6 carbon separation:</strong> Corrects cases where C6 carbon atoms of sugar moieties
     *       were incorrectly assigned to the aglycone instead of remaining with the sugar fragment.</li>
     *   <li><strong>Carboxy group separation:</strong> Moves entire carboxy groups (sugar-C(=O)-O-aglycone)
     *   from the aglycone to the sugar fragment.</li>
     * </ul>
     * <p>
     * The method identifies problematic cases by examining bonds that were broken during sugar removal (bonds between
     * carbon atoms that are no longer present in either the aglycone or sugar copies). It then applies specific
     * correction methods in this class.
     * No checks are performed!
     *
     * @param mol The original input molecule containing all atoms and bonds
     * @param copyForAglycone The aglycone fragment that may need correction
     * @param copyForSugars The sugar fragment that may receive corrected atoms/bonds
     * @param inputAtomToAtomCopyInAglyconeMap Map from original atoms to their copies in the aglycone fragment
     * @param inputBondToBondCopyInAglyconeMap Map from original bonds to their copies in the aglycone fragment
     * @param inputAtomToAtomCopyInSugarsMap Map from original atoms to their copies in the sugar fragment
     * @param inputBondToBondCopyInSugarsMap Map from original bonds to their copies in the sugar fragment
     */
    protected void preprocessSugarRemovalResults(
            IAtomContainer mol,
            IAtomContainer copyForAglycone,
            IAtomContainer copyForSugars,
            Map<IAtom, IAtom> inputAtomToAtomCopyInAglyconeMap,
            Map<IBond, IBond> inputBondToBondCopyInAglyconeMap,
            Map<IAtom, IAtom> inputAtomToAtomCopyInSugarsMap,
            Map<IBond, IBond> inputBondToBondCopyInSugarsMap
    ) {
        for (IBond bond : mol.bonds()) {
            //bond not in aglycone or sugars, so it was broken during sugar removal, and both bond atoms are carbons
            if (!copyForAglycone.contains(inputBondToBondCopyInAglyconeMap.get(bond))
                    && !copyForSugars.contains(inputBondToBondCopyInSugarsMap.get(bond))
                    && this.isCarbonAtom(bond.getBegin())
                    && this.isCarbonAtom(bond.getEnd())) {
                //detect cases where the C6 carbon was separated from the sugar and cases where the sugar should carry a carboxy group
                boolean isBeginInAglycone = copyForAglycone.contains(inputAtomToAtomCopyInAglyconeMap.get(bond.getBegin()));
                IAtom aglyconeCarbon = isBeginInAglycone?
                        inputAtomToAtomCopyInAglyconeMap.get(bond.getBegin()) : inputAtomToAtomCopyInAglyconeMap.get(bond.getEnd());
                IAtom aglyconeCarbonOriginalAtom = isBeginInAglycone? bond.getBegin() : bond.getEnd();
                if (copyForAglycone.getConnectedBondsCount(aglyconeCarbon) == 1) {
                    //section that corrects C6 separation if the only neighbor is an oxygen atom
                    if (copyForAglycone.getConnectedAtomsList(aglyconeCarbon).get(0).getAtomicNumber() == IElement.O) {
                        this.correctC6Separation(
                                mol,
                                copyForAglycone,
                                copyForSugars,
                                inputAtomToAtomCopyInSugarsMap,
                                inputBondToBondCopyInSugarsMap,
                                aglyconeCarbon,
                                aglyconeCarbonOriginalAtom,
                                bond);
                    }
                } else if (copyForAglycone.getConnectedBondsCount(aglyconeCarbon) == 2) {
                    //section that corrects carboxy groups being split from the sugar
                    boolean areBothNeighborsOxygen = true;
                    IAtom ketoOxygenInAglycone = null;
                    IAtom etherOxygenInAglycone = null;
                    //assign the oxygen atoms of the carboxy group:
                    for (IAtom nbrAtom : copyForAglycone.getConnectedAtomsList(aglyconeCarbon)) {
                        if (nbrAtom.getAtomicNumber() != IElement.O) {
                            areBothNeighborsOxygen = false;
                            break;
                        }
                        if (copyForAglycone.getBond(aglyconeCarbon, nbrAtom).getOrder() == IBond.Order.DOUBLE && ketoOxygenInAglycone == null) {
                            ketoOxygenInAglycone = nbrAtom;
                        } else if (copyForAglycone.getBond(aglyconeCarbon, nbrAtom).getOrder() == IBond.Order.SINGLE && etherOxygenInAglycone == null) {
                            etherOxygenInAglycone = nbrAtom;
                        } else {
                            areBothNeighborsOxygen = false;
                            break;
                        }
                    }
                    if (areBothNeighborsOxygen && ketoOxygenInAglycone != null && etherOxygenInAglycone != null) {
                        //carboxy group, so copy C, keto O, and ether O to sugars and remove everything except ether O from aglycone
                        this.correctCarboxyGroupSeparation(
                                mol,
                                copyForAglycone,
                                copyForSugars,
                                inputAtomToAtomCopyInAglyconeMap,
                                inputBondToBondCopyInAglyconeMap,
                                inputAtomToAtomCopyInSugarsMap,
                                inputBondToBondCopyInSugarsMap,
                                aglyconeCarbon,
                                aglyconeCarbonOriginalAtom,
                                ketoOxygenInAglycone,
                                etherOxygenInAglycone,
                                bond);
                    }
                } //end of carboxy group correction
            } //end of if that detects broken C-C bond
        } //end of bond iteration
    }

    /**
     * Corrects the separation of C6 carbon atoms that were incorrectly placed in the aglycone during sugar extraction.
     * <p>
     * This method addresses cases where a C6 carbon atom of a sugar moiety was incorrectly
     * assigned to the aglycone fragment instead of remaining with the sugar during extraction. This can
     * happen when the sugar removal utility breaks bonds between sugar moieties and their substituents.
     * <p>
     * The correction process:
     * <ul>
     *   <li>Removes the misplaced carbon atom from the aglycone fragment</li>
     *   <li>Creates a copy of the original carbon atom and adds it to the sugar fragment</li>
     *   <li>Recreates the bond between the carbon and its connected sugar atom</li>
     *   <li>Updates the atom and bond mapping tables to reflect the changes</li>
     *   <li>Preserves stereochemistry information for the moved bond</li>
     * </ul>
     * <p>
     * This method is typically called during preprocessing when a carbon atom in the aglycone has only one
     * connection, indicating it is likely a terminal carbon that should belong to a sugar moiety.
     * No checks are performed!
     *
     * @param mol The original input molecule containing all atoms and bonds
     * @param copyForAglycone The aglycone fragment from which the carbon atom will be removed
     * @param copyForSugars The sugar fragment to which the carbon atom will be added
     * @param inputAtomToAtomCopyInSugarsMap Map from original atoms to their copies in the sugar fragment
     * @param inputBondToBondCopyInSugarsMap Map from original bonds to their copies in the sugar fragment
     * @param aglyconeCarbon The carbon atom in the aglycone fragment to be moved
     * @param aglyconeCarbonOriginalAtom The original carbon atom from the input molecule corresponding to the atom to be moved
     * @param bond The bond between the carbon and the sugar fragment that needs to be recreated
     */
    protected void correctC6Separation(
            IAtomContainer mol,
            IAtomContainer copyForAglycone,
            IAtomContainer copyForSugars,
            Map<IAtom, IAtom> inputAtomToAtomCopyInSugarsMap,
            Map<IBond, IBond> inputBondToBondCopyInSugarsMap,
            IAtom aglyconeCarbon,
            IAtom aglyconeCarbonOriginalAtom,
            IBond bond
    ) {
        //remove the carbon from the aglycone and add it to the sugars; also create a new bond in the sugars
        copyForAglycone.removeAtom(aglyconeCarbon);
        IAtom newAtomCopyInSugars = this.deeperCopy(aglyconeCarbonOriginalAtom, copyForSugars);
        IBond newBondInSugars = this.deeperCopy(bond, newAtomCopyInSugars, inputAtomToAtomCopyInSugarsMap.get(bond.getOther(aglyconeCarbonOriginalAtom)));
        copyForSugars.addBond(newBondInSugars);
        //update the maps
        inputAtomToAtomCopyInSugarsMap.put(aglyconeCarbonOriginalAtom, newAtomCopyInSugars);
        inputBondToBondCopyInSugarsMap.put(bond, newBondInSugars);
        this.mapBondStereoElement(
                mol,
                bond,
                copyForSugars,
                inputAtomToAtomCopyInSugarsMap,
                inputBondToBondCopyInSugarsMap);
    }

    /**
     * Corrects the separation of carboxy groups that were incorrectly split between aglycone and sugar fragments during extraction.
     * <p>
     * This method addresses cases where a carboxy group (sugar-C(=O)-O-aglycone) was incorrectly separated during sugar removal,
     * with the formic acid ester remaining in the aglycone.
     * To maintain chemical validity and keep functional groups intact, this method moves the entire carboxy group
     * to the sugar fragment.
     * <p>
     * The correction process:
     * <ul>
     *   <li>Removes the carbonyl carbon and keto oxygen from the aglycone fragment</li>
     *   <li>Adds copies of both atoms to the sugar fragment</li>
     *   <li>Recreates the bonds within the carboxy group and to the connecting sugar atom</li>
     *   <li>Updates the atom and bond mapping tables to reflect the changes</li>
     *   <li>Preserves stereochemistry information for the moved bonds</li>
     * </ul>
     * <p>
     * This method is typically called during preprocessing to correct sugar removal results in special cases
     * where functional groups are inadvertently split across fragments.
     * No checks are performed!
     *
     * @param mol The original input molecule containing all atoms and bonds
     * @param copyForAglycone The aglycone fragment from which the carboxy atoms will be removed
     * @param copyForSugars The sugar fragment to which the carboxy atoms will be added
     * @param inputAtomToAtomCopyInAglyconeMap Map from original atoms to their copies in the aglycone fragment
     * @param inputBondToBondCopyInAglyconeMap Map from original bonds to their copies in the aglycone fragment
     * @param inputAtomToAtomCopyInSugarsMap Map from original atoms to their copies in the sugar fragment
     * @param inputBondToBondCopyInSugarsMap Map from original bonds to their copies in the sugar fragment
     * @param aglyconeCarbon The carbonyl carbon atom in the aglycone fragment to be moved
     * @param aglyconeCarbonOriginalAtom The original carbonyl carbon atom from the input molecule
     * @param ketoOxygenInAglycone The keto oxygen atom in the aglycone fragment to be moved
     * @param etherOxygenInAglycone The ether oxygen atom in the aglycone fragment
     * @param bond The bond between the carbonyl carbon and the sugar fragment that needs to be recreated
     */
    protected void correctCarboxyGroupSeparation(
            IAtomContainer mol,
            IAtomContainer copyForAglycone,
            IAtomContainer copyForSugars,
            Map<IAtom, IAtom> inputAtomToAtomCopyInAglyconeMap,
            Map<IBond, IBond> inputBondToBondCopyInAglyconeMap,
            Map<IAtom, IAtom> inputAtomToAtomCopyInSugarsMap,
            Map<IBond, IBond> inputBondToBondCopyInSugarsMap,
            IAtom aglyconeCarbon,
            IAtom aglyconeCarbonOriginalAtom,
            IAtom ketoOxygenInAglycone,
            IAtom etherOxygenInAglycone,
            IBond bond
    ) {
        //determine keto and ether oxygen atoms:
        IAtom ketoOxygenOriginalAtom = null;
        IAtom etherOxygenOriginalAtom = null;
        for (Map.Entry<IAtom, IAtom> entry : inputAtomToAtomCopyInAglyconeMap.entrySet()) {
            IAtom originalAtom = entry.getKey();
            IAtom mappedAglyconeAtom = entry.getValue();
            if (mappedAglyconeAtom.equals(ketoOxygenInAglycone)) {
                ketoOxygenOriginalAtom = originalAtom;
            }
            if (mappedAglyconeAtom.equals(etherOxygenInAglycone)) {
                etherOxygenOriginalAtom = originalAtom;
            }
        }
        if (ketoOxygenOriginalAtom == null || etherOxygenOriginalAtom == null) {
            SugarDetectionUtility.LOGGER.error("Could not find original atoms for carboxy group, this should not happen!");
            return;
        }
        //remove C and keto O from aglycone, add both to sugars, also add bond between them and bond between C and other sugar atom:
        //copy carboxy C to sugar
        IAtom newAtomCopyInSugars = this.deeperCopy(aglyconeCarbonOriginalAtom, copyForSugars);
        IBond newBondInSugars = this.deeperCopy(bond, newAtomCopyInSugars, inputAtomToAtomCopyInSugarsMap.get(bond.getOther(aglyconeCarbonOriginalAtom)));
        copyForSugars.addBond(newBondInSugars);
        inputAtomToAtomCopyInSugarsMap.put(aglyconeCarbonOriginalAtom, newAtomCopyInSugars);
        inputBondToBondCopyInSugarsMap.put(bond, newBondInSugars);
        //copy keto O to sugar
        IAtom newKetoOCopyInSugars = this.deeperCopy(ketoOxygenOriginalAtom, copyForSugars);
        IBond originalBondToKetoO = mol.getBond(aglyconeCarbonOriginalAtom, ketoOxygenOriginalAtom);
        IBond newKetoOBondInSugars = this.deeperCopy(originalBondToKetoO, newAtomCopyInSugars, newKetoOCopyInSugars);
        copyForSugars.addBond(newKetoOBondInSugars);
        inputAtomToAtomCopyInSugarsMap.put(ketoOxygenOriginalAtom, newKetoOCopyInSugars);
        inputBondToBondCopyInSugarsMap.put(originalBondToKetoO, newKetoOBondInSugars);
        //remove atoms and bonds from aglycone
        copyForAglycone.removeAtom(ketoOxygenInAglycone);
        copyForAglycone.removeBond(inputBondToBondCopyInAglyconeMap.get(originalBondToKetoO));
        copyForAglycone.removeAtom(aglyconeCarbon);
        IBond originalBondToEtherO = mol.getBond(aglyconeCarbonOriginalAtom, etherOxygenOriginalAtom);
        copyForAglycone.removeBond(inputBondToBondCopyInAglyconeMap.get(originalBondToEtherO));
        this.mapBondStereoElement(
                mol,
                bond,
                copyForSugars,
                inputAtomToAtomCopyInSugarsMap,
                inputBondToBondCopyInSugarsMap);
    }

    /**
     * Maps a stereo element from the original molecule (mol) to the receiving container (a copy of the former) if the
     * specified bond is involved in it and all its components (focus and carriers) are part of the receiving container.
     * <p>
     * This method searches for stereo elements in the original molecule that involve both atoms of the specified bond
     * and maps them to the corresponding atoms and bonds in the receiving container. Only stereo elements where
     * the focus atom and all carrier atoms/bonds are present in the receiving container will be mapped.
     * <p>
     * This is typically used during the sugar extraction process when atoms and bonds are moved between the aglycone
     * and sugar fragments to ensure that stereochemistry information is preserved in the correct fragment.
     * <p>
     * The method performs the following checks before mapping a stereo element:
     * <ul>
     *   <li>The stereo element must contain both atoms of the specified bond</li>
     *   <li>The focus atom of the stereo element must be present in the receiving container</li>
     *   <li>All carrier atoms and bonds must be present in the receiving container</li>
     * </ul>
     * <p>
     * If all conditions are met, the stereo element is mapped using the provided atom and bond maps and added
     * to the receiving container.
     * No checks are performed!
     *
     * @param mol The original molecule containing the stereo elements to be mapped
     * @param bond The bond whose associated stereo elements should be mapped
     * @param receivingContainer The container that should receive the mapped stereo elements
     * @param inputAtomToAtomCopyMap Map from original atoms to their copies in the receiving container
     * @param inputBondToBondCopyMap Map from original bonds to their copies in the receiving container
     */
    protected void mapBondStereoElement(
            IAtomContainer mol,
            IBond bond,
            IAtomContainer receivingContainer,
            Map<IAtom, IAtom>  inputAtomToAtomCopyMap,
            Map<IBond, IBond> inputBondToBondCopyMap
    ) {
        for (IStereoElement elem : mol.stereoElements()) {
            if (elem.contains(bond.getBegin()) && elem.contains(bond.getEnd())
                    && receivingContainer.contains(inputAtomToAtomCopyMap.get(elem.getFocus()))) {
                boolean areAllCarriersPresent = true;
                for (Object object : elem.getCarriers()) {
                    if (object instanceof IAtom) {
                        if (!receivingContainer.contains(inputAtomToAtomCopyMap.get(object))) {
                            areAllCarriersPresent = false;
                            break;
                        }
                    } else if (object instanceof IBond) {
                        if (!receivingContainer.contains(inputBondToBondCopyMap.get(object))) {
                            areAllCarriersPresent = false;
                            break;
                        }
                    } else {
                        areAllCarriersPresent = false;
                        break;
                    }
                }
                if (areAllCarriersPresent) {
                    receivingContainer.addStereoElement(elem.map(inputAtomToAtomCopyMap, inputBondToBondCopyMap));
                }
            }
        }
    }

    /**
     * Saturates a broken bond at an attachment point by either adding an R-group or increasing the implicit hydrogen count.
     * <p>
     * This method is used during the sugar extraction process to handle attachment points where bonds between
     * the aglycone and sugar moieties have been broken.
     * No checks are performed!
     *
     * @param copyAtomToSaturate The atom in the copy container that needs to be saturated due to a broken bond
     * @param copyContainer The atom container containing the atom to be saturated
     * @param markAttachPointsByR If true, an R-group pseudo atom is added to mark the attachment point;
     *                           if false, the implicit hydrogen count is increased.
     * @param originalAtom The original atom from the input molecule corresponding to the atom to be saturated.
     *                     Used for determining bond properties
     * @param originalBond The original bond that was broken during the extraction process.
     *                     Used for determining the bond order of the new R-group bond
     */
    protected void saturate(
            IAtom copyAtomToSaturate,
            IAtomContainer copyContainer,
            boolean markAttachPointsByR,
            IAtom originalAtom,
            IBond originalBond
    ) {
        if (markAttachPointsByR) {
            IPseudoAtom tmpRAtom = originalAtom.getBuilder().newInstance(IPseudoAtom.class, "R");
            tmpRAtom.setAttachPointNum(1);
            tmpRAtom.setImplicitHydrogenCount(0);
            IBond bondToR;
            if (originalBond.getBegin().equals(originalAtom)) {
                bondToR = originalAtom.getBuilder().newInstance(
                        IBond.class, copyAtomToSaturate, tmpRAtom, originalBond.getOrder());
            } else {
                bondToR = originalAtom.getBuilder().newInstance(
                        IBond.class, tmpRAtom, copyAtomToSaturate, originalBond.getOrder());
            }
            copyContainer.addAtom(tmpRAtom);
            copyContainer.addBond(bondToR);
        } else {
            copyAtomToSaturate.setImplicitHydrogenCount(
                    copyAtomToSaturate.getImplicitHydrogenCount()
                    + originalBond.getOrder().numeric());
        }
    }

    /**
     * Splits O-glycosidic (ether), ester, and peroxide bonds in the given molecule (circular sugar moieties) and optionally marks the
     * attachment points with R-groups.
     * This method identifies specific bond types (ether, ester, and peroxide) in the molecule using SMARTS patterns and
     * then breaks these bonds, duplicating oxygen atoms where adequate. The transformation can either mark the attachment points with R-groups or
     * saturate the resulting open valences with implicit H atoms, depending on the `markAttachPointsByR` parameter.
     * If bonds are split, an unconnected atom container results. If no matching bonds are found, the original molecule
     * remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which ether, ester, and peroxide bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated with implicit H.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set preservation mode threshold
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitOGlycosidicEsterPeroxideBondsCircularSugarsPostProcessing(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        this.splitEsters(molecule, markAttachPointsByR, limitPostProcessingBySize, true);
        this.splitPeroxides(molecule, markAttachPointsByR, limitPostProcessingBySize, true);
        this.splitOGlycosidicBondsAndEthers(molecule, markAttachPointsByR, limitPostProcessingBySize, true);
    }

    /**
     * Splits ether, ester, and peroxide bonds in the given molecule (linear sugar moieties) and optionally marks the
     * attachment points with R-groups.
     * This method identifies specific bond types (ether, ester, and peroxide) in the molecule using SMARTS patterns and
     * then breaks these bonds, duplicating oxygen atoms where adequate. The transformation can either mark the attachment points with R-groups or
     * saturate the resulting open valences with implicit H atoms, depending on the `markAttachPointsByR` parameter.
     * If bonds are split, an unconnected atom container results. If no matching bonds are found, the original molecule
     * remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which ether, ester, and peroxide bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated with implicit H.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set minimum size for linear sugars
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitEtherEsterPeroxideBondsLinearSugarsPostProcessing(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        //note: the order is important here, since the ether pattern is very promiscuous and matches esters and cross-linking ethers as well
        this.splitEsters(molecule, markAttachPointsByR, limitPostProcessingBySize, false);
        this.splitEthersCrossLinking(molecule, markAttachPointsByR, limitPostProcessingBySize);
        this.splitOGlycosidicBondsAndEthers(molecule, markAttachPointsByR, limitPostProcessingBySize, false);
        this.splitPeroxides(molecule, markAttachPointsByR, limitPostProcessingBySize, false);
    }

    /**
     * Splits ester bonds in the given molecule (circular or linear sugar moieties) and optionally marks the attachment
     * points with R-groups.
     * <p>
     * This method identifies ester bonds in the molecule using a SMARTS pattern and then breaks these bonds while
     * duplicating the formerly connecting oxygen atom to produce a carboxy acid and an alcohol as educts.
     * The transformation can either mark the attachment points with R-groups or saturate the resulting open
     * valences with implicit hydrogen atoms, depending on the `markAttachPointsByR` parameter.
     * <p>
     * Depending on the `splitCircularSugars` parameter, the method uses either the SMARTS pattern for circular sugar
     * ester bonds or the pattern for linear sugar ester bonds (see the public constants).
     * <p>
     * If bonds are split, the molecule may become disconnected. If no ester bonds are found, the original
     * molecule remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which ester bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated
     *                            with implicit hydrogen atoms.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set preservation mode threshold or the set
     *                                  minimum size for linear sugars (depends on 'splitCircularSugars').
     * @param splitCircularSugars If true, the SMARTS pattern for circular sugar ester bonds is used;
     *                            if false, the pattern for linear sugar ester bonds is used.
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitEsters(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize,
            boolean splitCircularSugars
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        Mappings esterMappings = SmartsPattern.create(
                splitCircularSugars? SugarDetectionUtility.ESTER_BOND_CIRCULAR_SUGARS_SMARTS : SugarDetectionUtility.ESTER_BOND_LINEAR_SUGARS_SMARTS)
                .matchAll(molecule).uniqueAtoms();
        if (esterMappings.atLeast(1)) {
            for (IAtomContainer esterGroup : esterMappings.toSubstructures()) {
                IAtom carbonOne = null;
                IAtom connectingOxygen = null;
                for (IAtom atom : esterGroup.atoms()) {
                    if (atom.getAtomicNumber() == IElement.O) {
                        connectingOxygen = atom;
                    } else if (carbonOne == null ) {
                        carbonOne = atom;
                    }
                }
                IBond bondToBreak = molecule.getBond(carbonOne, connectingOxygen);
                if (limitPostProcessingBySize && this.isFragmentTooSmall(molecule, bondToBreak, splitCircularSugars)) {
                    continue;
                }
                IAtom newOxygen = molecule.newAtom(IElement.O);
                molecule.newBond(carbonOne, newOxygen, IBond.Order.SINGLE);
                IStereoElement updatedStereoElement = null;
                for (IStereoElement stereoElement : molecule.stereoElements()) {
                    if (stereoElement.getFocus().equals(carbonOne) && stereoElement.contains(connectingOxygen)) {
                        updatedStereoElement = stereoElement.updateCarriers(connectingOxygen, newOxygen);
                        break;
                    }
                }
                if (updatedStereoElement != null) {
                    molecule.addStereoElement(updatedStereoElement);
                }
                molecule.removeBond(bondToBreak);
                IAtom[] oxygens = new IAtom[] {connectingOxygen, newOxygen};
                for (IAtom oxygen : oxygens) {
                    this.saturate(
                            oxygen,
                            molecule,
                            markAttachPointsByR,
                            oxygen,
                            bondToBreak);
                }
            }
        }
    }

    /**
     * Splits cross-linking ether bonds in the given molecule (linear sugar moieties) and optionally marks the attachment points with R-groups.
     * <p>
     * This method identifies cross-linking ether bonds in the molecule using a SMARTS pattern and then breaks these bonds
     * while duplicating the formerly connecting oxygen atom.
     * The transformation can either mark the attachment points with R-groups or saturate the resulting open valences
     * with implicit hydrogen atoms, depending on the `markAttachPointsByR` parameter.
     * <p>
     * If bonds are split, the molecule may become disconnected. If no matching bonds are found, the original molecule remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which cross-linking ether bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated with implicit hydrogen atoms.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set minimum size for linear sugars
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitEthersCrossLinking(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        Mappings mappings = SmartsPattern.create(SugarDetectionUtility.CROSS_LINKING_ETHER_BOND_LINEAR_SUGARS_SMARTS).matchAll(molecule).uniqueAtoms();
        if (mappings.atLeast(1)) {
            for (IAtomContainer esterGroup : mappings.toSubstructures()) {
                IAtom carbonOne = null;
                IAtom carbonTwo = null;
                IAtom connectingOxygen = null;
                for (IAtom atom : esterGroup.atoms()) {
                    if (atom.getAtomicNumber() == IElement.O) {
                        connectingOxygen = atom;
                    } else if (carbonOne == null ) {
                        carbonOne = atom;
                    } else {
                        carbonTwo = atom;
                    }
                }
                //no need to copy stereo elements, the connecting oxygen is not duplicated
                IBond bondToBreak = molecule.getBond(carbonTwo, connectingOxygen);
                if (limitPostProcessingBySize && this.isFragmentTooSmall(molecule, bondToBreak, false)) {
                    continue;
                }
                molecule.removeBond(bondToBreak);
                IAtom[] atoms = new IAtom[] {connectingOxygen, carbonTwo};
                for (IAtom atom : atoms) {
                    this.saturate(
                            atom,
                            molecule,
                            markAttachPointsByR,
                            atom,
                            bondToBreak);
                }
            }
        }
    }

    /**
     * Splits O-glycosidic (ether) groups connecting circular or linear sugars in the given molecule and optionally
     * marks the attachment points with R-groups.
     * <p>
     * This method identifies ether bonds in the molecule using a SMARTS pattern and then breaks these bonds while
     * duplicating the formerly connecting oxygen atom.
     * The transformation can either mark the attachment points with R-groups or saturate the resulting open valences
     * with implicit hydrogen atoms, depending on the `markAttachPointsByR` parameter.
     * <p>
     * Depending on the `splitCircularSugars` parameter, the method uses either the SMARTS pattern for circular sugar
     * ester bonds or the pattern for linear sugar ester bonds (see the public constants).
     * <p>
     * If bonds are split, the molecule may become disconnected. If no matching bonds are found, the original molecule remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which O-glycosidic (ether) bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated with implicit hydrogen atoms.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set preservation mode threshold or the set
     *                                  minimum size for linear sugars (depends on 'splitCircularSugars').
     * @param splitCircularSugars If true, the SMARTS pattern for circular sugar ester bonds is used;
     *                            if false, the pattern for linear sugar ester bonds is used.
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitOGlycosidicBondsAndEthers(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize,
            boolean splitCircularSugars
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        Mappings mappings = SmartsPattern.create(
                splitCircularSugars? SugarDetectionUtility.O_GLYCOSIDIC_BOND_CIRCULAR_SUGARS_SMARTS : SugarDetectionUtility.ETHER_BOND_LINEAR_SUGARS_SMARTS)
                .matchAll(molecule).uniqueAtoms();
        if (mappings.atLeast(1)) {
            for (IAtomContainer esterGroup : mappings.toSubstructures()) {
                IAtom carbonOne = null;
                IAtom connectingOxygen = null;
                for (IAtom atom : esterGroup.atoms()) {
                    if (atom.getAtomicNumber() == IElement.O) {
                        connectingOxygen = atom;
                    } else if (carbonOne == null ) {
                        carbonOne = atom;
                    }
                }
                IBond bondToBreak = molecule.getBond(carbonOne, connectingOxygen);
                if (limitPostProcessingBySize && this.isFragmentTooSmall(molecule, bondToBreak, splitCircularSugars)) {
                    continue;
                }
                IAtom newOxygen = molecule.newAtom(IElement.O);
                molecule.newBond(carbonOne, newOxygen, IBond.Order.SINGLE);
                IStereoElement updatedStereoElement = null;
                for (IStereoElement stereoElement : molecule.stereoElements()) {
                    if (stereoElement.getFocus().equals(carbonOne) && stereoElement.contains(connectingOxygen)) {
                        updatedStereoElement = stereoElement.updateCarriers(connectingOxygen, newOxygen);
                        break;
                    }
                }
                if (updatedStereoElement != null) {
                    molecule.addStereoElement(updatedStereoElement);
                }
                molecule.removeBond(bondToBreak);
                IAtom[] oxygens = new IAtom[] {connectingOxygen, newOxygen};
                for (IAtom oxygen : oxygens) {
                    this.saturate(
                            oxygen,
                            molecule,
                            markAttachPointsByR,
                            oxygen,
                            bondToBreak);
                }
            }
        }
    }

    /**
     * Splits peroxide groups in the given molecule (circular or linear sugar moieties) and
     * optionally marks the attachment points with R-groups.
     * <p>
     * This method identifies peroxide bonds in the molecule using a SMARTS pattern and then breaks these bonds.
     * The transformation can either mark the attachment points with R-groups or saturate the resulting open valences
     * with implicit hydrogen atoms, depending on the `markAttachPointsByR` parameter.
     * <p>
     * Depending on the `splitCircularSugars` parameter, the method uses either the SMARTS pattern for circular sugar
     * ester bonds or the pattern for linear sugar ester bonds (see the public constants).
     * <p>
     * If bonds are split, the molecule may become disconnected. If no matching bonds are found, the original molecule remains unchanged.
     * Note: SMIRKS transformations are not used here, since they create a copy of the molecule and that would destroy
     * the atom and bond mapping to the original molecule.
     *
     * @param molecule The molecule in which peroxide bonds are to be split. Must not be null.
     * @param markAttachPointsByR If true, the attachment points are marked with R-groups; otherwise, they are saturated with implicit hydrogen atoms.
     * @param limitPostProcessingBySize If true, the bond will only be split if both resulting fragments are large enough
     *                                  to be preserved according to the set preservation mode threshold or the set
     *                                  minimum size for linear sugars (depends on 'splitCircularSugars').
     * @param splitCircularSugars If true, the SMARTS pattern for circular sugar ester bonds is used;
     *                            if false, the pattern for linear sugar ester bonds is used.
     * @throws NullPointerException If the input molecule is null.
     */
    protected void splitPeroxides(
            IAtomContainer molecule,
            boolean markAttachPointsByR,
            boolean limitPostProcessingBySize,
            boolean splitCircularSugars
    ) {
        if (molecule == null) {
            throw new NullPointerException("The input molecule must not be null.");
        }
        if (molecule.isEmpty()) {
            return; //nothing to do
        }
        Mappings mappings = SmartsPattern.create(
                splitCircularSugars? SugarDetectionUtility.PEROXIDE_BOND_CIRCULAR_SUGARS_SMARTS: SugarDetectionUtility.PEROXIDE_BOND_LINEAR_SUGARS_SMARTS)
                .matchAll(molecule).uniqueAtoms();
        if (mappings.atLeast(1)) {
            for (IAtomContainer esterGroup : mappings.toSubstructures()) {
                IAtom oxygenOne = null;
                IAtom oxygenTwo = null;
                for (IAtom atom : esterGroup.atoms()) {
                    if (atom.getAtomicNumber() == IElement.O) {
                        if (oxygenOne == null) {
                            oxygenOne = atom;
                        } else {
                            oxygenTwo = atom;
                        }
                    }
                }
                IBond bondToBreak = molecule.getBond(oxygenOne, oxygenTwo);
                if (limitPostProcessingBySize && this.isFragmentTooSmall(molecule, bondToBreak, splitCircularSugars)) {
                    continue;
                }
                //no need to copy stereo elements, the connecting oxygen is not duplicated
                molecule.removeBond(bondToBreak);
                IAtom[] atoms = new IAtom[] {oxygenOne, oxygenTwo};
                for (IAtom atom : atoms) {
                    this.saturate(
                            atom,
                            molecule,
                            markAttachPointsByR,
                            atom,
                            bondToBreak);
                }
            }
        }
    }

    /**
     * Checks whether breaking the specified bond would result in a fragment that is too small by the set criterion.
     * <p>
     * This method creates a temporary copy of the input molecule, removes the specified bond, and then
     * checks if any of the resulting disconnected fragments would be smaller than the preservation
     * threshold for circular sugars or smaller than the linear sugar candidate minimum size for linear sugars.
     * This is used during postprocessing to prevent the splitting of bonds that would
     * create fragments too small to be meaningful (e.g., small substituents like methyl groups).
     * <p>
     * This method is typically called before actually breaking bonds during postprocessing to ensure
     * that only bonds connecting sufficiently large fragments are split.
     * <p>
     * No checks are performed!
     *
     * @param molecule The molecule containing the bond to be evaluated
     * @param bondToBreak The bond whose removal would be tested for fragment size
     * @param isCriterionForCircularSugars if true, the size evaluated based on the set preservation mode
     *                                     threshold; otherwise, the linear sugar candidate minimum size is used
     * @return True if breaking the bond would result in at least one fragment that is too small
     *         to preserve according to the applied criterion; false if all resulting
     *         fragments would be large enough to preserve.
     */
    protected boolean isFragmentTooSmall(IAtomContainer molecule, IBond bondToBreak, boolean isCriterionForCircularSugars) {
        //split the detected bond in a copy first to see whether the resulting fragment is large enough
        float loadFactor = 0.75f; //default load factor for HashMaps
        //ensuring sufficient initial capacity
        int atomMapInitCapacity = (int)((molecule.getAtomCount() / loadFactor) + 3.0f);
        int bondMapInitCapacity = (int)((molecule.getBondCount() / loadFactor) + 3.0f);
        Map<IAtom, IAtom> inputAtomToAtomCopyMap = new HashMap<>(atomMapInitCapacity);
        Map<IBond, IBond> inputBondToBondCopyMap = new HashMap<>(bondMapInitCapacity);
        IAtomContainer moleculeCopy = this.deeperCopy(molecule, inputAtomToAtomCopyMap, inputBondToBondCopyMap);
        moleculeCopy.removeBond(inputBondToBondCopyMap.get(bondToBreak));
        //do not split the bond if a resulting fragment is too small
        for (IAtomContainer fragment : ConnectivityChecker.partitionIntoMolecules(moleculeCopy)) {
            if (isCriterionForCircularSugars) {
                if (this.isTooSmallToPreserve(fragment)) {
                    return true;
                }
            } else {
                //criterion for LINEAR sugars
                if (fragment.getAtomCount() < this.getLinearSugarCandidateMinSizeSetting()) {
                    return true;
                }
            }

        }
        return false;
    }
}

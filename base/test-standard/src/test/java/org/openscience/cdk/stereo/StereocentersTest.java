/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.stereo;

import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Verifies the types of atoms accepted as exhibiting stereo chemistry.
 *
 * @author John May
 * @cdk.module test-standard
 */
public class StereocentersTest {

    @Test
    public void boron_v4_anion() throws Exception {

        tetrahedral("[BH-](C)(N)O");
        tetrahedral("[B-](C)(N)(O)CC");

        none("[BH2-](C)(C)");
        none("[BH3-](C)");
        none("[BH4-]");

        none("[B-](=C)(=C)(=C)(=C)"); // abnormal valence
        none("[B-](=C)(=C)");
        none("[B-](=C)(C)(C)(C)");

        none("B(C)");
        none("B(C)(N)");
        none("B(C)(N)O");
        none("B(C)(N)(O)CC"); // abnormal valence
    }

    @Test
    public void carbon_v4_neutral() throws Exception {

        // accept Sp3 Carbons with < 2 hydrogens
        tetrahedral("C(C)(N)(O)");
        tetrahedral("C(C)(N)(O)CC");

        // reject when > 1 hydrogen or < 4 neighbors
        none("C");
        none("C(C)");
        none("C(C)(N)");
        none("C(=C)(C)N");
        bicoordinate("C(=CC)=CC");
        none("C(=C)(=C)(=C)=C"); // nb abnormal valence
        none("C#N");
    }

    @Test
    public void carbon_cation() throws Exception {
        none("[C+](C)(N)(O)");
        none("[C+](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void carbon_anion() throws Exception {
        none("[C-](C)(N)(O)");
        none("[C-](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void silicon_v4_neutral() throws Exception {
        tetrahedral("[SiH](C)(N)(O)");
        tetrahedral("[Si](C)(N)(O)CC");

        none("[Si](=C)(C)C");
        none("[Si](=C)=C");
        none("[Si](#C)C");
    }

    @Test
    public void silicon_cation() throws Exception {
        none("[Si+](C)(N)(O)");
        none("[Si+](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void silicon_anion() throws Exception {
        none("[Si-](C)(N)(O)");
        none("[Si-](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void germanium_v4_neutral() throws Exception {
        tetrahedral("[GeH](C)(N)(O)");
        tetrahedral("[Ge](C)(N)(O)CC");

        none("[Ge](=C)(C)C");
        none("[Ge](=C)=C");
        none("[Ge](#C)C");
    }

    @Test
    public void germanium_cation() throws Exception {
        none("[Ge+](C)(N)(O)");
        none("[Ge+](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void germanium_anion() throws Exception {
        none("[Ge-](C)(N)(O)");
        none("[Ge-](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void tin_v4_neutral() throws Exception {
        tetrahedral("[SnH](C)(N)(O)");
        tetrahedral("[Sn](C)(N)(O)CC");

        none("[Sn](=C)(C)C");
        none("[Sn](=C)=C");
        none("[Sn](#C)C");
    }

    @Test
    public void tin_cation() throws Exception {
        none("[Sn+](C)(N)(O)");
        none("[Sn+](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void tin_anion() throws Exception {
        none("[Sn-](C)(N)(O)");
        none("[Sn-](C)(N)(O)CC"); // nb abnormal valence
    }

    @Test
    public void nitrogen_v3_neutral() throws Exception {

        // nitrogen inversion -> reject
        none("N");
        none("N(C)(N)(O)");
        none("N(=C)(C)");
    }

    @Test
    public void nitrogen_v3_neutral_in_small_ring() throws Exception {
        tetrahedral("N(C)(C1)O1");
        tetrahedral("N(C)(C1)C1C");
    }

    @Test
    public void nitrogen_v3_neutral_in_larger_ring() throws Exception {
        none("N(C)(C1)CCCC1"); // n.b. equivalence checked later
        none("N(C)(C1)CCCC1C");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void nitrogen_v3_neutral_reject_H() throws Exception {
        none("N(C1)C1"); // n.b. equivalence checked later
        none("N(C1)C1C");
    }

    @Test
    public void nitrogen_v4_cation() throws Exception {
        tetrahedral("[N+](C)(N)(O)CC");
        none("[N+](=C)(C)C");
        none("[N+](=C)=C");
        none("[N+](#C)C");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void nitrogen_v4_cation_reject_h() throws Exception {
        none("[NH+](=C)(C)C");
        none("[NH2+](C)C");
        none("[NH3+]C");
        none("[NH4+]");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void nitrogen_v4_cation_reject_h_on_terminal() throws Exception {
        none("[N+](N)([NH])(C)CC");
        none("[N+](O)([O])(C)CC");
        none("[N+](S)([S])(C)CC");
        none("[N+]([SeH])([Se])(C)C");
        none("[N+]([TeH])([Te])(C)C");
    }

    @Test
    public void nitrogen_v5_neutral() throws Exception {
        tetrahedral("N(=C)(C)(N)O");
        none("N(=C)(=C)C");
        none("N(#C)=C");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void nitrogen_v5_neutral_reject_h() throws Exception {
        none("N(=C)(C)(C)");
        none("N(=C)(C)");
        none("N(=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void nitrogen_v5_neutral_reject_h_on_terminal() throws Exception {
        none("N(N)(=N)(C)CC");
        none("N(O)(=O)(C)CC");
        none("N(S)(=S)(C)CC");
        none("N([SeH])(=[Se])(C)C");
        none("N([TeH])(=[Te])(C)C");
    }

    // n.b. undocumented by the InChI tech manual
    @Test
    public void phosphorus_v3_neutral() throws Exception {
        tetrahedral("P(C)(N)(O)");
        none("P(=C)(C)");
        none("P(#C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v3_neutral_reject_H() throws Exception {
        none("P(C)(C)");
        none("P(C)");
        none("P");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v3_neutral_reject_h_on_terminal() throws Exception {
        none("P(N)([NH4])C");
        none("P(S)([SH4])C");

    }

    @Test
    public void phosphorus_v4_cation() throws Exception {
        tetrahedral("[P+](C)(N)(O)CC");
        none("[P+](=C)(C)C");
        none("[P+](=C)=C");
        none("[P+](#C)C");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor. Since InChI software v.
     * 1.02-standard (2009), phosphines and arsines are always treated as
     * stereogenic even with H atom neighbors
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v4_cation_accept_h() throws Exception {
        tetrahedral("[PH+](C)(N)O");
        none("[PH2+](C)C");
        none("[PH3+]C");
        none("[PH4+]");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v4_cation_reject_h_on_terminal() throws Exception {
        none("[P+](N)([N])(C)CC");
        none("[P+](O)([O])(C)CC");
        none("[P+](S)([S])(C)CC");
        none("[P+]([SeH])([Se])(C)CC");
        none("[P+]([TeH])([Te])(C)CC");
    }

    @Test
    public void phosphorus_v5_neutral() throws Exception {
        tetrahedral("P(=C)(C)(N)O");
        none("P(=C)(=C)C");
        none("P(#C)=C");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v5_neutral_reject_h() throws Exception {
        none("P(=C)(C)(C)");
        none("P(=C)(C)");
        none("P(=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void phosphorus_v5_neutral_reject_h_on_terminal() throws Exception {
        none("P(=N)(N)(C)CC");
        none("P(=O)(O)(C)CC");
        none("P(=O)([O-])(C)CC");
        none("P(=S)(S)(C)CC");
        none("P(=[Se])([SeH])(C)C");
        none("P(=[Te])([TeH])(C)C");
    }

    @Test
    public void arsenic_v4_cation() throws Exception {
        tetrahedral("[As+](C)(N)(O)CC");
        none("[As+](=C)(C)(C)");
        none("[As+](=C)(=C)");
        none("[As+](#C)(C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor. Since InChI software v.
     * 1.02-standard (2009), phosphines and arsines are always treated as
     * stereogenic even with H atom neighbors
     *
     * @throws Exception
     */
    @Test
    public void arsenic_v4_cation_accept_h() throws Exception {
        tetrahedral("[AsH+](C)(N)O");
        none("[AsH2+](C)C");
        none("[AsH3+]C");
        none("[AsH4+]");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void arsenic_v4_cation_reject_h_on_terminal() throws Exception {
        none("[As+](N)([N])(C)CC");
        none("[As+](O)([O])(C)CC");
        none("[As+](S)([S])(C)CC");
        none("[As+]([SeH])([Se])(C)CC");
        none("[As+]([TeH])([Te])(C)CC");
    }

    @Test
    public void sulphur_4v_neutral() throws Exception {
        tetrahedral("S(=O)(C)CC");
        none("S(C)(N)(O)CC");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void sulphur_4v_neutral_reject_h() throws Exception {
        none("S(=O)(C)");
        none("S(=O)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void sulphur_4v_neutral_reject_h_on_terminal() throws Exception {
        none("S(=N)(N)C");
        none("S(=O)(O)C");
        none("S(=S)(S)C");
        none("S(=[Se])([SeH])C");
        none("S(=[Te])([TeH])C");

        tetrahedral("S(=O)(S)N");
    }

    @Test
    public void sulphur_3v_cation() throws Exception {
        tetrahedral("[S+](C)(N)(O)");
        none("[S+](=C)(C)");
    }

    @Test
    public void sulphur_1v_anion() throws Exception {
        none("[S-](C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void sulphur_3v_cation_reject_h() throws Exception {
        none("[SH+](C)(C)");
        none("[SH2+](C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void sulphur_3v_cation_reject_h_on_terminal() throws Exception {
        none("[S+](N)([N])(C)");
        none("[S+](O)([O])(C)");
        none("[S+]([SeH])([Se])(C)");
        none("[S+]([TeH])([Te])(C)");

        tetrahedral("[S+](O)(OC)(C)");
        tetrahedral("[S+](OC)(OC)(C)");
    }

    @Test
    public void sulphur_6v_neutral() throws Exception {
        tetrahedral("S(=C)(=CC)(C)(CC)");
        none("S(=C)(C)(CC)(CCC)(CCCC)");
        none("S(C)(C)(CC)(CCCC)(CCCC)(CCCCC)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void sulphur_6v_neutral_reject_h() throws Exception {
        none("S(=C)(=C)(C)");
        none("S(=C)(=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void sulphur_6v_neutral_reject_h_on_terminal() throws Exception {
        none("S(=N)(=C)(N)(C)");
        none("S(=O)(=C)(O)(C)");
        none("S(=S)(=C)(S)(C)");
        none("S(=[Se])(=C)([SeH])(C)");
        none("S(=[Te])(=C)([TeH])(C)");

        tetrahedral("S(=O)(=N)(S)(C)");
    }

    @Test
    public void sulphur_5v_cation() throws Exception {
        tetrahedral("[S+](=C)(N)(O)(C)");
        none("[S+](C)(C)(C)(C)(C)");
    }

    @Test
    public void sulphur_3v_anion() throws Exception {
        none("[S-](C)(C)(C)");
        none("[S-](=C)(C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void sulphur_5v_cation_reject_h() throws Exception {
        none("[SH+](=C)(CC)(CCC)");
        none("[SH2+](=C)(C)");
        none("[SH3+](=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void sulphur_5v_cation_reject_h_on_terminal() throws Exception {
        none("[S+](=N)(N)(C)(CC)");
        none("[S+](=O)(O)(C)(CC)");
        none("[S+](=[Se])([SeH])(C)(CC)");
        none("[S+](=[Te])([TeH])(C)(CC)");

        tetrahedral("[S+](=O)(N)(C)(CC)");
        tetrahedral("[S+](=O)(N)(S)(CC)");
    }

    @Test
    public void selenium_4v_neutral() throws Exception {
        tetrahedral("[Se](=O)(C)(CC)");
        none("[Se](C)(CC)(CCC)(CCCC)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void selenium_4v_neutral_reject_h() throws Exception {
        none("[SeH](=O)(C)");
        none("[SeH2](=O)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void selenium_4v_neutral_reject_h_on_terminal() throws Exception {
        none("[Se](=N)(N)C");
        none("[Se](=O)(O)C");
        none("[Se](=S)(S)C");
        none("[Se](=[Se])([SeH])C");
        none("[Se](=[Te])([TeH])C");

        tetrahedral("[Se](=O)(S)N");
    }

    @Test
    public void selenium_3v_cation() throws Exception {
        tetrahedral("[Se+](C)(CC)(CCC)");
        none("[Se+](=C)(C)");
    }

    @Test
    public void selenium_1v_anion() throws Exception {
        none("[Se-](C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void selenium_3v_cation_reject_h() throws Exception {
        none("[SeH+](C)(C)");
        none("[SeH2+](C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void selenium_3v_cation_reject_h_on_terminal() throws Exception {
        none("[Se+](N)(=N)(C)C");
        none("[Se+](O)(=O)(C)C");
        none("[Se+](O)(=O)(C)C");
        none("[Se+]([SeH])(=[Se])(C)C");
        none("[Se+]([TeH])(=[Te])(C)C");

        tetrahedral("[Se+](O)(=N)([SeH])C");
        tetrahedral("[Se+](O)(=N)(C)CC");
    }

    @Test
    public void selenium_6v_neutral() throws Exception {
        tetrahedral("[Se](=C)(=CC)(C)(CC)");
        none("[Se](=C)(C)(CC)(CCC)(CCCC)");
        none("[Se](C)(C)(CC)(CCC)(CCCC)(CCCC)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void selenium_6v_neutral_reject_h() throws Exception {
        none("[SeH](=C)(=C)(C)");
        none("[SeH2](=C)(=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void selenium_6v_neutral_reject_h_on_terminal() throws Exception {
        none("[Se](=N)(=N)(N)(C)");
        none("[Se](=O)(=O)(O)(C)");
        none("[Se](=S)(=S)(S)(C)");
        none("[Se](=[Se])(=[Se])([SeH])(C)");
        none("[Se](=[Te])(=[Te])([TeH])(C)");

        tetrahedral("[Se](=O)(=N)(S)(C)");
    }

    @Test
    public void selenium_5v_cation() throws Exception {
        tetrahedral("[Se+](=C)(CC)(CCC)(CCCC)");
        none("[Se+](C)(CC)(CCC)(CCCC)(CCCCC)");
    }

    @Test
    public void selenium_3v_anion() throws Exception {
        none("[Se-](C)(C)(C)");
        none("[Se-](=C)(C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (a) A terminal H atom neighbor
     *
     * @throws Exception
     */
    @Test
    public void selenium_5v_cation_reject_h() throws Exception {
        none("[SeH+](=C)(C)(CC)");
        none("[SeH2+](=C)(C)");
        none("[SeH3+](=C)");
    }

    /**
     * An atom or positive ion N, P, As, S, or Se is not treated as stereogenic
     * if it has - (b) At least two terminal neighbors, XHm and XHn, (n+m>0)
     * connected by any kind of bond, where X is O, S, Se, Te, or N.
     *
     * @throws Exception
     */
    @Test
    public void selenium_5v_cation_reject_h_on_terminal() throws Exception {
        none("[Se+](=N)(N)(C)(CC)");
        none("[Se+](=O)(O)(C)(CC)");
        none("[Se+](=[Se])([SeH])(C)(CC)");
        none("[Se+](=[Te])([TeH])(C)(CC)");

        tetrahedral("[Se+](=O)(N)(C)(CC)");
        tetrahedral("[Se+](=O)(N)(S)(CC)");
    }

    /** Geometric. */

    @Test
    public void carbon_neutral_geometric() throws Exception {
        geometric("C(=CC)C");
        geometric("[CH](=CC)C");
        geometric("C([H])(=CC)C");
        none("[CH2](=CC)");
        bicoordinate("C(=C)(=CC)");
        none("C(#CC)C");
    }

    @Test
    public void silicon_neutral_geometric() throws Exception {
        geometric("[SiH](=[SiH]C)C");
        geometric("[Si]([H])(=[SiH]C)C");
        none("[Si](=C)(=CC)");
        none("[Si](#CC)C");
    }

    @Test
    public void germanium_neutral_geometric() throws Exception {
        geometric("[GeH](=[GeH]C)C");
        geometric("[Ge]([H])(=[GeH]C)C");
        none("[Ge](=C)(=CC)");
        none("[Ge](#CC)C");
    }

    /**
     * This one is a bit of an odd bull and changes depending on hydrogen
     * representation. In most cast it's probably tautomeric. Note that
     * InChI does allow it: InChI=1S/H2N2/c1-2/h1-2H/b2-1+
     */
    @Test
    public void nitrogen_neutral_geometric() throws Exception {
        test("N(=NC)C", Stereocenters.Type.Tricoordinate,true);
        test("N(=NC)", Stereocenters.Type.None, false);
        test("N(=N)C", Stereocenters.Type.None,false);
        test("N(=N)", Stereocenters.Type.None, false);
        test("N(=NC)[H]", Stereocenters.Type.Tricoordinate, false);
        test("N(=N[H])[H]", Stereocenters.Type.Tricoordinate,false);
        test("N(=N[H])[H]", Stereocenters.Type.Tricoordinate, false);
    }

    @Test
    public void nitrogen_cation_geometric() throws Exception {
        geometric("[NH+](=[NH+]C)C");
        geometric("[N+]([H])(=[NH+]C)C");
        none("[NH2+](=[NH+]C)C");
    }

    @Test
    public void bridgehead_nitrogens() throws Exception {
        tetrahedral("N1(CC2)CC2CC1");
        // fused
        none("N1(CCCC2)CCCC12");
        // adjacent to fused (but not fused)
        tetrahedral("N1(c(cccc3)c32)CC2CC1");
    }

    // assert the first atom of the SMILES is accepted as a tetrahedral center
    void tetrahedral(String smi) throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        test(sp.parseSmiles(smi), Stereocenters.Type.Tetracoordinate, smi + " was not accepted", true);
    }

    // assert the first atom of the SMILES is accepted as a geometric center
    void geometric(String smi) throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        test(sp.parseSmiles(smi), Stereocenters.Type.Tricoordinate, smi + " was not accepted", true);
    }

    // assert the first atom of the SMILES is accepted as a bicoordinate center
    void bicoordinate(String smi) throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        test(sp.parseSmiles(smi), Stereocenters.Type.Bicoordinate, smi + " was not accepted", true);
    }

    // assert the first atom of the SMILES is non stereogenic
    void none(String smi) throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        test(sp.parseSmiles(smi), Stereocenters.Type.None, smi + " was not rejected", true);
    }

    // check if the first atom of the container is accepted
    void test(IAtomContainer container, Stereocenters.Type type, String mesg, boolean hnorm) {
        assertThat(mesg, Stereocenters.of(container).elementType(0), is(type));
        if (hnorm) {
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(container);
            assertThat(mesg + " (unsupressed hydrogens)", Stereocenters.of(container).elementType(0), is(type));
        }
    }

    void test(String smi, Stereocenters.Type type, boolean hnorm) throws Exception {
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        test(sp.parseSmiles(smi), type, smi + " was not accepted", hnorm);
    }
}

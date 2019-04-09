/* Copyright (C) 2007  Todd Martin (Environmental Protection Agency)  <Martin.Todd@epamail.epa.gov>
 * Copyright (C) 2007  Nikolay Kochev <nick@argon.acad.bg>
 *               2014  Mark B Vine (orcid:0000-0002-7794-0426)
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
package org.openscience.cdk.qsar.descriptors.molecular;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.atomtype.EStateAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.qsar.AbstractMolecularDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.AtomicProperties;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class calculates ALOGP (Ghose-Crippen LogKow) and the
 * Ghose-Crippen molar refractivity {@cdk.cite GHOSE1986,GHOSE1987}.
 *
 * <b>Note</b> The code assumes that aromaticity has been detected before
 * evaluating this descriptor. The code also expects that the molecule
 * will have hydrogens explicitly set. For SD files, this is usually not
 * a problem since hydrogens are explicit. But for the case of molecules
 * obtained from SMILES, hydrogens must be made explicit.
 *
 * <p>TODO: what should sub return if have missing fragment?
 * Just report sum for other fragments? Or report as -9999 and
 * then do not use descriptor if have this  value for any
 * chemicals in cluster?
 *
 * <table border="1"><caption>Parameters for this descriptor:</caption>
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td></td>
 *     <td></td>
 *     <td>no parameters</td>
 *   </tr>
 * </table>
 *
 * Returns three values
 * <ol>
 * <li>ALogP  - Ghose-Crippen LogKow
 * <li>ALogP2
 * <li>amr  - molar refractivity
 * </ol>
 *
 * @author     Todd Martin
 * @cdk.module qsarmolecular
 * @cdk.githash
 * @cdk.keyword logP
 * @cdk.keyword lipophilicity
 * @cdk.keyword refractivity
 * @see org.openscience.cdk.tools.CDKHydrogenAdder
 */
public class ALOGPDescriptor extends AbstractMolecularDescriptor implements IMolecularDescriptor {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(ALOGPDescriptor.class);

    IAtomContainer atomContainer;
    IRingSet       rs;
    String[]       fragment;                                                                     // estate fragments for each atom

    AtomicProperties ap;                                                                           // needed to retrieve electronegativities

    public int[] frags = new int[121];                                               // counts of each type of fragment in the molecule
    public int[] alogpfrag;                                                                    // alogp fragments for each atom (used to see which atoms have missing fragments)
    final static double[] FRAGVAL = new double[121];                                             // coefficients for alogp model

    static {
        // fragments for ALOGP from Ghose et al., 1998
        FRAGVAL[1] = -1.5603;
        FRAGVAL[2] = -1.012;
        FRAGVAL[3] = -0.6681;
        FRAGVAL[4] = -0.3698;
        FRAGVAL[5] = -1.788;
        FRAGVAL[6] = -1.2486;
        FRAGVAL[7] = -1.0305;
        FRAGVAL[8] = -0.6805;
        FRAGVAL[9] = -0.3858;
        FRAGVAL[10] = 0.7555;
        FRAGVAL[11] = -0.2849;
        FRAGVAL[12] = 0.02;
        FRAGVAL[13] = 0.7894;
        FRAGVAL[14] = 1.6422;
        FRAGVAL[15] = -0.7866;
        FRAGVAL[16] = -0.3962;
        FRAGVAL[17] = 0.0383;
        FRAGVAL[18] = -0.8051;
        FRAGVAL[19] = -0.2129;
        FRAGVAL[20] = 0.2432;
        FRAGVAL[21] = 0.4697;
        FRAGVAL[22] = 0.2952;
        FRAGVAL[23] = 0;
        FRAGVAL[24] = -0.3251;
        FRAGVAL[25] = 0.1492;
        FRAGVAL[26] = 0.1539;
        FRAGVAL[27] = 0.0005;
        FRAGVAL[28] = 0.2361;
        FRAGVAL[29] = 0.3514;
        FRAGVAL[30] = 0.1814;
        FRAGVAL[31] = 0.0901;
        FRAGVAL[32] = 0.5142;
        FRAGVAL[33] = -0.3723;
        FRAGVAL[34] = 0.2813;
        FRAGVAL[35] = 0.1191;
        FRAGVAL[36] = -0.132;
        FRAGVAL[37] = -0.0244;
        FRAGVAL[38] = -0.2405;
        FRAGVAL[39] = -0.0909;
        FRAGVAL[40] = -0.1002;
        FRAGVAL[41] = 0.4182;
        FRAGVAL[42] = -0.2147;
        FRAGVAL[43] = -0.0009;
        FRAGVAL[44] = 0.1388;
        FRAGVAL[45] = 0;
        FRAGVAL[46] = 0.7341;
        FRAGVAL[47] = 0.6301;
        FRAGVAL[48] = 0.518;
        FRAGVAL[49] = -0.0371;
        FRAGVAL[50] = -0.1036;
        FRAGVAL[51] = 0.5234;
        FRAGVAL[52] = 0.6666;
        FRAGVAL[53] = 0.5372;
        FRAGVAL[54] = 0.6338;
        FRAGVAL[55] = 0.362;
        FRAGVAL[56] = -0.3567;
        FRAGVAL[57] = -0.0127;
        FRAGVAL[58] = -0.0233;
        FRAGVAL[59] = -0.1541;
        FRAGVAL[60] = 0.0324;
        FRAGVAL[61] = 1.052;
        FRAGVAL[62] = -0.7941;
        FRAGVAL[63] = 0.4165;
        FRAGVAL[64] = 0.6601;
        FRAGVAL[65] = 0;
        FRAGVAL[66] = -0.5427;
        FRAGVAL[67] = -0.3168;
        FRAGVAL[68] = 0.0132;
        FRAGVAL[69] = -0.3883;
        FRAGVAL[70] = -0.0389;
        FRAGVAL[71] = 0.1087;
        FRAGVAL[72] = -0.5113;
        FRAGVAL[73] = 0.1259;
        FRAGVAL[74] = 0.1349;
        FRAGVAL[75] = -0.1624;
        FRAGVAL[76] = -2.0585;
        FRAGVAL[77] = -1.915;
        FRAGVAL[78] = 0.4208;
        FRAGVAL[79] = -1.4439;
        FRAGVAL[80] = 0;
        FRAGVAL[81] = 0.4797;
        FRAGVAL[82] = 0.2358;
        FRAGVAL[83] = 0.1029;
        FRAGVAL[84] = 0.3566;
        FRAGVAL[85] = 0.1988;
        FRAGVAL[86] = 0.7443;
        FRAGVAL[87] = 0.5337;
        FRAGVAL[88] = 0.2996;
        FRAGVAL[89] = 0.8155;
        FRAGVAL[90] = 0.4856;
        FRAGVAL[91] = 0.8888;
        FRAGVAL[92] = 0.7452;
        FRAGVAL[93] = 0.5034;
        FRAGVAL[94] = 0.8995;
        FRAGVAL[95] = 0.5946;
        FRAGVAL[96] = 1.4201;
        FRAGVAL[97] = 1.1472;
        FRAGVAL[98] = 0;
        FRAGVAL[99] = 0.7293;
        FRAGVAL[100] = 0.7173;
        FRAGVAL[101] = 0;
        FRAGVAL[102] = -2.6737;
        FRAGVAL[103] = -2.4178;
        FRAGVAL[104] = -3.1121;
        FRAGVAL[105] = 0;
        FRAGVAL[106] = 0.6146;
        FRAGVAL[107] = 0.5906;
        FRAGVAL[108] = 0.8758;
        FRAGVAL[109] = -0.4979;
        FRAGVAL[110] = -0.3786;
        FRAGVAL[111] = 1.5188;
        FRAGVAL[112] = 1.0255;
        FRAGVAL[113] = 0;
        FRAGVAL[114] = 0;
        FRAGVAL[115] = 0;
        FRAGVAL[116] = -0.9359;
        FRAGVAL[117] = -0.1726;
        FRAGVAL[118] = -0.7966;
        FRAGVAL[119] = 0.6705;
        FRAGVAL[120] = -0.4801;
    }

    final static double[] REFRACVAL = new double[121];                                            // coefficients for refractivity model

    static {
        // fragments for amr from Viswanadhan et al., 1989
        REFRACVAL[1] = 2.968;
        REFRACVAL[2] = 2.9116;
        REFRACVAL[3] = 2.8028;
        REFRACVAL[4] = 2.6205;
        REFRACVAL[5] = 3.015;
        REFRACVAL[6] = 2.9244;
        REFRACVAL[7] = 2.6329;
        REFRACVAL[8] = 2.504;
        REFRACVAL[9] = 2.377;
        REFRACVAL[10] = 2.5559;
        REFRACVAL[11] = 2.303;
        REFRACVAL[12] = 2.3006;
        REFRACVAL[13] = 2.9627;
        REFRACVAL[14] = 2.3038;
        REFRACVAL[15] = 3.2001;
        REFRACVAL[16] = 4.2654;
        REFRACVAL[17] = 3.9392;
        REFRACVAL[18] = 3.6005;
        REFRACVAL[19] = 4.487;
        REFRACVAL[20] = 3.2001;
        REFRACVAL[21] = 3.4825;
        REFRACVAL[22] = 4.2817;
        REFRACVAL[23] = 3.9556;
        REFRACVAL[24] = 3.4491;
        REFRACVAL[25] = 3.8821;
        REFRACVAL[26] = 3.7593;
        REFRACVAL[27] = 2.5009;
        REFRACVAL[28] = 2.5;
        REFRACVAL[29] = 3.0627;
        REFRACVAL[30] = 2.5009;
        REFRACVAL[31] = 0;
        REFRACVAL[32] = 2.6632;
        REFRACVAL[33] = 3.4671;
        REFRACVAL[34] = 3.6842;
        REFRACVAL[35] = 2.9372;
        REFRACVAL[36] = 4.019;
        REFRACVAL[37] = 4.777;
        REFRACVAL[38] = 3.9031;
        REFRACVAL[39] = 3.9964;
        REFRACVAL[40] = 3.4986;
        REFRACVAL[41] = 3.4997;
        REFRACVAL[42] = 2.7784;
        REFRACVAL[43] = 2.6267;
        REFRACVAL[44] = 2.5;
        REFRACVAL[45] = 0;
        REFRACVAL[46] = 0.8447;
        REFRACVAL[47] = 0.8939;
        REFRACVAL[48] = 0.8005;
        REFRACVAL[49] = 0.832;
        REFRACVAL[50] = 0.8;
        REFRACVAL[51] = 0.8188;
        REFRACVAL[52] = 0.9215;
        REFRACVAL[53] = 0.9769;
        REFRACVAL[54] = 0.7701;
        REFRACVAL[55] = 0;
        REFRACVAL[56] = 1.7646;
        REFRACVAL[57] = 1.4778;
        REFRACVAL[58] = 1.4429;
        REFRACVAL[59] = 1.6191;
        REFRACVAL[60] = 1.3502;
        REFRACVAL[61] = 1.945;
        REFRACVAL[62] = 0;
        REFRACVAL[63] = 0;
        REFRACVAL[64] = 11.1366;
        REFRACVAL[65] = 13.1149;
        REFRACVAL[66] = 2.6221;
        REFRACVAL[67] = 2.5;
        REFRACVAL[68] = 2.898;
        REFRACVAL[69] = 3.6841;
        REFRACVAL[70] = 4.2808;
        REFRACVAL[71] = 3.6189;
        REFRACVAL[72] = 2.5;
        REFRACVAL[73] = 2.7956;
        REFRACVAL[74] = 2.7;
        REFRACVAL[75] = 4.2063;
        REFRACVAL[76] = 4.0184;
        REFRACVAL[77] = 3.0009;
        REFRACVAL[78] = 4.7142;
        REFRACVAL[79] = 0;
        REFRACVAL[80] = 0;
        REFRACVAL[81] = 0.8725;
        REFRACVAL[82] = 1.1837;
        REFRACVAL[83] = 1.1573;
        REFRACVAL[84] = 0.8001;
        REFRACVAL[85] = 1.5013;
        REFRACVAL[86] = 5.6156;
        REFRACVAL[87] = 6.1022;
        REFRACVAL[88] = 5.9921;
        REFRACVAL[89] = 5.3885;
        REFRACVAL[90] = 6.1363;
        REFRACVAL[91] = 8.5991;
        REFRACVAL[92] = 8.9188;
        REFRACVAL[93] = 8.8006;
        REFRACVAL[94] = 8.2065;
        REFRACVAL[95] = 8.7352;
        REFRACVAL[96] = 13.9462;
        REFRACVAL[97] = 14.0792;
        REFRACVAL[98] = 14.073;
        REFRACVAL[99] = 12.9918;
        REFRACVAL[100] = 13.3408;
        REFRACVAL[101] = 0;
        REFRACVAL[102] = 0;
        REFRACVAL[103] = 0;
        REFRACVAL[104] = 0;
        REFRACVAL[105] = 0;
        REFRACVAL[106] = 7.8916;
        REFRACVAL[107] = 7.7935;
        REFRACVAL[108] = 9.4338;
        REFRACVAL[109] = 7.7223;
        REFRACVAL[110] = 5.7558;
        REFRACVAL[111] = 0;
        REFRACVAL[112] = 0;
        REFRACVAL[113] = 0;
        REFRACVAL[114] = 0;
        REFRACVAL[115] = 0;
        REFRACVAL[116] = 5.5306;
        REFRACVAL[117] = 5.5152;
        REFRACVAL[118] = 6.836;
        REFRACVAL[119] = 10.0101;
        REFRACVAL[120] = 5.2806;
    }

    String unassignedAtoms = "";

    double alogp  = 0.0;
    double amr    = 0.0;
    double alogp2 = 0.0;
    private static final String[] STRINGS = new String[]{"ALogP", "ALogp2", "AMR"};

    public ALOGPDescriptor() throws CDKException {
        try {
            ap = AtomicProperties.getInstance();
        } catch (Exception e) {
            logger.debug("Problem in accessing atomic properties. Can't calculate");
            throw new CDKException("Problem in accessing atomic properties. Can't calculate\n" + e.getMessage(), e);
        }
    }

    private void findUnassignedAtoms() {
        unassignedAtoms = "";

        for (int i = 0; i <= atomContainer.getAtomCount() - 1; i++) {
            if (alogpfrag[i] == 0) unassignedAtoms += (i + 1) + "(" + fragment[i] + "),";
        }
    }

    private double[] calculate(IAtomContainer atomContainer, String[] fragment, IRingSet rs) throws CDKException {
        this.atomContainer = atomContainer;
        this.fragment = fragment;
        this.rs = rs;
        alogp = 0.0;
        amr = 0.0;
        alogp2 = 0.0;

        alogpfrag = new int[atomContainer.getAtomCount()];

        for (int i = 1; i <= 120; i++) {
            frags[i] = 0;
        }

        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            // alogpfrag[i] = 0; // not needed (new array initalized above)
            try {
                if (fragment[i] != null) {
                    calcGroup001_005(i);
                    calcGroup002_006_007(i);
                    calcGroup003_008_009_010(i);
                    calcGroup004_011_to_014(i);
                    calcGroup015(i);
                    calcGroup016_018_036_037(i);
                    calcGroup017_019_020_038_to_041(i);
                    calcGroup021_to_023_040(i);
                    calcGroup024_027_030_033_042(i);
                    calcGroup025_026_028_029_031_032_034_035_043_044(i);
                    calcGroup056_57(i);
                    calcGroup058_61(i);
                    calcGroup059_060_063(i);
                    calcGroup066_to_079(i);
                    calcGroup081_to_085(i);
                    calcGroup086_to_090(i);
                    calcGroup091_to_095(i);
                    calcGroup096_to_100(i);
                    calcGroup101_to_104(i);
                    calcGroup106(i);
                    calcGroup107(i);
                    calcGroup108(i);
                    calcGroup109(i);
                    calcGroup110(i);
                    calcGroup111(i);
                    calcGroup112(i);
                    calcGroup115(i);
                    calcGroup116_117_120(i);
                    calcGroup118_119(i);
                }
            } catch (Exception e) {
                throw new CDKException(e.toString(), e);
            }
        } // end i atom loop

        logger.debug("\nFound fragments and frequencies ");

        for (int i = 1; i <= 120; i++) {
            alogp += FRAGVAL[i] * frags[i];
            amr += REFRACVAL[i] * frags[i];
            if (frags[i] > 0) {
                logger.debug("frag " + i + "  --> " + frags[i]);
            }
        }
        alogp2 = alogp * alogp;

        this.findUnassignedAtoms();

        return new double[]{alogp, alogp2, amr};

    }

    private static boolean isHetero(IAtom atom) {
        switch (atom.getAtomicNumber()) {
            case 5: // B
            case 7: // N
            case 8: // O
            case 9: // F
            case 14: // Si
            case 15: // P
            case 16: // S
            case 17: // Cl
            case 34: // Se
            case 35: // Br
            case 53: // I
                return true;
            default:
                return false;
        }
    }

    private void calcGroup001_005(int i) {
        // C in CH3R
        if (fragment[i].equals("SsCH3")) {
            List<IAtom> ca    = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            IAtom       atom  = atomContainer.getAtom(i);
            int         htype = getHAtomType(atom, ca);
            frags[htype] += atom.getImplicitHydrogenCount();
            for (int j = 0; j < ca.size(); j++) {
                if (ca.get(j).getSymbol().equals("C")) {
                    frags[1]++;
                    alogpfrag[i] = 1;
                }
                else if (ca.get(j).getSymbol().equals("H")) {
                    frags[htype]++;
                    alogpfrag[atomContainer.indexOf(ca.get(j))] = htype;
                }
                else {
                    frags[5]++;
                    alogpfrag[i] = 5;
                }
            }
        }

    }

    private void calcGroup002_006_007(int i) {
        // C in CH2RX

        if (fragment[i].equals("SssCH2")) {

            IAtom       atom  = atomContainer.getAtom(i);
            List<IAtom> nbors = atomContainer.getConnectedAtomsList(atom);
            int         htype = getHAtomType(atom, nbors);
            frags[htype] += atom.getImplicitHydrogenCount();

            int         carbonCount = 0;
            int         heteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j < nbors.size(); j++) {
                if (nbors.get(j).getSymbol().equals("C"))
                    carbonCount++;
                else if (nbors.get(j).getSymbol().equals("H")) {
                    frags[htype]++;
                    alogpfrag[atomContainer.indexOf(nbors.get(j))] = htype;
                }
                else
                    heteroCount++;
            }

            if (carbonCount == 2 && heteroCount == 0) {
                frags[2]++;
                alogpfrag[i] = 2;
            }
            else if (carbonCount == 1 && heteroCount == 1) {
                frags[6]++;
                alogpfrag[i] = 6;
            }
            else if (carbonCount == 0 && heteroCount == 2) {
                frags[7]++;
                alogpfrag[i] = 7;
            }
        }
    }

    private void calcGroup003_008_009_010(int i) {

        if (fragment[i].equals("SsssCH")) {

            IAtom atom  = atomContainer.getAtom(i);
            List  ca    = atomContainer.getConnectedAtomsList(atom);
            int   htype = getHAtomType(atom, ca);
            frags[htype] += atom.getImplicitHydrogenCount();

            int carbonCount = 0;
            int heteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j <= ca.size() - 1; j++) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C"))
                    carbonCount++;
                else if (((IAtom) ca.get(j)).getSymbol().equals("H")) {
                    frags[htype]++;
                    alogpfrag[((IAtom) ca.get(j)).getIndex()] = htype;
                }
                else
                    heteroCount++;
            }

            if (carbonCount == 3 && heteroCount == 0) {
                frags[3]++;
                alogpfrag[i] = 3;
            }
            else if (carbonCount == 2 && heteroCount == 1) {
                frags[8]++;
                alogpfrag[i] = 8;
            }
            else if (carbonCount == 1 && heteroCount == 2) {
                frags[9]++;
                alogpfrag[i] = 9;
            }
            else if (carbonCount == 0 && heteroCount == 3) {
                frags[10]++;
                alogpfrag[i] = 10;
            }
        }
    }

    private void calcGroup004_011_to_014(int i) {
        // C in CR4, CR3X, CX4
        if (fragment[i].equals("SssssC")) {
            List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            int carbonCount = 0;
            int heteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j <= ca.size() - 1; j++) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C"))
                    carbonCount++;
                else
                    heteroCount++;
            }

            if (carbonCount == 4 && heteroCount == 0) {
                frags[4]++;
                alogpfrag[i] = 4;
            }
            else if (carbonCount == 3 && heteroCount == 1) {
                frags[11]++;
                alogpfrag[i] = 11;
            }
            else if (carbonCount == 2 && heteroCount == 2) {
                frags[12]++;
                alogpfrag[i] = 12;
            }
            else if (carbonCount == 1 && heteroCount == 3) {
                frags[13]++;
                alogpfrag[i] = 13;
            }
            else if (carbonCount == 0 && heteroCount == 4) {
                frags[14]++;
                alogpfrag[i] = 14;
            }
        }
    }

    private void calcGroup015(int i) {
        if (fragment[i].equals("SdCH2")) {
            frags[15]++;
            alogpfrag[i] = 15;
            IAtom atom = atomContainer.getAtom(i);
            int   htype = getHAtomType(atom, null);
            frags[htype] += 2;
            for (IBond bond : atom.bonds()) {
                IAtom nbr = bond.getOther(atom);
                if (nbr.getAtomicNumber() == 1)
                    alogpfrag[nbr.getIndex()] = htype;
            }
        }
    }

    private void calcGroup016_018_036_037(int i) {

        IAtom ai = atomContainer.getAtom(i);
        if (!fragment[i].equals("SdsCH")) return;

        List<IAtom> ca    = atomContainer.getConnectedAtomsList(ai);
        int         htype = getHAtomType(atomContainer.getAtom(i), ca);
        frags[htype]++;

        boolean haveCdX = false;
        boolean haveCsX = false;
        boolean haveCsAr = false;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (((IAtom) ca.get(j)).getSymbol().equals("H")) {
                alogpfrag[atomContainer.indexOf(ca.get(j))] = htype;
                continue;
            }

            if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (!((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    haveCsX = true;
                }

                if (((IAtom) ca.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                    haveCsAr = true;
                }

            }
            else if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (!((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    haveCdX = true;
                }
            }
        }

        if (haveCdX) {
            if (haveCsAr) {
                frags[37]++;
                alogpfrag[i] = 37;
            }
            else {
                frags[36]++;
                alogpfrag[i] = 36;
            }
        }
        else {
            if (haveCsX) {
                frags[18]++;
                alogpfrag[i] = 18;
            }
            else {
                frags[16]++;
                alogpfrag[i] = 16;
            }
        }
    }

    private void calcGroup017_019_020_038_to_041(int i) {

        IAtom ai = atomContainer.getAtom(i);

        if (!fragment[i].equals("SdssC") &&
            !fragment[i].equals("SdaaC")) {
            return;
        }

        List<IAtom> ca = atomContainer.getConnectedAtomsList(ai);

        int rCount = 0;
        int xCount = 0;
        boolean haveCdX = false;
        int aromaticCount = 0;

        for (IBond bond : ai.bonds()) {
            IAtom nbor = bond.getOther(ai);
            if (bond.getOrder() == IBond.Order.SINGLE) {
                if (nbor.getAtomicNumber() == 6) {
                    rCount++;
                    if (nbor.isAromatic())
                        aromaticCount++;
                }
                else
                    xCount++;
            }
            else if (bond.getOrder() == IBond.Order.DOUBLE) {
                if (isHetero(nbor)) {
                    haveCdX = true;
                }
            }
        }

        if (haveCdX) {
            if (aromaticCount >= 1) {
                // Ar-C(=X)-R 39
                // Ar-C(=X)-X 40
                if (xCount == 1) {
                    frags[40]++;
                    alogpfrag[i] = 40;
                } else {
                    frags[39]++;
                    alogpfrag[i] = 39;
                }
            }
            else if (aromaticCount == 0) {
                if (rCount == 1 && xCount == 1) {
                    frags[40]++;
                    alogpfrag[i] = 40;
                }
                else if (rCount == 0 && xCount == 2) {
                    frags[41]++;
                    alogpfrag[i] = 41;
                }
                else {
                    frags[38]++;
                    alogpfrag[i] = 38;
                }
            }
        }
        else {
            if (rCount == 2 && xCount == 0) {
                frags[17]++;
                alogpfrag[i] = 17;
            }
            else if (rCount == 1 && xCount == 1) {
                frags[19]++;
                alogpfrag[i] = 19;
            }
            else if (rCount == 0 && xCount == 2) {
                frags[20]++;
                alogpfrag[i] = 20;
            }
        }

    }

    private void calcGroup021_to_023_040(int i) {

        List<IAtom> nbors = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom       ai    = atomContainer.getAtom(i);

        if (fragment[i].equals("StCH")) {
            frags[21]++;
            alogpfrag[i] = 21;
            int htype = getHAtomType(atomContainer.getAtom(i), nbors);
            frags[htype]++;
            for (IAtom nbor : nbors) {
                if (nbor.getAtomicNumber() == 1)
                    alogpfrag[nbor.getIndex()] = htype;
            }
        }
        else if (fragment[i].equals("SddC")) {
            if (((IAtom) nbors.get(0)).getSymbol().equals("C") && ((IAtom) nbors.get(1)).getSymbol().equals("C")) {// R==C==R
                frags[22]++;
                alogpfrag[i] = 22;
            }
            else if (!((IAtom) nbors.get(0)).getSymbol().equals("C") && !((IAtom) nbors.get(1)).getSymbol().equals("C")) {// X==C==X
                frags[40]++;
                alogpfrag[i] = 40;
            }
        }
        else if (fragment[i].equals("StsC")) {

            boolean haveCtX = false;
            boolean haveCsX = false;

            for (int j = 0; j <= nbors.size() - 1; j++) {
                if (atomContainer.getBond(ai, ((IAtom) nbors.get(j))).getOrder() == IBond.Order.SINGLE) {
                    if (!((IAtom) nbors.get(j)).getSymbol().equals("C")) {
                        haveCsX = true;
                    }
                } else if (atomContainer.getBond(ai, ((IAtom) nbors.get(j))).getOrder() == IBond.Order.TRIPLE) {
                    if (!((IAtom) nbors.get(j)).getSymbol().equals("C")) {
                        haveCtX = true;
                    }
                }
            }

            if (haveCtX && !haveCsX) {
                frags[40]++;
                alogpfrag[i] = 40;
            } else if (haveCsX) {// #C-X
                frags[23]++;
                alogpfrag[i] = 23;
            } else if (!haveCsX) { // #C-R
                frags[22]++;
                alogpfrag[i] = 22;
            }
        }
    }

    private void calcGroup024_027_030_033_042(int i) {
        // 24: C in R--CH--R
        // 27: C in R--CH--X
        // 30: C in X--CH--X
        // 33: C in R--CH...X
        // 42: C in X--CH...X

        if (!fragment[i].equals("SaaCH")) return;

        IAtom atom = atomContainer.getAtom(i);
        List<IAtom> nbors = atomContainer.getConnectedAtomsList(atom);
        int         htype = getHAtomType(atom, nbors);
        frags[htype]++;
        IAtom ca0;
        IAtom ca1;
        // determine which neighbor is the H atom
        if (atom.getImplicitHydrogenCount() == 1) {
            ca0 = nbors.get(0);
            ca1 = nbors.get(1);
        } else if (nbors.get(0).getAtomicNumber() == 1) {
            alogpfrag[atomContainer.indexOf(nbors.get(0))] = htype;
            ca0 = nbors.get(1);
            ca1 = nbors.get(2);
        } else if (nbors.get(1).getAtomicNumber() == 1) {
            ca0 = nbors.get(0);
            alogpfrag[atomContainer.indexOf(nbors.get(1))] = htype;
            ca1 = nbors.get(2);
        } else if (nbors.get(2).getAtomicNumber() == 1) {
            ca0 = nbors.get(0);
            ca1 = nbors.get(1);
            alogpfrag[atomContainer.indexOf(nbors.get(2))] = htype;
        } else
            throw new IllegalStateException();

        if (ca0.getSymbol().equals("C") && ca1.getSymbol().equals("C")) {
            frags[24]++;
            alogpfrag[i] = 24;
            return;
        }

        // check if both hetero atoms have at least one double bond
        List bonds = atomContainer.getConnectedBondsList(ca0);
        boolean haveDouble1 = false;
        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond) bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                haveDouble1 = true;
                break;
            }
        }

        bonds = atomContainer.getConnectedBondsList(ca1);
        boolean haveDouble2 = false;
        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond) bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                haveDouble2 = true;
                break;
            }
        }

        if (!(ca0).getSymbol().equals("C") && !((IAtom) nbors.get(1)).getSymbol().equals("C")) {
            if (haveDouble1 && haveDouble2) { // X--CH--X
                frags[30]++;
                alogpfrag[i] = 30;
            } else { // X--CH...X
                frags[42]++;
                alogpfrag[i] = 42;
            }

        } else if (ca0.getSymbol().equals("C") && !ca1.getSymbol().equals("C")
                || (!ca0.getSymbol().equals("C") && ca1.getSymbol().equals("C"))) {

            if (haveDouble1 && haveDouble2) { // R--CH--X
                frags[27]++;
                alogpfrag[i] = 27;
            } else {// R--CH...X
                frags[33]++;
                alogpfrag[i] = 33;

            }
        }
    }

    private void calcGroup025_026_028_029_031_032_034_035_043_044(int i) {
        // 25: R--CR--R
        // 26: R--CX--R
        // 28: R--CR--X
        // 29: R--CX--X
        // 31: X--CR--X
        // 32: X--CX--X
        // 34: X--CR...X
        // 35: X--CX...X
        // 43: X--CR...X
        // 43: X--CX...X

        if (!fragment[i].equals("SsaaC") && !fragment[i].equals("SaaaC")) return;

        IAtom       atm = atomContainer.getAtom(i);
        List<IAtom> nbors = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));

        IAtom[] sameringatoms = new IAtom[2];
        IAtom nonringatom = atomContainer.getBuilder().newInstance(IAtom.class);

        int sameringatomscount = 0;
        for (int j = 0; j <= nbors.size() - 1; j++) {
            if (inSameAromaticRing(atomContainer, atm, ((IAtom) nbors.get(j)), rs)) {
                sameringatomscount++;
            }

        }

        if (sameringatomscount == 2) {
            int count = 0;
            for (int j = 0; j <= nbors.size() - 1; j++) {
                if (inSameAromaticRing(atomContainer, atm, ((IAtom) nbors.get(j)), rs)) {
                    sameringatoms[count] = (IAtom) nbors.get(j);
                    count++;
                } else {
                    nonringatom = (IAtom) nbors.get(j);
                }

            }
        } else { // sameringsatomscount==3
            // arbitrarily assign atoms: (no way to decide consistently)
            // but to match VEGA we choose to but hetero atoms in the ring
            Collections.sort(nbors, new Comparator<IAtom>() {
                @Override
                public int compare(IAtom a, IAtom b) {
                    return -Boolean.compare(isHetero(a), isHetero(b));
                }
            });
            sameringatoms[0] = (IAtom) nbors.get(0);
            sameringatoms[1] = (IAtom) nbors.get(1);
            nonringatom = (IAtom) nbors.get(2);
        }

        // check if both hetero atoms have at least one double bond
        List bonds = atomContainer.getConnectedBondsList(sameringatoms[0]);

        boolean haveDouble1 = false;

        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond) bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                haveDouble1 = true;
                break;
            }

        }

        bonds = atomContainer.getConnectedBondsList(sameringatoms[1]);

        boolean haveDouble2 = false;

        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond) bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                haveDouble2 = true;
                break;
            }

        }

        if (!sameringatoms[0].getSymbol().equals("C") && !sameringatoms[1].getSymbol().equals("C")) {
            if (haveDouble1 && haveDouble2) { // X--CR--X
                if (nonringatom.getSymbol().equals("C")) {
                    frags[31]++;
                    alogpfrag[i] = 31;
                } else { // X--CX--X
                    frags[32]++;
                    alogpfrag[i] = 32;
                }

            } else {

                if (nonringatom.getSymbol().equals("C")) { // X--CR..X
                    frags[43]++;
                    alogpfrag[i] = 43;

                } else { // X--CX...X
                    frags[44]++;
                    alogpfrag[i] = 44;
                }

            }
        } else if (sameringatoms[0].getSymbol().equals("C") && sameringatoms[1].getSymbol().equals("C")) {

            if (nonringatom.getSymbol().equals("C")) {// R--CR--R
                frags[25]++;
                alogpfrag[i] = 25;
            } else { // R--CX--R
                frags[26]++;
                alogpfrag[i] = 26;
            }

        } else if ((sameringatoms[0].getSymbol().equals("C") && !sameringatoms[1].getSymbol().equals("C"))
                || (!sameringatoms[0].getSymbol().equals("C") && sameringatoms[1].getSymbol().equals("C"))) {

            if (haveDouble1 && haveDouble2) { // R--CR--X
                if (nonringatom.getSymbol().equals("C")) {
                    frags[28]++;
                    alogpfrag[i] = 28;
                } else { // R--CX--X
                    frags[29]++;
                    alogpfrag[i] = 29;
                }

            } else {

                if (nonringatom.getSymbol().equals("C")) { // R--CR..X
                    frags[34]++;
                    alogpfrag[i] = 34;

                } else { // R--CX...X
                    frags[35]++;
                    alogpfrag[i] = 35;
                }
            }
        }
    }

    private boolean isPyrroleLikeHetero(IAtom atom) {
        if (!atom.isAromatic())
            return false;
        switch (atom.getAtomicNumber()) {
            case 7:
            case 15:
                if (atom.getBondCount() == 3 && atom.getFormalCharge() == 0)
                    return true;
                if (atom.getBondCount() == 2 && atom.getImplicitHydrogenCount() == 1)
                    return true;
                if (atom.getBondCount() == 2 && atom.getFormalCharge() == -1)
                    return true;
                return false;
            case 8:
            case 16:
                return true;
        }
        return false;
    }

    private int getHAtomType(IAtom atom, List connectedAtoms) {
        //ai is the atom connected to a H atoms.
        //ai environment determines what is the H atom type
        //This procedure is applied only for carbons
        //i.e. H atom type 50 is never returned

        List<IAtom> ca;
        if (connectedAtoms == null)
            ca = atomContainer.getConnectedAtomsList(atom);
        else
            ca = connectedAtoms;

        IAtomType.Hybridization hyb;

        int ndoub = 0;
        int ntrip = 0;
        int oxNum = 0;
        int xCount = 0;
        boolean hasConjHetereo = false;

        for (IBond bond : atom.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE)
                ndoub++;
            else if (bond.getOrder() == IBond.Order.TRIPLE)
                ntrip++;
            final IAtom nbor = bond.getOther(atom);
            if (isHetero(nbor)) {
                if (bond.isAromatic()) {
                    if (bond.getOrder() == IBond.Order.SINGLE) {
                        if (!isPyrroleLikeHetero(nbor) && !hasConjHetereo) {
                            oxNum += 2;
                            hasConjHetereo = true;
                        } else
                            oxNum++;
                    } else if (!hasConjHetereo) {
                        hasConjHetereo = true;
                        oxNum += 2;
                    } else {
                        oxNum++;
                    }
                } else
                    oxNum += bond.getOrder().numeric();
            }
            else if (nbor.getAtomicNumber() == 6) {
                for (IBond bond2 : nbor.bonds()) {
                    IAtom nbor2 = bond2.getOther(nbor);
                    if (isHetero(nbor2))
                        xCount++;
                }
            }
        }

        if (ndoub == 0 && ntrip == 0)
            hyb = IAtomType.Hybridization.SP3;
        else if (ndoub == 1 && ntrip == 0)
            hyb = IAtomType.Hybridization.SP2;
        else if (ndoub == 2 || ntrip == 1)
            hyb = IAtomType.Hybridization.SP1;
        else
            return 0; // unknown

        // first check for alpha carbon:
        // -C=X, -C#X and -C:X
        boolean isAlphaC = false;
        if (atom.getAtomicNumber() == 6 && hyb == IAtomType.Hybridization.SP3) {
            for (IBond bond : atom.bonds()) {
                IAtom nbor = bond.getOther(atom);
                if (isHetero(nbor)) {
                    isAlphaC = false;
                    break;
                } else if (nbor.getAtomicNumber() == 6) {
                    int numDoubX = 0, numTripX = 0, numAromX = 0;
                    for (IBond bond2 : nbor.bonds()) {
                        IAtom nbor2 = bond2.getOther(nbor);
                        if (isHetero(nbor2)) {
                            switch (bond2.getOrder()) {
                                case SINGLE:
                                    if (bond2.isAromatic())
                                        numAromX++;
                                    break;
                                case DOUBLE:
                                    if (bond2.isAromatic())
                                        numAromX++;
                                    else
                                        numDoubX++;
                                    break;
                                case TRIPLE:
                                    numTripX++;
                                    break;
                            }
                        }
                    }
                    if (numDoubX + numTripX + numAromX == 1)
                        isAlphaC = true;
                }
            }
        }

        if (isAlphaC)
            return 51;
        switch (hyb) {
            case SP1:
                if (oxNum == 0)
                    return 48;
                if (oxNum == 1)
                    return 49;
                break;
            case SP2:
                if (oxNum == 0)
                    return 47;
                if (oxNum == 1)
                    return 48;
                if (oxNum == 2 || oxNum == 3)
                    return 49;
                break;
            case SP3:
                if (oxNum == 0) {
                    if (xCount == 0)
                        return 46;
                    else if (xCount == 1)
                        return 52;
                    else if (xCount == 2)
                        return 53;
                    else if (xCount == 3)
                        return 54;
                    else if (xCount == 4)
                        return 55;
                }
                if (oxNum == 1)
                    return 47;
                if (oxNum == 2)
                    return 48;
                if (oxNum == 3)
                    return 49;
                break;
        }

        return 0;
    }

    private void calcGroup056_57(int i) {
        // 56: O in =O
        // 57: O in phenol, enol, and carboxyl
        // enol : compound containing a hydroxyl group bonded to a carbon atom
        // that in turn forms a double bond with another carbon atom.
        // enol = HO-C=C-
        // carboxyl= HO-C(=O)-

        if (!fragment[i].equals("SsOH")) return;
        IAtom       atm  = atomContainer.getAtom(i);
        int htype = 50; //H atom attached to a hetero atom
        frags[htype]++;

        IAtom ca0 = null;
        for (IBond bond : atm.bonds()) {
            IAtom nbor = bond.getOther(atm);
            if (nbor.getAtomicNumber() == 6)
                ca0 = nbor;
            else if (nbor.getAtomicNumber() == 1)
                alogpfrag[nbor.getIndex()] = htype;
        }


        if (ca0 != null) {
        if (ca0.isAromatic()) { // phenol
            frags[57]++;
            alogpfrag[i] = 57;
            return;
        }

            // Check for C=COH, and C(OH)=O
            for (IBond bond : ca0.bonds()) {
                IAtom nbor2 = bond.getOther(ca0);
                if (nbor2 == atm)
                    continue;
                if (bond.getOrder() == IBond.Order.DOUBLE &&
                    (nbor2.getAtomicNumber() == 6 || nbor2.getAtomicNumber() == 8)) {
                frags[57]++;
                alogpfrag[i] = 57;
                return;
            }
        }
        }

        frags[56]++;
        alogpfrag[i] = 56;
    }

    private void calcGroup058_61(int i) {
        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));

        // 58: O in =O
        // 61: --O in nitro, N-oxides
        // 62: O in O-
        if (fragment[i].equals("SsOm")) {
            IAtom ca0 = (IAtom) ca.get(0);
            if (ca0.getSymbol().equals("N") && ca0.getFormalCharge() == 1) {
                frags[61]++;
                alogpfrag[i] = 61;
            } else {
                frags[62]++;
                alogpfrag[i] = 62;
            }

        } else if (fragment[i].equals("SdO")) {
            IAtom ca0 = (IAtom) ca.get(0);
            if (ca0.getSymbol().equals("N") && ca0.getFormalCharge() == 1) {
                frags[61]++;
                alogpfrag[i] = 61;
            } else {
                frags[58]++;
                alogpfrag[i] = 58;
            }
        }

    }

    private void calcGroup059_060_063(int i) {
        // O in Al-O-Ar, Ar2O, R...O...R, ROC=X
        // ... = aromatic single bonds
        if (!fragment[i].equals("SssO") && !fragment[i].equals("SaaO")) return;

        // Al-O-Ar, Ar2O
        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom) ca.get(0);
        IAtom ca1 = (IAtom) ca.get(1);

        if (fragment[i].equals("SssO")) {
            if (ca0.getFlag(CDKConstants.ISAROMATIC) || ca1.getFlag(CDKConstants.ISAROMATIC)) {
                frags[60]++;
                alogpfrag[i] = 60;

            } else {

                for (int j = 0; j <= ca.size() - 1; j++) {
                     if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                         List ca2 = atomContainer.getConnectedAtomsList(((IAtom) ca.get(j)));
                         for (int k = 0; k <= ca2.size() - 1; k++) {
                             if (atomContainer.getBond(((IAtom) ca.get(j)), (IAtom) ca2.get(k)).getOrder() == IBond.Order.DOUBLE) {
                                 if (!((IAtom) ca2.get(k)).getSymbol().equals("C")) {
                                     frags[60]++;
                                     alogpfrag[i] = 60;
                                     return;
                                 }
                             }
                         }
                     }
                }

                if (ca0.getSymbol().equals("O") || ca1.getSymbol().equals("O")) {
                    frags[63]++;
                    alogpfrag[i] = 63;
                } else {
                    frags[59]++;
                    alogpfrag[i] = 59;

                }

            }
        } else if (fragment[i].equals("SaaO")) {
            frags[60]++;
            alogpfrag[i] = 60;
        }

    }

    private void calcGroup066_to_079(int i) {
        int   nAr  = 0;
        int   nAl  = 0;
        IAtom atom = atomContainer.getAtom(i);
        IAtom ai   = atom;
        if (!ai.getSymbol().equals("N")) return;
        List<IAtom> nbors = atomContainer.getConnectedAtomsList(atom);

        int htype = 50; //H atom attached to a hetero atom
        for (IAtom nbor : nbors)
            if (nbor.getAtomicNumber() == 1) {
                alogpfrag[nbor.getIndex()] = htype;
                frags[htype]++;
            }
        frags[htype] += atom.getImplicitHydrogenCount();

        for (int j = 0; j <= nbors.size() - 1; j++) {
            if (((IAtom) nbors.get(j)).getSymbol().equals("H")) continue;
            if (((IAtom) nbors.get(j)).getFlag(CDKConstants.ISAROMATIC))
                nAr++;
            else
                nAl++;
        }

        if (fragment[i].equals("SsssN") ||
            fragment[i].equals("SssNH") ||
            fragment[i].equals("SsNH2")) {
            // first check if have RC(=O)N or NX=X
            for (int j = 0; j <= nbors.size() - 1; j++) {
                if (nbors.get(j).getAtomicNumber() == 1)
                    continue;
                List ca2 = atomContainer.getConnectedAtomsList((IAtom) nbors.get(j));
                for (int k = 0; k <= ca2.size() - 1; k++) {
                    IAtom ca2k = (IAtom) ca2.get(k);
                    if (atomContainer.indexOf(ca2k) != i) {
                        if (!ca2k.getSymbol().equals("C")) {
                            if (!ca2k.getFlag(CDKConstants.ISAROMATIC)
                                && !((IAtom) nbors.get(j)).getFlag(CDKConstants.ISAROMATIC)
                                && !ai.getFlag(CDKConstants.ISAROMATIC)) {
                                if (atomContainer.getBond(((IAtom) nbors.get(j)), ca2k).getOrder() == IBond.Order.DOUBLE) {
                                    frags[72]++;
                                    alogpfrag[i] = 72;
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (fragment[i].equals("SsNH2")) {
            IAtom ca0 = null;
            //Find which neigbpur is not the hydrogen atom
            for (IAtom nbor : nbors) {
                if (nbor.getAtomicNumber() != 1) {
                    ca0 = nbor;
                    break;
                }
            }
            if (ca0.getFlag(CDKConstants.ISAROMATIC) || !ca0.getSymbol().equals("C")) {
                frags[69]++;
                alogpfrag[i] = 69;
            } else {
                frags[66]++;
                alogpfrag[i] = 66;
            }
        } else if (fragment[i].equals("SaaNH") ||
                   fragment[i].equals("SsaaN") ||
                   fragment[i].equals("SaaaN") ||
                   fragment[i].equals("SaaNm")) { // R...NH...R
            frags[73]++;
            alogpfrag[i] = 73;
        } else if (fragment[i].equals("SssNH")) {
            if (nAr == 2 && nAl == 0) { // Ar2NH
                frags[73]++;
                alogpfrag[i] = 73;
            } else if (nAr == 1 && nAl == 1) { // Ar-NH-Al
                frags[70]++;
                alogpfrag[i] = 70;

            } else if (nAr == 0 && nAl == 2) { // Al2NH
                frags[67]++;
                alogpfrag[i] = 67;
            }
        } else if (fragment[i].equals("SsssN")) {
            if ((nAr == 3 && nAl == 0) || (nAr == 2 && nAl == 1)) { // Ar3N &
                // Ar2NAl
                frags[73]++;
                alogpfrag[i] = 73;
            } else if (nAr == 1 && nAl == 2) {
                frags[71]++;
                alogpfrag[i] = 71;
            } else if (nAr == 0 && nAl == 3) {
                frags[68]++;
                alogpfrag[i] = 68;
            }
        } else if (fragment[i].equals("SaaN")) {
            frags[75]++;
            alogpfrag[i] = 75;
        } else if (fragment[i].equals("SdssNp") ||
                   fragment[i].equals("SddsN")) {
            int     haveSsOm = 0;
            int     haveSdO  = 0;
            boolean ar       = false;

            for (int j = 0; j <= nbors.size() - 1; j++) {
                if (fragment[atomContainer.indexOf(((IAtom) nbors.get(j)))].equals("SsOm")) {
                    haveSsOm++;
                } else if (fragment[atomContainer.indexOf(((IAtom) nbors.get(j)))].equals("SdO")) {
                    haveSdO++;
                } else {
                    if (((IAtom) nbors.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                        ar = true;
                    }
                }
            }

            boolean isNitro = haveSdO == 2 || (haveSsOm >= 1 && haveSdO >= 1);

            if (isNitro && ar) {
                frags[76]++;
                alogpfrag[i] = 76;
            } else if (isNitro && !ar) {
                frags[77]++;
                alogpfrag[i] = 77;
            } else {
                frags[79]++;
                alogpfrag[i] = 79;
            }

        } else if (fragment[i].equals("StN")) {
            IAtom ca0 = (IAtom) nbors.get(0);
            if (ca0.getSymbol().equals("C")) { // R#N
                frags[74]++;
                alogpfrag[i] = 74;
            }
        } else if (fragment[i].equals("SdNH") || fragment[i].equals("SdsN")) {
            // test for RO-NO
            if (fragment[i].equals("SdsN")) {
                IAtom ca0 = nbors.get(0);
                IAtom ca1 = nbors.get(1);
                if (ca0.getSymbol().equals("O") && ca1.getSymbol().equals("O")) {
                    frags[76]++;
                    alogpfrag[i] = 76;
                    return;
                }
            }
            boolean flag1 = false;
            boolean flag2 = false;

            for (int j = 0; j <= nbors.size() - 1; j++) {
                if (((IAtom) nbors.get(j)).getSymbol().equals("H")) continue;
                if (atomContainer.getBond(ai, ((IAtom) nbors.get(j))).getOrder() == IBond.Order.DOUBLE) {
                    if (((IAtom) nbors.get(j)).getSymbol().equals("C")) {
                        frags[74]++;
                        alogpfrag[i] = 74;
                        return;
                    } else {
                        flag1 = true;
                    }
                } else {
                    if (!((IAtom) nbors.get(j)).getSymbol().equals("C")
                            || ((IAtom) nbors.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                        flag2 = true;
                    }
                }

                if (flag1 && flag2) { // X-N=X or Ar-N=X
                    frags[78]++;
                    alogpfrag[i] = 78;
                    return;
                } else {
                    //logger.debug("missing group: R-N=X");
                }
            }

        } else if (fragment[i].indexOf('p') > -1) {
            frags[79]++;
            alogpfrag[i] = 79;
        }

        // TODO add code for R--N(--R)--O
        // first need to have program correctly read in structures with this
        // fragment (pyridine-n-oxides)
    }

    private void calcGroup081_to_085(int i) {

        if (!fragment[i].equals("SsF")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom) ca.get(0);

        List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond) bonds.get(j);
            if (bj.getOrder() == IBond.Order.DOUBLE) {
                doublebondcount++;
            }

            else if (bj.getOrder() == IBond.Order.TRIPLE) {
                triplebondcount++;
            }

        }

        if (doublebondcount == 0 && triplebondcount == 0) {
            hybrid = "sp3";
        } else if (doublebondcount == 1) {
            hybrid = "sp2";
        } else if (doublebondcount == 2 || triplebondcount == 1) {
            hybrid = "sp";
        }

        List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int oxNum = 0;
        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom) ca2.get(j);
            if (isHetero(ca2j))
                oxNum += atomContainer.getBond(ca0, ca2j).getOrder().numeric();
        }

        if (ca0.getAtomicNumber() == 6) {
            if (hybrid.equals("sp3") && oxNum == 1) {
                frags[81]++;
                alogpfrag[i] = 81;
            } else if (hybrid.equals("sp3") && oxNum == 2) {
                frags[82]++;
                alogpfrag[i] = 82;
            } else if (hybrid.equals("sp3") && oxNum == 3) {
                frags[83]++;
                alogpfrag[i] = 83;
            } else if (hybrid.equals("sp2") && oxNum == 1) {
                frags[84]++;
                alogpfrag[i] = 84;
            } else if ((hybrid.equals("sp2") && oxNum > 1) || (hybrid.equals("sp") && oxNum >= 1)
                       || (hybrid.equals("sp3") && oxNum == 4)) {
                frags[85]++;
                alogpfrag[i] = 85;
            }
        } else {
            frags[85]++;
            alogpfrag[i] = 85;
        }

    }

    private void calcGroup086_to_090(int i) {

        if (!fragment[i].equals("SsCl")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom) ca.get(0);

        List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond) bonds.get(j);
            if (bj.getOrder() == IBond.Order.DOUBLE) {
                doublebondcount++;
            }

            else if (bj.getOrder() == IBond.Order.TRIPLE) {
                triplebondcount++;
            }

        }

        if (doublebondcount == 0 && triplebondcount == 0) {
            hybrid = "sp3";
        } else if (doublebondcount == 1) {
            hybrid = "sp2";
        } else if (doublebondcount == 2 || triplebondcount == 1) {
            hybrid = "sp";
        }

        List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int oxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom) ca2.get(j);
            String s = ca2j.getSymbol();

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(s) > 1) {
                // // F,O,Cl,Br,N
                oxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }
        }

        if (ca0.getAtomicNumber() == 6) {
            if (hybrid.equals("sp3") && oxNum == 1) {
                frags[86]++;
                alogpfrag[i] = 86;
            } else if (hybrid.equals("sp3") && oxNum == 2) {
                frags[87]++;
                alogpfrag[i] = 87;
            } else if (hybrid.equals("sp3") && oxNum == 3) {
                frags[88]++;
                alogpfrag[i] = 88;
            } else if (hybrid.equals("sp2") && oxNum == 1) {
                frags[89]++;
                alogpfrag[i] = 89;
            } else if ((hybrid.equals("sp2") && oxNum > 1) || (hybrid.equals("sp") && oxNum >= 1)
                       || (hybrid.equals("sp3") && oxNum == 4)) {
                frags[90]++;
                alogpfrag[i] = 90;
            }
        } else {
            frags[90]++;
            alogpfrag[i] = 90;
        }

    }

    private void calcGroup091_to_095(int i) {

        if (!fragment[i].equals("SsBr")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom) ca.get(0);

        List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond) bonds.get(j);
            if (bj.getOrder() == IBond.Order.DOUBLE) {
                doublebondcount++;
            }

            if (bj.getOrder() == IBond.Order.TRIPLE) {
                triplebondcount++;
            }

        }

        if (doublebondcount == 0 && triplebondcount == 0) {
            hybrid = "sp3";
        } else if (doublebondcount == 1) {
            hybrid = "sp2";
        } else if (doublebondcount == 2 || triplebondcount == 1) {
            hybrid = "sp";
        }

        List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int oxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom) ca2.get(j);

            // // F,O,Cl,Br,N

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(ca2j.getSymbol()) > 1) {
                oxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }

        }

        if (ca0.getAtomicNumber() == 6) {
            if (hybrid.equals("sp3") && oxNum == 1) {
                frags[91]++;
                alogpfrag[i] = 91;
            } else if (hybrid.equals("sp3") && oxNum == 2) {
                frags[92]++;
                alogpfrag[i] = 92;
            } else if (hybrid.equals("sp3") && oxNum == 3) {
                frags[93]++;
                alogpfrag[i] = 93;
            } else if (hybrid.equals("sp2") && oxNum == 1) {
                frags[94]++;
                alogpfrag[i] = 94;
            } else if ((hybrid.equals("sp2") && oxNum > 1) || (hybrid.equals("sp") && oxNum >= 1)
                       || (hybrid.equals("sp3") && oxNum == 4)) {
                frags[95]++;
                alogpfrag[i] = 95;
            }
        } else {
            frags[95]++;
            alogpfrag[i] = 95;
        }

    }

    private void calcGroup096_to_100(int i) {

        if (!fragment[i].equals("SsI")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom) ca.get(0);

        List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond) bonds.get(j);
            if (bj.getOrder() == IBond.Order.DOUBLE) {
                doublebondcount++;
            }

            else if (bj.getOrder() == IBond.Order.TRIPLE) {
                triplebondcount++;
            }

        }

        if (doublebondcount == 0 && triplebondcount == 0) {
            hybrid = "sp3";
        } else if (doublebondcount == 1) {
            hybrid = "sp2";
        } else if (doublebondcount == 2 || triplebondcount == 1) {
            hybrid = "sp";
        }

        List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int oxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom) ca2.get(j);

            // // F,O,Cl,Br,N

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(ca2j.getSymbol()) > 1) {
                oxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }

        }

        if (ca0.getAtomicNumber() == 6) {
            if (hybrid.equals("sp3") && oxNum == 1) {
                frags[96]++;
                alogpfrag[i] = 96;
            } else if (hybrid.equals("sp3") && oxNum == 2) {
                frags[97]++;
                alogpfrag[i] = 97;
            } else if (hybrid.equals("sp3") && oxNum == 3) {
                frags[98]++;
                alogpfrag[i] = 98;
            } else if (hybrid.equals("sp2") && oxNum == 1) {
                frags[99]++;
                alogpfrag[i] = 99;
            } else if ((hybrid.equals("sp2") && oxNum > 1) || (hybrid.equals("sp") && oxNum >= 1)
                       || (hybrid.equals("sp3") && oxNum == 4)) {
                frags[100]++;
                alogpfrag[i] = 100;
            }
        } else {
            frags[100]++;
            alogpfrag[i] = 100;
        }

    }

    private void calcGroup101_to_104(int i) {
        IAtom ai = atomContainer.getAtom(i);
        String s = ai.getSymbol();

        if (ai.getFormalCharge() == -1 ||
            (ai.getFormalCharge() == 0 && isBondedToHydrogenOnly(ai))) {
            if (s.equals("F")) {
                frags[101]++;
                alogpfrag[i] = 101;
            } else if (s.equals("Cl")) {
                frags[102]++;
                alogpfrag[i] = 102;
            } else if (s.equals("Br")) {
                frags[103]++;
                alogpfrag[i] = 103;
            } else if (s.equals("I")) {
                frags[104]++;
                alogpfrag[i] = 104;
            }

        }

    }

    private boolean isBondedToHydrogenOnly(IAtom ai) {
        return ai.getBondCount() == 0 && ai.getImplicitHydrogenCount() == 1 ||
               ai.getBondCount() == 1 && ai.bonds().iterator().next().getOther(ai).getAtomicNumber() == 1;
    }

    private void calcGroup106(int i) {
        // S in SH
        if (fragment[i].equals("SsSH")) {
            frags[106]++;
            alogpfrag[i] = 106;
            int htype = 50;
            frags[htype]++;
            IAtom atom = atomContainer.getAtom(i);
            for (IBond bond : atom.bonds()) {
                IAtom nbor = bond.getOther(atom);
                if (nbor.getAtomicNumber() == 1)
                    alogpfrag[nbor.getIndex()] = htype;
            }
        }
    }

    private void calcGroup107(int i) {
        // S in R2S, RS-SR
        // R = any group linked through C
        // if (!Fragment[i].equals("SssS")) return;

        // In ALOGP, for malathion PSC is consider to have group 107 (even
        // though has P instead of R)

        // for lack of fragment, use this fragment for SaaS

        if (fragment[i].equals("SssS") || fragment[i].equals("SaaS")) {
            frags[107]++;
            alogpfrag[i] = 107;
        }
        // IAtom [] ca=atomContainer.getConnectedAtoms(atomContainer.getAtomAt(i));
        //
        // if ((ca[0].getSymbol().equals("C") && ca[1].getSymbol().equals("C"))
        // ||
        // (ca[0].getSymbol().equals("C") && ca[1].getSymbol().equals("S")) ||
        // (ca[0].getSymbol().equals("S") && ca[1].getSymbol().equals("C"))) {
        // frags[107]++;
        // alogpfrag[i]=107;
        // }
    }

    private void calcGroup108(int i) {
        // S in R=S
        // In ALOGP, for malathion P=S is consider to have group 108 (even
        // though has P instead of R)
        if (fragment[i].equals("SdS")) {
            frags[108]++;
            alogpfrag[i] = 108;
        }
    }

    private void calcGroup109(int i) {
        // for now S in O-S(=O)-O is assigned to this group
        // (it doesn't check which atoms are singly bonded to S
        if (!fragment[i].equals("SdssS")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int sdOCount = 0;
        int ssCCount = 0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    ssCCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("O")) {
                    sdOCount++;
                }
            }
        }
        if (sdOCount == 1) { // for now dont check if ssCCount==2
            frags[109]++;
            alogpfrag[i] = 109;
        }
    }

    private void calcGroup110(int i) {
        if (!fragment[i].equals("SddssS")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int sdOCount = 0;
        int ssCCount = 0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    ssCCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("O")) {
                    sdOCount++;
                }
            }
        }
        if (sdOCount == 2) { // for now dont check if ssCCount==2
            frags[110]++;
            alogpfrag[i] = 110;
        }

    }

    private void calcGroup111(int i) {
        if (fragment[i].equals("SssssSi")) {
            frags[111]++;
            alogpfrag[i] = 111;
        }
    }

    private void calcGroup112(int i) {
        if (fragment[i].equals("SsssB") ||
            fragment[i].equals("SssBm")) {
            frags[112]++;
            alogpfrag[i] = 112;
        }
    }

    private void calcGroup115(int i) {
        if (fragment[i].equals("SssssPp")) {
            frags[115]++;
            alogpfrag[i] = 115;
        }
    }

    private void calcGroup116_117_120(int i) {

        // S in R=S

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);

        int xCount = 0;
        int rCount = 0;
        boolean pdX = false;

        if (!fragment[i].equals("SdsssP")) return;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    rCount++;
                } else {
                    xCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (!((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    pdX = true;
                }
            }
        }

        if (pdX) {
            if (rCount == 3) {
                frags[116]++;
                alogpfrag[i] = 116;
            } else if (xCount == 3) {
                frags[117]++;
                alogpfrag[i] = 117;
            } else if (xCount == 2 && rCount == 1) {
                frags[120]++;
                alogpfrag[i] = 120;
            }
        }

    }

    private void calcGroup118_119(int i) {
        if (!fragment[i].equals("SsssP")) return;

        List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int xCount = 0;
        int rCount = 0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom) ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom) ca.get(j)).getSymbol().equals("C")) {
                    rCount++;
                } else {
                    xCount++;
                }
            }
        }

        if (xCount == 3) {
            frags[118]++;
            alogpfrag[i] = 118;
        } else if (rCount == 3) {
            frags[119]++;
            alogpfrag[i] = 119;
        }

    }

    private boolean inSameAromaticRing(IAtomContainer atomContainer, IAtom atom1, IAtom atom2, IRingSet rs) {
        boolean sameRing = false;

        for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {
            IRing r = (IRing) rs.getAtomContainer(i);

            if (!r.getFlag(CDKConstants.ISAROMATIC)) continue;

            // ArrayList al=new ArrayList();

            boolean haveOne = false;
            boolean haveTwo = false;

            for (int j = 0; j <= r.getAtomCount() - 1; j++) {
                if (atomContainer.indexOf(r.getAtom(j)) == atomContainer.indexOf(atom1)) haveOne = true;
                if (atomContainer.indexOf(r.getAtom(j)) == atomContainer.indexOf(atom2)) haveTwo = true;
            }

            if (haveOne && haveTwo) {
                sameRing = true;
                return sameRing;
            }

        } // end ring for loop

        return sameRing;
    }

    /**
     * The AlogP descriptor.
     *
     * TODO Ideally we should explicit H addition should be cached
     *
     * @param atomContainer the molecule to calculate on
     * @return the result of the calculation
     */
    @Override
    public DescriptorValue calculate(IAtomContainer container) {
        IRingSet rs;
        try {
            AllRingsFinder arf = new AllRingsFinder();
            rs = arf.findAllRings(container);
        } catch (Exception e) {
            return getDummyDescriptorValue(new CDKException("Could not find all rings: " + e.getMessage()));
        }

        String[] fragment = new String[container.getAtomCount()];
        EStateAtomTypeMatcher eStateMatcher = new EStateAtomTypeMatcher();
        eStateMatcher.setRingSet(rs);

        for (IAtomContainer ring : rs.atomContainers()) {
            boolean arom = true;
            for (IBond bond : ring.bonds()) {
                if (!bond.isAromatic()) {
                    arom = false;
                    break;
                }
            }
            ring.setFlag(CDKConstants.ISAROMATIC, arom);
        }

        for (int i = 0; i < container.getAtomCount(); i++) {
            IAtomType atomType = eStateMatcher.findMatchingAtomType(container, container.getAtom(i));
            if (atomType == null) {
                fragment[i] = null;
            } else {
                fragment[i] = atomType.getAtomTypeName();
            }
        }

        double[] ret = new double[0];
        try {
            ret = calculate(container, fragment, rs);
        } catch (CDKException e) {
            e.printStackTrace();
            return getDummyDescriptorValue(new CDKException(e.getMessage()));
        }

        DoubleArrayResult results = new DoubleArrayResult();
        results.add(ret[0]);
        results.add(ret[1]);
        results.add(ret[2]);

        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), results,
                getDescriptorNames());
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult results = new DoubleArrayResult();
        results.add(Double.NaN);
        results.add(Double.NaN);
        results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), results,
                getDescriptorNames(), e);
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     *
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @Override
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(3);
    }

    @Override
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification("http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ALOGP",
                this.getClass().getName(), "The Chemistry Development Kit");
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public Object getParameterType(String name) {
        return null;
    }

    @Override
    public void setParameters(Object[] params) throws CDKException {}

    @Override
    public Object[] getParameters() {
        return null;
    }

    @Override
    public String[] getDescriptorNames() {
        return STRINGS;
    }

}// end class


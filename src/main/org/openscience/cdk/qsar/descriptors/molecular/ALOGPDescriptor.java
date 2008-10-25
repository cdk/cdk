/* $RCSfile: $
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2007  Todd Martin (Environmental Protection Agency)
 * Copyright (C) 2007  Nikolay Kochev <nick@argon.acad.bg>
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
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.atomtype.EStateAtomTypeMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleArrayResultType;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.AtomicProperties;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

import java.lang.reflect.Method;

/**
 * This class calculates ALOGP (Ghose-Crippen LogKow) and the 
 * Ghose-Crippen molar refractivity @cdk.cite{GHOSE1986} @cdk.cite{GHOSE1987}.
 *
 * <b>Note</b> The code assumes that aromaticity has been detected before
 * evaluating this descriptor. The code also expects that the molecule
 * will have hydrogens explicitly set. For SD files, this is usually not
 * a problem since hydrogens are explicit. But for the case of molecules
 * obtained from SMILES, hydrogens must be made explicit.
 * 
 * <p>TODO: what should sub return if have missing fragment?
 * Just report sum for other fragments? Or report as -9999 and 
 * then dont use descriptor if have this  value for any 
 * chemicals in cluster?
 * 
 * <p>This descriptor uses these parameters:
 * <table border="1">
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
 * <li>AMR  - molar refractivity
 * </ol>
 * 
 * @author     Todd Martin
 * @cdk.module qsarmolecular
 * @cdk.svnrev  $Revision$
 * @cdk.set    qsar-descriptors
 * @cdk.keyword logP
 * @cdk.keyword lipophilicity
 * @cdk.keyword refractivity
 * @see org.openscience.cdk.tools.CDKHydrogenAdder
 * @see org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector
 */
@TestClass("org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptorTest")
public class ALOGPDescriptor implements IMolecularDescriptor {
    private LoggingTool logger;

    IAtomContainer atomContainer;
    IRingSet rs;
    String[] fragment; // estate fragments for each atom

    AtomicProperties ap; // needed to retrieve electronegativities

    int[] frags = new int[121]; // counts of each type of fragment in the molecule
    public int[] alogpfrag; // alogp fragments for each atom (used to see which atoms have missing fragments)
    final static double[] fragval = new double[121];// coefficients for alogp model
    static {
        // fragments for ALOGP from Ghose et al., 1998
        fragval[1] = -1.5603;
        fragval[2] = -1.012;
        fragval[3] = -0.6681;
        fragval[4] = -0.3698;
        fragval[5] = -1.788;
        fragval[6] = -1.2486;
        fragval[7] = -1.0305;
        fragval[8] = -0.6805;
        fragval[9] = -0.3858;
        fragval[10] = 0.7555;
        fragval[11] = -0.2849;
        fragval[12] = 0.02;
        fragval[13] = 0.7894;
        fragval[14] = 1.6422;
        fragval[15] = -0.7866;
        fragval[16] = -0.3962;
        fragval[17] = 0.0383;
        fragval[18] = -0.8051;
        fragval[19] = -0.2129;
        fragval[20] = 0.2432;
        fragval[21] = 0.4697;
        fragval[22] = 0.2952;
        fragval[23] = 0;
        fragval[24] = -0.3251;
        fragval[25] = 0.1492;
        fragval[26] = 0.1539;
        fragval[27] = 0.0005;
        fragval[28] = 0.2361;
        fragval[29] = 0.3514;
        fragval[30] = 0.1814;
        fragval[31] = 0.0901;
        fragval[32] = 0.5142;
        fragval[33] = -0.3723;
        fragval[34] = 0.2813;
        fragval[35] = 0.1191;
        fragval[36] = -0.132;
        fragval[37] = -0.0244;
        fragval[38] = -0.2405;
        fragval[39] = -0.0909;
        fragval[40] = -0.1002;
        fragval[41] = 0.4182;
        fragval[42] = -0.2147;
        fragval[43] = -0.0009;
        fragval[44] = 0.1388;
        fragval[45] = 0;
        fragval[46] = 0.7341;
        fragval[47] = 0.6301;
        fragval[48] = 0.518;
        fragval[49] = -0.0371;
        fragval[50] = -0.1036;
        fragval[51] = 0.5234;
        fragval[52] = 0.6666;
        fragval[53] = 0.5372;
        fragval[54] = 0.6338;
        fragval[55] = 0.362;
        fragval[56] = -0.3567;
        fragval[57] = -0.0127;
        fragval[58] = -0.0233;
        fragval[59] = -0.1541;
        fragval[60] = 0.0324;
        fragval[61] = 1.052;
        fragval[62] = -0.7941;
        fragval[63] = 0.4165;
        fragval[64] = 0.6601;
        fragval[65] = 0;
        fragval[66] = -0.5427;
        fragval[67] = -0.3168;
        fragval[68] = 0.0132;
        fragval[69] = -0.3883;
        fragval[70] = -0.0389;
        fragval[71] = 0.1087;
        fragval[72] = -0.5113;
        fragval[73] = 0.1259;
        fragval[74] = 0.1349;
        fragval[75] = -0.1624;
        fragval[76] = -2.0585;
        fragval[77] = -1.915;
        fragval[78] = 0.4208;
        fragval[79] = -1.4439;
        fragval[80] = 0;
        fragval[81] = 0.4797;
        fragval[82] = 0.2358;
        fragval[83] = 0.1029;
        fragval[84] = 0.3566;
        fragval[85] = 0.1988;
        fragval[86] = 0.7443;
        fragval[87] = 0.5337;
        fragval[88] = 0.2996;
        fragval[89] = 0.8155;
        fragval[90] = 0.4856;
        fragval[91] = 0.8888;
        fragval[92] = 0.7452;
        fragval[93] = 0.5034;
        fragval[94] = 0.8995;
        fragval[95] = 0.5946;
        fragval[96] = 1.4201;
        fragval[97] = 1.1472;
        fragval[98] = 0;
        fragval[99] = 0.7293;
        fragval[100] = 0.7173;
        fragval[101] = 0;
        fragval[102] = -2.6737;
        fragval[103] = -2.4178;
        fragval[104] = -3.1121;
        fragval[105] = 0;
        fragval[106] = 0.6146;
        fragval[107] = 0.5906;
        fragval[108] = 0.8758;
        fragval[109] = -0.4979;
        fragval[110] = -0.3786;
        fragval[111] = 1.5188;
        fragval[112] = 1.0255;
        fragval[113] = 0;
        fragval[114] = 0;
        fragval[115] = 0;
        fragval[116] = -0.9359;
        fragval[117] = -0.1726;
        fragval[118] = -0.7966;
        fragval[119] = 0.6705;
        fragval[120] = -0.4801;
    }

    final static double[] refracval = new double[121]; // coefficients for refractivity model
    static {
        // fragments for AMR from Viswanadhan et al., 1989
        refracval[1]=2.968;
        refracval[2]=2.9116;
        refracval[3]=2.8028;
        refracval[4]=2.6205;
        refracval[5]=3.015;
        refracval[6]=2.9244;
        refracval[7]=2.6329;
        refracval[8]=2.504;
        refracval[9]=2.377;
        refracval[10]=2.5559;
        refracval[11]=2.303;
        refracval[12]=2.3006;
        refracval[13]=2.9627;
        refracval[14]=2.3038;
        refracval[15]=3.2001;
        refracval[16]=4.2654;
        refracval[17]=3.9392;
        refracval[18]=3.6005;
        refracval[19]=4.487;
        refracval[20]=3.2001;
        refracval[21]=3.4825;
        refracval[22]=4.2817;
        refracval[23]=3.9556;
        refracval[24]=3.4491;
        refracval[25]=3.8821;
        refracval[26]=3.7593;
        refracval[27]=2.5009;
        refracval[28]=2.5;
        refracval[29]=3.0627;
        refracval[30]=2.5009;
        refracval[31]=0;
        refracval[32]=2.6632;
        refracval[33]=3.4671;
        refracval[34]=3.6842;
        refracval[35]=2.9372;
        refracval[36]=4.019;
        refracval[37]=4.777;
        refracval[38]=3.9031;
        refracval[39]=3.9964;
        refracval[40]=3.4986;
        refracval[41]=3.4997;
        refracval[42]=2.7784;
        refracval[43]=2.6267;
        refracval[44]=2.5;
        refracval[45]=0;
        refracval[46]=0.8447;
        refracval[47]=0.8939;
        refracval[48]=0.8005;
        refracval[49]=0.832;
        refracval[50]=0.8;
        refracval[51]=0.8188;
        refracval[52]=0.9215;
        refracval[53]=0.9769;
        refracval[54]=0.7701;
        refracval[55]=0;
        refracval[56]=1.7646;
        refracval[57]=1.4778;
        refracval[58]=1.4429;
        refracval[59]=1.6191;
        refracval[60]=1.3502;
        refracval[61]=1.945;
        refracval[62]=0;
        refracval[63]=0;
        refracval[64]=11.1366;
        refracval[65]=13.1149;
        refracval[66]=2.6221;
        refracval[67]=2.5;
        refracval[68]=2.898;
        refracval[69]=3.6841;
        refracval[70]=4.2808;
        refracval[71]=3.6189;
        refracval[72]=2.5;
        refracval[73]=2.7956;
        refracval[74]=2.7;
        refracval[75]=4.2063;
        refracval[76]=4.0184;
        refracval[77]=3.0009;
        refracval[78]=4.7142;
        refracval[79]=0;
        refracval[80]=0;
        refracval[81]=0.8725;
        refracval[82]=1.1837;
        refracval[83]=1.1573;
        refracval[84]=0.8001;
        refracval[85]=1.5013;
        refracval[86]=5.6156;
        refracval[87]=6.1022;
        refracval[88]=5.9921;
        refracval[89]=5.3885;
        refracval[90]=6.1363;
        refracval[91]=8.5991;
        refracval[92]=8.9188;
        refracval[93]=8.8006;
        refracval[94]=8.2065;
        refracval[95]=8.7352;
        refracval[96]=13.9462;
        refracval[97]=14.0792;
        refracval[98]=14.073;
        refracval[99]=12.9918;
        refracval[100]=13.3408;
        refracval[101]=0;
        refracval[102]=0;
        refracval[103]=0;
        refracval[104]=0;
        refracval[105]=0;
        refracval[106]=7.8916;
        refracval[107]=7.7935;
        refracval[108]=9.4338;
        refracval[109]=7.7223;
        refracval[110]=5.7558;
        refracval[111]=0;
        refracval[112]=0;
        refracval[113]=0;
        refracval[114]=0;
        refracval[115]=0;
        refracval[116]=5.5306;
        refracval[117]=5.5152;
        refracval[118]=6.836;
        refracval[119]=10.0101;
        refracval[120]=5.2806;
    }
    
    String UnassignedAtoms="";

    double ALOGP = 0.0;
    double AMR = 0.0;
    double ALOGP2 = 0.0;
    private static final String[] strings = new String[] {"ALogP", "ALogp2", "AMR"};


    public ALOGPDescriptor() throws CDKException {
        logger = new LoggingTool(this);

        try {
            ap = AtomicProperties.getInstance();
        } catch (Exception e) {
            logger.debug("Problem in accessing atomic properties. Can't calculate");
            throw new CDKException("Problem in accessing atomic properties. Can't calculate");
        }
    }


    private void findUnassignedAtoms() {
        UnassignedAtoms="";

        for (int i = 0; i <= atomContainer.getAtomCount() - 1; i++) {
            if (alogpfrag[i]==0) UnassignedAtoms+=(i+1)+"("+fragment[i]+"),";
        }
    }




    private double[] calculate(IAtomContainer atomContainer, String[] fragment, IRingSet rs) throws CDKException {
        this.atomContainer = atomContainer;
        this.fragment = fragment;
        this.rs = rs;
        ALOGP = 0.0;
        AMR = 0.0;
        ALOGP2 = 0.0;

        alogpfrag = new int[atomContainer.getAtomCount()];

        for (int i = 1; i <= 120; i++) {
            frags[i] = 0;
        }

        for (int i = 0; i <= atomContainer.getAtomCount() - 1; i++) {

            alogpfrag[i] = 0;
            try {
                // instead of calling hardcoded methods here, use retrospection
                // and run all methods whos name start with 'calc' except for
                // 'calculate'. Nice :)
                Method[] methods = this.getClass().getDeclaredMethods();

                if (fragment[i] instanceof String) {
                    for (int j = 0; j <= methods.length - 1; j++) {
                        Method method = methods[j];
                        if (!method.getName().equals("calculate") && method.getName().startsWith("calc")) {

                            Object[] objs = {i};
                            //Object[] objs = { (int)(i) };
                            method.invoke(this, objs);
                        }
                    }
                }

            } catch (Exception e) {
                throw new CDKException(e.toString());
            }
        } // end i atom loop

        logger.debug("\nFound fragments and frequencies ");

        for (int i = 1; i <= 120; i++) {
            ALOGP += fragval[i] * frags[i];
            AMR += refracval[i] * frags[i];
            if (frags[i] > 0) {
                logger.debug("frag " + i + "  --> " + frags[i]);
            }
        }
        ALOGP2 = ALOGP * ALOGP;

        this.findUnassignedAtoms();

        return new double[]{ALOGP, ALOGP2, AMR};

    }

    private void calcGroup001_005(int i) {
        // C in CH3R
        if (fragment[i].equals("SsCH3")) {
            java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            int htype = getHAtomType(atomContainer.getAtom(i), ca);
            for (int j = 0; j < ca.size(); j++)
            {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    frags[1]++;
                    alogpfrag[i] = 1;
                }
                else
                    if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                    {
                        frags[htype]++;
                    }
                    else
                    {
                        frags[5]++;
                        alogpfrag[i] = 5;
                    }
            }
        }

    }

    private void calcGroup002_006_007(int i) {
        // C in CH2RX

        if (fragment[i].equals("SssCH2")) {

            java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            int htype = getHAtomType(atomContainer.getAtom(i), ca);
            int CarbonCount = 0;
            int HeteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j < ca.size(); j++) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C"))
                    CarbonCount++;
                else
                    if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                    {
                        frags[htype]++;
                    }
                    else
                        HeteroCount++;
            }

            if (CarbonCount == 2 && HeteroCount == 0) {
                frags[2]++;
                alogpfrag[i] = 2;
            } else if (CarbonCount == 1 && HeteroCount == 1) {
                frags[6]++;
                alogpfrag[i] = 6;
            } else if (CarbonCount == 0 && HeteroCount == 2) {
                frags[7]++;
                alogpfrag[i] = 7;
            }
        }
    }

    private void calcGroup003_008_009_010(int i) {

        if (fragment[i].equals("SsssCH")) {

            java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            int htype = getHAtomType(atomContainer.getAtom(i), ca);
            int CarbonCount = 0;
            int HeteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j <= ca.size() - 1; j++) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C"))
                    CarbonCount++;
                else
                    if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                    {
                        frags[htype]++;
                    }
                    else
                    HeteroCount++;
            }

            if (CarbonCount == 3 && HeteroCount == 0) {
                frags[3]++;
                alogpfrag[i] = 3;
            } else if (CarbonCount == 2 && HeteroCount == 1) {
                frags[8]++;
                alogpfrag[i] = 8;
            } else if (CarbonCount == 1 && HeteroCount == 2) {
                frags[9]++;
                alogpfrag[i] = 9;
            } else if (CarbonCount == 0 && HeteroCount == 3) {
                frags[10]++;
                alogpfrag[i] = 10;
            }
        }
    }

    private void calcGroup004_011_to_014(int i) {
        // C in CH2RX
        if (fragment[i].equals("SssssC")) {
            java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
            int CarbonCount = 0;
            int HeteroCount = 0;
            // logger.debug("here");
            for (int j = 0; j <= ca.size() - 1; j++) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C"))
                    CarbonCount++;
                else
                    HeteroCount++;
            }

            if (CarbonCount == 4 && HeteroCount == 0) {
                frags[4]++;
                alogpfrag[i] = 4;
            } else if (CarbonCount == 3 && HeteroCount == 1) {
                frags[11]++;
                alogpfrag[i] = 11;
            } else if (CarbonCount == 2 && HeteroCount == 2) {
                frags[12]++;
                alogpfrag[i] = 12;
            } else if (CarbonCount == 1 && HeteroCount == 3) {
                frags[13]++;
                alogpfrag[i] = 13;
            } else if (CarbonCount == 0 && HeteroCount == 4) {
                frags[14]++;
                alogpfrag[i] = 14;
            }
        }
    }

    private void calcGroup015(int i) {
        if (fragment[i].equals("SdCH2")) {
            frags[15]++;
            alogpfrag[i] = 15;
            int htype = getHAtomType(atomContainer.getAtom(i), null);
            frags[htype]+=2;
        }
    }

    private void calcGroup016_018_036_037(int i) {

        IAtom ai = atomContainer.getAtom(i);
        if (!fragment[i].equals("SdsCH"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(ai);
        int htype = getHAtomType(atomContainer.getAtom(i), ca);
        frags[htype]++;

        boolean HaveCdX = false;
        boolean HaveCsX = false;
        boolean HaveCsAr = false;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                continue;

            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    HaveCsX = true;
                }

                if (((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                    HaveCsAr = true;
                }

            } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    HaveCdX = true;
                }
            }
        }

        if (HaveCdX) {
            if (HaveCsAr) {
                frags[37]++;
                alogpfrag[i] = 37;
            } else {
                frags[36]++;
                alogpfrag[i] = 36;
            }
        } else {
            if (HaveCsX) {
                frags[18]++;
                alogpfrag[i] = 18;
            } else {
                frags[16]++;
                alogpfrag[i] = 16;
            }
        }
    }

    private void calcGroup017_019_020_038_to_041(int i) {

        IAtom ai = atomContainer.getAtom(i);

        if (!fragment[i].equals("SdssC"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(ai);

        int RCount = 0;
        int XCount = 0;
        boolean HaveCdX = false;
        int AliphaticCount = 0;
        int AromaticCount = 0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    RCount++;
                } else {
                    XCount++;
                }

                if (!((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                    AliphaticCount++;
                } else {
                    AromaticCount++;
                }

            } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    HaveCdX = true;
                }
            }
        }

        if (HaveCdX) {
            if (AromaticCount >= 1) { // Ar-C(=X)-R
                // TODO: add code to check if have R or X for nonaromatic
                // attachment to C?
                // if we do this check we would have missing fragment for
                // Ar-C(=X)-X
                // TODO: which fragment to use if we have Ar-C(=X)-Ar? Currently
                // this frag is used

                frags[39]++;
                alogpfrag[i] = 39;
            } else if (AromaticCount == 0) {
                if (RCount == 1 && XCount == 1) {
                    frags[40]++;
                    alogpfrag[i] = 40;
                } else if (RCount == 0 && XCount == 2) {
                    frags[41]++;
                    alogpfrag[i] = 41;
                } else {
                    frags[38]++;
                    alogpfrag[i] = 38;
                }

            }

        } else {
            if (RCount == 2 && XCount == 0) {
                frags[17]++;
                alogpfrag[i] = 17;
            } else if (RCount == 1 && XCount == 1) {
                frags[19]++;
                alogpfrag[i] = 19;
            } else if (RCount == 0 && XCount == 2) {
                frags[20]++;
                alogpfrag[i] = 20;
            }
        }

    }

    private void calcGroup021_to_023_040(int i) {

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);

        if (fragment[i].equals("StCH")) {
            frags[21]++;
            alogpfrag[i] = 21;
            int htype = getHAtomType(atomContainer.getAtom(i), ca);
            frags[htype]++;
        } else if (fragment[i].equals("SddC")) {
            if (((IAtom)ca.get(0)).getSymbol().equals("C") && ((IAtom)ca.get(1)).getSymbol().equals("C")) {// R==C==R
                frags[22]++;
                alogpfrag[i] = 22;
            } else if (!((IAtom)ca.get(0)).getSymbol().equals("C")
                    && !((IAtom)ca.get(1)).getSymbol().equals("C")) {// X==C==X
                frags[40]++;
                alogpfrag[i] = 40;
            }
        } else if (fragment[i].equals("StsC")) {

            boolean HaveCtX = false;
            boolean HaveCsX = false;

            for (int j = 0; j <= ca.size() - 1; j++) {
                if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                    if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                        HaveCsX = true;
                    }
                } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.TRIPLE) {
                    if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                        HaveCtX = true;
                    }
                }
            }

            if (HaveCtX && !HaveCsX) {
                frags[40]++;
                alogpfrag[i] = 40;
            } else if (HaveCsX) {// #C-X
                frags[23]++;
                alogpfrag[i] = 23;
            } else if (!HaveCsX) { // #C-R
                frags[22]++;
                alogpfrag[i] = 22;
            }
        }
    }

    private void calcGroup024_027_030_033_042(int i)
    {
        // 24: C in R--CH--R
        // 27: C in R--CH--X
        // 30: C in X--CH--X
        // 33: C in R--CH...X
        // 42: C in X--CH...X

        if (!fragment[i].equals("SaaCH"))
            return;
        // logger.debug("here");
        //IAtom ai = atomContainer.getAtom(i);
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        int htype = getHAtomType(atomContainer.getAtom(i), ca);
        frags[htype]++;
        IAtom ca0;
        IAtom ca1;
        //Determinig which neigbour is the H atom
        if (((IAtom)ca.get(0)).getSymbol().equals("H"))
        {
            ca0 = (IAtom)ca.get(1);
            ca1 = (IAtom)ca.get(2);
        }
        else
        {
            if (((IAtom)ca.get(1)).getSymbol().equals("H"))
            {
                ca0 = (IAtom)ca.get(0);
                ca1 = (IAtom)ca.get(2);
            }
            else
            {
                ca0 = (IAtom)ca.get(0);
                ca1 = (IAtom)ca.get(1);
            }
        }


        if (ca0.getSymbol().equals("C") && ca1.getSymbol().equals("C")) {
            frags[24]++;
            alogpfrag[i] = 24;
            return;
        }

        // check if both hetero atoms have at least one double bond
        java.util.List bonds = atomContainer.getConnectedBondsList(ca0);
        boolean HaveDouble1 = false;
        for (int k = 0; k <= bonds.size() - 1; k++)
        {
            if (((IBond)bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                HaveDouble1 = true;
                break;
            }
        }

        bonds = atomContainer.getConnectedBondsList(ca1);
        boolean HaveDouble2 = false;
        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond)bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                HaveDouble2 = true;
                break;
            }
        }

        if (!(ca0).getSymbol().equals("C") && !((IAtom)ca.get(1)).getSymbol().equals("C")) {
            if (HaveDouble1 && HaveDouble2) { // X--CH--X
                frags[30]++;
                alogpfrag[i] = 30;
            } else { // X--CH...X
                frags[42]++;
                alogpfrag[i] = 42;
            }

        } else if (ca0.getSymbol().equals("C") && !ca1.getSymbol().equals("C")
                || (!ca0.getSymbol().equals("C") && ca1.getSymbol().equals("C"))) {

            if (HaveDouble1 && HaveDouble2) { // R--CH--X
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

        if (!fragment[i].equals("SsaaC") && !fragment[i].equals("SaaaC"))
            return;

        IAtom ai = atomContainer.getAtom(i);
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));

        IAtom[] sameringatoms = new IAtom[2];
        IAtom nonringatom = atomContainer.getBuilder().newAtom();

        int sameringatomscount = 0;
        for (int j = 0; j <= ca.size() - 1; j++) {
            if (inSameAromaticRing(atomContainer, ai, ((IAtom)ca.get(j)), rs)) {
                sameringatomscount++;
            }

        }

        if (sameringatomscount == 2) {
            int count = 0;
            for (int j = 0; j <= ca.size() - 1; j++) {
                if (inSameAromaticRing(atomContainer, ai, ((IAtom)ca.get(j)), rs)) {
                    sameringatoms[count] = (IAtom)ca.get(j);
                    count++;
                } else {
                    nonringatom = (IAtom)ca.get(j);
                }

            }
        } else { // sameringsatomscount==3
            // arbitrarily assign atoms: (no way to decide consistently)
            sameringatoms[0] = (IAtom)ca.get(0);
            sameringatoms[1] = (IAtom)ca.get(1);
            nonringatom = (IAtom)ca.get(2);
        }

        // check if both hetero atoms have at least one double bond
        java.util.List bonds = atomContainer.getConnectedBondsList(sameringatoms[0]);

        boolean HaveDouble1 = false;

        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond)bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                HaveDouble1 = true;
                break;
            }

        }

        bonds = atomContainer.getConnectedBondsList(sameringatoms[1]);

        boolean HaveDouble2 = false;

        for (int k = 0; k <= bonds.size() - 1; k++) {
            if (((IBond)bonds.get(k)).getOrder() == IBond.Order.DOUBLE) {
                HaveDouble2 = true;
                break;
            }

        }

        if (!sameringatoms[0].getSymbol().equals("C")
                && !sameringatoms[1].getSymbol().equals("C")) {
            if (HaveDouble1 && HaveDouble2) { // X--CR--X
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
        } else if (sameringatoms[0].getSymbol().equals("C")
                && sameringatoms[1].getSymbol().equals("C")) {

            if (nonringatom.getSymbol().equals("C")) {// R--CR--R
                frags[25]++;
                alogpfrag[i] = 25;
            } else { // R--CX--R
                frags[26]++;
                alogpfrag[i] = 26;
            }

        } else if ((sameringatoms[0].getSymbol().equals("C") && !sameringatoms[1]
                .getSymbol().equals("C"))
                || (!sameringatoms[0].getSymbol().equals("C") && sameringatoms[1]
                        .getSymbol().equals("C"))) {

            if (HaveDouble1 && HaveDouble2) { // R--CR--X
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


    private int getHAtomType(IAtom ai, java.util.List connectedAtoms)
    {
        //ai is the atom connected to a H atoms.
        //ai environment determines what is the H atom type
        //This procedure is applied only for carbons
        //i.e. H atom type 50 is never returned

        java.util.List ca;
        if (connectedAtoms == null)
            ca = atomContainer.getConnectedAtomsList(ai);
        else
            ca = connectedAtoms;

        // first check for alpha carbon:
        if (ai.getSymbol().equals("C") && !ai.getFlag(CDKConstants.ISAROMATIC)) {
            for (int j = 0; j <= ca.size() - 1; j++)
            {
                if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE && ((IAtom)ca.get(j)).getSymbol().equals("C")) { // single bonded
                    java.util.List ca2 = atomContainer.getConnectedAtomsList((IAtom)ca.get(j));

                    for (int k = 0; k <= ca2.size() - 1; k++)
                    {
                        IAtom ca2k = (IAtom)ca2.get(k);
                        if (!ca2k.getSymbol().equals("C"))
                        {
                            if (atomContainer.getBond(((IAtom)ca.get(j)), ca2k).getOrder() != IBond.Order.SINGLE)
                                return 51;

                            if (((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)
                                    && ca2k.getFlag(CDKConstants.ISAROMATIC)) {
                                if (inSameAromaticRing(atomContainer, ((IAtom)ca.get(j)), ca2k,	rs))
                                {
                                    return 51;
                                }
                            }
                        } // end !ca2[k].getSymbol().equals("C"))
                    } // end k loop
                } // end if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
            }// end j loop
        } // end if(ai.getSymbol().equals("C") && !ai.getFlag(CDKConstants.ISAROMATIC))

        java.util.List bonds = atomContainer.getConnectedBondsList(ai);
        int doublebondcount = 0;
        int triplebondcount = 0;
        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++)
        {
            if (((IBond)bonds.get(j)).getOrder() == IBond.Order.DOUBLE)
                doublebondcount++;
            else
                if (((IBond)bonds.get(j)).getOrder() == IBond.Order.TRIPLE)
                    triplebondcount++;
        }

        if (doublebondcount == 0 && triplebondcount == 0)
            hybrid = "sp3";
        else
            if (doublebondcount == 1 && triplebondcount == 0)
                hybrid = "sp2";
            else
                if (doublebondcount == 2 || triplebondcount == 1)
                    hybrid = "sp";
        int OxNum = 0;
        int XCount = 0;

        for (int j = 0; j <= ca.size() - 1; j++)
        {
            //String s = ((IAtom)ca.get(j)).getSymbol();
            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))
            if (ap.getNormalizedElectronegativity(((IAtom)ca.get(j)).getSymbol()) > 1)
            {
                java.util.List bonds2 = atomContainer.getConnectedBondsList(((IAtom)ca.get(j)));
                boolean HaveDouble = false;
                for (int k = 0; k <= bonds2.size() - 1; k++)
                {
                    if (((IBond)bonds2.get(k)).getOrder() == IBond.Order.DOUBLE)
                    {
                        HaveDouble = true;
                        break;
                    }
                }
                if (HaveDouble && ((IAtom)ca.get(j)).getSymbol().equals("N"))
                    OxNum += 2; // C-N bond order for pyridine type N's is considered to be 2
                else
                    OxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder());
            }
            java.util.List ca2 = atomContainer.getConnectedAtomsList(((IAtom)ca.get(j)));

            for (int k = 0; k <= ca2.size() - 1; k++)
            {
                String s2 = ((IAtom)ca2.get(k)).getSymbol();
                if (!s2.equals("C"))
                    XCount++;
            }
        }// end j loop

        if (OxNum == 0)
        {
            if (hybrid.equals("sp3"))
            {
                if (XCount == 0)
                    return 46;
                else if (XCount == 1)
                    return 52;
                else if (XCount == 2)
                    return 53;
                else if (XCount == 3)
                    return 54;
                else if (XCount >= 4)
                    return 55;
            }
            else if (hybrid.equals("sp2"))
                return 47;
        }
        else if (OxNum == 1 && hybrid.equals("sp3"))
            return 47;
        else if ((OxNum == 2 && hybrid.equals("sp3"))
                || (OxNum == 1 && hybrid.equals("sp2"))
                || (OxNum == 0 && hybrid.equals("sp")))
            return 48;
        else if ((OxNum == 3 && hybrid.equals("sp3"))
                || (OxNum >= 2 && hybrid.equals("sp2"))
                || (OxNum >= 1 && hybrid.equals("sp")))
            return 49;

        return(0);
    }

    private void calcGroup056_57(int i) {
        // 56: O in =O
        // 57: O in phenol, enol, and carboxyl
        // enol : compound containing a hydroxyl group bonded to a carbon atom
        // that in turn forms a double bond with another carbon atom.
        // enol = HO-C=C-
        // carboxyl= HO-C(=O)-

        if (!fragment[i].equals("SsOH"))
            return;
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        frags[50]++; //H atom attached to a hetero atom

        IAtom ca0 = (IAtom)ca.get(0);
        if (ca0.getSymbol().equals("H"))
            ca0 = (IAtom)ca.get(1);

        if (ca0.getFlag(CDKConstants.ISAROMATIC)) { // phenol
            frags[57]++;
            alogpfrag[i] = 57;
            return;
        }

        java.util.List ca2 = atomContainer.getConnectedAtomsList(ca0);
        for (int j = 0; j <= ca2.size() - 1; j++) {
            if (atomContainer.getBond((IAtom)ca2.get(j), ca0).getOrder() == IBond.Order.DOUBLE) {
                frags[57]++;
                alogpfrag[i] = 57;
                return;
            }
        }
        frags[56]++;
        alogpfrag[i] = 56;
    }

    private void calcGroup058_61(int i) {
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));

        // 58: O in =O
        // 61: --O in nitro, N-oxides
        // 62: O in O-
        IAtom ca0 = (IAtom)ca.get(0);

        if (fragment[i].equals("SsOm")) {

            if (ca0.getSymbol().equals("N") && ca0.getFormalCharge() == 1) {
                frags[61]++;
                alogpfrag[i] = 61;
            } else {
                frags[62]++;
                alogpfrag[i] = 62;
            }

        } else if (fragment[i].equals("SdO")) {
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
        if (!fragment[i].equals("SssO") && !fragment[i].equals("SaaO"))
            return;

        // Al-O-Ar, Ar2O
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom)ca.get(0);
        IAtom ca1 = (IAtom)ca.get(1);

        if (fragment[i].equals("SssO")) {
            if (ca0.getFlag(CDKConstants.ISAROMATIC)
                    || ca1.getFlag(CDKConstants.ISAROMATIC)) {
                frags[60]++;
                alogpfrag[i] = 60;

            } else {

                for (int j = 0; j <= ca.size() - 1; j++) {
                    // if (((IAtom)ca.get(j)).getSymbol().equals("C")) { // for malathion
                    // O-P(=S)
                    // was considered to count as group 60

                    java.util.List ca2 = atomContainer.getConnectedAtomsList(((IAtom)ca.get(j)));
                    for (int k = 0; k <= ca2.size() - 1; k++) {
                        if (atomContainer.getBond(((IAtom)ca.get(j)), (IAtom)ca2.get(k)).getOrder() == IBond.Order.DOUBLE) {
                            if (!((IAtom)ca2.get(k)).getSymbol().equals("C")) {
                                frags[60]++;
                                alogpfrag[i] = 60;
                                return;
                            }
                        }
                    }

                } // end j ca loop

                if (ca0.getSymbol().equals("O")
                        || ca1.getSymbol().equals("O")) {
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

    private void calcGroup066_to_079(int i)
    {
        int NAr = 0;
        int NAl = 0;
        IAtom ai = atomContainer.getAtom(i);
        if (!ai.getSymbol().equals("N"))
            return;
        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        //IAtom ca0 = (IAtom)ca.get(0);
        //IAtom ca1 = (IAtom)ca.get(1);

        for (int j = 0; j <= ca.size() - 1; j++)
        {
            if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                continue;
            if (((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC))
                NAr++;
            else
                NAl++;
        }

        // first check if have RC(=O)N or NX=X
        for (int j = 0; j <= ca.size() - 1; j++)
        {
            if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                continue;
            java.util.List ca2 = atomContainer.getConnectedAtomsList((IAtom)ca.get(j));
            for (int k = 0; k <= ca2.size() - 1; k++) {
                IAtom ca2k = (IAtom)ca2.get(k);
                if (atomContainer.getAtomNumber(ca2k) != i) {
                    if (!ca2k.getSymbol().equals("C")) {
                        if (!ca2k.getFlag(CDKConstants.ISAROMATIC)
                                && !((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)
                                && !ai.getFlag(CDKConstants.ISAROMATIC)) {
                            if (atomContainer.getBond(((IAtom)ca.get(j)), ca2k).getOrder() == IBond.Order.DOUBLE) {
                                frags[72]++;
                                alogpfrag[i] = 72;
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (fragment[i].equals("SsNH2"))
        {
            IAtom ca0 = null;
            //Find which neigbpur is not the hydrogen atom
            for (int j = 0; j <= ca.size() - 1; j++)
            {
                if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                    continue;
                else
                {
                    ca0 = (IAtom)ca.get(j);
                    break;
                }
            }
            if (ca0.getFlag(CDKConstants.ISAROMATIC)
                    || !ca0.getSymbol().equals("C"))
            {
                frags[69]++;
                alogpfrag[i] = 69;
            }
            else
            {
                frags[66]++;
                alogpfrag[i] = 66;
            }
            frags[50]+=2; //H atom attached to a hetero atom
        }
        else if (fragment[i].equals("SaaNH") || fragment[i].equals("SsaaN"))
        { // R...NH...R
            frags[73]++;
            alogpfrag[i] = 73;
            if (fragment[i].equals("SaaNH"))
                frags[50]++; //H atom attached to a hetero atom
        }
        else if (fragment[i].equals("SssNH"))
        {
            if (NAr == 2 && NAl == 0) { // Ar2NH
                frags[73]++;
                alogpfrag[i] = 73;
            } else if (NAr == 1 && NAl == 1) { // Ar-NH-Al
                frags[70]++;
                alogpfrag[i] = 70;

            } else if (NAr == 0 && NAl == 2) { // Al2NH
                frags[67]++;
                alogpfrag[i] = 67;
            }
            frags[50]++; //H atom attached to a hetero atom
        }
        else if (fragment[i].equals("SsssN"))
        {
            if ((NAr == 3 && NAl == 0) || (NAr == 2 && NAl == 1)) { // Ar3N &
                // Ar2NAl
                frags[73]++;
                alogpfrag[i] = 73;
            } else if (NAr == 1 && NAl == 2) {
                frags[71]++;
                alogpfrag[i] = 71;
            } else if (NAr == 0 && NAl == 3) {
                frags[68]++;
                alogpfrag[i] = 68;
            }
        }
        else if (fragment[i].equals("SaaN"))
        {
            frags[75]++;
            alogpfrag[i] = 75;
        }
        else if (fragment[i].equals("SssdNp"))
        {
            boolean HaveSsOm = false;
            boolean HaveSdO = false;
            boolean Ar = false;

            for (int j = 0; j <= ca.size() - 1; j++) {
                if (fragment[atomContainer.getAtomNumber(((IAtom)ca.get(j)))].equals("SsOm")) {
                    HaveSsOm = true;
                } else if (fragment[atomContainer.getAtomNumber(((IAtom)ca.get(j)))].equals("SdO")) {
                    HaveSdO = true;
                } else {
                    if (((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                        Ar = true;
                    }
                }
            }

            if (HaveSsOm && HaveSdO && Ar) {
                frags[76]++;
                alogpfrag[i] = 76;
            } else if (HaveSsOm && HaveSdO && !Ar) {
                frags[77]++;
                alogpfrag[i] = 77;
            } else {
                frags[79]++;
                alogpfrag[i] = 79;
            }

        }
        else if (fragment[i].equals("StN"))
        {
            IAtom ca0 = (IAtom)ca.get(0);
            if (ca0.getSymbol().equals("C")) { // R#N
                frags[74]++;
                alogpfrag[i] = 74;
            }
        }
        else if (fragment[i].equals("SdNH") || fragment[i].equals("SdsN"))
        {
            // test for RO-NO
            if (fragment[i].equals("SdsN"))
            {
                IAtom ca0 = (IAtom)ca.get(0);
                IAtom ca1 = (IAtom)ca.get(1);
                if (ca0.getSymbol().equals("O")
                        && ca1.getSymbol().equals("O")) {
                    frags[76]++;
                    alogpfrag[i] = 76;
                    return;
                }
            }

            boolean flag1 = false;
            boolean flag2 = false;

            for (int j = 0; j <= ca.size() - 1; j++)
            {
                if (((IAtom)ca.get(j)).getSymbol().equals("H"))
                    continue;
                if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE)
                {
                    if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                        frags[74]++;
                        alogpfrag[i] = 74;
                        return;
                    } else {
                        flag1 = true;
                    }
                } else
                {
                    if (!((IAtom)ca.get(j)).getSymbol().equals("C")
                            || ((IAtom)ca.get(j)).getFlag(CDKConstants.ISAROMATIC)) {
                        flag2 = true;
                    }
                }

                if (flag1 && flag2)
                { // X-N=X or Ar-N=X
                    frags[78]++;
                    alogpfrag[i] = 78;
                } else
                {
                    //logger.debug("missing group: R-N=X");
                }
            }

            if (fragment[i].equals("SdNH"))
                frags[50]++; //H atom attached to a hetero atom
        }
        else if (fragment[i].indexOf("p") > -1)
        {
            frags[79]++;
            alogpfrag[i] = 79;
        }

        // TODO add code for R--N(--R)--O
        // first need to have program correctly read in structures with this
        // fragment (pyridine-n-oxides)
    }

    private void calcGroup081_to_085(int i) {

        if (!fragment[i].equals("SsF"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom)ca.get(0);

        java.util.List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond)bonds.get(j);
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

        java.util.List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int OxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom)ca2.get(j);
            String s = ca2j.getSymbol();

            // // F,O,Cl,Br,N

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(ca2j.getSymbol()) > 1) {
                OxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }

        }

        if (hybrid.equals("sp3") && OxNum == 1) {
            frags[81]++;
            alogpfrag[i] = 81;
        } else if (hybrid.equals("sp3") && OxNum == 2) {
            frags[82]++;
            alogpfrag[i] = 82;
        } else if (hybrid.equals("sp3") && OxNum == 3) {
            frags[83]++;
            alogpfrag[i] = 83;
        } else if (hybrid.equals("sp2") && OxNum == 1) {
            frags[84]++;
            alogpfrag[i] = 84;
        } else if ((hybrid.equals("sp2") && OxNum > 1)
                || (hybrid.equals("sp") && OxNum >= 1)
                || (hybrid.equals("sp3") && OxNum == 4)
                || !ca0.getSymbol().equals("C")) {
            frags[85]++;
            alogpfrag[i] = 85;
        }

    }

    private void calcGroup086_to_090(int i) {

        if (!fragment[i].equals("SsCl"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom)ca.get(0);

        java.util.List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond)bonds.get(j);
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

        java.util.List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int OxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom)ca2.get(j);
            String s = ca2j.getSymbol();

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(s) > 1) {
                // // F,O,Cl,Br,N
                OxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }
        }

        if (hybrid.equals("sp3") && OxNum == 1) {
            frags[86]++;
            alogpfrag[i] = 86;
        } else if (hybrid.equals("sp3") && OxNum == 2) {
            frags[87]++;
            alogpfrag[i] = 87;
        } else if (hybrid.equals("sp3") && OxNum == 3) {
            frags[88]++;
            alogpfrag[i] = 88;
        } else if (hybrid.equals("sp2") && OxNum == 1) {
            frags[89]++;
            alogpfrag[i] = 89;
        } else if ((hybrid.equals("sp2") && OxNum > 1)
                || (hybrid.equals("sp") && OxNum >= 1)
                || (hybrid.equals("sp3") && OxNum == 4)
                || !ca0.getSymbol().equals("C")) {
            frags[90]++;
            alogpfrag[i] = 90;
        }

    }

    private void calcGroup091_to_095(int i) {

        if (!fragment[i].equals("SsBr"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom)ca.get(0);

        java.util.List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond)bonds.get(j);
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

        java.util.List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int OxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom)ca2.get(j);
            String s = ca2j.getSymbol();

            // // F,O,Cl,Br,N

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(ca2j.getSymbol()) > 1) {
                OxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }

        }

        if (hybrid.equals("sp3") && OxNum == 1) {
            frags[91]++;
            alogpfrag[i] = 91;
        } else if (hybrid.equals("sp3") && OxNum == 2) {
            frags[92]++;
            alogpfrag[i] = 92;
        } else if (hybrid.equals("sp3") && OxNum == 3) {
            frags[93]++;
            alogpfrag[i] = 93;
        } else if (hybrid.equals("sp2") && OxNum == 1) {
            frags[94]++;
            alogpfrag[i] = 94;
        } else if ((hybrid.equals("sp2") && OxNum > 1)
                || (hybrid.equals("sp") && OxNum >= 1)
                || (hybrid.equals("sp3") && OxNum == 4)
                || !ca0.getSymbol().equals("C")) {
            frags[95]++;
            alogpfrag[i] = 95;
        }

    }

    private void calcGroup096_to_100(int i) {

        if (!fragment[i].equals("SsI"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ca0 = (IAtom)ca.get(0);

        java.util.List bonds = atomContainer.getConnectedBondsList(ca0);

        int doublebondcount = 0;
        int triplebondcount = 0;

        String hybrid = "";

        for (int j = 0; j <= bonds.size() - 1; j++) {
            IBond bj = (IBond)bonds.get(j);
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

        java.util.List ca2 = atomContainer.getConnectedAtomsList(ca0);

        int OxNum = 0;

        for (int j = 0; j <= ca2.size() - 1; j++) {
            IAtom ca2j = (IAtom)ca2.get(j);
            String s = ca2j.getSymbol();

            // // F,O,Cl,Br,N

            // if (s.equals("F") || s.equals("O") || s.equals("Cl")
            // || s.equals("Br") || s.equals("N") || s.equals("S"))

            if (ap.getNormalizedElectronegativity(ca2j.getSymbol()) > 1) {
                OxNum += BondManipulator.destroyBondOrder(atomContainer.getBond(ca0, ca2j).getOrder());
            }

        }

        if (hybrid.equals("sp3") && OxNum == 1) {
            frags[96]++;
            alogpfrag[i] = 96;
        } else if (hybrid.equals("sp3") && OxNum == 2) {
            frags[97]++;
            alogpfrag[i] = 97;
        } else if (hybrid.equals("sp3") && OxNum == 3) {
            frags[98]++;
            alogpfrag[i] = 98;
        } else if (hybrid.equals("sp2") && OxNum == 1) {
            frags[99]++;
            alogpfrag[i] = 99;
        } else if ((hybrid.equals("sp2") && OxNum > 1)
                || (hybrid.equals("sp") && OxNum >= 1)
                || (hybrid.equals("sp3") && OxNum == 4)
                || !ca0.getSymbol().equals("C")) {
            frags[100]++;
            alogpfrag[i] = 100;
        }

    }

    private void calcGroup101_to_104(int i) {
        IAtom ai = atomContainer.getAtom(i);
        String s = ai.getSymbol();

        if (ai.getFormalCharge() == -1) {
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

    private void calcGroup106(int i)
    {
        // S in SH
        if (fragment[i].equals("SsSH")) {
            frags[106]++;
            alogpfrag[i] = 106;
            frags[50]++; //H atom attached to a hetero atom
        }
    }

    private void calcGroup107(int i)
    {
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

    private void calcGroup108(int i)
    {
        // S in R=S
        // In ALOGP, for malathion P=S is consider to have group 108 (even
        // though has P instead of R)
        if (fragment[i].equals("SdS")) {
            frags[108]++;
            alogpfrag[i] = 108;
        }
    }

    private void calcGroup109(int i)
    {
        // for now S in O-S(=O)-O is assigned to this group
        // (it doesn't check which atoms are singly bonded to S
        if (!fragment[i].equals("SdssS")) return;


        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int SdOCount=0;
        int SsCCount=0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    SsCCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("O")) {
                    SdOCount++;
                }
            }
        }
        if (SdOCount==1) { // for now dont check if SsCCount==2
            frags[109]++;
            alogpfrag[i] = 109;
        }
    }

    private void calcGroup110(int i) {
        if (!fragment[i].equals("SddssS"))
            return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int SdOCount=0;
        int SsCCount=0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    SsCCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("O")) {
                    SdOCount++;
                }
            }
        }
        if (SdOCount==2) { // for now dont check if SsCCount==2
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

    private void calcGroup116_117_120(int i) {

        // S in R=S

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);

        int XCount=0;
        int RCount=0;
        boolean PdX=false;

        if (!fragment[i].equals("SdsssP")) return;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    RCount++;
                } else {
                    XCount++;
                }
            } else if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.DOUBLE) {
                if (!((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    PdX=true;
                }
            }
        }

        if (PdX) {
            if (RCount == 3) {
                frags[116]++;
                alogpfrag[i] = 116;
            } else if (XCount == 3) {
                frags[117]++;
                alogpfrag[i] = 117;
            } else if (XCount == 2 && RCount == 1) {
                frags[120]++;
                alogpfrag[i] = 120;
            }
        }

    }
    private void calcGroup118_119(int i) {
        if (!fragment[i].equals("SsssP")) return;

        java.util.List ca = atomContainer.getConnectedAtomsList(atomContainer.getAtom(i));
        IAtom ai = atomContainer.getAtom(i);
        int XCount=0;
        int RCount=0;

        for (int j = 0; j <= ca.size() - 1; j++) {
            if (atomContainer.getBond(ai, ((IAtom)ca.get(j))).getOrder() == IBond.Order.SINGLE) {
                if (((IAtom)ca.get(j)).getSymbol().equals("C")) {
                    RCount++;
                } else {
                    XCount++;
                }
            }
        }

        if (XCount==3) {
            frags[118]++;
            alogpfrag[i] = 118;
        } else if (RCount==3) {
            frags[119]++;
            alogpfrag[i] = 119;
        }


    }

    private boolean inSameAromaticRing(IAtomContainer atomContainer, IAtom atom1,
            IAtom atom2, IRingSet rs) {
        boolean SameRing = false;

        for (int i = 0; i <= rs.getAtomContainerCount() - 1; i++) {
            IRing r = (IRing)rs.getAtomContainer(i);

            if (!r.getFlag(CDKConstants.ISAROMATIC))
                continue;

            // ArrayList al=new ArrayList();

            boolean HaveOne = false;
            boolean HaveTwo = false;

            for (int j = 0; j <= r.getAtomCount() - 1; j++) {
                if (atomContainer.getAtomNumber(r.getAtom(j)) == atomContainer.getAtomNumber(atom1))
                    HaveOne = true;
                if (atomContainer.getAtomNumber(r.getAtom(j)) == atomContainer.getAtomNumber(atom2))
                    HaveTwo = true;
            }

            if (HaveOne && HaveTwo) {
                SameRing = true;
                return SameRing;
            }

        } // end ring for loop

        return SameRing;
    }

    @TestMethod("testChloroButane")
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
            return getDummyDescriptorValue(new CDKException(e.getMessage()));
        }

        DoubleArrayResult results = new DoubleArrayResult();
        results.add(ret[0]);
        results.add(ret[1]);
        results.add(ret[2]);

        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames());
    }

    private DescriptorValue getDummyDescriptorValue(Exception e) {
        DoubleArrayResult results = new DoubleArrayResult();
        results.add(Double.NaN);
        results.add(Double.NaN);
        results.add(Double.NaN);
        return new DescriptorValue(getSpecification(), getParameterNames(),
                getParameters(), results, getDescriptorNames(), e);
    }

    /**
     * Returns the specific type of the DescriptorResult object.
     * <p/>
     * The return value from this method really indicates what type of result will
     * be obtained from the {@link org.openscience.cdk.qsar.DescriptorValue} object. Note that the same result
     * can be achieved by interrogating the {@link org.openscience.cdk.qsar.DescriptorValue} object; this method
     * allows you to do the same thing, without actually calculating the descriptor.
     *
     * @return an object that implements the {@link org.openscience.cdk.qsar.result.IDescriptorResult} interface indicating
     *         the actual type of values returned by the descriptor in the {@link org.openscience.cdk.qsar.DescriptorValue} object
     */
    @TestMethod("testGetDescriptorResultType")
    public IDescriptorResult getDescriptorResultType() {
        return new DoubleArrayResultType(3);
    }


    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#ALOGP",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    }


    public String[] getParameterNames() {
        return new String[0];
    }


    public Object getParameterType(String name) {
        return null;
    }


    public void setParameters(Object[] params) throws CDKException {
    }


    public Object[] getParameters() {
        return null;
    }

    @TestMethod(value="testNamesConsistency")
    public String[] getDescriptorNames() {
        return strings;
    }


}// end class


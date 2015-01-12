/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 */
package org.openscience.cdk.smsd.tools;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.smsd.helper.BondEnergy;

/**
 *
 * Class that stores bond breaking/formation energy between two atoms.
 *
 * Reference: Huheey, pps. A-21 to A-34; T.L. Cottrell,
 * "The Strengths of Chemical Bonds," 2nd ed., Butterworths, London, 1958;
 * B. deB. Darwent, "National Standard Reference Data Series,
 * "National Bureau of Standards, No. 31, Washington, DC, 1970;
 * S.W. Benson, J. Chem. Educ., 42, 502 (1965).
 *
 *
 * Common Bond Energies (D) and Bond Lengths (r)
 *
 * Hydrogen
 * Bond	D(kJ/mol) r(pm)
 *
 * H-H	432	74
 * H-B	389	119
 * H-C	411	109
 * H-Si	318	148
 * H-Ge	288	153
 * H-Sn	251	170
 * H-N	386	101
 * H-P	322	144
 * H-As	247	152
 * H-O	459	96
 * H-S	363	134
 * H-Se	276	146
 * H-Te	238	170
 * H-F	565	92
 * H-Cl	428	127
 * H-Br	362	141
 * H-I	295	161
 *
 *
 * Group 13
 * Bond	D(kJ/mol) r(pm)
 * B-B	293
 * B-O	536
 * B-F	613
 * B-Cl	456	175
 * B-Br	377
 *
 *
 * Group 14
 * Bond	D(kJ/mol) r(pm)
 * C-C	346	154
 * C=C	602	134
 * C#C	835	120
 * C-Si	318	185
 * C-Ge	238	195
 * C-Sn	192	216
 * C-Pb	130	230
 * C-N	305	147
 * C=N	615	129
 * C#N	887	116
 * C-P	264	184
 * C-O	358	143
 * C=O	799	120
 * C#O	1072	113
 * C-B	356
 * C-S	272	182
 * C=S	573	160
 * C-F	485	135
 * C-Cl	327	177
 * C-Br	285	194
 * C-I	213	214
 *
 *
 * Group 14
 * Bond	D(kJ/mol) r(pm)
 * Si-Si	222	233
 * Si-N	355
 * Si-O	452	163
 * Si-S	293	200
 * Si-F	565	160
 * Si-Cl	381	202
 * Si-Br	310	215
 * Si-I	234	243
 * Ge-Ge	188	241
 * Ge-N	257
 * Ge-F	470	168
 * Ge-Cl	349	210
 * Ge-Br	276	230
 * Ge-I	212
 * Sn-F	414
 * Sn-Cl	323	233
 * Sn-Br	273	250
 * Sn-I	205	270
 * Pb-F	331
 * Pb-Cl	243	242
 * Pb-Br	201
 * Pb-I	142	279
 *
 *
 * Group 15
 * Bond	D(kJ/mol) r(pm)
 * N-N	167	145
 * N=N	418	125
 * N#N	942	110
 * N-O	201	140
 * N=O	607	121
 * N-F	283	136
 * N-Cl	313	175
 * P-P	201	221
 * P-O	335	163
 * P=O	544	150
 * P=S	335	186
 * P-F	490	154
 * P-Cl	326	203
 * P-Br	264
 * P-I	184
 * As-As	146	243
 * As-O	301	178
 * As-F	484	171
 * As-Cl	322	216
 * As-Br	458	233
 * As-I	200	254
 * Sb-Sb	121
 * Sb-F	440
 * Sb-Cl (SbCl5)	248
 * Sb-Cl (SbCl3)	315	232
 *
 * Group 16
 * Bond	D(kJ/mol) r(pm)
 * O-O	142	148
 * O=O	494	121
 * O-F	190	142
 * S=O	522	143
 * S-S (S8)	226	205
 * S=S	425	149
 * S-F	284	156
 * S-Cl	255	207
 * Se-Se	172
 * Se=Se	272	215
 *
 * Group 17
 * Bond	D(kJ/mol) r(pm))
 * F-F	155	142
 * Cl-Cl	240	199
 * Br-Br	190	228
 * I-I	148	267
 * At-At	116
 * I-O	201
 * I-F	273	191
 * I-Cl	208	232
 * I-Br	175
 *
 * Group 18
 * Bond	D(kJ/mol) r(pm)
 * Kr-F (KrF2)	50	190
 * Xe-O	84	175
 * Xe-F	130	195
 *
 * @cdk.githash
 * @cdk.module smsd
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class BondEnergies {

    private static Map<Integer, BondEnergy> bondEngergies = null;
    private static BondEnergies             instance      = null;

    /**
     * Returns Singleton pattern instance for the Bond Energy class
     * @return instance
     * @throws CDKException
     */
    public synchronized static BondEnergies getInstance() throws CDKException {
        if (null == instance) {
            instance = new BondEnergies();
        }

        return instance;
    }

    protected BondEnergies() {

        int key = 1;
        bondEngergies = Collections.synchronizedSortedMap(new TreeMap<Integer, BondEnergy>());

        //      =========Hydrogen Block==============
        key = setHydrogenBlock(key);
        //       ==================Group 13=================
        key = setGroup13(key);
        //      ===================Group 14 Part 1=================
        key = setGroup14Part1(key);
        //      ===================Group 14 Part 2=================
        key = setGroup14Part2(key);
        //      ===================Group 15=================
        key = setGroup15(key);
        //      ===================Group 16=================
        key = setGroup16(key);
        //      ===================Group 17=================
        key = setGroup17(key);
        //      ===================Group 18=================
        key = setGroup18(key);
    }

    /**
     * Returns bond energy for a bond type, given atoms and bond type
     * @param sourceAtom First bondEnergy
     * @param targetAtom Second bondEnergy
     * @param bondOrder (single, double etc)
     * @return bond energy
     */
    public int getEnergies(IAtom sourceAtom, IAtom targetAtom, Order bondOrder) {
        int dKJPerMol = -1;

        for (Map.Entry<Integer, BondEnergy> entry : bondEngergies.entrySet()) {
            BondEnergy bondEnergy = entry.getValue();
            String atom1 = bondEnergy.getSymbolFirstAtom();
            String atom2 = bondEnergy.getSymbolSecondAtom();
            if ((atom1.equalsIgnoreCase(sourceAtom.getSymbol()) && atom2.equalsIgnoreCase(targetAtom.getSymbol()))
                    || (atom2.equalsIgnoreCase(sourceAtom.getSymbol()) && atom1
                            .equalsIgnoreCase(targetAtom.getSymbol()))) {

                Order order = bondEnergy.getBondOrder();
                if (order.compareTo(bondOrder) == 0) {
                    dKJPerMol = bondEnergy.getEnergy();
                }
            }
        }
        return dKJPerMol;
    }

    /**
     * Returns bond energy for a bond type, given atoms and bond type
     * @param sourceAtom First bondEnergy
     * @param targetAtom Second bondEnergy
     * @param bondOrder (single, double etc)
     * @return bond energy
     */
    public int getEnergies(String sourceAtom, String targetAtom, Order bondOrder) {
        int dKJPerMol = -1;

        for (Map.Entry<Integer, BondEnergy> entry : bondEngergies.entrySet()) {
            BondEnergy bondEnergy = entry.getValue();
            String atom1 = bondEnergy.getSymbolFirstAtom();
            String atom2 = bondEnergy.getSymbolSecondAtom();
            if ((atom1.equalsIgnoreCase(sourceAtom) && atom2.equalsIgnoreCase(targetAtom))
                    || (atom2.equalsIgnoreCase(sourceAtom) && atom1.equalsIgnoreCase(targetAtom))) {

                Order order = bondEnergy.getBondOrder();
                if (order.compareTo(bondOrder) == 0) {
                    dKJPerMol = bondEnergy.getEnergy();
                }
            }
        }
        return dKJPerMol;
    }

    /**
     * Returns bond energy for a bond type, given atoms and bond type
     * @param bond (single, double etc)
     * @return bond energy
     */
    public int getEnergies(IBond bond) {
        int dKJPerMol = -1;
        for (Map.Entry<Integer, BondEnergy> entry : bondEngergies.entrySet()) {
            BondEnergy bondEnergy = entry.getValue();
            if (bondEnergy.matches(bond)) {
                dKJPerMol = bondEnergy.getEnergy();
            }
        }
        return dKJPerMol;
    }

    private int setHydrogenBlock(int key) {
        bondEngergies.put(key++, new BondEnergy("H", "H", Order.SINGLE, 432));
        bondEngergies.put(key++, new BondEnergy("H", "B", Order.SINGLE, 389));
        bondEngergies.put(key++, new BondEnergy("H", "C", Order.SINGLE, 411));
        bondEngergies.put(key++, new BondEnergy("H", "Si", Order.SINGLE, 318));
        bondEngergies.put(key++, new BondEnergy("H", "Ge", Order.SINGLE, 288));
        bondEngergies.put(key++, new BondEnergy("H", "Sn", Order.SINGLE, 251));
        bondEngergies.put(key++, new BondEnergy("H", "N", Order.SINGLE, 386));
        bondEngergies.put(key++, new BondEnergy("H", "P", Order.SINGLE, 322));
        bondEngergies.put(key++, new BondEnergy("H", "As", Order.SINGLE, 247));
        bondEngergies.put(key++, new BondEnergy("H", "O", Order.SINGLE, 459));
        bondEngergies.put(key++, new BondEnergy("H", "S", Order.SINGLE, 363));
        bondEngergies.put(key++, new BondEnergy("H", "Se", Order.SINGLE, 276));
        bondEngergies.put(key++, new BondEnergy("H", "Te", Order.SINGLE, 238));
        bondEngergies.put(key++, new BondEnergy("H", "F", Order.SINGLE, 565));
        bondEngergies.put(key++, new BondEnergy("H", "Cl", Order.SINGLE, 428));
        bondEngergies.put(key++, new BondEnergy("H", "Br", Order.SINGLE, 362));
        bondEngergies.put(key++, new BondEnergy("H", "I", Order.SINGLE, 295));
        return key;
    }

    private int setGroup13(int key) {

        bondEngergies.put(key++, new BondEnergy("B", "B", Order.SINGLE, 293));
        bondEngergies.put(key++, new BondEnergy("B", "O", Order.SINGLE, 536));
        bondEngergies.put(key++, new BondEnergy("B", "F", Order.SINGLE, 613));
        bondEngergies.put(key++, new BondEnergy("B", "Cl", Order.SINGLE, 456));
        bondEngergies.put(key++, new BondEnergy("B", "Br", Order.SINGLE, 377));
        return key;
    }

    private int setGroup14Part1(int key) {
        bondEngergies.put(key++, new BondEnergy("C", "C", Order.SINGLE, 346));
        bondEngergies.put(key++, new BondEnergy("C", "C", Order.DOUBLE, 602));
        bondEngergies.put(key++, new BondEnergy("C", "C", Order.TRIPLE, 835));
        bondEngergies.put(key++, new BondEnergy("C", "Si", Order.SINGLE, 318));
        bondEngergies.put(key++, new BondEnergy("C", "Ge", Order.SINGLE, 238));
        bondEngergies.put(key++, new BondEnergy("C", "Sn", Order.SINGLE, 192));
        bondEngergies.put(key++, new BondEnergy("C", "Pb", Order.SINGLE, 130));
        bondEngergies.put(key++, new BondEnergy("C", "N", Order.SINGLE, 305));
        bondEngergies.put(key++, new BondEnergy("C", "N", Order.DOUBLE, 615));
        bondEngergies.put(key++, new BondEnergy("C", "N", Order.TRIPLE, 887));
        bondEngergies.put(key++, new BondEnergy("C", "P", Order.SINGLE, 264));
        bondEngergies.put(key++, new BondEnergy("C", "O", Order.SINGLE, 358));
        bondEngergies.put(key++, new BondEnergy("C", "O", Order.DOUBLE, 799));
        bondEngergies.put(key++, new BondEnergy("C", "O", Order.TRIPLE, 1072));
        bondEngergies.put(key++, new BondEnergy("C", "B", Order.SINGLE, 356));
        bondEngergies.put(key++, new BondEnergy("C", "S", Order.SINGLE, 272));
        bondEngergies.put(key++, new BondEnergy("C", "S", Order.DOUBLE, 573));
        bondEngergies.put(key++, new BondEnergy("C", "F", Order.SINGLE, 485));
        bondEngergies.put(key++, new BondEnergy("C", "Cl", Order.SINGLE, 327));
        bondEngergies.put(key++, new BondEnergy("C", "Br", Order.SINGLE, 285));
        bondEngergies.put(key++, new BondEnergy("C", "I", Order.SINGLE, 213));
        return key;
    }

    private int setGroup14Part2(int key) {

        bondEngergies.put(key++, new BondEnergy("Si", "Si", Order.SINGLE, 222));
        bondEngergies.put(key++, new BondEnergy("Si", "N", Order.SINGLE, 355));
        bondEngergies.put(key++, new BondEnergy("Si", "O", Order.SINGLE, 452));
        bondEngergies.put(key++, new BondEnergy("Si", "S", Order.SINGLE, 293));
        bondEngergies.put(key++, new BondEnergy("Si", "F", Order.SINGLE, 565));
        bondEngergies.put(key++, new BondEnergy("Si", "Cl", Order.SINGLE, 381));
        bondEngergies.put(key++, new BondEnergy("Si", "Br", Order.SINGLE, 310));
        bondEngergies.put(key++, new BondEnergy("Si", "I", Order.SINGLE, 234));

        bondEngergies.put(key++, new BondEnergy("Ge", "Ge", Order.SINGLE, 188));
        bondEngergies.put(key++, new BondEnergy("Ge", "N", Order.SINGLE, 257));
        bondEngergies.put(key++, new BondEnergy("Ge", "F", Order.SINGLE, 470));
        bondEngergies.put(key++, new BondEnergy("Ge", "Cl", Order.SINGLE, 349));
        bondEngergies.put(key++, new BondEnergy("Ge", "Br", Order.SINGLE, 276));
        bondEngergies.put(key++, new BondEnergy("Ge", "I", Order.SINGLE, 212));

        bondEngergies.put(key++, new BondEnergy("Sn", "F", Order.SINGLE, 414));
        bondEngergies.put(key++, new BondEnergy("Sn", "Cl", Order.SINGLE, 323));
        bondEngergies.put(key++, new BondEnergy("Sn", "Br", Order.SINGLE, 273));
        bondEngergies.put(key++, new BondEnergy("Sn", "I", Order.SINGLE, 205));

        bondEngergies.put(key++, new BondEnergy("Pb", "F", Order.SINGLE, 313));
        bondEngergies.put(key++, new BondEnergy("Pb", "Cl", Order.SINGLE, 243));
        bondEngergies.put(key++, new BondEnergy("Pb", "Br", Order.SINGLE, 201));
        bondEngergies.put(key++, new BondEnergy("Pb", "I", Order.SINGLE, 142));
        return key;
    }

    private int setGroup15(int key) {
        bondEngergies.put(key++, new BondEnergy("N", "N", Order.SINGLE, 167));
        bondEngergies.put(key++, new BondEnergy("N", "N", Order.DOUBLE, 418));
        bondEngergies.put(key++, new BondEnergy("N", "N", Order.TRIPLE, 942));
        bondEngergies.put(key++, new BondEnergy("N", "O", Order.SINGLE, 201));
        bondEngergies.put(key++, new BondEnergy("N", "O", Order.DOUBLE, 607));
        bondEngergies.put(key++, new BondEnergy("N", "F", Order.SINGLE, 283));
        bondEngergies.put(key++, new BondEnergy("N", "Cl", Order.SINGLE, 313));

        bondEngergies.put(key++, new BondEnergy("P", "P", Order.SINGLE, 201));
        bondEngergies.put(key++, new BondEnergy("P", "O", Order.SINGLE, 335));
        bondEngergies.put(key++, new BondEnergy("P", "O", Order.DOUBLE, 544));
        bondEngergies.put(key++, new BondEnergy("P", "S", Order.DOUBLE, 335));
        bondEngergies.put(key++, new BondEnergy("P", "F", Order.SINGLE, 490));
        bondEngergies.put(key++, new BondEnergy("P", "Cl", Order.SINGLE, 326));
        bondEngergies.put(key++, new BondEnergy("P", "Br", Order.SINGLE, 264));
        bondEngergies.put(key++, new BondEnergy("P", "I", Order.SINGLE, 184));

        bondEngergies.put(key++, new BondEnergy("As", "As", Order.SINGLE, 146));
        bondEngergies.put(key++, new BondEnergy("As", "O", Order.SINGLE, 301));
        bondEngergies.put(key++, new BondEnergy("As", "F", Order.SINGLE, 484));
        bondEngergies.put(key++, new BondEnergy("As", "Cl", Order.SINGLE, 322));
        bondEngergies.put(key++, new BondEnergy("As", "Br", Order.SINGLE, 458));
        bondEngergies.put(key++, new BondEnergy("As", "I", Order.SINGLE, 200));

        bondEngergies.put(key++, new BondEnergy("Sb", "Sb", Order.SINGLE, 121));
        bondEngergies.put(key++, new BondEnergy("Sb", "F", Order.SINGLE, 440));
        //          Sb-Cl (SbCl 5)
        bondEngergies.put(key++, new BondEnergy("Sb", "Cl", Order.SINGLE, 248));
        //          Sb-Cl (SbCl 3)
        bondEngergies.put(key++, new BondEnergy("Sb", "Cl", Order.SINGLE, 315));
        return key;

    }

    private int setGroup16(int key) {

        bondEngergies.put(key++, new BondEnergy("O", "O", Order.SINGLE, 142));
        bondEngergies.put(key++, new BondEnergy("O", "O", Order.DOUBLE, 494));
        bondEngergies.put(key++, new BondEnergy("O", "F", Order.SINGLE, 190));
        bondEngergies.put(key++, new BondEnergy("O", "S", Order.SINGLE, 365));
        bondEngergies.put(key++, new BondEnergy("S", "O", Order.DOUBLE, 522));
        bondEngergies.put(key++, new BondEnergy("S", "S", Order.SINGLE, 226));
        bondEngergies.put(key++, new BondEnergy("S", "S", Order.DOUBLE, 425));
        bondEngergies.put(key++, new BondEnergy("S", "F", Order.SINGLE, 284));
        bondEngergies.put(key++, new BondEnergy("S", "Cl", Order.SINGLE, 255));
        bondEngergies.put(key++, new BondEnergy("Se", "Se", Order.SINGLE, 172));
        bondEngergies.put(key++, new BondEnergy("Se", "Se", Order.DOUBLE, 272));
        return key;

    }

    private int setGroup17(int key) {
        bondEngergies.put(key++, new BondEnergy("F", "F", Order.SINGLE, 155));
        bondEngergies.put(key++, new BondEnergy("Cl", "Cl", Order.SINGLE, 240));
        bondEngergies.put(key++, new BondEnergy("Br", "Br", Order.SINGLE, 190));
        bondEngergies.put(key++, new BondEnergy("I", "I", Order.SINGLE, 148));
        bondEngergies.put(key++, new BondEnergy("At", "At", Order.SINGLE, 116));

        bondEngergies.put(key++, new BondEnergy("I", "O", Order.SINGLE, 201));
        bondEngergies.put(key++, new BondEnergy("I", "F", Order.SINGLE, 273));
        bondEngergies.put(key++, new BondEnergy("I", "Cl", Order.SINGLE, 208));
        bondEngergies.put(key++, new BondEnergy("I", "Br", Order.SINGLE, 175));
        return key;

    }

    private int setGroup18(int key) {

        bondEngergies.put(key++, new BondEnergy("Kr", "F", Order.SINGLE, 50));
        bondEngergies.put(key++, new BondEnergy("Xe", "O", Order.SINGLE, 84));
        bondEngergies.put(key++, new BondEnergy("Xe", "F", Order.SINGLE, 130));
        return key;
    }
}

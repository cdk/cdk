/* $RCSfile: $
 * $Author: egonw $
 * $Date: 2006-07-14 14:09:39 +0200 (Fri, 14 Jul 2006) $
 * $Revision: 6672 $
 *
 * Copyright (C) 2006  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.config;

import org.openscience.cdk.Element;
import org.openscience.cdk.interfaces.IElement;

/**
 * List of elements. Data is taken from the Blue Obelisk Data Repository,
 * version 3.
 * 
 * @author     egonw
 * @cdk.module extra
 */
public class Elements {

	public final static String[] symbols = {
		"Xx", // 0
		"H", // 1
		"He", // 2
		"Li", // 3
		"Be", // 4
		"B", // 5
		"C", // 6
		"N", // 7
		"O", // 8
		"F", // 9
		"Ne", // 10
		"Na", // 11
		"Mg", // 12
		"Al", // 13
		"Si", // 14
		"P", // 15
		"S", // 16
		"Cl", // 17
		"Ar", // 18
		"K", // 19
		"Ca", // 20
		"Sc", // 21
		"Ti", // 22
		"V", // 23
		"Cr", // 24
		"Mn", // 25
		"Fe", // 26
		"Co", // 27
		"Ni", // 28
		"Cu", // 29
		"Zn", // 30
		"Ga", // 31
		"Ge", // 32
		"As", // 33
		"Se", // 34
		"Br", // 35
		"Kr", // 36
		"Rb", // 37
		"Sr", // 38
		"Y", // 39
		"Zr", // 40
		"Nb", // 41
		"Mo", // 42
		"Tc", // 43
		"Ru", // 44
		"Rh", // 45
		"Pd", // 46
		"Ag", // 47
		"Cd", // 48
		"In", // 49
		"Sn", // 50
		"Sb", // 51
		"Te", // 52
		"I", // 53
		"Xe", // 54
		"Cs", // 55
		"Ba", // 56
		"La", // 57
		"Ce", // 58
		"Pr", // 59
		"Nd", // 60
		"Pm", // 61
		"Sm", // 62
		"Eu", // 63
		"Gd", // 64
		"Tb", // 65
		"Dy", // 66
		"Ho", // 67
		"Er", // 68
		"Tm", // 69
		"Yb", // 70
		"Lu", // 71
		"Hf", // 72
		"Ta", // 73
		"W", // 74
		"Re", // 75
		"Os", // 76
		"Ir", // 77
		"Pt", // 78
		"Au", // 79
		"Hg", // 80
		"Tl", // 81
		"Pb", // 82
		"Bi", // 83
		"Po", // 84
		"At", // 85
		"Rn", // 86
		"Fr", // 87
		"Ra", // 88
		"Ac", // 89
		"Th", // 90
		"Pa", // 91
		"U", // 92
		"Np", // 93
		"Pu", // 94
		"Am", // 95
		"Cm", // 96
		"Bk", // 97
		"Cf", // 98
		"Es", // 99
		"Fm", // 100
		"Md", // 101
		"No", // 102
		"Lr", // 103
		"Rf", // 104
		"Db", // 105
		"Sg", // 106
		"Bh", // 107
		"Hs", // 108
		"Mt", // 109
		"Ds", // 110
		"Rg", // 111
		"Uub", // 112
		"Uut", // 113
		"Uuq", // 114
		"Uup", // 115
		"Uuh" // 116
	};
	
	public final static IElement DUMMY = new Element(symbols[0], 0);
	public final static IElement HYDROGEN = new Element(symbols[1], 1);
	public final static IElement HELIUM = new Element(symbols[2], 2);
	public final static IElement LITHIUM = new Element(symbols[3], 3);
	public final static IElement BERYLLIUM = new Element(symbols[4], 4);
	public final static IElement BORON = new Element(symbols[5], 5);
	public final static IElement CARBON = new Element(symbols[6], 6);
	public final static IElement NITROGEN = new Element(symbols[7], 7);
	public final static IElement OXYGEN = new Element(symbols[8], 8);
	public final static IElement FLUORINE = new Element(symbols[9], 9);
	public final static IElement NEON = new Element(symbols[10], 10);
	public final static IElement SODIUM = new Element(symbols[11], 11);
	public final static IElement MAGNESIUM = new Element(symbols[12], 12);
	public final static IElement ALUMINIUM = new Element(symbols[13], 13);
	public final static IElement SILICON = new Element(symbols[14], 14);
	public final static IElement PHOSPHORUS = new Element(symbols[15], 15);
	public final static IElement SULFUR = new Element(symbols[16], 16);
	public final static IElement CHLORINE = new Element(symbols[17], 17);
	public final static IElement ARGON = new Element(symbols[18], 18);
	public final static IElement POTASSIUM = new Element(symbols[19], 19);
	public final static IElement CALCIUM = new Element(symbols[20], 20);
	public final static IElement SCANDIUM = new Element(symbols[21], 21);
	public final static IElement TITANIUM = new Element(symbols[22], 22);
	public final static IElement VANADIUM = new Element(symbols[23], 23);
	public final static IElement CHROMIUM = new Element(symbols[24], 24);
	public final static IElement MANGANESE = new Element(symbols[25], 25);
	public final static IElement IRON = new Element(symbols[26], 26);
	public final static IElement COBALT = new Element(symbols[27], 27);
	public final static IElement NICKEL = new Element(symbols[28], 28);
	public final static IElement COPPER = new Element(symbols[29], 29);
	public final static IElement ZINC = new Element(symbols[30], 30);
	public final static IElement GALLIUM = new Element(symbols[31], 31);
	public final static IElement GERMANIUM = new Element(symbols[32], 32);
	public final static IElement ARSENIC = new Element(symbols[33], 33);
	public final static IElement SELENIUM = new Element(symbols[34], 34);
	public final static IElement BROMINE = new Element(symbols[35], 35);
	public final static IElement KRYPTON = new Element(symbols[36], 36);
	public final static IElement RUBIDIUM = new Element(symbols[37], 37);
	public final static IElement STRONTIUM = new Element(symbols[38], 38);
	public final static IElement YTTRIUM = new Element(symbols[39], 39);
	public final static IElement ZIRCONIUM = new Element(symbols[40], 40);
	public final static IElement NIOBIUM = new Element(symbols[41], 41);
	public final static IElement MOLYBDENUM = new Element(symbols[42], 42);
	public final static IElement TECHNETIUM = new Element(symbols[43], 43);
	public final static IElement RUTHENIUM = new Element(symbols[44], 44);
	public final static IElement RHODIUM = new Element(symbols[45], 45);
	public final static IElement PALLADIUM = new Element(symbols[46], 46);
	public final static IElement SILVER = new Element(symbols[47], 47);
	public final static IElement CADMIUM = new Element(symbols[48], 48);
	public final static IElement INDIUM = new Element(symbols[49], 49);
	public final static IElement TIN = new Element(symbols[50], 50);
	public final static IElement ANTIMONY = new Element(symbols[51], 51);
	public final static IElement TELLURIUM = new Element(symbols[52], 52);
	public final static IElement IODINE = new Element(symbols[53], 53);
	public final static IElement XENON = new Element(symbols[54], 54);
	public final static IElement CAESIUM = new Element(symbols[55], 55);
	public final static IElement BARIUM = new Element(symbols[56], 56);
	public final static IElement LANTHANUM = new Element(symbols[57], 57);
	public final static IElement CERIUM = new Element(symbols[58], 58);
	public final static IElement PRASEODYMIUM = new Element(symbols[59], 59);
	public final static IElement NEODYMIUM = new Element(symbols[60], 60);
	public final static IElement PROMETHIUM = new Element(symbols[61], 61);
	public final static IElement SAMARIUM = new Element(symbols[62], 62);
	public final static IElement EUROPIUM = new Element(symbols[63], 63);
	public final static IElement GADOLINIUM = new Element(symbols[64], 64);
	public final static IElement TERBIUM = new Element(symbols[65], 65);
	public final static IElement DYSPROSIUM = new Element(symbols[66], 66);
	public final static IElement HOLMIUM = new Element(symbols[67], 67);
	public final static IElement ERBIUM = new Element(symbols[68], 68);
	public final static IElement THULIUM = new Element(symbols[69], 69);
	public final static IElement YTTERBIUM = new Element(symbols[70], 70);
	public final static IElement LUTETIUM = new Element(symbols[71], 71);
	public final static IElement HAFNIUM = new Element(symbols[72], 72);
	public final static IElement TANTALUM = new Element(symbols[73], 73);
	public final static IElement TUNGSTEN = new Element(symbols[74], 74);
	public final static IElement RHENIUM = new Element(symbols[75], 75);
	public final static IElement OSMIUM = new Element(symbols[76], 76);
	public final static IElement IRIDIUM = new Element(symbols[77], 77);
	public final static IElement PLATINUM = new Element(symbols[78], 78);
	public final static IElement GOLD = new Element(symbols[79], 79);
	public final static IElement MERCURY = new Element(symbols[80], 80);
	public final static IElement THALLIUM = new Element(symbols[81], 81);
	public final static IElement LEAD = new Element(symbols[82], 82);
	public final static IElement BISMUTH = new Element(symbols[83], 83);
	public final static IElement POLONIUM = new Element(symbols[84], 84);
	public final static IElement ASTATINE = new Element(symbols[85], 85);
	public final static IElement RADON = new Element(symbols[86], 86);
	public final static IElement FRANCIUM = new Element(symbols[87], 87);
	public final static IElement RADIUM = new Element(symbols[88], 88);
	public final static IElement ACTINIUM = new Element(symbols[89], 89);
	public final static IElement THORIUM = new Element(symbols[90], 90);
	public final static IElement PROTACTINIUM = new Element(symbols[91], 91);
	public final static IElement URANIUM = new Element(symbols[92], 92);
	public final static IElement NEPTUNIUM = new Element(symbols[93], 93);
	public final static IElement PLUTONIUM = new Element(symbols[94], 94);
	public final static IElement AMERICIUM = new Element(symbols[95], 95);
	public final static IElement CURIUM = new Element(symbols[96], 96);
	public final static IElement BERKELIUM = new Element(symbols[97], 97);
	public final static IElement CALIFORNIUM = new Element(symbols[98], 98);
	public final static IElement EINSTEINIUM = new Element(symbols[99], 99);
	public final static IElement FERMIUM = new Element(symbols[100], 100);
	public final static IElement MENDELEVIUM = new Element(symbols[101], 101);
	public final static IElement NOBELIUM = new Element(symbols[102], 102);
	public final static IElement LAWRENCIUM = new Element(symbols[103], 103);
	public final static IElement RUTHERFORDIUM = new Element(symbols[104], 104);
	public final static IElement DUBNIUM = new Element(symbols[105], 105);
	public final static IElement SEABORGIUM = new Element(symbols[106], 106);
	public final static IElement BOHRIUM = new Element(symbols[107], 107);
	public final static IElement HASSIUM = new Element(symbols[108], 108);
	public final static IElement MEITNERIUM = new Element(symbols[109], 109);
	public final static IElement DARMSTADTIUM = new Element(symbols[110], 110);
	public final static IElement ROENTGENIUM = new Element(symbols[111], 111);
	public final static IElement UNUNBIUM = new Element(symbols[112], 112);
	public final static IElement UNUNTRIUM = new Element(symbols[113], 113);
	public final static IElement UNUNQUADIUM = new Element(symbols[114], 114);
	public final static IElement UNUNPENTIUM = new Element(symbols[115], 115);
	public final static IElement UNUNHEXIUM = new Element(symbols[116], 116);
	
}

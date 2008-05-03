/* $RCSfile: $
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
 * @cdk.svnrev  $Revision$
 */
public class Elements {
	
	public final static IElement DUMMY = new Element(Symbols.byAtomicNumber[0], 0);
	public final static IElement HYDROGEN = new Element(Symbols.byAtomicNumber[1], 1);
	public final static IElement HELIUM = new Element(Symbols.byAtomicNumber[2], 2);
	public final static IElement LITHIUM = new Element(Symbols.byAtomicNumber[3], 3);
	public final static IElement BERYLLIUM = new Element(Symbols.byAtomicNumber[4], 4);
	public final static IElement BORON = new Element(Symbols.byAtomicNumber[5], 5);
	public final static IElement CARBON = new Element(Symbols.byAtomicNumber[6], 6);
	public final static IElement NITROGEN = new Element(Symbols.byAtomicNumber[7], 7);
	public final static IElement OXYGEN = new Element(Symbols.byAtomicNumber[8], 8);
	public final static IElement FLUORINE = new Element(Symbols.byAtomicNumber[9], 9);
	public final static IElement NEON = new Element(Symbols.byAtomicNumber[10], 10);
	public final static IElement SODIUM = new Element(Symbols.byAtomicNumber[11], 11);
	public final static IElement MAGNESIUM = new Element(Symbols.byAtomicNumber[12], 12);
	public final static IElement ALUMINIUM = new Element(Symbols.byAtomicNumber[13], 13);
	public final static IElement SILICON = new Element(Symbols.byAtomicNumber[14], 14);
	public final static IElement PHOSPHORUS = new Element(Symbols.byAtomicNumber[15], 15);
	public final static IElement SULFUR = new Element(Symbols.byAtomicNumber[16], 16);
	public final static IElement CHLORINE = new Element(Symbols.byAtomicNumber[17], 17);
	public final static IElement ARGON = new Element(Symbols.byAtomicNumber[18], 18);
	public final static IElement POTASSIUM = new Element(Symbols.byAtomicNumber[19], 19);
	public final static IElement CALCIUM = new Element(Symbols.byAtomicNumber[20], 20);
	public final static IElement SCANDIUM = new Element(Symbols.byAtomicNumber[21], 21);
	public final static IElement TITANIUM = new Element(Symbols.byAtomicNumber[22], 22);
	public final static IElement VANADIUM = new Element(Symbols.byAtomicNumber[23], 23);
	public final static IElement CHROMIUM = new Element(Symbols.byAtomicNumber[24], 24);
	public final static IElement MANGANESE = new Element(Symbols.byAtomicNumber[25], 25);
	public final static IElement IRON = new Element(Symbols.byAtomicNumber[26], 26);
	public final static IElement COBALT = new Element(Symbols.byAtomicNumber[27], 27);
	public final static IElement NICKEL = new Element(Symbols.byAtomicNumber[28], 28);
	public final static IElement COPPER = new Element(Symbols.byAtomicNumber[29], 29);
	public final static IElement ZINC = new Element(Symbols.byAtomicNumber[30], 30);
	public final static IElement GALLIUM = new Element(Symbols.byAtomicNumber[31], 31);
	public final static IElement GERMANIUM = new Element(Symbols.byAtomicNumber[32], 32);
	public final static IElement ARSENIC = new Element(Symbols.byAtomicNumber[33], 33);
	public final static IElement SELENIUM = new Element(Symbols.byAtomicNumber[34], 34);
	public final static IElement BROMINE = new Element(Symbols.byAtomicNumber[35], 35);
	public final static IElement KRYPTON = new Element(Symbols.byAtomicNumber[36], 36);
	public final static IElement RUBIDIUM = new Element(Symbols.byAtomicNumber[37], 37);
	public final static IElement STRONTIUM = new Element(Symbols.byAtomicNumber[38], 38);
	public final static IElement YTTRIUM = new Element(Symbols.byAtomicNumber[39], 39);
	public final static IElement ZIRCONIUM = new Element(Symbols.byAtomicNumber[40], 40);
	public final static IElement NIOBIUM = new Element(Symbols.byAtomicNumber[41], 41);
	public final static IElement MOLYBDENUM = new Element(Symbols.byAtomicNumber[42], 42);
	public final static IElement TECHNETIUM = new Element(Symbols.byAtomicNumber[43], 43);
	public final static IElement RUTHENIUM = new Element(Symbols.byAtomicNumber[44], 44);
	public final static IElement RHODIUM = new Element(Symbols.byAtomicNumber[45], 45);
	public final static IElement PALLADIUM = new Element(Symbols.byAtomicNumber[46], 46);
	public final static IElement SILVER = new Element(Symbols.byAtomicNumber[47], 47);
	public final static IElement CADMIUM = new Element(Symbols.byAtomicNumber[48], 48);
	public final static IElement INDIUM = new Element(Symbols.byAtomicNumber[49], 49);
	public final static IElement TIN = new Element(Symbols.byAtomicNumber[50], 50);
	public final static IElement ANTIMONY = new Element(Symbols.byAtomicNumber[51], 51);
	public final static IElement TELLURIUM = new Element(Symbols.byAtomicNumber[52], 52);
	public final static IElement IODINE = new Element(Symbols.byAtomicNumber[53], 53);
	public final static IElement XENON = new Element(Symbols.byAtomicNumber[54], 54);
	public final static IElement CAESIUM = new Element(Symbols.byAtomicNumber[55], 55);
	public final static IElement BARIUM = new Element(Symbols.byAtomicNumber[56], 56);
	public final static IElement LANTHANUM = new Element(Symbols.byAtomicNumber[57], 57);
	public final static IElement CERIUM = new Element(Symbols.byAtomicNumber[58], 58);
	public final static IElement PRASEODYMIUM = new Element(Symbols.byAtomicNumber[59], 59);
	public final static IElement NEODYMIUM = new Element(Symbols.byAtomicNumber[60], 60);
	public final static IElement PROMETHIUM = new Element(Symbols.byAtomicNumber[61], 61);
	public final static IElement SAMARIUM = new Element(Symbols.byAtomicNumber[62], 62);
	public final static IElement EUROPIUM = new Element(Symbols.byAtomicNumber[63], 63);
	public final static IElement GADOLINIUM = new Element(Symbols.byAtomicNumber[64], 64);
	public final static IElement TERBIUM = new Element(Symbols.byAtomicNumber[65], 65);
	public final static IElement DYSPROSIUM = new Element(Symbols.byAtomicNumber[66], 66);
	public final static IElement HOLMIUM = new Element(Symbols.byAtomicNumber[67], 67);
	public final static IElement ERBIUM = new Element(Symbols.byAtomicNumber[68], 68);
	public final static IElement THULIUM = new Element(Symbols.byAtomicNumber[69], 69);
	public final static IElement YTTERBIUM = new Element(Symbols.byAtomicNumber[70], 70);
	public final static IElement LUTETIUM = new Element(Symbols.byAtomicNumber[71], 71);
	public final static IElement HAFNIUM = new Element(Symbols.byAtomicNumber[72], 72);
	public final static IElement TANTALUM = new Element(Symbols.byAtomicNumber[73], 73);
	public final static IElement TUNGSTEN = new Element(Symbols.byAtomicNumber[74], 74);
	public final static IElement RHENIUM = new Element(Symbols.byAtomicNumber[75], 75);
	public final static IElement OSMIUM = new Element(Symbols.byAtomicNumber[76], 76);
	public final static IElement IRIDIUM = new Element(Symbols.byAtomicNumber[77], 77);
	public final static IElement PLATINUM = new Element(Symbols.byAtomicNumber[78], 78);
	public final static IElement GOLD = new Element(Symbols.byAtomicNumber[79], 79);
	public final static IElement MERCURY = new Element(Symbols.byAtomicNumber[80], 80);
	public final static IElement THALLIUM = new Element(Symbols.byAtomicNumber[81], 81);
	public final static IElement LEAD = new Element(Symbols.byAtomicNumber[82], 82);
	public final static IElement BISMUTH = new Element(Symbols.byAtomicNumber[83], 83);
	public final static IElement POLONIUM = new Element(Symbols.byAtomicNumber[84], 84);
	public final static IElement ASTATINE = new Element(Symbols.byAtomicNumber[85], 85);
	public final static IElement RADON = new Element(Symbols.byAtomicNumber[86], 86);
	public final static IElement FRANCIUM = new Element(Symbols.byAtomicNumber[87], 87);
	public final static IElement RADIUM = new Element(Symbols.byAtomicNumber[88], 88);
	public final static IElement ACTINIUM = new Element(Symbols.byAtomicNumber[89], 89);
	public final static IElement THORIUM = new Element(Symbols.byAtomicNumber[90], 90);
	public final static IElement PROTACTINIUM = new Element(Symbols.byAtomicNumber[91], 91);
	public final static IElement URANIUM = new Element(Symbols.byAtomicNumber[92], 92);
	public final static IElement NEPTUNIUM = new Element(Symbols.byAtomicNumber[93], 93);
	public final static IElement PLUTONIUM = new Element(Symbols.byAtomicNumber[94], 94);
	public final static IElement AMERICIUM = new Element(Symbols.byAtomicNumber[95], 95);
	public final static IElement CURIUM = new Element(Symbols.byAtomicNumber[96], 96);
	public final static IElement BERKELIUM = new Element(Symbols.byAtomicNumber[97], 97);
	public final static IElement CALIFORNIUM = new Element(Symbols.byAtomicNumber[98], 98);
	public final static IElement EINSTEINIUM = new Element(Symbols.byAtomicNumber[99], 99);
	public final static IElement FERMIUM = new Element(Symbols.byAtomicNumber[100], 100);
	public final static IElement MENDELEVIUM = new Element(Symbols.byAtomicNumber[101], 101);
	public final static IElement NOBELIUM = new Element(Symbols.byAtomicNumber[102], 102);
	public final static IElement LAWRENCIUM = new Element(Symbols.byAtomicNumber[103], 103);
	public final static IElement RUTHERFORDIUM = new Element(Symbols.byAtomicNumber[104], 104);
	public final static IElement DUBNIUM = new Element(Symbols.byAtomicNumber[105], 105);
	public final static IElement SEABORGIUM = new Element(Symbols.byAtomicNumber[106], 106);
	public final static IElement BOHRIUM = new Element(Symbols.byAtomicNumber[107], 107);
	public final static IElement HASSIUM = new Element(Symbols.byAtomicNumber[108], 108);
	public final static IElement MEITNERIUM = new Element(Symbols.byAtomicNumber[109], 109);
	public final static IElement DARMSTADTIUM = new Element(Symbols.byAtomicNumber[110], 110);
	public final static IElement ROENTGENIUM = new Element(Symbols.byAtomicNumber[111], 111);
	public final static IElement UNUNBIUM = new Element(Symbols.byAtomicNumber[112], 112);
	public final static IElement UNUNTRIUM = new Element(Symbols.byAtomicNumber[113], 113);
	public final static IElement UNUNQUADIUM = new Element(Symbols.byAtomicNumber[114], 114);
	public final static IElement UNUNPENTIUM = new Element(Symbols.byAtomicNumber[115], 115);
	public final static IElement UNUNHEXIUM = new Element(Symbols.byAtomicNumber[116], 116);
	
}

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
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * List of elements. Data is taken from the Blue Obelisk Data Repository,
 * version 3.
 * 
 * @author     egonw
 * @cdk.module extra 
 * @cdk.githash
 */
public class Elements {
	
	public final static IElement DUMMY = new Element(PeriodicTable.getSymbol(0), 0);
	public final static IElement HYDROGEN = new Element(PeriodicTable.getSymbol(1), 1);
	public final static IElement HELIUM = new Element(PeriodicTable.getSymbol(2), 2);
	public final static IElement LITHIUM = new Element(PeriodicTable.getSymbol(3), 3);
	public final static IElement BERYLLIUM = new Element(PeriodicTable.getSymbol(4), 4);
	public final static IElement BORON = new Element(PeriodicTable.getSymbol(5), 5);
	public final static IElement CARBON = new Element(PeriodicTable.getSymbol(6), 6);
	public final static IElement NITROGEN = new Element(PeriodicTable.getSymbol(7), 7);
	public final static IElement OXYGEN = new Element(PeriodicTable.getSymbol(8), 8);
	public final static IElement FLUORINE = new Element(PeriodicTable.getSymbol(9), 9);
	public final static IElement NEON = new Element(PeriodicTable.getSymbol(10), 10);
	public final static IElement SODIUM = new Element(PeriodicTable.getSymbol(11), 11);
	public final static IElement MAGNESIUM = new Element(PeriodicTable.getSymbol(12), 12);
	public final static IElement ALUMINIUM = new Element(PeriodicTable.getSymbol(13), 13);
	public final static IElement SILICON = new Element(PeriodicTable.getSymbol(14), 14);
	public final static IElement PHOSPHORUS = new Element(PeriodicTable.getSymbol(15), 15);
	public final static IElement SULFUR = new Element(PeriodicTable.getSymbol(16), 16);
	public final static IElement CHLORINE = new Element(PeriodicTable.getSymbol(17), 17);
	public final static IElement ARGON = new Element(PeriodicTable.getSymbol(18), 18);
	public final static IElement POTASSIUM = new Element(PeriodicTable.getSymbol(19), 19);
	public final static IElement CALCIUM = new Element(PeriodicTable.getSymbol(20), 20);
	public final static IElement SCANDIUM = new Element(PeriodicTable.getSymbol(21), 21);
	public final static IElement TITANIUM = new Element(PeriodicTable.getSymbol(22), 22);
	public final static IElement VANADIUM = new Element(PeriodicTable.getSymbol(23), 23);
	public final static IElement CHROMIUM = new Element(PeriodicTable.getSymbol(24), 24);
	public final static IElement MANGANESE = new Element(PeriodicTable.getSymbol(25), 25);
	public final static IElement IRON = new Element(PeriodicTable.getSymbol(26), 26);
	public final static IElement COBALT = new Element(PeriodicTable.getSymbol(27), 27);
	public final static IElement NICKEL = new Element(PeriodicTable.getSymbol(28), 28);
	public final static IElement COPPER = new Element(PeriodicTable.getSymbol(29), 29);
	public final static IElement ZINC = new Element(PeriodicTable.getSymbol(30), 30);
	public final static IElement GALLIUM = new Element(PeriodicTable.getSymbol(31), 31);
	public final static IElement GERMANIUM = new Element(PeriodicTable.getSymbol(32), 32);
	public final static IElement ARSENIC = new Element(PeriodicTable.getSymbol(33), 33);
	public final static IElement SELENIUM = new Element(PeriodicTable.getSymbol(34), 34);
	public final static IElement BROMINE = new Element(PeriodicTable.getSymbol(35), 35);
	public final static IElement KRYPTON = new Element(PeriodicTable.getSymbol(36), 36);
	public final static IElement RUBIDIUM = new Element(PeriodicTable.getSymbol(37), 37);
	public final static IElement STRONTIUM = new Element(PeriodicTable.getSymbol(38), 38);
	public final static IElement YTTRIUM = new Element(PeriodicTable.getSymbol(39), 39);
	public final static IElement ZIRCONIUM = new Element(PeriodicTable.getSymbol(40), 40);
	public final static IElement NIOBIUM = new Element(PeriodicTable.getSymbol(41), 41);
	public final static IElement MOLYBDENUM = new Element(PeriodicTable.getSymbol(42), 42);
	public final static IElement TECHNETIUM = new Element(PeriodicTable.getSymbol(43), 43);
	public final static IElement RUTHENIUM = new Element(PeriodicTable.getSymbol(44), 44);
	public final static IElement RHODIUM = new Element(PeriodicTable.getSymbol(45), 45);
	public final static IElement PALLADIUM = new Element(PeriodicTable.getSymbol(46), 46);
	public final static IElement SILVER = new Element(PeriodicTable.getSymbol(47), 47);
	public final static IElement CADMIUM = new Element(PeriodicTable.getSymbol(48), 48);
	public final static IElement INDIUM = new Element(PeriodicTable.getSymbol(49), 49);
	public final static IElement TIN = new Element(PeriodicTable.getSymbol(50), 50);
	public final static IElement ANTIMONY = new Element(PeriodicTable.getSymbol(51), 51);
	public final static IElement TELLURIUM = new Element(PeriodicTable.getSymbol(52), 52);
	public final static IElement IODINE = new Element(PeriodicTable.getSymbol(53), 53);
	public final static IElement XENON = new Element(PeriodicTable.getSymbol(54), 54);
	public final static IElement CAESIUM = new Element(PeriodicTable.getSymbol(55), 55);
	public final static IElement BARIUM = new Element(PeriodicTable.getSymbol(56), 56);
	public final static IElement LANTHANUM = new Element(PeriodicTable.getSymbol(57), 57);
	public final static IElement CERIUM = new Element(PeriodicTable.getSymbol(58), 58);
	public final static IElement PRASEODYMIUM = new Element(PeriodicTable.getSymbol(59), 59);
	public final static IElement NEODYMIUM = new Element(PeriodicTable.getSymbol(60), 60);
	public final static IElement PROMETHIUM = new Element(PeriodicTable.getSymbol(61), 61);
	public final static IElement SAMARIUM = new Element(PeriodicTable.getSymbol(62), 62);
	public final static IElement EUROPIUM = new Element(PeriodicTable.getSymbol(63), 63);
	public final static IElement GADOLINIUM = new Element(PeriodicTable.getSymbol(64), 64);
	public final static IElement TERBIUM = new Element(PeriodicTable.getSymbol(65), 65);
	public final static IElement DYSPROSIUM = new Element(PeriodicTable.getSymbol(66), 66);
	public final static IElement HOLMIUM = new Element(PeriodicTable.getSymbol(67), 67);
	public final static IElement ERBIUM = new Element(PeriodicTable.getSymbol(68), 68);
	public final static IElement THULIUM = new Element(PeriodicTable.getSymbol(69), 69);
	public final static IElement YTTERBIUM = new Element(PeriodicTable.getSymbol(70), 70);
	public final static IElement LUTETIUM = new Element(PeriodicTable.getSymbol(71), 71);
	public final static IElement HAFNIUM = new Element(PeriodicTable.getSymbol(72), 72);
	public final static IElement TANTALUM = new Element(PeriodicTable.getSymbol(73), 73);
	public final static IElement TUNGSTEN = new Element(PeriodicTable.getSymbol(74), 74);
	public final static IElement RHENIUM = new Element(PeriodicTable.getSymbol(75), 75);
	public final static IElement OSMIUM = new Element(PeriodicTable.getSymbol(76), 76);
	public final static IElement IRIDIUM = new Element(PeriodicTable.getSymbol(77), 77);
	public final static IElement PLATINUM = new Element(PeriodicTable.getSymbol(78), 78);
	public final static IElement GOLD = new Element(PeriodicTable.getSymbol(79), 79);
	public final static IElement MERCURY = new Element(PeriodicTable.getSymbol(80), 80);
	public final static IElement THALLIUM = new Element(PeriodicTable.getSymbol(81), 81);
	public final static IElement LEAD = new Element(PeriodicTable.getSymbol(82), 82);
	public final static IElement BISMUTH = new Element(PeriodicTable.getSymbol(83), 83);
	public final static IElement POLONIUM = new Element(PeriodicTable.getSymbol(84), 84);
	public final static IElement ASTATINE = new Element(PeriodicTable.getSymbol(85), 85);
	public final static IElement RADON = new Element(PeriodicTable.getSymbol(86), 86);
	public final static IElement FRANCIUM = new Element(PeriodicTable.getSymbol(87), 87);
	public final static IElement RADIUM = new Element(PeriodicTable.getSymbol(88), 88);
	public final static IElement ACTINIUM = new Element(PeriodicTable.getSymbol(89), 89);
	public final static IElement THORIUM = new Element(PeriodicTable.getSymbol(90), 90);
	public final static IElement PROTACTINIUM = new Element(PeriodicTable.getSymbol(91), 91);
	public final static IElement URANIUM = new Element(PeriodicTable.getSymbol(92), 92);
	public final static IElement NEPTUNIUM = new Element(PeriodicTable.getSymbol(93), 93);
	public final static IElement PLUTOMNIUM = new Element(PeriodicTable.getSymbol(94), 94);
	public final static IElement AMERICIUM = new Element(PeriodicTable.getSymbol(95), 95);
	public final static IElement CURIUM = new Element(PeriodicTable.getSymbol(96), 96);
	public final static IElement BERKELIUM = new Element(PeriodicTable.getSymbol(97), 97);
	public final static IElement CALIFORNIUM = new Element(PeriodicTable.getSymbol(98), 98);
	public final static IElement EINSTEINIUM = new Element(PeriodicTable.getSymbol(99), 99);
	public final static IElement FERMIUM = new Element(PeriodicTable.getSymbol(100), 100);
	public final static IElement MENDELEVIUM = new Element(PeriodicTable.getSymbol(101), 101);
	public final static IElement NOBELIUM = new Element(PeriodicTable.getSymbol(102), 102);
	public final static IElement LAWRENCIUM = new Element(PeriodicTable.getSymbol(103), 103);
	public final static IElement RUTHERFORDIUM = new Element(PeriodicTable.getSymbol(104), 104);
	public final static IElement DUBNIUM = new Element(PeriodicTable.getSymbol(105), 105);
	public final static IElement SEABORGIUM = new Element(PeriodicTable.getSymbol(106), 106);
	public final static IElement BOHRIUM = new Element(PeriodicTable.getSymbol(107), 107);
	public final static IElement HASSIUM = new Element(PeriodicTable.getSymbol(108), 108);
	public final static IElement MEITNERIUM = new Element(PeriodicTable.getSymbol(109), 109);
	public final static IElement DARMSTADTIUM = new Element(PeriodicTable.getSymbol(110), 110);
	public final static IElement ROENTGENIUM = new Element(PeriodicTable.getSymbol(111), 111);
	public final static IElement UNUNBIUM = new Element(PeriodicTable.getSymbol(112), 112);
	public final static IElement UNUNTRIUM = new Element(PeriodicTable.getSymbol(113), 113);
	public final static IElement UNUNQUADIUM = new Element(PeriodicTable.getSymbol(114), 114); // to be removed in master
	public final static IElement FLEROVIUM = new Element(PeriodicTable.getSymbol(114), 114);
	public final static IElement UNUNPENTIUM = new Element(PeriodicTable.getSymbol(115), 115);
	public final static IElement UNUNHEXIUM = new Element(PeriodicTable.getSymbol(116), 116); // to be removed in master
	public final static IElement LIVERMORIUM = new Element(PeriodicTable.getSymbol(116), 116);
	
}

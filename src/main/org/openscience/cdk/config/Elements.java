/* Copyright (C) 2006-2012  Egon Willighagen <egonw@users.sf.net>
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

import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.periodictable.PeriodicTable;

/**
 * List of elements. Data is taken from the Blue Obelisk Data Repository,
 * version 3.
 * 
 * @author      egonw
 * @cdk.module  standard 
 * @cdk.githash
 */
public class Elements {
	
	public final static IElement DUMMY = new NaturalElement(PeriodicTable.getSymbol(0), 0);
	public final static IElement HYDROGEN = new NaturalElement(PeriodicTable.getSymbol(1), 1);
	public final static IElement HELIUM = new NaturalElement(PeriodicTable.getSymbol(2), 2);
	public final static IElement LITHIUM = new NaturalElement(PeriodicTable.getSymbol(3), 3);
	public final static IElement BERYLLIUM = new NaturalElement(PeriodicTable.getSymbol(4), 4);
	public final static IElement BORON = new NaturalElement(PeriodicTable.getSymbol(5), 5);
	public final static IElement CARBON = new NaturalElement(PeriodicTable.getSymbol(6), 6);
	public final static IElement NITROGEN = new NaturalElement(PeriodicTable.getSymbol(7), 7);
	public final static IElement OXYGEN = new NaturalElement(PeriodicTable.getSymbol(8), 8);
	public final static IElement FLUORINE = new NaturalElement(PeriodicTable.getSymbol(9), 9);
	public final static IElement NEON = new NaturalElement(PeriodicTable.getSymbol(10), 10);
	public final static IElement SODIUM = new NaturalElement(PeriodicTable.getSymbol(11), 11);
	public final static IElement MAGNESIUM = new NaturalElement(PeriodicTable.getSymbol(12), 12);
	public final static IElement ALUMINIUM = new NaturalElement(PeriodicTable.getSymbol(13), 13);
	public final static IElement SILICON = new NaturalElement(PeriodicTable.getSymbol(14), 14);
	public final static IElement PHOSPHORUS = new NaturalElement(PeriodicTable.getSymbol(15), 15);
	public final static IElement SULFUR = new NaturalElement(PeriodicTable.getSymbol(16), 16);
	public final static IElement CHLORINE = new NaturalElement(PeriodicTable.getSymbol(17), 17);
	public final static IElement ARGON = new NaturalElement(PeriodicTable.getSymbol(18), 18);
	public final static IElement POTASSIUM = new NaturalElement(PeriodicTable.getSymbol(19), 19);
	public final static IElement CALCIUM = new NaturalElement(PeriodicTable.getSymbol(20), 20);
	public final static IElement SCANDIUM = new NaturalElement(PeriodicTable.getSymbol(21), 21);
	public final static IElement TITANIUM = new NaturalElement(PeriodicTable.getSymbol(22), 22);
	public final static IElement VANADIUM = new NaturalElement(PeriodicTable.getSymbol(23), 23);
	public final static IElement CHROMIUM = new NaturalElement(PeriodicTable.getSymbol(24), 24);
	public final static IElement MANGANESE = new NaturalElement(PeriodicTable.getSymbol(25), 25);
	public final static IElement IRON = new NaturalElement(PeriodicTable.getSymbol(26), 26);
	public final static IElement COBALT = new NaturalElement(PeriodicTable.getSymbol(27), 27);
	public final static IElement NICKEL = new NaturalElement(PeriodicTable.getSymbol(28), 28);
	public final static IElement COPPER = new NaturalElement(PeriodicTable.getSymbol(29), 29);
	public final static IElement ZINC = new NaturalElement(PeriodicTable.getSymbol(30), 30);
	public final static IElement GALLIUM = new NaturalElement(PeriodicTable.getSymbol(31), 31);
	public final static IElement GERMANIUM = new NaturalElement(PeriodicTable.getSymbol(32), 32);
	public final static IElement ARSENIC = new NaturalElement(PeriodicTable.getSymbol(33), 33);
	public final static IElement SELENIUM = new NaturalElement(PeriodicTable.getSymbol(34), 34);
	public final static IElement BROMINE = new NaturalElement(PeriodicTable.getSymbol(35), 35);
	public final static IElement KRYPTON = new NaturalElement(PeriodicTable.getSymbol(36), 36);
	public final static IElement RUBIDIUM = new NaturalElement(PeriodicTable.getSymbol(37), 37);
	public final static IElement STRONTIUM = new NaturalElement(PeriodicTable.getSymbol(38), 38);
	public final static IElement YTTRIUM = new NaturalElement(PeriodicTable.getSymbol(39), 39);
	public final static IElement ZIRCONIUM = new NaturalElement(PeriodicTable.getSymbol(40), 40);
	public final static IElement NIOBIUM = new NaturalElement(PeriodicTable.getSymbol(41), 41);
	public final static IElement MOLYBDENUM = new NaturalElement(PeriodicTable.getSymbol(42), 42);
	public final static IElement TECHNETIUM = new NaturalElement(PeriodicTable.getSymbol(43), 43);
	public final static IElement RUTHENIUM = new NaturalElement(PeriodicTable.getSymbol(44), 44);
	public final static IElement RHODIUM = new NaturalElement(PeriodicTable.getSymbol(45), 45);
	public final static IElement PALLADIUM = new NaturalElement(PeriodicTable.getSymbol(46), 46);
	public final static IElement SILVER = new NaturalElement(PeriodicTable.getSymbol(47), 47);
	public final static IElement CADMIUM = new NaturalElement(PeriodicTable.getSymbol(48), 48);
	public final static IElement INDIUM = new NaturalElement(PeriodicTable.getSymbol(49), 49);
	public final static IElement TIN = new NaturalElement(PeriodicTable.getSymbol(50), 50);
	public final static IElement ANTIMONY = new NaturalElement(PeriodicTable.getSymbol(51), 51);
	public final static IElement TELLURIUM = new NaturalElement(PeriodicTable.getSymbol(52), 52);
	public final static IElement IODINE = new NaturalElement(PeriodicTable.getSymbol(53), 53);
	public final static IElement XENON = new NaturalElement(PeriodicTable.getSymbol(54), 54);
	public final static IElement CAESIUM = new NaturalElement(PeriodicTable.getSymbol(55), 55);
	public final static IElement BARIUM = new NaturalElement(PeriodicTable.getSymbol(56), 56);
	public final static IElement LANTHANUM = new NaturalElement(PeriodicTable.getSymbol(57), 57);
	public final static IElement CERIUM = new NaturalElement(PeriodicTable.getSymbol(58), 58);
	public final static IElement PRASEODYMIUM = new NaturalElement(PeriodicTable.getSymbol(59), 59);
	public final static IElement NEODYMIUM = new NaturalElement(PeriodicTable.getSymbol(60), 60);
	public final static IElement PROMETHIUM = new NaturalElement(PeriodicTable.getSymbol(61), 61);
	public final static IElement SAMARIUM = new NaturalElement(PeriodicTable.getSymbol(62), 62);
	public final static IElement EUROPIUM = new NaturalElement(PeriodicTable.getSymbol(63), 63);
	public final static IElement GADOLINIUM = new NaturalElement(PeriodicTable.getSymbol(64), 64);
	public final static IElement TERBIUM = new NaturalElement(PeriodicTable.getSymbol(65), 65);
	public final static IElement DYSPROSIUM = new NaturalElement(PeriodicTable.getSymbol(66), 66);
	public final static IElement HOLMIUM = new NaturalElement(PeriodicTable.getSymbol(67), 67);
	public final static IElement ERBIUM = new NaturalElement(PeriodicTable.getSymbol(68), 68);
	public final static IElement THULIUM = new NaturalElement(PeriodicTable.getSymbol(69), 69);
	public final static IElement YTTERBIUM = new NaturalElement(PeriodicTable.getSymbol(70), 70);
	public final static IElement LUTETIUM = new NaturalElement(PeriodicTable.getSymbol(71), 71);
	public final static IElement HAFNIUM = new NaturalElement(PeriodicTable.getSymbol(72), 72);
	public final static IElement TANTALUM = new NaturalElement(PeriodicTable.getSymbol(73), 73);
	public final static IElement TUNGSTEN = new NaturalElement(PeriodicTable.getSymbol(74), 74);
	public final static IElement RHENIUM = new NaturalElement(PeriodicTable.getSymbol(75), 75);
	public final static IElement OSMIUM = new NaturalElement(PeriodicTable.getSymbol(76), 76);
	public final static IElement IRIDIUM = new NaturalElement(PeriodicTable.getSymbol(77), 77);
	public final static IElement PLATINUM = new NaturalElement(PeriodicTable.getSymbol(78), 78);
	public final static IElement GOLD = new NaturalElement(PeriodicTable.getSymbol(79), 79);
	public final static IElement MERCURY = new NaturalElement(PeriodicTable.getSymbol(80), 80);
	public final static IElement THALLIUM = new NaturalElement(PeriodicTable.getSymbol(81), 81);
	public final static IElement LEAD = new NaturalElement(PeriodicTable.getSymbol(82), 82);
	public final static IElement BISMUTH = new NaturalElement(PeriodicTable.getSymbol(83), 83);
	public final static IElement POLONIUM = new NaturalElement(PeriodicTable.getSymbol(84), 84);
	public final static IElement ASTATINE = new NaturalElement(PeriodicTable.getSymbol(85), 85);
	public final static IElement RADON = new NaturalElement(PeriodicTable.getSymbol(86), 86);
	public final static IElement FRANCIUM = new NaturalElement(PeriodicTable.getSymbol(87), 87);
	public final static IElement RADIUM = new NaturalElement(PeriodicTable.getSymbol(88), 88);
	public final static IElement ACTINIUM = new NaturalElement(PeriodicTable.getSymbol(89), 89);
	public final static IElement THORIUM = new NaturalElement(PeriodicTable.getSymbol(90), 90);
	public final static IElement PROTACTINIUM = new NaturalElement(PeriodicTable.getSymbol(91), 91);
	public final static IElement URANIUM = new NaturalElement(PeriodicTable.getSymbol(92), 92);
	public final static IElement NEPTUNIUM = new NaturalElement(PeriodicTable.getSymbol(93), 93);
	public final static IElement PLUTOMNIUM = new NaturalElement(PeriodicTable.getSymbol(94), 94);
	public final static IElement AMERICIUM = new NaturalElement(PeriodicTable.getSymbol(95), 95);
	public final static IElement CURIUM = new NaturalElement(PeriodicTable.getSymbol(96), 96);
	public final static IElement BERKELIUM = new NaturalElement(PeriodicTable.getSymbol(97), 97);
	public final static IElement CALIFORNIUM = new NaturalElement(PeriodicTable.getSymbol(98), 98);
	public final static IElement EINSTEINIUM = new NaturalElement(PeriodicTable.getSymbol(99), 99);
	public final static IElement FERMIUM = new NaturalElement(PeriodicTable.getSymbol(100), 100);
	public final static IElement MENDELEVIUM = new NaturalElement(PeriodicTable.getSymbol(101), 101);
	public final static IElement NOBELIUM = new NaturalElement(PeriodicTable.getSymbol(102), 102);
	public final static IElement LAWRENCIUM = new NaturalElement(PeriodicTable.getSymbol(103), 103);
	public final static IElement RUTHERFORDIUM = new NaturalElement(PeriodicTable.getSymbol(104), 104);
	public final static IElement DUBNIUM = new NaturalElement(PeriodicTable.getSymbol(105), 105);
	public final static IElement SEABORGIUM = new NaturalElement(PeriodicTable.getSymbol(106), 106);
	public final static IElement BOHRIUM = new NaturalElement(PeriodicTable.getSymbol(107), 107);
	public final static IElement HASSIUM = new NaturalElement(PeriodicTable.getSymbol(108), 108);
	public final static IElement MEITNERIUM = new NaturalElement(PeriodicTable.getSymbol(109), 109);
	public final static IElement DARMSTADTIUM = new NaturalElement(PeriodicTable.getSymbol(110), 110);
	public final static IElement ROENTGENIUM = new NaturalElement(PeriodicTable.getSymbol(111), 111);
	public final static IElement UNUNBIUM = new NaturalElement(PeriodicTable.getSymbol(112), 112);
	public final static IElement UNUNTRIUM = new NaturalElement(PeriodicTable.getSymbol(113), 113);
	public final static IElement UNUNQUADIUM = new NaturalElement(PeriodicTable.getSymbol(114), 114);
	public final static IElement UNUNPENTIUM = new NaturalElement(PeriodicTable.getSymbol(115), 115);
	public final static IElement UNUNHEXIUM = new NaturalElement(PeriodicTable.getSymbol(116), 116);

}

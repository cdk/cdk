/* Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.interfaces;

/**
 * Implements the idea of an element in the periodic table.
 *
 * @cdk.githash
 * @cdk.keyword element
 * @cdk.keyword atomic number
 * @cdk.keyword number, atomic
 */
public interface IElement extends IChemObject {

    // Integer enum constants for element atomic numbers

    /** Wildcard atom is atomic number 0 */
    int Wildcard = 0;
    /** Hydrogen atomic number */
    int H        = 1;
    int He       = 2;
    int Li       = 3;
    int Be       = 4;
    int B        = 5;
    /** Carbon atomic number */
    int C        = 6;
    /** Nitrogen atomic number */
    int N        = 7;
    /** Oxygen atomic number */
    int O        = 8;
    int F        = 9;
    int Ne       = 10;
    int Na       = 11;
    int Mg       = 12;
    int Al       = 13;
    int Si       = 14;
    int P        = 15;
    int S        = 16;
    int Cl       = 17;
    int Ar       = 18;
    int K        = 19;
    int Ca       = 20;
    int Sc       = 21;
    int Ti       = 22;
    int V        = 23;
    int Cr       = 24;
    int Mn       = 25;
    int Fe       = 26;
    int Co       = 27;
    int Ni       = 28;
    int Cu       = 29;
    int Zn       = 30;
    int Ga       = 31;
    int Ge       = 32;
    int As       = 33;
    int Se       = 34;
    int Br       = 35;
    int Kr       = 36;
    int Rb       = 37;
    int Sr       = 38;
    int Y        = 39;
    int Zr       = 40;
    int Nb       = 41;
    int Mo       = 42;
    int Tc       = 43;
    int Ru       = 44;
    int Rh       = 45;
    int Pd       = 46;
    int Ag       = 47;
    int Cd       = 48;
    int In       = 49;
    int Sn       = 50;
    int Sb       = 51;
    int Te       = 52;
    int I        = 53;
    int Xe       = 54;
    int Cs       = 55;
    int Ba       = 56;
    int La       = 57;
    int Ce       = 58;
    int Pr       = 59;
    int Nd       = 60;
    int Pm       = 61;
    int Sm       = 62;
    int Eu       = 63;
    int Gd       = 64;
    int Tb       = 65;
    int Dy       = 66;
    int Ho       = 67;
    int Er       = 68;
    int Tm       = 69;
    int Yb       = 70;
    int Lu       = 71;
    int Hf       = 72;
    int Ta       = 73;
    int W        = 74;
    int Re       = 75;
    int Os       = 76;
    int Ir       = 77;
    int Pt       = 78;
    int Au       = 79;
    int Hg       = 80;
    int Tl       = 81;
    int Pb       = 82;
    int Bi       = 83;
    int Po       = 84;
    int At       = 85;
    int Rn       = 86;
    int Fr       = 87;
    int Ra       = 88;
    int Ac       = 89;
    int Th       = 90;
    int Pa       = 91;
    int U        = 92;
    int Np       = 93;
    int Pu       = 94;
    int Am       = 95;
    int Cm       = 96;
    int Bk       = 97;
    int Cf       = 98;
    int Es       = 99;
    int Fm       = 100;
    int Md       = 101;
    int No       = 102;
    int Lr       = 103;
    int Rf       = 104;
    int Db       = 105;
    int Sg       = 106;
    int Bh       = 107;
    int Hs       = 108;
    int Mt       = 109;
    int Ds       = 110;
    int Rg       = 111;
    int Cn       = 112;
    int Nh       = 113;
    int Fl       = 114;
    int Mc       = 115;
    int Lv       = 116;
    int Ts       = 117;
    int Og       = 118;

    /**
     * Returns the atomic number of this element.
     *
     * <p>Once instantiated all field not filled by passing parameters
     * to the constructor are null. Elements can be configured by using
     * the IsotopeFactory.configure() method:
     * <pre>
     *   Element element = new Element("C");
     *   IsotopeFactory if = IsotopeFactory.getInstance(element.getNewBuilder());
     *   if.configure(element);
     * </pre>
     *
     * @return The atomic number of this element
     *
     * @see    #setAtomicNumber
     */
    Integer getAtomicNumber();

    /**
     * Sets the atomic number of this element.
     *
     * @param   atomicNumber The atomic mass to be assigned to this element
     *
     * @see    #getAtomicNumber
     */
    void setAtomicNumber(Integer atomicNumber);

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    String getSymbol();

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    void setSymbol(String symbol);

}

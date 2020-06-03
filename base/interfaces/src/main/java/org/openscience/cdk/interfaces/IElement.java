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
 * @cdk.module interfaces
 * @cdk.githash
 * @cdk.keyword element
 * @cdk.keyword atomic number
 * @cdk.keyword number, atomic
 */
public interface IElement extends IChemObject {

    /**
     * Byte enum constants for element atomic numbers
     */
    public static final byte Wildcard = 0;
    public static final byte H        = 1;
    public static final byte He       = 2;
    public static final byte Li       = 3;
    public static final byte Be       = 4;
    public static final byte B        = 5;
    public static final byte C        = 6;
    public static final byte N        = 7;
    public static final byte O        = 8;
    public static final byte F        = 9;
    public static final byte Ne       = 10;
    public static final byte Na       = 11;
    public static final byte Mg       = 12;
    public static final byte Al       = 13;
    public static final byte Si       = 14;
    public static final byte P        = 15;
    public static final byte S        = 16;
    public static final byte Cl       = 17;
    public static final byte Ar       = 18;
    public static final byte K        = 19;
    public static final byte Ca       = 20;
    public static final byte Sc       = 21;
    public static final byte Ti       = 22;
    public static final byte V        = 23;
    public static final byte Cr       = 24;
    public static final byte Mn       = 25;
    public static final byte Fe       = 26;
    public static final byte Co       = 27;
    public static final byte Ni       = 28;
    public static final byte Cu       = 29;
    public static final byte Zn       = 30;
    public static final byte Ga       = 31;
    public static final byte Ge       = 32;
    public static final byte As       = 33;
    public static final byte Se       = 34;
    public static final byte Br       = 35;
    public static final byte Kr       = 36;
    public static final byte Rb       = 37;
    public static final byte Sr       = 38;
    public static final byte Y        = 39;
    public static final byte Zr       = 40;
    public static final byte Nb       = 41;
    public static final byte Mo       = 42;
    public static final byte Tc       = 43;
    public static final byte Ru       = 44;
    public static final byte Rh       = 45;
    public static final byte Pd       = 46;
    public static final byte Ag       = 47;
    public static final byte Cd       = 48;
    public static final byte In       = 49;
    public static final byte Sn       = 50;
    public static final byte Sb       = 51;
    public static final byte Te       = 52;
    public static final byte I        = 53;
    public static final byte Xe       = 54;
    public static final byte Cs       = 55;
    public static final byte Ba       = 56;
    public static final byte La       = 57;
    public static final byte Ce       = 58;
    public static final byte Pr       = 59;
    public static final byte Nd       = 60;
    public static final byte Pm       = 61;
    public static final byte Sm       = 62;
    public static final byte Eu       = 63;
    public static final byte Gd       = 64;
    public static final byte Tb       = 65;
    public static final byte Dy       = 66;
    public static final byte Ho       = 67;
    public static final byte Er       = 68;
    public static final byte Tm       = 69;
    public static final byte Yb       = 70;
    public static final byte Lu       = 71;
    public static final byte Hf       = 72;
    public static final byte Ta       = 73;
    public static final byte W        = 74;
    public static final byte Re       = 75;
    public static final byte Os       = 76;
    public static final byte Ir       = 77;
    public static final byte Pt       = 78;
    public static final byte Au       = 79;
    public static final byte Hg       = 80;
    public static final byte Tl       = 81;
    public static final byte Pb       = 82;
    public static final byte Bi       = 83;
    public static final byte Po       = 84;
    public static final byte At       = 85;
    public static final byte Rn       = 86;
    public static final byte Fr       = 87;
    public static final byte Ra       = 88;
    public static final byte Ac       = 89;
    public static final byte Th       = 90;
    public static final byte Pa       = 91;
    public static final byte U        = 92;
    public static final byte Np       = 93;
    public static final byte Pu       = 94;
    public static final byte Am       = 95;
    public static final byte Cm       = 96;
    public static final byte Bk       = 97;
    public static final byte Cf       = 98;
    public static final byte Es       = 99;
    public static final byte Fm       = 100;
    public static final byte Md       = 101;
    public static final byte No       = 102;
    public static final byte Lr       = 103;
    public static final byte Rf       = 104;
    public static final byte Db       = 105;
    public static final byte Sg       = 106;
    public static final byte Bh       = 107;
    public static final byte Hs       = 108;
    public static final byte Mt       = 109;
    public static final byte Ds       = 110;
    public static final byte Rg       = 111;
    public static final byte Cn       = 112;
    public static final byte Nh       = 113;
    public static final byte Fl       = 114;
    public static final byte Mc       = 115;
    public static final byte Lv       = 116;
    public static final byte Ts       = 117;
    public static final byte Og       = 118;

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
    public Integer getAtomicNumber();

    /**
     * Sets the atomic number of this element.
     *
     * @param   atomicNumber The atomic mass to be assigned to this element
     *
     * @see    #getAtomicNumber
     */
    public void setAtomicNumber(Integer atomicNumber);

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    public String getSymbol();

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    public void setSymbol(String symbol);

}

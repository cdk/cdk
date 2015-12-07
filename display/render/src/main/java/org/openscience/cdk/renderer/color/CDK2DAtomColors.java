/* Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.renderer.color;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.config.Elements;
import org.openscience.cdk.interfaces.IAtom;

import static org.openscience.cdk.config.Elements.Aluminium;
import static org.openscience.cdk.config.Elements.Argon;
import static org.openscience.cdk.config.Elements.Barium;
import static org.openscience.cdk.config.Elements.Beryllium;
import static org.openscience.cdk.config.Elements.Boron;
import static org.openscience.cdk.config.Elements.Bromine;
import static org.openscience.cdk.config.Elements.Caesium;
import static org.openscience.cdk.config.Elements.Calcium;
import static org.openscience.cdk.config.Elements.Carbon;
import static org.openscience.cdk.config.Elements.Chlorine;
import static org.openscience.cdk.config.Elements.Fluorine;
import static org.openscience.cdk.config.Elements.Francium;
import static org.openscience.cdk.config.Elements.Helium;
import static org.openscience.cdk.config.Elements.Hydrogen;
import static org.openscience.cdk.config.Elements.Iodine;
import static org.openscience.cdk.config.Elements.Iron;
import static org.openscience.cdk.config.Elements.Krypton;
import static org.openscience.cdk.config.Elements.Lithium;
import static org.openscience.cdk.config.Elements.Magnesium;
import static org.openscience.cdk.config.Elements.Neon;
import static org.openscience.cdk.config.Elements.Nitrogen;
import static org.openscience.cdk.config.Elements.Oxygen;
import static org.openscience.cdk.config.Elements.Phosphorus;
import static org.openscience.cdk.config.Elements.Potassium;
import static org.openscience.cdk.config.Elements.Radium;
import static org.openscience.cdk.config.Elements.Rubidium;
import static org.openscience.cdk.config.Elements.Silver;
import static org.openscience.cdk.config.Elements.Sodium;
import static org.openscience.cdk.config.Elements.Strontium;
import static org.openscience.cdk.config.Elements.Sulfur;
import static org.openscience.cdk.config.Elements.Titanium;
import static org.openscience.cdk.config.Elements.Unknown;
import static org.openscience.cdk.config.Elements.Xenon;

/**
 * Gives a short table of atom colors for 2D display. The coloring is loosely
 * based on Jmol CPK.
 *
 * The internal color map can be modified by invoking the set method. For convenience the set method
 * returns the colorer instance for chaining.
 * 
 * <pre>{@code
 * IAtomColorer colorer = new CDK2DAtomColors().set("H", Color.LIGHT_GRAY)
 *                                             .set("O", Color.RED.lighter());
 * }</pre>
 *
 * @cdk.module render
 * @cdk.githash
 * @see <a href="http://en.wikipedia.org/wiki/CPK_coloring">CPK coloring</a>
 * @see <a href="http://jmol.sourceforge.net/jscolors/">Jmol, Colors</a>
 */
public class CDK2DAtomColors implements IAtomColorer, java.io.Serializable {

    private static final long  serialVersionUID = 6712994043820219426L;

    private static final Color hexD9FFFF = new Color(0xD9FFFF);
    private static final Color hexCC80FF = new Color(0xCC80FF);
    private static final Color hexC2FF00 = new Color(0xC2FF00);
    private static final Color hexFFB5B5 = new Color(0xFFB5B5);
    private static final Color hex3050F8 = new Color(0x3050F8);
    private static final Color hexFF0D0D = new Color(0xFF0D0D);
    private static final Color hex90E050 = new Color(0x90E050);
    private static final Color hexB3E3F5 = new Color(0xB3E3F5);
    private static final Color hexAB5CF2 = new Color(0xAB5CF2);
    private static final Color hex8AFF00 = new Color(0x8AFF00);
    private static final Color hexBFA6A6 = new Color(0xBFA6A6);
    private static final Color hexF0C8A0 = new Color(0xF0C8A0);
    private static final Color hexFF8000 = new Color(0xFF8000);
    private static final Color hexFFFF30 = new Color(0xFFFF30);
    private static final Color hex1FF01F = new Color(0x1FF01F);
    private static final Color hex80D1E3 = new Color(0x80D1E3);
    private static final Color hex8F40D4 = new Color(0x8F40D4);
    private static final Color hex3DFF00 = new Color(0x3DFF00);
    private static final Color hexE6E6E6 = new Color(0xE6E6E6);
    private static final Color hexBFC2C7 = new Color(0xBFC2C7);
    private static final Color hexA6A6AB = new Color(0xA6A6AB);
    private static final Color hex8A99C7 = new Color(0x8A99C7);
    private static final Color hex9C7AC7 = new Color(0x9C7AC7);
    private static final Color hexE06633 = new Color(0xE06633);
    private static final Color hexF090A0 = new Color(0xF090A0);
    private static final Color hex50D050 = new Color(0x50D050);
    private static final Color hexC88033 = new Color(0xC88033);
    private static final Color hex7D80B0 = new Color(0x7D80B0);
    private static final Color hexC28F8F = new Color(0xC28F8F);
    private static final Color hex668F8F = new Color(0x668F8F);
    private static final Color hexBD80E3 = new Color(0xBD80E3);
    private static final Color hexFFA100 = new Color(0xFFA100);
    private static final Color hexA62929 = new Color(0xA62929);
    private static final Color hex5CB8D1 = new Color(0x5CB8D1);
    private static final Color hex702EB0 = new Color(0x702EB0);
    private static final Color hex00FF00 = new Color(0x00FF00);
    private static final Color hex94FFFF = new Color(0x94FFFF);
    private static final Color hex94E0E0 = new Color(0x94E0E0);
    private static final Color hex73C2C9 = new Color(0x73C2C9);
    private static final Color hex54B5B5 = new Color(0x54B5B5);
    private static final Color hex3B9E9E = new Color(0x3B9E9E);
    private static final Color hex248F8F = new Color(0x248F8F);
    private static final Color hex0A7D8C = new Color(0x0A7D8C);
    private static final Color hex006985 = new Color(0x006985);
    private static final Color hexC0C0C0 = new Color(0xC0C0C0);
    private static final Color hexFFD98F = new Color(0xFFD98F);
    private static final Color hexA67573 = new Color(0xA67573);
    private static final Color hex668080 = new Color(0x668080);
    private static final Color hex9E63B5 = new Color(0x9E63B5);
    private static final Color hexD47A00 = new Color(0xD47A00);
    private static final Color hex940094 = new Color(0x940094);
    private static final Color hex429EB0 = new Color(0x429EB0);
    private static final Color hex57178F = new Color(0x57178F);
    private static final Color hex00C900 = new Color(0x00C900);
    private static final Color hex70D4FF = new Color(0x70D4FF);
    private static final Color hexFFFFC7 = new Color(0xFFFFC7);
    private static final Color hexD9FFC7 = new Color(0xD9FFC7);
    private static final Color hexC7FFC7 = new Color(0xC7FFC7);
    private static final Color hexA3FFC7 = new Color(0xA3FFC7);
    private static final Color hex8FFFC7 = new Color(0x8FFFC7);
    private static final Color hex61FFC7 = new Color(0x61FFC7);
    private static final Color hex45FFC7 = new Color(0x45FFC7);
    private static final Color hex30FFC7 = new Color(0x30FFC7);
    private static final Color hex1FFFC7 = new Color(0x1FFFC7);
    private static final Color hex00FF9C = new Color(0x00FF9C);
    private static final Color hex00E675 = new Color(0x00E675);
    private static final Color hex00D452 = new Color(0x00D452);
    private static final Color hex00BF38 = new Color(0x00BF38);
    private static final Color hex00AB24 = new Color(0x00AB24);
    private static final Color hex4DC2FF = new Color(0x4DC2FF);
    private static final Color hex4DA6FF = new Color(0x4DA6FF);
    private static final Color hex2194D6 = new Color(0x2194D6);
    private static final Color hex267DAB = new Color(0x267DAB);
    private static final Color hex266696 = new Color(0x266696);
    private static final Color hex175487 = new Color(0x175487);
    private static final Color hexD0D0E0 = new Color(0xD0D0E0);
    private static final Color hexFFD123 = new Color(0xFFD123);
    private static final Color hexB8B8D0 = new Color(0xB8B8D0);
    private static final Color hexA6544D = new Color(0xA6544D);
    private static final Color hex575961 = new Color(0x575961);
    private static final Color hex9E4FB5 = new Color(0x9E4FB5);
    private static final Color hexAB5C00 = new Color(0xAB5C00);
    private static final Color hex754F45 = new Color(0x754F45);
    private static final Color hex428296 = new Color(0x428296);
    private static final Color hex420066 = new Color(0x420066);
    private static final Color hex007D00 = new Color(0x007D00);
    private static final Color hex70ABFA = new Color(0x70ABFA);
    private static final Color hex00BAFF = new Color(0x00BAFF);
    private static final Color hex00A1FF = new Color(0x00A1FF);
    private static final Color hex008FFF = new Color(0x008FFF);
    private static final Color hex0080FF = new Color(0x0080FF);
    private static final Color hex006BFF = new Color(0x006BFF);
    private static final Color hex545CF2 = new Color(0x545CF2);
    private static final Color hex785CE3 = new Color(0x785CE3);
    private static final Color hex8A4FE3 = new Color(0x8A4FE3);
    private static final Color hexA136D4 = new Color(0xA136D4);
    private static final Color hexB31FD4 = new Color(0xB31FD4);
    private static final Color hexB31FBA = new Color(0xB31FBA);
    private static final Color hexB30DA6 = new Color(0xB30DA6);
    private static final Color hexBD0D87 = new Color(0xBD0D87);
    private static final Color hexC70066 = new Color(0xC70066);
    private static final Color hexCC0059 = new Color(0xCC0059);
    private static final Color hexD1004F = new Color(0xD1004F);
    private static final Color hexD90045 = new Color(0xD90045);
    private static final Color hexE00038 = new Color(0xE00038);
    private static final Color hexE6002E = new Color(0xE6002E);
    private static final Color hexEB0026 = new Color(0xEB0026);

    /**
     * {@inheritDoc }
     */
    @Override
    public Color getAtomColor(IAtom atom) {
        return getAtomColor(atom, hexB31FBA);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Color getAtomColor(IAtom atom, Color defaultColor) {
        Elements elem = Elements.ofString(atom.getSymbol());
        if (elem == Unknown)
            elem = Elements.ofNumber(atom.getAtomicNumber());
        switch (elem) {
            case Helium:
                return hexD9FFFF;
            case Lithium:
                return hexCC80FF;
            case Beryllium:
                return hexC2FF00;
            case Boron:
                return hexFFB5B5;
            case Hydrogen:
            case Carbon:
            case Unknown:
                return Color.BLACK;
            case Nitrogen:
                return hex3050F8;
            case Oxygen:
                return hexFF0D0D;
            case Fluorine:
                return hex90E050;
            case Neon:
                return hexB3E3F5;
            case Sodium:
                return hexAB5CF2;
            case Magnesium:
                return hex8AFF00;
            case Aluminium:
                return hexBFA6A6;
            case Silicon:
                return hexF0C8A0;
            case Phosphorus:
                return hexFF8000;
            case Sulfur:
                return hexFFFF30;
            case Chlorine:
                return hex1FF01F;
            case Argon:
                return hex80D1E3;
            case Potassium:
                return hex8F40D4;
            case Calcium:
                return hex3DFF00;
            case Scandium:
                return hexE6E6E6;
            case Titanium:
                return hexBFC2C7;
            case Vanadium:
                return hexA6A6AB;
            case Chromium:
                return hex8A99C7;
            case Manganese:
                return hex9C7AC7;
            case Iron:
                return hexE06633;
            case Cobalt:
                return hexF090A0;
            case Nickel:
                return hex50D050;
            case Copper:
                return hexC88033;
            case Zinc:
                return hex7D80B0;
            case Gallium:
                return hexC28F8F;
            case Germanium:
                return hex668F8F;
            case Arsenic:
                return hexBD80E3;
            case Selenium:
                return hexFFA100;
            case Bromine:
                return hexA62929;
            case Krypton:
                return hex5CB8D1;
            case Rubidium:
                return hex702EB0;
            case Strontium:
                return hex00FF00;
            case Yttrium:
                return hex94FFFF;
            case Zirconium:
                return hex94E0E0;
            case Niobium:
                return hex73C2C9;
            case Molybdenum:
                return hex54B5B5;
            case Technetium:
                return hex3B9E9E;
            case Ruthenium:
                return hex248F8F;
            case Rhodium:
                return hex0A7D8C;
            case Palladium:
                return hex006985;
            case Silver:
                return hexC0C0C0;
            case Cadmium:
                return hexFFD98F;
            case Indium:
                return hexA67573;
            case Tin:
                return hex668080;
            case Antimony:
                return hex9E63B5;
            case Tellurium:
                return hexD47A00;
            case Iodine:
                return hex940094;
            case Xenon:
                return hex429EB0;
            case Caesium:
                return hex57178F;
            case Barium:
                return hex00C900;
            case Lanthanum:
                return hex70D4FF;
            case Cerium:
                return hexFFFFC7;
            case Praseodymium:
                return hexD9FFC7;
            case Neodymium:
                return hexC7FFC7;
            case Promethium:
                return hexA3FFC7;
            case Samarium:
                return hex8FFFC7;
            case Europium:
                return hex61FFC7;
            case Gadolinium:
                return hex45FFC7;
            case Terbium:
                return hex30FFC7;
            case Dysprosium:
                return hex1FFFC7;
            case Holmium:
                return hex00FF9C;
            case Erbium:
                return hex00E675;
            case Thulium:
                return hex00D452;
            case Ytterbium:
                return hex00BF38;
            case Lutetium:
                return hex00AB24;
            case Hafnium:
                return hex4DC2FF;
            case Tantalum:
                return hex4DA6FF;
            case Tungsten:
                return hex2194D6;
            case Rhenium:
                return hex267DAB;
            case Osmium:
                return hex266696;
            case Iridium:
                return hex175487;
            case Platinum:
                return hexD0D0E0;
            case Gold:
                return hexFFD123;
            case Mercury:
                return hexB8B8D0;
            case Thallium:
                return hexA6544D;
            case Lead:
                return hex575961;
            case Bismuth:
                return hex9E4FB5;
            case Polonium:
                return hexAB5C00;
            case Astatine:
                return hex754F45;
            case Radon:
                return hex428296;
            case Francium:
                return hex420066;
            case Radium:
                return hex007D00;
            case Actinium:
                return hex70ABFA;
            case Thorium:
                return hex00BAFF;
            case Protactinium:
                return hex00A1FF;
            case Uranium:
                return hex008FFF;
            case Neptunium:
                return hex0080FF;
            case Plutonium:
                return hex006BFF;
            case Americium:
                return hex545CF2;
            case Curium:
                return hex785CE3;
            case Berkelium:
                return hex8A4FE3;
            case Californium:
                return hexA136D4;
            case Einsteinium:
                return hexB31FD4;
            case Fermium:
                return hexB31FBA;
            case Mendelevium:
                return hexB30DA6;
            case Nobelium:
                return hexBD0D87;
            case Lawrencium:
                return hexC70066;
            case Rutherfordium:
                return hexCC0059;
            case Dubnium:
                return hexD1004F;
            case Seaborgium:
                return hexD90045;
            case Bohrium:
                return hexE00038;
            case Hassium:
                return hexE6002E;
            case Meitnerium:
                return hexEB0026;
            default:
                return defaultColor;
        }
    }
}

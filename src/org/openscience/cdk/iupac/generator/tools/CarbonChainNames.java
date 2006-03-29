/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.iupac.generator.tools;


/**
 * This class is used the name carbon chains.
 *
 * @cdk.module experimental
 *
 * @author Egon Willighagen
 */
public class CarbonChainNames {

    /**
     * Returns the language indifferent part of
     * a carbon chain name.
     *
     * @param length the length of the chain
     */
    public static String getName(int length) {
        String name = null;
        switch (length) {
            case 1:
                name = "meth";
                break;
            case 2:
                name = "eth";
                break;
            case 3:
                name = "prop";
                break;
            case 4:
                name = "but";
                break;
            case 5:
                name = "pent";
                break;
            case 6:
                name = "hex";
                break;
            case 7:
                name = "hept";
                break;
            case 8:
                name = "oct";
                break;
            case 9:
                name = "non";
                break;
            case 10:
                name = "dec";
                break;
            case 11:
                name = "undec";
                break;
            case 20:
                name = "eicos";
                break;
            case 21:
                name = "heneicos";
                break;
            // end of list of unregular names
            default:
                int tiental = (int)length/10;
                String base = null;
                switch (tiental) {
                    case 1:
                        base = "dec";
                        break;
                    case 2:
                        base = "cos";
                        break;
                    case 3:
                        base = "triacont";
                        break;
                    case 4:
                        base = "tetracont";
                        break;
                    case 5:
                        base = "pentacont";
                        break;
                    case 6:
                        base = "hexzcont";
                        break;
                    case 7:
                        base = "heptacont";
                        break;
                    case 8:
                        base = "octacont";
                        break;
                    case 9:
                        base = "nonacont";
                        break;
                    case 10:
                        base = "hect";
                        break;
                }
                if (base != null) {
                    int rest = length - tiental*10;
                    switch (rest) {
                        case 1:
                            name = "hen" + base;
                            break;
                        case 2:
                            name = "do" + base;
                            break;
                        case 3:
                            name = "tri" + base;
                            break;
                        case 4:
                            name = "tetra" + base;
                            break;
                        case 5:
                            name = "penta" + base;
                            break;
                        case 6:
                            name = "hexa" + base;
                            break;
                        case 7:
                            name = "hepta" + base;
                            break;
                        case 8:
                            name = "octa" + base;
                            break;
                        case 9:
                            name = "nona" + base;
                            break;
                        case 0:
                            name = base;
                            break;
                    }
                } else {
                    // cannot determine name
                }
                break;
        }
        return name;
    }
}

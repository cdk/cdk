/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman {asad@ebi.ac.uk}
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
package org.openscience.cdk.smsd.global;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Class that holds information, if the MCS search
 * would be bond sensitive or sensitive.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.global.BondTypeTest")
public class BondType {

    private static BondType instance = null;
    private boolean bondSensitive = false;

    /**
     * Gent singleton pattern instance
     * @return instance of Bond type class
     */
    @TestMethod("testGetInstance")
    public static synchronized BondType getInstance() {
        if (instance == null) {
            // it's ok, we can call this constructor
            instance = new BondType();
        }
        return instance;
    }

    protected BondType() {
        bondSensitive = false;
    }

    /**
     * set true if bond sensitive search else false
     * @param isBondSensitive (set true if bond sensitive else false)
     */
    @TestMethod("testSetBondSensitiveFlag")
    public void setBondSensitiveFlag(boolean isBondSensitive) {
        this.bondSensitive = isBondSensitive;
    }

    /**
     * Return true if its a bond sensitive search else false
     * @return true if bond sensitive else false
     */
    @TestMethod("testIsBondSensitive")
    public boolean isBondSensitive() {
        return bondSensitive;
    }
}

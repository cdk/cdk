/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * This class tests the matching of atom types defined in the
 * CDK atom type list, starting from SMILES strings.
 *
 * @cdk.module test-core
 */
public class CDKAtomTypeMatcherSMILESTest extends AbstractCDKAtomTypeTest {

    private static SmilesParser smilesParser;
    private static CDKAtomTypeMatcher atomTypeMatcher;
    
    @BeforeClass public static void setup() {
        smilesParser =
            new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
        atomTypeMatcher =
            CDKAtomTypeMatcher.getInstance(
                NoNotificationChemObjectBuilder.getInstance()
            );
    }

    /**
     * @cdk.bug 2826961
     */
    @Test public void testIdenticalTypes() throws Exception {
        String smiles1 = "CN(C)CCC1=CNC2=C1C=C(C=C2)CC1NC(=O)OC1";
        String smiles2 = "CN(C)CCC1=CNc2c1cc(cc2)CC1NC(=O)OC1";
        
        IMolecule mol1 = smilesParser.parseSmiles(smiles1);
        IMolecule mol2 = smilesParser.parseSmiles(smiles2);
        
        Assert.assertEquals(mol1.getAtomCount(), mol2.getAtomCount());
        Assert.assertEquals(mol1.getBondCount(), mol2.getBondCount());

        IAtomType[] types1 = atomTypeMatcher.findMatchingAtomType(mol1);
        IAtomType[] types2 = atomTypeMatcher.findMatchingAtomType(mol2);
        for (int i=0; i<mol1.getAtomCount(); i++) {
            Assert.assertEquals(
                types1[i].getAtomTypeName(),
                types2[i].getAtomTypeName()
            );
        }
    }

}

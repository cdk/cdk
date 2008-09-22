/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomDiff;
import org.xmlcml.cml.base.CMLElement;

/**
 * @cdk.module test-libiocml
 */
public class CDKRoundTripTest extends NewCDKTestCase {

    private static IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    private static Convertor convertor = new Convertor(true, null);
    
    @Test public void testAtom() {
        IAtom atom = builder.newAtom();
        String cmlString = createCMLFragment(atom);
        System.out.println("CML: " + cmlString);
        IChemObject cdkObject = createMolecule(cmlString);
        Assert.assertTrue(cdkObject instanceof IMolecule);
        IMolecule mol = (IMolecule)cdkObject;
        Assert.assertEquals(1, mol.getAtomCount());
        IAtom atomCopy = mol.getAtom(0); 
        String difference = AtomDiff.diff(atom, atomCopy);;
        Assert.assertEquals("Found non-zero diff: " + difference, 0, difference.length());
    }

    private IChemObject createMolecule( String cmlString ) {
        CMLReader cmlReader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        try {
            return cmlReader.read(builder.newMolecule());
        } catch (CDKException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private String createCMLFragment(IChemObject object) {
        if (object instanceof IAtom) { 
            CMLElement element = convertor.cdkAtomToCMLAtom((IAtom)object);
            return element.toXML();
        }
        return "<!-- failed -->";
    }
    
}

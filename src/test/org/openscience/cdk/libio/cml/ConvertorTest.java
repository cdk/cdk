/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Stefan Kuhn <shk3@users.sf.net>
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
 * 
 */
package org.openscience.cdk.libio.cml;

import nu.xom.Document;
import nu.xom.Serializer;
import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.libio.md.MDMolecule;
import org.xmlcml.cml.element.CMLAtom;
import org.xmlcml.cml.element.CMLBond;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @cdk.module test-libiocml
 */
public class ConvertorTest extends CDKTestCase {

    /**
     * @cdk.bug 1748257
     */
    @Test public void testBug1748257 () {
    	
    	MDMolecule mol=new MDMolecule();
        mol.addAtom(new Atom("C")); // 0
        mol.addAtom(new Atom("C")); // 1
        mol.addAtom(new Atom("H")); // 2
        mol.addAtom(new Atom("H")); // 3
        mol.addAtom(new Atom("H")); // 4
        mol.addAtom(new Atom("H")); // 5

        mol.addBond(0, 1, IBond.Order.DOUBLE); // 1
        mol.addBond(2, 0, IBond.Order.SINGLE); // 3
        mol.addBond(3, 0, IBond.Order.SINGLE); // 4
        mol.addBond(4, 1, IBond.Order.SINGLE); // 5
        mol.addBond(5, 1, IBond.Order.SINGLE); // 6
        
        Convertor convertor=new Convertor(false,"");
        CMLAtom cmlatom=convertor.cdkAtomToCMLAtom(mol,mol.getAtom(2));
        Assert.assertEquals(cmlatom.getHydrogenCount(),0);
    }
    

    @Test
    public void testCdkBondToCMLBond_Wedge() throws IOException {


        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IBond bond = builder.newInstance(IBond.class);
        bond.setOrder(IBond.Order.SINGLE);
        bond.setStereo(IBond.Stereo.UP);

        Convertor convertor = new Convertor(true, null);
        CMLBond cmlBond = convertor.cdkBondToCMLBond(bond);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Serializer serializer = new Serializer(out, "UTF-8");

        serializer.write(new Document(cmlBond));

        out.close();

        String expected = "<bondStereo dictRef=\"cml:W\">W</bondStereo>";
        String actual = new String(out.toByteArray());

        Assert.assertTrue(actual.contains(expected));

    }

    @Test
    public void testCdkBondToCMLBond_Hatch() throws IOException {


        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IBond bond = builder.newInstance(IBond.class);
        bond.setOrder(IBond.Order.SINGLE);
        bond.setStereo(IBond.Stereo.DOWN);

        Convertor convertor = new Convertor(true, null);
        CMLBond cmlBond = convertor.cdkBondToCMLBond(bond);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Serializer serializer = new Serializer(out, "UTF-8");

        serializer.write(new Document(cmlBond));

        out.close();

        String expected = "<bondStereo dictRef=\"cml:H\">H</bondStereo>";
        String actual = new String(out.toByteArray());

        Assert.assertTrue(actual.contains(expected));


    }


}

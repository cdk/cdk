/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.factory;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainerSet;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * Unit testing for the {@link FragmentMatcher} class.
 * @author     Syed Asad Rahman
 * @author     egonw
 * @cdk.module test-smsd
 */
public class FragmentMatcherTest {

    @Test
    public void testFragmentMatcher() {
        Assert.assertNotNull(
                new FragmentMatcher(
                new AtomContainerSet(), new AtomContainerSet(), false));
    }

    /**
     * Test of searchMCS method, of class FragmentMatcher.
     * @throws InvalidSmilesException
     */
    @Test
    public void testSearchMCS() throws InvalidSmilesException {
        System.out.println("searchMCS");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target1 = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer query1 = sp.parseSmiles("CCCOCC(C)=C");
        IAtomContainer query2 = sp.parseSmiles("C\\C=C/OCC=C");

        IAtomContainerSet source = new AtomContainerSet();
        source.addAtomContainer(query1);
        source.addAtomContainer(query2);
        IAtomContainerSet target = new AtomContainerSet();
        target.addAtomContainer(target1);
        FragmentMatcher instance = new FragmentMatcher(source, target, true);
        instance.searchMCS();
        Assert.assertEquals(2, instance.getAllAtomMapping().size());
        Assert.assertEquals(7, instance.getFirstAtomMapping().size());
        Assert.assertEquals(2, instance.getAllMapping().size());
        Assert.assertEquals(7, instance.getFirstMapping().size());
    }

    /**
     * Test of getAllAtomMapping method, of class FragmentMatcher.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetAllAtomMapping() throws InvalidSmilesException {
        System.out.println("getAllAtomMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target1 = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer query1 = sp.parseSmiles("CCCOCC(C)=C");
        IAtomContainer query2 = sp.parseSmiles("C\\C=C/OCC=C");

        IAtomContainerSet source = new AtomContainerSet();
        source.addAtomContainer(query1);
        source.addAtomContainer(query2);
        IAtomContainerSet target = new AtomContainerSet();
        target.addAtomContainer(target1);
        FragmentMatcher instance = new FragmentMatcher(source, target, true);
        instance.searchMCS();
        Assert.assertEquals(2, instance.getAllAtomMapping().size());
    }

    /**
     * Test of getAllMapping method, of class FragmentMatcher.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetAllMapping() throws InvalidSmilesException {
        System.out.println("getAllMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target1 = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer query1 = sp.parseSmiles("CCCOCC(C)=C");
        IAtomContainer query2 = sp.parseSmiles("C\\C=C/OCC=C");

        IAtomContainerSet source = new AtomContainerSet();
        source.addAtomContainer(query1);
        source.addAtomContainer(query2);
        IAtomContainerSet target = new AtomContainerSet();
        target.addAtomContainer(target1);
        FragmentMatcher instance = new FragmentMatcher(source, target, true);
        instance.searchMCS();
        Assert.assertEquals(2, instance.getAllMapping().size());
    }

    /**
     * Test of getFirstAtomMapping method, of class FragmentMatcher.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstAtomMapping() throws InvalidSmilesException {
        System.out.println("getFirstAtomMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target1 = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer query1 = sp.parseSmiles("CCCOCC(C)=C");
        IAtomContainer query2 = sp.parseSmiles("C\\C=C/OCC=C");

        IAtomContainerSet source = new AtomContainerSet();
        source.addAtomContainer(query1);
        source.addAtomContainer(query2);
        IAtomContainerSet target = new AtomContainerSet();
        target.addAtomContainer(target1);
        FragmentMatcher instance = new FragmentMatcher(source, target, true);
        instance.searchMCS();
        Assert.assertEquals(7, instance.getFirstAtomMapping().size());
    }

    /**
     * Test of getFirstMapping method, of class FragmentMatcher.
     * @throws InvalidSmilesException
     */
    @Test
    public void testGetFirstMapping() throws InvalidSmilesException {
        System.out.println("getFirstMapping");
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target1 = sp.parseSmiles("C\\C=C/OCC=C");
        IAtomContainer query1 = sp.parseSmiles("CCCOCC(C)=C");
        IAtomContainer query2 = sp.parseSmiles("C\\C=C/OCC=C");

        IAtomContainerSet source = new AtomContainerSet();
        source.addAtomContainer(query1);
        source.addAtomContainer(query2);
        IAtomContainerSet target = new AtomContainerSet();
        target.addAtomContainer(target1);
        FragmentMatcher instance = new FragmentMatcher(source, target, true);
        instance.searchMCS();
        Assert.assertEquals(7, instance.getFirstMapping().size());
    }
}

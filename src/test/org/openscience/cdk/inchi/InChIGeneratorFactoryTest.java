/* Copyright (C)      2006  Sam Adams <sea36@users.sf.net>
 *                    2007  Rajarshi Guha <rajarshi.guha@gmail.com>
 *               2010,2012  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.inchi;

import java.util.ArrayList;
import java.util.List;

import net.sf.jniinchi.INCHI_OPTION;
import net.sf.jniinchi.INCHI_RET;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * @cdk.module test-inchi
 */
public class InChIGeneratorFactoryTest {

	@Test
	public void testGetInstance() throws CDKException {
		InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
		Assert.assertNotNull(factory);
	}

	/**
	 * Because we are not setting any options, we get an Standard InChI.
	 */
    @Test public void testGetInChIGenerator_IAtomContainer() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

	/**
	 * Because we are setting an options, we get a non-standard InChI.
	 */
    @Test public void testGetInChIGenerator_IAtomContainer_String() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, "FixedH");
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

	/**
	 * Because we are setting no option, we get a Standard InChI.
	 */
    @Test public void testGetInChIGenerator_IAtomContainer_NullString() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, (String)null);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1S/ClH/h1H", gen.getInchi());
    }

	/**
	 * Because we are setting an options, we get a non-standard InChI.
	 */
    @Test public void testGetInChIGenerator_IAtomContainer_List() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        List<INCHI_OPTION> options = new ArrayList<INCHI_OPTION>();
        options.add(INCHI_OPTION.FixedH);
        InChIGenerator gen = InChIGeneratorFactory.getInstance().getInChIGenerator(ac, options);
        Assert.assertEquals(gen.getReturnStatus(), INCHI_RET.OKAY);
        Assert.assertEquals("InChI=1/ClH/h1H", gen.getInchi());
    }

	/**
	 * Because we are setting an options, we get a non-standard InChI.
	 */
    @Test(expected=IllegalArgumentException.class)
    public void testGetInChIGenerator_IAtomContainer_NullList() throws Exception {
        IAtomContainer ac = new AtomContainer();
        IAtom a = new Atom("Cl");
        a.setImplicitHydrogenCount(1);
        ac.addAtom(a);
        InChIGeneratorFactory.getInstance().getInChIGenerator(ac, (List<INCHI_OPTION>)null);
    }

    @Test
    public void testGetInChIToStructure_String_IChemObjectBuilder() throws CDKException {
    	InChIToStructure parser = InChIGeneratorFactory.getInstance().getInChIToStructure(
    		"InChI=1/ClH/h1H", DefaultChemObjectBuilder.getInstance()
    	);
    	Assert.assertNotNull(parser);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetInChIToStructure_String_IChemObjectBuilder_NullString() throws CDKException {
    	InChIGeneratorFactory.getInstance().getInChIToStructure(
    		"InChI=1/ClH/h1H", DefaultChemObjectBuilder.getInstance(), (String)null
    	);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetInChIToStructure_String_IChemObjectBuilder_NullList() throws CDKException {
    	InChIGeneratorFactory.getInstance().getInChIToStructure(
    		"InChI=1/ClH/h1H", DefaultChemObjectBuilder.getInstance(), (List<String>)null
    	);
    }

    /**
     * No options set.
     */
    @Test public void testGetInChIToStructure_String_IChemObjectBuilder_List() throws CDKException {
    	InChIToStructure parser = InChIGeneratorFactory.getInstance().getInChIToStructure(
    		"InChI=1/ClH/h1H", DefaultChemObjectBuilder.getInstance(), new ArrayList<String>()
    	);
    	Assert.assertNotNull(parser);
    }
}

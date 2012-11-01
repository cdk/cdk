/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.AbstractAtomParityTest;
import org.openscience.cdk.interfaces.IAtomParity;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;

/**
 * Checks the functionality of the AtomParity class.
 *
 * @cdk.module test-data
 *
 * @see org.openscience.cdk.AtomParity
 */
public class AtomParityTest extends AbstractAtomParityTest {

    @BeforeClass public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test public void testAtomParity_IAtom_IAtom_IAtom_IAtom_IAtom_int() {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Assert.assertNotNull(parity);
    }
    
	@Test public void testClone() throws Exception {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);
        Object clone = parity.clone();
        Assert.assertTrue(clone instanceof AtomParity);
    }    

    @Test public void testClone_SurroundingAtoms() throws Exception {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);

		AtomParity clone = (AtomParity)parity.clone();
		org.openscience.cdk.interfaces.IAtom[] atoms = parity.getSurroundingAtoms();
		org.openscience.cdk.interfaces.IAtom[] atomsClone = clone.getSurroundingAtoms();
        Assert.assertEquals(atoms.length, atomsClone.length);
		for (int f = 0; f < atoms.length; f++) {
			for (int g = 0; g < atomsClone.length; g++) {
				Assert.assertNotNull(atoms[f]);
				Assert.assertNotNull(atomsClone[g]);
				Assert.assertNotSame(atoms[f], atomsClone[g]);
			}
		}        
    }
    
    @Test public void testClone_IAtom() throws Exception {
        IAtom carbon = getNewBuilder().newInstance(IAtom.class,"C");
        carbon.setID("central");
        IAtom carbon1 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon1.setID("c1");
        IAtom carbon2 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon2.setID("c2");
        IAtom carbon3 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon3.setID("c3");
        IAtom carbon4 = getNewBuilder().newInstance(IAtom.class,"C");
        carbon4.setID("c4");
        int parityInt = 1;
        AtomParity parity = new AtomParity(carbon, carbon1, carbon2, carbon3, carbon4, parityInt);

		AtomParity clone = (AtomParity)parity.clone();
        Assert.assertNotSame(parity.getAtom(), clone.getAtom());
    }

    @Test public void testMap_Map_Map() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        IAtomParity original = new AtomParity(c1,
                                              o2,n3,c4,h5,
                                              2);

        // clone the atoms and place in a map
        Map<IAtom,IAtom> mapping = new HashMap<IAtom,IAtom>();
        IAtom c1clone = (IAtom) c1.clone(); mapping.put(c1, c1clone);
        IAtom o2clone = (IAtom) o2.clone(); mapping.put(o2, o2clone);
        IAtom n3clone = (IAtom) n3.clone(); mapping.put(n3, n3clone);
        IAtom c4clone = (IAtom) c4.clone(); mapping.put(c4, c4clone);
        IAtom h5clone = (IAtom) h5.clone(); mapping.put(h5, h5clone);

        // map the existing element a new element
        IAtomParity mapped = original.map(mapping, Collections.EMPTY_MAP);

        Assert.assertThat("mapped chiral atom was the same as the original",
                          mapped.getAtom(), is(not(sameInstance(original.getAtom()))));
        Assert.assertThat("mapped chiral atom was not the clone",
                          mapped.getAtom(), is(sameInstance(c1clone)));

        IAtom[] originalLigands = original.getSurroundingAtoms();
        IAtom[] mappedLigands   = mapped.getSurroundingAtoms();

        Assert.assertThat("first ligand was te same as the original",
                          mappedLigands[0], is(not(sameInstance(originalLigands[0]))));
        Assert.assertThat("first mapped ligand was not the clone",
                          mappedLigands[0], is(sameInstance(o2clone)));
        Assert.assertThat("second ligand was te same as the original",
                          mappedLigands[1], is(not(sameInstance(originalLigands[1]))));
        Assert.assertThat("second mapped ligand was not the clone",
                          mappedLigands[1], is(sameInstance(n3clone)));
        Assert.assertThat("third ligand was te same as the original",
                          mappedLigands[2], is(not(sameInstance(originalLigands[2]))));
        Assert.assertThat("third mapped ligand was not the clone",
                          mappedLigands[2], is(sameInstance(c4clone)));
        Assert.assertThat("forth ligand was te same as the original",
                          mappedLigands[3], is(not(sameInstance(originalLigands[3]))));
        Assert.assertThat("forth mapped ligand was not the clone",
                          mappedLigands[3], is(sameInstance(h5clone)));

        Assert.assertThat("stereo was not mapped",
                          mapped.getParity(), is(original.getParity()));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMap_Null_Map() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        IAtomParity original = new AtomParity(c1,
                                              o2,n3,c4,h5,
                                              2);


        // map the existing element a new element - should through an IllegalArgumentException
        IAtomParity mapped = original.map(null, Collections.EMPTY_MAP);

    }

    @Test
    public void testMap_Map_Map_NullElement() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        IAtomParity original = new AtomParity(null,
                                              null, null, null, null,
                                              0);


        // map the existing element a new element
        IAtomParity mapped = original.map(Collections.EMPTY_MAP, Collections.EMPTY_MAP);

        Assert.assertNull(mapped.getAtom());
        Assert.assertNull(mapped.getSurroundingAtoms()[0]);
        Assert.assertNull(mapped.getSurroundingAtoms()[1]);
        Assert.assertNull(mapped.getSurroundingAtoms()[2]);
        Assert.assertNull(mapped.getSurroundingAtoms()[3]);

    }

    @Test
    public void testMap_Map_Map_EmptyMapping() throws CloneNotSupportedException {

        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o2 = builder.newInstance(IAtom.class, "O");
        IAtom n3 = builder.newInstance(IAtom.class, "N");
        IAtom c4 = builder.newInstance(IAtom.class, "C");
        IAtom h5 = builder.newInstance(IAtom.class, "H");

        // new stereo element
        IAtomParity original = new AtomParity(c1,
                                              o2,n3,c4,h5,
                                              2);


        // map the existing element a new element - should through an IllegalArgumentException
        IAtomParity mapped = original.map(Collections.EMPTY_MAP, Collections.EMPTY_MAP);

        Assert.assertNull(mapped.getAtom());
        Assert.assertNull(mapped.getSurroundingAtoms()[0]);
        Assert.assertNull(mapped.getSurroundingAtoms()[1]);
        Assert.assertNull(mapped.getSurroundingAtoms()[2]);
        Assert.assertNull(mapped.getSurroundingAtoms()[3]);
        Assert.assertNotNull(mapped.getParity());

    }


}

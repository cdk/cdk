/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @cdk.module test-diff
 */
public class AtomContainerDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        IAtomContainer container = mock(IAtomContainer.class);
        when(container.getElectronContainerCount()).thenReturn(1);
        when(container.getElectronContainer(0)).thenReturn(mock(IBond.class));
        String result = AtomContainerDiff.diff(container, container);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {

        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        IBond b1 = mock(IBond.class);
        IBond b2 = mock(IBond.class);

        when(b1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(b2.getOrder()).thenReturn(IBond.Order.DOUBLE);

        when(b1.getAtomCount()).thenReturn(2);
        when(b2.getAtomCount()).thenReturn(2);

        when(b1.getAtom(0)).thenReturn(carbon);
        when(b1.getAtom(1)).thenReturn(carbon);
        when(b2.getAtom(0)).thenReturn(carbon);
        when(b2.getAtom(1)).thenReturn(oxygen);

        IAtomContainer container1 = mock(IAtomContainer.class);
        IAtomContainer container2 = mock(IAtomContainer.class);
        when(container1.getElectronContainerCount()).thenReturn(1);
        when(container2.getElectronContainerCount()).thenReturn(1);
        when(container1.getElectronContainer(0)).thenReturn(b1);
        when(container2.getElectronContainer(0)).thenReturn(b2);

        String result = AtomContainerDiff.diff(container1, container2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "AtomContainerDiff");
        assertContains(result, "BondDiff");
        assertContains(result, "SINGLE/DOUBLE");
        assertContains(result, "AtomDiff");
        assertContains(result, "C/O");
    }

    @Test
    public void testDifference() {
        IAtom carbon = mock(IAtom.class);
        IAtom oxygen = mock(IAtom.class);

        when(carbon.getSymbol()).thenReturn("C");
        when(oxygen.getSymbol()).thenReturn("O");

        IBond b1 = mock(IBond.class);
        IBond b2 = mock(IBond.class);

        when(b1.getOrder()).thenReturn(IBond.Order.SINGLE);
        when(b2.getOrder()).thenReturn(IBond.Order.DOUBLE);

        when(b1.getAtomCount()).thenReturn(2);
        when(b2.getAtomCount()).thenReturn(2);

        when(b1.getAtom(0)).thenReturn(carbon);
        when(b1.getAtom(1)).thenReturn(carbon);
        when(b2.getAtom(0)).thenReturn(carbon);
        when(b2.getAtom(1)).thenReturn(oxygen);

        IAtomContainer container1 = mock(IAtomContainer.class);
        IAtomContainer container2 = mock(IAtomContainer.class);
        when(container1.getElectronContainerCount()).thenReturn(1);
        when(container2.getElectronContainerCount()).thenReturn(1);
        when(container1.getElectronContainer(0)).thenReturn(b1);
        when(container2.getElectronContainer(0)).thenReturn(b2);

        String result = AtomContainerDiff.diff(container1, container2);
        Assert.assertNotNull(result);
    }

    @Ignore("unit test did not test AtomContainerDiff but rather the ability of AtomContainer"
            + "to be serialized. This is already tested in each respective domain module")
    public void testDiffFromSerialized() throws IOException, ClassNotFoundException {
        //        IAtomContainer atomContainer = new AtomContainer();
        //        IBond bond1 = new Bond(new Atom("C"), new Atom("C"));
        //        bond1.setOrder(IBond.Order.SINGLE);
        //        atomContainer.addBond(bond1);
        //
        //        File tmpFile = File.createTempFile("serialized", ".dat");
        //        tmpFile.deleteOnExit();
        //        String objFilename = tmpFile.getAbsolutePath();
        //
        //        FileOutputStream fout = new FileOutputStream(objFilename);
        //        ObjectOutputStream ostream = new ObjectOutputStream(fout);
        //        ostream.writeObject(atomContainer);
        //
        //        ostream.close();
        //        fout.close();
        //
        //        // now read the serialized atomContainer in
        //        FileInputStream fin = new FileInputStream(objFilename);
        //        ObjectInputStream istream = new ObjectInputStream(fin);
        //        Object obj = istream.readObject();
        //
        //        Assert.assertTrue(obj instanceof IAtomContainer);
        //
        //        IAtomContainer newAtomContainer = (IAtomContainer) obj;
        //        String diff = AtomDiff.diff(atomContainer, newAtomContainer);
        //
        //        Assert.assertTrue("There were differences between original and deserialized version!", diff.equals(""));

    }
}

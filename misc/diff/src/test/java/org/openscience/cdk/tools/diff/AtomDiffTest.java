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
import org.openscience.cdk.tools.diff.tree.IDifference;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @cdk.module test-diff
 */
public class AtomDiffTest extends CDKTestCase {

    @Test
    public void testMatchAgainstItself() {
        IAtom atom1 = mock(IAtom.class);
        String result = AtomDiff.diff(atom1, atom1);
        assertZeroLength(result);
    }

    @Test
    public void testDiff() {
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getSymbol()).thenReturn("H");
        when(atom2.getSymbol()).thenReturn("C");

        String result = AtomDiff.diff(atom1, atom2);
        Assert.assertNotNull(result);
        Assert.assertNotSame(0, result.length());
        assertContains(result, "AtomDiff");
        assertContains(result, "H/C");
    }

    @Test
    public void testDifference() {
        IAtom atom1 = mock(IAtom.class);
        IAtom atom2 = mock(IAtom.class);
        when(atom1.getSymbol()).thenReturn("H");
        when(atom2.getSymbol()).thenReturn("C");

        IDifference difference = AtomDiff.difference(atom1, atom2);
        Assert.assertNotNull(difference);
    }

    @Ignore("unit test did not test AtomDiff but rather the ability of AtomContainer"
            + "to be serialized. This is already tested in each respective domain module")
    public void testDiffFromSerialized() throws IOException, ClassNotFoundException {
        //        IAtom atom = new Atom("C");
        //
        //        File tmpFile = File.createTempFile("serialized", ".dat");
        //        tmpFile.deleteOnExit();
        //        String objFilename = tmpFile.getAbsolutePath();
        //
        //        FileOutputStream fout = new FileOutputStream(objFilename);
        //        ObjectOutputStream ostream = new ObjectOutputStream(fout);
        //        ostream.writeObject(atom);
        //
        //        ostream.close();
        //        fout.close();
        //
        //        // now read the serialized atom in
        //        FileInputStream fin = new FileInputStream(objFilename);
        //        ObjectInputStream istream  = new ObjectInputStream(fin);
        //        Object obj = istream.readObject();
        //
        //        Assert.assertTrue(obj instanceof IAtom);
        //
        //        IAtom newAtom = (IAtom) obj;
        //        String diff = AtomDiff.diff(atom, newAtom);
        //
        //        Assert.assertTrue("There were differences between original and deserialized version!", diff.equals(""));

    }
}

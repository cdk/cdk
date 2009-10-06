/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.libio.jena;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNAtom;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomContainerDiff;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * @cdk.module test-iordf
 */
public class ConvertorTest extends CDKTestCase {

    private static IChemObjectBuilder builder =
        NoNotificationChemObjectBuilder.getInstance();

    @Test public void roundtripMolecule() {
        IMolecule mol = new NNMolecule();
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals(0, diff.length());
    }

    @Test public void roundtripAtom() {
        IMolecule mol = new NNMolecule();
        mol.addAtom(new NNAtom("C"));
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

    @Test public void roundtripBond() {
        IMolecule mol = new NNMolecule();
        mol.addAtom(new NNAtom("C"));
        mol.addAtom(new NNAtom("C"));
        mol.addBond(0,1,IBond.Order.DOUBLE);
        Model model = Convertor.molecule2Model(mol);
        IMolecule rtMol = Convertor.model2Molecule(model, builder);
        String diff = AtomContainerDiff.diff(mol, rtMol);
        Assert.assertEquals("Unexpected diff: " + diff, 0, diff.length());
    }

}

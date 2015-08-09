/* Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.openscience.cdk.DefaultChemObjectBuilder;

import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 * @cdk.bug 1627763
 */
public class PartialTChargeMMFF94DescriptorTest extends AtomicDescriptorTest {

    private final double METHOD_ERROR = 0.16;

    private final double[] AtomInChIToMMFF94PartialCharges(String InChI) {

        InChIGeneratorFactory factory = null;

        try {
            factory = InChIGeneratorFactory.getInstance();
        } catch (CDKException e2) {

            e2.printStackTrace();
        }

        InChIToStructure parser = null;
        try {
            parser = factory.getInChIToStructure(InChI, DefaultChemObjectBuilder.getInstance());
        } catch (CDKException e1) {

            e1.printStackTrace();
        }

        IAtomContainer ac = parser.getAtomContainer();
        try {
            addExplicitHydrogens(ac);
        } catch (Exception e) {

            e.printStackTrace();
        }

        MMFF94PartialCharges mmff = new MMFF94PartialCharges();
        try {
            mmff.assignMMFF94PartialCharges(ac);
        } catch (Exception e) {

            e.printStackTrace();
        }

        double[] testResult = new double[ac.getAtomCount()];
        int i = 0;
        for (IAtom atom : ac.atoms()) {

            // System.out.println(atom.getAtomTypeName() + " " +
            // atom.getProperty("MMFF94charge").toString());
            testResult[i] = atom.getProperty("MMFF94charge", Double.class);
            i++;

        }

        return testResult;

    }

    /**
     * Constructor for the PartialTChargeMMFF94DescriptorTest object
     *
     * All values taken from table V of Merck Molecular Force Field. II. Thomas
     * A. Halgren DOI:
     * 10.1002/(SICI)1096-987X(199604)17:5/6<520::AID-JCC2>3.0.CO;2-W
     *
     */
    public PartialTChargeMMFF94DescriptorTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(PartialTChargeMMFF94Descriptor.class);
    }

}

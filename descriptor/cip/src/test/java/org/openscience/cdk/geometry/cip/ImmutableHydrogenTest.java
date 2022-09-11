/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry.cip;

import java.util.Properties;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * @cdk.module test-cip
 */
class ImmutableHydrogenTest extends CDKTestCase {

    // FIXME: think about how to cover all other IAtom methods that are not implemented...

    @Test
    void testExpectedValues() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assertions.assertEquals("H", hydrogen.getSymbol());
        Assertions.assertEquals(1, hydrogen.getAtomicNumber().intValue());
        Assertions.assertEquals(1, hydrogen.getMassNumber().intValue());
    }

    @Test
    void testOverwriteStaticValues() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        hydrogen.setSymbol("C");
        hydrogen.setAtomicNumber(12);
        hydrogen.setMassNumber(13);
        Assertions.assertEquals("H", hydrogen.getSymbol());
        Assertions.assertEquals(1, hydrogen.getAtomicNumber().intValue());
        Assertions.assertEquals(1, hydrogen.getMassNumber().intValue());
    }

    @Test
    void testListenerStuff() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assertions.assertEquals(0, hydrogen.getListenerCount());
        hydrogen.addListener(new IChemObjectListener() {

            @Override
            public void stateChanged(IChemObjectChangeEvent event) {}
        });
        Assertions.assertEquals(0, hydrogen.getListenerCount());
        hydrogen.removeListener(new IChemObjectListener() {

            @Override
            public void stateChanged(IChemObjectChangeEvent event) {}
        });
        Assertions.assertEquals(0, hydrogen.getListenerCount());

        hydrogen.notifyChanged();
        hydrogen.notifyChanged(String::new);

        Assertions.assertFalse(hydrogen.getNotification());
        hydrogen.setNotification(true);
        Assertions.assertFalse(hydrogen.getNotification());
    }

    @Test
    void testReturnsNull() {
        IAtom hydrogen = new ImmutableHydrogen();
        Assertions.assertNull(hydrogen.getCharge());
        Assertions.assertNull(hydrogen.getImplicitHydrogenCount());
        Assertions.assertNull(hydrogen.getPoint2d());
        Assertions.assertNull(hydrogen.getPoint3d());
        Assertions.assertNull(hydrogen.getStereoParity());
        Assertions.assertNull(hydrogen.getAtomTypeName());
        Assertions.assertNull(hydrogen.getBondOrderSum());
        Assertions.assertNull(hydrogen.getCovalentRadius());
        Assertions.assertNull(hydrogen.getFormalCharge());
        Assertions.assertNull(hydrogen.getFormalNeighbourCount());
        Assertions.assertNull(hydrogen.getHybridization());
        Assertions.assertNull(hydrogen.getMaxBondOrder());
        Assertions.assertNull(hydrogen.getValency());
        Assertions.assertNull(hydrogen.getExactMass());
        Assertions.assertNull(hydrogen.getNaturalAbundance());
        Assertions.assertNull(hydrogen.getFlags());
        Assertions.assertNull(hydrogen.getFlagValue());
        Assertions.assertFalse(hydrogen.getFlag(CDKConstants.ISPLACED));
        Assertions.assertNull(hydrogen.getID());
        Assertions.assertNull(hydrogen.getProperties());
        Assertions.assertNull(hydrogen.getProperty(""));
        Assertions.assertNull(hydrogen.getProperty("", String.class));
        Assertions.assertNull(hydrogen.getBuilder());
    }

    @Test
    void testSetIsSilent() throws Exception {
        // because we already test that the matching get methods
        // return null, we only test that set does not throw
        // exceptions
        IAtom hydrogen = new ImmutableHydrogen();
        hydrogen.setCharge(2.0);
        hydrogen.setImplicitHydrogenCount(1);
        hydrogen.setPoint2d(new Point2d(1, 2));
        hydrogen.setPoint3d(new Point3d(2, 3, 4));
        hydrogen.setStereoParity(1);
        hydrogen.setAtomTypeName("foo");
        hydrogen.setBondOrderSum(4.0);
        hydrogen.setCovalentRadius(1.4);
        hydrogen.setFormalCharge(1);
        hydrogen.setFormalNeighbourCount(2);
        hydrogen.setHybridization(Hybridization.PLANAR3);
        hydrogen.setMaxBondOrder(Order.QUADRUPLE);
        hydrogen.setValency(4);
        hydrogen.setExactMass(12.0);
        hydrogen.setNaturalAbundance(100.0);
        hydrogen.setFlags(null);
        hydrogen.setFlag(CDKConstants.ISINRING, true);
        hydrogen.setID("Me");
        hydrogen.addProperties(new Properties());
        hydrogen.setProperties(new Properties());
        hydrogen.setProperty("", "");
        hydrogen.removeProperty("");
        Assertions.assertTrue(true); // to indicate we made it
    }

    @Test
    void testClone() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assertions.assertEquals(hydrogen, hydrogen.clone());
    }
}

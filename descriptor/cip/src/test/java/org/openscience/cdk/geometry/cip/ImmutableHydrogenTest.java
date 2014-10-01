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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;
import org.openscience.cdk.interfaces.IAtomType.Hybridization;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * @cdk.module test-cip
 */
public class ImmutableHydrogenTest extends CDKTestCase {

    // FIXME: think about how to cover all other IAtom methods that are not implemented...

    @Test
    public void testExpectedValues() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assert.assertEquals("H", hydrogen.getSymbol());
        Assert.assertEquals(1, hydrogen.getAtomicNumber().intValue());
        Assert.assertEquals(1, hydrogen.getMassNumber().intValue());
    }

    @Test
    public void testOverwriteStaticValues() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        hydrogen.setSymbol("C");
        hydrogen.setAtomicNumber(12);
        hydrogen.setMassNumber(13);
        Assert.assertEquals("H", hydrogen.getSymbol());
        Assert.assertEquals(1, hydrogen.getAtomicNumber().intValue());
        Assert.assertEquals(1, hydrogen.getMassNumber().intValue());
    }

    @Test
    public void testListenerStuff() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assert.assertEquals(0, hydrogen.getListenerCount());
        hydrogen.addListener(new IChemObjectListener() {

            @Override
            public void stateChanged(IChemObjectChangeEvent event) {}
        });
        Assert.assertEquals(0, hydrogen.getListenerCount());
        hydrogen.removeListener(new IChemObjectListener() {

            @Override
            public void stateChanged(IChemObjectChangeEvent event) {}
        });
        Assert.assertEquals(0, hydrogen.getListenerCount());

        hydrogen.notifyChanged();
        hydrogen.notifyChanged(new IChemObjectChangeEvent() {

            @Override
            public Object getSource() {
                return new String();
            }
        });

        Assert.assertFalse(hydrogen.getNotification());
        hydrogen.setNotification(true);
        Assert.assertFalse(hydrogen.getNotification());
    }

    @Test
    public void testReturnsNull() {
        IAtom hydrogen = new ImmutableHydrogen();
        Assert.assertNull(hydrogen.getCharge());
        Assert.assertNull(hydrogen.getImplicitHydrogenCount());
        Assert.assertNull(hydrogen.getPoint2d());
        Assert.assertNull(hydrogen.getPoint3d());
        Assert.assertNull(hydrogen.getStereoParity());
        Assert.assertNull(hydrogen.getAtomTypeName());
        Assert.assertNull(hydrogen.getBondOrderSum());
        Assert.assertNull(hydrogen.getCovalentRadius());
        Assert.assertNull(hydrogen.getFormalCharge());
        Assert.assertNull(hydrogen.getFormalNeighbourCount());
        Assert.assertNull(hydrogen.getHybridization());
        Assert.assertNull(hydrogen.getMaxBondOrder());
        Assert.assertNull(hydrogen.getValency());
        Assert.assertNull(hydrogen.getExactMass());
        Assert.assertNull(hydrogen.getNaturalAbundance());
        Assert.assertNull(hydrogen.getFlags());
        Assert.assertNull(hydrogen.getFlagValue());
        Assert.assertFalse(hydrogen.getFlag(CDKConstants.ISPLACED));
        Assert.assertNull(hydrogen.getID());
        Assert.assertNull(hydrogen.getProperties());
        Assert.assertNull(hydrogen.getProperty(new String()));
        Assert.assertNull(hydrogen.getProperty(new String(), String.class));
        Assert.assertNull(hydrogen.getBuilder());
    }

    @Test
    public void testSetIsSilent() throws Exception {
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
        hydrogen.setProperty(new String(), new String());
        hydrogen.removeProperty(new String());
        Assert.assertTrue(true); // to indicate we made it
    }

    @Test
    public void testClone() throws Exception {
        IAtom hydrogen = new ImmutableHydrogen();
        Assert.assertEquals(hydrogen, hydrogen.clone());
    }
}

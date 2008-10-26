/* $Revision: 10234 $ $Author: egonw $ $Date: 2008-02-25 08:11:58 -0500 (Mon, 25 Feb 2008) $    
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.diff.ElementDiff;

/**
 * @cdk.module test-extra
 */
public class PeriodicTableElementTest extends CDKTestCase {
    private static PeriodicTableElement periodicTableElement;

    @BeforeClass
    public static void setUp() {
        periodicTableElement = new PeriodicTableElement("C");
    }

    @Test
    public void testConstructor() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNotNull(pte);
        Assert.assertNull(pte.getName());
        Assert.assertNull(pte.getGroup());
        Assert.assertNull(pte.getPaulingEneg());
        Assert.assertNull(pte.getPeriod());
        Assert.assertNull(pte.getPhase());
        Assert.assertNull(pte.getCASid());
        Assert.assertNull(pte.getChemicalSerie());
        Assert.assertNull(pte.getVdwRadius());
        Assert.assertNull(pte.getCovalentRadius());
    }

    @Test
    public void testName() {
        periodicTableElement.setName("carbon");
        Assert.assertEquals("carbon", periodicTableElement.getName());
    }

    @Test
    public void testCASid() {
        periodicTableElement.setCASid("43-6847");
        Assert.assertEquals("43-6847", periodicTableElement.getCASid());
    }

    @Test
    public void testVdw() {
        periodicTableElement.setVdwRadius(1.9);
        Assert.assertEquals(1.9, periodicTableElement.getVdwRadius(), 0.1);
    }


    @Test
    public void testCovalent() {
        periodicTableElement.setCovalentRadius(1.9);
        Assert.assertEquals(1.9, periodicTableElement.getCovalentRadius(), 0.1);
    }

    @Test
    public void testEneg() {
        periodicTableElement.setPaulingEneg(3.4);
        Assert.assertEquals(3.4, periodicTableElement.getPaulingEneg(), 0.1);
    }

    @Test
    public void testPhase() {
        periodicTableElement.setPhase("solid");
        Assert.assertEquals("solid", periodicTableElement.getPhase());
    }

    /**
     * @cdk.bug 2192238
     */
    @Test(expected = CDKException.class)
    public void testGroup() {
        periodicTableElement.setGroup("14");
        Assert.assertEquals("14", periodicTableElement.getGroup());

        periodicTableElement.setGroup("VI");
        periodicTableElement.setGroup("1875");
    }

    @Test
    public void testSeries() {
        periodicTableElement.setChemicalSerie("blah");
        Assert.assertEquals("blah", periodicTableElement.getChemicalSerie());
    }

    @Test
    public void testPeriod() {
        periodicTableElement.setPeriod("4");
        Assert.assertEquals("4", periodicTableElement.getPeriod());
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        PeriodicTableElement cloneElement = (PeriodicTableElement) periodicTableElement.clone();
        String diff = ElementDiff.diff(periodicTableElement, cloneElement);
        Assert.assertEquals("", diff);
    }

    @Test
    public void testString() {
        Assert.assertEquals("PeriodicTableElement(C, AN:0, N:null, CS:null, P:null, G:null, Ph:null, CAS:null, VdW:null, Cov:null, Eneg:null)",
                periodicTableElement.toString());

        periodicTableElement.setGroup("14");
        periodicTableElement.setPhase("solid");
        Assert.assertEquals("PeriodicTableElement(C, AN:0, N:null, CS:null, P:null, G:14, Ph:solid, CAS:null, VdW:null, Cov:null, Eneg:null)",
                periodicTableElement.toString());
    }

    /**
     * @cdk.bug 2192317
     */
    @Test
    public void testConfigure() {
        Element elem = PeriodicTableElement.configure(periodicTableElement);
        Assert.assertEquals("C", elem.getSymbol());
        Assert.assertEquals(6, elem.getAtomicNumber().intValue());
    }

}
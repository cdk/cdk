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
import org.openscience.cdk.tools.diff.ElementDiff;

/**
 * @cdk.module test-extra
 */
public class PeriodicTableElementTest extends CDKTestCase {

    @BeforeClass
    public static void setUp() {
    }

    @Test
    public void testConstructor() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNotNull(pte);
    }
    @Test
    public void testGetName() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getName());
    }
    
    @Test
    public void testSetName() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setName("carbon");
        Assert.assertEquals("carbon", pte.getName());
    }

    @Test
    public void testGetCASid() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getCASid());
    }

    @Test
    public void testSetCASid() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setCASid("43-6847");
        Assert.assertEquals("43-6847", pte.getCASid());
    }

    @Test
    public void testGetVdw() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getVdwRadius());
    }

    @Test
    public void testSetVdw() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setVdwRadius(1.9);
        Assert.assertEquals(1.9, pte.getVdwRadius(), 0.1);
    }
    
    @Test
    public void testGetCovalentRadius() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getCovalentRadius());
    }
    
    @Test
    public void testSetCovalentRadius() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setCovalentRadius(1.9);
        Assert.assertEquals(1.9, pte.getCovalentRadius(), 0.1);
    }

    @Test
    public void testGetEneg() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getPaulingEneg());
    }

    @Test
    public void testSetEneg() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setPaulingEneg(3.4);
        Assert.assertEquals(3.4, pte.getPaulingEneg(), 0.1);
    }

    @Test
    public void testGetPhase() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getPhase());
    }

    @Test
    public void testSetPhase() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setPhase("solid");
        Assert.assertEquals("solid", pte.getPhase());
    }

    @Test
    public void testGetGroup() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getGroup());

    }

    @Test
    public void testSetGroup() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setGroup(14);
        Assert.assertEquals(14, pte.getGroup(),0.1);
    }

    @Test
    public void testGetSeries() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getChemicalSerie());
    }

    @Test
    public void testSetSeries() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setChemicalSerie("blah");
        Assert.assertEquals("blah", pte.getChemicalSerie());
    }

    @Test
    public void testGetPeriod() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertNull(pte.getPeriod());
    }

    @Test
    public void testSetPeriod() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        pte.setPeriod(4);
        Assert.assertEquals(4, pte.getPeriod(),0.01);
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        PeriodicTableElement cloneElement = (PeriodicTableElement) pte.clone();
        String diff = ElementDiff.diff(pte, cloneElement);
        Assert.assertEquals("", diff);
    }

    @Test
    public void testString() {
        PeriodicTableElement pte = new PeriodicTableElement("C");
        Assert.assertEquals("PeriodicTableElement(C, AN:null, N:null, CS:null, P:null, G:null, Ph:null, CAS:null, VdW:null, Cov:null, Eneg:null)",
        		pte.toString());

        pte.setGroup(14);
        pte.setPhase("solid");
        Assert.assertEquals("PeriodicTableElement(C, AN:null, N:null, CS:null, P:null, G:14, Ph:solid, CAS:null, VdW:null, Cov:null, Eneg:null)",
        		pte.toString());
    }
}
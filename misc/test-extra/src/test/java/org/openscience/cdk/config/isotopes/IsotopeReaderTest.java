/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.config.isotopes;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.isotopes.IsotopeReader;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.CDKTestCase;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Checks the functionality of the IsotopeFactory
 *
 * @cdk.module test-extra
 */
public class IsotopeReaderTest extends CDKTestCase {

    @Test
    public void testIsotopeReader_InputStream_IChemObjectBuilder() {
        IsotopeReader reader = new IsotopeReader(new ByteArrayInputStream(new byte[0]), new ChemObject().getBuilder());
        Assert.assertNotNull(reader);
    }

    @Test
    public void testReadIsotopes() {
        IsotopeReader reader = new IsotopeReader(new ByteArrayInputStream(new byte[0]), new ChemObject().getBuilder());
        Assert.assertNotNull(reader);
        List<IIsotope> isotopes = reader.readIsotopes();
        Assert.assertNotNull(isotopes);
        Assert.assertEquals(0, isotopes.size());
    }

    @Test
    public void testReadIsotopes2() {
        String isotopeData = "<?xml version=\"1.0\"?>" + "<list xmlns=\"http://www.xml-cml.org/schema/cml2/core\""
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + "    xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlCore.xsd\">"
                + "" + "    <isotopeList id=\"H\">"
                + "        <isotope id=\"H1\" isotopeNumber=\"1\" elementType=\"H\">"
                + "            <abundance dictRef=\"cdk:relativeAbundance\">100.0</abundance>"
                + "            <scalar dictRef=\"cdk:exactMass\">1.00782504</scalar>"
                + "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" + "        </isotope>"
                + "        <isotope id=\"H2\" isotopeNumber=\"2\" elementType=\"H\">"
                + "            <abundance dictRef=\"cdk:relativeAbundance\">0.015</abundance>"
                + "            <scalar dictRef=\"cdk:exactMass\">2.01410179</scalar>"
                + "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" + "        </isotope>"
                + "        <isotope id=\"D2\" isotopeNumber=\"2\" elementType=\"D\">"
                + "            <abundance dictRef=\"cdk:relativeAbundance\">0.015</abundance>"
                + "            <scalar dictRef=\"cdk:exactMass\">2.01410179</scalar>"
                + "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" + "        </isotope>"
                + "    </isotopeList>" + "</list>";

        IsotopeReader reader = new IsotopeReader(new ByteArrayInputStream(isotopeData.getBytes()),
                new ChemObject().getBuilder());
        Assert.assertNotNull(reader);
        List<IIsotope> isotopes = reader.readIsotopes();
        Assert.assertNotNull(isotopes);
        Assert.assertEquals(3, isotopes.size());
    }

}

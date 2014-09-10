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
 */
package org.openscience.cdk.config.atomtypes;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.atomtypes.AtomTypeReader;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.CDKTestCase;

import java.io.StringReader;
import java.util.List;

/**
 * Checks the functionality of the AtomTypeReader.
 *
 * @cdk.module test-core
 */
public class AtomTypeReaderTest extends CDKTestCase {

    @Test
    public void testAtomTypeReader_Reader() {
        AtomTypeReader reader = new AtomTypeReader(new StringReader(""));
        Assert.assertNotNull(reader);
    }

    @Test
    public void testReadAtomTypes_IChemObjectBuilder() {
        AtomTypeReader reader = new AtomTypeReader(
                new StringReader(
                        "<atomTypeList xmlns=\"http://www.xml-cml.org/schema/cml2/core\"                              "
                                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"                                    "
                                + "  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlAll.xsd\""
                                + "  id=\"mol2\" title=\"MOL2 AtomTypes\">                                                      "
                                + "                                                                                             "
                                + "  <atomType id=\"C.3\" title=\"1\">                                                          "
                                + "    <atom elementType=\"C\"/>                                                                "
                                + "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp3</scalar>               "
                                + "  </atomType>                                                                                "
                                + "  <atomType id=\"C.2\" title=\"2\">                                                          "
                                + "    <atom elementType=\"C\"/>                                                                "
                                + "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp2</scalar>               "
                                + "  </atomType>                                                                                "
                                + "</atomTypeList>"));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(2, types.size());
    }

    @Test
    public void testReadAtomTypes2() {
        String data = "<atomTypeList xmlns=\"http://www.xml-cml.org/schema/cml2/core\"                              "
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"                                    "
                + "  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlAll.xsd\""
                + "  id=\"mol2\" title=\"MOL2 AtomTypes\">                                                      "
                + "                                                                                             "
                + "  <atomType id=\"C.3\" title=\"1\">                                                          "
                + "    <atom elementType=\"C\"/>                                                                "
                + "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp3</scalar>               "
                + "  </atomType>                                                                                "
                + "  <atomType id=\"C.2\" title=\"2\">                                                          "
                + "    <atom elementType=\"C\"/>                                                                "
                + "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp2</scalar>               "
                + "  </atomType>                                                                                "
                + "</atomTypeList>";

        AtomTypeReader reader = new AtomTypeReader(new StringReader(data));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(2, types.size());
    }

    @Test
    public void testReadAtomTypes_CDK() {
        String data = "<atomTypeList xmlns=\"http://www.xml-cml.org/schema/cml2/core\"                              \n"
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"                                    \n"
                + "  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlAll.xsd\"\n"
                + "  id=\"mol2\" title=\"MOL2 AtomTypes\">                                                      \n"
                + "                                                                                             \n"
                + "  <atomType id=\"C.sp\">\n" + "    <atom elementType=\"C\" formalCharge=\"0\">\n"
                + "      <scalar dataType=\"xsd:integer\" dictRef=\"cdk:formalNeighbourCount\">2</scalar>\n"
                + "      <scalar dataType=\"xsd:integer\" dictRef=\"cdk:lonePairCount\">0</scalar>\n"
                + "      <scalar dataType=\"xsd:integer\" dictRef=\"cdk:piBondCount\">2</scalar>\n" + "    </atom>\n"
                + "    <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp1</scalar>\n"
                + "  </atomType>                                                                                "
                + "</atomTypeList>";

        AtomTypeReader reader = new AtomTypeReader(new StringReader(data));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(1, types.size());

        Object object = types.get(0);
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof IAtomType);
        IAtomType atomType = (IAtomType) object;

        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertEquals(IAtomType.Hybridization.SP1, atomType.getHybridization());
        Assert.assertEquals(0, atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertEquals(2, atomType.getProperty(CDKConstants.PI_BOND_COUNT));
    }

    @Test
    public void testReadAtomTypes_FF() {
        String data = "<atomTypeList xmlns=\"http://www.xml-cml.org/schema/cml2/core\"                              \n"
                + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"                                    \n"
                + "  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlAll.xsd\"\n"
                + "  id=\"mol2\" title=\"MOL2 AtomTypes\">                                                      \n"
                + "                                                                                             \n"
                + " <atomType id=\"C\">\n" + "	<!-- for example in CC-->\n"
                + "   <atom elementType=\"C\" formalCharge=\"0\">\n"
                + "     <scalar dataType=\"xsd:double\" dictRef=\"cdk:maxBondOrder\">1.0</scalar>\n"
                + "     <scalar dataType=\"xsd:double\" dictRef=\"cdk:bondOrderSum\">4.0</scalar>\n"
                + "     <scalar dataType=\"xsd:integer\" dictRef=\"cdk:formalNeighbourCount\">4</scalar>\n"
                + "     <scalar dataType=\"xsd:integer\" dictRef=\"cdk:valency\">4</scalar>\n"
                + "     <scalar dataType=\"xsd:string\" dictRef=\"cdk:hybridization\">sp3</scalar>\n"
                + "     <scalar dataType=\"xsd:string\" dictRef=\"cdk:DA\">-</scalar>\n"
                + "     <scalar dataType=\"xsd:string\" dictRef=\"cdk:sphericalMatcher\">[CSP]-[0-4][-]?+;</scalar>\n"
                + "     <scalar dataType=\"xsd:integer\" dictRef=\"cdk:ringSize\">3</scalar>\n"
                + "     <scalar dataType=\"xsd:integer\" dictRef=\"cdk:ringConstant\">3</scalar>\n" + "   </atom>\n"
                + " </atomType>\n" + "</atomTypeList>\n";

        AtomTypeReader reader = new AtomTypeReader(new StringReader(data));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(1, types.size());

        Object object = types.get(0);
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof IAtomType);
        IAtomType atomType = (IAtomType) object;

        Assert.assertEquals("[CSP]-[0-4][-]?+;", atomType.getProperty(CDKConstants.SPHERICAL_MATCHER));
        Assert.assertFalse(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_ACCEPTOR));
        Assert.assertFalse(atomType.getFlag(CDKConstants.IS_HYDROGENBOND_DONOR));

        Assert.assertEquals(3, atomType.getProperty(CDKConstants.PART_OF_RING_OF_SIZE));
        Assert.assertEquals(3, atomType.getProperty(CDKConstants.CHEMICAL_GROUP_CONSTANT));
    }
}

/* Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
 *                    2008  Egon Willighagen <egonw@users.sf.net>
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

import java.io.StringReader;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtomType;

/**
 * Checks the functionality of the AtomTypeReader.
 *
 * @cdk.module test-core
 */
public class OWLAtomTypeReaderTest extends CDKTestCase {

    private final String OWL_CONTENT = "<?xml version=\"1.0\"?>" + "<!DOCTYPE rdf:RDF ["
                                             + "  <!ENTITY rdf  \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >"
                                             + "  <!ENTITY elem \"http://cdk.sf.net/ontologies/elements#\" >"
                                             + "  <!ENTITY at   \"http://cdk.sf.net/ontologies/atomtypes#\" >"
                                             + "  <!ENTITY cdkat \"http://cdk.sf.net/ontologies/atomtypes/cdk#\" >"
                                             + "]>" + "<rdf:RDF xmlns=\"&cdkat;\" xml:base=\"&cdkat;\""
                                             + "         xmlns:at=\"&at;\"" + "         xmlns:elem=\"&elem;\""
                                             + "         xmlns:rdf=\"&rdf;\"" + ">"
                                             + "  <at:AtomType rdf:ID=\"C.sp3.0\">"
                                             + "    <at:categorizedAs rdf:resource=\"&cdkat;C.sp3\"/>"
                                             + "    <at:hasElement rdf:resource=\"&elem;C\"/>"
                                             + "    <at:hybridization rdf:resource=\"&at;sp3\"/>"
                                             + "    <at:formalCharge>0</at:formalCharge>"
                                             + "    <at:lonePairCount>0</at:lonePairCount>"
                                             + "    <at:formalNeighbourCount>4</at:formalNeighbourCount>"
                                             + "    <at:piBondCount>0</at:piBondCount>"
                                             + "    <at:singleElectronCount>0</at:singleElectronCount>"
                                             + "  </at:AtomType>" + "</rdf:RDF>";

    @Test
    public void testAtomTypeReader_Reader() {
        OWLAtomTypeReader reader = new OWLAtomTypeReader(new StringReader(""));
        Assert.assertNotNull(reader);
    }

    @Test
    public void testReadAtomTypes_IChemObjectBuilder() {
        OWLAtomTypeReader reader = new OWLAtomTypeReader(new StringReader(OWL_CONTENT));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(1, types.size());
    }

    @Test
    public void testReadAtomTypes_CDK() {
        OWLAtomTypeReader reader = new OWLAtomTypeReader(new StringReader(OWL_CONTENT));
        Assert.assertNotNull(reader);
        List<IAtomType> types = reader.readAtomTypes(new ChemObject().getBuilder());
        Assert.assertNotNull(types);
        Assert.assertEquals(1, types.size());

        Object object = types.get(0);
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof IAtomType);
        IAtomType atomType = (IAtomType) object;

        Assert.assertEquals("C", atomType.getSymbol());
        Assert.assertEquals("C.sp3.0", atomType.getAtomTypeName());
        Assert.assertEquals(0, atomType.getFormalCharge().intValue());
        Assert.assertEquals(IAtomType.Hybridization.SP3, atomType.getHybridization());
        Assert.assertEquals(4, atomType.getFormalNeighbourCount().intValue());
        Assert.assertEquals(0, atomType.getProperty(CDKConstants.LONE_PAIR_COUNT));
        Assert.assertEquals(0, atomType.getProperty(CDKConstants.PI_BOND_COUNT));
        Assert.assertEquals(0, atomType.getProperty(CDKConstants.SINGLE_ELECTRON_COUNT));
    }

}

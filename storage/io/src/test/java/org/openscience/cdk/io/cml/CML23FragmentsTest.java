/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.io.cml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.dict.DictRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.stereo.TetrahedralChirality;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

/**
 * Atomic tests for reading CML documents. All tested CML strings are valid CML 2.3,
 * as can be determined in cdk/src/org.openscience.cdk/io/cml/cml23TestFramework.xml.
 *
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 */
class CML23FragmentsTest extends CDKTestCase {

    @Test
    void testAtomId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("a1", atom.getID());
    }

    @Test
    void testAtomId3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom = mol.getAtom(1);
        Assertions.assertEquals("a2", atom.getID());
    }

    @Test
    void testAtomElementType3() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1' elementType='C'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
    }

    @Test
    void testMassNumber() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' elementType='C' isotopeNumber='12'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
        Assertions.assertEquals(12, atom.getMassNumber().intValue());
    }

    @Test
    void testAtomicNumber() throws Exception {
        String cmlString = "<molecule><atomArray><atom id='a1' elementType=\"C\"><scalar dataType=\"xsd:integer\" dictRef=\"cdk:atomicNumber\">6</scalar></atom></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
        Assertions.assertEquals(6, atom.getAtomicNumber().intValue());
    }

    @Test
    void testIsotopicMass() throws Exception {
        String cmlString = "<molecule><atomArray><atom id='a1' elementType=\"C\"><scalar dataType=\"xsd:float\" dictRef=\"cdk:isotopicMass\">12.0</scalar></atom></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
        Assertions.assertEquals(12.0, atom.getExactMass(), 0.01);
    }

    @Test
    void testAtomParity() throws Exception {
        String cmlString = "<molecule><atomArray><atom id='a1' elementType='C'><atomParity atomRefs4='a2 a3 a5 a4'>1</atomParity></atom>"
                + "<atom id='a2' elementType='Br'/><atom id='a3' elementType='Cl'/><atom id='a4' elementType='F'/><atom id='a5' elementType='I'/></atomArray>"
                + "<bondArray><bond atomRefs2='a1 a2' order='1'/><bond atomRefs2='a1 a3' order='1'/><bond atomRefs2='a1 a4' order='1'/><bond atomRefs2='a1 a5' order='1'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(5, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertEquals("C", atom.getSymbol());
        IStereoElement stereo = mol.stereoElements().iterator().next();
        Assertions.assertTrue(stereo instanceof TetrahedralChirality);
        Assertions.assertEquals(((TetrahedralChirality) stereo).getChiralAtom().getID(), "a1");
        IAtom[] ligandAtoms = ((TetrahedralChirality) stereo).getLigands();
        Assertions.assertEquals(4, ligandAtoms.length);
        Assertions.assertEquals(ligandAtoms[0].getID(), "a2");
        Assertions.assertEquals(ligandAtoms[1].getID(), "a3");
        Assertions.assertEquals(ligandAtoms[2].getID(), "a5");
        Assertions.assertEquals(ligandAtoms[3].getID(), "a4");
        Assertions.assertEquals(((TetrahedralChirality) stereo).getStereo(), Stereo.CLOCKWISE);
    }

    @Test
    void testBond() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
    }

    @Test
    void testBond4() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' bondID='b1 b2'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        IAtom atom1 = bond.getBegin();
        IAtom atom2 = bond.getEnd();
        Assertions.assertEquals("a1", atom1.getID());
        Assertions.assertEquals("a2", atom2.getID());
        Assertions.assertEquals("b2", mol.getBond(1).getID());
    }

    @Test
    void testBond5() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2 a3'/><bondArray atomRef1='a1 a1' atomRef2='a2 a3' order='1 1'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        Assertions.assertEquals(2, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
        bond = mol.getBond(1);
        Assertions.assertEquals(2, bond.getAtomCount());
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
    }

    @Test
    void testBondId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals("b1", bond.getID());
    }

    @Test
    void testBondStereo() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'><bondStereo dictRef='cml:H'/></bond></bondArray></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        IBond bond = mol.getBond(0);
        Assertions.assertEquals(IBond.Stereo.DOWN, bond.getStereo());
    }

    @Test
    void testBondAromatic() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2'/><bondArray atomRef1='a1' atomRef2='a2' order='A'/></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(Order.SINGLE, bond.getOrder());
        Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void testBondAromatic2() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2'/><bondArray><bond atomRefs='a1 a2' order='2'><bondType dictRef='cdk:aromaticBond'/></bond></bondArray></molecule>";
        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertEquals(1, mol.getBondCount());
        org.openscience.cdk.interfaces.IBond bond = mol.getBond(0);
        Assertions.assertEquals(Order.DOUBLE, bond.getOrder());
        Assertions.assertTrue(bond.getFlag(IChemObject.AROMATIC));
    }

    @Test
    void testList() throws Exception {
        String cmlString = "<list>"
                + "<molecule id='m1'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "<molecule id='m2'><atomArray><atom id='a1'/><atom id='a2'/></atomArray><bondArray><bond id='b1' atomRefs2='a1 a2'/></bondArray></molecule>"
                + "</list>";

        IChemFile chemFile = parseCMLString(cmlString);
        checkForXMoleculeFile(chemFile, 2);
    }

    @Test
    void testCoordinates2D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x2='0.0 0.1' y2='1.2 1.3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNotNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNotNull(mol.getAtom(1).getPoint2d());
        Assertions.assertNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    void testCoordinates3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' x3='0.0 0.1' y3='1.2 1.3' z3='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNull(mol.getAtom(0).getPoint2d());
        Assertions.assertNull(mol.getAtom(1).getPoint2d());
        Assertions.assertNotNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNotNull(mol.getAtom(1).getPoint3d());
    }

    @Test
    void testFractional3D() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray atomID='a1 a2' xFract='0.0 0.1' yFract='1.2 1.3' zFract='2.1 2.5'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        Assertions.assertNull(mol.getAtom(0).getPoint3d());
        Assertions.assertNull(mol.getAtom(1).getPoint3d());
        Assertions.assertNotNull(mol.getAtom(0).getFractionalPoint3d());
        Assertions.assertNotNull(mol.getAtom(1).getFractionalPoint3d());
    }

    @Test
    void testMissing2DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xy2='0.0 0.1'/><atom id='a2'/><atom id='a3' xy2='0.1 0.0'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assertions.assertNotNull(atom1.getPoint2d());
        Assertions.assertNull(atom2.getPoint2d());
        Assertions.assertNotNull(atom3.getPoint2d());
    }

    @Test
    void testMissing3DCoordinates() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1' xyz3='0.0 0.1 0.2'/><atom id='a2'/><atom id='a3' xyz3='0.1 0.0 0.2'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(3, mol.getAtomCount());
        IAtom atom1 = mol.getAtom(0);
        IAtom atom2 = mol.getAtom(1);
        IAtom atom3 = mol.getAtom(2);

        Assertions.assertNotNull(atom1.getPoint3d());
        Assertions.assertNull(atom2.getPoint3d());
        Assertions.assertNotNull(atom3.getPoint3d());
    }

    @Test
    void testMoleculeId() throws Exception {
        String cmlString = "<molecule id='m1'><atomArray><atom id='a1'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals("m1", mol.getID());
    }

    @Test
    void testName() throws Exception {
        String cmlString = "<molecule id='m1'><name>acetic acid</name><atomArray atomID='a1 a2 a3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals("acetic acid", mol.getTitle());
    }

    /**
     * @cdk.bug 2142400
     */
    @Test
    void testHydrogenCount1() throws Exception {
        String cmlString = "<molecule><atomArray><atom id='a1' elementType='C' hydrogenCount='4'/></atomArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(1, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertNotNull(atom);
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(4, atom.getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 2142400
     */
    @Test
    void testHydrogenCount2() throws Exception {
        String cmlString = "<molecule><atomArray>" + "<atom id='a1' elementType='C' hydrogenCount='4'/>"
                + "<atom id='a2' elementType='H'/>" + "<atom id='a3' elementType='H'/>"
                + "<atom id='a4' elementType='H'/>" + "<atom id='a5' elementType='H'/>" + "</atomArray>"
                + "<bondArray>" + "<bond id='b1' atomRefs2='a1 a2' order='S'/>"
                + "<bond id='b2' atomRefs2='a1 a3' order='S'/>" + "<bond id='b3' atomRefs2='a1 a4' order='S'/>"
                + "<bond id='b4' atomRefs2='a1 a5' order='S'/>" + "</bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(5, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertNotNull(atom);
        Assertions.assertEquals("C", atom.getSymbol());
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(0, atom.getImplicitHydrogenCount().intValue());
    }

    /**
     * @cdk.bug 2142400
     */
    @Test
    void testHydrogenCount3() throws Exception {
        String cmlString = "<molecule>" + "<atomArray>" + "<atom id='a1' elementType='C' hydrogenCount='4'/>"
                + "<atom id='a2' elementType='H'/>" + "</atomArray>" + "<bondArray>"
                + "<bond id='b1' atomRefs2='a1 a2' order='S'/>" + "</bondArray>" + "</molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals(2, mol.getAtomCount());
        IAtom atom = mol.getAtom(0);
        Assertions.assertNotNull(atom);
        Assertions.assertNotNull(atom.getImplicitHydrogenCount());
        Assertions.assertEquals(3, atom.getImplicitHydrogenCount().intValue());
    }

    @Test
    void testInChI() throws Exception {
        String cmlString = "<molecule id='m1'><identifier convention='iupac:inchi' value='InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)'/><atomArray atomID='a1 a2 a3'/></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertEquals("InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)", mol.getProperty(CDKConstants.INCHI));
    }

    @Test
    void testDictRef() throws Exception {
        String cmlString = "<molecule id=\"alanine\" dictRef=\"pdb:aminoAcid\"><name>alanine</name><name dictRef=\"pdb:residueName\">Ala</name><name dictRef=\"pdb:oneLetterCode\">A</name><scalar dictRef=\"pdb:id\">3</scalar><atomArray><atom id=\"a1\" elementType=\"C\" x2=\"265.0\" y2=\"989.0\"/><atom id=\"a2\" elementType=\"N\" x2=\"234.0\" y2=\"972.0\" dictRef=\"pdb:nTerminus\"/><atom id=\"a3\" elementType=\"C\" x2=\"265.0\" y2=\"1025.0\"/><atom id=\"a4\" elementType=\"C\" x2=\"296.0\" y2=\"971.0\" dictRef=\"pdb:cTerminus\"/><atom id=\"a5\" elementType=\"O\" x2=\"296.0\" y2=\"935.0\"/><atom id=\"a6\" elementType=\"O\" x2=\"327.0\" y2=\"988.0\"/></atomArray><bondArray><bond id=\"b1\" atomRefs2=\"a2 a1\" order=\"S\"/><bond id=\"b2\" atomRefs2=\"a1 a3\" order=\"S\"/><bond id=\"b3\" atomRefs2=\"a1 a4\" order=\"S\"/><bond id=\"b4\" atomRefs2=\"a4 a5\" order=\"D\"/><bond id=\"b5\" atomRefs2=\"a4 a6\" order=\"S\"/></bondArray></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Iterator<Object> props = mol.getProperties().keySet().iterator();
        boolean foundDictRefs = false;
        while (props.hasNext()) {
            Object next = props.next();
            if (next instanceof DictRef) foundDictRefs = true;
        }
        Assertions.assertTrue(foundDictRefs);
    }

    @Test
    void testQSAROutput() throws Exception {
        String specificationReference = "qsar:weight";
        String implementationTitle = "org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor";
        String implementationIdentifier = "$Id$";
        String implementationVendor = "The Chemistry Development Kit";

        String cmlString = "<molecule xmlns=\"http://www.xml-cml.org/schema\"><atomArray><atom id=\"a5256233\" "
                + "elementType=\"C\" formalCharge=\"0\" hydrogenCount=\"0\" /><atom id=\"a26250401\" elementType=\"C\" "
                + "formalCharge=\"0\" hydrogenCount=\"0\" /><atom id=\"a16821027\" elementType=\"C\" formalCharge=\"0\" "
                + "hydrogenCount=\"0\" /><atom id=\"a14923925\" elementType=\"C\" formalCharge=\"0\" hydrogenCount=\"0\" />"
                + "<atom id=\"a7043360\" elementType=\"C\" formalCharge=\"0\" hydrogenCount=\"0\" /><atom id=\"a31278839\" "
                + "elementType=\"C\" formalCharge=\"0\" hydrogenCount=\"0\" /></atomArray><bondArray><bond id=\"b6175092\" "
                + "atomRefs2=\"a5256233 a26250401\" order=\"S\" /><bond id=\"b914691\" atomRefs2=\"a26250401 a16821027\" "
                + "order=\"D\" /><bond id=\"b5298332\" atomRefs2=\"a16821027 a14923925\" order=\"S\" /><bond id=\"b29167060\" "
                + "atomRefs2=\"a14923925 a7043360\" order=\"D\" /><bond id=\"b14093690\" atomRefs2=\"a7043360 a31278839\" "
                + "order=\"S\" /><bond id=\"b11924794\" atomRefs2=\"a31278839 a5256233\" order=\"D\" /></bondArray>"
                + "<propertyList><property xmlns:qsar=\"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/\" "
                + "convention=\"qsar:DescriptorValue\"><metadataList><metadata dictRef=\"qsar:specificationReference\" "
                + "content=\""
                + specificationReference
                + "\" /><metadata dictRef=\"qsar:implementationTitle\" content=\""
                + implementationTitle
                + "\" /><metadata dictRef=\"qsar:implementationIdentifier\" "
                + "content=\""
                + implementationIdentifier
                + "\" /><metadata dictRef=\""
                + "qsar:implementationVendor\" content=\""
                + implementationVendor
                + "\" /><metadataList title=\"qsar:"
                + "descriptorParameters\"><metadata title=\"elementSymbol\" content=\"*\" /></metadataList></metadataList>"
                + "<scalar dataType=\"xsd:double\" dictRef=\"qsar:weight\">72.0</scalar></property></propertyList></molecule>";

        IChemFile chemFile = parseCMLString(cmlString);
        IAtomContainer mol = checkForSingleMoleculeFile(chemFile);

        Assertions.assertNotNull(mol);
        Assertions.assertEquals(1, mol.getProperties().size());
        Object key = mol.getProperties().keySet().toArray()[0];
        Assertions.assertNotNull(key);
        Assertions.assertTrue(key instanceof DescriptorSpecification);
        DescriptorSpecification spec = (DescriptorSpecification) key;
        Assertions.assertEquals(specificationReference, spec.getSpecificationReference());
        Assertions.assertEquals(implementationTitle, spec.getImplementationTitle());
        Assertions.assertEquals(implementationIdentifier, spec.getImplementationIdentifier());
        Assertions.assertEquals(implementationVendor, spec.getImplementationVendor());

        Assertions.assertNotNull(mol.getProperty(key));
        Assertions.assertTrue(mol.getProperty(key) instanceof DescriptorValue);
        DescriptorValue value = mol.getProperty(key);
        IDescriptorResult result = value.getValue();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof DoubleResult);
        Assertions.assertEquals(72.0, ((DoubleResult) result).doubleValue(), 0.001);
    }

    private IChemFile parseCMLString(String cmlString) throws Exception {
        IChemFile chemFile;
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
        chemFile = reader.read(new org.openscience.cdk.ChemFile());
        reader.close();
        return chemFile;
    }

    /**
     * Tests whether the file is indeed a single molecule file
     */
    private IAtomContainer checkForSingleMoleculeFile(IChemFile chemFile) {
        return checkForXMoleculeFile(chemFile, 1);
    }

    private IAtomContainer checkForXMoleculeFile(IChemFile chemFile, int numberOfMolecules) {
        Assertions.assertNotNull(chemFile);

        Assertions.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);

        Assertions.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IAtomContainerSet moleculeSet = model.getMoleculeSet();
        Assertions.assertNotNull(moleculeSet);

        Assertions.assertEquals(moleculeSet.getAtomContainerCount(), numberOfMolecules);
        IAtomContainer mol = null;
        for (int i = 0; i < numberOfMolecules; i++) {
            mol = moleculeSet.getAtomContainer(i);
            Assertions.assertNotNull(mol);
        }
        return mol;
    }

    //    private ICrystal checkForCrystalFile(IChemFile chemFile) {
    //        Assert.assertNotNull(chemFile);
    //
    //        Assert.assertEquals(chemFile.getChemSequenceCount(), 1);
    //        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
    //        Assert.assertNotNull(seq);
    //
    //        Assert.assertEquals(seq.getChemModelCount(), 1);
    //        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
    //        Assert.assertNotNull(model);
    //
    //        org.openscience.cdk.interfaces.ICrystal crystal = model.getCrystal();
    //        Assert.assertNotNull(crystal);
    //
    //        return crystal;
    //    }

    @Test
    void testReaction() throws Exception {
        String cmlString = "<reaction>" + "<reactantList><reactant><molecule id='react'/></reactant></reactantList>"
                + "<productList><product><molecule id='product'/></product></productList>"
                + "<substanceList><substance><molecule id='water'/></substance></substanceList>" + "</reaction>";

        IChemFile chemFile = parseCMLString(cmlString);
        IReaction reaction = checkForSingleReactionFile(chemFile);

        Assertions.assertEquals(1, reaction.getReactantCount());
        Assertions.assertEquals(1, reaction.getProductCount());
        Assertions.assertEquals(1, reaction.getAgents().getAtomContainerCount());
        Assertions.assertEquals("react", reaction.getReactants().getAtomContainer(0).getID());
        Assertions.assertEquals("product", reaction.getProducts().getAtomContainer(0).getID());
        Assertions.assertEquals("water", reaction.getAgents().getAtomContainer(0).getID());
    }

    /**
     * Tests whether the file is indeed a single reaction file
     */
    private IReaction checkForSingleReactionFile(IChemFile chemFile) {
        return checkForXReactionFile(chemFile, 1);
    }

    private IReaction checkForXReactionFile(IChemFile chemFile, int numberOfReactions) {
        Assertions.assertNotNull(chemFile);

        Assertions.assertEquals(chemFile.getChemSequenceCount(), 1);
        org.openscience.cdk.interfaces.IChemSequence seq = chemFile.getChemSequence(0);
        Assertions.assertNotNull(seq);

        Assertions.assertEquals(seq.getChemModelCount(), 1);
        org.openscience.cdk.interfaces.IChemModel model = seq.getChemModel(0);
        Assertions.assertNotNull(model);

        IReactionSet reactionSet = model.getReactionSet();
        Assertions.assertNotNull(reactionSet);

        Assertions.assertEquals(reactionSet.getReactionCount(), numberOfReactions);
        IReaction reaction = null;
        for (int i = 0; i < numberOfReactions; i++) {
            reaction = reactionSet.getReaction(i);
            Assertions.assertNotNull(reaction);
        }
        return reaction;
    }

}

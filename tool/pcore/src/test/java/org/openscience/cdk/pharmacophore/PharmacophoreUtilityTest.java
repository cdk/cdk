/* Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.pharmacophore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

/**
 * @cdk.module test-pcore
 */
class PharmacophoreUtilityTest {

    private static ConformerContainer conformers = null;

    @BeforeAll
    static void loadConformerData() {
        String filename = "pcoretest1.sdf";
        InputStream ins = PharmacophoreUtilityTest.class.getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins,
                DefaultChemObjectBuilder.getInstance());
        if (reader.hasNext()) PharmacophoreUtilityTest.conformers = (ConformerContainer) reader.next();
    }

    @Test
    void testReadPcoreDef() throws Exception {
        String filename = "pcore.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getResourceAsStream(filename);
        List<PharmacophoreQuery> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        Assertions.assertEquals(2, defs.size());

        IQueryAtomContainer def1 = defs.get(0);
        Assertions.assertEquals(4, def1.getAtomCount());
        Assertions.assertEquals(2, def1.getBondCount());
        Assertions.assertEquals("An imaginary pharmacophore definition", def1.getProperty("description"));
        Assertions.assertEquals("Imaginary", def1.getTitle());

        IQueryAtomContainer def2 = defs.get(1);
        Assertions.assertEquals(3, def2.getAtomCount());
        Assertions.assertEquals(3, def2.getBondCount());
        Assertions.assertNull(def2.getTitle());

        String[] ids = {"Aromatic", "Hydroxyl", "BasicAmine"};
        for (IAtom atom : def2.atoms()) {
            String sym = atom.getSymbol();
            boolean found = false;
            for (String s : ids) {
                if (sym.equals(s)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found, "'" + sym + "' in pcore.xml is invalid");
        }
    }

    @Test
    void testReadPcoreAngleDef() throws Exception {
        String filename = "pcoreangle.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getResourceAsStream(filename);
        List<PharmacophoreQuery> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        Assertions.assertEquals(1, defs.size());

        IQueryAtomContainer def1 = defs.get(0);
        Assertions.assertEquals(3, def1.getAtomCount());
        Assertions.assertEquals(2, def1.getBondCount());
        Assertions.assertEquals("A modified definition for the D1 receptor", def1.getProperty("description"));

        String[] ids = {"Aromatic", "Hydroxyl", "BasicAmine"};
        for (IAtom atom : def1.atoms()) {
            String sym = atom.getSymbol();
            boolean found = false;
            for (String s : ids) {
                if (sym.equals(s)) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found, "'" + sym + "' in pcore.xml is invalid");
        }

        for (IBond bond : def1.bonds()) {
            if (bond instanceof PharmacophoreQueryBond) {
                PharmacophoreQueryBond cons = (PharmacophoreQueryBond) bond;
                IAtom[] a = getAtoms(cons);
                Assertions.assertEquals(2, a.length);
            } else if (bond instanceof PharmacophoreQueryAngleBond) {
                PharmacophoreQueryAngleBond cons = (PharmacophoreQueryAngleBond) bond;
                IAtom[] a = getAtoms(cons);
                Assertions.assertEquals(3, a.length);
            }
        }
    }

    @Test
    void testInvalidPcoreXML() throws IOException, CDKException {
        String filename = "invalid1.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getResourceAsStream(filename);
        Assertions.assertThrows(CDKException.class, () -> {
            PharmacophoreUtils.readPharmacophoreDefinitions(ins);
        });
    }

    @Test
    void testPCoreWrite() throws Exception {
        String filename = "pcore.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getResourceAsStream(filename);
        List<PharmacophoreQuery> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        PharmacophoreQuery[] defarray = defs.toArray(new PharmacophoreQuery[]{});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PharmacophoreUtils.writePharmacophoreDefinition(defarray, baos);
        String s = baos.toString();
        Assertions.assertNotNull(s);
        String[] lines = s.split("\n");
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", lines[0].trim());

        int ndef = 0;
        int ndist = 0;
        int nangle = 0;
        for (String line : lines) {
            if (line.contains("</pharmacophore>")) ndef++;
            if (line.contains("</distanceConstraint>")) ndist++;
            if (line.contains("</angleConstraint>")) nangle++;
        }
        Assertions.assertEquals(2, ndef);
        Assertions.assertEquals(5, ndist);
        Assertions.assertEquals(0, nangle);
    }

    private IAtom[] getAtoms(IBond bond) {
        ArrayList<IAtom> alist = new ArrayList<>();
        for (IAtom iAtom : bond.atoms()) {
            alist.add(iAtom);
        }
        return alist.toArray(new IAtom[]{});
    }
}

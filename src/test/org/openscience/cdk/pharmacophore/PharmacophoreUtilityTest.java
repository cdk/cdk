package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @cdk.module test-pcore
 */
public class PharmacophoreUtilityTest {

    public static ConformerContainer conformers = null;

    @BeforeClass
    public static void loadConformerData() {
        String filename = "data/mdl/pcoretest1.sdf";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        IteratingMDLConformerReader reader = new IteratingMDLConformerReader(ins, DefaultChemObjectBuilder.getInstance());
        if (reader.hasNext()) PharmacophoreUtilityTest.conformers = (ConformerContainer) reader.next();
    }


    @Test
    public void testReadPcoreDef() throws IOException, CDKException {
        String filename = "data/pcore/pcore.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        List<IQueryAtomContainer> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        Assert.assertEquals(2, defs.size());

        IQueryAtomContainer def1 = defs.get(0);
        Assert.assertEquals(4, def1.getAtomCount());
        Assert.assertEquals(2, def1.getBondCount());
        Assert.assertEquals("An imaginary pharmacophore definition", def1.getProperty("description"));

        IQueryAtomContainer def2 = defs.get(1);
        Assert.assertEquals(3, def2.getAtomCount());
        Assert.assertEquals(3, def2.getBondCount());
        String[] ids = {"Aromatic", "Hydroxyl", "BasicAmine"};
        Iterator<IAtom> atoms = def2.atoms();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            String sym = atom.getSymbol();
            boolean found = false;
            for (String s : ids) {
                if (sym.equals(s)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("'" + sym + "' in pcore.xml is invalid", found);
        }
    }

    @Test
    public void testReadPcoreAngleDef() throws CDKException, IOException {
        String filename = "data/pcore/pcoreangle.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        List<IQueryAtomContainer> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        Assert.assertEquals(1, defs.size());

        IQueryAtomContainer def1 = defs.get(0);
        Assert.assertEquals(3, def1.getAtomCount());
        Assert.assertEquals(2, def1.getBondCount());
        Assert.assertEquals("A modified definition for the D1 receptor", def1.getProperty("description"));


        String[] ids = {"Aromatic", "Hydroxyl", "BasicAmine"};
        Iterator<IAtom> atoms = def1.atoms();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            String sym = atom.getSymbol();
            boolean found = false;
            for (String s : ids) {
                if (sym.equals(s)) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue("'" + sym + "' in pcore.xml is invalid", found);
        }

        Iterator<IBond> bonds = def1.bonds();
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            if (bond instanceof PharmacophoreQueryBond) {
                PharmacophoreQueryBond cons = (PharmacophoreQueryBond) bond;
                IAtom[] a = getAtoms(cons);
                Assert.assertEquals(2, a.length);
            } else if (bond instanceof PharmacophoreQueryAngleBond) {
                PharmacophoreQueryAngleBond cons = (PharmacophoreQueryAngleBond) bond;
                IAtom[] a = getAtoms(cons);
                Assert.assertEquals(3, a.length);
            }
        }
    }

    @Test(expected = CDKException.class)
    public void testInvalidPcoreXML() throws IOException, CDKException {
        String filename = "data/pcore/invalid1.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        List<IQueryAtomContainer> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);
    }

    @Test
    public void testPCoreWrite() throws CDKException, IOException {
        String filename = "data/pcore/pcore.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        List<IQueryAtomContainer> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);

        IQueryAtomContainer[] defarray = defs.toArray(new IQueryAtomContainer[]{});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PharmacophoreUtils.writePharmacophoreDefinition(defarray, baos);
        String s = baos.toString();
        Assert.assertNotNull(s);
        String[] lines = s.split("\n");
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>", lines[0].trim());

        int ndef = 0;
        int ndist = 0;
        int nangle = 0;
        for (String line : lines) {
            if (line.indexOf("</pharmacophore>") != -1) ndef++;
            if (line.indexOf("</distanceConstraint>") != -1) ndist++;
            if (line.indexOf("</angleConstraint>") != -1) nangle++;
        }
        Assert.assertEquals(2, ndef);
        Assert.assertEquals(5, ndist);
        Assert.assertEquals(0, nangle);
    }

    private IAtom[] getAtoms(IBond bond) {
        ArrayList<IAtom> alist = new ArrayList<IAtom>();
        Iterator<IAtom> atoms = bond.atoms();
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            alist.add(atom);
        }
        return alist.toArray(new IAtom[]{});
    }
}


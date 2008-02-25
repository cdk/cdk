package org.openscience.cdk.pharmacophore;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.io.iterator.IteratingMDLConformerReader;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.pharmacophore.PharmacophoreUtils;

import java.io.IOException;
import java.io.InputStream;
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
        String[] ids = {"Aromatic", "Hydroxyl", "BasicAmine" };
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
            Assert.assertTrue("'"+sym+"' in pcore.xml is invalid", found);
        }
    }

    @Test(expected=CDKException.class)
    public void testInvalidPcoreXML() throws IOException, CDKException {
        String filename = "data/pcore/invalid1.xml";
        InputStream ins = PharmacophoreUtilityTest.class.getClassLoader().getResourceAsStream(filename);
        List<IQueryAtomContainer> defs = PharmacophoreUtils.readPharmacophoreDefinitions(ins);
    }
}


package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * @cdk.module test-qsaratomic
 */
public class RDFProtonDescriptor_GHR_topolTest extends AtomicDescriptorTest {

    public RDFProtonDescriptor_GHR_topolTest() {}

    @Before
    public void setUp() throws Exception {
        setDescriptor(RDFProtonDescriptor_GHR_topol.class);
    }

    @Test
    public void testExample1() throws Exception {
        //firstly read file to molecule
        String filename = "data/mdl/hydroxyamino.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainerSet som = model.getMoleculeSet();
        IAtomContainer mol = som.getAtomContainer(0);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            //			System.out.println("Atom: " + mol.getAtom(i).getSymbol());
            if (mol.getAtom(i).getSymbol().equals("H")) {
                //secondly perform calculation on it.
                RDFProtonDescriptor_GHR_topol descriptor = new RDFProtonDescriptor_GHR_topol();
                DescriptorValue dv = descriptor.calculate(mol.getAtom(i), mol);
                IDescriptorResult result = dv.getValue();
                //				System.out.println("array: " + result.toString());
                Assert.assertNotNull(result);
            }

        }
    }

    @Test
    public void testReturnsNaNForNonHydrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        mol.addAtom(atom);
        DescriptorValue dv = descriptor.calculate(atom, mol);
        IDescriptorResult result = dv.getValue();
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof DoubleArrayResult);
        DoubleArrayResult dResult = (DoubleArrayResult) result;
        for (int i = 0; i < result.length(); i++) {
            Assert.assertEquals(Double.NaN, dResult.get(i), 0.000001);
        }
    }

}

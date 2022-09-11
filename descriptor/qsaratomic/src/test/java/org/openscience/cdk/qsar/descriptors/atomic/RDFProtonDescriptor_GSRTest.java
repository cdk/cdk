package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.InputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * @cdk.module test-qsaratomic
 */
class RDFProtonDescriptor_GSRTest extends AtomicDescriptorTest {

    RDFProtonDescriptor_GSRTest() {}

    @BeforeEach
    void setUp() throws Exception {
        setDescriptor(RDFProtonDescriptor_GSR.class);
    }

    @Test
    void testExample1() throws Exception {
        //firstly read file to molecule
        String filename = "hydroxyamino.mol";
        InputStream ins = this.getClass().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        IChemSequence seq = chemFile.getChemSequence(0);
        IChemModel model = seq.getChemModel(0);
        IAtomContainerSet som = model.getMoleculeSet();
        IAtomContainer mol = som.getAtomContainer(0);

        for (int i = 0; i < mol.getAtomCount(); i++) {
            //			System.out.println("Atom: " + mol.getAtom(i).getSymbol());
            if (mol.getAtom(i).getAtomicNumber() == IElement.H) {
                //secondly perform calculation on it.
                RDFProtonDescriptor_GSR descriptor = new RDFProtonDescriptor_GSR();
                DescriptorValue dv = descriptor.calculate(mol.getAtom(i), mol);
                IDescriptorResult result = dv.getValue();
                //				System.out.println("array: " + result.toString());
                Assertions.assertNotNull(result);
                Assertions.assertEquals(dv.getNames().length, result.length());
            }

        }
    }

    @Test
    void testReturnsNaNForNonHydrogen() throws Exception {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("O");
        mol.addAtom(atom);
        DescriptorValue dv = descriptor.calculate(atom, mol);
        IDescriptorResult result = dv.getValue();
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result instanceof DoubleArrayResult);
        DoubleArrayResult dResult = (DoubleArrayResult) result;
        for (int i = 0; i < result.length(); i++) {
            Assertions.assertEquals(Double.NaN, dResult.get(i), 0.000001);
        }
    }

}

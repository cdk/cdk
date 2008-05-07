package org.openscience.cdk.qsar.descriptors.atomic;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GHR;
import org.openscience.cdk.qsar.result.IDescriptorResult;

/**
 * @cdk.module test-qsaratomic
 */
public class RDFProtonDescriptor_GHRTest extends AtomicDescriptorTest {

	public RDFProtonDescriptor_GHRTest() {
    }

    public void setUp() throws Exception {
    	setDescriptor(RDFProtonDescriptor_GHR.class);
    }
    
	public static Test suite() {
		return new TestSuite(RDFProtonDescriptor_GHRTest.class);
	}
    
	/**
	 *  @cdk.bug 1632419
	 */
	public void testExample1() throws Exception {
		//firstly read file to molecule		
		String filename = "data/mdl/hydroxyamino.mol" +
				"";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
		ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
		IChemSequence seq = chemFile.getChemSequence(0);
		IChemModel model = seq.getChemModel(0);
		IMoleculeSet som = model.getMoleculeSet();
		IMolecule mol = som.getMolecule(0);

		RDFProtonDescriptor_GHR descriptor = new RDFProtonDescriptor_GHR();
		int hNumber =0;
		for (int i=0; i < mol.getAtomCount(); i++) {
//			System.out.println("Atom: " + mol.getAtom(i).getSymbol());
//			System.out.println("  charge: " + mol.getAtom(i).getCharge());
			if(mol.getAtom(i).getSymbol().equals("H")){
				hNumber++;
				//secondly perform calculation on it.
				DescriptorValue dv = descriptor.calculate(mol.getAtom(i),mol );
				IDescriptorResult result = dv.getValue();
//				System.out.println("array: " + result.toString());
				assertNotNull(result);
			}
		}
	}

}

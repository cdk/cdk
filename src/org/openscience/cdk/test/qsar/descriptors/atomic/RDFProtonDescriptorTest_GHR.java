package org.openscience.cdk.test.qsar.descriptors.atomic;

import java.io.InputStream;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.qsar.descriptors.atomic.RDFProtonDescriptor_GHR;

public class RDFProtonDescriptorTest_GHR extends CDKTestCase {

	public RDFProtonDescriptorTest_GHR() {
    }

	public static Test suite() {
		return new TestSuite(RDFProtonDescriptorTest_GHR.class);
	}
    
	public void testExample1() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//firstly read file to molecule		
		String filename = "data/mdl/zinctest.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            IChemSequence seq = chemFile.getChemSequence(0);
            IChemModel model = seq.getChemModel(0);
            IMoleculeSet som = model.getMoleculeSet();
            IMolecule mol = som.getMolecule(0);

            for (int i=0; i < mol.getAtomCount(); i++) {
            	System.out.println("Atom: " + mol.getAtom(i).getSymbol());
        		if(mol.getAtom(i).getSymbol().equals("H")){
            	//secondly perform calculation on it.
                RDFProtonDescriptor_GHR descriptor = new RDFProtonDescriptor_GHR();
        		DescriptorValue dv = descriptor.calculate(mol.getAtom(i),mol );
        		IDescriptorResult result = dv.getValue();
        		System.out.println("array: " + result.toString());
        		}		

            }
        }
        
        catch (Exception e) {
        	e.printStackTrace();
            fail(e.toString());
            
        }
	}
}

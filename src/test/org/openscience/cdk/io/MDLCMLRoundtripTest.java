package org.openscience.cdk.io;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.IChemObjectReader.Mode;

public class MDLCMLRoundtripTest extends CDKTestCase {

    private IChemObjectBuilder builder;

    public MDLCMLRoundtripTest(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(MDLCMLRoundtripTest.class);
    }

    protected void setUp() throws Exception {
        builder = DefaultChemObjectBuilder.getInstance();
    }
    
    /**
     * @cdk.bug 1649526
     */
    public void testBug1649526() throws CDKException{
    	//Read the original
    	String filename = "data/mdl/bug-1649526.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLReader reader = new MDLReader(ins);
        Molecule mol = (Molecule)reader.read(new Molecule());
        //Write it as cml
		StringWriter writer = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(writer);        
        cmlWriter.write(mol);
        //Read this again
        CMLReader cmlreader = new CMLReader(new ByteArrayInputStream(writer.toString().getBytes()));
        IChemFile file = (IChemFile)cmlreader.read(new org.openscience.cdk.ChemFile());
        //And finally write as mol
        StringWriter writermdl = new StringWriter();
        MDLWriter mdlWriter = new MDLWriter(writermdl);
        mdlWriter.write(file);
        String output = writermdl.toString();
        //if there would be 3 instances (as in the bug), the only instance wouldnt't be right at the end
        this.assertEquals(2994,output.indexOf("M  END"));
        //there would need some $$$$ to be in
        this.assertEquals(-1,output.indexOf("$$$$"));
        this.assertEquals(25,output.indexOf(" 31 33  0  0  0  0"));
    }
}

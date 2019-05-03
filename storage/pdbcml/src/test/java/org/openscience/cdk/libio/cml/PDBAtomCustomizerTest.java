package org.openscience.cdk.libio.cml;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.io.StringWriter;

/**
 * @author John May
 * @cdk.module test-pdbcml
 */
public class PDBAtomCustomizerTest {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(PDBAtomCustomizerTest.class);

    @Test
    public void testPDBAtomCustomization() throws Exception {
        StringWriter writer = new StringWriter();
        IAtomContainer molecule = new AtomContainer();
        IPDBAtom atom = new PDBAtom("C");
        atom.setName("CA");
        atom.setResName("PHE");
        molecule.addAtom(atom);

        CMLWriter cmlWriter = new CMLWriter(writer);
        cmlWriter.registerCustomizer(new PDBAtomCustomizer());
        cmlWriter.write(molecule);
        cmlWriter.close();
        String cmlContent = writer.toString();
        logger.debug("****************************** testPDBAtomCustomization()");
        logger.debug(cmlContent);
        logger.debug("******************************");
        Assert.assertTrue(cmlContent.indexOf("<scalar dictRef=\"pdb:resName") != -1);
    }
}

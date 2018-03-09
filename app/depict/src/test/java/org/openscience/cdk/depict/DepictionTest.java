package org.openscience.cdk.depict;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DepictionTest {

    @Test
    public void depictAsEps() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        Depiction d = dg.depict(ac);
        String eps = d.toEpsStr();
        assertTrue(eps.startsWith("%!PS-Adobe-3.0 EPSF-3.0\n%%BoundingBox: 0 0 28 35\n"));
    }

}

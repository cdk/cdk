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
    public void depictAsPs() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        Depiction d = dg.depict(ac);
        String eps = d.toPsStr();
        String nl = System.getProperty("line.separator");
        String lines[] = eps.split(nl,3);
        assertEquals("%!PS-Adobe-3.0", lines[0]);
        assertEquals("%%Creator: FreeHEP Graphics2D Driver", lines[1]);
    }

    public void depictAsEps() throws CDKException {
        DepictionGenerator dg = new DepictionGenerator();
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer ac = sp.parseSmiles("[nH]1cccc1");
        Depiction d = dg.depict(ac);
        String eps = d.toEpsStr();
        String nl = System.getProperty("line.separator");
        String lines[] = eps.split(nl,3);
        assertEquals("%!PS-Adobe-3.0 EPSF-3.0", lines[0]);
        assertEquals("%%BoundingBox: 0 0 28 35", lines[1]);
    }

}

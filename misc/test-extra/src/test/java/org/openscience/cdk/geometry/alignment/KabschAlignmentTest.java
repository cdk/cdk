package org.openscience.cdk.geometry.alignment;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.vecmath.Point3d;
import java.io.InputStream;
import java.util.List;

/**
 * This class defines regression tests that should ensure that the source code
 * of the org.openscience.cdk.geometry.alignment.KabschAlignment is not broken.
 *
 * @cdk.module test-extra
 *
 * @author     Rajarshi Guha
 * @cdk.created    2004-12-11
 *
 * @see org.openscience.cdk.geometry.alignment.KabschAlignment
 */
public class KabschAlignmentTest extends CDKTestCase {

    @Test
    public void testAlign() throws java.lang.Exception {
        IAtomContainer ac;
        String filename = "data/hin/gravindex.hin";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new HINReader(ins);
        ChemFile content = (ChemFile) reader.read((ChemObject) new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        ac = cList.get(0);

        KabschAlignment ka = new KabschAlignment(ac, ac);
        Assert.assertNotNull(ka);
        ka.align();
        double rmsd = ka.getRMSD();
        Assert.assertTrue(1e-8 > rmsd);
        Assert.assertNotNull(ka.getRotationMatrix());

        double[][] p1 = {{16.754, 20.462, 45.049}, {19.609, 18.145, 46.011}, {17.101, 17.256, 48.707},
                {13.963, 18.314, 46.820}, {14.151, 15.343, 44.482}, {14.959, 12.459, 46.880}, {11.987, 13.842, 48.862},
                {9.586, 12.770, 46.123}, {11.006, 9.245, 46.116}, {10.755, 9.090, 49.885}};
        double[][] p2 = {{70.246, 317.510, 188.263}, {73.457, 317.369, 190.340}, {71.257, 318.976, 193.018},
                {68.053, 317.543, 191.651}, {68.786, 313.954, 192.637}, {70.248, 314.486, 196.151},
                {67.115, 316.584, 196.561}, {64.806, 313.610, 196.423}, {66.804, 311.735, 199.035},
                {66.863, 314.832, 201.113}};
        Atom[] a1 = new Atom[10];
        Atom[] a2 = new Atom[10];
        for (int i = 0; i < 10; i++) {
            a1[i] = new Atom("C");
            Point3d newCoord = new Point3d();
            newCoord.x = p1[i][0];
            newCoord.y = p1[i][1];
            newCoord.z = p1[i][2];
            a1[i].setPoint3d(newCoord);
            a2[i] = new Atom("C");
            newCoord = new Point3d();
            newCoord.x = p2[i][0];
            newCoord.y = p2[i][1];
            newCoord.z = p2[i][2];
            a2[i].setPoint3d(newCoord);
        }
        ka = new KabschAlignment(a1, a2);
        ka.align();
        rmsd = ka.getRMSD();
        Assert.assertEquals(0.13479726, rmsd, 0.00000001);
    }

}

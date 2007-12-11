package org.openscience.cdk.test;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ConformerContainer;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point3d;
import java.util.Iterator;
import java.util.Random;


/**
 * @cdk.module test-data
 */
public class ConformerContainerTest {
    private static IAtomContainer base;
    private static IAtomContainer[] confs;

    private static int natom = 10;
    private static int nconfs = 20;

    private static Random rnd = new Random();

    private static IAtomContainer getBaseAtomContainer(int natom, String title) {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.setProperty(CDKConstants.TITLE, title);
        for (int i = 0; i < 10; i++) {
            Point3d coord = new Point3d();
            coord.x = rnd.nextDouble();
            coord.y = rnd.nextDouble();
            coord.z = rnd.nextDouble();

            IAtom atom = DefaultChemObjectBuilder.getInstance().newAtom("C", coord);
            container.addAtom(atom);
        }

        for (int i = 0; i < natom - 1; i++) {
            IAtom atom1 = container.getAtom(i);
            IAtom atom2 = container.getAtom(i + 1);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(atom1, atom2, IBond.Order.SINGLE);
            container.addBond(bond);
        }
        return container;
    }

    private static IAtomContainer[] getConformers(IAtomContainer base, int nconf) throws CloneNotSupportedException {
        IAtomContainer[] ret = new IAtomContainer[nconf];
        for (int i = 0; i < nconf; i++) {
            IAtomContainer aClone = (IAtomContainer) base.clone();
            for (int j = 0; j < aClone.getAtomCount(); j++) {
                Point3d p = aClone.getAtom(j).getPoint3d();
                p.x = rnd.nextDouble();
                p.y = rnd.nextDouble();
                p.z = rnd.nextDouble();
                aClone.getAtom(j).setPoint3d(p);
            }
            ret[i] = aClone;
        }
        return ret;
    }

    @BeforeClass
    public static void setUp() throws CloneNotSupportedException {
        base = getBaseAtomContainer(natom, "myMolecule");
        confs = getConformers(base, nconfs);
    }

    @Test
    public void testSimpleConstructor() {
        ConformerContainer container = new ConformerContainer();
        Assert.assertNotNull(container);
        container.add(base);
        Assert.assertEquals(1, container.size());

        for (IAtomContainer conf : confs) container.add(conf);
        Assert.assertEquals(nconfs + 1, container.size());
    }

    @Test
    public void testArrayConstructor() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertNotNull(container);
        Assert.assertEquals(nconfs, container.size());
    }

    @Test
    public void testGetTitle() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals("myMolecule", container.getTitle());
    }

    @Test
    public void testIsEmpty() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertTrue(!container.isEmpty());
    }

    @Test
    public void testContains() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer o = container.get(0);
        Assert.assertTrue(container.contains(o));
    }

    @Test
    public void testToArray() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer[] array = (IAtomContainer[]) container.toArray();
        Assert.assertEquals(nconfs, array.length);
    }

    @Test
    public void testIterator() {
        ConformerContainer container = new ConformerContainer(confs);
        Iterator<IAtomContainer> iter = container.iterator();
        int nmol = 0;
        while (iter.hasNext()) {
            IAtomContainer atomContainer = iter.next();
            nmol++;
        }
        Assert.assertEquals(nconfs, nmol);
    }

    @Test
    public void testIterator2() {
        ConformerContainer container = new ConformerContainer(confs);
        int nmol = 0;
        for (IAtomContainer conf : container) {
            nmol++;
        }
        Assert.assertEquals(nconfs, nmol);

    }

    @Test
    public void testRemove() {
        ConformerContainer container = new ConformerContainer(confs);
        container.clear();
        Assert.assertEquals(0, container.size());

        for (int i = 0; i < nconfs; i++) container.add(confs[i]);
        Assert.assertEquals(nconfs, container.size());

        container.remove(0);
        Assert.assertEquals(nconfs-1, container.size());
    }

    @Test
    public void testIndexOf() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer ac = container.get(2);
        int index = container.indexOf(ac);
        Assert.assertEquals(2, index);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd() {
        ConformerContainer container = new ConformerContainer(confs);
        base.setProperty(CDKConstants.TITLE, "junk");
        container.add(base);
    }

    @Test(expected  = IndexOutOfBoundsException.class)
    public void testGet1() {
        ConformerContainer container = new ConformerContainer(confs);
        container.get(100);        
    }

    @Test(expected  = IndexOutOfBoundsException.class)
    public void testGet2() {
        ConformerContainer container = new ConformerContainer(confs);
        for (int i = 0; i < container.size()+1; i++) container.get(i);        
    }

}

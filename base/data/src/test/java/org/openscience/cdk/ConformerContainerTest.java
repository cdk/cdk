package org.openscience.cdk;

import java.util.Iterator;
import java.util.Random;

import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;

/**
 * @cdk.module test-data
 */
public class ConformerContainerTest extends CDKTestCase {

    private IAtomContainer   base;
    private IAtomContainer[] confs;

    private static int       natom  = 10;
    private static int       nconfs = 20;

    private static Random    rnd    = new Random();

    private static IAtomContainer getBaseAtomContainer(int natom, String title) {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        container.setProperty(CDKConstants.TITLE, title);
        for (int i = 0; i < natom; i++) {
            Point3d coord = new Point3d();
            coord.x = rnd.nextDouble();
            coord.y = rnd.nextDouble();
            coord.z = rnd.nextDouble();

            IAtom atom = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C", coord);
            container.addAtom(atom);
        }

        for (int i = 0; i < natom - 1; i++) {
            IAtom atom1 = container.getAtom(i);
            IAtom atom2 = container.getAtom(i + 1);
            IBond bond = DefaultChemObjectBuilder.getInstance().newInstance(IBond.class, atom1, atom2,
                    IBond.Order.SINGLE);
            container.addBond(bond);
        }
        return container;
    }

    private static IAtomContainer[] getConformers(IAtomContainer base, int nconf) throws CloneNotSupportedException {
        IAtomContainer[] ret = new IAtomContainer[nconf];
        for (int i = 0; i < nconf; i++) {
            for (int j = 0; j < base.getAtomCount(); j++) {
                Point3d p = base.getAtom(j).getPoint3d();
                p.x = rnd.nextDouble();
                p.y = rnd.nextDouble();
                p.z = rnd.nextDouble();
                base.getAtom(j).setPoint3d(p);
            }
            ret[i] = (IAtomContainer) base.clone();
        }
        return ret;
    }

    @Before
    public void setUp() throws CloneNotSupportedException {
        base = getBaseAtomContainer(natom, "myMolecule");
        confs = getConformers(base, nconfs);
    }

    @Test
    public void testConformerContainer() {
        ConformerContainer container = new ConformerContainer();
        Assert.assertNotNull(container);
        base.setProperty(CDKConstants.TITLE, "myMolecule");
        container.add(base);
        Assert.assertEquals(1, container.size());

        for (IAtomContainer conf : confs)
            container.add(conf);
        Assert.assertEquals(nconfs + 1, container.size());
    }

    @Test
    public void testConformerContainer_arrayIAtomContainer() {
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
    public void testRemove_int() {
        ConformerContainer container = new ConformerContainer(confs);
        container.clear();
        Assert.assertEquals(0, container.size());

        for (int i = 0; i < nconfs; i++)
            container.add(confs[i]);
        Assert.assertEquals(nconfs, container.size());

        container.remove(0);
        Assert.assertEquals(nconfs - 1, container.size());
    }

    @Test
    public void testIndexOf_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer ac = container.get(2);
        int index = container.indexOf(ac);
        Assert.assertEquals(2, index);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        base.setProperty(CDKConstants.TITLE, "junk");
        container.add(base);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet_int() {
        ConformerContainer container = new ConformerContainer(confs);
        container.get(100);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGet2() {
        ConformerContainer container = new ConformerContainer(confs);
        for (int i = 0; i < container.size() + 1; i++)
            container.get(i);
    }

    @Test
    public void testAdd_int_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        container.add(5, confs[5]);
    }

    @Test
    public void testAdd_int_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        container.add(5, confs[5]);
    }

    @Test
    public void testAdd_Object() {
        ConformerContainer container = new ConformerContainer();
        Assert.assertNotNull(container);
        for (IAtomContainer conf : confs)
            container.add(conf);
        Assert.assertEquals(nconfs, container.size());
    }

    @Test
    public void testIndexOf_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertNotNull(container);

        int counter = 0;
        for (IAtomContainer conf : confs) {
            Assert.assertEquals(counter, container.indexOf(conf));
            counter++;
        }
    }

    @Test
    public void testClear() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, container.size());
        container.clear();
        Assert.assertEquals(0, container.size());
    }

    @Test
    public void testSize() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, container.size());
    }

    @Test
    public void testLastIndexOf_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, container.size());
        int x = container.lastIndexOf(container.get(3));
        Assert.assertEquals(3, container.lastIndexOf(container.get(3)));
    }

    @Test
    public void testContains_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, container.size());
        Assert.assertTrue(container.contains(container.get(3)));
    }

    @Test
    public void testAddAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, container.size());
        Assert.assertTrue(container.contains(container.get(3)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll_int_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        container.addAll(5, null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testToArray_arrayObject() {
        ConformerContainer container = new ConformerContainer(confs);
        container.toArray(new IAtomContainer[]{});
    }

    @Test
    public void testRemove_Object() {
        ConformerContainer cContainer = new ConformerContainer(confs);
        Assert.assertEquals(nconfs, cContainer.size());
        IAtomContainer container = cContainer.get(3);
        Assert.assertTrue(cContainer.contains(container));
        cContainer.remove(container);
        Assert.assertEquals(nconfs - 1, cContainer.size());
        Assert.assertFalse(cContainer.contains(container));
    }

    @Test
    public void testSet_int_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        int location = 5;
        container.set(location, container.get(location + 1));
        Assert.assertEquals(location, container.indexOf(container.get(location + 1)));
    }

    /*
     * note that assertNotSame checks whether object A *refers* to the same as
     * object B since a ConformerContainer.get always returns the sme
     * IAtomContainer object (just changing the 3D coords for a given conformer)
     * the following will always fail
     * Assert.assertNotSame(container.get(location+1), container.get(location));
     * Sinmilarly: Assert.assertEquals(container.get(location+1),
     * container.get(location)); since this will check whether the references of
     * X & Y are the same. Since get() returns the same atom container (with the
     * coords chanegd), there is no difference in the reference returned. Thus
     * this test is always the same. Better to check set position X to X+1 and
     * then find the first occurence of the object originally at X+1 - it should
     * now be X
     */
    @Test
    public void testSet_int_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        int location = 5;
        container.set(location, container.get(location + 1));
        Assert.assertEquals(location, container.indexOf(container.get(location + 1)));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testContainsAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertNotNull(container);
        Assert.assertEquals(nconfs, container.size());
        Assert.assertTrue(container.containsAll(container));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assert.assertNotNull(container);
        Assert.assertEquals(nconfs, container.size());

        container.removeAll(container);
        Assert.assertEquals(0, container.size());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll_Collection() {
        ConformerContainer container = new ConformerContainer(base);
        container.retainAll(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSubList_int_int() {
        ConformerContainer container = new ConformerContainer(base);
        container.subList(3, 4);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testListIterator() {
        ConformerContainer container = new ConformerContainer(base);
        container.listIterator();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testListIterator_int() {
        ConformerContainer container = new ConformerContainer(base);
        container.listIterator(1);
    }

    @Test
    public void testConformerContainer_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(base);
        Assert.assertNotNull(container);
        Assert.assertEquals(1, container.size());
    }

}

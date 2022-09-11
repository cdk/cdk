package org.openscience.cdk;

import java.util.Iterator;
import java.util.Random;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-data
 */
class ConformerContainerTest extends CDKTestCase {

    private IAtomContainer   base;
    private IAtomContainer[] confs;

    private static final int       natom  = 10;
    private static final int       nconfs = 20;

    private static final Random    rnd    = new Random();

    private static IAtomContainer getBaseAtomContainer(int natom, String title) {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        container.setTitle(title);
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
            ret[i] = base.clone();
        }
        return ret;
    }

    @BeforeEach
    void setUp() throws CloneNotSupportedException {
        base = getBaseAtomContainer(natom, "myMolecule");
        confs = getConformers(base, nconfs);
    }

    @Test
    void testConformerContainer() {
        ConformerContainer container = new ConformerContainer();
        Assertions.assertNotNull(container);
        base.setTitle("myMolecule");
        container.add(base);
        Assertions.assertEquals(1, container.size());

        for (IAtomContainer conf : confs)
            container.add(conf);
        Assertions.assertEquals(nconfs + 1, container.size());
    }

    @Test
    void testConformerContainer_arrayIAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(nconfs, container.size());
    }

    @Test
    void testGetTitle() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals("myMolecule", container.getTitle());
    }

    @Test
    void testIsEmpty() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertTrue(!container.isEmpty());
    }

    @Test
    void testContains() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer o = container.get(0);
        Assertions.assertTrue(container.contains(o));
    }

    @Test
    void testToArray() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer[] array = (IAtomContainer[]) container.toArray();
        Assertions.assertEquals(nconfs, array.length);
    }

    @Test
    void testIterator() {
        ConformerContainer container = new ConformerContainer(confs);
        Iterator<IAtomContainer> iter = container.iterator();
        int nmol = 0;
        while (iter.hasNext()) {
            IAtomContainer atomContainer = iter.next();
            nmol++;
        }
        Assertions.assertEquals(nconfs, nmol);
    }

    @Test
    void testIterator2() {
        ConformerContainer container = new ConformerContainer(confs);
        int nmol = 0;
        for (IAtomContainer conf : container) {
            nmol++;
        }
        Assertions.assertEquals(nconfs, nmol);

    }

    @Test
    void testRemove_int() {
        ConformerContainer container = new ConformerContainer(confs);
        container.clear();
        Assertions.assertEquals(0, container.size());

        for (int i = 0; i < nconfs; i++)
            container.add(confs[i]);
        Assertions.assertEquals(nconfs, container.size());

        container.remove(0);
        Assertions.assertEquals(nconfs - 1, container.size());
    }

    @Test
    void testIndexOf_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        IAtomContainer ac = container.get(2);
        int index = container.indexOf(ac);
        Assertions.assertEquals(2, index);
    }

    @Test
    void testAdd_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        base.setTitle("junk");
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    container.add(base);
                                });
    }

    @Test
    void testGet_int() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                                () -> {
                                    container.get(100);
                                });
    }

    @Test
    void testGet2() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                                () -> {
                                    for (int i = 0; i < container.size() + 1; i++)
                                        container.get(i);
                                });
    }

    @Test
    void testAdd_int_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        container.add(5, confs[5]);
    }

    @Test
    void testAdd_int_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        container.add(5, confs[5]);
    }

    @Test
    void testAdd_Object() {
        ConformerContainer container = new ConformerContainer();
        Assertions.assertNotNull(container);
        for (IAtomContainer conf : confs)
            container.add(conf);
        Assertions.assertEquals(nconfs, container.size());
    }

    @Test
    void testIndexOf_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertNotNull(container);

        int counter = 0;
        for (IAtomContainer conf : confs) {
            Assertions.assertEquals(counter, container.indexOf(conf));
            counter++;
        }
    }

    @Test
    void testClear() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, container.size());
        container.clear();
        Assertions.assertEquals(0, container.size());
    }

    @Test
    void testSize() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, container.size());
    }

    @Test
    void testLastIndexOf_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, container.size());
        int x = container.lastIndexOf(container.get(3));
        Assertions.assertEquals(3, container.lastIndexOf(container.get(3)));
    }

    @Test
    void testContains_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, container.size());
        Assertions.assertTrue(container.contains(container.get(3)));
    }

    @Test
    void testAddAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, container.size());
        Assertions.assertTrue(container.contains(container.get(3)));
    }

    @Test
    void testAddAll_int_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.addAll(5, null);
                                });
    }

    @Test
    void testToArray_arrayObject() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.toArray(new IAtomContainer[]{});
                                });
    }

    @Test
    void testRemove_Object() {
        ConformerContainer cContainer = new ConformerContainer(confs);
        Assertions.assertEquals(nconfs, cContainer.size());
        IAtomContainer container = cContainer.get(3);
        Assertions.assertTrue(cContainer.contains(container));
        cContainer.remove(container);
        Assertions.assertEquals(nconfs - 1, cContainer.size());
        Assertions.assertFalse(cContainer.contains(container));
    }

    @Test
    void testSet_int_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(confs);
        int location = 5;
        container.set(location, container.get(location + 1));
        Assertions.assertEquals(location, container.indexOf(container.get(location + 1)));
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
    void testSet_int_Object() {
        ConformerContainer container = new ConformerContainer(confs);
        int location = 5;
        container.set(location, container.get(location + 1));
        Assertions.assertEquals(location, container.indexOf(container.get(location + 1)));
    }

    @Test
    void testContainsAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(nconfs, container.size());
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    Assertions.assertTrue(container.containsAll(container));
                                });
    }

    @Test
    void testRemoveAll_Collection() {
        ConformerContainer container = new ConformerContainer(confs);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(nconfs, container.size());
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.removeAll(container);
                                });
    }

    @Test
    void testRetainAll_Collection() {
        ConformerContainer container = new ConformerContainer(base);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.retainAll(null);
                                });
    }

    @Test
    void testSubList_int_int() {
        ConformerContainer container = new ConformerContainer(base);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.subList(3, 4);
                                });
    }

    @Test
    void testListIterator() {
        ConformerContainer container = new ConformerContainer(base);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.listIterator();
                                });
    }

    @Test
    void testListIterator_int() {
        ConformerContainer container = new ConformerContainer(base);
        Assertions.assertThrows(UnsupportedOperationException.class,
                                () -> {
                                    container.listIterator(1);
                                });
    }

    @Test
    void testConformerContainer_IAtomContainer() {
        ConformerContainer container = new ConformerContainer(base);
        Assertions.assertNotNull(container);
        Assertions.assertEquals(1, container.size());
    }

}

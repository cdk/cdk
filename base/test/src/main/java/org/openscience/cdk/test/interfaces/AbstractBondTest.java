/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IBond} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractBondTest extends AbstractElectronContainerTest {

    @Test
    @Override
    public void testCompare_Object() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        IBond b2 = (IBond) newChemObject();
        b2.setAtom(c, 0);
        b2.setAtom(o, 1);
        b2.setOrder(Order.SINGLE);

        Assertions.assertTrue(b.compare(b2));
    }

    @Test
    public void testContains_IAtom() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        Assertions.assertTrue(b.contains(c));
        Assertions.assertTrue(b.contains(o));
    }

    @Test
    public void testGetAtomCount() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        Assertions.assertEquals(2.0, b.getAtomCount(), 0.001);
    }

    @Test
    public void testSetAtoms_arrayIAtom() {
        IBond b = (IBond) newChemObject();
        IAtom[] atomsToAdd = new IAtom[2];
        atomsToAdd[0] = b.getBuilder().newInstance(IAtom.class, "C");
        atomsToAdd[1] = b.getBuilder().newInstance(IAtom.class, "O");

        b.setAtoms(atomsToAdd);

        Assertions.assertEquals(2, b.getAtomCount());
        Assertions.assertEquals(atomsToAdd[0], b.getBegin());
        Assertions.assertEquals(atomsToAdd[1], b.getEnd());
    }

    @Test
    public void testSetAtom_SomeNull() {
        IBond b = (IBond) newChemObject();
        b.setAtom(b.getBuilder().newInstance(IAtom.class, "C"), 0);
        Assertions.assertEquals(1, b.getAtomCount());
    }

    @Test
    public void testUnSetAtom() {
        IBond b = (IBond) newChemObject();
        b.setAtom(b.getBuilder().newInstance(IAtom.class, "C"), 0);
        Assertions.assertEquals(1, b.getAtomCount());
        b.setAtom(b.getBuilder().newInstance(IAtom.class, "C"), 0);
        Assertions.assertEquals(1, b.getAtomCount());
        b.setAtom(null, 0);
        Assertions.assertEquals(0, b.getAtomCount());
        b.setAtom(null, 0);
        Assertions.assertEquals(0, b.getAtomCount());
    }

    @Test
    public void testOverwriteAtom() {
        IBond b = (IBond) newChemObject();
        b.setAtom(b.getBuilder().newInstance(IAtom.class, "C"), 0);
        Assertions.assertEquals(1, b.getAtomCount());
        b.setAtom(b.getBuilder().newInstance(IAtom.class, "C"), 0);
        Assertions.assertEquals(1, b.getAtomCount());

        // test overwrite with null
        b.setAtom(null, 0);
        Assertions.assertEquals(0, b.getAtomCount());
        b.setAtom(null, 0);
        Assertions.assertEquals(0, b.getAtomCount());
    }

    @Test
    public void testAtoms() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        Iterator<IAtom> atoms = b.atoms().iterator();
        Assertions.assertEquals(2, b.getAtomCount());
        Assertions.assertTrue(atoms.hasNext());
        Assertions.assertEquals(c, atoms.next());
        Assertions.assertTrue(atoms.hasNext());
        Assertions.assertEquals(o, atoms.next());
        Assertions.assertFalse(atoms.hasNext());
    }

    @Test
    public void testGetAtom_int() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        Assertions.assertEquals(c, b.getBegin());
        Assertions.assertEquals(o, b.getEnd());
    }

    @Test
    public void testSetAtom_IAtom_int() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");

        b.setAtom(c, 0);
        b.setAtom(o, 1);

        Assertions.assertEquals(c, b.getBegin());
        Assertions.assertEquals(o, b.getEnd());
    }

    @Test
    public void testGetConnectedAtom_IAtom() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);

        Assertions.assertEquals(c, b.getOther(o));
        Assertions.assertEquals(o, b.getOther(c));

        // test default return value
        Assertions.assertNull(b.getOther(b.getBuilder().newInstance(IAtom.class)));
    }

    @Test
    public void testGetConnectedAtoms_IAtom() {
        IBond b = (IBond) newChemObject();
        IAtom[] atoms = new IAtom[3];
        atoms[0] = b.getBuilder().newInstance(IAtom.class, "B");
        atoms[1] = b.getBuilder().newInstance(IAtom.class, "H");
        atoms[2] = b.getBuilder().newInstance(IAtom.class, "B");

        b.setAtoms(atoms);
        b.setOrder(IBond.Order.SINGLE); // C=O bond

        IAtom[] connectedAtoms = b.getConnectedAtoms(atoms[1]);
        Assertions.assertNotNull(connectedAtoms);
        Assertions.assertEquals(2, connectedAtoms.length);
        Assertions.assertNotNull(connectedAtoms[0]);
        Assertions.assertNotNull(connectedAtoms[1]);

        // test default return value
        connectedAtoms = b.getConnectedAtoms(b.getBuilder().newInstance(IAtom.class));
        Assertions.assertNull(connectedAtoms);
    }

    @Test
    public void testIsConnectedTo_IBond() {
        IBond b = (IBond) newChemObject();
        IAtom c1 = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        IAtom c2 = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom c3 = b.getBuilder().newInstance(IAtom.class, "C");

        IBond b1 = b.getBuilder().newInstance(IBond.class, c1, o);
        IBond b2 = b.getBuilder().newInstance(IBond.class, o, c2);
        IBond b3 = b.getBuilder().newInstance(IBond.class, c2, c3);

        Assertions.assertTrue(b1.isConnectedTo(b2));
        Assertions.assertTrue(b2.isConnectedTo(b1));
        Assertions.assertTrue(b2.isConnectedTo(b3));
        Assertions.assertTrue(b3.isConnectedTo(b2));
        Assertions.assertFalse(b1.isConnectedTo(b3));
        Assertions.assertFalse(b3.isConnectedTo(b1));
    }

    @Test
    public void testGetOrder() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.DOUBLE);

        Assertions.assertEquals(Order.DOUBLE, b.getOrder());
    }

    @Test
    public void testSetOrder_IBond_Order() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.DOUBLE);

        Assertions.assertEquals(Order.DOUBLE, b.getOrder());

        b.setOrder(IBond.Order.SINGLE);
        Assertions.assertEquals(Order.SINGLE, b.getOrder());
    }

    @Test
    public void testSetOrder_electronCounts() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "C");

        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SINGLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(2, b.getElectronCount().intValue());

        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.DOUBLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(4, b.getElectronCount().intValue());

        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.TRIPLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(6, b.getElectronCount().intValue());

        // OK, a bit hypothetical
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.QUADRUPLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(8, b.getElectronCount().intValue());

        // OK, a bit hypothetical
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.QUINTUPLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(10, b.getElectronCount().intValue());

        // OK, a bit hypothetical
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.SEXTUPLE);
        Assertions.assertNotNull(b.getElectronCount());
        Assertions.assertEquals(12, b.getElectronCount().intValue());
    }

    @Test
    public void testSetStereo_IBond_Stereo() {
        IBond b = (IBond) newChemObject();
        IAtom c = b.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = b.getBuilder().newInstance(IAtom.class, "O");
        b.setAtom(c, 0);
        b.setAtom(o, 1);
        b.setOrder(Order.DOUBLE);
        b.setStereo(IBond.Stereo.DOWN);
        Assertions.assertEquals(IBond.Stereo.DOWN, b.getStereo());
        b.setStereo(IBond.Stereo.UP);
        Assertions.assertEquals(IBond.Stereo.UP, b.getStereo());
    }

    @Test
    public void testGetStereo() {
        IChemObject object = newChemObject();
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O");

        IBond b = object.getBuilder().newInstance(IBond.class, c, o, IBond.Order.DOUBLE, IBond.Stereo.UP);
        Assertions.assertEquals(IBond.Stereo.UP, b.getStereo());
    }

    @Test
    public void testGet2DCenter() {
        IChemObject object = newChemObject();
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O", new Point2d(0.0, 0.0));
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C", new Point2d(1.0, 1.0));
        IBond b = object.getBuilder().newInstance(IBond.class, c, o);

        Assertions.assertEquals(0.5, b.get2DCenter().x, 0.001);
        Assertions.assertEquals(0.5, b.get2DCenter().y, 0.001);
    }

    @Test
    public void testGet3DCenter() {
        IChemObject object = newChemObject();
        IAtom o = object.getBuilder().newInstance(IAtom.class, "O", new Point3d(0.0, 0.0, 0.0));
        IAtom c = object.getBuilder().newInstance(IAtom.class, "C", new Point3d(1.0, 1.0, 1.0));
        IBond b = object.getBuilder().newInstance(IBond.class, c, o);

        Assertions.assertEquals(0.5, b.get3DCenter().x, 0.001);
        Assertions.assertEquals(0.5, b.get3DCenter().y, 0.001);
        Assertions.assertEquals(0.5, b.get3DCenter().z, 0.001);
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IBond bond = (IBond) newChemObject();
        Object clone = bond.clone();
        Assertions.assertNotNull(clone);
        Assertions.assertTrue(clone instanceof IBond);
    }

    @Test
    public void testClone_IAtom() throws Exception {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = object.getBuilder().newInstance(IBond.class, atom1, atom2);
        IBond clone = bond.clone();

        // test cloning of atoms
        Assertions.assertNotSame(atom1, clone.getBegin());
        Assertions.assertNotSame(atom2, clone.getEnd());
    }

    @Test
    public void testClone_Order() throws Exception {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = object.getBuilder().newInstance(IBond.class, atom1, atom2, IBond.Order.SINGLE);
        IBond clone = bond.clone();

        // test cloning of bond order
        bond.setOrder(IBond.Order.DOUBLE);
        Assertions.assertEquals(Order.SINGLE, clone.getOrder());
    }

    @Test
    public void testClone_Stereo() throws Exception {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IBond bond = object.getBuilder().newInstance(IBond.class, atom1, atom2, IBond.Order.SINGLE, IBond.Stereo.UP);
        IBond clone = bond.clone();

        // test cloning of bond order
        bond.setStereo(IBond.Stereo.UP_INVERTED);
        Assertions.assertEquals(IBond.Stereo.UP, clone.getStereo());
    }

    /**
     * Test for RFC #9
     */
    @Test
    @Override
    public void testToString() {
        IBond bond = (IBond) newChemObject();
        String description = bond.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    public void testMultiCenter1() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3});
        Assertions.assertEquals(3, bond.getAtomCount());
        Assertions.assertEquals(atom1, bond.getAtom(0));
        Assertions.assertEquals(atom2, bond.getAtom(1));
        Assertions.assertEquals(atom3, bond.getAtom(2));

        Assertions.assertEquals(bond.getOrder(), CDKConstants.UNSET);
    }

    @Test
    public void testMultiCenterCompare() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3});
        IBond bond2 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3});

        Assertions.assertTrue(bond1.compare(bond2));

        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");
        IBond bond3 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom4});
        Assertions.assertFalse(bond1.compare(bond3));
    }

    @Test
    public void testMultiCenterContains() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3});
        Assertions.assertTrue(bond1.contains(atom1));
        Assertions.assertTrue(bond1.contains(atom2));
        Assertions.assertTrue(bond1.contains(atom3));
        Assertions.assertFalse(bond1.contains(atom4));
    }

    @Test
    public void testMultiCenterIterator() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3, atom4});
        Iterator<IAtom> atoms = bond1.atoms().iterator();
        int natom = 0;
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            Assertions.assertNotNull(atom);
            natom++;
        }
        Assertions.assertEquals(4, natom);
    }

    @Test
    public void testMultiCenterConnectedAtoms() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3, atom4});
        Assertions.assertEquals(atom2, bond1.getOther(atom1));
        Assertions.assertNull(bond1.getOther(object.getBuilder().newInstance(IAtom.class)));

        IAtom[] conAtoms = bond1.getConnectedAtoms(atom1);
        boolean correct = true;
        for (IAtom atom : conAtoms) {
            if (atom == atom1) {
                correct = false;
                break;
            }
        }
        Assertions.assertTrue(correct);

        conAtoms = bond1.getConnectedAtoms(atom3);
        correct = true;
        for (IAtom atom : conAtoms) {
            if (atom == atom3) {
                correct = false;
                break;
            }
        }
        Assertions.assertTrue(correct);
    }

    @Test
    public void testMultiCenterIsConnectedTo() {
        IChemObject object = newChemObject();
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom2 = object.getBuilder().newInstance(IAtom.class, "O");
        IAtom atom3 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom4 = object.getBuilder().newInstance(IAtom.class, "C");
        IAtom atom5 = object.getBuilder().newInstance(IAtom.class, "C");

        IBond bond1 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom1, atom2, atom3});
        IBond bond2 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom2, atom3, atom4});
        IBond bond3 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom2, atom4});
        IBond bond4 = object.getBuilder().newInstance(IBond.class, new IAtom[]{atom5, atom4});

        Assertions.assertTrue(bond1.isConnectedTo(bond2));
        Assertions.assertTrue(bond2.isConnectedTo(bond1));
        Assertions.assertTrue(bond1.isConnectedTo(bond3));
        Assertions.assertFalse(bond4.isConnectedTo(bond1));
    }

}

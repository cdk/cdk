package org.openscience.cdk.test.applications.jchempaint.undoredo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.jchempaint.undoredo.AdjustBondOrdesEdit;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Junit test for the AdjustBondOrdesEdit class
 * 
 * @author tohel
 * 
 */
public class AdjustBondOrdesEditTest extends CDKTestCase {

    private Molecule clearMol;

    private Molecule fittingMol;

    private SaturationChecker satChecker;

    public AdjustBondOrdesEditTest() throws IOException, ClassNotFoundException {
        satChecker = new SaturationChecker();
    }

    public static Test suite() {
        return new TestSuite(AdjustBondOrdesEditTest.class);
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.AdjustBondOrdesEdit.redo()'
     */
    public void testClearBondOrdersRedo() throws IOException,
            ClassNotFoundException {
        HashMap changedBonds = makeClearMolecule();
        AdjustBondOrdesEdit edit = new AdjustBondOrdesEdit(changedBonds);
        edit.undo();
        edit.redo();
        Set keys = changedBonds.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Bond bond = (Bond) it.next();
            double[] bondOrders = (double[]) changedBonds.get(bond);
            assertTrue(bond.getOrder() == bondOrders[0]);
        }
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.AdjustBondOrdesEdit.redo()'
     */
    public void testFitBondOrdersRedo() throws CDKException {
        HashMap changedBonds = makeFittingMolecule();
        AdjustBondOrdesEdit edit = new AdjustBondOrdesEdit(changedBonds);
        edit.undo();
        edit.redo();
        Set keys = changedBonds.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Bond bond = (Bond) it.next();
            double[] bondOrders = (double[]) changedBonds.get(bond);
            assertTrue(bond.getOrder() == bondOrders[0]);
        }
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.AdjustBondOrdesEdit.undo()'
     */
    public void testClearBondOrdersUndo() throws IOException,
            ClassNotFoundException {
        HashMap changedBonds = makeClearMolecule();
        AdjustBondOrdesEdit edit = new AdjustBondOrdesEdit(changedBonds);
        edit.undo();
        Set keys = changedBonds.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Bond bond = (Bond) it.next();
            double[] bondOrders = (double[]) changedBonds.get(bond);
            assertTrue(bond.getOrder() == bondOrders[1]);
        }
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.AdjustBondOrdesEdit.undo()'
     */
    public void testFitBondOrdersUndo() throws CDKException {
        HashMap changedBonds = makeFittingMolecule();
        AdjustBondOrdesEdit edit = new AdjustBondOrdesEdit(changedBonds);
        edit.undo();
        Set keys = changedBonds.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Bond bond = (Bond) it.next();
            double[] bondOrders = (double[]) changedBonds.get(bond);
            assertTrue(bond.getOrder() == bondOrders[1]);
        }
    }

    private HashMap makeClearMolecule() throws IOException,
            ClassNotFoundException {
        HashMap changedBonds = new HashMap();
        clearMol = MoleculeFactory.makeAlphaPinene();
        AtomContainer containerCopy = (AtomContainer) clearMol.clone();
        satChecker.unsaturate(clearMol.getBonds());
        for (int j = 0; j < containerCopy.getBondCount(); j++) {
            Bond bondCopy = containerCopy.getBondAt(j);
            Bond bond = clearMol.getBondAt(j);
            if (bond.getOrder() != bondCopy.getOrder()) {
                double[] bondOrders = new double[2];
                bondOrders[0] = bond.getOrder();
                bondOrders[1] = bondCopy.getOrder();
                changedBonds.put(bond, bondOrders);
            }
        }
        return changedBonds;
    }

    private HashMap makeFittingMolecule() throws CDKException {
        HashMap changedBonds = new HashMap();
        fittingMol = MoleculeFactory.makeAlphaPinene();
        AtomContainer containerCopy = (AtomContainer) fittingMol.clone();
        satChecker.saturate(fittingMol);
        for (int j = 0; j < containerCopy.getBondCount(); j++) {
            Bond bondCopy = containerCopy.getBondAt(j);
            Bond bond = fittingMol.getBondAt(j);
            if (bond.getOrder() != bondCopy.getOrder()) {
                double[] bondOrders = new double[2];
                bondOrders[0] = bond.getOrder();
                bondOrders[1] = bondCopy.getOrder();
                changedBonds.put(bond, bondOrders);
            }
        }
        return changedBonds;
    }

}

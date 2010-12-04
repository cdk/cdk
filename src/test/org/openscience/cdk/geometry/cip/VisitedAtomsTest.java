package org.openscience.cdk.geometry.cip;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.IAtom;

public class VisitedAtomsTest {

    @Test
    public void testConstructor() {
        VisitedAtoms visited = new VisitedAtoms();
        Assert.assertNotNull(visited);
    }

    @Test
    public void testVisiting() {
        VisitedAtoms visited = new VisitedAtoms();
        IAtom atom = new Atom("C");
        Assert.assertFalse(visited.isVisited(atom));
        visited.visited(atom);
        Assert.assertTrue(visited.isVisited(atom));
    }

    @Test
    public void testAddedVisitedAtoms() {
        VisitedAtoms visited = new VisitedAtoms();
        IAtom atom = new Atom("C");

        VisitedAtoms visitedToAdd = new VisitedAtoms();
        visited.visited(atom);

        Assert.assertFalse(visitedToAdd.isVisited(atom));
        visitedToAdd.visited(visited);
        Assert.assertTrue(visitedToAdd.isVisited(atom));
    }
}

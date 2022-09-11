package org.openscience.cdk.renderer.generators.standard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.SymbolVisibility;
import org.openscience.cdk.silent.Atom;

import javax.vecmath.Point2d;

import java.awt.Color;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelectionVisibilityTest {

    @Test
    void noHighlightOrGlow() {
        IAtomContainer methyl = new AtomContainer();
        methyl.addAtom(atomAt("C", new Point2d(0, 0)));
        methyl.addAtom(atomAt("H", new Point2d(0, 1)));
        methyl.addAtom(atomAt("H", new Point2d(0, -1)));
        methyl.addAtom(atomAt("H", new Point2d(1, 0)));
        methyl.addAtom(atomAt("H", new Point2d(-1, 0)));
        methyl.addBond(0, 1, IBond.Order.SINGLE);
        methyl.addBond(0, 2, IBond.Order.SINGLE);
        methyl.addBond(0, 3, IBond.Order.SINGLE);
        methyl.addBond(0, 4, IBond.Order.SINGLE);
        SymbolVisibility visibility = SelectionVisibility.all(SymbolVisibility.iupacRecommendations());
        Assertions.assertFalse(visibility.visible(methyl.getAtom(0), methyl.getConnectedBondsList(methyl.getAtom(0)),
                                                  new RendererModel()));
    }

    @Test
    void withHighlight() {
        IAtomContainer methyl = new AtomContainer();
        methyl.addAtom(atomAt("C", new Point2d(0, 0)));
        methyl.addAtom(atomAt("H", new Point2d(0, 1)));
        methyl.addAtom(atomAt("H", new Point2d(0, -1)));
        methyl.addAtom(atomAt("H", new Point2d(1, 0)));
        methyl.addAtom(atomAt("H", new Point2d(-1, 0)));
        methyl.getAtom(0).setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
        methyl.addBond(0, 1, IBond.Order.SINGLE);
        methyl.addBond(0, 2, IBond.Order.SINGLE);
        methyl.addBond(0, 3, IBond.Order.SINGLE);
        methyl.addBond(0, 4, IBond.Order.SINGLE);
        SymbolVisibility visibility = SelectionVisibility.all(SymbolVisibility.iupacRecommendations());
        Assertions.assertTrue(visibility.visible(methyl.getAtom(0), methyl.getConnectedBondsList(methyl.getAtom(0)),
                                                 new RendererModel()));
    }

    @Test
    void isolated() {
        IAtomContainer methyl = new AtomContainer();
        methyl.addAtom(atomAt("C", new Point2d(0, 0)));
        methyl.addAtom(atomAt("H", new Point2d(0, 1)));
        methyl.addAtom(atomAt("H", new Point2d(0, -1)));
        methyl.addAtom(atomAt("H", new Point2d(1, 0)));
        methyl.addAtom(atomAt("H", new Point2d(-1, 0)));
        methyl.addBond(0, 1, IBond.Order.SINGLE);
        methyl.addBond(0, 2, IBond.Order.SINGLE);
        methyl.addBond(0, 3, IBond.Order.SINGLE);
        methyl.addBond(0, 4, IBond.Order.SINGLE);
        methyl.getAtom(0).setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
        SymbolVisibility visibility = SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendations());
        Assertions.assertTrue(visibility.visible(methyl.getAtom(0), methyl.getConnectedBondsList(methyl.getAtom(0)),
                                                 new RendererModel()));
    }

    @Test
    void unIsolated() {
        IAtomContainer methyl = new AtomContainer();
        methyl.addAtom(atomAt("C", new Point2d(0, 0)));
        methyl.addAtom(atomAt("H", new Point2d(0, 1)));
        methyl.addAtom(atomAt("H", new Point2d(0, -1)));
        methyl.addAtom(atomAt("H", new Point2d(1, 0)));
        methyl.addAtom(atomAt("H", new Point2d(-1, 0)));
        methyl.addBond(0, 1, IBond.Order.SINGLE);
        methyl.addBond(0, 2, IBond.Order.SINGLE);
        methyl.addBond(0, 3, IBond.Order.SINGLE);
        methyl.addBond(0, 4, IBond.Order.SINGLE);
        methyl.getAtom(0).setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
        methyl.getBond(0).setProperty(StandardGenerator.HIGHLIGHT_COLOR, Color.RED);
        SymbolVisibility visibility = SelectionVisibility.disconnected(SymbolVisibility.iupacRecommendations());
        Assertions.assertFalse(visibility.visible(methyl.getAtom(0), methyl.getConnectedBondsList(methyl.getAtom(0)),
                                                  new RendererModel()));
    }

    @Test
    void highlightIsSelected() {
        IChemObject chemObject = mock(IChemObject.class);
        when(chemObject.getProperty(StandardGenerator.HIGHLIGHT_COLOR)).thenReturn(Color.RED);
        Assertions.assertTrue(SelectionVisibility.isSelected(chemObject, new RendererModel()));
    }

    static IAtom atomAt(String symb, Point2d p) {
        IAtom atom = new Atom(symb);
        atom.setPoint2d(p);
        return atom;
    }

}

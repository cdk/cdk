/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.renderer.generators.standard;

import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.renderer.RendererModel;

import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StandardAtomGeneratorTest {

    private final Font                  font          = new Font("Verdana", Font.PLAIN, 12);
    private final TextOutline           element       = new TextOutline("N", font);
    private final TextOutline           hydrogen      = new TextOutline("H", font);
    private final StandardAtomGenerator atomGenerator = new StandardAtomGenerator(font);

    @Test
    public void positionHydrogenAbove() {
        TextOutline positioned = atomGenerator.positionHydrogenLabel(HydrogenPosition.Above, element, hydrogen);
        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D hydrogenBounds = positioned.getBounds();

        assertThat(elementBounds.getMinY(), greaterThan(hydrogenBounds.getMaxY()));
        assertThat(elementBounds.getMinX(), closeTo(hydrogenBounds.getMinX(), 0.01));
    }

    @Test
    public void positionHydrogenBelow() {
        TextOutline positioned = atomGenerator.positionHydrogenLabel(HydrogenPosition.Below, element, hydrogen);
        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D hydrogenBounds = positioned.getBounds();

        assertThat(elementBounds.getMinY(), lessThan(hydrogenBounds.getMaxY()));
        assertThat(elementBounds.getMinX(), closeTo(hydrogenBounds.getMinX(), 0.01));
    }

    @Test
    public void positionHydrogenToLeft() {
        TextOutline positioned = atomGenerator.positionHydrogenLabel(HydrogenPosition.Left, element, hydrogen);
        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D hydrogenBounds = positioned.getBounds();

        assertThat(elementBounds.getMaxX(), greaterThan(hydrogenBounds.getMinX()));
        assertThat(elementBounds.getMaxY(), closeTo(hydrogenBounds.getMaxY(), 0.01));
    }

    @Test
    public void positionHydrogenToRight() {

        TextOutline positioned = atomGenerator.positionHydrogenLabel(HydrogenPosition.Right, element, hydrogen);
        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D hydrogenBounds = positioned.getBounds();

        assertThat(elementBounds.getMaxX(), lessThan(hydrogenBounds.getMinX()));
        assertThat(elementBounds.getMaxY(), closeTo(hydrogenBounds.getMaxY(), 0.01));
    }

    @Test
    public void positionHydrogenCount() {
        TextOutline hydrogenCount = new TextOutline("2", font);
        TextOutline positioned = atomGenerator.positionSubscript(hydrogen, hydrogenCount);

        Rectangle2D hydrogenBounds = hydrogen.getBounds();
        Rectangle2D hydrogenCountBounds = positioned.getBounds();

        assertThat(hydrogenCountBounds.getMinX(), greaterThan(hydrogenBounds.getMinX()));
        assertThat(hydrogenCountBounds.getCenterY(), closeTo(hydrogenBounds.getMaxY(), 0.01));
    }

    @Test
    public void positionMassLabel() {
        TextOutline mass = new TextOutline("15", font);
        TextOutline positioned = atomGenerator.positionMassLabel(mass, element);

        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D massBounds = positioned.getBounds();

        assertThat(massBounds.getMaxX(), lessThan(elementBounds.getMinX()));
        assertThat(massBounds.getCenterY(), closeTo(elementBounds.getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenHydrogensAreRight() {
        // hydrogen is arbitrarily moved to ensure x/y are different from the element
        TextOutline charge = new TextOutline("+", font);
        TextOutline localHydrogen = hydrogen.translate(10, 10);
        TextOutline positioned = atomGenerator.positionChargeLabel(1, HydrogenPosition.Right, charge, element,
                localHydrogen);

        Rectangle2D hydrogenBounds = localHydrogen.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        assertThat(chargeBounds.getMinX(), greaterThan(hydrogenBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(hydrogenBounds.getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenNoHydrogensAreRight() {
        // hydrogen is arbitrarily moved to ensure x/y are different from the element
        TextOutline charge = new TextOutline("+", font);
        TextOutline localHydrogen = hydrogen.translate(10, 10);

        TextOutline positioned = atomGenerator.positionChargeLabel(0, HydrogenPosition.Right, charge, element,
                localHydrogen);

        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        assertThat(chargeBounds.getMinX(), greaterThan(elementBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(elementBounds.getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenHydrogensAreLeft() {
        // hydrogen is arbitrarily moved to ensure x/y are different from the element
        TextOutline charge = new TextOutline("+", font);
        TextOutline localHydrogen = hydrogen.translate(10, 10);
        TextOutline positioned = atomGenerator.positionChargeLabel(1, HydrogenPosition.Left, charge, element,
                localHydrogen);

        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        assertThat(chargeBounds.getMinX(), greaterThan(elementBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(localHydrogen.getBounds().getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenHydrogensAreBelow() {
        TextOutline charge = new TextOutline("+", font);
        TextOutline positioned = atomGenerator.positionChargeLabel(1, HydrogenPosition.Below, charge, element,
                hydrogen.translate(0, 5));

        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        assertThat(chargeBounds.getMinX(), greaterThan(elementBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(elementBounds.getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenTwoHydrogensAreAbove() {
        // hydrogen is arbitrarily moved to ensure x/y are different from the element
        TextOutline charge = new TextOutline("+", font);
        TextOutline localHydrogen = hydrogen.translate(10, 10);
        TextOutline positioned = atomGenerator.positionChargeLabel(2, HydrogenPosition.Above, charge, element,
                localHydrogen);

        Rectangle2D hydrogenBounds = localHydrogen.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        Rectangle2D elementBounds = element.getBounds();
        assertThat(chargeBounds.getMinX(), greaterThan(elementBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(elementBounds.getMinY(), 0.01));
    }

    @Test
    public void positionOfChargeWhenOneHydrogenIsAbove() {
        // hydrogen is arbitrarily moved to ensure x/y are different from the element
        TextOutline charge = new TextOutline("+", font);
        TextOutline localHydrogen = hydrogen.translate(10, 10);
        TextOutline positioned = atomGenerator.positionChargeLabel(1, HydrogenPosition.Above, charge, element,
                localHydrogen);

        Rectangle2D elementBounds = element.getBounds();
        Rectangle2D chargeBounds = positioned.getBounds();

        assertThat(chargeBounds.getMinX(), greaterThan(elementBounds.getMinX()));
        assertThat(chargeBounds.getCenterY(), closeTo(elementBounds.getMinY(), 0.01));
    }

    @Test
    public void generateWithNoAdjuncts() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 0, -1, 0, 0, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(1));
    }

    @Test
    public void generateWithHydrogenAdjunct() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 1, -1, 0, 0, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(2));
    }

    @Test
    public void generateWithHydrogenAndCountAdjunct() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 2, -1, 0, 0, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(3));
    }

    @Test
    public void generateWithMassAdjunct() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 0, 15, 0, 0, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(2));
    }

    @Test
    public void generateWithChargeAdjunct() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 0, -1, 1, 0, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(2));
    }

    @Test
    public void generateWithRadicalAdjunct() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 0, -1, 0, 1, HydrogenPosition.Right);
        assertThat(symbol.getOutlines().size(), is(2));
    }

    @Test
    public void hydrogenDodgesMassLabel() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 1, 15, 0, 0, HydrogenPosition.Left);
        List<Shape> outlines = symbol.getOutlines();
        assertThat(outlines.size(), is(3));
        Shape hydrogenShape = outlines.get(1);
        Shape massShape = outlines.get(2);
        assertThat(hydrogenShape.getBounds2D().getMaxX(), lessThan(massShape.getBounds2D().getMinX()));
    }

    @Test
    public void hydrogenAndHydrogenCountDodgesMassLabel() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 2, 15, 0, 0, HydrogenPosition.Left);
        List<Shape> outlines = symbol.getOutlines();
        assertThat(outlines.size(), is(4));
        Shape hydrogenShape = outlines.get(1);
        Shape hydrogenCountShape = outlines.get(2);
        Shape massShape = outlines.get(3);

        assertThat(hydrogenShape.getBounds2D().getMaxX(), lessThan(massShape.getBounds2D().getMinX()));

        // the count subscript and mass overlap a little
        assertThat(hydrogenCountShape.getBounds2D().getMaxX(), greaterThan(massShape.getBounds2D().getMinX()));
        assertThat(hydrogenCountShape.getBounds2D().getMaxX(), lessThan(massShape.getBounds2D().getMaxX()));

        assertThat(hydrogenShape.getBounds2D().getMaxX(), lessThan(hydrogenCountShape.getBounds2D().getMinX()));
    }

    @Test
    public void hydrogenCountDodgesElement() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 2, -1, 0, 0, HydrogenPosition.Left);
        List<Shape> outlines = symbol.getOutlines();
        assertThat(outlines.size(), is(3));
        Shape elementShape = outlines.get(0);
        Shape hydrogenShape = outlines.get(1);
        Shape hydrogenCountShape = outlines.get(2);

        assertThat(hydrogenCountShape.getBounds2D().getMaxX(), lessThan(elementShape.getBounds2D().getMinX()));
        assertThat(hydrogenShape.getBounds2D().getMaxX(), lessThan(hydrogenCountShape.getBounds2D().getMinX()));
    }

    @Test
    public void hydrogenDoesNotNeedToDodge() {
        AtomSymbol symbol = atomGenerator.generatePeriodicSymbol(7, 1, -1, 0, 0, HydrogenPosition.Left);
        List<Shape> outlines = symbol.getOutlines();
        assertThat(outlines.size(), is(2));
        Shape elementShape = outlines.get(0);
        Shape hydrogenShape = outlines.get(1);
        assertThat(hydrogenShape.getBounds2D().getMaxX(), lessThan(elementShape.getBounds2D().getMinX()));
    }

    @Test
    public void anion() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(-1, 0), is("−"));
    }

    @Test
    public void cation() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(1, 0), is("+"));
    }

    @Test
    public void dianion() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(-2, 0), is("2−"));
    }

    @Test
    public void dication() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(2, 0), is("2+"));
    }

    @Test
    public void radical() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(0, 1), is("•"));
    }

    @Test
    public void diradical() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(0, 2), is("2•"));
    }

    @Test
    public void diradicalCation() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(1, 2), is("(2•)+"));
    }

    @Test
    public void radicalAndAnion() throws Exception {
        assertThat(StandardAtomGenerator.chargeAdjunctText(-1, 1), is("(•)−"));
    }

    @Test
    public void accessNullPseudoLabel() throws Exception {
        IPseudoAtom atom = mock(IPseudoAtom.class);
        when(atom.getLabel()).thenReturn(null);
        assertThat(StandardAtomGenerator.accessPseudoLabel(atom, "*"), is("*"));
    }

    @Test
    public void accessEmptyPseudoLabel() throws Exception {
        IPseudoAtom atom = mock(IPseudoAtom.class);
        when(atom.getLabel()).thenReturn("");
        assertThat(StandardAtomGenerator.accessPseudoLabel(atom, "*"), is("*"));
    }

    @Test
    public void accessRgroupPseudoLabel() throws Exception {
        IPseudoAtom atom = mock(IPseudoAtom.class);
        when(atom.getLabel()).thenReturn("R1");
        assertThat(StandardAtomGenerator.accessPseudoLabel(atom, "*"), is("R1"));
    }

    @Test
    public void numberedRgroupSymbol() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("R1", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void RgroupSymbol2A() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("R2a", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void RgroupSymbolY() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("Y1a2", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(1));
    }

    @Test
    public void RgroupSymbolPrime() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("R'", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void RgroupSymbolNumberedPrime() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("R2'", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(3));
    }

    @Test
    public void pseudoSymbol() {
        AtomSymbol atomSymbol = atomGenerator.generatePseudoSymbol("Protein", HydrogenPosition.Right);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(1));
    }

    @Test
    public void generatesRgroupPseudoAtom() {
        IAtomContainer container = mock(IAtomContainer.class);
        IPseudoAtom atom = mock(IPseudoAtom.class);
        when(atom.getLabel()).thenReturn("R1");
        AtomSymbol    atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, new RendererModel());
        List<Shape>   shapes     = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    // the mass symbol is not displayed
    @Test
    public void generatesCarbon12() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(12);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        model.set(StandardGenerator.OmitMajorIsotopes.class, true);
        AtomSymbol    atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape>   shapes     = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(1));
    }

    @Test
    public void generatesCarbon13() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(13);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void nullMassNumber() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(null);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, new RendererModel());
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(1));
    }

    @Test
    public void nullHydrogenCount() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(12);
        when(atom.getImplicitHydrogenCount()).thenReturn(null);
        when(atom.getFormalCharge()).thenReturn(0);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void nullFormatCharge() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(12);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(null);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
    }

    @Test
    public void nullAtomicNumber() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(null);
        when(atom.getSymbol()).thenReturn("C");
        when(atom.getMassNumber()).thenReturn(12);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(2));
        assertThat(atomSymbol.elementOutline().text(), is("C"));
    }

    @Test
    public void nullAtomicNumberAndSymbol() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(null);
        when(atom.getSymbol()).thenReturn(null);
        when(atom.getMassNumber()).thenReturn(0);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, new RendererModel());
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(1));
        assertThat(atomSymbol.elementOutline().text(), is("*"));
    }

    @Test
    public void unpairedElectronsAreAccessed() {
        IAtomContainer container = mock(IAtomContainer.class);
        IAtom atom = mock(IAtom.class);
        when(atom.getAtomicNumber()).thenReturn(6);
        when(atom.getMassNumber()).thenReturn(12);
        when(atom.getImplicitHydrogenCount()).thenReturn(0);
        when(atom.getFormalCharge()).thenReturn(0);
        when(container.getConnectedSingleElectronsCount(atom)).thenReturn(1);
        RendererModel model      = new RendererModel();
        model.registerParameters(new StandardGenerator(new Font(Font.SANS_SERIF, Font.PLAIN, 12)));
        AtomSymbol atomSymbol = atomGenerator.generateSymbol(container, atom, HydrogenPosition.Left, model);
        List<Shape> shapes = atomSymbol.getOutlines();
        assertThat(shapes.size(), is(3));
        verify(container).getConnectedSingleElectronsCount(atom);
    }
}

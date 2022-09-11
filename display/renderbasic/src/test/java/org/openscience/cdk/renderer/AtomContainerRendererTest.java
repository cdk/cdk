/* Copyright (C) 2010  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.renderer;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.KekuleStructure;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.Shape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowEndCarbons;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;

/**
 * @author     maclean
 * @cdk.module test-renderbasic
 */
class AtomContainerRendererTest {

    private final IChemObjectBuilder        builder = SilentChemObjectBuilder.getInstance();

    private final StructureDiagramGenerator sdg     = new StructureDiagramGenerator();

    IAtomContainer layout(IAtomContainer molecule) {
        sdg.setMolecule(molecule);
        try {
            sdg.generateCoordinates();
        } catch (Exception e) {
            System.err.println(e);
        }
        return sdg.getMolecule();
    }

    IAtomContainer makeSquare() {
        IAtomContainer square = builder.newInstance(IAtomContainer.class);
        square.addAtom(builder.newInstance(IAtom.class, "C"));
        square.addAtom(builder.newInstance(IAtom.class, "C"));
        square.addAtom(builder.newInstance(IAtom.class, "C"));
        square.addAtom(builder.newInstance(IAtom.class, "C"));
        square.addBond(0, 1, IBond.Order.SINGLE);
        square.addBond(0, 3, IBond.Order.SINGLE);
        square.addBond(1, 2, IBond.Order.SINGLE);
        square.addBond(2, 3, IBond.Order.SINGLE);

        return layout(square);
    }

    @Test
    void testSquareMolecule() {
        IAtomContainer square = makeSquare();

        List<IGenerator<IAtomContainer>> generators = new ArrayList<>();
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        BasicAtomGenerator atomGenerator = new BasicAtomGenerator();
        generators.add(atomGenerator);

        AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());
        RendererModel model = renderer.getRenderer2DModel();
        model.getParameter(CompactShape.class).setValue(Shape.OVAL);
        model.getParameter(CompactAtom.class).setValue(true);
        model.getParameter(KekuleStructure.class).setValue(true);
        model.getParameter(ShowEndCarbons.class).setValue(true);

        ElementUtility visitor = new ElementUtility();
        Rectangle screen = new Rectangle(0, 0, 100, 100);
        renderer.setup(square, screen);
        renderer.paint(square, visitor);

        for (IRenderingElement element : visitor.getElements()) {
            Assertions.assertTrue(visitor.toString(element).contains("Line") || visitor.toString(element).contains("Oval"));
        }
    }

}

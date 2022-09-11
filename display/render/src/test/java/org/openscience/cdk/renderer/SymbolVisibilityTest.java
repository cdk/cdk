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

package org.openscience.cdk.renderer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import java.util.Arrays;
import java.util.Collections;

class SymbolVisibilityTest {

    @Test
    void anyAtom() {
        Assertions.assertTrue(SymbolVisibility.all().visible(null, null, null));
    }

    @Test
    void iupacOxygen() {
        Assertions.assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("O"), Collections.emptyList(),
                                                                              new RendererModel()));
    }

    @Test
    void iupacNitrogen() {
        Assertions.assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("N"), Collections.emptyList(),
                                                                              new RendererModel()));
    }

    @Test
    void iupacMethane() {
        Assertions.assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("C"), Collections.emptyList(),
                                                                              new RendererModel()));
    }

    @Test
    void iupacMethylPreferred() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IBond bond = new Bond(a1, a2);
        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, 0));
        Assertions.assertTrue(SymbolVisibility.iupacRecommendations().visible(a1, Arrays.asList(bond), new RendererModel()));
    }

    @Test
    void iupacMethylAcceptable() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(null);
        a2.setImplicitHydrogenCount(null);
        IBond bond = new Bond(a1, a2);
        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, 0));
        Assertions.assertFalse(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon().visible(a1, Arrays.asList(bond),
                                                                                                    new RendererModel()));
    }

    @Test
    void iupacUnlabelledCarbon() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IAtom a3 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));
        a3.setPoint2d(new Point2d(0.5, 0.5));

        IBond bond1 = new Bond(a1, a2);
        IBond bond2 = new Bond(a1, a3);

        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(3);
        a3.setImplicitHydrogenCount(3);

        Assertions.assertFalse(SymbolVisibility.iupacRecommendations().visible(a1, Arrays.asList(bond1, bond2),
                                                                               new RendererModel()));
    }

    @Test
    void iupacCarbonIon() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IAtom a3 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));
        a3.setPoint2d(new Point2d(0.5, 0.5));

        IBond bond1 = new Bond(a1, a2);
        IBond bond2 = new Bond(a1, a3);

        a1.setFormalCharge(+1);
        a1.setImplicitHydrogenCount(1);
        a2.setImplicitHydrogenCount(3);
        a3.setImplicitHydrogenCount(3);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendations()
                                              .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    void iupacCarbonParallel() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IAtom a3 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, -0.5));
        a3.setPoint2d(new Point2d(0, 0.5));

        IBond bond1 = new Bond(a1, a2);
        IBond bond2 = new Bond(a1, a3);

        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(3);
        a3.setImplicitHydrogenCount(3);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendations()
                                              .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    // produces an NaN internally
    @Disabled("Multiple Group Sgroup rendering can have zero length C-C bonds (e.g. overlaid coords), we don't want to show the symbols")
    void iupacCarbonCornerCase() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IAtom a3 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, 0));
        a3.setPoint2d(new Point2d(0, 0));

        IBond bond1 = new Bond(a1, a2);
        IBond bond2 = new Bond(a1, a3);

        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(3);
        a3.setImplicitHydrogenCount(3);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendations()
                                              .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    void carbonIsotope() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IAtom a3 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));
        a3.setPoint2d(new Point2d(0.5, 0.5));

        IBond bond1 = new Bond(a1, a2);
        IBond bond2 = new Bond(a1, a3);

        a1.setMassNumber(13);

        a1.setImplicitHydrogenCount(2);
        a2.setImplicitHydrogenCount(3);
        a3.setImplicitHydrogenCount(3);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendations()
                                              .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    void ethaneNonTerminal() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));

        IBond bond1 = new Bond(a1, a2);

        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon()
                                              .visible(a1, Collections.singletonList(bond1), new RendererModel()));
    }

    @Test
    void alwaysDisplayCharges() {
        IAtom a1 = new Atom("CH-");
        IAtom a2 = new Atom("CH2");
        IAtom a3 = new Atom("CH3");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));
        a3.setPoint2d(new Point2d(1, 0));

        IBond bond1 = new Bond(a1, a2, IBond.Order.DOUBLE);
        IBond bond2 = new Bond(a2, a3, IBond.Order.SINGLE);

        Assertions.assertTrue(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon()
                                              .visible(a1, Collections.singletonList(bond1), new RendererModel()));
    }

    @Test
    void delocalisedCarbons() {
        IAtom a1 = new Atom("CH");
        IAtom a2 = new Atom("CH");
        IAtom a3 = new Atom("CH");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));
        a3.setPoint2d(new Point2d(1, 0));

        IBond bond1 = new Bond(a1, a2, IBond.Order.UNSET);
        IBond bond2 = new Bond(a2, a3, IBond.Order.UNSET);
        bond1.setIsAromatic(true);
        bond2.setIsAromatic(true);
        a1.setIsAromatic(true);
        a2.setIsAromatic(true);
        a3.setIsAromatic(true);

        Assertions.assertFalse(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon()
                                               .visible(a2, Arrays.asList(bond1, bond2), new RendererModel()));
    }

}

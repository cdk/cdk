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

import org.junit.Ignore;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

import javax.vecmath.Point2d;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SymbolVisibilityTest {

    @Test
    public void anyAtom() {
        assertTrue(SymbolVisibility.all().visible(null, null, null));
    }

    @Test
    public void iupacOxygen() {
        assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("O"), Collections.<IBond> emptyList(),
                new RendererModel()));
    }

    @Test
    public void iupacNitrogen() {
        assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("N"), Collections.<IBond> emptyList(),
                new RendererModel()));
    }

    @Test
    public void iupacMethane() {
        assertTrue(SymbolVisibility.iupacRecommendations().visible(new Atom("C"), Collections.<IBond> emptyList(),
                new RendererModel()));
    }

    @Test
    public void iupacMethylPreferred() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        IBond bond = new Bond(a1, a2);
        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, 0));
        assertTrue(SymbolVisibility.iupacRecommendations().visible(a1, Arrays.asList(bond), new RendererModel()));
    }

    @Test
    public void iupacMethylAcceptable() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");
        a1.setImplicitHydrogenCount(null);
        a1.setImplicitHydrogenCount(null);
        IBond bond = new Bond(a1, a2);
        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0, 0));
        assertFalse(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon().visible(a1, Arrays.asList(bond),
                new RendererModel()));
    }

    @Test
    public void iupacUnlabelledCarbon() {
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

        assertFalse(SymbolVisibility.iupacRecommendations().visible(a1, Arrays.asList(bond1, bond2),
                new RendererModel()));
    }

    @Test
    public void iupacCarbonIon() {
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

        assertTrue(SymbolVisibility.iupacRecommendations()
                .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    public void iupacCarbonParallel() {
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

        assertTrue(SymbolVisibility.iupacRecommendations()
                .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    // produces an NaN internally
    @Ignore("Multiple Group Sgroup rendering can have zero length C-C bonds (e.g. overlaid coords), we don't want to show the symbols")
    public void iupacCarbonCornerCase() {
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

        assertTrue(SymbolVisibility.iupacRecommendations()
                .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    public void carbonIsotope() {
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

        assertTrue(SymbolVisibility.iupacRecommendations()
                .visible(a1, Arrays.asList(bond1, bond2), new RendererModel()));
    }

    @Test
    public void ethaneNonTerminal() {
        IAtom a1 = new Atom("C");
        IAtom a2 = new Atom("C");

        a1.setPoint2d(new Point2d(0, 0));
        a2.setPoint2d(new Point2d(0.5, -0.5));

        IBond bond1 = new Bond(a1, a2);

        a1.setImplicitHydrogenCount(3);
        a2.setImplicitHydrogenCount(3);

        assertTrue(SymbolVisibility.iupacRecommendationsWithoutTerminalCarbon()
                                   .visible(a1, Collections.singletonList(bond1), new RendererModel()));
    }

}

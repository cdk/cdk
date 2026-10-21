/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.layout;

import org.junit.jupiter.api.Test;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

import javax.vecmath.Point2d;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

class CongestionTest {

    @Test
    void updatedScoreMatchesRecomputedScore() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CCCCCC |(0,0,;1.3,0.75,;2.6,0,;3.9,0.75,;5.2,0,;6.5,0.75,)|");
        int[][] adjList = GraphUtil.toAdjList(mol);
        Congestion congestion = new Congestion(mol, adjList);
        mol.getAtom(5).setPoint2d(new Point2d(6.5, -0.75));
        congestion.update(new int[]{5}, 1);
        assertThat(congestion.score(), closeTo(new Congestion(mol, adjList).score(), 1e-10));
    }
}

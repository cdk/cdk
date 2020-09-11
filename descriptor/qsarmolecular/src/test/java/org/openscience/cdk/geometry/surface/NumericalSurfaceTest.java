/*
 * Copyright (C) 2019  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.geometry.surface;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import javax.vecmath.Point3d;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class NumericalSurfaceTest {

    @Test
    public void testCranbinSurface() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IChemFile chemFile;
        String path = "/data/pdb/1CRN.pdb";
        try (InputStream in = getClass().getResourceAsStream(path);
             PDBReader pdbr = new PDBReader(in)) {
            chemFile = pdbr.read(bldr.newInstance(IChemFile.class));
        }
        IAtomContainer   mol     = ChemFileManipulator.getAllAtomContainers(chemFile).get(0);
        NumericalSurface surface = new NumericalSurface(mol);
        Map<IAtom, List<Point3d>> map = surface.getAtomSurfaceMap();
        assertThat(map.size(), CoreMatchers.is(222));
        assertThat(mol.getAtomCount(), CoreMatchers.is(327));
    }

}

/* Copyright (C) 2026 John Mayfield
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Verify the 'Isotopes' class captures everything from the XML with
 * enough precission.
 */
public class VerifyIsotopesTest {

    @Test public void verifyIsotopeData() throws IOException {
        IsotopeFactory expected = XMLIsotopeFactory.getInstance(SilentChemObjectBuilder.getInstance());
        IsotopeFactory actual = Isotopes.getInstance();

        List<IIsotope> expectedIsotopes = new ArrayList<>(expected.isotopes());
        List<IIsotope> actualIsotopes = new ArrayList<>(actual.isotopes());

        Assertions.assertEquals(actualIsotopes.size(),
                                expectedIsotopes.size());

        Comparator<IIsotope> comparator = Comparator.comparingInt(IIsotope::getAtomicNumber)
                                                    .thenComparingInt(IIsotope::getMassNumber);
        expectedIsotopes.sort(comparator);
        actualIsotopes.sort(comparator);

        for (int i=0; i<actualIsotopes.size(); i++) {
            IIsotope actIsotope = actualIsotopes.get(i);
            IIsotope expIsotope = expectedIsotopes.get(i);
            Assertions.assertEquals(actIsotope.getAtomicNumber(),
                                    expIsotope.getAtomicNumber());
            Assertions.assertEquals(actIsotope.getMassNumber(),
                                    expIsotope.getMassNumber());
            Assertions.assertEquals(actIsotope.getExactMass(),
                                    expIsotope.getExactMass(),
                                    0.00001);
            Assertions.assertEquals(actIsotope.getNaturalAbundance(),
                                    expIsotope.getNaturalAbundance(),
                                    0.00001);
        }

    }

}

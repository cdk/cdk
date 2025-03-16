/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.validate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.test.CDKTestCase;

/**
 */
class ProblemMarkerTest extends CDKTestCase {

    ProblemMarkerTest() {
        super();
    }

    @Test
    void testUnmarkWithError_IChemObject() {
        IChemObject object = new ChemObject();
        Assertions.assertNull(object.getProperty(ProblemMarker.ERROR_MARKER));
        ProblemMarker.markWithError(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.ERROR_MARKER));
        ProblemMarker.unmarkWithError(object);
        Assertions.assertNull(object.getProperty(ProblemMarker.ERROR_MARKER));
    }

    @Test
    void testUnmarkWithWarning_IChemObject() {
        IChemObject object = new ChemObject();
        Assertions.assertNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        ProblemMarker.markWithWarning(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        ProblemMarker.unmarkWithWarning(object);
        Assertions.assertNull(object.getProperty(ProblemMarker.WARNING_MARKER));
    }

    @Test
    void testUnmark_IChemObject() {
        IChemObject object = new ChemObject();
        Assertions.assertNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        ProblemMarker.markWithWarning(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        ProblemMarker.markWithError(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.ERROR_MARKER));
        ProblemMarker.unmark(object);
        Assertions.assertNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        Assertions.assertNull(object.getProperty(ProblemMarker.ERROR_MARKER));
    }

    @Test
    void testMarkWithError_IChemObject() {
        IChemObject object = new ChemObject();
        Assertions.assertNull(object.getProperty(ProblemMarker.ERROR_MARKER));
        ProblemMarker.markWithError(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.ERROR_MARKER));
    }

    @Test
    void testMarkWithWarning_IChemObject() {
        IChemObject object = new ChemObject();
        Assertions.assertNull(object.getProperty(ProblemMarker.WARNING_MARKER));
        ProblemMarker.markWithWarning(object);
        Assertions.assertNotNull(object.getProperty(ProblemMarker.WARNING_MARKER));
    }

}

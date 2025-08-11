/* Copyright (C) 2007,2025  Egon Willighagen <egonw@users.sf.net>
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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectChangeEvent;
import org.openscience.cdk.interfaces.IChemObjectListener;

/**
 */
class ProblemMarkerTest {

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

    private class ChemObject implements IChemObject {

        Map<Object,Object> properties = new HashMap<>();

        @Override
        public void setProperty(Object description, Object property) {
            properties.put(description, property);
        }

        @Override
        public void removeProperty(Object description) {
            if (properties != null) properties.remove(description);
        }

        @Override
        public <T> T getProperty(Object description) { return (T) properties.get(description); }

        @Override
        public <T> T getProperty(Object description, Class<T> c) { return (T) properties.get(description); }

        @Override
        public Map<Object, Object> getProperties() { return properties; }

        @Override
        public void setProperties(Map<Object, Object> properties) {
            this.properties = properties;
        }

        @Override
        public void addProperties(Map<Object, Object> properties) {
            properties.putAll(properties);
        }

        // the rest of the methods are needed for this test
        static final String NOTNEEDED = "Not needed for the test";
        @Override public IChemObjectBuilder getBuilder() { return null; }
        @Override public void addListener(IChemObjectListener col) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public int getListenerCount() { return 0; }
        @Override public void removeListener(IChemObjectListener col) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public void setNotification(boolean bool) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public boolean getNotification() { return false; }
        @Override public void notifyChanged() { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public void notifyChanged(IChemObjectChangeEvent evt) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public String getID() { return null; }
        @Override public void setID(String identifier) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public void setFlag(int mask, boolean value) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public boolean getFlag(int mask) { return false; }
        @Override public void setFlags(boolean[] newFlags) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public boolean[] getFlags() { return null; }
        @Override public Number getFlagValue() { return null; }
        @Override public void set(int flags) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public void clear(int flags) { throw new UnsupportedOperationException(NOTNEEDED); }
        @Override public boolean is(int flags) { return false; }
        @Override public int flags() { return 0; }
        public ChemObject clone() { return this; }

    }

}

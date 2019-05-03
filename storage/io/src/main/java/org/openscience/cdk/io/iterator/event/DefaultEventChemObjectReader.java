/* Copyright (C) 2005-2007  The Jmol Development Team
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.iterator.event;

import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.ChemObjectIO;
import org.openscience.cdk.io.ReaderEvent;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.listener.IReaderListener;

/**
 * Abstract class that IteratingChemObjectReader's can implement to have it
 * take care of basic stuff, like managing the ReaderListeners.
 *
 * @cdk.module io
 * @cdk.githash
 */
public abstract class DefaultEventChemObjectReader extends ChemObjectIO implements IEventChemObjectReader {

    /**
     * An event to be sent to listeners when a frame is read.
     */
    private ReaderEvent frameReadEvent = null;

    public boolean accepts(IChemObject object) {
        return accepts(object.getClass());
    }

    @Override
    public boolean accepts(Class<? extends IChemObject> objectClass) {
        // leave it up the read(IChemObject) to decide by default
        return true;
    }

    /* Extra convenience methods */

    /**
     * File IO generally does not support removing of entries.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Sends a frame read event to the registered ReaderListeners.
     */
    protected void fireFrameRead() {
        for (IChemObjectIOListener chemListener : getListeners()) {
            IReaderListener listener = (IReaderListener) chemListener;

            // Lazily create the event:
            if (frameReadEvent == null) {
                frameReadEvent = new ReaderEvent(this);
            }
            listener.frameRead(frameReadEvent);
        }
    }

}

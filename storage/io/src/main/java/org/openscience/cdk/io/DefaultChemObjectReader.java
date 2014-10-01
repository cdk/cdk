/* Copyright (C) 2002-2007  The Jmol Development Team
 *                    2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.listener.IChemObjectIOListener;
import org.openscience.cdk.io.listener.IReaderListener;

/**
 * Abstract class that ChemObjectReader's can implement to have it
 * take care of basic stuff, like managing the ReaderListeners.
 *
 * @cdk.module io
 * @cdk.githash
 */
public abstract class DefaultChemObjectReader extends ChemObjectIO implements ISimpleChemObjectReader {

    /**
     * An event to be sent to listeners when a frame is read.
     */
    private ReaderEvent                     frameReadEvent = null;

    protected IChemObjectReader.Mode        mode           = IChemObjectReader.Mode.RELAXED;
    protected IChemObjectReaderErrorHandler errorHandler   = null;

    /* Extra convenience methods */

    /**
     * Sends a frame read event to the registered ReaderListeners.
     */
    protected void fireFrameRead() {
        for (IChemObjectIOListener listener : getListeners()) {
            if (listener instanceof IReaderListener) {
                // Lazily create the event:
                if (frameReadEvent == null) {
                    frameReadEvent = new ReaderEvent(this);
                }
                ((IReaderListener) listener).frameRead(frameReadEvent);
            }
        }
    }

    @Override
    public void setReaderMode(ISimpleChemObjectReader.Mode mode) {
        this.mode = mode;
    }

    /** {@inheritDoc} */
    @Override
    public void setErrorHandler(IChemObjectReaderErrorHandler handler) {
        this.errorHandler = handler;
    }

    /** {@inheritDoc} */
    @Override
    public void handleError(String message) throws CDKException {
        if (this.errorHandler != null) this.errorHandler.handleError(message);
        if (this.mode == Mode.STRICT) throw new CDKException(message);
    }

    /** {@inheritDoc} */
    @Override
    public void handleError(String message, Exception exception) throws CDKException {
        if (this.errorHandler != null) this.errorHandler.handleError(message, exception);
        if (this.mode == Mode.STRICT) {
            throw new CDKException(message, exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void handleError(String message, int row, int colStart, int colEnd) throws CDKException {
        if (this.errorHandler != null) this.errorHandler.handleError(message, row, colStart, colEnd);
        if (this.mode == Mode.STRICT) throw new CDKException(message);
    }

    /** {@inheritDoc} */
    @Override
    public void handleError(String message, int row, int colStart, int colEnd, Exception exception) throws CDKException {
        if (this.errorHandler != null) this.errorHandler.handleError(message, row, colStart, colEnd, exception);
        if (this.mode == Mode.STRICT) {
            throw new CDKException(message, exception);
        }
    }
}

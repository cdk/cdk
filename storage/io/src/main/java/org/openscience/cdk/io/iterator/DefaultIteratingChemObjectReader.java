/* Copyright (C) 2003-2007  The Jmol Development Team
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
package org.openscience.cdk.io.iterator;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.io.ChemObjectIO;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.IChemObjectReaderErrorHandler;
import org.openscience.cdk.io.ISimpleChemObjectReader;

/**
 * Abstract class that IteratingChemObjectReader's can implement to have it
 * take care of basic stuff, like managing the ReaderListeners.
 *
 * @cdk.module io
 * @cdk.githash
 */
public abstract class DefaultIteratingChemObjectReader<T extends IChemObject> extends ChemObjectIO implements
        IIteratingChemObjectReader<T> {

    protected IChemObjectReader.Mode        mode         = IChemObjectReader.Mode.RELAXED;
    protected IChemObjectReaderErrorHandler errorHandler = null;

    @Override
    public boolean accepts(Class<? extends IChemObject> objectClass) {
        return false; // it's an iterator, idiot.
    }

    /* Extra convenience methods */

    /**
     * File IO generally does not support removing of entries.
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
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

/* Copyright (C) 2000-2007,2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.io;

import java.io.InputStream;
import java.io.Reader;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.iterator.IIteratingChemObjectReader;

/**
 * This interface specifies the common functionality all IO readers should provide.
 *
 * IO readers should not implement this interface directly, but rather implement
 * one of its child interfaces: {@link ISimpleChemObjectReader} or {@link IIteratingChemObjectReader}.
 * These sub-interfaces specify the information access methods:
 * a simple read() method for the {@link ISimpleChemObjectReader} and
 * more advanced iterator based access for the {@link IIteratingChemObjectReader} (suitable for large files)
 *
 * @cdk.module io
 * @cdk.githash
 *
 * @author     Egon Willighagen <egonw@users.sf.net>
 * @see ISimpleChemObjectReader
 * @see IIteratingChemObjectReader
 **/
public interface IChemObjectReader extends IChemObjectIO {

    public enum Mode {
        /** Only fail on serious format problems */
        RELAXED,
        /** Fail on any format problem */
        STRICT
    }

    /**
     * Sets the Reader from which this ChemObjectReader should read
     * the contents.
     */
    public void setReader(Reader reader) throws CDKException;

    /**
     * Sets the InputStream from which this ChemObjectReader should read
     * the contents.
     */
    public void setReader(InputStream reader) throws CDKException;

    /**
     * Sets the reader mode. If Mode.STRICT, then the reader will fail on
     * any problem in the format of the read file, instead of trying to
     * recover from that.
     *
     * @param mode
     */
    public void setReaderMode(Mode mode);

    /**
     * Sets an error handler that is sent events when file format issues occur.
     *
     * @param handler {@link IChemObjectReaderErrorHandler} to send error
     *                messages to.
     */
    public void setErrorHandler(IChemObjectReaderErrorHandler handler);

    /**
     * Redirects an error message to the {@link IChemObjectReaderErrorHandler}.
     * Throws an {@link CDKException} when in STRICT {@link Mode}.
     *
     * @param message the error message.
     */
    public void handleError(String message) throws CDKException;

    /**
     * Redirects an error message to the {@link IChemObjectReaderErrorHandler}.
     * Throws an {@link CDKException} when in STRICT {@link Mode}.
     *
     * @param message  the error message.
     * @param exception the corresponding {@link Exception}.
     */
    public void handleError(String message, Exception exception) throws CDKException;

    /**
     * Redirects an error message to the {@link IChemObjectReaderErrorHandler}.
     * Throws an {@link CDKException} when in STRICT {@link Mode}.
     *
     * @param message  the error message.
     * @param row      Row in the file where the error is found.
     * @param colStart Start column in the file where the error is found.
     * @param colEnd   End column in the file where the error is found.
     */
    public void handleError(String message, int row, int colStart, int colEnd) throws CDKException;

    /**
     * Redirects an error message to the {@link IChemObjectReaderErrorHandler}.
     * Throws an {@link CDKException} when in STRICT {@link Mode}.
     *
     * @param message  the error message.
     * @param exception the corresponding {@link Exception}.
     * @param row       Row in the file where the error is found.
     * @param colStart Start column in the file where the error is found.
     * @param colEnd   End column in the file where the error is found.
     */
    public void handleError(String message, int row, int colStart, int colEnd, Exception exception) throws CDKException;
}

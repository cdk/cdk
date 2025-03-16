/* Copyright (C) 2010,2023  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import org.openscience.cdk.io.IChemObjectReader.Mode;

/**
 * Interface for classes aimed to handle {@link IChemObjectReader} errors. There
 * are two kinds of errors: normal errors, and fatal errors. Users of the readers
 * can opt to continue parsing the file (see {@link Mode}. However, fatal errors
 * cannot be ignored, as the parser is not able to continue reading the file.
 * The user should immediately halt reading the file.
 *
 * @cdk.githash
 *
 * @author Egon Willighagen &lt;egonw@users.sf.net&gt;
 */
public interface IChemObjectReaderErrorHandler {

    /**
     * Method that should react on an error message send by an
     * {@link IChemObjectReader}.
     *
     * @param message Error found while reading.
     */
    void handleError(String message);

    /**
     * Method that should react on an error message send by an
     * {@link IChemObjectReader}.
     *
     * @param message   Error found while reading.
     * @param exception Exception thrown while reading.
     */
    void handleError(String message, Exception exception);

    /**
     * Method that should react on an error message send by an
     * {@link IChemObjectReader}.
     *
     * @param message  Error found while reading.
     * @param row      Row in the file where the error is found.
     * @param colStart Start column in the file where the error is found.
     * @param colEnd   End column in the file where the error is found.
     */
    void handleError(String message, int row, int colStart, int colEnd);

    /**
     * Method that should react on an error message send by an
     * {@link IChemObjectReader}.
     *
     * @param message   Error found while reading.
     * @param exception Exception thrown while reading.
     * @param colStart Start column in the file where the error is found.
     * @param colEnd   End column in the file where the error is found.
     */
    void handleError(String message, int row, int colStart, int colEnd, Exception exception);

    /**
     * Method that should react on a fatal error message send by an
     * {@link IChemObjectReader}. This error is fatal, and the state of
     * reading is no longer defined.
     *
     * @param message Error found while reading.
     */
    void handleFatalError(String message);

    /**
     * Method that should react on a fatal error message send by an
     * {@link IChemObjectReader}. This error is fatal, and the state of
     * reading is no longer defined.
     *
     * @param message   Error found while reading.
     * @param exception Exception thrown while reading.
     */
    void handleFatalError(String message, Exception exception);

    /**
     * Method that should react on a fatal error message send by an
     * {@link IChemObjectReader}. This error is fatal, and the state of
     * reading is no longer defined.
     *
     * @param message  Error found while reading.
     * @param row      Row in the file where the fatal error is found.
     * @param colStart Start column in the file where the fatal error is found.
     * @param colEnd   End column in the file where the fatal error is found.
     */
    void handleFatalError(String message, int row, int colStart, int colEnd);

    /**
     * Method that should react on a fatal error message send by an
     * {@link IChemObjectReader}. This error is fatal, and the state of
     * reading is no longer defined.
     *
     * @param message   Error found while reading.
     * @param exception Exception thrown while reading.
     * @param colStart Start column in the file where the fatal error is found.
     * @param colEnd   End column in the file where the fatal error is found.
     */
    void handleFatalError(String message, int row, int colStart, int colEnd, Exception exception);

}

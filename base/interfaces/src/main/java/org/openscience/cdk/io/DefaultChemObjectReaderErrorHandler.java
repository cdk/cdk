/* Copyright (C) 2024 Uli Fechner
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

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Default implementation of the {@link IChemObjectReaderErrorHandler} interface.
 * Emits log entries using the {@link LoggingToolFactory}.
 *
 * @cdk.githash
 *
 * @author Uli Fechner
 */
public class DefaultChemObjectReaderErrorHandler implements IChemObjectReaderErrorHandler {
    private final ILoggingTool logger;

    /**
     * Constructs a new instance using the {@code DefaultChemObjectReaderErrorHandler} class as the
     * source for logging purposes.
     */
    public DefaultChemObjectReaderErrorHandler() {
        this(DefaultChemObjectReaderErrorHandler.class);
    }

    /**
     * Constructs a new instance using a given class as the source for logging purposes.
     *
     * @param clazz the class for which the {@link ILoggingTool} should be constructed
     */
    public DefaultChemObjectReaderErrorHandler(final Class<?> clazz) {
        this(LoggingToolFactory.createLoggingTool(clazz));
    }

    /**
     * Constructs a new instance using the provided logging tool.
     *
     * @param logger the logger to be used for emitting log entries
     */
    public DefaultChemObjectReaderErrorHandler(final ILoggingTool logger) {
        this.logger = logger;
    }

    @Override
    public void handleError(String message) {
        logger.error(message);
    }

    @Override
    public void handleError(String message, Exception exception) {
        logger.error(message, ", ", exception);
    }

    @Override
    public void handleError(String message, int row, int colStart, int colEnd) {
        logger.error(message, ", row ", row, " column ", colStart, "-", colEnd);
    }

    @Override
    public void handleError(String message, int row, int colStart, int colEnd, Exception exception) {
        logger.error(message + ", row ", row, " column ", colStart, "-", colEnd, ", ", exception);
    }

    @Override
    public void handleFatalError(String message) {
        logger.fatal(message);
    }

    @Override
    public void handleFatalError(String message, Exception exception) {
        logger.fatal(message + ", " + exception);
    }

    @Override
    public void handleFatalError(String message, int row, int colStart, int colEnd) {
        logger.fatal(message + ", row " + row + " column " + colStart + "-" + colEnd);
    }

    @Override
    public void handleFatalError(String message, int row, int colStart, int colEnd, Exception exception) {
        logger.fatal(message + ", row " + row + " column " + colStart + "-" + colEnd + ", " + exception);
    }
}

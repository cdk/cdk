/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.exception;

/**
 * Indicates a computation did not complete within some predefined bound. The
 * bound could be a limit on time, iterations or another quantity. Exceeding the
 * bound means the problem is hard and it was not feasible to continue until
 * completion.
 *
 * @author John May
 * @cdk.module core
 * @cdk.githash
 */
public final class Intractable extends CDKException {

    /**
     * Create a new exception with the specified message.
     *
     * @param message a message
     */
    public Intractable(String message) {
        super(message);
    }

    /**
     * Utility for creating a message which indicates an operation timed out
     * after a given time in milliseconds, {@code t}. The message is of the
     * format - "Operation did not finish after {t} ms".
     *
     * @param t time in milliseconds
     * @return a throwable exception
     */
    public static Intractable timeout(long t) {
        return timeout("Operation", t);
    }

    /**
     * Utility for creating a message which indicates an operation timed out
     * after a given time in milliseconds, {@code t}. A description of the
     * operation can be provided. The message is of the format - "{desc} did not
     * finish after {t} ms".
     *
     * @param desc description of the operation
     * @param t    time in milliseconds
     * @return a throwable exception
     */
    public static Intractable timeout(String desc, long t) {
        return new Intractable(desc + " did not finish after " + t + " ms.");
    }
}

/* Copyright (C) 2024 Uli Fechner
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
package org.openscience.cdk.rinchi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Uli Fechner
 * @cdk.module rinchi
 * @cdk.githash
 */
public abstract class StatusMessagesOutput {

    /**
     * Represents the status of a process, indicating whether it was successful, encountered warnings, or errors.
     */
    public enum Status {
        /** Success, with no warnings or errors. */
        SUCCESS,
        /** Success with one or more warning(s) issued. */
        WARNING,
        /** Error, no result was obtained. */
        ERROR;

        /**
         * Compares the current status with another given status and yields the status with higher severity.
         *
         * @param other the other status to compare with
         * @return the status with the higher severity
         * @throws IllegalStateException if the current status is unknown
         */
        public Status getHigherSeverity(final Status other) {
            switch (this) {
                case SUCCESS:
                    return other;
                case WARNING:
                    return other == ERROR ? ERROR : this;
                case ERROR:
                    return this;
                default:
                    throw new IllegalStateException("Unknown status: " + this);
            }
        }
    }

    private Status status = Status.SUCCESS;
    private final List<String> messages = new ArrayList<>();

    /**
     * Adds a message with a given status, updating the current status to the higher severity.
     *
     * @param message the message to add
     * @param status the status associated with the message
     */
    protected void addMessage(final String message, final Status status) {
        this.status = this.status.getHigherSeverity(status);
        this.messages.add(message);
    }

    /**
     * Access the status of the RInChI Decomposition process.
     *
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Retrieves unmodifiable messages related to RInChI processing.
     *
     * @return an unmodifiable list of messages
     */
    public List<String> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }
}

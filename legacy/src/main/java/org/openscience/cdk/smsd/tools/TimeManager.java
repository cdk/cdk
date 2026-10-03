/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.tools;



/**
 * Measures elapsed duration from construction using a monotonic clock.
 * Wall-clock adjustments do not change the reported duration. This object
 * has no mutable state after construction and may be shared between readers.
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class TimeManager {

    private final long startTime;

    /**
     * Starts a new elapsed-time measurement.
     */
    public TimeManager() {
        startTime = System.nanoTime();
    }

    /**
     * Returns the elapsed duration in hours.
     *
     * @return elapsed hours since construction
     */
    public double getElapsedTimeInHours() {
        return getElapsedTimeInMilliSeconds() / 3600000.0;
    }

    /**
     * Returns the elapsed duration in minutes.
     *
     * @return elapsed minutes since construction
     */
    public double getElapsedTimeInMinutes() {
        return getElapsedTimeInMilliSeconds() / 60000.0;
    }

    /**
     * Returns the elapsed duration in seconds.
     *
     * @return elapsed seconds since construction
     */
    public double getElapsedTimeInSeconds() {
        return getElapsedTimeInMilliSeconds() / 1000.0;
    }

    /**
     * Returns the elapsed duration in milliseconds.
     *
     * @return elapsed milliseconds since construction
     */
    public double getElapsedTimeInMilliSeconds() {
        return (System.nanoTime() - startTime) / 1000000.0;
    }
}

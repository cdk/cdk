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

import java.text.SimpleDateFormat;
import java.util.TimeZone;


/**
 * Class that handles execution time of the MCS search.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public class TimeManager {

    private double           startTime;
    private SimpleDateFormat dateFormat;

    /**
     * Constructor for storing execution time
     */
    public TimeManager() {

        dateFormat = new SimpleDateFormat("HH:mm:ss");

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        startTime = System.currentTimeMillis();
    }

    /**
     * Returns Elapsed Time In Hours
     * @return Elapsed Time In Hours
     */
    public double getElapsedTimeInHours() {
        double currentTime = System.currentTimeMillis();

        return (currentTime - startTime) / (60 * 60 * 1000);

    }

    /**
     * Returns Elapsed Time In Minutes
     * @return Elapsed Time In Minutes
     */
    public double getElapsedTimeInMinutes() {

        //long diffSeconds = diff / 1000;
        //long diffMinutes = diff / (60 * 1000);
        //long diffHours = diff / (60 * 60 * 1000);
        //long diffDays = diff / (24 * 60 * 60 * 1000);

        double currentTime = System.currentTimeMillis();
        return (currentTime - startTime) / (60 * 1000);

    }

    /**
     * Return Elapsed Time In Seconds
     * @return Elapsed Time In Seconds
     */
    public double getElapsedTimeInSeconds() {
        double currentTime = System.currentTimeMillis();
        return ((currentTime - startTime) / 1000);

    }

    /**
     * Returns Elapsed Time In Mill Seconds
     * @return Elapsed Time In Mill Seconds
     */
    public double getElapsedTimeInMilliSeconds() {
        double currentTime = System.currentTimeMillis();
        return (currentTime - startTime);

    }
}

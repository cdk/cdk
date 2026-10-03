/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.global;


/**
 * Stores the legacy MCS timeout configuration and status for one thread.
 *
 * <p>Configure the cutoff on the thread performing the search. Search engines
 * capture a finite cutoff at search entry; subsequent configuration changes do
 * not extend that active budget. Checks are cooperative and cannot interrupt
 * user predicates. The flag records incomplete work, so a timed-out result
 * must not be treated as a certified maximum.</p>
 *
 * <p>Each thread obtains its own instance. Passing an instance to another thread
 * does not make its mutable fields safe for concurrent use.</p>
 *
 * @cdk.threadnonsafe
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class TimeOut {

    private static final ThreadLocal<TimeOut> INSTANCE = ThreadLocal.withInitial(TimeOut::new);
    private double         time        = -1;
    private boolean        timeOutFlag = false;

    /**
     * Get timeout state for the calling thread. Configure the cutoff on the
     * thread that performs the search.
     *
     * @return the calling thread's timeout configuration and flag
     */
    public static TimeOut getInstance() {
        return INSTANCE.get();
    }

    /** Creates disabled timeout configuration with an unset timeout flag. */
    protected TimeOut() {}

    /**
     * Sets the cutoff in minutes for subsequent searches.
     * Any finite negative value disables timeout checking; zero is an immediate
     * cooperative cutoff. Changing the cutoff does not clear the timeout flag.
     *
     * @param timeout finite cutoff in minutes
     * @throws IllegalArgumentException if the cutoff is not finite
     */
    public void setTimeOut(double timeout) {
        if (!Double.isFinite(timeout)) throw new IllegalArgumentException("Timeout must be finite");
        this.time = timeout;
    }

    /**
     * Returns the configured cutoff for subsequent searches.
     *
     * @return cutoff in minutes, or a finite negative value when disabled
     */
    public double getTimeOut() {
        return time;
    }

    /**
     * Returns the recorded status without consulting an elapsed clock.
     *
     * @return whether a search recorded a timeout on this thread
     */
    public boolean isTimeOutFlag() {
        return timeOutFlag;
    }

    /**
     * Sets the recorded timeout status.
     * Engines clear the flag when beginning a validated search.
     * This compatibility status does not replace an active engine's captured
     * budget or request cancellation of that engine.
     *
     * @param timeOut whether the search exceeded its captured budget
     */
    public void setTimeOutFlag(boolean timeOut) {
        this.timeOutFlag = timeOut;
    }
}

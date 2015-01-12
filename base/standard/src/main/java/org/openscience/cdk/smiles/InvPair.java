/* Copyright (C) 2001-2002  Oliver Horlacher <oliver.horlacher@therastrat.com>
 *                    2002  Christoph Steinbeck <steinbeck@users.sf.net>
 *          2003-2008,2011  Egon Willighagen <egonw@users.sf.net>
 *                    2004  Stefan Kuhn <shk3@users.sf.net>
 *                    2006  Kai Hartmann <kaihartmann@users.sf.net>
 *               2008-2009  Rajarshi Guha <rajarshi@users.sf.net>
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
 *  */
package org.openscience.cdk.smiles;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.math.Primes;

/**
 * This is used to hold the invariance numbers for the canonical labeling of
 * {@link IAtomContainer}s.
 *
 * @cdk.module standard
 * @cdk.githash
 */
public class InvPair implements java.io.Serializable {

    private static final long  serialVersionUID = -1397634098919863122L;

    /** The description used to set the invariance numbers in the atom's property*/
    public final static String INVARIANCE_PAIR  = "InvariancePair";
    public final static String CANONICAL_LABEL  = "CanonicalLabel";

    private long               last             = 0;

    private long               curr             = 0;

    private IAtom              atom;

    private int                prime;

    public InvPair() {}

    public InvPair(long current, IAtom atom) {
        curr = current;
        this.atom = atom;
        atom.setProperty(INVARIANCE_PAIR, this);
    }

    public long getLast() {
        return last;
    }

    /**
     * Set the value of the seed.
     * <p/>
     * Note that use of this method implies that a new prime number is desired.
     * If so, make sure to call {@link #setPrime()} to ensure that a new prime
     * number is obtained using the new seed.
     * <p/>
     * Todo make the following robust!
     *
     * @see #getCurr()
     * @see #setPrime()
     */
    public void setCurr(long newCurr) {
        curr = newCurr;
    }

    /**
     * Get the current seed.
     *
     * @return The seed
     * @see #setCurr(long)
     * @see #setPrime()
     * @see #getPrime()
     */
    public long getCurr() {
        return curr;
    }

    /**
     * Check whether this instance equals another instance.
     *
     * @param object An instance of InvPair
     * @return true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof InvPair) {
            InvPair o = (InvPair) object;
            //      logger.debug("Last " + last + "o.last " + o.getLast() + " curr " + curr + " o.curr " + o.getCurr() + " equals " +(last == o.getLast() && curr == o.getCurr()));
            return (last == o.getLast() && curr == o.getCurr());
        } else {
            return false;
        }
    }

    public void setLast(long newLast) {
        last = newLast;
    }

    public void setAtom(IAtom newAtom) {
        atom = newAtom;
    }

    public IAtom getAtom() {
        return atom;
    }

    public void commit() {
        atom.setProperty(CANONICAL_LABEL, Long.valueOf(curr));
    }

    /**
     * String representation.
     *
     * @return The string representation of the class.
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append(curr);
        buff.append('\t');
        return buff.toString();
    }

    /**
     * Get the current prime number.
     *
     * @return The current prime number
     * @see #setPrime()
     */
    public int getPrime() {
        return prime;
    }

    /**
     * Sets the prime number based on the current seed.
     * <p/>
     * Note that if you change the seed via {@link #setCurr(long)}, you should make
     * sure to call this method so that a new prime number is available via
     * {@link #getPrime()}
     *
     * @see #setCurr(long)
     * @see #getPrime()
     */
    public void setPrime() {
        prime = Primes.getPrimeAt((int) curr - 1);
    }
}

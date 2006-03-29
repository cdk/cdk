/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) Project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
import org.openscience.cdk.math.Primes;

/**
 * This is used to hold the invariance numbers for the cannonical labeling of
 * AtomContainers.
 *
 * @cdk.module standard
 */
public class InvPair implements java.io.Serializable{
  /** The description used to set the invatiance numbers in the atom's property*/
  public static String INVARIANCE_PAIR = "InvariancePair";

  private long last = 0;

  private long curr = 0;

  private IAtom atom;

  private int prime;

  public InvPair() {
  }

  public InvPair(long c, IAtom a){
    curr = c;
    atom = a;
    a.setProperty(INVARIANCE_PAIR, this);
  }

  public long getLast() {
    return last;
  }
  
  /*
   * Todo make the following robust!
   */
  public void setCurr(long newCurr) {
    curr = newCurr;
  }

  public long getCurr() {
    return curr;
  }

  public boolean equals(Object e){
    if(e instanceof InvPair){
      InvPair o = (InvPair)e;
//      System.out.println("Last " + last + "o.last " + o.getLast() + " curr " + curr + " o.curr " + o.getCurr() + " equals " +(last == o.getLast() && curr == o.getCurr()));
      return (last == o.getLast() && curr == o.getCurr());
    }
    else {
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

  public void comit(){
    atom.setProperty("CanonicalLable", new Long(curr));
  }

  public String toString(){
    StringBuffer buff = new StringBuffer();
    buff.append(curr);
    buff.append("\t");
    return buff.toString();
  }

  public int getPrime() {
    return prime;
  }

  public void setPrime(){
    prime = Primes.getPrimeAt((int)curr - 1);
  }
}

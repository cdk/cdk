/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 1997-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.io.cml;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * Low weigth alternative to Sun's Stack class.
 *
 * @cdk.module io
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword stack
 */
@TestClass("org.openscience.cdk.io.cml.CMLStackTest")
public class CMLStack {

  String[] stack = new String[64];
  int sp = 0;

  /**
   * Adds an entry to the stack.
   */
  @TestMethod("testPush_String")
  public void push(String item) {
    if (sp == stack.length) {
      String[] temp = new String[2 * sp];
      System.arraycopy(stack, 0, temp, 0, sp);
      stack = temp;
    }
    stack[sp++] = item;
  }
  
  public int length() {
	  return sp;
  }

  /**
   * Retrieves and deletes to last added entry.
   *
   * @see #current()
   */
  @TestMethod("testPop")
  public String pop() {
    return stack[--sp];
  }

  /**
   * Returns the last added entry.
   *
   * @see #pop()
   */
  @TestMethod("testCurrent")
  public String current() {
    if (sp > 0) {
        return stack[sp-1];
    } else {
        return "";
    }
  }

  /**
   * Returns a String representation of the stack.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("/");
    for (int i = 0; i < sp; ++i) {
      sb.append(stack[i]);
      sb.append("/");
    }
    return sb.toString();
  }

  /**
   * Convenience method to check the last added elements. 
   */
  @TestMethod("testEndsWith_String")
  public boolean endsWith(String lastElement) {
    return stack[sp-1].equals(lastElement);
  }

  /**
   * Convenience method to check the last two added elements. 
   */
  @TestMethod("testEndsWith_String_String")
  public boolean endsWith(String oneButLast, String lastElement) {
	    return endsWith(lastElement) && stack[sp-2].equals(oneButLast);
  }

  /**
   * Convenience method to check the last three added elements. 
   */
  @TestMethod("testEndsWith_String_String_String")
  public boolean endsWith(String twoButLast, String oneButLast, String lastElement) {
	    return endsWith(oneButLast,lastElement) && stack[sp-3].equals(twoButLast);
  }
}

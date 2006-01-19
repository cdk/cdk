/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.io.cml.cdopi;

/**
 * This is an dummy class that makes an application accept basic
 * CML objects. But it just disregards all the information.
 *
 * @cdk.module io
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLCDO implements IChemicalDocumentObject {

  /**
   * Called just before XML parsing is started.
   */
  public void startDocument() {}

  /**
   * Called just after XML parsing has ended.
   */
  public void endDocument() {}

  /**
   * Sets a property for this document.
   *
   * @param type  Type of the property.
   * @param value Value of the property.
   */
  public void setDocumentProperty(String type, String value) {}

  /**
   * Exports the list of objects that the CML CDO excepts as a CDOAcceptedObjects object:
   * Molecule, Fragment, Atom and Bond.
   */
  public CDOAcceptedObjects acceptObjects() {
    CDOAcceptedObjects objects = new CDOAcceptedObjects();
    objects.add("Molecule");
    objects.add("Fragment");
    objects.add("Atom");
    objects.add("Bond");
    return objects;
  }

  /**
   * Start the process of adding a new object to the CDO of a certain type.
   *
   * @param objectType  Type of the object being added.
   */
  public void startObject(String objectType) {}

  /**
   * End the process of adding a new object to the CDO of a certain type.
   *
   * @param objectType  Type of the object being added.
   */
  public void endObject(String objectType) {}

  /**
   * Sets a property of the object being added.
   *
   * @param objectType  Type of the object being added.
   * @param type        Type of the property being set.
   * @param value       Value of the property being set.
   */
  public void setObjectProperty(String objectType, String type, String value) {}
}

/*
 * @(#)CDOInterface.java   0.3 2000/03/19
 *
 * Information can be found at http://www.openscience.org/~egonw/cdopi/
 *
 * Copyright (c) 1999 E.L. Willighagen (egonw@sci.kun.nl)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */

package org.openscience.cdk.io.cml.cdopi;

public interface CDOInterface {

  public void startDocument();
  public void endDocument();
  public void setDocumentProperty(String type, String value);

  public void startObject(String objectType);
  public void endObject(String objectType);
  public void setObjectProperty(String objectType, String propertyType, String propertyValue);

  /**
   * The next procedure must be implemented by each CDO and
   * return a CDOAcceptedObjects class with the names of the 
   * objects that can be handled.
   **/

  public CDOAcceptedObjects acceptObjects();
}

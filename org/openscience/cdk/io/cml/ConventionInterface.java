/*
 * @(#)ConventionInterface.java   0.1 99/08/13
 *
 * Information can be found at http://www.openscience.org/~egonw/cml/
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

package org.openscience.cdk.io.cml;

import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

public interface ConventionInterface {

  void startDocument();
  void endDocument();
  void startElement(String uri, String local, String raw, Attributes atts);
  void endElement(String uri, String local, String raw);
  void characterData(char ch[], int start, int length);
  
  CDOInterface returnCDO();

  void inherit(Convention conv);
  
}

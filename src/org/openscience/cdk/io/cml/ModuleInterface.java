/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.io.cml;

import org.xml.sax.*;
import org.openscience.cdk.io.cml.cdopi.*;

/**
 * This interface describes the procedures classes must implement to be plugable
 * into the CMLHandler. Most procedures reflect those in SAX2.
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public interface ModuleInterface{

  void startDocument();
  void endDocument();
  void startElement(String uri, String local, String raw, Attributes atts);
  void endElement(String uri, String local, String raw);
  void characterData(char ch[], int start, int length);
  
  CDOInterface returnCDO();

  void inherit(ModuleInterface conv);
  
}

/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
package org.openscience.cdk.controller;

import org.openscience.cdk.ChemObject;
import javax.swing.JPopupMenu;

/**
 * Basically, identical to the JPopupMenu class, except that this menu
 * can also contain the source for which it was poped up.
 *
 * <p>IMPORTANT: The very nature of this design can lead to race conditions.
 * It would be better that the Event passed to the popup menu would define
 * the ChemObject source.
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @created 2003-07-36
 */
public class CDKPopupMenu extends JPopupMenu {
   
   private ChemObject source;
   
   public void setSource(ChemObject object) {
       this.source = object;
   }
   
   public ChemObject getSource() {
       return this.source;
   }
   
}

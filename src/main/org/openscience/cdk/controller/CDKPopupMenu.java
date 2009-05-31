/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.controller;

import javax.swing.JPopupMenu;

import org.openscience.cdk.interfaces.IChemObject;

/**
 * Basically, identical to the JPopupMenu class, except that this menu
 * can also contain the source for which it was popped up.
 *
 * <p>IMPORTANT: The very nature of this design can lead to race conditions.
 * It would be better that the Event passed to the popup menu would define
 * the IChemObject source.
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-07-36
 * @cdk.require swing
 * @cdk.module  controlold
 * @cdk.githash
 */
public class CDKPopupMenu extends JPopupMenu {
   
    private static final long serialVersionUID = -235498895062628065L;
    
    private IChemObject source;
   
    public void setSource(IChemObject object) {
        this.source = object;
    }
   
    public IChemObject getSource() {
        return this.source;
    }
   
}

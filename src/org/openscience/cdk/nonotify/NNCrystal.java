/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-04-11 20:37:50 +0200 (Tue, 11 Apr 2006) $
 * $Revision: 5915 $
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.nonotify;

import org.openscience.cdk.Crystal;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @cdk.module nonotify
 */
public class NNCrystal extends Crystal {

	private static final long serialVersionUID = -4151416243034267598L;

	public NNCrystal() {
    	super();
        setNotification(false);
    }

    public NNCrystal(IAtomContainer container) {
        super(container);
        setNotification(false);
    }

	public IChemObjectBuilder getBuilder() {
		return NoNotificationChemObjectBuilder.getInstance();
	}
}

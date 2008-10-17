/* $Revision: 12144 $ $Author: egonw $ $Date: 2008-09-02 21:53:30 +0100 (Tue, 02 Sep 2008) $
 *
 *  Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modeling.builder3d;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.NewCDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.templates.MoleculeFactory;


/**
 * Tests for AtomPlacer3D
 *
 * @cdk.module test-builder3d
 * @cdk.svnrev  $Revision: 12144 $
 */
public class AtomPlacer3DTest extends NewCDKTestCase{

	boolean standAlone = false;


    /**
	 *  Sets the standAlone attribute 
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}
	
	@Test
	public void testAllHeavyAtomsPlaced_IAtomContainer(){
		IAtomContainer ac=MoleculeFactory.makeAlphaPinene();
		Assert.assertFalse(new AtomPlacer3D().allHeavyAtomsPlaced(ac));
		for(IAtom atom : ac.atoms()){
			atom.setFlag(CDKConstants.ISPLACED,true);
		}
		Assert.assertTrue(new AtomPlacer3D().allHeavyAtomsPlaced(ac));
	}
}



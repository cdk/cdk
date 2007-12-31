/* $Revision: 7691 $ $Author: egonw $ $Date: 2007-01-11 12:47:48 +0100 (Thu, 11 Jan 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.ringsearch;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.ringsearch.Path;
import org.openscience.cdk.test.NewCDKTestCase;

/**
 * @cdk.module test-standard
 */
public class PathTest extends NewCDKTestCase {
    
    public PathTest() {
        super();
    }

	@Test
	public void testJoin() {

        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        Path path1 = new Path(atom1, atom2);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newAtom("F");
        Path path2 = new Path(atom2, atom3);

        Path joinedPath = Path.join(path1, path2, atom2);
        Assert.assertEquals(3, joinedPath.size());
        Assert.assertEquals(joinedPath.get(0), atom1);
        Assert.assertEquals(joinedPath.get(1), atom2);
        Assert.assertEquals(joinedPath.get(2), atom3);
    }

    @Test
    public void testGetIntersectionSize() {
       IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        Path path1 = new Path(atom1, atom2);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newAtom("F");
        Path path2 = new Path(atom2, atom3);

        int intersectSize = path1.getIntersectionSize(path2);
        Assert.assertEquals(1, intersectSize);
    }

}



/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.geometry;

import java.util.HashMap;

import javax.vecmath.Point2d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Tool to make projections from 3D to 2D
 *
 * @cdk.keyword projection in 2D
 * @cdk.svnrev  $Revision$
 */
public class Projector  {
  
  public static void project2D(IAtomContainer container) {
    for (int i = 0; i < container.getAtomCount(); i++) {
      IAtom atom = container.getAtom(i);
      if (atom.getPoint3d() != null) {
    	  atom.setPoint2d(
    		new Point2d(
    			atom.getPoint3d().x,
    			atom.getPoint3d().y
    		)
    	  );
      } else {
        // should throw an exception
      }
    }
  }
  public static void project2D(IAtomContainer container, HashMap renderingCoordinates) {
    for (int i = 0; i < container.getAtomCount(); i++) {
      IAtom atom = container.getAtom(i);
      if (atom.getPoint3d() != null) {
    	  atom.setPoint2d(
    	      new Point2d(
    	    	  atom.getPoint3d().x,
    	    	  atom.getPoint3d().y
    	      )
    	  );
        renderingCoordinates.put(atom,new Point2d(atom.getPoint3d().x,atom.getPoint3d().y));
      } else {
        // should throw an exception
      }
    }
  }
}

/* $Revision$ $Author$ $Date$
*
*  Copyright (C) 2008 Gilleain Torrance <gilleain.torrance@gmail.com>
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

package org.openscience.cdk.renderer.visitor;

import java.awt.geom.AffineTransform;

import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.IRenderingVisitor;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;


/**
 * @cdk.module renderextra
 */
public class DistanceSearchVisitor implements IRenderingVisitor {
	
	private int x;
	private int y;
	private double searchRadiusSQ;
	private double closestDistanceSQ;
	public IRenderingElement bestHit;
	
	public DistanceSearchVisitor(int x, int y, double searchRadius) {
		this.x = x;
		this.y = y;
		this.searchRadiusSQ = searchRadius * searchRadius;
		this.bestHit = null;
		this.closestDistanceSQ = -1;
	}
	
	private void check(IRenderingElement element, double xx, double yy) {
		double dSQ = (this.x - xx) * (this.x - xx) + (this.y - yy) * (this.y - yy);
		if (dSQ < this.searchRadiusSQ && 
				(this.closestDistanceSQ == -1 || dSQ < this.closestDistanceSQ)) {
			this.bestHit = element;
			this.closestDistanceSQ = dSQ;
		}
	}

	public void visitElementGroup(ElementGroup elementGroup) {
		elementGroup.visitChildren(this);
	}

	public void visitLine(LineElement lineElement) {
		// FIXME
		int xx = (int)(0.5 * (lineElement.x1 - lineElement.x2));
		int yy = (int)(0.5 * (lineElement.y1 - lineElement.y2));
		this.check(lineElement, xx, yy);
	}

	public void visitOval(OvalElement ovalElement) {
		this.check(ovalElement, ovalElement.x, ovalElement.y);
	}

	public void visitText(TextElement textElement) {
		this.check(textElement, textElement.xCoord, textElement.yCoord);
	}

	public void visitWedge(WedgeLineElement wedgeElement) {
		// TODO
	}
	
	public void visit( IRenderingElement element ) {
      if(element instanceof ElementGroup)
          visit((ElementGroup) element);
      else if(element instanceof LineElement)
          visit((LineElement) element);
      else if(element instanceof OvalElement)
          visit((OvalElement) element);
      else if(element instanceof TextElement)
          visit((TextElement) element);
      else
        System.err.println( "Visitor method for "+element.getClass().getName() 
                            + " is not implemented");
    }
	
	public void setTransform( AffineTransform transform ) {

        // TODO Auto-generated method stub
        
    }
}

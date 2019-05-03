/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.geometry.surface;

import javax.vecmath.Point3d;


public class Point_Type
{
    public int number;
    public Point3d coord;
    public String source;
    // Add constructor, get, set, as needed.
    
    Point_Type(){
    }
    
    Point_Type(int number, Point3d coord){
    	this.number = number;
    	this.coord = coord;
    }
    
    Point_Type(int number, Point3d coord, String source){
    	this.number = number;
    	this.coord = coord;
    	this.source = source;
    }
    
    public int getAtom() {
    return number;
	}

	public void setAtom(int number) {
    	this.number = number;
	}

	public Point3d getCoord() {
    	return coord;
	}

	public void setCoord(Point3d coord) {
    	this.coord = coord;
	}
	
	public void setSource(String source){
		this.source = source;
	}
	
	public String getSource(){
		return this.source;
	}
	
}



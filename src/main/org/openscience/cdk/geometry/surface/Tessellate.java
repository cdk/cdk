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


/**
 * Performs a tessellation of the unit sphere.
 *
 * This class generates the coordinates of the triangles that will
 * tessellate the unit sphere. The algorithm is recursive subdivision
 * of an initial representation which can be tetrahedral, octahedral or
 * icoshedral. The default is icosahedral. The number of points generated
 * depends on the level of subdivision. The default is 4 levels and with the
 * initial icoshedral representation this gives 1536 points.
 * <p>
 * The constants for the tetrahedral and icosahedral representations were 
 * taken from http://eeg.sourceforge.net/eegdoc/eeg_toolbox/sphere_tri.html
 * 
 * @author Rajarshi Guha
 * @cdk.created 2005-05-08  
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 */
public class Tessellate {

    Triangle[] oldtess;
    int maxlevel;

    public Tessellate() {
        this.oldtess = this.repIco();
        this.maxlevel = 4;
    }

    public Tessellate(String type, int level) {
        if (type.equals("tet")) this.oldtess = this.repTet();
        else if (type.equals("oct")) this.oldtess = this.repOct();
        else if (type.equals("ico")) this.oldtess = this.repIco();
        this.maxlevel = level;
    }
    private Point3d midpoint(Point3d p1, Point3d p2) {
        double x,y,z;
        x = 0.5 * (p1.x + p2.x);
        y = 0.5 * (p1.y + p2.y);
        z = 0.5 * (p1.z + p2.z);
        return( new Point3d(x,y,z) );
    }
    private void normalize(Point3d p) {
        double mag = p.x*p.x + p.y*p.y + p.z*p.z;
        if (mag != 0.0) {
            mag = 1.0 / Math.sqrt(mag);
            p.x = p.x  * mag;
            p.y = p.y * mag;
            p.z = p.z * mag;
        }
    }
    public void doTessellate() {
        for (int j = 1; j < maxlevel; j++) {
            int oldN = this.oldtess.length;
            int newN = oldN * 4;
            Triangle[] newtess = new Triangle[ newN ];

            for (int i = 0; i < oldN; i++) {
                Triangle old = oldtess[i];

                Point3d p1 = midpoint(old.p1, old.p3);
                Point3d p2 = midpoint(old.p1, old.p2);
                Point3d p3 = midpoint(old.p2, old.p3);

                normalize(p1);
                normalize(p2);
                normalize(p3);

                newtess[i*4] = new Triangle(old.p1, p2, p1);
                newtess[i*4+1] = new Triangle(p2, old.p2, p3);
                newtess[i*4+2] = new Triangle(p1,p2,p3);
                newtess[i*4+3] = new Triangle(p1,p3,old.p3);
            }

            oldtess = new Triangle[newN];
            for (int i = 0; i < newN; i++) oldtess[i] = newtess[i];
        }
    }

    public int getNumberOfTriangles() {
        return(oldtess.length);
    }
    public  Triangle[] getTessAsTriangles() {
        return(oldtess);
    }
    public Point3d[] getTessAsPoint3ds() {
        Point3d[] ret = new Point3d[ getNumberOfTriangles()*3 ];
        for (int i = 0; i < getNumberOfTriangles(); i++) {
            ret[i*3] = oldtess[i].p1;
            ret[i*3+1] = oldtess[i].p2;
            ret[i*3+2] = oldtess[i].p3;
        }
        return(ret);
    }


    private Triangle[] repTet() {
        double sqrt3 = 0.5773502692;
        Point3d[] v = { 
            new Point3d(sqrt3, sqrt3, sqrt3),
            new Point3d(-sqrt3, -sqrt3, sqrt3),
            new Point3d(-sqrt3,sqrt3,-sqrt3),
            new Point3d(sqrt3,-sqrt3,-sqrt3)
        };
        Triangle[] rep = {
            new Triangle( v[0],v[1],v[2] ),
            new Triangle( v[0],v[3],v[1] ),
            new Triangle( v[2],v[1],v[3] ),
            new Triangle( v[3],v[0],v[2] )
        };
        return(rep);
    }
    private Triangle[] repOct() {
        Point3d[] v = {
            new Point3d(1.0,0.0,0.0),
            new Point3d(-1.0,0.0,0.0),
            new Point3d(0.0,1.0,0.0),
            new Point3d(0.0,-1.0,0.0),
            new Point3d(0.0,0.0,1.0),
            new Point3d(0.0,0.0,-1.0)
        };
        Triangle[] rep = {
            new Triangle( v[0], v[4], v[2] ),
            new Triangle( v[2], v[4], v[1] ),
            new Triangle( v[1], v[4], v[3] ),
            new Triangle( v[3], v[4], v[0] ),
            new Triangle( v[0], v[2], v[5] ),
            new Triangle( v[2], v[1], v[5] ),
            new Triangle( v[1], v[3], v[5] ),
            new Triangle( v[3], v[0], v[5] )
        };
        return(rep);
    }

    private Triangle[] repIco() {
        double tau = 0.8506508084;
        double one = 0.5257311121;
        Point3d[] v = {
            new Point3d(  tau,  one,    0.0 ),  
            new Point3d( -tau,  one,    0.0 ),  
            new Point3d( -tau, -one,    0.0 ),  
            new Point3d(  tau, -one,    0.0 ),  
            new Point3d(  one,   0.0 ,  tau ),  
            new Point3d(  one,   0.0 , -tau ),  
            new Point3d( -one,   0.0 , -tau ),  
            new Point3d( -one,   0.0 ,  tau ),  
            new Point3d(   0.0 ,  tau,  one ),  
            new Point3d(   0.0 , -tau,  one ),  
            new Point3d(   0.0 , -tau, -one ),  
            new Point3d(   0.0 ,  tau, -one ) 
        };
        Triangle[] rep = {
            new Triangle( v[4], v[8], v[7] ),
            new Triangle( v[4], v[7], v[9] ),
            new Triangle( v[5], v[6], v[11] ),
            new Triangle( v[5], v[10], v[6] ),
            new Triangle( v[0], v[4], v[3] ),
            new Triangle( v[0], v[3], v[5] ),
            new Triangle( v[2], v[7], v[1] ),
            new Triangle( v[2], v[1], v[6] ),
            new Triangle( v[8], v[0], v[11] ),
            new Triangle( v[8], v[11], v[1] ),
            new Triangle( v[9], v[10], v[3] ),
            new Triangle( v[9], v[2], v[10] ),
            new Triangle( v[8], v[4], v[0] ),
            new Triangle( v[11], v[0], v[5] ),
            new Triangle( v[4], v[9], v[3] ),
            new Triangle( v[5], v[3], v[10] ),
            new Triangle( v[7], v[8], v[1] ),
            new Triangle( v[6], v[1], v[11] ),
            new Triangle( v[7], v[2], v[9] ),
            new Triangle( v[6], v[10], v[2] )
        };
        return(rep);
    }
}



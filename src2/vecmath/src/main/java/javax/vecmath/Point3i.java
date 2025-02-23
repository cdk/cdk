/*
 * Copyright 1999-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 */

package javax.vecmath;


/**
 * A 3 element point represented by signed integer x,y,z
 * coordinates.
 *
 * @since vecmath 1.2
 */
public class Point3i extends Tuple3i implements java.io.Serializable {

    // Compatible with 1.2
    static final long serialVersionUID = 6149289077348153921L;

    /**
     * Constructs and initializes a Point3i from the specified
     * x, y, and z coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public Point3i(int x, int y, int z) {
	super(x, y, z);
    }


    /**
     * Constructs and initializes a Point3i from the array of length 3.
     * @param t the array of length 3 containing x, y, and z in order.
     */
    public Point3i(int[] t) {
	super(t);
    }


    /**
     * Constructs and initializes a Point3i from the specified Tuple3i.
     * @param t1 the Tuple3i containing the initialization x, y, and z
     * data.
     */
    public Point3i(Tuple3i t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Point3i to (0,0,0).
     */
    public Point3i() {
	super();
    }

}

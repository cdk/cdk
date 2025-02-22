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
 * A 4 element point represented by signed integer x,y,z,w
 * coordinates.
 *
 * @since vecmath 1.2
 */
public class Point4i extends Tuple4i implements java.io.Serializable {

    // Combatible with 1.2
    static final long serialVersionUID = 620124780244617983L;

    /**
     * Constructs and initializes a Point4i from the specified
     * x, y, z, and w coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param w the w coordinate
     */
    public Point4i(int x, int y, int z, int w) {
	super(x, y, z, w);
    }


    /**
     * Constructs and initializes a Point4i from the array of length 4.
     * @param t the array of length 4 containing x, y, z, and w in order.
     */
    public Point4i(int[] t) {
	super(t);
    }


    /**
     * Constructs and initializes a Point4i from the specified Tuple4i.
     * @param t1 the Tuple4i containing the initialization x, y, z,
     * and w data.
     */
    public Point4i(Tuple4i t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Point4i to (0,0,0,0).
     */
    public Point4i() {
	super();
    }

}

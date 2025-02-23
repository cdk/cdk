/*
 * Copyright 2005-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 * A 2-element point represented by signed integer x,y
 * coordinates.
 *
 * @since vecmath 1.4
 */
public class Point2i extends Tuple2i implements java.io.Serializable {

    static final long serialVersionUID = 9208072376494084954L;

    /**
     * Constructs and initializes a Point2i from the specified
     * x and y coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point2i(int x, int y) {
	super(x, y);
    }


    /**
     * Constructs and initializes a Point2i from the array of length 2.
     * @param t the array of length 2 containing x and y in order.
     */
    public Point2i(int[] t) {
	super(t);
    }


    /**
     * Constructs and initializes a Point2i from the specified Tuple2i.
     * @param t1 the Tuple2i containing the initialization x and y
     * data.
     */
    public Point2i(Tuple2i t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Point2i to (0,0).
     */
    public Point2i() {
	super();
    }

}

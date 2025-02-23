/*
 * Copyright 1997-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 * A 3 element texture coordinate that is represented by single precision
 * floating point x,y,z coordinates.
 *
 */
public class TexCoord3f extends Tuple3f implements java.io.Serializable {

    // Combatible with 1.1
    static final long serialVersionUID = -3517736544731446513L;

    /**
     * Constructs and initializes a TexCoord3f from the specified xyz
     * coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    public TexCoord3f(float x, float y, float z)
    {
        super(x,y,z);
    }


    /**
     * Constructs and initializes a TexCoord3f from the array of length 3.
     * @param v the array of length 3 containing xyz in order
     */
    public TexCoord3f(float[] v)
    {
       super(v);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified TexCoord3f.
     * @param v1 the TexCoord3f containing the initialization x y z data
     */
    public TexCoord3f(TexCoord3f v1)
    {
       super(v1);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified Tuple3f.
     * @param t1 the Tuple3f containing the initialization x y z data
     */
    public TexCoord3f(Tuple3f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord3f from the specified Tuple3d.
     * @param t1 the Tuple3d containing the initialization x y z data
     */
    public TexCoord3f(Tuple3d t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord3f to (0,0,0).
     */
    public TexCoord3f()
    {
        super();
    }

}

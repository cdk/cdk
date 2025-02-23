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
 * A 2-element vector that is represented by single-precision floating
 * point x,y coordinates.
 *
 */
public class TexCoord2f extends Tuple2f implements java.io.Serializable {

    // Combatible with 1.1
    static final long serialVersionUID = 7998248474800032487L;

    /**
     * Constructs and initializes a TexCoord2f from the specified xy coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public TexCoord2f(float x, float y)
    {
         super(x,y);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified array.
     * @param v the array of length 2 containing xy in order
     */
    public TexCoord2f(float[] v)
    {
         super(v);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified TexCoord2f.
     * @param v1 the TexCoord2f containing the initialization x y data
     */
    public TexCoord2f(TexCoord2f v1)
    {
        super(v1);
    }


    /**
     * Constructs and initializes a TexCoord2f from the specified Tuple2f.
     * @param t1 the Tuple2f containing the initialization x y data
     */
    public TexCoord2f(Tuple2f t1)
    {
       super(t1);
    }


    /**
     * Constructs and initializes a TexCoord2f to (0,0).
     */
    public TexCoord2f()
    {
       super();
    }


}

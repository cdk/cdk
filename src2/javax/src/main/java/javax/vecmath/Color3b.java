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

import java.awt.Color;


/**
 * A three-byte color value represented by byte x, y, and z values. The
 * x, y, and z values represent the red, green, and blue values,
 * respectively.
 * <p>
 * Note that Java defines a byte as a signed integer in the range
 * [-128, 127]. However, colors are more typically represented by values
 * in the range [0, 255]. Java 3D recognizes this and for color
 * treats the bytes as if the range were [0, 255]---in other words, as
 * if the bytes were unsigned.
 * <p>
 * Java 3D assumes that a linear (gamma-corrected) visual is used for
 * all colors.
 *
 */
public class Color3b extends Tuple3b implements java.io.Serializable {

    // Compatible with 1.1
    static final long serialVersionUID = 6632576088353444794L;

    /**
     * Constructs and initializes a Color3b from the specified three values.
     * @param c1 the red color value
     * @param c2 the green color value
     * @param c3 the blue color value
     */
    public Color3b(byte c1, byte c2, byte c3) {
	super(c1,c2,c3);
    }


    /**
     * Constructs and initializes a Color3b from input array of length 3.
     * @param c the array of length 3 containing the r,g,b data in order
     */
    public Color3b(byte[] c) {
	super(c);
    }


    /**
     * Constructs and initializes a Color3b from the specified Color3b.
     * @param c1 the Color3b containing the initialization r,g,b data
     */
    public Color3b(Color3b c1) {
	super(c1);
    }


    /**
     * Constructs and initializes a Color3b from the specified Tuple3b.
     * @param t1 the Tuple3b containing the initialization r,g,b data
     */
    public Color3b(Tuple3b t1) {
	super(t1);
    }


    /**
     * Constructs and initializes a Color3b from the specified AWT
     * Color object.  The alpha value of the AWT color is ignored.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color with which to initialize this
     * Color3b object
     *
     * @since vecmath 1.2
     */
    public Color3b(Color color) {
	super((byte)color.getRed(),
	      (byte)color.getGreen(),
	      (byte)color.getBlue());
    }


    /**
     * Constructs and initializes a Color3b to (0,0,0).
     */
    public Color3b() {
	super();
    }


    /**
     * Sets the r,g,b values of this Color3b object to those of the
     * specified AWT Color object.
     * No conversion is done on the color to compensate for
     * gamma correction.
     *
     * @param color the AWT color to copy into this Color3b object
     *
     * @since vecmath 1.2
     */
    public final void set(Color color) {
	x = (byte)color.getRed();
	y = (byte)color.getGreen();
	z = (byte)color.getBlue();
    }


    /**
     * Returns a new AWT color object initialized with the r,g,b
     * values of this Color3b object.
     *
     * @return a new AWT Color object
     *
     * @since vecmath 1.2
     */
    public final Color get() {
	int r = (int)x & 0xff;
	int g = (int)y & 0xff;
	int b = (int)z & 0xff;

	return new Color(r, g, b);
    }

}

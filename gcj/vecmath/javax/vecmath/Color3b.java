/*
   Copyright (C) 1997,1998,1999
   Kenji Hiranabe, Eiwa System Management, Inc.

   This program is free software.
   Implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification by Sun Microsystems.

   Permission to use, copy, modify, distribute and sell this software
   and its documentation for any purpose is hereby granted without fee,
   provided that the above copyright notice appear in all copies and
   that both that copyright notice and this permission notice appear
   in supporting documentation. Kenji Hiranabe and Eiwa System Management,Inc.
   makes no representations about the suitability of this software for any
   purpose.  It is provided "AS IS" with NO WARRANTY.
*/
package javax.vecmath;

import java.io.Serializable;

/**
  * A three byte vector used for colors.
  * @version specification 1.2, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public class Color3b extends Tuple3b implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:14  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.9  1999/11/25  10:55:01  hiranabe
 * awt.Color conversion
 *
 * Revision 1.8  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.7  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.6  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.5  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:04:31  hiranabe
 * *** empty log message ***
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */

    /**
      * Constructs and initializes a Color3b from the specified three values.
      * @param c1 the first value
      * @param c2 the second value
      * @param c3 the third value
      */
    public Color3b(byte c1, byte c2, byte c3) {
	super(c1, c2, c3);
    }

    /**
      * Constructs and initializes a Color3b from input array of length 3.
      * @param t the array of length 3 containing c1 c2 c3 in order
      */
    public Color3b(byte c[]) {
	// ArrayIndexOutOfBounds is thrown if t.length < 3
	super(c);
    }

    /**
      * Constructs and initializes a Color3b from the specified Color3b.
      * @param c the Color3b containing the initialization x y z data
      */
    public Color3b(Color3b c1) {
	super(c1);
    }

    /**
      * Constructs and initializes a Color3b from the specified Tuple3b.
      * @param t1 the Tuple3b containing the initialization x y z data
      */
    public Color3b(Tuple3b t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Color3b to (0,0,0).
      */
    public Color3b() {
	// super(); called implicitly
    }

    /**
     * Constructs color from awt.Color.
     *
     * @param color awt color
     */
    public Color3b(java.awt.Color color) {
        x = (byte)color.getRed();
        y = (byte)color.getGreen();
        z = (byte)color.getBlue();
    }

    /**
     * Sets color from awt.Color.
     * @param color awt color
     */
    public final void set(java.awt.Color color) {
        x = (byte)color.getRed();
        y = (byte)color.getGreen();
        z = (byte)color.getBlue();
    }
     
    /**
     * Gets awt.Color.
     *
     * @return color awt color
     */
    public final java.awt.Color get() {
        return new java.awt.Color(x, y, z);
    }
}

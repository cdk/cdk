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
  * A 3 element texture coordinate that is represented by single precision
  * floating point x,y,z coordinates.
  * @version specification 1.1, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public class TexCoord3f extends Tuple3f implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.7  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.7  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.6  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.5  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


    /**
      * Constructs and initializes a TexCoord3f from the specified xy coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public TexCoord3f(float x, float y, float z) {
	super(x, y, z);
    }

    /**
      * Constructs and initializes a TexCoord3f from the specified array.
      * @param p the array of length 3 containing xyz in order
      */
    public TexCoord3f(float v[]) {
	super(v);
    }

    /**
      * Constructs and initializes a TexCoord3f from the specified TexCoord3f.
      * @param v1 the TexCoord3f containing the initialization x y z data
      */
    public TexCoord3f(TexCoord3f v1) {
	super(v1);
    }

    /**
      * Constructs and initializes a TexCoord3f to (0,0,0).
      */
    public TexCoord3f() {
	super();
    }

}

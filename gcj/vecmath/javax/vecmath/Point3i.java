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
  * A 3 element point that is represented by signed integer x,y,z coordinates.
  * @since Java 3D 1.2
  * @version specification 1.2, implementation $Revision$, $Date$
  * @author Kenji hiranabe
  */
public class Point3i extends Tuple3i implements Serializable {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:15  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
# Revision 1.2  1999/11/25  10:29:35  hiranabe
# Java3D 1.2 integer point
#
# Revision 1.2  1999/11/25  10:29:35  hiranabe
# Java3D 1.2 integer point
#
 */
    /**
      * Constructs and initializes a Point3i from the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public Point3i(int x, int y, int z) {
	super(x, y, z);
    }

    /**
      * Constructs and initializes a Point3i from the specified array.
      * @param t the array of length 3 containing xyz in order
      */
    public Point3i(int t[]) {
	super(t);
    }

    /**
      * Constructs and initializes a Point3i from the specified Point3i.
      * @param t1 the Point3i containing the initialization x y z data
      */
    public Point3i(Point3i t1) {
	super(t1);
    }

    /**
      * Constructs and initializes a Point3i to (0,0,0).
      */
    public Point3i() {
	// super(); called implicitly.
    }
}

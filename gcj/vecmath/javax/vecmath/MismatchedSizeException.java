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

/**
 * Indicates that an operation cannot be completed properly
 * because of a mismatch in the sizes of object attributes.
 *
 * @version specification 1.1, implementation $Revision$, $Date$
 * @author Kenji hiranabe
 */
public class MismatchedSizeException extends RuntimeException {
    /**
      * Creates the exception object with default values.
      */
    public MismatchedSizeException() {
    }

    /**
      * Creates the exception object that outputs a message.
      * @param str the message string to output
      */
    public MismatchedSizeException(String str) {
        super(str);
    }
}

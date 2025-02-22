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
 * A double precision, general, dynamically-resizable,
 * one-dimensional vector class.  Index numbering begins with zero.
 */

public class GVector implements java.io.Serializable, Cloneable {

    private int length;
    double[] values;

    // Compatible with 1.1
    static final long serialVersionUID = 1398850036893875112L;

    /**
     * Constructs a new GVector of the specified
     * length with all vector elements initialized to 0.
     * @param length the number of elements in this GVector.
     */
    public GVector(int length)
	{
	    int i;

            this.length = length;
	    values = new double[length];
	    for(i = 0; i < length; i++) values[i] = 0.0;
	}

    /**
     * Constructs a new GVector from the specified array elements.
     * The length of this GVector is set to the length of the
     * specified array.  The array elements are copied into this new
     * GVector.
     * @param vector the values for the new GVector.
     */
    public GVector(double[] vector)
	{
	    int i;

            length = vector.length;
	    values = new double[vector.length];
	    for(i = 0; i < length; i++) values[i] = vector[i];
	}

    /**
     * Constructs a new GVector from the specified vector.
     * The vector elements are copied into this new GVector.
     * @param vector the source GVector for this new GVector.
     */
    public GVector(GVector vector)
	{
	    int i;

	    values = new double[vector.length];
	    length = vector.length;
	    for(i = 0; i < length; i++) values[i] = vector.values[i];
	}

    /**
      * Constructs a new GVector and copies the initial values
      * from the specified tuple.
      * @param tuple the source for the new GVector's initial values
      */
    public GVector(Tuple2f tuple)
        {
            values = new double[2];
            values[0] = (double)tuple.x;
            values[1] = (double)tuple.y;
	    length = 2;
        }

    /**
      * Constructs a new GVector and copies the initial values
      * from the specified tuple.
      * @param tuple the source for the new GVector's initial values
      */
    public GVector(Tuple3f tuple)
	{
	    values = new double[3];
	    values[0] = (double)tuple.x;
	    values[1] = (double)tuple.y;
	    values[2] = (double)tuple.z;
	    length = 3;
	}

    /**
      * Constructs a new GVector and copies the initial values
      * from the specified tuple.
      * @param tuple the source for the new GVector's initial values
      */
    public GVector(Tuple3d tuple)
	{
	    values = new double[3];
	    values[0] = tuple.x;
	    values[1] = tuple.y;
	    values[2] = tuple.z;
	    length = 3;
	}

    /**
      * Constructs a new GVector and copies the initial values
      * from the specified tuple.
      * @param tuple the source for the new GVector's initial values
      */
    public GVector(Tuple4f tuple)
	{
	    values = new double[4];
	    values[0] = (double)tuple.x;
	    values[1] = (double)tuple.y;
	    values[2] = (double)tuple.z;
	    values[3] = (double)tuple.w;
	    length = 4;
	}

    /**
      * Constructs a new GVector and copies the initial values
      * from the specified tuple.
      * @param tuple the source for the new GVector's initial values
      */
    public GVector(Tuple4d tuple)
	{
	    values = new double[4];
	    values[0] = tuple.x;
	    values[1] = tuple.y;
	    values[2] = tuple.z;
	    values[3] = tuple.w;
	    length = 4;
	}

    /**
     * Constructs a new GVector of the specified length and
     * initializes it by copying the specified number of elements from
     * the specified array.  The array must contain at least
     * <code>length</code> elements (i.e., <code>vector.length</code> &ge;
     * <code>length</code>.  The length of this new GVector is set to
     * the specified length.
     * @param  vector   The array from which the values will be copied.
     * @param  length   The number of values copied from the array.
     */
    public GVector(double vector[], int length) {
	int i;

        this.length = length;
	values = new double [length];
	for(i=0;i<length;i++) {
	    values[i] = vector[i];
        }
    }

    /**
      * Returns the square root of the sum of the squares of this
      * vector (its length in n-dimensional space).
      * @return  length of this vector
      */

    public final double norm()
    {
      double sq = 0.0;
      int i;

	for(i=0;i<length;i++) {
	    sq += values[i]*values[i];
        }

	return(Math.sqrt(sq));

    }

    /**
      * Returns the sum of the squares of this
      * vector (its length squared in n-dimensional space).
      * @return  length squared of this vector
      */
    public final double normSquared()
    {
        double sq = 0.0;
        int i;

	for(i=0;i<length;i++) {
	    sq += values[i]*values[i];
        }

	return(sq);
    }

    /**
     * Sets the value of this vector to the normalization of vector v1.
     * @param v1 the un-normalized vector
     */
    public final void normalize(GVector v1)
    {
      double sq = 0.0;
      int i;

      if( length != v1.length)
          throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));

       for(i=0;i<length;i++) {
	    sq += v1.values[i]*v1.values[i];
       }

       double invMag;
       invMag = 1.0/Math.sqrt(sq);

       for(i=0;i<length;i++) {
          values[i] = v1.values[i]*invMag;
       }
    }


    /**
     * Normalizes this vector in place.
     */
    public final void normalize()
    {
      double sq = 0.0;
      int i;

        for(i=0;i<length;i++) {
            sq += values[i]*values[i];
        }

        double invMag;
	invMag = 1.0/Math.sqrt(sq);

        for(i=0;i<length;i++) {
            values[i] = values[i]*invMag;
        }

    }

    /**
     * Sets the value of this vector to the scalar multiplication
     * of the scale factor with the vector v1.
     * @param s the scalar value
     * @param v1 the source vector
     */
    public final void scale(double s, GVector v1)
    {
        int i;
	if( length != v1.length)
          throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));

        for(i=0;i<length;i++) {
            values[i] = v1.values[i]*s;
        }
    }

    /**
     * Scales this vector by the scale factor s.
     * @param s the scalar value
     */
    public final void scale(double s)
    {
      int i;

        for(i=0;i<length;i++) {
            values[i] = values[i]*s;
        }
    }

    /**
     * Sets the value of this vector to the scalar multiplication by s
     * of vector v1 plus vector v2 (this = s*v1 + v2).
     * @param s the scalar value
     * @param v1 the vector to be multiplied
     * @param v2 the vector to be added
     */
    public final void scaleAdd(double s, GVector v1, GVector v2)
    {

      int i;

      if( v2.length != v1.length )
	 throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));

       if( length  != v1.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));

        for(i=0;i<length;i++) {
            values[i] = v1.values[i]*s + v2.values[i];
        }
    }

    /**
      * Sets the value of this vector to sum of itself and the specified
      * vector
      * @param vector the second vector
      */
    public final void add(GVector vector)
	{
	    int i;

          if( length  != vector.length )
	     throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));

	   for(i = 0; i < length; i++) {
		this.values[i] += vector.values[i];
	   }
	}

    /**
      * Sets the value of this vector to the vector sum of vectors vector1
      * and vector2.
      * @param vector1 the first vector
      * @param vector2 the second vector
      */
    public final void add(GVector vector1, GVector vector2)
	{
         int i;

         if( vector1.length != vector2.length )
   	   throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));

       if( length  != vector1.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));

	    for(i = 0; i < length; i++)
		this.values[i] = vector1.values[i] + vector2.values[i];
	}

    /**
      * Sets the value of this vector to the vector difference of itself
      * and vector (this = this - vector).
      * @param vector the other vector
      */
    public final void sub(GVector vector)
	{
	    int i;

           if( length  != vector.length )
    	        throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));

	    for(i = 0; i < length; i++) {
		this.values[i] -= vector.values[i];
	    }
	}

    /**
      * Sets the value of this vector to the vector difference
      * of vectors vector1 and vector2 (this = vector1 - vector2).
      * @param vector1 the first vector
      * @param vector2 the second vector
      */
    public final void sub(GVector vector1, GVector vector2)
	{
	    int i;


	if( vector1.length != vector2.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));

       if( length  != vector1.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));

	    for(i = 0; i < length; i++)
		this.values[i] = vector1.values[i] - vector2.values[i];
	}

    /**
      * Multiplies matrix m1 times Vector v1 and places the result
      * into this vector (this = m1*v1).
      * @param m1  The matrix in the multiplication
      * @param v1  The vector that is multiplied
      */
    public final void mul(GMatrix m1, GVector v1) {
       if (m1.getNumCol() != v1.length)
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));

       if (length  != m1.getNumRow())
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));

       double v[];
       if (v1 != this) {
	   v = v1.values;
       } else {
	   v = (double []) values.clone();
       }

       for(int j=length-1; j>=0; j--){
	  values[j] = 0.0;
	  for(int i=v1.length-1;i>=0; i--){
	    values[j] += m1.values[j][i] * v[i];
	  }
       }
     }

    /**
      * Multiplies the transpose of vector v1 (ie, v1 becomes a row
      * vector with respect to the multiplication) times matrix m1
      * and places the result into this vector
      * (this = transpose(v1)*m1).  The result is technically a
      * row vector, but the GVector class only knows about column
      * vectors, and so the result is stored as a column vector.
      * @param m1  The matrix in the multiplication
      * @param v1  The vector that is temporarily transposed
      */
    public final void mul(GVector v1, GMatrix m1) {
	if (m1.getNumRow() != v1.length)
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));

       if (length  != m1.getNumCol())
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));

       double v[];
       if (v1 != this) {
	   v = v1.values;
       } else {
	   v = (double []) values.clone();
       }

       for (int j=length-1; j>=0; j--){
	  values[j] = 0.0;
	  for(int i=v1.length-1; i>=0; i--){
	     values[j] += m1.values[i][j] * v[i];
	  }
       }
     }

    /**
      * Negates the value of this vector: this = -this.
      */
    public final void negate() {
	for(int i=length-1; i>=0; i--) {
	    this.values[i] *= -1.0;
	}
    }

    /**
      * Sets all the values in this vector to zero.
      */
    public final void zero() {
	for (int i=0; i < this.length; i++) {
	    this.values[i] = 0.0;
	}
    }

    /**
      * Changes the size of this vector dynamically.  If the size is increased
      * no data values will be lost.  If the size is decreased, only those data
      * values whose vector positions were eliminated will be lost.
      * @param length  number of desired elements in this vector
      */
    public final void setSize(int length) {
	   double[] tmp = new double[length];
	   int i,max;

           if( this.length < length)
	      max = this.length;
           else
	       max = length;

	   for(i=0;i<max;i++) {
	      tmp[i] = values[i];
           }
	   this.length = length;

	   values = tmp;

	}

    /**
      * Sets the value of this vector to the values found in the array
      * parameter. The array should be at least equal in length to
      * the number of elements in the vector.
      * @param vector  the source array
      */
    public final void set(double[] vector) {
	for(int i = length-1; i >=0; i--)
	    values[i] = vector[i];
    }

    /**
      * Sets the value of this vector to the values found in vector vector.
      * @param vector  the source vector
      */
    public final void set(GVector vector) {
	int i;

	if (length < vector.length) {
	    length = vector.length;
	    values = new double[length];
	    for(i = 0; i < length; i++)
		values[i] = vector.values[i];
	}else {
	    for(i = 0; i < vector.length; i++)
		values[i] = vector.values[i];
	    for(i = vector.length; i < length; i++)
		values[i] = 0.0;
	}
    }

    /**
      * Sets the value of this vector to the values in tuple
      * @param tuple the source for the new GVector's new values
      */
    public final void set(Tuple2f tuple)
        {
            if (length < 2) {
	       length = 2;
	       values = new double[2];
	    }
            values[0] = (double)tuple.x;
            values[1] = (double)tuple.y;
	    for(int i = 2; i < length; i++) values[i] = 0.0;

        }

    /**
      * Sets the value of this vector to the values in tuple
      * @param tuple the source for the new GVector's new values
      */
    public final void set(Tuple3f tuple)
	{
            if (length < 3) {
	       length = 3;
	       values = new double[3];
	    }
            values[0] = (double)tuple.x;
            values[1] = (double)tuple.y;
	    values[2] = (double)tuple.z;
	    for(int i = 3; i < length; i++) values[i] = 0.0;
	}

    /**
      * Sets the value of this vector to the values in tuple
      * @param tuple the source for the new GVector's new values
      */
    public final void set(Tuple3d tuple)
	{
            if (length < 3) {
	       length = 3;
	       values = new double[3];
	    }
            values[0] = tuple.x;
            values[1] = tuple.y;
	    values[2] = tuple.z;
	    for(int i = 3; i < length; i++) values[i] = 0.0;
	}

    /**
      * Sets the value of this vector to the values in tuple
      * @param tuple the source for the new GVector's new values
      */
    public final void set(Tuple4f tuple)
	{
            if (length < 4) {
	       length = 4;
	       values = new double[4];
	    }
            values[0] = (double)tuple.x;
            values[1] = (double)tuple.y;
	    values[2] = (double)tuple.z;
	    values[3] = (double)tuple.w;
	    for(int i = 4; i < length; i++) values[i] = 0.0;
	}

    /**
      * Sets the value of this vector to the values in tuple
      * @param tuple the source for the new GVector's new values
      */
    public final void set(Tuple4d tuple)
	{
            if (length < 4) {
	       length = 4;
	       values = new double[4];
	    }
            values[0] = tuple.x;
            values[1] = tuple.y;
	    values[2] = tuple.z;
	    values[3] = tuple.w;
	    for(int i = 4; i < length; i++) values[i] = 0.0;
	}

    /**
      * Returns the number of elements in this vector.
      * @return  number of elements in this vector
      */
    public final int getSize()
	{
	    return values.length;
	}

    /**
      * Retrieves the value at the specified index value of this vector.
      * @param index the index of the element to retrieve (zero indexed)
      * @return the value at the indexed element
      */
    public final double getElement(int index)
	{
	    return values[index];
	}


    /**
      * Modifies the value at the specified index of this vector.
      * @param index  the index if the element to modify (zero indexed)
      * @param value  the new vector element value
      */
    public final void setElement(int index, double value)
	{
	    values[index] = value;
	}

    /**
      * Returns a string that contains the values of this GVector.
      * @return the String representation
      */
    @Override
    public String toString() {
      StringBuffer buffer = new StringBuffer(length*8);

      int i;

      for(i=0;i<length;i++) {
           buffer.append(values[i]).append(" ");
      }

      return buffer.toString();

    }


    /**
     * Returns a hash code value based on the data values in this
     * object.  Two different GVector objects with identical data
     * values (i.e., GVector.equals returns true) will return the
     * same hash number.  Two GVector objects with different data
     * members may return the same hash value, although this is not
     * likely.
     * @return the integer hash code value
     */
    @Override
    public int hashCode() {
	long bits = 1L;

	for (int i = 0; i < length; i++) {
		bits = VecMathUtil.hashDoubleBits(bits, values[i]);
	}

	return VecMathUtil.hashFinish(bits);
    }


    /**
      * Returns true if all of the data members of GVector vector1 are
      * equal to the corresponding data members in this GVector.
      * @param vector1  The vector with which the comparison is made.
      * @return  true or false
      */
    public boolean equals(GVector vector1)
	{
        try {
	    if( length != vector1.length)   return false;

            for(int i = 0;i<length;i++) {
	      if( values[i] != vector1.values[i]) return false;
            }

	    return true;
        }
        catch (NullPointerException e2) { return false; }

         }
    /**
     * Returns true if the Object o1 is of type GMatrix and all of the
     * data members of o1 are equal to the corresponding data members in
     * this GMatrix.
     * @param o1  The object with which the comparison is made.
     * @return  true or false
     */
    @Override
    public boolean equals(Object o1)
    {
        try {
            GVector v2 = (GVector) o1;

	    if( length != v2.length)   return false;

            for(int i = 0;i<length;i++) {
	      if( values[i] != v2.values[i]) return false;
            }
	    return true;
        }
        catch (ClassCastException   e1) { return false; }
        catch (NullPointerException e2) { return false; }

    }

   /**
     * Returns true if the L-infinite distance between this vector
     * and vector v1 is less than or equal to the epsilon parameter,
     * otherwise returns false.  The L-infinite
     * distance is equal to
     * MAX[abs(x1-x2), abs(y1-y2), . . .  ].
     * @param v1  The vector to be compared to this vector
     * @param epsilon  the threshold value
     */
    public boolean epsilonEquals(GVector v1, double epsilon)
    {
       double diff;

	    if( length != v1.length)   return false;

            for(int i = 0;i<length;i++) {
	        diff = values[i] - v1.values[i];
                if( (diff<0?-diff:diff) > epsilon) return false;
            }
	    return true;
    }

  /**
    * Returns the dot product of this vector and vector v1.
    * @param v1 the other vector
    * @return the dot product of this and v1
    */
   public final double dot(GVector v1)
     {
       if( length != v1.length)
	       throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));

       double result = 0.0;
       for(int i = 0;i<length;i++) {
	  result += values[i] * v1.values[i];
       }
       return result;
     }


  /**
    *  Solves for x in Ax = b, where x is this vector (nx1), A is mxn,
    *  b is mx1, and A = U*W*transpose(V); U,W,V must
    *  be precomputed and can be found by taking the singular value
    *  decomposition (SVD) of A using the method SVD found in the
    *  GMatrix class.
    *  @param U  The U matrix produced by the GMatrix method SVD
    *  @param W  The W matrix produced by the GMatrix method SVD
    *  @param V  The V matrix produced by the GMatrix method SVD
    *  @param b  The b vector in the linear equation Ax = b
    */
   public final void SVDBackSolve(GMatrix U, GMatrix W, GMatrix V, GVector b)
    {
       if( !(U.nRow == b.getSize() &&
	     U.nRow == U.nCol      &&
	     U.nRow == W.nRow  ) ) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
       }

       if( !(W.nCol == values.length &&
	     W.nCol == V.nCol        &&
	     W.nCol == V.nRow  ) ) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
       }

       GMatrix tmp = new GMatrix( U.nRow, W.nCol);
       tmp.mul( U, V);
       tmp.mulTransposeRight( U, W);
       tmp.invert();
       mul(tmp, b);

    }

   /**
     * LU Decomposition Back Solve; this method takes the LU matrix
     * and the permutation vector produced by the GMatrix method LUD
     * and solves the equation (LU)*x = b by placing the solution vector
     * x into this vector.  This vector should be the same length or
     * longer than b.
     * @param LU  The matrix into which the lower and upper decompostions
     * have been placed
     * @param b  The b vector in the equation (LU)*x = b
     * @param permutation  The row permuations that were necessary to
     * produce the LU matrix parameter
     */
   public final void LUDBackSolve(GMatrix LU, GVector b, GVector permutation)
   {
       int size = LU.nRow*LU.nCol;

       double[] temp = new double[size];
       double[] result = new double[size];
       int[] row_perm = new int[b.getSize()];
       int i,j;

       if( LU.nRow != b.getSize() ) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
       }

       if( LU.nRow != permutation.getSize() ) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
       }

       if (LU.nRow != LU.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
       }

        for(i=0;i<LU.nRow;i++) {
           for(j=0;j<LU.nCol;j++) {
               temp[i*LU.nCol+j] = LU.values[i][j];
           }
        }

       for(i=0;i<size;i++) result[i] = 0.0;
       for(i=0;i<LU.nRow;i++) result[i*LU.nCol] = b.values[i];
       for(i=0;i<LU.nCol;i++) row_perm[i] = (int)permutation.values[i];

       GMatrix.luBacksubstitution(LU.nRow, temp, row_perm, result);

       for(i=0;i<LU.nRow;i++) this.values[i] = result[i*LU.nCol];
   }

  /**
    *   Returns the (n-space) angle in radians between this vector and
    *   the vector parameter; the return value is constrained to the
    *   range [0,PI].
    *   @param v1    The other vector
    *   @return   The angle in radians in the range [0,PI]
    */
   public final double angle(GVector v1)
   {
   return( Math.acos( this.dot(v1) / ( this.norm()*v1.norm() ) ) );
   }


    /**
     * @deprecated Use interpolate(GVector, GVector, double) instead
     */
    public final void interpolate(GVector v1, GVector v2, float alpha) {
	interpolate(v1, v2, (double)alpha);
    }


    /**
     * @deprecated Use interpolate(GVector, double) instead
     */
    public final void interpolate(GVector v1, float alpha) {
	interpolate(v1, (double)alpha);
    }


  /**
    *  Linearly interpolates between vectors v1 and v2 and places the
    *  result into this tuple:  this = (1-alpha)*v1 + alpha*v2.
    *  @param v1  the first vector
    *  @param v2  the second vector
    *  @param alpha  the alpha interpolation parameter
    */
  public final void interpolate(GVector v1, GVector v2, double alpha)
  {
	if( v2.length != v1.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));

       if( length  != v1.length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));

       for(int i=0;i<length;i++) {
	  values[i] = (1-alpha)*v1.values[i] + alpha*v2.values[i];
       }
  }

  /**
    *  Linearly interpolates between this vector and vector v1 and
    *  places the result into this tuple:  this = (1-alpha)*this + alpha*v1.
    *  @param v1  the first vector
    *  @param alpha  the alpha interpolation parameter
    */
  public final void interpolate(GVector v1, double alpha)
  {
       if( v1.length != length )
	  throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));

       for(int i=0;i<length;i++) {
	  values[i] = (1-alpha)*values[i] + alpha*v1.values[i];
       }
  }

    /**
     * Creates a new object of the same class as this object.
     *
     * @return a clone of this instance.
     * @exception OutOfMemoryError if there is not enough memory.
     * @see java.lang.Cloneable
     * @since vecmath 1.3
     */
    @Override
    public Object clone() {
	GVector v1 = null;
	try {
	    v1 = (GVector)super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}

	// Also need to clone array of values
	v1.values = new double[length];
	for (int i = 0; i < length; i++) {
	    v1.values[i] = values[i];
	}

	return v1;
    }

}

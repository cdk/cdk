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
 * java.vecmath Test class.
 * <pre>
 *   % java java.vecmath.VecmathTest
 * </pre>
 * to run this test.
 *
 * If you find any bugs, please add a test method to reproduce the bug,
 * and insert the method call to this main driver.
 */
public class VecmathTest {
/*
 * $Log$
 * Revision 1.1  2002/08/22 20:01:16  egonw
 * Lots of new files. Amongst which the source code of vecmath.jar.
 * The latter has been changed to compile with gcj-3.0.4.
 * Actually, CDK does now compile, i.e. at least the classes mentioned
 * in core.classes and extra.classes. *And* a binary executable can get
 * generated that works!
 *
 * Revision 1.10  1999/11/25  10:30:23  hiranabe
 * get(GMatrix) bug
 *
 * Revision 1.10  1999/11/25  10:30:23  hiranabe
 * get(GMatrix) bug
 *
 * Revision 1.9  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.8  1999/03/11  00:17:50  hiranabe
 * removed some println's
 *
 * Revision 1.7  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.6  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.5  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.4  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.3  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.2  1997/12/10  06:08:05  hiranabe
 * *** empty log message ***
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */

    static public String NL = System.getProperty("line.separator"); 
    static public float epsilon = 1.0e-5f;
    static public boolean equals(double m1, double m2) {
	return Math.abs(m1 - m2) < (double)epsilon;
    }
    static public boolean equals(Matrix3d m1, Matrix3d m2) {
	return m1.epsilonEquals(m2, (double)epsilon);
    }
    static public boolean equals(Matrix4d m1, Matrix4d m2) {
	return m1.epsilonEquals(m2, (double)epsilon);
    }
    static public boolean equals(Tuple4d m1, Tuple4d m2) {
	return m1.epsilonEquals(m2, (double)epsilon);
    }
    static public boolean equals(Tuple3d m1, Tuple3d m2) {
	return m1.epsilonEquals(m2, (double)epsilon);
    }
    static public boolean equals(Matrix3f m1, Matrix3f m2) {
	return m1.epsilonEquals(m2, epsilon);
    }
    static public boolean equals(Matrix4f m1, Matrix4f m2) {
	return m1.epsilonEquals(m2, epsilon);
    }
    static public boolean equals(GMatrix m1, GMatrix m2) {
	return m1.epsilonEquals(m2, (double)epsilon);
    }
    static public boolean equals(GVector v1, GVector v2) {
	return v1.epsilonEquals(v2, (double)epsilon);
    }
    static public boolean equals(Tuple4f m1, Tuple4f m2) {
	return m1.epsilonEquals(m2, epsilon);
    }
    static public boolean equals(Tuple3f m1, Tuple3f m2) {
	return m1.epsilonEquals(m2, epsilon);
    }
    static public boolean equals(AxisAngle4d a1, AxisAngle4d a2) {
	if (0 < a1.x*a2.x + a1.y*a2.y + a1.z*a2.z) {  // same direction
	    return equals(a1.y*a2.z - a1.z*a2.y, 0) &&
		   equals(a1.z*a2.x - a1.x*a2.z, 0) &&
		   equals(a1.x*a2.y - a1.y*a2.x, 0) &&
		   equals(a1.angle, a2.angle);
	} else {
	    return equals(a1.y*a2.z - a1.z*a2.y, 0) &&
		   equals(a1.z*a2.x - a1.x*a2.z, 0) &&
		   equals(a1.x*a2.y - a1.y*a2.x, 0) &&
		   (
		    equals(a1.angle, -a2.angle) || 
		    equals(a1.angle + a2.angle, 2*Math.PI) || 
		    equals(a1.angle + a2.angle, -2*Math.PI)
		    );
	}
    }
    static public boolean equals(AxisAngle4f a1, AxisAngle4f a2) {
	if (0 < a1.x*a2.x + a1.y*a2.y + a1.z*a2.z) {  // same direction
	    return equals(a1.y*a2.z - a1.z*a2.y, 0) &&
		   equals(a1.z*a2.x - a1.x*a2.z, 0) &&
		   equals(a1.x*a2.y - a1.y*a2.x, 0) &&
		   equals(a1.angle, a2.angle);
	} else {
	    return equals(a1.y*a2.z - a1.z*a2.y, 0) &&
		   equals(a1.z*a2.x - a1.x*a2.z, 0) &&
		   equals(a1.x*a2.y - a1.y*a2.x, 0) &&
		   (
		    equals(a1.angle, -a2.angle) || 
		    equals(a1.angle + a2.angle, 2*Math.PI) || 
		    equals(a1.angle + a2.angle, -2*Math.PI)
		    );
	}
    }
    static public void ASSERT(boolean condition) {
	if (!condition)
	    throw new InternalError("Vecmath Test Failed!");
    }
    static public void ASSERT(boolean condition, String comment) {
	if (!condition)
	    throw new InternalError("Vecmath Test Failed!: " + comment);
    }
    static public void exit() {
	System.out.println("java.vecmath all test passed successfully.");
	System.out.print("Quit ?");
	try {
	    System.in.read();
	} catch (java.io.IOException e) {}
    }
    static public void main(String[] v) {
	System.out.print("Vector3d ...");
	Vector3dTest();
	System.out.println("ok.");
	System.out.print("Vector3f ...");
	Vector3fTest();
	System.out.println("ok.");
	System.out.print("Matrix3d with Quat4d, AxisAngle4d, Point/Vector3d interaction ...");
	Matrix3dTest();
	System.out.println("ok.");

	System.out.print("Matrix3f with Quat4f, AxisAngle4f, Point/Vector3f interaction ...");
	Matrix3fTest();
	System.out.println("ok.");

	System.out.print("Matrix4d with Quat4d, AxisAngle4d, Point/Vector3d interaction ...");
	Matrix4dTest();
	System.out.println("ok.");
	System.out.print("Matrix4f with Quat4f, AxisAngle4f, Point/Vector3f interaction ...");
	Matrix4fTest();
	System.out.println("ok.");

	System.out.print("GMatrix with GVector interaction ...");
	GMatrixTest();
	System.out.println("ok.");

	System.out.print("SVD test ...");
	SVDTest();
	System.out.println("ok.");

	exit();
    }

    /////////////////////
    //  test  methods.
    /////////////////////

    static public void Vector3dTest() {
	Vector3d zeroVector = new Vector3d();
	Vector3d v1 = new Vector3d(2,3,4);
	Vector3d v2 = new Vector3d(2,5,-8);

	Vector3d v3 = new Vector3d();
	v3.cross(v1, v2);

	// check cross and dot.
	ASSERT(equals(v3.dot(v1), 0));
	ASSERT(equals(v3.dot(v2), 0));

	// check alias-safe
	v1.cross(v1, v2);
	ASSERT(equals(v1, new Vector3d(-44,24,4)));

	// check length
	ASSERT(equals(v2.lengthSquared(), 93));
	ASSERT(equals(v2.length(), Math.sqrt(93)));

	// check normalize
	v1.set(v2);
	v2.normalize();
	ASSERT(equals(v2.length(), 1));
	v1.cross(v2,v1);
	ASSERT(equals(v1, zeroVector));

	// check Angle
	v1.set(1,2,3);
	v2.set(-1,-6,-3);
	double ang = v1.angle(v2);
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));

	// check Angle (0)
	v1.set(v2);
	ang = v1.angle(v2);
	ASSERT(equals(ang, 0));
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));

	// check small Angle
	v1.set(1,2,3);
	v2.set(1,2,3.00001);
	ang = v1.angle(v2);
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));

	// check large Angle
	v1.set(1,2,3);
	v2.set(-1,-2,-3.00001);
	ang = v1.angle(v2);
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));
    }

    static public void Vector3fTest() {
	Vector3f zeroVector = new Vector3f();
	Vector3f v1 = new Vector3f(2,3,4);
	Vector3f v2 = new Vector3f(2,5,-8);
	// System.out.println("v1=" + v1.toString());

	Vector3f v3 = new Vector3f();
	// System.out.println("v1=" + v1.toString());
	v3.cross(v1, v2);

	// check cross and dot.
	// System.out.println("v3=" + v3.toString());
	// System.out.println("v1=" + v1.toString());
	// System.out.println("v3.dot(v1) = " + v3.dot(v1));
	ASSERT(equals(v3.dot(v1), 0));
	ASSERT(equals(v3.dot(v2), 0));

	// check alias-safe
	v1.cross(v1, v2);
	ASSERT(equals(v1, new Vector3f(-44,24,4)));

	// check length
	ASSERT(equals(v2.lengthSquared(), 93));
	ASSERT(equals(v2.length(), Math.sqrt(93)));

	// check normalize
	v1.set(v2);
	v2.normalize();
	ASSERT(equals(v2.length(), 1));
	v1.cross(v2,v1);
	ASSERT(equals(v1, zeroVector));

	// check Angle
	v1.set(1,2,3);
	v2.set(-1,-6,-3);
	double ang = v1.angle(v2);
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));

	// check Angle (0)
	v1.set(v2);
	ang = v1.angle(v2);
	ASSERT(equals(ang, 0));
	ASSERT(equals(v1.length()*v2.length()*Math.cos(ang), v1.dot(v2)));
    }

    static public void Matrix3dTest() {
	Matrix3d O = new Matrix3d();
	Matrix3d I = new Matrix3d(); I.setIdentity();
	Matrix3d m1 = new Matrix3d();
	Matrix3d m2 = new Matrix3d();
	double [] v = { 2,1,4, 1,-2,3, -3,-1,1 };

	// check get/set
	for (int i = 0; i < 3; i++) {
	    for (int j = 0; j < 3; j++)
		m1.setElement(i, j, i*2*j + 3);
	}
	for (int i = 0; i < 3; i++) {
	    for (int j = 0; j < 3; j++)
		ASSERT(equals(m1.getElement(i, j), i*2*j + 3));
	}

	// check mul with O, I
	m1.set(v);
	m2 = new Matrix3d(m1);
	m2.mul(O);
	ASSERT(equals(m2, O));
	m2.mul(m1, I);
	ASSERT(equals(m2, m1));

	// check determinant
	ASSERT(equals(m1.determinant(), -36));

	// check negate, add
	m2.negate(m1);
	m2.add(m1);
	ASSERT(equals(m2, O));

	// check mul, sub
	m2.negate(m1);
	Matrix3d m3 = new Matrix3d(m1);
	m3.sub(m2);
	m3.mul(0.5);
	ASSERT(equals(m1, m3));
	
	// check invert
	m3.invert(m2);
	m3.mul(m2);
	ASSERT(equals(m3, I));

	// translate
	Point3d p1 = new Point3d(1,2,3);
	Vector3d v1 = new Vector3d(2,-1,-4);

	// rotZ
	// rotate (1,0,0) 30degree abount z axis -> (cos 30,sin 30,0)
	p1.set(1,0,0);
	m1.rotZ(Math.PI/6);
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/6),
	    Math.sin(Math.PI/6),
	    0)));

	// rotY
	// rotate() (1,0,0) 60degree about y axis -> (cos 60,0,-sin 60)
	p1.set(1,0,0);
	m1.rotY(Math.PI/3);
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));

	// rot around arbitary axis
	// rotate() (1,0,0) 60degree about y axis -> (cos 60,0,-sin 60)
	AxisAngle4d a1 = new AxisAngle4d(0,1,0,Math.PI/3);
	p1.set(1,0,0);
	m1.set(a1);
	m1.transform(p1, p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));

	// use quat.
	Quat4d q1 = new Quat4d();
	p1.set(1,0,0);
	q1.set(a1);
	m2.set(q1);
	ASSERT(equals(m1, m2));
	m2.transform(p1, p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));

	// Mat <-> Quat <-> Axis
	a1.set(1,2,-3,Math.PI/3);
	Mat3dQuatAxisAngle(a1);

	// Mat <-> Quat <-> Axis (near PI case)
	a1.set(1,2,3,Math.PI);
	Mat3dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, X major case )
	a1.set(1,.1,.1,Math.PI);
	Mat3dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, Y major case )
	a1.set(.1,1,.1,Math.PI);
	Mat3dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, Z major case )
	a1.set(.1,.1,1,Math.PI);
	Mat3dQuatAxisAngle(a1);

	// isometric view 3 times 2/3 turn
	a1.set(1,1,1,2*Math.PI/3);
	m1.set(a1);
	//System.out.println("m1="+m1);
	p1.set(1,0,0);
	//System.out.println("p1="+p1);
	m1.transform(p1);
	//System.out.println("after transform p1="+p1);
	ASSERT(equals(p1, new Point3d(0,1,0)));
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(0,0,1)));
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(1,0,0)));

	// check normalize, normalizeCP
	m1.set(a1);
	ASSERT(equals(m1.determinant(), 1));
	ASSERT(equals(m1.getScale(), 1));
	m2.set(a1);
	m2.normalize();
	ASSERT(equals(m1, m2));
	m2.set(a1);
	m2.normalizeCP();
	ASSERT(equals(m1, m2));
	double scale = 3.0;
	m2.rotZ(-Math.PI/4);
	m2.mul(scale);
	ASSERT(equals(m2.determinant(), scale*scale*scale));
	ASSERT(equals(m2.getScale(), scale));
	m2.normalize();
	ASSERT(equals(m2.determinant(), 1));
	ASSERT(equals(m2.getScale(), 1));
	m2.rotX(Math.PI/3);
	m2.mul(scale);
	ASSERT(equals(m2.determinant(), scale*scale*scale));
	ASSERT(equals(m2.getScale(), scale));
	m2.normalizeCP();
	ASSERT(equals(m2.determinant(), 1));
	ASSERT(equals(m2.getScale(), 1));

	// transpose and inverse
	m1.set(a1);
	m2.invert(m1);
	m1.transpose();
	ASSERT(equals(m1, m2));
    }

    static void Mat3dQuatAxisAngle(AxisAngle4d a1) {
	Matrix3d m1 = new Matrix3d();
	Matrix3d m2 = new Matrix3d();
	AxisAngle4d a2 = new AxisAngle4d();
	Quat4d q1 = new Quat4d();
	Quat4d q2 = new Quat4d();

	// Axis <-> Quat
	q1.set(a1);
	a2.set(q1);
	// a1.v parallels to a2.v 
	ASSERT(equals(a1, a2));
	q2 = new Quat4d();
	q2.set(a2);
	ASSERT(equals(q1, q2));

	// Quat <-> Mat
	q1.set(a1);
	m1.set(q1);
	q2.set(m1);
	ASSERT(equals(q1, q2));
	m2.set(q2);
	ASSERT(equals(m1, m2));

	// Mat <-> AxisAngle
	m1.set(a1);
	a2.set(m1);
	//System.out.println("a1="+a1);
	//System.out.println("a2="+a2);
	//System.out.println("m1="+m1);
	ASSERT(equals(a1, a2));
	m2.set(a1);
	ASSERT(equals(m1, m2));
	a1.x *= 2; a1.y *= 2; a1.z *= 2;
	m2.set(a1);
	a1.x = -a1.x; a1.y = -a1.y; a1.z = -a1.z; a1.angle = -a1.angle;
	m2.set(a1);
	ASSERT(equals(m1, m2));

    }


    static public void Matrix3fTest() {
    }

    static void Mat4dQuatAxisAngle(AxisAngle4d a1) {
	Matrix4d m1 = new Matrix4d();
	Matrix4d m2 = new Matrix4d();
	AxisAngle4d a2 = new AxisAngle4d();
	Quat4d q1 = new Quat4d();
	Quat4d q2 = new Quat4d();

	// Axis <-> Quat
	q1.set(a1);
	a2.set(q1);
	// a1.v parallels to a2.v 
	ASSERT(equals(a1, a2));
	q2 = new Quat4d();
	q2.set(a2);
	ASSERT(equals(q1, q2));

	// Quat <-> Mat
	q1.set(a1);
	m1.set(q1);
	q2.set(m1);
	ASSERT(equals(q1, q2));
	m2.set(q2);
	ASSERT(equals(m1, m2));

	// Mat <-> AxisAngle
	m1.set(a1);
	a2.set(m1);
	//System.out.println("a1="+a1);
	//System.out.println("a2="+a2);
	//System.out.println("m1="+m1);
	ASSERT(equals(a1, a2));
	m2.set(a1);
	ASSERT(equals(m1, m2));
	a1.x *= 2; a1.y *= 2; a1.z *= 2;
	m2.set(a1);
	a1.x = -a1.x; a1.y = -a1.y; a1.z = -a1.z; a1.angle = -a1.angle;
	m2.set(a1);
	ASSERT(equals(m1, m2));

    }

    static public void Matrix4dTest() {
	Matrix4d O = new Matrix4d();
	Matrix4d I = new Matrix4d(); I.setIdentity();
	Matrix4d m1 = new Matrix4d();
	Matrix4d m2 = new Matrix4d();

	// check get/set
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++)
		m1.setElement(i, j, i*2*j + 3);
	}
	for (int i = 0; i < 4; i++) {
	    for (int j = 0; j < 4; j++)
		ASSERT(equals(m1.getElement(i, j), i*2*j + 3));
	}

	// check mul with O, I
	m1 = new Matrix4d(
	    2,1,4,1,
	    -2,3,-3,1,
	    -1,1,2,2,
	    0,8,1,-10);
	m2 = new Matrix4d(m1);
	m2.mul(O);
	ASSERT(equals(m2, O), "O = m2 x O");
	m2.mul(m1, I);
    // System.out.println("m2 = " + m2.toString());
    // System.out.println("m1 = " + m1.toString());
	ASSERT(equals(m2, m1), "m2 = m1 x I");

	// check negate, add
	m2.negate(m1);
	m2.add(m1);
	ASSERT(equals(m2, O));

	// check mul, sub
	double v[] = { 5,1,4,0,
	       2,3,-4,-1,
	       2,3,-4,-1,
	       1,1,1,1};
	m2.set(v);
	m2.negate(m1);
	Matrix4d m3 = new Matrix4d(m1);
	m3.sub(m2);
	m3.mul(0.5);
	ASSERT(equals(m1, m3));
	
	// System.out.println("4");

	// check invert
	m2 = new Matrix4d(
	    .5,1,4,1,
	    -2,3,-4,-1,
	    1,9,100,2,
	    -20,2,1,9);
	m3.invert(m2);
	m3.mul(m2);
	ASSERT(equals(m3, I));

	// System.out.println("5");

	// translate
	m1 = new Matrix4d(
	    -1,2,0,3,
	    -1,1,-3,-1,
	    1,2,1,1,
	    0,0,0,1);
	Point3d p1 = new Point3d(1,2,3);
	Vector3d v0 = new Vector3d();
	Vector3d v1 = new Vector3d(1,2,3);
	Vector4d V2 = new Vector4d(2,-1,-4,1);

	// System.out.println("m1=" + m1.toString());
	ASSERT(m1.toString().equals("[" + NL +
"  [-1.0	2.0	0.0	3.0]" + NL +
"  [-1.0	1.0	-3.0	-1.0]" + NL +
"  [1.0	2.0	1.0	1.0]" + NL +
"  [0.0	0.0	0.0	1.0] ]"));

	// System.out.println("6");
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(6,-9,9)));
	// System.out.println("7");
	m1.transform(V2,V2);
	ASSERT(equals(V2, new Vector4d(-1,8,-3,1)));
	// System.out.println("8");

		      
	
	// rotZ
	// rotate (1,0,0) 30degree abount z axis -> (cos 30,sin 30,0)
	p1.set(1,0,0);
	m1.rotZ(Math.PI/6);
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/6),
	    Math.sin(Math.PI/6),
	    0)));
	// System.out.println("9");

	// rotY
	// rotate() (1,0,0) 60degree about y axis -> (cos 60,0,-sin 60)
	p1.set(1,0,0);
	m1.rotY(Math.PI/3);
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));
	// System.out.println("10");

	// rot around arbitary axis
	// rotate() (1,0,0) 60degree about y axis -> (cos 60,0,-sin 60)
	AxisAngle4d a1 = new AxisAngle4d(0,1,0,Math.PI/3);
	p1.set(1,0,0);
	m1.set(a1);
	m1.transform(p1, p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));
	// System.out.println("11");

	// use quat.
	Quat4d q1 = new Quat4d();
	p1.set(1,0,0);
	q1.set(a1);
	m2.set(q1);
	ASSERT(equals(m1, m2));
	// System.out.println("12");
	m2.transform(p1, p1);
	ASSERT(equals(p1, new Point3d(
	    Math.cos(Math.PI/3),
	    0,
	    -Math.sin(Math.PI/3))));
	// System.out.println("13");

	// Mat <-> Quat <-> Axis
	a1.set(1,2,-3,Math.PI/3);
	Mat4dQuatAxisAngle(a1);

	// Mat <-> Quat <-> Axis (near PI case)
	a1.set(1,2,3,Math.PI);
	Mat4dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, X major case )
	a1.set(1,.1,.1,Math.PI);
	Mat4dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, Y major case )
	a1.set(.1,1,.1,Math.PI);
	Mat4dQuatAxisAngle(a1);
	// Mat <-> Quat <-> Axis (near PI, Z major case )
	a1.set(.1,.1,1,Math.PI);
	Mat4dQuatAxisAngle(a1);

	// isometric view 3 times 2/3 turn
	a1.set(1,1,1,2*Math.PI/3);
	m1.set(a1);
	//System.out.println("m1="+m1);
	p1.set(1,0,0);
	//System.out.println("p1="+p1);
	m1.transform(p1);
	//System.out.println("after transform p1="+p1);
	ASSERT(equals(p1, new Point3d(0,1,0)));
	// System.out.println("14");
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(0,0,1)));
	// System.out.println("15");
	m1.transform(p1);
	ASSERT(equals(p1, new Point3d(1,0,0)));
	// System.out.println("16");

	// check getScale
	m1.set(a1);
	ASSERT(equals(m1.determinant(), 1));
	ASSERT(equals(m1.getScale(), 1));
    //	System.out.println("17");
	m2.set(a1);

	// transpose and inverse
	m1.set(a1);
	m2.invert(m1);
	m1.transpose();
	ASSERT(equals(m1, m2));
    //	System.out.println("18");

	// rot, scale, trans
	Matrix3d n1 = new Matrix3d();
	n1.set(a1);
	Matrix3d n2 = new Matrix3d();
	v1.set(2, -1, -1);
	m1.set(n1, v1, 0.4);
	m2.set(n1, v1, 0.4);
	Vector3d v2 = new Vector3d();
	double s = m1.get(n2, v2);
	ASSERT(equals(n1, n2));
	ASSERT(equals(s, 0.4));
	ASSERT(equals(v1, v2));
	ASSERT(equals(m1, m2)); // not modified

    }
    static public void Matrix4fTest() {
    }
    static public void GMatrixTest() {
	GMatrix I44 = new GMatrix(4,4); // Identity 4x4
	GMatrix O44 = new GMatrix(4,4); O44.setZero(); // O 4x4
	GMatrix O34 = new GMatrix(3,4); O34.setZero(); // O 3x4
	GMatrix m1 = new GMatrix(3,4);
	GMatrix m2 = new GMatrix(3,4);
	Matrix3d mm1 = new Matrix3d();
	Matrix3d mm2 = new Matrix3d();

	// get/setElement
	for (int i = 0; i < 3; i++)
	    for (int j = 0; j < 4; j++) {
		m1.setElement(i,j,(i+1)*(j+2));
		if (j < 3)
		    mm1.setElement(i,j,(i+1)*(j+2));
	    }
	for (int i = 0; i < 3; i++)
	    for (int j = 0; j < 4; j++) {
		ASSERT(equals(m1.getElement(i,j),(i+1)*(j+2)));
	    }

	m1.get(mm2);
	ASSERT(equals(mm1, mm2));

	// mul with I,O
	m2.mul(m1, I44);
	ASSERT(equals(m1, m2));
	m2.mul(m1, O44);
	ASSERT(equals(O34, m2));

	// LUD
	Matrix4d mm3 = new Matrix4d(
	    1, 2, 3, 4,
	    -2, 3, -1, 3,
	    -1, -2, -4, 1,
	    1, 1, -1, -2
	    );
	Matrix4d mm4 = new Matrix4d();
	Matrix4d mm5 = new Matrix4d();
	mm5.set(mm3);

	// setSize, invert
	m1.setSize(4, 4);
	m2.setSize(4, 4);
	m1.set(mm3);
	// System.out.println("m1=" + m1.toString());
	ASSERT(m1.toString().equals("[" + NL +
"  [1.0	2.0	3.0	4.0]" + NL +
"  [-2.0	3.0	-1.0	3.0]" + NL +
"  [-1.0	-2.0	-4.0	1.0]" + NL +
"  [1.0	1.0	-1.0	-2.0] ]"));

	m2.set(m1);
	m1.invert();
	mm3.invert();
	// System.out.println("mm3 = "+mm3.toString());
	// System.out.println("mm5 = "+mm5.toString());
	mm5.mul(mm3);
	// System.out.println("mm5(==I) = "+mm5.toString());
	ASSERT(equals(mm5, new Matrix4d(1,0,0,0,
					0,1,0,0,
					0,0,1,0,
					0,0,0,1)));

	m1.get(mm4);
	// System.out.println("m1 = "+m1.toString());
	// System.out.println("mm3 = "+mm3.toString());
	// System.out.println("mm4 = "+mm4.toString());
	ASSERT(equals(mm3, mm4));
	// System.out.println("m1 = "+m1.toString());
	// System.out.println("m2 = "+m2.toString());
	m1.mul(m2);
	// System.out.println("m1*m2 = "+m1.toString());
	ASSERT(equals(m1, I44));

	// LUD
	Matrix4d mm6 = new Matrix4d(
	    1, 2, 3, 4,
	    -2, 3, -1, 3,
	    -1, -2, -4, 1,
	    1, 1, -1, -2
	    );
	Vector4d vv1 = new Vector4d(1,-1,-1,2);
	Vector4d vv2 = new Vector4d();
	Vector4d vv3 = new Vector4d(4,2,7,-3);
	mm6.transform(vv1, vv2);
	// System.out.println("mm6 = "+mm6.toString());
	// System.out.println("vv1 = "+vv1.toString());
	// System.out.println("vv2 = "+vv2.toString());
	// System.out.println("vv3 = "+vv3.toString());
	ASSERT(equals(vv2, vv3));

	m1.set(mm6);
	GVector x = new GVector(4);
	GVector v2 = new GVector(4);
	GVector b = new GVector(4);
	x.set(vv1); // (1,-1,-1,2)
	b.set(vv3); // (4,2,7,-3)
	GVector mx = new GVector(4);
	mx.mul(m1, x); // M*x = (4,2,7,-3)
	ASSERT(equals(mx, b));

	GVector p = new GVector(4);
	m1.LUD(m2, p);
	ASSERT(checkLUD(m1, m2, p));
	GVector xx = new GVector(4);
	xx.LUDBackSolve(m2, b, p);
	ASSERT(equals(xx, x));
	
	GMatrix u = new GMatrix(m1.getNumRow(), m1.getNumRow());
	GMatrix w = new GMatrix(m1.getNumRow(), m1.getNumCol());
	GMatrix v = new GMatrix(m1.getNumCol(), m1.getNumCol());
	int rank = m1.SVD(u, w, v);
	ASSERT(rank == 4);
	ASSERT(checkSVD(m1, u, w, v));
	xx.SVDBackSolve(u, w, v, b);
	ASSERT(equals(xx, x));

	// overwrite m1 -LUD-> m1
	// m1.LUD(m1, p);
	// xx.LUDBackSolve(m2, b, p);
	// ASSERT(equals(xx, x));
    }

    static boolean checkLUD(GMatrix m, GMatrix LU, GVector permutation) {
	int n = m.getNumCol();
	boolean ok = true;
	for (int i = 0; i < n; i++) {
	    for (int j = 0; j < n; j++) {
		double aij = 0.0;
		int min = i < j ? i : j;
		for (int k = 0; k <= min; k++) {
		    if (i != k)
			aij += LU.getElement(i, k)*LU.getElement(k, j);
		    else
			aij += LU.getElement(k, j);
		}
		if (Math.abs(aij - m.getElement((int)permutation.getElement(i),j)) > epsilon) {
		    System.out.println("a["+i+","+j+"] = "+aij+"(LU)ij ! = "+m.getElement((int)permutation.getElement(i),j));
		    ok = false;
		}
	    }
	}
	return ok;
    }
    
    static boolean checkSVD(GMatrix m, GMatrix u, GMatrix w, GMatrix v) {
	boolean ok = true;
	int wsize = w.getNumRow() < w.getNumRow() ? w.getNumRow() : w.getNumCol();
	
	for (int i = 0; i < m.getNumRow(); i++) {
	    for (int j = 0; j < m.getNumCol(); j++) {
		double sum = 0.0;
		for (int k = 0; k < m.getNumCol(); k++) {
		    sum += u.getElement(i,k)*w.getElement(k,k)*v.getElement(j,k);
		}
		/* check if SVD is OK */
		if (epsilon < Math.abs(m.getElement(i, j)-sum)) {
		    System.out.println("(SVD)ij = "+sum +" != a["+i+","+j+"] = "+m.getElement(i,j));
		    ok = false;
		}
	    }
	    
	}
	if (!ok) {
	    System.out.print("[W] = ");
	    System.out.println(w);
	    System.out.print("[U] = ");
	    System.out.println(u);
	    System.out.print("[V] = ");
	    System.out.println(v);
	}
	return ok;
    }

    static public void SVDTest() {
        double val[] = {1,2,3,4,
                        5,6,7,8,
                        9,0,8,7,
                        6,5,4,3,
                        2,1,0,1};

        int m = 5;
        int n = 4;

        GMatrix matA = new GMatrix(m,n,val);
        GMatrix matU = new GMatrix(m,m);
        GMatrix matW = new GMatrix(m,n);
        GMatrix matV = new GMatrix(n,n);

        //this = U*W*transpose(V)
        int rank = matA.SVD(matU, matW, matV);

        GMatrix matTEMP = new GMatrix(m,n);
        matTEMP.mul(matU, matW);
        matV.transpose();
        matTEMP.mul(matV);

        if (!equals(matTEMP, matA)) {
            System.out.println("matU=" + matU);
            System.out.println("matW=" + matW);
            System.out.println("matV=" + matV);
            System.out.println("matA=" + matA);
            System.out.println("UWV=" + matTEMP);
        }
        ASSERT(equals(matTEMP, matA));
    }
}


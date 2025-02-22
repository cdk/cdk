/*
 * Copyright 2004-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 * Utility vecmath class used when computing the hash code for vecmath
 * objects containing float or double values. This fixes Issue 36.
 */
class VecMathUtil {
/**
 * Do not construct an instance of this class.
 */
private VecMathUtil() {}

	static final long hashLongBits(long hash, long l) {
		hash *= 31L;
		return hash + l;
	}

	static final long hashFloatBits(long hash, float f) {
		hash *= 31L;
		// Treat 0.0d and -0.0d the same (all zero bits)
		if (f == 0.0f)
			return hash;

		return hash + Float.floatToIntBits(f);
	}

	static final long hashDoubleBits(long hash, double d) {
		hash *= 31L;
		// Treat 0.0d and -0.0d the same (all zero bits)
		if (d == 0.0d)
			return hash;

		return hash + Double.doubleToLongBits(d);
	}

	/**
	 * Return an integer hash from a long by mixing it with itself.
	 */
	static final int hashFinish(long hash) {
		return (int)(hash ^ (hash >> 32));
	}
}

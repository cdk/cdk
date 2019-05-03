/*
 * Copyright (c) 2013 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo;


import javax.vecmath.Point3d;

/**
 * Geometric parity for 3D tetrahedral geometry. This class requires four 3D
 * coordinates. The 3D coordinates correspond to the four ligands of a
 * tetrahedral atom. If a tetrahedral atom has an implicit hydrogen (only 3
 * ligands) the forth coordinate should be that of the atom at the centre
 * {@cdk.cite Cieplak2001}.
 *
 * @author John May
 * @cdk.module hash
 * @see <a href="http://www.mdpi.org/molecules/papers/61100915/61100915.htm">Cieplak,
 *      T and Wisniewski, J.L. 2001</a>
 * @cdk.githash
 */
final class Tetrahedral3DParity extends GeometricParity {

    /* array of four 3D coordinates */
    private final Point3d[] coordinates;

    /**
     * Create a new geometric parity for 3D tetrahedral geometry by specifying
     * the coordinates.
     *
     * @param coordinates non-null, 4 3D coordinates
     * @throws IllegalArgumentException if the number of coordinates was not 4
     */
    public Tetrahedral3DParity(Point3d[] coordinates) {

        if (coordinates.length != 4) throw new IllegalArgumentException("4 coordinates expected");

        this.coordinates = coordinates;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int parity() {

        double x1 = coordinates[0].x;
        double x2 = coordinates[1].x;
        double x3 = coordinates[2].x;
        double x4 = coordinates[3].x;

        double y1 = coordinates[0].y;
        double y2 = coordinates[1].y;
        double y3 = coordinates[2].y;
        double y4 = coordinates[3].y;

        double z1 = coordinates[0].z;
        double z2 = coordinates[1].z;
        double z3 = coordinates[2].z;
        double z4 = coordinates[3].z;

        double det = (z1 * det(x2, y2, x3, y3, x4, y4)) - (z2 * det(x1, y1, x3, y3, x4, y4))
                + (z3 * det(x1, y1, x2, y2, x4, y4)) - (z4 * det(x1, y1, x2, y2, x3, y3));

        return (int) Math.signum(det);
    }

    // 3x3 determinant helper for a constant third column
    private static double det(double xa, double ya, double xb, double yb, double xc, double yc) {
        return (xa - xc) * (yb - yc) - (ya - yc) * (xb - xc);
    }
}

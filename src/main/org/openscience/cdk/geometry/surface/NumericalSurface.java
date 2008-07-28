/*
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */

package org.openscience.cdk.geometry.surface;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.PeriodicTable;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A class representing the solvent acessible surface area surface of a molecule.
 *
 * <p>This class is based on the Python implementation of the DCLM method
 * ({@cdk.cite EIS95}) by Peter McCluskey, which is a non-analytical method to generate a set of points
 * representing the solvent accessible surface area of a molecule.
 * 
 * <p>The neighbor list is a simplified version of that
 * described in {@cdk.cite EIS95} and as a result, the surface areas of the atoms may not be exact
 * (compared to analytical calculations). The tessellation is slightly different from
 * that described by McCluskey and uses recursive subdivision starting from an icosahedral
 * representation.
 * 
 * <p>The default solvent radius used is 1.4A and setting this to 0 will give the
 * Van der Waals surface. The accuracy can be increased by increasing the tessellation 
 * level, though the default of 4 is a good balance between accuracy and speed.
 *
 * @author      Rajarshi Guha
 * @cdk.created 2005-05-08  
 * @cdk.module  extra
 * @cdk.svnrev  $Revision$
 * @cdk.bug     1846421
 */
public class NumericalSurface {
    private LoggingTool logger;
    double solvent_radius = 1.4;
    int tesslevel = 4;
    IAtom[] atoms;
    ArrayList[] surf_points;
    double[] areas;
    double[] volumes;

    /**
     * Constructor to initialize the surface calculation with default values.
     *
     * This constructor use the Van der Waals radii as defined in <i>org/openscience/cdk/config/data/jmol_atomtypes.txt</i>
     * of the source distribution. Also uses a tesselation level of 4 and solvent radius of 1.4A.
     *
     * @param atomContainer The {@link IAtomContainer} for which the surface is to be calculated
     */
    public NumericalSurface(IAtomContainer atomContainer) {
        this.atoms = AtomContainerManipulator.getAtomArray(atomContainer);
        logger = new LoggingTool(this);
    }
    /**
     * Constructor to initialize the surface calculation with user specified values.
     *
     * This constructor use the Van der Waals radii as defined in <i>org/openscience/cdk/config/data/jmol_atomtypes.txt</i>
     * of the source distribution
     *
     * @param atomContainer The {@link IAtomContainer} for which the surface is to be calculated
     * @param solvent_radius The radius of a solvent molecule that is used to extend
     * the radius of each atom. Setting to 0 gives the Van der Waals surface
     * @param tesslevel The number of levels that the subdivision algorithm for tessllation
     * should use
     */
    public NumericalSurface(IAtomContainer atomContainer, double solvent_radius, int tesslevel) {
        this.solvent_radius = solvent_radius;
        this.atoms = AtomContainerManipulator.getAtomArray(atomContainer);
        this.tesslevel = tesslevel;
        logger = new LoggingTool(this);
    }

    /**
     * Evaluate the surface.
     *
     * This method generates the points on the accessible surface area of each atom
     * as well as calculating the surface area of each atom
     */
    public void calculateSurface() {

        // get r_f and geometric center
        Point3d cp = new Point3d(0,0,0);
        double max_radius = 0;
        for (int i = 0; i < atoms.length; i++) {
            double vdwr = PeriodicTable.getVdwRadius(atoms[i].getSymbol());
            if (vdwr+solvent_radius > max_radius)
                max_radius = PeriodicTable.getVdwRadius(atoms[i].getSymbol())+solvent_radius;

            cp.x = cp.x + atoms[i].getPoint3d().x;
            cp.y = cp.y + atoms[i].getPoint3d().y;
            cp.z = cp.z + atoms[i].getPoint3d().z;
        }
        cp.x = cp.x / atoms.length;
        cp.y = cp.y / atoms.length;
        cp.z = cp.z / atoms.length;


        // do the tesselation
        Tessellate tess = new Tessellate("ico",tesslevel);
        tess.doTessellate();
        logger.info("Got tesselation, number of triangles = "+tess.getNumberOfTriangles());


        // get neighbor list
        NeighborList nbrlist = new NeighborList(atoms, max_radius+solvent_radius);
        logger.info("Got neighbor list");

        /*
           for (int i = 0; i < atoms.length; i++) {
           int[] nlist = nbrlist.getNeighbors(i);
           logger.debug("Atom "+i+": ");
           for (int j = 0; j < nlist.length; j++)
           logger.debug(j+" ");
           logger.debug("");
           }
           */

        // loop over atoms and get surface points
        this.surf_points = new ArrayList[ atoms.length ];
        this.areas = new double[ atoms.length ];
        this.volumes = new double[ atoms.length ];

        for (int i = 0; i < atoms.length; i++) {
            int point_density = tess.getNumberOfTriangles()*3;
            Point3d[][] points = atomicSurfacePoints(nbrlist, i, atoms[i], tess);
            translatePoints(i, points, point_density, atoms[i], cp);
        }
        logger.info("Obtained points, areas and volumes");

    }

    /**
     * Get an array of all the points on the molecular surface.
     *
     * This returns an array of Point3d objects representing all the points
     * on the molecular surface
     *
     * @return  An array of Point3d objects
     */
    public Point3d[] getAllSurfacePoints() {
        int npt = 0;
        for (int i = 0; i < this.surf_points.length; i++)
            npt += this.surf_points[i].size();
        Point3d[] ret = new Point3d[npt];
        int j = 0;
        for (int i = 0; i < this.surf_points.length; i++) {
            ArrayList arl = this.surf_points[i];
            for (Iterator it = arl.iterator(); it.hasNext();) {
                ret[j] = (Point3d)it.next();
                j++;
            }
        }
        return(ret);
    }

    /**
     * Get an array of the points on the accessible surface of a specific atom.
     *
     * @param atomIdx The index of the atom. Ranges from 0 to n-1, where n is the 
     * number of atoms in the AtomContainer that the surface was calculated for
     * @return  An array of Point3d objects 
     * @throws CDKException if the atom index is outside the range of allowable indices
     */
    public Point3d[] getSurfacePoints(int atomIdx) throws CDKException {
        if (atomIdx >= this.surf_points.length) {
            throw new CDKException("Atom index was out of bounds");
        }
        ArrayList arl = this.surf_points[atomIdx];
        Point3d[] ret = new Point3d[arl.size()];
        for (int i = 0; i < arl.size(); i++) ret[i] = (Point3d)arl.get(i);
        return(ret);
    }
    /**
     * Get the surface area for the specified atom.
     *
     * @param atomIdx The index of the atom. Ranges from 0 to n-1, where n is the 
     * number of atoms in the AtomContainer that the surface was calculated for
     * @return A double representing the accessible surface area of the atom
     * @throws CDKException if the atom index is outside the range of allowable indices
     */
    public double getSurfaceArea(int atomIdx) throws CDKException {
        if (atomIdx >= this.surf_points.length) {
            throw new CDKException("Atom index was out of bounds");
        }
        return(this.areas[atomIdx]);
    }
    /**
     * Get an array containing the accessible surface area for each atom.
     *
     * @return An array of double giving the surface areas of all the atoms
     */
    public double[] getAllSurfaceAreas() {
        return(this.areas);
    }

    /**
     * Get the total surface area for the AtomContainer.
     *
     * @return A double containing the total surface area of the AtomContainer for
     * which the surface was calculated for
     */
    public  double getTotalSurfaceArea() {
        double ta = 0.0;
        for (int i =0; i < this.areas.length; i++) ta += this.areas[i];
        return(ta);
    }


    private void translatePoints(int atmIdx, Point3d[][] points, int point_density, IAtom atom, Point3d cp) {
        double total_radius = PeriodicTable.getVdwRadius(atom.getSymbol()) + solvent_radius;

        double area = 4 * Math.PI * (total_radius*total_radius) * points.length / point_density;

        double sumx = 0.0;
        double sumy = 0.0;
        double sumz = 0.0;
        for (int i = 0; i < points.length; i++) {
            Point3d p = points[i][1];
            sumx += p.x;
            sumy += p.y;
            sumz += p.z;
        }
        double vconst = 4.0/3.0 * Math.PI / (double)point_density;
        double dotp1 = (atom.getPoint3d().x - cp.x)*sumx +
            (atom.getPoint3d().y - cp.y)*sumy +
            (atom.getPoint3d().z - cp.z)*sumz;
        double volume = vconst*(total_radius*total_radius) *dotp1 +
            (total_radius*total_radius*total_radius)*points.length;

        this.areas[atmIdx] = area;
        this.volumes[atmIdx] = volume;

        ArrayList tmp = new ArrayList();
        for (int i = 0; i < points.length; i++) tmp.add( points[i][0] );
        this.surf_points[atmIdx] =  tmp;
    }

    private Point3d[][] atomicSurfacePoints(NeighborList nbrlist, int currAtomIdx, IAtom atom, Tessellate tess) {

        double total_radius = PeriodicTable.getVdwRadius(atom.getSymbol()) + solvent_radius;
        double total_radius2 = total_radius*total_radius;
        double twice_total_radius = 2*total_radius;

        int[] nlist = nbrlist.getNeighbors(currAtomIdx);
        double[][] data = new double[ nlist.length ][4];
        for (int i = 0; i < nlist.length; i++) {
            double x12 = atoms[nlist[i]].getPoint3d().x - atom.getPoint3d().x;
            double y12 = atoms[nlist[i]].getPoint3d().y - atom.getPoint3d().y;
            double z12 = atoms[nlist[i]].getPoint3d().z - atom.getPoint3d().z;

            double d2 = x12*x12 + y12*y12 + z12*z12;
            double tmp = PeriodicTable.getVdwRadius(atoms[nlist[i]].getSymbol()) + solvent_radius;
            tmp = tmp * tmp;
            double thresh = (d2 + total_radius2 - tmp) / twice_total_radius;

            data[i][0] = x12;
            data[i][1] = y12;
            data[i][2] = z12;
            data[i][3] = thresh;
        }

        Point3d[] tess_points = tess.getTessAsPoint3ds();
        ArrayList points = new ArrayList();
        for (int i = 0; i < tess_points.length; i++) {
            Point3d pt = tess_points[i];
            boolean buried = false;
            for (int j = 0; j < data.length; j++) {
                if (data[j][0] * pt.x + data[j][1] * pt.y + data[j][2] * pt.z > data[j][3]) {
                    buried = true;
                    break;
                }
            }
            if (buried == false) {
                Point3d[] tmp = new Point3d[2];
                tmp[0] =  new Point3d(
                        total_radius * pt.x + atom.getPoint3d().x,
                        total_radius * pt.y + atom.getPoint3d().y,
                        total_radius * pt.z + atom.getPoint3d().z
                        );
                tmp[1] = pt;
                points.add( tmp );
            }
        }

        // the first column contains the transformed points
        // and the second column contains the points from the
        // original unit tesselation
        Point3d[][] ret = new Point3d[ points.size() ][2];
        for (int i = 0; i < points.size(); i++) {
            Point3d[] tmp = (Point3d[])points.get(i);
            ret[i][0] = tmp[0];
            ret[i][1] = tmp[1];
        }
        return(ret);
    }
}




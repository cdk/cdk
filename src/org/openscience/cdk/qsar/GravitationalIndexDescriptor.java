/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Element;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.AtomEnumeration;

import java.lang.Math;
import java.util.Vector;
import java.util.ArrayList;


/**
 * Descriptor characterizing the mass distribution of the molecule. 
 * Described by Katritzky et al. {@cdk.cite KAT96}.
 * For modelling purposes the value of the descriptor is calculated
 * both with and without H atoms. Furthermore the square and cube roots 
 * of the descriptor are also generated as described by Wessel et al. {@cdk.cite WES98}.
 *<p>
 * The descriptor routine generates 9 descriptors:
 * <ul>
 * <li>grav1 -  gravitational index of heavy atoms
 * <li>grav2 -  square root of gravitational index of heavy atoms
 * <li>grav3 -  cube root of gravitational index of heavy atoms
 * <li>gravh1 -  gravitational index - hydrogens included
 * <li>gravh2 -  square root of hydrogen-included gravitational index
 * <li>gravh3 -  cube root of hydrogen-included gravitational index
 * <li>grav4 -  grav1 for all pairs of atoms (not just bonded pairs)
 * <li>grav5 -  grav2 for all pairs of atoms (not just bonded pairs)
 * <li>grav6 -  grav3 for all pairs of atoms (not just bonded pairs)
 * </ul>
 *
 * @author      Rajarshi Guha
 * @created     2004-11-23
 */
public class GravitationalIndexDescriptor implements Descriptor {
    
    private class pair {
        int x,y;
        public  pair() { 
            x = 0;
            y = 0;
        }
    }

    public GravitationalIndexDescriptor() {}

    /**
     *  Sets the parameters attribute of the GravitationalIndexDescriptor object
     *
     *@param  params            The new parameters value
     *@exception  CDKException  Description of the Exception
     */
    public void setParameters(Object[] params) throws CDKException {
        // no parameters for this descriptor
    }

    /**
     *  Gets the parameters attribute of the GravitationalIndexDescriptor object
     *
     *@return    The parameters value
     */
    public Object[] getParameters() {
        // no parameters to return
        return(null);
    }
    /**
     *  Gets the parameterNames attribute of the GravitationalIndexDescriptor object
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        // no param names to return
        return(null);
    }


    /**
     *  Gets the parameterType attribute of the GravitationalIndexDescriptor object
     *
     *@param  name  Description of the Parameter
     *@return       The parameterType value
     */
    public Object getParameterType(String name) {
         return (null);
    }

    /**
     *  Calculates the 9 gravitational indices
     *
     *@param  container  Parameter is the atom container.
     *@return            An ArrayList containing 9 elements in the order described above
     */

    public Object calculate(AtomContainer container) {
        IsotopeFactory factory = null;
        double m1 = 0;
        double m2 = 0;
        try {
            factory = IsotopeFactory.getInstance();
        } catch (Exception e) {
            System.out.println(e);
        }

        double sum = 0;
        for (int i = 0; i < container.getBondCount(); i++) {
            Bond b = container.getBondAt(i);

            if (b.getAtomCount() != 2) {
                System.out.println("GravitationalIndex: Only handles 2 center bonds");
                return(null);
            }

            m1 = factory.getMajorIsotope( b.getAtomAt(0).getSymbol() ).getMassNumber();
            m2 = factory.getMajorIsotope( b.getAtomAt(1).getSymbol() ).getMassNumber();

            double x1 = b.getAtomAt(0).getX3d();
            double y1 = b.getAtomAt(0).getY3d();
            double z1 = b.getAtomAt(0).getZ3d();
            double x2 = b.getAtomAt(1).getX3d();
            double y2 = b.getAtomAt(1).getY3d();
            double z2 = b.getAtomAt(1).getZ3d();

            double dist = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2);
            sum += (m1*m2) / dist;
        }

        // heavy atoms only
        double heavysum = 0;
        for (int i = 0; i < container.getBondCount(); i++) {
            Bond b = container.getBondAt(i);

            if (b.getAtomCount() != 2) {
                System.out.println("GravitationalIndex: Only handles 2 center bonds");
                return(null);
            }

            if (b.getAtomAt(0).getSymbol().equals("H") || 
                    b.getAtomAt(1).getSymbol().equals("H")) continue;



            m1 = factory.getMajorIsotope( b.getAtomAt(0).getSymbol() ).getMassNumber();
            m2 = factory.getMajorIsotope( b.getAtomAt(1).getSymbol() ).getMassNumber();

            double x1 = b.getAtomAt(0).getX3d();
            double y1 = b.getAtomAt(0).getY3d();
            double z1 = b.getAtomAt(0).getZ3d();
            double x2 = b.getAtomAt(1).getX3d();
            double y2 = b.getAtomAt(1).getY3d();
            double z2 = b.getAtomAt(1).getZ3d();

            double dist = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2);
            heavysum += (m1*m2) / dist;
        }

        // all pairs
        Vector x = new Vector();
        for (int i = 0; i < container.getAtomCount(); i++) {
            if (container.getAtomAt(i).getSymbol().equals("H")) continue;
            else x.add( new Integer(i) );
        }
        int npair = x.size() * (x.size()-1) / 2;
        pair[] p = new pair[npair];
        for (int i = 0; i < npair; i++) p[i] = new pair();
        int pcount = 0;
        for (int i = 0; i < x.size()-1; i++) {
            for (int j =  i+1; j < x.size(); j++) {
                int present = 0;
                Integer ia = (Integer)x.get(i);
                Integer ib = (Integer)x.get(j);
                int a = ia.intValue();
                int b = ib.intValue();
                for (int k = 0; k < pcount; k++) {
                    if ( (p[k].x == a && p[k].y == b) ||
                            (p[k].y == a && p[k].x == b) ) present = 1;
                }
                if (present == 1) continue;
                p[pcount].x = a;
                p[pcount].y = b;
                pcount += 1;
            }
        }
        double allheavysum = 0;
        for (int i = 0; i < p.length; i++) {
            int a1 = p[i].x;
            int a2 = p[i].y;

            m1 = factory.getMajorIsotope( container.getAtomAt(a1).getSymbol() ).getMassNumber();
            m2 = factory.getMajorIsotope( container.getAtomAt(a2).getSymbol() ).getMassNumber();

            double x1 = container.getAtomAt(a1).getX3d();
            double y1 = container.getAtomAt(a1).getY3d();
            double z1 = container.getAtomAt(a1).getZ3d();
            double x2 = container.getAtomAt(a2).getX3d();
            double y2 = container.getAtomAt(a2).getY3d();
            double z2 = container.getAtomAt(a2).getZ3d();

            double dist = (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) + (z1-z2)*(z1-z2);
            allheavysum += (m1*m2) / dist;
        }


        ArrayList retval = new ArrayList(9);
        retval.add( new Double(heavysum) );
        retval.add( new Double(Math.sqrt(heavysum)) );
        retval.add( new Double(Math.pow(heavysum,1.0/3.0)) );
        
        retval.add( new Double(sum) );
        retval.add( new Double(Math.sqrt(sum)) );
        retval.add( new Double(Math.pow(sum,1.0/3.0)) );

        retval.add( new Double(allheavysum) );
        retval.add( new Double(Math.sqrt(allheavysum)) );
        retval.add( new Double(Math.pow(allheavysum,1.0/3.0)) );

        return(retval);
    }
}
    


/* Copyright (C) 2002-2007  Stephane Werner <mail@ixelis.net>
 *               2007-2009  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * This code has been kindly provided by Stephane Werner
 * and Thierry Hanser from IXELIS mail@ixelis.net.
 *
 * IXELIS sarl - Semantic Information Systems
 *               17 rue des C?dres 67200 Strasbourg, France
 *               Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 *
 * CDK Contact: cdk-devel@lists.sf.net
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
package org.openscience.cdk.smsd.algorithm.rgraph;


/**
 *  An CDKRMap implements the association between an edge (bond) in G1 and an edge
 *  (bond) in G2, G1 and G2 being the compared graphs in a RGraph context.
 *
 * @author Stephane Werner, IXELIS &lt;mail@ixelis.net&gt;,
 *              Syed Asad Rahman &gt;asad@ebi.ac.uk&lt; (modified the orignal code)
 * @cdk.created 2002-07-24
 * @cdk.module  smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class CDKRMap {

    private int id1 = 0;
    private int id2 = 0;

    /**
     *  Constructor for the CDKRMap
     *
     * @param  id1  number of the edge (bond) in the graphe 1
     * @param  id2  number of the edge (bond) in the graphe 2
     */
    public CDKRMap(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    /**
     *  Sets the id1 attribute of the CDKRMap object
     *
     * @param  id1  The new id1 value
     */
    public void setId1(int id1) {
        this.id1 = id1;
    }

    /**
     *  Sets the id2 attribute of the CDKRMap object
     *
     * @param  id2  The new id2 value
     */
    public void setId2(int id2) {
        this.id2 = id2;
    }

    /**
     *  Gets the id1 attribute of the CDKRMap object
     *
     * @return    The id1 value
     */
    public int getId1() {
        return id1;
    }

    /**
     *  Gets the id2 attribute of the CDKRMap object
     *
     * @return    The id2 value
     */
    public int getId2() {
        return id2;
    }

    /**
     *  The equals method.
     *
     * @param  obj  The object to compare.
     * @return    true=if both ids equal, else false.
     */
    @Override
    public boolean equals(Object obj) {
        if (((CDKRMap) obj).getId1() == getId1() && ((CDKRMap) obj).getId2() == getId2()) {
            return (true);
        } else {
            return (false);
        }
    }

    /**
    * Returns a hash code for object comparison.
    * @return    Returns a hash code for object comparison.
    */

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.getId1();
        hash = 79 * hash + this.getId2();
        return hash;
    }
}

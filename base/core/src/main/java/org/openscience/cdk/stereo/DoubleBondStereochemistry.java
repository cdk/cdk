/* Copyright (C) 2012  Egon Willighagen <egonw@users.sf.net>
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.List;
import java.util.Map;

/**
 * Stereochemistry specification for double bonds. See {@link IDoubleBondStereochemistry} for
 * further details.
 *
 * @cdk.module core
 * @cdk.githash
 *
 * @see org.openscience.cdk.interfaces.IDoubleBondStereochemistry
 */
public class DoubleBondStereochemistry
    extends AbstractStereo<IBond,IBond>
    implements IDoubleBondStereochemistry {

    /**
     * Creates a new double bond stereo chemistry. The path of length three is defined by
     * <code>ligandBonds[0]</code>, <code>stereoBonds</code>, and <code>ligandBonds[1]</code>.
     */
    public DoubleBondStereochemistry(IBond stereoBond, IBond[] ligandBonds, Conformation stereo) {
        this(stereoBond, ligandBonds, Conformation.toConfig(stereo));
    }

    public DoubleBondStereochemistry(IBond stereoBond, IBond[] ligandBonds, int config) {
        super(stereoBond, ligandBonds, CT | (CFG_MASK & config));
    }

    public void setBuilder(IChemObjectBuilder builder) {
        super.setBuilder(builder);
    }

    /** {@inheritDoc} */
    @Override
    public IBond[] getBonds() {
        return getCarriers().toArray(new IBond[0]);
    }

    /** {@inheritDoc} */
    @Override
    public IBond getStereoBond() {
        return getFocus();
    }

    /** {@inheritDoc} */
    @Override
    public Conformation getStereo() {
        return Conformation.toConformation(getConfigOrder());
    }

    @Override
    public IDoubleBondStereochemistry map(Map<IAtom, IAtom> atoms,
                                            Map<IBond, IBond> bonds) {
        return (IDoubleBondStereochemistry) super.map(atoms, bonds);
    }

    @Override
    protected IStereoElement<IBond, IBond> create(IBond focus, List<IBond> carriers,
                                                  int cfg) {
        return new DoubleBondStereochemistry(focus, carriers.toArray(new IBond[2]), cfg);
    }
}

/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;

import java.util.List;
import java.util.Map;

/**
 * Stereochemistry specification for tetravalent atoms. See {@link ITetrahedralChirality} for
 * further details.
 *
 * @cdk.module core
 * @cdk.githash
 * @see org.openscience.cdk.interfaces.ITetrahedralChirality
 */
public class TetrahedralChirality
    extends AbstractStereo<IAtom, IAtom>
    implements ITetrahedralChirality {

    public TetrahedralChirality(IAtom chiralAtom,
                                IAtom[] ligands,
                                Stereo stereo) {
        this(chiralAtom, ligands, Stereo.toConfig(stereo));
    }

    public TetrahedralChirality(IAtom chiralAtom,
                                IAtom[] ligands,
                                int config) {
        super(chiralAtom, ligands, TH | (CFG_MASK & config));
    }

    @Override
    public IAtom[] getLigands() {
        return getCarriers().toArray(new IAtom[4]);
    }

    @Override
    public IAtom getChiralAtom() {
        return getFocus();
    }

    @Override
    public Stereo getStereo() {
        return Stereo.toStereo(getConfigOrder());
    }

    @Override
    public void setStereo(Stereo stereo) {
        setConfigOrder(Stereo.toConfig(stereo));
    }

    @Override
    protected IStereoElement<IAtom, IAtom> create(IAtom focus, List<IAtom> carriers, int config) {
        return new TetrahedralChirality(focus, carriers.toArray(new IAtom[4]), config);
    }

    @Override
    public ITetrahedralChirality map(Map<IAtom, IAtom> atoms,
                                     Map<IBond, IBond> bonds) {
        return (ITetrahedralChirality) super.map(atoms, bonds);
    }

    @Override
    public ITetrahedralChirality map(Map<IChemObject, IChemObject> chemobjs) {
        return (ITetrahedralChirality) super.map(chemobjs);
    }

    @Override
    public void setBuilder(IChemObjectBuilder builder) {
        super.setBuilder(builder);
    }

    /**
     * Returns a {@link String} representation of this chiral element.
     *
     * @return the String representation
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tetrahedral{").append(this.hashCode()).append(", ");
        builder.append(this.getStereo()).append(", ");
        builder.append("c:").append(this.getChiralAtom()).append(", ");
        IAtom[] ligands = this.getLigands();
        for (int i = 0; i < ligands.length; i++) {
            builder.append(i + 1).append(':').append(ligands[i]).append(", ");
        }
        builder.append('}');
        return builder.toString();
    }
}

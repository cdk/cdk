/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *               2012  John May <jwmay@users.sf.net>
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
package org.openscience.cdk;

import org.openscience.cdk.formula.AdductFormula;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.formula.MolecularFormulaSet;
import org.openscience.cdk.interfaces.IAdductFormula;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.ICDKObject;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IElectronContainer;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.ILonePair;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecularFormulaSet;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.interfaces.IPolymer;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionScheme;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStrand;
import org.openscience.cdk.interfaces.ISubstance;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.protein.data.PDBAtom;
import org.openscience.cdk.protein.data.PDBMonomer;
import org.openscience.cdk.protein.data.PDBPolymer;
import org.openscience.cdk.protein.data.PDBStructure;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * A factory class to provide implementation independent {@link ICDKObject}s.
 * 
 * <pre>{@code
 *     IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
 *
 *     IAtom a = builder.newInstance(IAtom.class);
 *     IAtom c12 = builder.newInstance(IAtom.class, "C");
 *     IAtom c13 = builder.newInstance(IAtom.class,
 *                                     builder.newInstance(IIsotope.class,
 *                                                         "C", 13));
 * }</pre>
 *
 * @author        egonw
 * @author        john may
 * @cdk.module    data
 * @cdk.githash
 */
public class DefaultChemObjectBuilder implements IChemObjectBuilder {

    private static volatile IChemObjectBuilder instance = null;
    private static final Object                LOCK     = new Object();
    private final DynamicFactory               factory  = new DynamicFactory(200);

    private DefaultChemObjectBuilder() {

        // self reference required for stereo-elements
        final IChemObjectBuilder self = this;

        // elements
        factory.register(IAtom.class, Atom.class);
        factory.register(IPseudoAtom.class, PseudoAtom.class);
        factory.register(IElement.class, Element.class);
        factory.register(IAtomType.class, AtomType.class);
        factory.register(IFragmentAtom.class, FragmentAtom.class);
        factory.register(IPDBAtom.class, PDBAtom.class);
        factory.register(IIsotope.class, Isotope.class);

        // electron containers
        factory.register(IBond.class, Bond.class);
        factory.register(IElectronContainer.class, ElectronContainer.class);
        factory.register(ISingleElectron.class, SingleElectron.class);
        factory.register(ILonePair.class, LonePair.class);

        // atom containers
        factory.register(IAtomContainer.class, AtomContainer.class);
        factory.register(IRing.class, Ring.class);
        factory.register(ICrystal.class, Crystal.class);
        factory.register(IPolymer.class, Polymer.class);
        factory.register(IPDBPolymer.class, PDBPolymer.class);
        factory.register(IMonomer.class, Monomer.class);
        factory.register(IPDBMonomer.class, PDBMonomer.class);
        factory.register(IBioPolymer.class, BioPolymer.class);
        factory.register(IPDBStructure.class, PDBStructure.class);
        factory.register(IAminoAcid.class, AminoAcid.class);
        factory.register(IStrand.class, Strand.class);

        // reactions
        factory.register(IReaction.class, Reaction.class);
        factory.register(IReactionScheme.class, ReactionScheme.class);

        // formula
        factory.register(IMolecularFormula.class, MolecularFormula.class);
        factory.register(IAdductFormula.class, AdductFormula.class);

        // chem object sets
        factory.register(IAtomContainerSet.class, AtomContainerSet.class);
        factory.register(IMolecularFormulaSet.class, MolecularFormulaSet.class);
        factory.register(IReactionSet.class, ReactionSet.class);
        factory.register(IRingSet.class, RingSet.class);
        factory.register(IChemModel.class, ChemModel.class);
        factory.register(IChemFile.class, ChemFile.class);
        factory.register(IChemSequence.class, ChemSequence.class);
        factory.register(ISubstance.class, Substance.class);

        // stereo components (requires some modification after instantiation)
        factory.register(ITetrahedralChirality.class, TetrahedralChirality.class,
                new DynamicFactory.CreationModifier<TetrahedralChirality>() {

                    @Override
                    public void modify(TetrahedralChirality instance) {
                        instance.setBuilder(self);
                    }
                });
        factory.register(IDoubleBondStereochemistry.class, DoubleBondStereochemistry.class,
                new DynamicFactory.CreationModifier<DoubleBondStereochemistry>() {

                    @Override
                    public void modify(DoubleBondStereochemistry instance) {
                        instance.setBuilder(self);
                    }
                });

        // miscellaneous
        factory.register(IMapping.class, Mapping.class);
        factory.register(IChemObject.class, ChemObject.class);

    }

    /**
     * Access the singleton instance of this DefaultChemObjectBuilder. 
     * <pre>{@code
     *
     * // get the builder instance
     * IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
     *
     * // using the builder...
     * // create an IAtom using the default constructor
     * IAtom atom = builder.newInstance(IAtom.class);
     *
     * // create a carbon atom
     * IAtom c1 = builder.newInstance(IAtom.class, "C");
     * }</pre>
     *
     * @return a DefaultChemObjectBuilder instance
     */
    public static IChemObjectBuilder getInstance() {
        IChemObjectBuilder result = instance;
        if (result == null) {
            result = instance;
            synchronized (LOCK) {
                if (result == null) {
                    instance = result = new DefaultChemObjectBuilder();
                }
            }
        }
        return result;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public <T extends ICDKObject> T newInstance(Class<T> clazz, Object... params) {
        return factory.ofClass(clazz, params);
    }

}

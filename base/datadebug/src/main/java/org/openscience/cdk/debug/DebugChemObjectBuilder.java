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
package org.openscience.cdk.debug;

import org.openscience.cdk.DynamicFactory;
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
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * A helper class to instantiate a {@link IChemObject} for the original CDK
 * implementation. The factory create debug objects which will log their
 * behaviour when used.
 *
 * 
 * <pre>{@code
 *     IChemObjectBuilder builder = DebugChemObjectBuilder.getInstance();
 *
 *     IAtom a = builder.newInstance(IAtom.class);
 *     IAtom c12 = builder.newInstance(IAtom.class, "C");
 *     IAtom c13 = builder.newInstance(IAtom.class,
 *                                     builder.newInstance(IIsotope.class,
 *                                                         "C", 13));
 * }</pre>
 *
 *
 * @author        egonw
 * @author        john may
 * @cdk.module    datadebug
 * @cdk.githash
 */
public class DebugChemObjectBuilder implements IChemObjectBuilder {

    private static volatile IChemObjectBuilder instance = null;
    private static final Object                LOCK     = new Object();
    private final DynamicFactory               factory  = new DynamicFactory(200);

    private DebugChemObjectBuilder() {

        // self reference required for stereo-elements
        final IChemObjectBuilder self = this;

        // elements
        factory.register(IAtom.class, DebugAtom.class);
        factory.register(IPseudoAtom.class, DebugPseudoAtom.class);
        factory.register(IElement.class, DebugElement.class);
        factory.register(IAtomType.class, DebugAtomType.class);
        factory.register(IFragmentAtom.class, DebugFragmentAtom.class);
        factory.register(IPDBAtom.class, DebugPDBAtom.class);
        factory.register(IIsotope.class, DebugIsotope.class);

        // electron containers
        factory.register(IBond.class, DebugBond.class);
        factory.register(IElectronContainer.class, DebugElectronContainer.class);
        factory.register(ISingleElectron.class, DebugSingleElectron.class);
        factory.register(ILonePair.class, DebugLonePair.class);

        // atom containers
        factory.register(IAtomContainer.class, DebugAtomContainer.class);
        factory.register(IRing.class, DebugRing.class);
        factory.register(ICrystal.class, DebugCrystal.class);
        factory.register(IPolymer.class, DebugPolymer.class);
        factory.register(IPDBPolymer.class, DebugPDBPolymer.class);
        factory.register(IMonomer.class, DebugMonomer.class);
        factory.register(IPDBMonomer.class, DebugPDBMonomer.class);
        factory.register(IBioPolymer.class, DebugBioPolymer.class);
        factory.register(IPDBStructure.class, DebugPDBStructure.class);
        factory.register(IAminoAcid.class, DebugAminoAcid.class);
        factory.register(IStrand.class, DebugStrand.class);

        // reactions
        factory.register(IReaction.class, DebugReaction.class);
        factory.register(IReactionScheme.class, DebugReactionScheme.class);

        // formula
        factory.register(IMolecularFormula.class, DebugMolecularFormula.class);
        factory.register(IAdductFormula.class, DebugAdductFormula.class);

        // chem object sets
        factory.register(IAtomContainerSet.class, DebugAtomContainerSet.class);
        factory.register(IMolecularFormulaSet.class, DebugMolecularFormulaSet.class);
        factory.register(IReactionSet.class, DebugReactionSet.class);
        factory.register(IRingSet.class, DebugRingSet.class);
        factory.register(IChemModel.class, DebugChemModel.class);
        factory.register(IChemFile.class, DebugChemFile.class);
        factory.register(IChemSequence.class, DebugChemSequence.class);
        factory.register(ISubstance.class, DebugSubstance.class);

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
        factory.register(IMapping.class, DebugMapping.class);
        factory.register(IChemObject.class, DebugChemObject.class);

    }

    /**
     * Access the singleton instance of this DebugChemObjectBuilder. 
     * <pre>{@code
     *
     * // get the builder instance
     * IChemObjectBuilder builder = DebugChemObjectBuilder.getInstance();
     *
     * // using the builder...
     * // create an IAtom using the default constructor
     * IAtom atom = builder.newInstance(IAtom.class);
     *
     * // create a carbon atom
     * IAtom c1 = builder.newInstance(IAtom.class, "C");
     * }</pre>
     *
     * @return a DebugChemObjectBuilder instance
     */
    public static IChemObjectBuilder getInstance() {
        IChemObjectBuilder result = instance;
        if (result == null) {
            result = instance;
            synchronized (LOCK) {
                if (result == null) {
                    instance = result = new DebugChemObjectBuilder();
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

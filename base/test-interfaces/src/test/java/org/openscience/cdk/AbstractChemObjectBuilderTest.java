/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAdductFormula;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.ICrystal;
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
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Checks the functionality of {@link IChemObjectBuilder} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractChemObjectBuilderTest extends CDKTestCase {

    private static IChemObject rootObject;

    public static IChemObject getRootObject() {
        return rootObject;
    }

    public static void setRootObject(IChemObject rootObject) {
        AbstractChemObjectBuilderTest.rootObject = rootObject;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewInstance_Class_arrayObject() throws Exception {
        // throw random stuff; it should fail
        IChemObjectBuilder builder = rootObject.getBuilder();
        builder.newInstance(IAtom.class, new Object[2]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncorrectNumberOf() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        builder.newInstance(IAtom.class, builder.newInstance(IAtomContainer.class));
    }

    @Test
    public void testNewAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class);
        Assert.assertNotNull(atom);
        Assert.assertNull(atom.getSymbol());
    }

    @Test
    public void testNewAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class, builder.newInstance(IElement.class, "N"));
        Assert.assertNotNull(atom);
        Assert.assertEquals("N", atom.getSymbol());
    }

    @Test
    public void testNewAminoAcid() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAminoAcid aa = builder.newInstance(IAminoAcid.class);
        Assert.assertNotNull(aa);
    }

    @Test
    public void testNewAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class, "C");
        Assert.assertNotNull(atom);
        Assert.assertEquals("C", atom.getSymbol());
    }

    @Test
    public void testNewAtom_String_Point2d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Point2d coord = new Point2d(1, 2);
        IAtom atom = builder.newInstance(IAtom.class, "C", coord);
        Assert.assertNotNull(atom);
        Assert.assertEquals("C", atom.getSymbol());
        assertEquals(coord, atom.getPoint2d(), 0.0);
    }

    @Test
    public void testNewAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Point3d coord = new Point3d(1, 2, 3);
        IAtom atom = builder.newInstance(IAtom.class, "C", coord);
        Assert.assertNotNull(atom);
        Assert.assertEquals("C", atom.getSymbol());
        assertEquals(coord, atom.getPoint3d(), 0.0);
    }

    @Test
    public void testNewAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        Assert.assertNotNull(container);
    }

    @Test
    public void testNewAtomContainer_int_int_int_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class, 1, 2, 3, 4);
        Assert.assertNotNull(container);
    }

    @Test
    public void testNewAtomContainer_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        Assert.assertNotNull(container);
        IAtomContainer second = builder.newInstance(IAtomContainer.class, container);
        Assert.assertNotNull(second);
    }

    @Test
    public void testNewAtomType_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, "C");
        Assert.assertNotNull(type);
    }

    @Test
    public void testNewAtomType_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, builder.newInstance(IElement.class, "C"));
        Assert.assertNotNull(type);
    }

    @Test
    public void testNewAtomType_String_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, "C", "C.sp2");
        Assert.assertNotNull(type);
    }

    @Test
    public void testNewBioPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBioPolymer polymer = builder.newInstance(IBioPolymer.class);
        Assert.assertNotNull(polymer);
    }

    @Test
    public void testNewBond() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class);
        Assert.assertNotNull(bond);
    }

    @Test
    public void testNewBond_IAtom_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class));
        Assert.assertNotNull(bond);
    }

    /**
     * @cdk.bug 3526870
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNewBond_IAtom_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        builder.newInstance(IBond.class, builder.newInstance(IAtom.class), builder.newInstance(IAtomContainer.class));
    }

    @Test
    public void testNewBond_IAtom_IAtom_IBond_Order() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class), IBond.Order.SINGLE);
        Assert.assertNotNull(bond);
    }

    @Test
    public void testNewBond_IAtom_IAtom_IBond_Order_IBond_Stereo() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class), IBond.Order.SINGLE, IBond.Stereo.E_OR_Z);
        Assert.assertNotNull(bond);
    }

    @Test
    public void testNewBond_arrayIAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class,
                (Object[]) new IAtom[]{builder.newInstance(IAtom.class), builder.newInstance(IAtom.class)});
        Assert.assertNotNull(bond);
    }

    @Test
    public void testNewBond_arrayIAtom_IBond_Order() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class,
                (Object[]) new IAtom[]{builder.newInstance(IAtom.class), builder.newInstance(IAtom.class)},
                IBond.Order.DOUBLE);
        Assert.assertNotNull(bond);
    }

    @Test
    public void testNewChemFile() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemFile file = builder.newInstance(IChemFile.class);
        Assert.assertNotNull(file);
    }

    @Test
    public void testNewChemModel() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemModel model = builder.newInstance(IChemModel.class);
        Assert.assertNotNull(model);
    }

    @Test
    public void testNewChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemObject model = builder.newInstance(IChemObject.class);
        Assert.assertNotNull(model);
    }

    @Test
    public void testNewChemObject_IChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemObject model = builder.newInstance(IChemObject.class, builder.newInstance(IChemObject.class));
        Assert.assertNotNull(model);
    }

    @Test
    public void testNewChemSequence() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemSequence sequence = builder.newInstance(IChemSequence.class);
        Assert.assertNotNull(sequence);
    }

    @Test
    public void testNewCrystal() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ICrystal crystal = builder.newInstance(ICrystal.class);
        Assert.assertNotNull(crystal);
    }

    @Test
    public void testNewCrystal_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ICrystal crystal = builder.newInstance(ICrystal.class, builder.newInstance(IAtomContainer.class));
        Assert.assertNotNull(crystal);
    }

    @Test
    public void testNewElectronContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElectronContainer container = builder.newInstance(IElectronContainer.class);
        Assert.assertNotNull(container);
    }

    @Test
    public void testNewElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class);
        Assert.assertNotNull(element);
    }

    @Test
    public void testNewElement_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, builder.newInstance(IElement.class));
        Assert.assertNotNull(element);
    }

    @Test
    public void testNewElement_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, "C");
        Assert.assertNotNull(element);
    }

    @Test
    public void testNewElement_String_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, "C", 13);
        Assert.assertNotNull(element);
    }

    @Test
    public void testNewIsotope_int_String_double_double() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, 6, "C", 1.0, 1.0);
        Assert.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_int_String_int_double_double() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, 6, "C", 13, 1.0, 1.0);
        Assert.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, builder.newInstance(IElement.class));
        Assert.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, "C");
        Assert.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_String_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, "C", 13);
        Assert.assertNotNull(isotope);
    }

    @Test
    public void testNewLonePair() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ILonePair lonePair = builder.newInstance(ILonePair.class);
        Assert.assertNotNull(lonePair);
    }

    @Test
    public void testNewLonePair_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ILonePair lonePair = builder.newInstance(ILonePair.class, builder.newInstance(IAtom.class));
        Assert.assertNotNull(lonePair);
    }

    @Test
    public void testNewMapping_IChemObject_IChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMapping mapping = builder.newInstance(IMapping.class, builder.newInstance(IChemObject.class),
                builder.newInstance(IChemObject.class));
        Assert.assertNotNull(mapping);
    }

    @Test
    public void testNewMonomer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMonomer monomer = builder.newInstance(IMonomer.class);
        Assert.assertNotNull(monomer);
    }

    @Test
    public void testNewPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPolymer polymer = builder.newInstance(IPolymer.class);
        Assert.assertNotNull(polymer);
    }

    @Test
    public void testNewPDBAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, builder.newInstance(IElement.class));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "O");
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String_Point3D() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "O", new Point3d(1, 2, 3));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPDBPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBPolymer polymer = builder.newInstance(IPDBPolymer.class);
        Assert.assertNotNull(polymer);
    }

    @Test
    public void testNewPDBStructure() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBStructure structure = builder.newInstance(IPDBStructure.class);
        Assert.assertNotNull(structure);
    }

    @Test
    public void testNewPDBMonomer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBMonomer monomer = builder.newInstance(IPDBMonomer.class);
        Assert.assertNotNull(monomer);
    }

    @Test
    public void testNewPseudoAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class);
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, builder.newInstance(IElement.class));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, builder.newInstance(IAtom.class));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo");
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String_Point2d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo", new Point2d(1, 2));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo", new Point3d(1, 2, 3));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "C", new Point3d(1, 2, 3));
        Assert.assertNotNull(atom);
    }

    @Test
    public void testNewReaction() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReaction reaction = builder.newInstance(IReaction.class);
        Assert.assertNotNull(reaction);
    }

    @Test
    public void testNewRing() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class);
        Assert.assertNotNull(ring);
    }

    @Test
    public void testNewRing_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, 4);
        Assert.assertNotNull(ring);
    }

    @Test
    public void testNewRing_int_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, 5, "C");
        Assert.assertNotNull(ring);
    }

    @Test
    public void testNewRing_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, builder.newInstance(IAtomContainer.class));
        Assert.assertNotNull(ring);
    }

    @Test
    public void testNewRingSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRingSet set = builder.newInstance(IRingSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void testNewAtomContainerSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void testNewMoleculeSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void testNewReactionSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReactionSet set = builder.newInstance(IReactionSet.class);
        Assert.assertNotNull(set);
    }

    @Test
    public void testNewReactionScheme() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReactionScheme scheme = builder.newInstance(IReactionScheme.class);
        Assert.assertNotNull(scheme);
    }

    @Test
    public void testNewSingleElectron() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISingleElectron electron = builder.newInstance(ISingleElectron.class);
        Assert.assertNotNull(electron);
    }

    @Test
    public void testNewSingleElectron_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISingleElectron electron = builder.newInstance(ISingleElectron.class, builder.newInstance(IAtom.class));
        Assert.assertNotNull(electron);
    }

    @Test
    public void testNewStrand() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IStrand strand = builder.newInstance(IStrand.class);
        Assert.assertNotNull(strand);
    }

    @Test
    public void testNewFragmentAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IFragmentAtom fragAtom = builder.newInstance(IFragmentAtom.class);
        Assert.assertNotNull(fragAtom);
    }

    @Test
    public void testNewMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormula mf = builder.newInstance(IMolecularFormula.class);
        Assert.assertNotNull(mf);
    }

    @Test
    public void testNewMolecularFormulaSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormulaSet mfSet = builder.newInstance(IMolecularFormulaSet.class);
        Assert.assertNotNull(mfSet);
    }

    @Test
    public void testNewMolecularFormulaSet_IMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormulaSet mfSet = builder.newInstance(IMolecularFormulaSet.class,
                builder.newInstance(IMolecularFormula.class));
        Assert.assertNotNull(mfSet);
    }

    @Test
    public void testNewAdductFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAdductFormula af = builder.newInstance(IAdductFormula.class);
        Assert.assertNotNull(af);
    }

    @Test
    public void testNewAdductFormula_IMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAdductFormula af = builder.newInstance(IAdductFormula.class, builder.newInstance(IMolecularFormula.class));
        Assert.assertNotNull(af);
    }

    @Test
    public void testNewTetrahedralChirality() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer molecule = builder.newInstance(IAtomContainer.class);
        molecule.addAtom(builder.newInstance(IAtom.class, "Cl"));
        molecule.addAtom(builder.newInstance(IAtom.class, "C"));
        molecule.addAtom(builder.newInstance(IAtom.class, "Br"));
        molecule.addAtom(builder.newInstance(IAtom.class, "I"));
        molecule.addAtom(builder.newInstance(IAtom.class, "H"));
        molecule.addBond(0, 1, Order.SINGLE);
        molecule.addBond(1, 2, Order.SINGLE);
        molecule.addBond(1, 3, Order.SINGLE);
        molecule.addBond(1, 4, Order.SINGLE);
        IAtom[] ligands = new IAtom[]{molecule.getAtom(4), molecule.getAtom(3), molecule.getAtom(2),
                molecule.getAtom(0)};
        ITetrahedralChirality chirality = builder.newInstance(ITetrahedralChirality.class, molecule.getAtom(1),
                ligands, Stereo.CLOCKWISE);
        Assert.assertNotNull(chirality);
        Assert.assertEquals(builder, chirality.getBuilder());
    }

    @Test
    public void testSugggestion() {
        IChemObjectBuilder builder = getRootObject().getBuilder();
        try {
            builder.newInstance(IAtom.class, Boolean.TRUE);
            Assert.fail("I expected an exception, because this constructor does not exist.");
        } catch (Exception exception) {
            String message = exception.getMessage();
            Assert.assertTrue("But got this message instead: " + message, message.contains("candidates are"));
        }
    }

    @Test
    public void testSubstance() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISubstance substance = builder.newInstance(ISubstance.class);
        Assert.assertNotNull(substance);
    }

}

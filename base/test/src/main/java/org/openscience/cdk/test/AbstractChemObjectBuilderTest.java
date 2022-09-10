/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.NoSuchAtomException;
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

    @Test
    public void testNewInstance_Class_arrayObject() {
        // throw random stuff; it should fail
        IChemObjectBuilder builder = rootObject.getBuilder();
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    builder.newInstance(IAtom.class, new Object[2]);
                                });
    }

    @Test
    public void testIncorrectNumberOf() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    builder.newInstance(IAtom.class, builder.newInstance(IAtomContainer.class));
                                });
    }

    @Test
    public void testNewAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class);
        Assertions.assertNotNull(atom);
        Assertions.assertNull(atom.getSymbol());
    }

    @Test
    public void testNewAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class, builder.newInstance(IElement.class, "N"));
        Assertions.assertNotNull(atom);
        Assertions.assertEquals("N", atom.getSymbol());
    }

    @Test
    public void testNewAminoAcid() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAminoAcid aa = builder.newInstance(IAminoAcid.class);
        Assertions.assertNotNull(aa);
    }

    @Test
    public void testNewAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtom atom = builder.newInstance(IAtom.class, "C");
        Assertions.assertNotNull(atom);
        Assertions.assertEquals("C", atom.getSymbol());
    }

    @Test
    public void testNewAtom_String_Point2d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Point2d coord = new Point2d(1, 2);
        IAtom atom = builder.newInstance(IAtom.class, "C", coord);
        Assertions.assertNotNull(atom);
        Assertions.assertEquals("C", atom.getSymbol());
        assertEquals(coord, atom.getPoint2d(), 0.0);
    }

    @Test
    public void testNewAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Point3d coord = new Point3d(1, 2, 3);
        IAtom atom = builder.newInstance(IAtom.class, "C", coord);
        Assertions.assertNotNull(atom);
        Assertions.assertEquals("C", atom.getSymbol());
        assertEquals(coord, atom.getPoint3d(), 0.0);
    }

    @Test
    public void testNewAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        Assertions.assertNotNull(container);
    }

    @Test
    public void testNewAtomContainer_int_int_int_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class, 1, 2, 3, 4);
        Assertions.assertNotNull(container);
    }

    @Test
    public void testNewAtomContainer_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        Assertions.assertNotNull(container);
        IAtomContainer second = builder.newInstance(IAtomContainer.class, container);
        Assertions.assertNotNull(second);
    }

    @Test
    public void testNewAtomType_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, "C");
        Assertions.assertNotNull(type);
    }

    @Test
    public void testNewAtomType_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, builder.newInstance(IElement.class, "C"));
        Assertions.assertNotNull(type);
    }

    @Test
    public void testNewAtomType_String_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomType type = builder.newInstance(IAtomType.class, "C", "C.sp2");
        Assertions.assertNotNull(type);
    }

    @Test
    public void testNewBioPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBioPolymer polymer = builder.newInstance(IBioPolymer.class);
        Assertions.assertNotNull(polymer);
    }

    @Test
    public void testNewBond() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class);
        Assertions.assertNotNull(bond);
    }

    @Test
    public void testNewBond_IAtom_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class));
        Assertions.assertNotNull(bond);
    }

    /**
     * @cdk.bug 3526870
     */
    @Test
    public void testNewBond_IAtom_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        Assertions.assertThrows(IllegalArgumentException.class,
                                () -> {
                                    builder.newInstance(IBond.class, builder.newInstance(IAtom.class), builder.newInstance(IAtomContainer.class));
                                });
    }

    @Test
    public void testNewBond_IAtom_IAtom_IBond_Order() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class), IBond.Order.SINGLE);
        Assertions.assertNotNull(bond);
    }

    @Test
    public void testNewBond_IAtom_IAtom_IBond_Order_IBond_Stereo() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class, builder.newInstance(IAtom.class),
                builder.newInstance(IAtom.class), IBond.Order.SINGLE, IBond.Stereo.E_OR_Z);
        Assertions.assertNotNull(bond);
    }

    @Test
    public void testNewBond_arrayIAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class,
                (Object[]) new IAtom[]{builder.newInstance(IAtom.class), builder.newInstance(IAtom.class)});
        Assertions.assertNotNull(bond);
    }

    @Test
    public void testNewBond_arrayIAtom_IBond_Order() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IBond bond = builder.newInstance(IBond.class,
                new IAtom[]{builder.newInstance(IAtom.class), builder.newInstance(IAtom.class)},
                IBond.Order.DOUBLE);
        Assertions.assertNotNull(bond);
    }

    @Test
    public void testNewChemFile() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemFile file = builder.newInstance(IChemFile.class);
        Assertions.assertNotNull(file);
    }

    @Test
    public void testNewChemModel() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemModel model = builder.newInstance(IChemModel.class);
        Assertions.assertNotNull(model);
    }

    @Test
    public void testNewChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemObject model = builder.newInstance(IChemObject.class);
        Assertions.assertNotNull(model);
    }

    @Test
    public void testNewChemObject_IChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemObject model = builder.newInstance(IChemObject.class, builder.newInstance(IChemObject.class));
        Assertions.assertNotNull(model);
    }

    @Test
    public void testNewChemSequence() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IChemSequence sequence = builder.newInstance(IChemSequence.class);
        Assertions.assertNotNull(sequence);
    }

    @Test
    public void testNewCrystal() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ICrystal crystal = builder.newInstance(ICrystal.class);
        Assertions.assertNotNull(crystal);
    }

    @Test
    public void testNewCrystal_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ICrystal crystal = builder.newInstance(ICrystal.class, builder.newInstance(IAtomContainer.class));
        Assertions.assertNotNull(crystal);
    }

    @Test
    public void testNewElectronContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElectronContainer container = builder.newInstance(IElectronContainer.class);
        Assertions.assertNotNull(container);
    }

    @Test
    public void testNewElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class);
        Assertions.assertNotNull(element);
    }

    @Test
    public void testNewElement_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, builder.newInstance(IElement.class));
        Assertions.assertNotNull(element);
    }

    @Test
    public void testNewElement_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, "C");
        Assertions.assertNotNull(element);
    }

    @Test
    public void testNewElement_String_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IElement element = builder.newInstance(IElement.class, "C", 13);
        Assertions.assertNotNull(element);
    }

    @Test
    public void testNewIsotope_int_String_double_double() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, 6, "C", 1.0, 1.0);
        Assertions.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_int_String_int_double_double() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, 6, "C", 13, 1.0, 1.0);
        Assertions.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, builder.newInstance(IElement.class));
        Assertions.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, "C");
        Assertions.assertNotNull(isotope);
    }

    @Test
    public void testNewIsotope_String_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IIsotope isotope = builder.newInstance(IIsotope.class, "C", 13);
        Assertions.assertNotNull(isotope);
    }

    @Test
    public void testNewLonePair() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ILonePair lonePair = builder.newInstance(ILonePair.class);
        Assertions.assertNotNull(lonePair);
    }

    @Test
    public void testNewLonePair_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ILonePair lonePair = builder.newInstance(ILonePair.class, builder.newInstance(IAtom.class));
        Assertions.assertNotNull(lonePair);
    }

    @Test
    public void testNewMapping_IChemObject_IChemObject() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMapping mapping = builder.newInstance(IMapping.class, builder.newInstance(IChemObject.class),
                builder.newInstance(IChemObject.class));
        Assertions.assertNotNull(mapping);
    }

    @Test
    public void testNewMonomer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMonomer monomer = builder.newInstance(IMonomer.class);
        Assertions.assertNotNull(monomer);
    }

    @Test
    public void testNewPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPolymer polymer = builder.newInstance(IPolymer.class);
        Assertions.assertNotNull(polymer);
    }

    @Test
    public void testNewPDBAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, builder.newInstance(IElement.class));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "O");
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String_Point3D() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "O", new Point3d(1, 2, 3));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPDBPolymer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBPolymer polymer = builder.newInstance(IPDBPolymer.class);
        Assertions.assertNotNull(polymer);
    }

    @Test
    public void testNewPDBStructure() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBStructure structure = builder.newInstance(IPDBStructure.class);
        Assertions.assertNotNull(structure);
    }

    @Test
    public void testNewPDBMonomer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBMonomer monomer = builder.newInstance(IPDBMonomer.class);
        Assertions.assertNotNull(monomer);
    }

    @Test
    public void testNewPseudoAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class);
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_IElement() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, builder.newInstance(IElement.class));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, builder.newInstance(IAtom.class));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo");
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String_Point2d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo", new Point2d(1, 2));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPseudoAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPseudoAtom atom = builder.newInstance(IPseudoAtom.class, "Foo", new Point3d(1, 2, 3));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewPDBAtom_String_Point3d() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IPDBAtom atom = builder.newInstance(IPDBAtom.class, "C", new Point3d(1, 2, 3));
        Assertions.assertNotNull(atom);
    }

    @Test
    public void testNewReaction() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReaction reaction = builder.newInstance(IReaction.class);
        Assertions.assertNotNull(reaction);
    }

    @Test
    public void testNewRing() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class);
        Assertions.assertNotNull(ring);
    }

    @Test
    public void testNewRing_int() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, 4);
        Assertions.assertNotNull(ring);
    }

    @Test
    public void testNewRing_int_String() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, 5, "C");
        Assertions.assertNotNull(ring);
    }

    @Test
    public void testNewRing_IAtomContainer() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRing ring = builder.newInstance(IRing.class, builder.newInstance(IAtomContainer.class));
        Assertions.assertNotNull(ring);
    }

    @Test
    public void testNewRingSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IRingSet set = builder.newInstance(IRingSet.class);
        Assertions.assertNotNull(set);
    }

    @Test
    public void testNewAtomContainerSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);
        Assertions.assertNotNull(set);
    }

    @Test
    public void testNewMoleculeSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAtomContainerSet set = builder.newInstance(IAtomContainerSet.class);
        Assertions.assertNotNull(set);
    }

    @Test
    public void testNewReactionSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReactionSet set = builder.newInstance(IReactionSet.class);
        Assertions.assertNotNull(set);
    }

    @Test
    public void testNewReactionScheme() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReactionScheme scheme = builder.newInstance(IReactionScheme.class);
        Assertions.assertNotNull(scheme);
    }

    @Test
    public void testNewSingleElectron() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISingleElectron electron = builder.newInstance(ISingleElectron.class);
        Assertions.assertNotNull(electron);
    }

    @Test
    public void testNewSingleElectron_IAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISingleElectron electron = builder.newInstance(ISingleElectron.class, builder.newInstance(IAtom.class));
        Assertions.assertNotNull(electron);
    }

    @Test
    public void testNewStrand() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IStrand strand = builder.newInstance(IStrand.class);
        Assertions.assertNotNull(strand);
    }

    @Test
    public void testNewFragmentAtom() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IFragmentAtom fragAtom = builder.newInstance(IFragmentAtom.class);
        Assertions.assertNotNull(fragAtom);
    }

    @Test
    public void testNewMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormula mf = builder.newInstance(IMolecularFormula.class);
        Assertions.assertNotNull(mf);
    }

    @Test
    public void testNewMolecularFormulaSet() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormulaSet mfSet = builder.newInstance(IMolecularFormulaSet.class);
        Assertions.assertNotNull(mfSet);
    }

    @Test
    public void testNewMolecularFormulaSet_IMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IMolecularFormulaSet mfSet = builder.newInstance(IMolecularFormulaSet.class,
                builder.newInstance(IMolecularFormula.class));
        Assertions.assertNotNull(mfSet);
    }

    @Test
    public void testNewAdductFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAdductFormula af = builder.newInstance(IAdductFormula.class);
        Assertions.assertNotNull(af);
    }

    @Test
    public void testNewAdductFormula_IMolecularFormula() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IAdductFormula af = builder.newInstance(IAdductFormula.class, builder.newInstance(IMolecularFormula.class));
        Assertions.assertNotNull(af);
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
        Assertions.assertNotNull(chirality);
        Assertions.assertEquals(builder, chirality.getBuilder());
    }

    @Test
    public void testSugggestion() {
        IChemObjectBuilder builder = getRootObject().getBuilder();
        try {
            builder.newInstance(IAtom.class, Boolean.TRUE);
            Assertions.fail("I expected an exception, because this constructor does not exist.");
        } catch (Exception exception) {
            String message = exception.getMessage();
            Assertions.assertTrue(message.contains("candidates are"), "But got this message instead: " + message);
        }
    }

    @Test
    public void testSubstance() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        ISubstance substance = builder.newInstance(ISubstance.class);
        Assertions.assertNotNull(substance);
    }

    @Test
    public void testNewReaction_dedicatedMethod() {
        IChemObjectBuilder builder = rootObject.getBuilder();
        IReaction reaction = builder.newReaction();
        Assertions.assertNotNull(reaction);
    }

}

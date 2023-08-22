/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;

import java.util.Iterator;

/**
 * TestCase for {@link org.openscience.cdk.interfaces.IReaction} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractReactionTest extends AbstractChemObjectTest {

    @Test
    public void testGetReactantCount() {
        IReaction reaction = (IReaction) newChemObject();
        Assertions.assertEquals(0, reaction.getReactantCount());
        reaction.addReactant(reaction.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertEquals(1, reaction.getReactantCount());
    }

    @Test
    public void testGetProductCount() {
        IReaction reaction = (IReaction) newChemObject();
        Assertions.assertEquals(0, reaction.getProductCount());
        reaction.addProduct(reaction.getBuilder().newInstance(IAtomContainer.class));
        Assertions.assertEquals(1, reaction.getProductCount());
    }

    @Test
    public void testAddReactant_IAtomContainer() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer acetate = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(sodiumhydroxide);
        reaction.addReactant(aceticAcid);
        reaction.addReactant(water);
        Assertions.assertEquals(3, reaction.getReactantCount());
        // next one should trigger a growArray, if the grow
        // size is still 3.
        reaction.addReactant(acetate);
        Assertions.assertEquals(4, reaction.getReactantCount());

        Assertions.assertEquals(1.0, reaction.getReactantCoefficient(aceticAcid), 0.00001);
    }

    @Test
    public void testSetReactants_IAtomContainerSet() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainerSet reactants = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        reactants.addAtomContainer(sodiumhydroxide);
        reactants.addAtomContainer(aceticAcid);
        reactants.addAtomContainer(water);
        reaction.setReactants(reactants);
        Assertions.assertEquals(3, reaction.getReactantCount());

        Assertions.assertEquals(1.0, reaction.getReactantCoefficient(aceticAcid), 0.00001);
    }

    @Test
    public void testAddReactant_IAtomContainer_Double() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer sulfate = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(proton, 2d);
        reaction.addReactant(sulfate, 1d);
        Assertions.assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);
        Assertions.assertEquals(1.0, reaction.getReactantCoefficient(sulfate), 0.00001);
    }

    @Test
    public void testAddProduct_IAtomContainer() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer acetate = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(sodiumhydroxide);
        reaction.addProduct(aceticAcid);
        reaction.addProduct(water);
        Assertions.assertEquals(3, reaction.getProductCount());
        // next one should trigger a growArray, if the grow
        // size is still 3.
        reaction.addProduct(acetate);
        Assertions.assertEquals(4, reaction.getProductCount());

        Assertions.assertEquals(1.0, reaction.getProductCoefficient(aceticAcid), 0.00001);
    }

    @Test
    public void testSetProducts_IAtomContainerSet() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainerSet products = reaction.getBuilder().newInstance(IAtomContainerSet.class);
        products.addAtomContainer(sodiumhydroxide);
        products.addAtomContainer(aceticAcid);
        products.addAtomContainer(water);
        reaction.setProducts(products);
        Assertions.assertEquals(3, reaction.getProductCount());

        Assertions.assertEquals(1.0, reaction.getProductCoefficient(aceticAcid), 0.00001);
    }

    @Test
    public void testAddProduct_IAtomContainer_Double() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer sulfate = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(proton, 2.0);
        reaction.addProduct(sulfate, 1.0);
        Assertions.assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);
        Assertions.assertEquals(1.0, reaction.getProductCoefficient(sulfate), 0.00001);
    }

    @Test
    public void testAddAgent_IAtomContainer() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addAgent(proton);
        Assertions.assertEquals(1, reaction.getAgents().getAtomContainerCount());
    }

    @Test
    public void testGetReactantCoefficient_IAtomContainer() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(proton, 2.0);
        Assertions.assertEquals(2.0, reaction.getReactantCoefficient(proton), 0.00001);

        Assertions.assertEquals(-1.0, reaction.getReactantCoefficient(reaction.getBuilder().newInstance(IAtomContainer.class)), 0.00001);
    }

    @Test
    public void testGetProductCoefficient_IAtomContainer() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(proton, 2.0);
        Assertions.assertEquals(2.0, reaction.getProductCoefficient(proton), 0.00001);

        Assertions.assertEquals(-1.0, reaction.getProductCoefficient(reaction.getBuilder().newInstance(IAtomContainer.class)), 0.00001);
    }

    @Test
    public void testSetReactantCoefficient_IAtomContainer_Double() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(proton, 2.0);
        reaction.setReactantCoefficient(proton, 3.0);
        Assertions.assertEquals(3.0, reaction.getReactantCoefficient(proton), 0.00001);
    }

    @Test
    public void testSetProductCoefficient_IAtomContainer_Double() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer proton = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(proton, 2.0);
        reaction.setProductCoefficient(proton, 1.0);
        Assertions.assertEquals(1.0, reaction.getProductCoefficient(proton), 0.00001);
    }

    @Test
    public void testGetReactantCoefficients() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer ed1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ed2 = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(ed1, 2d);
        reaction.addReactant(ed2, 3d);
        Double[] ec = reaction.getReactantCoefficients();
        Assertions.assertEquals(2.0, ec.length, 0.00001);
        Assertions.assertEquals(reaction.getReactantCoefficient(ed1), ec[0], 0.00001);
        Assertions.assertEquals(3.0, ec[1], 0.00001);
    }

    @Test
    public void testGetProductCoefficients() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer pr1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer pr2 = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(pr1, 1d);
        reaction.addProduct(pr2, 2d);
        Double[] pc = reaction.getProductCoefficients();
        Assertions.assertEquals(2.0, pc.length, 0.00001);
        Assertions.assertEquals(reaction.getProductCoefficient(pr1), pc[0], 0.00001);
        Assertions.assertEquals(2.0, pc[1], 0.00001);
    }

    @Test
    public void testSetReactantCoefficients_arrayDouble() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer ed1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer ed2 = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(ed1, 2d);
        reaction.addReactant(ed2, 3d);
        Double[] ec = {1.0, 2.0};
        boolean coeffSet = reaction.setReactantCoefficients(ec);
        Assertions.assertTrue(coeffSet);
        Assertions.assertEquals(1.0, reaction.getReactantCoefficient(ed1), 0.00001);
        Assertions.assertEquals(2.0, reaction.getReactantCoefficient(ed2), 0.00001);
        Double[] ecFalse = {1.0};
        Assertions.assertFalse(reaction.setReactantCoefficients(ecFalse));
    }

    @Test
    public void testSetProductCoefficients_arrayDouble() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer pr1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(pr1, 1d);
        Double[] pc = {2.0};
        boolean coeffSet = reaction.setProductCoefficients(pc);
        Assertions.assertTrue(coeffSet);
        Assertions.assertEquals(2.0, reaction.getProductCoefficient(pr1), 0.00001);
        Double[] pcFalse = {1.0, 2.0};
        Assertions.assertFalse(reaction.setProductCoefficients(pcFalse));
    }

    @Test
    public void testGetReactants() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(sodiumhydroxide);
        reaction.addReactant(aceticAcid);
        reaction.addReactant(water);
        Assertions.assertEquals(3, reaction.getReactants().getAtomContainerCount());
    }

    @Test
    public void testGetProducts() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer sodiumhydroxide = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer aceticAcid = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addProduct(sodiumhydroxide);
        reaction.addProduct(aceticAcid);
        reaction.addProduct(water);
        Assertions.assertEquals(3, reaction.getProducts().getAtomContainerCount());
    }

    @Test
    public void testGetAgents() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer water = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addAgent(water);
        Assertions.assertEquals(1, reaction.getAgents().getAtomContainerCount());
    }

    @Test
    public void testSetDirection_IReaction_Direction() {
        IReaction reaction = (IReaction) newChemObject();
        IReaction.Direction direction = IReaction.Direction.BIDIRECTIONAL;
        reaction.setDirection(direction);
        Assertions.assertEquals(direction, reaction.getDirection());
    }

    @Test
    public void testGetDirection() {
        IReaction reaction = (IReaction) newChemObject();
        Assertions.assertEquals(IReaction.Direction.FORWARD, reaction.getDirection());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    public void testToString() {
        IReaction reaction = (IReaction) newChemObject();
        String description = reaction.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IReaction reaction = (IReaction) newChemObject();
        Object clone = reaction.clone();
        Assertions.assertNotNull(clone);
        Assertions.assertTrue(clone instanceof IReaction);
    }

    @Test
    public void testClone_Mapping() throws Exception {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        IReaction clonedReaction = (IReaction) reaction.clone();
        Iterator<IMapping> mappings = reaction.mappings().iterator();
        Iterator<IMapping> clonedMappings = clonedReaction.mappings().iterator();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(mappings.hasNext());
        Assertions.assertNotNull(clonedMappings);
        Assertions.assertTrue(clonedMappings.hasNext());
    }

    @Test
    public void testAddMapping_IMapping() {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        Iterator<IMapping> mappings = reaction.mappings().iterator();
        Assertions.assertNotNull(mappings);
        Assertions.assertTrue(mappings.hasNext());
        Assertions.assertEquals(mapping, mappings.next());
    }

    @Test
    public void testRemoveMapping_int() {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        Assertions.assertEquals(1, reaction.getMappingCount());
        reaction.removeMapping(0);
        Assertions.assertEquals(0, reaction.getMappingCount());
    }

    @Test
    public void testGetMapping_int() {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        IMapping gotIt = reaction.getMapping(0);
        Assertions.assertEquals(mapping, gotIt);
    }

    @Test
    public void testGetMappingCount() {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        Assertions.assertEquals(1, reaction.getMappingCount());
    }

    @Test
    public void testMappings() {
        IReaction reaction = (IReaction) newChemObject();
        IMapping mapping = reaction.getBuilder().newInstance(IMapping.class,
                reaction.getBuilder().newInstance(IAtom.class, "C"),
                reaction.getBuilder().newInstance(IAtom.class, "C"));
        reaction.addMapping(mapping);
        Assertions.assertEquals(1, reaction.getMappingCount());
    }

    @Test
    public void testIterator() {
        IReaction reaction = (IReaction) newChemObject();
        IAtomContainer r1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer r2 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer a1 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer a2 = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer p  = reaction.getBuilder().newInstance(IAtomContainer.class);
        reaction.addReactant(r1);
        reaction.addReactant(r2);
        reaction.addAgent(a1);
        reaction.addAgent(a2);
        reaction.addProduct(p);
        Iterator<IAtomContainer> iterator = reaction.iterator();
        Assertions.assertEquals(r1, iterator.next());
        Assertions.assertEquals(r2, iterator.next());
        Assertions.assertEquals(a1, iterator.next());
        Assertions.assertEquals(a2, iterator.next());
        Assertions.assertEquals(p, iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }
}

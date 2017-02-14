package org.openscience.cdk.smsd.labelling;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * @cdk.module smsd
 * @cdk.githash
 * @deprecated This class is part of SMSD and either duplicates functionality elsewhere in the CDK or provides public
 *             access to internal implementation details. SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class AbstractReactionLabeller {

    /**
     * A nasty hack necessary to get around a bug in the CDK
     */
    private boolean fixAtomMappingCastType = false;

    private void fixAtomMapping(IAtomContainer canonicalForm) {
        for (IAtom a : canonicalForm.atoms()) {
            String v = (String) a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
            if (v != null) {
                a.setProperty(CDKConstants.ATOM_ATOM_MAPPING, Integer.valueOf(v));
            }
        }
    }

    private Map<IAtom, IAtom> atomAtomMap(IReaction reaction, IReaction clone, Map<IAtomContainer, int[]> permutationMap) {
        // create a Map of corresponding atoms for molecules
        // (key: original Atom, value: clone Atom)
        Map<IAtom, IAtom> atomAtom = new Hashtable<IAtom, IAtom>();
        IAtomContainerSet reactants = reaction.getReactants();
        IAtomContainerSet clonedReactants = clone.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); ++i) {
            IAtomContainer mol = reactants.getAtomContainer(i);
            IAtomContainer mol2 = clonedReactants.getAtomContainer(i);
            int[] permutation = permutationMap.get(mol2);
            for (int j = 0; j < mol.getAtomCount(); ++j) {
                atomAtom.put(mol.getAtom(j), mol2.getAtom(permutation[j]));
            }
        }
        IAtomContainerSet products = reaction.getProducts();
        IAtomContainerSet clonedProducts = clone.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); ++i) {
            IAtomContainer mol = products.getAtomContainer(i);
            IAtomContainer mol2 = clonedProducts.getAtomContainer(i);
            int[] permutation = permutationMap.get(mol2);
            for (int j = 0; j < mol.getAtomCount(); ++j) {
                atomAtom.put(mol.getAtom(j), mol2.getAtom(permutation[j]));
            }
        }

        for (IAtom key : atomAtom.keySet()) {
            IAtomContainer keyAC = ReactionManipulator.getRelevantAtomContainer(reaction, key);
            int keyIndex = keyAC.getAtomNumber(key);
            IAtom value = atomAtom.get(key);
            IAtomContainer valueAC = ReactionManipulator.getRelevantAtomContainer(clone, value);
            int valueIndex = valueAC.getAtomNumber(value);
            System.out.println("key " + keyIndex + key.getSymbol() + " mapped to " + valueIndex + value.getSymbol());
        }

        return atomAtom;
    }

    private List<IMapping> cloneMappings(IReaction reaction, Map<IAtom, IAtom> atomAtomMap) {
        // clone the mappings
        int numberOfMappings = reaction.getMappingCount();
        List<IMapping> map = new ArrayList<IMapping>();
        for (int mappingIndex = 0; mappingIndex < numberOfMappings; mappingIndex++) {
            IMapping mapping = reaction.getMapping(mappingIndex);
            IChemObject keyChemObj0 = mapping.getChemObject(0);
            IChemObject keyChemObj1 = mapping.getChemObject(1);
            IChemObject co0 = (IChemObject) atomAtomMap.get(keyChemObj0);
            IChemObject co1 = (IChemObject) atomAtomMap.get(keyChemObj1);
            map.add(new Mapping(co0, co1));
        }
        return map;
    }

    /**
     * Clone and Sort the mappings based on the order of the first object
     * in the mapping (which is assumed to be the reactant).
     *
     * @param reaction
     */
    private void cloneAndSortMappings(IReaction reaction, IReaction copyOfReaction,
            Map<IAtomContainer, int[]> permutationMap) {

        // make a lookup for the indices of the atoms in the copy
        final Map<IChemObject, Integer> indexMap = new HashMap<IChemObject, Integer>();
        List<IAtomContainer> all = ReactionManipulator.getAllAtomContainers(copyOfReaction);
        int globalIndex = 0;
        for (IAtomContainer ac : all) {
            for (IAtom atom : ac.atoms()) {
                indexMap.put(atom, globalIndex);
                globalIndex++;
            }
        }

        Map<IAtom, IAtom> atomAtomMap = atomAtomMap(reaction, copyOfReaction, permutationMap);
        List<IMapping> map = cloneMappings(reaction, atomAtomMap);

        Comparator<IMapping> mappingSorter = new Comparator<IMapping>() {

            /**
             * {@inheritDoc}
             */
            @Override
            public int compare(IMapping o1, IMapping o2) {
                IChemObject o10 = o1.getChemObject(0);
                IChemObject o20 = o2.getChemObject(0);
                return indexMap.get(o10).compareTo(indexMap.get(o20));
            }

        };
        Collections.sort(map, mappingSorter);
        int mappingIndex = 0;
        for (IMapping mapping : map) {
            mapping.getChemObject(0).setProperty(CDKConstants.ATOM_ATOM_MAPPING, mappingIndex);
            mapping.getChemObject(1).setProperty(CDKConstants.ATOM_ATOM_MAPPING, mappingIndex);
            copyOfReaction.addMapping(mapping);
            mappingIndex++;
        }

    }

    public IReaction labelReaction(IReaction reaction, ICanonicalMoleculeLabeller labeller) {
        System.out.println("labelling");
        IReaction canonReaction = new Reaction();

        Map<IAtomContainer, int[]> permutationMap = new HashMap<IAtomContainer, int[]>();

        IAtomContainerSet canonicalProducts = DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainerSet.class);
        for (IAtomContainer product : reaction.getProducts().atomContainers()) {
            IAtomContainer canonicalForm = labeller.getCanonicalMolecule(product);
            if (fixAtomMappingCastType) {
                fixAtomMapping(canonicalForm);
            }
            IAtomContainer canonicalMolecule = canonicalForm.getBuilder().newInstance(IAtomContainer.class,
                    canonicalForm);
            permutationMap.put(canonicalMolecule, labeller.getCanonicalPermutation(product));
            canonicalProducts.addAtomContainer(canonicalMolecule);
        }
        IAtomContainerSet canonicalReactants = DefaultChemObjectBuilder.getInstance().newInstance(
                IAtomContainerSet.class);
        for (IAtomContainer reactant : reaction.getReactants().atomContainers()) {
            IAtomContainer canonicalForm = labeller.getCanonicalMolecule(reactant);
            if (fixAtomMappingCastType) {
                fixAtomMapping(canonicalForm);
            }
            IAtomContainer canonicalMolecule = canonicalForm.getBuilder().newInstance(IAtomContainer.class,
                    canonicalForm);
            permutationMap.put(canonicalMolecule, labeller.getCanonicalPermutation(reactant));
            canonicalReactants.addAtomContainer(canonicalMolecule);
        }
        canonReaction.setProducts(canonicalProducts);
        canonReaction.setReactants(canonicalReactants);
        cloneAndSortMappings(reaction, canonReaction, permutationMap);
        return canonReaction;
    }

}
